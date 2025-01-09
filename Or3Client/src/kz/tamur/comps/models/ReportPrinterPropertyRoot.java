package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class ReportPrinterPropertyRoot extends PropertyRoot {
    public ReportPrinterPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.MSTRING, null, false, null);
        new PropertyNode(this, "titleKaz", Types.MSTRING, null, false, null);
        //new CompPosition(this);
        //PropertyNode ref =
        //        new PropertyNode(this, "ref", null, null);
        //new PropertyNode(ref, "data", Types.REF, null);
        //new PropertyNode(this, "enabled", Types.BOOLEAN, null, false, null);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.MSWORD_EDITOR, "Microsoft Word"),
            new EnumValue(Constants.MSEXCEL_EDITOR, "Microsoft Excel"),
            new EnumValue(Constants.JASPER_EDITOR, "Jasper Reports")
        };
        new PropertyNode(this, "editorType", Types.ENUM, env, false, null);
        new PropertyNode(this, "groupType", Types.BOOLEAN, null, false, Boolean.FALSE);

        new PropertyNode(this, "macros", Types.STRING, null, false, null);
        new PropertyNode(this, "templatePassword", Types.STRING, null, false, null);

        PropertyNode n = new PropertyNode(this, "bases", Types.KRNOBJECT, null, true, null);
        n.setKrnClass("Структура баз", "наименование");
        //PropertyNode n = new PropertyNode(this, "baseStructure", Types.KRNOBJECT,
        //        null, true, null);
        //n.setKrnClass("Структура баз", "наименование");
    }
}
