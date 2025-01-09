package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;
import static javax.swing.SwingConstants.*;

import javax.swing.SwingConstants;

/**
 * The Class AccordionPropertyRoot.
 * 
 * @author Lebedev Sergey
 */
public class AccordionPropertyRoot extends PropertyRoot {

    /**
     * Конструктор класса AccordionPropertyRoot.
     */
    public AccordionPropertyRoot() {
        super();
        PropertyNode t = new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyReestr.registerProperty(t);
        
        PropertyNode title = new PropertyNode(this, "titleN", -1, null, false, null);
        EnumValue defAlignmentText = new EnumValue(SwingConstants.CENTER, "По центру");
        EnumValue[] alignmentText = { defAlignmentText, new EnumValue(SwingConstants.LEFT, "Слева"), new EnumValue(SwingConstants.RIGHT, "Справа") };
        // Выравнивание текста заголовка
        new PropertyNode(title, "alignmentText", ENUM, alignmentText, false, defAlignmentText);

        EnumValue defOrientation = new EnumValue(VERTICAL, "Вертикальный");
        EnumValue[] orientation = { defOrientation, new EnumValue(HORIZONTAL, "Горизонтальный") };
        // Ориентация компонента
        new PropertyNode(title, "orientation", ENUM, orientation, false, defOrientation);
        // Гарнитура и цвет шрифта заголовка
        FontProperty font = new FontProperty(title);
        // панели
        new PropertyNode(title, "countPanel", INTEGER, null, false, 3);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        new PropertyNode(this, "panels", COMPONENT, null, false, null);
        // Позиция
        CompPosition pos = new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(ref, "data", REF, null, false, null);
        PropertyReestr.registerProperty(data);

        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "multiselection", BOOLEAN, null, false, false);
        new PropertyNode(pov, "expandPanel", INTEGER, null, false, false);
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
        // pos.removeChild("fill");
        // pos.removeChild("pref");
        // pos.removeChild("max");
        // pos.removeChild("min");
    }
}
