package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.util.MultiMap;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.*;
import java.io.UnsupportedEncodingException;

import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;

public class BlobPropertyField extends JButton
        implements PropertyField, ActionListener {
    //private boolean isModified = false;
    private KrnObject object;
    private KrnAttribute attr;
    private int i;
    private byte[] data;
    private byte[] oldData;
    private String enc;
    private JPopupMenu pm;
    private Map<Long, byte[]> dataMap = new TreeMap<Long, byte[]>();
    private Map<Long, Boolean> modifiedMap = new TreeMap<Long, Boolean>();
    private JTable table;

    public BlobPropertyField(KrnObject object, KrnAttribute attr, int i,JTable table,
                             byte[] data, String enc) {
        this.object = object;
        this.attr = attr;
        this.i = i;
        this.data = data;
        this.oldData = data;
        this.enc = enc;
        this.table=table;
        addActionListener(this);
//            propertyFields.add(this);
        init();
    }

    public String toString() {
        return ""+i;
    }

    public void deleteValue() {
        data=null;

    }

    private void init() {
        setFont(Utils.getDefaultFont());
        setForeground(Utils.getDarkShadowSysColor());
        setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        setPreferredSize(new Dimension(150, 20));
        if (attr.isMultilingual) {
            pm = new JPopupMenu();
            pm.setFont(Utils.getDefaultFont());
            pm.setBackground(Utils.getLightSysColor());
            java.util.List langItems = LangItem.getAll();
            for (Object langItem : langItems) {
                LangMenuItem mi = new LangMenuItem((LangItem) langItem);
                mi.addActionListener(this);
                if ("RU".equals(mi.getLangItem().code) ||
                        "KZ".equals(mi.getLangItem().code) ||
                        "EN".equals(mi.getLangItem().code)) {
                    pm.add(mi);
                }
            }
        }
    }
    public byte[] getData(){
        return data;
    }
    public void doClickSelBtn(){
    }
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == this) {
                if (attr.isMultilingual) {
                    Point p = this.getMousePosition();
                    pm.show(this, (int)p.getX(), (int)p.getY());
                } else {
                    long langId = 0;
                    dataMap.put(langId, data);
                    editBlob(dataMap.get(langId), langId);
                }
            } else if (e.getSource() instanceof LangMenuItem) {
                LangMenuItem mi = (LangMenuItem)e.getSource();
                long langId = mi.getLangItem().obj.id;
                dataMap.put(langId, data);
                editBlob(dataMap.get(langId), langId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void editBlob(byte[] data, long langId) throws UnsupportedEncodingException {
        String text = (data != null) ? new String(data, this.enc)
                : "";
        JTextArea editor = new JTextArea(text);
        JScrollPane scroller = new JScrollPane(editor);
        scroller.setPreferredSize(new Dimension(600, 400));
        DesignerDialog dlg = new DesignerDialog(
                (Dialog)this.getTopLevelAncestor(),
                "Редактирование свойства", scroller);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            String res = Funcs.normalizeInput(editor.getText());
            if (!text.equals(res)) {
                this.data= res.getBytes(enc);
                dataMap.put(langId, this.data);
                modifiedMap.put(langId, true);
//                    enableTransactionButtons(true);
            }
        }
    }

    public boolean isModified() {
        return modifiedMap.keySet().size() > 0;
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified()) {
            final Kernel krn = Kernel.instance();
            for (Long langId : dataMap.keySet()) {
                byte[] data = dataMap.get(langId);
                krn.setBlob(object.id, attr.id, i, data, langId, ObjectBrowser.transId);
            }
            modifiedMap = new TreeMap<Long, Boolean>();
        }
    }

    public void restore() {
        if (isModified()) {
            dataMap = new TreeMap<Long, byte[]>();
            modifiedMap = new TreeMap<Long, Boolean>();
        }
    }
}
