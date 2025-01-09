package kz.tamur.guidesigner.warnings;

import java.awt.Component;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;

public class PathWarningsListItem extends Component {

    private String message;
    private PropertyNode property;
    private OrGuiComponent component;
    
    public PathWarningsListItem(String message, PropertyNode property, OrGuiComponent component) {
        super();
        this.message = message;
        this.property = property;
        this.component = component;
    }
    
    public String toString() {
        return (message != null) ? message : "";
    }

    public PropertyNode getProperty() {
        return property;
    }
    
    public OrGuiComponent getComponent() {
        return component;
    }
}