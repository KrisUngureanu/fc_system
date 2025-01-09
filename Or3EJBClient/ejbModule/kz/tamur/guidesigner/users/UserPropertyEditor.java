package kz.tamur.guidesigner.users;

import com.cifs.or2.kernel.*;
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
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.guidesigner.service.ServiceNode;
import kz.tamur.guidesigner.bases.BaseTree;
import kz.tamur.Or3Frame;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.DualTreePanel;
import kz.tamur.util.ObjectList;
import kz.tamur.util.DesignerTreeNode;
import org.jdom.Element;

/**
 * User: vital
 * Date: 29.11.2004
 * Time: 18:21:45
 */
public class UserPropertyEditor extends DefaultCellEditor {
    public static final int USER_MODE = 0;
    public static final int GROUP_MODE = 1;
    public static final int POLICY_MODE = 2;

    private JTextField stringField = new JTextField();
    private JPasswordField pdField = new JPasswordField();
    private JCheckBox checkBox = new JCheckBox("", false);
    private Object value;
    private int currRow;
    //private InterfaceTree tree = null;
    private boolean isEditing = false;
    private KrnObject lastSelectedIfcNode = null;
    private KrnObject lastSelectedBase = null;
    private int mode;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public UserPropertyEditor() {
        super(new JTextField());
        //setClickCountToStart(1);
        stringField.setLayout(new BorderLayout());
        stringField.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor(), 1));
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
        pdField.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor(), 1));
        pdField.setFont(Utils.getDefaultFont());
        pdField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            	pdField.setText("");
            }

            public void focusLost(FocusEvent e) {
                if (isEditing && !(e.getOppositeComponent()
                        instanceof ButtonsFactory.EditorButton)) {
                    stopCellEditing();
                }
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
        if (mode == POLICY_MODE) {
            String text = stringField.getText();
            try {
                Long res = Long.parseLong(text);
                return res;
            } catch (Exception e) {
                return new Long(0);
            }
        } else {
            if (currRow == 0 || (currRow == 2 && value instanceof String) ||
                    (currRow == 3 && value instanceof String)) {
                return stringField.getText();
            } else if (currRow == 1 && value instanceof KrnObject[]) {
                return value;
            } else if (currRow == 1 && !(value instanceof KrnObject[])) {
                return new String(pdField.getPassword());
            } else if (value instanceof Boolean) {
                return new Boolean(checkBox.isSelected());
            } else {
                return value;
            }
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        Component res = null;
        this.value = value;
        currRow = row;
        if (mode == POLICY_MODE) {
            stringField.removeAll();
            if (value != null) {
                stringField.setText(value.toString());
                this.value = value;
            } else {
                stringField.setText("");
            }
            res = stringField;
        } else {
            if (row == 0 || (row == 2 && value instanceof String) || (row == 3 && value instanceof String) ||
                    (row == 4 && "Должность".equals(table.getValueAt(row, 0).toString()))) {
                stringField.removeAll();
                if (value != null) {
                    stringField.setText(value.toString());
                    this.value = value;
                }
                res = stringField;
            } else if ("Доступная помощь".equals(table.getValueAt(currRow, 0).toString())) {
                res = getHelpBrowser();
            } else if ("Процесс".equals(table.getValueAt(currRow, 0).toString())) {
                res = getProcessBrowser();
            } else if ("Права OR3".equals(table.getValueAt(currRow, 0).toString())) {
                res = getOr3RightsBrowser();
            }else if (row > 3 && row < 9) {
                res = getObjectEditor(row);
            } else if ("Пункты гиперменю".equals(table.getValueAt(currRow, 0).toString())) {
                res = getHypersEditor();
    /*
            } else if (row == 1 && value instanceof KrnObject[]) {
                if (value != null) {
                    stringField.setText(value.toString());
                }
                res = stringField;
    */
            } else if (row == 1 && !(value instanceof KrnObject[])) {
                res = pdField;
    /*
            } else if (row == 1 && value instanceof KrnObject[]) {
                res = getHypersEditor();
    */
            } else if (value instanceof Boolean) {
                checkBox.setSelected(((Boolean)value).booleanValue());
                res = checkBox;
            }
            if (res != null) {
                isEditing = true;
            }
        }
        return res;
    }

    private Component getHelpBrowser() {
        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);
        //stringField.setText(parseObjectToTitle((KrnObject) value,"help"));
        StringBuffer str = new StringBuffer();
        if (value instanceof KrnObject[]) {
            for (int i = 0; i<((KrnObject[])value).length; i++) {
                if (i == 0) str.append(parseObjectToTitle(((KrnObject[])value)[i],"help"));
                else str.append("," + parseObjectToTitle(((KrnObject[])value)[i],"help"));
            }
        }
        stringField.setText(str.toString());

        btn.addActionListener(new ActionListener() {
/*
            public void actionPerformed(ActionEvent e) {

                final NoteTree tree = kz.tamur.comps.Utils.getNotesTree();
                    final JScrollPane scroller = new JScrollPane(tree);
                    scroller.setPreferredSize(new Dimension(500, 600));
                    DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                            "Выберите помощь", scroller, true);
                    dlg.show();
                    int res = dlg.getResult();
                    if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                        NoteNode[] nodes = (NoteNode[])tree.getSelectedNodes();
                        StringBuffer str = new StringBuffer();
                        KrnObject[] objs = new KrnObject[nodes.length];
                        int i = 0;
                        for (NoteNode node : nodes) {
                            KrnObject obj = node.getKrnObj();
                            if (i == 0) str.append(node.toString());
                            else str.append("," + node.toString());
                            objs[i++] = obj;
                        }
                        value = objs;
                        stringField.setText(str.toString());
                        NoteNode node = (NoteNode) tree.getSelectedNode();
                        if (node != null) {
                            value = node.getKrnObj();
                            stringField.setText(node.toString());
                        }
                    } else if (res == ButtonsFactory.BUTTON_CLEAR) {
                        value = null;
                        stringField.setText("");
                    }

                stopCellEditing();
            }
            */
            public void actionPerformed(ActionEvent e) {
                MultiEditor me = new MultiEditor(MultiEditor.NOTE_EDITOR, -1);
                me.setOldValue((KrnObject[])value);
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите помощь", me);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                            && res == ButtonsFactory.BUTTON_OK) {
                    DesignerTreeNode[] helps = me.getSelectedNodeValues();
                    StringBuffer str = new StringBuffer();
                    if (helps != null && helps.length > 0) {
                        KrnObject[] objs = new KrnObject[helps.length];
                        for (int i = 0; i < helps.length; i++) {
                            DesignerTreeNode help = helps[i];
                            KrnObject obj = help.getKrnObj();
                            if (i == 0) str.append(help.toString());
                            else str.append("," + help.toString());
                            objs[i] = obj;
                        }
                        value = objs;
                    } else {
                        value = null;
                    }
                    stringField.setText(str.toString());
                }
                stopCellEditing();
            }
        });
        stringField.add(btn, BorderLayout.EAST);
        return stringField;
    }
    private Component getProcessBrowser() {
        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);
        stringField.setText(parseObjectToTitle((KrnObject) value,"process"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ServicesTree tree = kz.tamur.comps.Utils.getServicesTree();
                DualTreePanel selector = new DualTreePanel(tree);
                selector.setPreferredSize(new Dimension(500, 600));
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите процесс", selector, true);
                dlg.show();
                int res = dlg.getResult();
                if (res == ButtonsFactory.BUTTON_OK) {
                    ServiceNode node = (ServiceNode) tree.getSelectedNode();
                    KrnObject obj = node.getKrnObj();
                    if (obj != null) {
                        value = obj;
                        stringField.setText(node.toString());
                    }
                } else if (res == ButtonsFactory.BUTTON_CLEAR) {
                    stringField.setText("");
                }

                stopCellEditing();
            }
        });
        stringField.add(btn, BorderLayout.EAST);
        return stringField;
    }

    private Component getOr3RightsBrowser() {
        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);

        final Or3RightsTree tree = kz.tamur.comps.Utils.getOr3RightsTree((Element) value);
        final String oldValue = tree.toString();
        stringField.setText(oldValue);
        final JScrollPane scroller = new JScrollPane(tree);
        scroller.setPreferredSize(new Dimension(500, 600));
        scroller.setOpaque(isOpaque);
        scroller.getViewport().setOpaque(isOpaque);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                            "Выберите права OR3", scroller, true);
                    dlg.show();
                    int res = dlg.getResult();
                    String newValue = oldValue;
                    if (res == ButtonsFactory.BUTTON_OK) {
                        Element rights = tree.getOr3Rights();
                        value = rights;
                        newValue = tree.toString();
                    } else if (res == ButtonsFactory.BUTTON_CLEAR){
                        newValue = "";
                        value = null;
                    }
                    stringField.setText(newValue);

                stopCellEditing();
            }
        });
        stringField.add(btn, BorderLayout.EAST);
        return stringField;
    }

    public void cancelCellEditing() {
        isEditing = false;
        super.cancelCellEditing();
    }

    public boolean stopCellEditing() {
        if (mode == POLICY_MODE) {
            String text = stringField.getText();
            try {
                value = Long.parseLong(text);
            } catch (Exception e) {
                value = new Long(0);
            }
        } else {
            if ((currRow != 2 && currRow != 9 && currRow != 10) && "".equals(stringField.getText())) {
                value = null;
            } else if (!(value instanceof KrnObject) && !(value instanceof KrnObject[]) && !(value instanceof Element)) {
                if (value instanceof Boolean)
                    value = checkBox.isSelected();
                else
                    value = stringField.getText();
            }
        }
        //stringField.removeAll();
        isEditing = false;
        return super.stopCellEditing();
    }

    private Component getHypersEditor() {
        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);
        if (value instanceof KrnObject[]) {
        	stringField.setText(kz.tamur.comps.Utils.parseObjectsToTitle((KrnObject[])value));
        } else {
        	stringField.setText("");
        }
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HypersEditor he = new HypersEditor();
                if (value instanceof KrnObject[])
                	he.setOldValue((KrnObject[])value);
                else
                	he.setOldValue(null);
                DesignerDialog dlg =
                        new DesignerDialog(Or3Frame.instance(),
                                "Выбор доступных пунктов", he);
                dlg.show();
                if (dlg.isOK()) {
                    value = he.getSelectedValues();
                    stringField.setText(kz.tamur.comps.Utils.parseObjectsToTitle((KrnObject[])value));
                }
                stopCellEditing();
            }
        });
        stringField.add(btn, BorderLayout.EAST);
        return stringField;
    }

    private Component getObjectEditor(int row) {

        stringField.removeAll();
        final JButton btn = ButtonsFactory.createEditorButton(
                ButtonsFactory.DEFAULT_EDITOR);
        final JButton jumpBtn = ButtonsFactory.createEditorButton(
                ButtonsFactory.IFC_EDITOR);
        switch(row) {
            case 4:
                btn.addActionListener(new BrowseObjectsListener("Note",
                        "title"));
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                        "help"));
                stopCellEditing();
                stringField.add(btn, BorderLayout.EAST);
                return stringField;
            case 5:
                btn.addActionListener(new BrowseObjectsListener("Структура баз",
                        "наименование"));
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                        "base"));
                stopCellEditing();
                stringField.add(btn, BorderLayout.EAST);
                return stringField;
            case 6:
                btn.addActionListener(new BrowseObjectsListener("Language",
                        "name"));
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                        "data language"));
                stopCellEditing();
                stringField.add(btn, BorderLayout.EAST);
                return stringField;
            case 7:
                btn.addActionListener(new BrowseObjectsListener("Language",
                        "name"));
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                        "interface language"));
                stopCellEditing();
                stringField.add(btn, BorderLayout.EAST);
                return stringField;
            case 8:
                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                        "interface"));
                btn.addActionListener(new BrowseInterfaceActionListener());
                jumpBtn.addActionListener(new JumpActionListener());
                stopCellEditing();
                JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 1, 0));
                if (Kernel.instance().getUser().isDeveloper()) buttonsPanel.add(jumpBtn);
                buttonsPanel.add(btn);
                stringField.add(buttonsPanel, BorderLayout.EAST);
                return stringField;
        }
        return null;
    }

