package kz.tamur.rt.adapters;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.FindRowPanel;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.OrTree;
import kz.tamur.comps.OrTreeTable;
import kz.tamur.comps.OrTreeTable.TreeTableCellRenderer;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.adapters.TreeAdapter.Node;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.BooleanTableCellRenderer;
import kz.tamur.util.DateTableCellRenderer;
import kz.tamur.util.Funcs;
import kz.tamur.util.IntegerTableCellRenderer;
import kz.tamur.util.OrCellRenderer;
import kz.tamur.util.ZebraCellRenderer;

import com.cifs.or2.client.FloatTableCellRenderer;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA. User: Администратор Date: 30.11.2004 Time: 12:22:41
 * To change this template use File | Settings | File Templates.
 */
public class TreeTableAdapter extends TableAdapter implements TreeSelectionListener {
    
	private OrTreeTable treeTable;

    private OrRef treeRef;
    private OrRef treeValueRef;

    private int access = Constants.FULL_ACCESS;
    private int itemsSize;
    private KrnObject rootObj;

    private boolean selfExpand = false;
    private boolean hasFilters = false;
    private boolean hasRows = true;
    private List<TreeAdapter.Node> copiedNodes;
    private boolean showEmpty = false;
    private boolean isExpandAll = false;

    public TreeTableAdapter(OrFrame frame, OrTreeTable treeTable,
                            boolean isEditor) throws KrnException {
        super(frame, treeTable, 0, isEditor);
        this.treeTable = treeTable;
        final JTable tb = this.treeTable.getJTable();

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
            treeRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME,
                    frame.getRefs(), frame.getTransactionIsolation(), frame);
            treeRef.addOrRefListener(this);
        }

        if (treeRef != null && dataRef != null &&
                Kernel.instance().isSubclassOf(treeRef.getType().id,
                        dataRef.getType().id)) {
            hasRows = false;
        }

        rprop = proot.getChild("ref").getChild("treeValueRef");
        pv = treeTable.getPropertyValue(rprop);
        if (!pv.isNull()) {
            treeValueRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME,
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
        tb.setModel(model);
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

        JTableHeader header = tb.getTableHeader();
        //header.setUpdateTableInRealTime(true);
        header.addMouseListener(new TreeTableColumnListener());
        //header.setReorderingAllowed(true);
        //tb.setOpaque(false);

        //dataRef.getCash().addCashListener(this);
        //initActionMap(treeTable);
    }

    protected RtTableModel createModel() {
    	final RtTableModel model = new RtTreeTableModel();

    	model.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
	            TreeTableCellRenderer tree = treeTable.getTree();
	            // super.fireTableStructureChanged();
	            TableColumnModel cm = treeTable.getJTable().getColumnModel();
	            TableColumn tc = cm.getColumn(0);
	            // tc.setCellEditor(treeCellEditor);
	            // tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
	            tc.setPreferredWidth(treeTable.getTreeWidth());

	            for (int i = 0; i < model.columns.size(); ++i) {
	                ColumnAdapter orc = (ColumnAdapter)model.columns.get(i);
	                tc = treeTable.getJTable().getColumnModel().getColumn(i + 1);
	                if (orc.getCellRenderer() == null) {
	                	ZebraCellRenderer r = null;
	                    if (orc instanceof CheckBoxColumnAdapter) {
	                    	r = new BooleanTableCellRenderer();
	                        orc.setCellRenderer(r);
	                    } else if (orc instanceof DateColumnAdapter) {
	                    	r = new DateTableCellRenderer((DateColumnAdapter)orc);
	                        orc.setCellRenderer(r);
	                    } else if (orc instanceof IntColumnAdapter) {
	                    	r = new IntegerTableCellRenderer((IntColumnAdapter)orc);
	                        orc.setCellRenderer(r);
	                    } else if (orc instanceof FloatColumnAdapter) {
	                    	r = new FloatTableCellRenderer((FloatColumnAdapter)orc);
	                        orc.setCellRenderer(r);
	                    } else {
	                    	r = new OrCellRenderer();
	                        orc.setCellRenderer(r);
	                    }
	                    r.setZebra1Color(model.getZebra1Color());
	                    r.setZebra2Color(model.getZebra2Color());
	                } else {
                        ZebraCellRenderer r = (ZebraCellRenderer)orc.getCellRenderer();
                        r.setZebra1Color(model.getZebra1Color());
                        r.setZebra2Color(model.getZebra2Color());
	                }
                    tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
	                // tc = treeTable.getJTable().getColumnModel().getColumn(i + 1);
	                // tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
	                tc.setCellEditor(orc.getCellEditor());
	                treeTable.updateRenderers();
	            }
				}
			}
        	
        });
    	return model;
    }

    public void countCurrentTableItem() {
        int sel = treeTable.getJTable().getSelectedRow();
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
        int count = treeTable.getTree().getRowCount();
        ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
        rowCount = count;
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
            TreeTableCellRenderer tree = treeTable.getTree();
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
                    TreePath path = (root != null) ? root.find(dataRef
                            .getIndex()) : null;
                    if (path != null) {
                        if (path.getParentPath() != null)
                            tree.expandPath(path.getParentPath());

                        int row = tree.getRowForPath(path);
                        if (row > -1) {
                            treeTable.getJTable().setRowSelectionInterval(row, row);
                        	super.scrollToVisible(row, 0);
                        }
                    }
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

    protected void addLeftToTree() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeTableCellRenderer tree = treeTable.getTree();
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
                        TreeAdapter.Node defNode = treeAdapter.new Node(null, null, null, null);
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
                } // if (path != null)
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
            selfExpand = false;
            model.fireTableDataChanged();
            countCurrentTableItem();
        } 
        
        if (isExpandAll) {
            expandAll(root,tree);
        }
    }
    
    public void checkRemoveEmptyNodes(TreeAdapter.Node root) {
    	OrTree tree = treeTable.getTree();
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
        TreeTableCellRenderer tree = treeTable.getTree();
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
        TreeTableCellRenderer tree = treeTable.getTree();
        tree.setSelectionPath(path);
//        tree.updateImage();
    }

    protected void sort() {
        int selCol = getTable().getJTable().getSelectedColumn();
        super.sort();
        addLeftToTree();
//        treeTable.getTree().updateImage();
        if (selCol > -1) getTable().getJTable().setColumnSelectionInterval(selCol, selCol);
    }

    public void copySelectedRows() {
        int[] rows = table.getJTable().getSelectedRows();
        TreeTableCellRenderer tree = treeTable.getTree();

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
        int i = table.getJTable().getSelectedRow();
        TreeTableCellRenderer tree = treeTable.getTree();

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

                    model.fireTableRowsUpdated(min, max);
                    treeTable.getJTable().getSelectionModel().setSelectionInterval(i-beforeI, i-beforeI+is.size()-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (copiedNodes != null) copiedNodes.clear();
    }

  public void moveDown() {
      TreeTableCellRenderer tree = treeTable.getTree();

      int row = table.getJTable().getSelectedRow();
      if (model.getRowCount() > row + 1) {
        TreeAdapter.Node node1 = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
        TreeAdapter.Node node2 = (TreeAdapter.Node) tree.getPathForRow(row+1).getLastPathComponent();
        int actIndex1 = node1.index;
        int actIndex2 = node2.index;

        if (actIndex1 > -1 && actIndex2 > -1) {
            try {
                dataRef.changePlaces(this, actIndex1, actIndex2, this);
                model.fireTableRowsUpdated(row, row + 1);

                treeTable.getJTable().setRowSelectionInterval(row+1, row+1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      }
  }

  public void moveUp() {
    TreeTableCellRenderer tree = treeTable.getTree();

    int row = table.getJTable().getSelectedRow();
    if (row > 0) {
      TreeAdapter.Node node1 = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
      TreeAdapter.Node node2 = (TreeAdapter.Node) tree.getPathForRow(row-1).getLastPathComponent();
      int actIndex1 = node1.index;
      int actIndex2 = node2.index;

      if (actIndex1 > -1 && actIndex2 > -1) {
          try {
              dataRef.changePlaces(this, actIndex1, actIndex2, this);
              model.fireTableRowsUpdated(row - 1, row);

              treeTable.getJTable().setRowSelectionInterval(row-1, row-1);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    }
  }

  public void addNewRow() {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeTableCellRenderer tree = treeTable.getTree();
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
                try {
                	boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(beforAddFX, vc, this, new Stack<String>());
        			if (calcOwner)
        				OrCalcRef.makeCalculations();
                } catch (Exception e) {
                    Util.showErrorMessage(getTable(), e.getMessage(), res.getString("beforeAddAction"));
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

                dataRef.insertItem(i, null, this, this, true);
                treeRef.changeItem(i, crObject, this, this);
                item = treeRef.getItem(0, i);
    			if (calcOwner)
    				OrCalcRef.makeCalculations();

                TreeAdapter.Node defNode = treeAdapter.new Node(null, null, null, null);
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
                KrnObject kob = treeAdapter.createNewNode(treeTable.getJTable());
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
                        item = treeRef.getItem(0, i);
                    }
                }
            }

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
                try {
                	boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(afterAddFX, vc, this, new Stack<String>());
        			if (calcOwner)
        				OrCalcRef.makeCalculations();
                } catch (Exception e) {
                    Util.showErrorMessage(getTable(), e.getMessage(),
                            res.getString("afterAddAction"));
                }
            }

            model.fireTableDataChanged();
            treeTable.getJTable().getSelectionModel().setSelectionInterval(row,
                    row);
//            tree.updateImage();
        } catch (KrnException e) {
            e.printStackTrace();
        } finally {
            selfChange = false;
        }
    }

    public int getEmptyRow() {
        TreeTableCellRenderer tree = treeTable.getTree();
        int upperRowCount = getTable().getJTable().getRowCount();
        int upperColumnCount = getTable().getJTable().getColumnCount();
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
                if (getTable().getJTable().getValueAt(l, j) == null) {
                    itemsNull = itemsNull + 1;
                }
                if (getTable().getJTable().getValueAt(l, j) != null) {
                    try {
                        ColumnAdapter c = (ColumnAdapter) ((RtTreeTableModel) getTable()
                                .getJTable().getModel()).getColumnAdapter(j);
                        String val_c = getTable().getJTable().getValueAt(l, j)
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
        TreeTableCellRenderer tree = treeTable.getTree();
        try {
            TreeAdapter.Node selNode = treeAdapter.getSelectedNode();
            int selIdx = -1;
            if (hasRows) {
                if (selNode == null)
                    selNode = treeAdapter.getRoot();
                if (selNode.getObject() == null && selNode.index > -1) {
                    selIdx = treeTable.getJTable().getSelectedRow();
                    String msg = res.getString("deleteRowConfirm");
                    msg = msg.replaceAll("%1%", String.valueOf(selIdx + 1));

                    int res = MessagesFactory.showMessageDialog(treeTable
                            .getJTable().getTopLevelAncestor(),
                            MessagesFactory.QUESTION_MESSAGE,
                            msg);
                    if (res == ButtonsFactory.BUTTON_YES) {
                        int[] selIdxs = getTable().getJTable().getSelectedRows();
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
                            try {
                            	boolean calcOwner = OrCalcRef.setCalculations();
                                orlang.evaluate(beforDelFX, vc, this, new Stack<String>());
                    			if (calcOwner)
                    				OrCalcRef.makeCalculations();

                            } catch (Exception e) {
                                Util.showErrorMessage(getTable(), e.getMessage(), this.res.getString("beforeDeleteAction"));
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
                        for (int j : inds) {
                            dataRef.deleteItem(this, j, this);
                        }
            			if (calcOwner)
            				OrCalcRef.makeCalculations();

                        //формула после удаления
                        if (afterDelFX != null) {
                            ClientOrLang orlang = new ClientOrLang(frame);
                            Map<String, Object> vc = new HashMap<String, Object>();
                            vc.put("SELOBJS", selVect);
                            try {
                            	calcOwner = OrCalcRef.setCalculations();
                                orlang.evaluate(afterDelFX, vc, this, new Stack<String>());
                    			if (calcOwner)
                    				OrCalcRef.makeCalculations();
                            } catch (Exception e) {
                                Util.showErrorMessage(getTable(), e.getMessage(), this.res.getString("afterDeleteAction"));
                            }
                        }
                    }
                }
            } else {
                selIdx = treeTable.getJTable().getSelectedRow();

                int[] selIdxs = getTable().getJTable().getSelectedRows();
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
                        afterDelFX, selVect, sitems, getTable(), this);
            }
            int count = tree.getRowCount();
            if (selIdx > count - 1)
                selIdx--;

            ps.firePropertyChange("rowSelected", selRowIdx, selIdx);
            selRowIdx = selIdx;
            ps.firePropertyChange("rowCont", rowCount - 1, count - 1);
            rowCount = count;

            model.fireTableDataChanged();
            if (selIdx > -1 && selIdx < count) {
                treeTable.getJTable().getSelectionModel()
                        .setSelectionInterval(selIdx, selIdx);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    private void deleteNodeFromTree(TreeAdapter.Node selNode) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeTableCellRenderer tree = treeTable.getTree();
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

    protected int moveToRow(int i) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeTableCellRenderer tree = treeTable.getTree();
        TreeAdapter.Node root = treeAdapter.getRoot();
        countCurrentTableItem();
        try {
            selfChange = true;
            TreePath path = (root != null) ? root.find(i) : null;
            if (path != null) {
                TreePath parentPath = path.getParentPath();
                //while (parentPath != null) {
                tree.expandPath(parentPath);
//                            parentPath = parentPath.getParentPath();
//                        }

                int row = tree.getRowForPath(path);

                treeTable.getJTable().getSelectionModel()
                        .setSelectionInterval(row, row);
                MoveTableView(row);
                return row;
            }
        } finally {
            selfChange = false;
        }
        return -1;
    }

    protected int findText(ColumnAdapter ca, String textForSearch, int from) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeAdapter.Node root = treeAdapter.getRoot();
        int row = findTextInNode(ca, root, textForSearch, from, true);
        if (row > -1) return row;
        row = findTextInNode(ca, root, textForSearch, from, false);
        return row;
    }

    protected void findText(ColumnAdapter ca, String textForSearch, FindRowPanel findPanel, int from) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeAdapter.Node root = treeAdapter.getRoot();
        int row = findTextInNode(findPanel, ca, root, textForSearch, from, true);
        if (row > -1) return;
        row = findTextInNode(findPanel, ca, root, textForSearch, from, false);
        if (row == -1) {
            MessagesFactory.showMessageDialog(table.getJTable().getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, res.getString("searchComplete"));
        }
    }

    protected int findTextInNode(FindRowPanel findPanel, ColumnAdapter ca,
                                 TreeAdapter.Node node, String text, int index, boolean from) {
        int res = -1;

        if (node != null) {
            if (node.index > -1) {
                TreeTableCellRenderer tree = treeTable.getTree();
                int row = tree.getRowForPath(new TreePath(node.getPath()));
                if (row == -1) {
                    TreeAdapter.Node tempNode = (TreeAdapter.Node) node.getParent();
                    while (row == -1 && tempNode != null) {
                        row = tree.getRowForPath(new TreePath(tempNode.getPath()));
                        if (tempNode.getParent() instanceof TreeAdapter.Node)
                            tempNode = (TreeAdapter.Node) tempNode.getParent();
                        else
                            tempNode = null;
                    }
                    row++;
                }

                if ((from && row > index) || (!from && row <= index)) {
                    if (findTextInRow(findPanel, ca, node.index, text))
                        return node.index;
                }
            }
            if (node.getObject() != null) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
                    res = findTextInNode(findPanel, ca, child, text, index, from);
                    if (res > -1) return res;
                }
            }
        }
        return res;
    }

    protected int findTextInNode(ColumnAdapter ca, TreeAdapter.Node node,
                                 String text, int index, boolean from) {
        int res = -1;

        if (node != null) {
            if (node.index > -1) {
                TreeTableCellRenderer tree = treeTable.getTree();
                int row = tree.getRowForPath(new TreePath(node.getPath()));
                if (row == -1) {
                    TreeAdapter.Node tempNode = (TreeAdapter.Node) node.getParent();
                    while (row == -1 && tempNode != null) {
                        row = tree.getRowForPath(new TreePath(tempNode.getPath()));
                        if (tempNode.getParent() instanceof TreeAdapter.Node)
                            tempNode = (TreeAdapter.Node) tempNode.getParent();
                        else
                            tempNode = null;
                    }
                    row++;
                }

                if ((from && row > index) || (!from && row <= index)) {
                    if (findTextInRow(ca, node.index, text))
                        return node.index;
                }
            }
            if (node.getObject() != null) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
                    res = findTextInNode(ca, child, text, index, from);
                    if (res > -1) return res;
                }
            }
        }
        return res;
    }

    public OrRef getTreeRef() {
        return treeRef;
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

        public Color getZebra1Color() {
            return treeTable.getZebra1Color();
        }

        public Color getZebra2Color() {
            return treeTable.getZebra2Color();
        }

        public int getRowCount() {
            return rowCount;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            TreeTableCellRenderer tree = treeTable.getTree();
            if (columnIndex == 0) {
                return tree.getModel();
            }
            if (tree.getPathForRow(rowIndex) != null) {
                TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(rowIndex).getLastPathComponent();

                int actIndex = node.index;
                if (actIndex == -1) {
                    KrnObject object = node.getObject();
                    if (object != null) {
                        ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex - 1);
                        return ca.getValueForNode(object);
                    }
                    return null;
                }

                ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex - 1);
                return ca.getValueAt(actIndex);
            }
            return null;
        }

        public String getColumnName(int column) {
            if (column == 0)
                return treeTable.getTreeName();

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
            if (ca instanceof MemoColumnAdapter) {
                TreeTableCellRenderer tree = treeTable.getTree();
                if (tree != null) {
                    TreePath tp = tree.getPathForRow(rowIndex);
                    if (tp != null) {
                        TreeAdapter.Node node = (TreeAdapter.Node) tp.getLastPathComponent();
                        int actIndex = node.index;
                        if (actIndex == -1)
                            return false;
                        else {
                            return true;
                        }
                    }
                }
            }
            
            return isColumnCellEditable(rowIndex, columnIndex);
        }
        
        public boolean isColumnCellEditable(int rowIndex, int columnIndex) {
            TreeTableCellRenderer tree = treeTable.getTree();
            if (columnIndex == 0)
                return true;
            if (access == Constants.READ_ONLY_ACCESS) {
                ColumnAdapter ca = getColumnAdapter(columnIndex);
                if (ca instanceof PopupColumnAdapter
                		|| ca instanceof DocFieldColumnAdapter) {
                    return ca.checkEnabled();
                } else {
                    return false;
                }
            }

            if (tree != null) {
                TreePath tp = tree.getPathForRow(rowIndex);
                if (tp != null) {
                    TreeAdapter.Node node = (TreeAdapter.Node) tp.getLastPathComponent();
                    int actIndex = node.index;
                    if (actIndex == -1)
                        return false;
                    else {
                        ColumnAdapter ca = getColumnAdapter(columnIndex);
                        return ca.isEnabled();
                    }
                }
            }
            return false;
        }

        public void setInterfaceLangId(int langId) {
            for (int i = 0; i < columns.size(); i++) {
                ColumnAdapter ca = (ColumnAdapter) columns.get(i);
                ca.getColumn().setLangId(langId);
            }
            fireTableStructureChanged();
        }

        public OrTableColumn getColumn(int colIndex) {
            if (colIndex == 0)
                return null;
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex - 1);
            return ca.getColumn();
        }

        public ColumnAdapter getColumnAdapter(int colIndex) {
            if (colIndex == 0)
                return null;
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex - 1);
            return ca;
        }

        public Icon getColumnIcon(int column) {
            if (column == sortColIdx)
                return isSortAsc ? COLUMN_UP : COLUMN_DOWN;
            return null;
        }

        public int getActualRow(int row) {
            TreeTableCellRenderer tree = treeTable.getTree();
            TreePath path = tree.getPathForRow(row);
            if (path == null)
                return row;
            TreeAdapter.Node node = (TreeAdapter.Node) path
                    .getLastPathComponent();
            return node.index;
        }

        public int getRowFromIndex(int index) {
            TreeTableCellRenderer tree = treeTable.getTree();
            if (index > -1) {
                for (int i = 0; i < tree.getRowCount(); i++) {
                    TreeAdapter.Node obj = (TreeAdapter.Node) tree
                            .getPathForRow(i).getLastPathComponent();
                    if (obj.index == index) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public int getRowForObjectId(long objId) {
            TreeTableCellRenderer tree = treeTable.getTree();
            for (int i = 0; i < tree.getRowCount(); i++) {
                TreeAdapter.Node obj = (TreeAdapter.Node) tree
                        .getPathForRow(i).getLastPathComponent();
                if (objId == obj.getObject().id) {
                    return i;
                }
            }
            return -1;
        }
    }

    public class TreeTableColumnListener extends ColumnListener {

        public void mouseEntered(MouseEvent e) {
            TableColumnModel columnModel = treeTable.getJTable()
                    .getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(e.getX());
            if (columnIndex > 0) {
                int columnIndex_ = columnModel.getColumnIndexAtX(e.getX()
                        + columnModel.getColumn(columnIndex).getWidth() / 2);
                Object src = e.getSource();
                if (columnIndex == 0 && src instanceof JTableHeader) {
                    String toolTip = (columnIndex_ != columnIndex)
                            ? res.getString("treeCollapse")
                            : res.getString("treeExpand");
                    ((JTableHeader) src).setToolTipText(toolTip);
                } else if (src instanceof JTableHeader) {
                    ((JTableHeader) src).setToolTipText(null);
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
            TreeAdapter treeAdapter = treeTable.getTreeAdapter();
            TableColumnModel columnModel = treeTable.getJTable()
                    .getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(e.getX());
            int columnIndex_ = columnModel.getColumnIndexAtX(e.getX()
                    + columnModel.getColumn(columnIndex).getWidth() / 2);
            TableColumn tColumn = columnModel.getColumn(columnIndex);
            int columnModelIndex = columnModel.getColumn(columnIndex)
                    .getModelIndex();
            if (columnModelIndex < 0) {
                return;
            } else if (columnModelIndex == 0) {
                selfExpand = true;
                boolean calcOwner = OrCalcRef.setCalculations();
                if (columnIndex == columnIndex_) {
                    int index = getDataRef().getIndex();
                    treeAdapter.expandAll(treeAdapter.getRoot());
                    getDataRef().fireValueChangedEvent(index, TreeTableAdapter.this, 0);
                } else {
                    //isExpanded = false;
                    treeAdapter.collapseAll(treeAdapter.getRoot());
                    getDataRef().fireValueChangedEvent(0, TreeTableAdapter.this, 0);
                }
                int row = table.getJTable().getSelectedRow();
                int col = table.getJTable().getSelectedColumn();
                selfExpand = false;
//                tree.updateImage();
                countCurrentTableItem();
                getModel().fireTableDataChanged();
                if (row > -1 && col > -1) {
                    table.getJTable().setRowSelectionInterval(row, row);
                    table.getJTable().setColumnSelectionInterval(col, col);
                }
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            } else {
                OrTableColumn otc = null;
                TableModel tm = table.getJTable().getModel();
                if (tm instanceof OrTableModel) {
                    otc = ((OrTableModel)tm).getColumn(columnIndex);
                }
                if (canSort && otc != null && otc.isCanSort() && !otc.isHelpClick()) {
                    if (columnIndex == columnIndex_)
                        sortByColumn(tColumn);
                    else
                        removeSortColumn(tColumn);
                }
            }
            countCurrentTableItem();
            treeTable.getJTable().getTableHeader().repaint();
            treeTable.repaint();
        }
    }

    public OrTable getTable() {
        return treeTable;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(isEnabled);
        treeTable.setEnabled(enabled);
        if (treeTable.getTree() != null)
            treeTable.getTree().getAdapter().setEnabled(enabled);
    }

    public void scrollToVisible(int rowIndex, int vColIndex) {
        int row = ((RtTreeTableModel) model).getRowFromIndex(rowIndex);
        super.scrollToVisible(row, vColIndex + 1);
    }

    public int getRow(int index) {
        TreeAdapter treeAdapter = treeTable.getTreeAdapter();
        TreeTableCellRenderer tree = treeTable.getTree();
        TreePath path = treeAdapter.getRoot().find(index);
        if (path != null)
            return tree.getRowForPath(path);

        return index;
    }

    public boolean isHasRows() {
        return hasRows;
    }

    public void valueChanged(TreeSelectionEvent e) {
        try {
            if (treeValueRef != null && !selfChange) {
                TreeAdapter treeAdapter = treeTable.getTreeAdapter();

                TreeAdapter.Node n = treeAdapter.getSelectedNode();
                if (n != null) {
                	boolean calcOwner = OrCalcRef.setCalculations();
                    OrRef.Item item = treeValueRef.getItem(0);
                    if (item == null)
                        treeValueRef.insertItem(0, n.getObject(), this, this, false);
                    else
                        treeValueRef.changeItem(n.getObject(), this, this);
                    dataRef.absolute(n.index, this);
                    if (calcOwner)
                    	OrCalcRef.makeCalculations();
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

    protected void initActionMap(final OrTable table) {
        super.initActionMap(table);
        InputMap im = table.getJTable().getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        //  Have the enter key work the same as the tab key
        KeyStroke left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        final Action oldLeftAction = table.getJTable().getActionMap().get(im.get(left));
        final Action oldRightAction = table.getJTable().getActionMap().get(im.get(right));
        //  Override the default tab behaviour

        Action leftAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int selCol = table.getJTable().getSelectedColumn();
                if (selCol > -1) {
                    int mIndex = table.getJTable().getColumnModel().getColumn(selCol).getModelIndex();
                    if (mIndex == 0) {
                        int row = table.getJTable().getSelectedRow();
                        OrTree tree = treeTable.getTree();
                        TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
                        if (!node.isLeaf() && !tree.isCollapsed(row)) {
                            tree.collapseRow(row);
                            table.getJTable().getSelectionModel().setSelectionInterval(row, row);
                            table.getJTable().getColumnModel().getSelectionModel().setSelectionInterval(selCol, selCol);
                            countCurrentTableItem();
                            return;
                        }
                    }
                }
                oldLeftAction.actionPerformed(e);
            }
        };

        Action rightAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int selCol = table.getJTable().getSelectedColumn();
                if (selCol > -1) {
                    int mIndex = table.getJTable().getColumnModel().getColumn(selCol).getModelIndex();
                    if (mIndex == 0) {
                        int row = table.getJTable().getSelectedRow();
                        OrTree tree = treeTable.getTree();
                        TreeAdapter.Node node = (TreeAdapter.Node) tree.getPathForRow(row).getLastPathComponent();
                        if (!node.isLeaf() && !tree.isExpanded(row)) {
                            tree.expandRow(row);
                            table.getJTable().getSelectionModel().setSelectionInterval(row, row);
                            table.getJTable().getColumnModel().getSelectionModel().setSelectionInterval(selCol, selCol);
                            countCurrentTableItem();
                            return;
                        }
                    }
                }
                oldRightAction.actionPerformed(e);
            }
        };

        table.getJTable().getActionMap().put(im.get(left), leftAction);
        table.getJTable().getActionMap().put(im.get(right), rightAction);
    }
    
    public void expandAll(Node node,TreeTableCellRenderer tree) {
        if (node != null && !node.isLeaf()) {
            tree.expandPath(new TreePath(node.getPath()));
            Enumeration childNodes = node.children();
            while(childNodes.hasMoreElements()) {
                Node child = (Node)childNodes.nextElement();
                expandAll(child, tree);
            }
        }
    }
    public boolean isExpandAll() {
        return isExpandAll;
    }

    public void setExpandAll(boolean isExpandAll) {
        this.isExpandAll = isExpandAll;
    }
}
