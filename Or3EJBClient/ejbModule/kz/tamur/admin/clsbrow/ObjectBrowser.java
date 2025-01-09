package kz.tamur.admin.clsbrow;

import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.NumberFormatter;

import kz.tamur.admin.AttributeListPanel;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.GlobalConfig;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.SearchWindow;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.util.CursorToolkit;

@SuppressWarnings("serial")
public class ObjectBrowser extends GradientPanel implements ActionListener {
    private static final int MAX_PROPERTIES_COUNT = 1000;

    private static String datePattern = "yyyy-MM-dd";
    private KrnClass cls;
    private ObjectPropertyInspector inspector = new ObjectPropertyInspector(this);

    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton createBtn = createToolButton("createObject", "Создать объект");
    private JButton deleteBtn = createToolButton("deleteObjs", "Удалить объект");
    private JButton copyBtn = createToolButton("copyObject", "Создать копию объекта");
    private JButton refreshBtn = createToolButton("refreshObject", "Обновить объект");
    private JButton xmlBtn = createToolButton("XML", "");
    public JButton prevBtn = createToolButton("Cancel", "Назад");
    public JButton applyBtn = createToolButton("checkOk", "Применить");
    public JButton rollbackBtn = createToolButton("Delete", "Отмена");
    private JFormattedTextField transField = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#")));
    private JButton reloadTransBtn = createToolButton("reloadTrans", "Загрузить объекты транзакции");
    private JButton nullerBtn = new JButton("Обнулить");
    private JComboBox transCombo = new JComboBox(new String[] { "", "Все", "Только 0", "Кроме 0" });
    private JCheckBox checkOnly = Utils.createCheckBox("Только этот класс", false);
    private JButton findBtn = createToolButton("Find", "Поиск объекта по ID");
    private JButton searchBtn = createToolButton("findNavi", "Поиск объекта по выбранному атрибуту");
    public JButton floatModeBtn = createToolButton("RestoreIcon", ".png", "Развернуть");
    // Popup menus
    private JPopupMenu objectOperations = new JPopupMenu();
    private JMenuItem objectCreateItem = createMenuItem("Создать Объект");
    private JMenuItem objectDeleteItem = createMenuItem("Удалить Объект");
    private JMenuItem objectCloneItem = createMenuItem("Создать копию объекта");
    private JMenuItem objectRefreshItem = createMenuItem("Обновить объект");
    private JMenuItem getXmlItem = createMenuItem("XML");
    private JMenuItem findByIdItem = createMenuItem("Найти по ID объекта", "Find");
    private JMenuItem searchByAttrItem = createMenuItem("Найти объект по выбранному атрибуту", "findNavi");
    private JMenuItem searchNext = createMenuItem("Поиск по выбранному атрибуту (Далее)");
    private JSplitPane splitter = new JSplitPane();
    private String[] classObjectsTableColumns = new String[] { "classId", "id", "UID" };
    private String[] classObjectsTableColumns2 = new String[] { "UID" };
    private JTable classObjectsTable;
    private KrnObjectTableModel objsModel = new KrnObjectTableModel();

    public static long transId = 0;

    // Search attributes
    private KrnAttribute attr;
    private TreeSet<KrnObjectItem> findByAttr_;
    private long langId = com.cifs.or2.client.Utils.getDataLangId();
    private JLabel colLabel = Utils.createLabel("Кол-во:");
    private SearchWindow searchWindow;
    private JCheckBox limitEnabled = Utils.createCheckBox("Ограничение", true);
    private JTextField limitCount = Utils.createDesignerTextField();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private SearchInterfacePanel sip = null;
    private static GlobalConfig config = GlobalConfig.instance(Kernel.instance());
    private boolean isAdvancedSearch = true;
    private boolean isSearchMode = false;

