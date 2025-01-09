<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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
	/* Путь к логотипу для главного окна прописывается в файле web.xml
	 * вместе с контекстом веб-приложения
	 * 
	 * <init-param>
	 *    	<param-name>logoPath</param-name>
   	 * 		<param-value>/ekyzmet-ui/jsp/media/img/logo-0.png</param-value>
  	 * </init-param>
	*/

    // Для входа в систему с кирилическими именами и паролями	
    request.setCharacterEncoding("UTF-8");

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
    HttpSession s = request.getSession();
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
                s.setAttribute("user", user);

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
        byte[] b2 = new AES(StartupServlet.REMOTE_LOGIN_SECRET_KEY).decrypt(b1);
        if (b2 != null) {
            String name = new String(b2);
            try {
                String remoteIP = getIpAddress(request);
                String remoteHost = request.getRemoteHost();

                WebUser user = new WebUser(name, null, null, null, remoteIP, remoteHost, 0, null, -1);
                user.login(true, false, null);

                s = WebController.changeSessionIdentifier(request);
                s.setAttribute("user", user);

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
            s.setAttribute("user", user);
        } catch (Exception ex) {
        	WebController.jspLog.error(ex, ex);
            WebController.jspLog.warn("Пользователь name = " + uname + " не найден!");
        }
    }
    
    String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/jsp/media/css/";
    String pathJS = webContextName + "/jsp/media/js/";

    String userUID = (String) s.getAttribute("userUID");
    WebController.jspLog.info("userUID = " + userUID);

    if (userUID == null) {
    	WebUtils.includeResponse(request, response, "/jsp/login.jsp", Constants.MAX_DOC_SIZE);
        return;
    } else {
        WebSession ws = (WebSession) s.getAttribute("ws");
        ResourceBundle rb = ws.getResource();

        boolean showStaff = false;
        String daysOldFlows = (String)s.getAttribute("daysOldFlows");
        boolean isOldFlows = "0".equals(daysOldFlows);
        boolean isAdmin = s.getAttribute("isAdmin") != null;
        Boolean btmp = (Boolean) s.getAttribute("isMonitor");
        boolean isMonitor = (btmp != null) ? btmp : false;
        boolean interFace = s.getAttribute("interface") != null;
        boolean hasPerson = s.getAttribute("hasPerson") != null;

        Long tmp = (Long) s.getAttribute("userId");
        long userId = tmp != null ? tmp : 0;

        long langId = ws.getInterfaceLangId();

        if (s.getAttribute("isKadry") != null || s.getAttribute("isChief") != null) {
            showStaff = true;
        }

        String userName = (String) s.getAttribute("userSign" + langId);
        if (userName==null||userName.isEmpty()){
            userName = rb.getString("noname");
        }
        String userGO = (String) s.getAttribute("userGO" + langId);
        if (userGO == null) {
            userGO = "";
        }
        String userPhoneIn = (String) s.getAttribute("userPhoneIn");
        if (userPhoneIn == null) {
            userPhoneIn = "";
        }
        String userEmail = (String) s.getAttribute("userEmail");
        if (userEmail == null) {
            userEmail = "";
        }

        String userPosition = (String) s.getAttribute("userPosition" + langId);
        if (userPosition == null) {
            userPosition = "";
        }
        String userDepartment = (String) s.getAttribute("userDepartment" + langId);
        if (userDepartment == null) {
            userDepartment = "";
        }
        String birthday = (String) s.getAttribute("userBirthday");
        if (birthday == null) {
            birthday = "";
        }

        List<String> roles = (List<String>) s.getAttribute("userRoles");
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        String lang = Funcs.sanitizeHtml((String) s.getAttribute("langCode"));
        //tmp = (Long) s.getAttribute("theme");
        long theme = 2; // (tmp != null) ? tmp : 1;

        Map<String, Object> m = (Map<String, Object>) s.getAttribute("options");
        //m.put("menu.main",true);

        String logoPath = (String) s.getAttribute("head.logo");

        String logoImg = "<img src='" + (logoPath == null ? "media/img/logo-0.png" : logoPath) + "'/>";

        boolean onToolTip = true;
    	String tmpStr = getServletConfig().getServletContext().getInitParameter("breadcrumpsOn");
        boolean breadcrumpsOn = !"false".equals(tmpStr);
        
    	tmpStr = getServletConfig().getServletContext().getInitParameter("login.servicedesk");
    	boolean showServiceDesk = !"false".equals(tmpStr);

    	tmpStr = getServletConfig().getServletContext().getInitParameter("server.ping");
    	boolean showPing = !"false".equals(tmpStr);

    	String windowTitle = (String) s.getAttribute("head.title");
        if (windowTitle == null) windowTitle = "Регистр ИРиИС";
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
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>uiv<%=theme%>.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>or3v<%=theme%>.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>custom-easyui.css">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>jquery.mCustomScrollbarv<%=theme%>.css">
<link rel="stylesheet" type="text/css"	href="<%=pathCSS%>contactsInfo.css">

<script type="text/javascript">
	var changePass = <%=s.getAttribute("changePass")%>;
	// Хранит заголовок открытого в текущий момент раздела
	var selSect = <%out.print("'");%><%= (rb != null) ? rb.getString("webStartPage") : "" %><%out.print("'");%>;
	// хранит ID раздела 
	var idSect=-10;
	window.onbeforeunload = function () {
	    forseSaveChanges();
	}
</script>

<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js?v=2016-06-16"></script>
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
</head>

<body>
	<header id="main-navbar" class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<table width="100%" border="0">
				<tr>
					<td width="175">
						<div id="logo" data-logoid="0">
							<%=logoImg%>
						</div>
					</td>

					<td class="nameC"><div id="nameGo"><%=userGO%>
					<% if(isAdmin) {%>
							<li class="nameStr"></li>
					<%} else {%>
							<li class="nameStr"><%=userDepartment%></li>
					<%} %>	
						</div></td>
					<td class="nameC" >
						<table class="help-block" >
<% if (showServiceDesk) { %>
							<tr>
								<td class="hepl-adress">
							
								<a id="helpPhone" class="hepl-icon" href="javascript:void(0)" title="" >
								<%=Funcs.sanitizeXml(rb.getString("supportTeam"))%></a>							
								</td>
							</tr>
<% } %>
							<tr>
								<td>
								<div id="userDiv">
									<a id="account_panel" href="javascript:void(0)" title="" class="tooltip-f">
									<h2><%=userName%>.</h2>						
									</a>
									</div>
								</td>
							</tr>
<% if (showPing) { %>
							<tr>
								<td class="server-ping">
									<%=Funcs.sanitizeXml(rb.getString("pingTime"))%>: <span id="ping" class="badge badge-success"></span> мс
								</td>
							</tr>
<% } %>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</header>

	<!-- header -->
	<nav id="left-panel"
		style="height: 100%; position: fixed; top: 0px; left: 0px;">
		<div id="left-panel-content" style="overflow: hidden;">
			<ul
				style="transition-property: -moz-transform; transform-origin: 0px 0px 0px; transform: translate(0px, 0px);">
				<%
				    if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.main"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webStartPage")%>';idSect=-10;"
					href="#ui=start&id=ui_startPage" id="ui_startPage"><%=rb.getString("webStartPage")%></a>
				</li>
				<%
				    	if (startHash == null) startHash = "#ui=start&id=ui_startPage";
					}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.mainifc"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webStartPage")%>';idSect=-20;"
					href="#cmd=openMainIfc&id=ui_startPage" id="ui_startPage"><%=rb.getString("webStartPage")%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openMainIfc&id=ui_startPage";
												}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.monitor"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><div
						class="counter processes_counter"></div> <a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webMonitor")%>';idSect=-30;"
					href="#ui=tasksList&id=ui_Orders" id="ui_Orders"><%=rb.getString("webMonitor")%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=tasksList&id=ui_Orders";
												}
					if (!isOldFlows && hasPerson) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webMyProfile")%>';idSect=-40;"
					href="#cmd=openArch&uid=1014162.3198690&id=ui_personInfo" id="ui_personInfo"><%=rb.getString("webMyProfile")%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openArch&uid=1014162.3198690&id=ui_personInfo";
												}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.process"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webProcesses")%>';idSect=-50;"
					href="#ui=processesList&mode=layout&id=ui_process" id="ui_process"><%=rb.getString("webProcesses")%></a>
				</li>
				<%
				    if (startHash == null) startHash = "#ui=processesList&mode=layout&id=ui_process";
												}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.shtat"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webShtat")%>';idSect=-60;"
					href="#cmd=openArch&uid=1014162.3211302&id=ui_staff" id="ui_staff"><%=rb.getString("webShtat")%></a></li>
				<%
				    if (startHash == null) startHash = "#cmd=openArch&uid=1014162.3211302&id=ui_staff";
												}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.archive"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webArchive")%>';idSect=-70;"
					href="#ui=archList&mode=layout&id=ui_arch" id="ui_arch"><%=rb.getString("webArchive")%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=archList&mode=layout&id=ui_arch";
												}
					if (!isOldFlows && Boolean.TRUE.equals(m.get("menu.dict"))) {
				%>
				<li <%if (startHash == null) {%> class="active" <%}%>><a
					onclick="forseSaveChanges(this); selSect='<%=rb.getString("webDicts")%>';idSect=-80;"
					href="#ui=dictsList&mode=layout&id=ui_dicts" id="ui_dicts"><%=rb.getString("webDicts")%></a></li>
				<%
				    if (startHash == null) startHash = "#ui=dictsList&mode=layout&id=ui_dicts";
												}
				%>
			</ul>
		</div>
		<div class="icon-caret-down"></div>
		<div class="icon-caret-up"></div>
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
							onclick="saveChanges()" id="saveBtn"><%=rb.getString("save")%></a>
						<a class="easyui-linkbutton"
							data-options="iconCls:'icon-undo',disabled:true" id="cancelBtn"
							onclick="resetChanges()"><%=rb.getString("cancelChangesShort")%></a>
						<a style="display:inline-block;"
							data-options="duration:100000000,plain:false,iconCls:'icon-rept'"
							id="reportBtn"><%=rb.getString("print")%></a>
						<%
						    if (Boolean.TRUE.equals(m.get("menu.monitor"))) {
						%>
						<a class="easyui-linkbutton"
							data-options="iconCls:'icon-next'" onclick="nextStep()"
							id="nextBtn"><%=rb.getString("send")%></a>
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
		    if (Boolean.TRUE.equals(m.get("menu.main"))) {
		%>
		<div id="startDiv" style="display: none; height: 100%;" class='appwin'></div>
		<%
		    }
						if (Boolean.TRUE.equals(m.get("menu.process"))) {
		%>
		<div id="processesList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=rb.getString("webAvailProc")%></div>
					</td>
				</tr>
				<tr>
					<td>
						<div class="portlet">
							<div class="pcontent easyui-panel" data-options="fit:true,border:false">
								<div id="processesList_Layout" class="easyui-layout" data-options="fit:true,border:false">
									<div data-options="region:'west',split:true,minWidth:250,border:false" style="width: 250px;">
										<ul class='process-tree easyui-tree nochange' id='processTree' data-options='url:"<%=webContextName%>/main?cmd=getProcessData"'>
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
			if (Boolean.TRUE.equals(m.get("menu.archive"))) {
		%>
		<div id="archList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=rb.getString("webArchive")%></div>
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
											data-options='url:"<%=webContextName%>/main?cmd=getArchiveData"'>
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
						<div class="header"><%=rb.getString("webNSI")%></div>
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
											data-options='url:"<%=webContextName%>/main?cmd=getDictData"'>
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
						<div class="header"><%=rb.getString("webMonitor")%>
							<span id="processes_counter" class="badge badge-info processes_counter"></span>
							<input type="checkbox" id="task_checkbox"/><label id="select_all_label" style="margin-left: 5px;" title="Выделить все доступные"><%= rb.getString("webAllCheckedProc") %></label>
							<button id="disable_task" class="easyui-linkbutton">Выключить обновление списка</button>
							<button id="taskremoveprocess" style="display: none;" class="easyui-linkbutton c5"><%= rb.getString("webDeleteProcess") %></button>	
							<button id="usedmemory" class="easyui-linkbutton">Показать используемую память</button>	
							<label id="usedmemorylabel" style="margin-left: 5px;" title="Используемая память">Используемая память</label>
							<input id="taskSearchPage" class="filter-text" type="search" placeholder="<%=rb.getString("webSearchMain")%>">
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
						if (Boolean.TRUE.equals(m.get("menu.usersessions"))) {
		%>
		<div id="sessionsList"
			style="display: none; padding: 10px 30px 30px 10px;" class='appwin'>
			<table class="portlet-table" width="100%">
				<tr>
					<td>
						<div class="header"><%=rb.getString("webSessions")%>
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
						<div class="header"><%=rb.getString("webProfile")%></div>
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

													<div class="uRole prof"><%=rb.getString("webBirthday")%></div>
													<%=birthday%>


													<div class="uRole prof "><%=rb.getString("webRoles")%></div>
													<%
													    for (String role : roles) {
													%>
													<%=role%><br />
													<%
													    }
													%>
													<div class="uRole prof"><%=rb.getString("webPosition")%></div>
													<%=userPosition%>

													<div class="uRole prof"><%=rb.getString("webGO")%></div>
													<%=userGO%>

													<div class="uRole prof"><%=rb.getString("webContacts")%></div>
													<%=rb.getString("webTel")%>:
													<%=userPhoneIn%><br /> Email:
													<%=userEmail%>
												</div>
											</td>
											<td width="50%">
												<div class="pr">

													<div id="yourPhoto" class="Uphoto">
														<img id="yourImg" />
													</div>

													<div class="uRole prof"><%=rb.getString("webPass")%>
													</div>
													<div class="pasSm">
														<a href="javascript:changePwdDialog(false);"> <%=rb.getString("webPassChange")%>
														</a>
													</div>

													<input type="file" style="display: none;" id="yourUpload" />

													<div class="uRole prof"><%=rb.getString("webTooltips") %></div>
													<div id="uTooltip">
														<table class="utip">
															<tr>
																<td><%=rb.getString("webOn")%></td>
																<td><%=rb.getString("webOff")%></td>
															</tr>
															<tr>
																<td><input type="radio" name="nametool" value="1"
																	<%if (onToolTip) {%> checked="true" <%}%> /></td>
																<td><input type="radio" name="nametool" value="2"
																	<%if (!onToolTip) {%> checked="true" <%}%> /></td>
															</tr>
														</table>
													</div>
													<div class="uRole prof"><%=rb.getString("webBreadcrumbs")%></div>
													<div id="uBreadcrumpsOn">
														<table class="ubread">
															<tr>
																<td><%=rb.getString("webOn")%></td>
																<td><%=rb.getString("webOff")%></td>
															</tr>
															<tr>
																<td><input type="radio" name="namebread" value="1"
																	<%if (breadcrumpsOn) {%> checked="true" <%}%> /></td>
																<td><input type="radio" name="namebread" value="2"
																	<%if (!breadcrumpsOn) {%> checked="true" <%}%> /></td>
															</tr>
														</table>
													</div>
													<div class="uRole prof"><%=rb.getString("webLang")%></div>
													<div id="yourLang" class="ulang">
														<a id="langKz" href="#" <%if ("KZ".equals(lang)) {%>
															data-options="selected:true" <%}%>
															class="easyui-linkbutton"
															onclick="changeInterfaceLang('KZ')">Қазақша</a> <a
															id="langRu" href="#" <%if ("RU".equals(lang)) {%>
															data-options="selected:true" <%}%>
															class="easyui-linkbutton"
															onclick="changeInterfaceLang('RU')">Русский</a>
													</div>

													<!-- Контекстная менюшка для фотки -->
													<div id="mm" class="easyui-menu" style="width: 120px;">
														<div data-options="iconCls:'icon-arrow-down'"
															onclick="javascript:uploadYourImage(<%=userId%>);"><%=rb.getString("webPhotoLoad")%></div>
														<div data-options="iconCls:'icon-remove'"
															onclick="javascript:deleteImage();"><%=rb.getString("webPhotoDelete")%></div>
														<div data-options="iconCls:'icon-folder-open'"
															onclick="javascript:copyImageFromData();"><%=rb.getString("webPhotoTake")%></div>
													</div>

													<!-- Контекстная менюшка для деревьев -->
													<div id="treeMenu" class="easyui-menu" style="width: 120px;">
														<div onclick="makeTree('expand')" data-options="iconCls:'icon-expand'"><%=rb.getString("webExpand")%></div>
														<div onclick="makeTree('expandAll')" data-options="iconCls:'icon-expandAll'"><%=rb.getString("webExpandAll")%></div>
														<div class="menu-sep"></div>
														<div onclick="makeTree('collapse')" data-options="iconCls:'icon-collapse'"><%=rb.getString("webCollapse")%></div>
														<div onclick="makeTree('collapseAll')" data-options="iconCls:'icon-collapseAll'"><%=rb.getString("webCollapseAll")%></div>
													</div>
													<div id="procMenu" class="easyui-menu" style="width: 170px;">
														<div id="addToFavorites" data-options="iconCls:'icon-addToFavorites'"><%=rb.getString("webAddToFavorites")%></div>
													</div>
													<div id="procMenu2" class="easyui-menu" style="width: 170px;">
														<div id="removeFromFavorites" data-options="iconCls:'icon-removeFromFavorites'"><%=rb.getString("webRemoveFromFavorites")%></div>
													</div>														
													<!-- Контекстная менюшка для TreeTable -->
													<div id="treeTableMenu" class="easyui-menu"
														style="width: 120px;">
														<div onclick="makeTreeTable('expand')"
															data-options="iconCls:'icon-expand'"><%=rb.getString("webExpand")%></div>
														<div onclick="makeTreeTable('expandAll')"
															data-options="iconCls:'icon-expandAll'"><%=rb.getString("webExpandAll")%></div>
														<div class="menu-sep"></div>
														<div onclick="makeTreeTable('collapse')"
															data-options="iconCls:'icon-collapse'"><%=rb.getString("webCollapse")%></div>
														<div onclick="makeTreeTable('collapseAll')"
															data-options="iconCls:'icon-collapseAll'"><%=rb.getString("webCollapseAll")%></div>
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
				<a href="#ui=sessionsList&mode=tabs"><%=rb.getString("webSessions")%></a>
			</div>
			<%
			    }
			%>
			<div class="account_set">
				<%
				    if (Boolean.TRUE.equals(m.get("menu.profile"))) {
				%>
				<a onclick="forseSaveChanges();" href="#ui=profileWnd&mode=tabs" id=""
					class="btn btn-primary"> <i class="icon-user icon-white"></i> <%=rb.getString("webDetails")%></a>
				<%
				    }
				%>
				<a href="javascript:logout()"
					class="btn btn-danger btn-out"> <i
					class="icon-share-alt icon-white"></i> <%=rb.getString("webExit")%></a>
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
		var userId = <%=userId%>;
		var theme = <%=theme%>;
		var langCode = '<%= lang != null ? lang.toLowerCase() : "kz" %>';
		var startHash = '<%=startHash%>';
		var backPage = <%=backPage != null ? "'" + backPage + "'" : "null"%>;
		var onTool = <%=onToolTip%>;
		var breadcrumpsOn = <%=breadcrumpsOn%>;
		var notis = <%=Boolean.TRUE.equals(m.get("menu.notification"))%>;
		window.contextName = '<%=webContextName%>';

		var translation = {};
		translation['wait'] = '<%=rb.getString("webWait")%>';
		translation['passChange'] = '<%=rb.getString("webPassChangeTitle")%>';
		translation['passOld'] = '<%=rb.getString("webPassOld")%>';
		translation['passNew'] = '<%=rb.getString("webPassNew")%>';
		translation['passConfirm'] = '<%=rb.getString("webPassConfirm")%>';
		translation['ok'] = '<%=rb.getString("webDlgOk")%>';
		translation['cancel'] = '<%=rb.getString("webDlgCancel")%>';
		translation['close'] = '<%=rb.getString("webDlgClose")%>';
		translation['change'] = '<%=rb.getString("webDlgChange")%>';
		translation['save'] = '<%=rb.getString("weDlgSave")%>';
		translation['ignore'] = '<%=rb.getString("webDlgIgnore")%>';
		translation['continue'] = '<%=rb.getString("webDlgContinue")%>';
		translation['continue2'] = '<%=rb.getString("webDlgContinue2")%>';
		translation['errors'] = '<%=rb.getString("webErrors")%>';
		translation['saving'] = '<%=rb.getString("webSaving")%>';
		translation['canceling'] = '<%=rb.getString("webCanceling")%>';
		translation['error'] = '<%=rb.getString("error")%>';
		translation['stopProcess'] = '<%=rb.getString("webProcStop")%>';
		translation['removeProcess'] = '<%=rb.getString("webRemoveProcess")%>';
		translation['removeProcess2'] = '<%=rb.getString("webRemoveProcess2")%>';
		translation['askNextStep'] = '<%=rb.getString("webAskNextStep")%>';
		translation['ifcNotExistMessage'] = '<%=rb.getString("ifcNotExistMessage")%>';
		translation['deleting'] = '<%=rb.getString("webDeleting")%>';
		translation['alert'] = '<%=rb.getString("webAlert")%>';
		translation['sign'] = '<%=rb.getString("webDlgSign")%>';
		translation['keystore'] = '<%=rb.getString("webKeyStore")%>';
		translation['p12files'] = '<%=rb.getString("webPKCS12Files")%>';
		translation['enterPassword'] = '<%=rb.getString("enterPassword")%>';
		translation['lastSuccesProcessDef'] = '<%=rb.getString("webLastSuccesProcessDef")%>';
		translation['supportTeam'] = '<%=Funcs.sanitizeXml(rb.getString("supportTeam"))%>';

		var pageName = {};
		var pageId = {};
		pageName['ui_oldStartPage'] = '<%=rb.getString("webOldFlows")%>';
		pageId['ui_oldStartPage'] = -5;
		pageName['ui_startPage'] = '<%=rb.getString("webStartPage")%>';
		pageId['ui_startPage'] = -10;

		pageName['ui_Orders'] = '<%=rb.getString("webMonitor")%>';
		pageId['ui_Orders'] = -30;
		
		pageName['ui_OrdersNotification'] = '<%=rb.getString("webNotification")%>';
		pageId['ui_OrdersNotification'] = -40;
		
		pageName['ui_personInfo'] = '<%=rb.getString("webMyProfile")%>';
		pageId['ui_personInfo'] = -50;

		pageName['ui_process'] = '<%=rb.getString("webProcesses")%>';
		pageId['ui_process'] = -60;

		pageName['ui_staff'] = '<%=rb.getString("webShtat")%>';
		pageId['ui_staff'] = -70;

		pageName['ui_arch'] = '<%=rb.getString("webArchive")%>';
		pageId['ui_arch'] = -80;

		pageName['ui_dicts'] = '<%=rb.getString("webDicts")%>';
		pageId['ui_dicts'] = -90;

		pageName['ui_stat'] = 'Статистика';
		pageId['ui_stat'] = -100;

		pageName['ui_help'] = '<%=rb.getString("webHelp")%>';
		pageId['ui_help'] = -110;

		pageName['ui_right'] = '<%=rb.getString("webRights")%>';
		pageId['ui_right'] = -120;

		pageName['ui_actions'] = '<%=rb.getString("webUserAct")%>';
		pageId['ui_actions'] = -130;
		
		pageName['ui_admins'] = '<%=rb.getString("webAdmins")%>';
		pageId['ui_admins'] = -140;

		
	</script>
	<script type="text/javascript" src="<%=pathJS%>app-util.js?v=2016-11-27"></script>
	<script type="text/javascript" src="<%=pathJS%>app.js?v=2016-11-27"></script>
	<script type="text/javascript" src="<%=pathJS%>grid.editors.js?v=1"></script>
	<script type="text/javascript" src="<%=pathJS%>grid.formatter.js?v=2016-11-27"></script>
	<SCRIPT TYPE='text/javascript' SRC='<%= pathJS %>applet-load.js?v=2016-08-21'></SCRIPT>
</body>
</html>
<%
    }
%>
