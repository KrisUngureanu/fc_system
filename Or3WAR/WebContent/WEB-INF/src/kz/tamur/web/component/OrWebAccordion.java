package kz.tamur.web.component;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.AccordionPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.rt.adapters.AccordionAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPanel;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * The Class OrWebAccordion.
 * 
 * @author Lebedev Sergey
 */
public class OrWebAccordion extends WebPanel implements OrGuiContainer {

    private PropertyNode props = new AccordionPropertyRoot();
    private PropertyNode propTitle = props.getChild("titleN");

    /** Номер конфигурации, для нескольких БД. */
    private AccordionAdapter adapter;
    private OrGuiContainer parent;
    private WebFactory fm;
    List<OrWebPanel> panels = new ArrayList<OrWebPanel>();

    /**
     * Конструктор класса OrWebAccordion.
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
    public OrWebAccordion(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        this.fm = fm;
        uuid = PropertyHelper.getUUID(this, frame);
        init();
        this.xml = null;
    }

    private void init() throws KrnException {
        adapter = new AccordionAdapter(frame, this, false);
        // всплывающая подсказка
        PropertyValue pv;
        updateProperties(props);
        updateProperties();

        List<Element> ePanels;
        pv = getPropertyValue(props.getChild("panels"));
        ePanels = pv.elementValue().getChildren();
        for (Element element : ePanels) {
            panels.add((OrWebPanel) fm.create(element, mode, frame));
        }
    }

    private void updateProperties() {
        constraints = PropertyHelper.getConstraints(props, xml, id, frame);
        PropertyValue pv = null;
        pv = getPropertyValue(props.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }

        pv = getPropertyValue(props.getChild("description"));
        if (!pv.isNull()) {
            Pair<String, Object> p = pv.resourceStringValue();
            descriptionUID = p.first;
            description = (byte[]) p.second;
        }

        PropertyNode pn = props.getChild("pov");
        pv = getPropertyValue(pn.getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }

        pn = propTitle.getChild("countPanel");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            int count = pv.intValue();
            String name;
            for (int i = 0; i < count; i++) {
                name = "icon_" + i;
                new PropertyNode(propTitle, name, Types.IMAGE, null, false, null);
                com.cifs.or2.client.Utils.createFileImg(getPropertyValue(propTitle.getChild(name)), "ico");
            }
        }
    }

    @Override
    public PropertyNode getProperties() {
        return props;
    }

    @Override
    public int getComponentStatus() {
        return 0;
    }

    @Override
    public void setLangId(long langId) {
    	updateDescription();
        for (OrWebPanel panel : panels) {
            panel.setLangId(langId);
        }
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

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
    
    @Override
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
    	int count = panels.size();
        
    	for (int i=0; i<count; i++) {
    		WebComponent c = panels.get(i);
    		if (c instanceof OrGuiContainer) {
				OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
				if (cc != null) return cc; 
    		} else if (c instanceof OrGuiComponent) {
    			OrGuiComponent gc = (OrGuiComponent)c;
    			if (title.equals(gc.getVarName())) return gc;
    		}
    	}
    	return null;
    }
    
	@Override
	public JsonObject putJSON(boolean isSend) {
        for (OrWebPanel panel : panels) {
            panel.putJSON(true);
        }

		return super.putJSON(isSend);
	}

	@Override
	public boolean canAddComponent(int x, int y) {
		return false;
	}

	@Override
	public void addComponent(OrGuiComponent c, Object cs) {
	}

	@Override
	public Object removeComponent(OrGuiComponent c) {
		return null;
	}

	@Override
	public void moveComponent(OrGuiComponent c, int x, int y) {
	}

	@Override
	public void updateConstraints(OrGuiComponent c) {
	}

	@Override
	public void addPropertyListener(PropertyListener l) {
	}

	@Override
	public void removePropertyListener(PropertyListener l) {
	}

	@Override
	public void firePropertyModified() {
	}

	@Override
	public String getTitle() {
		return null;
	}
	
	public void setCardionPanelVisible(WebComponent panel, boolean isVisible) {
		for(int i = 0; i < panels.size(); i++) {
			if (panel.uuid.equals(panels.get(i).uuid)) {
				JsonArray accordionPanels = new JsonArray();
	            JsonObject accordionPanel = new JsonObject();
	            accordionPanel.add("spanElementId", "t" + i + uuid);
	            accordionPanel.add("divElementId", "cnt" + i + uuid);
	            accordionPanel.add("visible", isVisible ? 1 : 0);
	            accordionPanels.add(new JsonObject().add("accordionPanelVisible" + i, accordionPanel));
	            sendChangeProperty("accordionPanelsVisible", accordionPanels);
	            break;
			}
		}
	}
}
