package kz.tamur.guidesigner.boxes;

import kz.tamur.util.*;
import kz.tamur.guidesigner.*;
import kz.tamur.rt.Utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.client.Kernel;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 15:57:38
 * To change this template use File | Settings | File Templates.
 */
public class BoxTree extends DesignerTree implements PropertyChangeListener {

    private String searchString = "";
    private NodeFinder finder = new NodeFinder();

    public BoxTree(final BoxNode root) {
        super(root);
        this.root = root;
        model = new kz.tamur.guidesigner.boxes.BoxTree.BoxTreeModel(root);
        setModel(model);
        setCellRenderer(new kz.tamur.guidesigner.boxes.BoxTree.CellRenderer());
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
    }

    public void setSelectedNode(BoxNode selectedNode) {
        TreePath tpath = new TreePath(selectedNode.getPath());
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }

    protected void defaultDeleteOperations() {
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog(
                (Frame)getTopLevelAncestor(), "Поиск элемента", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
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
        ArrayList<BoxNode> list = new ArrayList<BoxNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            BoxNode node = (BoxNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        BoxNode[] res = new BoxNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {

    }

    public void deleteNode(BoxNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


    public class BoxTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private BoxNode rootNode;

        public BoxTreeModel(TreeNode root) {
            super(root);
            rootNode = (BoxNode)root;
        }

        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("BoxFolder");
            final KrnObject obj = krn.createObject(cls, 0);
            BoxNode selNode = (BoxNode)getSelectedNode();
            if (selNode == null) {
                selNode = rootNode;
            } else if (selNode.isLeaf()) {
                 selNode = (BoxNode)selNode.getParent();
            }
            TreePath parent = new TreePath(selNode);
            setSelectionPath(parent);
            KrnObject userObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "name", 0, 0, title, 0);
            KrnObject[] bases = krn.getObjects(obj,"base",0);
            KrnObject base=krn.getUser().getBase();
            if(bases.length>0)
                base=bases[0];
            int idx = selNode.getChildCount();
            krn.setObject(userObj.id, userObj.classId, "children",
                    idx, obj.id, 0, false);
            BoxNode node = new BoxNode(obj, title,base, "","", "","", "","","",new byte[0],"",0, idx,0);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createChildNode(String name) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("BoxExchange");
            final KrnObject obj = krn.createObject(cls, 0);
            BoxNode selNode = (BoxNode)getSelectedNode();
            KrnObject userObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "name", 0, 0, name, 0);
            KrnObject[] bases = krn.getObjects(obj,"base",0);
            KrnObject base=krn.getUser().getBase();
            if(bases.length>0)
                base=bases[0];
            int idx = selNode.getChildCount();
            krn.setObject(userObj.id, userObj.classId, "children",
                    idx, obj.id, 0, false);
            BoxNode node = new BoxNode(obj, name,base, "","","", "","", "","",new byte[0],"",0, idx,0);
            node.setModified(BoxNode.name_);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            BoxNode parent = (BoxNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "children", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
            }
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new BoxNode(node.getKrnObj(), node.toString(), ((BoxNode)node).getBaseStructureObj(),
                        ((BoxNode)node).getUrlIn(),((BoxNode)node).getUrlOut(),
                        ((BoxNode)node).getPathIn(),((BoxNode)node).getPathOut(),
                        ((BoxNode)node).getPathTypeIn(),((BoxNode)node).getPathTypeOut(),((BoxNode)node).getPathInit(),
                        ((BoxNode)node).getConfig(),((BoxNode)node).getCharSet(),((BoxNode)node).getTransportInt(),parent.getChildCount(),((BoxNode)node).getTypeMsg());
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId,
                    "children", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void rename(BoxNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }

        public void renameNode() {

        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                                            int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

    }

    public void addNode(BoxNode node, BoxNode parent) {
        try {
            model.addNode(node, parent, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            setOpaque(true);
            BoxNode node = (BoxNode)value;
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                if (node.isModified()==0) {
                    setForeground(Color.white);
                } else {
                    setForeground(Color.yellow);
                }
            } else {
                setBackground(Utils.getLightSysColor());
                if (node.isModified()==0) {
                    setForeground(Color.black);
                } else {
                    setForeground(Color.red);
                }
            }
            Font fnt = Utils.getDefaultFont();
            setFont(fnt);
            if (!leaf) {
                if (expanded) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                if (!selected) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("BoxNode"));
                } else {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("BoxNodeSel"));
                }
            }
            if (row == dragRow) {
                setBackground(Utils.getSysColor());
            }
            setText(value.toString());
            setOpaque(selected || isOpaque);
            return this;
        }

    }


    public void propertyChange(PropertyChangeEvent evt) {
        BoxNode node = (BoxNode)evt.getOldValue();
        if (node != null) {//&& evt.getOldValue() != null) {
            if ("name".equals(evt.getPropertyName())) {
                TreeNode[] tp = ((BoxTreeModel)model).getPathToRoot(node);
                ((BoxTreeModel)model).fireTreeNodesChanged(this, tp, null, null);
                node.setModified(BoxNode.name_);
            } else if ("base".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.base_);
            } else if ("urlIn".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.urlIn_);
            } else if ("urlOut".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.urlOut_);
            } else if ("xpathIn".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.pathIn_);
            } else if ("xpathOut".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.pathOut_);
            } else if ("xpathTypeIn".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.pathTypeIn_);
            } else if ("xpathTypeOut".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.pathTypeOut_);
            } else if ("xpathInit".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.pathInit_);
            } else if ("charSet".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.charSet_);
            } else if ("config".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.config_);
            } else if ("transport".equals(evt.getPropertyName())) {
                node.setModified(BoxNode.transport_);
            }
/*
            fireValueChanged(new TreeSelectionEvent(
                    this, getSelectionPath(), false, null, null) );
*/
            repaint();
        }
    }

}
