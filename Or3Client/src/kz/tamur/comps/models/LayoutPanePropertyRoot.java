package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 17:12:17
 * To change this template use File | Settings | File Templates.
 */
public class LayoutPanePropertyRoot extends PropertyRoot {
    public LayoutPanePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.RSTRING, null, false, null);

        new PropertyNode(this, "first", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "after", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "last", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "before", Types.COMPONENT, null, false, null);
        new PropertyNode(this, "center", Types.COMPONENT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.SCROLL_BOTH, "Оба"),
            new EnumValue(Constants.SCROLL_HORIZONTAL, "Горизонтальный"),
            new EnumValue(Constants.SCROLL_VERTICAL, "Вертикальный"),
            new EnumValue(Constants.SCROLL_AS_NEEDED, "По необходимости")
        };
        new PropertyNode(view, "scrollPolicy", Types.ENUM, env, false, null);
        new CompBorder(view, null);
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "isVisible", Types.EXPR, null, false, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);
    }
}
