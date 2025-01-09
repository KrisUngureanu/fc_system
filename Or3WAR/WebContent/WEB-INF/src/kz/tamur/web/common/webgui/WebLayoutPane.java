package kz.tamur.web.common.webgui;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.ServletUtilities;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.07.2006
 * Time: 16:48:52
 */
public class WebLayoutPane extends WebComponent implements JSONComponent {

	protected Map<String, WebComponent> comps = new HashMap<String, WebComponent>();

    public WebLayoutPane(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public void add(WebComponent comp, String pos) {
    	comps.put(pos, comp);
    }

    public WebComponent getWebComponent(String id) {
        if (id.equals(this.id)) {
            return this;
        }
        for (WebComponent comp : comps.values()) {
        	WebComponent res = comp.getWebComponent(id);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        for (WebComponent comp : comps.values()) {
        	comp.putJSON(isSend);
        }

        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}
