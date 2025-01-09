package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrLabel;
import kz.tamur.rt.RadioGroupManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class LabelAdapter.
 * 
 * @author Lebedev Sergey
 */
public class LabelAdapter extends ComponentAdapter {

    private OrLabel component;
    private OrRef copyRef;
    private RadioGroupManager groupManager = new RadioGroupManager();

    private static final Log log = LogFactory.getLog(LabelAdapter.class);

    /**
     * Конструктор класса label adapter.
     * 
     * @param frame
     *            frame.
     * @param comp
     *            comp.
     * @throws KrnException
     *             the krn exception
     */
    public LabelAdapter(OrFrame frame, OrLabel comp) throws KrnException {
        super(frame, comp, false);
        component = comp;
    }

    
    @Override
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (value instanceof String)
            component.setToolTipText((String) value);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public OrRef getRef() {
        return dataRef;
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

    /**
     * Do copy.
     */
    public void doCopy() {
        if (copyRef != null) {
            try {
                OrRef ref = dataRef;
                OrRef.Item item = copyRef.getItem(langId);
                Object value = (item != null) ? item.getCurrent() : null;
                if (ref.getItem(langId) == null)
                    ref.insertItem(0, value, null, LabelAdapter.this, false);
                else
                    ref.changeItem(value, LabelAdapter.this, null);
            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }
    }
}
