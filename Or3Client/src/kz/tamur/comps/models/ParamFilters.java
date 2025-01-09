package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 27.05.2004
 * Time: 15:21:59
 * To change this template use File | Settings | File Templates.
 */
public class ParamFilters extends PropertyNode {

    public ParamFilters(PropertyNode parent) {
        super(parent, "paramFilters", -1, null, false, null);
        new PropertyNode(this, "filters", Types.FILTER, null, true, null);
        new PropertyNode(this, "paramName", Types.STRING, null, false, null);
    }
}
