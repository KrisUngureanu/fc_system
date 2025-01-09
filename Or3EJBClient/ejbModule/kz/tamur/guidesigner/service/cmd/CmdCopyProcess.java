package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.Cmd;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import java.io.*;

import kz.tamur.guidesigner.service.*;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.CreateElementPanel;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:08:56
 * To change this template use File | Settings | File Templates.
 */
public class CmdCopyProcess extends Cmd {

    private MainFrame frm;
    private boolean isOpened;
    private CmdSaveProcess saveCmd;

    public CmdCopyProcess(String name, MainFrame frm, boolean isOpened) {
        super(name);
        this.frm = frm;
        this.isOpened = isOpened;
        saveCmd = new CmdSaveProcess("Save", frm, CmdSaveProcess.SAVE_CURRENT);
    }

    public void doIt() {
        try {
            final Kernel krn = Kernel.instance();
            String text = "";
            ServicesTree tree = frm.getServicesTree();
            if (isOpened) {
                text = frm.getSelectedDocument().getTitle();
            } else {
                text = tree.getSelectedNode().toString();
            }
            CreateElementPanel csp = new CreateElementPanel(CreateElementPanel.COPY_TYPE, text);
            DesignerDialog dlg = new DesignerDialog((JFrame)frm.getTopLevelAncestor(), "Создание копии процесса", csp);
            dlg.setResizable(false);
            dlg.show();
            if (dlg.isOK()) {
                KrnObject obj = null;
                if (isOpened) {
                    obj = frm.getSelectedDocument().getKrnObject();
                } else {
                    obj = frm.getServicesTree().getSelectedNode().getKrnObj();
                }
                String title = csp.getElementName();
                byte[] data = krn.getBlob(obj, "diagram", 0, 0, 0);
                byte[] msg_rus = krn.getBlob(obj, "message", 0, frm.getRusLang(), 0);
                byte[] msg_kaz = krn.getBlob(obj, "message", 0, frm.getKazLang(), 0);
                KrnObject service = krn.createObject(Kernel.SC_PROCESS_DEF, 0);
                KrnObject lang = krn.getInterfaceLanguage();
                String servName = csp.getElementName();
                long langId = (lang != null) ? lang.id : 0;
                krn.setString(service.id, service.classId, "title", 0, langId, servName, 0);
        		long currentUserId = krn.getUserSession().userObj.id;
                krn.setLong(service.id, service.classId, "developer", 0, currentUserId, 0);
                frm.createCopyPropcess(obj, service, servName);
                ServiceModel model = new ServiceModel(true,service,frm.getInterfaceLanguage().id);
                model.setMf(frm);
                krn.writeLogRecord(SystemEvent.EVENT_COPY_PROCESS, "'" + text + "' в '" + servName + "'");
                Document doc = new Document(service, title, model);
                if (data.length > 0) {
                    try {
                        InputStream is_msg_rus = msg_rus.length>0?new ByteArrayInputStream(msg_rus):null;
                        model.loadLangs(is_msg_rus,frm.getRusLang());
                        InputStream is_msg_kaz = msg_kaz.length>0?new ByteArrayInputStream(msg_kaz):null;
                        model.loadLangs(is_msg_kaz,frm.getKazLang());
                        InputStream is = new ByteArrayInputStream(data);
                        model.load(is,doc.getGraph());
                        is.close();
                        if(is_msg_rus!=null)
                            is_msg_rus.close();
                        if(is_msg_kaz!=null)
                            is_msg_kaz.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                saveCmd.save(doc);
                if (isOpened) {
                    frm.setDocument(doc,null);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void undoIt() {
    }
}
