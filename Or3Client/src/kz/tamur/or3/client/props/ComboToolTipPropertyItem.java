package kz.tamur.or3.client.props;

/**
 * Класс пунктов меню выпадающего списка с подсказками
 * 
 * @author Sergey Lebedev
 * 
 */
public class ComboToolTipPropertyItem extends ComboPropertyItem {

    // имя картинки для подсказки (например: XY_BAR_CHART_6.png)
    public final String pathIco;

    /**
     * Конструктор класса
     * 
     * @param id
     *            код пункта
     * @param title
     *            заголовок пункта
     * @param pathIco
     *            имя картинки для подсказки
     */
    public ComboToolTipPropertyItem(String id, String title, String pathIco) {
        super(id, title);
        // инициализация переменных класса
        this.pathIco = pathIco;
    }
}
