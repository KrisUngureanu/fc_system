package kz.tamur.comps.ui.panel;

import java.awt.*;

import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * The Class OrPanelStyle.
 *
 * @author Lebedev Sergey
 */
public final class OrPanelStyle {

    /** Decorate panel with Web-styled background or not. */
    public static boolean undecorated = false;

    /** Draw panel focus. */
    public static boolean drawFocus = false;

    /** Decoration rounding. */
    public static int round = StyleConstants.smallRound;

    /** Decoration shade width. */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /** Default panel margin. */
    public static Insets margin = StyleConstants.margin;

    /** Fill decoration background. */
    public static boolean drawBackground = true;

    /** Web-styled background. */
    public static boolean webColored = true;

    /** Default-styled background color. */
    public static Color backgroundColor = StyleConstants.backgroundColor;

    /** Default panel background painter. */
    public static Painter painter = StyleConstants.painter;

    /** Default panel clip shape provider. */
    public static ShapeProvider clipProvider = StyleConstants.clipProvider;

    /** Sides which will drawed when panel is decorated with Web-styled border. */
    public static boolean drawTop = true;

    /** draw left. */
    public static boolean drawLeft = true;

    /** draw bottom. */
    public static boolean drawBottom = true;

    /** draw right. */
    public static boolean drawRight = true;
}
