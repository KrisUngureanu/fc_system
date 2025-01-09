package kz.tamur.comps.ui.ext;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import kz.tamur.comps.ui.ext.utils.ColorUtils;

/**
 * Global components constants
 */
public final class StyleConstants {
    // Системный текстовый разделитель
    public static final String SEPARATOR = ";#&;";

    // Choosers choices
    public static final int NONE_OPTION = -2;
    public static final int CLOSE_OPTION = -1;
    public static final int OK_OPTION = 0;
    public static final int CANCEL_OPTION = 1;

    // Чистая иконка 16x16
    public static final ImageIcon EMPTY_ICON = kz.tamur.rt.Utils.getImageIconFull("empty.png");

    // Цветовая константа прозрачности
    public static final Color transparent = new Color(255, 255, 255, 0);

    // Highlight colors constants
    public static final Color redHighlight = new Color(255, 0, 0, 48);
    public static final Color greenHighlight = new Color(0, 255, 0, 48);
    public static final Color blueHighlight = new Color(0, 0, 255, 48);
    public static final Color yellowHighlight = new Color(255, 255, 0, 48);

    // Default borders
    public static final Border emptyBorder = BorderFactory.createEmptyBorder();

    /**
     * Global rewriteable components styles Be sure to change those before application UI initialized!
     */

    // Components shade painting style
    public static ShadeType shadeType = ShadeType.simple;
    public static float simpleShadeTransparency = 0.7f;

    // Disabled component icons transparency
    public static float disabledIconsTransparency = 0.7f;

    // Alpha-background settings
    public static int ALPHA_RECT_SIZE = 10;
    public static Color DARK_ALPHA = new Color(204, 204, 204);
    public static Color LIGHT_ALPHA = new Color(255, 255, 255);

    // Show hidden files in choosers
    public static boolean showHiddenFiles = false;

    // Components animation settings
    public static boolean animate = true;
    public static int animationDelay = 40;// 24;
    public static int avgAnimationDelay = 30;// 36;
    public static int fastAnimationDelay = 20;// 48;
    public static int maxAnimationDelay = 10;// 96;

    // Components border settings
    public static boolean drawBorder = true;
    public static boolean rolloverDarkBorderOnly = false;
    public static int borderWidth = 1;
    public static Color borderColor = new Color(170, 170, 170);// Color.LIGHT_GRAY;
    public static Color innerBorderColor = Color.WHITE;
    public static Color darkBorderColor = Color.GRAY;
    public static Color averageBorderColor = ColorUtils.getProgress(borderColor, darkBorderColor, 0.5f);
    public static Color disabledBorderColor = Color.LIGHT_GRAY;

    // Components focus settings
    public static boolean drawFocus = true;
    public static Color focusColor = new Color(160, 160, 160);
    public static Color fieldFocusColor = new Color(85, 142, 239);
    public static Color transparentFieldFocusColor = new Color(85, 142, 239, 128);
    public static FocusType focusType = FocusType.fieldFocus;
    public static Stroke fieldFocusStroke = new BasicStroke(1.5f);
    public static Stroke focusStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 1, 2 },
            0);

    // Toggle icon transparency settings
    public static boolean shadeToggleIcon = false;
    public static float shadeToggleIconTransparency = 0.5f;

    // Contol buttons highlight settings
    public static boolean highlightControlButtons = false;

    // Components decoration settings
    public static boolean rolloverDecoratedOnly = false;
    public static boolean undecorated = false;
    public static Painter painter = null;
    public static ShapeProvider clipProvider = null;

    // Components corners rounding settings
    public static int smallRound = 2;
    public static int round = 4;
    public static int largeRound = 6;
    public static int decorationRound = 8;

    // Components shade settings
    public static boolean rolloverShadeOnly = false;
    public static boolean showDisabledShade = false;
    public static int shadeWidth = 2;
    public static int innerShadeWidth = 2;
    public static Color shadeColor = new Color(210, 210, 210);
    public static Color innerShadeColor = new Color(190, 190, 190);

    // Components opacity settings
    public static float fullyTransparent = 0f;
    public static float mediumTransparent = 0.85f;
    public static float slightlyTransparent = 0.95f;
    public static float opaque = 1f;

    // Components content spacing
    public static int smallLeftRightSpacing = 2;
    public static int leftRightSpacing = 4;
    public static int largeLeftRightSpacing = 8;
    public static Insets margin = new Insets(0, 0, 0, 0);

    // Настройки компонентов-контейнеров
    public static int spacing = 2;
    public static int smallContentSpacing = 1;
    public static int contentSpacing = 2;
    public static int mediumContentSpacing = 4;
    public static int largeContentSpacing = 20;
    public static Color backgroundColor = Color.WHITE;
    public static Color darkBackgroundColor = new Color(248, 248, 248);

    // Настройки текста компонентов
    public static Color textSelectionColor = new Color(210, 210, 210);
    public static Color textColor = Color.BLACK;
    public static Color selectedTextColor = Color.BLACK;
    public static Color disabledTextColor = new Color(160, 160, 160);
    public static Color infoTextColor = Color.GRAY;
    public static Color disabledInfoTextColor = Color.LIGHT_GRAY;
    public static Color tooltipTextColor = Color.BLACK;

    // Настройки фона компонентов
    public static Color topBgColor = Color.WHITE;
    public static Color topDarkBgColor = new Color(242, 242, 242);
    public static Color bottomBgColor = new Color(223, 223, 223);
    public static Color selectedBgColor = new Color(223, 220, 213);
    public static Color topSelectedBgColor = new Color(242, 242, 242);
    public static Color bottomSelectedBgColor = new Color(213, 213, 213);
    public static Color bottomLightSelectedBgColor = Color.WHITE;
    public static Color shineColor = Color.WHITE;

    // Настройки меню
    public static Color menuSelectionColor = selectedBgColor;
    public static Color rolloverMenuBorderColor = new Color(160, 160, 160);

    // Настройки разделителей
    public static Color separatorLightUpperColor = new Color(255, 255, 255, 5);
    public static Color separatorLightColor = Color.WHITE;
    public static Color separatorUpperColor = new Color(176, 182, 188, 5);
    public static Color separatorColor = new Color(176, 182, 188);

    // Настройки Nine-patch editor
    public static Stroke guidelinesStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 4,
            4 }, 0);
}
