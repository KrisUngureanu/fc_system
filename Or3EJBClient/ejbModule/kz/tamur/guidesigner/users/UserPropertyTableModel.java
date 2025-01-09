package kz.tamur.guidesigner.users;

import com.cifs.or2.kernel.KrnObject;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import kz.tamur.comps.Utils;
import org.jdom.Element;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 14:24:16
 * To change this template use File | Settings | File Templates.
 */
public class UserPropertyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	static protected String[] cNames = {"Свойство", "Значение"};
    //static protected Class[]  cTypes = {TreeTableModel.class, Object.class};

    private UserNode node;
    private List<NodeProperty> data = new ArrayList<NodeProperty>();
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);
    private UserPropertyInspector inspector;
    private boolean canEdit = true;

    public UserPropertyTableModel(UserPropertyInspector userPropertyInspector) {
        super();
        this.inspector = userPropertyInspector;
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.size() > 0) {
            NodeProperty np = data.get(rowIndex);
            if (columnIndex == 0) {
                return np.propName;
            } else if (columnIndex == 1) {
            	if (np.value != null)
            		return np.value;
            	else if ("ПодписьКаз".equals(np.propName) || "Подпись".equals(np.propName) || "Должность".equals(np.propName))
            		return "";
            }
        }
        return null;
    }

    public void setNode(UserNode node) {
        this.node = node;
        data.clear();
        if (node instanceof PolicyNode) {
            PolicyNode pnode = (PolicyNode) node;
            NodeProperty np = new NodeProperty("Макс срок действия пароля (дней)", pnode.getPolicyWrapper().getMaxValidPeriod());
            data.add(np);
            np = new NodeProperty("Минимальная длина пароля", pnode.getPolicyWrapper().getMinPasswordLength());
            data.add(np);
            np = new NodeProperty("Мин длина имени пользователя", pnode.getPolicyWrapper().getMinLoginLength());
            data.add(np);
            inspector.setEditorMode(UserPropertyEditor.POLICY_MODE);
            // TODO почему не полная ветка?
        } else if (node.isLeaf()) {
            NodeProperty np = new NodeProperty("Логин", node.toString());
            data.add(np);
            np = new NodeProperty("Пароль", node.getPassword());
            data.add(np);
            np = new NodeProperty("Подпись", node.getSign());
            data.add(np);
            np = new NodeProperty("ПодписьКаз", node.getSignKz());
            data.add(np);
            np = new NodeProperty("Должность", node.getDoljnost());
            data.add(np);
            np = new NodeProperty("База данных", node.getBaseStructureObj());
            data.add(np);
            np = new NodeProperty("Язык данных", node.getDataLangObj());
            data.add(np);
            np = new NodeProperty("Язык интерфейса", node.getIfcLangObj());
            data.add(np);
            np = new NodeProperty("Интерфейс", node.getIfcObject());
            data.add(np);
            np = new NodeProperty("Администратор", new Boolean(node.isAdmin()));
            data.add(np);
            np = new NodeProperty("Заблокирован", new Boolean(node.isBlocked()));
            data.add(np);
            np = new NodeProperty("Множественный вход", new Boolean(node.isBlocked()));
            data.add(np);
            inspector.setEditorMode(UserPropertyEditor.USER_MODE);
            np = new NodeProperty("Отображать монитор задач", node.isMonitor());
            data.add(np);
            np = new NodeProperty("Отображать панель инструментов в WEB", node.isToolBar());
            data.add(np);
        } else {
            NodeProperty np = new NodeProperty("Имя группы", node.toString());
            data.add(np);
            np = new NodeProperty("Пункты гиперменю", node.getHypers());
            data.add(np);
            np = new NodeProperty("Редактор НСИ", new Boolean(node.isEditor()));
            data.add(np);
            np = new NodeProperty("Доступная помощь", node.getHelp());
            data.add(np);
            np = new NodeProperty("Процесс", node.getProcess());
            data.add(np);
            np = new NodeProperty("Права OR3", node.getOr3Rights());
            data.add(np);
            np = new NodeProperty("Отображать монитор задач для группы", node.getMonitor());
            data.add(np);
            np = new NodeProperty("Отображать панель инструментов в WEB для группы", node.getToolBar());
            data.add(np);
            inspector.setEditorMode(UserPropertyEditor.GROUP_MODE);
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
            return node.isEditable();
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
            if (node instanceof PolicyNode) {
                // TODO почему не полная ветка?
                switch (rowIndex) {
                    case 0:
                        name = "maxValidPeriod";
                        if (aValue instanceof Long) {
                            PolicyNode pnode = (PolicyNode)node;
                            if (pnode.getPolicyWrapper().getMaxValidPeriod() != ((Long)aValue).longValue()) {
                                pnode.getPolicyWrapper().setMaxValidPeriod((Long)aValue);
                                ps.firePropertyChange(name, node, null);
                            }
                        }
                        break;
                    case 1:
                        name = "minPasswordLength";
                        if (aValue instanceof Long) {
                            PolicyNode pnode = (PolicyNode)node;
                            if (pnode.getPolicyWrapper().getMinPasswordLength() != ((Long)aValue).longValue()) {
                                pnode.getPolicyWrapper().setMinPasswordLength((Long)aValue);
                                ps.firePropertyChange(name, node, null);
                            }
                        }
                        break;
                    case 2:
                        name = "minLoginLength";
                        if (aValue instanceof Long) {
                            PolicyNode pnode = (PolicyNode)node;
                            if (pnode.getPolicyWrapper().getMinLoginLength() != ((Long)aValue).longValue()) {
                                pnode.getPolicyWrapper().setMinLoginLength((Long)aValue);
                                ps.firePropertyChange(name, node, null);
                            }
                        }
                        break;
                }
            } else if (node.isLeaf()) {
                int res = 0;
                String newVal = "";
                switch (rowIndex) {
                    case 0:
                        name = "name";
                        res = node.setName(aValue.toString());
                        if (res == UserNode.PROPERTY_CHANGED)
                            ps.firePropertyChange(name, node, null);
                        else if (res == UserNode.LOGIN_TOO_SHORT) {
                            PolicyNode pnode = Utils.getPolicyNode();
                            JOptionPane.showMessageDialog(inspector, "Имя пользователя не может быть меньше " +
                                    pnode.getPolicyWrapper().getMinLoginLength() + " символов",
                                    "Сообщение", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case 1:
                        name = "password";
                        String  res1 = node.setPassword(aValue.toString());
                        if (res1 == null) {
                        	ps.firePropertyChange(name, node, null);	
                        }else{
                        	if (!res1.equals("NOT_CHANGED")) {
								JOptionPane.showMessageDialog(inspector,res1, "Сообщение", JOptionPane.ERROR_MESSAGE);
							}
                        }
                        break;
                    case 2: 
                        name = "sign";
                        newVal = (aValue != null) ? aValue.toString() : "";
                        if (!newVal.equals(node.getSign())) {
                        	node.setSign(newVal);
                        	ps.firePropertyChange(name, node, null);
                        }
                        break;
                    case 3:
                        name = "sign_kz";
                        newVal = (aValue != null) ? aValue.toString() : "";
                        if (!newVal.equals(node.getSignKz())) {
                        	node.setSignKz(newVal);
                        	ps.firePropertyChange(name, node, null);
                        }                        
                        break;
                    case 4:
                        name = "doljnost";
                        newVal = (aValue != null) ? aValue.toString() : "";
                        if (!newVal.equals(node.getDoljnost())) {
                        	node.setDoljnost(newVal);
                        	ps.firePropertyChange(name, node, null);
                        }  
                        break;
                    case 5:
                        name = "database";
                        np.value = aValue;
                        if (aValue instanceof KrnObject) {
                        	if (!(node.getBaseStructureObj() instanceof KrnObject &&
                        			((KrnObject)aValue).id == ((KrnObject)node.getBaseStructureObj()).id)) {
                        		node.setBaseStructureObj((KrnObject)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getBaseStructureObj() instanceof KrnObject) {
                        		node.setBaseStructureObj(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        
                        break;
                    case 6:
                        name = "dataLang";
                        np.value = aValue;
                        if (aValue instanceof KrnObject) {
                        	if (!(node.getDataLangObj() instanceof KrnObject &&
                        			((KrnObject)aValue).id == ((KrnObject)node.getDataLangObj()).id)) {
                        		node.setDataLangObj((KrnObject)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getDataLangObj() instanceof KrnObject) {
                        		node.setDataLangObj(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 7:
                        name = "ifcLang";
                        np.value = aValue;
                        if (aValue instanceof KrnObject) {
                        	if (!(node.getIfcLangObj() instanceof KrnObject &&
                        			((KrnObject)aValue).id == ((KrnObject)node.getIfcLangObj()).id)) {
                        		node.setIfcLangObj((KrnObject)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getIfcLangObj() instanceof KrnObject) {
                        		node.setIfcLangObj(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 8:
                        name = "interface";
                        np.value = aValue;
                        if (aValue instanceof KrnObject) {
                        	if (!(node.getIfcObject() instanceof KrnObject &&
                        			((KrnObject)aValue).id == ((KrnObject)node.getIfcObject()).id)) {
                        		node.setIfcObject((KrnObject)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getIfcObject() instanceof KrnObject) {
                        		node.setIfcObject(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 9:
                        name = "admin";
                        np.value = aValue;
                        if (aValue instanceof Boolean) {
                        	if (!aValue.equals(node.isAdmin())) {
                        		node.setAdmin((Boolean)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.isAdmin()) {
                        		node.setAdmin(false);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 10:
                        name = "blocked";
                        np.value = aValue;
                        if (aValue instanceof Boolean) {
                        	if (!aValue.equals(node.isBlocked())) {
                        		node.setBlocked((Boolean)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.isBlocked()) {
                        		node.setBlocked(false);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 11:
                        name = "email";
                        newVal = (aValue != null) ? aValue.toString() : "";
                        if (!newVal.equals(node.getEmail())) {
                        	node.setEmail(newVal);
                        	ps.firePropertyChange(name, node, null);
                        }
                        break;
                }
            } else {
                switch (rowIndex) {
                    case 0:
                        name = "name";
                        if (!aValue.toString().equals(node.getName())) {
	                        node.rename(aValue.toString());
	                        ps.firePropertyChange(name, node, null);
                        }
                        break;
                    case 1:
                        name = "hypers";
                        np.value = aValue;
                        if (aValue instanceof KrnObject[]) {
                        	if (!krnObjectsEqual((KrnObject[])aValue, node.getHypers())) {
                        		node.setHypers((KrnObject[])aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getHypers() instanceof KrnObject[] && node.getHypers().length > 0) {
                        		node.setHypers(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 2:
                        name = "editor";
                        np.value = aValue;
                        if (aValue instanceof Boolean) {
                        	if (!aValue.equals(node.isEditor())) {
                        		node.setEditor((Boolean)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.isEditor()) {
                        		node.setEditor(false);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 3:
                        name = "help";
                        np.value = aValue;
                        if (aValue instanceof KrnObject[]) {
                        	if (!krnObjectsEqual((KrnObject[])aValue, node.getHelp())) {
                        		node.setHelp((KrnObject[])aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getHelp() instanceof KrnObject[]) {
                        		node.setHelp(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 4:
                        name = "process";
                        np.value = aValue;
                        if (aValue instanceof KrnObject) {
                        	if (!(node.getProcess() instanceof KrnObject &&
                        			((KrnObject)aValue).id == ((KrnObject)node.getProcess()).id)) {
                        		node.setProcess((KrnObject)aValue);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        } else {
                        	if (node.getProcess() instanceof KrnObject) {
                        		node.setProcess(null);
                        		ps.firePropertyChange(name, node, null);
                        	}
                        }
                        break;
                    case 5:
                        name = "or3rights";
                        np.value = aValue;
                        if (aValue instanceof Element) {
                        	node.setOr3Rights((Element)aValue);
                        } else if (aValue == null) {
                            node.setOr3Rights(null);
                        }
                        ps.firePropertyChange(name, node, null);
                        break;
                }
            }
        }
    }

    public boolean krnObjectsEqual(KrnObject[] objs1, KrnObject[] objs2) {
    	if (objs2 == null || objs1.length != objs2.length) return false;
    	for (int i=0; i < objs1.length; i++) {
    		if (objs1[i].id != objs2[i].id)
    			return false;
    	}
    	return true;
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

    public UserNode getNode() {
        return node;
    }

}

