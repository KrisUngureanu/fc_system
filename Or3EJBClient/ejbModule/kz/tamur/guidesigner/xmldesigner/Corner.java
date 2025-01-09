package kz.tamur.guidesigner.xmldesigner;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 23.09.2005
 * Time: 16:49:19
 * To change this template use File | Settings | File Templates.
 */
public class Corner extends JComponent {

    public void paintComponent(Graphics g) {
        // Fill me with dirty brown/orange.
        g.setColor(kz.tamur.rt.Utils.getLightSysColor());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
}
