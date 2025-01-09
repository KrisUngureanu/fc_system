package kz.tamur.comps.models;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class CoolDateFieldPropertyRoot extends PropertyRoot {
    public CoolDateFieldPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        new CompPosition(this);
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            new PropertyNode(refs, "data", Types.REF, null, false, null);
            new PropertyNode(refs, "copy", Types.REF, null, false, null);
        new PropertyNode(this, "editable", Types.BOOLEAN, null, false, null);
        PropertyNode lang =
                new PropertyNode(this, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new ParamFilters(this);
        new PropertyNode(this, "font", Types.FONT, null, false,
                UIManager.getFont("TextField.font"));
        PropertyNode colors = new PropertyNode(this, "colors", -1, null, false, null);
            new PropertyNode(colors, "fontColor", Types.COLOR, null, false,
                    UIManager.getColor("TextField.foreground"));
            new PropertyNode(colors, "backgroundColor", Types.COLOR, null, false,
                    UIManager.getColor("TextField.background"));
        new CompBorder(this, UIManager.getBorder("TextField.border"));
        //Ограничения
        PropertyNode constr = new CompConstraints(this);
        //new PropertyNode(constr, "charsNumber", Types.INTEGER, null, false, null);
        
    }
}
