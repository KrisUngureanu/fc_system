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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 01.06.2009
 * Time: 16:41:14
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {

    private Object value;
    private ObjectPropertyEditor editor;
    long langId = DesignerFrame.instance().getInterfaceLang().id;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private JTextField label;
    private JButton arrayBtn;
    private KrnAttribute attr;
    private JTable table;

    public DateTimeAttrEditorDelegate(JTable table, KrnAttribute attr) {
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
        try {
            if(label.getText().equals("")){
                value=null;
            }else{
                if(attr.collectionType==0 && attr.typeClassId == Kernel.IC_TIME)
                    return kz.tamur.util.Funcs.convertTime(tf.parse(label.getText()));
                else if(attr.collectionType==0 && attr.typeClassId == Kernel.IC_DATE)
                    return kz.tamur.util.Funcs.convertDate(df.parse(label.getText()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
            return value;
	}

	public void setValue(Object value) {
		this.value = value;
		String text = "";
		if (value instanceof Time[]) {
			Time[] times = (Time[])value;
			if (times.length > 0) {
				KrnDate d = kz.tamur.util.Funcs.convertTime(times[0]);
				StringBuilder label_ = new StringBuilder(d != null ? tf.format(d) : "<null>");
				for (Time time : times) {
					d = kz.tamur.util.Funcs.convertTime(time);
					label_.append(';').append(d != null ? tf.format(d) : "<null>");
				}
				text = label_.toString();
			}
		} else if (value instanceof Time) {
			KrnDate d = kz.tamur.util.Funcs.convertTime((Time)value);
			if (d != null)
				text = tf.format(d);
		} else if (value instanceof Date[]) {
			Date[] dates = (Date[]) value;
			if (dates.length > 0) {
				KrnDate d = kz.tamur.util.Funcs.convertDate(dates[0]);
				StringBuilder label_ = new StringBuilder(d != null ? df.format(d) : "<null>");
				for (Date date : dates) {
					d = kz.tamur.util.Funcs.convertDate(date);
					label_.append(';').append(d != null ? df.format(d) : "<null>");
				}
				text = label_.toString();
			}
		} else if (value instanceof Date) {
			KrnDate d = kz.tamur.util.Funcs.convertDate((Date)value);
			if (d != null)
				text = df.format(d);
		}
		label.setText(text);
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
                if(attr.typeClassId==Kernel.IC_DATE){
                apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new DateValue[0]:(DateValue[])value);
                }else if(attr.typeClassId==Kernel.IC_TIME){
                    apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new TimeValue[0]:(TimeValue[])value);
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
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }
}