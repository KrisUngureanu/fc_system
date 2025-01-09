package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.util.*;

import kz.tamur.or3.client.comps.interfaces.OrTableComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableModel;

/**
 * Created by IntelliJ IDEA. User: Администратор Date: 30.11.2004 Time: 12:22:41
 * To change this template use File | Settings | File Templates.
 */
public class TreeTableAdapter extends TableAdapter implements TreeSelectionListener {
    
    private OrTreeTableComponent treeTable;

    private OrRef treeRef;
    private OrRef treeValueRef;

    private int access = Constants.FULL_ACCESS;
    private int itemsSize;
    private KrnObject rootObj;

    private boolean selfExpand = false;
    private boolean hasFilters = false;
    private boolean hasRows = true;
    private List<TreeAdapter.Node> copiedNodes;
    public static final int NEED_TITLE = -10;
    private boolean showEmpty = false;

    public TreeTableAdapter(OrFrame frame, OrTreeTableComponent treeTable,
                            boolean isEditor) throws KrnException {
        super(frame, treeTable, 0, isEditor);
        this.treeTable = treeTable;
        PropertyNode proot = treeTable.getProperties();

        PropertyNode pnode = proot.getChild("language");
        if (pnode != null) {
            PropertyValue pv = treeTable.getPropertyValue(pnode);
            if (!pv.isNull() && !pv.getKrnObjectId().equals("") ) {
                langId = Long.parseLong(pv.getKrnObjectId());
            }
        }
        
        PropertyNode rprop = proot.getChild("ref").getChild("treeRef");
        PropertyValue pv = treeTable.getPropertyValue(rprop);
        if (!pv.isNull()) {
            treeRef = OrRef.createRef(pv.stringValue(frame.getKernel()), true, Mode.RUNTIME,
                    frame.getRefs(), frame.getTransactionIsolation(), frame);
            treeRef.addOrRefListener(this);
        }

        if (treeRef != null && dataRef != null &&
                frame.getKernel().isSubclassOf(treeRef.getType().id,
                        dataRef.getType().id)) {
            hasRows = false;
        }

        rprop = proot.getChild("ref").getChild("treeValueRef");
        pv = treeTable.getPropertyValue(rprop);
        if (!pv.isNull()) {
            treeValueRef = OrRef.createRef(pv.stringValue(frame.getKernel()), true, Mode.RUNTIME,
                    frame.getRefs(), frame.getTransactionIsolation(), frame);
            treeValueRef.addOrRefListener(this);
        }

        pv = treeTable.getPropertyValue(treeTable.getProperties().getChild("pov").getChild("access"));
        if (!pv.isNull()) {
            access = pv.intValue();
        }

        pv = treeTable.getPropertyValue(treeTable.getProperties().getChild("view").getChild("showEmpty"));
        if (!pv.isNull()) {
            showEmpty = pv.booleanValue();
        }
        //model = new RtTreeTableModel();
        // selLnr = new RtTreeSelectionListener();

        // tb.setDefaultEditor(Object.class, cellEditor);
        // tb.getSelectionModel().addListSelectionListener(selLnr);

/*
		if (treeTable.isNaviExists()) {
			navi = treeTable.getNavi();
			ps.addPropertyChangeListener(navi);
			navi.setTableAdapter(this);
			// navi.initFilterPopupMenu(getFilterItems());
			addBtn = navi.getButtonByName("addBtn");
			yesManBtn = navi.getButtonByName("yesManBtn");
		}
*/
    }

    public void countCurrentTableItem() {
        int[] sels = table.getSelectedRows();
        int sel = -1;
        if (sels != null && sels.length > 0) sel = sels[0];
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
        int count = treeTable.getTree().getRowCount();
        ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
        rowCount = count;
    }

