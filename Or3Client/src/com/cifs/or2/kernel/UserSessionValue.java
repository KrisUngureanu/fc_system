package com.cifs.or2.kernel;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import kz.tamur.or3ee.common.UserSession;

public class UserSessionValue implements Serializable {
	
	public final UUID id;
	public final Date startTime;
	public final KrnObject userObj;
	public final KrnObject baseObj;
	public final String name;
	public final String logName;
	public final String typeClient;
	public final String dsName;
	public final String ip;
	public final String pcName;
	public final boolean isAdmin;
	public final String serverId;
	
	public UserSessionValue(UUID id, String serverId) {
		super();
		this.id = id;
		this.userObj = null;
		this.baseObj = null;
		this.typeClient = null;
		this.dsName = null;
		this.name = null;
		this.logName = null;
		this.ip = null;
		this.pcName = null;
		this.isAdmin = false;
		this.startTime = null;
		this.serverId = serverId;
	}

	public UserSessionValue(UUID id, KrnObject userObj, KrnObject baseObj,
			String dsName,String name,String logName,String typeClient, String ip, String pcName, boolean isAdmin,
			Date startTime,String serverId) {
		super();
		this.id = id;
		this.userObj = userObj;
		this.baseObj = baseObj;
		this.typeClient = typeClient;
		this.dsName = dsName;
		this.name = name;
		this.logName = logName;
		this.ip = ip;
		this.pcName = pcName;
		this.isAdmin = isAdmin;
		this.startTime = startTime != null ? new KrnDate(startTime.getTime()) : null;
		this.serverId = serverId;
	}

	public UUID getId() {
		return id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public KrnObject getUserObj() {
		return userObj;
	}
	
	public long getUserId() {
		return userObj != null ? userObj.id : 0;
	}

	public KrnObject getBaseObj() {
		return baseObj;
	}

	public String getUserName() {
		return name;
	}

	public String getLogUserName() {
		return logName;
	}

	public String getTypeClient() {
		return typeClient;
	}

	public String getDsName() {
		return dsName;
	}

	public String getIp() {
		return ip;
	}

	public String getComputer() {
		return pcName;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public String getServerId() {
		return serverId;
	}
}
