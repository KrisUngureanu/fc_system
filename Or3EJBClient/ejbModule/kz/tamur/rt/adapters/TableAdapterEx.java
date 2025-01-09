package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.List;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrTable;

import com.cifs.or2.kernel.KrnException;

public class TableAdapterEx extends ContainerAdapter {
	
	List<ColumnAdapter> columnAdapters = new ArrayList<ColumnAdapter>();
	
	public TableAdapterEx(OrFrame frame, OrTable table, boolean isEditor)
			throws KrnException {

		super(frame, table, isEditor);
	}

	public void addColumnAdapters(List<ColumnAdapter> adapters) {
		columnAdapters.addAll(adapters);
	}
	
	public int getColumnCount() {
		return columnAdapters.size();
	}
	
	public ColumnAdapter getColumnAt(int i) {
		return columnAdapters.get(i);
	}
	
	public Object getValueAt(int row, int column) {
		ColumnAdapter ca = columnAdapters.get(column);
		return ca.getValueAt(row);
	}
	
	public int getRowCount() {
		return dataRef.getItems(0).size();
	}
}
