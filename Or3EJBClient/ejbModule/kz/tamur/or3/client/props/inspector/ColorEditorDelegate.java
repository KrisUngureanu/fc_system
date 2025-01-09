package kz.tamur.or3.client.props.inspector;

import com.cifs.or2.client.util.CnrBuilder;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.util.colorchooser.OrColorChooser;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

    private Color value;
    private PropertyEditor editor;

    private JTextField label;
    private JButton colorBtn;
    private JPanel colorView = new JPanel();

    public ColorEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this, table.getFont());
        colorBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        kz.tamur.rt.Utils.setAllSize(colorView, Constants.BTN_EDITOR_SIZE);
        add(colorView, new CnrBuilder().x(2).wtx(0).fill(GridBagConstraints.NONE).build());
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(colorBtn, new CnrBuilder().x(0).build());
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
        if (value != null && !value.toString().isEmpty()) {
        	Color expr = (Color) value;
        	this.value = expr;
        	colorView.setVisible(true);
        	colorView.setBackground(expr);
        	label.setText(String.valueOf(expr.getRGB()));
        } else {
            colorView.setVisible(false);
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
        if (e.getSource() == colorBtn) {
            OrColorChooser cch = new OrColorChooser(value);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выбор цвета", cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                value = cch.getColor();
                editor.stopCellEditing();
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                value = null;
                editor.stopCellEditing();
            } else {
                editor.cancelCellEditing();
            }
        } else if (e.getSource() == label) {
            value = (label.getText() == null || "".equals(label.getText())) ? null : Color.decode(label.getText());
            editor.stopCellEditing();
        }
    }

}
