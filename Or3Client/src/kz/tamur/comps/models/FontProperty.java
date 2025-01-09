package kz.tamur.comps.models;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 31.03.2006
 * Time: 11:01:18
 */
public class FontProperty extends PropertyNode {
    public FontProperty(PropertyNode parent) {
        super(parent, "font", -1, null, false, null);
        new PropertyNode(this, "fontG", Types.FONT, null, false, PropertyUtil.getDefaultFont());
        new PropertyNode(this, "fontGExpr", Types.EXPR, null, false, null);
        new PropertyNode(this, "fontColor", Types.COLOR, null, false, Color.black);
        new PropertyNode(this, "fontExpr", Types.EXPR, null, false, null);
    }
}
