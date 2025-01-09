package kz.tamur.comps.models;

public class HyperTreePropertyRoot extends PropertyRoot {
    public HyperTreePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //new CompPosition(this);
        //PropertyNode hprop =
        //        new PropertyNode(this, "hipertree", Types.KRNOBJECT, null, false, null);
        //hprop.setKrnClass("HiperTree", "title");
        PropertyNode ifc = new PropertyNode(this, "interface", Types.KRNOBJECT, 
                null, true, null);
        ifc.setKrnClass("UI", "title");
    }
}
