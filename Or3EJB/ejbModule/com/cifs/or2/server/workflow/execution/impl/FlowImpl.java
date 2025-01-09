package com.cifs.or2.server.workflow.execution.impl;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.08.2004
 * Time: 16:21:01
 * To change this template use File | Settings | File Templates.
 */
import java.io.ByteArrayOutputStream;
import java.util.*;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.server.wf.WfUtils;
import kz.tamur.server.wf.WorkflowException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import static java.util.Collections.*;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.*;
import com.cifs.or2.server.workflow.definition.impl.ActivityStateImpl;
import com.cifs.or2.server.workflow.execution.*;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;

public class FlowImpl implements Flow {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + FlowImpl.class.getName());
	
  public FlowImpl() {}

  public FlowImpl(KrnObject flowObj, long[] actorId, ProcessInstanceImpl processInstance ) {
    this.flowObj=flowObj;
    this.id=flowObj.id;
    this.trId=processInstance.getTrId();
    this.name = "root";
    this.actorId = actorId;
    this.start = new KrnDate(System.currentTimeMillis());
    this.current = new Date(System.currentTimeMillis());
//    this.node = null;
    this.processInstance = processInstance;
    this.parent = null;
  }

  public FlowImpl(String name, FlowImpl parent) {
    this.name = name;
    this.start = new KrnDate(System.currentTimeMillis());
    this.current = new Date(System.currentTimeMillis());
    this.processInstance = parent.getProcessInstance();
    this.parent = parent;
  }

  public boolean isChanges(String attribut){
      if(changes.contains(attribut)){
          changes.remove(attribut);
          return true;
      }else return false;
  }
  public String getName() { return name; }
  public void setName( String  name ) { this.name = name; }
  public String getTitle(Long langId) { return title.get(langId); }
  public Map<Long, String> getTitle() { return title; }
  public void setTitle(Long langId, String  title ) {
      this.title.put(langId, title);
      changes.add("title");
  }
  public void setTitle(Map<Long, String> titles) {
	  if (titles == null)
		  this.title.clear();
	  else
		  title.putAll(titles);
      changes.add("title");
  }
  public String getProcessTitle(Long langId) { return processTitle.get(langId); }
  public Map<Long, String> getProcessTitle() { return processTitle; }
  public void setProcessTitle(Long langId, String  title ) {
      this.processTitle.put(langId, title);
      changes.add("processTitle");
  }
  public void setProcessTitle(Map<Long, String> titles) {
	  if (titles == null)
		  this.processTitle.clear();
	  else
		  processTitle.putAll(titles);
      changes.add("processTitle");
  }
  public long[] getActorId() { return actorId; }
  public void setActorId( long[] actorId ) {
      this.actorId = actorId;
      changes.add("actorId");
  }
  public long getActorFromId() { return actorFromId; }
  public void setActorFromId( long actorFromId ) { this.actorFromId = actorFromId; }
  public void setUi(KrnObject ui) {
      this.ui = ui;
      changes.add("ui");
      if(isOpenTransaction && ui!=null)
    	  isOpenTransaction=false;
  }
  public void setUiName(String uiName) {
      this.uiName = uiName;
      changes.add("uiName");
  }
  public void setCutObj(KrnObject[] cutObj) {
      this.cutObj = cutObj;
      changes.add("cutObj");
  }
  public String getTitleObj(Long langId) { return titleObj.get(langId); }
  public Map<Long, String> getTitleObj() { return titleObj; }
  public void setTitleObj(Long langId, String  title ) {
      this.titleObj.put(langId, title);
      changes.add("titleObj");
  }
  public void setTitleObj(Map<Long, String> titles) {
	  if (titles == null)
		  this.titleObj.clear();
	  else
		  titleObj.putAll(titles);
      changes.add("titleObj");
  }
 public KrnObject getUi() { return ui;}
 public String getUiName() { return uiName;}
  public KrnObject[] getCutObj() { return cutObj; }
  public KrnDate getStart() { return start; }
  public void setStart( KrnDate start ) { this.start = start; }
  public Date getCurrent() { return current; }
  public void setCurrent( Date current ) {
      this.current = current;
      changes.add("current");
  }
  public KrnDate getControl() { return control; }
  public void setControl( KrnDate control ) { 
	  this.control = control; 
      changes.add("control");
	  }
  public Date getEnd() { return end; }
  public void setEnd( Date end ) { this.end = end; }
  public Boolean getParentReactivation() { return parentReactivation; }
  public void setParentReactivation( Boolean parentReactivation ) {
      this.parentReactivation = parentReactivation;
      changes.add("parentReactivation");
  }
  public Node getNode() {
      if (this.nodes != null && this.nodes.size()>0)
    	  try{
    		  return this.getProcessInstance().getProcessDefinition().getNode(this.nodes.lastElement());
    	  }catch(Exception e){
    		  log.error("!!!!!!!!!!" + this.id);
              log.error(e, e);
    	  }
      return null;
  }

  public void setNode(Node node) {
      if (node != null && (nodes.size()==0 || !node.getId().equals(this.nodes.lastElement())))  {
          synchronized (nodes) {
        	  if(nodes.size()>50){
        		  nodes.removeElementAt(25);
        	  }
              nodes.add(node.getId());
          }
          changes.add("nodes");
      }
  }

  public void setAllNodes(String[] sa_nodes) {
      if (sa_nodes != null)  {
          synchronized (nodes) {
              nodes.clear();
              for (String node : sa_nodes)
                  nodes.add(node);
          }
          changes.add("nodes");
      }
  }
  
  public Collection<String> getNodes() {
      return nodes;
  }
