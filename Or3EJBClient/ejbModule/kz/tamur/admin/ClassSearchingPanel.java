package kz.tamur.admin;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.rt.Utils.createCombo;
import static kz.tamur.rt.Utils.createDesignerTextField;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getMidSysColor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.FindPattern;
import kz.tamur.guidesigner.LongPattern;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.StringPattern;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class ClassSearchingPanel extends JPanel implements ActionListener {

    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private ClassTree classTree;
    private TreeNodeFinder treeNodeFinder;
    private JComboBox modeCombo = createCombo();
    private JComboBox conditionCombo = createCombo();
    private Dimension dimension = new Dimension(600, 60);
    private Dimension fieldDimension = new Dimension(300, 20);
    private JTextField searchingClassName = createDesignerTextField();
    private JButton searchBtn = ButtonsFactory.createToolButton("ClassSearchIcon", ".png", "Найти");
    private JButton nextOneBtn = ButtonsFactory.createToolButton("NextClassSearchIcon", ".png", "Следующий");
    private Border border = Utils.createTitledBorder(BorderFactory.createLineBorder(getMidSysColor()), "Поиск класса");

    public ClassSearchingPanel(ClassTree classTree) {
        super(new GridBagLayout());
        this.classTree = classTree;
        treeNodeFinder = new TreeNodeFinder(classTree);
        init();
    }

    private void init() {
        setBorder(border);
        setOpaque(isOpaque);
        Utils.setAllSize(this, dimension);
        Utils.setAllSize(searchingClassName, fieldDimension);
        Border lineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        CompoundBorder border = new CompoundBorder(lineBorder, emptyBorder);
        searchingClassName.setBorder(border);
        searchingClassName.setFont(new Font("Arial", Font.PLAIN, 10));
        searchingClassName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && searchBtn.isEnabled()) {
                    searchBtn.doClick();
                }
            }
        });

        searchingClassName.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                setButtonsEnable();
            }

            public void insertUpdate(DocumentEvent e) {
                setButtonsEnable();
            }

            public void changedUpdate(DocumentEvent e) {
                setButtonsEnable();
            }

            private void setButtonsEnable() {
                searchBtn.setEnabled(searchingClassName.getText().trim().length() > 0);
            }
        });

        modeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                conditionCombo.setEnabled(modeCombo.getSelectedIndex() != 1);
                searchingClassName.setText("");
                searchBtn.setEnabled(false);
                nextOneBtn.setEnabled(false);
            }
        });

        searchBtn.setEnabled(false);
        nextOneBtn.setEnabled(false);
        searchBtn.addActionListener(this);
        nextOneBtn.addActionListener(this);

        modeCombo.addItem("Название класса");
        modeCombo.addItem("ID класса");

        conditionCombo.addItem("С начала");
        conditionCombo.addItem("Содержит");
        conditionCombo.addItem("Совпадает");

        add(searchingClassName, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, BOTH, Constants.INSETS_0, 0, 0));
        add(searchBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 1, 0, 0), 0, 0));
        add(nextOneBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(0, 1, 0, 0), 0, 0));
        add(modeCombo, new GridBagConstraints(3, 0, 1, 1, 0, 0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
        add(conditionCombo, new GridBagConstraints(4, 0, 1, 1, 0, 0, WEST, NONE, new Insets(0, 10, 0, 0), 0, 0));
    }

    private int getCondition() {
        if (conditionCombo.getSelectedIndex() == 0) {
            return ComparisonOperations.SEARCH_START_WITH;
        } else if (conditionCombo.getSelectedIndex() == 1) {
            return ComparisonOperations.CO_CONTAINS;
        } else {
            return ComparisonOperations.CO_EQUALS;
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        try {
            if (source == searchBtn) {
                ClassNode node = classTree.getSelectedNode();
                if (node == null) {
                    node = (ClassNode) classTree.getModel().getRoot();
                }
                String searchingText = searchingClassName.getText().trim();
                if (searchingText.isEmpty()) {
                    return;
                }
                ClassNode classNode = null;
                if (modeCombo.getSelectedIndex() == 0) {
                    classNode = treeNodeFinder.findTreeNode(new StringPattern(searchingText, getCondition()), node);
                } else {
                    long id = Long.parseLong(searchingText);
                    classNode = treeNodeFinder.findTreeNode(new LongPattern(id), node);
                }
                if (classNode == null) {
                    MessagesFactory.showMessageNotFound(getTopLevelAncestor());
                } else {
                    if (node == classNode) {
                        MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.INFORMATION_MESSAGE,
                                "Выбранный класс соответствует критериям поиска.");
                    }
                    classTree.setSelectedPath(classNode);
                }
            } else if (source == nextOneBtn) {
                ClassNode node = treeNodeFinder.getOne();
                if (node != null) {
                    classTree.setSelectedPath(node);
                }
            }
        } catch (KrnException exception) {
            exception.printStackTrace();
        }
    }

    class TreeNodeFinder {
        private ClassTree classTree;
        private FindPattern pattern;
        private List<ClassNode> nodes = new ArrayList<ClassNode>();

        public TreeNodeFinder(ClassTree classTree) {
            this.classTree = classTree;
        }

        public ClassNode findTreeNode(FindPattern pattern, ClassNode node) {
            this.pattern = pattern;
            nodes.clear();
            if (pattern.isMatches(node)) {
                nodes.add(node);
            }
            find(node);
            return getOne();
        }

        private ClassNode getOne() {
            ClassNode node = null;
            try {
                node = nodes.remove(0);
            } catch (IndexOutOfBoundsException e) {
            }
            nextOneBtn.setEnabled(nodes.size() > 0);
            return node;
        }

        private void find(ClassNode node) {
            for (int i = 0; i < node.getChildCount(); i++) {
                ClassNode classNode = (ClassNode) classTree.getModel().getChild(node, i);
                if (pattern.isMatches(classNode)) {
                    nodes.add(classNode);
                }
                find(classNode);
            }
        }
    }
}
