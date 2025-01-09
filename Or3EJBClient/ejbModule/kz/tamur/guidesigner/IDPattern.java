package kz.tamur.guidesigner;

import java.util.Locale;

import javax.swing.tree.TreeNode;

import kz.tamur.util.AbstractDesignerTreeNode;

public class IDPattern implements FindPattern {

	private long id;

    public IDPattern(long id) {
        this.id = id;
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode node = (TreeNode)obj;
            long id1 = ((AbstractDesignerTreeNode)node).getKrnObj().id;
            return id1 == id;
        }
        return false;
    }
}
