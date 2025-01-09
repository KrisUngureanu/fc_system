package kz.tamur.comps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.models.AnalyticPanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.rt.adapters.AnalyticPanelAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;

public class OrAnalyticPanel extends JPanel implements OrGuiContainer {
	
	private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
	private String title;
	private int mode;
	private GridBagConstraints constraints;
	public static PropertyNode PROPS = new AnalyticPanelPropertyRoot();
	private Element xml;
	public OrFrame frame;
	private boolean isCopy;
	private OrGuiContainer guiParent;
	private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    protected String UUID;
    private byte[] description;
    private AnalyticPanelAdapter adapter;
    private String varName;
    private EventListenerList listeners = new EventListenerList();
    
    OrAnalyticPanel(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
    }

	@Override
	public GridBagConstraints getConstraints() {
		return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
	}

	@Override
	public void setSelected(boolean isSelected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PropertyNode getProperties() {
		return PROPS;
	}

	@Override
	public PropertyValue getPropertyValue(PropertyNode prop) {
		return PropertyHelper.getPropertyValue(prop, xml, frame);
	}

	@Override
	public void setPropertyValue(PropertyValue value) {
		PropertyHelper.setPropertyValue(value, xml, frame);
        final String name = value.getProperty().getName();
        PropertyNode pn;
        PropertyValue pv;
        if ("title".equals(name)) {
            pv = getPropertyValue(getProperties().getChild("title"));
            setTitle(pv.toString());
        }		
	}

	@Override
	public Element getXml() {
		return xml;
	}

	@Override
	public int getComponentStatus() {
		return Constants.CONTAINER_COMP;
	}

	@Override
	public void setLangId(long langId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMode() {
		return mode;
	}

	@Override
	public boolean isCopy() {
		return isCopy;
	}

	@Override
	public void setCopy(boolean copy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OrGuiContainer getGuiParent() {
		return guiParent;
	}

	@Override
	public void setGuiParent(OrGuiContainer parent) {
		this.guiParent = guiParent;		
	}

	@Override
	public void setXml(Element xml) {
		this.xml = xml;
		
	}

	@Override
	public Dimension getPrefSize() {
		return (mode == Mode.RUNTIME) ? prefSize : PropertyHelper.getPreferredSize(this);
	}

	@Override
	public Dimension getMaxSize() {
		return (mode == Mode.RUNTIME) ? maxSize : PropertyHelper.getMaximumSize(this);
	}

	@Override
	public Dimension getMinSize() {
		return (mode == Mode.RUNTIME) ? minSize : PropertyHelper.getMinimumSize(this);
	}

	@Override
	public String getUUID() {
		return UUID;
	}

	@Override
	public byte[] getDescription() {
		return description != null ? Arrays.copyOf(description, description.length) : null;
	}

	@Override
	public ComponentAdapter getAdapter() {
		return adapter;
	}

	@Override
	public String getVarName() {
		return varName;
	}

	@Override
	public void setComponentChange(OrGuiComponent comp) {
		listListeners.add(comp);		
	}

	@Override
	public void setListListeners(List<OrGuiComponent> listForAdd, List<OrGuiComponent> listForDel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OrGuiComponent> getListListeners() {
		return listListeners;
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public void updateDynProp() {
		
	}

	@Override
	public int getPositionOnTopPan() {
		return -1;
	}

	@Override
	public boolean isShowOnTopPan() {
		return false;
	}

	@Override
	public void setAttention(boolean attention) {
		
	}

	@Override
	public boolean canAddComponent(int x, int y) {
		return true;
	}

	@Override
	public void addComponent(OrGuiComponent c, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeComponent(OrGuiComponent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveComponent(OrGuiComponent c, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateConstraints(OrGuiComponent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPropertyListener(PropertyListener l) {
		listeners.add(PropertyListener.class, l);		
	}

	@Override
	public void removePropertyListener(PropertyListener l) {
		listeners.remove(PropertyListener.class, l);		
	}

	@Override
	public void firePropertyModified() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public OrGuiComponent getComponent(String title) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setTitle(String title) {
        this.title = title;
    }
	
}
