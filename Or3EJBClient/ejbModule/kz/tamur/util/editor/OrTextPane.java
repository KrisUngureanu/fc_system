package kz.tamur.util.editor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 28.06.2005
 * Time: 15:47:38
 * To change this template use File | Settings | File Templates.
 */
public class OrTextPane extends JTextPane {
    public int m_xStart;
    public int m_xFinish;
    
    public OrTextPane() {
        super();
    }
    
    public void setSelection(int xStart, int xFinish, boolean moveUp) {
        if (moveUp) {
            setCaretPosition(xFinish);
            moveCaretPosition(xStart);
        } else
            select(xStart, xFinish);
        m_xStart = getSelectionStart();
        m_xFinish = getSelectionEnd();
    }
}
