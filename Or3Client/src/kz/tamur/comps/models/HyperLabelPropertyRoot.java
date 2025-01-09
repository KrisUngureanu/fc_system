package kz.tamur.comps.models;

import java.awt.*;

import kz.tamur.comps.Constants;

import static kz.tamur.comps.models.Types.*;

public class HyperLabelPropertyRoot extends PropertyRoot {
    public HyperLabelPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        // Позиция
        CompPosition position = new CompPosition(this);
        EnumValue defAI = new EnumValue(GridBagConstraints.EAST, "Справа");
        EnumValue[] enumV = { new EnumValue(GridBagConstraints.WEST, "Слева"), 
                defAI,
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"), };
        new PropertyNode(position, "anchorImage", ENUM, enumV, false, defAI);
        // Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(refs, "data", REF, null, false, null);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode font = new FontProperty(view);
        new PropertyNode(font, "lightFontColor", COLOR, null, false, Color.blue);
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(view, "image", IMAGE, null, false, null);
        new PropertyNode(view, "visibleArrow", BOOLEAN, null, false, true);
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        ActivProperty a = new ActivProperty(pov);
        new PropertyNode(a.getChildCount() - 2, a, "isArchiv", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(pov, "beforeOpen", EXPR, null, false, null);
        PropertyNode ui = new PropertyNode(pov, "interface", KRNOBJECT, null, false, null);
        ui.setKrnClass("UI", "title");
        new PropertyNode(pov, "dynamicIfc", REF, null, false, null);
        new PropertyNode(pov, "dynamicIfc_expr", EXPR, null, false, null);
        new PropertyNode(pov, "isBlockErrors", BOOLEAN, null, false, null);
        new PropertyNode(pov, "editIfc", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        
        EnumValue[] cash = { new EnumValue(Constants.CASH_SEPARATE, "Раздельный"),
                			 new EnumValue(Constants.CASH_GENERAL, "Общий"),
                			 new EnumValue(Constants.CASH_SEPARATE_NULL_TR, "Раздельный (0 транз.)"),
        };
        new PropertyNode(pov, "cashFlag", ENUM, cash, false, null)
        ;
        new WebProperty(this);
    }
}
