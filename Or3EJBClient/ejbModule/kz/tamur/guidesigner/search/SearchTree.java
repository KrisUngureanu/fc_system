package kz.tamur.guidesigner.search;

import kz.tamur.util.*;
import kz.tamur.guidesigner.*;
import kz.tamur.rt.Utils;

import java.awt.*;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.tree.*;


/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 15:57:38
 * To change this template use File | Settings | File Templates.
 */

public class SearchTree extends DesignerTree {
    private String searchString = "";
    private NodeFinder finder = new NodeFinder();

    public SearchTree(final SearchNode root) {
        super(root);
        this.root = root;
        model = new SearchTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
    }

    public AbstractDesignerTreeNode find(KrnObject obj) {
        TreeNode n = finder.findFirst(root, new KrnObjectPattern(obj));
        return (AbstractDesignerTreeNode)n;
    }

    public void setSelectedNode(SearchNode selectedNode) {
        TreePath tpath = new TreePath(selectedNode.getPath());
        setSelectionPath(tpath);
        scrollPathToVisible(tpath);
    }


    protected void defaultDeleteOperations() {}

    public void find() {
        requestFocusInWindow();
        setSelectionPath(new TreePath(root));
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        DesignerDialog dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Поиск элемента", sip);
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
        ArrayList<SearchNode> list = new ArrayList<SearchNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            SearchNode node = (SearchNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        SearchNode[] res = new SearchNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {}

    public class SearchTreeModel extends DefaultTreeModel implements DesignerTreeModel {

        private SearchNode rootNode;

        public SearchTreeModel(TreeNode root) {
            super(root);
            rootNode = (SearchNode)root;
        }

        public void addNode(AbstractDesignerTreeNode node,
                            AbstractDesignerTreeNode parent, boolean isMove) throws KrnException {
            if (!isMove) {
                node = new SearchNode(node.toString(), ((SearchNode)node).getText(),
                        ((SearchNode)node).getType(),((SearchNode)node).getCount(),
                        ((SearchNode)node).readIcon());
            }
            insertNodeInto(node, parent, parent.getChildCount());
        }
        
        public AbstractDesignerTreeNode createFolderNode(String title) throws KrnException {
			return null;
		}

		public AbstractDesignerTreeNode createChildNode(String title) throws KrnException {
			SearchNode node = new SearchNode(title, title, SearchNode.SEARCH_TYPE_TEXT, 0);
			insertNodeInto(node, rootNode, rootNode.getChildCount());
			return node;
		}

		public AbstractDesignerTreeNode createChildNode(String title, String sIcon, java.util.List<String[]> resultS) throws KrnException {
			SearchNode node = new SearchNode(title, sIcon, resultS);
			int index = 0;
			for (int i = 0; i < rootNode.getChildCount(); i++) {
				SearchNode dNode = (SearchNode) rootNode.getChildAt(i);
				if (title.toLowerCase().equals(dNode.getTitle().toLowerCase())) {
					setSelectedNode(rootNode);
					deleteNode(dNode, true);
					index = i;
				}
			}
			insertNodeInto(node, rootNode, index);
			return node;
		}

		public void deleteNode(AbstractDesignerTreeNode node, boolean isMove) throws KrnException {
			removeNodeFromParent(node); 
		}

		public void renameNode() {}

		protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

    }

    public class CellRenderer extends AbstractDesignerTreeCellRenderer implements TreeCellRenderer	{

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            setOpaque(true);
            SearchNode node = (SearchNode)value;
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(Color.white);
            } else {
                setBackground(Utils.getLightSysColor());
                setForeground(Color.black);
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
              	setIcon(kz.tamur.rt.Utils.getImageIcon(node.readIcon()));
            }
            if (row == dragRow) {
                setBackground(Utils.getSysColor());
            }
			setText(value.toString() + (node.getCount() == 0 ? "" : " - " + node.getCount()));
            setOpaque(selected || isOpaque);
            return this;
        }

    }
}