    /**
     * Конструктор класса object browser.
     * 
     * @param cls
     *            the cls
     * @throws KrnException
     *             the krn exception
     */
    public ObjectBrowser(KrnClass cls, boolean isSearchMode) throws KrnException {
        setColumnCount(3);
        classObjectsTable = new JTable(null, classObjectsTableColumns);
        this.cls = cls;
        this.isSearchMode = isSearchMode;
        classObjectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (objsModel.getRowCount() > 0) {
                    if (classObjectsTable.getSelectedRow() > -1) {
                        BlobAttrEditorDelegate.setObjectID(Long.parseLong(objsModel.getValueAt(
                                classObjectsTable.getSelectedRow(), 1).toString()));
                    }
                }
            }
        });
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ObjectBrowser(List<KrnObject> krnObjects, boolean isAdvancedSearch) {
    	this.isAdvancedSearch = isAdvancedSearch;
        findByAttr_ = new TreeSet<KrnObjectItem>(new Comparator<KrnObjectItem>() {
            public int compare(KrnObjectItem o1, KrnObjectItem o2) {
                return (o1.obj.id < o2.obj.id ? -1 : o1.obj.id == o2.obj.id ? 0 : 1);
            }
        });
        setColumnCount(1);
        classObjectsTable = new JTable(null, classObjectsTableColumns2);
        searchBtn.addActionListener(this);
        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        toolBar.add(searchBtn);
        findBtn.addActionListener(this);
        limitEnabled.addActionListener(this);
        limitCount.addActionListener(this);
        prevBtn.addActionListener(inspector);
        toolBar.add(prevBtn);
        toolBar.add(findBtn);
        toolBar.add(applyBtn);
        toolBar.add(floatModeBtn);
        applyBtn.addActionListener(inspector);
        enableTransactionButtons(false);
        prevBtn.setEnabled(false);
        add(toolBar, BorderLayout.NORTH);
        add(splitter, BorderLayout.CENTER);
        inspector.setPreferredSize(new Dimension(700, 600));
        splitter.add(inspector, JSplitPane.RIGHT);
        leftPanel.setLayout(new BorderLayout());

        JScrollPane osp = new JScrollPane(classObjectsTable);
        leftPanel.add(osp);
        JPanel limitPanel = new JPanel(new FlowLayout());
        colLabel.setText("Кол-во: " + (krnObjects != null ? krnObjects.size() : 0));
        limitPanel.add(colLabel);

        leftPanel.add(limitPanel, BorderLayout.SOUTH);
        osp.setPreferredSize(new Dimension(140, 300));
        osp.setMinimumSize(new Dimension(20, 10));
        splitter.add(leftPanel, JSplitPane.LEFT);
        classObjectsTable.setModel(objsModel);
        ObjTableRowSorter sorter = new ObjTableRowSorter(objsModel);
        classObjectsTable.setRowSorter(sorter);
        // сортировка первого столбца
        sorter.toggleSortOrder(0);
        classObjectsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showObjectOperations(e);
            }

            public void mouseReleased(MouseEvent e) {
                showObjectOperations(e);
            }
        });

        classObjectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // must check, if same one, not load data from server!s
                if (!e.getValueIsAdjusting()) {
                    objSelectionChanged();
                }
            }
        });

        classObjectsTable.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {

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
                            int foundRow = findRowByText(text, -1);
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
                        Point locs = classObjectsTable.getParent().getLocationOnScreen();
                        Point loc = table.getLocationOnScreen();
                        searchWindow.setLocation(loc.x + rect.x + 1, locs.y - 21);
                    }
                    searchWindow.setVisible(true);

                    int foundRow = findRowByText(text, -1);

                    searchWindow.setFound(foundRow > -1);

                }
            }

            public void keyReleased(KeyEvent event) {
                if (event.isControlDown()) {
                    if (event.getKeyCode() == KeyEvent.VK_C) {
                        int row = classObjectsTable.getSelectedRow();
                        int column = classObjectsTable.getSelectedColumn();
                        final String copyText = classObjectsTable.getModel().getValueAt(row, column).toString();
                        StringSelection stringSelection = new StringSelection(copyText);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                    }
                }
            }
        });

        // Popup initializations
        objectOperations.add(objectCreateItem);
        objectOperations.add(objectDeleteItem);
        objectOperations.add(objectCloneItem);
        objectOperations.add(getXmlItem);
        objectOperations.addSeparator();
        objectOperations.add(findByIdItem);
        objectOperations.add(searchByAttrItem);
        objectOperations.add(searchNext);
        searchNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        objectCreateItem.addActionListener(this);
        objectDeleteItem.addActionListener(this);
        objectCloneItem.addActionListener(this);
        getXmlItem.addActionListener(this);
        findByIdItem.addActionListener(this);
        searchByAttrItem.addActionListener(this);
        searchNext.addActionListener(this);

        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.addSeparator();

        setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        splitter.setOpaque(isOpaque);
        limitEnabled.setOpaque(isOpaque);
        classObjectsTable.setOpaque(isOpaque);
        leftPanel.setOpaque(isOpaque);

        limitEnabled.setOpaque(isOpaque);
        osp.setOpaque(isOpaque);
        osp.getViewport().setOpaque(isOpaque);
        findBtn.setOpaque(isOpaque);
        // ObjectList initialization
        classObjectsTable.getTableHeader().setReorderingAllowed(false);
        reloadTransaction(krnObjects);
        if (classObjectsTable.getRowCount() > 0) {
            classObjectsTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private void reloadTransaction(List<KrnObject> krnObjects) {
        populateObjects(krnObjects);
    }

    private void populateObjects(List<KrnObject> krnObjects) {
        try {
            objsModel.clear();
            objsModel.add(krnObjects);
            colLabel.setText("Кол-во: " + krnObjects.size());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public KrnObject getSelectedObject() {
        int row = classObjectsTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return objsModel.get(classObjectsTable.convertRowIndexToModel(row));
    }

    public KrnObject[] getSelectedObjects() {
        return new KrnObject[] { getSelectedObject() };
    }

    public void setSelectedObject(KrnObject obj, String attrID) {
        String classId = String.valueOf(obj.classId);
        String id = String.valueOf(obj.id);
        String uid = obj.uid;
        if (objsModel.getColumnCount() > 2)
            for (int i = 0; i < objsModel.getRowCount(); i++) {
                if (objsModel.getValueAt(i, 0).toString().equals(classId) && objsModel.getValueAt(i, 1).toString().equals(id)
                        && objsModel.getValueAt(i, 2).toString().equals(uid)) {
                    classObjectsTable.getSelectionModel().setSelectionInterval(classObjectsTable.convertRowIndexToView(i),
                            classObjectsTable.convertRowIndexToView(i));
                    classObjectsTable.scrollRectToVisible(classObjectsTable.getCellRect(i, 0, false));
                    
                    int count = Funcs.checkInt(inspector.getObjectPropertyTable().getRowCount(), MAX_PROPERTIES_COUNT);

                    for (int j = 0; j < count; j++) {
                        if (inspector.getObjectPropertyTable().getModel().getValueAt(j, 0).toString().equals(attrID)) {
                            inspector.getObjectPropertyTable().getSelectionModel().setSelectionInterval(j, j);
                            BlobAttrEditorDelegate.setObjectID(Long.parseLong(objsModel.getValueAt(i, 1).toString()));
                            break;
                        }
                    }
                    break;
                }
            }
    }

    // Implementing ActionListener interface
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Kernel krn = Kernel.instance();

        try {
            if (src == objectCreateItem || src == createBtn) {
                KrnObject obj = krn.createObject(cls, transId);
                objsModel.add(obj);
                classObjectsTable.getSelectionModel().setSelectionInterval(objsModel.getRowCount() - 1,
                        objsModel.getRowCount() - 1);
            } else if (src == objectDeleteItem || src == deleteBtn) {
                int[] rows = classObjectsTable.getSelectedRows();
                int length = rows.length;
                Object[] ows = new Object[length];
                for (int i = 0; i < length; i++) {
                    KrnObject kobj = objsModel.get(classObjectsTable.convertRowIndexToModel(rows[i]));
                    ows[i] = kobj;
                }

                Container cont = getTopLevelAncestor();
                int res = -1;
                StringBuffer b = new StringBuffer();
                if (ows.length > 0) {
                    KrnObject ow = (KrnObject) ows[0];
                    b.append(ow);
                    for (int i = 1; i < length; i++) {
                        ow = (KrnObject) ows[i];
                        b.append(", " + ow);
                    }
                    res = MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                            MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите удалить объект '" + b + "'?");
                    if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_YES) {
                        for (int i = ows.length; i > 0; i--) {
                            ow = (KrnObject) ows[i - 1];
                            krn.deleteObject(ow, transId);
                            objsModel.remove(ow);
                        }
                    }
                }
            } else if (src == objectCloneItem || src == copyBtn) {
                int row = classObjectsTable.getSelectedRow();
                KrnObject ow = new KrnObject();
                ow.classId = Long.valueOf((String) classObjectsTable.getValueAt(row, 0));
                ow.id = Long.valueOf((String) classObjectsTable.getValueAt(row, 1));
                ow.uid = (String) classObjectsTable.getValueAt(row, 2);

                Container cont = getTopLevelAncestor();
                int res = -1;
                res = MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                        MessagesFactory.QUESTION_MESSAGE, "Вы действительно хотите создать копию объекта '" + ow + "'?");
                if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_YES) {
                    KrnObject[] objs = new KrnObject[1];
                    objs[0] = ow;
                    KrnObject[] cloneds = krn.cloneObject2(objs, 0, transId);
                    KrnObject clone = cloneds[0];
                    objsModel.add(clone);
                }
            } else if (src == objectRefreshItem || src == refreshBtn) {
                objSelectionChanged();
            } else if (src == getXmlItem || src == xmlBtn) {
                int row = classObjectsTable.getSelectedRow();
                KrnObject ow = new KrnObject();
                ow.classId = Long.valueOf((String) classObjectsTable.getValueAt(row, 0));
                String xml = krn.getXml(ow);
                getToolkit().getSystemClipboard().setContents(new StringSelection(xml), null);
                System.out.println(xml);
            } else if (src == reloadTransBtn) {
                reloadTransaction();
            } else if (src == nullerBtn) {
                setToNull();
            } else if (src == findBtn || src == findByIdItem) {
                if (sip == null) {
                    sip = new SearchInterfacePanel("Введите идентификатор объекта", false);
                }
                Container cont = getTopLevelAncestor();
                DesignerDialog dlg = new DesignerDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, "Поиск", sip);
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    searchObject(sip.getSearchText(), sip.getSearchMethod() == Constants.SEARCH_UID,cls);
                }
            } else if (src == searchBtn || src == searchByAttrItem) {
                Container cont = getTopLevelAncestor();
                AttributeListPanel ap = new AttributeListPanel(cls);
                findByAttr_.clear();
                DesignerDialog dlg = new DesignerDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, "Выбор атрибута",
                        ap);
                dlg.show();
                attr = ap.getSelectedAttribute();
                KrnAttribute[] krnAttrs = ap.getSelectedPathsAttributes();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK && attr != null) {
                    SearchInterfacePanel sip = new SearchInterfacePanel("Введите значение атрибута", attr);
                    DesignerDialog dlg1 = new DesignerDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, "Поиск", sip);
                    dlg1.show();
                    if (dlg1.getResult() == ButtonsFactory.BUTTON_OK) {
                        CursorToolkit.startWaitCursor(getTopLevelAncestor());
                        String value_ = sip.getSearchText();
                        long languageID = sip.getLanguageID();
                        int op = sip.getSearchMethod();
                        if (attr.typeClassId == Kernel.IC_STRING || attr.typeClassId == Kernel.IC_MEMO
                                || attr.typeClassId == Kernel.IC_MSTRING || attr.typeClassId == Kernel.IC_MMEMO) {
                            if (op == ComparisonOperations.SEARCH_START_WITH) {
                                value_ = value_.toUpperCase(Constants.OK) + "%";
                            } else if (op == ComparisonOperations.CO_CONTAINS) {
                                value_ = "%" + value_.toUpperCase(Constants.OK) + "%";
                            }
                        }
                        Object value = value_;
                        boolean parf = true;
                        KrnObject[] objs = null;
                        try {
                            if (attr.typeClassId == Kernel.IC_BOOL || attr.typeClassId == Kernel.IC_INTEGER
                                    || attr.typeClassId > 99) {
                                value = Long.valueOf(value_);
                            } else if (attr.typeClassId == Kernel.IC_FLOAT) {
                                value = Double.valueOf(value_);
                            } else if (attr.typeClassId == Kernel.IC_DATE) {
                                SimpleDateFormat df = new SimpleDateFormat(datePattern);
                                df.setLenient(false);
                                value = convertDate(Funcs.convertDate(df.parse(value_)));
                            }
                            objs = krn.getObjectsByAttribute(cls.id, attr.id, languageID, op, value, transId, krnAttrs);
                        } catch (Exception ef) {
                            ef.printStackTrace();
                            parf = false;
                        }

                        if (!parf) {
                            String txt = "Неверный формат!";
                            MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                                    MessagesFactory.INFORMATION_MESSAGE, txt);
                            CursorToolkit.stopWaitCursor(getTopLevelAncestor());
                        } else {
                            if (objs != null && objs.length > 0) {
                                int size = writeObjsToTable(objs);
                                String txt = "Найдено совпадений: " + size + ".";
                                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, MessagesFactory.INFORMATION_MESSAGE, txt);
                                classObjectsTable.requestFocusInWindow();
                            } else {
                                MessagesFactory.showMessageNotFound(cont);
                            }
                            CursorToolkit.stopWaitCursor(cont);
                        }
                    }
                }
            } else if (src == limitEnabled) {
                populateObjects();
                if (config.getConfig().isObjectBrowserLimitForClasses()) {
                    config.setObjectBrowserLimitForClasses(cls.id,
                            limitEnabled.isSelected() ? Integer.parseInt(limitCount.getText()) : 0);
                }
            } else if (src == limitCount) {
                if (limitCount.getText().equals("0")) {
                    config.setObjectBrowserLimitForClasses(cls.id, 0);
                    limitEnabled.setSelected(false);
                } else {
                    limitEnabled.setSelected(true);
                    if (config.getConfig().isObjectBrowserLimitForClasses()) {
                        config.setObjectBrowserLimitForClasses(cls.id, Integer.parseInt(limitCount.getText()));
                    }
                }
                populateObjects();
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
            Container cont = getTopLevelAncestor();
            MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
    }

    private KrnObject[] getObjsByDeep(KrnAttribute attr, Object value, KrnAttribute[] kAttrs, int op) {
        Object[] vals = new Object[] { value };
        KrnObject[] objs = deepFeel(kAttrs, vals, kAttrs.length - 1, op);

        return objs;
    }

    private KrnObject[] deepFeel(KrnAttribute[] attrs, Object[] vals, int counter, int op) {
        HashMap<KrnObject, Long> map = new HashMap<KrnObject, Long>();
        for (int i = 0; i < vals.length; i++) {
            try {
                KrnObject[] kos = Kernel.instance().getObjectsByAttribute(attrs[counter].classId, attrs[counter].id,
                        attr.isMultilingual ? langId : 0, op, vals[i], transId);
                for (KrnObject ko : kos) {
                    map.put(ko, ko.id);
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        if (counter <= 0) {
            return map.keySet().toArray(new KrnObject[map.size()]);
        }
        return deepFeel(attrs, map.values().toArray(), --counter, op);
    }

    private int writeObjsToTable(KrnObject[] objs) {
        objsModel.clear();
        boolean thisClassOnly = checkOnly.isSelected();
        int size = 0;
        for (KrnObject obj : objs) {
            if (!thisClassOnly || obj.classId == cls.id) {
                objsModel.add(obj);
                size++;
            }
        }
        colLabel.setText("Кол-во: " + size);
        return size;
    }

    public int findRowByText(String text, int col) {
        if (col < 0)
            col = classObjectsTable.getSelectedColumn();
        if (col >= classObjectsTable.getColumnCount())
            return -1;
        int res = -1;
        for (int i = 0; i < objsModel.getRowCount(); i++) {
            String title = (String) objsModel.getValueAt(i, col);

            if (title != null && title.toLowerCase(Constants.OK).startsWith(text.toLowerCase(Constants.OK))) {
                classObjectsTable.getSelectionModel().setSelectionInterval(classObjectsTable.convertRowIndexToView(i),
                        classObjectsTable.convertRowIndexToView(i));
                Object parent = classObjectsTable.getParent().getParent();
                JViewport vp = ((JScrollPane) parent).getViewport();
                Point p = vp.getViewPosition();
                int lastRow = vp.getHeight() / classObjectsTable.getRowHeight() - 1;
                p.y = Math.max(0, (i - lastRow) * classObjectsTable.getRowHeight());
                vp.setViewPosition(p);
                res = i;
                classObjectsTable.scrollRectToVisible(classObjectsTable.getCellRect(classObjectsTable.convertRowIndexToView(i),
                        0, true));
                break;
            }
        }
        return res;
    }

    private void searchObject(String id, boolean isUid,KrnClass cls) throws KrnException {
        if (id == null) {
            Container cont = getTopLevelAncestor();
            MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                    MessagesFactory.INFORMATION_MESSAGE, "Объект \"" + id + "\" " + "не найден...");
            return;
        }
        int row = -1;
        if (isUid) {
            for (int i = 0; i < objsModel.getRowCount(); i++)
                if (id.equals(objsModel.get(i).uid)) {
                    row = i;
                    break;
                }
            if (isAdvancedSearch) {
	            if (row == -1) {
	            	KrnObject obj = Kernel.instance().getObjectByUid(id, transId);
	            	if (obj != null){
	            		row = objsModel.add(obj);
	                	if(obj.classId!= cls.id){
	                    	KrnClass cls_ = Kernel.instance().getClass(obj.classId);
	                    	Container cont = getTopLevelAncestor();
	                    	MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
	                            MessagesFactory.INFORMATION_MESSAGE, "Объект найден, но он принадлежит классу:'" + cls_.name+"'!");
	                	}
	            	}
	            }
            }
        } else {
            long objId = -1;
            try {
            	objId = Long.parseLong(id);
                for (int i = 0; i < objsModel.getRowCount(); i++)
                    if (objsModel.get(i) != null && objId == objsModel.get(i).id) {
                        row = i;
                        break;
                    }
                if (isAdvancedSearch) {
	                if (row == -1) {
	                	KrnObject obj = Kernel.instance().getObjectById(objId, transId);
	                	if (obj != null)
	                		row = objsModel.add(obj);
	                }
                }
            } catch (NumberFormatException nfe) {
                Container cont = getTopLevelAncestor();
                MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                        MessagesFactory.ERROR_MESSAGE, "Неверный формат значения id = \"" + id);
                return;
            }
        }
        if (row != -1) {
            row = classObjectsTable.convertRowIndexToView(row);
            classObjectsTable.getSelectionModel().setSelectionInterval(row, row);
            classObjectsTable.scrollRectToVisible(new Rectangle(classObjectsTable.getCellRect(row, 0, true)));
        } else {
            Container cont = getTopLevelAncestor();
            MessagesFactory.showMessageDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont,
                    MessagesFactory.INFORMATION_MESSAGE, "Объект \"" + id + "\" " + "не найден...");
        }
    }

    private void showObjectOperations(MouseEvent e) {
        if (e.isPopupTrigger()) {
            objectOperations.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void objSelectionChanged() {
        int row = classObjectsTable.getSelectedRow();
    	if(row != -1) {
    		KrnObject kobj = objsModel.get(classObjectsTable.convertRowIndexToModel(row));
   			inspector.setObject(new KrnObjectNodeItem(kobj, this), false);
    	}
    }

    private void jbInit() throws Exception {
        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        createBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        copyBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        xmlBtn.addActionListener(this);
        findBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        limitEnabled.addActionListener(this);
        limitCount.addActionListener(this);
        toolBar.add(createBtn);
        toolBar.add(deleteBtn);
        toolBar.add(copyBtn);
        toolBar.add(refreshBtn);
        toolBar.add(xmlBtn);
        prevBtn.addActionListener(inspector);
        applyBtn.addActionListener(inspector);
        rollbackBtn.addActionListener(inspector);
        toolBar.add(prevBtn);
        toolBar.add(applyBtn);
        toolBar.add(rollbackBtn);
        toolBar.add(findBtn);
        toolBar.add(searchBtn);
        enableTransactionButtons(false);
        prevBtn.setEnabled(false);
        add(toolBar, BorderLayout.NORTH);
        add(splitter, BorderLayout.CENTER);

        inspector.setPreferredSize(new Dimension(900, 600));
        splitter.add(inspector, JSplitPane.RIGHT);
        inspector.setObject(new KrnObjectNodeItem(null, this), false);
        limitCount.setText(config.getConfig().getObjectBrowserLimit() + "");
        limitEnabled.setSelected(config.getConfig().isObjectBrowserLimit());
        if (config.getConfig().isObjectBrowserLimitForClasses()) {
            Object lim = config.getConfig().getObjectBrowserLimitForClasses().get(cls.id);
            if (lim != null) {
                String l = lim + "";
                limitCount.setText(l);
                limitEnabled.setSelected(!l.equals("0"));
            }
        }

        JPanel limitPanel = new JPanel(new FlowLayout());
        limitPanel.add(colLabel);
        limitPanel.add(limitEnabled);
        limitPanel.add(limitCount);

        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(limitPanel, BorderLayout.SOUTH);
        JScrollPane osp = new JScrollPane(classObjectsTable);
        leftPanel.add(osp);
        osp.setPreferredSize(new Dimension(255, 300));
        osp.setMinimumSize(new Dimension(20, 10));
        splitter.add(leftPanel, JSplitPane.LEFT);
        findByAttr_ = new TreeSet<KrnObjectItem>(new Comparator<KrnObjectItem>() {
            public int compare(KrnObjectItem o1, KrnObjectItem o2) {
                return (o1.obj.id < o2.obj.id ? -1 : o1.obj.id == o2.obj.id ? 0 : 1);
            }
        });

        classObjectsTable.setModel(objsModel);
        ObjTableRowSorter sorter = new ObjTableRowSorter(objsModel);
        classObjectsTable.setRowSorter(sorter);
        // сортировка второго столбца
        sorter.toggleSortOrder(1);
        classObjectsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showObjectOperations(e);
            }

            public void mouseReleased(MouseEvent e) {
                showObjectOperations(e);
            }
        });

        classObjectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                // must check, if same one, not load data from server!s
                if (!e.getValueIsAdjusting()) {
                    objSelectionChanged();
                }
            }
        });

        classObjectsTable.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {

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
                            int foundRow = findRowByText(text, -1);
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
                        Point locs = classObjectsTable.getParent().getLocationOnScreen();
                        Point loc = table.getLocationOnScreen();
                        searchWindow.setLocation(loc.x + rect.x + 1, locs.y - 21);
                    }
                    searchWindow.setVisible(true);

                    int foundRow = findRowByText(text, -1);

                    searchWindow.setFound(foundRow > -1);

                }
            }

            public void keyReleased(KeyEvent event) {
                if (event.isControlDown()) {
                    if (event.getKeyCode() == KeyEvent.VK_C) {
                        int row = classObjectsTable.getSelectedRow();
                        int column = classObjectsTable.getSelectedColumn();
                        final String copyText = classObjectsTable.getModel().getValueAt(row, column).toString();
                        StringSelection stringSelection = new StringSelection(copyText);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                    }
                }
            }
        });

        // Popup initializations
        objectOperations.add(objectCreateItem);
        objectOperations.add(objectDeleteItem);
        objectOperations.add(objectCloneItem);
        objectOperations.add(objectRefreshItem);
        objectOperations.add(getXmlItem);
        objectOperations.addSeparator();
        objectOperations.add(findByIdItem);
        objectOperations.add(searchByAttrItem);
        objectOperations.add(searchNext);
        searchNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

        objectCreateItem.addActionListener(this);
        objectDeleteItem.addActionListener(this);
        objectCloneItem.addActionListener(this);
        objectRefreshItem.addActionListener(this);
        getXmlItem.addActionListener(this);
        findByIdItem.addActionListener(this);
        searchByAttrItem.addActionListener(this);
        searchNext.addActionListener(this);

        transField.setPreferredSize(new Dimension(50, 20));
        transField.setMaximumSize(new Dimension(50, 20));
        transField.setMinimumSize(new Dimension(50, 20));
        transField.setFont(Utils.getDefaultFont());
        transField.setForeground(Utils.getDarkShadowSysColor());
        transField.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        transField.setText(String.valueOf(transId));
        transField.setHorizontalAlignment(SwingConstants.CENTER);
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.add(Utils.createLabel("ID транзакции "));
        toolBar.add(transField);
        toolBar.addSeparator(new Dimension(10, 10));
        reloadTransBtn.addActionListener(this);
        toolBar.add(reloadTransBtn);
        toolBar.addSeparator(new Dimension(10, 10));
        transCombo.setPrototypeDisplayValue("XXXXXXXXXXX");
        Utils.setAllSize(transCombo, new Dimension(80, 25));
        toolBar.add(transCombo);
        transCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (transCombo.getSelectedIndex()) {
                case 1:
                    transId = -1L;
                    populateObjects();
                    break;
                case 2:
                    transId = 0L;
                    populateObjects();
                    break;
                case 3:
                    transId = -2L;
                    populateObjects();
                    transId = -1L;
                    break;
                }
            }
        });
        nullerBtn.addActionListener(this);
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.add(checkOnly);
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.addSeparator();
        toolBar.add(nullerBtn);

        setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        splitter.setOpaque(isOpaque);
        limitEnabled.setOpaque(isOpaque);
        classObjectsTable.setOpaque(isOpaque);
        checkOnly.setOpaque(isOpaque);
        leftPanel.setOpaque(isOpaque);
        limitPanel.setOpaque(isOpaque);
        limitEnabled.setOpaque(isOpaque);
        osp.setOpaque(isOpaque);
        osp.getViewport().setOpaque(isOpaque);
        createBtn.setOpaque(isOpaque);
        deleteBtn.setOpaque(isOpaque);
        copyBtn.setOpaque(isOpaque);
        xmlBtn.setOpaque(isOpaque);
        prevBtn.setOpaque(isOpaque);
        applyBtn.setOpaque(isOpaque);
        rollbackBtn.setOpaque(isOpaque);
        reloadTransBtn.setOpaque(isOpaque);
        nullerBtn.setOpaque(isOpaque);
        findBtn.setOpaque(isOpaque);
        searchBtn.setOpaque(isOpaque);
        classObjectsTable.getTableHeader().setReorderingAllowed(false);
        reloadTransaction();
        if (classObjectsTable.getRowCount() > 0) {
            classObjectsTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private void setToNull() {
        classObjectsTable.getSelectionModel().clearSelection();
        inspector.setObjNull();
        inspector.setLabel("NULL");
    }

    private void reloadTransaction() {
        try {
            transField.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (transField.isEditValid()) {
            transId = Long.parseLong(transField.getText());
            populateObjects();
        }
    }

    public KrnClass getKrnClass() {
        return this.cls;
    }

    public void enableTransactionButtons(boolean isEnabled) {
        applyBtn.setEnabled(isEnabled);
        rollbackBtn.setEnabled(isEnabled);
    }

    public void prevBtnManage(boolean isEnabled) {
        prevBtn.setEnabled(isEnabled);
    }

    public java.sql.Date convertDate(com.cifs.or2.kernel.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.day);
        cal.set(Calendar.MONTH, date.month);
        cal.set(Calendar.YEAR, date.year);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public Timestamp convertTime(Time time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, time.msec);
        cal.set(Calendar.SECOND, time.sec);
        cal.set(Calendar.MINUTE, time.min);
        cal.set(Calendar.HOUR_OF_DAY, time.hour);
        cal.set(Calendar.DAY_OF_MONTH, time.day);
        cal.set(Calendar.MONTH, time.month);
        cal.set(Calendar.YEAR, time.year);
        return new Timestamp(cal.getTimeInMillis());
    }

    private void populateObjects() {
        try {
            final Kernel krn = Kernel.instance();
            int limit = 0;
            if (limitEnabled.isSelected()) {
                if (limitCount.getText().length() == 0) {
                    limitCount.setText("0");
                }
                limit = Integer.parseInt(limitCount.getText());
            }
            int[] lim = { limit };

            KrnObject[] objs = isSearchMode ? new KrnObject[0] : krn.getClassObjects(cls, new long[0], lim, transId);
            List<KrnObject> objList = new ArrayList<KrnObject>(objs.length);

            boolean thisClassOnly = checkOnly.isSelected();
            for (KrnObject obj : objs) {
                if (!thisClassOnly || obj.classId == cls.id) {
                    objList.add(obj);
                }
            }

            colLabel.setText("Кол-во: " + objList.size());
            objsModel.clear();
            objsModel.add(objList);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private int columnCount;

    private void setColumnCount(int numberofcolumns) {
        columnCount = numberofcolumns;
    }

    private int getColumnNumberCount() {
        return columnCount;
    }

    private class KrnObjectTableModel extends AbstractTableModel {

        private List<KrnObject> objs;

        public KrnObjectTableModel() {
            objs = new ArrayList<KrnObject>();
        }

        public void clear() {
            if (objs.size() > 0) {
                int lastRow = objs.size() - 1;
                objs.clear();
                fireTableRowsDeleted(0, lastRow);
            }
        }

        public int add(KrnObject obj) {
            int firstRow = this.objs.size();
            objs.add(obj);
            fireTableRowsInserted(firstRow, firstRow);
            return objs.size() - 1;
        }

        public void add(List<KrnObject> objs) {
            if (objs.size() > 0) {
                int firstRow = this.objs.size();
                this.objs.addAll(objs);
                fireTableRowsInserted(firstRow, this.objs.size() - 1);
            }
        }

        public void add(KrnObject[] objs) {
            int firstRow = this.objs.size();
            for (KrnObject obj : objs)
                this.objs.add(obj);
            fireTableRowsInserted(firstRow, this.objs.size() - 1);
        }

        public void remove(int index) {
            objs.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public void remove(KrnObject obj) {
            int index = objs.indexOf(obj);
            if (index >= 0) {
                objs.remove(obj);
                fireTableRowsDeleted(index, index);
            }
        }

        public KrnObject get(int index) {
            return objs.get(index);
        }

        @Override
        public int getRowCount() {
            return objs.size();
        }

        @Override
        public int getColumnCount() {
            return getColumnNumberCount();
        }

        @Override
        public String getColumnName(int column) {
            return getColumnCount() == 3 ? classObjectsTableColumns[column] : classObjectsTableColumns2[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KrnObject obj = objs.get(rowIndex);
            if (obj != null) {
                switch (columnIndex) {
                case 0:
                    return getColumnCount() == 3 ? obj.classId : obj.uid;
                case 1:
                    return obj.id;
                case 2:
                    return obj.uid;
                }
            }
            return null;
        }

    }

    private class ObjTableRowSorter extends TableRowSorter {
    	private int columnCount = 0;
    	
        public ObjTableRowSorter(TableModel model) {
            super(model);
            columnCount = model.getColumnCount();
        }

        @Override
        public Comparator<?> getComparator(final int column) {
            if (columnCount == 3 && (column == 0 || column == 1 )) {
                return new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return (int) (Long.parseLong(s1) - Long.parseLong(s2));
                    }
                };
            } else if (column == 2 || columnCount == 1) {
                return new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return (int) (Long.parseLong(s1.replaceAll("\\.*", "")) - Long.parseLong(s2.replaceAll("\\.*", "")));
                    }
                };
            } else {
                return super.getComparator(column);
            }
        }

    }
}