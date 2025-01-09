package kz.tamur.web.component;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SplitPanePropertyRoot;
import kz.tamur.web.common.webgui.WebSplitPane;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.util.Pair;
import kz.tamur.rt.adapters.SplitPaneAdapter;
import kz.tamur.or3.client.comps.interfaces.OrSplitPaneComponent;
import org.jdom.Element;

import javax.swing.event.EventListenerList;

import java.util.*;
import java.awt.GridBagConstraints;
import java.awt.Dimension;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 19.07.2006
 * Time: 18:43:18
 * To change this template use File | Settings | File Templates.
 */
public class OrWebSplitPane extends WebSplitPane implements OrSplitPaneComponent {
	
    private static PropertyNode PROPS = new SplitPanePropertyRoot();
    private static PropertyNode LEFT = PROPS.getChild("left");
    private static PropertyNode RIGHT = PROPS.getChild("right");

    private boolean isShown = false;

    private OrGuiContainer guiParent;
    private boolean isCopy;

    private EventListenerList listeners = new EventListenerList();

    private String title;
    private String titleUID;
    private SplitPaneAdapter adapter;
    private String varName;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
	    
    public OrWebSplitPane(Element xml, int mode, WebFactory cf, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);

        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
            configNumber = ((WebFrame)frame).getSession().getConfigNumber();
            loadChildren(cf);

            setConstraints(PropertyHelper.getConstraints(PROPS, xml, id, frame));
            setPreferredSize(PropertyHelper.getPreferredSize(this, id, frame));
            setMaximumSize(PropertyHelper.getMaximumSize(this, id, frame));
            setMinimumSize(PropertyHelper.getMinimumSize(this, id, frame));

            PropertyNode prop = PROPS.getChild("title");
            PropertyValue pv = getPropertyValue(prop);
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                titleUID = (String)p.first;
                title = frame.getString(titleUID);
            }

            updateProperties();
            adapter = new SplitPaneAdapter(frame, this, false);
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

    private void loadChildren(WebFactory cf) throws KrnException {
        PropertyValue pv = getPropertyValue(LEFT);
        java.util.List children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            setLeftComponent(cf.create(e, mode, frame));
        } else {
            setLeftComponent(null);
        }
        pv = getPropertyValue(RIGHT);
        children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            setRightComponent(cf.create(e, mode, frame));
        } else {
            setRightComponent(null);
        }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyNode orient = pn.getChild("orientation");
        PropertyValue pv = getPropertyValue(orient);
        if (!pv.isNull()) {
            setOrientation(pv.intValue());
        } else {
            setOrientation(Constants.HORIZONTAL);
        }
        PropertyNode dividerLocation = pn.getChild("dividerLocation");
        pv = getPropertyValue(dividerLocation);
        if (!pv.isNull()) {
            setDividerLocation(pv.doubleValue());
        } else {
            setDividerLocation((Double) dividerLocation.getDefaultValue());
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
    }


    // implementing OrPanelComponent
    public boolean canAddComponent(int x, int y) {
        return false;
    }

    public void addComponent(OrGuiComponent c, Object cs) {
    	WebComponent wc = (WebComponent)c;
        wc.setParent(this);
        if ("left".equals(cs)) {
			leftComp = wc;
		}
		else if ("right".equals(cs)) {
			rightComp = wc;
		}
    }

    public Object removeComponent(OrGuiComponent c) {
		if (leftComp.equals(c)) {
			leftComp = null;
			return "left";
		}
		else if (rightComp.equals(c)) {
			rightComp = null;
			return "right";
		}
    	return null;
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public void addPropertyListener(PropertyListener l) {
    }

    public void removePropertyListener(PropertyListener l) {
    }

    public void firePropertyModified() {
    }

    public String getTitle() {
        return title;
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

    public int getComponentStatus() {
        return 0;
    }

    public void setLangId(long langId) {
        title = frame.getString(titleUID);
        OrGuiComponent comp = (OrGuiComponent) leftComp;
        if (comp != null) {
            comp.setLangId(langId);
        }
        comp = (OrGuiComponent) rightComp;
        if (comp != null) {
            comp.setLangId(langId);
        }
    }

    public OrGuiContainer getGuiParent() {
        return null;
    }

    public void setGuiParent(OrGuiContainer parent) {
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
    
    public void setEnabled(boolean isEnabled) {
    }

    public boolean isEnabled() {
        return false;
    }

    public byte[] getDescription() {
        return new byte[0];
    }

    public SplitPaneAdapter getAdapter() {
        return adapter;
    }
    
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
        
        WebComponent c = leftComp;
		if (c instanceof OrGuiContainer) {
			OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
			if (cc != null) return cc; 
		} else if (c instanceof OrGuiComponent) {
			OrGuiComponent gc = (OrGuiComponent)c;
			if (title.equals(gc.getVarName())) return gc;
		}
		
        c = rightComp;
		if (c instanceof OrGuiContainer) {
			OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
			if (cc != null) return cc; 
		} else if (c instanceof OrGuiComponent) {
			OrGuiComponent gc = (OrGuiComponent)c;
			if (title.equals(gc.getVarName())) return gc;
		}
    	return null;
    }
    
	public void removeChangeProperties() {
		super.removeChangeProperties();
        if (leftComp != null) {
        	leftComp.removeChangeProperties();
        }
        if (rightComp != null) {
        	rightComp.removeChangeProperties();
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
