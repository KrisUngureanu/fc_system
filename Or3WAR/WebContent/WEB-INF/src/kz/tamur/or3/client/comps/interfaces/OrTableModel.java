package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.ColumnAdapter;

import java.awt.*;
import java.util.Map;

import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 06.05.2004
 * Time: 12:18:43
 * To change this template use File | Settings | File Templates.
 */
public interface OrTableModel extends TableModel {
    OrColumnComponent getColumn(int colIndex);
    int getColumnCount();
    Color getZebra1Color();
    Color getZebra2Color();
    public Class getColumnClass(int columnIndex);
    public ColumnAdapter getColumnAdapter(int columnIndex);
    public Map getUniqueMap();
    void addColumn(OrColumnComponent col, int pos);

    int getRowFromIndex(int i);

    void fireTableRowsUpdated(int row, int row1);

    int getRowForObjectId(long objId);
}
