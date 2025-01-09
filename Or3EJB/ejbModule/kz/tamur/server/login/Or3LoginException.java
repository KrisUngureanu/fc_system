package kz.tamur.server.login;

import javax.security.auth.login.LoginException;

public class Or3LoginException extends LoginException {

	private int code;

	public Or3LoginException(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
