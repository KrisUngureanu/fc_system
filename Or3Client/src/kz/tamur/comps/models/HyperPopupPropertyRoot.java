package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

import javax.swing.*;

import java.awt.*;

import static kz.tamur.comps.models.Types.*;

public class HyperPopupPropertyRoot extends PropertyRoot {
    public HyperPopupPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        
        // Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(refs, "data", REF, null, false, null);
        new PropertyNode(refs, "titlePath", REF, null, false, null);
        new PropertyNode(refs, "titlePathExpr", EXPR, null, false, null);
        new PropertyNode(refs, "content", REF, null, false, null);
        new PropertyNode(refs, "selectedRef", REF, null, false, null);
        new PropertyNode(refs, "contentFilter", FILTER, null, false, null);
        new ParamFilters(refs);
        EnumValue[] env = new EnumValue[] { new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
                new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
                new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.") };
        new PropertyNode(refs, "refreshMode", ENUM, env, false, null);
        // Добавили новое свойство - Свойство: Данные.Содержимое формула
        new PropertyNode(refs, "contentCalc", EXPR, null, false, null);
        
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode font = new FontProperty(view);
        PropertyNode col1 = font.getChild("fontColor");
        col1.setDefaultValue(UIManager.getColor("Button.foreground"));
        new PropertyNode(font, "lightFontColor", COLOR, null, false, Color.blue);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Button.background"));

        new CompBorder(view, UIManager.getBorder("Button.border"));
        // Позиция
        CompPosition position = new CompPosition(this);
        
        EnumValue defAI = new EnumValue(GridBagConstraints.EAST, "Справа");
        EnumValue[] enumV = { new EnumValue(GridBagConstraints.WEST, "Слева"), 
                defAI,
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"), };
        new PropertyNode(position, "anchorImage", ENUM, enumV, false, defAI);
        
        new PropertyNode(view, "image", IMAGE, null, false, null);
        EnumValue[] evs = { new EnumValue(SwingConstants.CENTER, "По центру"), new EnumValue(SwingConstants.LEFT, "Слева"),
                new EnumValue(SwingConstants.RIGHT, "Справа"), };
        new PropertyNode(view, "alignmentText", ENUM, evs, false, new Integer(SwingConstants.CENTER));
        new PropertyNode(view, "opaque", BOOLEAN, null, false, true);
        new PropertyNode(view, "clearBtnShow", BOOLEAN, null, false, false);
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(view, "showIcon", BOOLEAN, null, true, false);

        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        // Поведение.Действие
        PropertyNode act = new PropertyNode(pov, "act", -1, null, false, null);
        new PropertyNode(act, "beforeOpen", EXPR, null, false, null);
        new PropertyNode(act, "actionJobBefore", EXPR, null, false, null);
        new PropertyNode(act, "actionJobAfter", EXPR, null, false, null);
        new PropertyNode(act, "actionJobBeforClear", EXPR, null, false, null);
        PropertyNode callDialog = new PropertyNode(act, "callDialog", KRNOBJECT, null, false, null);
        callDialog.setKrnClass("UI", "title");
        EnumValue[] action = new EnumValue[] { new EnumValue(Constants.CHANGE_ACTION, "Изменять"),
                new EnumValue(Constants.ADD_ACTION, "Добавлять") };
        new PropertyNode(pov, "charModification", ENUM, action, false, null);
        EnumValue[] cash = new EnumValue[] { new EnumValue(Constants.CASH_SEPARATE, "Раздельный"),
                new EnumValue(Constants.CASH_GENERAL, "Общий"), };
        new PropertyNode(pov, "cashFlag", ENUM, cash, false, null);
        new PropertyNode(pov, "ifcLock", BOOLEAN, null, false, null);
        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        new PropertyNode(pov, "dynamicIfc", REF, null, false, null);
        new PropertyNode(pov, "dynamicIfcExpr", EXPR, null, false, null);
        new PropertyNode(pov, "fork", BOOLEAN, null, false, false);

        EnumValue typeViewDef = new EnumValue(Constants.DIALOG, "Диалог");
        EnumValue[] typeView = new EnumValue[] { typeViewDef, new EnumValue(Constants.FRAME, "Окно"), };
        new PropertyNode(pov, "typeView", ENUM, typeView, false, typeViewDef);
        new PropertyNode(pov, "wrapStyleWord", Types.BOOLEAN, null, false, Boolean.FALSE);

        // Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        new PropertyNode(constr, "formatPattern", STRING, null, false, new String("#.#"));
        // Обязательность
        new CompObligation(this);
        new WebProperty(this);
    }
}
