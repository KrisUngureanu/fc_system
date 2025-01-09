package kz.tamur.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.UUID;

import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kz.gamma.TumarCSP;
import kz.gamma.asn1.ASN1Sequence;
import kz.gamma.asn1.x509.TBSCertificateStructure;
import kz.gamma.util.encoders.Base64;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.common.ErrorCodes;
import kz.tamur.common.PasswordPolicy;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.rt.Utils;
//import kz.tamur.server.login.LtpaLoginModule;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.web.common.LoginException;
import kz.tamur.web.common.UpdateContent;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.common.WebUser;
import kz.tamur.web.common.WebUtils;
import kz.tumar.Signer32;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.ConnectionManager;
import com.cifs.or2.server.db.ConnectionManagerLocal;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 06.06.2006
 * Time: 12:15:20
 * To change this template use File | Settings | File Templates.
 */
public class WebController extends HttpServlet {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + WebController.class);
    public static final Log jspLog = LogFactory.getLog("OR3.JSP" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
    
    private static Map<String, Map<String, Object>> httpSessions = new HashMap<>();
    
    // Ставим текущую дату при каждом релизе
    public static String BUILD_NUMBER = "211215";
    
    public static String APP_PATH;
    public static String PATH_IMG;
    public static String LOG_FOLDER;
    public static String APP_HOME;
    public static String IMG_HOME;
    public static String BACK_PAGE = "/or3";
    public static String TARGET_DEFAULT = "Web Page";
    public static String REFRESH_PERIOD = "10000";
    public static String PING_PERIOD = "60000";
    public static String CHECK_CONFIRM_PERIOD = "10000";
    public static String CHECK_SIGN_PERIOD = "5000";
	public static int WEB_SESSION_TIMEOUT = 180000;
    
	public static String PATH_TO_LOGO = null;
	public static String WINDOW_TITLE = null;
	public static String PATH_TO_LOGO_LOGIN = null;
	
    public static String TITLE_PAGE_HEIGTH;
    public static boolean ASK_CONFIRM = true;
    public static boolean EDITABLE_COMBO = false;

    public static int MENU_X = 80;
    public static int MENU_HEIGHT = 20;
    public static String HELP_LINK = null;
    public static String ZEBRA_COLOR_1 = null;
    public static String ZEBRA_COLOR_2 = null;

    public static String SERVER_TYPE = null;
    public static String[] SERVER_NAME = null;
    public static String[] SERVER_HOST = null;
    public static String[] SERVER_PORT = null;
    public static String[] BASE_NAME = null;
    public static boolean[] NO_KERNEL = null;
    public static boolean NO_COMP_DESCRIPTION = true;
    public static boolean APPLET_ON_INTERFACE = false;
    public static boolean PROCESS_MENU = false;

    public static String WINDOW_TITLE_RU = "Or3";
    public static String WINDOW_TITLE_KZ = "Or3";

    public static String[] TASK_TABLE_COLS;
    public static String[] TASK_TABLE_NAMES_RU;
    public static String[] TASK_TABLE_NAMES_KZ;
    public static String[] TASK_TABLE_COLUMN_ALIGNS;
    public static String[] TASK_TABLE_COLUMN_WIDTHS;
    public static boolean[] ADVANCED_UI;
    public static boolean[] SE_UI;
    public static String[] DIR_JS_CSS;
    public static int FETCH_DEPTH;
    public static String THEME;
    public static boolean DEBUG;
    public static boolean BREADCRUMPS_ON = true;
    public static boolean HIDE_CLOSE_INTERFACE_BUTTON = false;
    public static boolean DO_AFTER_TASKLIST_UPDATE;

    public static final int RESPONSE_XML = 0;
    public static final int RESPONSE_HTML = 1;
    public static final int RESPONSE_JSON = 2;
    
    public static String ROOT_CERT_PATH;
    public static String OCSP_SERVICE_URL = "http://ocsp.pki.kz:60001";
    public static String PROXY_HOST;
    public static String PROXY_PORT;
    
    public static long DOWNTIME = -1;
    
    public static String[] HIDE_ITEMS = null;
    public static boolean SHOW_FAVOURITE_PROCCESSES;
    public static boolean ACTIVATE_CHAT;
    public static boolean ACTIVATE_INTERVIEW; 

    public static File WEB_DOCS_DIRECTORY;
    public static File WEB_IMAGES_DIRECTORY;
    public static String WEB_IMGAGES_SUBDIR = "/images/foto/";
    
    public static final int FILE_SIZE_THRESHOLD = Integer.parseInt(Funcs.getValidatedSystemProperty("upload.memory.size", "1024")) * 1024;
    private static boolean isDestroying = false;


    // Менеджер для создания подключения к различным базам данных
    @EJB(beanName="ConnectionManager", beanInterface = ConnectionManagerLocal.class)
    private ConnectionManagerLocal connectionManager;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        isDestroying = false;
        		
        synchronized (this) {
            log.info("Loading servlet init params...");
            
            httpSessions.clear();
            
            if(servletConfig.getInitParameter("logFolder")!= null) {
                LOG_FOLDER = servletConfig.getInitParameter("logFolder").replaceAll("\\\\", "/");
            }
            log.info("LOG_FOLDER = " + LOG_FOLDER);
            
            APP_HOME =System.getProperty("apphome") != null? System.getProperty("apphome").replaceAll("\\\\", "/")
        			:servletConfig.getInitParameter("apphome").replaceAll("\\\\", "/");
            File parent = Funcs.getCanonicalFile(APP_HOME);
        	APP_HOME = parent.getAbsolutePath().replaceAll("\\\\", "/");
            log.info("APP_HOME = " + APP_HOME);
            
    		// Проверяем наличие папки doc, images/foto и очищаем ее
            WEB_DOCS_DIRECTORY = Funcs.getCanonicalFile(new File(parent, "doc"));
            WEB_DOCS_DIRECTORY.mkdirs();
    		File[] files = WEB_DOCS_DIRECTORY.listFiles();
            for (File file : files) {
            	try {
    	        	if (file.isFile()) {
    	        		log.info("deleting file: " + file.getAbsolutePath());
    	        		file.delete();
    	        	}
            	} catch (Exception e) {}
            }

            IMG_HOME = APP_HOME + WEB_IMGAGES_SUBDIR;
            File d = new File(parent, "images");
            d.mkdirs();
            WEB_IMAGES_DIRECTORY = Funcs.getCanonicalFile(new File(d, "foto"));
            WEB_IMAGES_DIRECTORY.mkdir();
    		files = WEB_IMAGES_DIRECTORY.listFiles();
            for (File file : files) {
            	try {
    	        	if (file.isFile()) {
    	        		log.info("deleting file: " + file.getAbsolutePath());
    	        		file.delete();
    	        	}
            	} catch (Exception e) {}
            }

            APP_PATH = servletConfig.getServletContext().getContextPath();
            PATH_IMG = APP_PATH + "/images/foto/";
            
            log.info("APP_PATH = " + APP_PATH);
            log.info("PATH_IMG = " + PATH_IMG);

            String tmp = servletConfig.getInitParameter("ref.fetch.depth");
            FETCH_DEPTH = (tmp == null) ? 1 : Integer.parseInt(tmp);

            System.setProperty("ref.fetch.depth", String.valueOf(FETCH_DEPTH));

            BACK_PAGE = servletConfig.getInitParameter("backpage");
            TITLE_PAGE_HEIGTH = servletConfig.getInitParameter("titlePageHeight");

            tmp = servletConfig.getInitParameter("refresh_period");
            if (tmp != null)
                REFRESH_PERIOD = tmp;

            tmp = servletConfig.getInitParameter("ping_period");
            if (tmp != null)
                PING_PERIOD = tmp;

            tmp = servletConfig.getInitParameter("check_confirm_period");
            if (tmp != null)
                CHECK_CONFIRM_PERIOD = tmp;

            tmp = servletConfig.getInitParameter("check_sign_period");
            if (tmp != null)
                CHECK_SIGN_PERIOD = tmp;

            tmp = servletConfig.getInitParameter("web_session_timeout");
            if (tmp != null)
                WEB_SESSION_TIMEOUT = Integer.valueOf(tmp);

            tmp = servletConfig.getInitParameter("deftarget");
            if (tmp != null) {
                TARGET_DEFAULT = tmp;
            }
            tmp = servletConfig.getInitParameter("serverhost");
            if (tmp != null) {
                System.setProperty("com.sun.CORBA.ORBServerHost", tmp);
            }
            tmp = servletConfig.getInitParameter("askConfirm");
            if (tmp != null)
                ASK_CONFIRM = Boolean.valueOf(tmp);

            tmp = servletConfig.getInitParameter("editableCombo");
            if (tmp != null)
                EDITABLE_COMBO = Boolean.valueOf(tmp);

            tmp = servletConfig.getInitParameter("menuX");
            if (tmp != null)
                MENU_X = Integer.valueOf(tmp);

            tmp = servletConfig.getInitParameter("menuHeight");
            if (tmp != null)
                MENU_HEIGHT = Integer.valueOf(tmp);

            tmp = servletConfig.getInitParameter("helpLink");
            if (tmp != null)
                HELP_LINK = tmp;

            tmp = servletConfig.getInitParameter("zebra1");
            if (tmp != null)
                ZEBRA_COLOR_1 = tmp;

            tmp = servletConfig.getInitParameter("zebra2");
            if (tmp != null)
                ZEBRA_COLOR_2 = tmp;

            tmp = servletConfig.getInitParameter("reportType");
            if (tmp != null) {
            	System.setProperty("reportType", tmp);
            }
            
            tmp = servletConfig.getInitParameter("useLocalCache");
            if (tmp != null) {
                System.setProperty("useLocalCache", tmp);
            }
            
            tmp = servletConfig.getInitParameter("needExpandExcelCell");
            if (tmp != null) {
                System.setProperty("needExpandExcelCell", tmp);
            }
            tmp = servletConfig.getInitParameter("noRights");
            if (tmp != null) {
                System.setProperty("noRights", tmp);
            }
            tmp = servletConfig.getInitParameter("finCentre");
            if (tmp != null) {
                System.setProperty("finCentre", tmp);
            }
            
            SERVER_TYPE = servletConfig.getInitParameter("serverType");
            
            log.info("configName = " + servletConfig.getInitParameter("configName"));
            log.info("hosts = " + servletConfig.getInitParameter(Context.URL_PKG_PREFIXES));
            log.info("ports = " + servletConfig.getInitParameter(Context.PROVIDER_URL));
            log.info("baseName = " + servletConfig.getInitParameter("baseName"));
            log.info("noKernel = " + servletConfig.getInitParameter("noKernel"));
            log.info("advanced_ui = " + servletConfig.getInitParameter("advanced_ui"));
            log.info("knb_ui = " + servletConfig.getInitParameter("knb_ui"));
            log.info("se_ui = " + servletConfig.getInitParameter("se_ui"));
            
            String[] confsArr = servletConfig.getInitParameter("configName").split(",");
            String[] hostsArr = servletConfig.getInitParameter(Context.URL_PKG_PREFIXES).split(",");
            String[] portsArr = servletConfig.getInitParameter(Context.PROVIDER_URL).split(",");
            String[] basesArr = servletConfig.getInitParameter("baseName").split(",");
           
            // необязательные параметры
            String[] dirJSCSSArr = null;
            if (servletConfig.getInitParameter("dirJSCSS") != null) {
                dirJSCSSArr = servletConfig.getInitParameter("dirJSCSS").split(",");
            }
            
            String[] noKernArr;
            if (servletConfig.getInitParameter("noKernel") == null) {
                noKernArr = new String[] { "0" };
            } else {
                noKernArr = servletConfig.getInitParameter("noKernel").split(",");
            }
            String[] advancedArr;
            if (servletConfig.getInitParameter("advanced_ui") == null) {
                advancedArr = new String[] { "0" };
            } else {
                advancedArr = servletConfig.getInitParameter("advanced_ui").split(",");
            }
            String[] knbArr;
            if (servletConfig.getInitParameter("knb_ui") == null) {
                knbArr = null;
            } else {
                knbArr = servletConfig.getInitParameter("knb_ui").split(",");
            }
            String[] seArr;
            if (servletConfig.getInitParameter("se_ui") == null) {
                seArr = new String[] { "0" };
            } else {
                seArr = servletConfig.getInitParameter("se_ui").split(",");
            }
            int count = confsArr.length;
            SERVER_NAME = new String[count]; 
            SERVER_HOST = new String[count]; 
            SERVER_PORT = new String[count];
            BASE_NAME = new String[count];
            NO_KERNEL = new boolean[count];
            ADVANCED_UI = new boolean[count];
            SE_UI = new boolean[count];
            DIR_JS_CSS = new String[count];
            
            for (int i = 0; i < count; i++) {
                log.info("i = " + i+"; configName = " + confsArr[i]+"; hosts = " + hostsArr[i]+"; ports = " + portsArr[i]+"; baseName = " + basesArr[i]);

                SERVER_NAME[i] = getElement(confsArr, i);
                SERVER_HOST[i] = getElement(hostsArr, i);
                SERVER_PORT[i] = getElement(portsArr, i);
                BASE_NAME[i] = getElement(basesArr, i);
                NO_KERNEL[i] = "1".equals(getElement(noKernArr, i));
                ADVANCED_UI[i] = "1".equals(getElement(advancedArr, i));
                SE_UI[i] = "1".equals(getElement(seArr, i));
                if (knbArr != null) {
                    SE_UI[i] = "1".equals(getElement(knbArr, i));
                }
                DIR_JS_CSS[i] = getElement(dirJSCSSArr, i);
            }
            
            // обработка параметров
            for (int i = 0; i < count; i++) {
                if (DIR_JS_CSS[i] != null) {
                	DIR_JS_CSS[i] = DIR_JS_CSS[i].replaceAll("\\\\", "/");
                    if (DIR_JS_CSS[i].charAt(DIR_JS_CSS[i].length() - 1) != '/') {
                        DIR_JS_CSS[i] += '/';
                    }
                }
            }
            tmp = servletConfig.getInitParameter("noDescription");
            if (tmp != null && "false".equals(tmp))
                NO_COMP_DESCRIPTION = false;

            tmp = servletConfig.getInitParameter("addApplet");
            if (tmp != null && "true".equals(tmp))
                APPLET_ON_INTERFACE = true;

            tmp = servletConfig.getInitParameter("processMenu");
            if (tmp != null && "true".equals(tmp))
                PROCESS_MENU = true;

            tmp = servletConfig.getInitParameter("webClientWindowTitleRu");
            if (tmp != null) {
            	WINDOW_TITLE_RU = codesToUnicode(tmp);
            	log.info(WINDOW_TITLE_RU);
            }
            tmp = servletConfig.getInitParameter("webClientWindowTitleKz");
            if (tmp != null) {
            	WINDOW_TITLE_KZ = codesToUnicode(tmp);
            	log.info(WINDOW_TITLE_KZ);
            }
            
            tmp = servletConfig.getInitParameter("taskTableColumns");
        	String cols = tmp != null ? tmp : "svetofor,processName,objectName,taskName,open,openInspector,nextStep,date,time,dateControl,from,initiator,kill";
        	TASK_TABLE_COLS = cols.split(",");

            tmp = servletConfig.getInitParameter("taskTableColumnNamesRu");
        	if (tmp != null) {
        		if (tmp.endsWith(",")) {
        			TASK_TABLE_NAMES_RU = codesToUnicode(tmp + "a").split(",");
        			TASK_TABLE_NAMES_RU[TASK_TABLE_NAMES_RU.length - 1] = "";
        		} else
        			TASK_TABLE_NAMES_RU = codesToUnicode(tmp).split(",");
        	} else {
        		TASK_TABLE_NAMES_RU = new String[] {"", "Процесс", "Объект обработки", "Задача",
    													"", "", "",
    										            "Дата", "Время", "Дата контр.",
    										            "От кого", "Иниц.процесса", ""};
        	}

            tmp = servletConfig.getInitParameter("taskTableColumnNamesKz");
        	if (tmp != null) {
        		if (tmp.endsWith(",")) {
        			TASK_TABLE_NAMES_KZ = codesToUnicode(tmp + "a").split(",");
        			TASK_TABLE_NAMES_KZ[TASK_TABLE_NAMES_KZ.length - 1] = "";
        		} else
        			TASK_TABLE_NAMES_KZ = codesToUnicode(tmp).split(",");

        	} else {
        		TASK_TABLE_NAMES_KZ = new String[] {"", "Процесс", "\u04e8\u04a3деу объектісі", "Тапсырма",
    	                "", "", "",
    	                "К\u04afн", "Са\u0493ат", "Тексеру к\u04afні",
    	                "Кімнен", "Процесті\u04a3 баст.", ""};
        	}

            tmp = servletConfig.getInitParameter("taskTableColumnAligns");
        	cols = tmp != null ? tmp : ",,,,,,,right,right,right,,,";
        	TASK_TABLE_COLUMN_ALIGNS = cols.split(",");

            tmp = servletConfig.getInitParameter("taskTableColumnWidths");
        	cols = tmp != null ? tmp : "20,260,260,200,20,20,20,60,50,70,150,100,20";
        	TASK_TABLE_COLUMN_WIDTHS = cols.split(",");

            tmp = servletConfig.getInitParameter("theme");
            if (tmp != null) {
                THEME = tmp;
            }
            
            tmp = servletConfig.getInitParameter("debug");
            DEBUG = tmp != null && tmp.equals("1");

            tmp = servletConfig.getInitParameter("doAfterTaskListUpdate");
            DO_AFTER_TASKLIST_UPDATE = !"0".equals(tmp);

            tmp = servletConfig.getInitParameter("root_cert_path");
            if (tmp != null)
                ROOT_CERT_PATH = tmp;
            tmp = servletConfig.getInitParameter("ocsp_service_url");
            if (tmp != null)
                OCSP_SERVICE_URL = tmp;
            tmp = servletConfig.getInitParameter("proxy_host");
            if (tmp != null)
                PROXY_HOST = tmp;
            tmp = servletConfig.getInitParameter("proxy_port");
            if (tmp != null)
                PROXY_PORT = tmp;

            PATH_TO_LOGO = servletConfig.getInitParameter("logoPath");
            log.info("PATH_TO_LOGO = " + PATH_TO_LOGO);

            WINDOW_TITLE = servletConfig.getInitParameter("windowTitle");
            if (WINDOW_TITLE == null) WINDOW_TITLE = "е-Қызмет";
            
            ConnectionManagerLocal mgr = connectionManager != null ? connectionManager : ConnectionManager.instance();
            tmp = (String) mgr.getInitParamByName("version");
            tmp = (tmp != null) ? tmp : "1.0";
            WINDOW_TITLE += " (" + tmp + "." + BUILD_NUMBER + ")";
            
            PATH_TO_LOGO_LOGIN = servletConfig.getInitParameter("logoLoginPath");
            log.info("PATH_TO_LOGO_LOGIN = " + PATH_TO_LOGO_LOGIN);

            tmp = servletConfig.getInitParameter("breadcrumpsOn");
            BREADCRUMPS_ON = !"false".equals(tmp);
            
            tmp = servletConfig.getInitParameter("hideCloseIfcBtn");
            HIDE_CLOSE_INTERFACE_BUTTON = "true".equals(tmp);
            
            // обновить хеши JS файлов
            //UpdateContent.update();

            tmp = servletConfig.getInitParameter("downtime");
            if (tmp != null) {
            	try {
            		DOWNTIME = Long.valueOf(tmp);
            	} catch (NumberFormatException e) {
            		log.error("Не удалось преобразовать значение времени оповещения пользователя о сохранении данных!", e);
            	}
            }
            
            tmp = servletConfig.getInitParameter("hideItems");
            if (tmp != null) {
            	HIDE_ITEMS = tmp.split(";");
            }
            
            tmp = servletConfig.getInitParameter("showFavouriteProccesses");
            if (tmp != null) {
            	SHOW_FAVOURITE_PROCCESSES = Boolean.valueOf(tmp);
            }
            
            tmp = (String) ConnectionManager.instance().getInitParamByName("activateChat");
            if (tmp != null) {
            	ACTIVATE_CHAT = Boolean.valueOf(tmp);
            }
            
            tmp = (String) ConnectionManager.instance().getInitParamByName("activateInterview");
            if (tmp != null) {
            	ACTIVATE_INTERVIEW = Boolean.valueOf(tmp);
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,  response);
    }

    private void parseQQFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String query = Funcs.normalizeInput(request.getQueryString());
    	int beg = query.indexOf("qqfile=");
    	int end = query.indexOf("&", beg);
    	if (end == -1) end = query.length();
    	String fileName = query.substring(beg + 7, end);
    	fileName = URLDecoder.decode(fileName, "UTF-8");
    	fileName = Funcs.sanitizeFileName(fileName);
    	
        Map<String, Object> hs = WebController.getSession(request, Funcs.getValidatedParameter(request, "guid"));
        WebUser user = (WebUser) hs.get("user");
        if (user != null) {
        	prepareResponse(response, RESPONSE_XML);
            PrintWriter w = response.getWriter();
            WebSession s = user.getSession();
            Map<String, String> params = getParametersOld(request);
            w.println(s.process(params, request.getInputStream(), fileName));
        }
    }

    private void checkUserMandatoryFields(WebUser user, boolean checkECP) throws KrnException {
	    if (user.getSession().getKernel().getUser().getBase() == null)
	        throw new KrnException(ErrorCodes.USER_NO_BASE, "Вам не назначена рабочая база. Обратитесь к Вашему администратору.");
	    else if (user.getSession().getKernel().getUser().getIfcLang() == null)
	        throw new KrnException(ErrorCodes.USER_NO_IFC_LANG, "Вам не назначен язык интерфейса. Обратитесь к Вашему администратору.");
	    else if (user.getSession().getKernel().getUser().getDataLanguage() == null)
	        throw new KrnException(ErrorCodes.USER_NO_DATA_LANG, "Вам не назначен язык данных. Обратитесь к Вашему администратору.");
	    else if (checkECP && user.getSession().getKernel().getUser().isOnlyECP())
	        throw new KrnException(ErrorCodes.USER_NEED_ECP, "Вам необходимо войти с помощью ЭЦП. Обратитесь к Вашему администратору.");
    }
    
    private void checkFlowExists(HttpServletRequest request, WebUser user) throws KrnException {
	    String id = request.getParameter("flow");
	    if(id != null && id.length() > 0) {
	        long flowId = Long.parseLong(id);
	        Activity act = user.getSession().getTaskHelper().getActivityById(flowId);
	        if (act == null) 
	        	throw new KrnException(ErrorCodes.USER_NO_TASK, "Решение по данной задаче уже принято.");
	    }
    }
    
    private void throwLoginException(WebUser user, String name, Exception ex) throws LoginException {
	    log.error("|USER: " + name + "| Failed to create session");
	    
	    // Если сессия создана, то удалить ее
	    if (user != null && user.getSid() != null && user.getSid() > 0) {
	        WebSessionManager.releaseSession(user.getSid());
	    }
	    if (user != null) user = null;
	    
	    if (ex instanceof KrnException) {
	        log.error("|USER: " + name + "| " + ((KrnException)ex).getMessage());
	        throw new LoginException(((KrnException)ex).code, ((KrnException)ex).getMessage());
	    } else {
	    	log.error(ex, ex);
	        log.error("|USER: " + name + "| Неверное имя пользователя или пароль.");
	        throw new LoginException("Неверное имя пользователя или пароль.");
	    }
    }
    
    private void parseFiles(HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException {
    	File uploadRepo = new File(APP_HOME, "upload");
    	uploadRepo.mkdirs();
    	
        ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(FILE_SIZE_THRESHOLD, uploadRepo));
        List<FileItem> fileItemsList = servletFileUpload.parseRequest(request);

        FileItem fileItem = null;

        Iterator<FileItem> it = fileItemsList.iterator();
        while (it.hasNext()) {
            FileItem fileItemTemp = it.next();
            if (!fileItemTemp.isFormField())
                fileItem = fileItemTemp;
        }

        if (fileItem != null) {
            if (fileItem.getSize() > 0) {
                Map<String, Object> hs = WebController.getSession(request, Funcs.getValidatedParameter(request, "guid"));
                WebUser user = (WebUser) hs.get("user");
                if (user != null) {
                	prepareResponse(response, RESPONSE_HTML);
                    PrintWriter w = response.getWriter();
                    WebSession s = user.getSession();
                    Map<String, String> params = getParametersOld(request);
                    try {
                    	s.process(params, fileItem);
                    	try {
	                        JsonObject obj = new JsonObject();
	    	                obj.add("result", "success");
	                        w.println(obj.toString());
                    	} catch (Exception ex) {
                    		log.error(ex.getMessage());
                    	}
                    } catch (Exception e) {
                    	try {
	                        JsonObject obj = new JsonObject();
	    	                obj.add("result", "error");
	                        obj.add("message", "Ошибка при загрузке файла!");
	                        w.println(obj.toString());
                    	} catch (Exception ex) {
                    		log.error(ex.getMessage());
                    	}
                		log.error(e.getMessage());
                    }
                    fileItem.delete();
                }
            } else {
            	prepareResponse(response, RESPONSE_HTML);
            	PrintWriter w = response.getWriter();
            	JsonObject obj = new JsonObject();
                obj.add("result", "error");
                obj.add("message", "Ошибка при загрузке файла, размер файла равен нулю!");
                w.println(obj.toString());
                fileItem.delete();
             }
        }
    }

    private void prepareResponse(HttpServletResponse response, int type) throws IOException {
    	if (type == RESPONSE_XML)
    		response.setContentType("text/xml; charset=UTF-8");
    	else if (type == RESPONSE_HTML)
    		response.setContentType("text/html; charset=UTF-8");
    	else
    		response.setContentType("application/json; charset=UTF-8");

    	response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
    }
    
    private JsonObject isUseECP(HttpServletRequest request) {
    	JsonObject res = new JsonObject();
		Session session = null;
		try {
			String dsName = BASE_NAME[0];
			session = SrvUtils.getSession(dsName, "sys", null);
			PasswordPolicy policy = session.getPolicy();
			boolean isUseECP = policy.isUseECP();
			res.add("isUseECP", isUseECP);
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (session != null) {
				session.release();
			}
		}
		return res;
    }
    
    private JsonObject executeMethod(HttpServletRequest request, Map<String, String> params) {
    	JsonObject res = new JsonObject();
		Session session = null;
    	// проверяем указаны ли класс и функция для логина
    	String clsName = params.get("cls");
    	String func = params.get("func");
    	
    	if (clsName != null && func != null) {
    		try {
    			String dsName = BASE_NAME[0];
    			session = SrvUtils.getSession(dsName, "sys", null);
    			
	    		KrnClass cls = null;
	    		try {
	    			cls = session.getClassByName(clsName);
	    		} catch (Exception e) {}
	    		
		    	if (cls != null) {
		            SrvOrLang orlang = new SrvOrLang(session);
		            
		            com.cifs.or2.server.Context ctx = new com.cifs.or2.server.Context(new long[0], 0, 0); 
			        session.setContext(ctx);
			        
		            List<Object> args = new ArrayList<>();
		            args.add(params.get("login"));
		            args.add(Integer.parseInt(params.get("purpose")));
		            
		            Map<String, Object> vars = new HashMap<String, Object>();
	                List ret = (List) orlang.exec(cls, cls, func, args, new Stack<String>(), vars);
	        		
	                res.add("code", ret.get(0));
	                res.add("msg", ret.get(1));
	        		
		    	} else {
		    		log.error("Не найден класс " + clsName);
		    		
		    		res.add("msg", "Не найден класс " + clsName);
		    	}
    		} catch (Throwable e) {
    			log.error(e, e);
        		res.add("msg", "Ошибка при сбросе пароля! Обратитесь к разработчику!");
    		} finally {
    			if (session != null) {
    				session.release();
    			}
    		}
    	}

		return res;
    }
    
    private Object executeMethodTr(HttpServletRequest request, Map<String, String> args) {
    	Session session = null;
		try {
			String dsName = BASE_NAME[0];
			session = SrvUtils.getSession(dsName, "sys", null);

	    	long trId = args.get("trId") != null ? Long.parseLong(args.get("trId")) : 0;
	    	String clsName = args.get("cls");
			if (clsName != null) {
	    		KrnClass cls = null;
	    		try {
	    			cls = session.getClassByName(clsName);
	    		} catch (Exception e) {}
	    		
		    	if (cls != null) {
		            SrvOrLang orlang = new SrvOrLang(session);
		            
		            com.cifs.or2.server.Context ctx = new com.cifs.or2.server.Context(new long[0], trId, 0); 
			        session.setContext(ctx);

			        String name = args.get("name");
		            
	                return orlang.exec(cls, cls, name, getMethodArgs(args), new Stack<String>());
		    	} else {
		    		log.error("Не найден класс " + clsName);
		    	}
	    	}
	    	String objUid = args.get("obj");
	    	
	    	if (objUid != null) {
		    	KrnObject obj = session.getObjectByUid(objUid, 0);
		    	if (obj != null) {
			    	String name = args.get("name");
		            SrvOrLang orlang = new SrvOrLang(session);
		            
		            com.cifs.or2.server.Context ctx = new com.cifs.or2.server.Context(new long[0], trId, 0); 
			        session.setContext(ctx);

			        return orlang.exec(obj, obj, name, getMethodArgs(args), new Stack<String>());
		    	} else {
		    		log.error("Не найден объект " + objUid);
		    	}
	    	}
			session.commitTransaction();
		} catch (Throwable e) {
			log.error(e, e);
		} finally {
			if (session != null) {
				session.release();
			}
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

    private JsonObject getContactInfo1(HttpServletRequest request) {
    	JsonObject res = new JsonObject();
    	try {
            Map<String, Object> hs = WebController.getSession(request, Funcs.getValidatedParameter(request, "guid"));
        	boolean withAdditionalInfo = Boolean.parseBoolean(request.getParameter("additionalInfo"));
            WebUser user = (WebUser) hs.get("user");
	        if (user != null) {
	        	WebSession session = user.getSession();
	        	if (session != null) {
		        	Kernel krn = session.getKernel();
	                KrnClass contactInfoCls = krn.getClassByName("ContactInfo");
	                if (contactInfoCls == null) {
	                	return res;
	                }
		        	KrnObject userObj = krn.getUser().getObject();
		        	KrnClass userCls = krn.getClassByName("User");
		        	KrnAttribute executorAttr = krn.getAttributeByName(userCls, "исполнитель");
		        	if (executorAttr != null) { 
			        	KrnObject executorObj = krn.getObjectsSingular(userObj.id, executorAttr.id, false);
			        	if (executorObj != null) {
			        		KrnClass executorCls = krn.getClassByName("Исполнитель");
			        		if (executorCls != null) {
			        			KrnAttribute regOrganStructureAttr = krn.getAttributeByName(executorCls, "рег орган -структ регистрирущ органа-");
			        			if (regOrganStructureAttr != null) {
			    		        	KrnObject regOrganStructureObj = krn.getObjectsSingular(executorObj.id, regOrganStructureAttr.id, false);
			    		        	if (regOrganStructureObj != null) {
			    		        		KrnClass regOrganStructureCls = krn.getClassByName("Структ регистрирущ органа");
			    		        		if (regOrganStructureCls != null) {
			    		        			KrnAttribute valueAttr = krn.getAttributeByName(regOrganStructureCls, "значение");
			    		        			if (valueAttr != null) {
			    		        				List<KrnObject> objs = new ArrayList<>();
		    		    		    			KrnAttribute regOrganAttr = krn.getAttributeByName(contactInfoCls, "regOrgan");
			    		    		        	KrnObject regOrganObj = krn.getObjectsSingular(regOrganStructureObj.id, valueAttr.id, false);
			    		    		        	if (regOrganObj != null) {
			    		    		        		objs.addAll(Arrays.asList(krn.getObjectsByAttribute(contactInfoCls.id, regOrganAttr.id, 0, 0, regOrganObj, 0)));
			    		    		        	}
			    		    		        	KrnAttribute parentAttr = krn.getAttributeByName(regOrganStructureCls, "родитель");
				    		        			if (parentAttr != null) {
				    		    		        	KrnObject parentRegOrganStructureObj = krn.getObjectsSingular(regOrganStructureObj.id, parentAttr.id, false);
				    		    		        	if (parentRegOrganStructureObj != null) {
					    		    		        	KrnObject parentRegOrganObj = krn.getObjectsSingular(parentRegOrganStructureObj.id, valueAttr.id, false);
					    		    		        	if (parentRegOrganObj != null) {
					    		    		        		objs.addAll(Arrays.asList(krn.getObjectsByAttribute(contactInfoCls.id, regOrganAttr.id, 0, 0, parentRegOrganObj, 0)));
					    		    		        	}
				    		    		        	}
				    		        			}
		    		    		        		if (objs.size() > 0) {
		    		    		        			KrnAttribute personAttr = krn.getAttributeByName(contactInfoCls, "person");
		    		    		    				KrnAttribute telephoneAttr = krn.getAttributeByName(contactInfoCls, "telephone");
		    		    		    				KrnAttribute emailAttr = krn.getAttributeByName(contactInfoCls, "email");
		    		    		    				KrnAttribute linkAttr = krn.getAttributeByName(contactInfoCls, "link");
		    		    							KrnAttribute freeTextMessageAttr = krn.getAttributeByName(contactInfoCls, "freeTextMessage");
	    		    		    					JsonArray contacts = new JsonArray();
	    		    		    					StringBuilder text = new StringBuilder();
	    		    		    					for (int i = 0; i < objs.size(); i++) {
	    		    		    						String person = krn.getStringsSingular(objs.get(i).id, personAttr.id, 0, false, false);
	    		    		    						String telephone = krn.getStringsSingular(objs.get(i).id, telephoneAttr.id, 0, false, false);
	    		    		    						String email = krn.getStringsSingular(objs.get(i).id, emailAttr.id, 0, false, false);
	    		    		    						String link = krn.getStringsSingular(objs.get(i).id, linkAttr.id, 0, false, false);
	    		    		    						JsonObject contact = new JsonObject();
	    		    		    						contact.add("person", person);
	    		    		    						contact.add("telephone", telephone);
	    		    		    						contact.add("email", email);
	    		    		    						if (!"".equals(link)) {
	    		    		    							contact.add("link", link);
	    		    		    						}
	    		    		    						contacts.add(contact);
	    		    		    						
	    		    		    						if (withAdditionalInfo) {
		    		    		    						String freeTextMessage = krn.getStringsSingular(objs.get(i).id, freeTextMessageAttr.id, 0, false, false);
		    		    		    						if (freeTextMessage.trim().length() > 0) {
		    		    		    							text.append(freeTextMessage.trim() + "." + (i < (objs.size() - 1) ? " " : ""));
		    		    		    						}
	    		    		    						}
	    		    		    					}
	    		    		    					res.add("contacts", contacts);
													if (text.toString().length() > 0) {
														res.add("additionalInfo", text.toString());
													}
	    		    		    					return res;
		    		    		        		}
			    		        			}
			    		        		}
			    		        	}
			        			}
			        		}
			        	}
		        	}
	        	}
	        }
	        return getContactInfo2(withAdditionalInfo);
    	} catch (KrnException e) {
			log.error(e, e);
    	}
        return res;
    }
    
	private JsonObject getContactInfo2(boolean withAdditionalInfo) {
    	JsonObject res = new JsonObject();
		Session session = null;
		try {
			String dsName = BASE_NAME[0];
			session = SrvUtils.getSession(dsName, "sys", null);
			KrnClass contactInfoCls = session.getClassByName("ContactInfo");
			if (contactInfoCls != null) {
    			KrnAttribute regOrganAttr = session.getAttributeByName(contactInfoCls, "regOrgan");
        		KrnObject[] objs = session.getObjectsByAttribute(contactInfoCls.id, regOrganAttr.id, 0, 2, null, 0);
				if (objs.length > 0) {
					KrnAttribute personAttr = session.getAttributeByName(contactInfoCls, "person");
					KrnAttribute telephoneAttr = session.getAttributeByName(contactInfoCls, "telephone");
					KrnAttribute emailAttr = session.getAttributeByName(contactInfoCls, "email");
    				KrnAttribute linkAttr = session.getAttributeByName(contactInfoCls, "link");
					KrnAttribute freeTextMessageAttr = session.getAttributeByName(contactInfoCls, "freeTextMessage");
					JsonArray contacts = new JsonArray();
					StringBuilder text = new StringBuilder();
					for (int i = 0; i < objs.length; i++) {
						String person = session.getStringsSingular(objs[i].id, personAttr.id, 0, false, false);
						String telephone = session.getStringsSingular(objs[i].id, telephoneAttr.id, 0, false, false);
						String email = session.getStringsSingular(objs[i].id, emailAttr.id, 0, false, false);
						String link = session.getStringsSingular(objs[i].id, linkAttr.id, 0, false, false);
						JsonObject contact = new JsonObject();
						contact.add("person", person);
						contact.add("telephone", telephone);
						contact.add("email", email);
						if (!"".equals(link)) {
							contact.add("link", link);
						}
						contacts.add(contact);
						
						if (withAdditionalInfo) {
							String freeTextMessage = session.getStringsSingular(objs[i].id, freeTextMessageAttr.id, 0, false, false);
							if (freeTextMessage != null && freeTextMessage.trim().length() > 0) {
								text.append(freeTextMessage.trim() + ". ");
							}
						}
					}
					res.add("contacts", contacts);
					if (text.toString().length() > 0) {
						res.add("additionalInfo", text.toString());
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (session != null) {
				session.release();
			}
		}
		return res;
	}
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        request.setCharacterEncoding("UTF-8");
        // Затягиваем в мапу все параметры запроса
        Map<String, String> params = getParameters(request);
        
        if (params.get("loginLogo") != null) {
        		Session session = null;
        		try {
        			String dsName = BASE_NAME[0];
        			session = SrvUtils.getSession(dsName, "sys", null);
        			byte[] img = null;
        	    	long picWidth = 0;
        	    	long picHeight =  0;
        	    	img = session.getLogoPic();
        	    	picWidth = session.getLogoPicWidth();
        	    	picHeight = session.getLogoPicHeight();
        	    	if(img != null && img.length > 0) {
        	    	    BufferedImage resized = Utils.resize(img, (int)picWidth, (int)picHeight);
        	    	    ByteArrayOutputStream os = new ByteArrayOutputStream();
        	    	    ImageIO.write(resized, "png", os);
        	    	    Funcs.writeStream(new ByteArrayInputStream(os.toByteArray()), response.getOutputStream(), Constants.MAX_IMAGE_SIZE);
        	    	} else {
        	    		InputStream ris = WebController.class.getResourceAsStream("/login-logo.png");
        	    		if (ris != null) {
        	    			Funcs.writeStream(ris, response.getOutputStream(), Constants.MAX_IMAGE_SIZE);
        	    			ris.close();
        	    		} else {
        	    			String path = PATH_TO_LOGO_LOGIN != null ? PATH_TO_LOGO_LOGIN : "/jsp/media/img/login-logo.png";
        	    			WebUtils.includeResponse(request, response, path, Constants.MAX_IMAGE_SIZE);
        	    		}
        	    	}
        		} catch (Exception e) {
        			log.error(e, e);
        		} finally {
        			if (session != null) {
        				session.release();
        			}
        		}
	        return;
        }
        
        String cmd = params.get("cmd");
        int responseType = (params.get("json") != null) ? RESPONSE_JSON : (params.get("xml") != null || "getConfigs".equals(cmd)) ? RESPONSE_XML : RESPONSE_HTML;
        
        if ("contactInfo".equals(cmd)) {
			prepareResponse(response, responseType);
			PrintWriter w = response.getWriter();
			JsonObject res = getContactInfo1(request);
			w.println(res.toString());
			return;
        }
        
        if ("isUseECP".equals(cmd)) {
			prepareResponse(response, responseType);
			PrintWriter w = response.getWriter();
			JsonObject res = isUseECP(request);
			w.println(res.toString());
			return;
        }
        
        if ("method".equals(cmd)) {
			prepareResponse(response, responseType);
			PrintWriter w = response.getWriter();
			JsonObject res = executeMethod(request, params);
			w.println(res.toString());
			return;
        }

        if ("execute".equals(cmd)) {
			prepareResponse(response, responseType);
			PrintWriter w = response.getWriter();
			Object res = executeMethodTr(request, params);
			w.println(res.toString());
			return;
        }

        if ("hideItems".equals(cmd)) {
			prepareResponse(response, responseType);
			PrintWriter w = response.getWriter();
			JsonObject res = new JsonObject();
			if (HIDE_ITEMS != null) {
				JsonArray items = new JsonArray();
				for (int i = 0; i < HIDE_ITEMS.length; i++) {
					items.add(HIDE_ITEMS[i].trim());
				}
				res.add("items", items);
			}
			w.println(res.toString());
			return;
        }
        
        try {
            if (request.getParameter("qqfile") != null) {
            	parseQQFiles(request, response);
            } else if (ServletFileUpload.isMultipartContent(request)) {
            	parseFiles(request, response);
            	return;
            }

            if ("getConfigs".equals(cmd)) {
            	
            	prepareResponse(response, responseType);
                PrintWriter w = response.getWriter();

                if (responseType == RESPONSE_JSON) {
                    JsonArray options = new JsonArray();
                    for (int i=0; i<SERVER_NAME.length; i++) {
                        JsonObject option = new JsonObject();
                        option.add("value", i);
                        option.add("text", SERVER_NAME[i]);
                        options.add(option);
                    }
                    JsonObject res = new JsonObject();
                    res.add("options", options);
                    w.println(res.toString());
            	} else {
	                w.println("<r>");
	                for (int i=0; i<SERVER_NAME.length; i++) {
	                	w.println("<option value=\"" + i + "\">" + SERVER_NAME[i] + "</option>");
	                }
	                w.println("</r>");
            	}
                return;
            }
            
            String encodedLoginInfo = params.get("encodedInfo");

            String guid = params.get("guid"); //Funcs.getValidatedParameter(request, "guid");
            if (guid == null && encodedLoginInfo != null) {
            	guid = UUID.randomUUID().toString();
            }
            
            Map<String, Object> hs = WebController.getSession(request, guid);
            if (guid == null) {
        		log.warn("Parameters: " + getParametersString(request));
            }
            
            WebUser user = (WebUser) hs.get("user");
            String name = params.get("name");
            String pd = params.get("passwd");
            String tmp = params.get("configNumber");
            int configNumber = (tmp != null) ? Integer.parseInt(tmp) : 0;
            String dsName = (String) hs.get("dsName");
            String sign = params.get("sign");
            String signedData = params.get("signedData");
            boolean login = params.get("force") != null;
            boolean isUseECP = params.get("isUseECP") != null;

            String newPD = params.get("newPass");
            String confPD = params.get("confirmPass");
            boolean force = "1".equals(params.get("force"));
            boolean sLogin = "1".equals(params.get("sLogin"));

            String lang = params.get("lang") != null ? params.get("lang") : "ru";

            // Генерация случайной строки для подписания апплетом на стороне клиента для последующей аутентификации через ЭЦП
            if ("getSecret".equals(cmd)) { // KALKAN
                String secret = UUID.randomUUID().toString();
                hs.put("secret", secret);
            	if (responseType == RESPONSE_JSON) {
	            	prepareResponse(response, RESPONSE_JSON);
	                PrintWriter w = response.getWriter();
	                w.println(new JsonObject().add("secret", secret).toString());
            	} else {
	            	prepareResponse(response, RESPONSE_XML);
	                PrintWriter w = response.getWriter();
	                w.println("<s>" + secret + "</s>");
            	}
            	return;
            } else if ((user == null || user.getSession() == null) && "getSecret32".equals(cmd)) { // Gamma v3.2
            	prepareResponse(response, RESPONSE_XML);
                PrintWriter w = response.getWriter();
                String secret = UUID.randomUUID().toString();
                hs.put("secret32", secret);
                w.println("<s>" + secret + "</s>");
            	return;
            } else if ("getSecret42".equals(cmd)) { // Gamma v4.2
            	prepareResponse(response, RESPONSE_XML);
                PrintWriter w = response.getWriter();
                String secret = UUID.randomUUID().toString();
                hs.put("secret42", secret);
                w.println("<s>" + secret + "</s>");
            	return;
            }
            
            Long daysLeft = null;
            String errorMsg = null;
            
        	Map<String, String> loginParams = new HashMap<>();
            if (encodedLoginInfo != null) {
            	// Вход по зашифрованной ссылке
            	// Расшифровываем ссылку
            	String loginInfo = new String(Base64.decode(encodedLoginInfo));
            	// Парсим параметры
            	String[] pairs = loginInfo.split(";");
            	for (String pair : pairs) {
            		String[] pars = pair.split("=");
            		loginParams.put(pars[0], (pars.length > 1 ? pars[1] : ""));
            	}
            	// добавляем в мапу параметры незашифрованные
            	for (String param : params.keySet()) {
            		loginParams.put(param, params.get(param));
            	}
            	
            	// проверяем указаны ли класс и функция для логина
    	    	String clsName = loginParams.get("cls");
            	String loginFunc = loginParams.get("loginFunc");
            	
		    	if (clsName != null && loginFunc != null) {
		    		Session session = null;
	        		try {
	        			session = SrvUtils.getSession(BASE_NAME[configNumber], "sys", null);
	        			
    		    		KrnClass cls = null;
    		    		try {
    		    			cls = session.getClassByName(clsName);
    		    		} catch (Exception e) {}
    		    		
    		    		// Запускаем функцию loginFunc, которая должна вернуть имя пользователя
    		    		// параметром функции является мапа с параметрами запроса из другой системы
    		    		
    		    		// #set($params = $ARGS[0])
    		    		// #set($userId = $params.get("userId"))
    		    		// и т.д.
    		    		
    			    	if (cls != null) {
    			            SrvOrLang orlang = new SrvOrLang(session);
    			            
    			            com.cifs.or2.server.Context ctx = new com.cifs.or2.server.Context(new long[0], 0, 0); 
    				        session.setContext(ctx);
    				        
    			            List<Object> args = new ArrayList<>();
    			            args.add(loginParams);
    			            
    			            Map<String, Object> vars = new HashMap<String, Object>();
			                name = (String) orlang.exec(cls, cls, loginFunc, args, new Stack<String>(), vars);
			                loginParams.put("USERNAME", name);
			                errorMsg = (String) vars.get("ERRMSG");
			                force = true;
			                login = true;
    			    	} else {
    			    		log.error("Не найден класс " + clsName);
    			    	}
	        		} catch (Throwable e) {
	        			log.error(e, e);
	        		} finally {
	        			if (session != null) {
	        				session.release();
	        			}
	        		}
		    	}
            }

            // Если пользователь еще не авторизован, но предоставил пароль или подписанную секретную строку
            if (user == null || (name != null && pd != null) || loginParams.get("USERNAME") != null || (hs.get("secret") != null && (sign != null || signedData != null)) || (hs.get("secret42") != null && sign != null)) {
                String remoteIP = getIpAddress(request);
                String remoteHost = request.getRemoteHost();
            	
            	// Попытка авторизации через куки (если ранее был авторизован на портале Вебсферы
                String uid = null;//authorizeWithLtpaToken(request, response);
                /*
                // Авторизация через NTLM
                boolean valid = false;
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                   StringTokenizer st = new StringTokenizer(authHeader);
                   if (st.hasMoreTokens()) {
                      String basic = st.nextToken();

                      // We only handle HTTP Basic authentication

                      if (basic.equalsIgnoreCase("NTLM")) {
                         String credentials = st.nextToken();
                         byte[] msg = kz.iola.util.encoders.Base64.decode(credentials);
                         int off = 0, length, offset;
                         if (msg[8] == 1)
                         {
                           byte z = 0;
                           byte[] msg1 = {(byte)'N', (byte)'T', (byte)'L', (byte)'M', (byte)'S', (byte)'S', (byte)'P', 
                             z,(byte)2, z, z, z, z, z, z, z,(byte)40, z, z, z, 
                             (byte)1, (byte)130, z, z,z, (byte)2, (byte)2,
                             (byte)2, z, z, z, z, z, z, z, z, z, z, z, z};
                           response.setHeader("WWW-Authenticate", "NTLM " + 
                              new sun.misc.BASE64Encoder().encodeBuffer(msg1));
                           response.sendError(response.SC_UNAUTHORIZED);
                           return;
                         }
                         else if (msg[8] == 3)
                         {
                           off = 30;

                           //length = msg[off+17]*256 + msg[off+16];
                           //offset = msg[off+19]*256 + msg[off+18];
                           //String remoteHost = new String(msg, offset, length);

                           length = msg[off+1]*256 + msg[off];
                           offset = msg[off+3]*256 + msg[off+2];
                           String domain = new String(msg, offset, length);

                           length = msg[off+9]*256 + msg[off+8];
                           offset = msg[off+11]*256 + msg[off+10];
                           String username = new String(msg, offset, length);

                           log.info("Username:"+username+"<BR>");
                           //log.info("RemoteHost:"+remoteHost+"<BR>");
                           log.info("Domain:"+domain+"<BR>");
                           valid = true;
                         }
                      }
                   }
                }
                if (params.get("sso") != null) {
	                if (!valid) {
	                    String s = "Basic realm=\"Login Test Servlet Users\"";
	                    response.setHeader("WWW-Authenticate", "NTLM");
	                    response.setStatus(401);
	                    return;
	                 }
                }
*/               
                
                log.info("No previous session for user: '" + name + "'");
                // Авторизация по логину и паролю
                if (name != null && pd != null) {
                    log.info("|USER: " + name + "| Creating new session for user: '" + name + "'");
                    try {
                    	if (isUseECP) {
                    		user = new WebUser(name, pd, newPD, confPD, remoteIP, remoteHost, configNumber, dsName, DOWNTIME, true, signedData);
		                	X509Certificate c = KalkanUtil.getCertificate(signedData);
	                        daysLeft = (c.getNotAfter().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
                    	} else {
                    		user = new WebUser(name, pd, newPD, confPD, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                    	}
                        user.login(force, sLogin, null);
                        checkUserMandatoryFields(user, true);
                        checkFlowExists(request, user);
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if ("1".equals(params.get("ntlm"))) {
                	
                	String userDN = (String)hs.get("remoteUserName");
                	if (userDN == null)
                		userDN = (request.getRemoteUser() != null) ? request.getRemoteUser() : "null";
                	
                	log.info("|USER: " + userDN + "| Creating new session for userDN: '" + userDN + "'");
                    
                	try {
                        user = new WebUser(userDN, remoteIP, remoteHost, Kernel.LOGIN_LDAP, configNumber, DOWNTIME);
                        user.login(force, sLogin, null);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);
                    } catch (Exception ex) {
                    	throwLoginException(user, userDN, ex);
                    }
                } else if (hs.get("secret") != null && sign != null) {
                	String sct = (String)hs.remove("secret");
                	String cert = params.get("cert");

                	String userDN = null;
                	String fullDN = null;
                	
                	try {
	        			X509Certificate c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(cert));
	        	        fullDN = c.getSubjectDN().getName();
	        			int beg = fullDN.indexOf("IIN");
	        			if (beg > -1) {
	        				int end = fullDN.indexOf(",", beg + 3);
	        				if (end == -1) end = fullDN.length();
	        				userDN = fullDN.substring(beg + 3, end);
	        			} else {
	        				userDN = fullDN;
	        			}

                        CheckSignResult res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, sct, sign, c, true);
                        if (!res.isOK()) {
                            throw new LoginException(res.getErrorMessage(true));
                        }
                        
                        daysLeft = (c.getNotAfter().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
                	} catch (LoginException le) {
                        log.error(le, le);
                        throw le;
                	} catch (Exception e) {
                        log.error(e, e);
                        throw new LoginException("Ошибка при проверке подписи!");
                	}

                    log.info("|USER: " + fullDN + "| Creating new session for fullDN: '" + fullDN + "'");
                    try {
                        user = new WebUser(userDN, remoteIP, remoteHost, Kernel.LOGIN_DN, configNumber, DOWNTIME);
                        user.login(force, sLogin, null);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        user.getSession().setProfile(params.get("profile"));
                        user.getSession().setProfileConteiner(params.get("conteiner"));
                        
                        if (user.getSession().getKernel().getUser().isInstantECP())
                        	user.getSession().setProfilePassword(params.get("passwd"));
                        
                        user.getSession().setCert(cert);
                        user.getSession().setCertType("KALKAN");

                        log.info("profile = " + params.get("profile"));
                    } catch (Exception ex) {
                    	throwLoginException(user, fullDN, ex);
                    }
                } else if ((hs.get("secret") != null || params.get("secret") != null) && signedData != null) {
                	String userDN = null;
                	String fullDN = null;
                	
                	X509Certificate c = null;
                	
                	try {
                    	boolean isUCGO = "1".equals(params.get("isUCGO"));
                    	if (isUCGO) {
                    		c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(params.get("cert")));
                    	} else {
                    		c = KalkanUtil.getCertificate(signedData);
                    	}
	        	        fullDN = c.getSubjectDN().getName();
	        			int beg = fullDN.indexOf("IIN");
	        			if (beg > -1) {
	        				int end = fullDN.indexOf(",", beg + 3);
	        				if (end == -1) end = fullDN.length();
	        				userDN = fullDN.substring(beg + 3, end);
	        			} else {
	        				userDN = fullDN;
	        			}
	        			
	                	CheckSignResult res;
	                	if (isUCGO) {
	                		res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, ((String) hs.get("secret")).getBytes("UTF-8"), signedData.getBytes("UTF-8"), c);
	                	} else {
	                		res = KalkanUtil.checkXML(signedData, true);
	                	}
	                    if (!res.isOK()) {
	                        throw new LoginException(res.getErrorMessage(true));
	                    }
                        daysLeft = (c.getNotAfter().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
                	} catch (LoginException le) {
                        log.error(le, le);
                        throw le;
                	} catch (Exception e) {
                        log.error(e, e);
                        throw new LoginException("Ошибка при проверке подписи!");
                	}           	
                	
                    log.info("|USER: " + fullDN + "| Creating new session for fullDN2: '" + fullDN + "'");
                    try {
                        user = new WebUser(userDN, remoteIP, remoteHost, Kernel.LOGIN_DN, configNumber, DOWNTIME);
                        user.login(force, sLogin, null);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        user.getSession().setProfile(params.get("profile"));
                        user.getSession().setProfileConteiner(params.get("conteiner"));
                        if (user.getSession().getKernel().getUser().isInstantECP())
                        	user.getSession().setProfilePassword(params.get("passwd"));
                        user.getSession().setCert(new String(new kz.gov.pki.kalkan.util.encoders.Base64().encode(c.getEncoded())));
                        user.getSession().setCertType("KALKAN");

                        log.info("profile = " + params.get("profile"));
                    } catch (Exception ex) {
                    	throwLoginException(user, fullDN, ex);
                    }
                } else if (hs.get("secret32") != null && sign != null) {
                	String sct = (String)hs.get("secret32");
                	String cert = params.get("cert");

                	try {
		                Signer32 signer = new Signer32();
		                signer.init();

		                CertificateFactory cf = CertificateFactory.getInstance("X.509");
		                InputStream certSteam = new ByteArrayInputStream(Base64.decode(cert));
		                X509Certificate c = (X509Certificate)cf.generateCertificate(certSteam);
		                certSteam.close();

		                byte[] cb = new TBSCertificateStructure((ASN1Sequence) ASN1Sequence.fromByteArray(c.getTBSCertificate()))
		                        .getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

		                log.info("=== cb = " + new String(Base64.encode(cb)));
		                log.info("=== ss = " + sign);

		                boolean ok = signer.verifyString(sct.getBytes(), cb, Base64.decode(sign)) == 1;
		                log.info("ok = " + ok);

		                if (!ok)
		                    throw new LoginException("Ошибка авторизации! Используйте другой ключевой контейнер.");

/*	        	        int r = signer.verifyCertificate(IolaProvider.PROVIDER_NAME, c, null);
	        	        
	        	        switch (r) {
	        	        	case CryptoUtil.CERT_EXPIRED:
	        	        		throw new LoginException("Сертификат просрочен!", response);
	        	        	case CryptoUtil.CERT_NOT_YET_VALID:
	        	        		throw new LoginException("Сертификат еще не действует!", response);
	        	        	case CryptoUtil.CERT_SIGN_ERROR:
	        	        		throw new LoginException("Сертификат не подписан НУЦ!", response);
	        	        	case CryptoUtil.CERT_CRL_NO_ACCESS:
	        	        		throw new LoginException("Не доступен список отзыва сертификатов!", response);
	        	        	case CryptoUtil.CERT_REVOKED:
	        	        		throw new LoginException("Сертификат отозван!", response);
	        	        	case CryptoUtil.CERT_NOT_FOR_SIGN:
	        	        		throw new LoginException("Сертификат не может быть использован для подписи!", response);
	        	        }
*/	        	        
                	} catch (Exception e) {
                        log.error(e, e);
                        throw new LoginException("Ошибка при проверке подписи!");
                	}

                    log.info("|USER: " + name + "| Creating new session for signer32: '" + name + "'");
                    try {
                        user = new WebUser(name, null, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.login(force, sLogin, null);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        user.getSession().setProfile(params.get("profile"));
                        log.info("profile = " + params.get("profile"));
                        log.info("cert = " + cert);
                        user.getSession().setCert(cert);
                        user.getSession().setCertType("T32");
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if (hs.get("secret42") != null && sign != null) {
                	String sct = (String)hs.get("secret42");
                	X509Certificate cert = null;
                	String fullDN = null;
	                TumarCSP tumar = new TumarCSP();
                	try {
		                tumar.init();

		                log.info("=== ss = " + sign);

		                boolean ok = tumar.verifyPKCS7(Base64.decode(sct), Base64.decode(sign));
		                log.info("ok = " + ok);
		                

		                cert = tumar.getCertificateFromPKCS7(Base64.decode(sign));
		                log.info("=== cert = " + cert);
		                
	        	        fullDN = cert.getSubjectDN().getName();

		                if (!ok)
		                    throw new LoginException("Ошибка авторизации! Используйте другой ключевой контейнер.");

/*	        	        int r = signer.verifyCertificate(IolaProvider.PROVIDER_NAME, c, null);
	        	        
	        	        switch (r) {
	        	        	case CryptoUtil.CERT_EXPIRED:
	        	        		throw new LoginException("Сертификат просрочен!", response);
	        	        	case CryptoUtil.CERT_NOT_YET_VALID:
	        	        		throw new LoginException("Сертификат еще не действует!", response);
	        	        	case CryptoUtil.CERT_SIGN_ERROR:
	        	        		throw new LoginException("Сертификат не подписан НУЦ!", response);
	        	        	case CryptoUtil.CERT_CRL_NO_ACCESS:
	        	        		throw new LoginException("Не доступен список отзыва сертификатов!", response);
	        	        	case CryptoUtil.CERT_REVOKED:
	        	        		throw new LoginException("Сертификат отозван!", response);
	        	        	case CryptoUtil.CERT_NOT_FOR_SIGN:
	        	        		throw new LoginException("Сертификат не может быть использован для подписи!", response);
	        	        }
*/	        	        
                	} catch (Exception e) {
                        log.error(e, e);
                        throw new LoginException("Ошибка при проверке подписи!");
                	}

                    log.info("|USER: " + name + "| Creating new session for signer42: '" + name + "'");
                    try {
                        user = new WebUser(name, null, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.login(true, sLogin, null);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        user.getSession().setProfile(params.get("profile"));
                        log.info("profile = " + params.get("profile"));
                        log.info("cert = " + cert);
                        user.getSession().setCert(new String(Base64.encode(cert.getEncoded())));
                        user.getSession().setCertType("T42");
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if (name != null && sign != null) {

                    log.info("|USER: " + name + "| Creating new session for iola: '" + name + "'");
                    try {
                        user = new WebUser(name, null, remoteIP, remoteHost, sign, configNumber, DOWNTIME);
                        user.login(force, sLogin, sign);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        user.getSession().setProfile(params.get("profile"));
                        user.getSession().setCert(params.get("cert"));
                        user.getSession().setCertType("IOLA");
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if ("mu_pub".equals(name)) {
                    String pdUnicode = "123123";

                    log.info("|USER: " + name + "| Creating new session for mu_pub");
                    try {
                        user = new WebUser(name, pdUnicode, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.login(force, sLogin, null);
                        user.getSession().setIsForPublicUser(true);
                        user.getSession().setOnlyArchive(true);
                        checkUserMandatoryFields(user, false);
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if ("itrud_pub".equals(name)) {
                    String pdUnicode = "123";

                    log.info("|USER: " + name + "| Creating new session for itrud_pub");
                    try {
                        user = new WebUser(name, pdUnicode, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.login(force, sLogin, null);
                        user.getSession().setIsForPublicUser(true);
                        checkUserMandatoryFields(user, false);
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if (name != null && pd == null && login) {
                    log.info("|USER: " + name + "| Creating new session for no pd user: '" + name + "'");
                    try {
                        user = new WebUser(name, null, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        params.put("trg", "top");
                        params.put("bp", BACK_PAGE);
                        user.login(force, sLogin, null);
                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                } else if (uid != null) {
                    log.info("|USER: " + uid + "| Creating new session for uid: '" + uid + "'");
                    try {
                        user = new WebUser(uid, null, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.ltpaLogin();
                        params.put("trg", "top");
                        params.put("bp", BACK_PAGE);
                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);
                    } catch (Exception ex) {
                    	throwLoginException(user, uid, ex);
                    }
                } else if (errorMsg != null) {
                    log.error("|IP: " + remoteIP + "| errorMsg");
                    throw new LoginException(errorMsg);
                } else {
                    log.error("|IP: " + remoteIP + "| Session already released!");
                    throw new LoginException("Пользователь неизвестен. Необходимо войти в систему.");
                }

                // После логина устанавливаем переменную сессии
                log.info("Saving new user to httpSession");
                hs = changeSessionIdentifier(request, guid);
                
                hs.put("user", user);
                user.valueBound(hs);
                request.getSession(true).setAttribute("guid", guid);
                
                // Если внешняя ссылка, то перенаправляем на index.jsp
                if (encodedLoginInfo != null) {
                	user.setLoginParams(loginParams);
                	if (responseType == RESPONSE_HTML) {
                		request.getRequestDispatcher("/jsp/index.jsp?guid=" + guid).forward(request, response);
                		return;
                	}
                }

                /*
                // Устанавливаем связь с XMPP-сервером
                String xmppHost = getServletConfig().getInitParameter("xmpp.bosh.host");
                if (xmppHost != null) {
                    String str = getServletConfig().getInitParameter("xmpp.bosh.port");
                    int xmppPort = str != null ? Integer.parseInt(str) : 5222;
	                log.info("Trying XMPP server. Host:" + xmppHost + " Port:" + xmppPort);
	                /*
	                XMPPPrebind xmppPrebind = new XMPPPrebind(xmppHost, "localhost.localdomain", "/http-bind/", "" + xmppPort, "ekyzmet", false, true);
	                xmppPrebind.connect(user.getName(), user.getPassword());
	                xmppPrebind.auth();
	                SessionInfo sessionInfo = xmppPrebind.getSessionInfo();
	                log.info("Connected JID:" + sessionInfo.getJid() + " SID:" + sessionInfo.getSid() + " RID:" + sessionInfo.getRid());
	                hs.setAttribute("xmpp.jid", sessionInfo.getJid());
	                hs.setAttribute("xmpp.sid", sessionInfo.getSid());
	                hs.setAttribute("xmpp.rid", sessionInfo.getRid());
	          
	                BOSHConfiguration boshConfig = new BOSHConfiguration(false, xmppHost, xmppPort, "/http-bind/", "localhost.localdomain");
	                XMPPBOSHConnection boshConn = new XMPPBOSHConnection(boshConfig);
	                boshConn.connect();
	                boshConn.login(user.getName(), user.getPassword());
	                log.info("Connected JID:" + boshConn.getUser() + " SID:" + boshConn.getSessionID() + " RID:" + boshConn.getRequestID());
	                hs.setAttribute("xmpp.jid", "sys_admin@localhost.localdomain/" + boshConn.getSessionID());
	                hs.setAttribute("xmpp.sid", boshConn.getSessionID());
	                hs.setAttribute("xmpp.rid", "" + boshConn.getRequestID());

                }
            */
                HttpSession chatSession = request.getSession(true);
                chatSession.setAttribute("xmpp.jid", user.getIin() + "@localhost.localdomain");
                chatSession.setAttribute("xmpp.sid", user.getPassword());
                chatSession.setAttribute("xmpp.rid", "0");
            } else {
                String uid = null; //authorizeWithLtpaToken(request, response);
                String remoteIP = getIpAddress(request);
                String remoteHost = request.getRemoteHost();
            	
            	// Релогин под другим именем, если ранее заходил под другим
            	if (name != null && !name.equals(user.getName()) && "top".equals(params.get("trg"))) {
            		// Удаляем предыдущий логин
                    log.info("Deleting old user from httpSession");
                    hs = changeSessionIdentifier(request);
                    
                    if (pd != null) {
                        log.info("|USER: " + name + "| Creating new session for user: '" + name + "'");
                        try {
                            user = new WebUser(name, pd, newPD, confPD, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                            user.login(force, sLogin, null);

                            checkUserMandatoryFields(user, true);
                            checkFlowExists(request, user);

                            hs.put("user", user);
                            user.valueBound(hs);
                            request.getSession(true).setAttribute("guid", guid);
                        } catch (Exception ex) {
                        	throwLoginException(user, name, ex);
                        }
                    } else if ("itrud_pub".equals(name)) {
                        String pdUnicode = "123";

                        log.info("|USER: " + name + "| Creating new session for user: '" + name + "'");
                        try {
                            user = new WebUser(name, pdUnicode, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                            user.login(force, sLogin, null);
                            user.getSession().setIsForPublicUser(true);

                            checkUserMandatoryFields(user, false);

                            hs.put("user", user);
                            user.valueBound(hs);
                            request.getSession(true).setAttribute("guid", guid);
                        } catch (Exception ex) {
                        	throwLoginException(user, name, ex);
                        }
                    }
                } else if (name == null && uid != null && uid.equals(user.getName()) && params.size() == 0) {
                    params.put("trg", "top");
                    params.put("bp", BACK_PAGE);
                } else if (name == null && uid != null && !uid.equals(user.getName())) {
                    log.info("Deleting old user from httpSession (uid)");
                    hs = changeSessionIdentifier(request);
                    log.info("|USER: " + uid + "| Creating new session for uid: '" + uid + "'");
                    try {
                        user = new WebUser(uid, null, null, null, remoteIP, remoteHost, configNumber, dsName, DOWNTIME);
                        user.ltpaLogin();
                        params.put("trg", "top");
                        params.put("bp", BACK_PAGE);

                        checkUserMandatoryFields(user, false);
                        checkFlowExists(request, user);

                        hs.put("user", user);
                        user.valueBound(hs);
                        request.getSession(true).setAttribute("guid", guid);
                    } catch (Exception ex) {
                    	throwLoginException(user, uid, ex);
                    }
                }
            }

            if (user.getSession()!=null && (user.getSession().browserType == null || user.getSession().browserType.isEmpty()) && params.get("browser") != null) {
                String[] cf = params.get("browser").split(";");
                user.getSession().browserType = cf[0];
                user.getSession().browserVersion = cf[1];
                user.getSession().browserOS = cf[2];
                if (cf.length > 3 && ("true".equals(cf[3]) || "1".equals(cf[3]))) 
                	user.getSession().isMobile = true;
                log.info("Подключение тонкого клиента. Браузер: "+cf[0]+" "+cf[1]+"; ОС: "+cf[2] + (cf.length > 3 ? ("; Mobile: " + cf[3]) : ""));
                user.getSession().updateBrowser(cf[0],cf[2]);
                if (WebController.DEBUG) {
                    UpdateContent.update();
                }
            }
            
            if ("ext".equals(cmd)) { // Выход
                if (hs.get("user") != null) {
                	log.info("Exiting session after user's request: " + user.getName());
        			user.setWaitingToUnbound(true);
                    WebController.releaseHttpSession(request, user.getGUID(), true);
                }
                if (responseType == RESPONSE_XML) {
                	prepareResponse(response, RESPONSE_XML);
	                PrintWriter w = response.getWriter();
	                w.println("<r></r>");
                } else {
                	prepareResponse(response, RESPONSE_JSON);
	                PrintWriter w = response.getWriter();
	                w.println(new JsonObject().add("result", "success"));
                }
            }
            else if (user != null) {
                WebSession s = user.getSession();
                if (s == null && ("mu_pub".equals(user.getName()) || "itrud_pub".equals(user.getName()))) {
                    try {
                        user.login(force, sLogin, null);
                        s = user.getSession();
                        s.setIsForPublicUser(true);
                    } catch (Exception ex) {
                    	throwLoginException(user, name, ex);
                    }
                }
                
                if ("rep".equals(cmd) || "opf".equals(cmd)) {
                    s.openFile(params, response, request);
                } else if("loadNoteSound".equals(cmd)) {
                	byte[] noteSound = user.getSession().getNoteSound();
                	OutputStream os = response.getOutputStream();
                	for(byte b: noteSound) {
                		os.write(b);
                	}
                	if(os != null)
                		os.close();
                } else {
                    String toPage = params.get("toPage");
                    if (toPage == null) {
                        if (s == null || s.getKernel() == null || !s.getKernel().isAlive()) {
                            throw new LoginException("Связь с сервером потеряна. Необходимо заново войти в систему.");
                        }
                        String res = s.process(params, request, response, daysLeft);
                        if (res != null) {
                        	prepareResponse(response, responseType);
                        	try {
                        		PrintWriter w = response.getWriter();
                        		w.println(res);
                        	} catch (IllegalStateException e) {
                        		log.error(e, e);
                        	}
                        }
                    } else {
                    	prepareResponse(response, RESPONSE_HTML);
                    	WebUtils.includeResponse(request, response, toPage, Constants.MAX_DOC_SIZE);
                    }
                }
            } else {
                log.error("|IP: " + getIpAddress(request) + "| Session already released!");
                throw new LoginException("Пользователь неизвестен. Необходимо войти в систему.");
            }
        } catch (LoginException e) {
            log.info("Connection already closed! Params: " + params);
            //changeSessionIdentifier(request);
            e.makeResponse(params, response);
        } catch (Throwable e) {
            log.error(params);
            log.error(e, e);
            new LoginException("Пользователь неизвестен. Необходимо войти в систему.").makeResponse(params, response);
        } finally {
        	SecurityContextHolder.setKernel(null);
        	SecurityContextHolder.setFrame(null);
        	SecurityContextHolder.setLog(null);
        }
    }

	private String getParametersString(HttpServletRequest request) {
        StringBuilder res = new StringBuilder();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = Funcs.normalizeInput(names.nextElement());
            String value = Funcs.normalizeInput(request.getParameter(name));
            res.append(name).append("=").append(value).append(";");
        }
        return res.toString();
    }

	private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				byte[] reqBytes = Funcs.readStream(request.getInputStream(), Constants.MAX_DOC_SIZE);
				String data = new String(reqBytes);
				
				String[] pairs = data.split("&");
				data = null;
				
				for (String pair : pairs) {
					String[] keyVal = pair.split("=");
					pair = null;
					if (keyVal.length == 2)
						res.put(URLDecoder.decode(keyVal[0], "UTF-8"), URLDecoder.decode(keyVal[1], "UTF-8"));
					else if (keyVal.length == 1)
						res.put(URLDecoder.decode(keyVal[0], "UTF-8"), "");
						
				}
				pairs = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = Funcs.normalizeInput(names.nextElement());
            String value = Funcs.normalizeInput(request.getParameter(name));
            res.put(name, value);
        }
        return res;
    }

	private Map<String, String> getParametersOld(HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
	    Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = Funcs.normalizeInput(names.nextElement());
            String value = Funcs.normalizeInput(request.getParameter(name));
            res.put(name, value);
        }
        return res;
    }

/*    // Авторизация по LtpaToken, хранящейся в куки браузера, который сформировался при логине в WebSphere 
    protected String authorizeWithLtpaToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieName = "LtpaToken";
        String sessionToken = "";
        String webSphereKey = "5p/P5XkNHhwHfRotT8eNYyrf1FgT+JfhLN3iaKgbA0c="; // you can get this from your Websphere configuration
        String webSpherePass = "ltpa"; // you can also get this from your Websphere cofiguration

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }
    private static byte[] getSK(String shared3DES, String pd) throws Exception {
        MessageDigest md = MessageDigest.getInstance(LtpaLoginModule.WEBSPHERE_DA);
        md.update(pd.getBytes());
        byte[] hash3DES = new byte[LtpaLoginModule.WEBSPHERE_DL];
        System.arraycopy(md.digest(), 0, hash3DES, 0, LtpaLoginModule.WEBSPHERE_DL - 4);
        Arrays.fill(hash3DES, LtpaLoginModule.WEBSPHERE_DL - 4, LtpaLoginModule.WEBSPHERE_DL, (byte) 0);
        // decrypt the real key and return it
        return decrypt(Base64.decode(shared3DES), hash3DES);
    }

    public static byte[] decryptLtpaToken(String encryptedLtpaToken, byte[] k) throws Exception {
        final byte[] ltpaByteArray = Base64.decode(encryptedLtpaToken);
        return decrypt(ltpaByteArray, k);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] k) throws Exception {
        final Cipher cipher = Cipher.getInstance(LtpaLoginModule.WEBSPHERE_CA);
        final KeySpec ks = new DESedeKeySpec(k);
        final Key sk = SecretKeyFactory.getInstance(LtpaLoginModule.WEBSPHERE_SKF).generateSecret(ks);

        cipher.init(Cipher.DECRYPT_MODE, sk);
        return cipher.doFinal(ciphertext);
    }
*/

	public static String codesToUnicode(String str) {
		try {
			ByteArrayInputStream fis = new ByteArrayInputStream(str.getBytes());
			int b = -1;
			
			StringBuffer r = new StringBuffer();
			StringBuffer res = new StringBuffer();
			int t = 0;
			while ((b = fis.read()) != -1) {
				if (b == '\\') {
					t = -100;
				} else if (b == 'u' && t == -100) {
					t = 4;
				} else if (t > 0) {
					r.append((char) b);
					t--;
					if (t == 0) {
						res.append((char) Integer.parseInt(r.toString(), 16));
						r = new StringBuffer();
					}
				} else {
					res.append((char) b);
				}
				
			}
			fis.close();
			
			return res.toString();
		} catch (Exception e) {
            log.error(e, e);
		}      
		return str;
	}

    public static SessionOpsOperations lookup(boolean remote) throws KrnException {
    	try {
    		if ("JBossAS7".equals(SERVER_TYPE)) {
    			
    			final Hashtable<String, Object> props = new Hashtable<String, Object>();
                // setup the ejb: namespace URL factory
                props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
                props.put("jboss.naming.client.ejb.context", true);
	    		props.put(Context.PROVIDER_URL, "remote://" + SERVER_HOST + ":" + SERVER_PORT);

	    		//props.put(Context.SECURITY_PRINCIPAL, "user1");
	    		//props.put(Context.SECURITY_CREDENTIALS, "123456");
	    		props.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

	    		Context ic = new InitialContext(props);
				Object obj = remote 
						? ic.lookup("ejb:Or3EAR/Or3EJB//SessionOps!kz.tamur.or3ee.server.session.SessionOpsRemote")
						: ic.lookup("Or3EAR/Or3EJB/SessionOps!kz.tamur.or3ee.server.session.SessionOpsLocal");
		    	ic.close();
				return (SessionOpsOperations)obj;
    		
            } else if ("Weblogic".equals(SERVER_TYPE)) {
        		if (remote) {
        			Hashtable<String, String> env = new Hashtable<String, String>();
        	
        			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        			env.put(Context.PROVIDER_URL, "t3://" + SERVER_HOST + ":" + SERVER_PORT);
        	
        			InitialContext ctx = new InitialContext(env);
        			Object obj = ctx.lookup("SessionOps#kz.tamur.or3ee.server.session.SessionOpsRemote");
        			ctx.close();
        			return (SessionOpsOperations) obj;
        		} else {
        			InitialContext ctx = new InitialContext();
        			Object obj = ctx.lookup("SessionOps#kz.tamur.or3ee.server.session.SessionOpsLocal");
        			ctx.close();
        			return (SessionOpsOperations) obj;
        		}
    		} else if ("JBossServer".equals(SERVER_TYPE)) {
        		Properties props = new Properties();
                props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
                props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
                props.put("java.naming.provider.url", SERVER_HOST + ":" + SERVER_PORT);

        		Context ic = new InitialContext(props);
    			Object obj = remote 
    					? ic.lookup("Or3EAR/SessionOps/remote")
    					: ic.lookup("Or3EAR/SessionOps/local");
    	    	ic.close();
    			return (SessionOpsOperations)obj;
    		}
    	} catch (NamingException e) {
            log.error(e, e);
            String msg = "Сервер не доступен";
    		throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, msg);
    	}
		throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, "Не определен тип сервера");
    }
    
    /**
     * Получить элемент массива.
     * Обрабатывается ситуация когда индекс запрашиваемого элемента выходит за границу диапазона индексов массива и если массив пустой
     * 
     * @param array
     *            массив, чей элемент необходимо получить
     * @param index
     *            индекс требуемого элемента
     * @return элемент массива, если массив пустой то <code>null</code>, если индекс вне диапазона, то последний элемент
     */
    private String getElement(String[] array, int index) {
        if (array == null) {
            return null;
        } else {
            int lE = array.length - 1;
            return lE == -1 ? null : (index > lE || index < 0) ? array[lE] : array[index];
        }
    }

    /**
	 * Получение реального IP рабочей станции при использовании
	 * проксирующего сервлета WLS
	 * @param req
	 * @return
	 */
	private String getIpAddress(HttpServletRequest req) {
		String ip = req.getHeader("wl-proxy-client-ip");
		if (ip == null) {
			ip = req.getHeader("x-real-ip");
			if (ip == null)
				ip = req.getHeader("x-forwarded-for");
		}
		return ip == null ? req.getRemoteAddr() : ip;
	}

	
	public static Map<String, Object> changeSessionIdentifier(HttpServletRequest request) {
		String guid = Funcs.getValidatedParameter(request, "guid");
		return changeSessionIdentifier(request, guid);
	}
	
	public static Map<String, Object> changeSessionIdentifier(HttpServletRequest request, String guid) {
		// get the current session
		Map<String, Object> oldSession = WebController.getSession(request, guid);
		WebUser user = (WebUser) oldSession.get("user");
		if (user != null) {
			user.setWaitingToUnbound(true);
		}
		releaseHttpSession(request, guid, false);
		
		Map<String, Object> newSession = WebController.getSession(request, guid);
		return newSession;
	}
	
	public static String risType() {
		String risType = null;
		Session session = null;
		try {
			String dsName = BASE_NAME[0];
			session = SrvUtils.getSession(dsName, "sys", null);
			byte[] img = null;
	    	img = session.getLogoPic();
	    	if(img != null && img.length > 0) {
	    		risType = "configPic";
	    	} else {
	    		risType = "notConfigPic";
	    	}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (session != null) {
				session.release();
			}
		}
		return risType;
	}

    public static Map<String, Object> getSession(String guid) {
    	return getSession(null, guid);
    }
    
    public static Map<String, Object> getSession(HttpServletRequest req, String guid) {
    	if (Database.isRnDB) {
    		HttpSession hts = req.getSession(true);
            Map<String, Object> hs = (Map<String, Object>) hts.getAttribute("vars");
            if (hs == null) {
            	hs = new HashMap<>();
				hs.put("GUID", guid);
				hts.setAttribute("vars", hs);
            }
            	
            return hs;
    	} else {
	    	if (guid == null) {
				log.warn("Requesting httpSession for null GUID");
	    		return new HashMap<>();
	    	}
			synchronized (httpSessions) {
				Map<String, Object> hs = httpSessions.get(guid);
				if (hs == null) {
					hs = new HashMap<>();
					hs.put("GUID", guid);
					httpSessions.put(guid, hs);
					log.info("Created httpSession for GUID: " + guid);
			    	log.info("Active web sessions: " + httpSessions.size());
				}
				return hs;
			}
    	}
	}

    public static boolean releaseHttpSession(String guid) {
    	return releaseHttpSession(null, guid, true);
    }

    public static boolean releaseHttpSession(HttpServletRequest req, String guid) {
    	return releaseHttpSession(req, guid, false);
    }

    public static boolean releaseHttpSession(HttpServletRequest req, String guid, boolean invalidate) {

    	if (Database.isRnDB) {
    		if (req != null) {
	    		HttpSession hts = req.getSession(true);
	    		hts.removeAttribute("guid");
	            Map<String, Object> hs = (Map<String, Object>) hts.getAttribute("vars");
				if (hs != null) {
					WebUser user = (WebUser) hs.get("user");
					hs.clear();
					hs = null;
					
					if (user != null) {
						user.valueUnbound(false);
					}
	
					log.info("Released httpSession for GUID: " + guid);
			    	log.info("Active web sessions: " + httpSessions.size());
				}
				if (invalidate)
					hts.invalidate();
    		}
			return true;
    	} else {
	    	if (guid == null) {
				log.error("Releasing httpSession for null GUID");
	    		Thread.dumpStack();
	    		return false;
	    	}
			synchronized (httpSessions) {
				Map<String, Object> hs = httpSessions.remove(guid);
				if (hs != null) {
					WebUser user = (WebUser) hs.get("user");
					hs.clear();
					hs = null;
					
					if (user != null) {
						user.valueUnbound(false);
					}
	
					log.info("Released httpSession for GUID: " + guid);
			    	log.info("Active web sessions: " + httpSessions.size());
				}
				try {
					if (invalidate && req != null) {
						HttpSession hts = req.getSession(false);
						if (hts != null)
							hts.invalidate();
					}
				} catch (Exception e) {
					log.error(e, e);
				}
				return true;
			}
    	}
	}
    
    public static boolean isUserLoggedIn(HttpServletRequest req) {
    	HttpSession session = req.getSession();
    	String guid = (String)session.getAttribute("guid");
    	
    	if (guid != null) {
    		Map<String, Object> hs = httpSessions.get(guid);
    		if (hs != null && hs.get("user") != null)
    			return true;
    		else
    			session.removeAttribute("guid");
    	}
    	
    	return false;
    }

	@Override
	public void destroy() {
		isDestroying = true;
		log.info("Destroying WebController Servlet");
		try {
			synchronized (httpSessions) {
				for (String guid : httpSessions.keySet()) {
					Map<String, Object> hs = httpSessions.get(guid);
					if (hs != null) {
						WebUser user = (WebUser) hs.get("user");
						hs.clear();
						hs = null;
						
						if (user != null) {
							user.setWaitingToUnbound(true);
							user.valueUnbound(false);
						}
		
						log.info("Released httpSession for GUID: " + guid);
				    	log.info("Active web sessions: " + httpSessions.size());
					}
				}
				httpSessions.clear();
			}
		} catch (Throwable e) {
			log.error(e, e);
		}
		log.info("Destroying WebController Servlet DONE!");
		
		super.destroy();
	}
	
	public static boolean isDestroying() {
		return isDestroying;
	}
}
