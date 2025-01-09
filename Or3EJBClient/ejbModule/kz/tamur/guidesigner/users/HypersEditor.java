package kz.tamur.guidesigner.users;

import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.hypers.HyperNode;
import kz.tamur.guidesigner.hypers.HyperTree;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.LangItem;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * User: vital
 * Date: 11.12.2004
 * Time: 11:58:47
 */
public class HypersEditor extends JPanel implements ActionListener {

    private HyperTree tree;
    private HyperTree selectedTree;
    private DefaultListModel model = new DefaultListModel();
    private JButton addAllBtn =
            ButtonsFactory.createToolButton("addSingle", "", "", true);
    private JButton removeBtn =
            ButtonsFactory.createToolButton("removeSingle", "", "", true);

    private KrnObject[] oldValue;
    //private HyperTree.HyperTreeModel treeModel;
    private SelectedTreeModel selectedTreeModel;
    //private ExistTreeModel existTreeModel;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public HypersEditor() {
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        setOpaque(isOpaque);
        setPreferredSize(new Dimension(600, 400));
        tree = kz.tamur.comps.Utils.getHyperTree();
        List l = LangItem.getAll();
        long langId = 0;
        for (int i = 0; i < l.size(); i++) {
            LangItem li =  (LangItem)l.get(i);
            if ("RU".equals(li.code)) {
                langId = li.obj.id;
                break;
            }
        }
        tree.setLang(langId);
        tree.setRootVisible(false);
        HyperNode root = tree.getRoot();
/*
        existTreeModel = new ExistTreeModel(root);
        tree.setModel(existTreeModel);
*/
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        selectedTree = kz.tamur.comps.Utils.getHyperTree();
        selectedTreeModel = new SelectedTreeModel(root);
        selectedTree.setModel(selectedTreeModel);
        selectedTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        selectedTree.setRootVisible(false);

