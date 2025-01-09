package kz.tamur.comps.models;

public class PropertyRoot extends PropertyNode {

    public PropertyRoot() {
        super(null, "Root", -1, null, false, null);
        PropertyNode varName = new PropertyNode(this, "varName", Types.STRING, null, false, null);
        new PropertyNode(this, "UUID", Types.VIEW_STRING, null, false, null);
        PropertyReestr.registerDebugProperty(varName);
        PropertyReestr.registerProperty(varName);
    }
}