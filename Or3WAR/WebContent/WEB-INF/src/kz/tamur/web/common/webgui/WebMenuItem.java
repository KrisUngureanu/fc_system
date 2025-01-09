package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 01.06.2007
 * Time: 18:15:00
 * To change this template use File | Settings | File Templates.
 */
public class WebMenuItem extends WebButton {
    private String nameKz;
    private String lang;

    public WebMenuItem(String name, String nameKz, Element xml, int mode, OrFrame frame, String id) {
        super("", xml, mode, frame, id);
        setText(name);
        
        this.nameKz = nameKz;
    }
    
    public WebMenuItem(String name, String nameKz, Element xml, int mode, OrFrame frame, String id, String lang) {
        this(name, nameKz, xml, mode, frame, id);
        this.lang = lang;
    }

    public String getNameKz() {
		return nameKz;
	}
    
    public JsonObject getReportJSON(JsonArray items) {
        JsonObject obj = new JsonObject();

        obj.add("name", text);
        obj.add("id", id);
        if (lang != null)
        	obj.add("lang", lang);

        obj.add("v", isVisible ? 1 : 0);

        items.add(obj);
        return obj;
    }

    public void setVisible(boolean visible) {
    	if (this.isVisible != visible)
            ((WebPanel)frame.getPanel()).sendChangeProperty("rv." + getId(), visible ? 1 : 0);
    	
        this.isVisible = visible;
    }

    public void setVisible(boolean visible, boolean fire) {
    	if (fire && this.isVisible != visible)
            sendChangeProperty("v." + getId(), visible ? 1 : 0);
    	
        this.isVisible = visible;
    }
}