/*
    public String parseObjectsToTitle(KrnObject[] objs) {
        String title = "";
        if (objs != null && objs.length > 0) {
            Kernel krn = Kernel.instance();
            try {
                KrnClass cls = krn.getClassByName("HiperTree");
                KrnAttribute attr = krn.getAttributeByName(cls, "title");
                java.util.List objIdList = new ArrayList();
                for (int i = 0; i < objs.length; i++) {
                    KrnObject obj = objs[i];
                    objIdList.add(obj);
                }
                long[] ids = Utils.makeObjectIdArray(objIdList);
                StringValue[] strVals = krn.getStringValues(ids, attr,
                        com.cifs.or2.client.Utils.getInterfaceLangId(), false, 0);
                for (int i = 0; i < strVals.length; i++) {
                    StringValue strVal = strVals[i];
                    if (strVal.value.length() > 0) {
                        title = ("".equals(title)) ? strVal.value : title + "," + strVal.value;
                    }
                }
/*
                for (int i = 0; i < objs.length; i++) {
                    KrnObject obj = objs[i];
                    String[] strs = krn.getStrings(obj, "title",
                            com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                    if (strs.length > 0) {
                        title = ("".equals(title)) ? strs[0] : title + "," +strs[0];
                    }
                }
*/
        /*    } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return title;
    }
*/

    public String parseObjectToTitle(KrnObject obj, String attrName) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                if ("interface".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "title",
                            com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("base".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "наименование", 0, 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("data language".equals(attrName) ||
                        "interface language".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "name", 0, 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("help".equals(attrName)) {
                    String[] strs = krn.getStrings(obj,"title",com.cifs.or2.client.Utils.getInterfaceLangId(),0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("process".equals(attrName)) {
                    String[] strs = krn.getStrings(obj,"title",com.cifs.or2.client.Utils.getInterfaceLangId(),0);
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

    public void setMode(int mode) {
        this.mode = mode;
    }


    class JumpActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (value != null) {
                    Or3Frame.instance().jumpInterface((KrnObject)value);
            } else {
                InterfaceNode node = Or3Frame.instance().createInterface();
                if (node != null) {
                    KrnObject obj = node.getKrnObj();
                    String title = node.toString();
                    value = obj;
                    stringField.setText(title);
                    Or3Frame.instance().jumpInterface((KrnObject)value);
                }
            }
            stopCellEditing();
        }
    }

    class BrowseInterfaceActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            final InterfaceTree tree = kz.tamur.comps.Utils.getIfcTree(value,
                    lastSelectedIfcNode);
            final JScrollPane sp = new JScrollPane(tree);
            sp.setPreferredSize(new Dimension(500, 600));
            sp.setOpaque(isOpaque);
            sp.getViewport().setOpaque(isOpaque);
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
                    stringField.setText(fieldText);
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
    }

    class BrowseObjectsListener implements ActionListener {

        private String titleAttr;
        private KrnClass cls= null;
        private String className;

        public BrowseObjectsListener(String className, String titleAttr) {
            this.titleAttr = titleAttr;
            final Kernel krn = Kernel.instance();
            try {
                this.className = className;
                cls = krn.getClassByName(className);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        public void actionPerformed(ActionEvent e) {
            try {
                if ("Структура баз".equals(className)) {
                    final BaseTree tree = kz.tamur.comps.Utils.getBaseTree(
                            value, lastSelectedBase);
                    final JScrollPane scroller = new JScrollPane(tree);
                    scroller.setPreferredSize(new Dimension(500, 600));
                    scroller.setOpaque(isOpaque);
                    scroller.getViewport().setOpaque(isOpaque);
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
                } else {
                    final ObjectList objectList = new ObjectList(cls, titleAttr);
                    final JScrollPane scroller = new JScrollPane(objectList);
                    scroller.setPreferredSize(new Dimension(500, 600));
                    DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                            "Выберите объект", scroller);
                    dlg.show();
                    int res = dlg.getResult();
                    if (res != ButtonsFactory.BUTTON_NOACTION
                            && res == ButtonsFactory.BUTTON_OK) {
                        KrnObject obj = objectList.getSelectedObject();
                        value = obj;
                        switch(currRow) {
                            case 4:
                                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                                        "help"));
                                break;
                            case 6:
                                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                                        "data language"));
                                break;
                            case 7:
                                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                                        "interface language"));
                                break;
                            case 8:
                                stringField.setText("" + parseObjectToTitle((KrnObject)value,
                                        "interface"));
                                break;
                        }
                    }
                }
                stopCellEditing();
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

}
