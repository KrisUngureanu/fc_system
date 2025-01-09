package kz.tamur.rt.adapters;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.kernel.*;
import com.cifs.or2.util.expr.Editor;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.RuntimeException;
import kz.tamur.rt.data.AttrRecord;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.CashChangeListener;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import kz.tamur.or3.util.PathElement2;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/*
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
*/
import static kz.tamur.rt.Utils.createMenuItem;
public class TreeAdapter extends ComponentAdapter implements CashChangeListener {//,
//DropTargetListener, DragSourceListener, DragGestureListener {

    protected JTree tree;
    private Record rootRec_;
    protected Node root_;
    protected Cache cash;
    //protected KrnAttribute childrenAttr;
    //protected KrnAttribute parentAttr;
    //protected KrnAttribute valueAttr;
    //protected KrnClass childType;
    protected KrnAttribute[] titleAttrs;
    protected KrnAttribute[] titleAttrs2;

    protected PathElement2[] valueAttrs;
    protected PathElement2[] parentAttrs;
    protected PathElement2[] childrenAttrs;

    protected static final Kernel krn_ = Kernel.instance();
    protected String titlePath, oldTitlePath;
    protected String titlePath2, oldTitlePath2;
    private JPopupMenu nodeOperations_ = new JPopupMenu();
    private JMenuItem nodeRenameItem_;
    private JMenuItem nodeCreateItem_;
    private JMenuItem nodeCreateWithHistoryItem_;
    private JMenuItem nodeChangeItem_;
    private JMenuItem nodeDeleteItem_;
    private JMenuItem nodeSelectChildren_;
    private JMenuItem expandItem;
    private JMenuItem collapsItem;
    public OrRef rootRef;
    private int access = Constants.FULL_ACCESS;
    private boolean rootUpdated = false;
    private boolean isFolderSelect = false;
    private int refreshMode = 0;
    private long defaultFilterId;
    private List filteredIds;

    protected ResourceBundle res = ResourceBundle.getBundle(
            Constants.NAME_RESOURCES, new Locale("ru"));
    private long ifcLangId = 0;
    public OrCalcRef rootCalcRef;
    private boolean rootChanged = false;
/*
    protected DropTarget dropTarget = null;
    protected DragSource dragSource = null;
    protected boolean isDragStarted;
    protected double lastLocation;
*/
    protected ASTStart beforeDelAction;
    protected ASTStart afterDelAction;
    protected boolean sorted = false;

    public TreeAdapter(OrFrame frame, OrTree tree, boolean isEditor)
            throws KrnException {
        super(frame, tree, isEditor);
        PropertyNode proot = tree.getProperties();
        this.tree = tree;
        tree.setAdapter(this);
        this.tree.setOpaque(true);
        //this.tree.setBackground(kz.tamur.comps.Utils.getLightSysColor());
        this.refreshMode = 0;

        PropertyValue pv = tree.getPropertyValue(proot.getChild("view").getChild("combonotsorted"));
        if (!pv.isNull()) {
            sorted = !pv.booleanValue();
        }

        PropertyNode pnode = proot.getChild("ref").getChild("language");
        if (pnode != null) {
            pv = tree.getPropertyValue(pnode);
            if (!pv.isNull() && !pv.getKrnObjectId().equals("") ) {
                langId = Long.parseLong(pv.getKrnObjectId());
            }
        }

        PropertyNode rprop = proot.getChild("ref").getChild("refreshMode");
        pv = tree.getPropertyValue(rprop);
        if (!pv.isNull()) {
            refreshMode = pv.intValue();
        }

        rprop = proot.getChild("ref").getChild("rootRef");
        pv = tree.getPropertyValue(rprop);
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Корень";
                if (refreshMode == Constants.RM_DIRECTLY) {
                    boolean hasParentRef = false;
                    Map<String, OrRef> refs = frame.getRefs();
                    Iterator<String> it = refs.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        OrRef ref = refs.get(key);
                        if (ref != null && pv.stringValue().startsWith(ref.toString())) {
                            hasParentRef = true;
                            break;
                        }
                    }
                    if (!hasParentRef)
                        rootRef = OrRef.createContentRef(pv.stringValue(),
                                Constants.RM_ALWAYS, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                    else
                        rootRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME,
                                frame.getRefs(), frame.getTransactionIsolation(), frame);
                } else {
                    rootRef = OrRef.createContentRef(pv.stringValue(), refreshMode, Mode.RUNTIME,
                             frame.getTransactionIsolation(), frame);
                }
                rootRef.addOrRefListener(this);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }

        pv = tree.getPropertyValue(proot.getChild("ref").getChild("rootExpr"));
        String rootExpr = null;
        if (!pv.isNull()) {
            rootExpr = pv.stringValue();
        }
        if (rootExpr != null && rootExpr.length() > 0) {
            propertyName = "Свойство: Корень формула";
            try {
                rootCalcRef = new OrCalcRef(rootExpr, false, Mode.RUNTIME,
                        frame.getRefs(), frame.getTransactionIsolation(),
                        frame, tree, propertyName, this);
                rootCalcRef.addOrRefListener(this);
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }

        // Перед удалением
        PropertyNode beforeDelNode = proot.getChild("pov").getChild("beforeDelAction");
        if (beforeDelNode != null) {
            pv = tree.getPropertyValue(beforeDelNode);
            String expr = pv.isNull() ? "" : pv.stringValue();
            if (expr.trim().length() == 0) expr = "";
            if (expr.length() > 0) {
            	beforeDelAction = OrLang.createStaticTemplate(expr);
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            }
        }

        // После удаления
        PropertyNode afterDelNode = proot.getChild("pov").getChild("afterDelAction");
        if (afterDelNode != null) {
            pv = tree.getPropertyValue(afterDelNode);
            String expr = pv.isNull() ? "" : pv.stringValue();
            if (expr.trim().length() == 0) expr = "";
            if (expr.length() > 0) {
            	afterDelAction = OrLang.createStaticTemplate(expr);
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            }
        }
        
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath"));
        if (!pv.isNull()) {
            titlePath = pv.stringValue();
        }

        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath2"));
        if (!pv.isNull()) {
            titlePath2 = pv.stringValue();
        }

        Cache cash = getCash();
        ClassNode cn = krn_.getClassNode(rootRef.getType().id);

        if (titlePath != null && titlePath.length() > 0) {
            titleAttrs = Utils.getAttributesForPath(titlePath);
            for (int i = 0; i<titleAttrs.length; i++) {
                cash.addCashChangeListener(titleAttrs[i].id, this);
            }
        }
        if (titlePath2 != null && titlePath2.length() > 0) {
            titleAttrs2 = Utils.getAttributesForPath(titlePath2);
            for (int i = 0; i<titleAttrs2.length; i++) {
                cash.addCashChangeListener(titleAttrs2[i].id, this);
            }
        }

        String valuePath = null;
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("valueRef"));
        if (!pv.isNull()) {
            valuePath = pv.stringValue();
        }

        KrnAttribute va = cn.getAttribute("значение");
        if (valuePath != null && valuePath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(valuePath);
            if (pes.length > 1) {
                valueAttrs = new PathElement2[pes.length - 1];
                for (int i = 1; i<pes.length; i++) {
                    valueAttrs[i-1] = pes[i];
                }
            }
        } else if (va != null) {
            valueAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
        } else {
            valueAttrs = new PathElement2[0];
        }

        String parentPath = null;
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("parentRef"));
        if (!pv.isNull()) {
            parentPath = pv.stringValue();
        }

        va = cn.getAttribute("родитель");
        if (parentPath != null && parentPath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(parentPath);
            if (pes.length > 1) {
                parentAttrs = new PathElement2[pes.length - 1];
                for (int i = 1; i<pes.length; i++) {
                    parentAttrs[i-1] = pes[i];
                }
            }
        } else if (va != null) {
            parentAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
        } else {
            parentAttrs = new PathElement2[0];
        }

        String childrenPath = null;
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("childrenRef"));
        if (!pv.isNull()) {
            childrenPath = pv.stringValue();
        }

        va = cn.getAttribute("дети");
        if (childrenPath != null && childrenPath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(childrenPath);
            if (pes.length > 1) {
                childrenAttrs = new PathElement2[pes.length - 1];
                for (int i = 1; i<pes.length; i++) {
                    childrenAttrs[i-1] = pes[i];
                }
            }
        } else if (va != null) {
            childrenAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
        } else {
            childrenAttrs = new PathElement2[0];
        }

        if (parentAttrs.length > 0)
            cash.addCashChangeListener(parentAttrs[parentAttrs.length - 1].attr.id, this);

        if (valueAttrs.length > 0)
            cash.addCashChangeListener(valueAttrs[valueAttrs.length - 1].attr.id, this);

        if (childrenAttrs.length > 0)
                     cash.addCashChangeListener(childrenAttrs[childrenAttrs.length - 1].attr.id, this);

        cash.addCashChangeListener(rootRef.getType().id, this);

        // TODO Временно. Поменять свойство фильтр на содержимое для дерева
        if (!(tree instanceof OrTreeTable.TreeTableCellRenderer)) {
            pv = tree.getPropertyValue(proot.getChild("ref").getChild("defaultFilter"));
            if (!pv.isNull()) {
                defaultFilterId = pv.filterValue().getObjId();
            }
        }
        /*if (titlePath2_ != null && titlePath2_.length() > 0) {
            KrnAttribute[] attrs = Utils.getAttributesForPath(titlePath2_);
            titleAttr2_ = (attrs.length > 0) ? attrs[0] : null;
        } */

        pv = tree.getPropertyValue(proot.getChild("view").getChild("popup").getChild("show"));
        if (!pv.isNull() && pv.booleanValue()) {
            tree.addMouseListener(new TreeMouseListener());
            NodeOperationsActionListener l = new NodeOperationsActionListener();
            PropertyNode items = proot.getChild("view").getChild("popup").getChild("items");
            //if (!isReadOnly) {
            pv = tree.getPropertyValue(items.getChild("renameNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("renameNode");
                nodeRenameItem_ =  createMenuItem(menuName, "Rename");
                nodeRenameItem_.addActionListener(l);
                nodeOperations_.add(nodeRenameItem_);
            }

            pv = tree.getPropertyValue(items.getChild("changeNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("changeNode");
                nodeChangeItem_ =  createMenuItem(menuName);
                nodeChangeItem_.addActionListener(l);
                nodeOperations_.add(nodeChangeItem_);
            }

            pv = tree.getPropertyValue(items.getChild("createNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("createNode");
                nodeCreateItem_ =  createMenuItem(menuName, "Create");
                nodeCreateItem_.addActionListener(l);
                nodeOperations_.add(nodeCreateItem_);
            }
            pv = tree.getPropertyValue(items.getChild("createAndBindNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("createNodeAndBind");
                nodeCreateWithHistoryItem_ =  createMenuItem(menuName, "Create");
                nodeCreateWithHistoryItem_.addActionListener(l);
                nodeOperations_.add(nodeCreateWithHistoryItem_);
            }
            pv = tree.getPropertyValue(items.getChild("deleteNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("deleteNode");
                nodeDeleteItem_ =  createMenuItem(menuName, "Delete");
                nodeDeleteItem_.addActionListener(l);
                nodeOperations_.add(nodeDeleteItem_);
            }

            pv = tree.getPropertyValue(items.getChild("expandNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("expandNode");
                expandItem =  createMenuItem(menuName, "ExpandTree");
                expandItem.addActionListener(l);
                nodeOperations_.add(expandItem);
            }
            pv = tree.getPropertyValue(items.getChild("collapseNode"));
            if (pv.isNull() || pv.booleanValue()) {
                String menuName = res.getString("collapseNode");
                collapsItem =  createMenuItem(menuName, "CollapseTree");
                collapsItem.addActionListener(l);
                nodeOperations_.add(collapsItem);
            }
        }
        //}

        /*if (isMultiSelect) {
            if (!isReadOnly)
                nodeOperations_.addSeparator();
            nodeSelectChildren_ = new JMenuItem("Выделить подчиненные");
            nodeSelectChildren_.addActionListener(l);
            nodeOperations_.add(nodeSelectChildren_);
        }*/
        pv = tree.getPropertyValue(proot.getChild("view").getChild("folderSelect"));
        if (!pv.isNull()) {
            isFolderSelect = pv.booleanValue();
        } else {
            isFolderSelect = ((Boolean)proot.getChild("view").getChild("folderSelect").getDefaultValue()).booleanValue();
        }
        this.tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent e) {
                JTree t = (JTree)e.getSource();
                TreeNode tn = (TreeNode)e.getPath().getLastPathComponent();
                Container cnt = t.getTopLevelAncestor();
                if (cnt instanceof DesignerDialog) {
                    DesignerDialog dlg = (DesignerDialog)cnt;
                    if (!isFolderSelect && !tn.isLeaf()) {
                        dlg.setOkEnabled(false);
                    } else {
                        dlg.setOkEnabled(true);
                    }
                }
            }
        });
        
        pv = tree.getPropertyValue(proot.getChild("pov").getChild("wClickAsOK"));
        if (pv.booleanValue()) {
            this.tree.addMouseListener(new MouseListener() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        TreeNode tn = (TreeNode) TreeAdapter.this.tree.getLastSelectedPathComponent();
                        DesignerDialog dlg = (DesignerDialog) TreeAdapter.this.tree.getTopLevelAncestor();
                        if (dlg != null) {
                            if (dlg.getOkBtn().isEnabled() && tn.isLeaf()) {
                                dlg.getOkBtn().doClick();
                            }
                        }
                    }
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                }
            });
        }
        ((OrTree)this.tree).setXml(null);

        if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
            OrTreeTable.TreeTableCellRenderer t =
                    (OrTreeTable.TreeTableCellRenderer) tree;
            TreeTableAdapter tta = t.getTableAdapter();
            tree.addTreeSelectionListener(tta);
        }
