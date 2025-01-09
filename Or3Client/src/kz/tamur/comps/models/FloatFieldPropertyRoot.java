package kz.tamur.comps.models;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class FloatFieldPropertyRoot extends PropertyRoot {
    public FloatFieldPropertyRoot() {
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
        new FontProperty(view);
        new BackgroundProperty(view);
        new CompBorder(view, UIManager.getBorder("TextField.border"));
        new PropertyNode(view, "bitSeparation", Types.BOOLEAN, null, false, Boolean.FALSE);
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new CopyProperty(pov);
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        new PropertyNode(pov, "deleteOnType", Types.BOOLEAN, null, false, Boolean.FALSE);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new PropertyNode(constr, "charsNumber", Types.INTEGER, null, false, null);
        new PropertyNode(constr, "formatPattern", Types.STRING, null, false, new String("#.#"));
        new PropertyNode(constr, "include", Types.STRING, null, false, null);
        new PropertyNode(constr, "exclude", Types.STRING, null, false, null);
        new CompConstraints(constr);
        //
        new CompObligation(this);
    }
}
