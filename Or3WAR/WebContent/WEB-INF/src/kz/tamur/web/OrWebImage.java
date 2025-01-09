package kz.tamur.web;

import static kz.tamur.comps.Mode.RUNTIME;
import kz.tamur.comps.models.ImagePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.ImageAdapter;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.webgui.WebLabel;
import kz.tamur.web.controller.WebController;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.or3.client.comps.interfaces.OrImageComponent;
import kz.tamur.or3.server.lang.SystemOp;

import org.jdom.Element;
import org.apache.commons.fileupload.FileItem;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebImage extends WebLabel implements JSONComponent, OrImageComponent {
    public static PropertyNode PROPS = new ImagePropertyRoot();

    protected boolean isSelected;
    private OrGuiContainer guiParent;
    private ImageAdapter adapter;
    private File dst;
    private boolean valueChanged = false;
    /** Максимальный размер загружаемого изображения(в байтах), если 0, то нет ограничения. */
    private long maxDataSize = 0;

    public OrWebImage(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super(" ", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);

        try {
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        PropertyNode pn = PROPS.getChild("ref").getChild("maxSize");
	        PropertyValue pv = getPropertyValue(pn);
	
	        updateProperties();
	        if (mode == RUNTIME) {
	            if (!pv.isNull()) {
	                maxDataSize = pv.intValue() * 1024;
	            }
	            adapter = new ImageAdapter(frame, this, isEditor);
	            pv = getPropertyValue(getProperties().getChild("view").getChild("image"));
	            if (!pv.isNull()) {
	                setFile(pv.getImageValue());
	            }
	        }
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
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

    public void setAdapter(ImageAdapter imageAdapter) {
        adapter = imageAdapter;
    }

    public ComponentAdapter getAdapter() {
        return adapter;
    }

    public void setIcon(ImageIcon img) {
    }

    public void setFile(Object src) {
        try {
            valueChanged = true;
            if (src instanceof File) {
				dst = Funcs.createTempFile("owi", null, WebController.WEB_IMAGES_DIRECTORY);
                if (!dst.exists()) {
                    dst.createNewFile();
    				((WebFrame)frame).getSession().deleteOnExit(dst);
                }
                adapter.copy((File) src, dst);
            } else if (src instanceof byte[]) {
				dst = Funcs.createTempFile("owi", null, WebController.WEB_IMAGES_DIRECTORY);
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

    public StringBuilder getWebImagePath() {
        if (dst != null) {
            return new StringBuilder(WebController.PATH_IMG).append(dst.getName());
        }
        return null;
    }

    public void setValue(FileItem fileItem) throws Exception {
        if (fileItem != null) {        	
        	File file = (File) adapter.doBeforeModification(fileItem);
        	if(file != null) {
        		valueChanged = true;
        		dst = Funcs.createTempFile("owi", ".png", WebController.WEB_IMAGES_DIRECTORY);
        		((WebFrame)frame).getSession().deleteOnExit(dst);
        		byte[] uploadImg = new byte[(int)file.length()];
        		FileInputStream fis = new FileInputStream(file);
        		fis.read(uploadImg);
        		fis.close();
        		//            byte[] img = new SystemOp(null).getScaledImage(uploadImg, getWidth(), getHeight(), "PNG");
        		FileOutputStream os = new FileOutputStream(dst);
        		os.write(uploadImg);
        		os.close();
        		adapter.setItem(dst);
        		adapter.doAfterModification();
        	}
        } else {
        	valueChanged = true;
        	dst = null;
            adapter.setItem(dst);
        }
        sendChangeProperty("img", getJSONImg());
    }

    public void setValue(InputStream is) {
    	OutputStream os = null;
        try {
            if (is != null) {
                valueChanged = true;

                dst = Funcs.createTempFile("owi", ".png", WebController.WEB_IMAGES_DIRECTORY);
                ((WebFrame)frame).getSession().deleteOnExit(dst);

                os = new FileOutputStream(dst);

                Funcs.writeStream(is, os, Constants.MAX_IMAGE_SIZE);

                adapter.setItem(dst);
            } else {
                valueChanged = true;
                dst = null;
                adapter.setItem(dst);
            }
            sendChangeProperty("img", getJSONImg());
        } catch (Exception e) {
            log.error(e, e);
        } finally {
			Utils.closeQuietly(os);
		}
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
}
