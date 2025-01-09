package kz.tamur;

import javax.swing.*;
import java.awt.*;
/**
 * User: Vital
 * Date: 21.02.2006
 * Time: 9:53:59
 */
public class FullScreenFrame extends JFrame {

    private static FullScreenFrame frame;
    private Container container;

    public static FullScreenFrame instance() {
        if (frame == null) {
            frame = new FullScreenFrame();
        }
        return frame;
    }


    FullScreenFrame() throws HeadlessException {
        super();
        container = getContentPane();
        container.setLayout(new BorderLayout());
        init();
    }

    private void init() {
        setSize(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay());
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
    }

    public void addPanel(Component c) {
        container.add(c, BorderLayout.CENTER);
        container.validate();
        container.repaint();
    }
}
