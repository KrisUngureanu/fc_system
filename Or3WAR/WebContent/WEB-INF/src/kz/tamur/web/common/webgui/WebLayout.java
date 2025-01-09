package kz.tamur.web.common.webgui;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 15.07.2006
 * Time: 11:27:40
 */
public interface WebLayout {
    public void layoutPanel(WebPanel panel);
    JsonObject getJSON(WebPanel panel, boolean isChange);
}
