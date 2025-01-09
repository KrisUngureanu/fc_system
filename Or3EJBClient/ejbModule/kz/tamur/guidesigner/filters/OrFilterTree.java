package kz.tamur.guidesigner.filters;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kz.tamur.comps.*;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.rt.MainFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import kz.tamur.rt.Utils;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import org.jdom.Element;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * User: vital
 * Date: 06.12.2004
 * Time: 9:41:58
 */
public class OrFilterTree extends JTree implements ActionListener {

    private OrFilterNode root;
    private KrnObject obj;
    private TreeModel model;

    public OrFilterNode copyNode;
    public static final int BEFOR=0,AFTER=1;

    private JMenuItem addItem = new JMenu("Создать");
    private JMenuItem addItemBefor = createMenuItem("Создать перед узлом");
    private JMenuItem addItemAfter = createMenuItem("Создать внутри узла");
    private JMenuItem deleteItem = createMenuItem("Удалить");
    private JMenuItem copyItem = createMenuItem("Копировать");
    private JMenuItem cutItem = createMenuItem("Вырезать");
    private JMenuItem pasteItem = new JMenu("Вставить");
    private JMenuItem pasteItemBefor = createMenuItem("Вставить перед узлом");
    private JMenuItem pasteItemAfter = createMenuItem("Вставить внутри узла");
    private JPopupMenu pmenu = new JPopupMenu();
    private ControlTabbedContent filterTabbedContent;

    private EventListenerList listeners = new EventListenerList();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public OrFilterTree(OrFilterNode root, KrnObject obj,ControlTabbedContent filterTabbedContent) {
        this.root = root;
        this.obj = obj;
        this.filterTabbedContent=filterTabbedContent;
        model = getModel();
        model = new DefaultTreeModel(root);
        setModel(model);
        setRootVisible(true);
        addItemBefor.addActionListener(this);
        addItemAfter.addActionListener(this);
        deleteItem.addActionListener(this);
        copyItem.addActionListener(this);
        cutItem.addActionListener(this);
        pasteItemBefor.addActionListener(this);
        pasteItemAfter.addActionListener(this);
        pmenu.add(addItem);
        addItem.add(addItemBefor);
        addItem.add(addItemAfter);
        pmenu.addSeparator();
        pmenu.add(deleteItem);
        pmenu.add(copyItem);
        pmenu.add(cutItem);
        pmenu.add(pasteItem);
        pasteItem.add(pasteItemBefor);
        pasteItem.add(pasteItemAfter);
        addMouseListener(new PopupListener());
        if(isOpaque) {
            setBackground(Utils.getLightSysColor());
        }
        root.setNodeTitle(null);
        setCellRenderer(new CellRenderer());
        //
        addItem.setFont(Utils.getDefaultFont());
        addItem.setBackground(pmenu.getBackground());
        addItem.setForeground(pmenu.getForeground());
        pasteItem.setFont(Utils.getDefaultFont());
        pasteItem.setBackground(pmenu.getBackground());
        pasteItem.setForeground(pmenu.getForeground());
        addItem.setIcon(kz.tamur.rt.Utils.getImageIcon("Create"));
        pasteItem.setIcon(kz.tamur.rt.Utils.getImageIcon("Paste"));
        //
        setOpaque(isOpaque);
    }

    public OrFilterNode getSelectedFilterNode() {
        Object o = getSelectionPath().getLastPathComponent();
        if (o != null) {
            return (OrFilterNode)o;
        }
        return null;
    }

