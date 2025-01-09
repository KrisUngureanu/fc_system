package kz.tamur.web.common.webgui;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.component.OrWebFloatField;
import kz.tamur.web.component.OrWebTextField;

import javax.swing.*;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 18.07.2006
 * Time: 18:46:35
 */
public class WebTextField extends WebComponent implements JSONComponent {
    private String text = "";
    private String oldText = "";

    private int charsCount = 0;
    private String charsRE = "";
    private String excludeChars = null;
    private String includeChars = null;
    protected boolean valueChanged = false;
    private int hAlign = SwingConstants.LEFT;
    private boolean upperFirstChar = false;
    private boolean upperAllChars = false;
    protected boolean alwaysFocused = false;
    protected boolean firstFocused = false;
    private int type = Constants.DEF_TYPE;
    protected boolean formatting = false;

    public WebTextField(Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        setFocusable(true);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (!oldText.equals(text)) {
            oldText = text;
            sendChangeProperty("text", text);
        }
    }

    public void setTextDirectly(String text) {
        this.text = text;
        oldText = text;
    }

    public void setValue(Object value) {
        setText(value != null ? value.toString() : "");
    }

    public Object getValue() {
        return getText();
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width, JsonObject mainJSON)
            {
        JsonObject obj = new JsonObject();
        JsonObject data = new JsonObject();
        mainJSON.add(tid, data);
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();
        data.add("st", style);
        data.add("pr", property);
        property.add("value", Funcs.xmlQuote(text));
        // JSON не используется
        // property.add("hAlign", hAlign);
        property.add("charsCount", charsCount);
        property.add("charsRE", charsRE);
        property.add("allUp", upperAllChars);
        property.add("up", upperFirstChar);
        property.add("dt", type);
        if (width > 0) {
            style.add("width", width);
        }
        return obj;
    }

    protected void setCharsLimit(int count) {
        this.charsCount = count;
        sendChangeProperty("charsCount", count);
    }

    public int getCharsLimit() {
		return charsCount;
	}

	public String getExcludeChars() {
		return excludeChars;
	}

	public String getIncludeChars() {
		return includeChars;
	}

	public void setExcludeChars(String excludeChars) {
		this.excludeChars = excludeChars;
	}

	public void setIncludeChars(String includeChars) {
		this.includeChars = includeChars;
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

    protected void setHorizontalAlignment(int alignment) {
        hAlign = alignment;
        // JSON не используется
        // sendChangeProperty("hAlign", alignment);
    }

    public boolean isUpperFirstChar() {
        return upperFirstChar;
    }

    public void setUpperFirstChar(boolean upperCase) {
        this.upperFirstChar = upperCase;
//        sendChangeProperty("up", upperCase);
    }

    public boolean isUpperAllChars() {
        return upperAllChars;
    }

    public void setUpperAllChars(boolean upperAllChars) {
        this.upperAllChars = upperAllChars;
//        sendChangeProperty("allUp", upperAllChars);
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(int type) {
        this.type = type;
        sendChangeProperty("dt", type);
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if (text != null && text.length() > 0)
        	property.add("text", text);
        
        // JSON не используется
        //property.add("hAlign", hAlign);
        //if (alwaysFocused) {
        //    property.add("alf", true);
        //}
        //if (firstFocused) {
        //    property.add("dff", true);
        //}

        if (this instanceof OrWebFloatField) {
        	boolean bitSeparation = ((OrWebFloatField) this).isBitsSeparated();
        	if (bitSeparation) {
            	property.add("bitSeparation", bitSeparation);
        	}
        }

        property.add("e", toInt(isEnabled()));
        //if (formatting) {
        //    property.add("formatting", true);
        //}

        // JSON пока не используется
        //property.add("dt", type);
        //if (type == Constants.DEF_TYPE) {
        //   property.add("allUp", upperAllChars);
        //    property.add("up", upperCase);
        //}

        //if (charsCount > 0)
        //	property.add("charsCount", charsCount);
        
        //if (charsRE != null && charsRE.length() > 0)
        //	property.add("charsRE", charsRE);

        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
    
    public JsonObject getJsonEditor() {
    	if (getCharsLimit() > 0 || getIncludeChars() != null || getExcludeChars() != null) {
    		return new JsonObject().add("type", "intfield")
    				.add("options", new JsonObject().add("maxlength", getCharsLimit())
    						.add("include", getIncludeChars() != null ? getIncludeChars() : "")
    						.add("exclude", getExcludeChars() != null ? getExcludeChars() : ""));
    	} else {
    		return new JsonObject().add("type", "text").add("options", new JsonObject().add("upperAllChars", upperAllChars).add("upperCase", upperFirstChar));
    	}
    }
}