/*public Node getNode() {
      if(this.node!=null)
           return this.processInstance.getProcessDefinition().getNode(this.node.getId());
      return this.node;
  }
  public void setNode(Node node) {
      if (node != null && this.node!= node) {
          getNodes().add(node);
          changes.add("nodes");
      }
      this.node = node;
  }
  public Collection<Node> getNodes() {  return nodes;  }
  */
  public ProcessInstance getProcessInstance() { return processInstance; }
  public void setProcessInstance( ProcessInstance processInstance ) {
      this.processInstance = processInstance;
      changes.add("processInstance");
  }
  public Collection getAttributeInstances() { return attributeInstances; }
  public void setAttributeInstances( Collection attributeInstances ) { this.attributeInstances = attributeInstances; }
  public Flow getParent() { return parent; }
  public void setParent( Flow parent ) {
      this.parent = parent;
      changes.add("parent");
  }
  public Collection<FlowImpl> getChildren() { return children; }
    public synchronized void addChild(FlowImpl child) {
        children.add(child);
        changes.add("children");
    }

    public synchronized void removeChild(Flow child) {
        children.remove(child);
        changes.add("children");
    }

    public void addChildren(Collection<FlowImpl> childs) {
        children.addAll(childs);
        changes.add("children");
    }

    public Map<String,Object> getVariable(){
      return variable;
    }

    public Map<String,Object> getVariable(Session session,long attrId) throws WorkflowException{
	  if(variable.size()==0){
	    	try{
	            byte[] buf = session.getBlob(id, attrId, 0, 0, 0);
	            if (buf.length > 0) {
	            	Map<String,Object> res= WfUtils.loadFromXml(buf, session);
	            	variable.putAll(res);
	            }
	    	}catch(Exception e){
	            throw new WorkflowException("Ошибка при считывании переменных потока", 0, e);
	    	}
	  }
    return variable;
  }
  
  public void setVariable(Map<String,Object> vars) {
	    this.variable = vars;
  }

  public ProcessInstance getSubProcessInstance() {
    return subProcessInstance;
  }

    public void setSubProcessInstance( ProcessInstance subProcessInstance ) {
    this.subProcessInstance = subProcessInstance;
        changes.add("subProcessInstance");
  }

    public synchronized EventType getEventType() {
        return event;
    }

    public void setEventType(EventType event) {
        this.event = event;
        changes.add("event");
    }


  public boolean isRootFlow() {
    return ( parent == null );
  }

  public boolean isActive() {
    return ( end == null );
  }

  public String toString() {
    return "flow[" + id + "|" + name + "]";
  }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getParam() { return param; }
    public void setParam(long param) {
        this.param = param;
        changes.add("param");
    }

    public boolean equals(Object obj) {
        if (obj instanceof FlowImpl) {
            FlowImpl flow = (FlowImpl)obj;
            return id == flow.id;
        }
        return false;
    }

    public Vector<Long> getLockObj() { return lockObj;}
    public void setLockObj(long lockObj,int index){
        if(index==-1){
           this.lockObj.add(lockObj);
        }else if(index>this.lockObj.size()-1){
            for(int i=0;i<index-this.lockObj.size();++i){
                this.lockObj.add((long) -1);
            }
            this.lockObj.add(lockObj);
        } else this.lockObj.set(index,lockObj);
    }
    public void clearLockObj(Long lockObj){
        if(lockObj==null) this.lockObj.clear();
        else this.lockObj.remove(lockObj);
    }

    public Collection getFlowse(Collection<FlowImpl> col){
        if(col==null) col= new ArrayList<FlowImpl>();
        if(children!=null){
            col.addAll(children);
            for (FlowImpl aChildren : children) {
                aChildren.getFlowse(col);
            }
        }
        return col;
    }

     public void setParamObj(Map<Long, String> paramObj) {
		 this.paramObj.clear();
    	 if (paramObj != null)
    		 this.paramObj.putAll(paramObj);
    }

    public Map<Long, String> getParamObj() {
        return paramObj;
    }

    public String getParamObj(Long langId) {
        return paramObj.get(langId);
    }

    public long getBoxId() {
        return boxId;
    }

    public void setBoxId(long boxId) {
        this.boxId = boxId;
        changes.add("boxId");
    }

    public KrnObject getUser() {
        return user;
    }

    public void setUser(KrnObject user) {
        this.user = user;
        changes.add("user");
    }

    public String getSyncNode() {
        return syncNode;
    }

    public void setSyncNode(String nodeId) {
        syncNode = nodeId;
        changes.add("syncNode");
    }
    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }
    public boolean isRollback() {
        return isRollback;
    }
    public void setRollback(boolean rollback) {
        isRollback = rollback;
    }

    public String getTransitionTo() {
        return transitionToId;
    }

    public void setTransitionTo(String transitionToId) {
        this.transitionToId = transitionToId;
        changes.add("transitionToId");
    }


    public String getCorelId() {
        return corelId;
    }

    public void setCorelId(String corelId) {
        this.corelId = corelId;
        changes.add("corelId");
    }
    
    public String getComputer() {
    	return computer;
    }

    public void setComputer(String computer) {
    	this.computer = computer;
    }

    public String getIp() {
    	return ip;
    }

    public void setIp(String ip) {
    	this.ip = ip;
    }
    public long getTrId() {
        return trId;
    }
  private long id=-1;
  private long trId=-1;
  private KrnObject flowObj=null;
  private long param=0;
  private transient String  name = "";
  private Map<Long, String>  title = new HashMap<Long, String>();
  private Map<Long, String>  processTitle = new HashMap<Long, String>();
  private String  corelId = "";
  private Map<Long, String>  titleObj = new HashMap<Long, String>();
  private Map<Long, String>  paramObj = new HashMap<Long, String>();
  private String syncNode="";
  private String statusMsg="";
  private boolean isServer=false;
  private boolean isRollback=false;
  private boolean isOpenTransaction=false;
  private Vector<Long> inspectors=new Vector<Long>();
  private long[] actorId =new long[0];
  private long boxId = 0;
  private long actorFromId = 0;
  private KrnObject user ;
  private KrnObject ui ;
  private String uiName="" ;
  private String uiType="" ;
  private String taskColor="" ;
  private KrnObject[] cutObj ;
  private KrnObject infUi = null;
  private String uiTypeInf="" ;
  private KrnObject[] cutInfObj ;
  private Vector<Long> lockObj = new  Vector<Long>();
  private KrnDate start=null;
  private Date current=null;
  private KrnDate control=null;
  private Date end=null;
  private Boolean parentReactivation = Boolean.FALSE;
