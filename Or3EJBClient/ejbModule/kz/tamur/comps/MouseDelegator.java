package kz.tamur.comps;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.03.2004
 * Time: 20:15:16
 * To change this template use File | Settings | File Templates.
 */
public class MouseDelegator implements MouseListener, MouseMotionListener {
    private MouseTarget trg;

    public MouseDelegator(MouseTarget trg) {
        this.trg = trg;
    }

    public void mouseClicked(MouseEvent e) {
        trg.delegateMouseEvent(e);
    }

    public void mousePressed(MouseEvent e) {
        trg.delegateMouseEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
        trg.delegateMouseEvent(e);
    }

    public void mouseEntered(MouseEvent e) {
        trg.delegateMouseEvent(e);
    }

    public void mouseExited(MouseEvent e) {
        trg.delegateMouseEvent(e);
    }

    public void mouseDragged(MouseEvent e) {
        trg.delegateMouseMotionEvent(e);
    }

    public void mouseMoved(MouseEvent e) {
        trg.delegateMouseMotionEvent(e);
    }
}
