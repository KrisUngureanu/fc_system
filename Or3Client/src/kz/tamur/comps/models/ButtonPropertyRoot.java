package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;

import java.awt.GridBagConstraints;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class ButtonPropertyRoot extends PropertyRoot {
    public ButtonPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyNode titleExpr = new PropertyNode(new PropertyNode(this, "titleN", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(titleExpr);
        PropertyReestr.registerProperty(titleExpr);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        //Позиция
        CompPosition position = new CompPosition(this);
        EnumValue defAI = new EnumValue(GridBagConstraints.WEST, "Слева");
        EnumValue[] enumV = {defAI,
                new EnumValue(GridBagConstraints.EAST, "Справа"),
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"), };
        new PropertyNode(position, "anchorImage", ENUM, enumV, false, defAI);
        //Данные
        PropertyNode ref =
                new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "data", REF, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Button.background"));
        new CompBorder(view, UIManager.getBorder("Button.border"));
        EnumValue[] evs = {
            new EnumValue(SwingConstants.CENTER, "По центру"),
            new EnumValue(SwingConstants.LEFT, "Слева"),
            new EnumValue(SwingConstants.RIGHT, "Справа"),
        };
        new PropertyNode(view, "alignmentText", ENUM, evs, false,
                new Integer(SwingConstants.CENTER));
        new PropertyNode(view, "opaque", BOOLEAN, null, false, true);
        new PropertyNode(view, "image", IMAGE, null, false, null);
        PropertyNode lang =
                new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "autoRefresh", INTEGER, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, Boolean.TRUE);
        PropertyNode form = new PropertyNode(pov, "formula", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(form);
        PropertyReestr.registerProperty(form);
        new PropertyNode (pov, "tabIndex", INTEGER, null, false, null);
        new PropertyNode(pov, "defaultButton", BOOLEAN, null, false, false);
        new WebProperty(this);
    }
}
