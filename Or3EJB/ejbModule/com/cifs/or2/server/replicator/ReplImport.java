package com.cifs.or2.server.replicator;

import static com.cifs.or2.kernel.ModelChange.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kz.tamur.ods.AttributeChange;
import kz.tamur.ods.ClassChange;
import kz.tamur.ods.DataChange;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.IndexChange;
import kz.tamur.ods.IndexKeyChange;
import kz.tamur.ods.MethodChange;
import kz.tamur.ods.TriggerChange;
import kz.tamur.ods.oracle.OracleDriver3;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.sgds.HexStringOutputStream;
import com.cifs.or2.util.Funcs;

import static kz.tamur.or3ee.common.SessionIds.*;

/**
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: 30.03.2006
 * Time: 9:24:07
 * To change this template use File | Settings | File Templates.
 */
public class ReplImport {

    /*  re - Replication Entity */
    private Log log;
    public static final int RE_CLASS = 0;
    public static final int RE_ATTRIBUTE = 1;
    public static final int RE_CONSTRAINT = 2;

    /*  ra - Replication Action */
    public static final int RA_CREATE = 0;
    public static final int RA_MODIFY = 1;
    public static final int RA_DROP = 2;
    public static final int RE_REV_ATTRIBUTE = 3;

    private Session ses;
    private kz.tamur.ods.Driver drv;
    private long expId;
    private KrnAttribute attr_creating;
    private KrnAttribute attr_deleting;
    private KrnObject oldObj;
    private long oldAttrId;
    private boolean forceImport;
    
    private List<String> createdUIDs = new ArrayList<>();
    private List<String> excludeClsIds = new ArrayList<>();

    public ReplImport(Session session) {
        String fi = kz.tamur.util.Funcs.getSystemProperty("forceImport");
        forceImport = fi != null && (fi.equals("true") || fi.equals("1"));
        ses = session;
        this.log = LogFactory.getLog(ses.getDsName() + "." + ses.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());

        KrnClass cls = ses.getClassByName("Объект");
        attr_creating = ses.getAttributeByName(cls, "creating");
        attr_deleting = ses.getAttributeByName(cls, "deleting");

        oldAttrId = 0;
    }
    
    public String run() throws KrnException {
    	//Считывание информации о классах
		String excludeCls=System.getProperty("excludeCls", "");
		if(!"".equals(excludeCls)) {
			String[] clsIds=excludeCls.split(",");
			for(String excludeClsId:clsIds) {
				excludeClsIds.add(excludeClsId);
			}
		}

        String fname = "", res = "";
        KrnObject obj = null;
        KrnClass cls = ses.getClassByName("Import");
        try {
        	// Перед репликацией убираем учет контроля версий
        	Driver2.isImportState = true;
        	Driver2.importObjId = -1;
            File filesDir = new File(ses.getReplicationDirectory(), "Import");
            KrnObject parentDb = ses.getObjectsSingular(ses.getCurrentDb().id, ses.getAttributeByName(ses.getClassByName("Структура баз"), "родитель").id, false);
            if (parentDb == null) {
            	log.warn("Текущая база данных не имеет родителя!");
                res += "\nТекущая база данных не имеет родителя!";
            } else {
	            List<Path> files = kz.tamur.util.Funcs.fileList(Paths.get(filesDir.getCanonicalPath()), "R_" + parentDb.uid.replace('.','_').trim() + "_*.xml");
	            
	            boolean haveMissingObjects = false;
	            
                createdUIDs.clear();

	            if (files != null && files.size() > 0) {
	                files = sortFiles(files);
	                Time importStart;
	                Time importFinish;
	                for (int i = 0; i < files.size(); i++) {
	                	importStart = kz.tamur.util.Funcs.convertTime(new java.util.Date());
	                	File file = files.get(i).toFile();
	                    fname = file.getName();
	                    trace("reading: " + fname);
	                    File nFile = isNextFile(file);
	                    
	                    if (nFile == null) {
	                        trace("file \""+fname+"\" not suitable for import.");
	                        ses.writeLogRecord(SystemEvent.WARNING_REPL, "Файл: " + fname + " - не подходит",cls.id,-1);
	                        res+="\nФайл: " + fname + " - не подходит";
	                        log.warn("Файл: " + fname + " - не подходит");
	                        continue;
	                    }
	                    
		            	ses.sendMessage(0, files.size(), i + 1, file.getName(), importStart);

	                    // Создание объекта текущего импорта в БД
	                    obj = ses.createObject(cls, 0);
	                    createdUIDs.add(obj.uid);
	                    
	                    Driver2.importObjId=obj.id;
	                    ArrayList parseResult = parseFile(nFile);
	                    String info = (String) parseResult.get(3);
	                    
	                    ses.commitVcsObjectsAfterReplication(obj.id, "Файл: " + fname);
	                    
	                    importFinish = kz.tamur.util.Funcs.convertTime(new java.util.Date());
	                    
	                    boolean b = (Boolean) parseResult.get(0);
	                    
	                    if (haveMissingObjects == false && b == true) {
	                    	haveMissingObjects = true;
	                    }
	                    	
	                    log.info("Репликационный файл: " + fname);
						registerImport(nFile, parseResult, obj, importStart, importFinish);
	                    clearToRecycle(nFile, "oldImport");
	                    ses.commitTransaction();
	                    
	                    createdUIDs.clear();
		            	
	                    ses.sendMessage(1, files.size(), i + 1, file.getName(), importFinish);

	                    ses.writeLogRecord(SystemEvent.EVENT_REPL, "Файл: " + fname,cls.id,-1);
	                }
	                if (haveMissingObjects == true) { 
		                res = "Внимание! Импорт данных завершен.\n" +
	                		"При импорте обнаружено отсутствие объектов, номера которых зафиксированы в файлах формата MIS_OBJ_НаименованиеРепликационногоФайла.\n" + 
	                		"Обратитесь к администратору или разработчикам Системы.";
	                }
	            } else {                
	            	traceImportant("no import.");
	            	res = "Репликационные файлы отсутствуют.";
	            }
            }
        } catch (Exception e) {
            log.error(e, e);
            ses.rollbackTransaction();
            ses.removeObjectsFromCache(createdUIDs);
            
            if (obj != null) {
            	ses.deleteObject(obj, 0);
            	ses.commitTransaction();
            }
            trace("ERROR: " + e.getMessage());
            ses.writeLogRecord(SystemEvent.ERROR_REPL, "Файл: " + fname,cls.id,-1);
            res+="\nERROR: " + e.getMessage();
        } finally {
        	// После завершения репликации возвращаем учет контроля версий
        	Driver2.isImportState=false;
        	Driver2.importObjId=-1;
        }
        return res;
    }

