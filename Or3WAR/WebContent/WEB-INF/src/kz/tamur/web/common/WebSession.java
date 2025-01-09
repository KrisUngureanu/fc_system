package kz.tamur.web.common;

import static kz.tamur.comps.Constants.ACT_ARTICLE_STRING;
import static kz.tamur.comps.Constants.ACT_FASTREPORT_STRING;
import static kz.tamur.comps.Constants.ACT_AUTO_STRING;
import static kz.tamur.comps.Constants.ACT_DIALOG_STRING;
import static kz.tamur.comps.Constants.ACT_ERR;
import static kz.tamur.comps.Constants.ACT_PERMIT;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.rt.InterfaceManager.ARCH_RO_MODE;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.Stack;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.TreePath;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.PolicyNode;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3.server.lang.SystemOp;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.session.SessionOps;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.rt.Config;
import kz.tamur.rt.ConfigObject;
import kz.tamur.rt.GlobalConfig;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManager.CommitResult;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.web.LocalKernel;
import kz.tamur.web.OrWebImage;
import kz.tamur.web.common.LangHelper.WebLangItem;
import kz.tamur.web.common.ProcessHelper.ProcessNode;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.OrWebAnalyticPanel;
import kz.tamur.web.component.OrWebComboBox;
import kz.tamur.web.component.OrWebComboColumn;
import kz.tamur.web.component.OrWebDocField;
import kz.tamur.web.component.OrWebDocFieldColumn;
import kz.tamur.web.component.OrWebGISPanel;
import kz.tamur.web.component.OrWebHyperColumn;
import kz.tamur.web.component.OrWebHyperLabel;
import kz.tamur.web.component.OrWebHyperPopup;
import kz.tamur.web.component.OrWebImagePanel;
import kz.tamur.web.component.OrWebMap;
import kz.tamur.web.component.OrWebNote;
import kz.tamur.web.component.OrWebNoteBrowser;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.OrWebPopUpPanel;
import kz.tamur.web.component.OrWebRadioBox;
import kz.tamur.web.component.OrWebTabbedPane;
import kz.tamur.web.component.OrWebTable;
import kz.tamur.web.component.OrWebTree;
import kz.tamur.web.component.OrWebTree2;
import kz.tamur.web.component.OrWebTreeColumn;
import kz.tamur.web.component.OrWebTreeControl2;
import kz.tamur.web.component.OrWebTreeCtrl;
import kz.tamur.web.component.OrWebTreeField;
import kz.tamur.web.component.OrWebTreeTable;
import kz.tamur.web.component.OrWebTreeTable2;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.component.WebFrameManager;
import kz.tamur.web.controller.WebController;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cifs.or2.client.HelpFile;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.kernel.UserSessionValue;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class WebSession {
    private Integer id_;
    private Log log;
    private static String version = null;
    private String userName;
    private Kernel wkrn;
    private long interfaceLangId;
    private ProcessHelper processHelper;
    private TaskHelper taskHelper;
    private WebFrameManager frameManager;
    private WebInterfaceManager ifcManager;
    private CommonHelper commonHelper;
    private ArchiveHelper archiveHelper;
    private ResourceBundle resource = CommonHelper.RESOURCE_RU;
    private long dataLangId;
    public String backPage = "";
    public String onclick = "";
    private boolean isForPublicUser = false;
    private boolean isOnlyArchive = false;
    private boolean needMain = true;
    private String message = null;
    private List<Object> helpObjs;
    private transient List<File> files = new ArrayList<File>();

    private long lastPing;
    private String profile;
    private String conteiner;
    private String profileSaved;
    private String cert;
    private String certType;

    private String ncaProfile;
    private String ncaConteiner;
    private String ncaProfileSaved;
    private String ncaCert;

    private WebMenuItem processMenu;
    
    private GlobalConfig config;
    private Config conf;

    private Map<Long, Map<String, JsonObject>> changesObjsByFrameId = new HashMap<Long, Map<String, JsonObject>>();
    private Map<String,Object> commands = new HashMap<String,Object>();
    private Map<String,Object> setters = new HashMap<String,Object>();
    
    /** Хранит параметры градиентной заливки для главного фрейма системы. */
    public GradientColor GRADIENT_MAIN_FRAME;

    /** Хранит параметры градиентной заливки для панели управления системы. */
    public GradientColor GRADIENT_CONTROL_PANEL;

    /** Хранит параметры градиентной заливки для панели меню системы. */
    public GradientColor GRADIENT_MENU_PANEL;
    
    public Color COLOR_FIELD_NO_FLC;

    /** Отображать монитор задач. */
    public boolean isMonitorTask = true;
    /** Отображать панель инструментов. */
    public boolean isToolBar = true;

    private Map<String, WebActionMaker> actions = new HashMap<String, WebActionMaker>();
    private Map<String, WebChangeMaker> changes = new HashMap<String, WebChangeMaker>();

    private int lastId = 0;
    private int configNumber = 0;

    private boolean openPrev = false;

    public String browserType = null;
    public String browserVersion = null;
    public String browserOS = null;
    public boolean isMobile = false;
    
    // типы браузеров
    public boolean isChrome = false;
    public boolean isOmniweb = false;
    public boolean isSafari = false;
    public boolean isOpera = false;
    public boolean isIcab = false;
    public boolean isKonqueror = false;
    public boolean isFirefox = false;
    public boolean isCamino = false;
    public boolean isNetscape = false;
    public boolean isExplorer = false;
    public boolean isMozilla = false;

    // типы ОС
    public boolean isWin = false;
    public boolean isMac = false;
    public boolean isIphone = false;
    public boolean isLinux = false;
    
    private String confirmMessage = null;
    
	private boolean noMainUiCommand = false; 
    
    public static Map<String, String> contentTypes = new HashMap<String, String>();
    
    static {
    	contentTypes.put("doc", "application/msword");
    	contentTypes.put("xls", "application/vnd.ms-excel");
    	contentTypes.put("ppt", "application/vnd.ms-powerpoint");
    	contentTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    	contentTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    	contentTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    	contentTypes.put("html", "text/html; charset=UTF-8");
    	contentTypes.put("xml", "text/xml; charset=UTF-8");
    	contentTypes.put("txt", "text/plain; charset=UTF-8");
    	contentTypes.put("pdf", "application/pdf");
    	contentTypes.put("png", "image/png");
    	contentTypes.put("jpg", "image/jpeg");
    	contentTypes.put("gif", "image/gif");
    	contentTypes.put("zip", "application/zip");
    }
    
    private long lastOperationTime = System.currentTimeMillis();
    private boolean isConfirmed = false;
    private long downtime;
    public boolean tasksRefreshing = true;
    public List<Object[]> tasksToRefreshing = new ArrayList<Object[]>();
	
    private WebUser webUser;
	private boolean longPolling = false;

    private static DateFormat notificationDatetimeFormat = new SimpleDateFormat("dd.MM.yyy hh:mm:ss"); 
    
    // проверяем стартанул ли процесс каждые 1.5 сек, 20 раз (итого 30 сек)
    public static int CHECK_PROCCESS_STARTED_TIMEOUT = 1500;
    public static int CHECK_PROCCESS_STARTED_COUNT = 20;
    
    public String getLangCode() {
    	LangHelper.WebLangItem li = LangHelper.getLangById(interfaceLangId, configNumber);
    	return li.code;    	
    }
    
    public WebSession(Integer id, String user, String path, String newPD, String confPD, String ip, String host, String title,
    		int loginType, int configNumber, String dsName, boolean force, boolean sLogin, long downtime, boolean isUseECP, String signedData) throws KrnException {
    	this.downtime = downtime;
        userName = user;
        StringBuilder sb = new StringBuilder(WebController.BASE_NAME[configNumber]).append(".").append(user.replaceAll("\\s|\\.", "_")).append(".")
        		.append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
        this.log = LogFactory.getLog(sb.toString());

        isOnlyArchive = "mu_pub".equals(user);
        this.configNumber = configNumber;
        String clientType = null;
        SessionOpsOperations ops = null;
        if (WebController.NO_KERNEL[configNumber]) {
        	clientType = Constants.CLIENT_TYPE_LOCALWEB;
        	ops = new SessionOps();
        	wkrn = new LocalKernel(configNumber);
        } else {
        	clientType = Constants.CLIENT_TYPE_WEB;
        	ops = WebController.lookup(true);
        	wkrn = new WebKernel(configNumber);
        }
        
        wkrn.setADVANCED_UI(WebController.ADVANCED_UI[configNumber]);
        wkrn.setSE_UI(WebController.SE_UI[configNumber]);
        
        try {
            log.info("|USER: " + user + "| before kernel.init");
            wkrn.init(user, path, newPD, confPD, WebController.SERVER_HOST[configNumber], WebController.SERVER_PORT[configNumber], 
            		dsName, clientType, ip, host, loginType, ops, force, sLogin, isUseECP, signedData);
            
            userName = wkrn.getUser().getName();
            
            log.info("|USER: " + user + "| after kernel.init");
        } catch (KrnException e) {
        	if (e.type == ErrorCodes.TYPE_ERROR)
        		log.error(e, e);
        	else
        		log.warn(e.getMessage());
        	
            wkrn.release();
            throw e;
        }
        lastPing = System.currentTimeMillis();
        dataLangId = interfaceLangId = wkrn.getInterfaceLanguage().id;
        LangHelper.init(this);
        LangHelper.WebLangItem li = LangHelper.getLangById(interfaceLangId, configNumber);
        //getKernel().setLang(li.obj);
        if ("KZ".equals(li.code)) {
            resource = CommonHelper.RESOURCE_KZ;
        } else {
            resource = CommonHelper.RESOURCE_RU;
        }
        //dataLangId = wkrn.getDataLanguage().id;
        id_ = id;

        frameManager = new WebFrameManager(this);

        if (!isOnlyArchive) {
        	processHelper = new ProcessHelper(this);
            taskHelper = new TaskHelper(this, interfaceLangId);
        }        
        archiveHelper = new ArchiveHelper(this);

        ifcManager = new WebInterfaceManager(this);
        ifcManager.setDataLangId(dataLangId);
        commonHelper = new CommonHelper(this);

        if (WebController.PROCESS_MENU || WebController.ADVANCED_UI[configNumber]) {
            List<WebButton> toolbarButtons = new ArrayList<WebButton>();
            processMenu = processHelper.loadProcessMenu(toolbarButtons);
        }

        // Создание основного интерфейса пользователя
        boolean openMainIfc = wkrn.getUser().hasRight(Or3RightsNode.WEB_PAGE_MAINIFC_RIGHT);
        KrnObject uiObj = wkrn.getInterface();
        if (openMainIfc && uiObj != null) {
        	if (WebController.ADVANCED_UI[configNumber]) {
	            ifcManager.setMainUI(frameManager.createFrame(uiObj, null));
	            ifcManager.absolute(null, uiObj, null, null, ARCH_RO_MODE, false, -1, false, 0, true, "");
	            ifcManager.doAfterOpen(ifcManager.getMainUI());
	            ifcManager.getMainUI().setEvaluationMode(ARCH_RO_MODE);
	            ifcManager.enableRollback(true);
        	} else {
	            ifcManager.setMainUI(frameManager.createFrame(uiObj, null));
	            ifcManager.absolute(null, uiObj, null, null, InterfaceManager.ARCH_RW_MODE, false, 0, false, 0, true, "");
	            ifcManager.doAfterOpen(ifcManager.getMainUI());
	            ifcManager.getMainUI().setEvaluationMode(InterfaceManager.ARCH_RW_MODE);
	            ifcManager.enableRollback(true);
//                ifcManager.absolute(uiObj, null, "", InterfaceManager.SERVICE_MODE, true, 0, 0, false, "");
        	}
        } else
            frameManager.absolute(new KrnObject(0, "", 0), null);

        config = GlobalConfig.instance(wkrn);
        conf = Utils.mergeConfig(config.getConfig());

        GRADIENT_MAIN_FRAME = conf.getGradientMainFrame();
        GRADIENT_CONTROL_PANEL = conf.getGradientControlPanel();
        GRADIENT_MENU_PANEL = conf.getGradientMenuPanel();
        GradientColor colfldNoFLC = conf.getGradientFieldNOFLC();
        COLOR_FIELD_NO_FLC = (colfldNoFLC != null && colfldNoFLC.isEnabled()) ? colfldNoFLC.getStartColor() : new Color(255, 204, 204);
        isMonitorTask = wkrn.getUser().isMonitor();
        isToolBar = wkrn.getUser().isToolBar();
        setLangId(interfaceLangId);
        
        WebClientCallback cb = (WebClientCallback)wkrn.getCallback();
        cb.setWebSession(this);
        cb.start();
    }
    
    public List<NavBarComponent> getNavBarContent(){
    	
    	List<NavBarComponent> res = new ArrayList<>();
		
    	res.add(new NavBarComponent(10, "oldordersList_count", "ui_oldStartPage", "#ui=oldStart&id=ui_oldStartPage", Funcs.sanitizeXml(resource.getString("webOldFlows")), false, "counter", null, -5));
    	res.add(new NavBarComponent(20, null, "ui_startPage", "#ui=start&id=ui_startPage", Funcs.sanitizeXml(resource.getString("webStartPage")), false, null, null, -10));
    	res.add(new NavBarComponent(30, null, "ui_startPage", "#cmd=openMainIfc&id=ui_startPage", Funcs.sanitizeXml(resource.getString("webStartPage")), false, null, null, -20));
    	res.add(new NavBarComponent(40, null, "ui_Orders", "#ui=tasksList&id=ui_Orders", Funcs.sanitizeXml(resource.getString("webMonitor")), false, "counter processes_counter", null, -30));
    	res.add(new NavBarComponent(50, null, "ui_OrdersNotification", "#ui=notiInList&id=ui_OrdersNotification", Funcs.sanitizeXml(resource.getString("webNotification")), false, "counter processesIN_counter", null, -40));
    	res.add(new NavBarComponent(60, null, "ui_personInfo", "#cmd=openArch&uid=1014162.3198690&id=ui_personInfo", Funcs.sanitizeXml(resource.getString("webMyProfile")), false, null, null, -50));
    	res.add(new NavBarComponent(70, null, "ui_process", "#ui=processesList&mode=layout&id=ui_process", Funcs.sanitizeXml(resource.getString("webProcesses")), false, null, null, -60));
    	res.add(new NavBarComponent(80, null, "ui_staff", "#cmd=openArch&uid=1014162.3211302&id=ui_staff", Funcs.sanitizeXml(resource.getString("webShtat")), false, null, null, -70));
    	res.add(new NavBarComponent(90, null, "ui_arch", "#ui=archList&mode=layout&id=ui_arch", Funcs.sanitizeXml(resource.getString("webArchive")), false, null, null, -80));
    	res.add(new NavBarComponent(100, null, "ui_help", "#ui=helpWnd&mode=tabs&id=ui_help", Funcs.sanitizeXml(resource.getString("webHelp")), false, null, null, -110));
    	res.add(new NavBarComponent(110, null, "ui_dicts", "#ui=dictsList&mode=layout&id=ui_dicts", Funcs.sanitizeXml(resource.getString("webDicts")), false, null, null, -90));
    	res.add(new NavBarComponent(120, null, "ui_stat", "#cmd=openArch&uid=1014162.3887376&id=ui_stat", "Статистика", false, null, null, -100));
    	res.add(new NavBarComponent(130, null, "ui_right", "#cmd=openDict&uid=1014162.3554408&id=ui_right", Funcs.sanitizeXml(resource.getString("webRights")), false, null, null, -120));
    	res.add(new NavBarComponent(140, null, "ui_actions", "#cmd=openDict&uid=1014162.3555088&id=ui_actions", Funcs.sanitizeXml(resource.getString("webUserAct")), false, null, null, -130));
    	res.add(new NavBarComponent(150, null, "ui_admins", "#ui=adminsList&mode=layout&id=ui_admins", Funcs.sanitizeXml(resource.getString("webAdmins")), false, null, null, -140));
    	List<KrnObject> objs = Utils.getDynamicNodeObjs(wkrn);
		String[] dynTitles = Utils.getDynamicNodeTitles(wkrn, objs, interfaceLangId);
		String[] dynUis = Utils.getDynamicNodeUids(wkrn, objs);
		String[] dynIcons = getDynamicNodeIcons(objs);
		Integer[] ids = Utils.getDynamicNodeIds(wkrn, objs);
		if(objs != null) {
			for(int i = 0; i < objs.size(); i++) {
				String id = "ui_dynamicTitle_"+objs.get(i).id;
				String href = "#cmd=openArch&uid="+dynUis[i]+"&id="+id;
				int idSect = -(150 + i * 10);
				res.add(new NavBarComponent(ids[i], null, id, href, Funcs.sanitizeXml(dynTitles[i]), true, null, dynIcons[i], idSect));
			}
		}
		
		Collections.sort(res);
    	
    	return res;
    }
    
    public String[] getDynamicNodeIcons(List<KrnObject> objs){
    	byte[][] icons = Utils.getDynamicNodeIcons(wkrn, objs);
    	String[] iconsString = new String[icons.length];
    	for(int i = 0; i < icons.length; i++) {    		
    		iconsString[i] = Base64.encodeBytes(icons[i]);
    	}
    	return iconsString;
    }
    private void setLangId(long langId) {
    	setLangId(langId, false);
    }
    
    public boolean isScopeEmpty() {
    	boolean res = true;
    	Kernel krn = this.getKernel();
    	User user = krn.getUser();
    	String[] scopeArr = user.getScopeUids();
    	res = scopeArr.length == 0;
    	return res;
    }
    
    public String getChatSrchTxt() {
    	Kernel krn = this.getKernel();
    	String res = null;
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
    		KrnObject obj = krn.getClassOwnObjects(cls, 0)[0];
    		String[] strs = krn.getStrings(obj, "chat_srch_txt", interfaceLangId, 0);
    		if (strs != null && strs.length > 0)
    			res = strs[0];    		
    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
    	return res!=null? res:"Set text in Configuration";
    }
    
    public String getLdSrchTxt() {
    	Kernel krn = this.getKernel();
    	String res = null;
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
    		KrnObject obj = krn.getClassOwnObjects(cls, 0)[0];
    		String[] strs = krn.getStrings(obj, "srch_txt", interfaceLangId, 0);
    		if (strs != null && strs.length > 0)
    			res = strs[0];    		
    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
    	return res!=null? res:"Set text in Configuration";
//    	return res!=null? res: LangHelper.getLangById(interfaceLangId, configNumber).code.equals("KZ")? "KZ Настройте 'Быстрый доступ к объекту учета ": "Настройте 'Быстрый доступ к объекту учета";
    }
    public boolean showLDinput() {
    	Kernel krn = this.getKernel();
    	boolean res = false;
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
    		KrnObject obj = krn.getClassOwnObjects(cls, 0)[0];
    		AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn).add("showSearchField");
    		long[] objIds = {obj.id};    		
    		Object[] row = krn.getObjects(objIds, arb.build(), 0).get(0); 
    		res = arb.getBooleanValue("showSearchField", row);
    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
    	return res;
    }
    
    private void setLangId(long langId, boolean withReloading) {
        this.interfaceLangId = langId;
        helpObjs = null;
        try {
            LangHelper.WebLangItem li = LangHelper.getLangById(langId, configNumber);
            getKernel().setLang(li.obj);
            if ("KZ".equals(li.code)) {
                resource = CommonHelper.RESOURCE_KZ;
            } else {
                resource = CommonHelper.RESOURCE_RU;
            }
            if (taskHelper != null) taskHelper.setLangId(langId, withReloading);
            ifcManager.setLangId(langId);
            
            KrnObject lang = LangHelper.getLangById(langId, configNumber).obj;
            if (withReloading) {
            	frameManager.setInterfaceLang(lang, withReloading);
            } else if (frameManager.getCurrentFrame() != null)
                frameManager.getCurrentFrame().setInterfaceLang(lang, withReloading);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public Kernel getKernel() {
        return wkrn;
    }

    /**
     * @param args
     * @param request
     * @param response
     * @param daysLeft
     * @return
     * @throws Throwable
     */
    public String process(Map<String, String> args, HttpServletRequest request, HttpServletResponse response, Long daysLeft) throws Throwable {
    	if (args.get("polling") == null && args.get("ping") == null)
    		lastPing = System.currentTimeMillis();

        String cmd = args.get("cmd");

        if (downtime > -1) {
	        if (cmd == null) {
	        	if (System.currentTimeMillis() - lastOperationTime > downtime && !isConfirmed) {
	            	if (args.get("ping") != null) {
		            	isConfirmed = true;
	                	JsonObject res = new JsonObject();
	                	res.add("commitMessage", "1");
	                	return res.toString();
	            	}
	        	}
	        } else if ("fcommit".equals(cmd)) {
	        	if (!isConfirmed) {
		        	lastOperationTime = System.currentTimeMillis();
		        	isConfirmed = false;
	        	}
	        } else {
	        	lastOperationTime = System.currentTimeMillis();
	        	isConfirmed = false;
	        }
        }
        
    	OrCalcRef.removeCalculations();
        if ("fcs".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = null;
            if (frm != null) {
                comp = frm.getComponentByUID(uid);
                frm.setSelectedComponent((OrGuiComponent) comp);
            }
            return "{}";
        } else if (args.get("ping") != null) {
            return "{}";
        } else if (args.get("doAfterLogin") != null) {
        	webUser.doAfterLogin();
        	return "{}";
        } else if ("getAnalyticProps".equals(cmd)) {
        	WebFrame frm = frameManager.getCurrentFrame();
        	OrWebAnalyticPanel analyticPanel = (OrWebAnalyticPanel)frm.getComponentByUID(args.get("id"));
        	JsonObject res = new JsonObject();
        	
        	String expr = analyticPanel.getFirstXAxisExpr();
        	if (expr != null) {
        		KrnObject krnObj = (KrnObject) kz.tamur.comps.Utils.evalExp(expr, frm, frm.getPanelAdapter());
        		KrnAttribute attrName = wkrn.getAttributeByName(krnObj.classId, "name");
            	String[] names = wkrn.getStrings(krnObj, attrName, interfaceLangId, 0);
            	JsonObject firstXAxis = new JsonObject();
            	firstXAxis.add("id", krnObj.id);
            	firstXAxis.add("name", names[0]);
	        	res.add("firstXAxis", firstXAxis);
        	}
        	
        	expr = analyticPanel.getFirstYAxisExpr();
        	if (expr != null) {
        		KrnObject krnObj = (KrnObject) kz.tamur.comps.Utils.evalExp(expr, frm, frm.getPanelAdapter());
        		KrnAttribute attrName = wkrn.getAttributeByName(krnObj.classId, "name");
            	String[] names = wkrn.getStrings(krnObj, attrName, interfaceLangId, 0);
            	JsonObject firstYAxis = new JsonObject();
            	firstYAxis.add("id", krnObj.id);
            	firstYAxis.add("name", names[0]);
        		res.add("firstYAxis", firstYAxis);
        	}
        	
        	expr = analyticPanel.getXAxisExpr();
        	if (expr != null) {
        		List axes = (List) kz.tamur.comps.Utils.evalExp(expr, frm, frm.getPanelAdapter());
        		getAxisNames(axes, res, "x");
        	}
        	
        	expr = analyticPanel.getYAxisExpr();
        	if (expr != null) {
        		List axes = (List) kz.tamur.comps.Utils.evalExp(expr, frm, frm.getPanelAdapter());
        		getAxisNames(axes, res, "y");
        	}
        	
        	expr = analyticPanel.getZAxisExpr();
        	if (expr != null) {
        		List axes = (List) kz.tamur.comps.Utils.evalExp(expr, frm, frm.getPanelAdapter());
        		getAxisNames(axes, res, "z");
        	}
        	
        	String fact = analyticPanel.getFact();
        	if (fact != null) {
        		res.add("fact", fact);
        	}

        	int type = analyticPanel.getType();
        	if (type == 0) {
        		res.add("type", "BAR");
        	} else if (type == 1) {
        		res.add("type", "PIE");
        	} else if (type == 2) {
        		res.add("type", "LINE");
        	} else if (type == 3) {
        		res.add("type", "DONUT");
        	} else if (type == 4) {
        		res.add("type", "3DBAR");
        	}
        	
        	res.add("showLegend", analyticPanel.isShowLegend());
        	
        	int aggType = analyticPanel.getAggType();
        	if (aggType == 0) {
        		res.add("aggType", "count");
        	} else if (aggType == 1) {
        		res.add("aggType", "sum");
        	} else if (aggType == 2) {
        		res.add("aggType", "avg");
        	}
        	
        	String aggField = analyticPanel.getAggField();
        	if (aggField != null) {
        		res.add("aggField", aggField);
        	}
        	
        	return res.toString();
        } else if("getCahtSrchTxtPlaceholder".equals(cmd)){
        	JsonObject res = new JsonObject();
        	res.add("content", getChatSrchTxt());
        	return res.toString();
        } else if ("getFileIsNull".equals(cmd)) {
	        WebFrame frm = frameManager.getCurrentFrame();
	        frm.setFile(null);
        } else if ("loadNotifications".equals(cmd)) {
            JsonObject res = new JsonObject();
            try {
	            KrnClass userCls = wkrn.getClassByName("User");
	            KrnAttribute notificationsAttr = wkrn.getAttributeByName(userCls, "notifications");
	            if (notificationsAttr != null) {
	            	KrnObject[] notifications = wkrn.getObjects(wkrn.getUser().object, notificationsAttr, 0);
	            	if (notifications.length > 0) {
	            		KrnClass notificationCls = wkrn.getClassByName("Notification");
	        	    	KrnAttribute messageAttr = wkrn.getAttributeByName(notificationCls, "message");
	        	    	KrnAttribute uidAttr = wkrn.getAttributeByName(notificationCls, "uid");
	        	    	KrnAttribute cuidAttr = wkrn.getAttributeByName(notificationCls, "cuid");
	        	    	KrnAttribute rowAttr = wkrn.getAttributeByName(notificationCls, "row");
	        	    	KrnAttribute datetimeAttr = wkrn.getAttributeByName(notificationCls, "datetime");
	
	        	    	JsonArray array = new JsonArray();
		                for (int i = 0; i < notifications.length; i++) {
		                	String message = wkrn.getStringsSingular(notifications[i].id, messageAttr.id, this.interfaceLangId, false, false);
		                	String uid = wkrn.getStringsSingular(notifications[i].id, uidAttr.id, 0, false, false);
		                	String cuid = wkrn.getStringsSingular(notifications[i].id, cuidAttr.id, 0, false, false);
		                	long row = wkrn.getLongsSingular(notifications[i], rowAttr, false);
		                	TimeValue[] values = wkrn.getTimeValues(new long[] { notifications[i].id }, datetimeAttr, 0);
	
		                	JsonObject obj = new JsonObject();
		                	obj.add("objId", notifications[i].id);
		                	obj.add("message", message);
		                	obj.add("uid", uid);
		                	obj.add("cuid", cuid);
		                	obj.add("row", row);
		                	obj.add("datetime", notificationDatetimeFormat.format(Funcs.convertToDate(values[0].value)));
		                	array.add(obj);
		                }
		                res.add("notifications", array);
	            	}
	            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res.toString();
        } else if ("deleteNotification".equals(cmd)) {
        	long objId = Long.parseLong(args.get("objId"));
        	KrnObject obj = wkrn.getObjectById(objId, 0);
            KrnClass userCls = wkrn.getClassByName("User");
            KrnAttribute notificationsAttr = wkrn.getAttributeByName(userCls, "notifications");
            List<Object> list = new ArrayList<>();
            list.add(obj);
        	wkrn.deleteValue(objId, notificationsAttr.id, list, 0);
        	wkrn.deleteObject(obj, 0);
        	
        } else if ("signLiability".equals(cmd)) {
            JsonObject obj = new JsonObject();
            try {
            	X509Certificate c = null;
            	boolean isNCAMode = "1".equals(args.get("isNCAMode"));
            	if (isNCAMode) {
            		c = KalkanUtil.getCertificate(args.get("signedData"));
            	} else {
        			c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(args.get("cert")));
            	}
                String userDN;
                String fullDN = c.getSubjectDN().getName();
                int beg = fullDN.indexOf("IIN");
                if (beg > -1) {
                    int end = fullDN.indexOf(",", beg + 3);
                    if (end == -1)
                        end = fullDN.length();
                    userDN = fullDN.substring(beg + 3, end);
                } else {
                    userDN = fullDN;
                }
                
                KrnClass userCls = wkrn.getClassByName("User");
                KrnAttribute iinAttr = wkrn.getAttributeByName(userCls, "iin");
                String iin = wkrn.getStringsSingular(wkrn.getUser().object.id, iinAttr.id, 0, false, false);
                if (iin.length() == 0) {
                	obj.add("signResult", 0);
                	obj.add("errorMessage", "У пользователя не задан ИИН!");
                    return obj.toString();
                } else if (userDN.equals(iin)) {
                    CheckSignResult checkSignResult = null;
                	if (isNCAMode) {
                		checkSignResult = KalkanUtil.checkXML(args.get("signedData"));
                	} else {
                        checkSignResult = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, args.get("liabilityText"), args.get("sign"), c, false);
                	}
                    if (checkSignResult.isDigiSignOK() &&  checkSignResult.isCertOK()) {
                        Date signDate = new Date(Long.parseLong(args.get("signDate")));
                        String liabilityObjectUID = args.get("liabilityObjectUID");
                        KrnObject liabilityObject = null;
                        if (liabilityObjectUID.length() > 0) {
                            liabilityObject = wkrn.getObjectByUid(liabilityObjectUID, 0);
                        }
                        List<Object> params = new ArrayList<Object>();
                        params.add(c);
                        if (isNCAMode) {
                        	String signedData = args.get("signedData");
                    		Document doc = KalkanUtil.createXmlDocumentFromString(signedData, "UTF-8");
                    		NodeList nodeList = doc.getElementsByTagName("liabilityText");
                    		signedData = nodeList.item(0).getTextContent();
                    		signedData = new String(kz.gov.pki.kalkan.util.encoders.Base64.decode(signedData), "UTF-8");
                            params.add(signedData);
                        } else {
                            params.add(args.get("sign"));
                        }
                        params.add(signDate);
                        params.add(liabilityObject);
                        params.add(isNCAMode);
                        KrnClass utilCls = wkrn.getClassByName(wkrn.isRNDB() ? "ADM_util" : "Util");
                        int res = (Integer) wkrn.executeMethod(utilCls, utilCls, "подписать обязательство о неразглашении", params, 0);
                    	obj.add("signResult", res);
                        return obj.toString();
                    } else {
                    	obj.add("signResult", 0);
                    	obj.add("errorMessage", checkSignResult.getErrorMessage(false));
                        return obj.toString();
                    }
                } else {
                	obj.add("signResult", 0);
                	obj.add("errorMessage", "ИИН в сертификате " + userDN + " не соответствует ИИН пользователя " + iin + "!");
                    return obj.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            obj.add("signResult", 0);
            return obj.toString();
        } else if (args.get("polling") != null) {
            return longPolling();
		} else if ("getVarsValues".equals(cmd)) {
			// Подготовка списка переменных			
			JsonArray varArray = new JsonArray();
			String taskId = args.get("taskId");		
			// Read xml file
			byte[] data = wkrn.getBlob(new KrnObject(Long.parseLong(taskId),
					"", wkrn.SC_FLOW.id), "variables", 0, 0, -1);
			if (data.length > 0) {
				SAXBuilder builder = new SAXBuilder();
				org.jdom.Document doc = builder.build(new ByteArrayInputStream(
						data), "UTF-8");
				Element root = doc.getRootElement();
				List<Element> varList = root.getChildren();				
				// loop for var objects
				for (int i = 0; i < varList.size(); i++) {
					Element varTag = varList.get(i);
					JsonObject varObj = new JsonObject();
					org.jdom.Attribute nameAttribute = varTag
							.getAttribute("name");					
					if (varTag.getAttribute("type") != null) {
						org.jdom.Attribute typeAttribute = varTag
								.getAttribute("type");						
						if (typeAttribute.getValue().equals("list")) {
							List<Element> values = varTag.getChildren();							
							// loop for 100 child values inside var tag
							JsonObject childArrayObj = new JsonObject();
							JsonArray jv = new JsonArray();
							for (int j = 0; j < values.size(); j++) {
								
								if (j > 100) {
								    break;
								  } 
								else{
									Element valueTag = values.get(j);			
									JsonObject valueObj = new JsonObject();
									valueObj.add("id", 100 + j);
									valueObj.add("text","Value= " + valueTag.getText());	
									jv.add(valueObj);
								  }															
							}
							varObj.add("id", i);
							varObj.add("text",(nameAttribute.getValue() + "("+  typeAttribute.getValue() +")") );
							varObj.add("state", "closed");
							varObj.add("children", jv);						
						} 
						else if(typeAttribute.getValue().equals("map")){
							Element pairTag = varTag.getChild("pair");
							List<Element> pairs = pairTag.getChildren();
							JsonArray jm = new JsonArray();
							// loop for key-value pairs							
							for (int j = 0; j < pairs.size(); j+=2) {									
								Element keyValueTag = pairs.get(j);	
								Element keyValueTag2 = pairs.get(j+1);	
								JsonObject pairObj = new JsonObject();								
								pairObj.add("id", 200 + j);
								pairObj.add("text", keyValueTag.getName() + '-' + keyValueTag.getText() + "," + keyValueTag2.getName() + '-' + keyValueTag2.getText());	
								jm.add(pairObj);
							}
							varObj.add("id", i);
							varObj.add("text", (nameAttribute.getValue() + "("+  typeAttribute.getValue() +")"));
							varObj.add("state", "closed");
							varObj.add("children", jm);
							
						}
						else{
							varObj.add("id", i);
							varObj.add("text", nameAttribute.getValue() + "("+  typeAttribute.getValue() +") = " + varTag.getText().toString() ); 
						}							
														
					} else{
						varObj.add("id", i);
						varObj.add("text", nameAttribute.getValue() + varTag.getText().toString());
					}
							
					varArray.add(varObj);
				}		
				
			}					
			return varArray.toString();
			
        } else if ("addToFavorites".equals(cmd)) {
        	String processUID = args.get("processUID");
        	ProcessNode processNode = processHelper.getProcessByUID(processUID);
        	if (processNode != null) {
        		commonHelper.addToFavorites(processNode.getObject());
        		//addToFavorites(processNode);
        	}
        } else if ("gisOnSelectObjects".equals(cmd)) {
        	Map<String, String> urls = new HashMap<>();
        	urls.put("buildings", args.get("buildingsURL"));
        	urls.put("lands", args.get("landsURL"));
        	JSONObject ids = new JSONObject();
        	for (String key : urls.keySet()) {
	            URL url = new URL(urls.get(key));
	            URLConnection urlConnection = url.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	            String jsonString = in.readLine();
	            in.close();
	            
	            JSONObject json = new JSONObject(jsonString);
	            JSONArray features = json.getJSONArray("features");
	            ids.put(key, features);
        	}
            String uid = args.get("uid");
            WebFrame parentFrm = frameManager.getCurrentFrame();
            OrWebGISPanel comp = (OrWebGISPanel) parentFrm.getComponentByUID(uid);
            comp.getAdapter().fillData(ids);
        } else if ("requestFromGis".equals(cmd)) {
        	String val = args.get("body");
        	JSONObject json = new JSONObject(val);
            String uid = args.get("uid");
            WebFrame parentFrm = frameManager.getCurrentFrame();
            OrWebGISPanel comp = (OrWebGISPanel) parentFrm.getComponentByUID(uid);
            comp.getAdapter().fillData(json);
            sendCommand("refresh", "");
        } else if ("treegridZebraColors".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame parentFrm = frameManager.getCurrentFrame();
            OrWebTable comp = (OrWebTable) parentFrm.getComponentByUID(uid);
            if (comp != null) {
            	JsonObject res = new JsonObject();
	        	res.add("zebra1", comp.getZebra1());
	        	res.add("zebra2", comp.getZebra2());
	        	return res.toString();
            }
            return "{}";
        } else if ("isWrapNodeContent".equals(cmd)) {
        	JsonObject res = new JsonObject();
            String uid = args.get("uid");
            WebFrame parentFrm = frameManager.getCurrentFrame();
            WebComponent comp = parentFrm.getComponentByUID(uid);
            if (comp instanceof OrWebTree2) {
	            OrWebTree2 tree = (OrWebTree2) comp;
	        	res.add("isWrapNodeContent", tree.isWrapNodeContent() ? 1 : 0);
            } else {
	        	res.add("isWrapNodeContent", 0);
            }
        	return res.toString();
        } else if ("removeFromFavorites".equals(cmd)) {
        	String processUID = args.get("processUID");
        	ProcessNode processNode = processHelper.getProcessByUID(processUID);
        	if (processNode != null) {
        		commonHelper.removeFromFavorites(processNode.getObject());
        		//removeFromFavorites(processNode);
        	}
        } else if ("getUsedMemory".equals(cmd)) {
        	JsonObject res=new JsonObject();
        	res.add("result", "success");
        	String usedMem=CommonHelper.getUsedMemorySnapshot();
        	res.add("message", usedMem);
            return res.toString();
        } else if ("getTasksCount".equals(cmd)) {
        	JsonObject res=new JsonObject();
        	res.add("result", "success");
        	long tasksCount = taskHelper.getTasksCount();
        	res.add("message", tasksCount);
            return res.toString();
        } else if (args.get("ifcLeft") != null) {
            ifcManager.previous(false, false, true);
        	return "{}";
        } else if(args.get("setTooltipPref") != null) {
        	boolean isShowTooltip = !"false".equals(args.get("setTooltipPref"));
        	wkrn.setLong(wkrn.getUser().getObject().id, wkrn.getUser().getObject().classId, "showTooltip", 0, isShowTooltip ? 1 : 0, 0);
        	wkrn.getUser().setShowTooltip(isShowTooltip);
        	WebController.getSession(request, webUser.getGUID()).put("showTooltip", isShowTooltip);
        }else if(args.get("setNoteSoundPref") != null) {
        	boolean useNoteSound = !"false".equals(args.get("setNoteSoundPref"));
        	wkrn.setLong(wkrn.getUser().getObject().id, wkrn.getUser().getObject().classId, "useNoteSound", 0, useNoteSound ? 1 : 0, 0);
        	wkrn.getUser().setUseNoteSound(useNoteSound);
        	WebController.getSession(request, webUser.getGUID()).put("useNoteSound", useNoteSound);
        } else if(args.get("setInstantECPPref") != null) {
        	boolean isInstantECP = !"false".equals(args.get("setInstantECPPref"));
        	wkrn.setLong(wkrn.getUser().getObject().id, wkrn.getUser().getObject().classId, "instantECP", 0, isInstantECP ? 1 : 0, 0);
        	wkrn.getUser().setInstantECP(isInstantECP);
        	WebController.getSession(request, webUser.getGUID()).put("instantECP", isInstantECP);
        }
        else if (args.get("setLang") != null) {
        	WebLangItem li = LangHelper.getLangByCode(args.get("setLang"), configNumber);
        	setLangId(li.obj.id, true);
            setDataLangId(li.obj.id);
            if (taskHelper != null) taskHelper.setLangId(li.obj.id, true);
        	WebController.getSession(request, webUser.getGUID()).put("langCode", li.code);
        	wkrn.setObject(wkrn.getUser().getObject().id, wkrn.getUser().getObject().classId, "interface language", 0, li.obj.id, 0, false);
        	wkrn.getUser().setIfcLang(li.obj);
        	wkrn.getUser().setDataLanguage(li.obj);
        	return "";
        } else if (args.get("setTheme") != null) {
        	long theme = Long.parseLong(args.get("setTheme"));
        	WebController.getSession(request, webUser.getGUID()).put("theme", theme);
        	try {
        		wkrn.setLong(wkrn.getUser().getObject().id, wkrn.getUser().getObject().classId, "theme",
        			0, theme, 0);
        	} catch (KrnException e) {
        		log.error(e, e);
        	}
        	return "";
        } else if ("loadTasks".equals(cmd)) {
        	String rowFirst = args.get("rowFirst");
        	String rowLast = args.get("rowLast");
        	String sortBy = args.get("sort");
        	String sortDesc = args.get("desc");
        	String searchText = args.get("searchText");
        	taskHelper.setFilterParam(rowFirst, rowLast, sortBy, sortDesc, searchText);
            return ViewHelper.getActiveTasksJSON(taskHelper.getTable(), this);
        } else if ("loadSessions".equals(cmd)) {
        	UserSessionValue[] res = null;
        	try {
        		res = wkrn.getUserSessions();
        	} catch (KrnException e) {
        		sendMultipleCommand("alert", e.getMessage());
        	}
            return ViewHelper.getUserSessionsJSON(res, this);
        } else if ("loadTasksCount".equals(cmd)) {
            return ViewHelper.getActiveTasksCountJSON(taskHelper.getTable(), this);
        } else if ("loadProcesses".equals(cmd)) {
            SortedSet<ProcessNode> tabs = processHelper.getTabs();
            return ViewHelper.getProcessesJSON(tabs, resource, processHelper, this);
        } else if ("getFavoriteProcesses".equals(cmd)) {
        	/*KrnClass favouriteProcessCls = wkrn.getClassByName("UserFavouriteProcess");
        	KrnAttribute userAttr = wkrn.getAttributeByName(favouriteProcessCls, "user");
        	KrnAttribute processDefAttr = wkrn.getAttributeByName(favouriteProcessCls, "processDef");
        	KrnObject user = wkrn.getUser().getObject();
        	KrnObject[] objsByUser = wkrn.getObjectsByAttribute(favouriteProcessCls.id, userAttr.id, 0, 0, user, 0);
        	List<KrnObject> favouriteProcesses = new ArrayList<KrnObject>();
        	for (int i = 0; i < objsByUser.length; i++) {
        		KrnObject favouriteProcess = wkrn.getObjectsSingular(objsByUser[i].id, processDefAttr.id, false);
        		if (favouriteProcess != null) {
        			favouriteProcesses.add(favouriteProcess);
        		}
        	}*/
        	return ViewHelper.getFavoriteProcessesJSON(processHelper, this, interfaceLangId, commonHelper.getUserFavouriteProcesses());
        } else if ("getProcessData".equals(cmd)) {
        	String id = args.get("id");
        	boolean loadLeafs = args.get("leaf") != null;
            return ViewHelper.getProcessFolderJSON(id, loadLeafs, processHelper, this, interfaceLangId);
        } else if ("getArchiveData".equals(cmd)) {
        	String id = args.get("id");
        	boolean loadLeafs = args.get("leaf") != null;
            return archiveHelper.getArchiveFolderJSON(this, id, loadLeafs, archiveHelper.getRoot(), interfaceLangId);
        } else if ("getDictData".equals(cmd)) {
        	String id = args.get("id");
        	boolean loadLeafs = args.get("leaf") != null;
            return archiveHelper.getArchiveFolderJSON(this, id, loadLeafs, archiveHelper.getDictRoot(), interfaceLangId);
        } else if ("getAdminData".equals(cmd)) {
        	String id = args.get("id");
        	boolean loadLeafs = args.get("leaf") != null;
            return archiveHelper.getArchiveFolderJSON(this, id, loadLeafs, archiveHelper.getAdminRoot(), interfaceLangId);
        } else if ("getAnalyticData".equals(cmd)) {
        	String uid = args.get("uid");
        	ArchiveHelper.HyperNode node = archiveHelper.getNode(uid);
        	if (node != null) {
                return archiveHelper.getArchiveFolderJSON(this, null, true, node, interfaceLangId);
        	}
        	else
        		return "[]";
        } else if ("loadHelpNotes".equals(cmd)) {
        	JsonObject res = new JsonObject();

        	List<Object> helpObjs = getHelpObjs();
        	JsonArray arr = new JsonArray();
        	for (int i=0; i<helpObjs.size(); i++) {
        		Object obj = helpObjs.get(i);
        		
        		if (obj instanceof OrWebNoteBrowser) {
	        		OrWebNoteBrowser b = (OrWebNoteBrowser) obj;
	        		
	            	JsonObject item = new JsonObject();
	            	item.add("id", i);
	            	item.add("title", b.getTitle());
	            	item.add("type", 1);
	            	arr.add(item);
        		} else {
        			HelpFile hFile = (HelpFile) obj;
        			
	            	JsonObject item = new JsonObject();
	            	item.add("id", i);
	            	item.add("title", hFile.getTitle(interfaceLangId));
	            	item.add("type", 2);
	            	arr.add(item);
        		}
        	}
        	
        	res.add("helps", arr);
        	return res.toString();
        } else if ("getHelpTree".equals(cmd)) {
        	String hid = args.get("hid");
        	String uid = args.get("uid");
    		String id = args.get("id");
        	
        	if (hid != null && hid.length() > 0) {
        		int helpIndex = Integer.parseInt(hid);
        	
        		List<Object> helpObjs = getHelpObjs();
        		OrWebNoteBrowser b = (OrWebNoteBrowser)helpObjs.get(helpIndex);
        		return b.getFolderJSON(id, interfaceLangId);
        	} else {
        		WebFrame frm = frameManager.getCurrentFrame();
                WebComponent comp = frm.getComponentByUID(uid);
                
                if (comp instanceof OrWebNote) {
                	OrWebNoteBrowser b = ((OrWebNote)comp).getWebNoteBrowser();
                	return b.getFolderJSON(id, interfaceLangId);
                }
        	}
        	return "[]";
        } else if ("getHelpNoteContent".equals(cmd)) {
        	String hid = args.get("hid");
        	String uid = args.get("uid");
    		String id = args.get("id");

        	if (hid != null && hid.length() > 0) {
        		int helpIndex = Integer.parseInt(hid);
        	
            	List<Object> helpObjs = getHelpObjs();
        		Object obj = helpObjs.get(helpIndex);
        		
        		if (obj instanceof OrWebNoteBrowser) {
            		OrWebNoteBrowser b = (OrWebNoteBrowser) obj;
            		return b.getContent(id);
        		} else {
        			HelpFile hFile = (HelpFile) obj;
        			
        			if (hFile.getFSFileName() == null) {
        				File dir = WebController.WEB_DOCS_DIRECTORY;
        				File helpFile = Funcs.createTempFile("help", hFile.getFileExtension(), dir);
        				this.deleteOnExit(helpFile);
        				OutputStream os = new FileOutputStream(helpFile);
        				os.write(hFile.getContent());
        				os.close();
        				
        				hFile.setFSFileName(helpFile.getName());
        			}

    	            args.put("cmd", "opf");
    	            args.put("fn", hFile.getFSFileName());
    	            args.put("fr", hFile.getFileName());
    	            openFile(args, response, request);
    	            return null;
        		}
        	} else {
        		WebFrame frm = frameManager.getCurrentFrame();
                WebComponent comp = frm.getComponentByUID(uid);
                
                if (comp instanceof OrWebNote) {
                	OrWebNoteBrowser b = ((OrWebNote)comp).getWebNoteBrowser();
            		return b.getContent(id);
                }
        	}
        } else if ("loadArchive".equals(cmd)) {
        	JsonObject res = new JsonObject();
        	JsonArray arr = archiveHelper.getArchiveNodeJSON(this, archiveHelper.getRoot(), interfaceLangId);
        	res.add("nodes", arr);
        	return res.toString();
        } else if ("loadDict".equals(cmd)) {
        	JsonObject res = new JsonObject();
        	JsonArray arr = archiveHelper.getArchiveNodeJSON(this, archiveHelper.getDictRoot(), interfaceLangId);
        	res.add("nodes", arr);
        	return res.toString();
        } else if("getUserPrivateDeal".equals(cmd)) {
        	String text = args.get("text");
        	return ViewHelper.getUsersArray(text, this);
        } else if("openLDIfc".equals(cmd)) {
        	String uid = args.get("uid");
        	taskHelper.clearAutoIfcFlowId(0);
            frameManager.clearFrame();
            ifcManager.openLDIfc(uid, this);
            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null) ((WebPanel)frm.getPanel()).putJSON();
            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
            
        } else if("openTaskIntf".equals(cmd)) {
        	String objUid = args.get("objUid");
        	String intUid = args.get("intUid");
        	
        	taskHelper.clearAutoIfcFlowId(0);
            frameManager.clearFrame();
            ifcManager.openTaskIntf(objUid, intUid, this);
            WebFrame frm = frameManager.getCurrentFrame();
            if((WebPanel)frm.getPanel()!=null) {
            	if (frm != null) ((WebPanel)frm.getPanel()).putJSON();
                return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);	
            }
        //Проверяет объект на существование в базе данных по его UID
        } else if("checkIfUIDIsCorrect".equals(cmd)) {
          	String UID = args.get("UID");
        	KrnObject obj = getKernel().getObjectByUid(UID, -1);
        	JsonObject res = new JsonObject();
            if(obj!=null){
            	res.set("result", "success");
            } else{
            	res.set("result", "error");
            }
            return res.toString();
                
        } else if("searchProcess".equals(cmd)){
        	String text = args.get("text");
        	String index = args.get("index");
            return ViewHelper.getSearchProcessJSON(text, processHelper, interfaceLangId, index);
        } else if("searchDict".equals(cmd)){
        	String text = args.get("text");
        	String index = args.get("index");
        	JsonObject res = archiveHelper.searchArchiveByName(this, archiveHelper.getDictRoot(), interfaceLangId, text, index);
        	return res.toString();
        } else if("searchArchive".equals(cmd)) {
        	String text = args.get("text");
        	String index = args.get("index");
        	JsonObject res = archiveHelper.searchArchiveByName(this, archiveHelper.getRoot(), interfaceLangId, text, index);
        	return res.toString();
        } else if("searchAdmin".equals(cmd)) {
        	String text = args.get("text");
        	String index = args.get("index");
        	JsonObject res = archiveHelper.searchArchiveByName(this, archiveHelper.getAdminRoot(), interfaceLangId, text, index);
        	return res.toString();
        } else if("searchChatUsers".equals(cmd)) {
        	
        	String searchStr = args.get("search");
        	String searchMethod = args.get("method");

	    	JsonObject res = new JsonObject();
	    	JsonArray arr = new JsonArray();
	    	res.add("rows", arr);
	    	
	    	if (searchStr != null) {
	    		Map<String, String> params = new HashMap<>();
	    		params.put("cls", "WsUtilNew");
	    		params.put("name", searchMethod);
	    		params.put("arg0", searchStr);

	    		params.put("arg1", args.get("page"));
	    		params.put("arg2", args.get("rows"));

	    		Map<String, Object> ret = (Map<String, Object>) executeMethod(params, true);
	    		
		    	res.add("total", ret.get("count"));
		    	
		    	List<JsonObject> list = (List<JsonObject>) ret.get("list");
		        for (JsonObject row : list) {
		        	String jid = row.get("jid") != null ? row.get("jid").asString() : "";
		        	String name = row.get("name") != null ? row.get("name").asString() : row.get("field0") != null ? row.get("field0").asString() : "";
		        	row.add("action", "<button type=\"button\" class=\"btn btn-success\" onclick=\"chatAddContact(this)\" data-jid=\"" + jid + "\" data-name=\"" + name + "\"><i class=\"icon-plus icon-white\"></i> Пригласить</button>");
		            arr.add(row);
		        }
        	} else {
		    	res.add("total", 0);
        	}
	
	        return res.toString();
        } else if ("taskType".equals(cmd)) {
            String id = args.get("uid");
            Activity act = null;
            JsonObject res = new JsonObject();
            if (id != null) {
                long flowId = Long.parseLong(id);
                act = taskHelper.getActivityById(flowId);
            	
                if (act == null) {
		            act = wkrn.getTask(flowId, 0, true, true);
                }

            	if (act == null) {
                    res.add("type", "noop");
                } else if (ACT_ARTICLE_STRING.equals(act.uiType)) {
                	byte[] htmlBuf = null;
                    if (act.ui != null) {
        	            KrnAttribute htmlAttr = wkrn.getAttributeByName(wkrn.getClass(act.ui.classId), "htmlTemplate");
        	            if (htmlAttr != null) {
        	                long lid = act.articleLang != null ? act.articleLang.id : 0;
        	                if (lid <= 0 && wkrn.getAttributeByName(wkrn.getClassByName("Flow"), "article_lang") != null) {
            	                KrnObject flow = new KrnObject(act.flowId, "", Kernel.SC_FLOW.id);
        	                    KrnObject[] langs = wkrn.getObjects(flow, "article_lang", -1);
        	                    lid = (langs != null && langs.length > 0) ? langs[0].id : 0;
        	                }
        	                htmlBuf = wkrn.getBlob(act.ui, "htmlTemplate", 0, lid, 0);
        	            }
                    }
                    if (htmlBuf != null && htmlBuf.length > 0)
                    	res.add("type", "htmlreport");
                    else
                    	res.add("type", "report");
                } else if (ACT_FASTREPORT_STRING.equals(act.uiType)) {
                    res.add("type", "fastreport");
                } else if (ACT_AUTO_STRING.equals(act.uiType) && (act.ui != null && act.ui.id > 0)) {
                    res.add("type", "choose");
                } else if (ACT_DIALOG_STRING.equals(act.uiType) && (act.ui != null && act.ui.id > 0)) {
                    res.add("type", "dialog");
                } else if (act.ui != null && act.ui.id > 0) {
                    res.add("type", "ifc");
                } else if (act.transitions != null && act.transitions.length > 1) {
                    res.add("type", "option");
                } else if ((act.uiType == null || act.uiType.length() == 0) && (act.ui == null || act.ui.id <= 0)) {
                    res.add("type", "undefined");
                } else {
                    res.add("type", "error");
                    res.add("msg", "Ошибка при работе с задачей!");
                }
            }
            return res.toString();
        } else if ("openTask".equals(cmd)) {
            String id = args.get("uid");
            //boolean isPrevProc = "1".equals(args.get("isPrevProc"));
            boolean dlg = false;
            Activity act = null;

            if (id != null) {
                long flowId = Long.parseLong(id);
                act = taskHelper.getActivityById(flowId);
                if (act == null) {// && isPrevProc) {
		            act = wkrn.getTask(flowId, 0, true, true);
                }

                if (act == null) {
                    log.info("Задача не найдена! sid:" + getId() + ", flow: " + id);
                	JsonObject obj = new JsonObject();
                    obj.add("message","Задача не найдена!");
                    obj.add("result", "error");
                	return obj.toString();
                    //throw new LoginException(0, "Задача не найдена!", configNumber);
                }
                
                if (act.transitions != null && act.transitions.length > 0 && (act.ui == null || act.ui.id <= 0) && !ACT_ARTICLE_STRING.equals(act.uiType)) {
                	JsonObject obj = new JsonObject();
                    String res_ = "";
                	if(act.transitions.length>1){
	                    String[] trs = new String[act.transitions.length];
	                    for (int i = 0; i < trs.length; ++i) {
	                        trs[i] = act.transitions[i].substring(0, act.transitions[i].indexOf(","));
	                    }
	                    int result_ = frameManager.getCurrentFrame().getOption(trs);
	                    if (result_ > -1) {
	                        res_ = act.transitions[result_].substring(act.transitions[result_].lastIndexOf(",") + 1);
	                    }
	                    
	                    wkrn.setPermitPerform(act.flowId, true);
	                    
	                	long oldId = act.ui.id;
	                	long oldInfId = act.ui.id;
	                	int oldPermit = (int) act.param & ACT_PERMIT;
	                    try {
	                    	taskHelper.disableActivity(act);

		                    String[] res_s = wkrn.performActivitys(new Activity[] { act }, res_);
		                    if (res_s.length > 0) {
		                        if (res_s.length == 1 && res_s[0].equals("synch")) {
		                            taskHelper.setAutoIfcFlowId_(act.flowId);
		                        } else {
			                        taskHelper.reenableActivity(act, oldId, oldInfId, oldPermit);
		                            // обработка ошибок
		                            String msg_ = res_s[0];
		                            for (int i = 1; i < res_s.length; ++i)
		                                msg_ += "\n" + res_s[i];
		
			                        obj.add("message", msg_.replaceFirst("^\\!", ""));
			                        obj.add("result", "error");
			                        return obj.toString();
		                        }
		                    }
		                    taskHelper.reenableActivity(act.flowId);
		                    taskHelper.setAutoIfcFlowId_(act.flowId);
		                    obj.add("result", "success");
		
		                    if (res_s.length == 1 && res_s[0].equals("synch")) {
		                    	taskHelper.reloadTask(act.flowId, act.ui.id > 0 && act.infUi.id > 0 ? 2
		                                : act.infUi.id > 0 ? 1 : 0, true, true);
		                    }
	                    } catch (KrnException e) {
	                        taskHelper.reenableActivity(act, oldId, oldInfId, oldPermit);
	                        log.error(e, e);
	                    }
                	}else{
                        obj.add("message","Идет выполнение задачи!");
                        obj.add("result", "error");
                        log.info("Идет выполнение задачи! sid:" + getId() + ", flow: " + id);
                	}
                	return obj.toString();
                } else if (ACT_ARTICLE_STRING.equals(act.uiType)) {
                    taskHelper.clearAutoIfcFlowId(act.flowId);
                    taskHelper.checkOpenUI(act.ui);
                    taskHelper.closeAutoAct();
                    Object res = getKernel().openInterface(act.ui.id,act.flowId,act.trId,act.processDefId.length>0?act.processDefId[0]:-1);
                    String fn = taskHelper.generateReport2(args);
                    args.put("cmd", "opf");
                    args.put("fn", fn);
                    openFile(args, response, request);
                    
                    if (res instanceof Number && ((Number)res).intValue() == 1) {
	                    args.put("id", "" + act.flowId);
	                    taskHelper.nextStep(args);
                    }
                    return null;
                } else if (ACT_FASTREPORT_STRING.equals(act.uiType)) {
                    taskHelper.clearAutoIfcFlowId(act.flowId);
                    taskHelper.checkOpenUI(act.ui);
                    taskHelper.closeAutoAct();
                    Object res = getKernel().openInterface(act.ui.id,act.flowId,act.trId,act.processDefId.length>0?act.processDefId[0]:-1);
                    String fn = taskHelper.generateFastReport2(args);
                    args.put("cmd", "opf");
                    args.put("fn", fn);
                    openFile(args, response, request);
                    
                    if (res instanceof Number && ((Number)res).intValue() == 1) {
	                    args.put("id", "" + act.flowId);
	                    taskHelper.nextStep(args);
                    }
                    return null;
                }
                
                int waitCount = CHECK_PROCCESS_STARTED_COUNT;
                boolean waitReseted = false;
                
                while (act == null || act.ui == null || act.ui.id <= 0) {
                	Thread.sleep(CHECK_PROCCESS_STARTED_TIMEOUT);
                	act = taskHelper.getActivityById(flowId);
                    if (act == null)
                    	act = taskHelper.getActivityByRootFlowId(flowId);
                    if (act == null)
                    	act = taskHelper.getActivityBySuperFlowId(flowId);
                    if (waitCount == 0) {
                    	log.error("!!! NO UI ERROR - SHOW IT TO DEVELOPER !!!");
                        return "No UI error";
                    }
                    
                    if (act == null) {
                    	waitCount--;
                    } else if (!waitReseted) {
                    	waitReseted = true;
                    	waitCount = CHECK_PROCCESS_STARTED_COUNT;
                    }
                }

                taskHelper.clearAutoIfcFlowId(act.flowId);

                dlg = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType);

                taskHelper.checkOpenUI(act.ui);
                
                if (!dlg) {
                    frameManager.clearFrame();
                    ifcManager.openActivityInterface(act);
                } else {
                	if (args.get("size") != null)
                		ifcManager.openAutoActivityInterface(act);
                }

                WebFrame frm = frameManager.getCurrentFrame();
            	if (args.get("size") != null || !dlg) {
	                if (frm != null && frm.getPanel() != null)
	                    ((WebPanel) frm.getPanel()).putJSON();
            	}
            	
            	if (args.get("size") != null) {
            		JsonObject res = new JsonObject();

                	OrPanelComponent p = frm.getPanel();
                    String title = p.getTitle();
                    
                    int w = ((WebComponent)p).getMaxWidth();
                    int h = ((WebComponent)p).getMaxHeight();
                    
                    res.add("w", (w > 0) ? w + Constants.WEB_DIALOG_EXTRA_WIDTH : 800);
                    res.add("h", (h > 0) ? h + Constants.WEB_DIALOG_EXTRA_HEIGHT : 600);
                    res.add("t", title);

                    return res.toString();
            	} else {
            		return TemplateHelper.load(webUser.getGUID(), act.ui.uid, interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
            	}
            }
        } 
        else if ("prevUI".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            ifcManager.reopen(frm);
            sendCommand("stack", getFrameManager().getStackFrames());
            ((WebPanel)frm.getPanel()).putJSON();
            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openMainIfc".equals(cmd)) {
            if (ifcManager.getMainUI() != null) {
                frameManager.clearFrame2();
                ifcManager.absolute(null, ifcManager.getMainUI().getObj(), null, null, 
                		wkrn.isSE_UI() ? ARCH_RO_MODE : InterfaceManager.ARCH_RW_MODE,
                		false, wkrn.isSE_UI() ? -1 : 0, false, 0, true, "");
                WebFrame frm = frameManager.getCurrentFrame();
                if (frm != null) {
                    ((WebPanel) frm.getPanel()).putJSON();
                }
                return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
            }
        } else if ("openOrder".equals(cmd)) {
            taskHelper.clearAutoIfcFlowId(0);

            String iuid = args.get("iuid");
            String ouid = args.get("ouid");
            frameManager.clearFrame();
            ifcManager.openOrderInterface(iuid,ouid);

            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null) ((WebPanel)frm.getPanel()).putJSON();

            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openArch".equals(cmd)) {
            taskHelper.clearAutoIfcFlowId(0);

            String uid = args.get("uid");
            ArchiveHelper.HyperNode node = archiveHelper.getNode(uid);
            frameManager.clearFrame();
            ifcManager.openArchiveInterface(node);

            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null) {
            	WebPanel panel = (WebPanel) frm.getPanel();
            	if(panel != null)
            		panel.putJSON();
            	else {
            		JsonObject res = new JsonObject();
                    	res.set("result", "error");
                    	res.set("message", " ... ИНТЕРФЕЙС НЕ ЗАДАН! ... ");
                    return res.toString();
            	}
            }

            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openDict".equals(cmd)) {
            taskHelper.clearAutoIfcFlowId(0);
            frameManager.clearFrame();

            String uid = args.get("uid");
            ArchiveHelper.HyperNode node = archiveHelper.getNode(uid);
            
            if (node == null) {
            	KrnObject ifc = wkrn.getObjectByUid(uid, 0);
            	ifcManager.openDictInterface(ifc);
            } else
            	ifcManager.openDictInterface(node);

            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null) ((WebPanel)frm.getPanel()).putJSON();

            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openAdmin".equals(cmd)) {
            String uid = args.get("uid");
            ArchiveHelper.HyperNode node = archiveHelper.getNode(uid);
            frameManager.clearFrame();
            ifcManager.openAdminInterface(node);

            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null) ((WebPanel)frm.getPanel()).putJSON();

            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("previewIfc".equals(cmd)) {
            String uid = args.get("uid");

            return TemplateHelper.load(webUser.getGUID(), uid, interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openIfc".equals(cmd)) {
            String uid = args.get("uid");
            
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            
            boolean evalBeforeOpen = true;
            
            if (comp == null) {
            	frameManager.prev();
                frm = frameManager.getCurrentFrame();
                comp = frm.getComponentByUID(uid);
                evalBeforeOpen = false;
            }
            
            if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
                String row = args.get("row");
                String colUid = args.get("cuid");
                if (row != null && colUid != null) {
                    table.forward(Integer.parseInt(row), colUid, evalBeforeOpen);
                }
            } else if (comp instanceof OrWebHyperLabel) {
                ((OrWebHyperLabel) comp).forward(evalBeforeOpen);
            } else if (comp instanceof OrWebHyperColumn) {
                WebComponent editor = (WebComponent) ((OrWebHyperColumn)comp).getEditor();
                if (editor instanceof OrWebHyperLabel) {
                    ((OrWebHyperLabel) editor).forward(evalBeforeOpen);
                }
            }

            frm = frameManager.getCurrentFrame();
            if (frm != null) ((WebPanel)frm.getPanel()).putJSON();
            
            if (comp == null)
                sendCommand("stack", getFrameManager().getStackFrames());

            return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("openProcess".equals(cmd)) {
            String uid = args.get("uid");
            String obj = args.get("obj");
            KrnObject defObj = getKernel().getObjectByUid(uid, -1);
            KrnObject objObj = getKernel().getObjectByUid(obj, -1);
            JsonObject res = new JsonObject();
            if (defObj == null) {
                res.add("result", "error").add("acts", 0).add("message", "Не найден процесс uid = " + uid);
            } else if (objObj == null) {
                res.add("result", "error").add("acts", 0).add("message", "Не найден объект uid = " + obj);
            } else {
	           	List<Activity> acts = taskHelper.findProcess(defObj, objObj, false, false);
	            if (acts.size() == 1) {
	                Activity act = acts.get(0);
	                if ((act.param & ACT_ERR) != ACT_ERR) {
	                	if (act.ui != null && act.ui.id > 0) {
			                res.add("result", "success");
			                res.add("uid", act.flowId);
			                boolean dlg = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType);
			                res.add("mode", dlg ? "dialog" : "window");
	                	} else {
	                		if (act.timeActive > 0) {
		                    	res.add("message", getResource().getString("processActive"));
	                		} else if (act.actorId > 0) {
	                			String flowUserName = getUserNameById(act.actorId, getInterfaceLangId());
		                    	res.add("message", getResource().getString("processEngagedByUser").replace("{1}", flowUserName));
	                		} else {
		                    	res.add("message", getResource().getString("processEngaged"));
	                		}
	                        res.add("result", "error");
	                	}
	                } else {
	                    res.add("result", "error");
	                	res.add("message", getResource().getString("processStateError"));
	                }
	            } else if (acts.size() > 1) {
	                res.add("result", "error").add("acts", acts.size()).add("message", getResource().getString("processManyFound").replace("{1}", String.valueOf(acts.size())));
	            } else {
	                res.add("result", "error").add("acts", 0).add("message", getResource().getString("processNotFound"));
	            }
            }
            return res.toString();
        } else if ("startProcess".equals(cmd)) {
            taskHelper.clearAutoIfcFlowId(0);

            String uid = args.get("uid");
            String obj = args.get("obj");
            
            Object pr = null;
            
        	WebFrame frm = frameManager.getCurrentFrame();
           if (obj == null) {
                pr = processHelper.createProcess(uid, null,frm);
            } else {
            	KrnObject objObj = getKernel().getObjectByUid(obj, -1);
            	
				Map<String, Object> vars = new HashMap<String, Object>();
				List<KrnObject> poruchs = new ArrayList<KrnObject>();
				poruchs.add(objObj);
				vars.put("OBJS", poruchs);
					
	            pr = processHelper.createProcess(uid, vars,frm);
            }
            
            JsonObject res = new JsonObject();
            if (pr instanceof Activity) {
                res.add("result", "success");
                
                long id = this.getKernel().getObjectByUid(uid, 0).id;
                commonHelper.setUserNotOpenProcessDef(id);
                
                long flowId = ((Activity)pr).flowId;
                
                int waitCount = CHECK_PROCCESS_STARTED_COUNT;
                boolean waitReseted = false;
                
                Activity act = getReadyToOpenActivity(flowId);
                if (act != null && act.infMsg != null && act.infMsg.length() > 0) {
                	res.add("infMsg", act.infMsg);
                }
                
                while (act == null || ((act.uiType == null || act.uiType.length() == 0) && (act.param & Constants.ACT_ERR) != Constants.ACT_ERR)) {
                	Thread.sleep(CHECK_PROCCESS_STARTED_TIMEOUT);
                	
                	act = getReadyToOpenActivity(flowId);
                    
                	if (waitCount == 0) {
                		res.set("result", "error");
                    	res.add("message", getResource().getString("processStartTimeout").replace("{1}", String.valueOf(CHECK_PROCCESS_STARTED_COUNT*CHECK_PROCCESS_STARTED_TIMEOUT/1000)));
                        return res.toString();
                    }
                    
                    if (act == null) {
                    	waitCount--;
                    } else if (!waitReseted) {
                    	waitReseted = true;
                    	waitCount = CHECK_PROCCESS_STARTED_COUNT;
                    }
                    
                	if ((act!=null && (act.param & Constants.ACT_AUTO_NEXT) == Constants.ACT_AUTO_NEXT)) {
                        res.set("result", "success");
                        return res.toString();
                    }
                }
                
                if ((act.param & Constants.ACT_ERR) == Constants.ACT_ERR) {
                    res.set("result", "error");
                	res.add("message", getResource().getString("processStartError"));
                    return res.toString();
                }
                
                if (obj != null) {
                	waitCount = CHECK_PROCCESS_STARTED_COUNT;
                    while (act.objs == null || act.objs.length == 0) {
                    	Thread.sleep(CHECK_PROCCESS_STARTED_TIMEOUT);
                    	act = taskHelper.getActivityById(act.flowId);
                    	if (act == null || waitCount-- == 0) {
                            res.set("result", "error");
                        	res.add("message", getResource().getString("processStartError"));
                            return res.toString();
                    	}
                    }
                }

                res.add("uid", act.flowId);
                String mode = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType)
                		? "dialog" : Constants.ACT_NO_UI.equals(act.uiType) ? "no" : "window";
                res.add("mode", mode);
            }else if(pr instanceof Integer && ButtonsFactory.BUTTON_NOACTION==(Integer)pr) {
            	res.set("result", "success");
            } else {
                res.add("result", "error");
                if (pr instanceof String) {
                	res.add("message", pr.toString().replaceFirst("^\\!", ""));
                }
            }
            return res.toString();
        } else if ("kill".equals(cmd)) {
            taskHelper.killProcess(args);
        } else if ("taskReload".equals(cmd)) {
            boolean b = taskHelper.reloadFlow(Long.parseLong(args.get("id")));
            JsonObject res = new JsonObject();
            if (b) {
            	res.set("result", "success");
        		res.add("message", "Процесс успешно перегружен");
            } else {
            	res.set("result", "error");
        		res.add("message", "Процесс перегрузить не удалось!");
        	}
            return res.toString();
        } else if ("tasksRefreshing".equals(cmd)) {
            String value = args.get("value");
            if ("1".equals(value)) {
            	tasksRefreshing = true;
            	for (int i = 0; i < tasksToRefreshing.size(); i++) {
                	sendMultipleCommand((String) tasksToRefreshing.get(i)[0], tasksToRefreshing.get(i)[1]);
            	}
            	tasksToRefreshing.clear();
            } else {
            	tasksRefreshing = false;
            }
        } else if ("killSession".equals(cmd)) {
        	wkrn.killUserSession(UUID.fromString(args.get("id")), false);
        } else if ("sendMessage".equals(cmd)) {
        	wkrn.sendMessage(UUID.fromString(args.get("id")), args.get("msg"));
        } else if ("oldFlowPanRemove".equals(cmd)) {
        	Map<String, Object> s = WebController.getSession(request, webUser.getGUID());
        	s.put("daysOldFlows", "-1");
        	return "{}";
        } else if (args.get("confirm") != null) {
        	int cres = Integer.parseInt(args.get("confirm"));
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setConfirm(cres);
            return "";
        } else if (args.get("promptAction") != null) {
        	int action = Integer.parseInt(args.get("promptAction"));
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setPassword(args.get("promptRes"), action);
            return "";
        } else if (args.get("alert") != null) {
        	WebFrame frm = frameManager.getWaitingFrame();
        	if (frm != null)
        		frm.wakeupFrameAction();
        } else if (args.get("commitResult") != null) {
        	WebFrame frm = frameManager.getCurrentFrame();
        	frm.setCommitAction(Integer.parseInt(args.get("commitResult")));
        	return "";
        } else if (args.get("optionResult") != null) {
        	WebFrame frm = frameManager.getCurrentFrame();
        	frm.setOptionRes(Integer.parseInt(args.get("optionResult")));
        	return "";
        } else if (args.get("signres") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setSignValue(args.get("signres"));
            
            if (args.get("cert") != null && args.get("cert").length() > 0) setCert(args.get("cert"));
            if (args.get("path") != null && args.get("path").length() > 0) setProfile(args.get("path"));
            if (args.get("cont") != null && args.get("cont").length() > 0) setProfileConteiner(args.get("cont"));
            if (args.get("code") != null && args.get("code").length() > 0) setProfilePassword(args.get("code"));
        } else if ("signTexWithNCAResult".equals(cmd)) {
        	String error = args.get("error");
			WebFrame frm = frameManager.getCurrentFrame();
        	if (error == null) {
            	String signedData = args.get("signedData");
            	X509Certificate cert = null;
            	try {
            		cert = KalkanUtil.getCertificate(signedData);
            	} catch (Exception e) {
	    			log.error(e, e);
            	}
                CheckSignResult checkSignResult = null;
            	try {
            		checkSignResult = KalkanUtil.checkXML(signedData);
            	} catch (Exception e) {
	    			log.error(e, e);
            	}
    			frm.setSignDataResult(true, signedData, cert, checkSignResult);

                if (args.get("path") != null && args.get("path").length() > 0) setNCAProfile(args.get("path"));
                if (args.get("cont") != null && args.get("cont").length() > 0) setNCAProfileConteiner(args.get("cont"));
                if (args.get("code") != null && args.get("code").length() > 0) setNCAProfilePassword(args.get("code"));

//                if (checkSignResult.isDigiSignOK() &&  checkSignResult.isCertOK()) {
//                    Date signDate = new Date(Long.parseLong(args.get("signDate")));
//            		Document doc = KalkanUtil.createXmlDocumentFromString(signedData, "UTF-8");
//            		NodeList nodeList = doc.getElementsByTagName("signText");
//            		signedData = nodeList.item(0).getTextContent();
//            		signedData = new String(kz.gov.pki.kalkan.util.encoders.Base64.decode(signedData), "UTF-8");
//        			frm.setSignedData(args.get("signedData"));
//                }
        	} else {
    			frm.setSignDataResult(false, error, null, null);
        	}
        } else if (args.get("ucgores") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setUcgoResponse(args.get("ucgores"));
        // ответ из JavaScript о подключении к вебсокет
        } else if (args.get("ucgosocketconn") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setUcgoResponse(args.get("ucgosocketconn"));
        } else if (args.get("wsres[id]") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            
            try {
            	JsonObject res = getJsonParameter(args, "wsres");

            	String func = res.get("function") != null ? res.get("function").asString() : null;
            	String scanId = res.get("id") != null ? res.get("id").asString() : null;
            	
            	String waitingRequest = frm.getWsRequest();
            	
            	if ("scan".equals(func) && (waitingRequest == null || !waitingRequest.equals("startScan " + scanId)))
            		frm.doMessageRecieved("scan", res);
            	else if ("openFiles".equals(func) && (waitingRequest == null || !waitingRequest.equals("openClientFiles " + scanId)))
            		frm.doMessageRecieved("openFile", res);
            	else
            		frm.setWsResponse(res.toString());
            	
            	
            } catch (Exception e) {
            	log.error(e, e);
            	frm.setWsResponse("{\"error\": " + e.getMessage() + "}");
            }
        // ответ из JavaScript о подключении к вебсокет
        } else if (args.get("scansocketconn") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setWsResponse(args.get("scansocketconn"));
        } else if ("reload".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame parentFrm = frameManager.getCurrentFrame();
            WebFrame frm = frameManager.getFrameByPanelUid(uid);
            WebComponent comp = parentFrm.getComponentByUID(uid);
            if (frm != null && comp != null) {
	        	try {
	    			parentFrm.getSession().getInterfaceManager().absolute(frm, parentFrm, null);
                    ((WebPanel) frm.getPanel()).putJSON();
	    		} catch (KrnException e) {
	    			log.error(e, e);
	    		}
	            return comp.getHtml();
            }
        } else if ("clr".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebHyperPopup) {
                ((OrWebHyperPopup)comp).clearValue();
            } else if (comp instanceof OrWebTreeField) {
                ((OrWebTreeField)comp).clearValue();
            }
            return getChanges();
        } else if ("hidePopUp".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebPopUpPanel) {
               ((OrWebPopUpPanel)comp).setVisible(false, args.get("t").equals("b"));
            } 
        } else if ("showPopUp".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebPopUpPanel) {
                ((OrWebPopUpPanel)comp).setVisible(true, args.get("t").equals("b"));
            } 
        } else if ("openPopup".equals(cmd)) {
            String uid = args.get("uid");
            String row = args.get("row");
            String colUid = args.get("cuid");

            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTreeTable2) {
            	OrWebTreeTable2 table = (OrWebTreeTable2)comp;
                return table.openPopup(row, colUid, frm.getObj().id);
            } else if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
                return table.openPopup(Integer.parseInt(row), colUid, frm.getObj().id);
            } else if (comp instanceof OrWebHyperPopup) {
                return ((OrWebHyperPopup)comp).openPopup(frm.getObj().id);
            } else {
                return "{\"result\":\"error\"}";
            }
        } else if ("loadPopup".equals(cmd)) {

            WebFrame frm = frameManager.getCurrentFrame();
            ((WebPanel)frm.getPanel()).putJSON();
            
            KrnObject uiObj = frm.getKernel().getCachedObjectById(frm.getObj().id);
            
            return TemplateHelper.load(webUser.getGUID(), uiObj.uid, interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
        } else if ("closePopup".equals(cmd)) {
            String uid = args.get("uid");
            String val = args.get("val");
            String row = args.get("row");
            String colUid = args.get("cuid");

            WebFrame frm = frameManager.getPreviousFrame();
            
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTreeTable2) {
            	OrWebTreeTable2 table = (OrWebTreeTable2)comp;
            	JsonObject res = table.setValue(val, row, colUid);
                if (res != null)
                	return res.toString();
            } else if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
                JsonObject res = table.setValue(val, Integer.parseInt(row), colUid);
                if (res != null)
                	return res.toString();
            } else if (comp instanceof OrWebHyperPopup) {
            	JsonObject res = ((OrWebHyperPopup)comp).buttonPressed(val);
                if (res != null)
                	return res.toString();
            }
            return getChanges();
        } else if ("closeDialog".equals(cmd)) {
        	 WebFrame frame = frameManager.getCurrentFrame();
        	 frame.setDialogResult(Integer.valueOf(args.get("val")));
        } else if ("userDecision".equals(cmd)) {
        	int value = Integer.valueOf(args.get("value"));
            WebFrame frame = frameManager.getCurrentFrame();
            frame.setUserDecision(value);
        } else if ("openTree".equals(cmd)) {
            String uid = args.get("uid");
            String row = args.get("row");
            String colUid = args.get("cuid");

            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);

        	if (comp instanceof OrWebTreeTable2)
        		return ((OrWebTreeTable2)comp).treeFieldPressed(row, colUid);
        	else if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
                if (row != null && colUid != null) {
                    return table.treeFieldPressed(Integer.parseInt(row),
                                        colUid);
                }
            } else {
                return ((OrWebTreeField)comp).treeFieldPressed(uid);
            }
        } else if ("viewFile".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
                String row = args.get("row");
                String col = args.get("col");
                if (row != null && col != null) {
                	JsonObject r = table.buttonPressed(Integer.parseInt(row),
                                        Integer.parseInt(col));
                    if (r != null)
                    	return r.toString();
                }
            } else if (comp instanceof OrWebDocField) {
            	int index = args.get("row") != null ? Integer.parseInt(args.get("row")) : -1;
                return ((OrWebDocField)comp).openFile(index).toString();
            } else if (comp instanceof OrWebDocFieldColumn) {
                String row = args.get("row");
                return ((OrWebDocField)((OrWebDocFieldColumn)comp).getEditor(Integer.parseInt(row))).buttonPressed().toString();
            }
            return "{\"result\":\"error\"}";
        } else if ("action".equals(cmd)) {
            WebActionMaker wa = actions.get(args.get("id"));
            if (wa != null) {
                wa.makeAction();
            }

        } else if ("set".equals(cmd)) {
            String uid = args.get("uid");
            String val = args.get("val");
            
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);

            // not synchronized actions
            String command = args.get("com");
            
        	if ("getEditor".equals(command)) {
                OrWebTable table = (OrWebTable) comp;
                frm.setSelectedComponent(table);
                String row = args.get("row");
                String colUid = args.get("cuid");
        		if (comp instanceof OrWebTreeTable2)
        			return ((OrWebTreeTable2) table).getCellEditor(row, colUid);
        		else
        			return table.getCellEditor(Integer.parseInt(row), colUid);
        	} else if ("sort".equals(command)) {
                OrWebTable table = (OrWebTable) comp;
                String colUid = args.get("cuid");
        		table.sortColumn(Integer.parseInt(colUid));
        	} else if ("rsort".equals(command)) {
                OrWebTable table = (OrWebTable) comp;
                String colUid = args.get("cuid");
        		table.removeSortColumn(Integer.parseInt(colUid));
            }
            
            synchronized (setters) {
	            if (comp instanceof OrWebTable) {
	                frm.setSelectedComponent((OrGuiComponent) comp);
	                OrWebTable table = (OrWebTable) comp;
	                String row = args.get("row");
	                String colUid = args.get("cuid");
	                String scl = args.get("scl");
	                if ("add".equals(command)) {
	                    table.addRow();
	                } else if ("del".equals(command)) {
	                    String idx = args.get("idx");
	                	
	                    if (args.get("sure") != null) {
	                        table.deleteRow(true, idx);
	                    } else {
	                        return table.deleteRow(false, idx).toString();
	                    }
	                } else if ("showDel".equals(command)) {
	                    table.showDeleted();
	                } else if ("copyRowsNavi".equals(command)) {
	                    table.copyRows();
	                } else if ("fastRepNavi".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("consalNavi".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("findNavi".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("filterNavi".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("goRight".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("moveUp".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("moveDown".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("firstPage".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("lastPage".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("nextPage".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("backPage".equals(command)) {
	                    log.warn("не определено!");
	                } else if ("sct".equals(command)) {
	                    if (scl != null && comp instanceof OrWebTreeTable2) {
	                        ((OrWebTreeTable2) comp).setSingleClick(true);
	                        table.setValue(row);
	                    } else {
	                        if (row != null) {
	                            if (comp instanceof OrWebTreeTable2) {
	                                ((OrWebTreeTable2) comp).setSingleClick(false);
	                            }
	                            table.setValue(row);
	                        }
	                    }
	                    if (colUid != null && colUid.length() > 0) {
	                        if (comp instanceof OrWebTreeTable2) {
	                            ((OrWebTreeTable2) table).selectColumn(colUid);
	                        } else if (comp instanceof OrWebTreeTable) {
	                            ((OrWebTreeTable) table).selectColumn(colUid);
	                        } else {
	                            table.setSelectedColumn(Integer.parseInt(colUid));
	                        }
	                    }
	                        
	                } else if (row != null && colUid != null) {
	                    if (comp instanceof OrWebTreeTable2)
	                        ((OrWebTreeTable2) table).setValue(val, row, colUid);
	                    else if (comp instanceof OrWebTreeTable)
	                        ((OrWebTreeTable) table).setValue(val, row, colUid);
	                    else
	                        table.setValue(val, Integer.parseInt(row), colUid);
	                } else if (colUid != null) {
	                    try {
	                        long nodeId = Long.parseLong(val);
	                        table.selectNode(nodeId, Integer.parseInt(colUid));
	                    } catch (Exception e) {
	                        log.error("|USER: " + getUserName() + "| NOT POSSIBLE SITUATION 1");
	                    }
	                } else if (command == null) {
	                    if (comp instanceof OrWebTreeTable2)
	                        ((OrWebTreeTable2) table).selectNode(val); // TODO
	                    else if (comp instanceof OrWebTreeTable)
	                        ((OrWebTreeTable) table).selectNode(val); // TODO
	                    else
	                        table.setValue(val);
	                }
	            } else if (comp instanceof OrWebTreeCtrl) {
	                OrWebTreeCtrl tree = (OrWebTreeCtrl)comp;
	
	                tree.selectNodes(val);
	            		
	            } else if (comp instanceof OrWebTreeControl2) {
	            	OrWebTreeControl2 tree = (OrWebTreeControl2)comp;
	            	//if(!val.isEmpty()) {
	                    tree.selectNodes(val);
	                //}
	            } else if (comp instanceof OrWebTreeField) {
	                OrWebTreeField tree = (OrWebTreeField)comp;
	                try {
	                    long nodeId = Long.parseLong(val);
	                    tree.setSelectedNode(nodeId);
	                } catch (Exception e) {
	                    log.error(e, e);
	                }
	            } else if (comp instanceof OrWebImage) {
	                //OrWebImage img = (OrWebImage) comp;
	            } else if (comp instanceof OrWebRadioBox) {
	            	if (comp.isEnabled())
	            		comp.setValue(val);
	            } else if (comp instanceof OrWebDocField) {
	            	if (comp.isEnabled()) {
		            	OrWebDocField c = (OrWebDocField)comp;
		                String index = args.get("ind");
		                if ("del".equals(command)) {
		                    c.deleteValue(Integer.parseInt(index));
		                } else {
		                    comp.setValue(val);
		                }
	            	}
	            } else if (comp instanceof OrWebComboBox) {
	            	if (comp.isEnabled()) {
		            	OrWebComboBox c = (OrWebComboBox)comp;
		                if ("add".equals(command)) {
		                    c.addValue(val);
		                } else if ("del".equals(command)) {
		                    c.deleteValue(val);
		                } else
		                    comp.setValue(val);
	            	}
	            } else if (comp instanceof OrWebImagePanel) {
	            	((OrWebImagePanel)comp).setValue(val);
	            } else if (comp != null) {
	            	if (comp.isEnabled())
	            		comp.setValue(val);
	            }
            }
            return getChanges();
        } else if ("commit".equals(cmd)) {
            String result = args.get("result");
            WebFrame frm = frameManager.getCurrentFrame();
            if ("save".equals(result)) {
                return frm.commit(true).toString();
            } else {
                return frm.canCommit().toString();
            }
        } else if ("closeIfc".equals(cmd)) {
            WebFrame webFrame = frameManager.getCurrentFrame();
            WebFrameManager manager = (WebFrameManager) webFrame.getInterfaceManager();
            CommitResult cr = manager.beforePrevious();
            if (cr == CommitResult.SESSION_REALESED) return "{}";
            manager.afterPrevious(true, true, true, cr);
        } else if ("fcommit".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            if (frm != null)
            	return frm.commit(false).toString();
        } else if ("getErrors".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            return frm.toHTML(frm.getMessageList());
        } else if ("cancelProcess".equals(cmd)) {
            frameManager.releaseInterface(false);
            String id = args.get("uid");
            Activity act = null;
            if (id != null) {
                long flowId = Long.parseLong(id);
                act = taskHelper.getActivityById(flowId);
                
                if (act != null) {
		            try {
	    				getKernel().cancelProcess(act.flowId, act.msg, false, true);
		    			getTaskHelper().closeAutoAct();
		    		} catch (KrnException ex) {
		    			log.error(ex, ex);
		    		}
                }
            }
            return "";
        } else if ("startInterview".equals(cmd)) {
        	// Вывод опроса
			JsonObject res = new JsonObject();
        	if (WebController.ACTIVATE_INTERVIEW) {
                try {
                	// Поиск активных опросников
                	List<KrnObject> activeOprosnikObjs = new ArrayList<>();
                    KrnClass oprosnikCls = wkrn.getClassByName("Опросник");
                    KrnObject[] oprosnikObjs = wkrn.getClassObjects(oprosnikCls, 0);
                    if (oprosnikObjs.length > 0) {
                    	KrnAttribute startDateAttr = wkrn.getAttributeByName(oprosnikCls, "дата_начала");
                    	KrnAttribute finishDateAttr = wkrn.getAttributeByName(oprosnikCls, "дата_конца");
                    	Date currentDate = new Date();
                    	for (KrnObject oprosnikObj: oprosnikObjs) {
	                        DateValue[] startDateValues = wkrn.getDateValues2(new long[] {oprosnikObj.id}, startDateAttr, 0);
	                        Date startDate = null;
	                        if (startDateValues.length > 0 && startDateValues[0].value != null) {
	                        	startDate = new Date(startDateValues[0].value.year - 1900, startDateValues[0].value.month, startDateValues[0].value.day);
	                        }
	                        DateValue[] finishDateValues = wkrn.getDateValues2(new long[] {oprosnikObj.id}, finishDateAttr, 0);
	                        Date finishDate = null;
	                        if (finishDateValues.length > 0 && finishDateValues[0].value != null) {
	                        	finishDate = new Date(finishDateValues[0].value.year - 1900, finishDateValues[0].value.month, finishDateValues[0].value.day);
	                        }
	                        if (startDate != null && finishDate != null && currentDate.after(startDate) && currentDate.before(finishDate)) {
	                        	activeOprosnikObjs.add(oprosnikObj);
	                        }
                        }
                    }
                    if (activeOprosnikObjs.size() > 0) {
                    	// Проверка на прохождение опросника. Предполагается, что активных опросников не может быть больше 1
                        KrnClass userCls = wkrn.getClassByName("User");
                        KrnAttribute zapTablOprosaAttr = wkrn.getAttributeByName(userCls, "зап_прецендента_опроса");
                        KrnClass zapTablOprosaCls = wkrn.getClassByName("Зап_прецендента_опроса_персоны");
                        KrnAttribute oprosnikAttr = wkrn.getAttributeByName(zapTablOprosaCls, "опросник");
                    	KrnObject[] zapTablOprosaObjs = wkrn.getObjects(wkrn.getUser().object, zapTablOprosaAttr, 0);
                    	boolean isDone = false;
                    	for (KrnObject zapTablOprosaObj: zapTablOprosaObjs) {
                    		KrnObject oprosnikObj = wkrn.getObjectsSingular(zapTablOprosaObj.id, oprosnikAttr.id, false);
                    		if (oprosnikObj != null && oprosnikObj.equals(activeOprosnikObjs.get(0))) {
                    			isDone = true;
                    			break;
                    		}
                    	}
                    	if (!isDone) {
	                        KrnAttribute processAttr = wkrn.getAttributeByName(oprosnikCls, "процесс");
                    		KrnObject processObj = wkrn.getObjectsSingular(activeOprosnikObjs.get(0).id, processAttr.id, false);
                    		if (processObj != null) {
                    			JsonObject oprosnik = new JsonObject();
                    			oprosnik.add("processUID", processObj.uid);
                    			res.add("oprosnik", oprosnik);
                    		}
                    	}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        	return res.toString();
        } else if ("nextStep".equals(cmd)) {
        	String result = args.get("result");
        	String transitionId = args.get("transitionId");
        	String flowId = args.get("flowId");
        	
        	if (flowId != null && flowId.length() > 0) {
        		return nextStep(Long.parseLong(flowId));
        	} else {
	            WebFrame frm = frameManager.getCurrentFrame();
	        	if ("save".equals(result)) {
	                frm.commit(true).toString();
	                return frm.nextStep(transitionId);
	        	} else {
	        		JsonObject obj = new JsonObject();
	                Activity act = taskHelper.getActivityById(frm.getFlowId());
	                boolean dlg = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType);
	                if (dlg) {
	                    int res = BUTTON_CANCEL;
	                    OrRef ref = frm.getRef();
	                    ReqMsgsList msg = ref.canCommit();
	                    int errors = msg.getListSize();
	                    if (errors > 0) {
	                        res = frm.getCommitAction(msg, false, null);
	                    }
		                if (res != BUTTON_OK && !msg.hasFatalErrors()) {
		                    OrPanelComponent p = frm.getPanel();
		                    ASTStart template = p.getBeforeCloseTemplate();
		                    if (template != null) {
		                        ClientOrLang orlang = new ClientOrLang(frm);
		                        Map<String, Object> vc = new HashMap<String, Object>();
		                        boolean calcOwner = OrCalcRef.setCalculations();
		                        try {
		                            orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
		                        } catch (Exception ex) {
		                            Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
		                        	log.error("Ошибка при выполнении формулы 'Действие перед закрытием' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
		                            log.error(ex, ex);
		                        } finally {
		                            if (calcOwner)
		                            	OrCalcRef.makeCalculations();
		                        }
		                    }
	
		                    frm.getCash().commit(act.flowId);
		                    frm.getRef().commitChanges(null);
		                    boolean b = frm.doAfterCommit(frm);
	/*	                    if (ref.getItems(ref.getLangId()) != null
		                            && ref.getItems(ref.getLangId()).size() != 0
		                            && ref.getSelectedItems().size() == 0) {
		                    	ref.setSelectedItems(new int[] { 0 });
		                    }
	*/	                    List<OrRef.Item> a_sel = frm.getRef().getSelectedItems();
		                    if (a_sel.size() == 0) {
		                        String text = resource.getString("checkObjectMessage");
		                        obj.add("message", text.replaceFirst("^\\!", ""));
		                        obj.add("result", "error");
		                        return obj.toString();
		                    }
		                    KrnObject[] selObjs = new KrnObject[a_sel.size()];
		                    for (int i = 0; i < a_sel.size(); i++) {
		                        OrRef.Item item = a_sel.get(i);
		                        selObjs[i] = (KrnObject) item.getCurrent();
		                    }
		                    int result_ = -1;
		                    String res_ = "";
		                    if (act.transitions.length > 1) {
		                    	//@todo 
		                        String[] trs = new String[act.transitions.length];
		                        for (int i = 0; i < trs.length; ++i) {
		                            trs[i] = act.transitions[i].substring(0, act.transitions[i].indexOf(";"));
		                        }
		                        result_ = frm.getOption(trs);
		                        if (result_ > -1) {
	                                res_ = act.transitions[result_].substring(act.transitions[result_].lastIndexOf(";") + 1);
		                        }
		                    }
		                    if (wkrn.setSelectedObjects(act.flowId,
		                            act.nodesId[0][act.nodesId[0].length - 1], selObjs)) {
		                        wkrn.setPermitPerform(act.flowId, true);
		                        
			                	long oldId = act.ui.id;
			                	long oldInfId = act.ui.id;
			                	int oldPermit = (int) act.param & ACT_PERMIT;
			                    try {
			                    	taskHelper.disableActivity(act);
	
			                    	//
				                    ASTStart templateCl = p.getAfterCloseTemplate();
				                    if (templateCl != null) {
				                        ClientOrLang orlang = new ClientOrLang(frm);
				                        Map<String, Object> vc = new HashMap<String, Object>();
				                        boolean calcOwner = OrCalcRef.setCalculations();
				                        try {
				                            orlang.evaluate(templateCl, vc, frm.getPanelAdapter(), new Stack<String>());
				                        } catch (Exception ex) {
				                            Util.showErrorMessage(p, ex.getMessage(), "Действие после закрытия");
				                        	log.error("Ошибка при выполнении формулы 'Действие после закрытия' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
				                            log.error(ex, ex);
				                        } finally {
				                            if (calcOwner)
				                            	OrCalcRef.makeCalculations();
				                        }
				                    }
				                    
			                        String[] res_s = wkrn.performActivitys(new Activity[] { act }, res_);
			                        if (res_s.length > 0) {
			                            if (res_s.length == 1 && res_s[0].equals("synch")) {
			                                taskHelper.setAutoIfcFlowId_(act.flowId);
			                            } else {
					                        taskHelper.reenableActivity(act, oldId, oldInfId, oldPermit);
			                                // обработка ошибок
			                                String msg_ = res_s[0];
			                                for (int i = 1; i < res_s.length; ++i)
			                                    msg_ += "\n" + res_s[i];
		
			    	                        obj.add("message", msg_.replaceFirst("^\\!", ""));
			    	                        obj.add("result", "error");
			    	                        return obj.toString();
			                            }
			                        }
	
			                        if (!frm.isSharedCache() && (frm.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
				                        frm.clear();
				                    } else {
				                        if (frm.isSharedCache()) {
			                        		frm.getCash().undoCacheChange(frm.getInterfaceId(), this);
				                        	frm.setCache(null);
				                        } else
				                            frm.clear();
				                    }
				                    frm = frameManager.prev();
	
				                    taskHelper.reenableActivity(act.flowId);
		                            taskHelper.setAutoIfcFlowId_(act.flowId);
		
			                        if (res_s.length == 1 && res_s[0].equals("synch")) {
			                        	taskHelper.reloadTask(act.flowId, act.ui.id > 0 && act.infUi.id > 0 ? 2
			                                    : act.infUi.id > 0 ? 1 : 0, true, true);
			                        }
			                    } catch (KrnException e) {
			                        taskHelper.reenableActivity(act, oldId, oldInfId, oldPermit);
			                        log.error(e, e);
			                    }
		                    }
		                    
		                } else {
		                	obj.add("result", "error");
		                	return obj.toString();
		                }
	                	obj.add("result", "success");
	                	return obj.toString();
	                } else {
	                	if (transitionId == null) {
		                	if (act.transitions.length > 1) {
		    	            	obj.add("result", "selectStep");
		    	            	JsonArray transitions = new JsonArray();
		    	                for (int i = 0; i < act.transitions.length; i++) {
		    	                	String[] elements = act.transitions[i].split(",");
		    	                	JsonObject transition = new JsonObject();
		    	                	transition.add("title", elements[0]);
		    	                	transition.add("value", elements[elements.length - 1]);
		    	                	transitions.add(transition);
		    	                }
		    	            	obj.add("transitions", transitions);
		    	                return obj.toString();
		    	            }
	                	}
	            		CommitResult cr = ifcManager.previous(true, false);
	            		if (cr == CommitResult.WITH_ERRORS || cr == CommitResult.WITHOUT_ERRORS) {
	                        return frm.nextStep(transitionId);
	                    } else {
	                    	return "{}";
	                    }
	                }
	        	}
        	}
        } else if ("rollback".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.rollback();
            return getChanges();
        } else if ("print".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            String id = args.get("id");
            return frm.generateReportJSON(id).toString();
        } else if (args.get("getChange") != null) {
            return getChanges();
        } else if ("getStack".equals(cmd)) {
            return frameManager.getStackFrames().toString();
        } else if ("clearToFrame".equals(cmd)) {
            int index = Integer.valueOf(args.get("index"));
            frameManager.getFrame(index);
            return "{}";
        } else if ("goToFrame".equals(cmd)) {
            int index = Integer.valueOf(args.get("index"));
            WebFrame frm = frameManager.getFrame(index);
            ifcManager.absolute(frm, frm.getObj(), frm.getInitialObjs(), null, frm.getEvaluationMode(), false, frm.getTransactionId(), frm.isSharedCache(), frm.getFlowId(), true, "");
            if (frm != null && frm.getPanel() != null) {
                if (frm.getPanel() != null) {
                    ((WebPanel) frm.getPanel()).putJSON();
                }
                return TemplateHelper.load(webUser.getGUID(), frm.getInterfaceUid(), interfaceLangId, wkrn, WebController.APP_HOME + "/orui", false);
            }
        } else if("selTab".equals(cmd)) {
            String uid = args.get("uid");
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTabbedPane) {
                ((OrWebTabbedPane)comp).selectedIndex(Integer.parseInt(args.get("indx")));
            } 
        } else if (args.get("getReports") != null) {
        	// Вызывается после открытия интерфейса
        	noMainUiCommand = false;
            WebFrame frm = frameManager.getCurrentFrame();
            return frm == null ? "" : frm.getReportsJSON().toString();
        } else if (args.get("tableData") != null) {
        	String uid = args.get("tableData");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTreeTable2) {
            	String sortCol = args.get("sort") != null ? args.get("sort") : null;
            	String sortOrder = args.get("order") != null ? args.get("order") : null;
            	return ((OrWebTreeTable2)comp).getData(args.get("id"), sortCol, sortOrder);
            } else if (comp instanceof OrWebTreeTable) {
            	String sortCol = args.get("sort") != null ? args.get("sort") : null;
            	String sortOrder = args.get("order") != null ? args.get("order") : null;
            	return ((OrWebTreeTable)comp).getData(args.get("id"), sortCol, sortOrder);
            } else if (comp instanceof OrWebTable) {
                OrWebTable table = (OrWebTable)comp;
            	int page = args.get("page") != null ? Integer.parseInt(args.get("page")) : 1;
            	int rows = args.get("rows") != null ? Integer.parseInt(args.get("rows")) : 0;

            	String sortCols = args.get("sort") != null ? args.get("sort") : null;
            	String orders = args.get("order") != null ? args.get("order") : null;
//            	ifcManager.doNextPage(frm, page, rows);
            	String data = table.getData(page, rows, sortCols, orders);
            	sendCommand("refresh", "");
            	return data;
            }
            return "{\"result\":\"error\"}";
        } else if (args.get("comboData") != null) {
        	String uid = args.get("comboData");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebComboColumn) {
            	return ((OrWebComboColumn)comp).getData().toString();
            } else if (comp instanceof OrWebComboBox) {
            	OrWebComboBox cb = (OrWebComboBox)comp;
                return cb.getData();
            }
            return "{\"result\":\"error\"}";
        } else if (args.get("imgPnlData") != null) {
        	String uid = args.get("imgPnlData");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebImagePanel) {
            	return ((OrWebImagePanel)comp).getData(args.get("rows")).toString();
            }
            return "{\"result\":\"error\"}";
        } else if (args.get("treeFind") != null) {
        	String uid = args.get("treeFind");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTreeCtrl) {
            	OrWebTreeCtrl cb = (OrWebTreeCtrl)comp;
//                return cb.findTitle(args.get("id"), args.get("title"), args.get("index"));
            	return cb.findTitle(args.get("title"));
            } else if (comp instanceof OrWebTreeControl2) {
                OrWebTreeControl2 cb = (OrWebTreeControl2)comp;
                return cb.findTitle(args.get("title"));
            } else if (comp instanceof OrWebTreeTable2) {
            	OrWebTreeTable2 cb = (OrWebTreeTable2)comp;
                return cb.findTitle(args.get("id"), args.get("title"), args.get("index"));
            } else if (comp instanceof OrWebTreeField) {
            	OrWebTreeField cb = (OrWebTreeField) comp;
            	return cb.findTitle(args.get("title"));
            }
            return "{\"result\":\"error\"}";

        } else if (args.get("treeData") != null) {
        	String uid = args.get("treeData");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebTreeColumn) {
            	return ((OrWebTreeColumn)comp).getData(args.get("id"));
            } else if (comp instanceof OrWebTreeField) {
            	OrWebTreeField cb = (OrWebTreeField)comp;
                return cb.getData(args.get("id"));
            } else if (comp instanceof OrWebTreeCtrl) {
            	OrWebTreeCtrl cb = (OrWebTreeCtrl)comp;
                return cb.getData(args.get("id"));
            }else if (comp instanceof OrWebTreeControl2) {
                OrWebTreeControl2 cb = (OrWebTreeControl2)comp;
                return cb.getData(args.get("id"));
            }
            return "{\"result\":\"error\"}";
        } else if (args.get("uploadedData") != null) {
        	String uid = args.get("uploadedData");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            if (comp instanceof OrWebDocField) {
            	OrWebDocField df = (OrWebDocField)comp;
                return df.getUploadedData();
            }
            return "";
        } else if (args.get("collapse") != null) {
        	String nodeId = args.get("collapse");
        	String uid = args.get("uid");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            
            if (comp instanceof OrWebTreeTable2) {
            	((OrWebTreeTable2)comp).collapse(nodeId);
            } else if (comp instanceof OrWebTreeTable) {
            	((OrWebTreeTable)comp).collapse(nodeId);
            } else if (comp instanceof OrWebTreeCtrl) {
            	OrWebTreeCtrl cb = (OrWebTreeCtrl)comp;
                TreePath path = cb.getPathByNodeId(nodeId);
                cb.collapsePath(path);
            } else if (comp instanceof OrWebTreeControl2) {
            	OrWebTreeControl2 cb = (OrWebTreeControl2)comp;
                TreePath path = cb.getPathByNodeId(nodeId);
                cb.collapsePath(path);
            }
            return "";
        } else if (args.get("expand") != null) {
        	String nodeId = args.get("expand");
        	String uid = args.get("uid");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            
            if (comp instanceof OrWebTreeTable2) {
            	((OrWebTreeTable2)comp).expand(nodeId);
            } else if (comp instanceof OrWebTreeTable) {
            	((OrWebTreeTable)comp).expand(nodeId);
            } else if (comp instanceof OrWebTreeCtrl) {
            	OrWebTreeCtrl cb = (OrWebTreeCtrl)comp;
                TreePath path = cb.getPathByNodeId(nodeId);
                cb.expandPath(path);
            } else if (comp instanceof OrWebTreeControl2) {
            	OrWebTreeControl2 cb = (OrWebTreeControl2)comp;
                TreePath path = cb.getPathByNodeId(nodeId);
                cb.expandPath(path);
            }
            return "";
        } else if (args.get("moveUp") != null) {
        	String nodeId = args.get("moveUp");
        	String uid = args.get("uid");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            
            if (comp instanceof OrWebTree) {
            	((OrWebTree)comp).moveUp(nodeId);
            }else if (comp instanceof OrWebTreeControl2) {
            	((OrWebTreeControl2)comp).moveUp(nodeId);
            }
            return "";
        } else if (args.get("moveDown") != null) {
        	String nodeId = args.get("moveDown");
        	String uid = args.get("uid");
        	
            WebFrame frm = frameManager.getCurrentFrame();
            WebComponent comp = frm.getComponentByUID(uid);
            
            if (comp instanceof OrWebTree) {
            	((OrWebTree)comp).moveDown(nodeId);
            }else if (comp instanceof OrWebTreeControl2) {
            	((OrWebTreeControl2)comp).moveDown(nodeId);
            }
            return "";
        } else if (args.get("getTitle") != null) {
            WebFrame frm = frameManager.getCurrentFrame();
            return new JsonObject().add("title", frm.getPanel().getTitle()).toString();
        } else if (args.get("func") != null) {
        	return executeMethod(args, false).toString();
        } else if (args.get("sfunc") != null) {
        	String extension = args.get("ext");
        	if (extension != null) {
            	String fileName = args.get("fn");
            	byte[] res = (byte[])executeMethod(args, true);
            	if (res != null)
            		sendResult(extension, fileName, res, response);
        		return null;
        	}
        	Object r = executeMethod(args, true);
        	return r != null ? r.toString() : "{}";
        } else if (args.get("getAttr") != null) {
        	return getAttr(args);
        } else if ("changePass".equals(cmd)) {
        	return ViewHelper.changePasswordJSON(args, this, resource).toString();
        }

        String trg = args.get("trg");
        
    	int responseType = (args.get("json") != null) ? WebController.RESPONSE_JSON
						: (args.get("xml") != null) ? WebController.RESPONSE_XML
													: WebController.RESPONSE_HTML;

        if (!isForPublicUser && "fwb".equals(trg) && "opn".equals(cmd))
            trg = "frm";

        String lng = args.get("lng");
        if (lng != null) {
            long lid = Long.parseLong(lng);
            setLangId(lid);
        }
        lng = args.get("dlng");
        if (lng != null) {
            long lid = Long.parseLong(lng);
            setDataLangId(lid);
        }

        if ("logo".equals(trg)) {
            return ViewHelper.getLogoHTML(configNumber);
        } else if ("sts".equals(trg)) {
            return ViewHelper.getStatusHTMLOld(interfaceLangId, dataLangId, resource, this);
        } else if ("menu".equals(trg)) {
            boolean mainFrame = false;
            KrnObject iObj = wkrn.getInterface();
            WebFrame f  = frameManager.getCurrentFrame();

            if (f == null || f.getObj().id == 0 || (iObj != null && f.getObj().id == iObj.id))
                mainFrame = true;
            return ViewHelper.getMenuHTMLOld/*jq*/(this, resource, mainFrame, isForPublicUser, getKernel().getUser().isAdmin(), (getCert() != null && "T32".equals(getCertType())), request);
        } else if ("bts".equals(trg)) {
            return ViewHelper.getButtonsHTML/*jq*/(commonHelper.getSelectedButton(), interfaceLangId, backPage, onclick, this);
        } else if ("chp".equals(trg)) {
            return ViewHelper.changePasswordDialog(args, this, resource);
        } else if ("shu".equals(trg)) {
            return ViewHelper.getShowUsers(this, resource);
        } else if ("dlu".equals(trg)) {
            String sid = args.get("sid");
            try {
                WebSessionManager.releaseSession(Integer.valueOf(sid));
            } catch (Exception e) {log.error(e, e);}
            return "";
        } else if ("srv".equals(trg)) {
            if (cmd == null || "lng".equals(cmd) || "dlng".equals(cmd)) {
                commonHelper.setSelectedButton(CommonHelper.TASKS_BUTTON);
                return ViewHelper.getTaskAndProcessesHTML(args, this);
            } else if ("tsk".equals(cmd)) {
                if (taskHelper != null) return ViewHelper.getTaskTableHTML(taskHelper.getTable(),configNumber);
            //} else if ("xtsk".equals(cmd)) {
            	//if (taskHelper != null) return ViewHelper.getTaskTableXMLjq(args, taskHelper.getTable());
            } else if ("prs".equals(cmd)) {
            	if (processHelper != null) {
	                SortedSet<ProcessNode> tabs = processHelper.getTabs();
	                Long selectedTab = processHelper.getSelectedTab();
	                return ViewHelper.getProcessesHTML(tabs, selectedTab, resource, processHelper, this);
            	}
            } else if ("tab".equals(cmd)) {
            	if (processHelper != null) {
	                SortedSet<ProcessNode> tabs = processHelper.getTabs();
	                String pid = args.get("id");
	                String wait =args.get("wait");
	                long id = Long.parseLong(pid);
	                processHelper.setSelectedTab(id);
	                if (wait == null)
	                    return ViewHelper.getTabXML(id, tabs, resource, processHelper);
            	}
            } else if ("exp".equals(cmd)) {
            	if (processHelper != null)
            		return ViewHelper.getProcessXML(args, processHelper, resource);
            		
            } else if ("crp".equals(cmd)) {
                Object res = processHelper.createProcess(args);
                return ViewHelper.getAfterStartProcessXML(res, taskHelper.getTable(), isMonitorTask);
            } else if ("klp".equals(cmd)) {
                taskHelper.killProcess(args);
            } else if ("nst".equals(cmd)) {
                return taskHelper.nextStep(args);
            } else if ("gop".equals(cmd)) {
                return taskHelper.getOptionPane(args, resource);
            } else if ("sct".equals(cmd)) {
                String id = args.get("id");
                if (id != null) {
                    long flowId = Long.parseLong(id);
                    taskHelper.setSelectedActivity(flowId);
                }
                String col = args.get("col");
                if (col != null) {
                    int numCol = Integer.parseInt(col);
                    taskHelper.setSelectedColumn(numCol);
                }
            } else if ("stp".equals(cmd)) {
                return ViewHelper.getStartProcessXML(args);
            } else if ("rtsk".equals(cmd)) {
            	if (taskHelper != null) {
            	    return ViewHelper.getRefreshTaskTableXML(taskHelper.getTable(),this);
            	}
            } else if ("hlp".equals(cmd)) {
                String id = args.get("id");
                return getHelpHTML(Integer.parseInt(id));
            }
        } else if ("frm".equals(trg)) {
            if ("changePassword".equals(cmd)) {
            	return ViewHelper.changePassword(args, this, resource);
            } else if ("tableData".equals(cmd)) {
                String id = args.get("id");
                WebFrame frm = frameManager.getCurrentFrame();
                WebComponent comp = frm.getComponent(id);
                if (comp instanceof OrWebTable) {
                    try {
                        return ((OrWebTable) comp).getData(args);
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            } else if ("sta".equals(cmd)) {
                WebActionMaker wa = actions.get(args.get("id"));
                if (wa != null) {
                    wa.makeAction();
                }
            } else if ("cfr".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                frm.setConfirm(Integer.parseInt(args.get("res")));
                return "<r></r>";
            } else if ("sop".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                frm.setOption(Integer.parseInt(args.get("opt")));
                return "";
            } else if ("checkToSign".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                return frm.getSignXML();
            } else if ("signres".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                frm.setSignValue(args.get("sign"));
                if (args.get("cert") != null && args.get("cert").length() > 0) setCert(args.get("cert"));
                if (args.get("path") != null && args.get("path").length() > 0) setProfile(args.get("path"));
                if (args.get("code") != null && args.get("code").length() > 0) setProfilePassword(args.get("code"));
                if (args.get("cont") != null && args.get("cont").length() > 0) setProfileConteiner(args.get("cont"));
            } else if ("operationres".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                frm.setOperationResult(args.get("res"));
            } else if ("opndlg".equals(cmd)) {
                String id = args.get("id");
                boolean auto = false;
                Activity act = null;
                StringBuilder sb = new StringBuilder("<r>");
                sb.append("<f>").append(id).append("</f>");
                if (id != null) {
                    long flowId = Long.parseLong(id);
                    act = taskHelper.getActivityById(flowId);
                    auto = ACT_AUTO_STRING.equals(act.uiType);
                    if (auto) {
                        sb.append("<a>1</a>");
                    }
                    Dimension size = ((OrWebPanel)getFrameManager().absolute2(act.ui, null).getPanel()).getPrefSize();
                    if (size != null) {
                        sb.append("<w>").append(size.getWidth()).append("</w>");
                        sb.append("<h>").append(size.getHeight()).append("</h>");
                    }
                }
                sb.append("</r>");
                return sb.toString();
            } else if ("wcl".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                if (frm != null) {
                    String val = args.get("val");
                    if ("AUTOCLEAR".equals(val))
                        frm.cancelAutoActivity();
                    frameManager.releaseInterface(false);
                }
            }
            else if ("pops".equals(cmd)) {
                String id = args.get("id");
                WebFrame frm = frameManager.getCurrentFrame();
                WebComponent comp = frm.getComponent(id);
                if (comp instanceof OrWebTable) {
                    OrWebTable table = (OrWebTable)comp;
                    String row = args.get("row");
                    String col = args.get("col");
                    if (row != null && col != null) {
                        return table.openPopup(Integer.parseInt(row),
                                            Integer.parseInt(col), frm.getObj().id);
                    }
                } else if (comp instanceof OrWebHyperPopup) {
                    return ((OrWebHyperPopup)comp).actionPerformed(frm.getObj().id);
                }
            }
            else if ("maps".equals(cmd)) {
                String id = args.get("id");
                int ind = Integer.parseInt(args.get("aid"));
                WebFrame frm = frameManager.getCurrentFrame();
                WebComponent comp = frm.getComponent(id);
                return ((OrWebMap)comp).actionPerformed(frm.getObj().id, ind);
            }
            else if ("addnt".equals(cmd)) {
                String id = args.get("id");
                return ViewHelper.getNeedTitleHTML(id, resource,configNumber);
            }
            else if ("cfm".equals(cmd)) {
                String val = args.get("val");

                WebFrame frm = frameManager.getCurrentFrame();
                frm.setConfirm(Integer.parseInt(val));
            }
            else if ("com".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                if (frm != null) {
                	try {
                		CommitResult res = frm.commitCurrent(new String[]{getResource().getString("continue"),
                    		getResource().getString("ignore")});
                		
                		log.info("CommitResult = " + res);
                	} catch (Exception e) {
                		log.error(e, e);
                	}
                	
                    return "<r><stopWait /></r>";
                }
            }
            else if ("previous".equals(cmd)) {
            	try {
            		CommitResult res = frameManager.beforePrevious();
            		frameManager.afterPrevious(true, true, true, res);
            		log.info("CommitResult = " + res);
            	} catch (Exception e) {
            		log.error(e, e);
            	}
            	
                return "<r><stopWait /></r>";
            }
            else if ("run".equals(cmd)) {
                try {
                    CommitResult cr = frameManager.beforePrevious();
                    int res = -1;
                    if (cr == CommitResult.WITH_ERRORS || cr == CommitResult.WITHOUT_ERRORS) {
                        res = taskHelper.next(frameManager.getCurrentFrame());
                    }
                    if (res == BUTTON_YES) {
                        frameManager.afterPrevious(true, true, true, cr);
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }

                return "<r></r>";
            }
            else if ("commitOptionSelected".equals(cmd)) {
            	WebFrame frm = frameManager.getCurrentFrame();
            	frm.setCommitAction(Integer.parseInt(args.get("opt")));
            	return "<r></r>";
            }
            else if ("prevcom".equals(cmd)) {
                try {
                    WebFrame frm = frameManager.getCurrentFrame();
                    if (frm != null) {
                        frm.commit(1);
                    }
            		CommitResult cr = frameManager.beforePrevious();
            		frameManager.afterPrevious(true, true, true, cr);
                } catch (KrnException e) {
                    log.error(e, e);
                }
                boolean b = frameManager.hasPrev() || !needMain;
                return "<r><prev>" + (b ? 1 : 0) + "</prev></r>";
            }
            else if ("prv".equals(cmd)) {
                try {
            		CommitResult cr = frameManager.beforePrevious();
            		frameManager.afterPrevious(true, true, true, cr);
                } catch (KrnException e) {
                    log.error(e, e);
                }
                boolean b = frameManager.hasPrev() || !needMain;
                return "<r><prev>" + (b ? 1 : 0) + "</prev></r>";
            } else if("prvws".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                if(frm != null) {
                    boolean b = frm.beforePrevCanCommitChanges();
                    if(b) {
                    	String xml = ifcManager.getStringToSign();
                        return "<r><sign><toSign>" + Funcs.xmlQuote(xml) +
                        		"</toSign><profile>" + profile + "</profile></sign></r>";
                    } else {
                    	return "<r><hasErrors>true</hasErrors></r>";
                    }
                }
            } else if("prvas".equals(cmd)) {
                WebFrame frm = frameManager.getCurrentFrame();
                if(frm != null) {
                    ifcManager.performBeforeClose((String)args.get("signed"));
                    frm.commit(1);
                    args.put("id", String.valueOf(frm.getFlowId()));
                    try {
                		CommitResult cr = frameManager.beforePrevious();
                		frameManager.afterPrevious(true, true, true, cr);
                    } catch (KrnException e) {
                        log.error(e, e);
                    }
                    taskHelper.nextStep(args);
                    return "<r><ok>1</ok></r>";
                }
            } else if ("grpt".equals(cmd)) {
                String id = args.get("id");
                String fd = args.get("fd");
                String ld = args.get("ld");
                String cd = args.get("cd");

                WebFrame frm = frameManager.getCurrentFrame();
                if (fd != null || ld != null || cd != null) {
                	ThreadLocalDateFormat sdf = ThreadLocalDateFormat.dd_MM_yyyy;
                    Date fDate = null, lDate = null, cDate = null;
                    try {
                        fDate = sdf.parse(fd);
                    } catch (Exception e) {}
                    try {
                        lDate = sdf.parse(ld);
                    } catch (Exception e) {}
                    try {
                        cDate = sdf.parse(cd);
                    } catch (Exception e) {}
                    return frm.generateReport(id, fDate, lDate, cDate);
                } else {
                    return frm.generateReport(id);
                }
            } else if ("mrpt".equals(cmd)) {
                return taskHelper.generateReport(args);
            } else if ("mfrpt".equals(cmd)) {
                return taskHelper.generateFastReport(args);
            } else if ("fdp".equals(cmd)) {
                String id = args.get("id");
                int flags = Integer.parseInt(args.get("fs"));
                return ViewHelper.getDatesPanelHTML(id, flags, resource,configNumber);
            } else if ("shi".equals(cmd)) {
                String id = args.get("id");
                String row = args.get("row");
                String col = args.get("col");

                return ViewHelper.getFotoPanelHTML(id, row, col, this);
            } 
        } else if ("readIdCard".equals(cmd)) {
            WebFrame frm = frameManager.getCurrentFrame();
            frm.setIdCardData(args);
        } else {
        	if (responseType == WebController.RESPONSE_XML) {
	        	StringBuilder out = new StringBuilder(25);
	            out.append("<r>");
	            out.append("<status>");
	            if (getKernel() != null && getKernel().getUser() != null)
	                out.append("1");
	            else
	                out.append("0");
	            out.append("</status>");
	            out.append("</r>");
	
	            return out.toString();
        	} else {
                JsonObject obj = new JsonObject();
	            if (getKernel() != null && getKernel().getUser() != null) {
	                obj.add("result", "success");
	                // Подписание соглашения о неразглашении
                	PolicyNode pnode = kz.tamur.comps.Utils.getPolicyNode(wkrn);
                	if (pnode.getPolicyWrapper().isActivateLiabilitySign()) {
                        try {
                            KrnClass userCls = wkrn.getClassByName("User");
                            KrnAttribute liabilitySignDateAttr = wkrn.getAttributeByName(userCls, "дата подписания обязательства о неразглашении");
                            TimeValue[] timeValues = wkrn.getTimeValues(new long[] {wkrn.getUser().object.id}, liabilitySignDateAttr, 0);
                            com.cifs.or2.kernel.Time liabilitySignDate = null;
                            if (timeValues.length > 0) {
                            	liabilitySignDate = timeValues[0].value;
                            }
                            if (liabilitySignDate == null) {
                                // Первая авторизация
                            	obj.add("la", getLiabilityValues());
                            } else {
                                // Проверка на истечение срока действия подписания соглашения
                                long liabilitySignPeriod = pnode.getPolicyWrapper().getLiabilitySignPeriod();
                                
                                Calendar calendar1 = Calendar.getInstance();
        
                                Calendar calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.DAY_OF_MONTH, liabilitySignDate.day);
                                calendar2.set(Calendar.MONTH, liabilitySignDate.month);
                                calendar2.set(Calendar.YEAR, liabilitySignDate.year);
                                calendar2.set(Calendar.HOUR_OF_DAY, liabilitySignDate.hour);
                                calendar2.set(Calendar.MINUTE, liabilitySignDate.min);
                                calendar2.set(Calendar.SECOND, liabilitySignDate.sec);
                                calendar2.set(Calendar.MILLISECOND, liabilitySignDate.msec);
                                calendar2.add(Calendar.DAY_OF_MONTH, (int) liabilitySignPeriod);
        
                                if (calendar2.before(calendar1)) {
                                	obj.add("la", getLiabilityValues());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                	
                	// Оповещение о сроке действительности ЭЦП
                	if (pnode.getPolicyWrapper().isActivateECPExpiryNotif()) {
	                	if (daysLeft != null) {
							long ecpExpiryNotifPeriod = kz.tamur.comps.Utils.getPolicyNode(wkrn).getPolicyWrapper().getECPExpiryNotifPeriod();
							if (daysLeft < 0) {
								obj.add("dl", "Срок действия ЭЦП истёк!");
							} else if (daysLeft < ecpExpiryNotifPeriod) {
								obj.add("dl", "Действительность ЭЦП истекает через " + daysLeft + " дней!");
							}
	                	}
					}
                	
                	// Оповещение об истечении срока временной регистрации
                	if (pnode.getPolicyWrapper().isActivateTempRegNotif()) {
	                    try {
	                        KrnClass userCls = wkrn.getClassByName("User");
	                        KrnAttribute temRagDateAttr = wkrn.getAttributeByName(userCls, "дата истечения срока временной регистрации");
	                        DateValue[] dateValues = wkrn.getDateValues2(new long[] {wkrn.getUser().object.id}, temRagDateAttr, 0);
	                        
	                        com.cifs.or2.kernel.Date tempRegDate = null;
                            if (dateValues.length > 0) {
                            	tempRegDate = dateValues[0].value;
                            }
	
	                        long tempRegNotifPeriod = kz.tamur.comps.Utils.getPolicyNode(wkrn).getPolicyWrapper().getTempRegNotifPeriod();
	                        
	                        if (tempRegDate != null && tempRegNotifPeriod > 0) {
	                            Calendar calendar1 = Calendar.getInstance();
	                            calendar1.add(Calendar.DATE, (int) tempRegNotifPeriod);
	                            
	                            Calendar calendar2 = Calendar.getInstance();
	                            calendar2.set(Calendar.DAY_OF_MONTH, tempRegDate.day);
	                            calendar2.set(Calendar.MONTH, tempRegDate.month);
	                            calendar2.set(Calendar.YEAR, tempRegDate.year);
	                            
	                            if (calendar2.before(calendar1)) {
	                            	ThreadLocalDateFormat format = ThreadLocalDateFormat.dd_MM_yyyy;
	                                obj.add("tempReg", "Ваша учетная запись будет заблокирована " + format.format(calendar2.getTime()) + " в связи с истечением срока регистрации!");
	                            }
	                        }
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
                	}
	            } else {
	                obj.add("result", "error");
                    obj.add("message", "ERROR");
	            }
                return obj.toString();
        	}
        }
        if (responseType == WebController.RESPONSE_XML)
        	return "<r></r>";
        else
        	return "";
    }
    
	private JsonObject getLiabilityValues() {
    	JsonObject obj = new JsonObject();
        try {
            KrnClass liabilityCls = wkrn.getClassByName("Обязательство о неразглашении сведений");
            KrnObject[] objs = wkrn.getClassObjects(liabilityCls, 0);
            KrnObject lastObj = null;
            for (int i = 0; i < objs.length; i++) {
                if (i == 0) {
                    lastObj = objs[i];
                } else {
                    if (objs[i].id > lastObj.id) {
                        lastObj = objs[i];
                    }
                }
            }
            if (lastObj != null) {
                KrnAttribute liabilityDialogTitleAttr = wkrn.getAttributeByName(liabilityCls, "заголовок окна");
                
                KrnClass langCls = wkrn.getClassByName("Language");
                KrnAttribute codeAttr = wkrn.getAttributeByName(langCls, "code");
                KrnObject[] langsRU = wkrn.getObjectsByAttribute(langCls.id, codeAttr.id, 0, 0, "RU", 0);
                KrnObject[] langsKZ = wkrn.getObjectsByAttribute(langCls.id, codeAttr.id, 0, 0, "KZ", 0);
                
                String liabilityDialogTitleRU = wkrn.getStringsSingular(lastObj.id, liabilityDialogTitleAttr.id, langsRU.length > 0 ? langsRU[0].id : 0, false, false);
                String liabilityDialogTitleKZ = wkrn.getStringsSingular(lastObj.id, liabilityDialogTitleAttr.id, langsKZ.length > 0 ? langsKZ[0].id : 0, false, false);
                obj.add("liabilityDialogTitleRU", liabilityDialogTitleRU);
                obj.add("liabilityDialogTitleKZ", liabilityDialogTitleKZ);

                KrnAttribute liabilityMSDocRUAttr = wkrn.getAttributeByName(liabilityCls, "файл обязательства о неразглашении русский");
                KrnAttribute liabilityMSDocKZAttr = wkrn.getAttributeByName(liabilityCls, "файл обязательства о неразглашении казахский");
                
                KrnObject liabilityMSDocRU = wkrn.getObjectsSingular(lastObj.id, liabilityMSDocRUAttr.id, false);
                if (liabilityMSDocRU != null) {
                    byte[] bytes = wkrn.getBlob(liabilityMSDocRU, "file", 0, 0, 0);
                    if (bytes.length > 0) {
                    	obj.add("liabilityTextRU", new String(kz.gov.pki.kalkan.util.encoders.Base64.encode(bytes), "UTF-8"));
                    }
                }
                
                KrnObject liabilityMSDocKZ = wkrn.getObjectsSingular(lastObj.id, liabilityMSDocKZAttr.id, false);
                if (liabilityMSDocKZ != null) {
                    byte[] bytes = wkrn.getBlob(liabilityMSDocKZ, "file", 0, 0, 0);
                    if (bytes.length > 0) {
                    	obj.add("liabilityTextKZ", new String(kz.gov.pki.kalkan.util.encoders.Base64.encode(bytes), "UTF-8"));
                    }
                }

                KrnAttribute liabilityCheckAttr = wkrn.getAttributeByName(liabilityCls, "текст для checkbox");
                String liabilityCheckTextRU = wkrn.getStringsSingular(lastObj.id, liabilityCheckAttr.id, langsRU.length > 0 ? langsRU[0].id : 0, false, false);
                String liabilityCheckTextKZ = wkrn.getStringsSingular(lastObj.id, liabilityCheckAttr.id, langsKZ.length > 0 ? langsKZ[0].id : 0, false, false);
                obj.add("liabilityCheckTextRU", liabilityCheckTextRU);
                obj.add("liabilityCheckTextKZ", liabilityCheckTextKZ);
                
                obj.add("liabilityObjectUID", lastObj.uid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void setDataLangId(long langId) {
        this.dataLangId = langId;
        try {
            LangHelper.WebLangItem li = LangHelper.getLangById(langId, configNumber);
            getKernel().setDataLanguage(li.obj);
            //LangHelper.WebLangItem li = LangHelper.getLangById(langId);
            ifcManager.setDataLangId(langId);
            if (frameManager.getCurrentFrame() != null)
                frameManager.getCurrentFrame().setDataLang(li.obj, true);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public long getDataLangId() {
		return dataLangId;
	}

    public void openFile(Map<String, String> args,
                           HttpServletResponse response,
                           HttpServletRequest request) throws KrnException {
        String cmd = args.get("cmd");
        String fn = args.get("fn");
        String fr = args.get("fr");
        
        if (!fn.startsWith("xxx")) {
        	try {
        		fn = new String(Base64.decode(fn.replaceAll(" ", "+")));
        	} catch (Exception e) {
        	}
        }
    	try {
    		if (fr != null)
    			fr = new String(Base64.decode(fr.replaceAll(" ", "+")));
    	} catch (Exception e) {
    	}
        String fnOld = fr != null ? fr : fn;

        if ("opf".equals(cmd)) { // Open ReportPrinter Window
        	String contentType = null;
            if (fn.endsWith("doc"))
            	contentType = "application/msword";
            else if (fn.endsWith("xls"))
            	contentType = "application/vnd.ms-excel";
            else if (fn.endsWith("docx"))
            	contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            else if (fn.endsWith("xlsx"))
            	contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            else if (fn.endsWith("pdf"))
            	contentType = "application/pdf";
            else if (fn.endsWith("html")) {
            	contentType = "text/html; charset=UTF-8";
                
                try {
                    try {
                        fn = URLDecoder.decode(fn, "UTF-8");
                    } catch (Exception e) {
                        log.error("|USER: " + getUserName() + "| NOT POSSIBLE SITUATION 2");
                    }

                    fn = fn.replaceAll("\\\\", "/");
                    String fileName = "/doc/" + fn;

                    ServletOutputStream stream = null;
                    BufferedInputStream buf = null;
                    try {
                      File doc = new File(WebController.APP_HOME + fileName);
                      FileInputStream input = new FileInputStream(doc);
                      if (contentType != null)
                    	  response.setContentType(contentType);
                      
                      buf = new BufferedInputStream(input);
                      
                      stream = response.getOutputStream();
                      response.setContentLength((int) doc.length());
                      int readBytes = 0;
                      while ((readBytes = buf.read()) != -1)
                    	  stream.write(readBytes);
                    } catch (IOException ioe) {
                    	throw new ServletException(ioe.getMessage());
                    } finally {
                    	if (stream != null)
                    		stream.close();
                    	if (buf != null)
                    		buf.close();
                    }
                } catch (IOException e) {
                    //log_.error(e, e);
                } catch (ServletException e) {
                    //log_.error(e, e);
                }
                return;
            }
            try {
                fn = fn.replaceAll("\\\\", "/");
                String fileName = "/doc/" + fn;

                ServletOutputStream stream = null;
                BufferedInputStream buf = null;
                try {
                  stream = response.getOutputStream();
                  File doc = new File(WebController.APP_HOME + fileName);

                  FileInputStream input = new FileInputStream(doc);
                  if (contentType == null && doc.length() > 100) {
                	  byte[] buf1 = new byte[100];
                	  input.read(buf1);
                	  input.close();
                	  String temp = Funcs.normalizeInput(new String(buf1, "UTF-8"));
                	  if (temp.contains("docProps/core.xml"))
                      	contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                	  else if (temp.contains("_rels/.rels"))
                      	contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                	  else if (temp.contains("‰PNG"))
                    	contentType = "image/png";
                	  else if (temp.contains("►JFIF"))
                      	contentType = "image/jpeg";
                	  else if (temp.contains("GIF89"))
                      	contentType = "image/gif";
                  	  else if (temp.contains("%PDF-"))
                        	contentType = "application/pdf";
                  }
                  if (contentType != null)
                	  response.setContentType(contentType);

                  fnOld = URLEncoder.encode(fnOld, "UTF-8");
                  fnOld = fnOld.replaceAll("\\+", "%20");
            	  response.addHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fnOld);
                  response.setContentLength((int) doc.length());
                  response.setCharacterEncoding("UTF-8");
                  input = new FileInputStream(doc);
                  buf = new BufferedInputStream(input);
                  
                  Funcs.writeStream(input, stream, Constants.MAX_DOC_SIZE);
                } catch (IOException ioe) {
                	throw new ServletException(ioe.getMessage());
                } finally {
                	if (stream != null)
                		stream.close();
                	if (buf != null)
                		buf.close();
                }
            } catch (IOException e) {
                //log_.error(e, e);
            } catch (ServletException e) {
                //log_.error(e, e);
            }
        }
    }

    public long getInterfaceLangId() {
        return interfaceLangId;
    }

    public ProcessHelper getProcessHelper() {
        return processHelper;
    }

    public TaskHelper getTaskHelper() {
        return taskHelper;
    }

    public void taskReload(long flowId, long ifsPar, boolean isStartAutoAct) {
        if (taskHelper != null){
            taskHelper.reloadTask(flowId, ifsPar, isStartAutoAct, false);
        }
    }

    public WebFrameManager getFrameManager() {
        return frameManager;
    }

    public ArchiveHelper getArchiveHelper() {
        return archiveHelper;
    }

    public WebInterfaceManager getInterfaceManager() {
        return ifcManager;
    }

    public ResourceBundle getResource() {
        return resource;
    }

    public String process(Map<String, String> params, FileItem fileItem) throws Exception {
        lastPing = System.currentTimeMillis();
        String id = params.get("id");
        String uid = params.get("uid");

        if ("getFile".equals(fileItem.getFieldName())) {
        	File dir = WebController.WEB_DOCS_DIRECTORY;
        	OutputStream os = null;
        	try {
                String fn = fileItem.getName();
                String fs = "";
                int beg = fn.lastIndexOf("\\");
                if (beg == -1) {
                    beg = fn.lastIndexOf("/");
                }
                if (beg > -1) {
                    fn = fn.substring(beg + 1);
                }
                
                beg = fn.lastIndexOf('.');
                if (beg > -1) {
                	fs = fn.substring(beg);
                	fn = fn.substring(0, beg);
                }
                
                File file;
                int i = 0;
                do {
                	file = new File(dir, fn + (i++ > 0 ? ("-" + i) : "") + fs);
                } while (!file.createNewFile());
                
                deleteOnExit(file);

                os = new FileOutputStream(file);
                Funcs.writeStream(fileItem.getInputStream(), os, Constants.MAX_DOC_SIZE);
                
		        WebFrame frm = frameManager.getCurrentFrame();
		        frm.setFile(file);
            } catch (Exception e) {
                log.error(e, e);
            } finally {
            	Utils.closeQuietly(os);
            }
        } else {
	        if (id == null && uid == null) {
	        	int w = Integer.parseInt(params.get("width"));
	        	int h = Integer.parseInt(params.get("height"));
	        	int methodVersion = params.containsKey("methodVersion") ? Integer.parseInt(params.get("methodVersion")) : 1;
	            byte[] uploadImg = fileItem.get();
	            byte[] img = methodVersion == 2 ? new SystemOp(null).getScaledImage2(uploadImg, w, h, "PNG") : new SystemOp(null).getScaledImage(uploadImg, w, h, "PNG");
	            
	            KrnClass cls = getKernel().getClassByName("User");
	            getKernel().setBlob(getKernel().getUser().getObject().id, cls.id, "аватар", 0, img, 0, 0);
	        } else {
		        WebFrame frm = frameManager.getCurrentFrame();
		        
		        WebComponent comp = id != null ? frm.getComponent(id) : frm.getComponentByUID(uid);
		        
		        if (comp instanceof OrWebImage) {
		            OrWebImage img = (OrWebImage) comp;
		            img.setValue(fileItem);
		        } else if (comp instanceof OrWebDocField) {
		            OrWebDocField df = (OrWebDocField) comp;
		            df.setValue(fileItem);
		        } else if (comp instanceof OrWebDocFieldColumn) {
		        	OrWebDocFieldColumn dfc = (OrWebDocFieldColumn) comp;
		            String row = params.get("row");
		            dfc.setValue(Integer.parseInt(row), fileItem);
		        } else if (comp instanceof OrWebTable) {
		        	OrWebTable df = (OrWebTable) comp;
		            String row = params.get("row");
		            String col = params.get("col");
		            df.uploadDoc(Integer.parseInt(row),
		                    Integer.parseInt(col), fileItem);
		            id = id + "_" + col;
		        }
		
		        if (uid == null) {
			        StringBuffer b = new StringBuffer();
			        b.append("<html>");
			        b.append("<body onload=\"parent.doLoad('").append(id).append("');\"></body>");
			        b.append("</html>");
			        return b.toString();
		        }
	        }
        }
        return "";
    }

    public String process(Map<String, String> params, InputStream is, String fileName) {
        lastPing = System.currentTimeMillis();
        String id = params.get("id");

        WebFrame frm = frameManager.getCurrentFrame();
        WebComponent comp = frm.getComponent(id);
        if (comp instanceof OrWebImage) {
            OrWebImage img = (OrWebImage) comp;
            img.setValue(is);
        } else if (comp instanceof OrWebDocField) {
            OrWebDocField df = (OrWebDocField) comp;
            df.setValue(is, fileName);
        } else if (comp instanceof OrWebTable) {
        	OrWebTable df = (OrWebTable) comp;
            String row = params.get("row");
            String col = params.get("col");
            df.uploadDoc(Integer.parseInt(row),
                    Integer.parseInt(col), is, fileName);
            id = "-1";
        }

        return "<r></r>";
    }

/*    private String getParamsString(Map<String, String> args) {
        StringBuffer res = new StringBuffer();
        for (Iterator<String> it = args.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String val = args.get(key);
            res.append(key).append("=").append(val);
            if (it.hasNext()) res.append("&");
        }
        return res.toString();
    }
*/
    public String getUserName() {
        return userName;
    }

    public void setIsForPublicUser(boolean b) {
        this.isForPublicUser = b;
    }

    public boolean isForPublicUser() {
        return isForPublicUser;
    }

    public boolean isOnlyArchive() {
		return isOnlyArchive;
	}

	public void setOnlyArchive(boolean isOnlyArchive) {
		this.isOnlyArchive = isOnlyArchive;
	}

	public void release() {
        if (taskHelper != null) taskHelper.release();
        taskHelper = null;
        GlobalConfig.removeInstance(wkrn);
		ConfigObject.removeInstance(wkrn);
        wkrn.release();
        wkrn = null;
        if (processHelper != null) processHelper.release();
        processHelper = null;
        frameManager.release();
        frameManager = null;
        ifcManager = null;
        commonHelper = null;
        archiveHelper.release();
        archiveHelper = null;
        resource = null;
        helpObjs = null;
        config = null;
        
        int count = files.size();
        if (count < Constants.MAX_ELEMENTS_COUNT_2) {
	        for (int i = count - 1; i>=0 && i<Constants.MAX_ELEMENTS_COUNT_2; i--) {
	        	File f = files.get(i);
	        	try {
	        		if (f != null) {
	        			log.info("deleting file after WebSession close: " + f.getAbsolutePath());
	        			f.delete();
	        		}
	        	} catch (Exception e) {}
	        	f = null;
	        }
        }
	    files = null;
        
        if (webUser != null) {
        	WebController.releaseHttpSession(null, webUser.getGUID(), false);
        }
        webUser = null;
        	
        wakeupLongPolling();
    }
	
	public synchronized void wakeupLongPolling() {
		this.longPolling = false;
        try {
        	this.notifyAll();
        } catch (Exception e) {}
	}

    public Integer getId() {
        return id_;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        sendMultipleCommand("alert", message);
    }

    public void setPopupMessage(String message) {
        this.message = message;
        sendMultipleCommand("slide", message);
    }

    public boolean isNeedMain() {
        return needMain;
    }

    public String getVersion() {
        if (version == null) {
            String buildNumber = "4.0.11";
            long replicationNumber = 0;

            KrnObject[] objs = null;
            try {
                objs = wkrn.getClassObjects(
                        wkrn.getClassByName("Import"),new long[0], 0);
            } catch (KrnException e) {
                log.error(e, e);
            }
            KrnObject last = null;
            if (objs.length > 0) {
                last = objs[0];
                for (int i = 0; i < objs.length; i++) {
                    if (objs[i].id > last.id) {
                        last = objs[i];
                    }
                }
            }
            if (last != null) {
                try {
                    replicationNumber = (long) wkrn.getLongsSingular(
                        last, wkrn.getAttributeByName(wkrn.getClassByName("Import"), "exp_id"), true);
                } catch (KrnException e) {
                    log.error(e, e);
                }
            }

            try {
                String pathToThisClass = getClass().getResource("/kz/tamur/web/common/WebSession.class").toString();
                int pos = pathToThisClass.lastIndexOf("!");
                if (pos != -1) {
                    String manifestPath = pathToThisClass.substring(0, pos + 1) + "/META-INF/MANIFEST.MF";
                    Manifest m = new Manifest(new java.net.URL(manifestPath).openStream());
                    Attributes attrs = m.getMainAttributes();
                    String version = attrs.getValue("Implementation-Version");

                    if (version != null) {
                        buildNumber = version.replace("b", "").replace("-", ".");
                    }
                    else
                        buildNumber = "NoVersion";
                }
            } catch(IOException e) {
                log.error(e, e);
            }
            version = buildNumber + "." + replicationNumber;
        }
        return version;
    }
    
    public void loadHelpObjects() {
		helpObjs = new ArrayList<>();
    	if (!isOnlyArchive) {
	        //достаем пользоватея и смотрим его помощь если есть то вставляем помощь в меню
	        User user = wkrn.getUser();
	        List<KrnObject> helps = user.getHelp();
	        List<HelpFile> helpFiles = user.getHelpFiles();

            for (KrnObject help : helps) {
                long langId = getInterfaceLangId();
                if (help != null) {
                	helpObjs.add(new OrWebNoteBrowser(help, langId, this));
                }
            }

            for (HelpFile helpFile : helpFiles) {
            	helpObjs.add(helpFile);
            }
    	}
    }

	public List<Object> getHelpObjs() {
		if (helpObjs == null)
			loadHelpObjects();
		return helpObjs;
	}
	
    public String getHelpHTML(int i) {
    	OrWebNoteBrowser b = (OrWebNoteBrowser)helpObjs.get(i);
       	return "";//b.getHtml(b.getTitle());
    }

	public void deleteOnExit(File f) {
		if (f != null)
			files.add(f);
	}

	public long getLastPing() {
		return lastPing;
	}
	
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfileConteiner() {
        return conteiner;
    }

    public void setProfileConteiner(String conteiner) {
        this.conteiner = conteiner;
    }

    public String getProfilePassword() {
        return profileSaved;
    }

    public void setProfilePassword(String profile) {
        this.profileSaved = profile;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getNCAProfile() {
        return ncaProfile;
    }

    public void setNCAProfile(String profile) {
        this.ncaProfile = profile;
    }

    public String getNCAProfileConteiner() {
        return ncaConteiner;
    }

    public void setNCAProfileConteiner(String conteiner) {
        this.ncaConteiner = conteiner;
    }

    public String getNCAProfilePassword() {
        return ncaProfileSaved;
    }

    public void setNCAProfilePassword(String profile) {
        this.ncaProfileSaved = profile;
    }

    public String getNCACert() {
        return ncaCert;
    }

    public void setNCACert(String cert) {
        this.ncaCert = cert;
    }

	public WebMenuItem getProcessMenu() {
		return processMenu;
	}

	public void addAction(WebActionMaker action) {
		actions.put(action.getId(), action);
	}
	public void addChange(WebChangeMaker change) {
	    changes.put(change.getId(), change);
	}

	public synchronized long getNextId() {
		return lastId ++;
	}

	public boolean isOpenPrev() {
		return openPrev;
	}

	public void setOpenPrev(boolean openPrev) {
		this.openPrev = openPrev;
	}

	public int getConfigNumber() {
		return configNumber;
	}

	public CommonHelper getCommonHelper() {
		return commonHelper;
	}

    public void updateBrowser(String browserType, String browserOS) {
        isChrome = Constants.CHROME.equals(browserType);
        isOmniweb = Constants.OMNIWEB.equals(browserType);
        isSafari = Constants.SAFARI.equals(browserType);
        isOpera = Constants.OPERA.equals(browserType);
        isIcab = Constants.ICAB.equals(browserType);
        isKonqueror = Constants.KONQUEROR.equals(browserType);
        isFirefox = Constants.FIREFOX.equals(browserType);
        isCamino = Constants.CAMINO.equals(browserType);
        isNetscape = Constants.NETSCAPE.equals(browserType);
        isExplorer = Constants.EXPLORER.equals(browserType);
        isMozilla = Constants.MOZILLA.equals(browserType);
        isWin = Constants.WIN.equals(browserOS);
        isMac = Constants.MAC.equals(browserOS);
        isIphone = Constants.IPHONE.equals(browserOS);
        isLinux = Constants.LINUX.equals(browserOS);
    }

    public String getIDCoreBrowser() {
        if (isChrome) {
            return "-webkit-";
        } else if (isFirefox || isMozilla || isNetscape) {
            return "-moz-";
        } else if (isOpera) {
            return "-o-";
        } else if (isExplorer) {
            return "-ms-";
        } else {
            return "";
        }
    }
    public void doOnNotification(SystemNote note) {
        if (frameManager.getCurrentFrame() != null) {
            frameManager.getCurrentFrame().doOnNotification(note);
        }
    }
    
    public void removeChanges(long frameId, String key) {
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) return;
    	}
    	synchronized (changesObjs) {
            changesObjs.remove(key);
		}
    }
    
    public JsonObject getChanges(long frameId, String key) {
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) return null;
    	}
    	synchronized (changesObjs) {
            return changesObjs.get(key);
		}
    }

    public void removeCommand(String key) {
    	synchronized (commands) {
            commands.remove(key);
		}
    }
    
    /**
     * Отправляет комманду в очередь для отправки на клиента
     */
    public void sendCommand(String command, Object param) {
    	synchronized (commands) {
    		if (!noMainUiCommand || !"main_ui".equals(command)) 
        		commands.put(command, param);
        	if ("start_ui".equals(command) || "next_ui".equals(command))
        		noMainUiCommand = true;
        	else if ("main_ui".equals(command))
        		noMainUiCommand = false;
    	}
    	
        wakeupLongPolling();
    }
    
    public void sendMultipleCommand(String command, Object param) {
    	synchronized (commands) {
    		List<Object> list = (List) commands.get(command);
    		if (list == null) {
    			list = new ArrayList<Object>();
    			commands.put(command, list);
    		}
    		if (!list.contains(param))
    			list.add(param);
		}
        wakeupLongPolling();
    }

    public void removeChange(long frameId, String key, String path, String index) {
    	JsonObject json = null;
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) return;
    	}
    	synchronized (changesObjs) {
    		json = changesObjs.get(key);
        	try {
    	        if (json != null) {
    	            if (path.contains(".")) {
    	                String[] nodes = path.split("\\.");
    	                int length = nodes.length;
    	                JsonObject jsonSub = json;
    	                for (int i = 0; i < length; i++) {
    	                    if (i == length - 1) {
    	                    	remove(jsonSub, nodes[i], index);
    	                    } else {
    	                    	JsonValue jv = jsonSub.get(nodes[i]);
    	                    	if (jv != null) {
    								jsonSub = jv.asObject();
    	                    	} else
    	                    		return;
    	                    }
    	                }
    	            } else {
    	            	if (json.has(path))
                        	remove(json, path, index);
    	            }
    	    	}
        	} catch (Exception e) {
                log.error(e, e);
        	}
    	}
    }
    
    private void remove(JsonObject parent, String path, String index) {
		if (index == null)
			parent.remove(path);
		
		JsonValue jv = parent.get(path);
		if (jv != null && jv.isArray()) {
			JsonArray arr = jv.asArray();
    		for (int j=0; j < arr.size(); j++) {
    			JsonValue child = arr.get(j);
    			String ind = getUniqueIndex(child);
    			if (child instanceof JsonObject && ind != null) {
    				if (index.equals(ind)) {
    					arr.remove(j);
    					break;
	    			}
    			}
    		}
		}
    }
    
    private JsonValue get(JsonObject parent, String path, long index) {
		if (index == -1)
			return parent.get(path);

		JsonValue jv = parent.get(path);
		if (jv != null && jv.isArray()) {
			JsonArray arr = jv.asArray();
    		for (int j=0; j < arr.size(); j++) {
    			JsonValue child = arr.get(j);
    			if (child instanceof JsonObject && (((JsonObject)child).has("index") || ((JsonObject)child).has("id"))) {
	    			long ind = ((JsonObject)child).has("index")
	    							? ((JsonObject)child).get("index").asLong()
	    							: ((JsonObject)child).get("id").asLong();
    				if (index == ind) {
    					return child.asObject();
	    			}
    			}
    		}
		}
		return null;
    }

    public JsonValue getChange(long frameId, String key, String path, long index) {
    	JsonObject json = null;
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) return null;
    	}
    	synchronized (changesObjs) {
    		json = changesObjs.get(key);
            if (json != null) {
                if (path.contains(".")) {
                    String[] nodes = path.split("\\.");
                    int length = nodes.length;
                    JsonObject jsonSub = json;
                    for (int i = 0; i < length; i++) {
                        if (i == length - 1) {
                        	if (jsonSub.has(nodes[i]))
                        		return get(jsonSub, nodes[i], index);
                        } else {
                        	if (jsonSub.has(nodes[i])) {
    							jsonSub = jsonSub.get(nodes[i]).asObject();
    							json = jsonSub;
                        	} else
                        		return null;
                        }
                    }
                } else {
                	if (json.has(path))
                		return get(json, path, index);
                }
        	}
    	}
        return null;
    }

    public void addChange(long frameId, String key, JsonObject value) {
    	JsonObject json = null;
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) {
    			changesObjs = new HashMap<String, JsonObject>();
    			changesObjsByFrameId.put(frameId, changesObjs);
    		}
    	}
    	synchronized (changesObjs) {
    		json = changesObjs.get(key);
            if (json == null) {
        		changesObjs.put(key, value);
            } else {
        		List<String> list = new ArrayList<String>();
        		list.addAll(value.names());
        		for (String childKey : list) {
        			setValue(json, childKey, value.get(childKey));
        		}
        	}
    	}
    }

    public void addChange(long frameId, String key, String path, Object value) {
    	if (value == null) value = "";
    	JsonObject json = null;
    	Map<String, JsonObject> changesObjs = null;
    	synchronized (changesObjsByFrameId) {
    		changesObjs = changesObjsByFrameId.get(frameId);
    		if (changesObjs == null) {
    			changesObjs = new HashMap<String, JsonObject>();
    			changesObjsByFrameId.put(frameId, changesObjs);
    		}
    	}
    	synchronized (changesObjs) {
    		json = changesObjs.get(key);
            if (json == null) {
                json = new JsonObject();
        		changesObjs.put(key, json);
            }
            if (path.contains(".")) {
                String[] nodes = path.split("\\.");
                int length = nodes.length;
                JsonObject jsonSub = json;
                for (int i = 0; i < length; i++) {
                    if (i == length - 1) {
                    	setValue(jsonSub, nodes[i], value);
                    } else {
                    	JsonValue jv = jsonSub.get(nodes[i]);
                    	if (jv != null)
                    		jsonSub = jv.asObject();
                    	else {
                            jsonSub = new JsonObject();
                            json.add(nodes[i], jsonSub);
                    	}
                        json = jsonSub;
                    }
                }
            } else {
                setValue(json, path, value);
            }
    	}
    }
    
    private void setValue(JsonObject parent, String key, Object value) {
    	JsonValue oldValue = parent.get(key);
		
    	if ("content".equals(key) || "nodes".equals(key)) {
    		parent.set(key, value);
    	} else if (oldValue instanceof JsonObject && value instanceof JsonObject) {
    		JsonObject oldObj = (JsonObject) oldValue;
    		JsonObject newObj = (JsonObject) value;
    		List<String> list = new ArrayList<String>();
    		list.addAll(newObj.names());
    		for (String childKey : list) {
    			setValue(oldObj, childKey, newObj.get(childKey));
    		}
    	} else if (oldValue instanceof JsonArray && value instanceof JsonArray) {
    		JsonArray oldObj = (JsonArray) oldValue;
    		JsonArray newObj = (JsonArray) value;
    		
    		for (int i=0; i<newObj.size(); i++) {
    			Object newChild = newObj.get(i);
    			Object oldChild = null;
    			int oldIndex = -1;
    			String index = getUniqueIndex(newChild);
    			if (newChild instanceof JsonObject && index != null) {
	    			for (int j=0; j<oldObj.size(); j++) {
	    				JsonObject child = (JsonObject)oldObj.get(j);
	        			String tempIndex = getUniqueIndex(child);
	    				if (index.equals(tempIndex)) {
	    					oldChild = child;
	    					oldIndex = j;
	    					break;
	    				}
	    			}
    			} else if (oldObj.size() == 1) {
    				oldChild = oldObj.get(0);
    			}
    			if (oldChild == null) {
    				oldObj.add(newChild);
    			} else {
    				if (newChild instanceof JsonObject) {
    					if (oldIndex > -1) {
    						oldObj.remove(oldIndex);
    						oldObj.add(oldChild);
    					}
    		    		List<String> list = new ArrayList<String>();
    		    		list.addAll(((JsonObject)newChild).names());
    		    		for (String childKey : list) {
	    	    			setValue((JsonObject)oldChild, childKey, ((JsonObject)newChild).get(childKey));
	    	    		}
    				} else {
        				oldObj.add(newChild);
    				}
    			}
    		}
    	} else if (oldValue != null && value == null) {
    		parent.remove(key);
    	} else {
    		parent.set(key, value);
    	}
    }
    
    private String getUniqueIndex(Object obj) {
    	if (obj instanceof JsonObject) {
    		JsonObject jobj = (JsonObject)obj;
	    	String index = jobj.has("index")
					? String.valueOf(jobj.get("index").asLong())
					: jobj.has("id")
					? String.valueOf(jobj.get("id").asLong())
					: jobj.has("actionId")
					? jobj.get("actionId").asString()
					: null;
					
			return index;
    	}
    	return null;
    }
    
    public String getChanges() {
    	//JsonObject out = new JsonObject();
        //JsonArray components = new JsonArray();
        StringBuilder res = new StringBuilder("{\"changes\":[");
        try {
            if (frameManager == null || frameManager.getCurrentFrame() == null) {
                return "";
            }
        	long frameId = frameManager.getCurrentFrame().getInterfaceId();
        	Map<String, JsonObject> changesObjs = null;
        	synchronized (changesObjsByFrameId) {
        		changesObjs = changesObjsByFrameId.get(frameId);
        		if (changesObjs == null) {
        			changesObjs = new HashMap<String, JsonObject>();
        			changesObjsByFrameId.put(frameId, changesObjs);
        		}
        	}
    		boolean append = false;
        	synchronized (changesObjs) {
                for (Entry<String, JsonObject> cmp : changesObjs.entrySet()) {
                	//JsonObject component = new JsonObject();
                    //component.add(cmp.getKey(), cmp.getValue());
                    //components.add(component);
                    if (append) 
                    	res.append(",");
                    else
                        append = true;

                    try {
                    	res.append("{\"").append(cmp.getKey()).append("\":").append(cmp.getValue().toString()).append("}");
                    } catch (Exception ex) {
                    	log.error("JSON ERROR: " + cmp.getKey() + " - " + res);
                    	log.error(ex, ex);
                    }

                }
                changesObjs.clear();
			}
            //out.add("changes", components);
        	res.append("]");
            if (confirmMessage != null) {
            	res.append(",\"confirm\":").append(Funcs.xmlQuote(confirmMessage));
            	//out.add("confirm", confirmMessage);
            	confirmMessage = null;
            }
        	res.append("}");
        } catch (Exception e) {
            log.error(e, e);
        }
        return res.toString();//out.toString();
    }
    
    public String getCommands() {
        JsonObject out = new JsonObject();
        JsonArray components = new JsonArray();
        synchronized (commands) {
            try {
                for (Entry<String, Object> cmp : commands.entrySet()) {
                	Object v = cmp.getValue();
                	if (v instanceof List) {
                		List<Object> l = (List) v;
                		for (Object o : l) {
                        	JsonObject component = new JsonObject();
	                		component.add(cmp.getKey(), o);
	                		components.add(component);
                		}
                	} else {
                    	JsonObject component = new JsonObject();
                		component.add(cmp.getKey(), v);
                		components.add(component);
                	}
                }
                out.add("commands", components);
            } catch (Exception e) {
                log.error(e, e);
            }
            commands.clear();
        }
        return out.toString();
    }
    
    public String getConfirmMessage() {
		return confirmMessage;
	}

	public void setConfirmMessage(String message) {
    	confirmMessage = message;
    }

    private Object executeMethod(Map<String, String> args, boolean onServer) {
    	String clsName = args.get("cls");
		try {
	    	if (clsName != null) {
	    		KrnClass cls = null;
	    		try {
	    			cls = wkrn.getClassByName(clsName);
	    		} catch (Exception e) {}
	    		
		    	if (cls != null) {
			    	String name = args.get("name");
		            ClientOrLang orlang = new ClientOrLang(wkrn);
		            
			    	if (onServer)
		                return orlang.sexec(cls, cls, name, getMethodArgs(args), new Stack<String>());
			    	else
		                return orlang.exec(cls, cls, name, getMethodArgs(args), new Stack<String>());
		    	} else {
		    		log.error("Не найден класс " + clsName);
		    	}
	    	}
	    	String objUid = args.get("obj");
	    	
	    	if (objUid != null) {
		    	KrnObject obj = "USER".equals(objUid) ? wkrn.getUser().getObject() : wkrn.getObjectByUid(objUid, 0);
		    	if (obj != null) {
			    	String name = args.get("name");
		            ClientOrLang orlang = new ClientOrLang(wkrn);
			    	if (onServer)
			    		return orlang.sexec(obj, obj, name, getMethodArgs(args), new Stack<String>());
			    	else
			    		return orlang.exec(obj, obj, name, getMethodArgs(args), new Stack<String>());
		    	} else {
		    		log.error("Не найден объект " + objUid);
		    	}
	    	}
		} catch (Throwable e) {
			log.error(e, e);
		}

    	return "{}";
    }
    
    private String getAttr(Map<String, String> args) {
        String id = args.get("objId");
        String path = args.get("attr");
        String lang = args.get("lang");

        try {
            KrnObject obj = getKernel().getObjectById(Long.parseLong(id), 0);
            KrnClass cls = getKernel().getClass(obj.classId);
            KrnAttribute attr = getKernel().getAttributeByName(cls, path);

            Object res = null;
            if (attr != null) {
                if (attr.typeClassId == Kernel.IC_BLOB)
                    res = getKernel().getBlob(obj, path, 0, 0, 0);
                else if (attr.typeClassId == Kernel.IC_DATE) {
                    DateValue[] vs = getKernel().getDateValues(new long[] { obj.id }, attr, 0);
                    if (vs != null && vs.length > 0)
                        res = kz.tamur.util.Funcs.convertDate(vs[0].value).toString("dd.MM.yyyy");
                } else if (attr.typeClassId == Kernel.IC_STRING || attr.typeClassId == Kernel.IC_MEMO) {
                    String[] ss = getKernel().getStrings(obj, path, 0, 0);
                    if (ss.length > 0)
                        res = ss[0];
                } else if (attr.typeClassId == Kernel.IC_INTEGER || attr.typeClassId == Kernel.IC_BOOL) {
                    long[] ss = getKernel().getLongs(obj, path, 0);
                    if (ss.length > 0)
                        res = ss[0];
                } else if (attr.typeClassId > 99) {
                    KrnObject[] ss = getKernel().getObjects(obj, path, 0);
                    if (ss.length > 0)
                        res = ss[0].id;
                }
            }
            String value = "";
            if (res instanceof byte[]) {
                value = new String(kz.gov.pki.kalkan.util.encoders.Base64.encode((byte[]) res));
            } else if (res != null) {
                value = res.toString();
            }
            JsonObject r = new JsonObject();
            r.add("result", value);

            return r.toString();
        } catch (Throwable e) {
            log.error(e, e);
        }

        return "{}";
    }

    private List<Object> getMethodArgs(Map<String, String> args) {
    	List<Object> res = new ArrayList<Object>();
    	int i = 0;
    	String arg = args.get("arg" + i);
    	while (arg != null) {
			res.add(arg);
	    	arg = args.get("arg" + ++i);
    	}
    	return res;
    }
    
    /**
     * Реализация "долгого" запроса
     * Метод постоянно проверяет очередь команд и если их находит, отправляет клиенту
     * пока команд нет - запрос висит 
     * @throws InterruptedException 
     */
    private String longPolling() {
        // остановить другие потоки пулинга
        wakeupLongPolling();
        
        try {
            if (wkrn == null) {
                JsonObject obj = new JsonObject();
                obj.add("result", "error");
                obj.add("session", "off");
                return obj.toString();
            } else if (commands.size() != 0) {
                return getCommands();
            }
            synchronized (this) {
                this.longPolling  = true;
                
            	for (int i = 0; i < Constants.TIME_OUT_WEB_LONG_POLLING_QUANTS; i++) {
                	this.wait(Constants.TIME_OUT_WEB_WAIT_QUANT);
                	if (!longPolling)
                		break;

                	if (WebController.isDestroying())
                		return "{}";
            	}
			}
        } catch (InterruptedException e) {
        }
        if (wkrn == null) {
            JsonObject obj = new JsonObject();
            obj.add("result", "error");
            obj.add("session", "off");
            return obj.toString();
        } else if (commands.size() != 0) {
            return getCommands();
        }
        return "{}";
    }
    
	public void sendResult(String extension, String fileName, byte[] out,
			HttpServletResponse response) throws KrnException {
		
		String contentType = contentTypes.get(extension);
		try {
			fileName = URLDecoder.decode(fileName.replaceAll(" ", "+"), "UTF-8");
		} catch (Exception e) {
		}

		if (contentType != null)
			response.setContentType(contentType);

		ServletOutputStream stream = null;
		try {
			stream = response.getOutputStream();

			if (contentType != null)
				response.setContentType(contentType);

			fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
      	  	response.addHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

			response.setContentLength(out.length);
			response.setCharacterEncoding("UTF-8");
			stream.write(out);
		} catch (IOException ioe) {
			log.error(ioe, ioe);
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException ioe) {
					log.error(ioe, ioe);
				}
		}
	}
	
	private void addToFavorites(ProcessNode processNode) throws KrnException {
		// Проверка на наличие
    	KrnClass favouriteProcessCls = wkrn.getClassByName("UserFavouriteProcess");
    	KrnAttribute userAttr = wkrn.getAttributeByName(favouriteProcessCls, "user");
    	KrnAttribute processDefAttr = wkrn.getAttributeByName(favouriteProcessCls, "processDef");
    	KrnObject user = wkrn.getUser().getObject();
    	KrnObject[] objsByUser = wkrn.getObjectsByAttribute(favouriteProcessCls.id, userAttr.id, 0, 0, user, 0);
    	boolean isExists = false;
    	for (int i = 0; i < objsByUser.length; i++) {
    		KrnObject favouriteProcess = wkrn.getObjectsSingular(objsByUser[i].id, processDefAttr.id, false);
    		if (processNode.getObject().equals(favouriteProcess)) {
    			isExists = true;
    			break;
    		}
    	}
		if (!isExists) {
	    	KrnObject favouriteProcess = wkrn.createObject(favouriteProcessCls, 0);
	    	wkrn.setLong(favouriteProcess.id, favouriteProcessCls.id, "processDef", 0, processNode.getObject().id, 0);
	    	wkrn.setLong(favouriteProcess.id, favouriteProcessCls.id, "user", 0, user.id, 0);
		}
	}
	
	private void removeFromFavorites(ProcessNode processNode) throws KrnException {
    	KrnClass favouriteProcessCls = wkrn.getClassByName("UserFavouriteProcess");
    	KrnAttribute userAttr = wkrn.getAttributeByName(favouriteProcessCls, "user");
    	KrnAttribute processDefAttr = wkrn.getAttributeByName(favouriteProcessCls, "processDef");
    	KrnObject user = wkrn.getUser().getObject();
    	KrnObject[] objsByUser = wkrn.getObjectsByAttribute(favouriteProcessCls.id, userAttr.id, 0, 0, user, 0);
    	for (int i = 0; i < objsByUser.length; i++) {
    		KrnObject favouriteProcess = wkrn.getObjectsSingular(objsByUser[i].id, processDefAttr.id, false);
    		if (processNode.getObject().equals(favouriteProcess)) {
    			wkrn.deleteObject(objsByUser[i], 0);
    		}
    	}
	}
	
	public Activity getReadyToOpenActivity(long flowId) {
		Activity notOpenableActivity = null;
        Activity act = taskHelper.getActivityById(flowId);
        if (act == null || ((act.uiType == null || act.uiType.length() == 0) && (act.param & Constants.ACT_ERR) != Constants.ACT_ERR)) {
        	if (notOpenableActivity == null) notOpenableActivity = act;
        	act = taskHelper.getActivityByRootFlowId(flowId);
            if (act == null || ((act.uiType == null || act.uiType.length() == 0) && (act.param & Constants.ACT_ERR) != Constants.ACT_ERR)) {
            	if (notOpenableActivity == null) notOpenableActivity = act;
            	act = taskHelper.getActivityBySuperFlowId(flowId);
                if (act == null || ((act.uiType == null || act.uiType.length() == 0) && (act.param & Constants.ACT_ERR) != Constants.ACT_ERR)) {
                	if (notOpenableActivity == null) notOpenableActivity = act;
                	return notOpenableActivity;
                } else
                	return act;
            } else
            	return act;
        } else
        	return act;
	}

	public void setWaitingFrame(WebFrame frame) {
		frameManager.setWaitingFrame(frame);
	}

	public WebUser getWebUser() {
		return this.webUser;
	}

	public void setWebUser(WebUser webUser) {
		this.webUser = webUser;
	}
	
	public String getUserNameById(long userId, long langId) {
    	Kernel krn = this.getKernel();
    	String res = null;
    	try {
    		KrnClass cls = krn.getClassByName("User");
    		String[] strs = krn.getStrings(new KrnObject(userId, "", cls.id), "sign", langId, 0);
    		if (strs != null && strs.length > 0)
    			res = strs[0];    		
    	} catch(KrnException e) {
    		log.error(e, e);
    	}
    	return res;
	}
	
	public byte[] getNoteSound() {
		Kernel krn = this.getKernel();
		try {
			KrnClass cls = krn.getClassByName("ConfigGlobal");
			KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
			String attrName = "notificationSound";
			return krn.getBlob(krnObjs[0], attrName, 0, 0, 0);
		} catch(KrnException e) {
			log.error(e, e);
		}
		return null;
	}

	public boolean activateNoteSound() {
		Kernel krn = this.getKernel();
		try {
			KrnClass cls = krn.getClassByName("ConfigGlobal");
			KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
			String attrName = "useNotificationSound";
			long useNoteSoundL[] = krn.getLongs(krnObjs[0], attrName, 0);
			if(useNoteSoundL != null && useNoteSoundL.length > 0) {
				if(useNoteSoundL[0] == 1)
					return true;
				else return false;
			}
		} catch(KrnException e) {
			log.error(e, e);
		}
		return false;
	}
	
    private JsonObject getJsonParameter(Map<String, String> args, String prefix) {
    	JsonObject res = new JsonObject();
		for (String name : args.keySet()) {
			if (name.startsWith(prefix)) {
				Pattern arrPattern = Pattern.compile("\\[[^\\]]*\\]");
				Matcher m = arrPattern.matcher(name);
				
				JsonObject parent = res;
				while (m.find()) {
					String childName = m.group();
					childName = childName.substring(1, childName.length() - 1);
					
					if (m.end() == name.length()) {
						parent.set(childName, args.get(name));
					} else {
						JsonValue childValue = parent.get(childName);
						if (childValue == null) {
							childValue = new JsonObject();
							parent.set(childName, childValue);
						}
						parent = childValue.asObject();
					}
				}
			}
		}
		return res.isEmpty() ? null : res;
	}
    
    private void getAxisNames(List axes, JsonObject res, String axis) throws KrnException {
        JsonArray arr = new JsonArray();
        for (Object obj : axes) {
        	JsonObject jsonObj = new JsonObject();
        	KrnObject krnObj = (KrnObject) obj;
        	jsonObj.add("id", krnObj.id);
        	KrnAttribute attrName = wkrn.getAttributeByName(krnObj.classId, "name");
        	String[] names = wkrn.getStrings(krnObj, attrName, interfaceLangId, 0);
        	JsonArray jsNames = new JsonArray();
        	for (String name : names) {
        		jsNames.add(name);
        	}
        	jsonObj.add("text", jsNames);
        	jsonObj.add("value", names[0]);
        	if (res.get("first" + axis.toUpperCase() + "Axis") != null && krnObj.id == res.get("first" + axis.toUpperCase() + "Axis").asObject().get("id").asLong()) {
        		jsonObj.add("selected", true);
        	}
        	arr.add(jsonObj);
        }
        res.add(axis + "AxisId", arr);
    }
    
    public String nextStep(long flowId) {
        JsonObject obj = new JsonObject();
        try {
            Activity act = getTaskHelper().getActivityById(flowId);
            if (act != null) {
            	long oldId = act.ui.id;
            	long oldInfId = act.ui.id;
            	int oldPermit = (int) act.param & ACT_PERMIT;
                try {
                	getTaskHelper().disableActivity(act);

                    String[] res = getKernel().performActivitys(new Activity[] { act }, "");
                    if (res.length == 1 && res[0].equals("synch")) {
                        getTaskHelper().setAutoIfcFlowId_(act.flowId);
                    }
                    if (res.length > 0 && !res[0].equals("synch")) {
                        act.param |= ACT_ERR;
                        String msg = res[0];
                        for (int i = 1; i < res.length; ++i) {
                            msg += "<br>" + res[i];
                        }
                        getTaskHelper().reenableActivity(act, oldId, oldInfId, oldPermit);
                        obj.add("message", msg.replaceFirst("^\\!", ""));
                        obj.add("result", "success");
                    } else {
                        getTaskHelper().reenableActivity(act.flowId);
                        getTaskHelper().addAutoActivity(act);
                        obj.add("result", "success");
                        if (res.length == 1 && res[0].equals("synch")) {
                            getTaskHelper().reloadTask(act.flowId, 0, true, true);
                        }
                    }
                } catch (KrnException e) {
                    log.error(e, e);
                    getTaskHelper().reenableActivity(act, oldId, oldInfId, oldPermit);
                }
            } else {
                obj.add("message", "Не найден процесс с flowId = " + flowId);
                obj.add("result", "error");
            }
            return obj.toString();
        } catch (Exception ex) {
            log.error(ex, ex);
            obj.add("message", "Ошибка при переходе на следующий шаг flowId = " + flowId);
            obj.add("result", "error");
        }
        return obj.toString();
    }
}