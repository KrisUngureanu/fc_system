package kz.tamur.comps;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 24.08.2004
 * Time: 17:24:47
 * To change this template use File | Settings | File Templates.
 */
public class FilterMenuItem extends JCheckBox implements Comparable{

    public Filter filter;

    public FilterMenuItem(Filter filter) {
        super(filter.toString());
        this.filter = filter;
        setFont(kz.tamur.rt.Utils.getDefaultFont());
        setForeground(kz.tamur.rt.Utils.getDarkShadowSysColor());
    }

    public int compareTo(Object o) {
        Filter f = ((FilterMenuItem) o).filter;
        String s = f.toString();
        return filter.toString().compareTo(s);
    }
}
