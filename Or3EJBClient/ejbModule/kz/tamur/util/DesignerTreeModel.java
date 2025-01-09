package kz.tamur.util;

import com.cifs.or2.kernel.KrnException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;

/**
 * User: vital
 * Date: 02.02.2005
 * Time: 15:11:35
 */
public interface DesignerTreeModel extends TreeModel {

    public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException;
    public AbstractDesignerTreeNode createChildNode(String title) throws KrnException;
    public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException;
    public void addNode(AbstractDesignerTreeNode node,
                        AbstractDesignerTreeNode parent, boolean isMove) throws KrnException;
    public void renameNode();
    Object getRoot();
    int getChildCount(Object parent);
    boolean isLeaf(Object node);
    void addTreeModelListener(TreeModelListener l);
    void removeTreeModelListener(TreeModelListener l);
    Object getChild(Object parent, int index);
    int getIndexOfChild(Object parent, Object child);
    void valueForPathChanged(TreePath path, Object newValue);
}
