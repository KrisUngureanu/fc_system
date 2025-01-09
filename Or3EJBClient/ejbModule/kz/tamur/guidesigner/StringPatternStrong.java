package kz.tamur.guidesigner;

import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;

/**
 * User: vital
 * Date: 30.11.2004
 * Time: 17:58:04
 */
public class StringPatternStrong implements FindPattern {
    private String str;

    public StringPatternStrong(String str) {
        this.str = str.toLowerCase(Constants.OK);
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode node = (TreeNode)obj;
            if (node != null && node.toString() != null) {
                String s1 = node.toString().toLowerCase(Constants.OK);
                return s1.equals(str);
            }
        }
        return false;
    }
}
