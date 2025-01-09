package kz.tamur.comps;

import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;

/**
 * Created by IntelliJ IDEA.
 * Date: 05.04.2004
 * Time: 19:39:05
 * 
 * @author berik
 */
public class EmptyPlace extends JLabel implements Place, OrGuiComponent {

    private OrGuiContainer parent;
    private boolean isCopy;

    /**
     * Конструктор класса empty place.
     */
    EmptyPlace() {
        setOpaque(false);
    }

    @Override
    public PropertyNode getProperties() {
        return null;
    }

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return null;
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
    }

    @Override
    public GridBagConstraints getConstraints() {
        return null;
    }

    @Override
    public void setSelected(boolean isSelected) {
    }

    @Override
    public Element getXml() {
        return null;
    }

    @Override
    public int getComponentStatus() {
        return Constants.NONE_COMP;
    }

    @Override
    public void setLangId(long langId) {
    }

    @Override
    public int getMode() {
        return Mode.DESIGN;
    }

    @Override
    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public void setCopy(boolean copy) {
        this.isCopy = copy;
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return parent;
    }

    @Override
    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    @Override
    public void setXml(Element xml) {
    }

    @Override
    public Dimension getPrefSize() {
        return null;
    }

    @Override
    public Dimension getMaxSize() {
        return null;
    }

    @Override
    public Dimension getMinSize() {
        return null;
    }

    /**
     * Получить border title uid.
     * 
     * @return border title uid.
     */
    public String getBorderTitleUID() {
        return null;
    }

    /**
     * Получить tab index.
     * 
     * @return tab index.
     */
    public int getTabIndex() {
        return -1;
    }

    @Override
    public byte[] getDescription() {
        return new byte[0];
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    /**
     * Установить adapter.
     * 
     * @param adapter
     *            новое значение adapter.
     */
    public void setAdapter(ComponentAdapter adapter) {
    }

    @Override
    public String getVarName() {
        return null;
    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
    }

    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners, java.util.List<OrGuiComponent> listForDel) {
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return null;
    }

    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }
}
