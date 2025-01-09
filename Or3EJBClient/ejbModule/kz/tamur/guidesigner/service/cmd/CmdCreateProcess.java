package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.Cmd;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;

import kz.tamur.guidesigner.service.*;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DesignerTree;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:08:56
 * To change this template use File | Settings | File Templates.
 */
public class CmdCreateProcess extends Cmd {
	private MainFrame frm;

	public CmdCreateProcess(String name, MainFrame frm) {
		super(name);
		this.frm = frm;
	}

	public void doIt() {
		final DesignerTree tree = frm.getServicesTree();
		tree.setShowPopupEnabled(false);
		CreateElementPanel csp = new CreateElementPanel(CreateElementPanel.CREATE_NEW_ELEMENT_TYPE, "", tree);
		DesignerDialog dlg = new DesignerDialog((JFrame) frm.getTopLevelAncestor(), "Создание процесса", csp);
		dlg.setResizable(false);
		dlg.setOkEnabled(false);
		dlg.show();
		if (dlg.isOK()) {
			// final ServicesTree tree = frm.getServicesTree();
			final ServicesTree.ServiceTreeModel treeModel = (ServicesTree.ServiceTreeModel) tree.getModel();
			String servName = ("".equals(csp.getElementName())) ? "Безымянный" : csp.getElementName();
			ServiceNode node = null;
			try {
				node = (ServiceNode) treeModel.createChildNode(servName);
			} catch (KrnException e) {
				e.printStackTrace();
			}
			ServiceModel model = new ServiceModel(true, node.getKrnObj(), frm.getInterfaceLanguage().id);
			model.setMf(frm);
			KrnObject lang = Kernel.instance().getInterfaceLanguage();
			long langId = (lang != null) ? lang.id : 0;
			Document doc = new Document(node.getKrnObj(), servName, model);
			frm.setDocument(doc, null);
			frm.getServicesTree().removeTreeSelectionListener(csp);
			ServiceActionsConteiner.instance(node);
		}
	}

	public void undoIt() {}
}