    public void delete() {
        OrFilterNode node = getSelectedFilterNode();
        OrFilterNode parent = (OrFilterNode)node.getParent();
        if (parent != null) {
            int res = MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Удалить узел \"" + node.toString() + "\"?");
            if (res == ButtonsFactory.BUTTON_YES) {
                int index=parent.getIndex(node);
                parent.removeFilterNode(node);
                OrFilterNode nodePath=parent;
                if(index>0){
                    nodePath=(OrFilterNode)parent.getChildAt(index-1);
                }
                ((DefaultTreeModel)model).nodeStructureChanged(root);
                firePropertyModified(parent);
                TreePath path= new TreePath(nodePath.getPath());
                if(!isExpanded(path))
                     fireTreeExpanded(path);
                setSelectionPath(path);
            }
        }
    }

    public void create(int befor_after) throws KrnException {
        OrFilterNode parent, selNode = getSelectedFilterNode();
        int index=-1;
        if (selNode != null) {
            if(befor_after==BEFOR && getSelectedFilterNode().getParent()!=null){
                parent = (OrFilterNode)getSelectedFilterNode().getParent();
            }else
                parent = selNode;
            OrFilterNode node = (OrFilterNode)Factories.instance().create("FilterNode", selNode.getFilterNode());
            node.setLangId(parent.getLangId());
            if(befor_after==BEFOR && parent != selNode){
                index=parent.getIndex(selNode);
                parent.insertChild(node, index);
            }else{
                parent.addChild(node, false);
            }
            ((DefaultTreeModel)model).nodesWereInserted(
                    parent, new int[] {index>-1?index:parent.getChildCount() - 1});
            firePropertyModified(parent);
            TreePath path= new TreePath(node.getPath());
            if(!isExpanded(path))
                 fireTreeExpanded(path);
            setSelectionPath(path);
        }

    }



    private class PopupListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger() && getSelectionPath()!=null) {
                if (getSelectedFilterNode().equals(root)) {
                    deleteItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    cutItem.setEnabled(false);
                    addItemBefor.setEnabled(false);
                    pasteItemBefor.setEnabled(false);
                } else {
                    deleteItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    cutItem.setEnabled(true);
                    pasteItemBefor.setEnabled(true);
                    addItemBefor.setEnabled(true);
                }
                if(copyNode!=null)
                    pasteItem.setEnabled(true);
                else
                    pasteItem.setEnabled(false);
                pmenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
    	try {
	        Object src = e.getSource();
	        if (src == addItemBefor) {
	            create(BEFOR);
	        } else if (src == addItemAfter) {
	            create(AFTER);
	        } else if (src == deleteItem) {
	            delete();
	        } else if (src == copyItem) {
	            copy();
	        } else if (src == cutItem) {
	            cut();
	        } else if (src == pasteItemBefor) {
	            paste(BEFOR);
	        } else if (src == pasteItemAfter) {
	            paste(AFTER);
	        }
    	} catch (Exception e1) {
    		e1.printStackTrace();
    	}
    }

    public void paste(int befor_after) throws KrnException {
        OrFilterNode parent, selNode = getSelectedFilterNode();
        int index=-1;
        if (selNode != null) {
            if(befor_after==BEFOR && getSelectedFilterNode().getParent()!=null){
                parent = (OrFilterNode)getSelectedFilterNode().getParent();
            }else
                parent = selNode;
            if(copyNode.isSelected()){
                if(!copyNode.equals(selNode)){
                    OrFilterNode parent_=(OrFilterNode)copyNode.getParent();
                    parent_.removeFilterNode(copyNode);
                    ((DefaultTreeModel)model).nodeStructureChanged(root);
                    firePropertyModified(parent_);
                    copyNode.setSelected(false);
                }
            }else{
                copyNode = (OrFilterNode)Factories.instance().create(
                        (Element)copyNode.getXml().clone(), Mode.DESIGN, parent.getFilterNode());
                copyNode.getXml().removeChild("parent");
            }
            if(copyNode.isSelected())
                copyNode.setSelected(false);
            else if(befor_after==BEFOR && parent != selNode){
                    index=parent.getIndex(selNode);
                    parent.insertChild(copyNode, index);
            }else{
                parent.addChild(copyNode, false);
            }
            if(index>-1 || parent.getChildCount()>0){
                ((DefaultTreeModel)model).nodesWereInserted(
                        parent, new int[] {index>-1?index:parent.getChildCount() - 1});
                copyNode.setNodeTitle(parent);
                firePropertyModified(parent);
                TreePath path= new TreePath(copyNode.getPath());
                if(!isExpanded(path))
                     fireTreeExpanded(path);
                setSelectionPath(path);
            }
            copyNode=null;
        }
    }

    public void cut() {
        copyNode = getSelectedFilterNode();
        copyNode.setSelected(true);
    }

    public void copy() {
        copyNode = getSelectedFilterNode();
        filterTabbedContent.setCopyNode(copyNode);
    }

    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified(OrGuiComponent c) {
        Object[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            if (list[i] != this) {
              ((PropertyListener)list[i]).propertyModified(c);
            }
        }
    }

    public KrnObject getObj() {
        return obj;
    }

    public OrFilterNode getRoot() {
        return root;
    }

    private class CellRenderer extends JLabel implements TreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            setOpaque(true);
            if (selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(Color.white);
            } else {
                setBackground(Utils.getLightSysColor());
                boolean exclude=((OrFilterNode)value).getPropertyValue("excludeFlr").booleanValue();
                if(exclude)		
                    setForeground(Color.red);
                else
                	setForeground(Color.black);
            }
            Font fnt = Utils.getDefaultFont();
            setFont(fnt);
            if (!leaf) {
                if (expanded) {
                    if(((OrFilterNode)value).getModeIcon()){
                       setIcon(kz.tamur.rt.Utils.getImageIcon("filterAndOpen"));
                    } else {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("filterOrOpen"));
                    }
                } else {
                    if (((OrFilterNode)value).getModeIcon()){
                        setIcon(kz.tamur.rt.Utils.getImageIcon("filterAndClose"));
                    } else {
                        setIcon(kz.tamur.rt.Utils.getImageIcon("filterOrClose"));
                    }
                }
            } else {
                setIcon(kz.tamur.rt.Utils.getImageIcon("leaf"));
            }
            setText(value.toString());
            setOpaque(selected || isOpaque);
            return this;
        }
    }

    public void setCopyNode(OrFilterNode copyNode) {
        this.copyNode = copyNode;
    }
}
