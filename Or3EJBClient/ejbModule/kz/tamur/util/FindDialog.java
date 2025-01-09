package kz.tamur.util;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.VERTICAL;
import static kz.tamur.comps.Constants.INSETS_0;
import static kz.tamur.comps.Constants.INSETS_5;
import static kz.tamur.guidesigner.ButtonsFactory.createDialogButton;
import static kz.tamur.rt.Utils.getDefaultFont;
import static kz.tamur.rt.Utils.setAllSize;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import kz.tamur.admin.ClassBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.Utils;
import kz.tamur.util.editor.OrTextPane;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;

public class FindDialog extends JDialog implements ActionListener {
    private OrTextPane m_owner;
    private OrBasicTabbedPane mainTB = new OrBasicTabbedPane();
    private JComboBox textFieldFind1 = new JComboBox();
    private JComboBox textFieldFind2 = new JComboBox();
    private JComboBox textFieldReplace = new JComboBox();
    private JCheckBox chkWord = Utils.createCheckBox("Только слово целиком", false);
    private JCheckBox chkCase = Utils.createCheckBox("Учитывать регистр", false);
    private JRadioButton rdUp = new JRadioButton("Вверх");
    private JRadioButton rdDown = new JRadioButton("Вниз", true);
    
    private int m_searchIndex = -1;
    private boolean m_searchUp = false;
    private String m_searchData;
    private int index;
    private String oldkey = "";
    private String sel_str;
    private boolean start = false;
    private JButton btFind = createDialogButton(ButtonsFactory.BUTTON_FIND);
    private JButton btReplace = createDialogButton(ButtonsFactory.BUTTON_REPLACE);
    private JButton btFindNext = createDialogButton(ButtonsFactory.BUTTON_FIND_NEXT);
    private JButton btReplaceAll = createDialogButton(ButtonsFactory.BUTTON_REPLACEALL);
    private JButton btClose = createDialogButton(ButtonsFactory.BUTTON_CLOSE);
    private JButton browseBtn = ButtonsFactory.createToolButton("editor", "Выбрать", true);
    private JButton browseBtn2 = ButtonsFactory.createToolButton("editor", "Выбрать", true);
    private JButton browseReplBtn = ButtonsFactory.createToolButton("editor", "Выбрать", true);

    public FindDialog(OrTextPane owner, int index, String str) {
        super((Window) owner.getTopLevelAncestor(), "Найти и заменить");
        m_owner = owner;
        this.index = index;
        sel_str = str;
        init();
    }

