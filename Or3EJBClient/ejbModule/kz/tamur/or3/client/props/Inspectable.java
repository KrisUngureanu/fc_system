package kz.tamur.or3.client.props;

public interface Inspectable {

    Property getProperties();

    /**
     * Получает свойства компонента. Если компонент необходимого типа, то карта свойств объекта переписывается
     */
    Property getNewProperties();

    Object getValue(Property prop);

    void setValue(Property prop, Object value);

    void setValue(Property prop, Object value, Object oldValue);

    String getTitle();

}
