package kz.tamur.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: KazakBala
 * Date: 11.03.2005
 * Time: 10:42:19
 * To change this template use File | Settings | File Templates.
 */
public class OrCellRenderer extends ZebraCellRenderer {

 //   Color REQ_ERROR_COLOR = new Color(255, 204, 204);
    Color REQ_ERROR_COLOR = MainFrame.COLOR_FIELD_NO_FLC;
    Color EXPR_ERROR_COLOR = new Color(202, 247, 187);
    Color fontColor = null;


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
    }
    
    public void changeComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column, Component comp) {
    	super.changeComponent(table, value, isSelected, hasFocus, row, column, comp);
        TableAdapter.RtTableModel model = (TableAdapter.RtTableModel) table.getModel();
        if (model instanceof TreeTableAdapter.RtTreeTableModel) {
            row = ((TreeTableAdapter.RtTreeTableModel) model).getActualRow(row);
        }
        ColumnAdapter adapter = model.getColumnAdapter(column);
        if (adapter == null || row < 0) return;
        
        Integer state = adapter.getState(new Integer(row));
        if (state == Constants.REQ_ERROR && adapter.getEnterDB() == Constants.BINDING)
            comp.setBackground(REQ_ERROR_COLOR);
        else if (state == Constants.EXPR_ERROR)
            comp.setBackground(EXPR_ERROR_COLOR);
        else if (adapter.isBackgroundColorSet()) {
            Color c = adapter.getColumnBackgroundColor();
            if (c.equals(Color.white)) {
                comp.setBackground(row % 2 != 0 ? zebra1 : zebra2);
            } else {
                comp.setBackground(c);
            }
        } else if (adapter.isBackColorCalculated()) {
            Color c = adapter.getColumnBackgroundColor(row, column);
            if (c.equals(Color.white)) {
                comp.setBackground(row % 2 != 0 ? zebra1 : zebra2);
            } else {
                comp.setBackground(c);
            }
        }
        if (adapter.isFontColorSet()) {
            comp.setForeground(adapter.getColumnFontColor());
        }
        
        if (model.isRowFontColorCalc()) {
            Color color = model.getRowFontColor(row);
            comp.setForeground(color != null ? color : Color.black);
        } else if (!adapter.isFontColorCalculated()) {
            comp.setForeground(adapter.getColumnDefaultForegroundColor(column));            
        }
        
        if (adapter.isFontColorCalculated()) {
            comp.setForeground(adapter.getColumnFontColor(row, column));
        }
        
        Font font = adapter.getColumnFont(row, column);
        if (font != null) {
            comp.setFont(font);
        } else if (adapter.getColumnFont() != null) {
            comp.setFont(adapter.getColumnFont());
        }
        comp.repaint();
    }

    public void setFocus() {
        this.requestFocus();
    }
}
