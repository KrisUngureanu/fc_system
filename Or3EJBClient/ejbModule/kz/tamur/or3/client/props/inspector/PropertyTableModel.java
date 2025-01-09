package kz.tamur.or3.client.props.inspector;

import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.InterfaceActionsConteiner;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.service.ServiceItem;
import kz.tamur.guidesigner.service.ui.ActivityStateNode;
import kz.tamur.guidesigner.service.ui.DecisionStateNode;
import kz.tamur.guidesigner.service.ui.ProcessStateNode;
import kz.tamur.or3.client.props.FolderProperty;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import other.treetable.TreeTableModel;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import java.util.ArrayList;
import java.util.List;

public class PropertyTableModel implements TreeTableModel {
	
	protected EventListenerList listenerList = new EventListenerList();
	protected Property root = new FolderProperty(null, null,"Свойства не доступны");
	protected List<Inspectable> objs = new ArrayList<Inspectable>();
	protected boolean plainMode = false;
	// StateNode name of the PropertyTable
	private String state_title;

	public PropertyTableModel(Inspectable obj) {
		objs.add(obj);
		if (obj != null) {
			this.root = obj.getProperties();
		}
	}
	
	public void addObject(Inspectable obj) {
		objs.add(obj);
	}

	public void setObject(Inspectable obj) {
		objs.clear();
		objs.add(obj);
		state_title = null;
		if(obj instanceof ServiceItem) {
			Object item = ((ServiceItem)obj).getItem();
			if(item instanceof ActivityStateNode)
				state_title = "Действие";
			else if(item instanceof ProcessStateNode)
				state_title = "Процесс";
			else if(item instanceof DecisionStateNode)
				state_title = "Условие";
		}
		root.removeAllChildren();
		if (obj != null && obj.getProperties()!=null) {
			if (obj.getProperties().getNode() != null)
				obj.getProperties().getNode().setPlainMode(plainMode);
			if (plainMode) {
				List<Property> children = new ArrayList<Property>();
				getAllChildren(obj.getProperties(), children);
				root.addChildren(children);
			} else {
				root.addChildren(obj.getProperties().getChildren());
			}
		}
		fireTreeStructureChanged(this, new Object[] {root}, null, null);
	}
	
    /**
     * Обновление объекта в модели Если объект необходимого типа, то его карта свойств перечитывается
     */
    public void updateObject(Inspectable obj) {
		objs.clear();
		objs.add(obj);
        root.removeAllChildren();
        if (obj != null) {
            if (plainMode) {
                List<Property> children = new ArrayList<Property>();
                getAllChildren(obj.getNewProperties(), children);
                root.addChildren(children);
            } else {
                root.addChildren(obj.getProperties().getChildren());
            }
        }
        fireTreeStructureChanged(this, new Object[] { root }, null, null);
    }
	
	public void setPlainMode(boolean plainMode) {
		if (this.plainMode != plainMode) {
			this.plainMode = plainMode;
			setObject(objs.get(0));
		}
	}

	public Class getColumnClass(int column) {
		if (column == 0) {
			return TreeTableModel.class;
		}
		return Object.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Свойство";
		case 1:
			return "Значение";
		default:
			return null;
		}
	}

	public Object getValueAt(Object node, int column) {
		switch (column) {
		case 0:
			return node;
		case 1:
			Object obj = objs.get(0) != null ? objs.get(0).getValue((Property)node) : null;
			if(state_title != null)
			if(state_title.equals("Действие") || state_title.equals("Процесс") || state_title.equals("Условие")) {
				String s = state_title + ": " + ((Property)node).getTtitle();
				ExprEditorObject eobj = new ExprEditorObject(obj, s);
				return (Object)eobj;
			}
			return obj;
		default:
			return null;
		}
	}

	public boolean isCellEditable(Object node, int column) {
		return true;
	}

    public void setValueAt(Object value, Object node, int column) {
        if (node != null && column == 1) {
            Property property = (Property) node;
            for(int i = 0; i < objs.size(); i++) {
	            Object oldValue = objs.get(i).getValue(property);
	            if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue)) || value instanceof ReportRecord) {
	            	objs.get(i).setValue(property, value, oldValue);
	                if (objs.get(i) instanceof GuiComponentItem) {
	                    OrGuiComponent component = (OrGuiComponent) ((GuiComponentItem) objs.get(i)).getItem();
	                    long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
	                    String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
	                    String propertyID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getProperties().put(propertyID, property);
	                    String oldValueID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getValues().put(oldValueID, oldValue);
	                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).propertyChanged(componentID, component, propertyID, property, oldValueID, oldValue, value);
	                }
	            }
            }
        }
    }

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
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

	public boolean isLeaf(Object node) {
		if (node instanceof Property) {
			return !((Property) node).hasChildren();
		}
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				if (e == null)
					e = new TreeModelEvent(source, path, childIndices, children);
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}
	
    private void getAllChildren(Property prop, List<Property> children) {
        if (!(prop instanceof FolderProperty)) {
            children.add(prop);
        } else {
	        for (Property child : prop.getChildren()) {
	            getAllChildren(child, children);
	        }
        }
    }
    
    public Property getSelectedNode(int index) {
    	List<Property> children = root.getChildren();
    	return children.get(index);
    }
    
    public List<Property> getChildren() {
    	return root.getChildren();
    }
}