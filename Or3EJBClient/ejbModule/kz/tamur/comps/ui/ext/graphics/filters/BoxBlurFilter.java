package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.image.BufferedImage;

/**
 * A filter which performs a box blur on an image. The horizontal and vertical blurs can be specified separately and a number of iterations
 * can be given which allows an approximation to Gaussian blur.
 * 
 * @author Lebedev Sergey
 */
public class BoxBlurFilter extends AbstractBufferedImageOp {

    /** h radius. */
    private int hRadius;

    /** v radius. */
    private int vRadius;

    /** iterations. */
    private int iterations = 1;

    /**
     * Конструктор класса box blur filter.
     */
    public BoxBlurFilter() {
        super();
    }

    /**
     * Конструктор класса box blur filter.
     * 
     * @param hRadius
     *            the h radius
     * @param vRadius
     *            the v radius
     * @param iterations
     *            the iterations
     */
    public BoxBlurFilter(int hRadius, int vRadius, int iterations) {
        super();
        setHRadius(hRadius);
        setVRadius(vRadius);
        setIterations(iterations);
    }

    /**
     * Установить h radius.
     * 
     * @param hRadius
     *            новое значение h radius
     */
    public void setHRadius(int hRadius) {
        this.hRadius = hRadius;
    }

    /**
     * Получить h radius.
     * 
     * @return h radius
     */
    public int getHRadius() {
        return hRadius;
    }

    /**
     * Установить v radius.
     * 
     * @param vRadius
     *            новое значение v radius
     */
    public void setVRadius(int vRadius) {
        this.vRadius = vRadius;
    }

    /**
     * Получить v radius.
     * 
     * @return v radius
     */
    public int getVRadius() {
        return vRadius;
    }

    /**
     * Установить radius.
     * 
     * @param radius
     *            новое значение radius
     */
    public void setRadius(int radius) {
        this.hRadius = this.vRadius = radius;
    }

    /**
     * Получить radius.
     * 
     * @return radius
     */
    public int getRadius() {
        return hRadius;
    }

    /**
     * Установить iterations.
     * 
     * @param iterations
     *            новое значение iterations
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * Получить iterations.
     * 
     * @return iterations
     */
    public int getIterations() {
        return iterations;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        getRGB(src, 0, 0, width, height, inPixels);

        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }

        setRGB(dst, 0, 0, width, height, inPixels);
        return dst;
    }

    /**
     * Blur.
     * 
     * @param in
     *            the in
     * @param out
     *            the out
     * @param width
     *            the width
     * @param height
     *            the height
     * @param radius
     *            the radius
     */
    public static void blur(int[] in, int[] out, int width, int height, int radius) {
        int widthMinus1 = width - 1;
        int tableSize = 2 * radius + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++) {
            divide[i] = i / tableSize;
        }

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -radius; i <= radius; i++) {
                int rgb = in[inIndex + ImageMath.clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

                int i1 = x + radius + 1;
                if (i1 > widthMinus1) {
                    i1 = widthMinus1;
                }
                int i2 = x - radius;
                if (i2 < 0) {
                    i2 = 0;
                }
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }
}
