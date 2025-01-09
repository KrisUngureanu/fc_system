package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.web.component.OrWebPopUpPanel;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class PopUpPanelAdapter.
 *
 * @author Lebedev Sergey
 */
public class PopUpPanelAdapter extends ComponentAdapter {

    /** panel. */
    private OrGuiComponent panel;

    /**
     * Конструктор класса pop up panel adapter.
     *
     * @param frame the frame
     * @param c the c
     * @param isEditor the is editor
     * @throws KrnException the krn exception
     */
    public PopUpPanelAdapter(OrFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
        panel = c;
    }

    @Override
    public void clear() {
    }

    /**
     * Button pressed.
     */
    public void buttonPressed() {
        ((OrWebPopUpPanel)panel).showPopUp();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        panel.setEnabled(isEnabled);
    }

    /**
     * Проверяет, является ли need pass.
     *
     * @return <code>true</code>, если need pass
     */
    public boolean isNeedPass() {
        return false;
    }
}
