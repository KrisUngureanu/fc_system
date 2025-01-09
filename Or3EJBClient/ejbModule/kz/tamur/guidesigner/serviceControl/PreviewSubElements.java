package kz.tamur.guidesigner.serviceControl;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static kz.tamur.comps.Constants.GLOBAL_DEF_GRADIENT;
import static kz.tamur.rt.MainFrame.GRADIENT_MAIN_FRAME;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.comps.Utils;
import kz.tamur.comps.ui.AddCheckBoxTree;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.ServiceControlNode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JScrollPane;

/**
 * The Class PreviewSubElements.
 * 
 * @author Lebedev Sergey
 */
public class PreviewSubElements extends JDialog implements ActionListener {

    /** Использование прозрачности в диалоге. */
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    /** Главная панель. */
    private GradientPanel mainPane = new GradientPanel();

    /** Дерево просмотра структуры. */
    private StructureViewTree m_tree;

    /** Модель поведения дерева. */
    private DefaultTreeModel m_model;

    /** Класс добавления чекбоксов к дереву. */
    private AddCheckBoxTree AddCh = new AddCheckBoxTree();

    /** Менеджер управления деревом. */
    private AddCheckBoxTree.CheckTreeManager checkTreeManager;

    /** Скролл под дерево. */
    private JScrollPane scroll = new JScrollPane();

    /** Добавить выбранные узлы в дерево управления. */
    private JButton addBtn = new JButton("Добавить");

    /** Отменить добавление узлов в дерево управления. */
    private JButton cancelBtn = new JButton("Отменить");

    /** Результат открытия диалога. */
    private int result = -1;

    /**
     * Конструктор класса preview sub elements.
     * 
     * @param owner
     *            the owner
     * @param title
     *            the title
     * @param root_
     *            the root_
     */
    public PreviewSubElements(Frame owner, String title, ServiceControlNode root_) {
        super(owner, title, true);
        StructureViewNode root = new StructureViewNode(root_.getValue(), root_.getTitle(), root_.getLangId());
        setSize(400, 400);
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
        mainPane.setGradient(GRADIENT_MAIN_FRAME.isEmpty() ? GLOBAL_DEF_GRADIENT : GRADIENT_MAIN_FRAME);
        add(mainPane);
        m_model = new DefaultTreeModel(root);
        m_tree = Utils.getStructureViewTree(root);
        m_tree.setModel(m_model);
        m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        m_tree.setEditable(false);

        checkTreeManager = AddCh.new CheckTreeManager(m_tree, null);
        m_tree.repaint();
        scroll.getViewport().setOpaque(isOpaque);
        scroll.setOpaque(isOpaque);
        scroll.getViewport().add(m_tree);
        scroll.setPreferredSize(new Dimension(400, 400));

        mainPane.setLayout(new GridBagLayout());
        getRootPane().setDefaultButton(addBtn);
        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        addBtn.setFont(kz.tamur.rt.Utils.getDefaultFont());
        cancelBtn.setFont(kz.tamur.rt.Utils.getDefaultFont());

        mainPane.add(scroll, new GridBagConstraints(0, 0, 2, 1, 1, 1, CENTER, BOTH, new Insets(1, 1, 1, 1), 0, 0));
        mainPane.add(addBtn, new GridBagConstraints(0, 1, 1, 1, 1, 0, EAST, NONE, new Insets(5, 5, 2, 0), 0, 0));
        mainPane.add(cancelBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0, EAST, NONE, new Insets(5, 5, 2, 2), 0, 0));
    }

    /**
     * Получить tree node.
     * 
     * @param path
     *            the path
     * @return tree node
     */
    DefaultMutableTreeNode getTreeNode(TreePath path) {
        return (DefaultMutableTreeNode) (path.getLastPathComponent());
    }

    /**
     * Получить check tree manager.
     * 
     * @return check tree manager
     */
    public AddCheckBoxTree.CheckTreeManager getCheckTreeManager() {
        return checkTreeManager;
    }

    /**
     * Получить выбранные пути дерева просмотра.
     * 
     * @return selected paths
     */
    public List<TreePath> getSelectedPaths() {
        List<TreePath> list = new ArrayList<TreePath>();
        List<TreePath> returnList = new ArrayList<TreePath>();
        getPaths(m_tree, m_tree.getPathForRow(0), false, list, false);

        for (TreePath path : list) {
            if (checkTreeManager.getSelectionModel().isPathSelected(path, true)) {
                returnList.add(path);
            } else if (checkTreeManager.getSelectionModel().isPartiallySelected(path)) {
                returnList.add(path);
            }
        }

        return returnList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            result = ButtonsFactory.BUTTON_OK;
            setVisible(false);
        } else if (e.getSource() == cancelBtn) {
            result = ButtonsFactory.BUTTON_CANCEL;
            setVisible(false);
        }

    }

    /**
     * Получить выбранные пути дерева просмотра.
     * 
     * @param tree
     *            дерево
     * @param root
     *            начало просмотра
     * @param expanded
     *            развёрнут узел?
     * @param list
     *            результирующий список
     * @param add
     *            добавлять элемент?
     * @return paths
     */
    public void getPaths(JTree tree, TreePath root, boolean expanded, List<TreePath> list, boolean add) {
        if (expanded && !tree.isVisible(root)) {
            return;
        }
        if (add) {
            list.add(root);
        }
        TreeNode node = (TreeNode) root.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
                TreeNode n = e.nextElement();
                TreePath path = root.pathByAddingChild(n);
                getPaths(tree, path, expanded, list, true);
            }
        }
    }

    /**
     * Получить результат работы.
     * 
     * @return the result
     */
    public int getResult() {
        return result;
    }
}
