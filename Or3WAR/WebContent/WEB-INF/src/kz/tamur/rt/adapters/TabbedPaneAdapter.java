package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrTabbedPaneComponent;

import com.cifs.or2.kernel.KrnException;

public class TabbedPaneAdapter extends ContainerAdapter {

    private OrTabbedPaneComponent tabbedPane;

    public TabbedPaneAdapter(OrFrame frame, OrTabbedPaneComponent tabbedPane, boolean isEditor)
            throws KrnException {
        super(frame, tabbedPane, isEditor);
        this.tabbedPane = tabbedPane;
        this.tabbedPane.setXml(null);
    }

    public OrTabbedPaneComponent getTabbedPane() {
        return tabbedPane;
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
