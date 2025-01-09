package kz.tamur.web.common;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

public class WebUtils {
	
	public static final String LIGHT_SYS_COLOR = colorToString(Constants.LIGHT_SYS_COLOR);
	public static final String DEFAULT_FONT_COLOR = colorToString(Constants.DEFAULT_FONT_COLOR);

    public static String colorToString(Color bg) {
        if (bg != null) {
            return String.format("#%02x%02x%02x", bg.getRed(), bg.getGreen(), bg.getBlue());
        }
        return "";
    }

    public static String orientationToString(int orientation) {
        switch (orientation) {
        case Constants.HORIZONTAL:
            return "left";
        case Constants.VERTICAL:
            return "top";
        case Constants.DIAGONAL2:
            return "top left";
        case Constants.DIAGONAL:
            return "bottom left";
        }
        return "";
    }

    public static String gradientToString(GradientColor g) {
        final String or = orientationToString(g.getOrientation());
        final String startColor = colorToString(g.getStartColor());
        final String endColor = colorToString(g.getEndColor());
        final int beg = g.getPositionStartColor();
        final int end = g.getPositionEndColor();
        final String sp = " ";
        final String prc = "%,";

        int delta = end - beg;
        int t = beg;
        int k = 0;
        String res = "";

        if (g.isCycle()) {
            if (delta > 0) {
                res = startColor + sp + t + prc;
                while (t > 0) {
                    t -= delta;
                    k--;
                    res = ((k % 2 == 0) ? startColor : endColor) + sp + t + prc + res;
                }
                t = beg;
                k = 0;
                while (t < 100) {
                    k++;
                    t += delta;
                    res += ((k % 2 == 0) ? startColor : endColor) + sp + t + prc;
                }
            } else if (delta < 0) {
                res = startColor + sp + t + prc;
                while (t < 100) {
                    t -= delta;
                    k--;
                    res += ((k % 2 == 0) ? startColor : endColor) + sp + t + prc;
                }
                t = beg;
                k = 0;
                while (t > 0) {
                    k++;
                    t += delta;
                    res = ((k % 2 == 0) ? startColor : endColor) + sp + t + prc + res;
                }
            } else {
                res = startColor;
            }
        } else {
            res = startColor + sp + beg + prc + endColor + sp + end + "%";
        }

        return or + ", " + res.replaceFirst("%,$", "%");
    }

    public static String getColorState(int state) {
        if (state == Constants.REQ_ERROR) {
            return " background-color: #FFCCCC; ";
        }
        if (state == Constants.EXPR_ERROR) {
            return " background-color: #CAF7BB; ";
        }
        return null;
    }

    public static void getColorState(int state, StringBuilder sb) {
        String c = getColorState(state);
        if (c != null)
            sb.append(c);
    }

    public static void getColorState(int state, JsonObject obj) {
        if (state == Constants.REQ_ERROR) {
            obj.add("backgroundColor", "#FFCCCC");
        } else if (state == Constants.EXPR_ERROR) {
            obj.add("backgroundColor", "#CAF7BB");
        }
    }

    public static String getColorStyleState(int state) {
        if (state == Constants.REQ_ERROR) {
            return " style='background-color: #FFCCCC;' ";
        }
        if (state == Constants.EXPR_ERROR) {
            return " style='background-color: #CAF7BB;' ";
        }
        return null;
    }

    /**
     * Получить color style state.
     * 
     * @param state
     *            the state
     * @param sb
     *            the sb
     * @return the color style state
     */
    public static void getColorStyleState(int state, StringBuilder sb) {
        String c = getColorStyleState(state);
        if (c != null)
            sb.append(c);
    }

    public static void appendFontStyle(Font font, JsonObject style) {
        if (font != null) {
            if (!"dialog".equalsIgnoreCase(font.getName()))
                style.add("ff", font.getName());
            if (font.getSize() > 0)
                style.add("fs", font.getSize());
            if (font.isBold())
                style.add("fw", "bold");
            if (font.isItalic())
                style.add("fst", "italic");
        }
    }
    
    public static void includeResponse(HttpServletRequest request, HttpServletResponse response, String path, long max) {
    	String url = null;
    	if (path.startsWith("http"))
    		url = Funcs.validate(path);
    	else {
    		url = request.getScheme() + "://" + request.getServerName()  
    				+ ((("http".equals(request.getScheme()) && request.getLocalPort() == 80)
    						|| ("https".equals(request.getScheme()) && request.getLocalPort() == 443))
    						? "" : (":" + request.getLocalPort())) + request.getContextPath();
    		url = Funcs.validate(url);
	    			
        	if (path.startsWith("/jsp"))
    			url += "/jsp" + path.substring(4);
    		else
    			url += "/jsp" + path;
    	}

    	HttpURLConnection urlConnection = null;
        InputStream is = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            is = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            if (contentType != null)
            	response.setContentType(contentType);
            int length = urlConnection.getContentLength();
            if (length > 0)
            	response.setContentLength(length);
            Funcs.writeStream(is, response.getOutputStream(), max);
        } catch (IOException io) {
        	Utils.closeQuietly(is);
        	Utils.closeQuietly(urlConnection);
        }
    }
}
