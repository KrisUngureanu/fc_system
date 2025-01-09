package kz.tamur.rt.adapters;

import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrTree2;
import kz.tamur.comps.OrTreeControl2;
import kz.tamur.comps.OrTreeTable2;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.data.Record;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class TreeAdapter2 extends ComponentAdapter {

    public static final int INSERT_CHILD = 0;
    public static final int INSERT_BEFORE = 1;
    public static final int INSERT_AFTER = 2;

    protected List<Node> selectedNodes = new ArrayList<Node>();

    protected static final Kernel krn_ = Kernel.instance();

    protected OrRef titleRef;

    private DefaultTreeModel model;

    protected List<Node> listView = new ArrayList<Node>();
    private Set<Node> expandedNodes = new HashSet<Node>();
    private Map<Long, Set<Node>> expandedNodesByRoot = new HashMap<Long, Set<Node>>();
    private List<TreeListEventListener> tmListeners = new ArrayList<TreeListEventListener>();

    private boolean askAlowsChildren = false;
    private boolean isExpandAll = false;
    private kz.tamur.comps.OrTreeTable2.TreeTableCellRenderer tree;
    private OrGuiComponent tree_;
    private OrFrame frame;
    private boolean wClOk = false;
    protected boolean sorted = false;

    private Stack<Node> expandHierarchy = new Stack<Node>();

    public TreeAdapter2(OrFrame frame, OrGuiComponent tree, boolean isEditor) throws KrnException {
        super(frame, tree, isEditor);
        tree_ = tree;
        this.frame = frame;
        PropertyNode proot = tree.getProperties();

        PropertyValue pv = tree.getPropertyValue(proot.getChild("view").getChild("combonotsorted"));
        if (!pv.isNull()) {
            sorted = !pv.booleanValue();
        }
        PropertyNode pnode = proot.getChild("ref").getChild("language");
        if (pnode != null) {
            pv = tree.getPropertyValue(pnode);
            if (!pv.isNull() && !pv.getKrnObjectId().equals("")) {
                langId = Long.parseLong(pv.getKrnObjectId());
            }
        }

        PropertyNode refNode = proot.getChild("ref");

        pv = tree.getPropertyValue(refNode.getChild("childrenRef"));
        String childrenAttrName = "дети";
        if (!pv.isNull()) {
            String str = pv.stringValue();
            childrenAttrName = str.substring(str.lastIndexOf('.') + 1);
        }
        pv = tree.getPropertyValue(refNode.getChild("data"));
        PathElement2[] paths = com.cifs.or2.client.Utils.parsePath2(pv.stringValue());

        ClassNode cn = krn_.getClassNode(paths[paths.length - 1].type.id);
        dataRef.setChildrenAttr(cn.getAttribute(childrenAttrName));

        KrnAttribute hasChildrenAttr = cn.getAttribute("есть дети?");
        if (hasChildrenAttr != null) {
            askAlowsChildren = true;
            dataRef.setHasChildrenAttr(hasChildrenAttr);
        }

        // Формула для вычисления потомков
        PropertyNode chExprNode = refNode.getChild("childrenExpr");
        if (chExprNode != null) {
            pv = tree.getPropertyValue(chExprNode);
            if (!pv.isNull()) {
                String str = pv.stringValue();
                dataRef.setChildrenExpr(str);
            }
        }

        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath"));
        if (!pv.isNull()) {
            titleRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                    frame);
            OrRef ref = titleRef.getParent();
            while (ref != null && ref != dataRef) {
                ref.setColumn(true);
                ref = ref.getParent();
            }
            titleRef.addOrRefListener(this);
        }
        pv = tree.getPropertyValue(refNode.getChild("defaultFilter"));
        if (!pv.isNull()) {
            dataRef.setDefaultFilter(pv.filterValue().getObjId());
        }

        pv = tree.getPropertyValue(tree.getProperties().getChild("pov").getChild("wClickAsOK"));
        wClOk = pv.booleanValue();
        if (wClOk) {
            if (tree instanceof OrTreeTable2) {
                if (((OrTreeTable2) tree).getTree() != null) {
                    ((OrTreeTable2) tree).getTree().addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                TreeNode tn = (TreeNode) ((OrTreeTable2) TreeAdapter2.this.tree_).getTree()
                                        .getLastSelectedPathComponent();
                                DesignerDialog dlg = (DesignerDialog) ((JComponent) TreeAdapter2.this.frame.getPanel())
                                        .getTopLevelAncestor();
                                if (dlg != null) {
                                    if (dlg.getOkBtn().isEnabled() && tn.isLeaf()) {
                                        dlg.getOkBtn().doClick();
                                    }
                                }
                            }
                        }
                    });
                }
            } else if (tree instanceof OrTreeControl2) {
                ((OrTree2) tree).addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            TreeNode tn = (TreeNode) ((OrTree2) TreeAdapter2.this.tree).getLastSelectedPathComponent();
                            DesignerDialog dlg = (DesignerDialog) ((JComponent) TreeAdapter2.this.frame.getPanel())
                                    .getTopLevelAncestor();
                            if (dlg != null) {
                                if (dlg.getOkBtn().isEnabled() && tn.isLeaf()) {
                                    dlg.getOkBtn().doClick();
                                }
                            }
                        }
                    }
                });
            }
        }
        model = new DefaultTreeModel(null, true);
    }

    public TreeAdapter2(String dataPath, String titlePath, String childrenPath, String childrenExpr, Map<String, OrRef> refs,
            long langId, long filterId, OrFrame frame) throws KrnException {
        super();
        this.frame = frame;
        this.langId = langId;

        dataRef = OrRef.createRef(dataPath, false, Mode.RUNTIME, refs, frame.getTransactionIsolation(), frame);

        ClassNode cn = krn_.getClassNode(dataRef.getType().id);

        if (childrenPath.indexOf('.') > -1) {
            childrenPath = childrenPath.substring(childrenPath.lastIndexOf('.') + 1);
        }
        dataRef.setChildrenAttr(cn.getAttribute(childrenPath));

        if (childrenExpr != null && childrenExpr.length() > 0) {
            dataRef.setChildrenExpr(childrenExpr);
        }

        if (titlePath != null) {
            titleRef = OrRef.createRef(titlePath, true, Mode.RUNTIME, refs, frame.getTransactionIsolation(), frame);
            if (langId > 0) {
                titleRef.addLanguage(langId);
            }
        }
        if (filterId > 0) {
            dataRef.setDefaultFilter(filterId);
        }
        model = new DefaultTreeModel(null, false);
    }

    private void expandNodes(Node node) {
        try {
            node.reload();
        } catch (KrnException exception) {
            exception.printStackTrace();
        }
        List<Node> children = new ArrayList<Node>();
        model.nodeStructureChanged(node);
        if (!expandedNodes.contains(node)) {
            expandedNodes.add(node);
            int index = listView.indexOf(node);
            if (index != -1) {
                Enumeration<Node> enumeration = node.children();
                while (enumeration.hasMoreElements()) {
                    children.add(enumeration.nextElement());
                }
                try {
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    dataRef.insertItems(index + 1, getNodeItems(children), this);
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                } finally {
                    selfChange = false;
                }
                listView.addAll(index + 1, children);
                int indexes[] = new int[node.getChildCount()];
                for (int i = 0; i < indexes.length; i++)
                    indexes[i] = i;
                model.nodesChanged(node, indexes);
            }
            if (tree != null) {
                tree.expandPath(new TreePath(node.getPath()));
            }
            if (tree_ instanceof OrTree2) {
                ((OrTree2) tree_).expandPath(new TreePath(node.getPath()));
            }
        }
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (!selfChange) {
            try {
                OrRef ref = e.getRef();
                if (dataRef == ref) {
                    if (e.getReason() == OrRefEvent.ROOT_ITEM_CHANGED) {
                        // Обновление корня
                        populateRoot();
                    } else if (e.getReason() == OrRefEvent.UPDATED) {
                        // Обновление узлов дерева
                        for (int i = e.getStartIndex(); i < e.getEndIndex(); i++) {
                            Node node = getNodeForRow(i);
                            if (node == null) {
                                continue;
                            }
                            boolean expanded = expandedNodes.contains(node);
                            if (expanded) {
                                expandHierarchy.removeAllElements();
                                nodeCollapsed(node);
                            }
                            expandNodes(node);
                        }
                    } else if (e.getReason() == OrRefEvent.DELETED) {
                        // Обновление узлов дерева
                        for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--) {
                            Node node = getNodeForRow(i);
                            List<Node> nodes = new ArrayList<Node>();
                            countChildren(node, nodes);
                            nodes.add(node);
                            expandedNodes.removeAll(nodes);
                            listView.removeAll(nodes);
                            Node parent = (Node) node.getParent();
                            if (parent != null) {
                                int[] inds = { parent.getIndex(node) };
                                Object[] rchs = { node };
                                parent.remove(node);
                                model.nodesWereRemoved(parent, inds, rchs);
                            }
                        }
                    }
                } else if (titleRef == ref) {
                    // Обновление наименований узлов
                    if (e.getReason() == OrRefEvent.UPDATED || e.getReason() == OrRefEvent.CHANGED) {
                        for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--) {
                            Node node = getNodeForRow(i);
                            ((DefaultTreeModel) getModel()).nodeChanged(node);
                        }
                        if (e.getEndIndex() == -1 && e.getStartIndex() == -1) {
                            Node n = getSelectedNode();
                            ((DefaultTreeModel) getModel()).nodeChanged(n);
                            int row = getRowForNode(n);
                            fireTableRowsUpdate(row, row);
                        } else {
                            fireTableRowsUpdate(e.getStartIndex(), e.getEndIndex());
                        }
                    }
                }
            } catch (KrnException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void populateRoot() throws KrnException {
        Item item = dataRef.getRootItem();
        Node curRoot = (Node) model.getRoot();
        Item curRootItem = (curRoot != null) ? curRoot.item : null;
        if (item != null && item.getRec() != null && item != curRootItem) {
            try {
                selfChange = true;
                final Node root = new Node(item, 0);
                // expandedNodes.clear();
                KrnObject rootObj = (KrnObject) item.getCurrent();
                expandedNodes = expandedNodesByRoot.get(rootObj.id);
                if (expandedNodes == null) {
                    expandedNodes = new HashSet<Node>();
                    expandedNodesByRoot.put(rootObj.id, expandedNodes);
                }
                listView.clear();
                listView.add(root);
                dataRef.insertItems(0, Collections.singletonList(root.item), this);
                model.setRoot(root);
                root.reload();
                expandedNodes.remove(root);
                if (tree != null) {
                    tree.collapseRow(0);
                }
                if (tree_ instanceof OrTreeControl2) {
                    ((OrTreeControl2)tree_).collapseRow(0);
                }
                expandAll();
            } catch (KrnException ex) {
                ex.printStackTrace();
            } finally {
                selfChange = false;
            }
        } else {
            // Если нет данных, то очистить содержимое дерева
            expandedNodes.clear();
            listView.clear();
        }
    }

    public void populateRootForReport() throws KrnException {
        Item item = dataRef.getRootItem();
        Node curRoot = (Node) model.getRoot();
        Item curRootItem = (curRoot != null) ? curRoot.item : null;
        if (item != null && item.getRec() != null && item != curRootItem) {
            Node root = new Node(item, 0);
            model.setRoot(root);
            try {
                selfChange = true;
                // Node root = (Node)model.getRoot();
                if (root != null) {
                    root.reload();
                    model.nodeStructureChanged(root);
                    expandedNodes.clear();
                    expandedNodes.add(root);
                    listView.clear();
                    listView.add(root);
                    listView.addAll(getAllChildren(root));
                    dataRef.setItems(0, getNodeItems(listView), this);
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            } finally {
                selfChange = false;
            }
        }
    }

    public void clear() {
    }

    public void setDefaultFilterId(long filterId) throws KrnException {
        dataRef.setDefaultFilter(filterId);
    }

    /**
     * Создает новый узел в модели под текущим узлов. Если не выделен ни один
     * узел, то ничего не происходит.
     * 
     * @param title
     *            Наименование создаваемого узла.
     * @param position
     *            Расположение создаваемого узла. INSERT_CHILD - добавить в
     *            конец списка дочерних узлов выбранного узла. INSERT_BEFORE -
     *            вставить перед выбранным узлом. INSERT_AFTER - вставить после
     *            выбранного узла.
     * @return Созданный узел.
     * @throws KrnException
     */
    public Node createNode(String title, int position, Object originator) throws KrnException {
        Node node = getSelectedNode();
        if (node != null) {
            if (!expandedNodes.contains(node)) {
                expandNodes(node);
            }
            // индекс узла с поправкой на все раскрытые внутри него
            List<Node> nodes = new ArrayList<Node>();
            countChildren(node, nodes);
            int count = nodes.size();
            // Определяем индекс нового узла
            int i = node.getChildCount();
            if (position != INSERT_CHILD) {
                Node parent = (Node) node.getParent();
                if (parent == null) {
                    return null;
                }
                int index = parent.getIndex(node);
                i = (position == INSERT_BEFORE) ? index : index + 1;
                node = parent;
            }
            try {
                selfChange = true;
                boolean calcOwner = OrCalcRef.setCalculations();
                Item item = node.item.createChild(i, originator);
                Node newNode = new Node(item, node.getLevel() + 1);
                int pos = node.item.index + count + 1;
                // int pos = count+1;
                // if (expandedNodes.contains(node)) {
                dataRef.insertItems(pos, Collections.singletonList(item), this);
                // Считаем что dataRef является родителем titleRef
                titleRef.insertItem(item, 0, title, originator, this, false);
                listView.add(pos, newNode);
                // }
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
                model.insertNodeInto(newNode, node, i);
                if (count == 0 && tree_ != null) {
                    ((OrTree2) tree_).expandPath(new TreePath(node.getPath()));
                }
                model.nodeChanged(newNode);
                return newNode;
            } finally {
                selfChange = false;
            }
        }
        return null;
    }

    /**
     * Удаляет текущий узел. Если не выделен ни один узел, то ничего
     * не происходит.
     * 
     * @param title
     *            Наименование создаваемого узла.
     * @return Удаленный узел. NULL если ничего не удалилось.
     * @throws KrnException
     */
    public void deleteNodes(Object originator) throws KrnException {
        Node[] nodes = getSelectedNodes();
        for (Node node : nodes) {
            Node parent = (Node) node.getParent();
            if (parent != null) {
                try {
                    if (expandedNodes.contains(node)) {
                        nodeCollapsed(node);
                    }
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    parent.item.removeChild(node.item, originator);
                    // if (expandedNodes.contains(parent)) {
                    int pos = node.item.index;
                    dataRef.deleteItems(pos, pos + 1, originator);
                    // Считаем что dataRef является родителем titleRef
                    // titleRef.deleteItems(pos, pos + 1, originator);
                    listView.remove(pos);
                    // }
                    if (calcOwner) {
                        OrCalcRef.makeCalculations();
                    }
                    model.removeNodeFromParent(node);
                } finally {
                    selfChange = false;
                }
            }
        }
    }

    public void renameNode(String title, Object originator) throws KrnException {
        Node node = getSelectedNode();
        if (node != null) {
            try {
                selfChange = true;
                OrRef.Item titleItem = titleRef.getItem(langId, node.item);
                titleRef.changeItem(titleItem, title, this, originator);
                model.nodeChanged(node);
                int pos = getRowForNode(node);
                fireTableRowsUpdate(pos, pos);
            } finally {
                selfChange = false;
            }
        }
    }

    public Node getSelectedNode() {
        return selectedNodes.size() > 0 ? selectedNodes.get(0) : null;
    }

    public void setSelectedNode(Node node, Object originator) throws KrnException {
        selectedNodes.clear();
        selectedNodes.add(node);
    }

    public Node[] getSelectedNodes() {
        return selectedNodes.toArray(new Node[selectedNodes.size()]);
    }

    public void setSelectedNodes(Node[] nodes) {
        selectedNodes.clear();
        for (Node node : nodes) {
            selectedNodes.add(node);
        }
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) throws KrnException {
        // this.langId = langId;
    }

    public boolean isOrderSupported() {
        return dataRef.getChildrenAttr().collectionType == COLLECTION_ARRAY;
    }

    public int getRowCount() {
        return listView.size();
    }

    public Node getNodeForRow(int row) {
        return (listView.size() > row) ? listView.get(row) : null;
    }

    public int getRowForObjectId(long objId) {
        for (int i = 0; i < listView.size(); i++) {
            Node node = listView.get(i);
            if (objId == node.getObject().id) {
                return i;
            }
        }
        return -1;
    }

    public Node getNodeForObjectId(long objId) {
        for (Node node : listView) {
            if (objId == node.getObject().id) {
                return node;
            }
        }
        return null;
    }

    public int getRowForNode(Node node) {
        return listView.indexOf(node);
    }

    public int[] nodeCollapsed(Node node) {
        if (expandedNodes.remove(node)) {
            int index = getRowForNode(node);
            if (index != -1) {
                List<Node> children = new LinkedList<Node>();
                countChildrenFirstLevel(node, children);
                for (int i = 0; i < children.size(); i++) {
                    Node child = children.get(i);
                    if (expandedNodes.contains(child)) {
                        expandHierarchy.push(child);
                        nodeCollapsed(child);
                    }
                }
                int count = children.size();
                for (int i = index + count; i > index; i--) {
                    listView.remove(i);
                }
                try {
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    dataRef.deleteItems(index + 1, index + 1 + count, this);
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                } finally {
                    selfChange = false;
                }
                return new int[] { index + 1, index + 1 + count };
            }
        }
        return null;
    }

    public int[] nodeExpanded(Node node) {
        if (!expandedNodes.contains(node)) {
            expandedNodes.add(node);
            int index = listView.indexOf(node);
            if (index != -1) {
                List<Node> children = new LinkedList<Node>();
                countChildren(node, children);
                try {
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    dataRef.insertItems(index + 1, getNodeItems(children), this);
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                } finally {
                    selfChange = false;
                }
                listView.addAll(index + 1, children);
                int inds[] = new int[node.getChildCount()];
                for (int i = 0; i < inds.length; i++) {
                    inds[i] = i;
                }
                model.nodesChanged(node, inds);

                if (tree != null) {
                    for (int i = 0; i < children.size(); i++) {
                        Node n = children.get(i);
                        if (expandedNodes.contains(n)) {
                            tree.expandRow(index + 1 + i);
                        }
                    }
                }
                return new int[] { index + 1, index + children.size() };
            }
        }
        return null;
    }

    private List<Item> getNodeItems(List<Node> nodes) {
        List<Item> items = new ArrayList<Item>();
        for (Node node : nodes)
            items.add(node.item);
        return items;
    }

    public void addTableModelListener(TreeListEventListener l) {
        tmListeners.add(l);
    }

    private void fireTableRowsUpdate(int firstRow, int lastRow) {
        for (TreeListEventListener tmListener : tmListeners)
            tmListener.rowsUpdated(firstRow, lastRow);
    }

    private void countChildrenFirstLevel(Node node, List<Node> children) {
        Enumeration<Node> chs = node.children();
        while (chs.hasMoreElements()) {
            Node child = chs.nextElement();
            children.add(child);
        }
    }

    private void countChildren(Node node, List<Node> children) {
        Enumeration<Node> chs = node.children();
        while (chs.hasMoreElements()) {
            Node child = chs.nextElement();
            children.add(child);
            if (expandedNodes.contains(child)) {
                try {
                    child.loadEx();
                } catch (Exception e) {
                }
                countChildren(child, children);
            }
        }
    }

    private List<Node> getChildren(Node node) {
        int cnt = node.getChildCount();
        List<Node> res = new ArrayList<Node>(cnt);
        for (int i = 0; i < cnt; i++) {
            res.add((Node) node.getChildAt(i));
        }
        return res;
    }

    private List<Node> getAllChildren(Node node) {
        int cnt = node.getChildCount();
        List<Node> res = new ArrayList<Node>(cnt);
        for (int i = 0; i < cnt; i++) {
            Node child = (Node) node.getChildAt(i);
            res.add(child);
            res.addAll(getAllChildren(child));
        }
        return res;
    }

    public class Node extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        public OrRef.Item item;

        public boolean isLoaded;

        private int level = 0;

        public Node(OrRef.Item item, int level) {
            this.item = item;
            this.level = level;
            isLoaded = false;
        }

        public KrnObject getObject() {
            return (item != null) ? (KrnObject) item.getCurrent() : null;
        }

        public int getChildCount() {
            try {
                loadEx();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getChildCount();
        }

        public String toString(int row) {
            Item titleItem = titleRef.getItem(langId, row);
            return titleItem != null ? titleItem.getCurrent() + "" : null;
        }

        public String toString() {
            return "(" + item.title + ", " + item.index + ", " + level + ")";
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Node) {
                OrRef.Item item = ((Node) obj).item;
                if (item == this.item) {
                    return true;
                } else if (item != null) {
                    if (this.item != null) {
                        return item.equals(this.item);
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (item != null)
                return item.hashCode();
            else
                return super.hashCode();
        }

        public int getItemIndex() {
            return (item != null) ? item.getIndex() : -1;
        }

        public int getLevel() {
            return level;
        }

        public TreePath find(KrnObject obj, boolean loadedOnly) throws KrnException {

            if (obj == null)
                return null;

            if (item != null && ((KrnObject) item.getCurrent()).id == obj.id)
                return new TreePath(getPath());
            else {
                TreePath result = null;
                if (!loadedOnly) {
                    loadEx();
                }
                for (Enumeration c = children(); c.hasMoreElements();) {
                    Node child = (Node) c.nextElement();
                    result = child.find(obj, loadedOnly);
                    if (result != null)
                        break;
                }
                return result;
            }
        }

        public TreePath find(long objId, boolean loadedOnly) throws KrnException {

            if (item != null && ((KrnObject) item.getCurrent()).id == objId)
                return new TreePath(getPath());
            else {
                TreePath result = null;
                if (!loadedOnly) {
                    loadEx();
                }
                for (Enumeration c = children(); c.hasMoreElements();) {
                    Node child = (Node) c.nextElement();
                    result = child.find(objId, loadedOnly);
                    if (result != null)
                        break;
                }
                return result;
            }
        }

        private void loadEx() throws KrnException {
            if (!isLoaded) {
                isLoaded = true;
                if (item == null)
                    return;
                // Загрузка детей
                List<OrRef.Item> children = item.getChildren(TreeAdapter2.this);

                if (sorted) {
                    Long[] ids = new Long[children.size()];
                    long[] ids2 = new long[children.size()];
                    Map<Long, OrRef.Item> items = new HashMap<Long, OrRef.Item>();
                    Map<Long, String> titles = new HashMap<Long, String>();
                    int i = 0;
                    for (OrRef.Item child : children) {
                        KrnObject obj = (KrnObject) child.getCurrent();
                        if (obj != null) {
                            ids[i] = obj.id;
                            ids2[i++] = obj.id;
                            items.put(obj.id, child);
                        }
                    }
                    long lid = langId;
                    KrnAttribute attr = titleRef.getAttribute();
                    if (lid == 0 && attr.isMultilingual) {
                        KrnObject lang = frame.getInterfaceLang();
                        lid = (lang != null) ? lang.id : langId;
                    }
                    SortedSet<Record> recs = dataRef.getCash().getRecords(ids2, attr, langId, null);
                    for (Record rec : recs) {
                        String title = (String) rec.getValue();
                        titles.put(rec.getObjId(), title);
                    }
                    Arrays.sort(ids, new NodeComparator<Long>(titles));
                    for (Long id : ids) {
                        add(new Node(items.get(id), level + 1));
                    }
                } else {
                    for (OrRef.Item child : children) {
                        add(new Node(child, level + 1));
                    }
                }
            }
        }

        public void reload() throws KrnException {
            removeAllChildren();
            isLoaded = false;
            item.reset();
            loadEx();
        }

        @Override
        public boolean getAllowsChildren() {
            if (askAlowsChildren)
                return item.hasChildren;
            return getChildCount() > 0;
        }

    }

    public TreeModel getModel() {
        return model;
    }

    public OrRef getTitleRef() {
        return titleRef;
    }

    public List<Node> getListView() {
        return listView;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        getComponent().setEnabled(isEnabled);
    }

    /**
     * Развёртка всего дерева
     */
    public void expandAll() {
        if (isExpandAll) {
            expandAll((Node) model.getRoot(), tree);
        }
    }

    /**
     * Развертка ветки дерева (рекурсия)
     * 
     * @param node
     *            ветка, с которой необходимо провести развёртку
     * @param tree
     *            рендер таблицы
     */
    public void expandAll(Node node, kz.tamur.comps.OrTreeTable2.TreeTableCellRenderer tree) {
        if (node != null && !node.isLeaf()) {
            nodeExpanded(node);
            tree.expandPath(new TreePath(node.getPath()));
            Enumeration childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                Node child = (Node) childNodes.nextElement();
                expandAll(child, tree);
            }
        }
    }

    /**
     * Проверяет, необходимо ли разворачивать дерево при выводе
     * 
     * @return true, если expand all
     */
    public boolean isExpandAll() {
        return isExpandAll;
    }

    /**
     * Установить параметр необходимости развёртки дерева при выводе.
     * 
     * @param isExpandAll
     *            the new expand all
     */
    public void setExpandAll(boolean isExpandAll) {
        this.isExpandAll = isExpandAll;
    }

    /**
     * Задать рендер таблицы
     * 
     * @param tree
     *            the tree to set
     */
    public void setTree(kz.tamur.comps.OrTreeTable2.TreeTableCellRenderer tree) {
        this.tree = tree;
        if (tree != null && wClOk) {
            tree.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        TreeNode tn = (TreeNode) TreeAdapter2.this.tree.getLastSelectedPathComponent();
                        DesignerDialog dlg = (DesignerDialog) ((JComponent) TreeAdapter2.this.frame.getPanel())
                                .getTopLevelAncestor();
                        if (dlg != null) {
                            if (dlg.getOkBtn().isEnabled() && tn.isLeaf()) {
                                dlg.getOkBtn().doClick();
                            }
                        }
                    }
                }
            });
        }
    }

    private class NodeComparator<Long> implements Comparator<Long> {
        private Map<Long, String> titles;

        public NodeComparator(Map<Long, String> titles) {
            this.titles = titles;
        }

        @Override
        public int compare(Long o1, Long o2) {
            String s1 = titles.get(o1);
            String s2 = titles.get(o2);

            if (s1 == null && s2 == null)
                return 0;
            else if (s1 == null && s2 != null)
                return 1;
            else if (s1 != null && s2 == null)
                return -1;
            else
                return s1.compareTo(s2);
        }
    }
}
