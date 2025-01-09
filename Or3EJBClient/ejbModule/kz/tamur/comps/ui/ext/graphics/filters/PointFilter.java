package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * An abstract superclass for point filters. The interface is the same as the old RGBImageFilter.
 *
 * @author Lebedev Sergey
 */

public abstract class PointFilter extends AbstractBufferedImageOp {

    /** can filter index color model. */
    protected boolean canFilterIndexColorModel = false;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        int type = src.getType();
        WritableRaster srcRaster = src.getRaster();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }
        WritableRaster dstRaster = dst.getRaster();

        setDimensions(width, height);

        int[] inPixels = new int[width];
        for (int y = 0; y < height; y++) {
            // We try to avoid calling getRGB on images as it causes them to become unmanaged, causing horrible performance problems.
            if (type == BufferedImage.TYPE_INT_ARGB) {
                srcRaster.getDataElements(0, y, width, 1, inPixels);
                for (int x = 0; x < width; x++) {
                    inPixels[x] = filterRGB(x, y, inPixels[x]);
                }
                dstRaster.setDataElements(0, y, width, 1, inPixels);
            } else {
                src.getRGB(0, y, width, 1, inPixels, 0, width);
                for (int x = 0; x < width; x++) {
                    inPixels[x] = filterRGB(x, y, inPixels[x]);
                }
                dst.setRGB(0, y, width, 1, inPixels, 0, width);
            }
        }

        return dst;
    }

    /**
     * Sets the dimensions.
     *
     * @param width the width
     * @param height the height
     */
    public void setDimensions(int width, int height) {
    }

    /**
     * Filter rgb.
     *
     * @param x the x
     * @param y the y
     * @param rgb the rgb
     * @return int
     */
    public abstract int filterRGB(int x, int y, int rgb);
}
