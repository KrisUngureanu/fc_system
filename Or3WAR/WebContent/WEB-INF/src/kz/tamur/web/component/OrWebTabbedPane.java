package kz.tamur.web.component;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TabbedPanePropertyRoot;
import kz.tamur.web.common.webgui.WebTabbedPane;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.util.Pair;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.TabbedPaneAdapter;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrTabbedPaneComponent;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;

import org.jdom.Element;

import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JTabbedPane;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 19.07.2006
 * Time: 19:13:54
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTabbedPane extends WebTabbedPane implements OrTabbedPaneComponent {
	
    public static PropertyNode PROPS = new TabbedPanePropertyRoot();

    private int prevIndex = -1;

    private OrGuiContainer guiParent;
    private boolean isCopy;
    private String title;
    private String titleUID;
    private TabbedPaneAdapter adapter;
    private String varName;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;

    public OrWebTabbedPane(Element xml, int mode, WebFactory cf, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        PropertyNode prop = PROPS.getChild("children");
	        PropertyValue pv = getPropertyValue(prop);
	        java.util.List<Element> children = Collections.EMPTY_LIST;
	        if (!pv.isNull()) {
	            children = pv.elementValue().getChildren();
	        }
	        if (children.size() > 0) {
	            for (Element child : children) {
	                WebComponent comp = cf.create(child, mode, frame);
	                String title = "Закладка";
	                String iconName = null;
	                if (mode == Mode.DESIGN) {
	                    pv = ((OrGuiComponent)comp).getPropertyValue(((OrGuiComponent)comp).getProperties().getChild("title"));
	                    if (!pv.isNull()) {
	                        title = frame.getString((String)pv.resourceStringValue().first);
	                    }
	                } else {
	                    if (comp instanceof OrPanelComponent) {
	                        title = ((OrPanelComponent)comp).getTitle();
	                        iconName = ((OrPanelComponent) comp).getIconName();
	                    }
	                }
	
	                addTab(title, iconName, (WebComponent)comp);
	            }
	        }
	        setConstraints(PropertyHelper.getConstraints(PROPS, xml, id, frame));
	        setPreferredSize(PropertyHelper.getPreferredSize(this, id, frame));
	        setMaximumSize(PropertyHelper.getMaximumSize(this, id, frame));
	        setMinimumSize(PropertyHelper.getMinimumSize(this, id, frame));
	        updateProperties();
	
	        prop = PROPS.getChild("title");
	        pv = getPropertyValue(prop);
	        if (!pv.isNull()) {
	            Pair p = pv.resourceStringValue();
	            titleUID = (String)p.first;
	            title = frame.getString(titleUID);
	        }
	        adapter = new TabbedPaneAdapter(frame, this, false);
	        setSelectedIndex(0);
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
    
    public String getVarName() {
        return varName;
    }

    private void updateProperties() {
        //Utils.processBorder(this, frame, borderProps);
        //Utils.processBorderProperties(this, frame, borderProps);
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("tabPolicy"));
        if (!pv.isNull()) {
            switch(pv.intValue()) {
                case Constants.TAB_WRAP_LINE:
//                    setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                    break;
                case Constants.TAB_SCROLL:
//                    setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                    break;
                default:
//                    setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
            }
        } else {
//            setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        }

        pv = getPropertyValue(pn.getChild("tabOrientation"));
        if (!pv.isNull() && pv.intValue() > 0) {
            setTabPlacement(pv.intValue());
        } else {
            setTabPlacement(JTabbedPane.TOP);
        }

        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
//            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
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
    }

    public Object removeComponent(OrGuiComponent c) {
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
        for (int i = 0; i < tabs.size(); i++) {
            OrGuiComponent comp = (OrGuiComponent) tabs.get(i);
            comp.setLangId(langId);
            if (comp instanceof OrGuiContainer) {
                setTitleAt(i, ((OrGuiContainer)comp).getTitle());
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

    public TabbedPaneAdapter getAdapter() {
        return adapter;
    }

    public void setTabVisible(OrGuiComponent comp, boolean visible) {
        setTabVisible((WebComponent)comp, visible);
    }
    
    public OrGuiComponent getComponent(String title) {
		if (title.equals(getVarName())) return this;
    	int count = tabs.size();
        
    	for (int i=0; i<count; i++) {
    		WebComponent c = tabs.get(i);
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
    	int count = tabs.size();
        
    	for (int i=0; i<count; i++) {
    		WebComponent c = tabs.get(i);
            c.removeChangeProperties();
        }
	}

    public void selectedIndex(int selectedIndex) {
        if (tabs.size() > selectedIndex) {
            WebComponent panel = tabs.get(selectedIndex);
            if (panel instanceof OrWebPanel) {
                ASTStart template = ((OrWebPanel) panel).getBeforeOpenTemplate();
                if (template != null) {
                    ClientOrLang orlang = new ClientOrLang(frame, true);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(template, vc, ((WebFrame) frame).getPanelAdapter(), new Stack<String>());
                    } catch (Exception ex) {
                        kz.tamur.rt.adapters.Util.showErrorMessage(((OrWebPanel) panel), ex.getMessage(),
                                "Действие перед открытием вкладки");
	                	log.error("Ошибка при выполнении формулы 'Действие перед открытием вкладки'" + panel.getClass().getName() + "', uuid: " + panel.getUUID());
                        log.error(ex, ex);
                    } finally {
                        if (calcOwner)
                        	OrCalcRef.makeCalculations();
                    }
                }
            }
            if (panel instanceof OrWebPanel) {
                ASTStart template = ((OrWebPanel) panel).getAfterOpenTemplate();
                if (template != null) {
                    ClientOrLang orlang = new ClientOrLang(frame, true);
                    Map<String, Object> vc = new HashMap<String, Object>();
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(template, vc, ((WebFrame) frame).getPanelAdapter(), new Stack<String>());
                    } catch (Exception ex) {
                        kz.tamur.rt.adapters.Util.showErrorMessage(((OrWebPanel) panel), ex.getMessage(),
                                "Действие после открытия вкладки");
	                	log.error("Ошибка при выполнении формулы 'Действие после открытия вкладки'" + panel.getClass().getName() + "', uuid: " + panel.getUUID());
                        log.error(ex, ex);
                    } finally {
                        if (calcOwner)
                        	OrCalcRef.makeCalculations();
                    }
                }
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
