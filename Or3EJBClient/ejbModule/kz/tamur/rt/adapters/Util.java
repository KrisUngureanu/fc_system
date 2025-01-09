package kz.tamur.rt.adapters;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.InterfaceManagerFactory;

import javax.swing.*;

import com.cifs.or2.client.Kernel;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 08.02.2005
 * Time: 15:09:56
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static DesignerDialog getDesignerDialog(Container comp, String title, JPanel content, boolean hasClearBtn) {
        if (comp instanceof Dialog)
            return new DesignerDialog((Dialog)comp, title, content, hasClearBtn);
        else if (comp instanceof Frame)
            return new DesignerDialog((Frame)comp, title, content, hasClearBtn);
        return null;
    }

    public static void showErrorMessage(OrGuiComponent comp, String message,
                                        String propertyName) {
        String componentName = "";
        if (comp != null) {
            PropertyNode node = comp.getProperties().getChild("title");
            componentName = "Компонент: ";
            if (node != null) {
                PropertyValue pv = comp.getPropertyValue(node);
                if (!pv.isNull()) {
                    componentName = componentName + pv.stringValue();
                }
            }
            componentName = componentName + " [" + comp.getClass().getName().substring(
                    Constants.COMPS_PACKAGE.length()) + "]";
        }
        String mainMessage = message;
        Container container = (Frame)InterfaceManagerFactory.instance().getManager();
        MessagesFactory.showMessageDialogBig((Frame)container, MessagesFactory.ERROR_MESSAGE,
                    mainMessage + "\n" + componentName + "\n\n" + propertyName);
        Kernel.instance().release();
        //System.exit(1);
    }
}
