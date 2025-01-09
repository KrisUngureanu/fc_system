package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;


import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.MultiMap;
import kz.tamur.guidesigner.DesignerDialog;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ObjectPropertyField extends JPanel
        implements PropertyField, ActionListener {
    private KrnObject object;
    private KrnAttribute attr;
    private int i;
    private KrnObject value;
    private KrnObject oldValue;
    private boolean isModified = false;
    private JLabel label;

    private JButton linkBtn;
    private JButton selectBtn;
    private JTable table;
    private ArrayPropertyField owner;
    private boolean isNew=false;

    public ObjectPropertyField(KrnObject object, KrnAttribute attr, int i,JTable table,ArrayPropertyField owner,
                               KrnObject value) throws KrnException {
        this.object = object;
        this.attr = attr;
        this.i = i;
        this.value = value;
        this.oldValue = value;
        this.table=table;
        this.owner=owner;
        setLayout(new GridBagLayout());
        label=new JLabel(""+object.id);
        label.setFont(table.getFont());
        
        linkBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        linkBtn.setToolTipText("Перейти к объекту");
        updateLinkButton();

        selectBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        selectBtn.setToolTipText("Выбрать объект");
        
        add(selectBtn, new CnrBuilder().x(0).build());
        add(linkBtn, new CnrBuilder().x(1).build());
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        isModified = false;
        //setPreferredSize(new Dimension(350, 25));
        //  propertyFields.add(this);
    }
    

    public String toString() {
        return label.getText();
    }

    public void restore() {
        if (isModified) {
            value = oldValue;
            updateLinkButton();
            isModified = false;
        }
    }

    public void save(MultiMap deletions) throws KrnException {
        if (isModified) {
            if (value == null) {
                if (oldValue != null) {
                    if (attr.collectionType == COLLECTION_SET) {
                        deletions.put(attr.id, oldValue);
                    } else {
                        deletions.put(attr.id,i);
                    }
                }
            } else if (!value.equals(oldValue)) {
                final Kernel krn = Kernel.instance();
                krn.setObject(object.id, attr.id, i, value.id, ObjectBrowser.transId, false);
            }
            isModified = false;
        }
    }

    public boolean isModified() {
        return isModified;
    }

    // Iplementing ActionListener interface
    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();
            KrnClass cls = Kernel.instance().getClass(attr.typeClassId);
            if (src == linkBtn && owner !=null && value!=null && value.id>0) {
                owner.setLinkValue(value,i);
                owner.getOwner().processOkClicked();
            }else if (src == selectBtn) {
                if (owner != null) {
                    owner.setLinkValue(null,i);
                }
                ObjectBrowser ob = new ObjectBrowser(cls, false);
                if(value!=null && value.id>0){
                    ob.setSelectedObject(value, null);
                }
                Container cont = getTopLevelAncestor();
                DesignerDialog dlg ;
                if (cont instanceof Dialog) {
                    dlg = new DesignerDialog((Dialog)cont, "Выбор объекта", ob);
                } else {
                    dlg = new DesignerDialog((Frame)cont, "Выбор объекта", ob);
                }
                dlg.show();
                if (dlg.isOK()) {
                    value = ob.getSelectedObject();
                    if (value != null) {
	                    updateLinkButton();
	                    isModified = true;
                    } else {
                        int idx = table.getSelectedRow();
                        ((DefaultTableModel) table.getModel()).removeRow(idx);
                    }
                } else if (isNew) {
                    int idx = table.getSelectedRow();
                    if(idx>=0)
                        ((DefaultTableModel) table.getModel()).removeRow(idx);
                }
                if(isNew){
                    linkBtn.setEnabled(false);
                }
            }
            table.grabFocus();
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    private void updateLinkButton() {
        String title = com.cifs.or2.client.Utils.getTitle(value);
        label.setText(title);
        linkBtn.setEnabled(value != null);
        if(table!=null)
            ((DefaultTableModel)table.getModel()).fireTableDataChanged();
    }

    public void deleteValue() {
        value = null;
        isModified = true;
  //          enableTransactionButtons(true);
    }
    public KrnObject getValue(){
        return value;
    }
    public void doClickSelBtn(){
        isNew=true;
        selectBtn.doClick();
    }
}

