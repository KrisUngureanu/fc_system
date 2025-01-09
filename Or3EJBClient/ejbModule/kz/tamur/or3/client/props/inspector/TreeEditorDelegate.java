package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.OpenElementPanel;

import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.util.KrnObjectItem;

public class TreeEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Object value;
    private PropertyEditor editor;
    private String className;

    private JLabel label;
	private JButton treeBtn;
	private JButton openBtn;

    public TreeEditorDelegate(JTable table, String className) {
        this.className = className;
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        openBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        treeBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        
        add(openBtn, new CnrBuilder().x(0).build());
        add(treeBtn, new CnrBuilder().x(1).build());
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
    }

	public int getClickCountToStart() {
		return 1;
	}

	public Component getEditorComponent() {
		return this;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
        this.value = value;
        if(value instanceof Vector && ((Vector)value).size()>0){
            String label_="";
            for(int i=0;i<((Vector)value).size();i++){
                label_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).title;
            }
            label.setText(label_);
            openBtn.setEnabled(getKrnObject() == null ? false : true);
        }else if(value instanceof KrnObjectItem){
            label.setText(((KrnObjectItem)value).title);
            openBtn.setEnabled(getKrnObject() == null ? false : true);
        }else{
            label.setText("");
            openBtn.setEnabled(getKrnObject() == null ? false : true);
		}
	}

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
		this.editor = editor;
	}
	
	private KrnObjectItem getKrnObject() {
        KrnObjectItem objectItem = null;
        if (value instanceof Vector && ((Vector) value).size() > 0 && ((Vector) value).get(0) != null) {
        	objectItem = (KrnObjectItem) ((Vector) value).get(0);
        } else if (value instanceof KrnObjectItem && value != null) {
        	objectItem = (KrnObjectItem) value;
        }
    	return objectItem;
    }
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openBtn) {
        	if (getKrnObject() != null) {
        	    if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
        	        Or3Frame.instance().getDesignerFrame().load(getKrnObject().obj,null);
        	    }else {
        	        Or3Frame.instance().jumpFilter(getKrnObject().obj);
        	    }
        	}
        }
		else if (e.getSource() == treeBtn) {
			OpenElementPanel op = null;
			String title = "";
			if ("UI".equals(className)) {
				InterfaceTree tree = Utils.getInterfaceTree();
				op = new OpenElementPanel(tree);
				op.setSearchUIDPanel(true);
				title = "Выберите интерфейс";
			} else if ("ProcessDef".equals(className)) {
				ServicesTree tree = Utils.getServicesTree();
				op = new OpenElementPanel(tree);
				title = "Выберите процесс";
			} else if ("User".equals(className)) {
				UserTree tree = Utils.getUserTree();
				op = new OpenElementPanel(tree);
				title = "Выберите пользователя";
			} else if ("Box".equals(className)) {
				BoxTree tree = Utils.getBoxTree();
				op = new OpenElementPanel(tree);
				title = "Выберите пункт обмена";
			} else if ("ReportPrinter".equals(className)) {
				ReportTree tree = Utils.getReportTree(null);
				op = new OpenElementPanel(tree);
				title = "Выберите отчет";
			} else if ("Структура баз".equals(className)) {
				BaseTree tree = Utils.getBaseTree();
				op = new OpenElementPanel(tree);
				title = "Выберите структуру баз";
			} else if ("Filter".equals(className)) {
				FiltersTree tree = Utils.getFiltersTree();
				op = new OpenElementPanel(tree);
				op.setSearchUIDPanel(true);
				title = "Выберите фильтр";
			}
			if (op != null) {
				DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), title, op, true);
				KrnObjectItem oi = null;
				if ((value instanceof Vector && ((Vector) value).size() > 0 && (oi = (KrnObjectItem) ((Vector) value).get(0)) != null) || (value instanceof KrnObjectItem && (oi = (KrnObjectItem) value) != null)) {
					DesignerTree tree = op.getTree();
					TreeNode node = (oi.obj!=null)?op.searchByUID(oi.obj.uid, true):null;
					if (node != null) {
						tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode) node).getPath()));
						dlg.setOkEnabled(node.isLeaf());
                        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
                        TreeNode[] path = m.getPathToRoot(node);
                        TreePath tpath = new TreePath(path);
                        tree.setSelectionPath(tpath);
                        tree.scrollPathToVisible(tpath);
                        op.setSearchText(oi.obj.uid);
					} else {
						if(dlg != null)
							dlg.setOkEnabled(false);
					}
				} else {
					DesignerTree tree = op.getTree();
                    TreeNode node = tree.getSelectedNode();
                    if (node != null) {
                        tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode) node).getPath()));
                        dlg.setOkEnabled(node.isLeaf());
                        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
                        TreeNode[] path = m.getPathToRoot(node);
                        TreePath tpath = new TreePath(path);
                        tree.setSelectionPath(tpath);
                        tree.scrollPathToVisible(tpath);
                    } else {
                    	if (dlg != null)
                    		dlg.setOkEnabled(false);
                    }
				}
				dlg.show();
				if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
					AbstractDesignerTreeNode node = op.getTree().getSelectedNode();
					boolean parCheckFlr=true;
					int res = ButtonsFactory.BUTTON_YES;
					if (node != null){
						if(op.getTree() instanceof FiltersTree){
							parCheckFlr=((FiltersTree)op.getTree()).isExistsExprFlrs(op.getNodeObj(node));
						}
						value = new KrnObjectItem(op.getNodeObj(node),	node.toString());
					}else
						value = null;
					if(!parCheckFlr){
						String msg="Внимание! Данный фильтр не имеет параметров."
								+ "В дальнейшем это может стать причиной зависания системы."
								+ "Рекомендуется создать в фильтре уточняющий параметр."
								+ "\nПродолжить?";
				    	res = MessagesFactory.showMessageDialog(getTopLevelAncestor(),
				    			MessagesFactory.QUESTION_MESSAGE,msg);
					}
					if(res == ButtonsFactory.BUTTON_YES)
						editor.stopCellEditing();
					else
						editor.cancelCellEditing();
				} else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
					value = null;
					editor.stopCellEditing();
				} else
					editor.cancelCellEditing();
			}

		}
	}
}
