package kz.tamur.server.wf;

public class ProcessDefForEhCache implements java.io.Serializable{
	private String processDefinition="";
	transient long timeOfModification=0;
	public String getProcessDefinition() {
		return processDefinition;
	}
	public long getTimeOfModification() {
		return timeOfModification;
	}
	public void setProcessDefinition(String processDefinition) {
		this.processDefinition=processDefinition;
	}
	public void setTimeOfModification(long timeOfModification) {
		this.timeOfModification=timeOfModification;
	}
}

