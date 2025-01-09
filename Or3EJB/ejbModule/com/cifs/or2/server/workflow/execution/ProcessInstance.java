package com.cifs.or2.server.workflow.execution;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.08.2004
 * Time: 15:00:23
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import com.cifs.or2.server.workflow.definition.*;
/**
 * is one execution of a ProcessDefinition.
 */
public interface ProcessInstance extends java.io.Serializable {
  long getId();
  long getTrId();
  Date getStart();
  Date getEnd();
  long getInitiator();
  long getChopper();
  ProcessDefinition getProcessDefinition();
  Flow getRootFlow();
  Flow getSuperProcessFlow();
}
