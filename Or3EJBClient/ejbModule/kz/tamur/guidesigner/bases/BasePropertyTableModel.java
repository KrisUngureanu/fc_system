package kz.tamur.guidesigner.bases;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 14:24:16
 * To change this template use File | Settings | File Templates.
 */
public class BasePropertyTableModel extends AbstractTableModel {
    static protected String[] cNames = {"Свойство", "Значение"};
    //static protected Class[]  cTypes = {TreeTableModel.class, Object.class};

    private BaseNode node;
    private List data = new ArrayList();
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);
    private boolean canEdit = true;

    public BasePropertyTableModel() {
        super();
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.size() > 0) {
            NodeProperty np = (NodeProperty) data.get(rowIndex);
            if (columnIndex == 0) {
                return np.propName;
            } else if (columnIndex == 1) {
                return np.value;
            }
        }
        return null;
    }

    public void setNode(BaseNode node) {
        this.node = node;
        data.clear();
        NodeProperty np = new NodeProperty("Наименование", node.toString());
        data.add(np);
        np = new NodeProperty("Флаг", new Long(node.getFlags()));
        data.add(np);
        np = new NodeProperty("Уровень", new Integer(node.getLevel()));
        data.add(np);
        np = new NodeProperty("Физически раздельная?", new Boolean(node.isPhysical()));
        data.add(np);
        fireTableStructureChanged();
    }

    public int getColumnCount() {
        return cNames.length;
    }

    public String getColumnName(int column) {
        return cNames[column];
    }


    //public Class getColumnClass(int column) {
    //    return cTypes[column];
    //}

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!canEdit) return;
        NodeProperty np = (NodeProperty) data.get(rowIndex);
        if (columnIndex == 1) {
            np.value = aValue;
            String name = "modified";
            switch (rowIndex) {
                case 0:
                    name = "name";
                    node.rename(aValue.toString());
                    ps.firePropertyChange(name, node, null);
                    break;
                case 1:
                    name = "flags";
                    node.setFlags((Integer)aValue);
                    ps.firePropertyChange(name, node, null);
                    break;
                case 2:
                    name = "level";
                    node.setLevel((Integer)aValue);
                    ps.firePropertyChange(name, node, null);
                    break;
                case 3:
                    name = "isPhysical";
                    node.setAsPhysical((Boolean)aValue);
                    ps.firePropertyChange(name, node, null);
                    break;
            }
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    class NodeProperty extends Object {
        String propName;
        Object value;

        public NodeProperty(String propName, Object value) {
            this.propName = propName;
            this.value = value;
        }

    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
            ps.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
            ps.removePropertyChangeListener(l);
    }

    public BaseNode getNode() {
        return node;
    }

}

