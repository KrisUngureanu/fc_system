package kz.tamur.guidesigner.translator;

import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.*;
import kz.tamur.util.Pair;
import kz.tamur.util.LanguageCombo;
import kz.tamur.util.LangItem;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.io.ByteArrayInputStream;

import com.cifs.or2.util.MultiMap;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: vital
 * Date: 10.10.2005
 * Time: 10:16:11
 * To change this template use File | Settings | File Templates.
 */
// TODO Не используется
public class TranslatorPanel extends JPanel implements ActionListener {

    private MultiMap translateList = new MultiMap();
    //Выбор интерфейса
    private JPanel chooseIfcPanel = new JPanel(new GridBagLayout());
    private JTextField ifcText = Utils.createDesignerTextField();
    private JCheckBox checkBox = Utils.createCheckBox("все интерфейсы", false);
    private JButton browseBtn = ButtonsFactory.createToolButton("editor",
            "Выбрать", true);
    //Выбор языков
    private JPanel langPanel = new JPanel(new GridBagLayout());
    private LanguageCombo langCombo = new LanguageCombo();
    private JLabel langLabel = Utils.createLabel(langCombo.getSelectedItem().toString());
    //Счётчики
    private JPanel progressPanel = new JPanel(new GridBagLayout());
    private JLabel ifcLabel = Utils.createLabel("Интерфейс(ов): ");
    private JLabel ifcCountLabel = Utils.createLabel("");
    private JLabel checkLabel = Utils.createLabel("Совпадений имён: ");
    private JLabel checkCountLabel = Utils.createLabel("");
    //Статус выполнения
    private JPanel statusPanel = new JPanel(new GridBagLayout());
    private JProgressBar ifcProgress = new JProgressBar();
    private JProgressBar compProgress = new JProgressBar();
    //Результирующая панель
    private JPanel resultPanel = new JPanel(new BorderLayout());
    private JTable resultTable = new JTable();
    //Панель с кнопками
    private Box buttonPanel = new Box(BoxLayout.Y_AXIS);
    private JButton runBtn = ButtonsFactory.createToolButton("run", "Запустить",
            "Запустить формирование исходного файла", true);
    private JButton saveToFileBtn = ButtonsFactory.createToolButton(
            "Save", "Сохранить", "Сохранить в файл", true);
    private Pair ifcSingle;
    private InterfaceTree tree = kz.tamur.comps.Utils.getInterfaceTree();

    public TranslatorPanel() {
        init();
    }