    private ArrayList parseFile(File file) throws KrnException {
        String rplTrId = System.getProperties().getProperty("rplTrId");
    	ArrayList res = new ArrayList();
        traceImportant("parseFile(\"" + file.getName()+"\")");

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        XMLReader xmlReader = null;
        try {
            // Create a JAXP SAXParser
            SAXParser saxParser = spf.newSAXParser();
            // Get the encapsulated SAX XMLReader
            xmlReader = saxParser.getXMLReader();
        } catch (Exception ex) {
        	log.error(ex, ex);
            trace(ex.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        }

        SAX_changesScan changesScan = new SAX_changesScan();
        xmlReader.setContentHandler(changesScan);
        xmlReader.setErrorHandler(new MyErrorHandler(System.err));
        try {
        	InputSource is = new InputSource(new FileInputStream(file));
        	xmlReader.parse(is);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        System.out.println("ModelChanges: " + changesScan.getModelChanges());
        System.out.println("DataChanges: " + changesScan.getDataChanges());

        // Set the ContentHandler of the XMLReader
        SAX_extended_attr sax = new SAX_extended_attr();
        xmlReader.setContentHandler(sax);
        xmlReader.setErrorHandler(new MyErrorHandler(System.err));
        try {
        	InputSource is = new InputSource(new FileInputStream(file));
            xmlReader.parse(is);
        } catch (SAXException se) {
        	log.error(se, se);
            trace(se.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        } catch (IOException ioe) {
        	log.error(ioe, ioe);
            trace(ioe.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
		}
        String scriptOnBeforeAction = sax.scriptOnBeforeAction;
        String scriptOnAfterAction = sax.scriptOnAfterAction;
        String info = sax.info;
        expId = sax.expId;

        long trId;
        if(rplTrId!=null && "0".equals(rplTrId))
        	trId = 0;
        else
            trId = ses.createLongTransaction();


        // Run script on before replication
        if (scriptOnBeforeAction != null && !scriptOnBeforeAction.equals("")) {
    		ses.sendMessage(3, 0, 0, "ScriptOnBeforeAction", "");
	        ses.execute(scriptOnBeforeAction, trId, new HashMap<String, Object>());
    		ses.sendMessage(3, 1, 0, "ScriptOnBeforeAction", "");
        }

		ses.sendMessage(2, -1, changesScan.getModelChanges(), "ModelChange", "");
        
        // Set the ContentHandler of the XMLReader
        SAX_modelChanges SH = new SAX_modelChanges(changesScan.getModelChanges());
        xmlReader.setContentHandler(SH);
        xmlReader.setErrorHandler(new MyErrorHandler(System.err));
        try {
        	InputSource is = new InputSource(new FileInputStream(file));
            xmlReader.parse(is);
        } catch (SAXException se) {
        	log.error(se, se);
            trace(se.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        } catch (IOException ioe) {
            trace(ioe.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        }
        
		ses.sendMessage(2, -1, changesScan.getDataChanges(), "DataChange", "");

        SAX_dataChanges SDC = new SAX_dataChanges(trId, changesScan.getDataChanges());
        xmlReader.setContentHandler(SDC);
        try {
        	InputSource is = new InputSource(new FileInputStream(file));
            xmlReader.parse(is);
        } catch (SAXException se) {
        	log.error(se, se);
            trace(se.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        } catch (IOException ioe) {
        	log.error(ioe, ioe);
            trace(ioe.getMessage());
            throw new KrnException(0, "Ошибка при чтении репликационного файла!");
        }
        
        Iterator<Long> it = SDC.updatedFilters.iterator();
        while(it.hasNext()) {
            ses.saveFilter(it.next(), trId);
        }

        if (scriptOnAfterAction != null && !scriptOnAfterAction.equals("")) {
    		ses.sendMessage(3, 0, 0, "ScriptOnAfterAction", "");
	        ses.execute(scriptOnAfterAction, trId, new HashMap<String, Object>());
    		ses.sendMessage(3, 1, 0, "ScriptOnAfterAction", "");
        }
        
        ses.commitLongTransaction(trId, 0);

        boolean haveMis = false;
        if (SDC.missingObjects.length() > 0) {
        	String replFileName = file.getName();
        	replFileName = replFileName.replaceAll(".xml", ".txt"); 
            File missingObjectFile = new File("MIS_OBJ_" + replFileName);
            if (missingObjectFile.exists())
            	missingObjectFile.delete();
            try {
            	missingObjectFile.createNewFile();
                PrintWriter pw = new PrintWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                		missingObjectFile.getPath(), true), "UTF-8"));
                try {
                    pw.print(SDC.missingObjects.toString());
                } finally {
                    pw.close();
                }
            } catch (IOException e) {
            	log.error("ERROR IOException: " + e.getMessage());
            }
            haveMis = true;
        }
        res.add(new Boolean(haveMis));
        res.add(scriptOnBeforeAction);
        res.add(scriptOnAfterAction);
        res.add(info);
        return res;
    }
    
    public class ReplImportFilenameFilter implements FilenameFilter {
        Pattern re;

        public ReplImportFilenameFilter(String mask) throws KrnException {
            try {
                re = Pattern.compile(mask);
            } catch (Exception e) {
                log.error(e, e);
                throw new KrnException(0, e.getMessage());
            }
        }

        public boolean accept(File dir, String name) {
            return re.matcher(name).find();
        }
    }

	private List<Path> sortFiles(List<Path> files) {
		Comparator<Path> c = new Comparator<Path>() {
			public int compare(Path f1, Path f2) {
				int res = 0;
				if (getId(f1) < getId(f2))
					res = -1;
				else if (getId(f1) > getId(f2))
					res = 1;
				return res;
			}

			long getId(Path f) {
				String s = f.getFileName().toString();
				int i = s.lastIndexOf('_');
				s = s.substring(i + 1, s.indexOf("."));
				return Long.parseLong(s);
			}
		};
		java.util.Collections.sort(files, c);
		return files;
	}
	
    private File isNextFile(File f) throws KrnException {
        // находим максимальное значение exp_id из всех импортов, произведенных от рассматривоемой базы
        KrnClass clsImport = ses.getClassByName("Import");
        KrnObject[] imps = ses.getClassObjects(
                clsImport, new long[0], 0);
        KrnAttribute attr_ExpId = ses.getAttributeByName(clsImport, "exp_id");
        LongValue[] vals = ses.getLongValues(Funcs.makeObjectIdArray(imps),
                attr_ExpId.id, 0);
        long max = 0;
        for (int i = 0; i < vals.length; i++) {
            if (vals[i].value > max)
                max = vals[i].value;
        }
        trace("last_exp_id = " + max);
        if (compareWithPriorId(f, max)) {
            trace("This file is next");
            return f;
        }
        return null;
    }
    
    private boolean compareWithPriorId(File file, long last_exp_id) throws KrnException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);

        XMLReader xmlReader = null;
        try {
            // Create a JAXP SAXParser
            SAXParser saxParser = spf.newSAXParser();

            // Get the encapsulated SAX XMLReader
            xmlReader = saxParser.getXMLReader();
        } catch (ParserConfigurationException|SAXException ex) {
            throw new KrnException("Ошибка при создании XML парсера.", 0, ex);
        }

        SAX_getPriorId gp = new SAX_getPriorId();
        gp.exp_id = last_exp_id;

        xmlReader.setContentHandler(gp);
        xmlReader.setErrorHandler(new MyErrorHandler(System.err));

        try {
        	InputSource is = new InputSource(new FileInputStream(file));
            xmlReader.parse(is);
        } catch (SAXException se) {
            log.error(se.getMessage());
        } catch (IOException ioe) {
        	log.error(ioe);
        }
        return gp.found;
    }
    
    private class SAX_getPriorId extends DefaultHandler {
        private long exp_id = 0;
        private boolean found = false;
        public void startElement(String namespaceURI, String localName, String qName,
                                 org.xml.sax.Attributes atts) throws SAXException {
            if (qName.equals("Info"))
                if (!found) {
                    found = exp_id < Long.parseLong(atts.getValue("id"));
                    if (found) {
                        long pId = Long.parseLong(atts.getValue("priorId"));
                        if (pId == 0)
                            found = true;
                        else {
                            found = pId == exp_id;
                            if (!found) {
                                log.warn("Prior file not imported.");
                                log.warn("Предыдущие репликационный файл не импортирован.");
                            }
                        }
                    }
                }
        }
    }
    
    private class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private PrintStream out;

        MyErrorHandler(PrintStream out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                    " Line=" + spe.getLineNumber() +
                    ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
    
    private class SAX_modelChanges extends DefaultHandler {

        private ClassChange chCls;
        private AttributeChange chAttr;
        private TriggerChange chTrigger;
        private MethodChange chMethod;
        private StringBuilder comment;
        private IndexChange chIndex;        
        private StringBuilder expr;

        private int changesCount;
        private int currentChangeNumber;
        
        public SAX_modelChanges(int changesCount) {
        	this.changesCount = changesCount;
    	}
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (qName.equals("Comment")) {
				comment = new StringBuilder();
			} else if (qName.equals("Expr")) {
				expr = new StringBuilder();
			} else if (qName.equals("Class")) {
				long id = Long.parseLong(atts.getValue("id"));
				//Проверка на исключение класса из импорта
				if(excludeClsIds.contains(""+id)) {
					log.info("class exclude: change_id = " + id);
					return;
				}
				//
				int action = Integer.parseInt(atts.getValue("action"));
				String entity_id = atts.getValue("entityId").trim();
				String name = atts.getValue("name");
				String tname = atts.getValue("tname");
				int mod = atts.getValue("mod") != null ? Integer.parseInt(atts.getValue("action")) : 0;
				if (action != 2 && "".equals(name)) {
					final String errMsg = "ERROR: class_id = " + entity_id + ". The name is empty!";
					log.error(errMsg);
					ses.rollbackTransactionQuietly();
					throw new SAXException(errMsg);
				}
				boolean isRepl = Integer.parseInt(atts.getValue("isRepl")) == 1;
				try {
					String parentUid = atts.getValue("parent").trim();
					KrnClass parentCls = (parentUid != null && parentUid.length() > 0) ? ses.getClassByUid(parentUid) : null;
					log.info("class: change_id = " + id);

					currentChangeNumber++;
					ses.sendMessage(0, currentChangeNumber, changesCount, "ModelChange", atts.getValue("id"));

					chCls = new ClassChange(id, action, entity_id, name, parentCls, isRepl, tname, mod);
				} catch (KrnException e) {
					log.error(e, e);
					ses.rollbackTransactionQuietly();
					throw new SAXException(e);
				}
			} else if (qName.equals("Trigger")) {
				long id = Long.parseLong(atts.getValue("id"));
				int action = Integer.parseInt(atts.getValue("action"));
				String entity_id = atts.getValue("entityId").trim();
				String name = atts.getValue("name");
				int type = Integer.parseInt(atts.getValue("type"));
				//Проверка на исключение тригера из импорта
				/*if(type<=ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE) {
					try {
						KrnClass cls = ses.getClassByUid(entity_id);
						if(cls!=null && excludeClsIds.contains(""+cls.id)) {
		    				log.info("exclude trigger: change_id = " + id+";clsId="+cls.id);
							return;
						}
					} catch (KrnException e) {
						e.printStackTrace();
					}
				}else {
					try {
						KrnAttribute attr = ses.getAttributeByUid(entity_id);
						if(attr!=null && excludeClsIds.contains(""+attr.classId) || excludeClsIds.contains(""+attr.typeClassId)) {
		    				log.info("exclude trigger: change_id = " + id);
							return;
						}
					} catch (KrnException e) {
						e.printStackTrace();
					}
				}*/
				//пока не нужно
				String tmp = atts.getValue("tr");
				int tr = tmp != null ? Integer.parseInt(tmp) : 1;
				log.info("trigger: change_id = " + id);
				
				currentChangeNumber++;
				ses.sendMessage(0, currentChangeNumber, changesCount, "ModelChange", atts.getValue("id"));
				
				chTrigger = new TriggerChange(id, action, entity_id, name, type, tr);
			} else if (qName.equals("Attribute")) {
				long id = Long.parseLong(atts.getValue("id"));
				String idCls = atts.getValue("class");
				String idTypeCls = atts.getValue("type");
				//Проверка на исключение атрибута из импорта
				/*if(excludeClsIds.contains(""+idCls) || excludeClsIds.contains(""+idTypeCls)) {
                	log.info("attribute exclude: change_id = " + id+";idCls"+idCls+";idTypeDlc"+idTypeCls);
					return;
				}*/
				//пока не нужно

				int action = Integer.parseInt(atts.getValue("action"));
				String entity_id = atts.getValue("entityId").trim();
				String name = atts.getValue("name");
				String tname = atts.getValue("tname");
				if (action != 2 && "".equals(name)) {
					final String errMsg = "ERROR: attr_id = " + entity_id + ". The name is empty!";
					log.error(errMsg);
					ses.rollbackTransactionQuietly();
					throw new SAXException(errMsg);
				}
				int colType = Integer.parseInt(atts.getValue("colType"));
				boolean isUnique = Integer.parseInt(atts.getValue("isUnique")) == 1;
				boolean isIndexed = Integer.parseInt(atts.getValue("isIndexed")) == 1;
				boolean isMultilingual = Integer.parseInt(atts.getValue("isMultilingual")) == 1;
				boolean isRepl = Integer.parseInt(atts.getValue("isRepl")) == 1;
				int size = Integer.parseInt(atts.getValue("size"));
				long flags = Integer.parseInt(atts.getValue("flags"));
				boolean sDesc = Integer.parseInt(atts.getValue("sDesc")) == 1;
				String revIds = atts.getValue("revIds");
				String accessModifierAttr = atts.getValue("accessModifier");
				int accessModifier = accessModifierAttr == null ? 0 : Integer.parseInt(accessModifierAttr);
				try {
                	log.info("attribute: change_id = " + id);
                    KrnClass cls = action != 2 ? ses.getClassByUid(atts.getValue("class").trim()) : null;
                    KrnClass type = action != 2 ? ses.getClassByUid(atts.getValue("type").trim()) : null;
                    
                    String rAttrUid = atts.getValue("rAttrId");
                    String sAttrUid = atts.getValue("sAttrId");

                    KrnAttribute rAttr = null, sAttr = null;;
                    
                    if (!"0".equals(rAttrUid)) {
                    	rAttr = ses.getAttributeByUid(rAttrUid.trim());
                    	if (rAttr == null) throw new KrnException(0, "Not found reverse attribute!!!");
                    }
                    if (!"0".equals(sAttrUid)) {
                    	sAttr = ses.getAttributeByUid(sAttrUid.trim());
                    	if (sAttr == null) throw new KrnException(0, "Not found sort attribute!!!");
                    }

                    currentChangeNumber++;
	            	ses.sendMessage(0, currentChangeNumber, changesCount, "ModelChange", atts.getValue("id"));

	            	chAttr = new AttributeChange(id, action, entity_id, name, cls, type, colType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttr, sAttr, sDesc, revIds, tname, accessModifier);
                } catch (KrnException e) {
                    log.error(e, e);
                    ses.rollbackTransactionQuietly();
                    throw new SAXException(e);
                }
			} else if (qName.equals("Method")) {
				long id = Long.parseLong(atts.getValue("id"));
				int action = Integer.parseInt(atts.getValue("action"));
				String entity_id = atts.getValue("entityId").trim();
				String name = atts.getValue("name");
				if (action != 2 && "".equals(name)) {
					final String errMsg = "ERROR: method_id = " + entity_id + ". The name is empty!";
					log.error(errMsg);
					ses.rollbackTransactionQuietly();
					throw new SAXException(errMsg);
				}
				boolean isCMethod = Integer.parseInt(atts.getValue("isCMethod")) == 1;
				try {
					log.info("method: change_id = " + id);
					String clsUid = atts.getValue("class").trim();
					KrnClass cls = !("0".equals(clsUid) || "".equals(clsUid)) ? ses.getClassByUid(clsUid) : null;

					//Проверка на исключение метода из импорта
					if(cls!=null && excludeClsIds.contains(""+cls.id)) {
	                	log.info("method exclude: change_id = " + id+";idCls"+cls.id);
						return;
					}
					//
					currentChangeNumber++;
					ses.sendMessage(0, currentChangeNumber, changesCount, "ModelChange", atts.getValue("id"));

					chMethod = new MethodChange(id, action, entity_id, name, cls, isCMethod);
				} catch (KrnException e) {
					log.error(e, e);
					ses.rollbackTransactionQuietly();
					throw new SAXException(e);
				}
			} else if (qName.equals("Index")) {
				try {
					long id = Long.parseLong(atts.getValue("id"));
					int action = Integer.parseInt(atts.getValue("action"));
					String entityId = atts.getValue("entityId");
					String clsUid = atts.getValue("class");
					KrnClass cls = null;
					if (!"".equals(clsUid)) {
						if (ses.classExists(clsUid)) {
							cls = ses.getClassByUid(clsUid);
						}
					}

					currentChangeNumber++;
					ses.sendMessage(0, currentChangeNumber, changesCount, "ModelChange", atts.getValue("id"));

					chIndex = new IndexChange(id, action, entityId, cls);
				} catch (KrnException e) {
					log.error(e, e);
					ses.rollbackTransactionQuietly();
					throw new SAXException(e);
				}
			} else if (qName.equals("Key")) {
				try {
					String attrUid = atts.getValue("attr").trim();
					long keyno = Long.parseLong(atts.getValue("keyno"));
					boolean isDesc = Integer.parseInt(atts.getValue("isDesc")) == 1;
					KrnAttribute krnAttr = null;
					if (ses.attributeExists(attrUid)) {
						krnAttr = ses.getAttributeByUid(attrUid);
					}
					chIndex.addKeyChange(new IndexKeyChange(krnAttr, keyno, isDesc));
				} catch (KrnException e) {
					log.error(e, e);
					ses.rollbackTransactionQuietly();
					throw new SAXException(e);
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (comment != null) {
				comment.append(ch, start, length);
			} else if (expr != null) {
				expr.append(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			try {
				if (comment != null) {
					if (chCls != null) {
						chCls.comment = comment.toString();
					} else if (chAttr != null) {
						chAttr.comment = comment.toString();
					} else if (chMethod != null) {
						chMethod.comment = comment.toString();
					}
					comment = null;
				} else if (expr != null) {
					byte[] me = HexStringOutputStream.fromHexString(expr.toString());
					try {
						if (chMethod != null) {
							chMethod.expr = new String(me, "UTF-8");
						} else if (chTrigger != null) {
							chTrigger.expr = new String(me, "UTF-8");
						}
					} catch (UnsupportedEncodingException e) {
						log.error(e, e);
					}
					expr = null;
				} else if (chCls != null) {
					setChange(chCls);
					chCls = null;
				} else if (chTrigger != null) {
					setChange(chTrigger);
					chTrigger = null;
				} else if (chAttr != null) {
					setChange(chAttr);
					chAttr = null;
				} else if (chMethod != null) {
					setChange(chMethod);
					chMethod = null;
				} else if (qName.equals("Index")) {
					setChange(chIndex);
					chIndex = null;
				}
				//необходимо комитить каждую запись, т.к. при создании атрибутов или классов 
				//создаются таблицы или колонки в таблицах, а такие изменения не откатываются,
				//откатываются только записи в таблице атрибутов и классов,в результате при падении 
				//на какой то записи откатываются и все предыдущие, а хвосты остаются.
				ses.commitTransaction();
			} catch (KrnException e) {
				ses.rollbackTransactionQuietly();
				throw new SAXException(e);
			}
		}
    }
    
	private class SAX_changesScan extends DefaultHandler {
		
		private int dataChangesCount = 0;
		private int modelChangesCount = 0;

		public int getDataChanges() {
			return dataChangesCount;
		}
		
		public int getModelChanges() {
			return modelChangesCount;
		}
		
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (qName == "Row") {
				dataChangesCount++;
			} else if (qName.equals("Class") || qName.equals("Attribute") || qName.equals("Trigger") || qName.equals("Method") || qName.equals("Index")) {
				modelChangesCount++;
			}
		}
	}
    
	private class SAX_extended_attr extends DefaultHandler {

		private String scriptOnBeforeAction;
		private String scriptOnAfterAction;
		private String info;
		private long expId;
		private StringBuilder soba;
		private StringBuilder soaa;
		private StringBuilder sbInfo;

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (qName.equals("BeforeAction")) {
				soba = new StringBuilder();
			} else if (qName.equals("AfterAction")) {
				soaa = new StringBuilder();
			} else if (qName.equals("Info")) {
				sbInfo = new StringBuilder();
				expId = Long.parseLong(atts.getValue("id"));
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (soba != null) {
				soba.append(ch, start, length);
			} else if (soaa != null) {
				soaa.append(ch, start, length);
			} else if (sbInfo != null) {
				sbInfo.append(ch, start, length);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (soba != null) {
				scriptOnBeforeAction = Funcs.reverseXmlQuote(soba.toString().trim());
				soba = null;
			} else if (soaa != null) {
				scriptOnAfterAction = Funcs.reverseXmlQuote(soaa.toString().trim());
				soaa = null;
			} else if (sbInfo != null) {
				info = Funcs.reverseXmlQuote(sbInfo.toString().trim());
				sbInfo = null;
			}
		}
	}
	
    private class SAX_dataChanges extends DefaultHandler {
        private String uid = "";
        private String attrUid = null;
        private String attrName;
        private int index = 0;
        private String langUid = "";
        private StringBuffer elementValue = null;
        private SimpleDateFormat form;
        private int total = 0;
        private int schet = 0;
        private long filterSQLAttrId = 0;
        public List<Long> updatedFilters = new ArrayList<Long>();
        public StringBuffer missingObjects = new StringBuffer();
        private long trId;
        
        private int changesCount;
        private int currentChangeNumber;
        
		public SAX_dataChanges(long trId, int changesCount) {
			this.trId = trId;
			this.changesCount = changesCount;
		}

		public void startDocument() throws SAXException {
			form = new SimpleDateFormat("hh:mm:ss");
			filterSQLAttrId = ses.getAttributeByName(ses.getClassByName("Filter"), "exprSql").id;
		}

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			if (qName == "Row") {
				elementValue = new StringBuffer();
				uid = atts.getValue("obj");
				attrUid = atts.getValue("attr");
				attrName = atts.getValue("attrName");
				index = Integer.parseInt(atts.getValue("i"));
				langUid = atts.getValue("lang");
			}
		}

		public void endElement(String s, String s1, String s2) throws SAXException {
			if (elementValue != null) {
				//Проверяем на исключение объектов класса из импорта
				boolean par_exclude=false;
				if(attr_creating.uid.equals(attrUid) || attr_deleting.uid.equals(attrUid)) {
					try {
						KrnClass cls=ses.getClassByUid(elementValue.toString().trim());
						if(cls!=null && excludeClsIds.contains(""+cls.id)) {
					    	log.info("exclude ch = " + uid + " ;attrUid=" + attrUid+" ;classId=" + cls.id);
							par_exclude=true; 
						}
					} catch (KrnException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else {
					try {
						KrnAttribute attr=ses.getAttributeByUid(attrUid);
						if(attr!=null && excludeClsIds.contains(""+attr.classId) || excludeClsIds.contains(""+attr.typeClassId)) {
					    	log.info("exclude ch = " + uid + " ;attrUid=" + attrUid+" ;classId=" + attr.classId+" ;typeClassId=" + attr.typeClassId);
							par_exclude=true; 
						}
					} catch (KrnException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//
				if (s2.equals("Row") && !par_exclude) {
					try {
						currentChangeNumber++;
						ses.sendMessage(0, currentChangeNumber, changesCount, "DataChange", uid);
						setChange(new DataChange(0, uid, attrUid, attrName, index, langUid, elementValue.toString()), missingObjects, trId);
//						ses.sendMessage(1, currentChangeNumber, changesCount, "DataChange", uid);
						schet++;
						total++;
						if (schet == 1000) {
							schet = 0;
							trace(total + " rows inserted");
						}
						try {
							KrnObject obj = ses.getDirtyObjectByUid(uid, trId);
							if (obj != null && !ses.isDeleted(obj) && obj.classId == ses.getClassByName("Filter").id) {
								if (updatedFilters.indexOf(obj.id) == -1)
									updatedFilters.add(obj.id);
							}
						} catch (KrnException e) {
							log.error(e, e);
						}
					} catch (Throwable e) {
						log.error(e, e);
						if (!forceImport) {
							trace(e.getMessage());
							trace("ERROR: " + uid + " " + attrUid + " " + index + " " + langUid);
							log.error(e, e);
							ses.rollbackTransactionQuietly();
							throw new SAXException(e.getMessage());
						}
					}
				}
			}
			elementValue = null;
		}

		public void characters(char[] chars, int i, int i1) throws SAXException {
			if (elementValue != null) {
				elementValue.append(chars, i, i1);
			}
		}
    }
    
	private KrnObject registerImport(File file, ArrayList parseResult, KrnObject obj, Time importStart, Time importFinish) throws KrnException {
		try {
			KrnClass cls = ses.getClassByName("Import");
			ses.setLong(obj.id, ses.getAttributeByName(cls, "exp_id").id, 0, (long) expId, 0);
			com.cifs.or2.kernel.Date date = kz.tamur.util.Funcs.convertDate(new java.util.Date(System.currentTimeMillis()));
			ses.setDate(obj.id, ses.getAttributeByName(cls, "date").id, 0,date, 0);
			ses.setString(obj.id, ses.getAttributeByName(cls, "file_name").id, 0, 0, false, file.getName(), 0);
			String scriptOnBeforeAction = (String) parseResult.get(1);
			String scriptOnAfterAction = (String) parseResult.get(2);
			String info = (String) parseResult.get(3);
			if (scriptOnBeforeAction != null && !scriptOnBeforeAction.equals(""))
				ses.setString(obj.id, ses.getAttributeByName(cls, "scriptOnBeforeAction").id, 0, 0, true, scriptOnBeforeAction, 0);
			if (scriptOnAfterAction != null && !scriptOnAfterAction.equals(""))
				ses.setString(obj.id, ses.getAttributeByName(cls, "scriptOnAfterAction").id, 0, 0, true, scriptOnAfterAction, 0);
			if (info != null && !info.equals(""))
				ses.setString(obj.id, ses.getAttributeByName(cls, "информация о содержимом файла").id, 0, 0, true, info, 0);
			KrnAttribute importStartAttr = ses.getAttributeByName(cls, "importStart");
			if (importStartAttr != null) {
				ses.setTime(obj.id, importStartAttr.id, 0, importStart, 0);
			}
			KrnAttribute importFinishAttr = ses.getAttributeByName(cls, "importFinish");
			if (importFinishAttr != null) {
				ses.setTime(obj.id, importFinishAttr.id, 0, importFinish, 0);
			}
		} catch (Exception e) {
			ses.rollbackTransaction();
			log.error(e, e);
			throw new KrnException(0, e.getMessage());
		}
		return obj;
	}
    
    private void setChange(ClassChange ch) throws KrnException {
        try {
            if (ch.action == RA_CREATE) {
                ses.createClass(ch.parentCls.id, ch.name, ch.isRepl, -1, ch.entityId, ch.tname, ch.mod);
                ses.setClassComment(ch.entityId, ch.comment);
            } else if (ch.action == RA_MODIFY) {
            	KrnClass cls = ses.getClassByUid(ch.entityId.trim());
                ses.changeClass(cls, ch.parentCls, ch.name, ch.isRepl);
                
                if (ch.tname != null && ch.tname.length() > 0) { //TODO r tname
	            	if (!ch.tname.equalsIgnoreCase(cls.tname) && !ch.tname.equalsIgnoreCase("ct" + cls.id)) {
	            		ses.renameClass(cls, ch.name);
	            	}
	            }
                
                ses.setClassComment(ch.entityId, ch.comment);
                if (drv instanceof OracleDriver3) {
                    drv.commit();	// В Oracle при изменении метаданных происходит автокоммит, для равнобедренного поведения по всем событиям необходимо комитеть и здесь
                }
            } else if (ch.action == RA_DROP) {
            	KrnClass cls = ses.getClassByUid(ch.entityId);
            	if (cls != null)
                    ses.deleteClass(cls);
            }
        } catch (Exception e) {
            if (ses.getClassByUid(ch.entityId) != null) {
            	log.error("Данный класс уже существует!");
            } else {
                log.error(e, e);
                if (ch.action == RA_CREATE && drv instanceof OracleDriver3) {
                    try {
                    	log.warn("Удаление остатков класса неуспешно созданного при импорте репликации: '"+ch.name+"'");
						KrnClass clsForDel=((OracleDriver3)drv).getClassByNameComp(ch.name);
						ses.deleteClass(clsForDel);
						drv.commit();
					} catch (Exception edel) {
                    	log.warn("Ошибка при удаление остатков класса неуспешно созданного при импорте репликации: '"+ch.name+"'");
						edel.printStackTrace();
					}
                }

                if (!forceImport) {
                    throw new KrnException(0, e.getMessage());
                }
            }
        }
    }
    
    private void setChange(TriggerChange ch) throws KrnException {
        try {
        	int type = ch.type;
        	KrnClass cls;
        	KrnAttribute attr;
        	String entityId = ch.entityId;
        	String expr = ch.expr;
        	int tr = ch.tr;
        	switch (type) {
        		case ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE:
        			cls = ses.getClassByUid(entityId);
        			ses.setClsTriggerEventExpression(expr, cls.id, 0, tr == 0);
        			break;
        		case ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE:
        			cls = ses.getClassByUid(entityId);
        			ses.setClsTriggerEventExpression(expr, cls.id, 1, tr == 0);
        			break;
        		case ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE:
        			cls = ses.getClassByUid(entityId);
        			ses.setClsTriggerEventExpression(expr, cls.id, 2, tr == 0);
        			break;
        		case ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE:
        			cls = ses.getClassByUid(entityId);
        			ses.setClsTriggerEventExpression(expr, cls.id, 3, tr == 0);
        			break;
        		case ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE:
        			attr = ses.getAttributeByUid(entityId);
        			ses.setAttrTriggerEventExpression(expr, attr.id, 0, tr == 0);
        			break;
        		case ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE:
        			attr = ses.getAttributeByUid(entityId);
        			ses.setAttrTriggerEventExpression(expr, attr.id, 1, tr == 0);
        			break;
        		case ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE:
        			attr = ses.getAttributeByUid(entityId);
        			ses.setAttrTriggerEventExpression(expr, attr.id, 2, tr == 0);
        			break;
        		case ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE:
        			attr = ses.getAttributeByUid(entityId);
        			ses.setAttrTriggerEventExpression(expr, attr.id, 3, tr == 0);
        			break;
        	}
        } catch (Exception e) {
            log.error(e, e);
	        if (!forceImport) {
	            throw new KrnException(0, e.getMessage());
	        }
        }
    }
    
    private void setChange(AttributeChange ch) throws KrnException {
        try {
        	long rAttrId = ch.rAttr != null ? ch.rAttr.id : 0;
        	long sAttrId = ch.sAttr != null ? ch.sAttr.id : 0;
            if (ch.action == RA_CREATE) {
                ses.createAttribute(-1, ch.entityId, ch.cls.id, ch.type.id, ch.name, ch.collectionType, ch.isUnique, ch.isIndexed, ch.isMultilingual, ch.isRepl, ch.size, ch.flags, rAttrId, sAttrId, ch.sDesc, ch.tname, ch.accessModifier);
                ses.setAttributeComment(ch.entityId, ch.comment);
            } else if (ch.action == RA_MODIFY) {
            	KrnAttribute attr = ses.getAttributeByUid(ch.entityId.trim());
                ses.changeAttribute(attr, ch.type, ch.name, ch.collectionType, ch.isUnique, ch.isIndexed, ch.isMultilingual, ch.isRepl, ch.size, ch.flags, rAttrId, sAttrId, ch.sDesc, ch.tname, ch.accessModifier);
                ses.setAttributeComment(ch.entityId, ch.comment);
                if (drv instanceof OracleDriver3) {
                    drv.commit();   // В Oracle при изменении метаданных происходит автокоммит, для равнобедренного поведения по всем событиям необходимо комитеть и здесь
                }
            } else if (ch.action == RA_DROP) {
                KrnAttribute attr = null;
                attr = ses.getAttributeByUid(ch.entityId);
                if (attr != null)
                	ses.deleteAttribute(attr);
            }
        } catch (Exception e) {
            if (ses.getAttributeByUid(ch.entityId) != null) {
            	log.error("Данный атрибут уже существует!");
            } else {
                log.error(e, e);
                if (ch.action == RA_CREATE && drv instanceof OracleDriver3) {
                    try {
                    	log.warn("Удаление остатков атрибута неуспешно созданного при импорте репликации: '"+ch.name+"'");
						KrnAttribute attrForDel=((OracleDriver3)drv).getAttributeByNameComp(ch.cls,ch.name);
						ses.deleteAttribute(attrForDel);
						drv.commit();
					} catch (Exception edel) {
                    	log.warn("Ошибка при удаление остатков атрибута неуспешно созданного при импорте репликации: '"+ch.name+"'");
						edel.printStackTrace();
					}
                }
                if (!forceImport) {
                    throw new KrnException(0, e.getMessage());
                }
            }
        }
    }
    
    private void setChange(MethodChange ch) throws KrnException {
        try {
            if (ch.action == RA_CREATE)
                try {
                    KrnMethod method = ses.getMethodById(ch.entityId.trim());
                    if (method != null) {
                        if (!method.name.equals(ch.name)) {
                            ses.changeMethod(ch.entityId, ch.name, ch.isCMethod, ch.expr.getBytes("UTF-8"));
                        } else {
                            //при обновлении создания всех методов может быть повторное создание,
                            //такого рода создания игнорируем.
                        }
                    } else {
                        method = ses.createMethod(
                            ch.entityId,
                            ch.cls, ch.name,
                            ch.isCMethod, ch.expr.getBytes("UTF-8")
                        );
                    	ses.setMethodComment(method.uid, ch.comment);
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error(e, e);
                }
            else if (ch.action == RA_MODIFY) {
                try {
                    if (ch.name != null && ch.name.length() > 0) {
                        //если имя пустое, значит метод удален
                        ses.changeMethod(
                            ch.entityId, ch.name,
                            ch.isCMethod, ch.expr.getBytes("UTF-8")
                        );
                        ses.setMethodComment(ch.entityId, ch.comment);
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error(e, e);
                }
            } else if (ch.action == RA_DROP) {
                ses.deleteMethod(ch.entityId);
            }
        } catch (Exception e) {
            if (ses.getMethodById(ch.entityId) != null) {
            	log.error("Данный метод уже существует!");
            } else {
                log.error(e, e);
                if (!forceImport) {
                    throw new KrnException(0, e.getMessage());
                }
            }
        }
    }
    
    private void setChange(IndexChange ch) throws KrnException{
    	try{
	    	if(ch.action == RA_CREATE){
	    		boolean check = true;
	    		if(ch.getKrnClass() == null)
	    			check = false;
	    		if(check){
	    			for(KrnAttribute attr : ch.getKrnAttributes())
	    				if(attr == null){
	    					check = false;
	    					break;
	    				}
	    		}
	    		if(check){
	    			ses.createIndex(ch.getKrnClass(), ch.getKrnAttributes(),ch.getIsDecs(),ch.entityId);
	    		}else{;
	    			if(ch.getKrnClass() == null)
	    				trace("Индекс с uid " + ch.entityId + " не создан, потому что не найден требуемый для индекса класс");
	    			else
	    				trace("Индекс с uid " + ch.entityId + " не создан, потому что не найдены требуемые для индекса атрибут или атрибуты");
	    			
	    		}
	    	}else if(ch.action == RA_DROP){
	    		try{
	    			if(ses.indexExists(ch.entityId)){
	    				ses.deleteIndex(ses.getIndexByUid(ch.entityId));
	    			}else{
	    				trace("Индекс не удален, потому что он был удален ранее или не существовал");
	    			}
	    		}catch(KrnException e){
	    			if(e.getMessage().indexOf("не найден") == -1)
	    				throw new KrnException(e.code,e.getMessage());
	    		}
	    	}
        } catch (Exception e) {
            if (ses.getIndexByUid(ch.entityId) != null) {
            	log.error("Данный индекс уже существует!");
            } else {
                log.error(e, e);
                if (!forceImport) {
                    throw new KrnException(0, e.getMessage());
                }
            }
        }
    }
    
    private int[] getIndexes(long objId, long attrId, long langId) throws KrnException {
        KrnAttribute attr = ses.getAttributeById(attrId);
        SortedMap<Integer, Object> map = SrvUtils.getObjectAttr(objId, attr, langId, 0, ses);
        if (map.size() > 0) {
            int i = 0;
            int[] inds = new int[map.size()];
            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                Number j = (Number) it.next();
                inds[i++] = j.intValue();
            }
            return inds;
        }
        return new int[0];
    }
    private Collection<Object> getValues(long objId, long attrId, long langId) throws KrnException {
        KrnAttribute attr = ses.getAttributeById(attrId);
        SortedMap<Integer, Object> map = SrvUtils.getObjectAttr(objId, attr, langId, 0, ses);
        return map.values();
    }
    private void setChange(DataChange ch, StringBuffer missingObjects, long trId) throws KrnException {
    	log.info("ch = " + ch.uid + " " + ch.attrUid);
        boolean collDeleting = false;
        
        KrnObject obj = null;
        try { obj = getKrnObject(ch.uid); } catch(KrnException e) { log.error(e, e); }
        
        if (obj == null && !ch.attrUid.equals(attr_creating.uid)) {
        	if (missingObjects.indexOf(ch.uid) == -1)
        		missingObjects.append(ch.uid + ",");
        	return;
// 			Если objId = 0 и обрабатывается не событие "создание объекта",  
//        	значит импортируется значение атрибута объекта, несуществующего в текущей БД.
//        	В этом случае текущую импортируемую строку просто игнорируем, 
//        	но uid объекта сохраняем в отдельный лог файл формата MIS_OBJ_НаименованиеРепликационногоФайла.txt
//        	для того, чтобы позже проанализировать причину отсутствия объекта(ов).
//			По заверщению импорта в диалоговом окне сообщается администратору о завершении импорта и о том,
//			что в БД не содержатся необходимые объекты, которые зафиксированы в отдельном лог файле.        	
        }
        
        KrnAttribute attr = null;
        if (obj != null && ch.attrName != null) {
        	KrnClass cls = ses.getClassById(obj.classId);
        	attr = ses.getAttributeByName(cls, ch.attrName);
        } else {
        	attr = ses.getAttributeByUid(ch.attrUid.trim());
        }

        long attrId = attr.id;
        if (oldAttrId != attr.id || oldObj == null || (obj != null && oldObj.id != obj.id)) {
            if (attr.collectionType == 1) {
                int[] indexes = getIndexes(obj.id, attrId,
                        getLocalId(ch.langUid));
                ses.deleteValue(obj.id, attrId, indexes, 0, trId);
                collDeleting = true;
            } else if (attr.collectionType == 2) {
                Collection<Object> values = getValues(obj.id, attrId,
                        getLocalId(ch.langUid));
                ses.deleteValue(obj.id, attrId, values, trId);
                collDeleting = true;
            }
            oldObj = obj;
            oldAttrId = attr.id;
        }
        String v = ch.value.toString().trim();
        long typeId = attr.typeClassId;
        if (v.length() == 0) {
            if (!collDeleting) {
                if (!attr.isMultilingual)
                    ses.deleteValue(obj.id, attrId, new int[]{0}, 0, trId);
                else {
                    if (typeId == CID_STRING) {
                        ses.setString(obj.id, attrId, ch.index, getLocalId(ch.langUid), false, v, trId);
                    } else if (typeId == CID_MEMO) {
                        ses.setString(obj.id, attrId, ch.index, getLocalId(ch.langUid), true, v, trId);
                    } else if (typeId == CID_BLOB) {
                        ses.setBlob(obj.id, attrId, ch.index, new byte[]{}, getLocalId(ch.langUid), trId);
                    }
                }
            }
        } else {
            if (typeId == CID_STRING) {
                ses.setString(obj.id, attrId, ch.index,
                    getLocalId(ch.langUid), false, v, trId);
            } else if (typeId == CID_MEMO) {
                ses.setString(obj.id, attrId, ch.index,
                        getLocalId(ch.langUid), true, v, trId);
            } else if (typeId == CID_INTEGER
                    || typeId == CID_BOOL) {
                if (attr.id == attr_creating.id) {
                    try {
                        KrnClass cls = ses.getClassByUid(v);
                        KrnObject[] objs = ses.getObjectsByUid(new String[]{ch.uid}, trId);
                        if (objs.length == 0) {
                            KrnObject nobj = ses.getDriver().createObject(cls.id, trId, -1, ch.uid);
    	                    createdUIDs.add(nobj.uid);
                        } else {
                            KrnObject o = (KrnObject) objs[0];
                            if (o.classId != cls.id) {
                                throw new KrnException(0, "Object '"+ch.uid+"' already exists with differ classId!");
                            }
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                        throw new KrnException(0, e.getMessage());
                    }
                } else if (attr.id == attr_deleting.id) {
                    if (v.equals("0")) {
                        try{
                        ses.deleteObject(ses.getObjectById(obj.id,-1), trId);
                        }catch(KrnException e){
                            log.error(e, e);
                        }
                    }
                    else if (v.equals("1")) {
                        //@todo доработать когда в системе появиться функция восстановления удаленных объектов
                        //ses.undeleteObject(o, 0);
                    }
                } else
                    ses.setLong(obj.id, attrId, ch.index, Long.parseLong(v), trId);
            } else if (typeId == CID_DATE) {
                ses.setDate(obj.id, attrId, ch.index,
                        convertDate(new java.sql.Date(Long.parseLong(v))), trId);
            } else if (typeId == CID_TIME) {
                ses.setTime(obj.id, attrId, ch.index,
                        convertTime(new Timestamp(Long.parseLong(v))), trId);
            } else if (typeId == CID_BLOB) {
                ses.setBlob(obj.id, attrId, ch.index,
                        convertToByte(v), getLocalId(ch.langUid), trId);
            } else if (typeId == CID_FLOAT) {
                ses.setFloat(obj.id, attrId, ch.index, Double.parseDouble(v), trId);
            } else {
            	KrnObject vobj = getKrnObject(v);
                if (vobj == null) {
                	if (missingObjects.indexOf(v) == -1)
                		missingObjects.append(v + ",");
                } else
                	ses.setObject(obj.id, attrId, ch.index, vobj.id, trId, false);
            }
        }
    }

    private com.cifs.or2.kernel.Date convertDate(java.sql.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new com.cifs.or2.kernel.Date((short)c.get(Calendar.DAY_OF_MONTH), (short)c.get(Calendar.MONTH), (short)c.get(Calendar.YEAR));
    }

    private Time convertTime(Timestamp time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return new Time((short)c.get(Calendar.MILLISECOND),
                (short)c.get(Calendar.SECOND), (short)c.get(Calendar.MINUTE),
                (short)c.get(Calendar.HOUR_OF_DAY), (short)c.get(Calendar.DAY_OF_MONTH),
                (short)c.get(Calendar.MONTH), (short)c.get(Calendar.YEAR));
    }

    public byte[] convertToByte(String val) {
        return HexStringOutputStream.fromHexString(val);
    }

    private KrnObject getKrnObject(String uid) throws KrnException {
        KrnObject[] objs = ses.getObjectsByUid(new String[] {uid}, 0);
        if (objs.length > 0)
            return objs[0];
        return null;
    }
    
    private long getLocalId(String uid) throws KrnException {
    	KrnObject obj = getKrnObject(uid);
    	return obj != null ? obj.id : 0;
    }

    protected void clearToRecycle(File file, String Dir) {
        if (file != null && file.exists()) {
            try {
                File musorDir = new File(Dir);
                musorDir.mkdirs();
                File new_file = new File(musorDir, file.getName());
                if (new_file.exists())
                    new_file.delete();
                file.renameTo(new_file);
                file = new_file;
            } catch (Exception e) {
                log.error(e, e);
                trace(e.getMessage());
            }
        }
    }
    private void trace(String text) {
        Replication.trace(text, log);
    }
    private void traceImportant(String text) {
        Replication.trace_important(text, log);
    }
    protected void rollbackMetaData() throws KrnException {
    	log.error("rollbackMetaData");
        ses.rollbackTransaction();
    }

}
