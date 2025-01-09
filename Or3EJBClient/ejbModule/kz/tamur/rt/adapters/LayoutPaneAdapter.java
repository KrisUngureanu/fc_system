package kz.tamur.rt.adapters;

import kz.tamur.comps.*;

import com.cifs.or2.kernel.KrnException;

public class LayoutPaneAdapter extends ContainerAdapter {

    private OrLayoutPane layoutPane;

    public LayoutPaneAdapter(UIFrame frame, OrLayoutPane layoutPane, boolean isEditor)
            throws KrnException {
        super(frame, layoutPane, isEditor);
        this.layoutPane = layoutPane;
        this.layoutPane.setXml(null);
    }

    public OrLayoutPane getLayoutPane() {
        return layoutPane;
    }

    public void clear() {
    }
}
