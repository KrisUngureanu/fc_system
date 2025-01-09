package kz.tamur.rt.adapters;

import kz.tamur.comps.*;

import com.cifs.or2.kernel.KrnException;

public class ScrollPaneAdapter extends ContainerAdapter {

    private OrScrollPane scrollPane;

    public ScrollPaneAdapter(UIFrame frame, OrScrollPane scrollPane, boolean isEditor)
            throws KrnException {
        super(frame, scrollPane, isEditor);
        this.scrollPane = scrollPane;
        this.scrollPane.setXml(null);
    }

    public OrScrollPane getScrollPane() {
        return scrollPane;
    }

    public void clear() {
    }
}
