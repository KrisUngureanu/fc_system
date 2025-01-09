package kz.tamur.admin.clsbrow;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 01.06.2009
 * Time: 16:41:14
 * To change this template use File | Settings | File Templates.
 */
public class StringAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {
    private Object value;
    private JButton arrayBtn;
    private JButton langBtn;
    private JButton delValBtn;
    private KrnAttribute attr;
    private JPopupMenu pm;
    private JTextField label;
    private JTable table;
    private ObjectPropertyEditor editor;

    public StringAttrEditorDelegate(JTable table,KrnAttribute attr) {
        this.table=table;
        this.attr=attr;
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(table.getFont());
        if(attr.collectionType>0){
            arrayBtn = kz.tamur.comps.Utils.createBtnEditor(this);
            add(arrayBtn, new CnrBuilder().x(0).build());
            label.setEditable(false);
        }else if(attr.isMultilingual){
            langBtn = kz.tamur.comps.Utils.createBtnEditor(this);
            pm = new JPopupMenu();
            pm.setFont(Utils.getDefaultFont());
            pm.setBackground(Utils.getLightSysColor());
            java.util.List langItems = LangItem.getAll();
            for (Object langItem : langItems) {
                LangMenuItem mi = new LangMenuItem((LangItem) langItem);
                mi.addActionListener(this);
                if ("RU".equals(mi.getLangItem().code) ||
                        "KZ".equals(mi.getLangItem().code) ||
                        "EN".equals(mi.getLangItem().code)) {
                    pm.add(mi);
                }
            }
            add(langBtn, new CnrBuilder().x(0).build());
            label.setEditable(false);
        }
        if (attr.collectionType == 0 && !attr.isMultilingual) {
        	delValBtn = kz.tamur.comps.Utils.createBtn(this, "DelRCSmall");
        	add(delValBtn);
        }
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
    }
    
	public Object getValue() {
		if (attr.collectionType == 0)
			return label.getText();
		else
			return value;
	}

    public void setValue(Object value) {
        this.value=value;
        if (value == null) {
			label.setText(null);
        } else {
			if (value instanceof String) {
				label.setText("");
		        try {
		        	label.getDocument().insertString(0, value.toString(), null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else if (value instanceof String[]) {
				String text = "";
				for (int i = 0; i < ((String[]) value).length; i++) {
					text += (i > 0 ? ";" : "") + ((String[]) value)[i];
				}
				label.setText("");
		        try {
		        	label.getDocument().insertString(0, text, null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
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
            if (e.getSource() == arrayBtn) {
                    int row=table.getSelectedRow();
                    String title=table.getValueAt(row,1).toString()+":"+table.getValueAt(row,2).toString();
                    ArrayPropertyField apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new String[0]:(String[])value);
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
                        Vector<PropertyField> tpfs=new Vector<PropertyField>();
                        boolean isModified=false;
                        String[] value_=new String[data.size()];
                        for(int i=0;i<data.size();i++){
                            PropertyField tpf=(PropertyField)((Vector)data.get(i)).get(0);
                            tpfs.add(tpf);
                            if(!isModified && tpf.isModified())
                                isModified=true;
                            if(attr.typeClassId == Kernel.IC_MEMO)
                                value_[i]=((MemoPropertyField)((Vector)data.get(i)).get(0)).getText();
                            else
                                value_[i]=((TextPropertyField)((Vector)data.get(i)).get(0)).getText();
                        }
                        Vector delList=apf.getDelList();
                        if(delList.size()>0){
                            isModified=true;
                            tpfs.addAll(delList);
                        }
                        if(isModified){
                            ins.setObjectArray(attr,tpfs);
                            value=value_;
                        }
                        editor.stopCellEditing();
                    }


            }else if (e.getSource() == langBtn) {
                ObjectInspectable ins= editor.getObject();
                if (attr.isMultilingual) {
                	Point p = this.getMousePosition();
                	pm.show(this, (int)p.getX(), (int)p.getY());
                }else{
                 String data = (String)value;
                  value=editLang(data);
                  if(!value.equals(data))
                         editor.stopCellEditing();
                }
            }else if(e.getSource() instanceof LangMenuItem){
                String data=(String)value;
                ObjectInspectable ins= editor.getObject();
                Map<Long,String> dataMap=ins.getLangData(attr.id);
                if(dataMap==null)
                    dataMap=new HashMap<Long,String>();
                try{
                    long langId= ((LangMenuItem)e.getSource()).getLangItem().obj.id;
                    String[] datas;
                    if(langId != ins.getLangId()){
                        if(!dataMap.containsKey(langId)){
                            datas= Kernel.instance().getStrings(ins.getKrnObject(),attr,langId,ObjectBrowser.transId);
                        }else{
                            datas= new String[]{dataMap.get(langId)};
                        }
                        String value_=editLang(datas.length>0?datas[0]:"");
                        if(value_!=null && (datas.length==0 || datas[0]==null || !datas[0].equals(value_)))
                            dataMap.put(langId,value_);
                    }else{
                        value=editLang(data);
                        if((value == null && data != null) || (value != null && !value.equals(data))){
                            dataMap.put(langId,(String)value);
                            setValue(value);
                        }
                    }
                }catch(KrnException ex){
                    ex.printStackTrace();
                }
                if(dataMap.size()>0)
                    ins.setLangData(attr.id,dataMap);
                if((value==null && data!=null) ||(value!=null && data!=null && !value.equals(data)) || dataMap.size()>0)
                    editor.stopCellEditing();
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
    
    private String editLang(String data){
    	Container cont = getTopLevelAncestor();
        String text = (data != null) ? data : "";
        JTextArea editor = new JTextArea(text);
        JScrollPane scroller = new JScrollPane(editor);
        scroller.setPreferredSize(new Dimension(600, 400));
        DesignerDialog dlg = new DesignerDialog(
        		cont instanceof Dialog?(Dialog)cont:(Frame)cont,
                "Редактирование свойства", scroller);
        dlg.show();
        String text_ = Funcs.normalizeInput(editor.getText());
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK && !text.equals(text_)) {
                return text_;
        }
    return data;
}
}