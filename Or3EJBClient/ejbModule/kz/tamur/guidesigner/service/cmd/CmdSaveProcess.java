package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.Cmd;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.Document;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.MapMap;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:29:42
 * To change this template use File | Settings | File Templates.
 */
public class CmdSaveProcess extends Cmd {

    public static final int SAVE_CURRENT = 0;
    public static final int SAVE_ALL = 1;

    private MainFrame frm;
    private int saveType = SAVE_CURRENT;

    public CmdSaveProcess(String name, MainFrame frm, int saveType) {
        super(name);
        this.frm = frm;
        this.saveType = saveType;
    }

    public void save(Document doc) {
        final Kernel krn = Kernel.instance();
        try {
            final KrnClass cls = krn.getClassByName("ProcessDef");
            if (doc != null && !doc.isReadOnly()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                doc.getModel().save(os,doc);
                os.close();
                Vector error= doc.getModel().getError();
                if(error!=null && error.size()>0){
                    String msg="Подтвердите сохранение изменений!\n";
                    for (Object anError : error) {
                        msg += "\n" + anError;

                    }
                   int result=MessagesFactory.showMessageDialog(
                            (JFrame)frm.getTopLevelAncestor(),
                           MessagesFactory.QUESTION_MESSAGE, msg);
                    if(result!=ButtonsFactory.BUTTON_YES)
                    return;
                }
                ByteArrayOutputStream ops = new ByteArrayOutputStream();
                doc.getModel().saveProcess(ops,doc);
                boolean isInBox=doc.getModel().isInBox();
                ops.close();
                krn.setBlob(doc.getKrnObject().id, cls.id, "diagram", 0,
                            os.toByteArray(), 0, 0);
                krn.setBlob(doc.getKrnObject().id, cls.id, "config", 0, ops.toByteArray(), 0, 0);
                MapMap<Long, String, String> langMap=doc.getModel().getLangMap();
                for (Long key : langMap.keySet()) {
                    Map<String, String> map = langMap.get(key);
                    if (map != null && map.size() > 0) {
                        Element root = new Element("message");
                        for (String uid_ : map.keySet()) {
                            String value = map.get(uid_);
                            Element xml = new Element("msg");
                            xml.setAttribute("uid", uid_);
                            xml.setText(value);
                            root.addContent(xml);
                        }
                        ByteArrayOutputStream os_msg = new ByteArrayOutputStream();
                        XMLOutputter out = new XMLOutputter();
                        out.getFormat().setEncoding("UTF-8");
                        out.output(root, os_msg);
                        krn.setBlob(doc.getKrnObject().id, cls.id, "message", 0, os_msg.toByteArray(), key.intValue(), 0);
                        String property_expr = (String) langMap.get(key, "process_0");
                        if (property_expr == null || !property_expr.equals(doc.getTitle())) {
                            property_expr = doc.getTitle();
                            langMap.put(key, "process_0", property_expr);
                            krn.setString(doc.getKrnObject().id, cls.id, "title", 0, key, property_expr, 0);
                        }
                    } else {
                        krn.setBlob(doc.getKrnObject().id, cls.id, "message", 0,
                                new byte[0], key.intValue(), 0);
                    }
                }
                MapMap<Long, String, String> stringMap=doc.getModel().getStringsMap();
                for (Long key : stringMap.keySet()) {
                    Map<String, String> map = stringMap.get(key);
                    if (map != null && map.size() > 0) {
                        Element root = new Element("marks");
                        for (String uid_ : map.keySet()) {
                            String value = map.get(uid_);
                            Element xml = new Element("strings");
                            xml.setAttribute("uid", uid_);
                            xml.setAttribute("value", value);
                            root.addContent(xml);
                        }
                        ByteArrayOutputStream os_mrks = new ByteArrayOutputStream();
                        XMLOutputter out = new XMLOutputter();
                        out.getFormat().setEncoding("UTF-8");
                        out.output(root, os_mrks);
                        krn.setBlob(doc.getKrnObject().id, cls.id, "strings", 0,
                                os_mrks.toByteArray(), key, 0);
                    } else {
                        krn.setBlob(doc.getKrnObject().id, cls.id, "strings", 0,
                                new byte[0], key, 0);
                    }
                }
                krn.setLong(doc.getKrnObject().id,cls.id,"isInBox", 0, isInBox?1:0, 0);
                krn.reloadProcessDefinition(doc.getKrnObject().id);
                krn.writeLogRecord(SystemEvent.EVENT_CHANGE_PROCESS, doc.getTitle());
                frm.setProcessModified(false);
                frm.loadServices();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doIt() {
        if (saveType == SAVE_ALL) {
            Document[] docs = frm.getModifiedDocuments();
            for (Document doc : docs) {
                save(doc);
            }
        } else if (saveType == SAVE_CURRENT) {
            Document doc = frm.getSelectedDocument();
            save(doc);
        }
    }

    public void undoIt() {
    }
}
