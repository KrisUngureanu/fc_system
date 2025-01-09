package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.or3.client.comps.interfaces.OrMemoComponent;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MemoFieldAdapter extends ComponentAdapter {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + MemoFieldAdapter.class.getName());

    private OrMemoComponent memoField;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private OrRef copyRef;

    public MemoFieldAdapter(OrFrame frame, OrMemoComponent memoField, boolean isEditor)
            throws KrnException {
        super(frame, memoField, isEditor);
        this.memoField = memoField;
        this.memoField.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }
    
    public void clear() {
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        memoField.setEnabled(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            memoField.setValue("");
        }
    }

    public OrRef getCopyRef() {
        return copyRef;
    }

    public void setCopyRef(OrRef copyRef) {
        this.copyRef = copyRef;
    }

    public void copyPerformed() {
        if (copyRef != null) {
            try {
                OrRef ref = dataRef;
                OrRef.Item item = copyRef.getItem(langId);
                Object value = (item != null) ? item.getCurrent() : null;
                if (ref.getItem(langId) == null)
                    ref.insertItem(0, value, null, MemoFieldAdapter.this, false);
                else
                    ref.changeItem(value, MemoFieldAdapter.this, null);
                if (isEditor()) {
                    memoField.setValue(String.valueOf(value));
                }
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Object getValueFor(Object obj) {
        Object val = ((OrRef.Item) obj).getCurrent();
        String res = null;
        if (val instanceof File && ((File)val).length() < Constants.MAX_DOC_SIZE) {
        	Path p = ((File) val).toPath();
            byte[] buff = new byte[(int)((File)val).length()];
            InputStream is = null;
            try {
            	// Проверка имени файла
            	if (p.toFile().getName().startsWith("blob")) {
	            	is = Files.newInputStream(p);
	            	buff = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
	                is.close();
	                res = new String(buff, "UTF-8");
            	}
            } catch (Exception e) {
                log.error(e, e);
            } finally {
            	Utils.closeQuietly(is);
            }
        } else if (val instanceof String) {
            res = (String) val;
        }
        return res;
    }
}
