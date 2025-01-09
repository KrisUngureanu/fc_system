package kz.tamur.guidesigner;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 28.02.2006
 * Time: 11:24:45
 * To change this template use File | Settings | File Templates.
 */
public class ComponentsTreeDlg extends JDialog {

    private Container topLevelAncestor;

    public ComponentsTreeDlg(Frame owner, String title, Container topLevelAncestor) throws HeadlessException {
        super(owner, title);
        this.topLevelAncestor = topLevelAncestor;
    }

    public void setTopLevelComp(Container c) {
        topLevelAncestor = c;
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (topLevelAncestor != null) {
                ((DesignerFrame)topLevelAncestor).setCompsTreeMode(false);
            }
        }
        super.processWindowEvent(e);
    }

}
