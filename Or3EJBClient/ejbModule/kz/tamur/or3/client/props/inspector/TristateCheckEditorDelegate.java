package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTable;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.checkBox.OrTristateCheckBox;

/**
 * The Class TristateCheckEditorDelegate.
 * 
 * @author Sergey Lebedev
 */
public class TristateCheckEditorDelegate extends OrTristateCheckBox implements EditorDelegate, RendererDelegate, ItemListener {

    /** The editor. */
    private PropertyEditor editor;
    
    /**
     * Создание нового tristate check editor delegate.
     * 
     * @param table
     *            the table
     */
    public TristateCheckEditorDelegate(JTable table) {
        super();
        this.setOpaque(false);
        this.setFont(table.getFont());
        addItemListener(this);
        // Отключить анимацию компонента
        setAnimate(false);
    }

    public int getClickCountToStart() {
        return 1;
    }

    public Component getEditorComponent() {
        return this;
    }

    public Component getRendererComponent() {
        return this;
    }

    public Object getValue() {
        return getState();
    }

    public void setValue(Object value) {
        if (value instanceof Boolean) {
            setState((Boolean) value ? Constants.SELECTED : Constants.NOT_SELECTED);
        } else if (value instanceof Integer) {
            setState((Integer) value);
        } else {
            setSelected(false);
        }
        repaint();
    }

    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void itemStateChanged(ItemEvent e) {
        if (editor != null) {
            editor.stopCellEditing();
        }
    }
}
