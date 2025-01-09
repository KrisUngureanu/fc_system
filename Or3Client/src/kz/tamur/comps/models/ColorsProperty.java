package kz.tamur.comps.models;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 27.05.2004
 * Time: 15:21:59
 * To change this template use File | Settings | File Templates.
 */
public class ColorsProperty extends PropertyNode {

    public ColorsProperty(PropertyNode parent) {
        super(parent, "colors", -1, null, false, null);
        new PropertyNode(this, "fontColor", Types.COLOR, null, false,
                UIManager.getColor("TextField.foreground"));
        new PropertyNode(this, "fontColorExpr", Types.EXPR, null, false,
                UIManager.getColor("TextField.foreground"));
        new PropertyNode(this, "backgroundColor", Types.COLOR, null, false,
                UIManager.getColor("TextField.background"));
        new PropertyNode(this, "backgroundColorExpr", Types.EXPR, null, false,
                UIManager.getColor("TextField.background"));

    }
}
