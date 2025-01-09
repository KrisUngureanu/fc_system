package kz.tamur.guidesigner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 28.02.2006
 * Time: 11:24:45
 * To change this template use File | Settings | File Templates.
 */
public class MultiInspectorDlg extends JDialog {

    private Container topLevelAncestor;
    private JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private Component c1;
    private Component c2;

    public MultiInspectorDlg(Frame owner, String title, Container topLevelAncestor) throws HeadlessException {
        super(owner, title);
        this.topLevelAncestor = topLevelAncestor;
        getContentPane().add(split);
        //setPreferredSize(new Dimension(800, 600));
        //setLocation(0, 0);
    }

    public void setComponents(Component c1, Component c2) {
        this.c1 = c1;
        this.c2 = c2;
        split.setLeftComponent(this.c1);
        split.setRightComponent(this.c2);
    }

    public void setTopLevelComp(Container c) {
        topLevelAncestor = c;
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (topLevelAncestor != null) {
                ((DesignerFrame)topLevelAncestor).setCompsTreeMode(false);
                ((DesignerFrame)topLevelAncestor).setInspector(false);
            }
        }
        super.processWindowEvent(e);
    }

    public Component getC1() {
        return c1;
    }

    public Component getC2() {
        return c2;
    }

}
