package kz.tamur.rt.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrImageComponent;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.util.Funcs;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

public class ImageAdapter extends ComponentAdapter {
    private OrImageComponent image;
    private boolean autoresize = true;
    private RadioGroupManager groupManager = new RadioGroupManager();

    public ImageAdapter(OrFrame frame, OrImageComponent img, boolean isEditor) throws KrnException {
        super(frame, img, isEditor);
        image = img;
        PropertyNode proot = img.getProperties();
        PropertyValue pv = img.getPropertyValue(proot.getChild("view").getChild("autoresize"));
        if (!pv.isNull()) {
            autoresize = pv.booleanValue();
        }
        
        image.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        OrRef.Item item = dataRef.getItem(langId);
        image.setFile(item == null ? null : item.getCurrent());
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }  
    }

    public void clear() {
        image.setFile(null);
    }

    public void setItem(File file) throws KrnException {
        OrRef ref = dataRef;
        if (ref.getItem(langId) == null) {
            ref.insertItem(0, file, this, this, false);
        } else {
            if (file == null) {
                dataRef.deleteItem(this, dataRef.getValue(0, 0));
            } else {
                ref.changeItem(file, this, this);
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream is = new FileInputStream(src);
        byte[] buf = new byte[(int) src.length()];
        is.read(buf);
        is.close();
        FileOutputStream os = new FileOutputStream(dst);
        os.write(buf);
        os.close();
    }
    
    public void doAfterModification() {
    	try {
			super.doAfterModification();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public Object doBeforeModification(FileItem fileItem) {
    	Object res = null;
    	try {    		
    		if(fileItem instanceof DiskFileItem) {
    			File file = null;
    			file = Funcs.createTempFile("owi", ".png", WebController.WEB_IMAGES_DIRECTORY);
                ((WebFrame)frame).getSession().deleteOnExit(file);
                byte[] uploadImg = fileItem.get();
                FileOutputStream os = new FileOutputStream(file);
                os.write(uploadImg);
                os.close();
    			res = super.doBeforeModification(file);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return res;
    }

    public void copy(byte[] src, File dst) throws IOException {
        FileOutputStream os = new FileOutputStream(dst);
        os.write(src);
        os.close();
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        image.setEnabled(isEnabled);
    }

    public void setLangId(long langId) {
        image.setLangId(langId);
    }

    public boolean isAutoresize() {
        return autoresize;
    }
}
