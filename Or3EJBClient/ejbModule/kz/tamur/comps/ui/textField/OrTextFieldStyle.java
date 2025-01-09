package kz.tamur.comps.ui.textField;

import javax.swing.*;
import java.awt.*;

import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * Реализация стилистики UI компонента TextField
 * 
 * @author Lebedev Sergey
 * 
 */
public final class OrTextFieldStyle {
    /**
     * Should draw border
     */
    public static boolean drawBorder = StyleConstants.drawBorder;

    /**
     * Should draw focus
     */
    public static boolean drawFocus = StyleConstants.drawFocus;

    /**
     * Default corners rounding
     */
    public static int round = StyleConstants.round;

    /**
     * Default shade width
     */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /**
     * Fill decoration background
     */
    public static boolean drawBackground = true;

    /**
     * Web-styled background
     */
    public static boolean webColored = false;

    /**
     * Default label background painter
     */
    public static Painter painter = StyleConstants.painter;

    /**
     * Default content spacing
     */
    public static int componentSpacing = StyleConstants.contentSpacing;

    /**
     * Default margin
     */
    public static Insets margin = new Insets(0, 0, 0, 0);

    /**
     * Default field margin
     */
    public static Insets fieldMargin = new Insets(2, 2, 2, 2);

    /**
     * Input prompt text (null = none)
     */
    public static String inputPrompt = null;

    /**
     * Input prompt text font (null = same as the text component font)
     */
    public static Font inputPromptFont = null;

    /**
     * Input prompt text foreground (null = same as the text component)
     */
    public static Color inputPromptForeground = new Color(160, 160, 160);

    /**
     * Input prompt text position
     */
    public static int inputPromptPosition = SwingConstants.LEADING;

    /**
     * Hide input prompt text on focus
     */
    public static boolean hideInputPromptOnFocus = true;
}