package kz.tamur.comps.ui.comboBox;

import java.awt.*;

import kz.tamur.comps.ui.ext.DefaultPainter;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.LafUtils;

/**
 * Отрисовщик элементов выпадающего списка ComboBox
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrComboBoxElementPainter extends DefaultPainter<OrComboBoxElement> {
    public OrComboBoxElementPainter() {
        super();
    }

    public Insets getMargin(OrComboBoxElement element) {
        return element.getIndex() != -1 ? new Insets(element.getIndex() == 0 ? 2 : 3, 2,
                element.getIndex() + 1 == element.getTotalElements() ? 2 : 3, 2) : new Insets(2, 2, 2, 2);
    }

    public void paint(Graphics2D g2d, Rectangle bounds, OrComboBoxElement element) {
        if (element.getIndex() == -1) {
            paintBoxBackground(g2d, element);
        } else if (element.isSelected()) {
            paintSelectedBackground(g2d, element);
        } else {
            paintDeselectedBackground(g2d, element);
        }
    }

    /**
     * Отрисовка коробки пункта
     * 
     * @param g2d
     * @param element
     */
    protected void paintBoxBackground(Graphics2D g2d, OrComboBoxElement element) {

    }

    /**
     * Отрисовка выделенного пункта списка
     * 
     * @param g2d
     * @param element
     */
    protected void paintSelectedBackground(Graphics2D g2d, OrComboBoxElement element) {
        // Фон
        int height = element.getHeight();
        int startV = (int) (height / 100f * 50);
        int endV = (int) (height / 100f * 100);
        g2d.setPaint(LafUtils.getGradientPaint(0, startV, 0, endV, StyleConstants.bottomSelectedBgColor, StyleConstants.topSelectedBgColor, true));

        g2d.fillRect(0, 0, element.getWidth(), element.getHeight());

        // Рамка
        g2d.setPaint(element.isEnabled() ? StyleConstants.darkBorderColor : StyleConstants.disabledBorderColor);
        if (element.getIndex() != 0) {
            g2d.drawLine(0, 0, element.getWidth() - 1, 0);
        }
        if (element.getIndex() < element.getTotalElements() - 1) {
            g2d.drawLine(0, element.getHeight() - 1, element.getWidth() - 1, element.getHeight() - 1);
        }
    }

    /**
     * Отрисовка невыделенного пункта списка
     * 
     * @param g2d
     * @param element
     */
    protected void paintDeselectedBackground(Graphics2D g2d, OrComboBoxElement element) {
        // Фон
        int height = element.getHeight();
        int startV = (int) (height / 100f * 50);
        int endV = (int) (height / 100f * 100);
        g2d.setPaint(LafUtils.getGradientPaint(0, startV, 0, endV, StyleConstants.topBgColor, StyleConstants.bottomBgColor, true));

        g2d.fillRect(0, 0, element.getWidth(), element.getHeight());
    }
}