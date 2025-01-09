package kz.tamur.web.common.webgui;

import com.eclipsesource.json.JsonObject;

import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.component.OrWebRadioBox;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 19.05.2007
 * Time: 11:49:36
 * To change this template use File | Settings | File Templates.
 */
public class WebRadioButton extends WebButton implements JSONComponent {
    private OrWebRadioBox box;
    private int number;

    public WebRadioButton(String name, OrWebRadioBox box, int number) {
        super("OrRadioButton", null, box.getMode(), box.frame, null);
        setText(name);
        this.box = box;
        this.number = number;
        setEnabled(true);
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();
        property.add("value", number);
        property.add("name", "radio" + box.getId());
        property.add("uuid", box.getId() + "_" + number);
        property.add("text", getText());
        property.add("e", toInt(isEnabled()));
        property.add("v", toInt(isVisible()));
        style.add("color", box.getForeground());
        style.add("font", box.getFont().getName());
        style.add("fontSize", box.getFont().getSize());
        style.add("fontStyle", box.getFont().getStyle());
        if (isSelected()) {
            property.add("checked", isSelected());
        }
        if (tooltipText != null) {
            property.add("tt", Funcs.xmlQuote2(tooltipText));
        }
        obj.add("st", style);
        obj.add("pr", property);
        sendChange(obj, isSend);
        return obj;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
}
