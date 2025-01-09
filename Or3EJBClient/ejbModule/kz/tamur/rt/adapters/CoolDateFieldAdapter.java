package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

import com.cifs.or2.kernel.KrnException;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Date;

import java.awt.event.*;
import java.awt.*;

public class CoolDateFieldAdapter extends ComponentAdapter {

    private OrCoolDateField dateField;
    //private boolean selfChange = false;
    private int langId;
    private ThreadLocalDateFormat FORMAT_;
    private FocusListener FOCUS_LISTENER_;
    private KeyListener KEY_LISTENER_;
    //private final String MASK = "дд.мм.гггг";
    OrCellEditor editor_;
    //private Border border;

    public CoolDateFieldAdapter(UIFrame frame, OrCoolDateField dateField, boolean isEditor)
            throws KrnException {
        super(frame, dateField, isEditor);
        PropertyNode proot = dateField.getProperties();
        PropertyValue pv =
                dateField.getPropertyValue(proot.getChild("language"));
        if (!pv.isNull()) {
            langId = Integer.parseInt(pv.getKrnObjectId());
            dataRef.addLanguage(langId);
        }
        this.dateField = dateField;
        FORMAT_ = Funcs.getDateFormat();
        FOCUS_LISTENER_ = new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                valueChanged();
            }
        };
        KEY_LISTENER_ = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    valueChanged();
            }
        };
        this.dateField.getSpinner().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                valueChanged();
            }
        });
        this.dateField.getSpinner().addFocusListener(FOCUS_LISTENER_);
        this.dateField.getSpinner().addKeyListener(KEY_LISTENER_);
        if (!dataRef.isColumn())
            kz.tamur.rt.Utils.setComponentFocusCircle(this.dateField);
        this.dateField.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == calcRef) {
                if (ref != null && ref.isCleared) {
                    Date value = (Date) calcRef.getValue(langId);
                    updateValue(value, false);
                    updateParamFilters(value);
                }
                //dateField.setEditable(checkEnabled());
            } else if (ref == dataRef) {
                Date value = (Date)ref.getValue(langId);
                if (value != null) {
                    dateField.setDate(value);
                }
                updateParamFilters(value);
                //setEditable(checkEnabled());
            }
        }  
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void updateValue(Date value, boolean overwrite) {
        //updateFilterParam(value);
        OrRef ref = dataRef;
        if (ref != null) {
            if (ref.getValue(langId) == null || overwrite) {
                OrRef.Item item = ref.getItem(langId);
                try {
                    if (item == null) {
                        ref.insertItem(-1, value, this, this,false);
                    } else {
                        ref.changeItem(value, this, this);
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                dateField.setDate(value);
            }
        } else {
            dateField.setDate(value);
        }
        updateParamFilters(value);
    }

    public void valueChanged() {
        try {
            updateValue(dateField.getDate(), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class OrDateCellEditor extends OrCellEditor {
            /**
		 * 
		 */
		private static final long serialVersionUID = -1863739046109911748L;

			public Object getCellEditorValue() {
                return dateField.getDate();
            }

            public Component getTableCellEditorComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    int row,
                    int column
                    ) {
                valueChanged(new OrRefEvent(dataRef, -1, -1, null));
                //dateField.getCaret().setVisible(true);
                return dateField;
            }

        public Object getValueFor(Object obj) {
            Object val = null;
            Date date = (Date)((OrRef.Item) obj).getCurrent();
            if (date != null)
                val = FORMAT_.format(date);
            return val;
        }

        public boolean stopCellEditing() {
            valueChanged();
            return super.stopCellEditing();
        }

    }


    public OrCellEditor getCellEditor() {
        if (editor_ == null) {
            editor_ = new OrDateCellEditor();
            //dateField.addActionListener(editor_);
            dateField.setBorder(BorderFactory.createEmptyBorder());
        }
        return editor_;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        dateField.setEnabled(isEnabled);
    }
}
