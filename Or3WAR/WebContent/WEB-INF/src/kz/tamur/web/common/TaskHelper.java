package kz.tamur.web.common;

import static kz.tamur.comps.Constants.*;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ReportLauncher;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.ReportObserver;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.web.common.table.WebTable;
import kz.tamur.web.common.table.WebTableCellRenderer;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.server.workflow.definition.EventType;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 03.07.2006
 * Time: 16:41:24
 */
public class TaskHelper implements ReportObserver {
    private Log log;
    private WebSession session;
    private Activity aAct = null;
    private Activity act_ui_open = null;
    
    private Activity auto_act_open = null;
    private KrnObject openUI = null;
    private boolean isAutoAct = false;
    private TaskTableModel model;
    private Vector<Activity> data = new Vector<Activity>();
    private static HashMap<String, String> processMap_ = new HashMap<String, String>();
    private HashMap<Long, Pair<long[], long[][]>> flowMap_ = new HashMap<Long, Pair<long[], long[][]>>();
    private HashMap<Long, Pair<long[], long[][]>> superFlowMap_ = new HashMap<Long, Pair<long[], long[][]>>();
    private HashMap<Long, Pair<long[], long[][]>> subFlowMap_ = new HashMap<Long, Pair<long[], long[][]>>();
    private HashMap<Long, Activity> createMap = new HashMap<Long, Activity>();
    private Set<Long> autoIfcSet_ = new TreeSet<Long>();
    private long autoIfcFlowId_ = 0;
    private long lastSuperFlowId_ = -1;
    //private User user;
    private long langId;
    private boolean isFC;

    private WebTable taskTable = new WebTable();
    private boolean isRanning = false;
    private TreeSet<Long> reportSet = new TreeSet<Long>();

    private static final String[] COLS = WebController.TASK_TABLE_COLS;
    private static final String[] COL_NAMES_RU = WebController.TASK_TABLE_NAMES_RU;
    private static final String[] COL_NAMES_KZ = WebController.TASK_TABLE_NAMES_KZ;
    private static final String[] COL_ALIGNS = WebController.TASK_TABLE_COLUMN_ALIGNS;
    private static final String[] COL_WIDTHS = WebController.TASK_TABLE_COLUMN_WIDTHS;
    
    private ThreadLocalDateFormat dateTimeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss");
    private ThreadLocalDateFormat dateFormat = new ThreadLocalDateFormat("dd.MM.yyyy");
    private ThreadLocalDateFormat timeFormat = new ThreadLocalDateFormat("HH:mm:ss");
    
    private Set<Long> activitiesInProcess = Collections.synchronizedSet(new HashSet<Long>());

	private Map<Long, EventListenerList> listeners_ = new HashMap<Long, EventListenerList>();

    public static final String FLR_ROW_BEGIN = "beginRow";
    public static final String FLR_ROW_END = "endRow";
    public static final String TASK_SORT_BY = "sortBy";
    public static final String TASK_SORT_DESC = "sortDesc";
    public static final String TASK_SEARCH_TEXT = "searchText";
    public static final String INTERFACE_LANG_ID = "interfaceLangId";
	private Map<String, Object> filterParams;
	private long ruLangId;
	
	private static final int ACT_WAITING_ERROR = -1;
	private static final int ACT_NO_ERROR = 0;
	private static final int ACT_HAS_ERROR = 1;

    public TaskHelper(WebSession s, long langId) {
        this.session = s;
        String prefix = session.getKernel().getUserSession().dsName + "."
				+ session.getKernel().getUserSession().logName + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : "");
        
        this.log = LogFactory.getLog(prefix + "." + getClass().getName());

        this.langId = langId;
        filterParams = new HashMap<String, Object>();
        model = new TaskTableModel(data);
        TaskTableRenderer renderer = new TaskTableRenderer();
        
        taskTable.setLogPrefix(prefix);
        taskTable.setClassName("taskTable");
        taskTable.setModel(model);
        taskTable.setRenderer(renderer);
        isRanning = true;
        //String noRights = System.getProperty("noRights");
        isFC = "1".equals(System.getProperty("finCentre"));
        
