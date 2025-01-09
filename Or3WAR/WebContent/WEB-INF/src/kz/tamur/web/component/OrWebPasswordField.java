package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TextFieldPropertyRoot;
import kz.tamur.rt.adapters.*;
import kz.tamur.util.CopyButton;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import kz.tamur.or3.client.comps.interfaces.OrTextComponent;
import kz.tamur.web.common.webgui.WebPasswordField;

public class OrWebPasswordField extends WebPasswordField implements OrTextComponent {

    public static PropertyNode PROPS = new TextFieldPropertyRoot();
    private OrGuiContainer guiParent;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;

    private String copyTitleUID;
    private CopyButton copyBtn = null;

    private PasswordFieldAdapter adapter;
    private boolean deleteOnType = false;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    
    OrWebPasswordField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        if (mode == Mode.RUNTIME) {
	            constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	            prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	            maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	            minSize = PropertyHelper.getMinimumSize(this, id, frame);
	            //description = PropertyHelper.getDescription(this);
	        }
	
	        adapter = new PasswordFieldAdapter(frame, this, isEditor);
	        //setDisabledTextColor(Color.BLACK);
	        updateProperties();
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

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }
    
    public GridBagConstraints getConstraints() {
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml, id, frame);
        }
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        //Utils.processBorderProperties(this, frame);
        if (copyBtn != null) {
            copyBtn.setCopyTitle(frame.getString(copyTitleUID));
        }
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        if (mode == Mode.RUNTIME) {
        	updateProperties(PROPS);
            pv = getPropertyValue(
                    PROPS.getChild("pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
            pv = getPropertyValue(
                    PROPS.getChild("pov").getChild("deleteOnType"));
            deleteOnType = !pv.isNull() && pv.booleanValue();

            pv = getPropertyValue(
                    getProperties().getChild("pov").getChild("tabIndex"));

            if (!pv.isNull()) {
                tabIndex = pv.intValue();
            } else {
                tabIndex = pv.intValue();
            }
            if (adapter.getCopyRef() != null) {
                pn = getProperties().getChild("pov").getChild("copy");
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
/*                    if(copyBtn == null) {
                        setLayout(new BorderLayout());
                        copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        copyBtn.setCopyAdapter(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                            	adapter.doCopy();
                            }
                        });
                        add(copyBtn, BorderLayout.EAST);
                    }
*/                }
            }
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

    public int getTabIndex() {
        return tabIndex;
    }

    public String getCopyTitleUID() {
        return copyTitleUID;
    }

    public ComponentAdapter getAdapter() {
    	return adapter;
    }

    public void setValue(String value) {
        try {
            setTextDirectly(value);
            Object newValue = adapter.changeValue(value.length() > 0 ? value : null);
            if ((newValue == null && value != null) ||
                    !newValue.equals(value)) {
                setTextDirectly(newValue instanceof String ? (String)newValue : "");
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