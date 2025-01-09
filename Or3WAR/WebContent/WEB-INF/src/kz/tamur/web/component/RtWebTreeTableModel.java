package kz.tamur.web.component;

import kz.tamur.rt.adapters.*;
import kz.tamur.comps.Constants;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.06.2007
 * Time: 17:24:28
 * To change this template use File | Settings | File Templates.
 */
public class RtWebTreeTableModel extends RtWebTableModel {

    private final OrWebTreeTable treeTable;
    private final String COLUMN_UP = "SortUp";
    private final String COLUMN_DOWN = "SortDown";

    public int sortColIdx = 0;
    public boolean isSortAsc = true;

    public RtWebTreeTableModel(TreeTableAdapter adapter) {
        super(adapter);
        this.treeTable = (OrWebTreeTable) adapter.getTable();
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
        OrTreeComponent tree = treeTable.getTree();
        if (tree.getPathForRow(rowIndex) != null) {
            TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(rowIndex).getLastPathComponent();

            if (node.getObject() != null)
                return String.valueOf(node.getObject().id);
            else
                return String.valueOf(node.index);
        }
        return "";
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        OrTreeComponent tree = treeTable.getTree();
        if (columnIndex == 0) {
            return tree.getModel();
        }
        if (tree.getPathForRow(rowIndex) != null) {
            TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(rowIndex).getLastPathComponent();

            int actIndex = node.index;
            List<ColumnAdapter> cadapters = adapter.getColumnAdapters();
            if (actIndex == -1) {
                KrnObject object = node.getObject();
                if (object != null) {
                    ColumnAdapter ca = cadapters.get(columnIndex - 1);
                    Object o = ca.getValueForNode(object);
                    if (o != null) return o;
                }
                return tree.getModel();
            }

            ColumnAdapter ca = cadapters.get(columnIndex - 1);
            if (ca instanceof ComboColumnAdapter)
                return ca.getValueAt(actIndex);
            else
                return ca.getObjectValueAt(actIndex);
        }
        return null;
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

/*        public void fireTableStructureChanged() {
        TreeTableCellRenderer tree = treeTable.getTree();
        // super.fireTableStructureChanged();
        TableColumnModel cm = treeTable.getJTable().getColumnModel();
        TableColumn tc = cm.getColumn(0);
        // tc.setCellEditor(treeCellEditor);
        // tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
        tc.setPreferredWidth(treeTable.getTreeWidth());

        for (int i = 0; i < columns.size(); ++i) {
            ColumnAdapter orc = (ColumnAdapter) columns.get(i);
            tc = treeTable.getJTable().getColumnModel().getColumn(i + 1);
            if (orc.getCellRenderer() == null) {
                if (orc instanceof CheckBoxColumnAdapter) {
                    orc.setCellRenderer(new BooleanTableCellRenderer());
                } else if (orc instanceof IntColumnAdapter) {
                    orc.setCellRenderer(new IntegerTableCellRenderer());
                } else if (orc instanceof FloatColumnAdapter) {
                    orc.setCellRenderer(new FloatTableCellRenderer());
                } else {
                    orc.setCellRenderer(new OrCellRenderer());
                }
            }
            tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
            // tc = treeTable.getJTable().getColumnModel().getColumn(i + 1);
            // tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
            tc.setCellEditor(orc.getCellEditor());
            treeTable.updateRenderers();
        }
    }
*/
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        OrTreeComponent tree = treeTable.getTree();
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
        TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(rowIndex).getLastPathComponent();
        int actIndex = node.index;
        if (actIndex == -1)
            return false;
        else {
            ColumnAdapter ca = getColumnAdapter(columnIndex);
            return ca.isEnabled();
        }
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

    public int getActualRow(int row) {
        OrTreeComponent tree = treeTable.getTree();
        TreePath path = tree.getPathForRow(row);
        if (path == null)
            return row;
        TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
        return node.index;
    }

    public int getRowFromIndex(int index) {
        OrTreeComponent tree = treeTable.getTree();
        TreeAdapter.Node root = treeTable.getTreeAdapter().getRoot();
        if (root != null) {
            TreePath path = root.find(index);
            if (path != null) {
                return tree.getRowForPath(path);
            }
        }
        return -1;
    }

    public int getRowForObjectId(long objId) {
        OrTreeComponent tree = treeTable.getTree();
        TreeAdapter.Node root = treeTable.getTreeAdapter().getRoot();
        if (root != null) {
            TreePath path = root.find(objId, true);
            if (path != null) {
                return tree.getRowForPath(path);
            }
        }
        return -1;
    }

    public void fireTableRowsUpdated(int row, int row1) {
        treeTable.tableRowsUpdated(row, row1);
    }
}
