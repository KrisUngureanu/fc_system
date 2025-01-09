package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.NotePropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.controller.WebController;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.or3.client.comps.interfaces.OrNoteComponent;

import org.jdom.Element;

import java.awt.*;
import java.io.File;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 12.09.2007
 * Time: 10:51:36
 */
public class OrWebNote extends WebButton implements JSONComponent, OrNoteComponent {
    public static PropertyNode PROPS = new NotePropertyRoot();

    private OrWebNoteBrowser popup;
    private OrGuiContainer guiParent;
    private KrnObject krnObj;
    private File dst;
    private String href;
    private String title;
    private String titleUID;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;

    OrWebNote(Element xml, int mode, OrFrame frame, String id) {
        super("OrWebNote", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        updateProperties();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        popup = null;
        title = frame.getString(titleUID);
        setText(title);
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            titleUID = (String) p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(getProperties().getChild("pov").getChild("spravInterface"));
        if (!pv.isNull()) {
            try {
                Kernel krn = frame.getKernel();
                String objId = pv.getKrnObjectId();
                if (!"".equals(objId)) {
                    krnObj = new KrnObject(Long.parseLong(objId), "", krn.getClassByName("Note").id);
                }
            } catch (KrnException e) {
            	log.error(e, e);
            }
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
    }

    public OrWebNoteBrowser getWebNoteBrowser() {
        long langId = frame.getInterfaceLang().id;
        if (krnObj != null) {
            if (popup == null) {
                popup = new OrWebNoteBrowser(krnObj, langId, ((WebFrame) frame).getSession());
            }
        }
        return popup;
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

    //
    public int getTabIndex() {
        return -1;
    }

    public byte[] getDescription() {
        return new byte[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    public ComponentAdapter getAdapter() {
        return null;
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        if (href == null) {
            href = WebController.APP_PATH + "/main?trg=frm&cmd=hlp&id=" + uuid;
        }
        property.add("href", Funcs.xmlQuote(href));
        property.add("text", getText());
        JsonObject img = new JsonObject();
        img.add("src", "images/" + (WebController.SE_UI[configNumber] ? "noteNew.png" : "Note.gif"));
        property.add("img", img);
        obj.add("pr", property);
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
