package kz.tamur.guidesigner.noteeditor;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.Rule;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.FindDialog;
import kz.tamur.util.colorchooser.OrColorChooser;
import kz.tamur.util.editor.OrTextPane;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

/**
 * Редактор текста
 * Разметка текста осуществяется с помошъю html
 */
public class HTMLEditor extends JPanel implements ActionListener {

    /** Панель меню */
    private JPanel menuBar = new JPanel(new GridBagLayout());

    /** Панель редактирования */
    private JToolBar editMenu = kz.tamur.comps.Utils.createDesignerToolBar();

    /** Панель инструментов*/
    private JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();

    /** Кнопка "Копировать"*/
    private JButton copyBtn = ButtonsFactory.createToolButton("Copy", "Копировать");

    /** Кнопка "Вырезать"*/
    private JButton cutBtn = ButtonsFactory.createToolButton("Cut", "Вырезать");

    /** Кнопка "Вставить" */
    private JButton pasteBtn = ButtonsFactory.createToolButton("Paste", "Вставить");

    /** Кнопка "Найти */
    private JButton findBtn = ButtonsFactory.createToolButton("Find", "Найти");

    /** Кнопка "Заменить" */
    private JButton replaceBtn = ButtonsFactory.createToolButton("S&R", "Заменить");

    /** Кнопка "Вставить горизонтальную линию" */
    private JButton hrBtn = ButtonsFactory.createToolButton("hr", "Вставить горизонтальную линию");

    /** Кнопка "Полужирный" */
    private ButtonsFactory.ToggleButton fontBold = ButtonsFactory.createToggleButton(false, "fontBold", "Полужирный");

    /** Кнопка "Курсив" */
    private ButtonsFactory.ToggleButton fontItalic = ButtonsFactory.createToggleButton(false, "fontItalic", "Курсив");

    /** Кнопка "Подчеркнутый" */
    private ButtonsFactory.ToggleButton fontUnderline = ButtonsFactory.createToggleButton(false, "fontUnderLine", "Подчеркнутый");

    /** Кнопка "По левому краю" */
    private ButtonsFactory.ToggleButton fontLeftAlign = ButtonsFactory.createToggleButton(false, "pLeft", "По левому краю");

    /** Кнопка "По центру" */
    private ButtonsFactory.ToggleButton fontCenterAlign = ButtonsFactory.createToggleButton(false, "pCenter", "По центру");

    /** Кнопка "По правому краю" */
    private ButtonsFactory.ToggleButton fontRightAlign = ButtonsFactory.createToggleButton(false, "pRight", "По правому краю");

    /** The action font bold. */
    private StyledEditorKit.BoldAction actionFontBold;

    /** The action font italic. */
    private StyledEditorKit.ItalicAction actionFontItalic;

    /** The action font underline. */
    private StyledEditorKit.UnderlineAction actionFontUnderline;

    /** The action align left. */
    private StyledEditorKit.AlignmentAction actionAlignLeft;

    /** The action align center. */
    private StyledEditorKit.AlignmentAction actionAlignCenter;

    /** The action align right. */
    private StyledEditorKit.AlignmentAction actionAlignRight;

    /** The action align justified. */
    private StyledEditorKit.AlignmentAction actionAlignJustified;

    /** Кнопка "Цвет шрифта" */
    private ButtonsFactory.ToggleButton fontColor = ButtonsFactory.createToggleButton(false, "fontColor", "Цвет шрифта");

    /** The is metric. */
    private JToggleButton isMetric;

    /** The m_x start. */
    protected int m_xStart = -1;

    /** The m_x finish. */
    protected int m_xFinish = -1;

    /** The m_skip update. */
    protected boolean m_skipUpdate;

    /** Кегль шрифта */
    protected int fontSz = 0;

    /** The col rule. */
    private Rule colRule = new Rule(Rule.HORIZONTAL, true);

    /** The row rule. */
    private Rule rowRule = new Rule(Rule.VERTICAL, true);

