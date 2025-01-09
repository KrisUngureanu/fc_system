package kz.tamur.guidesigner.users;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 17:04:53
 * To change this template use File | Settings | File Templates.
 */
public class Or3RightsTree extends JTree implements MouseListener {

    private static Icon trueIcon;
    private static Icon falseIcon;

    static {
        JCheckBox chb = new JCheckBox();
        chb.setOpaque(false);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setBounds(0, 0, 20, 16);
        chb.paint(img.createGraphics());
        falseIcon = new ImageIcon(img);

        img = gc.createCompatibleImage(20, 16, Transparency.BITMASK);
        chb.setSelected(true);
        chb.paint(img.createGraphics());
        trueIcon = new ImageIcon(img);
    }

    private Or3RightsNode root;
    private Or3RightsTreeModel model;

    public Or3RightsTree(final Or3RightsNode root) {
        super(root, false);
        this.root = root;
        root.calculate();
        model = new Or3RightsTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        addMouseListener(this);
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
    }

    public Element getOr3Rights() {
        root.calculate();
        return root.getXml();
    }

    public String toString() {
        return root.toString();
    }

    public void mouseClicked(MouseEvent e) {
        TreePath path = getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            Or3RightsNode node = (Or3RightsNode)path.getLastPathComponent();
            if (node.isLeaf()) {
                node.setChecked(!node.isChecked());
                model.fireTreeNodesChanged(this, new Object[] {node}, null, null);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public class Or3RightsTreeModel extends DefaultTreeModel {

        private Or3RightsNode rootNode;

        public Or3RightsTreeModel(Or3RightsNode root) {
            super(root);
            rootNode = root;
        }

        protected void fireTreeNodesChanged(Object source, Object[] path,
                                            int[] childIndices, Object[] children) {
            super.fireTreeNodesChanged(source, path, childIndices, children);
        }
    }

    private class CellRenderer extends JLabel implements TreeCellRenderer {

        public CellRenderer() {
            super();
            setOpaque(true);
            Font fnt = kz.tamur.rt.Utils.getDefaultFont();
            setFont(fnt);
            setBackground(kz.tamur.rt.Utils.getLightSysColor());
            setForeground(Color.black);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            Or3RightsNode node = (Or3RightsNode)value;
            if (node.isLeaf()) {
                setIcon(node.isChecked() ? trueIcon : falseIcon);
            } else {
                setIcon(null);
            }
            setText(node.getName());
            return this;
        }

    }
}
