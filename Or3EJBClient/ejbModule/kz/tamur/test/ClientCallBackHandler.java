package kz.tamur.test;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class ClientCallBackHandler implements CallbackHandler {
	private String user, pd;

	/**
	 * Constructor that takes username and password
	 */
	public ClientCallBackHandler(String user, String pd) {
		this.user = user;
		this.pd = pd;
	}

	
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		int len = callbacks.length;
		Callback cb;
		for (int i = 0; i < len; i++) {
			cb = callbacks[i];
			if (cb instanceof NameCallback) {
				NameCallback ncb = (NameCallback) cb;
				ncb.setName(user);
			} else if (cb instanceof PasswordCallback) {
				PasswordCallback pcb = (PasswordCallback) cb;
				pcb.setPassword(pd.toCharArray());
			} else {
				throw new UnsupportedCallbackException(cb,
						"Don’t know what to do with this!!");
			}
		}// end of for-loop
	}
}
