package kz.tamur.comps.ui.scrollbar;

import java.awt.*;

import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * Реализация стилистики UI компонента ScrollPane
 * 
 * @author Sergey Lebedev
 * 
 */
public final class OrScrollPaneStyle {
    /**
     * Border color
     */
    public static Color borderColor = Color.LIGHT_GRAY;

    /**
     * Dark border color
     */
    public static Color darkBorder = new Color(170, 170, 170);

    /**
     * Draw border
     */
    public static boolean drawBorder = StyleConstants.drawBorder;

    /**
     * Decoration rounding
     */
    public static int round = StyleConstants.round;

    /**
     * Decoration shade width
     */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /**
     * Default scroll pane margin
     */
    public static Insets margin = StyleConstants.margin;

    /**
     * Draw focus when ancestor of focused component
     */
    public static boolean drawFocus = true;

    /**
     * Draw background
     */
    public static boolean drawBackground = false;
}
