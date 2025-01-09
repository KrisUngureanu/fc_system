package kz.tamur.web.common;

public abstract class WebAction implements WebActionMaker {
	private String id;
	private boolean enabled = true;
	private boolean enableChanged = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public abstract void makeAction();

	public boolean isEnabled() {
		enableChanged = false;
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		enableChanged = true;
		this.enabled = enabled;
	}

	public boolean isEnableChanged() {
		return enableChanged;
	}
}
