package kz.tamur.web.common.webgui;

import kz.tamur.comps.OrFrame;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.web.common.IntegerRef;

import javax.swing.tree.*;

import org.jdom.Element;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 15.02.2007
 * Time: 10:33:01
 * To change this template use File | Settings | File Templates.
 */
public class WebTree extends WebComponent {
    private TreeSelectionModel selectionModel;
    private TreeModel treeModel;
    private Hashtable<Long, Boolean> expandedState;
    private boolean rootVisible;
    protected boolean isFolderSelect = false;
    protected boolean isFolderAsLeaf = false;
    protected boolean showSearchLine = true;

    public WebTree(Element xml, int mode, OrFrame frame, String id) {
        this(getDefaultModel(), xml, mode, frame, id);
    }

    private static TreeModel getDefaultModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");
        DefaultMutableTreeNode parent;

        parent = new DefaultMutableTreeNode("colors");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("blue"));
        parent.add(new DefaultMutableTreeNode("violet"));
        parent.add(new DefaultMutableTreeNode("red"));
        parent.add(new DefaultMutableTreeNode("yellow"));

        parent = new DefaultMutableTreeNode("sports");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("basketball"));
        parent.add(new DefaultMutableTreeNode("soccer"));
        parent.add(new DefaultMutableTreeNode("football"));
        parent.add(new DefaultMutableTreeNode("hockey"));

        parent = new DefaultMutableTreeNode("food");
        root.add(parent);
        parent.add(new DefaultMutableTreeNode("hot dogs"));
        parent.add(new DefaultMutableTreeNode("pizza"));
        parent.add(new DefaultMutableTreeNode("ravioli"));
        parent.add(new DefaultMutableTreeNode("bananas"));
        return new DefaultTreeModel(root);
    }

    public WebTree(TreeModel treeModel, Element xml, int mode, OrFrame frame, String id) {
        super(xml, mode, frame, id);
        expandedState = new Hashtable<Long, Boolean>();
        rootVisible = true;
        selectionModel = new DefaultWebTreeSelectionModel();
        setModel(treeModel);
    }

    public void setSelectionPath(TreePath path) {
        getSelectionModel().setSelectionPath(path);
    }

    public void setSelectionPaths(TreePath[] paths) {
        getSelectionModel().setSelectionPaths(paths);
    }

    public TreeModel getModel() {
        return treeModel;
    }

    public void expandPath(TreePath path) {
        // Only expand if not leaf!
        TreeModel model = getModel();

        if (path != null && model != null) {
            if (!model.isLeaf(path.getLastPathComponent()))
                setExpandedState(path, true);
            else
                setExpandedState(path.getParentPath(), true);
        }
    }

    public TreeSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void collapsePath(TreePath path) {
        setExpandedState(path, false);
    }

    public void changeState(TreePath path) {
        setExpandedState(path, !isExpanded(path));
    }

    public boolean isExpanded(TreePath path) {
        if (path != null) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
            long objId = 0;
            if (n instanceof TreeAdapter.Node) {
                TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
                if (node.getObject() == null)
                    return false;
                objId = node.getObject().id;
            } else if (n instanceof TreeAdapter2.Node) {
                TreeAdapter2.Node node = (TreeAdapter2.Node) path.getLastPathComponent();
                if (node.getObject() == null)
                    return false;
                objId = node.getObject().id;
            }
            Boolean b = expandedState.get(objId);
            if (b != null && b.booleanValue())
                return true;
        }
        return false;
    }

    public void clearStates() {
        expandedState.clear();
    }

    private void setExpandedState(TreePath path, boolean b) {
        while (path != null) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
            long objId = 0;
            if (n instanceof TreeAdapter.Node) {
                TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
                objId = node.getObject().id;
            } else if (n instanceof TreeAdapter2.Node) {
                TreeAdapter2.Node node = (TreeAdapter2.Node) path.getLastPathComponent();
                objId = node.getObject().id;
            }
            expandedState.put(objId, b);
            if (b)
                path = path.getParentPath();
            else
                path = null;
        }
    }

    public void setModel(TreeModel m) {
        treeModel = m;
    }

    public TreePath getSelectionPath() {
        return getSelectionModel().getSelectionPath();
    }

    public TreePath[] getSelectionPaths() {
        return getSelectionModel().getSelectionPaths();
    }

    public void addSelectionPath(TreePath path) {
        getSelectionModel().addSelectionPath(path);
    }

    public void removeSelectionPath(TreePath path) {
        getSelectionModel().removeSelectionPath(path);
    }

    class DefaultWebTreeSelectionModel extends DefaultTreeSelectionModel {
        private TreePath[] selectedPaths;

        public TreePath getSelectionPath() {
            return (selectedPaths != null && selectedPaths.length > 0) ? selectedPaths[0] : null;
        }

        public TreePath[] getSelectionPaths() {
            return selectedPaths;
        }

        public void setSelectionPath(TreePath path) {
        	if (path != null)
        		setSelectionPaths(new TreePath[] { path });
        	else
        		setSelectionPaths(null);
        }

        public void setSelectionPaths(TreePath[] paths) {
            selectedPaths = paths;
        }

        public boolean isPathSelected(TreePath path) {
            if (selectedPaths != null && path != null) {
                for (TreePath p : selectedPaths) {
                    if (p != null && path.toString().equals(p.toString()))
                        return true;
                }
            }
            return false;
        }

        public void addSelectionPath(TreePath path) {
            addSelectionPaths(new TreePath[] { path });
        }

        public void addSelectionPaths(TreePath[] paths) {
            if (selectedPaths == null)
            	selectedPaths = new TreePath[0];
            	
            for (TreePath path : paths) {
            	int index = -1;
                for (int i=0; i<selectedPaths.length; i++) {
                	if (selectedPaths[i].equals(path)) {
                		index = i;
                		break;
                	}
                }
                if (index == -1) {
                    TreePath[] ps = new TreePath[selectedPaths.length + 1];
                    int cur = 0;
                    if (selectedPaths != null) {
                        for (int i = 0; i < selectedPaths.length; i++)
                            ps[cur++] = selectedPaths[i];
                    }
                    ps[cur] = path;
                    setSelectionPaths(ps);
                }
            }
        }

        public void removeSelectionPath(TreePath path) {
            if (selectedPaths != null) {
            	int index = -1;
                for (int i=0; i<selectedPaths.length; i++) {
                	if (selectedPaths[i].equals(path)) {
                		index = i;
                		break;
                	}
                }
                
                if (index > -1) {
                    TreePath[] ps = new TreePath[selectedPaths.length - 1];
                    for (int i = 0; i < index; i++)
                        ps[i] = selectedPaths[i];
                    for (int i = index + 1; i < selectedPaths.length; i++)
                        ps[i-1] = selectedPaths[i];
                    setSelectionPaths(ps);
                }
            }
        }
    }

    public boolean isRootVisible() {
        return rootVisible;
    }

    public void setRootVisible(boolean rootVisible) {
        this.rootVisible = rootVisible;
    }

    public int getRowCount() {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        int res = getVisibleRowCount(mroot);
        if (!isRootVisible())
            res--;
        return res;
    }

    public int getVisibleRowCount(DefaultMutableTreeNode n) {
        int res = 0;
        if (n != null) {
            res = 1;
            if (isExpanded(new TreePath(n.getPath()))) {
                for (Enumeration en = n.children(); en.hasMoreElements();) {
                    DefaultMutableTreeNode ch = (DefaultMutableTreeNode) en.nextElement();
                    res += getVisibleRowCount(ch);
                }
            }
        }
        return res;
    }

    public int getRowForPath(TreePath treePath) {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        IntegerRef res = new IntegerRef(0);
        getDistanceBetween(mroot, node, res);
        if (!isRootVisible())
            res.i--;
        return res.i;
    }

    protected boolean getDistanceBetween(DefaultMutableTreeNode n, DefaultMutableTreeNode node, IntegerRef res) {
        if (n.equals(node))
            return true;
        res.i++;
        if (isExpanded(new TreePath(n.getPath()))) {
            for (Enumeration en = n.children(); en.hasMoreElements();) {
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode) en.nextElement();
                boolean b = getDistanceBetween(ch, node, res);
                if (b)
                    return true;
            }
        }
        return false; // To change body of created methods use File | Settings | File Templates.
    }

    public TreePath getPathForRow(int row) {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        if (!isRootVisible())
            row++;

        return getPathForRow(mroot, new IntegerRef(row));
    }

    protected TreePath getPathForRow(DefaultMutableTreeNode n, IntegerRef row) {
        if (n != null) {
            if (row.i == 0)
                return new TreePath(n.getPath());

            if (isExpanded(new TreePath(n.getPath()))) {
                for (Enumeration en = n.children(); en.hasMoreElements();) {
                    DefaultMutableTreeNode ch = (DefaultMutableTreeNode) en.nextElement();
                    row.i--;
                    TreePath path = getPathForRow(ch, row);
                    if (path != null)
                        return path;
                }
            }
        }
        return null;
    }
    
    public JsonObject getJSONNode(TreeAdapter.Node node, int col) {
        return getJSONNode(node, col, null);
    }

    public JsonObject getJSONNode(TreeAdapter.Node node, int col, String idBtn) {
        JsonObject jsonNode = new JsonObject();
        String val = "";
        long id = 0;
        int childCount = node.getChildCount();
        boolean isExpanded = isExpanded(new TreePath(node.getPath()));

        id = node.getObject().id;
        val = node.toString();
        String clazz = getSelectionModel().isPathSelected(new TreePath(node.getPath())) ? "class='Current'" : "";
        JsonObject icon = new JsonObject();
        if (childCount > 0) {
            icon.add("uuid", id);
            icon.add("class", clazz);
            String onClick = "treeExpand(this";
            if (col > -1) {
                onClick += ", " + col;
            }
            onClick += ");";
            icon.add("onClick", onClick);
            JsonObject img = new JsonObject();
            img.add("src", isExpanded ? "images/minus.gif" : "images/plus.gif");
            img.add("uuid", "img" + id);
            icon.add("img", img);
        } else {
            JsonObject img = new JsonObject();
            img.add("src", "images/empty.gif");
            icon.add("img", img);
        }
        jsonNode.add("icon", icon);
        
        
        JsonObject item = new JsonObject();
        item.add("uuid", "s" + id);
        item.add("class", clazz);

        String onClick = "treeSelChanged(this,'" + idBtn + "', " + (isFolderSelect || childCount == 0);
        if (col > -1) {
            onClick += ", " + col;
        }
        onClick += ");";
        item.add("onClick", onClick);

        if (childCount > 0) {
            onClick = "treeExpandID('" + id + "'";
            if (col > -1) {
                onClick += ", " + col;
            }
            onClick += ");";
            item.add("onDblClick", onClick);
        }
        item.add("value", val);
        jsonNode.add("item", item);

        if (childCount > 0) {
            JsonObject children = new JsonObject();
            if (isExpanded) {
                children.add("class", "Shown");
                JsonArray children_ = new JsonArray();
                for (int i = 0; i < childCount; i++) {
                    TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
                    children_.add(getJSONNode(child, col, idBtn));
                }
                children.add("children", children_);

            } else {
                children.add("class", "Hidden");
            }
            children.add("uuid", "ul" + id);
            jsonNode.add("children", children);
        }
        return jsonNode;
    }

	public boolean isFolderAsLeaf() {
		return isFolderAsLeaf;
	}

	public boolean isShowSearchLine() {
		return showSearchLine;
	}

	public boolean isFolderSelect() {
		return isFolderSelect;
	}
}
