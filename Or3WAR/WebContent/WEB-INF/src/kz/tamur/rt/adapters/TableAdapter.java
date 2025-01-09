package kz.tamur.rt.adapters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.TableColumn;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Filter;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableModel;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ProcessRecord;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList.MsgListItem;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.WebAction;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.OrWebTable;
import kz.tamur.web.component.OrWebTreeTable2;
import kz.tamur.web.component.RtWebTableModel;
import kz.tamur.web.component.WebFrame;

import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonObject;

public class TableAdapter extends ContainerAdapter implements DataCashListener {

    protected OrTableComponent table;
    protected int selRowIdx=-1, rowCount=-1;
    protected int selColumnIndex;

    private List<TableColumn> sortedColumns = new ArrayList<TableColumn>();
    private List<Integer> webSortedColumns = new ArrayList<Integer>();
    private List<Boolean> sortingDirection = new ArrayList<Boolean>();
    protected boolean isSort=false;
    private final ImageIcon columnUp = kz.tamur.rt.Utils.getImageIcon("SortUp");
    private final ImageIcon columnDown = kz.tamur.rt.Utils.getImageIcon("SortDown");
    private final ImageIcon columnUpRotateRight = kz.tamur.rt.Utils.getImageIcon("SortUpRotateRight");
    private final ImageIcon columnDownRotateRight = kz.tamur.rt.Utils.getImageIcon("SortDownRotateRight");
    private final ImageIcon columnUpRotateLeft = kz.tamur.rt.Utils.getImageIcon("SortUpRotateLeft");
    private final ImageIcon columnDownRotateLeft = kz.tamur.rt.Utils.getImageIcon("SortDownRotateLeft");
    private final ImageIcon yesManDown = kz.tamur.rt.Utils.getImageIcon("goDown");
    private final ImageIcon yesManRight = kz.tamur.rt.Utils.getImageIcon("goRight");
    private boolean autoCreateObject = true;
    private boolean yes_man = true;
    private boolean multiSelection = false;
    private int access;
    PropertyChangeSupport ps = new PropertyChangeSupport(this);
    protected ASTStart beforDelFX, beforAddFX, afterDelFX, afterAddFX, afterCopyFX, afterMoveFX;
    private boolean hackEnabled = true;

    protected OrCalcRef rowFontRef;
    private OrCalcRef rowBackgroundRef;
    private OrCalcRef rowForegroundRef;
    private Color zebraColor1;
    private Color zebraColor2;

    protected List<ColumnAdapter> columns = new ArrayList<ColumnAdapter>();
    private String langCode = ((WebFrame)frame).getSession().getLangCode();
    public ResourceBundle res = ResourceBundle.getBundle(
            Constants.NAME_RESOURCES, "KZ".equals(langCode)? new  Locale("kk") : new Locale("ru"));
    //private long ifcLangId;
    protected boolean canSort = true;
    private OrCalcRef delActivityRef;
    private List<ProcessRecordAction> processActions;
    protected boolean showDeleted = true;
    
    // переменки для последующей реализации постраничного вывода данных
    /** Номер текущей страницы */
    private int numberSelectPage;
    /** Cтраниц всего */
    private int countPage;
    /** максимальное количество записей на странице */
    private int countRowPage;

    /** Всего записей */
    private int countRow;
    /** Номер записи начала страниы */
    private int numberRowstartPage;
    /** Номер записи в конце страницы */
    private int numberRowEndPage;

    /**
     * Набор новых записей добавленных средствами таблицы.
     * Набор пополняется при каждом вызове addNewRow() и очищается при
     * вызове cacheCommitted() и cacheRollbacked(). Набор используется в методе
     * deleteEmptyRow() для определения необходимости в удалении пустой строки.
     * Таким обрзом перед сохранением данных формы удаляются только те пустые
     * строки, которые были созданы средствами этой таблицы.
     */
    private Set<Item> insertedItems = new HashSet<Item>();
    private List<Object> data = null;

    public TableAdapter(OrFrame frame, OrTableComponent table, int i, boolean isEditor) throws KrnException {
        this(frame, table, isEditor);
    }

