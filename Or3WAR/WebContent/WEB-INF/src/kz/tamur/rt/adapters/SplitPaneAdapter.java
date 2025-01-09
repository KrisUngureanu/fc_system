package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrSplitPaneComponent;

import com.cifs.or2.kernel.KrnException;


public class SplitPaneAdapter extends ContainerAdapter {

    private OrSplitPaneComponent splitPane;

    public SplitPaneAdapter(OrFrame frame, OrSplitPaneComponent splitPane, boolean isEditor)
            throws KrnException {
        super(frame, splitPane, isEditor);
        this.splitPane = splitPane;
        this.splitPane.setXml(null);
    }

    public OrSplitPaneComponent getSplitPane() {
        return splitPane;
    }

    public void clear() {
    }
}
