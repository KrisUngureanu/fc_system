package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.Dimension;
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
import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.guidesigner.service.ServiceItem;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.or3.client.props.ComboPropertyItem;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.OpenElementPanel;

import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnClass;

public class TreeOrExprEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener, EditorDelegateSet {

    private Object value;
    private String stringValue = null;
    public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	private PropertyEditor editor;
    private String className;

    private JLabel label;
    private JButton treeBtn;
    private JButton exprBtn;
    private JButton openBtn;

    public TreeOrExprEditorDelegate(JTable table, String className) {
        this.className = className;
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        openBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        treeBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        exprBtn = kz.tamur.comps.Utils.createBtnOpenIfc(this);
        if (className.equals("UI") || className.equals("ProcessDef")) {
        	add(openBtn, new CnrBuilder().x(0).build());
        }
        add(treeBtn, new CnrBuilder().x((className.equals("UI") || className.equals("ProcessDef")) ? 1 : 0).build());
        add(exprBtn, new CnrBuilder().x((className.equals("UI") || className.equals("ProcessDef")) ? 2 : 1).build());
        add(label, new CnrBuilder().x((className.equals("UI") || className.equals("ProcessDef")) ? 3 : 2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
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
    
    private void setToolTipsText() {
    	openBtn.setToolTipText(openBtn.isEnabled() ? "Открыть интерфейс" : "");
    	treeBtn.setToolTipText(treeBtn.isEnabled() ? "Открыть дерево элементов" : "");
    	exprBtn.setToolTipText(exprBtn.isEnabled() ? "Редактировать" : "");
    }

    public void setValue(Object value) {
    	if (value instanceof ExprEditorObject)
    		value = ((ExprEditorObject)value).getObject();
        this.value = value;
        if (value instanceof Vector && ((Vector) value).size() > 0) {
            String content = "";
            for (int i = 0; i < ((Vector) value).size(); i++) {
            	content += (i > 0 ? "," : "") + ((KrnObjectItem) ((Vector) value).get(i)).title;
            }
            label.setText(content);
            openBtn.setEnabled(getKrnObject() == null ? false : true);
            treeBtn.setEnabled(true);
            exprBtn.setEnabled(false);
        } else if (value instanceof Expression) {
            label.setText(((Expression) value).text);
            openBtn.setEnabled("".equals(((Expression) value).text)  && (getKrnObject() == null ? false : true));
            treeBtn.setEnabled("".equals(((Expression) value).text));
            exprBtn.setEnabled(true);
        } else if (value instanceof KrnObjectItem) {
            label.setText(value.toString());
            openBtn.setEnabled(getKrnObject() == null ? false : true);
            treeBtn.setEnabled(true);
            exprBtn.setEnabled(false);
        } else {
            openBtn.setEnabled(getKrnObject() == null ? false : true);
            treeBtn.setEnabled(true);
            exprBtn.setEnabled(true);
        }
        setToolTipsText();
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
        		KrnClass UI_cls;  
        		if (className.equals("UI")) {
	        	    if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
	        	        Or3Frame.instance().getDesignerFrame().load(getKrnObject().obj,null);
	        	    }else {
	        	        Or3Frame.instance().jumpInterface(getKrnObject().obj);
	        	    }
        		}else if (className.equals("ProcessDef")) {
	        	    if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
	        	        Or3Frame.instance().getDesignerFrame().load(getKrnObject().obj,null);
	        	    }else {
	        	        Or3Frame.instance().jumpService(getKrnObject().obj);
	        	    }
        		}
        	}
        } else if (e.getSource() == treeBtn) {
            OpenElementPanel op = null;
            String title = "";
            Inspectable ins = editor.getObject();
            boolean isRpt = false;
            if (ins instanceof ServiceItem) {
                Property prop = (Property) editor.getTable().getValueAt(editor.getTable().getSelectedRow(), 0);
                if (prop.getId().equals(NodePropertyConstants.UI_PROCESS.getName())) {
                    Object prop_ = ins.getValue(ins.getProperties().getChild("Свойства")
                            .getChild(NodePropertyConstants.UI_TYPE.getName()));
                    if (prop != null
                            && (((ComboPropertyItem) prop_).id.equals(Constants.ACT_ARTICLE_STRING) || ((ComboPropertyItem) prop_).id
                                    .equals(Constants.ACT_FASTREPORT_STRING)))
                        isRpt = true;
                }

            }
            if ("UI".equals(className) && !isRpt) {
                InterfaceTree tree = Utils.getInterfaceTree();
                op = new OpenElementPanel(tree);
                op.setSearchUIDPanel(true);
                title = "Выберите интерфейс";
            } else if ("ProcessDef".equals(className)) {
                ServicesTree tree = Utils.getServicesTree();
                op = new OpenElementPanel(tree);
                op.setSearchUIDPanel(true);
                title = "Выберите процесс";
            } else if ("User".equals(className)) {
                UserTree tree = Utils.getUserTree();
                op = new OpenElementPanel(tree, false, Constants.NEED_CHECK_FOLDER, true);
                title = "Выберите пользователя";
            } else if ("BoxExchange".equals(className)) {
                BoxTree tree = Utils.getBoxTree();
                op = new OpenElementPanel(tree);
                title = "Выберите пункт обмена";
            } else if ("ReportPrinter".equals(className) || isRpt) {
                ReportTree tree = Utils.getReportTree(null);
                op = new OpenElementPanel(tree);
                title = "Выберите отчет";
            } else if ("Структура баз".equals(className)) {
                BaseTree tree = Utils.getBaseTree();
                op = new OpenElementPanel(tree);
                title = "Выберите структуру баз";
            } else  if ("Filter".equals(className)) {
                FiltersTree tree = Utils.getFiltersTree();
                op = new OpenElementPanel(tree);
                op.setSearchUIDPanel(true);
                title = "Выберите фильтр";
            }
            if (op != null) {
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), title, op, true);
                KrnObjectItem oi = null;
                if ((value instanceof Vector && ((Vector) value).size() > 0 && (oi = (KrnObjectItem) ((Vector) value).get(0)) != null)
                        || (value instanceof KrnObjectItem && (oi = (KrnObjectItem) value) != null)) {
                    DesignerTree tree = op.getTree();
                    TreeNode node = op.searchByUID(oi.obj.uid, true);
                    if (node != null) {
                        tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode) node).getPath()));
                        if ("User".equals(className))
                        dlg.setOkEnabled(true);
                        else 
                        	dlg.setOkEnabled(node.isLeaf());
                        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
                        TreeNode[] path = m.getPathToRoot(node);
                        TreePath tpath = new TreePath(path);
                        tree.setSelectionPath(tpath);
                        tree.scrollPathToVisible(tpath);
                        op.setSearchText(oi.obj.uid);
                    } else {
                    	if (dlg != null)
                    		dlg.setOkEnabled(false);
                    }
                } else {
                	DesignerTree tree = op.getTree();
                    TreeNode node = tree.getSelectedNode();
                    if (node != null) {
                        tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode) node).getPath()));
                        if ("User".equals(className))
                            dlg.setOkEnabled(true);
                            else 
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
                    if (node != null)
                        value = new KrnObjectItem(op.getNodeObj(node), node.toString());
                    else
                        value = null;
                    editor.stopCellEditing();
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    editor.stopCellEditing();
                } else
                    editor.cancelCellEditing();
            }

        } else if (e.getSource() == exprBtn) {
            String value_ = "";
            if (value instanceof Expression) {
                value_ = ((Expression) value).text;
                stringValue = value_;
            } else if (value != null) {
                value_ = value.toString();
            }
            ExpressionEditor exprEditor = new ExpressionEditor(value_, TreeOrExprEditorDelegate.this);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
            dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.show();
            if (dlg.isOK()) {
            	setExpression(exprEditor.getExpression());
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                editor.cancelCellEditing();
            }
        }
    }
    
    public void setExpression(String expression) {
   	 	value = new Expression(expression);
        editor.stopCellEditing();
   }
}
