package kz.tamur.rt.adapters;

import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;

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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.ods.Value;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent2;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent2;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.data.Record;
import kz.tamur.util.Funcs;
import kz.tamur.web.component.OrWebTree2;
import kz.tamur.web.component.OrWebTreeTable2;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class TreeAdapter2 extends ComponentAdapter {

    public static final int INSERT_CHILD = 0;
    public static final int INSERT_BEFORE = 1;
    public static final int INSERT_AFTER = 2;

    protected List<Node> selectedNodes = new ArrayList<Node>();

    private Kernel krn_;

    private OrRef titleRef;
    private OrRef titleRef2;
    private OrRef sortRef;

    private DefaultTreeModel model;

    private List<Node> listView = new ArrayList<Node>();
    private Set<Node> expandedNodes = new HashSet<Node>();
    private Map<Long, Set<Node>> expandedNodesByRoot = new HashMap<Long, Set<Node>>();

    private List<TreeListEventListener> tmListeners = new ArrayList<TreeListEventListener>();

    private boolean askAlowsChildren = false;
    private boolean isExpandAll = false;
    private boolean wClickAsOK;
    protected OrWebTree2 tree;
    protected boolean sorted = false;

    public TreeAdapter2(OrFrame frame, OrGuiComponent tree, boolean isEditor) throws KrnException {
        super(frame, tree, isEditor);
        krn_ = frame.getKernel();

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
            String str = pv.stringValue(frame.getKernel());
            childrenAttrName = str.substring(str.lastIndexOf('.') + 1);
        }
        
        pv = tree.getPropertyValue(refNode.getChild("data"));
        PathElement2[] paths = com.cifs.or2.client.Utils.parsePath2(pv.stringValue(frame.getKernel()), frame.getKernel());

        ClassNode cn = krn_.getClassNode(paths[paths.length - 1].type.id);
        dataRef.setChildrenAttr(cn.getAttribute(childrenAttrName));

        pv = tree.getPropertyValue(refNode.getChild("hasChildrenRef"));
        String hasChildrenAttrName = "есть дети?";
        if (!pv.isNull()) {
            String str = pv.stringValue(frame.getKernel());
            hasChildrenAttrName = str.substring(str.lastIndexOf('.') + 1);
        }

        KrnAttribute hasChildrenAttr = cn.getAttribute(hasChildrenAttrName);
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
            titleRef = OrRef.createRef(pv.stringValue(frame.getKernel()), true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            OrRef ref = titleRef.getParent();
            while (ref != null && ref != dataRef) {
                ref.setColumn(true);
                ref = ref.getParent();
            }
            titleRef.addOrRefListener(this);
        }        
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath2"));
        if (!pv.isNull()) {
            titleRef2 = OrRef.createRef(pv.stringValue(frame.getKernel()), true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
            OrRef ref = titleRef2.getParent();
            while (ref != null && ref != dataRef) {
                ref.setColumn(true);
                ref = ref.getParent();
            }
            titleRef2.addOrRefListener(this);
        }
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("sortPath"));
        if (!pv.isNull()) {
            sortRef = OrRef.createRef(pv.stringValue(frame.getKernel()), false, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            OrRef ref = sortRef.getParent();
            while (ref != null && ref != dataRef) {
                ref.setColumn(false);
                ref = ref.getParent();
            }
            sortRef.addOrRefListener(this);
        }
        pv = tree.getPropertyValue(refNode.getChild("defaultFilter"));
        if (!pv.isNull()) {
            dataRef.setDefaultFilter(pv.filterValue().getObjId());
            //String fuid = krn.getUId(fRecord.getObjId());
            frame.getKernel().addFilterParamListener(pv.filterValue().getKrnObject().uid, "", this);
        }

        pv = tree.getPropertyValue(tree.getProperties().getChild("pov").getChild("wClickAsOK"));
        wClickAsOK = pv.booleanValue();
        model = new DefaultTreeModel(null, true);
        dataRef.getCash().addCashListener(new TreeCashListener(), frame);
    }

    public TreeAdapter2(String dataPath, String titlePath, String childrenPath, String childrenExpr, Map<String, OrRef> refs, long langId, long filterId, OrFrame frame) throws KrnException {
    	this(dataPath, titlePath, null, childrenPath, childrenExpr, refs, langId, filterId, frame);
    	
    }
    public TreeAdapter2(String dataPath, String titlePath, String sortPath, String childrenPath, String childrenExpr, Map<String, OrRef> refs, long langId, long filterId, OrFrame frame) throws KrnException {
        super();
        this.frame = frame;
        log = getLog();
        this.langId = langId;
        krn_ = frame.getKernel();

        dataRef = OrRef.createRef(dataPath, false, Mode.RUNTIME, refs, frame.getTransactionIsolation(), frame);

        ClassNode cn = krn_.getClassNode(dataRef.getType().id);
        
        if (childrenPath.contains(".")) {
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
        if (sortPath != null) {
            sortRef = OrRef.createRef(sortPath, false, Mode.RUNTIME, refs, frame.getTransactionIsolation(), frame);
        }
        if (filterId > 0) {
            dataRef.setDefaultFilter(filterId);
        }
        model = new DefaultTreeModel(null, false);
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
                            if (tree.getTreeTable() != null) {
                                tree.getTreeTable().setDoNotUpdateRows(true);
                            }
                            boolean expanded = expandedNodes.contains(node);
                            if (expanded) {
                                nodeCollapsed(node);
                            }
                            int count = node.getChildCount();
                            node.reload();
                            model.nodeStructureChanged(node);
                            if (expanded || count != node.getChildCount()) {
                                nodeExpanded(node);
                            }

                            JsonArray arr = new JsonArray();
                            JsonObject obj = new JsonObject();
                            obj.add("index", node.getObject().id);
                            arr.add(obj);

                            OrWebTreeTable2 tt = tree.getTreeTable();

                            if (tt != null) {
                                tt.setDoNotUpdateRows(false);
                                tt.removeChange("pr.updateRow", node.getObject().id);
                                JsonValue r = tt.getChange("pr.reloadTreeTable");
                                if (r == null) {
                                	if (expanded) {
                                        List<Node> nodes = new ArrayList<Node>();
                                        countChildren(node, nodes);
                                        for (Node ch : nodes) {
                                        	tt.removeChange("pr.reloadRow", ch.getObject().id);
                                        }
                                	}
                                	tt.sendChangeProperty("reloadRow", arr);
                                }
                            } else {
                                tree.sendChangeProperty("reloadNode", arr);
                            }
                        }
                    } else if (e.getReason() == OrRefEvent.DELETED) {
                        // Обновление узлов дерева
                        for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--) {
                            Node node = getNodeForRow(i);
                            if (node == null) {
                                continue;
                            }
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
                        TreeTableAdapter2 tta = null;
                        OrGuiComponent comp = getComponent();
                        if (comp instanceof OrTreeComponent2) {
                        	tta = ((OrTreeComponent2) comp).getTableAdapter();
                        } else if (comp instanceof OrTreeTableComponent2) {
                        	tta = (TreeTableAdapter2) ((OrTreeTableComponent2) comp).getAdapter();
                        }
                        if (tta != null) {
                            tta.countCurrentTableItem();
                        }
                    }
                } else if (titleRef == ref) {
                    // Обновление наименований узлов
                    if (e.getReason() == OrRefEvent.UPDATED || e.getReason() == OrRefEvent.CHANGED) {
                        for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--) {
                            Node node = getNodeForRow(i);
                            if (node == null) continue;
                            ((DefaultTreeModel) getModel()).nodeChanged(node);

                            JsonArray arr = new JsonArray();
                            JsonObject obj = new JsonObject();
                            obj.add("index", node.getObject().id);
                            obj.add("title", node.toString(i));
                            arr.add(obj);
                            tree.sendChangeProperty("setNodeTitle", arr);
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
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
	public void filterParamChanged(String fuid, String pid, List<?> values) {
    	model.setRoot(null);
    	try {
    		dataRef.clearFilteredObjects();
    		populateRoot();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	private void populateRoot() throws KrnException {
        Item item = dataRef.getRootItem();
        Node curRoot = (model.getRoot() instanceof Node) ? (Node) model.getRoot() : null;
        Item curRootItem = (curRoot != null) ? curRoot.item : null;
        if (item != null && item.getRec() != null && item != curRootItem) {
            try {
                selfChange = true;
                final Node root = new Node(item, 0);
                KrnObject rootObj = (KrnObject) item.getCurrent();
                expandedNodes = expandedNodesByRoot.get(rootObj.id);
                if (expandedNodes == null) {
                	expandedNodes = new HashSet<Node>();
                	expandedNodesByRoot.put(rootObj.id, expandedNodes);
                }
                root.reload();
                listView.clear();
                listView.add(root);
                model.setRoot(root);
                model.nodeStructureChanged(root);
                dataRef.insertItems(0, Collections.singletonList(root.item), this);
                
                expandedNodes.remove(root);
                nodeExpanded(root);

                if (tree != null && tree.getTreeTable() != null){
                    tree.getTreeTable().tableDataChanged();
                }
                expandAll();
            } catch (KrnException ex) {
                ex.printStackTrace();
            } finally {
                selfChange = false;
            }
        }else {
            // если нет данных, то очистить содержимое дерева
            expandedNodes.clear();
            listView.clear();
            if (curRoot != null) {
                model.setRoot(null);
                if (tree != null && tree.getTreeTable() != null){
                    tree.getTreeTable().tableDataChanged();
                }
            }
        }
        // сброс выделенной строчки
        if (tree != null && tree.getTreeTable() != null) {
        	if (dataRef != null) {
        		int[] selInds = dataRef.getPreviousSelectedIndexes();
        		if (selInds.length > 0) {
        			dataRef.absolute(selInds[0], this);
        			dataRef.setSelectedItems(selInds);
        		}
        	}
            tree.getTreeTable().setSelectedRows(new int[]{});
        }
    }

    public void populateRootForReport() throws KrnException {
        Item item = dataRef.getRootItem();
        Node curRoot = (Node) model.getRoot();
        Item curRootItem = (curRoot != null) ? curRoot.item : null;
        if (item != null && item.getRec() != null && item != curRootItem) {
            Node root = new Node(item, 0);// (Node)model.getRoot();
            model.setRoot(new Node(item, 0));
            try {
                selfChange = true;
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
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                selfChange = true;
                Item item = node.item.createChild(i, originator);
                Node newNode = new Node(item, node.getLevel() + 1);
                int pos = node.item.index + i + 1;
                if (expandedNodes.contains(node)) {
                    dataRef.insertItems(pos, Collections.singletonList(item), this);
                    // Считаем что dataRef является родителем titleRef
                    titleRef.insertItem(item, 0, title, originator, this, false);
                    if(sortRef!=null) sortRef.insertItem(item, 0, pos, originator, this, false);
                    listView.add(pos, newNode);
                }
                model.insertNodeInto(newNode, node, i);
                model.nodeChanged(newNode);
                return newNode;
            } finally {
                selfChange = false;
                if (calcOwner)
                    OrCalcRef.makeCalculations();
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
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    selfChange = true;
                    parent.item.removeChild(node.item, originator);
                    if (expandedNodes.contains(parent)) {
                        int pos = node.item.index;
                        dataRef.deleteItems(pos, pos + 1, originator);
                        // Считаем что dataRef является родителем titleRef
                        titleRef.deleteItems(pos, pos + 1, originator);
                        if(sortRef!=null) sortRef.deleteItems(pos, pos + 1, originator);
                        listView.remove(pos);
                    }
                    model.removeNodeFromParent(node);
                } finally {
                    selfChange = false;
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
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
    
    public void treeSelectionChanged() {
        try {
            if (!selfChange) {
                Node n = getSelectedNode();
                if (n != null) {
                	boolean calcOwner = OrCalcRef.setCalculations();
                	try {
	                    OrRef.Item item = dataRef.getItem(0);
	                    if (item == null)
	                        dataRef.insertItem(0, n.getObject(), this, this, false);
	                    else
	                        dataRef.changeItem(n.getObject(), this, this);
                	} catch (Exception e) {
                		e.printStackTrace();
                	} finally {
	        			if (calcOwner)
	        				OrCalcRef.makeCalculations();
                	}
                }
                Node[] nodes = getSelectedNodes();
                KrnObject[] objs = new KrnObject[nodes.length];
                for (int i = 0; i < nodes.length; ++i)
                    objs[i] = nodes[i].getObject();
                dataRef.setSelectedItems(objs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public long getLangId() {
        return 0;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public boolean isOrderSupported() {
        return dataRef.getChildrenAttr().collectionType == COLLECTION_ARRAY;
    }

    public int getRowCount() {
        return listView.size();
    }

    public Node getNodeForRow(int row) {
        return listView.size() > row ? listView.get(row) : null;
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

    public void moveUp(long objId) throws KrnException{
    	if(sortRef==null) return;
    	Node node=getNodeForObjectId(objId);
        if (node != null) {
        	Node parent=(Node)node.getParent();
            if (parent!=null) {
                try {
                    selfChange = true;
    	        	int sort_index_node=parent.getIndex(node);
                    if(sort_index_node<1) return;
                    Node nodep=(Node)parent.getChildAt(sort_index_node-1);
    	        	parent.remove(node);
    	        	parent.insert(node, sort_index_node-1);
    	        	Map<Long,Long> values=new HashMap<Long,Long>();
    	        	//Значение индекса в атрибуте на 1 больше чем индекс в родительском узле(начинается не с 0 а с 1)
    	        	values.put(((KrnObject)nodep.item.getRec().getValue()).id,(long)sort_index_node+1);
    	        	values.put(objId,(long)sort_index_node);
    	        	parent.changeSortItems(values);
    	        	model.nodeChanged(node);
				} finally {
                    selfChange = false;
                }
            }
        }
    }

    public void moveDown(long objId) throws KrnException{
    	if(sortRef==null) return;
    	Node node=getNodeForObjectId(objId);
        if (node != null) {
        	Node nnode = (Node)node.getNextNode();
        	Node parent=(Node)node.getParent();
            if (parent!=null && nnode!=null) {
                try {
                    selfChange = true;
    	        	int sort_index_node=parent.getIndex(node);
                    if(sort_index_node>=parent.getChildCount()-1) return;
                    Node noden=(Node)parent.getChildAt(sort_index_node+1);
    	        	parent.remove(node);
    	        	parent.insert(node, sort_index_node+1);
    	        	Map<Long,Long> values=new HashMap<Long,Long>();
    	        	//Значение индекса в атрибуте на 1 больше чем индекс в родительском узле(начинается не с 0 а с 1)
    	        	values.put(((KrnObject)noden.item.getRec().getValue()).id,(long)sort_index_node+1);
    	        	values.put(objId,(long)sort_index_node+2);
    	        	parent.changeSortItems(values);
    	        	model.nodeChanged(node);
				} finally {
                    selfChange = false;
                }
            }
        }
    }
    public int[] nodeCollapsed(Node node) {
        // выбрать сворачиваевымый узел 
        try {
            setSelectedNode(node,null);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (expandedNodes.remove(node)) {
            int index = getRowForNode(node);
            if (index != -1) {
                List<Node> children = new LinkedList<Node>();
                countChildren(node, children);
                int count = children.size();
                for (int i = index + count; i > index; i--) {
                    if (i < listView.size())
                        listView.remove(i);
                }
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    selfChange = true;
                    dataRef.deleteItems(index + 1, index + 1 + count, this);
                } finally {
                    selfChange = false;
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
                }
                return new int[] { index + 1, index + 1 + count };
            }
        }
        return null;
    }

    public int[] nodeExpanded(Node node) {
    	try {
            setSelectedNode(node,null);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        if (!expandedNodes.contains(node)) {
            expandedNodes.add(node);
            // ((OrWebTree2)comp).changeState(new TreePath(node.getPath()));
            int index = listView.indexOf(node);
            if (index != -1) {
                List<Node> children = new LinkedList<Node>();
                countChildren(node, children);
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    selfChange = true;
                    dataRef.insertItems(index + 1, getNodeItems(children), this);
                } finally {
                    selfChange = false;
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
                }
                listView.addAll(index + 1, children);
                int inds[] = new int[node.getChildCount()];
                for (int i = 0; i < inds.length; i++)
                    inds[i] = i;
                model.nodesChanged(node, inds);

                if (tree != null) {
	                for (int i=0; i<children.size(); i++) {
	                	Node n = children.get(i);
	                	if (n.children().hasMoreElements()) {
	                        tree.expandPath(tree.getPathForRow(index + 1 + i));
	                	}
	                }
                }

                return new int[] { index + 1, index + children.size() };
            }
        }
        return null;
    }

    public boolean isNodeExpanded(Node node) {
        return expandedNodes.contains(node);
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

    private void countChildren(Node node, List<Node> children) {
        Enumeration<Node> chs = node.children();
        while (chs.hasMoreElements()) {
            Node child = chs.nextElement();
            children.add(child);
            if (expandedNodes.contains(child)) {
            	try {
            		child.loadEx();
            	} catch (Exception e) {
            		e.printStackTrace();
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
            String val = "";
        	Item titleItem = titleRef.getItem(langId, row);
        	if (titleItem != null) {
        		val = (String) titleItem.getCurrent();
        	}
        	if ("".equals(val) && titleRef2 != null) {
        		Item titleItem2 = titleRef2.getItem(langId, row);
        		if (titleItem2 != null) {
            		val = (String) titleItem2.getCurrent();
        		}
        	}
        	return val;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Node) {
                OrRef.Item item_ = ((Node) obj).item;
                if (item_ == item) {
                    return true;
                } else if (item_ != null) {
                    if (item != null) {
                        return item_.equals(item);
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return item == null ? super.hashCode() : item.hashCode();
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
        
        public JsonArray findTitleByObj(KrnObject obj, String txt) {
        	long start = System.currentTimeMillis();
        	JsonArray result = new JsonArray();
        	try {
        		KrnObject[] objs = null;
        		KrnClass cls = krn_.getClassById(obj.classId);
        		objs = krn_.getClassObjects(cls, 0);
        		if(objs.length == 1) {
        			cls = krn_.getClassById(cls.parentId);        	
        			objs = krn_.getClassObjects(cls, 0);
        		}
        		if(objs.length > 0) {
        			long[] objIds = new long[objs.length];
        			for(int i = 0; i<objs.length;i++) {
        				objIds[i] = objs[i].id;
        			}
        			String attrName = titleRef.getAttr().name;
        			AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn_).add("id").add(attrName).add("дети");
        			List<Object[]> rows = krn_.getObjects(objIds, arb.build(), 0);
        			Map<Long, String> titles = new HashMap<Long,String>();
        			Map<Long, KrnObject[]> childrenList = new HashMap<Long,KrnObject[]>();
        			int idx = 0;
        			for(Object[] row : rows) {
        				long id = arb.getLongValue("id", row);
        				titles.put(objIds[idx], arb.getStringValue(attrName, row));
        				KrnObject[] childObjs = null;
        				List<Value> childs =(List<Value>) arb.getValue("дети", row);        			
        				if (childs != null) {
        					childObjs = new KrnObject[childs.size()];
        					for (int i = 0; i < childs.size(); i++) {
        						childObjs[i] = (KrnObject) childs.get(i).value;
        					}	                    
        				}
        				childrenList.put(objIds[idx], childObjs);
        				idx++;
        			}

        			findTitleByObj(obj.id, "", txt, titles, childrenList, result);  

        			KrnObject obj1 = objs[0];
        		}
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
//        	findTitleByObj(obj, "", txt, result);
        	System.out.println((System.currentTimeMillis() - start) + " мс");
        	
        	JsonArray sortedRes = new JsonArray();
        	List<JsonObject> resList = new ArrayList<JsonObject>();
        	for(int i = 0; i<result.size(); i++) {
        		resList.add((JsonObject) result.get(i));        		
        	}
        	
        	Collections.sort(resList, new Comparator<JsonObject>() {

				@Override
				public int compare(JsonObject o1, JsonObject o2) {
					String s1 = o1.get("parentNodes").toString();
					String s2 = o2.get("parentNodes").toString();
					
					return s1.compareTo(s2);
				}        		
        	});
        	for(JsonObject res: resList) {
        		sortedRes.add(res);
        	}
        	
        	return sortedRes;
        }
        
        public void findTitleByObj(long objId, String parIds, String txt, Map<Long, String> titles, Map<Long, KrnObject[]> children, JsonArray result) {
        	if(titles.get(objId).toUpperCase(Constants.OK).contains(txt.toUpperCase(Constants.OK))) {
        		JsonObject res = new JsonObject();
				res.add("parentNodes", parIds.length() > 0 ? (parIds.substring(0, parIds.length() - 1)): "");
            	res.add("node", objId);
            	result.add(res);
        	}
        	
        	KrnObject[] childs = children.get(objId);
        	if(childs != null && childs.length > 0) {
        		for(KrnObject child: childs) {
        			findTitleByObj(child.id, parIds + objId + ",", txt, titles, children, result);
        		}
        		
        	}
        }
        
        public List<TreePath> findTitle(String text, int childIndex, boolean loadedOnly) throws KrnException {
        	List<TreePath> result = new ArrayList<TreePath>();
        	if (childIndex == -1) {
        		long[] ids = new long[] {getObject().id};
        		KrnAttribute attr = titleRef.getAttribute();
        		long langId = 0;
                if (attr.isMultilingual) {
                    KrnObject lang = frame.getInterfaceLang();
                    langId = (lang != null) ? lang.id : langId;
                }
                SortedSet<Record> recs = dataRef.getCash().getRecords(ids, attr, langId, null);
                if (recs.size() > 0) {
                	String title = (String) recs.last().getValue();
                	if (title != null && title.toUpperCase(Constants.OK).contains(text.toUpperCase(Constants.OK))) result.add(new TreePath(getPath()));
                }
        	}

        	if (!loadedOnly) {
	              try {
	                  loadEx();
	              } catch (Exception e) {
	                  e.printStackTrace();
	              }
	        }

        	int index = 0;
			for (Enumeration c = children(); c.hasMoreElements();) {
			    Node child = (Node) c.nextElement();
			    if (index++ > childIndex) {
				    List<TreePath> res= child.findTitle(text, -1, loadedOnly);
				    if (res != null && res.size() > 0)
				    	result.addAll(res);
			    }
			}
          
			if (childIndex > -1) {
				TreeNode parent = getParent();
				if (parent instanceof Node) {
					index = parent.getIndex(this);
					List<TreePath> res = ((Node)parent).findTitle(text, index, loadedOnly);
					if(res != null)
						result.addAll(res);
				}
			}
			
			return result;
        }

        private void loadEx() throws KrnException {
            if (!isLoaded) {
                isLoaded = true;
                if (item == null)
                    return;
                
                OrWebTreeTable2 tt = tree.getTreeTable();
            	
	            int[] limit = tt != null ? new int[] {tt.getChildrenSize()}: new int[1];
                // Загрузка детей
                List<OrRef.Item> children = item.getChildren(TreeAdapter2.this, limit);
                
                Map<Long, Long> sorts = null;
              if (sorted) {
	                Long[] ids = new Long[children.size()];
	                long[] ids2 = new long[children.size()];
	                Map<Long, OrRef.Item> items = new HashMap<Long, OrRef.Item>();
	                Map<Long, String> titles = new HashMap<Long, String>();
	                int i = 0;
	                for (OrRef.Item child : children) {
	                    KrnObject obj = (KrnObject)child.getCurrent();
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
	                	String title = (String)rec.getValue();
	                	titles.put(rec.getObjId(), title);
	                }
	                if(sortRef!=null){ 
		                sorts=new HashMap<Long, Long>();
		                KrnAttribute sattr = sortRef.getAttribute();
		                SortedSet<Record> srecs = sortRef.getCash().getRecords(ids2, sattr, 0, null);
		                //1. достаю все записи в некоторых(или во всех) этот атрибут может быть не проставлен
		                for (Record rec : srecs) {
		                	Long sort_index = (Long)rec.getValue();
		                	sorts.put(rec.getObjId(), sort_index);
		                }
		                if(srecs.size()<ids2.length){
		                	//2.сортирую с учетом titles и sorts
			            	Arrays.sort(ids, new NodeComparator(titles,sorts));
			                for (Long id : ids) {
			                    add(new Node(items.get(id), level + 1));
			                }
			                //3.пробегаюсь по всем идентификаторам и те которых нет создаю (т.е. проставляю непроставленный атрибут)
		                	long sort_index_=1;//Значение индекса начинается с 1 (пожелание заказчика)
			                for(Long id:ids){
			                	if(!sorts.containsKey(id)){
			                		OrRef.Item child=items.get(id);
			                		Record rec=sortRef.getCash().insertObjectAttribute((KrnObject)child.getCurrent(), sattr, child.index, 0, sort_index_, this);
				                	Long sort_index = (Long)rec.getValue();
				                	sorts.put(rec.getObjId(), sort_index);
			                	}
			                	sort_index_++;
			                }
		                }else{
		                	// если все атрибуты проставлены то по ним осуществляю сортировку
			            	Arrays.sort(ids, new NodeComparator(null,sorts));
			                for (Long id : ids) {
			                    add(new Node(items.get(id), level + 1));
			                }
		                }
	                }else{
		            	Arrays.sort(ids, new NodeComparator(titles,null));
		                for (Long id : ids) {
		                    add(new Node(items.get(id), level + 1));
		                }
	                }
                } else {
	                if(sortRef!=null){ 
		                Long[] ids = new Long[children.size()];
		                long[] ids2 = new long[children.size()];
		                Map<Long, OrRef.Item> items = new HashMap<Long, OrRef.Item>();
		                int i = 0;
		                for (OrRef.Item child : children) {
		                    KrnObject obj = (KrnObject)child.getCurrent();
		                    if (obj != null) {
		                    	ids[i] = obj.id;
		                    	ids2[i++] = obj.id;
		                    	items.put(obj.id, child);
		                    }
		                }
		                sorts=new HashMap<Long, Long>();
		                KrnAttribute sattr = sortRef.getAttribute();
		                SortedSet<Record> srecs = sortRef.getCash().getRecords(ids2, sattr, 0, null);
		                //1. достаю все записи в некоторых(или во всех) этот атрибут может быть не проставлен
		                for (Record rec : srecs) {
		                	Long sort_index = (Long)rec.getValue();
		                	sorts.put(rec.getObjId(), sort_index);
		                }
		                if(srecs.size()<ids2.length){
			                //2.пробегаюсь по всем идентификаторам и те которых нет создаю (т.е. проставляю непроставленный атрибут)
		                	long sort_index_=1;//Значение индекса начинается с 1 (пожелание заказчика)
			                for(Long id:ids){
			                	if(!sorts.containsKey(id)){
			                		if(id<0){//Для созданных объектов, которых нет еще в базе
					                	sorts.put(id, sort_index_);
			                		}else{
				                		OrRef.Item child=items.get(id);
				                		Record rec=sortRef.getCash().insertObjectAttribute((KrnObject)child.getCurrent(), sattr, child.index, 0, sort_index_, this);
					                	Long sort_index = (Long)rec.getValue();
					                	sorts.put(rec.getObjId(), sort_index);
			                		}
			                	}
			                	sort_index_++;
			                }
			                // в этом случае сортировки нет
			                for (OrRef.Item child : children) {
			                    add(new Node(child, level + 1));
			                }
		                }else{
		                	// если все атрибуты проставлены то по ним осуществляю сортировку
			            	Arrays.sort(ids, new NodeComparator(null,sorts));
			                for (Long id : ids) {
			                    add(new Node(items.get(id), level + 1));
			                }
		                }
	                }else{
		                for (OrRef.Item child : children) {
		                    add(new Node(child, level + 1));
		                }
	                }
                }
            }
        }
        
        public void changeSortItems(Map<Long,Long> values) throws KrnException {
        	long[] ids=Funcs.makeLongArray(values.keySet());
            KrnAttribute attr = sortRef.getAttribute();
            SortedSet<Record> recs = sortRef.getCash().getRecords(ids, attr, 0, null);
            for (Record rec : recs) {
            	sortRef.getCash().changeObjectAttribute(rec, values.get(rec.getObjId()), this);
            }
        }
        public void reload() throws KrnException {
            removeAllChildren();
            isLoaded = false;
            item.reset();
            loadEx();
        }

        @Override
        public TreeNode getChildAt(int index) {
            return super.getChildAt(index);
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
    public void expandAll(Node node, OrWebTree2 tree) {
        if (node != null && !node.isLeaf()) {
            nodeExpanded(node);
            tree.expandPath(new TreePath(node.getPath()));
            Enumeration<Node> childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                expandAll(childNodes.nextElement(), tree);
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
    public void setTree(OrWebTree2 tree) {
        this.tree = tree;
    }

    /**
     * @return the wClickAsOK
     */
    public boolean isWClickAsOK() {
        return wClickAsOK;
    }
    public boolean isSortRef(){
    	return sortRef!=null;
    }
    private class NodeComparator implements Comparator<Long> {
        private Map<Long, String> titles;
        private Map<Long, Long> sorts;

        public NodeComparator(Map<Long, String> titles,Map<Long, Long> sorts) {
            this.titles = titles;
            this.sorts = sorts;
        }

        @Override
        public int compare(Long o1, Long o2) {
            if(sorts!=null){
                Long l1 = sorts.get(o1);
                Long l2 = sorts.get(o2);
                if (l1 == null && l2 != null)
                    return 1;
                else if (l1 != null && l2 == null)
                    return -1;
                else if(l1 != null && l2 != null){
					return l1>l2?1:l1<l2?-1:0;
                }
            }
            if(titles!=null){
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
            }else
            	return 0;
        }
    }
    
    private class TreeCashListener implements DataCashListener {
        public void cashCleared() {
        	Node root = (Node) model.getRoot();
        	if (root != null) {
	            JsonArray arr = new JsonArray();
	            JsonObject obj = new JsonObject();
	            obj.add("index", root.getObject().id);
	            arr.add(obj);
	
	            OrWebTreeTable2 tt = tree.getTreeTable();
	
	            if (tt != null) {
	            	JsonValue r = tt.getChange("pr.reloadTreeTable");
                    if (r == null) {
                        tt.removeChange("pr.reloadRow");
                    	tt.sendChangeProperty("reloadRow", arr);
                    }
	            } else {
	                tree.sendChangeProperty("reloadNode", arr);
	            }
        	}
        }

        public void beforeCommitted() {
        }

        public void cashCommitted() {
        }

        public void cashRollbacked() {
        }
    }
}
