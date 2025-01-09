package kz.tamur.rt.adapters;

import kz.tamur.comps.*;

import com.cifs.or2.kernel.KrnException;


public class LayoutPaneAdapter extends ContainerAdapter {

    private OrGuiContainer pane;

    public LayoutPaneAdapter(OrFrame frame, OrGuiContainer pane, boolean isEditor)
            throws KrnException {
        super(frame, pane, isEditor);
        this.pane = pane;
        this.pane.setXml(null);
    }

    public OrGuiContainer getLayoutPane() {
        return pane;
    }

    public void clear() {
    }
}