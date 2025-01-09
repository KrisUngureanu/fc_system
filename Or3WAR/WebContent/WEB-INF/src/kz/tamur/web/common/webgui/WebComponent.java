package kz.tamur.web.common.webgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.common.WebUtils;
import kz.tamur.web.component.OrWebLabel;
import kz.tamur.web.component.OrWebTableNavigator;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.07.2006
 * Time: 16:38:15
 * To change this template use File | Settings | File Templates.
 */
public class WebComponent implements JSONComponent, JSONCellComponent {
    protected Dimension maxSize;
    protected Dimension minSize;
    protected Dimension prefSize;
    protected GridBagConstraints constraints;
    protected String bg;
    protected String fg;
    protected WebComponent parent;
    protected String cellWidth;
    protected String cellHeight;
    protected String sWidth;
    protected String sHeight;
    protected String minWidth = null;
    protected String minHeight = null;
    protected String maxWidth = null;
    protected String maxHeight = null;
    protected String align;
    protected String valign;
    protected String id;
    public String uuid;
    private String html;
    private boolean isEnabled = true;
    protected boolean enableChanged = false;
    protected boolean stateChanged = false;
    protected String alertMessage;
    protected boolean isVisible = true;
    protected boolean childVisibleChanged = false;
    private static final int approximateScreenWidth = 2000;
    private static final int approximateScreenHeight = 1200;

    private Insets insets;
    protected String tooltipText = "";
    protected Font font;
    private String fontCSS;

    protected int state = 0, oldState = 0;
    private boolean focusable = false;
    protected String varName;
    private boolean opaque = true;
    /**
     * Имя компонента. Аналог {@link java.awt.Component#name}
     * 
     * @see #getName
     * @see #setName(String)
     */
    private String name;
    protected GradientColor gradientColor;

    protected WebSession session;
    protected int mode;
    protected Element xml;
    protected byte[] description;
    protected String descriptionUID;
    protected OrFrame frame;
	protected boolean needToPutJSON = true;

	protected Log log;
	
	public WebComponent(Element xml, int mode, OrFrame frame, String id) {
    	this.id = Funcs.normalizeInput(id);
        this.mode = mode;
        this.frame = frame;
        this.xml = xml;
        if (this instanceof OrGuiComponent)
        	((WebFrame)frame).setSelectedComponent((OrGuiComponent)this);
        
        log = getLog();
	}

    public void setPreferredSize(Dimension sz) {
        prefSize = sz;
    }

    public void setMinimumSize(Dimension sz) {
        minSize = sz;
    }

    public void setMaximumSize(Dimension sz) {
        maxSize = sz;
    }

    public void setAllSize(Dimension sz) {
        setPreferredSize(sz);
        setMinimumSize(sz);
        setMaximumSize(sz);
    }

    public void setConstraints(GridBagConstraints cs) {
        constraints = cs;
    }

    public void setConstraintsIndent(Insets indent) {
        if (constraints != null && indent != null) {
            constraints.insets.top = indent.top;
            constraints.insets.left = indent.left;
            constraints.insets.bottom = indent.bottom;
            constraints.insets.right = indent.right;
        }
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }

    public String getBackground() {
        return bg;
    }

    public void setBackground(String bg) {
        this.bg = Funcs.normalizeInput(bg);
    }

    public void setBackground(Color color) {
        bg = color == null ? null : Utils.colorToString(color);
        if (bg != null)
        	sendChangeProperty("bg", bg);
   }

    public void setForeground(Color color) {
        fg = color == null ? null : Utils.colorToString(color);
        if (fg != null)
        	sendChangeProperty("fg", fg);
    }

    public String getForeground() {
        return fg;
    }

    public void setForeground(String fg) {
        this.fg = Funcs.normalizeInput(fg);
    }

    public void setFont(Font font) {
        this.font = font;
        setFontCSS();
    }

    public Font getFont() {
        return font;
    }

    protected void appendFontStyle(StringBuilder temp) {
        appendFont(temp);
        appendFontColor(temp);
        appendBgFontColor(temp);
    }

    protected void appendFontStyle2(StringBuilder temp) {
        appendFont(temp);
        appendFontColor(temp);

    }

    protected void appendFontStyle(JsonObject style) {
        appendFont(style);
        appendFontColor(style);
        appendBgFontColor(style);
    }

    protected void appendFontStyle2(JsonObject style) {
        appendFont(style);
        appendFontColor(style);
    }

