package kz.tamur.web.component;

import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.evalExp;
import static kz.tamur.web.common.ServletUtilities.EOL;

import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeTablePropertyRoot;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent2;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent2;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComboColumnAdapter;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.adapters.TreeTableAdapter2;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.web.common.webgui.WebComponent;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 29.11.2004
 * Time: 15:51:43
 */
public class OrWebTreeTable2 extends OrWebTable implements JSONComponent, OrTreeTableComponent2 {
    private static PropertyNode treeTableProps = new TreeTablePropertyRoot();
    
    private static JsonArray titleList;
    private static List<String> titleListValues;
    private List<TreePath> foundPath;
    
    private String treeName;
    private String treeNameUID;
    private int treeWidth;
    private OrWebTree2 tree;
    private TreeAdapter2 treeAdapter;
    private static final int PADDING = 11;
    boolean useCheck = false;
    private boolean doNotUpdateRows = false;
    
    private String prevSortCol;
    private String prevSortOrder;
    private String titlePathExpr;
    
    private int childrenSize = 0;
    
    OrWebTreeTable2(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
        super(xml, mode, fm, frame, id);
        
        try {
	        getTree();
	        tree.getAdapter().setTree(tree);
	        PropertyValue pv = getPropertyValue(treeTableProps.getChild("view").getChild("expandAll"));
	        tree.getAdapter().setExpandAll(pv.booleanValue());
	        tree.setRootVisible(false);
	
	        if (mode == RUNTIME) {
	            treeAdapter = tree.getAdapter(false, true);
	            useCheck = getPropertyValue(treeTableProps.getChild("view").getChild("useCheck")).booleanValue();
	            boolean multiselection = getPropertyValue(treeTableProps.getChild("pov").getChild("multiselection")).booleanValue();
	            useCheck = useCheck && multiselection;
	            tree.setUseCheck(useCheck);
	            ((TreeTableAdapter2) adapter).setUseCheck(useCheck);
	            
	            pv = getPropertyValue(treeTableProps.getChild("ref").getChild("titlePathExpr"));
	            if (!pv.isNull()) {
	            	titlePathExpr = pv.stringValue();
	            }
	            
	            pv = getPropertyValue(treeTableProps.getChild("view").getChild("childrenSize"));
	            childrenSize = pv.isNull() ? 0 : pv.intValue();
	        }
	        tree.setTreeTable(this);
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
    }

    public OrWebTreeTableColumn createTreeTableColumn() {
        return new OrWebTreeTableColumn(xml, mode, frame, id);
    }

    public TreeAdapter2 getTreeAdapter() {
        return treeAdapter;
    }

    public OrTreeComponent2 getTree() {
        if (tree == null && mode == RUNTIME) {
            try {
                tree = new WebTreeTableCellRenderer(xml, mode, frame, false, id);
                tree.setTableAdapter((TreeTableAdapter2) adapter);
            } catch (KrnException e) {
            	log.error(e, e);
            }
        }
        return tree;
    }

    protected void init() {
        PropertyValue pv = getPropertyValue(treeTableProps.getChild("treeTitle"));
        if (!pv.isNull()) {
            Pair<String, Object> p = pv.resourceStringValue();
            treeNameUID = p.first;
            treeName = frame.getString(treeNameUID);
        } else {
            treeName = "Дерево";
            try {
                KrnObject lang = frame.getInterfaceLang();
                if (lang != null) {
                    LangItem li = LangItem.getById(lang.id);
                    if ("KZ".equals(li.code))
                        treeName = "А\u0493аш";
                }
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        pv = getPropertyValue(treeTableProps.getChild("pos").getChild("treeWidth"));
        if (!pv.isNull() && pv.intValue() != 0) {
            treeWidth = pv.intValue();
        } else {
            treeWidth = Constants.DEFAULT_PREF_WIDTH;
        }

    }

    protected WebTableModel createTableModel() throws KrnException {
        if (mode == RUNTIME) {
            adapter = new TreeTableAdapter2(frame, this, false);
            return new RtWebTreeTableModel2((TreeTableAdapter2) adapter);
        }
        return null;
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public PropertyNode getProperties() {
        return treeTableProps;
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        treeName = frame.getString(treeNameUID);
        if ("Безымянный".equals(treeName)) {
            LangItem li = LangItem.getById(langId);
            treeName = "KZ".equals(li.code) ? "А\u0493аш" : "Дерево";
        }
        if (tree != null) {
            tree.setLangId(langId);
        }
    }

    private class OrWebTreeTableColumn extends OrWebTableColumn {

        public OrWebTreeTableColumn(Element xml, int mode, OrFrame frame, String id) {
            super(xml, mode, frame, id);
        }

        public GridBagConstraints getConstraints() {
            return null;
        }

        public PropertyNode getProperties() {
            return null;
        }

        public PropertyValue getPropertyValue(PropertyNode prop) {
            return null;
        }

        public int getTabIndex() {
            return -1;
        }

        public String getTitle() {
            return treeName;
        }

        public ColumnAdapter getAdapter() {
            return null;
        }

        public boolean isCanSort() {
            return false;
        }
    }

    public String getTreeName() {
        return treeName;
    }

    public int getTreeWidth() {
        return treeWidth;
    }

    public class WebTreeTableCellRenderer extends OrWebTree2 {
        protected WebTreeTableCellRenderer(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
            super(xml, mode, frame, isEditor, id);
        }
    }

    public void setSelectedRows(int[] rows) {
        ((TreeTableAdapter2) adapter).addSelectedRows(rows);
        if (rows != null) {
            OrTreeComponent2 tree = getTree();
        	TreePath[] paths = new TreePath[rows.length];
        	for (int i=0; i<paths.length; i++) {
        		paths[i] = tree.getPathForRow(rows[0]);
        	}
			tree.setSelectionPaths(paths);
        }
        super.setSelectedRows(rows);
    }

    public String getData(String nid, String sortCol, String sortOrder) throws KrnException {
    	boolean sortChanged = (sortCol != null && prevSortCol == null)
    			|| (sortCol == null && prevSortCol != null)
    			|| (sortCol != null && prevSortCol != null && (!sortCol.equals(prevSortCol) || !sortOrder.equals(prevSortOrder)));
    	
    	prevSortCol = sortCol;
    	prevSortOrder = sortOrder;

    	if (sortChanged)
    		nid = null;

    	JsonArray arr = new JsonArray();

    	Node node = (Node)tree.getModel().getRoot();
    	if (node != null && node.getObject() != null) {
	    	long parentId = 0;
	    	if (nid != null && nid.length() > 0) {
		    	TreePath path = getPathByNodeId(nid);
		        if (path != null) {
		            node = (Node)path.getLastPathComponent();
		            parentId = node.getObject().id;
		        }
	    	}
	    	if (node != null) {
	    		TreeTableAdapter2 adapter = getTreeTableAdapter();
	    		synchronized (adapter) {
		        	adapter.nodeExpanded(node);
		        	
		        	if (parentId == 0) {
		        		JsonObject rowObj = getData(node, 0, sortCol, sortOrder);
		        		arr.add(rowObj);
		        	} else {
				    	for (int i = 0; i < node.getChildCount(); i++) {
				        	JsonObject rowObj = getData((Node)node.getChildAt(i), node.getObject().id, sortCol, sortOrder);
				        	arr.add(rowObj);
				    	}
				    	arr = sort(arr, sortCol, sortOrder);
		        	}
				}
	    	}
    	}    	
        return arr.toString();
    }

    private JsonArray sort(JsonArray arr, String sortCol, String sortOrder) {
		JsonArray res = new JsonArray();
        
        List<JsonObject> sitems = new ArrayList<JsonObject>(arr.size());
        for (int i=0; i<arr.size(); i++)
        	sitems.add((JsonObject)arr.get(i));

        if(sortCol != null || sortOrder != null){
        	Comparator<JsonObject> comparator = new RowSorter(sortOrder, sortCol);
        	Collections.sort(sitems, comparator);
        }
        
        for (int i=0; i<sitems.size(); i++)
        	res.add(sitems.get(i));

		return res;
	}

	public JsonObject getData(Node node, long parentId, String sortCol, String sortOrder) {
        JsonObject row = new JsonObject();
        TreePath path = new TreePath(node.getPath());
        int r = tree.getRowForPath(path);
        if (node.getObject() != null) {
            long id = node.getObject().id;
            String val;
            if (titlePathExpr != null && titlePathExpr.trim().length() > 0) {
				Map<String, Object> vc = new HashMap<String, Object>();
				vc.put("OBJ", node.getObject());
				val = (String) evalExp(titlePathExpr, frame, getAdapter(), vc);
            } else {
                val = node.toString(r);
            }
            // Надо чтоб загрузились дети
            node.isLeaf();
            row.add("id", id);
            row.add("fd", node.item.hasChildren ? 1 : 0);
            row.add("name", val != null ? val : "");
            if (!tree.isExpanded(path))
                row.add("state", node.getAllowsChildren() ? "closed" : "open");
            else {
                JsonArray arr = new JsonArray();
                for (int i = 0; i < node.getChildCount(); i++) {
                    JsonObject rowObj = getData((Node) node.getChildAt(i), id, sortCol, sortOrder);
                    arr.add(rowObj);
                }
		    	arr = sort(arr, sortCol, sortOrder);
                row.add("children", arr);
                if (arr.size() == 0) {
                	row.add("state", "closed");
                }
            }
            row.add("parent", parentId);
            if (tree.isFolderAsLeaf()) {
                row.add("iconCls", "tree-file");
            }
        }
        for (int j = 1; j < model.getColumnCount(); j++) {
            OrColumnComponent col = ((kz.tamur.or3.client.comps.interfaces.OrTableModel) model).getColumn(j);
            Object value = model.getValueAt(r, j);
            if (col instanceof OrWebDateColumn) {
                value = ((OrWebDateField) ((OrWebDateColumn) col).getEditor()).toString(value);
            } else if (col instanceof OrWebCheckColumn) {
                value = (value instanceof Number) ? (((Number) value).intValue() == 1 ? "x" : "") : "";
            } else if (col instanceof OrWebTreeColumn) {
                value = ((OrWebTreeField) ((OrWebTreeColumn) col).getEditor()).valueToString(value);
            } else if (col instanceof OrWebDocFieldColumn) {
                switch (((OrWebDocField) col.getEditor()).getAdapter().getAction()) {
                case Constants.DOC_UPDATE:
                    value = "<i class='fam-attach'></i>";
                    break;
                case Constants.DOC_EDIT: // TODO временно, пока не определились как в веб редактирвать файлы
                case Constants.DOC_VIEW:
                    if (value != null) {
                        StringBuilder v = new StringBuilder().append("<a onclick=\"downloadFile(event, '").append(col.getUUID())
                                .append("',").append(r).append(",").append(j)
                                .append(");\"><img src=\"media/img/DocField.gif\" /></a>");
                        value = v.toString();
                    }
                    break;
                }
            }  else if (col instanceof OrWebFloatColumn) {
                OrWebFloatField editor = (OrWebFloatField) ((OrWebFloatColumn) col).getEditor();
                value = editor.getValueAsText(value instanceof Number ? ((Number)value).doubleValue() : null);
            } else if (value instanceof Double) {
                DecimalFormat df = new DecimalFormat("#.###");
                df.setMaximumIntegerDigits(21);
                value = df.format((Double) value);
            } else if (col instanceof OrWebPopupColumn) {
            	OrWebHyperPopup editor  = (OrWebHyperPopup) ((OrWebPopupColumn) col).getEditor();
            	if (editor.isIconVisible()) {
                    StringBuilder sb = new StringBuilder();
	            	String base64Icon = editor.getBase64Icon();
	            	if (base64Icon == null) {
	            		sb.append("<i class='fam-bullet-green'></i>").append(value != null ? value.toString() : "");
	            	} else {
	            		sb.append("<img src='data:image/png;base64,").append(base64Icon).append("'/>").append(value);
	            	}
	                value = sb.toString();
            	}
            }
            String val = value != null ? value.toString() : "";
            if (col instanceof OrWebComboColumn) {
                row.add(col.getUUID() + "-title", val);
                row.add(col.getUUID(), ((ComboColumnAdapter) col.getAdapter()).getIndexAt(r));
            } else {
                row.add(col.getUUID(), val);
            }
        }
        return row;
    }

    public JsonObject getJSON(OrWebTableNavigator navi, String zebra1, String zebra2) {
        JsonObject obj = new JsonObject();
        // необходимо очистить список выбранных объектов рефа
        if (useCheck) {
            ((TreeTableAdapter2) adapter).clearSelItem();
        }

        JsonObject style = new JsonObject();
        addSize(style);
        if (style.size() > 0) {
            obj.add("style", style);
        }

        int width = 0;
        for (int i = 0; i < model.getColumnCount(); i++) {
            width += Integer.parseInt(model.getColumnWidth(i));
        }

        if (navi != null) {
            navi.setRowCount(getRowCount());
            obj.add("navi", navi.getJSON());
        }

        if (zebra1 != null) {
            obj.add("zebra1", zebra1);
        }

        if (zebra2 != null) {
            obj.add("zebra2", zebra2);
        }

        String selRows = getSelectedRowsString();
        if (selRows != null) {
            obj.add("selectedRows", selRows);
        }
        obj.add("width", width);
/*
        JsonArray headers = new JsonArray();

        for (int i = 0; i < model.getColumnCount(); i++) {
            JsonObject header = new JsonObject();
            String colName = model.getColumnName(i);
            colName = colName.replaceAll("@", "<br/>");
            header.add("width", model.getColumnWidth(i));
            header.add("title", colName);
            headers.add(header);
        }
        obj.add("headers", headers);
        JsonArray rows = new JsonArray();
        for (int i = 0; i < model.getRowCount(); i++) {
            rows.add(getRowJSON(i, zebra1, zebra2));
        }
        setSingleClick(false);
        obj.add("rows", rows);
*/

        return obj;
    }

    public JsonObject getRowJSON(int row, String zebra1, String zebra2) {
        JsonObject obj = new JsonObject();
        String zebra = row % 2 == 0 ? zebra1 : zebra2;
        obj.add("id", row);

        boolean isRowSelected = isRowSelected(row);
        boolean isColSelected = false;
        JsonArray columns = new JsonArray();
        for (int j = 0; j < model.getColumnCount(); j++) {
            JsonObject column = new JsonObject();
            Object value = model.getValueAt(row, j);
            if (renderer != null && !(value instanceof TreeModel)) {
                renderer.getTableCellRendererString(this, value, false, false, row, j, column);
            } else if (value instanceof TreeModel) {
                TreePath path = tree.getPathForRow(row);
                if (path != null && path.getLastPathComponent() instanceof Node) {
                    Node node = (Node) path.getLastPathComponent();
                    if (j == 0) {
                        int padding = (path.getPathCount() - 1) * PADDING;
                        column.add("paddingLeft", padding);
                        if (node.getObject() != null) {
                            isColSelected = ((TreeTableAdapter2) adapter).isNodeSelected(node);
                            column.add("node", tree.treeTableNodeToJSON(node, row, isRowSelected || isColSelected));
                        }
                    }
                }
            } else {
                if (value != null) {
                    column.add("value", value);
                }
            }
            columns.add(column);
        }
        obj.add("class", isRowSelected || isColSelected ? "selected" : "notselected");
        if (!(isRowSelected || isColSelected)) {
            if (zebra != null) {
                JsonObject style = new JsonObject();
                style.add("backgroundColor", zebra);
                obj.add("style", style);
            }
        }
        obj.add("columns", columns);

        return obj;
    }
    
    public void clearRowSelection() {
    	adapter.getDataRef().setSelectedItems(new int[0]);
    	setSelectedRows(new int[0]);
    	adapter.getDataRef().clearSelItem(); 
    	adapter.getDataRef().setIndex(0);       	
    	setSelectedRow(0);
    }

    public void setValue(String value) {
        value = value.replaceFirst("^,", "");
        if (value.contains(",")) {
            String[] tArr = value.split(",");
            int vArr[] = new int[tArr.length];
            for (int i = 0; i < tArr.length; ++i) {
                vArr[i] = Integer.parseInt(tArr[i]);
            }
            setSelectedRows(vArr);
        } else {
            if (tree.isSingleClick()) {
                setSelectedRow(Integer.parseInt(value));
            } else {
                setSelectedRows(new int[] { Integer.parseInt(value) });
            }

        }
        adapter.setSelectedRows(getSelectedRows());
    }

    public void selectNode(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        long[] nids = new long[st.countTokens()];
        int[] rows = new int[nids.length];
        		
        for (int i = 0; i < nids.length; i++) {
            String token = st.nextToken();
            nids[i] = Long.parseLong(token);
            TreePath path = getPathByNodeId(nids[i]);
            if (path != null) {
                rows[i] = tree.getRowForPath(path);
            }
        }
        adapter.setSelectedRows(rows);
        setSelectedRows(rows);
    }
    
    public JsonObject setValue(String val, String nid, String colUid) {
    	JsonObject res = null;
    	TreePath path = getPathByNodeId(nid);
        if (path != null) {
            int row = tree.getRowForPath(path);
            absoluteRow(row);

	        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
	        WebComponent editor = (WebComponent) c.getEditor();
	        if (editor instanceof OrWebTreeField) {
	        	try {
                    long nodeId = Long.parseLong(val);
                    ((OrWebTreeField)editor).setSelectedNode(nodeId);
                } catch (Exception e) {
                	log.error(e, e);
                }
	        } else if (editor instanceof OrWebHyperPopup) {
                res = ((OrWebHyperPopup)editor).buttonPressed(val);
	        } else
	        	editor.setValue(val);
	        
	        tableCellUpdated(row, colUid);
        }
        return res;
    }

    public void tableRowsUpdated(int firstRow, int lastRow) {
        if (doNotUpdateRows) {
            return;
        }
        if (updatedList == null) {
            updatedList = new ArrayList<Integer>();
        }
        for (int i = firstRow; i <= lastRow; i++) {
            if (!updatedList.contains(i)) {
                updatedList.add(i);
            }
            try {
                TreePath path = tree.getPathForRow(i);
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    long parentId = node.getParent() instanceof Node ? ((Node) node.getParent()).getObject().id : 0;
                    long nid = node.getObject().id;
                    JsonArray arr = new JsonArray();
                    JsonObject obj = new JsonObject();
                    obj.add("index", nid);
                    obj.add("row", getData(node, parentId, prevSortCol, prevSortOrder));
                    arr.add(obj);
                    JsonValue r = getChange("pr.reloadRow", nid);
                    JsonValue r2 = getChange("pr.reloadTreeTable");
                    if (r == null && r2 == null) {
                        sendChangeProperty("updateRow", arr);
                    }
                }
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
    }

    public void tableRowsInserted(int firstRow, int lastRow) {
        int count = lastRow - firstRow + 1;
        insertedRows = new int[count];
        for (int i = 0; i < count; i++) {
            insertedRows[i] = firstRow + i;

        	try {
	        	TreePath path = tree.getPathForRow(firstRow + i);
		        Node node = (Node)path.getLastPathComponent();
		        long parentId = (node.getParent() instanceof Node) ? ((Node)node.getParent()).getObject().id : 0;
		        long nid = node.getObject().id;
		        
		        JsonArray arr = new JsonArray();
		        JsonObject obj = new JsonObject();
		        obj.add("index", nid);
		        obj.add("row", getData(node, parentId, prevSortCol, prevSortOrder));
		        arr.add(obj);
		        
		        sendChangeProperty("addRow", arr);
        	} catch (Exception e) {
            	log.error(e, e);
        	}
        }
    }

    public String getCellEditor(String nid, String colUid) {
        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
        JsonObject res = new JsonObject();
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            int row = tree.getRowForPath(path);
            absoluteRow(row);
	        if (c != null && c.isCellEditable(row)) {
	        	res = c.getCellEditor(row);
	        }
        }
        if (c instanceof OrWebMemoColumn && ((OrWebMemoColumn) c).isShowTextAsXML()) {
        	res.add("showTextAsXML", 1);
        }
    	return res.toString();
    }

    public String openPopup(String nid, String colUid, long fid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            int row = tree.getRowForPath(path);
	        absoluteRow(row);
	
	        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
	        WebComponent editor = (WebComponent) c.getEditor();
	        if (editor instanceof OrWebHyperPopup) {
	            return ((OrWebHyperPopup) editor).openPopup(row, colUid, id, fid);
	        }
        }
	    return "";
    }

    public String treeFieldPressed(String nid, String colUid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            int row = tree.getRowForPath(path);
            absoluteRow(row);

	        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
	        WebComponent editor = (WebComponent) c.getEditor();
	        if (editor instanceof OrWebTreeField) {
	            return ((OrWebTreeField) editor).treeFieldPressed(colUid);
	        }
        }
        return new JsonObject().toString();
    }

    public void expandNode(String nid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            if (tree.isExpanded(path)) {
            }
        }
    }
    
    public void expand(String nid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            if (!tree.isExpanded(path)) {
	    		TreeTableAdapter2 adapter = getTreeTableAdapter();
	    		synchronized (adapter) {
	    			adapter.nodeExpanded((Node) path.getLastPathComponent());
	    		}
            }

            adapter.countCurrentTableItem();
            if (navi != null) {
                navi.setRowCount(getRowCount());
            }
        }
    }

