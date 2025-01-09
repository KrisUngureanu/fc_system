package kz.tamur.admin.clsbrow;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import kz.tamur.or3.client.props.inspector.CellBorder;

public class ObjectPropertyRenderer extends DefaultTableCellRenderer {

    private ObjectRendererDelegate delegate;
    private Border cellBorder;
    private JTable table;

    public ObjectPropertyRenderer(JTable table) {
        this.table = table;
        cellBorder = new CellBorder(table.getGridColor());
    }

    public void setDelegate(ObjectRendererDelegate delegate) {
        this.delegate = delegate;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component comp = null;

        if (delegate != null) {
            delegate.setValue(value);
            comp = delegate.getObjectRendererComponent();
        } else {
            comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        comp.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        ((JComponent) comp).setBorder(cellBorder);
        return comp;
    }

    public JTable getTable() {
        return table;
    }
}
