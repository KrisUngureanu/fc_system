package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.HyperLabelPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.HyperLabelAdapter;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.or3.client.comps.interfaces.OrHyperLabelComponent;

import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebHyperLabel extends WebButton implements JSONComponent, JSONCellComponent, OrHyperLabelComponent {

	public static PropertyNode PROPS = new HyperLabelPropertyRoot();
    private OrGuiContainer guiParent;
    private int tabIndex;
    private boolean isHelpClick = false;
    private boolean isBlockErrors = false;
    private boolean isArchiv = false;
    private String title;
    private String titleUID;
    private HyperLabelAdapter adapter;
    private boolean visibleArrow;
    private int posIcon;
    private final String tegImage = "<img style=\"max-width:none;\" src=\"";
    private ThreadLocalDateFormat formatter = new ThreadLocalDateFormat("dd.MM.yyyy");
    private boolean isEditor;

    OrWebHyperLabel(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrHyperLabel", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        setOpaque(false);
        
        try {
	        updateText();
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        PropertyNode pn = PROPS.getChild("pos").getChild("anchorImage");
	        PropertyValue pv = getPropertyValue(pn);
	        posIcon = pv.isNull() ? GridBagConstraints.EAST : pv.intValue();
	        adapter = new HyperLabelAdapter(frame, this, isEditor);
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }

        this.isEditor = isEditor;
        this.xml = null;
    }

    public void forward(boolean evalBeforeOpen) {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            adapter.forward(evalBeforeOpen);
        }
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public GridBagConstraints getConstraints() {
        return constraints;
    }

    @Override
    public void setLangId(long langId) {
        title = frame.getString(titleUID);
        setText(title);
        updateDescription();
    }

    private void updateText() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("title"));
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

        PropertyNode pn = PROPS.getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }

        pv = getPropertyValue(pn.getChild("visibleArrow"));
        if (pv.isNull()) {
            visibleArrow = true;
            setIconPath("VSlider");
        } else {
            visibleArrow = pv.booleanValue();
            if (visibleArrow) {
            	pv = getPropertyValue(pn.getChild("image"));
            	if (!pv.isNull())
            		setIconPath("foto/" + com.cifs.or2.client.Utils.createFileImg(pv, "hlb"));
            	else
                    setIconPath("VSlider");
            }
        }

        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("isBlockErrors"));
        if (!pv.isNull()) {
            isBlockErrors = pv.booleanValue();
        }
        pv = getPropertyValue(pn.getChild("activity").getChild("isArchiv"));
        if (!pv.isNull()) {
            isArchiv = pv.booleanValue();
        } else {
            // TODO Непонятная херовина - разобраться
            isArchiv = (Boolean) pn.getChild("editIfc").getDefaultValue();
        }

        pn = PROPS.getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();

        updateProperties(PROPS);

        pv = getPropertyValue(PROPS.getChild("pos").getChild("anchorImage"));
        posIcon = pv.isNull() ? GridBagConstraints.EAST : pv.intValue();
    }

    public boolean isBlockErrors() {
        return isBlockErrors;
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    @Override
    public Dimension getPrefSize() {
        return prefSize;
    }

    @Override
    public Dimension getMaxSize() {
        return maxSize;
    }

    @Override
    public Dimension getMinSize() {
        return minSize;
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public boolean isArchiv() {
        return isArchiv;
    }

    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public TableCellRenderer getCellRenderer() {
        return null;
    }

    @Override
    public int getEstimatedWidth() {
        int res = 0;
        if (title != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = this.title.replaceAll("@", "\n");
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
        if (title != null) {
            Font f = (getFont() != null) ? getFont() : new Font("Tahoma", 0, 12);
            String text = this.title.replaceAll("@", "\n");
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
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("e", toInt(isEnabled()));
        obj.add("pr", property);
        sendChange(obj, isSend);
        return obj;
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
        obj.add("pr", property);
        return obj;
    }

    /**
     * @return the posIcon
     */
    public int getPosIcon() {
        return posIcon;
    }

    /**
     * @return the visibleArrow
     */
    public boolean isVisibleArrow() {
        return visibleArrow;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }

    public void setValue(Object value) {
        if (value != null) {
            if (value instanceof Date) {
                setText(formatter.format((Date)value));
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
    	if (isEditor)
    		super.setTextDirectly(text);
    	else
    		super.setText(text);
	}
}
