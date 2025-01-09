package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.Rule;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.FindDialog;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.colorchooser.OrColorChooser;
import kz.tamur.util.editor.OrTextPane;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Author: kazakbala
 * Date: 09.07.2004
 * Time: 17:33:53
 */
public class NoteEditor extends JPanel implements ActionListener {

    private JPanel menuBar = new JPanel(new GridBagLayout());
    private JToolBar editMenu = kz.tamur.comps.Utils.createDesignerToolBar();
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
    private JButton save = ButtonsFactory.createToolButton("Save", "Сохранить");
    private JButton open = ButtonsFactory.createToolButton("Open", "Открыть");

    private JButton image = ButtonsFactory.createToolButton("addImage", "Вставка рисунка");
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Копировать");
    private JButton cutBtn = ButtonsFactory.createToolButton("Cut", "Вырезать");
    private JButton pasteBtn = ButtonsFactory.createToolButton("Paste", "Вставить");
    private JButton findBtn = ButtonsFactory.createToolButton("Find", "Найти");
    private JButton replaceBtn = ButtonsFactory.createToolButton("S&R", "Заменить");
    private JButton attchBtn = ButtonsFactory.createToolButton("attachment", "Прикрепить файл");
    private JButton jumperBtn = ButtonsFactory.createToolButton("jumper", "Переход на другой раздел");
    private JButton hrBtn = ButtonsFactory.createToolButton("hr", "Вставить горизонтальную линию");

    private ButtonsFactory.ToggleButton fontBold = ButtonsFactory.createToggleButton(false, "fontBold", "Полужирный");
    private ButtonsFactory.ToggleButton fontItalic = ButtonsFactory.createToggleButton(false, "fontItalic", "Курсив");
    private ButtonsFactory.ToggleButton fontUnderline = ButtonsFactory.createToggleButton(false, "fontUnderLine", "Подчеркнутый");

    private ButtonsFactory.ToggleButton fontLeftAlign = ButtonsFactory.createToggleButton(false, "pLeft", "По левому краю");
    private ButtonsFactory.ToggleButton fontCenterAlign = ButtonsFactory.createToggleButton(false, "pCenter", "По центру");
    private ButtonsFactory.ToggleButton fontRightAlign = ButtonsFactory.createToggleButton(false, "pRight", "По правому краю");

    private StyledEditorKit.BoldAction actionFontBold;
    private StyledEditorKit.ItalicAction actionFontItalic;
    private StyledEditorKit.UnderlineAction actionFontUnderline;
    private StyledEditorKit.AlignmentAction actionAlignLeft;
    private StyledEditorKit.AlignmentAction actionAlignCenter;
    private StyledEditorKit.AlignmentAction actionAlignRight;
    private StyledEditorKit.AlignmentAction actionAlignJustified;

    private ButtonsFactory.ToggleButton fontColor = ButtonsFactory.createToggleButton(false, "fontColor", "Цвет шрифта");
    private JToggleButton isMetric;
    protected int m_xStart = -1;
    protected int m_xFinish = -1;
    protected boolean m_skipUpdate;

    protected int fontSz = 0;
    private Rule colRule = new Rule(Rule.HORIZONTAL, true);
    private Rule rowRule = new Rule(Rule.VERTICAL, true);
    private DesignerTree tree;
    private NoteTabbedContent noteTabPane = new NoteTabbedContent();

    private JMenuBar menu = new JMenuBar();
    private JMenu fileMenu = new JMenu("Файл");
    private JMenuItem openItem = createMenuItem("Открыть");
    private JMenuItem saveItem = createMenuItem("Сохранить");
    private JMenuItem closeItem = createMenuItem("Закрыть");

    private JMenu edit = new JMenu("Редактирование");
    private JMenuItem copyItem = createMenuItem("Копировать");
    private JMenuItem pasteItem = createMenuItem("Вставить");
    private JMenuItem cutItem = createMenuItem("Вырезать");

