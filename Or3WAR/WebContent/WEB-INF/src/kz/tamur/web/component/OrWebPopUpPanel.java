package kz.tamur.web.component;

import static kz.tamur.comps.Mode.RUNTIME;
import kz.tamur.comps.models.PopUpPanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.util.Pair;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.PopUpPanelAdapter;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrButtonComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonObject;

public class OrWebPopUpPanel extends WebButton implements JSONComponent, OrButtonComponent {

    public static PropertyNode PROPS = new PopUpPanelPropertyRoot();

    private String expression;
    private OrGuiContainer parent;
    private int tabIndex;
    private boolean isHelpClick = false;
    private Map borderProps = new TreeMap();
    private String title;
    private String titleUID;
    private PopUpPanelAdapter adapter;
    private boolean hasSetAttr = false;
    private boolean isRuntime;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private OrWebPanel mainPane;
    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate;
    
    OrWebPopUpPanel(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
        super("OrButton", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        isRuntime = mode == Mode.RUNTIME;
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        setPadding(new Dimension(14, 2));
        Element panel;

        PropertyValue pv = getPropertyValue(PROPS.getChild("panel"));
        if (!pv.isNull()) {
            List children = pv.elementValue().getChildren();
            panel = (Element) children.get(0);

        } else {
            panel = new Element("Component");
            panel.setAttribute("class", "Panel");
        }
        updateProperties();
        mainPane = (OrWebPanel) fm.create(panel, mode, frame);
        mainPane.setVisible(true);
        if (pv.isNull()) {
            PropertyHelper.addProperty(new PropertyValue(mainPane.getXml(), PROPS.getChild("panel")), xml);
        }
        if (isRuntime) {
            adapter = new PopUpPanelAdapter(frame, this, false);
        }
        
        PropertyNode pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("beforeOpen"));
        if (pv != null) {
            String expr = null;
            if (!pv.isNull()) {
                expr = pv.stringValue();
            }
            if (expr != null && expr.length() > 0) {
                beforeOpenTemplate = OrLang.createStaticTemplate(expr, getLog());
                try {
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                    }
                } catch (Exception ex) {
                	log.error(ex, ex);
                }
            }
        }

        pv = getPropertyValue(pn.getChild("afterOpen"));
        if (pv != null) {
            String expr = null;
            if (!pv.isNull()) {
                expr = pv.stringValue();
            }
            if (expr != null && expr.length() > 0) {
                afterOpenTemplate = OrLang.createStaticTemplate(expr, getLog());
                try {
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                    }
                } catch (Exception ex) {
                	log.error(ex, ex);
                }
            }
        }

        pv = getPropertyValue(pn.getChild("beforeClose"));
        if (pv != null) {
            String expr = null;
            if (!pv.isNull()) {
                expr = pv.stringValue();
            }
            if (expr != null && expr.length() > 0) {
                beforeCloseTemplate = OrLang.createStaticTemplate(expr, getLog());
                try {
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                    }
                } catch (Exception ex) {
                	log.error(ex, ex);
                }
            }
        }

        pv = getPropertyValue(pn.getChild("afterClose"));
        if (pv != null) {
            String expr = null;
            if (!pv.isNull()) {
                expr = pv.stringValue();
            }
            if (expr != null && expr.length() > 0) {
                afterCloseTemplate = OrLang.createStaticTemplate(expr, getLog());
                try {
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                    }
                } catch (Exception ex) {
                	log.error(ex, ex);
                }
            }
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
            mainPane.setLangId(langId);
            title = frame.getString(titleUID);
            setText(title);
            updateDescription();
        }
    }

    private void updateProperties() {
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        updateProperties(PROPS);

        PropertyNode pn = getProperties().getChild("view");
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
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        } else {
            setEnabled(true);
        }

        hasSetAttr = false;

        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
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

    public PopUpPanelAdapter getAdapter() {
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
        property.add("text", getText());
        property.add("panel", mainPane.getJSON());
        property.add("e", toInt(isEnabled()));
        property.add("isNeedPass", adapter.isNeedPass());
   
        if (getIconPath() != null) {
            JsonObject img = new JsonObject();
            img.add("src", getIconPath());
            property.add("img", img);
        }
        obj.add("pr", property);
        sendChange(obj, isSend);
        return obj;
    }
    
    public void showPopUp() {
        sendChange2("show", 1);
    }
    
    /**
     * Обработка собитий до/после открытия/закрытия
     *  
     * @param isVisible <code>true</code> - показать
     * @param isBefore <code>true</code> до события показа или скрытия
     */
    public void setVisible(boolean isVisible, boolean isBefore) {
        if (isVisible) {
            if (isBefore) {
                evaluate(beforeOpenTemplate, 0);
            } else {
                evaluate(afterOpenTemplate, 1);
            }
        } else {
            if (isBefore) {
                evaluate(beforeCloseTemplate, 2);
            } else {
                evaluate(afterCloseTemplate, 3);
            }
        }
    }
    
    private void evaluate(ASTStart template, int type) {
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang((WebFrame) frame);
            Map vc = new HashMap();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, ((WebFrame) frame).getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                switch (type) {
                case 0:
                    Util.showErrorMessage(this, ex.getMessage(), "Действие перед открытием");
                	log.error("Ошибка при выполнении формулы 'Действие перед открытием' компонента '" + getClass().getName() + "', uuid: " + getUUID());
                case 1:
                    Util.showErrorMessage(this, ex.getMessage(), "Действие после открытия");
                	log.error("Ошибка при выполнении формулы 'Действие после открытия' компонента '" + getClass().getName() + "', uuid: " + getUUID());
                case 2:
                    Util.showErrorMessage(this, ex.getMessage(), "Действие перед закрытием");
                	log.error("Ошибка при выполнении формулы 'Действие перед закрытием' компонента '" + getClass().getName() + "', uuid: " + getUUID());
                case 3:
                    Util.showErrorMessage(this, ex.getMessage(), "Действие после закрытия");
                	log.error("Ошибка при выполнении формулы 'Действие после закрытия' компонента '" + getClass().getName() + "', uuid: " + getUUID());
                }
                log.error(ex, ex);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
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
