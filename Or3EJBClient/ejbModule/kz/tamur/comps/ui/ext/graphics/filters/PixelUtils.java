package kz.tamur.comps.ui.ext.graphics.filters;

import java.awt.*;

import kz.tamur.comps.ui.ext.utils.MathUtils;

/**
 * Some more useful math functions for image processing. These are becoming obsolete as we move to Java2D. Use MiscComposite instead.
 *
 * @author Lebedev Sergey
 */

public class PixelUtils {
    
    /** Константа REPLACE. */
    public final static int REPLACE = 0;
    
    /** Константа NORMAL. */
    public final static int NORMAL = 1;
    
    /** Константа MIN. */
    public final static int MIN = 2;
    
    /** Константа MAX. */
    public final static int MAX = 3;
    
    /** Константа ADD. */
    public final static int ADD = 4;
    
    /** Константа SUBTRACT. */
    public final static int SUBTRACT = 5;
    
    /** Константа DIFFERENCE. */
    public final static int DIFFERENCE = 6;
    
    /** Константа MULTIPLY. */
    public final static int MULTIPLY = 7;
    
    /** Константа HUE. */
    public final static int HUE = 8;
    
    /** Константа SATURATION. */
    public final static int SATURATION = 9;
    
    /** Константа VALUE. */
    public final static int VALUE = 10;
    
    /** Константа COLOR. */
    public final static int COLOR = 11;
    
    /** Константа SCREEN. */
    public final static int SCREEN = 12;
    
    /** Константа AVERAGE. */
    public final static int AVERAGE = 13;
    
    /** Константа OVERLAY. */
    public final static int OVERLAY = 14;
    
    /** Константа CLEAR. */
    public final static int CLEAR = 15;
    
    /** Константа EXCHANGE. */
    public final static int EXCHANGE = 16;
    
    /** Константа DISSOLVE. */
    public final static int DISSOLVE = 17;
    
    /** Константа DST_IN. */
    public final static int DST_IN = 18;
    
    /** Константа ALPHA. */
    public final static int ALPHA = 19;
    
    /** Константа ALPHA_TO_GRAY. */
    public final static int ALPHA_TO_GRAY = 20;

    /**
     * Clamp a value to the range 0..255
     *
     * @param c the c
     * @return int
     */
    public static int clamp(int c) {
        if (c < 0) {
            return 0;
        }
        if (c > 255) {
            return 255;
        }
        return c;
    }

    /**
     * Interpolate.
     *
     * @param v1 the v1
     * @param v2 the v2
     * @param f the f
     * @return int
     */
    public static int interpolate(int v1, int v2, float f) {
        return clamp((int) (v1 + f * (v2 - v1)));
    }

    /**
     * Brightness.
     *
     * @param rgb the rgb
     * @return int
     */
    public static int brightness(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }

