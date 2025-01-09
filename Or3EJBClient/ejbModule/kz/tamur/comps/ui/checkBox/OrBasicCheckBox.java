package kz.tamur.comps.ui.checkBox;

import javax.swing.JCheckBox;

/**
 * Класс реализует основной компонент "флажок"
 * При его использовании можно отключать анимацию UI.
 * 
 * @author Sergey Lebedev
 */
public class OrBasicCheckBox extends JCheckBox {

    /** Анимировать прорисовку?. */
    private boolean isAnimate = true;

    /**
     * Конструктор класса.
     */
    public OrBasicCheckBox() {
        super();
    }

    /**
     * Конструктор класса.
     * 
     * @param text
     *            подпись флажка
     * @param selected
     *            Первоначальное значение
     */
    public OrBasicCheckBox(String text, boolean selected) {
        super(text, selected);
    }

    /**
     * Конструктор класса.
     * 
     * @param text
     *            подпись флажка
     */
    public OrBasicCheckBox(String text) {
        super(text);
    }

    /**
     * Анимировать прорисовку?
     * 
     * @return <code>true</code> если анимировать.
     */
    public boolean isAnimate() {
        return isAnimate;
    }

    /**
     * Включить/выключить анимацию.
     * 
     * @param isAnimate
     *            <code>true</code> если анимировать.
     */
    public void setAnimate(boolean isAnimate) {
        this.isAnimate = isAnimate;
    }

}