    private JMenu insert = new JMenu("Вставка");
    private JMenuItem imageItem = createMenuItem("Рисунок", "addImage");
    private JMenuItem lineItem = createMenuItem("Линия", "hr");
    private JMenuItem fileItem = createMenuItem("Файл", "attachment");

    public NoteEditor() {
        fileMenu.setFont(Utils.getDefaultFont());

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        closeItem.addActionListener(this);

        edit.setFont(Utils.getDefaultFont());

        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);
        cutItem.addActionListener(this);

        insert.setFont(Utils.getDefaultFont());

        imageItem.addActionListener(this);
        lineItem.addActionListener(this);
        fileItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);
        menu.add(fileMenu);

        edit.add(copyItem);
        edit.add(pasteItem);
        edit.addSeparator();
        edit.add(cutItem);
        menu.add(edit);

        insert.add(imageItem);
        insert.add(lineItem);
        insert.addSeparator();
        insert.add(fileItem);
        menu.add(insert);

        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        // создаем popup для меню - Файл
        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBar.add(open);
        toolBar.add(save);
        toolBar.addSeparator();
        toolBar.add(copyBtn);
        toolBar.add(pasteBtn);
        toolBar.add(cutBtn);
        toolBar.addSeparator();
        toolBar.add(findBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();

        toolBar.add(image);
        toolBar.add(hrBtn);
        toolBar.addSeparator();
        toolBar.add(attchBtn);
        toolBar.add(jumperBtn);
        toolBar.addSeparator();

        editMenu.add(fontBold);
        editMenu.add(fontItalic);
        editMenu.add(fontUnderline);
        editMenu.addSeparator();

        actionFontBold        = new StyledEditorKit.BoldAction();
        fontBold.setAction(actionFontBold);
        fontBold.setText(null);
        fontBold.setIcon(kz.tamur.rt.Utils.getImageIcon("fontBold"));

        actionFontItalic      = new StyledEditorKit.ItalicAction();
        fontItalic.setAction(actionFontItalic);
        fontItalic.setText(null);
        fontItalic.setIcon(kz.tamur.rt.Utils.getImageIcon("fontItalic"));

        actionFontUnderline   = new StyledEditorKit.UnderlineAction();
		fontUnderline.setAction(actionFontUnderline);
        fontUnderline.setText(null);
        fontUnderline.setIcon(kz.tamur.rt.Utils.getImageIcon("fontUnderLine"));


        actionAlignCenter = new StyledEditorKit.AlignmentAction("seredina", StyleConstants.ALIGN_CENTER);
        fontCenterAlign.setAction(actionAlignCenter);
        fontCenterAlign.setText(null);
        fontCenterAlign.setIcon(kz.tamur.rt.Utils.getImageIcon("pCenter"));

        actionAlignLeft = new StyledEditorKit.AlignmentAction("leftAlign", StyleConstants.ALIGN_LEFT);
        fontLeftAlign.setAction(actionAlignLeft);
        fontLeftAlign.setText(null);
        fontLeftAlign.setIcon(kz.tamur.rt.Utils.getImageIcon("pLeft"));

        actionAlignRight = new StyledEditorKit.AlignmentAction("rightAlign", StyleConstants.ALIGN_RIGHT);
        fontRightAlign.setAction(actionAlignRight);
        fontRightAlign.setText(null);
        fontRightAlign.setIcon(kz.tamur.rt.Utils.getImageIcon("pRight"));


        editMenu.add(fontLeftAlign);
        editMenu.add(fontCenterAlign);
        editMenu.add(fontRightAlign);
        editMenu.addSeparator();
        editMenu.add(fontColor);
        editMenu.setMaximumSize(new Dimension(450, editMenu.getHeight()));

        save.addActionListener(this);
        open.addActionListener(this);

        image.addActionListener(this);
        attchBtn.addActionListener(this);
        copyBtn.addActionListener(this);
        pasteBtn.addActionListener(this);
        cutBtn.addActionListener(this);
        fontColor.addActionListener(this);
        findBtn.addActionListener(this);
        replaceBtn.addActionListener(this);
        jumperBtn.addActionListener(this);
        hrBtn.addActionListener(this);
        //конец editMenu
        //JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //pane.add(toolBar);
        menuBar.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        menuBar.add(editMenu, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_1, 0, 0));
        menuBar.add(new JLabel(""), new GridBagConstraints(2, 0, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_1, 0, 0));

        add(menuBar, BorderLayout.NORTH);
        /*JScrollPane sp = new JScrollPane(editor);
        colRule.setPreferredWidth(editor.getWidth());
        sp.setColumnHeaderView(colRule);
        rowRule.setPreferredHeight(editor.getHeight());
        sp.setRowHeaderView(rowRule);
        JPanel buttonCorner = new JPanel();
        buttonCorner.setBackground(Utils.getLightSysColor());
        editor.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));*/
