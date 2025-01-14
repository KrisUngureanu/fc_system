package kz.tamur.comps.ui.ext.graphics.filters;

/**
 * Sets the opacity (alpha) of every pixel in an image to a constant value.
 *
 * @author Lebedev Sergey
 */

public class OpacityFilter extends PointFilter {

    /** opacity. */
    private int opacity;
    
    /** opacity24. */
    private int opacity24;

    /**
     * Construct an OpacityFilter with 50% opacity.
     */
    public OpacityFilter() {
        this(0x88);
    }

    /**
     * Construct an OpacityFilter with the given opacity (alpha).
     * 
     * @param opacity
     *            the opacity (alpha) in the range 0..255
     */
    public OpacityFilter(int opacity) {
        setOpacity(opacity);
    }

    /**
     * Set the opacity.
     * 
     * @param opacity
     *            the opacity (alpha) in the range 0..255
     * @see #getOpacity
     */
    public void setOpacity(int opacity) {
        this.opacity = opacity;
        opacity24 = opacity << 24;
    }

    /**
     * Get the opacity setting.
     * 
     * @return the opacity
     * @see #setOpacity
     */
    public int getOpacity() {
        return opacity;
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
        if ((rgb & 0xff000000) != 0) {
            return (rgb & 0xffffff) | opacity24;
        }
        return rgb;
    }
}
