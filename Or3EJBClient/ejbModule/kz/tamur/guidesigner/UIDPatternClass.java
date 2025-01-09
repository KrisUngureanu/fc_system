package kz.tamur.guidesigner;

import static kz.tamur.ods.ComparisonOperations.*;

import com.cifs.or2.client.ClassNode;
import kz.tamur.comps.Constants;

/**
 * Date: 30.11.2004
 * Time: 17:58:04
 * 
 * @author vital
 */
public class UIDPatternClass implements FindPattern {
    private final String uid;
    private final int searchMethod;

    /**
     * Конструктор класса string pattern.
     * 
     * @param str
     *            str.
     */
    public UIDPatternClass(String uid) {
        this(uid, -1);
    }

    /**
     * Конструктор класса string pattern.
     * 
     * @param str
     *            str.
     * @param searchMethod
     *            search method.
     */
    public UIDPatternClass(String uid, Integer searchMethod) {
        this.uid = uid.toUpperCase(Constants.OK);
        this.searchMethod = searchMethod;
    }

    @Override
    public boolean isMatches(Object obj) {
        if (obj instanceof ClassNode) {
        	String uid = ((ClassNode) obj).getKrnClass().uid;
            String s = uid.toString().toUpperCase(Constants.OK);
            switch (searchMethod) {
            case SEARCH_START_WITH:
                return s.startsWith(this.uid);
            case CO_EQUALS:
                return s.equals(this.uid);
            case CO_CONTAINS:
                return s.contains(this.uid);
            default:
                return s.startsWith(this.uid);
            }
        }
        return false;
    }
}
