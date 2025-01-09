package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.ENUM;

import java.awt.GridBagConstraints;

import javax.swing.SwingConstants;
import javax.swing.UIManager;


public class NotePropertyRoot extends PropertyRoot {
    public NotePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
      //Позиция
        CompPosition position = new CompPosition(this);
        EnumValue defAI = new EnumValue(GridBagConstraints.WEST, "Слева");
        EnumValue[] enumV = {defAI,
                new EnumValue(GridBagConstraints.EAST, "Справа"),
                new EnumValue(GridBagConstraints.NORTH, "Сверху"),
                new EnumValue(GridBagConstraints.SOUTH, "Снизу"), };
        new PropertyNode(position, "anchorImage", ENUM, enumV, false, defAI);
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
        new PropertyNode(view, "alignmentText", Types.ENUM, evs, false,
                new Integer(SwingConstants.CENTER));
        new PropertyNode(view, "opaque", BOOLEAN, null, false, true);
        new PropertyNode(view, "image", Types.IMAGE, null, false, null);
        PropertyNode lang = new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode item = new PropertyNode(pov, "spravInterface", Types.KRNOBJECT, null, false, null);
        item.setKrnClass("Note","title");
        new WebProperty(this);
    }
}
