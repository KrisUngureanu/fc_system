package kz.tamur.or3.client.props.inspector;

import kz.tamur.rt.Utils;
import kz.tamur.util.BorderEditor;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.Or3Frame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.cifs.or2.client.util.CnrBuilder;

public class BorderEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

    private Border value;
    private PropertyEditor editor;

    private JLabel label;
    private JButton borderBtn;

    public BorderEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        borderBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(borderBtn, new CnrBuilder().x(0).build());
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
            Border expr = (Border) value;
            this.value = expr;
            label.setText(Utils.getBorderToString(expr));
        } else {
            this.value = null;
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
        if (e.getSource() == borderBtn) {
            BorderEditor bch = new BorderEditor(value);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Бордюр", bch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                value = bch.getResultBorder();
                editor.stopCellEditing();
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                value = null;
                editor.stopCellEditing();
            } else {
                editor.cancelCellEditing();
            }
        }
    }

}
