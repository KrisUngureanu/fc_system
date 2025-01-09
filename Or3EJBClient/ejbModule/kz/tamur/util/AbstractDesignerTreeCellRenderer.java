package kz.tamur.util;

//import kz.tamur.comps.Utils;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

import kz.tamur.rt.Utils;

/**
 * User: vital
 * Date: 02.02.2005
 * Time: 12:11:48
 */
public abstract class AbstractDesignerTreeCellRenderer 
        extends JLabel implements TreeCellRenderer {

    protected int dragRow = -1;

    public AbstractDesignerTreeCellRenderer() {
        super();
        setOpaque(true);
        setFont(Utils.getDefaultFont());
    }

    public boolean setDragRow(int dragRow) {
        if (this.dragRow != dragRow) {
            this.dragRow = dragRow;
            return true;
        } else {
            return false;
        }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        AbstractDesignerTreeNode val = (AbstractDesignerTreeNode)value;
        if (selected) {
            setBackground(Utils.getDarkShadowSysColor());
            setForeground(Color.white);
        } else {
            if (val.isCopyProcessStarted()) {
                setBackground(Utils.getSysColor());
            } else {
                setBackground(Utils.getLightSysColor());
                setForeground(Color.black);
            }
        }
        if (row == dragRow) {
            setBackground(Utils.getSysColor());
        }
        setText(value.toString());
        return this;
    }


}