/*        else {
            dropTarget = new DropTarget(tree, this);
            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer(tree,
                    DnDConstants.ACTION_MOVE, this);

        }
*/
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public TreeAdapter(String rootPath, String titlePath, String rootExpr, Map refs,
                       long langId, OrFrame frame)
            throws KrnException {
        super();
        this.frame = frame;
        tree = new JTree();
        if (rootPath != null) {
            rootRef = OrRef.createRef(rootPath, false, Mode.RUNTIME, refs,
                    frame.getTransactionIsolation(), frame);
        }
        if (rootExpr != null && rootExpr.length() > 0) {
            propertyName = "Свойство: Корень формула";
            try {
                rootCalcRef = new OrCalcRef(rootExpr, false, Mode.RUNTIME,
                        frame.getRefs(), frame.getTransactionIsolation(),
                        frame, null, propertyName, this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.titlePath = titlePath;

        ClassNode cn = krn_.getClassNode(rootRef.getType().id);

        KrnAttribute va = cn.getAttribute("значение");
        if (va != null) {
            valueAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
        } else {
            valueAttrs = new PathElement2[0];
        }
        va = cn.getAttribute("дети");
        if (va != null) {
             childrenAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
         } else {
             childrenAttrs = new PathElement2[0];
         }

        va = cn.getAttribute("родитель");
        if (va != null) {
            parentAttrs = new PathElement2[] {new PathElement2(krn_.getClassNode(va.typeClassId).getKrnClass(), va, 0)};
        } else {
            parentAttrs = new PathElement2[0];
        }

        if (titlePath != null && titlePath.length() > 0) {
            titleAttrs = Utils.getAttributesForPath(titlePath);
        }

        /*if (titlePath2_ != null && titlePath2_.length() > 0) {
            KrnAttribute[] attrs = Utils.getAttributesForPath(titlePath2_);
            titleAttr2_ = (attrs.length > 0) ? attrs[0] : null;
        } */

    }

    // RefListener
    public void changesRollbacked(OrRefEvent e) {
        super.changesRollbacked(e);
        if (root_ != null) {
            root_.reset();
        }
    }

    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        try {
            if (!selfChange && e.getOriginator() != this && (e.getReason() & OrRefEvent.ITERATING) == 0) {
                if (e.getRef() == rootCalcRef)
                    populateRoot();
                else {
                    if (rootRef != null && e.getRef() != null &&
                        e.getRef().toString() != null &&
                        rootRef.toString().equals(e.getRef().toString()))
                        populateRoot();
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void populateRoot() throws KrnException {
        try {
            selfChange = true;
            if (rootCalcRef != null) {
                OrRef.Item item = rootCalcRef.getItem();
                KrnObject obj = (KrnObject) ((item != null) ? item.getCurrent() : null);
                Record rec = null;
                if (obj != null)
                    rec = new ObjectRecord(obj.classId, obj);
                setRoot(rec);
            } else {
                if (rootRef.getAttribute() == null && rootRef.getIndex() != 0)
                    rootRef.absolute(0, this);
                OrRef.Item item = rootRef.getItem(0, 0);
                Record rec = (item != null) ? item.getRec() : null;
                setRoot(rec);
            }
        } finally {
            selfChange = false;
        }
    }

    public void clear() {
    }

    public Node getRoot() {
        if (root_ == null && rootCalcRef != null)
        	if (!selfChange) {
	        	try {
	        		selfChange = true;
		        	rootCalcRef.refresh(null);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	} finally {
	        		selfChange = false;
	        	}
        	}
        return root_;
    }

    public Node getRoot(boolean rootUpdated) {
        if (root_ == null && rootCalcRef != null) {
        	if (!selfChange) {
	        	try {
	        		selfChange = true;
		        	rootCalcRef.refresh(null);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	} finally {
	        		selfChange = false;
	        	}
        	}
        }
        return root_;
    }

    public void setRoot(Record newRoot) throws KrnException {
        if ((newRoot != null && !newRoot.equals(rootRec_)) || !rootUpdated) {
            rootUpdated = true;
            rootChanged = true;
            rootRec_ = newRoot;
            if (defaultFilterId > 0) filter();
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            if (root_ != null) {
            	if (root_.getParent() != null) {
            		m.removeNodeFromParent(root_);
            	}
                root_ = null;
            }
            if (rootRec_ != null) {
                root_ = new Node(rootRec_);
                root_.loadEx();
                m.insertNodeInto(root_, (MutableTreeNode) m.getRoot(), 0);
                tree.expandPath(new TreePath(m.getRoot()));
            }
        } else if (newRoot == null) {
            rootUpdated = true;
            rootRec_ = newRoot;
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            if (root_ != null) {
            	if (root_.getParent() != null) {
            		m.removeNodeFromParent(root_);
            	}
                root_ = null;
            }
        } else if(newRoot.equals(rootRec_)) {
            if (defaultFilterId > 0) filter();
            root_.reload();
            
            if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
            	
            	TreeTableAdapter tta = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                tta.checkRemoveEmptyNodes(root_);
            }

        }
    }

    private void filter() throws KrnException {
         filteredIds = new ArrayList<Long>();
         if (childrenAttrs.length > 0) {
             KrnClass cls = krn_.getClassNode(childrenAttrs[childrenAttrs.length - 1].type.id).getKrnClass();
             long tid = getCash().getTransactionId();
             KrnObject[] objs = krn_.getClassObjects(cls, new long[] {defaultFilterId}, tid);
             if (objs != null) {
                 for (KrnObject obj : objs) {
                     filteredIds.add(obj.id);
                 }
             }
         }
     }

    public void objectChanged(Object src, long objId, long attrId) {
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        if (root_ != null) {
            if (parentAttrs.length > 0 && attrId == parentAttrs[parentAttrs.length - 1].attr.id) {
                long pObjId = objId;
                try {
                    Record rec = root_.getParent(objId);
                    if (rec != null) {
                        pObjId = ((KrnObject)rec.getValue()).id;
                        Record recTest = root_.getParent(pObjId);
                        if (recTest != null) {
                            long pTestObjId = ((KrnObject)recTest.getValue()).id;
                            if (objId == pTestObjId) {
                                System.out.println("===================== WARNING!!!=================");
                                System.out.println("parent in cikl " + objId);
                                System.out.println("===================== WARNING!!!=================");
                            }
                        }
                    } else {
                        TreePath path = null;
                        path = root_.find(objId, true);
                        if (path == null) {
                                path = root_.find(objId, false);
                        }
                        if (path != null) {
                            path = path.getParentPath();
                            if (path != null) {
                                Node node = (Node) path.getLastPathComponent();
                                pObjId = node.getObject().id;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TreePath path = null;
                path = root_.find(pObjId, true);
                if (path == null) {
                        path = root_.find(pObjId, false);
                }
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null) {
                        node.removeAllChildren();
                        node.isLoaded = false;
                        try {
                            node.loadEx();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        m.nodeStructureChanged(node);
                    }
                }
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                    adapter.getModel().fireTableStructureChanged();
                    adapter.addLeftToTree();
                }
                path = root_.find(objId, true);
                if (path == null) {
                        path = root_.find(objId, false);
                }
                if (path != null) {
                    tree.getSelectionModel().setSelectionPath(path);
                }
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                    adapter.countCurrentTableItem();
/*
                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
*/
                }
            } else if (childrenAttrs.length > 0 && attrId == childrenAttrs[childrenAttrs.length - 1].attr.id) {
                long pObjId = objId;
                TreePath path = null;
                path = root_.find(pObjId, true);
                if (path == null) {
                        path = root_.find(pObjId, false);
                }
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null) {
                        node.removeAllChildren();
                        node.isLoaded = false;
                        try {
                            node.loadEx();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        m.nodeStructureChanged(node);
                    }
                }
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                    adapter.getModel().fireTableStructureChanged();
                    adapter.addLeftToTree();
                }
                path = root_.find(objId, true);
                if (path == null) {
                        path = root_.find(objId, false);
                }
                if (path != null) {
                    tree.getSelectionModel().setSelectionPath(path);
                }
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                    adapter.countCurrentTableItem();
/*
                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
*/
                }
            //} else if (attrId == ((valueAttr != null) ? valueAttr.id : -1)) {
            } else if (valueAttrs.length > 0 && attrId == valueAttrs[valueAttrs.length - 1].attr.id) {
                TreePath path = null;
                if (root_ != null) {
                        path = root_.find(objId, true);
                        if (path == null) {
                                path = root_.find(objId, false);
                        }
                }
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null) {
                        try {
                            node.reloadTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        m.nodeChanged(node);
                    }
                }
/*
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
                }
*/
            } else if (valueAttrs.length > 0 && attrId == valueAttrs[valueAttrs.length - 1].attr.id) {
                TreePath path = null;
                if (root_ != null) {
                        path = root_.find(objId, true);
                        if (path == null) {
                                path = root_.find(objId, false);
                        }
                }
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null) {
                        try {
                            node.reloadTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        m.nodeChanged(node);
                    }
                }
            }
            for (int i = 0; i<titleAttrs.length; i++) {
                if (attrId == titleAttrs[i].id) {
                    TreePath path = null;
                    if (root_ != null) {
                        try {
                            path = root_.findValue(objId, true, i);
                            if (path == null) {
                                    path = root_.findValue(objId, false, i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (path != null) {
                        Node node = (Node) path.getLastPathComponent();
                        if (node != null) {
                            try {
                                node.reloadTitle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            m.nodeChanged(node);
                        }
                        tree.setSelectionPath(path);
                    }
    /*
                    if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                        ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
                    }
    */
                }
            }
            if (titleAttrs2 != null && titleAttrs2.length > 0) {
                for (int i = 0; i<titleAttrs2.length; i++) {
                    if (attrId == titleAttrs2[i].id) {
                        TreePath path = null;
                        if (root_ != null) {
                            try {
                                path = root_.findValue2(objId, true, i);
                                if (path == null) {
                                        path = root_.findValue2(objId, false, i);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (path != null) {
                            Node node = (Node) path.getLastPathComponent();
                            if (node != null) {
                                try {
                                    node.reloadTitle();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                m.nodeChanged(node);
                            }
                            tree.setSelectionPath(path);
                        }
        /*
                        if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                            ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
                        }
        */
                    }
                }
            }
        }
    }

    public void objectDeleted(Cache cache, long classId, long objId) {
    }

    public void objectCreated(Cache cache, long classId, long objId) {
        try {
            if (krn_.isSubclassOf(classId, childrenAttrs[childrenAttrs.length - 1].type.id)) {
                ObjectRecord or = cache.findRecord(new KrnObject(objId, "", classId));
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                    if (!adapter.isHasRows() && or != null) {
                        adapter.getRef().insertItem(-1, or.getValue(), TreeAdapter.this, TreeAdapter.this, true);
                        if (adapter.getTreeRef() != null &&
                                adapter.getTreeRef().toString().equals(adapter.getRef().toString())) {
                            adapter.getTreeRef().insertItem(-1, or.getValue(), TreeAdapter.this, TreeAdapter.this, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefaultFilterId(long objId) {
        defaultFilterId = objId;
    }

    public class Node extends DefaultMutableTreeNode {

    	private String title;

        public Record rec;
        public Record vrec;
        public Record trec;
        public Record trec2;

        public boolean isLeafErik() {
            for (Enumeration ch = children(); ch.hasMoreElements();) {
                Node chn = (Node) ch.nextElement();
                if (chn.getObject() != null)
                    return false;
            }
            return true;
        }

        public Object data;
        public int index = -1;
        public boolean isLoaded;

        //public Node(KrnObject object, KrnObject value, String title)
        public Node(Record rec, Record vrec, Record trec, Record trec2) {
            this.rec = rec;
            this.vrec = vrec;
            this.trec = trec;
            this.trec2 = trec2;
            isLoaded = false;
            
            updateTitle();
        }

        public Node(Record rec) {
            this.rec = rec;
            isLoaded = false;
            try {
	            KrnObject object = (KrnObject)rec.getValue();
	            if (valueAttrs != null && valueAttrs.length > 0) {
		            long[] oids = new long[] {object.id};
		            for (int i = 0; i<valueAttrs.length && oids != null; i++) {
		                SortedSet<Record> recs = getCash().getRecords(oids, valueAttrs[i].attr, 0, null);
		                Record vrec = recs.size() > 0 ? recs.first() : null;
		                if (vrec != null) {
		                    oids = new long[] {((KrnObject)vrec.getValue()).id};
		                } else {
		                    oids = null;
		                }
		                this.vrec = vrec;
		            }
		            if (vrec != null) {
		                KrnObject value = (KrnObject) vrec.getValue();
		                trec = getTitleRecord(value, value.id);
		                trec2 = getTitleRecord2(value, value.id);
		            }
		        } else {
		            trec = getTitleRecord(object, object.id);
		            trec2 = getTitleRecord2(object, object.id);
		        }
		        // Обновляем заголовок
		        updateTitle();
            } catch (KrnException e) {
            	e.printStackTrace();
            }
        }

        public KrnObject getObject() {
            return (rec != null) ? (KrnObject) rec.getValue() : null;
        }

        public KrnObject getValue() {
            return (vrec != null) ? (KrnObject) vrec.getValue() : null;
        }

        public String getTitle() {
            return (trec != null) ? (String) trec.getValue() : null;
        }

        public String getTitle2() {
            return (trec2 != null) ? (String) trec2.getValue() : null;
        }

        public int getChildCount() {
            try {
                loadEx();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getChildCount();
        }

        public String toString() {
            return title;
        }
        
        private void updateTitle() {
        	StringBuilder tb = new StringBuilder();
        	String title1, title2;
        	SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        	if (trec == null) {
        		title1 = null;
        	} else {
        		if (trec.getValue() instanceof KrnDate) {
        			title1 = format.format(trec.getValue());
        		} else {
        			title1 = String.valueOf(trec.getValue());
        		}
        	}
        	if (trec2 == null) {
        		title2 = null;
        	} else {
        		if (trec2.getValue() instanceof KrnDate) {
        			title2 = format.format(trec2.getValue());
        		} else {
        			title2 = String.valueOf(trec2.getValue());
        		}
        	}        	
        	if (title1 != null) {
        		tb.append(title1);
        		if (title2 != null)
        			tb.append(':').append(title2);
        	} else if (title2 != null) {
        		tb.append(title2);
        	}
        	this.title = tb.toString();
        }

        public void reset() {
            if (isLoaded) {
                try {
                    //selfChange = true;
                    /*for (Enumeration en = children(); en.hasMoreElements(); )
                    {
                        Node ch = (Node) en.nextElement();
                        ch.reset();
                    }                 */
                    DefaultTreeModel m = (DefaultTreeModel) tree.getModel();

                    KrnObject object = (KrnObject)rec.getValue();
    	            if (valueAttrs != null && valueAttrs.length > 0) {
    		            long[] oids = new long[] {object.id};
    		            for (int i = 0; i<valueAttrs.length && oids != null; i++) {
    		                SortedSet<Record> recs = cash.getRecords(oids, valueAttrs[i].attr, 0, null);
    		                Record vrec = recs.size() > 0 ? recs.first() : null;
    		                if (vrec != null) {
    		                    oids = new long[] {((KrnObject)vrec.getValue()).id};
    		                } else {
    		                    oids = null;
    		                }
    		                this.vrec = vrec;
    		            }
    		            if (vrec != null) {
    		                KrnObject value = (KrnObject) vrec.getValue();
    		                trec = getTitleRecord(value, value.id);
    		                trec2 = getTitleRecord2(value, value.id);
    		            }
    		        } else {
    		            trec = getTitleRecord(object, object.id);
    		            trec2 = getTitleRecord2(object, object.id);
    		        }
    		        // Обновляем заголовок
    		        updateTitle();
                    reload();
/*
                    removeAllChildren();
                    if (getParent() != null) {
                        m.removeNodeFromParent(this);
                    }
                    m.insertNodeInto(this, (MutableTreeNode) m.getRoot(), 0);
                    m.nodeStructureChanged(this);
                    tree.expandPath(new TreePath(m.getRoot()));
                    isLoaded = false;
*/

                } catch (KrnException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    ///selfChange = false;
                }
            }
        }

        public TreePath find(KrnObject obj, boolean loadedOnly) {
            if (obj == null)
                return null;

            if (rec != null && ((KrnObject) rec.getValue()).id == obj.id)
                return new TreePath(getPath());
            if (!loadedOnly) {
                try {
                    Record prec = getParent(obj.id);
                    List pobjs = new ArrayList();
                    while (prec != null) {
                        pobjs.add(obj);
                        obj = (KrnObject) prec.getValue();
                        prec = getParent(obj.id);
                    }
                    TreePath path = null;
                    for (int i = pobjs.size() - 1; i >= 0; i--) {
                        KrnObject pobj = (KrnObject)pobjs.get(i);
                        loadEx();
                        path = find(pobj, true);
                        if (path != null) {
                            Node node = (Node)path.getLastPathComponent();
                            node.loadEx();
                        }
                    }
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TreePath result = null;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                result = child.find(obj, loadedOnly);
                if (result != null) {
                    Node resNode = (Node)result.getLastPathComponent();
                    try {
                    	resNode.loadEx();
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                    break;
                }
            }
            return result;
        }

        public TreePath find(long objId, boolean loadedOnly) {
            if (rec != null && ((KrnObject) rec.getValue()).id == objId)
                return new TreePath(getPath());
            if (!loadedOnly) {
                try {
                    Record prec = getParent(objId);
                    List pobjs = new ArrayList();
                    while (prec != null) {
                        pobjs.add(objId);
                        objId = ((KrnObject) prec.getValue()).id;
                        prec = getParent(objId);
                    }
                    TreePath path = null;
                    for (int i = pobjs.size() - 1; i >= 0; i--) {
                        long pobjId = (Long)pobjs.get(i);
                        loadEx();
                        path = find(pobjId, true);
                        if (path != null) {
                            Node node = (Node)path.getLastPathComponent();
                            node.loadEx();
                        }
                    }
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TreePath result = null;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                result = child.find(objId, loadedOnly);
                if (result != null)
                    break;
            }
            return result;
        }

        private Record getParent(long id) throws KrnException {
            SortedSet<Record> recs = getCash().getRecords(
            		new long[] {id}, parentAttrs[parentAttrs.length - 1].attr, 0, null);
            return recs.size() > 0 ? recs.first() : null;
        }

        public TreePath find(int index) {
            if (this.index == index)
                return new TreePath(getPath());

            TreePath result = null;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                result = child.find(index);
                if (result != null)
                    break;
            }
            return result;
        }

        private void loadEx() throws KrnException {
            if (!isLoaded) {
                isLoaded = true;
                if (rec == null)
                    return;

//                DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
//                Cache cash = getCash();
                // Загружаем значение и титул
                KrnObject object = (KrnObject) rec.getValue();
                // Обновляем заголовок
//                updateTitle();

                loadChildren(object.id);
//                long[] objIds = {object.id};

/*                if (valueAttrs != null && valueAttrs.length > 0) {
                    long[] oids = objIds;
                    for (int i = 0; i<valueAttrs.length && oids != null; i++) {
                        SortedSet<Record> recs = cash.getRecords(oids, valueAttrs[i].attr, 0, null);
                        Record rec = recs.size() > 0 ? recs.first() : null;
                        if (rec != null) {
                            oids = new long[] {((KrnObject)rec.getValue()).id};
                        } else {
                            oids = null;
                        }
                        vrec = rec;
                    }
                    if (vrec != null) {
                        KrnObject value = (KrnObject) vrec.getValue();
                        trec = getTitleRecord(value, object.id);
                        trec2 = getTitleRecord2(value, object.id);
                    }
                } else {
                    trec = getTitleRecord(object, object.id);
                    trec2 = getTitleRecord2(object, object.id);
                }
                // Обновляем заголовок
                updateTitle();
                // Загрузка детей
                SortedSet<Record> chObjRecs = cash.getRecords(objIds, childrenAttrs[childrenAttrs.length - 1].attr, 0, null);
                for (Record r : chObjRecs) {
                    long id = ((KrnObject)r.getValue()).id;
                    if (filteredIds == null || filteredIds.contains(id) || id < 0) {
                        Node child = new Node(r, null, null, null);
                        m.insertNodeInto(child, this, getChildCount());
                    }
                }
*/            }
        }
        
        private void loadChildren(long objId) throws KrnException {

        	DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            Cache cash = getCash();
        	
            Map<Long, Record> childs = new HashMap<Long, Record>();
            List<Long> chIds = new ArrayList<Long>(); 
            SortedSet<Record> chObjRecs = cash.getRecords(new long[] {objId}, childrenAttrs[childrenAttrs.length - 1].attr, 0, null);
            for (Record r : chObjRecs) {
                long id = ((KrnObject)r.getValue()).id;
                
                if (filteredIds == null || filteredIds.contains(id) || id < 0) {
                	childs.put(id, r);
                	chIds.add(id);
//                	Node child = new Node(r, null, null, null);
//                    m.insertNodeInto(child, this, getChildCount());
                }
            }
            
        	List<Long> firstIds = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) firstIds.add(chId);

        	Map<Long, AttrRecord> values = new HashMap<Long, AttrRecord>();
    		Map<Long, Long> idToId = new HashMap<Long, Long>();

            if (valueAttrs != null && valueAttrs.length > 0) {
		        for (int i = 0; chIds != null && chIds.size() > 0 && i < valueAttrs.length; i++) {
		        	
		        	long[] ids = Funcs.makeLongArray(chIds);

		            KrnAttribute attr = valueAttrs[i].attr;
		            SortedSet<Record> recs = cash.getRecords(ids, attr, 0, null);
		            SortedSet<AttrRecord> attrRecs = new TreeSet<AttrRecord>();
		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) attrRecs.add((AttrRecord)it.next());
		            
		            chIds = new ArrayList<Long>();
		            if (!recs.isEmpty()) {
		            	if (i == 0) {
			            	for (long id : ids) {
				            	AttrRecord r = get(id, attr.id, 0, 0, attrRecs);
				            	
				            	if (r != null) {
					            	KrnObject obj = (KrnObject)r.getValue();
					            	idToId.put(id, obj.id);
					            	chIds.add(obj.id);
					            	if (i == valueAttrs.length - 1) values.put(id, r);
				            	}
			            	}
		            	} else {
		    				for (long id : idToId.keySet()) {
		    					long newId = idToId.get(id);

				            	AttrRecord r = get(newId, attr.id, 0, 0, attrRecs);
				            	
				            	if (r != null) {
					            	KrnObject obj = (KrnObject)r.getValue();
					            	idToId.put(id, obj.id);
					            	chIds.add(obj.id);
					            	if (i == valueAttrs.length - 1) values.put(id, r);
				            	}
		    				}
		            	}
		            }
		        }
            } else {
            	for (long id : childs.keySet()) {
            		idToId.put(id, id);
            		values.put(id, (AttrRecord)childs.get(id));
            	}
            }
        	
        	Map<Long, AttrRecord> titles = new HashMap<Long, AttrRecord>();

        	List<Long> chIds2 = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) chIds2.add(chId);
        	
        	Map<Long, Long> idToId2 = new HashMap<Long, Long>();
        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));
        	
            for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < titleAttrs.length - 1; i++) {
            	long[] ids = Funcs.makeLongArray(chIds2);
            	chIds2 = new ArrayList<Long>();
                SortedSet<Record> recs = cash.getRecords(ids, titleAttrs[i], 0, null);
	            if (!recs.isEmpty()) {
    				SortedSet<AttrRecord> titleRecs = new TreeSet<AttrRecord>();
		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());

		            for (long id : idToId2.keySet()) {
    					long newId = idToId2.get(id);

		            	AttrRecord r = get(newId, titleAttrs[i].id, 0, 0, titleRecs);
		            	
		            	if (r != null) {
			            	KrnObject obj = (KrnObject)r.getValue();
			            	idToId2.put(id, obj.id);
			            	chIds2.add(obj.id);
		            	}
    				}
	            }
            }
            long lid = langId;
        	long[] ids = Funcs.makeLongArray(chIds2);
            KrnAttribute attr = titleAttrs[titleAttrs.length - 1];
            if (lid == 0 && attr.isMultilingual) {
                KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
            SortedSet<AttrRecord> titleRecs = new TreeSet<AttrRecord>();
            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());
            for (long id : idToId2.keySet()) {
				long newId = idToId2.get(id);

            	AttrRecord r = get(newId, attr.id, 0, 0, titleRecs);
            	
            	if (r != null) {
	            	titles.put(id, r);
            	}
			}

        	Map<Long, AttrRecord> titles2 = new HashMap<Long, AttrRecord>();

        	chIds2 = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) chIds2.add(chId);
        	
        	idToId2 = new HashMap<Long, Long>();
        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));

        	if (titleAttrs2 != null) {
                for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < titleAttrs2.length - 1; i++) {
                	ids = Funcs.makeLongArray(chIds2);
                	chIds2 = new ArrayList<Long>();
                    recs = cash.getRecords(ids, titleAttrs2[i], 0, null);
    	            if (!recs.isEmpty()) {
        				titleRecs = new TreeSet<AttrRecord>();
    		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());

    		            for (long id : idToId2.keySet()) {
        					long newId = idToId2.get(id);

    		            	AttrRecord r = get(newId, titleAttrs2[i].id, 0, 0, titleRecs);
    		            	
    		            	if (r != null) {
    			            	KrnObject obj = (KrnObject)r.getValue();
    			            	idToId2.put(id, obj.id);
    			            	chIds2.add(obj.id);
    		            	}
        				}
    	            }
                }
            	ids = Funcs.makeLongArray(chIds2);
                attr = titleAttrs2[titleAttrs2.length - 1];
                recs = cash.getRecords(ids, attr, lid, null);
                titleRecs = new TreeSet<AttrRecord>();
                for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());
                for (long id : idToId2.keySet()) {
    				long newId = idToId2.get(id);

                	AttrRecord r = get(newId, attr.id, 0, 0, titleRecs);
                	
                	if (r != null) {
    	            	titles2.put(id, r);
                	}
    			}
        	}
        	
        	if (sorted) Collections.sort(firstIds, new NodeComparator<Long>(titles, titles2));

        	for (long id : firstIds) {
            	Node child = new Node(childs.get(id), values.get(id), titles.get(id), titles2.get(id));
            	m.insertNodeInto(child, this, getChildCount());
            }
        }

        private AttrRecord get(long objId, long attrId, long langId, int index, SortedSet<AttrRecord> recs) {
            SortedSet<AttrRecord> set = Funcs.find(recs, objId, attrId);

            int i = set.size() - 1;
            int j = index;
            if (j < 0) {
                i = i + 1 + j;
            } else {
                i = j;
            }
            Iterator<AttrRecord> rit = set.iterator();
            AttrRecord r = null;
            while(rit.hasNext() && i-- >= 0) {
            	r = rit.next();
            }
            return r;
        }

        private void reloadChildren(long objId) throws KrnException {

        	DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            Cache cash = getCash();
        	
//            List<Record> childs = new ArrayList<Record>();
            Map<Long, Record> childs = new HashMap<Long, Record>();
            List<Long> chIds = new ArrayList<Long>(); 
            Map<Long, Integer> indexes = new HashMap<Long, Integer>(); 
            Map<Long, Node> childsToReload = new TreeMap<Long, Node>(); 

            SortedSet<Record> chObjRecs = cash.getRecords(new long[] {objId}, childrenAttrs[childrenAttrs.length - 1].attr, 0, null);
            int index = 0;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                if (child.getObject() == null) {
                    index++;
                }
            }
            for (Record r : chObjRecs) {
                long id = ((KrnObject)r.getValue()).id;
                if (filteredIds == null || filteredIds.contains(id) || id < 0) {
                    boolean childExists = false;
                    for (Enumeration c = children(); c.hasMoreElements();) {
                        Node child = (Node) c.nextElement();
                        if (child.getObject() != null && id == child.getObject().id) {

                            childExists = true;
//                            child.reload();
                            childsToReload.put(id, child);
                        	childs.put(id, r);
                        	chIds.add(id);
                        	indexes.put(id, -1);
                            break;
                        }
                    }
                    if (!childExists) {
                    	childs.put(id, r);
                    	chIds.add(id);
                    	indexes.put(id, index);
                    }
                    index++;
                }
            }

        	List<Long> firstIds = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) firstIds.add(chId);

            Map<Long, AttrRecord> values = new HashMap<Long, AttrRecord>();
    		Map<Long, Long> idToId = new HashMap<Long, Long>();

            if (valueAttrs != null && valueAttrs.length > 0) {
		        for (int i = 0; chIds != null && chIds.size() > 0 && i < valueAttrs.length; i++) {
		        	
		        	long[] ids = Funcs.makeLongArray(chIds);

		            KrnAttribute attr = valueAttrs[i].attr;
		            SortedSet<Record> recs = cash.getRecords(ids, attr, 0, null);
		            SortedSet<AttrRecord> attrRecs = new TreeSet<AttrRecord>();
		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) attrRecs.add((AttrRecord)it.next());
		            
		            chIds = new ArrayList<Long>();
		            if (!recs.isEmpty()) {
		            	if (i == 0) {
			            	for (long id : ids) {
				            	AttrRecord r = get(id, attr.id, 0, 0, attrRecs);
				            	
				            	if (r != null) {
					            	KrnObject obj = (KrnObject)r.getValue();
					            	idToId.put(id, obj.id);
					            	chIds.add(obj.id);
					            	if (i == valueAttrs.length - 1) values.put(id, r);
				            	}
			            	}
		            	} else {
		    				for (long id : idToId.keySet()) {
		    					long newId = idToId.get(id);

				            	AttrRecord r = get(newId, attr.id, 0, 0, attrRecs);
				            	
				            	if (r != null) {
					            	KrnObject obj = (KrnObject)r.getValue();
					            	idToId.put(id, obj.id);
					            	chIds.add(obj.id);
					            	if (i == valueAttrs.length - 1) values.put(id, r);
				            	}
		    				}
		            	}
		            }
		        }
            } else {
            	for (long id : childs.keySet()) {
            		idToId.put(id, id);
            		values.put(id, (AttrRecord)childs.get(id));
            	}
            }
        	
        	Map<Long, AttrRecord> titles = new HashMap<Long, AttrRecord>();

        	List<Long> chIds2 = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) chIds2.add(chId);
        	
        	Map<Long, Long> idToId2 = new HashMap<Long, Long>();
        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));
        	
            for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < titleAttrs.length - 1; i++) {
            	long[] ids = Funcs.makeLongArray(chIds2);
            	chIds2 = new ArrayList<Long>();
                SortedSet<Record> recs = cash.getRecords(ids, titleAttrs[i], 0, null);
	            if (!recs.isEmpty()) {
    				SortedSet<AttrRecord> titleRecs = new TreeSet<AttrRecord>();
		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());

		            for (long id : idToId2.keySet()) {
    					long newId = idToId2.get(id);

		            	AttrRecord r = get(newId, titleAttrs[i].id, 0, 0, titleRecs);
		            	
		            	if (r != null) {
			            	KrnObject obj = (KrnObject)r.getValue();
			            	idToId2.put(id, obj.id);
			            	chIds2.add(obj.id);
		            	}
    				}
	            }
            }
        	long[] ids = Funcs.makeLongArray(chIds2);
            KrnAttribute attr = titleAttrs[titleAttrs.length - 1];
            long lid = langId;
            if (lid == 0 && attr.isMultilingual) {
                KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
            SortedSet<Record> recs = cash.getRecords(ids, attr, lid, null);
            SortedSet<AttrRecord> titleRecs = new TreeSet<AttrRecord>();
            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());
            for (long id : idToId2.keySet()) {
				long newId = idToId2.get(id);

            	AttrRecord r = get(newId, attr.id, 0, 0, titleRecs);
            	
            	if (r != null) {
	            	titles.put(id, r);
            	}
			}

        	Map<Long, AttrRecord> titles2 = new HashMap<Long, AttrRecord>();

        	chIds2 = new ArrayList<Long>(chIds.size());
        	for (Long chId : chIds) chIds2.add(chId);
        	
        	idToId2 = new HashMap<Long, Long>();
        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));

        	if (titleAttrs2 != null) {
                for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < titleAttrs2.length - 1; i++) {
                	ids = Funcs.makeLongArray(chIds2);
                	chIds2 = new ArrayList<Long>();
                    recs = cash.getRecords(ids, titleAttrs2[i], 0, null);
    	            if (!recs.isEmpty()) {
        				titleRecs = new TreeSet<AttrRecord>();
    		            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());

    		            for (long id : idToId2.keySet()) {
        					long newId = idToId2.get(id);

    		            	AttrRecord r = get(newId, titleAttrs2[i].id, 0, 0, titleRecs);
    		            	
    		            	if (r != null) {
    			            	KrnObject obj = (KrnObject)r.getValue();
    			            	idToId2.put(id, obj.id);
    			            	chIds2.add(obj.id);
    		            	}
        				}
    	            }
                }
            	ids = Funcs.makeLongArray(chIds2);
                attr = titleAttrs2[titleAttrs2.length - 1];
                recs = cash.getRecords(ids, attr, lid, null);
                titleRecs = new TreeSet<AttrRecord>();
                for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) titleRecs.add((AttrRecord)it.next());
                for (long id : idToId2.keySet()) {
    				long newId = idToId2.get(id);

                	AttrRecord r = get(newId, attr.id, 0, 0, titleRecs);
                	
                	if (r != null) {
    	            	titles2.put(id, r);
                	}
    			}
        	}
        	
        	if (sorted) Collections.sort(firstIds, new NodeComparator<Long>(titles, titles2));

            for (long id : firstIds) {
            	int ind = indexes.get(id);
            	
            	if (ind == -1) {
            		Node child = childsToReload.get(id);
            		child.vrec = values.get(id);
            		child.trec = titles.get(id);
            		child.trec2 = titles2.get(id);
            		child.updateTitle();
            		child.reload();
            	} else {
	            	Node child = new Node(childs.get(id), values.get(id), titles.get(id), titles2.get(id));
	                m.insertNodeInto(child, this, indexes.get(id));
            	}
            }

            boolean childExists = false;
            for (int i = getChildCount() - 1; i >= 0; i--) {
                Node child = (Node) getChildAt(i);
                if (child.getObject() != null) {
                    for (Record r : chObjRecs) {
                        long id = ((KrnObject)r.getValue()).id;
                        if (id == child.getObject().id) {
                            childExists = true;
                            break;
                        }
                    }
                    if (!childExists)
                        m.removeNodeFromParent(child);
                }
            }
        }

        private void reload() throws KrnException {
            if (isLoaded) {
                if (rec == null)
                    return;

                DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
                KrnObject object = (KrnObject) rec.getValue();
                // Загрузка детей
                reloadChildren(object.id);
                m.nodeChanged(this);
            }
        }

        public void reloadTitle() throws KrnException {
            if (rec == null)
                return;

            Cache cash = getCash();
            // Загружаем значение и титул
            KrnObject object = (KrnObject) rec.getValue();
            long[] objIds = {object.id};
/*
            if (valueAttr != null) {
                SortedSet<Record> recs = cash.getRecords(objIds, valueAttr, 0, null);
                Record rec = recs.size() > 0 ? recs.first() : null;
                vrec = rec;
*/
            if (valueAttrs.length > 0) {
                long[] oids = objIds;
                for (int i = 0; i<valueAttrs.length && oids != null; i++) {
                    SortedSet<Record> recs = cash.getRecords(oids, valueAttrs[i].attr, 0, null);
                    Record rec = recs.size() > 0 ? recs.first() : null;
                    if (rec != null) {
                        oids = new long[] {((KrnObject)rec.getValue()).id};
                    } else {
                        oids = null;
                    }
                    vrec = rec;
                }
                if (vrec != null) {
                    KrnObject value = (KrnObject) vrec.getValue();
                    trec = getTitleRecord(value, object.id);
                    trec2 = getTitleRecord2(value, object.id);
                }
            } else {
                trec = getTitleRecord(object, object.id);
                trec2 = getTitleRecord2(object, object.id);
            }
            updateTitle();
        }

        public void reloadTitles() throws KrnException {
            if (rec == null)
                return;

            Cache cash = getCash();
            // Загружаем значение и титул
            KrnObject object = (KrnObject) rec.getValue();
            long[] objIds = {object.id};
/*
            if (valueAttr != null) {
                SortedSet<Record> recs = cash.getRecords(objIds, valueAttr, 0, null);
                Record rec = recs.size() > 0 ? recs.first() : null;
                vrec = rec;
*/
            if (valueAttrs.length > 0) {
                long[] oids = objIds;
                for (int i = 0; i<valueAttrs.length && oids != null; i++) {
                    SortedSet<Record> recs = cash.getRecords(oids, valueAttrs[i].attr, 0, null);
                    Record rec = recs.size() > 0 ? recs.first() : null;
                    if (rec != null) {
                        oids = new long[] {((KrnObject)rec.getValue()).id};
                    } else {
                        oids = null;
                    }
                    vrec = rec;
                }
                if (vrec != null) {
                    KrnObject value = (KrnObject) vrec.getValue();
                    trec = getTitleRecord(value, object.id);
                    trec2 = getTitleRecord2(value, object.id);
                }
            } else {
                trec = getTitleRecord(object, object.id);
                trec2 = getTitleRecord2(object, object.id);
            }
            updateTitle();
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            m.nodeChanged(this);
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                child.reloadTitles();
            }
        }

