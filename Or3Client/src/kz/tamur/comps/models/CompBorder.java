package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 27.05.2004
 * Time: 9:29:50
 * To change this template use File | Settings | File Templates.
 */
public class CompBorder extends PropertyNode {

    public CompBorder(PropertyNode parent, Object defaultBorder) {
        super(parent, "border", -1, null, false, null);
        new PropertyNode(this, "borderType", Types.BORDER, null, false, defaultBorder);
        new PropertyNode(this, "borderTitle", Types.RSTRING, null, false, null);
    }
}
