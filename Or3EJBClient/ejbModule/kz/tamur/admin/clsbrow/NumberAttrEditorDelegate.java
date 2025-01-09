package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.*;

import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 01.06.2009
 * Time: 16:41:14
 * To change this template use File | Settings | File Templates.
 */
public class NumberAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {

    private Object value;
    private ObjectPropertyEditor editor;
    long langId = DesignerFrame.instance().getInterfaceLang().id;

    private JTextField label;
    private JButton arrayBtn;
    private KrnAttribute attr;
    private JTable table;

    public NumberAttrEditorDelegate(JTable table, KrnAttribute attr) {
        this.table=table;
        this.attr=attr;
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(table.getFont());
        if(attr.collectionType>0){
            arrayBtn = kz.tamur.comps.Utils.createBtnEditor(this);
            add(arrayBtn, new CnrBuilder().x(0).build());
            label.setEditable(false);
        }
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
    }

	public Object getValue() {
        if(attr.collectionType==0){
        if("".equals(label.getText()))
            value=null;
        else if(attr.typeClassId==Kernel.IC_INTEGER)
            value= Long.valueOf(label.getText());
        else if(attr.typeClassId==Kernel.IC_BOOL){
            value= Long.valueOf(label.getText());
        }else if(attr.typeClassId==Kernel.IC_FLOAT)
            value= Double.valueOf(label.getText());
        }

        return value;
	}

	public void setValue(Object value) {
        this.value = value;
        if(value instanceof long[]){
            String label_="";
            for(int i=0;i<((long[])value).length;i++){
                label_ += (i>0?";":"")+ ((long[])value)[i];
            }
            label.setText(label_);
        }else if(value instanceof Number){
            label.setText(""+value);
        }else if(value instanceof Boolean){
            label.setText(((Boolean)value)?"1":"0");
        }else if(value instanceof Double[]){
                String label_="";
                for(int i=0;i<((Double[])value).length;i++){
                    label_ += (i>0?";":"")+ ((Double[])value)[i];
                }
                label.setText(label_);
            }else if(value instanceof Double){
                label.setText(""+(Double)value);
        }else{
            label.setText("");
		}
	}

    public Component getObjectRendererComponent() {
        return this;
    }

    public Component getObjectEditorComponent() {
		return this;
	}

	public int getClickCountToStart() {
		return 1;
	}

	public void setObjectPropertyEditor(ObjectPropertyEditor editor) {
        this.editor=editor;
	}

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == arrayBtn) {
                int row=table.getSelectedRow();
                String title=table.getValueAt(row,1).toString()+":"+table.getValueAt(row,2).toString();
                ArrayPropertyField apf=null;
                if(attr.typeClassId==Kernel.IC_INTEGER){
                apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new long[0]:(long[])value);
                }else if(attr.typeClassId==Kernel.IC_FLOAT){
                    apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new double[0]:(double[])value);
                }
                Container cont = getTopLevelAncestor();
                DesignerDialog dlg;
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog)cont, title, apf);
                } else {
                    dlg = new DesignerDialog((Frame)cont, title, apf);
                }
                dlg.show();
                if (dlg.isOK()) {
                    ObjectInspectable ins= editor.getObject();
                    Vector data= apf.getList();
                    Vector<IntPropertyField> ipfs=new Vector<IntPropertyField>();
                    boolean isModified=false;
                    long[] value_=new long[data.size()];
                    for(int i=0;i<data.size();i++){
                        IntPropertyField tpf=(IntPropertyField)((Vector)data.get(i)).get(0);
                        ipfs.add(tpf);
                        if(!isModified && tpf.isModified())
                            isModified=true;
                        value_[i]=Long.valueOf(((IntPropertyField)((Vector)data.get(i)).get(0)).getText());
                    }
                    Vector delList=apf.getDelList();
                    if(delList.size()>0){
                        isModified=true;
                        ipfs.addAll(delList);
                    }
                    if(isModified){
                        ins.setObjectArray(attr,ipfs);
                        value=value_;
                    }
                    editor.stopCellEditing();
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }
}