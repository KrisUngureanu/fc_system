package kz.tamur.util;

import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.TableAdapter;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: KazakBala
 * Date: 11.03.2005
 * Time: 10:42:19
 * To change this template use File | Settings | File Templates.
 */
public class ZebraCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    protected Color zebra1 = Color.WHITE;
    protected Color zebra2 = Color.WHITE;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        final JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
            // непрозрачность для текста
            JLabel newComp = new JLabel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    // значение параметра прозрачности для текста
                    g.setFont(comp.getFont());
                    g.drawString(comp.getText(), 2, 10);

                }
            };
            newComp.setIcon(comp.getIcon());
            newComp.setFont(comp.getFont());
            newComp.setBackground(comp.getBackground());
            newComp.setForeground(comp.getForeground());
            newComp.setOpaque(true);
            newComp.setAlignmentX(comp.getAlignmentX());
            newComp.setAlignmentY(comp.getAlignmentY());
            if (value instanceof String) {
                newComp.setToolTipText((String) value);
            }
            changeComponent(table, value, isSelected, hasFocus, row, column, newComp);
            return newComp;
        }
        changeComponent(table, value, isSelected, hasFocus, row, column, comp);
        if (value instanceof String) {
            comp.setToolTipText((String) value);
        }
        return comp;
    }

    public void changeComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column,
            Component comp) {
        TableAdapter.RtTableModel model = (TableAdapter.RtTableModel) table.getModel();

        comp.setBackground(row % 2 == 0 ? zebra1 : zebra2);
        if (hasFocus) {
            comp.setBackground(Color.white);
        }
        if (model.isRowBackColorCalc()) {
            Color color = model.getRowBgColor(row);
            if (color != null) {
                comp.setBackground(color);
            }
        }
        int[] srows = table.getSelectedRows();
        for (int srow : srows)
            if (row == srow) {
                comp.setBackground(table.getSelectionBackground());
                break;
            }
        ColumnAdapter adapter = model.getColumnAdapter(column);
        if (adapter.getColumnFont() != null) {
            setFont(adapter.getColumnFont());
        }
    }

    public void setZebra1Color(Color color) {
        zebra1 = color;
    }

    public void setZebra2Color(Color color) {
        zebra2 = color;
    }
}