/*
        private Record getValueRecord(KrnObject value)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            if (valueAttrs == null)
                return null;
            for (int i = 0; i < valueAttrs.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, valueAttrs[i], 0, null);
                if (recs.size() > 0) {
                    rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            long lid = 0;
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, valueAttrs[valueAttrs.length - 1], lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            return rec;
        }
*/

        private Record getTitleRecord(KrnObject value, long id)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            if (titleAttrs == null)
                return null;
            for (int i = 0; i < titleAttrs.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
                if (recs.size() > 0) {
                    rec = null;
                    for (Record r : recs) {
                        if (r.getValue() instanceof KrnObject &&
                                ((KrnObject)r.getValue()).id == id) {
                            rec = r;
                            break;
                        }
                    }
                    if (rec == null) rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            KrnAttribute titleAttr = titleAttrs[titleAttrs.length - 1];
            long lid = langId;
            if (lid == 0 && titleAttr.isMultilingual) {
            	KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttr, lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            if (rec != null && rec.getValue() == null) {
                StringValue[] vs = Kernel.instance().getStringValues(valueIds, titleAttr, lid, false,
                        frame.getCash().getTransactionId());
                for (StringValue v : vs) {
//                    rec = new AttrRecord(v.objectId, titleAttrs[titleAttrs.length - 1].id, lid, v.index,
//                            v.value);
                    cash.changeObjectAttribute(rec, v.value, this);
                }
            }
            return rec;
        }

        private Record getTitleRecord2(KrnObject value, long id)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            if (titleAttrs2 == null)
                return null;
            for (int i = 0; i < titleAttrs2.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[i], 0, null);
                if (recs.size() > 0) {
                    rec = null;
                    for (Record r : recs) {
                        if (r.getValue() instanceof KrnObject &&
                                ((KrnObject)r.getValue()).id == id) {
                            rec = r;
                            break;
                        }
                    }
                    if (rec == null) rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            KrnAttribute titleAttr = titleAttrs2[titleAttrs2.length - 1];
            long lid = langId;
            if (lid == 0 && titleAttr.isMultilingual) {
            	KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttr, lid, null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            return rec;
        }

        public TreePath findValue(long objId, boolean loadedOnly, int index) throws KrnException {
//            Record frec = (valueAttr != null) ? vrec : rec;
            Record frec = (valueAttrs.length > 0) ? vrec : rec;
            if (frec != null) {
                KrnObject value = (KrnObject) frec.getValue();
                long id = ((KrnObject) rec.getValue()).id;
                for (int i = 0; i < index; i++) {
                    long[] valueIds = {value.id};
                    SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
                    if (recs.size() > 0) {
                        Record rec = null;
                        for (Record r : recs) {
                            if (r.getValue() instanceof KrnObject &&
                                    ((KrnObject)r.getValue()).id == id) {
                                rec = r;
                                break;
                            }
                        }
                        if (rec == null) rec = recs.last();

                        value = (KrnObject) rec.getValue();
                    } else {
                        value = null;
                        break;
                    }
                }
                if (value != null && value.id == objId) return new TreePath(getPath());
            }

            if (!loadedOnly) {
                try {
                    KrnObject obj = (KrnObject) rec.getValue();
                    TreeNode parent = getParent();
                    List pobjs = new ArrayList();
                    while (parent instanceof Node) {
                        pobjs.add(objId);
                        //Record prec = (valueAttr != null) ? ((Node)parent).vrec : ((Node)parent).rec;
                        Record prec = (valueAttrs.length > 0) ? ((Node)parent).vrec : ((Node)parent).rec;
                        objId = ((KrnObject) prec.getValue()).id;
                        parent = parent.getParent();
                    }
                    TreePath path = null;
                    for (int i = pobjs.size() - 1; i >= 0; i--) {
                        long pobjId = (Long)pobjs.get(i);
                        loadEx();
                        path = findValue(pobjId, true, index);
                        if (path != null) {
                            Node node = (Node)path.getLastPathComponent();
                            node.loadEx();
                        }
                    }
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TreePath result = null;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                result = child.findValue(objId, loadedOnly, index);
                if (result != null)
                    break;
            }
            return result;
        }

        public TreePath findValue2(long objId, boolean loadedOnly, int index) throws KrnException {
//            Record frec = (valueAttr != null) ? vrec : rec;
            Record frec = (valueAttrs.length > 0) ? vrec : rec;
            if (frec != null) {
                KrnObject value = (KrnObject) frec.getValue();
                long id = ((KrnObject) rec.getValue()).id;
                for (int i = 0; i < index; i++) {
                    long[] valueIds = {value.id};
                    SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[i], 0, null);
                    if (recs.size() > 0) {
                        Record rec = null;
                        for (Record r : recs) {
                            if (r.getValue() instanceof KrnObject &&
                                    ((KrnObject)r.getValue()).id == id) {
                                rec = r;
                                break;
                            }
                        }
                        if (rec == null) rec = recs.last();

                        value = (KrnObject) rec.getValue();
                    } else {
                        value = null;
                        break;
                    }
                }
                if (value != null && value.id == objId) return new TreePath(getPath());
            }

            if (!loadedOnly) {
                try {
                    KrnObject obj = (KrnObject) rec.getValue();
                    TreeNode parent = getParent();
                    List pobjs = new ArrayList();
                    while (parent instanceof Node) {
                        pobjs.add(objId);
//                        Record prec = (valueAttr != null) ? ((Node)parent).vrec : ((Node)parent).rec;
                        Record prec = (valueAttrs.length > 0) ? ((Node)parent).vrec : ((Node)parent).rec;
                        objId = ((KrnObject) prec.getValue()).id;
                        parent = parent.getParent();
                    }
                    TreePath path = null;
                    for (int i = pobjs.size() - 1; i >= 0; i--) {
                        long pobjId = (Long)pobjs.get(i);
                        loadEx();
                        path = findValue2(pobjId, true, index);
                        if (path != null) {
                            Node node = (Node)path.getLastPathComponent();
                            node.loadEx();
                        }
                    }
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TreePath result = null;
            for (Enumeration c = children(); c.hasMoreElements();) {
                Node child = (Node) c.nextElement();
                result = child.findValue2(objId, loadedOnly, index);
                if (result != null)
                    break;
            }
            return result;
        }
    }

    protected Cache getCash() {
        if (cash == null) {
            //final InterfaceManager mgr =
            //        InterfaceManagerFactory.instance().getManager();
            //cash = (mgr != null) ? mgr.getCash() : null;
            //if (cash != null) cash.addCashListener(new TreeCashListener());
            cash = frame.getCash();
            cash.addCashListener(new TreeCashListener());
        }
        return cash;
    }

    protected void preloadParent(long[] pids) throws KrnException {
    	if (pids != null && pids.length > 0)
    		getCash().getRecords(pids, parentAttrs[parentAttrs.length - 1].attr, 0, null);
    }

    private class TreeMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        private void showNodeOperations(MouseEvent e) {
            //if (isEditable) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null && !tree.isPathSelected(path)) {
                tree.setSelectionPath(path);
            }
/*
            if (access == Constants.READ_ONLY_ACCESS) {
                nodeRenameItem_.setEnabled(false);
                nodeCreateItem_.setEnabled(false);
                nodeChangeItem_.setEnabled(false);
                nodeDeleteItem_.setEnabled(false);
            } else {
                nodeRenameItem_.setEnabled(true);
                nodeCreateItem_.setEnabled(true);
                nodeChangeItem_.setEnabled(true);
                nodeDeleteItem_.setEnabled(true);
            }
*/
            if (e.getComponent() instanceof OrTreeTable.TreeTableCellRenderer) {
                OrTreeTable.TreeTableCellRenderer rend = (OrTreeTable.TreeTableCellRenderer)e.getComponent();
                if (!rend.getTableAdapter().isHasRows()) {
                    Component comp = rend.getTable();
                    nodeOperations_.show(comp, e.getX(), e.getY());
                }
            } else
                nodeOperations_.show(e.getComponent(), e.getX(), e.getY());
            //}
        }
    }

    private class TreeCashListener implements DataCashListener {
        public void cashCleared() {
        	if (refreshMode == Constants.RM_DIRECTLY) {
	            try {
	                //setRoot(null);
                        //if (getRoot() != null) {
                        //    getRoot().reload();
                        //}
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
        	}
        }

        public void beforeCommitted() {
        }

        public void cashCommitted() {
            // NOP
        }

        public void cashRollbacked() {
            //if (refreshMode > 0)
        }
    }

    public void expandAll(Node node) {
        if (node != null && !node.isLeaf()) {
            tree.expandPath(new TreePath(node.getPath()));
            Enumeration childNodes = node.children();
            while(childNodes.hasMoreElements()) {
                Node child = (Node)childNodes.nextElement();
                expandAll(child);
            }
        }
    }

    public void collapseAll(Node node) {
        if (node != null) {
            Enumeration childNodes = node.children();
            while(childNodes.hasMoreElements()) {
                Node child = (Node)childNodes.nextElement();
                collapseAll(child);
            }
            if (!node.isLeaf()) {
                tree.collapsePath(new TreePath(node.getPath()));
            }
        }
    }

    private class NodeOperationsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            Cache cash = getCash();
            JComponent comp = (tree instanceof OrTreeTable.TreeTableCellRenderer)
                ? ((OrTreeTable.TreeTableCellRenderer)tree).getTable()
                : tree;
            try {
                if (src == nodeCreateItem_) {
                    createNewNode(comp);
                } else  if (src == nodeCreateWithHistoryItem_) {
                    Pair p = getSelectedValue(comp);
                    if (p != null) {
                        KrnObject v = (KrnObject) p.first;
                        String title = (String) p.second;
                        createNode(new ObjectRecord(v.classId, v), title);
                    }
                } else if (src == nodeChangeItem_) {
                    Pair p = getSelectedValue(comp);
                    if (p != null) {
                        Node node = getSelectedNode();
                        if (node != null) {
/*
                            if (valueAttr != null) {
                                cash.deleteObjectAttribute(node.vrec, this);
                                cash.insertObjectAttribute(node.getObject(), valueAttr, 0, 0, p.first, this);
*/
                            if (valueAttrs.length > 0) {
                                cash.deleteObjectAttribute(node.vrec, this, false);
                                long[] oids = new long[] {node.getObject().id};
                                KrnObject obj = node.getObject();
                                for (int i = 0; i<valueAttrs.length - 1 && oids != null; i++) {
                                    SortedSet<Record> recs = cash.getRecords(oids, valueAttrs[i].attr, 0, null);
                                    Record rec = recs.size() > 0 ? recs.first() : null;
                                    if (rec != null) {
                                        oids = new long[] {((KrnObject)rec.getValue()).id};
                                        obj = (KrnObject)rec.getValue();
                                    } else {
                                        oids = null;
                                        obj = null;
                                    }
                                }
                                if (obj != null)
                                    cash.insertObjectAttribute(obj, valueAttrs[valueAttrs.length - 1].attr, 0, 0, p.first, this);
                            } else {

                            }
                        }
                    }
                } else if (src == nodeDeleteItem_) {
                    if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                        TreeTableAdapter a =
                                ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
                        a.deleteRow();
                    } else {
                        deleteNode(comp);
                    }
                } else if (src == nodeRenameItem_) {
                    Node n = getSelectedNode();
                    if (n != null) {
                        Container cnt = comp.getTopLevelAncestor();
                        DesignerDialog dlg = null;
                        String name = null;
                        if (titleAttrs2 != null &&
                            titleAttrs2.length > 0)
                            name = n.getTitle2();
                        else
                            name = n.getTitle();

                        CreateElementPanel cp = new CreateElementPanel(
                                CreateElementPanel.RENAME_TYPE, name, ifcLangId);
                        if (cnt instanceof Dialog) {
                            dlg = new DesignerDialog((Dialog)cnt, res.getString("renameNodeTitle"), cp);
                        } else {
                            dlg = new DesignerDialog((Frame)cnt, res.getString("renameNodeTitle"), cp);
                        }
                        dlg.setLanguage(frame.getInterfaceLang().id);
                        dlg.show();
                        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                            String title = cp.getElementName();
                            if (title != null) {
                                if (titleAttrs2 != null &&
                                    titleAttrs2.length > 0) {
                                    if (n.trec2 != null)
                                        cash.changeObjectAttribute(n.trec2, title, null);
                                    else {
                                        long lid = (langId == 0)
                                                ? InterfaceManagerFactory.instance().getManager().getDataLang().id : langId;
                                        KrnObject tho2 = getTitleHolderObject2(
                                                //(valueAttr != null)
                                                (valueAttrs.length > 0)
                                                        ? (KrnObject) n.vrec.getValue()
                                                        : (KrnObject) n.rec.getValue());
                                        cash.insertObjectAttribute(tho2,
                                            titleAttrs2[titleAttrs2.length - 1], 0, lid, title, this);
                                    }
                                } else {
                                    cash.changeObjectAttribute(n.trec, title, null);
                                }
                            }
                        }
                    }
                } else if (src == nodeSelectChildren_) {
                    selectChildren();
                } else if (src == expandItem) {
                    expandAll(getSelectedNode());
                } else if (src == collapsItem) {
                    collapseAll(getSelectedNode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private Pair getSelectedValue(JComponent comp) throws KrnException {
            List list = new ArrayList();
            long trId = cash.getTransactionId();
/*
            KrnObject[] objs = (valueAttr != null)
                    ? krn_.getClassObjects(krn_.getClassById(valueAttr.typeClassId), trId)
                    : krn_.getClassObjects(krn_.getClassById(childrenAttr.typeClassId), trId);
*/
            KrnObject[] objs = (valueAttrs.length > 0)
                    ? krn_.getClassObjects(krn_.getClassNode(valueAttrs[valueAttrs.length - 1].type.id).getKrnClass(), trId)
                    : krn_.getClassObjects(krn_.getClassNode(childrenAttrs[childrenAttrs.length - 1].type.id).getKrnClass(), trId);
            if (objs.length > 0) {
                List items = new ArrayList();
                for (KrnObject obj : objs) {
                    String name = null;
                    if (titleAttrs2 != null &&
                        titleAttrs2.length > 0) {
                        Record rec2 = getTitleRecord2(obj);
                        name = (rec2 != null && rec2.getValue() != null
                                ? rec2.getValue().toString() : "");
                    } else {
                        Record rec = getTitleRecord(obj);
                        name = (rec != null && rec.getValue() != null)
                                ? rec.getValue().toString() : "";
                    }
                    items.add(new Item(obj, name));
                }
                Collections.sort(items);
                for (Iterator it = items.iterator(); it.hasNext();)
                    list.add(it.next());
            }
            DesignerDialog dlg = null;
            Container cnt = comp.getTopLevelAncestor();
            SelectObjectPanel sop = new SelectObjectPanel(list);
            sop.setPreferredSize(new Dimension(600, 600));
            if (cnt instanceof Dialog) {
                dlg = new DesignerDialog((Dialog)cnt, res.getString("bindNodeTitle"), sop);
            } else {
                dlg = new DesignerDialog((Frame)cnt, res.getString("bindNodeTitle"), sop);
            }
            dlg.setLanguage(frame.getInterfaceLang().id);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                return sop.getSelectedObject();
            }
            return null;
        }

        private Record getTitleRecord(KrnObject value)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            for (int i = 0; i < titleAttrs.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
                if (recs.size() > 0) {
                    rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            long lid = (langId == 0) ? frame.getDataLang().id : langId;
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[titleAttrs.length - 1], lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            return rec;
        }

        private Record getTitleRecord2(KrnObject value)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            for (int i = 0; i < titleAttrs2.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[i], 0, null);
                if (recs.size() > 0) {
                    rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            long lid = (langId == 0) ? frame.getDataLang().id : langId;
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[titleAttrs2.length - 1], lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            return rec;
        }
    }

    public KrnObject createNewNode(JComponent comp) throws KrnException{
        String title = getNewTitle(comp);
        if (title != null) {
            Record vrec = null;
//            if (valueAttr != null)
//                vrec = cash.createObject(valueAttr.typeClassId);
            Record tempRec = null;
            if (valueAttrs.length > 0) {
                for (int i=0; i<valueAttrs.length; i++) {
                    Record rec = cash.createObject(valueAttrs[i].type.id);
                    if (tempRec != null) {
                        cash.insertObjectAttribute((KrnObject)tempRec.getValue(), valueAttrs[i-1].attr, 0, 0, rec.getValue(), this);
                    } else {
                        vrec = rec;
                    }
                    tempRec = rec;
                }
            }
            KrnObject obj = createNode(vrec, title);
            return obj;
        }
        return null;
    }

    private String getNewTitle(JComponent comp) {
        CreateElementPanel cp = new CreateElementPanel(
                CreateElementPanel.CREATE_ELEMENT_TYPE, "");
        DesignerDialog dlg = null;
        Container cnt = comp.getTopLevelAncestor();
        if (cnt instanceof Dialog) {
            dlg = new DesignerDialog((Dialog)cnt, res.getString("createNodeTitle"), cp);
        } else {
            dlg = new DesignerDialog((Frame)cnt, res.getString("createNodeTitle"), cp);
        }
        dlg.setLanguage(frame.getInterfaceLang().id);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            return cp.getElementName();
        }
        return null;
    }

    public void deleteNode(JComponent comp) throws KrnException {
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        Node[] nodes = getSelectedNodes();
        Container cnt = comp.getTopLevelAncestor();
        int res = -1;
        LangItem li = LangItem.getById(frame.getInterfaceLang().id);
        if (cnt instanceof Frame) {
            res = MessagesFactory.showMessageDialog((Frame)cnt,
                    MessagesFactory.CONFIRM_MESSAGE, this.res.getString("deleteNodeTitle"), li);
        } else {
            res = MessagesFactory.showMessageDialog((Dialog)cnt,
                    MessagesFactory.CONFIRM_MESSAGE, this.res.getString("deleteNodeTitle"), li);
        }
        if (res == ButtonsFactory.BUTTON_YES) {
            try {
                if (doBeforeDelete())
                    return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < nodes.length; i++) {
                Node n = nodes[i];
                int row = tree.getRowForPath(new TreePath(n.getPath()));
                if (n != null) {
//                    if (valueAttr != null)
                    if (valueAttrs.length > 0)
                        cash.deleteObjectAttribute(n.vrec, this, false);
                    cash.deleteObjectAttribute(n.rec, this, false);
                    Record rec = cash.findRecord((KrnObject)n.rec.getValue());
                    cash.deleteObject(rec, this);
                    //m.removeNodeFromParent(n);
                }
//                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
//                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
//                    adapter.getModel().fireTableRowsDeleted(row, row);
//                    adapter.countCurrentTableItem();
//                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
//                }
            }
            try {
				if (!doAfterDelete())
					return;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    public void deleteNode(ASTStart beforDelFX, ASTStart afterDelFX, List<Object> selVect,
                           Map<Object, TreeAdapter.Node> sitems, OrTable table, TreeTableAdapter a) throws KrnException {
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        //Node[] nodes = getSelectedNodes();

        Container cnt = table.getJTable().getTopLevelAncestor();
        int res = -1;
        LangItem li = LangItem.getById(frame.getInterfaceLang().id);
        if (cnt instanceof Frame) {
            res = MessagesFactory.showMessageDialog((Frame)cnt,
                    MessagesFactory.CONFIRM_MESSAGE, this.res.getString("deleteNodeTitle"), li);
        } else {
            res = MessagesFactory.showMessageDialog((Dialog)cnt,
                    MessagesFactory.CONFIRM_MESSAGE, this.res.getString("deleteNodeTitle"), li);
        }
        if (res == ButtonsFactory.BUTTON_YES) {
            //формула до удаления
            if (beforDelFX != null) {
                ClientOrLang orlang = new ClientOrLang(frame);
                Map<String, Object> vc = new HashMap<String, Object>();
                vc.put("SELOBJS", selVect);
                try {
                    orlang.evaluate(beforDelFX, vc, a, new Stack<String>());
                } catch (Exception e) {
                    Util.showErrorMessage(table, e.getMessage(), this.res.getString("beforeDeleteAction"));
                }
            }

            sitems.keySet().retainAll(selVect);

            for (Node n : sitems.values()) {
                int row = tree.getRowForPath(new TreePath(n.getPath()));
                if (n != null) {
                    //m.removeNodeFromParent(n);
/*
                    if (valueAttr != null)
                        cash.deleteObjectAttribute(n.vrec, this);
*/
                    if (valueAttrs.length > 0)
                        cash.deleteObjectAttribute(n.vrec, this, false);
                    cash.deleteObjectAttribute(n.rec, this, false);
                    Record rec = cash.findRecord((KrnObject)n.rec.getValue());
                    cash.deleteObject(rec, this);
                }
//                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
//                    TreeTableAdapter adapter = ((OrTreeTable.TreeTableCellRenderer)tree).getTableAdapter();
//                    adapter.getModel().fireTableRowsDeleted(row, row);
//                    adapter.countCurrentTableItem();
//                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
//                }
            }

            //формула после удаления
            if (afterDelFX != null) {
                ClientOrLang orlang = new ClientOrLang(frame);
                Map<String, Object> vc = new HashMap<String, Object>();
                vc.put("SELOBJS", selVect);
                try {
                    orlang.evaluate(afterDelFX, vc, a, new Stack<String>());
                } catch (Exception e) {
                    Util.showErrorMessage(table, e.getMessage(), this.res.getString("afterDeleteAction"));
                }
            }
        }
    }

    public KrnObject createNode(Record vrec, String title) throws KrnException {
        Node node = getSelectedNode();
        KrnClass t = (node == null) ? rootRef.getType() : childrenAttrs[childrenAttrs.length - 1].type;
        KrnObject newNodeObj = (KrnObject) cash.createObject(t.id).getValue();
        long lid = (langId == 0)
                ? InterfaceManagerFactory.instance().getManager().getDataLang().id : langId;

/*
        KrnObject vobj = (valueAttr != null) ? (KrnObject) vrec.getValue() : newNodeObj;
        if (valueAttr != null)
            cash.insertObjectAttribute(newNodeObj, valueAttr, 0, 0, vrec.getValue(), this);
*/
        KrnObject vobj = (valueAttrs.length > 0) ? (KrnObject) vrec.getValue() : newNodeObj;
        if (valueAttrs.length > 0)
            cash.insertObjectAttribute(newNodeObj, valueAttrs[0].attr, 0, 0, vrec.getValue(), this);

        Record trec = null, trec2 = null;
        if (titleAttrs2 != null && titleAttrs2.length > 0) {
            KrnObject tho2 = getTitleHolderObject2(vobj);
            trec2 = cash.insertObjectAttribute(tho2,
                titleAttrs2[titleAttrs2.length - 1], 0, lid, title, this);
        } else {
            KrnObject tho = getTitleHolderObject(vobj);
            trec = cash.insertObjectAttribute(tho,
                titleAttrs[titleAttrs.length - 1], 0, lid, title, this);
        }
        if (node != null) {
            Record rec = cash.insertObjectAttribute(node.getObject(), childrenAttrs[childrenAttrs.length - 1].attr, -1, 0, newNodeObj, this);
/*
            Node newNode = new Node(rec, vrec, trec, null);
            DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
            m.insertNodeInto(newNode, node, node.getChildCount());
*/
        } else {
            rootRef.insertItem(0, newNodeObj, null,
                    TreeAdapter.this, false);
            DefaultTreeModel m = new DefaultTreeModel(new Node(null, vrec, trec, trec2));
            tree.setModel(m);
        }
        return newNodeObj;
    }

    private KrnObject getTitleHolderObject(KrnObject obj) throws KrnException {
        for (int i = 0; i < titleAttrs.length - 1; i++) {
            long[] valueIds = {obj.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
            if (recs.size() > 0) {
                Record rec = recs.last();
                obj = (KrnObject) rec.getValue();
            } else {
/*
                Record rec = cash.createObject(titleAttrs[i].typeClassId);
                cash.insertObjectAttribute(obj, titleAttrs[i], 0, 0, rec.getValue(), TreeAdapter.this);
                obj = (KrnObject) rec.getValue();
*/
                return null;
            }
        }
        return obj;
    }

    private KrnObject getTitleHolderObject2(KrnObject obj) throws KrnException {
        for (int i = 0; i < titleAttrs2.length - 1; i++) {
            long[] valueIds = {obj.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[i], 0, null);
            if (recs.size() > 0) {
                Record rec = recs.last();
                obj = (KrnObject) rec.getValue();
            } else {
/*
                Record rec = cash.createObject(titleAttrs2[i].typeClassId);
                cash.insertObjectAttribute(obj, titleAttrs2[i], 0, 0, rec.getValue(), TreeAdapter.this);
                obj = (KrnObject) rec.getValue();
*/
                return null;
            }
        }
        return obj;
    }

    public Node getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        return (path != null) ? (Node) path.getLastPathComponent() : null;
    }

    public Node[] getSelectedNodes() {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null)
            return new Node[0];
        Node[] res = new Node[paths.length];
        for (int i = 0; i < paths.length; ++i)
            res[i] = (Node) paths[i].getLastPathComponent();
        return res;
    }


    private void selectChildren() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            Stack s = new Stack();
            s.push(path.getLastPathComponent());
            while (!s.empty()) {
                Node node = (Node) s.pop();
                int count = node.getChildCount();
                for (int i = 0; i < count; ++i) {
                    Node child = (Node) node.getChildAt(i);
                    tree.addSelectionPath(new TreePath(child.getPath()));
                    if (!child.isLeaf())
                        s.push(child);
                }
            }
        }
    }

    public OrTree getTree() {
        return (OrTree) tree;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        if (nodeRenameItem_ != null) nodeRenameItem_.setEnabled(isEnabled);
        if (nodeCreateItem_ != null) nodeCreateItem_.setEnabled(isEnabled);
        if (nodeCreateWithHistoryItem_ != null) nodeCreateWithHistoryItem_.setEnabled(isEnabled);
        if (nodeChangeItem_ != null) nodeChangeItem_.setEnabled(isEnabled);
        if (nodeDeleteItem_ != null) nodeDeleteItem_.setEnabled(isEnabled);

    }

    public long getLangId() {
        return 0;
    }
    class Item extends Pair implements Comparable {
        public Item(Object first, Object second) {
            super(first, second);
        }
        public String toString() {
            return second.toString();
        }
        public int compareTo(Object o) {
            int res = 1;
            if (o instanceof Pair)
                res = ((Comparable) second).compareTo(((Pair) o).second);
            return res;
        }
    }

    public void setLangId(long langId) {
        this.ifcLangId = langId;
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
            } else {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("ru"));
            }
            changeTitles(res);
            if (root_ != null) {
                try {
                    root_.reloadTitles();
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
                }
*/
            }
        }
    }

    public boolean isRootUpdated() {
        return rootUpdated;
    }

    public boolean isRootChanged() {
        return rootChanged;
    }

    public void setRootChanged(boolean rootChanged) {
        this.rootChanged = rootChanged;
    }

    private void changeTitles(ResourceBundle res) {
        if (nodeRenameItem_ != null) nodeRenameItem_.setText(res.getString("renameNode"));
        if (nodeChangeItem_ != null) nodeChangeItem_.setText(res.getString("changeNode"));
        if (nodeCreateItem_ != null) nodeCreateItem_.setText(res.getString("createNode"));
        if (nodeCreateWithHistoryItem_ != null) nodeCreateWithHistoryItem_.setText(res.getString("createNodeAndBind"));
        if (nodeDeleteItem_ != null) nodeDeleteItem_.setText(res.getString("deleteNode"));
        if (expandItem != null) expandItem.setText(res.getString("expandNode"));
        if (collapsItem != null) collapsItem.setText(res.getString("collapseNode"));
    }

    public List getFilteredIds() {
        return filteredIds;
    }
    
    protected boolean doBeforeDelete() throws Exception {
        if (beforeDelAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJ", getSelectedNode().getObject());
            boolean calcOwner = OrCalcRef.setCalculations();
            orlang.evaluate(beforeDelAction, vc, this, new Stack<String>());
			if (calcOwner)
				OrCalcRef.makeCalculations();
			return ((Integer) vc.get("RETURN")) == 1 ? true : false;
        }
		return false;
    }
    
    protected boolean doAfterDelete() throws Exception {
        if (afterDelAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJ", getSelectedNode().getObject());
            boolean calcOwner = OrCalcRef.setCalculations();
            orlang.evaluate(afterDelAction, vc, this, new Stack<String>());
			if (calcOwner)
				OrCalcRef.makeCalculations();
			return ((Integer) vc.get("RETURN")) == 1 ? true : false;
        }
		return false;
    }

    private class NodeComparator<Long> implements Comparator<Long> {
    	private Map<Long, AttrRecord> titles;
    	private Map<Long, AttrRecord> titles2;

		public NodeComparator(Map<Long, AttrRecord> titles,
				Map<Long, AttrRecord> titles2) {
			
			this.titles = titles;
			this.titles2 = titles2;
		}

		@Override
		public int compare(Long o1, Long o2) {
			AttrRecord a1 = titles.get(o1);
			AttrRecord a2 = titles.get(o2);

			if (a1 == null && a2 == null) return 0;
			else if (a1 == null && a2 != null) return 1;
			else if (a1 != null && a2 == null) return -1;
			else {
				String s1 = (String) a1.getValue();
				String s2 = (String) a2.getValue();
				
				if (s1 == null && s2 == null) return 0;
				else if (s1 == null && s2 != null) return 1;
				else if (s1 != null && s2 == null) return -1;
				else if (s1.equals(s2)) {
					a1 = titles2.get(o1);
					a2 = titles2.get(o2);

					if (a1 == null && a2 == null) return 0;
					else if (a1 == null && a2 != null) return 1;
					else if (a1 != null && a2 == null) return -1;
					else {
						s1 = (String) a1.getValue();
						s2 = (String) a2.getValue();
						
						if (s1 == null && s2 == null) return 0;
						else if (s1 == null && s2 != null) return 1;
						else if (s1 != null && s2 == null) return -1;
						return s1.compareTo(s2);
					}
				} else
					return s1.compareTo(s2);
			}
		}
    }
