package kz.tamur.web.common.webgui;

import kz.tamur.comps.OrFrame;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.component.OrWebLabel;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import org.jdom.Element;

import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 18.07.2006
 * Time: 18:30:32
 */
public class WebLabel extends WebComponent implements JSONComponent {

    /** Тест метки. */
    private String text;

    /** Горизонтальное позиционирование текста. */
    private int hAlign;

    /** Максимальная ширина. */
    private int labelMaxWidth = -1;

    /** Картинка компонента. */
    private String iconName;

    /**
     * Конструктор новой метки.
     */
    public WebLabel(Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
    }

    /**
     * Конструктор новой метки.
     * 
     * @param text
     *            текст метки
     */
    public WebLabel(String text, Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        this.text = text;
    }

    /**
     * Получить текст метки.
     * 
     * @return текст метки
     */
    public String getText() {
        return text;
    }

    /**
     * Установить текст метки.
     * 
     * @param text
     *            новый текст метки
     */
    public void setText(String text) {
        this.text = text;
        this.labelMaxWidth = -1;
        sendChangeProperty("text", this.text);
    }

    /**
     * Установить горизонтальное позиционирование текста.
     * 
     * @param a
     *            the new horizontal alignment
     */
    public void setHorizontalAlignment(int a) {
        hAlign = a;
        // JSON не используется
        // sendChangeProperty("hAlign", a);
    }

    public int getMaxWidth() {
        if (labelMaxWidth == -1) {
            int res = 0;
            if (text != null) {
                Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
                String text = this.text.replaceAll("@", "\n");
                Rectangle2D bs = f.getStringBounds(text, new FontRenderContext(null, false, false));
                int width = (int) bs.getWidth();
                if (width > res) {
                    res = width;
                }
            }
            labelMaxWidth = res + 15;
        }
        return labelMaxWidth;
    }

    /**
     * Установить имя иконки.
     * 
     * @param iconPath
     *            новое имя иконки
     */
    public void setIconName(String iconName) {
        this.iconName = iconName;
        sendChangeProperty("iconName", iconName);
    }

    /**
     * Получить имя иконки
     * 
     * @return имя иконки
     */
    public String getIconName() {
        return iconName;
    }

    @Override
    public int getEstimatedWidth() {
        int res = 0;
        if (text != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = this.text.replaceAll("@", "\n");
            Rectangle2D bs = f.getStringBounds(text, new FontRenderContext(null, false, false));
            int width = (int) bs.getWidth();
            if (width > res) {
                res = width;
            }
        }
        return res + 5;
    }

    @Override
    public int getEstimatedHeight() {
        int res = 0;
        if (text != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = this.text.replaceAll("@", "\n");
            Rectangle2D bs = f.getStringBounds(text, new FontRenderContext(null, false, false));
            int height = (int) bs.getHeight();
            if (height > res) {
                res = height;
            }
        }
        return res + 5;
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if (!(this instanceof OrWebLabel)) property.add("text", text);
        if (iconName != null && !iconName.isEmpty()) {
            JsonObject img = new JsonObject();
            img.add("src", iconName);
            property.add("img", img);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
        return obj;
    }
}