    public int getRowCount() {
        return rowCount;
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        if (e.getOriginator() instanceof ReportPrinterAdapter.ReportNode ||
                e.getOriginator() instanceof ReportPrinterAdapter ||
                e.getOriginator() instanceof OrCalcRef) return;
        super.valueChanged(e);
        //evaluateTreeRef();
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        if (e.getOriginator() != this) treeAdapter.valueChanged(e);

        if (e.getOriginator() != this)
            check(e);

        if (e.getOriginator() instanceof HyperPopupAdapter) {
            if (!hasFilters && dataRef.hasFilters()) {
                itemsSize = 0;
                hasFilters = true;
                addLeftToTree();
            }
        }
        if (e.getOriginator() != this
                && !(e.getOriginator() instanceof HyperPopupAdapter)
                && !selfChange) {
            OrTreeComponent tree = treeTable.getTree();
            OrRef ref = e.getRef();
            if ((e.getReason() & OrRefEvent.ITERATING) == 0
                    && (ref == treeRef || ref == treeAdapter.rootRef || ref == treeAdapter.rootCalcRef)) {
                TreeAdapter.Node root = treeAdapter.getRoot();
                if (root == null) root = treeAdapter.getRoot(false);
                if (root != null) {
                    if (treeAdapter.isRootChanged()) {
                        addLeftToTree();
                    } else {
                        int size = treeRef.getItems(0).size();
                        if ((size != itemsSize && !treeRef.isCleared)
                                || rootObj == null
                                || rootObj.id != root.getObject().id
                                || (!hasRows)) {
                            addLeftToTree();
                        } else {
                            if (dataRef.hasFilters()) {
                                DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
                                addLeftToTree();
                                //removeEmptyNodes(root, m);
                            }
                        }
                    }
                } else {
                    itemsSize = 0;
                }
            }
            if (((e.getReason() & OrRefEvent.ITERATING) > 0 && e.getOriginator() == null) 
            		|| ((e.getReason() & OrRefEvent.ITERATING) == 0 && ref != treeValueRef)) {
                TreeAdapter.Node root = treeAdapter.getRoot();
                countCurrentTableItem();
                try {
                    selfChange = true;
                    treeTable.setSelectedRow(dataRef.getIndex());
                } finally {
                    selfChange = false;
                }
            }
        }
    }

    public String getSelectedPathString(int index) {
    	String res = "";
    	TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeAdapter.Node root = treeAdapter.getRoot();
    	TreePath p = root.find(index);
    	while (p != null && p.getLastPathComponent() instanceof TreeAdapter.Node) {
    		TreeAdapter.Node node = (TreeAdapter.Node)p.getLastPathComponent();
    		if (node.trec != null) {
    			res = node.toString() + ((res.length() > 0) ? ", " : "") + res ;
    		} else {
    			res = "(" + (node.getParent().getIndex(node) + 1) + ")";
    		}
    		p = p.getParentPath();
    	}
    	return res;
    }

    // RefListener
    public void changesRollbacked(OrRefEvent e) {
        super.changesRollbacked(e);
        addLeftToTree();
    }

