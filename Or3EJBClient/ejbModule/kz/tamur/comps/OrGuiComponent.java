package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;

import java.awt.*;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.03.2004
 * Time: 18:14:33
 */
public interface OrGuiComponent {
    GridBagConstraints getConstraints();
    void setSelected(boolean isSelected);
    PropertyNode getProperties();
    PropertyValue getPropertyValue(PropertyNode prop);
    void setPropertyValue(PropertyValue value);
    Element getXml();
    int getComponentStatus();
    void setLangId(long langId);
    int getMode();
    //For copy process
    boolean isCopy();
    void setCopy(boolean copy);
    OrGuiContainer getGuiParent();
    void setGuiParent(OrGuiContainer parent);
    void setXml(Element xml);
    Dimension getPrefSize();
    Dimension getMaxSize();
    Dimension getMinSize();
    
    /**
     * Получить уникальный идентификатор компонента.
     *
     * @return uuid - идентификатор.
     */
    String getUUID();
    void setEnabled(boolean isEnabled);
    boolean isEnabled();
    byte[] getDescription();
    
    /**
     * Получить адаптер компонента.
     *
     * @return адаптер компонента.
     */
    ComponentAdapter getAdapter();
    
    /**
     * Получить имя переменной компонента.
     *
     * @return имя переменной.
     */
    String getVarName();
    
    /**
     * Задать для компонента слушатель (другой компонет, который будет реагировать на состояние этого компонента).
     * 
     * @param comp
     *             слушатель компонента
     */
    void setComponentChange(OrGuiComponent comp);
    
    /**
     * Sets the list listeners.
     * 
     * @param listListeners
     *            the list listeners
     * @param listForDel
     *            the list for del
     */
    void setListListeners(java.util.List<OrGuiComponent> listForAdd, java.util.List<OrGuiComponent> listForDel);
    
    /**
     * Получить список слушателей компонента.
     * Используется в дизайнере, при необходимости раскрыть, открыть родительский комопонент 
     * чтобы отобразить текущий компонент когда его выбрали в дереве компонентов.
     * 
     * @return слушатели компонента.
     */
    java.util.List<OrGuiComponent> getListListeners();
    
    /**
     * Получить всплывающую подсказку.
     * 
     * @return текст всплывающей подсказки
     */
    String getToolTip();
    
    /**
     * Обновление динамических свойств компонента.
     */
    void updateDynProp();
    
    /**
     * Получить позицию отображения компонента на верхней панели веб интерфейса.
     * 
     * @return position позиция компонента.
     */
    int getPositionOnTopPan();
    
    /**
     * Проверяет, отображать ли компонент на верхней панели веб-интерфейса
     * 
     * @return <code>true</code>, если отображать
     */
    boolean isShowOnTopPan();
    
    /**
     * Установить флаг привлечения внимания к компоненту.
     * Обычно это мигание компонента на интерфейса.
     * 
     * @param attention <code>true</code>, если наобходимо привлечь внимание
     */
    void setAttention(boolean attention);
}
