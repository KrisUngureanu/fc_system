package kz.tamur.rt.adapters;

import kz.tamur.comps.*;

import com.cifs.or2.kernel.KrnException;


public class SplitPaneAdapter extends ContainerAdapter {

    private OrSplitPane splitPane;

    public SplitPaneAdapter(UIFrame frame, OrSplitPane splitPane, boolean isEditor)
            throws KrnException {
        super(frame, splitPane, isEditor);
        this.splitPane = splitPane;
        this.splitPane.setXml(null);
    }

    public OrSplitPane getSplitPane() {
        return splitPane;
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
