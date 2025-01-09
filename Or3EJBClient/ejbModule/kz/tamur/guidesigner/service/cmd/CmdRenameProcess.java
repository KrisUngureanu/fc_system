package kz.tamur.guidesigner.service.cmd;

import java.util.List;

import javax.swing.JFrame;

import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.ServiceControlNode;

import org.tigris.gef.base.Cmd;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:08:56
 * To change this template use File | Settings | File Templates.
 */
public class CmdRenameProcess extends Cmd {
    private MainFrame frm;
    private boolean isOpened;

    public CmdRenameProcess(String name, MainFrame frm, boolean isOpened) {
        super(name);
        this.frm = frm;
        this.isOpened = isOpened;
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
            CreateElementPanel csp = new CreateElementPanel(CreateElementPanel.RENAME_TYPE, text);
            DesignerDialog dlg = new DesignerDialog((JFrame)frm.getTopLevelAncestor(), "Переименование процесса", csp);
            dlg.setResizable(false);
            dlg.show();
            if (dlg.isOK()) {
                KrnObject obj = null;
                String servName = csp.getElementName();
                if (isOpened)
                    obj = frm.getSelectedDocument().getKrnObject();
                else
                    obj = frm.getServicesTree().getSelectedNode().getKrnObj();
                KrnObject lang = frm.getInterfaceLanguage();
                long langId = (lang != null) ? lang.id : 0;
                krn.setString(obj.id, obj.classId, "title", 0, langId, servName, 0);
                frm.renameProcess(obj, servName, isOpened);
                krn.writeLogRecord(SystemEvent.EVENT_RENAME_PROCESS, "'" + text + "' в '" + servName + "'");
                
                // Обновить узлы в дереве управления
                ServicesControlTree tree_ = kz.tamur.comps.Utils.getServicesControlTree();
                List<ServiceControlNode> sns = tree_.findAllChild(obj);
                if (sns != null) {
                    for (ServiceControlNode sn : sns) {
                        tree_.renameServiceControlNode2(sn, servName);
                    }
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void undoIt() {}
}