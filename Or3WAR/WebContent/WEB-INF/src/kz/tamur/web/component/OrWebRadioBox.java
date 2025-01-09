package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.RadioBoxPropertyRoot;
import kz.tamur.or3.client.comps.interfaces.OrRadioBoxComponent;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.RadioBoxAdapter;
import kz.tamur.web.common.webgui.*;

import org.jdom.Element;

import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

public class OrWebRadioBox extends WebPanel implements OrRadioBoxComponent {

    public static PropertyNode PROPS = new RadioBoxPropertyRoot();

    private OrGuiContainer guiParent;
    private String borderTitleUID;
    private Border standartBorder;
    private int tabIndex;

    public WebButtonGroup btnGroup = new WebButtonGroup();
    private int columncount = 0;

    private Map borderProps = new TreeMap();
    private int currentItemCount = 0;
    private WebRadioButton clear;
    private RadioBoxAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;

    private Color fontColor;

    private Color backgroundColor;

    OrWebRadioBox(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        updateProperties();

        if (mode == Mode.RUNTIME) {
            try {
                adapter = new RadioBoxAdapter(frame, this, false);
            } catch (KrnException e) {
            	log.error(e, e);
            }
            clear = new WebRadioButton("clear", this, getComponentCount());
            clear.setVisible(false);
            btnGroup.add(clear);
        }
        this.xml = null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        WebComponent[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            WebComponent c = comps[i];
            c.setEnabled(enabled);
        }
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    @Override
    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
        processBorderProperties();
    }

