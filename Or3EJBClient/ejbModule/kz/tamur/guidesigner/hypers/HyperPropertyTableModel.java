package kz.tamur.guidesigner.hypers;

import com.cifs.or2.kernel.KrnObject;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 14:24:16
 * To change this template use File | Settings | File Templates.
 */
public class HyperPropertyTableModel extends AbstractTableModel {
    static protected String[]  cNames = {"Свойство", "Значение"};
    //static protected Class[]  cTypes = {TreeTableModel.class, Object.class};

    private HyperNode node;
    private List data = new ArrayList();
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);
    private boolean canEdit;

    public HyperPropertyTableModel(boolean canEdit) {
        super();
        this.canEdit = canEdit;
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.size() > 0) {
            NodeProperty np = (NodeProperty)data.get(rowIndex);
            if (columnIndex == 0) {
                return np.propName;
            } else if (columnIndex == 1) {
                return np.value;
            }
        }
        return null;
    }

    public void setNode(HyperNode node) {
        this.node = node;
        data.clear();
        NodeProperty np = new NodeProperty("Заголовок", node.toString());
        data.add(np);
        np = new NodeProperty("ЗаголовокКаз", node.getTitleKz());
        data.add(np);
        np = new NodeProperty("Индекс", new Long(node.getRuntimeIndex()));
        data.add(np);
        if (node.isLeaf()) {
            np = new NodeProperty("Интерфейс", node.getIfcObject());
            data.add(np);
            np = new NodeProperty("Диалог", new Boolean(node.isDialog()));
            data.add(np);
            np = new NodeProperty("Сохраняемый", new Boolean(node.isChangeable()));
            data.add(np);
        }
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
          return columnIndex == 1;
      }
/*
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            if (rowIndex != 1) {
                return true;
            } else if (node.isLeaf()) {
                if (getValueAt(2, 1) == null) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.INFORMATION_MESSAGE,
                            "Необходимо ввести интерфейс обработки...");
                }
                return getValueAt(2, 1) != null;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
*/

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!canEdit) return;
        NodeProperty np = (NodeProperty)data.get(rowIndex);
        if (columnIndex == 1) {
            np.value = aValue;
            String name = "modified";
            if (rowIndex == 0) {
                name = "title";
                node.rename(aValue.toString());
            } else if (rowIndex == 1) {
                    name = "titleKz";
                    node.renameKz(aValue.toString());
            } else if (rowIndex == 2) {
                name = "index";
                node.setRuntimeIndex((aValue != null) ?
                        ((Integer)aValue).intValue() : 0);
            } else if (rowIndex == 3) {
                name = "interface";
                np.value = aValue;
                if (aValue != null && aValue instanceof KrnObject) {
                    node.setIfcObject((KrnObject)aValue);
                } else {
                    node.setIfcObject(null);
                }
            } else if (rowIndex == 4) {
                name = "isdialog";
                np.value = aValue;
                node.setDialog((aValue != null) ?
                        ((Boolean)aValue).booleanValue() : false);
            } else if (rowIndex == 5) {
                name = "ischangeable";
                np.value = aValue;
                node.setChangeable((aValue != null) ?
                        ((Boolean)aValue).booleanValue() : false);
            }
            ps.firePropertyChange(name, node, null);
        }
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




}
