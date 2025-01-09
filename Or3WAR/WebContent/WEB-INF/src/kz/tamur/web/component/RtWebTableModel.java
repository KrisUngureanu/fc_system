package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.EventListenerList;

import kz.tamur.comps.*;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.rt.adapters.*;
import kz.tamur.or3.client.comps.interfaces.OrTableModel;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;

@SuppressWarnings("serial")
public class RtWebTableModel implements WebTableModel, OrTableModel {
    protected EventListenerList listenerList = new EventListenerList();

    protected final TableAdapter adapter;
    private final OrWebTable orTable;

    private boolean isRowBackColorCalc = false;
    private boolean isRowFontColorCalc = false;
    protected Map<Integer, List<Integer>> uniqueCols =
    		new HashMap<Integer, List<Integer>>();
    private int selectedColumn = -1;

    public RtWebTableModel(TableAdapter adapter) {
    	this.adapter = adapter;
    	this.orTable = (OrWebTable)adapter.getTable();

    	if (adapter.getRowBgRef() != null) {
    		isRowBackColorCalc = true;
    	}
    	if (adapter.getRowFgRef() != null) {
    		isRowFontColorCalc = true;
    	}
    }

    public boolean isRowFontColorCalc() {
        return isRowFontColorCalc;
    }

    public void setRowFontColorCalc(boolean rowFontColorCalc) {
        isRowFontColorCalc = rowFontColorCalc;
    }

    public boolean isRowBackColorCalc() {
        return isRowBackColorCalc;
    }

    public void setRowBackColorCalc(boolean rowBackColorCalc) {
        isRowBackColorCalc = rowBackColorCalc;
    }

    public void addColumn(OrColumnComponent column, int pos) {
        OrWebTableColumn col = (OrWebTableColumn)column;
    	ColumnAdapter a = col.getAdapter();
    	adapter.addColumnAdapter(a, pos);
    	int caCount = adapter.getColumnAdapters().size();
        if (a.getUniqueIndex() > 0) {
            Integer uin = a.getUniqueIndex();
            if (uniqueCols.get(uin) == null) {
                List<Integer> cols = new ArrayList<Integer>();
                cols.add(caCount - 1);
                uniqueCols.put(uin,cols);
            } else {
                List<Integer> cols = uniqueCols.get(uin);
                cols.add(caCount - 1);
                uniqueCols.put(uin,cols);
            }
        }
        TableColumn c = new TableColumn(caCount - 1,
            col.getPreferredWidth());
        TableColumnModel cmodel = orTable.getColumnModel();
        cmodel.addColumn(c);
        if (pos == -1) {
        	col.setModelIndex(caCount - 1);
        } else {
        	col.setModelIndex(pos);
        	// Сдвигаем modelIndex для всех колонок с modelIndex > pos
        	for(int i = 0; i < cmodel.getColumnCount(); i++) {
        		TableColumn tcol = cmodel.getColumn(i);
        		int modelIndex = tcol.getModelIndex();
        		if (modelIndex > pos) {
        			tcol.setModelIndex(modelIndex + 1);
        		}
        	}
        }
        c.setMaxWidth(col.getMaxWidth());
        c.setMinWidth(col.getMinWidth());
    }

    public int getColumnCount() {
        return adapter.getColumnAdapters().size();
    }

    public Color getZebra1Color() {
        return adapter.getZebraColor1();
    }

    public Color getZebra2Color() {
        return adapter.getZebraColor2();
    }

    public int getRowCount() {
        return adapter.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	if (adapter.getDataRef() != null) {
    		rowIndex = adapter.webToRefIndex(rowIndex);
	    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
	        if (columnIndex >= cadapters.size()) return null;
	        ColumnAdapter ca = cadapters.get(columnIndex);
	        if (ca instanceof ComboColumnAdapter || ca instanceof TreeColumnAdapter || ca instanceof MemoColumnAdapter)
	            return ca.getValueAt(rowIndex);
	        else
	            return ca.getObjectValueAt(rowIndex);
    	} else {
    		Object row = adapter.getData().get(rowIndex);
    		if (row instanceof String[])
    			return ((String[])row)[columnIndex];
    		else if (row instanceof Object[])
    			return ((Object[])row)[columnIndex];
    		else if (row instanceof List && ((List)row).size() > columnIndex)
    			return ((List)row).get(columnIndex);
    		else
    			return "";
    	}
    }

    public String getColumnAlign(int column) {
    	return null;
    }
    
