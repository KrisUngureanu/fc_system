package kz.tamur.web.common.table;

import java.awt.Color;
import java.awt.Font;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 05.07.2006
 * Time: 10:54:41
 * To change this template use File | Settings | File Templates.
 */
public interface WebTableModel {

    public int getRowCount();
    public int getColumnCount();
    public String getColumnName(int columnIndex);
    public String getColumnAlign(int columnIndex);
    public String getColumnIconName(int columnIndex);
    public String getColumnWidth(int columnIndex);
    public Class<?> getColumnClass(int columnIndex);
//    public boolean isCellEditable(int rowIndex, int columnIndex);
    public Object getValueAt(int rowIndex, int columnIndex);
    public void setValueAt(Object aValue, int rowIndex, int columnIndex);

    void setSelectedObject(Object obj);
    int getSelectedRow();
    int getSelectedColumn();
    void setSelectedColumn(int col);
    int getRowForObject(Object obj);

    String getRowId(int row);
    
    boolean isRowFontColorCalc();
    boolean isRowBackColorCalc();
    Color getRowBgColor(int index);
    Color getRowFontColor(int index);
    Font getFont(int row, int col);
    
    boolean isFontColorCalculated(int col);
    Color getColumnFontColor(int row, int col);
}
