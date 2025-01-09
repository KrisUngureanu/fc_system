package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 03.05.2004
 * Time: 18:02:49
 * To change this template use File | Settings | File Templates.
 */
public class ColumnPosition extends PropertyNode {
    public ColumnPosition(PropertyNode parent) {
        super(parent, "width", -1, null, false, null);
        new PropertyNode(this, "pref", Types.INTEGER, null, false,
                new Integer(Constants.DEFAULT_PREF_WIDTH));
        new PropertyNode(this, "min", Types.INTEGER, null, false,
                new Integer(Constants.DEFAULT_MIN_WIDTH));
        new PropertyNode(this, "max", Types.INTEGER, null, false,
                new Integer(Constants.DEFAULT_MAX_WIDTH));
    }
}
