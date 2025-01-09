package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import kz.tamur.or3.client.comps.interfaces.OrScrollPaneComponent;

import com.cifs.or2.kernel.KrnException;



public class ScrollPaneAdapter extends ContainerAdapter {

    private OrScrollPaneComponent scrollPane;

    public ScrollPaneAdapter(OrFrame frame, OrScrollPaneComponent scrollPane, boolean isEditor)
            throws KrnException {
        super(frame, scrollPane, isEditor);
        this.scrollPane = scrollPane;
        this.scrollPane.setXml(null);
    }

    public OrScrollPaneComponent getScrollPane() {
        return scrollPane;
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
