package kz.tamur.web.component;

import java.awt.Dimension;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.AnalyticPanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.rt.adapters.AnalyticPanelAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.web.common.webgui.WebPanel;

public class OrWebAnalyticPanel extends WebPanel implements OrPanelComponent {
	
	public static PropertyNode PROPS = new AnalyticPanelPropertyRoot();
	private String title;
	private boolean enabled;
	private AnalyticPanelAdapter adapter;
	private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate, createXmlTemplate;
	private int analyticType, aggType;
	private String xAxis, yAxis, zAxis, fact, xAxisExpr, yAxisExpr, zAxisExpr, firstXAxisExpr, firstYAxisExpr, aggField;
	private boolean showLegend;

	public OrWebAnalyticPanel(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
		super(xml, mode, frame, id);
		uuid = PropertyHelper.getUUID(this, frame);
		try {
			init(mode);
		} catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        }
	}
	
	private void init(int mode) throws KrnException {
		adapter = new AnalyticPanelAdapter(frame, this, false);
		if (mode != Mode.DESIGN) {
			setConstraints(PropertyHelper.getConstraints(PROPS, xml, id, frame));
		}
	
		if (mode == Mode.RUNTIME) {
			PropertyNode pn = getProperties().getChild("analytic").getChild("xAxis");
	        PropertyValue pv;
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                	xAxisExpr = pv.stringValue(frame.getKernel());
                }
            }
            
            pn = getProperties().getChild("analytic").getChild("yAxis");
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                	yAxisExpr = pv.stringValue(frame.getKernel());
                }
            }
            
            pn = getProperties().getChild("analytic").getChild("zAxis");
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                	zAxisExpr = pv.stringValue(frame.getKernel());
                }
            }
            
            pn = getProperties().getChild("analytic").getChild("firstXAxis");
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                	firstXAxisExpr = pv.stringValue(frame.getKernel());
                }
            }
            
            pn = getProperties().getChild("analytic").getChild("firstYAxis");
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                	firstYAxisExpr = pv.stringValue(frame.getKernel());
                }
            }
            
            pn = getProperties().getChild("analytic").getChild("type");
            if (pn != null) {
            	pv = getPropertyValue(pn);
            	Object pValue = pv.getValue();
            	if (pValue != null) {
            		analyticType = (Integer) pValue;
            	} else {
            		analyticType = (Integer) pn.getDefaultValue();
            	}
            }
            
            pn = getProperties().getChild("analytic").getChild("showLegend");
            if (pn != null) {
            	pv = getPropertyValue(pn);
            	showLegend = pv.booleanValue();
            }
            
            pn = getProperties().getChild("analytic").getChild("fact");
            if (pn != null) {
            	pv = getPropertyValue(pn);
            	fact = pv.stringValue();
            }
            
            pn = getProperties().getChild("analytic").getChild("agg").getChild("aggType");
            if (pn != null) {
            	pv = getPropertyValue(pn);
            	Object pValue = pv.getValue();
            	if (pValue != null) {
            		aggType = (Integer) pValue;
            	} else {
            		aggType = (Integer) pn.getDefaultValue();
            	}
            }
            
            pn = getProperties().getChild("analytic").getChild("agg").getChild("aggField");
            if (pn != null) {
            	pv = getPropertyValue(pn);
            	aggField = pv.stringValue();
            }
		}
	}
	
	public String getX() {
		return xAxis;
	}
	
	public String getY() {
		return yAxis;
	}
	
	public String getZ() {
		return zAxis;
	}
	
	public String getFact() {
		return fact;
	}

	@Override
	public boolean canAddComponent(int x, int y) {
		return false;
	}

	@Override
	public void addComponent(OrGuiComponent c, Object cs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object removeComponent(OrGuiComponent c) {
		children.remove(c);
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
		return title;
	}

	@Override
	public OrGuiComponent getComponent(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyNode getProperties() {
		return PROPS;
	}

	@Override
	public void setLangId(long langId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OrGuiContainer getGuiParent() {
		return null;
	}

	@Override
	public void setGuiParent(OrGuiContainer parent) {
		
	}

	@Override
	public Dimension getPrefSize() {
		return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
	}

	@Override
	public Dimension getMaxSize() {
		return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
	}

	@Override
	public Dimension getMinSize() {
		return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
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
	public ASTStart getAfterOpenTemplate() {
		return afterOpenTemplate;
	}

	@Override
	public ASTStart getBeforeOpenTemplate() {
		return beforeOpenTemplate;
	}

	@Override
	public ASTStart getBeforeCloseTemplate() {
		return beforeCloseTemplate;
	}

	@Override
	public ASTStart getAfterCloseTemplate() {
		return afterCloseTemplate;
	}

	@Override
	public ASTStart getCreateXmlTemplate() {
		return createXmlTemplate;
	}

	@Override
	public boolean isPanelEnabled() {
		return enabled;
	}

	@Override
	public String getIconName() {
		return iconName;
	}
	
	public String getXAxisExpr() {
		return xAxisExpr;
	}
	
	public String getYAxisExpr() {
		return yAxisExpr;
	}
	
	public String getZAxisExpr() {
		return zAxisExpr;
	}
	
	public String getFirstXAxisExpr() {
		return firstXAxisExpr;
	}
	
	public String getFirstYAxisExpr() {
		return firstYAxisExpr;
	}
	
	public int getType() {
		return analyticType;
	}
	
	public int getAggType()	{
		return aggType;
	}
	
	public String getAggField() {
		return aggField;
	}
	
	public boolean isShowLegend() {
		return showLegend;
	}
	
}
