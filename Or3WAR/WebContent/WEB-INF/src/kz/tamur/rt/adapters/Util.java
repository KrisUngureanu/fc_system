package kz.tamur.rt.adapters;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.controller.WebController;
import kz.tamur.web.component.OrWebTableColumn;
import kz.tamur.web.component.OrWebTable;

import java.awt.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 08.02.2005
 * Time: 15:09:56
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Util.class.getName());

    public static String showErrorMessage(OrGuiComponent comp, String message,
                                        String propertyName) {
        String componentName = "";
        if (comp != null) {
            PropertyNode node = comp.getProperties().getChild("title");
            componentName = "Компонент: ";
            if (node != null) {
                PropertyValue pv = comp.getPropertyValue(node);
                if (!pv.isNull()) {
                    componentName = componentName + pv.stringValue(comp.getAdapter().getFrame().getKernel());
                }
            }
        }
        if (comp instanceof OrWebTableColumn) {
            componentName = componentName + " [" + comp.getClass().getName().substring(
                    Constants.WEB_COMPS_PACKAGE.length()) + "]";
            String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
            ((OrWebTable)((OrWebTableColumn)comp).getParent()).setErrorMessage(mainMessage, false);
            return mainMessage;
        } else if (comp instanceof WebComponent) {
            componentName = componentName + " [" + comp.getClass().getName().substring(
                    Constants.WEB_COMPS_PACKAGE.length()) + "]";
            String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
            ((WebComponent)comp).setErrorMessage(mainMessage, false);
            return mainMessage;
        } else {
            componentName = "Компонент: [null]";
            String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
            if (WebController.APP_HOME != null) {
                log.debug(mainMessage);
                return mainMessage;
            } else {
                return null;
            }
        }
    }

    public static String showMessage(OrGuiComponent comp, String message,
                                        String propertyName, int messageType) {
        String componentName = "";
        if (comp != null) {
            PropertyNode node = comp.getProperties().getChild("title");
            componentName = "Компонент: ";
            if (node != null) {
                PropertyValue pv = comp.getPropertyValue(node);
                if (!pv.isNull()) {
                    componentName = componentName + pv.stringValue(comp.getAdapter().getFrame().getKernel());
                }
            }
        }
        if (comp instanceof OrWebTableColumn) {
	        componentName = componentName + " [" + comp.getClass().getName().substring(
	                Constants.WEB_COMPS_PACKAGE.length()) + "]";
	        String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
	        ((OrWebTable)((OrWebTableColumn)comp).getParent()).setAlertMessage(mainMessage, false);
	        return mainMessage;
	    } else if (comp instanceof WebComponent) {
            componentName = componentName + " [" + comp.getClass().getName().substring(
                    Constants.WEB_COMPS_PACKAGE.length()) + "]";
            String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
            ((WebComponent)comp).setAlertMessage(mainMessage, false);
            return mainMessage;
        } else {
            componentName = "Компонент: [null]";
            String mainMessage = message + "\n" + componentName + "\n\n" + propertyName;
            if (WebController.APP_HOME != null) {
                log.debug(mainMessage);
                return mainMessage;
            } else {
                return null;
            }
        }
    }
    
    public static void showInformMessage(OrGuiComponent comp, String message) {
        if (comp instanceof OrWebTableColumn) {
            ((OrWebTable) ((OrWebTableColumn) comp).getParent()).setAlertMessage(message, false);
        } else if (comp instanceof WebComponent) {
            ((WebComponent) comp).setAlertMessage(message, false);
        } else if (WebController.APP_HOME != null) {
            log.debug(message);
        }
    }
    
}
