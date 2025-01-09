package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.Rule;
import kz.tamur.guidesigner.xmldesigner.Corner;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.colorchooser.OrColorChooser;
import kz.tamur.util.editor.FontDialog;
import kz.tamur.util.editor.ParagraphDialog;
import kz.tamur.util.editor.FontCombo;
import kz.tamur.util.editor.OrTextPane;


import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import static kz.tamur.rt.Utils.setLastSelectDir;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getLightSysColor;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.getDefaultFont;

/**
 * Author: kazakbala
 * Date: 09.07.2004
 * Time: 17:33:53
 */
public class StyledEditor extends JPanel implements ActionListener {
    StyledDocument doc;
    private OrTextPane editor = new OrTextPane();
    private JPanel menuBar = new JPanel(new GridBagLayout());
    private JToolBar editMenu = Utils.createDesignerToolBar();
    private JToolBar toolBar = Utils.createDesignerToolBar();
    private JButton save = ButtonsFactory.createToolButton("Save", "Сохранить");
    private JButton open = ButtonsFactory.createToolButton("Open", "Открыть");
    private JButton fonts = ButtonsFactory.createToolButton("Fonts", "Формат шрифта");
    private JButton paragraph = ButtonsFactory.createToolButton("Paragraph", "Формат абзаца");
    private JButton image = ButtonsFactory.createToolButton("addImage", "Вставка рисунка");
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Коприрвать");
    private JButton cutBtn = ButtonsFactory.createToolButton("Cut", "Вырезать");
    private JButton pasteBtn = ButtonsFactory.createToolButton("Paste", "Вставить");
    private JButton findBtn = ButtonsFactory.createToolButton("Find", "Найти");
    private JButton replaceBtn = ButtonsFactory.createToolButton("S&R", "Заменить");

    private FontCombo fontFace = new FontCombo();
    private JComboBox fontSize = new JComboBox();

    private ButtonsFactory.ToggleButton fontBold = ButtonsFactory.createToggleButton(false, "fontBold", "Полужирный");
    private ButtonsFactory.ToggleButton fontItalic = ButtonsFactory.createToggleButton(false, "fontItalic", "Курсив");
    private ButtonsFactory.ToggleButton fontUnderline = ButtonsFactory.createToggleButton(false, "fontUnderLine", "Подчеркнутый");

    private ButtonsFactory.ToggleButton fontLeftAlign = ButtonsFactory.createToggleButton(false, "pLeft", "По левому краю");
    private ButtonsFactory.ToggleButton fontCenterAlign = ButtonsFactory.createToggleButton(false, "pCenter", "По центру");
    private ButtonsFactory.ToggleButton fontRightAlign = ButtonsFactory.createToggleButton(false, "pRight", "По правому краю");

