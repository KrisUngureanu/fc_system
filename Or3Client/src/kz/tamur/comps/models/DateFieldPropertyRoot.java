package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class DateFieldPropertyRoot extends PropertyRoot {
    public DateFieldPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        PropertyReestr.registerProperty(title);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            PropertyNode data = new PropertyNode(refs, "data", Types.REF, null, false, null);
            PropertyReestr.registerProperty(data);
            PropertyReestr.registerDebugProperty(data);
        new ParamFilters(refs);
        new PropertyNode(refs, "calcData", Types.EXPR, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new PropertyNode(view, "showDateChooser", Types.BOOLEAN, null, false, Boolean.FALSE);
        new FontProperty(view);
        new BackgroundProperty(view);
        new CompBorder(view, UIManager.getBorder("TextField.border"));
        EnumValue defDate = new EnumValue(Constants.DD_MM_YYYY, "дд.мм.гггг");
        EnumValue[] evs = { 
                new EnumValue(Constants.DD_MM, "дд.мм"), 
                defDate,
                new EnumValue(Constants.DD_MM_YYYY_HH_MM, "дд.мм.гггг чч:ММ"),
                new EnumValue(Constants.DD_MM_YYYY_HH_MM_SS, "дд.мм.гггг чч:ММ:сс"),
                new EnumValue(Constants.DD_MM_YYYY_HH_MM_SS_SSS, "дд.мм.гггг чч:ММ:сс:ССС"),
                new EnumValue(Constants.HH_MM, "чч:ММ"), 
                new EnumValue(Constants.HH_MM_SS, "чч:ММ:сс")
        };
        new PropertyNode(view, "format", Types.ENUM, evs, false, defDate);
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new CopyProperty(pov);
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        new PropertyNode(pov, "deleteOnType", Types.BOOLEAN, null, false, Boolean.TRUE);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
    }
}
