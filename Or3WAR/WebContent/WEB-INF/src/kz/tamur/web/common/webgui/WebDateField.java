package kz.tamur.web.common.webgui;

import static kz.tamur.comps.Constants.DD_MM;
import static kz.tamur.comps.Constants.DD_MM_YYYY;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS_SSS;
import static kz.tamur.comps.Constants.HH_MM;
import static kz.tamur.comps.Constants.HH_MM_SS;

import java.sql.Time;
import java.text.ParseException;
import java.util.ResourceBundle;

import org.jdom.Element;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.web.common.JSONComponent;

import com.cifs.or2.kernel.KrnDate;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 18.07.2006
 * Time: 19:03:14
 * To change this template use File | Settings | File Templates.
 */
public class WebDateField extends WebTextField implements JSONComponent {

	public static final String MASK_TABLE = "дд.мм.гггг";

    public String MASK_ = "дд.мм.гггг";
    public String MASK_1 = "дд.мм.гггг чч:ММ";
    public String MASK_2 = "дд.мм.гггг чч:ММ:сс";
    public String MASK_3 = "дд.мм.гггг чч:ММ:сс:ССС";
    public String MASK_4 = "чч:ММ:сс";
    public String MASK_5 = "чч:ММ";
    public String MASK_6 = "дд.мм";
    private String charD = "д";
    private String charM = "м";
    private String charG = "г";
    private String charCH = "ч";
    private String charMM = "М";
    private String charS = "с";
    private String charSS = "С";

    protected int dateFormat;

