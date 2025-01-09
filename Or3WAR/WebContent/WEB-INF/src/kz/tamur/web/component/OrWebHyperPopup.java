package kz.tamur.web.component;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.HyperPopupPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.ThreadLocalNumberFormat;
import kz.tamur.rt.adapters.*;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.controller.WebController;
import kz.tamur.or3.client.comps.interfaces.OrHyperPopupComponent;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;

import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebHyperPopup extends WebButton implements JSONComponent, JSONCellComponent, OrHyperPopupComponent {

    public static PropertyNode PROPS = new HyperPopupPropertyRoot();

    private OrGuiContainer guiParent;
    private int tabIndex;

    private boolean isClearBtnExists;
    private boolean isHelpClick = false;

    private String title;
    private String titleUID;
    private Map borderProps = new TreeMap();
    private WebButton deleteBtn;
    private HyperPopupAdapter adapter;
    private ThreadLocalDateFormat formatter = new ThreadLocalDateFormat("dd.MM.yyyy");
    private ThreadLocalNumberFormat dformat = null;
    private boolean ifcLock = false;
    private WebFrame frm;
    private int typeView = Constants.DIALOG;
    private String row, col;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private int posIcon;
    private ColumnAdapter columnAdapter;
	private String attentionExpr;
    private String base64Icon;
    
    OrWebHyperPopup(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrHyperPopup", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        
        try {
	        deleteBtn = OrWebTableNavigator.createButton("Delete", frame.getResourceBundle().getString("deleteBtn"));
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
	        PropertyValue pv = getPropertyValue(pn);
	        if (!pv.isNull()) {
	            posIcon = pv.intValue();
	        } else {
	            posIcon = GridBagConstraints.EAST;
	        }
	        
	        setPadding(new Dimension(1, 1));
	
	        // Тип отображаемого интерфейса
	        pv = getPropertyValue(PROPS.getChild("pov").getChild("typeView"));
	        if (pv.isNull()) {
	            typeView = Constants.DIALOG;
	        } else {
	            typeView = pv.intValue();
	        }
	        updateProperties();
	        setIconFullPath(WebController.APP_PATH + (isEditor ? "/images/HyperPopCol.gif" : "/images/VSlider.gif"));
	
	        if (mode == Mode.RUNTIME) {
	            pn = PROPS.getChild("pov").getChild("ifcLock");
	            pv = getPropertyValue(pn);
	            if (!pv.isNull()) {
	                ifcLock = pv.booleanValue();
	            }
	
	            pn = getProperties().getChild("constraints").getChild("formatPattern");
	            pv = getPropertyValue(pn);
	            String pattern;
	            if (!pv.isNull()) {
	                pattern = pv.stringValue(frame.getKernel());
	            } else {
	                pattern = pn.getDefaultValue().toString();
	            }
	            if (pattern != null && pattern.length() > 0) {
	                dformat = new ThreadLocalNumberFormat(pattern, ',', ' ', true, -1, 3);
	            }
	
	            adapter = new HyperPopupAdapter(frame, this, isEditor);
	        }
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
    }

    public String actionPerformed(long fid) {
        row = col = null;
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            adapter.doBeforeOpen();
            frm = (WebFrame) adapter.getPopupFrame();
            if (frm == null) {
                String mess = "Не задан интерфейс обработки!";
                log.debug(mess);
                getWebSession().sendMultipleCommand("alert", mess);
                StringBuffer b = new StringBuffer();
                b.append("<r>");
                b.append("<alert>");
                b.append(mess);
                b.append("</alert>");
                b.append("</r>");
                return b.toString();
            }
            
            int mode = frm.getEvaluationMode();
            PanelAdapter pa = frm.getPanelAdapter();
            OrPanelComponent p = frm.getPanel();
            String title = p.getTitle();
            // frm.getRef().absolute(index, hpopup);
            boolean ifcEnabled = !ifcLock;
            if (ifcEnabled) {
                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.ARCH_RO_MODE);
            }
            if (ifcEnabled) {
                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.READONLY_MODE);
            }
            pa.setEnabled(ifcEnabled);
            
            return frm.setSize(id, fid);
        }
        return "";
    }

    public String openPopup(long fid) {
    	JsonObject res = new JsonObject();
        row = col = null;
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            boolean open = adapter.doBeforeOpen();
            if (open) {
	            frm = (WebFrame) adapter.getPopupFrame();
	            if (frm == null) {
	                res.add("result", "error").add("message", "Не задан интерфейс обработки!");
	                return res.toString();
	            }
	            
	            int mode = frm.getEvaluationMode();
	            PanelAdapter pa = frm.getPanelAdapter();
	            OrPanelComponent p = frm.getPanel();
	            String title = p.getTitle();
	            // frm.getRef().absolute(index, hpopup);
	            boolean ifcEnabled = !ifcLock;
	            if (ifcEnabled) {
	                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.ARCH_RO_MODE);
	            }
	            if (ifcEnabled) {
	                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.READONLY_MODE);
	            }
	            pa.setEnabled(ifcEnabled);
	            
	            res.add("w", ((WebComponent)p).getMaxWidth() + 10);
	            res.add("h", ((WebComponent)p).getMaxHeight() + 60);
	            res.add("t", title);
	            res.add("tv",typeView);
            } else {
                res.add("result", "nop");
            }
        }
        return res.toString();
    }

    public String actionPerformed(int row, int col, String id, long fid) {
        this.row = String.valueOf(row);
        this.col = String.valueOf(col);
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            boolean open = adapter.doBeforeOpen();
            if (open) {
	            frm = (WebFrame) adapter.getPopupFrame();
	            if (frm == null) {
	                String mess = "Не задан интерфейс обработки!";
	                log.debug(mess);
	                getWebSession().sendMultipleCommand("alert", mess);
	                StringBuffer b = new StringBuffer();
	                b.append("<r>");
	                b.append("<alert>");
	                b.append(mess);
	                b.append("</alert>");
	                b.append("</r>");
	                return b.toString();
	            }
	            return frm.setSize(id, fid, row, col);
            }
        }
        return "";
    }
    
    public String getBase64Icon() {
        return base64Icon;
    }

    public String openPopup(int row, String colUid, String id, long fid) {
    	JsonObject res = new JsonObject();
        this.row = String.valueOf(row);
        this.col = String.valueOf(colUid);
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            boolean open = adapter.doBeforeOpen();
            if (open) {
	            frm = (WebFrame) adapter.getPopupFrame();
	            if (frm == null) {
	                String mess = "Не задан интерфейс обработки!";
	                log.debug(mess);
	                res.add("result", "error").add("message", "Не задан интерфейс обработки!");
	                return res.toString();
	            }
	
	            int mode = frm.getEvaluationMode();
	            PanelAdapter pa = frm.getPanelAdapter();
	            OrPanelComponent p = frm.getPanel();
	            String title = p.getTitle();
	            // frm.getRef().absolute(index, hpopup);
	            boolean ifcEnabled = !ifcLock;
	            if (ifcEnabled) {
	                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.ARCH_RO_MODE);
	            }
	            if (ifcEnabled) {
	                ifcEnabled = !(mode == kz.tamur.rt.InterfaceManager.READONLY_MODE);
	            }
	            pa.setEnabled(ifcEnabled);
	            
	            res.add("w", ((WebComponent)p).getMaxWidth() + 10);
	            res.add("h", ((WebComponent)p).getMaxHeight() + 60);
	            res.add("t", title);
	            res.add("tv",typeView);
            } else {
                res.add("result", "nop");
            }
        }
        return res.toString();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            title = frame.getString(titleUID);
            setText(title);
            updateDescription();
            deleteBtn.setToolTipText(frame.getResourceBundle().getString("deleteBtn"));
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
            if (!pv.isNull()) {
                setText(pv.stringValue(frame.getKernel()));
            }
            if (!WebController.NO_COMP_DESCRIPTION) {
                pv = getPropertyValue(PROPS.getChild("description"));
                if (!pv.isNull()) {
                    Pair p = pv.resourceStringValue();
                    descriptionUID = (String) p.first;
                    description = frame.getBytes(descriptionUID);
                }
            }
        }
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        updateProperties(PROPS);
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("opaque"));
        if (!pv.isNull()) {
            setOpaque(pv.booleanValue());
        } else {
            setOpaque(true);
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            // setContentAreaFilled(false);
            // setOpaque(true);
            setBackground(pv.colorValue());
            // setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        }
        pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            byte[] b = pv.getImageValue();
            if (b != null && b.length > 0) {
                base64Icon = new String(Base64.encode(b));
            }
        }
        PropertyNode pnode = pn.getChild("alignmentText");
        pv = getPropertyValue(pnode);
        if (!pv.isNull()) {
            // setHorizontalAlignment(pv.intValue());
        } else {
            // setHorizontalAlignment(((Integer)pnode.getDefaultValue()).intValue());
        }
        pv = getPropertyValue(pn.getChild("clearBtnShow"));
        isClearBtnExists = pv.isNull() ? false : pv.booleanValue();
        deleteBtn.setVisible(isClearBtnExists);

        pv = getPropertyValue(pn.getChild("showIcon"));
        if (!pv.isNull()) {
            isIconVisible = pv.booleanValue();
        }

        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
        
        pv = getPropertyValue(pn.getChild("activity").getChild("attention"));
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }
    }

    public String getAttentionExpr() {
    	return attentionExpr;
    }
    
    public boolean isClearBtnExists() {
        return isClearBtnExists;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public String getBorderTitleUID() {
        return null;
    }

    //
    public int getTabIndex() {
        return tabIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        deleteBtn.setEnabled(b);
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public void setValue(Object value) {
        if (value != null) {
            if (value instanceof java.util.Date) {
                setText(formatter.format((Date)value));
            } else if (value instanceof Double && dformat != null) {
                setText(dformat.format(value));
            } else {
                setText(value.toString());
            }
        } else {
            setText("");
        }
    }
    
    @Override
	public void setText(String text) {
    	if (text == null || text.length() == 0)
    		text = title != null ? title : "";
		super.setText(text);
	}

	public void setValue(String value) {
        if ("0".equals(value)) {
            adapter.okPressed(frm);
        } else if ("5".equals(value)) {
            adapter.clearPressed(true);
        } else if ("DEL".equals(value)) {
            adapter.deleteButtonPressed();
        } else {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
            mgr.releaseInterface(false);
        }
    }

    public JsonObject buttonPressed(String value) {
        if ("CHECK".equals(value)) {
        	JsonObject res = adapter.checkConstraints(frm);
        	
    		if ("success".equals(res.get("result").asString())) {
                adapter.okPressed(frm);
    		} else
    			return res;
        } else if ("0".equals(value)) {
            adapter.okPressed(frm);
        } else if ("5".equals(value)) { // В попапах еще может быть кнопка "Очистить", пока не реализовано
            adapter.clearPressed(true);
        } else if ("DEL".equals(value)) {
            adapter.deleteButtonPressed();
        } else {
            kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
            mgr.releaseInterface(false);
        }
        return null;
    }

    public TableCellRenderer getCellRenderer() {
        return null;
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width) {
        String text = "";
        if (adapter.getTitleRef() != null) {
            List<OrRef.Item> items = adapter.getTitleRef().getItems(adapter.getLangId());
            if (items.size() > 0 && items.size() != row) {
                Object o = items.get(row).getCurrent();
                if (o instanceof Date) {
                    text = formatter.format((Date)o);
                } else if (o instanceof Double && dformat != null) {
                    text = dformat.format(o);
                } else if (o != null) {
                    text = o.toString();
                }
            }
        }

        JsonObject json = new JsonObject();
        json.add("uuid", tid);
        json.add("row", row);
        json.add("column", col);
        json.add("class", "hlb");
        json.add("text", text);
        json.add("onClick", "popupPressed(this, '" + tid + "', " + row + ", " + col + "); return false;");
        return json;
    }

    @Override
    public int getEstimatedWidth() {
        int res = 0;
        if (getText() != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = getText().replaceAll("@", "\n");
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
        if (getText() != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = getText().replaceAll("@", "\n");
            Rectangle2D bs = f.getStringBounds(text, new FontRenderContext(null, false, false));
            int height = (int) bs.getHeight();
            if (height > res) {
                res = height;
            }
        }
        return res + 5;
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        return null;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state) {
        JsonObject obj = addJSON(tid);
        JsonObject property = new JsonObject();
        property.add("row", row);
        property.add("column", column);
        property.add("cellEditable", cellEditable);
        property.add("isSelected", isSelected);
        property.add("state", state);

        String text = "";
        if (value instanceof java.util.Date) {
            text = formatter.format((Date)value);
        } else if (value instanceof Double && dformat != null) {
            text = dformat.format(value);
        } else if (value != null) {
            text = value.toString();
        }

        property.add("text", text);

        if (isIconVisible) {
            JsonObject img = new JsonObject();
            if (getIconPath() != null) {
                img.add("src", "images/" + getIconPath() + ".gif");
            } else if (getIconFullPath() != null) {
                img.add("src", getIconFullPath());
            }
            property.add("img", img);
        }
        obj.add("pr", property);
        return obj;

    }
    
    public JsonObject getJsonEditor() {
        return new JsonObject().add("popup", 1);
    }

    public void setColumnAdapter(ColumnAdapter adapter) {
        this.columnAdapter = adapter;
    }

    public ColumnAdapter getColumnAdapter() {
        return columnAdapter;
    }

    public void clearValue() {
        if (isClearBtnExists) {
            adapter.clearPressed(false);
        }
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }
}
