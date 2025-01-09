package kz.tamur.web.component;

import kz.tamur.comps.models.MemoFieldPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.util.CopyButton;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.webgui.WebMemoField;
import kz.tamur.rt.adapters.MemoFieldAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.Util;
import kz.tamur.or3.client.comps.interfaces.OrMemoComponent;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

public class OrWebMemoField extends WebMemoField implements OrMemoComponent {
    public static PropertyNode PROPS = new MemoFieldPropertyRoot();
    private OrGuiContainer guiParent;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private String copyRefPath;
    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private MemoFieldAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    
    public OrWebMemoField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        updateProperties();
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        //description = PropertyHelper.getDescription(this);
	        if (this.mode == Mode.DESIGN) {
	            setEnabled(false);
	        }
            adapter = new MemoFieldAdapter(frame, this, isEditor);
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
            return mode == Mode.RUNTIME? constraints:PropertyHelper.getConstraints(PROPS, xml, id, frame);
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
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("useWYSIWYGforWEB"));
        if (!pv.isNull()) {
            wysiwyg = pv.booleanValue();
        }
        
        pn = getProperties().getChild("pov");
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
            pn = getProperties().getChild("pov").getChild("copy");
            pv = getPropertyValue(pn.getChild("copyPath"));
            if (!pv.isNull()) {
                copyRefPath = pv.stringValue(frame.getKernel());
            }
            if (copyRefPath != null) {
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
                    if(copyBtn == null) {
                        //setLayout(new BorderLayout());
                        //copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        //add(copyBtn, BorderLayout.EAST);
                    }
                }
            }
        	updateProperties(PROPS);
        }
        pv = getPropertyValue(getProperties().getChild("pov").getChild("tabIndex"));
        tabIndex = pv.intValue();
        
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
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

    public ComponentAdapter getAdapter() {
    	return adapter;
    }

    public void setValue(String value) {
        try {
            setTextDirectly(value);
            try {
                adapter.changeValue((value != null && value.length() > 0) ? value : null);
            } catch (Exception ex) {
                log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
                		+ "| interface id=" + ((WebFrame)frame).getObj().id
                		+ "| ref=" + adapter.getRef() 
                		+ "| value=" + value);
                log.error(ex.getMessage(), ex);
                Util.showErrorMessage(this, ex.getMessage(), "");
            }
        } catch (Exception ex) {
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
