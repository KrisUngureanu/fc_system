package kz.tamur.web.component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.adapters.TreeControlAdapter2;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 */
public class OrWebTreeControl2 extends OrWebTree2 {
    private static final long serialVersionUID = 1L;

    public static PropertyNode PROPS = new TreePropertyRoot();

    OrWebTreeControl2(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super(xml, mode, frame, isEditor, id);
        setRootVisible(false);
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        
        try {
	        // Создать адаптер
	        setAdapter(getAdapter(isEditor, true));
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

    }

    public TreeAdapter2 getAdapter(boolean isEditor, boolean needAdapter) throws KrnException {
        if (needAdapter && adapter == null) {
            adapter = new TreeControlAdapter2(frame, this, isEditor);
            adapter.setTree(this);
        }
        return adapter;
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        TreeNode root = (TreeNode) getModel().getRoot();
        if (root != null && root.getChildCount() > 0) {
            TreePath path = getSelectionPath();
            Node sn = (path != null) ? (Node) path.getLastPathComponent() : null;
            Node n = (Node) root.getChildAt(0);
            try {
	            n.reload();
	            if (sn != null) {
	                path = n.find(sn.getObject(), false);
	                setSelectionPath(path);
	            }
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
    }

    public void selectNode(long id) {
        try {
            if (adapter.getModel().getRoot() != null) {
                TreePath path = ((Node) adapter.getModel().getRoot()).find(id, true);
                if (path == null)
                    path = ((Node) adapter.getModel().getRoot()).find(id, false);
                if (path != null) {
                    setSelectionPath(path);
                    Node node = (Node) path.getLastPathComponent();
                    adapter.setSelectedNodes(new Node[] { node });
                } else {
                    adapter.setSelectedNodes(new Node[0]);
                }
            }
        } catch (Exception e) {
        	log.error(e, e);
        }
    }

    public void selectNodes(String ids) {
        try {
        	String[] idsStr = ids.split(",");
            if (adapter.getModel().getRoot() != null) {
            	List<TreePath> paths = new ArrayList<TreePath>();
            	List<Node> nodes = new ArrayList<Node>();
            	for (String idStr : idsStr) {
            		if (idStr.length() > 0) {
	            		long id = Long.parseLong(idStr);
	            		TreePath path = ((Node) adapter.getModel().getRoot()).find(id, true);
	                    if (path == null)
	                        path = ((Node) adapter.getModel().getRoot()).find(id, false);
	                    if (path != null) {
	                        paths.add(path);
	                        nodes.add((Node)path.getLastPathComponent());
	                    }
            		}
            	}
            	setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
            	adapter.setSelectedNodes(nodes.toArray(new Node[nodes.size()]));
            }
            adapter.treeSelectionChanged();
        } catch (Exception e) {
        	log.error(e, e);
        }
    }
    
    public String findTitle(String title) {
    	JsonObject res = new JsonObject();
    	JsonArray titleList = findTitles(title);
    	if(titleList != null && !titleList.isEmpty()) {
    		res.add("foundNodes", titleList);
    	}
    	return res.toString();
    }
    
    public JsonArray findTitles(String title) {
    	JsonArray result = new JsonArray();
    	try {
    		Node root = (Node) adapter.getModel().getRoot();
    		if (root != null && root.getChildCount() > 0) {    			
    			KrnObject obj = root.getObject();
    			return root.findTitleByObj(obj, title);
    		}            
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return result;
    }

    public String getData(String nid) {
        JsonArray arr = new JsonArray();
        Node mroot = (Node) adapter.getModel().getRoot();
        if (mroot != null && mroot.getChildCount() > 0) {
            long parentId = 0;
            Node node = mroot;
            if (nid != null && nid.length() > 0) {
                TreePath path = getPathByNodeId(nid);
                if (path != null) {
                    node = (Node) path.getLastPathComponent(); 
                    adapter.nodeExpanded(node);
                    expandPath(path);
                    parentId = ((Node) node).getObject().id;
                    for (int i = 0; i < node.getChildCount() && (parentId != 0 || i == 0); i++) {
                        getData((Node) node.getChildAt(i), parentId, arr);
                    }
                }
            } else {
                getData(node, parentId, arr);
            }
        }
        return arr.toString();
    }

    public void getData(Node node, long parentId, JsonArray arr) {
        JsonObject row = new JsonObject();
        TreePath path = new TreePath(node.getPath());
        boolean expanded = isExpanded(path);
        boolean checked = false;
        if (getSelectionPaths() != null) {
            for (TreePath p : getSelectionPaths()) {
                if (p.equals(path)) {
                    checked = true;
                    break;
                }
            }
        }

        if (node.getObject() != null) {
            long id = node.getObject().id;
            int r = getRowForPath(path);
            String val = node.toString(r);

            row.add("id", id);
            row.add("text", val);
            row.add("state", node.getChildCount() == 0 || expanded ? "open" : "closed");
            row.add("parent", parentId);
            row.add("checked", checked);
            if (isFolderAsLeaf) {
                row.add("iconCls", "tree-file");
            }

            if (expanded) {
                JsonArray arr2 = new JsonArray();
                for (int i = 0; i < node.getChildCount(); i++) {
                    getData((Node) node.getChildAt(i), node.getObject().id, arr2);
                }
                row.add("children", arr2);
            }
        }

        arr.add(row);
    }

    public TreePath getPathByNodeId(String nid) {
        Node root = (Node)adapter.getModel().getRoot();
        TreePath path = null;
        long objId = Long.parseLong(nid);
        try {
            path = root.find(objId, true);
            if (path == null) {
                path = root.find(objId, false);
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
        return path;
    }
}
