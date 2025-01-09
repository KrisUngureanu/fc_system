package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.ImagePanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.rt.adapters.ImagePanelAdapter;
import kz.tamur.rt.adapters.ImagePanelAdapter.OrImageItem;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebGridBagLayout;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.controller.WebController;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 14.07.2006
 * Time: 17:02:48
 * To change this template use File | Settings | File Templates.
 */
public class OrWebImagePanel extends WebPanel implements OrPanelComponent {
	
    public static PropertyNode PROPS = new ImagePanelPropertyRoot();
    private WebGridBagLayout gbl = new WebGridBagLayout();

    private boolean enabled;
    private String title;
    private String titleUID;
    private ImagePanelAdapter adapter;
    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate, createXmlTemplate, afterSaveTemplate, afterTaskListUpdateTemplate, onNotificationTemplate;
    private String borderTitleUID;
    private String titleAlign = "center";
	private DefaultListModel<OrImageItem> model;
	private ListSelectionModel selectionModel;
	private String imageUID = null;
	private int orientation = 1; // горизонтальная по умолчанию

    public OrWebImagePanel(Element xml, int mode, WebFactory fm, OrFrame frame, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model = new DefaultListModel<>();
		
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
        adapter = new ImagePanelAdapter(frame, this, false);

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
        
        pn = getProperties().getChild("view").getChild("imageUID");
        if (pn != null) {
            pv = getPropertyValue(pn);
            this.imageUID  = pv.isNull() ? null : pv.stringValue();
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

        pn = PROPS.getChild("view").getChild("orientation");
        pv = getPropertyValue(pn);
        if (!pv.isNull()) {
            setOrientation(pv.enumValue());
        } else {
        	setOrientation((Integer)pn.getDefaultValue());
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

    public ImagePanelAdapter getAdapter() {
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
    
    public void setValue(String value) {
        try {
        	if (value == null || value.length() == 0)
        		setSelectedIndex(0);
        	else
        		setSelectedIndex(Integer.parseInt(value));
        	
//            adapter.updateValue();
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
            		+ "| interface id=" + ((WebFrame)frame).getObj().id
            		+ "| ref=" + adapter.getRef() 
            		+ "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        int selectedIndex = getSelectedIndex();
        property.add("selectedImg", selectedIndex);

        property.add("e", toInt(isEnabled()));
        
    	//property.add("reload", 1);
                
        if (property.size() > 0) {
            obj.add("pr", property);
        }

        sendChange(obj, isSend);
        return obj;
    }

    public String getData(String rows) {
    	JsonObject res = new JsonObject();
    	
    	JsonArray items = new JsonArray();
    	
    	if (rows == null ) {
    		for (int i = 0; i < getItemCount(); ++i) {
    			OrImageItem item = getItemAt(i);
    			JsonObject itemObj = new JsonObject();
            	
    			String title = item.toString();
    			String path = item.getDownloadPath();
    			
            	itemObj.set("index", i);
            	itemObj.set("title", title);
            	itemObj.set("src", item.isImage() ? (WebController.WEB_IMGAGES_SUBDIR + path) : "/jsp/media/img/nofoto.png");
            	items.add(itemObj);
    		}  		
    	} else {
    		String[] rowsArr = rows.split(",");
    		for (String row : rowsArr) {
    			int i = Integer.parseInt(row);
    			OrImageItem item = getItemAt(i);
    			JsonObject itemObj = new JsonObject();
            	
    			String title = item.toString();
    			String path = item.getDownloadPath();
    			
            	itemObj.set("index", i);
            	itemObj.set("title", title);
            	itemObj.set("src", item.isImage() ? (WebController.WEB_IMGAGES_SUBDIR + path) : "/jsp/media/img/nofoto.png");
            	items.add(itemObj);
    		}  		
    	}
    	
		res.set("items", items);
		res.set("count", getItemCount());
		res.set("id", this.getUUID());
		res.set("orientation", this.getOrientation());
		res.set("height", this.getHeight());
		res.set("width", this.getWidth());
		if (this.getImageUID() != null)
			res.set("cid", this.getImageUID());

        int selectedIndex = getSelectedIndex();
        res.set("selectedImg", selectedIndex);

		return res.toString();
    }

    public void setModel(DefaultListModel<OrImageItem> model) {
    	this.model = model;
    	removeChangeProperties();
		sendChangeProperty("reload", 1);
    }
    
    public void imageInserted(int index) {
    	JsonArray arr = new JsonArray();
		arr.add(index);
		sendChangeProperty("imageAdded", arr);
    }

    public void imageDeleted(int start, int end) {
    	JsonArray arr = new JsonArray();
    	for (int index = end - 1; index >= start; index--)
    		arr.add(index);
		sendChangeProperty("imageDeleted", arr);
    }

    public DefaultListModel<OrImageItem> getModel() {
    	return this.model;
    }
    
    public void titleChanged(int i, String title) {
    	if (i > -1 && i < getItemCount()) {
	    	JsonObject res = new JsonObject();
			OrImageItem item = getItemAt(i);
			item.setTitle(title);
	
			res.set("index", i);
			res.set("title", title);
	    	
	    	sendChangeProperty("imgTitleChanged", res);
    	}
    }

    public void imageChanged(int i, File file) {
    	if (i > -1 && i < getItemCount()) {
	    	JsonObject res = new JsonObject();
			OrImageItem item = getItemAt(i);
			item.setFile(file);
			String path = item.getDownloadPath();
			
			res.set("index", i);
			res.set("file", item.isImage() ? (WebController.WEB_IMGAGES_SUBDIR + path) : "/jsp/media/img/nofoto.png");
	    	
	    	sendChangeProperty("imgFileChanged", res);
    	}
    }

    public void setSelectedIndexDirectly(int anIndex) {
		selectionModel.addSelectionInterval(anIndex, anIndex);
		JsonObject props = getChangeProperties();
		if (props == null || props.get("pr") == null || props.get("pr").asObject().get("reload") == null)
			sendChangeProperty("selectedImg", anIndex);
    }

    public void setSelectedIndex(int anIndex) {
    	adapter.setSelectedIndex(anIndex);
    }
    
    public int getItemCount() {
        return (model != null) ? model.getSize() : 0;
    }

    public OrImageItem getItemAt(int index) {
        return (model != null) ? model.getElementAt(index) : null;
    }

    public OrImageItem getSelectedItem() {
    	int index = getSelectedIndex();
        return (model != null && index > -1 && index < getItemCount()) ? getItemAt(index) : null;
    }

    public int getSelectedIndex() {
    	return selectionModel.getMinSelectionIndex();
    }

    public String getImageUID() {
    	return this.imageUID;
    }
    
    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}