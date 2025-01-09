package kz.tamur.or3.client.props.inspector;

import kz.tamur.util.OrFontChooser;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.Or3Frame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

public class FontEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Font value;
    private PropertyEditor editor;

	private JLabel label;
	private JButton fontBtn;

    public FontEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        fontBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(fontBtn, new CnrBuilder().x(0).build());
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
            Font font = (Font)value;
            this.value = font;
			label.setText(font.getPSName()+"-"+font.getSize());
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
        if (e.getSource() == fontBtn) {
            OrFontChooser fc =
                    new OrFontChooser(value);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выбор шрифта", fc, false, true);
            dlg.show();
            if (dlg.isOK()) {
                value = fc.getChooserFont();
                editor.stopCellEditing();
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                value = null;
                editor.stopCellEditing();
            }else{
                editor.cancelCellEditing();
            }
        }
    }

}
