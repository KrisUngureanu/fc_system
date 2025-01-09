package kz.tamur.guidesigner.service.cmd;

import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.swing.JFrame;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.service.Document;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.NodeEventTypeConstants;
import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.guidesigner.service.ServiceActionsConteiner;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.OpenElementPanel;

import org.tigris.gef.base.Cmd;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:08:56
 * To change this template use File | Settings | File Templates.
 */
public class CmdOpenProcess extends Cmd {
    private MainFrame frm;

    public CmdOpenProcess(String name, MainFrame frm) {
        super(name);
        this.frm = frm;
    }

	public void doIt() {
		DesignerTree tree = frm.getServicesTree();
		tree.setShowPopupEnabled(true);
		OpenElementPanel osp = new OpenElementPanel(tree);
		DesignerDialog dlg = new DesignerDialog((JFrame) frm.getTopLevelAncestor(), "Выбор процесса", osp);
		// dlg.setResizable(false);
		osp.setSearchUIDPanel(true);
		if (osp.getTree().getSelectedNode() == null || !osp.getTree().getSelectedNode().isLeaf()) {
			dlg.setOkEnabled(false);
		} else {
			dlg.setOkEnabled(true);
		}
		tree.requestFocusInWindow();
		dlg.show();
		if (dlg.isOK()) {
			AbstractDesignerTreeNode n = osp.getTree().getSelectedNode();
			if (n == null || !n.isLeaf())
				return;
			frm.setLastNode(n);
			KrnObject obj = osp.getNodeObj(n);
			String title = n.toString();
			if (obj == null) {
				return;
			}
			doIt(obj, title, null);
			// Сохранение в историю
			HistoryWithDate hwd = new HistoryWithDate(obj, new Date());
			Kernel.instance().getUser().addSrvInHistory(hwd, title);
		}
	}
    
	public void doIt(KrnObject obj, String title, KrnObject nodeObj) {
		try {
			final Kernel krn = Kernel.instance();
			if (frm.isProcessOpened(obj.id)) {
				return;
			}
			
//			if (krn.getBindingModuleToUserMode()) {
//				KrnObject[] developerObjs = krn.getObjects(obj, "developer", 0);
//            	if (developerObjs.length > 0) {
//            		long ownerId = developerObjs[0].id;
//            		long currentUserId = krn.getUserSession().userObj.id;
//            		if (ownerId != currentUserId) {
//            			KrnObject userObj = krn.getObjectById(ownerId, 0);
//            			if (userObj != null) {	// Владелец процесса существует
//                			KrnClass userCls = krn.getClassByName("User");
//                			KrnAttribute userNameAttr = krn.getAttributeByName(userCls, "name");
//                			String userName = krn.getStringsSingular(ownerId, userNameAttr.id, 0, false, false);
//                            MessagesFactory.showMessageDialog(frm.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, "Владельцем данного процесса является пользователь " + userName + "!");
//            			}
//            		}
//            	}
//            }
			
			boolean readOnly = !Kernel.instance().getUser().hasRight(Or3RightsNode.PROCESS_EDIT_RIGHT);
			if (!readOnly) {
				try {
					UserSessionValue us = krn.vcsLockObject(obj.id);
					if (us != null) {
						int res = MessagesFactory.showMessageDialog(frm.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Процесс '" + title + "' редактируется!\nПользователь: " + us.name + "\nОткрыть процесс в режиме просмотра?", 235, 130);
						if (res == BUTTON_YES) {
							readOnly = true;
						} else {
							return;
						}
					}
					if (!readOnly) {
						us = krn.blockObject(obj.id);
						if (us != null) {
							int res = MessagesFactory.showMessageDialog(frm.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Процесс '" + title + "' заблокирован!\nПользователь: " + us.name + "\nIP адрес: " + us.ip + "\nИмя компьютера: " + us.pcName + "\nОткрыть процесс в режиме просмотра?", 235, 130);
							if (res != ButtonsFactory.BUTTON_YES) {
								return;
							}
							readOnly = true;
						}
					}
				} catch (KrnException e) {
					e.printStackTrace();
				}
			}

			byte[] data = krn.getBlob(obj, "diagram", 0, 0, 0);
			byte[] msg_rus = krn.getBlob(obj, "message", 0, frm.getRusLang(), 0);
			byte[] msg_kaz = krn.getBlob(obj, "message", 0, frm.getKazLang(), 0);
			long langId = frm.getInterfaceLanguage().id;
			ServiceModel model = new ServiceModel(true, obj, langId);
			model.setMf(frm);
			Document doc = new Document(obj, title, model);
			doc.setReadOnly(readOnly);
			try {
				InputStream is_msg_rus = msg_rus.length > 0 ? new ByteArrayInputStream(msg_rus) : null;
				model.loadLangs(is_msg_rus, frm.getRusLang());
				if (is_msg_rus != null)
					is_msg_rus.close();
				InputStream is_msg_kaz = msg_kaz.length > 0 ? new ByteArrayInputStream(msg_kaz) : null;
				model.loadLangs(is_msg_kaz, frm.getKazLang());
				if (is_msg_kaz != null)
					is_msg_kaz.close();
				if (data.length > 0) {
					InputStream is = new ByteArrayInputStream(data);
					model.load(is, doc.getGraph());
					is.close();
				} else {
					model.getProcess().setProperty(NodePropertyConstants.CHOPPER, "#return $user_");
					model.getProcess().setAction(NodeEventTypeConstants.PROCESS_INSTANCE_START, "#set($user_ = $USER)");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			frm.setDocument(doc, nodeObj);
			doc.getGraph().select(model.getProcess().getPresentation());
			model.setLang(langId, doc.getGraph());
			frm.setLangId();
			frm.getServicesTree().setSelectedNode(obj);
			ServiceActionsConteiner.instance(frm.getServicesTree().getSelectedNode());
		} catch (KrnException e) {
			e.printStackTrace();
		}
	}

    public void undoIt() {}
}