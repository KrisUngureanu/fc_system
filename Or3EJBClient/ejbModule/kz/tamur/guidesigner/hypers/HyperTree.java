package kz.tamur.guidesigner.hypers;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.*;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.dnd.DragSourceEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class HyperTree extends DesignerTree implements PropertyChangeListener {

    private String searchString = "";
    private int hiperComboIndex=0;
    private int conditionIndex=0;
    private boolean isParamChecked = false;
    //private NodeFinder finder = new NodeFinder();
    private boolean isVisIcon=true;
    private long langId;
    private TreeSet langs=new TreeSet();
    private HashMap hipers =new HashMap();
    private boolean canEdit = false;
    private boolean canDelete = false;
    private boolean canCreate = false;
    private List<KrnObject> systemObjList = new ArrayList();
    private FindPattern pattern;    
    

    public HyperTree(final HyperNode root, boolean isVisIcon) {
        super(root, Kernel.instance().getUser().isDeveloper());
        _init(root, isVisIcon);
    }

    public HyperTree(final HyperNode root, boolean isVisIcon, boolean enableDragAndDrop) {
        super(root, enableDragAndDrop);
        _init(root, isVisIcon);
    }

    private void _init(final HyperNode root, boolean isVisIcon) {
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.MENU_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.MENU_DELETE_RIGHT);
        canCreate = user.hasRight(Or3RightsNode.MENU_CREATE_RIGHT);

        this.root = root;
        this.isVisIcon = isVisIcon;
        model = new HyperTreeModel(root);
        this.langId=Utils.getInterfaceLangId();
        langs.add(new Long(langId));
        setModel(model);
        setCellRenderer(new CellRenderer());
        if(isVisIcon)
           setBackground(kz.tamur.rt.Utils.getLightSysColor());
        else
           setBackground(kz.tamur.rt.Utils.getLightGraySysColor());
        miRename.setVisible(false);
        getSystemObjList();
    }
    
    private void getSystemObjList() {
    	final Kernel krn = Kernel.instance();
    	KrnObject[] objs = null;
        try {
            KrnClass hiperCls = krn.getClassByName("HiperTree");
            KrnAttribute isSystem = krn.getAttributeByName(hiperCls, "isSystem");
            if (isSystem != null) 
            	objs = krn.getObjectsByAttribute(hiperCls.id, isSystem.id, 0, ComparisonOperations.CO_EQUALS, 1, 0);
            if (objs != null && objs.length > 0) {
            	for (KrnObject obj : objs) {
            		systemObjList.add(obj);
            	}
            }
        } catch (KrnException e){
            e.printStackTrace();
        }
    }

    public AbstractDesignerTreeNode find(KrnObject obj) {
        TreeNode n = finder.findFirst(root, new KrnObjectPattern(obj));
        return (HyperNode)n;
    }

    protected void defaultDeleteOperations() {

    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel(2);
        sip.setSearchText(searchString);
        sip.setHiperIndex(hiperComboIndex);
        sip.setConditionIndex(conditionIndex);
        sip.setParamCheck(isParamChecked);
        DesignerDialog dlg = new DesignerDialog(
                (JFrame)getTopLevelAncestor(), "Поиск элемента", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            hiperComboIndex = sip.getHiperParam();
            conditionIndex = sip.getCondition();
            isParamChecked = sip.paramChecked();
            if(sip.paramChecked() == true){
            	if(sip.getHiperParam() == 0) {
            		pattern = new StringIfcPattern(searchString, sip.getSearchMethod());
            	}
            	else if(sip.getHiperParam() == 1) {            		
            		pattern = new IDIfcPattern(Long.parseLong(searchString));
            	} else if(sip.getHiperParam() == 2){
            		pattern = new UIDIfcPattern(searchString);
            	}          	
            }else{
            	if(sip.getHiperParam() == 0){
            		pattern = new StringPattern(searchString, sip.getSearchMethod());
            	} else if (sip.getHiperParam() == 1){
            		pattern = new IDPattern(Long.parseLong(searchString));
            	} else if (sip.getHiperParam() == 2){
            		pattern = new UIDPattern(searchString);
            	}
            	
            }
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {                	
                    TreeNode fnode = finder.findFirst(node, pattern);
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                    }
                }
            });
            t.start();

        }
    }

    public DesignerTreeNode[] getSelectedNodes() {
        Set<HyperNode> list = new HashSet<HyperNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            HyperNode node = (HyperNode)path.getLastPathComponent();
            list.add(node);
            java.util.List l = kz.tamur.rt.Utils.getPathToRoot(
                    new ArrayList(), node);
            l.remove(l.size() - 1);
            for (int j = 0; j < l.size(); j++) {
                HyperNode o =  (HyperNode)l.get(j);
                if (!list.contains(o)) {
                    list.add(o);
                }
            }
            for (int j = 0; j < node.getChildCount(); j++) {
                HyperNode n = (HyperNode)node.getChildAt(j);
                list.add(n);
            }
        }
        HyperNode[] res = new HyperNode[list.size()];
        list.toArray(res);
        return res;
    }

    public DesignerTreeNode[] getOnlySelectedNodes() {
        Set<HyperNode> list = new HashSet<HyperNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            HyperNode node = (HyperNode)path.getLastPathComponent();
            list.add(node);
        }
        HyperNode[] res = new HyperNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {
        AbstractDesignerTreeNode parent = getSelectedNode();
        if (copyNode != null && !parent.isLeaf()) {
            CreateElementPanel cp =
                    new CreateElementPanel(CreateElementPanel.COPY_TYPE,
                            copyNode.toString());
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Вставка копии элемента гиперменю", cp);
            dlg.pack();
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String hyperName = cp.getElementName();
                if (hyperName == null) {
                    JOptionPane.showMessageDialog(this, "Неверное имя элемента!",
                            "Сообщение", JOptionPane.ERROR_MESSAGE);
                } else {
                    Kernel krn = Kernel.instance();
                    krn.setAutoCommit(false);
                    final long langId = Utils.getInterfaceLangId();
                    try {
                        if (!copyNode.isCutProcess()) {
                            final KrnClass cls = krn.getClassByName("HiperTree");
                            KrnObject hyperObj = krn.createObject(cls, 0);
                            krn.setString(hyperObj.id, hyperObj.classId, "title", 0, langId, hyperName, 0);
                            KrnObject ifcObj = ((HyperNode) copyNode).getIfcObject();
                            if (ifcObj != null) {
                                krn.setObject(hyperObj.id, hyperObj.classId, "hiperObj", 0, ifcObj.id, 0, false);
                            }
                            HyperNode n = new HyperNode(hyperObj, hyperName, hyperName, ifcObj, ((HyperNode) copyNode).getTitleIfc(), parent.getChildCount(), null, null, langId, false, null);
                            model.addNode(n, parent, false);
                        } else {
                            krn.setString(copyNode.getKrnObj().id, copyNode.getKrnObj().classId, "title", 0, langId, hyperName, 0);
                            HyperNode n = new HyperNode(copyNode.getKrnObj(), hyperName, hyperName, ((HyperNode) copyNode).getIfcObject(), ((HyperNode) copyNode).getTitleIfc(),parent.getChildCount(), null, null, langId, false, null);
                            model.deleteNode(copyNode, true);
                            model.addNode(n, parent, false);
                            defaultDeleteOperations();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    copyNode.setCopyProcessStarted(false);
                    copyNode = null;
                    setCursor(Constants.DEFAULT_CURSOR);
                }
            }
        }
    }

    public void deleteNode(HyperNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


    public class HyperTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private HyperNode rootNode;

        public HyperTreeModel(TreeNode root) {
            super(root);
            rootNode = (HyperNode)root;
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("HiperFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            HyperNode selNode = (HyperNode)getSelectedNode();
            if (selNode == null) {
                selNode = rootNode;
            } else if (selNode.isLeaf()) {
                 selNode = (HyperNode)selNode.getParent();
            }
            TreePath parent = new TreePath(selNode);
            setSelectionPath(parent);
            KrnObject hObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(hObj.id, hObj.classId, "hipers",
                    idx, obj.id, 0, false);
            HyperNode node = new HyperNode(obj, title, title, null, null, idx, null, null, langId, false, null);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("HiperTree");
            final KrnObject obj = krn.createObject(cls, 0);
            HyperNode selNode = (HyperNode)getSelectedNode();
            KrnObject hObj = selNode.getKrnObj();
            int idx = selNode.getChildCount();
            krn.setObject(hObj.id, hObj.classId, "hipers",
                    idx, obj.id, 0, false);
            krn.setString(obj.id, cls.id, "title", 0, langId, title, 0);
            HyperNode node = new HyperNode(obj, title, title, null, null, idx, null, null, langId, false, null);
            node.setModified(true);
            insertNodeInto(node, selNode, selNode.getChildCount());
            hipers.put(new Long(node.getKrnObj().id),node);
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
        	boolean reloadOrRightsTree = false;
            final Kernel krn = Kernel.instance();
            HyperNode parent = (HyperNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            if(parentObj.uid.equals("9.30198536")) {
        		reloadOrRightsTree = true;
        	}
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "hipers", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
            }
            Or3RightsNode.addDynamicNodes();
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new HyperNode(node.getKrnObj(), node.toString(), ((HyperNode)node).getTitleKz(),
                        ((HyperNode)node).getIfcObject(), ((HyperNode)node).getTitleIfc(),
                        parent.getChildCount(), null, null, langId, false, null);
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId, "hipers", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void renameNode() {

        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                                            int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

        public void rename(HyperNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }

        public void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
            super.fireTreeStructureChanged(source, path, childIndices, children);
        }
    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            HyperNode node = (HyperNode)value;
            JLabel l = (JLabel)super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
            if (selected) {
                l.setBackground(kz.tamur.rt.Utils.getDarkShadowSysColor());
            } else {
                if(isVisIcon)
                   setBackground(kz.tamur.rt.Utils.getLightSysColor());
                else
                   setBackground(kz.tamur.rt.Utils.getLightGraySysColor());
            }
            if (selected && !node.isAdded()) {
                if (!node.isModified() || !node.isLeaf()) {
                    l.setForeground(Color.white);
                } else {
                    l.setForeground(Color.yellow);
                }
            } else if (!selected && !node.isAdded()) {
                if (!node.isModified() || !node.isLeaf()) {
                    l.setForeground(Color.black);
                } else {
                    l.setForeground(Color.red);
                }
            } else if (selected && node.isAdded()) {
                l.setForeground(new Color(129, 254, 179));
            } else if (!selected && node.isAdded()) {
                l.setForeground(Color.gray);
            }
            if (!leaf) {
                if (expanded) {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    l.setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else if(isVisIcon){
                l.setIcon(kz.tamur.rt.Utils.getImageIcon("editElement"));
            } else {
                l.setIcon(null);
            }
            l.setOpaque(selected || isOpaque);
            return l;
        }
    }

    public void renameProcess(OrGuiComponent c) {
        PropertyNode pnode = c.getProperties().getChild("title");
        PropertyValue pv = c.getPropertyValue(pnode);
        HyperNode hn = (HyperNode)getSelectedNode();
        if (!pv.isNull()) {
            ((HyperTreeModel)model).rename(hn, pv.stringValue());
        }
        hn.setModified(true);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        HyperNode node = (HyperNode)evt.getOldValue();
        if ("title".equals(evt.getPropertyName())) {
            TreeNode[] tp = ((HyperTreeModel)model).getPathToRoot(node);
            ((HyperTreeModel)model).fireTreeNodesChanged(this, tp, null, null);
        } else if ("index".equals(evt.getPropertyName())) {
            HyperNode parent = (HyperNode)node.getParent();
            if (parent != null) {
                TreePath path=this.getSelectionPath();
                parent.resort();
                TreeNode[] paths = parent.getPath();
                ((HyperTreeModel)model).fireTreeStructureChanged(parent, paths, null, null);
                this.setSelectionPath(path);
            }
        }
        node.setModified(true);
        repaint();
    }

    class DragScroller extends Thread {
        public void run() {
            try {
                Container c = getParent();
                if (c instanceof JViewport) {
                    JViewport jv = (JViewport) c;
                    Point p = jv.getViewPosition();
                    double diff = p.y + jv.getHeight() / 2 - lastLocation;
                    int maxY = getHeight() - jv.getHeight();

                    while (isDragStarted) {
                        if (diff > 0) {
                            p.y = p.y - 10;
                            if (p.y < 0) p.y = 0;
                        }
                        if (diff < 0) {
                            p.y = p.y + 10;

                            if (p.y > maxY) p.y = maxY;
                        }
                        jv.setViewPosition(p);
                        HyperTree.this.repaint();
                        sleep(100);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setLang(long lang){
        if(langId!=lang){
            langId=lang;
            TreePath path=this.getSelectionPath();
            getChildren((HyperNode)root);
            for(Iterator it=hipers.values().iterator();it.hasNext();){
                HyperNode node=(HyperNode)it.next();
                node.setLang(lang);
            }
            if(!langs.contains(new Long(langId)))//{
                langs.add(new Long(langId));
                long[]ids=Funcs.makeLongArray(hipers.keySet());
                try{
                    final KrnClass cls = Kernel.instance().getClassByName("HiperTree");
                    StringValue[] strv = Kernel.instance().getStringValues(ids,cls.id,"title",lang,false, 0);
                     for(int i=0;i<strv.length;++i){
                         HyperNode node=(HyperNode)hipers.get(new Long(strv[i].objectId));
                         node.setTitle(strv[i].value,lang);
                         ((DefaultTreeModel)this.getModel()).nodeStructureChanged(node);
                     }
                }catch(KrnException ex){
                    ex.printStackTrace();
                }


//            }else{
//                  for(Iterator it=hipers.values().iterator();it.hasNext();){
//                      HyperNode node=(HyperNode)it.next();
//                      ((DefaultTreeModel)this.getModel()).nodeStructureChanged(node);
//                  }
//            }
            this.setSelectionPath(path);
        }
    }
    private void getChildren(HyperNode node){
        if(!node.isLeaf()){
            List children=node.getChildren();
            if(children==null) return;
            for(Iterator it=children.iterator();it.hasNext();){
                HyperNode child=(HyperNode)it.next();
                hipers.put(new Long(child.getKrnObj().id),child);
                getChildren(child);
            }
        }

    }
    public void dragExit(DragSourceEvent dse) {
        isDragStarted = true;
        DragScroller scroller = new DragScroller();
        scroller.start();
    }

    public HyperNode getRoot() {
        return (HyperNode)root;
    }

     protected void showPopup(MouseEvent e) {
        if (getSelectedNode().isLeaf()) {
            miCreateFolder.setEnabled(false);
            miCreateElement.setEnabled(false);
            miCopy.setEnabled(canCreate);
            miCut.setEnabled(canCreate);
            miPaste.setEnabled(false);
        } else {
            miCreateFolder.setEnabled(canCreate);
            miCreateElement.setEnabled(canCreate);
            miCopy.setEnabled(false);
            miCut.setEnabled(false);
            if (copyNode != null) {
                miPaste.setEnabled(canCreate);
            } else {
                miPaste.setEnabled(false);
            }
        }
        miDelete.setEnabled(!(getSelectedNode() == root) && canDelete && !systemObjList.contains(getSelectedNode().getKrnObj()));
        miRename.setEnabled(canEdit);
        pm.show(e.getComponent(), e.getX(), e.getY());
    }
}
