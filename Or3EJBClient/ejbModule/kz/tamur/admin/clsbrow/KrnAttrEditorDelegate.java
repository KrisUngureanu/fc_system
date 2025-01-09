package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

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
public class KrnAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {

    private Object value;
    private ObjectPropertyEditor editor;
    long langId = DesignerFrame.instance().getInterfaceLang().id;

    private JTextField label;
    private JButton krnBtn;
    private JButton lnkBtn;
    private JButton delValBtn;
    private KrnAttribute attr;
    private JTable table;

    public KrnAttrEditorDelegate(JTable table, KrnAttribute attr) {
        this.table=table;
        this.attr=attr;
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(table.getFont());
        label.setEditable(false);

        krnBtn = kz.tamur.comps.Utils.createBtnEditor(this);

        add(krnBtn, new CnrBuilder().x(0).build());
		if (attr.collectionType == 0) {
        	delValBtn = kz.tamur.comps.Utils.createBtn(this, "DelRCSmall");
        	add(delValBtn, new CnrBuilder().x(1).build());
			lnkBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
			add(lnkBtn, new CnrBuilder().x(2).build());
		}
        add(label, new CnrBuilder().x(3).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
    }

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
        this.value = value;
        if (value == null) {
			label.setText(null);
        } else {
			if (value instanceof KrnObject[]) {
				String text = "";
				for (int i = 0; i < ((KrnObject[]) value).length; i++) {
					text += (i > 0 ? "," : "") + ((KrnObject[]) value)[i].id;
				}
				label.setText(text);
			} else if (value instanceof KrnObject) {
				label.setText("" + ((KrnObject) value).id);
				if (lnkBtn != null)
					lnkBtn.setEnabled(true);
			} else {
				if (lnkBtn != null)
					lnkBtn.setEnabled(false);
				label.setText("");
			}
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
            if (e.getSource() == lnkBtn) {
                if (value instanceof KrnObject){
                    editor.getObject().getObjectInspector().setObjectHistory((KrnObject) value, attr, 0);
                }
            } else if (e.getSource() == krnBtn) {
                if(attr.collectionType==0){
                    KrnClass cls = Kernel.instance().getClass(attr.typeClassId);
                    ObjectBrowser ob = new ObjectBrowser(cls, false);
                    if(value!=null && value instanceof KrnObject){
                        ob.setSelectedObject((KrnObject) value, null);
                    }
                    Container cont = getTopLevelAncestor();
                    DesignerDialog dlg;
                    if (cont instanceof Dialog) {
                        dlg = new DesignerDialog((Dialog)cont, "Выбор объекта", ob);
                    } else {
                        dlg = new DesignerDialog((Frame)cont, "Выбор объекта", ob);
                    }
                    dlg.show();
                    if (dlg.isOK()) {
                        value = ob.getSelectedObject();
                        editor.stopCellEditing();
                    }
				} else {
                    int row=table.getSelectedRow();
                    ObjectInspectable ins= editor.getObject();
                    String title=table.getValueAt(row,1).toString()+":"+table.getValueAt(row,2).toString();
                    ArrayPropertyField apf;
                    Object vals=ins.getObjectArray().get(attr);
                    if(vals instanceof Vector && ((Vector)vals).size()>0){
                        apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,(Vector)vals);
                    }else{
                        apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,(KrnObject[])value);
                    }
                    Container cont = getTopLevelAncestor();
                    DesignerDialog dlg;
                    if (cont instanceof Dialog) {
                        dlg = new DesignerDialog((Dialog)cont, title, apf);
                    } else {
                        dlg = new DesignerDialog((Frame)cont, title, apf);
                    }
                    apf.setOwner(dlg);
                    dlg.show();
                    if (dlg.isOK()) {
                        KrnObject linkValue=apf.getLinkValue();
                        int li=apf.getLinkIndex();
                        if(linkValue!=null){
                            ins.getObjectInspector().setObjectHistory(linkValue,attr,li);
                        }
                        Vector data=apf.getList();
                        KrnObject[] value_=new KrnObject[data.size()];
                        Vector<ObjectPropertyField> opfs=new Vector<ObjectPropertyField>();
                        boolean isModified=false;
                        for(int i=0;i<value_.length;i++){
                            ObjectPropertyField opf=(ObjectPropertyField)((Vector)data.get(i)).get(0);
                            opfs.add(opf);
                            if(!isModified && opf.isModified())
                                isModified=true;
                            value_[i]=((ObjectPropertyField)((Vector)data.get(i)).get(0)).getValue();
                        }
                        Vector delList=apf.getDelList();
                        if(delList.size()>0){
                            isModified=true;
                            opfs.addAll(delList);
                        }
                        if(isModified){
                            ins.setObjectArray(attr,opfs);
                            value=value_;
                        }
                        editor.stopCellEditing();
                    }
                }
            } else if (e.getSource() == delValBtn) {
                ObjectInspectable ins = editor.getObject();
            	ObjectProperty objProp = ((ObjectProperty) ((ObjectPropertyTableModel) table.getModel()).getRoot()).getChild(String.valueOf(attr.id));
                if (ins.getValue(objProp) != null) {
	                ins.setValue(objProp, null);
	                setValue(null);
                    editor.stopCellEditing();
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }
}