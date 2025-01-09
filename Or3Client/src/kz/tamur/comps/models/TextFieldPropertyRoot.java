package kz.tamur.comps.models;

import javax.swing.*;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class TextFieldPropertyRoot extends PropertyRoot {
    public TextFieldPropertyRoot() {
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
        EnumValue[] evs = {new EnumValue(SwingConstants.CENTER, "По центру"), new EnumValue(SwingConstants.LEFT, "Слева"), new EnumValue(SwingConstants.RIGHT, "Справа")};
        /* 
         * Выравнивание текста в поле, дополнительная функциональность в том что если 
         * текст превышает размер поля  - текст должен вырвниваться аналогично данному 
         * свойству, но тут уже используется установка позиции каретки.
         * 
         * */
        new PropertyNode(view, "alignmentText", Types.ENUM, evs, false, null);
        new PropertyNode(view, "showAllText", Types.BOOLEAN, null, false, Boolean.FALSE);
        new FontProperty(view);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("TextField.background"));

        new CompBorder(view, UIManager.getBorder("TextField.border"));
        PropertyNode lang = new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(view, "langExpr", Types.EXPR, null, false, null);
        new PropertyNode(view, "typeEmail", Types.BOOLEAN, null, false, Boolean.FALSE);
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new CopyProperty(pov);
        new PropertyNode(pov, "deleteOnType", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        PropertyNode pn = new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        PropertyReestr.registerProperty(pn);
        PropertyReestr.registerDebugProperty(pn);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new PropertyNode(constr, "upperAllChar", Types.BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(constr, "upperCase", Types.BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(constr, "include", Types.STRING, null, false, null);
        new PropertyNode(constr, "exclude", Types.STRING, null, false, null);
        new PropertyNode(constr, "charsNumber", Types.INTEGER, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
    }
}
