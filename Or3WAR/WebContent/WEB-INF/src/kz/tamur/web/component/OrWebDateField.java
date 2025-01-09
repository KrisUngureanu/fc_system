package kz.tamur.web.component;

import static kz.tamur.comps.Constants.NAME_RESOURCES;
import static kz.tamur.comps.Mode.RUNTIME;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.DateFieldPropertyRoot;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.webgui.WebDateField;
import kz.tamur.util.CopyButton;
import kz.tamur.rt.adapters.DateFieldAdapter;
import kz.tamur.or3.client.comps.interfaces.OrDateComponent;

import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 18.07.2006
 * Time: 19:07:00
 */
public class OrWebDateField extends WebDateField implements OrDateComponent {
    public static PropertyNode PROPS = new DateFieldPropertyRoot();
    private OrGuiContainer parent;

    private int tabIndex;
    private String copyRefPath;
    private String copyTitleUID;
    private DateFieldAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
	private String attentionExpr;
    
    OrWebDateField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        dateFormat = Constants.DD_MM_YYYY;
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        updateProperties();
	        switch(dateFormat) {
	            case Constants.DD_MM_YYYY:
	                setText(MASK_);
	                break;
	            case Constants.DD_MM_YYYY_HH_MM:
	                setText(MASK_1);
	                break;
	            case Constants.DD_MM_YYYY_HH_MM_SS:
	                setText(MASK_2);
	                break;
	            case Constants.DD_MM_YYYY_HH_MM_SS_SSS:
	                setText(MASK_3);
	                break;
	            case Constants.HH_MM_SS:
	                setText(MASK_4);
	                break;
	            case Constants.HH_MM:
	                setText(MASK_5);
	                break;
	            case Constants.DD_MM:
	                setText(MASK_6);
	                break;
	        }
	        adapter = new DateFieldAdapter(frame, this, isEditor);
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

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view").getChild("format");
        PropertyValue pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            dateFormat = pv.intValue();
        } 
        pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pn = getProperties().getChild("view").getChild("background");
        pv = getPropertyValue(pn.getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }

        if (mode == RUNTIME) {
            pn = getProperties().getChild("pov");
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            }
            pv = getPropertyValue(pn.getChild("tabIndex"));
            tabIndex = pv.intValue();
            
            pn = getProperties().getChild("pov").getChild("copy");
            pv = getPropertyValue(pn.getChild("copyPath"));
            if (!pv.isNull()) {
                copyRefPath = pv.stringValue(frame.getKernel());
            }
            if (copyRefPath != null) {
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
                }
            }
        	updateProperties(PROPS);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        pv = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("attention"));
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }

    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public String getaAttentionExpr() {
    	return attentionExpr;
    }
    
    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public String getCopyRefPath() {
        return copyRefPath;
    }

    public CopyButton getCopyBtn() {
        return null;
    }

    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        if (mode == RUNTIME) {
        	updateDescription();
        	LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame)frame).getSession().getConfigNumber());
            if (li != null) {
                ResourceBundle res = ResourceBundle.getBundle(NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
                changeTitles(res);
            }
        }
    }

    public DateFieldAdapter getAdapter() {
        return adapter;
    }

    public void setValue(String value) {
        try {
            setTextDirectly(value);
            Object val = getValue();
            Object newValue = adapter.changeValue(val);
            if ((newValue == null && val != null) ||
            		(newValue != null && !newValue.equals(val))) {
                setText(toString(newValue));
            }
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
            		+ "| interface id=" + ((WebFrame)frame).getObj().id
            		+ "| ref=" + adapter.getRef() 
            		+ "| value=" + value);
            log.error(ex.getMessage(), ex);
        }

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
