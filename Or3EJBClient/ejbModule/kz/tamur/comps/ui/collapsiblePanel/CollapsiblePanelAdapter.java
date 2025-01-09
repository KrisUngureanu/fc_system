package kz.tamur.comps.ui.collapsiblePanel;

import kz.tamur.comps.OrCollapsiblePanel;

/**
 * The Class CollapsiblePanelAdapter.
 * 
 * @author Lebedev Sergey
 */
public abstract class CollapsiblePanelAdapter implements CollapsiblePanelListener {

    /**
     * Expanding.
     * 
     * @param pane
     *            the pane
     */
    public void expanding(OrCollapsiblePanel pane) {
    }

    /**
     * Expanded.
     * 
     * @param pane
     *            the pane
     */
    public void expanded(OrCollapsiblePanel pane) {
    }

    /**
     * Collapsing.
     * 
     * @param pane
     *            the pane
     */
    public void collapsing(OrCollapsiblePanel pane) {
    }

    /**
     * Collapsed.
     * 
     * @param pane
     *            the pane
     */
    public void collapsed(OrCollapsiblePanel pane) {
    }
}