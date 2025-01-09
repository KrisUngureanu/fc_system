package kz.tamur.comps.ui.checkBox;

import java.awt.*;

import kz.tamur.comps.ui.ext.StyleConstants;

/**
 * Реализация стилистики UI компонента CheckBox
 * @author Sergey Lebedev
 *
 */
public final class OrCheckBoxStyle {
    /**
     * Цвет рамки.
     */
    public static Color borderColor = StyleConstants.borderColor;

    /**
     * Цвет тёмной рамки.
     */
    public static Color darkBorderColor = StyleConstants.darkBorderColor;

    /**
     * Цвет рамки неактивного компонента.
     */
    public static Color disabledBorderColor = StyleConstants.disabledBorderColor;

    /**
     * Верхний цвет фонового градиента.
     */
    public static Color topBgColor = StyleConstants.topBgColor;

    /**
     * Нижний цвет фонового градиента.
     */
    public static Color bottomBgColor = StyleConstants.bottomBgColor;

    /**
     *  Верхний цвет фонового градиента при выборе.
     */
    public static Color topSelectedBgColor = StyleConstants.topSelectedBgColor;

    /**
     * Нижний цвет фонового градиента при выборе.
     */
    public static Color bottomSelectedBgColor = StyleConstants.bottomSelectedBgColor;

    /**
     * Скругление в оформлении.
     */
    public static int round = StyleConstants.smallRound;

    /**
     * Ширина тени в оформлении
     */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /**
     * Границы по умолчанию
     */
    public static Insets margin = StyleConstants.margin;

    /**
     * Анимировать?
     */
    public static boolean animated = StyleConstants.animate;

    /**
     * Показывать тёмную рамку только при наведении мыши.
     */
    public static boolean rolloverDarkBorderOnly = StyleConstants.rolloverDarkBorderOnly;
}
