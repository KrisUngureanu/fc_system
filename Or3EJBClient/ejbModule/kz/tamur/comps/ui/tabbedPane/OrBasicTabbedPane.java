package kz.tamur.comps.ui.tabbedPane;

import javax.swing.JTabbedPane;

/**
 * The Class OrBasicTabbedPane.
 * 
 * @author Sergey Lebedev
 */
public class OrBasicTabbedPane extends JTabbedPane {

    protected boolean isOpaque = !kz.tamur.rt.MainFrame.TRANSPARENT_DIALOG;

    /**
     * Создание нового or basic tabbed pane.
     */
    public OrBasicTabbedPane() {
        super();
    }

    /**
     * Создание нового or basic tabbed pane.
     * 
     * @param tabPlacement
     *            the tab placement
     */
    public OrBasicTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }

    /**
     * Создание нового or basic tabbed pane.
     * 
     * @param tabPlacement
     *            the tab placement
     * @param tabLayoutPolicy
     *            the tab layout policy
     */
    public OrBasicTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }
}
