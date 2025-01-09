package kz.tamur.comps;

import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeTablePropertyRoot;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.CheckBoxColumnAdapter;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.DateColumnAdapter;
import kz.tamur.rt.adapters.FloatColumnAdapter;
import kz.tamur.rt.adapters.IntColumnAdapter;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.rt.adapters.TableAdapter.RtTableModel;
import kz.tamur.rt.adapters.TreeAdapter2;
import kz.tamur.rt.adapters.TreeAdapter2.Node;
import kz.tamur.rt.adapters.TreeTableAdapter2;
import kz.tamur.util.BooleanTableCellRenderer;
import kz.tamur.util.DateTableCellRenderer;
import kz.tamur.util.IntegerTableCellRenderer;
import kz.tamur.util.LangItem;
import kz.tamur.util.OrCellRenderer;
import kz.tamur.util.Pair;
import kz.tamur.util.ZebraCellRenderer;

import org.jdom.Element;

import com.cifs.or2.client.FloatTableCellRenderer;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 29.11.2004
 * Time: 15:51:43
 */
public class OrTreeTable2 extends OrTable {

    private static PropertyNode treeTableProps = new TreeTablePropertyRoot();
    private ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private String treeName;
    private String treeNameUID;
    private int treeWidth;
    private TreeTableCellRenderer tree;
    private TableCellEditor treeCellEditor;
    private Color zebra1;
    private Color zebra2;
    private ListToTreeSelectionModelWrapper selectionWrapper;
    private Font font;
    private Font fontHeader;
    private Color fontColorHeader;
    private JLabel expandLabel = new JLabel(getImageIconFull("TreeExpandIcon.png"));
    private JLabel collapseLabel = new JLabel(getImageIconFull("TreeCollapseIcon.png"));
    private boolean useCheck = false;
    private boolean onlyLeaf = true;
    private int viewType;
    private boolean showSearchLine = false;
    protected boolean rowNowrap = true;

