package kz.tamur.rt.adapters;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;

import com.cifs.or2.kernel.KrnException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 21.06.2005
 * Time: 15:01:48
 */
public class ContainerAdapter extends ComponentAdapter {

    protected List<ComponentAdapter> childrenAdapters = new ArrayList<ComponentAdapter>();
    private boolean selfEnabled = true;

    public ContainerAdapter(OrFrame frame, OrGuiComponent c, boolean isEditor) throws KrnException {
        super(frame, c, isEditor);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selfEnabled = enabled;
        for (int i = 0; i < childrenAdapters.size(); i++) {
            ComponentAdapter ca = childrenAdapters.get(i);
            if ((ca.getDataRef() != null || ca.getDataCalcRef() != null)
            		&& !(ca instanceof HyperPopupAdapter)
            		&& !(ca instanceof DocFieldAdapter)
            		&& !(ca instanceof ButtonAdapter)) {

                ca.setEnabled(enabled ? ca.checkEnabled() : false);
            } else if (Constants.IS_UL_PROJECT) {
                ca.setEnabled(ca.isInherit() ? enabled : ca.checkEnabled());
            } else if (enabled) {
                ca.setEnabled(ca.isInherit() ? enabled : ca.checkEnabled());
            } else {
            	if (!(ca instanceof HyperPopupAdapter)
                		&& !(ca instanceof DocFieldAdapter)
                		&& !(ca instanceof ButtonAdapter)) {
            		ca.setEnabled(ca.isInherit() ? enabled : ca.checkEnabled());
            	}
            }
        }
    }

    public List<ComponentAdapter> getChildrenAdapters() {
        return childrenAdapters;
    }

    public boolean isSelfEnabled() {
        return selfEnabled;
    }

    public void setChildrenAdapters(List<ComponentAdapter> adapters) {
        ComponentAdapter ca;
        for (int i = 0; i < adapters.size(); i++) {
            ca = (ComponentAdapter) adapters.get(i);
            if (ca != null) {
                ca.setParentAdapter(this);
                childrenAdapters.add(ca);
            }
        }
    }

    public void clearFilterParam() {
        ComponentAdapter ca;
        for (int i = 0; i < childrenAdapters.size(); i++) {
            ca = (ComponentAdapter) childrenAdapters.get(i);
            ca.clearFilterParam();
        }
    }

    public void clear() {
    }
}
