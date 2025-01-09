package kz.tamur.comps.ui.ext.utils;

import java.awt.*;
import java.util.Locale;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class ColorUtils {
    /**
     * Removes the alpha channel from the color
     */

    public static Color removeAlpha(Color c) {
        if (c.getAlpha() != 100) {
            c = new Color(c.getRGB());
        }
        return c;
    }

    /**
     * Returns Color progressed from color1 to color2 by specified percentage
     */

    public static Color getProgress(Color color1, Color color2, float progress) {
        return new Color(getProgress(color1.getRed(), color2.getRed(), progress), getProgress(color1.getGreen(),
                color2.getGreen(), progress), getProgress(color1.getBlue(), color2.getBlue(), progress));
    }

    public static int getProgress(int color1, int color2, float progress) {
        return color1 + Math.round(((float) color2 - color1) * progress);
    }

    /**
     * Web-safe color
     */

    public static int getWebSafe(int color) {
        if (0 <= color && color <= 51) {
            return color > 51 - color ? 51 : 0;
        } else if (51 <= color && color <= 102) {
            return 51 + color > 102 - color ? 102 : 51;
        } else if (102 <= color && color <= 153) {
            return 102 + color > 153 - color ? 153 : 102;
        } else if (153 <= color && color <= 204) {
            return 153 + color > 204 - color ? 204 : 153;
        } else if (204 <= color && color <= 255) {
            return 204 + color > 255 - color ? 255 : 204;
        }
        return color;
    }

    /**
     * RGB to HEX and HEX to RGB parsing
     */

    public static String rgbToHex(Color color) {
        return rgbToHex(color.getRGB());
    }

    public static String rgbToHex(int rgb) {
        String hex = Integer.toHexString(rgb).toUpperCase(Locale.ROOT);
        return hex.substring(2, hex.length());
    }

    public static Color hexToColor(String hex) {
        return Color.decode(hex.startsWith("#") ? hex : ("#" + hex));
    }

    /**
     * Returns transparent color
     */

    public static Color black(int alpha) {
        return new Color(0, 0, 0, alpha);
    }

    public static Color white(int alpha) {
        return new Color(255, 255, 255, alpha);
    }
}
