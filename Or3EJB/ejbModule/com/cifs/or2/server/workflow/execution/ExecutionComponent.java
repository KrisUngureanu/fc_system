package com.cifs.or2.server.workflow.execution;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.08.2004
 * Time: 14:47:58
 * To change this template use File | Settings | File Templates.
 */

import com.cifs.or2.kernel.Activity;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.UserSrv;
import com.cifs.or2.server.workflow.definition.EventType;

import java.util.*;

/**
 * is the session facade that exposes the interface for the execution of processes.
 **/
public interface ExecutionComponent {

  Collection getTaskList( UserSrv user);
  Collection getTaskList(Collection actorIds);
  String[] startProcessInstance( int processDefinitionId ,int actorId,Session session);
  String[] performActivitys(Activity[] activitys ,Session session,EventType event);
  void cancelProcessInstance(long processInstanceId,String nodeId,Session session );
  Flow getFlow( int flowId ,Session session);
}
