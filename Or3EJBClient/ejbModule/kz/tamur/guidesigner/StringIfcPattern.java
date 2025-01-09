package kz.tamur.guidesigner;

import static kz.tamur.ods.ComparisonOperations.CO_CONTAINS;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS;
import static kz.tamur.ods.ComparisonOperations.SEARCH_START_WITH;

import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.hypers.HyperNode;

public class StringIfcPattern implements FindPattern{

	private final String str;
    private final int searchMethod;

    /**
     * Конструктор класса string pattern.
     * 
     * @param str
     *            str.
     */
    public StringIfcPattern(String str) {
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
    public StringIfcPattern(String str, Integer searchMethod) {
        this.str = str.toUpperCase(Constants.OK);
        this.searchMethod = searchMethod;
    }

    @Override
    public boolean isMatches(Object obj) {
        if (obj instanceof TreeNode) {
            HyperNode node = (HyperNode) obj;
            String s = null;
            if(node.getIfcObjectItem() != null && node.getIfcObjectItem().title != null)
            	s = node.getIfcObjectItem().title.toUpperCase(Constants.OK);
            if(s != null) {
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
        }
        return false;
    }
}
