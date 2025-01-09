package kz.tamur.rt.adapters;

import static kz.tamur.comps.Mode.RUNTIME;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrTableComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent2;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.RtWebTreeTableModel2;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 30.11.2004
 * Time: 12:22:41
 */
public class TreeTableAdapter2 extends TableAdapter implements TreeListEventListener {

    private int access = Constants.FULL_ACCESS;
    private OrTreeTableComponent2 treeTable;
    private List<Node> listView = new ArrayList<Node>();
    private Set<Node> expandedNodes = new HashSet<Node>();
    private boolean selfChange = false;
    private TreeAdapter2 treeAdapter;
    /** Реф для последнего выбранного значения. */
    private OrRef treeValueRef;
    /** Массив индексов строк предыдущего выбора.*/    
    private int[] oldSelectedRow;
    /** Признак использования мультивыбора узлов с чекбоксами в качестве элементов выбора.*/
    private boolean useCheck = false;


    public TreeTableAdapter2(OrFrame frame, OrTreeTableComponent2 treeTable, boolean isEditor) throws KrnException {
        super(frame, treeTable, 0, isEditor);
        this.treeTable = treeTable;
        treeAdapter = new TreeAdapter2(frame, treeTable, isEditor);
        treeAdapter.addTableModelListener(this);
        treeTable.getTree().setAdapter(treeAdapter);

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
                treeValueRef = OrRef.createRef(pv.stringValue(), false, RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }
    }

    public void countCurrentTableItem() {
        int sel = table.getSelectedRow();
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
        int count = treeTable.getTree().getRowCount();
        ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
        rowCount = count;
    }

    public int getRowCount() {
        return treeAdapter.getRowCount();
    }

    public void nodeCollapsed(Node node) {
        treeTable.getTreeAdapter().nodeCollapsed(node);
    }

    public void nodeExpanded(Node node) {
        treeTable.getTreeAdapter().nodeExpanded(node);
    }

    private void countChildren(Node node, List<Node> children) {
        Enumeration<Node> chs = node.children();
        while (chs.hasMoreElements()) {
            Node child = chs.nextElement();
            children.add(child);
            if (expandedNodes.contains(child)) {
                countChildren(child, children);
            }
        }
    }

    public OrTableComponent getTable() {
        return table;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(isEnabled);
        table.setEnabled(enabled);
        treeTable.getTreeAdapter().setEnabled(enabled);
    }

    public TreeAdapter2 getTreeAdapter() {
        return treeTable.getTreeAdapter();
    }

    public OrTreeTableComponent2 getTreeTable() {
        return treeTable;
    }

    public Node getNodeForRow(int row) {
        return (listView.size() > row) ? listView.get(row) : null;
    }

