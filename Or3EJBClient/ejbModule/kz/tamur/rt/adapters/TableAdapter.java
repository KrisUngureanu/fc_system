package kz.tamur.rt.adapters;

import static kz.tamur.comps.Constants.DONT_ROTATE;
import static kz.tamur.comps.Constants.ROTATE_LEFT;
import static kz.tamur.comps.Constants.ROTATE_RIGHT;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
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
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Filter;
import kz.tamur.comps.FilterMenuItem;
import kz.tamur.comps.FindRowPanel;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrHeaderTableCellRenderer;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.OrTableNavigator;
import kz.tamur.comps.PropertyValue;
import kz.tamur.rt.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.AdvancedScrollPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ProcessRecord;
import kz.tamur.rt.SearchWindow;
import kz.tamur.rt.TaskTable;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.BooleanTableCellRenderer;
import kz.tamur.util.DateTableCellRenderer;
import kz.tamur.util.Funcs;
import kz.tamur.util.IntegerTableCellRenderer;
import kz.tamur.util.LangItem;
import kz.tamur.util.OrCellRenderer;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList.MsgListItem;
import kz.tamur.util.ZebraCellRenderer;

import com.cifs.or2.client.FloatTableCellRenderer;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.util.CursorToolkit;
import com.cifs.or2.util.expr.Editor;

public class TableAdapter extends ContainerAdapter implements DataCashListener, TableModelListener {
    private static final int MAX_COPY_ROWS = 10000;

    protected OrTable table;
    protected RtTableModel model;
    protected RtSelectionListener selLnr;
    protected OrTableNavigator navi;
    protected int selRowIdx = -1;
   
    protected int selColumnIndex;

    private List<TableColumn> sortedColumns = new ArrayList<TableColumn>();
    private List<Boolean> sortingDirection = new ArrayList<Boolean>();
    protected boolean isSort = false;

    private final ImageIcon columnUp = kz.tamur.rt.Utils.getImageIcon("SortUp");
    private final ImageIcon columnDown = kz.tamur.rt.Utils.getImageIcon("SortDown");
    private final ImageIcon columnUpRotateRight = kz.tamur.rt.Utils.getImageIcon("SortUpRotateRight");
    private final ImageIcon columnDownRotateRight = kz.tamur.rt.Utils.getImageIcon("SortDownRotateRight");
    private final ImageIcon columnUpRotateLeft = kz.tamur.rt.Utils.getImageIcon("SortUpRotateLeft");
    private final ImageIcon columnDownRotateLeft = kz.tamur.rt.Utils.getImageIcon("SortDownRotateLeft");
    private final ImageIcon yesManDown = kz.tamur.rt.Utils.getImageIcon("goDown");
    private final ImageIcon yesManRight = kz.tamur.rt.Utils.getImageIcon("goRight");
    
    protected JButton addBtn, yesManBtn;
    private boolean autoCreateObject = true;
    private boolean yes_man = true;
    private boolean isAutoAddRow = true;
    private boolean multiSelection = false;
    private int access;
    PropertyChangeSupport ps = new PropertyChangeSupport(this);
    FilterMenuActionListener filterMenuAction = new FilterMenuActionListener();
    protected ASTStart beforDelFX, beforAddFX, afterDelFX, afterAddFX, afterCopyFX, afterMoveFX;
    private boolean hackEnabled = true;

    private OrCalcRef rowBackgroundRef;
    private OrCalcRef rowForegroundRef;
    protected OrCalcRef rowFontRef;

    protected ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private SearchWindow searchWindow;
    private long ifcLangId;

    private String maxObjectCountMessage;
    protected int[] copiedRows;
    protected boolean canSort = true;
    private OrCalcRef delActivityRef;
    private List<ProcessRecordAction> processActions;

    /** Номер текущей страницы */
    private int numberSelectPage;
    /** Cтраниц всего */
    private int countPage;
    /** текущее на странице */
    protected int rowCount = -1;
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

    private List<String[]> data = null;
    protected boolean showDeleted = true;

    public TableAdapter(OrFrame frame, OrTable table, int i, boolean isEditor) throws KrnException {
        this(frame, table, isEditor);
        createRowBackColorRef(table);
        createRowFontColorRef(table);
        if (rowBackgroundRef != null) {
            rowBackgroundRef.setTableRef(this.getRef());
        }
        if (rowForegroundRef != null) {
            rowForegroundRef.setTableRef(this.getRef());
        }
    }

