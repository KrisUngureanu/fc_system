package kz.tamur.web.common.webgui;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.07.2006
 * Time: 16:08:46
 */
public class WebScrollPane extends WebComponent implements JSONComponent {

	protected WebComponent viewComp;
    private static double heightCoeff = 1;// 0.78;
    private static double maxHeightCoeff = 1;// 0.92;

    public WebScrollPane(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public void setViewComponent(WebComponent viewComp) {
        if (viewComp != null)
            viewComp.setParent(this);
        this.viewComp = viewComp;
    }

    public void calculateSize() {
        super.calculateSize();
        if (viewComp != null && viewComp.isVisible())
            viewComp.calculateSize();
        if ((viewComp instanceof WebMemoField) || (viewComp instanceof WebRichTextEditor) && viewComp.isVisible()) {
            ((WebMemoField) viewComp).setSize(sWidth, sHeight);
        }
    }

    public WebComponent getWebComponent(String id) {
        if (id.equals(getId()))
            return this;
        if (viewComp != null) {
            WebComponent res = viewComp.getWebComponent(id);
            if (res != null)
                return res;
        }
        return null;
    }

    public String getAlign() {
        if (viewComp != null)
            return viewComp.getAlign();
        return align;
    }

    public String getValign() {
        if (viewComp != null)
            return viewComp.getValign();
        return valign;
    }

    public int getHeight() {
        if (prefSize != null && prefSize.height > 0) {
            return (int) (heightCoeff * prefSize.height);
        } else if (minSize != null && minSize.height > 0) {
            return (int) (heightCoeff * minSize.height);
        } else if (maxSize != null && maxSize.height > 0) {
            return (int) (heightCoeff * maxSize.height);
        } else {
            return 0;
        }
    }

    public int getMaxHeight() {
        if (maxSize != null && maxSize.height > 0) {
            return (int) (maxHeightCoeff * maxSize.height);
        } else if (prefSize != null && prefSize.height > 0) {
            return (int) (maxHeightCoeff * prefSize.height);
        } else if (minSize != null && minSize.height > 0) {
            return (int) (maxHeightCoeff * minSize.height);
        } else {
            return 0;
        }
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if ((viewComp instanceof WebMemoField) || (viewComp instanceof WebRichTextEditor) && viewComp.isVisible()) {
            return viewComp.putJSON();
        } else {
            if (viewComp != null && viewComp.isVisible()) {
                viewComp.putJSON();
            }
            if (property.size() > 0) {
                obj.add("pr", property);
            }
            sendChange(obj, isSend);
            return obj;
        }
    }

}
