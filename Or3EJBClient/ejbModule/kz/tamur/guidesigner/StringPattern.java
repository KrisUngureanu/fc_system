package kz.tamur.guidesigner;

import static kz.tamur.ods.ComparisonOperations.*;

import java.util.Locale;

import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;

/**
 * Date: 30.11.2004
 * Time: 17:58:04
 * 
 * @author vital
 */
public class StringPattern implements FindPattern {
    private final String str;
    private final int searchMethod;

    /**
     * Конструктор класса string pattern.
     * 
     * @param str
     *            str.
     */
    public StringPattern(String str) {
        this(str, -1);
    }

    /**
     * Конструктор класса string pattern.
     * 
     * @param str
     *            str.
     * @param searchMethod
     *            search method.
     */
    public StringPattern(String str, Integer searchMethod) {
        this.str = str.toUpperCase(Constants.OK);
        this.searchMethod = searchMethod;
    }

    @Override
    public boolean isMatches(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode node = (TreeNode) obj;
            String s = node.toString().toUpperCase(Constants.OK);
            switch (searchMethod) {
            case SEARCH_START_WITH:
                return s.startsWith(str);
            case CO_EQUALS:
                return s.equals(str);
            case CO_CONTAINS:
                return s.contains(str);
            default:
                return s.startsWith(str);
            }
        }
        return false;
    }
}