    /** The panel content. */
    private HTMLPanelContent panelContent = new HTMLPanelContent();
    
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    /**
     * Instantiates a new hTML editor.
     */
    public HTMLEditor() {
        setOpaque(isOpaque);
        menuBar.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        editMenu.setOpaque(isOpaque);
        toolBar.setBorderPainted(false);
        editMenu.setBorderPainted(false);
        Dimension btnSize = new Dimension(380, 250);
        this.setPreferredSize(btnSize);
        this.setMinimumSize(btnSize);
        this.setMaximumSize(btnSize);
        setLayout(new BorderLayout());

        toolBar.add(copyBtn);
        toolBar.add(pasteBtn);
        toolBar.add(cutBtn);
        toolBar.addSeparator();
        toolBar.add(findBtn);
        toolBar.add(replaceBtn);
        toolBar.addSeparator();

     //   toolBar.add(image);
        toolBar.add(hrBtn);
        toolBar.addSeparator();
    //    toolBar.add(attchBtn);
        toolBar.addSeparator();

        editMenu.add(fontBold);
        editMenu.add(fontItalic);
        editMenu.add(fontUnderline);
        editMenu.addSeparator();

        actionFontBold = new StyledEditorKit.BoldAction();
        fontBold.setAction(actionFontBold);
        fontBold.setText(null);
        fontBold.setIcon(kz.tamur.rt.Utils.getImageIcon("fontBold"));

        actionFontItalic = new StyledEditorKit.ItalicAction();
        fontItalic.setAction(actionFontItalic);
        fontItalic.setText(null);
        fontItalic.setIcon(kz.tamur.rt.Utils.getImageIcon("fontItalic"));

        actionFontUnderline = new StyledEditorKit.UnderlineAction();
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

      //  image.addActionListener(this);
      //  attchBtn.addActionListener(this);
        copyBtn.addActionListener(this);
        pasteBtn.addActionListener(this);
        cutBtn.addActionListener(this);
        fontColor.addActionListener(this);
        findBtn.addActionListener(this);
        replaceBtn.addActionListener(this);
        hrBtn.addActionListener(this);

        menuBar.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        menuBar.add(editMenu, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_1, 0, 0));
        menuBar.add(new JLabel(""), new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, Constants.INSETS_1, 0, 0));

        add(menuBar, BorderLayout.NORTH);
        add(panelContent, BorderLayout.CENTER);
        panelContent.newNoteBrowser(this);
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
        } else if (obj.equals(copyBtn)) {
            editor.copy();
        } else if (obj.equals(pasteBtn) ) {
            editor.paste();
        } else if (obj.equals(cutBtn)) {
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
                    StyledEditorKit.ForegroundAction customColorAction = new StyledEditorKit.ForegroundAction("CustomColor",
                            color);
                    customColorAction.actionPerformed(e);
                    AttributeSet a = getDocument().getCharacterElement(editor.getCaretPosition()).getAttributes();
                    MutableAttributeSet attrs = new SimpleAttributeSet(a);
                    attrs.removeAttribute(HTML.Tag.FONT);
                    setAttributeSet(attrs, false, true);
                }

            }
        } else if (obj.equals(findBtn) || obj.equals(replaceBtn)) {
            FindDialog findDlg = new FindDialog(editor, obj.equals(findBtn) ? 0 : 1, "");
            findDlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(findDlg.getSize()));
            findDlg.setVisible(true);
        } else if (hrBtn.equals(obj)) {
            insertHTML("<br><hr><br>", HTML.Tag.HR);
        }
    }

    /**
     * Gets the document.
     * 
     * @return the document
     */
    public StyledDocument getDocument() {
        return (StyledDocument) getEditor().getDocument();
    }
    
    /**
     * Получить html текст из редактора.
     *
     * @return the html
     */
    public String getHTML() {
        return panelContent.getHTML();
    }
    
    /**
     * Установить html текст в поле редактора.
     *
     * @param html the new html
     */
    public void setHTML(String html) {
        panelContent.setHTML(html);
    }

    
   
    /**
     * Sets the attribute set.
     * 
     * @param attr
     *            the new attribute set
     */
    protected void setAttributeSet(AttributeSet attr) {
        setAttributeSet(attr, false);
    }

    /**
     * Sets the attribute set.
     * 
     * @param attr
     *            the attr
     * @param setParagraphAttributes
     *            the set paragraph attributes
     */
    protected void setAttributeSet(AttributeSet attr, boolean setParagraphAttributes) {
        setAttributeSet(attr, false, false);
    }

    /**
     * Sets the attribute set.
     * 
     * @param attr
     *            the attr
     * @param setParagraphAttributes
     *            the set paragraph attributes
     * @param replace
     *            the replace
     */
    protected void setAttributeSet(AttributeSet attr, boolean setParagraphAttributes, boolean replace) {
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
            getDocument().setParagraphAttributes(xStart, xFinish - xStart, attr, replace);
        } else if (xStart != xFinish)
            getDocument().setCharacterAttributes(xStart, xFinish - xStart, attr, replace);
        editor.grabFocus();
    }

    /**
     * Show attributes.
     * 
     * @param p
     *            the p
     */
    protected void showAttributes(int p) {
        m_skipUpdate = true;
        AttributeSet a = getDocument().getCharacterElement(p).getAttributes();
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

    /**
     * Show align btn.
     * 
     * @param align
     *            the align
     */
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

    /**
     * Sets the selection.
     * 
     * @param xStart
     *            the x start
     * @param xFinish
     *            the x finish
     * @param moveUp
     *            the move up
     */
    public void setSelection(int xStart, int xFinish, boolean moveUp) {

    }

    /**
     * The listener interface for receiving units events.
     * The class that is interested in processing a units
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addUnitsListener<code> method. When
     * the units event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see UnitsEvent
     */
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
        }
    }


    private OrTextPane getEditor() {
        return panelContent.getEditor();
    }

    /**
     * Insert html.
     * 
     * @param html
     *            the html
     * @param tag
     *            the tag
     */
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
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
