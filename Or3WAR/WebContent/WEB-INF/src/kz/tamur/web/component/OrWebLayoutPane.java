package kz.tamur.web.component;

import kz.tamur.comps.*;
import kz.tamur.comps.models.LayoutPanePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.web.common.webgui.WebLayoutPane;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.rt.adapters.LayoutPaneAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import javax.swing.event.EventListenerList;

import java.util.*;
import java.awt.BorderLayout;
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
public class OrWebLayoutPane extends WebLayoutPane implements OrGuiContainer {
	
    private static PropertyNode PROPS = new LayoutPanePropertyRoot();

    private boolean isShown = false;

    private OrGuiContainer guiParent;
    private boolean isCopy;

    private EventListenerList listeners = new EventListenerList();

    private String title;
    private String titleUID;
    private String varName;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private LayoutPaneAdapter adapter;
	    
    public OrWebLayoutPane(Element xml, int mode, WebFactory cf, OrFrame frame, String id) throws KrnException {
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
            adapter = new LayoutPaneAdapter(frame, this, false);
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
        List children = Collections.EMPTY_LIST;

        PropertyValue pv = getPropertyValue(PROPS.getChild("first"));
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            WebComponent c = cf.create(e, mode, frame);
            c.setParent(this);
            add(c, BorderLayout.PAGE_START);
        }
        pv = getPropertyValue(PROPS.getChild("after"));
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            WebComponent c = cf.create(e, mode, frame);
            c.setParent(this);
            add(c, BorderLayout.LINE_END);
        }
        pv = getPropertyValue(PROPS.getChild("last"));
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            WebComponent c = cf.create(e, mode, frame);
            c.setParent(this);
            add(c, BorderLayout.PAGE_END);
        }
        pv = getPropertyValue(PROPS.getChild("before"));
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            WebComponent c = cf.create(e, mode, frame);
            c.setParent(this);
            add(c, BorderLayout.LINE_START);
        }
        pv = getPropertyValue(PROPS.getChild("center"));
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            WebComponent c = cf.create(e, mode, frame);
            c.setParent(this);
            add(c, BorderLayout.CENTER);
        }
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
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
        add(wc, (String) cs);
    }

    public Object removeComponent(OrGuiComponent c) {
    	for (String key : comps.keySet()) {
    		WebComponent ch = comps.get(key);
    		if (ch.equals(c)) {
    			comps.remove(key);
    			return key;
    		}
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
        for (WebComponent comp : comps.values()) {
            ((OrGuiComponent)comp).setLangId(langId);
        }
        if (parent == null) {
            calculateSize();
            try {
            	putJSON();
            } catch (Exception e) {
            	log.error(e, e);
            }
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

    public LayoutPaneAdapter getAdapter() {
        return adapter;
    }
    
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;

        for (WebComponent c : comps.values()) {
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
    
	public void removeChangeProperties() {
		super.removeChangeProperties();
        for (WebComponent c : comps.values()) {
        	c.removeChangeProperties();
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
