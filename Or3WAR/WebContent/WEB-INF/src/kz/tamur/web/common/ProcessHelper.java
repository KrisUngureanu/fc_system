package kz.tamur.web.common;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.EventType;
import com.cifs.or2.util.MMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Driver2;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.LangHelper.WebLangItem;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebMenu;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.common.webgui.WebProcessMenuItem;
import kz.tamur.web.component.WebFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 26.06.2006
 * Time: 10:58:00
 * To change this template use File | Settings | File Templates.
 */
public class ProcessHelper {
    private static final Log LOG_ = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessHelper.class);

    private WebSession session;
    private SortedSet<ProcessNode> tabs = new TreeSet<ProcessNode>();
    private Long selectedTab;
    private Hashtable<String, Boolean> expandedState;
    private static long ROOT_ID = 0;
    private static String ROOT_UID = null;
    private Log log;
	private static KrnAttribute attrParent;
	private static KrnAttribute attrIndex;
	private static KrnAttribute attrToolbar;
	private static KrnAttribute attrTitle;
	private static KrnAttribute attrHotkey;
	private static KrnAttribute attrIcon;
	private static KrnAttribute attrIsTab;
	private static KrnAttribute attrTabName;
	
	private static KrnAttribute attrConfig;
	private static KrnAttribute attrMessage;
	
	private static KrnClass pdfCls;
	private static KrnClass pdCls;

	private static Boolean listenerInitialized = false;
    private static Map<Long, ProcessNode> processesById = new HashMap<Long, ProcessNode>();
    private static Map<String, ProcessNode> processesByUid = new HashMap<String, ProcessNode>();
	private static Map<Long, List<ProcessNode>> processesByParentId = new HashMap<Long, List<ProcessNode>>();
	private static Map<String, String> parentUidByChildUid = new HashMap<String, String>();
	private static Map<ProcessNode, String> titleRus = new HashMap<ProcessNode, String>();
	private static Map<ProcessNode, String> titleKaz = new HashMap<ProcessNode, String>();
	private List<String> procListKaz = new ArrayList<String>();
	private List<String> procListRus = new ArrayList<String>();

	private static Map<UUID, List<AttrChange>> attrChanges = new HashMap<UUID, List<AttrChange>>();
	private static Log logReload;
	private static ProcessListener listener;
	
	private List<Long> availProcs;

    public ProcessHelper(WebSession s) throws KrnException {
        this.session = s;
    	initListener(session);
        this.log = LogFactory.getLog(session.getKernel().getUserSession().dsName + "."
				+ session.getKernel().getUserSession().logName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());

        expandedState = new Hashtable<String, Boolean>();
        getRootId(session);
        
        if (User.USE_OLD_USER_RIGHTS) {
        	long[] processIds = s.getKernel().getProcessDefinitions();
        	availProcs = new ArrayList<Long>();
        	for (long processId : processIds)
        		availProcs.add(processId);
        }
        else
        	availProcs = session.getKernel().getUserSubjects(SystemAction.ACTION_START_PROCESS, session.getKernel().getUser().getObject().id);
        
        synchronized (processesById) {
        	for (ProcessNode pn : processesById.values()) {
        		if (containsProcess(pn) && pn.isTab()) {
                	tabs.add(pn);
        		}
        	}
		}
        List<WebLangItem> langs = LangHelper.getAll(s.getConfigNumber());
        for (WebLangItem li : langs) {
        	long langId = li.obj.id;
        	Set<ProcessNode> parentProcList = new TreeSet<ProcessNode>(new ProcessNodeComparator(User.USE_OLD_USER_RIGHTS ? 2 : 1, langId, s));
        	parentProcList.addAll(tabs);
        	for(ProcessNode procNode: parentProcList) {
        			getChildren(procNode, langId);
        	}	
        }

    }
    
    public void release() {
    	expandedState.clear();
    	expandedState = null;
    	tabs.clear();
    	tabs = null;
    }

    protected static void load(WebSession s, long[] ids) {
        final Kernel krn = s.getKernel();

        try {
            // дети
            ObjectValue[] ovs = krn.getObjectValues(ids, pdfCls.id, "children", 0);
            
            if (ovs.length > 0) {
            	Map<Long, Long> parentByChild = new HashMap<Long, Long>();
	            long[] childIds = new long[ovs.length];
	            for (int i = 0; i < ovs.length; i++) {
	            	childIds[i] = ovs[i].value.id;
	            	parentByChild.put(childIds[i], ovs[i].objectId);
	            }
	
	            List<WebLangItem> langs = LangHelper.getAll(s.getConfigNumber());
	            
	            AttrRequestBuilder arb = new AttrRequestBuilder(pdfCls, krn).add("parent").add("runtimeIndex").add("isTab").add("isBtnToolBar").add("hotKey").add("icon");
                
	            for (WebLangItem li : langs) {
	            	arb.add("title", li.obj.id);
	            	arb.add("tabName", li.obj.id);
	            }
	            arb.add("message");
	
	            List<Object[]> recs = krn.getObjects(childIds, arb.build(), 0);
                    
                for (Object[] rec : recs) {
                	KrnObject obj = (KrnObject)rec[0];

		        	ProcessNode pn = new ProcessNode(obj);
		            KrnObject parent = (KrnObject) rec[2];
		            if(parent==null) {
		            	logReload = LogFactory.getLog(s.getKernel().getUserSession().dsName + "."
		        				+ s.getKernel().getUserSession().logName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessHelper.class.getName());
		            	logReload.error("PARENT IS NULL for obj.uid=" + obj.uid);
		            	continue;
		            }
		            pn.setParentId(parentByChild.get(obj.id));
		            pn.setParentUid(parent.uid);
		            
		            int index = arb.getIntValue("runtimeIndex", rec);
		            pn.setIndex(index);
		            
		            boolean isTab = arb.getBooleanValue("isTab", rec, false);
		            pn.setIsTab(isTab);

		            long toolBar = arb.getLongValue("isBtnToolBar", rec);
		            pn.setToolbar(toolBar);

		            String hotkey = arb.getStringValue("hotKey", rec);
		            pn.setHotkey(hotkey);
		            
		            byte[] message = (byte[])arb.getValue("message", rec);
		            pn.setMessage(message);

		            byte[] icon = (rec[7] != null) ? (byte[]) rec[7] : null;
		            pn.setIcon(icon);
		            
		            int i = 8;
    	            for (WebLangItem li : langs) {
                    	String title = (rec[i] instanceof String) ? (String) rec[i] : "*";
                        pn.setTitle(title, li.obj.id);
                        if(li.obj.id == 122 && !pn.isFolder)
                        	titleRus.put(pn, title);
                        if(li.obj.id == 123 && !pn.isFolder)
                        	titleKaz.put(pn, title);
                        i++;
                    	String tabName = (rec[i] instanceof String) ? (String) rec[i] : "*";
                        pn.setTabName(tabName, li.obj.id);
                        i++;
    	            }
                    
                    addNode(parentByChild.get(obj.id), pn);

                }
                load(s, childIds);
            }
        } catch (KrnException e) {
        	logReload = LogFactory.getLog(s.getKernel().getUserSession().dsName + "."
    				+ s.getKernel().getUserSession().logName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessHelper.class.getName());
        	logReload.error(e, e);
        }
    }
    
    private void getChildren(ProcessNode pn, long langId) {
    	Set<ProcessNode> leafChilds = new TreeSet<ProcessNode>(User.USE_OLD_USER_RIGHTS ? new ProcessNodeComparator(2, langId, session) : null);
    	Set<ProcessNode> nodeChilds = new TreeSet<ProcessNode>(new ProcessNodeComparator(2, langId, session));
    	for(ProcessNode child: getChildren(pn)) {
    		if(child.isFolder())
    			nodeChilds.add(child);
    		else leafChilds.add(child);
    	}
    	for(ProcessNode child: leafChilds) {
    		if(langId == 123)
    			procListKaz.add(child.obj.uid + ':' + child.titleMap.get(langId));
    		else
    			procListRus.add(child.obj.uid + ':' + child.titleMap.get(langId));
    	}
    	for(ProcessNode child: nodeChilds) {
    		getChildren(child, langId);
    	}
    }

    
    protected static void reload(Session s, long id, ProcessNode n) {
        try {
            List<KrnObject> langs = s.getSystemLangs();
            
            kz.tamur.or3ee.server.kit.AttrRequestBuilder arb = new kz.tamur.or3ee.server.kit.AttrRequestBuilder(pdfCls, s).add("parent").add("runtimeIndex").add("isTab").add("isBtnToolBar").add("hotKey").add("icon");
            
            for (KrnObject li : langs) {
            	arb.add("title", li.id);
            	arb.add("tabName", li.id);
            }
            arb.add("message");

            QueryResult qr = s.getObjects(new long[] {id}, arb.build(), 0);
        	
            for (Object[] rec : qr.rows) {
            	KrnObject obj = arb.getObject(rec);

            	KrnObject pObj = (KrnObject)rec[2];
            	// Если создается новый процесс, то его нет в мапах, нужно его добавить
            	if (n == null){
            		n = new ProcessNode(obj);
        			addNode(pObj != null ? pObj.id : 0, n);
            	}
                n.setParentId(pObj != null ? pObj.id : 0);
                n.setParentUid(pObj != null ? pObj.uid : "");
	            int index = arb.getIntValue("runtimeIndex", rec);
	            n.setIndex(index);
	            
	            boolean isTab = arb.getBooleanValue("isTab", rec, false);
	            n.setIsTab(isTab);

	            long toolBar = arb.getLongValue("isBtnToolBar", rec);
	            n.setToolbar(toolBar);

	            String hotkey = arb.getStringValue("hotKey", rec);
	            n.setHotkey(hotkey);
	            
	            byte[] message = (byte[])arb.getValue("message", rec);
	            n.setMessage(message);

	            byte[] icon = (rec[7] != null) ? (byte[]) rec[7] : null;
	            n.setIcon(icon);
	            
	            int i = 8;
	            for (KrnObject li : langs) {
                	String title = (rec[i] instanceof String) ? (String) rec[i] : "*";
                    n.setTitle(title, li.id);
                    if(li.id == 122 && !n.isFolder)
                    	titleRus.put(n, title);
                    if(li.id == 123 && !n.isFolder)
                    	titleKaz.put(n, title);
                    i++;
                	String tabName = (rec[i] instanceof String) ? (String) rec[i] : "*";
                    n.setTabName(tabName, li.id);
                    i++;
	            }
            }
        } catch (KrnException e) {
        	logReload = LogFactory.getLog(s.getUserSession().getDsName() + "."
    				+ s.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessHelper.class.getName());
        	logReload.error(e, e);
        }
    }
    
    public Map<Integer, List<String>> searchProcesses(String text, long lid) {
    	Map<Integer, List<String>> litsSearchProcess = new HashMap<Integer, List<String>>();
    	int i = 0;
    	try {
    		for (String entry : lid == 123 ? procListKaz : procListRus) {
    			int point = entry.indexOf(':');
    			String nodeUid = entry.substring(0, point);
    			String nodeTitle = entry.substring(point + 1);
    			Kernel krn = session.getKernel();
    			KrnObject krnObj = krn.getObjectByUid(nodeUid, 0);
    			ProcessNode node = new ProcessNode(krnObj);
    			long id = node.obj.id;
    			if (node.obj.classId == Kernel.SC_PROCESS_DEF.id) {
    				if (availProcs == null || availProcs.contains(id)) {
    					if(nodeTitle.toUpperCase(Constants.OK).indexOf(text.toUpperCase(Constants.OK)) > -1) {
    						String temp;
    						List<String> listUIDS = new ArrayList<String>();
    						String uid = nodeUid;
    						listUIDS.add(uid);
    						do{
    							temp = parentUidByChildUid.get(uid);
    							if(!ROOT_UID.equals(temp)) {
    								listUIDS.add(temp);
    								uid = temp;
    							} else {
    								break;
    							}
    						} while(true);
    						litsSearchProcess.put(i, listUIDS);
    						i++;
    					}
    				}
    			} else if (containsProcess(node)) {
    				if(nodeTitle.toUpperCase(Constants.OK).indexOf(text.toUpperCase(Constants.OK)) > -1) {
    					String temp;
    					List<String> listUIDS = new ArrayList<String>();
    					String uid = nodeUid;
    					do{
    						temp = parentUidByChildUid.get(uid);
    						if(!ROOT_UID.equals(temp)) {
    							listUIDS.add(temp);
    							uid = temp;
    						} else {
    							break;
    						}
    					} while(true);
    					litsSearchProcess.put(i, listUIDS);
    					i++;
    				}
    			}
    		}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return litsSearchProcess;
    }

//    public Map<Integer, List<String>> searchProcess(String text, long lid) {
//    	Map<Integer, List<String>> litsSearchProcess = new HashMap<Integer, List<String>>();
//    	int i = 0;
//	    	for (Map.Entry <ProcessNode, String> entry : lid == 123 ? titleKaz.entrySet() : titleRus.entrySet()) {
//	    		ProcessNode node = entry.getKey();
//	    		long id = node.obj.id;
//                if (node.obj.classId == Kernel.SC_PROCESS_DEF.id) {
//                	if (availProcs == null || availProcs.contains(id)) {
//        	            String value = entry.getValue();
//        	            if(value.toUpperCase(Constants.OK).indexOf(text.toUpperCase(Constants.OK)) > -1) {
//        	            	ProcessNode key = entry.getKey();
//        	                String temp;
//        	    	        List<String> listUIDS = new ArrayList<String>();
//        	    	        String uid = key.obj.uid;
//        	    	        listUIDS.add(uid);
//        	    	        do{
//        	    	        	temp = parentUidByChildUid.get(uid);
//        	    	            if(!ROOT_UID.equals(temp)) {
//        	    	            	listUIDS.add(temp);
//        	    	            	uid = temp;
//        	    	            } else {
//        	    	            	break;
//        	    	            }
//        	    	        } while(true);
//        	    	        litsSearchProcess.put(i, listUIDS);
//        	    	        i++;
//        	            }
//                	}
//                } else if (containsProcess(node)) {
//                		String value = entry.getValue();
//                		if(value.toUpperCase(Constants.OK).indexOf(text.toUpperCase(Constants.OK)) > -1) {
//	    	            	ProcessNode key = entry.getKey();
//	    	                String temp;
//	    	    	        List<String> listUIDS = new ArrayList<String>();
//	    	    	        String uid = key.obj.uid;
//	    	    	        do{
//	    	    	        	temp = parentUidByChildUid.get(uid);
//	    	    	            if(!ROOT_UID.equals(temp)) {
//	    	    	            	listUIDS.add(temp);
//	    	    	            	uid = temp;
//	    	    	            } else {
//	    	    	            	break;
//	    	    	            }
//	    	    	        } while(true);
//	    	    	        litsSearchProcess.put(i, listUIDS);
//	    	    	        i++;
//                		}
//                }
//	        }
//		return litsSearchProcess;
//    }

    protected static void addNode(long parentId, ProcessNode n) {
    	processesById.put(n.getObject().id, n);
    	processesByUid.put(n.getObject().uid, n);
    	parentUidByChildUid.put(n.getObject().uid, n.getParentUid());
    	List<ProcessNode> chs = processesByParentId.get(parentId);
    	if (chs == null) {
    		chs = new ArrayList<ProcessNode>();
    		processesByParentId.put(parentId, chs);
    	}
    	chs.remove(n);
    	chs.add(n);
    }

    private boolean containsProcess(ProcessNode node) {
    	if (!node.isFolder() && (availProcs == null || availProcs.contains(node.obj.id)))
			return true;
    	
    	List<ProcessNode> children = getChildren(node);
    	if (children != null) {
    		for (ProcessNode pn : children) {
    			if (containsProcess(pn)) return true;
    		}
    	}
		return false;
    }
    
    public List<ProcessNode> getChildren(ProcessNode p) {
    	List<ProcessNode> res = new ArrayList<ProcessNode>();
    	List<ProcessNode> children = p.getChildren();
        if (children != null) {
            for (ProcessNode child : children) {
            	if (!child.isTab()) {
	                long id = child.getObject().id;
	                if (!child.isFolder()) {
	                	if (availProcs == null || availProcs.contains(id)) {
	                    	res.add(child);
	                	}
	                } else if (containsProcess(child)) {
	                	res.add(child);
	                }
            	}
            }
        }
        return res;
    }

    public boolean hasNonEmptyFolder(ProcessNode p) {
    	List<ProcessNode> children = p.getChildren();
        if (children != null) {
            for (ProcessNode child : children) {
            	if (!child.isTab()) {
	                if (child.isFolder() && containsProcess(child)) {
	                	return true;
	                }
            	}
            }
        }
        return false;
    }

    private static synchronized long getRootId(WebSession s) {
    	if (ROOT_ID == 0) {
    		try {
	            KrnClass pdRootCls = s.getKernel().getClassByName("ProcessDefRoot");
	            KrnObject[] rootObjs = s.getKernel().getClassObjects(pdRootCls, 0);
	
	            load(s, new long[] {rootObjs[0].id});
	            // ROOT_ID устанавливаем после считывания всех нод, иначе возможна недогрузка узлов!!!(Redmine #10460)
	            ROOT_ID = rootObjs[0].id;
	            ROOT_UID = rootObjs[0].uid;
    		} catch (Exception e) {
    			LOG_.error(e, e);
    		}
    	}
    	return ROOT_ID;
    }

    public Object createProcess(Map<String, String> args) {
        String pid = args.get("id");
        long id = Long.parseLong(pid);
        try {
            String[] res_ = session.getKernel().startProcess(id, null);
            if (res_.length > 0 && !res_[0].equals("")) {
                return res_[0];
            } else {
                List<String> param = new ArrayList<String>();
                // если монитор событий скрыт - отобразить интерфейс
                if (!session.isMonitorTask) {
                    param.add("autoIfc");
                }
                if (res_.length > 3) {
                    param.add(res_[3]);
                }
                Activity act = session.getTaskHelper().startProcess(res_[1], param);
                if (res_.length > 2) {
                	act.infMsg = res_[2];
                }
				return act;
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return null;
    }

    public Object createProcess(String uid, Map<String, Object> vars, WebFrame frm) throws KrnException {
    	KrnObject pObj = session.getKernel().getObjectByUid(uid, 0);
    	if (pObj != null) {
	        long id = pObj.id;
	        try {
	            String[] res_ = session.getKernel().startProcess(id, vars);
	            //Предупреждение позволяющее пользователю запустить процесс по своему усмотрению
	           if(res_.length > 1 && res_[1].indexOf("deferred")==0) {
	            	int resConf=frm.confirm(res_[0]);
	            	if(resConf!=ButtonsFactory.BUTTON_NO) {
	            		vars=new HashMap<String, Object>();
	            		vars.put("DEFERRED", "DEFERRED");
	                    res_ = session.getKernel().startProcess(id, vars);
	            	}else {
	            		return ButtonsFactory.BUTTON_NOACTION;
	            	}
	            }
	            if (res_.length > 0 && !res_[0].equals("")) {
	                return res_[0];
	            } else {
	                List<String> param = new ArrayList<String>();
	                // если монитор событий скрыт - отобразить интерфейс
	                // если старт процесса происходит из меню, то интерфейс и так откроется
	/*                if (!session.isMonitorTask) {
	                    param.add("autoIfc");
	                }
	*/                if (res_.length > 3) {
	                    param.add(res_[3]);
	                }
					Activity act = session.getTaskHelper().startProcess(res_[1], param);
					if (res_.length > 2) {
	                	act.infMsg = res_[2];
	                }
	                return act;
	            }
	        } catch (Exception ex) {
	            log.error(ex, ex);
	        }
    	} else {
    		log.error("Не найден процесс с uid = " + uid);
    	}
        return null;
    }

    public SortedSet<ProcessNode> getTabs() {
        return tabs;
    }

    public ProcessNode getProcessById(long id) {
    	return processesById.get(id);
    }

    public ProcessNode getProcessByUID(String uid) {
    	return processesByUid.get(uid);
    }

    public Long getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(long selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void expand(long id) {
        ProcessNode p = getProcessById(id);
        if (p != null) {
            changeState(new TreePath(p.getPath()));
        }
    }

    public void changeState(TreePath path) {
        setExpandedState(path, !isExpanded(path));
    }

    protected boolean isExpanded(TreePath path) {
        if (path != null) {
            Boolean b = expandedState.get(path.toString());
            if (b != null && b.booleanValue())
                return true;
        }
        return false;
    }

    private void setExpandedState(TreePath path, boolean b) {
        if (path != null) expandedState.put(path.toString(), b);
    }
    
    public WebMenuItem loadProcessMenu(List<WebButton> toolbarButtons) throws KrnException {
        int index;
        String titleRu = null;
        String titleKz = null;
        boolean isTab;
        boolean isBtnToolBar;
        String hotKey;
        byte[] message;
        byte[] icon;
        Kernel krn = session.getKernel();
        
        long rid = LangHelper.getRusLang(session.getConfigNumber()).obj.id;
        long kid = LangHelper.getKazLang(session.getConfigNumber()).obj.id;
        
        // Считать все папки процессов со всеми необходимыми атрибутами
        KrnClass prFolderCls = krn.getClassByName("ProcessDefFolder");
        long langId = krn.getInterfaceLanguage().id;
        AttrRequest ar = new AttrRequestBuilder(prFolderCls, krn).add("parent").add("runtimeIndex").add("isTab")
                .add("title", rid).add("tabName", rid).add("title", kid).add("tabName", kid).add("isBtnToolBar").add("hotKey", langId).add("icon", langId).add("message", langId)
                .build();
        
        List<Object[]> recs = krn.getClassObjects(prFolderCls, ar, new long[0], new int[] { 0 }, 0);

        KrnClass prRootCls = krn.getClassByName("ProcessDefRoot");
        MMap<Long, ProcessItem, Set<ProcessItem>> map = new MMap<Long, ProcessItem, Set<ProcessItem>>(
                (Class<Set<ProcessItem>>) ((Set<ProcessItem>)new TreeSet<ProcessItem>()).getClass());
        ProcessItem prRoot = null;
        for (Object[] rec : recs) {
            KrnObject parent = (KrnObject) rec[2];
            index = (rec[3] != null) ? ((Number) rec[3]).intValue() : 0;
            titleRu = (rec[6] != null) ? (String) rec[6] : (String) rec[5];
            titleKz = (rec[8] != null) ? (String) rec[8] : (String) rec[7];
            if (titleKz == null || titleKz.length() == 0) titleKz = titleRu;
            
            isTab = (rec[4] != null) ? (Boolean) rec[4] : false;
            try {
                isBtnToolBar = (rec[9] != null) ? (Boolean) rec[9] : false;
                hotKey = (rec[10] != null) ? (String) rec[10] : "";
                icon = (rec[11] != null) ? (byte[]) rec[11] : null;
                message = (rec[12] !=null)? (byte[]) rec[12] : null;
            } catch (Exception e) {
                hotKey = "";
                isBtnToolBar = false;
                icon = null;
                message = null;
            }
            
            ProcessItem prItem = new ProcessItem((KrnObject) rec[0], titleRu, titleKz, index, true, isTab, isBtnToolBar, hotKey, icon, message);
            if (prItem.processObj.classId == prRootCls.id) {
                prRoot = prItem;
            } else if (parent != null) {
                map.put(((KrnObject) parent).id, prItem);
            }
        }

        // Считать данные процессов пользователя со всеми необходимыми атрибутами
    	long[] prIds = null;
    	
    	List<Long> procs = krn.getUserSubjects(SystemAction.ACTION_START_PROCESS, krn.getUser().getObject().id);
    	if (procs != null)
    		prIds = Funcs.makeLongArray(procs);
    	else {
    		prIds = krn.getProcessDefinitions();
    	}
        KrnClass prCls = krn.getClassByName("ProcessDef");
        ar = new AttrRequestBuilder(prCls, krn).add("parent").add("runtimeIndex").add("title", rid).add("title", kid).add("isBtnToolBar").add("hotKey", langId).add("icon", langId).add("message", langId).build();
        recs = krn.getObjects(prIds, ar, 0);
        for (Object[] rec : recs) {
            KrnObject parent = (KrnObject) rec[2];
            index = rec[3] != null ? ((Number) rec[3]).intValue() : 0;
            try {
                titleRu = (String) rec[4];
                titleKz = (String) rec[5];
                if (titleKz == null || titleKz.length() == 0) titleKz = titleRu;
				
                isBtnToolBar = (rec[6] != null) ? ((Long) rec[6])==1 : false;
				hotKey = (rec[7] != null) ? (String) rec[7] : "";
				icon = (rec[8] != null) ? (byte[]) rec[8] : null;  
				message = (rec[9] != null) ?(byte[]) rec[9] : null;
            } catch (Exception e) {
                hotKey = "";
                isBtnToolBar = false;
                icon = null;
                message = null;
            }
            
            ProcessItem prItem = new ProcessItem((KrnObject) rec[0], titleRu, titleKz, index, false, true, isBtnToolBar, hotKey, icon, message);
            if (parent != null) {
                map.put(((KrnObject) parent).id, prItem);
            }
        }
        // создание меню процессов
        // для возврата на старый алгоритм необходимо после названия метода добавить _
        return createProcessMenu(prRoot, map);
    }
	

    /**
     * Первичный метод создания меню Реализация без рекурсии
     * 
     * @param root
     *            корень меню (обычно это пункт "Процессы")
     * @param map
     *            карта объектов
     * @return сформированное меню процессов
     */
    private WebMenuItem createProcessMenu(ProcessItem root, MMap<Long, ProcessItem, Set<ProcessItem>> map) {
        if (!root.isFolder) {
            return new WebMenuItem(root.getTitle(), root.getTitleKz(), null, Mode.RUNTIME, null, null);
        } else {
            Collection<ProcessItem> children = map.get(root.processObj.id);
            if (children == null) {
                return null;
            }
            // задать корень меню
            WebMenu menu = new WebMenu(root.getTitle(), root.getTitleKz(), null, Mode.RUNTIME, null, null);
            // перебор всех потомков корневого элемента
            for (ProcessItem child : children) {
                menu = createProcessMenuIsTab(child, map, menu, createProcessMenu_(child, map));
            }
            return menu;
        }
    }    

    /**
     * оригинальный метод
     * Содание элементов меню
     * Рекурсивный метод
     * @param root предок меню
     * @param map карта объектов
     * @return элемент меню
     */
    private WebMenuItem createProcessMenu_(ProcessItem root, MMap<Long, ProcessItem, Set<ProcessItem>> map) {
        if (!root.isFolder) {
            return new WebProcessMenuItem(root.getTitle(), root.getTitleKz(), root.getProcessObj(), root.isBtnToolBar, root.icon, session);
        } else {
            Collection<ProcessItem> children = map.get(root.processObj.id);
            if (children != null) {
                WebMenu menu = null;
                for (ProcessItem child : children) {
                    WebMenuItem mi = createProcessMenu_(child, map);
                    if (mi != null) {
                        if (menu == null) {
                            menu = new WebMenu(root.getTitle(), root.getTitleKz(), null, Mode.RUNTIME, null, null);
                        }
                        menu.add(mi);
                    }
                }
                return menu;
            }
        }
        return null;
    }
	


    /**
     * Дополнительный рекурсивный метод создания меню Работает в связке с
     * первычным методом Отличается тем что в нём идёт проверка на тип "потомка"
     * пункт добавляется в меню только если он "Вкладка"
     * 
     * @param child
     *            объект из карты, претендуемый на добавление в меню
     * @param map
     *            карта объектов
     * @param menu
     *            уже собранное предыдущими рекурсивными вызовами меню
     * @param mi
     *            потомок, претендуемый на добавление в меню
     * @return меню объектов
     */
    private WebMenu createProcessMenuIsTab(ProcessItem child, MMap<Long, ProcessItem, Set<ProcessItem>> map, WebMenu menu,
            WebMenuItem mi) {
        if (child.isTab) {
            if (mi != null) {
                menu.add(mi);
            }
        } else {
            Collection<ProcessItem> _children = map.get(child.processObj.id);
            if (_children != null) {
                for (ProcessItem _child : _children) {
                    menu = createProcessMenuIsTab(_child, map, menu, createProcessMenu_(_child, map));
                }
            }
        }
        return menu;
    }

    public static class ProcessNode extends DefaultMutableTreeNode implements Comparable<ProcessNode> {
        private KrnObject obj;
        private HashMap<Long, String> titleMap = new HashMap<Long, String>();
        private HashMap<Long, String> tabMap = new HashMap<Long, String>();
        private long index = 0;
        private boolean isTab = false;
        private boolean isFolder = false;
        private long toolbar;
        private String hotkey;
        private byte[] message;
        private String desc;
        private byte[] icon;

		private long parentId = 0;
		private String parentUid = null;

        public ProcessNode(KrnObject o) {
            obj = o;

            if (obj.classId != Kernel.SC_PROCESS_DEF.id) {
                isFolder = true;
            }
        }
        
        public String getProcDesc() {
        	return this.desc;
        }

		public String toString(long langId, WebSession s) {
			String title = null;
			if (isTab) {
	            title = tabMap.get(langId);
	            if (title == null) {
	                long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
	                title = tabMap.get(new Long(rid));
	            }
			}
			if (title == null) {
	            title = titleMap.get(langId);
	            if (title == null) {
	                long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
	                title = titleMap.get(new Long(rid));
	            }
			}
            return (title != null) ? title : "*";
        }

        @Override
		public boolean equals(Object o) {
            if (o instanceof ProcessNode && this.obj.id == ((ProcessNode) o).obj.id) {
            	return true;
            }
            return false;
		}

		public int compareTo(ProcessNode o) {
            if (o == null) {
                return 1;
            } else if (this.obj.id == o.obj.id) {
                return 0;
            } else if (this.isFolder && !o.isFolder) {
                return -1;
            } else if (!this.isFolder && o.isFolder) {
                return 1;
            } else {
                return (this.index < o.index) ? -1 : 1;
            }
        }

        public void setTitle(String title, long langId) {
            titleMap.put(langId, title);
        }

        public void setTabName(String title, long langId) {
            tabMap.put(langId, title);
        }

        public void setIsTab(boolean isTab) {
            this.isTab = isTab;
        }

        public void setIndex(long index) {
            this.index = index;
        }
        
        public long getIndex() {
            return index;
        }

        public boolean isTab() {
            return isTab;
        }

        public boolean isFolder() {
            return isFolder;
        }

        public List<ProcessNode> getChildren() {
        	return processesByParentId.get(obj.id);
        }

        public KrnObject getObject() {
            return obj;
        }

        public long getParentId() {
			return parentId;
		}
        
        public void setParentId(long pid) {
			this.parentId  = pid;
		}
        
        public String getParentUid() {
        	return parentUid;
        }
        
        public void setParentUid(String uid) {
        	this.parentUid = uid;
        }

		public long getToolbar() {
			return toolbar;
		}

		public void setToolbar(long toolbar) {
			this.toolbar = toolbar;
		}

		public String getHotkey() {
			return hotkey;
		}
		
		public byte[] getMessage() {
			return message;
		}

		public void setHotkey(String hotkey) {
			this.hotkey = hotkey;
		}
		
		public void setMessage(byte[] message) {
			this.message = message;
        	if (message != null) {
                try	{
                	SAXBuilder builder = new SAXBuilder();
                	InputStream is_msg = message.length > 0 ? new ByteArrayInputStream(message) : null;
                	builder.setValidation(false);
                	Element xml_msg = builder.build(is_msg).getRootElement();
                	List msgs=xml_msg.getChildren("msg");
                    for (Object msg1 : msgs) {
                        Element e = (Element) msg1;
                        String uid = e.getAttribute("uid").getValue();
                        String value = e.getText();
                        if ("process_desc_0".equals(uid)) {
                        	this.desc = value;
                        }
                    }
                } catch(Exception e) {
                    LOG_.error(e, e);
                }
        	}
		}
		

		public byte[] getIcon() {
			return icon;
		}

		public void setIcon(byte[] icon) {
			this.icon = icon;
		}
    }
    
	private class ProcessItem implements Comparable<ProcessItem> {
		
		private final KrnObject processObj;
		private final int index;
		private final boolean isFolder;
		private final boolean isTab;
		private final boolean isBtnToolBar;
		private final String hotKey;
		private final byte[] message;
		private final byte[] icon;
		private final String title;
		private final String titleKz;
		private boolean enabled = false;

		public ProcessItem(KrnObject processObj, String title, String titleKz, int index, boolean isFolder,boolean isTab,boolean isBtnToolBar,String hotKey,byte[] icon, byte[] message) {
			this.title = title;
			this.titleKz = titleKz;
			this.processObj = processObj;
			this.index = index;
			this.isFolder = isFolder;
			this.isTab = isTab;
			this.isBtnToolBar = isBtnToolBar;
			this.hotKey = hotKey;
			this.icon = icon;
			this.message = message;
			this.enabled = true;
        }

		public int compareTo(ProcessItem o) {
			int res = index - o.index;
			if (res == 0) {
				res = processObj.id > o.processObj.id ? 1 : processObj.id < o.processObj.id ? -1 : 0;
			}
			return res;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof ProcessItem)
				return processObj.id == ((ProcessItem)obj).processObj.id;
			return false;
		}

		public String getTitle() {
			return title;
		}

		public String getTitleKz() {
			return titleKz;
		}

		public KrnObject getProcessObj() {
			return processObj;
		}
    }

    private static class ProcessListener implements AttrChangeListener {
    	private static final Log log = LogFactory.getLog("ProcessListener" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
    	private String dsName;
    	
    	public ProcessListener(String dsName) {
    		this.dsName = dsName;
    	}

    	@Override
		public void attrChanged(KrnObject obj, long attrId, long langId, long trId, UUID uuid) {
			List<AttrChange> l = attrChanges.get(uuid);
			if (l == null) {
				l = new ArrayList<AttrChange>();
				attrChanges.put(uuid, l);
			}
			
			l.add(new AttrChange(obj, attrId, langId, trId));
		}
	
		@Override
		public void commit(UUID uuid) {
			if (uuid != null) {
				List<AttrChange> l = attrChanges.remove(uuid);
				if (l != null && l.size() > 0) {
					log.info("COMMIT PDEF " + uuid + "; SIZE = " + l.size());
			        try {
				        Session s = SrvUtils.getSession(dsName, "sys", null);
				        try {
							for (AttrChange ch : l) {
								processAttrChanged(ch.obj, ch.attrId, ch.langId, ch.trId, s);
							}
				        } catch (Exception e) {
				        	log.error(e, e);
			            } finally {
			                if (s != null) {
			                    s.release();
			                }
			            }
			        } catch (KrnException e) {
			        	log.error(e, e);
			        }
					l.clear();
				}
			}
		}
	
		@Override
		public void rollback(UUID uuid) {
			if (uuid != null) {
				List<AttrChange> l = attrChanges.remove(uuid);
				if (l != null && l.size() > 0) {
			        log.info("ROLBACK USER SESSION " + uuid + "; SIZE = " + l.size());
					l.clear();
				}
			}
		}

		@Override
		public void commitLongTransaction(UUID uuid, long trId) {
		}

		@Override
		public void rollbackLongTransaction(UUID uuid, long trId) {
		}

		public void processAttrChanged(KrnObject obj, long attrId, long langId, long trId, Session s) {
			if (attrId == 0 || attrId == 1) {
				ProcessNode n = processesByUid.get(obj.uid);
				if (n == null)
					reload(s, obj.id, null);
			} if (attrId == 2) {
				processesByUid.remove(obj.uid);
				processesById.remove(obj.id);
				for (List<ProcessNode> l : processesByParentId.values()) {
					l.remove(new ProcessNode(obj));
				}
			} else if (attrId == attrParent.id) {
				ProcessNode n = processesByUid.get(obj.uid);
				List<ProcessNode> l = processesByParentId.get(n.parentId);
				if (l != null) l.remove(n);
				reload(s, obj.id, n);
				addNode(n.parentId, n);
			} else if (attrId == attrToolbar.id || attrId == attrTitle.id || attrId == attrIndex.id || attrId == attrHotkey.id || attrId == attrIcon.id
					|| attrId == attrIsTab.id || attrId == attrTabName.id || attrId == attrMessage.id) {
				ProcessNode n = processesByUid.get(obj.uid);
				reload(s, obj.id, n);
			// Пока убираем, так как перечитывание процесса приходит отдельно
			/*} else if (attrId == attrConfig.id) {
				try {
					s.reloadProcessDefinition(obj.id);
				} catch (Exception e) {
		        	logReload = LogFactory.getLog(s.getUserSession().getDsName() + "."
		    				+ s.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessHelper.class.getName());
		        	logReload.error(e, e);
				}*/
			}
		}
    }
    
	private static synchronized void initListener(WebSession s) throws KrnException {
		if (!listenerInitialized) {
			listener = new ProcessListener(s.getKernel().getBaseName());
			
			pdfCls = s.getKernel().getClassByName("ProcessDefFolder");
			pdCls = s.getKernel().getClassByName("ProcessDef");
			
			attrParent = s.getKernel().getAttributeByName(pdCls, "parent");
			attrIndex = s.getKernel().getAttributeByName(pdCls, "runtimeIndex");
			attrTitle = s.getKernel().getAttributeByName(pdCls, "title");
			attrToolbar = s.getKernel().getAttributeByName(pdCls, "isBtnToolBar");
			attrHotkey = s.getKernel().getAttributeByName(pdCls, "hotKey");
			attrIcon = s.getKernel().getAttributeByName(pdCls, "icon");

			attrConfig = s.getKernel().getAttributeByName(pdCls, "config");
			attrMessage = s.getKernel().getAttributeByName(pdCls, "message");

			attrIsTab = s.getKernel().getAttributeByName(pdfCls, "isTab");
			attrTabName = s.getKernel().getAttributeByName(pdfCls, "tabName");

			Driver2.addAttrChangeListener(pdCls.id, listener);
			Driver2.addAttrChangeListener(pdfCls.id, listener);

			listenerInitialized = true;
		}
	}
}
