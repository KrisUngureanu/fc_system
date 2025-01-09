package kz.tamur.guidesigner;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 07.05.2004
 * Time: 17:08:08
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionStatusBar extends JPanel {

    private JLabel statusCorner = new JLabel(kz.tamur.rt.Utils.getImageIcon("StatusCorner"));

    public ExpressionStatusBar() {
    	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Utils.getLightSysColor());
        setFont(Utils.getDefaultFont());
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Utils.getDarkShadowSysColor()));
        setPreferredSize(new Dimension(20, 22)); 
        addLabel("  ");
    }

    private int getComponentIdx() {
        int count = getComponentCount();
        return count;
    }

    public void addEmptySpace() {
        JLabel label = new JLabel();
        add(label);
    }
    
    public void addLabel(JLabel label) {
    	int count = getComponentCount();
    	label.setFont(Utils.getDefaultFont());
        label.setForeground(Utils.getDarkShadowSysColor());
        add(label);
    }

    public void addLabel(String title) {
        JLabel label = new JLabel(title);
        label.setFont(Utils.getDefaultFont());
        label.setForeground(Utils.getDarkShadowSysColor());
        add(label);
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

        add(tf);
    }

    public void addAnyComponent(JComponent c) {
        add(c);
    }

    public void addAnyComponent(JComponent c, double weight) {
        add(c);
    }

    public void addSeparator() {
        StatusSeparator sep = new StatusSeparator();
        sep.setBorder(new EmptyBorder(0,5,0,5));
        add(sep);
    }

    public void addCorner() {
        if (getComponentIdx() == 0) {
            addEmptySpace();
        }
        add(statusCorner);
    }

    class StatusSeparator extends JLabel {
        public StatusSeparator() {
            super("1");
            setPreferredSize(new Dimension(10, ExpressionStatusBar.this.getHeight()));
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
    
    /**
     * @param firstComponent
     *            the firstComponent to set
     */
   
}
