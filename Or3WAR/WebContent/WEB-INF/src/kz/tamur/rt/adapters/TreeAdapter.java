package kz.tamur.rt.adapters;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.*;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.Value;
import kz.tamur.rt.RuntimeException;
import kz.tamur.rt.data.AttrRecord;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.CashChangeListener;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.component.OrWebTree;
import kz.tamur.web.component.OrWebTreeTable;

import javax.swing.tree.*;

import java.util.*;

public class TreeAdapter extends ComponentAdapter implements CashChangeListener {

    protected OrTreeComponent tree;
    private Record rootRec_;
    protected Node root_;
    protected Cache cash;
    //protected KrnAttribute childrenAttr;
    //protected KrnAttribute parentAttr;
    //protected KrnAttribute valueAttr;
    //protected KrnClass childType;
    protected KrnAttribute[] titleAttrs;
    protected KrnAttribute[] titleAttrs2;
    protected KrnAttribute[] sortAttrs;

    protected PathElement2[] valueAttrs;
    protected PathElement2[] parentAttrs;
    protected PathElement2[] childrenAttrs;
    protected PathElement2[] valueSortAttrs;

    protected String titlePath, oldTitlePath;
    protected String titlePath2, oldTitlePath2;
    protected ASTStart titleExprFX;
    protected String sortPath, oldSortPath;
    public OrRef rootRef;
    private int access = Constants.FULL_ACCESS;
    private boolean rootUpdated = false;
    private int refreshMode = 0;
    private long defaultFilterId;
    private List<Long> filteredIds;
    private ClassNode cn;

    protected ResourceBundle res = ResourceBundle.getBundle(
            Constants.NAME_RESOURCES, new Locale("ru"));
    private long ifcLangId = 0;
    public OrCalcRef rootCalcRef;
    private Kernel krn_;
    private boolean rootChanged = false;
    private boolean wClickAsOK;
    protected boolean sorted = false;

