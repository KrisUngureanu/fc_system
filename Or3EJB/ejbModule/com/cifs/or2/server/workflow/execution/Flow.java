package com.cifs.or2.server.workflow.execution;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.08.2004
 * Time: 14:55:19
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import com.cifs.or2.server.workflow.definition.*;

/**
 * represents one 'thread-of-execution' for a sequence of Activitys. The concept of
 * flow corresponds to the notion of 'token' in petrinets.
 *
 */
public interface Flow extends java.io.Serializable {
  long getId();
  String getName();
  Node getNode();
  long[] getActorId();
  Date getStart();
  Date getEnd();
  boolean isRootFlow();
  Collection getAttributeInstances();
  Flow getParent();
  ProcessInstance getProcessInstance();
  Collection getChildren();
  void removeChild(Flow child);
  ProcessInstance getSubProcessInstance();
  EventType getEventType();
}
