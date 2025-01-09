package kz.tamur.guidesigner.bases;

//import com.cifs.or2.kernel.KrnObject;
//import com.cifs.or2.kernel.KrnClass;
//import com.cifs.or2.kernel.KrnException;
//import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.beans.PropertyChangeSupport;
//import java.util.Map;
//import java.util.TreeMap;

import kz.tamur.guidesigner.*;
//import kz.tamur.Or3Frame;
//import kz.tamur.util.ObjectList;

/**
 * User: vital
 * Date: 29.11.2004
 * Time: 18:21:45
 */
public class BasePropertyEditor extends DefaultCellEditor {

	private JCheckBox checkBox = new JCheckBox("", false);
	private JTextField stringField = new JTextField();
    private JFormattedTextField intField = new JFormattedTextField(
            new NumberFormatter(new DecimalFormat("#")));
    private Object value;
    private int currRow;
    //private InterfaceTree tree = null;
    private boolean isEditing = false;
    //private KrnObject lastSelectedIfcNode = null;
    private PropertyChangeSupport ps = new PropertyChangeSupport(this);

    public BasePropertyEditor() {
        super(new JTextField());
        setClickCountToStart(1);
        stringField.setLayout(new BorderLayout());
        stringField.setBorder(BorderFactory.createEmptyBorder());
        stringField.setFont(Utils.getDefaultFont());
        intField.setBorder(BorderFactory.createEmptyBorder());
        intField.setFont(Utils.getDefaultFont());
        intField.addFocusListener(new EditorFocusListener());
        stringField.addFocusListener(new EditorFocusListener());
        intField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Component c = e.getComponent();
                    if (((JFormattedTextField)c).isEditValid()) {
                        stopCellEditing();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                }
                super.keyPressed(e);
            }
        });
        checkBox.setOpaque(false);
        checkBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                if (isEditing && !(e.getOppositeComponent()
                        instanceof ButtonsFactory.EditorButton)) {
                    stopCellEditing();
                }
            }
        });
    }

    public Object getCellEditorValue() {
        if (currRow == 0) {
            return stringField.getText();
        } else {
            if (value instanceof Boolean) {
            	return new Boolean(checkBox.isSelected());
            } else if (intField.isEditValid()) {
            	return new Integer(intField.getText());
            } else {
                return new Integer(0);
            }
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        Component res = null;
        this.value = value;
        currRow = row;
        if (row == 0) {
            stringField.removeAll();
            if (value != null) {
                stringField.setText(value.toString());
            }
            res = stringField;
        } else {
            if (value != null) {
            	if (value instanceof Boolean) {
                    checkBox.setSelected(((Boolean)value).booleanValue());
                    res = checkBox;
            	} else {
            		intField.setValue(value);
                    res = intField;
            	}
            }
        }
        if (res != null) {
            isEditing = true;
        }
        return res;
    }

    public void cancelCellEditing() {
        isEditing = false;
        super.cancelCellEditing();
    }

    public boolean stopCellEditing() {
        isEditing = false;
        return super.stopCellEditing();
    }


    class EditorFocusListener implements FocusListener {
        public void focusGained(FocusEvent e) {

        }

        public void focusLost(FocusEvent e) {
            if (isEditing && !(e.getOppositeComponent()
                    instanceof ButtonsFactory.EditorButton)) {
                stopCellEditing();
            }
        }
    }


}