/*
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        lastLocation = dtde.getLocation().getY();
        isDragStarted = false;
        Point loc = dtde.getLocation();
        int idx = tree.getRowForLocation(loc.x, loc.y);
        if (idx != -1) {
            ///AbstractDesignerTreeCellRenderer cr =
            //        (AbstractDesignerTreeCellRenderer)tree.getCellRenderer();
            //if (cr.setDragRow(idx))
            //    repaint();
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    public void drop(DropTargetDropEvent dtde) {
        int res = ButtonsFactory.BUTTON_NO;
        try {
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                KrnObject krnObj = (KrnObject) transferable.getTransferData(
                        DataFlavor.stringFlavor);
                Node s =
                        (Node) getRoot().find(krnObj, true).getLastPathComponent();
                String mes = (s.isLeaf()) ? "Переместить элемент '" : "Переместить папку '";

                Container cnt = tree.getTopLevelAncestor();
                if (cnt instanceof Dialog) {
                    res = MessagesFactory.showMessageDialog((Dialog)cnt,
                            MessagesFactory.QUESTION_MESSAGE, mes +
                            s.toString() + "' ?");
                } else {
                    res = MessagesFactory.showMessageDialog((Frame)cnt,
                            MessagesFactory.QUESTION_MESSAGE, mes +
                            s.toString() + "' ?");
                }
                if (res == ButtonsFactory.BUTTON_YES) {
                    dtde.getDropTargetContext().dropComplete(addElement(s, dtde.getLocation()));
                }

//                AbstractDesignerTreeCellRenderer cr =
//                        (AbstractDesignerTreeCellRenderer)getCellRenderer();
//                cr.setDragRow(-1);
//                repaint();
//
            } else {
                dtde.rejectDrop();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            dtde.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
            dtde.rejectDrop();
        }
    }

    public boolean addElement(Object s, Point location) {
        try {
            Node firstNode = (Node) s;
            Node secondNode = null;

            TreePath tp = tree.getPathForLocation(location.x, location.y);
            if (tp != null) {
                secondNode = (Node) tp.getLastPathComponent();
            }
            TreeNode tempNode = secondNode;
            while (tempNode instanceof Node) {
                if (tempNode.equals(firstNode)) {
                    System.out.println("Нельзя переместить узел внутрь самого себя");
                    return false;
                }
                tempNode = tempNode.getParent();
            }
            if (firstNode != null && secondNode != null) {
                Node parent = null;
                if (!secondNode.isLeaf()) {
                    parent = secondNode;
                } else {
                    parent = (Node)secondNode.getParent();
                }
                if (parent == null) {
                    System.out.println("Нельзя вынести узел на один уровень с корневым узлом");
                    return false;
                }

                if (getRoot() != null) {
                    parent = (Node)secondNode.getParent();

                    cash.deleteObjectAttribute(firstNode.rec, this);

                    cash.insertObjectAttribute(parent.getObject(), childrenAttrs[childrenAttrs.length - 1].attr, secondNode.rec.getIndex(), 0, firstNode.getObject(), this);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public void dragExit(DropTargetEvent dte) {
        isDragStarted = true;
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {

    }

    public void dragDropEnd(DragSourceDropEvent dsde) {

    }

    public void dragExit(DragSourceEvent dse) {

    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        Node node = getSelectedNode();
        if (node != null) {
            TransferableObject selected = new TransferableObject(node.getObject());
            dragSource.startDrag(dge, DragSource.DefaultMoveDrop, selected, this);
        }
    }

    public class TransferableObject implements Transferable {
        private static final int STRING = 0;
        private final DataFlavor[] flavors = {DataFlavor.stringFlavor};
        private KrnObject transfer;

        public TransferableObject(KrnObject transfer) {
            this.transfer = transfer;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return (DataFlavor[]) flavors.clone();
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(flavor)) return true;
            }
            return false;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (flavor.equals(flavors[STRING]))
                return transfer;
            else
                throw new UnsupportedFlavorException(flavor);
        }
    }
*/
}