//  private transient Node node = null;
  private EventType event= null;
  private Vector<String> nodes = new  Vector<String>();
  private transient ProcessInstance processInstance = null;
  private transient ProcessInstance subProcessInstance = null;
  private Collection attributeInstances = null;
  private Map<String,Object> variable = synchronizedMap(new HashMap<String,Object>());
  private Flow parent = null;
  private Collection<FlowImpl> children = new Vector<FlowImpl>();
  private String transitionToId;
  private Set<String> changes = synchronizedSet(new TreeSet<String>());
  private String ip;
  private String computer;
  private FlowImpl errorFlow;
  private ActivityStateImpl errorActivity;

    public KrnObject getInfUi() {
        return infUi;
    }

    public void setInfUi(KrnObject infUi) {
        this.infUi = infUi;
    }

    public KrnObject[] getCutInfObj() {
        return cutInfObj;
    }

    public void setCutInfObj(KrnObject[] cutInfObj) {
        this.cutInfObj = cutInfObj;
    }

    public Vector<Long> getInspectors() {
        return new Vector<Long>(inspectors);
    }

    public void setInspectors(Vector<Long> inspectors) {
        this.inspectors = new Vector<Long>(inspectors);
        changes.add("inspector");
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public String getTaskColor() {
        return taskColor;
    }

    public void setTaskColor(String taskColor) {
        this.taskColor = taskColor;
    }
    public String getUiTypeInf() {
        return uiTypeInf;
    }

    public void setUiTypeInf(String uiTypeInf) {
        this.uiTypeInf = uiTypeInf;
    }

    public KrnObject getFlowObj() {
        return flowObj;
    }
    public void setFlowObj(KrnObject obj) {
        this.flowObj = obj;
    }
    public String getStatusMsg() {
        return this.statusMsg;
    }
    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
        changes.add("statusMsg");
    }
    public void setArticle(Element root,Session session) throws WorkflowException {
    	try{
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
    		XMLOutputter out = new XMLOutputter();
    		out.getFormat().setEncoding("UTF-8");
    		out.output(root, os);
    		os.close();
    		byte[] buf = os.toByteArray();
    		KrnClass flowCls = session.getClassByName("Flow");
    		KrnAttribute articleAttr = session.getAttributeByName(flowCls, "article");
    		session.setBlob(getId(), articleAttr.id, 0, buf, 0, 0);
    	}catch(Exception e){
            throw new WorkflowException("Ошибка при сохранении переменных отчета", 0, e);
    	}
    }
    public void setArticle(byte[] buf,Session session) throws WorkflowException {
    	try{
    		KrnClass flowCls = session.getClassByName("Flow");
    		KrnAttribute articleAttr = session.getAttributeByName(flowCls, "article");
    		session.setBlob(getId(), articleAttr.id, 0, buf, 0, 0);
    	}catch(Exception e){
            throw new WorkflowException("Ошибка при сохранении переменных отчета", 0, e);
    	}
    }
    public void setArticleLang(KrnObject lang,Session session) throws WorkflowException {
    	try{
    		KrnClass flowCls = session.getClassByName("Flow");
    		KrnAttribute alangAttr = session.getAttributeByName(flowCls, "article_lang");
    		session.setObject(getId(),alangAttr.id, 0, lang.id, 0,false);
    	}catch(Exception e){
            throw new WorkflowException("Ошибка при сохранении переменных отчета", 0, e);
    	}
  	
    }

    public Flow getRoot() {
  	  Flow root = this;
  	  while (root.getProcessInstance().getSuperProcessFlow() != null)
  		  root = root.getProcessInstance().getSuperProcessFlow();
  	  return root;
    }

	public boolean isOpenTransaction() {
		return isOpenTransaction;
	}

	public void setOpenTransaction(boolean isOpenTransaction) {
		this.isOpenTransaction = isOpenTransaction;
	}

	public FlowImpl getErrorFlow() {
		return errorFlow;
	}

	public void setErrorFlow(FlowImpl errorFlow) {
		this.errorFlow = errorFlow;
	}

	public ActivityStateImpl getErrorActivity() {
		return errorActivity;
	}

	public void setErrorActivity(ActivityStateImpl errorActivity) {
		this.errorActivity = errorActivity;
	}
}
