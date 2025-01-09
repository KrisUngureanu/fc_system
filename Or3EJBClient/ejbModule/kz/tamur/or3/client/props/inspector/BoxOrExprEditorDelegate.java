package kz.tamur.or3.client.props.inspector;


import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.util.KrnObjectItem;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.boxes.BoxTree;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.DesignerTree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class BoxOrExprEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener, EditorDelegateSet {

	private Object value;
	private String stringValue = null;

    public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	private PropertyEditor editor;

    private JLabel label;
	private JButton exprBtn;
	private JButton boxBtn;

    public BoxOrExprEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        boxBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(boxBtn, new CnrBuilder().x(0).build());
        add(exprBtn, new CnrBuilder().x(1).build());
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
		if (value instanceof ExprEditorObject)
    		value = ((ExprEditorObject)value).getObject();
        this.value = value;
        if (value instanceof Expression ) {
            label.setText(((Expression) value).text);
            boxBtn.setEnabled("".equals(((Expression) value).text));
            exprBtn.setEnabled(true);
        } else if (value instanceof KrnObjectItem) {
            label.setText(value.toString());
            boxBtn.setEnabled(true);
            exprBtn.setEnabled(false);
        } else {
            boxBtn.setEnabled(true);
            exprBtn.setEnabled(true);
        }
    }

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == boxBtn) {
            BoxTree box_tree= Utils.getBoxTree();
            OpenElementPanel op = new OpenElementPanel(box_tree);
            DesignerDialog dlg = new DesignerDialog((Frame) boxBtn.
                    getTopLevelAncestor(), "Выберите пункт обмена", op,true);
            KrnObjectItem oi=null;
            if(value instanceof KrnObjectItem && (oi = (KrnObjectItem) value) != null){
                DesignerTree tree=op.getTree();
                TreeNode node=op.searchByUID(oi.obj.uid, true);
                if(node!=null)
                    tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode)node).getPath()));
            }
            dlg.show();
            if(dlg.getResult()== ButtonsFactory.BUTTON_OK){
                AbstractDesignerTreeNode node = op.getTree().getSelectedNode();
                if(node!=null)
                    value = new KrnObjectItem(op.getNodeObj(node), node.toString());
                else
                    value=null;
                editor.stopCellEditing();
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                value = null;
                editor.stopCellEditing();
            } else
                editor.cancelCellEditing();
        }else if (e.getSource() == exprBtn) {
            String value_="";
            if(value instanceof Expression){
               value_=((Expression)value).text;
               stringValue = value_;
            }else if(value!=null){
                value_=value.toString();
            }
            ExpressionEditor exprEditor = new ExpressionEditor(value_, BoxOrExprEditorDelegate.this);
            DesignerDialog dlg = new DesignerDialog((Frame) exprBtn.getTopLevelAncestor(), "Выражение", exprEditor);
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
