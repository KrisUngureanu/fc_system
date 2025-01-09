package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.MultiMap;
import com.cifs.or2.client.Kernel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;

public class MemoPropertyField extends JButton
        implements PropertyField, ActionListener {
    private boolean isModified = false;
    private KrnObject object;
    private KrnAttribute attr;
    private int i;
    private String data;
    private String oldData;
    private JTextArea editor = new JTextArea();
    protected boolean isDelete = false;
    protected boolean isNew = false;
    private JTable table;

    public MemoPropertyField(KrnObject object, KrnAttribute attr, int i,JTable table,
                             String data) {
        this.object = object;
        this.attr = attr;
        this.i = i;
        this.data = data;
        this.oldData = data;
        this.table = table;
        addActionListener(this);
//            propertyFields.add(this);
        init();
    }

    public String toString() {
        return ""+i;
    }

    public void setText(String t) {
        super.setText(t);
    }

    public String getText() {
        return data;
    }
    public void deleteValue() {
        data="";
        isDelete=true;
        isModified=true;
    }

    private void init() {
        setFont(Utils.getDefaultFont());
        setForeground(Utils.getDarkShadowSysColor());
        setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        setPreferredSize(new Dimension(150, 20));
    }

    public void actionPerformed(ActionEvent e) {
        try {
            String text = (data != null) ? data : "";
            editor.setText(text);
            JScrollPane scroller = new JScrollPane(editor);
            scroller.setPreferredSize(new Dimension(600, 400));
            DesignerDialog dlg = new DesignerDialog(
                    (Dialog)this.getTopLevelAncestor(),
                    "Редактирование свойства", scroller);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                String res = Funcs.normalizeInput(editor.getText());
                if (!text.equals(res)) {
                    data = res;
                    isModified = true;
//                        enableTransactionButtons(true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doClickSelBtn(){
        isModified=true;
        isNew=true;
    }

    public boolean isModified() {
        return isModified;
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified) {
            String text = getText();
                if (isDelete) {
                    deletions.put(attr.id, i);
                } else if (isNew || !text.equals(oldData)) {
                    final Kernel krn = Kernel.instance();
                    long langId = 0;
                    if (attr.isMultilingual) {
                        langId = com.cifs.or2.client.Utils.getDataLangId();
                    }
                    krn.setString(object.id, attr.id, i, langId, text, ObjectBrowser.transId);
                }
            oldData = text;
            isModified = false;
        }
    }

    public void restore() {
        if (isModified) {
            data = oldData;
            isModified = false;
            isDelete=false;
            isNew=false;
        }
    }
}
