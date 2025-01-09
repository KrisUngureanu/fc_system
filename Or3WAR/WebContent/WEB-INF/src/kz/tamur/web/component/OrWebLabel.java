package kz.tamur.web.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.LabelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.LabelAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebLabel;
import kz.tamur.web.controller.WebController;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 18.07.2006
 * Time: 18:30:50
 */
public class OrWebLabel extends WebLabel implements JSONComponent, JSONCellComponent, OrGuiComponent {
	
    public static PropertyNode PROPS = new LabelPropertyRoot();
    private OrGuiContainer guiParent;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private boolean isEditor = false;;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    private LabelAdapter adapter;

    /**
     * Конструктор класса or web label.
     * 
     * @param xml
     *            xml.
     * @param mode
     *            mode.
     * @param frame
     *            frame.
     * @param isEditor
     *            is editor.
     * @param id
     *            id.
     * @throws KrnException
     *             the krn exception
     */
    OrWebLabel(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrLabel", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        
        try {
	        this.isEditor = isEditor;
	        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        adapter = new LabelAdapter(frame, this);
	        updateText();
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
    }

    private void updateText() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));

        if (!pv.isNull()) {
            String titleUID = (String) pv.resourceStringValue().first;
            String title = frame.getString(titleUID);
            setText(title);
        }
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontGExpr"));
        if (!pv.isNull()) {
        	Object res=evalExpr(pv);
            if (res!=null && res instanceof Font) {
                setFont((Font)res);
            }
        }
       pv = getPropertyValue(pn.getChild("image"));
        if (!pv.isNull()) {
            // setIcon(Utils.processCreateImage(pv.getImageValue()));
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontExpr"));
        if (!pv.isNull()) {
        	Object res=evalExpr(pv);
            if (res instanceof Number) {
                setForeground(new Color(((Number)res).intValue()));
            } else if (res instanceof String) {
            	setForeground(Utils.getColorByName(res.toString()));
            }
        }
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
    }

    private Object evalExpr(PropertyValue value){
    	String expr ="";
    	Object res=null;
        if (value.objectValue() instanceof Expression) {
        	expr = ((Expression)value.objectValue()).text;
        } else if (value.objectValue() instanceof String){
        	expr = (String)value.objectValue();
        }
        if(!"".equals(expr)){
            try {
                res = kz.tamur.comps.Utils.evalExp(expr, frame, getAdapter());
            } catch (Exception e) {
                log.error("Ошибка в формуле\r\n" + expr + "\r\n" + e);
            }
        }
        return res;    	
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public GridBagConstraints getConstraints() {
        if (mode == Mode.RUNTIME) {
            return constraints;
        } else {
            return PropertyHelper.getConstraints(PROPS, xml, id, frame);
        }
    }

    @Override
    public void setLangId(long langId) {
        updateText();
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
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    @Override
    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    /**
     * Получить tab index.
     * 
     * @return tab index.
     */
    public int getTabIndex() {
        return -1;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public byte[] getDescription() {
        return new byte[0];
    }

    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        //JsonObject obj = super.putJSON(isSend);
        //sendChange(obj, isSend);
        return null;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state) {
        byte[] b = null;
        if (value instanceof File) {
            try {
            	File input = Funcs.getCanonicalFile((File)value);
                int len = (int) input.length();
                if (len < Constants.MAX_IMAGE_SIZE) {
                    InputStream inp = Files.newInputStream(input.toPath());
                    b = Funcs.readStream(inp, Constants.MAX_IMAGE_SIZE);
                    inp.close();
                } else
                	throw new IOException("Превышен допустимый размер изображения: " + Constants.MAX_IMAGE_SIZE);
            } catch (IOException e) {
            	log.error(e, e);
            }
        } else if (value instanceof byte[]) {
            b = (byte[]) value;
        }
        String iconPath = null;
        if (b != null && b.length > 0) {
            StringBuilder name = new StringBuilder();
            name.append("imc");
            kz.tamur.rt.Utils.getHash(b, name);
            name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
            try {
                File f = Funcs.getCanonicalFile(WebController.IMG_HOME + File.separator + name.toString());
                if (!f.exists()) {
                    f.createNewFile();
                    FileOutputStream os = new FileOutputStream(f);
                    os.write(b);
                    os.close();
                }
                iconPath = WebController.PATH_IMG + name.toString();
            } catch (IOException e) {
            	log.error(e, e);
            }
        }

        JsonObject obj = addJSON(tid);
        JsonObject property = new JsonObject();
        property.add("row", row);
        property.add("column", column);
        property.add("cellEditable", cellEditable);
        property.add("isSelected", isSelected);
        property.add("state", state);

        if (iconPath != null) {
            JsonObject img = new JsonObject();
            img.add("src", iconPath);
            property.add("img", img);
        }
        obj.add("pr", property);
        return obj;
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
