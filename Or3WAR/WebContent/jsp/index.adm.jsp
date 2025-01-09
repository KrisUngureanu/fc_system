<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="kz.tamur.web.common.WebSession"%>
<%@ page import="kz.tamur.web.controller.WebController"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="kz.tamur.util.AES"%>
<%@ page import="kz.tamur.web.common.WebUser"%>
<%@ page import="kz.tamur.test.StartupServlet"%>
<%@ page import="kz.gov.pki.kalkan.util.encoders.Base64"%>
<%@ page import="kz.tamur.util.Funcs"%>
<%@ page import="kz.tamur.web.common.WebUtils"%>
<%@ page import="kz.tamur.comps.Constants"%>

<%
	// Для входа в систему с кирилическими именами и паролями	
	request.setCharacterEncoding("UTF-8");

	// Идентификатор сессии - не используем HttpSession (из-за прокси)
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(guid);

	/* Путь к логотипу для главного окна прописывается в файле web.xml
	 * вместе с контекстом веб-приложения
	 * 
	 * <init-param>
	 *    	<param-name>logoPath</param-name>
   	 * 		<param-value>/ekyzmet-ui/jsp/media/img/logo-0.png</param-value>
  	 * </init-param>
	*/

    // Разбирает закодированный в Base64 хэш для перехода сразу к нужной страничке
    String path = request.getParameter("hash");
    String startHash = null;
    if (path != null) {
        path = new String(Base64.decode(AES.toBase64(path)));
        int beg = path.indexOf('#');
        if (beg > -1)
            startHash = Funcs.sanitizeHashUrl(path.substring(beg));
    }
    // Возвращается на эту страницу при нажатии "Выход"
    String backPage = request.getParameter("bp");

    // Для перехода с ИПГО без повторной авторизации
    String remoteAuth = request.getParameter("remauth");
    if (remoteAuth != null) {
        byte[] b1 = Base64.decode(AES.toBase64(remoteAuth));
        byte[] b2 = new AES(StartupServlet.REMOTE_LOGIN_SK,
        		StartupServlet.REMOTE_LOGIN_KGA, StartupServlet.REMOTE_LOGIN_MDA, 
        		StartupServlet.REMOTE_LOGIN_SALT, StartupServlet.REMOTE_LOGIN_MDL).decrypt3Times(b1);

        if (b2 != null) {
            String name = new String(b2);
            try {
                String remoteIP = getIpAddress(request);
                String remoteHost = request.getRemoteHost();

                WebUser user = new WebUser(name, null, null, null, remoteIP, remoteHost, 0, null, -1);
                user.login(true, false, null);

                s = WebController.changeSessionIdentifier(request);
                s.put("user", user);
                user.valueBound(s);

            } catch (Exception ex) {
            	WebController.jspLog.error(ex, ex);
                WebController.jspLog.warn("Пользователь name = " + name + " не найден!");
            }
        } else {
        	WebController.jspLog.warn("Автологин remauth = " + remoteAuth + " просрочен!");
        }
    }

    // Для авторизации напрямую без пароля (однако имя пользователя в этом случае закодировано)
    String auth = request.getParameter("auth");
    if (auth != null) {
        byte[] b1 = Base64.decode(AES.toBase64(auth));
        byte[] b2 = new AES(StartupServlet.REMOTE_LOGIN_SK,
        		StartupServlet.REMOTE_LOGIN_KGA, StartupServlet.REMOTE_LOGIN_MDA, 
        		StartupServlet.REMOTE_LOGIN_SALT, StartupServlet.REMOTE_LOGIN_MDL).decrypt(b1);
        if (b2 != null) {
            String name = new String(b2);
            try {
                String remoteIP = getIpAddress(request);
                String remoteHost = request.getRemoteHost();

                WebUser user = new WebUser(name, null, null, null, remoteIP, remoteHost, 0, null, -1);
                user.login(true, false, null);

                s = WebController.changeSessionIdentifier(request);
                s.put("user", user);
                user.valueBound(s);

            } catch (Exception ex) {
            	WebController.jspLog.error(ex, ex);
                WebController.jspLog.warn("Пользователь name = " + name + " не найден!");
            }
        }
    }

    // Для авторизации напрямую без WebController
    String uname = Funcs.sanitizeXml(request.getParameter("name"));
    String path2 = request.getParameter("passwd");

    if (uname != null && path2 != null) {
    	WebController.jspLog.info("uname = " + uname);
        try {
            String remoteIP = getIpAddress(request);
            String remoteHost = request.getRemoteHost();
            WebUser user = new WebUser(uname, path2, null, null, remoteIP, remoteHost, 0, null, -1);
            user.login(true, false, null);
            s = WebController.changeSessionIdentifier(request);
            s.put("user", user);
            user.valueBound(s);
        } catch (Exception ex) {
        	WebController.jspLog.error(ex, ex);
            WebController.jspLog.warn("Пользователь name = " + uname + " не найден!");
        }
    }
    
    String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/jsp/media/css/";
    String pathJS = webContextName + "/jsp/media/js/";
    String pathTinyMCE = webContextName + "/jsp/media/tinymce/js/tinymce/";

    String userUID = (String) s.get("userUID");
    WebController.jspLog.info("userUID = " + userUID);

    if (userUID == null) {
    	WebUtils.includeResponse(request, response, "/jsp/login.jsp", Constants.MAX_DOC_SIZE);
        return;
    } else {
        WebSession ws = (WebSession) s.get("ws");
        ResourceBundle rb = ws.getResource();

        boolean showStaff = false;
        String daysOldFlows = (String)s.get("daysOldFlows");
        boolean isOldFlows = "0".equals(daysOldFlows);
        boolean isAdmin = s.get("isAdmin") != null;
        Boolean btmp = (Boolean) s.get("isMonitor");
        boolean isMonitor = (btmp != null) ? btmp : false;
        boolean interFace = s.get("interface") != null;
        boolean hasPerson = s.get("hasPerson") != null;

        Long tmp = (Long) s.get("userId");
        long userId = tmp != null ? tmp : 0;

        String userIP = (String) s.get("userIP");
        if (userIP == null) {
        	userIP = "";
        }

        String userIIN = (String) s.get("userIIN");
        if (userIIN == null) {
        	userIIN = "";
        }

        long langId = ws.getInterfaceLangId();

        if (s.get("isKadry") != null || s.get("isChief") != null) {
            showStaff = true;
        }

        String userName = (String) s.get("userSign" + langId);
        if (userName==null||userName.isEmpty()){
            userName = Funcs.sanitizeXml(rb.getString("noname"));
        }
        String userGO = (String) s.get("userGO" + langId);
        if (userGO == null) {
            userGO = "";
        }
        String userPhoneIn = (String) s.get("userPhoneIn");
        if (userPhoneIn == null) {
            userPhoneIn = "";
        }
        String userEmail = (String) s.get("userEmail");
        if (userEmail == null) {
            userEmail = "";
        }

        String userPosition = (String) s.get("userPosition" + langId);
        if (userPosition == null) {
            userPosition = "";
        }
        String userDepartment = (String) s.get("userDepartment" + langId);
        if (userDepartment == null) {
            userDepartment = "";
        }
        String birthday = (String) s.get("userBirthday");
        if (birthday == null) {
            birthday = "";
        }

        List<String> roles = (List<String>) s.get("userRoles");
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        String lang = Funcs.sanitizeHtml((String) s.get("langCode"));
        tmp = (Long) s.get("theme");
        long theme = (tmp != null) ? tmp : 4;

        Map<String, Object> m = (Map<String, Object>) s.get("options");
        //m.put("menu.main",true);

        String logoPath = (String) s.get("head.logo");

        String logoImg = "<img src='" + (logoPath == null ? "media/img/logo-0.png" : logoPath) + "'/>";

        boolean onToolTip = true;
    	String tmpStr = getServletConfig().getServletContext().getInitParameter("breadcrumpsOn");
        boolean breadcrumpsOn = !"false".equals(tmpStr);
        
    	tmpStr = getServletConfig().getServletContext().getInitParameter("login.servicedesk");
    	boolean showServiceDesk = !"false".equals(tmpStr);

    	tmpStr = getServletConfig().getServletContext().getInitParameter("server.ping");
    	boolean showPing = !"false".equals(tmpStr);

    	String windowTitle = WebController.WINDOW_TITLE;

		boolean showFavouriteProccesses = WebController.SHOW_FAVOURITE_PROCCESSES;
		boolean showMenuItem = !isOldFlows && !(Boolean.TRUE.equals(m.get("menu.mainifc")) && interFace);
		boolean showMenuNotif = Boolean.TRUE.equals(m.get("menu.notification"));
		boolean activateChat = WebController.ACTIVATE_CHAT;
%>

<%!
	/**
	 * Получение реального IP рабочей станции при использовании
	 * проксирующего сервлета WLS
	 * @param req
	 * @return
	 */
	private String getIpAddress(HttpServletRequest req) {
		String ip = req.getHeader("wl-proxy-client-ip");
		return ip == null ? req.getRemoteAddr() : ip;
	}

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="description" content="">
<meta name="viewport" content="width=device-width">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title><%= windowTitle %></title>

<link rel="stylesheet" type="text/css" href="<%=pathCSS%>bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>bootstrap/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>icon.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>color.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>uiv<%=theme%>.css?2018-03-13">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>or3v<%=theme%>.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>custom-easyui.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>jquery.mCustomScrollbarv<%=theme%>.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>ap.css">

<link rel="shortcut icon" type="image/x-icon" href="<%=webContextName%>/jsp/media/img/favico.png">

<% if (activateChat) { %>
	<link rel="stylesheet" type="text/css" href="<%=webContextName%>/chat/js/chat-files/chat.css">
<% } %>
<link rel="stylesheet" type="text/css"	href="<%=pathCSS%>contactsInfo.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>notifications.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>jodit.min.css">

<script type="text/javascript">
	var changePass = <%=s.get("changePass")%>;
	// Хранит заголовок открытого в текущий момент раздела
	var selSect = <%out.print("'");%><%= (rb != null) ? Funcs.sanitizeXml(rb.getString("webStartPage")) : "" %><%out.print("'");%>;
	// хранит ID раздела 
	var idSect=-10;
	window.onbeforeunload = function () {
	    forseSaveChanges();
	}
</script>

<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js?v=2020-01-11"></script>
<script type="text/javascript" src="<%=pathJS%><%=lang%>.js?v=1"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.blockUI.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.nicescroll.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>upload/vendor/jquery.ui.widget.js"></script>
<script type="text/javascript" src="<%=pathJS%>upload/jquery.iframe-transport.js"></script>
<script type="text/javascript" src="<%=pathJS%>upload/jquery.fileupload.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.maskedinput.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.inputmask.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.inputmask.bundle.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>canvas.loader.js"></script>
<script type="text/javascript" src="<%=pathJS%>keys.js?v=1"></script>
<script type="text/javascript" src="<%=pathJS%>nicEdit.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.printPage.js"></script>
<script type="text/javascript" src="<%=pathJS%>notifications.js"></script>
<script type="text/javascript" src="<%=pathJS%>jodit.min.js"></script>

<script type="text/javascript" src="<%=pathTinyMCE%>tinymce.min.js"></script>
<script type="text/javascript" src="<%=pathTinyMCE%>jquery.tinymce.min.js"></script>

</head>

<body style="position: static;">
	<header id="main-navbar" class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div id="logo" data-logoid="0">
				<%=logoImg%>
				<h1>Мониторинг государственных служащих</h1>
			</div>
			<div id="nameGo">
				<%=userGO%>
				<% if(isAdmin) {%>
						<li class="nameStr"></li>
				<%} else {%>
						<li class="nameStr"><%=userDepartment%></li>
				<%} %>	
			</div>
			<div class="left-bar">
<% if (showServiceDesk) { %>
				<a id="helpPhone" class="hepl-icon tooltip-f" href="javascript:void(0)" title=""></a>							
<% } %>
				<div id="userDiv">
					<a id="account_panel" href="javascript:void(0)" title="" class="tooltip-f">
						<h2><%=userName%></h2>						
					</a>
				</div>
			</div>
		</div>
	</header>

	<!-- header -->
	<nav id="left-panel" style="height: 100%; position: fixed; top: 0px; left: 0px;">
		<div id="left-panel-content" style="overflow: hidden;">
			<ul
				style="transition-property: -moz-transform; transform-origin: 0px 0px 0px; transform: translate(0px, 0px);">
				<%
					if (Boolean.TRUE.equals(daysOldFlows!=null && !"-1".equals(daysOldFlows))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><div
						class="counter" id="oldordersList_count"></div><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webOldFlows"))%>';idSect=-5;"
					href="#ui=oldStart&id=ui_oldStartPage" id="ui_oldStartPage"><%=Funcs.sanitizeXml(rb.getString("webOldFlows"))%></a>
				</li>
				<%
				    	if (startHash == null) startHash = "#ui=oldStart&id=ui_oldStartPage";
					}
				    if (showMenuItem && Boolean.TRUE.equals(m.get("menu.main"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webStartPage"))%>';idSect=-10;"
					href="#ui=start&id=ui_startPage" id="ui_startPage"><%=Funcs.sanitizeXml(rb.getString("webStartPage"))%></a>
				</li>
				<%
				    	if (startHash == null) startHash = "#ui=start&id=ui_startPage";
					}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.mainifc")) && interFace) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webStartPage"))%>';idSect=-20;"
					href="#cmd=openMainIfc&id=ui_startPage" id="ui_startPage"><%=Funcs.sanitizeXml(rb.getString("webStartPage"))%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openMainIfc&id=ui_startPage";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.monitor"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><div
						class="counter processes_counter"></div> <a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webMonitor"))%>';idSect=-30;"
					href="#ui=tasksList&id=ui_Orders" id="ui_Orders"><%=Funcs.sanitizeXml(rb.getString("webMonitor"))%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=tasksList&id=ui_Orders";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.notification"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><div
						class="counter processesIN_counter"></div> <a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webNotification"))%>';idSect=-40;"
					href="#ui=notiInList&id=ui_OrdersNotification" id="ui_OrdersNotification"><%=Funcs.sanitizeXml(rb.getString("webNotification"))%></a></li>
					<%
				    if (startHash == null) startHash = "#ui=notiInList&id=ui_OrdersNotification";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.process"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webProcesses"))%>';idSect=-60;"
					href="#ui=processesList&mode=layout&id=ui_process" id="ui_process"><%=Funcs.sanitizeXml(rb.getString("webProcesses"))%></a>
				</li>
				<%
				    if (startHash == null) startHash = "#ui=processesList&mode=layout&id=ui_process";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.shtat"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webShtat"))%>';idSect=-70;"
					href="#cmd=openArch&uid=1014162.3211302&id=ui_staff" id="ui_staff"><%=Funcs.sanitizeXml(rb.getString("webShtat"))%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openArch&uid=1014162.3211302&id=ui_staff";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.archive"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webArchive"))%>';idSect=-80;"
					href="#ui=archList&mode=layout&id=ui_arch" id="ui_arch"><%=Funcs.sanitizeXml(rb.getString("webArchive"))%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=archList&mode=layout&id=ui_arch";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.help"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webHelp"))%>';idSect=-110;"
					href="#ui=helpWnd&mode=tabs&id=ui_help" id="ui_help"><%=Funcs.sanitizeXml(rb.getString("webHelp"))%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=helpWnd&mode=tabs&id=ui_help";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.dict"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webDicts"))%>';idSect=-90;"
					href="#ui=dictsList&mode=layout&id=ui_dicts" id="ui_dicts"><%=Funcs.sanitizeXml(rb.getString("webDicts"))%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=dictsList&mode=layout&id=ui_dicts";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.statistics"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='Статистика';idSect=-100;"
					href="#cmd=openArch&uid=1014162.3887376&id=ui_stat" id="ui_stat">Статистика</a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openArch&uid=1014162.4046423&id=ui_stat";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.userrights"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webRights"))%>';idSect=-120;"
					href="#cmd=openDict&uid=1014162.3554408&id=ui_right" id="ui_right"><%=Funcs.sanitizeXml(rb.getString("webRights"))%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openDict&uid=1014162.3554408&id=ui_right";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.useractions"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webUserAct"))%>';idSect=-130;"
					href="#cmd=openDict&uid=1014162.3555088&id=ui_actions" id="ui_actions"><%=Funcs.sanitizeXml(rb.getString("webUserAct"))%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openDict&uid=1014162.3555088&id=ui_actions";
												}
					if (showMenuItem && Boolean.TRUE.equals(m.get("menu.admins"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=Funcs.sanitizeXml(rb.getString("webAdmins"))%>';idSect=-140;"
					href="#ui=adminsList&mode=layout&id=ui_admins" id="ui_admins"><%=Funcs.sanitizeXml(rb.getString("webAdmins"))%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=adminsList&mode=layout&id=ui_admins";
												}
				%>
			</ul>
		</div>
<% if (showPing) { %>
		<div class="system-info">
			<div class="sysinfo server-ping">Время отклика: <span id="ping" class="badge badge-success"></span> мс</div>
		</div>
<% } %>
	</nav>
	<!-- //header -->


	<!-- container -->
	<div class="container">
		<div id="glassPane"
			style="width: 100%; z-index: 100; position: absolute; opacity: 1; background-color: #ffffff; height: 100%; display: none;"></div>
		
		<div id="uiPanel" class="easyui-layout ui-panel appwin"	data-options="fit:true,border:false">
			<div data-options="region:'north',split:false,border:false" style="height: 115px;">
				<div id="uiTitle" class="header ui-title" style="display: none;">
					Название интерфейса
					<div id="Winclose"></div>
				</div>
				<div id="fullPath" class="header header-t"></div>
				<div id="uiToolbar" class="ui-toolbar" style="display: none;">
					<div style="padding: 5px; display: inline;">
						<a class="easyui-linkbutton"
							data-options="iconCls:'icon-save',disabled:true"
							onclick="saveChanges()" id="saveBtn"><%=Funcs.sanitizeXml(rb.getString("save"))%></a>
						<a class="easyui-linkbutton"
							data-options="iconCls:'icon-undo',disabled:true" id="cancelBtn"
							onclick="resetChanges()"><%=Funcs.sanitizeXml(rb.getString("cancelChangesShort"))%></a>
						<a style="display:inline-block;"
							data-options="duration:100000000,plain:false,iconCls:'icon-rept'"
							id="reportBtn"><%=Funcs.sanitizeXml(rb.getString("print"))%></a>
						<%
						    if (Boolean.TRUE.equals(m.get("menu.monitor"))) {
						%>
						<a class="easyui-linkbutton"
							data-options="iconCls:'icon-next'" onclick="nextStep()"
							id="nextBtn"><%=Funcs.sanitizeXml(rb.getString("send"))%></a>
						<%
						    }
					    	if (!(Boolean) s.get("hideCloseIfcBtn")) {
						%>
						<a class="easyui-linkbutton" style="float: right;"
							data-options="iconCls:'icon-close'" onclick="closeIfc()"
							id="closeIfcBtn"></a>
						<%
						    }
						%>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false"
				id="app" style="min-height: 300px;"></div>
		</div>
		<%
		    if (Boolean.TRUE.equals(daysOldFlows!=null && !"-1".equals(daysOldFlows))) {
		%>
		<div id="oldStartDiv" style="display: none; height: 100%;" class='appwin'></div>
		<%
	    	}
		    if (Boolean.TRUE.equals(m.get("menu.main")) && !(Boolean.TRUE.equals(m.get("menu.mainifc")) && interFace)) {
		%>
		<div id="startDiv" style="display: none; height: 100%;" class='appwin'></div>
		<%
		    }
			if (Boolean.TRUE.equals(m.get("menu.process"))) {
		%>
		<%
			if (showFavouriteProccesses) {
		%>	
		<div id="processesList" style="width: 100%; display: none; padding: 10px 30px 30px 10px; box-sizing: border-box;" class='appwin'>
			<div class="easyui-panel" data-options="fit:true,border:false">
				<div id="allProcesses_Layout" class="easyui-layout" data-options="fit:true,border:false">
					<div id="favoriteProcesses" data-options="hideCollapsedContent:true,region:'north',split:true,maxHeight:234,minHeight:234,border:false,hideExpandTool:true,expandMode:null">
						<table class="portlet-table" width="100%">
							<tr>
								<td>
									<div class="header"><%=Funcs.sanitizeXml(rb.getString("webFavoriteProcesses"))%></div>
								</td>
							</tr>
							<tr>
								<td>
									<div class="portletFavProc">
										<div class="pcontent easyui-panel" data-options="fit:true,border:false">
											<div id="processesList_Layout2" data-options="border:false">
												<div data-options="border:false" id="processesList_body2" style="padding: 5px; background: #fff;"></div>
											</div>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</div>
					<div id="allowedProcesses" data-options="region:'center',split:true,border:false">
						<table class="portlet-table" width="100%">
							<tr>
								<td>
									<div class="header"><%=Funcs.sanitizeXml(rb.getString("webAvailProc"))%></div>
								</td>
							</tr>
							<tr>
								<td>
									<div class="portletProc">
										<div class="easyui-panel" data-options="fit:true,border:false">
											<div id="processesList_Layout" class="easyui-layout" data-options="fit:true,border:false">
												<div data-options="region:'west',split:true,minWidth:250,border:false" style="width: 250px;">
													<ul class='process-tree easyui-tree nochange' id='processTree' data-options='url:"<%=webContextName%>/main?cmd=getProcessData&guid=<%=guid%>"'>
													</ul>
												</div>
												<div data-options="region:'center',border:false" id="processesList_body" style="padding: 5px; background: #fff;"></div>
											</div>
										</div>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
		<%
			} else {
		%>
		<div id="processesList" style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webAvailProc"))%>
							<input id="processSearchPage" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">					
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel" data-options="fit:true,border:false">
								<div id="processesList_Layout" class="easyui-layout" data-options="fit:true,border:false">
									<div data-options="region:'west',split:true,minWidth:250,border:false" style="width: 250px;">
										<ul class='process-tree easyui-tree nochange' id='processTree' data-options='url:"<%=webContextName%>/main?cmd=getProcessData&guid=<%=guid%>"'>
										</ul>
									</div>
									<div data-options="region:'center',border:false" id="processesList_body" style="padding: 5px; background: #fff;"></div>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
			}
		%>
		<%
		    }
			if (Boolean.TRUE.equals(m.get("menu.archive"))) {
		%>
		<div id="archList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webArchive"))%></div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false">
								<div id="archList_Layout" class="easyui-layout"
									data-options="fit:true,border:false">
									<div
										data-options="region:'west',split:true,minWidth:250,border:false"
										style="width: 250px;">
										<ul class='arch-tree easyui-tree nochange' id='archiveTree'
											data-options='url:"<%=webContextName%>/main?cmd=getArchiveData&guid=<%=guid%>"'>
										</ul>
									</div>
									<div data-options="region:'center',border:false"
										id="archList_body" style="padding: 5px; background: #fff;"></div>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.dict"))) {
		%>
		<div id="dictsList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webNSI"))%></div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false">
								<div id="dictsList_Layout" class="easyui-layout"
									data-options="fit:true,border:false">
									<div
										data-options="region:'west',split:true,minWidth:250,border:false"
										style="width: 250px;">
										<ul class='spr-tree easyui-tree nochange' id='dictsTree'
											data-options='url:"<%=webContextName%>/main?cmd=getDictData&guid=<%=guid%>"'>
										</ul>
									</div>
									<div data-options="region:'center',border:false"
										id="dictsList_body" style="padding: 5px; background: #fff;"></div>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.monitor"))) {
		%>
		<div id="tasksList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webMonitor"))%>
							<span id="processes_counter" class="badge badge-info processes_counter"></span>
							<input type="checkbox" id="task_checkbox"/><label id="select_all_label" style="margin-left: 5px;" title="Выделить все доступные"><%= Funcs.sanitizeXml(rb.getString("webAllCheckedProc")) %></label>
							<button id="disable_task" class="easyui-linkbutton"><%= Funcs.sanitizeXml(rb.getString("webDisableListUpdate"))%></button>
							<button id="taskremoveprocess" style="display: none;" class="easyui-linkbutton c5"><%= Funcs.sanitizeXml(rb.getString("webDeleteProcess"))%></button>	
							<button id="usedmemory" class="easyui-linkbutton"><%= Funcs.sanitizeXml(rb.getString("webShowUsedMemory"))%></button>	
							<label id="usedmemorylabel" style="margin-left: 5px;" title="Используемая память"><%= Funcs.sanitizeXml(rb.getString("webUsedMemory"))%></label>
							<% if(isAdmin) {%><button style="display: none;" class="easyui-linkbutton c1" id="debugger" onclick="showdebugger()"><font style="color:white;">Отладка</font></button>
							<%}%>	
							<input id="taskSearchPage" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">
							<div id="tasksPages" style="width:100%; padding-top: 5px;"></div>	
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portletTasks">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false"></div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.notification"))) {
		%>
		<div id="notiInList" style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="headerNotif"><%=Funcs.sanitizeXml(rb.getString("webNotification"))%>
							<span id="processesIN_counter" class="badge badge-info"></span>
							<label><%=Funcs.sanitizeXml(rb.getString("webPeriodNotification"))%>:</label>
							<label><%=Funcs.sanitizeXml(rb.getString("webStartDayNotification"))%> </label><input class="easyui-datebox" id="dateN1" style="width:100px;height:22px;"></input>
							<label><%=Funcs.sanitizeXml(rb.getString("webEndDayNotification"))%> </label><input class="easyui-datebox" id="dateN2" style="width:100px;height:22px;"></input>
							<label><%=Funcs.sanitizeXml(rb.getString("webFeature"))%>:</label>
								<select id="selectNoti">
									<option value="0" selected><%=Funcs.sanitizeXml(rb.getString("webAll"))%></option>
									<option value="1"><%=Funcs.sanitizeXml(rb.getString("webViewed"))%></option>
									<option value="2"><%=Funcs.sanitizeXml(rb.getString("webUnreviewed"))%></option>
								</select>
							<button id="filterNoti" class="easyui-linkbutton c1 l-btn l-btn-small">
								<span class="l-btn-left">
								<span class="l-btn-text" style="color: #fff"><%=Funcs.sanitizeXml(rb.getString("webFilter"))%></span>
								</span>
							</button>
							<button id="filterClean" class="easyui-linkbutton c5 l-btn l-btn-small">
								<span class="l-btn-left">
								<span class="l-btn-text" style="color: #fff"><%=Funcs.sanitizeXml(rb.getString("webClear"))%></span>
								</span>
							</button>
							<input id="txtSearchPageNotification" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">
						</div>
						<div id="pp" style="width:100%;"></div>
						<table id="toolNotif" width="100%;">
							<td width="50%"><a id="sortTitle" title="Сортировать по заголовоку" onclick="sortColumnNotif(sortTitle)"><%=Funcs.sanitizeXml(rb.getString("webNotificationTitle"))%></a></td>
							<td width="20%"><a id="sortFrom" title="Сортировать по отправителю" onclick="sortColumnNotif(sortFrom)"><%=Funcs.sanitizeXml(rb.getString("webNotificationFrom"))%></a></td>
							<td width="10%"><a id="sortInDate" title="Сортировать по дате получения" onclick="sortColumnNotif(sortInDate)" class="sortNotif asc"><%=Funcs.sanitizeXml(rb.getString("webNotificationInDate"))%></a></td>
							<td width="10%"><a id="sortOpenDate" title="Сортировать по дате открытия" onclick="sortColumnNotif(sortOpenDate)"><%=Funcs.sanitizeXml(rb.getString("webNotificationOpenDate"))%></a></td>
							<td width="10%"><a id="sortAwereDate" title="Сортировать по дате ознакомления" onclick="sortColumnNotif(sortAwereDate)"><%=Funcs.sanitizeXml(rb.getString("webNotificationAwereDate"))%></a></td>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portletNotif">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false"></div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.admins"))) {
		%>
		<div id="adminsList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webAdmins"))%></div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false">
								<div id="adminsList_Layout" class="easyui-layout"
									data-options="fit:true,border:false">
									<div
										data-options="region:'west',split:true,minWidth:250,border:false"
										style="width: 250px;">
										<ul class='admin-tree easyui-tree nochange' id='adminsTree'
											data-options='url:"<%=webContextName%>/main?cmd=getAdminData&guid=<%=guid%>"'>
										</ul>
									</div>
									<div data-options="region:'center',border:false"
										id="adminsList_body" style="padding: 5px; background: #fff;"></div>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.usersessions"))) {
		%>
		<div id="sessionsList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webSessions"))%>
						<span class="badge badge-info" id="countSessions"></span>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false"></div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.profile"))) {
		%>
		<div id="profileWnd"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=Funcs.sanitizeXml(rb.getString("webProfile"))%></div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false">
								<div class="profile">
									<table width="100%" border="0">
										<tr>
											<td width="50%">
												<div class="pr">
													<div class="uName" title="" class="tooltip-f"><%=userName%></div>

													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webBirthday"))%></div>
													<%=birthday%>


													<div class="uRole prof "><%=Funcs.sanitizeXml(rb.getString("webRoles"))%></div>
													<%
													    for (String role : roles) {
													%>
													<%=Funcs.sanitizeXml(role)%><br />
													<%
													    }
													%>
													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webPosition"))%></div>
													<%=userPosition%>

													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webGO"))%></div>
													<%=userGO%>

													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webContacts"))%></div>
													<%=Funcs.sanitizeXml(rb.getString("webTel"))%>:
													<%=userPhoneIn%><br /> Email:
													<%=userEmail%>
												</div>
											</td>
											<td width="50%">
												<div class="pr">

													<div id="yourPhoto" class="Uphoto">
														<img id="yourImg" />
													</div>

													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webPass"))%>
													</div>
													<div class="pasSm">
														<a href="javascript:changePwdDialog(false);"> <%=Funcs.sanitizeXml(rb.getString("webPassChange"))%>
														</a>
													</div>

													<input type="file" style="display: none;" id="yourUpload" />

													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webTooltips")) %></div>
													<div id="uTooltip">
														<table class="utip">
															<tr>
																<td><%=Funcs.sanitizeXml(rb.getString("webOn"))%></td>
																<td><%=Funcs.sanitizeXml(rb.getString("webOff"))%></td>
															</tr>
															<tr>
																<td><input type="radio" name="nametool" value="1"
																	<%if (onToolTip) {%> checked="true" <%}%> /></td>
																<td><input type="radio" name="nametool" value="2"
																	<%if (!onToolTip) {%> checked="true" <%}%> /></td>
															</tr>
														</table>
													</div>
													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webBreadcrumbs"))%></div>
													<div id="uBreadcrumpsOn">
														<table class="ubread">
															<tr>
																<td><%=Funcs.sanitizeXml(rb.getString("webOn"))%></td>
																<td><%=Funcs.sanitizeXml(rb.getString("webOff"))%></td>
															</tr>
															<tr>
																<td><input type="radio" name="namebread" value="1"
																	<%if (breadcrumpsOn) {%> checked="true" <%}%> /></td>
																<td><input type="radio" name="namebread" value="2"
																	<%if (!breadcrumpsOn) {%> checked="true" <%}%> /></td>
															</tr>
														</table>
													</div>
													<div class="uRole prof"><%=Funcs.sanitizeXml(rb.getString("webLang"))%></div>
													<div id="yourLang" class="ulang">
														<a id="langKz" href="#" <%if ("KZ".equals(lang)) {%>
															data-options="selected:true" <%}%>
															class="easyui-linkbutton"
															onclick="changeInterfaceLang('KZ', event)">Қазақша</a> <a
															id="langRu" href="#" <%if ("RU".equals(lang)) {%>
															data-options="selected:true" <%}%>
															class="easyui-linkbutton"
															onclick="changeInterfaceLang('RU', event)">Русский</a>
													</div>

													<!-- Контекстная менюшка для фотки -->
													<div id="mm" class="easyui-menu" style="width: 120px;">
														<div data-options="iconCls:'icon-arrow-down'"
															onclick="javascript:uploadYourImage(<%=userId%>);"><%=Funcs.sanitizeXml(rb.getString("webPhotoLoad"))%></div>
														<div data-options="iconCls:'icon-remove'"
															onclick="javascript:deleteImage();"><%=Funcs.sanitizeXml(rb.getString("webPhotoDelete"))%></div>
														<div data-options="iconCls:'icon-folder-open'"
															onclick="javascript:copyImageFromData();"><%=Funcs.sanitizeXml(rb.getString("webPhotoTake"))%></div>
													</div>

													<!-- Контекстная менюшка для деревьев -->
													<div id="treeMenu" class="easyui-menu" style="width: 120px;">
														<div onclick="makeTree('expand')" data-options="iconCls:'icon-expand'"><%=Funcs.sanitizeXml(rb.getString("webExpand"))%></div>
														<div onclick="makeTree('expandAll')" data-options="iconCls:'icon-expandAll'"><%=Funcs.sanitizeXml(rb.getString("webExpandAll"))%></div>
														<div class="menu-sep"></div>
														<div onclick="makeTree('collapse')" data-options="iconCls:'icon-collapse'"><%=Funcs.sanitizeXml(rb.getString("webCollapse"))%></div>
														<div onclick="makeTree('collapseAll')" data-options="name:'collapseAll',iconCls:'icon-collapseAll'"><%=Funcs.sanitizeXml(rb.getString("webCollapseAll"))%></div>
														<div class="menu-sep"></div>
														<div id="moveUp" onclick="makeTree('moveUp')" data-options="iconCls:'icon-up'"><%=Funcs.sanitizeXml(rb.getString("webMoveUp"))%></div>
														<div id="moveDown" onclick="makeTree('moveDown')" data-options="iconCls:'icon-down'"><%=Funcs.sanitizeXml(rb.getString("webMoveDown"))%></div>
													</div>
													<div id="procMenu" class="easyui-menu" style="width: 170px;">
														<div id="addToFavorites" data-options="iconCls:'icon-addToFavorites'"><%=Funcs.sanitizeXml(rb.getString("webAddToFavorites"))%></div>
													</div>
													<div id="procMenu2" class="easyui-menu" style="width: 170px;">
														<div id="removeFromFavorites" data-options="iconCls:'icon-removeFromFavorites'"><%=Funcs.sanitizeXml(rb.getString("webRemoveFromFavorites"))%></div>
													</div>														
													<!-- Контекстная менюшка для TreeTable -->
													<div id="treeTableMenu" class="easyui-menu"
														style="width: 120px;">
														<div onclick="makeTreeTable('expand')"
															data-options="iconCls:'icon-expand'"><%=Funcs.sanitizeXml(rb.getString("webExpand"))%></div>
														<div onclick="makeTreeTable('expandAll')"
															data-options="iconCls:'icon-expandAll'"><%=Funcs.sanitizeXml(rb.getString("webExpandAll"))%></div>
														<div class="menu-sep"></div>
														<div onclick="makeTreeTable('collapse')"
															data-options="iconCls:'icon-collapse'"><%=Funcs.sanitizeXml(rb.getString("webCollapse"))%></div>
														<div onclick="makeTreeTable('collapseAll')"
															data-options="iconCls:'icon-collapseAll'"><%=Funcs.sanitizeXml(rb.getString("webCollapseAll"))%></div>
													</div>

												</div>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.statistics"))) {
		%>
		<div id="statWnd" style="display: none; padding: 10px 30px 30px 10px;"
			class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header">Статистика</div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel"
								data-options="fit:true,border:false"></div>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.help"))) {
		%>
		<div id="helpWnd" style="display: none; padding: 12px 30px 30px 12px;"
			class='appwin'></div>
		<%
		    }
		%>
	</div>
		<div style="display: none">
		<div id="popup_user_content">
			<table width="100%" border="0">
				<tr>
					<td><div class="phPr">
							<img />
						</div></td>
					<td>
						<div class="doljn">
							<%=userPosition%><br /> <span><i class="icon-phone"></i><%=userPhoneIn%></span>
							<span><i class="icon-email"></i><%=userEmail%></span>
						</div>
					</td>
				</tr>
			</table>
			<%
			    if (isAdmin || Boolean.TRUE.equals(m.get("menu.usersessions"))) {
			%>
			<div class="pasSm">
				<a href="#ui=sessionsList&mode=tabs"><%=Funcs.sanitizeXml(rb.getString("webSessions"))%></a>
			</div>
			<%
			    }
			%>
			<div class="account_set">
				<%
				    if (Boolean.TRUE.equals(m.get("menu.profile"))) {
				%>
				<a onclick="forseSaveChanges();" href="#ui=profileWnd&mode=tabs" id=""
					class="btn btn-primary"> <i class="icon-user icon-white"></i> <%=Funcs.sanitizeXml(rb.getString("webDetails"))%></a>
				<%
				    }
				%>
				<a href="javascript:logout()"
					class="btn btn-danger btn-out"> <i
					class="icon-share-alt icon-white"></i> <%=Funcs.sanitizeXml(rb.getString("webExit"))%></a>
			</div>
		</div>
	</div>
	
	<div style="display: none">
		<div id="popup_help_content">
			<table width="100%" border="0">
				<tr>
					<td><div class="tool-help" style="width:300px"/></td>
				</tr>
			</table>
						
		</div>
	</div>

	<!-- // container -->
	<script type="text/javascript">
		var guid = '<%=guid%>';
		var userId = <%=userId%>;
		var theme = <%=theme%>;
		var langCode = '<%= lang != null ? lang.toLowerCase() : "kz" %>';
		var startHash = '<%=startHash%>';
		var backPage = <%=backPage != null ? "'" + backPage + "'" : "null"%>;
		var onTool = <%=onToolTip%>;
		var breadcrumpsOn = <%=breadcrumpsOn%>;
		var notis = <%= showMenuNotif %>;
		var userIP = '<%=userIP%>';
		var userIIN = '<%=userIIN%>';
		window.contextName = '<%=webContextName%>';

		var translation = {};
		translation['wait'] = '<%=Funcs.sanitizeXml(rb.getString("webWait"))%>';
		translation['passChange'] = '<%=Funcs.sanitizeXml(rb.getString("webPassChangeTitle"))%>';
		translation['passOld'] = '<%=Funcs.sanitizeXml(rb.getString("webPassOld"))%>';
		translation['passNew'] = '<%=Funcs.sanitizeXml(rb.getString("webPassNew"))%>';
		translation['passConfirm'] = '<%=Funcs.sanitizeXml(rb.getString("webPassConfirm"))%>';
		translation['ok'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgOk"))%>';
		translation['cancel'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgCancel"))%>';
		translation['close'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgClose"))%>';
		translation['change'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgChange"))%>';
		translation['save'] = '<%=Funcs.sanitizeXml(rb.getString("weDlgSave"))%>';
		translation['ignore'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgIgnore"))%>';
		translation['continue'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgContinue"))%>';
		translation['continue2'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgContinue2"))%>';
		translation['errors'] = '<%=Funcs.sanitizeXml(rb.getString("webErrors"))%>';
		translation['saving'] = '<%=Funcs.sanitizeXml(rb.getString("webSaving"))%>';
		translation['canceling'] = '<%=Funcs.sanitizeXml(rb.getString("webCanceling"))%>';
		translation['error'] = '<%=Funcs.sanitizeXml(rb.getString("error"))%>';
		translation['stopProcess'] = '<%=Funcs.sanitizeXml(rb.getString("webProcStop"))%>';
		translation['removeProcess'] = '<%=Funcs.sanitizeXml(rb.getString("webRemoveProcess"))%>';
		translation['removeProcess2'] = '<%=Funcs.sanitizeXml(rb.getString("webRemoveProcess2"))%>';
		translation['askNextStep'] = '<%=Funcs.sanitizeXml(rb.getString("webAskNextStep"))%>';
		translation['ifcNotExistMessage'] = '<%=Funcs.sanitizeXml(rb.getString("ifcNotExistMessage"))%>';
		translation['rptGenerateMessage'] = '<%=Funcs.sanitizeXml(rb.getString("rptGenerateMessage"))%>';
		translation['procPerformedMessage'] = '<%=Funcs.sanitizeXml(rb.getString("procPerformedMessage"))%>';
		translation['deleting'] = '<%=Funcs.sanitizeXml(rb.getString("webDeleting"))%>';
		translation['alert'] = '<%=Funcs.sanitizeXml(rb.getString("webAlert"))%>';
		translation['sign'] = '<%=Funcs.sanitizeXml(rb.getString("webDlgSign"))%>';
		translation['keystore'] = '<%=Funcs.sanitizeXml(rb.getString("webKeyStore"))%>';
		translation['p12files'] = '<%=Funcs.sanitizeXml(rb.getString("webPKCS12Files"))%>';
		translation['enterPassword'] = '<%=Funcs.sanitizeXml(rb.getString("enterPassword"))%>';
		translation['lastSuccesProcessDef'] = '<%=Funcs.sanitizeXml(rb.getString("webLastSuccesProcessDef"))%>';
		translation['mask'] = '<%=Funcs.sanitizeXml(rb.getString("mask"))%>';
		translation['print'] = '<%=Funcs.sanitizeXml(rb.getString("print"))%>';
		translation['supportTeam'] = '<%=Funcs.sanitizeXml(rb.getString("supportTeam"))%>';
		translation['webEnableListUpdate'] = '<%=Funcs.sanitizeXml(rb.getString("webEnableListUpdate"))%>';
		translation['webDisableListUpdate'] = '<%=Funcs.sanitizeXml(rb.getString("webDisableListUpdate"))%>';

		var pageName = {};
		var pageId = {};
		pageName['ui_oldStartPage'] = '<%=Funcs.sanitizeXml(rb.getString("webOldFlows"))%>';
		pageId['ui_oldStartPage'] = -5;
		pageName['ui_startPage'] = '<%=Funcs.sanitizeXml(rb.getString("webStartPage"))%>';
		pageId['ui_startPage'] = -10;

		pageName['ui_Orders'] = '<%=Funcs.sanitizeXml(rb.getString("webMonitor"))%>';
		pageId['ui_Orders'] = -30;
		
		pageName['ui_OrdersNotification'] = '<%=Funcs.sanitizeXml(rb.getString("webNotification"))%>';
		pageId['ui_OrdersNotification'] = -40;
		
		pageName['ui_personInfo'] = '<%=Funcs.sanitizeXml(rb.getString("webMyProfile"))%>';
		pageId['ui_personInfo'] = -50;

		pageName['ui_process'] = '<%=Funcs.sanitizeXml(rb.getString("webProcesses"))%>';
		pageId['ui_process'] = -60;

		pageName['ui_staff'] = '<%=Funcs.sanitizeXml(rb.getString("webShtat"))%>';
		pageId['ui_staff'] = -70;

		pageName['ui_arch'] = '<%=Funcs.sanitizeXml(rb.getString("webArchive"))%>';
		pageId['ui_arch'] = -80;

		pageName['ui_dicts'] = '<%=Funcs.sanitizeXml(rb.getString("webDicts"))%>';
		pageId['ui_dicts'] = -90;

		pageName['ui_stat'] = 'Статистика';
		pageId['ui_stat'] = -100;

		pageName['ui_help'] = '<%=Funcs.sanitizeXml(rb.getString("webHelp"))%>';
		pageId['ui_help'] = -110;

		pageName['ui_right'] = '<%=Funcs.sanitizeXml(rb.getString("webRights"))%>';
		pageId['ui_right'] = -120;

		pageName['ui_actions'] = '<%=Funcs.sanitizeXml(rb.getString("webUserAct"))%>';
		pageId['ui_actions'] = -130;
		
		pageName['ui_admins'] = '<%=Funcs.sanitizeXml(rb.getString("webAdmins"))%>';
		pageId['ui_admins'] = -140;

		
	</script>
	<script type="text/javascript" src="<%=pathJS%>app.js?v=2018-09-04"></script>
	<script type="text/javascript" src="<%=pathJS%>grid.editors.js?v=2018-08-08"></script>
	<script type="text/javascript" src="<%=pathJS%>grid.formatter.js?v=2016-11-29"></script>
	<% if (activateChat) { %>
		<script type="text/javascript" src="<%=webContextName%>/chat/js/jquery.localstorage.js"></script>
		<script type="text/javascript" src="<%=webContextName%>/chat/js/strophe.js"></script>
		<script type="text/javascript" src="<%=webContextName%>/chat/js/chat.js?v=2016-11-27"></script>
	<% } %>
	<SCRIPT TYPE='text/javascript' SRC='<%= pathJS %>applet-load.js?v=2016-08-21'></SCRIPT>
	<script type="text/javascript" src="<%= pathJS %>tumsocket_or3.js?v=2018-09-14"></script>
</body>
</html>
<%
    }
%>
