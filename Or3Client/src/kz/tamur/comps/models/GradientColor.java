/**
 * 
 */
package kz.tamur.comps.models;

import java.awt.Color;

import kz.tamur.rt.Utils;

/**
 * Начальный цвет градиента
 * конечный цвет градиента
 * Ориентация градиента
 * Цикличность градиента
 * позиция отсчёта градиента для начального цвета
 * позиция отсчёта градиента для конечного цвета
 * активность
 * 
 * @author Sergey Lebedev
 * 
 */
public class GradientColor {

    /** Начальный цвет градиента */
    private Color startColor;

    /** конечный цвет градиента */
    private Color endColor;

    /** Ориентация градиента */
    private int orientation;

    /** Цикличность градиента */
    private boolean isCycle;

    /** позиция отсчёта градиента для начального цвета */
    private int positionStartColor;

    /** позиция отсчёта градиента для конечного цвета */
    private int positionEndColor;

    /** The is enable. */
    private boolean isEnable;

    public GradientColor(Color startColor, Color endColor, int orientation, boolean isCycle, int positionStartColor,
            int positionEndColor, boolean isEnable) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.orientation = orientation;
        this.isCycle = isCycle;
        this.positionStartColor = positionStartColor;
        this.positionEndColor = positionEndColor;
        this.isEnable = isEnable;
    }

    public GradientColor() {
        init();
    }

    public GradientColor(String text) {
        setGradient(text);
    }

    /**
     * Установить переменные по котрым будет построена градиентная заливка компонента.
     * 
     * @param gradient
     *            массив объектов, по которым задаются переменные
     *            тип - Object[]
     *            elemtent[0] - начальный цвет(Color)
     *            elemtent[1] - конечный цвет(Color)
     *            elemtent[2] - ориентация(int)
     *            elemtent[3] - цикличность(boolean)
     *            elemtent[4] - позиция начального цвета(int)
     *            elemtent[5] - позиция конечного цвета(int)
     */
    public void setGradient(Object gradient) {
        if (gradient instanceof String) {
            if (((String) gradient).isEmpty()) {
                init();
                return;
            }
            String[] value_ = ((String) gradient).split("[^0-9-]+");
            try {
                startColor = Color.decode(value_[0]);
                endColor = Color.decode(value_[1]);
                orientation = Integer.parseInt(value_[2]);
                isCycle = Utils.stringToBoolean(value_[3].toString());
                positionStartColor = Integer.parseInt(value_[4].toString());
                positionEndColor = Integer.parseInt(value_[5].toString());
                isEnable = (value_.length == 7) ? Utils.stringToBoolean(value_[6].toString()) : true;
            } catch (Exception e) {
                init();
                System.out.println("Некорректный формат задания градиента, произведена его инициализация.");
            }

        } else if (gradient instanceof Object[]) {
            Object[] value = (Object[]) gradient;
            startColor = (Color) value[0];
            endColor = (Color) value[1];
            orientation = Integer.parseInt(value[2].toString());
            isCycle = Boolean.parseBoolean(value[3].toString());
            positionStartColor = Integer.parseInt(value[4].toString());
            positionEndColor = Integer.parseInt(value[5].toString());
            isEnable = (value.length == 7) ? Utils.stringToBoolean(value[6].toString()) : true;
        } else if (gradient == null) {
            init();
        }
    }

    private void init() {
        startColor = null;
        endColor = null;
        orientation = 0;
        isCycle = true;
        positionStartColor = 0;
        positionEndColor = 50;
        isEnable = false;
    }

    /**
     * Установить gradient.
     * 
     * @param gradient
     *            the new gradient
     */
    public void setGradient(GradientColor gradient) {
        startColor = gradient.getStartColor();
        endColor = gradient.getEndColor();
        orientation = gradient.getOrientation();
        isCycle = gradient.isCycle();
        positionStartColor = gradient.getPositionStartColor();
        positionEndColor = gradient.getPositionEndColor();
        isEnable = gradient.isEnabled();
    }

    /**
     * Установить начальный цвет градиента.
     * 
     * @param startColor
     *            новый цвет
     */
    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    /**
     * Установить конечный цвет градиента
     * 
     * @param endColor
     *            новый цвет
     */
    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    /**
     * Установить ориентацию заливки копонента.
     * 
     * @param orientation
     *            the new orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Установить цикличность
     * 
     * @param isCycle
     *            цикличность
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * Установить позицию начального цвета градиента
     * 
     * @param positionStartColor
     *            позиция(в процентах от линии градиента)
     */
    public void setPositionStartColor(int positionStartColor) {
        this.positionStartColor = positionStartColor;
    }

    /**
     * Установить позицию конечного цвета градиента
     * 
     * @param positionEndColor
     *            позиция(в процентах от линии градиента)
     */
    public void setPositionEndColor(int positionEndColor) {
        this.positionEndColor = positionEndColor;
    }

    /**
     * Задана цикличность?
     * 
     * @return true, если циклить градиент
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * Получить позицию начального цвета градиента
     * 
     * @return the position start color
     */
    public int getPositionStartColor() {
        return positionStartColor;
    }

    /**
     * Получить позицию конечного цвета градиента
     * 
     * @return позиция конечного цвета градиента
     */
    public int getPositionEndColor() {
        return positionEndColor;
    }

    /**
     * Получить начальный цвет градиента
     * 
     * @return начальный цвет градиента
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * Получить конечный цвет градиента
     * 
     * @return конечный цвет градиента
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * Получить ориентацию заливки компонента.
     * 
     * @return ориентация заливки
     */
    public int getOrientation() {
        return orientation;
    }

    public String toString() {
        String enable = Utils.objectToString(isEnable);
        StringBuilder sb = new StringBuilder();
        final String s = ", ";
        if (startColor != null && endColor != null) {
            sb.append(startColor.getRGB());
            sb.append(s);
            sb.append(endColor.getRGB());
            sb.append(s);
            sb.append(orientation);
            sb.append(s);
            sb.append(Utils.objectToString(isCycle));
            sb.append(s);
            sb.append(positionStartColor);
            sb.append(s);
            sb.append(positionEndColor);
            sb.append(s);
            sb.append(enable == null ? "1" : enable);
        }
        return sb.toString();
    }

    public boolean isEnabled() {
        return isEnable;
    }
    
    public boolean isEmpty() {
        return startColor == null && endColor == null;
    }

    public void setEnabled(boolean enabled) {
        this.isEnable = enabled;
    }
}
