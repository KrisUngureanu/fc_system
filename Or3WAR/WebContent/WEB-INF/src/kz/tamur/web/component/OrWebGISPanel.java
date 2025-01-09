package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.border.Border;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.GISPanelPropertyRoot;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.rt.adapters.GISPanelAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebGridBagLayout;
import kz.tamur.web.common.webgui.WebPanel;

import org.jdom.Element;
import org.json.JSONObject;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 14.07.2006
 * Time: 17:02:48
 * To change this template use File | Settings | File Templates.
 */
public class OrWebGISPanel extends WebPanel implements OrPanelComponent {
	
    public static PropertyNode PROPS = new GISPanelPropertyRoot();
    private WebGridBagLayout gbl = new WebGridBagLayout();

    private boolean enabled;
    private String title;
    private String titleUID;
    private GISPanelAdapter adapter;
    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate, createXmlTemplate, afterSaveTemplate, afterTaskListUpdateTemplate, onNotificationTemplate;
    private String borderTitleUID;
    private String titleAlign = "center";
    
    public OrWebGISPanel(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
        	init(fm, mode);
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
//        this.xml = null;
    }

    private void init(WebFactory fm, int mode) throws KrnException {
        setLayout(gbl);
        adapter = new GISPanelAdapter(frame, this, false);

        PropertyNode pn = PROPS.getChild("pov").getChild("activity");
        PropertyValue pv =  getPropertyValue(PROPS.getChild("children"));
        if (!pv.isNull()) {
            List<Element> children = pv.elementValue().getChildren();
            
            for (Element child : children) {
                WebComponent c = fm.create(child, mode, frame);
                if (c instanceof OrGuiComponent) {
                    GridBagConstraints cs = c.getConstraints();
                    if (mode == Mode.RUNTIME) {
                        setPreferredSize((OrGuiComponent)c);
                        setMinimumSize((OrGuiComponent)c);
                        setMaximumSize((OrGuiComponent)c);
                        add(c, cs);
                    }
                }
            }
       }
        //Utils.processBorder(this, frame, borderProps);
        if (mode != Mode.DESIGN) {
            setConstraints(PropertyHelper.getConstraints(PROPS, xml, id, frame));
            setPreferredSize(PropertyHelper.getPreferredSize(this, id, frame));
            setMaximumSize(PropertyHelper.getMaximumSize(this, id, frame));
            setMinimumSize(PropertyHelper.getMinimumSize(this, id, frame));

            PropertyValue titleVal = getPropertyValue(PROPS.getChild("title"));
            if (!titleVal.isNull()) {
                Pair p = titleVal.resourceStringValue();
                titleUID = (String)p.first;
                title = frame.getString(titleUID);
            }
            if (title.length() == 0) {
				PropertyValue titleExprVal = getPropertyValue(PROPS.getChild("title1").getChild("expr"));
				if (!titleExprVal.isNull()) {
	                String titleExpr = (String) titleExprVal.objectValue();
	                title = Utils.getExpReturn(titleExpr, frame, getAdapter());
				}
			}
        }
        updateProperties();
        pn = getProperties().getChild("pov").getChild("activity").getChild("enabled");
        if (pn != null) {
            pv = getPropertyValue(pn);
            enabled = pv.isNull() ? (Boolean) pn.getDefaultValue() : pv.booleanValue();
        }

        if (mode == Mode.RUNTIME) {
            pn = getProperties().getChild("pov").getChild("beforeOpen");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue(frame.getKernel());
                }
                if (expr != null && expr.length() > 0) {
                    long ifcId = ((WebFrame) frame).getObj().id;
                    String key = id + "_" + OrLang.BEFORE_OPEN_TYPE;
                    beforeOpenTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (String path : paths) {
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                    	log.error(ex, ex);
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("afterOpen");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue(frame.getKernel());
                }
                if (expr != null && expr.length() > 0) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = id + "_" + OrLang.AFTER_OPEN_TYPE;
                    afterOpenTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (String path : paths) {
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                    OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                    	log.error(ex, ex);
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("beforeClose");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue(frame.getKernel());
                }
                if (expr != null && expr.length() > 0) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = id + "_" + OrLang.BEFORE_CLOSE_TYPE;
                	beforeCloseTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (String path : paths) {
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                    OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                    	log.error(ex, ex);
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("afterClose");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue(frame.getKernel());
                }
                if (expr != null && expr.length() > 0) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = id + "_" + OrLang.AFTER_CLOSE_TYPE;
                	afterCloseTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (String path : paths) {
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                    OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                    	log.error(ex, ex);
                    }
                }
            }
            
