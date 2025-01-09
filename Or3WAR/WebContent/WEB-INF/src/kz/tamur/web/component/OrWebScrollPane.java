package kz.tamur.web.component;

import kz.tamur.web.common.webgui.WebScrollPane;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.ScrollPanePropertyRoot;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.util.Pair;
import kz.tamur.rt.adapters.ScrollPaneAdapter;
import kz.tamur.or3.client.comps.interfaces.OrScrollPaneComponent;

import java.awt.*;
import java.util.Collections;

import org.jdom.Element;

import javax.swing.event.EventListenerList;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 19.07.2006
 * Time: 18:33:30
 * To change this template use File | Settings | File Templates.
 */
public class OrWebScrollPane extends WebScrollPane implements OrScrollPaneComponent {
    public static PropertyNode PROPS = new ScrollPanePropertyRoot();

    private OrGuiContainer guiParent;
    private boolean isCopy;

    private EventListenerList listeners = new EventListenerList();

    private String title;
    private String titleUID;
    private ScrollPaneAdapter adapter;
    private String varName;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
	    
    OrWebScrollPane(Element xml, int mode, WebFactory cf, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        PropertyNode prop = PROPS.getChild("viewComp");
	        PropertyValue pv = getPropertyValue(prop);
	        java.util.List children = Collections.EMPTY_LIST;
	        if (!pv.isNull()) {
	            children = pv.elementValue().getChildren();
	        }
	        if (children.size() > 0) {
	            Element e = (Element)children.get(0);
	            WebComponent view = cf.create(e, mode, frame);
	            setViewComponent(view);
	        }
	        setConstraints(PropertyHelper.getConstraints(PROPS, xml, id, frame));
	        setPreferredSize(PropertyHelper.getPreferredSize(this, id, frame));
	        setMaximumSize(PropertyHelper.getMaximumSize(this, id, frame));
	        setMinimumSize(minSize = PropertyHelper.getMinimumSize(this, id, frame));
	        prop = PROPS.getChild("title");
	        pv = getPropertyValue(prop);
	        if (!pv.isNull()) {
	            Pair p = pv.resourceStringValue();
	            titleUID = (String)p.first;
	            title = frame.getString(titleUID);
	        }
	        pv = getPropertyValue(PROPS.getChild("varName"));
	        if (!pv.isNull()) {
	            varName = pv.stringValue(frame.getKernel());
	        }
	        updateProperties();
	        adapter = new ScrollPaneAdapter(frame, this, false);
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

    private void updateProperties() {
    }

    // implementing OrPanelComponent
    public boolean canAddComponent(int x, int y) {
        return false;
    }

    public void addComponent(OrGuiComponent c, Object cs) {
    	WebComponent wc = (WebComponent)c;
        wc.setParent(this);
        viewComp = wc;
    }

    public Object removeComponent(OrGuiComponent c) {
		if (viewComp.equals(c)) {
			viewComp = null;
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
        OrGuiComponent comp = (OrGuiComponent) viewComp;
        if (viewComp != null) {
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

    public ScrollPaneAdapter getAdapter() {
        return adapter;
    }
    
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
		if (viewComp instanceof OrGuiContainer) {
			OrGuiContainer v = (OrGuiContainer)viewComp;
			return v.getComponent(title);
		}
    	return null;
    }
    
	public void removeChangeProperties() {
		super.removeChangeProperties();
        if (viewComp != null) {
            viewComp.removeChangeProperties();
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
