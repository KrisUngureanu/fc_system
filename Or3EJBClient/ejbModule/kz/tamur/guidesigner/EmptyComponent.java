package kz.tamur.guidesigner;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.MapMap;

import java.awt.*;
import java.util.List;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 17.05.2004
 * Time: 9:52:04
 * 
 * @author Vital
 */
public class EmptyComponent implements OrGuiComponent {

    private OrGuiContainer guiParent;
    private boolean isCopy;

    private static final PropertyNode PROPS = new PropertyNode(null, "Отсутствует", -1, null, false, null);

    @Override
    public GridBagConstraints getConstraints() {
        return null;
    }

    @Override
    public void setSelected(boolean isSelected) {

    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return null;
    }

    @Override
    public void setPropertyValue(PropertyValue value) {

    }

    @Override
    public Element getXml() {
        return null;
    }

    @Override
    public int getComponentStatus() {
        return 0;
    }

    @Override
    public void setLangId(long langId) {

    }

    @Override
    public int getMode() {
        return Mode.DESIGN;
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
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
     * Является ли component enabled.
     * 
     * @return <code>true</code>, если component enabled.
     */
    public boolean isComponentEnabled() {
        return false;
    }

    /**
     * Получить strings.
     * 
     * @param strings
     *            strings.
     * @return strings.
     */
    public void getStrings(MapMap strings) {

    }

    /**
     * Установить strings.
     * 
     * @param strings
     *            новое значение strings.
     */
    public void setStrings(MapMap strings) {

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
    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public void setCopy(boolean copy) {
        isCopy = copy;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public byte[] getDescription() {
        return new byte[0]; // To change body of implemented methods use File | Settings | File Templates.
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