    private void init() {
        //Панель выбора интерфейса
        Border b = BorderFactory.createLineBorder(Utils.getMidSysColor());
        TitledBorder tb = Utils.createTitledBorder(b, "Выбор интерфейса");
        chooseIfcPanel.setBorder(tb);
        chooseIfcPanel.setPreferredSize(new Dimension(600, 100));
        chooseIfcPanel.setMaximumSize(new Dimension(600, 100));
        chooseIfcPanel.setMinimumSize(new Dimension(600, 100));
        chooseIfcPanel.add(Utils.createLabel("Имя"), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        ifcText.setPreferredSize(new Dimension(300, 20));
        ifcText.setEditable(false);
        chooseIfcPanel.add(ifcText, new GridBagConstraints(1, 0,
                2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        browseBtn.addActionListener(this);
        chooseIfcPanel.add(browseBtn, new GridBagConstraints(3, 0,
                1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        checkBox.addActionListener(this);
        chooseIfcPanel.add(checkBox, new GridBagConstraints(0, 1,
                2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));

        //Панель языков
/*
        tb = BorderFactory.createTitledBorder(b, "Счётчики выполнения",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                Utils.getDefaultFont(), Utils.getDarkShadowSysColor());
*/
        progressPanel.setBorder(null);
        progressPanel.add(ifcLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        progressPanel.add(ifcCountLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        progressPanel.add(checkLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        progressPanel.add(checkCountLabel, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        progressPanel.add(new JLabel(""), new GridBagConstraints(4, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));

        //Статус выполнения
        tb = Utils.createTitledBorder(b, "Статус выполнения");
        statusPanel.setBorder(tb);
        statusPanel.setPreferredSize(new Dimension(600, 100));
        statusPanel.setMaximumSize(new Dimension(600, 100));
        statusPanel.setMinimumSize(new Dimension(600, 100));
        ifcProgress.setBorder(null);
        compProgress.setBorder(null);
        statusPanel.add(Utils.createLabel("Интерфейсы"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        ifcProgress.setPreferredSize(new Dimension(350, 17));
        statusPanel.add(ifcProgress, new GridBagConstraints(1, 0, 3, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        statusPanel.add(Utils.createLabel("Компоненты"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 5), 0, 0));
        compProgress.setPreferredSize(new Dimension(350, 17));
        statusPanel.add(compProgress, new GridBagConstraints(1, 1, 3, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        statusPanel.add(progressPanel, new GridBagConstraints(0, 2, 4, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));

        //Выбор языка
        tb = Utils.createTitledBorder(b, "Выбор языка");
        langPanel.setBorder(tb);
        langPanel.setPreferredSize(new Dimension(100, 100));
        langPanel.setMaximumSize(new Dimension(100, 100));
        langPanel.setMinimumSize(new Dimension(100, 100));
        langCombo.addActionListener(this);
        langPanel.add(langCombo, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));
        langPanel.add(langLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));

        //Панель кнопок
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(runBtn);
        runBtn.setPreferredSize(new Dimension(100, 30));
        runBtn.setMaximumSize(new Dimension(100, 30));
        runBtn.setMinimumSize(new Dimension(100, 30));
        runBtn.addActionListener(this);
        runBtn.setToolTipText("Запустить процесс отбора");
        runBtn.setEnabled(false);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(saveToFileBtn);
        saveToFileBtn.setPreferredSize(new Dimension(100, 30));
        saveToFileBtn.setMaximumSize(new Dimension(100, 30));
        saveToFileBtn.setMinimumSize(new Dimension(100, 30));
        saveToFileBtn.setEnabled(false);
        buttonPanel.add(Box.createVerticalStrut(10));

        //Результирующая таблица
        tb = Utils.createTitledBorder(b, "Результаты отбора");
        resultPanel.setBorder(tb);


        setLayout(new GridBagLayout());
        add(chooseIfcPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(statusPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(langPanel, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 0, 0, 0), 0, 0));
        add(buttonPanel, new GridBagConstraints(1, 0, 1, 3, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(5, 5, 0, 5), 0, 0));

        resultPanel.setPreferredSize(new Dimension(690, 450));
        resultPanel.setMaximumSize(new Dimension(690, 450));
        resultPanel.setMinimumSize(new Dimension(690, 450));
        resultPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        add(resultPanel, new GridBagConstraints(0, 2, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        setPreferredSize(new Dimension(700, 600));
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == checkBox) {
            ifcText.setEnabled(!checkBox.isSelected());
            browseBtn.setEnabled(!checkBox.isSelected());
            runBtn.setEnabled(checkBox.isSelected());
        } else if (src == browseBtn) {
            JScrollPane sp = new JScrollPane(tree);
            sp.setPreferredSize(new Dimension(500, 500));
            DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                    "Выбор интерфейса", sp);
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                InterfaceNode node = (InterfaceNode)tree.getSelectedNode();
                if (node.isLeaf()) {
                    ifcSingle = new Pair(node.toString(), node.getKrnObj());
                    ifcText.setText(ifcSingle.first.toString());
                    runBtn.setEnabled(true);
                } else {
                    MessagesFactory.showMessageDialog((Dialog)getTopLevelAncestor(),
                            MessagesFactory.ERROR_MESSAGE, "Невозможен выбор папки!");
                }
            }
        } else if (src == runBtn) {
            runBtn.setEnabled(false);
            //langPanel.setVisible(true);
            if (!checkBox.isSelected()) {
                new TranslateLoader((KrnObject)ifcSingle.second).start();
            } else {
                new TranslateLoader(null).start();
            }
        } else if (src == langCombo) {
            langLabel.setText(langCombo.getSelectedItem().toString());
        }
    }

    class ResultTableModel extends AbstractTableModel {

        private String[] COL_NAMES = {"Текст для перевода", "Перевод"};

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public int getRowCount() {
            return translateList.keySet().size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Object[] records = translateList.keySet().toArray();
            if (columnIndex == 0) {
                return records[rowIndex];
            } else {
                return null;
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    class TranslateLoader extends SwingWorker {

        private Kernel krn = Kernel.instance();
        private KrnObject singleIfc;
        private KrnObject[] interfaceObjs;
        private ArrayList interfaces = new ArrayList();

        public TranslateLoader(KrnObject singleIfc) {
            this.singleIfc = singleIfc;
            if (singleIfc == null) {
                try {
                    interfaceObjs = krn.getClassObjects(
                            krn.getClassByName("UI"), 0);
                    NodeFinder finder = new NodeFinder();
                    for (int i = 0; i < interfaceObjs.length; i++) {
                        KrnObject interfaceObj = interfaceObjs[i];
                        TreeNode node = finder.findFirst(
                                (TreeNode)tree.getModel().getRoot(),
                                new KrnObjectPattern(interfaceObj));
                        if (node != null && node.isLeaf()) {
                            interfaces.add(interfaceObj);
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
                ifcProgress.setValue(0);
                ifcProgress.setMinimum(0);
                ifcProgress.setMaximum(interfaces.size());
            }
        }

        public Object construct() {
            try {
                if (singleIfc != null) {
                    Element xml = null;
                    byte[] data = krn.getBlob(singleIfc, "config", 0, 0, 0);
                    if (data.length > 0) {
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
                        SAXBuilder b = new SAXBuilder();
                        xml = b.build(is).getRootElement();
                        is.close();
                        compProgress.setMinimum(0);
                        compProgress.setValue(0);
                        compProgress.setMaximum(Utils.getChildrenCount(xml));
                        loadList(xml, singleIfc);
                    }
                } else {
                    for (int i = 0; i < interfaces.size(); i++) {
                        ifcProgress.setValue(ifcProgress.getValue() + 1);
                        ifcCountLabel.setText(String.valueOf(ifcProgress.getValue()));
                        KrnObject krnObject = (KrnObject) interfaces.get(i);
                        Element xml = null;
                        byte[] data = krn.getBlob(krnObject, "config", 0, 0, 0);
                        if (data.length > 0) {
                            ByteArrayInputStream is = new ByteArrayInputStream(data);
                            SAXBuilder b = new SAXBuilder();
                            xml = b.build(is).getRootElement();
                            is.close();
                            compProgress.setMinimum(0);
                            compProgress.setValue(0);
                            compProgress.setMaximum(Utils.getChildrenCount(xml));
                            loadList(xml, krnObject);
                        }
                    }
                }
                runBtn.setEnabled(true);
                compProgress.setValue(0);
                ifcProgress.setValue(0);
                resultTable.setModel(new ResultTableModel());
                saveToFileBtn.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void loadList(Element xml, KrnObject obj) {
            Stack s = new Stack();
            s.push(xml);
            while (!s.isEmpty()) {
                Element el = (Element) s.pop();
                if ("Component".equals(el.getName())) {
                    compProgress.setValue(compProgress.getValue() + 1);
                    String title = "";
                    long ruId = LangItem.getByCode("RU").obj.id;
                    if ("Panel".equals(el.getAttributeValue("class")) ||
                            "Label".equals(el.getAttributeValue("class")) ||
                            "HyperLabel".equals(el.getAttributeValue("class")) ||
                            "Button".equals(el.getAttributeValue("class")) ||
                            "CheckBox".equals(el.getAttributeValue("class")) ||
                            "HyperPopup".equals(el.getAttributeValue("class"))) {
                        Element elem = el.getChild("title");
                        if (elem != null) {
                            if (elem.getChild("L"+ruId) != null) {
                                title = elem.getChild("L"+ruId).getValue();
                            }
                        }
                        elem = el.getChild("constraints");
                        if (elem != null) {
                            Element elem1 = elem.getChild("obligation");
                            if (elem1 != null) {
                                Element elem2 = elem1.getChild("message");
                                if (elem2 != null) {
                                    title = elem2.getValue();
                                }
                            }
                        }
                    } else if ("TextColumn".equals(el.getAttributeValue("class")) ||
                            "IntColumn".equals(el.getAttributeValue("class")) ||
                            "FloatColumn".equals(el.getAttributeValue("class")) ||
                            "DateColumn".equals(el.getAttributeValue("class")) ||
                            "MemoColumn".equals(el.getAttributeValue("class")) ||
                            "ComboColumn".equals(el.getAttributeValue("class")) ||
                            "CheckColumn".equals(el.getAttributeValue("class")) ||
                            "HyperColumn".equals(el.getAttributeValue("class")) ||
                            "PopupColumn".equals(el.getAttributeValue("class")) ||
                            "TreeColumn".equals(el.getAttributeValue("class")) ||
                            "DocFieldColumn".equals(el.getAttributeValue("class"))) {
                        title = el.getChild("header").getChild("text").getChild("L"+ruId).getValue();
                    }
                    if (title != null && !"".equals(title)) {
                        Collection coll = translateList.get(title);
                        if (coll == null) {
                            Set l = new HashSet();
                            l.add(obj);
                            translateList.put(title, l);
                            checkCountLabel.setText(
                                    String.valueOf(translateList.keySet().size()));
                        } else {
                            coll.add(obj);
                        }
                    }
                }
                java.util.List children = el.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element child = (Element) children.get(i);
                    s.push(child);
                }
            }
        }

    }

}
