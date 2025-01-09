package kz.tamur.or3.client.comps.interfaces;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public interface TreeComponent {
        void setSelectionPath(TreePath path);
        void setSelectionPaths(TreePath[] paths);
        TreeModel getModel();
        void expandPath(TreePath treePath);
        TreeSelectionModel getSelectionModel();
        void collapsePath(TreePath treePath);
        int getRowForPath(TreePath treePath);
        void setModel(TreeModel m);
        TreePath getSelectionPath();
        TreePath[] getSelectionPaths();
        void addSelectionPath(TreePath treePath);
        int getRowCount();
        boolean isExpanded(TreePath treePath);
        TreePath getPathForRow(int row);
}