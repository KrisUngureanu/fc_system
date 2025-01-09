package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import com.cifs.or2.kernel.KrnException;


public class TabbedPaneAdapter extends ContainerAdapter {

    private OrTabbedPane tabbedPane;

    public TabbedPaneAdapter(UIFrame frame, OrTabbedPane tabbedPane, boolean isEditor)
            throws KrnException {
        super(frame, tabbedPane, isEditor);
        this.tabbedPane = tabbedPane;
        this.tabbedPane.setXml(null);
    }

    public OrTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
