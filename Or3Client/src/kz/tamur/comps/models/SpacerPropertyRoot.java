package kz.tamur.comps.models;


public class SpacerPropertyRoot extends PropertyRoot {
    public SpacerPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new CompPosition(this);
    }
}
