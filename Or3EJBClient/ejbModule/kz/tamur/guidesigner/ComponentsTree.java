package kz.tamur.guidesigner;

import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 17.05.2004
 * Time: 18:58:53
 * To change this template use File | Settings | File Templates.
 */
public class ComponentsTree extends JTree implements ActionListener, KeyListener, PropertyListener, TreeSelectionListener {

    private EventListenerList listeners = new EventListenerList();
    private boolean selfChange = false;
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem cutComponent = createMenuItem("Вырезать компонент", "CutIcon.png");
    private JMenuItem removeComponent = createMenuItem("Удалить компонент", "DeleteIcon.png");

    public ComponentsTree() {
        super();
        addTreeSelectionListener(this);
        addKeyListener(this);
        cutComponent.addActionListener(this);
        removeComponent.addActionListener(this);
        popupMenu.add(cutComponent);
        popupMenu.add(removeComponent);
        this.addMouseListener(new MouseAdapter() {
    		private void popupEvent(MouseEvent e) {
    			int x = e.getX();
    			int y = e.getY();
    			JTree tree = (JTree) e.getSource();
    			TreePath path = tree.getPathForLocation(x, y);
    			if (path == null)
    				return;
    			tree.setSelectionPath(path);
    			popupMenu.show(tree, x, y);
    		}
    		
    		public void mousePressed(MouseEvent e) {
    			if (e.isPopupTrigger()) {
    				popupEvent(e);
    			}
    		}
    		
    		public void mouseReleased(MouseEvent e) {
    			if (e.isPopupTrigger()) {
    				popupEvent(e);
    			}
    		}
    	});
    }

    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    protected void firePropertyModified(OrGuiComponent component, int propertyEvent) {
        Object[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            if (list[i] != this) {
                ((PropertyListener)list[i]).propertyModified(component, propertyEvent);
            }
        }
    }
    
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();
    	if (keyCode == KeyEvent.VK_DELETE) {
	        OrGuiComponent component = (OrGuiComponent) getLastSelectedPathComponent();
	        if (component != null) {
	            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	                firePropertyModified(component, PropertyListener.DELETE_EVENT);
	            }
	        }
    	} else if (keyCode == KeyEvent.VK_F3) {
            DesignerFrame.instance().searchNext();
    	} else if (keyCode == KeyEvent.VK_X && e.isControlDown()) {
    		DesignerFrame.instance().getController().cutComponent();
    	}
       
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        if (!selfChange) {
            OrGuiComponent component = (OrGuiComponent) getLastSelectedPathComponent();
            if (component != null) {
                firePropertyModified(component, PropertyListener.SELECT_EVENT);
                requestFocus();
            }
        }
    }

    public void propertyModified(OrGuiComponent component) {}
    public void propertyModified(OrGuiComponent component, PropertyNode property) {}

    public void propertyModified(OrGuiComponent component, int propertyEvent) {
        if (propertyEvent == PropertyListener.SELECT_EVENT) {
            selfChange = true;
            ComponentsTreeModel model = (ComponentsTreeModel)getModel();
            setSelectionPath(model.getPathToRoot(component));
            scrollPathToVisible(model.getPathToRoot(component));
            selfChange = false;
        }
    }

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == removeComponent) {
			DesignerFrame.instance().getController().deleteComponent();
		} else if (source == cutComponent) {
			DesignerFrame.instance().getController().cutComponent();
		}
	}
}
