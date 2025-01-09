package kz.tamur.guidesigner.boxes;


import com.cifs.or2.kernel.KrnObject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 16:31:56
 * To change this template use File | Settings | File Templates.
 */
public class BoxPropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	static protected String[] cNames = {"Свойство", "Значение"};
    //static protected Class[]  cTypes = {TreeTableModel.class, Object.class};

    private BoxNode node;
    private List<NodeProperty> data = new ArrayList<NodeProperty>();
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);
    private boolean canEdit = true;

    public BoxPropertyTableModel() {
        super();
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.size() > 0) {
            BoxPropertyTableModel.NodeProperty np = data.get(rowIndex);
            if (columnIndex == 0) {
                return np.propName;
            } else if (columnIndex == 1) {
                return np.value;
            }
        }
        return null;
    }

    public void setNode(BoxNode node) {
        this.node = node;
        data.clear();
        if (node.isLeaf()) {
            NodeProperty np = new NodeProperty("Наименование", node.toString());
            data.add(np);
            np = new NodeProperty("База данных", node.getBaseStructureObj());
            data.add(np);
            np = new NodeProperty("UrlIn", node.getUrlIn());
            data.add(np);
            np = new NodeProperty("UrlOut", node.getUrlOut());
            data.add(np);
            np = new NodeProperty("PathIn", node.getPathIn());
            data.add(np);
            np = new NodeProperty("PathOut", node.getPathOut());
            data.add(np);
            np = new NodeProperty("PathTypeIn", node.getPathTypeIn());
            data.add(np);
            np = new NodeProperty("PathTypeOut", node.getPathTypeOut());
            data.add(np);
            np = new NodeProperty("PathInit", node.getPathInit());
            data.add(np);
            np = new NodeProperty("CharSet", node.getCharSet());
            data.add(np);
            np = new NodeProperty("Config", node.getConfig());
            data.add(np);
            np = new NodeProperty("Тип транспорта", ""+node.getTransport());
            data.add(np);
        } else {
            NodeProperty np = new NodeProperty("Имя группы", node.toString());
            data.add(np);
            np = new NodeProperty("База данных", node.getBaseStructureObj());
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
        if (columnIndex == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!canEdit) return;
        NodeProperty np = data.get(rowIndex);
        if (columnIndex == 1) {
            np.value = aValue;
            String name = "modified";
            switch (rowIndex) {
                case 0:
                    name = "name";
                    if (!aValue.toString().equals(node.getName())) {
                    	node.rename(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 1:
                    name = "base";
                    np.value = aValue;
                    if (aValue instanceof KrnObject) {
                    	if (!(node.getBaseStructureObj() instanceof KrnObject &&
                    			((KrnObject)aValue).id == ((KrnObject)node.getBaseStructureObj()).id)) {
                    		node.setBase((KrnObject)aValue);
                    		ps.firePropertyChange(name, node, null);
                    	}
                    } else {
                    	if (node.getBaseStructureObj() instanceof KrnObject) {
                    		node.setBase(null);
                    		ps.firePropertyChange(name, node, null);
                    	}
                    }
                    break;
                case 2:
                    name = "urlIn";
                    if (!aValue.toString().equals(node.getUrlIn())) {
                    	node.setUrlIn(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 3:
                    name = "urlOut";
                    if (!aValue.toString().equals(node.getUrlOut())) {
                    	node.setUrlOut(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 4:
                    name = "xpathIn";
                    if (!aValue.toString().equals(node.getPathIn())) {
                    	node.setPathIn(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 5:
                    name = "xpathOut";
                    if (!aValue.toString().equals(node.getPathOut())) {
                    	node.setPathOut(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 6:
                    name = "xpathTypeIn";
                    if (!aValue.toString().equals(node.getPathTypeIn())) {
                    	node.setPathTypeIn(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 7:
                    name = "xpathTypeOut";
                    if (!aValue.toString().equals(node.getPathTypeOut())) {
                    	node.setPathTypeOut(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 8:
                    name = "xpathInit";
                    if (!aValue.toString().equals(node.getPathInit())) {
                    	node.setPathInit(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 9:
                    name = "charSet";
                    if (!aValue.toString().equals(node.getCharSet())) {
                    	node.setCharSet(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
                case 10:
                    name = "config";
                    if (aValue instanceof byte[]) {
                    	if (!(node.getConfig() instanceof byte[] &&
                    			(byte[])aValue == (byte[])node.getConfig())) {
                    		node.setConfig((byte[])aValue);
                    		ps.firePropertyChange(name, node, null);
                    	}
                    } else {
                    	if (node.getConfig() instanceof byte[]) {
                    		node.setConfig(null);
                    		ps.firePropertyChange(name, node, null);
                    	}
                    }
                    break;
                case 11:
                    name = "transport";
                    if (!aValue.toString().equals(node.getTransport())) {
                    	node.setTransport(aValue.toString());
                    	ps.firePropertyChange(name, node, null);
                    }
                    break;
            }
        }
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public class NodeProperty extends Object {
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

    public BoxNode getNode() {
        return node;
    }

}

