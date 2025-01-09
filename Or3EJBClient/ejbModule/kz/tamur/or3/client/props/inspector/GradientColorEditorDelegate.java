package kz.tamur.or3.client.props.inspector;

import com.cifs.or2.client.util.CnrBuilder;
import kz.tamur.Or3Frame;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.util.Funcs;
import kz.tamur.util.colorchooser.OrGradientColorChooser;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Class GradientColorEditorDelegate.
 * 
 * @author Sergey Lebedev
 */
public class GradientColorEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

    /** The value. */
    private Object value = null;

    /** The editor. */
    private PropertyEditor editor;

    /** The label. */
    private JTextField label;

    /** The color btn. */
    private JButton colorBtn;

    /** The color btnvalue. */
    private Color colorBtnvalue;

    /**
     * Создание нового gradient color editor delegate.
     * 
     * @param table
     *            the table
     */
    public GradientColorEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this,table.getFont());
        colorBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        colorBtnvalue = colorBtn.getBackground();
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
        if (value != null && value instanceof GradientColor) {
            GradientColor val = (GradientColor) value;
            this.value = val;
            if (val.getStartColor() != null && val.getEndColor() != null) {
                label.setText(val.toString());
                return;
            }
        }
        this.value = null;
        label.setText("");
        colorBtn.setBackground(colorBtnvalue);

    }

    
    public Component getRendererComponent() {
        return this;
    }

    
    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;

    }

    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == colorBtn) {
            OrGradientColorChooser cch;
            if (value == null) {
                cch = new OrGradientColorChooser();
            } else {
                cch = new OrGradientColorChooser((GradientColor) value);
            }
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выбор цвета", cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                value = cch.getGradient();
                editor.stopCellEditing();
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                value = null;
                editor.stopCellEditing();
            } else {
                editor.cancelCellEditing();
            }
        } else if (e.getSource() == label) {
            if (label.getText() != null && !label.getText().isEmpty()) {
                value = new GradientColor(Funcs.normalizeInput(label.getText()));
            } else {
                value = null;
            }
            editor.stopCellEditing();
        }
    }

}
