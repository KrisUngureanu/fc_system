package kz.tamur.server.plugins;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;
import kz.tamur.scan.ScanDriver;

/**
 * Created by IntelliJ IDEA.
 * User: erik
 * Date: 08.08.2008
 * Time: 15:11:39
 * To change this template use File | Settings | File Templates.
 */
public class ScannerPlugin implements SrvPlugin {

    private Session session;

    public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

    public ScannerPlugin() {
        ScanDriver.getInstanse();
    }

    public void scanImage() {
        ScanDriver.getInstanse().scanImage();
    }
}