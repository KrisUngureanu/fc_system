package kz.tamur.web.component;

import kz.tamur.comps.Constants;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent2;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComboColumnAdapter;
import kz.tamur.rt.adapters.DocFieldColumnAdapter;
import kz.tamur.rt.adapters.PopupColumnAdapter;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.adapters.TreeTableAdapter2;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.06.2007
 * Time: 17:24:28
 * To change this template use File | Settings | File Templates.
 */
public class RtWebTreeTableModel2 extends RtWebTableModel {

    private final OrWebTreeTable2 treeTable;
    private final String COLUMN_UP = "SortUp";
    private final String COLUMN_DOWN = "SortDown";

    public int sortColIdx = 0;
    public boolean isSortAsc = true;

    public RtWebTreeTableModel2(TreeTableAdapter2 adapter) {
        super(adapter);
        this.treeTable = (OrWebTreeTable2) adapter.getTable();
        treeTable.createTreeTableColumn();
        TableColumnModel cmodel = treeTable.getColumnModel();
        TableColumn tcol = new TableColumn(0, treeTable.getTreeWidth());
        cmodel.addColumn(tcol);
    }

    public void addColumn(OrColumnComponent column, int pos) {
        OrWebTableColumn col = (OrWebTableColumn) column;
        ColumnAdapter a = col.getAdapter();
        adapter.addColumnAdapter(a, pos);
        int caCount = adapter.getColumnAdapters().size();
        if (a.getUniqueIndex() > 0) {
            Integer uin = new Integer(a.getUniqueIndex());
            if (uniqueCols.get(uin) == null) {
                List<Integer> cols = new ArrayList<Integer>();
                cols.add(new Integer(caCount - 1));
                uniqueCols.put(uin, cols);
            } else {
                List<Integer> cols = uniqueCols.get(uin);
                cols.add(new Integer(caCount - 1));
                uniqueCols.put(uin, cols);
            }
        }
        TableColumn c = new TableColumn(caCount,
                col.getPreferredWidth());
        TableColumnModel cmodel = treeTable.getColumnModel();
        cmodel.addColumn(c);
        if (pos == -1) {
            col.setModelIndex(caCount);
        } else {
            col.setModelIndex(pos);
            // Сдвигаем modelIndex для всех колонок с modelIndex > pos
            for (int i = 0; i < cmodel.getColumnCount(); i++) {
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
        return super.getColumnCount() + 1;
    }

    public String getRowId(int rowIndex) {
        OrTreeComponent2 tree = treeTable.getTree();
        if (tree.getPathForRow(rowIndex) != null) {
            Node node = (Node) tree.getPathForRow(rowIndex).getLastPathComponent();

            return String.valueOf(node.getObject().id);
        }
        return "";
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        OrTreeComponent2 tree = treeTable.getTree();
        if (columnIndex == 0) {
            return tree.getModel();
        } else {
            List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
            ColumnAdapter ca = cadapters.get(columnIndex - 1);
            if (ca instanceof ComboColumnAdapter)
                return ca.getValueAt(rowIndex);
            else
                return ca.getObjectValueAt(rowIndex);
        }
    }

    public String getColumnName(int column) {
        if (column == 0)
            return treeTable.getTreeName();

        List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(column - 1);
        return ca.getColumn().getTitle();
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return TreeModel.class;
        } else {
            return super.getColumnClass(columnIndex - 1);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        OrTreeComponent2 tree = treeTable.getTree();
        if (columnIndex == 0)
            return true;
        if (adapter.getAccess() == Constants.READ_ONLY_ACCESS) {
            ColumnAdapter ca = getColumnAdapter(columnIndex);
            if (ca instanceof PopupColumnAdapter
                    || ca instanceof DocFieldColumnAdapter) {
                return ca.checkEnabled();
            } else {
                return false;
            }
        }
        ColumnAdapter ca = getColumnAdapter(columnIndex);
        return ca.isEnabled();
    }

    public OrColumnComponent getColumn(int colIndex) {
        if (colIndex == 0)
            return null;
        List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(colIndex - 1);
        return (OrColumnComponent) ca.getColumn();
    }

    public ColumnAdapter getColumnAdapter(int colIndex) {
        if (colIndex == 0)
            return null;
        List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
        ColumnAdapter ca = cadapters.get(colIndex - 1);
        return ca;
    }

    public String getColumnIconName(int column) {
        if (column == sortColIdx)
            return isSortAsc ? COLUMN_UP : COLUMN_DOWN;
        return null;
    }

    public int getRowFromIndex(int index) {
        return -1;
    }

    public int getRowForObjectId(long objId) {
        OrTreeComponent2 tree = treeTable.getTree();
        Node root = (Node) treeTable.getTreeAdapter().getModel().getRoot();
        if (root != null) {
        	try {
	            TreePath path = root.find(objId, true);
	            if (path != null) {
	                return tree.getRowForPath(path);
	            }
        	} catch (KrnException e) {
        		treeTable.getLog().error(e, e);
        	}
        }
        return -1;
    }

    public void fireTableRowsUpdated(int row, int row1) {
        treeTable.tableRowsUpdated(row, row1);
    }
}
