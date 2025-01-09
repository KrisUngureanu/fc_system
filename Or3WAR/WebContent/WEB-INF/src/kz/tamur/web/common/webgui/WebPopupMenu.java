package kz.tamur.web.common.webgui;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import org.jdom.Element;

import java.awt.font.FontRenderContext;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 01.06.2007
 * Time: 17:59:54
 */
public class WebPopupMenu extends WebPanel implements JSONComponent {

	private int x;
    private int y;
    private int height;
    private String text;

    public WebPopupMenu(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public WebMenuItem add(WebMenuItem menuItem) {
        super.add(menuItem);
        return menuItem;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxWidth() {
        int res = 0;
        for (int i = 0; i < children.size(); i++) {
            WebMenuItem ch = (WebMenuItem) children.get(i);
            String s = ch.getText();
            Font f = new Font("Tahoma", 0, 13);
            Rectangle2D bs = f.getStringBounds(s, new FontRenderContext(null, false, false));
            int width = (int) bs.getWidth();
            if (width > res)
                res = width;
        }
        return res + 15;
    }

    public int getHeight() {
        return height;
    }

    public JsonObject getReportsJSON() {
        JsonObject obj = new JsonObject();
        JsonArray items = new JsonArray();
        for (int i = 0; i < children.size(); i++) {
            WebMenuItem ch = (WebMenuItem) children.get(i);
            ch.getReportJSON(items);
        }
        obj.add("children", items);
        return obj;
    }

    public JsonObject getReportsJSON(JsonArray arr) {
        JsonObject obj = new JsonObject();

        obj.add("name", text);
        obj.add("id", id);
        obj.add("v", isVisible ? 1 : 0);

        arr.add(obj);

        JsonArray items = new JsonArray();
        for (int i = 0; i < children.size(); i++) {
            WebMenuItem ch = (WebMenuItem) children.get(i);
            ch.getReportJSON(items);
        }
        obj.add("children", items);
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
    
	public void setText(String text) {
		this.text = text;
	}
}
