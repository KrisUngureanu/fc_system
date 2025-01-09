package kz.tamur.web.common.webgui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.Margin;
import kz.tamur.web.common.WebActionMaker;
import kz.tamur.web.common.WebChangeMaker;
import kz.tamur.web.common.WebUtils;

import org.apache.commons.codec.binary.Base64;
import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 24.01.2007
 * Time: 17:54:27
 */
public class WebButton extends WebComponent implements JSONComponent, WebActionMaker, WebChangeMaker {

    /** The icon path. */
    protected String iconPath;

    /** The icon full path. */
    protected String iconFullPath;

    /** The tool tip. */
    private String toolTip;

    private String textInfo;
    
    private String textInfoUID;
    
    /** The text. */
    protected String text;

    /** The old text. */
    private String oldText = "";

    /** The value changed. */
    protected boolean valueChanged = false;

    /** The is selected. */
    private boolean isSelected;

    /** The btn group. */
    private WebButtonGroup btnGroup;

    /** The selection changed. */
    protected boolean selectionChanged;

    /** The h align. */
    private int hAlign;

    /** The drop down. */
    private boolean dropDown;

    /** The action id. */
    private String actionId = null;
    private String changeId = null;

    /** Внутренние отступы. */
    protected Dimension padding = null;

    protected Margin marginImage = null;

    private StringBuilder actionClick = null;

    private StringBuilder additParam = null;

    // private Map<String, Object> actionClick2 = new HashMap<String, Object>();

    // private Map<String, Object> additParam2 = new HashMap<String, Object>();

    private StringBuilder disable = null;
    private StringBuilder classes = null;

    private boolean subComponent = false;

    private ActionListener actionListener;
    /** Слушатель изменений */
    private ChangeListener changeListener;
    /** Изменённое значение */
    protected String changeValue = null;

    protected boolean isIconVisible = true;
    
    private byte[] iconBytes;

    public WebButton(String name, Element xml, int mode, OrFrame frame, String id) {
        this(null, null, false, name, null, xml, mode, frame, id);
    }

    public WebButton(String iconPath, String toolTip, Element xml, int mode, OrFrame frame, String id) {
        this(iconPath, toolTip, false, null, null, xml, mode, frame, id);
    }

    public WebButton(String iconPath, String toolTip, String actionId, Element xml, int mode, OrFrame frame, String id) {
        this(iconPath, toolTip, false, null, actionId, xml, mode, frame, id);
    }

    public WebButton(String iconPath, String toolTip, boolean dropDown, String name, String actionId, Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        this.iconPath = iconPath;
        this.toolTip = toolTip;
        this.dropDown = dropDown;
        this.actionId = actionId;
        setName(name);
        setBackground(WebUtils.LIGHT_SYS_COLOR);
        setForeground(WebUtils.DEFAULT_FONT_COLOR);
        marginImage = new Margin(2, 2, 2, 2);
        session = getWebSession();
    }

    public void setToolTipText(String toolTip) {
        this.toolTip = toolTip;
        sendChangeProperty("toolTip", toolTip);
    }
    
    public boolean isIconVisible() {
    	return isIconVisible;
    }
    