    private JButton fontColor = ButtonsFactory.createToolButton("fontColor", "Цвет шрифта");
    private String[] fontSizes = new String[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26",
                                              "28", "36", "48", "72"};
    private JToggleButton isMetric;
    protected int m_xStart = -1;
    protected int m_xFinish = -1;
    protected boolean m_skipUpdate;
    protected String fontName = "";
    protected int fontSz = 0;
    private String[] allFonts = new String[]{"Arial", "Arial Black", "Century Gothic", "Comic Sans MS", "Garamond",
                                     "Monotype Corsiva", "Tahoma", "Times New Roman", "Verdana"};;
    private Rule colRule = new Rule(Rule.HORIZONTAL, true);
    private Rule rowRule = new Rule(Rule.VERTICAL, true);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public StyledEditor(Document doc) {
        menuBar.setOpaque(isOpaque);
        setOpaque(isOpaque);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        RTFEditorKit kit = new RTFEditorKit();
        editor.setEditorKit(kit);
        if (doc != null)
            editor.setDocument(doc);
        else
            editor.setDocument(new DefaultStyledDocument());
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
        toolBar.add(fonts);
        toolBar.add(paragraph);
        toolBar.add(image);
        toolBar.addSeparator();

        fontSize = new JComboBox(fontSizes);
        fontSize.setEditable(true);
        fontSize.setFont(getDefaultFont());
        fontSize.setPreferredSize(new Dimension(50, 21));
        fontSize.setMaximumSize(new Dimension(50, 21));
        fontSize.setMinimumSize(new Dimension(50, 21));
        fontFace.setPreferredSize(new Dimension(150, 21));
        fontFace.setMaximumSize(new Dimension(150, 21));
        fontFace.setMinimumSize(new Dimension(150, 21));
        editMenu.add(createLabel("Шрифт "));
        editMenu.add(fontFace);
        editMenu.add(createLabel(" Размер "));
        editMenu.add(fontSize);
        editMenu.addSeparator();
        editMenu.add(fontBold);
        editMenu.add(fontItalic);
        editMenu.add(fontUnderline);
        editMenu.addSeparator();
        editMenu.add(fontLeftAlign);
        editMenu.add(fontCenterAlign);
        editMenu.add(fontRightAlign);
        editMenu.addSeparator();
        editMenu.add(fontColor);
        editMenu.setMaximumSize(new Dimension(450, editMenu.getHeight()));

        save.addActionListener(this);
        open.addActionListener(this);
        fontSize.addActionListener(this);
        fontFace.addActionListener(this);
        fontBold.addActionListener(this);
        fontItalic.addActionListener(this);
        fontUnderline.addActionListener(this);
        fontLeftAlign.addActionListener(this);
        fontCenterAlign.addActionListener(this);
        fontRightAlign.addActionListener(this);
        paragraph.addActionListener(this);
        fonts.addActionListener(this);
        image.addActionListener(this);
        copyBtn.addActionListener(this);
        pasteBtn.addActionListener(this);
        cutBtn.addActionListener(this);
        fontColor.addActionListener(this);
        findBtn.addActionListener(this);
        replaceBtn.addActionListener(this);
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
        JScrollPane sp = new JScrollPane(editor);
        colRule.setPreferredWidth(editor.getWidth());
        sp.setColumnHeaderView(colRule);
        rowRule.setPreferredHeight(editor.getHeight());
        sp.setRowHeaderView(rowRule);
        JPanel buttonCorner = new JPanel();
        buttonCorner.setBackground(getLightSysColor());
        editor.setBorder(BorderFactory.createLineBorder(getDarkShadowSysColor()));
/*
        isMetric = new JToggleButton("cm", true);
        isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
        isMetric.setMargin(new Insets(0,0,0,0));
        isMetric.addItemListener(new UnitsListener());
        //buttonCorner.add(isMetric); //Use the default FlowLayout
*/
        sp.setCorner(JScrollPane.UPPER_LEFT_CORNER, buttonCorner);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
        sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());
        add(sp, BorderLayout.CENTER);
        editor.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (m_xStart >= 0 && m_xFinish >= 0)
                    if (editor.getCaretPosition() == m_xStart) {
                        editor.setCaretPosition(m_xFinish);
                        editor.moveCaretPosition(m_xStart);
                    } else
                        editor.select(m_xStart, m_xFinish);
            }

            public void focusLost(FocusEvent e) {
                m_xStart = editor.getSelectionStart();
                m_xFinish = editor.getSelectionEnd();
            }
        });
        editor.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                showAttributes(e.getDot());
            }
        });

        editor.requestFocusInWindow();

    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(fontSize)) {
            JComboBox cb = (JComboBox) obj;
            String str = (String) cb.getSelectedItem().toString();
            int size = Integer.parseInt(str);
            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontSize(attr, size);
            setAttributeSet(attr);
            editor.grabFocus();
        } else if (obj.equals(fontFace)) {
            JComboBox cb = (JComboBox) e.getSource();
            String font = (String) cb.getSelectedItem().toString();
            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, font);
            setAttributeSet(attr);
            editor.grabFocus();
        } else if (obj.equals(fontBold)) {
            SimpleAttributeSet bold = new SimpleAttributeSet();
            StyleConstants.setBold(bold, fontBold.isSelected());
            setAttributeSet(bold);
            editor.grabFocus();
        } else if (obj.equals(fontItalic)) {
            SimpleAttributeSet italic = new SimpleAttributeSet();
            StyleConstants.setItalic(italic, fontItalic.isSelected());
            setAttributeSet(italic);
            editor.grabFocus();
        } else if (obj.equals(fontUnderline)) {
            SimpleAttributeSet underline = new SimpleAttributeSet();
            StyleConstants.setUnderline(underline, fontUnderline.isSelected());
            setAttributeSet(underline);
            editor.grabFocus();
        } else if (obj.equals(fontLeftAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_LEFT);
            editor.grabFocus();
        } else if (obj.equals(fontRightAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_RIGHT);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_RIGHT);
            editor.grabFocus();
        } else if (obj.equals(fontCenterAlign)) {
            Element el = getDocument().getCharacterElement(editor.getSelectionStart());
            AttributeSet a = el.getAttributes();
            MutableAttributeSet attr = new SimpleAttributeSet(a);
            StyleConstants.setAlignment(attr, StyleConstants.ALIGN_CENTER);
            setAttributeSet(attr, true);
            showAlignBtn(StyleConstants.ALIGN_CENTER);
            editor.grabFocus();
        } else if (obj.equals(save)) {
            JFileChooser fc = Utils.createSaveChooser(Constants.STYLEDTEXT_FILTER);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                	String name = ((OrFileChooserUI) fc.getUI()).getFileName() + ".stt";
                	name = name.replace("/", "").replace("\\", "");
                	if (Funcs.isValid(name)) {
	                	File f = Funcs.getCanonicalFile(Funcs.getCanonicalFile(fc.getCurrentDirectory().getAbsolutePath()), name);
	                    FileOutputStream fstrm = new FileOutputStream(f);
	                    ObjectOutput ostrm = new ObjectOutputStream(fstrm);
	                    ostrm.writeObject(editor.getDocument());
	                    ostrm.flush();
	                    ostrm.close();
                	}
                } catch (IOException io) {
                    // should put in status panel
                    System.err.println("IOException: " + io.getMessage());
                }
            }
        } else if (obj.equals(open)) {
            JFileChooser fc = Utils.createOpenChooser(Constants.STYLEDTEXT_FILTER);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                ObjectInputStream istrm = null;
                try {
                    File f = fc.getSelectedFile();
                    setLastSelectDir(f.getParentFile().getCanonicalPath());
                    FileInputStream fin = new FileInputStream(f);
                    istrm = new ObjectInputStream(fin);
                    Document doc = (Document) istrm.readObject();
                    if (doc != null)
                        editor.setDocument(doc);
                } catch (IOException io) {
                    System.err.println("IOException: " + io.getMessage());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                	kz.tamur.rt.Utils.closeQuietly(istrm);
                }
            }
        } else if (obj.equals(paragraph)) {
            ParagraphDialog paraDlg;
            if (getTopLevelAncestor() instanceof JFrame) {
                paraDlg = new ParagraphDialog((Frame) getTopLevelAncestor());
            } else {
                paraDlg = new ParagraphDialog((Dialog) getTopLevelAncestor());
            }
            AttributeSet a = getDocument().getCharacterElement(editor.getCaretPosition()).getAttributes();
            paraDlg.setAttributes(a);
            paraDlg.setLocation(Utils.getCenterLocationPoint(paraDlg.getSize()));
            paraDlg.setVisible(true);
            if (paraDlg.getOption() == JOptionPane.OK_OPTION) {
                setAttributeSet(paraDlg.getAttributes(), true);
                showAttributes(editor.getCaretPosition());
            }
            editor.grabFocus();
        } else if (obj.equals(fonts)) {
            FontDialog dlg;
            if (getTopLevelAncestor() instanceof JFrame) {
                dlg = new FontDialog((Frame) getTopLevelAncestor(), allFonts, fontSizes);
            } else {
                dlg = new FontDialog((Dialog) getTopLevelAncestor(), allFonts, fontSizes);
            }
            AttributeSet a = getDocument().getCharacterElement(editor.getCaretPosition()).getAttributes();
            dlg.setAttributes(a);
            dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.setVisible(true);
            if (dlg.getOption() == JOptionPane.OK_OPTION) {
                setAttributeSet(dlg.getAttributes());
                showAttributes(editor.getCaretPosition());
            }
            editor.grabFocus();
        } else if (obj.equals(image)) {
            JFileChooser fc = Utils.createOpenChooser(Constants.IMAGE_FILTER);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File imgFile = fc.getSelectedFile();
                ImageIcon img = new ImageIcon(imgFile.getPath());
                int w = img.getIconWidth();
                int h = img.getIconHeight();
                if (w <= 0 || h <= 0) {
                    System.out.println("Ошибка при загрузке рисунка!");
                    return;
                }
                MutableAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setIcon(attr, img);
                int p = editor.getCaretPosition();
                try {
                    getDocument().insertString(p, " ", attr);
                } catch (BadLocationException ex) {
                }
            }
        } else if (obj.equals(copyBtn)) {
            editor.copy();
        } else if (obj.equals(pasteBtn)) {
            editor.paste();
        } else if (obj.equals(cutBtn)) {
            editor.cut();
        } else if (obj.equals(fontColor)) {
            AttributeSet a = getDocument().getCharacterElement(editor.getCaretPosition()).getAttributes();
            MutableAttributeSet attrs = new SimpleAttributeSet(a);
            Color color = StyleConstants.getForeground(a);
            OrColorChooser colorDlg = new OrColorChooser(color);
            DesignerDialog dlg;
            if (getTopLevelAncestor() instanceof JFrame) {
                dlg = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор цвета", colorDlg, false, true);
            } else {
                dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор цвета", colorDlg, false, true);
            }
            dlg.show();
            if (dlg.isOK()) {
                color = colorDlg.getColor();
                StyleConstants.setForeground(attrs, color);
                setAttributeSet(attrs);
            }
        } else if (obj.equals(findBtn) || obj.equals(replaceBtn)) {
            FindDialog findDlg = new FindDialog(editor, obj.equals(findBtn) ? 0 : 1, "");
            findDlg.setLocation(Utils.getCenterLocationPoint(findDlg.getSize()));
            findDlg.setVisible(true);
        }
    }

    public StyledDocument getDocument() {
        return (StyledDocument) editor.getDocument();
    }

    protected void setAttributeSet(AttributeSet attr) {
        setAttributeSet(attr, false);
    }

    protected void setAttributeSet(AttributeSet attr,
                                   boolean setParagraphAttributes) {
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
                    xFinish - xStart, attr, false);
        } else if (xStart != xFinish)
            getDocument().setCharacterAttributes(xStart,
                    xFinish - xStart, attr, false);
    }

    protected void showAttributes(int p) {
        m_skipUpdate = true;
        AttributeSet a = getDocument().getCharacterElement(p).
                getAttributes();
        String name = StyleConstants.getFontFamily(a);
        if (!fontName.equals(name)) {
            fontName = name;
            fontFace.setSelectedItem(name);
        }
        int size = StyleConstants.getFontSize(a);
        if (fontSz != size) {
            fontSz = size;
            fontSize.setSelectedItem(Integer.toString(fontSz));
        }
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

    public void setContentType(String type) {
        editor.setContentType(type);
    }

    public OrTextPane getEditor() {
        return editor;
    }
}