    public TreeAdapter(OrFrame frame, OrTreeComponent tree, boolean isEditor)
            throws KrnException {
        super(frame, tree, isEditor);
        krn_ = frame.getKernel();
        PropertyNode proot = tree.getProperties();
        this.tree = tree;
        //this.tree.setOpaque(true);
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

        pv = tree.getPropertyValue(proot.getChild("ref").getChild("rootExpr"));
        String rootExpr = null;
        if (!pv.isNull()) {
            rootExpr = pv.stringValue(frame.getKernel());
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
        
        rprop = proot.getChild("ref").getChild("rootRef");
        pv = tree.getPropertyValue(rprop);
        
        String rootPath = null;
        if (!pv.isNull()) {
            try {
            	rootPath = pv.stringValue(frame.getKernel());
                propertyName = "Свойство: Корень";
                
                if (rootExpr == null || rootExpr.length() == 0) {
	                if (refreshMode == Constants.RM_DIRECTLY) {
	                    boolean hasParentRef = false;
	                    Map<String, OrRef> refs = frame.getRefs();
	                    Iterator<String> it = refs.keySet().iterator();
	                    while (it.hasNext()) {
	                        String key = it.next();
	                        OrRef ref = refs.get(key);
	                        if (ref != null && rootPath.startsWith(ref.toString())) {
	                            hasParentRef = true;
	                            break;
	                        }
	                    }
	                    if (!hasParentRef)
	                        rootRef = OrRef.createContentRef(rootPath,
	                                Constants.RM_ALWAYS, Mode.RUNTIME,
	                                frame.getTransactionIsolation(), frame);
	                    else
	                        rootRef = OrRef.createRef(rootPath, true, Mode.RUNTIME,
	                                frame.getRefs(), frame.getTransactionIsolation(), frame);
	                } else {
	                    rootRef = OrRef.createContentRef(rootPath, refreshMode, Mode.RUNTIME,
	                             frame.getTransactionIsolation(), frame);
	                }
	                rootRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }

        
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath"));
        if (!pv.isNull()) {
            titlePath = pv.stringValue(frame.getKernel());
        }

        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePath2"));
        if (!pv.isNull()) {
            titlePath2 = pv.stringValue(frame.getKernel());
        }

        String titleExpr = null;
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("titlePathExpr"));
        if (!pv.isNull()) {
            titleExpr = pv.stringValue(frame.getKernel());
        }
        if (titleExpr != null && titleExpr.length() > 0) {
            propertyName = "Свойство: Титулы(Формула)";
            try {
            	if (tree instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)tree).getId() + "_" + OrLang.TITLE_EXPR_TEXT;
                	titleExprFX = ClientOrLang.getStaticTemplate(ifcId, key, titleExpr, getLog());
            	} else {
            		titleExprFX = OrLang.createStaticTemplate(titleExpr, log);
            	}
                Editor e = new Editor(titleExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("sortPath"));
        if (!pv.isNull()) {
            sortPath = pv.stringValue(frame.getKernel());
        }
        Cache cash = getCash();
        if (rootRef != null)
			cn = krn_.getClassNode(rootRef.getType().id);
		else if (rootPath != null) {
			cn = krn_.getClassNodeByName(rootPath);
		} else if (rootCalcRef != null) {
            OrRef.Item item = rootCalcRef.getItem();
            KrnObject obj = (KrnObject) ((item != null) ? item.getCurrent() : null);
            if (obj != null && obj.classId > 0 )
            	cn = krn_.getClassNode(obj.classId);
		}
		
        if (titlePath != null && titlePath.length() > 0) {
            titleAttrs = Utils.getAttributesForPath(titlePath, frame.getKernel());
            for (int i = 0; i<titleAttrs.length; i++) {
                cash.addCashChangeListener(titleAttrs[i].id, this, frame);
            }
        }
        if (titlePath2 != null && titlePath2.length() > 0) {
            titleAttrs2 = Utils.getAttributesForPath(titlePath2, frame.getKernel());
            for (int i = 0; i<titleAttrs2.length; i++) {
                cash.addCashChangeListener(titleAttrs2[i].id, this, frame);
            }
        }

        if (sortPath != null && sortPath.length() > 0) {
            sortAttrs = Utils.getAttributesForPath(sortPath, frame.getKernel());
            for (int i = 0; i<sortAttrs.length; i++) {
                cash.addCashChangeListener(sortAttrs[i].id, this, frame);
            }
        }
        String valuePath = null;
        pv = tree.getPropertyValue(proot.getChild("ref").getChild("valueRef"));
        if (!pv.isNull()) {
            valuePath = pv.stringValue(frame.getKernel());
        }

        KrnAttribute va = cn.getAttribute("значение");
        if (valuePath != null && valuePath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(valuePath, frame.getKernel());
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
            parentPath = pv.stringValue(frame.getKernel());
        }

        va = cn.getAttribute("родитель");
        if (parentPath != null && parentPath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(parentPath, frame.getKernel());
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
            childrenPath = pv.stringValue(frame.getKernel());
        }

        va = cn.getAttribute("дети");
        if (childrenPath != null && childrenPath.length() > 0) {
            PathElement2[] pes = Utils.parsePath2(childrenPath, frame.getKernel());
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
            cash.addCashChangeListener(parentAttrs[parentAttrs.length - 1].attr.id, this, frame);

        if (valueAttrs.length > 0)
            cash.addCashChangeListener(valueAttrs[valueAttrs.length - 1].attr.id, this, frame);

        if (childrenAttrs.length > 0)
                     cash.addCashChangeListener(childrenAttrs[childrenAttrs.length - 1].attr.id, this, frame);

        cash.addCashChangeListener(cn.getId(), this, frame);

        // TODO Временно. Поменять свойство фильтр на содержимое для дерева
        if (!(tree instanceof OrWebTreeTable.WebTreeTableCellRenderer)) {
            pv = tree.getPropertyValue(proot.getChild("ref").getChild("defaultFilter"));
            if (!pv.isNull()) {
                defaultFilterId = pv.filterValue().getObjId();
            }
        }
        pv = tree.getPropertyValue(proot.getChild("pov").getChild("wClickAsOK"));
        wClickAsOK = pv.booleanValue();
        /*if (titlePath2_ != null && titlePath2_.length() > 0) {
            KrnAttribute[] attrs = Utils.getAttributesForPath(titlePath2_);
            titleAttr2_ = (attrs.length > 0) ? attrs[0] : null;
        } */

        /*if (isMultiSelect) {
            if (!isReadOnly)
                nodeOperations_.addSeparator();
            nodeSelectChildren_ = new JMenuItem("Выделить подчиненные");
            nodeSelectChildren_.addActionListener(l);
            nodeOperations_.add(nodeSelectChildren_);
        }*/
        //tree.setXml(null);
        cash.addCashListener(new TreeCashListener(), frame);
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
        log = getLog();
        krn_ = frame.getKernel();
        if (frame instanceof WebFrame) {
            tree = new OrWebTree(frame);
        }
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
      
        if (rootRef != null)
			cn = krn_.getClassNode(rootRef.getType().id);
		else if (rootPath != null) {
			cn = krn_.getClassNodeByName(rootPath);
		} else if (rootCalcRef != null) {
            OrRef.Item item = rootCalcRef.getItem();
            KrnObject obj = (KrnObject) ((item != null) ? item.getCurrent() : null);
            if (obj != null && obj.classId > 0 )
            	cn = krn_.getClassNode(obj.classId);
		}

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
            titleAttrs = Utils.getAttributesForPath(titlePath, frame.getKernel());
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
                    if (rootRef != null && e.getRef() != null && (e.getReason() & OrRefEvent.ITERATING) == 0 &&
                        e.getRef().toString() != null &&
                        rootRef.toString().equals(e.getRef().toString()))
                        populateRoot();
                }
            }
        } catch (KrnException ex) {
            log = getLog();
            log.error(ex, ex);
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
        if (root_ == null && rootCalcRef != null) {
        	if (!selfChange) {
	        	try {
	        		selfChange = true;
		        	rootCalcRef.refresh(null);
		        	populateRoot();
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	} finally {
	        		selfChange = false;
	        	}
        	}
        } else {
            if (!rootUpdated
                    && rootRef != null && rootRef.getAttribute() == null) {
                try {
                    rootRef.evaluate(null);
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return root_;
    }

    public Node getRoot(boolean rootUpdated) {
        if (root_ == null && rootCalcRef != null) {
            rootCalcRef.refresh(null);
        } else {
            if (!rootUpdated
                    && rootRef != null && rootRef.getAttribute() == null) {
                try {
                    rootRef.evaluate(null);
                } catch (KrnException ex) {
                    ex.printStackTrace();
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
            
            if (tree instanceof OrWebTreeTable.WebTreeTableCellRenderer) {
            	
            	TreeTableAdapter tta = ((OrWebTreeTable.WebTreeTableCellRenderer)tree).getTableAdapter();
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
                                log.warn("===================== WARNING!!!=================");
                                log.warn("parent in cikl " + objId);
                                log.warn("===================== WARNING!!!=================");
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
                        JsonArray arr = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.add("index", node.getObject().id);
                        arr.add(obj);
                        if (tree instanceof OrWebTree)
                        	((OrWebTree)tree).sendChangeProperty("reloadNode", arr);
                    }
                }
                if (tree.getTableAdapter() != null) {
                    TreeTableAdapter adapter = tree.getTableAdapter();
                    adapter.getTable().tableStructureChanged();
                    adapter.addLeftToTree();
                }
                path = root_.find(objId, true);
                if (path == null) {
                        path = root_.find(objId, false);
                }
                if (path != null) {
                    tree.getSelectionModel().setSelectionPath(path);
                }
                if (tree.getTableAdapter() != null) {
                    TreeTableAdapter adapter = tree.getTableAdapter();
                    if (path != null) {
                        int row = tree.getRowForPath(path);
                        adapter.getTable().setSelectedRows(new int[] {row}, false);
                        adapter.setSelectedRows(new int[] {row});
                    }
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
                        JsonArray arr = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.add("index", node.getObject().id);
                        arr.add(obj);
                        if (tree instanceof OrWebTree)
                        	((OrWebTree)tree).sendChangeProperty("reloadNode", arr);
                    }
                }
                if (tree.getTableAdapter() != null) {
                    TreeTableAdapter adapter = tree.getTableAdapter();
                    adapter.getTable().tableStructureChanged();
                    adapter.addLeftToTree();
                }
                path = root_.find(objId, true);
                if (path == null) {
                        path = root_.find(objId, false);
                }
                if (path != null) {
                    tree.getSelectionModel().setSelectionPath(path);
                }
                if (tree.getTableAdapter() != null) {
                    TreeTableAdapter adapter = tree.getTableAdapter();
                    if (path != null) {
                        int row = tree.getRowForPath(path);
                        adapter.getTable().setSelectedRows(new int[] {row}, false);
                        adapter.setSelectedRows(new int[] {row});
                    }
                    adapter.countCurrentTableItem();
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
                        if (tree instanceof OrWebTree) {
                            JsonArray arr = new JsonArray();
                            JsonObject obj = new JsonObject();
                            obj.add("index", node.getObject().id);
                            obj.add("title", node.toString());
                            arr.add(obj);
                        	((OrWebTree)tree).sendChangeProperty("setNodeTitle", arr);
                        }
                    }
                }

                if (tree.getTableAdapter() != null) {
                    if (path != null) {
                        int row = tree.getRowForPath(path);
                        tree.getTableAdapter().getTable().tableRowsUpdated(row, row);
                    }
                }
/*
                if (tree instanceof OrTreeTable.TreeTableCellRenderer) {
                    ((OrTreeTable.TreeTableCellRenderer)tree).updateImage();
                }
*/
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
                            if (tree instanceof OrWebTree) {
                                JsonArray arr = new JsonArray();
                                JsonObject obj = new JsonObject();
                                obj.add("index", node.getObject().id);
                                obj.add("title", node.toString());
                                arr.add(obj);
                            	((OrWebTree)tree).sendChangeProperty("setNodeTitle", arr);
                            }
                        }
                        tree.setSelectionPath(path);
                    }

                    if (tree.getTableAdapter() != null) {
                        if (path != null) {
                            int row = tree.getRowForPath(path);
                            tree.getTableAdapter().getTable().tableRowsUpdated(row, row);
                        }
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
                                if (tree instanceof OrWebTree) {
                                    JsonArray arr = new JsonArray();
                                    JsonObject obj = new JsonObject();
                                    obj.add("index", node.getObject().id);
                                    obj.add("title", node.toString());
                                    arr.add(obj);
                                	((OrWebTree)tree).sendChangeProperty("setNodeTitle", arr);
                                }
                            }
                            tree.setSelectionPath(path);
                        }
                        if (tree.getTableAdapter() != null) {
                            if (path != null) {
                                int row = tree.getRowForPath(path);
                                tree.getTableAdapter().getTable().tableRowsUpdated(row, row);
                            }
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
                if (tree.getTableAdapter() != null) {
                    TreeTableAdapter adapter = tree.getTableAdapter();
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
        public Record srec;

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
        //public Node(Record rec, Record vrec, Record trec, Record trec2) {
        public Node(Record rec, Record vrec, Record trec, Record trec2, Record srec) {
        	try{
            this.rec = rec;
            this.vrec = vrec;
            this.trec = trec;
            this.trec2 = trec2;
            this.srec = srec;
            isLoaded = false;
            updateTitle();
		}catch(Exception e){
			e.printStackTrace();
			throw new NullPointerException();  
		}

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
		                srec=getSortRecord(value, value.id);
		            }
		        } else {
		            trec = getTitleRecord(object, object.id);
		            trec2 = getTitleRecord2(object, object.id);
		            srec = getSortRecord(object, object.id);
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
        	try{
        	StringBuilder tb = new StringBuilder();
        	String title1 = (trec != null) ? (String)trec.getValue() : null;
        	String title2 = (trec2 != null) ? (String)trec2.getValue() : null;
        	KrnObject value = (KrnObject) rec.getValue();
        	String titleExpr = getTitleExpr(value);
        	if(titleExpr != null)
        		tb.append(titleExpr);
        	else	
        	if (title1 != null) {
        		tb.append(title1);
        		if (title2 != null)
        			tb.append(':').append(title2);
        	} else if (title2 != null) {
        		tb.append(title2);
        	}
        	this.title = tb.toString();
		}catch(Exception e){
			e.printStackTrace();
			throw new NullPointerException();  
		}

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
    			            srec = getSortRecord(value, value.id);
    		            }
    		        } else {
    		            trec = getTitleRecord(object, object.id);
    		            trec2 = getTitleRecord2(object, object.id);
    		            srec = getSortRecord(object, object.id);
    		        }
    		        // Обновляем заголовок
    		        updateTitle();
                    //reload();
                    
                    removeAllChildren();
                    if (getParent() != null) {
                        m.removeNodeFromParent(this);
                    }
                    m.insertNodeInto(this, (MutableTreeNode) m.getRoot(), 0);
                    m.nodeStructureChanged(this);
                    tree.expandPath(new TreePath(m.getRoot()));
                    isLoaded = false;

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

                KrnObject object = (KrnObject) rec.getValue();
                loadChildren(object.id);

            }
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
        	
        	//
        	Map<Long, AttrRecord> sorts = null;
        	
        	if (sortAttrs != null) {
//
//        		
        		sorts = new HashMap<Long, AttrRecord>();
	        	chIds2 = new ArrayList<Long>(chIds.size());
	        	for (Long chId : chIds) chIds2.add(chId);
	        	
	        	idToId2 = new HashMap<Long, Long>();
	        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));
        	
