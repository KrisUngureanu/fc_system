package kz.tamur.comps.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.checkBox.OrTristateCheckBox;

/**
 * Класс с функционалом добавления чекбоксов в дерево.
 * 
 * @author Lebedev Sergey
 */
public class AddCheckBoxTree {

    /**
     * Модель выбора узлов дерева
     * 
     * @author Lebedev Sergey
     */
    public class CheckTreeSelectionModel extends DefaultTreeSelectionModel {

        /** model. */
        private TreeModel model;

        /**
         * Конструктор класса check tree selection model.
         * 
         * @param model
         *            the model
         */
        public CheckTreeSelectionModel(TreeModel model) {
            this.model = model;
            setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }

        /**
         * Проверки, есть ли невыбранные узлы в поддереве данного пути (DONT_CARE).
         * 
         * @param path
         *            the path
         * @return <code>true</code>, если partially selected
         */
        public boolean isPartiallySelected(TreePath path) {
            if (isPathSelected(path, true)) {
                return false;
            }

            TreePath[] selectionPaths = getSelectionPaths();

            if (selectionPaths == null) {
                return false;
            }

            for (TreePath treePath : selectionPaths) {
                if (isDescendant(treePath, path)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * tells whether given path is selected.
         * if dig is true, then a path is assumed to be selected, if
         * one of its ancestor is selected.
         * 
         * @param path
         *            the path
         * @param dig
         *            the dig
         * @return <code>true</code>, если path selected
         */
        public boolean isPathSelected(TreePath path, boolean dig) {
            if (!dig) {
                return super.isPathSelected(path);
            }

            while (path != null && !super.isPathSelected(path)) {
                path = path.getParentPath();
            }

            return path != null;
        }

        /**
         * Определяет, является ли путь <code>path1</code> потомком путя <code>path2</code>.
         * 
         * @param path1
         *            путь, потомство которого определяется
         * @param path2
         *            путь, у которого ищем потомка
         * @return <code>true</code> если <code>path1</code> потомок путя <code>path2</code>
         */
        private boolean isDescendant(TreePath path1, TreePath path2) {
            Object obj1[] = path1.getPath();
            Object obj2[] = path2.getPath();
            for (int i = 0; i < obj2.length; i++) {
                if (obj1[i] != obj2[i])
                    return false;
            }
            return true;
        }

        @Override
        public void setSelectionPaths(TreePath[] pPaths) {
            throw new UnsupportedOperationException("Не реализованно!");
        }

        @Override
        public void addSelectionPaths(TreePath[] paths) {
            // отменить выбор всех потомков узлов массива paths[]
            for (TreePath path : paths) {
                // получить все выбранные узлы
                TreePath[] selectionPaths = getSelectionPaths();

                // если нет выбранных узлов, то необходимо выйти
                if (selectionPaths == null) {
                    break;
                }
                // определить список узлов на удаление выбора
                ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();

                for (TreePath path_ : selectionPaths) {
                    // если узел - потомок, то выделение с него необходимо удалить
                    if (isDescendant(path_, path)) {
                        toBeRemoved.add(path_);
                    }
                }
                // убрать выбор найденных узлов
                super.removeSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[0]));
            }

            // if all siblings are selected then unselect them and select parent recursively
            // otherwize just select that path.
            for (TreePath path : paths) {
                TreePath temp = null;

                while (areSiblingsSelected(path)) {
                    temp = path;

                    if (path.getParentPath() == null) {
                        break;
                    }

                    path = path.getParentPath();
                }

                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        addSelectionPath(temp.getParentPath());
                    } else {
                        if (!isSelectionEmpty()) {
                            removeSelectionPaths(getSelectionPaths());
                        }

                        super.addSelectionPaths(new TreePath[] { temp });
                    }
                } else {
                    super.addSelectionPaths(new TreePath[] { path });
                }
            }
        }