    public WebDateField(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    protected void changeTitles(ResourceBundle res) {
        setTextDirectly(getText().replaceAll(charD, res.getString("charD")).replaceAll(charM, res.getString("charM"))
                .replaceAll(charG, res.getString("charG")).replaceAll(charCH, res.getString("charCH"))
                .replaceAll(charMM, res.getString("charMM")).replaceAll(charS, res.getString("charS"))
                .replaceAll(charSS, res.getString("charSS")));

        MASK_ = res.getString("mask");
        MASK_1 = res.getString("mask1");
        MASK_2 = res.getString("mask2");
        MASK_3 = res.getString("mask3");
        MASK_4 = res.getString("mask4");
        MASK_5 = res.getString("mask5");
        MASK_6 = res.getString("mask6");
        charD = res.getString("charD");
        charM = res.getString("charM");
        charG = res.getString("charG");
        charCH = res.getString("charCH");
        charMM = res.getString("charMM");
        charS = res.getString("charS");
        charSS = res.getString("charSS");
    }

    public Object getValue() {
        String str = getText();
        java.util.Date res = null;
        try {
            if (str != null && str.length() > 0) {
                switch (dateFormat) {
                case DD_MM_YYYY:
                    if (!(str.contains(charD) || str.contains(charM) || str.contains(charG))) {
                        try {
                            res = getDateFormat2(dateFormat).parse(str);
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case DD_MM_YYYY_HH_MM:
                    if (!(str.contains(charD) || str.contains(charM) || str.contains(charG) || str.contains(charCH) || str
                            .contains(charMM))) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case DD_MM_YYYY_HH_MM_SS:
                    if (!(str.contains(charD) || str.contains(charM) || str.contains(charG) || str.contains(charCH)
                            || str.contains(charMM) || str.contains(charS))) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case DD_MM_YYYY_HH_MM_SS_SSS:
                    if (!(str.contains(charD) || str.contains(charM) || str.contains(charG) || str.contains(charCH)
                            || str.contains(charMM) || str.contains(charS) || str.contains(charSS))) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case HH_MM_SS:
                    if (!(str.contains(charCH) || str.contains(charMM) || str.contains(charS))) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case HH_MM:
                    if (!(str.contains(charCH) || str.contains(charMM))) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                case DD_MM:
                    if (!(str.contains(charD) || str.contains(charM) )) {
                        try {
                            res = new Time(getDateFormat2(dateFormat).parse(str).getTime());
                        } catch (ParseException e) {
                            getLog().error(e, e);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
        return res == null ? null : new KrnDate(res.getTime());
    }

    public void setValue(Object value) {
        setText(toString(value));
    }

    public String toString(Object value) {
        switch (dateFormat) {
        case DD_MM_YYYY:
            return value == null ? MASK_ : getDateFormat2(dateFormat).format(value);
        case DD_MM_YYYY_HH_MM:
            return value == null ? MASK_1 : getDateFormat2(dateFormat).format(value);
        case DD_MM_YYYY_HH_MM_SS:
            return value == null ? MASK_2 : getDateFormat2(dateFormat).format(value);
        case DD_MM_YYYY_HH_MM_SS_SSS:
            return value == null ? MASK_3 : getDateFormat2(dateFormat).format(value);
        case HH_MM_SS:
            return value == null ? MASK_4 : getDateFormat2(dateFormat).format(value);
        case HH_MM:
            return value == null ? MASK_5 : getDateFormat2(dateFormat).format(value);
        case DD_MM:
            return value == null ? MASK_6 : getDateFormat2(dateFormat).format(value);
        default:
            return "";
        }
    }

    public int getDateFormat() {
        return dateFormat;
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width, JsonObject mainJSON)
            {
        JsonObject data = new JsonObject();
        mainJSON.add(tid, data);
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();

        property.add("col", col);
        property.add("row", row);
        property.add("e", toInt(isEnabled()));

        property.add("value", toString(value));
        property.add("data-date-format", getDateFormatHTML());
        if (width > 0) {
            style.add("width", width);
        }
        if (style.size() > 0) {
            data.add("st", style);
        }
        if (property.size() > 0) {
            data.add("pr", property);
        }
        return data;
    }

    public void getCellEditor(Object value, int row, int col, String tid, int width, StringBuilder b) {
        String text = toString(value);
        b.append("<r><id>").append(tid).append("</id>");
        b.append("<row>").append(row).append("</row>");
        b.append("<col>").append(col).append("</col>");
        b.append("<editor><input ");
        if (isEnabled()) {
            b.append("class=\"dpick\" editor=\"1\" ");
        }
        b.append("type='text' value=\"").append(text).append("\"");
        b.append(" data-date-format='").append(getDateFormatHTML()).append("' ");
        if (width > 0) {
            b.append(" style=\"width: ").append(width).append(";\"");
        }
        b.append(" onChange=\"textChanged(this, '");
        b.append(tid);
        b.append("', ");
        b.append(row);
        b.append(", ");
        b.append(col);
        b.append(");\"/></editor></r>");
    }

    private static ThreadLocalDateFormat getDateFormat2(int format) {
        switch (format) {
        case DD_MM_YYYY:
            return ThreadLocalDateFormat.get("dd.MM.yyyy");
        case DD_MM_YYYY_HH_MM:
            return ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm");
        case DD_MM_YYYY_HH_MM_SS:
            return ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss");
        case DD_MM_YYYY_HH_MM_SS_SSS:
            return ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss:SSS");
        case HH_MM_SS:
            return ThreadLocalDateFormat.get("HH:mm:ss");
        case HH_MM:
            return ThreadLocalDateFormat.get("HH:mm");
        case DD_MM:
            return ThreadLocalDateFormat.get("dd.MM");
        }
        return null;
    }

    public String getDateFormatHTML() {
        switch (dateFormat) {
        default:
        case DD_MM_YYYY:
            return "dd.mm.yyyy";
        case DD_MM_YYYY_HH_MM:
            return "dd.mm.yyyy HH:nn";
        case DD_MM_YYYY_HH_MM_SS:
            return "dd.mm.yyyy HH:nn:ss";
        case DD_MM_YYYY_HH_MM_SS_SSS:
            return "dd.mm.yyyy HH:nn:ss:SSS";
        case HH_MM_SS:
            return "HH:nn:ss";
        case HH_MM:
            return "HH:nn";
        case DD_MM:
            return "dd.mm";
        }
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        property.add("value", getText());
        property.add("data-date-format", getDateFormatHTML());
        property.add("e", toInt(isEnabled()));

        obj.add("pr", property);
        sendChange(obj, isSend);
        return obj;
    }

    public JsonObject getJsonEditor() {
    	int format = getDateFormat();
    	String editor = "datebox";
    	if (format == Constants.DD_MM_YYYY)
			editor = "datebox";
		else if (format == Constants.HH_MM)
			editor = "hhmmEditor";
		else if (format == Constants.HH_MM_SS)
			editor = "hhmmssEditor";
		else if (format == Constants.DD_MM_YYYY_HH_MM)
			editor = "datehhmmEditor";
		else if (format == Constants.DD_MM_YYYY_HH_MM_SS)
			editor = "datehhmmssEditor";
		else if (format == Constants.DD_MM_YYYY_HH_MM_SS_SSS)
			editor = "datehhmmssSSSEditor";
		else if (format == Constants.DD_MM)
			editor = "ddmmEditor";
    	
        return new JsonObject().add("type", editor);
    }
}
