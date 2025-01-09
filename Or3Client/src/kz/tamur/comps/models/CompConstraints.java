package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 27.05.2004
 * Time: 15:21:59
 * To change this template use File | Settings | File Templates.
 */
public class CompConstraints extends PropertyNode {

    public CompConstraints(PropertyNode parent) {
        super(parent, "formula", -1, null, false, null);
        PropertyNode expr = new PropertyNode(this, "expr", Types.EXPR, null, false, null);
        PropertyReestr.registerProperty(expr);
        PropertyReestr.registerDebugProperty(expr);
        new PropertyNode(this, "message", Types.RSTRING, null, false, null);
        new PropertyNode(this, "dataIntegrityControl", Types.BOOLEAN, null, false, false);
    }
}