/*
        isMetric = new JToggleButton("cm", true);
        isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
        isMetric.setMargin(new Insets(0,0,0,0));
        isMetric.addItemListener(new UnitsListener());
        //buttonCorner.add(isMetric); //Use the default FlowLayout
*/
        /*sp.setCorner(JScrollPane.UPPER_LEFT_CORNER, buttonCorner);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
        sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());*/

        add(noteTabPane, BorderLayout.CENTER);
    }

    private DesignerTree getNotesTree() {
        DesignerTree noteTree = null;
        final Kernel krn = Kernel.instance();
        KrnClass cls = null;
        NoteNode inode = null;
        try {
            cls = krn.getClassByName("NoteRoot");
            KrnObject filterRoot = krn.getClassObjects(cls, 0)[0];
            long langId = DesignerFrame.instance().getInterfaceLang().id;
            long[] ids = {filterRoot.id};
            StringValue[] strs = krn.getStringValues(ids, cls.id, "title", langId,
                    false, 0);
            String title = "Не определён";
            if (strs.length > 0) {
                title = strs[0].value;
            }
            inode = new NoteNode(filterRoot, title, langId, 0);
            noteTree = new NoteTree(inode, langId);
            noteTree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return noteTree;
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        OrTextPane editor = getEditor();
        if (obj.equals(fontBold)) {
            SimpleAttributeSet bold = new SimpleAttributeSet();
            StyleConstants.setBold(bold, fontBold.isSelected());
            setAttributeSet(bold);
        } else if (obj.equals(fontItalic)) {
            SimpleAttributeSet italic = new SimpleAttributeSet();
            StyleConstants.setItalic(italic, fontItalic.isSelected());
            setAttributeSet(italic);
        } else if (obj.equals(fontUnderline)) {
            SimpleAttributeSet underline = new SimpleAttributeSet();
            StyleConstants.setUnderline(underline, fontUnderline.isSelected());
            setAttributeSet(underline);
        } else if (obj.equals(fontLeftAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_LEFT);
        } else if (obj.equals(fontRightAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_RIGHT);
        } else if (obj.equals(fontCenterAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_CENTER);
        } else if (obj.equals(save) || obj == saveItem) {
            noteTabPane.saveCurrent();
        } else if (obj == closeItem) {
            Container cont = this.getTopLevelAncestor();
            if (cont instanceof Frame) {
                ((Frame)cont).dispose();
            }
        } else if (obj.equals(open) || obj == openItem) {
            tree = getNotesTree();
            tree.setShowPopupEnabled(true);
            OpenElementPanel op = new OpenElementPanel(tree);
            DesignerDialog dlg = new DesignerDialog((Frame)getTopLevelAncestor(),
                    "Открытие справки", op);
            tree.requestFocusInWindow();
            dlg.show();
            if (dlg.isOK()) {
                load((NoteNode) tree.getSelectedNode());
            }
        } else if (obj.equals(image) || obj == imageItem) {
            JFileChooser fc = kz.tamur.comps.Utils.createOpenChooser(Constants.IMAGE_FILTER);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File imgFile = fc.getSelectedFile();
                Utils.setLastSelectDir(imgFile.getParentFile().toString());
                ImageIcon img = new ImageIcon(imgFile.getPath());
                int w = img.getIconWidth();
                int h = img.getIconHeight();
                if (w <= 0 || h <= 0) {
                    System.out.println("Ошибка при загрузке рисунка!");
                    return;
                }
                final Kernel krn = Kernel.instance();
                FileInputStream is = null;
                try {
                    is = new FileInputStream(imgFile);
                    byte[] buf = new byte[(int) imgFile.length()];
                    is.read(buf);
                    is.close();
                    String value = Utils.getImageToString(buf);
                     insertHTML("<img src=\""+value+"\" border=1>", HTML.Tag.IMG);
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } else if (obj.equals(copyBtn) || obj == copyItem) {
            editor.copy();
        } else if (obj.equals(pasteBtn) || obj == pasteItem) {
            editor.paste();
        } else if (obj.equals(cutBtn) || obj == cutItem) {
            editor.cut();
        } else if (obj.equals(fontColor)) {      
            OrColorChooser colorDlg = new OrColorChooser(null);
            DesignerDialog dlg;
            if (getTopLevelAncestor() instanceof JFrame) {
                dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор цвета", colorDlg, false, true);
            } else {
                dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор цвета", colorDlg, false, true);
            }
            dlg.show();
            if (dlg.isOK()) {
                Color color = colorDlg.getColor();
                if (color != null) {
                    StyledEditorKit.ForegroundAction customColorAction = new StyledEditorKit.ForegroundAction("CustomColor", color);
                    customColorAction.actionPerformed(e);
                    AttributeSet a = getDocument().getCharacterElement(editor.getCaretPosition()).getAttributes();
                    MutableAttributeSet attrs = new SimpleAttributeSet(a);
                    attrs.removeAttribute(HTML.Tag.FONT);
                    setAttributeSet(attrs, false, true);
                }

            }
        } else if (obj.equals(findBtn)||obj.equals(replaceBtn)) {
            FindDialog findDlg = new FindDialog(editor, obj.equals(findBtn)?0:1, "");
            findDlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(findDlg.getSize()));
            findDlg.setVisible(true);
        } else if (obj.equals(attchBtn)) {
            int objectId;
            JFileChooser fc = kz.tamur.comps.Utils.createOpenChooser(Constants.MSDOC_FILTER);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    if (file != null) {
                        final Kernel krn = Kernel.instance();
                        FileInputStream is = null;
                        is = new FileInputStream(file);
                        byte[] buf = new byte[(int) file.length()];
                        is.read(buf);
                        is.close();
                        KrnObject krnObj = krn.createObject(krn.getClassByName("MSDoc"), 0);
                        //krn.getAttributeByName()
                        //krn.setBlob()
                        krn.setBlob(krnObj.id, krnObj.classId, "file", 0, buf, 0, 0);
                        krn.setString(krnObj.id, krnObj.classId, "filename", 0, 0, file.getName(), 0);
                        insertHTML("<a href=\"file:" + krnObj.id + "\"> Файл <img src=\"attach\" border=0 align=\"absmiddle\"></a>", HTML.Tag.A);
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (obj.equals(jumperBtn)) {
            NotePageNode root = noteTabPane.getActiveRoot();
            PageNoteChooser ch = new PageNoteChooser(root);
            DesignerDialog dlg;
            if (getTopLevelAncestor() instanceof JFrame) {
                dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор раздела", ch, false, false);
            } else {
                dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор раздела", ch, false, false);
            }
            dlg.show();
            if (dlg.isOK()) {
                TreePath path = ch.getSelectedPath();
                String title = ch.getTitle();
                if (path != null) {
                    insertHTML("<a href=\"jump:" + path.toString() + "\">" + title + " <img src=\"jumper\" border=0 align=\"absmiddle\"></a>", HTML.Tag.A);
                }
            }
        } else if (hrBtn.equals(obj)) {
            insertHTML("<br><hr><br>", HTML.Tag.HR);
        }
    }

    public StyledDocument getDocument() {
        return (StyledDocument) getEditor().getDocument();
    }

    protected void setAttributeSet(AttributeSet attr) {
        setAttributeSet(attr, false);
    }

    protected void setAttributeSet(AttributeSet attr,
                                   boolean setParagraphAttributes) {
        setAttributeSet(attr, false, false);
    }

    protected void setAttributeSet(AttributeSet attr,
                                   boolean setParagraphAttributes, boolean replace) {
        OrTextPane editor = getEditor();
        if (m_skipUpdate)
            return;
        int xStart = editor.getSelectionStart();
        int xFinish = editor.getSelectionEnd();
        if (!editor.hasFocus()) {
            xStart = m_xStart;
            xFinish = m_xFinish;
        }
        if (setParagraphAttributes) {
            getDocument().setParagraphAttributes(xStart,
                    xFinish - xStart, attr, replace);
        } else if (xStart != xFinish)
            getDocument().setCharacterAttributes(xStart,
                    xFinish - xStart, attr, replace);
        editor.grabFocus();
    }

    protected void showAttributes(int p) {
        m_skipUpdate = true;
        AttributeSet a = getDocument().getCharacterElement(p).
                getAttributes();
        String name = StyleConstants.getFontFamily(a);

        boolean bold = StyleConstants.isBold(a);
        if (bold != fontBold.isSelected())
            fontBold.setSelected(bold);
        boolean italic = StyleConstants.isItalic(a);
        if (italic != fontItalic.isSelected())
            fontItalic.setSelected(italic);
        boolean underLine = StyleConstants.isUnderline(a);
        if (underLine != fontUnderline.isSelected())
            fontUnderline.setSelected(underLine);

        int align = StyleConstants.getAlignment(a);
        showAlignBtn(align);

        m_skipUpdate = false;
    }

    private void showAlignBtn(int align) {
        fontLeftAlign.setSelected(false);
        fontCenterAlign.setSelected(false);
        fontRightAlign.setSelected(false);
        switch (align) {
            case StyleConstants.ALIGN_LEFT:
                fontLeftAlign.setSelected(true);
                break;
            case StyleConstants.ALIGN_CENTER:
                fontCenterAlign.setSelected(true);
                break;
            case StyleConstants.ALIGN_RIGHT:
                fontRightAlign.setSelected(true);
                break;
        }
    }

    public void setSelection(int xStart, int xFinish, boolean moveUp) {

    }


    class UnitsListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Turn it to metric.
                rowRule.setIsMetric(true);
                colRule.setIsMetric(true);
            } else {
                // Turn it to inches.
                rowRule.setIsMetric(false);
                colRule.setIsMetric(false);
            }
            //editor.setMaxUnitIncrement(rowRule.getIncrement());
        }
    }

    private void load(NoteNode fnode) {
        noteTabPane.addNoteTab(fnode, this);

    }

    private OrTextPane getEditor() {
        return noteTabPane.getEditor();
    }

    private void insertHTML(String html, HTML.Tag tag) {
        OrTextPane editor = getEditor();
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
        int pos = editor.getCaretPosition();
        try {
            if (tag == HTML.Tag.A) {
                doc.insertString(pos, "  ", null);
                pos++;
            }
            kit.insertHTML(doc, pos, html, 0, 0, tag);
        } catch (BadLocationException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public JMenuBar getMenuBar() {
        return menu;
    }


}
