package kz.tamur.server.wf;

import static java.util.Collections.synchronizedMap;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.DatabaseCacheListener;
import com.cifs.or2.server.exchange.Box;
import com.cifs.or2.server.exchange.BoxException;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.plugins.SystemProperties;
import com.cifs.or2.server.timer.ServerTasks;
import com.cifs.or2.server.workflow.definition.EventType;
import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.impl.ActionImpl;
import com.cifs.or2.server.workflow.definition.impl.ActivityStateImpl;
import com.cifs.or2.server.workflow.definition.impl.DecisionImpl;
import com.cifs.or2.server.workflow.definition.impl.DefinitionComponentImpl;
import com.cifs.or2.server.workflow.definition.impl.DefinitionObjectImpl;
import com.cifs.or2.server.workflow.definition.impl.EndStateImpl;
import com.cifs.or2.server.workflow.definition.impl.EndSyncState;
import com.cifs.or2.server.workflow.definition.impl.ForkImpl;
import com.cifs.or2.server.workflow.definition.impl.InBoxStateImpl;
import com.cifs.or2.server.workflow.definition.impl.JoinImpl;
import com.cifs.or2.server.workflow.definition.impl.NodeImpl;
import com.cifs.or2.server.workflow.definition.impl.OutBoxStateImpl;
import com.cifs.or2.server.workflow.definition.impl.ProcessDefinitionImpl;
import com.cifs.or2.server.workflow.definition.impl.ProcessStateImpl;
import com.cifs.or2.server.workflow.definition.impl.StartStateImpl;
import com.cifs.or2.server.workflow.definition.impl.StartSyncState;
import com.cifs.or2.server.workflow.definition.impl.TransitionImpl;
import com.cifs.or2.server.workflow.execution.impl.FlowImpl;
import com.cifs.or2.server.workflow.execution.impl.ProcessInstanceImpl;
import com.cifs.or2.server.workflow.organisation.OrganisationComponent;

import kz.tamur.OrException;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.lang.MathOp;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.debug.ResourceRegistry;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.Cache;
import kz.tamur.or3ee.server.kit.CacheUtils;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
                                                            
