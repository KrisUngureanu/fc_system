package kz.tamur.comps;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 18.03.2004
 * Time: 17:31:09
 * To change this template use File | Settings | File Templates.
 */
public class Cursors {

    public static Cursor DND_OK;
    public static Cursor DND_ERR;
    public static Cursor PASTE;
    public static Cursor WAIT_CURSOR;

    static {
        ImageIcon img = new ImageIcon(Cursors.class.getResource("images/okDnd.gif")); 
        DND_OK = Toolkit.getDefaultToolkit().createCustomCursor(img.getImage(), new Point(0, 0), "okDnd");
        img = new ImageIcon(Cursors.class.getResource("images/errDnd.gif"));
        DND_ERR = Toolkit.getDefaultToolkit().createCustomCursor(img.getImage(), new Point(0, 0), "errDnd");
        PASTE = Toolkit.getDefaultToolkit().createCustomCursor(kz.tamur.rt.Utils.getImageIcon("PasteCursor").getImage(), new Point(0, 0), "Paste");
        WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    }
}