    /**
     * Near colors.
     *
     * @param rgb1 the rgb1
     * @param rgb2 the rgb2
     * @param tolerance the tolerance
     * @return true, в случае успеха
     */
    public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) <= tolerance && Math.abs(g1 - g2) <= tolerance && Math.abs(b1 - b2) <= tolerance;
    }

    /** Константа hsb1. */
    private final static float hsb1[] = new float[3];// FIXME-not thread safe
    
    /** Константа hsb2. */
    private final static float hsb2[] = new float[3];// FIXME-not thread safe

    // Return rgb1 painted onto rgb2
    /**
     * Combine pixels.
     *
     * @param rgb1 the rgb1
     * @param rgb2 the rgb2
     * @param op the op
     * @return int
     */
    public static int combinePixels(int rgb1, int rgb2, int op) {
        return combinePixels(rgb1, rgb2, op, 0xff);
    }

    /**
     * Combine pixels.
     *
     * @param rgb1 the rgb1
     * @param rgb2 the rgb2
     * @param op the op
     * @param extraAlpha the extra alpha
     * @param channelMask the channel mask
     * @return int
     */
    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha, int channelMask) {
        return (rgb2 & ~channelMask) | combinePixels(rgb1 & channelMask, rgb2, op, extraAlpha);
    }

    /**
     * Combine pixels.
     *
     * @param rgb1 the rgb1
     * @param rgb2 the rgb2
     * @param op the op
     * @param extraAlpha the extra alpha
     * @return int
     */
    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha) {
        if (op == REPLACE) {
            return rgb1;
        }
        int a1 = (rgb1 >> 24) & 0xff;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int a2 = (rgb2 >> 24) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        switch (op) {
        case NORMAL:
            break;
        case MIN:
            r1 = Math.min(r1, r2);
            g1 = Math.min(g1, g2);
            b1 = Math.min(b1, b2);
            break;
        case MAX:
            r1 = Math.max(r1, r2);
            g1 = Math.max(g1, g2);
            b1 = Math.max(b1, b2);
            break;
        case ADD:
            r1 = clamp(r1 + r2);
            g1 = clamp(g1 + g2);
            b1 = clamp(b1 + b2);
            break;
        case SUBTRACT:
            r1 = clamp(r2 - r1);
            g1 = clamp(g2 - g1);
            b1 = clamp(b2 - b1);
            break;
        case DIFFERENCE:
            r1 = clamp(Math.abs(r1 - r2));
            g1 = clamp(Math.abs(g1 - g2));
            b1 = clamp(Math.abs(b1 - b2));
            break;
        case MULTIPLY:
            r1 = clamp(r1 * r2 / 255);
            g1 = clamp(g1 * g2 / 255);
            b1 = clamp(b1 * b2 / 255);
            break;
        case DISSOLVE:
            if ((MathUtils.random() & 0xff) <= a1) {
                r1 = r2;
                g1 = g2;
                b1 = b2;
            }
            break;
        case AVERAGE:
            r1 = (r1 + r2) / 2;
            g1 = (g1 + g2) / 2;
            b1 = (b1 + b2) / 2;
            break;
        case HUE:
        case SATURATION:
        case VALUE:
        case COLOR:
            Color.RGBtoHSB(r1, g1, b1, hsb1);
            Color.RGBtoHSB(r2, g2, b2, hsb2);
            switch (op) {
            case HUE:
                hsb2[0] = hsb1[0];
                break;
            case SATURATION:
                hsb2[1] = hsb1[1];
                break;
            case VALUE:
                hsb2[2] = hsb1[2];
                break;
            case COLOR:
                hsb2[0] = hsb1[0];
                hsb2[1] = hsb1[1];
                break;
            }
            rgb1 = Color.HSBtoRGB(hsb2[0], hsb2[1], hsb2[2]);
            r1 = (rgb1 >> 16) & 0xff;
            g1 = (rgb1 >> 8) & 0xff;
            b1 = rgb1 & 0xff;
            break;
        case SCREEN:
            r1 = 255 - ((255 - r1) * (255 - r2)) / 255;
            g1 = 255 - ((255 - g1) * (255 - g2)) / 255;
            b1 = 255 - ((255 - b1) * (255 - b2)) / 255;
            break;
        case OVERLAY:
            int m,
            s;
            s = 255 - ((255 - r1) * (255 - r2)) / 255;
            m = r1 * r2 / 255;
            r1 = (s * r1 + m * (255 - r1)) / 255;
            s = 255 - ((255 - g1) * (255 - g2)) / 255;
            m = g1 * g2 / 255;
            g1 = (s * g1 + m * (255 - g1)) / 255;
            s = 255 - ((255 - b1) * (255 - b2)) / 255;
            m = b1 * b2 / 255;
            b1 = (s * b1 + m * (255 - b1)) / 255;
            break;
        case CLEAR:
            r1 = g1 = b1 = 0xff;
            break;
        case DST_IN:
            r1 = clamp((r2 * a1) / 255);
            g1 = clamp((g2 * a1) / 255);
            b1 = clamp((b2 * a1) / 255);
            a1 = clamp((a2 * a1) / 255);
            return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
        case ALPHA:
            a1 = a1 * a2 / 255;
            return (a1 << 24) | (r2 << 16) | (g2 << 8) | b2;
        case ALPHA_TO_GRAY:
            int na = 255 - a1;
            return (a1 << 24) | (na << 16) | (na << 8) | na;
        }
        if (extraAlpha != 0xff || a1 != 0xff) {
            a1 = a1 * extraAlpha / 255;
            int a3 = (255 - a1) * a2 / 255;
            r1 = clamp((r1 * a1 + r2 * a3) / 255);
            g1 = clamp((g1 * a1 + g2 * a3) / 255);
            b1 = clamp((b1 * a1 + b2 * a3) / 255);
            a1 = clamp(a1 + a3);
        }
        return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
    }
}
