package kz.tamur.or3.client.props.inspector;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import kz.tamur.or3.client.props.Property;

public class PropertyRenderer extends DefaultTableCellRenderer {

    private RendererDelegate delegate;
    private Border cellBorder;
    private JTable table;
    private boolean plainMode = false;

    private static Color secondColor = kz.tamur.rt.Utils.getLightSysColor();

    public PropertyRenderer(JTable table) {
        this.table = table;
        cellBorder = new CellBorder(table.getGridColor());
    }

    public void setDelegate(RendererDelegate delegate) {
        this.delegate = delegate;
    }

    public void setPlainMode(boolean plainMode) {
        this.plainMode = plainMode;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component comp = null;

        if (delegate != null) {
            delegate.setValue(value);
            comp = delegate.getRendererComponent();
        } else {
            comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        if (isSelected) {
            if ( comp instanceof CheckEditorDelegate) {
                ((CheckEditorDelegate)comp).setOpaque(true);
            }
            comp.setBackground(table.getSelectionBackground());
        } else {
            if ( comp instanceof CheckEditorDelegate) {
                ((CheckEditorDelegate)comp).setOpaque(false);
            }
            comp.setBackground(table.getBackground());
            if (plainMode) {
                Property folder = ((Property) table.getValueAt(row, 0)).getParent();
                if (folder != null) {
                    Property root = folder.getParent();
                    if (root != null) {
                        int i = root.getChildren().indexOf(folder);
                        if (i != -1) {
                            if ((i % 2) == 0) {
                                comp.setBackground(secondColor);
                            }
                        }
                    }
                }
            }
        }

        ((JComponent) comp).setBorder(cellBorder);
        return comp;
    }

    public JTable getTable() {
        return table;
    }
}
