package kz.tamur.comps.models;

import java.awt.Color;

/**
 * Класс цвета с дополнительным атрибутом активности
 * Атрибут поясняет, активен цвет или нет
 * @author Sergey Lebedev
 *
 */
public class ColorAct extends Color {
    
    /** флаг активности цвета */
    private boolean isEnable = true;

    /**
     * Конструктор нового класса
     *
     * @param color цвет
     * @param isEnable флаг активности
     */
    public ColorAct(Color color, boolean isEnable) {
        super(color.getRed(), color.getGreen(), color.getBlue());
        this.isEnable = isEnable;
    }

    /**
     * Проверяет, является ли цвет активным.
     *
     * @return <code>true</code> если да
     */
    public boolean isEnable() {
        return isEnable;
    }

    /**
     * Установить флаг активности цвета
     *
     * @param isEnable новый флаг активности
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * Получить строку представления цвета в формате RGB плюс флаг активности
     *
     * @return строка
     */
    public String getRGBAct() {
        return this.getRGB() + (isEnable ? " 1" : " 0");
    }
}
