package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.07.2006
 * Time: 16:48:52
 */
public class WebSplitPane extends WebComponent implements JSONComponent {

	protected WebComponent leftComp;
    protected WebComponent rightComp;

    protected int orientation = 0;
    protected double dividerLocation = 0.5;

    public WebSplitPane(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public void setLeftComponent(WebComponent leftComp) {
        if (leftComp != null)
            leftComp.setParent(this);
        this.leftComp = leftComp;
    }

    public void setRightComponent(WebComponent rightComp) {
        if (rightComp != null)
            rightComp.setParent(this);
        this.rightComp = rightComp;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void calculateSize() {
        super.calculateSize();
        if (leftComp != null && leftComp.isVisible())
            leftComp.calculateSize();
        if (rightComp != null && rightComp.isVisible())
            rightComp.calculateSize();
    }

    public WebComponent getWebComponent(String id) {
        if (id.equals(getId()))
            return this;
        if (leftComp != null) {
            WebComponent res = leftComp.getWebComponent(id);
            if (res != null)
                return res;
        }
        if (rightComp != null) {
            WebComponent res = rightComp.getWebComponent(id);
            if (res != null)
                return res;
        }
        return null;
    }

    public void setDividerLocation(double dividerLocation) {
        this.dividerLocation = dividerLocation * 0.91;
    }

    public WebComponent[] getComponents() {
        return new WebComponent[] { leftComp, rightComp };
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("orient", orientation);
        String leftWidth = String.valueOf((int) (dividerLocation * 100));
        String rightWidth = String.valueOf((int) (100 - dividerLocation * 100));
        property.add("leftWidth", leftWidth);
        property.add("rightWidth", rightWidth);

        if (leftComp != null && leftComp.isVisible()) {
        	leftComp.putJSON();
        }

        if (rightComp != null && rightComp.isVisible()) {
        	rightComp.putJSON();
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}
