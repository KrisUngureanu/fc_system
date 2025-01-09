package kz.tamur.comps.models;

/**
 * The Class WebProperty.
 *
 * @author Lebedev Sergey
 */
public class WebProperty extends PropertyNode {
    
    /**
     * Конструктор класса web property.
     *
     * @param parent the parent
     */
    public WebProperty(PropertyNode parent) {
        super(parent, "web", -1, null, false, null);
        new PropertyNode(this, "showOnTopPan", Types.BOOLEAN, null, false, false);
        new PropertyNode(this, "positionOnTopPan", Types.INTEGER, null, false, null);
    }
}
