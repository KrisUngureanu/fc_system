package kz.tamur.rt.adapters;

import kz.tamur.comps.OrCollapsiblePanel;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.ui.CollapsiblePanel;
import kz.tamur.comps.ui.collapsiblePanel.CollapsiblePanelListener;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class PopUpPanelAdapter.
 * 
 * @author Lebedev Sergey
 */
public class CollapsiblePanelAdapter extends ComponentAdapter implements CollapsiblePanelListener{

    /** panel. */
    private OrCollapsiblePanel panel;

    /**
     * Конструктор класса CollapsiblePanelAdapter.
     * 
     * @param frame
     *            the frame
     * @param c
     *            the c
     * @param isEditor
     *            the is editor
     * @throws KrnException
     *             the krn exception
     */
    public CollapsiblePanelAdapter(OrFrame frame, OrCollapsiblePanel c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
        panel = c;
    }

    
    @Override
    public void clear() {
    }

    
    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        panel.setEnabled(isEnabled);
    }

    
    @Override
    public void expanding(CollapsiblePanel pane) {
        // TODO УБРАТЬ ?
        
    }

    
    @Override
    public void expanded(CollapsiblePanel pane) {
        // TODO УБРАТЬ ?
        
    }

    
    @Override
    public void collapsing(CollapsiblePanel pane) {
        // TODO УБРАТЬ ?
        
    }

    
    @Override
    public void collapsed(CollapsiblePanel pane) {
        // TODO УБРАТЬ ?
        
    }

}
