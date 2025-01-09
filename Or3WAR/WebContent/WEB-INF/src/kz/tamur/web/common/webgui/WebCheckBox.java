package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;

/**
 * Date: 18.07.2006
 * Time: 18:46:35
 */
public class WebCheckBox extends WebComponent implements JSONComponent {
    private boolean valueChanged = false;
    private boolean selected = false;
    private boolean oldSelected = false;
    private String text;

    public WebCheckBox(String name, boolean selected, Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        this.selected = selected;
        this.oldSelected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (oldSelected != selected) {
            valueChanged = true;
            oldSelected = selected;
            sendChangeProperty("checked", selected);
        }
    }

    public void setSelectedDirectly(boolean selected) {
        this.selected = selected;
        oldSelected = selected;
    }

    public void setText(String text) {
    	if (text == null) text = "";
    	if (!text.equals(this.text)) {
    		this.text = text;
    		sendChangeProperty("text", text);
    	}
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("checked", selected);
        property.add("e", toInt(isEnabled()));
        if (text != null && text.trim().length() > 0) {
            property.add("text", text);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
    
    public JsonObject getJsonEditor() {
        return new JsonObject().add("type", "checkbox").add("options", new JsonObject().add("on", "x").add("off", ""));
    }
}
