package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.MultiMap;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DatePropertyField extends TextPropertyField {
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public DatePropertyField(KrnObject object, KrnAttribute attr, int i, JTable table,
                              DateValue value) {
        super(object, attr, i,table, "");
        String text = (value != null && value.value != null)
                ? df.format(kz.tamur.util.Funcs.convertDate(value.value)) : "";
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
                        java.util.Date value = df.parse(text);
                        krn.setDate(object.id, attr.id, i, value, ObjectBrowser.transId);
                    } catch (ParseException e) {
                    }
                }
                oldText = text;
            }
            isModified = false;
        }
    }
}
