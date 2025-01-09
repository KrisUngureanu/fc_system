package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;
import static javax.swing.SwingConstants.*;

import javax.swing.SwingConstants;

/**
 * The Class CollapsiblePanelPropertyRoot.
 * 
 * @author Lebedev Sergey
 */
public class CollapsiblePanelPropertyRoot extends PropertyRoot {

    /**
     * Конструктор класса CollapsiblePanelPropertyRoot.
     */
    public CollapsiblePanelPropertyRoot() {
        super();
        PropertyNode t = new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyReestr.registerProperty(t);
        PropertyNode title = new PropertyNode(this, "titleN", -1, null, false, null);

        EnumValue defAlignmentText = new EnumValue(SwingConstants.CENTER, "По центру");
        EnumValue[] alignmentText = { defAlignmentText, new EnumValue(SwingConstants.LEFT, "Слева"), new EnumValue(SwingConstants.RIGHT, "Справа") };
        
        // Выравнивание текста заголовка
        new PropertyNode(title, "alignmentText", ENUM, alignmentText, false, defAlignmentText);

        EnumValue defTitleAlign = new EnumValue(TOP, "Сверху");
        EnumValue[] titleAlign = { defTitleAlign, new EnumValue(BOTTOM, "Снизу"), new EnumValue(LEFT, "Слева"),
                new EnumValue(RIGHT, "Справа") };
        // Местоположение заголовка
        new PropertyNode(title, "titleAlign", ENUM, titleAlign, false, defTitleAlign);
        // Гарнитура и цвет шрифта заголовка
        FontProperty font = new FontProperty(title);

        new PropertyNode(title, "icon", IMAGE, null, false, null);

        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        new PropertyNode(this, "panel", COMPONENT, null, false, null);
        // Позиция
        CompPosition pos = new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(ref, "data", REF, null, false, null);
        PropertyReestr.registerProperty(data);
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "expandAll", BOOLEAN, null, false, false);
        PropertyNode act = new ActivProperty(pov);
        new PropertyNode(act.getChildCount() - 2, act, "enabled", BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        new PropertyNode(this, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(this, "transparent", BOOLEAN, null, false, false);

        // удалить ненужные свойства
        act.removeChild("editable");
        font.removeChild("fontGExpr");
        font.removeChild("fontExpr");
        //pos.removeChild("fill");
        //pos.removeChild("pref");
        //pos.removeChild("max");
        //pos.removeChild("min");
    }
}
