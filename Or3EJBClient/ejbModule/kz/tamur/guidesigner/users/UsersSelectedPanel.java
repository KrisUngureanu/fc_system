package kz.tamur.guidesigner.users;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.guidesigner.ButtonsFactory.createToolButton;
import static kz.tamur.rt.Utils.createLabel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.guidesigner.users.UserTree.UserTreeModel;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

/**
 * User: vital
 * Date: 11.12.2004
 * Time: 11:58:47
 */
public class UsersSelectedPanel extends JPanel implements TreeSelectionListener, ActionListener, ListSelectionListener {

    private UserTree tree;
    private DefaultListModel model = new DefaultListModel();
    private JList selectedList = new JList(model);
    private UserNode selectedNode;
    private JButton addBtn = createToolButton("addSingle", "", "", true);
    private JButton removeBtn = createToolButton("removeSingle", "", "", true);
    private JButton removeAllBtn = createToolButton("removeAll", "", "", true);
    private UserNode[] oldValue;
    private ArrayList<UserNode> oldVals = new ArrayList<UserNode>();
    private ArrayList<Long> addedNodeIds = new ArrayList<Long>();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private final Dimension s = new Dimension(30, 30);

    public UsersSelectedPanel(UserNode selectedNode) {
        this.selectedNode = selectedNode;
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(600, 400));
        tree = kz.tamur.comps.Utils.getUserTree();
        ((UserTreeModel) tree.getModel()).nodeStructureChanged((UserNode) tree.getModel().getRoot());
        tree.addTreeSelectionListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        selectedList.setFont(Utils.getDefaultFont());
        selectedList.setBackground(Utils.getLightSysColor());
        selectedList.setForeground(Utils.getDarkShadowSysColor());
        selectedList.addListSelectionListener(this);
        Utils.setAllSize(addBtn, s);
        Utils.setAllSize(removeBtn, s);
        Utils.setAllSize(removeAllBtn, s);

        addBtn.setEnabled(false);
        removeBtn.setEnabled(false);
        addBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        removeAllBtn.addActionListener(this);

        add(createLabel("Доступные пользователи"), new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        JScrollPane sp = new JScrollPane(tree);
        sp.setPreferredSize(new Dimension(255, 100));
        add(sp, new GridBagConstraints(0, 1, 2, 3, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(addBtn, new GridBagConstraints(2, 1, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 5, 0, 5), 0, 0));
        add(removeBtn, new GridBagConstraints(2, 2, 1, 1, 0, 0, CENTER, NONE, new Insets(10, 5, 0, 5), 0, 0));
        add(removeAllBtn, new GridBagConstraints(2, 3, 1, 1, 0, 0, NORTH, NONE, new Insets(10, 5, 0, 5), 0, 0));
        add(createLabel("Выбранные пользователи"), new GridBagConstraints(3, 0, 1, 1, 0, 0, WEST, HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        JScrollPane sp2 = new JScrollPane(selectedList);
        sp2.setPreferredSize(new Dimension(255, 100));
        add(sp2, new GridBagConstraints(3, 1, 2, 3, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));

        setSelectedNode(selectedNode);
        setOpaque(isOpaque);
        selectedList.setOpaque(isOpaque);
        ((JComponent)selectedList.getCellRenderer()).setOpaque(false);
        tree.setOpaque(isOpaque);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        sp2.setOpaque(isOpaque);
        sp2.getViewport().setOpaque(isOpaque);

    }

    public void setSelectedNode(UserNode selectedNode) {
        this.selectedNode = selectedNode;
        if (selectedNode != null) {
            UserNode n = (UserNode) tree.find(selectedNode.getKrnObj());
            tree.setSelectedNode(n);
            tree.requestFocusInWindow();
            oldVals.clear();
            addedNodeIds.clear();
            model.clear();
            addExists(n);
            if (oldVals.size() > 0) {
                oldValue = new UserNode[oldVals.size()];
                for (int i = 0; i < oldVals.size(); i++) {
                    oldValue[i] = (UserNode) oldVals.get(i);
                }
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        UserNode node = (UserNode) e.getPath().getLastPathComponent();
        addBtn.setEnabled(node.isLeaf() && (!addedNodeIds.contains(node.getKrnObj().id)));
        removeBtn.setEnabled(false);
    }

    private void addExists(UserNode node) {
        if (!node.isLeaf()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                UserNode child = (UserNode) node.getChildAt(i);
                if (child.isLeaf() && !addedNodeIds.contains(child.getKrnObj().id)) {
                    model.addElement(child);
                    addedNodeIds.add(child.getKrnObj().id);
                    oldVals.add(child);
                }
            }
        } else if (!addedNodeIds.contains(node.getKrnObj().id)) {
            model.addElement(node);
            addedNodeIds.add(node.getKrnObj().id);
            oldVals.add(node);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addBtn) {
            UserNode[] nodes = (UserNode[]) tree.getSelectedNodes();
            for (int i = 0; i < nodes.length; i++) {
                UserNode node = nodes[i];
                if (!addedNodeIds.contains(node.getKrnObj().id)) {
                    model.addElement(node);
                    addedNodeIds.add(node.getKrnObj().id);
                }
            }
        }
        if (src == removeBtn) {
            int idx = selectedList.getSelectedIndex();
            UserNode node = (UserNode) model.remove(idx);
            addedNodeIds.remove(new Long(node.getKrnObj().id));
            tree.treeStartSelection(node);
        }
        if (src == removeAllBtn) {
            int count = model.getSize();
            for (int i = count - 1; i >= 0; i--) {
                UserNode node = (UserNode) model.remove(i);
                addedNodeIds.remove(new Long(node.getKrnObj().id));
            }
        }
        tree.repaint();
    }

    public void valueChanged(ListSelectionEvent e) {
        Object o = selectedList.getSelectedValue();
        removeBtn.setEnabled(o != null);
    }

    public UserNode[] getSelectedValues() {
        UserNode[] res = null;
        ArrayList<UserNode> list = new ArrayList<UserNode>();
        for (int i = 0; i < model.getSize(); i++) {
            UserNode unode = (UserNode) model.getElementAt(i);
            boolean isExist = false;
            if (oldValue != null && oldValue.length > 0) {
                for (int j = 0; j < oldValue.length; j++) {
                    UserNode old = oldValue[j];
                    if (unode.getKrnObj().id == old.getKrnObj().id) {
                        isExist = true;
                        break;
                    }
                }
            }
            if (!isExist) {
                list.add(unode);
            }
        }
        int size = list.size();
        res = new UserNode[size];
        for (int i = 0; i < list.size(); i++) {
            res[i] = (UserNode) list.get(i);
        }
        return res;
    }

    public UserNode[] getOldValue() {
        return oldValue;
    }

    public void setOldValue(UserNode[] oldValue) {
        this.oldValue = oldValue;
    }

    public void setTree(UserTree tree) {
        this.tree = tree;
    }
}