    /*
     * public void valueChanged(OrRefEvent e) { //try { OrRef ref = e.getRef();
     * Object originator = e.getOriginator(); if (ref == treeRef && originator ==
     * null) { //(treeAdapter.getRoot()); addLeftToTree(); } else if (ref ==
     * treeAdapter.rootRef && originator != this) { treeAdapter.valueChanged(e); }
     * super.valueChanged(e); //} catch (KrnException ex) { //
     * ex.printStackTrace(); //} }
     */
    public void evaluateTreeRef() {
        if (treeRef != null && treeRef.getAttribute() == null &&
                treeRef.getItems(0).size() == 0) {
            try {
                treeRef.evaluate(null);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void addLeftToTree() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        TreeAdapter.Node root = treeAdapter.getRoot();
        if (root == null) root = treeAdapter.getRoot(false);
        if (root != null) {
            selfExpand = true;
            rootObj = root.getObject();
            treeAdapter.setRootChanged(false);
            List expandedPaths = new ArrayList();
            deleteLeftFromTree(root, expandedPaths);
            root.reset();
            List refItems = treeRef.getItems(0);
            itemsSize = refItems.size();
			
            List<KrnObject> pobjs = new ArrayList<KrnObject>();
            for (int i = 0; i < refItems.size(); ++i) {
                OrRef.Item item = (OrRef.Item) refItems.get(i);
                KrnObject obj = (KrnObject) item.getCurrent();
				if (obj != null)
					pobjs.add(obj);
            }            
			long[] poids = Funcs.makeObjectIdArray(pobjs);
			
			try {
				preloadParent(poids);
			} catch (KrnException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < refItems.size(); ++i) {
                OrRef.Item item = (OrRef.Item) refItems.get(i);
                KrnObject kob = (KrnObject) item.getCurrent();
                TreePath path = null;
                if (root != null) {
                    path = root.find(kob, true);
                    if (path == null) {
                        path = root.find(kob, false);
                    }
                }

                if (path != null) {
                    TreeAdapter.Node node = (TreeAdapter.Node) path
                            .getLastPathComponent();
                    if (hasRows) {
                        TreeAdapter.Node defNode = treeAdapter.new Node(null, null,
                                null, null,null);
                        defNode.data = item;
                        defNode.index = i;
                        defNode.isLoaded = true;
                        int k = 0;
                        for (Enumeration e = node.children(); e.hasMoreElements();) {
                            TreeAdapter.Node cn = (TreeAdapter.Node) e.nextElement();
                            if (cn.getObject() == null)
                                k++;
                        }
                        m.insertNodeInto(defNode, node, k);
                    } else {
                        node.index = i;
                    }
                }
            }
            for (int i = 0; i < expandedPaths.size(); i++) {
                KrnObject kob = (KrnObject) expandedPaths.get(i);
                TreePath path = null;
                if (root != null) {
                    path = root.find(kob, true);
                    if (path == null) {
                        path = root.find(kob, false);
                    }
                }
                if (path != null) tree.expandPath(path);
            }

            if (!showEmpty && dataRef.hasFilters()) removeEmptyNodes(root, m);
/*            if (root.children().hasMoreElements()) {
                TreePath path = new TreePath(((TreeAdapter.Node)root.getChildAt(0)).getPath());
                while (path != null) {
                    tree.expandPath(path);
                    path = path.getParentPath();
                }
            }
*/            selfExpand = false;
            treeTable.tableDataChanged();
            countCurrentTableItem();
        }
    }

    public void checkRemoveEmptyNodes(TreeAdapter.Node root) {
    	OrTreeComponent tree = treeTable.getTree();
    	DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
    	if (!showEmpty && dataRef.hasFilters()) removeEmptyNodes(root, m);
    }
    
    private void removeEmptyNodes(TreeAdapter.Node node, DefaultTreeModel m) {
        for (int i = node.getChildCount() - 1; i >= 0; --i) {
            TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
            removeEmptyNodes(child, m);
        }
        if (node.getObject() != null && node.getChildCount() == 0 && node.index == -1
            && node.getParent() instanceof TreeAdapter.Node) {
            m.removeNodeFromParent(node);
        }
    }

    protected void deleteLeftFromTree(TreeAdapter.Node node, List expandedPaths) {
        OrTreeComponent tree = treeTable.getTree();
        if (node != null) {
            if (node.getObject() != null) {
                TreePath path = new TreePath(node.getPath());
                if (tree.isExpanded(path)) {
                    expandedPaths.add(node.getObject());
                }
            }
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            for (int i = node.getChildCount() - 1; i >= 0; --i) {
                TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
                deleteLeftFromTree(child, expandedPaths);
            }
            if (node.getObject() == null) {
                m.removeNodeFromParent(node);
            }
        }
    }

    private void preloadParent(long[] pids) throws KrnException {
		treeTable.getTreeAdapter().preloadParent(pids);
    }

    protected void check(OrRefEvent e) {
        if (selfChange)
            return;
        OrRef ref = e.getRef();

        if (ref == treeValueRef) {
            TreeAdapter treeAdapter = treeTable.getTreeAdapter();
            if (ref.getAttribute() != null && treeAdapter.isRootUpdated()) {
                TreeAdapter.Node root = treeAdapter.getRoot();

                OrRef.Item item = ref.getItem(0);
                try {
                    selfChange = true;
                    if (root != null) {
                        if (item == null || item.getCurrent() == null)
                            setSelectionPath(null);
                        else {
                            TreePath path = root.find((KrnObject) item.getCurrent(), true);
                            if (path == null) {
                                path = root.find((KrnObject) item.getCurrent(), false);
                            }
                            setSelectionPath(path);
                        }
                    }
                } finally {
                    selfChange = false;
                }
            }
        }
    }

    public void setSelectionPath(TreePath path) {
        OrTreeComponent tree = treeTable.getTree();
        tree.setSelectionPath(path);
//        tree.updateImage();
    }

    public void sort() {
        super.sort();
        addLeftToTree();
    }

    public void copySelectedRows() {
        int[] rows = table.getSelectedRows();
        OrTreeComponent tree = treeTable.getTree();

        if (rows != null && rows.length > 0) {
            if (copiedNodes == null)
                copiedNodes = new ArrayList<TreeAdapter.Node>();
            else
                copiedNodes.clear();

            long pid = 0;
            for (int row : rows) {
                TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
                if (node.index == -1) {
                    copiedNodes.clear();
                    return;
    }
                TreeAdapter.Node pNode = (TreeAdapter.Node) node.getParent();
                long tpid = (pNode.getObject() != null) ? pNode.getObject().id : 0;
                if (pid != 0 && pid != tpid) {
                    copiedNodes.clear();
                    return;
                }
                pid = tpid;
                copiedNodes.add(node);
            }
        }
    }

    public void pasteCopiedRows() {
        int i = table.getSelectedRow();
        OrTreeComponent tree = treeTable.getTree();

        if (copiedNodes != null && copiedNodes.size() > 0 && i > -1) {
            TreeAdapter.Node inode = (TreeAdapter.Node) tree.getPathForRow(i).getLastPathComponent();
            if (inode.index == -1) {
                copiedNodes.clear();
                return;
            }
            TreeAdapter.Node pNode = (TreeAdapter.Node) inode.getParent();
            long tpid = (pNode.getObject() != null) ? pNode.getObject().id : 0;
            TreeAdapter.Node cnode = copiedNodes.get(0);
            TreeAdapter.Node pcNode = (TreeAdapter.Node) cnode.getParent();
            long tcpid = (pcNode.getObject() != null) ? pcNode.getObject().id : 0;
            if (tpid != tcpid) {
                copiedNodes.clear();
                return;
            }

            SortedSet<Integer> is = new TreeSet<Integer>();

            for (Enumeration en = pcNode.children(); en.hasMoreElements(); ) {
                Object o = en.nextElement();
                if (o instanceof TreeAdapter.Node) {
                    TreeAdapter.Node chNode = (TreeAdapter.Node)o;
                    if (chNode.index != -1)
                        is.add(chNode.index);
                }
            }
            int[] mrows = new int[is.size()];
            int j = 0;
            for (Integer k : is) {
                mrows[j++] = k;
            }

            is = new TreeSet<Integer>();
            for (TreeAdapter.Node node : copiedNodes) {
                if (node.index != inode.index)
                    is.add(node.index);
            }
            int[] rows = new int[is.size()];
            j = 0;
            for (Integer k : is) {
                rows[j++] = k;
            }
            if (rows.length > 0) {
                try {
                    dataRef.moveRowsBefore(this, inode.index, rows, mrows, this);

                    is = new TreeSet<Integer>();
                    for (TreeAdapter.Node node : copiedNodes) {
                        if (node.index != inode.index) {
                            int row = tree.getRowForPath(new TreePath(node.getPath()));
                            is.add(row);
                        }
                    }

                    int min = Math.min(i, is.first());
                    int max = Math.max(i, is.last());
                    int beforeI = 0;
                    for (Integer k : is) {
                        if (i > k) {
                            beforeI++;
                        }
                    }

                    treeTable.tableRowsUpdated(min, max);
                    treeTable.setSelectedRow(i-beforeI);//, i-beforeI+is.size()-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (copiedNodes != null) copiedNodes.clear();
    }

  public void moveDown() {
      OrTreeComponent tree = treeTable.getTree();

      int row = treeTable.getSelectedRow();
      if (treeTable.getRowCount() > row + 1) {
        TreeAdapter.Node node1 = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
        TreeAdapter.Node node2 = (TreeAdapter.Node) tree.getPathForRow(row+1).getLastPathComponent();
        int actIndex1 = node1.index;
        int actIndex2 = node2.index;

        if (actIndex1 > -1 && actIndex2 > -1) {
            try {
                dataRef.changePlaces(this, actIndex1, actIndex2, this);
                treeTable.tableRowsUpdated(row, row + 1);

                treeTable.setSelectedRow(row+1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      }
  }

  public void moveUp() {
    OrTreeComponent tree = treeTable.getTree();

    int row = table.getSelectedRow();
    if (row > 0) {
      TreeAdapter.Node node1 = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
      TreeAdapter.Node node2 = (TreeAdapter.Node) tree.getPathForRow(row-1).getLastPathComponent();
      int actIndex1 = node1.index;
      int actIndex2 = node2.index;

      if (actIndex1 > -1 && actIndex2 > -1) {
          try {
              dataRef.changePlaces(this, actIndex1, actIndex2, this);
              treeTable.tableRowsUpdated(row - 1, row);

              treeTable.setSelectedRow(row-1);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    }
  }

  public int addNewRow() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        DefaultTreeModel m = ((DefaultTreeModel) tree.getModel());
        int row = -1;
        int i = dataRef.getItems(0).size();
        OrRef.Item item = null;

        try {
            selfChange = true;
            //формула до вставки
            if (beforAddFX != null) {
                ClientOrLang orlang = new ClientOrLang(frame);
                Map vc = new HashMap();
            	boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    orlang.evaluate(beforAddFX, vc, this, new Stack<String>());
                } catch (Exception e) {
                    Util.showErrorMessage(getTable(), e.getMessage(), res.getString("beforeAddAction"));
                	log.error("Ошибка при выполнении формулы '" + res.getString("beforeAddAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(e, e);
                } finally {
        			if (calcOwner)
        				OrCalcRef.makeCalculations();
                }
            }

            if (hasRows) {
                TreeAdapter.Node selNode = treeAdapter.getSelectedNode();
                if (selNode == null)
                    selNode = treeAdapter.getRoot();
                if (selNode.getObject() == null)
                    selNode = (TreeAdapter.Node) selNode.getParent();
                KrnObject crObject = selNode.getObject();

            	boolean calcOwner = OrCalcRef.setCalculations();
            	try {
	            	dataRef.insertItem(i, null, this, this, true);
	                treeRef.changeItem(i, crObject, this, this);
	                item = treeRef.getItem(0, i);
            	} catch (Exception e) {
            		log.error(e, e);
            	} finally {
        			if (calcOwner)
        				OrCalcRef.makeCalculations();
            	}

                TreeAdapter.Node defNode = treeAdapter.new Node(null, null, null,
                        null,null);
                defNode.data = item;
                defNode.index = i;
                defNode.isLoaded = true;
                int k = 0;
                for (Enumeration e = selNode.children(); e.hasMoreElements();) {
                    TreeAdapter.Node cn = (TreeAdapter.Node) e.nextElement();
                    if (cn.getObject() == null)
                        k++;
                }
                m.insertNodeInto(defNode, selNode, k);
                tree.expandPath(new TreePath(defNode.getPath()));
                row = treeAdapter.getTree().getRowForPath(new TreePath(defNode.getPath()));
            } else {
                return TreeTableAdapter.NEED_TITLE;
            }
        } catch (Exception e) {
    		log.error(e, e);
        } finally {
            selfChange = false;
        }
        return row;
    }

    public int addNewNode(String title) {
        int row = -1;
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        try {
            KrnObject kob = treeAdapter.createNewNode(title);
            TreeAdapter.Node root = treeAdapter.getRoot();
            TreePath path = null;
            if (root != null && kob != null) {
                path = root.find(kob, true);
                if (path == null) {
                    path = root.find(kob, false);
                }
                if (path != null) {
                    tree.expandPath(path);
                    row = tree.getRowForPath(path);
                    //item = treeRef.getItem(0, i);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return row;
    }

    public void afterAddRow(int row, int i) {
        OrTreeComponent tree = treeTable.getTree();
        OrRef.Item item = null;

        ps.firePropertyChange("rowSelected", selRowIdx, row);
        selRowIdx = row;
        int count = tree.getRowCount();
        ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
        rowCount = count;

        if (afterAddFX != null) {
            ArrayList sitems = new ArrayList();
            sitems.add(dataRef.getItem(dataRef.getLangId(), i));
            ClientOrLang orlang = new ClientOrLang(frame);
            Map vc = new HashMap();
            ArrayList selVect = new ArrayList();
            if (sitems.size() > 0) {
                for (int n = 0; n < sitems.size(); n++) {
                    item = (OrRef.Item) sitems.get(n);
                    if (item != null) {
                        selVect.add(item.getCurrent());
                    }
                }
                vc.put("SELOBJS", selVect);
            }
        	boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterAddFX, vc, this, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(getTable(), e.getMessage(),
                        res.getString("afterAddAction"));
            	log.error("Ошибка при выполнении формулы '" + res.getString("afterAddAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
        }

        treeTable.tableDataChanged();
        treeTable.setSelectedRow(row);
    }

    public int getEmptyRow() {
        OrTreeComponent tree = treeTable.getTree();
        int upperRowCount = getTable().getRowCount();
        int upperColumnCount = getTable().getColumnCount();
        for (int l = 0; l < upperRowCount; l++) {
            TreePath path = tree.getPathForRow(l);
            if (path == null) {
                continue;
            }
            TreeAdapter.Node node = (TreeAdapter.Node) path
                    .getLastPathComponent();
            int actIndex = node.index;
            List<OrRef.Item> items = dataRef.getItems(getLangId());
            if (actIndex == -1 || actIndex >= items.size())
                continue;

            OrRef.Item item = items.get(actIndex);

            if (item.getRec() == null)
                continue;
            Object obj_ = item.getRec().getValue();
            if ((obj_ instanceof KrnObject) && ((KrnObject) obj_).id > 0)
                continue;
            int itemsNull = 0;
            for (int j = 1; j < upperColumnCount; j++) {
                if (getTable().getValueAt(l, j) == null) {
                    itemsNull = itemsNull + 1;
                }
                if (getTable().getValueAt(l, j) != null) {
                    try {
                        ColumnAdapter c = (ColumnAdapter) ((OrTableModel) getTable()
                                .getModel()).getColumnAdapter(j);
                        String val_c = getTable().getValueAt(l, j)
                                .toString().trim();
                        OrRef ref = c.dataRef;
                        String typeName = (ref != null) ? ref.getType().name : null;
                        boolean flagEmptyRow = val_c.equals("")
                                || val_c.equals("false")
                                || val_c.equals("0")
                                || val_c.equals("0.0")
                                || "HiperColumn".equals(typeName)
                                || "SeqColumn".equals(typeName);
                        if (flagEmptyRow) {
                            itemsNull = itemsNull + 1;
                        }
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (itemsNull == upperColumnCount)
                return actIndex;
        }
        return -1;
    }

    public void deleteRow() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        try {
            TreeAdapter.Node selNode = treeAdapter.getSelectedNode();
            int selIdx = -1;
            if (hasRows) {
                if (selNode == null)
                    selNode = treeAdapter.getRoot();
                if (selNode.getObject() == null && selNode.index > -1) {
                    selIdx = treeTable.getSelectedRow();
                    int[] selIdxs = getTable().getSelectedRows();
                    List<Object> selVect = new ArrayList<Object>();
                    Map<Object, Integer> sitems = new HashMap<Object, Integer>();
                    for(int i : selIdxs) {
                        TreePath path = tree.getPathForRow(i);
                        if (path == null) {
                            continue;
                        }
                        TreeAdapter.Node node = (TreeAdapter.Node) path
                                .getLastPathComponent();
                        int actIndex = node.index;
                        if (actIndex > -1 && actIndex < dataRef.getItems(getLangId()).size()) {
                            OrRef.Item item = dataRef.getItem(dataRef.getLangId(), actIndex);
                            sitems.put(item.getCurrent(), actIndex);
                            selVect.add(item.getCurrent());
                        }
                    }

                    //формула до удаления
                    if (beforDelFX != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("SELOBJS", selVect);
                    	boolean calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(beforDelFX, vc, this, new Stack<String>());
                        } catch (Exception e) {
                            Util.showErrorMessage(getTable(), e.getMessage(), this.res.getString("beforeDeleteAction"));
                        	log.error("Ошибка при выполнении формулы '" + res.getString("beforeDeleteAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                            log.error(e, e);
                        } finally {
                			if (calcOwner)
                				OrCalcRef.makeCalculations();
                        }
                    }

                    sitems.keySet().retainAll(selVect);
                    Comparator<Integer> revCmp = new Comparator<Integer>() {
                        public int compare(Integer o1, Integer o2) {
                            return o2.compareTo(o1);
                        }
                    };
                    Set<Integer> inds = new TreeSet<Integer>(revCmp);
                    inds.addAll(sitems.values());

                	boolean calcOwner = OrCalcRef.setCalculations();
                	try {
	                    for (int j : inds) {
	                        dataRef.deleteItem(this, j, this);
	                    }
                	} catch (Exception e) {
                        log.error(e, e);
                	} finally {
	        			if (calcOwner)
	        				OrCalcRef.makeCalculations();
                	}
                    //формула после удаления
                    if (afterDelFX != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("SELOBJS", selVect);
                    	calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(afterDelFX, vc, this, new Stack<String>());
                        } catch (Exception e) {
                            Util.showErrorMessage(getTable(), e.getMessage(), this.res.getString("afterDeleteAction"));
                        	log.error("Ошибка при выполнении формулы '" + res.getString("afterDeleteAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                            log.error(e, e);
                        } finally {
                			if (calcOwner)
                				OrCalcRef.makeCalculations();
                        }
                    }
                }
            } else {
                selIdx = treeTable.getSelectedRow();

                int[] selIdxs = getTable().getSelectedRows();
                List<Object> selVect = new ArrayList<Object>();
                Map<Object, TreeAdapter.Node> sitems = new HashMap<Object, TreeAdapter.Node>();

                for(int i : selIdxs) {
                    TreePath path = tree.getPathForRow(i);
                    if (path == null) {
                        continue;
                    }
                    TreeAdapter.Node node = (TreeAdapter.Node) path
                            .getLastPathComponent();
                    int actIndex = node.index;
                    if (actIndex > 0 && actIndex < dataRef.getItems(getLangId()).size()) {
                        OrRef.Item item = dataRef.getItem(dataRef.getLangId(), actIndex);
                        selVect.add(item.getCurrent());
                        sitems.put(item.getCurrent(), node);
                    }
                }
                treeAdapter.deleteNode(beforDelFX,
                        afterDelFX, selVect, sitems, this);
            }
            int count = tree.getRowCount();
            if (selIdx > count - 1)
                selIdx--;

            ps.firePropertyChange("rowSelected", selRowIdx, selIdx);
            selRowIdx = selIdx;
            ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
            rowCount = count;

            treeTable.tableDataChanged();
            if (selIdx > -1 && selIdx < count) {
                treeTable.setSelectedRows(new int[] {selIdx}, false);
            }
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public String getDeleteRowString() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeAdapter.Node selNode = treeAdapter.getSelectedNode();
        int selIdx = -1;
        if (hasRows) {
            if (selNode == null)
                selNode = treeAdapter.getRoot();
            if (selNode.getObject() == null && selNode.index > -1) {
                selIdx = treeTable.getSelectedRow();
                String msg = res.getString("deleteRowConfirm");
                msg = msg.replaceAll("%1%", String.valueOf(selIdx + 1));
                return msg;
            }
        } else {
            return treeAdapter.getDeleteNodeString();
        }
        return null;
    }

    private void deleteNodeFromTree(TreeAdapter.Node selNode) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        m.removeNodeFromParent(selNode);
        decreaseNodeIndexes(treeAdapter.getRoot(), selNode.index);
    }

    private void decreaseNodeIndexes(TreeAdapter.Node node, int index) {
        for (int i = node.getChildCount() - 1; i >= 0; --i) {
            TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
            decreaseNodeIndexes(child, index);
        }
        if (node.getObject() == null && node.index > index) {
            node.index--;
        }
    }

    public boolean isSelfExpanded() {
        return selfExpand;
    }

    public void setSelfExpand(boolean selfExpand) {
        this.selfExpand = selfExpand;
    }

    public TableModel getModel() {
        return treeTable.getModel();
    }

    public TreeAdapter getTreeAdapter() {
        return treeTable.getTreeAdapter();
    }

    public OrTableComponent getTable() {
        return treeTable;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(isEnabled);
        treeTable.setEnabled(enabled);
        if (treeTable.getTree() != null)
            treeTable.getTree().getAdapter().setEnabled(enabled);
    }

    public int getRow(int index) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        OrTreeComponent tree = treeTable.getTree();
        TreePath path = treeAdapter.getRoot().find(index);
        if (path != null)
            return tree.getRowForPath(path);

        return index;
    }

    public boolean isHasRows() {
        return hasRows;
    }

    public void valueChanged(TreeSelectionEvent e) {
        treeSelectionChanged();
    }

    public void treeSelectionChanged() {
        try {
            if (treeValueRef != null && !selfChange) {
                TreeAdapter treeAdapter = treeTable.getTreeAdapter();

                TreeAdapter.Node n = treeAdapter.getSelectedNode();
                if (n != null) {
                	boolean calcOwner = OrCalcRef.setCalculations();
                	try {
	                    OrRef.Item item = treeValueRef.getItem(0);
	                    if (item == null)
	                        treeValueRef.insertItem(0, n.getObject(), this, this, false);
	                    else
	                        treeValueRef.changeItem(n.getObject(), this, this);
	                    dataRef.absolute(n.index, this);
                	} catch (Exception e) {
                		log.error(e, e);
                	} finally {
	                    if (calcOwner)
	                    	OrCalcRef.makeCalculations();
                	}
                }
                TreeAdapter.Node[] nodes = treeAdapter.getSelectedNodes();
                KrnObject[] objs = new KrnObject[nodes.length];
                for (int i = 0; i < nodes.length; ++i)
                    objs[i] = nodes[i].getObject();
                treeValueRef.setSelectedItems(objs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSelectedRows(int rows[]) {
        if (selfChange) {
            return;
        }
        try {
            OrTreeComponent tree = getTreeAdapter().getTree();
            int emptyRow = getEmptyRow();
            if (rows.length > 0) {
                TreePath path = tree.getPathForRow(rows[0]);
                if (path != null) {
                    TreeAdapter.Node node = (TreeAdapter.Node) path
                        .getLastPathComponent();
                    int actIndex = node.index;
                    if (emptyRow > -1) {
                        if (actIndex != -1
                                && emptyRow != actIndex) {
                            dataRef.deleteItem(TreeTableAdapter.this,
                                    emptyRow, this);
                        }
                    }

                    if (actIndex != -1) {
                        dataRef.setSelectedItems(new int[]{actIndex});
                        dataRef.absolute(actIndex, this);
                    } else {
                        dataRef.setSelectedItems(new int[0]);
                        dataRef.absolute(-1, this);
                    }
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    public OrRef getTreeRef() {
        return treeRef;
    }
}
