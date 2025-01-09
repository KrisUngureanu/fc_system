package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class PopUpPanelPropertyRoot extends PropertyRoot {

    public PopUpPanelPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        new PropertyNode(this, "panel", COMPONENT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "data", REF, null, false, null);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Button.background"));
        new CompBorder(view, UIManager.getBorder("Button.border"));
        EnumValue[] evs = { new EnumValue(SwingConstants.CENTER, "По центру"), new EnumValue(SwingConstants.LEFT, "Слева"),
                new EnumValue(SwingConstants.RIGHT, "Справа"), };
        new PropertyNode(view, "alignmentText", ENUM, evs, false, new Integer(SwingConstants.CENTER));
        new PropertyNode(view, "image", IMAGE, null, false, null);
        new PropertyNode(view, "opaque", BOOLEAN, null, false, true);
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode act = new ActivProperty(pov);
        act.removeChild("editable");
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode beforeOpen = new PropertyNode(pov, "beforeOpen", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(beforeOpen);
        PropertyReestr.registerProperty(beforeOpen);

        PropertyNode afterOpen = new PropertyNode(pov, "afterOpen", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(afterOpen);
        PropertyReestr.registerProperty(afterOpen);

        PropertyNode beforeClose = new PropertyNode(pov, "beforeClose", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(beforeClose);
        PropertyReestr.registerProperty(beforeClose);

        PropertyNode afterClose = new PropertyNode(pov, "afterClose", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(afterClose);
        PropertyReestr.registerProperty(afterClose);

        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        new PropertyNode(pov, "defaultButton", BOOLEAN, null, false, false);
        new PropertyNode(pov, "hideAfterClick", BOOLEAN, null, false, true);
        new PropertyNode(pov, "isShowAsMenu", BOOLEAN, null, false, false);
        new WebProperty(this);
    }

}
