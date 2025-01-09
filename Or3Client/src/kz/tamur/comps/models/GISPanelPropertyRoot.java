package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.COMPONENT;
import static kz.tamur.comps.models.Types.ENUM;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.IMAGE;
import static kz.tamur.comps.models.Types.PMENUITEM;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.REPORT;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;

import java.awt.GridBagConstraints;

import javax.swing.UIManager;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class GISPanelPropertyRoot extends PropertyRoot {
    public GISPanelPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyReestr.registerDebugProperty(title);
        PropertyReestr.registerProperty(title);
        PropertyNode titleExpr = new PropertyNode(new PropertyNode(this, "title1", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(titleExpr);
        PropertyReestr.registerProperty(titleExpr);
        PropertyNode dynamicTitleExpr = new PropertyNode(new PropertyNode(this, "dynamicTitle", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(dynamicTitleExpr);
        PropertyReestr.registerProperty(dynamicTitleExpr);
        EnumValue defTitleAlign = new EnumValue(GridBagConstraints.CENTER, "По центру");
        EnumValue[] titleAlign = { defTitleAlign, new EnumValue(GridBagConstraints.WEST, "Слева"), new EnumValue(GridBagConstraints.EAST, "Справа"),
            };
        new PropertyNode(this, "titleAlign", ENUM, titleAlign, false, defTitleAlign);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        new PropertyNode(this, "children", COMPONENT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(ref, "data", REF, null, false, null);
        PropertyReestr.registerProperty(data);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Panel.background"));
        new CompBorder(view, UIManager.getBorder("Panel.border"));
        new PropertyNode(view, "icon", IMAGE, null, false, null);
        new PropertyNode(view, "backgroundPict", IMAGE, null, false, null);
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
        new PropertyNode(view, "positionPict", ENUM, positions, false,GridBagConstraints.CENTER);
        new PropertyNode(view, "autoResizePict", BOOLEAN, null, false, true);

        // Показывать заголовок панели
        new PropertyNode(view, "showHeader", BOOLEAN, null, false, false);
        // Скрываемая панель
        new PropertyNode(view, "collapsible", BOOLEAN, null, false, false);
        // Кнопка обновить
        new PropertyNode(view, "refreshable", BOOLEAN, null, false, false);
        // Кнопка - на весь экран
        new PropertyNode(view, "expandable", BOOLEAN, null, false, false);

        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(pov, "beforeOpen", EXPR, null, false, null);
        new PropertyNode(pov, "afterOpen", EXPR, null, false, null);
        new PropertyNode(pov, "beforeClose", EXPR, null, false, null);
        new PropertyNode(pov, "afterClose", EXPR, null, false, null);
        new PropertyNode(pov, "afterSave", EXPR, null, false, null);
        new PropertyNode(pov, "afterTaskListUpdate", EXPR, null, false, null);
        new PropertyNode(pov, "onNotification", EXPR, null, false, null);
        new PropertyNode(pov, "createXml", EXPR, null, false, null);

        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);

        PropertyNode reports = new PropertyNode(this, "reports", REPORT, null, true, null);
        PropertyReestr.registerProperty(reports);
        new PropertyNode(this, "pmenu", PMENUITEM, null, true, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);
        
        PropertyNode map = new PropertyNode(this, "map", -1, null, false, null);
        new PropertyNode(map, "formula", EXPR, null, false, null);
        new PropertyNode(map, "layers",  EXPR, null, false, null);
        new PropertyNode(map, "bounds", EXPR, null, false, null);
        new PropertyNode(map, "selections", EXPR, null, false, null);
        new PropertyNode(map, "onSelect", EXPR, null, false, null);
    }
}