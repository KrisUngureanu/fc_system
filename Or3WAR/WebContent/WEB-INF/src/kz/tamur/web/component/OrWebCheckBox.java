package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.CheckBoxPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.CheckBoxAdapter;
import kz.tamur.web.common.webgui.WebCheckBox;
import kz.tamur.util.Pair;
import kz.tamur.or3.client.comps.interfaces.OrCheckBoxComponent;

import org.jdom.Element;

import javax.swing.border.Border;
import javax.swing.*;

import java.awt.*;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 19.01.2007
 * Time: 12:17:00
 */
public class OrWebCheckBox extends WebCheckBox implements OrCheckBoxComponent {
    public static PropertyNode PROPS = new CheckBoxPropertyRoot();

    protected boolean isSelected;
    private OrGuiContainer parent;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private String borderTitleUID;
    private String title;
    private String titleUID;
    private CheckBoxAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */

    OrWebCheckBox(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrCheckBox", false, xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        updateProperties();
	        adapter = new CheckBoxAdapter(frame, this, isEditor);
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

        this.xml = null;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
            return constraints;
    }

    public void setLangId(long langId) {
        String text = frame.getString(titleUID);
        title = "Безымянный".equals(text) || " ".equals(text) ? "" : text;
        setText(title);
        updateDescription();
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            String text = frame.getString(titleUID);
            title = "Безымянный".equals(text) || " ".equals(text) ? "" : text;
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        updateProperties(PROPS);
        PropertyNode pn = PROPS.getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
        if (!pv.isNull()) {
            setEnabled(!pv.booleanValue());
        } else {
            setEnabled(true);
        }

        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public Dimension getPrefSize() {
        return prefSize;
    }

    public Dimension getMaxSize() {
        return maxSize;
    }

    public Dimension getMinSize() {
        return minSize;
    }

    public String getBorderTitleUID() {
        return borderTitleUID;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setValue(Object value) {
        setSelected(Boolean.TRUE.equals(value));
    }

    public Object getValue() {
        return isSelected();
    }

    public void setValue(String value) {
        try {
            setSelectedDirectly("x".equals(value) || Boolean.valueOf(value));
            Long val = ("x".equals(value) || "true".equals(value)) ? 1L : 0;
            adapter.itemStateChanged(val);
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame) frame).getSession().getUserName() + "| interface id="
                    + ((WebFrame) frame).getObj().id + "| ref=" + adapter.getRef() + "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }

    public CheckBoxAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
}
