package kz.tamur.guidesigner.service;

import kz.tamur.guidesigner.ButtonsFactory;
import javax.swing.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 15.11.2004
 * Time: 11:27:47
 * To change this template use File | Settings | File Templates.
 */
public class ServicesController implements AWTEventListener {

    private MainFrame serviceFrame;

    public ServicesController(MainFrame serviceFrame) {
        this.serviceFrame = serviceFrame;
    }

    private void mouseMoved(MouseEvent e) {
        Component c = e.getComponent();
        if (c instanceof ButtonsFactory.DesignerToolButton ||
                c instanceof ButtonsFactory.DesignerCompButton) {
            serviceFrame.getStatusTextLab().setText(((AbstractButton)c).getToolTipText());
        } else {
            serviceFrame.getStatusTextLab().setText("");
        }
    }

    public void eventDispatched(AWTEvent event) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_MOVED :
                mouseMoved((MouseEvent)event);
                break;
        }
    }
}
