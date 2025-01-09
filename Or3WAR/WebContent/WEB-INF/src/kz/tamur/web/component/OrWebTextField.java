package kz.tamur.web.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TextFieldPropertyRoot;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.TextFieldAdapter;
import kz.tamur.web.common.webgui.WebTextField;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 15.07.2006
 * Time: 10:45:21
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTextField extends WebTextField implements OrTextComponent {

    private TextFieldAdapter adapter;
    public static PropertyNode PROPS = new TextFieldPropertyRoot();
    private OrGuiContainer guiParent;
    private int tabIndex;

    private String copyTitleUID;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    
    public OrWebTextField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        //description = PropertyHelper.getDescription(this);
	
	      	adapter = new TextFieldAdapter(frame, this, isEditor);
	        // Если не колонка, то слушаем фокус
	        updateProperties();
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

      	// Не храним XML в режиме выполнения компонента
        this.xml = null;
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
        pv = getPropertyValue(pn.getChild("alignmentText"));        
        if (!pv.isNull()) {
             setHorizontalAlignment(pv.intValue());
        }
        if (mode == Mode.RUNTIME) {
        	updateProperties(PROPS);
            pv = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
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
            
            PropertyNode pnc = getProperties().getChild("constraints");
            pv = getPropertyValue(pnc.getChild("upperCase"));
            if (!pv.isNull()) {
                setUpperFirstChar(pv.booleanValue());
                                                   }
            pv = getPropertyValue(pnc.getChild("upperAllChar"));
            if (!pv.isNull()) {
                setUpperAllChars(pv.booleanValue());
            }

            pv = getPropertyValue(getProperties().getChild("pov").getChild("tabIndex"));

            if (!pv.isNull()) {
                tabIndex = pv.intValue();
            } else {
                tabIndex = pv.intValue();
            }
            switch (tabIndex) {
            	case -1:
            		alwaysFocused = true;
            		firstFocused = true;
            		break;
            	case -2:
            		alwaysFocused = true;
            		break;
            	case -3:
            		firstFocused = true;
            		break;
            }
            
            if (adapter.getCopyRef() != null) {
                pn = getProperties().getChild("pov").getChild("copy");
                pv = getPropertyValue(pn.getChild("copyTitle"));
                if (!pv.isNull()) {
                    copyTitleUID = (String)pv.resourceStringValue().first;
/*
                    if(copyBtn == null) {
                        setLayout(new BorderLayout());
                        copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
                        copyBtn.setCopyAdapter(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                            	adapter.doCopy();
                            }
                        });
                        add(copyBtn, BorderLayout.EAST);
                    }
*/
                }
            }
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        //Utils.processBorderProperties(this, frame);
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
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public ComponentAdapter getAdapter() {
    	return adapter;
    }

	public void setValue(String value) {
		try {
			setTextDirectly(value);
			Object newValue = adapter.changeValue(value.length() > 0 ? value : null);
			if ((newValue == null && value != null) || !newValue.equals(value)) {
				setTextDirectly(newValue instanceof String ? (String) newValue : "");
			}
		} catch (Exception ex) {
			log.error("|USER: " + ((WebFrame) frame).getSession().getUserName() + "| interface id=" + ((WebFrame) frame).getObj().id + "| ref=" + adapter.getRef() + "| value=" + value);
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