    protected void setFontCSS() {
        Font font = getFont();
        if (font != null) {
            StringBuilder temp = new StringBuilder(128);
            Utils.getCSS(font, temp);
            fontCSS = temp.toString();
        } else {
            fontCSS = null;
        }
    }

    protected void appendFont(StringBuilder temp) {
        if (fontCSS != null) {
            temp.append(fontCSS);
        }
    }

    protected void appendFontColor(StringBuilder temp) {
        if (fg != null)
            temp.append(fg);
    }

    protected void appendBgFontColor(StringBuilder temp) {
        if (bg != null && opaque)
            temp.append(bg);
    }

    protected void appendFont(JsonObject style) {
        Font font = this.font != null ? this.font : getFont();
        if (font != null) {
            if (!"dialog".equalsIgnoreCase(font.getName())) {
                style.add("font-family", font.getName());
            }
            if (font.getSize() > 0 && font.getSize()!=12 ) {
                style.add("font-size", font.getSize() + "px");
            }
            if (font.isBold()) {
                style.add("font-weight", "bold");
            }
            if (font.isItalic()) {
                style.add("font-style", "italic");
            }
        }
    }

    protected void appendFontColor(JsonObject style) {
        style.add("color", fg);
    }

    protected void appendBgFontColor(JsonObject style) {
        style.add("background-color", bg);
    }

    public int getWidth() {
        if (this instanceof OrWebLabel) {
            return getMaxWidth();
        }

        if (prefSize != null && prefSize.width > 0) {
            return prefSize.width;
        } else if (minSize != null && minSize.width > 0) {
            return minSize.width;
        } else if (maxSize != null && maxSize.width > 0) {
            return maxSize.width;
        } else {
            return 0;
        }
    }

    public int getHeight() {
        if (prefSize != null && prefSize.height > 0) {
            return prefSize.height;
        } else if (minSize != null && minSize.height > 0) {
            return minSize.height;
        } else if (maxSize != null && maxSize.height > 0) {
            return maxSize.height;
        } else {
            return 0;
        }
    }

    public int getMaxWidth() {
        if (maxSize != null && maxSize.width > 0) {
            return maxSize.width;
        } else if (prefSize != null && prefSize.width > 0) {
            return prefSize.width;
        } else {
            return 0;
        }
    }

    public int getMaxHeight() {
        if (maxSize != null && maxSize.height > 0) {
            return maxSize.height;
        } else if (prefSize != null && prefSize.height > 0) {
            return prefSize.height;
        } else {
            return 0;
        }
    }

    public int getMaxWidth2() {
        return maxSize != null && maxSize.width > 0 ? maxSize.width : 0;
    }

    public int getMaxHeight2() {
        return maxSize != null && maxSize.height > 0 ? maxSize.height : 0;
    }

   public void setParent(WebComponent parent) {
        this.parent = parent;
    }

    public WebComponent getParent() {
        return parent;
    }

    public String getCellWidth() {
        return cellWidth;
    }

    public String getCellHeight() {
        return cellHeight;
    }

    protected int getRelativeWeightX() {
        double total = 1.0;
        if (parent instanceof WebPanel)
            total = ((WebPanel) parent).getTotalWeightX();

        if (total == 0.0d || constraints.weightx == 0.0d)
            return 0;
        return (parent == null || !(parent instanceof WebPanel) || ((WebPanel) parent).getCols() > 1) ? (int) (constraints.weightx * 100 / total)
                : 100;
    }

    protected int getRelativeWeightY() {
        double total = 1.0;
        if (parent instanceof WebPanel) {
            total = ((WebPanel) parent).getTotalWeightY();
        }
        if (total == 0.0d || constraints.weighty == 0.0d) {
            return 0;
        }
        return (parent == null || !(parent instanceof WebPanel) || ((WebPanel) parent).getRows() > 1) ? (int) (constraints.weighty * 100 / total)
                : 100;
    }

