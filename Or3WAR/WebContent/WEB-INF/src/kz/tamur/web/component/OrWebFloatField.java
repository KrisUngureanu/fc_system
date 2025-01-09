package kz.tamur.web.component;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.*;
import kz.tamur.comps.models.FloatFieldPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.FloatFieldAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.util.CopyButton;
import kz.tamur.util.ThreadLocalNumberFormat;
import kz.tamur.web.common.webgui.WebFormattedTextField;

import org.jdom.Element;

import javax.swing.*;

import java.awt.*;
import java.text.ParseException;

import kz.tamur.or3.client.comps.interfaces.OrTextComponent;

public class OrWebFloatField extends WebFormattedTextField implements OrTextComponent {
    public static PropertyNode PROPS = new FloatFieldPropertyRoot();
    private OrGuiContainer guiParent;
    private int tabIndex;
    private String copyRefPath;
    private String copyTitleUID;
    private CopyButton copyBtn = null;
    private FloatFieldAdapter adapter;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private int precision = 0;
    private boolean bitSeparation;
    
    OrWebFloatField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
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
	        PropertyNode pn = PROPS.getChild("constraints").getChild("formatPattern");
	        PropertyValue pv = getPropertyValue(pn);
	        String pattern = "";
	        if (!pv.isNull()) {
	            pattern = pv.stringValue(frame.getKernel());
	        } else {
	            pattern = pn.getDefaultValue().toString();
	        }
            precision = pattern.length() - pattern.indexOf('.') - 1;

            ThreadLocalNumberFormat pat = new ThreadLocalNumberFormat(pattern, ',', (char)0, false, precision, -1);
	        FloatFormatter fmt = new FloatFormatter(pat);
	        setFormatter(fmt);
	        
	        pn = getProperties().getChild("constraints");
	        pv = getPropertyValue(pn.getChild("charsNumber"));
	        if (!pv.isNull()) {
	            int count = pv.intValue();
	            setCharsLimit(count);
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
	/*
	                if(copyBtn == null) {
	                    setLayout(new BorderLayout());
	                    copyBtn = new CopyButton(this, frame.getString(copyTitleUID));
	                    add(copyBtn, BorderLayout.WEST);
	                }
	*/
	            }
	        }
	        adapter = new FloatFieldAdapter(frame, this, isEditor);
	
	        //���������� �������
	        String copyRefPath = getCopyRefPath();
	        if (copyRefPath != null && !"".equals(copyRefPath)) {
	            try {
	                OrRef copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(),
	                        OrRef.TR_CLEAR, frame);
	                adapter.setCopyRef(copyRef);
	                //CopyAdapter adapter = new CopyAdapter();
	                //getCopyBtn().setCopyAdapter(adapter);
	            } catch (Exception e) {
	                if (e instanceof RuntimeException) {
	                    adapter.showErrorNessage(e.getMessage());
	                }
                    getLog().error(e, e);
	            }
	        }
	        setHorizontalAlignment(SwingConstants.RIGHT);
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
        //Utils.processBorderProperties(this, frame);
/*
        pn = getProperties().getChild("border").getChild("borderType");
        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setBorder((Border)pn.getDefaultValue());
        }
*/
        pv = getPropertyValue(pn.getChild("bitSeparation"));
        if (!pv.isNull()) {
            bitSeparation = pv.booleanValue();
        }
        
        pn = getProperties().getChild("pov");
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(pn.getChild("activity").getChild("editable"));
            if (!pv.isNull()) {
                setEnabled(!pv.booleanValue());
            } else {
                setEnabled(true);
            }
        }
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }
    	updateProperties(PROPS);
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

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return -1;
    }

    private class FloatFormatter extends JFormattedTextField.AbstractFormatter {

        private ThreadLocalNumberFormat fmt;

        public FloatFormatter(ThreadLocalNumberFormat fmt) {
            this.fmt = fmt;
        }

        public Object stringToValue(String text) throws ParseException {
            Number res = null;
            if (text != null && !text.isEmpty()) { 
            	text = text.replaceAll("\\p{Z}", "");
                res = fmt.parse(text);
                if (!(res instanceof Double)) {
                    res = new Double(res.doubleValue());
                }
            }
            return res;
        }

        public String valueToString(Object value) throws ParseException {
                return value instanceof Number?fmt.get().format(value):"";
        }
    }

    public String getCopyRefPath() {
        return copyRefPath;
    }

    public CopyButton getCopyBtn() {
        return copyBtn;
    }

    public FloatFieldAdapter getAdapter() {
        return adapter;
    }

    public boolean isBitsSeparated() {
    	return bitSeparation;
    }

    /**
     * Форматирует значение типа <code>Double</code> в приятный для пользователя вид
     * @return строка в формате  <i>x xxx xxx,yy</i>
     */
    public String getFormatValue(Double value) {
        return value == null ? "" : String.format("%,." + precision + "f", value);
    }

    //IntCell Editor
    public void setValue(String value) {
        try {
            Object val = transform(value);
            setValueDirectly(val);
            Number newVal = adapter.update((Number)val);
            if ((newVal == null && val != null) ||
                (newVal != null && !newVal.equals(val))) {
                setValueDirectly(newVal);
            }
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() + "| interface id=" + ((WebFrame)frame).getObj().id + "| ref=" + adapter.getRef() + "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }

    public JsonObject getJsonEditor() {
        return new JsonObject().add("type", "floatfield").add("options", new JsonObject().add("precision", precision).add("decimalSeparator", ","));
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