    public TableAdapter(OrFrame frame, OrTableComponent table, boolean isEditor) throws KrnException {
        super(frame, table, isEditor);
        
        createRowFontRef(table);
        createColors(table);

        this.table = table;
        // Get Access
        PropertyNode prop = table.getProperties();
        
     // Поведение
        PropertyNode pov = prop.getChild("pov");
     // Ограничение кол-ва строк в таблице
        PropertyNode pn = pov.getChild("maxObjectCount");
        PropertyValue pv = table.getPropertyValue(pn);
        if (dataRef != null) {
	        dataRef.setColumn(false);
	        dataRef.getCash().addCashListener(this, frame);
	        if (!pv.isNull()) {
	        	dataRef.setLimit(pv.intValue());
	        }
        }
        // Get Access
        PropertyNode rprop = pov.getChild("access");
        pv = table.getPropertyValue(rprop);
        if (!pv.isNull()) {
            access = pv.intValue();
        }
        pv = table.getPropertyValue(pov.getChild("canSort"));
        if (pv != null && !pv.isNull()) {
            canSort = pv.booleanValue();
        } else {
            canSort = true;
        }

        pv = table.getPropertyValue(prop.getChild("ref").getChild("defaultFilter"));
        if (!pv.isNull()) {
            dataRef.setDefaultFilter(pv.filterValue().getObjId());
            frame.getKernel().addFilterParamListener(pv.filterValue().getKrnObject().getUID(), "", this);
        }
        pv = table.getPropertyValue(prop.getChild("ref").getChild("deletedRef"));
        if (!pv.isNull()) {
        	KrnClass cls = dataRef.getType();
        	KrnAttribute attr = null;
        	String v = pv.stringValue();
        	StringTokenizer t = new StringTokenizer(v, ".");
        	t.nextToken();
        	while (t.hasMoreTokens()) {
        		String name = t.nextToken();
        		attr = frame.getKernel().getAttributeByName(cls, name);
        		cls = frame.getKernel().getClass(attr.typeClassId);
        	}
            dataRef.setDeletedAttr(attr);
        }
        pv = table.getPropertyValue(pov.getChild("multiselection"));
        if (!pv.isNull()) {
            multiSelection = pv.booleanValue();
        }
        //formila for Before Add
        pn = pov.getChild("act");
        pv = table.getPropertyValue(pn.getChild("beforAdd"));
        String beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue(frame.getKernel());
        }
        if (beforExpr != null && beforExpr.length() > 0) {
            propertyName = "Свойство: Перед добавлением";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.BEFORE_ADD_TYPE;
                	beforAddFX = ClientOrLang.getStaticTemplate(ifcId, key, beforExpr, getLog());
            	} else {
            		beforAddFX = OrLang.createStaticTemplate(beforExpr, log);
            	}
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        String afterExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterAdd"));
        if (!pv.isNull()) {
            afterExpr = pv.stringValue(frame.getKernel());
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            propertyName = "Свойство: После добавления";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.AFTER_ADD_TYPE;
                	afterAddFX = ClientOrLang.getStaticTemplate(ifcId, key, afterExpr, getLog());
            	} else {
            		afterAddFX = OrLang.createStaticTemplate(afterExpr, log);
            	}
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        //formula for AfterDelete
        pv = table.getPropertyValue(pn.getChild("afterDelete"));
        afterExpr = null;
        if (!pv.isNull()) {
            afterExpr = pv.stringValue(frame.getKernel());
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            propertyName = "Свойство: После удаления";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.AFTER_DELETE_ROW_TYPE;
                	afterDelFX = ClientOrLang.getStaticTemplate(ifcId, key, afterExpr, getLog());
            	} else {
            		afterDelFX = OrLang.createStaticTemplate(afterExpr, log);
            	}
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }

        pv = table.getPropertyValue(pn.getChild("beforeDelete"));
        beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue(frame.getKernel());
        }
        if (beforExpr != null && beforExpr.length() > 0) {
            propertyName = "Свойство: Перед удалением";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.BEFORE_DELETE_ROW_TYPE;
                	beforDelFX = ClientOrLang.getStaticTemplate(ifcId, key, beforExpr, getLog());
            	} else {
            		beforDelFX = OrLang.createStaticTemplate(beforExpr, log);
            	}
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        String afterCopyExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterCopy"));
        if (!pv.isNull()) {
            afterCopyExpr = pv.stringValue(frame.getKernel());
        }
        if (afterCopyExpr != null && afterCopyExpr.length() > 0) {
            propertyName = "Свойство: После копирования";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.AFTER_COPY_TYPE;
                	afterCopyFX = ClientOrLang.getStaticTemplate(ifcId, key, afterCopyExpr, getLog());
            	} else {
            		afterCopyFX = OrLang.createStaticTemplate(afterCopyExpr, log);
            	}
                Editor e = new Editor(afterCopyExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        String afterMoveExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterMove"));
        if (!pv.isNull()) {
            afterMoveExpr = pv.stringValue(frame.getKernel());
        }
        if (afterMoveExpr != null && afterMoveExpr.length() > 0) {
            propertyName = "Свойство: После перемещения";
            try {
            	if (table instanceof WebComponent && frame instanceof WebFrame) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = ((WebComponent)table).getId() + "_" + OrLang.AFTER_MOVE_TYPE;
                	afterMoveFX = ClientOrLang.getStaticTemplate(ifcId, key, afterMoveExpr, getLog());
            	} else {
            		afterMoveFX = OrLang.createStaticTemplate(afterMoveExpr, log);
            	}
                Editor e = new Editor(afterMoveExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                log.error(ex, ex);
            }
        }
        createDelActivityRef(table);
        
        pv = table.getPropertyValue(prop.getChild("pov").getChild("activity").getChild("processes"));
        if (!pv.isNull()) {
            List<ProcessRecord> prs = pv.processRecordsValue();
            processActions = new ArrayList<ProcessRecordAction>(prs.size());
            for (ProcessRecord pr : prs) {
                processActions.add(new ProcessRecordAction(pr));
            }
        }
    }

    protected void createDelActivityRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode pn = rprop.getChild("activity");
            if (pn != null) {
                PropertyNode  pn1 = pn.getChild("activDelExpr");
                if (pn1 != null) {
                    PropertyValue pv = c.getPropertyValue(pn1);
                    String fx = "";
                    if (!pv.isNull() && !"".equals(pv.stringValue(frame.getKernel()))) {
                        try {
                            propertyName = "Свойство: Активность Удаление";
                            fx = pv.stringValue(frame.getKernel());
                            if (fx.trim().length() > 0) {
                                delActivityRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                delActivityRef.addOrRefListener(this);
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage() + fx);
                            log.error(e, e);
                        }
                    }
                }
            }
        }
    }

    public int getAccess() {
        return access;
    }

    public boolean isHackEnabled() {
    	return hackEnabled;
    }

    public List<ColumnAdapter> getColumnAdapters() {
    	return columns;
    }

    public void moveDown() {
        int i = table.getSelectedRow();
        try {
            dataRef.moveDown(this, i, this);
            if (table.getRowCount() > i + 1) {
                table.tableRowsUpdated(i, i+1);
                table.setSelectedRow(i+1);
                dataRef.absolute(i+1, this);
                dataRef.setSelectedItems(table.getSelectedRows());
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void moveUp() {
        int i = table.getSelectedRow();
        try {
            dataRef.moveUp(this, i, this);
            if (i > 0) {
                table.tableRowsUpdated(i - 1, i);
                table.setSelectedRow(i-1);
                dataRef.absolute(i-1, this);
                dataRef.setSelectedItems(table.getSelectedRows());
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void addColumnAdapter(ColumnAdapter a, int pos) {
    	if (pos == -1) {
    		columns.add(a);
            a.setIndex(columns.size() - 1);
            pos = columns.size() - 1;
    	} else {
    		columns.add(pos, a);
    		// ��������� �������� index ��� ���� ��������� ����� ������������
    		for (int i = pos; i < columns.size(); i++) {
    			columns.get(i).setIndex(i);
    		}
    	}
    	childrenAdapters.add(a);
        a.setTableAdapter(this);
        a.setIndex(pos);
        if (a instanceof ComboColumnAdapter) {
            if (a.dataRef == dataRef) {
                autoCreateObject = false;
            }
        } else if (a instanceof PopupColumnAdapter) {
            OrRef titleRef = ((PopupColumnAdapter)a).getTitleRef();
            if (titleRef != null && dataRef != null) {
                OrRef temp = titleRef;
                while (temp != null && !temp.toString().equals(dataRef.toString())) {
                    temp.setColumn(true);
                    temp = temp.getParent();
                }
            }
        }
        if (canSort && a.isSort() && a.getColumn().isCanSort()) {
            Boolean dir = a.getSortingDirection() == Constants.SORT_ASCENDING;
            int sind = a.getSortingIndex();

            if (table instanceof OrWebTable) {
                if (sind > webSortedColumns.size()) sind = webSortedColumns.size();
                webSortedColumns.add(sind, pos);
                sortingDirection.add(sind, dir);

                if (dir)
                    a.getColumn().setIconName("SortDown");
                else
                    a.getColumn().setIconName("SortUp");
            }
        }
        //@todo ����������� � �����������
/*
        if (a.isSort()) {
            Boolean dir = Boolean.TRUE;
            TableColumn column = table.getJTable().getColumnModel().getColumn(index);
            sortedColumns.add(column);
            sortingDirection.add(dir);
            JLabel renderer = (JLabel) column.getHeaderRenderer();
            if (dir.booleanValue())
                renderer.setIcon(COLUMN_DOWN);
            else
                renderer.setIcon(COLUMN_UP);
    }
*/
    }

    public void setSelectedRows(int[] rows) {
        if (selfChange || dataRef == null) {
            return;
        }
        int emptyRow = getEmptyRow();
        try {
            selfChange = true;
            if (emptyRow != -1 && Funcs.indexOf(emptyRow, rows) == -1) {
                // dataRef.deleteItem(TableAdapter.this, emptyRow, this);
                deleteEmptyRow(emptyRow);
                table.tableDataChanged();
            }
            if (rows.length > 0) {
                dataRef.absolute(rows[0], this);
                dataRef.setSelectedItems(rows);
            } else {
                dataRef.absolute(0, this);
                dataRef.setSelectedItems(new int[0]);
            }
        } catch (KrnException ex) {
            log.error(ex, ex);
        } finally {
            selfChange = false;
        }
    }

    public OrRef getRef() {
        return dataRef;
    }

    public long getLangId() {
        return dataRef.getLangId();
    }

    public OrTableComponent getTable() {
        return table;
    }

    public void removeSortColumn(TableColumn column) {
        int index = sortedColumns.indexOf(column);
        if (index > -1) {
            sortedColumns.remove(index);
            sortingDirection.remove(index);
            JLabel renderer = (JLabel) column.getHeaderRenderer();
            renderer.setIcon(null);
            sort();
        }
    }

    public boolean sortByColumn(int column) {
        int index = webSortedColumns.indexOf(column);
        Boolean dir = Boolean.TRUE;
        if (index > -1) {
            dir = sortingDirection.get(index);
            dir = !dir;
            sortingDirection.set(index, dir);
        } else {
            webSortedColumns.add(column);
            sortingDirection.add(dir);
        }
        sort();
        return dir;
    }

    public void clearSort() {
        webSortedColumns.clear();
        sortingDirection.clear();
    }

    public void addSortByColumn(int column, boolean dir) {
        webSortedColumns.add(column);
        sortingDirection.add(dir);
    }

    public void removeSortColumn(int column) {
        int index = webSortedColumns.indexOf(column);
        if (index > -1) {
            webSortedColumns.remove(index);
            sortingDirection.remove(index);
            sort();
        }
    }

    public void sort() {
        if ((sortedColumns != null && sortedColumns.size() > 0) ||
                (webSortedColumns != null && webSortedColumns.size() > 0)) {
        	if (dataRef != null) {
	        	long langId = getLangId();
	            List<Item> items = new ArrayList<OrRef.Item>(dataRef.getItems(langId));
	            int size = items.size();
	            List<SortableItem> sitems = new ArrayList<SortableItem>(size);
	            for (int i=0; i<size; i++)
	            	sitems.add(new SortableItem(i, items.get(i)));
	
	            Comparator<SortableItem> comparator = new WebItemSorter();
	            Collections.sort(sitems, comparator);
	
	            items.clear();
	            
	            for (int i=0; i<size; i++)
	            	items.add(sitems.get(i).getItem());
	
	            isSort=true;
	            dataRef.setItems(langId, items, this);
        	} else if (data != null) {
	            int size = data.size();
	            List<Object> sitems = new ArrayList<Object>(size);
	            for (int i=0; i<size; i++)
	            	sitems.add((Object)data.get(i));
	
	            Comparator<Object> comparator = new WebDataSorter();
	            Collections.sort(sitems, comparator);
	            
	            isSort=true;
	            data.clear();
	            for (int i=0; i<size; i++)
	            	data.add(sitems.get(i));
        	}
        } else {
            isSort=true;
        }
    }

    public boolean showDeleted() {
        showDeleted = !showDeleted;
        table.tableDataChanged();
        return showDeleted;
    }

    public int getRowCount() {
    	int count = (dataRef != null) ? dataRef.getItems(0).size() : data != null ? data.size() : 0;
    	if (!showDeleted)
    		count -= dataRef.getDeletedIndexes().size();
    	
    	return count;
    }

    public int webToRefIndex(int row) {
    	if (!showDeleted) {
    		for (int deletedRow : dataRef.getDeletedIndexes()) {
    			if (deletedRow <= row)
    				row++;
    			else
    				return row;
    		}
    	}
    	return row;
    }
    
    public int refToWebIndex(int row) {
    	if (!showDeleted) {
    		int i = 0;
    		for (int deletedRow : dataRef.getDeletedIndexes()) {
    			if (deletedRow < row)
    				i++;
    			else
    				break;
    		}
    		return row-i;
    	} else {
    	 	return row;
    	}
    }

    public void addPropertyChangeListener(PropertyChangeListener navi) {
        ps.addPropertyChangeListener(navi);
    }

    class ItemSorter implements Comparator<OrRef.Item> {

        public int compare(OrRef.Item a, OrRef.Item b) {
        	List<Item> items = dataRef.getItems(getLangId());
            int ind1 = identityIndexOf(items, a);
            int ind2 = identityIndexOf(items, b);
            boolean isId=true;
            if(a!=null && b!=null && a.getRec().getValue() instanceof KrnObject && b.getRec().getValue() instanceof KrnObject){
                  isId=((KrnObject)a.getRec().getValue()).id>
                          ((KrnObject)b.getRec().getValue()).id;
            }
            try {
                for (int i = 0; i < sortedColumns.size(); i++) {
                    int res = 0;
                    TableColumn column = sortedColumns.get(i);
                    boolean dir = sortingDirection.get(i);
                    ColumnAdapter c = ((OrTableModel) table.getModel()).getColumnAdapter(column.getModelIndex());
                    String typeColumn = (c.dataRef != null) 
                    	? c.dataRef.getType().name : "";
                    Object o1,o2;
                    if(c instanceof ComboColumnAdapter ||
                    		c instanceof CheckBoxColumnAdapter ||
                            c instanceof PopupColumnAdapter ||
                            c instanceof TreeColumnAdapter){
                        o1 = c.getValueAt(ind1);
                        o2 = c.getValueAt(ind2);
                    }else{
                        o1 = c.getObjectValueAt(ind1);
                        o2 = c.getObjectValueAt(ind2);
                    }
                    if (o1 == null && o2 == null) {
                        res = 0;
                    } else if (o1 == null) {
                        res = -1;
                    } else if (o2 == null) {
                        res = 1;
                    } else {
                        if (typeColumn.equals("long") ||
                                typeColumn.equals("float") ||
                                typeColumn.equals("double")) {
                            Number n1 = (Number) o1;
                            double d1 = n1.doubleValue();
                            Number n2 = (Number) o2;
                            double d2 = n2.doubleValue();
                            if (d1 < d2) {
                                res = -1;
                            } else if (d1 > d2) {
                                res = 1;
                            } else {
                                res = 0;
                            }
                        } else if (typeColumn.equals("date")||typeColumn.equals("time")) {
                            Date d11 = (Date)o1;
                            Date d22 = (Date)o2;
                            if (d11.compareTo(d22) < 0) {
                                res = -1;
                            } else if (d11.compareTo(d22) > 0) {
                                res = 1;
                            } else {
                                res = 0;
                            }
                        } else if (typeColumn.equals("string")) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;
                            res = s1.compareToIgnoreCase(s2);
                        } else if (typeColumn.equals("boolean")) {
                            Boolean bool1 = (Boolean) o1;
                            boolean b1 = bool1;
                            Boolean bool2 = (Boolean) o2;
                            boolean b2 = bool2;
                            if (b1 == b2) {
                                res = 0;
                            } else if (b1) {
                                res = 1;
                            } else {
                                res = -1;
                            }
                        } else {
                            String s1 = o1.toString();
                            String s2 = o2.toString();
                            res = s1.compareToIgnoreCase(s2);
                        }//        } else {

                    }
                    if (res != 0) {
                        return res * ((dir) ? 1 : -1);
                    }else if( i== sortedColumns.size()-1){
                        return (isId ? (dir?1:-1) :(dir?-1:1));
                    }
                }
                return 0;
            } catch (Exception e) {
                log.error("Ошибка! Преобразование в сортировке");
                return (0);
            }
        }
        
        private int identityIndexOf(List<Item> items, Item item) {
        	int sz = items.size();
        	for (int i = 0; i < sz; i++) {
        		if (item == items.get(i))
        			return i;
        	}
        	return -1;
        }
    }

    class WebItemSorter implements Comparator<SortableItem> {

        public int compare(SortableItem a, SortableItem b) {
            //List<Item> items = dataRef.getItems(getLangId());
            int ind1 = a.getIndex();
            int ind2 = b.getIndex();
            boolean isId=true;
            if(a!=null && b!=null && a.getItem() != null && b.getItem() != null
            		&& a.getItem().getRec().getValue() instanceof KrnObject && b.getItem().getRec().getValue() instanceof KrnObject){
                  isId=((KrnObject)a.getItem().getRec().getValue()).id>
                          ((KrnObject)b.getItem().getRec().getValue()).id;
            }
            try {
                for (int i = 0; i < webSortedColumns.size(); i++) {
                    int res = 0;
                    int column = webSortedColumns.get(i);
                    boolean dir = sortingDirection.get(i);
                    ColumnAdapter c = columns.get(column);
                    Object o1,o2;
                    if(c instanceof ComboColumnAdapter ||
                    		c instanceof CheckBoxColumnAdapter ||
                            c instanceof PopupColumnAdapter ||
                            c instanceof TreeColumnAdapter){
                        o1 = c.getValueAt(ind1);
                        o2 = c.getValueAt(ind2);
                    }else{
                        o1 = c.getObjectValueAt(ind1);
                        o2 = c.getObjectValueAt(ind2);
                    }
                    if (o1 == null && o2 == null) {
                        res = (ind1 > ind2) ? 1 : -1;
                    } else if (o2 == null) {
                        res = -1;
                    } else if (o1 == null) {
                        res = 1;
                    } else {
                        if (o1 instanceof Number) {
                            Number n1 = (Number) o1;
                            double d1 = n1.doubleValue();
                            Number n2 = (Number) o2;
                            double d2 = n2.doubleValue();
                            if (d1 < d2) {
                                res = -1;
                            } else if (d1 > d2) {
                                res = 1;
                            } else {
                                res = 0;
                            }
                        } else if (o1 instanceof Date) {
                            Date d11 = (Date)o1;
                            Date d22 = (Date)o2;
                            if (d11.compareTo(d22) < 0) {
                                res = -1;
                            } else if (d11.compareTo(d22) > 0) {
                                res = 1;
                            } else {
                                res = 0;
                            }
                        } else if (o1 instanceof String) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;
                            if (s1.length() == 0 && s2.length() == 0)
                                res = (ind1 > ind2) ? 1 : -1;
                            else if (s2.length() == 0)
                            	res = -1;
                            else if (s1.length() == 0)
                            	res = 1;
                            else
                            	res = s1.compareToIgnoreCase(s2);
                        } else if (o1 instanceof Boolean) {
                            Boolean bool1 = (Boolean) o1;
                            boolean b1 = bool1;
                            Boolean bool2 = (Boolean) o2;
                            boolean b2 = bool2;
                            if (b1 == b2) {
                                res = 0;
                            } else if (b1) {
                                res = 1;
                            } else {
                                res = -1;
                            }
                        } else {
                            String s1 = o1.toString();
                            String s2 = o2.toString();
                            res = s1.compareToIgnoreCase(s2);
                        }//        } else {

                    }
                    if (res != 0) {
                        return res * ((dir) ? 1 : -1);
                    }else if( i== webSortedColumns.size()-1){
                        return (isId ? (dir?1:-1) :(dir?-1:1));
                    }
                }
                return 0;
            } catch (Exception e) {
            	log.error("Ошибка! Преобразование в сортировке");
                return (0);
            }
        }
        
/*        private int identityIndexOf(List<Item> items, Item item) {
        	int sz = items.size();
        	for (int i = 0; i < sz; i++) {
        		if (item == items.get(i))
        			return i;
        	}
        	return -1;
        }
*/    }
    ////
    class WebDataSorter implements Comparator<Object> {

        public int compare(Object a, Object b) {
            try {
                for (int i = 0; i < webSortedColumns.size(); i++) {
                    int res = 0;
                    int column = webSortedColumns.get(i);
                    boolean dir = sortingDirection.get(i);

                    Object o1 = null; 
                    Object o2 = null;
                    if (a instanceof Object[]) {
                    	o1 = ((Object[])a)[column];
                    	o2 = ((Object[])b)[column];
                	} else { 
                    	o1 = ((List)a).get(column);
                    	o2 = ((List)b).get(column);
                	}
                    
                    if (o1 == null && o2 == null) {
                        res = 0;
                    } else if (o2 == null) {
                        res = -1;
                    } else if (o1 == null) {
                        res = 1;
                    } else {
                        if (o1 instanceof String) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;
                            if (s1.length() == 0 && s2.length() == 0)
                                res = 0;
                            else if (s2.length() == 0)
                            	res = -1;
                            else if (s1.length() == 0)
                            	res = 1;
                            else
                            	res = s1.compareToIgnoreCase(s2);
                        } else {
                            String s1 = o1.toString();
                            String s2 = o2.toString();
                            res = s1.compareToIgnoreCase(s2);
                        }
                    }
                    if (res != 0) {
                        return res * ((dir) ? 1 : -1);
                    }else if( i== webSortedColumns.size()-1){
                        return (dir?1:-1);
                    }
                }
                return 0;
            } catch (Exception e) {
            	log.error("Ошибка! Преобразование в сортировке");
                return (0);
            }
        }
    }
    
    public int addNewRow() {
        int result = -1;
        try {
            selfChange = true;
            int emptyRow = getEmptyRow();
            if (emptyRow > -1) {
                table.setSelectedRow(emptyRow);
            } else {
                // формула до вставки
            	boolean canAdd = true;
            	
                if (beforAddFX != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    /*ArrayList sitems = new ArrayList();
                    //sitems.add(dataRef.getItem(dataRef.getLangId(), sel));

                    ArrayList selVect = new ArrayList();
                    if (sitems.size() > 0) {
                        for (int k = 0; k < sitems.size(); k++) {
                            OrRef.Item item = (OrRef.Item) sitems.get(k);
                            if (item != null) {
                                KrnObject object = (KrnObject) item.getCurrent();
                                ClientObjectWrp warp = new ClientObjectWrp(object);
                                selVect.add(warp);
                            }
                        }
                        ArrayList selVector = new ArrayList();
                        vc.put("SELOBJS", selVector);
                    }   */
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(beforAddFX, vc, this, new Stack<String>());
                    } catch (Exception e) {
                        Util.showErrorMessage(table, e.getMessage(), res.getString("beforeAddAction"));
                    	log.error("Ошибка при выполнении формулы '" + res.getString("beforeAddAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                        log.error(e, e);
                	} finally {
        	            if (calcOwner)
        	            	OrCalcRef.makeCalculations();
                    }
                    Object ret = vc.get("RETURN");
                    canAdd = !(ret instanceof Number && ((Number) ret).intValue() == 0);
                }

                if (canAdd) {
		            if (dataRef != null) {
		                boolean calcOwner = OrCalcRef.setCalculations();
		                Item newItem = null;
		                try {
		                	newItem = dataRef.insertItem(-1, null, this, this, autoCreateObject);
		            	} catch (Exception e) {
		                    log.error(e, e);
		            	} finally {
		        			if (calcOwner)
		        				OrCalcRef.makeCalculations();
		            	}
		                // Пополняем набор записей созданных средствами этой таблицы
		                insertedItems.add(newItem);
		            } else if (data != null) {
		            	int size = getColumnAdapters().size();
		            	List<String> l = Arrays.asList(new String[size]);
		            	data.add(l);
		            }
		            
		            int sel = (dataRef != null) ? dataRef.getItems(dataRef.getLangId()).size() - 1
		            		: (data != null) ? data.size() - 1 : 0;
		            table.tableRowsInserted(sel,sel);
		            table.setSelectedRow(sel);
		            ps.firePropertyChange("rowSelected", selRowIdx, sel);
		            selRowIdx = sel;
		            if (dataRef != null) dataRef.absolute(sel, this);
		            countCurrentTableItem();
		            
		            if (afterAddFX != null) {
		                List<OrRef.Item> sitems = new ArrayList<OrRef.Item>();
		                sitems.add(dataRef.getItem(dataRef.getLangId(), sel));
		                ClientOrLang orlang = new ClientOrLang(frame);
		                Map<String, Object> vc = new HashMap<String, Object>();
		                List<Object> selVect = new ArrayList<Object>();
		                if (sitems.size() > 0) {
		                    for (int k = 0; k < sitems.size(); k++) {
		                        OrRef.Item item = sitems.get(k);
		                        if (item != null) {
		                            selVect.add((KrnObject) item.getCurrent());
		                        }
		                    }
		                    vc.put("SELOBJS", selVect);
		                }
		            	boolean calcOwner = OrCalcRef.setCalculations();
		                try {
		                    orlang.evaluate(afterAddFX, vc, this, new Stack<String>());
		                } catch (Exception e) {
		                    Util.showErrorMessage(table, e.getMessage(), res.getString("afterAddAction"));
		                } finally {
		                    if (calcOwner)
		                    	OrCalcRef.makeCalculations();
		                }
		            }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            selfChange = false;
        }
        return result;
    }

    public int getEmptyRow() {
    	if (dataRef != null) {
	        int upperRowCount = table.getRowCount();
	        int upperColumnCount = table.getColumnCount();
	        try {
		        for (int l = 0; l < upperRowCount; l++) {
		            OrRef.Item item = dataRef.getItems(getLangId()).get(l);
		            if (item.getRec() == null) continue;
		            Object obj_ = item.getRec().getValue();
		            if ((obj_ instanceof KrnObject) && ((KrnObject) obj_).id > 0) continue;
		            int itemsNull = 0;
		            for (int j = 0; j < upperColumnCount; j++) {
		                if (table.getValueAt(l, j) == null) {
		                    itemsNull = itemsNull + 1;
		                }
		                if (table.getValueAt(l, j) != null) {
		                    try {
		                        ColumnAdapter c = ((OrTableModel) table.getModel()).getColumnAdapter(j);
		                        if (c != null) {
			                        String val_c = table.getValueAt(l, j).toString().trim();
			                        OrRef ref = c.dataRef;
			                        String typeName = (ref != null) ? ref.getType().name : null;
			                        boolean flagEmptyRow = val_c.equals("")
			                                || val_c.equals("false")
			                                || val_c.equals("0")
			                                || val_c.equals("0.0")
			                                || "HiperColumn".equals(typeName)
			                                || "SeqColumn".equals(typeName);
			                        if (flagEmptyRow) {
			                            itemsNull = itemsNull + 1;
			                        }
		                        }
		                    } catch (KrnException e) {
		                        log.error(e, e);
		                    }
		                }
		            }
		            if (itemsNull == upperColumnCount) return l;
		        }
	        } catch (Throwable e) {
	        	log.error(e, e);
	        }
    	}
        return -1;
    }
    public void deleteRow() {
        deleteRow(table.getSelectedRows());
    }
    
    // Delete Row NEW OR3
    public JsonObject deleteRow(int[] rows, boolean sure) {
        try {
            if (rows != null && rows.length > 0) {
	            if (!sure) {
		            String msg = "";
		            if (rows.length > 1) {
		                msg = res.getString("deleteRowsConfirm");
		                msg = msg.replaceAll("%1%", String.valueOf(rows.length));
		            } else if (rows.length == 1) {
		                msg = res.getString("deleteRowConfirm");
		                msg = msg.replaceAll("%1%", String.valueOf(rows[0] + 1));
		            } else
		                msg = "not selected";
		            
		            return new JsonObject().add("result", "success").add("message", msg.replaceFirst("^\\!", ""));
	            } else {
	            	List<Object> selVect = new ArrayList<Object>();
		        	Map<Object, Integer> sitems = new HashMap<Object, Integer>();
		        	for(int i : rows) {
		        		i = webToRefIndex(i);
		        		if (dataRef != null) {
			        		OrRef.Item item = dataRef.getItem(dataRef.getLangId(), i);
			        		if (item != null) {
				                sitems.put(item.getCurrent(), i);
				                selVect.add(item.getCurrent());
			        		}
		        		} else if (data != null) {
			        		Object item = data.get(i);
			        		if (item != null) {
				                sitems.put(item, i);
				                selVect.add(item);
			        		}
		        		}
		        	}
		        	
		            if (sitems.size() > 0) {
		                // формула перед удалением
		                boolean canDel = doBeforeDel(selVect);
		                if (canDel) {
				            sitems.keySet().retainAll(selVect);
				            Comparator<Integer> revCmp = new Comparator<Integer>() {
								public int compare(Integer o1, Integer o2) {
									return o2.compareTo(o1);
								}
				            };
							Set<Integer> inds = new TreeSet<Integer>(revCmp);
				            inds.addAll(sitems.values());
				
			                boolean calcOwner = OrCalcRef.setCalculations();
			                try {
					            for (int j : inds) {
					                if (dataRef != null) {
					                	dataRef.deleteItem(this, j, this);
					                } else if (data != null) {
					                	data.remove(j);
					                }
					                table.tableRowsDeleted(j, j);
					                if (j - 1 >= 0) {
					                    table.setSelectedRow(j - 1);
					                    int[] selRows = table.getSelectedRows();
					                    for (int i=0; i < selRows.length; i++) {
					                    	selRows[i] = webToRefIndex(selRows[i]);
					                    }
					                    setSelectedRows(selRows);

			/*		                    ps.firePropertyChange("rowSelected", selRowIdx, j - 1);
					                    selRowIdx = j - 1;
					                    int count = dataRef.getItems(dataRef.getLangId()).size() - 1;
					                    ps.firePropertyChange("rowCont", rowCount, count);
					                    rowCount = count;
			*/		                }
					                countCurrentTableItem();
					            }
			            	} catch (Exception e) {
			                    log.error(e, e);
			            	} finally {
			        			if (calcOwner)
			        				OrCalcRef.makeCalculations();
			            	}
		                }
		                // формула после удаления
		                doAfterDel(selVect);
		            }
	            }
        	} else {
	            return new JsonObject();
        	}
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public void deleteRow(int[] rows) {
        try {
            int[] selIdx = table.getSelectedRows();
            String msg = "";
            if (selIdx.length > 1) {
                msg = res.getString("deleteRowsConfirm");
                msg = msg.replaceAll("%1%", String.valueOf(selIdx.length));
            } else if (selIdx.length == 1) {
                msg = res.getString("deleteRowConfirm");
                msg = msg.replaceAll("%1%", String.valueOf(selIdx[0] + 1));
            } else
                return;
            
            int res = ((WebFrame)getFrame()).confirm(msg);
            
            log.info("res = " + res);
            
            if (res == ButtonsFactory.BUTTON_YES) {
            	List<Object> selVect = new ArrayList<Object>();
	        	Map<Object, Integer> sitems = new HashMap<Object, Integer>();
	        	for(int i : rows) {
	        		i = webToRefIndex(i);
	        		OrRef.Item item = dataRef.getItem(dataRef.getLangId(), i);
	        		if (item != null) {
		                sitems.put(item.getCurrent(), i);
		                selVect.add(item.getCurrent());
	        		}
	        	}
	        	
	            if (sitems.size() > 0) {
	                // формула перед удалением
	                boolean canDel = doBeforeDel(selVect);
	                if (canDel) {
			            sitems.keySet().retainAll(selVect);
			            Comparator<Integer> revCmp = new Comparator<Integer>() {
							public int compare(Integer o1, Integer o2) {
								return o2.compareTo(o1);
							}
			            };
						Set<Integer> inds = new TreeSet<Integer>(revCmp);
			            inds.addAll(sitems.values());
			
		                boolean calcOwner = OrCalcRef.setCalculations();
		                try {
				            for (int j : inds) {
				                dataRef.deleteItem(this, j, this);
				                table.tableRowsDeleted(j, j);
				                if (j - 1 >= 0) {
				                    table.setSelectedRow(j - 1);
		/*		                    ps.firePropertyChange("rowSelected", selRowIdx, j - 1);
				                    selRowIdx = j - 1;
				                    int count = dataRef.getItems(dataRef.getLangId()).size() - 1;
				                    ps.firePropertyChange("rowCont", rowCount, count);
				                    rowCount = count;
		*/		                }
				                countCurrentTableItem();
				            }
		            	} catch (Exception e) {
		                    log.error(e, e);
		            	} finally {
		        			if (calcOwner)
		        				OrCalcRef.makeCalculations();
		            	}
	                }
	                // формула после удаления
	                doAfterDel(selVect);
	            }
        	}
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    private void doAfterDel(List<Object> selVect) {
        if (afterDelFX != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", selVect);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterDelFX, vc, this, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(table, e.getMessage(), this.res.getString("afterDeleteAction"));
            	log.error("Ошибка при выполнении формулы '" + res.getString("afterDeleteAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
        	} finally {
	            if (calcOwner)
	            	OrCalcRef.makeCalculations();
            }
        }
    }


    /**
     * Копировать выбранные строки в одном экземпляре
     */
    public void copyRows() {
        int[] indexRows = table.getSelectedRows();
        copyRows(indexRows, 1);
        
    }

    /**
     * Copy rows.
     *
     * @param rows копируемые строки
     * @param count количество
     */
    public void copyRows(int[] rows, int count) {
        try {
            if (dataRef.getItems(dataRef.getLangId()).size() > 0) {
                if (rows.length > 0) {
                    List<Object> newObjs = new ArrayList<Object>();

                    for (int k = 1; k <= count; k++) {
                        for (int row : rows) {
                        	boolean calcOwner = OrCalcRef.setCalculations();
                        	Item item = null;
                            try {
                            	item = dataRef.insertItem(-1, null, this, this, true);
                        	} catch (Exception e) {
                        		throw e;
                        	} finally {
                    			if (calcOwner)
                    				OrCalcRef.makeCalculations();
                        	}
                            newObjs.add((KrnObject) item.getCurrent());
                            dataRef.absolute(row, this);
                            List<ColumnAdapter> cas = columns;
                            for (ColumnAdapter ca : cas) {
                                OrRef columnRef = ca.getRef();
                                columnRef.copy(item, this);
                            }
                        }
                    }
                    int count_ = dataRef.getItems(dataRef.getLangId()).size() - 1;
                    table.tableRowsInserted(count_, count_);
                    
                    dataRef.absolute(count_, null);
                    countCurrentTableItem();

                    if (afterCopyFX != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("SELOBJS", newObjs);
                    	boolean calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(afterCopyFX, vc, this, new Stack<String>());
                        } catch (Exception e) {
                            Util.showErrorMessage(table, e.getMessage(), res.getString("afterCopyAction"));
                        	log.error("Ошибка при выполнении формулы '" + res.getString("afterCopyAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                            log.error(e, e);
                    	} finally {
            	            if (calcOwner)
            	            	OrCalcRef.makeCalculations();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    public void mpveDown() {
        int[] indexRows = table.getSelectedRows();
        try {
            if (dataRef.getItems(dataRef.getLangId()).size() > 0) {
                if (indexRows.length > 0) {
                    int count = dataRef.getItems(dataRef.getLangId()).size();
                    table.tableRowsInserted(count-1, count-1);
                    dataRef.absolute(count-1, null);
                    countCurrentTableItem();
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    public boolean yesMan() {
        yes_man = !yes_man;
        return yes_man;
    }

    public boolean isYesMan() {
    	return yes_man;
    }

    public void applyFilter(Filter filter) throws KrnException {
        dataRef.addFilter(filter);
    }

    public void applyFilters(List<Filter> filters) throws KrnException {
        dataRef.addFilters(filters);
    }

    public void cancelFilterAction() {
        try {
            dataRef.removeAllFilters(false);
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        
        if (e.getRef() == delActivityRef && getTable().getRowCount() > 0) {
            Object val = delActivityRef.getValue(langId);
            if (val instanceof Number) {
                boolean enable = (((Number) val).intValue() == 1);
                getTable().setDelEnabled(enable);
            }
        }
        
        if (e.getOriginator() instanceof ReportPrinterAdapter.ReportNode || e.getOriginator() instanceof OrCalcRef) return;
        if (e.getOriginator() instanceof ReportPrinterAdapter) {
            countCurrentTableItem();
            return;
        }

        if (delActivityRef == null && hackEnabled) {
        	if (isEnabled() && getTable().getRowCount() > 0 && !getTable().isDelEnabled())
        		getTable().setDelEnabled(true);
        }

        if (e.getRef() == dataRef && paramFiltersUIDs != null && paramFiltersUIDs.length > 0) {
            for (int i = 0; i < paramFiltersUIDs.length; i++) {
                String paramFiltersUID = paramFiltersUIDs[i];
                try {
                    OrRef.Item item = dataRef.getItem(langId);
                    Object obj = (item != null) ? item.getCurrent() : null;
                    frame.getKernel().setFilterParam(paramFiltersUID, paramName, Collections.singletonList(obj));
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //������� ����� �����������
        if (afterMoveFX != null) {
        	Item item = dataRef.getItem(langId);
        	if (item != null) {
        		List<Object> selVect = new ArrayList<Object>();
            	selVect.add(item.getCurrent());
            	ClientOrLang orlang = new ClientOrLang(frame);
                boolean calcOwner = OrCalcRef.setCalculations();
            	try {
            		Map<String, Object> vc = new HashMap<String, Object>();
                    vc.put("SELOBJS", selVect);
                    orlang.evaluate(afterMoveFX, vc, this, new Stack<String>());
            	} catch (Exception ex) {
            		Util.showErrorMessage(table, ex.getMessage(), res.getString("afterMoveAction"));
                	log.error("Ошибка при выполнении формулы '" + res.getString("afterMoveAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(ex, ex);
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
            	}
        	}
        }
        if (e.getOriginator() != this && !selfChange) {
            OrRef ref = e.getRef();
            if (ref == dataRef && !isSort && e.getReason() != OrRefEvent.ITERATING) {
                sort();
            }
            if (e.getReason() == OrRefEvent.ITERATING) {
                if (!(this instanceof TreeTableAdapter) && !(this instanceof TreeTableAdapter2)) {
                    int i = dataRef.getIndex();
                    getTable().setSelectedRow(i);
                }
            } else {
                try {
                    selfChange = true;
                    if (!(this instanceof TreeTableAdapter2))
                    	table.tableDataChanged();
                    int i = dataRef.getIndex();
                    if (!(this instanceof TreeTableAdapter) && !(this instanceof TreeTableAdapter2 && i >= getTable().getRowCount()))
                    	getTable().setSelectedRow(i);
                    if (dataRef.getLimit() != 0) {
                    	table.setLimitExcceded(dataRef.isLimitExceeded());
                    }
                } finally {
                    selfChange = false;
                }
            }
            
            if(table instanceof OrWebTreeTable2) {
                	if (multiSelection && ref == dataRef && (e.getReason() == OrRefEvent.CHANGED || e.getReason() == OrRefEvent.ROOT_ITEM_CHANGED)) {
                		((OrWebTreeTable2) table).clearRowSelection();
                }
			}            
        }
        countCurrentTableItem();
        
        //если строк в таблице нет то и кнопка удаления должна быть не доступна
    	if (getTable().getRowCount() == 0 && getTable().isDelEnabled())
            getTable().setDelEnabled(false);
    }

    public void beforeCommitted() {
        int emptyRow = getEmptyRow();
        if (emptyRow > -1)
            try {
            	deleteEmptyRow(emptyRow);
            } catch (KrnException e) {
                log.error(e, e);
            }
    }

    public void cashCommitted() {
    	insertedItems.clear();
    }

    public void cashRollbacked() {
    	insertedItems.clear();
    }

    public void cashCleared() {
        isSort=false;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        hackEnabled = enabled;
        if (table != null) table.setEnabled(enabled);
    }

    protected void createRowFontRef(OrGuiComponent c) {
        String expr = null;
        PropertyNode pn = c.getProperties().getChild("view").getChild("background");
        if (pn != null) {
            pn = pn.getChild("rowFontExpr");
            if (pn != null) {
                PropertyValue pv = c.getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue().trim();
                }
            }
        }
        if (expr != null && expr.length() > 0) {
            try {
                propertyName = "Свойство: Шрифт (формула) строки";
                rowFontRef = new OrCalcRef(expr, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c,
                        propertyName, this);
                rowFontRef.addOrRefListener(this);
                rowFontRef.setTableRef(dataRef);
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                log.error(e, e);
            }
        }
    }

    protected void createColors(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode bgProp = prop.getChild("view").getChild("background");
        // ���� ���� (�������)
        PropertyValue pv = c.getPropertyValue(bgProp.getChild("rowBackColorExpr"));
        if (!pv.isNull()) {
        	String expr = pv.stringValue(frame.getKernel());
            try {
                propertyName = "Свойство: Цвет фона";
                if (expr.trim().length() > 0) {
                    rowBackgroundRef = new OrCalcRef(expr, true, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, c, propertyName, this);
                    rowBackgroundRef.addOrRefListener(this);
                    rowBackgroundRef.setTableRef(dataRef);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                log.error(e, e);
            }
        }
        // ���� ������ (�������)
        pv = c.getPropertyValue(bgProp.getChild("rowFontColorExpr"));
        if (!pv.isNull()) {
            String expr = pv.stringValue(frame.getKernel());
            try {
                propertyName = "Свойство: Цвет шрифта";
                if (expr.trim().length() > 0) {
                    rowForegroundRef = new OrCalcRef(expr, true, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, c, propertyName, this);
                    rowForegroundRef.addOrRefListener(this);
                    rowForegroundRef.setTableRef(dataRef);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                log.error(e, e);
            }
        }
        // �����
        PropertyNode zebraProp = bgProp.getChild("zebra");
        // ����� (���� 1)
        pv = c.getPropertyValue(zebraProp.getChild("color1"));
        if (!pv.isNull()) {
            zebraColor1 = pv.colorValue();
        }
        // ����� (���� 2)
        pv = c.getPropertyValue(zebraProp.getChild("color2"));
        if (!pv.isNull()) {
            zebraColor2 = pv.colorValue();
        }
    }

    public void setSort(boolean sort) {
        isSort = sort;
    }

    public long getInterfaceLangId() {
    	return langId;
    }

    public boolean getMultiSelection() {
    	return multiSelection;
    }

    public Color getZebraColor1() {
    	return zebraColor1;
    }

    public Color getZebraColor2() {
    	return zebraColor2;
    }

    public OrCalcRef getRowBgRef() {
    	return rowBackgroundRef;
    }

    public OrCalcRef getRowFgRef() {
    	return rowForegroundRef;
    }

    
    public void firstPage() {

    }

    public void backPage() {

    }

    public void nextPage() {

    }

    public void lastPage() {

    }

    public void setInterfaceLangId(long langId) {
        this.langId = langId;
        LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame)frame).getSession().getConfigNumber());
        if ("KZ".equals(li.code)) {
            res = ResourceBundle.getBundle(
                    Constants.NAME_RESOURCES, new Locale("kk"));
        } else {
            res = ResourceBundle.getBundle(
                    Constants.NAME_RESOURCES, new Locale("ru"));
        }
        for (int i = 0; i < columns.size(); i++) {
            ColumnAdapter ca = columns.get(i);
            ca.getColumn().setLangId(langId);
        }
    }

    protected int moveToRow(int i) {
        table.setSelectedRow(i);
        countCurrentTableItem();
        return i;
    }

    public void countCurrentTableItem() {
    	int count = (dataRef != null) ? dataRef.getItems(dataRef.getLangId()).size() - 1
        		: (data != null) ? data.size() - 1 : 0;
        ps.firePropertyChange("rowCont", rowCount, count);
        rowCount = count;
        int[] sels = table.getSelectedRows();
        int sel = -1;
        if (sels != null && sels.length > 0) sel = sels[0];
        if(count<0 ){
            selRowIdx=0;
            sel=-1;
        }
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
    }

    public boolean isCanSort() {
        return canSort;
    }

    /**
     * Удаляет пустую строку если она была создана средствами этой таблицы.
     * 
     * @param row
     *            номер пустой строки.
     * @return true если строка была удалена, false в противном случае.
     * @throws KrnException
     */
    protected boolean deleteEmptyRow(int row) throws KrnException {
    	long langId = dataRef.getLangId();
    	Item item = dataRef.getItem(langId, row);
    	if (item != null && insertedItems.contains(item)) {
            // Выполняем действия перед удалением
            List<Object> selVect = new ArrayList<Object>();
            selVect.add(item.getCurrent());
            doBeforeDel(selVect);

            if (selVect.size() > 0) {
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                	dataRef.deleteItem(this, row, this);
            	} catch (Exception e) {
                    log.error(e, e);
            	} finally {
        			if (calcOwner)
        				OrCalcRef.makeCalculations();
            	}
                return true;
            }
    	}
    	return false;
    }
    
    private boolean doBeforeDel(List<Object> selVect) {
    	boolean canDel = true;
        if (beforDelFX != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", selVect);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforDelFX, vc, this, new Stack<String>());
            } catch (Exception e) {
                Util.showErrorMessage(table, e.getMessage(), this.res.getString("beforeDeleteAction"));
            	log.error("Ошибка при выполнении формулы '" + res.getString("beforeDeleteAction") + "' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
        	} finally {
	            if (calcOwner)
	            	OrCalcRef.makeCalculations();
            }
            Object ret = vc.get("RETURN");
            canDel = !(ret instanceof Number && ((Number) ret).intValue() == 0);
        }
        return canDel;
    }
    
    private class SortableItem {
    	private int index;
    	private Item item;

		public SortableItem(int index, Item item) {
			super();
			this.index = index;
			this.item = item;
		}
		public int getIndex() {
			return index;
		}
		public Item getItem() {
			return item;
		}
    }
    
    public List<ProcessRecordAction> getActions() {
        return processActions;
    }
    /**
     * Установить максимально возможное количество записей на страницу
     * @param countRowPage новое количество записей
     */
    public void setCountRowPage(int countRowPage) {
        this.countRowPage = countRowPage;
        if (dataRef != null  && countRowPage != -1) {
            dataRef.setLimit(countRowPage);
        } 
    }
    
    public class ProcessRecordAction extends WebAction implements OrRefListener {
        private ProcessRecord record;
        private OrCalcRef enabledRef;
        private OrCalcRef visibleRef;
        private ASTStart template;
        
        private String name;
        private String icon;
        private String description;

        public ProcessRecordAction(ProcessRecord record) throws KrnException {
            super();
            setId(((WebFrame)frame).getObj().id + "_" + ((WebFrame)frame).getSession().getNextId());
            ((WebFrame)frame).getSession().addAction(this);
            this.record = record;
            this.name = (String) (record.getShortName() == null ? "" : record.getShortName().second);
            this.description = (String) (record.getShortName() == null ? "" : record.getShortName().second);
            BufferedImage image = record.getImage();
            if (image != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "PNG", baos);
                    baos.flush();
                    byte[] resultImageAsRawBytes = baos.toByteArray();
                    baos.close();
                    icon = com.cifs.or2.client.Utils.createFileImg(resultImageAsRawBytes,"ico");
                } catch (Exception e) {
                	log.error(e, e);
                }
            }

            Expression expr = record.getEnabledExpr();
            if (expr != null) {
                try {
                    propertyName = "Доступность контекстного меню";
                    enabledRef = new OrCalcRef(expr.text, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame, table, propertyName, TableAdapter.this);
                    enabledRef.setTableRef(dataRef);
                    enabledRef.addOrRefListener(this);
                    
                   /* // valueChanged срабатывает уже после того как по action создан, поэтому необходимо здесь устанавливать активность 
                    boolean enabled = false;
                    Item item = enabledRef.getItem();
                    if (item != null) {
                        Object v = item.getCurrent();
                        if (v instanceof Boolean)
                            enabled = ((Boolean) v).booleanValue();
                        else if (v instanceof Number) {
                            enabled = ((Number) v).intValue() == 1;
                        }
                    }
                    setEnabled(enabled);*/
                    
                } catch (Exception e) {
                    showErrorNessage(e.getMessage());
                    log.error(e, e);
                }
            }

            expr = record.getVisibleExpr();
            if (expr != null) {
                try {
                    propertyName = "Видимость контекстного меню";
                    visibleRef = new OrCalcRef(expr.text, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame, table, propertyName, TableAdapter.this);
                    visibleRef.setTableRef(dataRef);
                    visibleRef.addOrRefListener(this);
                } catch (Exception e) {
                    showErrorNessage(e.getMessage());
                    log.error(e, e);
                }
            }

            expr = record.getActionExpr();
            if (expr != null) {
                template = OrLang.createStaticTemplate(expr.text, log);
                Editor e = new Editor(expr.text);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            }

        }

        public void setInterfaceLangId(long langId) {
            String str = frame.getString(record.getShortName() == null ? "" : record.getShortName().first);
            this.name = str;
            this.description = str;
            List<WebButton> listBtn = ((OrWebTable)table).getNavi().getProcessButtons();
            for (WebButton btn :listBtn) {
                if (getId() == btn.getActionId()) {
                    btn.setToolTipText(str);
                }
            }
        }

        public void makeAction() {
            if (record.getKrnObject() != null) {
                List<Item> items = getDataRef().getSelectedItems();
                if (items.size() > 0) {
                    List<Object> values = new ArrayList<Object>(items.size());
                    for (Item item : items)
                        values.add(item.getCurrent());
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put("OBJS", values);
                    try {
                        String[] res = ((WebFrame) frame).getKernel().startProcess(record.getKrnObject().id, vars);
                        if (res.length > 0 && !res[0].equals("")) {
                            ((WebPanel) frame.getPanel()).setAlertMessage(res[0], false);
                        } else {
                            List<String> param = new ArrayList<String>();
                            param.add("autoIfc");
                            if (res.length > 3) {
                                param.add(res[3]);
                            }
                            ((WebFrame) frame).getSession().getTaskHelper().startProcess(res[1], param);
                        }
                    } catch (KrnException ex) {
                        log.error(ex, ex);
                    } catch (ProcessException ex) {
                        log.error(ex, ex);
                    }
                }
            } else if (template != null) {
                ClientOrLang jep = new ClientOrLang(frame);
                // jep.setComponent(table);
                Map vc = new HashMap();
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                    jep.evaluate(template, vc, TableAdapter.this, new Stack<String>());
                } catch (ProcessException e) {
                    log.error(e, e);
                    Util.showInformMessage(table, e.getMessage());
                } catch (Exception ex) {
                    log.error(ex, ex);
                    Util.showErrorMessage(table, ex.getMessage(), "Выражение");
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
                }
                if (table instanceof OrWebTable) {
                 //   ((OrWebTable) table).isStopWait = true;
                }
            }
        }

        public void valueChanged(OrRefEvent e) {
            OrRef ref = e.getRef();
            if (enabledRef == ref) {
                boolean enabled = false;
                Item item = enabledRef.getItem();
                if (item != null) {
                    Object v = item.getCurrent();
                    if (v instanceof Boolean)
                        enabled = ((Boolean) v).booleanValue();
                    else if (v instanceof Number) {
                        enabled = ((Number) v).intValue() == 1;
                    }
                }
                setEnabled(enabled);
            } else if (visibleRef == ref) {
            }
        }

        public void changesCommitted(OrRefEvent e) {
        }

        public void changesRollbacked(OrRefEvent e) {
        }

        public void pathChanged(OrRefEvent e) {
        }

        public void checkReqGroups(OrRef ref, List<MsgListItem> errMsgs, List<MsgListItem> reqMsgs, Stack<Pair> locs) {
        }

        public void clear() {
        }

        public void stateChanged(OrRefEvent e) {
        }

		public String getIcon() {
			return icon;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
    }
    
	public void setData(List<Object> data) {
		this.data = data;
		table.tableDataChanged();
	}

	public List<Object> getData() {
		return data;
	}
	
    public boolean checkUnique(String val, int rowID, int col) {
        if (table != null) {
            if (table.getModel() instanceof RtWebTableModel) {
                Map uinMap = ((RtWebTableModel) table.getModel()).getUniqueMap();
                if (uinMap != null) {
                    Integer uin = ((RtWebTableModel) table.getModel()).getColumn(col).getAdapter().getUniqueIndex();
                    if (uinMap.containsKey(uin)) {
                        ArrayList cls = (ArrayList) uinMap.get(uin);
                        int rows = table.getRowCount();
                        String[] values = new String[rows];
                        for (int r = 0; r < rows; r++) {
                            values[r] = "";
                            for (int i = 0; i < cls.size(); i++) {
                                int c = ((Integer) cls.get(i)).intValue();
                                if (rowID == r && c == col) {
                                    values[r] += val;
                                } else {
                                    values[r] += table.getValueAt(r, c);
                                }
                            }
                        }
                        String control_str = values[rowID];
                        for (int r = 0; r < rows; r++) {
                            if (r == rowID || values[r] == null || values[r].equals("null") || values[r].equals("")) {
                                continue;
                            }
                            if (values[r].equals(control_str)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return true;
    }
    
    public int getColumnIndex(OrColumnComponent column) {
        List<ColumnAdapter> cadapters = getColumnAdapters();
        return cadapters.indexOf(column.getAdapter());
    }

	public OrCalcRef getDelActivityRef() {
		return delActivityRef;
	}
	
    public void filterParamChanged(String fuid, String pid, List<?> values) {
    	if (frame!= null && frame.equals(frame.getInterfaceManager().getCurrentFrame())) {
	        try {
	        	dataRef.refresh(this);
	        } catch (Exception e) {
	    		log.error(e, e);
	        }
    	}
    }

    public void clearParam() {
    	if (frame!= null && frame.equals(frame.getInterfaceManager().getCurrentFrame())) {
	        try {
	        	dataRef.refresh(this);
	        } catch (Exception e) {
	    		log.error(e, e);
	        }
    	}
    }

}
