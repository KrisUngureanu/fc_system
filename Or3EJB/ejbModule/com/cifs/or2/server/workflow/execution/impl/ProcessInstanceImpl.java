package com.cifs.or2.server.workflow.execution.impl;

import java.util.*;


import com.cifs.or2.server.workflow.definition.*;
import com.cifs.or2.server.workflow.execution.*;

public class ProcessInstanceImpl implements ProcessInstance {

  public ProcessInstanceImpl() {}

  public ProcessInstanceImpl(long processId, long actorId, ProcessDefinition processDefinition,long trId ) {
    this.id=processId;
    this.start = new Date(System.currentTimeMillis());
    this.trId=trId;
    this.initiatorActorId = actorId;
    this.processDefinition = processDefinition;
  }

  public Date getStart() { return start; }
  public void setStart( Date start ) { this.start = start; }

  public Date getEnd() { return end; }
  public void setEnd( Date end ) { this.end = end; }

  public ProcessDefinition getProcessDefinition() { return processDefinition; }
  public void setProcessDefinition( ProcessDefinition processDefinition ) { this.processDefinition = processDefinition; }

  public Flow getRootFlow() { return rootFlow; }
  public void setRootFlow( Flow rootFlow ) { this.rootFlow = rootFlow; }

  public Flow getSuperProcessFlow() { return superProcessFlow; }
  public void setSuperProcessFlow( Flow superProcessFlow ) { this.superProcessFlow = superProcessFlow; }

  public long getInitiator() {
    return initiatorActorId;
  }
  public void setInitiator(long initiatorActorId) {
	    this.initiatorActorId=initiatorActorId;
	  }
  public long getChopper() {
    return chopperActorId;
  }

  public void setChopper(long chopperId) {
    this.chopperActorId=chopperId;
  }
  public boolean isActive() {
    return ( end == null );
  }

  public String toString() {
    return "processInstance[" + id + "|" + processDefinition.getName() + "]";
  }

    public boolean isProcess() {
        return isProcess;
    }

    public void setProcess(boolean process) {
        isProcess = process;
    }

    public long getId() { return id; }

    public long getTrId() {
        return trId;
    }

    public void setId(int id) { this.id = id; }

  public String getObservers() {
		return observers;
	}

	public void setObservers(String observers) {
		this.observers = observers;
	}

public String getUiObservers() {
		return uiObservers;
	}

	public void setUiObservers(String uiObservers) {
		this.uiObservers = uiObservers;
	}

public String getTypeUiObservers() {
		return typeUiObservers;
	}

	public void setTypeUiObservers(String typeUiObservers) {
		this.typeUiObservers = typeUiObservers;
	}

private long id=-1;
  private long trId=-1;
  private Date start = null;
  private Date end = null;
  private boolean isProcess=true;
  private long initiatorActorId = 0;
  private long chopperActorId = 0;
  private ProcessDefinition processDefinition = null;
  private Flow rootFlow = null;
  private Flow superProcessFlow = null;
  private String observers="";
  private String uiObservers="";
  private String typeUiObservers="";

}