/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 06.12.2005 Used memory:
 * Time: 18:34:02
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionEngine implements Runnable {

    private final static Log _log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ExecutionEngine.class.getName());

    private ExecutionComponent exeComp;
    private DefinitionComponentImpl defComp;
    private OrganisationComponent orgComp;
    private FlowImpl flow;
    private FlowImpl waitChildFlow;
    private Session currSession;
    private Session lockSession;
    private Session waitChildSession;
    private Future future;
    private boolean rollback = false;
    private boolean interrupting = false;
    private boolean interruptFailed = false;
    private boolean isSessionCommit=true;
    private static PausableThreadPoolExecutor executor;
    private static Collection <Runnable> idleList;
    private static final Map<String,List<FlowImpl>> synchron=synchronizedMap(new HashMap<String,List<FlowImpl>>());
    private static final Map<String,Queue<FlowImpl>> waitFlow=synchronizedMap(new HashMap<String,Queue<FlowImpl>>());
    private static final Map<Long,ExecutionEngine> runFlow=synchronizedMap(new HashMap<Long,ExecutionEngine>());
    private static boolean isGlobalSynchronOn=false;
    private static boolean isClearVar=false;
    
    // Класс отслеживающий изменения в кешах
 	private static DatabaseCacheListener cacheListener = null;
 	
    private static Cache<Long, String> activeFlowCache = CacheUtils.getCache("activeFlowCache");
    public static Map<Long, String> activeFlowCacheMap = new HashMap<>();

    static {
    	cacheListener = new DatabaseCacheListener(null);
    	activeFlowCache.addEntryListener(cacheListener);
		for (Long flowId : activeFlowCache.getKeys()) {
			cacheListener.entryAdded(activeFlowCache.getName(), flowId, activeFlowCache.get(flowId), null);
		}
    }

    public ExecutionEngine(FlowImpl flow, ExecutionComponent exeComp,
    		DefinitionComponentImpl defComp,
    		OrganisationComponent orgComp) {
        this.exeComp = exeComp;
        this.defComp = defComp;
        this.orgComp = orgComp;
        this.flow = flow;
    }
    
    private Log getLog(String userName) {
    	StringBuilder logName = new StringBuilder(exeComp.getDsName()).append(".").append(userName).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
    	return LogFactory.getLog(logName.toString());
    }

    String[] startProcess(UserSession user) {
        String[] res;
        // запуск соответствующих действий
        flow.setUser(user.getUserObj());
        flow.setIp(user.getIp());
        flow.setComputer(user.getComputer());
        //записываем историю
        flow.setActorFromId(user.getUserId());
        res = new String[]{"", "" + flow.getId()};
        return res;
    }

    public boolean cancelProcess(Session s, boolean forceCancel) throws WorkflowException {
    	if (!isRunning() || forceCancel) {
	    	boolean release = true;
	        flow.setRollback(rollback=true);
        	if (flow.getProcessInstance().getProcessDefinition() != null) {
        		EndStateImpl endState = (EndStateImpl) flow.getProcessInstance().getProcessDefinition().getEndState();
        		flow.setNode(endState);
        		flow.setEventType(null);
        	}
	        if(interrupt(s)) {
	        	if (flow.getProcessInstance().getProcessDefinition() == null) {
	    	        processRollback(s);
	        	}
	            release = start(s);
	        }
	        return release;
    	}
    	return false;
    }

    String performActivity(Node node,String event, Session userSession) throws WorkflowException,KrnException {
        EventType et = flow.getEventType();
        if (et.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)) {

        	//удаляем признак предыдущей ошибки
        	if ((flow.getParam() & Constants.ACT_ERR) > 0) {
                flow.setParam(flow.getParam() ^ Constants.ACT_ERR);
        	}
            // и у суперпроцесса убираем ошибку
            FlowImpl superProcessFlow=(FlowImpl)flow.getProcessInstance().getSuperProcessFlow();
            if (superProcessFlow != null && (superProcessFlow.getParam() & Constants.ACT_ERR) > 0) {
            	superProcessFlow.setParam(superProcessFlow.getParam() ^ Constants.ACT_ERR);
            }
            saveFlow(flow, userSession);
            
        	KrnObject oldUI = flow.getUi();	//Исключить доступность интерфейса и стрелки после того как
        	flow.setUi(null);				//стрелка нажата а задача выполняет какие то действия достаточно долго

        	try {
	        	Map vc=flow.getVariable(userSession, exeComp.SA_FLOW_VAR.id);
	        	vc=null;
	            Object res_after = runActionsForEvent(EventType.PERFORM_OF_ACTIVITY, (NodeImpl) node, flow, userSession);
	            if (res_after != null && res_after instanceof Number && ((Number) res_after).intValue() == 0) {
	            	flow.setUi(oldUI);			// Возвращаем прежний интерфейс обработки
	                saveFlow(flow, userSession);
	                res_after = flow.getVariable(userSession,exeComp.SA_FLOW_VAR.id).get("ERRMSG");
	                return res_after != null ? res_after.toString() : "ERRMSG!!!";
	            } else if (res_after != null 
	            		&& res_after instanceof Number 
	            		&& ((Number) res_after).intValue() == 2 
	            		&& !EventType.DEFERRED_PERFORM_OF_ACTIVITY.toString().equals(event)) {
		            	flow.setUi(oldUI);// Возвращаем прежний интерфейс обработки
		                saveFlow(flow, userSession);
		                res_after = flow.getVariable(userSession,exeComp.SA_FLOW_VAR.id).get("WARNMSG");
		                return res_after != null ? "deferred "+res_after.toString() : "deferred WARNMSG!!!";
	            } else {
	                flow.setEventType(EventType.PERFORM_OF_ACTIVITY);
	    			WfUtils.setFlowProperties(flow,orgComp,userSession,false);
	                if ((flow.getParam() & Constants.ACT_PERMIT) == Constants.ACT_PERMIT)
	                    flow.setParam(flow.getParam() ^ Constants.ACT_PERMIT);
	                saveFlow(flow, userSession);
	            }
            } catch (WorkflowException e) {
            	flow.setUi(oldUI);			// Возвращаем прежний интерфейс обработки
                saveFlow(flow, userSession);
                _log.error(e, e);
                throw e;
            } catch (KrnException e) {
            	flow.setUi(oldUI);			// Возвращаем прежний интерфейс обработки
                saveFlow(flow, userSession);
                _log.error(e, e);
                throw e;
            }
        }
        return "";
    }

    int checkXml(Box box,Session session) throws WorkflowException {
        int res=0;
    	Map vc=flow.getVariable(session, exeComp.SA_FLOW_VAR.id);
    	vc=null;
        Object check = runActionsForEvent(EventType.CHECK_XML, (InBoxStateImpl) flow.getNode(), flow, session);
        if (check != null && check instanceof Number) {
            res=((Number) check).intValue();
            if (res==1) {
                Object ex_err_msg=flow.getVariable(session,exeComp.SA_FLOW_VAR.id).get("EX_ERR_MSG");
                if(ex_err_msg!=null){
                    res=-1;
                    //Сообщение содержат ошибки
                    String userName = session.getUserSession().getLogUserName();
                	Log log = getLog(userName);
                    try {
                        //Отправляем сообщение об ошибке
                        box.send(ex_err_msg, flow.getId(),"");
                        //Сохраняем состояние переменных в потоке
                        Map vc1=flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
                        vc1.remove("EX_ERR_MSG");
                        vc1=null;
                        saveFlow(flow,session);
                        session.commitTransaction();
                    } catch (KrnException e) {
                        log.error(e, e);
                    } catch (BoxException e) {
                        Map vc1=flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
                        vc1.put("SEND_RESULT", e);
                        vc1=null;
                    }
                }
            }
        }
        return res;
    }

    public Object runActionsForEvent(EventType eventType,
                                     DefinitionObjectImpl definitionObject,
                                     FlowImpl flow,
                                     Session session) throws WorkflowException{
        // поиск действия соответствующего данному событию
        Collection actions = definitionObject.getActions();
        NodeImpl node=null;
        if(definitionObject instanceof NodeImpl){
             node=(NodeImpl)definitionObject;
        }
        for (Object action1 : actions) {
            ActionImpl action = (ActionImpl) action1;
            if (action.getEventType() == eventType)
                if (action.getExpression() != null) {
                    return WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                            action.getExpression(), null, flow,node, eventType, session);
                } else
                    return null;
        }
        return null;
    }

    private Object processActivityState(ActivityStateImpl activityState, EventType event, Session session)  throws WorkflowException {
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        Object res = runActionsForEvent(event, activityState, flow, session);
        if (event.equals(EventType.AFTER_ACTIVITYSTATE_ASSIGNMENT)) {
            try {
                 WfUtils.setFlowPropertiesUI(flow,activityState,session,false);
			} catch (Exception e) {
                log.error(e, e);
			}
        }else if (event.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)) {
            Map<String,Object> var_ = flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
            if(flow.getUi()==null){
                try {
                	ASTStart uiExpression = activityState.getUiExpression();
	                String uiExpressionKrn = activityState.getUiExpressionKrn();
	                if(uiExpressionKrn != null && !uiExpressionKrn.equals("")){
	                    flow.setUi(session.getObjectByUid(uiExpressionKrn, 0));
	                    flow.setUiType(activityState.getUiType()!=null?activityState.getUiType():"");
	                }else if (uiExpression != null) {
		                Object ui = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
		                        uiExpression, null, flow,  flow.getNode(),EventType.ACT_USER_INTERFACE, session);
		                if (ui != null && ui instanceof KrnObject) {
		                    flow.setUi((KrnObject) ui);
		                    flow.setUiType(activityState.getUiType()!=null?activityState.getUiType():"");
		                }
		            }
    			} catch (Exception e) {
                    log.error(e, e);
    			}
            }
	        if(flow.getUi()==null){
               var_.put("INTERFACE",1);
                flow.setParam(flow.getParam() | Constants.ACT_PERMIT);
            }else
                var_.put("INTERFACE",0);
	        String transitions="";
            for (TransitionImpl tr : activityState.getLeavingTransitions()) {
            	if("".equals(transitions))
            		transitions = tr.getName()+","+tr.getId()+","+tr.getTo().getId();
            	else
            		transitions +=";"+ tr.getName()+","+tr.getId()+","+tr.getTo().getId();
            }
            flow.setTransitionTo(transitions);
            flow.setTaskColor(activityState.getTaskColor());
        }else if(event.equals(EventType.AFTER_PERFORM_OF_ACTIVITY)){
            if(flow.isServer())
                flow.setServer(false);
            Map<String,Object> var_ = flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
            var_.put("INTERFACE",0);
            flow.setParam(0);
            flow.setUi(null);
            flow.setUiType("");
            flow.setInfUi(null);
            flow.setUiTypeInf("");
            flow.setTaskColor("");

            //Если есть необходимость не комитить сессию (последний шаг подпроцесса с транзакцией суперпроцесса)
            FlowImpl superProcessFlow=(FlowImpl)flow.getProcessInstance().getSuperProcessFlow();
            if (superProcessFlow != null 
            		&& !((ProcessInstanceImpl)flow.getProcessInstance()).isProcess()
                    && Boolean.TRUE.equals(flow.getParentReactivation())
                    && superProcessFlow.getProcessInstance().getTrId()== flow.getProcessInstance().getTrId()
                    && getNextNode(flow.getNode(), null) instanceof EndStateImpl){
            	isSessionCommit=false;
            }
            if(flow.isOpenTransaction() && activityState.getErrorExpression()!=null && !"".equals(activityState.getErrorExpression())){
            	//сохраняем ссылку на действие в ктотором обисано действие при ошибке
            	flow.setErrorActivity(activityState);
            }
            //
       }
        return res;
    }

    private Object processInBoxState(FlowState nextState, Session session)  throws WorkflowException{
        InBoxStateImpl inBoxState = (InBoxStateImpl)nextState.node;
        if (nextState.eventType.equals(EventType.BEFORE_CHECK_XML)) {
            String userName = session.getUserSession().getLogUserName();
        	Log log = getLog(userName);

        	flow.setParam(flow.getParam() | Constants.ACT_IN_BOX);
            // удаление прерывателя процесса
            if ((flow.getParam() & Constants.ACT_CANCEL) == Constants.ACT_CANCEL) {
                flow.setParam(flow.getParam() ^ Constants.ACT_CANCEL);
            }
            KrnObject box_krn=null;
            String boxId = null;
            ASTStart boxExpression = inBoxState.getBoxExpression();
            String boxExpressionKrn = inBoxState.getBoxExpressionKrn();
            if (boxExpressionKrn != null && !boxExpressionKrn.equals("")){
                   try {
                         box_krn=session.getObjectByUid(boxExpressionKrn,0);
                        } catch (KrnException e) {
                            log.error(e, e);
                        }
                   if(box_krn!=null) boxId=""+box_krn.id;
            }else if (boxExpression != null) {
                // get the box of the inbox-state
                Object box_ = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                                                          boxExpression, null, flow,nextState.node, EventType.BOX_EXCHANGE, session);
                if (box_ instanceof String)
                    boxId = (String) box_;
                else if (box_ instanceof Number)
                    boxId = "" + ((Number) box_).longValue();
                else if (box_ instanceof KrnObject)
                    boxId = "" + ((KrnObject) box_).id;
            }
            if (boxId != null)
                flow.setBoxId(Long.valueOf(boxId));
            KrnObject actor_krn=null;
            long[] actorId = new long[0];
            String actorId_=null;
            ASTStart assignmentExpression = inBoxState.getAssignment();
            String assignmentKrn = inBoxState.getAssignmentKrn();
            if(assignmentKrn!=null && !assignmentKrn.equals("")){
                try {
                    actor_krn=session.getObjectByUid(assignmentKrn,0);
                } catch (KrnException e) {
                    log.error(e, e);
                }
            }
            if(actor_krn!=null){
                  actorId= new long[]{actor_krn.id};
            }else if (assignmentExpression != null) {
                // get the assignment of the activity-state
                flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
            	Object actor = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                                                           assignmentExpression, null, flow, nextState.node,EventType.RESPONSIBLE, session);
                if (actor instanceof String){
                    actorId_ = (String) actor;
                    if("SERVER".equals(actorId_))
                        actorId=new long[]{0};
                    else
                        actorId=new long[]{Long.valueOf(actorId_)};
                }else if (actor instanceof Number)
                    actorId = new long[]{((Number) actor).longValue()};
                else if (actor instanceof KrnObject)
                    actorId = new long[]{((KrnObject) actor).id};
                else if (actor instanceof List) {
                    actorId = new long[((List) actor).size()];
                    for (int i = 0; i < ((List) actor).size(); ++i) {
                        Object o = ((List) actor).get(i);
                        if (o != null)
                            actorId[i] = ((KrnObject) o).id;
                    }
                }

            }
            flow.setActorId(actorId);
            ASTStart titleExpr = inBoxState.getTitle();
            if (titleExpr != null) {
                Map<Long, String> title = WfUtils.getResolvExpressionAllLangs(
                		(ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                		titleExpr, null, flow, nextState.node,EventType.TITLE_EXPR, session);
                flow.setTitle(title);
            }
            //Установка заголовка
            titleExpr = inBoxState.getObjTitleExpression();
            if (titleExpr == null)
                titleExpr = flow.getProcessInstance().getProcessDefinition().getTitle();
            if (titleExpr != null) {
                // получение титула для activity-state
            	Map<Long, String> title =  WfUtils.getResolvExpressionAllLangs((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                                                             titleExpr, null, flow, nextState.node,EventType.TITLE_EXPR, session);
                flow.setTitleObj(title);
            }
        } else if (nextState.eventType.equals(EventType.PARS_XML)) {
            if (flow.getBoxId() > 0) {
                flow.setBoxId(-1);
            }
            flow.setParam(flow.getParam() ^ Constants.ACT_IN_BOX);
            if(!"".equals(flow.getStatusMsg()))
            	flow.setStatusMsg("");
            // выполненин действия соответствующего данному событию
            return runActionsForEvent(EventType.PARS_XML, inBoxState, flow, session);
        }
        return null;
    }

    private Boolean  processOutBoxState(OutBoxStateImpl outBoxState, EventType event, Session session)  throws WorkflowException{
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);

    	boolean res=true;
        if (event.equals(EventType.BEFORE_PERFORM_XML)) {
            flow.setParam(flow.getParam() | Constants.ACT_OUT_BOX);
            // удаление прерывателя процесса
            if ((flow.getParam() & Constants.ACT_CANCEL) == Constants.ACT_CANCEL) {
                flow.setParam(flow.getParam() ^ Constants.ACT_CANCEL);
            }
            flow.getVariable(session,exeComp.SA_FLOW_VAR.id).put("MAIL", null);
            flow.getVariable(session,exeComp.SA_FLOW_VAR.id).put("XML", null);
            runActionsForEvent(EventType.PERFORM_XML, outBoxState, flow, session);
        } else if (event.equals(EventType.PERFORM_XML)) {
            long boxId = -1;
            ASTStart boxExpression = outBoxState.getBoxExpression();
            KrnObject box_krn=null;
            String boxExpressionKrn = outBoxState.getBoxExpressionKrn();
            if (boxExpressionKrn != null && !boxExpressionKrn.equals("")){
                   try {
                         box_krn=session.getObjectByUid(boxExpressionKrn,0);
                        } catch (KrnException e) {
                            log.error(e, e);
                        }
                   if(box_krn!=null) boxId=box_krn.id;
            } else if (boxExpression != null) {

                Object box = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                                                         boxExpression, null, flow, outBoxState,EventType.BOX_EXCHANGE, session);
                if (box instanceof String)
                    boxId = Long.valueOf((String) box);
                else if (box instanceof Number)
                    boxId = ((Number) box).longValue();
                else if (box instanceof KrnObject)
                    boxId = ((KrnObject) box).id;
            }
            flow.setBoxId(boxId > 0 ? boxId : 0);
            // выполнение действия соответствующего данному событию
            if (boxId != -1) {
                Box box_ = Session.getMessageCache(exeComp.getDsName()).getBoxById(boxId);
                if(box_!=null){
                    Map<String,Object> var = flow.getVariable(session,exeComp.SA_FLOW_VAR.id);
                    String objId="";
                    Object msg=null;
                    if(box_.getTransportId()==TransportIds.MAIL){
                        try {
                            KrnObject obj=(KrnObject)var.get("MAIL");
                            if(obj!=null){
                                msg=com.cifs.or2.server.exchange.ExchangeUtils.sentMailMessage(session,obj,flow.getProcessInstance().getTrId());
                                objId=""+obj.id;
                            }
                        } catch (Exception e) {
                            log.error(e, e);
                        }
                    }else{
                        msg = var.get("XML");
                    }
                    if (msg != null) {
                        try {
                            box_.send(msg, flow.getId(),objId);
                            Object statusMsg = var.get("STATUS_MSG");
                            if(statusMsg!=null && statusMsg instanceof String){
                            	flow.setStatusMsg((String)statusMsg);
                            	var.remove("STATUS_MSG");
                            }
                        } catch (Throwable e) {
                            var.put("SEND_RESULT", e);
                        }
                    } else {
                        log.info("Для исходящего узла обмена '" + outBoxState.getName() + "'не определено отправляемое сообщение");
                        res=false;
                    }
                }else{
                    log.info("Для исходящего узла обмена '" + outBoxState.getName() + "'не загружен блок обмена");
                    res=false;
                }
            }else{
                log.info("Для исходящего узла обмена '" + outBoxState.getName() + "'не определен блок обмена");
                res=false;
            }
        } else if (event.equals(EventType.AFTER_PERFORM_XML)) {
            flow.setParam(flow.getParam() ^ Constants.ACT_OUT_BOX);
            if(flow.isServer())
                flow.setServer(false);
            if (flow.getBoxId() > 0) flow.setBoxId(-1);
        }
        return res;
    }

    private void processProcessState(FlowState nextState,FlowState oldState, List<FlowImpl> flowsToStart, Session session) throws WorkflowException {
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        ProcessStateImpl processState = (ProcessStateImpl)nextState.node;
        if (EventType.SUB_PROCESS_INSTANCE_START.equals(nextState.eventType)) {
            flow.setParam(flow.getParam() | Constants.ACT_SUB_PROC);
            // получение  sub-process-definition и его start-state
            KrnObject proc_krn=null;
            ASTStart subProcess = processState.getSubProcess();
            String subProcessKrn = processState.getSubProcessKrn();
			ProcessDefinitionImpl procDef=null;
            if (subProcessKrn != null && !subProcessKrn.equals("")) {
                try {
                      proc_krn=session.getObjectByUid(subProcessKrn,0);
                     } catch (KrnException e) {
                         log.error(e, e);
                     }
                procDef = (ProcessDefinitionImpl) defComp.getProcessDefinition(proc_krn.id);
 		        if(procDef==null){
		        	procDef=(ProcessDefinitionImpl)exeComp.deployProcess(proc_krn.id, session);
		        }
            }else if (subProcess != null) {
                Object subProcessDefinition = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                                                                          subProcess, null, flow,nextState.node, EventType.SUB_PROCESS, session);
                
                String res = "";
                if (subProcessDefinition instanceof String)
                	res = (String) subProcessDefinition;
                else if (subProcessDefinition instanceof Number)
                	res = "" + ((Number) subProcessDefinition).longValue();
                else if (subProcessDefinition instanceof KrnObject)
                	res = "" + ((KrnObject) subProcessDefinition).id;
				try {
					procDef = (ProcessDefinitionImpl) defComp.getProcessDefinition(new Long(res));
	 		        if (procDef == null) {
	 		        	procDef=(ProcessDefinitionImpl) exeComp.deployProcess(new Long(res), session);
			        }
				}catch(Exception e){
                    log.error(e, e);
				}
			}
            if (procDef == null) {
                log.info("ОШИБКА!:Не определен подпроцесс:"+processState.getName());
                return;
            }

            String procType = processState.getSubProcessType();
            boolean wait = Constants.SUBPROCESS_PASS_WAIT.equals(procType)
                    || Constants.SUBPROCESS_WAIT.equals(procType) ||"Подпроцесс".equals(procType);
            // действие при создании подпроцесса
            runActionsForEvent(EventType.SUB_PROCESS_INSTANCE_START, processState, flow, session);
            List<String> res = new ArrayList<String>(2);
            boolean par_tr_id=false;
            if(flow.getVariable(session,exeComp.SA_FLOW_VAR.id)!=null && "1".equals(flow.getVariable(session,exeComp.SA_FLOW_VAR.id).get("WITHOUTTRID")))
            		par_tr_id=true;
            // создание подпроцесса
            ExecutionEngine engine = exeComp.createProcessInstance(procDef.getId(), flow.getActorId(), flow, null, session, procType, res,flow.getProcessInstance().getTrId()==0 || par_tr_id,nextState);
            FlowImpl subFlow_= engine.getFlow();
            flowsToStart.add(subFlow_);
            engine.getFlow().setParentReactivation(wait);
            saveProcess(subFlow_, session);
            saveFlow(subFlow_, session);
            //а новое устанавливаем в зависимости от типа подпроцесса
            if (wait) {
                nextState.eventType = EventType.SUB_PROCESS_INSTANCE_COMPLETION;
            } else {
                nextState.eventType = EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION;
            }
            //старое состояние заменяем на новое в случае последующего 
            //отката поток не должен останавливаться
            oldState.node=nextState.node; 
            oldState.eventType=nextState.eventType; 
        } else if (EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION_2.equals(nextState.eventType)) {
            runActionsForEvent(EventType.SUB_PROCESS_INSTANCE_COMPLETION, processState, flow, session);
        }
    }

    private void processDecision(DecisionImpl decision, EventType event, Session session)  throws WorkflowException{
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        if (event.equals(EventType.BEFORE_DECISION)) {
            Object name = runActionsForEvent(EventType.BEFORE_DECISION, decision, flow, session);
            if (name == null) {
                log.info("ОШИБКА!: В выборе '" + decision.getName() + "' для процесса '"
                        +decision.getProcessDefinition().getName()
                        +"' неверно определяется имя перемещения(null)!");
            }
            for (Object tr : decision.getLeavingTransitions()) {
                TransitionImpl transition = (TransitionImpl) tr;
                if(transition.getName().equals(name)){
                    flow.setTransitionTo(transition.getName()+","+transition.getId()+","+transition.getTo().getId());
                    break;
                }
            }
        }else if (event.equals(EventType.AFTER_DECISION)) {
            runActionsForEvent(EventType.AFTER_DECISION, decision, flow, session);
        }
    }

    private void processFork(ForkImpl fork, EventType event, List<FlowImpl> flowsToStart, Session session) throws WorkflowException {
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);

    	if (event.equals(EventType.BEFORE_FORK)) {
            runActionsForEvent(EventType.FORK, fork, flow, session);
        }else if (event.equals(EventType.FORK)) {
            Collection ctxs = (Collection)flow.getVariable(session,exeComp.SA_FLOW_VAR.id).get("FORK_CONTEXTS");
            Iterator it=null;
            if(ctxs!=null && ctxs.size()>0)
                it=ctxs.iterator();
            int count_=ctxs==null || ctxs.size()==0?1:ctxs.size();
            for(int i=0;i<count_;++i){
                String str_ctx="";
                Object value=null;
                // инициализация разветвленных потоков
                if(it!=null && it.hasNext()){
                    str_ctx= "_"+i;
                    value=it.next();
                }
                Collection<FlowImpl> chs=flow.getChildren();
                for (Object o : fork.getLeavingTransitions()) {
                    TransitionImpl transition = (TransitionImpl) o;
                    String to_id = transition.getName()+","+transition.getId()+","+transition.getTo().getId();
                    if (to_id == null) {
                        log.info("ОШИБКА!: В разветвлении '" + flow.getNode().getName() + "' не задано имя перемещения!!!");
                    }
                    String t_name= to_id+str_ctx;
                    //проверяем не создавался ли поток перед падением(если оно было)
                    FlowImpl forkFlow=null;
                    if(chs.size()>0){
                    	for(FlowImpl ch:chs){
                    		if(t_name.equals(ch.getName())){
                                forkFlow=ch;
                    			break;
                    		}
                    	}
                    }
                    if(forkFlow==null){
	                    ExecutionEngine engine= exeComp.startForkFlow(t_name, flow, session);
	                    forkFlow=engine.getFlow();
	                    forkFlow.setNode(fork);
	                    forkFlow.setEventType(EventType.AFTER_FORK);
	                    forkFlow.setTransitionTo(to_id);
	                    forkFlow.setParent(flow);
	                    forkFlow.getVariable(session,exeComp.SA_FLOW_VAR.id).putAll(flow.getVariable(session,exeComp.SA_FLOW_VAR.id));
	                    if(ctxs!=null && ctxs.size()>0)
	                        forkFlow.getVariable(session,exeComp.SA_FLOW_VAR.id).put("FORK_CONTEXT",value);
	                    forkFlow.setUser(flow.getUser());
	                    forkFlow.setIp(flow.getIp());
	                    forkFlow.setComputer(flow.getComputer());
	                    forkFlow.setParentReactivation(Boolean.TRUE);
	                    flow.addChild(forkFlow);
                    }
                    //проверяем сохранился ли поток перед падением(если оно было)
                    if(!flowsToStart.contains(forkFlow)){
	                    saveFlow(forkFlow, session);
	                    flowsToStart.add(forkFlow);
                    }
                }
            }
            return;
      }
        return;
    }

    private void processJoin(JoinImpl join, EventType event, Session session) throws WorkflowException {

        if (event.equals(EventType.JOIN)) {

            flow.setEnd(new Date(System.currentTimeMillis()));

            FlowImpl parentFlow = (FlowImpl)flow.getParent();

            boolean parentReactivation = false;

            if (parentFlow != null)
            synchronized (parentFlow) {
                if (flow.getVariable(session,exeComp.SA_FLOW_VAR.id) != null) {
                    //Передача переменных  в родительский поток
                    parentFlow.getVariable(session,exeComp.SA_FLOW_VAR.id).putAll(flow.getVariable(session,exeComp.SA_FLOW_VAR.id));
                }
                if (flow.getParentReactivation()) {

                    // check if the parent needs to be reactivated
                    Collection concurrentFlows = getOtherActiveConcurrentFlows(flow);
                    if (concurrentFlows.size() == 0) {
                        // if no concurrent flows are present any more, reactivation is forced
                        parentReactivation = true;

                    } else {
                        // if other concurrent flows are present, the decision to reactivate is left
                        // to the joinExpression (if there is one specified)
                        Object joinResult = runActionsForEvent(EventType.JOIN, join, flow, session);
                        if (joinResult != null && joinResult instanceof Integer)
                            parentReactivation = (Integer) joinResult == 1;
                    }
                    parentFlow.removeChild(flow);
                    if (parentReactivation) {
                        // make sure the other concurrent flows will not reactivate the
                        // parent again
                        for (Object concurrentFlow1 : concurrentFlows) {
                            FlowImpl concurrentFlow = (FlowImpl) concurrentFlow1;
                            concurrentFlow.setParentReactivation(false);
                            //не сохраняем весь набор переменных, т.к. они не меняются
                            saveFlow(concurrentFlow, false,session);
                        }
                    }
                }
	            if (parentReactivation) {
	                // Если есть суперпоток, то дожидаемся его завершения
	                ExecutionEngine parentEngine = exeComp.getEngine(parentFlow.getId(),session);
	                if (parentEngine != null) {
	                    parentEngine.join();
	                }
	                // reactivate the parent by first setting the parentflow into the executionContext
	                Node parentNode=parentFlow.getNode();
	                if (parentNode != null) {
	                	if(parentNode instanceof JoinImpl){//если это не возобновление после падения
		                    parentFlow.setNode(getNextNode(parentFlow.getNode(), null));
		                    parentFlow.setEventType(null);
	                	}
	                    saveFlow(parentFlow, session);
	                    exeComp.startFlow(parentFlow);
	                }
	            }
	        }
        }
    }
    
    private void processStartSyncState(FlowState state, Session s) throws WorkflowException {
    	if (state.eventType.equals(EventType.SYNC_BEFORE_START)) {
            String userName = s.getUserSession().getLogUserName();
        	Log log = getLog(userName);
	        synchronized(synchron){
                String synKey=getSynKey((NodeImpl)state.node,s);
                List<FlowImpl> ls = synchron.get(synKey);
                if(ls==null || ls.size()==0){
                	if(ls==null) ls=new ArrayList<FlowImpl>();
                	ls.add(flow);
	                synchron.put(synKey,ls);
	                flow.setSyncNode(state.node.getId());
		        	log.debug("@" + flow.getId() + " START SYN");
	            } else {
	            	addWaitFlow(synKey);
	            	state.eventType = EventType.SYNC_WAIT_START;
		        	log.debug("@" + flow.getId() + " WAIT SYN");
	            }
	        }
    	}
    }
    private void processError(Session s, Log log){
    	Node node=flow.getNode();
    	FlowImpl errorFlow=flow;
    	ActivityStateImpl errorActivity=null;
    	if ((node instanceof ActivityStateImpl) && ((ActivityStateImpl)node).getErrorExpression()!=null && !"".equals(((ActivityStateImpl)node).getErrorExpression())){
    		errorActivity=(ActivityStateImpl)node;//ошибка на активити
    	}else if(flow.getErrorActivity()!=null) {
    		errorActivity=flow.getErrorActivity();//ошибка на одном из следующих шагов после активити
    	}else if(flow.getErrorFlow()!=null) {
    		errorFlow=flow.getErrorFlow();//ошибка при завершении суперпроцесса 
    		errorActivity=errorFlow.getErrorActivity();
    	}
    	if (errorActivity!=null){
    		String errorExpression=errorActivity.getErrorExpression();
    		try {
            	SrvOrLang orl = SrvOrLang.class.cast(s.getOrLang());
            	Stack<String> stack = new Stack<String>();
                orl.evaluate(errorExpression, errorFlow.getVariable(), (ProcessDefinitionImpl) errorFlow.getProcessInstance().getProcessDefinition(), false, stack);
				s.commitTransaction();
    		}catch(Throwable e) {
    			log.error(e,e);
    			log.warn("Ошибка при выполнении действий определенный в свойстве:after-error-of-activity");
    		}
    	}
    }
    public boolean checkSynKey(String synKey) throws WorkflowException{
        synchronized(synchron){
            List<FlowImpl> ls = synchron.get(synKey);
            return ls!=null && ls.size()>0;
        }
    }
    
    public String getSynKey(Session s) throws WorkflowException{
    	String synKey="";
        String nodeId=flow.getSyncNode();
        NodeImpl node=(NodeImpl)flow.getProcessInstance().getProcessDefinition().getNode(nodeId);
        if(node!=null){
            synKey=getSynKey(node,s);
        }
        return synKey;
    }
    public String getSynKey(NodeImpl node,Session s) throws WorkflowException{
        String userName = s.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        String synParamKey ="";
        Object res_sync = runActionsForEvent(EventType.SYNC_START, node, flow, s);
        if (res_sync != null) {
            synParamKey = ""+res_sync;
        }
        log.debug("@" + flow.getId() + " BEFORE SYN");
        String synKey =
            flow.getProcessInstance().getProcessDefinition().getId()
            + "_" + node.getId();
        if(!synParamKey.equals("")){
            synKey += "_"+synParamKey;
        }
        log.debug("SET SYNCKEY:" + synKey );
        return synKey;
    }

    public void loadSynKey(Session s) throws WorkflowException{
        String nodeId=flow.getSyncNode();
        NodeImpl node=(NodeImpl)flow.getProcessInstance().getProcessDefinition().getNode(nodeId);
        if(node!=null){
            String synKey=getSynKey(node,s);
            List<FlowImpl> ls = synchron.get(synKey);
        	if(ls==null) ls=new ArrayList<FlowImpl>();
        	ls.add(flow);
            synchron.put(synKey,ls);
        }
    }

    public void addWaitFlow(String synKey) {
    	if (synKey == null) {
        	synKey =
        		flow.getProcessInstance().getProcessDefinition().getId()
        		+ "_" + flow.getNode().getId();
    	}
        Queue<FlowImpl> queue = waitFlow.get(synKey);
        if(queue == null) {
            queue = new LinkedList<FlowImpl>();
            waitFlow.put(synKey, queue);
        }
        queue.offer(flow);
    }
    
    private void processEndSyncState(Session s, List<FlowImpl> flowsToResume,boolean isEndSyncState) throws WorkflowException{
        String userName = s.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        synchronized(synchron){
            String synParamKey ="";
            Object res_sync = runActionsForEvent(EventType.SYNC_START, (NodeImpl)flow.getProcessInstance().getProcessDefinition().getNode(flow.getSyncNode()), flow, s);
            if (res_sync != null) {
                synParamKey = ""+res_sync;
            }
        	String synKey =
        		flow.getProcessInstance().getProcessDefinition().getId()
        		+ "_" + flow.getSyncNode();
            if(!synParamKey.equals("")){
                synKey += "_"+synParamKey;
            }
            log.debug("REMOVE SYNCKEY:" + synKey );
            List<FlowImpl> ls = synchron.get(synKey);
        	if(ls!=null)
        		ls.remove(flow);
            if(isEndSyncState)
                flow.setSyncNode("");
            Queue<FlowImpl> queue = waitFlow.get(synKey);
            if(queue != null && (ls==null || ls.size()==0)){
                while(queue.size() > 0){
                    FlowImpl flowToResume = queue.remove();
                    ExecutionEngine engine = exeComp.getEngine(flowToResume.getId(),s);
                    if(engine!=null){
                        engine.join();
                        flowToResume.setEventType(null);
                        flowsToResume.add(flowToResume);
                        log.debug("@" + flowToResume.getId() + " flowToResume");
                        break;
                    }else{
                        log.debug("@" + flowToResume.getId() + " flowToResumeNotFind");
                    }
                }
            }else{
                log.debug("@ queue flowToResume EMPTY");
            }
        	log.debug("@" + flow.getId() + " END SYN");
        }
    }
    
    private Collection getOtherActiveConcurrentFlows(FlowImpl flow) {
        String userName = "sys";
    	Log log = getLog(userName);

    	HashSet<FlowImpl> res = new HashSet<FlowImpl>();
        Collection children = flow.getParent().getChildren();
        if (children != null) {
            for (Object aChildren : children) {
                FlowImpl flow_ = (FlowImpl) aChildren;
                if (flow.getId() != flow_.getId() && flow_.getEnd() == null)
                    res.add(flow_);
            }
        } else
            log.info("ОШИБКА!: Не определен родительский поток!!!");
        return res;
    }

    private void unlock(FlowImpl flow, Session session) throws KrnException {
    	long flowId = flow.getId();
    	session.unlockFlowObjects(flowId);
    }
    public void saveFlow(FlowImpl flow, Session session) throws WorkflowException {
    	saveFlow(flow, true,session);
    }

    public void saveFlow(FlowImpl flow, boolean isAllVarSave,Session userSession) throws WorkflowException {
    	if(flow!=null && flow.isOpenTransaction()) return;//Если базовая транзакция не комитится 
    													  //то и нет необходимости сохранять данные flow
        String userName = userSession.getUserSession().getLogUserName();
        Session session = userSession;
    	Log log = getLog(userName);
        //блокировка записи flow осуществляется в сессии flowSession, поэтому в ней необходимо сохранять его атрибуты
    	String expr=" Save flow-flowId="+flow.getId()+"; Session=" + session.getUserSession().getId();
		ExecutionComponent.writeExprToFile(expr);
    	try {
    		if(session.getObjectById(flow.getId(), 0) == null) return;
	        //поток
            if(isRunning()) flow.setCurrent(new Date(System.currentTimeMillis()));
	        KrnObject obj = flow.getFlowObj();
    		Map<Pair<KrnAttribute, Long>, Object> cache = session.setValue(
    				obj, exeComp.SA_CUR_FLOW, 0, flow.getCurrent(), null); 
	        if (flow.getEnd() != null) {
                session.setValue(obj, exeComp.SA_END_FLOW, 0, flow.getEnd(), cache);
	            unlock(flow, session);
	        }
	        if(flow.isChanges("control")){
                session.setValue(obj, exeComp.SA_CONTROL_FLOW, 0, flow.getControl(), cache);
            }
	        if(flow.isChanges("actorId") && flow.getActorId().length>0 && flow.getActorId()[0]>0){
                session.setValue(obj, exeComp.SA_ACTOR, 0, flow.getActorId()[0], cache);
            }
            if (flow.getUser() != null && flow.isChanges("user"))
	            session.setValue(obj, exeComp.SA_USER, 0, flow.getUser(), cache);
	        if(flow.isChanges("ui")){
	        	String tc= flow.getTaskColor();
	        	tc=(tc!=null && !"".equals(tc))? ";"+tc:"";//цвет строки задания
	            if(flow.getUi() != null && exeComp.SA_UI.typeClassId== CID_STRING){
	            	String uiStr=""+flow.getUi().id+","+flow.getUi().uid+","+flow.getUi().classId 
	            			+ (flow.getUiName()!=null && !"".equals(flow.getUiName())?","+flow.getUiName():"");
                    session.setValue(obj, exeComp.SA_UI, 0, uiStr, cache);
	            	session.setValue(obj, exeComp.SA_TYPE_UI, 0, flow.getUiType()+tc, cache);
	            } else if(flow.getUi() != null){
	            	session.setValue(obj, exeComp.SA_UI, 0, flow.getUi().id, cache);
	            	session.setValue(obj, exeComp.SA_TYPE_UI, 0, flow.getUiType()+tc, cache);
                }else{
	                session.setValue(obj, exeComp.SA_UI, 0, null, cache);
	            	session.setValue(obj, exeComp.SA_TYPE_UI, 0, tc, cache);
	            }
	        }
	        if(flow.isChanges("boxId")){
	            if (flow.getBoxId() > 0) {
	                session.setValue(obj, exeComp.SA_BOX, 0, flow.getBoxId(), cache);
	            } else {
	                flow.setBoxId(0);
	                session.setValue(obj, exeComp.SA_BOX, 0, null, cache);
	            }
	        }
	        if(!rollback && flow.isChanges("cutObj")){
            	StringBuffer sb= new StringBuffer();
	            if (flow.getCutObj() != null) {
	            	if(flow.getCutObj().length>0 && flow.getCutObj()[0]!=null){
		            	sb.append(""+flow.getCutObj()[0].id+","+flow.getCutObj()[0].uid+","+flow.getCutObj()[0].classId);
		                for (int i = 1; i < flow.getCutObj().length; ++i){
		                	sb.append(";").append(""+flow.getCutObj()[i].id+","+flow.getCutObj()[i].uid+","+flow.getCutObj()[i].classId);
		                }
	            	}
	            }else sb.append("-1");
                session.setString(flow.getId(), exeComp.SA_CUT_OBJ.id, 0,0,false,sb.toString(), 0);
	        }
	        if (flow.isChanges("children") && flow.getChildren() != null && flow.getChildren().size() > 0) {
	            int i = 0;
	            synchronized (flow) {
            		for (FlowImpl flow1 : flow.getChildren())
            			session.setObject(flow.getId(), exeComp.SA_CHILDREN_FLOW.id, i++, flow1.getId(), 0, false);
	            }
	        }
	        if(flow.isChanges("title")){
	            for (Long langId : flow.getTitle().keySet()) {
		            session.setValue(obj, exeComp.SA_TITLE, langId, flow.getTitle().get(langId), cache);
	            }
	        }
	        if(flow.isChanges("statusMsg")){
	            String statusMsg=flow.getStatusMsg();
	            session.setValue(obj, exeComp.SA_STATUS_MSG, 0, statusMsg, cache);
	        }
	        if(!rollback && flow.isChanges("titleObj")){
	            for (Long langId : flow.getTitleObj().keySet()) {
		            session.setValue(obj, exeComp.SA_TITLE_OBJ, langId, flow.getTitleObj().get(langId), cache);
	            }
	        }
	        if (flow.isChanges("event")){
	            session.setValue(obj, exeComp.SA_NODE_EVENT, 0, flow.getEventType() !=null ? flow.getEventType().toString() : "", cache);
                try{
                    exeComp.putCorelId(flow);
                }catch(JDOMException ex){
                    log.error(ex, ex);
                }
            }
	        if(flow.isChanges("corelId")){
            	session.setValue(obj, exeComp.SA_COREL_ID, 0, flow.getCorelId(), cache);
	        }
            if(flow.isChanges("syncNode")){
	            String sincNode=flow.getSyncNode();
	            session.setValue(obj, exeComp.SA_SYNCNODE, 0, sincNode, cache);
	        }
	        if(flow.isChanges("nodes")){
	            Collection<String> nodes = flow.getNodes();
	            synchronized(nodes) {
	            	StringBuffer sb= new StringBuffer();
	            	for(String node:nodes){
	            		if(sb.length()==0)
	            			sb.append(node);
	            		else
	            			sb.append(";").append(node);
	            	}
                    session.setString(flow.getId(), exeComp.SA_NODE.id, 0, 0, false, sb.toString(), 0);
                    if(exeComp.SA_TYPE_NODE!=null && flow.getNode()!=null){
                        String t_=flow.getNode().getType();
                        session.setString(flow.getId(), exeComp.SA_TYPE_NODE.id, 0, 0, false, t_, 0);
                    }
	            }
            }
	        if(flow.isChanges("parentReactivation"))
	        session.setValue(obj, exeComp.SA_PARENT_REACTIVATE, 0, flow.getParentReactivation().booleanValue() ? 1 : 0, cache);
	        if(flow.isChanges("transitionToId"))
	        session.setValue(obj, exeComp.SA_TRANSITION, 0, flow.getTransitionTo(), cache);
	        if(flow.isChanges("param"))
	        session.setValue(obj, exeComp.SA_PERMIT, 0, flow.getParam(), cache);
	        if(isAllVarSave){
		        byte[] buf = WfUtils.saveToXml(flow.getVariable(), null, null);
		        session.setValue(obj, exeComp.SA_FLOW_VAR, 0, buf, cache);
		        session.saveValues(obj, 0, cache);
	        }
    	} catch (Throwable e) {
            int codeError=0;
            if(e instanceof OrException){
                codeError=((OrException)e).getErrorCode();
            }else if(e instanceof KrnException){
                codeError=((KrnException)e).code;
            }
            log.error(e, e);
            log.error("codeError="+codeError);
            String msg="Ошибка при сохранении переменных потока";
            session.writeLogRecord(SystemEvent.ERROR_EXECUTION_ENGINE,msg,exeComp.SC_FLOW.id,-1);
            throw new WorkflowException(msg,codeError);
    	}
    }

    public void saveProcess(FlowImpl flow, Session session) {
    	if(flow!=null && flow.isOpenTransaction()) return;//Если базовая транзакция не комитится 
		  //то и нет необходимости сохранять данные process
    	ProcessInstanceImpl process= (ProcessInstanceImpl)flow.getProcessInstance();
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        try {
            //процесс
            if (process.getEnd() != null){
                session.setTime(process.getId(), exeComp.SA_END_PROCESS.id, 0, Funcs.convertTime(process.getEnd()), 0);
                session.setLong(process.getId(), exeComp.SA_PROCESS.id, 0, process.isProcess() ? 1 : 0, 0);
            }else{
            	session.setString(process.getId(),exeComp.SA_OBSERVERS.id, 0, 0, false, process.getObservers(), 0);
            	session.setString(process.getId(),exeComp.SA_UI_OBSERVERS.id, 0, 0, false, process.getUiObservers(), 0);
            	session.setString(process.getId(),exeComp.SA_TYPE_UI_OBSERVERS.id, 0, 0, false, process.getTypeUiObservers(), 0);
            }
            FlowImpl superProcessFlow= (FlowImpl)process.getSuperProcessFlow();
            if(superProcessFlow!=null)
            	session.setObject(process.getId(),exeComp.SA_SUPER_FLOW.id, 0, superProcessFlow.getId(), 0, false);
      } catch (KrnException ex) {
            log.error(ex, ex);
        }
    }

    private void processRollback(Session s) throws WorkflowException {
        String userName = s.getUserSession().getLogUserName();
    	Log log = getLog(userName);
        try {
            log.info("Flow:" + flow.getId() + " rolling back");
            flow.setEnd(new Date(System.currentTimeMillis()));
            ProcessInstanceImpl processInstance = (ProcessInstanceImpl) flow.getProcessInstance();
            processInstance.setEnd(new Date(System.currentTimeMillis()));
            long tr_id=flow.getProcessInstance().getTrId();
            if(tr_id>0){
            	s.rollbackLongTransaction(tr_id);
            	if(flow.isOpenTransaction())
            		log.info("OPEN_TRANSACTION: ended after rollbackLongTransaction:"+tr_id+";fowId:"+flow.getId()+";CONNECTION_ID:"+s.getConnectionId());
            	log.info("Flow:" + flow.getId() + " rollbackLongTransaction:"+tr_id);
            }
            log.info("Flow:" + flow.getId() + " rolled back");
        } catch (KrnException e) {
        		s.writeLogRecord(SystemEvent.ERROR_EXECUTION_ENGINE,e.getMessage(),exeComp.SC_FLOW.id,-1);
                throw new WorkflowException(e.getMessage(),e.code);
        }
    }

    private void processNullState(
    		Node node,
    		EventType event,
    		Session s,
    		List<FlowImpl> flowsToResume,
    		List<FlowImpl> flowsToRollback
    ) throws WorkflowException {
        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) flow.getProcessInstance();
        FlowImpl superProcessFlow= (FlowImpl)processInstance.getSuperProcessFlow();
