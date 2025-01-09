package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebTree;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.controller.WebController;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;
import org.jdom.Element;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTree extends WebTree implements JSONComponent, OrTreeComponent {

    public static PropertyNode PROPS = new TreePropertyRoot();

    protected Cache cash;
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    private OrGuiContainer guiParent;
    private WebButton treeFieldButton = null;

    protected TreeAdapter adapter;
    private TreeTableAdapter tableAdapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private boolean multiSelection = false;
	private boolean rootChecked = true;

    protected OrWebTree(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        this(xml, mode, frame, isEditor, true, id);
    }

    protected OrWebTree(Element xml, int mode, OrFrame frame, boolean isEditor, boolean needAdapter, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        
        try {
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	
	        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
	        if (!pv.isNull()) {
	            varName = pv.stringValue(frame.getKernel());
	        }
	
	        // description = PropertyHelper.getDescription(this);
	        if (!WebController.NO_COMP_DESCRIPTION) {
	            pv = getPropertyValue(PROPS.getChild("description"));
	            if (!pv.isNull()) {
	                Pair p = pv.resourceStringValue();
	                descriptionUID = (String) p.first;
	                description = frame.getBytes(descriptionUID);
	            }
	        }
	        // setBackground(Utils.getLightSysColor());
	        // rend.setBackground(Utils.getLightSysColor());
            adapter = getAdapter(isEditor, needAdapter);
            pv = getPropertyValue(PROPS.getChild("view").getChild("folderSelect"));
            if (!pv.isNull()) {
                isFolderSelect = pv.booleanValue();
            } else {
                isFolderSelect = ((Boolean) PROPS.getChild("view").getChild("folderSelect").getDefaultValue()).booleanValue();
            }
            pv = getPropertyValue(PROPS.getChild("view").getChild("folderAsLeaf"));
            if (pv.isNull()) {
            	isFolderAsLeaf = ((Boolean) PROPS.getChild("view").getChild("folderAsLeaf").getDefaultValue()).booleanValue();
            } else {
            	isFolderAsLeaf = pv.booleanValue();
            }
            pv = getPropertyValue(PROPS.getChild("view").getChild("showSearchLine"));
            if (pv.isNull()) {
            	showSearchLine = ((Boolean) PROPS.getChild("view").getChild("showSearchLine").getDefaultValue()).booleanValue();
            } else {
            	showSearchLine = pv.booleanValue();
            }
            
            pv = getPropertyValue(PROPS.getChild("pov").getChild("multiselection"));
            if (pv.isNull()) {
            	multiSelection = ((Boolean) PROPS.getChild("pov").getChild("multiselection").getDefaultValue()).booleanValue();
            } else {
            	multiSelection = pv.booleanValue();
            }
            
            pv = getPropertyValue(PROPS.getChild("pov").getChild("rootChecked"));
            if (pv.isNull()) {
            	rootChecked = ((Boolean) PROPS.getChild("pov").getChild("rootChecked").getDefaultValue()).booleanValue();
            } else {
            	rootChecked = pv.booleanValue();
            }
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
    }

    public OrWebTree(OrFrame frame) {
    	super(null, Mode.RUNTIME, frame, null);
    }

    public TreeAdapter getAdapter(boolean isEditor, boolean needAdapter) throws KrnException {
        if (needAdapter && adapter == null)
            adapter = new TreeAdapter(frame, this, isEditor);
        return adapter;
    }

    public TreeTableAdapter getTableAdapter() {
        return tableAdapter;
    }

    public void setTableAdapter(TreeTableAdapter tableAdapter) {
        this.tableAdapter = tableAdapter;
    }

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
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
        if (mode == Mode.RUNTIME) {
            if (!WebController.NO_COMP_DESCRIPTION) {
                if (descriptionUID != null)
                    description = frame.getBytes(descriptionUID);
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = frame.getBytes(descriptionUID);
            }
        }
        if (adapter != null)
            adapter.setLangId(langId);
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public int getTabIndex() {
        return -1;
    }

    public WebButton getTreeFieldButton() {
        return treeFieldButton;
    }

    public void setTreeFieldButton(WebButton treeFieldButton) {
        this.treeFieldButton = treeFieldButton;
    }

    public TreeAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TreeAdapter adapter) {
        this.adapter = adapter;
    }
    public void moveUp(String nid) {
        long objId = Long.parseLong(nid);
        TreePath path = adapter.getRoot().find(objId, true);
        if (path == null)
            path = adapter.getRoot().find(objId, false);
        TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
         try {
			adapter.moveUp(node);
			((DefaultTreeModel)getModel()).nodeChanged(node);
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void moveDown(String nid) {
        long objId = Long.parseLong(nid);
        TreePath path = adapter.getRoot().find(objId, true);
        if (path == null)
            path = adapter.getRoot().find(objId, false);
        TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
         try {
			adapter.moveDown(node);
			((DefaultTreeModel)getModel()).nodeChanged(node);
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public JsonObject expand2(String nid, String wait, int col) {
        return expand2(nid, wait, col, null);
    }

    public JsonObject expand2(String nid, String wait, int col, String id) {
        if (adapter.getRoot() != null) {
            long objId = Long.parseLong(nid);
            TreePath path = adapter.getRoot().find(objId, true);
            if (path == null)
                path = adapter.getRoot().find(objId, false);
            if (path != null) {
                changeState(path);
                if (wait == null) {
                    TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
                    return getXML2(node, col, id);
                }
            }
        }
        return null;
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

    public JsonObject treeTableNodeToHTML(TreeAdapter.Node node) {
        JsonObject nodeJSON = new JsonObject();
        JsonObject img = new JsonObject();
        String val = "";
        long id = 0;
        int childCount = node.getChildCount();
        boolean isExpanded = isExpanded(new TreePath(node.getPath()));
        id = node.getObject().id;
        val = node.toString();
        if (childCount > 0) {
            nodeJSON.add("id", id);
            nodeJSON.add("onClick", "treeTableExpand(this);");
            img.add("id", "img" + id);
            img.add("src", isExpanded ? "images/minus.gif" : "images/plus.gif");
        } else {
            img.add("src", "images/empty.gif");
        }
        nodeJSON.add("img", img);
        nodeJSON.add("value", val);
        return nodeJSON;
    }

    public JsonObject getXML2(TreeAdapter.Node node, int col) {
        return getXML2(node, col, null);
    }

    public JsonObject getXML2(TreeAdapter.Node node, int col, String id) {
        JsonObject obj = new JsonObject();
        obj.add("id", node.getObject().id);
        JsonArray data = new JsonArray();
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeAdapter.Node child = (TreeAdapter.Node) node.getChildAt(i);
            data.add(getJSONNode(child, col, id));
        }
        obj.add("data", data);

        return obj;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        MutableTreeNode mroot = (MutableTreeNode) getModel().getRoot();
        if (mroot != null && mroot.getChildCount() > 0) {
            TreeAdapter.Node root = (TreeAdapter.Node) mroot.getChildAt(0);
            
            TreePath path = getSelectionPath();
            TreeAdapter.Node n = (path != null && path.getLastPathComponent() instanceof TreeAdapter.Node) ? (TreeAdapter.Node) path.getLastPathComponent() : null;
            long id = n != null ? n.getObject().id : root != null ? root.getObject().id : 0;
            if (multiSelection) {
            	setSelectionPath(null);
            	if (rootChecked) {
            		property.add("checkboxTree", 0);
            	} else {
            		property.add("checkboxTree", root.getObject().id);
            	}
            } else {
                property.add("selectNode", id);
            }
        }
        
        if (property.size() > 0) {
            obj.add("pr", property);
        }

        sendChange(obj, isSend);
        return obj;
    }

    @Override
    public void changeTitles(ResourceBundle res) {
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }

    /**
     * Компонент поддерживает мультивыбор значений?
     * @return <code>true</code> или <code>false</code>
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }
}