    private void updateProperties() {
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        setFont(pv.isNull() ? (Font) pn.getChild("font").getChild("fontG").getDefaultValue() : pv.fontValue());
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        fontColor = pv.isNull() ? (Color) pn.getChild("font").getChild("fontColor").getDefaultValue() : pv.colorValue();
        setForeground(fontColor);
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        backgroundColor = pv.isNull() ? (Color) pn.getChild("background").getChild("backgroundColor").getDefaultValue() : pv
                .colorValue();
        setBackground(backgroundColor);
        pv = getPropertyValue(pn.getChild("columncount"));
        if (!pv.isNull()) {
            columncount = pv.intValue();
            setBoxLayout(currentItemCount);
        }

        pn = getProperties().getChild("pov");
        tabIndex = getPropertyValue(pn.getChild("tabIndex")).intValue();

        pv = getPropertyValue(getProperties().getChild("view").getChild("sort"));
        if (!pv.isNull()) {
            if (pv.booleanValue()) {
                updateSort(true);
            }
        }
        pn = getProperties().getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            borderType = pv.isNull() ? (Border) pn.getChild("borderType").getDefaultValue() : pv.borderValue();
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String) pv.resourceStringValue().first;
            }
        }

        updateProperties(PROPS);

        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        processBorderProperties();
    }

    private void processBorderProperties() {
        borderTitle = frame.getString(borderTitleUID, "");
    }

    public void setBoxLayout(int choices) {
        if (columncount == 0) {
            columncount = choices;
        }
        if (columncount == 1) {
            setLayout(new WebGridLayout(choices, 1));
        } else if (columncount > 1) {
            float rowcount_ = choices / columncount;
            setLayout(new WebGridLayout((int) rowcount_, columncount));
        }
    }

    public void removeAllButtons() {
        do {
            for (Enumeration e = btnGroup.getElements(); e.hasMoreElements();) {
                WebRadioButton item_ = (WebRadioButton) e.nextElement();
                remove(item_);
                btnGroup.remove(item_);
            }
        } while (btnGroup.getElements().hasMoreElements());

    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer parent) {
        guiParent = parent;
    }

    @Override
    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    @Override
    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void updateSort(boolean isSort) {
        if (isSort) {
            WebComponent[] comps = getComponents();
            if (comps.length > 0) {
                List compsList = new ArrayList();
                for (int i = 0; i < comps.length; i++) {
                    WebComponent comp = comps[i];
                    if (comp instanceof WebRadioButton) {
                        compsList.add(comp);
                        remove(comp);
                    }
                }
                Collections.sort(compsList, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if (o1 != null && o2 != null) {
                            String t1 = ((WebRadioButton) o1).getText();
                            String t2 = ((WebRadioButton) o2).getText();
                            return t1.compareTo(t2);
                        }
                        return 0;
                    }
                });
                for (int i = 0; i < compsList.size(); i++) {
                    WebComponent component = (WebComponent) compsList.get(i);
                    add(component);
                }
            }
        }
    }

    public Border getBorderType() {
        return borderType;
    }

    public String getBorderTitleUID() {
        return borderTitleUID;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void setItems(RadioBoxAdapter.OrRadioItem[] items) {
        JsonArray children = new JsonArray();
        for (int i = 0; i < items.length; ++i) {
            if (items[i] != null) {
                WebRadioButton btn = new WebRadioButton(items[i].toString(), this, i);
                btnGroup.add(btn);
                add(btn);
                children.add(btn.putJSON(false));
            }
        }
        clear = new WebRadioButton("clear", this, items.length);
        btnGroup.add(clear);
        clear.setVisible(false);
        children.add(clear.putJSON(false));
        currentItemCount = items.length;
        setBoxLayout(items.length);
//        for (int i = 0; i < getComponentCount(); i++) {
//            WebComponent c = getComponent(i);
//            if (c instanceof WebRadioButton) {
//                c.setBackground(backgroundColor);
//                c.setFont(font);
//            }
//        }
    }
    
    
    @Override
    public void optionsChanged() {
		JsonArray children = new JsonArray();
	    for (Enumeration<WebButton> en = btnGroup.getElements(); en.hasMoreElements(); ) {
	        WebRadioButton btn = (WebRadioButton) en.nextElement();
	        children.add(btn.putJSON(false));
	    }

        removeChange("options");
        sendChange2("options", children);
    }

    public void selectIndex(int index) {
        WebButton b = (WebButton) getComponent(index);
        if (b != null) {
            b.setSelected(true);
            adapter.select(index);
        }
    }

    @Override
    public void setValue(String val) {
        int index = getComponentIndexByValue(val);
        if (index > -1) {
            selectIndex(index);
        }
    }

    private int getSelectedIndex(WebButtonGroup group) {
        int i;
        i = 0;
        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
            WebRadioButton b = (WebRadioButton) e.nextElement();
            if (!b.getText().equals("clear")) {
                if (b == group.getSelection()) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    @Override
    public void select(KrnObject obj, RadioBoxAdapter.OrRadioItem[] radioitems) {
        int i = 0;
        for (Enumeration e = btnGroup.getElements(); e.hasMoreElements() && i < radioitems.length;) {
            WebRadioButton item_ = (WebRadioButton) e.nextElement();
            if (!item_.getText().equals("clear")) {
                KrnObject radiobtn = radioitems[i].getObject();
                if (radiobtn.id == obj.id) {
                    item_.setSelected(true);
                    sendChangeProperty("text", i);
                }
                ++i;
            }
        }
    }

    public void clearAllSelection() {
        clear.setSelected(true);
        sendChangeProperty("text", getListChildren().size());
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        int index = getSelectedIndex(btnGroup);
        if (adapter.getContentRef() != null && adapter.getContentRef().getRefreshMode() == Constants.RM_ONCE) {
        	JsonArray children = new JsonArray();
            for (Enumeration<WebButton> en = btnGroup.getElements(); en.hasMoreElements(); ) {
                WebRadioButton btn = (WebRadioButton) en.nextElement();
                children.add(btn.putJSON(false));
            }
            obj.add("options", children);
            removeChange("options");
        }
        if (index > -1) {
        	WebRadioButton b = (WebRadioButton) getComponent(index);

            JsonObject property = new JsonObject();
            property.add("text", b.getNumber());

            property.add("e", toInt(isEnabled()));
            obj.add("pr", property);
        }
        sendChange(obj, isSend);

        return super.putJSON(true, true);
    }

    public int getColumncount() {
        return columncount;
    }

    public int getComponentIndexByValue(String val) {
        int i = 0;
        for (WebComponent comp : children) {
                if (comp instanceof WebRadioButton && val.equals(String.valueOf(((WebRadioButton) comp).getNumber()))) {
                    return i;
                }
            i++;
        }
        return -1;
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
