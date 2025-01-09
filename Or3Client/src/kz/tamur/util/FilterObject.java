package kz.tamur.util;

import kz.tamur.comps.Filter;

/**
 * Класс описывающий Фильтр-объект
 * 
 * @author Sergey Lebedev
 * 
 */
public class FilterObject {

    /** Заголовок фильтра. */
    private String title;

    /** Активирован?. */
    private boolean isEnabled;

    /** Фильтр. */
    private Filter filter;

    /**
     * Фильтр-Объект.
     * 
     * @param title
     *            заголовок фильтра
     * @param filter
     *            фильтр
     */
    public FilterObject(String title, Filter filter) {
        super();
        this.title = title;
        this.filter = filter;
        isEnabled = false;
    }

    /**
     * Получить заголовок объекта.
     * 
     * @return заголовок объекта
     */
    public String getTitle() {
        return title;
    }

    /**
     * Установить заголовок объекта.
     * 
     * @param title
     *            новый заголовок объекта
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Получить фильтр объекта.
     * 
     * @return the фильтр
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Установить фильтр для объекта.
     * 
     * @param filter
     *            новый фильтр
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Фильтр активирован?.
     * 
     * @return the <code>true</code> если активирован
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Изменить состояние фильтра.
     * 
     * @param isEnabled
     *            новое состояние фильтра
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