    OrTreeTable2(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        super(xml, mode, fm, frame);
        table.setGridColor(Color.white);
        PropertyValue pv = getPropertyValue(treeTableProps.getChild("treeTitle1").getChild("font"));
        fontHeader = pv == null ? null : pv.fontValue();

        pv = getPropertyValue(treeTableProps.getChild("treeTitle1").getChild("fontColorCol"));
        fontColorHeader = pv == null ? null : pv.colorValue();

        pv = getPropertyValue(treeTableProps.getChild("view").getChild("showSearchLine"));
        if (pv.isNull()) {
        	showSearchLine = ((Boolean) getProperties().getChild("view").getChild("showSearchLine").getDefaultValue()).booleanValue();
        } else {
        	showSearchLine = pv.booleanValue();
        }

        pv = getPropertyValue(treeTableProps.getChild("view").getChild("rowNowrap"));
        rowNowrap = pv.isNull() ? (Boolean) treeTableProps.getChild("view").getChild("rowNowrap").getDefaultValue() : pv.booleanValue();

        if (mode == Mode.RUNTIME) {
            onlyLeaf = getPropertyValue(treeTableProps.getChild("obligation").getChild("onlyLeaf")).booleanValue();
            PropertyNode pn = treeTableProps.getChild("view");
            pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
            font = pv == null ? null : pv.fontValue();

            tree = new TreeTableCellRenderer(xml, mode, this, font);
            pv = getPropertyValue(pn.getChild("expandAll"));

            useCheck = getPropertyValue(pn.getChild("useCheck")).booleanValue();
            boolean multiselection = getPropertyValue(treeTableProps.getChild("pov").getChild("multiselection")).booleanValue();
            useCheck = useCheck && multiselection;
            tree.setUseCheck(useCheck);
            ((TreeTableAdapter2) adapter).setUseCheck(useCheck);

            tree.setAdapter(((TreeTableAdapter2) adapter).getTreeAdapter());
            tree.getAdapter().setTree(tree);
            tree.getAdapter().setExpandAll(pv.booleanValue());
            tree.setRootVisible(true);
            tree.setTreeTableRenderer();
            tree.setOpaque(false);
            tree.addTreeExpansionListener(new TreeTableExpansionListener());
            tree.setRowHeight(table.getRowHeight());
            selectionWrapper = new ListToTreeSelectionModelWrapper();
            tree.setSelectionModel(selectionWrapper);
            table.setSelectionModel(selectionWrapper.getListSelectionModel());
            tree.setTreeTableModel(adapter.getModel());
        }
        TableColumnModel cm = table.getColumnModel();
        TableColumn tc = cm.getColumn(0);

        treeCellEditor = new TreeTableCellEditor();

        tc.setCellEditor(treeCellEditor);
        tc.setCellRenderer(tree);
        tc.setHeaderRenderer(createRendererFirstColumn());
        if (mode == Mode.RUNTIME) {
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
                                Rectangle p = OrTreeTable2.this.getBounds();
                                MouseEvent newME = new MouseEvent(tree, e.getID(), e.getWhen(), e.getModifiers(), e.getX()
                                        - table.getCellRect(0, counter, true).x - p.x - table.getX(), e.getY() - p.y
                                        - table.getY(), e.getClickCount(), true);
                                tree.dispatchEvent(newME);
                                break;
                            }
                        }
                    }
                }
            });
            updateRenderers();

            table.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (TableModelEvent.HEADER_ROW == e.getFirstRow()) {
                        table.setDefaultRenderer(TreeModel.class, tree);

                        TableColumnModel cm = getJTable().getColumnModel();
                        TableColumn tc = cm.getColumn(0);
                        tc.setPreferredWidth(getTreeWidth());

                        RtTableModel model = adapter.getModel();
                        for (int i = 1; i < model.getColumnCount(); ++i) {
                            ColumnAdapter orc = model.getColumnAdapter(i);
                            tc = getJTable().getColumnModel().getColumn(i);
                            if (orc.getCellRenderer() == null) {
                                ZebraCellRenderer r = null;
                                if (orc instanceof CheckBoxColumnAdapter) {
                                    r = new BooleanTableCellRenderer();
                                    orc.setCellRenderer(r);
                                } else if (orc instanceof DateColumnAdapter) {
                                    r = new DateTableCellRenderer((DateColumnAdapter) orc);
                                    orc.setCellRenderer(r);
                                } else if (orc instanceof IntColumnAdapter) {
                                    r = new IntegerTableCellRenderer((IntColumnAdapter) orc);
                                    orc.setCellRenderer(r);
                                } else if (orc instanceof FloatColumnAdapter) {
                                    r = new FloatTableCellRenderer((FloatColumnAdapter) orc);
                                    orc.setCellRenderer(r);
                                } else {
                                    r = new OrCellRenderer();
                                    orc.setCellRenderer(r);
                                }
                                r.setZebra1Color(getZebraColor1());
                                r.setZebra2Color(getZebraColor2());
                            } else {
                                ZebraCellRenderer r = (ZebraCellRenderer) orc.getCellRenderer();
                                r.setZebra1Color(model.getZebra1Color());
                                r.setZebra2Color(model.getZebra2Color());
                            }
                            tc.setCellRenderer(tree);
                            tc.setCellEditor(orc.getCellEditor());
                            updateRenderers();
                        }
                    }
                }
            });

            JTableHeader header = table.getTableHeader();
            header.addMouseListener(new TreeTableColumnListener());
            // скрыть корень дерева
            pv = getPropertyValue(treeTableProps.getChild("view").getChild("hideRoot"));
            // tree.setRootVisible(pv.isNull() || !pv.booleanValue());
            if (pv.isNull() || !pv.booleanValue()) {
                // TODO реализация отложена
            }

        }
        if (mode == Mode.DESIGN) {
            JTableHeader header = table.getTableHeader();
            header.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    repaintAll();
                }

                public void mouseExited(MouseEvent e) {
                    repaintAll();
                }

                public void mouseClicked(MouseEvent e) {
                    repaintAll();
                }
            });
        }
    }

    protected TableAdapter createAdapter() throws KrnException {
        // TODO Перевести на OrFrame
        return new TreeTableAdapter2(frame, this, false);
    }

    protected void init() {
        super.init();
        PropertyValue pv = getPropertyValue(treeTableProps.getChild("treeTitle"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            treeNameUID = (String) p.first;
            treeName = (String) p.second;
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
                e.printStackTrace();
            }
        }
        pv = getPropertyValue(treeTableProps.getChild("pos").getChild("treeWidth"));
        treeWidth = pv.isNull() || pv.intValue() == 0 ? Constants.DEFAULT_PREF_WIDTH : pv.intValue();
        pv = getPropertyValue(treeTableProps.getChild("view").getChild("viewType"));
        if (!pv.isNull()) {
            viewType = pv.enumValue();
        } else {
            viewType = ((EnumValue) treeTableProps.getChild("view").getChild("viewType").getDefaultValue()).code;
        }
    }

    protected DefaultOrTableModel getTableModel() {
        return new DefaultOrTreeTableModel();
    }

    protected OrTableColumnModelListener getTableColumnModelListener() {
        return new OrTreeTableColumnModelListener(table);
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public PropertyNode getProperties() {
        return treeTableProps;
    }

    public void setLangId(long langId) {
        isSelfChange = true;
        super.setLangId(langId);
        treeName = frame.getString(treeNameUID);
        if ("Безымянный".equals(treeName)) {
            treeName = "Дерево";
            try {
                LangItem li = LangItem.getById(langId);
                if ("KZ".equals(li.code))
                    treeName = "А\u0493аш";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TableColumn tc = table.getColumnModel().getColumn(0);
        // преобразование заголовка таблицы в многострочный текст
        if (treeName.contains("@")) {
            treeName = "<html><p align=\"center\">" + treeName.replaceAll("@", "<br>") + "</p></html>";
        }
        tc.setHeaderValue(treeName);
        if (tree != null) {
            tree.setLangId(langId);
        }
        isSelfChange = false;
    }

    private class DefaultOrTreeTableModel extends DefaultOrTableModel {
        private boolean movingBack = false;

        public void addColumn(OrTableColumn col) {
            col.addPropertyChangeListener(this);
            columns.add(col);
            JTable tb = getJTable();
            col.setModelIndex(columns.size());
            TableColumn column = new TableColumn(columns.size(), col.getPreferredWidth());
            tb.getColumnModel().addColumn(column);
            column.setMaxWidth(col.getMaxWidth());
            column.setMinWidth(col.getMinWidth());
            fireTableStructureChanged();
        }

        public void removeColumn(OrTableColumn col) {
            if (col == null)
                return;
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
                TableColumn c = tcm.getColumn(idx + 1);
                tcm.removeColumn(c);
            }
            fireTableStructureChanged();
        }

        public void moveColumn(int startIndex, int endIndex) {
            if (startIndex == 0 || endIndex == 0) {
                movingBack = !movingBack;
                if (movingBack) {
                    table.getColumnModel().moveColumn(endIndex, startIndex);
                } else {
                    fireTableStructureChanged();
                }
                return;
            }
            OrTableColumn tc = (OrTableColumn) columns.get(startIndex - 1);
            Utils.moveListElementTo(columns, startIndex - 1, endIndex - 1);
            Element elem = xml.getChild("columns");
            List list = elem.getChildren();
            Utils.moveListElementTo(list, startIndex - 1, endIndex - 1);
            fireTableStructureChanged();
            ControlTabbedContent.instance().propertyModified(tc);
        }

        public int getColumnCount() {
            return columns.size() + 1;
        }

        public int getRowCount() {
            return 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        public String getColumnName(int column) {
            if (column == 0) {
                return treeName;
            }
            return getColumn(column - 1).getTitle();
        }

        public OrTableColumn getColumn(int colIndex) {
            if (colIndex == 0) {
                return null;
            }
            return (OrTableColumn) columns.get(colIndex - 1);
        }

        public void setInterfaceLangId(int langId) {
            for (int i = 0; i < columns.size(); i++) {
                OrTableColumn column = (OrTableColumn) columns.get(i);
                column.setLangId(langId);
            }
            fireTableStructureChanged();
        }

        public void fireTableStructureChanged() {
            TableColumnModel columnModel = table.getColumnModel();
            TableColumn tc = columnModel.getColumn(0);
            tc.setPreferredWidth(treeWidth);

            updateHeaderRenderers();
            if (isSelfChange && columnModel.getColumnCount() > 0) {
                for (int i = 1; i < columns.size() + 1; i++) {
                    OrTableColumn column = (OrTableColumn) columns.get(i - 1);
                    tc = columnModel.getColumn(i);
                    tc.setPreferredWidth(column.getPreferredWidth());
                    tc.setMaxWidth(column.getMaxWidth());
                    tc.setMinWidth(column.getMinWidth());
                }
            }
            super.superFireTableStructureChanged();
        }
    }

    class OrTreeTableColumnModelListener extends OrTableColumnModelListener {

        public OrTreeTableColumnModelListener(JTable table) {
            super(table);
        }

        public void columnMoved(TableColumnModelEvent e) {
            int fromIdx = e.getFromIndex();
            int toIdx = e.getToIndex();
            if (fromIdx != toIdx) {
                DefaultOrTableModel model = (DefaultOrTableModel) table.getModel();
                model.moveColumn(fromIdx, toIdx);
            }
        }

        public void columnMarginChanged(ChangeEvent e) {
            if (!isSelfChange) {
                TableColumnModel columnModel = table.getColumnModel();
                OrTableColumn column = null;

                TableColumn tableColumn = columnModel.getColumn(0);
                if (treeWidth != tableColumn.getWidth()) {
                    setPropertyValue(new PropertyValue(tableColumn.getWidth(), getProperties().getChild("pos").getChild(
                            "treeWidth")));
                    ControlTabbedContent.instance().propertyModified(column);
                }

                for (int i = 1; i < columnModel.getColumnCount(); i++) {
                    column = ((DefaultOrTreeTableModel) table.getModel()).getColumn(i);
                    tableColumn = columnModel.getColumn(i);
                    if (column.getPreferredWidth() != tableColumn.getWidth()) {
                        column.setPropertyValue(new PropertyValue(tableColumn.getWidth(), column.getProperties()
                                .getChild("width").getChild("pref")));
                        ((DefaultOrTreeTableModel) table.getModel()).fireTableStructureChanged();
                        ControlTabbedContent.instance().propertyModified(column);
                    }
                }
            }
        }
    }

    protected void updateHeaderRenderers() {
        DefaultOrTreeTableModel model = (DefaultOrTreeTableModel) table.getModel();
        TableColumn tc = table.getColumnModel().getColumn(0);
        tc.setHeaderValue(treeName);
        tc.setHeaderRenderer(createRendererFirstColumn());
        OrTableColumn col;
        DefaultTableCellRenderer rend;
        for (int i = 1; i < table.getColumnCount(); i++) {
            tc = table.getColumnModel().getColumn(i);
            col = model.getColumn(i);
            rend = (DefaultTableCellRenderer) col.createDefaultRenderer();
            tc.setHeaderRenderer(rend);
        }
    }

    public void updateRenderers() {
        Color zebraColor1 = getZebraColor1();
        Color zebraColor2 = getZebraColor2();
        if (zebraColor1 == null) {
            zebraColor1 = Color.WHITE;
        }
        if (zebraColor2 == null) {
            zebraColor2 = Color.WHITE;
        }
        zebra1 = zebraColor1;
        zebra2 = zebraColor2;
    }

    public String getTreeName() {
        return treeName;
    }

    public int getTreeWidth() {
        return treeWidth;
    }

    public class TreeTableCellRenderer extends OrTree2 implements TableCellRenderer {
        protected int visRow;
        protected int visCol;
        protected OrTreeTable2 table;
        private TableAdapter.RtTableModel model;

        private TreeLabel rend;
        private Font font = null;

        public TreeTableCellRenderer(Element xml, int mode, OrTreeTable2 table) {
            this(xml, mode, table, Utils.getDefaultFont());
        }

        public TreeTableCellRenderer(Element xml, int mode, OrTreeTable2 table, Font font) {
            super(xml, mode, table.frame);
            this.table = table;
            setOpaque(false);
            rend = new TreeLabel();
            rend.setCellHeight(table.getJTable().getRowHeight());
            this.font = font == null ? Utils.getDefaultFont() : font;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            int modelIndex = table.getColumnModel().getColumn(column).getModelIndex();
            if (getPathForRow(row) == null) {
                return null;
            }
            if (modelIndex == 0) {
                Node node = (Node) getPathForRow(row).getLastPathComponent();
                return getTreeCellRenderer(table, isSelected, hasFocus, row, node);
            }
            ColumnAdapter orc = (ColumnAdapter) model.getColumns().get(modelIndex - 1);
            Component comp = (Component) orc.getCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            return comp;
        }

        private Component getTreeCellRenderer(JTable table, boolean isSelected, boolean hasFocus, int row, Node node) {
            rend.setBackground(isSelected || table.isRowSelected(row) ? table.getSelectionBackground() : row % 2 == 0 ? zebra1
                    : zebra2);
            rend.setCellBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : null);
            if (mode == Mode.RUNTIME) {
                String iconNameExp = MainFrame.iconsSettings.get("iconNodeParentExp");
                String iconNameCol = MainFrame.iconsSettings.get("iconNodeParentCol");
                ImageIcon iconExp = getImageIconFull(iconNameExp);
                ImageIcon iconCol = getImageIconFull(iconNameCol);
                if (node.getAllowsChildren()) {
                    if (this.table.getViewType() > Constants.FILES) {
                        rend.setIcon(getImageIconFull("groupNode.png"));
                    } else {
                        if (node.isLeaf()) {
                            rend.setIcon(iconCol == null ? getImageIcon("CloseFolder") : iconCol);
                        } else {
                            if (isExpanded(row)) {
                                rend.setIcon(iconExp == null ? getImageIcon("Open") : iconExp);
                            } else {
                                rend.setIcon(iconCol == null ? getImageIcon("CloseFolder") : iconCol);
                            }
                        }
                    }
                } else {
                    rend.setIcon(null);
                }
            } else {
                rend.setIcon(!node.getAllowsChildren() ? null : isExpanded(row) ? getImageIcon("Open")
                        : getImageIcon("CloseFolder"));
            }
            rend.setFont(font);

            final int offset = getRowBounds(row) != null ? getRowBounds(row).x : 0;
            rend.setOffset(offset);
            String title = node.toString(row);
            rend.setText(title == null ? "" : title);

            if (MainFrame.TRANSPARENT_CELL_TABLE > 0) {
                // непрозрачность для текста
                TreeLabel newComp = new TreeLabel() {
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        int k = 0;
                        if (rend.getIcon() != null) {
                            k = 15;
                            g.drawImage(((ImageIcon) rend.getIcon()).getImage(), 0, -2, null);
                        }
                        g.setFont(font);
                        g.drawString(rend.getText(), 9 + k, 11);
                    }
                };

                newComp.setAlignmentX(rend.getAlignmentX());
                newComp.setIcon(rend.getIcon());
                newComp.setFont(rend.getFont());
                newComp.setBackground(rend.getBackground());
                newComp.setForeground(rend.getForeground());
                newComp.setOpaque(true);
                newComp.setToolTipText(rend.getToolTipText());
                newComp.setOffset(offset);
                newComp.setCellBorder(rend.getCellBorder());
                return newComp;
            }
            return rend;
        }

        public void setTreeTableModel(TableAdapter.RtTableModel model) {
            this.model = model;
        }

        public OrCellRenderer getRenderer(int col) {
            if (col == 0)
                return null;
            ColumnAdapter orc = (ColumnAdapter) model.getColumns().get(col - 1);
            return (OrCellRenderer) orc.getCellRenderer();
        }

        public JTable getTable() {
            return table.getJTable();
        }

        public TreeTableAdapter2 getTableAdapter() {
            return (TreeTableAdapter2) table.getAdapter();
        }
    }

    public void setPropertyValue(PropertyValue value) {
        String name = value.getProperty().getName();
        String nameParent = value.getProperty().getParent().getName();
        if ("treeTitle".equals(name)) {
            treeName = value.stringValue();
            ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
            repaint();
        } else if ("treeWidth".equals(name)) {
            treeWidth = value.intValue();
            ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
        } else if ("treeTitle1".equals(nameParent)) {
            if ("font".equals(name)) {
                fontHeader = value.fontValue();
                ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
            } else if ("fontColorCol".equals(name)) {
                fontColorHeader = value.colorValue();
                ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
            }
            repaint();
        } else if ("showSearchLine".equals(name)) {
        	showSearchLine = value.booleanValue();
        } else if ("rowNowrap".equals(name)) {
            rowNowrap = value.booleanValue();
        }
        super.setPropertyValue(value);
    }

    public class TreeTableExpansionListener implements TreeExpansionListener {

        public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
            JComponent component = OrTreeTable2.this;
            if (component.getTopLevelAncestor() != null) {
                CursorToolkit.startWaitCursor(component);
                getTreeTableAdapter().nodeExpanded((Node) e.getPath().getLastPathComponent());
                CursorToolkit.stopWaitCursor(component);
            }
        }

        public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
            CursorToolkit.startWaitCursor(OrTreeTable2.this);
            getTreeTableAdapter().nodeCollapsed((Node) e.getPath().getLastPathComponent());
            CursorToolkit.stopWaitCursor(OrTreeTable2.this);
        }
    }

    /*
     * protected void updateRenderers(TableColumn tabColumn) { final TableColumn
     * tableColumn = tabColumn; Color zebraColor1 = table.getZebraColor(1);
     * Color zebraColor2 = table.getZebraColor(2); if (zebraColor1 == null)
     * zebraColor1 = Color.WHITE; if (zebraColor2 == null) zebraColor2 =
     * Color.WHITE; }
     * 
     * /** ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel to
     * listen for changes in the ListSelectionModel it maintains. Once a change
     * in the ListSelectionModel happens, the paths are updated in the
     * DefaultTreeSelectionModel.
     */
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
        /** Set to true when we are updating the ListSelectionModel. */
        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener(createListSelectionListener());
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
         * This is overridden to set <code>updatingListSelectionModel</code> and message super. This is the only place DefaultTreeSelectionModel
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
            updatingListSelectionModel = true;
            try {
                // This is way expensive, ListSelectionModel needs an
                // enumerator for iterating.
                if (!listSelectionModel.isSelectionEmpty()) {
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();
                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int i = min; i <= max; i++) {
                            if (listSelectionModel.isSelectedIndex(i)) {
                                Node node = getTreeTableAdapter().getNodeForRow(i);
                                if (node != null) {
                                    addSelectionPath(new TreePath(node.getPath()));
                                }
                            }
                        }
                        getTreeTableAdapter().setSelectedRow(min, OrTreeTable2.this);
                    }
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            } finally {
                updatingListSelectionModel = false;
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

    public class TreeTableCellEditor implements TableCellEditor {

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
            return tree;
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
                        MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX()
                                - table.getCellRect(0, counter, true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
                        tree.dispatchEvent(newME);
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

    public class TreeLabel extends JLabel {
        private int offsetX = 0;
        private int cellHeight;
        private Border cellBorder;
        private int cellWidth;
        private int startX;

        public TreeLabel() {
            super();
            setFont(Utils.getDefaultFont());
            setOpaque(true);

        }

        public void paint(Graphics g) {
            int translate = 0;
            if (startX > 0) {
                Color c = g.getColor();
                g.setColor(getBackground());
                g.fillRect(0, 0, cellWidth, cellHeight);
                g.setColor(c);
                translate = offsetX - startX;
                g.translate(translate, 0);
                super.paint(g);
            } else {
                Color c = g.getColor();
                g.setColor(getBackground());
                g.fillRect(0, 0, offsetX, cellHeight);
                g.setColor(c);
                translate = offsetX;
                g.translate(translate, 0);
                super.paint(g);
            }
            if (cellBorder != null) {
                g.translate(-translate, 0);
                cellBorder.paintBorder(this, g, 0, 0, cellWidth, cellHeight);
            }
        }

        public void setOffset(int x) {
            this.offsetX = x;
        }

        public void setCellHeight(int cellHeight) {
            this.cellHeight = cellHeight;
        }

        public void setCellBorder(Border cellBorder) {
            this.cellBorder = cellBorder;
        }

        public Border getCellBorder() {
            return cellBorder;
        }

        public void setCellWidth(int cellWidth) {
            this.cellWidth = cellWidth;
        }

        public void setStartX(int startX) {
            this.startX = startX;
        }

        public void setBounds(int x, int y, int w, int h) {
            startX = x;
            cellWidth = w;
            super.setBounds(x, y, w, h);
        }
    }

    private TreeTableAdapter2 getTreeTableAdapter() {
        return (TreeTableAdapter2) adapter;
    }

    public class TreeTableColumnListener extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            TableColumnModel columnModel = getJTable().getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(e.getX());
            if (columnIndex > 0) {
                int columnIndex_ = columnModel.getColumnIndexAtX(e.getX() + columnModel.getColumn(columnIndex).getWidth() / 2);
                Object src = e.getSource();
                if (columnIndex == 0 && src instanceof JTableHeader) {
                    String toolTip = (columnIndex_ != columnIndex) ? res.getString("treeCollapse") : res.getString("treeExpand");
                    ((JTableHeader) src).setToolTipText(toolTip);
                } else if (src instanceof JTableHeader) {
                    ((JTableHeader) src).setToolTipText(null);
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
            TableColumnModel columnModel = getJTable().getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(e.getX());
            if (columnIndex != -1) {
                int columnIndex_ = columnModel.getColumnIndexAtX(e.getX() + columnModel.getColumn(columnIndex).getWidth() / 2);
                int columnModelIndex = columnModel.getColumn(columnIndex).getModelIndex();
                if (columnModelIndex == 0) {
                    TreeAdapter2.Node root = (TreeAdapter2.Node) tree.getAdapter().getModel().getRoot();
                    if (columnIndex == columnIndex_) {
                        tree.expandAll(root);
                    } else {
                        tree.collapseAll(root);
                    }
                }
            }
        }
    }

    void setTransparent(boolean transparent) {
        setOpaque(transparent);
        scroller.setOpaque(transparent);
        scroller.getViewport().setOpaque(transparent);
    }

    protected TableCellRenderer createRendererFirstColumn() {
        // рендер с изменяемым положением текста
        OrHeaderTableCellRenderer label = new OrHeaderTableCellRenderer() {
            public void paint(Graphics g) {
                super.paint(g);
                if (getMode() == Mode.DESIGN && isSelected) {
                    kz.tamur.rt.Utils.drawRects(this, g);
                }
            }

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                        setHorizontalAlignment(CENTER);
                        setForeground(header.getForeground());
                        setBackground(backgroundMain);
                        setFont(header.getFont());
                        if (fontHeader != null) {
                            setFont(fontHeader);
                        }
                        if (fontColorHeader != null) {
                            setForeground(fontColorHeader);
                        }
                    }
                    if (getMode() == Mode.DESIGN && OrTreeTable2.this.isSelected) {
                        setBackground(Color.WHITE);
                    }
                }
                boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
                Border border = BorderFactory.createEmptyBorder();
                this.setText(treeName);
                this.setOpaque(isOpaque);
                this.setBorder(border);

                expandLabel.setOpaque(isOpaque);
                expandLabel.setBorder(border);
                JPanel expandPanel = new JPanel(new GridBagLayout());
                expandPanel.setOpaque(isOpaque);
                expandPanel.add(expandLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, Constants.INSETS_5, 0, 0));

                collapseLabel.setOpaque(isOpaque);
                collapseLabel.setBorder(border);
                JPanel collapsePanel = new JPanel(new GridBagLayout());
                collapsePanel.setOpaque(isOpaque);
                collapsePanel.add(collapseLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, Constants.INSETS_5, 0, 0));

                JLabel finishLabel = new JLabel();
                finishLabel.setOpaque(isOpaque);
                finishLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                finishLabel.setLayout(new BorderLayout());
                finishLabel.add(expandPanel, BorderLayout.WEST);
                finishLabel.add(this, BorderLayout.CENTER);
                finishLabel.add(collapsePanel, BorderLayout.EAST);
                return finishLabel;
            }
        };
        return label;
    }

    /**
     * @return the tree
     */
    public TreeTableCellRenderer getTree() {
        return tree;
    }

    /**
     * Выбирать только листья?
     * 
     * @return <code>true</code> если разрешён выбор только листьев.
     */
    public boolean isOnlyLeaf() {
        return onlyLeaf;
    }

    public int getViewType() {
        return viewType;
    }
    
	public boolean isShowSearchLine() {
		return showSearchLine;
	}
	
	public boolean isRowNowrap() {
		return rowNowrap;
	}
}