    public void calculateSize() {
        try {
            cellWidth = "";
            cellHeight = "";
            sWidth = getWidth() > 0 ? getWidth() + "px" : "";
            sHeight = getHeight() > 0 ? getHeight() + "px" : "";

            if (minSize != null) {
                if (minSize.width > 0) {
                    minWidth = minSize.width + "px";
                }
                if (minSize.height > 0) {
                    minHeight = minSize.height + "px";
                }
            }

            if (maxSize != null) {
                if (maxSize.width > 0) {
                    maxWidth = maxSize.width + "px";
                }
                if (maxSize.height > 0) {
                    maxHeight = maxSize.height + "px";
                }
            }

            if (this instanceof OrWebLabel && parent instanceof WebPanel && ((WebPanel) parent).totalCols() > 1) {
                if (((WebPanel) parent).isLastInRow(constraints.gridy, this)) {
                    // непонятно почему?
                    cellWidth = "%";
                } else if (constraints.weightx == 0) {
                    cellWidth = "1%";
                }
            } else if (parent instanceof WebPanel) {
                if (this instanceof WebPanel && ((WebPanel) this).bgImageName != null && sHeight.length() > 0)
                    cellHeight = sHeight;
                else
                    cellHeight = (getRelativeWeightY() > 0) ? getRelativeWeightY() + "%"
                            : (((WebPanel) parent).totalRows() == 1 && !(this instanceof WebPanel && ((WebPanel) this).bgImageName != null)) ? "100%"
                                    : sHeight;

                if (this instanceof WebPanel && ((WebPanel) this).bgImageName != null && sWidth.length() > 0)
                    cellWidth = sWidth;
                else
                    cellWidth = (getRelativeWeightX() > 0) ? getRelativeWeightX() + "%"
                            : (((WebPanel) parent).totalCols() == 1 && !(this instanceof WebPanel && ((WebPanel) this).bgImageName != null)) ? "100%"
                                    : sWidth;

                if (this instanceof WebScrollPane && getRelativeWeightY() > 0 && getRelativeWeightY() < 15) {
                    cellHeight = "15%";
                } else if (this instanceof WebTable && getRelativeWeightY() > 0 && getRelativeWeightY() < 20) {
                    cellHeight = "20%";
                }
            }

            if (constraints != null) {
                if (constraints.fill == GridBagConstraints.VERTICAL) {
                    if (!(this instanceof WebPanel && ((WebPanel) this).bgImageName != null))
                        sHeight = "99%";
                } else if (constraints.fill == GridBagConstraints.HORIZONTAL) {
                    if (!(this instanceof WebPanel && ((WebPanel) this).bgImageName != null))
                        sWidth = "99%";
                } else if (constraints.fill == GridBagConstraints.BOTH) {
                    if (!(this instanceof WebPanel && ((WebPanel) this).bgImageName != null))
                        sWidth = "99%";
                    if (!(this instanceof WebPanel && ((WebPanel) this).bgImageName != null))
                        sHeight = "99%";
                }
            }

            if (parent instanceof WebTabbedPane && this instanceof WebPanel) {
                sWidth = ((WebPanel) this).hasHorizontalWeight() ? "99%" : ((WebPanel) this).getTotalSizeX() + "px";
                sHeight = ((WebPanel) this).hasVerticalWeight() ? "99%" : "";
            }

            valign = "";
            align = "";
            if (constraints != null) {
                // insets = new Insets(constraints.insets.top, constraints.insets.left, constraints.insets.bottom, constraints.insets.right);
                insets = constraints.insets;

                switch (constraints.anchor) {
                case GridBagConstraints.NORTH: {
                    valign = "top";
                    align = "center";
                    break;
                }
                case GridBagConstraints.NORTHEAST: {
                    valign = "top";
                    align = "right";
                    break;
                }
                case GridBagConstraints.EAST: {
                    align = "right";
                    break;
                }
                case GridBagConstraints.SOUTHEAST: {
                    valign = "bottom";
                    align = "right";
                    break;
                }
                case GridBagConstraints.SOUTH: {
                    valign = "bottom";
                    align = "center";
                    break;
                }
                case GridBagConstraints.SOUTHWEST: {
                    align = "left";
                    valign = "bottom";
                    break;
                }
                case GridBagConstraints.NORTHWEST: {
                    align = "left";
                    valign = "top";
                    break;
                }
                case GridBagConstraints.CENTER: {
                    align = "center";
                    break;
                }
                case GridBagConstraints.WEST: {
                    align = "left";
                    break;
                }
                }
            }
        } catch (Exception e) {
            getLog().error(e, e);
        }
    }

    public String getAlign() {
        return align;
    }

    public String getValign() {
        return valign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Funcs.normalizeInput(id);
    }

    public WebComponent getWebComponent(String id) {
        return id.equals(getId()) ? this : null;
    }
    
