package kz.tamur.guidesigner.hypers;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.util.Map;
import java.util.TreeMap;

import kz.tamur.guidesigner.*;
import kz.tamur.rt.MainFrame;
import kz.tamur.Or3Frame;

/**
 * User: vital
 * Date: 29.11.2004
 * Time: 18:21:45
 */
public class HyperPropertyEditor extends DefaultCellEditor {

    private JTextField stringField = new JTextField();
    private Object value;
    private int currRow;
    private InterfaceTree tree = null;
    private boolean isEditing = false;
    private KrnObject lastSelectedIfcNode = null;
    private JCheckBox checkBox = kz.tamur.rt.Utils.createCheckBox("", false);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public HyperPropertyEditor() {
        super(new JTextField());
        setClickCountToStart(1);
        stringField.setLayout(new BorderLayout());
        stringField.setBorder(BorderFactory.createEmptyBorder());
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
        checkBox.setOpaque(false);
    }

    public Object getCellEditorValue() {
        if (currRow <=1) {
            return stringField.getText();
        } else if (currRow == 2) {
            try {
                return new Integer(stringField.getText());
            } catch (NumberFormatException e) {
                return new Integer(0);
            }
        } else if (currRow == 3) {
            return value;
        } else if (currRow == 4 || currRow == 5) {
            return new Boolean(checkBox.isSelected());
        }
        return null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        Component res = null;
        this.value = value;
        currRow = row;
        if (row <= 2) {
            stringField.removeAll();
            stringField.setText(value==null?"":value.toString());
            res = stringField;
        } else if (row == 3) {
            res = getObjectEditor();
        } else if (row == 4 || row == 5) {
            checkBox.setSelected(((Boolean)value).booleanValue());
            res = checkBox;
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
        if (currRow == 3 && "".equals(stringField.getText())) {
            value = null;
        }
        isEditing = false;
        return super.stopCellEditing();
    }

    private Component getObjectEditor() {
        final InterfaceTree tree = kz.tamur.comps.Utils.getInterfaceTree();
        final JScrollPane sp = new JScrollPane(tree);
        sp.setPreferredSize(new Dimension(500, 600));
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);
        final JButton jumpBtn = ButtonsFactory.createEditorButton(
                ButtonsFactory.IFC_EDITOR);
        jumpBtn.addActionListener(new JumpActionListener());
        stringField.setText("" + parseIfcObject((KrnObject)value));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите интерфейс", sp, true);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                    && res == ButtonsFactory.BUTTON_OK) {
                    InterfaceNode[] interfaces = (InterfaceNode[])tree.getSelectedNodes();
                    Map m = new TreeMap();
                    String fieldText = "";
                    for (int i = 0; i < interfaces.length; i++) {
                        InterfaceNode ifc = interfaces[i];
                        KrnObject obj = ifc.getKrnObj();
                        String title = ifc.toString();
                        m.put(new Long(obj.id), title);
                        if (i != interfaces.length - 1) {
                            fieldText = fieldText + title + ",";
                        } else {
                            fieldText = fieldText + title;
                        }
                        value = obj;
                        lastSelectedIfcNode = obj;
                        stringField.setText(parseIfcObject(obj));
                        stopCellEditing();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    stringField.setText("");
                    stopCellEditing();
                } else {
                    stopCellEditing();
                }
            }
        });
        stopCellEditing();
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 1, 0));
        buttonsPanel.add(jumpBtn);
        buttonsPanel.add(btn);
        stringField.add(buttonsPanel, BorderLayout.EAST);
        return stringField;
    }

    private String parseIfcObject(KrnObject obj) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                String[] str = krn.getStrings(obj, "title", Utils.getInterfaceLangId(), 0);
                if(str.length > 0)
                    title = str[0];
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    private InterfaceTree getIfcTree() {
        Kernel krn = Kernel.instance();
        try {
            KrnClass cls = krn.getClassByName("UIRoot");
            KrnObject ifcRoot = krn.getClassObjects(cls, 0)[0];
            long[] ids = {ifcRoot.id};
            String title = krn.getStringValues(ids, cls.id, "title",
                    Utils.getInterfaceLangId(),
                    false, 0)[0].value;
            long langId = Utils.getInterfaceLangId();
            InterfaceNode inode = new InterfaceNode(ifcRoot, title, langId);
            tree = new InterfaceTree(inode, langId);
            if (value != null) {
                KrnObject obj = (KrnObject)value;
                KrnObject checkObject =
                        kz.tamur.rt.Utils.getObjectById(obj.id,0);
                if (checkObject != null) {
                    tree.setSelectedNode(obj);
                    return tree;
                }
            }
            if (lastSelectedIfcNode != null) {
                tree.setSelectedNode(lastSelectedIfcNode);
            }
            return tree;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    class JumpActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (value != null) {
                    Or3Frame.instance().jumpInterface((KrnObject)value);
            } else {
                InterfaceNode node = Or3Frame.instance().createInterface();
                KrnObject obj = node.getKrnObj();
                String title = node.toString();
                value = obj;
                stringField.setText(title);
                Or3Frame.instance().jumpInterface((KrnObject)value);
            }
            stopCellEditing();
        }
    }

}
