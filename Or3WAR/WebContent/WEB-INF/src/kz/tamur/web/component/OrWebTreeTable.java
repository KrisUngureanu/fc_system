package kz.tamur.web.component;

import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeTablePropertyRoot;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComboColumnAdapter;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeAdapter.Node;
import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.web.common.webgui.WebComponent;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.06.2007
 * Time: 15:58:26
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTreeTable extends OrWebTable implements OrTreeTableComponent, JSONComponent {
    public static PropertyNode TREE_TABLE_PROPS = new TreeTablePropertyRoot();
    private String treeName;
    private String treeNameUID;

    private int treeWidth;
    private OrWebTree tree;
    private TreeAdapter treeAdapter;
    private static final int PADDING = 11;

    OrWebTreeTable(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
        super(xml, mode, fm, frame, id);
        
        try {
	        getTree();
	        tree.setRootVisible(false);
	        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
	        if (mode == Mode.RUNTIME) {
	            this.treeAdapter = tree.getAdapter(false, true);
	            this.treeAdapter.setComponentLangId(adapter.getComponentLangId());
	            PropertyValue pv = getPropertyValue(TREE_TABLE_PROPS.getChild("ref").getChild("treeFilter"));
	            if (!pv.isNull()) {
	                treeAdapter.setDefaultFilterId(pv.filterValue().getObjId());
	            }
	
	            if (treeAdapter.rootRef != null) {
	                treeAdapter.rootRef.removeOrRefListener(treeAdapter);
	                treeAdapter.rootRef.addOrRefListener(adapter);
	            }
	
	            if (treeAdapter.rootCalcRef != null) {
	                treeAdapter.rootCalcRef.removeOrRefListener(treeAdapter);
	                treeAdapter.rootCalcRef.addOrRefListener(adapter);
	            }
	
	            treeAdapter.setAccess(adapter.getAccess());
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

    public OrWebTreeTableColumn createTreeTableColumn() {
        return new OrWebTreeTableColumn(xml, mode, frame, id);
    }

    protected void init() {
        PropertyValue pv = getPropertyValue(TREE_TABLE_PROPS.getChild("treeTitle"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            treeNameUID = (String) p.first;
            treeName = frame.getString(treeNameUID);
        } else {
            treeName = "Дерево";
            try {
                KrnObject lang = frame.getInterfaceLang();
                if (lang != null) {
                    LangHelper.WebLangItem li = LangHelper
                            .getLangById(lang.id, ((WebFrame) frame).getSession().getConfigNumber());
                    if ("KZ".equals(li.code))
                        treeName = "А\u0493аш";
                }
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        pv = getPropertyValue(TREE_TABLE_PROPS.getChild("pos").getChild("treeWidth"));
        if (!pv.isNull() && pv.intValue() != 0) {
            treeWidth = pv.intValue();
        } else {
            treeWidth = Constants.DEFAULT_PREF_WIDTH;
        }
    }

    protected WebTableModel createTableModel() throws KrnException {
        if (mode == Mode.RUNTIME) {
            adapter = new TreeTableAdapter(frame, this, false);
            return new RtWebTreeTableModel((TreeTableAdapter) adapter);
        }
        return null;
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public PropertyNode getProperties() {
        return TREE_TABLE_PROPS;
    }

    public void setLangId(long langId) {
        treeName = frame.getString(treeNameUID);
        if ("Безымянный".equals(treeName)) {
            treeName = "Дерево";
            try {
                LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame) frame).getSession().getConfigNumber());
                if ("KZ".equals(li.code))
                    treeName = "А\u0493аш";
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        if (tree != null)
            tree.setLangId(langId);
        super.setLangId(langId);
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

        public int getComponentStatus() {
            return 0;
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

    public OrTreeComponent getTree() {
        if (tree == null) {
            if (mode == Mode.RUNTIME) {
                try {
                    tree = new WebTreeTableCellRenderer(xml, mode, frame, false, id);
                    tree.setTableAdapter((TreeTableAdapter) adapter);
                } catch (KrnException e) {
                	log.error(e, e);
                }
            }
        }
        return tree;
    }

    public class WebTreeTableCellRenderer extends OrWebTree {
        protected WebTreeTableCellRenderer(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
            super(xml, mode, frame, isEditor, id);
            this.xml = null;
        }
    }

    public String getTreeName() {
        return treeName;
    }

    public int getTreeWidth() {
        return treeWidth;
    }

    public TreeAdapter getTreeAdapter() {
        return treeAdapter;
    }

    public void deleteRow() {
        TreeTableAdapter a = (TreeTableAdapter) adapter;
        a.deleteRow();
        if (navi != null)
            navi.setRowCount(getRowCount());
        sendChangeProperty("deleteRow", "");
    }

    /*
     * @Override
     * public void setSelectedRow(int row) {
     * TreeAdapter treeAdapter = getTreeAdapter();
     * OrTreeComponent tree = getTree();
     * TreeAdapter.Node root = treeAdapter.getRoot();
     * TreePath path = (root != null) ? root.find(row) : null;
     * if (path == null)
     * path = root.find(row, false);
     * 
     * if (path != null) {
     * TreePath parentPath = path.getParentPath();
     * tree.expandPath(parentPath);
     * tree.setSelectionPath(path);
     * ((TreeTableAdapter)adapter).treeSelectionChanged();
     * 
     * int t = tree.getRowForPath(path);
     * super.setSelectedRow(t);
     * } else {
     * super.setSelectedRows(new int[0]);
     * }
     * }
     */
    public void setSelectedRow(int row) {
        OrTreeComponent tree = getTree();

        TreePath path = tree.getPathForRow(row);

        if (path != null) {
            tree.setSelectionPath(path);
            ((TreeTableAdapter) adapter).treeSelectionChanged();

            super.setSelectedRow(row);
        } else {
            super.setSelectedRows(new int[0]);
        }
    }

    public void setSelectedRows(int[] rows, boolean selfChange) {
        if (rows != null && rows.length > 0) {
            OrTreeComponent tree = getTree();
            TreePath path = tree.getPathForRow(rows[0]);

            if (path != null) {
                tree.setSelectionPath(path);
                ((TreeTableAdapter) adapter).treeSelectionChanged();
            }
        }
        super.setSelectedRows(rows, selfChange);
    }

    /*
     * @Override
     * public void tableRowsUpdated(int firstRow, int lastRow) {
     * RtWebTreeTableModel model = (RtWebTreeTableModel) getModel();
     * int row = model.getRowFromIndex(firstRow);
     * super.tableRowsUpdated(row, row);
     * }
     * 
     * public void tableRowsDeleted(int firstRow, int lastRow) {
     * int count = lastRow - firstRow + 1;
     * deletedRows = new int[count];
     * for (int i = 0; i < count; i++) {
     * deletedRows[i] = Integer.parseInt(model.getRowId(firstRow + i));
     * }
     * }
     */

    public JsonObject getRowHTML(int row, String zebra1, String zebra2) {
        JsonObject rowJSON = new JsonObject();
        String zebra = row % 2 == 0 ? zebra1 : zebra2;
        JsonObject style = new JsonObject();
        if (isRowSelected(row)) {
            rowJSON.add("class", "selected");
        } else {
            rowJSON.add("class", "notselected");
            if (zebra != null) {
                style.add("backgroundColor", zebra);
                rowJSON.add("style", style);
            }
        }
        rowJSON.add("id", row);

        JsonObject[] columns = new JsonObject[model.getColumnCount()];

        for (int j = 0; j < model.getColumnCount(); j++) {
            JsonObject column = new JsonObject();
            Object value = model.getValueAt(row, j);
            if (renderer != null && !(value instanceof TreeModel)) {
                renderer.getTableCellRendererString(this, value, false, false, row, j, column);
            } else if (value instanceof TreeModel) {
                TreePath path = tree.getPathForRow(row);
                if (path != null) {
                    TreeAdapter.Node node = (TreeAdapter.Node) path.getLastPathComponent();
                    if (j == 0) {
                        int padding = (path.getPathCount() - 2) * PADDING;
                        JsonObject style_ = new JsonObject();
                        style_.add("paddingLeft", padding);
                        column.add("style", style_);
                        if (((TreeTableAdapter) adapter).isHasRows() && node.getObject() != null) {
                            column.add("colspan", model.getColumnCount());
                        }

                        if (node.getObject() != null) {
                            column.add("node", tree.treeTableNodeToHTML(node));
                        }
                    }
                }
            } else {
                if (value != null) {
                    column.add("value", value);
                }
            }
            columns[j] = column;
        }

        rowJSON.add("columns", columns);
        return rowJSON;
    }

    public void setValue(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        int[] rows = new int[st.countTokens()];
        for (int i = 0; i < rows.length; i++) {
            String token = st.nextToken();
            rows[i] = Integer.parseInt(token);
        }
        setSelectedRows(rows);
        adapter.setSelectedRows(getSelectedRows());
    }

    public void selectNode(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        long[] nids = new long[st.countTokens()];
        int[] rows = new int[nids.length];
        		
        for (int i = 0; i < nids.length; i++) {
            String token = st.nextToken();
            int ind = token.indexOf('_');
            if (ind > -1) {
	            nids[i] = Long.parseLong(token.substring(0, ind));
	            TreePath path = getPathByNodeId(nids[i]);
	            if (path != null) {
	            	Node parent = (Node) path.getLastPathComponent();
	            	Node child = (Node) parent.getChildAt(Integer.parseInt(token.substring(ind + 1)));

	                rows[i] = tree.getRowForPath(new TreePath(child.getPath()));
	            }
                //rows[i] = Integer.parseInt(token.substring(1));
            } else {
	            nids[i] = Long.parseLong(token);
	            TreePath path = getPathByNodeId(nids[i]);
	            if (path != null) {
	                rows[i] = tree.getRowForPath(path);
	            }
            }
        }
        adapter.setSelectedRows(rows);
        setSelectedRows(rows);
    }

/*    public void expand(String nid) {
        TreePath path = getPathByNodeId(nid);
        if (path != null) {
            int row = tree.getRowForPath(path);
            if (tree.isExpanded(path)) {
                int count = tree.getVisibleRowCount((DefaultMutableTreeNode) path.getLastPathComponent()) - 1;
                if (count > 0)
                    tableRowsDeleted(row + 1, row + count);
            }
            tree.changeState(path);
            super.tableRowsUpdated(row, row);
            if (tree.isExpanded(path)) {
                int count = tree.getVisibleRowCount((DefaultMutableTreeNode) path.getLastPathComponent()) - 1;
                if (count > 0)
                    super.tableRowsInserted(row + 1, row + count);
            }
            adapter.countCurrentTableItem();
            if (navi != null)
                navi.setRowCount(getRowCount());
        }
        dataChanged(zebra1, zebra2);
    }
*/    
    public TreePath getPathByNodeId(long nid) {
        TreePath path = null;
        if (treeAdapter.getRoot() != null) {
            path = treeAdapter.getRoot().find(nid, true);
            if (path == null)
                path = treeAdapter.getRoot().find(nid, false);
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
            TreeAdapter.Node n = (TreeAdapter.Node) path.getLastPathComponent();
            adapter.getRef().absolute(n.index, this);
            if (n.index > -1)
                adapter.getRef().setSelectedItems(new int[] { n.index });
        }
    }

    public int addRow() {
        int i = adapter.getRef().getItems(0).size();
        int r = adapter.addNewRow();
        if (TreeTableAdapter.NEED_TITLE == r)
            return r;
        ((TreeTableAdapter) adapter).afterAddRow(r, i);
        sendChangeProperty("addRow", "");
        if (navi != null)
            navi.setRowCount(getRowCount());
        return r;
    }

    public void addRowNeedTitle(String title) {
        if (title != null && title.length() > 0) {
            int i = adapter.getRef().getItems(0).size();
            int row = ((TreeTableAdapter) adapter).addNewNode(title);
            ((TreeTableAdapter) adapter).afterAddRow(row, i);
        }
        if (navi != null)
            navi.setRowCount(getRowCount());
        sendChangeProperty("addRow", title);
    }

    public void getErrorXML(StringBuilder b) {
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("table", isNaviExists ? getJSON(navi, zebra1, zebra2) : getJSON(null, zebra1, zebra2));
        if (isNaviExists && addPan != null) {
            property.add("panel", addPan.getJSON());
        }
        RtWebTableModel m = (RtWebTableModel) getModel();
        JsonArray dialogs = new JsonArray();
        for (int i = 0; i < m.getColumnCount(); i++) {
            if (m.getColumn(i) instanceof OrWebTreeColumn) {
                OrWebTreeColumn c = (OrWebTreeColumn) m.getColumn(i);
                OrWebTreeField tf = (OrWebTreeField) c.getEditor();
                JsonObject obj_ = new JsonObject();
                obj_.add("index", i);
                obj_.add("title", c.getTitle());
                obj_.add("width", Utils.mergeWidth(tf.getDialogWidth()));
                obj_.add("height", Utils.mergeHeight(tf.getDialogHeight()));
                obj_.add("title", c.getTitle());
                dialogs.add(obj_);
            }
        }
        property.add("dialogs", dialogs);
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        removeChange("pr.updateRow");
        removeChange("pr.reloadRow");
        removeChange("pr.reloadTreeTable");

        return obj;
    }

    public JsonObject getJSON(OrWebTableNavigator navi, String zebra1, String zebra2) {
        JsonObject table = new JsonObject();
        int width = 0;
        for (int i = 0; i < model.getColumnCount(); i++) {
            width += Integer.parseInt(model.getColumnWidth(i));
        }

        if (navi != null) {
            navi.setRowCount(getRowCount());
            table.add("navigator", navi.putJSON(false));
        }

        if (zebra1 != null) {
            table.add("zebra1", zebra1);
        }

        if (zebra2 != null) {
            table.add("zebra2", zebra2);
        }

        String selRows = getSelectedRowsString();
        if (selRows != null) {
            table.add("selectedRows", selRows);
        }
        table.add("width", width);
        return table;
    }
    
    public String getData(String nid, String sortCol, String sortOrder) throws KrnException {
    	JsonArray arr = new JsonArray();

        MutableTreeNode mroot = (MutableTreeNode) tree.getModel().getRoot();
        if (mroot != null && mroot.getChildCount() > 0) {
            TreeAdapter.Node node = (TreeAdapter.Node) mroot.getChildAt(0);
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
		    		TreeTableAdapter adapter = getTreeTableAdapter();
		    		synchronized (adapter) {
			        	expand(node);
			        	
			        	if (parentId == 0) {
			        		JsonObject rowObj = getData(node, 0, sortCol, sortOrder);
			        		arr.add(rowObj);
			        	} else {
					    	for (int i = 0; i < node.getChildCount(); i++) {
					        	JsonObject rowObj = getData((Node)node.getChildAt(i), node.getObject().id, sortCol, sortOrder);
					        	arr.add(rowObj);
					    	}
					    	//arr = sort(arr, sortCol, sortOrder);
			        	}
					}
		    	}
	    	}
        }
        return arr.toString();
    }

    private TreeTableAdapter getTreeTableAdapter() {
        return (TreeTableAdapter) adapter;
    }

    public void expand(Node node) {
        TreePath path = new TreePath(node.getPath());
        expand(path);
    }

    public void expand(String nid) {
        TreePath path = getPathByNodeId(nid);
        expand(path);
    }
    
    public void expand(TreePath path) {
        if (path != null) {
            if (!tree.isExpanded(path)) {
                int row = tree.getRowForPath(path);

                tree.changeState(path);
                super.tableRowsUpdated(row, row);

                if (tree.isExpanded(path)) {
                    int count = tree.getVisibleRowCount((DefaultMutableTreeNode) path.getLastPathComponent()) - 1;
                    if (count > 0)
                        super.tableRowsInserted(row + 1, row + count);
                }
            }

            adapter.countCurrentTableItem();
            if (navi != null) {
                navi.setRowCount(getRowCount());
            }
        }
    }

    public void collapse(Node node) {
        TreePath path = new TreePath(node.getPath());
        collapse(path);
    }

    public void collapse(String nid) {
        TreePath path = getPathByNodeId(nid);
        collapse(path);
    }

    public void collapse(TreePath path) {
        if (path != null) {
            if (tree.isExpanded(path)) {
                int row = tree.getRowForPath(path);
                int count = tree.getVisibleRowCount((DefaultMutableTreeNode) path.getLastPathComponent()) - 1;
                if (count > 0)
                    tableRowsDeleted(row + 1, row + count);

                tree.changeState(path);
                super.tableRowsUpdated(row, row);
            }

            adapter.countCurrentTableItem();
            if (navi != null) {
                navi.setRowCount(getRowCount());
            }
        }
    }
    
	public JsonObject getData(Node node, long parentId, String sortCol, String sortOrder) {
        JsonObject row = new JsonObject();
        TreePath path = new TreePath(node.getPath());
        int r = tree.getRowForPath(path);
        if (node.getObject() != null) {
            long id = node.getObject().id;
            String val = node.toString();
            // Надо чтоб загрузились дети
            node.isLeaf();
            row.add("id", id);
            row.add("fd", node.getChildCount() > 0 ? 1 : 0);
            row.add("name", val != null ? val : "");
            if (!tree.isExpanded(path))
                row.add("state", node.getAllowsChildren() ? "closed" : "open");
            else {
                JsonArray arr = new JsonArray();
                for (int i = 0; i < node.getChildCount(); i++) {
                    JsonObject rowObj = getData((Node) node.getChildAt(i), id, sortCol, sortOrder);
                    arr.add(rowObj);
                }
		    	//arr = sort(arr, sortCol, sortOrder);
                row.add("children", arr);
            }
            row.add("parent", parentId);
            if (tree.isFolderAsLeaf()) {
                row.add("iconCls", "tree-file");
            }
        } else {
            row.add("id", parentId + "_" + node.getParent().getIndex(node));
            row.add("fd", 0);
            row.add("name", "");
            row.add("state", "open");
            row.add("parent", parentId);
        }
        for (int j = 1; j < model.getColumnCount(); j++) {
            OrColumnComponent col = ((kz.tamur.or3.client.comps.interfaces.OrTableModel) model).getColumn(j);
            Object value = model.getValueAt(r, j);
            
            if (value instanceof TreeModel) {
            	value = "";
            } else if (col instanceof OrWebDateColumn) {
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

}
