package kz.tamur.guidesigner.users;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.cifs.or2.kernel.UserSessionValue;

public class ChatUsers implements Serializable {
	UUID id;
	String name;
	String ip;  
	Date startTime;
	
	public ChatUsers(UserSessionValue fullUserSessionValue) {
		this.id = fullUserSessionValue.id;
		this.name = fullUserSessionValue.name;
		this.ip = fullUserSessionValue.ip;
		this.startTime = fullUserSessionValue.startTime;
	}
	
	ChatUsers getChatUsers() {
		return this;
	}
}
 