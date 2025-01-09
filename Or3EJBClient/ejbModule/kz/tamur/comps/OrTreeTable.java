package kz.tamur.comps;

import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeTablePropertyRoot;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeTableAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.LangItem;
import kz.tamur.util.OrCellRenderer;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;


/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 29.11.2004
 * Time: 15:51:43
 * To change this template use File | Settings | File Templates.
 */
public class OrTreeTable extends OrTable {
    public static PropertyNode TREE_TABLE_PROPS = new TreeTablePropertyRoot();
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    //private JTable table = new JTable(new DefaultOrTreeTableModel());
    //private TableScroller scroller = new TableScroller(table);

    private String treeName;
    private String treeNameUID;

    private int treeWidth;
  //  OrTreeTableColumn column;
    private TreeTableAdapter treeTable;
    private OrTree tree;
	private TableCellEditor treeCellEditor;
    private TreeAdapter treeAdapter;
    private Color zebra1;
    private Color zebra2;
    private ListToTreeSelectionModelWrapper selectionWrapper;
    private boolean onlyLeaf = true;
    private int viewType; 

    OrTreeTable(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        super(xml, mode, fm, frame);
        if (mode == Mode.RUNTIME) {
            tree = new TreeTableCellRenderer(xml, mode, this);
            tree.setTreeTableRenderer();
        } else
            tree = new OrTree(xml, mode, frame);

        tree.setRootVisible(false);
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        table.setGridColor(Color.white);

        if (mode == Mode.RUNTIME) {
            
            onlyLeaf = getPropertyValue(TREE_TABLE_PROPS.getChild("obligation").getChild("onlyLeaf")).booleanValue();
            
            this.treeAdapter = new TreeAdapter((UIFrame) frame, tree, false);
            this.treeAdapter.setComponentLangId(adapter.getComponentLangId());
            PropertyValue pv = getPropertyValue(TREE_TABLE_PROPS.getChild("ref").getChild("treeFilter"));
            if (!pv.isNull()) {
                treeAdapter.setDefaultFilterId(pv.filterValue().getObjId());
            }
            // tree.setOpaque(false);
            tree.addTreeExpansionListener(new TreeTableExpansionListener());
            tree.setRowHeight(table.getRowHeight());

            // Force the JTable and JTree to share their row selection models.
            selectionWrapper = new ListToTreeSelectionModelWrapper();
            tree.setSelectionModel(selectionWrapper);
            table.setSelectionModel(selectionWrapper.getListSelectionModel());

            if (treeAdapter.rootRef != null) {
                treeAdapter.rootRef.removeOrRefListener(treeAdapter);
                treeAdapter.rootRef.addOrRefListener(adapter);
            }

            if (treeAdapter.rootCalcRef != null) {
                treeAdapter.rootCalcRef.removeOrRefListener(treeAdapter);
                treeAdapter.rootCalcRef.addOrRefListener(adapter);
            }

            ((TreeTableCellRenderer) tree).setTreeTableModel(adapter.getModel());
        }
        TableColumnModel cm = table.getColumnModel();
        TableColumn tc = cm.getColumn(0);
        treeCellEditor = new TreeTableCellEditor();
        //updateHeaderRenderers();
        if (mode == Mode.RUNTIME) {
            tc.setCellEditor(treeCellEditor);
            tc.setCellRenderer((OrTreeTable.TreeTableCellRenderer) tree);
            tc.setHeaderRenderer(createRendererFirstColumn());
            table.setDefaultEditor(TreeModel.class, treeCellEditor);

            treeAdapter.setAccess(adapter.getAccess());

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
                                Rectangle p = OrTreeTable.this.getBounds();
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
        }
        if (mode == Mode.DESIGN) {
            JTableHeader header = table.getTableHeader();
            header.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {
                    repaintAll();
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                    repaintAll();
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                    repaintAll();
                }
            });
        }
    }

    protected TableAdapter createAdapter() throws KrnException {
        TreeTableAdapter tta = new TreeTableAdapter(frame, this, false);
        PropertyValue pv = getPropertyValue(TREE_TABLE_PROPS.getChild("view").getChild("expandAll"));
        tta.setExpandAll(pv.booleanValue());
    	return tta;
    }

    protected void init() {
        PropertyValue pv = getPropertyValue(
                TREE_TABLE_PROPS.getChild("treeTitle"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            treeNameUID = (String)p.first;
            treeName = (String)p.second;
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
        pv = getPropertyValue(TREE_TABLE_PROPS.getChild("pos").getChild("treeWidth"));
        if (!pv.isNull() && pv.intValue() != 0) {
            treeWidth = pv.intValue();
        } else {
            treeWidth = Constants.DEFAULT_PREF_WIDTH;
        }
        pv = getPropertyValue(TREE_TABLE_PROPS.getChild("view").getChild("viewType"));
        if (!pv.isNull()) {
            viewType = pv.intValue();
        } else {
            viewType = ((EnumValue)TREE_TABLE_PROPS.getChild("view").getChild("viewType").getDefaultValue()).code;
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
        return TREE_TABLE_PROPS;
    }

    public void setLangId(long langId) {
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
        tc.setHeaderValue(treeName);
        if (tree != null) tree.setLangId(langId);
    }

    private class DefaultOrTreeTableModel extends DefaultOrTableModel {
        private boolean movingBack = false;

        public void addColumn(OrTableColumn col) {
            col.addPropertyChangeListener(this);
            columns.add(col);
            JTable tb = getJTable();
            col.setModelIndex(columns.size());
            TableColumn column = new TableColumn(columns.size(),
                    col.getPreferredWidth());
            if (mode != Mode.DESIGN) {
                //updateRenderers(column);
            }
            tb.getColumnModel().addColumn(column);
            column.setMaxWidth(col.getMaxWidth());
            column.setMinWidth(col.getMinWidth());
            fireTableStructureChanged();
        }

        public void removeColumn(OrTableColumn col) {
            if (col == null) return;
            col.removePropertyChangeListener(this);
            int idx = -1;
            for (int i = 0; i < columns.size(); i++) {
                OrTableColumn removingColumn = (OrTableColumn)columns.get(i);
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
                }
                else {
                    fireTableStructureChanged();
                }
                return;
            }
            OrTableColumn tc = (OrTableColumn) columns.get(startIndex-1);
            kz.tamur.rt.Utils.moveListElementTo(columns, startIndex-1, endIndex-1);
            Element elem = xml.getChild("columns");
            List list = elem.getChildren();
            kz.tamur.rt.Utils.moveListElementTo(list, startIndex-1, endIndex-1);
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
            OrTableColumn col = getColumn(column - 1);
            return col == null ? null : col.getTitle();
        }

        public OrTableColumn getColumn(int colIndex) {
            if (colIndex == 0) return null; //edit
            return (OrTableColumn)columns.get(colIndex - 1);
        }

        public void setInterfaceLangId(int langId) {
            for (int i = 0; i < columns.size(); i++) {
                OrTableColumn column = (OrTableColumn)columns.get(i);
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
                    OrTableColumn column = (OrTableColumn) columns.get(i-1);
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
            if (mode == Mode.DESIGN) {
                int fromIdx = e.getFromIndex();
                int toIdx = e.getToIndex();
                if (fromIdx != toIdx) {
                    DefaultOrTableModel model = (DefaultOrTableModel)table.getModel();
                    model.moveColumn(fromIdx, toIdx);
                }
            }
        }

        public void columnMarginChanged(ChangeEvent e) {
            if (!isSelfChange && mode == Mode.DESIGN) {
                TableColumnModel columnModel = table.getColumnModel();
                OrTableColumn column = null;

                TableColumn tableColumn = columnModel.getColumn(0);
                if (treeWidth != tableColumn.getWidth()) {
                    setPropertyValue(new PropertyValue(tableColumn.getWidth(),
                        getProperties().getChild("pos").getChild("treeWidth")));
                    ControlTabbedContent.instance().propertyModified(column);
                }

                for (int i = 1; i < columnModel.getColumnCount(); i++) {
                    column =
                            ((DefaultOrTreeTableModel) table.getModel()).getColumn(i);
                    tableColumn = columnModel.getColumn(i);
                    if (column.getPreferredWidth() != tableColumn.getWidth()) {
                        column.setPropertyValue(new PropertyValue(tableColumn.getWidth(),
                                column.getProperties().getChild("width").getChild("pref")));
                        ((DefaultOrTreeTableModel)table.getModel()).fireTableStructureChanged();
                        ControlTabbedContent.instance().propertyModified(column);
                    }
                }
            }
        }
    }


    protected void updateHeaderRenderers() {
        OrTableModel model = (OrTableModel) table.getModel();
        TableColumn tc = table.getColumnModel().getColumn(0);
        tc.setHeaderValue(treeName);
        tc.setHeaderRenderer(createRendererFirstColumn());
        DefaultTableCellRenderer rend = null;
        for (int i = 1; i < table.getColumnCount(); i++) {
            tc = table.getColumnModel().getColumn(i);
            OrTableColumn col = model.getColumn(i);
            rend = (DefaultTableCellRenderer)col.createDefaultRenderer();
            tc.setHeaderRenderer(rend);
        }
    }

    public void updateRenderers() {
        Color zebraColor1 = getZebraColor1();
        Color zebraColor2 = getZebraColor2();
        if (zebraColor1 == null)
            zebraColor1 = Color.WHITE;
        if (zebraColor2 == null)
                    zebraColor2 = Color.WHITE;
        zebra1 = zebraColor1;
        zebra2 = zebraColor2;
    }

    
    
    private class OrTreeTableColumn extends OrTableColumn {
        
        public OrTreeTableColumn(Element xml, int mode, OrFrame frame) {
            super(xml, mode, frame);
            super.setOrTable(this.getOrTable());
            setHeaderBackground(Color.RED);
        }

        public GridBagConstraints getConstraints() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public PropertyNode getProperties() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public PropertyValue getPropertyValue(PropertyNode prop) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setPropertyValue(PropertyValue value) {
        }

        public Element getXml() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getComponentStatus() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getMode() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        //
        public int getTabIndex() {
            return -1;
        }

        public String getTitle() {
            return treeName;
        }

        public TableCellRenderer getDefaultRenderer() {
            return super.getDefaultRenderer();    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void setSelected(boolean isSelected) {
        }

        @Override
        public void setAttention(boolean attention) {
        }
    }

    public TreeTableCellRenderer getTree() {
        return (TreeTableCellRenderer)tree;
    }

    public OrTree getOrTree() {
        return tree;
    }

    public String getTreeName() {
        return treeName;
    }

    public int getTreeWidth() {
        return treeWidth;
    }

    public class TreeTableCellRenderer extends OrTree implements TableCellRenderer {
        protected int visRow;
        protected int visCol;
        protected OrTreeTable table;
        private TableAdapter.RtTableModel model;

        private TreeLabel rend;
        private Font font = null; 
        
        public TreeTableCellRenderer(Element xml, int mode, OrTreeTable table) {
            super(xml, mode, table.frame);
            this.table = table;
            setOpaque(false);
            rend = new TreeLabel();
            rend.setCellHeight(table.getJTable().getRowHeight());
            this.font = font == null ? Utils.getDefaultFont() : font;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int modelIndex = table.getColumnModel().getColumn(column).getModelIndex();
            if (getPathForRow(row) == null) {
                return null;
            }
            TreeAdapter.Node node = (TreeAdapter.Node) getPathForRow(row).getLastPathComponent();
            if (modelIndex == 0) {
                return getTreeCellRenderer(table, isSelected, hasFocus, row, node);
            }
            int actIndex = node.index;
            if (actIndex != -1) {
                ColumnAdapter orc = (ColumnAdapter) model.getColumns().get(modelIndex - 1);
                Component comp = (Component) orc.getCellRenderer().getTableCellRendererComponent(table, value, isSelected,  hasFocus, row, column);
                return comp;
            } else {
                ColumnAdapter orc = (ColumnAdapter) model.getColumns().get(modelIndex - 1);
                if (orc.hasTreeAttrs()) {
                    Component comp = (Component) orc.getCellRenderer().getTableCellRendererComponent(table, value, isSelected,  hasFocus, row, column);
                    return comp;
                } else {
                    return getTreeCellRenderer(table, isSelected, hasFocus, row, node);
                }
            }
        }

        private Component getTreeCellRenderer(JTable table, boolean isSelected, boolean hasFocus, int row, TreeAdapter.Node node) {
            rend.setBackground(isSelected || table.isRowSelected(row) ? table.getSelectionBackground() : row % 2 == 0 ? zebra1
                    : zebra2);
            rend.setCellBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : null);
            if (mode == Mode.RUNTIME) {
                String iconNameExp = MainFrame.iconsSettings.get("iconNodeParentExp");
                String iconNameCol = MainFrame.iconsSettings.get("iconNodeParentCol");
                ImageIcon iconExp = getImageIconFull(iconNameExp);
                ImageIcon iconCol = getImageIconFull(iconNameCol);
                if (node.isLeaf()) {
                    rend.setIcon(null);
                } else {
                    if (this.table.getViewType() > Constants.FILES) {
                        rend.setIcon(getImageIconFull("groupNode.png"));
                    } else {
                        if (isExpanded(row)) {
                            if (iconExp == null) {
                                rend.setIcon(getImageIcon("Open"));
                            } else {
                                rend.setIcon(iconExp);
                            }
                        } else {
                            if (iconCol == null) {
                                rend.setIcon(getImageIcon("CloseFolder"));
                            } else {
                                rend.setIcon(iconCol);
                            }
                        }
                    }
                }
            } else {
                rend.setIcon(!node.getAllowsChildren() ? null : isExpanded(row) ? getImageIcon("Open")
                        : getImageIcon("CloseFolder"));
            }
            rend.setFont(font);

            final int offset = getRowBounds(row).x;
            rend.setOffset(offset);
            String title = node.toString();
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
            if (col == 0) return null;
            ColumnAdapter orc = (ColumnAdapter) model.getColumns().get(col - 1);
            return (OrCellRenderer) orc.getCellRenderer();
        }

        public JTable getTable() {
            return table.getJTable();
        }

        public TreeTableAdapter getTableAdapter() {
            return (TreeTableAdapter)table.getAdapter();
        }
    }

    public void setPropertyValue(PropertyValue value) {
        if ("treeTitle".equals(value.getProperty().getName())) {
            treeName = value.stringValue();
            ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
            repaint();
        } else if ("treeWidth".equals(value.getProperty().getName())) {
            treeWidth = value.intValue();
            ((DefaultOrTableModel) table.getModel()).fireTableStructureChanged();
        }
        super.setPropertyValue(value);
    }

    public Color getZebra1Color() {
        return zebra1;
    }

    public Color getZebra2Color() {
        return zebra2;
    }

    public TreeAdapter getTreeAdapter() {
    	return treeAdapter;
    }
    
    public class TreeTableExpansionListener implements TreeExpansionListener {

        public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
            if (!((TreeTableAdapter)adapter).isSelfExpanded()) {
                //adapter.getDataRef().fireValueChangedEvent(-1, adapter, 0);
                adapter.getModel().fireTableDataChanged();
                adapter.countCurrentTableItem();
            }
        }

        public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
            if (!((TreeTableAdapter)adapter).isSelfExpanded()) {
                //adapter.getDataRef().fireValueChangedEvent(0, adapter, 0);
                adapter.getModel().fireTableDataChanged();
                adapter.countCurrentTableItem();
            }
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
			OrRef dataRef = adapter.getDataRef();
			if (!updatingListSelectionModel) {// || !((TreeTableAdapter)adapter).isHasRows()) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					if (!listSelectionModel.isSelectionEmpty()) {
						int min = listSelectionModel.getMinSelectionIndex();
						int max = listSelectionModel.getMaxSelectionIndex();
						// clearSelection();
						if (min != -1 && max != -1) {
							for (int counter = min; counter <= max; counter++) {
								if (listSelectionModel.isSelectedIndex(counter)) {
									TreePath selPath = tree
											.getPathForRow(counter);

									if (selPath != null) {
										setSelectionPath(selPath);
									}
								}
							}
							try {
								int emptyRow = adapter.getEmptyRow();
								TreePath path = tree.getPathForRow(min);
								if (path != null) {
									TreeAdapter.Node node = (TreeAdapter.Node) path
											.getLastPathComponent();
									int actIndex = node.index;
									if (emptyRow > -1) {
										if (actIndex != -1
												&& emptyRow != actIndex) {
											int selectedCol = getJTable()
													.getSelectedColumn();
											dataRef.deleteItem(
													adapter,
													emptyRow, this);
											getJTable()
													.setColumnSelectionInterval(
															selectedCol,
															selectedCol);
										}
									}
                                                                        int selectedCol = getJTable().getSelectedColumn();
                                                                        if (selectedCol < 0) selectedCol = 0;
                                                                        getJTable().setColumnSelectionInterval(
                                                                                                    selectedCol,
                                                                                                    selectedCol);
                                                                        getJTable()
                                                                                    .setRowSelectionInterval(
                                                                                                    min, max);

									if (actIndex != -1) {
										dataRef
												.setSelectedItems(new int[]{actIndex});
										dataRef.absolute(actIndex,
												adapter);
									} else {
										dataRef.setSelectedItems(new int[0]);
										dataRef.absolute(-1,
												adapter);
									}
								}
							} catch (KrnException ex) {
								ex.printStackTrace();
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
/*
			} else {
				try {
					dataRef.setSelectedItems(new int[0]);
					dataRef.absolute(-1, adapter);
				} catch (KrnException e) {
					e.printStackTrace();
				}
*/
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
            setFont(kz.tamur.rt.Utils.getDefaultFont());
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
                    }
                    if (getMode() == Mode.DESIGN && hasFocus) {
                        setBackground(Color.WHITE);
                    }
                }
                setText(treeName);
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };
        return label;
    }

    /**
     * @return the onlyLeaf
     */
    public boolean isOnlyLeaf() {
        return onlyLeaf;
    }
    
    public int getViewType() {
        return viewType;
    }  
    
	public boolean isShowSearchLine() {
		return tree.isShowSearchLine();
	}
}