    public void collapse(String nid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            if (tree.isExpanded(path))
                getTreeTableAdapter().nodeCollapsed((Node) path.getLastPathComponent());

            adapter.countCurrentTableItem();
            if (navi != null) {
                navi.setRowCount(getRowCount());
            }
        }
    }
    
    public String findTitle(String nid, String title, String index) {
    	JsonObject res = new JsonObject();
    	int idx = 0;
    	if("0".equals(index)) {
    		titleList = findTitles(nid, title);
    		if(titleList != null && !titleList.isEmpty()) {
    			res = (JsonObject) titleList.get(0);
    		}
    	} else {
    		if(titleList != null && !titleList.isEmpty()) {
    			idx = Integer.parseInt(index) % titleList.size();
    			res = (JsonObject)titleList.get(idx);
    		} 
    	}
    	if(titleListValues != null && titleListValues.size() > idx)
    	res.add("value", titleListValues.get(idx));
    	return res.toString();
    }

    public JsonArray findTitles(String nid, String title) {
    	JsonArray result = new JsonArray();
        
    	try {
            Node root = (Node) treeAdapter.getModel().getRoot();
            if (root != null && root.getChildCount() > 0) {
	            List<TreePath> foundPath = null;
	            if (nid != null && nid.length() > 0) {
	                TreePath path = getPathByNodeId(nid);
	                if (path != null) {
	                	Node node = (Node) path.getLastPathComponent();
	                	if (node.getParent() instanceof Node) {
		                	Node parent = (Node) node.getParent();
		                	int index = parent.getIndex(node);
		                	titleListValues = new ArrayList<String>();
		                	foundPath = findTitle(node, title, index, false);
	                	} else {
	                		titleListValues = new ArrayList<String>();
		                	foundPath = new ArrayList<TreePath>();
	            			for (Enumeration c = root.children(); c.hasMoreElements();) {
	            			    Node child = (Node) c.nextElement();
	            			    List<TreePath> innerPath= findTitle(child, title, -1, false);
            				    if (innerPath != null)
            				    	foundPath.addAll(innerPath);
	            			}
	                	}
	                }
	            } else {
	            	titleListValues = new ArrayList<String>();
	            	foundPath = findTitle(root, title, -1, false);
	            }
	            this.foundPath = foundPath;        
	            if (foundPath != null) {
	            	for(TreePath eachPath:foundPath) {
	            		JsonObject res = new JsonObject();
	            		Node node = (Node) eachPath.getLastPathComponent();
	            		int count = eachPath.getPathCount();
	            		String parentNodes = "";		            	
		            	for(int j=1;j<count-1;j++) {
		            		Node pNode = (Node) eachPath.getPathComponent(j);
		            		if(parentNodes.length()>0)
		            			parentNodes += "," + pNode.getObject().id;
		            		else
		            			parentNodes += pNode.getObject().id;
		            	}
		            	res.add("parentNodes", parentNodes);
		            	res.add("node", node.getObject().id);
		            	result.add(res);
	            	}
	            	
	            }
	        }            
        } catch (Exception e) {
        	log.error(e, e);
        }
        return result;
    }
	
	public List<TreePath> findTitle(Node node, String text, int childIndex, boolean loadedOnly) {
		List<TreePath> result = new ArrayList<TreePath>();
        TreePath path = new TreePath(node.getPath());
        int r = tree.getRowForPath(path);

        if (childIndex == -1) {
	        String title = node.toString(r);
	        if (title != null && title.toUpperCase(Constants.OK).contains(text.toUpperCase(Constants.OK))) {
	        	result.add(path);
	        	titleListValues.add(title +"##"+ 0);
	        }
	        
	        for (int j = 1; j < model.getColumnCount(); j++) {
	            OrColumnComponent col = ((kz.tamur.or3.client.comps.interfaces.OrTableModel) model).getColumn(j);
	            Object value = model.getValueAt(r, j);
	            if (col instanceof OrWebDateColumn) {
	                value = ((OrWebDateField) ((OrWebDateColumn) col).getEditor()).toString(value);
	            } else if (col instanceof OrWebCheckColumn) {
	                value = "";
	            } else if (col instanceof OrWebTreeColumn) {
	                value = ((OrWebTreeField) ((OrWebTreeColumn) col).getEditor()).valueToString(value);
	            } else if (value instanceof Double) {
	                DecimalFormat df = new DecimalFormat("#.###");
	                df.setMaximumIntegerDigits(21);
	                value = df.format((Double) value);
	            }
	            String val = value != null ? value.toString() : "";
	            
	            if (val.toUpperCase(Constants.OK).contains(text.toUpperCase(Constants.OK))) {
		        	result.add(path);
		        	titleListValues.add(val + "##" + j);
	            }
	        }
		}
		
        boolean collapse = false;
    	if (!loadedOnly) {
            try {
                if (!tree.isExpanded(path)) {
                	collapse = true;
                	Node n = (Node) path.getLastPathComponent();
                	n.getChildCount();
    	    		TreeTableAdapter2 adapter = getTreeTableAdapter();
    	    		synchronized (adapter) {
    	    			adapter.nodeExpanded(n);
    	    		}
                }
                	
            } catch (Exception e) {
            	log.error(e, e);
            }
    	}

    	int index = 0;
		for (Enumeration c = node.children(); c.hasMoreElements();) {
		    Node child = (Node) c.nextElement();
		    if (index++ > childIndex) {
			    List<TreePath> res = findTitle(child, text, -1, loadedOnly);
			    if (res != null)
			    	result.addAll(res);
		    }
		}
		
        if (collapse && tree.isExpanded(path))
        	getTreeTableAdapter().nodeCollapsed((Node) path.getLastPathComponent());

		if (childIndex > -1) {
			TreeNode parent = node.getParent();
			if (parent instanceof Node) {
				index = parent.getIndex(node);
				List<TreePath> res = findTitle((Node)parent, text, index, loadedOnly);
				if(res!=null)
					result.addAll(res);
			}
		}
		
		return result;
    }

	public TreePath getPathByNodeId(long objId) {
        TreePath path = null;
        if (treeAdapter.getModel().getRoot() != null) {
            try {
                path = ((Node) treeAdapter.getModel().getRoot()).find(objId, true);
                if (path == null)
                    path = ((Node) treeAdapter.getModel().getRoot()).find(objId, false);
            } catch (KrnException e) {
            	log.error(e, e);
            }
        }
        return path;
    }

    public TreePath getPathByNodeId(String nid) {
        long objId = Long.parseLong(nid);
        return getPathByNodeId(objId);
    }

    protected void absoluteRow(int row) {
        TreePath path = getTree().getPathForRow(row);
        if (path != null) {
            tree.setSelectionPath(path);
            setSelectedRow(row);
        }
        adapter.setSelectedRows(getSelectedRows());
    }

    public void getErrorXML(StringBuilder out) {
    }

    private TreeTableAdapter2 getTreeTableAdapter() {
        return (TreeTableAdapter2) adapter;
    }

    public String getJavaScript() {
        StringBuilder out = new StringBuilder(190);
        if (treeAdapter.isWClickAsOK() || true) {
            out.append(EOL);
            // добавление javascript
            out.append(
                    "$(\"table[id='tbl" + id + "'] tbody tr\").live('dblclick',clickOKforModalFrame($(\"table[id='tbl" + id
                            + "']\")));").append(EOL);
            out.append(EOL);
        }
        return out.toString();
    }

    /**
     * @param singleClick
     *            the singleClick to set
     */
    public void setSingleClick(boolean singleClick) {
        tree.setSingleClick(singleClick);
    }

    public void tableDataChanged() {
        dataChanged = true;
        selectedRows = null;
        if (navi != null) {
            navi.setRowCount(getRowCount());
        }
        ((TreeTableAdapter2) adapter).setOldSelectedRow(new int[] {});
        removeChange("pr.updateRow");
        removeChange("pr.reloadRow");
        sendChangeProperty("reloadTreeTable", 1);
    }
    
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        obj.set("pr", property);

        property.add("tree", getJSON(isNaviExists ? navi : null, zebra1, zebra2));
        if (isNaviExists && addPan != null) {
            property.add("panel", addPan.getJSON());
        }
        sendChange(obj, isSend);
        removeChange("pr.updateRow");
        removeChange("pr.reloadRow");
        removeChange("pr.reloadTreeTable");
        return obj;
    }

    public void setDoNotUpdateRows(boolean doNotUpdateRows) {
        this.doNotUpdateRows = doNotUpdateRows;
    }
    
    public String getSelectedRowsString() {
        List<Item> items = ((TreeTableAdapter2) adapter).getDataRef().getSelectedItems();
        if (items != null && items.size() > 0) {
            int size = items.size();
            StringBuilder b = new StringBuilder();
            long id;
            for (int i = 0; i < size; i++) {
                id = ((KrnObject) items.get(i).getCurrent()).id;
                TreePath path = getPathByNodeId(id);
                if (path != null) {
                    b.append(id).append(",");
                }
            }
            return b.length() > 0 ? b.substring(0, b.length() - 1) : "";
        } else {
            return super.getSelectedRowsString();
        }
    }
    
    class RowSorter implements Comparator<JsonObject> {

        private String sortOrder;
		private String sortCol;

		public RowSorter(String sortOrder, String sortCol) {
			super();
			this.sortOrder = sortOrder;
			this.sortCol = sortCol;
		}

		public int compare(JsonObject a, JsonObject b) {
        	boolean isFolder1 = 1 == a.get("fd").asInt();
        	boolean isFolder2 = 1 == b.get("fd").asInt();
        	if (isFolder1 && !isFolder2) return -1;
        	else if (!isFolder1 && isFolder2) return 1;

        	if (sortCol == null || sortOrder == null) return 0;
        	
        	String val1 = a.get(sortCol + "-title") != null ? a.get(sortCol + "-title").asString() : a.get(sortCol).asString();
        	String val2 = b.get(sortCol + "-title") != null ? b.get(sortCol + "-title").asString() : b.get(sortCol).asString();
        	
        	int res = 0;
        	if (Funcs.isEmpty(val1) && Funcs.isEmpty(val2)) return 0;
        	else if (!Funcs.isEmpty(val1) && Funcs.isEmpty(val2)) res = 1;
        	else if (Funcs.isEmpty(val1) && !Funcs.isEmpty(val2)) res = -1;
        	else {
        		try {
        			Date d1 = ThreadLocalDateFormat.dd_MM_yyyy.parse(val1);
        			Date d2 = ThreadLocalDateFormat.dd_MM_yyyy.parse(val2);
        			res = d1.compareTo(d2);
        		} catch (Exception e) {
            		res = val1.compareTo(val2);
        		}
        	}
        	
        	res = ("asc".equals(sortOrder)) ? res : -res;
        	return res;
        }
    }

	public int getChildrenSize() {
		return childrenSize;
	}
}