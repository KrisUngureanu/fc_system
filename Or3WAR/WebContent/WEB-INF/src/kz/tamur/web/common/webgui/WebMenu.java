package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 01.06.2007
 * Time: 18:14:50
 * To change this template use File | Settings | File Templates.
 */
public class WebMenu extends WebMenuItem {
    private WebPopupMenu popupMenu;
    
    public WebMenu(String name, String nameKz, Element xml, int mode, OrFrame frame, String id) {
        super(name, nameKz, xml, mode, frame, id);
        popupMenu = new WebPopupMenu(null, mode, frame, null);
        popupMenu.setText(name);
    }

    public void add(WebMenuItem menu) {
        popupMenu.add(menu);
    }

    public WebPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setId(String id) {
        popupMenu.setId(id);
    }

    public String getId() {
        return popupMenu.getId();
    }

	public WebComponent getWebComponent(String id) {
        if (id.equals(getId()))
            return this;
        return popupMenu.getWebComponent(id);
    }
	
    public JsonObject getReportJSON(JsonArray arr) {
        return popupMenu.getReportsJSON(arr);
    }

    public void setVisible(boolean visible) {
    	popupMenu.setVisible(visible);
    }
    
    public void setVisible(boolean visible, boolean fire) {
    	popupMenu.setVisible(visible, fire);
    }
}
