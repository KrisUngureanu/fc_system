package kz.tamur.guidesigner;

import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.setAllSize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.CreateElementPanel;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.DualTreePanel;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.ServiceControlNode;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;

/**
 * User: vital
 * Date: 11.12.2004
 * Time: 11:58:47
 */
public class ReportEditor extends JPanel implements TreeSelectionListener,
        ActionListener, ListSelectionListener {

    public static final int BASE_EDITOR = 0;
    public static final int REPORT_EDITOR = 1;
    public static final int FILTER_EDITOR = 2;

    public static final int INTERFACE_AREA = 0;
    public static final int PROCESS_AREA = 1;


    private DesignerTree tree;

    private ClassBrowser classBrowser;
    private String defaultClass = "";
    private String lastPath;
    private Object value;
    private long oldFilterId;

    private ReportTreeTableModel tableModel = new ReportTreeTableModel();

    private JTable table;
    private TreeTableCellRenderer reportTree = new TreeTableCellRenderer();

    private DefaultListModel model = new DefaultListModel();
    private JList selectedList = new JList(model);
    private JToggleButton rootBtn =
            ButtonsFactory.createToggleButton(false, "UpLevel", "");
    private JButton addBtn =
            ButtonsFactory.createToolButton("addSingle", "", "", true);
    private JButton removeBtn =
            ButtonsFactory.createToolButton("removeSingle", "", "", true);
    private JButton removeAllBtn =
            ButtonsFactory.createToolButton("removeAll", "", "", true);

    private KrnObject[] oldValue;

    private int editorType;
    private int area;
    private TreeNode filterRoot;

    private JPopupMenu nodeOperations_ = new JPopupMenu();
    private JMenuItem nodeRenameItem_;
    private JMenuItem nodeCreateItem_;
    private JMenuItem nodeInsertItem_;
    private JMenuItem nodeAddItem_;
    private JMenuItem nodeDeleteItem_;
    private JMenuItem nodeCutItem_;
    private JMenuItem nodeInsertSelectedItem_;
    private JMenuItem nodeAddSelectedItem_;
    private JMenuItem nodeJumptoReportItem_;
    private DualTreePanel selector;
    private List cutNodes;

    private long langId;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ReportEditor(int editorType, int area, long langId) {//UserNode selectedUser) {
        this.editorType = editorType;
        this.area = area;
        this.langId = langId;
        setLayout(new GridBagLayout());
        reportTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        reportTree.setRootVisible(false);
        reportTree.addTreeExpansionListener(new TreeTableExpansionListener());

        table = new JTable(tableModel);
        reportTree.setRowHeight(table.getRowHeight());
        init();

        TableColumnModel cm = table.getColumnModel();
        TableColumn tc = cm.getColumn(0);
        TableCellEditor treeCellEditor = new TreeTableCellEditor();

        tc.setCellEditor(treeCellEditor);
        tc.setCellRenderer((TreeTableCellRenderer) reportTree);

        table.setDefaultEditor(TreeModel.class, treeCellEditor);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                delegate(e);
            }

            public void mouseReleased(MouseEvent e) {
                delegate(e);
            }

            public void delegate(MouseEvent e) {
                TableModel tm = table.getModel();
                if (e.isPopupTrigger()) {
                    for (int counter = tm.getColumnCount() - 1; counter >= 0; counter--) {
                        if (tm.getColumnClass(counter) == TreeModel.class) {
                            MouseEvent newME = new MouseEvent(reportTree, e.getID(), e
                                .getWhen(), e.getModifiers(), e.getX()
                                - table.getCellRect(0, counter,
                                true).x, e.getY(), e.getClickCount(),
                                true);
                            reportTree.dispatchEvent(newME);
                            break;
                        }
                    }
                }
            }
        });

        tc = cm.getColumn(1);
        tc.setCellEditor(new DataCellEditor());
        tc = cm.getColumn(2);
        tc.setCellEditor(new DataCellEditor());
        tc = cm.getColumn(3);
        tc.setCellEditor(new DataCellEditor());
        tc = cm.getColumn(4);
        tc.setCellEditor(new DataCellEditor());
        tc = cm.getColumn(5);
        tc.setCellEditor(new BooleanCellEditor());
        tc.setCellRenderer(new BooleanCellRenderer());
        
        JTableHeader th = table.getTableHeader();
        th.setFont(Utils.getDefaultFont());
        th.setForeground(Utils.getDarkShadowSysColor());
        table.setFont(Utils.getDefaultFont());
        table.setForeground(Utils.getDarkShadowSysColor());

        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
        reportTree.setSelectionModel(selectionWrapper);
        reportTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        table.setSelectionModel(selectionWrapper.getListSelectionModel());

        reportTree.addMouseListener(new TreeMouseListener());
        NodeOperationsActionListener l = new NodeOperationsActionListener();

        //if (!isReadOnly) {

        String menuName = "Переименовать узел";
        nodeRenameItem_ = createMenuItem(menuName, "Rename");
        nodeRenameItem_.addActionListener(l);
        nodeOperations_.add(nodeRenameItem_);

        menuName = "Создать узел";
        nodeCreateItem_ = createMenuItem(menuName, "Create");
        nodeCreateItem_.addActionListener(l);
        nodeOperations_.add(nodeCreateItem_);

        menuName = "Вставить узел";
        nodeInsertItem_ = createMenuItem(menuName, "Create");
        nodeInsertItem_.addActionListener(l);
        nodeOperations_.add(nodeInsertItem_);

        menuName = "Добавить отчеты";
        nodeAddItem_ = createMenuItem(menuName, "Create");
        nodeAddItem_.addActionListener(l);
        nodeOperations_.add(nodeAddItem_);

        menuName = "Удалить узел";
        nodeDeleteItem_ = createMenuItem(menuName, "Delete");
        nodeDeleteItem_.addActionListener(l);
        nodeOperations_.add(nodeDeleteItem_);

        menuName = "Вырезать";
        nodeCutItem_ = createMenuItem(menuName);
        nodeCutItem_.addActionListener(l);
        nodeOperations_.add(nodeCutItem_);
        cutNodes = new ArrayList();
        
        menuName = "Перейти в отчёт";
        nodeJumptoReportItem_ = createMenuItem(menuName);
        nodeJumptoReportItem_.addActionListener(l);
        nodeOperations_.add(nodeJumptoReportItem_);

        menuName = "Вставить вырезанные";
        nodeInsertSelectedItem_ = createMenuItem(menuName);
        nodeInsertSelectedItem_.addActionListener(l);
        nodeOperations_.add(nodeInsertSelectedItem_);

        menuName = "Добавить вырезанные";
        nodeAddSelectedItem_ = createMenuItem(menuName);
        nodeAddSelectedItem_.addActionListener(l);
        nodeOperations_.add(nodeAddSelectedItem_);
    }

    private void init() {
        
        setPreferredSize(new Dimension(600, 400));
        switch(editorType) {
            case BASE_EDITOR:
                tree = kz.tamur.comps.Utils.getBaseTree();
                break;
            case REPORT_EDITOR:
                tree = kz.tamur.comps.Utils.getReportTree(DesignerFrame.instance().getInterfaceLang());
                break;
            case FILTER_EDITOR:
                try {
                    Kernel krn = Kernel.instance();
                    KrnClass cls = krn.getClassByName("FilterRoot");
                    KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
                    long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
                    long[] ids = {filterRoot.id};
                    StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId,
                            false, 0);
                    String title = "Не определён";
                    if (strs.length > 0) {
                        title = strs[0].value;
                    }
                    this.filterRoot = new FilterNode(filterRoot, title, langId, 0);
                } catch (KrnException e) {
                    e.printStackTrace();
                }

                if (area == INTERFACE_AREA) {
                    tree = kz.tamur.comps.Utils.getFiltersTree();
                } else {
                    tree = kz.tamur.comps.Utils.getFiltersTree();
                    validate();
                    repaint();
                    rootBtn.setEnabled(false);
                }
                tree.addTreeSelectionListener(new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        Container cnt = getTopLevelAncestor();
                        if (cnt instanceof DesignerDialog) {
                            FilterNode fn = (FilterNode)e.getPath().getLastPathComponent();
                            ((DesignerDialog)cnt).setOkEnabled(fn.isLeaf());
                        }
                    }
                });
                break;
        }

        tree.addTreeSelectionListener(this);
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        selector = new DualTreePanel(tree);
        if (selector.getTreePr()!=null) {
            selector.getTreePr().addTreeSelectionListener(this);
        }
        selector.setPreferredSize(new Dimension(500, 100));
        selectedList.setFont(Utils.getDefaultFont());
        selectedList.setBackground(Utils.getLightSysColor());
        selectedList.setForeground(Utils.getDarkShadowSysColor());
        selectedList.addListSelectionListener(this);
        Dimension sz = new Dimension(30, 30);
        setAllSize(rootBtn,sz);
        setAllSize(addBtn,sz);
        setAllSize(removeBtn,sz);
        setAllSize(removeAllBtn,sz);
        addBtn.setEnabled(false);
        removeBtn.setEnabled(false);
        rootBtn.addActionListener(this);
        addBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        removeAllBtn.addActionListener(this);
        JLabel lab = Utils.createLabel("Структуры баз");
        if (editorType == REPORT_EDITOR) {
            lab.setText("   Отчёты");
        } else if (editorType == FILTER_EDITOR) {
            lab.setText("   Фильтры");
        }
        if (editorType == REPORT_EDITOR && area == INTERFACE_AREA) {
            add(lab, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
                    5, 0, 0, 0), 0, 0));
            add(selector, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
        } else {
            lab = Utils.createLabel("Выбранные структуры баз");
            if (editorType == REPORT_EDITOR) {
                lab.setText("Выбранные отчёты");
            } else if (editorType == FILTER_EDITOR) {
                lab.setText("Выбранные фильтры");
            }
            add(lab, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 0, 0 ,0), 0, 0));
            JScrollPane sp = null;
            if (editorType == REPORT_EDITOR) {
                sp = new JScrollPane(table);
            } else {
                sp = new JScrollPane(selectedList);
            }
            sp.setOpaque(isOpaque);
            sp.getViewport().setOpaque(isOpaque);
            sp.setPreferredSize(new Dimension(500, 100));
            add(sp, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0 ,0), 0, 0));
        }
        setOpaque(isOpaque);
        table.setOpaque(isOpaque);
    }

    public void valueChanged(TreeSelectionEvent e) {
        DesignerTreeNode node = (DesignerTreeNode) e.getPath().getLastPathComponent();
        if (node instanceof ServiceControlNode) {
            boolean enabled = false;
            if (node != null && selector.getTreePr() != null) {
                switch (selector.getTypeTree()) {
                case 0:
                    enabled = ((ServiceControlNode) node).isService();
                    break;
                case 1:
                    enabled = ((ServiceControlNode) node).isInterface();
                    break;
                case 2:
                    enabled = ((ServiceControlNode) node).isFilter();
                    break;
                case 3:
                    enabled = ((ServiceControlNode) node).isReport();
                    break;
                case 4:
                    enabled = true;
                    break;
                default:
                    break;
                }
            }
            addBtn.setEnabled(enabled);
        } else {
            addBtn.setEnabled(editorType == REPORT_EDITOR ? !tableModel.exists(node) : !isListExists(node));
        }
        removeBtn.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addBtn) {
            DesignerTreeNode[] nodes = null;
            nodes = selector.getSelectedTree().getSelectedNodes();
            for (int i = 0; i < nodes.length; i++) {
                DesignerTreeNode node = nodes[i];
                ReportRecord rec = new ReportRecord(node.getKrnObj().id,
                         "", 0,"", "", false);
                tableModel.addReport(rec);
            }
        }
        if (src == removeBtn) {
            tableModel.deleteRecord(
                    tableModel.getReportAt(table.getSelectedRow()));
        }
        if (src == removeAllBtn) {
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                tableModel.deleteRecord(
                        tableModel.getReportAt(i));
            }
        }
        selector.getSelectedTree().repaint();
    }

    public void valueChanged(ListSelectionEvent e) {
        Object o = null;
        if (editorType != REPORT_EDITOR) {
            o = selectedList.getSelectedValue();
        }
        removeBtn.setEnabled(o != null);
    }

    public DesignerTreeNode[] getSelectedNodeValues() {
        DesignerTreeNode[] res = null;
        int size = model.getSize();
        if (size > 0) {
            res = new DesignerTreeNode[size];
            for (int i = 0; i < size; i++) {
                res[i] = (DesignerTreeNode)model.getElementAt(i);
            }
        }
        return res;
    }

    public KrnObject[] getSelectedValues() {
        KrnObject[] res = null;
        int size = model.getSize();
        if (size > 0) {
            res = new KrnObject[size];
            for (int i = 0; i < size; i++) {
                res[i] = ((DesignerTreeNode)model.getElementAt(i)).getKrnObj();
            }
        }
        return res;
    }

    public KrnObject[] getOldValue() {
        return oldValue;
    }

    public void setOldValue(KrnObject[] oldValue) {
        this.oldValue = oldValue;
        if (oldValue != null && oldValue.length > 0) {
            for (int i = 0; i < oldValue.length; i++) {
                KrnObject krnObject = oldValue[i];
                DesignerTreeNode n = selector.getSelectedTree().find(krnObject);
                if (n != null) {
                    model.addElement(n);
                }
            }
            selector.getSelectedTree().repaint();
        }
    }

    class ReportTreeTableModel extends AbstractTableModel {

        public final String[] COL_NAMES = {"Отчёт", "Данные", "Фильтр", "Функция", "Видимость", "На сервере"};

        private ArrayList<ReportRecord> reports = new ArrayList<ReportRecord>();

        public boolean exists(DesignerTreeNode node) {
            for (int i = 0; i < reports.size(); i++) {
                ReportRecord reportRecord = (ReportRecord) reports.get(i);
                if (node.getKrnObj().id == reportRecord.getObjId()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) return true;
            ReportRecord rec = getReportAt(rowIndex);
            return !rec.isFolder();
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return reportTree.getRowCount();
        }

        public void addReport(ReportRecord rec) {
            reports.add(rec);
            fireTableDataChanged();
        }

        public DesignerTreeNode deleteRecord(ReportRecord rec) {
            try {
                reports.remove(rec);
                DesignerTreeNode node = selector.getSelectedTree().find(
                        Utils.getObjectById(rec.getObjId(), 0));
                int idx = table.getSelectedRow();
                fireTableRowsDeleted(idx, idx);
                return node;
            } catch (KrnException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ReportRecord getReportAt(int rowIndex) {
            if (reportTree.getPathForRow(rowIndex) != null) {
                Node node = (Node) reportTree.getPathForRow(
                                    rowIndex).getLastPathComponent();

                return node.report;
            }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                    return reportTree.getModel();
            }
            if (reportTree.getPathForRow(rowIndex) != null) {
                Node node = (Node) reportTree.getPathForRow(
                                    rowIndex).getLastPathComponent();

                ReportRecord rep = node.report;
                switch(columnIndex) {
                    case 0:
                        return rep.getTitle(langId);
                    case 1:
                        return rep.getPath();
                    case 2:
                        return new Long(rep.getFilterId()) + ":" + kz.tamur.comps.Utils.getFilterNameById(rep.getFilterId());
                    case 3:
                        return rep.getFunc();
                    case 4:
                        return rep.getVisibilityFunc();
                    case 5:
                        return rep.isFormOnServer();
                    default:
                        return "";
                }
            }
            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ReportRecord rec = getReportAt(rowIndex);
            if (rec != null) {
                switch(columnIndex) {
                    case 1:
                        rec.setPath("" + aValue);
                        break;
                    case 2:
                        StringTokenizer st = new StringTokenizer(aValue.toString(), ":");
                        if (st.hasMoreTokens()) {
                            rec.setFilterId(new Integer(st.nextToken()).intValue());
                        } else {
                            rec.setFilterId(0);
                        }
                        break;
                    case 3:
                        rec.setFunc("" + aValue);
                        break;
                    case 4:
                        rec.setVisibilityFunc("" + aValue);
                        break;
                    case 5:
                        rec.setFormOnServer((Boolean)aValue);
                        break;
                }
            }
        }

        public Class getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                        return TreeModel.class;
                } else {
                        return super.getColumnClass(columnIndex);
                }
        }

    }

    public void setOldReportValue(ReportRecord root) {
        Node n = new Node(root);
        DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
        DefaultMutableTreeNode r = (DefaultMutableTreeNode) m.getRoot();
        m.insertNodeInto(n, r, 0);
        reportTree.expandPath(new TreePath(m.getRoot()));
        reportTree.expandPath(new TreePath(n.getPath()));
        tableModel.fireTableRowsInserted(0, 0);
        reportTree.updateImage();
        reportTree.repaint();
    }

    public void setOldReportValue(ReportRecord[] oldVals) {
        for (int i = 0; i < oldVals.length; i++) {
            ReportRecord oldVal = oldVals[i];
            tableModel.addReport(oldVal);
        }
    }

    public void setOldFiltersValue(FilterRecord[] oldVals) {
        for (int i = 0; i < oldVals.length; i++) {
            FilterRecord oldVal = oldVals[i];
            model.addElement(oldVal);
        }
/*
        FilterRecord fr = oldVals[oldVals.length - 1];
        if (fr != null) {
            ((FiltersTree)tree).setSelectedNode(fr.getObjId());
        }
*/
    }


    public ReportRecord[] getSelectedReportValues() {
        ReportRecord[] res = new ReportRecord[table.getRowCount()];
        for (int i = 0; i < table.getRowCount(); i++) {
            ReportRecord re = tableModel.getReportAt(i);
            res[i] = re;
        }
        return res;
    }

    public ReportRecord getSelectedReportValue() {
        DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
        DefaultMutableTreeNode r = (DefaultMutableTreeNode) m.getRoot();
        if (r.getChildCount() > 0)
            return ((Node)r.getChildAt(0)).report;

        return null;
    }

    public DesignerTreeNode[] getSelectedReports() {
        return selector.getSelectedTree().getSelectedNodes();
    }

    public FilterRecord[] getSelectedFilterValues() {
        FilterRecord[] res = new FilterRecord[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            FilterRecord re = (FilterRecord)model.getElementAt(i);
            res[i] = re;
        }
        return res;
    }

    public class DataCellEditor extends DefaultCellEditor {
        private JTextField field = Utils.createDesignerTextField();

        public DataCellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }

        public boolean stopCellEditing() {
            value = field.getText();
            return super.stopCellEditing();
        }

        public boolean stopCellEditingExpression() {
            return super.stopCellEditing();
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            ReportEditor.this.value = value;
            field.setLayout(new BorderLayout());
            field.setText("" + value);
            final JButton btn = ButtonsFactory.createEditorButton(
                    ButtonsFactory.DEFAULT_EDITOR);
            if (column == 1) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            ClassBrowser cb = getClassBrowser(ReportEditor.this.value.toString());
                            DesignerDialog dlg =
                                    new DesignerDialog(Or3Frame.instance(),
                                            "Выберите путь", cb);
                            dlg.show();
                            int res = dlg.getResult();
                            if (res != ButtonsFactory.BUTTON_NOACTION
                                    && res == ButtonsFactory.BUTTON_OK) {
                                String path = cb.getSelectedPath();
                                field.setText(path);
                                ReportEditor.this.value = path;
                            }
                            stopCellEditing();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else if (column == 2) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            MultiEditor me = new MultiEditor(MultiEditor.FILTER_EDITOR,
                                    MultiEditor.INTERFACE_AREA);
                            if (ReportEditor.this.value != null && !"0:".equals(ReportEditor.this.value.toString())) {
                                StringTokenizer st = new StringTokenizer(ReportEditor.this.value.toString(), ":");
                                FilterRecord frec = null;
                                int id = 0;
                                String title = "";
                                if (st.hasMoreTokens()) {
                                    id = new Integer(st.nextToken()).intValue();
                                    title = st.nextToken();
                                }
                                frec = new FilterRecord(Kernel.instance().getObjectById(id, 0), title);
                                me.setOldFiltersValue(new FilterRecord[] {frec});
                            }
                            DesignerDialog dlg =
                                    new DesignerDialog(Or3Frame.instance(),
                                            "Выберите фильтр", me);
                            dlg.show();
                            int res = dlg.getResult();
                            if (res != ButtonsFactory.BUTTON_NOACTION
                                    && res == ButtonsFactory.BUTTON_OK) {
                                long fid=0;
                                if(me.getSelectedFilterValues().length>0){
                                    fid = me.getSelectedFilterValues()[0].getObjId();
                                }
                                field.setText(fid + ":" + kz.tamur.comps.Utils.getFilterNameById(fid));
                                ReportEditor.this.value = new Long(fid);
                            }
                            stopCellEditing();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else if (column == 3 || column == 4) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                        	ExpressionEditor exprEditor = new ExpressionEditor(ReportEditor.this.value != null?ReportEditor.this.value.toString() : "", DataCellEditor.this);
                            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Напишите Функцию", exprEditor);
                            dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
                            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
                            dlg.show();
                            int res = dlg.getResult();
                            if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                            	setExpression(exprEditor.getExpression());
                            }
                            stopCellEditingExpression();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
            field.add(btn, BorderLayout.EAST);
            return field;
        }

        public void setExpression(String expression) {
        	field.setText(expression);
            ReportEditor.this.value = expression;
       }
        
        public Object getCellEditorValue() {
            return ReportEditor.this.value;
        }
    }

    private class BooleanCellEditor extends DefaultCellEditor {

        private JCheckBox checkBox = Utils.createCheckBox(null, false);

        public BooleanCellEditor() {
        	super(new JCheckBox());
        	checkBox = Utils.createCheckBox(null, false);
        	checkBox.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					stopCellEditing();
				}
			});
            setClickCountToStart(0);
        }

        public boolean stopCellEditing() {
        	ReportEditor.this.value = checkBox.isSelected();
            return super.stopCellEditing();
        }


        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            ReportEditor.this.value = value;
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setForeground(Color.WHITE);
            checkBox.setSelected((Boolean)value);
            return checkBox;

        }

        public Object getCellEditorValue() {
            return ReportEditor.this.value;
        }
    }
    
    private static class BooleanCellRenderer extends JCheckBox implements TableCellRenderer, UIResource
    {
        private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public BooleanCellRenderer() {
	    super();
	    setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(true);
	}

        public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus, int row, int column) {
        	if (isSelected) {
        		setForeground(table.getSelectionForeground());
        		super.setBackground(table.getSelectionBackground());
        	}
        	else {
        		setForeground(table.getForeground());
        		setBackground(table.getBackground());
        	}
        	setSelected((value != null && ((Boolean)value).booleanValue()));

        	if (hasFocus) {
        		setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(noFocusBorder);
            }

            return this;
        }
    }

    private ClassBrowser getClassBrowser(String value) {
        final Kernel krn = Kernel.instance();
        ClassNode cls = null;
        String s = "";
        try {
            if ("".equals(value)) {
                if ("".equals(defaultClass)) {
                    cls = krn.getClassNodeByName("Объект");
                } else {
                    cls = krn.getClassNodeByName(defaultClass);
                }
            } else {
                try {
                    s = getClassNameFromPath(value.toString());
                    cls = krn.getClassNodeByName(s);
                    defaultClass = s;
                } catch(KrnException e) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.ERROR_MESSAGE, "\"" + s +
                            "\" - ошибочное имя класса!");
                }
            }
            classBrowser = new ClassBrowser(cls, true);
            classBrowser.setPreferredSize(new Dimension(800, 500));
            if (lastPath != null && !"".equals(lastPath)) {
                classBrowser.setSelectedPath(lastPath);
            }
            return classBrowser;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getClassNameFromPath(String path) {
        StringTokenizer st = new StringTokenizer(path, ".");
        String s = st.nextToken();
        return s;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    private boolean isListExists(DesignerTreeNode node) {
        if (editorType != FILTER_EDITOR) {
            for (int i = 0; i < model.size(); i++) {
                DesignerTreeNode n =  (DesignerTreeNode)model.get(i);
                if (node.getKrnObj().id == n.getKrnObj().id) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < model.size(); i++) {
                FilterRecord fr =  (FilterRecord)model.get(i);
                if (node.getKrnObj().id == fr.getObjId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setOldFilterNode(long oldFilterNode) {
        this.oldFilterId = oldFilterNode;
        selector.getSelectedTree().setSelectedNode(oldFilterId);
    }

    public long getOldFilterNode() {
        return oldFilterId;
    }

    public DesignerTree getTree() {
        return selector.getSelectedTree();
    }

    public Node getSelectedNode() {
        TreePath path = reportTree.getSelectionPath();
        if (path != null) {
            Object obj = path.getLastPathComponent();
            if (obj instanceof Node)
                return (Node) obj;
        }
        return null;
    }

    public Node[] getSelectedNodes() {
        TreePath[] paths = reportTree.getSelectionPaths();
        if (paths == null)
            return new Node[0];
        Node[] res = new Node[paths.length];
        for (int i = 0; i < paths.length; ++i)
            res[i] = (Node) paths[i].getLastPathComponent();
        return res;
    }

    public class TreeTableExpansionListener implements TreeExpansionListener {

        public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
            tableModel.fireTableDataChanged();
            reportTree.updateImage();
            reportTree.repaint();
        }

        public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
            tableModel.fireTableDataChanged();
            reportTree.updateImage();
            reportTree.repaint();
        }
    }

    public class TreeTableCellEditor implements TableCellEditor {
            public Component getTableCellEditorComponent(JTable table,
                            Object value, boolean isSelected, int r, int c) {
//                final JButton btn = ButtonsFactory.createEditorButton(
//                        ButtonsFactory.DEFAULT_EDITOR);
//                
//                btn.addActionListener(new ActionListener() {
//                	public void actionPerformed(ActionEvent e) {
//                		try {
//                			Node node = getSelectedNode();
//                			ReportRecord rpt = node.report;
//                			long objId = rpt.getObjId();
//                			KrnObject obj = Kernel.instance().getObjectById(objId, 0);
//                			if(obj != null) {
//                				if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
//                					Or3Frame.instance().getReportFrame().load(obj);
//                				}else {
//                					Or3Frame.instance().jumpReport(obj);
//                				}
//                				DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
//                				if(dialog != null)
//                					dialog.dispose();
//                			}
//                		} catch (Exception ex) {
//                			ex.printStackTrace();
//                		}
//                	}
//                });
//                reportTree.add(btn, BorderLayout.EAST);

                return reportTree;
            }

            public void cancelCellEditing() {
            }

            public boolean stopCellEditing() {
                    return true;
            }

            public Object getCellEditorValue() {
                    return null;
            }

            public boolean isCellEditable(EventObject e) {
                    if (e instanceof MouseEvent) {
                            TableModel tm = table.getModel();
                            for (int counter = tm.getColumnCount() - 1; counter >= 0; counter--) {
                                    if (tm.getColumnClass(counter) == TreeModel.class) {
                                            MouseEvent me = (MouseEvent) e;
                                            MouseEvent newME = new MouseEvent(reportTree, me.getID(), me
                                                            .getWhen(), me.getModifiers(), me.getX()
                                                            - table.getCellRect(0, counter,
                                                                            true).x, me.getY(), me.getClickCount(),
                                                            me.isPopupTrigger());
                                            reportTree.dispatchEvent(newME);
                                            break;
                                    }
                            }
                    }
                    return false;
            }

            public boolean shouldSelectCell(EventObject anEvent) {
                    return false;
            }

            public void addCellEditorListener(CellEditorListener l) {
            }

            public void removeCellEditorListener(CellEditorListener l) {
            }
    }

    public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
        protected int visRow;
        protected int visCol;
        private BufferedImage img;

        public TreeTableCellRenderer() {
            super();
            setOpaque(false);
            setCellRenderer(new ReportTreeCellRenderer());

            addTreeExpansionListener(new TreeExpansionListener() {
                public void treeCollapsed(TreeExpansionEvent event) {
                    updateImage();
                }

                public void treeExpanded(TreeExpansionEvent event) {
                    updateImage();
                }
            });
        }

        public void updateImage() {
            int w = getWidth();
            int h = getRowCount() * table.getRowHeight();
            TreeTableCellRenderer.super.setBounds(0, 0, w, h);
            if (w > 0 && h > 0) {
                img = new BufferedImage(w, h,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics g = img.getGraphics();
                TreeTableCellRenderer.super.paint(g);
                g.dispose();
            } else {
                img = null;
            }
        }

        public void setBounds(int x, int y, int w, int h) {
            visCol = x;
            super.setBounds(0, 0, table.getWidth(), table.getHeight());
        }

        public void paint(Graphics g) {
            if (img != null) {
                Graphics2D g2d = (Graphics2D)g;
                Color c = g2d.getColor();
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getRowHeight());
                g2d.setColor(c);
                g2d.drawImage(img, 0, 0, getWidth(), getRowHeight(), visCol, visRow * getRowHeight(), visCol + getWidth(), (visRow + 1) * getRowHeight(), null);
            }
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column) {
            if (img == null) {
                updateImage();
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            visRow = row;

            int modelIndex = table.getColumnModel().getColumn(column).getModelIndex();
            if (modelIndex == 0) return this;
            return this;
        }
    }

    public class Node extends DefaultMutableTreeNode {
        public ReportRecord report;

        public Node(ReportRecord rec) {
            this.report = rec;
            load();
        }

        private void load() {
            if (report == null)
                    return;

            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            // Загрузка детей
            if (report.isFolder()) {
                for (ReportRecord r : report.getChildren()) {
                    Node child = new Node(r);
                    m.insertNodeInto(child, this, getChildCount());
                }
            }
        }

        public String toString() {
            return report.toStringValue(langId);
        }

        public void insertChild(Node n, int index) {
            report.addChild(n.report, index);
        }

        public void removeNode(Node n) {
            report.removeChild(n.report);
        }
    }

    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
            /** Set to true when we are updating the ListSelectionModel. */
            protected boolean updatingListSelectionModel;

            public ListToTreeSelectionModelWrapper() {
                    super();
                    getListSelectionModel().addListSelectionListener(
                                    createListSelectionListener());
            }

            /**
             * Returns the list selection model. ListToTreeSelectionModelWrapper
             * listens for changes to this model and updates the selected paths
             * accordingly.
             */
            ListSelectionModel getListSelectionModel() {
                    return listSelectionModel;
            }

            public void setUpdatingListSelectionModel(boolean b) {
                    updatingListSelectionModel = b;
            }
            /**
             * This is overridden to set <code>updatingListSelectionModel</code>
             * and message super. This is the only place DefaultTreeSelectionModel
             * alters the ListSelectionModel.
             */
            public void resetRowSelection() {
                    if (!updatingListSelectionModel) {
                            updatingListSelectionModel = true;
                            try {
                                    super.resetRowSelection();
                            } finally {
                                    updatingListSelectionModel = false;
                            }
                    }
                    // Notice how we don't message super if
                    // updatingListSelectionModel is true. If
                    // updatingListSelectionModel is true, it implies the
                    // ListSelectionModel has already been updated and the
                    // paths are the only thing that needs to be updated.
            }

            /**
             * Creates and returns an instance of ListSelectionHandler.
             */
            protected ListSelectionListener createListSelectionListener() {
                    return new ListSelectionHandler();
            }

            /**
             * If <code>updatingListSelectionModel</code> is false, this will
             * reset the selected paths from the selected rows in the list selection
             * model.
             */
            protected void updateSelectedPathsFromSelectedRows() {
                    if (!updatingListSelectionModel) {
                            updatingListSelectionModel = true;
                            try {
                                    // This is way expensive, ListSelectionModel needs an
                                    // enumerator for iterating.
                                    if (!listSelectionModel.isSelectionEmpty()) {
                                            int min = listSelectionModel.getMinSelectionIndex();
                                            int max = listSelectionModel.getMaxSelectionIndex();
                                            // clearSelection();
                                            if (min != -1 && max != -1) {
                                                    reportTree.setSelectionInterval(min, max);
    /*                                                for (int counter = min; counter <= max; counter++) {
                                                            if (listSelectionModel.isSelectedIndex(counter)) {
                                                                    TreePath selPath = reportTree
                                                                                    .getPathForRow(counter);

                                                                    if (selPath != null) {
                                                                            setSelectionPath(selPath);
                                                                    }
                                                            }
                                                    }
    */                                        }
                                    }
                            } finally {
                                    updatingListSelectionModel = false;
                            }
                    }
            }

            /**
             * Class responsible for calling updateSelectedPathsFromSelectedRows
             * when the selection of the list changse.
             */
            class ListSelectionHandler implements ListSelectionListener {
                    public void valueChanged(ListSelectionEvent e) {
                            if (e.getValueIsAdjusting())
                                    return;
                            updateSelectedPathsFromSelectedRows();
                    }
            }
    }

    private class NodeOperationsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            JComponent comp = table;
            try {
                if (src == nodeCreateItem_) {
                    Node node = getSelectedNode();
                    if (node != null) {
                        String title = getNewTitle(comp);
                        if (title != null && title.length() > 0) {
                            InterfaceFrame frm = ControlTabbedContent.instance().getSelectedFrame();
                            String uid = frm.getNextUid();
                            frm.setString(uid, title);
                            //Map<Long, String> ns = new TreeMap<Long, String>();
                            //ns.put(langId, title);
                            ReportRecord rec = new ReportRecord(uid);
                            Node n = new Node(rec);
                            node.insertChild(n, node.getChildCount());
                            m.insertNodeInto(n, node, node.getChildCount());
                            reportTree.expandPath(new TreePath(n.getPath()));
                            int row = reportTree.getRowForPath(new TreePath(n.getPath()));
                            tableModel.fireTableRowsInserted(row, row);
                            reportTree.updateImage();
                        }
                    }
                } else if (src == nodeInsertItem_) {
                    Node node = getSelectedNode();
                    if (node != null) {
                        Node parent = (Node)node.getParent();
                        int index = parent.getIndex(node);
                        String title = getNewTitle(comp);
                        if (title != null && title.length() > 0) {
                            InterfaceFrame frm = ControlTabbedContent.instance().getSelectedFrame();
                            String uid = frm.getNextUid();
                            frm.setString(uid, title);
                            ReportRecord rec = new ReportRecord(uid);
                            Node n = new Node(rec);
                            parent.insertChild(n, index);
                            m.insertNodeInto(n, parent, index);
                            int row = reportTree.getRowForPath(new TreePath(n.getPath()));
                            tableModel.fireTableRowsInserted(row, row);
                            reportTree.updateImage();
                        }
                    }
                } else if (src == nodeAddItem_) {
                    addReports(comp);
                } else if (src == nodeDeleteItem_) {
                    deleteNode(comp);
                } else if (src == nodeCutItem_) {
                    cutNodes();
                } else if (src == nodeInsertSelectedItem_) {
                    addCutNodes(true);
                } else if (src == nodeAddSelectedItem_) {
                    addCutNodes(false);
                } else if (src == nodeRenameItem_) {
                    Node n = getSelectedNode();
                    if (n != null) {
                        Container cnt = comp.getTopLevelAncestor();
                        DesignerDialog dlg = null;
                        CreateElementPanel cp = new CreateElementPanel(
                                CreateElementPanel.RENAME_TYPE, n.toString());
                        if (cnt instanceof Dialog) {
                            dlg = new DesignerDialog((Dialog)cnt, "Переименовывание узла", cp);
                        } else {
                            dlg = new DesignerDialog((Frame)cnt, "Переименовывание узла", cp);
                        }
                        dlg.show();
                        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                            String title = cp.getElementName();
                            if (title != null) {
                                n.report.setName(title);
                                m.nodeChanged(n);
                                reportTree.updateImage();
                            }
                        }
                    }
                } else if (src == nodeJumptoReportItem_) {
                	Node node = getSelectedNode();
                	ReportRecord rpt = node.report;
                	long objId = rpt.getObjId();
                	KrnObject obj = Kernel.instance().getObjectById(objId, 0);
                	if(obj != null) {
                		if(ServiceControl.instance().getContentTabs().isServiceControlMode()){
                	        Or3Frame.instance().getReportFrame().load(obj);
                		}else {
                			Or3Frame.instance().jumpReport(obj);
                		}
                		DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
                		if(dialog != null)
                			dialog.dispose();
                	}
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private String getNewTitle(JComponent comp) {
            CreateElementPanel cp = new CreateElementPanel(
                    CreateElementPanel.CREATE_ELEMENT_TYPE, "");
            DesignerDialog dlg = null;
            Container cnt = comp.getTopLevelAncestor();
            if (cnt instanceof Dialog) {
                dlg = new DesignerDialog((Dialog)cnt, "Создание узла", cp);
            } else {
                dlg = new DesignerDialog((Frame)cnt, "Создание узла", cp);
            }
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                return cp.getElementName();
            }
            return null;
        }

        public void deleteNode(JComponent comp) throws KrnException {
            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            Node[] nodes = getSelectedNodes();
            int res = MessagesFactory.showMessageDialog(comp.getTopLevelAncestor(), MessagesFactory.CONFIRM_MESSAGE, "Удалить элемент(ы)?");
            if (res == ButtonsFactory.BUTTON_YES) {
                for (int i = 0; i < nodes.length; i++) {
                    Node n = nodes[i];
                    int row = reportTree.getRowForPath(new TreePath(n.getPath()));
                    if (n != null) {
                        ((Node)n.getParent()).removeNode(n);
                        m.removeNodeFromParent(n);
                    }
                    tableModel.fireTableRowsDeleted(row, row);
                }
                reportTree.updateImage();
            }
        }

        public void cutNodes() throws KrnException {
            cutNodes = new ArrayList();
            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            Node[] nodes = getSelectedNodes();
            for (int i = 0; i < nodes.length; i++) {
                Node n = nodes[i];
                TreeNode parent = n.getParent();
                boolean contains = false;
                while (parent != null) {
                    if (cutNodes.contains(parent)) {
                        contains = true;
                        break;
                    }
                    parent = parent.getParent();
                }
                if (!contains)
                    cutNodes.add(n);
            }
        }

        public void addReports(JComponent comp) throws KrnException {
            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            Node node = getSelectedNode();
            if (node != null) {
                Node parent = null;
                int index = 0;
                if (node.report.isFolder()) {
                    parent = node;
                    index = node.getChildCount();
                } else {
                    parent = (Node)node.getParent();
                    index = parent.getIndex(node);
                }

//                ReportEditor re =  new ReportEditor(REPORT_EDITOR, INTERFACE_AREA, langId);
                OpenElementPanel re = new OpenElementPanel(tree);
                DesignerDialog dlg = null;
                Container cnt = comp.getTopLevelAncestor();
                if (cnt instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog)cnt, "Выберите отчеты", re);
                } else {
                    dlg = new DesignerDialog((Frame)cnt, "Выберите отчеты", re);
                }
                re.setSearchUIDPanel(true);
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    DesignerTreeNode[] selNodes = re.getTree().getSelectedNodes();
                    for (DesignerTreeNode selNode : selNodes) {
                        long id;
                        if (selNode instanceof ServiceControlNode) {
                           id = ((ServiceControlNode)selNode).getValue().id;
                        }else {
                            id = selNode.getKrnObj().id;
                        }
                        ReportRecord rec = new ReportRecord( id, "", 0,"", "", false);
                        Node n = new Node(rec);
                        parent.insertChild(n, index);
                        m.insertNodeInto(n, parent, index++);
                        reportTree.expandPath(new TreePath(n.getPath()));
                        int row = reportTree.getRowForPath(new TreePath(n.getPath()));
                        if (row > -1) tableModel.fireTableRowsInserted(row, row);
                    }
                    reportTree.updateImage();
                }
            }
        }

        public void addCutNodes(boolean insert) throws KrnException {
            DefaultTreeModel m = (DefaultTreeModel) reportTree.getModel();
            Node node = getSelectedNode();
            for (int i = 0; i < cutNodes.size(); i++) {
                Node parent;
                int index = 0;
                if (!insert) {
                    parent = node;
                    index = node.getChildCount();
                } else {
                    parent = (Node)node.getParent();
                    index = parent.getIndex(node);
                }

                Node n = (Node) cutNodes.get(i);
                int row = reportTree.getRowForPath(new TreePath(n.getPath()));
                if (n != null) {
                    ((Node)n.getParent()).removeNode(n);
                    m.removeNodeFromParent(n);
                }
                tableModel.fireTableRowsDeleted(row, row);

                parent.insertChild(n, index);
                m.insertNodeInto(n, parent, index++);
                reportTree.expandPath(new TreePath(n.getPath()));
                row = reportTree.getRowForPath(new TreePath(n.getPath()));
                if (row > -1) tableModel.fireTableRowsInserted(row, row);
            }
            reportTree.updateImage();
            cutNodes = new ArrayList();
        }

    }

    private class TreeMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger())
                showNodeOperations(e);
        }

        private void showNodeOperations(MouseEvent e) {
            TreePath path = reportTree.getPathForLocation(e.getX(), e.getY());
            if (path != null && !reportTree.isPathSelected(path)) {
                if (e.isShiftDown())
                    reportTree.addSelectionPath(path);
                else
                    reportTree.setSelectionPath(path);
            }
            Node n = getSelectedNode();
            if (n != null) {
                nodeRenameItem_.setEnabled(true);
                nodeInsertItem_.setEnabled(true);
                nodeCreateItem_.setEnabled(true);
                nodeDeleteItem_.setEnabled(true);

                nodeCutItem_.setEnabled(true);
                nodeAddSelectedItem_.setEnabled(true);
                nodeInsertSelectedItem_.setEnabled(true);

                if (n.getParent().getParent() == null) {
                    nodeRenameItem_.setEnabled(false);
                    nodeInsertItem_.setEnabled(false);
                    nodeDeleteItem_.setEnabled(false);
                    nodeCutItem_.setEnabled(false);
                    nodeInsertSelectedItem_.setEnabled(false);
                }
                else if (!n.report.isFolder()) {
                    nodeRenameItem_.setEnabled(false);
                    nodeCreateItem_.setEnabled(false);
                }

                nodeOperations_.show(table, e.getX(), e.getY());
            }
        }
    }

    private class ReportTreeCellRenderer extends JLabel implements TreeCellRenderer {

        public ReportTreeCellRenderer() {
        }


        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf, int row,
                                                      boolean hasFocus) {
            setOpaque(false);
            setForeground(Color.black);
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
            } else {
                setBackground(Utils.getLightSysColor());
            }
            setFont(Utils.getDefaultFont());
            if (value instanceof Node) {
                if (((Node)value).report.isFolder()) {
                    if (expanded) {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                    } else {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                    }
                } else {
                    setIcon(null);
                }
            } else {
                setIcon(null);
            }
            setText(value.toString());
            return this;
        }
    }
}
