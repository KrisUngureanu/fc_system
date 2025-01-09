package kz.tamur.web.component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeCtrlAdapter;
import kz.tamur.rt.adapters.TreeAdapter.Node;
import kz.tamur.rt.data.Cache;
import kz.tamur.web.common.webgui.WebTree;

import org.jdom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTreeCtrl extends OrWebTree {

    public static PropertyNode PROPS = new TreePropertyRoot();
    
    private static JsonArray titleList;
    private List<TreePath> foundPath;

    protected Cache cash;
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    public OrRef rootRef;
    private OrGuiContainer guiParent;

    OrWebTreeCtrl(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super(xml, mode, frame, isEditor, id);
        //this.xml = xml;
        this.mode = mode;
        setRootVisible(false);
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        adapter = getAdapter(isEditor, true);
        this.xml = null;
    }

    public TreeAdapter getAdapter(boolean isEditor, boolean needAdapter) throws KrnException {
        if (needAdapter && adapter == null)
            adapter = new TreeCtrlAdapter(frame, this, isEditor);
        return adapter;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    private void updateProperties() {
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public void setLangId(long langId) {
        TreeNode root = (TreeNode)getModel().getRoot();
        if (root.getChildCount() > 0) {
            TreePath path = getSelectionPath();
            TreeAdapter.Node sn = (path != null)
                    ? (TreeAdapter.Node) path.getLastPathComponent() : null;
            TreeAdapter.Node n = (TreeAdapter.Node)root.getChildAt(0);
            n.reset();
            if (sn != null) {
                path = n.find(sn.getObject(), false);
                setSelectionPath(path);
            }
        }
        //Utils.processBorderProperties(this, frame);
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public void selectNode(long id) {
        try {
            if (adapter.getRoot() != null) {
                TreePath path = adapter.getRoot().find(id, true);
                if (path == null)
                    path = adapter.getRoot().find(id, false);
                if (path != null)
                    setSelectionPath(path);
            }
            ((TreeCtrlAdapter)adapter).treeSelectionChanged();
        } catch (Exception e) {
        	log.error(e, e);
        }
    }

    public void addSelectNode(long id) {
        try {
            if (adapter.getRoot() != null) {
                TreePath path = adapter.getRoot().find(id, true);
                if (path == null)
                    path = adapter.getRoot().find(id, false);
                if (path != null)
                    addSelectionPath(path);
            }
            ((TreeCtrlAdapter)adapter).treeSelectionChanged();
        } catch (Exception e) {
        	log.error(e, e);
        }
    }
    
    public void deselectNode(long id) {
        try {
            if (adapter.getRoot() != null) {
                TreePath path = adapter.getRoot().find(id, true);
                if (path == null)
                    path = adapter.getRoot().find(id, false);
                if (path != null)
                    removeSelectionPath(path);
            }
            ((TreeCtrlAdapter)adapter).treeSelectionChanged();
        } catch (Exception e) {
        	log.error(e, e);
        }
    }

    public void selectNodes(String ids) {
        try {
        	String[] idsStr = ids.split(",");
            if (adapter.getRoot() != null) {
            	List<TreePath> paths = new ArrayList<TreePath>();
            	for (String idStr : idsStr) {
            		if (idStr.length() > 0) {
	            		long id = Long.parseLong(idStr);
	            		TreePath path = adapter.getRoot().find(id, true);
	                    if (path == null)
	                    	path = adapter.getRoot().find(id, false);
	                    if (path != null)
	                        paths.add(path);
            		}
            	}
            	setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
            }
            ((TreeCtrlAdapter)adapter).treeSelectionChanged();
        } catch (Exception e) {
        	log.error(e, e);
        }
    }

    public String getData(String nid) {
    	JsonArray arr = new JsonArray();

        MutableTreeNode mroot = (MutableTreeNode) getModel().getRoot();

        if (mroot != null && mroot.getChildCount() > 0) {
        	long parentId = 0;
        	MutableTreeNode node = mroot;
        	if (nid != null && nid.length() > 0) {
        		
    	    	TreePath path = getPathByNodeId(nid);
    	        if (path != null) {
    	            node = (TreeAdapter.Node)path.getLastPathComponent();
    	            expandPath(path);
    	            parentId = ((TreeAdapter.Node)node).getObject().id;
    	        }
        	}
        	for (int i = 0; i < node.getChildCount() && (parentId != 0 || i == 0); i++) {
        		getData((TreeAdapter.Node)node.getChildAt(i), parentId, arr);
        	}
        }
    	
        return arr.toString();
    }

    public void getData(TreeAdapter.Node node, long parentId, JsonArray arr) {
        JsonObject row = new JsonObject();
        
        TreePath path = new TreePath(node.getPath());
        boolean expanded = isExpanded(path);
        boolean checked =  false;
        if (getSelectionPaths() != null) {
	        for (TreePath p : getSelectionPaths()) {
	        	if (path.equals(p)) {
	        		checked = true;
	        		break;
	        	}
	        }
        }
        
        if (node.getObject() != null) {
            long id = node.getObject().id;
            String val = node.toString();
            
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
            		getData((TreeAdapter.Node)node.getChildAt(i), node.getObject().id, arr2);
            	}
            	row.add("children", arr2);
            }
        }

        arr.add(row);
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
    		Node root =  adapter.getRoot();
    		if (root != null && root.getChildCount() > 0) {    			
    			KrnObject obj = root.getObject();
    			return root.findTitleByObj(obj, title);
    		}            
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return result;
    }

    public TreePath getPathByNodeId(String nid) {
        WebTree tree = (WebTree) adapter.getTree();
        MutableTreeNode mroot = (MutableTreeNode) tree.getModel().getRoot();
        
        TreeAdapter.Node root = (TreeAdapter.Node) mroot.getChildAt(0);
        
        TreePath path = null;
        long objId = Long.parseLong(nid);
        path = root.find(objId, true);
        if (path == null)
            path = root.find(objId, false);
        return path;
    }
}
