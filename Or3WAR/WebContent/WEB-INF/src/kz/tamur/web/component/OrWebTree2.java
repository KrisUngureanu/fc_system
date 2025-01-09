package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent2;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.adapters.TreeTableAdapter2;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import kz.tamur.web.common.IntegerRef;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.ServletUtilities;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebTree;
import kz.tamur.web.controller.WebController;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class OrWebTree2 extends WebTree implements JSONComponent, OrTreeComponent2 {

    public static PropertyNode PROPS = new TreePropertyRoot();
    protected Cache cash;
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    private OrGuiContainer guiParent;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private WebButton treeFieldButton = null;

    protected TreeAdapter2 adapter;

    private JPopupMenu nodeOperations_ = new JPopupMenu();
    private JMenuItem nodeRenameItem_;
    private JMenuItem nodeCreateItem_;
    private JMenuItem nodeCreateBeforeItem_;
    private JMenuItem nodeCreateAfterItem_;
    private JMenuItem nodeCreateWithHistoryItem_;
    private JMenuItem nodeDeleteItem_;
    private JMenuItem nodeSelectChildren_;
    private JMenuItem expandItem;
    private JMenuItem collapsItem;

    protected ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    private TreeTableAdapter2 tableAdapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private boolean useCheck = false;
    private boolean singleClick = false;

    private OrWebTreeTable2 treeTable;
	private boolean multiSelection = false;
	private boolean rootChecked = true;
	private boolean isWrapNodeContent = false;

    protected OrWebTree2(Element xml, int mode, OrFrame frame, boolean isEditor, String id) {
        this(xml, mode, frame, isEditor, true, id);
    }

    protected OrWebTree2(Element xml, int mode, OrFrame frame, boolean isEditor, boolean needAdapter, String id) {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);

        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }

        if (!WebController.NO_COMP_DESCRIPTION) {
            pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = frame.getBytes(descriptionUID);
            }
        }

        if (mode == Mode.RUNTIME) {
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
            
            PropertyNode pn = PROPS.getChild("view").getChild("wrapNodeContent");
            if (pn != null) {
	            pv = getPropertyValue(pn);
	            if (pv.isNull()) {
	            	isWrapNodeContent = ((Boolean) pn.getDefaultValue()).booleanValue();
	            } else {
	            	isWrapNodeContent = pv.booleanValue();
	            }
            }
        }
    }

    public TreeTableAdapter2 getTableAdapter() {
        return tableAdapter;
    }

    public void setTableAdapter(TreeTableAdapter2 tableAdapter) {
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
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            changeTitles(res);
        }
        if (adapter != null) {
            adapter.setLangId(langId);
        }
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

    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

    public TreeAdapter2 getAdapter(boolean isEditor, boolean needAdapter) throws KrnException {
        if (needAdapter && adapter == null)
            adapter = new TreeAdapter2(frame, this, isEditor);
        return adapter;
    }

    public TreeAdapter2 getAdapter() {
        return adapter;
    }

    public void setAdapter(TreeAdapter2 adapter) {
        this.adapter = adapter;
        setModel(adapter.getModel());
        setRootVisible(true);
    }

    public void moveUp(String nid) {
        try {
	        long objId = Long.parseLong(nid);
	        adapter.moveUp(objId);
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }
    public void moveDown(String nid) {
    	try {
            long objId = Long.parseLong(nid);
			adapter.moveDown(objId);
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }

    public void changeTitles(ResourceBundle res) {
        if (nodeRenameItem_ != null)
            nodeRenameItem_.setText(res.getString("renameNode"));
        if (nodeCreateItem_ != null)
            nodeCreateItem_.setText(res.getString("createNode"));
        if (nodeCreateWithHistoryItem_ != null)
            nodeCreateWithHistoryItem_.setText(res.getString("createNodeAndBind"));
        if (nodeDeleteItem_ != null)
            nodeDeleteItem_.setText(res.getString("deleteNode"));
        if (expandItem != null)
            expandItem.setText(res.getString("expandNode"));
        if (collapsItem != null)
            collapsItem.setText(res.getString("collapseNode"));
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        if (nodeRenameItem_ != null)
            nodeRenameItem_.setEnabled(isEnabled);
        if (nodeCreateItem_ != null)
            nodeCreateItem_.setEnabled(isEnabled);
        if (nodeCreateWithHistoryItem_ != null)
            nodeCreateWithHistoryItem_.setEnabled(isEnabled);
        if (nodeDeleteItem_ != null)
            nodeDeleteItem_.setEnabled(isEnabled);

    }

    public void expandAll(Node node) {
        if (node != null && !node.isLeaf()) {
            expandPath(new TreePath(node.getPath()));
            adapter.nodeExpanded(node);

            Enumeration childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                Node child = (Node) childNodes.nextElement();
                expandAll(child);
            }
        }
    }

    public void collapseAll(Node node) {
        if (node != null) {
            Enumeration childNodes = node.children();
            while (childNodes.hasMoreElements()) {
                Node child = (Node) childNodes.nextElement();
                collapseAll(child);
            }
            if (!node.isLeaf()) {
                collapsePath(new TreePath(node.getPath()));
            }
        }
    }

    private void getChildPaths(TreePath path, List<TreePath> paths) {
        paths.add(path);
        Node node = (Node) path.getLastPathComponent();
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            Node child = (Node) node.getChildAt(i);
            TreePath childPath = path.pathByAddingChild(child);
            getChildPaths(childPath, paths);
        }
    }

    public JsonObject treeTableNodeToJSON(Node node, int row, boolean isSelected) {
        JsonObject obj = new JsonObject();
        String val = "";
        long id = 0;
        int childCount = node.getChildCount();
        boolean isExpanded = isExpanded(new TreePath(node.getPath()));

        id = node.getObject().id;
        val = node.toString(row);
        if (useCheck) {
            JsonObject check = new JsonObject();
            check.add("class", "check");
            check.add("id", "chb" + id);
            check.add("onClick", "selectIfcRow(event, this.parentNode.parentNode,true); event.stopPropagation();");
            if (isSelected && !singleClick) {
                check.add("checked", true);
            }
            obj.add("check", check);
        }
        if (childCount > 0) {
            obj.add("id", id);
            obj.add("onClick", "treeTableExpand(this); event.stopPropagation();");
            JsonObject img = new JsonObject();
            img.add("id", "img" + id);
            img.add("src", "images/" + (isExpanded ? "minus" : "plus") + ".gif");
            obj.add("img", img);

        } else {
            JsonObject img = new JsonObject();
            img.add("src", "images/empty.gif");
            obj.add("img", img);
        }
        obj.add("value", val);
        return obj;
    }

    public JsonObject getJSONNode(Node node, int col) {
        String val = "";
        long id = 0;
        int childCount = node.getChildCount();
        TreePath path = new TreePath(node.getPath());
        boolean isExpanded = isExpanded(path);
        int row = getRowForPath(path);

        id = node.getObject().id;
        val = node.toString(row);

        JsonObject obj = new JsonObject();

        String cl = getSelectionModel().isPathSelected(new TreePath(node.getPath())) ? "Current" : "";
        JsonObject icon = new JsonObject();
        JsonObject img = new JsonObject();
        if (childCount > 0) {
            icon.add("id", id);
            String onclick = "treeExpand(this";
            if (col > -1) {
                onclick += ", " + col;
            }
            onclick += ");";
            icon.add("onClick", onclick);
            img.add("id", "img" + id);
            img.add("src", "images/" + (isExpanded ? "minus" : "plus") + ".gif");
            icon.add("img", img);
        } else {
            img.add("src", "images/empty.gif");
            icon.add("img", img);
        }
        obj.add("icon", icon);

        JsonObject item = new JsonObject();

        if (!cl.isEmpty()) {
            item.add("class", cl);
        }
        item.add("uuid", "s" + id);
        item.add("onClick", "treeSelChanged(this, 'null', " + (isFolderSelect || childCount == 0) + (col > -1 ? ", " + col : "")
                + ");");
        if (childCount > 0) {
            item.add("onDblClick", "treeExpandID('" + id + "'" + (col > -1 ? ", " + col : "") + ");");
        }
        item.add("value", val);
        obj.add("item", item);

        if (childCount > 0) {
            JsonObject children = new JsonObject();
            if (!isExpanded)
                children.add("class", "Hidden");
            else {
                children.add("class", "Shown");
                JsonArray children_ = new JsonArray();
                for (int i = 0; i < childCount; i++) {
                    Node child = (Node) node.getChildAt(i);
                    children_.add(getJSONNode(child, col));
                }
                children.add("children", children_);

            }
            children.add("uuid", "ul" + id);
            obj.add("children", children);
        }
        return obj;
    }

    public JsonObject getXML2(Node node, int col) {
        JsonObject sb = new JsonObject();
        sb.add("id", node.getObject().id);

        int childCount = node.getChildCount();
        JsonArray data = new JsonArray();
        for (int i = 0; i < childCount; i++) {
            Node child = (Node) node.getChildAt(i);
            data.add(getJSONNode(child, col));
        }
        sb.add("data", data);
        return sb;
    }

    public void treeTableNodeToHTML(Node node, int row, StringBuilder sb, boolean isSelected) {
        String val = "";
        String cl = "";
        long id = 0;
        int childCount = node.getChildCount();
        boolean isExpanded = isExpanded(new TreePath(node.getPath()));

        id = node.getObject().id;
        val = node.toString(row);

        if (childCount > 0) {
            if (useCheck) {
                sb.append("<input class='check' style='margin:1 5 1 1;' type='checkbox' id='chb").append(id).append("'");
                sb.append(" onclick=\"selectIfcRow(event, this.parentNode.parentNode,true); event.stopPropagation();\"");
                sb.append(isSelected && !singleClick ? " checked='1'/>" : "/>");
            }
            sb.append("<a ").append(cl).append(" id='").append(id)
                    .append("' onclick='treeTableExpand(this); event.stopPropagation();'>");
            sb.append("<img id='img").append(id).append("' src='images/").append(isExpanded ? "minus" : "plus")
                    .append(".gif'/></a>");
        } else {
            if (useCheck) {
                sb.append("<input class='check' style='margin:1 5 1 1;' type='checkbox' id='chb").append(id).append("'");
                sb.append(" onclick='selectIfcRow(event, this.parentNode.parentNode, true); event.stopPropagation();'");
                sb.append(isSelected && !singleClick ? " checked='1'/>" : "/>");
            }
            sb.append("<img src='images/empty.gif'/>");
        }
        sb.append(val);
    }

    public void toHTML(StringBuilder b) {
        b.append("<div align='left'");
        b.append(" id='").append(id).append("'");
        StringBuilder temp = new StringBuilder(256);
        addSize(temp);
        kz.tamur.rt.Utils.getColorState(state, temp);
        addConstraints(temp);
        if (temp.length() > 0) {
            b.append(" style='").append(temp).append(" overflow:auto;'");
        }
        b.append(" align='left'>").append(ServletUtilities.EOL);
        b.append("<ul>");
        Node root = (Node) getModel().getRoot();
        b.append(nodeToHTML(root, -1));
        b.append("</ul>");
        b.append("</div>");
    }

    public String nodeToHTML(Node node, int col) {
        String val = "";
        long id = 0;
        int childCount = node.getChildCount();
        TreePath path = new TreePath(node.getPath());
        boolean isExpanded = isExpanded(path);
        int row = getRowForPath(path);

        id = node.getObject().id;
        val = node.toString(row);

        StringBuffer sb = new StringBuffer();

        String cl = getSelectionModel().isPathSelected(new TreePath(node.getPath())) ? "class='Current'" : "";
        sb.append("<li>");
        if (childCount > 0) {
            sb.append("<a ").append(cl).append(" id='").append(id).append("' onclick=\"treeExpand(this");
            if (col > -1) {
                sb.append(", ").append(col);
            }
            sb.append(");\"><img id='img").append(id).append("' src='images/").append(isExpanded ? "minus" : "plus")
                    .append(".gif' /></a>");
        } else {
            sb.append("<img src='images/empty.gif'/>");
        }

        sb.append("<a ").append(cl).append(" id='s").append(id).append("' onClick=\"treeSelChanged(this, 'null', ")
                .append(isFolderSelect || childCount == 0);
        if (col > -1) {
            sb.append(", ").append(col);
        }
        sb.append(");\"");
        if (childCount > 0) {
            sb.append(" ondblclick=\"treeExpandID('" + id + "'");
            if (col > -1) {
                sb.append(", ").append(col);
            }
            sb.append(");\"");
        }
        sb.append(">").append(val).append("</a></li>");

        if (childCount > 0) {
            if (!isExpanded)
                sb.append("<ul class=\"Hidden\" id=\"ul").append(id).append("\"></ul>");
            else {
                sb.append("<ul class=\"Shown\" id=\"ul").append(id).append("\">");
                for (int i = 0; i < childCount; i++) {
                    Node child = (Node) node.getChildAt(i);
                    sb.append(nodeToHTML(child, col));
                }
                sb.append("</ul>");

            }
        }
        return sb.toString();
    }

    public String getXML(Node node, int col) {
        StringBuffer sb = new StringBuffer();
        sb.append("<r>");
        sb.append("<id>");
        sb.append(node.getObject().id);
        sb.append("</id>");
        sb.append("<data>");
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = (Node) node.getChildAt(i);
            sb.append(nodeToHTML(child, col));
        }
        sb.append("</data>");
        sb.append("</r>");
        return sb.toString();
    }

    public int getRowCount() {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        int res = getVisibleRowCount(mroot);
        return res;
    }

    public int getRowForPath(TreePath treePath) {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        IntegerRef res = new IntegerRef(0);
        getDistanceBetween(mroot, node, res);
        return res.i;
    }

    public TreePath getPathForRow(int row) {
        DefaultMutableTreeNode mroot = (DefaultMutableTreeNode) getModel().getRoot();
        return getPathForRow(mroot, new IntegerRef(row));
    }

    public boolean isExpanded(TreePath path) {
        if (path != null) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (n instanceof TreeAdapter2.Node) {
                TreeAdapter2.Node node = (TreeAdapter2.Node) path.getLastPathComponent();
                return adapter.isNodeExpanded(node);
            }
        }
        return false;
    }

    public String expand(String nid, String wait, int col) {
        try {
            TreeModel model = getModel();
            Node root = (Node) model.getRoot();
            if (root != null) {
                long objId = Long.parseLong(nid);
                TreePath path = root.find(objId, true);
                if (path == null)
                    path = root.find(objId, false);
                if (path != null) {
                    if (!isExpanded(path))
                        adapter.nodeExpanded((Node) path.getLastPathComponent());
                    else
                        adapter.nodeCollapsed((Node) path.getLastPathComponent());
                    // changeState(path);
                    if (wait == null) {
                        Node node = (Node) path.getLastPathComponent();
                        return getXML(node, col);
                    }
                }
            }
        } catch (Exception e) {
        	log.error(e, e);
        }
        return "<r></r>";
    }

    public JsonObject expand2(String nid, String wait, int col) {
        try {
            TreeModel model = getModel();
            Node root = (Node) model.getRoot();
            if (root != null) {
                long objId = Long.parseLong(nid);
                TreePath path = root.find(objId, true);
                if (path == null)
                    path = root.find(objId, false);
                if (path != null) {
                    if (!isExpanded(path))
                        adapter.nodeExpanded((Node) path.getLastPathComponent());
                    else
                        adapter.nodeCollapsed((Node) path.getLastPathComponent());
                    // changeState(path);
                    if (wait == null) {
                        Node node = (Node) path.getLastPathComponent();
                        return getXML2(node, col);
                    }
                }
            }
        } catch (Exception e) {
        	log.error(e, e);
        }
        return null;
    }

    /**
     * @return the useCheck
     */
    public boolean isUseCheck() {
        return useCheck;
    }

    public void setUseCheck(boolean useCheck) {
        this.useCheck = useCheck;
    }

    public void setSingleClick(boolean singleClick) {
        this.singleClick = singleClick;
    }

    public boolean isSingleClick() {
        return singleClick;
    }

    public void setTreeTable(OrWebTreeTable2 treeTable) {
        this.treeTable = treeTable;
    }

    /**
     * @return the treeTable
     */
    public OrWebTreeTable2 getTreeTable() {
        return treeTable;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        Node root = (Node) getModel().getRoot();
        if(root != null) {
            TreePath path = getSelectionPath();
            Node n = (path != null && path.getLastPathComponent() instanceof Node) ? (Node) path.getLastPathComponent() : null;
            long id = n != null ? n.getObject().id : root != null ? root.getObject().id : 0;
            property.add("selectNode", id);
            if (multiSelection) {
            	if (rootChecked) {
            		property.add("checkboxTree", 0);
            	} else {
            		property.add("checkboxTree", root.getObject().id);
            	}
            }
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
    
    public boolean isWrapNodeContent() {
    	return isWrapNodeContent;
    }
    
    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
}
