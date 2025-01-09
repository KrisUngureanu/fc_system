package kz.tamur.web.component;

import kz.tamur.comps.models.ComboBoxPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.ComboBoxAdapter;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter.OrComboItem;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.web.common.WebUtils;
import kz.tamur.web.common.webgui.WebComboBox;
import kz.tamur.web.controller.WebController;
import kz.tamur.or3.client.comps.interfaces.OrComboBoxComponent;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 18:24:06
 * To change this template use File | Settings | File Templates.
 */
public class OrWebComboBox extends WebComboBox implements OrComboBoxComponent {
    public static PropertyNode PROPS = new ComboBoxPropertyRoot();

    private OrGuiContainer parent;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private int tabIndex;
    private String fFam;
    private int fSize;
    private int fStyle;
    private ComboBoxAdapter adapter;
    private boolean isHelpClick = false;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;

    OrWebComboBox(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
    	super(xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        configNumber = ((WebFrame)frame).getSession().getConfigNumber();
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        //description = PropertyHelper.getDescription(this);
	        updateProperties();
	
	        // Создаем адаптер TODO UIFrame -> OrFrame
	        adapter = new ComboBoxAdapter(frame, this, isEditor);
	        adapter.calculateContent();
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

        // Не храним XML в режиме выполнения
        this.xml = null;
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

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
        	updateDescription();
        }
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
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

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    private void processProperties(PropertyValue pv) {
        PropertyNode pn = pv.getProperty();
        if ("backgroundColor".equals(pn.getName())) {
            //setBackground(pv.colorValue());
        } else if ("fontColor".equals(pn.getName())) {
            //setForeground(pv.colorValue());
        } else if ("fontG".equals(pn.getName())) {
            setFont(pv.fontValue());
        } else if ("borderType".equals(pn.getName())) {
            //setBorder(pv.borderValue());
        }
    }

    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view");
        PropertyValue pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            //setForeground(pv.colorValue());
        } else {
            //setForeground((Color)pn.getChild("font").getChild("fontColor").getDefaultValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            //setBackground(pv.colorValue());
        } else {
            //setBackground((Color)pn.getChild("background").getChild("backgroundColor").getDefaultValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
            fFam = pv.fontValue().getName();
            if (fFam.equals("Dialog")) {
                fFam = "Arial";
            }
            fSize = pv.fontValue().getSize();
            fStyle = pv.fontValue().getStyle();
        } else {
            setFont((Font)pn.getChild("font").getChild("fontG").getDefaultValue());
        }
        pv = getPropertyValue(pn.getChild("appearance"));
        if (!pv.isNull()) {
        	setAppearance(pv.enumValue());
        }
        pv = getPropertyValue(pn.getChild("comboSearch"));
        if (!pv.isNull()) {
        	setComboSearch(!pv.booleanValue());
        }
/*
        pn = getProperties().getChild("border");
        pv = getPropertyValue(pn.getChild("borderType"));
        if (!pv.isNull()) {
            setBorder(pv.borderValue());
        } else {
            setBorder((Border)pn.getChild("borderType").getDefaultValue());
        }
*/
        setEnabled(true);

        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        if (!pv.isNull()) {
            tabIndex = pv.intValue();
        } else {
            tabIndex = pv.intValue();
        }

