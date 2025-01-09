package kz.tamur.comps.models;

import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 02.04.2004
 * Time: 17:15:07
 * To change this template use File | Settings | File Templates.
 */
public class CompPosition extends PropertyNode {
    public CompPosition(PropertyNode parent) {
        super(parent, "pos", -1, null, false, null);
        // GridBag constraints
        new PropertyNode(this, "x", Types.INTEGER, null, false, null);
        new PropertyNode(this, "y", Types.INTEGER, null, false, null);
        new PropertyNode(this, "width", Types.INTEGER, null, false, null);
        new PropertyNode(this, "height", Types.INTEGER, null, false, null);
        new PropertyNode(this, "weightx", Types.DOUBLE, null, false, null);
        new PropertyNode(this, "weighty", Types.DOUBLE, null, false, null);
        EnumValue[] evs = {
            new EnumValue(GridBagConstraints.NONE, "нет"),
            new EnumValue(GridBagConstraints.HORIZONTAL, "горизонтальное"),
            new EnumValue(GridBagConstraints.VERTICAL, "вертикальное"),
            new EnumValue(GridBagConstraints.BOTH, "полное")
        };
        new PropertyNode(this, "fill", Types.ENUM, evs, false,
                new Integer(GridBagConstraints.NONE));
        EnumValue[] evs1 = {
            new EnumValue(GridBagConstraints.CENTER, "По центру"),
            new EnumValue(GridBagConstraints.WEST, "Слева"),
            new EnumValue(GridBagConstraints.EAST, "Справа"),
            new EnumValue(GridBagConstraints.NORTH, "Сверху"),
            new EnumValue(GridBagConstraints.SOUTH, "Снизу"),
            new EnumValue(GridBagConstraints.NORTHWEST, "Сверху слева"),
            new EnumValue(GridBagConstraints.NORTHEAST, "Сверху справа"),
            new EnumValue(GridBagConstraints.SOUTHWEST, "Снизу слева"),
            new EnumValue(GridBagConstraints.SOUTHEAST, "Снизу справа"),
        };
        new PropertyNode(this, "anchor", Types.ENUM, evs1, false, 
                new Integer(GridBagConstraints.CENTER));

        //Insets
        PropertyNode ins = new PropertyNode(this, "insets", -1, null, false, null);
            new PropertyNode(ins, "topInsets", Types.INTEGER, null, false, null);
            new PropertyNode(ins, "leftInsets", Types.INTEGER, null, false, null);
            new PropertyNode(ins, "bottomInsets", Types.INTEGER, null, false, null);
            new PropertyNode(ins, "rightInsets", Types.INTEGER, null, false, null);
        // Preferred Size
        PropertyNode prsz = new PropertyNode(this, "pref", -1, null, false, null);
        new PropertyNode(prsz, "width", Types.INTEGER, null, false, null);
        new PropertyNode(prsz, "height", Types.INTEGER, null, false, null);
        // Max size
        PropertyNode mxsz = new PropertyNode(this, "max", -1, null, false, null);
        new PropertyNode(mxsz, "width", Types.INTEGER, null, false, null);
        new PropertyNode(mxsz, "height", Types.INTEGER, null, false, null);
        // Min size
        PropertyNode mnsz = new PropertyNode(this, "min", -1, null, false, null);
        new PropertyNode(mnsz, "width", Types.INTEGER, null, false, null);
        new PropertyNode(mnsz, "height", Types.INTEGER, null, false, null);
    }
}
