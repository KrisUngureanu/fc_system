package kz.tamur.web.component;

import java.awt.Dimension;
import java.util.List;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.CollapsiblePanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.CollapsiblePanelAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPanel;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

/**
 * The Class OrWebCollapsiblePanel.
 * 
 * @author Lebedev Sergey
 */
public class OrWebCollapsiblePanel extends WebPanel implements OrGuiComponent {

    public static PropertyNode PROPS = new CollapsiblePanelPropertyRoot();

    /** Номер конфигурации, для нескольких БД. */
    private CollapsiblePanelAdapter adapter;
    private String titleUID;
    private String title;
    private WebComponent content;
    private OrGuiContainer parent;
    private int alignmentText;
    private int titlePanePostion;
    private boolean isExpanded = false;

    /**
     * Конструктор класса OrWebCollapsiblePanel.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param fm
     *            the fm
     * @param frame
     *            the frame
     * @param id
     *            the id
     * @throws KrnException
     *             the krn exception
     */
    public OrWebCollapsiblePanel(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        init(fm);
    }

    /**
     * Inits the.
     * 
     * @param fm
     *            the fm
     * @throws KrnException
     *             the krn exception
     */
    private void init(WebFactory fm) throws KrnException {
        adapter = new CollapsiblePanelAdapter(frame, this, false);
        
        PropertyNode pn;
        PropertyValue pv;
        Element panel;
        pv = getPropertyValue(PROPS.getChild("panel"));
        boolean init = pv.isNull();
        if (!init) {
            List<Element> children = pv.elementValue().getChildren();
            panel = children.get(0);
        } else {
            panel = new Element("Component");
            panel.setAttribute("class", "Panel");
        }
        pv = getPropertyValue(PROPS.getChild("pov").getChild("expandAll"));
        if (!pv.isNull()) {
        	isExpanded = pv.booleanValue();
        }

        updateProperties();
        setContent((WebComponent) fm.create(panel, mode, frame));
        setOpaque(false);
    }

    private void updateProperties() {
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        PropertyValue pv = null;
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        pv = getPropertyValue(PROPS.getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
        }

        updateProperties(PROPS);
        
        PropertyNode pn = PROPS.getChild("titleN");
        com.cifs.or2.client.Utils.createFileImg(getPropertyValue(pn.getChild("icon")),"ico");
    }

    public void setContent(WebComponent content) {
        this.content = content;
    }

    public WebComponent getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public void setLangId(long langId) {
    	updateDescription();
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return parent;
    }

    @Override
    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    @Override
    public Dimension getPrefSize() {
        return null;
    }

    @Override
    public Dimension getMaxSize() {
        return null;
    }

    @Override
    public Dimension getMinSize() {
        return null;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    /**
     * @return the alignmentText
     */
    public int getAlignmentText() {
        return alignmentText;
    }

    /**
     * @return the titlePanePostion
     */
    public int getTitlePanePostion() {
        return titlePanePostion;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
    
    public boolean isExpanded() {
    	return isExpanded;
    }
    
	@Override
	public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        if (isExpanded()) {
            property.add("expanded", true);
        }
        
        if (property.size() > 0) {
            obj.add("pr", property);
            sendChange(obj, isSend);
        }

        if (content != null && content.isVisible()) {
        	content.putJSON(isSend);
        }

		return super.putJSON(isSend);
	}

}