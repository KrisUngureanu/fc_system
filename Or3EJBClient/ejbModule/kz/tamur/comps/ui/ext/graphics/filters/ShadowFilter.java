package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

 /**
  * A filter which draws a drop shadow based on the alpha channel of the image.
  *
  * @author Lebedev Sergey
  */

public class ShadowFilter extends AbstractBufferedImageOp {
    
    /** radius. */
    private int radius = 5;
    
    /** x offset. */
    private int xOffset = 5;
    
    /** y offset. */
    private int yOffset = 5;
    
    /** opacity. */
    private float opacity = 0.5f;
    
    /** add margins. */
    private boolean addMargins = false;
    
    /** shadow only. */
    private boolean shadowOnly = true;
    
    /** shadow color. */
    private int shadowColor = 0xff000000;

    /**
     * Конструктор класса shadow filter.
     */
    public ShadowFilter() {
    }

    /**
     * Конструктор класса shadow filter.
     *
     * @param radius the radius
     * @param xOffset the x offset
     * @param yOffset the y offset
     * @param opacity the opacity
     */
    public ShadowFilter(int radius, int xOffset, int yOffset, float opacity) {
        this.radius = radius;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.opacity = opacity;
    }

    /**
     * Установить x offset.
     *
     * @param xOffset новое значение x offset
     */
    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    /**
     * Получить x offset.
     *
     * @return x offset
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * Установить y offset.
     *
     * @param yOffset новое значение y offset
     */
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    /**
     * Получить y offset.
     *
     * @return y offset
     */
    public int getYOffset() {
        return yOffset;
    }

    /**
     * Set the radius of the kernel, and hence the amount of blur. The bigger the radius, the longer this filter will take.
     * 
     * @param radius
     *            the radius of the blur in pixels.
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Get the radius of the kernel.
     * 
     * @return the radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Установить opacity.
     *
     * @param opacity новое значение opacity
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * Получить opacity.
     *
     * @return opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Установить shadow color.
     *
     * @param shadowColor новое значение shadow color
     */
    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    /**
     * Получить shadow color.
     *
     * @return shadow color
     */
    public int getShadowColor() {
        return shadowColor;
    }

    /**
     * Установить adds the margins.
     *
     * @param addMargins новое значение adds the margins
     */
    public void setAddMargins(boolean addMargins) {
        this.addMargins = addMargins;
    }

    /**
     * Получить adds the margins.
     *
     * @return adds the margins
     */
    public boolean getAddMargins() {
        return addMargins;
    }

    /**
     * Установить shadow only.
     *
     * @param shadowOnly новое значение shadow only
     */
    public void setShadowOnly(boolean shadowOnly) {
        this.shadowOnly = shadowOnly;
    }

    /**
     * Получить shadow only.
     *
     * @return shadow only
     */
    public boolean getShadowOnly() {
        return shadowOnly;
    }

    /**
     * Transform space.
     *
     * @param r the r
     */
    protected void transformSpace(Rectangle r) {
        if (addMargins) {
            r.width += Math.abs(xOffset) + 2 * radius;
            r.height += Math.abs(yOffset) + 2 * radius;
        }
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            if (addMargins) {
                ColorModel cm = src.getColorModel();
                dst = new BufferedImage(cm, cm.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
                        cm.isAlphaPremultiplied(), null);
            } else {
                dst = createCompatibleDestImage(src, null);
            }
        }

        // Make a black mask from the image's alpha channel
        float[][] extractAlpha = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, opacity } };
        BufferedImage shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        new BandCombineOp(extractAlpha, null).filter(src.getRaster(), shadow.getRaster());
        shadow = new GaussianFilter(radius).filter(shadow, null);

        Graphics2D g = dst.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        if (addMargins) {
            int topShadow = Math.max(0, radius - yOffset);
            int leftShadow = Math.max(0, radius - xOffset);
            g.translate(topShadow, leftShadow);
        }
        g.drawRenderedImage(shadow, AffineTransform.getTranslateInstance(xOffset, yOffset));
        if (!shadowOnly) {
            g.setComposite(AlphaComposite.SrcOver);
            g.drawRenderedImage(src, null);
        }
        g.dispose();

        return dst;
    }
}
