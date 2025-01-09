package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.util.MultiMap;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimePropertyField extends TextPropertyField {
    private DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public TimePropertyField(KrnObject object, KrnAttribute attr, int i, JTable table,
                              TimeValue value) {
        super(object, attr, i,table, "");
        String text = (value != null && value.value != null)
                ? tf.format(kz.tamur.util.Funcs.convertTime(value.value)) : "";
        setText(text);
        setPreferredSize(new Dimension(100, 20));
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified) {
            String text = getText();
            if (!text.equals(oldText)) {
                if (text.length() == 0) {
                    deletions.put(attr.id,i);
                } else {
                    final Kernel krn = Kernel.instance();
                    try {
                        java.util.Date value = tf.parse(text);
                        krn.setTime(object.id, attr.id, i, value, ObjectBrowser.transId);
                    } catch (ParseException e) {
                    }
                }
                oldText = text;
            }
            isModified = false;
        }
    }
}
