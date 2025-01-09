package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;

import java.awt.GridBagConstraints;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class PanelPropertyRoot extends PropertyRoot {
    public PanelPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        PropertyReestr.registerDebugProperty(title);
        PropertyReestr.registerProperty(title);
        PropertyNode titleExpr = new PropertyNode(new PropertyNode(this, "title1", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(titleExpr);
        PropertyReestr.registerProperty(titleExpr);
        PropertyNode dynamicTitleExpr = new PropertyNode(new PropertyNode(this, "dynamicTitle", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(dynamicTitleExpr);
        PropertyReestr.registerProperty(dynamicTitleExpr);
        EnumValue defTitleAlign = new EnumValue(GridBagConstraints.CENTER, "По центру");
        EnumValue[] titleAlign = {
                defTitleAlign,
                new EnumValue(GridBagConstraints.WEST, "Слева"),
                new EnumValue(GridBagConstraints.EAST, "Справа"),
            };
        new PropertyNode(this, "titleAlign", Types.ENUM, titleAlign, false, defTitleAlign);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        new PropertyNode(this, "children", Types.COMPONENT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode ref =
                new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(ref, "data", Types.REF, null, false, null);
        PropertyReestr.registerProperty(data);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Panel.background"));
        new CompBorder(view, UIManager.getBorder("Panel.border"));
        new PropertyNode(view, "icon", Types.IMAGE, null, false, null);
        new PropertyNode(view, "backgroundPict", Types.IMAGE, null, false, null);
        EnumValue[] positions = {
                new EnumValue(GridBagConstraints.CENTER, "По центру"),
                new EnumValue(GridBagConstraints.WEST, "Слева"),
                new EnumValue(GridBagConstraints.EAST, "Справа"),
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"),
                new EnumValue(GridBagConstraints.NORTHWEST, "Сверху слева"),
                new EnumValue(GridBagConstraints.NORTHEAST, "Сверху справа"),
                new EnumValue(GridBagConstraints.SOUTHWEST, "Снизу слева"),
                new EnumValue(GridBagConstraints.SOUTHEAST, "Снизу справа"),
            };
        new PropertyNode(view, "positionPict", Types.ENUM, positions, false,GridBagConstraints.CENTER);
        new PropertyNode(view, "autoResizePict", Types.BOOLEAN, null, false, true);

        // Показывать заголовок панели
        new PropertyNode(view, "showHeader", Types.BOOLEAN, null, false, false);
        // Скрываемая панель
        new PropertyNode(view, "collapsible", Types.BOOLEAN, null, false, false);
        // Кнопка обновить
        new PropertyNode(view, "refreshable", Types.BOOLEAN, null, false, false);
        // Кнопка - на весь экран
        new PropertyNode(view, "expandable", Types.BOOLEAN, null, false, false);
        // Скрыть хлебные крошки
        new PropertyNode(view, "hideBreadCrumps", Types.BOOLEAN, null, false, false);

        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", Types.BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(pov, "beforeOpen", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterOpen", Types.EXPR, null, false, null);
        new PropertyNode(pov, "beforeClose", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterClose", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterSave", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterTaskListUpdate", Types.EXPR, null, false, null);
        new PropertyNode(pov, "onNotification", Types.EXPR, null, false, null);
        new PropertyNode(pov, "onMessageReceived", Types.EXPR, null, false, null);
        new PropertyNode(pov, "createXml", Types.EXPR, null, false, null);

        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        new PropertyNode(constr, "dataIntegrityControl", Types.BOOLEAN, null, false, false);

        PropertyNode reports = new PropertyNode(this, "reports", Types.REPORT, null, true, null);
        PropertyReestr.registerProperty(reports);
        new PropertyNode(this, "pmenu", Types.PMENUITEM, null, true, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);
    }
}