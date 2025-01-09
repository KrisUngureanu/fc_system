package kz.tamur.comps.models;

/**
 * Класс для хранения значений пунктов выпадающего списка
 * Расширяет класс <code>EnumValue</code>
 * @author Sergey Lebedev
 *
 */
public class EnumValueToolTip extends EnumValue {
    // имя картинки для всплывающей подсказки
    public final String pathIco;

    /**
     * Конструктор класса
     * @param code код пункта
     * @param name заголовок пункта
     * @param pathIco имя картинки для всплывающей подсказки
     */
    public EnumValueToolTip(int code, String name, String pathIco) {
        super(code, name);
        // инициализация переменной картинки
        this.pathIco = (pathIco == null) ? "" : pathIco;
    }
}
