package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;

public class WebPasswordField extends WebTextField implements JSONComponent {

    public WebPasswordField(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width, JsonObject mainJSON)
            {
        JsonObject obj = new JsonObject();
        JsonObject data = new JsonObject();
        mainJSON.add(tid, data);
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();
        data.add("pr", property);

        property.add("col", col);
        property.add("row", row);
        property.add("e", toInt(isEnabled()));

        String text = (value != null) ? value.toString() : "";
        property.add("value", Funcs.xmlQuote(text));

        if (width > 0) {
            style.add("width", width);
        }
        if (style.size() > 0) {
            obj.add("st", style);
        }
        return obj;
    }

    @Override
     public JsonObject putJSON(boolean isSend) {
         JsonObject obj = addJSON();
         JsonObject action = new JsonObject();
         JsonObject property = new JsonObject();

         if (tooltipText.length() > 0) {
             property.add("tt", Funcs.xmlQuote2(tooltipText));
         }
         property.add("e", toInt(isEnabled()));     
         if (action.size() > 0) {
             obj.add("on", action);
         }
         if (property.size() > 0) {
             obj.add("pr", property);
         }
         sendChange(obj, isSend);
         return obj;
     }
}
