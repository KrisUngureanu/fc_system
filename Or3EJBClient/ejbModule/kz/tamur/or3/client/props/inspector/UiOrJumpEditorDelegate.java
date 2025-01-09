package kz.tamur.or3.client.props.inspector;

import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceNode;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.DesignerTree;
import kz.tamur.Or3Frame;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


public class UiOrJumpEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Object value;

    private PropertyEditor editor;

    private JTextField label;
	private JButton jumpBtn;
	private JButton uiBtn;

    public UiOrJumpEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this,table.getFont());
        jumpBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        uiBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(jumpBtn, new CnrBuilder().x(0).build());
        add(uiBtn, new CnrBuilder().x(1).build());
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
        }else if(value instanceof KrnObjectItem){
            label.setText(value.toString());
        }else{
            label.setText("");
        }
    }

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uiBtn) {
            OpenElementPanel op = new OpenElementPanel(Utils.getInterfaceTree());
            op.setSearchUIDPanel(true);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите интерфейс", op,true);
            KrnObjectItem oi=null;
            if((value instanceof Vector &&((Vector)value).size()>0 && (oi=(KrnObjectItem)((Vector)value).get(0))!=null)
                    ||(value instanceof KrnObjectItem && (oi = (KrnObjectItem) value) != null)){
                DesignerTree tree=op.getTree();
                if(oi.obj!=null){
                    TreeNode node=op.searchByUID(oi.obj.uid, true);
                    if(node!=null)
                        tree.setSelectionPath(new TreePath(((AbstractDesignerTreeNode)node).getPath()));
                }
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
            }else
                editor.cancelCellEditing();
        }else if (e.getSource() == jumpBtn ) {
            KrnObjectItem oi=null;
            if((value instanceof Vector &&((Vector)value).size()>0 && (oi=(KrnObjectItem)((Vector)value).get(0))!=null)
                    ||(value instanceof KrnObjectItem && (oi = (KrnObjectItem) value) != null)){
                    Or3Frame.instance().jumpInterface(oi.obj);
            } else {
                InterfaceNode node = Or3Frame.instance().createInterface();
                if(node!=null){
                    value = new KrnObjectItem(node.getKrnObj(), node.toString());
                    Or3Frame.instance().jumpInterface((KrnObject)value);
                }
            }
            editor.stopCellEditing();
        }
    }
}
