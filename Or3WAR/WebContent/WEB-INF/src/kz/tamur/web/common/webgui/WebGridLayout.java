package kz.tamur.web.common.webgui;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 19.05.2007
 * Time: 12:20:19
 */
public class WebGridLayout implements WebLayout {
    private int rows;
    private int cols;

    public WebGridLayout(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public void layoutPanel(WebPanel panel) {
    }

    public JsonObject getJSON(WebPanel panel, boolean isChange) {
        JsonObject obj = new JsonObject();
        JsonObject property = new JsonObject();
        if (panel.getParent() instanceof WebPanel) {
            if (panel.getValign() != null && panel.getValign().length() > 0) {
                property.add("valign", panel.getValign());
            }
        }

        if (panel.borderTitle != null && panel.borderTitle.length() > 0 && !isChange) {
            panel.getBorderHTML(property);
        }

        if (panel.getAlign() != null && panel.getAlign().length() > 0) {
            property.add("align", panel.getAlign());
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                WebComponent comp = panel.getComponent(i * cols + j);
                if (comp != null) {
                    comp.putJSON();
                }
            }
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        return obj;
    }
}
