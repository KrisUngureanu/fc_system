package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 31.03.2006
 * Time: 11:01:18
 */
public class CopyProperty extends PropertyNode {
    public CopyProperty(PropertyNode parent) {
        super(parent, "copy", -1, null, false, null);
        new PropertyNode(this, "copyPath", Types.REF, null, false, null);
        new PropertyNode(this, "copyTitle", Types.RSTRING, null, false, null);
    }
}
