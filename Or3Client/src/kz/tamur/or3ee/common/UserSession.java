package kz.tamur.or3ee.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.cifs.or2.kernel.AnyPair;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Note;

import kz.tamur.util.Funcs;

public abstract class UserSession implements Serializable {
	
	private final Date startTime;

	protected KrnObject userObj;
	protected KrnObject baseObj;
	protected KrnObject balansObj;
	protected String dsName;
	protected String typeClient;
	protected String name;
	protected String iin;
	protected String logName;
	protected String ip;
	protected String pcName;
	protected boolean isAdmin;
	protected boolean isMulti;
	protected KrnObject intLangObj;
	protected KrnObject dataLangObj;
	protected boolean callbacks;
	protected long lastPing;
	protected String serverId;
	
    private AnyPair[] tasksFilter;
	private List<Note> notes = Collections.synchronizedList(new ArrayList<Note>());
	private boolean isAlive = true;

    public static final String SERVER_ID = Funcs.normalizeInput(System.getProperty("SERVER_ID", "default"));

	protected UserSession() {
		this.startTime = new KrnDate();
		this.lastPing = System.currentTimeMillis();
	}

	public abstract UUID getId();
	
	public Date getStartTime() {
		return startTime;
	}

	public KrnObject getUserObj() {
		return userObj;
	}

	public void setUserObj(KrnObject userObj) {
		this.userObj=userObj;
	}
	public long getUserId() {
		return userObj != null ? userObj.id : 0;
	}
	
	public String getUserName() {
		return name;
	}

	public String getUserIin() {
		return iin;
	}
	public String getLogUserName() {
		return logName;
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

	public boolean isMulti() {
		return isMulti;
	}
    public KrnObject getDataLanguage() {
        return dataLangObj;
    }

    public KrnObject getIfcLang() {
        return intLangObj;
    }
    
    public KrnObject getBaseObj() {
    	return baseObj;
    }
    public KrnObject getBalansObj() {
    	return balansObj;
    }
    public long getBaseId() {
    	return baseObj != null ? baseObj.id : 0;
    }

    public long getBalansId() {
    	return balansObj != null ? balansObj.id : 0;
    }

    public void setLang(KrnObject lang){
    	intLangObj = lang;
    }
    
    public void setDataLanguage(KrnObject lang){
    	dataLangObj = lang;
    }

    public String getDsName() {
    	return dsName;
    }
    
    public String getTypeClient() {
    	return typeClient;
    }
    
    public String getServerId() {
		return serverId;
	}

    public boolean isMySession() {
		return serverId == null || serverId.equals(SERVER_ID);
	}

    public boolean callbacks() {
    	return callbacks;
    }
    
    public void addNote(Note note) {
    	if (callbacks) {
    		synchronized (notes) {
        		notes.add(note);
			}
    	}
    }

    public Note[] getNotes() {
    	synchronized (notes) {
        	if (notes.size() > 0) {
        		Note[] res = notes.toArray(new Note[notes.size()]);
        		notes.clear();
        		return res;
        	}
		}
    	return new Note[0];
    }
    
    public void setAlive(boolean b) {
    	isAlive = b;
    }
    
    public boolean isAlive() {
		return isAlive;
	}

	public AnyPair[] getTasksFilter() {
    	return tasksFilter;
    }
    
    public void setTasksFilter(AnyPair[] tasksFilter) {
    	this.tasksFilter = tasksFilter;
    }
    
    public long getLastPing() {
    	return lastPing;
    }
    
    public void ping() {
		lastPing = System.currentTimeMillis();
    }
    
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof UserSession) {
			return getId().equals(UserSession.class.cast(obj).getId());
		}
		return false;
	}
}
