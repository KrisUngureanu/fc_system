package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MemoAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {

    private Object value;
    private ObjectPropertyEditor editor;

    private JButton memoBtn;
    private KrnAttribute attr;
    private JPopupMenu pm;
    private String enc="UTF-8";
    private JTextField label;
    private JTable table;

    public MemoAttrEditorDelegate(JTable table, KrnAttribute attr) {
        this.table=table;
        this.attr=attr;
        setLayout(new GridBagLayout());

        label = kz.tamur.comps.Utils.createEditor(table.getFont());
        label.setEditable(false);

        memoBtn = kz.tamur.comps.Utils.createBtnEditor(this);
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


        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(memoBtn, new CnrBuilder().x(0).build());
    }

    public Object getValue() {
            return value;
    }

    public void setValue(Object value) {
        this.value=value;
        if(value instanceof String){
            label.setText(value.toString());
        }else if(value instanceof String[]){
            String label_="";
            for(int i=0;i<((String[])value).length;i++){
                label_ += (i>0?";":"")+ ((String[])value)[i];
            }
            label.setText(label_);
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
        if (e.getSource() == memoBtn) {
            ObjectInspectable ins= editor.getObject();
            if (attr.isMultilingual) {
                Point p = this.getMousePosition();
                pm.show(this, (int)p.getX(), (int)p.getY());
            }else {
                if(attr.collectionType>0){
                    try{
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
                        Vector data= apf.getList();
                        Vector<PropertyField> tpfs=new Vector<PropertyField>();
                        boolean isModified=false;
                        byte[][] value_=new byte[data.size()][];
                        for(int i=0;i<data.size();i++){
                            PropertyField tpf=(PropertyField)((Vector)data.get(i)).get(0);
                            tpfs.add(tpf);
                            if(!isModified && tpf.isModified())
                                isModified=true;
                                value_[i]=((BlobPropertyField)((Vector)data.get(i)).get(0)).getData();
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
                    } catch (KrnException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    String data = (String)value;
                    value=editMemo(data);
                    if (value != null) {
	                    if(!value.equals(data))
	                        editor.stopCellEditing();
                    }
                }
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
                    String value_=editMemo(datas.length>0?datas[0]:"");
                    if(((datas.length==0 || datas[0]==null) 
                    		&& value_!=null) 
                    		||((datas.length!=0 && value_!=null) && !datas[0].equals(value_)))
                        dataMap.put(langId,value_);
                }else{
                    value=editMemo(data);
                    if((value==null && data!=null) ||(!value.equals(data)))
                        dataMap.put(langId,(String)value);
                }
            }catch(KrnException ex){
                ex.printStackTrace();
            }
            if(dataMap.size()>0)
                ins.setLangData(attr.id,dataMap);
            if((value==null && data!=null) ||(!value.equals(data)) || dataMap.size()>0)
                editor.stopCellEditing();
        }
    }

    private String editMemo(String data){
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