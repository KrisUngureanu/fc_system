package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiContainer;

import javax.swing.table.TableModel;
import java.beans.PropertyChangeListener;

public interface OrTableComponent extends OrGuiContainer {
	OrColumnComponent getColumnAt(int col);
	void setSelectedRow(int row);
    void setSelectedRows(int[] rows, boolean selfChange);
	int[] getSelectedRows();
        int getSelectedRow();
	void tableDataChanged();
	void tableRowsDeleted(int firstRow, int lastRow);
	void tableRowsInserted(int firstRow, int lastRow);
	void tableRowsUpdated(int firstRow, int lastRow);
	void tableCellUpdated(int row, int col);
	void tableStructureChanged();

        public boolean isNaviExists();
        public PropertyChangeListener getNavigator();

        int getRowCount();
        int getColumnCount();
        Object getValueAt(int row, int col);
        TableModel getModel();

    void setDelEnabled(boolean enabled);
    boolean isDelEnabled();
    void setLimitExcceded(boolean limitExceeded);
}
