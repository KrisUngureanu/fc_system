package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.rt.RadioGroupManager;
import com.cifs.or2.kernel.KrnException;

/**
 * The Class LabelAdapter.
 * 
 * @author Lebedev Sergey
 */
public class LabelAdapter extends ComponentAdapter {

    private OrGuiComponent component;
    private OrRef copyRef;
    private RadioGroupManager groupManager = new RadioGroupManager();

    /**
     * Конструктор класса LabelAdapter.
     * 
     * @param frame
     *            frame.
     * @param comp
     *            comp.
     * @throws KrnException
     *             the krn exception
     */
    public LabelAdapter(OrFrame frame, OrGuiComponent comp) throws KrnException {
        super(frame, comp, false);
        component = comp;
    }

    @Override
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }
    @Override
    public void clear() {
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        component.setEnabled(isEnabled);
    }

    /**
     * Получить copy ref.
     * 
     * @return copy ref.
     */
    public OrRef getCopyRef() {
        return copyRef;
    }
}
