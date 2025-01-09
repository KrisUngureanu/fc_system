package kz.tamur.guidesigner.userrights;

import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

public class RightObject {

	private KrnObject obj;
	private ChildObject action;
	private String name;
	private String desc;
	private boolean blocked;
	private boolean denied;
	private List<ChildObject> userOrRoles;
	private List<ChildObject> procs;
	private List<ChildObject> archs;
	private List<ChildObject> dicts;
	private boolean isChanged = false;
	
	public RightObject(KrnObject obj, ChildObject action, String name, String desc,
			boolean blocked, boolean denied, List<ChildObject> userOrRoles, List<ChildObject> procs,
			List<ChildObject> archs, List<ChildObject> dicts) {
		super();
		this.obj = obj;
		this.action = action;
		this.name = name;
		this.desc = desc;
		this.blocked = blocked;
		this.denied = denied;
		this.userOrRoles = userOrRoles;
		this.procs = procs;
		this.archs = archs;
		this.dicts = dicts;
	}

	public KrnObject getObj() {
		return obj;
	}

	public ChildObject getAction() {
		return action;
	}

	public int getActionCode() {
		return (action != null && action.getObj() != null) ? action.getCode() : -1;
	}
	
	public String getActionName() {
		return action != null ? action.getTitle() : "";
	}

	public String getName() {
		return name != null ? name : "";
	}

	public String getDesc() {
		return desc;
	}
	
	public boolean isBlocked() {
		return blocked;
	}

	public boolean isDenied() {
		return denied;
	}

	public List<ChildObject> getUserOrRoles() {
		if (userOrRoles == null)
			userOrRoles = new ArrayList<ChildObject>();
		return userOrRoles;
	}

	public String getUserOrRolesString() {
		StringBuilder sb = new StringBuilder();
		if (userOrRoles != null && userOrRoles.size() > 0) {
			sb.append(userOrRoles.get(0).getTitle());
			for (int i=1; i<userOrRoles.size(); i++) {
				sb.append("; ").append(userOrRoles.get(i).getTitle());
			}
		}
		return sb.toString();
	}

	public List<ChildObject> getProcs() {
		if (procs == null)
			procs = new ArrayList<ChildObject>();
		return procs;
	}

	public void setAction(ChildObject action) {
		this.action = action;
	}

	public List<ChildObject> getArchs() {
		if (archs == null)
			archs = new ArrayList<ChildObject>();
		return archs;
	}

	public List<ChildObject> getDicts() {
		if (dicts == null)
			dicts = new ArrayList<ChildObject>();
		return dicts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public void setDenied(boolean denied) {
		this.denied = denied;
	}
}
