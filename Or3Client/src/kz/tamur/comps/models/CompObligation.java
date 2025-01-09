package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 31.03.2006
 * Time: 11:57:30
 */
public class CompObligation extends PropertyNode {
    public CompObligation(PropertyNode parent) {
        super(parent, "obligation", -1, null, false, null);
        new PropertyNode(this, "group", Types.INTEGER, null, false, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.BINDING, "Обязательный"),
            new EnumValue(Constants.OPTIONAL, "Факультативный")
        };
        new PropertyNode(this, "input", Types.ENUM, env, false, null);
        PropertyNode mess = new PropertyNode(this, "message", Types.RSTRING, null, false, null);
        PropertyReestr.registerProperty(mess);
        PropertyReestr.registerDebugProperty(mess);
        PropertyNode calc = new PropertyNode(this, "calc", Types.EXPR, null, false, null);
        PropertyReestr.registerProperty(calc);
        PropertyReestr.registerDebugProperty(calc);
        PropertyNode calcValue = new PropertyNode(this, "calcValue", Types.EXPR, null, false, null);
        PropertyReestr.registerProperty(calcValue);
        PropertyReestr.registerDebugProperty(calcValue);

    }
}
