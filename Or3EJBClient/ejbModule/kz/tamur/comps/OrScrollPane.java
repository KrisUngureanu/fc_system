package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.ScrollPanePropertyRoot;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

public class OrScrollPane extends JScrollPane implements OrGuiContainer {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new ScrollPanePropertyRoot();

    private int mode;
    private Element xml;
    private boolean isSelected;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    private EventListenerList listeners = new EventListenerList();
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;

    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();

    private String varName;
	private int scrollPolicy;

    OrScrollPane(Element xml, int mode, Factory cf, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        PropertyNode prop = PROPS.getChild("viewComp");
        PropertyValue pv = getPropertyValue(prop);
        java.util.List children = Collections.EMPTY_LIST;
        if (!pv.isNull()) {
            children = pv.elementValue().getChildren();
        }
        if (children.size() > 0) {
            Element e = (Element)children.get(0);
            OrGuiComponent view = cf.create(e, mode, frame);
            view.setGuiParent(this);
            setViewComponent(view);
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        prop = PROPS.getChild("title");
        pv = getPropertyValue(prop);
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String)p.first;
            title = frame.getString(titleUID);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        setBackground(kz.tamur.rt.Utils.getLightSysColor());
        /*
         * добавление слушателя, который будет перерисовывать родителя компонента если компонент прозрачен
         * необходимо для удаления артефактов прорисовки при изменении размеров прозрачных компонентов
         */

        addComponentListener(new ComponentListener() {

            public void componentShown(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                if (isOpaque() && getTopLevelAncestor() != null) {
                    getTopLevelAncestor().repaint();
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
        updateProperties();
    }

    public boolean canAddComponent(int x, int y) {
        return (getViewport().getView() == null);
    }

    public void addComponent(OrGuiComponent c, int x, int y) {
     // Выделить слушателей на удаление
        java.util.List<OrGuiComponent> copyList = new ArrayList<OrGuiComponent>(c.getListListeners());
        // Добавить слушателей родителя в добавляемый компонент
        c.setListListeners(listListeners, copyList);
        setViewComponent(c);
        PropertyNode view = PROPS.getChild("viewComp");
        setPropertyValue(new PropertyValue(c.getXml(), view));
    }

    public void setViewComponent(OrGuiComponent c) {
        setViewportView((Component)c);
    }

    public void removeComponent(OrGuiComponent c) {
        remove((Component) c);
        setViewportView(null);
        PropertyNode view = PROPS.getChild("viewComp");
        if (!c.isCopy()) {
            PropertyHelper.removeProperty(new PropertyValue(c.getXml(), view), xml);
        }
        revalidate();
    }

    public void moveComponent(OrGuiComponent c, int x, int y) {
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public GridBagConstraints getConstraints() {
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml);
        }
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        updateProperties();
        final String name = value.getProperty().getName();
        PropertyNode pn;
        PropertyValue pv;
        if ("title".equals(name)) {
            firePropertyModified();
        } else if ("transparent".equals(name)) {
            repaint();
        }
    }

    public Element getXml() {
        return xml;
    }

    public void updateConstraints(OrGuiComponent c) {
    }

    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    public void setLangId(long langId) {
        Component c = getViewport().getView();
        if (c instanceof OrGuiComponent) {
            ((OrGuiComponent)c).setLangId(langId);
        }
        title = frame.getString(titleUID);
    }

    public int getMode() {
        return mode;
    }

    private void updateProperties() {
        //Utils.processBorder(this, frame, borderProps);
        //Utils.processBorderProperties(this, frame, borderProps);
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("scrollPolicy"));
        if (!pv.isNull()) {
            switch(pv.intValue()) {
                case Constants.SCROLL_BOTH:
                    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                    scrollPolicy = Constants.SCROLL_BOTH;
                    break;
                case Constants.SCROLL_HORIZONTAL:
                    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                    scrollPolicy = Constants.SCROLL_HORIZONTAL;
                    break;
                case Constants.SCROLL_VERTICAL:
                    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPolicy = Constants.SCROLL_VERTICAL;
                    break;
                case Constants.SCROLL_AS_NEEDED:
                    setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scrollPolicy = Constants.SCROLL_AS_NEEDED;
                    break;
            }
        } else {
            setPropertyValue(new PropertyValue(Constants.SCROLL_BOTH,
                    pn.getChild("scrollPolicy")));
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
        
        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("transparent"));
        if (!pv.isNull()) {
            // прозрачность компонента(да/нет)
            getViewport().setOpaque(!pv.booleanValue());
            setOpaque(!pv.booleanValue());
        }
        repaint();
    }

    public int getScrollPolicy() {
		return scrollPolicy;
	}

	public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener)list[i]).propertyModified(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        if (mode == Mode.RUNTIME) {
            return prefSize;
        } else {
            return PropertyHelper.getPreferredSize(this);
        }
    }

    public Dimension getMaxSize() {
        if (mode == Mode.RUNTIME) {
            return maxSize;
        } else {
            return PropertyHelper.getMaximumSize(this);
        }
    }

    public Dimension getMinSize() {
        if (mode == Mode.RUNTIME) {
            return minSize;
        } else {
            return PropertyHelper.getMinimumSize(this);
        }
    }

    //
    public int getTabIndex() {
        return -1;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component[] comps = getViewport().getComponents();
        if (comps.length > 0) {
            for (int i = 0; i < comps.length; i++) {
                Component comp = comps[i];
                if (comp instanceof OrGuiComponent &&
                        !(comp instanceof OrLabel)) {
                    comp.setEnabled(enabled);
                }
            }
        }
    }

    public int getOrComponentCount() {
        return getViewport().getComponentCount();
    }

    public Component getOrComponent(int n) {
        return getViewport().getComponent(n);
    }

    public Component[] getOrComponents() {
        return getViewport().getComponents();
    }

    public byte[] getDescription() {
        return null; 
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}
	
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
    	int count = getViewport().getComponentCount();
        
    	for (int i=0; i<count; i++) {
    		Component c = getViewport().getComponent(i);
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
    
    public String getVarName() {
        return varName;
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setListListeners(listListeners, listForDel);
            }
        }
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
}
