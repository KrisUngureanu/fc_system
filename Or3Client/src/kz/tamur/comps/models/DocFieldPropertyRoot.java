package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.ENUM;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.IMAGE;
import static kz.tamur.comps.models.Types.INTEGER;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;

import java.awt.GridBagConstraints;

import kz.tamur.comps.Constants;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class DocFieldPropertyRoot extends PropertyRoot {
    public DocFieldPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        new PropertyNode(this, "titleBeforeAttaching", RSTRING, null, false, null);
        new PropertyNode(this, "titleAfterAttaching", RSTRING, null, false, null);
        new PropertyNode(this, "iconBeforeAttaching", IMAGE, null, false, null);
        new PropertyNode(this, "iconAfterAttaching", IMAGE, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        // Позиция
        CompPosition position = new CompPosition(this);
        EnumValue defAI = new EnumValue(GridBagConstraints.WEST, "Слева");
        EnumValue[] enumV = {defAI,
                new EnumValue(GridBagConstraints.EAST, "Справа"),
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"), };
        new PropertyNode(position, "anchorImage", ENUM, enumV, false, defAI);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "data", REF, null, false, null);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        new BackgroundProperty(view);
        new CompBorder(view, UIManager.getBorder("Button.border"));
        EnumValue[] evs = { new EnumValue(SwingConstants.CENTER, "По центру"), new EnumValue(SwingConstants.LEFT, "Слева"),
                new EnumValue(SwingConstants.RIGHT, "Справа"), };
        new PropertyNode(view, "clearBtnShow", BOOLEAN, null, false, false);
        new PropertyNode(view, "alignmentText", ENUM, evs, false, SwingConstants.CENTER);
        new PropertyNode(view, "opaque", BOOLEAN, null, false, true);
        new PropertyNode(view, "image", IMAGE, null, false, null);
        new PropertyNode(view, "showUploaded", BOOLEAN, null, false, false);
        PropertyNode lang = new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, true);
        new PropertyNode(act, "dontDependNull", BOOLEAN, null, false, false);
        EnumValue[] env = new EnumValue[] { new EnumValue(Constants.DOC_VIEW, "Просмотр"),
                new EnumValue(Constants.DOC_UPDATE, "Загрузка"), new EnumValue(Constants.DOC_EDIT, "Редактирование"), 
                new EnumValue(Constants.DOC_PRINT, "Печать"), new EnumValue(Constants.DOC_UPDATE_VIEW, "Загрузка-Просмотр")};
        new PropertyNode(pov, "action", ENUM, env, false, null);
        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        new PropertyNode(pov, "beforeOpenAction", EXPR, null, false, null);
        new PropertyNode(pov, "beforeModAction", EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", EXPR, null, false, null);
        // Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        new PropertyNode(constr, "maxSize2", INTEGER, null, false, null);
        new PropertyNode(constr, "extensions", RSTRING, null, false, null);
        // Обязательность
        new CompObligation(this);
        new WebProperty(this);
        new PropertyNode(this, "multipleFile", BOOLEAN, null, false, false);
    }
}
