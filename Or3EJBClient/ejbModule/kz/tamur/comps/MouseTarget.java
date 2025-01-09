package kz.tamur.comps;

import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 30.03.2004
 * Time: 10:13:50
 * To change this template use File | Settings | File Templates.
 */
public interface MouseTarget {
    void delegateMouseEvent(MouseEvent e);
    void delegateMouseMotionEvent(MouseEvent e);
}
