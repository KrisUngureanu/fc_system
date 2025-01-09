package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 9:55:11
 * To change this template use File | Settings | File Templates.
 */
public class SplitPanePropertyRoot extends PropertyRoot {
    public SplitPanePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //
        new PropertyNode(this, "left", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "right", Types.COMPONENT, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.HORIZONTAL, "Горизонтальное"),
            new EnumValue(Constants.VERTICAL, "Вертикальное")
        };
        new PropertyNode(view, "orientation", Types.ENUM, env, false, null);
        new PropertyNode(view, "dividerLocation", Types.DOUBLE, null, false,
                new Double(0.5));
        new CompBorder(view, null);
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "isVisible", Types.EXPR, null, false, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);
/*
        new PropertyNode(constr, "activity", Types.EXPR, null, false, null);
        //new PropertyNode(this, "dividerLocation1", Types.INTEGER, null, false, null);
*/
    }
}
