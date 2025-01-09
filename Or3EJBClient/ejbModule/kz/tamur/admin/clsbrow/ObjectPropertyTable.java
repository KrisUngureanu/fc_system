package kz.tamur.admin.clsbrow;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import kz.tamur.rt.Utils;

public class ObjectPropertyTable extends JTable {

    private ObjectInspectable obj;
    private ObjectPropertyTableModel model = new ObjectPropertyTableModel(null);
    private static final ImageIcon sortUp = kz.tamur.rt.Utils.getImageIcon("SortUpLight");
    private static final ImageIcon sortDown = kz.tamur.rt.Utils.getImageIcon("SortDownLight");
    private int columnModelIndex = 1;
    private boolean isAsc = true;

    private ObjectPropertyEditor editor;
    private Map<String, ObjectEditorDelegate> editorDelegates = new HashMap<String, ObjectEditorDelegate>();

    private ObjectPropertyRenderer renderer;
    private Map<String, ObjectRendererDelegate> rendererDelegates = new HashMap<String, ObjectRendererDelegate>();

    public ObjectPropertyTable() {
        setFont(Utils.getDefaultFont());
        setModel(model);
        JTableHeader header = getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            column.setHeaderRenderer(createHeader());
        }

        editor = new ObjectPropertyEditor(this);
        renderer = new ObjectPropertyRenderer(this);
    }
    
    public void setNull() {
        model = new ObjectPropertyTableModel(null);
        setFont(Utils.getDefaultFont());
        setModel(model);
        JTableHeader header = getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new ColumnListener());
        header.setReorderingAllowed(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            column.setHeaderRenderer(createHeader());
        }

        editor = new ObjectPropertyEditor(this);
        renderer = new ObjectPropertyRenderer(this);
    }
    public ObjectInspectable getObject() {
        return obj;
    }

    public void setObject(ObjectInspectable obj) {
        this.obj = obj;
        editor.cancelCellEditing();
        model.setObject(obj);
        JLabel renderer = (JLabel) getColumnModel().getColumn(columnModelIndex).getHeaderRenderer();
        renderer.setIcon(isAsc ? sortUp : sortDown);
        model.sortData(columnModelIndex, isAsc);
        tableChanged(new TableModelEvent(model));
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 3) {
            ObjectProperty prop = (ObjectProperty) getModel().getValueAt(row, 1);
            ObjectEditorDelegate delegate = getEditorDelegate(prop);
            editor.setDelegate(delegate);
            return editor;
        }
        return super.getCellEditor(row, column);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 3) {
            ObjectProperty prop = (ObjectProperty) getModel().getValueAt(row, 1);
            ObjectRendererDelegate delegate = getRendererDelegate(prop);
            renderer.setDelegate(delegate);
            return renderer;
        }
        return super.getCellRenderer(row, column);
    }

    private ObjectEditorDelegate getEditorDelegate(ObjectProperty prop) {
        String propId = prop.getId();
        if (propId != null) {
            if (!editorDelegates.containsKey(propId)) {
                editorDelegates.put(propId, prop.createEditorDelegate(this));
            }
            return editorDelegates.get(propId);
        }
        return null;
    }

    private ObjectRendererDelegate getRendererDelegate(ObjectProperty prop) {
        String propId = prop.getId();
        if (propId != null) {
            if (!rendererDelegates.containsKey(propId)) {
                rendererDelegates.put(propId, prop.createRendererDelegate(this));
            }
            return rendererDelegates.get(propId);
        }
        return null;
    }

    public ObjectPropertyTableModel getObjectPropertyTableModel() {
        return model;
    }

    /**
     * Рендер заголовка таблицы просмотра атибутов объектов класса
     *
     * @return table cell renderer
     */
    private TableCellRenderer createHeader() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(Utils.getLightSysColor());
                        setBackground(Utils.getDarkShadowSysColor());
                        setFont(Utils.getDefaultFont());
                        setBorder(BorderFactory.createEtchedBorder());
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                setText(value.toString());
                repaint();
                return this;
            }
        };
    }

    class ColumnListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = getColumnModel();
            columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
            if (modelIndex < 0) {
                return;
            }
            isAsc = true;
            for (int i = 0; i < model.getColumnCount() - 1; i++) {
                JLabel renderer = (JLabel) getColumnModel().getColumn(i).getHeaderRenderer();
                if (i == columnModelIndex) {
                    Icon icon = renderer.getIcon();
                    
                    if (icon == null || icon.equals(sortDown)) {
                        renderer.setIcon(sortUp);
                    } else {
                        renderer.setIcon(sortDown);
                        isAsc = false;
                    }
                } else {
                    renderer.setIcon(null);
                }
            }
            model.sortData(modelIndex, isAsc);
            getTableHeader().repaint();
            tableChanged(new TableModelEvent(model));
        }
    }
}
