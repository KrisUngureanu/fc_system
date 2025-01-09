package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;

import java.awt.GridBagConstraints;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 16:54:07
 * To change this template use File | Settings | File Templates.
 */
public class ImagePanelPropertyRoot extends PropertyRoot {
    public ImagePanelPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        PropertyReestr.registerDebugProperty(title);
        PropertyReestr.registerProperty(title);
        PropertyNode titleExpr = new PropertyNode(new PropertyNode(this, "title1", -1, null, false, null), "expr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(titleExpr);
        PropertyReestr.registerProperty(titleExpr);
        EnumValue defTitleAlign = new EnumValue(GridBagConstraints.CENTER, "По центру");
        EnumValue[] titleAlign = {
                defTitleAlign,
                new EnumValue(GridBagConstraints.WEST, "Слева"),
                new EnumValue(GridBagConstraints.EAST, "Справа"),
            };
        new PropertyNode(this, "titleAlign", Types.ENUM, titleAlign, false, defTitleAlign);
        
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode ref = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "tableRef", Types.REF, null, false, null);
        new PropertyNode(ref, "imageRef", Types.REF, null, false, null);
        new PropertyNode(ref, "titleRef", Types.REF, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new CompBorder(view, UIManager.getBorder("Label.border"));
        new PropertyNode(view, "image", Types.IMAGE, null, false, null);
        new PropertyNode(view, "autoresize", Types.BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Panel.background"));
        
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent",  BOOLEAN, null, false, false);

        EnumValue[] vals = {
                new EnumValue(1, "Горизонтальная"),
                new EnumValue(2, "Вертикальная"),
            };
        new PropertyNode(view, "orientation", Types.ENUM, vals, false, 1);
        new PropertyNode(view, "imageUID", Types.STRING, null, false, null);

        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        PropertyNode activ = new PropertyNode(pov, "activity", -1, null, false, null);
        new PropertyNode(activ, "enabled", Types.BOOLEAN, null, false, Boolean.TRUE);
    }
}
