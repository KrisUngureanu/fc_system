package kz.tamur.guidesigner;

import kz.tamur.util.AbstractDesignerTreeNode;

import java.util.Locale;

import javax.swing.tree.TreeNode;

/**
 * User: vital
 * Date: 30.11.2004
 * Time: 17:58:04
 */
public class UIDPattern implements FindPattern {
    private String str;

    public UIDPattern(String str) {
        this.str = str.toLowerCase(Locale.ROOT);
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode node = (TreeNode)obj;
            String s1 = ((AbstractDesignerTreeNode)node).getKrnObj().uid;
            return s1.equals(str);
        }
        return false;
    }
}
