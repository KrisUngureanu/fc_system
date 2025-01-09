/**
 * 
 */
package kz.tamur.comps.ui.label;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import static kz.tamur.comps.Constants.DONT_ROTATE;
import static kz.tamur.comps.Constants.ROTATE_RIGHT;
import static kz.tamur.comps.Constants.ROTATE_LEFT;

/**
 * The Class OrRotationLabel.
 *
 * @author Sergey Lebedev
 */
public class OrRotationLabel extends OrLabel {
    
    /** rotation. */
    private int rotation = DONT_ROTATE;

    /** painting. */
    private boolean painting = false;

    /**
     * Конструктор класса or rotation label.
     */
    public OrRotationLabel() {
        super();
    }

    /**
     * Конструктор класса or rotation label.
     *
     * @param text the text
     */
    public OrRotationLabel(String text) {
        super(text);
    }

    /**
     * Конструктор класса or rotation label.
     *
     * @param image the image
     */
    public OrRotationLabel(Icon image) {
        super(image);
    }

    /**
     * Конструктор класса or rotation label.
     *
     * @param text the text
     * @param rotation the rotation
     */
    public OrRotationLabel(String text, int rotation) {
        super(text);
        this.rotation = rotation;
    }

    /**
     * Конструктор класса or rotation label.
     *
     * @param image the image
     * @param rotation the rotation
     */
    public OrRotationLabel(Icon image, int rotation) {
        super(image);
        this.rotation = rotation;
    }

    /**
     * Конструктор класса or rotation label.
     *
     * @param text the text
     * @param icon the icon
     * @param rotation the rotation
     */
    public OrRotationLabel(String text, Icon icon, int rotation) {
        super(text, icon);
        this.rotation = rotation;
    }

    /**
     * Получить rotation.
     *
     * @return rotation
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Установить rotation.
     *
     * @param rotation новое значение rotation
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Проверяет, является ли rotated.
     *
     * @return <code>true</code>, если rotated
     */
    public boolean isRotated() {
        return rotation != DONT_ROTATE;
    }

    
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isRotated())
            g2d.rotate(Math.toRadians(90 * rotation));
        if (rotation == ROTATE_RIGHT)
            g2d.translate(0, -this.getWidth());
        else if (rotation == ROTATE_LEFT)
            g2d.translate(-this.getHeight(), 0);
        painting = true;

        super.paintComponent(g2d);

        painting = false;
        if (isRotated())
            g2d.rotate(-Math.toRadians(90 * rotation));
        if (rotation == ROTATE_RIGHT)
            g2d.translate(-this.getWidth(), 0);
        else if (rotation == ROTATE_LEFT)
            g2d.translate(0, -this.getHeight());
    }

    
    public Insets getInsets(Insets insets) {
        insets = super.getInsets(insets);
        if (painting) {
            if (rotation == ROTATE_LEFT) {
                int temp = insets.bottom;
                insets.bottom = insets.left;
                insets.left = insets.top;
                insets.top = insets.right;
                insets.right = temp;
            } else if (rotation == ROTATE_RIGHT) {
                int temp = insets.bottom;
                insets.bottom = insets.right;
                insets.right = insets.top;
                insets.top = insets.left;
                insets.left = temp;
            }
        }
        return insets;
    }

    
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (painting) {
            if (rotation == ROTATE_LEFT) {
                int temp = insets.bottom;
                insets.bottom = insets.left;
                insets.left = insets.top;
                insets.top = insets.right;
                insets.right = temp;
            } else if (rotation == ROTATE_RIGHT) {
                int temp = insets.bottom;
                insets.bottom = insets.right;
                insets.right = insets.top;
                insets.top = insets.left;
                insets.left = temp;
            }
        }
        return insets;
    }

    
    public int getWidth() {
        if ((painting) && (isRotated()))
            return super.getHeight();
        return super.getWidth();
    }

    
    public int getHeight() {
        if ((painting) && (isRotated()))
            return super.getWidth();
        return super.getHeight();
    }

    
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (isRotated()) {
            int width = d.width;
            d.width = d.height;
            d.height = width;
        }
        return d;
    }

    
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        if (isRotated()) {
            int width = d.width;
            d.width = d.height;
            d.height = width;
        }
        return d;
    }

    
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        if (isRotated()) {
            int width = d.width;
            d.width = d.height + 10;
            d.height = width + 10;
        }
        return d;
    }

    public void setIconSuperClass(Icon icon) {
        super.setIcon(icon);  
    }
    
    
    public void setIcon(Icon icon) {
        switch (rotation) {
        case DONT_ROTATE:
            setVerticalTextPosition(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            break;
        case ROTATE_LEFT:
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            break;
        case ROTATE_RIGHT:
            setVerticalTextPosition(SwingConstants.BOTTOM);
           setHorizontalTextPosition(SwingConstants.CENTER);
            break;
        }
        super.setIcon(icon);
    }

}