        addAllBtn.setPreferredSize(new Dimension(30, 30));
        addAllBtn.setMaximumSize(new Dimension(30, 30));
        addAllBtn.setMinimumSize(new Dimension(30, 30));
        removeBtn.setPreferredSize(new Dimension(30, 30));
        removeBtn.setMaximumSize(new Dimension(30, 30));
        removeBtn.setMinimumSize(new Dimension(30, 30));
        addAllBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        JLabel lab = Utils.createLabel("Доступные пункты");
        add(lab, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0 ,0), 0, 0));
        JScrollPane sp = new JScrollPane(tree);
        sp.setPreferredSize(new Dimension(255, 100));
        add(sp, new GridBagConstraints(0, 1, 2, 4, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0 ,0), 0, 0));
        add(addAllBtn, new GridBagConstraints(2, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 5, 0 ,5), 0, 0));
        add(removeBtn, new GridBagConstraints(2, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 5, 0 ,5), 0, 0));
        lab = Utils.createLabel("Выбранные пункты");
        add(lab, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0 ,0), 0, 0));
        sp = new JScrollPane(selectedTree);
        sp.setPreferredSize(new Dimension(255, 100));
        add(sp, new GridBagConstraints(3, 1, 2, 4, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0 ,0), 0, 0));
    }

    private void addAllChildren(List list, AbstractDesignerTreeNode node) {
        if (!node.isLeaf()) {
            list.add(node);
            for (int i = 0; i < node.getChildCount(); i++) {
                addAllChildren(list, (AbstractDesignerTreeNode)node.getChildAt(i));
            }
        } else {
            list.add(node);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addAllBtn) {
            HyperNode node = (HyperNode)tree.getSelectedNode();
            List l = Utils.getPathToRoot(new ArrayList(), node);
            l.remove(l.size() - 1);
            addAllChildren(l, node);
            for (int i = 0; i < l.size(); i++) {
                HyperNode o =  (HyperNode)l.get(i);
                o.setAdded(true);
            }
            //existTreeModel.fireTreeStructureChanged();
            selectedTreeModel.fireTreeStructureChanged();
            for (int i=0; i<tree.getRowCount(); i++) {
            	TreePath path = tree.getPathForRow(i);
            	if (tree.isExpanded(path)) {
            		try {
            			selectedTree.expandPath(path);
            		} catch (Exception tpe) {
            		}
            	}
            }
        }
        if (src == removeBtn) {
            HyperNode node = (HyperNode)selectedTree.getSelectedNode();
            setAddedRecurcive(node, false);
            removeFolder(node);
            selectedTreeModel.fireTreeStructureChanged();
            for (int i=0; i<tree.getRowCount(); i++) {
            	TreePath path = tree.getPathForRow(i);
            	if (tree.isExpanded(path)) {
            		try {
            			selectedTree.expandPath(path);
            		} catch (Exception tpe) {
            		}
            	}
            }
        }
        tree.invalidate();
        tree.repaint();
    }

    private void setAddedRecurcive(HyperNode node, boolean added) {
    	node.setAdded(added);
    	for (int i=0; i<node.getChildCount(); i++) {
    		HyperNode n = (HyperNode) node.getChildAt(i);
    		setAddedRecurcive(n, added);
    	}
    }
    
    private void removeFolder(HyperNode hyperNode) {
        HyperNode parent = (HyperNode)hyperNode.getParent();
        if (parent != null) {
            Enumeration children = parent.children();
            int count = 0;
            while(children.hasMoreElements()) {
                HyperNode child = (HyperNode)children.nextElement();
                if (child.isAdded()) {
                    count++;
                }
            }
            if (count == 0) {
                parent.setAdded(false);
            }
            removeFolder(parent);
        }
    }

    private void addSelectedItems(List list, HyperNode node) {
        if (!node.isLeaf()) {
            list.add(node);
            int count = selectedTreeModel.getChildCount(node);
            for (int i = 0; i < count; i++) {
                HyperNode n = (HyperNode)selectedTreeModel.getChild(node, i);
                addSelectedItems(list, n);
            }
        } else {
            list.add(node);
        }
    }

    public Vector getSelectedItems() {
        List l = new ArrayList();
        addSelectedItems(l, (HyperNode)selectedTreeModel.getRoot());
        l.remove(0);
        Vector res = new Vector();
        for (int i = 0; i < l.size(); i++) {
            HyperNode hyperNode = (HyperNode) l.get(i);
            res.add(new KrnObjectItem(hyperNode.getKrnObj(),hyperNode.toString()));
        }
        return res;
    }
    public KrnObject[] getSelectedValues() {
        List l = new ArrayList();
        addSelectedItems(l, (HyperNode)selectedTreeModel.getRoot());
        l.remove(0);
        List resList = new ArrayList();
        for (int i = 0; i < l.size(); i++) {
            HyperNode hyperNode = (HyperNode) l.get(i);
            resList.add(hyperNode.getKrnObj());
        }
        KrnObject[] res = new KrnObject[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            KrnObject krnObject = (KrnObject) resList.get(i);
            res[i] = krnObject;
        }
/*
        int size = model.getSize();
        if (size > 0) {
            res = new KrnObject[size];
            for (int i = 0; i < size; i++) {
                res[i] = ((HyperNode)model.getElementAt(i)).getKrnObj();
            }
        }
*/
        return res;
    }



    public KrnObject[] getOldValue() {
        return oldValue;
    }

    public void setOldValue(KrnObject[] oldValue) {
        this.oldValue = oldValue;
        if (oldValue != null && oldValue.length > 0) {
            for (int i = 0; i < oldValue.length; i++) {
                KrnObject krnObject = oldValue[i];
                HyperNode n = (HyperNode)tree.find(krnObject);
                if (n != null)
                    n.setAdded(true);
            }
            tree.repaint();
            selectedTreeModel.fireTreeStructureChanged();
        }
    }

    class SelectedTreeModel extends DefaultTreeModel {
        public SelectedTreeModel(TreeNode root) {
            super(root);
        }

        public int getChildCount(Object parent) {
            int count = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                HyperNode child = (HyperNode) children.nextElement();
                if (child.isAdded()) {
                    count++;
                }
            }
            return count;
        }

        public Object getChild(Object parent, int index) {
            int pos = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            HyperNode child = null;
            while (pos <= index && children.hasMoreElements()) {
                child = (HyperNode) children.nextElement();
                if (child.isAdded()) {
                    pos++;
                }
            }
            return child;
        }

        public int getIndexOfChild(Object parent, Object child) {
            int index = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                if (child.equals(children.nextElement())) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public boolean isLeaf(Object node) {
            return getChildCount(node) == 0;
        }

        public void fireTreeStructureChanged() {
            super.fireTreeStructureChanged(root, getPathToRoot(root),
                    null, null);
        }
    }

    class ExistTreeModel extends DefaultTreeModel {
        private HyperNode root;

        public ExistTreeModel(TreeNode root) {
            super(root);
            this.root = (HyperNode)root;
        }

        public int getChildCount(Object parent) {
            int count = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                HyperNode child = (HyperNode) children.nextElement();
                if (!parent.equals(root)) {
                    if (!child.isAdded()) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
            return count;
        }

        public Object getChild(Object parent, int index) {
            int pos = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            HyperNode child = null;
            while (pos <= index && children.hasMoreElements()) {
                child = (HyperNode) children.nextElement();
                if (!child.isAdded()) {
                    pos++;
                }
            }
            return child;
        }

        public int getIndexOfChild(Object parent, Object child) {
            int index = 0;
            HyperNode node = (HyperNode)parent;
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                if (child.equals(children.nextElement())) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public boolean isLeaf(Object node) {
            return getChildCount(node) == 0;
        }

        public void fireTreeStructureChanged() {
            super.fireTreeStructureChanged(root, getPathToRoot(root),
                    null, null);
        }
    }

}
