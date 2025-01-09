package kz.tamur.comps.ui.ext;

import javax.swing.*;

import java.awt.*;
import java.net.URL;

/**
 * The Class OrientedIcon.
 *
 * @author Lebedev Sergey
 */
public class OrientedIcon extends ImageIcon {
    
    /**
     * Конструктор класса oriented icon.
     */
    public OrientedIcon() {
        super();
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param imageData the image data
     */
    public OrientedIcon(byte[] imageData) {
        super(imageData);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param imageData the image data
     * @param description the description
     */
    public OrientedIcon(byte[] imageData, String description) {
        super(imageData, description);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param image the image
     */
    public OrientedIcon(Image image) {
        super(image);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param image the image
     * @param description the description
     */
    public OrientedIcon(Image image, String description) {
        super(image, description);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param location the location
     */
    public OrientedIcon(URL location) {
        super(location);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param location the location
     * @param description the description
     */
    public OrientedIcon(URL location, String description) {
        super(location, description);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param filename the filename
     */
    public OrientedIcon(String filename) {
        super(filename);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param filename the filename
     * @param description the description
     */
    public OrientedIcon(String filename, String description) {
        super(filename, description);
    }

    /**
     * Конструктор класса oriented icon.
     *
     * @param icon the icon
     */
    public OrientedIcon(ImageIcon icon) {
        super(icon.getImage());
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = getIconWidth();
        int h = getIconHeight();
        if (c == null || c.getComponentOrientation().isLeftToRight()) {
            g.drawImage(getImage(), x, y, w, h, getImageObserver());
        } else {
            g.drawImage(getImage(), x + w, y, -w, h, getImageObserver());
        }
    }
}
