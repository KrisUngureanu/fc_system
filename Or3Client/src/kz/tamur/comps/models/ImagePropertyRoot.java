package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.ENUM;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class ImagePropertyRoot extends PropertyRoot {
    public ImagePropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "data", Types.REF, null, false, null);
        
        EnumValue sizeDef = new EnumValue(0, "нет");
        EnumValue[] sizes = { sizeDef, new EnumValue(100, "100KB"), new EnumValue(200, "200KB"), new EnumValue(500, "500KB"),
                new EnumValue(700, "700KB"), new EnumValue(1024, "1MB"), new EnumValue(1536, "1.5MB"), new EnumValue(2048, "2MB") };
        new PropertyNode(ref, "maxSize", ENUM, sizes, false, sizeDef);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new CompBorder(view, UIManager.getBorder("Label.border"));
        new PropertyNode(view, "image", Types.IMAGE, null, false, null);
        new PropertyNode(view, "autoresize", Types.BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode activ = new PropertyNode(pov, "activity", -1, null, false, null);
        new PropertyNode(activ, "editable", Types.BOOLEAN, null, false, Boolean.FALSE);
        
        // Поведение перед и после модификации
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        PropertyNode pn = new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        PropertyReestr.registerProperty(pn);
        PropertyReestr.registerDebugProperty(pn);
        
        // Обязательность
        new CompObligation(this);
    }
}