    public void changeTextInfo(String textInfo) {
    	this.textInfo = textInfo;
        sendChangeProperty("textInfo", textInfo);
    }
    
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }
    
    public void setTextInfo(String textInfo) {
        this.textInfo = textInfo;
    }
    
    public void setTextInfoUID(String textInfoUID) {
        this.textInfoUID = textInfoUID;
    }
    
    public String getTextInfoUID() {
        return textInfoUID;
    }
    
    public void setIcon(byte[] iconBytes) {
        this.iconBytes = iconBytes;
    }

    public void setHorizontalAlignment(int a) {
        hAlign = a;
        // JSON не используется
        // sendChangeProperty("hAlign", a);
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getIconFullPath() {
        return iconFullPath;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
        iconFullPath = iconPath == null ? null : "images/" + iconPath + ".gif";
        JsonObject img = new JsonObject();
        img.set("src", iconFullPath);
        sendChangeProperty("img", img);
    }

    public void setIconFullPath(String fullPath) {
        iconFullPath = fullPath;
        JsonObject img = new JsonObject();
        img.set("src", fullPath);
        sendChangeProperty("img", img);
    }

    /**
     * Установить текст кнопки.
     * 
     * @param text
     *            новый текст кнопки
     */
    public void setText(String text) {
        this.text = text;
        valueChanged = true;
        sendChangeProperty("text", text);
    }

    public void setTextDirectly(String text) {
        this.text = text;
    }

    /**
     * Получить текст кнопки.
     * 
     * @return the текст
     */
    public String getText() {
        return text;
    }

    /**
     * Установить selected.
     * 
     * @param b
     *            the new selected
     */
    public void setSelected(boolean b) {
        isSelected = b;
        selectionChanged = true;
        if (b && btnGroup != null) {
            btnGroup.setSelected(this);
        }
        sendChangeProperty("selected", b);
    }

    /**
     * Проверяет, является ли компонент выбранным
     * 
     * @return true, если выбран
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Установить внутренние отступы компонента
     * 
     * @param padding
     *            новые отступы
     */
    public void setPadding(Dimension padding) {
        this.padding = padding;
        JsonObject style = new JsonObject();
        if (padding != null) {
            style.add("padding", padding.getHeight() + " " + padding.getWidth());
        }
        sendChange2("st", style);
    }

    /**
     * @return the actionClick
     */
    public StringBuilder getActionClick() {
        return actionClick;
    }

    /**
     * @param actionClick
     *            the actionClick to set
     */
    public void setActionClick(StringBuilder actionClick) {
        this.actionClick = actionClick;
    }

    /**
     * @return the additParam
     */
    public StringBuilder getAdditParam() {
        return additParam;
    }

    /**
     * @param additParam
     *            the additParam to set
     */
    public void setAdditParam(StringBuilder additParam) {
        this.additParam = additParam;
    }

    public StringBuilder getDisable() {
        return disable;
    }

    public StringBuilder getClasses() {
        return classes;
    }

    public void setClasses(StringBuilder classes) {
        this.classes = classes;
        sendChangeProperty("class", classes);
    }

    public void setDisable(StringBuilder disable) {
        this.disable = disable;
        sendChangeProperty("enabled", disable);
    }

    public boolean isSubComponent() {
        return subComponent;
    }

    public void setSubComponent(boolean subComponent) {
        this.subComponent = subComponent;
        if(constraints != null) {
            sendChange2("st.margin-top", constraints.insets.top);
            sendChange2("st.margin-left", constraints.insets.left);
            sendChange2("st.margin-bottom", constraints.insets.bottom);
            sendChange2("st.margin-right", constraints.insets.right); 
        }
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;

    }

    @Override
    public void makeAction() {
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "press"));
        }
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;

    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void addChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void makeChange(String value) {
        if (changeListener != null) {
            changeValue = value;
            changeListener.stateChanged(new ChangeEvent(this));
        }

    }

    /**
     * @return the btnGroup
     */
    public WebButtonGroup getBtnGroup() {
        return btnGroup;
    }

    /**
     * @param btnGroup
     *            the btnGroup to set
     */
    public void setBtnGroup(WebButtonGroup btnGroup) {
        this.btnGroup = btnGroup;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        if (dropDown) {
            property.add("dataToggle", "dropdown");
        }
        if (toolTip != null) {
            property.add("tt", toolTip);
        }

        if (actionId != null) {
            property.add("actionId", actionId);
        }
        boolean isText = text != null && !text.isEmpty();
        property.add("e", disable == null ? toInt(isEnabled()) : 0);

        if (isText) {
            property.add("text", text);
        }

        if (textInfo != null && textInfo.length() > 0) {
            property.add("textInfo", textInfo);
        }
        
        if (isIconVisible && (iconPath != null && !iconPath.isEmpty() || iconFullPath != null && !iconFullPath.isEmpty())) {
            JsonObject img = new JsonObject();
            img.add("src", iconPath != null && !iconPath.isEmpty() ? iconPath : iconFullPath);
            property.add("img", img);
            if (iconBytes != null && iconBytes.length > 0) {
            	try {
            		img.add("bytes", new String(Base64.encodeBase64(iconBytes), "UTF-8"));
    			} catch (UnsupportedEncodingException e) {
    				e.printStackTrace();
    			}
            }
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        if (isSend) {
            sendChange(obj, isSend);
        }
        return obj;
    }
}