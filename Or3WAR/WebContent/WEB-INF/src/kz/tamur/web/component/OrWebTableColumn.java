package kz.tamur.web.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.PopupColumnAdapter;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.controller.WebController;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 19.03.2004
 * Time: 11:04:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class OrWebTableColumn extends WebComponent implements JSONComponent, OrColumnComponent {

    protected OrWebTable table;
    protected PropertyChangeSupport ps = new PropertyChangeSupport(this);
    protected OrGuiComponent editor;
    protected int preferredWidth;
    protected int maxWidth;
    protected int minWidth;
    private OrGuiContainer parent;
    private int modelIndex;
    private int uniqueIndex;
    private String title;
    private String titleUid;
    private String columnBackColorExpr;
    private String columnFontColorExpr;
    private boolean canSort = true;

    protected boolean isHelpClick = false;

    protected ColumnAdapter adapter;
    private String iconName;
    private Map<Integer, Integer> states = new HashMap<Integer, Integer>();
    private int rotation;

    protected OrWebTableColumn(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUid);
        if (mode == Mode.RUNTIME) {
            if (!WebController.NO_COMP_DESCRIPTION) {
                if (descriptionUID != null)
                    description = frame.getBytes(descriptionUID);
            }
        } else {
            PropertyValue pv = getPropertyValue(getProperties().getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = frame.getBytes(descriptionUID);
            }
        }
        if (this instanceof OrWebDateColumn) {
            ((OrWebDateColumn) this).editor.setLangId(langId);
        }
    }

    protected void init() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("width").getChild("pref"));
        if (!pv.isNull() && pv.intValue() != 0) {
            preferredWidth = pv.intValue();
        } else {
            preferredWidth = Constants.DEFAULT_PREF_WIDTH;
        }
        pv = getPropertyValue(getProperties().getChild("width").getChild("max"));
        if (!pv.isNull() && pv.intValue() != 0) {
            maxWidth = pv.intValue();
        } else {
            maxWidth = Constants.DEFAULT_MAX_WIDTH;
        }
        pv = getPropertyValue(getProperties().getChild("width").getChild("min"));
        if (!pv.isNull() && pv.intValue() != 0) {
            minWidth = pv.intValue();
        } else {
            minWidth = Constants.DEFAULT_MIN_WIDTH;
        }
        PropertyNode pn = getProperties().getChild("header");

        pv = getPropertyValue(pn.getChild("rotation"));
        if (!pv.isNull()) {
            rotation = pv.intValue();
        }

        pv = getPropertyValue(pn.getChild("text"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUid = (String) p.first;
            title = frame.getString(titleUid);
        } else {
            title = "Column";
        }
        
        pv = getPropertyValue(pn.getChild("canSort"));
        if (pv != null && !pv.isNull()) {
            canSort = pv.booleanValue();
        } else {
            canSort = true;
        }
        if (!WebController.NO_COMP_DESCRIPTION) {
            pv = getPropertyValue(getProperties().getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                descriptionUID = (String) p.first;
                description = frame.getBytes(descriptionUID);
            }
        }
        if (mode == Mode.RUNTIME) {
            xml = null;
        }
    }
    
    public OrWebTable getOrWebTable() {
        return table;
    }

    public void setOrWebTable(OrWebTable table) {
        this.table = table;
        init();
    }

    public OrGuiComponent getEditor() {
        return editor;
    }

    public void setPrefWidth(int width) {
        preferredWidth = width;
    }

    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    public void setMinWidth(int width) {
        minWidth = width;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public String getTitle() {
        return title;
    }

    public int getRotation() {
        return rotation;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String name) {
        this.iconName = name;
    }

    public int getSummaryType() {
        int type = Constants.SUMMARY_NO;
        if (this instanceof OrWebIntColumn || this instanceof OrWebFloatColumn) {
            PropertyValue pv = getPropertyValue(getProperties().getChild("view").getChild("summary"));
            if (!pv.isNull()) {
                type = pv.intValue();
            }
        }
        return type;
    }

    private boolean isSorted() {
        PropertyNode pn = getProperties().getChild("sorted");
        PropertyValue pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            return pv.booleanValue();
        } else {
            return false;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        ps.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        ps.removePropertyChangeListener(l);
    }

    public GridBagConstraints getConstraints() {
        return null;
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public Dimension getPrefSize() {
        return null;
    }

    public Dimension getMaxSize() {
        return null;
    }

    public Dimension getMinSize() {
        return null;
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    public void setModelIndex(int modelIndex) {
        this.modelIndex = modelIndex;
    }

    public void setUniqueIndex(int unique) {
        uniqueIndex = unique;
    }

    public int getUniqueIndex() {
        return uniqueIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setEnabled(boolean isEnabled) {
    }

    public OrGuiComponent getEditor(int row) {
        table.absoluteRow(row);
        return editor;
    }

    public boolean isEnabled() {
        return false;
    }

    /*
     * private void setColumnBackColorExpr() {
     * PropertyNode prop = getProperties();
     * PropertyNode pn = prop.getChild("view").getChild("background");
     * if (pn != null) {
     * PropertyNode pn1 = pn.getChild("backgroundColorExpr");
     * if (pn1 != null) {
     * PropertyValue pv = getPropertyValue(pn1);
     * if (!pv.isNull()) {
     * columnBackColorExpr = pv.stringValue(frame.getKernel());
     * }
     * }
     * }
     * }
     */

    /*
     * private void setColumnFontColorExpr() {
     * PropertyNode prop = getProperties();
     * PropertyNode pn = prop.getChild("view").getChild("font");
     * if (pn != null) {
     * PropertyNode pn1 = pn.getChild("fontExpr");
     * if (pn1 != null) {
     * PropertyValue pv = getPropertyValue(pn1);
     * if (!pv.isNull()) {
     * columnFontColorExpr = pv.stringValue(frame.getKernel());
     * }
     * }
     * }
     * }
     */

    public String getColumnBackColorExpr() {
        return columnBackColorExpr;
    }

    public String getColumnFontColorExpr() {
        return columnFontColorExpr;
    }

    public ColumnAdapter getAdapter() {
        return adapter;
    }

    public void getJSONValue(Object value, int row, boolean cellEditable, boolean isSelected, String tid, JsonObject obj) {
        if (editor != null) {
            int index = adapter.getIndex();
            Integer state = states.get(row);
            if (state == null)
                state = 0;
            if (table instanceof OrWebTreeTable || table instanceof OrWebTreeTable2) {
                index++;
            }
            obj.add("editor", ((WebComponent) editor).getJSON(value, row, index, tid, cellEditable, isSelected, state));
        } else {
            JsonObject rnd = new JsonObject();
            if (value != null) {
                rnd.add("title", Funcs.xmlQuote(value.toString()));
            }
            obj.add("render", rnd);
        }
    }

    public WebComponent getCellRenderer(Object value, int row, boolean cellEditable, boolean isSelected, String tid) {
        return editor == null ? null : (WebComponent) editor;
    }

    public JsonObject getCellEditor(Object value, int row, String tid, boolean cellEditable) {
        if (cellEditable) {
            // try {
            table.absoluteRow(row);
            // getAdapter().getRef().absolute(row, table.getAdapter());
            // } catch (KrnException e) {
            // e.printStackTrace();
            // }
            int index = adapter.getIndex();
            if (table instanceof OrWebTreeTable || table instanceof OrWebTreeTable2)
                index++;
            return ((WebComponent) editor).getCellEditor(value, row, index, tid, getPreferredWidth(),(JsonObject)null);
        } else
            return null;
    }

    public JsonObject getCellEditor(int row) {
        table.absoluteRow(row);
        return ((WebComponent) editor).getJsonEditor();
    }

    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }

    public WebComponent getParent() {
        return table;
    }

    public boolean isCanSort() {
        return canSort;
    }

    public int getState(Integer index) {
        return states.get(index);
    }

    public void setStates(Map<Integer, Integer> states) {
        if (!this.states.toString().equals(states.toString())) {
            this.states.clear();
            for (Iterator<Integer> it = states.keySet().iterator(); it.hasNext();) {
                Integer key = it.next();
                this.states.put(key, states.get(key));
            }
            table.setStateChanged(true);
        }
    }

    public void setVisible(boolean isVisible) {
        ((WebComponent)editor).setVisible(isVisible);

        JsonArray arr = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.add("index", getAdapter().getIndex());
		obj.add("uuid", uuid);
        obj.add("v", isVisible ? 1 : 0);
        arr.add(obj);
        
        table.sendChangeProperty("cv", arr);
      //  table.tableDataChanged() ;
        table.tableSortChanged() ;
    }

    public boolean isVisible() {
        return ((WebComponent) editor).isVisible();
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        sendChange(obj, isSend);
        return obj;
    }
    
    public boolean isCellEditable(int rowIndex) {
    	TableAdapter ta = (TableAdapter)table.getAdapter();
    	int access = ta.getAccess();
        if (!ta.isHackEnabled() || access == Constants.READ_ONLY_ACCESS) {
            if (!(adapter instanceof PopupColumnAdapter)) {
                return false;
            } else {
                return adapter.checkEnabled();
            }
        } else if (access == Constants.LAST_ROW_ACCESS) {
            int rowcount = table.getRowCount();
            return (rowcount - 1 == rowIndex);
        } else if (access == Constants.BY_TRANSACTION_ACCESS) {
            //ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
            //OrRef c_ref = ca.dataRef; //@todo Выяснить у Каиржана!
        }
        return adapter.isEnabled();
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