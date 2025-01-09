package kz.tamur.comps;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.LINE_END;
import static kz.tamur.comps.Mode.DESIGN;
import static kz.tamur.comps.Mode.RUNTIME;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.gui.PopupMenuSupport;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TablePropertyRoot;
import kz.tamur.comps.ui.AdvancedScrollPane;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.HyperPopupAdapter;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter2;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.gui.OrMultiLineToolTip;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 19.03.2004
 * Time: 10:50:33
 * To change this template use File | Settings | File Templates.
 */
public class OrTable extends JPanel implements OrTableComponent, MouseTarget, PropertyChangeListener, MouseMotionListener {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private final ImageIcon YESMAN_DOWN = kz.tamur.rt.Utils.getImageIcon("goDown");
    private final ImageIcon YESMAN_RIGHT = kz.tamur.rt.Utils.getImageIcon("goRight");

    protected static final PropertyNode PROPS = new TablePropertyRoot();
    protected static final PropertyNode COLUMNS = PROPS.getChild("columns");
    protected static final PropertyNode PROP_BG_COLOR = PROPS.getChild("view").getChild("background").getChild("backgroundColor");
    protected static final PropertyNode PROP_NAVI_BG_COLOR = PROPS.getChild("view").getChild("navi").getChild("background")
            .getChild("backgroundColor");