    public String getColumnName(int column) {
    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(column);
        return ca.getColumn().getTitle();
    }

    public String getColumnIconName(int column) {
        List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(column);
        return ca.getColumn().getIconName();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
		rowIndex = adapter.webToRefIndex(rowIndex);
    	int access = adapter.getAccess();
        if (!adapter.isHackEnabled() || access == Constants.READ_ONLY_ACCESS) {
            ColumnAdapter ca = getColumnAdapter(columnIndex);
            if (!(ca instanceof PopupColumnAdapter)) {
                return false;
            } else {
                return ca.checkEnabled();
            }
        } else if (access == Constants.LAST_ROW_ACCESS) {
            int rowcount = orTable.getRowCount();
            return (rowcount - 1 == rowIndex);
        } else if (access == Constants.BY_TRANSACTION_ACCESS) {
            //ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
            //OrRef c_ref = ca.dataRef; //@todo Выяснить у Каиржана!
        }
    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(columnIndex);
        return ca.isEnabled();
    }

    public OrColumnComponent getColumn(int colIndex) {
    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(colIndex);
        return ca.getColumn();
    }

    public ColumnAdapter getColumnAdapter(int colIndex) {
    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        return cadapters.get(colIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
    	List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(columnIndex);
        if (ca instanceof CheckBoxColumnAdapter) {
            return Boolean.class;
        } else {
            return Object.class;
        }
    }

    public Map getUniqueMap() {
        return uniqueCols;
    }

    public Color getRowBgColor(int index) {
        if (isRowBackColorCalc) {
        	index = adapter.webToRefIndex(index);
            OrRef.Item item = adapter.getRowBgRef().getItem(0, index);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return kz.tamur.rt.Utils.getColorByName(o.toString());
            }
        }
        return Color.white;
    }

    public Color getRowFontColor(int index) {
        if (isRowFontColorCalc) {
        	index = adapter.webToRefIndex(index);
            OrRef.Item item = adapter.getRowFgRef().getItem(0, index);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return kz.tamur.rt.Utils.getColorByName(o.toString());
            }
        }
        return Color.black;
    }
    
    public Font getFont(int row, int col) {
    	ColumnAdapter a = getColumnAdapter(col);
    	if (a != null) {
        	row = adapter.webToRefIndex(row);
	        Font font = a.getColumnFont(row, col);
	        if (font != null) {
	            return font;
	        } else if (a.getColumnFont() != null) {
	            return a.getColumnFont();
	        }
    	}
    	return null;
    }

    @Override
	public boolean isFontColorCalculated(int col) {
    	ColumnAdapter a = getColumnAdapter(col);
    	if (a != null) {
    		return a.isFontColorCalculated();
    	}
    	return false;
	}

	@Override
	public Color getColumnFontColor(int row, int col) {
		if (isFontColorCalculated(col)) {
	    	ColumnAdapter a = getColumnAdapter(col);
	    	if (a != null) {
	        	row = adapter.webToRefIndex(row);
	    		return a.getColumnFontColor(row, col);
	    	}
		}
    	return null;
	}

	public String getColumnWidth(int columnIndex) {
        return String.valueOf(orTable.getColumnModel().getColumn(columnIndex).getPreferredWidth());
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	if (adapter.getDataRef() == null) {
    		Object row = adapter.getData().get(rowIndex);
    		if (row instanceof String[])
    			((String[])row)[columnIndex] = aValue.toString();
    		else if (row instanceof Object[])
    			((Object[])row)[columnIndex] = aValue;
    		else if (row instanceof List)
    			((List)row).set(columnIndex, aValue);
    	}
    }

    public void setSelectedObject(Object obj) {
    }

    public int getRowForObject(Object obj) {
        return 0;
    }

    public int getSelectedRow() {
        return 0;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int col) {
        selectedColumn = col;
    }

    public String getRowId(int row) {
        return String.valueOf(row);
    }

    public void addTableModelListener(TableModelListener l) {
	listenerList.add(TableModelListener.class, l);
    }

    public void removeTableModelListener(TableModelListener l) {
	listenerList.remove(TableModelListener.class, l);
    }

    public int getActualRow(int row) {
    	row = adapter.webToRefIndex(row);
        return row;
    }

    public int getRowFromIndex(int i) {
    	i = adapter.webToRefIndex(i);
        return i;
    }

    public void fireTableRowsUpdated(int row, int row1) {
        orTable.tableRowsUpdated(row, row1);
    }

    public int getRowForObjectId(long objId) {
        return -1;
    }
}
