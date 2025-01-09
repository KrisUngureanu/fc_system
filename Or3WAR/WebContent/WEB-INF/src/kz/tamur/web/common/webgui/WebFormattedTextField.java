package kz.tamur.web.common.webgui;

import javax.swing.*;

import org.jdom.Element;

import kz.tamur.comps.OrFrame;

import java.text.ParseException;

public class WebFormattedTextField extends WebTextField {
    JFormattedTextField.AbstractFormatter formatter;

    public WebFormattedTextField(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
    }

    public Object getValue() {
        String s = getText();
        if (s.length() == 0)
            return null;
        else {
            try {
                return formatter.stringToValue(s);
            } catch (ParseException pe) {
                return null;
            }
        }
    }

    public void setValue(Object value) {
        try {
            setText(formatter.valueToString(value));
        } catch (ParseException pe) {
            setText("");
        }
    }

    public void setValueDirectly(Object value) {
        try {
            setTextDirectly(formatter.valueToString(value));
        } catch (ParseException pe) {
            setTextDirectly("");
        }
    }

    public Object transform(String value) {
        Object val = null;
        try {
            val = formatter.stringToValue(value);
        } catch (ParseException pe) {
        }
        return val;
    }

    public void setFormatter(JFormattedTextField.AbstractFormatter formatter) {
        this.formatter = formatter;
    }

    public String getValueAsText(Object value) {
        String val = "";
        try {
            val = formatter.valueToString(value);
        } catch (ParseException pe) {
        }
        return val;
    }
}