    protected OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    protected boolean isCopy;
    protected Border standartBorder;
    protected Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());
    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    protected boolean isNaviExists;
    private boolean isAddPan;
    protected OrTableNavigator navi = null;
    protected boolean isFooterExist = false;
    protected OrTableFooter footer = null;
    protected boolean isSelfChange = false;

    protected JTable table = new JOrTable();
    protected TableScroller scroller;
    protected EventListenerList listeners = new EventListenerList();
    protected OrFrame frame;
    private Factory fm;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;

    private String title;
    private String titleUID;
    private FilterRecord[] filters;
    private String rowBackColorExpr;
    private String rowFontColorExpr;
    private Color zebraColor1 = Color.WHITE;
    private Color zebraColor2 = Color.WHITE;

    protected TableAdapter adapter;
    private byte[] description;
    private String descriptionUID;
    private JList rowHeader = null;
    private boolean visibleRowHeader = false;
    private int widthRowHeader = 25;
    private int filterBtnView = Constants.DIALOG;
    private int[] separators;

    private String limitExceededMessageId;
    private boolean limitExceeded = false;
    private boolean yesMan;
    private boolean autoAddRow = true;
    private String varName;
    // private JPopupMenu popupMenu;
    private int countProc = 0;
    public int countBtn = 10;
    private boolean isToolTip = false;
    private float alpha;
    /** Прозрачность таблицы */
    private boolean isOpaque;
    protected Color backgroundMain = Utils.getBlueSysColor();
    private int heightRow = Constants.HEIGHT_ROW;

    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem moveLeft = createMenuItem("Сдвинуть влево");
    private JMenuItem moveRight = createMenuItem("Сдвинуть вправо");
    private Point point;
    private OrPanel addPan;

    protected int tableViewType;
    protected boolean showTitle = true;
    protected boolean showPaging = true;
    protected int pageSize = 10; //TODO: pageSize
    protected String pageList = "10,20,30,40,50"; //TODO: pageList
    private boolean canSort = true;
    protected String selCol;

    protected boolean showHeader = true;
    protected boolean fitColumns = false;
    protected boolean deleteRowColumn = false;
    protected boolean rowNowrap = true;
    protected boolean showSearchLine = false;

    /**
     * Конструктор таблицы
     * 
     * @param xml
     * @param mode
     * @param fm
     * @param frame
     * @throws KrnException
     */
    OrTable(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        this.fm = fm;
        UUID = PropertyHelper.getUUID(this);

        moveLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == moveLeft) {
                    int columnIndex = table.columnAtPoint(point);
                    TableColumnModel columnModel = table.getColumnModel();
                    columnModel.moveColumn(columnIndex, columnIndex - 1);
                    DesignerFrame.instance().getComponentsTree().updateUI();
                }
            }
        });

        moveRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == moveRight) {
                    int columnIndex = table.columnAtPoint(point);
                    TableColumnModel columnModel = table.getColumnModel();
                    columnModel.moveColumn(columnIndex, columnIndex + 1);
                    DesignerFrame.instance().getComponentsTree().updateUI();
                }
            }
        });

        moveLeft.setIcon(kz.tamur.rt.Utils.getImageIconFull("MoveLeft.png"));
        moveRight.setIcon(kz.tamur.rt.Utils.getImageIconFull("MoveRight.png"));
        popupMenu.add(moveLeft);
        popupMenu.add(moveRight);

        final JTableHeader header = table.getTableHeader();
        if (Kernel.instance().getUser().isDesignerRun) {
            header.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        point = e.getPoint();
                        moveLeft.setEnabled(true);
                        moveRight.setEnabled(true);
                        int columnIndex = table.columnAtPoint(point);
                        if (columnIndex == table.getColumnCount() - 1) {
                            moveRight.setEnabled(false);
                        }
                        if (columnIndex == 0) {
                            moveLeft.setEnabled(false);
                        }
                        popupMenu.show(header, e.getX(), e.getY());
                    }
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                }
            });
        }

        TableModel m = getTableModel();
        table.setModel(m);
        scroller = new TableScroller(table);
        // установка прозрачности
        alpha = 1f - MainFrame.TRANSPARENT_CELL_TABLE / 100f;
        setLayout(new BorderLayout());
        MouseDelegator delegator = new MouseDelegator(this);
        scroller.addMouseListener(delegator);
        scroller.addMouseMotionListener(delegator);
        table.addMouseListener(delegator);
        table.addMouseListener(delegator);
        table.addMouseMotionListener(this);
        add(scroller, BorderLayout.CENTER);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setFocusable(mode == DESIGN);
        if (mode == DESIGN) {
            JTableHeader th = table.getTableHeader();
            th.setFocusable(true);
            th.addMouseListener(new MouseAdapter() {
            });
        } else {
            table.setCellSelectionEnabled(true);
            table.setRowSelectionAllowed(true);
        }
        table.setAutoCreateColumnsFromModel(false);
        PropertyValue pv = getPropertyValue(COLUMNS);
        if (!pv.isNull()) {
            List columns = pv.elementValue().getChildren();
            for (int i = 0; i < columns.size(); i++) {
                Element e = (Element) columns.get(i);
                OrGuiComponent comp = fm.create(e, mode, frame);
                comp.setGuiParent(this);
                addComponent(comp, true);
            }
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("tableViewType"));
        tableViewType = pv.isNull() ? (Integer) PROPS.getChild("view").getChild("tableViewType").getDefaultValue() : pv.enumValue();

        pv = getPropertyValue(PROPS.getChild("view").getChild("showTitle"));
        showTitle = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("showTitle").getDefaultValue() : pv.booleanValue();
        pv = getPropertyValue(PROPS.getChild("view").getChild("showPaging"));
        showPaging = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("showPaging").getDefaultValue() : pv.booleanValue();
        pv = getPropertyValue(PROPS.getChild("view").getChild("showColHeader"));
        showHeader = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("showColHeader").getDefaultValue() : pv.booleanValue();
        pv = getPropertyValue(PROPS.getChild("view").getChild("fitColumns"));
        fitColumns = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("fitColumns").getDefaultValue() : pv.booleanValue();
        pv = getPropertyValue(PROPS.getChild("view").getChild("deleteRowColumn"));
        deleteRowColumn = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("deleteRowColumn").getDefaultValue() : pv.booleanValue();
        pv = getPropertyValue(PROPS.getChild("view").getChild("rowNowrap"));
        rowNowrap = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("rowNowrap").getDefaultValue() : pv.booleanValue();

        pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("show"));
        isNaviExists = pv.booleanValue();

        pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("addPan"));
        isAddPan = pv.booleanValue();
        
        pv = getPropertyValue(PROPS.getChild("view").getChild("heightRow"));
        if (pv != null) {
            int value = pv.intValue();
            if (value > 0) {
                heightRow = value;
            }
        }

        if (isNaviExists) {
            navi = new OrTableNavigator(this);
            add(navi, BorderLayout.NORTH);
            // Определить разделители кнопок
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
                addPan = (OrPanel) fm.create(panel, mode, frame);
                addPan.setGuiParent(this);

                if (pv.isNull()) {
                    PropertyHelper.addProperty(new PropertyValue(addPan.getXml(), PROPS.getChild("addPanC")), xml);
                }
                navi.add(addPan, new GridBagConstraints(3, 0, 1, 1, 0, 0, LINE_END, BOTH, Constants.INSETS_1, 0, 0));
            }
        }

        PropertyNode pov = PROPS.getChild("pov");

        // считать атрибут отвечающий за формат отображения фильтра
        PropertyNode filterBtnAttr = pov.getChild("filterBtnAttr");
        pv = getPropertyValue(filterBtnAttr);
        // если он пуст то задать его формат как "Диалог"
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(Constants.DIALOG, filterBtnAttr));
        }

        filterBtnView = pv.intValue();

        PropertyNode access = pov.getChild("access");
        pv = getPropertyValue(access);
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(Constants.FULL_ACCESS, access));
        }

        if (mode == RUNTIME) {
            PropertyNode pn = pov.getChild("maxObjectCountMessage");
            pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                limitExceededMessageId = (String) pv.resourceStringValue().first;
            }
        }

        pv = getPropertyValue(pov.getChild("canSort"));
		if (pv != null && !pv.isNull()) {
            canSort = pv.booleanValue();
        } else {
            canSort = true;
        }

        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }

        pv = getPropertyValue(PROPS.getChild("rowNums").getChild("isVisibleNumRows"));
        if (!pv.isNull()) {
            visibleRowHeader = pv.booleanValue();
        }

        pv = getPropertyValue(PROPS.getChild("rowNums").getChild("width"));
        if (!pv.isNull()) {
            widthRowHeader = pv.intValue();
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
        updateHeaderRenderers();
        if (mode == DESIGN) {
            table.getColumnModel().addColumnModelListener(getTableColumnModelListener());
        }

        table.setGridColor(Color.white);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
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
        PropertyNode pn = getProperties().getChild("view").getChild("background");
        pv = getPropertyValue(pn.getChild("zebra").getChild("color1"));
        zebraColor1 = pv.isNull() ? null : pv.colorValue();

        pv = getPropertyValue(pn.getChild("zebra").getChild("color2"));
        zebraColor2 = pv.isNull() ? null : pv.colorValue();

        pv = getPropertyValue(getProperties().getChild("view").getChild("pageSize"));
        pageSize = pv.intValue();
        pv = getPropertyValue(getProperties().getChild("view").getChild("pageList"));
        pageList = pv.stringValue();

        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("gradient"));
        if (!pv.isNull() && navi != null) {
            // градиентная заливка компонента
            navi.setGradient((GradientColor) pv.objectValue());
        }
        // прозрачность компонента(да/нет)
        pv = getPropertyValue(pn.getChild("transparent"));
        isOpaque = !pv.booleanValue();
        setTransparent(isOpaque);

        if (mode == RUNTIME) {
            setRowFontColorExpr();
            setRowBackColorExpr();

            // Создаем адаптер
            adapter = createAdapter();

            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, adapter);
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            isToolTip = true;
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = (String) pv.resourceStringValue().first;
                    isToolTip = toolTipUid != null;
                    byte[] toolTip = frame.getBytes(toolTipUid);
                    if (toolTip != null) {
                        setToolTipText(new String(toolTip));
                    }
                }
            }

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    updateToolTip();
                }
            });

            // Создаем контекстное меню
            final PopupMenuSupport popupSupport = PopupMenuSupport.create(adapter);
            if (popupSupport != null) {
                // Если оно есть, то вызываем его по щелчку правой кнопки мыши
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupSupport.getMenu().show(table, e.getX(), e.getY());
                        }
                    }
                });
            }

            // Создаем меню процессов TODO реализовать общий механизм с контекстным меню
            List<TableAdapter.ProcessRecordAction> actions = adapter.getActions();
            if (actions != null) {
                final JPopupMenu popupMenu = new JPopupMenu();
                for (Action action : actions) {
                    popupMenu.add(createMenuItem(action));
                    if (isNaviExists && action.getValue(Action.LARGE_ICON_KEY) != null) {
                        navi.addAction(action);
                    }
                }
                // так как были добавлены новые кнопки на панель необходимо
                // обновить их состояние
                updateNaviButtons();
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            // получить координаты курсора
                            Point location = MouseInfo.getPointerInfo().getLocation();
                            // преобразование координат
                            SwingUtilities.convertPointFromScreen(location, OrTable.this);
                            popupMenu.show(OrTable.this, (int) location.getX(), (int) location.getY());
                        }
                    }
                });
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getClickCount() == 2) {
                        Container cnt = getTopLevelAncestor();
                        if (cnt instanceof DesignerDialog) {
                            boolean b = !(getAdapter() instanceof TreeTableAdapter)
                                    && !(getAdapter() instanceof TreeTableAdapter2) && getAdapter().isOnlyChildren();
                            if (b) {
                                ((DesignerDialog) cnt).processOkClicked();
                            }
                        }
                    }
                }
            });
        }

        /*
         * добавление слушателя, который будет перерисовывать родителя компонента если компонент прозрачен
         * необходимо для удаления артефактов прорисовки при изменении размеров прозрачных компонентов
         */
        addComponentListener(new ComponentListener() {

            public void componentShown(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                if (isOpaque() && getTopLevelAncestor() != null) {
                    getTopLevelAncestor().repaint();
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
        init();
    }

    protected TableAdapter createAdapter() throws KrnException {
        // TODO Перевести на OrFrame
        return new TableAdapter(frame, this, false);
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

    public FilterRecord[] getFilters() {
        return filters;
    }

    protected void init() {
        // Установить прозрачный фон заголовка
        table.getTableHeader().setBackground(new Color(0, 0, 0, 0));
        PropertyValue pv = getPropertyValue(PROP_BG_COLOR);
        if (!pv.isNull()) {
            backgroundMain = pv.colorValue();
        }

        if (isOpaque) {
            scroller.setBackground(backgroundMain);
            scroller.getViewport().setBackground(backgroundMain);
            table.getTableHeader().setBackground(backgroundMain);
        }

        pv = getPropertyValue(PROP_NAVI_BG_COLOR);
        if (!pv.isNull() && isNaviExists) {
            if (isNaviExists)
                navi.setBackground((Color) pv.colorValue());
        }
    }

    protected DefaultOrTableModel getTableModel() {
        return new DefaultOrTableModel();
    }

    protected OrTableColumnModelListener getTableColumnModelListener() {
        return new OrTableColumnModelListener(table);
    }

    public void setFooter() {
        if (mode == RUNTIME) {
            PropertyValue pv = getPropertyValue(PROPS.getChild("pov").getChild("footer"));
            if (!pv.isNull()) {
                isFooterExist = pv.booleanValue();
            }
            if (isFooterExist) {
                TableAdapter.RtTableModel model = (TableAdapter.RtTableModel) table.getModel();
                ArrayList columns = new ArrayList();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    columns.add(model.getColumnAdapter(i));
                }
                footer = new OrTableFooter(table, columns);
                JTableHeader th = table.getTableHeader();
                if (th != null) {
                    Dimension dim = th.getPreferredSize();
                    if (dim.height < 20)
                        dim.height = 20;
                    footer.setPreferredSize(dim);
                }
                scroller.setColumnFooterView(footer);
            }
        }
    }

    public boolean canAddComponent(int x, int y) {
        return true;
    }

    public void addComponent(OrGuiComponent c, int x, int y) {
        // Выделить слушателей на удаление
        java.util.List<OrGuiComponent> copyList = new ArrayList<OrGuiComponent>(c.getListListeners());
        // Добавить слушателей родителя в добавляемый компонент
        c.setListListeners(listListeners, copyList);
        addComponent(c, false);
    }

    private void addComponent(OrGuiComponent c, boolean isLoading) {
        if (c instanceof OrTableColumn) {
            OrTableColumn col = (OrTableColumn) c;
            col.setOrTable(this);
            DefaultOrTableModel m = (DefaultOrTableModel) table.getModel();
            col.addPropertyChangeListener(this);
            m.addColumn(col);
            if (!isLoading) {
                PropertyHelper.addProperty(new PropertyValue(c.getXml(), COLUMNS), xml);
            }
        }
    }

    public void removeComponent(OrGuiComponent c) {
        if (c instanceof OrTableColumn) {
            DefaultOrTableModel m = (DefaultOrTableModel) table.getModel();
            m.removeColumn((OrTableColumn) c);
            PropertyHelper.removeProperty(new PropertyValue(c.getXml(), COLUMNS), xml);
        }
        revalidate();
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    public void setEnabled(boolean enabled) {
        if (mode == RUNTIME) {
            super.setEnabled(enabled);
            Component[] comps = getComponents();
            for (Component comp : comps) {
                if (comp instanceof OrTableNavigator) {
                    comp.setEnabled(enabled);
                }
            }
        }
    }

    public void setDelEnabled(boolean enabled) {
        if (mode == RUNTIME) {
            Component[] comps = getComponents();
            for (Component comp : comps) {
                if (comp instanceof OrTableNavigator) {
                    ((OrTableNavigator) comp).setDelEnabled(enabled);
                }
            }
        }
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        String name = value.getProperty().getName();
        if (value.getProperty() == PROP_BG_COLOR) {
            Color c = value.isNull() ? (Color) PROP_BG_COLOR.getDefaultValue() : value.colorValue();
            if (isOpaque) {
                scroller.setBackground(c);
                scroller.getViewport().setBackground(c);
                table.getTableHeader().setBackground(c);
            }
        }

        if (value.getProperty() == PROP_NAVI_BG_COLOR) {
            Color c = value.isNull() ? (Color) PROP_NAVI_BG_COLOR.getDefaultValue() : value.colorValue();
            if (isNaviExists)
                navi.setBackground(c);
        }

        if ("title".equals(name)) {
            firePropertyModified();
        } else if ("show".equals(name)) {
            isNaviExists = value.booleanValue();
            if (!isNaviExists && navi != null) {
                remove(navi);
                navi = null;
                validate();
            } else {
                if (navi == null) {
                    navi = new OrTableNavigator(this);
                }
                add(navi, BorderLayout.NORTH);
                updateNaviButtons();
                validate();
                repaint();
            }
        } else if ("background".equals(name)) {
            setBackground(value.isNull() ? (Color) PROPS.getChild("view").getChild("background").getChild("backgroundColor").getDefaultValue() : value.colorValue());
            repaintAll();
        } else if ("gradient".equals(name)) {
            // если градиентная заливка отключена, необходимо перерисовать компоеннт основным его цветом
            if (value.isNull()) {
                PropertyValue pv_ = getPropertyValue(PROP_NAVI_BG_COLOR);
                if (!pv_.isNull() && isNaviExists) {
                    navi.setBackground((Color) pv_.colorValue());
                }
                repaintAll();
            }
        } else if ("transparent".equals(name)) {
            isOpaque = !value.booleanValue();
            setTransparent(isOpaque);
            repaintAll();
        } else if ("showTitle".equals(name)) {
            showTitle = value.booleanValue();
        } else if ("showPaging".equals(name)) {
            showPaging = value.booleanValue();
        } else if ("showColHeader".equals(name)) {
            showHeader = value.booleanValue();
        } else if ("fitColumns".equals(name)) {
            fitColumns = value.booleanValue();
        } else if ("deleteRowColumn".equals(name)) {
            deleteRowColumn = value.booleanValue();
        } else if ("rowNowrap".equals(name)) {
            rowNowrap = value.booleanValue();
        } else if ("pageSize".equals(name)) {
            pageSize = value.intValue();
        } else if ("pageList".equals(name)) {
            pageList = value.stringValue();
        } else if ("isVisibleNumRows".equals(name)) {
            visibleRowHeader = value.booleanValue();
        }
        
        if (isNaviExists) {
            if ("addPan".equals(name)) {
                isAddPan = value.booleanValue();
                if (isAddPan) {
                    Element panel;
                    PropertyValue pv = getPropertyValue(PROPS.getChild("addPanC"));
                    if (pv.isNull()) {
                        panel = new Element("Component");
                        panel.setAttribute("class", "Panel");
                    } else {
                        List children = pv.elementValue().getChildren();
                        panel = (Element) children.get(0);
                    }
                    try {
                        addPan = (OrPanel) fm.create(panel, mode, frame);
                        addPan.setGuiParent(this);
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }

                    if (pv.isNull()) {
                        PropertyHelper.addProperty(new PropertyValue(addPan.getXml(), PROPS.getChild("addPanC")), xml);
                    }
                    navi.add(addPan, new GridBagConstraints(3, 0, 1, 1, 0, 0, LINE_END, BOTH, Constants.INSETS_1, 0, 0));
                } else {
                    if (navi != null && addPan != null) {
                        navi.remove(addPan);
                        addPan = null;
                    }
                }
                DesignerFrame.instance().getComponentsTree().updateUI();
            }
            updateNaviButtons();
        }
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    public OrTableFooter getFooter() {
        return footer;
    }

    protected class TableScroller extends AdvancedScrollPane implements Place {
        public TableScroller(Component view) {
            super(view);
            if (!isOpaque) {
                setColumnHeader(new JViewport());
                getColumnHeader().setOpaque(false);
            }
        }
    }

    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUID);
        if (mode == RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }

            // Обновляем наименования процессов
            List<TableAdapter.ProcessRecordAction> actions = adapter.getActions();
            if (actions != null)
                for (TableAdapter.ProcessRecordAction action : actions)
                    action.setInterfaceLangId(langId);
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        OrTableModel m = (OrTableModel) table.getModel();
        m.setInterfaceLangId(langId);
        if (navi != null) {
            navi.setInterfaseLangId(langId);
            if (addPan != null) {
                addPan.setLangId(langId);

            }
        }
        updateLimitExceedMessage();
    }

    private void updateLimitExceedMessage() {
        if (navi != null) {
            navi.setMessage(limitExceeded && limitExceededMessageId != null ? frame.getString(limitExceededMessageId) : "");
        }
    }

    public void setLimitExcceded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
        updateLimitExceedMessage();
    }

    public JTable getJTable() {
        return table;
    }

    public TableScroller getScroller() {
        return scroller;
    }

    public void CellError() {

    }

    /**
     * Модель данных для таблицы
     */
    protected class DefaultOrTableModel extends AbstractTableModel implements OrTableModel, PropertyChangeListener {

        protected List<OrTableColumn> columns = new ArrayList<OrTableColumn>();

        public void addColumn(OrTableColumn col) {
            col.addPropertyChangeListener(this);
            columns.add(col);
            JTable tb = getJTable();
            col.setModelIndex(columns.size() - 1);
            TableColumn column = new TableColumn(columns.size() - 1, col.getPreferredWidth());
            tb.getColumnModel().addColumn(column);
            column.setMaxWidth(col.getMaxWidth());
            column.setMinWidth(col.getMinWidth());
            fireTableStructureChanged();
        }

        public void removeColumn(OrTableColumn col) {
            col.removePropertyChangeListener(this);
            int idx = -1;
            for (int i = 0; i < columns.size(); i++) {
                OrTableColumn removingColumn = (OrTableColumn) columns.get(i);
                if (removingColumn == col) {
                    idx = i;
                    break;
                }
            }
            columns.remove(col);
            if (idx != -1) {
                TableColumnModel tcm = table.getColumnModel();
                TableColumn c = tcm.getColumn(idx);
                tcm.removeColumn(c);
            }
            fireTableStructureChanged();
        }

        public void moveColumn(int startIndex, int endIndex) {
            OrTableColumn tc = (OrTableColumn) columns.get(startIndex);
            Utils.moveListElementTo(columns, startIndex, endIndex);
            Element elem = xml.getChild("columns");
            List list = elem.getChildren();
            Utils.moveListElementTo(list, startIndex, endIndex);
            fireTableStructureChanged();
            ControlTabbedContent.instance().propertyModified(tc);
            scroller.getColumnHeader().repaint();
        }

        public int getColumnCount() {
            return columns.size();
        }

        public Color getZebra1Color() {
            return null;
        }

        public Color getZebra2Color() {
            return null;
        }

        public ColumnAdapter getColumnAdapter(int columnIndex) {
            return null;
        }

        public Map getUniqueMap() {
            return null;
        }

        public int getRowCount() {
            return 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        public String getColumnName(int column) {
            return getColumn(column).getTitle();
        }

        public OrTableColumn getColumn(int colIndex) {
            return (OrTableColumn) columns.get(colIndex);
        }

        public void setInterfaceLangId(long langId) {
            for (int i = 0; i < columns.size(); i++) {
                OrTableColumn column = (OrTableColumn) columns.get(i);
                column.setLangId(langId);
            }
            fireTableStructureChanged();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            final String name = evt.getPropertyName();
            if ("text".equals(name) || "editor".equals(name) || "font".equals(name) || "backgroundColorCol".equals(name)
                    || "fontColorCol".equals(name)) {
                fireTableStructureChanged();
            } else if ("pref".equals(name) || "max".equals(name) || "min".equals(name)) {
                isSelfChange = true;
                fireTableStructureChanged();
                isSelfChange = false;
            }
        }

        public void fireTableStructureChanged() {
            TableColumnModel columnModel = table.getColumnModel();
            updateHeaderRenderers();
            if (columnModel.getColumnCount() > 0) {
                // isSelfChange = false;
                for (int i = 0; i < columns.size(); i++) {
                    OrTableColumn column = (OrTableColumn) columns.get(i);
                    TableColumn tc = columnModel.getColumn(i);
                    tc.setPreferredWidth(column.getPreferredWidth());
                    tc.setMaxWidth(column.getMaxWidth());
                    tc.setMinWidth(column.getMinWidth());
                }
            }
            super.fireTableStructureChanged();
        }

        public void superFireTableStructureChanged() {
            super.fireTableStructureChanged();
        }

    }

    public int getMode() {
        return mode;
    }

    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener) list[i]).propertyModified(this);
        }
    }

    public String getTitle() {
        return title;
    }

    protected void updateHeaderRenderers() {
        DefaultOrTableModel model = (DefaultOrTableModel) table.getModel();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            OrTableColumn col = model.getColumn(i);
            DefaultTableCellRenderer rend = (DefaultTableCellRenderer) col.createDefaultRenderer();
            tc.setHeaderRenderer(rend);
        }
        table.setRowHeight(heightRow);
        repaintAll();
    }

    public Color getZebraColor1() {
        return zebraColor1;
    }

    public Color getZebraColor2() {
        return zebraColor2;
    }

    class OrTableColumnModelListener implements TableColumnModelListener {

        JTable table;

        public OrTableColumnModelListener(JTable table) {
            this.table = table;
        }

        public void columnAdded(TableColumnModelEvent e) {

        }

        public void columnRemoved(TableColumnModelEvent e) {

        }

        public void columnMoved(TableColumnModelEvent e) {
            if (mode == DESIGN) {
                int fromIdx = e.getFromIndex();
                int toIdx = e.getToIndex();
                if (fromIdx != toIdx) {
                    DefaultOrTableModel model = (DefaultOrTableModel) table.getModel();
                    model.moveColumn(fromIdx, toIdx);

                }
            }
            repaintAll();
        }

        public void columnMarginChanged(ChangeEvent e) {
            if (!isSelfChange && mode == DESIGN) {
                TableColumnModel columnModel = table.getColumnModel();
                OrTableColumn column = null;
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    column = ((DefaultOrTableModel) table.getModel()).getColumn(i);
                    TableColumn tableColumn = columnModel.getColumn(i);
                    if (column.getPreferredWidth() != tableColumn.getWidth()) {
                        column.setPropertyValue(new PropertyValue(tableColumn.getWidth(), column.getProperties()
                                .getChild("width").getChild("pref")));
                        ControlTabbedContent.instance().propertyModified(column);
                    }
                }
            }
            repaintAll();
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
            System.out.println("");
        }

    }

    public boolean isNaviExists() {
        return isNaviExists;
    }

    public OrTableNavigator getNavi() {
        return navi;
    }

    public boolean isYesMan() {
        return yesMan;
    }

    public boolean isAutoAddRow() {
        return autoAddRow;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    /**
     * Получить tab index.
     * 
     * @return the tab index
     */
    public int getTabIndex() {
        return -1;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    private void updateNaviButtons() {
        if (navi != null) {
            PropertyNode pn = getProperties().getChild("extended");
            PropertyValue pv = getPropertyValue(pn.getChild("gradient"));
            if (!pv.isNull() && navi != null) {
                // градиентная заливка компонента
                navi.setGradient((GradientColor) pv.objectValue());
            }
            repaintAll();
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
                pn = buttonsNode.getChildAt(i);
                if (pn.getChildCount() > 0) {
                    for (int j = 0; j < pn.getChildCount(); j++) {
                        pv = getPropertyValue(pn.getChildAt(j));
                        if ("naviBtnTooltip".equals(pn.getChildAt(j).getName())) {
                        	String toolTip = pv.stringValue();
                    		navi.setButtonsToolTip(pn, toolTip);
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
                	continue;
                }
                pv = getPropertyValue(pn);
                vis = false;
                // Если это не строка(в которой содержатся позиции сепараторов)
                if (pv.objectValue() instanceof Boolean || pn.getDefaultValue() instanceof Boolean) {
                    if (!pv.isNull()) {
                        vis = pv.booleanValue();
                    } else {
                        vis = ((Boolean) pn.getDefaultValue()).booleanValue();
                        setPropertyValue(new PropertyValue(vis, pn));
                    }
                    
                    navi.setButtonsVisible(pn, vis);
                    String propName = pn.getName();
                    if ("findBtn".equals(propName)) {
                    	showSearchLine = vis;
                    }
                }
            }

            navi.setSeparator(separators, navi.indxBtn);
            pv = getPropertyValue(PROPS.getChild("view").getChild("navi").getChild("yesManDirection"));

            if (pv == null || pv.isNull() || pv.intValue() == Constants.DIRECTION_RIGHT) {
                navi.getButtonByName("yesManBtn").setIcon(YESMAN_RIGHT);
                yesMan = true;
                autoAddRow = true;
            } else if (pv.intValue() == Constants.DIRECTION_DOWN) {
                navi.getButtonByName("yesManBtn").setIcon(YESMAN_DOWN);
                yesMan = false;
                autoAddRow = true;
            } else if (pv.intValue() == Constants.DIRECTION_RIGHT_WITHOUT_ADD_ROW) {
                navi.getButtonByName("yesManBtn").setIcon(YESMAN_RIGHT);
                yesMan = true;
                autoAddRow = false;
            } else if (pv.intValue() == Constants.DIRECTION_DOWN_WITHOUT_ADD_ROW) {
                navi.getButtonByName("yesManBtn").setIcon(YESMAN_DOWN);
                yesMan = false;
                autoAddRow = false;
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if ("text".equals(name)) {
            ((DefaultTableColumnModel) table.getColumnModel()).propertyChange(new PropertyChangeEvent(this, "width", null, null));
        } else if ("pref".equals(name)) {
            ((DefaultTableColumnModel) table.getColumnModel()).propertyChange(new PropertyChangeEvent(this, "pref", null, null));
        }
    }

    public int getTableComponentCount() {
        AbstractTableModel model = (AbstractTableModel) table.getModel();
        return model.getColumnCount();
    }

    public OrTableColumn getTableComponent(int n) {
        DefaultOrTableModel model = (DefaultOrTableModel) table.getModel();
        return model.getColumn(n);
    }

    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return table.requestFocusInWindow();
    }

    public OrColumnComponent getColumnAt(int col) {
        OrTableModel model = (OrTableModel) table.getModel();
        return model.getColumn(col);
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        int column = table.getColumnModel().getColumnIndexAtX(p.x);
        TableColumn tc = table.getColumnModel().getColumn(column);
        TableCellEditor tcr = tc.getCellEditor();
        setCursor(tcr instanceof HyperPopupAdapter.HiperPopupCellEditor ? Constants.HAND_CURSOR : Constants.DEFAULT_CURSOR);
        repaintAll();
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public OrFrame getFrame() {
        return frame;
    }

    // RowHeader
    class RowHeaderRenderer extends JLabel implements ListCellRenderer {
        private JTable table = null;

        public RowHeaderRenderer(JTable table) {
            this.table = table;
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());

            if (isSelected && cellHasFocus) {
                table.setColumnSelectionAllowed(false);
                table.setRowSelectionInterval(index, index);
                table.scrollRectToVisible(table.getCellRect(index, 0, false));
                table.requestFocus();
            } else {
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            }

            return this;
        }
    }

    public void setRowHeader(ListModel lm) {
        rowHeader = new JList(lm);
        rowHeader.setForeground(table.getTableHeader().getForeground());
        rowHeader.setBackground(table.getTableHeader().getBackground());
        rowHeader.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rowHeader.setAutoscrolls(false);
        rowHeader.setFixedCellHeight(table.getRowHeight());
        if (table.getRowCount() == 0) {
            rowHeader.setFixedCellWidth(widthRowHeader);
        }
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        scroller.setRowHeaderView(rowHeader);
        if (rowHeader.getCellRenderer()
                .getListCellRendererComponent(rowHeader, lm.getElementAt(lm.getSize() - 1), lm.getSize() - 1, false, false)
                .getPreferredSize().getWidth() < widthRowHeader)
            rowHeader.setFixedCellWidth(widthRowHeader);

    }

    public void setVisibleRowHeader(boolean visible) {
        visibleRowHeader = visible;
        if (visible) {
            if (rowHeader == null) {
                repaint();
            } else {
                scroller.setRowHeaderView(rowHeader);
            }
        } else {
            scroller.setRowHeaderView(null);
        }
    }

    /**
     * Удаляет названия строк.
     */
    public void removeRowHeader() {
        scroller.setRowHeaderView(null);
        rowHeader = null;
    }

    public void repaint() {
        if ((getParent() != null) && (rowHeader == null) && (table.getRowCount() > 0) && (visibleRowHeader)) {
            ListModel lm = new AbstractListModel() {
                public int getSize() {
                    return table.getRowCount();
                }

                public Object getElementAt(int index) {
                    return new Integer(index + 1);
                }
            };
            setRowHeader(lm);
        }
        if (rowHeader != null) {
            if (table.getRowCount() == 0)
                rowHeader.setFixedCellWidth(widthRowHeader);
            rowHeader.revalidate();
            rowHeader.repaint();
        }
        super.repaint();
    }

    // RowHeader
    public class JOrTable extends JTable {
        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
            boolean res = super.processKeyBinding(ks, e, condition, pressed);
            TableCellEditor ed = getCellEditor();
            if (ed instanceof ComboBoxAdapter.OrComboCellEditor) {
                ComboBoxAdapter.OrComboCellEditor cbe = (ComboBoxAdapter.OrComboCellEditor) ed;
                OrComboBox cb = cbe.getComboBox();
                JTextField tf = (JTextField) cb.getEditor().getEditorComponent();
                if (cb.getAdapter() != null && cb.getAdapter().isEditor() && tf != null && !tf.hasFocus()
                        && e.getKeyCode() != KeyEvent.VK_SPACE && e.getKeyChar() != ' ') {
                    cb.setEditable(true);
                    cb.getComboBox().showPopup();
                    tf.requestFocus();
                    tf.setText(String.valueOf(e.getKeyChar()));
                }
            }
            return res;
        }

        /**
         * Задать прозрачность для таблицы.
         * 
         * @param g
         *            the g
         */
        protected void paintComponent(Graphics g) {
            // задание прозрачности, необходимо провести ДО вызова суперкласса
            if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
            super.paintComponent(g);
        }
    }

    @Override
    public void requestFocus() {
        table.requestFocus();
    }

    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName())) {
            return this;
        }

        int count = getTableComponentCount();
        for (int i = 0; i < count; i++) {
            OrColumnComponent c = getColumnAt(i);
            if (c != null && title.equals(c.getVarName())) {
                return c;
            }
        }
        return null;
    }

    public String getVarName() {
        return varName;
    }

    /**
     * Получить формат отображения формы задания фильтра
     * 
     * @return 0 - Диалоговое окно; 1 - Выпадающее меню
     */
    public int getFilterBtnView() {
        return filterBtnView;
    }

    /**
     * Возвращает массив позиций кнопок, после которых нужно поставить разделитель.
     * 
     * @return
     */
    public int[] getSeparators() {
        return separators;
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

    @Override
    public JToolTip createToolTip() {
        return isToolTip ? super.createToolTip() : new OrMultiLineToolTip();
    }

    /**
     * Перерисовать компонент и всех его потомков
     */
    public void repaintAll() {
        repaint();
        table.repaint();
        table.getTableHeader().repaint();
        scroller.repaint();
        scroller.getViewport().repaint();
        scroller.getColumnHeader().repaint();
    }

    /**
     * Установить прозрачность компонента.
     * 
     * @param isOpaque
     *            the new transparent
     */
    void setTransparent(boolean isOpaque) {
        setOpaque(isOpaque);
        scroller.setOpaque(isOpaque);
        scroller.getViewport().setOpaque(isOpaque);
    }

    /**
     * Обновление всплывающей подсказки для компонента
     */
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                isToolTip = !toolTipExprText_.isEmpty();
                if (!isToolTip) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    /**
     * @return the addPan
     */
    public OrPanel getAddPan() {
        return addPan;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setListListeners(listListeners, listForDel);
            }
        }
    }
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    
    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isShowPaging() {
        return showPaging;
    }
    
    public boolean isShowHeader() {
		return showHeader;
	}

	public boolean isFitColumns() {
		return fitColumns;
	}

	public boolean isDeleteRowColumn() {
		return deleteRowColumn;
	}

	public boolean isRowNowrap() {
		return rowNowrap;
	}

	public int getPageSize() { //TODO: pageSize
        return pageSize;
    }
    
    public String getPageList() { //TODO: pageSize
        return pageList;
    }

    public void setData(List<String[]> data) {
        adapter.setData(data);
    }

    public List<String[]> getData() {
        return adapter.getData();
    }

    public boolean isCanSort() {
        return canSort;
    }

    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }

    /**
     * @return the visibleRowHeader
     */
    public boolean isVisibleRowHeader() {
        return visibleRowHeader;
    }
    
    public void selectColumn(String value) {
        selCol = value;
    }

    public String getSelectValue() {
        return null;
    }

    public String getPathOfSelectedColumn() {
        return null;
    }

    public KrnAttribute getAttributeOfSelectedColumn() {
        return null;
    }

	public int getTableViewType() {
		return tableViewType;
	}

	@Override
    public int getRowCount() {
        return (table.getModel() != null) ? table.getModel().getRowCount() : 0;
    }

	@Override
    public int getColumnCount() {
        return (table.getModel() != null) ? table.getModel().getColumnCount() : 0;
    }

	public boolean isShowSearchLine() {
		return showSearchLine;
	}
}
