package kz.tamur.web;

import static kz.tamur.comps.Mode.RUNTIME;
import kz.tamur.comps.models.BarcodePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebLabel;
import kz.tamur.web.controller.WebController;
import kz.tamur.web.component.BarcodeAdapter;
import kz.tamur.web.component.BarcodeComponent;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.WebFrame;

import org.jdom.Element;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class OrWebBarcode extends WebLabel implements JSONComponent, BarcodeComponent {

    public static PropertyNode PROPS = new BarcodePropertyRoot();

    protected boolean isSelected;
    private OrGuiContainer guiParent;
    private BarcodeAdapter adapter;
    private File dst;
    private boolean valueChanged = false;

    public OrWebBarcode(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(" ", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
        minSize = PropertyHelper.getMinimumSize(this, id, frame);
        PropertyNode pn = PROPS.getChild("ref").getChild("maxSize");
        PropertyValue pv = getPropertyValue(pn);
        try {
			generateQR(getWidth(), getHeight(), "TEST");
		} catch (Exception e) {
            log.error(e, e);
		}
        if (mode == RUNTIME) {
        	updateProperties();
        	adapter = new BarcodeAdapter(frame, this);
        	
        	pv = getPropertyValue(getProperties().getChild("view").getChild("barCodeImg"));
            if(!pv.isNull()) {
            	setFile(dst);
            }
        }
        this.xml = null;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(PROPS.getChild("pov").getChild("activity").getChild("editable"));
        setEnabled(pv.isNull() ? true : !pv.booleanValue());
        
        updateProperties(PROPS);
        
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return 0;
    }

    public void setLangId(long langId) {
        if (mode == RUNTIME) {
        	updateDescription();
        }
        updateProperties();
    }

    public void setAdapter(BarcodeAdapter adapter) {
        this.adapter = adapter;
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public void setIcon(ImageIcon img) {
    }

    private void setFile(Object src) {
        try {
            valueChanged = true;
            if (src instanceof File) {
                File f = (File) src;
                StringBuilder name = new StringBuilder();
                name.append("foto");
                byte[] b = org.apache.commons.io.FileUtils.readFileToByteArray(f);
                kz.tamur.rt.Utils.getHash(b, name);
                // name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
                dst = Funcs.getCanonicalFile(WebController.IMG_HOME + File.separator + name.toString());
                if (!dst.exists()) {
                    dst.createNewFile();
                    ((WebFrame)frame).getSession().deleteOnExit(dst);
                }
                adapter.copy((File) src, dst);
            } else if (src instanceof byte[]) {
                StringBuilder name = new StringBuilder();
                name.append("foto");
                kz.tamur.rt.Utils.getHash((byte[]) src, name);
                // name.append(".").append(kz.tamur.rt.Utils.getSignature((byte[]) src));
                dst = Funcs.getCanonicalFile(WebController.IMG_HOME + File.separator + name.toString());
                if (!dst.exists()) {
                    dst.createNewFile();
                    ((WebFrame)frame).getSession().deleteOnExit(dst);
                }
                adapter.copy((byte[]) src, dst);
            } else {
                dst = null;
            }
            sendChangeProperty("img", getJSONImg());
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    public void setVerticalAlignment(int center) {
    }

    private JsonObject getJSONImg() {
        JsonObject img = new JsonObject();
        if (dst != null) {
            String name = dst.getName();
            img.add("src", name);
        }
        return img;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        sendChangeProperty("img", getJSONImg());
    }

    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();
        property.add("img", getJSONImg());
        if(tooltipText != null) {
            property.add("tt", tooltipText);
        }
        boolean isEnabled = isEnabled();
        if (getParent() instanceof OrWebPanel) {
            isEnabled &= ((OrWebPanel) getParent()).getAdapter().isEnabled();
        }
        property.add("e", toInt(isEnabled && adapter.isActive()));
        property.add("head", ((WebFrame) frame).getSession().getResource().getString("fileUploader"));
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        sendChange(obj, isSend);
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
    
    private void generateQR(int width, int height, String content) throws Exception { 
    	
    	//Проверяем размеры
    	if(width < 0 && height < 0) return;
    	
    	//Проверяем строку для шифрования
    	else if(content == "" || content == null) return ;
    	
    	//Файл для сохранения QR кода
    
		dst = Funcs.createTempFile("barcode", null, WebController.WEB_IMAGES_DIRECTORY);
        ((WebFrame)frame).getSession().deleteOnExit(dst);

    	String imageFormat = "png";
    	String charset = "UTF-8";
    	
    	//Создаем мэп с подсказкой по кодировке
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, charset);
        hints.put(EncodeHintType.MARGIN, 0);
	    
    	BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToPath(bitMatrix, imageFormat, dst.toPath());
    }

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		if (value instanceof String) {
			try {
				generateQR(getWidth(), getHeight(), (String)value);
				sendChangeProperty("img", getJSONImg());
			} catch (Exception e) {
	            log.error(e, e);
			}
		}
	}    
}
