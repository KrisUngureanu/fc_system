package kz.tamur.guidesigner.bases;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import kz.tamur.guidesigner.*;
import kz.tamur.rt.Utils;
import kz.tamur.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class BaseTree extends DesignerTree implements PropertyChangeListener  {

    private String searchString = "";

    public BaseTree(final BaseNode root) {
        super(root);
        this.root = root;
        model = new BaseTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    BaseNode node = (BaseNode)getSelectedNode();
                    if (node != null && !node.equals(root)) {
                        try {
                            String mes = "Удаление Структуры баз '" +
                                        node.toString() + "'!\nПродолжить?";
                            int res = MessagesFactory.showMessageDialog(
                                    (Frame)getTopLevelAncestor(),
                                    MessagesFactory.QUESTION_MESSAGE, mes);
                            if (res == ButtonsFactory.BUTTON_YES) {
                                model.deleteNode(node, false);
                            }
                        } catch (KrnException e1) {
                            e1.printStackTrace();
                        }
                    }
/*
                } else if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                    find();
                } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                    TreeNode fnode = finder.findNext();
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode)fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                        }
                    } else {
                                    MessagesFactory.showMessageSearchFinished(getTopLevelAncestor())
                    }
*/
                }
            }
        });
    }

    protected void defaultDeleteOperations() {
    }

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog(
                (JFrame)getTopLevelAncestor(), "Поиск элемента", sip);
        dlg.show();
        if (dlg.isOK()) {
            searchString = sip.getSearchText();
            final AbstractDesignerTreeNode node = getSelectedNode() == null ? root : getSelectedNode();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString));
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
        ArrayList<BaseNode> list = new ArrayList<BaseNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            BaseNode node = (BaseNode)path.getLastPathComponent();
            list.add(node);
        }
        BaseNode[] res = new BaseNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {

    }

    public void deleteNode(BaseNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void treeStartSelection(InterfaceNode node) {
        TreePath path = new TreePath(node);
        setSelectionPath(path);
    }

    public class BaseTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private BaseNode rootNode;

        public BaseTreeModel(TreeNode root) {
            super(root);
            rootNode = (BaseNode)root;
        }

        public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
            final Kernel krn = Kernel.instance();
            final KrnClass cls = krn.getClassByName("Структура баз");
            final KrnObject obj = krn.createObject(cls, 0);
            BaseNode selNode = (BaseNode)getSelectedNode();
            if (selNode == null) {
                selNode = rootNode;
            }
            TreePath parent = new TreePath(selNode);
            setSelectionPath(parent);
            KrnObject baseObj = selNode.getKrnObj();
            krn.setString(obj.id, cls.id, "наименование", 0, 0, title, 0);
            int idx = selNode.getChildCount();
            krn.setObject(baseObj.id, baseObj.classId, "дети",
                    idx, obj.id, 0, false);
            BaseNode node = new BaseNode(obj, title, 0, 0, null, idx, false);
            insertNodeInto(node, selNode, selNode.getChildCount());
            return node;
        }

        public AbstractDesignerTreeNode createFolderNode(String name) throws KrnException {
            return null;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            BaseNode parent = (BaseNode)node.getParent();
            KrnObject parentObj = parent.getKrnObj();
            Collection<Object> values =
        		Collections.singletonList((Object)node.getKrnObj());
            removeNodeFromParent(node);
            krn.deleteValue(parentObj.id, parentObj.classId, "дети", values, 0);
            if (!isMove) {
                krn.deleteObject(node.getKrnObj(), 0);
            }
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
                node = new BaseNode(node.getKrnObj(), node.toString(),
                        ((BaseNode)node).getFlags(), ((BaseNode)node).getLevel(),
                        ((BaseNode)node).getBaseObj(), parent.getChildCount(), 
                        ((BaseNode)node).isPhysical());
            }
            KrnObject parentObj = parent.getKrnObj();
            krn.setObject(parentObj.id, parentObj.classId,
                    "дети", parent.getChildCount(), node.getKrnObj().id, 0, false);
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void renameNode() {

        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                                            int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

        public void rename(BaseNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }
    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            setOpaque(true);
            BaseNode node = (BaseNode)value;
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                if (!node.isModified()) {
                    setForeground(Color.white);
                } else {
                    setForeground(Color.yellow);
                }
            } else {
                setBackground(Utils.getLightSysColor());
                if (!node.isModified()) {
                    setForeground(Color.black);
                } else {
                    setForeground(Color.red);
                }
            }
            Font fnt = Utils.getDefaultFont();
            setFont(fnt);
            setIcon(kz.tamur.rt.Utils.getImageIcon("BaseNode"));
            if (row == dragRow) {
                setBackground(Utils.getSysColor());
            }
            setText(value.toString());
            setOpaque(selected || isOpaque);
            return this;
        }

    }

    public void propertyChange(PropertyChangeEvent evt) {
        BaseNode node = (BaseNode)evt.getOldValue();
        if (node != null) {
            TreeNode[] tp = ((BaseTreeModel)model).getPathToRoot(node);
            if ("name".equals(evt.getPropertyName())) {
                ((BaseTreeModel)model).fireTreeNodesChanged(this, tp, null, null);
            }
            node.setModified(true);
/*
            fireValueChanged(new TreeSelectionEvent(
                    this, new TreePath(tp), false, null, null) );
*/
            repaint();
        }
    }
}