        updateProperties(PROPS);
        
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
    }

    public ComponentAdapter getAdapter() {
    	return adapter;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setValue(Object value) {
        if (getAppearance() != Constants.VIEW_SIMPLE_COMBO && getAppearance() != Constants.VIEW_SOLID_LIST) {
        	clearSelection();
        	if (adapter.getDataRef().getAttribute() != null) {
	        	List<Item> items = adapter.getDataRef().getItems(0);
	        	for (Item item : items) {
	        		setSelectedIndex(getIndexForObject((KrnObject) item.getCurrent()));
	        	}
        	}
        } else if (value instanceof KrnObject) {
            setSelectedIndex(getIndexForObject((KrnObject) value));
    	} else if (value instanceof String) {
    		String o = (String)value;
	        int count = getItemCount();
	        if (count > 0) {
                for (int i = 0; i < count; ++i) {
                	ComboBoxAdapter.OrComboItem item = (ComboBoxAdapter.OrComboItem)getItemAt(i);
                    String curr = item.toString();
                    if (o != null && curr != null && o.equals(curr)) {
                        setSelectedIndex(i);
                        break;
                    }
                }
	        }
        } else {
            setSelectedIndex(-1);
        }
    }

    public void setValue(String value) {
        try {
        	if (value == null || value.length() == 0)
        		setSelectedIndexDirectly("0");
        	else if (adapter.isEditor() || value != null){
                for (int i = 1; i < getItemCount(); ++i) {
                	OrComboItem item = (OrComboItem)getItemAt(i);
                	if ((item.getObject() != null && value.equals(item.getObject().uid)) || (item.getObject() == null && value.equals(item.toString()))) {
                		setSelectedIndexDirectly(String.valueOf(i));
                		break;
                	}
                }
        	} else
        		setSelectedIndexDirectly(value);
            adapter.updateValue();
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
            		+ "| interface id=" + ((WebFrame)frame).getObj().id
            		+ "| ref=" + adapter.getRef() 
            		+ "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }
    
    public void addValue(String value) {
        try {
        	int i = Integer.parseInt(value);
    		boolean selected = setSelectedIndexDirectly(value);
    		if (!selected) {
	        	OrComboItem item = (OrComboItem)getItemAt(i);
	            adapter.addValue(item.getObject());
    		}
        } catch (Exception ex) {
            log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
            		+ "| interface id=" + ((WebFrame)frame).getObj().id
            		+ "| ref=" + adapter.getRef() 
            		+ "| value=" + value);
            log.error(ex.getMessage(), ex);
        }
    }

    public void deleteValue(String value) {
        try {
        	int i = Integer.parseInt(value);
    		removeSelectedIndexDirectly(value);
        	OrComboItem item = (OrComboItem)getItemAt(i);
            adapter.deleteValue(item.getObject());
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
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();//property.add("ORType", Class)
        int selectedIndex = getSelectedIndex();
        OrComboItem item = (OrComboItem)getItemAt(selectedIndex);
        String valueObj = null;
        if(item != null) {
        	valueObj = item.getObject() != null ? item.getObject().uid : item.toString();
        } else {
        	valueObj = "";
        }

        //property.add("isEditable", WebController.EDITABLE_COMBO);
        addSize(style);
        addConstraints(style);
        WebUtils.getColorState(state, style);
        if (!WebController.EDITABLE_COMBO) {

            property.add("e", toInt(isEnabled()));
        } else {
            style.add("height", sHeight.length() > 0 ? sHeight : "100%");
            if (sWidth.endsWith("px")) {
                style.add("width", Integer.parseInt(sWidth.substring(0, sWidth.length() - 2)) - 17);
            } else if ("100%".equals(sWidth) && cellWidth.endsWith("px")) {
                style.add("width", "95%");
            } else {
                style.add("width", "100%");
            }
            if (tooltipText != null) {
                property.add("tt", tooltipText);
            }
            property.add("e", toInt(isEnabled()));

            int count = getItemCount();
            int size = 4;
            if (count > 4 && count < 12) {
                size = count;
            } else if (count >= 12) {
                size = 12;
            }
            int hC = size * 20;
            property.add("height", hC);
            property.add("size", size);
        }

        if (!adapter.isEditor()) {
	        JsonArray arr = new JsonArray();
	        for (int i = 0; i < getItemCount(); ++i) {
	        	JsonObject contentObj = new JsonObject();
	        	item = (OrComboItem)getItemAt(i);
	            contentObj.add("o", item.toString());
	            contentObj.add("u", (item != null && item.getObject() != null) ? item.getObject().uid : item.toString());
	            contentObj.add("h", item.getHint());
	            arr.add(contentObj);
	        }
	        if (getItemCount() > 0) {
                if (getAppearance() == Constants.VIEW_SIMPLE_COMBO || getAppearance() == Constants.VIEW_SOLID_LIST)
                	property.add("content", arr);
                else
                	property.add("reload", 1);
                
	            property.add("fFam", fFam);
	            property.add("fSize", fSize);
	            property.add("fStyle", fStyle);
	        }
        }
        style.add("font-family", fFam);
        style.add("font-size", fSize);
        style.add("font-style", fStyle);
        if (style.size() > 0) {
            obj.add("st", style);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        property.add("value", valueObj);
        property.add("change", selectedIndex);

        sendChange(obj, isSend);
        return obj;
    }

    public String getData() {
    	//adapter.calculateContent();

    	JsonArray arr = new JsonArray();
    	if(getAppearance() == Constants.VIEW_SIMPLE_COMBO || getAppearance() == Constants.VIEW_SOLID_LIST){
    		for (int i = 0; i < getItemCount(); ++i) {
    			OrComboItem item = (OrComboItem) getItemAt(i);
    			JsonObject itemObj = new JsonObject();
            	itemObj.add(uuid, item.getObject() != null ? item.getObject().uid : item.toString());
            	JsonArray titleArr = new JsonArray();
            	if (item.isListTitle()) {
            		for (Object title : item.getTitles()) {
            			titleArr.add(title.toString());
            		}
            	} else {
            		titleArr.add(item.toString());
            	}
            	itemObj.add(uuid + "-title", titleArr);
            	arr.add(itemObj);
    		}
    		return arr.toString();
    	} else {
    		int[] selIdxs = getSelectedIndicies();
    		for (int i = 0; i < getItemCount(); ++i) {
    			JsonObject itemObj = new JsonObject();
        		String title = getItemAt(i).toString();
/*        		itemObj.add(uuid, i);
            	if (title.isEmpty()) title = "-";
            	itemObj.add(uuid + "-title", title);
*/ 
        		itemObj.add("text", title);
            	boolean contains = false;
            	for (int idx : selIdxs) {
            		if (idx == i) {
            			contains = true;
            			break;
            		}
            	}
            	if (contains)
            		itemObj.add("checkbox", "true");
            	arr.add(itemObj);
        	}
        	return arr.toString();
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
    
	public void setData(List<String> data) {
		adapter.setData(data);
	}

    public void setModel(ComboBoxModel aModel) {
    	super.setModel(aModel);
        if (!adapter.isEditor()) {
        	if (getAppearance() == Constants.VIEW_SIMPLE_COMBO || getAppearance() == Constants.VIEW_SOLID_LIST) {
                JsonArray arr = new JsonArray();
                for (int i = 0; i < getItemCount(); ++i) {
                	JsonObject contentObj = new JsonObject();
                	JsonArray titleArr = new JsonArray();
                	OrComboItem item = (OrComboItem)getItemAt(i);
                	if (item.isListTitle()) {
                		List titles = item.getTitles();
                		for (Object title : titles) {
                			titleArr.add(title.toString());
                		}
                	} else {
                		titleArr.add(item.toString());
                	}
                	contentObj.add("o", titleArr);
    	            contentObj.add("u", (item != null && item.getObject() != null) ? item.getObject().uid : item.toString());
                    arr.add(contentObj);
                }
        		sendChangeProperty("content", arr);
        	} else
        		sendChangeProperty("reload", 1);
        }
    }
}
