package kz.tamur.web.common.webgui;

import kz.tamur.comps.Mode;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 01.06.2007
 * Time: 18:15:00
 * To change this template use File | Settings | File Templates.
 */
public class WebProcessMenuItem extends WebMenuItem {

    private KrnObject processObject = null;
    private boolean onToolbal = false;

    public WebProcessMenuItem(String name, String nameKz, KrnObject procObj, boolean isToolBar, byte[] icon, WebSession s) {
        super(name, nameKz, null, Mode.RUNTIME, null, null);
        processObject = procObj;
        onToolbal = isToolBar;
        if (icon != null && icon.length > 0) {
            String nameF = com.cifs.or2.client.Utils.createFileImg(icon, "pic");
            setIconFullPath(WebController.PATH_IMG + nameF);
        }
    }

    public KrnObject getProcessObject() {
        return processObject;
    }

    public boolean isOnToolbal() {
        return onToolbal;
    }
}
