package kz.tamur.comps;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 21.03.2004
 * Time: 14:47:37
 * To change this template use File | Settings | File Templates.
 */
public class DTableModel extends AbstractTableModel {
    private List columns = new ArrayList();

    public DTableModel() {
    }

    public void addColumn(OrTableColumn c) {
        columns.add(c);
        fireTableStructureChanged();
    }

    public void removeColumn(OrTableColumn c) {
        columns.add(c);
        fireTableStructureChanged();
    }

    public OrTableColumn getColumn(int index) {
        return (OrTableColumn)columns.get(index);
    }

    public String getColumnName(int column) {
        return "" + ((TableColumn)columns.get(column)).getHeaderValue();
    }

    public int getRowCount() {
        return 2;
    }

    public int getColumnCount() {
        return columns.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
