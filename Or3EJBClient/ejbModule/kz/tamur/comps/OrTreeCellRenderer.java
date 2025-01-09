package kz.tamur.comps;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

import kz.tamur.rt.adapters.TreeAdapter2.Node;

/**
 * User: vital
 * Date: 18.02.2005
 * Time: 9:56:38
 */
class OrTreeCellRenderer extends JLabel implements TreeCellRenderer {

    private boolean isTreeTable;
    private boolean isFolderAsLeaf;
    private boolean isOpaque = true;
    private Font font;

    public OrTreeCellRenderer(boolean treeTable, boolean transparent, boolean isFolderAsLeaf, Font font) {
        isTreeTable = treeTable;
        this.isFolderAsLeaf = isFolderAsLeaf;
        isOpaque = !transparent;
        this.font = font == null ? kz.tamur.rt.Utils.getDefaultFont() : font;
    }

    public OrTreeCellRenderer(boolean treeTable, boolean transparent, boolean isFolderAsLeaf) {
        this(treeTable, transparent, isFolderAsLeaf, kz.tamur.rt.Utils.getDefaultFont());
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        setForeground(isTreeTable ? Color.black : selected ? Color.white : Color.black);
        setBackground(selected ? kz.tamur.rt.Utils.getDarkShadowSysColor() : tree.getBackground());
        setOpaque(selected || isOpaque);
        setFont(font);

        int viewType = Constants.FILES;
        if (tree instanceof OrTree2) {
            viewType = ((OrTree2) tree).getViewType();
        } else if (tree instanceof OrTree) {
            viewType = ((OrTree) tree).getViewType();
        }

        if (!leaf && !isFolderAsLeaf) {
            if (viewType > Constants.FILES) {
                setIcon(kz.tamur.rt.Utils.getImageIconFull("groupNode.png"));
            } else {
                setIcon(expanded ? kz.tamur.rt.Utils.getImageIcon("Open") : kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
            }
        } else {
            setIcon(isTreeTable && leaf ? null : kz.tamur.rt.Utils.getImageIcon("Create"));
        }
        setText(value instanceof Node ? ((Node) value).toString(row) : value.toString());

        return this;
    }
}
