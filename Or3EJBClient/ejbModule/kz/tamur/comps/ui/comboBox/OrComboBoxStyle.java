package kz.tamur.comps.ui.comboBox;

import javax.swing.*;

import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.ImageUtils;
/**
 * Реализация стилистики UI компонента ComboBox
 * 
 * @author Sergey Lebedev
 *
 */
public final class OrComboBoxStyle {
    /**
     * Иконка "Развернуть список"
     */
    public static ImageIcon expandIcon = kz.tamur.rt.Utils.getImageIconFull("arrow.png");

    /**
     * Иконка "Свернуть список"
     */
    public static ImageIcon collapseIcon = ImageUtils.rotateImage180(expandIcon);

    /**
     * Icon side spacing
     */
    public static int iconSpacing = 5;

    /**
     * Draw combobox border
     */
    public static boolean drawBorder = StyleConstants.drawBorder;

    /**
     * Draw combobox focus
     */
    public static boolean drawFocus = true;

    /**
     * Скругление в оформлении.
     */
    public static int round = StyleConstants.round;

    /**
     * Ширина тени в оформлении
     */
    public static int shadeWidth = StyleConstants.shadeWidth;

    /**
     * Values scrolling using mouse wheel enabled
     */
    public static boolean mouseWheelScrollingEnabled = true;
}
