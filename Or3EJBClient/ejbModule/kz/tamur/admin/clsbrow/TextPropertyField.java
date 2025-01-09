package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.MultiMap;
import com.cifs.or2.client.Kernel;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import kz.tamur.rt.Utils;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

public class TextPropertyField extends JTextField
        implements PropertyField, DocumentListener {
    protected KrnObject object;
    protected KrnAttribute attr;
    protected int i;
    protected boolean isModified = false;
    protected String oldText;
    protected boolean isDelete = false;
    protected boolean isNew = false;
    private JTable table;

    public TextPropertyField(KrnObject object, KrnAttribute attr, int i,JTable table,
                             String text) {
        super(text);
        this.object = object;
        this.attr = attr;
        this.i = i;
        this.oldText = text;
        this.table = table;
        addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        setFont(Utils.getDefaultFont());
        //setPreferredSize(new Dimension(350, 25));
        getDocument().addDocumentListener(this);
//            propertyFields.add(this);
    }

    public void setText(String t) {
        super.setText(t);
    }

    public String toString() {
        return getText();
    }

    public void restore() {
        if (isModified) {
            setText(oldText);
            isModified = false;
            isDelete=false;
            isNew=false;
        }
    }
    public void doClickSelBtn(){
        isNew=true;
        isModified=true;
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified) {
            String text = getText();
            if (isDelete) {
                if (attr.collectionType == COLLECTION_SET) {
                    deletions.put(attr.id, oldText);
                } else {
                    deletions.put(attr.id, i);
                }
            } else if (isNew || !text.equals(oldText)){
                final Kernel krn = Kernel.instance();
                long langId = 0;
                if (attr.isMultilingual) {
                    langId = com.cifs.or2.client.Utils.getDataLangId();
                }
                krn.setString(object.id, attr.id, i, langId, text, ObjectBrowser.transId);
            }
            oldText = text;
            isModified = false;
        }
    }

    public void deleteValue() {
        setText("");
        isModified = true;
        isDelete=true;
//            enableTransactionButtons(true);
    }

    public boolean isModified() {
        return isModified;
    }

    // Implementing DocumentListener interface
    public void insertUpdate(DocumentEvent e) {
        isModified = true;
//            enableTransactionButtons(true);
    }

    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
    }
}

