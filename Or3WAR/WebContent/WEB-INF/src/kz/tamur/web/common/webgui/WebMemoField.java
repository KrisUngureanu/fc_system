package kz.tamur.web.common.webgui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.jdom.Element;

import kz.tamur.comps.OrFrame;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 18.07.2006
 * Time: 18:46:35
 */
public class WebMemoField extends WebComponent implements JSONComponent {

	private String text = "";
    private String oldText = "";

    private int charsCount = 0;
    private String charsRE = "";
    private boolean valueChanged = false;
    protected boolean wysiwyg = false;

    public WebMemoField(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public String getText() {
        text = text.replace("\\n", "\n");
        text = text.replace("\u0000", "");
        return text;
    }

    public void setText(String text) {
        text = text.replace("\\n", "\n");
        text = text.replace("\u0000", "");
        this.text = text;
        if (!oldText.equals(text)) {
            valueChanged = true;
            oldText = text;
            sendChangeProperty("text", text);
        }
    }

    public void setTextDirectly(String text) {
        this.text = text;
        oldText = text;
    }

    public void setValue(Object value) {
    	String val = null;
    	if (value instanceof byte[]) {
    		try {
				val = new String((byte[])value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
    	} else if (value instanceof File) {
    		try {
    			byte[] b = Funcs.read((File)value);
				val = new String(b, "UTF-8");
			} catch (Exception e) {
			}
    	} else if (value instanceof String)
    		val = (String)value;
    	
        setText(val != null ? val.toString() : "");
    }

    public Object getValue() {
        return getText();
    }

    public void setSize(String sWidth, String sHeight) {
        this.sWidth = sWidth;
        this.sHeight = sHeight;
        JsonObject size = new JsonObject();
        size.add("width", sWidth);
        size.add("height", sHeight);
        sendChangeProperty("size",size);
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width) {
        JsonObject obj = new JsonObject();
        obj.add("uuid", tid);
        obj.add("col", col);
        obj.add("row", row);
        String text = (value != null) ? value.toString() : "";
        obj.add("value", text);
        if (width > 0) {
            JsonObject style = new JsonObject();
            style.add("width", width);
            obj.add("st", style);
        }
        StringBuilder tmp = new StringBuilder();
        tmp.append("return controlText(this, event, ").append(charsCount).append(", '").append(charsRE).append("');");
        obj.add("onKeyPress", tmp);
        tmp = new StringBuilder();
        tmp.append("textChanged(this, '").append(tid).append("', ").append(row).append(", ").append(col).append(");");
        obj.add("onBlur", tmp);
        return obj;
    }

    protected void setCharsLimit(int count) {
        this.charsCount = count;
    }

    protected void setExcludeIncludeChars(String excludeChars, String includeChars) {
        String specSymbols = "\\()?:=![]*+.{}-,^$|/";
        StringBuilder res = new StringBuilder();
        if (includeChars != null && includeChars.length() > 0) {
            res.append("/[");
            StringTokenizer st = new StringTokenizer(includeChars, ";");
            while (st.hasMoreTokens()) {
                String str = st.nextToken();
                if (specSymbols.indexOf(str) > -1)
                    res.append("\\").append(str);
                else if (str.length() == 2)
                    res.append(str.charAt(0)).append("-").append(str.charAt(1));
                else
                    res.append(str);
            }
            res.append("]/");
        }

        if (excludeChars != null && excludeChars.length() > 0) {
            res = new StringBuilder();
            res.append("/[^");
            StringTokenizer st = new StringTokenizer(excludeChars, ";");
            while (st.hasMoreTokens()) {
                String str = st.nextToken();
                if (specSymbols.indexOf(str) > -1)
                    res.append("\\").append(str);
                else if (str.length() == 2)
                    res.append(str.charAt(0)).append("-").append(str.charAt(1));
                else
                    res.append(str);
            }
            res.append("]/");
        }

        charsRE = Funcs.xmlQuote(res.toString());
        charsRE = charsRE.replaceAll("\\\\", "\\\\\\\\");
        charsRE = charsRE.replaceAll("&#39;", "\\\\\\\'");
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        
        text = text.replace("\\n", "\n");
        text = text.replace("\u0000", "");
        
        property.add("text", text);
        property.add("tt", tooltipText);
        
        if (tooltipText.length() > 0) {
            property.add("tt", tooltipText);
        }

        if (wysiwyg) {
            property.add("wysiwyg", wysiwyg);
        }
        property.add("e", toInt(isEnabled()));
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }

    public JsonObject getJsonEditor() {
        return new JsonObject().add("type", "textarea");
    }
}