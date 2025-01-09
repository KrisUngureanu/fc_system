package kz.tamur.comps;

import kz.tamur.comps.models.ImageColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import java.awt.*;

/**
 * Класс столбец с иконками для таблицы
 *
 * @author Sergey Lebedev
 */
public class OrImageColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new ImageColumnPropertyRoot();

    /**
     * конструктор класса
     *
     * @param xml the xml
     * @param mode the mode
     * @param frame the frame
     * @throws KrnException 
     */
    OrImageColumn(Element xml, int mode, OrFrame frame) throws KrnException {
        super(xml, mode, frame);
        editor = new OrLabel(xml, mode, frame, true);
    }

    
    public GridBagConstraints getConstraints() {
        return null;
    }

    
    public Element getXml() {
        return super.getXml();
    }

    
    public PropertyNode getProperties() {
        return PROPS;
    }

    
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, getXml(), frame);
    }

    
    public void setPropertyValue(PropertyValue value) {
        super.setPropertyValue(value);
    }

    
    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }

    
    public int getMode() {
        return super.getMode();
    }

    /**
     * Получить индекс табуляции.
     *
     * @return -1 данный компонент не должен переводить на себя фокус при табуляции
     */
    public int getTabIndex() {
        return -1;
    }
}
