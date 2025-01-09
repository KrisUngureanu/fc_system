package kz.tamur.web.component;

import static kz.tamur.comps.Utils.getExpReturn;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.ButtonPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.util.Pair;
import kz.tamur.util.Funcs;
import kz.tamur.rt.adapters.ButtonAdapter;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.controller.WebController;
import kz.tamur.or3.client.comps.interfaces.OrButtonComponent;

import java.awt.*;
import java.util.TreeMap;
import java.util.Map;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebButton extends WebButton implements JSONComponent, OrButtonComponent {

    public static PropertyNode PROPS = new ButtonPropertyRoot();

    private String expression;
    private OrGuiContainer parent;
    private int tabIndex;
    private boolean isHelpClick = false;
    private Map borderProps = new TreeMap();
    private String title;
    private String titleUID;
    private String titleExpr = null;
    private String titleExprText = null;
    private ButtonAdapter adapter;
    private boolean hasSetAttr = false;
    private boolean isRuntime;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
	private int autoRefreshMillis = 0;
	private String attentionExpr;

    OrWebButton(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super("OrButton", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        isRuntime = mode == Mode.RUNTIME;
        try {
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        setPadding(new Dimension(14, 2));
	        updateProperties();
	        if (isRuntime) {
	            adapter = new ButtonAdapter(frame, this, false);
	        }
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
        return isRuntime ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public void setLangId(long langId) {
        if (isRuntime) {
            title = frame.getString(titleUID);
            setText(title != null ? title : "");
            updateDescription();
        }
        // Utils.processBorderProperties(this, frame);
    }

    private void updateProperties() {
        // Utils.processBorderProperties(this, frame);
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            setText(title != null ? title : "");
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        updateProperties(PROPS);
        
        PropertyNode pn = PROPS.getChild("titleN").getChild("expr");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            titleExpr = pv.stringValue();
        }
        
        pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }

        com.cifs.or2.client.Utils.createFileImg(getPropertyValue(pn.getChild("image")),"ico");

        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setOpaque(pv.booleanValue());
        } else {
            setOpaque(true);
        }
        pn = getProperties().getChild("pov");

        pv = getPropertyValue(pn.getChild("autoRefresh"));
        if (!pv.isNull()) {
            autoRefreshMillis  = pv.intValue();
        }
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        } else {
            setEnabled(true);
        }
        
        pv = getPropertyValue(pn.getChild("activity").getChild("attention"));
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }

        pv = getPropertyValue(pn.getChild("formula"));
        hasSetAttr = false;
        if (!pv.isNull()) {
            expression = pv.stringValue(frame.getKernel());
            hasSetAttr = expression.contains("setAttr(");
        }

        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
    }

    public String getaAttentionExpr() {
    	return attentionExpr;
    }
    
    void updateTitle() {
        if (titleExpr != null && !titleExpr.isEmpty()) {
            String titleExprText_ = getExpReturn(titleExpr, frame, getAdapter());
            if (titleExprText_ != null) {
                if (titleExprText_.isEmpty()) {
                	titleExprText_ = null;    
                }
                setText(titleExprText_);
                titleExprText = titleExprText_;
            }
        }
    }
    
    public String getExpression() {
        return expression;
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public Dimension getPrefSize() {
        return isRuntime ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return isRuntime ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return isRuntime ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public ButtonAdapter getAdapter() {
        return adapter;
    }

    public void setValue(String value) {
        buttonPressed();
    }

    public void buttonPressed() {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            adapter.buttonPressed();
        }
    }

    @Override
    public boolean hasSetAttr() {
        return hasSetAttr;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("e", toInt(isEnabled()));
        property.add("isNeedPass", adapter.isNeedPass());
        obj.add("pr", property);

        if (autoRefreshMillis > 0)
        	obj.add("autoRefresh", autoRefreshMillis);
        
        sendChange(obj, isSend);
        return obj;
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