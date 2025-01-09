package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class CollapsiblePanelAdapter.
 * 
 * @author Lebedev Sergey
 */
public class CollapsiblePanelAdapter extends ComponentAdapter {

    /** panel. */
    private OrGuiComponent panel;

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
    public CollapsiblePanelAdapter(OrFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
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
}
