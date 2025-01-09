package kz.tamur.comps.ui.collapsiblePanel;

import kz.tamur.comps.ui.CollapsiblePanel;

/**
 * This is a special CollapsiblePanel events listener that fires four kinds of events. 
 * Two of them are fired before the collapsible pane finished either collapsing or expanding and two other fired after. 
 * Notice that both events could be fired almost in the same time in case CollapsiblePanel is not animated or its animation is speeded up.
 * 
 * @see CollapsiblePanelEvent
 */

public interface CollapsiblePanelListener {

    /**
     * Notifies when CollapsiblePanel starts to expand.
     * 
     * @param pane
     *            the pane
     */

    public void expanding(CollapsiblePanel pane);

    /**
     * Notifies when CollapsiblePanel finished expanding.
     * 
     * @param pane
     *            the pane
     */

    public void expanded(CollapsiblePanel pane);

    /**
     * Notifies when CollapsiblePanel starts to collapse.
     * 
     * @param pane
     *            the pane
     */

    public void collapsing(CollapsiblePanel pane);

    /**
     * Notifies when CollapsiblePanel finished collapsing.
     * 
     * @param pane
     *            the pane
     */

    public void collapsed(CollapsiblePanel pane);
}
