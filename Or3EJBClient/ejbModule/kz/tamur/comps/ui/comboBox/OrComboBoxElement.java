package kz.tamur.comps.ui.comboBox;

import java.awt.Insets;

import kz.tamur.comps.ui.label.OrLabel;

/**
 * Реализация элемента выпадающего списка для компонента ComboBox
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrComboBoxElement extends OrLabel {

    /** The painter. */
    private OrComboBoxElementPainter painter;

    /** Общее количество элементов */
    private int totalElements;

    /** Индекс */
    private int index;

    /** Выбран? */
    private boolean isSelected;

    /**
     * Конструктор класса.
     */
    public OrComboBoxElement() {
        super();
        setPainter(new OrComboBoxElementPainter());
        setMargin(new Insets(0, 3, 0, 1));
        
    }

    /**
     * Обновление painter.
     */
    public void updatePainter() {
        super.setPainter(painter);
    }

    @Override
    public OrComboBoxElementPainter getPainter() {
        return painter;
    }

    /**
     * Установить painter.
     * 
     * @param painter
     *            the new painter
     */
    public void setPainter(OrComboBoxElementPainter painter) {
        this.painter = painter;
        updatePainter();
    }

    /**
     * Получить общее количество элементов.
     * 
     * @return количество элементов
     */
    public int getTotalElements() {
        return totalElements;
    }

    /**
     * Задать общее количество элементов.
     * 
     * @param totalElements
     *            новое значение
     */
    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * Получить индекс элемента.
     * 
     * @return индекс
     */
    public int getIndex() {
        return index;
    }

    /**
     * Установить индекс элемента.
     * 
     * @param index
     *            новое значение индекса элемента
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Выбран?
     * 
     * @return <code>true</code>, если выбран
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * установить флаг выбран/не выбран
     * 
     * @param isSelected
     *            новое значение
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

}