package kz.tamur.web.component;

import java.awt.Dimension;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.SpacerPropertyRoot;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebComponent;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebSpacer extends WebComponent implements JSONComponent, OrGuiComponent {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final int STEP = 5;

    private int type;
    private OrGuiContainer guiParent;
    public static PropertyNode PROPS = new SpacerPropertyRoot();
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;

    OrWebSpacer(Element xml, int type, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        this.type = type;
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        PropertyNode ps = PROPS.getChild("pos");
        PropertyValue pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        this.xml = null;
    }

    public int getType() {
        return type;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        guiParent = parent;
    }

    public int getTabIndex() {
        return -1;
    }

    public Dimension getPrefSize() {
        return prefSize;
    }

    public Dimension getMaxSize() {
        return maxSize;
    }

    public Dimension getMinSize() {
        return minSize;
    }

    public String getBorderTitleUID() {
        return null;
    }

    public byte[] getDescription() {
        return new byte[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    public ComponentAdapter getAdapter() {
        return null;
    }

    public void toHTML(StringBuilder b) {
        b.append("<span");
        StringBuilder temp = new StringBuilder(256);
        addSize(temp);
        addConstraints(temp);
        if (temp.length() > 0) {
            b.append(" style=\"").append(temp).append("\"");
        }
        b.append("> </span>");
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
       
        sendChange(obj, isSend);
        return obj;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public KrnAttribute getAttribute() {
        return null;
    }
}
