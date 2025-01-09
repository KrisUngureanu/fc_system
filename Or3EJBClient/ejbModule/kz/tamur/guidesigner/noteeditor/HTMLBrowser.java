/*
 * 
 */
package kz.tamur.guidesigner.noteeditor;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.editor.OrTextPane;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * The Class HTMLBrowser.
 */
public class HTMLBrowser extends JPanel implements HyperlinkListener {

    /** The html e. */
    private HTMLEditor htmlEditor = null;

    /** Редактор текста подсказки. */
    private OrTextPane editor = new OrTextPane();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    /**
     * Конструктор класса.
     * 
     * @param htmlEditor
     *            the html e
     */
    public HTMLBrowser(HTMLEditor htmlEditor) {
        super(new GridBagLayout());
        this.htmlEditor = htmlEditor;
        init();
    }

    /**
     * Инициализация класса.
     */
    private void init() {
        JScrollPane scrolleditor = new JScrollPane(editor);
        Dimension fieldSize = new Dimension(360, 200);
        editor.setEditable(true);
        editor.setBackground(new Color(255, 255, 225));
        editor.setContentType("html/text; charset=Cp1251");
        editor.setEditorKit(new OrHTMLEditorKit());
        editor.setDocument(kz.tamur.rt.Utils.createHTMLDocument());
        scrolleditor.setPreferredSize(fieldSize);
        add(scrolleditor, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));

        editor.addFocusListener(new FocusListener() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
             * 
             * Получение фокуса компонентом
             */
            public void focusGained(FocusEvent e) {
                if (htmlEditor.m_xStart >= 0 && htmlEditor.m_xFinish >= 0)
                    if (editor.getCaretPosition() == htmlEditor.m_xStart) {
                        editor.setCaretPosition(htmlEditor.m_xFinish);
                        editor.moveCaretPosition(htmlEditor.m_xStart);
                    } else {
                        editor.select(htmlEditor.m_xStart, htmlEditor.m_xFinish);
                    }
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
             * 
             * Потеря фокуса компонентом
             */
            public void focusLost(FocusEvent e) {
                htmlEditor.m_xStart = editor.getSelectionStart();
                htmlEditor.m_xFinish = editor.getSelectionEnd();
            }
        });
        setOpaque(isOpaque);
        scrolleditor.setOpaque(isOpaque);
        scrolleditor.getViewport().setOpaque(isOpaque);
    }

    /**
     * Получить editor.
     * 
     * @return the editor
     */
    public OrTextPane getEditor() {
        return editor;
    }

    /**
     * Получить html.
     * 
     * @return the html
     */
    public String getHTML() {
        return editor.getText();
    }
    public void setHTML(String html) {
        editor.setText(html);
    }

    
    public void hyperlinkUpdate(HyperlinkEvent e) {
    }
}
