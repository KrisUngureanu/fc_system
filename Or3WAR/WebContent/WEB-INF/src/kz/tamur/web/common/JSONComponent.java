package kz.tamur.web.common;

import com.eclipsesource.json.JsonObject;

public interface JSONComponent {
    
    public JsonObject putJSON();

    JsonObject putJSON(boolean isSend);

    JsonObject getJSON();

}
