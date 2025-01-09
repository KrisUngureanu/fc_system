package com.cifs.or2.server.replicator;

import static kz.tamur.or3ee.common.SessionIds.*;
import static com.cifs.or2.kernel.ModelChange.*;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.sgds.HexStringOutputStream;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

import kz.tamur.DriverException;
import kz.tamur.comps.Constants;
import kz.tamur.ods.*;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.LongHolder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Calendar;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: 30.03.2006
 * Time: 18:53:04
 * To change this template use File | Settings | File Templates.
 */
public class ReplExport {

    /* et - Export Type */
    public static final int et_NONE = -1;
    public static final int et_SECOND_EXPORT = 0;
    public static final int et_NEXT_EXPORT = 1;

    /*  re - Replication Entity */
    public static final int RE_CLASS = 0;
    public static final int RE_ATTRIBUTE = 1;
    public static final int RE_CONSTRAINT = 2;

    /*  ra - Replication Action */
    public static final int RA_CREATE = 0;
    public static final int RA_MODIFY = 1;
    public static final int RA_DROP = 2;
    public static final int RE_REV_ATTRIBUTE = 3;

    private Session ses;
    private Log log;

    public ReplExport(Session session) {
        ses = session;
        this.log = LogFactory.getLog(ses.getDsName() + "." + ses.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
    }
    
    // 0 - успешный экспорт,  1 - файл экспорта не создан, 2 - нет предыдущего экспорта, 3 - нет изменений
    public int run(int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws KrnException {
        File file = null;
        try {
            file = createFile();
        } catch (Exception e) {
            log.error(e, e);
        }
        if (file == null) {
            return 1;
        }
        KrnObject[] objs = ses.getClassObjects(ses.getClassByName("Export"),new long[0], 0);
        KrnObject last = null;
        KrnObject priorExp = null;
        // Ищем экспорт с максимальным номером
        if (objs.length > 0) {
            last = objs[0];
            for (int i = 0; i < objs.length; i++) {
                if (objs[i].id > last.id) {
                    last = objs[i];
                }
            }
            if(objs.length > 1)
                priorExp = objs[objs.length - 2];
        }
        // Проверка на предыдущий экспорт
        if (last == null && action == et_SECOND_EXPORT) {
            traceImportant("no prior export.");
            return 2;
        }
        long modelChId = -1; long pModelChId = -1;
        long dataChId = -1; long pDataChId = -1;
        if (last != null) {
            KrnClass cls = ses.getClassByName("Export");
            modelChId = ses.getLongsSingular(last, ses.getAttributeByName(cls, "clschange_id"), true);
            pModelChId = ses.getLongsSingular(last, ses.getAttributeByName(cls, "prior_clschange_id"), true);
            dataChId = ses.getLongsSingular(last, ses.getAttributeByName(cls, "change_id"), true);
            pDataChId = ses.getLongsSingular(last, ses.getAttributeByName(cls, "prior_change_id"), true);
        }
        //Подтверждаем изменения в модели данных если есть неподтвержденные
        try {
        	ses.commitVcsModelClassAttr("Подтверждение незакомиченных изменений в модели данных перед репликацией");
            ses.commitTransaction();
        } catch (Throwable e) {
        	ses.rollbackTransaction();
        	throw new KrnException(0, e.getMessage());
        }
        //
        try {
            long newModelChId = getModelChanges(file, action == et_NEXT_EXPORT ? modelChId : pModelChId, action == et_NEXT_EXPORT ? -1 : modelChId);
            long newDataChId = getDataChanges(file, action == et_NEXT_EXPORT ? dataChId : pDataChId, action == et_NEXT_EXPORT ? -1 : dataChId);
            // Проверка - есть ли изменения в модели или по данным
            if ((modelChId < newModelChId) || (dataChId < newDataChId)) {
                KrnObject newExp = null;
                KrnObject oldExp = null;
                try {
                    if (action == et_NEXT_EXPORT) {
                        newExp = registerExport(newModelChId, modelChId, newDataChId, dataChId, info, scriptOnBeforeAction, scriptOnAfterAction);
                        oldExp = last;
                    } else if(action == et_SECOND_EXPORT) {
                        newExp = last;
                        oldExp = priorExp;
                    }
                    addInfo(file, newExp, oldExp, oldExp == null);
                    renameFile(file, newExp.id);
                    ses.commitTransaction();
                } catch (Throwable e) {
                    ses.rollbackTransaction();
                    throw new KrnException(0, e.getMessage());
                }
                if (newExp != null) {
                	// Записываем информацию о репликационном файле 
	                try {
	                	ses.setVcsExport(newExp.id);
	                    ses.commitTransaction();
	                } catch (Throwable e) {
	                    ses.rollbackTransaction();
	                }
                }
                return 0;
            } else {
                traceImportant("no change.");
                if (file.exists()) {
                    file.delete();
                }
                return 3;
            }
        } catch (Throwable e) {
            log.error(e, e);
            if (file.exists()) {
                file.delete();
            }
            trace("ERROR: " + e.getMessage());
            throw new KrnException(0, e.getMessage());
        }
    }
    
    private long getModelChanges(
            File file,
            long fromId,
            long toId
            ) throws KrnException {
        try {
            class MCP implements ModelChangeProcessor {
                PrintWriter pw;
                public MCP(PrintWriter pw) {
                    this.pw = pw;
                }
                public boolean process(ModelChange ch) {
                    writeModelChanges(ch, pw);
                    return false;
                }
            }
            PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file.getPath(), true), "UTF-8"));
            try {
                pw.println("<ModelChanges>");
                MCP mcp = new MCP(pw);
                toId = ses.getDriver().getModelChanges(fromId, toId, mcp);
                pw.println("</ModelChanges>");
            } finally {
                pw.close();
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
        return toId;
    }
    
    private void writeModelChanges(ModelChange ch, PrintWriter pw)  {
        if (ch instanceof ClassChange) {
            ClassChange c = (ClassChange) ch;
            String parentUid = (c.parentCls != null) ? c.parentCls.uid : "";
            pw.print(
                "<Class id=\"" + c.id +
                "\" action=\"" + c.action +
                "\" mod=\"" + c.mod +
                "\" entityId=\"" + Funcs.sanitizeHtml(c.entityId) +
                "\" parent=\"" + Funcs.sanitizeHtml(parentUid) +
                "\" name=\"" + Funcs.xmlQuote(c.action == ACTION_DELETE ? getEntityNameFromVCS(ch) : c.name) +
                "\" tname=\"" + Funcs.sanitizeHtml(c.tname != null ? c.tname : "") +
                "\" isRepl=\"" + (c.isRepl ? 1 : 0)  + "\">" +
                "<Comment>" + Funcs.sanitizeHtml(c.comment) + "</Comment></Class>\r\n"
            );
        } else if (ch instanceof TriggerChange) {
        	TriggerChange c = (TriggerChange) ch;
        	pw.print(
                "<Trigger id=\"" + c.id +
                "\" action=\"" + c.action +
                "\" entityId=\"" + Funcs.sanitizeHtml(c.entityId) +
                "\" name=\"" + Funcs.xmlQuote(c.action == ACTION_DELETE ? getEntityNameFromVCS(ch) : c.name) +
                "\" type=\"" + c.type  +
                 "\" tr=\"" + c.tr + "\">" +
                "<Expr>" + com.cifs.or2.util.Funcs.xmlQuote(c.expr) + "</Expr>" +
                "</Trigger>\r\n"
            );
        } else if (ch instanceof AttributeChange) {
            AttributeChange c = (AttributeChange) ch;
            pw.print(
                "<Attribute id=\"" + c.id +
                "\" action=\"" + c.action +
                "\" entityId=\"" + Funcs.sanitizeHtml(c.entityId) +
                "\" class=\"" + Funcs.sanitizeHtml(c.cls.uid) +
                "\" type=\"" + Funcs.sanitizeHtml(c.type.uid) +
                "\" name=\"" + Funcs.xmlQuote(c.action == ACTION_DELETE ? getEntityNameFromVCS(ch) : c.name) +
                "\" colType=\"" + c.collectionType +
                "\" isUnique=\"" + (c.isUnique ? 1 : 0) +
                "\" isIndexed=\"" + (c.isIndexed ? 1 : 0)  +
                "\" isMultilingual=\"" + (c.isMultilingual ? 1 : 0)  +
                "\" isRepl=\"" + (c.isRepl ? 1 : 0)  +
                "\" size=\"" + c.size +
                "\" flags=\"" + c.flags +
                "\" rAttrId=\"" + (c.rAttr != null ? Funcs.sanitizeHtml(c.rAttr.uid) : 0) +
                "\" sAttrId=\"" + (c.sAttr != null ? Funcs.sanitizeHtml(c.sAttr.uid) : 0) +
                "\" sDesc=\"" + (c.sDesc ? 1 : 0) +
                "\" tname=\"" + Funcs.sanitizeHtml(c.tname != null ? c.tname : "") +
                "\" revIds=\"" + Funcs.sanitizeHtml(c.revIds) +
                "\" accessModifier=\"" + c.accessModifier + "\">" +
                "<Comment>" + Funcs.sanitizeHtml(c.comment) + "</Comment></Attribute>\r\n"
            );
        } else if (ch instanceof MethodChange) {
            MethodChange c = (MethodChange) ch;
            String clsUid = (c.cls != null) ? c.cls.uid : "";
            pw.print(
                "<Method id=\"" + c.id +
                "\" action=\"" + c.action +
                "\" entityId=\"" + Funcs.sanitizeHtml(c.entityId) +
                "\" name=\"" + Funcs.xmlQuote(c.action == ACTION_DELETE ? getEntityNameFromVCS(ch) : c.name) +
                "\" class=\"" + clsUid +
                "\" isCMethod=\"" + (c.isCMethod ? 1 : 0) + "\">" +
                "<Expr>" + com.cifs.or2.util.Funcs.xmlQuote(c.expr) + "</Expr>" +
                "<Comment>" + Funcs.sanitizeHtml(c.comment) + "</Comment></Method>\r\n"
            );
        } else if (ch instanceof IndexChange) {
        	IndexChange c = (IndexChange) ch;
        	String clsUid = (c.getKrnClass() != null) ? c.getKrnClass().uid : "";
        	pw.print(
        		"<Index id=\"" + c.id + "\"" +
        		" action=\"" + c.action + "\"" +
        		" entityId=\"" + Funcs.sanitizeHtml(c.entityId) + "\"" +
        		" class=\"" + Funcs.sanitizeHtml(clsUid) + "\"" +        		        		
        		">" + c.getContentXML() +
        		"</Index>\r\n"
        	);
        }
    }
    
    private String getEntityNameFromVCS(ModelChange ch) {
    	try {
    		String name = ses.getEntityNameFromVCS(ch);
        	return name == null ? "" : name;
        } catch (Exception e) {
        	log.error(e, e);
        }
    	return "";
    }
    
    private long getDataChanges(
            File file,
            long fromId,
            long toId
            ) throws KrnException {
        try {
            PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(file.getPath(), true), "UTF-8"));
            pw.println("<DataChanges>");

            LongHolder lh = new LongHolder();
            Iterator it = null;
            try {
                it = ses.getDriver().getDataChanges(fromId, toId, lh);
            } catch (DriverException e) {
                log.error(e, e);
            }
            while (it.hasNext()) {
                DataChange c = (DataChange) it.next();
                KrnAttribute a = ses.getAttributeByUid(c.attrUid);
                // Делаем проверку, так как атрибут может быть удаленным
                if (a != null) {
	                long type = a.typeClassId;
	                if (type == CID_BLOB) {
	                    pw.close();
	                    writeBlobChanges(file, c);
	                    pw = new PrintWriter(
	                            new OutputStreamWriter(
	                                new FileOutputStream(file.getPath(), true), "UTF-8"));
	                }
	                else {
	                    writeDataChanges(c, pw);
	                }
                }
            }
            pw.println("</DataChanges>");
            pw.close();

            toId = lh.value();
        } catch (Exception e) {
            log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
        return toId;
    }
    private void writeDataChanges(DataChange ch, PrintWriter pw)
            throws KrnException
    {
        String v = "";
        if (ch.value != null) {
            long type = ses.getAttributeByUid(ch.attrUid).typeClassId;
            if (type > 10)
                v = ses.getDirtyObjectById(((KrnObject) ch.value).id).uid;
            else if (type == CID_DATE) {
                Calendar c = Calendar.getInstance();
                c.setTime((java.util.Date)ch.value);
                v = "" + c.getTimeInMillis();
            }
            else if (type == CID_TIME) {
                Calendar c = Calendar.getInstance();
                c.setTime((Timestamp)ch.value);
                v = "" + c.getTimeInMillis();
            }
            else if (type == CID_BOOL) {
                v = ((Boolean) ch.value).booleanValue() ? "1" : "0";
            } else
                v = ("" + ch.value).trim();
        }
        pw.print(
                "<Row obj=\"" + ch.uid +
                "\" attr=\"" + ch.attrUid +
                "\" i=\"" + ch.index +
                "\" lang=\"" + ch.langUid +
                "\">" + Funcs.xmlQuote(v) +
                "</Row>\r\n"
        );
    }
    private void writeBlobChanges(File file, DataChange ch)
            throws Exception {
        log.info(ch.uid+":"+ch.attrUid);
        PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(file.getPath(), true), "UTF-8"));
        pw.print(
                "<Row obj=\"" + ch.uid +
                "\" attr=\"" + ch.attrUid +
                "\" i=\"" + ch.index +
                "\" lang=\"" + ch.langUid +
                "\">"
        );
        pw.close();
        if (ch.value != null) {
            InputStream is = new ByteArrayInputStream((byte[]) ch.value);
            HexStringOutputStream hsos = new HexStringOutputStream(
                   new FileOutputStream(file.getPath(), true));
            try {
            	kz.tamur.util.Funcs.writeStream(is, hsos, Constants.MAX_ARCHIVED_SIZE);
            } catch (Exception e) {
                trace("Exception = " + e.getMessage());
                log.error(e, e);
                hsos.close();
                throw e;
            }
            hsos.close();
        }
        pw = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(file.getPath(), true), "UTF-8"));
        pw.print(
            "</Row>\r\n"
        );
        pw.close();
    }
    private void trace(String text) {
        Replication.trace(text, log);
    }
    private void traceImportant(String text) {
        Replication.trace_important(text, log);
    }
    
    private File createFile() throws Exception {
        File filesDir = kz.tamur.util.Funcs.getCanonicalFile(ses.getReplicationDirectory(), "Export");
        filesDir.mkdirs();
        
        File file = new File(filesDir, "xxx.xml");
        if (file.exists())
            if (!file.delete()) {
                trace("file " + file.getName() + " not successfuly deleted.");
                return null;
            }
        file.createNewFile();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file.getPath(), true), "UTF-8"));
        try {
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<Export>");
        } finally {
            pw.close();
        }
        return file;
    }

    protected void clearToRecycle(File file, String Dir) {
        if (file != null && file.exists()) {
            try {
                File musorDir = new File(Dir);
                musorDir.mkdirs();
                File new_file = new File(musorDir, file.getName());
                file.renameTo(new_file);
                file = new_file;
            } catch (Exception e) {
                log.error(e, e);
                trace(e.getMessage());
            }
        }
    }
    private KrnObject registerExport(
            long modelChangeId, 
            long priorModelChangeId,
            long dataChangeId,
            long priorDataChangeId, String info, String scriptOnBeforeAction, String scriptOnAfterAction
            ) throws KrnException {
        try {
            // создание объекта текущего экспорта в БД
            KrnClass cls = ses.getClassByName("Export");
            KrnObject obj = ses.createObject(cls, 0);
            ses.setLong(obj.id,
                ses.getAttributeByName(cls, "clschange_id").id, 0, modelChangeId, 0);
            ses.setLong(obj.id,
                ses.getAttributeByName(cls, "prior_clschange_id").id, 0, priorModelChangeId, 0);
            ses.setLong(obj.id,
                ses.getAttributeByName(cls, "change_id").id, 0, dataChangeId, 0);
            ses.setLong(obj.id,
                ses.getAttributeByName(cls, "prior_change_id").id, 0, priorDataChangeId, 0);
            com.cifs.or2.kernel.Date date = kz.tamur.util.Funcs.convertDate(
                    new java.util.Date(System.currentTimeMillis()));
            ses.setDate(obj.id,
                ses.getAttributeByName(cls, "date").id, 0, date, 0);
            ses.setString(obj.id, ses.getAttributeByName(
                    cls, "scriptOnBeforeAction").id,
                    0, 0, true, scriptOnBeforeAction, 0);
            ses.setString(obj.id, ses.getAttributeByName(
                    cls, "scriptOnAfterAction").id,
                    0, 0, true, scriptOnAfterAction, 0);
            ses.setString(obj.id, ses.getAttributeByName(
                    cls, "информация о содержимом файла").id,
                    0, 0, true, info, 0);
            return obj;
        } catch (Exception e) {
            ses.rollbackTransaction();
            log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }
    private void addInfo(File file, KrnObject exp, KrnObject prior)
    throws KrnException {
        addInfo(file, exp, prior, false);
    }
    private void addInfo(File file, KrnObject exp, KrnObject priorExp, boolean realFirstExport)
    throws KrnException {
        try {
            PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file.getPath(), true), "UTF-8"
                    )
            );
            try {
                KrnObject curDb = ses.getCurrentDb();
                KrnClass cls = ses.getClassByName("Структура баз");
                KrnClass clsImpExp = ses.getClassByName("ImpExp");
                String curDbName = ses.getStringsSingular(
                        curDb.id, ses.getAttributeByName(cls, "наименование").id,
                        0, false, true);
                String scriptOnBeforeAction = ses.getStringsSingular(exp.id, ses.getAttributeByName(
                        clsImpExp, "scriptOnBeforeAction").id,
                        0, true, false);
                String scriptOnAfterAction = ses.getStringsSingular(exp.id, ses.getAttributeByName(
                        clsImpExp, "scriptOnAfterAction").id,
                        0, true, false);
                String info = ses.getStringsSingular(exp.id, ses.getAttributeByName(
                        clsImpExp, "информация о содержимом файла").id,
                        0, true, false);
                String priorId = "0";
                if (!realFirstExport && priorExp != null)
                    priorId = "" + priorExp.id;
                
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String exportTime = format.format(new Date());
                		
                pw.println("<BeforeAction>");
                pw.println(Funcs.xmlQuote(scriptOnBeforeAction));
                pw.println("</BeforeAction>");
                
                pw.println("<AfterAction>");
                pw.println(Funcs.xmlQuote(scriptOnAfterAction));
	            pw.println("</AfterAction>");
                
                pw.println("<Info id=\"" + exp.id +
                        "\" priorId=\"" + priorId +
                        "\" curDbUid=\"" + curDb.uid +
                        "\" curDbName=\"" + curDbName +
                        "\" exportTime=\"" + exportTime +
                        "\" toDbUid=\"" + // здесь будет uid пакета
                        "\" toDbName=\"" + // здесь будет название пакета
                        "\" >");
	            pw.println(Funcs.xmlQuote(info));
	            pw.println("</Info>");
	            pw.println("</Export>");
            
            } finally {
                pw.close();
            }
        } catch (Throwable ex) {
            log.error(ex, ex);
            throw new KrnException(0, "ERROR: " + ex.getMessage());
        }
    }
    private void renameFile(File file, long expId) throws KrnException {
        File nFile = new File(file.getParent(), getFileName(expId));
        if(nFile.exists())
            if (!nFile.delete())
                trace("file \"" + file.getName() + "\" not deleted!");
        if(!file.renameTo(nFile))
            trace("file \"" + file.getName() + "\" not renamed to " + nFile.getName() + "!");
    }
    private String getFileName(long expId) throws KrnException {
        return "R_" + ses.getCurrentDb().uid.replace('.','_') +
                "_" + expId +".xml";
    }
}
