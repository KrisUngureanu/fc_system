package kz.tamur.comps.ui.label;

import java.awt.*;

import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * 
 * @author Sergey Lebedev
 *
 */
public final class OrLabelStyle
{
    /**
     * Default label margin
     */
    public static Insets margin = StyleConstants.margin;

    /**
     * Default label background painter
     */
    public static Painter painter = StyleConstants.painter;

    /**
     * Default label background color
     */
    public static Color backgroundColor = StyleConstants.backgroundColor;

    /**
     * Показывать тень позади текста?
     */
    public static boolean drawShade = false;

    /**
     * Цвет тени для текста
     */
    public static Color shadeColor = new Color ( 200, 200, 200 );
}