    public WebComponent getWebComponentUID(String id) {
        return id.equals(getUUID()) ? this : null;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        enableChanged = true;
        sendChangeProperty("e", isEnabled ? 1 : 0);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setValue(String value) {
    }
    
    public void setValue(Object value) {
    }

    public Object getValue() {
    	return null;
    }

    public String getsWidth() {
        return sWidth;
    }

    public String getsHeight() {
        return sHeight;
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width, JsonObject json) {
        return null;
    }

    public JsonObject getJsonEditor() {
        return new JsonObject();
    }

    /**
     * Вывод сообщений.
     * 
     * @param message
     *            сообщение который надо выводить
     * @param flow
     *            ждат подтверждений
     */
    public void setAlertMessage(String message, boolean flow) {
    	if (flow) {
    		getWebSession().sendMultipleCommand("alertInfoFlow", message);
    		((WebFrame) frame).waitFrameAction();
    	} else
    		getWebSession().sendMultipleCommand("alert", message);
    }
    
    /**
     * Вывод сообщений типа ошибок.
     * 
     * @param message
     *            сообщение который надо выводить
     * @param flow
     *            ждат подтверждений
     */
    public void setErrorMessage(String message, boolean flow) {
    	if (flow) {
    		getWebSession().sendMultipleCommand("alertErrorFlow", message);
    		((WebFrame) frame).waitFrameAction();
    	} else
    		getWebSession().sendMultipleCommand("alertError", message);
    }
    
    /**
     * Вывод сообщений типа предупрждений.
     * 
     * @param message
     *            сообщение который надо выводить
     * @param flow
     *            ждат подтверждений
     */
    public void setWarningMessage(String message, boolean flow) {
    	if (flow) {
    		getWebSession().sendMultipleCommand("alertWarningFlow", message);
    		((WebFrame) frame).waitFrameAction();
    	} else
    		getWebSession().sendMultipleCommand("alertWarning", message);
    }

    public void setVisible(boolean visible) {
        if (visible != isVisible && parent != null) {
            parent.childVisibleChanged = true;
            parent.calculateSize();
        }
        isVisible = visible;
        if (visible && needToPutJSON) {
        	needToPutJSON = false;
        	try {
        		putJSON();
        	} catch (Exception e) {
                getLog().error(e, e);
        	}
        }
        sendChangeProperty("v", visible ? 1 : 0);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Insets getInsets() {
        return insets;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (oldState != state) {
            this.state = state;
            oldState = state;
            stateChanged = true;
            sendChangeProperty("state", state);
        }
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
        sendChangeProperty("tt", tooltipText);
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
        sendChangeProperty("focusable", focusable);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
		this.varName = varName;
	}

	public boolean isOpaque() {
        return opaque;
    }

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
        sendChangeProperty("opaque", opaque);
    }

    /**
     * Получить имя компонента
     * 
     * @return текущее имя компонента
     * @see #setName
     */
    public String getName() {
        return name;
    }

    /**
     * Задать имя компонента
     * 
     * @param name
     *            новое имя компонента
     * @see #getName
     */
    public void setName(String name) {
        this.name = name;
    }

    public int getEstimatedWidth() {
        return getWidth();
    }

    public int getEstimatedHeight() {
        return getHeight();
    }

    public int getApproximateWidth() {
        if (getRelativeWeightX() > 0)
            return getRelativeWeightX() * ((parent != null) ? parent.getApproximateWidth() / 100 : approximateScreenWidth / 100);
        else if (getWidth() > 0)
            return getWidth();
        else
            return approximateScreenWidth;

    }

    public int getApproximateHeight() {
        if (getRelativeWeightY() > 0)
            return getRelativeWeightY()
                    * ((parent != null) ? parent.getApproximateHeight() / 100 : approximateScreenHeight / 100);
        else if (getWidth() > 0)
            return getHeight();
        else
            return approximateScreenHeight;
    }

    public String getUUID() {
        return uuid;
    }

    /**
     * Добавление в CSS описания ограничений положения компонента
     * 
     * @param b
     *            сборщик строки со всем HTML кодом
     */
    public void addConstraints(StringBuilder b) {
        if (constraints != null) {
            b.append("margin-top: ").append(constraints.insets.top).append("; ");
            b.append("margin-left: ").append(constraints.insets.left).append("; ");
            b.append("margin-bottom: ").append(constraints.insets.bottom).append("; ");
            b.append("margin-right: ").append(constraints.insets.right).append("; ");
        }
    }

    public void addConstraints(JsonObject obj) {
        if (constraints != null) {
            obj.add("margin-top", constraints.insets.top);
            obj.add("margin-left", constraints.insets.left);
            obj.add("margin-bottom", constraints.insets.bottom);
            obj.add("margin-right", constraints.insets.right);
        }
    }

    public void addSize(StringBuilder b) {
        if (sWidth != null && sWidth.length() > 0) {
            b.append("width: ").append(sWidth).append("; ");
        }
        if (sHeight != null && sHeight.length() > 0) {
            b.append("height: ").append(sHeight).append("; ");
        }
        if (minWidth != null) {
            b.append("min-width: ").append(minWidth).append("; ");
        }
        if (minHeight != null) {
            b.append("min-height: ").append(minHeight).append("; ");
        }
        /*
         * if (maxWidth != null) {
         * b.append("max-width: ").append(maxWidth).append("; ");
         * }
         * if (maxHeight != null) {
         * b.append("max-height: ").append(maxHeight).append("; ");
         * }
         */
    }

    public void addSize(JsonObject obj) {
        if (sWidth != null && sWidth.length() > 0) {
            obj.add("width", sWidth);
        }
        if (sHeight != null && sHeight.length() > 0) {
            obj.add("height", sHeight);
        }
        if (minWidth != null && minWidth.length() > 0) {
            obj.add("min-width", minWidth);
        }
        if (minHeight != null && minHeight.length() > 0) {
            obj.add("min-height", minHeight);
        }
    }

    public void addSizeMinMax(JsonObject obj) {
        if (minWidth != null && minWidth.length() > 0) {
            obj.add("min-width", minWidth);
        }
        if (minHeight != null && minHeight.length() > 0) {
            obj.add("min-height", minHeight);
        }
    }

    public void addSizeMinMax(StringBuilder b) {
        if (minWidth != null) {
            b.append("min-width: ").append(minWidth).append("; ");
        }
        if (minHeight != null) {
            b.append("min-height: ").append(minHeight).append("; ");
        }
        /*
         * if (maxWidth != null) {
         * b.append("max-width: ").append(maxWidth).append("; ");
         * }
         * if (maxHeight != null) {
         * b.append("max-height: ").append(maxHeight).append("; ");
         * }
         */
    }

    public String getJavaScript() {
        return "";
    }

    /**
     * Установить gradient.
     * 
     * @param gradient
     *            the new gradient
     */
    public void setGradient(GradientColor gradient) {
        this.gradientColor = gradient;
    }

    /**
     * Append gradient.
     * 
     * @param temp
     *            the temp
     */
    protected void appendGradient(StringBuilder temp) {
        if (gradientColor != null) {
            WebSession s = getWebSession();
            String gradient = WebUtils.gradientToString(gradientColor);
            temp.append(" background: ").append(s != null ? s.getIDCoreBrowser() : "").append("linear-gradient(")
                    .append(gradient).append("); ");
        }
    }

    protected void appendGradient(JsonObject obj) {
        if (gradientColor != null) {
            WebSession s = getWebSession();
            String gradient = WebUtils.gradientToString(gradientColor);
            obj.add("background", s != null ? s.getIDCoreBrowser() : "" + "linear-gradient(" + gradient + ")");
        }
    }

    public WebSession getWebSession() {
        if (session == null) {
            Object obj = null;
            if (this instanceof OrWebTableNavigator) {
                obj = ((OrWebTableNavigator) this).getTable();
            } else if (this instanceof OrGuiComponent) {
                obj = this;
            }
            if (obj instanceof OrWebLabel) {
                session = ((WebFrame)((OrWebLabel) obj).getFrame()).getSession();
            } else if (obj != null && ((OrGuiComponent) obj).getAdapter() != null) {
                session = ((WebFrame) ((OrGuiComponent) obj).getAdapter().getFrame()).getSession();
            }
        }
        return session;
    }

    protected JsonObject addJSON() {
        return addJSON(null);
    }

    protected JsonObject addJSON(String tid) {
        JsonObject mainJSON = new JsonObject();
        JsonObject dataObj = new JsonObject();
        String key = tid == null ? this instanceof OrGuiComponent ? uuid : id : tid;
        if (key != null) {
            mainJSON.add(key, dataObj);
        }
        return dataObj;
    }

    public void sendChangeProperty(String name, Object value) {
        if (this instanceof OrGuiComponent && getWebSession() != null && frame instanceof WebFrame) {
            if (uuid == null) {
                uuid = PropertyHelper.getUUID((OrGuiComponent) this, frame);
            }
        	long frameId = getInterfaceId();
            getWebSession().addChange(frameId, uuid, "pr."+name, value) ;
        }
    }
    
    protected void sendChange(JsonObject value, boolean isSend) {
        if (isSend && this instanceof OrGuiComponent && getWebSession() != null
        		&& frame instanceof WebFrame && value.size() > 0) {
            if (uuid == null) {
                uuid = PropertyHelper.getUUID((OrGuiComponent) this, frame);
            }
        	long frameId = getInterfaceId();
            getWebSession().addChange(frameId, uuid, value) ;
        }
    }
    
    protected void sendChange2(String name, Object value) {
        if (this instanceof OrGuiComponent && getWebSession() != null && frame instanceof WebFrame) {
            if (uuid == null) {
                uuid = PropertyHelper.getUUID((OrGuiComponent) this, frame);
            }
        	long frameId = getInterfaceId();
            getWebSession().addChange(frameId, uuid, name, value) ;
        }
    }

    public void removeChange(String name) {
    	removeChange(name, null);
    }
    
    public void removeChange(String name, long index) {
    	removeChange(name, String.valueOf(index));
    }
    
    public void removeChange(String name, String index) {
        if (this instanceof OrGuiComponent && getWebSession() != null && frame instanceof WebFrame) {
            if (uuid == null) {
                uuid = PropertyHelper.getUUID((OrGuiComponent) this, frame);
            }
        	long frameId = getInterfaceId();
            getWebSession().removeChange(frameId, uuid, name, index) ;
        }
    }

    public JsonValue getChange(String name) {
    	return getChange(name, -1);
    }
    
    public JsonValue getChange(String name, long index) {
        if (this instanceof OrGuiComponent && getWebSession() != null && frame instanceof WebFrame) {
            if (uuid == null) {
                uuid = PropertyHelper.getUUID((OrGuiComponent) this, frame);
            }
        	long frameId = getInterfaceId();
            return getWebSession().getChange(frameId, uuid, name, index) ;
        }
        return null;
    }

    @Override
    public JsonObject getJSON() {
        return putJSON(false);
    }
    @Override
    public JsonObject putJSON() {
        return putJSON(true);
    }
    
    public void setNeedToPutJSON() {
    	this.needToPutJSON = true;
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        return null;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state) {
        return null;
    }

    public int toInt(boolean value) {
        return value ? 1 : 0;
    }

    public void removeChangeProperties() {
        if (getWebSession() != null && frame instanceof WebFrame) {
            long frameId = ((WebFrame) frame).getInterfaceId();
            session.removeChanges(frameId, uuid);
        }
    }

    public JsonObject getChangeProperties() {
        if (getWebSession() != null && frame instanceof WebFrame) {
            long frameId = ((WebFrame) frame).getInterfaceId();
            return session.getChanges(frameId, uuid);
        }
        return null;
    }

    public Log getLog() {
    	if (log == null) {
	    	if (frame != null)
	            this.log = WebSessionManager.getLog(frame.getKernel().getUserSession().dsName, frame.getKernel().getUserSession().logName);
	    	else
	    		this.log = WebSessionManager.getLog(null, "");
    	}
    	return log;
    }

    public long getInterfaceId() {
        return ((WebFrame) frame).getInterfaceId();
    }

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
	
    public int getMode() {
        return mode;
    }

    public Element getXml() {
        return xml;
    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public OrFrame getFrame() {
        return frame;
    }

    public byte[] getDescription() {
    	return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, id, frame, uuid, getClass().getName());
    }

    protected void updateProperties(PropertyNode prop) {
        if (!WebController.NO_COMP_DESCRIPTION) {
            PropertyValue pv = getPropertyValue(prop.getChild("description"));
            if (!pv.isNull()) {
                Pair<String, Object> p = pv.resourceStringValue();
                descriptionUID = p.first;
                updateDescription();
            }
        }
	}

    protected void updateDescription() {
        if (!WebController.NO_COMP_DESCRIPTION) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (description != null && description.length > 0) {
                DefaultStyledDocument doc = new DefaultStyledDocument();
                ByteArrayInputStream is = new ByteArrayInputStream(description);
                try {
                    RTFEditorKit kit = new RTFEditorKit();
                    kit.read(is, doc, 0);
                    String text = doc.getText(0, doc.getLength());
                    setTooltipText(text);
                } catch (IOException e) {
                    getLog().error(e, e);
                } catch (BadLocationException e) {
                    getLog().error(e, e);
                }
            }
        }
	}
}
