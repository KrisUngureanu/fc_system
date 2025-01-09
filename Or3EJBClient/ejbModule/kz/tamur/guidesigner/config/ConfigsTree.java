package kz.tamur.guidesigner.config;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import kz.tamur.comps.Constants;
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
public class ConfigsTree extends DesignerTree implements PropertyChangeListener {

    private String searchString = "";

    public ConfigsTree(final ConfigNode root) {
        super(root);
        this.root = root;
        model = new ConfigTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightSysColor());
    }

    public void setSelectedNode(ConfigNode selectedNode) {
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
        java.util.List<ConfigNode> list = new ArrayList<ConfigNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ConfigNode node = (ConfigNode) path.getLastPathComponent();
            if (node.isLeaf()) {
                list.add(node);
            }
        }
        ConfigNode[] res = new ConfigNode[list.size()];
        list.toArray(res);
        return res;
    }

    public DesignerTreeNode[] getOnlySelectedNodes() {
        java.util.List<ConfigNode> list = new ArrayList<ConfigNode>();
        TreePath[] paths = getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            ConfigNode node = (ConfigNode) path.getLastPathComponent();
            list.add(node);
        }
        ConfigNode[] res = new ConfigNode[list.size()];
        list.toArray(res);
        return res;
    }

    protected void pasteElement() {

    }

    public void deleteNode(ConfigNode node) {
        try {
            model.deleteNode(node, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public class ConfigTreeModel extends DefaultTreeModel implements
            DesignerTreeModel {

        private ConfigNode rootNode;

        public ConfigTreeModel(TreeNode root) {
            super(root);
            rootNode = (ConfigNode) root;
        }

        @Override
		public AbstractDesignerTreeNode createFolderNode(String title)
				throws KrnException {
			return null;
		}

		public AbstractDesignerTreeNode createChildNode(final String name) throws KrnException {
			return createChildNode(name, name);
    	}
    
		public AbstractDesignerTreeNode createChildNode(final String name, final String dsName) throws KrnException {
			return createChildNode(name, name, name);
    	}

		public AbstractDesignerTreeNode createChildNode(final String name, final String dsName, final String schemeName)
                throws KrnException {
            TreeNode fnode = finder.findFirst(root, new FindPattern() {
				@Override
				public boolean isMatches(Object obj) {
			        if (obj instanceof ConfigNode) {
			        	ConfigNode node = (ConfigNode)obj;
			            if (node != null && node.getName() != null) {
			                String s1 = node.getName().toLowerCase(Constants.OK);
			                return s1.equals(name);
			            }
			        }
			        return false;
				}
			});
            if (fnode != null) {
                MessagesFactory.showMessageDialog(
                        (Frame) getTopLevelAncestor(),
                        MessagesFactory.ERROR_MESSAGE, "Конфигурация \"" + dsName
                                + "\" уже существует в системе!");
                return null;
            }

            fnode = finder.findFirst(root, new FindPattern() {
				@Override
				public boolean isMatches(Object obj) {
			        if (obj instanceof ConfigNode) {
			        	ConfigNode node = (ConfigNode)obj;
			            if (node != null && node.getDsName() != null) {
			                String s1 = node.getDsName().toLowerCase(Constants.OK);
			                return s1.equals(name);
			            }
			        }
			        return false;
				}
			});
            if (fnode != null) {
                MessagesFactory.showMessageDialog(
                        (Frame) getTopLevelAncestor(),
                        MessagesFactory.ERROR_MESSAGE, "Конфигурация c уникальным именем \"" + dsName
                                + "\" уже существует в системе!");
                return null;
            }

            final Kernel krn = Kernel.instance();

            ConfigNode selNode = (ConfigNode) getSelectedNode();

            int idx = selNode.getChildCount();

            ProjectConfiguration c = new ProjectConfiguration(name, dsName, schemeName);
            ConfigNode node = new ConfigNode(c);
            node.setModified(true); 
            
            insertNodeInto(node, selNode, selNode.getChildCount());

            krn.addConfiguration(c, selNode.getDsName());
            
            return node;
        }

        public void deleteNode(AbstractDesignerTreeNode node, boolean isMove)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            ConfigNode parent = (ConfigNode) node.getParent();
            removeNodeFromParent(node);
            parent.setModified(true);
            setSelectedNode(parent);
            krn.removeConfiguration(((ConfigNode)node).getDsName(), parent.getDsName());
        }

        public void addNode(AbstractDesignerTreeNode node,
                AbstractDesignerTreeNode parent, boolean isMove)
                throws KrnException {
            final Kernel krn = Kernel.instance();
            if (!isMove) {
            	ConfigNode unode = (ConfigNode) node;
                node = new ConfigNode(unode.getConfig());
            }
            krn.moveConfiguration(((ConfigNode)node).getDsName(), ((ConfigNode)parent).getDsName());
            insertNodeInto(node, parent, parent.getChildCount());
        }

        public void rename(ConfigNode node, String title) {
            node.rename(title);
            TreeNode[] tp = getPathToRoot(node);
            fireTreeNodesChanged(this, tp, null, null);
        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }

        public void renameNode() {

        }

    }

    public void addNode(ConfigNode node, ConfigNode parent) {
        try {
            model.addNode(node, parent, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            setOpaque(true);
            ConfigNode node = (ConfigNode) value;
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(node.isModified() ? Color.yellow : Color.white);
            } else {
                if (isOpaque) {
                    setBackground(Utils.getLightSysColor());
                }
                setForeground(node.isModified() ? Color.red : Color.black);
            }
            
            if (node.getDsName().equals(Kernel.instance().getBaseName())) {
            	setFont(Utils.getAppTitleFont());
            } else
            	setFont(Utils.getDefaultFont());
            
            if (!leaf) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon(expanded?"Open":"CloseFolder"));
            } else {
                setIcon(kz.tamur.rt.Utils.getImageIcon("HostConn"));
                //setIcon(kz.tamur.rt.Utils.getImageIcon(selected ? "userNodeSel" : "userNode"));
            }
            if (isOpaque && row == dragRow) {
                setBackground(Utils.getSysColor());
            }
            setText(value.toString());
            setOpaque(selected || isOpaque);
            return this;
        }

    }

    public void propertyChange(PropertyChangeEvent evt) {
    	ConfigNode node = (ConfigNode) evt.getOldValue();
        if (node != null) {
            if ("name".equals(evt.getPropertyName())) {
                TreeNode[] tp = ((ConfigTreeModel) model).getPathToRoot(node);
                ((ConfigTreeModel) model).fireTreeNodesChanged(this, tp, null,
                        null);
            }
            node.setModified(true);
            repaint();
        }
    }
}
