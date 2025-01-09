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
public class ComboBoxPropertyRoot extends PropertyRoot {
    public ComboBoxPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            new PropertyNode(refs, "data", Types.REF, null, false, null);
            PropertyNode content = new PropertyNode(refs, "content", Types.REF, null, false, null);
            PropertyReestr.registerProperty(content);
            PropertyReestr.registerDebugProperty(content);
            PropertyNode contentSort = new PropertyNode(refs, "contentSort", Types.REF, null, false, null);
            PropertyReestr.registerProperty(contentSort);
            PropertyReestr.registerDebugProperty(contentSort);
            PropertyNode contentCalc = new PropertyNode(refs, "contentCalc", Types.EXPR, null, false, null);
            PropertyReestr.registerProperty(contentCalc);
            PropertyReestr.registerDebugProperty(contentCalc);
            new PropertyNode(refs, "hintTitle", Types.REF, null, false, null);
            new PropertyNode(refs, "defaultFilter", Types.FILTER, null, false, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
            new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
            new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.")
        };
        new PropertyNode(refs, "refreshMode", Types.ENUM, env, false, null);
        new ParamFilters(refs);
        new PropertyNode(refs, "calcData", Types.EXPR, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        EnumValue[] styleVs = {
                new EnumValue(Constants.VIEW_SIMPLE_COMBO, "Обычный комбобокс"),
                new EnumValue(Constants.VIEW_LIST, "Список"),
                new EnumValue(Constants.VIEW_CHECKBOX_LIST, "Набор чекбоксов"),
                new EnumValue(Constants.VIEW_RADIOBOX_LIST, "Набор радиобоксов"),
                new EnumValue(Constants.VIEW_SIMPLE_AND_LIST, "Обычный или список"),
                new EnumValue(Constants.VIEW_SOLID_LIST, "Твердый список"),
            };
        new PropertyNode(view, "appearance", Types.ENUM, styleVs, false, Constants.VIEW_SIMPLE_COMBO);

        EnumValue[] evs = {
                new EnumValue(DefaultListCellRenderer.CENTER, "По центру"),
                new EnumValue(DefaultListCellRenderer.LEFT, "Слева"),
                new EnumValue(DefaultListCellRenderer.RIGHT, "Справа"),
            };
        // TODO реализация приостановлена
        /* выравнивание текста в поле, дополнительная функциональность в том что если 
         * текст превышает размер поля  - текст должен вырвниваться аналогично данному 
         * свойству, но тут уже используется установка позиции каретки*/
        new PropertyNode(view, "alignmentText", Types.ENUM, evs, false, null);
        new FontProperty(view);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("ComboBox.background"));

        new CompBorder(view, BorderFactory.createEmptyBorder(1, 1, 1, 1));
        new PropertyNode(view, "combonotsorted", Types.BOOLEAN, null, false, null);
        PropertyNode lang =
                new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(view, "comboSearch", Types.BOOLEAN, null, false, null);
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
        

    }
}
