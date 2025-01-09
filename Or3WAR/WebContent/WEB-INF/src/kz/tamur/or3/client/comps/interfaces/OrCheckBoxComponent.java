package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 19.01.2007
 * Time: 16:29:19
 */
public interface OrCheckBoxComponent extends OrGuiComponent {
    // TODO Перенести в OrGuiComponent после перевода всех
    // компонентов на новый вариант
    void setValue(Object value);
    Object getValue();
    void setSelected(boolean isSelected);
}
