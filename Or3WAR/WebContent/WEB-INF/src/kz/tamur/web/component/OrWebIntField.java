package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.*;
import kz.tamur.comps.models.IntFieldPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.IntFieldAdapter;
import kz.tamur.util.CopyButton;
import kz.tamur.web.common.webgui.WebTextField;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

public class OrWebIntField extends WebTextField implements OrTextComponent {
    public static PropertyNode PROPS = new IntFieldPropertyRoot();
    private OrGuiContainer guiParent;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private String copyRefPath;
    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private IntFieldAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    
    OrWebIntField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
        
        try {
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        //description = PropertyHelper.getDescription(this);
	        updateProperties();
	        adapter = new IntFieldAdapter(frame, this, isEditor);
	        setHorizontalAlignment(SwingConstants.RIGHT);
	        setType(Constants.INT_TYPE);
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

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
        //Utils.processBorderProperties(this, frame);
    }

    private void updateProperties() {
        PropertyValue pv = null;
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            //setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        
        pv = getPropertyValue(pn.getChild("formatting"));
        formatting =  pv.booleanValue();
        
        pn = getProperties().getChild("constraints");
        pv = getPropertyValue(pn.getChild("charsNumber"));
        if (!pv.isNull()) {
            int count = pv.intValue();
            setCharsLimit(count);
        }
        pv = getPropertyValue(pn.getChild("exclude"));
        if (!pv.isNull()) {
            setExcludeChars(pv.stringValue(frame.getKernel()));
        }
        pv = getPropertyValue(pn.getChild("include"));
        if (!pv.isNull()) {
            setIncludeChars(pv.stringValue(frame.getKernel()));
        }
        
        if (mode == Mode.RUNTIME) {
            pn = getProperties().getChild("pov").getChild("copy");
            pv = getPropertyValue(pn.getChild("copyPath"));
            if (!pv.isNull()) {
                copyRefPath = pv.stringValue(frame.getKernel());
            }
            if (copyRefPath != null) {
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
/*
                    if(copyBtn == null) {
                        setLayout(new BorderLayout());
                        copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        add(copyBtn, BorderLayout.WEST);
                    }
*/
                }
            }
            pn = getProperties().getChild("pov");
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
            pv = getPropertyValue(pn.getChild("tabIndex"));
            tabIndex = pv.intValue();
            
        	updateProperties(PROPS);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        //Utils.processBorderProperties(this, frame);
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public String getCopyRefPath() {
        return copyRefPath;
    }

    public CopyButton getCopyBtn() {
        return copyBtn;
    }

    public IntFieldAdapter getAdapter() {
        return adapter;
    }

    public Object getValue() {
        String s = getText();
        if (s.length() == 0)
            return null;
        else {
            try {
                return Long.valueOf(s);
            } catch (Exception pe) {
                return null;
            }
        }
    }

    public void setValue(String value) {
        try {
            setTextDirectly(value);
            Object val = transform(value);
            Object newVal = adapter.changeValue(val);
            if ((newVal == null && val != null) ||
                (newVal != null && !newVal.equals(val))) {
                setValue(newVal);
            }
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
            		+ "| interface id=" + ((WebFrame)frame).getObj().id
            		+ "| ref=" + adapter.getRef() 
            		+ "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }

    public Object transform(String value) {
        Object val = null;
        try {
            val = Long.valueOf(value);
        } catch (Exception pe) {
        }
        return val;
    }

    public JsonObject getJsonEditor() {
    	if (getCharsLimit() > 0 || getIncludeChars() != null || getExcludeChars() != null) {
    		return new JsonObject().add("type", "intfield")
    				.add("options", new JsonObject().add("maxlength", getCharsLimit())
    						.add("include", getIncludeChars() != null ? getIncludeChars() : "")
    						.add("exclude", getExcludeChars() != null ? getExcludeChars() : ""));
    	} else
    		return new JsonObject().add("type", "intfield")
    				.add("options", new JsonObject());
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
