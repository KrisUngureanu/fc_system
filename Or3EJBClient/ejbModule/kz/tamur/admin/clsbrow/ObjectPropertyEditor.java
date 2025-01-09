package kz.tamur.admin.clsbrow;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

import kz.tamur.or3.client.props.inspector.CellBorder;

public class ObjectPropertyEditor extends AbstractCellEditor implements TableCellEditor {

    private Border cellBorder;
    private ObjectEditorDelegate delegate;
    private ObjectPropertyTable table;

    public ObjectPropertyEditor(ObjectPropertyTable table) {
        this.table = table;
        cellBorder = new CellBorder(table.getGridColor());
    }

    public void setDelegate(ObjectEditorDelegate delegate) {
        this.delegate = delegate;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (delegate != null) {
            delegate.setObjectPropertyEditor(this);
            delegate.setValue(value);
            Component comp = delegate.getObjectEditorComponent();
            ((JComponent) comp).setBorder(cellBorder);
            return comp;
        }

        return null;
    }

    public Object getCellEditorValue() {
        return delegate.getValue();
    }

    public boolean isCellEditable(EventObject e) {
        return delegate == null ? false : !(e instanceof MouseEvent) || ((MouseEvent) e).getClickCount() >= delegate.getClickCountToStart();
    }

    public JTable getTable() {
        return table;
    }

    public ObjectInspectable getObject() {
        return table.getObject();
    }

}
