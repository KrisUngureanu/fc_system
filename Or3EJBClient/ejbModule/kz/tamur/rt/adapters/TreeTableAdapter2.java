package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.TreeAdapter2.Node;

import javax.swing.*;
import javax.swing.tree.TreeModel;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Date: 30.11.2004 Time: 12:22:41
 * 
 * @author Администратор
 */
public class TreeTableAdapter2 extends TableAdapter implements TreeListEventListener {

    private int access = Constants.FULL_ACCESS;

    private TreeAdapter2 treeAdapter;

    private boolean selfChange = false;

    /** Реф для последнего выбранного значения. */
    private OrRef treeValueRef;
    /** Массив индексов строк предыдущего выбора. */
    private int[] oldSelectedRow;
    /** Признак использования мультивыбора узлов с чекбоксами в качестве элементов выбора. */
    private boolean useCheck = false;

    public TreeTableAdapter2(OrFrame frame, OrTreeTable2 treeTable, boolean isEditor) throws KrnException {
        super(frame, treeTable, 0, isEditor);

        treeAdapter = new TreeAdapter2(frame, treeTable, isEditor);
        treeAdapter.addTableModelListener(this);

        PropertyNode proot = treeTable.getProperties();

        PropertyNode pnode = proot.getChild("language");
        if (pnode != null) {
            PropertyValue pv = treeTable.getPropertyValue(pnode);
            if (!pv.isNull() && !pv.getKrnObjectId().equals("")) {
                langId = Long.parseLong(pv.getKrnObjectId());
            }
        }

        PropertyValue pv = treeTable.getPropertyValue(treeTable.getProperties().getChild("pov").getChild("access"));
        if (!pv.isNull()) {
            access = pv.intValue();
        }

        // Переставляем адаптер в список листененров позже treeAdapter
        dataRef.removeOrRefListener(this);
        dataRef.addOrRefListener(this);

        if (useCheck) {
            // инициализировать selItems в REF
            dataRef.setSelItems(null);
        }

        pnode = proot.getChild("ref").getChild("treeValueRef");
        pv = treeTable.getPropertyValue(pnode);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Выбранное значение";
                treeValueRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }

    }

    protected RtTableModel createModel() {
        return new RtTreeTableModel();
    }

    public void countCurrentTableItem() {
        int sel = table.getJTable().getSelectedRow();
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
        int count = model.getRowCount();
        ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
        rowCount = count;
    }

    protected int findText(ColumnAdapter ca, String textForSearch, int from) {
        TreeAdapter2.Node root = (TreeAdapter2.Node) treeAdapter.getModel().getRoot();
        int row = findTextInNode(ca, root, textForSearch, from, true);
        if (row > -1) {
            return row;
        }
        row = findTextInNode(ca, root, textForSearch, from, false);
        return row;
    }

    protected void findText(ColumnAdapter ca, String textForSearch, FindRowPanel findPanel, int from) {
        TreeAdapter2.Node root = (TreeAdapter2.Node) treeAdapter.getModel().getRoot();
        int row = findTextInNode(findPanel, ca, root, textForSearch, from, true);
        if (row > -1) {
            return;
        }
        row = findTextInNode(findPanel, ca, root, textForSearch, from, false);
        if (row == -1) {
            MessagesFactory.showMessageDialog(table.getJTable().getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                    res.getString("searchComplete"));
        }
    }

    protected int findTextInNode(FindRowPanel findPanel, ColumnAdapter ca, TreeAdapter2.Node node, String text, int index,
            boolean from) {
        return -1;
    }

    protected int findTextInNode(ColumnAdapter ca, TreeAdapter2.Node node, String text, int index, boolean from) {
        return -1;
    }

    public class RtTreeTableModel extends RtTableModel {

        private final ImageIcon COLUMN_UP = kz.tamur.rt.Utils.getImageIcon("SortUp");
        private final ImageIcon COLUMN_DOWN = kz.tamur.rt.Utils.getImageIcon("SortDown");

        public int sortColIdx = 0;
        public boolean isSortAsc = true;

        public int addColumn(ColumnAdapter a) {
            columns.add(a);
            return columns.size();
        }

        public int getColumnCount() {
            return columns.size() + 1;
        }

        public int getRowCount() {
            return treeAdapter.getRowCount();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return treeAdapter;
            } else {
                ColumnAdapter ca = columns.get(columnIndex - 1);
                return ca.getValueAt(rowIndex);
            }
        }

        public String getColumnName(int column) {
            if (column == 0) {
                return "Tree";
            }
            ColumnAdapter ca = (ColumnAdapter) columns.get(column - 1);
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
            ColumnAdapter ca = getColumnAdapter(columnIndex);
            if (ca instanceof MemoColumnAdapter)
                return true;

            return isColumnCellEditable(rowIndex, columnIndex);
        }

        public boolean isColumnCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 || access != Constants.READ_ONLY_ACCESS;
        }

        public void setInterfaceLangId(int langId) {
            for (int i = 0; i < columns.size(); i++) {
                ColumnAdapter ca = (ColumnAdapter) columns.get(i);
                ca.getColumn().setLangId(langId);
            }
            fireTableStructureChanged();
        }

        public OrTableColumn getColumn(int colIndex) {
            if (colIndex == 0) {
                return null;
            }
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex - 1);
            return ca.getColumn();
        }

        public ColumnAdapter getColumnAdapter(int colIndex) {
            if (colIndex == 0) {
                return null;
            }
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex - 1);
            return ca;
        }

        public Icon getColumnIcon(int column) {
            if (column == sortColIdx) {
                return isSortAsc ? COLUMN_UP : COLUMN_DOWN;
            }
            return null;
        }

        public int getRowFromIndex(int index) {
            return -1;
        }

        public int getRowForObjectId(long objId) {
            return treeAdapter.getRowForObjectId(objId);
        }
    }

    public void nodeCollapsed(Node node) {
        Container cnt = table.getTopLevelAncestor();
        if (cnt != null) {
            cnt.setCursor(Constants.WAIT_CURSOR);
        }
        treeAdapter.nodeCollapsed(node);
        if (cnt != null) {
            cnt.setCursor(Constants.DEFAULT_CURSOR);
        }

    }

    public void nodeExpanded(Node node) {
        Container cnt = table.getTopLevelAncestor();
        if (cnt != null) {
            cnt.setCursor(Constants.WAIT_CURSOR);
        }
        treeAdapter.nodeExpanded(node);
        if (cnt != null) {
            cnt.setCursor(Constants.DEFAULT_CURSOR);
        }
    }

    public OrTable getTable() {
        return table;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(isEnabled);
        table.setEnabled(enabled);
        treeAdapter.setEnabled(enabled);
    }

    public void scrollToVisible(int rowIndex, int vColIndex) {
        int row = ((RtTreeTableModel) model).getRowFromIndex(rowIndex);
        super.scrollToVisible(row, vColIndex + 1);
    }

    protected void initActionMap(final OrTable table) {
        super.initActionMap(table);
        InputMap im = table.getJTable().getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // Have the enter key work the same as the tab key
        KeyStroke left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        final Action oldLeftAction = table.getJTable().getActionMap().get(im.get(left));
        final Action oldRightAction = table.getJTable().getActionMap().get(im.get(right));
    }

    public TreeAdapter2 getTreeAdapter() {
        return treeAdapter;
    }

    public Node getNodeForRow(int row) {
        return treeAdapter.getNodeForRow(row);
    }

    public void setSelectedRow(int row, Object originator) throws KrnException {
        Node node = treeAdapter.getNodeForRow(row);
        if (node != null) {
            try {
                selfChange = true;
                dataRef.absolute(row, this);
                int[] selR = table.getJTable().getSelectedRows();
                dataRef.setSelectedItems(selR);

                addSelectedRows(selR);

                if (treeValueRef != null) {
                    TreeAdapter2.Node n = treeAdapter.getSelectedNode();
                    if (n != null) {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        OrRef.Item item = treeValueRef.getItem(0);
                        if (item == null)
                            treeValueRef.insertItem(0, n.getObject(), this, this, false);
                        else
                            treeValueRef.changeItem(n.getObject(), this, this);
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    }
                    TreeAdapter2.Node[] nodes = treeAdapter.getSelectedNodes();
                    KrnObject[] objs = new KrnObject[nodes.length];
                    for (int i = 0; i < nodes.length; ++i)
                        objs[i] = nodes[i].getObject();
                    treeValueRef.setSelectedItems(objs);
                }
            } finally {
                selfChange = false;
            }
        }
    }

    public void addSelectedRows(int[] rows) {
        if (dataRef == null || !useCheck) {
            return;
        }
        // нужно выявить разницу между предыдущим и выбором и новым, чтобы удалить отсутствующие записи
        List<Integer> onDel = new ArrayList<Integer>();
        if (oldSelectedRow != null) {
            for (int row : oldSelectedRow) {
                if (Utils.arrayFinder(rows, row) == -1) {
                    onDel.add(row);
                }
            }
        }
        // если найдены записи на удаление, то удалить их из рефа
        if (onDel.size() > 0) {
            for (int row : onDel) {
                if (row < treeAdapter.getRowCount()) {
                    Node node = treeAdapter.getNodeForRow(row);
                    selfChange = true;
                    try {
                        if (isNodeSelected(node)) {
                            boolean calcOwner = OrCalcRef.setCalculations();
                            dataRef.removeSelItem(node.getObject());
                            if (calcOwner) {
                                OrCalcRef.makeCalculations();
                            }
                        }
                    } finally {
                        selfChange = false;
                    }
                }
            }
            onDel = new ArrayList<Integer>();
        }

        for (int row : rows) {
            if (row < treeAdapter.getRowCount()) {
                if (row < treeAdapter.getRowCount() && row != -1) {
                    Node node = treeAdapter.getNodeForRow(row);
                    try {
                        selfChange = true;
                        if (!isNodeSelected(node)) {
                            boolean calcOwner = OrCalcRef.setCalculations();
                            dataRef.addSelItem(node.getObject());
                            if (calcOwner) {
                                OrCalcRef.makeCalculations();
                            }
                        }
                    } finally {
                        selfChange = false;
                    }
                }
            }

        }
        if (rows == null || rows.length == 0) {
            if (oldSelectedRow == null || oldSelectedRow.length == 0) {
                List<OrRef.Item> selectedItems = dataRef.getSelItems();
                if (selectedItems != null) {
                    List<Integer> oldRow = new ArrayList<Integer>();
                    int n;
                    for (OrRef.Item item : selectedItems) {
                        n = treeAdapter.getRowForObjectId(((KrnObject) item.getCurrent()).id);
                        if (n != -1) {
                            oldRow.add(n);
                        }
                    }
                    if (oldRow.size() > 0) {
                        oldSelectedRow = new int[oldRow.size()];
                        for (int i = 0; i < oldSelectedRow.length; i++) {
                            oldSelectedRow[i] = oldRow.get(i);
                        }
                    }
                }
            }
        } else {
            oldSelectedRow = rows;
        }
    }

    public void rowsUpdated(int start, int end) {
        for (int i = start; i <= end; i++)
            model.fireTableCellUpdated(i, 0);

        table.invalidate();
        table.repaint();
    }

    protected void sort() {
        isSort = true;
    }

    /**
     * Очистка списка выбранных объектов.
     */
    public void clearSelItem() {
        if (dataRef != null) {
            dataRef.clearSelItem();
        }
    }

    /**
     * Проверка, присутствует ли <code>Node</code> в списке выбранных объектов.
     * 
     * @param node
     *            провряемая <code>Node</code>
     * @return true, если есть в списке
     */
    public boolean isNodeSelected(Node node) {
        return node != null && dataRef.isInSelItem(node.getObject());
    }

    /**
     * Задание массива строк предыдущего выбора
     * 
     * @param oldSelectedRow
     *            новый массив строк
     */
    public void setOldSelectedRow(int[] oldSelectedRow) {
        this.oldSelectedRow = oldSelectedRow;
    }

    /**
     * Установить атрибут признака использования мультивыбора узлов с чекбоксами в качестве элементов выбора.
     * 
     * @param useCheck
     *            новое значение пизнака мультивыбора
     */
    public void setUseCheck(boolean useCheck) {
        this.useCheck = useCheck;
    }
}
