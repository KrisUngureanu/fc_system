package kz.tamur.server.login;

import javax.security.auth.callback.Callback;

import com.cifs.or2.server.SessionManager;

public class Or3LoginContextCallback implements Callback {

	private SessionManager mgr;
	private String ip;
	private String computer;

	public Or3LoginContextCallback() {
	}
	
	public SessionManager getSessionManager() {
		return mgr;
	}

	public SessionManager getMgr() {
		return mgr;
	}

	public void setMgr(SessionManager mgr) {
		this.mgr = mgr;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getComputer() {
		return computer;
	}

	public void setComputer(String computer) {
		this.computer = computer;
	}
	
	
}
