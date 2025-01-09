package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 31.03.2006
 * Time: 11:01:18
 */
public class BackgroundProperty extends PropertyNode {
    public BackgroundProperty(PropertyNode parent) {
        super(parent, "background", -1, null, false, null);
        new PropertyNode(this, "backgroundColor", Types.COLOR, null, false, null);
        new PropertyNode(this, "backgroundColorExpr", Types.EXPR, null, false, null);
    }
}
