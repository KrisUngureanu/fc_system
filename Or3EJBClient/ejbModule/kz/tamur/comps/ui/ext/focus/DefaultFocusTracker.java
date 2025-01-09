package kz.tamur.comps.ui.ext.focus;

import java.awt.*;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public abstract class DefaultFocusTracker implements FocusTracker {
    private boolean trackingEnabled;
    private Component component;
    private boolean uniteWithChilds;
    private boolean listenGlobalChange;

    public DefaultFocusTracker(Component component) {
        this(component, true);
    }

    public DefaultFocusTracker(Component component, boolean uniteWithChilds) {
        this(component, uniteWithChilds, false);
    }

    public DefaultFocusTracker(Component component, boolean uniteWithChilds, boolean listenGlobalChange) {
        super();
        this.trackingEnabled = true;
        this.component = component;
        this.uniteWithChilds = uniteWithChilds;
        this.listenGlobalChange = listenGlobalChange;
    }

    public void setTrackingEnabled(boolean trackingEnabled) {
        this.trackingEnabled = trackingEnabled;
    }

    public boolean isTrackingEnabled() {
        return trackingEnabled;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getTrackedComponent() {
        return component;
    }

    public void setUniteWithChilds(boolean uniteWithChilds) {
        this.uniteWithChilds = uniteWithChilds;
    }

    public boolean isUniteWithChilds() {
        return uniteWithChilds;
    }

    public boolean isListenGlobalChange() {
        return listenGlobalChange;
    }

    public void focusChanged(boolean focused) {
        //
    }
}
