package kz.tamur.web.component;

import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.comps.Utils.mergeHeight;
import static kz.tamur.comps.Utils.mergeWidth;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JTable;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Filter;
import kz.tamur.comps.FilterMenuItem;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TablePropertyRoot;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableComponent;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPopupMenu;
import kz.tamur.web.common.webgui.WebTable;
import kz.tamur.web.controller.WebController;

import org.apache.commons.fileupload.FileItem;
import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class OrWebTable extends WebTable implements JSONComponent, OrTableComponent {

	protected static PropertyNode PROPS = new TablePropertyRoot();
    protected static PropertyNode COLUMNS = PROPS.getChild("columns");
    protected OrGuiContainer guiParent;
    protected boolean isNaviExists = false;
    private boolean isAddPan;
    protected OrWebTableNavigator navi = null;
    protected boolean isFooterExist = false;
    protected OrWebTableFooter footer = null;
    protected boolean isSelfChange = false;

    private String title;
    private String titleUID;
    private FilterRecord[] filters;

    protected TableAdapter adapter;
    protected WebButton addBtn, yesManBtn;
    protected final String YESMAN_DOWN = WebController.APP_PATH + "/images/goDown.gif";
    protected final String YESMAN_RIGHT = WebController.APP_PATH + "/images/goRight.gif";

    private String limitExceededMessageId;
    private boolean limitExceeded = false;
    private String rowBackColorExpr;
    private String rowFontColorExpr;

    protected String zebra1;
    protected String zebra2;

    private int countProc = 0;
    public int countBtn = 10;
    private int filterBtnView = Constants.DIALOG;
    private int[] separators;
    private boolean isOpaque;
    private boolean yesMan;
    private boolean autoAddRow = true;
    private boolean noCopy = true;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    public boolean isStopWait = false;
    protected OrWebPanel addPan;
    private int autoRefreshMillis = 0;
    private List<OrWebTableColumn> columns = new ArrayList<OrWebTableColumn>();
    protected String selCol;
	
    private String prevSortCol;
    private String prevSortOrder;

	private Color isZebraColor1;
	private Color isZebraColor2;
    protected int tableViewType;

    OrWebTable(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        
        try {
	        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
	        if (!pv.isNull()) {
	            varName = pv.stringValue(frame.getKernel());
	        }
	
	        init();
	        // Создаем и инициализируем таблицу
	        setGridColor(Color.white);
	        setAutoCreateColumnsFromModel(false);
	        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	        
	        // Создаем колонки
	        pv = getPropertyValue(COLUMNS);
	        if (!pv.isNull()) {
	            List<Element> columns = pv.elementValue().getChildren();
	            for (Element child : columns) {
	                WebComponent comp = fm.create(child, mode, frame);
	                addComponent(comp, -1, true);
	            }
	        }
	
	        
	        pv = getPropertyValue(PROPS.getChild("view").getChild("tableViewType"));
	        tableViewType = pv.isNull() ? (Integer) PROPS.getChild("view").getChild("tableViewType").getDefaultValue() : pv.enumValue();

	        pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("show"));
	        isNaviExists = pv.booleanValue();
	        
	        pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("addPan"));
	        isAddPan= pv.booleanValue();
	        
	        pv = getPropertyValue(PROPS.getChild("view").getChild("background").getChild("zebra").getChild("color1"));
	        isZebraColor1= pv.isNull() ? Color.white : pv.colorValue();
	        
	        pv = getPropertyValue(PROPS.getChild("view").getChild("background").getChild("zebra").getChild("color2"));
	        isZebraColor2= pv.isNull() ? Color.white : pv.colorValue();
	        
	        if (isNaviExists) {
	            navi = new OrWebTableNavigator(this);
	            // определить разделители кнопок
	            setSeparates();
	            updateNaviButtons();
	            if (isAddPan) {
	                Element panel;
	
	                pv = getPropertyValue(PROPS.getChild("addPanC"));
	                if (!pv.isNull()) {
	                    List children = pv.elementValue().getChildren();
	                    panel = (Element) children.get(0);
	
	                } else {
	                    panel = new Element("Component");
	                    panel.setAttribute("class", "Panel");
	                }
	                addPan = (OrWebPanel) fm.create(panel, mode,frame);
	                
	                if (pv.isNull()) {
	                    PropertyHelper.addProperty(new PropertyValue(addPan.getXml(), PROPS.getChild("addPanC")), xml);
	                }
	            }
	        }
	
	        PropertyNode pov = PROPS.getChild("pov");
	
	        PropertyNode refreshAttr = pov.getChild("autoRefresh");
	        pv = getPropertyValue(refreshAttr);
	        if (!pv.isNull()) {
	            autoRefreshMillis  = pv.intValue();
	        }
	
	        // считать атрибут отвечающий за формат отображения фильтра
	        PropertyNode filterBtnAttr = pov.getChild("filterBtnAttr");
	        pv = getPropertyValue(filterBtnAttr);
	        filterBtnView = pv.intValue();
	        
	        PropertyNode nocopy = pov.getChild("activity").getChild("nocopy");
	        pv = getPropertyValue(nocopy);
	        if (!pv.isNull()) {
	            noCopy  = !pv.booleanValue();
	        }
	        
	        PropertyNode access = pov.getChild("access");
	        pv = getPropertyValue(access);
	        if (mode == Mode.RUNTIME) {
	            PropertyNode pn = pov.getChild("maxObjectCountMessage");
	            pv = getPropertyValue(pn);
	            if (!pv.isNull()) {
	                limitExceededMessageId = (String) pv.resourceStringValue().first;
	            }
	        }
	
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        PropertyNode prop = PROPS.getChild("title");
	        pv = getPropertyValue(prop);
	        if (!pv.isNull()) {
	            Pair p = pv.resourceStringValue();
	            titleUID = (String) p.first;
	            title = frame.getString(titleUID);
	        }
	        pv = getPropertyValue(getProperties().getChild("ref").getChild("filters"));
	        if (!pv.isNull()) {
	            filters = (FilterRecord[]) pv.objectValue();
	        }
	
	        if (mode == Mode.RUNTIME) {
	            if (isNaviExists) {
	                addBtn = navi.getButtonByName("addBtn");
	                yesManBtn = navi.getButtonByName("yesManBtn");
	                navi.initFilterPopupMenu(getFilterItems());
	
	                switch (getFilterBtnView()) {
	                default:
	                case Constants.MENU_MULTI: // Меню-мультивыбор
	                    navi.initMenu(true);
	                    break;
	                case Constants.MENU_SWITCH: // Меню-переключатель
	                    navi.initMenu(false);
	                    break;
	                }
	
	            }
	        }
	
	        pv = getPropertyValue(getProperties().getChild("extended").getChild("gradient"));
	        if (!pv.isNull() && navi != null) {
	            // градиентная заливка компонента
	            navi.setGradient((GradientColor) pv.objectValue());
	        }
	        // прозрачность компонента(да/нет)
	        pv = getPropertyValue(getProperties().getChild("extended").getChild("transparent"));
	        isOpaque = !pv.booleanValue();
	        setTransparent(isOpaque);
	
	        setRowFontColorExpr();
	        setRowBackColorExpr();
	
	        pv = getPropertyValue(PROPS.getChild("pos").getChild("fill"));
	        fill = pv.intValue();
	
	        setModel(createTableModel());
	/*        if (adapter.getRef() != null)
	            adapter.getRef().removeOrRefListener(adapter);
	        if (adapter.getRef() != null)
	            adapter.getRef().addOrRefListener(adapter);
	*/        setRenderer(new RtWebTableCellRenderer(adapter));
	
			for (OrWebTableColumn col : columns) {
	            RtWebTableModel m = (RtWebTableModel) getModel();
	            m.addColumn(col, -1);
		    }
	
			if (mode == Mode.RUNTIME) {
	            if (isNaviExists) {
	                adapter.addPropertyChangeListener(navi);
	                navi.setTableAdapter(adapter);
	            }
			}
		
	        if (isZebraColor1 == Color.white && WebController.ZEBRA_COLOR_1 != null) {
	            zebra1 = WebController.ZEBRA_COLOR_1;
	        } else if (isZebraColor1 == Color.white) {
	        	zebra1 = null;
	        } else {
	            String r = Integer.toString(adapter.getZebraColor1().getRed(), 16);
	            if (r.length() < 2)
	                r = "0" + r;
	            String g = Integer.toString(adapter.getZebraColor1().getGreen(), 16);
	            if (g.length() < 2)
	                g = "0" + g;
	            String b = Integer.toString(adapter.getZebraColor1().getBlue(), 16);
	            if (b.length() < 2)
	                b = "0" + b;
	            zebra1 = "#" + r + g + b;
	        }
	
	        if (isZebraColor2 == Color.white && WebController.ZEBRA_COLOR_2 != null) {
	            zebra2 = WebController.ZEBRA_COLOR_2;
	        } else if (isZebraColor2 == Color.white) {
	        	zebra2 = null;
	        } else {
	            String r = Integer.toString(adapter.getZebraColor2().getRed(), 16);
	            if (r.length() < 2)
	                r = "0" + r;
	            String g = Integer.toString(adapter.getZebraColor2().getGreen(), 16);
	            if (g.length() < 2)
	                g = "0" + g;
	            String b = Integer.toString(adapter.getZebraColor2().getBlue(), 16);
	            if (b.length() < 2)
	                b = "0" + b;
	            zebra2 = "#" + r + g + b;
	        }
	
	        // Создаем меню процессов TODO реализовать общий механизм с контекстным меню
	        List<TableAdapter.ProcessRecordAction> actions = adapter.getActions();
	        if (isNaviExists && actions != null) {
	            final WebPopupMenu popupMenu = new WebPopupMenu(null, mode, frame, null);
	            for (TableAdapter.ProcessRecordAction action : actions) {
	                // popupMenu.add(createMenuItem(action));
	                if (action.getIcon() != null) {
	                    navi.addAction(action);
	                }
	            }
	            // так как были добавлены новые кнопки на панель необходимо обновить их состояние
	            updateNaviButtons();
	        }
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

        if (!(this instanceof OrWebTreeTable) && !(this instanceof OrWebTreeTable2))
            this.xml = null;
    }

    public FilterRecord[] getFilters() {
        return filters;
    }

    protected void init() {
    }

    protected WebTableModel createTableModel() throws KrnException {
        if (mode == Mode.RUNTIME) {
            adapter = new TableAdapter(frame, this, false);
            return new RtWebTableModel(adapter);
        }
        return null;
    }

    public void setFooter() {
        if (mode == Mode.RUNTIME) {
            PropertyValue pv = getPropertyValue(PROPS.getChild("pov").getChild("footer"));
            if (!pv.isNull()) {
                isFooterExist = pv.booleanValue();
            }
            if (isFooterExist) {
                RtWebTableModel model = (RtWebTableModel) getModel();
                ArrayList<ColumnAdapter> columns = new ArrayList<ColumnAdapter>();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    columns.add(model.getColumnAdapter(i));
                }
                footer = new OrWebTableFooter(columns);
            }
        }
    }

    protected void addComponent(WebComponent c, int pos, boolean isLoading) {
        if (c instanceof OrWebTableColumn) {
            OrWebTableColumn col = (OrWebTableColumn) c;
            col.setOrWebTable(this);
            //RtWebTableModel m = (RtWebTableModel) getModel();
            //m.addColumn(col, pos);
            columns.add(col);
            
            if (!isLoading) {
                PropertyHelper.addProperty(new PropertyValue(((OrGuiComponent) c).getXml(), COLUMNS), xml);
            }
        }
    }

    public boolean canAddComponent(int x, int y) {
        return true;
    }

    public void addComponent(OrGuiComponent c, Object cs) {
    }

    public Object removeComponent(OrGuiComponent c) {
    	return null;
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    public void setEnabled(boolean enabled) {
        if (mode == Mode.RUNTIME) {
            super.setEnabled(enabled);
            if (isNaviExists) {
                navi.setEnabled(enabled, adapter.getDelActivityRef() == null);
            }
        }
    }
    
    public PropertyNode getProperties() {
        return PROPS;
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUID);
        adapter.setInterfaceLangId(langId);
        if (navi != null) {
            navi.setInterfaseLangId(langId);
            if (mode == RUNTIME) {
                // Обновляем наименования процессов
                List<TableAdapter.ProcessRecordAction> actions = adapter.getActions();
                if (actions != null)
                    for (TableAdapter.ProcessRecordAction action : actions) {
                        action.setInterfaceLangId(langId);
                    }
                if (addPan != null) {
                    addPan.setLangId(langId);
                }
            }
        }
    }

    public void CellError() {
    }

    public void firePropertyModified() {
    }

    public String getTitle() {
        return title;
    }

    public boolean isNaviExists() {
        return isNaviExists;
    }

    public PropertyChangeListener getNavigator() {
        return navi;
    }

    public OrWebTableNavigator getNavi() {
        return navi;
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

    private void updateNaviButtons() {
        PropertyValue pvg = getPropertyValue(getProperties().getChild("extended").getChild("gradient"));
        if (!pvg.isNull() && navi != null) {
            // градиентная заливка компонента
            navi.setGradient((GradientColor) pvg.objectValue());
        }
        setSeparates();
        PropertyNode buttonsNode = getProperties().getChild("view").getChild("navi").getChild("buttons");
        PropertyValue pvProc = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("processes"));

        countProc = (pvProc.processRecordsValue() == null) ? 0 : pvProc.processRecordsValue().size();
        countBtn = 12; //buttonsNode.getChildCount() - 2; // отнять двойку так как 11-12-й элементы это: панель навигации и поле с параметрами разделителями
        final int sumCount = countBtn + countProc;
        navi.indxBtn = new boolean[sumCount];

        // в массиве задать кнопки процессов
        for (int i = countBtn; i < sumCount; i++) {
            navi.indxBtn[i] = true;
        }

        boolean vis;
        for (int i = 0; i < buttonsNode.getChildCount(); i++) {
            PropertyNode pn = buttonsNode.getChildAt(i);
            PropertyValue pv = getPropertyValue(pn);
            vis = false;
            // если это не строка(в которой содержатся позиции сепараторов)
            if (pv.objectValue() instanceof Boolean || pn.getDefaultValue() instanceof Boolean) {
                if (!pv.isNull()) {
                    vis = pv.booleanValue();
                    navi.setButtonsVisible(pn, vis);
                } else {
                    vis = ((Boolean) pn.getDefaultValue()).booleanValue();
                    navi.setButtonsVisible(pn, vis);
                }
            }
            
            if (pn.getChildCount() > 0) {
                for (int j = 0; j < pn.getChildCount(); j++) {
                    pv = getPropertyValue(pn.getChildAt(j));
                    if ("naviBtnTooltip".equals(pn.getChildAt(j).getName())) {
                        Pair p = pv.resourceStringValue();
                        if (p != null) {
                        	String textInfoUID = (String) p.first;
	                        String textInfo = frame.getString(textInfoUID);
                    		navi.setButtonsTextInfo(pn, textInfo, textInfoUID);
                        }
                    } else if ("naviBtnIcon".equals(pn.getChildAt(j).getName())) {
                    	Object value = pv.getValue();
                    	if (value != null) {
                    		byte[] iconBytes = (byte[]) value;
                    		if (iconBytes.length > 0) {
                				navi.setButtonsIcon(pn, iconBytes);
                    		}
                    	}
                    }
                }
            }
        }
        navi.setSeparator(separators, navi.indxBtn);
        PropertyValue pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("yesManDirection"));

        if (pv == null || pv.isNull() || pv.intValue() == Constants.DIRECTION_RIGHT) {
            navi.getButtonByName("yesManBtn").setIconPath(YESMAN_RIGHT);
            yesMan = true;
            autoAddRow = true;
        } else if (pv.intValue() == Constants.DIRECTION_DOWN) {
            navi.getButtonByName("yesManBtn").setIconPath(YESMAN_DOWN);
            yesMan = false;
            autoAddRow = true;
        } else if (pv.intValue() == Constants.DIRECTION_RIGHT_WITHOUT_ADD_ROW) {
            navi.getButtonByName("yesManBtn").setIconPath(YESMAN_RIGHT);
            yesMan = true;
            autoAddRow = false;
        } else if (pv.intValue() == Constants.DIRECTION_DOWN_WITHOUT_ADD_ROW) {
            navi.getButtonByName("yesManBtn").setIconPath(YESMAN_DOWN);
            yesMan = false;
            autoAddRow = false;
        }
    }

    public OrColumnComponent getColumnAt(int col) {
        RtWebTableModel model = (RtWebTableModel) getModel();
        return model.getColumn(col);
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public byte[] getDescription() {
        return null;
    }

    public void setSelectedRow(int row) {
    	row = adapter.refToWebIndex(row);
        super.setSelectedRow(row);
        if (navi != null && isSelected) {
            navi.setSelectedRow(row + 1);
        }
    }

    public void setSelectedRows(int[] rows) {
        super.setSelectedRows(rows, false);
        if (navi != null) {
            navi.setSelectedRow(rows != null && rows.length > 0 ? rows[0] + 1 : 0);
        }
    }

    public void yesMan() {
        yesManBtn.setIconPath(adapter.yesMan() ? YESMAN_DOWN : YESMAN_RIGHT);
    }

    public int[] getSelectedRows() {
        return super.getSelectedRows();
    }

    public int getSelectedRow() {
        int rows[] = getSelectedRows();
        if (rows != null && rows.length > 0)
            return rows[0];
        return -1;
    }

    public void tableSortChanged() {
        sortChanged = true;
        sortChanged();
    }

    public void tableDataChanged() {
        dataChanged = true;
        selectedRows = null;
        if (navi != null) {
            navi.setRowCount(getRowCount());
        }
        sendChangeProperty("reload", 1);
        removeChange("pr.updateTblRow");
    }

    public void tableRowsDeleted(int firstRow, int lastRow) {
        int count = lastRow - firstRow + 1;
        if (deletedRows == null) {
            deletedRows = new int[count];
            for (int i = 0; i < count; i++) {
                deletedRows[i] = firstRow + i;
            }
        } else {
            int[] rows = new int[count + deletedRows.length];
            for (int i = 0; i < deletedRows.length; i++) {
                rows[i] = deletedRows[i];
            }
            for (int i = count - 1; i >= 0; i--) {
                rows[i + deletedRows.length] = firstRow + i;
            }
            deletedRows = rows;
        }
        sendChangeProperty("reload", 1);
        removeChange("pr.updateTblRow");
    }

    public void tableRowsInserted(int firstRow, int lastRow) {
        int count = lastRow - firstRow + 1;
        insertedRows = new int[count];
        for (int i = 0; i < count; i++) {
            insertedRows[i] = firstRow + i;
        }
        sendChangeProperty("reload", 1);
        removeChange("pr.updateTblRow");
    }

    public void tableCellUpdated(int row, int col) {
        tableRowsUpdated(row, row);
    }

    public void tableCellUpdated(int row, String col) {
        tableRowsUpdated(row, row);
    }

    public void tableStructureChanged() {
        dataChanged = true;
        dataChanged(zebra1, zebra2);
    }

    public void tableRowsUpdated(int firstRow, int lastRow) {
        if (updatedList == null)
            updatedList = new ArrayList<Integer>();

        for (int i = firstRow; i <= lastRow; i++) {
            if (!updatedList.contains(i)) {
                updatedList.add(i);
            }
	        JsonArray arr = new JsonArray();
	        JsonObject obj = new JsonObject();
	        int webIndex = adapter.refToWebIndex(i);
	        obj.add("index", webIndex);
	        obj.add("row", getRowData(webIndex));
	        arr.add(obj);
	        
	        sendChangeProperty("updateTblRow", arr);
        }
    }

    public void addPropertyListener(PropertyListener l) {
    }

    public void removePropertyListener(PropertyListener l) {
    }

    public void copyRows() {
    	if(adapter.isEnabled()){
	        adapter.copyRows();
	        sendChangeProperty("addRow", "");
	        if (navi != null)
	            navi.setRowCount(getRowCount());
	    	}
    }

    public int addRow() {
    	if (adapter.isEnabled()){
	        int r = adapter.addNewRow();
	        if (navi != null) {
	            navi.setRowCount(getRowCount());
	        }
	        return r;
	    	}
    	return -1;
    }

    // Delete Row NEW OR3
    public JsonObject deleteRow(boolean sure, String idx) {
    	if(adapter.isEnabled() && getRowCount() > 0){
    		if(idx != null) {
	    		StringTokenizer st = new StringTokenizer(idx, ",");
	            int[] rows = new int[st.countTokens()];
	            for (int i = 0; i < rows.length; i++) {
	                String token = st.nextToken();
	                rows[i] = Integer.parseInt(token);
	            }
	            return deleteRow(sure, rows);
    		} else {
	    		int[] selIdx = getSelectedRows();
	            return deleteRow(sure, selIdx);
    		}
    	}
    	return new JsonObject();
    }
    
    public JsonObject deleteRow(boolean sure, int[] index) {
        JsonObject res = adapter.deleteRow(index, sure);
        if (navi != null) {
            navi.setRowCount(getRowCount());
        }
        return res;
    }

    public void deleteRow() {
        int[] selIdx = getSelectedRows();
        adapter.deleteRow(selIdx);
        if (navi != null) {
            navi.setRowCount(getRowCount());
        }
    }

    public void setValue(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        int[] rows = new int[st.countTokens()];
        for (int i = 0; i < rows.length; i++) {
            String token = st.nextToken();
            rows[i] = Integer.parseInt(token);
        }
        setSelectedRows(rows, true);
        int[] selRows = getSelectedRows();
        int[] refRows = new int[selRows.length];
        for (int i=0; i<selRows.length; i++) {
        	refRows[i] = adapter.webToRefIndex(selRows[i]);
        }
        adapter.setSelectedRows(refRows);
    }

    protected void absoluteRow(int row) {
    	row = adapter.webToRefIndex(row);
    	if (adapter.getRef() != null) {
	        adapter.getRef().absolute(row, this);
	        adapter.getRef().setSelectedItems(new int[] { row });
    	}
    }

    public void setValue(String val, int row, int col) {
        absoluteRow(row);
        if (adapter.checkUnique(val, row, col)) {
            OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
            WebComponent editor = (WebComponent) c.getEditor();
            editor.setValue(val);
        } else {
            ResourceBundle resource = getWebSession().getResource();
            getWebSession().sendMultipleCommand("alert", resource.getString("duplicateData"));
        }
        tableCellUpdated(row, col);
    }

    public JsonObject setValue(String val, int row, String colUid) {
        absoluteRow(row);
        OrColumnComponent c = (OrColumnComponent) ((WebFrame) frame).getComponentByUID(colUid);
        WebComponent editor = (WebComponent) c.getEditor();
        if (adapter.checkUnique(val, row, adapter.getColumnIndex(c))) {
            if (editor instanceof OrWebTreeField) {
                try {
                    long nodeId = Long.parseLong(val);
                    ((OrWebTreeField) editor).setSelectedNode(nodeId);
                } catch (Exception e) {
                    getLog().error(e, e);
                }
            } else if (editor instanceof OrWebHyperPopup) {
                return ((OrWebHyperPopup) editor).buttonPressed(val);
            } else if (!(editor instanceof OrWebDocField)) {
                editor.setValue(val);
				if (editor instanceof OrWebCheckBox) {
					if (((OrWebCheckColumn) c).isUniqueSelection()) {
						if ("true".equals(val)) {
							for (int i = 0; i < getRowCount(); i++) {
								if (i == row) {
									continue;
								}
								long other = (Long) getValueAt(i, getColumnIndexByUID(colUid));
								if (other == 1) {
									absoluteRow(i);
									editor.setValue("false");
									tableCellUpdated(i, colUid);
								}
							}
						}
					}
				}
            }
            if (adapter.getDataRef() == null && model != null) {
            	model.setValueAt(val, row, adapter.getColumnIndex(c));
            }
        } else {
            ResourceBundle resource = getWebSession().getResource();
            getWebSession().sendMultipleCommand("alert", resource.getString("duplicateData"));
        }
        tableCellUpdated(row, colUid);
        return null;
    }

    public void forward(int row, String colUid, boolean evalBeforeOpen) {
        absoluteRow(row);

        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebHyperLabel) {
            ((OrWebHyperLabel) editor).forward(evalBeforeOpen);
        }
    }

    public String openPopup(int row, int col, long fid) {
        absoluteRow(row);

        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebHyperPopup) {
            return ((OrWebHyperPopup) editor).actionPerformed(row, col, id, fid);
        }
        return "";
    }

    public String openPopup(int row, String colUid, long fid) {
        absoluteRow(row);

        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebHyperPopup) {
            return ((OrWebHyperPopup) editor).openPopup(row, colUid, id, fid);
        }
        return "";
    }

    public JsonObject buttonPressed(int row, int col) {
        absoluteRow(row);

        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebDocField) {
            return ((OrWebDocField) editor).buttonPressed();
        }
        return null;
    }

    public void uploadDoc(int row, int col, FileItem fileItem) {
        absoluteRow(row);

        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebDocField) {
            ((OrWebDocField) editor).setValue(fileItem);
        }
    }

    public void uploadDoc(int row, int col, InputStream is, String name) {
        absoluteRow(row);

        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebDocField) {
            ((OrWebDocField) editor).setValue(is, name);
        }
    }

    public String treeFieldPressed(int row, String colUid) {
        absoluteRow(row);

        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebTreeField) {
            return ((OrWebTreeField) editor).treeFieldPressed(colUid);
        }
        return "";
    }

    public JsonObject treeFieldPressed2(int row, int col) {
        absoluteRow(row);
        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebTreeField) {
            return ((OrWebTreeField) editor).actionPerformed2(id, row, col);
        }
        return null;
    }

    public JsonObject expandTreeField2(String nid, String wait, int col) {
        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebTreeField) {
            return ((OrWebTreeField) editor).expand2(nid, wait, col);
        }
        return null;
    }

    public void selectNode(long nid, int col) {
        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        WebComponent editor = (WebComponent) c.getEditor();
        if (editor instanceof OrWebTreeField) {
            ((OrWebTreeField) editor).selectNode(nid);
        }
    }

    public String getCellEditor(int row, String colUid) {
        absoluteRow(row);
        OrColumnComponent c = (OrColumnComponent) ((WebFrame)frame).getComponentByUID(colUid);
        JsonObject res;
        if (c.isCellEditable(row)) {
        	res = c.getCellEditor(row);
        } else {
        	res = new JsonObject();
        }
        if (c instanceof OrWebMemoColumn && ((OrWebMemoColumn) c).isShowTextAsXML()) {
        	res.add("showTextAsXML", 1);
        }
        return res.toString();
    }

    public JsonObject getCellEditor2(int row, int col) {
        JsonObject b = new JsonObject();
        ColumnAdapter ca = ((RtWebTableModel) model).getColumnAdapter(col);
        boolean isCellEditable = getModel().isCellEditable(row, col);
        b = ca.getColumn().getCellEditor(model.getValueAt(row, col), row, id, isCellEditable);
        return b;
    }

    public void sortColumn(int col) {
        boolean b = adapter.sortByColumn(col);
        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        if (b)
            c.setIconName("SortDown");
        else
            c.setIconName("SortUp");
        tableSortChanged();
        tableDataChanged();
    }

    public void removeSortColumn(int col) {
        adapter.removeSortColumn(col);
        OrColumnComponent c = ((RtWebTableModel) model).getColumn(col);
        c.setIconName(null);
        tableSortChanged();
        tableDataChanged();
    }
    public boolean isDelEnabled(){
        if (mode == Mode.RUNTIME) {
            if (isNaviExists) {
                return navi.isDelEnabled();
            }
        }
        return false;
    }
    public void setDelEnabled(boolean enabled) {
        if (mode == Mode.RUNTIME) {
            if (isNaviExists) {
                navi.setDelEnabled(enabled);
                sendChangeProperty("ne", new JsonObject().add("actionId", getId() + "_del").add("e", enabled ? "1" : "0"));
            }
        }
    }

    private void updateLimitExceedMessage() {
        if (navi != null) {
            if (limitExceeded && limitExceededMessageId != null) {
                navi.setMessage(frame.getString(limitExceededMessageId));
    			sendChangeProperty("limitExceededMessage", frame.getString(limitExceededMessageId));
            } else {
                navi.setMessage("");
    			sendChangeProperty("limitExceededMessage", "");
            }
        }
    }

    public void setLimitExcceded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
        updateLimitExceedMessage();
    }

    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName()))
            return this;

        int count = getColumnCount();
        for (int i = 0; i < count; i++) {
            OrColumnComponent c = getColumnAt(i);
            if (c != null) {
                if (title.equals(c.getVarName()))
                    return c;
            }
        }
        return null;
    }

    /**
     * Получить формат отображения формы задания фильтра
     * 
     * @return 0 - Диалоговое окно; 1 - Выпадающее меню
     */
    public int getFilterBtnView() {
        return filterBtnView;
    }

    private FilterMenuItem[] getFilterItems() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("ref").getChild("filters"));
        List<Filter> list = new ArrayList<Filter>();
        if (!pv.isNull()) {
            try {
                Kernel krn = frame.getKernel();
                KrnClass cls = krn.getClassByName("Filter");
                KrnAttribute attr = krn.getAttributeByName(cls, "dateSelect");
                FilterRecord[] frs = (FilterRecord[]) pv.objectValue();
                long langId = krn.getInterfaceLanguage().id;
                // Set keySet = m.keySet();
                // Iterator it = keySet.iterator();
                for (int i = 0; i < frs.length; ++i) {
                    KrnObject obj = new KrnObject(frs[i].getObjId(), "", cls.id);
                    long[] flags = krn.getLongs(obj, attr, 0);
                    Filter f = new Filter(obj, langId, "Filter", flags.length > 0 ? flags[0] : 0);
                    String[] titles = krn.getStrings(obj, "title", langId, 0);
                    f.setTitle(titles.length > 0 ? titles[0] : "", langId);
                    list.add(f);
                }
            } catch (Exception e) {
                getLog().error(e, e);
            }
            if (list.size() > 0) {
                FilterMenuItem[] res = new FilterMenuItem[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    FilterMenuItem fm = new FilterMenuItem((Filter) list.get(i));
                    res[i] = fm;
                }
                return res;
            }
        }
        return null;
    }

    /**
     * Установить прозрачность компонента.
     * 
     * @param isOpaque
     *            the new transparent
     */
    void setTransparent(boolean isOpaque) {
        setOpaque(isOpaque);
    }

    /**
     * Установка разделителей.
     * Считывает свойство таблицы, чистит его и парсит,
     * после чего пишет значения в переменную <i>separators[]</i>
     */
    private void setSeparates() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("buttons").getChild("naviSeparator"));
        if (!pv.isNull()) {
            // получить массив строк c индексами позиций кнопок, после которых необходим разделитель
            // строка подвергается предв обработке
            // все символы отличные от цифр(идущие подряд тоже) заменяются на пробел и по пробелу строка разбивается на массив
            String[] temp = pv.stringValue().replaceAll("\\D+", " ").split(" ");
            separators = new int[temp.length];
            for (int i = 0; i < temp.length; ++i) {
                try {
                    separators[i] = Integer.parseInt(temp[i]);
                } catch (Exception e) {
                    separators[i] = -1;
                }
            }
        } else {
            separators = null;
        }
    }

    public void setRowBackColorExpr() {
        PropertyNode prop = getProperties();
        PropertyNode pn = prop.getChild("view").getChild("background");
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("rowBackColorExpr");
            if (pn1 != null) {
                PropertyValue pv = getPropertyValue(pn1);
                if (!pv.isNull()) {
                    rowBackColorExpr = pv.stringValue();
                }
            }
        }
    }

    public void setRowFontColorExpr() {
        PropertyNode prop = getProperties();
        PropertyNode pn = prop.getChild("view").getChild("background");
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("rowFontColorExpr");
            if (pn1 != null) {
                PropertyValue pv = getPropertyValue(pn1);
                if (!pv.isNull()) {
                    rowFontColorExpr = pv.stringValue();
                }
            }
        }
    }

    public String getRowBackColorExpr() {
        return rowBackColorExpr;
    }

    public String getRowFontColorExpr() {
        return rowFontColorExpr;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject json = super.getJSON(isNaviExists ? navi : null, zebra1, zebra2);
        JsonObject property = (JsonObject) json.get("pr");
        if (isNaviExists && addPan != null) {
            property.set("panel", addPan.getJSON());
        }
        
        if (zebra1 != null || zebra2 != null){
        	JsonObject clm = new JsonObject();
            JsonArray zebra = new JsonArray();
            clm.add("zebra1", zebra1);
            clm.add("zebra2", zebra2);
            zebra.add(clm);        
            property.set("zebra", zebra);
        }
        property.set("nocopy", noCopy ? 1 : 0);
        int[] rows = getSelectedRows();
        if (rows != null && rows.length > 0) {
            adapter.getRef().absolute(rows[0], this);
        }
        RtWebTableModel m = (RtWebTableModel) getModel();
        JsonArray dialogs = new JsonArray();
        for (int i = 0; i < m.getColumnCount(); i++) {
            JsonObject obj = new JsonObject();
            if (m.getColumn(i) instanceof OrWebTreeColumn) {
                OrWebTreeColumn c = (OrWebTreeColumn) m.getColumn(i);
                OrWebTreeField tf = (OrWebTreeField) c.getEditor();
                obj.add("uuid", id);
                obj.add("indx", i);
                obj.add("title", c.getTitle());
                obj.add("width", mergeWidth(tf.getDialogWidth()));
                obj.add("height", mergeHeight(tf.getDialogHeight()));
                dialogs.add(obj);
            } else if (m.getColumn(i) instanceof OrWebDocFieldColumn) {
                OrWebDocFieldColumn c = (OrWebDocFieldColumn) m.getColumn(i);
                OrWebDocField df = (OrWebDocField) c.getEditor();
                if (df.getAdapter().getAction() == Constants.DOC_UPDATE) {
                    obj.add("uuid", id);
                    obj.add("indx", i);
                    obj.add("fotoHead", ((WebFrame) frame).getSession().getResource().getString("fileUploader"));
                    dialogs.add(obj);
                }
            } else if (m.getColumn(i) instanceof OrWebIntColumn) {
                JsonObject headerObj = new JsonObject();
                OrWebIntColumn c = (OrWebIntColumn) m.getColumn(i);
                headerObj.add("uid", c.uuid);
                headerObj.add("title", c.getTitle());
                obj.add("header", headerObj);
                dialogs.add(obj);
            }
        }
        property.set("dialogs", dialogs);
        
        if (autoRefreshMillis > 0)
        	json.add("autoRefresh", autoRefreshMillis);

        sendChange(json, isSend);
        
        removeChange("pr.updateTblRow");
        removeChange("pr.reload");
        return json;
    }
    
	public void removeChangeProperties() {
		super.removeChangeProperties();
        int count = getColumnCount();
        for (int i = 0; i < count; i++) {
            WebComponent c = (WebComponent)getColumnAt(i);
            if (c != null)
            	c.removeChangeProperties();
        }
	}
	
	public void setData(List<Object> data) {
		adapter.setData(data);
	}
	
	public int getColumnIndexByUID(String uuid) {
        int count = getColumnCount();
        for (int i = 0; i < count; i++) {
            WebComponent c = (WebComponent)getColumnAt(i);
            if (c != null && uuid.equals(c.uuid))
            	return i;
        }
		return -1;
	}
	
	public String getData(int page, int itemsOnPage, String sortCol, String sortOrder) {
    	if (sortCol != null && sortOrder != null) {
        	boolean sortChanged = (sortCol != null && prevSortCol == null)
        			|| (sortCol == null && prevSortCol != null)
        			|| (sortCol != null && prevSortCol != null && (!sortCol.equals(prevSortCol) || !sortOrder.equals(prevSortOrder)));
        	
        	prevSortCol = sortCol;
        	prevSortOrder = sortOrder;

        	if (sortChanged) {
        		String[] colUIDs = sortCol.split(",");
        		String[] colOrders = sortOrder.split(",");

	    		adapter.clearSort();
	    		for (int i = 0; i < colUIDs.length; i++) {
	    	        int col = getColumnIndexByUID(colUIDs[i]);
	    	        boolean direction = "asc".equals(colOrders[i]);
	
	    			adapter.addSortByColumn(col, direction);
	    		}
	    		adapter.sort();
	    		//tableDataChanged();
    		}
    	}
    	return super.getData(page, itemsOnPage, tableViewType);
    }

    public List<Object> getData() {
        return adapter.getData();
    }

    public void showDeleted() {
    	if (adapter.isEnabled()){
    		navi.actionPerformed("showDelBtn");
    	}
    }

    public void selectColumn(String value) {
        selCol = value;
    }

    public String getSelectValue() {
        return getValueAt(getSelectedRow(), getColumnIndexByUID(selCol)).toString();
    }
    
    public Object getSelectValueObj() {
        List<Item> si = getAdapter().getRef().getSelectedItems();
        return si == null || si.size() == 0 ? null : si.get(0);
    }
    
    public String getPathOfSelectedColumn() {
        OrColumnComponent c = (OrColumnComponent) ((WebFrame) frame).getComponentByUID(selCol);
        if (c != null) {
            ColumnAdapter adapter = c.getAdapter();
            if (adapter != null && adapter.getDataRef() != null) {
                return adapter.getDataRef().toString();
            }
        }
        return null;
    }

    public KrnAttribute getAttributeOfSelectedColumn() {
        OrColumnComponent c = (OrColumnComponent) ((WebFrame) frame).getComponentByUID(selCol);
        if (c != null) {
            ColumnAdapter adapter = c.getAdapter();
            if (adapter != null && adapter.getDataRef() != null) {
                return adapter.getDataRef().getAttr();
            }
        }
        return null;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
    
	public int getTableViewType() {
		return tableViewType;
	}
    
    public String getZebra1() {
		return zebra1;
	}
	
	public String getZebra2() {
		return zebra2;
	}
}
