package kz.tamur.web.common.webgui;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 19.05.2007
 * Time: 11:52:08
 * To change this template use File | Settings | File Templates.
 */
public class WebButtonGroup {
    private Vector<WebButton> buttons = new Vector<WebButton>();
    private WebButton selectedButton;

    public void setSelected(WebButton button) {
        selectedButton = button;
        for (int i=0; i < buttons.size(); i++) {
            WebButton b = buttons.get(i);
            if (b != button)
                b.setSelected(false);
        }
    }

    public void add(WebButton button) {
        buttons.add(button);
        button.setBtnGroup(this);
    }

    public Enumeration<WebButton> getElements() {
        return buttons.elements();
    }

    public void remove(WebButton button) {
        buttons.remove(button);
    }

    public WebButton getSelection() {
        return selectedButton;
    }
}
