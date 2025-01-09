package kz.tamur.or3.client.props.inspector;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.PropertyEditor;
import kz.tamur.comps.MenuItemRecord;
import kz.tamur.util.MenuItemEditorPanel;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.Or3Frame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

public class MenuEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private MenuItemRecord[] value;
    private PropertyEditor editor;

	private JLabel label;
	private JButton exprBtn;

    public MenuEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
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
        if (value != null && !"".equals(value)) {
            MenuItemRecord[] expr = (MenuItemRecord[])value;
            this.value = expr;
            String txt="";
            for(MenuItemRecord mr:expr){
                txt+="".equals(txt)?"":";"+mr.getTitle();
            }
            label.setText(txt);
        } else {
            this.value = null;
            label.setText("");
        }
	}

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
		this.editor=editor;

	}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exprBtn) {
            MenuItemEditorPanel me = new MenuItemEditorPanel();
            MenuItemRecord[] val = value;
            if (val != null && val.length > 0) {
                me.setOldValues(val);
            }
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                    "Пункты всплывающего меню", me);
            me.setDividerLoc();
            dlg.show();
            int res = dlg.getResult();
            if (res != ButtonsFactory.BUTTON_NOACTION
                    && res == ButtonsFactory.BUTTON_OK) {
                    value = me.getMenuItems();
                    editor.stopCellEditing();
            }else
                editor.cancelCellEditing();
        }
    }
}