//        ProcessStateImpl superNode=null;
//        if(superProcessFlow!=null && superProcessFlow.getNode() instanceof ProcessStateImpl){
//            superNode= (ProcessStateImpl)superProcessFlow.getNode();
//        }
        DefinitionObjectImpl pdef =
                (DefinitionObjectImpl)processInstance.getProcessDefinition();

        if (event.equals(EventType.PROCESS_INSTANCE_START)) {
            //runActionsForEvent(event, pdef, flow, s);
        } else if (event.equals(EventType.BEFORE_PROCESS_INSTANCE_CANCEL)) {
        		//Выполняем операции при откате в ненулевой транзакции(считывание данных)
        		runActionsForEvent(EventType.BEFORE_PROCESS_INSTANCE_CANCEL, pdef, flow, s);
        } else if (event.equals(EventType.PROCESS_INSTANCE_CANCEL)) {
        	//Собираем все порожденные подпроцессы в той же транзакции для отката
            if(!((ProcessInstanceImpl)flow.getProcessInstance()).isProcess()){
                FlowImpl topFlow = getTopFlow();
                FlowImpl superFlow=(FlowImpl)topFlow.getProcessInstance().getSuperProcessFlow();
                rollbackFlow(topFlow, flowsToRollback);
                if(superFlow!=null){
                    superFlow.setEventType(EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION);
                    saveFlow(superFlow, s);
                    flowsToResume.add(superFlow);
                }
            }
        	//Выполняем действия в нулевой транзакции, т.к. ненулевая на данный момент откачена
       		runActionsForEvent(EventType.PROCESS_INSTANCE_CANCEL, pdef, flow, s);
        } else if (event.equals(EventType.PROCESS_INSTANCE_END)) {
                runActionsForEvent(EventType.PROCESS_INSTANCE_END, pdef, flow, s);
                if (superProcessFlow != null) {
                    // Если есть суперпоток, то дожидаемся его завершения
                    ExecutionEngine superEngine = exeComp.getEngine(superProcessFlow.getId(),s);
                    if(superEngine!=null)
                        superEngine.join();
                }

                String userName = s.getUserSession().getLogUserName();
                Log log = getLog(userName);
              if (superProcessFlow != null && !processInstance.isProcess()
                        && Boolean.TRUE.equals(flow.getParentReactivation())) {
                    if(superProcessFlow.getProcessInstance().getTrId()!= processInstance.getTrId()){
                        try {
                        	boolean deleteRefs = false,isNotWriteSysLog=false,isLogCommitLongTr=false;
                        	Map<String, Object> vars = flow.getVariable(s,exeComp.SA_FLOW_VAR.id);
                        	if (vars != null) {
                        		deleteRefs = Integer.valueOf(1).equals(vars.get("DELETE_REFS"));
                        		isNotWriteSysLog = "1".equals(vars.get("WITHOUTSYSLOG"));
                        		isLogCommitLongTr = "1".equals(vars.get("WITHLOGCOMMITLT"));
                                Context ctx = new Context(new long[0], 0, 0);
                                ctx.langId = s.getUserSession().getIfcLang().id;
                        		ctx.pdId=processInstance.getTrId();
                                ctx.flowId=flow.getId();
                                ctx.trId = processInstance.getTrId();
                                ctx.isNotWriteSysLog=isNotWriteSysLog;
                                ctx.isLogCommitLongTr =isLogCommitLongTr;
                                s.setContext(ctx);
                        	}
                            s.commitLongTransaction(processInstance.getTrId(), 0, deleteRefs);
                            log.info("Flow:" + flow.getId() + " commitLongTransaction:"+processInstance.getTrId());
                            if(flow.isOpenTransaction()) {
                            	flow.setOpenTransaction(false);//после этого необходим вынужденный комит
                            	log.info("OPEN_TRANSACTION: ended after commitLongTransaction:"+processInstance.getTrId()+";fowId:"+flow.getId()+";CONNECTION_ID:"+s.getConnectionId());
                            }
                        } catch (KrnException e) {
                            log.error("Flow:" + flow.getId() + " FAILED commitLongTransaction:"+processInstance.getTrId());
                            throw new WorkflowException(e.getMessage(),e.code);
                        }
                    }else
                    	isSessionCommit=false;
                    superProcessFlow.setEventType(EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION);
                    if (flow.getVariable(s,exeComp.SA_FLOW_VAR.id) != null ) {
                        //Передача переменных из подпроцесса в суперпроцесс
                        Map<String,Object> var=flow.getVariable(s,exeComp.SA_FLOW_VAR.id);
                        if(superProcessFlow.getNode() instanceof ProcessStateImpl && Constants.NOT_RETURN_VAR.equals(((ProcessStateImpl)superProcessFlow.getNode()).getReturnVarType())){
                            for (String s1 : var.keySet()) {
                                if (s1.substring(0, 1).equals("_")) {
                                    superProcessFlow.getVariable(s,exeComp.SA_FLOW_VAR.id).put(s1, var.get(s1));

                                }
                            }
                        }else{
                        	superProcessFlow.getVariable(s,exeComp.SA_FLOW_VAR.id).putAll(var);
                        }
                    }
                    if(flow.isOpenTransaction()) {
                    	superProcessFlow.setOpenTransaction(true);
                        if(flow.getErrorActivity()!=null){//сохраняем ссылку на flow в котором описаны действия при ошибке
                        	superProcessFlow.setErrorFlow(flow);
                        }
                    }
                    saveFlow(superProcessFlow, s);
                    flowsToResume.add(superProcessFlow);
                } else {
                    try {
                    	boolean deleteRefs = false,isNotWriteSysLog=false,isLogCommitLongTr=false;
                    	Map<String, Object> vars = flow.getVariable(s,exeComp.SA_FLOW_VAR.id);
                    	if (vars != null) {
                    		deleteRefs = Integer.valueOf(1).equals(vars.get("DELETE_REFS"));
                    		isNotWriteSysLog = "1".equals(vars.get("WITHOUTSYSLOG"));
                    		isLogCommitLongTr = "1".equals(vars.get("WITHLOGCOMMITLT"));
                            Context ctx = new Context(new long[0], 0, 0);
                            ctx.langId = s.getUserSession().getIfcLang().id;
                    		ctx.pdId=processInstance.getTrId();
                            ctx.flowId=flow.getId();
                            ctx.trId = processInstance.getTrId();
                            ctx.isNotWriteSysLog=isNotWriteSysLog;
                            ctx.isLogCommitLongTr =isLogCommitLongTr;
                            s.setContext(ctx);
                    	}
                        s.commitLongTransaction(processInstance.getTrId(), 0, deleteRefs);
                        log.info("Flow:" + flow.getId() + " commitLongTransaction:"+processInstance.getTrId());
                        if(flow.isOpenTransaction()) {
                        	flow.setOpenTransaction(false);//после этого необходим вынужденный комит
                        	log.info("OPEN_TRANSACTION: ended after commitLongTransaction:"+processInstance.getTrId()+";fowId:"+flow.getId()+";CONNECTION_ID:"+s.getConnectionId());
                        }	
                    } catch (KrnException e) {
                        log.error("Flow:" + flow.getId() + " FAILED commitLongTransaction:"+processInstance.getTrId());
                        throw new WorkflowException(e.getMessage(),e.code);
                    }
                }

                flow.setEnd(new Date(System.currentTimeMillis()));
                processInstance.setEnd(new Date(System.currentTimeMillis()));
                log.info("Завершен процесс:" + processInstance.getProcessDefinition().getName() +
                        "; id=" + processInstance.getId() +
                        "; пользователь:" + s.getUserSession().getUserName());
               
                s.writeLogRecord(SystemEvent.EVENT_PROCESS_END,
        			processInstance.getProcessDefinition().getName(),exeComp.SC_FLOW.id,-1);
        		waitChildSession=null;//если подпроцесс ждет коммита, то эта операция позволяет суперпроцессу сделать коммит
        } else if (!flow.isRollback()) {
        	String nodeTitle = null;
        	if (node != null) {
	            ASTStart titleExpr = node.getTitle();
	            if (titleExpr != null) {
	                Map<Long, String> title = WfUtils.getResolvExpressionAllLangs(
	                		(ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
	                		titleExpr, null, flow, node, EventType.TITLE_EXPR, s);
	                nodeTitle = (title != null && title.size() > 0) ? title.values().iterator().next() : null; 
	            }
        	}
            if (nodeTitle == null) nodeTitle = "безымянный";
        	String nodeType = node != null ? node.getType() : "неизвестный";
        	throw new WorkflowException(
        			"flow=" + flow.getId() + "; node=" + nodeTitle + "(" + nodeType +
        			"); event=" + event + "; msg=Ошибочное состояние потока");
        }
   }

    private FlowImpl getTopFlow() {
        FlowImpl res = flow;
        FlowImpl forkFlow;
        FlowImpl superFlow = null;
        while ((forkFlow = (FlowImpl)res.getParent()) != null
                || (superFlow = (FlowImpl)res.getProcessInstance().getSuperProcessFlow()) != null)
            if (forkFlow != null) {
                res = forkFlow;
            } else{
                if(superFlow.getNode() instanceof ProcessStateImpl
                        && Constants.SUPERPROCESS_CONTINUE.equals(((ProcessStateImpl)superFlow.getNode()).getSubRollbackType())){
                    break;
                }
                res = superFlow;
            }
        return res;
    }

    private void rollbackFlow(FlowImpl flow, List<FlowImpl> flowsToRollback) {
        Collection children = flow.getChildren();
        for (Object aChildren : children) {
            FlowImpl child = (FlowImpl) aChildren;
            rollbackFlow(child, flowsToRollback);
        }
        if (flow.getSubProcessInstance() != null) {
            FlowImpl subFlow = (FlowImpl)flow.getSubProcessInstance().getRootFlow();
            if (subFlow != null) {
                rollbackFlow(subFlow, flowsToRollback);
            }
        }
        flowsToRollback.add(flow);
    }

    private void processStartState(StartStateImpl node, EventType event, Session s)  throws WorkflowException {
        runActionsForEvent(event, node, flow, s);
    	//установка наблюдателей
        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) flow.getProcessInstance();
        FlowImpl superProcessFlow= (FlowImpl)processInstance.getSuperProcessFlow();
    	if(superProcessFlow!=null && processInstance.getProcessDefinition().getInspectors() != null){
			String userName = s.getUserSession().getLogUserName();
			Log log = getLog(userName);
			try {
				WfUtils.setProcessProperties(flow, s);
				saveProcess(flow, s);
			} catch (KrnException e) {
				log.error(e, e);
			}
    	}
    }

	private void process(Session s, Log log) throws WorkflowException,
			KrnException {
		long[] oldUserId = flow.getActorId().length > 0 ? flow.getActorId()
				: new long[] { flow.getUser().id };

		List<FlowImpl> flowsToResume = new ArrayList<FlowImpl>();
		List<FlowImpl> flowsToRollback = new ArrayList<FlowImpl>();
		List<KrnObject> subFlowsToRollback = new ArrayList<KrnObject>();
		List<FlowImpl> flowsToStart = new ArrayList<FlowImpl>();
		boolean isUpdate=true;
		boolean flowRemoved=false;
		isSessionCommit=true;
		int errorCode = 0;
		FlowState oldState=new FlowState(flow.getNode(),flow.getEventType());
		while (!waitState() || rollback) {

			FlowState state = getNextState();
			Object res = null;
			boolean isNotBreak = true;

			// Если текущее состояние потока совпадает со следующим, то
			// это зациклившийся поток.
			// Устанавливаем следующее состояние в откат.
			String nextNodeId = (state.node != null) ? state.node.getId()
					: null;
			String nodeId = (flow.getNode() != null) ? flow.getNode().getId()
					: null;
			if (nextNodeId != null) {
				if ((nextNodeId.equals(nodeId) || (nodeId != null && nodeId
						.equals(nextNodeId)))
						&& state.eventType == flow.getEventType()) {
					rollback = true;
				}
			}
			try {
				if (rollback) {
					processNullState(state.node, state.eventType, s,
							flowsToResume, flowsToRollback);
					flow.setEventType(state.eventType);
					state = getNextState();
					processRollback(s);
					processNullState(state.node, state.eventType, s,
							flowsToResume, flowsToRollback);
				} else if (state.node == null) {
					processNullState(state.node, state.eventType, s,
							flowsToResume, flowsToRollback);

				} else if (state.node instanceof StartStateImpl) {
					processStartState((StartStateImpl) state.node,
							state.eventType, s);

				} else if (state.node instanceof ActivityStateImpl) {
					res = processActivityState((ActivityStateImpl) state.node,
							state.eventType, s);
				} else if (state.node instanceof DecisionImpl) {
					processDecision((DecisionImpl) state.node, state.eventType,
							s);

				} else if (state.node instanceof ForkImpl) {
					processFork((ForkImpl) state.node, state.eventType,
							flowsToStart, s);

				} else if (state.node instanceof JoinImpl) {
					processJoin((JoinImpl) state.node, state.eventType, s);

				} else if (state.node instanceof ProcessStateImpl) {
					processProcessState(state,oldState, flowsToStart, s);

				} else if (state.node instanceof InBoxStateImpl) {
					res = processInBoxState(state, s);

				} else if (state.node instanceof OutBoxStateImpl) {
					isNotBreak = processOutBoxState((OutBoxStateImpl) state.node,
							state.eventType, s);

				} else if (state.node instanceof StartSyncState) {
					processStartSyncState(state, s);
				} else if (state.node instanceof EndSyncState) {
					// processEndSyncState(s, flowsToResume,true);
				}

				flow.setNode(state.node);
				FlowState stateNew;
				if(flow.isOpenTransaction() 
						&& !(state.node instanceof ProcessStateImpl)
						&& (stateNew = getNextState())!=null 
						&& stateNew.node instanceof ProcessStateImpl) {
					flow.setOpenTransaction(false);//перед запуском подпроцесса необходим вынужденный комит
                    log.info("OPEN_TRANSACTION: ended before subprocess start;fowId:"+flow.getId()+";CONNECTION_ID:"+s.getConnectionId());
				}
				if (res != null && res instanceof Number
						&& ((Number) res).intValue() == 0) {
					if (state.node instanceof ActivityStateImpl) {
						flow.setEventType(EventType.BEFORE_PERFORM_OF_ACTIVITY);
						flow.setParam(flow.getParam() | Constants.ACT_ERR);
					} else if (state.node instanceof InBoxStateImpl) {
						flow.setEventType(EventType.BEFORE_CHECK_XML);
						flow.setParam(flow.getParam() | Constants.ACT_IN_BOX);
						flow.setParam(flow.getParam() | Constants.ACT_ERR);
					}
				} else {
					if (isNotBreak)
						flow.setEventType(state.eventType);
				}

				if (state.node == null
						&& state.eventType
								.equals(EventType.PROCESS_INSTANCE_END)
						&& !flow.getSyncNode().equals("")) {
					processEndSyncState(s, flowsToResume, false);
				} else if (state.node instanceof EndSyncState) {
					processEndSyncState(s, flowsToResume, true);
				}

				if (flow.getEnd() != null) {
					unlock(flow, s);
					isNotBreak = false;
				} else{
					if (!state.eventType
							.equals(EventType.BEFORE_ACTIVITYSTATE_ASSIGNMENT)
							&& !state.eventType
									.equals(EventType.AFTER_ACTIVITYSTATE_ASSIGNMENT)
							&& !state.eventType
									.equals(EventType.AFTER_PERFORM_OF_ACTIVITY)) {
						WfUtils.setFlowProperties(flow, orgComp, s, false);
						
						if (state.eventType.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)
								&& flow.isServer())
							clientTaskReload(oldUserId);
						
					}
					saveFlow(flow, s);
				}
	            //Перед завершающим шагом процесса не комитим сессию.Комитится она будет после завершения процесса
				//чтобы при ошибке можно было откатить на начало этого шага
				/*//пока не нужно
	            if(state.node!=null && !state.eventType.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY) && !(state.node instanceof StartStateImpl) 
	            		&& getNextNode(state.node, null) instanceof EndStateImpl)
	            	isSessionCommit=false;
	            */
				if(!flow.isOpenTransaction() && ((isSessionCommit && waitChildSession==null) || waitState() || !isNotBreak))// суперпроцесс комитит сессию на последнем своем шаге
					s.commitTransaction();
				oldState.node=state.node;
				oldState.eventType=state.eventType;
				//Обновить состояние потока перед длительным выполнением операции
				if(isSessionCommit && isUpdate 
						&& flow.getNode() instanceof ActivityStateImpl 
						&& (((ActivityStateImpl)flow.getNode()).isAutoNext() || ((ActivityStateImpl)flow.getNode()).isReportRequire())){
					isUpdate=false;
					clientTaskReload(oldUserId);
				}
				
			} catch (Throwable e) {
				// ловим все исключения
				if (log != null) {
					if (e instanceof WorkflowException)
						log.error(null, e);
					else
						log.error(e, e);
				} else
					_log.error(e, e);
				if (interrupting){//если этот флаг при откате выставлен в true
					Thread.interrupted();//очистка статуса для текущего потока
				}
				s.rollbackTransactionQuietly();
				if (e instanceof KrnException) {
					errorCode = ((KrnException) e).code;
				} else if (e instanceof OrException) {
					errorCode = ((OrException) e).getErrorCode();
				}

				if (errorCode == ErrorCodes.ER_LOCK_DEADLOCK ) { // если дедлок, то засыпаем на время
					try {
						if (log != null)
							log.debug("@SLEEPING AFTER DEADLOCK (FLOW: "
									+ flow.getId() + ")");
						Thread.sleep(MathOp.random(5000) + 5000);
						if (log != null)
							log.info("ER_LOCK_DEADLOCK flow :"
									+ flow.getId() + " restart");
					} catch (InterruptedException ex) {
						if (log != null)
							log.error(ex, ex);
					}
				} else {
					// выхода из цыкла
					isNotBreak = false;
				}
				if (!rollback) {
					// В любом случае загружаем набор переменных с их значениями
					// до отката
					try {

						// Все синхронизационные блоки стартуем заново
						if (!flow.getSyncNode().equals("")) {
							flowsToResume = new ArrayList<FlowImpl>();
							processEndSyncState(s, flowsToResume, false);
							for (FlowImpl flowToResume : flowsToResume) {
								if (flowToResume.getId() != flow.getId())
									exeComp.startFlow(flowToResume);
							}
						}
						// Возвращаем переменные в прежнее состояние
						byte[] buf = s.getBlob(flow.getId(),
								exeComp.SA_FLOW_VAR.id, 0, 0, 0);
						if (buf.length > 0) {
							flow.getVariable(s, exeComp.SA_FLOW_VAR.id).putAll(
									WfUtils.loadFromXml(buf, s));
						}
						if (log != null)
							log.info("tr="+(flow.getSubProcessInstance()!=null?flow.getSubProcessInstance().getTrId():"")+";id="+flow.getId()
								+";state.node"+(state.node!=null?state.node.getName():"")
								+";state.eventType="+(state.eventType!=null?state.eventType.toString():"")
								+";oldState.node="+(oldState.node!=null?oldState.node.getName():"")
								+";oldState.eventType="	+(oldState.eventType!=null?oldState.eventType.toString():""));
						flow.setEventType(oldState.eventType);
						flow.setNode(oldState.node);
					} catch (Exception e1) {
						if (log != null)
							log.error(e1.getMessage(), e1);
					}
					// Записываем тип ошибки в переменную потока
					if (errorCode != ErrorCodes.ER_LOCK_DEADLOCK && s.getObjectById(flow.getId(), 0)!=null){
						if ((flow.getParam() & Constants.ACT_ERR) != Constants.ACT_ERR)
							flow.setParam(flow.getParam() | Constants.ACT_ERR);
						try {
							s.setLong(flow.getId(), exeComp.SA_PERMIT.id, 0,flow.getParam(), 0);
					    	if(flow.getVariable()!=null && flow.getVariable().get("TIMERPROTOCOLOBJID")!=null) {
					    		long tpobjId=(Long)flow.getVariable().get("TIMERPROTOCOLOBJID"); 
					    		ServerTasks.updateProtocol(tpobjId,true,s);
					    	}
							s.commitTransaction();
						} catch (KrnException e1) {
							if (log != null)
								log.error(e1, e1);
						}
						//Если выставлена дата завершения в потоке ее обнуляем
						if(flow.getEnd()!=null)	flow.setEnd(null);
						// выхода из цыкла
						isNotBreak = false;
					}
					if (errorCode != ErrorCodes.ER_LOCK_DEADLOCK ) {
						//выполнение действий при ошибке
						processError(s,log);
						//
					}
				} else if (interrupting){//если этот флаг при откате выставлен в true
						interrupting = false;
						if (interruptFailed) {
							// Если не удалось прервать нормально
							// то не выходим из цикла, ждем processRollback
							log.info("не выходим из цикла, ждем processRollback и удаление потока");
							interruptFailed = false;
							isNotBreak = true;
						}
				}else if(errorCode != ErrorCodes.ER_LOCK_DEADLOCK){// в противном случае ставим параметр в значение для
						// выхода из цыкла
					isNotBreak = false;
				}
			}
			if (!isNotBreak)
				break;
		}
		if (flow.getEnd() != null) {
			if (!rollback) {
				//выполняем действие после завершения процесса
				ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition();
	            Collection actions=processDefinition.getActions();
	            for (Object action1 : actions) {
	                ActionImpl action = (ActionImpl) action1;
	                if (action.getEventType().equals(EventType.PROCESS_INSTANCE_AFTER_END)) {
	         			try {
	                        if (action.getExpression() != null) {
	                        	SrvOrLang orl = SrvOrLang.class.cast(s.getOrLang());
	                        	Stack<String> stack = new Stack<String>();
	                        	stack.add("Поток: " + (flow != null ? flow.getId() : "?")
	                                    + " Процесс: '" + processDefinition.getName()  + "'"
	                                    + " Событие: '" + EventType.PROCESS_INSTANCE_AFTER_END.toStringRu() + "'");
	                        	
	                        	flow.getVariable().put("FLOW", flow.getFlowObj());
	                        	flow.getVariable().put("PROCESSDEF", s.getObjectByIdQuit(processDefinition.getId(), 0));

	                            orl.evaluate(action.getExpression(), flow.getVariable(), processDefinition, false, stack, null);
	                        }
	           			}catch(Exception e) {
	         	              log.info("ОШИБКА!: При выполнении действия после завершения процесса,"
	                      	        + "Поток: " + (flow != null ? flow.getId() : "?")
	                                + " Процесс: '" + processDefinition.getName()  + "'"
	                                + " Событие: '" + EventType.PROCESS_INSTANCE_AFTER_END.toStringRu() + "'");
	          			}
	                    break;
	                 }
	            }
			}
            //удаляем поток
			while(!flowRemoved){
				try{
					log.info("удаление потока " + flow.getId());
					if(lockSession!=null && lockSession.getDriver()!=null)
						lockSession.rollbackTransaction();
					FlowImpl flowRemove=exeComp.removeFlow(flow,rollback,s,isSessionCommit,subFlowsToRollback);
					while(flowRemove!=null){
						//Удаляем суперпроцесс, который завершился ранее
						flowRemove=exeComp.removeFlow(flowRemove,rollback,s,isSessionCommit,null);
					}
					flowRemoved=true;
				}catch(Throwable e){
					// ловим все исключения
					if (log != null)
						log.error(e.getMessage(), e);
					if (e instanceof KrnException) {
						errorCode = ((KrnException) e).code;
					} else if (e instanceof OrException) {
						errorCode = ((OrException) e).getErrorCode();
					}
					if (errorCode == ErrorCodes.ER_LOCK_DEADLOCK ) { // если дедлок, то засыпаем на время
						try {
							if (log != null)
								log.debug("@SLEEPING AFTER failRemoveFlow (FLOW: "
										+ flow.getId() + ")");
							Thread.sleep(MathOp.random(5000) + 5000);
							if (log != null)
								log.info("failRemoveFlow flow :"
										+ flow.getId() + " restart");
						} catch (InterruptedException ex) {
							if (log != null)
								log.error(ex, ex);
						}
					}else{
						flowRemoved=true;
					}
				}
			}
		}

		for (FlowImpl flowToRollback : flowsToRollback) {
			exeComp.rollbackFlow(flowToRollback.getFlowObj(),s);
		}

		for (KrnObject subFlowToRollback : subFlowsToRollback) {
			exeComp.rollbackFlow(subFlowToRollback,s);
		}
		if (flowsToStart != null && flowsToStart.size() > 0) {
			for (FlowImpl flow_ : flowsToStart) {
				exeComp.startFlow(flow_);
			}
		}

		for (FlowImpl flowToResume : flowsToResume) {
			if(isSessionCommit)
				exeComp.startFlow(flowToResume,s);
			else
				exeComp.startFlow(flowToResume,s,s,waitChildFlow!=null?waitChildFlow:flow);
		}
		
		if(isSessionCommit || flowsToResume.size()==0)
			clientTaskReload(oldUserId);
	}

    private void clientTaskReload(long[] oldUserId){
        TreeSet<Long> userIds = new TreeSet<Long>();
        TreeSet<Long> userIdsInf = new TreeSet<Long>();
        long[] userId= flow.getActorId() !=null && (flow.getActorId().length>1 || (flow.getActorId().length>0 &&flow.getActorId()[0]>0))
                ?flow.getActorId():(flow.getUser()!=null?new long[]{flow.getUser().id}:new long[0]);
        for(long uId:userId){
            userIds.add(uId);
        }
        userIdsInf.addAll(flow.getInspectors());
        if(oldUserId.length>1 || (oldUserId.length>0 && oldUserId[0]>0)){
            for(long uId:oldUserId){
                userIds.add(uId);
            }
        }
        exeComp.clientTaskReload(userIds,userIdsInf,flow);
        //Если подпроцесс ожидает завершение суперпроцесса, то его обновление производится после завершения суперпроцесса
        if(waitChildFlow!=null)
        	exeComp.clientTaskReload(userIds,userIdsInf,waitChildFlow);

    }
    private boolean waitState() {

        if(flow.getNode() == null || flow.getEventType()==null) {
            return false;
        }

        boolean debugWait=((flow.getParam() & Constants.ACT_DEBUG) == Constants.ACT_DEBUG);
        boolean userWait = (flow.getNode() instanceof ActivityStateImpl
                && !(flow.getNode() instanceof StartStateImpl)
                && flow.getEventType().equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)
                && !((ActivityStateImpl)flow.getNode()).isAutoNext() //если стоит условие на узле для автоматического перехода на следующий шаг 
                && !flow.isServer());

        boolean inBoxWait = (flow.getNode() instanceof InBoxStateImpl
                && flow.getEventType().equals(EventType.BEFORE_CHECK_XML));

        boolean outBoxWait = (flow.getNode() instanceof OutBoxStateImpl
                && !(flow.getNode() instanceof InBoxStateImpl)
                && flow.getEventType().equals(EventType.PERFORM_XML));

        boolean subFlowWait = (flow.getNode() instanceof JoinImpl
                && flow.getEventType().equals(EventType.AFTER_JOIN));

        boolean subProcessWait = (flow.getNode() instanceof ProcessStateImpl
                && flow.getEventType().equals(EventType.SUB_PROCESS_INSTANCE_COMPLETION));

        boolean synchronWait = (flow.getNode() instanceof StartSyncState
                && flow.getEventType().equals(EventType.SYNC_WAIT_START));

        return debugWait || userWait || inBoxWait || outBoxWait
                || subFlowWait || subProcessWait || synchronWait;
    }

    public FlowState getNextState() throws WorkflowException {
        Node node = flow.getNode();
        EventType eventType = flow.getEventType();
        ProcessDefinitionImpl processDef=(ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition();
        do {
            if(rollback && processDef==null){
                  node = null;
                  eventType = EventType.PROCESS_INSTANCE_CANCEL;
            }else if (node == null || EventType.PROCESS_INSTANCE_START.equals(eventType)) {
                if (eventType == null) {
                    eventType = EventType.PROCESS_INSTANCE_START;
                } else if (eventType.equals(EventType.PROCESS_INSTANCE_START)) {
                    node = processDef.getStartState();
                    eventType = EventType.BEFORE_PERFORM_OF_ACTIVITY;
                }
            } else if (node instanceof StartStateImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_PERFORM_OF_ACTIVITY;
                } else if (eventType.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)) {
                    eventType = EventType.AFTER_PERFORM_OF_ACTIVITY;
                } else if (eventType.equals(EventType.AFTER_PERFORM_OF_ACTIVITY)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            } else if (node instanceof EndStateImpl) {
                if(rollback){
                    if (eventType == null) {
                        eventType = EventType.BEFORE_PROCESS_INSTANCE_CANCEL;
                    } else if (eventType.equals(EventType.BEFORE_PROCESS_INSTANCE_CANCEL)) {
                        node = null;
                        eventType = EventType.PROCESS_INSTANCE_CANCEL;
                    }
                }else{
	                node = null;
	                eventType = EventType.PROCESS_INSTANCE_END;
                }
            } else if (node instanceof ActivityStateImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_ACTIVITYSTATE_ASSIGNMENT;
                } else if (eventType.equals(EventType.BEFORE_ACTIVITYSTATE_ASSIGNMENT)) {
                    eventType = EventType.AFTER_ACTIVITYSTATE_ASSIGNMENT;
                } else if (eventType.equals(EventType.AFTER_ACTIVITYSTATE_ASSIGNMENT)) {
                    eventType = EventType.BEFORE_PERFORM_OF_ACTIVITY;
                } else if (eventType.equals(EventType.BEFORE_PERFORM_OF_ACTIVITY)) {
                    eventType = EventType.PERFORM_OF_ACTIVITY;
                } else if (eventType.equals(EventType.PERFORM_OF_ACTIVITY)) {
                    eventType = EventType.AFTER_PERFORM_OF_ACTIVITY;
                } else if (eventType.equals(EventType.AFTER_PERFORM_OF_ACTIVITY)) {
                   String transitionToId=((ActivityStateImpl)node).getTransitionTo();
                    ((ActivityStateImpl)node).setTransitionTo("");
                    node = getNextNode(node, transitionToId);
                    eventType = null;
                }
            } else if (node instanceof DecisionImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_DECISION;
                } else if (eventType.equals(EventType.BEFORE_DECISION)) {
                    eventType = EventType.AFTER_DECISION;
                } else if (eventType.equals(EventType.AFTER_DECISION)) {
                    node = getNextNode(node, flow.getTransitionTo());
                    eventType = null;
                }
            } else if (node instanceof ForkImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_FORK;
                } else if (eventType == EventType.BEFORE_FORK) {
                    eventType = EventType.FORK;
                } else if (eventType.equals(EventType.FORK)) {
                    node = findJoin((ForkImpl)node);
                    eventType = EventType.AFTER_JOIN;
                    flow.setParam(flow.getParam() | Constants.ACT_FORK_JOIN);
                } else if (eventType.equals(EventType.AFTER_FORK)) {
                    node = getNextNode(node, flow.getTransitionTo());
                    eventType = null;
                }
            } else if (node instanceof JoinImpl) {
                if (eventType == null) {
                    eventType = EventType.JOIN;
                } else if (eventType.equals(EventType.JOIN)) {
                    node = getNextNode(node, null);
                    eventType = null;
                } else if (eventType.equals(EventType.AFTER_JOIN)) {
                    node = getNextNode(node, null);
                    eventType = null;
                    flow.setParam(flow.getParam() ^ Constants.ACT_FORK_JOIN);
                }
            } else if (node instanceof ProcessStateImpl) {
                if (eventType == null) {
                    eventType = EventType.SUB_PROCESS_INSTANCE_START;
                } else if (eventType.equals(EventType.SUB_PROCESS_INSTANCE_START)) {
                    eventType = EventType.SUB_PROCESS_INSTANCE_COMPLETION;
                } else if (eventType.equals(EventType.SUB_PROCESS_INSTANCE_COMPLETION)) {
                    eventType = EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION;
                } else if (eventType.equals(EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION)) {
                    eventType = EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION_2;
                } else if (eventType.equals(EventType.SUB_PROCESS_INSTANCE_AFTER_COMPLETION_2)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            } else if (node instanceof InBoxStateImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_CHECK_XML;
                } else if (eventType.equals(EventType.BEFORE_CHECK_XML)) {
                    eventType = EventType.CHECK_XML;
                } else if (eventType.equals(EventType.CHECK_XML)) {
                    eventType = EventType.PARS_XML;
                } else if (eventType.equals(EventType.PARS_XML)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            } else if (node instanceof OutBoxStateImpl) {
                if (eventType == null) {
                    eventType = EventType.BEFORE_PERFORM_XML;
                } else if (eventType.equals(EventType.BEFORE_PERFORM_XML)) {
                    eventType = EventType.PERFORM_XML;
                } else if (eventType.equals(EventType.PERFORM_XML)) {
                    eventType = EventType.AFTER_PERFORM_XML;
                } else if (eventType.equals(EventType.AFTER_PERFORM_XML)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            } else if (node instanceof StartSyncState) {
                if (eventType == null) {
                    eventType = EventType.SYNC_BEFORE_START;
                } else if (eventType.equals(EventType.SYNC_WAIT_START)) {
                    eventType = EventType.SYNC_BEFORE_START;
                } else if (eventType.equals(EventType.SYNC_BEFORE_START)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            } else if (node instanceof EndSyncState) {
                if (eventType == null) {
                    eventType = EventType.SYNC_STOP;
                } else if (eventType.equals(EventType.SYNC_STOP)) {
                    node = getNextNode(node, null);
                    eventType = null;
                }
            }
        } while (eventType == null);
        return new FlowState(node, eventType);
    }

    private Node getNextNode(Node node, String transitionToId) throws WorkflowException {
        Collection trs = node.getLeavingTransitions();
        if (trs.size() <=1 || transitionToId == null || transitionToId.equals("")) {
            if (trs.size() > 0) {
                return ((TransitionImpl)trs.iterator().next()).getTo();
            } else {
                throw new WorkflowException(
                        "Неверное количество исходящих связей для узла \""
                        + node.getName() + "\" в процессе \""
                        + node.getProcessDefinition().getProcessDefinition().getName()
                        + "\"");
            }
        } else {
        	String[] tToId=transitionToId.split(",");
        	if(tToId.length>2)
        		transitionToId=tToId[2];
            for (Object tr1 : trs) {
                TransitionImpl tr = (TransitionImpl) tr1;
                if (transitionToId.equals(tr.getTo().getId())) {
                    return tr.getTo();
                }
            }
            throw new WorkflowException(
                    "Исходящая связь от узла (" + transitionToId + ") "
                    + node.getName() + "\" в процессе \""
                    + node.getProcessDefinition().getProcessDefinition().getName()
                    + "\" отсутсвует");
        }
    }

    private JoinImpl findJoin(ForkImpl fork) throws WorkflowException {
        int forkCount = 0;
        Node node = getNextNode(fork, null);
        while (!(node instanceof JoinImpl) || forkCount > 0) {
            if (node instanceof ForkImpl) {
                forkCount++;
            } else if (node instanceof JoinImpl) {
                forkCount--;
            }
            node = getNextNode(node, null);
        }
        return (JoinImpl)node;
    }

    public FlowImpl getFlow() {
        return flow;
    }
    public void setFlow(FlowImpl flow) {
        this.flow=flow;
    }

    public boolean start(Session lockSession,Session waitChildSession,FlowImpl waitChildFlow) {
    	this.waitChildSession=waitChildSession;
    	this.waitChildFlow=waitChildFlow;
        return start(lockSession);
    }
    public boolean start(Session lockSession) {
    	boolean release = true;
        if(!executor.isShutdown()) {
        	if(this.lockSession!=null)
            	_log.info("this.lockSession:"+this.lockSession.getUserSession().getId()
            			+";lockSession:"+lockSession!=null?lockSession.getUserSession().getId():lockSession);
        	if(lockSession!=null)
        		this.lockSession=lockSession;
    		release = false;
            future = executor.submit(this);
        }
        return release;
    }

    private void logThreads(String msg, Session s) {
    	Log log = getLog(s != null ? s.getUserSession().getLogUserName() : "sys");

    	int active = executor.getActiveCount();
        int queueS = executor.getQueue() != null ? executor.getQueue().size() : 0;
        log.info(new StringBuilder(msg).append(" ACTIVE:").append(active).append(" IDLE:").append(queueS).append(" CONNECTION_ID:").append(s.getConnectionId()).toString());
    }

    public void run() {
    	String resource = "WorkerThread (Flow:" + flow.getId() + ")";
    	// put string flowid;currtime;serverId
    	activeFlowCache.put(flow.getId(), flow.getId()+";"+System.currentTimeMillis()+";"+(UserSession.SERVER_ID==null?"":UserSession.SERVER_ID));
        synchronized(runFlow) {//для контроля за заданиями запущенными  на данном сервере добавляем их в мапу runFlow
        	runFlow.put(flow.getId(), this);
        }
        
    	_log.info("Вход>>>>>>>>>>>>>>"+flow.getId());
    	ResourceRegistry.instance().resourceAllocated(resource);
    	if(waitChildSession!=null)
    		currSession = waitChildSession;
    	else if(lockSession!=null && !rollback) {
    		currSession = lockSession;
    		lockSession=null;
    	}
        String expr="";
        Log log = null;
        try {
        	SrvOrLang.createQueryCache();
        	if(currSession==null){
        		currSession = SrvUtils.getSession(exeComp.getDsName(), flow.getUser(), flow.getIp(), flow.getComputer(), false);
        	}else if(waitChildSession!=null){
        		// суперпроцесс захватывает сессию подпроцесса
        		_log.info("waitChildSession:"+waitChildSession.getUserSession().getId());
        	}else if(lockSession!=null){
        		// суперпроцесс захватывает сессию которой блокируется запись
        		_log.info("lockSession:"+lockSession.getUserSession().getId());
        	}
        	_log.info("Session:"+currSession.getUserSession().getId()+";lockSession:"+(lockSession!=null?lockSession.getUserSession().getId():lockSession));
			expr=" Open session before lock-flowId="+flow.getId()+"; Session=" + currSession.getUserSession().getId();
			ExecutionComponent.writeExprToFile(expr);
	    	log = getLog(currSession != null ? currSession.getUserSession().getLogUserName() : "sys");
	        boolean flowLock=true;
			expr=" Open session before process-flowId="+flow.getId()+"; Session=" + currSession.getUserSession().getId();
			ExecutionComponent.writeExprToFile(expr);
			logThreads("THREAD START FLOW:"+ flow.getId(), currSession);
	        int errorCode=0;
	        if(currSession.isContextEmpty())
	        	currSession.setContext(new Context(new long[0], 0, 0));
	        try {
	            process(currSession,log);
	        } catch(Throwable e) {
	        	currSession.writeLogRecord(SystemEvent.ERROR_EXECUTION_ENGINE, e.getMessage(),exeComp.SC_FLOW.id,-1);
	        	log.error(e, e);
	        }
	        if(isClearVar) flow.getVariable().clear();
	        logThreads("THREAD STOP FLOW:" + flow.getId(), currSession);
        } catch (Throwable e) {
			if (log != null)
				log.error(e, e);
			else
				_log.error(e, e);
		} finally {
	        //релизим транзакцию в которой блокировали flow
	        if(lockSession!=null){
	        	try {
					lockSession.rollbackTransaction();
				} catch (KrnException e) {
					if (log != null)
						log.error(e, e);
					else
						_log.error(e, e);
				} finally {
					expr=" Close session-flowId="+flow.getId()+"; Session=" + lockSession.getUserSession().getId();
					ExecutionComponent.writeExprToFile(expr);
					lockSession.release();
					lockSession=null;
				}
	        }
			SrvOrLang.destroyQueryCache();
			if(currSession != null && isSessionCommit){
		        if(!currSession.isContextEmpty())
		        	currSession.restoreContext();
				currSession.release();
				currSession=null;
			}
	    	ResourceRegistry.instance().resourceReleased(resource);
	       	waitChildSession=null;
	        future = null;
	    	activeFlowCache.remove(flow.getId());
	    	synchronized(runFlow) {
	    		runFlow.remove(flow.getId());
	    	}
	    	_log.info("Выход<<<<<<<<"+flow.getId());
        }
  }

    public boolean isRunning() {
        return future != null;
    }

    public boolean interrupt(Session s) {
        if (future != null) {
        	interrupting = true;
            future.cancel(true);
            try {
            	future.get();
            	interruptFailed = false;
            } catch (CancellationException e) {
            	interruptFailed = true;
            	Log log = getLog(s.getUserSession().getLogUserName());
            	log.error(e, e);
            	return false;
            } catch (Exception e) {
            	interruptFailed = true;
            	Log log = getLog(s.getUserSession().getLogUserName());
            	log.error(e, e);
        		return false;
            }
        }
	    logThreads("Thread interrupted.", s);
        return true;
    }

    public void join() {
        if (future != null) {
            try {
                future.get();
            } catch (Exception e) {
            	Log log = getLog("sys");
                log.error(e, e);
            }
            future = null;
        }
    }

    public void rollback() {
        rollback = true;
        flow.setRollback(true);
        start(null);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ExecutionEngine) {
            ExecutionEngine e = (ExecutionEngine)obj;
            return e.flow.equals(flow);
        }
        return false;
    }

    public static void startRunner() {
        ThreadFactory thf = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                //thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        };
        String str_var = System.getProperty("isClearVar");
        isClearVar = "1".equals(str_var);
        executor = new PausableThreadPoolExecutor(
        		SystemProperties.maxThreadCount, SystemProperties.maxThreadCount, 0, TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<Runnable>(),
                thf);
        _log.info("ThreadRunner started.");
    }

    private static void resumeRunner() {
        ThreadFactory thf = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                //thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        };
        executor = new PausableThreadPoolExecutor(
        		SystemProperties.maxThreadCount, SystemProperties.maxThreadCount, 0, TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<Runnable>(idleList),
                thf);
        _log.info("ThreadRunner resumed.");
    }

    public static int getThreadCount(){
              return executor.getPoolSize() + (executor.getQueue() != null ? executor.getQueue().size() : 0);
    }
    
    public static void pause(){
        executor.pause();
        idleList=new LinkedBlockingQueue<Runnable>(executor.getQueue());
        executor.getQueue().removeAll(idleList);
        executor.resume();
        executor.shutdown();
        _log.info("ThreadRunner paused (executor.shutdown()).");
    }
    public static void resume(){
        resumeRunner();
    }
    public static boolean isMaxThreadCount(){
        return executor.getQueue()!=null && executor.getQueue().size()>0;
    }
    public static Vector<FlowImpl> getReloadSincFlows(){
        Vector<FlowImpl> res=new Vector<FlowImpl>();
        executor.pause();
        synchron.clear();
        for(Queue<FlowImpl> queue_:waitFlow.values()){
            res.add(queue_.remove());
        }
        return res;
    }
    public static void setMaxActiveCount(int maxThreadCount){
    	SystemProperties.setProperty("maxThreadCount", String.valueOf(maxThreadCount));
    	executor.setCorePoolSize(maxThreadCount);
    	executor.setMaximumPoolSize(maxThreadCount);
    }
    
    public static int getMaxThreadCount(){
    	return executor.getMaximumPoolSize();
    }
    
    public static int getActivThreadCount(){
    	return executor.getActiveCount();
    }
    
    public static int getIdleThreadCount(){
        int queueS = executor.getQueue() != null ? executor.getQueue().size() : 0;
    	return queueS;
    }
    
    public static Map<Long,Long> getActiveFlows() {
    	Map<Long,Long> res=new HashMap<Long,Long>();
    	if (activeFlowCacheMap.size() > 0) {
    		synchronized (activeFlowCacheMap) {
    			for (Long flowId : activeFlowCacheMap.keySet()) {
    	    		String se = activeFlowCacheMap.get(flowId);
    	    		if (se != null) {
    		    		String[] se_ = se.split(";");
    		    		if (se_.length > 1)
    		    			res.put(flowId, Long.valueOf(se_[1]));
    	    		}
    			}
			}
    	}
    	return res;
    }

    public static long getTimeActive(long flowId) {
    	long res = 0;
    	String[] flowInfo = getActiveFlow(flowId);
		
    	if (flowInfo.length > 1)
			res = Long.valueOf(flowInfo[1]);
		
		_log.info("Проверка>>>>>>>>>>>>>>" + flowId + ";" + res);
    	return res;
    }
    
    public static String[] getActiveFlow(long flowId) {
    	//0-идентификатор потока;1-время старта;2-идентификатор сервера
		String se = null;
		synchronized (activeFlowCacheMap) {
    		se = activeFlowCacheMap.get(flowId);
		}
		return (se != null) ? se.split(";") : new String[0];
    }
    
    public static ExecutionEngine getActivityExecutionEngine(long flowId) {
        synchronized(runFlow) {
        	return runFlow.get(flowId);
        }
    }
}
