package kz.tamur.server.wf;

import static kz.tamur.or3ee.common.TransportIds.MAIL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.cifs.or2.client.User;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.BoxListener;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.UserSrv;
import com.cifs.or2.server.exchange.Box;
import com.cifs.or2.server.exchange.transport.MessageCash;
import com.cifs.or2.server.exchange.transport.TransportException;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.plugins.SystemProperties;
import com.cifs.or2.server.timer.ServerTasks;
import com.cifs.or2.server.workflow.definition.ActivityState;
import com.cifs.or2.server.workflow.definition.EventType;
import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.StartState;
import com.cifs.or2.server.workflow.definition.impl.ActionImpl;
import com.cifs.or2.server.workflow.definition.impl.ActivityStateImpl;
import com.cifs.or2.server.workflow.definition.impl.DefinitionComponentImpl;
import com.cifs.or2.server.workflow.definition.impl.InBoxStateImpl;
import com.cifs.or2.server.workflow.definition.impl.NodeImpl;
import com.cifs.or2.server.workflow.definition.impl.OutBoxStateImpl;
import com.cifs.or2.server.workflow.definition.impl.ProcessDefinitionImpl;
import com.cifs.or2.server.workflow.definition.impl.ProcessStateImpl;
import com.cifs.or2.server.workflow.definition.impl.StartStateImpl;
import com.cifs.or2.server.workflow.definition.impl.TransitionImpl;
import com.cifs.or2.server.workflow.execution.Flow;
import com.cifs.or2.server.workflow.execution.ProcessInstance;
import com.cifs.or2.server.workflow.execution.impl.FlowImpl;
import com.cifs.or2.server.workflow.execution.impl.ProcessInstanceImpl;
import com.cifs.or2.server.workflow.organisation.OrganisationComponent;
import com.cifs.or2.util.MultiMap;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 06.12.2005
 * Time: 18:24:07
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionComponent implements BoxListener{

    private DefinitionComponentImpl defComp;
    private OrganisationComponent orgComp;
    public KrnClass SC_SERVICE;
    public KrnClass SC_FLOW;
    public KrnClass SC_OBJECT;
    public KrnClass SC_PROCESS;
    public KrnClass SC_LANGUAGE;
    public KrnAttribute SA_LANGCODE;
    public KrnAttribute SA_CONFIG;
    public KrnAttribute SA_STRINGS;
    public KrnAttribute SA_MESSAGE;
    public KrnAttribute SA_START_PROCESS;
    public KrnAttribute SA_END_PROCESS;
    public KrnAttribute SA_PROCESS_DEF;
    public KrnAttribute SA_ROOT_FLOW;
    public KrnAttribute SA_SUPER_FLOW;
    public KrnAttribute SA_INITIATOR;
    public KrnAttribute SA_KILLER;
    public KrnAttribute SA_PROCESS;
    public KrnAttribute SA_ISINBOX;
    public KrnAttribute SA_PARENT;
    public KrnAttribute SA_FLOW_VAR;
    public KrnAttribute SA_FLOW_DEBUG;
    public KrnAttribute SA_TRANS_ID;
    public KrnAttribute SA_FLOW_TRANS_ID;
    public KrnAttribute SA_FLOW_NAME;
    public KrnAttribute SA_START_FLOW;
    public KrnAttribute SA_END_FLOW;
    public KrnAttribute SA_CONTROL_FLOW;
    public KrnAttribute SA_CUR_FLOW;
    public KrnAttribute SA_PROCESS_INST;
    public KrnAttribute SA_PARENT_FLOW;
    public KrnAttribute SA_CHILDREN_FLOW;
    public KrnAttribute SA_ACTOR;
    public KrnAttribute SA_UI;
    public KrnAttribute SA_TYPE_UI;
    public KrnAttribute SA_CUT_OBJ;
    public KrnAttribute SA_COREL_ID;
    public KrnAttribute SA_NODE;
    public KrnAttribute SA_BOX;
    public KrnAttribute SA_TITLE;
    public KrnAttribute SA_SYNCNODE;
    public KrnAttribute SA_TITLE_OBJ;
    public KrnAttribute SA_TYPE_NODE;
    public KrnAttribute SA_PARENT_REACTIVATE;
    public KrnAttribute SA_PERMIT;
    public KrnAttribute SA_USER;
    public KrnAttribute SA_TRANSITION;
    public KrnAttribute SA_NODE_EVENT;
    public KrnAttribute SA_IFC_VARS;
    public KrnAttribute SA_STATUS_MSG;
    public KrnAttribute SA_OBSERVERS;
    public KrnAttribute SA_UI_OBSERVERS;
    public KrnAttribute SA_TYPE_UI_OBSERVERS;
    public KrnAttribute SA_SERVICE_TITLE;

    private final MultiMap<Long, FlowImpl> processFlowMap = new MultiMap<Long,FlowImpl>();
    public long threadSleep=7200000;//Каждые два часа
    private static final boolean DEBUG = "debug".equals(System.getProperty("Flow"));
    private static boolean isInBoxLoad = false;
    private UserSession user;
    private boolean isDateAlarmSet=false;
    private String dsName;

    private static final Log log = LogFactory.getLog(ExecutionComponent.class);

    public ExecutionComponent(Session session) {
    	dsName = session.getUserSession().getDsName();
    	Log log = getLog(session);
    	
        SC_SERVICE = session.getClassByName("ProcessDef");
        SC_PROCESS = session.getClassByName("Process");
        SC_FLOW = session.getClassByName("Flow");
        SC_OBJECT = session.getClassByName("Объект");
        SC_LANGUAGE = session.getClassByName("Language");
        SA_LANGCODE = session.getAttributeByName(SC_LANGUAGE, "code");
        SA_CONFIG = session.getAttributeByName(SC_SERVICE, "config");
        SA_STRINGS = session.getAttributeByName(SC_SERVICE, "strings");
        SA_MESSAGE = session.getAttributeByName(SC_SERVICE, "message");
        SA_SERVICE_TITLE = session.getAttributeByName(SC_SERVICE, "title");
        SA_ISINBOX = session.getAttributeByName(SC_SERVICE, "isInBox");
        SA_PARENT = session.getAttributeByName(SC_SERVICE, "parent");
        SA_START_PROCESS = session.getAttributeByName(SC_PROCESS, "start");
        SA_END_PROCESS = session.getAttributeByName(SC_PROCESS, "end");
        SA_PROCESS_DEF = session.getAttributeByName(SC_PROCESS, "processDefinition");
        SA_ROOT_FLOW = session.getAttributeByName(SC_PROCESS, "rootFlow");
        SA_SUPER_FLOW = session.getAttributeByName(SC_PROCESS, "superFlow");
        SA_INITIATOR = session.getAttributeByName(SC_PROCESS, "initiator");
        SA_KILLER = session.getAttributeByName(SC_PROCESS, "killer");
        SA_PROCESS = session.getAttributeByName(SC_PROCESS, "isProcess");
        SA_FLOW_VAR = session.getAttributeByName(SC_FLOW, "variables");
        SA_FLOW_DEBUG = session.getAttributeByName(SC_FLOW, "debug");
        SA_TRANS_ID = session.getAttributeByName(SC_PROCESS, "transId");
        SA_FLOW_TRANS_ID = session.getAttributeByName(SC_FLOW, "transId");
        SA_FLOW_NAME = session.getAttributeByName(SC_FLOW, "name");
        SA_START_FLOW = session.getAttributeByName(SC_FLOW, "start");
        SA_END_FLOW = session.getAttributeByName(SC_FLOW, "end");
        SA_CONTROL_FLOW = session.getAttributeByName(SC_FLOW, "control");
        SA_CUR_FLOW = session.getAttributeByName(SC_FLOW, "current");
        SA_PROCESS_INST = session.getAttributeByName(SC_FLOW, "processInstance");
        SA_PARENT_FLOW = session.getAttributeByName(SC_FLOW, "parentFlow");
        SA_CHILDREN_FLOW = session.getAttributeByName(SC_FLOW, "children");
        SA_ACTOR = session.getAttributeByName(SC_FLOW, "actor");
        SA_USER = session.getAttributeByName(SC_FLOW, "user");
        SA_TRANSITION = session.getAttributeByName(SC_FLOW, "transition");
        SA_NODE_EVENT = session.getAttributeByName(SC_FLOW, "event");
        SA_UI = session.getAttributeByName(SC_FLOW, "ui");
        SA_TYPE_UI = session.getAttributeByName(SC_FLOW, "typeUi");
        SA_CUT_OBJ = session.getAttributeByName(SC_FLOW, "cutObj");
        SA_COREL_ID = session.getAttributeByName(SC_FLOW, "corelId");
        SA_NODE = session.getAttributeByName(SC_FLOW, "node");
        SA_BOX = session.getAttributeByName(SC_FLOW, "box");
        SA_TITLE = session.getAttributeByName(SC_FLOW, "title");
        SA_SYNCNODE = session.getAttributeByName(SC_FLOW, "syncNode");
        SA_TITLE_OBJ = session.getAttributeByName(SC_FLOW, "titleObj");
        SA_PARENT_REACTIVATE = session.getAttributeByName(SC_FLOW, "parentReactivate");
        SA_PERMIT = session.getAttributeByName(SC_FLOW, "permit");
        SA_IFC_VARS = session.getAttributeByName(SC_FLOW, "interfaceVars");
        SA_OBSERVERS = session.getAttributeByName(SC_PROCESS, "observers");
        SA_UI_OBSERVERS = session.getAttributeByName(SC_PROCESS, "uiObservers");
        SA_TYPE_UI_OBSERVERS = session.getAttributeByName(SC_PROCESS, "typeUiObservers");
        SA_TYPE_NODE = session.getAttributeByName(SC_FLOW, "typeNode");
        SA_STATUS_MSG = session.getAttributeByName(SC_FLOW, "status");
        
        this.orgComp = session.getOrgComp();
        defComp = new DefinitionComponentImpl(dsName);
        user = session.getUserSession();
        //установка языка по умолчанию
       	List<KrnObject> langObjs=session.getSystemLangs();
       	if(langObjs.size()>0) {
	       	KrnObject langObj=langObjs.get(0);
	        user.setLang(langObj);
	        user.setDataLanguage(langObj);
       	}
        String threadSleep_ = Funcs.normalizeInput(System.getProperty("threadSleep"));
        String refreshAlarmColorOff_ = Funcs.normalizeInput(System.getProperty("refreshAlarmColorOff"));
        isDateAlarmSet ="true".equals(System.getProperty("isDateAlarmSet"));
        if(threadSleep_!=null && !threadSleep_.equals("")){
        	threadSleep=Long.valueOf(threadSleep_);
        }
        if (User.USE_OLD_USER_RIGHTS || isDateAlarmSet) {
        	// загрузка служб
        	try {
                KrnObject[] objs = session.getClassObjects(SC_SERVICE, new long[0], 0);
                for (KrnObject obj : objs) {
                    if (obj.classId != SC_SERVICE.id)
                        continue;
                    
                    if (!isProcUidAllowed(obj.uid))
                		continue;
                    
                    byte[] data = session.getBlob(obj.id, SA_CONFIG.id, 0, 0, 0);
                    if (data.length == 0)
                        continue;
                    InputStream is = new ByteArrayInputStream(data);
                    List<KrnObject> langs = session.getSystemLangs();
                    InputStream[] is_msg = new InputStream[langs.size()];
                    for (int j = 0; j < is_msg.length; ++j) {
                        byte[] strings = session.getBlob(obj.id, SA_MESSAGE.id, 0, langs.get(j).id, 0);
                        if (strings != null && strings.length > 0)
                            is_msg[j] = new ByteArrayInputStream(strings);
                        else
                            is_msg[j] = null;
                    }
                    defComp.deployProcess(new Long(obj.id), is, is_msg, session);
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        
        ExecutionEngine.startRunner();
        Thread dateThread;
        if(!"1".equals(refreshAlarmColorOff_) 
        		&& !"true".equals(refreshAlarmColorOff_)){
        	dateThread = new DateThread();
        	dateThread.start();
        }
    }
    
    private Log getLog(Session s) {
        String userName = s.getUserSession().getLogUserName();
        return getLog(userName);
    }
    
    private Log getLog(String userName) {
    	return LogFactory.getLog(dsName + "." + userName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
    }

    public ProcessDefinition deployProcess(long processDefId, Session session ){
    	Log log = getLog(session);
        try {     //загрузка служб
        	List<KrnObject> langs = session.getSystemLangs();
        	KrnObject obj = session.getObjectById(processDefId, 0);
			byte[] data = session.getBlob(obj.id, SA_CONFIG.id, 0, 0, 0);
            if (data == null || data.length == 0) return null;
            InputStream is = new ByteArrayInputStream(Funcs.normalizeInput(data, "UTF-8"));

            InputStream[] is_msg = new InputStream[langs.size()];
            int j = 0;
            for (KrnObject lang : langs) {
                byte[] strings = session.getBlob(obj.id, SA_MESSAGE.id,0, lang.id, 0);
                if (strings != null && strings.length > 0)
                    is_msg[j++] = new ByteArrayInputStream(Funcs.normalizeInput(strings, "UTF-8"));
                else
                    is_msg[j++] = null;
            }
            return defComp.deployProcess(new Long(obj.id), is, is_msg, session);
        } catch (Exception e) {
            log.error(e, e);
        }
    	return null;
    }

    public void rollbackFlow(KrnObject flowObj,Session session) {
        ExecutionEngine engine = loadFlow(flowObj, session, null);
 		if(engine != null) engine.rollback();
     }
    public void startFlow(FlowImpl flow) {
        ExecutionEngine engine = new ExecutionEngine(flow, this, defComp,orgComp);
         if(engine != null) engine.start(null);
     }
    public void closeFlowTransaction(long flowId) {
    	ExecutionEngine engine =ExecutionEngine.getActivityExecutionEngine(flowId);
    	if(engine!=null)
    		engine.getFlow().setOpenTransaction(false);
    }
    public void startFlow(FlowImpl flow,Session session) {
       ExecutionEngine engine = loadFlow(flow.getFlowObj(), session, null);
        if(engine != null) engine.start(null);
    }

    public void startFlow(FlowImpl flow,Session session,Session waitChildSession,FlowImpl waitChildFlow) {
    	ExecutionEngine engine;
    	if(flow.isOpenTransaction())
            engine = new ExecutionEngine(flow, this, defComp,orgComp);
    	else
    		engine = loadFlow(flow.getFlowObj(), session, null);
         if(engine != null) 
        	 engine.start(null,waitChildSession,waitChildFlow);
         else if(waitChildSession!=null){
        	 //Если суперпроцесса по каким то причинам нет то комитим транзакцию
        	 try{
        		 waitChildSession.commitTransaction();
             } catch (KrnException e) {
        		 waitChildSession.rollbackLocked();
     		} finally {
     			SrvOrLang.destroyQueryCache();
     			if(waitChildSession != null){
     				waitChildSession.release();
     			}
     		}
        	 
         }
     }
    public long[] getProcessDefinitions(Session s) {
    	Log log = getLog(s);

        Collection<ProcessDefinitionImpl> pds = defComp.getProcessDefinition();
        Iterator<ProcessDefinitionImpl> e = pds.iterator();
        List<Long> processIds = new ArrayList<Long>(); 
        try{
            while (e.hasNext()) {
                ProcessDefinitionImpl pd = e.next();
                if (User.USE_OLD_USER_RIGHTS) {
	                List<Long> responsibleIds = pd.getResponsibleId();
	                if (responsibleIds.size() == 0 || pd.isInbox()) {
	                    continue;
	                }
	                boolean par = false;
	                for (long rId : responsibleIds) {
	                	long userId = s.getUserSession().getUserId();
	                    par = orgComp.isUserActor(userId, rId, s);
	                    if (par)
	                        break;
	                }
	                if (par)
	                    processIds.add(pd.getId());
                } else {
                    processIds.add(pd.getId());
                }
            }
        } catch(Exception ex){
            log.error(ex, ex);
        }
        long[] res = new long[processIds.size()];
        for (int i = 0; i<res.length; i++) {
            res[i] = processIds.get(i);
        }
        processIds=null;
        return res;
    }

    public String[] startProcessInstance(long processDefinitionId, long actorId, UserSrv user, Map<String, Object> vars, String ip, String comp,boolean isTimerTask,boolean withoutTransaction,boolean start, Session outerSession) {
        String[] res;
        Session session = null;
        
        if (SystemProperties.maxFlowCount <= (ExecutionEngine.getIdleThreadCount() + ExecutionEngine.getActivThreadCount())) 
        	return new String[]{"Достигнуто максимальное количество процессов. Попытайтесь стартовать процесс позже!", ""};
        
        String userName = (outerSession != null) ? outerSession.getUserSession().getLogUserName()
        					: (user != null) ? user.getUserName() : "sys";
    	Log log = getLog(userName);

        try{
			if(actorId > 0) {
				user = orgComp.findActorById(actorId,session);
			}
			session = start ? SrvUtils.getSession(dsName, user.getUserObj(), ip, comp, false) : outerSession;
        	log.info("Session:"+session.getUserSession().getId());
        	//Проверяем наличие процессов придадлежащих данному пользователю и упавших при старте.Если такие есть то удаляем их
        	
        	//@ ERIK - Бесполезная и даже вредная функция при одновременном старте двух одинаковых процессов
        	// checkFlowToRemove(processDefinitionId, user.getUserId(),session);
        	//
			res =  startProcessInstance(processDefinitionId, actorId, vars
					, withoutTransaction, start, session);
        } catch (Exception ex) {
            log.error(ex, ex);
            session.rollbackTransactionQuietly();
            res = new String[]{"Ошибка при запуске процесса.", ""};
        } finally {
            if(start && session != null)
            	session.release();
        }
        return res;
    }

    public ExecutionEngine createProcessInstance (
            long procDefId, long[] actorId, FlowImpl parentFlow, Map<String,Object> vars,
            Session session, String procType, List<String> res,boolean withoutTransaction,FlowState nextState) throws WorkflowException{

    	Log log = getLog(session);

        ProcessDefinitionImpl processDefinition =
                (ProcessDefinitionImpl)defComp.getProcessDefinition(
                        new Long(procDefId));
        if(processDefinition==null){
        	processDefinition=(ProcessDefinitionImpl)deployProcess(procDefId, session);
        }
        List<Long> responsibleIds = new ArrayList<Long>();
        responsibleIds.addAll(processDefinition.getResponsibleId());
        if (parentFlow != null && !responsibleIds.contains(parentFlow.getUser().id)) {
            responsibleIds.add(parentFlow.getUser().id);
        } else if (vars == null || (actorId.length==1 && actorId[0] != -1)) {
        	
       }
        boolean par = false;
        if (vars == null) {
            long userId = session.getUserSession().getUserId();
            for(long resp:responsibleIds){
                par=orgComp.isUserActor(userId, resp,session);
                if(par) break;
            }
        }
        if ((vars != null && (actorId.length==1 && actorId[0] == -1)) || par /*|| responsibleId.equals(String.valueOf(actorId))*/ || parentFlow != null) {
            long trId = 0;
            if (parentFlow != null) {
                if (Constants.SUBPROCESS_WAIT.equals(procType)||"Подпроцесс".equals(procType))
                    trId = parentFlow.getProcessInstance().getTrId();
            }
            ProcessInstanceImpl processInstance = createProcessInstance(processDefinition
            		, parentFlow,null, trId,withoutTransaction, session,nextState);
            try {
            	session.commitTransaction();
            } catch (KrnException e) {
            	throw new WorkflowException(e);
            }
            log.info("Инициирован процесс:" + processDefinition.getName() +
                    "; id=" + processInstance.getId() +
                    "; trId=" + processInstance.getTrId() +
                    ";  пользователь:" + session.getUserSession().getUserName());

            session.writeLogRecord(SystemEvent.EVENT_PROCESS_START,
        			processDefinition.getName(),SC_PROCESS.id,-1);
            
            FlowImpl flow_ = (FlowImpl) processInstance.getRootFlow();
            if (vars != null) {
                Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
                vc.putAll(vars);
                vc=null;
            }
            if (parentFlow != null) {
                flow_.setUser(parentFlow.getUser());
                flow_.setIp(parentFlow.getIp());
                flow_.setComputer(parentFlow.getComputer());
                Map<String,Object> var=parentFlow.getVariable(session,SA_FLOW_VAR.id);
                if (Constants.SUBPROCESS_PASS_WAIT.equals(procType)||Constants.SUBPROCESS_WAIT.equals(procType)||"Подпроцесс".equals(procType)) {
                    processInstance.setProcess(false);
                    //Передача переменных из суперпроцесса в подпроцесс
                    if(nextState!=null && nextState.node instanceof ProcessStateImpl && Constants.NOT_RETURN_VAR.equals(((ProcessStateImpl)nextState.node).getReturnVarType())){
                        Iterator it;
                        for(it = var.keySet().iterator(); it.hasNext();){
                            String key=(String)it.next();
                            if(key.substring(0,1).equals("_")){
                                Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
                                vc.put(key,var.get(key));
                                vc=null;

                            }
                        }
                    }else{
                        Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
                        vc.putAll(var);
                        vc=null;
                    }
                }else{
                    Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
                    vc.putAll(WfUtils.clone(var));
                    vc=null;
                }
                // привязка подпроцесса к родительскому потоку
                parentFlow.setSubProcessInstance(processInstance);
                processInstance.setSuperProcessFlow(parentFlow);
            }
            ExecutionEngine engine = new ExecutionEngine(flow_, this, defComp,orgComp);
            Object res_start =null;
            if(vars==null){
                //Проверка параметров при старте процесса
                Collection actions=processDefinition.getActions();
                for (Object action1 : actions) {
                    ActionImpl action = (ActionImpl) action1;
                    if (action.getEventType().equals(EventType.PROCESS_INSTANCE_START)) {
                        if (action.getExpression() != null) {
                        	
                        	flow_.getVariable().put("FLOW", flow_.getFlowObj());
                        	flow_.getVariable().put("PROCESSDEF", session.getObjectByIdQuit(procDefId, 0));

                            res_start =WfUtils.getResolvExpression(processDefinition,
                                        action.getExpression(), flow_.getVariable(), engine.getFlow(),engine.getFlow().getNode(), EventType.PROCESS_INSTANCE_START, session);
                            break;
                        }
                    }
                }
            } //убрать оно в данном случае играет только роль когда будет фатальная ошибка
            String[] strs = engine.startProcess(session.getUserSession());
            res.add(strs[0]);
            res.add(strs[1]);
            return engine;
        } else {
            res.add("Нет прав на запуск процесса:'" + processDefinition.getName() + "'");
            res.add("");
        }
        return null;
    }

    public String[] startProcessInstance(long processDefinitionId,
                                         long actorId,
                                         Map<String,Object> var,
                                         boolean withoutTransaction,
                                         boolean start,
                                         Session session) throws WorkflowException{
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);
    	String[] res_;
     	Session lockSession=null;
     	boolean flowLock=false;
    	boolean procLock=false;
    	String expr="";
    	String infMsg = "";
    	ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl) defComp.getProcessDefinition(new Long(processDefinitionId));
        if(processDefinition==null){
        	processDefinition=(ProcessDefinitionImpl)deployProcess(processDefinitionId, session);
        }
        List<Long> responsibleIds = null;
        if (var == null || actorId > -1 || "DEFERRED".equals(var.get("DEFERRED"))) {
        	responsibleIds=processDefinition.getResponsibleId();
        }
        boolean par = false;
        if (var == null || actorId >0 || "1".equals(var.get("TIMERTASK")) || var.containsKey("OBJS") || "DEFERRED".equals(var.get("DEFERRED"))) {
                //Проверка параметров при старте процесса
                Collection actions=processDefinition.getActions();
                Iterator iter = actions.iterator();
				if(var==null)
					var=new HashMap<String,Object>();
                Object res_start=null;
                while (iter.hasNext()) {
                    ActionImpl action = (ActionImpl) iter.next();
                    if (action.getEventType().equals(EventType.PROCESS_INSTANCE_START)) {
                        if (action.getExpression() != null) {
                            var.put("PROCESSDEF", session.getObjectByIdQuit(processDefinitionId, 0));
                            res_start = WfUtils.getResolvExpression(processDefinition, action.getExpression(), var, null, null, EventType.PROCESS_INSTANCE_START, session);
                            if (var.containsKey("INFMSG")) {
                            	infMsg = (String) var.get("INFMSG");
                            }
                            break;
                        }
                    }
                }
                if (res_start != null && res_start instanceof Number && ((Number) res_start).intValue() == 0) {
                    res_start = var.get("ERRMSG");
                    if(res_start==null)
                        res_start="";
                    return new String[]{res_start.toString(), "", infMsg};
                }else if (res_start != null 
                		&& res_start instanceof Number 
                		&& ((Number) res_start).intValue() == 2 
                		&& (var==null || !"DEFERRED".equals(var.get("DEFERRED")))) {
                        res_start = var.get("ERRMSG");
                        if(res_start==null)
                            res_start="";
                        return new String[]{res_start.toString(), "deferred", infMsg};
                }

            if ("1".equals(var.get("TIMERTASK")) || var.containsKey("OBJS")) {
                par=true;
            }else{
            	par=false;
            	for(long rId:responsibleIds){
            		par=orgComp.isUserActor(actorId == -1 ? session.getUserSession().getUserId() : actorId, rId,session);
            		if(par) break;
            	}
            }
        }
        if ((var != null && actorId == -1) || par || responsibleIds.contains(actorId)) {
            long trId = 0;
            ProcessInstanceImpl processInstance = createProcessInstance(processDefinition, null,var, trId, withoutTransaction, session,null);
            if (start)
            	try {
            		session.commitTransaction();
            	} catch (KrnException e) {
            		throw new WorkflowException(e);
            	}
            log.info("Инициирован процесс:" + processDefinition.getName() +
                    "; id=" + processInstance.getId() +
                    "; trId=" + processInstance.getTrId() +
                    ";  пользователь:" + session.getUserSession().getUserName());
            
            session.writeLogRecord(SystemEvent.EVENT_PROCESS_START,
    			processDefinition.getName(),SC_PROCESS.id,-1);
            
            FlowImpl flow_ = (FlowImpl) processInstance.getRootFlow();
            if (var != null) {
                Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
                vc.putAll(var);
                vc=null;
            }
            // TODO связь по которой будет идти переход
            TransitionImpl trTo = null;
            // Получить исходящие связи
            if (processInstance.getProcessDefinition().getStartState() != null) {
                Collection<TransitionImpl> trs = processInstance.getProcessDefinition().getStartState().getLeavingTransitions();
                for (TransitionImpl tr : trs) {
                    // берём первую исходящую связь(она будет и единственной)
                    trTo = tr;
                }
            }

            // Получить свойства связи и определить, необходим ли синхронный переход
            boolean isSynch = false;
            ASTStart val = null;
            if (trTo != null) {
                val = trTo.getSynch(); 
                if (val != null) {
                    Object o = WfUtils.getResolvExpression((ProcessDefinitionImpl) processInstance.getProcessDefinition(), val,
                            null, flow_, flow_.getNode(), EventType.ACT_DATE_ALARM, session);
                    if (o != null && o instanceof Number) {
                        isSynch = ((Number) o).intValue() == 1;
                    }
                }
            }

            if (start) {
                ExecutionEngine engine = new ExecutionEngine((FlowImpl) processInstance.getRootFlow(), this, defComp, orgComp);
            	try {
					WfUtils.setProcessProperties(engine.getFlow(), session);
					engine.saveProcess(engine.getFlow(), session);
				} catch (KrnException e) {
					log.error(e, e);
				}
                res_ = engine.startProcess(session.getUserSession());
                res_ = new String[] { res_[0], res_[1], infMsg };
	     		try {
	     			lockSession = SrvUtils.getSession(session.getUserSession());
					expr="Try Lock "+flowLock+" record-flowId="+engine.getFlow().getId()+"; Session=" + lockSession.getUserSession().getId();
					writeExprToFile(expr);
					flowLock = lockSession.lockRecord(SC_FLOW, engine.getFlow().getId(), 0);
					expr=" Lock "+flowLock+" record-flowId="+engine.getFlow().getId()+"; Session=" + lockSession.getUserSession().getId();
					writeExprToFile(expr);
	     		} catch (KrnException e1) {
                    log.error(e1, e1);
	     		}finally{
		     		if(flowLock){
		                engine.start(lockSession);
		                if (isSynch) {
		                    engine.join();
		                    return new String[] { res_[0], res_[1], infMsg, "synch" };  
		                }
		     		} else 
		     			if (lockSession != null) lockSession.release();
	     		}

            } else {
                res_ = new String[] { "", "" + flow_.getId(), infMsg };
            }

        } else{
            res_ = new String[] { "Нет прав на запуск процесса:'" + processDefinition.getName() + "'", "", infMsg };
            log.info("ОШИБКА!:Нет прав на запуск процесса:'" + processDefinition.getName() + "'");
        }
        return res_;
    }

    public String[] performActivitys(Activity[] activitys, String transition, Session s, String event) {
        String userName = s.getUserSession().getLogUserName();
        Log log = getLog(userName);

        if (SystemProperties.maxFlowCount < (ExecutionEngine.getActivThreadCount() + ExecutionEngine.getIdleThreadCount())) 
        	return new String[]{"Достигнуто максимальное количество процессов. Попытайтесь стартовать процесс позже!"};
        
        ArrayList<String> res_ = new ArrayList<String>();
        for (Activity activity : activitys) {
        	if(ExecutionEngine.getActivityExecutionEngine(activity.flowId)==null) {
				try {
					ExecutionEngine engine=null;
		            KrnObject fobj = s.getObjectById(activity.flowId, 0);
					if(fobj!=null) engine=loadFlow(fobj, s, null);
	            if (engine == null) {
	                res_.add("Действие выполнено");
	            } else {
	                boolean isSynch = false,isDeferred = false;
	                synchronized (engine) {
	                    if (engine.isRunning() || !engine.getFlow().getNode().getId().equals(activity.msg)) {
	                        UserSrv usr = orgComp.findActorById(engine.getFlow().getUser().id,s);
	                        res_.add("Действие выполнено другим пользователем:'" + usr.getUserName() + "'");
	                    } else {
	                        if (engine.getFlow().getNode() instanceof ActivityStateImpl
	                                && !(engine.getFlow().getNode() instanceof StartStateImpl)) {
	                            try {
	                                String res = performActivity(engine, engine.getFlow().getNode(), transition,event, s);
	                                isSynch = "synch".equals(res);
	                                isDeferred = (res!=null && res.indexOf("deferred")==0);
	                                if (res.isEmpty()) {
	                                   // s.commitTransaction();
	                                } else {
	                                    if (isSynch) {
	                                       // s.commitTransaction();
	                                    } else if (isDeferred) {
	                                        s.rollbackTransactionQuietly();
	                                    } else {
	                                        // нужно как-то отличать ошибки выполнения кода от тех что шлют проектировщики, поэтому будем добавлять к сообщению "!"
	                                        res = "!" + res;
	                                        // откат транзакции
	                                        s.rollbackTransactionQuietly();
	                                    }
	                                    if(!(isSynch && engine.getFlow().isOpenTransaction()) || isDeferred)
	                                    	res_.add(res);
	                                }
	                            } catch (Exception e) {
	                                log.error(e, e);
	                                s.rollbackTransactionQuietly();
	                                res_.add("Ошибка при выполнении действия:'" + engine.getFlow().getNode().getName());
	                            }
	                        } else {
	                        	Session lockSession=null;
	                        	boolean flowLock=false;
	                        	boolean release=true;
	                    		try {
	                    			lockSession = SrvUtils.getSession(s.getUserSession());
	                				String expr="Try Lock "+flowLock+" record-flowId="+engine.getFlow().getId()+"; Session=" + lockSession.getUserSession().getId();
	                				writeExprToFile(expr);
	                    			flowLock = lockSession.lockRecord(SC_FLOW, engine.getFlow().getId(), 0);
	               					expr=" Lock record "+flowLock+"-flowId="+engine.getFlow().getId()+"; Session=" + lockSession.getUserSession().getId();
	               					writeExprToFile(expr);
	                    		} catch (KrnException e1) {
	                    			// TODO Auto-generated catch block
	                    			log.error(e1, e1);
	                    		}finally{
		                    		if(flowLock) {
		                    			release=engine.start(lockSession);
		                    			if(!release && engine.getFlow().isOpenTransaction())// старт без комит базовой транзакции пока этот флаг не станет =false
		                    				log.info("OPEN_TRANSACTION: started;tr_id:"+engine.getFlow().getProcessInstance().getTrId()
		                    						+";fowId:"+engine.getFlow().getId()+";CONNECTION_ID:"+lockSession.getConnectionId());
		                    		}
		                   			if (release && lockSession != null)
		                   				lockSession.release();
	                    		}
	                        }
	                    }
	                }
	                if (isSynch) {
	                    engine.join();
	                }
	            }
				} catch (KrnException e2) {
					log.error(e2, e2);
				}
        	}else {
                res_.add("Действие обрабатывается!");
        	}
        }
        return res_.toArray(new String[res_.size()]);

    }

    String performActivity(ExecutionEngine execEngine, Node node, String transitionTo, String event, Session s) throws WorkflowException {
    	Log log = getLog(s);
    	FlowImpl flow=execEngine.getFlow();
    	Session flowSession=null;
    	boolean releaseLock = true;
    	try {
	    	//блокировка записи на уровне базы, чтобы с двух серверов одновременно нельзя было перейти на слудующий шаг
	    	//создаем новую сессию, в которой будем сохранять обновления flow и комитить при выходе из обработки
	    	boolean flowLock=false;
			try {
				flowSession = SrvUtils.getSession(getDsName(), s.getUserSession().getUserObj(), s.getUserSession().getIp(), s.getUserSession().getComputer(), false);
				String expr="Try Lock "+flowLock+" record-flowId="+flow.getId()+"; Session=" + flowSession.getUserSession().getId();
				writeExprToFile(expr);
				flowLock = flowSession.lockRecord(SC_FLOW, flow.getId(), 0);
				expr=" Lock record "+flowLock+"-flowId="+flow.getId()+"; Session=" + flowSession.getUserSession().getId();
				writeExprToFile(expr);
		        //s.runSql("SELECT c_obj_id FROM "+flowTableName+" WHERE c_obj_id = "+flow.getId()+" FOR UPDATE", false);
				//s.commitTransaction();
			} catch (KrnException e1) {
				// TODO Auto-generated catch block
    			log.error(e1, e1);
			}
			if(!flowLock)
	            return "Действие заблокировано другим пользователем или выполняется на другом сервере!";
	        
	        if(flowSession.getUserSession().getUserId()==user.getUserId()){
	            flow.setUser(user.getUserObj());
	            flow.setIp(user.getIp());
	            flow.setComputer(user.getComputer());
	        }else{
	            if (flow.getActorId()==null ||flow.getActorId().length == 0) {
	                return "Не определен пользователь для выполнения действия:'" + flow.getNode().getName() + "'";
	            }
	            boolean par=false;
	            long userId=flowSession.getUserSession().getUserId();
	            for(long resp:flow.getActorId()){
	            	par=orgComp.isUserActor(userId, resp,flowSession);
	            	if(par) break;
	            }
	            if (!par) {
	                return "Нет прав для выполнения действия:'" + flow.getNode().getName() + "'";
	            } else {
	                flow.setUser(flowSession.getUserSession().getUserObj());
	                flow.setIp(flowSession.getUserSession().getIp());
	                flow.setComputer(flowSession.getUserSession().getComputer());
	            }
	        }
	        try {
	            String errs = execEngine.performActivity(node,event, flowSession);
	            flowSession.commitTransaction();
	            if(execEngine.getFlow().isOpenTransaction())
	            	flowSession.getContext().isOpenTranpaction=true;
	            // TODO связь по которой будет идти переход
	            TransitionImpl trTo=null;
	            // Получить исходящие связи
	            Collection<TransitionImpl> trs = node.getLeavingTransitions();
	            for (TransitionImpl tr : trs) {
	                if (transitionTo.isEmpty() || transitionTo.equals(tr.getTo().getId())) {
	                    trTo = tr;
	                }
	            }
	            
	            // Получить свойства связи и определить, необходим ли синхронный переход
	            boolean isSynch = false;
	            ASTStart val = null;
	            if (trTo != null) {
	                val = trTo.getSynch();
	                if (val != null) {
	                    Object o = WfUtils.getResolvExpression((ProcessDefinitionImpl) flow.getProcessInstance()
	                            .getProcessDefinition(), val, null, flow, flow.getNode(), EventType.ACT_DATE_ALARM, flowSession);
	                    if (o instanceof Number) {
	                        isSynch = ((Number) o).intValue() == 1;
	                    }
	                }
	            }
	            
	            if (errs.equals("")) {
	                ((ActivityStateImpl)execEngine.getFlow().getNode()).setTransitionTo(transitionTo);
	                releaseLock = execEngine.start(flowSession);
        			if(!releaseLock && execEngine.getFlow().isOpenTransaction())// старт без комит базовой транзакции пока этот флаг не станет =false
        				log.info("OPEN_TRANSACTION: started;tr_id:"+execEngine.getFlow().getProcessInstance().getTrId()
        						+";fowId:"+execEngine.getFlow().getId()+";CONNECTION_ID:"+flowSession.getConnectionId());
	                if(isSynch) {
	                    return "synch";
	                }
	            }else if(flowSession!=null){
	            		flowSession.commitTransaction();
	            		flowSession.release();
	            }
	            return errs;
	        } catch (KrnException e) {
	            log.error(e, e);
	            flowSession.rollbackTransactionQuietly();
	        	if(flowSession!=null){
	        		flowSession.rollbackTransactionQuietly();
	        	}
	        }
    	} finally {
    		if (releaseLock && flowSession != null) flowSession.release();
    	}
        return null;
    }

    public long performActivity(long flowId, Object args, Session s) throws WorkflowException {
        String userName = s.getUserSession().getLogUserName();
    	Log log = getLog(userName);
    	boolean releaseLock = true;
    	Session lockSession=null;
    	//31.05.2019  все действия с потоком используют lockSession
        try {
        	boolean flowLock=false;
    		try {
    			lockSession = SrvUtils.getSession(s.getUserSession());
				String expr="Try Lock "+flowLock+" record-flowId="+flowId+"; Session=" + lockSession.getUserSession().getId();
				writeExprToFile(expr);
    			flowLock = lockSession.lockRecord(SC_FLOW, flowId, 0);
				expr=" Lock "+flowLock+" record-flowId="+flowId+"; Session=" + lockSession.getUserSession().getId();
				writeExprToFile(expr);
    		} catch (KrnException e1) {
    			log.error(e1, e1);
    		}finally{
        		if(flowLock){
        			ExecutionEngine execEngine=null;
        			KrnObject fobj = lockSession.getObjectById(flowId, 0);
        			if(fobj==null) return 0;
        			execEngine=loadFlow(fobj, lockSession, null);
                	FlowImpl flow = execEngine.getFlow();
                	flow.setUser(user.getUserObj());
                	flow.setIp(user.getIp());
                	flow.setComputer(user.getComputer());
    	        	flow.getVariable().put("ARGS", args);
		            String errs = execEngine.performActivity(flow.getNode(),null, lockSession);
		        	flow.getVariable().remove("ARGS");
		        	lockSession.commitTransaction();
		            if (errs.equals("")) {
		            	releaseLock = execEngine.start(lockSession);
		            	return 1;
		            }else  if (errs.equals("deferred")) {
		            	return 2;
		            }
        		}
    		}
        } catch (KrnException e) {
            log.error(e, e);
            lockSession.rollbackTransactionQuietly();
        } finally {
        	if (releaseLock && lockSession != null) lockSession.release();
        }
        return 0;
    }
    private void deployInboxProcessDef(Session session) {
        //загрузка описаний процессов с isInBox=true
    	try {
            KrnObject[] objs = session.getObjectsByAttribute(SC_SERVICE.id, SA_ISINBOX.id, 0, ComparisonOperations.CO_EQUALS, true, 0);
            long[] objIds=Funcs.makeObjectIdArray(objs);
            ObjectValue[] ovs=session.getObjectValues(objIds, SA_PARENT.id, new long[0], 0);
            for (ObjectValue ov : ovs) {
                if (ov.value == null) continue;
                deployProcess(ov.objectId, session);
            }
            isInBoxLoad=true;
            log.info("Службы для прослушивания пунктов обмена успешно загружены!");
         } catch (Exception e) {
            log.error("Ошибка при загрузке служб для прослушивания пунктов обмена!");
            log.error(e, e);
        }
    }
    private Collection getProcesses(Box box) throws WorkflowException {
    	Log log = getLog(user.getLogUserName());
        Collection <NodeImpl> procs = new Vector<NodeImpl>();
        Session session = null;
            try {
                session = SrvUtils.getSession(user);
                if(!isInBoxLoad)
                	deployInboxProcessDef(session);
                for (Object o1 : defComp.getInBox()) {
                    ProcessDefinitionImpl process = (ProcessDefinitionImpl) o1;
                    StartState start = process.getStartState();
                    if (start == null) continue;
                    Collection leavingTransitions = start.getLeavingTransitions();
                    if (leavingTransitions.size() == 1) {
                        TransitionImpl startTransition = (TransitionImpl) leavingTransitions.iterator().next();
                        NodeImpl node = (NodeImpl) startTransition.getTo();
                        if (node instanceof InBoxStateImpl) {
                            // определение блока обмена
                            long[] boxId = new long[]{-1};
                            KrnObject box_krn=null;
                            ASTStart boxExpression = ((InBoxStateImpl) node).getBoxExpression();
                            String boxExpressionKrn = ((InBoxStateImpl) node).getBoxExpressionKrn();
                            if (boxExpressionKrn != null && !boxExpressionKrn.equals("")){
                                    try {
                                        box_krn=session.getObjectByUid(boxExpressionKrn,0);
                                    } catch (KrnException e) {
                                        log.error(e, e);
                                    }
                                if(box_krn!=null) boxId=new long[]{box_krn.id};
                            }else if (boxExpression != null){
                            Object box_ = WfUtils.getResolvExpression((ProcessDefinitionImpl) process.getProcessDefinition(),
                                    boxExpression, null, null, null, EventType.BOX_EXCHANGE, session);
                            if (box_ instanceof String)
                                boxId[0] = Long.valueOf((String) box_);
                            else if (box_ instanceof Number)
                                boxId[0] = ((Number) box_).longValue();
                            else if (box_ instanceof KrnObject)
                                boxId[0] = ((KrnObject) box_).id;
                            else if (box_ instanceof List) {
                                boxId = new long[((List) box_).size()];
                                for (int i = 0; i < ((List) box_).size(); ++i) {
                                    Object o = ((List) box_).get(i);
                                    if (o != null)
                                        boxId[i] = ((KrnObject) o).id;
                                }
                            }
                            }else continue;
                            for (long aBoxId : boxId) {
                                if (aBoxId <= 0 || aBoxId != box.getKrnObject().id) continue;
                                procs.add(node);
                                break;
                            }
                        }
                    }
                }
            } catch (KrnException e) {
                log.error(e, e);
            } finally {
                if (session != null) {
                    session.release();
                }
            }
        return procs;
    }

    public ExecutionEngine startForkFlow(String t_name, FlowImpl parent, Session session) throws WorkflowException {
        FlowImpl flow = createFlow(t_name, parent, session);
        ExecutionEngine engine = new ExecutionEngine(flow, this, defComp,orgComp);
        return engine;

    }
    
    public boolean isRunning(long flowId, Session session) {
		ExecutionEngine engine=ExecutionEngine.getActivityExecutionEngine(flowId);//Проверяем запущен ли процесс на текущем сервере
		if (engine == null) {
			String[] activeFlow=ExecutionEngine.getActiveFlow(flowId);
			if (activeFlow.length>0)
				return true;
			return false;
		} else
			return engine.isRunning();
    }
    
    public boolean cancelProcessInstance(long flowId, UserSession user, String ip, String comp, boolean forceCancel) {
        return cancelProcessInstance(flowId, "-1", user, ip, comp, true, forceCancel, false);
    }
    
    public boolean cancelProcessInstance(long flowId, String nodeId, UserSession user, String ip, String comp, boolean isAll, boolean forceCancel, boolean isFromEhCache) {
    	Log log = getLog(user.getLogUserName());
        Session session=null;
        boolean release=true;
        try {
            session = SrvUtils.getSession(user);
	        // выбор процесса и проверка разрешено ли пользователю отменить процесс
			KrnObject fobj = session.getObjectById(flowId, 0);
	        if (fobj == null) {
	            log.info("ОШИБКА!: Процесс уже завершен!!!");
	            TreeSet<Long> users=new TreeSet<Long>();
	            users.add(user.getUserId());
	            com.cifs.or2.server.Session.clientTaskReload(users,new TreeSet<Long>(),flowId);
	            return false;
	        }
	        
			ExecutionEngine engine=ExecutionEngine.getActivityExecutionEngine(flowId);//Проверяем запущен ли процесс на текущем сервере
			if(engine==null) {
				String[] activeFlow=ExecutionEngine.getActiveFlow(flowId);
				if(activeFlow.length>0) {
					if(!isFromEhCache) {
			            log.info("Рассылка по серверам для удаления процесса!!!flowId:"+flowId+";engine:"+engine+";serverId:"+(activeFlow.length>2?activeFlow[2]:""));
						session.processCanceled(flowId, activeFlow.length>2?activeFlow[2]:"", nodeId, isAll, forceCancel);//передаем serverId, если он задан, на котором работает в данный момент процесс чтобы адресно его удалить
					}
					// процесс выполняется на другом сервере, что вернуть?
					// пока вернем true если форсировать остановку и false - если не форсировать
					return forceCancel;
				} else {
					engine=loadFlow(fobj, session, null);
				}
			}
			if("-1".equals(nodeId))		nodeId=engine.getFlow().getNode().getId();
	        if (nodeId != null && !nodeId.equals("") 
	        		&& engine.getFlow().getNode()!=null 
	        		&& !engine.getFlow().getNode().getId().equals("" + nodeId)) {
	            log.info("ОШИБКА!: Действие уже выполнено!!!");
	            TreeSet<Long> users=new TreeSet<Long>();
	            users.add(user.getUserId());
	            com.cifs.or2.server.Session.clientTaskReload(users,new TreeSet<Long>(),flowId);
	            return false;
	        }
            //Map vc=engine.getFlow().getVariable(session,SA_FLOW_VAR.id);
            //vc=null;
	        if((!isAll && (engine.getFlow().getParam() & Constants.ACT_AUTO) == Constants.ACT_AUTO)){
                Node node;
                Vector<String> nodes = (Vector<String>)engine.getFlow().getNodes();
                for(int i=nodes.size()-2;i>0;--i){
                    node = engine.getFlow().getProcessInstance().getProcessDefinition().getNode(nodes.get(i));
                    if(node instanceof ActivityStateImpl && !(node instanceof StartStateImpl)){
                        KrnObject actor_krn=null;
                        String actorId = null;
                        ASTStart assignmentExpression;
                        String assignmentKrn;
                        // получение роли назначенной данному activity-state
                        assignmentExpression = ((ActivityState)node).getAssignment();
                        assignmentKrn = ((ActivityState)node).getAssignmentKrn();
                        if(assignmentKrn!=null && !assignmentKrn.equals("")){
                            actor_krn=session.getObjectByUid(assignmentKrn,0);
                        }
                        if(actor_krn!=null){
                            actorId=""+actor_krn.id;
                        }else if (assignmentExpression != null) {
                            // get the assignment of the activity-state
                                Object actor = WfUtils.getResolvExpression((ProcessDefinitionImpl)engine.getFlow().getProcessInstance().getProcessDefinition(),
                                        assignmentExpression, null, engine.getFlow(),engine.getFlow().getNode(), EventType.RESPONSIBLE, session);
                                if (actor instanceof String)
                                    actorId = (String) actor;
                                else if (actor instanceof Number)
                                    actorId = "" + ((Number) actor).longValue();
                                else if (actor instanceof KrnObject)
                                    actorId = "" + ((KrnObject) actor).id;
                        }
                        if (actorId!=null && !actorId.equals("SERVER")) {
                            engine.getFlow().setNode(node);
                            TreeSet<Long> userIds = new TreeSet<Long>();
                            userIds.add(new Long(actorId));
                            clientTaskReload(userIds,new TreeSet<Long>(),engine.getFlow());
                            return true;
                        }
                    }
                }
            }
            
            release=cancelProcess(engine, session, forceCancel);
       } catch (Exception e) {
            log.error(e, e);
            if (session != null)
            	session.rollbackTransactionQuietly();
        } finally {
            if (release && session!=null)
            	session.release();
        }
        return release;
    }

    boolean cancelProcess(ExecutionEngine engine, Session session, boolean forceCancel) throws WorkflowException {
        boolean release=true;
    	Log log = getLog(session);
        FlowImpl flow = engine.getFlow();
        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) flow.getProcessInstance();
        release = engine.cancelProcess(session, forceCancel);
        
        String name =processInstance.getProcessDefinition() != null ? processInstance.getProcessDefinition().getName() : "БЕЗ ОПРЕДЕЛЕНИЯ ПРОЦЕССА";
        if (release) {
	        log.info("Остановлен процесс:" + name + "; id=" + processInstance.getId() +
		                    "; пользователь:" + session.getUserSession().getUserName());
	
	        session.writeLogRecord(SystemEvent.EVENT_PROCESS_CANCEL, name,SC_PROCESS.id,-1);
        } else {
	        log.info("Процесс не остановлен, так как находится в обработке:" + name + "; id=" + processInstance.getId() +
                    "; пользователь:" + session.getUserSession().getUserName());
        }
        return release;

    }

    public void reloadProcessDefinition(long processDefinition, UserSession user,boolean isRemote) {
        String userName = user.getLogUserName();
    	Log log = getLog(userName);
        Session session=null;
        try {     //загрузка служб
            session = SrvUtils.getSession(user);
            
            KrnObject obj = session.getObjectById(processDefinition, 0);
            if (!isProcUidAllowed(obj.uid))
        		return;
            
            AttrRequestBuilder arb = new AttrRequestBuilder(SC_SERVICE, session).add(SA_CONFIG.name);
            
        	List<KrnObject> langs = session.getSystemLangs();
        	for (KrnObject lang : langs) {
            	arb.add(SA_MESSAGE.name, lang.id);
        	}
            
        	QueryResult qr = session.getObjects(new long[] {processDefinition}, arb.build(), 0);
    		for (Object[] row : qr.rows) {
    			obj = arb.getObject(row);
    			byte[] data = (byte[]) arb.getValue(SA_CONFIG.name, row);
                if (data == null || data.length == 0) continue;
                InputStream is = new ByteArrayInputStream(Funcs.normalizeInput(data, "UTF-8"));

                InputStream[] is_msg = new InputStream[langs.size()];
                int j = 0;
                for (KrnObject lang : langs) {
                    byte[] strings = (byte[]) arb.getValue(SA_MESSAGE.name + lang.id, row);
                    if (strings != null && strings.length > 0)
                        is_msg[j++] = new ByteArrayInputStream(Funcs.normalizeInput(strings, "UTF-8"));
                    else
                        is_msg[j++] = null;
                }
                defComp.deployProcess(new Long(obj.id), is, is_msg, session);
    		}
            session.commitTransaction();
/*       		if(!isRemote) {
                ProcessDefForEhCache procDef = new ProcessDefForEhCache();
                String procDefId=""+processDefinition;
                long procDefTime=System.currentTimeMillis();
                procDef.setProcessDefinition(procDefId);
                procDef.setTimeOfModification(procDefTime);
       			processDefCache.put(new net.sf.ehcache.Element(""+processDefinition,procDef));
       		}
*/       		
        } catch (Exception e) {
            log.error(e, e);
            assert session != null;
            session.rollbackTransactionQuietly();
        } finally {
            if(session!=null)
            session.release();
        }
    }

    public ProcessInstanceImpl createProcessInstance(ProcessDefinition processDefinition,
                                                     FlowImpl superFlow,Map<String,Object> var,
                                                     long trId, boolean withoutTransaction,Session session
                                                     ,FlowState nextState) throws WorkflowException {
    	UserSession us = session.getUserSession();
    	if(us.getUserId()<0) {//если для пользователя sys не установлен объект, то необходимо его установить
    		UserSrv su=orgComp.getSuperUser(session);
    		us.setUserObj(su.getUserObj());
    	}
    	Log log = getLog(session);
        try{
        	if(var!=null && "1".equals(var.get("WITHOUTTRID"))) withoutTransaction=true;
            long tr_id = trId > 0 || withoutTransaction ? trId : session.createLongTransaction();
            KrnObject flow_obj = session.createObject(SC_FLOW, 0);
            KrnObject process_obj = session.createObject(SC_PROCESS, 0);
            ProcessInstanceImpl processInstance = new ProcessInstanceImpl(process_obj.id, session.getUserSession().getUserId(), processDefinition, tr_id);
            String chopperId = null;
            KrnObject chopper_krn=null;
            ASTStart chopper= processDefinition.getChopperId();
            String chopperKrn= ((ProcessDefinitionImpl)processDefinition).getChopperIdKrn();
            if (chopperKrn != null) {
                try {
                      chopper_krn=session.getObjectByUid(chopperKrn,0);
                     } catch (KrnException e) {
                         log.error(e, e);
                     }
                if(chopper_krn!=null)
                    chopperId=""+chopper_krn.id;
            }else if (chopper != null) {
                Object chopper_ = WfUtils.getResolvExpression((ProcessDefinitionImpl)processDefinition, chopper, var, null, null,EventType.CHOPPER, session);
                if (chopper_ instanceof String)
                    chopperId = (String) chopper_;
                else if (chopper_ instanceof Number)
                    chopperId = "" + ((Number) chopper_).longValue();
                else if (chopper_ instanceof KrnObject)
                    chopperId = "" + ((KrnObject) chopper_).id;
            }
            if (chopperId != null) processInstance.setChopper(Long.valueOf(chopperId));
            FlowImpl flow_ = new FlowImpl(flow_obj, new long[]{us.getUserId()}, processInstance);
            Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
            vc.put("SERVER", "SERVER");
            vc=null;
            processInstance.setRootFlow(flow_);
            //процесс
            session.setObject(process_obj.id, SA_INITIATOR.id, 0, us.getUserId(), 0, false);
            if(processInstance.getChopper()!=0){
                session.setObject(process_obj.id, SA_KILLER.id, 0, processInstance.getChopper(), 0, false);
            }
            session.setTime(process_obj.id, SA_START_PROCESS.id, 0, Funcs.convertTime(processInstance.getStart()), 0);
            session.setLong(process_obj.id, SA_PROCESS_DEF.id, 0, processDefinition.getId(), 0);
            session.setObject(process_obj.id, SA_ROOT_FLOW.id, 0, flow_obj.id, 0, false);
            //Передача переменных из суперпроцесса в подпроцесс
            if (superFlow != null) {
                if (superFlow.getVariable(session,SA_FLOW_VAR.id) != null) {
                    Map<String,Object> var_=superFlow.getVariable(session,SA_FLOW_VAR.id);
                    //Передача переменных из суперпроцесса в подпроцесс
                    if(nextState!=null && nextState.node instanceof ProcessStateImpl && Constants.NOT_RETURN_VAR.equals(((ProcessStateImpl)nextState.node).getReturnVarType())){
                        for(Iterator it=var_.keySet().iterator();it.hasNext();){
                            String key=(String)it.next();
                            if(key.substring(0,1).equals("_")){
                                Map vc1=flow_.getVariable(session,SA_FLOW_VAR.id);
                                vc1.put(key,var_.get(key));
                                vc1=null;

                            }
                        }
                    }else{
                        Map vc1=flow_.getVariable(session,SA_FLOW_VAR.id);
                        vc1.putAll(var_);
                        vc1=null;
                    }
                    byte[] buf = WfUtils.saveToXml(flow_.getVariable(session,SA_FLOW_VAR.id), null, null);
                    session.setBlob(flow_.getId(), SA_FLOW_VAR.id, 0, buf, 0, 0);
                }
                	session.setObject(process_obj.id, SA_SUPER_FLOW.id, 0, superFlow.getId(), 0, false);
            }
            //Inspectors
            session.setLong(process_obj.id, SA_TRANS_ID.id, 0, tr_id, 0);
            //поток
            session.setLong(flow_obj.id, SA_FLOW_TRANS_ID.id, 0, tr_id,0);
            session.setString(flow_obj.id, SA_FLOW_NAME.id, 0, 0, false, "root_" + processDefinition.getName() + "_" + flow_.getId(), 0);
            session.setObject(flow_obj.id, SA_PROCESS_INST.id, 0, process_obj.id, 0, false);
            if (flow_.getStart() != null)
                session.setTime(flow_obj.id, SA_START_FLOW.id, 0, Funcs.convertTime(flow_.getStart()), 0);
            else
                session.deleteValue(flow_obj.id, SA_START_FLOW.id, new int[]{0}, 0, 0);
            if (flow_.getCurrent() != null)
                session.setTime(flow_obj.id, SA_CUR_FLOW.id, 0, Funcs.convertTime(flow_.getCurrent()), 0);
            else
                session.deleteValue(flow_obj.id, SA_CUR_FLOW.id, new int[]{0}, 0, 0);
            session.setObject(flow_obj.id, SA_ACTOR.id, 0, us.getUserId(), 0, false);
            session.setString(flow_obj.id, SA_NODE.id, 0, 0, false, processDefinition.getStartState().getId(), 0);
            return processInstance;
        }catch(KrnException e){
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e.code);
        }catch(IOException e){
            log.error(e, e);
            throw new WorkflowException(e.getMessage());
        }
    }
    public boolean saveFlowParam(long flowId, List<String> args,Session session) {
		ExecutionEngine engine=null;
        String userName = user.getLogUserName();
    	Log log = getLog(userName);
		try {
            KrnObject fobj = session.getObjectById(flowId, 0);
			if(fobj!=null) engine=loadFlow(fobj, session, null);
	    	if(engine !=null && !engine.isRunning()){
	    		FlowImpl flow=engine.getFlow();
	            engine =loadFlow(engine.getFlow().getFlowObj(),session,null);
		        for(String arg:args){
		        	if("UI".equals(arg)){
			            if(flow.getNode() instanceof ActivityStateImpl
			            		&& EventType.BEFORE_PERFORM_OF_ACTIVITY.equals(flow.getEventType())){
			            	if(flow.getUi()==null){ 
			                    try {
			                    	ASTStart uiExpression = ((ActivityStateImpl)flow.getNode()).getUiExpression();
			    	                String uiExpressionKrn = ((ActivityStateImpl)flow.getNode()).getUiExpressionKrn();
			    	                if(uiExpressionKrn != null && !uiExpressionKrn.equals("")){
			    	                    flow.setUi(session.getObjectByUid(uiExpressionKrn, 0));
			    	                    flow.setUiType(((ActivityStateImpl)flow.getNode()).getUiType()!=null?((ActivityStateImpl)flow.getNode()).getUiType():"");
			    	                }else if (uiExpression != null) {
			    		                Object ui = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
			    		                        uiExpression, null, flow,  flow.getNode(),EventType.ACT_USER_INTERFACE, session);
			    		                if (ui != null && ui instanceof KrnObject) {
			    		                    flow.setUi((KrnObject) ui);
			    		                    flow.setUiType(((ActivityStateImpl)flow.getNode()).getUiType()!=null?((ActivityStateImpl)flow.getNode()).getUiType():"");
			    		                }
			    		            }
			        			} catch (Exception e) {
			                        log.error(e, e);
			        			}
		                    }else{
		                    	flow.setUi(flow.getUi());
		                    	flow.setUiType(flow.getUiType());
		                    }
			            	
			            }
		            }
	            }
            	try {
					engine.saveFlow(flow, session);
					session.commitTransaction();
					return true;
				} catch (WorkflowException e) {
					log.error(e, e);
				}
	    	}
		} catch (KrnException e1) {
			log.error(e1, e1);
		}
		return false;
    }
    public boolean reLoadFlow(long flowId, Session session) {
		ExecutionEngine engine=null;
    	boolean flowLock=false;
    	Session lockSession=null;
    	boolean releaseLock = true;
        String userName = user.getLogUserName();
    	Log log = getLog(userName);
		try {
            KrnObject fobj = session.getObjectById(flowId, 0);
			if(fobj!=null) engine=loadFlow(fobj, session, null);
	    	if(engine !=null && !engine.isRunning()){
	    		FlowImpl flow=engine.getFlow();
	    		KrnObject user =flow.getUser();
    			//Если у потока есть роль под которой он запускался то перестартовываем под этим пользователем 
	    		if(flow.getActorId()!=null && flow.getActorId().length>0 && flow.getActorId()[0]>0){
	    			user = session.getObjectById(flow.getActorId()[0], 0);
	    		}else if(flow.getActorFromId()>0){
	    			user = session.getObjectById(flow.getActorFromId(), 0);
	    		}
				lockSession = SrvUtils.getSession(getDsName(), user, flow.getIp(), flow.getComputer(), false);
	            engine =loadFlow(engine.getFlow().getFlowObj(),lockSession,null);
	            String expr="Try Lock "+flowLock+" record-flowId="+flowId+"; Session=" + lockSession.getUserSession().getId();
	            writeExprToFile(expr);
	            flowLock = lockSession.lockRecord(SC_FLOW, flowId, 0);
	            expr=" Lock "+flowLock+" record-flowId="+flowId+"; Session=" + lockSession.getUserSession().getId();
	            writeExprToFile(expr);
	            Node node=flow.getNode();
	            //Если до этого действие завершилось ошибкой, то убираем ее, чтобы она не влияла на дальнейшие действия.
	            if((flow.getParam() & Constants.ACT_ERR) == Constants.ACT_ERR)
	            	flow.setParam(flow.getParam() ^ Constants.ACT_ERR);
	            if(flow.getNode() instanceof ActivityStateImpl
	            		&& EventType.BEFORE_PERFORM_OF_ACTIVITY.equals(flow.getEventType())){
	            	if(flow.getUi()==null){ 
	                    try {
	                    	ASTStart uiExpression = ((ActivityStateImpl)flow.getNode()).getUiExpression();
	    	                String uiExpressionKrn = ((ActivityStateImpl)flow.getNode()).getUiExpressionKrn();
	    	                if(uiExpressionKrn != null && !uiExpressionKrn.equals("")){
	    	                    flow.setUi(session.getObjectByUid(uiExpressionKrn, 0));
	    	                    flow.setUiType(((ActivityStateImpl)flow.getNode()).getUiType()!=null?((ActivityStateImpl)flow.getNode()).getUiType():"");
	    	                }else if (uiExpression != null) {
	    		                Object ui = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
	    		                        uiExpression, null, flow,  flow.getNode(),EventType.ACT_USER_INTERFACE, session);
	    		                if (ui != null && ui instanceof KrnObject) {
	    		                    flow.setUi((KrnObject) ui);
	    		                    flow.setUiType(((ActivityStateImpl)flow.getNode()).getUiType()!=null?((ActivityStateImpl)flow.getNode()).getUiType():"");
	    		                }
	    		            }
	        			} catch (Exception e) {
	                        log.error(e, e);
	        			}
                    }else{
                    	flow.setUi(flow.getUi());
                    	flow.setUiType(flow.getUiType());
                    }
	            	try {
						engine.saveFlow(flow, session);
						session.commitTransaction();
					} catch (WorkflowException e) {
						log.error(e, e);
					}
	            }
	    	}
		} catch (KrnException e1) {
			log.error(e1, e1);
		} finally {
			if(flowLock){
				releaseLock=engine.start(lockSession);
				if (releaseLock && lockSession != null)
					lockSession.release();
	            return true;
			}else if(lockSession != null){
				lockSession.release();
			}
		}
		return false;
    }

    private ExecutionEngine loadFlow(KrnObject flowObj, Session session, HashMap<Long,ExecutionEngine> activeFlowIds) {
    	Log log = getLog(session);
    	if(activeFlowIds==null){
    		activeFlowIds = new HashMap<Long,ExecutionEngine>();
    	}
    	ExecutionEngine engine = activeFlowIds.get(flowObj.id);
        try {
            if (engine == null) {
                AttrRequestBuilder arbFlow = new AttrRequestBuilder(session.getClassById(flowObj.classId), session);
                arbFlow.add(SA_PROCESS_INST.name).add(SA_PARENT_FLOW.name).add(SA_ACTOR.name).add(SA_USER.name)
                		.add(SA_FLOW_VAR.name).add(SA_FLOW_NAME.name).add(SA_START_FLOW.name).add(SA_CUR_FLOW.name)
                		.add(SA_END_FLOW.name).add(SA_NODE.name).add(SA_NODE_EVENT.name).add(SA_STATUS_MSG.name)
                		.add(SA_SYNCNODE.name).add(SA_TRANSITION.name).add(SA_UI.name).add(SA_CUT_OBJ.name)
                		.add(SA_CHILDREN_FLOW.name).add(SA_TYPE_UI.name).add(SA_COREL_ID.name);
                
            	List<KrnObject> langs = session.getSystemLangs();
            	for (KrnObject lang : langs) {
                	arbFlow.add(SA_TITLE.name, lang.id).add(SA_TITLE_OBJ.name, lang.id);
            	}
            	arbFlow.add(SA_PARENT_REACTIVATE.name).add(SA_PERMIT.name);
            	
                QueryResult qr = session.getObjects(new long[] {flowObj.id}, arbFlow.build(), 0);
        		for (Object[] row : qr.rows) {
        			KrnObject process_obj = arbFlow.getObjectValue(SA_PROCESS_INST.name, row);
        			KrnObject parent_ = arbFlow.getObjectValue(SA_PARENT_FLOW.name, row);
        			KrnObject actor_ = arbFlow.getObjectValue(SA_ACTOR.name, row);
        			KrnObject user_ = arbFlow.getObjectValue(SA_USER.name, row);
        			byte[] buf = (byte[])arbFlow.getValue(SA_FLOW_VAR.name, row);
        			String name_ = arbFlow.getStringValue(SA_FLOW_NAME.name, row);
                    Time start_f = (Time) arbFlow.getValue(SA_START_FLOW.name, row);
                    Time cur_f = (Time) arbFlow.getValue(SA_CUR_FLOW.name, row);
                    Time end_f = (Time) arbFlow.getValue(SA_END_FLOW.name, row);
        			String node_ = arbFlow.getStringValue(SA_NODE.name, row);
        			String event_ = arbFlow.getStringValue(SA_NODE_EVENT.name, row);
                    String statusMsg_ = arbFlow.getStringValue(SA_STATUS_MSG.name, row);
                    String sincNode_ = arbFlow.getStringValue(SA_SYNCNODE.name, row);
                    String transition_ = arbFlow.getStringValue(SA_TRANSITION.name, row);
                    Object ui_ = arbFlow.getValue(SA_UI.name, row);;
                    String typeUi_ = arbFlow.getStringValue(SA_TYPE_UI.name, row);
                    String cut_obj = arbFlow.getStringValue(SA_CUT_OBJ.name, row);
                	KrnObject box_ = arbFlow.getObjectValue(SA_BOX.name, row);
                    String corelId_ = arbFlow.getStringValue(SA_COREL_ID.name, row);
                	Map<Long, String> titles_ = new HashMap<Long, String>();
                	Map<Long, String> titleObjs_ = new HashMap<Long, String>();
                	for (KrnObject lang : langs) {
            			String title = arbFlow.getStringValue(SA_TITLE.name, lang.id, row);
            			String titleObj = arbFlow.getStringValue(SA_TITLE_OBJ.name, lang.id, row);
            			titles_.put(lang.id, title);
            			titleObjs_.put(lang.id, titleObj);
                	}
                    boolean react_ = arbFlow.getBooleanValue(SA_PARENT_REACTIVATE.name, row, false);
                    long param_ = arbFlow.getLongValue(SA_PERMIT.name, row, 0);
                    List<Value> children_ = (List<Value>) arbFlow.getValue(SA_CHILDREN_FLOW.name, row);

        			if (process_obj != null) {
                        AttrRequestBuilder arbInst = new AttrRequestBuilder(session.getClassById(process_obj.classId), session);
                        arbInst.add(SA_PROCESS_DEF.name).add(SA_TRANS_ID.name).add(SA_INITIATOR.name).add(SA_KILLER.name).add(SA_PROCESS.name)
                        		.add(SA_ROOT_FLOW.name).add(SA_START_PROCESS.name).add(SA_END_PROCESS.name).add(SA_SUPER_FLOW.name)
                        		.add(SA_OBSERVERS.name).add(SA_UI_OBSERVERS.name).add(SA_TYPE_UI_OBSERVERS.name);
                        
                    	QueryResult qr1 = session.getObjects(new long[] {process_obj.id}, arbInst.build(), 0);
                		for (Object[] row1 : qr1.rows) {
                            KrnObject obj = arbInst.getObject(row1);

                            KrnObject processDef_ = arbInst.getObjectValue(SA_PROCESS_DEF.name, row1);
                            ProcessDefinition processDef = null;
                            if (processDef_ != null) {
                            	if (!isProcUidAllowed(processDef_.uid)) return null;
                            	
                            	processDef = defComp.getProcessDefinition(new Long(processDef_.id));
                 		        if(processDef==null){
                		        	processDef=(ProcessDefinitionImpl)deployProcess(processDef_.id, session);
                		        }
                            }
//                            if (processDef == null) return null;

                            long transId_ = arbInst.getLongValue(SA_TRANS_ID.name, row1, 0);
                			KrnObject initiator_ = arbInst.getObjectValue(SA_INITIATOR.name, row1);
                			KrnObject killer_ = arbInst.getObjectValue(SA_KILLER.name, row1);
                			boolean isProcess_ = arbInst.getBooleanValue(SA_PROCESS.name, row1, true);
                			KrnObject rootFlow = arbInst.getObjectValue(SA_ROOT_FLOW.name, row1);
                			String observers_ = arbFlow.getStringValue(SA_OBSERVERS.name, row1);
                			String uiObservers_ = arbFlow.getStringValue(SA_UI_OBSERVERS.name, row1);
                			String typeUiObservers_ = arbFlow.getStringValue(SA_TYPE_UI_OBSERVERS.name, row1);

                            ProcessInstanceImpl processInstance = new ProcessInstanceImpl(process_obj.id, 
                            		initiator_ != null ? initiator_.id : 0, processDef, transId_);
                            processInstance.setChopper(killer_ != null ? killer_.id : 0);
                            processInstance.setProcess(isProcess_);
                            processInstance.setObservers(observers_);
                            processInstance.setUiObservers(uiObservers_);
                            processInstance.setTypeUiObservers(typeUiObservers_);
                         
                            FlowImpl flow_ = new FlowImpl(flowObj, actor_ != null ? new long[] {actor_.id}:new long[0], processInstance);
                            flow_.setUser(user_ != null ? user_ : session.getUserSession().getUserObj());
                            flow_.setIp(session.getUserSession().getIp());
                            flow_.setComputer(session.getUserSession().getComputer());
                            FlowImpl rootFlow_ = flow_;
                            //Загрузка набора переменных с их значениями
                            if (buf != null && buf.length > 0) {
                                Map vc = WfUtils.loadFromXml(buf, session);
                                if (session.getDriver().getDatabase().hasArticleAttr) {
        	                        Object article = vc.get("ARTICLE");
        	                        if (article != null) {
        	                        	if (article instanceof Element)
        	                        		flow_.setArticle((Element)article,session);
        	                        	else
        	                        		flow_.setArticle((byte[])article,session);
        	                        	vc.remove("ARTICLE");
        	                        }
        	                        KrnObject articleLang = (KrnObject)vc.get("ARTICLE_LANG");
        	                        if (articleLang != null) {
        	                        	flow_.setArticleLang(articleLang,session);
        	                        	vc.remove("ARTICLE_LANG");
        	                        }
        	                        if(article!=null || articleLang!=null){
        	                        	byte[] buf_ = WfUtils.saveToXml(vc, null, null);
        	               	        	session.setBlob(flowObj.id, SA_FLOW_VAR.id, 0, buf_, 0, 0);
        	               	        	session.commitTransaction();
        	                        }
                                }
                                Map vc1=flow_.getVariable();
                                vc1.putAll(vc);
                                vc1=null;
                            }

                            Time start_ = (Time) arbInst.getValue(SA_START_PROCESS.name, row1);
                            if (start_ != null) processInstance.setStart(Funcs.convertTime(start_));
                            Time end_ = (Time) arbInst.getValue(SA_END_PROCESS.name, row1);
                            if (end_ != null) processInstance.setEnd(Funcs.convertTime(end_));
                            KrnObject superFlow_ = arbInst.getObjectValue(SA_SUPER_FLOW.name, row1);

                            //Загрузка потока
                            if (name_ != null) flow_.setName(name_);
                            if (start_f != null) flow_.setStart(Funcs.convertTime(start_f));
                            if (cur_f != null) flow_.setCurrent(Funcs.convertTime(cur_f));
                            if (end_f != null) flow_.setEnd(Funcs.convertTime(end_f));
                            if (node_ != null && !"".equals(node_) && processDef != null) {
                            	String[] ns = node_.split(";");
                                for (String aNode_ : ns) flow_.setNode(processDef.getNode(aNode_));
                           }
                            if (event_ != null) flow_.setEventType(EventType.fromText(event_));
                            if (statusMsg_ != null) flow_.setStatusMsg(statusMsg_);
                            if (sincNode_ != null) flow_.setSyncNode(sincNode_);
                            if (transition_ != null) flow_.setTransitionTo(transition_);
                            
                            if (ui_ instanceof String){
                            	String[] uiObj = ((String)ui_).split(",");
                            	if(uiObj.length>1)
                            		flow_.setUi(new KrnObject(Long.parseLong(uiObj[0]),uiObj[1],Long.parseLong(uiObj[2])));
                            	else
                            		flow_.setUi(session.getObjectByUid((String)ui_, -1));
                            	if(uiObj.length>3)
                            		flow_.setUiName(uiObj[3]);
                            }else if (ui_ instanceof KrnObject)
                                flow_.setUi((KrnObject)ui_);
                            if(typeUi_!=null){
                            	flow_.setUiType(typeUi_);
                            }
                            if(corelId_!=null){
                            	flow_.setCorelId(corelId_);
                            }
                            if (cut_obj != null) {
	                            if (cut_obj.equals("-1"))
	                                flow_.setCutObj(null);
	                            else {
	                            	String[] uis = cut_obj.split(";");
	                            	KrnObject cutOgjs[] = new KrnObject[uis.length];
	                            	for(int i=0;i<cutOgjs.length;i++){
		                            	String[] ui = uis[i].split(",");
		                            	if(ui.length>1)
		                            		cutOgjs[i]=new KrnObject(Long.parseLong(ui[0]),ui[1],Long.parseLong(ui[2]));
		                            	else
		                            		cutOgjs[i] = session.getObjectByUid(uis[i],-1);
	                            	}
	                            	flow_.setCutObj(cutOgjs);
	                            }
                            }
                            if (box_ != null) flow_.setBoxId(box_.id);
                            flow_.setTitle(titles_);
                            flow_.setTitleObj(titleObjs_);
                            flow_.setParentReactivation(react_);
                            flow_.setParam(param_);
                            engine = new ExecutionEngine(flow_, this, defComp,orgComp);
                            activeFlowIds.put(flow_.getId(), engine);
                            if (event_ != null) {
                                try{
                                    putCorelId(flow_);
                                }catch(JDOMException ex){
                                    log.error(ex, ex);
                                }
                            }

                            //Загрузка суперпотока
                            if (superFlow_ != null ) {
                                ExecutionEngine engineSuper = loadFlow(superFlow_, session, activeFlowIds);
                                if (engineSuper != null) {
                                	processInstance.setSuperProcessFlow(engineSuper.getFlow());
                                	engineSuper.getFlow().setSubProcessInstance(processInstance);
                                } else {
                                    log.error("ОШИБКА: отсутствует суперпроцесс!!!! idSuper=" + superFlow_.id + ";  id=" + flow_.getId());
                                }
                            }
                            //
                            //Загрузка корневого потока
                            if (rootFlow != null && rootFlow.id != flowObj.id) {
                                ExecutionEngine engineRoot = loadFlow(rootFlow, session, activeFlowIds);
                                rootFlow_ = engineRoot.getFlow();
                            }
                            processInstance.setRootFlow(rootFlow_);
                            //Загрузка родительского потока
                            if (parent_ != null && parent_.id != flowObj.id ) {
                                ExecutionEngine engineParent = loadFlow(parent_, session, activeFlowIds);
                                flow_.setParent(engineParent.getFlow());
                            }

                            if (children_ != null && children_.size() > 0) {
                                Collection<FlowImpl> children = new Vector<FlowImpl>();
                                for (Value aChildren_ : children_) {
                                        ExecutionEngine engineChild = loadFlow((KrnObject)aChildren_.value, session, activeFlowIds);
                                        if(engineChild==null || engineChild.getFlow()==null) {
                                            log.error("ОШИБКА при загрузке детей:aChildren_.value=" + aChildren_.value + "; для flowObj.id=" + flowObj.id);
                                        	continue;
                                        }
                                        engineChild.getFlow().setParent(flow_);
                                        children.add(engineChild.getFlow());
                                }
                                flow_.getChildren().addAll(children);
                            }
                            Node node = flow_.getNode();
                            Map<String, Object> vc = flow_.getVariable(session,SA_FLOW_VAR.id);
                            vc.put("SERVER", "SERVER");
                            vc = null;
                            if (processDef != null && processDef.getInspectors() != null) {
                                Object inspectors = WfUtils.getResolvExpression((ProcessDefinitionImpl)processDef, processDef.getInspectors(),flow_.getVariable(session,SA_FLOW_VAR.id), null, null,EventType.INSPECTORS, session);
                                if (inspectors != null && inspectors instanceof List && ((List) inspectors).size() > 0) {
                                    Vector<Long> inspectors_=new Vector<Long>();
                                    for(int i=0;i<((List)inspectors).size();++i){
                                    	if(((List)inspectors).get(i)!=null)
                                    		inspectors_.add(((KrnObject)((List)inspectors).get(i)).id);
                                    }
                                    flow_.setInspectors(inspectors_);
                                }
                            }
                            if (processDef != null && processDef.getTitleExpr() != null) {
                                Map<Long, String> o_ = WfUtils.getResolvExpressionAllLangs((ProcessDefinitionImpl)processDef, processDef.getTitleExpr(),flow_.getVariable(session,SA_FLOW_VAR.id), flow_, flow_.getNode(),EventType.TITLE_EXPR, session);
                                flow_.setProcessTitle(o_);
                            }

                            if (flow_.getNode() instanceof ActivityStateImpl) {
                            	ASTStart assignmentExpression = ((ActivityStateImpl) node).getAssignment();
                                KrnObject actor_krn=null;
                                String actorId_ = "";
                                long[] actorId=new long[0];
                                String assignmentKrn = ((ActivityState)node).getAssignmentKrn();
                                if(assignmentKrn!=null && !assignmentKrn.equals("")){
                                    actor_krn=session.getObjectByUid(assignmentKrn,0);
                                }
                                if(actor_krn!=null){
                                    actorId=new long[]{actor_krn.id};
                                }else if (assignmentExpression != null) {
                                    // get the assignment of the activity-state
                                    Object actor = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow_.getProcessInstance().getProcessDefinition(), assignmentExpression, null, flow_, flow_.getNode(),EventType.RESPONSIBLE, session);
                                    if (actor instanceof String){
                                        actorId_ = (String) actor;
                                        if("SERVER".equals(actorId_)){
                                            actorId=new long[]{0};
                                            flow_.setServer(true);
                                        }else
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
                                if(flow_.getActorId()!=null && flow_.getActorId().length>0 
                                		&& flow_.getActorId()[0]>0 && flow_.getActorFromId()<=0)
                                	flow_.setActorFromId(flow_.getActorId()[0]);
                                flow_.setActorId(actorId);
                            }
                            WfUtils.setFlowProperties(flow_, orgComp, session, false);
                		}
        			}
        		}

            }
        } catch (Exception ex) {
        	log.info(">>>>>>>>>>>>>>>>>>>>flow:"+flowObj.id);
            log.error(ex, ex);
        }
        return engine;
    }
    
    private ProcessInstanceImpl loadProcessInstance(FlowImpl flow,Session session){
    	Log log = getLog(session);
    	ProcessInstanceImpl processInstance=null;
        try {

        KrnObject[] process_objs = session.getObjects(flow.getId(), SA_PROCESS_INST.id, new long[0], 0);
		if (process_objs.length > 0) {
			KrnObject process_obj= process_objs[0];
            AttrRequestBuilder arbInst = new AttrRequestBuilder(session.getClassById(process_obj.classId), session);
            arbInst.add(SA_PROCESS_DEF.name).add(SA_TRANS_ID.name).add(SA_INITIATOR.name).add(SA_KILLER.name).add(SA_PROCESS.name)
            		.add(SA_ROOT_FLOW.name).add(SA_START_PROCESS.name).add(SA_END_PROCESS.name).add(SA_SUPER_FLOW.name);
            
        	QueryResult qr1 = session.getObjects(new long[] {process_obj.id}, arbInst.build(), 0);
    		for (Object[] row1 : qr1.rows) {
                KrnObject obj = arbInst.getObject(row1);

                KrnObject processDef_ = arbInst.getObjectValue(SA_PROCESS_DEF.name, row1);
                if (processDef_ == null) return null;
                ProcessDefinition processDef = defComp.getProcessDefinition(new Long(processDef_.id));
 		        if(processDef==null){
		        	processDef=(ProcessDefinitionImpl)deployProcess(processDef_.id, session);
		        }
                if (processDef == null) return null;

                long transId_ = arbInst.getLongValue(SA_TRANS_ID.name, row1, 0);
    			KrnObject initiator_ = arbInst.getObjectValue(SA_INITIATOR.name, row1);
    			KrnObject killer_ = arbInst.getObjectValue(SA_KILLER.name, row1);
    			boolean isProcess_ = arbInst.getBooleanValue(SA_PROCESS.name, row1, true);
    			KrnObject rootFlow = arbInst.getObjectValue(SA_ROOT_FLOW.name, row1);

                processInstance = new ProcessInstanceImpl(process_obj.id, 
                		initiator_ != null ? initiator_.id : 0, processDef, transId_);
                processInstance.setChopper(killer_ != null ? killer_.id : 0);
                processInstance.setProcess(isProcess_);
                Time start_ = (Time) arbInst.getValue(SA_START_PROCESS.name, row1);
                if (start_ != null) processInstance.setStart(Funcs.convertTime(start_));
                Time end_ = (Time) arbInst.getValue(SA_END_PROCESS.name, row1);
                if (end_ != null) processInstance.setEnd(Funcs.convertTime(end_));
                KrnObject superFlow = arbInst.getObjectValue(SA_SUPER_FLOW.name, row1);
                //Загрузка суперпотока
                if (superFlow != null){
                   	ExecutionEngine engineSuper = loadFlow(superFlow, session, null);
                    if (engineSuper != null) {
                    	processInstance.setSuperProcessFlow(engineSuper.getFlow());
                    	engineSuper.getFlow().setSubProcessInstance(processInstance);
                    } else {
                        log.error("ОШИБКА: отсутствует суперпроцесс!!!! idSuper=" + superFlow.id + ";  id=" + flow.getId());
                    }
                }
                //
                //Загрузка корневого потока
                if (rootFlow != null){
                	ExecutionEngine engineRoot = loadFlow(rootFlow, session, null);
                        if(engineRoot!=null)
                        	processInstance.setRootFlow(engineRoot.getFlow());
                        else
                            log.error("ОШИБКА: отсутствует корневой поток!!!! rootFlow.id=" + rootFlow.id + ";  id=" + flow.getId());
                }
    		}
		}
   	
        } catch (Exception ex) {
        	log.info("Ошибка прои загрузке processInstance для flow:" + flow.getId());
            log.error(ex, ex);
        }
        return processInstance;
    }

    public List<Lock> getConflictLocker(long objId, long flowId, long pdId, Session session) {
    	Log log = getLog(session);
		ProcessDefinitionImpl process = null, superProcess;
		ASTStart conflictExpr = null, superConflictExpr;
		if (flowId > 0) {
			try {
				KrnObject fobj=session.getObjectById(flowId, 0);
				if (fobj != null) {
					KrnObject[] pobjs=session.getObjects(fobj.id, SA_PROCESS_INST.id, new long[0], 0);
					if(pobjs!=null && pobjs.length>0){
						KrnObject[] pdobjs=session.getObjects(pobjs[0].id, SA_PROCESS_DEF.id, new long[0], 0);
						if(pdobjs!=null && pdobjs.length>0){
							process = (ProcessDefinitionImpl) defComp.getProcessDefinition(pdobjs[0].id);
							if(process==null)
								process = (ProcessDefinitionImpl) deployProcess(pdobjs[0].id, session);
							if (process != null) {
								conflictExpr = process.getConflict();
								if (conflictExpr == null) {
									KrnObject sfobj = fobj;
									while (true) {
										KrnObject[] spobjs=session.getObjects(sfobj.id, SA_PROCESS_INST.id, new long[0], 0);
										if(spobjs!=null && spobjs.length>0){
											KrnObject[] sfobjs=session.getObjects(spobjs[0].id, SA_SUPER_FLOW.id, new long[0], 0);
											if (sfobjs == null || sfobjs.length==0 || sfobjs[0].equals(fobj))
												break;
											pdobjs=session.getObjects(pobjs[0].id, SA_PROCESS_DEF.id, new long[0], 0);
												if(pdobjs!=null && pdobjs.length>0){
													superProcess = (ProcessDefinitionImpl) defComp.getProcessDefinition(pdobjs[0].id);
													if(superProcess==null)
														superProcess = (ProcessDefinitionImpl) deployProcess(pdobjs[0].id, session);
													superConflictExpr = superProcess.getConflict();
												if (superConflictExpr != null) {
													process = superProcess;
													conflictExpr = superConflictExpr;
													break;
												}
											}else
												break;
											sfobj=sfobjs[0];
										}else
											break;
									}
								}
							}
						}
					}
				}
			} catch (KrnException e) {
				log.error(e, e);
			}
		} else if (pdId > 0) {
			process = (ProcessDefinitionImpl) defComp.getProcessDefinition(pdId);
	        if(process==null){
	        	process=(ProcessDefinitionImpl)deployProcess(pdId, session);
	        }
			conflictExpr = process.getConflict();
		}

		List<Lock> res = new ArrayList<Lock>();
		if (process != null && conflictExpr != null) {
			try {
				List<KrnObject> conflict = (List<KrnObject>) WfUtils.getResolvExpression(process, conflictExpr, null, null, null, EventType.CONFLICT_PROCESS, session);
				if (conflict != null && conflict.size() > 0) {
					for (KrnObject aConflict : conflict) {
						if (aConflict != null) {
							Lock lock = session.isObjectLock2(objId, aConflict.id);
							if (lock != null) {
								res.add(lock);
							}
						}
					}
				}
			} catch (Exception ex) {
				log.error(ex, ex);
			}
		}
		return res;
	}

    public List<String> getProcessLocker(long processId, Session session){
    	Log log = getLog(session);

    	ProcessDefinitionImpl process=null;
		ASTStart conflictExpr=null;
        ArrayList<String> res= new ArrayList<String>();
		if(processId>0){
				process= (ProcessDefinitionImpl)defComp.getProcessDefinition(processId);
		        if(process==null){
		        	process=(ProcessDefinitionImpl)deployProcess(processId, session);
		        }
				conflictExpr=process.getConflict();
		}
		if(process!=null && conflictExpr!=null){
			try{
				List<KrnObject> conflict  =  (List<KrnObject>)WfUtils.getResolvExpression(process,
							conflictExpr, null, null, null,EventType.CONFLICT_PROCESS, session);
			if(conflict!=null && conflict.size()>0){
                for (KrnObject aConflict : conflict) {
                    String res_str= isObjectLock(aConflict.id, session);
                    if (!res_str.equals("")) res.add(res_str);
                }
				return res;                }
			} catch(Exception ex){
	            log.error(ex, ex);
			}
		}
		 return  res;
    }

    public KrnObject[] getConflictProcess(long processId, Session session){
    	Log log = getLog(session);

    	ProcessDefinition process=defComp.getProcessDefinition(processId);
        if(process==null){
        	process=(ProcessDefinitionImpl)deployProcess(processId, session);
        }
    	ASTStart conflictExpr=process.getConflict();
        if(conflictExpr!=null){
            try{
               List<KrnObject> conflict  =  (List<KrnObject>)WfUtils.getResolvExpression((ProcessDefinitionImpl)process,
                    conflictExpr, null, null,null, EventType.CONFLICT_PROCESS, session);
                if(conflict!=null && conflict.size()>0)
                    return conflict.toArray(new KrnObject[conflict.size()]);
            } catch(Exception ex){
                log.error(ex, ex);
            }

        }
         return  null;
    }

    public void lock(long objId, long flowId, Session session) throws KrnException {
    	Log log = getLog(session);
		KrnObject fobj = session.getObjectById(flowId,0);
		if (fobj != null) {
			KrnObject pobjs[] = session.getObjects(flowId, SA_PROCESS_INST.id, new long[0], 0);
			if(pobjs!=null && pobjs.length>0 && pobjs[0] !=null){
				KrnObject pdobjs[] = session.getObjects(pobjs[0].id, SA_PROCESS_DEF.id, new long[0], 0);
				if(pdobjs!=null && pdobjs.length>0 && pdobjs[0] !=null){
					long lockerId = pdobjs[0].id;
					List<Lock> conflicts = getConflictLocker(objId, flowId, 0, session);
					if (conflicts.size() > 0) {
						String message = "Объект уже заблокирован " + (conflicts.size() == 1 ? "процессом" : "процессами") + ": ";
						for(int i = 0; i < conflicts.size(); i++) {
							Lock conflict = conflicts.get(i);
							try {
								message += "'>>" +  "<<<<' - flowId=" + conflict.flowId + (i == conflicts.size() - 1 ? "." : ", ");
							} catch(Exception e) {}
						}
						throw new KrnException(0, message);
					}
					try {
						session.lockObject(objId, lockerId, Lock.LOCK_FLOW, flowId, null);
						log.debug(new StringBuilder("lock objectId=").append(objId).append("  flowId=").append(flowId).append(" processId=").append(lockerId).toString());
					} catch (KrnException e) {
						try {
							session.rollbackTransaction();
						} catch (Exception eex) {
							log.error(eex, eex);
						}
						throw e;
					}
				}
			}
		}
	}

    public void unlock(long objId, long flowId, Session session) throws KrnException {
    	Collection<Lock> locks = session.getLocksByObjectId(objId);
    	for (Lock lock : locks) {
    		if(lock.flowId==flowId){
    			session.unlockObject(objId, lock.lockerId);
    		}
    	}
    }

    public KrnObject getLocker(long objId, long processDefId,Session session) {
    	Log log = getLog(session);
    	try {
	    	Lock lock = session.getLock(objId, processDefId);
	    	if (lock != null) {
	    		return session.getObjectById(lock.flowId, 0);
	    	}
    	} catch (KrnException e) {
    		log.error(e, e);
    	}
    	return null;
    }

    
    public String isCachedObjectLock(long objId, Session session) throws KrnException {
    	Collection<Lock> locks = session.getLocksByObjectId(objId);
    	KrnObject lang = session.getUserSession().getDataLanguage();
    	for (Lock lock : locks) {
    		if (!((ServerUserSession)session.getUserSession()).cachedUnlocked(objId, lock.lockerId)) {
        		KrnObject fobj = session.getObjectById(lock.flowId,0);
        		if (fobj != null) {
            		KrnObject pobj = session.getObjectById(lock.lockerId,0);
            		String pname=session.getStringsSingular(pobj.id, SA_SERVICE_TITLE.id, lang.id, false, false);
        			KrnObject actors[] = session.getObjects(fobj.id, SA_ACTOR.id, new long[0], 0);
                    String userName = "";
                    if (actors != null && actors.length > 0 && actors[0]!=null) {
                        UserSrv user = orgComp.findActorById(actors[0].id,session);
                        if (user != null)
                            userName = user.getUserName();
                    }
                    return "'" + pname + "'" + (!userName.equals("") ? "; пользователь:'" + userName + "'" : "");
        		}else
    	        	session.unlockObject(objId, lock.lockerId);
    		}
    	}
        return null;
    }

    public String isObjectLock(long objId, Session session) throws KrnException {
    	Collection<Lock> locks = session.getLocksByObjectId(objId);
    	KrnObject lang = session.getUserSession().getDataLanguage();
    	for (Lock lock : locks) {
    		KrnObject fobj = session.getObjectById(lock.flowId,0);
    		if (fobj != null) {
        		KrnObject pobj = session.getObjectById(lock.lockerId,0);
        		String pname=session.getStringsSingular(pobj.id, SA_SERVICE_TITLE.id, lang.id, false, false);
    			KrnObject actors[] = session.getObjects(fobj.id, SA_ACTOR.id, new long[0], 0);
                String userName = "";
                if (actors != null && actors.length > 0 && actors[0]!=null) {
                    UserSrv user = orgComp.findActorById(actors[0].id,session);
                    if (user != null)
                        userName = user.getUserName();
                }
                return "'" + pname + "'" + (!userName.equals("") ? "; пользователь:'" + userName + "'" : "");
    		}else
	        	session.unlockObject(objId, lock.lockerId);
    	}
        return "";
    }

    public boolean setSelectedObjects(long activityId, long nodeId, KrnObject[] sel_objs, Session session) {
    	Log log = getLog(session);
        ExecutionEngine engine = getEngine(activityId, session);
        if (engine != null) {
            try {
                FlowImpl flow_ = engine.getFlow();
                if (sel_objs.length == 0 || flow_ == null) return false;
                Map<String,Object> var_ = flow_.getVariable(session,SA_FLOW_VAR.id);
                var_.put("SELOBJ", sel_objs[0]);

                List<KrnObject> sobjs = new ArrayList<KrnObject>(sel_objs.length);
                for (KrnObject sobj : sel_objs)
                	sobjs.add(sobj);
                var_.put("SELOBJS", sobjs);
                
                if( !(flow_.getNode() instanceof ActivityStateImpl))return false;
                ActivityStateImpl activity_ = (ActivityStateImpl) flow_.getNode();
                if (!activity_.getId().equals("" + nodeId)) return false;
                ASTStart title = activity_.getTitle();
                ASTStart titleObj = activity_.getObjTitleExpression();
                ASTStart param = activity_.getParamExpression();
                if (title != null) {
                    Map<Long, String> o_ = WfUtils.getResolvExpressionAllLangs(
                    		(ProcessDefinitionImpl)flow_.getProcessInstance().getProcessDefinition(),
                    		title, null, flow_, flow_.getNode(),EventType.TITLE_EXPR, session);
                    flow_.setTitle(o_);
                }
                if (titleObj != null) {
                    Map<Long, String> o_ = WfUtils.getResolvExpressionAllLangs(
                    		(ProcessDefinitionImpl)flow_.getProcessInstance().
                            getProcessDefinition(), titleObj, null, flow_, flow_.getNode(),
                            EventType.ACT_OBJ_TITLE_EXPR, session);
                    flow_.setTitleObj(o_);
                } else
                    flow_.setTitleObj(null);
                if (param != null) {
                	Map<Long, String> o_ = WfUtils.getResolvExpressionAllLangs(
                			(ProcessDefinitionImpl)flow_.getProcessInstance().getProcessDefinition(),
                            param, null, flow_, flow_.getNode(),EventType.ACT_OBJ_TITLE_EXPR, session);
                	flow_.setParamObj(o_);
                } else
                    flow_.setParamObj(null);
                flow_.setTaskColor(activity_.getTaskColor());
	            engine.saveFlow(flow_, session);
	            session.commitTransaction();
            } catch (Exception ex) {
                log.error(ex, ex);
            	session.rollbackTransactionQuietly();
            }
        }
        return true;
    }
    public Object openInterface(long activityId,Session session) throws WorkflowException {
        ExecutionEngine engine = getEngine(activityId, session);

        if (engine != null) {
            FlowImpl flow_ = engine.getFlow();
            Node node=flow_.getNode();
            if(node instanceof ActivityStateImpl){
            	Map<String,Object> vc=flow_.getVariable(session, SA_FLOW_VAR.id);
            	vc=null;
            	return engine.runActionsForEvent(EventType.BEFORE_OPEN_INTERFACE,
            			(ActivityStateImpl)node,
            			flow_,
            			session);
            }
        }
        return null;
    }
    public void setProcessInitiator(long flowId, long userId, Session session) throws WorkflowException,KrnException {
    	ExecutionEngine engine = getEngine(flowId, session);
        if (engine != null) {
            FlowImpl flow = engine.getFlow();
            ProcessInstanceImpl processInstance = (ProcessInstanceImpl) flow.getProcessInstance();
            processInstance.setInitiator(userId);
        	session.setObject(processInstance.getId(), SA_INITIATOR.id, 0, userId, 0, false);
        }
    }

    public void setPermit(long activityId, boolean permit, Session session) throws WorkflowException,KrnException {
    	Log log = getLog(session);
        ExecutionEngine engine = getEngine(activityId, session);
        
        if (engine != null) {
            FlowImpl flow_ = engine.getFlow();
            if (flow_ == null) return;
			WfUtils.setFlowProperties(flow_,orgComp,session,false);
           Map<String,Object> var_ = flow_.getVariable(session,SA_FLOW_VAR.id);
            var_.put("INTERFACE", permit ? 1 : 0);
            boolean par_ = false;
            if (permit && (flow_.getParam() & Constants.ACT_PERMIT) != Constants.ACT_PERMIT) {
                flow_.setParam(flow_.getParam() | Constants.ACT_PERMIT);
                if ((flow_.getParam() & Constants.ACT_ERR) == Constants.ACT_ERR)//убираем информацию об ошибке
                    flow_.setParam(flow_.getParam() ^ Constants.ACT_ERR);
                par_ = true;
            } else if (!permit && (flow_.getParam() & Constants.ACT_PERMIT) == Constants.ACT_PERMIT) {
                flow_.setParam(flow_.getParam() ^ Constants.ACT_PERMIT);
                par_ = true;
            }
            if (par_) {
                try {
                    //поток
                    byte[] buf = WfUtils.saveToXml(var_, null, null);
                    session.setBlob(flow_.getId(), SA_FLOW_VAR.id, 0, buf, 0, 0);
                    session.setLong(flow_.getId(), SA_PERMIT.id, 0, flow_.getParam(), 0);
                    session.commitTransaction();
        	    	log.info("setPermit: " + flow_.getId() + ", UI: " + flow_.getUi());

                    long[] actorId = flow_.getActorId();
                    Vector<Long> inspectors=flow_.getInspectors();
                    if (((actorId.length > 1 || actorId.length>0 && actorId[0]>0) && (flow_.getParam() & Constants.ACT_AUTO) != Constants.ACT_AUTO)|| inspectors.size()>0) {
                        TreeSet<Long> userIds = new TreeSet<Long>();
                        TreeSet<Long> userIdsInf = new TreeSet<Long>();
                        if (actorId.length > 1 || actorId.length>0 && actorId[0]>0){
                            for(long aId:actorId){
                                userIds.add(aId);
                            }
                        }
                        if(inspectors.size()>0) userIdsInf.addAll(inspectors);
                        if (userIds.size() > 0)
                            clientTaskReload(userIds,userIdsInf, flow_);
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        }
    }
    private class DateThread extends Thread {
        // This method is called when the thread runs
        public void run() {
        	Log log = getLog(user.getLogUserName());
            Session s = null;
    		if(isDateAlarmSet){// Если нужно перевычислить формулы для дат внимания и завершения и установить цвет задания
    			setStatusAlarm();
    		}
        	while(true){
        		try {
            		sleep(threadSleep);
            		s = SrvUtils.getSession(user);
                   log.info("autoRefresh: start!");
            		autoRefresh(s);
        		} catch(Throwable e) {
        			log.error(e, e);
                } finally {
                    if (s != null) {
                        s.release();
                    }
                    log.info("autoRefresh: end!");
        		}
        	}
        }
    }
    private void autoRefresh(Session session) {
    	Log log = getLog(session);
    	try {
			List<String> flowsDateAlarm=session.getFlowsByControlDate(false,null);
			if(flowsDateAlarm!=null && flowsDateAlarm.size()>0){
	            TreeSet<Long> userIdsInf = new TreeSet<Long>();
	            for(String dateStr:flowsDateAlarm){
	            	String[] dateStrs=dateStr.split(";");
	            	try{
			            TreeSet<Long> userIds = new TreeSet<Long>();
			            userIds.add(Long.parseLong(dateStrs[1]));
			            com.cifs.or2.server.Session.clientTaskReload(userIds,userIdsInf,Long.parseLong(dateStrs[0]));
	            	}catch(Exception e){
	            		log.info("Ошибка при вызове clientTaskReload для userId:"+dateStrs[1]+";flowId:"+dateStrs[0]);
	            	}
	            }
			}
		} catch (KrnException e1) {
    		log.info("Ошибка при получении списка потоков с просроченной датой контроля!");
			log.error(e1, e1);
		}
    }
    private void setStatusAlarm(){
    	Session session=null;
    	try {
    		Thread.sleep(30000);
    		session = SrvUtils.getSession(user);
    		//Установка цвета для задания
    		Map<Long,String> pdim = new HashMap<>();
    		for(ProcessDefinitionImpl pdi:defComp.getProcessDefinition()){
    			for(Node node:pdi.getNodes()){
    				if(node instanceof ActivityStateImpl && ((ActivityStateImpl)node).getTaskColor()!=null && !"".equals(((ActivityStateImpl)node).getTaskColor())){
    					pdim.put(pdi.getId(), node.getId()+";"+((ActivityStateImpl)node).getTaskColor());
    				}
    			}
    		}
    		//Установка даты внимания
			List<String> flowsDateAlarm=session.getFlowsByControlDate(true,pdim);
            for(String dateStr:flowsDateAlarm){
            	String[] dateStrs=dateStr.split(";");
            	ExecutionEngine engine = getEngine(Long.parseLong(dateStrs[0]), session);
		        if (engine != null) {
		            FlowImpl flow_ = engine.getFlow();
		            if (flow_ == null) return;
		            if(dateStrs.length>2){
		            	flow_.setUi(flow_.getUi());
		            	flow_.setTaskColor(dateStrs[2]);
		            }else
		            	WfUtils.setFlowDateProperties(flow_,session);
					engine.saveFlow(flow_, session);
		        }
            }
			session.commitTransaction();
		} catch (Exception e) {
    		log.info("Ошибка при обновлении даты внимания и завершения!");
			log.error(e, e);
			if(session!=null)
			try {
				session.rollbackTransaction();
			} catch (KrnException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }
    public SuperMap[] getMapList(long[] flowIds, Session s) {
        List<Pair<FlowImpl,Long>>list_ = new Vector<Pair<FlowImpl,Long>>();
        for (long flowId : flowIds) {
            while (true) {
                ExecutionEngine engine = getEngine(flowId, s);
                FlowImpl flow_ = (engine != null) ? (FlowImpl) engine.getFlow().getProcessInstance().getSuperProcessFlow() : null;
                if (flow_ != null) {
                    list_.add(new Pair<FlowImpl,Long>(flow_, flowId));
                    flowId = flow_.getId();
                } else
                    break;
            }
        }
        SuperMap[] res_ = new SuperMap[list_.size()];
        for (int i = 0; i < res_.length; ++i) {
            Pair p_ = list_.get(i);
            res_[i] = new SuperMap();
            res_[i].flowId = ((FlowImpl) p_.first).getId();
            ProcessInstance processInstance = ((FlowImpl) p_.first).getProcessInstance();
            if (processInstance != null) {
            	ProcessDefinition processDefinition = processInstance.getProcessDefinition();
            	if (processDefinition != null) { 
            		res_[i].processDefId = processDefinition.getId();
            	}	
			}
            res_[i].subflowId = (Long) p_.second;
            res_[i].nodes = getNodesId((FlowImpl) p_.first);
        }
        return res_;
    }

    private long[][] getNodesId(FlowImpl flow_) {
        Collection<FlowImpl> c = new ArrayList<FlowImpl>();
        FlowImpl parentFlow = (FlowImpl) flow_.getParent();
        if (parentFlow == null) parentFlow = flow_;
        c.add(parentFlow);
        parentFlow.getFlowse(c);
        long[][] res = new long[c.size()][];
        Collection<String> cc = flow_.getNodes();
        int i = 0, j = 0;
        res[i] = new long[cc.size()];
        for (String aCc1 : cc) {
            res[i][j++] = aCc1 != null ? Long.parseLong(aCc1) : 0L;
        }
        for (Object aC : c) {
            FlowImpl flow = (FlowImpl) aC;
            if (flow_ == flow) continue;
            cc = flow.getNodes();
            if (res.length > 1 && i < res.length - 1) {
                res[++i] = new long[cc.size()];
                j = 0;
                for (String aCc : cc) {
                    res[i][j++] = aCc!= null ? Long.parseLong(aCc) : 0L;
                }
            }
        }
        return res;
    }

    private String getLastNodeName(FlowImpl flow_, long langId) {
        Node n = flow_.getNode();
        ProcessDefinitionImpl pd = (ProcessDefinitionImpl) flow_.getProcessInstance().getProcessDefinition();
        try {
            String res = (n != null) ? pd.getString(langId, "name_" + n.getId()) : "";
            return (res != null && !res.isEmpty()) ? res : (n != null) ? n.getName() : "";
        } catch (Exception e) {
        	Log log = getLog(user.getLogUserName());
            log.error(e, e);
        }
        return "";
    }

    public synchronized int messageReceived(File file,Object msg, Box box) {
    	Log log = getLog(user.getLogUserName());

    	ExecutionEngine engine = null;
        Element xml=null;
        Session session = null;
        int res = MessageCash.NOT_FOUND;
        try {
            session = SrvUtils.getSession(user);
	        String fileName=file.getName();
	        if(msg!=null && box.getTransportId()!=MAIL){
	        	xml=(Element)msg;
	        }
	        //Проверка xml на ждущих почтовых ящиках
	        List <Long>flowIds = new ArrayList<Long>();
	        long flowId = Long.valueOf(fileName.substring(fileName.lastIndexOf("_") + 1));
	        if(flowId>0){
	            KrnObject fobj = session.getObjectById(flowId, 0);
	            if(fobj!=null)
	                flowIds.add(flowId);
	            else return res;
	        }else{
	            if(xml!=null){
	                flowId = getFlowIdCorelId(xml,session);
					if(flowId>0)
					    flowIds.add(flowId);
	            }else{
	            	long[] flows=session.getFlowsByEvent(EventType.BEFORE_CHECK_XML.toString(), box.getKrnObject().uid);
                    for (long flowId_:flows) {
                                flowIds.add(flowId_);
                    }
	            }
	        }
	        for (long flowId_ : flowIds) {
	            KrnObject fobj=session.getObjectById(flowId_, 0);
                ExecutionEngine engine_=loadFlow(fobj, session, null);
	            FlowImpl flow_ = engine_.getFlow();
	            Map vc=flow_.getVariable(session,SA_FLOW_VAR.id);
	            if(box.getTransportId()==MAIL)
	                vc.put("MAIL", msg);
	            else if(xml!=null)
	            	vc.put("XML", xml);
	            else
	                vc.put("FILEPATH",file.getAbsolutePath());
	            try {
	                int res_= engine_.checkXml(box,session);
	                if (res_ == 1) {
	                    engine = engine_;
	                    break;
	                } else if(res_==-1){
	                    return MessageCash.NORMAL;
	                } else{
	                    if(box.getTransportId()==MAIL)
	                        vc.remove("MAIL");
	                    else if(xml!=null)
	                    	vc.remove("XML");
	                    else
	                    	vc.remove("FILEPATH");
	                }
	            } catch (WorkflowException e) {
	                log.error(e, e);
	            }
	            vc=null;
	        }
        } catch (Throwable e) {
            session.rollbackTransactionQuietly();
            log.error(e, e);
        } finally {
            if (session != null) {
                session.release();
            }
        }

        //Проверка параметров
        MessageCash msgCache = getMessageCache();
        if (engine != null) {
        	long flowId = engine.getFlow().getId();
            //Запись в лог-журнал о принятии xml в обработку
            try {
                session = SrvUtils.getSession(user);
                msgCache.writeLogRecord(ExchangeEvents.MSC_101, msgCache.getMessageId(box,MessageCash.IN,xml),msgCache.getMessageType(box,MessageCash.IN,xml),
                    file.getPath(), box.getUrlIn());
                engine.getFlow().setEventType(EventType.CHECK_XML);
                engine.saveFlow(engine.getFlow(),session);
                session.commitTransaction();
                if(xml!=null)
                    engine.getFlow().setCorelId("");
            	Session flowSession=null;
            	boolean flowLock=false;
        		try {
        			flowSession = SrvUtils.getSession(session.getUserSession());
    				String expr="Try Lock "+flowLock+" record-flowId="+flowId+"; Session=" + flowSession.getUserSession().getId();
    				writeExprToFile(expr);
        			flowLock = flowSession.lockRecord(SC_FLOW, flowId, 0);
   					expr = " Lock "+flowLock+" record-flowId="+flowId+"; Session=" + flowSession.getUserSession().getId();
   					writeExprToFile(expr);
        		} catch (KrnException e1) {
        			// TODO Auto-generated catch block
        			log.error(e1, e1);
        		}finally{
	        		if(flowLock){
		                engine.start(flowSession);
		                return MessageCash.NORMAL;
	        		} else
	        			if (flowSession != null) flowSession.release();
        		}

            } catch (Throwable e) {
                session.rollbackTransactionQuietly();
                log.error(e, e);
            } finally {
                if (session != null) {
                    session.release();
                }
            }
        } else {
            Map<String,Object> var = new HashMap<String,Object>();
            if(box.getTransportId()==MAIL)
                var.put("MAIL", msg);
            else if(xml!=null)
            	var.put("XML", xml);
            else
                var.put("FILEPATH",file.getPath());
            Collection nods = null;
            try {
                nods = getProcesses(box);
            } catch (WorkflowException e) {
                log.error(e, e);
            }
            if (engine == null && nods != null && nods.size() > 0) {
                try {
                    session = SrvUtils.getSession(user);
                    for (Object nod : nods) {
                        NodeImpl node = (NodeImpl) nod;
                        Collection actions = node.getActions();
                        // поиск действия соответствующего данному событию
                        for (Object action1 : actions) {
                            ActionImpl action = (ActionImpl) action1;
                            if (action.getEventType() == EventType.CHECK_XML) {
                            	ASTStart expr = action.getExpression();
                                if (expr == null) continue;
                                //Проверка параметров
                                try{
                                    ProcessDefinitionImpl pd = (ProcessDefinitionImpl)node.getProcessDefinition();
                                    Object res_ = WfUtils.getResolvExpression(pd,expr, var, null,node, EventType.CHECK_XML, session);
                                    if (res_ != null && res_ instanceof Number && ((Number) res_).intValue() == 1) {
                                    	getMessageCache().writeLogRecord(ExchangeEvents.MSC_101,
                                                xml!=null?msgCache.getMessageId(box,MessageCash.IN,xml):file.getAbsolutePath(),
                                                xml!=null?msgCache.getMessageId(box,MessageCash.IN,xml):file.getAbsolutePath(),
                                                file.getPath(), box.getUrlIn());
	                                        // Запуск соответствующего процесса
	                                        List<String> errs = new ArrayList<String>(2);
                                       engine = createProcessInstance(node.getProcessDefinition().getId(), new long[] {-1}, null, var, session, Constants.SUBPROCESS_PASS, errs,false,null);
                                        FlowImpl flow = engine.getFlow();
                                        flow.setNode(node);
                                        flow.setEventType(EventType.CHECK_XML);
                                        UserSrv suser = orgComp.getSuperUser(session);//Необходим суперюзер для старта процесса
                                        flow.setActorId(new long[] {suser.getUserId()});
                           				flow.setUser(suser.getUserObj());
                                        flow.setIp(user.getIp());
                                        flow.setComputer(user.getComputer());
                                        //записываем историю
                                        flow.setActorFromId(suser.getUserId());
                                        engine.saveFlow(flow,session);
                                        session.commitTransaction();
                                        res = MessageCash.NORMAL;
                                        break;
                                    }else {
                                        session.rollbackTransactionQuietly();
                                    }
                                } catch (Throwable ex) {
                                    session.rollbackTransactionQuietly();
                                    log.error(ex, ex);
                                }

                                var.clear();
                                if(box.getTransportId()==MAIL)
                                    var.put("MAIL", msg);
                                else if(xml!=null)
                                	var.put("XML", xml);
                                else
                                	var.put("FILEPATH", file.getAbsolutePath());
                           }
                        }
                        if (res == MessageCash.NORMAL) break;
                    }
                } catch (KrnException e) {
                    log.error(e, e);
                } finally {
                    if (session != null) {
                        session.release();
                    }
                }
            }
            if (engine != null) {
            	long flowId = engine.getFlow().getId();
            	Session flowSession=null;
            	boolean flowLock=false;
        		try {
        			flowSession = SrvUtils.getSession(session.getUserSession());
    				String expr="Try Lock "+flowLock+" record-flowId="+flowId+"; Session=" + flowSession.getUserSession().getId();
    				writeExprToFile(expr);

    				flowLock = flowSession.lockRecord(SC_FLOW, flowId, 0);
   					expr=" Lock "+flowLock+" record-flowId="+flowId+"; Session=" + flowSession.getUserSession().getId();
   					writeExprToFile(expr);
        		} catch (KrnException e1) {
        			// TODO Auto-generated catch block
        			log.error(e1, e1);
        		}
        		if(flowLock)
        			engine.start(flowSession);
        		else
        			if (flowSession != null) flowSession.release();

            }
        }
        return res;
    }

    public synchronized int responseSend(long flowId, boolean result) {
        ExecutionEngine engine = null;
       	KrnObject fobj=null;
        Session session = null;
		try {
			session = SrvUtils.getSession(user);
			fobj = session.getObjectById(flowId, 0);
        	if(fobj!=null)   	engine=loadFlow(fobj, session, null);
		} catch (KrnException e) {
            String userName = user.getLogUserName();
        	Log log = getLog(userName);
        	log.error(e, e);
        } finally {
            if (session != null) {
                session.release();
            }
		}
        if (engine != null) {
            engine.join();
            FlowImpl flow = engine.getFlow();
            if(flow!=null && flow.getNode()!=null && (OutBoxStateImpl.class.equals(flow.getNode().getClass())) 
            		&& flow.getEventType()!= null && !flow.getEventType().equals(EventType.PERFORM_XML))
                return MessageCash.NOT_PERFORM;
            if(flow==null || flow.getNode()==null 
            		||!(OutBoxStateImpl.class.equals(flow.getNode().getClass())) 
            		|| flow.getEventType()== null || !flow.getEventType().equals(EventType.PERFORM_XML))
                return MessageCash.NORMAL;
            session = null;
        	Log log = null;
            try {
            	String ip = flow.getIp() != null ? flow.getIp() : user.getIp();
            	String comp = flow.getComputer() != null ? flow.getComputer() : user.getComputer();
            	if (flow.getUser() != null) {
            		session = SrvUtils.getSession(dsName, flow.getUser(), ip, comp, false);
            	} else {
            		session = SrvUtils.getSession(user);
            	}
                String userName = session.getUserSession().getLogUserName();
            	log = getLog(userName);

                Map vc=flow.getVariable(session,SA_FLOW_VAR.id);
                vc=null;
                if (result) {
                    if ((flow.getParam() & Constants.ACT_ERR) == Constants.ACT_ERR)
                        flow.setParam(flow.getParam() ^ Constants.ACT_ERR);
                    flow.setEventType(EventType.AFTER_PERFORM_XML);
                	Session flowSession=null;
                	boolean flowLock=false;
            		try {
            			flowSession = SrvUtils.getSession(session.getUserSession());
        				String expr="Try Lock "+flowLock+" record-flowId="+flowId+"; Session=" + flowSession.getUserSession().getId();
        				writeExprToFile(expr);
        				flowLock = flowSession.lockRecord(SC_FLOW, flow.getId(), 0);
      					expr=" Lock "+flowLock+" record-flowId="+flow.getId()+"; Session=" + flowSession.getUserSession().getId();
       					writeExprToFile(expr);
            		} catch (KrnException e1) {
            			// TODO Auto-generated catch block
            			log.error(e1, e1);
            		}finally{
	            		if(flowLock){                    
	//	                    engine.saveFlow(flow, session);
		                    session.commitTransaction();
		                    engine.start(flowSession);
	            		} else 
	            			if (flowSession != null) flowSession.release();
            		}
                } else {
                    flow.setParam(flow.getParam() | Constants.ACT_ERR);
                    session.setLong(flow.getId(), SA_PERMIT.id, 0, flow.getParam(), 0);
                    session.commitTransaction();
                }
                return MessageCash.NORMAL;
            } catch (Throwable ex) {
                assert session != null;
                session.rollbackTransactionQuietly();
                if (log != null) log.error(ex, ex);
            } finally {
                if (session != null) {
                    session.release();
                }
            }
        }
        return MessageCash.NOT_FOUND;
    }

    public void setDebugStatus(FlowImpl flow, Session session) {
        if (DEBUG) {
            String userName = session.getUserSession().getLogUserName();
        	Log log = getLog(userName);
            try {
                if (flow.getEventType() == null || flow.getNode()==null) return;
                String eventName = NodeEventType.forEventType(flow.getEventType()) != null
                        ? NodeEventType.forEventType(flow.getEventType()).getTitle() : flow.getEventType().toString();
                Element root = null, node = null;
                byte[] data = session.getBlob(flow.getId(), SA_FLOW_DEBUG.id, 0, 0, 0);
                if (data.length > 0) {
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
                    root = doc.getRootElement();
                    List nodes = root.getChildren("node");
                    if (nodes.size() > 0) {
                        node = (Element) nodes.get(nodes.size() - 1);
                        if (!node.getText().equals(flow.getNode().getName())) {
                            node = null;
                        }
                    }
                }
                if (root == null) root = new Element("debug");
                if (node == null) {
                    node = new Element("node");
                    node.setText(flow.getNode().getName());
                    root.addContent(node);
                }
                Element event = new Element("event");
                event.setText(eventName);
                node.addContent(event);
                data = WfUtils.saveToXml(flow.getVariable(session,SA_FLOW_VAR.id), root, event);
                session.setBlob(flow.getId(), SA_FLOW_DEBUG.id, 0, data, 0, 0);
                session.commitTransaction();
            } catch (Exception e) {
                log.error(e, e);
                try {
                    session.rollbackTransaction();
                } catch (Exception eex) {
                    log.error(eex, eex);
                }
            }
        }
    }

    public FlowImpl createFlow(String name, FlowImpl parentFlow, Session session) {
    	Log log = getLog(session);
        try {
            KrnObject flow_obj = session.createObject(SC_FLOW, 0);
            FlowImpl flow_ = new FlowImpl(name, parentFlow);
            flow_.setFlowObj(flow_obj);
            flow_.setId(flow_obj.id);
            session.setString(flow_obj.id, SA_FLOW_NAME.id, 0, 0, false, name, 0);
            session.setObject(flow_obj.id, SA_PROCESS_INST.id, 0, parentFlow.getProcessInstance().getId(), 0, false);
            if (flow_.getStart() != null)
                session.setTime(flow_obj.id, SA_START_FLOW.id, 0, Funcs.convertTime(flow_.getStart()), 0);
            else
                session.deleteValue(flow_obj.id, SA_START_FLOW.id, new int[]{0}, 0, 0);
            if (flow_.getCurrent() != null)
                session.setTime(flow_obj.id, SA_CUR_FLOW.id, 0, Funcs.convertTime(flow_.getCurrent()), 0);
            else
                session.deleteValue(flow_obj.id, SA_CUR_FLOW.id, new int[]{0}, 0, 0);
            session.setObject(flow_obj.id, SA_ACTOR.id, 0, session.getUserSession().getUserId(), 0, false);
            session.setObject(flow_obj.id, SA_PARENT_FLOW.id, 0, parentFlow.getId(), 0, false);
            session.commitTransaction();
            return flow_;
        } catch (KrnException ex) {
            log.error(ex, ex);
        }
        return null;
    }

    public FlowImpl removeFlow(FlowImpl flow,boolean rollback,Session s,boolean isSessionCommit,List<KrnObject> subFlowsToRollback) throws KrnException {
        String userName = s.getUserSession().getLogUserName();
    	Log log = getLog(userName);
    	log.info("@REMOVE FLOW: " + flow.getId());
    	FlowImpl res=null;
    	long tpobjId=-1;
    	if(flow.getVariable()!=null && flow.getVariable().get("TIMERPROTOCOLOBJID")!=null)
    		tpobjId=(Long)flow.getVariable().get("TIMERPROTOCOLOBJID"); 
        //Необходимо убрать все ссылки на поток
        synchronized (flow) {
		    // Если есть подпроцесс, у которого данный является суперпроцессом, то ждем его завершения!
		    KrnObject[] subObjs = s.getObjectsByAttribute(
		    		SC_PROCESS.id, SA_SUPER_FLOW.id, 0, ComparisonOperations.CO_EQUALS, flow.getId(), 0);
		    if(!rollback && !flow.getEventType().equals(EventType.PROCESS_INSTANCE_END) && subObjs!=null && subObjs.length>0 && !flow.getEventType().equals(EventType.JOIN)) {
		        if (flow.getEnd() != null) {
	                s.setTime(flow.getId(), SA_END_FLOW.id, 0, Funcs.convertTime(flow.getEnd()), 0);
		        }
		    }else{
			    if (!flow.isOpenTransaction() && flow.getVariable() != null && flow.getVariable().size() > 0)
			    	// Очищаем переменные
			        flow.getVariable().clear();
				// Удаляем процесс только если завершается его Root Flow
				ProcessInstance process = flow.getProcessInstance();
				if (flow.equals(process.getRootFlow())) {
			    	if(s.getObjectById(flow.getId(), 0)!=null)
			    		s.deleteValue(flow.getId(),SA_PROCESS_INST.id,new int[]{0},0, 0);
			    	if(s.getObjectById(flow.getProcessInstance().getId(), 0)!=null)
			    		s.deleteValue(flow.getProcessInstance().getId(),SA_ROOT_FLOW.id,new int[]{0},0, 0);
					s.deleteObject(new KrnObject(process.getId(),"0",SC_PROCESS.id),0);
				}
			
			    // Удаляем поток из детей родительского потока
			    Flow parentFlow = flow.getParent();
			    if (parentFlow != null) {
			    	parentFlow.removeChild(flow);
			        ObjectValue[] children_ = s.getObjectValues(new long[]{parentFlow.getId()}, SA_CHILDREN_FLOW.id, new long[0], 0);
			        if (children_.length > 0) {
			            for (ObjectValue child:children_) {
			                if (child.objectId==flow.getId()) {
			                    s.deleteValue(parentFlow.getId(),SA_CHILDREN_FLOW.id,new int[]{child.index},0,0);
			                    break;
			                }
			            }
			        }
			    }
			    
			    // Удаляем поток из детей
			    Collection<FlowImpl> chFlows = flow.getChildren();
			    int[] indexes = {0};
			    for (FlowImpl chFlow : chFlows) {
			    	long chFlowId= chFlow.getProcessInstance().getId();
			    	KrnObject chFlowObj=s.getObjectById(chFlowId, 0);
			    	if(chFlowObj!=null)
			    		s.deleteValue(chFlowId, SA_SUPER_FLOW.id, indexes, 0, 0);
			    }
			//Удалить суперпроцесс который уже завершился если в нем не должна комититься транзакция
			    FlowImpl superFlow=(FlowImpl)flow.getProcessInstance().getSuperProcessFlow();
			    if(superFlow!=null && superFlow.getEnd()!=null && isSessionCommit){
			    	res=superFlow;
			    }
			    if(rollback && subFlowsToRollback!=null && subObjs!=null && subObjs.length>0){
			    //подготовить подпроцессы для отката в той же транзакции что и суперпроцесс
			    	for(KrnObject subObj:subObjs){
			    		long isProcess_=s.getLongsSingular(subObj, SA_PROCESS, false);
			    		if(isProcess_==0){
				    		KrnObject subFlow=s.getObjectsSingular(subObj.id, SA_ROOT_FLOW.id, false);
				    		if(subFlow!=null)
				    			subFlowsToRollback.add(subFlow);
			    		}
			    	}
			    }
				// Удаляем поток
			    s.deleteObject(new KrnObject(flow.getId(),"0",SC_FLOW.id),0);
		    }
		    if(isSessionCommit) {// коммит произойдет после того как завершится родительский процесс
		    	s.commitTransaction();
		    	if(tpobjId>0)
		    		ServerTasks.updateProtocol(tpobjId,false,s);
		    }
        }
        if (flow.getProcessInstance().getProcessDefinition() != null) {
	        synchronized (processFlowMap) {
	            List<FlowImpl> flows=processFlowMap.get(flow.getProcessInstance().getProcessDefinition().getId());
	            if(flows!=null && flows.contains(flow))
	            	flows.remove(flow);
	        }
        }
        return res;

    }
    private void  checkFlowToRemove(long pdId,long roleId,Session s) {
		try {
			List<String> ress = s.getFlowsToRemove(pdId, roleId);
	    	for(String res:ress) {
	    		String[] rs=res.split(",");
	    		long fid=Long.parseLong(rs[0]);
	    		long tid=Long.parseLong(rs[1]);
	    		long pid=Long.parseLong(rs[1]);
				//Проверяем не запущен ли сейчас этот процесс
	    		String[] chs=ExecutionEngine.getActiveFlow(fid);
	    		if(chs.length>0) {
					log.warn("Процесс находится в обработке, удалить нельзя. pdId:"+pdId+";flowId:"+fid+";tid:"+tid+";roleId:"+roleId+";");
	    		}else {
					try {
						//Откатываем транзакцию
			    		if(tid>0)
			    			s.rollbackLongTransaction(tid);
			    		//Удаляем процесс
						s.deleteObject(new KrnObject(pid,"0",SC_PROCESS.id),0);
						// Удаляем поток
					    s.deleteObject(new KrnObject(fid,"0",SC_FLOW.id),0);
					    s.commitTransaction();
					} catch (Exception e) {
						try {
							s.rollbackTransaction();
						} catch (KrnException e1) {
							e1.printStackTrace();
						}
						log.warn("Не удалось удалить процесс упавший при старте pdId:"+pdId+";flowId:"+fid+";tid:"+tid+";roleId:"+roleId+";");
					}finally {
						log.info("Удален процесс упавший при старте pdId:"+pdId+";flowId:"+fid+";tid:"+tid+";roleId:"+roleId+";");
					}
	    		}
	    	}
		} catch (KrnException e2) {
			e2.printStackTrace();
		}
    }

    private void clientTaskReload(FlowImpl flow,long[] oldUserId){
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
        clientTaskReload(userIds,userIdsInf,flow);

    }
   public void clientTaskReload(Collection userIds,Collection userIdsInf,FlowImpl flow) {
            Collection<Long> users = new TreeSet<Long>();
            Collection<Long> usersInf = new TreeSet<Long>();
            for (Object userId : userIds) {
                users = orgComp.findActorIdsByGroup((Long) userId, users);
            }
            for (Object userId : userIdsInf) {
                usersInf = orgComp.findActorIdsByGroup((Long) userId, usersInf);
            }
	        if (users.size() > 0 || usersInf.size()>0)
	            com.cifs.or2.server.Session.clientTaskReload(users,usersInf,flow.getId());
    }
    
    public  String resendMessage(Activity act,Session session){
        String userName = session.getUserSession().getLogUserName();
    	Log log = getLog(userName);

    	String res="";
        ExecutionEngine engine = null;
		try {
			KrnObject fobj = session.getObjectById(act.flowId, 0);
        	if(fobj!=null)   	engine=loadFlow(fobj, session, null);
		} catch (KrnException e) {
			log.error(e, e);
		}
        if(engine!=null){
            FlowImpl flow=engine.getFlow();
            Vector<String> nodes=(Vector<String>)flow.getNodes();
            long boxId=-1;
            for(int i=nodes.size()-2;i>0;--i){
                Node node=(Node)flow.getProcessInstance().getProcessDefinition().getNode(nodes.get(i));
                if(node instanceof OutBoxStateImpl){
                	ASTStart boxExpression = ((OutBoxStateImpl)node).getBoxExpression();
                    Object box = null;
                    if (boxExpression != null) {
	                    try {
	                        box = WfUtils.getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
	                                                                 boxExpression, null, flow, node, EventType.BOX_EXCHANGE, session);
	                    } catch (WorkflowException e) {
	                        log.error(e, e);
	                    }
                    }
                    if (box instanceof String)
                        boxId = Long.valueOf((String) box);
                    else if (box instanceof Number)
                        boxId = ((Number) box).longValue();
                    else if (box instanceof KrnObject)
                        boxId = ((KrnObject) box).id;
                    flow.setBoxId(boxId > 0 ? boxId : 0);
                    break;
                }
            }
            Map var=null;
            try{
            	var=flow.getVariable(session,SA_FLOW_VAR.id);
            }catch(Exception e){
                log.error(e, e);
            }
            Element xml=(Element)var.get("XML");
            if(boxId>0 && xml!=null){
                try {
                        List<Element> objTags = XPath.selectNodes(xml,  "//*[local-name()='id_message']");
                        if(objTags.size()>0){
                            String item=objTags.get(0).getText();
                            if(item!=null && !item.equals(""))
                                return getMessageCache().resendMessage(item,boxId);
                        }
                } catch (Exception e) {
                    log.error(e, e);
               }
            }
            KrnObject msg=(KrnObject)var.get("MAIL");
            if(boxId>0 && msg!=null){
                try {
                       String item=""+msg.id;
                       if(item!=null && !item.equals(""))
                           return getMessageCache().resendMessage(item,boxId);
                } catch (Exception e) {
                    log.error(e, e);
               }
            }
        }
        return res;
    }

    public ExecutionEngine getEngine(FlowImpl flow) {
        return null/*execEngines.get(flow.getId())*/;
    }
    
    public ExecutionEngine getEngine(long flowId, Session s) {
        ExecutionEngine engine = null;
       	KrnObject fobj=null;
		try {
			fobj = s.getObjectById(flowId, 0);
		} catch (KrnException e) {
	    	Log log = getLog(s);
	    	log.error(e, e);
		}
       	if (fobj != null)
       		engine=loadFlow(fobj, s, null);

       	return engine;
    }
    
    public void putCorelId(FlowImpl flow) throws JDOMException {
        if(flow.getEventType()!=null && flow.getEventType().equals(EventType.BEFORE_CHECK_XML)){
            String userName = user.getLogUserName();
        	Log log = getLog(userName);
            Element xml=(Element)flow.getVariable().get("XML");
            if(xml!=null){
                List<Element> objTags = XPath.selectNodes(xml, "//*[local-name()='id_message']");
                List<Element> objTags1 = XPath.selectNodes(xml, "//*[local-name()='id_producer']");
                if(objTags.size()>0){
                    String item=objTags.get(0).getText();
                    String item1="";
                    if(objTags1.size()>0){
                        item1=objTags1.get(0).getText();
                    }
                    if(item!=null && !item.equals("")){
                        flow.setCorelId(item);
                        log.debug(flow.getId()+":"+item+":"+item1);
                    }
                }
            }
        }
    }
    public long getFlowIdCorelId(Element xml,Session session) throws KrnException {
        long flowId=-1;
        if(xml!=null){
        	Log log = getLog(session);
            String item=getInitId(xml);
            if(item!=null && !item.equals("")){
            	flowId=session.findFlowByCorelId(item);
            	log.debug("<<<<<<<<<<<<<<<flowId="+flowId+":"+item+">>>>>>>>>>>>>>>");
            }
        }
        return flowId;
    }
    public String getInitId(Element xml){
        String res="";
        if(xml!=null){
        	Log log = getLog(user.getLogUserName());
            try {
                List<Element> objTags = XPath.selectNodes(xml, "//*[local-name()='id_initiator']");
                if(objTags.size()>0){
                    String item=objTags.get(0).getText();
                    res = (item!=null?item:"");
                }
            } catch (JDOMException e) {
                log.error(e, e);
            }
        }
        return res;
    }
    public String getProdId(Element xml){
        String res="";
        if(xml!=null){
        	Log log = getLog(user.getLogUserName());
            try {
                List<Element> objTags = XPath.selectNodes(xml, "//ct:id_producer");
                if(objTags.size()>0){
                    String item=objTags.get(0).getText();
                    res = (item!=null?item:"");
                }
            } catch (JDOMException e) {
                log.error(e, e);
            }
        }
        return res;
    }

    public static boolean isLimitThreads(){
       return ExecutionEngine.getIdleThreadCount()>0;
    }
    public void pause(){
    	getMessageCache().transportStop();
         ExecutionEngine.pause();
    }
    public void resume(){
        ExecutionEngine.resume();
        getMessageCache().transportResume();
    }
    public void setFlowDebug(long flowId){
        /*ExecutionEngine engine =execEngines.get(flowId);
        if(engine!=null && !engine.isRunning()){
            FlowImpl flow=engine.getFlow();
            flow.setParam((flow.getParam()& Constants.ACT_DEBUG)!=Constants.ACT_DEBUG
                    ?flow.getParam()| Constants.ACT_DEBUG
                    :flow.getParam()^ Constants.ACT_DEBUG);
        }*/

    }
    public boolean reloadSincFlows(Session session){
        String userName = user.getLogUserName();
    	Log log = getLog(userName);
        Vector<FlowImpl> start_=ExecutionEngine.getReloadSincFlows();
        try{
            for(FlowImpl flow_:start_){
                flow_.setEventType(EventType.SYNC_BEFORE_START);
                startFlow(flow_,session);
            }
            return true;
        }catch(Exception ex){
            log.error(ex, ex);
        }
        return false;
    }
    
    public String getDsName() {
    	return dsName;
    }
    
    private MessageCash getMessageCache() {
    	return Session.getMessageCache(dsName);
    }
    
    public synchronized void  setMessageStatus(String initId,String prodId,String text,Session se){
        if(initId!=null && !initId.equals("")){
            String userName = se.getUserSession().getLogUserName();
        	Log log = getLog(userName);
            try{
            	long flowId=se.findFlowByCorelId(initId);
        		if(flowId>0){
            		se.setString(flowId,SA_STATUS_MSG.id,0,0,false,text,0);
            		se.commitTransaction();
	        		KrnObject[] actors_=se.getObjects(flowId, SA_ACTOR.id,new long[0],0);
	        		if(actors_!=null && actors_.length>0){
	        			long[] actors=Funcs.makeObjectIdArray(actors_);
	                    Collection<Long> users = orgComp.findActorIdsByGroup(actors[0], null);
	                    if (users.size()>0)
	                    	com.cifs.or2.server.Session.clientTaskReload(users, new ArrayList<Long>(),flowId);
	        		}
		        }
	            log.debug(">>>>>"+(flowId>0?flowId+":":"")+initId+"<<<<<<");
        	}catch(KrnException ex){
                log.error(ex, ex);
        	}
        }
    }

    public boolean sendMessage(Element msg, long boxId, long flowId) {
        boolean res = false;
        try {
            res = getMessageCache().dispose(1, boxId, flowId, msg, "");
        } catch (TransportException e) {
        	String userName = user.getLogUserName();
        	Log log = getLog(userName);
        	log.error(e, e);
        }
        return res;
    }

	public OrganisationComponent getOrgComp() {
		return orgComp;
	}
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
	public static void writeExprToFile(String expr){
        log.debug(expr);
	}
	
	public static void setMaxFlowCount(int maxFlowCount){
		SystemProperties.setProperty("maxFlowCount", String.valueOf(maxFlowCount));
	}
	public Map<Long,Long> getActiveFlows(){
		return ExecutionEngine.getActiveFlows();
	}
	public long getTimeActive(long flowId){
		return ExecutionEngine.getTimeActive(flowId);
	}
	
    private static Set<String> allowedProcUids;
    private static Set<String> deniedProcUids;

    public static boolean isProcUidAllowed(String procUid) {
		if (allowedProcUids == null) {
			allowedProcUids = new HashSet<String>();
			String s = System.getProperty("exec.procAllowed");
			if (s != null)
				for (String uid : s.split(","))
					allowedProcUids.add(uid);
			deniedProcUids = new HashSet<String>();
			s = System.getProperty("exec.procDenied");
			if (s != null)
				for (String uid : s.split(","))
					deniedProcUids.add(uid);
		}
		if (!allowedProcUids.isEmpty() && !allowedProcUids.contains(procUid))
			return false;
		if (!deniedProcUids.isEmpty() && deniedProcUids.contains(procUid))
			return false;
		return true;
	}
}
