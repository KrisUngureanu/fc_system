package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import kz.tamur.comps.ui.ext.utils.ImageUtils;

/**
 * The Class ImageFilterUtils.
 *
 * @author Lebedev Sergey
 */
public class ImageFilterUtils {
    
    /**
     * Applies box blur filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param hRadius the h radius
     * @param vRadius the v radius
     * @param iterations the iterations
     * @return buffered image
     */

    public static BufferedImage applyBoxBlurFilter(Image src, Image dst, int hRadius, int vRadius, int iterations) {
        return applyBoxBlurFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), hRadius, vRadius,
                iterations);
    }

    /**
     * Apply box blur filter.
     *
     * @param src the src
     * @param dst the dst
     * @param hRadius the h radius
     * @param vRadius the v radius
     * @param iterations the iterations
     * @return buffered image
     */
    public static BufferedImage applyBoxBlurFilter(BufferedImage src, BufferedImage dst, int hRadius, int vRadius, int iterations) {
        return new BoxBlurFilter(hRadius, vRadius, iterations).filter(src, dst);
    }

    /** Applies grayscale filter to image. */

    private static ColorConvertOp grayscaleColorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    /**
     * Apply grayscale filter.
     *
     * @param src the src
     * @param dst the dst
     * @return buffered image
     */
    public static BufferedImage applyGrayscaleFilter(Image src, Image dst) {
        return applyGrayscaleFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst));
    }

    /**
     * Apply grayscale filter.
     *
     * @param src the src
     * @param dst the dst
     * @return buffered image
     */
    public static BufferedImage applyGrayscaleFilter(BufferedImage src, BufferedImage dst) {
        return grayscaleColorConvert.filter(src, dst);
    }

    /**
     * Applies gaussian filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param radius the radius
     * @return buffered image
     */

    public static BufferedImage applyGaussianFilter(Image src, Image dst, float radius) {
        return applyGaussianFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), radius);
    }

    /**
     * Apply gaussian filter.
     *
     * @param src the src
     * @param dst the dst
     * @param radius the radius
     * @return buffered image
     */
    public static BufferedImage applyGaussianFilter(BufferedImage src, BufferedImage dst, float radius) {
        return new GaussianFilter(radius).filter(src, dst);
    }

    /**
     * Applies zoom blur filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param zoom the zoom
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */

    public static BufferedImage applyZoomBlurFilter(Image src, Image dst, float zoom, float centreX, float centreY) {
        return applyZoomBlurFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), zoom, centreX, centreY);
    }

    /**
     * Apply zoom blur filter.
     *
     * @param src the src
     * @param dst the dst
     * @param zoom the zoom
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */
    public static BufferedImage applyZoomBlurFilter(BufferedImage src, BufferedImage dst, float zoom, float centreX, float centreY) {
        return new MotionBlurOp(0f, 0f, 0f, zoom, centreX, centreY).filter(src, dst);
    }

    /**
     * Applies rotation blur filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param rotation the rotation
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */

    public static BufferedImage applyRotationBlurFilter(Image src, Image dst, float rotation, float centreX, float centreY) {
        return applyRotationBlurFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), rotation, centreX,
                centreY);
    }

    /**
     * Apply rotation blur filter.
     *
     * @param src the src
     * @param dst the dst
     * @param rotation the rotation
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */
    public static BufferedImage applyRotationBlurFilter(BufferedImage src, BufferedImage dst, float rotation, float centreX,
            float centreY) {
        return new MotionBlurOp(0f, 0f, rotation, 0f, centreX, centreY).filter(src, dst);
    }

    /**
     * Applies rotation blur filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param distance the distance
     * @param angle the angle
     * @param rotation the rotation
     * @param zoom the zoom
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */

    public static BufferedImage applyMotionBlurFilter(Image src, Image dst, float distance, float angle, float rotation,
            float zoom, float centreX, float centreY) {
        return applyMotionBlurFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), distance, angle,
                rotation, zoom, centreX, centreY);
    }

    /**
     * Apply motion blur filter.
     *
     * @param src the src
     * @param dst the dst
     * @param distance the distance
     * @param angle the angle
     * @param rotation the rotation
     * @param zoom the zoom
     * @param centreX the centre x
     * @param centreY the centre y
     * @return buffered image
     */
    public static BufferedImage applyMotionBlurFilter(BufferedImage src, BufferedImage dst, float distance, float angle,
            float rotation, float zoom, float centreX, float centreY) {
        return new MotionBlurOp(distance, angle, rotation, zoom, centreX, centreY).filter(src, dst);
    }

    /**
     * Applies opacity filter to image.
     *
     * @param src the src
     * @param dst the dst
     * @param opacity the opacity
     * @return buffered image
     */

    public static BufferedImage applyOpacityFilter(Image src, Image dst, int opacity) {
        return applyOpacityFilter(ImageUtils.getBufferedImage(src), ImageUtils.getBufferedImage(dst), opacity);
    }

    /**
     * Apply opacity filter.
     *
     * @param src the src
     * @param dst the dst
     * @param opacity the opacity
     * @return buffered image
     */
    public static BufferedImage applyOpacityFilter(BufferedImage src, BufferedImage dst, int opacity) {
        return new OpacityFilter(opacity).filter(src, dst);
    }
}
