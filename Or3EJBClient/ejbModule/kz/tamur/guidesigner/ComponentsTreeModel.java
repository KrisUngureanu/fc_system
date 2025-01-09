package kz.tamur.guidesigner;


import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;

import java.util.*;
import java.util.List;
import java.awt.*;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 15.05.2004
 * Time: 12:49:19
 * To change this template use File | Settings | File Templates.
 */
public class ComponentsTreeModel implements TreeModel, PropertyListener {

    private OrGuiComponent comp;

    EventListenerList listenerList = new EventListenerList();


    public ComponentsTreeModel(OrGuiComponent comp) {
        this.comp = comp;
    }

    public void setRoot(OrGuiComponent comp) {
        this.comp = comp;
        fireComponentsTreeModelChanged(this);
    }

    private List getListComponents(Component[] comps) {
        List l = new ArrayList();
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            if ((comp instanceof OrGuiComponent) && !(comp instanceof EmptyPlace)) {
                l.add(comp);
            }
        }
        return l;
    }

    private List getChildren(OrGuiComponent comp) {
        if (comp instanceof OrPanel) {
            Component[] c = ((OrPanel) comp).getComponents();
            List l = getListComponents(c);
            return l;
        } else if (comp instanceof OrTabbedPane) {
            Component[] c = ((OrTabbedPane) comp).getComponents();
            List l = getListComponents(c);
            return l;
        } else if (comp instanceof OrScrollPane) {
            Component[] c = ((OrScrollPane) comp).getViewport().getComponents();
            List l = getListComponents(c);
            return l;
        } else if (comp instanceof OrSplitPane) {
            Component c = ((OrSplitPane) comp).getLeftComponent();
            List l = new ArrayList();
            if (c != null && !(c instanceof EmptyPlace)) {
                l.add(c);
            }
            Component c1 = ((OrSplitPane) comp).getRightComponent();
            if (c1 != null && !(c1 instanceof EmptyPlace)) {
                l.add(c1);
            }
            return l;
        } else if (comp instanceof OrLayoutPane) {
            Component[] c = ((OrLayoutPane) comp).getComponents();
            List l = getListComponents(c);
            return l;
        } else if (comp instanceof OrTreeTable) {
            OrTableModel m = (OrTableModel) ((OrTreeTable) comp).getJTable().getModel();
            List l = new ArrayList();
            for (int i = 1; i < m.getColumnCount(); i++) {
                l.add(m.getColumn(i));
            }
            if (((OrTreeTable) comp).getAddPan() != null) {
                l.add(((OrTreeTable) comp).getAddPan());
            }
            return l;
        } else if (comp instanceof OrTreeTable2) {
            OrTableModel m = (OrTableModel) ((OrTreeTable2) comp).getJTable().getModel();
            List l = new ArrayList();
            for (int i = 1; i < m.getColumnCount(); i++) {
                l.add(m.getColumn(i));
            }
            if (((OrTreeTable2) comp).getAddPan() != null) {
                l.add(((OrTreeTable2) comp).getAddPan());
            }
            return l;
        } else if (comp instanceof OrTable) {
            OrTableModel m = (OrTableModel) ((OrTable) comp).getJTable().getModel();
            List l = new ArrayList();
            for (int i = 0; i < m.getColumnCount(); i++) {
                l.add(m.getColumn(i));
            }
            if (((OrTable) comp).getAddPan() != null) {
                l.add(((OrTable) comp).getAddPan());
            }
            return l;
        } else if (comp instanceof OrPopUpPanel) {
            List<OrPanel> l = new ArrayList<OrPanel>();
            l.add(((OrPopUpPanel) comp).getMainPanel());
            return l;
        } else if (comp instanceof OrCollapsiblePanel) {
            List<OrPanel> l = new ArrayList<OrPanel>();
            l.add(((OrCollapsiblePanel) comp).getContent());
            return l;
        } else if (comp instanceof OrAccordion) {
            List<OrPanel> l = ((OrAccordion) comp).getContent();
            return l == null ? Collections.EMPTY_LIST : l;
        }
        return Collections.EMPTY_LIST;
    }

    public TreePath getPathToRoot(OrGuiComponent c) {
        OrGuiComponent root = (OrGuiComponent)getRoot();
        Stack currPath = new Stack();
        currPath.push(root);
        if (find(c, currPath)) {
            TreePath tp = new TreePath(currPath.toArray(new OrGuiComponent[currPath.size()]));
            return tp;
        }
        return null;
    }

    private boolean find(OrGuiComponent c, Stack path) {
        OrGuiComponent comp = (OrGuiComponent)path.peek();
        if (comp == c) {
            return true;
        } else {
            List children = getChildren(comp);
            for (int i = 0; i < children.size(); i++) {
                OrGuiComponent child = (OrGuiComponent) children.get(i);
                path.push(child);
                if (find(c, path)) {
                    return true;
                }
                path.pop();
            }
            return false;
        }
    }

    public OrGuiComponent find(String title) {
        Stack path = new Stack();
        OrGuiComponent root = (OrGuiComponent)getRoot();
        path.push(root);
        return find(title, path);
    }

    private OrGuiComponent find(String title, Stack path) {
        OrGuiComponent comp = (OrGuiComponent)path.peek();
        PropertyNode pn = comp.getProperties().getChild("title");
        if (pn != null) {
            PropertyValue pv = comp.getPropertyValue(pn);
            if (!pv.isNull()) {
                if (pv.stringValue().startsWith(title)) {
                    return comp;
                } else {
                    List children = getChildren(comp);
                    if (children.size() > 0) {
                        for (int i = 0; i < children.size(); i++) {
                            OrGuiComponent child = (OrGuiComponent) children.get(i);
                            path.push(child);
                            return find(title, path);
                        }
                    } else {
                        path.pop();
                        return find(title, path);
                    }
                }
            }
        }
        return null;
    }

    public Object getRoot() {
        return comp;
    }

    public Object getChild(Object parent, int index) {
        OrGuiComponent e = (OrGuiComponent)parent;
        return getChildren(e).get(index);
    }

    public int getChildCount(Object parent) {
        OrGuiComponent e = (OrGuiComponent)parent;
        return getChildren(e).size();
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    public int getIndexOfChild(Object parent, Object child) {
        List l = getChildren((OrGuiComponent)parent);
        if (l.size() > 0) {
            return l.indexOf(child);
        } else {
            return 0;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public void fireComponentsTreeModelChanged(Object source) {
        TreeModelEvent e = null;
        if (comp != null && source instanceof OrGuiComponent) {
            TreePath tp = getPathToRoot((OrGuiComponent)source);
            e = new TreeModelEvent(source, tp);//new TreePath(new Object[]{comp}));
        } else {
            e = new TreeModelEvent(source, new TreePath(new String("Отсутствует")));
        }
        Object[] list = listenerList.getListeners(TreeModelListener.class);
        for (int i = 0; i < list.length; i++) {
            ((TreeModelListener)list[i]).treeStructureChanged(e);
        }
    }

    public void propertyModified(OrGuiComponent c) {
        fireComponentsTreeModelChanged(c);
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {

    }

}
