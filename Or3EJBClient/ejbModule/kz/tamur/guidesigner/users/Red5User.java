package kz.tamur.guidesigner.users;

import java.util.Date;

import javax.swing.Icon;

public class Red5User {
	public String uid;
	public String name;
	public String ip;  
	public Date startTime;
	public Icon photo;
	public boolean cameraSwitchedOn;
	public boolean watchingYou;
	
	public Red5User(String uid, String name, String ip, Date startTime,
			Icon photo, boolean cameraSwitchedOn, boolean watchingYou) {
		super();
		this.uid = uid;
		this.name = name;
		this.ip = ip;
		this.startTime = startTime;
		this.photo = photo;
		this.cameraSwitchedOn = cameraSwitchedOn;
		this.watchingYou = watchingYou;
	}
}
