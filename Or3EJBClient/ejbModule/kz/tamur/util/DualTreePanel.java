package kz.tamur.util;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Utils.getServicesControlTree;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceTree;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.reports.ReportTree;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;

/**
 * The Class DualTreePanel.
 *
 * @author Lebedev Sergey
 */
public class DualTreePanel extends JPanel implements ActionListener {

    /** color f. */
    private Color colorF = getForeground();
    
    /** layout. */
    private static CardLayout layout = new CardLayout();
    
    /** all btn. */
    private OrTransparentButton allBtn = new OrTransparentButton("Все", null, colorF);
    
    /** project btn. */
    private OrTransparentButton projectBtn = new OrTransparentButton("Проект", null, colorF);
    
    /** tree. */
    private DesignerTree tree;
    
    /** tree pr. */
    private DesignerTree treePr;
    
    /** selected tree. */
    private DesignerTree selectedTree;
    
    /** type tree. */
    private int typeTree = -1;

    /** selector. */
    private JPanel selector = new JPanel(layout);

    /**
     * Конструктор класса dual tree panel.
     *
     * @param tree the tree
     */
    public DualTreePanel(DesignerTree tree) {
        this(tree, null);
    }

    /**
     * Конструктор класса dual tree panel.
     *
     * @param tree_ the tree_
     * @param root the root
     */
    public DualTreePanel(DesignerTree tree_, ServiceControlNode root) {
        super(new GridBagLayout());
        tree = tree_;
        if (tree instanceof ServicesTree) {
            typeTree = 0;
        } else if (tree instanceof InterfaceTree) {
            typeTree = 1;
        } else if (tree instanceof FiltersTree) {
            typeTree = 2;
        } else if (tree instanceof ReportTree) {
            typeTree = 3;
        } else if (tree instanceof ServicesControlTree) {
            typeTree = 4;
        }
        if (typeTree != -1) {
            createProjectTree(root);
            treePr.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    setEnabledBtn((ServiceControlNode) e.getPath().getLastPathComponent());
                }
            });
            JScrollPane treePrScrollPane = new JScrollPane(treePr);
            treePrScrollPane.setOpaque(false);
            treePrScrollPane.getViewport().setOpaque(false);
            selector.add(treePrScrollPane, "project");
            treePr.setOpaque(false);
            allBtn.addActionListener(this);
            projectBtn.addActionListener(this);
            allBtn.setSelected(true);
            if (treePr != null) {
                add(allBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, new Insets(4, 4, 4, 0), 0, 0));
                add(projectBtn, new GridBagConstraints(1, 0, 1, 1, 1, 0, WEST, NONE, new Insets(4, 4, 4, 0), 0, 0));
            }
        }

        JScrollPane treeScrollPane = new JScrollPane(tree);
        setOpaque(false);
        selector.setOpaque(false);
        tree.setOpaque(false);
        treeScrollPane.setOpaque(false);
        treeScrollPane.getViewport().setOpaque(false);
        add(selector, new GridBagConstraints(0, 1, 2, 1, 1, 1, CENTER, BOTH, new Insets(4, 4, 4, 4), 0, 0));
        selector.add(treeScrollPane, "all");
        layout.show(selector, "all");
        selectedTree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object key = e.getSource();
        if (key == allBtn) {
            layout.show(selector, "all");
            selectedTree = tree;
            allBtn.setSelected(true);
            projectBtn.setSelected(false);
            DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
            dialog.setOkEnabled(tree.getSelectedNode()!= null && tree.getSelectedNode().isLeaf());
            dialog.setEditEnabled(false);
        } else if (key == projectBtn) {
            layout.show(selector, "project");
            selectedTree = treePr;
            allBtn.setSelected(false);
            projectBtn.setSelected(true);
            setEnabledBtn((ServiceControlNode) treePr.getSelectedNode());
        }
    }

    /**
     * Creates the project tree.
     *
     * @param root the root
     */
    public void createProjectTree(ServiceControlNode root) {
        treePr = getServicesControlTree(root == null ? (ServiceControlNode) getServicesControlTree().getSelectedNode() : root);
    }

    /**
     * Получить tree pr.
     *
     * @return the treePr
     */
    public DesignerTree getTreePr() {
        return treePr;
    }
    
    public DesignerTree getTree() {
        return tree;
    }

    /**
     * Получить selected tree.
     *
     * @return the selectedTree
     */
    public DesignerTree getSelectedTree() {
        return selectedTree;
    }
    
    /**
     * Установить enabled btn.
     *
     * @param node новое значение enabled btn
     */
    public void setEnabledBtn(ServiceControlNode node) {
        DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
        boolean enabled = false;
        if (node != null) {
            switch (typeTree) {
            case 0:
                enabled = node.isService();
                break;
            case 1:
                enabled = node.isInterface();
                break;
            case 2:
                enabled = node.isFilter();
                break;
            case 3:
                enabled = node.isReport();
                break;
            case 4:
                enabled = true;
                break;
            default:
                break;
            }
        }
        dialog.setEditEnabled(enabled);
        dialog.setOkEnabled(enabled);
    
    }

    /**
     * Получить type tree.
     *
     * @return the typeTree
     */
    public int getTypeTree() {
        return typeTree;
    }
}
