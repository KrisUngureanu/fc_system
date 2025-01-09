package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.rt.adapters.RadioBoxAdapter;
import kz.tamur.comps.OrGuiComponent;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 20.02.2007
 * Time: 22:41:31
 * To change this template use File | Settings | File Templates.
 */
public interface OrRadioBoxComponent extends OrGuiComponent {
    void setItems(RadioBoxAdapter.OrRadioItem[] items);

    void removeAllButtons();

    void select(KrnObject obj, RadioBoxAdapter.OrRadioItem[] radioitems);

    void clearAllSelection();

    void optionsChanged();
}