    private void init() {
        JPanel findPan = new JPanel(new GridBagLayout());
        JPanel replacePan = new JPanel(new GridBagLayout());
        JPanel optionPan = new JPanel(new GridLayout(2, 1));
        JPanel directionPan = new JPanel(new GridLayout(2, 1));
        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        JLabel findLbl = Utils.createLabel("Найти: ");
        JLabel findLbl2 = Utils.createLabel("Найти: ");
        JLabel replaceLbl = Utils.createLabel("Заменить: ");
        ButtonGroup bg = new ButtonGroup();

        Dimension sizeTxtF = new Dimension(200, 22);
        Insets ins = new Insets(0, 0, 0, 5);
        Insets ins2 = new Insets(5, 5, 0, 5);

        setFont(getDefaultFont());
        setLayout(new GridBagLayout());

        mainTB.setFont(getDefaultFont());

        setAllSize(findPan, new Dimension(300, 100));

        findLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        findLbl2.setHorizontalAlignment(SwingConstants.RIGHT);
        replaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        setAllSize(textFieldFind1, sizeTxtF);
        setAllSize(textFieldFind2, sizeTxtF);
        setAllSize(textFieldReplace, sizeTxtF);
        textFieldFind2.setFont(getDefaultFont());
        textFieldFind1.setFont(getDefaultFont());
        textFieldReplace.setFont(getDefaultFont());
        chkWord.setFont(getDefaultFont());
        chkCase.setFont(getDefaultFont());

        textFieldFind1.setEditable(true);
        textFieldFind2.setEditable(true);
        textFieldReplace.setEditable(true);

        chkWord.setMnemonic('w');
        chkCase.setMnemonic('c');
        rdUp.setMnemonic('u');
        rdDown.setMnemonic('d');
        btFind.setMnemonic('f');
        btReplace.setMnemonic('r');
        btReplaceAll.setMnemonic('a');

        btClose.setDefaultCapable(true);
        textFieldFind2.addActionListener(this);
        btFind.addActionListener(this);
        btReplace.addActionListener(this);
        btFindNext.addActionListener(this);
        btReplaceAll.addActionListener(this);
        btClose.addActionListener(this);
        browseBtn.addActionListener(this);
        browseBtn2.addActionListener(this);
        browseReplBtn.addActionListener(this);

        optionPan.setBorder(new TitledBorder(new EtchedBorder(), "Параметры", TitledBorder.LEADING,
                TitledBorder.DEFAULT_POSITION, getDefaultFont()));
        directionPan.setBorder(new TitledBorder(new EtchedBorder(), "Направление", TitledBorder.LEADING,
                TitledBorder.DEFAULT_POSITION, getDefaultFont()));

        findPan.add(findLbl, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        findPan.add(textFieldFind1, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, ins, 0, 0));
        findPan.add(browseBtn, new GridBagConstraints(2, 0, 1, 1, 1, 1, CENTER, NONE, ins, 0, 0));
        
        replacePan.add(findLbl2, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        replacePan.add(textFieldFind2, new GridBagConstraints(1, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, ins, 0, 0));
        replacePan.add(browseBtn2, new GridBagConstraints(2, 0, 1, 1, 1, 1, CENTER, NONE, ins, 0, 0));
        
        replacePan.add(replaceLbl, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        replacePan.add(textFieldReplace, new GridBagConstraints(1, 1, 1, 1, 1, 1, CENTER, HORIZONTAL, ins, 0, 0));
        replacePan.add(browseReplBtn, new GridBagConstraints(2, 1, 1, 1, 1, 1, CENTER, NONE, ins, 0, 0));

        mainTB.addTab("Найти", findPan);
        mainTB.add("Заменить", replacePan);

        optionPan.add(chkWord);
        optionPan.add(chkCase);
        bg.add(rdUp);
        bg.add(rdDown);
        directionPan.add(rdUp);
        directionPan.add(rdDown);
        
        buttonsPanel.add(btFind, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, ins2, 0, 0));
        buttonsPanel.add(btReplace, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, NONE, ins2, 0, 0));
        buttonsPanel.add(btFindNext, new GridBagConstraints(0, 2, 1, 1, 0, 0, CENTER, NONE, ins2, 0, 0));
        buttonsPanel.add(btReplaceAll, new GridBagConstraints(0, 3, 1, 1, 0, 0, CENTER, NONE, ins2, 0, 0));
        buttonsPanel.add(btClose, new GridBagConstraints(0, 4, 1, 1, 0, 0, CENTER, NONE, ins2, 0, 0));
        buttonsPanel.add(new JLabel(" "), new GridBagConstraints(0, 5, 1, 1, 0, 1, CENTER, VERTICAL, INSETS_0, 0, 0));

        add(mainTB, new GridBagConstraints(0, 0, 2, 1, 1, 0, CENTER, HORIZONTAL, INSETS_5, 0, 0));
        add(optionPan, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, BOTH, INSETS_0, 0, 0));
        add(directionPan, new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, BOTH, INSETS_0, 0, 0));
        add(buttonsPanel, new GridBagConstraints(2, 0, 1, 2, 0, 1, CENTER, VERTICAL, INSETS_0, 0, 0));

        loadHistory(textFieldFind1);
        loadHistory(textFieldFind2);
        loadHistory(textFieldReplace);

        mainTB.setSelectedIndex(index);
        WindowListener flst = new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                m_searchIndex = -1;
                if (mainTB.getSelectedIndex() == 0) {
                    textFieldFind1.grabFocus();
                } else {
                    textFieldFind2.grabFocus();
                }
            }

            public void windowDeactivated(WindowEvent e) {
                m_searchData = null;
            }
        };

        if (index == 0) {
            textFieldFind1.insertItemAt(sel_str, 0);
            start = true;
            textFieldFind1.setSelectedIndex(0);
            start = false;
            getRootPane().setDefaultButton(btFind);
        } else {
            textFieldFind2.insertItemAt(sel_str, 0);
            start = true;
            textFieldFind2.setSelectedIndex(0);
            getRootPane().setDefaultButton(btFindNext);
            start = false;
        }
        hideButtons(index);
        mainTB.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                OrBasicTabbedPane pane = (OrBasicTabbedPane) evt.getSource();
                int sel = pane.getSelectedIndex();
                if (sel == 0) {
                    getRootPane().setDefaultButton(btFind);
                    textFieldFind1.grabFocus();
                } else {
                    getRootPane().setDefaultButton(btFindNext);
                    textFieldFind2.grabFocus();
                }
                hideButtons(sel);
            }
        });
        Action closeFind = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        mainTB.getInputMap().put(KeyStroke.getKeyStroke("ESC"), "closefind");
        mainTB.getActionMap().put("clodeFind", closeFind);
        getRootPane().registerKeyboardAction((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        getRootPane().registerKeyboardAction((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findNext(false, true);
            }
        }), KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        getRootPane().registerKeyboardAction((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findNext(false, false);
            }
        }), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        addWindowListener(flst);
        pack();
        setResizable(false);
    }

    private void hideButtons(int idx) {
        btFind.setVisible(idx == 0);
        btFindNext.setVisible(idx == 1);
        btReplace.setVisible(idx == 1);
        btReplaceAll.setVisible(idx == 1);
    }

    private void loadHistory(JComboBox cmb) {
        try {
            File file = new File(cmb == textFieldFind1 || cmb == textFieldFind2 ? "find.log" : "replace.log");
            if (file != null && file.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String str;
                List<String> list = new ArrayList<String>();
                while ((str = in.readLine()) != null) {
                    if (!str.isEmpty()) {
                        list.add(str);
                    }
                }
                in.close();
                cmb.setModel(new DefaultComboBoxModel(list.toArray()));
            }
        } catch (IOException e) {
        }

    }

    public void setSelectedIndex(int index) {
        mainTB.setSelectedIndex(index);
        setVisible(true);
        m_searchIndex = -1;
    }

    public int findNext(boolean doReplace, boolean showWarnings) {
        OrTextPane monitor = m_owner;
        int pos = monitor.getCaretPosition();
        if (rdUp.isSelected() != m_searchUp) {
            m_searchUp = rdUp.isSelected();
            m_searchIndex = -1;
        }
        if (m_searchIndex == -1) {
            try {
                Document doc = m_owner.getDocument();
                m_searchData = m_searchUp ? doc.getText(0, pos) : doc.getText(pos, doc.getLength() - pos);
                m_searchIndex = pos;
            } catch (BadLocationException ex) {
                warning(ex.toString());
                return -1;
            }
        }
        String key = "";
        if (mainTB.getSelectedIndex() == 0) {
            key = textFieldFind1.getSelectedItem().toString();
            if (key.length() > 0 && !oldkey.equals(key)) {
                textFieldFind1.insertItemAt(key, 0);
                saveHistory(textFieldFind1, key);
                oldkey = key;
            }
        } else {
            key = textFieldFind2.getSelectedItem().toString();
            if (key.length() > 0 && !oldkey.equals(key)) {
                textFieldFind2.insertItemAt(key, 0);
                saveHistory(textFieldFind2, key);
                oldkey = key;
            }
        }

        if (key.length() == 0) {
            warning("Введите слово для поиска");
            return -1;
        }

        if (!chkCase.isSelected()) {
            if (m_searchData == null) {
                return 0;
            }
            m_searchData = m_searchData.toLowerCase(Constants.OK);
            key = key.toLowerCase(Constants.OK);
        }
        if (chkWord.isSelected()) {
            for (int k = 0; k < Utils.WORD_SEPARATORS.length; k++) {
                if (key.indexOf(Utils.WORD_SEPARATORS[k]) >= 0) {
                    warning("The text target contains an illegal " + "character \'" + Utils.WORD_SEPARATORS[k] + "\'");
                    return -1;
                }
            }
        }
        String replacement = "";
        if (doReplace) {
            if (textFieldReplace.getSelectedItem() == null || textFieldReplace.getSelectedItem().toString().equals("")) {
                warning("Введите слово для замены");
                return -1;
            }
            replacement = textFieldReplace.getSelectedItem().toString();
        }
        int xStart = -1;
        int xFinish = -1;
        while (true) {
            xStart = m_searchUp ? m_searchData.lastIndexOf(key, pos - 1) : m_searchData.indexOf(key, xStart == -1 ? pos
                    - m_searchIndex : pos);
            if (xStart < 0) {
                if (showWarnings) {
                    warning("Поиск завершен. Текст не найден.");
                }
                if (mainTB.getSelectedIndex() == 0)
                    textFieldFind1.grabFocus();
                else
                    textFieldFind2.grabFocus();
                return 0;
            }
            xFinish = xStart + key.length();
            if (chkWord.isSelected()) {
                boolean s1 = xStart > 0;
                boolean b1 = s1 && !Utils.isSeparator(m_searchData.charAt(xStart - 1));
                boolean s2 = xFinish < m_searchData.length();
                boolean b2 = s2 && !Utils.isSeparator(m_searchData.charAt(xFinish));

                if (b1 || b2) { // Не целое слово
                    if (m_searchUp && s1) {// Можно продолжать вверх
                        pos = xStart;
                        continue;
                    }
                    if (!m_searchUp && s2) {// Можно продолжать вниз
                        pos = xFinish;
                        continue;
                    }

                    if (showWarnings) { // Найдено, но не целое слово, нельзя продолжать поиск
                        warning("Текст не найден");
                    }
                    return 0;
                }
            }
            break;
        }
        if (!m_searchUp) {
            xStart += m_searchIndex;
            xFinish += m_searchIndex;
        }
        if (doReplace) {
            if (m_owner.getSelectedText() == null || !m_owner.getSelectedText().equals(key)) {
                m_owner.setSelection(xStart, xFinish, m_searchUp);
            }
            monitor.replaceSelection(replacement);
            m_owner.setSelection(xStart, xStart + replacement.length(), m_searchUp);
            m_searchIndex = -1;
        } else {
            m_owner.setSelection(xStart, xFinish, m_searchUp);
        }
        return 1;
    }

    private void saveHistory(JComboBox comboBox, String key) {
        try {
            File file = new File(comboBox == textFieldFind1 || comboBox == textFieldFind2 ? "find.log" : "replace.log");
            if (file != null) {
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < comboBox.getItemCount(); i++) {
                    out.write(comboBox.getItemAt(i).toString());
                    out.newLine();
                }
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void warning(String message) {
        MessagesFactory.showMessageDialog(m_owner.getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE, message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(btFind)) {
            if (mainTB.getSelectedIndex() == 0)
                findNext(false, true);
        } else if (obj.equals(btReplace)) {
            if (mainTB.getSelectedIndex() == 1)
                findNext(true, true);
        } else if (obj.equals(btFindNext)) {
            if (mainTB.getSelectedIndex() == 1)
                findNext(false, true);
        } else if (obj.equals(btReplaceAll)) {
            int counter = 0;
            while (true) {
                int result = findNext(true, false);
                if (result < 0) // error
                    return;
                else if (result == 0) // no more
                    break;
                counter++;
            }
            warning("Произведено замен:" + counter);
        } else if (obj.equals(btClose)) {
            setVisible(false);
        } else if (obj.equals(browseBtn) || obj.equals(browseBtn2) || obj.equals(browseReplBtn)) {
            try {
                ClassNode cls = Kernel.instance().getClassNodeByName("Объект");
                ClassBrowser cb = new ClassBrowser(cls, true);
                DesignerDialog dlg = new DesignerDialog(this, "Выберите класс", cb);
                dlg.show();
                if (dlg.isOK()) {
                    if (obj.equals(browseBtn)) {
                        textFieldFind1.getEditor().setItem(cb.getSelectedPath());
                    } else if (obj.equals(browseBtn2)) {
                        textFieldFind2.getEditor().setItem(cb.getSelectedPath());
                    } else {
                        textFieldReplace.getEditor().setItem(cb.getSelectedPath());
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }
}
