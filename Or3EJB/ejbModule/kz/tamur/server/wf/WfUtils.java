package kz.tamur.server.wf;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.workflow.execution.impl.FlowImpl;
import com.cifs.or2.server.workflow.execution.impl.ProcessInstanceImpl;
import com.cifs.or2.server.workflow.definition.ActivityState;
import com.cifs.or2.server.workflow.definition.EventType;
import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.impl.*;
import com.cifs.or2.server.workflow.organisation.OrganisationComponent;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;

import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.lang.reflect.InvocationTargetException;

import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.apache.commons.logging.Log;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.OrException;
import kz.tamur.lang.EvalException;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.12.2005
 * Time: 10:27:56
 * To change this template use File | Settings | File Templates.
 */
public class WfUtils {
    private static ThreadLocalDateFormat dateFormat = new ThreadLocalDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    
    public static Map<String,Object> loadFromXml(byte[] data, Session s)
            throws IOException, JDOMException{
        HashMap<String,Object> res = new HashMap<String,Object>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(Funcs.normalizeInput(data, "UTF-8")), "UTF-8");
        List vars = doc.getRootElement().getChildren();
        for (Object var1 : vars) {
            Element var = (Element) var1;
            String type = var.getAttributeValue("type");
            String name = var.getAttributeValue("name");
            Object obj = loadFromEvent(var, type, builder, s);
            res.put(name, obj);
        }
        return res;
    }
    private static Object loadFromEvent(Element var,String type,SAXBuilder builder,Session s){
        Object obj=null;
        try{
            if ("number".equals(type)) {
                obj= new Double(var.getText());
			}else if ("integer".equals(type)) {
					obj= new Integer(var.getText());
			}else if ("long".equals(type)) {
					obj= new Long(var.getText());
			}else if ("double".equals(type)) {
					obj= new Double(var.getText());
            } else if ("string".equals(type)) {
                obj= var.getText();
            } else if ("boolean".equals(type)) {
                obj= Boolean.valueOf(var.getText());
            } else if ("date".equals(type)) {
                try {
                    obj = new KrnDate(dateFormat.parse(var.getText()).getTime());
                } catch (ParseException e) {
                	Log log = getLog(s);
                    log.error(e, e);
                }
            } else if ("object".equals(type)) {
            	String[] objAttrs=var.getText().split(";");
            	if(objAttrs.length>1){
            		obj=new KrnObject(Long.parseLong(objAttrs[0]),objAttrs[1],Long.parseLong(objAttrs[2]));
            	}else
                obj = s.getObjectById(Long.parseLong(var.getText()),-1);
            } else if ("class".equals(type)) {
                obj = s.getClassById(Integer.parseInt(var.getText()));
            } else if ("list".equals(type)) {
                List ls=var.getChildren("value");
                obj=new ArrayList();
                for (Object l : ls) {
                    Element e = (Element) l;
                    String type_ = e.getAttributeValue("type");
                    Object obj_ = loadFromEvent(e, type_, builder, s);
                    ((List) obj).add(obj_);
                }
            } else if ("set".equals(type) || "sortedset".equals(type)) {
                List ls=var.getChildren("value");
                obj = ("set".equals(type)) ? new HashSet() : new TreeSet<Object>();
                for (Object l : ls) {
                    Element e = (Element) l;
                    String type_ = e.getAttributeValue("type");
                    Object obj_ = loadFromEvent(e, type_, builder, s);
                    ((Set) obj).add(obj_);
                }
            } else if ("map".equals(type) || "sortedmap".equals(type)) {
                List ls=var.getChildren("pair");
                obj = ("map".equals(type)) ? new HashMap() : new TreeMap<Object, Object>();
                for (Object l : ls) {
                    Element e = (Element) l;
                    Element key = e.getChild("key");
                    Element value = e.getChild("value");
                    String type_key = key.getAttributeValue("type");
                    String type_value = value.getAttributeValue("type");
                    Object obj_key = loadFromEvent(key, type_key, builder, s);
                    Object obj_value = loadFromEvent(value, type_value, builder, s);
                    ((Map) obj).put(obj_key, obj_value);
                }
            } else if ("element".equals(type)) {
                Document xml = builder.build(new StringReader(var.getText()));
                obj= xml.getRootElement();
            } else if ("namespace".equals(type)) {
                String prefix = var.getAttributeValue("prefix");
                String uri = var.getAttributeValue("uri");
                obj=  Namespace.getNamespace(prefix, uri);
            }
        }catch(Exception ex){
            return null;
        }
        return obj;
    }
    public static byte[] saveToXml(Map vars, Element root, Element event) throws IOException {

        synchronized (vars) {
            if (root == null)
                root = new Element("vars");
            if (event == null)
                event = root;
            for (Object o : vars.keySet()) {
                String name = (String) o;
                Object value = vars.get(name);
                saveToEvent(event, "var", name, value);
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
        os.close();
        return os.toByteArray();
    }
    private static void saveToEvent(Element event,String var,String name,Object value) throws IOException {
        Element e = new Element(var);
        if(var.equals("var"))
            e.setAttribute("name", name);
        if (value instanceof Integer) {
            e.setAttribute("type", "integer");
            e.addContent("" +  value);
		}else if (value instanceof Long) {
				e.setAttribute("type", "long");
				e.addContent("" +  value);
		}else if (value instanceof Double) {
				e.setAttribute("type", "double");
				e.addContent("" +  value);
        } else if (value instanceof String) {
            e.setAttribute("type", "string");
            e.addContent((String) value);
        } else if (value instanceof Boolean) {
            e.setAttribute("type", "boolean");
            e.addContent(value.toString());
        } else if (value instanceof Date) {
            e.setAttribute("type", "date");
            e.addContent(dateFormat.format((Date) value));
        } else if (value instanceof KrnObject) {
            e.setAttribute("type", "object");
            e.addContent("" + ((KrnObject) value).id + ";" + ((KrnObject) value).uid + ";" + ((KrnObject) value).classId);
        } else if (value instanceof KrnClass) {
            e.setAttribute("type", "class");
            e.addContent("" + ((KrnClass) value).id);
        } else if (value instanceof Element) {
            e.setAttribute("type", "element");
            CharArrayWriter w = new CharArrayWriter();
            XMLOutputter opr = new XMLOutputter();
            opr.getFormat().setEncoding("UTF-8");
            opr.output((Element) value, w);
            w.close();
            e.addContent(new CDATA(w.toString()));
        } else if (value instanceof Namespace) {
            Namespace ns = (Namespace)value;
            e.setAttribute("type", "namespace");
            e.setAttribute("prefix", ns.getPrefix());
            e.setAttribute("uri", ns.getURI());
        } else if (value instanceof List) {
            e.setAttribute("type", "list");
            if(((List)value).size()>0)
                for (Object o : ((List) value)) {
                    saveToEvent(e, "value", "value", o);
                }
        } else if (value instanceof Set) {
            e.setAttribute("type", (value instanceof SortedSet) ? "sortedset" : "set");
            if(((Set)value).size()>0)
                for (Object o : ((Set) value)) {
                    saveToEvent(e, "value", "value", o);
                }
        } else if (value instanceof Map) {
            e.setAttribute("type", (value instanceof SortedMap) ? "sortedmap" : "map");
            if(((Map)value).size()>0)
                for (Object o : ((Map) value).keySet()) {
                    Element pair = new Element("pair");
                    saveToEvent(pair, "key", "key", o);
                    saveToEvent(pair, "value", "value", ((Map) value).get(o));
                    e.addContent(pair);
                }
        }
        event.addContent(e);
    }
//    public static Object getResolvExpression(String process,String expression,Map var,FlowImpl flow,EventType event,Session session){
//        return getResolvExpression2( process, expression, var, flow, event,session, true);
//    }

  public static Object getResolvExpression (ProcessDefinitionImpl process,
                                              ASTStart expression,
                                              Map<String,Object> var,
                                              FlowImpl flow,
                                              Node node,
                                              EventType event,
                                              Session session) throws WorkflowException {
    	Log log = getLog(session);
        Object res = null;
        if(session.isContextEmpty())
        	session.setContext(new Context(new long[0], 0, 0));
        Context ctx = session.getContext();
        if(session.getUserSession().getIfcLang()!=null)
        	ctx.langId = session.getUserSession().getIfcLang().id;
		ctx.pdId=process.getId();
		ctx.beforeCommitExpr=process.getBeforeCommitExpr();
		ctx.afterCommitExpr=process.getAfterCommitExpr();
		try{
          Map<String,Object> vc = new HashMap<String,Object>();
          ProcessDefinitionImpl pd=process;
          if(flow!=null){
              ctx.flowId=flow.getId();
              ctx.trId = (EventType.PROCESS_INSTANCE_CANCEL.equals(event)?0:flow.getProcessInstance().getTrId());
              //ctx.isOpenTranpaction=flow.isOpenTransaction();
              vc.putAll(flow.getVariable());
              vc.put("FLOW",flow.getFlowObj());
              vc.put("PROCESSDEF", session.getObjectByIdQuit(flow.getProcessInstance().getProcessDefinition().getId(), 0));
           	  ctx.isNotWriteSysLog = "1".equals(vc.get("WITHOUTSYSLOG"));
          }
          if(pd!=null){
              vc.put("PROCESSDEFID",process.getId());
          }
          if(var!=null){
              //считываю все переменные
              for (Object o : var.keySet()) {
                  String name_v = (String) o;
                  vc.put(name_v, var.get(name_v));
              }
          }
            boolean res_v = false;
            if (expression != null) {
                try{
                	SrvOrLang orl = SrvOrLang.class.cast(session.getOrLang());
                	Stack<String> stack = new Stack<String>();
                	stack.add("Поток: " + (flow != null ? flow.getId() : "?")
                            + " Процесс: '" + process.getName()  + "'"
                            + (node != null ? " Узел: '" + node.getName() + "'": "")
                            + (event != null ? ("event".equals(event.toType()) ? " Событие: '" : " Свойство: '")
                            		+ event.toStringRu() + "'" : ""));
                    res_v = orl.evaluate(expression, vc, pd, false, stack, null);
                } catch (Throwable e) {
                    int codeError=0;
                    if(e instanceof OrException){
                        codeError=((OrException)e).getErrorCode();
                    }else if(e instanceof KrnException){
                        codeError=((KrnException)e).code;
                    }else if(e instanceof EvalException && e.getCause() instanceof InvocationTargetException){
                        InvocationTargetException ite= (InvocationTargetException)e.getCause();
                        if(ite.getTargetException() instanceof KrnException){
                        codeError=((KrnException)ite.getTargetException()).code;
                        }
                    }
                    log.error("codeError="+codeError);
                    
                    String msg = (e instanceof EvalException) ? ((EvalException)e).getFullMessage() : e.getMessage();
                    session.writeLogRecord(SystemEvent.ERROR_WF_EXPR, process.getName(), "Поток: " + (flow != null ? flow.getId() : "?"), msg,(flow!=null && flow.getFlowObj()!=null)?flow.getFlowObj().classId:-1,-1);

                    log.error(msg);
                    log.error(e, e);
                    //if (EventType.PROCESS_INSTANCE_CANCEL.equals(event)) {
                        //log.error(e, e);
                    //}else {
                        //log.error(e, e);
                        throw new WorkflowException(msg,codeError,e.getCause());
                    //}
                }
            }

          if(!res_v){
              res= 0;
              log.info("ОШИБКА!: При выполнении формулы в службе: '"
                      +process.getName()+"'"
                      +(node!=null?" для узла: '"+node.getName()+"'":"")
                      +(event!=null?" и "+("event".equals(event.toType())?"события":"свойства")+": '"
                      +event.toStringRu()+"'":""));
          }else{
              res = vc.remove("RETURN");
              if(flow!=null){
            	  if (session.getDriver().getDatabase().hasArticleAttr) {
                      Object article = vc.get("ARTICLE");
                      if (article != null) {
                    	  if (article instanceof Element)
                    		  flow.setArticle((Element)article,session);
                    	  else
                    		  flow.setArticle((byte[])article,session);
                    	  vc.remove("ARTICLE");
                      }
	            	  KrnObject articleLang = (KrnObject)vc.get("ARTICLE_LANG");
	            	  if (article != null && articleLang != null) {
	            		  flow.setArticleLang(articleLang,session);
	            		  vc.remove("ARTICLE_LANG");
	            	  }
	            	  String articleName = (String)vc.get("REPORT_FILE_NAME");
	            	  if (article != null && articleName != null) {
	            		  flow.setUiName(articleName);
	            	  }
            	  }
                  if (vc.get("OPEN_TRANSACTION") != null) {//Устанавливаем флаг,далее процесс выполняется без комита базовой транзакции
                	  flow.setOpenTransaction(true);	   //до моментов вынужденного комита
                	  vc.remove("OPEN_TRANSACTION");
      				  log.info("OPEN_TRANSACTION: set;tr_id:"+flow.getProcessInstance().getTrId()
      						  +";fowId:"+flow.getId()+";CONNECTION_ID:"+session.getConnectionId());
                 }
                  flow.getVariable().putAll(vc);
              } else if(var!=null){
                  synchronized(var) {
                      for (String name : vc.keySet()) {
                          var.put(name, vc.get(name));
                      }
                  }
              }
          }
        } catch (Exception ex) {
            String error="ОШИБКА!: (flow: "+(flow!=null?flow.getId():0)+") "
                        +"При выполнении формулы в службе: '"+process.getName()+"'"
                        +(node!=null?" для узла: '"+node.getName()+"'":"")
                        +(event!=null?" и "+("event".equals(event.toType())?"события":"свойства")+": '"
                        +(NodeEventType.forEventType(event)==null?event.toStringRu():
                            NodeEventType.forEventType(event).getTitle())+"'":"");
            if(flow!=null){
            	flow.getVariable().put("ERRMSG",error);
            }else if(var!=null){
                var.put("ERRMSG",error);
            }
            int codeError=0;
            if(ex instanceof OrException){
                codeError=((OrException)ex).getErrorCode();
            }else if(ex instanceof KrnException){
                codeError=((KrnException)ex).code;
            }
            if (EventType.PROCESS_INSTANCE_CANCEL.equals(event)) {
                log.error(error);
                log.error(ex, ex);
            } else {
                log.error(error);
                log.error(ex, ex);
                session.writeLogRecord(SystemEvent.ERROR_WF_EXPR, process.getName(), "Поток: " + (flow != null ? flow.getId() : "?"), ex.getMessage(),(flow!=null && flow.getFlowObj()!=null)?flow.getFlowObj().classId:-1,-1);
                throw new WorkflowException(ex.getMessage(), codeError, ex.getCause());
            }
        }
        return res;
    }
  
	public static void setFlowPropertiesUI(FlowImpl flow, ActivityState activity, Session session, boolean isCommit)
			throws WorkflowException, KrnException {
		// Установка интерфейса
		if (isCommit)
			session.commitTransaction();
		if ((flow.getParam() & Constants.ACT_CANCEL) == Constants.ACT_CANCEL)
			flow.setParam(flow.getParam() ^ Constants.ACT_CANCEL);
		if ((flow.getParam() & Constants.ACT_WINDOW) == Constants.ACT_WINDOW)
			flow.setParam(flow.getParam() ^ Constants.ACT_WINDOW);
		if ((flow.getParam() & Constants.ACT_DIALOG) == Constants.ACT_DIALOG)
			flow.setParam(flow.getParam() ^ Constants.ACT_DIALOG);
		if ((flow.getParam() & Constants.ACT_AUTO) == Constants.ACT_AUTO)
			flow.setParam(flow.getParam() ^ Constants.ACT_AUTO);
		if ((flow.getParam() & Constants.ACT_IN_BOX) == Constants.ACT_IN_BOX)
			flow.setParam(flow.getParam() ^ Constants.ACT_IN_BOX);
		if ((flow.getParam() & Constants.ACT_OUT_BOX) == Constants.ACT_OUT_BOX)
			flow.setParam(flow.getParam() ^ Constants.ACT_OUT_BOX);
		if ((flow.getParam() & Constants.ACT_ARTICLE) == Constants.ACT_ARTICLE)
			flow.setParam(flow.getParam() ^ Constants.ACT_ARTICLE);
		if ((flow.getParam() & Constants.ACT_FASTREPORT) == Constants.ACT_FASTREPORT)
			flow.setParam(flow.getParam() ^ Constants.ACT_FASTREPORT);
		if (activity instanceof InBoxStateImpl)
			flow.setParam(flow.getParam() | Constants.ACT_IN_BOX);
		else if (activity instanceof OutBoxStateImpl)
			flow.setParam(flow.getParam() | Constants.ACT_OUT_BOX);
		
		if (activity instanceof ActivityStateImpl) {
			ActivityStateImpl activity_ = (ActivityStateImpl) activity;
			ASTStart uiExpression = activity_.getUiExpression();
			String uiExpressionKrn = activity_.getUiExpressionKrn();
			if (Constants.CHOPPER_YES.equals(activity_.getChopperEnable()))
				flow.setParam(flow.getParam() | Constants.ACT_CANCEL);
			if (Constants.ACT_WINDOW_STRING.equals(activity_.getUiType()))
				flow.setParam(flow.getParam() | Constants.ACT_WINDOW);
			else if (Constants.ACT_DIALOG_STRING.equals(activity_.getUiType()))
				flow.setParam(flow.getParam() | Constants.ACT_DIALOG);
			else if (Constants.ACT_AUTO_STRING.equals(activity_.getUiType()))
				flow.setParam(flow.getParam() | Constants.ACT_AUTO);
			else if (Constants.ACT_ARTICLE_STRING.equals(activity_.getUiType()))
				flow.setParam(flow.getParam() | Constants.ACT_ARTICLE);
			else if (Constants.ACT_FASTREPORT_STRING.equals(activity_.getUiType()))
				flow.setParam(flow.getParam() | Constants.ACT_FASTREPORT);
			if (activity_.isAutoNext())
				flow.setParam(flow.getParam() | Constants.ACT_AUTO_NEXT);
			if (activity_.isReportRequire())
				flow.setParam(flow.getParam() | Constants.ACT_REPORT_REQUIRE);
			if (EventType.BEFORE_PERFORM_OF_ACTIVITY.equals(flow.getEventType())) {
				if (uiExpressionKrn != null && !uiExpressionKrn.equals("")) {
					flow.setUi(session.getObjectByUid(uiExpressionKrn, 0));
					flow.setUiType(activity_.getUiType() != null ? activity_.getUiType() : "");
				} else if (uiExpression != null) {
					Object ui = WfUtils.getResolvExpression(
							(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), uiExpression, null,
							flow, flow.getNode(), EventType.ACT_USER_INTERFACE, session);
					if (ui != null && ui instanceof KrnObject) {
						flow.setUi((KrnObject) ui);
						flow.setUiType(activity_.getUiType() != null ? activity_.getUiType() : "");
					}
				} else {
					flow.setUi(null);
					flow.setUiType(Constants.ACT_NO_UI);
				}
			} else {
				flow.setUi(null);
				flow.setUiType("");
			}
		} else {
			flow.setUi(null);
			flow.setUiType("");
		}
		ProcessDefinitionImpl processDef = (ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition();
		if (processDef.getTitleExpr() != null) {
			Map<Long, String> o_ = WfUtils.getResolvExpressionAllLangs((ProcessDefinitionImpl) processDef,
					processDef.getTitleExpr(), flow.getVariable(), flow, flow.getNode(), EventType.TITLE_EXPR, session);
			flow.setProcessTitle(o_);
		}
		
		// Установка обрабатываемого объекта
		ASTStart objExpression = ((ActivityStateImpl) activity).getObjExpression();
		if (objExpression != null) {
			Object cut_obj = getResolvExpression(
					(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), objExpression, null, flow,
					flow.getNode(), EventType.ACT_OBJ_EXPR, session);
			if (cut_obj != null && cut_obj instanceof List) {
				List<KrnObject> ol = (List<KrnObject>) cut_obj;
				flow.setCutObj(ol.toArray(new KrnObject[ol.size()]));
			} else if (cut_obj != null && cut_obj instanceof KrnObject[]) {
				flow.setCutObj((KrnObject[]) cut_obj);
			} else if (cut_obj != null && cut_obj instanceof KrnObject)
				flow.setCutObj(new KrnObject[] { ((KrnObject) cut_obj) });
			else {
				flow.setCutObj(null);
			}
		} else {
			flow.setCutObj(null);
		}
		if (isCommit)
			session.rollbackTransaction();
	}
	
    //установка параметров интерфейса
    public static void setFlowPropertiesAssignment(FlowImpl flow,ActivityState activity,Session session,boolean isCommit) throws WorkflowException, KrnException {
        KrnObject actor_krn=null;
        String actorId_ = null;
        long[] actorId=new long[0];
        ASTStart assignmentExpression;
        String assignmentKrn;
        
        if(isCommit)
            session.commitTransaction();
        // получение роли назначенной данному activity-state
        assignmentExpression = activity.getAssignment();
        assignmentKrn = activity.getAssignmentKrn();
        if(assignmentKrn!=null && !assignmentKrn.equals("")){
                      actor_krn=session.getObjectByUid(assignmentKrn,0);
        }
        if(actor_krn!=null){
               actorId=new long[]{actor_krn.id};
        }else if (assignmentExpression != null) {
            // get the assignment of the activity-state
            Object actor = getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                    assignmentExpression, null, flow,flow.getNode(), EventType.RESPONSIBLE, session);
            if (actor instanceof String){
                actorId_ = (String) actor;
                if("SERVER".equals(actorId_)){
                    actorId=new long[]{0};
                    flow.setServer(true);
                }else
                    actorId=new long[]{Long.valueOf(actorId_)};
            }else if (actor instanceof Number)
                actorId = new long[]{((Number) actor).intValue()};
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
        if(actorId.length==0 || actorId[0]!=0)
            flow.setServer(false); 
        flow.setActorId(actorId);
        if(isCommit)
            session.rollbackTransaction();

    }

	public static void setFlowProperties(FlowImpl flow, OrganisationComponent orgComp, Session session,
			boolean isCommit) throws WorkflowException, KrnException {
		Log log = getLog(session);
		if (flow.getNode() instanceof StartSyncState) {
			StartSyncState sync = (StartSyncState) flow.getNode();
			ASTStart titleExpr = sync.getTitle();
			if (titleExpr != null) {
				// получение заголовка
				Map<Long, String> title = getResolvExpressionAllLangs(
						(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), titleExpr, null, flow,
						flow.getNode(), EventType.TITLE_EXPR, session);
				flow.setTitle(title);
			} else
				flow.setTitle(null);
		} else if (flow.getNode() instanceof ActivityState) {
			ActivityState activity = (ActivityState) flow.getNode();
			setFlowPropertiesAssignment(flow, activity, session, isCommit);
			if (flow.isServer()) {
				// Установка заголовка
				ASTStart titleExpr = activity.getTitle();
				if (titleExpr == null)
					titleExpr = flow.getProcessInstance().getProcessDefinition().getTitle();
				if (titleExpr != null) {
					// получение титула для activity-state
					Map<Long, String> title = getResolvExpressionAllLangs(
							(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), titleExpr, null,
							flow, flow.getNode(), EventType.TITLE_EXPR, session);
					flow.setTitle(title);
				} else
					flow.setTitle(null);
				
				// Установка обрабатываемого объекта (для поиска процесса)
				if(activity instanceof ActivityStateImpl){
					ASTStart objExpression = ((ActivityStateImpl) activity).getObjExpression();
					if (objExpression != null) {
						Object cut_obj = getResolvExpression(
								(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), objExpression, null, flow,
								flow.getNode(), EventType.ACT_OBJ_EXPR, session);
						if (cut_obj != null && cut_obj instanceof List) {
							List<KrnObject> ol = (List<KrnObject>) cut_obj;
							flow.setCutObj(ol.toArray(new KrnObject[ol.size()]));
						} else if (cut_obj != null && cut_obj instanceof KrnObject[]) {
							flow.setCutObj((KrnObject[]) cut_obj);
						} else if (cut_obj != null && cut_obj instanceof KrnObject)
							flow.setCutObj(new KrnObject[] { ((KrnObject) cut_obj) });
						else {
							flow.setCutObj(null);
						}
					}
				}
			} else if (flow.getActorId().length > 1 || (flow.getActorId().length > 0 && flow.getActorId()[0] > 0)) {
				// Установка заголовка
				ASTStart titleExpr = activity.getTitle();
				// if (titleExpr != null && !titleExpr.equals("")){
				// получение титула для activity-state
				Map<Long, String> title = getResolvExpressionFlowAllLangs(
						(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), titleExpr, null, flow,
						flow.getNode(), EventType.TITLE_EXPR, session);
				flow.setTitle(title);
				// }else flow.setTitle(null);

				if (flow.getNode() instanceof ActivityStateImpl && !(flow.getNode() instanceof StartStateImpl)) {
					// Установка параметров
					ASTStart paramExpr = ((ActivityStateImpl) activity).getParamExpression();
					if (paramExpr != null) {
						// получение параметров для activity-state
						title = WfUtils.getResolvExpressionAllLangs(
								(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), paramExpr,
								null, flow, flow.getNode(), EventType.TITLE_EXPR, session);
						flow.setParamObj(title);
					}
					// Установка прерывателя процесса
					boolean isChopper = false;
					if (flow.getProcessInstance().getChopper() != 0) {
						long chopper = flow.getProcessInstance().getChopper();
						for (long aId : flow.getActorId()) {
							isChopper = orgComp.isUserActor(aId, chopper, session);
							if (isChopper)
								break;
						}
					}
					if (isChopper && ((ActivityStateImpl) activity).getChopperEnable() != null
							&& ((ActivityStateImpl) activity).getChopperEnable().equals(Constants.CHOPPER_YES)) {
						if ((flow.getParam() & Constants.ACT_CANCEL) != Constants.ACT_CANCEL)
							flow.setParam(flow.getParam() | Constants.ACT_CANCEL);
					} else if ((flow.getParam() & Constants.ACT_CANCEL) == Constants.ACT_CANCEL) {
						flow.setParam(flow.getParam() ^ Constants.ACT_CANCEL);
					}
					// установка параметров интерфейса
					setFlowPropertiesUI(flow, activity, session, isCommit);
					// Установка заголовка
					titleExpr = ((ActivityStateImpl) activity).getObjTitleExpression();
					if (titleExpr != null) {
						if (titleExpr == null)
							titleExpr = flow.getProcessInstance().getProcessDefinition().getTitle();
						if (titleExpr != null) {
							// получение титула для activity-state
							title = getResolvExpressionAllLangs(
									(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), titleExpr,
									null, flow, flow.getNode(), EventType.TITLE_EXPR, session);
							flow.setTitleObj(title);
						} else
							log.info("ОШИБКА!: Для действия '" + (activity).getName()
									+ "'не задан титул для отображения обрабатываемого объекта");

					} else {
						flow.setTitleObj(null);
					}
					setFlowDateProperties(flow,session);
				} else if (flow.getNode() instanceof InBoxStateImpl
						&& flow.getEventType().equals(EventType.BEFORE_CHECK_XML)) {
					// Установка заголовка
					titleExpr = ((InBoxStateImpl) activity).getObjTitleExpression();
					if (titleExpr == null)
						titleExpr = flow.getProcessInstance().getProcessDefinition().getTitle();
					if (titleExpr != null) {
						// получение титула для activity-state
						title = getResolvExpressionAllLangs(
								(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(), titleExpr,
								null, flow, flow.getNode(), EventType.TITLE_EXPR, session);
						flow.setTitleObj(title);
					}

					// Для наблюдателя
					ProcessDefinition processDef = flow.getProcessInstance().getProcessDefinition();
					ASTStart uiExpressionInf = processDef.getUiExpressionInf();
					if (uiExpressionInf != null) {
						Object ui_inf = WfUtils.getResolvExpression((ProcessDefinitionImpl) processDef, uiExpressionInf,
								flow.getVariable(), null, null, EventType.UI_INF, session);
						if (ui_inf != null && ui_inf instanceof KrnObject) {
							flow.setInfUi((KrnObject) ui_inf);
							flow.setUiTypeInf(processDef.getUiTypeInf() != null ? processDef.getUiTypeInf() : "");
						}
					}
					ASTStart objExpressionInf = processDef.getObjExpressionInf();
					if (objExpressionInf != null) {
						Object inf_obj = WfUtils.getResolvExpression((ProcessDefinitionImpl) processDef,
								objExpressionInf, flow.getVariable(), null, null, EventType.ACT_OBJ_EXPR, session);
						if (inf_obj != null && inf_obj instanceof List && ((List) inf_obj).size() > 0) {
							List<KrnObject> ol = (List<KrnObject>) inf_obj;
							flow.setCutInfObj(ol.toArray(new KrnObject[ol.size()]));
						} else if (inf_obj != null && inf_obj instanceof KrnObject[]) {
							flow.setCutInfObj((KrnObject[]) inf_obj);
						} else if (inf_obj != null && inf_obj instanceof KrnObject)
							flow.setCutInfObj(new KrnObject[] { ((KrnObject) inf_obj) });
					} else {
						flow.setCutInfObj(null);
					}

					//
					Date current_ = new Date(System.currentTimeMillis());
					if (((InBoxStateImpl) activity).getDateAlarmExpression() != null) {
						Object o_ = getResolvExpression(
								(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(),
								((InBoxStateImpl) activity).getDateAlarmExpression(), null, flow, flow.getNode(),
								EventType.ACT_DATE_ALARM, session);
						if (o_ instanceof KrnDate) {
							KrnDate dateAlarm = (KrnDate) o_;
							flow.setControl(dateAlarm);
							if (dateAlarm.before(current_)
									&& (flow.getParam() & Constants.ACT_ALARM) != Constants.ACT_ALARM) {
								flow.setParam(flow.getParam() | Constants.ACT_ALARM);
							} else if (dateAlarm.after(current_)
									&& (flow.getParam() & Constants.ACT_ALARM) == Constants.ACT_ALARM) {
								flow.setParam(flow.getParam() ^ Constants.ACT_ALARM);
							}
						}
					}
					if (((InBoxStateImpl) activity).getDateAlertExpression() != null) {
						Object o_ = getResolvExpression(
								(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(),
								((InBoxStateImpl) activity).getDateAlertExpression(), null, flow, flow.getNode(),
								EventType.ACT_DATE_ALERT, session);
						if (o_ != null && o_ instanceof Date) {
							Date dateAlert = (Date) o_;
							if (dateAlert.before(current_)
									&& (flow.getParam() & Constants.ACT_ALERT) != Constants.ACT_ALERT) {
								flow.setParam(flow.getParam() | Constants.ACT_ALERT);
							} else if (dateAlert.after(current_)
									&& (flow.getParam() & Constants.ACT_ALERT) == Constants.ACT_ALERT) {
								flow.setParam(flow.getParam() ^ Constants.ACT_ALERT);
							}
						}
					}
					flow.setUi(null);
					flow.setUiType("");
				} else {
					flow.setUi(null);
					flow.setUiType("");
				}
			}
			if (isCommit)
				session.rollbackTransaction();
		} else {
			flow.setUi(null);
			flow.setUiType("");
		}
	}
	public static void setFlowDateProperties(FlowImpl flow, Session session) throws WorkflowException, KrnException {
		ActivityState activity = (ActivityState) flow.getNode();
		Date current_ = new Date(System.currentTimeMillis());
		KrnDate dateAlarm=null;
		KrnDate dateAlert = null;
		if (((ActivityStateImpl) activity).getDateAlarm() != null) {
			Object o_ = getResolvExpression(
					(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(),
					((ActivityStateImpl) activity).getDateAlarm(), null, flow, flow.getNode(),
					EventType.ACT_DATE_ALARM, session);
			if (o_ instanceof KrnDate) {
				dateAlarm = (KrnDate) o_;
			}
		}
		if (((ActivityStateImpl) activity).getDateAlert() != null) {
			Object o_ = getResolvExpression(
					(ProcessDefinitionImpl) flow.getProcessInstance().getProcessDefinition(),
					((ActivityStateImpl) activity).getDateAlert(), null, flow, flow.getNode(),
					EventType.ACT_DATE_ALERT, session);
			if (o_ != null && o_ instanceof KrnDate) {
				dateAlert = (KrnDate) o_;
			}
		}
		if(dateAlarm!=null && dateAlert!=null){
			flow.setStatusMsg((dateAlarm.getTime()-dateAlert.getTime())+"");
			flow.setControl(dateAlert);
		}else if(dateAlarm!=null)
			flow.setControl(dateAlarm);
		if (dateAlarm!=null && dateAlarm.before(current_)
				&& (flow.getParam() & Constants.ACT_ALARM) != Constants.ACT_ALARM) {
			flow.setParam(flow.getParam() | Constants.ACT_ALARM);
		} else if (dateAlert!=null && dateAlert.before(current_)
				&& (flow.getParam() & Constants.ACT_ALERT) != Constants.ACT_ALERT) {
			flow.setParam(flow.getParam() | Constants.ACT_ALERT);
		}else if((dateAlarm==null || dateAlarm.after(current_)) 
				&& (flow.getParam() & Constants.ACT_ALARM) == Constants.ACT_ALARM){
			flow.setParam(flow.getParam() ^ Constants.ACT_ALARM);
		}else if((dateAlert==null || dateAlert.after(current_)) 
				&& (flow.getParam() & Constants.ACT_ALERT) == Constants.ACT_ALERT){
			flow.setParam(flow.getParam() ^ Constants.ACT_ALERT);
		}
	}
    
    public static void setProcessProperties(FlowImpl flow,Session session) throws WorkflowException, KrnException{
        //Для наблюдателя
    	ProcessInstanceImpl processInstance=(ProcessInstanceImpl)flow.getProcessInstance();
        ProcessDefinitionImpl processDef= (ProcessDefinitionImpl)processInstance.getProcessDefinition();
		if (processDef.getInspectors() != null) {
			Object inspectors = WfUtils.getResolvExpression((ProcessDefinitionImpl) processDef,
					processDef.getInspectors(), flow.getVariable(), null, null, EventType.INSPECTORS, session);
        	String observers="";
			if (inspectors instanceof List) {
				List<KrnObject> inspectorList = (List<KrnObject>) inspectors;
				if (inspectorList.size() > 0) {
					Vector<Long> inspectors_ = new Vector<Long>();
					for (KrnObject inspector : inspectorList) {
						if (inspector != null) {
							inspectors_.add(inspector.id);
			        		if(!"".equals(observers))
			        			observers += (";"+inspector.id);
			        		else
			        			observers += (""+inspector.id);
						}
					}
					flow.setInspectors(inspectors_);
				}
			}
			ASTStart uiExpressionInf = processDef.getUiExpressionInf();
			String uiExpressionInfKrn = processDef.getUiExpressionInfKrn();
			if (uiExpressionInfKrn != null && !uiExpressionInfKrn.equals("")) {
				flow.setInfUi(session.getObjectByUid(uiExpressionInfKrn, 0));
				flow.setUiTypeInf(processDef.getUiTypeInf() != null ? processDef.getUiTypeInf() : "");
			} else if (uiExpressionInf != null) {
				Object ui_inf = WfUtils.getResolvExpression((ProcessDefinitionImpl) processDef, uiExpressionInf,
						flow.getVariable(), null, null, EventType.UI_INF, session);
				if (ui_inf != null && ui_inf instanceof KrnObject) {
					flow.setInfUi((KrnObject) ui_inf);
					flow.setUiTypeInf(processDef.getUiTypeInf() != null ? processDef.getUiTypeInf() : "");
				}
			}
			ASTStart objExpressionInf = processDef.getObjExpressionInf();
			if (objExpressionInf != null) {
				Object inf_obj = WfUtils.getResolvExpression((ProcessDefinitionImpl) processDef, objExpressionInf,
						flow.getVariable(), null, null, EventType.ACT_OBJ_EXPR, session);
				if (inf_obj != null && inf_obj instanceof List && ((List) inf_obj).size() > 0) {
					List<KrnObject> ol = (List<KrnObject>) inf_obj;
					flow.setCutInfObj(ol.toArray(new KrnObject[ol.size()]));
				} else if (inf_obj != null && inf_obj instanceof KrnObject[]) {
					flow.setCutInfObj((KrnObject[]) inf_obj);
				} else if (inf_obj != null && inf_obj instanceof KrnObject)
					flow.setCutInfObj(new KrnObject[] { ((KrnObject) inf_obj) });
			} else {
				flow.setCutInfObj(null);
			}
			//Формируем строку из объектов обработки для наблюдателей 
			//и записываем их вместе с интерфейсом для наблюдателей
			KrnObject[] infCutObjs_=flow.getCutInfObj();
			String infCutObjs="";
			if(infCutObjs_!=null && infCutObjs_.length>0){
				for(KrnObject infCutObj:infCutObjs_){
					infCutObjs += ";" + infCutObj.id + "," + infCutObj.uid + "," + infCutObj.classId;					
				}
			}
        	processInstance.setObservers(observers);
        	processInstance.setUiObservers(flow.getInfUi()!=null?flow.getInfUi().uid+infCutObjs:"");
        	processInstance.setTypeUiObservers(flow.getUiType());
		}
    }

    public static Map<String, Object> clone(Map<String, Object> vars) {
    	Map<String, Object> res = new HashMap<String, Object>();
    	for (String key : vars.keySet()) {
    		res.put(key, clone(vars.get(key)));
    	}
    	return res;
    }

    private static Object clone(Object o) {
    	if (o instanceof KrnObject) {
    		KrnObject obj = (KrnObject)o;
    		return new KrnObject(obj.id, obj.uid, obj.classId);
    	} else if (o instanceof KrnClass) {
    		KrnClass cls = (KrnClass)o;
			return new KrnClass(cls.uid, cls.id, cls.parentId, cls.isRepl,
					cls.modifier, cls.name, cls.tname, cls.beforeCreateObjExpr,
					cls.afterCreateObjExpr, cls.beforeDeleteObjExpr,
					cls.afterDeleteObjExpr, cls.beforeCreateObjTr, cls.afterCreateObjTr,
					cls.beforeDeleteObjTr, cls.afterDeleteObjTr);// TODO tname
    	} else if (o instanceof Date) {
    		return ((Date)o).clone();
    	} else if (o instanceof Element) {
    		return ((Element)o).clone();
    	} else if (o instanceof List) {
    		List l = (List)o;
    		List res = new ArrayList(l.size());
    		for (Object v : l) {
    			res.add(clone(v));
    		}
    		return res;
    	} else if (o instanceof Map) {
    		Map m = (Map)o;
    		Map res = new HashMap();
    		for (Object key : m.keySet()) {
    			res.put(key, clone(m.get(key)));
    		}
    		return res;
    	} else {
    		return o;
    	}
    }

    public static Map<Long, String> getResolvExpressionAllLangs(
    		ProcessDefinitionImpl process,
            ASTStart expression,
            Map<String,Object> var,
            FlowImpl flow,
            Node node,
            EventType event,
            Session session
    ) throws WorkflowException {

    	Map<Long, String> res = new HashMap<Long, String>();
    	Map<String, Object> extVars = var != null ? var : new HashMap<String, Object>();
    	List<KrnObject> langs = session.getSystemLangs();
    	for (KrnObject lang : langs) {
        	extVars.put("ILANG", lang);
            Object value = getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
                    expression, extVars, flow, flow.getNode(), event, session);
            
            if (value instanceof String)
            	res.put(lang.id, (String)value);
            else if (value != null)
            	res.put(lang.id, value.toString());
    	}
    	extVars.remove("ILANG");
    	return res;
    }
    public static Map<Long, String> getResolvExpressionFlowAllLangs(
    		ProcessDefinitionImpl process,
    		ASTStart expression,
            Map<String,Object> var,
            FlowImpl flow,
            Node node,
            EventType event,
            Session session
    ) throws WorkflowException {
    	Log log = getLog(session);

    	Map<Long, String> res = new HashMap<Long, String>();
    	Map<String, Object> extVars = var != null ? var : new HashMap<String, Object>();
    	List<KrnObject> langs = session.getSystemLangs();
    	for (KrnObject lang : langs) {
    		Object value=null;
    		if(expression!=null){
	        	extVars.put("ILANG", lang);
	            value = getResolvExpression((ProcessDefinitionImpl)flow.getProcessInstance().getProcessDefinition(),
	                    expression, extVars, flow, flow.getNode(), event, session);
    		}else{
    	        try {
    	        	value= (node != null) ? process.getString(lang.id,""+node.getId()) : "";
    			} catch (Exception e) {
    				log.error(e, e);
    			}
	        	value= (value != null && value.equals("")) ? value : (node != null) ? node.getName():"";
    		}
            
            if (value instanceof String)
            	res.put(lang.id, (String)value);
            else if (value != null)
            	res.put(lang.id, value.toString());
    	}
    	extVars.remove("ILANG");
    	return res;
    }
    
    private static Log getLog(Session s) {
    	return s.getLog(WfUtils.class);
    }
}
