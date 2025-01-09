package kz.tamur.comps;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.04.2004
 * Time: 19:25:38
 * To change this template use File | Settings | File Templates.
 */
public interface Factory {
    OrGuiComponent create(Element xml, int mode, OrFrame frame) throws KrnException;
    OrGuiComponent create(String name, OrFrame frame) throws KrnException;
    Place createEmptyPlace();
    void addFactoryListener(FactoryListener l);
    void removeFactoryListener(FactoryListener l);
}
