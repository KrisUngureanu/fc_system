package kz.tamur.comps.ui.ext.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Sergey Lebedev
 * 
 */

public class CollectionUtils {
    /**
     * Clones list into new one
     */

    public static <T> List<T> clone(List<T> list) {
        List<T> cloned = new ArrayList<T>(list.size());
        cloned.addAll(list);
        return cloned;
    }

    /**
     * Converts data collection into list
     */

    public static <T> List<T> asList(T... data) {
        List<T> list = new ArrayList<T>(data.length);
        Collections.addAll(list, data);
        return list;
    }

    public static <T> List<T> asList(Collection<T> data) {
        List<T> list = new ArrayList<T>(data.size());
        list.addAll(data);
        return list;
    }

    /**
     * Removes all null elements from list
     */

    public static <T> List<T> removeNulls(List<T> list) {
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i) == null) {
                    list.remove(i);
                }
            }
        }
        return list;
    }

    /**
     * Returns true if both lists has same content
     */

    public static boolean areEqual(List list1, List list2) {
        if (list1 == null && list2 == null) {
            return true;
        } else if ((list1 == null || list2 == null) && list1 != list2) {
            return false;
        } else {
            if (list1.size() != list2.size()) {
                return false;
            } else {
                for (Object object : list1) {
                    if (!list2.contains(object)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    /**
     * Converts Integer list to int[] array
     */

    public static int[] toArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Integer integer = list.get(i);
            array[i] = integer != null ? integer : 0;
        }
        return array;
    }
}