        try {
			KrnClass langCls = s.getKernel().getClassByName("Language");
			KrnAttribute codeAttr = s.getKernel().getAttributeByName(langCls, "code");
			KrnObject[] langObjs = s.getKernel().getObjectsByAttribute(langCls.id, codeAttr.id, 0, 0, "RU", 0);
			if (langObjs.length > 0) {
				ruLangId = langObjs[0].id;
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
    }

    public void release() {
    	isRanning = false;
    	session = null;
        if (data != null)
        	data.clear();
        data = null;
        if (flowMap_ != null)
        	flowMap_.clear();
        flowMap_ = null;
        if (superFlowMap_ != null)
        	superFlowMap_.clear();
        superFlowMap_ = null;
        if (subFlowMap_ != null)
        	subFlowMap_.clear();
        subFlowMap_ = null;
        model = null;
        taskTable = null;
    }

    public void setFilterParam(String rowFirst, String rowLast, String orderBy, String desc, String searchText) {
    	long firstRow = Long.parseLong(rowFirst);
    	long lastRow = Long.parseLong(rowLast);
    	filterParams.put(FLR_ROW_END, lastRow);
		filterParams.put(FLR_ROW_BEGIN, firstRow);
		filterParams.put(TASK_SORT_BY, orderBy);
		filterParams.put(TASK_SORT_DESC, desc == null || "1".equals(desc));
		filterParams.put(TASK_SEARCH_TEXT, searchText);
		filterParams.put(INTERFACE_LANG_ID, (session.getInterfaceLangId() == 0 || session.getInterfaceLangId() == ruLangId || ruLangId == 0 ? "1" : "2"));
        try {
        	session.getKernel().setTaskListFilter(filterParams);
        	data.clear();
            log.info("Loading tasks...");
        	loadTasks();
            log.info("Tasks loaded. Count: " + data.size());
        } catch (KrnException e) {
            log.error(e, e);
        }
    }
    
    private void loadTasks() {
        try {
            Kernel krn = session.getKernel();
            Activity[] data_ = krn.getTaskList();
            List<Activity> data_v = new Vector<Activity>();
            for (int i = 0; i < data_.length; ++i) {
                if (data_[i] == null || data_[i].flowId < 0)
                    continue;
                data.add(data_[i]);
                if (data_[i].processDefId.length > 1)
                    data_v.add(data_[i]);
            }
            if (data_v.size() > 0) {
                long[] flowIds = new long[data_v.size()];
                for (int i = 0; i < flowIds.length; i++) {
                    Activity act = data_v.get(i);
                    flowMap_.put(new Long(act.flowId), new Pair<long[], long[][]>(new long[] { act.flowId, act.processDefId[0] },
                            act.nodesId));
                    flowIds[i] = act.flowId;
                }
                SuperMap[] sm = krn.getMapList(flowIds);
                for (int i = 0; i < sm.length; ++i) {
                    flowMap_.put(new Long(sm[i].flowId), new Pair<long[], long[][]>(
                            new long[] { sm[i].flowId, sm[i].processDefId }, sm[i].nodes));
                    superFlowMap_.put(new Long(sm[i].subflowId), new Pair<long[], long[][]>(new long[] { sm[i].flowId,
                            sm[i].processDefId }, sm[i].nodes));
                }
                for (int i = 0; i < sm.length; ++i) {
                    subFlowMap_.put(new Long(sm[i].flowId), flowMap_.get(new Long(sm[i].subflowId)));
                }
            }
        } catch (KrnException e) {
            log.error(e, e);
        }
    }
    public long getTasksCount(){
    	long res=0;
    	try {
    		res= session.getKernel().getTasksCount();
		} catch (KrnException e) {
            log.error(e, e);
		}
    	return res;
    }
    public void setSelectedActivity(long flowId) {
        Activity act = getActivityById(flowId);
        taskTable.setSelectedObject(act);
    }

    public void setSelectedColumn(int col) {
        taskTable.setSelectedColumn(col);
    }

    public Activity startProcess(String flowId, List<String> param) {
        isAutoAct = false;
        Activity act = new Activity();
        act.flowId = Long.valueOf(flowId);
        act.rootFlowId = act.flowId;
        act.titles = new String[] { session.getResource().getString("createProcess"), "", "" };
        act.param = ACT_SUB_PROC;
        // если монир задач не отображается, то установить флаг автоматического открытия интерфейса
        act.autoIfc = !session.getKernel().getUser().isMonitor();
        ((TaskTableModel) taskTable.getModel()).addActivity(act);
        taskTable.setSelectedObject(act);
        createMap.put(act.flowId, act);
        autoIfcSet_.add(act.flowId);
        
        if (param.contains("autoIfc") || param.contains("synch")) {
            setAutoIfcFlowId_(act.flowId);
        }
        if (param.contains("synch")) {
            reloadTask(act.flowId, 0, true, false);
        }
        return act;
    }

    public boolean stopProcess(Activity act, boolean forceCancel) throws KrnException {
    	long oldId = act.ui.id;
    	long oldInfId = act.ui.id;
    	int oldPermit = (int) act.param & ACT_PERMIT;

    	boolean res = false;
    	try {
	        disableActivity(act);
	        res = session.getKernel().cancelProcess(act.flowId, act.msg, true, forceCancel);
	        if (res) {
	        	model.deleteRow(act);
	        }
        	activitiesInProcess.remove(act.flowId);
    	} catch (KrnException e) {
            reenableActivity(act, oldId, oldInfId, oldPermit);
    		log.error(e, e);
    		throw e;
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return res;
    }
    
    // Блокировка задачи
    public void disableActivity(Activity act) {
        act.ui.id = -1;
        act.infUi.id = -1;
        if ((act.param & ACT_PERMIT) == ACT_PERMIT) {
            act.param ^= ACT_PERMIT;
        }
        // Не обновляеть значение интерфейса (так как мы выставили его на -1)
        activitiesInProcess.add(act.flowId);
        model.addUpdatedRow(act);
    }

    // Разблокировка задачи (в случае падения или невозможности перейти на следующий шаг)
    public void reenableActivity(Activity act, long uiId, long infId, int permit) {
        act.ui.id = uiId;
        act.infUi.id = infId;
        if ((act.param & ACT_PERMIT) == ACT_PERMIT) {
            act.param &= permit;
        } else {
            act.param |= permit;
        }
        activitiesInProcess.remove(act.flowId);
        model.addUpdatedRow(act);
    }

    // Начинаем принимать изменения с сервера связанные с интерфейсом
    public void reenableActivity(long flowId) {
        activitiesInProcess.remove(flowId);
    }

    public List<Activity> findLocalProcess(KrnObject def, KrnObject obj) {
        List<Activity> res = new ArrayList<Activity>();
        synchronized (model.data) {
            for (Activity act : model.data) {
                if (Funcs.indexOf(def.id, act.processDefId) != -1) {
                    if (Funcs.indexOf(obj, act.objs) != -1 || Funcs.indexOf(obj, act.infObjs) != -1) {
                        res.add(act);
                    }
                }
            }
        }
        log.info("findLocalProcess def:"+def+";obj:"+obj+";res:"+res);
        return res;
    }
    
    // addRow - Добавлять найденный чужой процесс к себе в монитор
    // onlyMy - искать только процессы доступные user-у
    public List<Activity> findProcess(KrnObject def, KrnObject obj, boolean addRow, boolean onlyMy) {
        List<Activity> res = findLocalProcess(def, obj);
        if (res.isEmpty()) {
			try {
	        	List<Long> l = findForeignProcess(def, obj);
	        	if (l != null && l.size() > 0) {
	        		long flowId = l.get(0);
		            Activity act = session.getKernel().getTask(flowId, 0, true, onlyMy);
		            res.add(act);

		            if (act.ui != null && act.ui.id > -1 && 
		            		(act.timeActive > 0 ||
		            				(session.getKernel().getUser() != null 
		            				&& session.getKernel().getUser().getObject().id != act.actorId
		            				&& !session.getKernel().getUser().isAdmin())
		            		)
		            	) {
		            	// процесс чужой - заходить туда не надо
		            	act.ui = null;
		            }
		            
		            if (act.ui != null && act.ui.id > -1) {
		            	TaskTableModel model = (TaskTableModel) taskTable.getModel();
		            	model.reloadActivity(flowId, false, 0);
		            }
	        	}
			} catch (KrnException e) {
				log.error(e, e);
			}
        }
        log.info("findProcess def:"+def+"; obj:"+obj+"; res:"+res);
        return res;
    }

    public List<Long> findForeignProcess(KrnObject def, KrnObject obj) throws KrnException {
        return session.getKernel().findForeignProcess(def.id, obj.id);
    }
 
    public void openUI(Activity act, boolean nextStep) {
        model.addOpenUIRow(act, nextStep);
    }

    public void removeOpenUI(Activity act) {
        model.removeOpenUIRow(act);
    }

    public int next(WebFrame frm) {
        Activity act_ = getActivityById(frm.getFlowId());
        return next(act_, frm);
    }

    public int next(Activity act_, WebFrame frm) {
        try {
            return next(act_, frm, false);
        } catch (KrnException e) {
        }
        return -1;
    }

    public int next(Activity act_, WebFrame frm, boolean force) throws KrnException {
        int result = -1;
        try {
            int result_ = -1;
            String res_ = "";
            
            result = force ? ButtonsFactory.BUTTON_YES : frm.confirm(session.getResource().getString("nextStepMessage"));
            
            if (result == ButtonsFactory.BUTTON_YES) {
            	long oldId = act_.ui.id;
            	long oldInfId = act_.ui.id;
            	
            	disableActivity(act_);
            	
                if (act_.transitions.length > 1) {
                    String[] trs = new String[act_.transitions.length];
                    for (int i = 0; i < trs.length; ++i) {
                        trs[i] = new String(act_.transitions[i].substring(0, act_.transitions[i].indexOf(",")));
                    }
                    result_ = frm.getOption(act_);

                    if (result_ != -1) {
                        for (int i = 0; i < trs.length; ++i) {
                            if (result_ == i) {
                                res_ = new String(act_.transitions[i].substring(act_.transitions[i].lastIndexOf(",") + 1));
                                break;
                            }
                        }
                    }
                }

                if (act_.processDefId.length > 1) {
                    lastSuperFlowId_ = act_.processDefId[1];
                } else if (act_.processDefId.length > 0) {
                    lastSuperFlowId_ = act_.processDefId[0];
                }
                act_.error_msg = ACT_WAITING_ERROR;
                
            	if ((act_.param & Constants.ACT_ERR) > 0) {
            		act_.param = act_.param ^ Constants.ACT_ERR;
            	}
        		if(act_.superFlowIds != null && act_.superFlowIds.length > 0) {
        			Activity actSuper_ = model.getActivity(act_.superFlowIds[0]);
        			
        			if (actSuper_ != null && (actSuper_.param & Constants.ACT_ERR) > 0) {
        				actSuper_.param = actSuper_.param ^ Constants.ACT_ERR;
        			}
                }
            	
                String[] res = session.getKernel().performActivitys(new Activity[] { act_ }, res_);

                //Предупреждение позволяющее пользователю продолжить процесс по своему усмотрению
               if(res.length>0 && res[0].indexOf("deferred")==0) {
                	int resConf=frm.confirm(res[0].substring(8));
                	if(resConf!=ButtonsFactory.BUTTON_NO) {
                        res = session.getKernel().performActivitys(new Activity[] { act_ }, res_,EventType.DEFERRED_PERFORM_OF_ACTIVITY.toString());
                	}else {
                        reenableActivity(act_, oldId, oldInfId, 0);
                		return ButtonsFactory.BUTTON_NOACTION;
                	}
                }
                if (res.length == 1 && res[0].equals("synch")) {
                    setAutoIfcFlowId_(act_.flowId);
                }

                if (res.length > 0 && !res[0].equals("synch")) {
                    session.getKernel().setPermitPerform(act_.flowId, false);
                    reenableActivity(act_, oldId, oldInfId, 0);
                    
                    StringBuilder msg = new StringBuilder(res[0]);
                    for (int i = 1; i < res.length; ++i) {
                        msg.append("\n").append(res[i]);
                    }
                    result = -1;
                    ((WebPanel) frm.getPanel()).setAlertMessage(msg.toString().replaceFirst("^\\!", ""), false);
                    if (force && msg.charAt(0) != '!') {
                        throw new KrnException(0, msg.toString());
                    }
                } else {
                    reenableActivity(act_.flowId);
                    autoIfcSet_.add(act_.flowId);
                    if (act_ui_open == act_) {
                        act_ui_open = null;
                    }
                }
                if (result != -1 && res.length == 1 && res[0].equals("synch")) {
                    reloadTask(act_.flowId, 0, true, true);
                }
            }
        } catch (KrnException e1) {
            log.error(e1, e1);
            reenableActivity(act_, -1, -1, 0);
            if (force) {
                throw new KrnException(0, e1.getMessage());
            }
        }
        return result;
    }

    public void killProcess(Map<String, String> args) {
        String pid = args.get("id");
        Long flowId = (long) -1;
        // Eсли завершение процесса не из монитора задач.
        KrnObject defObj = null;
        KrnObject objObj = null;

        if (pid == null) {
            try {
                pid = args.get("uid");
                String obj = args.get("obj");
                if("".equals(pid) || pid == null) {
                	log.error("Не задан идентификатор процесса!\tfindProcess(pid='" + pid + "'; obj='" + obj + "')");
//                	return;
                }
                defObj = session.getKernel().getObjectByUid(pid, 0);
                objObj = session.getKernel().getObjectByUid(obj, 0);
                List<Activity> acts = new ArrayList<Activity>();
                if (defObj != null && objObj!=null) {
                	acts = findLocalProcess(defObj, objObj);
                }
                if (acts.size() == 1) {
                    flowId = acts.get(0).flowId;
                } else {
                    log.error("Процесс не найден в таблице TaskTable!\tfindProcess(pid='" + pid + "'; obj='" + obj + "')");
                    log.info("Поиск процесса в БД.");
        			List<Long> flowIds = new ArrayList<Long>();
        			if (objObj != null) {
        				flowIds = session.getKernel().findForeignProcess(defObj == null ? -1 : defObj.id, objObj.id);
                    }
        			if (flowIds == null) {
        				log.error("Ошибка при поиске процесса в БД!");
        				session.sendCommand("closeWaiting","1");
                        return;
        			} else if (flowIds.size() == 0) {
        				log.error("Процесс не найден в БД! Удаление поручения и его детей!");
        				// Удаление детей
        				KrnClass poruchenieCls = session.getKernel().getClassByName("уд::осн::Поручение");
        				KrnAttribute childrenAttr = session.getKernel().getAttributeByName(poruchenieCls, "дети");
        				KrnObject[] children = null;
        				if (objObj != null) {
	        				children = session.getKernel().getObjects(objObj, childrenAttr, 0);
	        				for(int i = 0; i < children.length; i++) {
	        					session.getKernel().deleteObject(children[i], 0);
	        				}
	        				// Удаление родителя
	        				session.getKernel().deleteObject(objObj, 0);
        				}
        				String key = args.get("key");
        				if (key != null && key.length() > 0) {
	        				KrnObject objByKey = session.getKernel().getClassObjectByUid(poruchenieCls.id, key,-1);
	        				if (objByKey!=null) {
		        				children = session.getKernel().getObjects(objByKey, childrenAttr, -1);
		        				for(int i = 0; i < children.length; i++) {
		        					try {
		        						session.getKernel().deleteObject(children[i], 0);
		        					} catch (Exception e) {
		        						log.error(e, e);
		        					}
		        				}
		        				// Удаление родителя
	        					try {
			        				session.getKernel().deleteObject(objByKey, 0);
	        					} catch (Exception e) {
	        						log.error(e, e);
	        					}
	        				}
        				}
        				session.sendCommand("closeWaiting","1");
                        return;
        			} else if (flowIds.size() > 1) {
        				log.info("В БД найдено более одного процесса, связанного с данным поручением!\tfindProcess(pid='" + pid + "'; obj='" + obj + "')");
        				session.sendCommand("closeWaiting","1");
                        return;
        			} else {
        				flowId = flowIds.get(0);
        			}
                }
            } catch (KrnException e) {
                log.error(e, e);
            }
        } else {
            flowId = Long.parseLong(pid);
        }
        
        TaskTableModel model = (TaskTableModel) taskTable.getModel();
        Activity act = model.getActivity(flowId);
        if (act == null) {
        	try {
        		act = session.getKernel().getTask(flowId, 0, true, false);
        	} catch (KrnException e) {
                log.error(e, e);
            }
    	}
        if (act != null && ((act.param & ACT_CANCEL) == ACT_CANCEL || (act.param & ACT_ERR) == ACT_ERR || !isProcessRunning(act.flowId))) {
            try {
            	stopProcess(act, true);
				// Удаление поручения когда процесс запущен в нулевой транзакции
            	if(objObj!=null && act.trId==0) {
    				String key = args.get("key");
    				KrnClass poruchenieCls = session.getKernel().getClassByName("уд::осн::Поручение");
    				KrnAttribute childrenAttr = session.getKernel().getAttributeByName(poruchenieCls, "дети");
    				if (key != null && key.length() > 0) {
        				KrnObject objByKey = session.getKernel().getClassObjectByUid(poruchenieCls.id, key, 0);
        				KrnObject[] children = session.getKernel().getObjects(objByKey, childrenAttr, 0);
        				// Удаление родителя
        				session.getKernel().deleteObject(objByKey, 0);
        				//удаление детей
        				for(int i = 0; i < children.length; i++) {
        					session.getKernel().deleteObject(children[i], 0);
        				}
    				}
            	}
            } catch (KrnException e) {
                log.error(e, e);
            }
		} else {
			session.sendCommand("closeWaiting","1");
			session.sendCommand("alert", "У вас нет прав для удаления данного процесса!");
		}
    }

    public String nextStep(Map<String, String> args) {
        StringBuilder b = new StringBuilder(1024).append("<r>");
        String pid = args.get("id");
        Long flowId = Long.parseLong(pid);
        // if (!myActivities_.contains(flowId)) myActivities_.add(flowId);
        String opt = args.get("opt");
        Integer ind = -10;
        if (opt != null) {
            try {
                ind = Integer.parseInt(opt);
            } catch (Exception e) {
                ind = -1;
            }
        }

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        String res_ = "";

        if (act != null) {
            if (ind > -10) {
                if (ind == -1) {
                    b.append("</r>");
                    return b.toString();
                } else {
                    res_ = act.transitions[ind].substring(act.transitions[ind].lastIndexOf(",") + 1);
                }
            } else {
                if (act.transitions != null && act.transitions.length > 1) {
                    ViewHelper.getOptionDialogXml(act, b);
                    b.append("</r>");
                    return b.toString();
                }
            }
        	long oldId = act.ui.id;
        	long oldInfId = act.ui.id;
        	int oldPermit = (int) act.param & ACT_PERMIT;
            try {
            	disableActivity(act);

                String[] res = session.getKernel().performActivitys(new Activity[] { act }, res_);
                if (res.length == 1 && res[0].equals("synch")) {
                    setAutoIfcFlowId_(act.flowId);
                }
                if (res.length > 0 && !res[0].equals("synch")) {
                    act.param |= ACT_ERR;
                    reenableActivity(act, oldId, oldInfId, oldPermit);
                    
                    String msg = res[0];
                    for (int i = 1; i < res.length; ++i)
                        msg += "\n" + res[i];
                    b.append("<alert>").append(msg).append("</alert>");

                } else {
                    reenableActivity(act.flowId);
                    autoIfcSet_.add(act.flowId);

                    if (res.length == 1 && res[0].equals("synch")) {
                        reloadTask(act.flowId, 0, true, true);
                    }
                }
            } catch (KrnException e) {
                log.error(e, e);
                reenableActivity(act, oldId, oldInfId, oldPermit);
            }
        } else {
            b.append("<alert>Не найден процесс с flowId = ").append(flowId).append("</alert>");
        }
        b.append("</r>");
        return b.toString();
    }

    public String generateFastReport(Map<String, String> args) {
        StringBuilder b = new StringBuilder("<r>");
        String pid = args.get("id");
        Long flowId = Long.parseLong(pid);

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        reportSet.add(act.flowId);

        File dir = WebController.WEB_DOCS_DIRECTORY;

        long editorType = 0;

        try {
            Kernel krn = session.getKernel();
            KrnObject flow = new KrnObject(act.flowId, "", Kernel.SC_FLOW.id);
            byte[] article = (act.article != null && act.article.length > 0) ? act.article : krn.getBlob(flow, "article", 0, 0,
                    -1);

            InputStream is = new ByteArrayInputStream(article);
            SAXBuilder builder = new SAXBuilder();
            Element report = builder.build(is).getRootElement();

            String suffix = ".xls";
            String nameFile = act.titles.length>4 && !"".equals(act.titles[4])?act.titles[4]: act.titles[3];
            nameFile = Utils.convertToNameFile(nameFile);
            
            File docFile = null;
            int i = 0;
            do {
            	docFile = (nameFile != null) ? new File(dir, nameFile + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
            } while (docFile.exists());
            
            String fileName = docFile.getAbsolutePath();
            docFile.delete();

            ReportLauncher.generateReportPoi(report, editorType, session.getResource().getString("formReport"), fileName);

            docFile = new File(fileName);
            if (docFile != null && docFile.exists()) {
                session.deleteOnExit(docFile);
                String afn = docFile.getAbsolutePath();
                afn = afn.replaceAll("\\\\", "/");
                fileName = afn.substring(afn.lastIndexOf("/") + 1);
                b.append("<viewFile>");
                b.append(fileName);
                b.append("</viewFile>");
            }
        } catch (Exception e) {
            b.append("<alert>");
            b.append("Ошибка при генерировании отчета!");
            b.append("</alert>");
            log.error(e, e);
        } finally {
            setReportComplete(flowId);
        }

        b.append("</r>");
        return b.toString();

    }

    public String generateReport(Map<String, String> args) {
        StringBuilder b = new StringBuilder("<r>");
        String pid = args.get("id");
        Long flowId = Long.parseLong(pid);

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        reportSet.add(act.flowId);

        File dir = WebController.WEB_DOCS_DIRECTORY;

        long lid = act.articleLang != null ? act.articleLang.id : 0;
        Kernel krn = session.getKernel();
        File docFile = null;

        try {
            KrnObject flow = new KrnObject(act.flowId, "", Kernel.SC_FLOW.id);
            if (lid <= 0 && krn.getAttributeByName(krn.getClassByName("Flow"), "article_lang") != null) {
                KrnObject[] langs = krn.getObjects(flow, "article_lang", -1);
                lid = (langs != null && langs.length > 0) ? langs[0].id : 0;
            }

            byte[] article = (act.article != null && act.article.length > 0) ? act.article : krn.getBlob(flow, "article", 0, 0,
                    -1);

            byte[] htmlBuf = null;
            KrnAttribute htmlAttr = krn.getAttributeByName(krn.getClass(act.ui.classId), "htmlTemplate");
            if (htmlAttr != null) {
                htmlBuf = krn.getBlob(act.ui, "htmlTemplate", 0, lid, 0);
            }
            if (htmlBuf != null && htmlBuf.length > 0) {
                docFile = ReportLauncher.viewHtmlReportI(article, htmlBuf, session.getResource().getString("formReport"),
                        TaskHelper.this, act.flowId, dir);
                session.deleteOnExit(docFile);
            } else if (article.length > 0 && article[0] == '<') {

                ByteArrayInputStream bis = new ByteArrayInputStream(article);
                SAXBuilder builder = new SAXBuilder();
                Element root = builder.build(bis).getRootElement();
                List res = XPath.selectNodes(root, "//@src");

                byte[] bytes;
                if (res != null && res.size() > 0) {
                    for (int i = 0; i < res.size(); i++) {
                        Attribute attr = (Attribute) res.get(i);
                        String img = attr.getValue();
                        if (img.length() > 0) {
                            File f = Funcs.createTempFile("img", ".tmp", dir);
                            session.deleteOnExit(f);
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(Base64.decode(img));
                            fos.close();
                            attr.setValue(f.getAbsolutePath());
                        }
                    }
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    XMLOutputter opr = new XMLOutputter();
                    opr.getFormat().setEncoding("UTF-8");
                    opr.output(root, os);
                    os.close();
                    bytes = os.toByteArray();
                } else {
                    bytes = article;
                }
                File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
                session.deleteOnExit(xmlFile);
                OutputStream os = new FileOutputStream(xmlFile);
                os.write(bytes);
                os.close();

                byte[] data = krn.getBlob(act.ui, "config", 0, 0, 0);

                InputStream is = new ByteArrayInputStream(data);
                builder = new SAXBuilder();
                Element config = builder.build(is).getRootElement();
                Element type = config.getChild("editorType");
                int editorType = MSWORD_EDITOR;
                if (type != null) {
                    editorType = Integer.parseInt(type.getText());
                }

                Element macrosElement = config.getChild("macros");
                String macros = "";
                if (macrosElement != null) {
                    macros = macrosElement.getText();
                }
                String templatePD = config.getChildText("templatePassword");

                String suffix = (editorType == MSWORD_EDITOR) ? ".doc" : ".xls";
                if (!"jacob".equals(System.getProperty("reportType")))
                    suffix += "x";
                String nameFile = act.titles.length>4 && !"".equals(act.titles[4])?act.titles[4]: act.titles[3];
                nameFile = Utils.convertToNameFile(nameFile);
                
                int i = 0;
                do {
                	docFile = (nameFile != null) ? new File(dir, nameFile + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
                } while (docFile.exists());

                session.deleteOnExit(docFile);
                byte[] buf = krn.getBlob(act.ui, "template", 0, lid, 0);
                os = new FileOutputStream(docFile);
                os.write(buf);
                os.close();

                ReportLauncher.viewReportI(docFile.getAbsolutePath(), xmlFile.getAbsolutePath(), editorType, session
                        .getResource().getString("formReport"), macros, templatePD, TaskHelper.this, act.flowId);
            } else if (article.length > 4 && article[0] == '%' && article[1] == 'P') {
            	docFile = ReportLauncher.viewPdfReport(article, session.getResource().getString("formReport"), 
            			TaskHelper.this, act.flowId, dir, false);
                session.deleteOnExit(docFile);
            }
            if (docFile != null) {
                String afn = docFile.getAbsolutePath();
                afn = afn.replaceAll("\\\\", "/");
                String fileName = afn.substring(afn.lastIndexOf("/") + 1);
                b.append("<viewFile>");
                b.append(fileName);
                b.append("</viewFile>");
            }
        } catch (Exception e) {
            b.append("<alert>");
            b.append("Ошибка при генерировании отчета!");
            b.append("</alert>");
            log.error(e, e);
        }

        b.append("</r>");
        return b.toString();
    }

    public String generateReport2(Map<String, String> args) {
        String pid = args.get("uid");
        Long flowId = Long.parseLong(pid);

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        reportSet.add(act.flowId);

        File dir = WebController.WEB_DOCS_DIRECTORY;

        long lid = act.articleLang != null ? act.articleLang.id : 0;
        Kernel krn = session.getKernel();
        File docFile = null;

        try {
            KrnObject flow = new KrnObject(act.flowId, "", Kernel.SC_FLOW.id);
            if (lid <= 0 && krn.getAttributeByName(krn.getClassByName("Flow"), "article_lang") != null) {
                KrnObject[] langs = krn.getObjects(flow, "article_lang", -1);
                lid = (langs != null && langs.length > 0) ? langs[0].id : 0;
            }

            byte[] article = (act.article != null && act.article.length > 0) ? act.article : krn.getBlob(flow, "article", 0, 0,
                    -1);

            byte[] htmlBuf = null;
            
            if (act.ui != null) {
	            KrnAttribute htmlAttr = krn.getAttributeByName(krn.getClass(act.ui.classId), "htmlTemplate");
	            if (htmlAttr != null) {
	                htmlBuf = krn.getBlob(act.ui, "htmlTemplate", 0, lid, 0);
	            }
            }
            if (htmlBuf != null && htmlBuf.length > 0) {
                docFile = ReportLauncher.viewHtmlReportI(article, htmlBuf, session.getResource().getString("formReport"),
                        TaskHelper.this, act.flowId, dir);
                session.deleteOnExit(docFile);
            } else if (act.ui != null && article.length > 0 && article[0] == '<') {

                ByteArrayInputStream bis = new ByteArrayInputStream(article);
                SAXBuilder builder = new SAXBuilder();
                Element root = builder.build(bis).getRootElement();
                List res = XPath.selectNodes(root, "//@src");

                byte[] bytes;
                if (res != null && res.size() > 0) {
                    for (int i = 0; i < res.size(); i++) {
                        Attribute attr = (Attribute) res.get(i);
                        String img = attr.getValue();
                        if (img.length() > 0) {
                            File f = Funcs.createTempFile("img", ".tmp", dir);
                            session.deleteOnExit(f);
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(Base64.decode(img));
                            fos.close();
                            attr.setValue(f.getAbsolutePath());
                        }
                    }
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    XMLOutputter opr = new XMLOutputter();
                    opr.getFormat().setEncoding("UTF-8");
                    opr.output(root, os);
                    os.close();
                    bytes = os.toByteArray();
                } else {
                    bytes = article;
                }
                File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
                session.deleteOnExit(xmlFile);
                OutputStream os = new FileOutputStream(xmlFile);
                os.write(bytes);
                os.close();

                byte[] data = krn.getBlob(act.ui, "config", 0, 0, 0);

                InputStream is = new ByteArrayInputStream(data);
                builder = new SAXBuilder();
                Element config = builder.build(is).getRootElement();
                Element type = config.getChild("editorType");
                int editorType = MSWORD_EDITOR;
                if (type != null) {
                    editorType = Integer.parseInt(type.getText());
                }

                Element macrosElement = config.getChild("macros");
                String macros = "";
                if (macrosElement != null) {
                    macros = macrosElement.getText();
                }
                String templatePD = config.getChildText("templatePassword");

                String suffix = (editorType == MSWORD_EDITOR) ? ".doc" : ".xls";
                if (!"jacob".equals(System.getProperty("reportType")))
                    suffix += "x";
                String nameFile = act.titles.length>4 && !"".equals(act.titles[4])?act.titles[4]: act.titles[3];
                nameFile = Utils.convertToNameFile(nameFile);
                
                int i = 0;
                do {
                	docFile = (nameFile != null) ? new File(dir, nameFile + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
                } while (docFile.exists());
                
                session.deleteOnExit(docFile);

                byte[] buf = krn.getBlob(act.ui, "template", 0, lid, 0);
                os = new FileOutputStream(docFile);
                os.write(buf);
                os.close();

                ReportLauncher.viewReportI(docFile.getAbsolutePath(), xmlFile.getAbsolutePath(), editorType, session
                        .getResource().getString("formReport"), macros, templatePD, TaskHelper.this, act.flowId);
            } else if (article.length > 4 && article[0] == '%' && article[1] == 'P') {
            	docFile = ReportLauncher.viewPdfReport(article, session.getResource().getString("formReport"), 
            			TaskHelper.this, act.flowId, dir, false);
                session.deleteOnExit(docFile);
            } else if (article.length > 0) {
            	byte[] data = krn.getBlob(act.ui, "config", 0, 0, 0);

                InputStream is = new ByteArrayInputStream(data);
                SAXBuilder builder = new SAXBuilder();
                Element config = builder.build(is).getRootElement();
                Element type = config.getChild("editorType");
                int editorType = MSWORD_EDITOR;
                if (type != null) {
                    editorType = Integer.parseInt(type.getText());
                }
                String suffix = (editorType == MSWORD_EDITOR) ? ".doc" : ".xls";
                if (!"jacob".equals(System.getProperty("reportType")))
                    suffix += "x";
                long time = System.currentTimeMillis();
                long time2 = act.date.getTime();
                ThreadLocalDateFormat sdf = ThreadLocalDateFormat.get("dd.MM.yyyy");
                Date resultdate = new Date(time2);
                String date = sdf.format(resultdate);
                try {
                	String nameFile = act.titles[3];
                	nameFile = Utils.convertToNameFile(nameFile);
                    
                    int i = 0;
                    do {
                    	docFile = (nameFile != null) ? new File(dir, nameFile + " от " + date + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
                    } while (docFile.exists());

                    docFile.deleteOnExit();
                    FileOutputStream os = new FileOutputStream(docFile);
                    os.write(article);
                    os.close();
                } catch (Exception e) {
                    log.error(e, e);
                }
                log.info("Forming report time: " + (System.currentTimeMillis() - time));
                
                if (flowId > 0) {
                    this.setReportComplete(flowId);
                }
            }
            if (docFile != null) {
                String afn = docFile.getAbsolutePath();
                afn = afn.replaceAll("\\\\", "/");
                return afn.substring(afn.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return null;
    }

    public String generateFastReport2(Map<String, String> args) {
        String pid = args.get("uid");
        Long flowId = Long.parseLong(pid);

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        reportSet.add(act.flowId);

        File dir = WebController.WEB_DOCS_DIRECTORY;

        Kernel krn = session.getKernel();
        File docFile = null;

        long editorType = 0;

        try {
            KrnObject flow = new KrnObject(act.flowId, "", Kernel.SC_FLOW.id);
            byte[] article = (act.article != null && act.article.length > 0) ? act.article : krn.getBlob(flow, "article", 0, 0,
                    -1);

            File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
            session.deleteOnExit(xmlFile);
            OutputStream os = new FileOutputStream(xmlFile);
            os.write(article);
            os.close();

            InputStream is = new ByteArrayInputStream(article);
            SAXBuilder builder = new SAXBuilder();
            Element report = builder.build(is).getRootElement();

            String suffix = ".xls";
            String nameFile = act.titles.length>4 && !"".equals(act.titles[4])?act.titles[4]: act.titles[3];
            nameFile = Utils.convertToNameFile(nameFile);
            
            int i = 0;
            do {
            	docFile = (nameFile != null) ? new File(dir, nameFile + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
            } while (docFile.exists());
            
            String fileName = docFile.getAbsolutePath();
            docFile.delete();

            ReportLauncher.generateReportPoi(report, editorType, session.getResource().getString("formReport"), fileName);

            docFile = new File(fileName);
            if (docFile != null && docFile.exists()) {
                session.deleteOnExit(docFile);
                String afn = docFile.getAbsolutePath();
                
                afn = afn.replaceAll("\\\\", "/");
                return afn.substring(afn.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            setReportComplete(flowId);
        }

        return null;
    }

    public String getOptionPane(Map<String, String> args, ResourceBundle resource) {
        String pid = args.get("id");
        Long flowId = Long.parseLong(pid);

        Activity act = ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
        if (act != null)
            return ViewHelper.getOptionDialogHTML(act, resource,session.getConfigNumber());
        return "";
    }

    public Activity getActivityById(long flowId) {
        return ((TaskTableModel) taskTable.getModel()).getActivity(flowId);
    }

    public Activity getActivityByRootFlowId(long flowId) {
        return ((TaskTableModel) taskTable.getModel()).getActivityByRootFlowId(flowId);
    }

    public Activity getActivityBySuperFlowId(long flowId) {
        return ((TaskTableModel) taskTable.getModel()).getActivityBySuperFlowId(flowId);
    }

    public WebTable getTable() {
        return taskTable;
    }

    public void reloadTask(long flowId, long ifsPar, boolean isStartAutoAct, boolean nextStep) {
        TaskTableModel model = (TaskTableModel) taskTable.getModel();
        model.reloadActivity(flowId, isStartAutoAct, ifsPar);
        if (isStartAutoAct && aAct != null && !isAutoAct) {
            if (!aAct.autoIfc || !session.getKernel().isSE_UI()) {
                isAutoAct = true;
            }
			if (model.data.contains(aAct)
					&& (openUI == null || !openUI.equals(aAct.ui) || (aAct.transitions != null && aAct.transitions.length > 1))
					&& (aAct.param & ACT_ERR) != ACT_ERR && aAct.ui != null && aAct.ui.id > 0) {
				startAutoIfc(nextStep);
			} else {
				if ((aAct.param & ACT_ERR) == ACT_ERR
						&& (aAct.param & ACT_CANCEL) != ACT_CANCEL) {
					aAct.param = aAct.param | ACT_CANCEL;
				}
				isAutoAct = false;
			}
        }
        if( aAct != null && aAct.error_msg == ACT_HAS_ERROR) {
        	aAct.error_msg = ACT_NO_ERROR; //информацию об ошибке передана пользователю, исключаем повторную передачу 
			session.sendCommand("alert", "Ошибка на шаге процесса!");
        }
        if (WebController.DO_AFTER_TASKLIST_UPDATE && session.getFrameManager().getCurrentFrame() != null) {
            session.getFrameManager().getCurrentFrame().doAfterTaskListUpdate();
        }
        try {
			if(aAct!=null) sendNodeType(aAct);
		} catch (KrnException e) {
			log.error(e, e);
		}
    }
    
    private void sendNodeType(Activity act) throws KrnException {
    	String nodeType = session.getKernel().getNextProcessNode(act.flowId);
    	String buttonTitle = session.getResource().getString("send");
    	if ("end-state".equals(nodeType)) {
    		buttonTitle = session.getResource().getString("webSend");
    	} else if (!"".equals(nodeType)) {
    		buttonTitle = session.getResource().getString("buttonRun");
    	}
    	session.sendCommand("nodeType", buttonTitle);
    }

    private void startAutoIfc(boolean nextStep) {
        try {
            //aAct.autoIfc = !session.getKernel().getUser().isMonitor();
            setSelectedActivity(aAct.flowId);
            session.getKernel().openInterface(aAct.ui.id,aAct.flowId,aAct.trId,aAct.processDefId.length>0?aAct.processDefId[0]:-1);
            boolean isDialog = ACT_DIALOG_STRING.equals(aAct.uiType) || ACT_AUTO_STRING.equals(aAct.uiType);
            if ((aAct.param & Constants.ACT_REPORT_REQUIRE) != Constants.ACT_REPORT_REQUIRE //если на активити стоит условие открывать отчет по требованию
            		&& ((autoIfcFlowId_ == aAct.flowId || aAct.rootFlowId == autoIfcFlowId_ || Funcs.indexOf(autoIfcFlowId_, aAct.superFlowIds) != -1) 
            		&& (!aAct.autoIfc || !session.getKernel().isSE_UI()) || !isDialog)) {
                autoIfcFlowId_ = 0;
                openUI = aAct.ui;
                setAutoAct(true);
                openUI(aAct, nextStep);
            }
        } catch (KrnException e) {
            log.error(e, e);
        } finally {
            if (aAct != null && (!aAct.autoIfc || !session.getKernel().isSE_UI())) {
                // aAct = null;
                setAutoAct(false);
                // autoIfcFlowId_ = 0;
            }
        }
    }

    public boolean isEnable(Activity act, String column) {
        boolean res = false;
        if ("open".equals(column)) {
            res = (act.processDefId != null && (act.ui.id > 0 || (act.param & ACT_FASTREPORT) == ACT_FASTREPORT ||
            		(act.transitions != null && act.transitions.length > 1)) && !reportSet.contains(act.flowId));
        } else if ("openInspector".equals(column)) {
            res = (act.processDefId != null && act.infUi.id > 0);
        } else if ("nextStep".equals(column)) {
            res = ((act.param & ACT_ARTICLE) == ACT_ARTICLE && (act.processDefId != null && act.ui.id > 0 && !reportSet
                    .contains(act.flowId)))
                    || ((act.param & ACT_FASTREPORT) == ACT_FASTREPORT)
                    || (session.getKernel().getUser().isAdmin() && ((act.param & ACT_ERR) == ACT_ERR) && (act.param & ACT_PERMIT) == ACT_PERMIT)
                    || (((act.param & ACT_PERMIT) == ACT_PERMIT) && ((act.param & ACT_IN_BOX) != ACT_IN_BOX)
                            && ((act.param & ACT_OUT_BOX) != ACT_OUT_BOX) && ((act.param & ACT_SUB_PROC) != ACT_SUB_PROC));

        } else if ("kill".equals(column)) {
            res = act.processDefId == null || (act.param & ACT_CANCEL) == ACT_CANCEL || (act.param & ACT_ERR) == ACT_ERR || !isProcessRunning(act.flowId);
        }
        return res;
    }

    public void setPermitPerform(long flowId, boolean permit) {
        Activity act = model.getActivity(flowId);
        if (act == null)
            return;
        if (permit && (act.param & ACT_PERMIT) != ACT_PERMIT) {
            act.param |= ACT_PERMIT;
        } else if (!permit && (act.param & ACT_PERMIT) == ACT_PERMIT) {
            act.param ^= ACT_PERMIT;
        }
    }

    public Activity getAutoAct() {
        return auto_act_open;
    }

    public void setAutoAct(Activity act) {
        aAct = act;
    }

    public void setAutoActOpen(Activity act) {
        auto_act_open = act;
    }

    public void closeAutoAct() {
        auto_act_open = null;
        aAct = null;
    }

    public void addAutoActivity(Activity act) {
        autoIfcSet_.add(act.flowId);
    }

    public void setLangId(long langId, boolean par) {
        this.langId = langId;
        if (par) {
            for (int i = 0; i < model.data.size(); ++i) {
                Activity act = model.data.get(i);
                reloadTask(act.flowId, act.ui.id > 0 && act.infUi.id > 0 ? 2 : act.infUi.id > 0 ? 1 : 0, false, false);
            }
        }
    }

    public class TaskTableModel implements WebTableModel {
        private Vector<Activity> data;
        private int selectedColumn = -1;
        private List<Activity> autoStartedRows;
        private List<Activity> openUIRows;

        private boolean isSortAcs = true;
        private int sortColumn = 7;

        public TaskTableModel(Vector<Activity> data) {
            this.data = (data != null) ? data : new Vector<Activity>();
            autoStartedRows = new ArrayList<Activity>();
            openUIRows = new ArrayList<Activity>();
            if (data != null && data.size() > 0)
                sortData();
            loadProcess();
        }

        public int getRowCount() {
            return data.size();
        }

        public String getRowId(int row) {
            Activity act = (data.size() > row && row > -1) ? data.get(row) : null;
            return (act != null) ? String.valueOf(act.flowId) : "";
        }

        public int getColumnCount() {
            return COL_NAMES_RU.length;
        }

        public String getColumnWidth(int columnIndex) {
            return COL_WIDTHS[columnIndex];
        }

        public String getColumnName(int columnIndex) {
            if (langId == LangHelper.getKazLang(session.getConfigNumber()).obj.id) {
                return COL_NAMES_KZ[columnIndex];
            } else
                return COL_NAMES_RU[columnIndex];
        }

        public String getColumnAlign(int columnIndex) {
            return COL_ALIGNS[columnIndex];
        }

        public String getColumnIconName(int columnIndex) {
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return getValueAt(rowIndex, COLS[columnIndex]);
        }

        public Object getValueAt(int rowIndex, String columnName) {
            Activity act = data.size() > rowIndex ? data.get(rowIndex) : null;
            if (act == null)
                return null;
            if ("processName".equals(columnName)) {
                if (act.processDefId != null && act.processDefId.length > 0)
                    return getProcessTitle(act.processDefId[0], langId);
            } else if ("objectName".equals(columnName)) {
                return act.titles.length > 1 ? act.titles[1] : "";
            } else if ("taskName".equals(columnName)) {
                return act.titles.length > 0 ? act.titles[0] : "";
            } else if ("date".equals(columnName)) {
                if (act.date != null)
                    return dateFormat.format(act.date);
            } else if ("time".equals(columnName)) {
                if (act.date != null)
                    return timeFormat.format(act.date);
            } else if ("dateControl".equals(columnName)) {
                if (act.controlDate == null)
                    return session.getResource().getString("mask");
                else
                    return dateFormat.format(act.controlDate);
            } else if ("from".equals(columnName)) {
                return act.userFrom;
            } else if ("initiator".equals(columnName)) {
                return act.userInit;
            }

            return null;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        public Class<?> getColumnClass(int columnIndex) {
            return Object.class;
        }

        public void setSelectedObject(Object obj) {
        }

        @Override
        public boolean isRowFontColorCalc() {
            return false;
        }

        @Override
        public boolean isRowBackColorCalc() {
            return false;
        }

        @Override
        public Color getRowBgColor(int index) {
            return null;
        }

        @Override
        public Color getRowFontColor(int index) {
            return null;
        }

        @Override
        public Font getFont(int row, int col) {
            return null;
        }

        @Override
        public boolean isFontColorCalculated(int col) {
            return false;
        }

        @Override
        public Color getColumnFontColor(int row, int col) {
            return null;
        }

        public int getRowForObject(Object obj) {
            if (obj instanceof Activity) {
                for (int i = 0; i < data.size(); ++i) {
                    if (data.get(i).flowId == ((Activity) obj).flowId) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public Activity getActivityByRow(int row) {
            if (row > -1 && row < data.size())
                return data.get(row);
            else
                return null;
        }

        public int getSelectedRow() {
            return -1;
        }

        public int getSelectedColumn() {
            return selectedColumn;
        }

        public void setSelectedColumn(int col) {
            selectedColumn = col;
        }

        public void addActivity(Activity act) {
            data.add(act);
            session.sendMultipleCommand("addTask", activityAsJson(act));
            if (act != null && act.flowId > 0 && act.processDefId != null && act.processDefId.length > 0) {
            	for (long defId : act.processDefId)
            		fireProcessChanged(0, defId, act.flowId);
            }
        }

        public Activity getActivity(long flowId) {
            int sz = data.size();
            for (int i = 0; i < sz; ++i) {
                Activity a = data.get(i);
                if (a != null && a.flowId == flowId) {
                    return a;
                }
            }
            return null;
        }

        public Activity getActivityByRootFlowId(long flowId) {
            int sz = data.size();
            for (int i = 0; i < sz; ++i) {
                Activity a = data.get(i);
                if (a != null && a.rootFlowId == flowId && a.flowId != flowId) {
                    return a;
                }
            }
            return null;
        }
        public Activity getActivityBySuperFlowId(long flowId) {
            int sz = data.size();
            for (int i = 0; i < sz; ++i) {
                Activity a = data.get(i);
                if (a != null && Funcs.indexOf(flowId, a.superFlowIds) != -1) {
                    return a;
                }
            }
            return null;
        }

        public void reloadActivity(long flowId, boolean isStartAutoAct, long ifsPar) {
            try {
                if (flowId <= 0) {
                    return;
                }
                Activity act = getActivity(flowId);
                Activity act_ = session.getKernel().getTask(flowId, ifsPar, true, true);
                if (act_.flowId == -1) {
                    act_ = new Activity();
                    act_.flowId = flowId;
                    if (act != null) {
                        deleteRow(act);

                        if(act.openArh!=null){
                        	openIfc(act);
                        }
                        Long fId = act.flowId;
                        if (flowMap_.containsKey(fId))
                            flowMap_.remove(fId);
                        if (subFlowMap_.containsKey(fId))
                            subFlowMap_.remove(fId);
                        act.article = null;
                        act.articleLang = null;
                        act = null;
                    }
                } else if (act != null) {
                    updateRow(act, act_, isStartAutoAct);
                } else if (act_ != null) {
                    addRow(data.size(), act_, flowId);
                    act = act_;
                }
                if (act != null && (act.flowId == autoIfcFlowId_ 
                		|| act.rootFlowId == autoIfcFlowId_ 
                		|| Funcs.indexOf(autoIfcFlowId_, act.superFlowIds) != -1)) {
                    aAct = act;
                }
                if (act != null && autoIfcSet_.contains(act.flowId))
                    autoIfcSet_.remove(act.flowId);
                if (act != null && act.processDefId != null) {
                    Set<String> uids = new HashSet<String>();
                    Map<Long, Element> confXmls = new HashMap<Long, Element>();
                    for (int j = 0; j < act.processDefId.length; ++j) {
                        long pid = act.processDefId[j];
                        if (!processMap_.containsKey(pid + "_" + langId)) {
                            confXmls.put(pid, preloadProcessUids(pid, uids));
                        }
                    }
                    session.getKernel().addToCache(uids);
                    for (int j = 0; j < act.processDefId.length; ++j) {
                        getProcessTitle(act.processDefId[j], langId);
                    }
                }
                if (aAct != null) {
                    aAct.autoIfc = !session.getKernel().getUser().isMonitor();
                }
            } catch (Exception e) {
            	if (session != null && session.getKernel() != null)
            		log.error(e, e);
            }
        }

        private void updateRow(Activity oldAct, Activity newAct, boolean isStartAutoAct) {
        	Activity oldActSuper=null;
            oldAct.titles = newAct.titles;
            oldAct.userFrom = newAct.userFrom;
            oldAct.userInit = newAct.userInit;
            oldAct.param = newAct.param;

            if (!activitiesInProcess.contains(oldAct.flowId))
            	oldAct.setUI(newAct.ui);
            else if (newAct.timeActive <= 0) //(newAct.ui == null || newAct.ui.id <= 0)
            	activitiesInProcess.remove(newAct.flowId);

            oldAct.nodesId = newAct.nodesId;

            oldAct.uiType = newAct.uiType;
            oldAct.objs = newAct.objs;
            oldAct.uiFlag = newAct.uiFlag;
            oldAct.infUi = newAct.infUi;
            oldAct.uiTypeInf = newAct.uiTypeInf;
            oldAct.infObjs = newAct.infObjs;

            oldAct.date = newAct.date;
            oldAct.controlDate = newAct.controlDate;
            oldAct.msg = newAct.msg;
            oldAct.trId = newAct.trId;
            oldAct.article = newAct.article;
            oldAct.articleLang = newAct.articleLang;
            oldAct.color = newAct.color;
            if(oldAct.superFlowIds !=null && oldAct.superFlowIds.length>0) {
            	oldActSuper=getActivity(oldAct.superFlowIds[0]);
            }
            if (oldAct.processDefId == null) {
                createMap.remove(new Long(oldAct.flowId));
                oldAct.processDefId = newAct.processDefId;
            }
            if (isStartAutoAct && autoIfcSet_.contains(oldAct.flowId) && aAct == null && (oldAct.param & ACT_AUTO) == ACT_AUTO) {
                aAct = oldAct;
            } else if (aAct != null && aAct == oldAct && (oldAct.param & ACT_AUTO) != ACT_AUTO) {
                aAct = null;
            }
            oldAct.transitions = newAct.transitions;
            oldAct.autoIfc = newAct.autoIfc;
            
            oldAct.timeActive=newAct.timeActive;
            if (aAct!=null) {
                aAct.autoIfc = !session.getKernel().getUser().isMonitor();
            }
            // Если ждем ошибку (после перехода на след шаг) и есть ошибка процесса или суперпроцесса
            if (oldAct.error_msg == ACT_WAITING_ERROR
            		&& 	(
            				(oldAct.param & ACT_ERR) > 0 
            			|| 	(oldActSuper != null && (oldActSuper.param & ACT_ERR) > 0)
            			)) {
            	oldAct.error_msg = ACT_HAS_ERROR;
            	aAct = oldAct;
            }
            addUpdatedRow(oldAct);
        }

        private void addRow(int ind, Activity act_, long flowId) throws KrnException {
            if (data.size() > 0 && ind > -1 && data.size() >= ind)
                data.add(ind, act_);
            else
                data.add(act_);

            Set<String> uids = new HashSet<String>();
            Map<Long, Element> confXmls = new HashMap<Long, Element>();
            for (int j = 0; j < act_.processDefId.length; ++j) {
                long pid = act_.processDefId[j];
                if (!processMap_.containsKey(pid + "_" + langId)) {
                    confXmls.put(pid, preloadProcessUids(pid, uids));
                }
            }
            session.getKernel().addToCache(uids);

            for (int j = 0; j < act_.processDefId.length; ++j) {
                getProcessTitle(act_.processDefId[j], langId);
            }
            if (act_.processDefId.length > 1) {
                flowMap_.put(new Long(act_.flowId), new Pair<long[], long[][]>(new long[] { act_.flowId, act_.processDefId[0] },
                        act_.nodesId));
                SuperMap[] sm = session.getKernel().getMapList(new long[] { flowId });
                for (int i = 0; i < sm.length; ++i) {
                    flowMap_.put(new Long(sm[i].flowId), new Pair<long[], long[][]>(
                            new long[] { sm[i].flowId, sm[i].processDefId }, sm[i].nodes));
                    superFlowMap_.put(new Long(sm[i].subflowId), new Pair<long[], long[][]>(new long[] { sm[i].flowId,
                            sm[i].processDefId }, sm[i].nodes));
                    if (sm[i].subflowId == act_.flowId && autoIfcSet_.contains(sm[sm.length - 1].flowId)) {
                        autoIfcSet_.add(sm[i].subflowId);
                    }
                }
                Pair<long[], long[][]> p = superFlowMap_.get(new Long(act_.flowId));
                if(p!=null){
                	Activity super_act = createMap.get(new Long(p.first[0]));
                	if (super_act != null && super_act.processDefId == null) {
	                    createMap.remove(new Long(super_act.flowId));
	                    deleteRow(super_act);
	                }
	                for (int i = 0; i < sm.length; ++i) {
	                    subFlowMap_.put(new Long(sm[i].flowId), flowMap_.get(new Long(sm[i].subflowId)));
	                }
                }
            }
            if (aAct == null && (autoIfcSet_.contains(act_.flowId)) && (act_.param & ACT_AUTO) == ACT_AUTO) {
                aAct = act_;
            }

            if (lastSuperFlowId_ > -1 && act_.processDefId.length > 1 && act_.processDefId[1] == lastSuperFlowId_) {
                lastSuperFlowId_ = -1;
            }
            
            if (session.tasksRefreshing) {
            	session.sendMultipleCommand("addTask", activityAsJson(act_));
            } else {
            	session.tasksToRefreshing.add(new Object[] {"addTask", activityAsJson(act_)});
            }

            if (act_ != null && act_.flowId > 0 && act_.processDefId != null && act_.processDefId.length > 0) {
            	for (long defId : act_.processDefId)
            		fireProcessChanged(0, defId, act_.flowId);
            }
        }

        private void deleteRow(Activity a) {
        	activitiesInProcess.remove(a.flowId);
            if(data.remove(a)){
	            if (session.tasksRefreshing) {
	            	session.sendMultipleCommand("deleteTask", a.getFlowId());
	            } else {
	            	session.tasksToRefreshing.add(new Object[] {"deleteTask", a.getFlowId()});
	            }
	            if (a != null && a.flowId > 0 && a.processDefId != null && a.processDefId.length > 0) {
	            	for (long defId : a.processDefId)
	            		fireProcessChanged(1, defId, a.flowId);
	            }
            }
        }

        private void openIfc(Activity act) {
        	if(act!=null)
        		session.sendMultipleCommand("openArh", act.openArh.uid);
        }
        public void addAutoStartRow(Activity act) {
            if (!autoStartedRows.contains(act))
                autoStartedRows.add(act);
        }

        public void addOpenUIRow(Activity act, boolean nextStep) {
            if (!openUIRows.contains(act)) {
                openUIRows.add(act);
            }
            if (nextStep)
            	session.sendCommand("next_ui", String.valueOf(act.flowId));
            else
            	session.sendCommand("start_ui", String.valueOf(act.flowId));
        }
        
        public void removeOpenUIRow(Activity act) {
            openUIRows.remove(act);
        }

        public void addUpdatedRow(Activity act) {
        	if (session.tasksRefreshing) {
                session.sendMultipleCommand("updateTask", activityAsJson(act));
            } else {
            	session.tasksToRefreshing.add(new Object[] {"updateTask", activityAsJson(act)});
            }
        }

        public List<Activity> getAutoStartedRows() {
            return autoStartedRows;
        }

        public List<Activity> getOpenUIRows() {
            return openUIRows;
        }

        public void sortData() {
            Collections.sort(data, new TaskComparator(sortColumn, isSortAcs, langId));
        }

        public void loadProcess() {
            // Загрузка процессов
            for (Activity act : data) {
                if (act == null || act.processDefId == null)
                    continue;
                for (int j = 0; j < act.processDefId.length; ++j) {
                    getProcessTitle(act.processDefId[j], langId);
                }
            }
        }
    }

    public String getProcessTitle(long processId, long langId) {
        // Загрузка процессов
        try {
            String key = processId + "_" + langId;
            String title = processMap_.get(key);
            if (title == null) {
                KrnObject proc = new KrnObject(processId, "", Kernel.SC_PROCESS_DEF.id);
                String[] titles = session.getKernel().getStrings(proc, "title", langId, 0);
                title = (titles != null && titles.length > 0) ? titles[0] : session.getResource().getString("noname");

                synchronized (processMap_) {
                    processMap_.put(key, title);
                }
            }
            return title;
        } catch (KrnException e) {
            log.error(e, e);
        }
        return session.getResource().getString("noname");
    }

    class TaskComparator implements Comparator<Activity> {

        protected int sortColumn;
        protected boolean isSortAsc;
        protected long langId;

        public TaskComparator(int sortColumn, boolean sortAsc, long langId) {
            this.sortColumn = sortColumn;
            this.isSortAsc = sortAsc;
            this.langId = langId;
        }

        public int compare(Activity a1, Activity a2) {
            int res = 0;
            if (a1 != null && a2 != null) {
                String columnName = COLS[sortColumn];

                if ("processName".equals(columnName)) {
                    String p1 = a1.processDefId.length > 0 ? getProcessTitle(a1.processDefId[a1.processDefId.length - 1], langId) : "";
                    String p2 = a2.processDefId.length > 0 ? getProcessTitle(a2.processDefId[a2.processDefId.length - 1], langId) : "";
                    res = p1.compareTo(p2);
                } else if ("objectName".equals(columnName)) {
                    String s1 = a1.titles[1];
                    String s2 = a2.titles[1];
                    res = compareWithNulls(s1, s2);
                } else if ("taskName".equals(columnName)) {
                    String s1 = a1.titles[0];
                    String s2 = a2.titles[0];
                    res = compareWithNulls(s1, s2);
                } else if ("date".equals(columnName)) {
                    Date d1 = a1.date;
                    Date d2 = a2.date;
                    res = compareWithNulls(d1, d2);
                } else if ("time".equals(columnName)) {
                    Date t1 = a1.date;
                    Date t2 = a2.date;
                    res = compareWithNulls(t1, t2);
                } else if ("dateControl".equals(columnName)) {
                    Date c1 = a1.controlDate;
                    Date c2 = a2.controlDate;
                    res = compareWithNulls(c1, c2);
                } else if ("from".equals(columnName)) {
                    String uf1 = a1.userFrom;
                    String uf2 = a2.userFrom;
                    res = uf1.compareTo(uf2);
                } else if ("initiator".equals(columnName)) {
                    String ui1 = a1.userInit;
                    String ui2 = a2.userInit;
                    res = compareWithNulls(ui1, ui2);
                } else {
                    res = isEnable(a1, columnName) ? !isEnable(a2, columnName) ? 1 : 0 : isEnable(a2, columnName) ? -1 : 0;
                }
                if (!isSortAsc) {
                    res = -res;
                }
            }
            return res;
        }
        
        private <T extends Comparable<T>> int compareWithNulls(T d1, T d2) {
            return d1 == null ? (d2 == null ? 0 : 1) : (d2 == null ? -1 : d1.compareTo(d2));
        }

        public boolean equals(Object obj) {
            if (obj instanceof TaskComparator) {
                TaskComparator compObj = (TaskComparator) obj;
                return (compObj.sortColumn == sortColumn) && (compObj.isSortAsc == isSortAsc);
            }
            return false;
        }
    }

    private class TaskTableRenderer implements WebTableCellRenderer {
        public String getTableCellRendererString(WebTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int col) {
            String className = null;
            String fgColor = null;
            String bgColor = null;
            String icon = null;
            String toolTip = null;
            String text = "";
            // boolean bold = false;
            String alignment = null;
            String onclick = null;
            String cursor = null;

            String column = COLS[col];

            /*
             * if (!isSelected) {
             * bgColor = "#cccccc";
             * } else {
             * bgColor = "#d8dde7";
             * }
             */
            TaskTableModel taskModel = (TaskTableModel) table.getModel();
            Activity act = taskModel.getActivityByRow(row);
            if (act != null) {
                if ((act.param & ACT_ALARM) == ACT_ALARM) {
                    fgColor = "red";
                } else if ((act.param & ACT_ALERT) == ACT_ALERT) {
                    fgColor = "#46516a";
                } else {
                    fgColor = "black";
                }

                if (!isSelected) {
                    if (act.color != 0)
                        bgColor = colorToString(new Color((int) act.color));
                }

                if ("svetofor".equals(column)) {
                    if ((act.param & ACT_ERR) == ACT_ERR)
                        icon = "red";
                    else if ((act.processDefId == null || (act.param & ACT_PERMIT) != ACT_PERMIT || (act.param & ACT_IN_BOX) == ACT_IN_BOX)
                            && (act.param & ACT_ARTICLE) != ACT_ARTICLE && (act.param & ACT_FASTREPORT) != ACT_FASTREPORT)
                        icon = "yellow";
                    else
                        icon = "green";

                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < COLS.length; i++) {
                        String name = COLS[i];
                        if (!"svetofor".equals(name) && !"open".equals(name) && !"openInspector".equals(name)
                                && !"nextStep".equals(name) && !"kill".equals(name))
                            sb.append(taskModel.getColumnName(i)).append(" : ").append(taskModel.getValueAt(row, i))
                                    .append("\r\n");
                    }

                    toolTip = sb.toString();
                    toolTip = Funcs.xmlQuote(toolTip);

                } else if ("processName".equals(column) || "objectName".equals(column) || "taskName".equals(column)
                        || "from".equals(column) || "initiator".equals(column)) {
                    text = value != null ? value.toString() : "";
                    if ("objectName".equals(column) && act.titles.length > 2) {
                        toolTip = act.titles[2].length() > 0 ? getMultiLine(act.titles[2]) : null;
                    }
                    toolTip = Funcs.xmlQuote(toolTip);

                } else if ("date".equals(column) || "time".equals(column) || "dateControl".equals(column)) {
                    if ("dateControl".equals(column)) {
                        fgColor = "darkRed";
                        className = "col10";
                    }
                    alignment = "center";
                    text = (value != null) ? value.toString() : "";
                } else if ("open".equals(column)) {
                    alignment = "center";

                    toolTip = session.getResource().getString("openInterfaceTip");

                    if (isEnable(act, column)) {
                        if ((act.param & ACT_FASTREPORT) == ACT_FASTREPORT) {
                            onclick = "showFastReport(this);";
                            if ("daulet".equals(WebController.THEME)) {
                                if ("KZ".equals(LangItem.getById(langId).code))
                                    icon = "FormTabKZ6";
                                else
                                    icon = "FormTab6";
                            } else {
                                icon = "FormTab";
                            }
                        } else if ((act.param & ACT_DIALOG) == ACT_DIALOG) {
                            // toolTip = "Открыть диалоговое окно";
                            // onclick = "openInterface('" + act.flowId + "', true);";
                            onclick = "openInterface(this);";
                            if ("daulet".equals(WebController.THEME)) {
                                if ("KZ".equals(LangItem.getById(langId).code))
                                    icon = "FormTabKZ6";
                                else
                                    icon = "FormTab6";
                            } else {
                                icon = "FormTab";
                            }
                        } else if ((act.param & ACT_ARTICLE) == ACT_ARTICLE) {
                            toolTip = session.getResource().getString("openReportTip");
                            onclick = "showProcessReport(this);";
                            if ("daulet".equals(WebController.THEME)) {
                                if ("KZ".equals(LangItem.getById(langId).code))
                                    icon = "FormTabKZ6";
                                else
                                    icon = "FormTab6";
                            } else {
                                icon = "FormTab";
                            }
                        } else {
                            // onclick = "openInterface('" + act.flowId + "', false);";
                            onclick = "openInterface(this);";
                            if ("daulet".equals(WebController.THEME)) {
                                if ("KZ".equals(LangItem.getById(langId).code))
                                    icon = "FormTabKZ6";
                                else
                                    icon = "FormTab6";
                            } else {
                                icon = "FormTab";
                            }
                        }
                        cursor = "pointer";
                    } else {
                        toolTip = session.getResource().getString("ifcNotExistMessage");
                        if ("daulet".equals(WebController.THEME)) {
                            if ("KZ".equals(LangItem.getById(langId).code))
                                icon = "FormTabDisKZ6";
                            else
                                icon = "FormTabDis6";
                        } else {
                            icon = isFC ? "FormTabDisFC.png" : "FormTabDis";
                        }
                    }
                } else if ("openInspector".equals(column)) {
                    alignment = "center";

                    toolTip = session.getResource().getString("openInterfaceTip");

                    if (isEnable(act, column)) {
                        onclick = "openControlInterface(this);";
                        icon = "Tree";
                        cursor = "pointer";
                    } else {
                        toolTip = session.getResource().getString("ifcNotExistMessage");
                        icon = null;
                    }

                } else if ("nextStep".equals(column)) {
                    alignment = "center";
                    toolTip = session.getResource().getString("nextStepTip");
                    if (isEnable(act, column)) {
                        if ("daulet".equals(WebController.THEME)) {
                            if ("KZ".equals(LangItem.getById(langId).code))
                                icon = "actionRunKZ2";
                            else
                                icon = "actionRun2";
                        } else {
                            icon = "actionRun";
                        }
                        // onclick = "nextStep('" + act.flowId + "');";
                        onclick = new StringBuilder("nextStep(this, '")
                                .append(session.getResource().getString("nextStepMessage")).append("');").toString();
                        cursor = "pointer";
                    } else if ("daulet".equals(WebController.THEME)) {
                        if ("KZ".equals(LangItem.getById(langId).code))
                            icon = "actionRunDisKZ2";
                        else
                            icon = "actionRunDis2";
                    } else {
                        icon = "actionRunDis";
                    }

                } else if ("kill".equals(column)) {
                    alignment = "center";
                    toolTip = session.getResource().getString("killProcessTip");
                    if (isEnable(act, column)) {
                        if ("daulet".equals(WebController.THEME)) {
                            icon = "DeleteProc3";
                        } else {
                            icon = "DeleteProc";
                        }
                        // onclick = "killProcess('" + act.flowId + "');";
                        onclick = new StringBuilder("killProcess(this, '")
                                .append(session.getResource().getString("killProcMessage")).append("');").toString();
                        cursor = "pointer";
                    } else {
                        if ("daulet".equals(WebController.THEME)) {
                            icon = "DeleteProcDis3";
                        } else {
                            icon = "DeleteProcDis";
                        }
                    }
                }
            }
            StringBuilder out = new StringBuilder("<td");
            if (alignment != null)
                out.append(" align=\"").append(alignment).append("\"");
            if (toolTip != null)
                out.append(" title=\"").append(toolTip).append("\"");
            if (onclick != null)
                out.append(" onclick=\"").append(onclick).append("\"");
            if ((act.param & ACT_DIALOG) == ACT_DIALOG || (act.param & ACT_AUTO) == ACT_AUTO) {
                out.append(" dialog=\"true\"");
            } else {
                out.append(" dialog=\"false\"");
            }

            if (isSelected)
                className = "selected";
            if (className != null) {
                out.append(" class=\"");
                out.append(className);
                out.append("\"");
            }
            if (fgColor != null || bgColor != null // || bold
                    || cursor != null) {
                out.append(" style=\"");
                if (fgColor != null)
                    out.append("color: ").append(fgColor).append("; ");
                if (bgColor != null)
                    out.append("background-color: ").append(bgColor).append("; ");
                if (cursor != null)
                    out.append("cursor: ").append(cursor).append("; ");
                // if (bold)
                // out.append("font-weight: bold;");

                out.append("\"");
            }

            out.append(">");
            if (icon != null) {
                String ext;
                final Matcher M = Pattern.compile("\\.JPG$|\\.JPEG$|\\.GIF$|\\.PNG$").matcher(icon.toUpperCase(Constants.OK));
                ext = (M.find()) ? "" :".gif";
                
                out.append("<img src=\"").append(WebController.APP_PATH).append("/images/").append(icon).append(ext).append("\" />");
            }
            out.append(Funcs.sanitizeHtml(text));
            out.append("</td>").append(ServletUtilities.EOL);
            return out.toString();
        }

        public String getTableCellRendererStringJq(WebTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int col) {
            String className = null;
            String fgColor = null;
            String icon = null;
            String toolTip = null;
            String text = "";
            // boolean bold = false;
            String alignment = null;
            String onclick = null;
            String cursor = null;
            String column = COLS[col];

            /*
             * if (!isSelected) {
             * bgColor = "#cccccc";
             * } else {
             * bgColor = "#d8dde7";
             * }
             */
            TaskTableModel taskModel = (TaskTableModel) table.getModel();
            Activity act = taskModel.getActivityByRow(row);
            if (act != null) {
                if ((act.param & ACT_ALARM) == ACT_ALARM) {
                    fgColor = "red";
                } else if ((act.param & ACT_ALERT) == ACT_ALERT) {
                    fgColor = "#46516a";
                } else {
                    fgColor = "black";
                }
                if ("svetofor".equals(column)) {
                    if ((act.param & ACT_ERR) == ACT_ERR)
                        icon = "red";
                    else if ((act.processDefId == null || (act.param & ACT_PERMIT) != ACT_PERMIT || (act.param & ACT_IN_BOX) == ACT_IN_BOX)
                            && (act.param & ACT_ARTICLE) != ACT_ARTICLE && (act.param & ACT_FASTREPORT) != ACT_FASTREPORT)
                        icon = "yellow";
                    else
                        icon = "green";

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < COLS.length; i++) {
                        String name = COLS[i];
                        if (!"svetofor".equals(name) && !"open".equals(name) && !"openInspector".equals(name)
                                && !"nextStep".equals(name) && !"kill".equals(name))
                            sb.append(taskModel.getColumnName(i)).append(":").append(taskModel.getValueAt(row, i)).append("\n");
                    }

                    toolTip = sb.toString();
                    toolTip = Funcs.xmlQuote(toolTip);
                } else if ("processName".equals(column) || "objectName".equals(column) || "taskName".equals(column)
                        || "from".equals(column) || "initiator".equals(column)) {
                    text = value != null ? value.toString() : "";
                    if ("objectName".equals(column) && act.titles.length > 2) {
                        toolTip = act.titles[2].length() > 0 ? getMultiLine(act.titles[2]) : null;
                    }
                    toolTip = Funcs.xmlQuote(toolTip);
                } else if ("date".equals(column) || "time".equals(column) || "dateControl".equals(column)) {
                    if ("dateControl".equals(column)) {
                        fgColor = "darkRed";
                        className = "col10";
                    }
                    alignment = "center";
                    text = (value != null) ? value.toString() : "";
                } else if ("open".equals(column)) {
                    alignment = "center";

                    toolTip = session.getResource().getString("openInterfaceTip");

                    if (isEnable(act, column)) {
                        if ((act.param & ACT_DIALOG) == ACT_DIALOG) {
                            // toolTip = "Открыть диалоговое окно";
                            // onclick = "openInterface('" + act.flowId + "', true);";
                            onclick = "openInterface(this);";
                            icon = "FormTab";
                        } else if ((act.param & ACT_ARTICLE) == ACT_ARTICLE) {
                            toolTip = session.getResource().getString("openReportTip");
                            onclick = "showProcessReport(this);";
                            icon = "FormTab";
                        } else {
                            // onclick = "openInterface('" + act.flowId + "', false);";
                            onclick = "openInterface(this);";
                            icon = "FormTab";
                        }
                        cursor = "pointer";
                    } else {
                        toolTip = session.getResource().getString("ifcNotExistMessage");
                        icon = isFC ? "FormTabDisFC.png" : "FormTabDis";
                    }
                } else if ("openInspector".equals(column)) {
                    alignment = "center";

                    toolTip = session.getResource().getString("openInterfaceTip");

                    if (isEnable(act, column)) {
                        onclick = "openControlInterface(this);";
                        icon = "Tree";
                        cursor = "pointer";
                    } else {
                        toolTip = session.getResource().getString("ifcNotExistMessage");
                        icon = null;
                    }

                } else if ("nextStep".equals(column)) {
                    alignment = "center";
                    toolTip = session.getResource().getString("nextStepTip");
                    if (isEnable(act, column)) {
                        icon = "actionRun";
                        // onclick = "nextStep('" + act.flowId + "');";
                        onclick = new StringBuilder("nextStep(this, '")
                                .append(session.getResource().getString("nextStepMessage")).append("');").toString();
                        cursor = "pointer";
                    } else
                        icon = "actionRunDis";

                } else if ("kill".equals(column)) {
                    alignment = "center";
                    toolTip = session.getResource().getString("killProcessTip");
                    if (isEnable(act, column)) {
                        icon = "DeleteProc";
                        // onclick = "killProcess('" + act.flowId + "');";
                        onclick = new StringBuilder("killProcess(this, '")
                                .append(session.getResource().getString("killProcMessage")).append("');").toString();
                        cursor = "pointer";
                    } else {
                        icon = "DeleteProcDis";
                    }
                }
            }
            StringBuilder out = new StringBuilder("<span");
            if (alignment != null)
                out.append(" align=\"").append(alignment).append("\"");
            if (toolTip != null)
                out.append(" title=\"").append(toolTip).append("\"");
            if (onclick != null)
                out.append(" onclick=\"").append(onclick).append("\"");
            if ((act.param & ACT_DIALOG) == ACT_DIALOG || (act.param & ACT_AUTO) == ACT_AUTO) {
                out.append(" dialog=\"true\"");
            } else {
                out.append(" dialog=\"false\"");
            }

            if (isSelected)
                className = "selected";
            if (className != null) {
                out.append(" class=\"");
                out.append(className);
                out.append("\"");
            } else if (fgColor != null// || bgColor != null // || bold
                    || cursor != null) {
                out.append(" style=\"");
                if (fgColor != null)
                    out.append("color: ").append(fgColor).append("; ");
                if (cursor != null)
                    out.append("cursor: ").append(cursor).append("; ");
                // if (bold)
                // out.append("font-weight: bold;");

                out.append("\"");
            }

            out.append(">");
            if (icon != null) {
                out.append("<img src=\"").append(WebController.APP_PATH).append("/images/").append(icon).append(".gif\" />");
            }
            out.append(Funcs.sanitizeHtml(text));
            out.append("</span>").append(ServletUtilities.EOL);
            return out.toString();
        }

        private String getMultiLine(String src) {
            String strResultCurrent = "";
            strResultCurrent = src.replaceAll("\\\\n", "\n");
            return strResultCurrent.trim();
        }
    }

    public void taskReload(long flowId, long ifsPar) {
        new Thread(new ClientUpdater(flowId, ifsPar)).start();
    }

    private class ClientUpdater implements Runnable {
        private long flowId;
        private long ifsPar;

        public ClientUpdater(long flowId, long ifsPar) {
            this.flowId = flowId;
            this.ifsPar = ifsPar;

        }

        public void run() {
            if (isRanning && session != null) {
            	String userName = session.getUserName();
                reloadTask(flowId, ifsPar, true, false);
                log.info("|USER: " + userName + "| Tasks reloaded");
            }
        }
    }

    public void setReportComplete(long flowId) {
        reportSet.remove(flowId);
    }

    private String colorToString(Color bg) {
        if (bg != null) {
            StringBuilder temp = new StringBuilder(27);
            temp.append("#");
            String code = Integer.toHexString(bg.getRed());
            if (code.length() < 2) {
                temp.append("0");
            }
            temp.append(code);
            code = Integer.toHexString(bg.getGreen());
            if (code.length() < 2) {
                temp.append("0");
            }
            temp.append(code);
            code = Integer.toHexString(bg.getBlue());
            if (code.length() < 2) {
                temp.append("0");
            }
            temp.append(code);

            return temp.toString();
        }
        return null;
    }

    private Element preloadProcessUids(long pid, Collection<String> uids) {
        KrnObject proc = new KrnObject(pid, "", Kernel.SC_PROCESS_DEF.id);
        try {
            byte[] data_ = session.getKernel().getBlob(proc.id,
                    session.getKernel().getAttributeByName(Kernel.SC_PROCESS_DEF, "diagram"), 0, 0, 0);
            InputStream is = new ByteArrayInputStream(data_);
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Element xml = builder.build(is).getRootElement();
            is.close();
            // Загрузка ссылок на KrnObject в кэш
            XPath xp = XPath.newInstance("//property[starts-with(@name,'KRN')]");
            List<Element> elems = xp.selectNodes(xml);
            for (Element elem : elems) {
                uids.add(elem.getText());
            }
            return xml;
        } catch (Exception ex) {
        	if (session != null && session.getKernel() != null)
        		log.error(ex, ex);
        }
        return null;
    }

    /**
     * @return the isAutoAct
     */
    public boolean isAutoAct() {
        return isAutoAct;
    }

    /**
     * @param isAutoAct the isAutoAct to set
     */
    public void setAutoAct(boolean isAutoAct) {
        this.isAutoAct = isAutoAct;
    }

    @Override
    public void setProgressCaption(String text) {
    }

    @Override
    public void setProgressMinimum(int val) {
    }

    @Override
    public void setProgressMaximum(int val) {
    }

    @Override
    public void setProgressValue(int val) {
    }

    /**
     * @return the autoIfcFlowId_
     */
    public long getAutoIfcFlowId_() {
        return autoIfcFlowId_;
    }

    /**
     * @param autoIfcFlowId_ the autoIfcFlowId_ to set
     */
    public void setAutoIfcFlowId_(long autoIfcFlowId) {
        autoIfcFlowId_ = autoIfcFlowId;
        isAutoAct = false;
    }

    public void clearAutoIfcFlowId(long flowId) {
        if (autoIfcFlowId_ != flowId) {
        	autoIfcFlowId_ = 0;
            isAutoAct = false;
            openUI = null;
        }
    }

    public void setOpenUI(KrnObject openUI) {
        this.openUI = openUI;
    }

    public void checkOpenUI(KrnObject openUI) {
    	if (this.openUI != null && openUI != null && this.openUI.id == openUI.id)
    		this.openUI = null;
    }
    
    public JsonObject activityAsJson(Activity act) {
        JsonObject res = new JsonObject();
        String processName = "";
        if (act.processDefId != null && act.processDefId.length > 0) {
            processName = getProcessTitle(act.processDefId[0], session.getInterfaceLangId());
        }
        String taskName = act.titles.length > 0 ? act.titles[0] : "";
        String objectName = act.titles.length > 0 ? act.titles[1] : "";
        String date = act.date != null ? dateTimeFormat.format(act.date) : "";
        String controlDate = act.controlDate != null ? dateTimeFormat.format(act.controlDate) : "";
        String userBase = act.userBase_name;
        long id = act.flowId;

        JsonObject process = new JsonObject();
        process.add("p", processName);
        process.add("t", taskName);
        process.add("o", objectName);
        process.add("d", date);
        process.add("c", controlDate);
        if(userBase != null)
        	process.add("b", userBase);

        boolean dlg = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType);
        process.add("m", dlg ? "dialog" : "window");

        res.add(String.valueOf(id), process);

        boolean canKill = (act.processDefId == null || (act.param & ACT_CANCEL) == ACT_CANCEL || (act.param & ACT_ERR) == ACT_ERR  || !isProcessRunning(id)) && !activitiesInProcess.contains(id);
        if (!canKill) {
        	// Проверяем, есть ли ошибка на родительском потоке?
        	if (act.superFlowIds != null && act.superFlowIds.length > 0) {
        		for (long superFlowId : act.superFlowIds) {
        			Activity superAct = getActivityById(superFlowId);
        			if (superAct != null && (superAct.param & ACT_ERR) == ACT_ERR && !activitiesInProcess.contains(superFlowId)) {
        				canKill = true;
        				break;
        			}
        		}
        	}
        }
        if (canKill) {
            process.add("k", 1);
        }

        process.add("i", act.userInit);

        if ((act.param & ACT_ALARM) == ACT_ALARM) {
            process.add("c1", "2");
        } else if ((act.param & ACT_ALERT) == ACT_ALERT) {
            process.add("c1", "1");
        } else {
            process.add("c1", "3");
        }

        if ((act.param & ACT_ERR) == ACT_ERR)
            process.add("c2", "2");
        else if ((act.processDefId == null || (act.param & ACT_PERMIT) != ACT_PERMIT || (act.param & ACT_IN_BOX) == ACT_IN_BOX)
                && (act.param & ACT_ARTICLE) != ACT_ARTICLE && (act.param & ACT_FASTREPORT) != ACT_FASTREPORT)
            process.add("c2", "1");
        else
            process.add("c2", "0");

        if (act.color!=0)
            process.add("c3",colorToString(new Color((int) act.color)));
        process.add("op", isEnable(act, "open") ? "1" 
        		:(act.param & ACT_AUTO_NEXT) == ACT_AUTO_NEXT ?"2"
        				:(act.param & ACT_REPORT_REQUIRE) == ACT_REPORT_REQUIRE ?"3": "0");
        return res;
    }
    
	public void addProcessListener(long procId, ProcessListener l) {
		EventListenerList list = listeners_.get(procId);
		if (list == null) {
			list = new EventListenerList();
			listeners_.put(procId, list);
		}
		list.remove(ProcessListener.class, l);
		list.add(ProcessListener.class, l);
	}

	public void removeProcessListener(long procId, ProcessListener l) {
		EventListenerList list = listeners_.get(procId);
		if (list == null) {
			list = new EventListenerList();
			listeners_.put(procId, list);
		}
		list.remove(ProcessListener.class, l);
	}

	private void fireProcessChanged(int type, long defId, long flowId) {
		EventListenerList list = listeners_.get(defId);
		if (list != null) {
			Object[] listeners = list.getListenerList();
			for (int i = 0; i < listeners.length; i += 2) {
				if (listeners[i] == ProcessListener.class) {
					ProcessListener l = (ProcessListener) listeners[i + 1];
					if (type == 0)
						l.processStarted(defId, flowId);
					else if (type == 1)
						l.processEnded(defId, flowId);
				}
			}
		}
	}

	public boolean reloadFlow(long flowId) {
		Activity act = getActivityById(flowId);
		try {
	        boolean res = session.getKernel().reloadFlow(act.flowId);
	        if (res) {
	            reloadTask(act.flowId, act.ui.id > 0 && act.infUi.id > 0 ? 2 : act.infUi.id > 0 ? 1 : 0, false, false);
	            return true;
	        }
		} catch (Exception e) {
			log.error(e, e);
		}
        return false;
	}
	
	public boolean isProcessRunning(long flowId) {
		try {
			return session.getKernel().isProcessRunning(flowId);
		} catch (KrnException e) {
			log.error(e, e);
		}
		return true;
	}
}
