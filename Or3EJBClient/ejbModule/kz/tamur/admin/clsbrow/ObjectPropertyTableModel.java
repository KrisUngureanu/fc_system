package kz.tamur.admin.clsbrow;

import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.or3.client.props.Property;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ObjectPropertyTableModel extends AbstractTableModel {

    protected EventListenerList listenerList = new EventListenerList();
    protected ObjectProperty root = new FolderObjectProperty(null, null);
    protected ObjectInspectable obj;

    public ObjectPropertyTableModel(ObjectInspectable obj) {
        this.obj = obj;
        if (obj != null) {
            this.root = obj.getObjectProperties();
        }
    }

    public void setObject(ObjectInspectable obj) {
        this.obj = obj;
        root.removeAllChildren();
        if (obj != null) {
            List<ObjectProperty> children = new ArrayList<ObjectProperty>();
            getAllChildren(obj.getObjectProperties(), children);
            root.addChildren(children);
        }
        this.fireTableDataChanged();
    }

    public Class getColumnClass(int column) {
        if (column < 3) {
            return AbstractTableModel.class;
        }
        return Object.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object res = "";
        ObjectProperty op = root.getChildren().get(rowIndex);
        if (columnIndex == 0) {
            res = op.getId();
        } else if (columnIndex == 1) {
            res = op;
        } else if (columnIndex == 2) {
            res = op.getType();
        } else if (columnIndex == 3)
            res = obj.getValue(op);

        return res;
    }

    public int getRowCount() {
        return root.getChildren().size(); // To change body of implemented methods use File | Settings | File Templates.
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Идентификатор";
        case 1:
            return "Наименование";
        case 2:
            return "Тип";
        case 3:
            return "Значение";
        default:
            return null;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    public void setValueAt(Object value, int row, int column) {
        if (column == 3) {
            ObjectProperty prop = root.getChildren().get(row);
            Object oldValue = obj.getValue(prop);
            if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue))
                    || value instanceof ReportRecord) {
                obj.setValue(prop, value);
            }
        }
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof Property) {
            return ((Property) parent).getChildren().get(index);
        }
        return null;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof Property) {
            return ((Property) parent).getChildren().size();
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof Property) {
            return ((Property) parent).getChildren().indexOf(child);
        }
        return 0;
    }

    public Object getRoot() {
        return root;
    }

    private void getAllChildren(ObjectProperty prop, List<ObjectProperty> children) {
        if (!(prop instanceof FolderObjectProperty)) {
            children.add(prop);
        } else {
            for (ObjectProperty child : prop.getChildren()) {
                getAllChildren(child, children);
            }
        }
    }

    public void sortData(int sortColumn, boolean isAsc) {
        Collections.sort(root.getChildren(), new TaskComparator(sortColumn, isAsc));
    }

    class TaskComparator implements Comparator {

        protected int sortColumn;
        protected boolean isSortAsc;

        public TaskComparator(int sortColumn, boolean sortAsc) {
            this.sortColumn = sortColumn;
            isSortAsc = sortAsc;
        }

        public int compare(Object o1, Object o2) {
            int res = 0;
            if ((o1 instanceof ObjectProperty) && (o2 instanceof ObjectProperty)) {
                ObjectProperty a1 = (ObjectProperty) o1;
                ObjectProperty a2 = (ObjectProperty) o2;
                switch (sortColumn) {
                case 0:
                    res = Integer.valueOf(a1.getId()).compareTo(Integer.valueOf(a2.getId()));
                    break;
                case 1:
                    res = a1.getTtitle().compareTo(a2.getTtitle());
                    break;
                case 2:
                    res = a1.getType().compareTo(a2.getType());
                    break;
                }
                if (!isSortAsc) {
                    res = -res;
                }
            }
            return res;
        }
    }
}
