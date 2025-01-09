package kz.tamur.guidesigner.boxes;

import com.cifs.or2.client.Utils;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.comps.Constants;
import kz.tamur.Or3Frame;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.ExpressionEditor;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 16:46:57
 * To change this template use File | Settings | File Templates.
 */
public class BoxPropertyEditor extends DefaultCellEditor {

    private JTextField stringField = new JTextField();
    private JComboBox comboField = new JComboBox();
    private Object value;
    private int currRow;
    private final JButton btnD = ButtonsFactory.createEditorButton(
            ButtonsFactory.DEFAULT_EDITOR);
    //private InterfaceTree tree = null;
    private boolean isEditing = false;
    private KrnObject lastSelectedBase = null;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public BoxPropertyEditor() {
        super(new JTextField());
        setClickCountToStart(1);
        stringField.setLayout(new BorderLayout());
        stringField.setBorder(BorderFactory.createEmptyBorder());
        stringField.setFont(Utils.getDefaultFont());
        stringField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                if (isEditing && !(e.getOppositeComponent()
                        instanceof ButtonsFactory.EditorButton)) {
                    stopCellEditing();
                }
            }
        });
        comboField.addItem("");
        comboField.addItem(Constants.TRANSPORT_LOCAL);
        comboField.addItem(Constants.TRANSPORT_EMAIL);
        comboField.addItem(Constants.TRANSPORT_MQ_CLIENT);
        comboField.addItem(Constants.TRANSPORT_MQ_JMS);
        comboField.addItem(Constants.TRANSPORT_JBOSS_JMS);
        comboField.addItem(Constants.TRANSPORT_WS);
        comboField.addItem(Constants.TRANSPORT_SGDS);
        comboField.addItem(Constants.TRANSPORT_DIIOP);
//        comboField.addItem(Constants.TRANSPORT_OPENMQ);
    }

    public Object getCellEditorValue() {
        if (currRow!=1  && currRow <10) {
            return stringField.getText();
        } else if (currRow == 11) {
            return comboField.getSelectedItem();
        } else {
            return value;
        }
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        Component res = null;
        this.value = value;
        currRow = row;
        if (row <11) {
           if (row==1) {
            res = getObjectEditor("Структура баз");
           }else if(row==10){
               res = getObjectEditor("config");
           }else{
            stringField.removeAll();
            if (value != null) {
                stringField.setText(value.toString());
            }
            res = stringField;
           }
        } else if (currRow == 11) {
            comboField.setSelectedItem(value !=null ? value : "");
            res =comboField;
        }
        if (res != null) {
            isEditing = true;
        }
        return res;
    }

    public void setCellEditorValue(Object value_) {
        value = value_;
    }

    public void cancelCellEditing() {
        isEditing = false;
        super.cancelCellEditing();
    }

    public boolean stopCellEditing() {
        if (currRow == 1 && "".equals(stringField.getText())) {
            value = null;
        }
        stringField.removeAll();
        isEditing = false;
        return super.stopCellEditing();
    }

    private Component getObjectEditor(String name) {
        stringField.removeAll();
        ActionListener[] ls=btnD.getActionListeners();
        for(int i=0;i<ls.length;++i){
            btnD.removeActionListener(ls[i]);
        }
        btnD.addActionListener(new BrowseObjectsListener(name));
        if (value != null) {
            if("Структура баз".equals(name)){
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                    "base"));
            }else
                stringField.setText("");
        } else {
            stringField.setText("");
        }
        stopCellEditing();
        stringField.add(btnD, BorderLayout.EAST);
        return stringField;
    }

    class BrowseObjectsListener implements ActionListener {

        private String className;

        public BrowseObjectsListener(String className) {
            this.className = className;
        }

        public void actionPerformed(ActionEvent e) {
            if(!e.getSource().equals(btnD)) return;
            try {
                if ("Структура баз".equals(className)) {
                    final BaseTree tree = kz.tamur.comps.Utils.getBaseTree(
                            value, lastSelectedBase);
                    final JScrollPane scroller = new JScrollPane(tree);
                    scroller.setPreferredSize(new Dimension(500, 600));
                    scroller.setOpaque(isOpaque);
                    scroller.getViewport().setOpaque(isOpaque);
                    tree.setOpaque(isOpaque);
                    DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                            "Выберите структуру баз", scroller, true);
                    dlg.show();
                    int res = dlg.getResult();
                    if (res != ButtonsFactory.BUTTON_NOACTION
                            && res == ButtonsFactory.BUTTON_OK) {
                        KrnObject obj = tree.getSelectedNode().getKrnObj();
                        if (obj != null) {
                            value = obj;
                            lastSelectedBase = obj;
                            stringField.setText(parseObjectToTitle(obj, "base"));
                        }
                    } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR){
                        value = null;
                        stringField.setText("");
                    }
                } else if("config".equals(className)) {
                    Object value_=getCellEditorValue();
                    ExpressionEditor exprEditor = new ExpressionEditor(value_!= null && ((byte[]) value_).length > 0 ? new String((byte[]) value_) : "", BoxPropertyEditor.this);
                    DesignerDialog dlg = new DesignerDialog((Frame) btnD.getTopLevelAncestor(), "Выражение", exprEditor);
                    dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
                    dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
                    dlg.show();
                    if (dlg.isOK()) {
                        setCellEditorValue(exprEditor.getExpression().getBytes());
                    }
                }
                stopCellEditing();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String parseObjectToTitle(KrnObject obj, String attrName) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                if ("base".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "наименование", 0, 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return title;
    }


}
