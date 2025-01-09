package kz.tamur.comps.ui.ext.focus;

import java.awt.*;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public interface FocusTracker {
    /**
     * Should tack the provided component or not (can be used to switch off focus tracking when its not needed to optimize overall
     * performance)
     */
    public boolean isTrackingEnabled();

    /**
     * Tracked component
     */
    public Component getTrackedComponent();

    /**
     * Should count component and its childs as a single focus owner (in case this returns true - any focus change between component and any
     * of its childs will be ignored)
     */
    public boolean isUniteWithChilds();

    /**
     * Listen to any focus change that happens, not only this component focus changes
     */
    public boolean isListenGlobalChange();

    /**
     * This method will inform about focus changes (only actual focus changes will be fired in case "isListenGlobalChange" returns false and
     * all focus changes otherwise)
     */
    public void focusChanged(boolean focused);
}
