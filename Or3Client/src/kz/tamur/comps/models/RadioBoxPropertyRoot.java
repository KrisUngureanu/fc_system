package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

import javax.swing.*;


public class RadioBoxPropertyRoot extends PropertyRoot {
    public RadioBoxPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            new PropertyNode(refs, "data", Types.REF, null, false, null);
            new PropertyNode(refs, "content", Types.REF, null, false, null);
            new PropertyNode(refs, "defaultFilter", Types.FILTER, null, false, null);
            new ParamFilters(refs);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
            new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
            new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.")
        };
        new PropertyNode(refs, "refreshMode", Types.ENUM, env, false, null);
        new PropertyNode(refs, "calcData", Types.EXPR, null, false, null);

        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        new BackgroundProperty(view);
        new CompBorder(view, BorderFactory.createEmptyBorder(1, 1, 1, 1));
        new PropertyNode(view, "columncount", Types.INTEGER, null, false, null);
        new PropertyNode(view, "sort", Types.BOOLEAN, null, false, null);
        PropertyNode lang =
            new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
    }
}
