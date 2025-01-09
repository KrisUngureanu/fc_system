package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeFieldPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.*;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.adapters.TreeAdapter.Node;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebTree;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.Margin;
import kz.tamur.web.common.ServletUtilities;
import kz.tamur.web.common.WebUtils;
import kz.tamur.util.Funcs;
import kz.tamur.or3.client.comps.interfaces.OrTreeFieldComponent;
import kz.tamur.or3.client.comps.interfaces.TreeComponent;

import org.jdom.Element;

import javax.swing.tree.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class OrWebTreeField extends WebButton implements JSONComponent, JSONCellComponent, OrTreeFieldComponent {
    private static final PropertyNode PROPS = new TreeFieldPropertyRoot();

    private OrGuiContainer guiParent;

    private WebTreeComponent buttonTree;

    private OrWebTree orTree;
    private boolean titleMode;
    private boolean isHelpClick = false;
    private Color fontColor;
    private TreeFieldAdapter adapter;
    private int dialogWidth;
    private int dialogHeight;
    private String title = "";
    private boolean isClearBtnExists;
    private boolean isFolderAsLeaf = false;
    protected boolean showSearchLine = true;
	private String sortOrder;
	private boolean childrenPath;

    OrWebTreeField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrTreeField", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        buttonTree = new WebTreeComponent();
        
        try {
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        updateProperties();
	        orTree = new OrWebTree(xml, mode, frame, isEditor, false, id);
	        orTree.setRootVisible(false);
	        orTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
	        setIconPath("Tree");
	        marginImage = new Margin(0, 2, 0, 2);
	        adapter = new TreeFieldAdapter(frame, this, isEditor);
	        orTree.setAdapter(adapter);
	        PropertyNode pn = PROPS.getChild("pov");
	        PropertyNode node = pn.getChild("activity").getChild("editable");
	        PropertyValue pv = getPropertyValue(node);
	        if (pv != null && !pv.isNull()) {
	            setEnabled(!pv.booleanValue());
	        }
	        node = PROPS.getChild("title");
	        pv = getPropertyValue(node);
	        if (!pv.isNull()) {
	            title = pv.stringValue(frame.getKernel());
	        }
	        node = pn.getChild("dialogSize").getChild("dialogWidth");
	        pv = getPropertyValue(node);
	        dialogWidth = pv.isNull() ? (Integer) node.getDefaultValue() : pv.intValue();
	
	        node = pn.getChild("dialogSize").getChild("dialogHeight");
	        pv = getPropertyValue(node);
	        dialogHeight = pv.isNull() ? (Integer) node.getDefaultValue() : pv.intValue();
	        
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
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
        
        this.xml = null;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public void setLangId(long langId) {
    	updateDescription();

        TreeNode root = (TreeNode) orTree.getModel().getRoot();
        if (root.getChildCount() > 0) {
            TreeAdapter.Node n = (TreeAdapter.Node) root.getChildAt(0);
            n.reset();
        }
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
        if (!pv.isNull()) {
            setText(pv.stringValue(frame.getKernel()));
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        pv = getPropertyValue(PROPS.getChild("ref").getChild("childrenRef"));
        if (!pv.isNull()) {
            childrenPath = true;
        }
        pv = getPropertyValue(PROPS.getChild("ref").getChild("sortTreeData"));
        if (!pv.isNull()) {
        	sortOrder = pv.intValue() == 0 ? "asc" : "desc";
        }
        
        updateProperties(PROPS);
        
        PropertyNode pn = PROPS.getChild("view");
        PropertyNode pnode = pn.getChild("alignmentText");
        pv = getPropertyValue(pnode);
        if (!pv.isNull()) {
            // setHorizontalAlignment(pv.intValue());
        } else {
            // setHorizontalAlignment(((Integer)pnode.getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFonts(pv.fontValue());
        }

        pv = getPropertyValue(pn.getChild("clearBtnShow"));
        isClearBtnExists = pv.booleanValue();

        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            fontColor = pv.colorValue();
            setForegroundColors(fontColor);
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackgroundColors(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("fullPath"));
        if (!pv.isNull()) {
            titleMode = pv.booleanValue();
            if (pv.booleanValue()) {
            }
        }
    }

    private void setBackgroundColors(Color bg) {
        setBackground(bg);
    }

    private void setForegroundColors(Color bg) {
    }

    private void setFonts(Font f) {
        setFont(f);
    }

    public OrWebTree getOrTree() {
        return orTree;
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

    public boolean isTitleMode() {
        return titleMode;
    }

    public TreeComponent getTree() {
        return buttonTree;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public String treeFieldPressed(String uid) {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            KrnObject obj = null;
            if (adapter.getDataRef() != null) {
                Item o = adapter.getDataRef().getItem(0);
                if (o != null && o.getCurrent() instanceof KrnObject)
                    obj = (KrnObject) o.getCurrent();
            } else {
                obj = adapter.getObject();
            }

            if (obj != null) {
                TreePath path = adapter.getRoot().find(obj, true);
                if (path == null) {
                    path = adapter.getRoot().find(obj, false);
                }
                if (path != null) {
                    adapter.getTree().expandPath(path);
                    adapter.getTree().setSelectionPath(path);
                }
            }
        }
        return getTreeWindowHTMLNew(uid);
    }

    public JsonObject actionPerformed2(String id, int row, int col) {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            KrnObject obj = null;
            if (adapter.getDataRef() != null) {
                Item o = adapter.getDataRef().getItem(0);
                if (o != null && o.getCurrent() instanceof KrnObject)
                    obj = (KrnObject) o.getCurrent();
            } else {
                obj = adapter.getObject();
            }

            if (obj != null) {
                TreePath path = adapter.getRoot().find(obj, true);
                if (path == null) {
                    path = adapter.getRoot().find(obj, false);
                }
                if (path != null) {
                    adapter.getTree().expandPath(path);
                    adapter.getTree().setSelectionPath(path);
                }
            }
        }
        return getTreeWindowHTML2(id, row, col);
    }

    public void setValue(String value) {
        try {
            if ("YES".equals(value)) {
                adapter.setValue(adapter.getSelectedNode());
            } else if ("CLEAR".equals(value)) {
                adapter.setValue(null);
                if (adapter.getRef() != null) {
                    OrRef ref = adapter.getRef();
                    OrRef.Item item = ref.getItem(0);
                    if (item != null && item.getCurrent() != null) {
                        ref.deleteItem(adapter, adapter);
                    }
                }
                setText("");
            }
        } catch (Exception e) {
            log.error("|USER: " + ((WebFrame) frame).getSession().getUserName() + "| interface id="
                    + ((WebFrame) frame).getObj().id + "| ref=" + adapter.getRef() + "| value=" + value);
            log.error(e, e);
        }
    }

    public String getTreeWindowHTMLNew(String uid) {
        StringBuilder out = new StringBuilder(2048);
        out.append(ServletUtilities.DOCTYPE).append(ServletUtilities.EOL);
        out.append("<html>").append(ServletUtilities.EOL);

        out.append("<body>").append(ServletUtilities.EOL);
        if(showSearchLine) {
        	out.append("<input id='_").append(uid).append("' class=\"treefield-search\" placeholder=\"Поиск\" style=\"width: inherit;height:20px;position: fixed\"/>");
        }
        out.append("<div style='overflow:auto; text-align:left;");
        if(showSearchLine) out.append("padding-top: 20px'");
        out.append(">");
        out.append("<ul class='easyui-tree' url='../main?guid=" + getWebSession().getWebUser().getGUID() + "&treeData=").append(uid).append("'").append(" id='trfld").append(uid).append("'");
        out.append(" selectFolder='").append(getOrTree().isFolderSelect());
        out.append("'>");
        out.append("</ul>");
        out.append("</div>");

        out.append("</body>");
        out.append("</html>");
        return out.toString();
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
    		WebTree tree = (WebTree) adapter.getTree();
    		MutableTreeNode mroot = (MutableTreeNode) tree.getModel().getRoot();
    		Node root = (Node) mroot.getChildAt(0);
    		
    		if (root != null && root.getChildCount() > 0) {
    			KrnObject obj = (KrnObject) root.rec.getValue();
    			return root.findTitleByObj(obj, title);
    		}            
    	} catch (Exception e) {
        	log.error(e, e);
        }
        return result;
    }

    /**
     * Вызов при клике на кнопку
     * 
     * @param nid
     * @return
     */
    public String getData(String nid) {
        JsonArray arr = new JsonArray();

        WebTree tree = (WebTree) adapter.getTree();
        MutableTreeNode mroot = (MutableTreeNode) tree.getModel().getRoot();

        if (mroot != null && mroot.getChildCount() > 0) {
            long parentId = 0;
            MutableTreeNode node = mroot;
            if (nid != null && nid.length() > 0) {

                TreePath path = getPathByNodeId(nid);
                if (path != null) {
                    node = (TreeAdapter.Node) path.getLastPathComponent();
                    orTree.expandPath(path);
                    parentId = ((TreeAdapter.Node) node).getObject().id;
                }
            }
            for (int i = 0; i < node.getChildCount() && (parentId != 0 || i == 0); i++) {
                getData((TreeAdapter.Node) node.getChildAt(i), parentId, arr);
            }
        }
        if (sortOrder != null && !childrenPath) arr = sort(arr, sortOrder);
        return arr.toString();
    }

    public void getData(TreeAdapter.Node node, long parentId, JsonArray arr) {
        JsonObject row = new JsonObject();

        boolean expanded = orTree.isExpanded(new TreePath(node.getPath()));

        if (node.getObject() != null) {
            long id = node.getObject().id;
            String val = node.toString();

            row.add("id", id);
            row.add("text", val);
            row.add("state", node.getChildCount() == 0 || expanded ? "open" : "closed");
            row.add("parent", parentId);
            if (isFolderAsLeaf) {
                row.add("iconCls", "tree-file");
            }
            if (expanded) {
                JsonArray arr2 = new JsonArray();
                for (int i = 0; i < node.getChildCount(); i++) {
                    getData((TreeAdapter.Node) node.getChildAt(i), node.getObject().id, arr2);
                }
                row.add("children", arr2);
            }
        }

        arr.add(row);
    }

    private JsonArray sort(JsonArray arr, String sortOrder) {
		JsonArray res = new JsonArray();
        
        List<JsonObject> sitems = new ArrayList<JsonObject>(arr.size());
        for (int i=0; i<arr.size(); i++)
        	sitems.add((JsonObject)arr.get(i));
        
        Comparator<JsonObject> comparator = new TreeFieldSorter(sortOrder);
        Collections.sort(sitems, comparator);
        
        for (int i=0; i<sitems.size(); i++)
        	res.add(sitems.get(i));

		return res;
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

    public JsonObject getTreeWindowHTML2(String id, int row, int col) {
        return getTreeHtml2(id, col);
    }

    public JsonObject getTreeHtml2(String id, int col) {
        WebTree tree = (WebTree) adapter.getTree();
        MutableTreeNode mroot = (MutableTreeNode) tree.getModel().getRoot();
        if (mroot != null && mroot.getChildCount() > 0) {
            TreeAdapter.Node root = (TreeAdapter.Node) mroot.getChildAt(0);
            return new JsonObject().add("id", id).add("tree", tree.getJSONNode(root, col, this.id));
        }
        return null;
    }

    public void selectNode(long nodeId) {
        WebTree tree = (WebTree) adapter.getTree();
        if (adapter.getRoot() != null) {
            TreePath path = adapter.getRoot().find(nodeId, true);
            if (path == null) {
                path = adapter.getRoot().find(nodeId, false);
            }
            if (path != null) {
                tree.setSelectionPath(path);
            }
        }
    }

    public void setSelectedNode(long nodeId) throws Exception {
        WebTree tree = (WebTree) adapter.getTree();
        if (nodeId == -1) {
            tree.setSelectionPath(null);
            adapter.setValue(null);
            if (adapter.getRef() != null) {
                OrRef ref = adapter.getRef();
                OrRef.Item item = ref.getItem(0);
                if (item != null && item.getCurrent() != null) {
                    ref.deleteItem(adapter, adapter);
                }
            }
            setText("");
        } else {
            if (adapter.getRoot() != null) {
                TreePath path = adapter.getRoot().find(nodeId, true);
                if (path == null) {
                    path = adapter.getRoot().find(nodeId, false);
                }
                if (path != null) {
                    tree.setSelectionPath(path);
                }
                adapter.setValue(adapter.getSelectedNode());
            }
        }
    }

    public JsonObject expand2(String nid, String wait, int col) {
        OrWebTree tree = (OrWebTree) adapter.getTree();
        return tree.expand2(nid, wait, col, id);
    }

    class WebTreeComponent extends WebTree implements TreeComponent {
        public WebTreeComponent() {
            super(null, Mode.RUNTIME, OrWebTreeField.this.frame, null);
            setRootVisible(false);
        }

        public void setModel(TreeModel m) {
            super.setModel(m);
            valueChanged = true;
            JsonObject obj = new JsonObject();
            if (isTitleMode()) {
                obj.add("value", getJSONFieldSet());
            } else {
                obj.add("value", Funcs.xmlQuote(getText()));
            }
            sendChangeProperty("valueChange", obj);
        }
    }

    private JsonObject getJSONFieldSet() {
        TreeAdapter.Node root = adapter.getRoot();
        KrnObject object = adapter.getObject();
        TreeAdapter.Node node = null;

        if (root != null && object != null) {
            TreePath path = root.find(object, true);
            if (path == null) {
                path = root.find(object, false);
            }
            if (path != null)
                node = (TreeAdapter.Node) path.getLastPathComponent();
        }
        JsonObject obj = new JsonObject();
        JsonObject style = new JsonObject();

        addSize(style);
        style.add("position", "absolute");
        obj.add("style", style);
        obj.add("e", toInt(isEnabled()));

        if (isEnabled()) {
            obj.add("onClick", "if (showToolTip(event, '" + Funcs.xmlQuote2(tooltipText) + "')) {treeFieldPressed(this, "
                    + dialogWidth + ", " + dialogHeight + ");}");
        }
        if (node != null) {
            TreeNode[] nodes = node.getPath();
            if (nodes.length > 0) {
                JsonArray nodesJSON = new JsonArray();
                JsonObject obj_;
                JsonObject img;
                for (int i = 0; i < nodes.length - 1; i++) {
                    if (nodes[i] instanceof TreeAdapter.Node) {
                        obj_ = new JsonObject();
                        obj_.add("title", nodes[i].toString());
                        img = new JsonObject();
                        img.add("src", "images/minus.gif");
                        obj_.add("img", img);
                        nodesJSON.add(obj_);
                    }
                    obj_ = new JsonObject();
                    obj_.add("title", nodes[nodes.length - 1].toString());
                    img = new JsonObject();
                    img.add("src", "images/empty.gif");
                    obj_.add("img", img);
                    nodesJSON.add(obj_);
                    obj.add("nodes", nodesJSON);
                }
            }
        }
        return obj;
    }

    public int getDialogWidth() {
        return dialogWidth;
    }

    public int getDialogHeight() {
        return dialogHeight;
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        TreeAdapter.Node root = adapter.getRoot();
        KrnObject object = adapter.getObject();
        TreeAdapter.Node node = null;

        if (root != null && object != null) {
            TreePath path = root.find(object, true);
            if (path == null) {
                path = root.find(object, false);
            }
            if (path != null)
                node = (TreeAdapter.Node) path.getLastPathComponent();
        }

        property.add("titleMode", isTitleMode());
        if (isTitleMode()) {
            property.add("fieldSet", getJSONFieldSet());
        } else {
        }
        property.add("e", toInt(isEnabled()));
        JsonObject img = new JsonObject();
        img.add("src", "images/" + iconPath + ".gif");
        property.add("img", img);
        property.add("text", getText());

        property.add("title", title);
        property.add("width", Utils.mergeWidth(dialogWidth));
        property.add("height", Utils.mergeHeight(dialogHeight));
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state) {
        JsonObject obj = addJSON(tid);
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();
        property.add("row", row);
        property.add("column", column);
        property.add("cellEditable", cellEditable);
        property.add("isSelected", isSelected);
        property.add("state", state);

        TreeAdapter.Node root = adapter.getRoot();
        String str = "<...>";
        if (value instanceof KrnObject) {
            KrnObject object = (KrnObject) value;
            TreeAdapter.Node node = null;

            if (root != null && object != null) {
                TreePath path = root.find(object, true);
                if (path == null) {
                    path = root.find(object, false);
                }
                if (path != null) {
                    node = (TreeAdapter.Node) path.getLastPathComponent();
                    if (node != null)
                        str = node.toString();
                }
            }
        } else if (value instanceof String) {
            str = (String) value;
        }

        WebUtils.getColorState(state, style);

        property.add("title", Funcs.xmlQuote(str));
        property.add("dialogWidth", dialogWidth);
        property.add("dialogHeight", dialogHeight);
        if (style.size() > 0) {
            obj.add("st", style);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        return obj;
    }

    public String valueToString(Object value) {
        TreeAdapter.Node root = adapter.getRoot();
        String str = "";
        if (value instanceof KrnObject) {
            KrnObject object = (KrnObject) value;
            TreeAdapter.Node node = null;

            if (root != null && object != null) {
                TreePath path = root.find(object, true);
                if (path == null) {
                    path = root.find(object, false);
                }
                if (path != null) {
                    node = (TreeAdapter.Node) path.getLastPathComponent();
                    if (node != null)
                        str = node.toString();
                }
            }
        } else if (value instanceof String) {
            str = (String) value;
        }
        return str;
    }

    public void clearValue() {
        if (isClearBtnExists) {
            adapter.clearValue();
        }
    }
    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }

	public boolean isShowSearchLine() {
		return showSearchLine;
	}
	
	class TreeFieldSorter implements Comparator<JsonObject> {

        private String sortOrder;

		public TreeFieldSorter(String sortOrder) {
			super();
			this.sortOrder = sortOrder;
		}

		public int compare(JsonObject a, JsonObject b) {
			int res = 0;
			res = a.get("text").asString().compareToIgnoreCase(b.get("text").asString());
			res = ("asc".equals(sortOrder)) ? res : -res;
        	return res;
        }
    }
}
