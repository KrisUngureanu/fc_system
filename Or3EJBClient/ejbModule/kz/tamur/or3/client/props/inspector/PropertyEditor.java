package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

import kz.tamur.or3.client.props.Inspectable;

public class PropertyEditor extends AbstractCellEditor implements TableCellEditor {

    private Border cellBorder;
    private EditorDelegate delegate;
    private PropertyTable table;

    public PropertyEditor(PropertyTable table) {
        this.table = table;
        cellBorder = new CellBorder(table.getGridColor());
    }

    public void setDelegate(EditorDelegate delegate) {
        this.delegate = delegate;
    }

    public EditorDelegate getDelegate() {
        return delegate;
    }

     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (delegate != null) {
            delegate.setPropertyEditor(this);
            delegate.setValue(value);
            Component comp = delegate.getEditorComponent();
            ((JComponent) comp).setBorder(cellBorder);
            return comp;
        }

        return null;
    }

    public Object getCellEditorValue() {
        return delegate.getValue();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (delegate != null) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= delegate.getClickCountToStart();
            }
            return true;
        }
        return false;
    }

    public JTable getTable() {
        return table;
    }

    public Inspectable getObject() {
        return table.getObject();
    }
}
