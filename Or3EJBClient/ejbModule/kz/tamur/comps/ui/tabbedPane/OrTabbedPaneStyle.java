package kz.tamur.comps.ui.tabbedPane;

import java.awt.*;

import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.TabStretchType;
import kz.tamur.comps.ui.ext.TabbedPaneStyle;
import kz.tamur.rt.Utils;


public final class OrTabbedPaneStyle {
    /**
     * Tab content margin
     */
    public static TabbedPaneStyle tabbedPaneStyle = TabbedPaneStyle.standalone;

    /**
     * Цвет начала градиента заголовка фоновой вкладки
     */
    public static Color topBg = Utils.getMainColor();

    /**
     * Цвет окончания градиента заголовка фоновой вкладки
     */
    public static Color bottomBg = Utils.getSilverColor();

    /**
     * Цвет начала градиента заголовка выбранной вкладки
     */
    public static Color selectedTopBg = Color.white;

    /**
     * Не используется, в UI вместо этого цвета берётся фоновый цвет закладки
     * Цвет окончания градиента заголовка выбранной вкладки
     */
    public static Color selectedBottomBg = Utils.getMainColor();
    
    /**
     * Округление углов.
     */
    public static int round = StyleConstants.largeRound;

    /**
     * Decoration shade width
     */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /**
     * Rotate tab insets so they will not be the same for different tab positions
     */
    public static boolean rotateTabInsets = false;

    /**
     * Отступы контента вкладок
     */
    public static Insets contentInsets = new Insets(0, 0, 0, 0);

    /**
     * Отступы заголовка вкладки
     */
    public static Insets tabInsets = new Insets(3, 4, 3, 4);

    /**
     * Empty pane Painter (when there are no available tabs)
     */
    public static Painter painter = null;

    /**
     * Отступ заголовка крайней левой вкладки от края панели.
     */
    public static int tabRunIndent = 0;

    /**
     * Выполняемое вкладкой наложение (в пикселях)
     */
    public static int tabOverlay = 1;

    /**
     * Тип заголовков вкладок
     */
    public static TabStretchType tabStretchType = TabStretchType.never;
}
