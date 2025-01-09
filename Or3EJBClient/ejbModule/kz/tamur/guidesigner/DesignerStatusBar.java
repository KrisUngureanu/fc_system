package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;

import javax.swing.*;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 07.05.2004
 * Time: 17:08:08
 * To change this template use File | Settings | File Templates.
 */
public class DesignerStatusBar extends JPanel {

    private JLabel statusCorner = new JLabel(kz.tamur.rt.Utils.getImageIcon("StatusCorner"));
    private JComponent firstComponent = new JLabel();

    public DesignerStatusBar() {
        super(new GridBagLayout());
        setBackground(Utils.getLightSysColor());
        setFont(Utils.getDefaultFont());
        setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        setPreferredSize(new Dimension(20, 22));
        add(firstComponent, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(0, 2, 0, 2), 0, 0));
        addSeparator();
    }

    private int getComponentIdx() {
        int count = getComponentCount();
        return count;
    }

    public void addEmptySpace() {
        JLabel label = new JLabel();
        add(label, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 1, 0, CENTER, BOTH, new Insets(0, 2, 0, 2), 0, 0));
    }
    
    public void addLabel(JLabel label) {
    	label.setFont(Utils.getDefaultFont());
        label.setForeground(Utils.getDarkShadowSysColor());
        add(label, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(0, 2, 0, 2), 0, 0));
    }

    public void addLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(Utils.getDefaultFont());
        label.setForeground(Utils.getDarkShadowSysColor());
        add(label, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(0, 2, 0, 2), 0, 0));
    }

    public void addTextField(JTextField tf) {
        addTextField(tf, 1);
    }

    public void addTextField(JTextField tf, double weight) {
        tf.setFont(Utils.getDefaultFont());
        tf.setForeground(Utils.getDarkShadowSysColor());
        tf.setBackground(Utils.getLightSysColor());
        tf.setEditable(false);
        tf.setBorder(null);
        tf.setSelectedTextColor(Utils.getLightSysColor());
        tf.setSelectionColor(Utils.getDarkShadowSysColor());

        add(tf, new GridBagConstraints(getComponentIdx(), 0, 1, 1, weight, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    }

    public void addAnyComponent(JComponent c) {
        add(c, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    }

    public void addAnyComponent(JComponent c, double weight) {
        add(c, new GridBagConstraints(getComponentIdx(), 0, 1, 1, weight, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    }

    public void addSeparator() {
        StatusSeparator sep = new StatusSeparator();
        add(sep, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(0, 5, 0, 5), 0, 0));
    }

    public void addCorner() {
        if (getComponentIdx() == 0) {
            addEmptySpace();
        }
        add(statusCorner, new GridBagConstraints(getComponentIdx(), 0, 1, 1, 0, 0, CENTER, BOTH, Constants.INSETS_1, 0, 0));
    }

    class StatusSeparator extends JLabel {
        public StatusSeparator() {
            super("1");
            setPreferredSize(new Dimension(10, DesignerStatusBar.this.getHeight()));
            setOpaque(false);
        }

        public void paint(Graphics g) {
            int s = this.getHeight();
            Color oldColor = g.getColor();
            g.setColor(Utils.getDarkShadowSysColor());
            g.drawLine(5, 0, 5, s);
            g.setColor(Color.white);
            g.drawLine(6, 0, 6, s);
            g.setColor(oldColor);
        }
    }

    /**
     * @return the firstComponent
     */
    public JComponent getFirstComponent() {
        return firstComponent;
    }

    /**
     * @param firstComponent
     *            the firstComponent to set
     */
    public void setFirstComponent(JComponent component) {
        remove(firstComponent);
        if (component.getParent() != null) {
            component.getParent().remove(component);
        }
        add(component, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(0, 2, 0, 2), 0, 0));
    }
}
