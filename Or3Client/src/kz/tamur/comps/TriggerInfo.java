package kz.tamur.comps;

import java.io.Serializable;

public class TriggerInfo implements Serializable {
	
	private String name;
	private String description;
	private String body;

	public TriggerInfo(String name, String description, String body) {
		this.name = name;
		this.description = description;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}