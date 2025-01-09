package com.cifs.or2.server.workflow.execution.impl;

import com.cifs.or2.server.workflow.definition.impl.TransitionImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 19.08.2004
 * Time: 20:21:40
 * To change this template use File | Settings | File Templates.
 */
public class ForkedFlowImpl {
  public ForkedFlowImpl( TransitionImpl transition, FlowImpl flow ){
    this.transition = transition;
    this.flow = flow;
  }

  public TransitionImpl getTransition() {
    return this.transition;
  }

  public FlowImpl getFlow() {
    return this.flow;
  }

  private FlowImpl flow = null;
  private TransitionImpl transition = null;

}