    public TableAdapter(OrFrame frame, OrTable table, boolean isEditor) throws KrnException {
        super(frame, table, isEditor);
        model = createModel();

        this.table = table;
        final JTable tb = this.table.getJTable();
        tb.setModel(model);
        if (dataRef != null) {
	        selLnr = new RtSelectionListener();
	        tb.getSelectionModel().addListSelectionListener(selLnr);
        }
        if (table.isNaviExists()) {
            navi = table.getNavi();
            yes_man = table.isYesMan();
            isAutoAddRow = table.isAutoAddRow();
            ps.addPropertyChangeListener(navi);
            navi.setTableAdapter(this);
            navi.initFilterPopupMenu(getFilterItems());
            addBtn = navi.getButtonByName("addBtn");
            yesManBtn = navi.getButtonByName("yesManBtn");
            countRowPage = navi.getCountRowPage();
        }
        tb.addMouseListener(new MouseAdapter() {
            /*
             * public void mouseClicked(MouseEvent e) {
             * if (e.getClickCount() == 2) {
             * try {
             * String val;
             * TableColumnModel columnModel = tb.getColumnModel();
             * int col = columnModel.getColumnIndexAtX(
             * e.getX() - TableAdapter.this.table.getLocation().x);
             * if (col >= 0) {
             * ColumnAdapter cola = model.getColumnAdapter(col);
             * int selRow = tb.getSelectedRow();
             * boolean isCheck = tb.getRowCount() > 0
             * && selRow >= 0;
             * if (isCheck && cola instanceof CheckBoxColumnAdapter &&
             * model.isCellEditable(selRow, col)) {
             * OrRef ref = cola.dataRef;
             * val = model.getValueAt(selRow, col).toString();
             * if (val.equals("false")) {
             * ref.changeItem(new Long(1), TableAdapter.this, this);
             * } else {
             * ref.changeItem(new Long(0), TableAdapter.this, this);
             * }
             * }
             * }
             * 
             * } catch (Exception ex) {
             * ex.printStackTrace();
             * }
             * }
             * }
             */

            public void mousePressed(MouseEvent e) {
                countCurrentTableItem();
                super.mousePressed(e);
            }
        });
        JTableHeader header = tb.getTableHeader();
        header.setUpdateTableInRealTime(true);
        if (!(this instanceof TreeTableAdapter)) {
            header.addMouseListener(new ColumnListener());
        }

        // перерисовка необходима при изменении заголовка таблицы, иначе возникают артефакты
        header.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                TableAdapter.this.table.repaintAll();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                TableAdapter.this.table.repaintAll();
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                TableAdapter.this.table.repaintAll();
            }
        });

        header.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
            }

            public void mouseDragged(MouseEvent e) {
                TableAdapter.this.table.repaintAll();
            }
        });

        header.setReorderingAllowed(true);
        // Задание направление движения курсора по клавишам TAB и ENTER
        initActionMap(table);

        PropertyNode prop = table.getProperties();

        // Поведение
        PropertyNode pov = prop.getChild("pov");
        // Ограничение кол-ва строк в таблице
        PropertyValue pv = table.getPropertyValue(pov.getChild("maxObjectCount"));
        if (dataRef != null) {
        	dataRef.setColumn(false);
            dataRef.getCash().addCashListener(this);
            countRowPage = navi == null ? pv.isNull() ? -1 : pv.intValue() : navi.getCountRowPage();
            if (countRowPage != -1) {
            dataRef.setLimit(countRowPage);}
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
        		attr = Kernel.instance().getAttributeByName(cls, name);
        		cls = Kernel.instance().getClass(attr.typeClassId);
        	}
            dataRef.setDeletedAttr(attr);
        }
        pv = table.getPropertyValue(pov.getChild("multiselection"));
        if (!pv.isNull()) {
            multiSelection = pv.booleanValue();
        }
        // formila for Before Add
        PropertyNode pn = pov.getChild("act");
        pv = table.getPropertyValue(pn.getChild("beforAdd"));
        String beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue();
        }
        if (beforExpr != null && beforExpr.length() > 0) {
            propertyName = "Свойство: Перед добавлением";
            try {
                beforAddFX = OrLang.createStaticTemplate(beforExpr);
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }
        String afterExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterAdd"));
        if (!pv.isNull()) {
            afterExpr = pv.stringValue();
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            propertyName = "Свойство: После добавления";
            try {
                afterAddFX = OrLang.createStaticTemplate(afterExpr);
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }
        // formula for AfterDelete
        pv = table.getPropertyValue(pn.getChild("afterDelete"));
        afterExpr = null;
        if (!pv.isNull()) {
            afterExpr = pv.stringValue();
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            propertyName = "Свойство: После удаления";
            try {
                afterDelFX = OrLang.createStaticTemplate(afterExpr);
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }

        pv = table.getPropertyValue(pn.getChild("beforeDelete"));
        beforExpr = null;
        if (!pv.isNull()) {
            beforExpr = pv.stringValue();
        }
        if (beforExpr != null && beforExpr.length() > 0) {
            propertyName = "Свойство: Перед удалением";
            try {
                beforDelFX = OrLang.createStaticTemplate(beforExpr);
                Editor e = new Editor(beforExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }
        String afterCopyExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterCopy"));
        if (!pv.isNull()) {
            afterCopyExpr = pv.stringValue();
        }
        if (afterCopyExpr != null && afterCopyExpr.length() > 0) {
            propertyName = "Свойство: После копирования";
            try {
                afterCopyFX = OrLang.createStaticTemplate(afterCopyExpr);
                Editor e = new Editor(afterCopyExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }
        String afterMoveExpr = null;
        pv = table.getPropertyValue(pn.getChild("afterMove"));
        if (!pv.isNull()) {
            afterMoveExpr = pv.stringValue();
        }
        if (afterMoveExpr != null && afterMoveExpr.length() > 0) {
            propertyName = "Свойство: После перемещения";
            try {
                afterMoveFX = OrLang.createStaticTemplate(afterMoveExpr);
                Editor e = new Editor(afterMoveExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    showErrorNessage(ex.getMessage());
                }
                ex.printStackTrace();
            }
        }
        ListSelectionModel lsm = table.getJTable().getSelectionModel();
        lsm.setSelectionMode((multiSelection) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                : ListSelectionModel.SINGLE_SELECTION);
        tb.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    countCurrentTableItem();
                }
            }
        });
        createRowBackColorRef(table);
        createRowFontColorRef(table);
        createRowFontRef(table);
        createDelActivityRef(table);
        if (rowBackgroundRef != null) {
            rowBackgroundRef.setTableRef(this.getRef());
        }
        if (rowForegroundRef != null) {
            rowForegroundRef.setTableRef(this.getRef());
        }

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
                PropertyNode pn1 = pn.getChild("activDelExpr");
                if (pn1 != null) {
                    PropertyValue pv = c.getPropertyValue(pn1);
                    String fx = "";
                    if (!pv.isNull() && !"".equals(pv.stringValue())) {
                        try {
                            propertyName = "Свойство: Активность Удаление";
                            fx = pv.stringValue();
                            if (fx.trim().length() > 0) {
                                delActivityRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                delActivityRef.addOrRefListener(this);
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage() + fx);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public int getAccess() {
        return access;
    }

    protected RtTableModel createModel() {
        final RtTableModel model = new RtTableModel();

        model.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    for (int i = 0; i < model.columns.size(); ++i) {
                        ColumnAdapter orc = (ColumnAdapter) model.columns.get(i);
                        TableColumn tc = table.getJTable().getColumnModel().getColumn(i);
                        if (orc.getCellRenderer() != null) {
                            ZebraCellRenderer r = (ZebraCellRenderer) orc.getCellRenderer();
                            r.setZebra1Color(model.getZebra1Color());
                            r.setZebra2Color(model.getZebra2Color());
                            tc.setCellRenderer(r);
                        } else {
                            ZebraCellRenderer r = null;
                            if (orc instanceof CheckBoxColumnAdapter) {
                                r = new BooleanTableCellRenderer();
                                tc.setCellRenderer(r);
                            } else if (orc instanceof DateColumnAdapter) {
                                r = new DateTableCellRenderer((DateColumnAdapter) orc);
                                tc.setCellRenderer(r);
                            } else if (orc instanceof IntColumnAdapter) {
                                r = new IntegerTableCellRenderer((IntColumnAdapter) orc);
                                tc.setCellRenderer(r);
                            } else if (orc instanceof FloatColumnAdapter) {
                                r = new FloatTableCellRenderer((FloatColumnAdapter) orc);
                                tc.setCellRenderer(r);
                            } else {
                                r = new OrCellRenderer();
                                tc.setCellRenderer(r);
                            }
                            r.setZebra1Color(model.getZebra1Color());
                            r.setZebra2Color(model.getZebra2Color());
                        }
                        tc.setCellEditor(orc.getCellEditor());
                    }
                }
            }
        });

        return model;
    }

    @SuppressWarnings("serial")
    protected void initActionMap(OrTable table) {
        InputMap im = table.getJTable().getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // Have the enter key work the same as the tab key

        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);

        KeyStroke f3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK);

        final Action oldTabAction = table.getJTable().getActionMap().get(im.get(tab));
        final Action oldEnterAction = table.getJTable().getActionMap().get(im.get(enter));
        final Action oldDownAction = table.getJTable().getActionMap().get(im.get(down));
        im.put(enter, im.get(tab));
        im.put(f3, "findNextRow");
        im.put(ctrlF, "findRow");
        // Disable the right arrow key
        // KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        // im.put(right, "none");

        // Override the default tab behaviour
        // Tab to the next editable cell. When no editable cells goto next cell.

        Action tabAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (searchWindow != null && searchWindow.isVisible()) {
                    searchWindow.setVisible(false);
                    return;
                }
                JTable table = (JTable) e.getSource();
                if (table.isEditing() && !((OrCellEditor) table.getCellEditor()).stopCellEdit()) {
                    return;
                }
                int rowCount = table.getRowCount();
                int columnCount = table.getColumnCount();
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if ((row == rowCount - 1 && ((yes_man && column == columnCount - 1) || !yes_man)) || rowCount == 0) {
                    int emptyRowInsNew = getEmptyRow();
                    if (isAutoAddRow && addBtn.isVisible() && addBtn.isEnabled() && emptyRowInsNew == -1) {
                        addNewRow();
                        if (rowCount == 0)
                            row = 0;
                        else
                            row += 1;
                    }
                    column = 0;
                } else {
                    if (yes_man)
                        oldTabAction.actionPerformed(e);
                    else if (row != rowCount - 1)
                        oldEnterAction.actionPerformed(e);
                    if (yes_man || row != rowCount - 1) {
                        row = table.getSelectedRow();
                        column = table.getSelectedColumn();
                    }
                }

                table.changeSelection(row, column, false, false);
            }
        };
        Action downAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                if (table.isEditing()) {
                    return;
                }
                int rowCount = table.getRowCount();
                int row = table.getSelectedRow();
                if (row == rowCount - 1) {
                    int emptyRow = getEmptyRow();
                    if (isAutoAddRow && addBtn.isVisible() && addBtn.isEnabled() && emptyRow == -1) {
                        addNewRow();
                        row += 1;
                    }
                }
                oldDownAction.actionPerformed(e);
            }
        };
        Action findAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                Container parent = table;
                while (parent != null && !(parent instanceof OrTable)) {
                    parent = parent.getParent();
                }

                if (parent instanceof OrTable) {
                    OrTable ortable = (OrTable) parent;
                    if (ortable.getNavi() != null) {
                        JButton btn = ortable.getNavi().getButtonByName("findBtn");
                        if (btn != null) {
                            e.setSource(btn);
                            ortable.getNavi().actionPerformed(e);
                        }
                    }
                }

            }
        };
        Action findNextAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();

                Container parent = table;
                while (parent != null && !(parent instanceof OrTable)) {
                    parent = parent.getParent();
                }
                if (parent instanceof OrTable) {
                    OrTable ortable = (OrTable) parent;
                    if (ortable.getNavi() != null) {
                        JButton btn = ortable.getNavi().getButtonByName("findBtn");
                        if (btn != null) {
                            e = new ActionEvent(btn, e.getID(), "nextRow");
                            ortable.getNavi().actionPerformed(e);
                        }
                    }
                }

            }
        };
        table.getJTable().addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (shouldFind()) {
                    JTable table = (JTable) e.getSource();
                    table.editingCanceled(null);
                    if (e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_ENTER) {
                        if (searchWindow != null && searchWindow.isVisible()) {
                            searchWindow.setText("");
                            searchWindow.setVisible(false);
                        }
                    } else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                        if (searchWindow != null && searchWindow.isVisible()) {
                            String text = searchWindow.deleteSymbol();
                            if (text.length() > 0) {
                                int foundRow = findRowByText(text);
                                searchWindow.setFound(foundRow > -1);
                            }
                        }
                    } else if (!e.isActionKey() && !e.isControlDown() && !e.isAltDown() && !(e.getKeyChar() == KeyEvent.VK_TAB)) {
                        String text = "" + e.getKeyChar();
                        if (searchWindow == null) {
                            Container c = table.getTopLevelAncestor();
                            if (c instanceof JDialog)
                                searchWindow = new SearchWindow((JDialog) c);
                            else
                                searchWindow = new SearchWindow((JFrame) c);
                        }
                        int row = table.getSelectedRow();
                        int col = table.getSelectedColumn();

                        if (searchWindow.isVisible())
                            text = searchWindow.addText(text);
                        else {
                            searchWindow.setText(text);
                            Rectangle rect = table.getCellRect(row, col, true);
                            Point locs = ((JScrollPane) TableAdapter.this.table.getScroller()).getLocationOnScreen();
                            Point loc = table.getLocationOnScreen();
                            searchWindow.setLocation(loc.x + rect.x + 1, locs.y - 21);
                        }
                        searchWindow.setVisible(true);

                        int foundRow = findRowByText(text);

                        searchWindow.setFound(foundRow > -1);

                    }
                } else {
                    super.keyTyped(e);
                }
            }

            public void keyPressed(KeyEvent e) {
                super.keyPressed(e); // To change body of overridden methods use File | Settings | File Templates.
            }

            public void keyReleased(KeyEvent e) {
                super.keyReleased(e); // To change body of overridden methods use File | Settings | File Templates.
            }
        });
        table.getJTable().addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (!(e.getOppositeComponent() instanceof SearchWindow))
                    if (searchWindow != null && searchWindow.isVisible()) {
                        searchWindow.setText("");
                        searchWindow.setVisible(false);
                    }
            }
        });
        ((JScrollPane) table.getScroller()).getHorizontalScrollBar().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); // To change body of overridden methods use File | Settings | File Templates.
                if (searchWindow != null && searchWindow.isVisible()) {
                    searchWindow.setText("");
                    searchWindow.setVisible(false);
                }
            }
        });
        table.getJTable().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); // To change body of overridden methods use File | Settings | File Templates.
                if (searchWindow != null && searchWindow.isVisible()) {
                    searchWindow.setText("");
                    searchWindow.setVisible(false);
                }
            }
        });
        table.getJTable().getActionMap().put(im.get(down), downAction);
        table.getJTable().getActionMap().put(im.get(tab), tabAction);

        table.getJTable().getActionMap().put(im.get(ctrlF), findAction);
        table.getJTable().getActionMap().put(im.get(f3), findNextAction);

        if (table.isNaviExists() && (navi.getButtonByName("upBtn").isVisible() || navi.getButtonByName("downBtn").isVisible())) {

            KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK);
            KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK);

            Action copyAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    copySelectedRows();
                }
            };
            Action pasteAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    pasteCopiedRows();
                }
            };
            table.getJTable().getActionMap().put(im.get(ctrlC), copyAction);
            table.getJTable().getActionMap().put(im.get(ctrlV), pasteAction);

        }
    }

    public void tableChanged(TableModelEvent e) {
        // TableModel tm = (TableModel) e.getSource();
        // int row = e.getFirstRow();
        // int col = e.getColumn();
        // OrTableColumn column = model.getColumn(col);
        // column.setValueAt(row, tm.getValueAt(row, col));

    }

    public void copySelectedRows() {
        int[] rows = table.getJTable().getSelectedRows();
        this.copiedRows = rows;
    }

    public void pasteCopiedRows() {
        int i = table.getJTable().getSelectedRow();
        if (copiedRows != null && copiedRows.length > 0 && i > -1) {
            SortedSet<Integer> is = new TreeSet<Integer>();
            for (int k : copiedRows) {
                if (k != i)
                    is.add(k);
            }
            int[] rows = new int[is.size()];
            int j = 0;
            for (Integer k : is) {
                rows[j++] = k;
            }
            if (rows.length > 0) {
                try {
                    dataRef.moveRowsBefore(this, i, rows, this);

                    int min = Math.min(i, rows[0]);
                    int max = Math.max(i, rows[rows.length - 1]);
                    int beforeI = 0;
                    for (int k = 0; k < rows.length; k++) {
                        if (i > rows[k]) {
                            beforeI++;
                        }
                    }

                    model.fireTableRowsUpdated(min, max);
                    table.getJTable().getSelectionModel().setSelectionInterval(i - beforeI, i - beforeI + rows.length - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.copiedRows = null;
    }

    public void moveDown() {
        int i = table.getJTable().getSelectedRow();
        try {
            OrCalcRef.setCalculations();
            dataRef.moveDown(this, i, this);
            OrCalcRef.makeCalculations();
            if (model.getRowCount() > i + 1) {
                model.fireTableRowsUpdated(i, i + 1);
                table.getJTable().getSelectionModel().setSelectionInterval(i + 1, i + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveUp() {
        int i = table.getJTable().getSelectedRow();
        try {
            OrCalcRef.setCalculations();
            dataRef.moveUp(this, i, this);
            OrCalcRef.makeCalculations();
            if (i > 0) {
                model.fireTableRowsUpdated(i - 1, i);
                table.getJTable().getSelectionModel().setSelectionInterval(i - 1, i - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ColumnListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            TableColumnModel columnModel = table.getJTable().getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(e.getX());
            if (columnIndex == -1) {
                return;
            }
            int columnIndex_ = columnModel.getColumnIndexAtX(e.getX() + columnModel.getColumn(columnIndex).getWidth() / 2);
            TableColumn tColumn = columnModel.getColumn(columnIndex);
            int columnModelIndex = columnModel.getColumn(columnIndex).getModelIndex();
            if (columnModelIndex < 0) {
                return;
            }
            OrTableColumn otc = null;
            TableModel tm = table.getJTable().getModel();
            if (tm instanceof OrTableModel) {
                otc = ((OrTableModel) tm).getColumn(columnIndex);
            }
            if (canSort && otc != null && otc.isCanSort() && !otc.isHelpClick()) {
                if (columnIndex == columnIndex_) {
                    sortByColumn(tColumn);
                } else {
                    removeSortColumn(tColumn);
                }
            } else if (otc != null) {
                otc.setHelpClick(false);
            }
            table.getJTable().getTableHeader().repaint();
            table.repaint();
        }
    }

    public void addColumnAdapter(ColumnAdapter a) {
        childrenAdapters.add(a);
        int index = model.addColumn(a);
        a.setTableAdapter(this);
        a.setIndex(index);
        if (a instanceof ComboColumnAdapter) {
            if (a.dataRef == dataRef) {
                autoCreateObject = false;
            }
        } else if (a instanceof PopupColumnAdapter) {
            OrRef titleRef = ((PopupColumnAdapter) a).getTitleRef();
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
            TableColumn column = table.getJTable().getColumnModel().getColumn(index);
            int sind = a.getSortingIndex();
            if (sind > sortedColumns.size()) {
                sind = sortedColumns.size();
            }
            sortedColumns.add(sind, column);
            sortingDirection.add(sind, dir);
            JLabel renderer = (JLabel) column.getHeaderRenderer();
            renderer.setIcon(dir.booleanValue()?columnDown:columnUp);
        }

        model.fireTableStructureChanged();
    }

    public RtTableModel getModel() {
        return model;
    }

    @SuppressWarnings("serial")
    public class RtTableModel extends AbstractTableModel implements OrTableModel {

        private boolean isRowBackColorCalc = false;
        private boolean isRowFontColorCalc = false;
        protected List<ColumnAdapter> columns = new ArrayList<ColumnAdapter>();
        private Map<Integer, List<Integer>> uniqueCols = new HashMap<Integer, List<Integer>>();

        public List<ColumnAdapter> getColumns() {
            return columns;
        }

        public boolean isRowFontColorCalc() {
            return isRowFontColorCalc;
        }

        public void setRowFontColorCalc(boolean rowFontColorCalc) {
            isRowFontColorCalc = rowFontColorCalc;
        }

        public boolean isRowBackColorCalc() {
            return isRowBackColorCalc;
        }

        public void setRowBackColorCalc(boolean rowBackColorCalc) {
            isRowBackColorCalc = rowBackColorCalc;
        }

        public int addColumn(ColumnAdapter a) {
            columns.add(a);
            if (a.getUniqueIndex() > 0) {
                Integer uin = new Integer(a.getUniqueIndex());
                if (uniqueCols.get(uin) == null) {
                    List<Integer> cols = new ArrayList<Integer>();
                    cols.add(new Integer(columns.size() - 1));
                    uniqueCols.put(uin, cols);
                } else {
                    List<Integer> cols = uniqueCols.get(uin);
                    cols.add(new Integer(columns.size() - 1));
                    uniqueCols.put(uin, cols);
                }
                if (!a.getCellEditor().hasEditingTable())
                    a.getCellEditor().setEditingTable(table.getJTable());
            }
            return columns.size() - 1;
        }

        public int getColumnCount() {
            return columns.size();
        }

        public Color getZebra1Color() {
            return table.getZebraColor1();
        }

        public Color getZebra2Color() {
            return table.getZebraColor2();
        }

        public int getRowCount() {
        	if (dataRef != null) {
        		int count = dataRef.getItems(0).size();
        		if (!showDeleted)
        			count -= dataRef.getDeletedIndexes().size();
        			
        		return count;
        	} else
        		return data != null ? data.size() : 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex >= columns.size())
                return null;
            
            if (data == null) {
        		rowIndex = TableAdapter.this.getRowIndex(rowIndex);
	            ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
	            return ca.getValueAt(rowIndex);
            } else
            	return data.get(rowIndex)[columnIndex];
        }

        public String getColumnName(int column) {
            ColumnAdapter ca = (ColumnAdapter) columns.get(column);
            return ca.getColumn().getTitle();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            ColumnAdapter ca = getColumnAdapter(columnIndex);
            if (ca instanceof MemoColumnAdapter)
                return true;

    		rowIndex = TableAdapter.this.getRowIndex(rowIndex);
            return isColumnCellEditable(rowIndex, columnIndex);
        }

        public boolean isColumnCellEditable(int rowIndex, int columnIndex) {
            boolean res = true;
    		rowIndex = TableAdapter.this.getRowIndex(rowIndex);

    		if (!hackEnabled || access == Constants.READ_ONLY_ACCESS) {
                ColumnAdapter ca = getColumnAdapter(columnIndex);
                if (ca instanceof PopupColumnAdapter || ca instanceof DocFieldColumnAdapter) {
                    return ca.checkEnabled();
                } else {
                    return false;
                }
            } else if (access == Constants.LAST_ROW_ACCESS) {
                int rowcount = table.getJTable().getRowCount();
                res = (rowcount - 1 != rowIndex) ? false : true;
            } else if (access == Constants.BY_TRANSACTION_ACCESS) {
                // ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
                // OrRef c_ref = ca.dataRef; //@todo Выяснить у Каиржана!
            }
            ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
            return res && ca.isEnabled();
        }

        public void setInterfaceLangId(long langId) {
            TableAdapter.this.ifcLangId = langId;
            LangItem langItem = LangItem.getById(langId);
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(langItem.code) ? "kk" : "ru"));
            for (int i = 0; i < columns.size(); i++) {
                ColumnAdapter ca = (ColumnAdapter) columns.get(i);
                ca.getColumn().setLangId(langId);
            }
            fireTableStructureChanged();
        }

        public OrTableColumn getColumn(int colIndex) {
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex);
            return ca.getColumn();
        }

        public ColumnAdapter getColumnAdapter(int colIndex) {
            ColumnAdapter ca = (ColumnAdapter) columns.get(colIndex);
            return ca;
        }

        public Class<?> getColumnClass(int columnIndex) {
            ColumnAdapter ca = (ColumnAdapter) columns.get(columnIndex);
            if (ca instanceof CheckBoxColumnAdapter) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }

        public Map<Integer, List<Integer>> getUniqueMap() {
            return uniqueCols;
        }

        public Color getRowBgColor(int index) {
            if (rowBackgroundRef != null) {
        		index = TableAdapter.this.getRowIndex(index);
                OrRef.Item item = rowBackgroundRef.getItem(0, index);
                Object o = (item != null) ? item.getCurrent() : null;
                if (o instanceof Number) {
                    return new Color(((Number) o).intValue());
                } else if (o instanceof String) {
                    return Utils.getColorByName(o.toString());
                }
            }
            return defaultBgColor;
        }

        public Color getRowFontColor(int index) {
            if (rowForegroundRef != null) {
        		index = TableAdapter.this.getRowIndex(index);
                OrRef.Item item = rowForegroundRef.getItem(0, index);
                Object o = (item != null) ? item.getCurrent() : null;
                if (o instanceof Number) {
                    return new Color(((Number) o).intValue());
                } else if (o instanceof String) {
                    return Utils.getColorByName(o.toString());
                }
            }
            return Color.black;
        }
    }

    protected class RtSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (selfChange || e.getValueIsAdjusting())
                return;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            try {
                selfChange = true;
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    int emptyRow = getEmptyRow();
                    if (emptyRow > -1) {
                        if (selectedRow != -1 && emptyRow > selectedRow) {
                            if (deleteEmptyRow(emptyRow)) {
                                int selectedCol = table.getJTable().getSelectedColumn();
                                lsm.setSelectionInterval(selectedRow, selectedRow);
                                model.fireTableDataChanged();
                                table.getJTable().setRowSelectionInterval(selectedRow, selectedRow);
                                if (selectedCol > -1)
                                    table.getJTable().setColumnSelectionInterval(selectedCol, selectedCol);
                            }
                        }
                    }
                    selectedRow = TableAdapter.this.getRowIndex(selectedRow);
                    dataRef.absolute(selectedRow, this);
                    dataRef.setSelectedItems(table.getJTable().getSelectedRows());
                } else {
                    dataRef.absolute(TableAdapter.this.getRowIndex(0), this);
                    dataRef.setSelectedItems(new int[0]);
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            } finally {
                selfChange = false;
            }
        }
    }

    public OrRef getRef() {
        return dataRef;
    }

    public long getLangId() {
        return dataRef.getLangId();
    }

    public OrTable getTable() {
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

    public void sortByColumn(TableColumn column) {
        int index = sortedColumns.indexOf(column);
        Boolean dir = Boolean.TRUE;
        if (index > -1) {
            dir = !sortingDirection.get(index);
           // dir = new Boolean(!dir.booleanValue());
            sortingDirection.set(index, dir);
        } else {
            sortedColumns.add(column);
            sortingDirection.add(dir);
        }
        if (column.getHeaderRenderer() instanceof OrHeaderTableCellRenderer) {
            OrHeaderTableCellRenderer renderer = (OrHeaderTableCellRenderer) column.getHeaderRenderer();
            ImageIcon UP = null;
            ImageIcon DOWN = null;
            switch (renderer.getRotation()) {
            case DONT_ROTATE:
                UP = columnUp;
                DOWN = columnDown;
                break;
            case ROTATE_LEFT:
                UP = columnUpRotateRight;
                DOWN = columnDownRotateRight;
                break;
            case ROTATE_RIGHT:
                UP = columnUpRotateLeft;
                DOWN = columnDownRotateLeft;
                break;
            }

            renderer.setIcon(dir.booleanValue() ? DOWN : UP);
        } else {
            JLabel renderer = (JLabel) column.getHeaderRenderer();
            renderer.setIcon(dir.booleanValue() ? columnDown : columnUp);
        }
        sort();
    }

    protected void sort() {
        if (sortedColumns != null && sortedColumns.size() > 0) {
        	if (dataRef != null) {
	            long langId = getLangId();
	            List<Item> items = new ArrayList<Item>(dataRef.getItems(langId));
	            Collections.sort(items, new ItemSorter());
	            isSort = true;
	            dataRef.setItems(langId, items, this);
        	}
        } else {
            isSort = true;
        }
    }

    class ItemSorter implements Comparator<OrRef.Item> {

        public int compare(OrRef.Item a, OrRef.Item b) {
            List<Item> items = dataRef.getItems(getLangId());
            int ind1 = identityIndexOf(items, a);
            int ind2 = identityIndexOf(items, b);
            boolean isId = true;
            if (a != null && b != null && a.getRec().getValue() instanceof KrnObject
                    && b.getRec().getValue() instanceof KrnObject) {
                isId = ((KrnObject) a.getRec().getValue()).id > ((KrnObject) b.getRec().getValue()).id;
            }
            try {
                for (int i = 0; i < sortedColumns.size(); i++) {
                    int res = 0;
                    TableColumn column = (TableColumn) sortedColumns.get(i);
                    boolean dir = ((Boolean) sortingDirection.get(i)).booleanValue();
                    ColumnAdapter c = (ColumnAdapter) ((RtTableModel) table.getJTable().getModel()).getColumnAdapter(column
                            .getModelIndex());
                    String typeColumn = (c.dataRef != null) ? c.dataRef.getType().name : "";
                    Object o1, o2;
                    if (c instanceof ComboColumnAdapter || c instanceof CheckBoxColumnAdapter || c instanceof PopupColumnAdapter
                            || c instanceof TreeColumnAdapter) {
                        o1 = c.getValueAt(ind1);
                        o2 = c.getValueAt(ind2);
                    } else {
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
                        if (typeColumn.equals("long") || typeColumn.equals("float") || typeColumn.equals("double") || o1 instanceof Number || o2 instanceof Number) {
                            if (o1 instanceof Number) {
                                Number n1 = (Number) o1;
                                double d1 = n1.doubleValue();
                                if (o2 instanceof Number) {
    	                            Number n2 = (Number) o2;
    	                            double d2 = n2.doubleValue();
    	                            if (d1 < d2) {
    	                                res = -1;
    	                            } else if (d1 > d2) {
    	                                res = 1;
    	                            } else {
    	                                res = 0;
    	                            }
                                } else {
                                	res = 1;
                                }
                            } else {
                            	res = -1;
                            }
                        } else if (typeColumn.equals("date") || typeColumn.equals("time") || o1 instanceof Date) {
                            Date d11 = (Date) o1;
                            Date d22 = (Date) o2;
                            if (d11.compareTo(d22) < 0) {
                                res = -1;
                            } else if (d11.compareTo(d22) > 0) {
                                res = 1;
                            } else {
                                res = 0;
                            }
                        } else if (typeColumn.equals("string") || o1 instanceof String) {
                            String s1 = (String) o1;
                            String s2 = (String) o2;
                            res = s1.compareToIgnoreCase(s2);
                        } else if (typeColumn.equals("boolean") || o1 instanceof Boolean) {
                            Boolean bool1 = (Boolean) o1;
                            boolean b1 = bool1.booleanValue();
                            Boolean bool2 = (Boolean) o2;
                            boolean b2 = bool2.booleanValue();
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
                        }// } else {

                    }
                    if (res != 0) {
                        return res * ((dir) ? 1 : -1);
                    } else if (i == sortedColumns.size() - 1) {
                        return (isId ? (dir ? 1 : -1) : (dir ? -1 : 1));
                    }
                }
                return 0;
            } catch (Exception e) {
                System.out.println("Ошибка! Преобразование в сортировке");
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

    public void addNewRow() {
        try {
            selfChange = true;
            int emptyRow = getEmptyRow();
            if (emptyRow > -1) {
                table.getJTable().changeSelection(emptyRow, 0, false, false);
            } else {
                // формула до вставки
                if (beforAddFX != null) {
                    ClientOrLang orlang = new ClientOrLang(frame);
                    try {
                        Map<String, Object> vc = new HashMap<String, Object>();
                        boolean calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(beforAddFX, vc, this, new Stack<String>());
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } catch (Exception e) {
                        Util.showErrorMessage(table, e.getMessage(), res.getString("beforeAddAction"));
                    }
                }
                OrCalcRef.setCalculations();
                Item newItem = dataRef.insertItem(-1, null, this, this, autoCreateObject);
                OrCalcRef.makeCalculations();
                // Поплняем набор записей созданных средствами этой таблицы
                insertedItems.add(newItem);
                int sel = dataRef.getItems(dataRef.getLangId()).size() - 1;
                model.fireTableRowsInserted(sel, sel);
                table.getJTable().getSelectionModel().setSelectionInterval(sel, sel);
                ps.firePropertyChange("rowSelected", selRowIdx, sel);
                selRowIdx = sel;
                dataRef.absolute(sel, this);
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
                                selVect.add(item.getCurrent());
                            }
                        }
                        vc.put("SELOBJS", selVect);
                    }
                    try {
                        OrCalcRef.setCalculations(new ArrayList<OrCalcRef>());
                        orlang.evaluate(afterAddFX, vc, this, new Stack<String>());
                        OrCalcRef.makeCalculations();
                    } catch (Exception e) {
                        Util.showErrorMessage(table, e.getMessage(), res.getString("afterAddAction"));
                    }
                }
                table.repaint();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        } finally {
            selfChange = false;
        }
    }

    public int getEmptyRow() {
    	if (dataRef != null) {
	        int upperRowCount = table.getJTable().getRowCount();
	        int upperColumnCount = table.getJTable().getColumnCount();
	        for (int l = 0; l < upperRowCount; l++) {
	            OrRef.Item item = (OrRef.Item) dataRef.getItems(getLangId()).get(l);
	            if (item.getRec() == null)
	                continue;
	            Object obj_ = item.getRec().getValue();
	            if ((obj_ instanceof KrnObject) && ((KrnObject) obj_).id > 0)
	                continue;
	            int itemsNull = 0;
	            for (int j = 0; j < upperColumnCount; j++) {
	                if (table.getJTable().getValueAt(l, j) == null) {
	                    itemsNull = itemsNull + 1;
	                }
	                if (table.getJTable().getValueAt(l, j) != null) {
	                    try {
	                        ColumnAdapter c = (ColumnAdapter) ((RtTableModel) table.getJTable().getModel()).getColumnAdapter(j);
	                        if (c != null) {
	                            String val_c = table.getJTable().getValueAt(l, j).toString().trim();
	                            OrRef ref = c.dataRef;
	                            String typeName = (ref != null) ? ref.getType().name : null;
	                            boolean flagEmptyRow = val_c.equals("") || val_c.equals("false") || val_c.equals("0")
	                                    || val_c.equals("0.0") || "HiperColumn".equals(typeName) || "SeqColumn".equals(typeName);
	                            if (flagEmptyRow) {
	                                itemsNull = itemsNull + 1;
	                            }
	                        }
	                    } catch (KrnException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	            if (itemsNull == upperColumnCount)
	                return l;
	        }
    	}
        return -1;
    }

    public void deleteRow() {
        try {
            int[] selIdx = table.getJTable().getSelectedRows();
            String msg = "";
            if (selIdx.length > 1) {
                msg = res.getString("deleteRowsConfirm");
                msg = msg.replaceAll("%1%", String.valueOf(selIdx.length));
            } else if (selIdx.length == 1) {
                msg = res.getString("deleteRowConfirm");
                msg = msg.replaceAll("%1%", String.valueOf(selIdx[0] + 1));
            } else
                return;
            int res = MessagesFactory.showMessageDialog(table.getJTable().getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, msg);
            if (res == ButtonsFactory.BUTTON_YES) {
                List<Object> selVect = new ArrayList<Object>();
                Map<Object, Integer> sitems = new HashMap<Object, Integer>();
                for (int i : selIdx) {
                    OrRef.Item item = dataRef.getItem(dataRef.getLangId(), i);
                    sitems.put(item.getCurrent(), i);
                    selVect.add(item.getCurrent());
                }

                // формула перед удалением
                doBeforeDel(selVect);

                sitems.keySet().retainAll(selVect);
                Comparator<Integer> revCmp = new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                };
                Set<Integer> inds = new TreeSet<Integer>(revCmp);
                inds.addAll(sitems.values());

                boolean calcOwner = OrCalcRef.setCalculations();
                for (int j : inds) {
                    dataRef.deleteItem(this, j, this);
                    model.fireTableRowsDeleted(j, j);
                    if (j - 1 >= 0) {
                        table.getJTable().getSelectionModel().setSelectionInterval(j - 1, j - 1);
                        ps.firePropertyChange("rowSelected", selRowIdx, j - 1);
                        selRowIdx = j - 1;
                        int count = dataRef.getItems(dataRef.getLangId()).size() - 1;
                        ps.firePropertyChange("rowCont", rowCount, count);
                        rowCount = count;
                    }
                }
                if (calcOwner)
                    OrCalcRef.makeCalculations();

                // формула после удаления
                doAfterDel(selVect);

                table.repaint();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public boolean showDeleted() {
        showDeleted = !showDeleted;
        getModel().fireTableDataChanged();
        return showDeleted;
    }
    
    public void findRow() {
        RtTableModel model = (RtTableModel) getTable().getJTable().getModel();
        ColumnAdapter ca = null;
        if (table.getJTable().getSelectedColumn() < 0) {
            ca = model.getColumnAdapter(0);
        } else {
            ca = model.getColumnAdapter(getTable().getJTable().getSelectedColumn());
        }
        Container topLevel = getTable().getJTable().getTopLevelAncestor();
        if (ca == null) {
            MessagesFactory.showMessageDialog(topLevel, MessagesFactory.ERROR_MESSAGE, res.getString("searchChooseColumn"));
            return;
        }
        int selCol = getTable().getJTable().getSelectedColumn();

        FindRowPanel findPanel = FindRowPanel.getInstance(ifcLangId, ca);
        DesignerDialog dlg;
        if (topLevel instanceof JDialog)
            dlg = new DesignerDialog((Dialog) topLevel, res.getString("findTitle"), findPanel);
        else
            dlg = new DesignerDialog((Frame) topLevel, res.getString("findTitle"), findPanel);
        dlg.setLanguage(ifcLangId);
        dlg.setResizable(false);
        dlg.show();
        if (dlg.isOK()) {
            String f = findPanel.getFindText();
            String textForSearch = (f != null) ? f : "";
            int from = getTable().getJTable().getSelectionModel().getMinSelectionIndex();
            findText(ca, textForSearch, findPanel, from);
        }

        if (selCol > -1)
            getTable().getJTable().setColumnSelectionInterval(selCol, selCol);
    }

    public void findNextRow() {
        RtTableModel model = (RtTableModel) getTable().getJTable().getModel();
        ColumnAdapter ca = null;
        if (table.getJTable().getSelectedColumn() < 0) {
            ca = model.getColumnAdapter(0);
        } else {
            ca = model.getColumnAdapter(getTable().getJTable().getSelectedColumn());
        }
        Container topLevel = getTable().getJTable().getTopLevelAncestor();
        if (ca == null) {
            MessagesFactory.showMessageDialog(topLevel, MessagesFactory.ERROR_MESSAGE, res.getString("searchChooseColumn"));
            return;
        }
        int selCol = getTable().getJTable().getSelectedColumn();

        FindRowPanel findPanel = FindRowPanel.getInstance(ifcLangId, ca);
        String f = findPanel.getFindText();
        String textForSearch = (f != null) ? f : "";
        int from = getTable().getJTable().getSelectionModel().getMinSelectionIndex();
        findText(ca, textForSearch, findPanel, from);

        if (selCol > -1)
            getTable().getJTable().setColumnSelectionInterval(selCol, selCol);
    }

    public boolean shouldFind() {
        RtTableModel model = (RtTableModel) getTable().getJTable().getModel();
        ColumnAdapter ca = null;
        if (table.getJTable().getSelectedColumn() < 0) {
            ca = model.getColumnAdapter(0);
        } else {
            ca = model.getColumnAdapter(getTable().getJTable().getSelectedColumn());
        }
        if (ca instanceof PopupColumnAdapter) {
            return true;
        } else if (ca == null || (ca.checkEnabled() && access == Constants.FULL_ACCESS)) {
            return false;
        } else {
            return true;
        }
    }

    public int findRowByText(String text) {
        RtTableModel model = (RtTableModel) getTable().getJTable().getModel();
        ColumnAdapter ca = null;
        if (table.getJTable().getSelectedColumn() < 0) {
            ca = model.getColumnAdapter(0);
        } else {
            ca = model.getColumnAdapter(getTable().getJTable().getSelectedColumn());
        }
        Container topLevel = getTable().getJTable().getTopLevelAncestor();
        if (ca == null) {
            MessagesFactory.showMessageDialog(topLevel, MessagesFactory.ERROR_MESSAGE, res.getString("searchChooseColumn"));
            return -1;
        }
        int selCol = getTable().getJTable().getSelectedColumn();

        String textForSearch = (text != null) ? text : "";

        FindRowPanel findPanel = FindRowPanel.getInstance(ifcLangId, ca);
        findPanel.setCheckRegister(false);
        findPanel.setCheckStart(true);
        findPanel.setFindText(textForSearch);

        int res = findText(ca, textForSearch, -1);

        if (selCol > -1)
            getTable().getJTable().setColumnSelectionInterval(selCol, selCol);

        return res;
    }

    protected int findText(ColumnAdapter ca, String textForSearch, int from) {
        List<Item> items;
        if (ca.dataRef != null)
            items = ca.dataRef.getItems(ca.dataRef.getLangId());
        else
            items = dataRef.getItems(dataRef.getLangId());
        for (int i = from + 1; i < items.size(); i++) {
            boolean found = findTextInRow(ca, i, textForSearch);
            if (found)
                return i;
        }
        for (int i = 0; i < from + 1; i++) {
            boolean found = findTextInRow(ca, i, textForSearch);
            if (found)
                return i;
        }
        return -1;
    }

    protected void findText(ColumnAdapter ca, String textForSearch, FindRowPanel findPanel, int from) {
        List<Item> items = (ca.dataRef != null) ? ca.dataRef.getItems(ca.dataRef.getLangId()) : ca.calcRef != null ? ca.calcRef
                .getItems(ca.calcRef.getLangId()) : new ArrayList<OrRef.Item>();

        for (int i = from + 1; i < items.size(); i++) {
            boolean found = findTextInRow(findPanel, ca, i, textForSearch);
            if (found)
                return;
        }
        for (int i = 0; i < from + 1; i++) {
            boolean found = findTextInRow(findPanel, ca, i, textForSearch);
            if (found)
                return;
        }
        Container topLevel = getTable().getJTable().getTopLevelAncestor();
        topLevel = table.getJTable().getTopLevelAncestor();
        LangItem langItem = LangItem.getById(ifcLangId);
        String code = langItem.code;
        if (topLevel instanceof JDialog)
            MessagesFactory.showMessageDialog((Dialog) topLevel, MessagesFactory.ERROR_MESSAGE, res.getString("searchComplete"),
                    code);
        else
            MessagesFactory.showMessageDialog((Frame) topLevel, MessagesFactory.ERROR_MESSAGE, res.getString("searchComplete"),
                    code);
    }

    protected boolean findTextInRow(FindRowPanel findPanel, ColumnAdapter ca, int row, String textForSearch) {
        Object value = null;
        if (ca.dataRef != null) {
            OrRef.Item item = ca.dataRef.getItem(ca.dataRef.getLangId(), row);
            value = item.getCurrent();
        } else if (ca.calcRef != null) {
            OrRef.Item item = ca.calcRef.getItem(ca.calcRef.getLangId(), row);
            value = item.getCurrent();
        }
        String val = "";
        if (ca instanceof DateColumnAdapter) {
            val = (value != null) ? kz.tamur.util.Funcs.getDateFormat().format(value) : "";
        } else if (ca instanceof PopupColumnAdapter) {
            Object obj = ((PopupColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else if (ca instanceof TreeColumnAdapter) {
            Object obj = ((TreeColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else if (ca instanceof ComboColumnAdapter) {
            Object obj = ((ComboColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else {
            val = (value != null) ? value.toString() : "";
        }
        if (!findPanel.isCheckRegister()) {
            if (findPanel.isCheckFull()) {
                if (textForSearch.equalsIgnoreCase(val)) {
                    moveToRow(row);
                    return true;
                }
            } else if (findPanel.isCheckStart()) {
                if (val.toLowerCase(Constants.OK).startsWith(textForSearch.toLowerCase(Constants.OK))) {
                    moveToRow(row);
                    return true;
                }
            } else {
                if (val.toLowerCase(Constants.OK).contains(textForSearch.toLowerCase(Constants.OK))) {
                    moveToRow(row);
                    return true;
                }
            }
        } else {
            if (findPanel.isCheckFull()) {
                if (textForSearch.equals(val)) {
                    moveToRow(row);
                    return true;
                }
            } else if (findPanel.isCheckStart()) {
                if (val.startsWith(textForSearch)) {
                    moveToRow(row);
                    return true;
                }
            } else {
                if (val.contains(textForSearch)) {
                    moveToRow(row);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean findTextInRow(ColumnAdapter ca, int row, String textForSearch) {
        Object value = null;
        if (ca.dataRef != null) {
            OrRef.Item item = ca.dataRef.getItem(ca.dataRef.getLangId(), row);
            value = item.getCurrent();
        } else if (ca.calcRef != null) {
            OrRef.Item item = ca.calcRef.getItem(ca.calcRef.getLangId(), row);
            value = item.getCurrent();
        }
        String val = "";
        if (ca instanceof DateColumnAdapter) {
            val = (value != null) ? kz.tamur.util.Funcs.getDateFormat().format(value) : "";
        } else if (ca instanceof ComboColumnAdapter) {
            Object obj = ((ComboColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else if (ca instanceof PopupColumnAdapter) {
            Object obj = ((PopupColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else if (ca instanceof TreeColumnAdapter) {
            Object obj = ((TreeColumnAdapter) ca).getValueAt(row);
            val = (obj != null) ? obj.toString() : "";
        } else {
            val = (value != null) ? value.toString() : "";
        }
        if (val.toLowerCase(Constants.OK).startsWith(textForSearch.toLowerCase(Constants.OK))) {
            moveToRow(row);
            return true;
        }
        return false;
    }

    protected int moveToRow(int i) {
        table.getJTable().getSelectionModel().setSelectionInterval(i, i);
        MoveTableView(i);
        countCurrentTableItem();
        return i;
    }

    public void copyRows() {
        int[] indexRows = table.getJTable().getSelectedRows();

        try {
            JTextField fbCopy = new JTextField("1");
            DesignerDialog dlgCopy = new DesignerDialog((Frame) table.getJTable().getTopLevelAncestor(),
                    res.getString("copiesCount"), fbCopy);
            if (!dlgCopy.getTitle().trim().equals("")) {
                dlgCopy.setResizable(false);
                dlgCopy.setModal(true);
                dlgCopy.show();
                
                int upBound = 0;
                try {
                	String str = fbCopy.getText();
                	if (str.matches(".+")) {
                		upBound = Integer.parseInt(str);
                	}
                } catch (NumberFormatException nfe) {
                	upBound = 0;
                }
                if (dlgCopy.getResult() == ButtonsFactory.BUTTON_OK && upBound > 0) {
                    List<Object> newObjs = new ArrayList<Object>();
                    if (dataRef.getItems(dataRef.getLangId()).size() > 0) {
                        if (indexRows.length > 0) {
                        	if (upBound < MAX_COPY_ROWS) {
	                            for (int k = 1; k <= upBound && k < MAX_COPY_ROWS; k++) {
	                                for (int row : indexRows) {
	                                    OrRef.Item item = dataRef.insertItem(-1, null, this, this, true);
	                                    newObjs.add((KrnObject) item.getCurrent());
	                                    dataRef.absolute(row, this);
	                                    List<ColumnAdapter> cas = model.getColumns();
	                                    for (ColumnAdapter ca : cas) {
	                                        OrRef columnRef = ca.getDataRef();
	                                        columnRef.copy(item, this);
	                                    }
	                                }
	                            }
	                            int count = dataRef.getItems(dataRef.getLangId()).size();
	                            model.fireTableRowsInserted(count - 1, count - 1);
	                            dataRef.absolute(count - 1, null);
	                            countCurrentTableItem();
                        	}
                        }
                    }
                    if (afterCopyFX != null) {
                        ClientOrLang orlang = new ClientOrLang(frame);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("SELOBJS", newObjs);
                        try {
                            orlang.evaluate(afterCopyFX, vc, this, new Stack<String>());
                        } catch (Exception e) {
                            Util.showErrorMessage(table, e.getMessage(), res.getString("afterCopyAction"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mpveDown() {
        int[] indexRows = table.getJTable().getSelectedRows();
        try {
            if (dataRef.getItems(dataRef.getLangId()).size() > 0) {
                if (indexRows.length > 0) {
                    int count = dataRef.getItems(dataRef.getLangId()).size();
                    model.fireTableRowsInserted(count - 1, count - 1);
                    dataRef.absolute(count - 1, null);
                    countCurrentTableItem();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void yesMan() {
        yes_man = !yes_man;
        if (yes_man) {
            yesManBtn.setIcon(yesManRight);
        } else {
            yesManBtn.setIcon(yesManDown);
        }
    }

    public void firstPage() {

    }

    public void backPage() {

    }

    public void nextPage() {

    }

    public void lastPage() {

    }

    // TODO Обдумать
    public void countCurrentTableItem() {
        int count = (dataRef != null) ? dataRef.getItems(dataRef.getLangId()).size() - 1
        			: (data != null) ? data.size() : 0;
        ps.firePropertyChange("rowCont", rowCount, count);
        rowCount = count;
        int sel = table.getJTable().getSelectedRow();
        if (count < 0) {
            selRowIdx = 0;
            sel = -1;
        }
        ps.firePropertyChange("rowSelected", selRowIdx, sel);
        selRowIdx = sel;
    }

    private FilterMenuItem[] getFilterItems() {
        PropertyValue pv = table.getPropertyValue(table.getProperties().getChild("ref").getChild("filters"));
        List<Filter> list = new ArrayList<Filter>();
        if (!pv.isNull()) {
            try {
                Kernel krn = Kernel.instance();
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
                e.printStackTrace();
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

    public void applyFilter(Filter filter) throws KrnException {
        dataRef.addFilter(filter);
        // sort();
    }

    public void applyFilters(List<Filter> filters) throws KrnException {
        dataRef.addFilters(filters);
        // sort();
    }

    public void cancelFilterAction() {
        try {
            dataRef.removeAllFilters(false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() instanceof ReportPrinterAdapter.ReportNode || e.getOriginator() instanceof OrCalcRef)
            return;
        if (e.getOriginator() instanceof ReportPrinterAdapter) {
            countCurrentTableItem();
            return;
        }

        if (e.getRef() == delActivityRef) {
            Object val = delActivityRef.getValue(langId);
            if (val instanceof Number) {
                boolean enable = (((Number) val).intValue() == 1);
                getTable().setDelEnabled(enable);
            }
        }else if(hackEnabled && getTable().getRowCount()>0){
            getTable().setDelEnabled(true);
        }
        //если строк в таблице нет то и кнопка удаления должна быть не доступна
        if(hackEnabled && getTable().getRowCount()==0)
            getTable().setDelEnabled(false);
        

        if (e.getRef() == dataRef && paramFiltersUIDs != null && paramFiltersUIDs.length > 0) {
            for (int i = 0; i < paramFiltersUIDs.length; i++) {
                String paramFiltersUID = paramFiltersUIDs[i];
                try {
                    OrRef.Item item = dataRef.getItem(langId);
                    Object obj = (item != null) ? item.getCurrent() : null;
                    Kernel.instance().setFilterParam(paramFiltersUID, paramName, Collections.singletonList(obj));
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        }
        // формула после перемещения
        if (afterMoveFX != null) {
            Item item = dataRef.getItem(langId);
            if (item != null) {
                List<Object> selVect = new ArrayList<Object>();
                selVect.add(item.getCurrent());
                ClientOrLang orlang = new ClientOrLang(frame);
                try {
                    Map<String, Object> vc = new HashMap<String, Object>();
                    vc.put("SELOBJS", selVect);
                    boolean calcOwner = OrCalcRef.setCalculations();
                    orlang.evaluate(afterMoveFX, vc, this, new Stack<String>());
                    if (calcOwner)
                        OrCalcRef.makeCalculations();
                } catch (Exception ex) {
                    Util.showErrorMessage(table, ex.getMessage(), res.getString("afterMoveAction"));
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
                    getTable().getJTable().getSelectionModel().setSelectionInterval(i, i);
                }
            } else {
                try {
                    selfChange = true;
                    model.fireTableDataChanged();
                    int i = dataRef.getIndex();
                    if (!(this instanceof TreeTableAdapter) && !(this instanceof TreeTableAdapter2 && i >= getTable().getJTable().getRowCount()))
                        getTable().getJTable().getSelectionModel().setSelectionInterval(i, i);
                    if (dataRef.getLimit() != 0) {
                        table.setLimitExcceded(dataRef.isLimitExceeded());
                    }
                } finally {
                    selfChange = false;
                }
            }
        }
        countCurrentTableItem();
    }

    public void beforeCommitted() {
        int emptyRow = getEmptyRow();
        if (emptyRow > -1)
            try {
                deleteEmptyRow(emptyRow);
            } catch (KrnException e) {
                e.printStackTrace();
            }
    }

    public void cashCommitted() {
        insertedItems.clear();
    }

    public void cashRollbacked() {
        insertedItems.clear();
    }

    public void cashCleared() {
        isSort = false;
    }

    class FilterMenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src instanceof FilterMenuItem) {
                try {
                    applyFilter(((FilterMenuItem) src).filter);
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        hackEnabled = enabled;
        if (table != null)
            table.setEnabled(enabled);
        /*
         * if (dataRef != null) {
         * dataRef.setActive(enabled);
         * }
         */
    }

    protected void MoveTableView(int i) {
        JTable tb = getTable().getJTable();
        // TableCellEditor rend = tb.getCellEditor(i, 1);
        // Component cmp = rend.getTableCellEditorComponent(tb, null, true, i, 1);
        JViewport vp = ((AdvancedScrollPane) getTable().getScroller()).getViewport();
        Point p = vp.getViewPosition();
        // p.x = 0;//comp.getX();
        // p.y = cmp.getY();
        // int maxYExt = vp.getView().getHeight() - vp.getHeight();
        // p.y = Math.max(0, p.y);
        // p.y = Math.min((maxYExt < 0) ? 0 : maxYExt, ((p.y - 50) > 0) ? p.y - 50 : 0);
        int lastRow = vp.getHeight() / tb.getRowHeight() - 1;
        p.y = Math.max(0, (i - lastRow) * tb.getRowHeight());
        vp.setViewPosition(p);
    }

    public void scrollToVisible(int rowIndex, int vColIndex) {
        if (!(getTable().getJTable().getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) getTable().getJTable().getParent();
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = getTable().getJTable().getCellRect(rowIndex, vColIndex, true);

        // The location of the view relative to the table
        Rectangle viewRect = viewport.getViewRect();
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0).
        rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

        // Calculate location of rect if it were at the center of view
        int centerX = (viewRect.width - rect.width) / 2;
        int centerY = (viewRect.height - rect.height) / 2;

        // Fake the location of the cell so that scrollRectToVisible
        // will move the cell to the center
        if (rect.x < centerX) {
            centerX = -centerX;
        }
        if (rect.y < centerY) {
            centerY = -centerY;
        }
        rect.translate(centerX, centerY);
        if (viewRect.x + rect.x < 0)
            rect.x = -viewRect.x;
        if (viewRect.y + rect.y < 0)
            rect.y = -viewRect.y;
        // Scroll the area into view.
        viewport.scrollRectToVisible(rect);
    }

    protected void createRowBackColorRef(OrGuiComponent c) {
        String fx = table.getRowBackColorExpr();
        if (fx != null && !"".equals(fx)) {
            try {
                propertyName = "Свойство: Цвет фона";
                if (fx.trim().length() > 0) {
                    rowBackgroundRef = new OrCalcRef(fx, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame, c, propertyName, this);
                    rowBackgroundRef.addOrRefListener(this);
                    model.setRowBackColorCalc(true);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
            }
        }
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
                e.printStackTrace();
            }
        }
    }

    protected void createRowFontColorRef(OrGuiComponent c) {
        String fx = table.getRowFontColorExpr();
        if (fx != null && !"".equals(fx)) {
            try {
                propertyName = "Свойство: Цвет шрифта";
                if (fx.trim().length() > 0) {
                    rowForegroundRef = new OrCalcRef(fx, true, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame, c, propertyName, this);
                    rowForegroundRef.addOrRefListener(this);
                    model.setRowFontColorCalc(true);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // TODO Избавиться
    public RtTableModel getRtModel() {
        return model;
    }

    public void setSort(boolean sort) {
        isSort = sort;
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
                dataRef.deleteItem(this, row, this);
                if (calcOwner)
                    OrCalcRef.makeCalculations();
                return true;
            }
        }
        return false;
    }

    public List<ProcessRecordAction> getActions() {
        return processActions;
    }

    private void doBeforeDel(List<Object> selVect) {
        if (beforDelFX != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", selVect);
            try {
                boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(beforDelFX, vc, this, new Stack<String>());
                if (calcOwner)
                    OrCalcRef.makeCalculations();
            } catch (Exception e) {
                Util.showErrorMessage(table, e.getMessage(), this.res.getString("beforeDeleteAction"));
            }
        }
    }

    private void doAfterDel(List<Object> selVect) {
        if (afterDelFX != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", selVect);
            try {
                boolean calcOwner = OrCalcRef.setCalculations();
                orlang.evaluate(afterDelFX, vc, this, new Stack<String>());
                if (calcOwner)
                    OrCalcRef.makeCalculations();
            } catch (Exception e) {
                Util.showErrorMessage(table, e.getMessage(), this.res.getString("afterDeleteAction"));
            }
        }
    }

    /**
     * Получить максимально возможное количество записей на страницу
     * @return количество записей
     */
    public int getCountRowPage() {
        return countRowPage;
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
    
    public class ProcessRecordAction extends AbstractAction implements OrRefListener {
        private ProcessRecord record;
        private OrCalcRef enabledRef;
        private OrCalcRef visibleRef;
        private ASTStart template;

        public ProcessRecordAction(ProcessRecord record) throws KrnException {
            super();
            this.record = record;
            putValue(Action.NAME, record.getShortName() == null ? "" : record.getShortName().second);
            putValue(Action.SHORT_DESCRIPTION, record.getShortName() == null ? "" : record.getShortName().second);
            BufferedImage image = record.getImage();
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                putValue(Action.SMALL_ICON, icon);
                putValue(Action.LARGE_ICON_KEY, icon);
            }

            Expression expr = record.getEnabledExpr();
            if (expr != null) {
                try {
                    propertyName = "Доступность контекстного меню";
                    enabledRef = new OrCalcRef(expr.text, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame, table, propertyName, TableAdapter.this);
                    enabledRef.setTableRef(dataRef);
                    enabledRef.addOrRefListener(this);
                } catch (Exception e) {
                    showErrorNessage(e.getMessage());
                    e.printStackTrace();
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
                    e.printStackTrace();
                }
            }

            expr = record.getActionExpr();
            if (expr != null) {
                template = OrLang.createStaticTemplate(expr.text);
                Editor e = new Editor(expr.text);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            }

        }

        public void actionPerformed(ActionEvent e) {
            CursorToolkit.startWaitCursor(table.getTopLevelAncestor());
            if (record.getKrnObject() != null) {
                List<Item> items = getDataRef().getSelectedItems();
                if (items.size() > 0) {
                    List<Object> values = new ArrayList<Object>(items.size());
                    for (Item item : items)
                        values.add(item.getCurrent());
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put("OBJS", values);
                    try {
                        String[] res = Kernel.instance().startProcess(record.getKrnObject().id, vars);
                        if (res.length > 0 && !res[0].equals("")) {
                            MessagesFactory.showMessageDialog(table.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, res[0]);
                        } else {
                            List<String> param = new ArrayList<String>();
                                param.add("autoIfc");
                            if (res.length > 3) {
                                param.add(res[3]);
                            }
                            TaskTable.instance(false).startProcess(res[1], param);
                        }
                    } catch (KrnException ex) {
                        CursorToolkit.stopWaitCursor(table.getTopLevelAncestor());
                        ex.printStackTrace();
                    }
                    catch (ProcessException ex) {
                        CursorToolkit.stopWaitCursor(table.getTopLevelAncestor());
                        ex.printStackTrace();
                    }
                }
            } else if (template != null) {
                ClientOrLang jep = new ClientOrLang(frame);
                jep.setComponent(table);
                Map vc = new HashMap();
                try {
                    boolean calcOwner = OrCalcRef.setCalculations();
                    jep.evaluate(template, vc, TableAdapter.this, new Stack<String>());
                    if (calcOwner) {
                        OrCalcRef.makeCalculations();
                    }
                } catch (Exception ex) {
                    CursorToolkit.stopWaitCursor(table.getTopLevelAncestor());
                    ex.printStackTrace();
                    Util.showErrorMessage(table, ex.getMessage(), "Выражение");
                }
                
            }
            CursorToolkit.stopWaitCursor(table.getTopLevelAncestor());
        }

        public void setInterfaceLangId(long langId) {
            String str = frame.getString(record.getShortName() == null ? "" : record.getShortName().first);
            putValue(Action.NAME, str);
            putValue(Action.SHORT_DESCRIPTION, str);
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

        public void setFocus(int index, OrRefEvent e) {
        }
    }
    
	public void setData(List<String[]> data) {
		this.data = data;
		model.fireTableDataChanged();
	}

	public List<String[]> getData() {
		return data;
	}

	public int getRowIndex(int row) {
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
}
