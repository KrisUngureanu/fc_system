package kz.tamur.comps.ui.scrollbar;

import java.awt.*;

import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * Реализация стилистики UI компонента ScrollBar
 * 
 * @author Sergey Lebedev
 * 
 */
public final class OrScrollBarStyle {
    /**
     * Scroll bar background color
     */
    public static Color scrollBg = new Color(245, 245, 245);

    /**
     * Scroll bar side border color
     */
    public static Color scrollBorder = new Color(230, 230, 230);

    /**
     * Scroll bar border color
     */
    public static Color scrollBarBorder = new Color(201, 201, 201);

    /**
     * Scroll bar top or left gradient
     */
    public static Color scrollGradientLeft = new Color(239, 239, 239);

    /**
     * Scroll bar bottom or right gradient
     */
    public static Color scrollGradientRight = new Color(211, 211, 211);

    /**
     * Dragged scroll bar top or left gradient
     */
    public static Color scrollSelGradientLeft = new Color(203, 203, 203);

    /**
     * Dragged scroll bar bottom or right gradient
     */
    public static Color scrollSelGradientRight = new Color(175, 175, 175);

    /**
     * Decoration rounding
     */
    public static int rounding = StyleConstants.round;

    /**
     * Decoration shade width
     */
    public static boolean drawBorder = StyleConstants.drawBorder;

    /**
     * Minimum horizontal scroll bar thumb width
     */
    public static int minThumbWidth = 40;

    /**
     * Minimum vertical scroll bar thumb height
     */
    public static int minThumbHeight = 40;
}