	            for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < sortAttrs.length - 1; i++) {
	            	ids = Funcs.makeLongArray(chIds2);
	            	chIds2 = new ArrayList<Long>();
	                recs = cash.getRecords(ids, sortAttrs[i], 0, null);
		            if (!recs.isEmpty()) {
	    				SortedSet<AttrRecord> sortRecs = new TreeSet<AttrRecord>();
			            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) sortRecs.add((AttrRecord)it.next());
	
			            for (long id : idToId2.keySet()) {
	    					long newId = idToId2.get(id);
	
			            	AttrRecord r = get(newId, sortAttrs[i].id, 0, 0, sortRecs);
			            	
			            	if (r != null) {
				            	KrnObject obj = (KrnObject)r.getValue();
				            	idToId2.put(id, obj.id);
				            	chIds2.add(obj.id);
			            	}
	    				}
		            }
	            }
	        	ids = Funcs.makeLongArray(chIds2);
	            attr = sortAttrs[sortAttrs.length - 1];
	            recs = cash.getRecords(ids, attr, 0, null);
                //1. достаю все записи в некоторых(или во всех) этот атрибут может быть не проставлен
                for (Record rec : recs) {
                	sorts.put(rec.getObjId(), (AttrRecord)rec);
                }
	            if (recs.size()<firstIds.size()) {
	            	Collections.sort(firstIds, new NodeComparator<Long>(null,titles, titles2));
	                //2.пробегаюсь по всем идентификаторам и те которых нет создаю (т.е. проставляю непроставленный атрибут)
                	long sort_index_=1;//Значение индекса начинается с 1 (пожелание заказчика)
	                for(Long id:firstIds){
	                	AttrRecord rec=sorts.get(id);
	                	if(rec==null){
	                		Record child=values.get(id);
	                		rec=(AttrRecord)cash.insertObjectAttribute((KrnObject)child.getValue(), attr, child.getIndex(), 0, sort_index_, this);
		                	sorts.put(id, rec);
	                	}else{
	                		rec.setValue(sort_index_);
		                	sorts.put(id, (AttrRecord)rec);
	                	}
	                	sort_index_++;
	                }
	                // в этом случае сортировки нет
	            }else{
	            	Collections.sort(firstIds, new NodeComparator<Long>(sorts,titles, titles2));
	            }
        	}else if (sorted) 
        		Collections.sort(firstIds, new NodeComparator<Long>(sorts,titles, titles2));

        	for (long id : firstIds) {
        		try{
	            	Node child = new Node(childs.get(id), values.get(id), titles.get(id), titles2.get(id),sorts!=null?sorts.get(id):null);
	            	m.insertNodeInto(child, this, getChildCount());
        		}catch(Exception e){
        			e.printStackTrace();
        			throw new NullPointerException();  
        		}
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
        	
        	Map<Long, AttrRecord> sorts = null;
        	if (sortAttrs != null) {
        		sorts = new HashMap<Long, AttrRecord>();
	        	chIds2 = new ArrayList<Long>(chIds.size());
	        	for (Long chId : chIds) chIds2.add(chId);
	        	
	        	idToId2 = new HashMap<Long, Long>();
	        	for (Long id : idToId.keySet()) idToId2.put(id, idToId.get(id));
        	
	            for (int i = 0; chIds2 != null && chIds2.size() > 0 && i < sortAttrs.length - 1; i++) {
	            	ids = Funcs.makeLongArray(chIds2);
	            	chIds2 = new ArrayList<Long>();
	                recs = cash.getRecords(ids, sortAttrs[i], 0, null);
		            if (!recs.isEmpty()) {
	    				SortedSet<AttrRecord> sortRecs = new TreeSet<AttrRecord>();
			            for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) sortRecs.add((AttrRecord)it.next());
	
			            for (long id : idToId2.keySet()) {
	    					long newId = idToId2.get(id);
	
			            	AttrRecord r = get(newId, sortAttrs[i].id, 0, 0, sortRecs);
			            	
			            	if (r != null) {
				            	KrnObject obj = (KrnObject)r.getValue();
				            	idToId2.put(id, obj.id);
				            	chIds2.add(obj.id);
			            	}
	    				}
		            }
	            }
	        	ids = Funcs.makeLongArray(chIds2);
	            attr = sortAttrs[sortAttrs.length - 1];
	            recs = cash.getRecords(ids, attr, 0, null);
                //1. достаю все записи в некоторых(или во всех) этот атрибут может быть не проставлен
                for (Record rec : recs) {
                	sorts.put(rec.getObjId(), (AttrRecord)rec);
                }
	            if (recs.size()<firstIds.size()) {
	            	Collections.sort(firstIds, new NodeComparator<Long>(null,titles, titles2));
	                //2.пробегаюсь по всем идентификаторам и те которых нет создаю (т.е. проставляю непроставленный атрибут)
                	long sort_index_=1;//Значение индекса начинается с 1 (пожелание заказчика)
	                for(Long id:firstIds){
	                	AttrRecord rec=sorts.get(id);
	                	if(rec==null){
	                		Record child=values.get(id);
	                		rec=(AttrRecord)cash.insertObjectAttribute((KrnObject)child.getValue(), attr, child.getIndex(), 0, sort_index_, this);
		                	sorts.put(id, rec);
	                	}else{
	                		rec.setValue(sort_index_);
		                	sorts.put(id, (AttrRecord)rec);
	                	}
	                	sort_index_++;
	                }
	                // в этом случае сортировки нет
	            }else{
	            	Collections.sort(firstIds, new NodeComparator<Long>(sorts,titles, titles2));
	            }
        	}else if (sorted) 
        		Collections.sort(firstIds, new NodeComparator<Long>(sorts,titles, titles2));
        	
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
	            	Node child = new Node(childs.get(id), values.get(id), titles.get(id), titles2.get(id),sorts!=null?sorts.get(id):null);
	                m.insertNodeInto(child, this, indexes.get(id));
            	}
            }

            for (int i = getChildCount() - 1; i >= 0; i--) {
                boolean childExists = false;
                Node child = (Node) getChildAt(i);
                if (child.getObject() != null) {
                	long chId = child.getObject().id;
                	if (filteredIds == null || filteredIds.contains(chId) || chId < 0) {
	                    for (Record r : chObjRecs) {
	                        long id = ((KrnObject)r.getValue()).id;
	                        if (id == child.getObject().id) {
	                            childExists = true;
	                            break;
	                        }
	                    }
                	}
                    if (!childExists)
                        m.removeNodeFromParent(child);
                }
            }
        }

        public void changeSortItems(Map<Long,Long> values) throws KrnException {
        	long[] ids=Funcs.makeLongArray(values.keySet());
            KrnAttribute attr = sortAttrs[sortAttrs.length - 1];
            Cache cache=getCash();
            SortedSet<Record> recs = cache.getRecords(ids, attr, 0, null);
            for (Record rec : recs) {
            	cache.changeObjectAttribute(rec, values.get(rec.getObjId()), this);
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
		            srec = getSortRecord(value, object.id);
                }
            } else {
                trec = getTitleRecord(object, object.id);
                trec2 = getTitleRecord2(object, object.id);
	            srec = getSortRecord(object, object.id);
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
		            srec = getSortRecord(value, object.id);
                }
            } else {
                trec = getTitleRecord(object, object.id);
                trec2 = getTitleRecord2(object, object.id);
	            srec = getSortRecord(object, object.id);
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
            long lid = titleAttr.isMultilingual ? langId : 0;
            if (lid == 0 && titleAttr.isMultilingual) {
            	KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[titleAttrs.length - 1], lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            if (rec != null && rec.getValue() == null) {
                StringValue[] vs = krn_.getStringValues(valueIds, titleAttrs[titleAttrs.length - 1], lid, false,
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
            long lid = langId;
            if (lid == 0) {
            	KrnObject lang = frame.getInterfaceLang();
                lid = (lang != null) ? lang.id : langId;
            }
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

        private Record getSortRecord(KrnObject value, long id)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            if (sortAttrs == null)
                return null;
            for (int i = 0; i < sortAttrs.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, sortAttrs[i], 0, null);
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
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, sortAttrs[sortAttrs.length - 1], 0,
                    null);
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

        public JsonArray findtitleByObj(KrnObject obj, String txt) {
//        	long start = System.currentTimeMillis();
        	JsonArray result = new JsonArray();
        	findtitleByObj(obj, "", txt, result);
        	return result;
        }
        
        public void findtitleByObj(KrnObject obj, String parIds, String txt, JsonArray result) {
        	try {
				String title = krn_.getStrings(obj, "наименование", ifcLangId, 0)[0];
				if(title != null && title.toUpperCase(Constants.OK).contains(txt.toUpperCase(Constants.OK))) {
					JsonObject res = new JsonObject();
					res.add("parentNodes", parIds.length() > 0 ? (parIds.substring(0, parIds.length() - 1)): "");
	            	res.add("node", obj.id);
	            	result.add(res);
				}
				KrnObject[] children = krn_.getObjects(obj, "дети", 0);
				if(children != null && children.length > 0) {
					for(KrnObject child: children) {						
						findtitleByObj(child, parIds+ obj.id + ",", txt, result);
					}
				}
			} catch (KrnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
        			String attrName = titleAttrs[0].name;
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
        	return result;
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
		          Record frec = (valueAttrs.length > 0) ? vrec : rec;
		          if (frec != null) {
		              KrnObject value = (KrnObject) frec.getValue();
		              long id = ((KrnObject) rec.getValue()).id;
		              
		              String title = null;
		              for (int i = 0; i < titleAttrs.length; i++) {
		                  long[] valueIds = {value.id};
		                  if (i < titleAttrs.length - 1) {
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
		                  } else {
			                  SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
			                  if (recs.size() > 0) {
			                	  title = (String) recs.last().getValue();
			                  }
		                  }
		              }
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
				    List<TreePath> res = child.findTitle(text, -1, loadedOnly);
				    if (res != null)
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

    public boolean equals(Object obj) {
            if (obj instanceof Node) {
                Node n = (Node)obj;
                if (rec != null) {
                    if (n.rec == null) return false;
                    return rec.equals(n.rec);
                } else {
                    if (n.rec != null) return false;
                    return index == n.index;
                }
            }
            return false;
        }
    }

    public Cache getCash() {
        if (cash == null) {
            //final InterfaceManager mgr =
            //        InterfaceManagerFactory.instance().getManager();
            //cash = (mgr != null) ? mgr.getCash() : null;
            //if (cash != null) cash.addCashListener(new TreeCashListener());
            cash = frame.getCash();
        }
        return cash;
    }

    protected void preloadParent(long[] pids) throws KrnException {
    	if (pids != null && pids.length > 0)
    		getCash().getRecords(pids, parentAttrs[parentAttrs.length - 1].attr, 0, null);
    }

    private class TreeCashListener implements DataCashListener {
        public void cashCleared() {
        	Node root = root_;
        	if (root != null) {
	            JsonArray arr = new JsonArray();
	            JsonObject obj = new JsonObject();
	            obj.add("index", root.getObject().id);
	            arr.add(obj);
                if (tree instanceof OrWebTree)
                	((OrWebTree)tree).sendChangeProperty("reloadNode", arr);
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

    public void moveUp(Node node) throws KrnException{
    	if(sortPath==null) return;
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
    	        	values.put(((KrnObject)nodep.rec.getValue()).id,(long)sort_index_node+1);
    	        	values.put(((KrnObject)node.rec.getValue()).id,(long)sort_index_node);
    	        	parent.changeSortItems(values);
				} finally {
                    selfChange = false;
                }
            }
        }
    }

    public void moveDown(Node node) throws KrnException {
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
    	        	values.put(((KrnObject)noden.rec.getValue()).id,(long)sort_index_node+1);
    	        	values.put(((KrnObject)node.rec.getValue()).id,(long)sort_index_node+2);
    	        	parent.changeSortItems(values);
				} finally {
                    selfChange = false;
                }
            }
        }
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

    public KrnObject createNewNode(String title) throws KrnException{
        if (title != null) {
            Record vrec = null;
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

    public String getDeleteNodeString() {
        return this.res.getString("deleteNodeTitle");
    }
    
    public String getTitleExpr(KrnObject value) throws KrnException {
    	String res = null;
    	if (titleExprFX != null) {
    		ClientOrLang orlang = new ClientOrLang(frame);
    		Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("OBJ", value);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(titleExprFX, vc, this, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(tree, e.getMessage(), "Титулы(Формула)");
                log.error("Ошибка при выполнении формулы Данные.Титулы(Формула) компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
            Object ret = vc.get("RETURN");
            res = (ret instanceof KrnObject || ret instanceof String) ? (String)ret : null;
    	}
		return res;
    	
    }

    public void deleteNode(ASTStart beforDelFX, ASTStart afterDelFX, List<Object> selVect,
                           Map<Object, TreeAdapter.Node> sitems, TreeTableAdapter a) throws KrnException {
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        //Node[] nodes = getSelectedNodes();
        //формула до удаления
        if (beforDelFX != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", selVect);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforDelFX, vc, a, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(a.getTable(), e.getMessage(), this.res.getString("beforeDeleteAction"));
            	log.error("Ошибка при выполнении формулы '" + this.res.getString("beforeDeleteAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
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
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterDelFX, vc, a, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(a.getTable(), e.getMessage(), this.res.getString("afterDeleteAction"));
                log.error(e, e);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
        }
    }

    public KrnObject createNode(Record vrec, String title) throws KrnException {
        Node node = getSelectedNode();
        KrnClass t = (node == null) ? cn.getKrnClass() : childrenAttrs[childrenAttrs.length - 1].type;
        KrnObject newNodeObj = (KrnObject) cash.createObject(t.id).getValue();
        long lid = (langId == 0)
                ? frame.getDataLang().id : langId;

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
            DefaultTreeModel m = new DefaultTreeModel(new Node(null, vrec, trec, trec2,null));
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

    public KrnObject getTitleHolderObject2(KrnObject obj) throws KrnException {
        for (int i = 0; i < titleAttrs2.length - 1; i++) {
            long[] valueIds = {obj.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs2[i], 0, null);
            if (recs.size() > 0) {
                Record rec = recs.last();
                obj = (KrnObject) rec.getValue();
            } else {
                return null;
            }
        }
        return obj;
    }

    public Node getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        return (path != null) ? (Node) path.getLastPathComponent() : null;
    }

    public void changeNode(Node node, Pair p) throws Exception {
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

    public Node[] getSelectedNodes() {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null)
            return new Node[0];
        Node[] res = new Node[paths.length];
        for (int i = 0; i < paths.length; ++i)
            res[i] = (Node) paths[i].getLastPathComponent();
        return res;
    }


    public void selectChildren() {
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

    public OrTreeComponent getTree() {
        return tree;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        tree.setEnabled(isEnabled);
    }

    public long getLangId() {
        return 0;
    }

    public long getTitleLangId() {
        return langId;
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
        LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame)frame).getSession().getConfigNumber());
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
        tree.changeTitles(res);
    }

    public long getIfcLangId() {
        return ifcLangId;
    }

    public List<Long> getFilteredIds() {
        return filteredIds;
    }

    public ResourceBundle getResources() {
        return res;
    }

    public KrnAttribute[] getTitleAttrs2() {
        return titleAttrs2;
    }

    public KrnAttribute[] getTitleAttrs() {
        return titleAttrs;
    }

    /**
     * @return the wClickAsOK
     */
    public boolean isWClickAsOK() {
        return wClickAsOK;
    }
    
    private class NodeComparator<Long> implements Comparator<Long> {
    	private Map<Long, AttrRecord> sorts;
    	private Map<Long, AttrRecord> titles;
    	private Map<Long, AttrRecord> titles2;

		public NodeComparator(Map<Long, AttrRecord> sorts,
				Map<Long, AttrRecord> titles,
				Map<Long, AttrRecord> titles2) {
			
			this.sorts = sorts;
			this.titles = titles;
			this.titles2 = titles2;
		}

		@Override
		public int compare(Long o1, Long o2) {
            if(sorts!=null){
            	AttrRecord st1 = sorts.get(o1);
            	AttrRecord st2 = sorts.get(o2);
                if (st1 == null && st2 != null)
                    return 1;
                else if (st1 != null && st2 == null)
                    return -1;
                else if(st1 != null && st2 != null){
					return (long)st1.getValue()>(long)st2.getValue()?1:(long)st1.getValue()<(long)st2.getValue()?-1:0;
                }
            }
            if(titles!=null){
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
            }else
            	return 0;
		}
    }
}