            pn = getProperties().getChild("pov").getChild("afterSave");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterSaveTemplate = OrLang.createStaticTemplate(expr, getLog());
                }
            }
            pn = getProperties().getChild("pov").getChild("afterTaskListUpdate");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterTaskListUpdateTemplate = OrLang.createStaticTemplate(expr, getLog());
                }
            }
            pn = getProperties().getChild("pov").getChild("onNotification");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                        onNotificationTemplate = OrLang.createStaticTemplate(expr, getLog());
                }
            }
            pn = getProperties().getChild("pov").getChild("createXml");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue(frame.getKernel());
                }
                if (expr != null && expr.length() > 0) {
                	long ifcId = ((WebFrame)frame).getObj().id;
                	String key = id + "_" + OrLang.CREATE_XML_TYPE;
                	createXmlTemplate = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                    OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                    	log.error(ex, ex);
                    }
                }
            }

            pn = PROPS.getChild("titleAlign");
            if (pn != null) {
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    switch (pv.enumValue()) {
                    case GridBagConstraints.CENTER:
                        titleAlign  = "center";
                        break;
                    case GridBagConstraints.WEST:
                        titleAlign  = "left";
                        break;
                    case GridBagConstraints.EAST:
                        titleAlign  = "right";
                        break;
                    }
                }
            }
        }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view").getChild("background");
        PropertyValue pv = getPropertyValue(pn.getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        } else {
            setBackground((Color)pn.getChild("backgroundColor").getDefaultValue());
        }
        
        pv = getPropertyValue(PROPS.getChild("view").getChild("icon"));
        if (!pv.isNull()) {
            setIcon(pv.getImageValue());
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("backgroundPict"));
        if (!pv.isNull()) {
            setBackgroundPict(pv.getImageValue());
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("positionPict"));
        if (!pv.isNull()) {
            setPositionPict(pv.intValue());
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("autoResizePict"));
        if (!pv.isNull()) {
            setAutoResizePict(pv.booleanValue());
        }

        pn = getProperties().getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            if (!pv.isNull()) {
                borderType = pv.borderValue();
            } else {
                borderType = (Border)pn.getChild("borderType").getDefaultValue();
            }
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String)pv.resourceStringValue().first;
            }
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        
        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("gradient"));
        if (!pv.isNull()) {
            isFoundGradient = true;
            setGradient((GradientColor) pv.objectValue());
        } else {
            isFoundGradient = false;
        }
        pv = getPropertyValue(pn.getChild("transparent"));
        if (!pv.isNull()) {
            setOpaque(!pv.booleanValue());
        }

        processBorderProperties();
    }

    private void setIcon(byte[] imageValue) {
        iconName = com.cifs.or2.client.Utils.createFileImg(imageValue, "icoPanel");
    }

    private void setBackgroundPict(byte[] imageValue) {
        bgImageName = com.cifs.or2.client.Utils.createFileImg(imageValue, "bgPanel");
    }

    public void setPositionPict(int positionPict) {
        this.positionPict = positionPict;
    }

    public void setAutoResizePict(boolean autoResizePict) {
        this.autoResizePict = autoResizePict;
    }

	private void processBorderProperties() {
        borderTitle = frame.getString(borderTitleUID, "");
    }

    public void setPreferredSize(OrGuiComponent comp) {
        Dimension sz = comp.getPrefSize();
        if (sz != null) {
            ((WebComponent)comp).setPreferredSize(sz);
        }
    }

    public void setMinimumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMinSize();
        if (sz != null) {
            ((WebComponent)comp).setMinimumSize(sz);
        }
    }

    public void setMaximumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMaxSize();
        if (sz != null) {
            ((WebComponent)comp).setMaximumSize(sz);
        }
    }

    // implementing OrPanelComponent
    public boolean canAddComponent(int x, int y) {
        return false;
    }

    public void addComponent(OrGuiComponent c, Object cs) {
        GridBagConstraints cns = c.getConstraints();
        if (mode == Mode.RUNTIME) {
            setPreferredSize(c);
            setMinimumSize(c);
            setMaximumSize(c);
            add((WebComponent)c, cns);
        }
    }

    public Object removeComponent(OrGuiComponent c) {
        children.remove(c);
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
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getComponentStatus() {
        return 0;
    }

    public void setLangId(long langId) {
        processBorderProperties();
        title = frame.getString(titleUID);
        if (title.length() == 0) {
			PropertyValue titleExprVal = getPropertyValue(PROPS.getChild("title1").getChild("expr"));
			if (!titleExprVal.isNull()) {
                String titleExpr = (String) titleExprVal.objectValue();
                title = Utils.getExpReturn(titleExpr, frame, getAdapter());
			}
		}
        for (int i = 0; i < children.size(); i++) {
            OrGuiComponent comp = (OrGuiComponent) children.get(i);
            comp.setLangId(langId);
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

    public boolean isPanelEnabled() {
        return enabled;
    }

    public byte[] getDescription() {
        return new byte[0];
    }

    public GISPanelAdapter getAdapter() {
        return adapter;
    }

    public ASTStart getAfterOpenTemplate() {
        return afterOpenTemplate;
    }

    public ASTStart getBeforeOpenTemplate() {
        return beforeOpenTemplate;
    }

    public ASTStart getBeforeCloseTemplate() {
        return beforeCloseTemplate;
    }

    public ASTStart getAfterCloseTemplate() {
        return afterCloseTemplate;
    }

    public ASTStart getAfterSaveTemplate() {
        return afterSaveTemplate;
    }

    public ASTStart getCreateXmlTemplate() {
        return createXmlTemplate;
    }
    
    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName())) {
            return this;
        }
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            WebComponent c = getComponent(i);
            if (c instanceof OrGuiContainer) {
                OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
                if (cc != null) {
                    return cc;
                }
            } else if (c instanceof OrGuiComponent) {
                OrGuiComponent gc = (OrGuiComponent) c;
                if (title.equals(gc.getVarName())) {
                    return gc;
                }
            }
        }
        return null;
    }

	@Override
	public String getIconName() {
		return iconName;
	}

    public String setSizeAndLoad(String id, long fid) {
        StringBuilder b = new StringBuilder(128);
        b.append("<r><id>").append(id).append("</id>");
        b.append("<fid>").append(fid).append("</fid>");
        b.append("<width>").append(getMaxWidth()).append("</width>");
        b.append("<height>").append(getMaxHeight()).append("</height>");
        b.append("<title>").append(title).append("</title></r>");
        JsonObject out = new JsonObject();
        out.add("id", id);
        out.add("fid", fid);
        out.add("wigth", getMaxWidth());
        out.add("height", getMaxHeight());
        out.add("title", title);
        sendChange2("loadFrame", out);
        return b.toString();
    }

    public String setSizeAndLoad(String id, long fid, int row, int col) {
        StringBuilder b = new StringBuilder(128);
        b.append("<r><id>").append(id).append("</id>");
        b.append("<fid>").append(fid).append("</fid>");
        b.append("<width>").append(getMaxWidth()).append("</width>");
        b.append("<height>").append(getMaxHeight()).append("</height>");
        b.append("<row>").append(row).append("</row>");
        b.append("<col>").append(col).append("</col>");
        b.append("<title>").append(title).append("</title></r>");
        
        JsonObject out = new JsonObject();
        out.add("id", id);
        out.add("fid", fid);
        out.add("wigth", getMaxWidth());
        out.add("height", getMaxHeight());
        out.add("row", row);
        out.add("col", col);
        out.add("title", title);
        sendChange2("loadFrame", out);
        
        return b.toString();
    }
    
    public ASTStart getAfterTaskListUpdateTemplate() {
        return afterTaskListUpdateTemplate;
    }
    
    public ASTStart getOnNotificationTemplate() {
        return onNotificationTemplate;
    }

	public void removeChangeProperties() {
		super.removeChangeProperties();
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            WebComponent c = getComponent(i);
            c.removeChangeProperties();
        }
	}

    public String getTitleAlign() {
        return titleAlign;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
    
	public void gisParseRequest(JsonObject val) {
        sendChangeProperty("gisParseRequest", val);
	}

	public void gisParseRequest(JSONObject val) {
		JsonObject obj = JsonObject.readFrom(val.toString());
        sendChangeProperty("gisParseRequest", obj);
	}

	public void gisParseRequest(String val) {
		JsonObject obj = JsonObject.readFrom(val);
        sendChangeProperty("gisParseRequest", obj);
	}

	public void setBounds(Object val) {
		List points = (List) val;
		JsonArray jsonPoints = new JsonArray();
		for (int i = 0; i < points.size(); i++) {
			jsonPoints.add(points.get(i));
		}
        sendChangeProperty("gisBounds", jsonPoints);
	}
	
	public void setLayers(Object val) {
		List layers = (List) val;
		JsonArray jsonLayers = new JsonArray();
		for (int i = 0; i < layers.size(); i++) {
			Map layer = (Map) layers.get(i);
			JsonObject jsonLayer = new JsonObject();
			jsonLayer.add("name", (String) layer.get("name"));
			jsonLayer.add("type", (String) layer.get("type"));
			Map source = (Map) layer.get("source");
			JsonObject jsonSource = new JsonObject();
			jsonSource.add("url", (String) source.get("url"));
			jsonSource.add("params", (String) source.get("params"));
			jsonLayer.add("source", jsonSource);
			jsonLayers.add(jsonLayer);
		}
        sendChangeProperty("gisLayers", jsonLayers);
	}
	
	public void setLayerVisible(String layerName, boolean isVisible) {
		JsonObject layerVisible = new JsonObject();
		layerVisible.add("layerName", layerName);
		layerVisible.add("isVisible", isVisible);
        sendChangeProperty("gisLayerVisible", layerVisible);
	}
	
	public void setLayerOpacity(String layerName, double opacity) {
		JsonObject layerOpacity = new JsonObject();
		layerOpacity.add("layerName", layerName);
		layerOpacity.add("opacity", opacity);
        sendChangeProperty("gisLayerOpacity", layerOpacity);
	}
	
	public void setSelections(Object val) {
		List objects = (List) val;
		JsonArray jsonObjects = new JsonArray();
		for (int i = 0; i < objects.size(); i++) {
			List points = (List) objects.get(i);
			JsonArray jsonPoints = new JsonArray();
			for (int j = 0; j < points.size(); j++) {
				List point = (List) points.get(j);
				JsonArray jsonPoint = new JsonArray();
				jsonPoint.add(point.get(0));
				jsonPoint.add(point.get(1));
				jsonPoints.add(jsonPoint);
			}
			jsonObjects.add(jsonPoints);
		}
        sendChangeProperty("gisSelections", jsonObjects);
	}
	
	public void setFormula(List bounds) {
		JsonArray jsonBounds = new JsonArray();
		for (int i = 0; i < bounds.size(); i++) {
			jsonBounds.add(bounds.get(i));
		}
        sendChangeProperty("gisCreateMap", jsonBounds);
	}
}