    public void setSelectedRow(int row, Object originator) throws KrnException {
        if (row < treeAdapter.getRowCount()) {
            Node node = treeAdapter.getNodeForRow(row);
            try {
                selfChange = true;
                treeTable.getTreeAdapter().setSelectedNode(node, originator);

                if (treeValueRef != null) {
                    TreeAdapter2.Node n = treeAdapter.getSelectedNode();
                    if (n != null) {
                        boolean calcOwner = OrCalcRef.setCalculations();
                        try {
	                        OrRef.Item item = treeValueRef.getItem(0);
	                        if (item == null) {
	                            treeValueRef.insertItem(0, n.getObject(), this, this, false);
	                        } else {
	                            treeValueRef.changeItem(n.getObject(), this, this);
	                        }
		            	} catch (Exception e) {
		            		log.error(e, e);
		            	} finally {
		                    if (calcOwner)
		                    	OrCalcRef.makeCalculations();
		            	}
                    }
                    TreeAdapter2.Node[] nodes = treeAdapter.getSelectedNodes();
                    KrnObject[] objs = new KrnObject[nodes.length];
                    for (int i = 0; i < nodes.length; ++i) {
                        objs[i] = nodes[i].getObject();
                    }
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
        /*
         * if (dataRef.getSelItems() != null) {
         * System.out.println("Количество SelItems " + dataRef.getSelItems().size());
         * }
         */
        /* нужно выявить разницу между предыдущим и выбором и новым, чтобы удалить отсутствующие записи */
        List<Integer> onDel = new ArrayList<Integer>();
        // System.out.println("Выделить строки" + Arrays.toString(rows));

        if (oldSelectedRow != null) {
            // System.out.println("Предыдущий выбор" + Arrays.toString(oldSelectedRow));
            for (int row : oldSelectedRow) {
                if (Utils.arrayFinder(rows, row) == -1) {
                    onDel.add(row);
                    // System.out.println("Строка на удаление " + row);
                }
            }
        }
        // если найдены записи на удаление, то удалить их из рефа
        if (onDel.size() > 0) {
            for (int row : onDel) {
                if (row < treeAdapter.getRowCount()) {
                    Node node = treeAdapter.getNodeForRow(row);
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        if (isNodeSelected(node)) {
                            dataRef.removeSelItem(node.getObject());
                            // System.out.println("Запись удалена из SelItem");
                        }
                    } finally {
                        selfChange = false;
                        if (calcOwner) {
                            OrCalcRef.makeCalculations();
                        }
                    }
                }
            }
            onDel = new ArrayList<Integer>();
        }

        for (int row : rows) {
            if (row < treeAdapter.getRowCount()) {
                if (row < treeAdapter.getRowCount() && row != -1) {
                    Node node = treeAdapter.getNodeForRow(row);
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        selfChange = true;
                        if (!isNodeSelected(node)) {
                            dataRef.addSelItem(node.getObject());
                            // System.out.println("Выбранна запись id " + node.getObject().id);
                        }
                    } finally {
                        selfChange = false;
                        if (calcOwner) {
                            OrCalcRef.makeCalculations();
                        }
                    }
                }
            }

        }
        if (rows == null || rows.length == 0) {
            if (oldSelectedRow == null || oldSelectedRow.length == 0) {
                List<OrRef.Item> selectedItems = dataRef.getSelectedItems();
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
        if (dataRef.getSelectedItems() != null) {
            // System.out.println("Количество SelItems(после добавления) " + dataRef.getSelItems().size());
        }
    }

    public void treeNodesChanged(TreeModelEvent e) {
    }

    
    public void treeNodesInserted(TreeModelEvent e) {
        TreePath path = e.getTreePath();
        Node node = (Node) path.getLastPathComponent();
        // Если узел в который добавили новый узел - раскрыт, то добавить созданный узел в listView
        if (expandedNodes.contains(node)) {
            int i = listView.indexOf(node);
            int[] indices = e.getChildIndices();
            for (int j : indices) {
                int row = i + j + 1;
                listView.add(row, (Node) node.getChildAt(j));
                table.tableRowsInserted(row, row);
            }
        }
    }

    public void rowsUpdated(int start, int end) {
        ((RtWebTreeTableModel2) table.getModel()).fireTableRowsUpdated(start, end);
    }

    public void treeNodesRemoved(TreeModelEvent e) {

    }

    public void treeStructureChanged(TreeModelEvent e) {
        Node root = (Node) treeTable.getTreeAdapter().getModel().getRoot();
        expandedNodes.add(root);
        int sz = listView.size();
        if (sz > 0) {
            listView.clear();
            table.tableRowsDeleted(0, sz - 1);
        }
        if (root != null) {
            List<Node> children = new LinkedList<Node>();
            countChildren(root, children);
            listView.add(root);
            listView.addAll(children);
            table.tableRowsInserted(0, children.size());
        }
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
     * @param node провряемая <code>Node</code>
     * @return true, если есть в списке
     */
    public boolean isNodeSelected(Node node) {
        return node != null && dataRef.isInSelItem(node.getObject());
    }

    /**
     * Задание массива строк предыдущего выбора
     * @param oldSelectedRow новый массив строк
     */
    public void setOldSelectedRow(int[] oldSelectedRow) {
        this.oldSelectedRow = oldSelectedRow;
    }

    /**
     * Установить атрибут признака использования мультивыбора узлов с чекбоксами в качестве элементов выбора.
     * @param useCheck новое значение пизнака мультивыбора
     */
    public void setUseCheck(boolean useCheck) {
        this.useCheck = useCheck;
    }

    public void sort() {
        isSort = true;
    }
    
    public void beforeCommitted() {
    }
    
    public void cashCommitted() {
    	((WebComponent)treeTable).removeChange("pr.updateRow");
    	((WebComponent)treeTable).removeChange("pr.reloadRow");
        ((WebComponent)treeTable).sendChangeProperty("reloadTreeTable", 1);
    }
}
