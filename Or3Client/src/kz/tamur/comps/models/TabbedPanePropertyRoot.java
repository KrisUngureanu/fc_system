package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import kz.tamur.comps.Constants;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class TabbedPanePropertyRoot extends PropertyRoot {
    public TabbedPanePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        //
        new PropertyNode(this, "children", Types.COMPONENT, null, false, null);
        //Позиция
        new CompPosition(this);
        //
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode font = new FontProperty(view);
        PropertyNode fCol = font.getChild("fontColor");
        fCol.setDefaultValue(UIManager.getColor("TabbedPane.foreground"));
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("TabbedPane.background"));
        new CompBorder(view, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.TAB_WRAP_LINE, "Разрыв линии"),
            new EnumValue(Constants.TAB_SCROLL, "Прокрутка")
        };
        new PropertyNode(view, "tabPolicy", Types.ENUM, env, false, null);

        env = new EnumValue[] {
            new EnumValue(Constants.TAB_TOP, "Сверху"),
            new EnumValue(Constants.TAB_LEFT, "Слева"),
            new EnumValue(Constants.TAB_BOTTOM, "Снизу"),
            new EnumValue(Constants.TAB_RIGHT, "Справа")
        };
        new PropertyNode(view, "tabOrientation", Types.ENUM, env, false, Constants.TAB_TOP);

        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
      //  new PropertyNode(pov, "isVisible", Types.EXPR, null, false, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);
    }
}