        /**
         * tells whether all siblings of given path are selected
         * 
         * @param path
         *            the path
         * @return true, в случае успеха
         */
        private boolean areSiblingsSelected(TreePath path) {
            TreePath parent = path.getParentPath();

            if (parent == null) {
                return true;
            }
            // узел искомого путя
            Object node = path.getLastPathComponent();
            // родитель искомого путя (родитель)
            Object parentNode = parent.getLastPathComponent();
            // количество потомков родителя
            int childCount = model.getChildCount(parentNode);

            // обход всех потомков родителя
            for (int i = 0; i < childCount; i++) {
                // получить узел-одного из потомков родителя
                Object childNode = model.getChild(parentNode, i);
                // если потомок - искомый узел, то пропустить его анализ
                if (childNode == node) {
                    continue;
                }
                if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void removeSelectionPaths(TreePath[] paths) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() == 1)
                    super.removeSelectionPaths(new TreePath[] { path });
                else
                    toggleRemoveSelection(path);
            }
        }

        /**
         * if any ancestor node of given path is selected then unselect it
         * and selection all its descendants except given path and descendants.
         * otherwise just unselect the given path
         * 
         * @param path
         *            the path
         */
        private void toggleRemoveSelection(TreePath path) {

            Stack<TreePath> stack = new Stack<TreePath>();
            TreePath parent = path.getParentPath();

            Boolean isParameters = false;
            Boolean isDescription = false;

            while (parent != null && !isPathSelected(parent)) {
                stack.push(parent);
                parent = parent.getParentPath();
            }
            if (parent != null)
                stack.push(parent);
            else {
                super.removeSelectionPaths(new TreePath[] { path });
                return;
            }

            while (!stack.isEmpty()) {
                TreePath temp = (TreePath) stack.pop();

                TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();

                Object node = temp.getLastPathComponent();
                Object peekNode = peekPath.getLastPathComponent();
                int childCount = model.getChildCount(node);

                for (int i = 0; i < childCount; i++) {
                    Object childNode = model.getChild(node, i);

                    if (childNode.toString().equals("parameters") && model.isLeaf(childNode)) {
                        isParameters = true;
                    }
                    if (childNode.toString().equals("description") && model.isLeaf(childNode)) {
                        isDescription = true;
                    }

                    if (childNode != peekNode) {
                        if (!isParameters && !isDescription)
                            super.addSelectionPaths(new TreePath[] { temp.pathByAddingChild(childNode) });
                    }
                }
            }

            super.removeSelectionPaths(new TreePath[] { parent });
        }

        /**
         * Получить model.
         * 
         * @return model
         */
        public TreeModel getModel() {
            return model;
        }
    }

    /**
     * Отрисовщик узла дерева с чекбоксом
     * 
     * @author Lebedev Sergey
     */
    public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

        /** selection model. */
        CheckTreeSelectionModel selectionModel;

        /** delegate. */
        private TreeCellRenderer delegate;

        /**
         * Конструктор класса check tree cell renderer.
         * 
         * @param delegate
         *            the delegate
         * @param selectionModel
         *            the selection model
         */
        public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
            this.delegate = delegate;
            this.selectionModel = selectionModel;
            setLayout(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            removeAll();
            OrTristateCheckBox checkBox = new OrTristateCheckBox(null, Constants.DONT_CARE);
            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                if (selectionModel.isPathSelected(path, true)) {
                    checkBox.setState(Constants.SELECTED);
                } else {
                    checkBox.setState(Constants.NOT_SELECTED);
                }

                if (selectionModel.isPartiallySelected(path)) {
                    checkBox.setState(Constants.DONT_CARE);
                }
            }
            checkBox.updateUI();
            add(checkBox, BorderLayout.WEST);
            add(renderer, BorderLayout.CENTER);
            return this;
        }

        /**
         * Получить delegate.
         * 
         * @return delegate
         */
        public TreeCellRenderer getDelegate() {
            return delegate;
        }

        /**
         * Установить delegate.
         * 
         * @param delegate
         *            новое значение delegate
         */
        public void setDelegate(TreeCellRenderer delegate) {
            this.delegate = delegate;
        }
    }

    /**
     * Управление деревом.
     * 
     * @author Lebedev Sergey
     */
    public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener {

        /** Модель выбора. */
        CheckTreeSelectionModel selectionModel;

        /** Дерево. */
        private JTree tree = new JTree();

        /** Ширина иконки чекбокса, используется для определения действия по клику мыши на узле дерева. */
        int hotspot = new JCheckBox().getPreferredSize().width;

        /**
         * Конструктор класса check tree manager.
         * 
         * @param tree_
         *            the tree_
         * @param model
         *            the model
         */
        public CheckTreeManager(JTree tree_, CheckTreeSelectionModel model) {
            tree = tree_;
            selectionModel = model == null ? new CheckTreeSelectionModel(tree.getModel()) : model;
            tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
            tree.addMouseListener(this);
            selectionModel.addTreeSelectionListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            if (path == null || (me.getX() / 1.2) > (tree.getPathBounds(path).x + hotspot)) {
                return;
            }

            boolean selected = selectionModel.isPathSelected(path, true);
            selectionModel.removeTreeSelectionListener(this);
            try {
                if (selected) {
                    selectionModel.removeSelectionPaths(new TreePath[] { path });
                } else {
                    selectionModel.addSelectionPaths(new TreePath[] { path });
                }

                selected = selectionModel.isPathSelected(path, true);
            } finally {
                selectionModel.addTreeSelectionListener(this);
                tree.treeDidChange();
            }
        }

        /**
         * Получить модель выбора.
         * 
         * @return selection model
         */
        public CheckTreeSelectionModel getSelectionModel() {
            return selectionModel;
        }

        /**
         * Установить модель выбора.
         * 
         * @param model
         *            новое значение selection model
         */
        public void setSelectionModel(CheckTreeSelectionModel model) {
            selectionModel = model;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            tree.treeDidChange();
        }
    }

}
