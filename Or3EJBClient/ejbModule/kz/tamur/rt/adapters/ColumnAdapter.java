package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.data.CashChangeListener;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.Record;
import kz.tamur.rt.Utils;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class ColumnAdapter extends ComponentAdapter implements CashChangeListener {

    protected int index;
    protected int rowIndex;
    protected TableAdapter tableAdapter;
    protected OrTableColumn column;
    protected int summaryType = Constants.SUMMARY_NO;
    protected boolean defSummary = false;
    OrCellEditor celleditor;
    TableCellRenderer renderer;
    protected boolean enabled;
    protected int unique;

    private OrCalcRef columnTitleRef;
    private OrCalcRef columnBackColorRef;
    private OrCalcRef columnFontColorRef;
    private Color columnFontColor = null;
    private Color columnBackgroundColor = null;
    private boolean sort;
    private KrnAttribute[] attrs;
    private String treePath;
    private KrnAttribute[] treeAttrs;
    private Map<Long, Object> objToValue;
    private Map<Long, Long> objToParent;
    private int direction;
    private int sortingIndex;
    private int maxWidth;
    private int minWidth;
    private int prefWidth;
    private Font columnFont;

    public ColumnAdapter(UIFrame frame, OrTableColumn col, boolean isEditor) throws KrnException {
        super(frame, col, isEditor);
        this.column = col;
        maxWidth = column.getMaxWidthOfProp();
        minWidth = column.getMinWidthOfProp();
        prefWidth = column.getPreferredWidthOfProp();
        PropertyNode proot = col.getProperties();
        if (!(col instanceof OrCheckColumn || col instanceof OrImageColumn)) {
            PropertyValue pv = col.getPropertyValue(
                    proot.getChild("view").getChild("summary"));
            if (!pv.isNull()) {
                summaryType = pv.intValue();
            }
        } else if (col instanceof OrCheckColumn) {
            PropertyValue pv = col.getPropertyValue(
                    proot.getChild("view").getChild("defSummary"));
            if (!pv.isNull()) {
                defSummary = pv.booleanValue();
                if (defSummary) {
                    summaryType = Constants.SUMMARY_TRUE_COUNT;
                }
            }
        }
        PropertyNode pn = proot.getChild("constraints");
        if (pn != null) {
            PropertyValue pv = col.getPropertyValue(pn.getChild("unique"));
            if (pv != null) {
                unique = pv.intValue();
            }
        }
        if (!(col instanceof OrImageColumn)) {
            pn = proot.getChild("view");
            PropertyNode pn1 = pn.getChild("font").getChild("fontColor");
            if (pn1 != null) {
                PropertyValue pv = col.getPropertyValue(pn1);
                if (!pv.isNull()) {
                    columnFontColor = pv.colorValue();
                }
            }
            pn1 = pn.getChild("background");
            if (pn1 != null) {
                PropertyNode pn2 = pn1.getChild("backgroundColor");
                if (pn2 != null) {
                    PropertyValue pv = col.getPropertyValue(pn2);
                    if (!pv.isNull()) {
                        columnBackgroundColor = pv.colorValue();
                    } else {
                        columnBackgroundColor = null;
                    }
                }
            }}
        pn = proot.getChild("header");
        if (pn != null) {
            PropertyValue pv = col.getPropertyValue(pn.getChild("sorted"));
            if (pv != null) {
                sort = pv.booleanValue();
            } else {
                sort = false;
            }
            pv = col.getPropertyValue(pn.getChild("sortingDirection"));
            if (pv != null) {
                direction = pv.intValue();
            } else {
            	direction = Constants.SORT_ASCENDING;
            }
            pv = col.getPropertyValue(pn.getChild("sortingIndex"));
            if (pv != null) {
                sortingIndex = pv.intValue();
            } else {
            	sortingIndex = 0;
            }
        }

        setEnabled(checkEnabled());
        createTitleRef(column);
        createBackColorRef(column);
        createFontColorRef(column);

        if (getRef() != null) {
            String path = getRef().toString();
            if (path != null && path.length() > 0) {
                attrs = com.cifs.or2.client.Utils.getAttributesForPath(path);
                if (attrs != null && attrs.length > 0)
                    getRef().getCash().addCashChangeListener(attrs[attrs.length - 1].id, this);
            }
        }
        if (!(col instanceof OrImageColumn)) {
        PropertyValue pv = col.getPropertyValue(proot.getChild("ref").getChild("treeDataRef"));
        if (!pv.isNull()) {
            treePath = pv.stringValue();
            if (treePath != null && treePath.length() > 0) {
                treeAttrs = Utils.getAttributesForPath(treePath);
                if (treeAttrs != null && treeAttrs.length > 0) {
                    objToParent = new HashMap<Long, Long>();
                    objToValue = new HashMap<Long, Object>();
                    for (int i=0; i<treeAttrs.length; i++) {
                        getRef().getCash().addCashChangeListener(treeAttrs[i].id, this);
                    }
                }
            }
        }
            if (!(col instanceof OrImageColumn)) {
                pv = col.getPropertyValue(proot.getChild("view").getChild("font").getChild("fontG"));
                if (!pv.isNull() && pv.fontValue() != null) {
                    columnFont = pv.fontValue();
                }
            }
        }
    }


    public void setTableAdapter(TableAdapter a) {
        tableAdapter = a;
        OrRef tableRef = tableAdapter.getRef();
        OrRef ref = dataRef;
        while (ref != null && ref != tableRef) {
            ref.setColumn(true);
            ref = ref.getParent();
        }
        if (columnBackColorRef != null) {
            columnBackColorRef.setTableRef(tableAdapter.getRef());
        }
        if (columnFontColorRef != null) {
            columnFontColorRef.setTableRef(tableAdapter.getRef());
        }
        if (calcRef != null) {
            calcRef.setTableRef(tableAdapter.getRef());
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public OrCellEditor getCellEditor() {
        return celleditor;
    }

    public TableCellRenderer getCellRenderer() {
        return renderer;
    }

    public Object getObjectValueAt(int i) {
        if (dataRef != null) {
            List<Item> items = dataRef.getItems(0);
            OrRef.Item item = (i < items.size()) ? items.get(i) : null;
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        } else if (calcRef != null) {
            Item item = calcRef.getItem(0, i);
            if (item != null && !item.isDeleted) {
                return item.getCurrent();
            }
        }
        return null;
    }

    public Object getValueAt(int i) {
        if (celleditor != null) {
            if (dataRef != null) {
            	List<Item> items = dataRef.getItems(0);
                Item item = (i < items.size()) ? items.get(i) : null;
                if (item != null && !item.isDeleted) {
                    return celleditor.getValueFor(item);
                }
            } else if (calcRef != null) {
                Item item = calcRef.getItem(0, i);
                if (item != null && !item.isDeleted) {
                    if (celleditor != null) {
                    return celleditor.getValueFor(item);}
                }
            }
        } else
            return getObjectValueAt(i);
        return null;
    }

    public int indexOf(Object value) {
        if (dataRef != null) {
            List<Item> items = dataRef.getItems(0);
            int sz = items.size();
            for (int i = 0; i < sz; i++) {
            	Item item = items.get(i);
            	if (value.equals(item.getCurrent())) {
            		return i;
            	}
            }
        }
        return -1;
    }

    public Object getValueForNode(KrnObject obj) {
        try {
            if (treeAttrs != null && treeAttrs.length > 0) {
                Object val = objToValue.get(obj.id);
                if (val == null) {
                    KrnObject object = obj;
                    final Cache cash = getRef().getCash();
                    Record rec = null;
                    for (int i = 0; i < treeAttrs.length - 1; i++) {
                        long[] ids = {object.id};
                        SortedSet<Record> recs = cash.getRecords(ids, treeAttrs[i], 0, null);
                        if (recs.size() > 0) {
                            rec = recs.last();
                            KrnObject value = (KrnObject) rec.getValue();
                            objToParent.put(value.id, object.id);
                            object = value;
                        } else {
                            return null;
                        }
                    }

                    long lid = (treeAttrs[treeAttrs.length - 1].isMultilingual
                                && langId == 0) ? frame.getDataLang().id : langId;
                    long[] ids = {object.id};
                    SortedSet<Record> recs = cash.getRecords(ids, treeAttrs[treeAttrs.length - 1], lid,
                        null);
                    if (recs.size() > 0) {
                        rec = recs.last();
                        objToValue.put(obj.id, rec.getValue());
                        return rec.getValue();
                    } else {
                        return null;
                    }
                } else {
                    return val;
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasTreeAttrs() {
        return (treeAttrs != null && treeAttrs.length > 0);
    }

    public OrTableColumn getColumn() {
        return column;
    }

    protected void createDataRef(OrGuiComponent c) throws KrnException {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("data");
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull()) {
            dataRef = OrRef.createRef(
                    pv.stringValue(), true, Mode.RUNTIME, frame.getRefs(),
                    frame.getTransactionIsolation(), frame);
            dataRef.addOrRefListener(this);
            dataRef.addCheckContext(this);
        }
    }

    protected void createEvalRef(OrGuiComponent c) {
        super.createEvalRef(c);
        if (calcRef != null) {
            calcRef.setColumn(true);
        }
    }
    
    protected void createTitleRef(OrGuiComponent c) {
        Object fx = column.getPropertyValue(column.getProperties().getChild("header").getChild("editor")).objectValue();
        if (fx instanceof Expression) {
            try {
                propertyName = "Свойство: текст заголовка (формула)";
                columnTitleRef = new OrCalcRef(((Expression)fx).text, false, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                columnTitleRef.addOrRefListener(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void createBackColorRef(OrGuiComponent c) {
        PropertyNode prop = column.getProperties();
        PropertyNode pn = prop.getChild("view").getChild("background");
        String fx = null;
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("backgroundColorExpr");
            if (pn1 != null) {
                PropertyValue pv = column.getPropertyValue(pn1);
                if (!pv.isNull()) {
                    fx = pv.stringValue();
                }
            }
        }
        if (fx != null && !"".equals(fx)) {
            try {
                propertyName = "Свойство: Цвет фона колонки";
                if (fx.trim().length() > 0) {
                    columnBackColorRef = new OrCalcRef(fx, true, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, c, propertyName, this);
                    columnBackColorRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected void createFontColorRef(OrGuiComponent c) {
        String fx = null;
        PropertyNode prop = column.getProperties();
        PropertyNode pn = prop.getChild("view").getChild("font");
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("fontExpr");
            if (pn1 != null) {
                PropertyValue pv = column.getPropertyValue(pn1);
                if (!pv.isNull()) {
                    fx = pv.stringValue();
                }
            }
        }
        if (fx != null && !"".equals(fx)) {
            try {
                propertyName = "Свойство: Цвет шрифта колонки";
                if (fx.trim().length() > 0) {
                    columnFontColorRef = new OrCalcRef(fx, true, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, c, propertyName, this);
                    columnFontColorRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        OrRef ref = e.getRef();
        if (ref == columnTitleRef) {
        	System.out.println("@COLUMN_TITLE");
        	column.setHeaderTitle((String)columnTitleRef.getValue(langId));
        } else {
	        int i = e.getRef().getIndex();
	        if (i > -1 && tableAdapter instanceof TreeTableAdapter) {
	            TreeTableAdapter.RtTreeTableModel model =
	                    (TreeTableAdapter.RtTreeTableModel)tableAdapter.getModel();
	            int row = model.getRowFromIndex(i);
	            if (row > -1)
	                model.fireTableRowsUpdated(row, row);
	        } else if (i > -1 && tableAdapter != null) {
	            tableAdapter.getModel().fireTableCellUpdated(i, index);
	            rowIndex = e.getIndex();
	        }
        }
    }

    public Color getColumnDefaultForegroundColor(int col) {
        if (col == index) {
            return getFontColor(0);
        }
        return Color.black;
    }

    public String getColumnTitle(int columnIdx) {
        if (columnIdx == index) {
            OrRef.Item item = columnBackColorRef.getItem();
            return (String)((item != null) ? item.getCurrent() : null);
        }
        return null;
    }

    public Color getColumnBackgroundColor(int row, int columnIdx) {
        if (columnIdx == index) {
            OrRef.Item item = columnBackColorRef.getItem(0, row);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return Utils.getColorByName(o.toString());
            }
        }
        return Color.white;
    }

    public Color getColumnFontColor(int row, int columnIdx) {
        if (columnIdx == index) {
            OrRef.Item item = columnFontColorRef.getItem(getLangId(), row);
            if (item == null) {
                item = columnFontColorRef.getItem(0, row);
            }
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return Utils.getColorByName(o.toString());
            }
        }
        return Color.black;
    }

    public Font getColumnFont(int row, int columnIdx) {
        if (tableAdapter.rowFontRef != null && columnIdx == index) {
        	OrRef.Item item = tableAdapter.rowFontRef.getItem(0, row);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Font) {
                return (Font)o;
            }
        }
        return null;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setFocus(int index,OrRefEvent e) {
    	tableAdapter.setFocus(0, e);
        int row = index;
        int col = this.index;
        
        if (row > -1 && tableAdapter instanceof TreeTableAdapter) {
        	TreeAdapter treeAdapter = ((OrTreeTable)tableAdapter.getTable()).getTreeAdapter();
        	treeAdapter.getTree();
            TreeAdapter.Node root = treeAdapter.getRoot();
        	TreePath p = root.find(index);
        	if (p != null) treeAdapter.getTree().expandPath(p.getParentPath());

            TreeTableAdapter.RtTreeTableModel model =
                    (TreeTableAdapter.RtTreeTableModel)tableAdapter.getModel();
            tableAdapter.scrollToVisible(row, col-1);
            row = model.getRowFromIndex(row);
            
            if (row > -1 && tableAdapter.getTable().getJTable().getRowCount()>row) {
                tableAdapter.getTable().getJTable().clearSelection();
                tableAdapter.getTable().getJTable().setRowSelectionInterval(row, row);
                tableAdapter.getTable().getJTable().setColumnSelectionInterval(col, col);
                tableAdapter.getTable().getJTable().editCellAt(row, col);
            }
        } else if (row > -1 && tableAdapter != null) {
            tableAdapter.scrollToVisible(row, col);
            if (row > -1 && tableAdapter.getTable().getJTable().getRowCount()>row) {
                tableAdapter.getTable().getJTable().clearSelection();
                tableAdapter.getTable().getJTable().setRowSelectionInterval(row, row);
                tableAdapter.getTable().getJTable().setColumnSelectionInterval(col, col);
                tableAdapter.getTable().getJTable().editCellAt(row, col);
            }
        }
    }

    public void clear() {
    }

    public int count() {
        OrRef ref = dataRef;
        if (ref == null) {
            if (this instanceof PopupColumnAdapter) {
                ref = ((PopupColumnAdapter)this).getTitleRef();
            } else if (this instanceof HyperColumnAdapter) {
                ref = ((HyperColumnAdapter)this).getDynamicInterfaceRef();
            }
            if (ref == null && tableAdapter != null) {
                ref = tableAdapter.getRef();
            }
        }
        if (ref != null) {
            List<Integer> indexes = null;
            if (tableAdapter instanceof TreeTableAdapter) {
                TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
                TreeNode root = (TreeNode)model.getRoot();
                indexes = new ArrayList<Integer>();
                indexes = indexes(root, indexes);
            }

            List<Item> l = ref.getItems(ref.getLangId());
            int res = 0;
            for (int i = 0; i < l.size(); i++) {
                if (indexes == null || indexes.contains(i)) {
	                OrRef.Item item = l.get(i);
	                Object o = item.getCurrent();
	                if (o != null && o.toString().length() > 0) {
	                    res++;
	                }
                }
            }
            return res;
        }
        return 0;
    }

    private List<Integer> indexes(TreeNode node, List<Integer> res) {
        if (node instanceof  TreeAdapter.Node && ((TreeAdapter.Node)node).index > -1)
        	res.add(((TreeAdapter.Node)node).index);
        else {
        	int cnt = node.getChildCount();
            for (int i = 0; i < cnt; i++) {
                indexes(node.getChildAt(i), res);
            }
        }
        return res;
    }

    public int trueCount() {
        List<Integer> indexes = null;
        if (tableAdapter instanceof TreeTableAdapter) {
            TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
            TreeNode root = (TreeNode)model.getRoot();
            indexes = new ArrayList<Integer>();
            indexes = indexes(root, indexes);
        }

        List<Item> l = dataRef.getItems(dataRef.getLangId());
        int res = 0;
        for (int i = 0; i < l.size(); i++) {
            if (indexes == null || indexes.contains(i)) {
	            OrRef.Item item = l.get(i);
	            Object o = item.getCurrent();
	            if (o != null && ((Number)o).longValue() > 0) {
	                res++;
	            }
            }
        }
        return res;
    }

    public long sumInt() {
        List<Integer> indexes = null;
        if (tableAdapter instanceof TreeTableAdapter) {
            TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
            TreeNode root = (TreeNode)model.getRoot();
            indexes = new ArrayList<Integer>();
            indexes = indexes(root, indexes);
        }

        List<Item> l = dataRef.getItems(dataRef.getLangId());
        long res = 0;
        for (int i = 0; i < l.size(); i++) {
            if (indexes == null || indexes.contains(i)) {
	            OrRef.Item item = l.get(i);
	            Object o = item.getCurrent();
	            if (o != null) {
	                res = res + ((Number)o).longValue();
	            }
            }
    	}
        return res;
    }

    public double sumFloat() {
        List<Integer> indexes = null;
        if (tableAdapter instanceof TreeTableAdapter) {
            TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
            TreeNode root = (TreeNode)model.getRoot();
            indexes = new ArrayList<Integer>();
            indexes = indexes(root, indexes);
        }

        List<Item> l = dataRef.getItems(dataRef.getLangId());
        double res = 0.0;
        for (int i = 0; i < l.size(); i++) {
            if (indexes == null || indexes.contains(i)) {
	            OrRef.Item item = l.get(i);
	            Object o = item.getCurrent();
	            if (o != null) {
	                res = res + ((Number)o).doubleValue();
	            }
            }
        }
        return res;
    }

    public double average() {
        List<Integer> indexes = null;
        if (tableAdapter instanceof TreeTableAdapter) {
            TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
            TreeNode root = (TreeNode)model.getRoot();
            indexes = new ArrayList<Integer>();
            indexes = indexes(root, indexes);
        }

        List<Item> l = dataRef.getItems(dataRef.getLangId());
        double res = 0.0;
        int count = 0;
        for (int i = 0; i < l.size(); i++) {
            if (indexes == null || indexes.contains(i)) {
	            OrRef.Item item = l.get(i);
	            Object o = item.getCurrent();
	            if (o != null) {
	                res = res + ((Number)o).doubleValue();
	                count++;
	            }
            }
        }
        res = res / count;
        return res;
    }

    protected Number maxMin(boolean isMin) {
        List<Integer> indexes = null;
        if (tableAdapter instanceof TreeTableAdapter) {
            TreeModel model = ((OrTreeTable)tableAdapter.getTable()).getTree().getModel();
            TreeNode root = (TreeNode)model.getRoot();
            indexes = indexes(root, indexes);
        }

        List<Item> l = dataRef.getItems(dataRef.getLangId());
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < l.size(); i++) {
            if (indexes == null || indexes.contains(i)) {
	            OrRef.Item item = l.get(i);
	            Object o = item.getCurrent();
	            if (o != null) {
	                list.add(o);
	            }
            }
        }
        Collections.sort(list, new MinMaxCompare(this, isMin));
        Number res = (Number)list.get(0);
        return res;
    }

    public int maxMinInt(boolean isMin) {
        return maxMin(isMin).intValue();
    }

    public double maxMinFloat(boolean isMin) {
        return maxMin(isMin).doubleValue();
    }

    public void setCellRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }

    public int getUniqueIndex() {
        return unique;
    }

    public void objectChanged(Object src, long objId, long attrId) {
        if (attrId == attrs[attrs.length - 1].id) {
            int i = getRef().getParent().getIndexForObjId(objId);
            if (i > -1 && tableAdapter instanceof TreeTableAdapter) {
                TreeTableAdapter.RtTreeTableModel model =
                        (TreeTableAdapter.RtTreeTableModel)tableAdapter.getModel();
                int row = model.getRowFromIndex(i);
                if (row > -1)
                    model.fireTableRowsUpdated(row, row);
                OrTableFooter f = tableAdapter.getTable().getFooter();
                if (f != null)
                    f.refresh();
            } else if (i > -1 && tableAdapter != null) {
                tableAdapter.getModel().fireTableCellUpdated(i, index);
                OrTableFooter f = tableAdapter.getTable().getFooter();
                if (f != null)
                    f.refresh();
            }
        } else if (treeAttrs != null) {
            if (attrId == treeAttrs[treeAttrs.length - 1].id) {
                Object obj = objToParent.get(objId);
                while (obj != null) {
                    objId = (Long)obj;
                    obj = objToParent.get(objId);
                }
                objToValue.remove(objId);
                if (tableAdapter instanceof TreeTableAdapter) {
                    TreeTableAdapter.RtTreeTableModel model =
                            (TreeTableAdapter.RtTreeTableModel)tableAdapter.getModel();
                    int row = model.getRowForObjectId(objId);
                    if (row > -1) model.fireTableRowsUpdated(row, row);
                }
            }
        }
    }

    public void objectDeleted(Cache cache, long classId, long objId) {
    }

    public void objectCreated(Cache cache, long classId, long objId) {
    }

    class MinMaxCompare implements Comparator {

        private ColumnAdapter columnAdapter;
        private boolean isSortAsc;

        public MinMaxCompare(ColumnAdapter columnAdapter, boolean sortAsc) {
            this.columnAdapter = columnAdapter;
            isSortAsc = sortAsc;
        }

        public int compare(Object o1, Object o2) {
            int res = 0;
            if (o1 == o2) {
                res = 0;
            } else if (o1 != null && o2 == null) {
                res = 1;
            } else if (o1 == null && o2 != null) {
                res = -1;
            } else {
                Number num1 = (Number)o1;
                Number num2 = (Number)o2;
                if (columnAdapter instanceof IntColumnAdapter) {
                    Integer int1 = new Integer(num1.intValue());
                    Integer int2 = new Integer(num2.intValue());
                    res = int1.compareTo(int2);
                } else if (columnAdapter instanceof FloatColumnAdapter) {
                    Double doub1 = new Double(num1.doubleValue());
                    Double doub2 = new Double(num2.doubleValue());
                    res = doub1.compareTo(doub2);
                }
            }
            if (!isSortAsc) {
                res = -res;
            }
            return res;
        }

        public boolean equals(Object o) {
            if (o instanceof MinMaxCompare) {
                MinMaxCompare comparator = (MinMaxCompare)o;
                return (columnAdapter == comparator.columnAdapter) &&
                        (isSortAsc == comparator.isSortAsc);
            } else {
                return false;
            }
        }
    }

    public int getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(int summaryType) {
        this.summaryType = summaryType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        enabled = isEnabled;
    }

    public boolean isDefSummary() {
        return defSummary;
    }

    public void setDefSummary(boolean defSummary) {
        this.defSummary = defSummary;
    }


    public boolean isBackColorCalculated() {
        return columnBackColorRef != null;
    }

    public boolean isFontColorCalculated() {
        return columnFontColorRef != null;
    }

    public boolean isFontColorSet() {
        return columnFontColor != null;
    }

    public boolean isBackgroundColorSet() {
        return (columnBackgroundColor != null &&
                !columnBackgroundColor.equals(Color.white));
    }

    public Color getColumnFontColor() {
        return columnFontColor;
    }

    public Color getColumnBackgroundColor() {
        return columnBackgroundColor;
    }

    public boolean isSort() {
        return sort;
    }
    
    public int getSortingDirection() {
    	return direction;
    }

    public int getSortingIndex() {
    	return sortingIndex;
    }
    
    public Class getColumnClass() {
    	return Object.class;
    }
    
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            column.setMaxWidth(maxWidth);
            column.setMinWidth(minWidth);
            column.setPrefWidth(prefWidth);
        }else {
            column.setMaxWidth(0);
            column.setMinWidth(0);
            column.setPrefWidth(0);
        }
    }


    /**
     * @return the columnFont
     */
    public Font getColumnFont() {
        return columnFont;
    }


    /**
     * @param columnFont the columnFont to set
     */
    public void setColumnFont(Font columnFont) {
        this.columnFont = columnFont;
    }
}
