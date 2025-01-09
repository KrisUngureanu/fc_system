package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;

import javax.swing.*;

/**
 * The Class LabelPropertyRoot.
 * 
 * @author Lebedev Sergey
 */
public class LabelPropertyRoot extends PropertyRoot {
    protected PropertyNode view;

    /**
     * Конструктор класса LabelPropertyRoot.
     */
    public LabelPropertyRoot() {
        this(null);
    }

    /**
     * Конструктор класса LabelPropertyRoot.
     * 
     * @param root_
     *            корневая ветка свойств, если <code>null</code> - корневым будет текущий объект.
     */
    public LabelPropertyRoot(PropertyRoot root_) {
        super();
        PropertyRoot root = root_ == null ? this : root_;
        new PropertyNode(root, "title", RSTRING, null, false, null);
        // Позиция
        new CompPosition(root);
        // Вид
        view = new PropertyNode(root, "view", -1, null, false, null);
        new FontProperty(view);
        new BackgroundProperty(view);
        EnumValue[] evs = {new EnumValue(JLabel.CENTER, "По центру"), new EnumValue(JLabel.LEFT, "Слева"), new EnumValue(JLabel.RIGHT, "Справа")};
        new PropertyNode(view, "alignmentText", ENUM, evs, false, null);
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(view, "image", IMAGE, null, false, null);
    }
}
