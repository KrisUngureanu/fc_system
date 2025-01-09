package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.MultiMap;

import javax.swing.*;
import java.awt.*;

public class IntPropertyField extends TextPropertyField {
    public IntPropertyField(KrnObject object, KrnAttribute attr, int i, JTable table,
                            long value) {
        super(object, attr, i,table, String.valueOf(value));
        setPreferredSize(new Dimension(100, 20));
        setMaximumSize(new Dimension(100, 20));
        setMinimumSize(new Dimension(100, 20));
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified) {
            String text = getText();
            if (!text.equals(oldText)) {
                if (text.length() == 0) {
                    deletions.put(attr.id,i);
                } else {
                    final Kernel krn = Kernel.instance();
                    long value = Long.parseLong(text);
                    krn.setLong(object.id, attr.id, i, value, ObjectBrowser.transId);
                }
                oldText = text;
            }
            isModified = false;
        }
    }
}

