/**
 * 
 */
package kz.tamur.web.common;

/**
 * The Class Margin.
 *
 * @author Sergey Lebedev
 */
public class Margin {
    
    /** The margin top. */
    private int marginTop;
    
    /** The margin left. */
    private int marginLeft;

    /** The margin bottom. */
    private int marginBottom;
    
    /** The margin right. */
    private int marginRight;

    /**
     * Создание нового margin.
     *
     * @param marginTop the margin top
     * @param marginLeft the margin left
     * @param marginBottom the margin bottom
     * @param marginRight the margin right
     */
    public Margin(int marginTop, int marginLeft, int marginBottom, int marginRight) {
        super();
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginBottom = marginBottom;
        this.marginRight = marginRight;
    }

    /**
     * Получить margin top.
     *
     * @return the marginTop
     */
    public int getMarginTop() {
        return marginTop;
    }

    /**
     * Установить margin top.
     *
     * @param marginTop the marginTop to set
     */
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    /**
     * Получить margin left.
     *
     * @return the marginLeft
     */
    public int getMarginLeft() {
        return marginLeft;
    }

    /**
     * Установить margin left.
     *
     * @param marginLeft the marginLeft to set
     */
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    /**
     * Получить margin bottom.
     *
     * @return the marginBottom
     */
    public int getMarginBottom() {
        return marginBottom;
    }

    /**
     * Установить margin bottom.
     *
     * @param marginBottom the marginBottom to set
     */
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    /**
     * Получить margin right.
     *
     * @return the marginRight
     */
    public int getMarginRight() {
        return marginRight;
    }

    /**
     * Установить margin right.
     *
     * @param marginRight the marginRight to set
     */
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }
}
