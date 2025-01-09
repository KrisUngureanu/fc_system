<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="kz.tamur.web.common.WebSession"%>
<%@ page import="kz.tamur.web.controller.WebController"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="kz.tamur.web.common.WebUser"%>
<%@ page import="kz.tamur.test.StartupServlet"%>
<%@ page import="kz.gov.pki.kalkan.util.encoders.Base64"%>
<%@ page import="kz.tamur.util.Funcs"%>
<%@ page import="kz.tamur.web.common.WebUtils"%>
<%@ page import="kz.tamur.comps.Constants"%>
<%@ page import="java.util.Stack"%>
<%@ page import="kz.tamur.rt.orlang.ClientOrLang"%>
<%@ page import="com.cifs.or2.kernel.KrnClass"%>
<%@ page import="com.cifs.or2.client.Kernel"%>
<%@ page import="kz.tamur.SecurityContextHolder"%>
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

<%
	// Для входа в систему с кирилическими именами и паролями	
	request.setCharacterEncoding("UTF-8");

	// Идентификатор сессии - не используем HttpSession (из-за прокси)
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(guid);

    String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/qyzmet/css/";
    String pathJS = webContextName + "/qyzmet/js/";
	String pathMedia = webContextName + "/qyzmet/media/img/";

    String userUID = (String) s.get("userUID");
    WebController.jspLog.info("userUID = " + userUID);

    if (userUID == null) {
    	WebUtils.includeResponse(request, response, "/qyzmet/login.jsp", Constants.MAX_DOC_SIZE);
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
        
        Kernel wkrn = ws.getKernel();
        if (SecurityContextHolder.getKernel() == null)
        	SecurityContextHolder.setKernel(wkrn);

        KrnClass cls = wkrn.getClassByName("ImageUtil");
        ClientOrLang orlang = new ClientOrLang(wkrn);
        List<Object> args = new ArrayList<>();
        args.add(userId);
        args.add(34);
        args.add(0);
        
		String img64 = (String) orlang.sexec(cls, cls, "getUserImage", args, new Stack<String>());

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
        List<String> roles = (List<String>) s.get("userRoles" + langId);
        if (roles == null) {
            roles = new ArrayList<String>();
        }

        String lang = Funcs.sanitizeHtml((String) s.get("langCode"));
        tmp = (Long) s.get("theme");
        long theme = (tmp != null) ? tmp : 1;

        Map<String, Object> m = (Map<String, Object>) s.get("options");
        //m.put("menu.main",true);

        String logoPath = (String) s.get("head.logo");

        String logoImg = "<img src='" + (logoPath == null ? webContextName + "/jsp/media/img/logo-0.png" : logoPath) + "'/>";

        boolean onToolTip = (Boolean) s.get("showTooltip");
        boolean useNoteSound = (Boolean) s.get("useNoteSound");
        boolean instantECP = (Boolean) s.get("instantECP");
        
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
		boolean activateNoteSound = ws.activateNoteSound();
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
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>bootstrap/easyui.css?2021-03-26">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>jquery.mCustomScrollbar.css">

<link rel="stylesheet" type="text/css" href="<%=pathCSS%>fonts.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>main.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>portlet.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>ui.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>orders.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>processes.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>icon.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>profile.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>archive.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>analytic.css?v=2023-02-28">
<link rel="stylesheet" type="text/css" href="<%=pathCSS%>help.css?v=2023-02-28">

<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js?2021-03-26"></script>
<script type="text/javascript" src="<%=pathJS%><%=lang%>.js?v=2020-04-09"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.blockUI.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>chart.js"></script>

<script type="text/javascript" src="<%=pathJS%>upload/vendor/jquery.ui.widget.js"></script>
<script type="text/javascript" src="<%=pathJS%>upload/jquery.iframe-transport.js"></script>
<script type="text/javascript" src="<%=pathJS%>upload/jquery.fileupload.js"></script>

<script type="text/javascript" src="<%=pathJS%>jquery.inputmask.bundle.min.js"></script>

</head>

<body>
	<div class="easyui-layout" data-options="fit:true">
		<div class="header" data-options="region:'north', border: false" style="height:92px;">
			<a href="<%= webContextName %>/qyzmet/index.jsp?guid=<%= guid %>" class="logo"><img src="css/img/logo-main.png" /></a>
			<div class="info">
				<!-- <span class="page-title"><i class="icon-page-main"></i><%=Funcs.sanitizeXml(rb.getString("webStartPage"))%></span> -->
				<!-- личное дело раньше было здесь -->
				<span class="department">
					<%=userGO%>
					<br>
					<% if(isAdmin) {%>
						
					<%} else {%>
							<%=userDepartment%>
					<%} %>	
				</span>
			</div>
			<div class="search">
				<i class="icon-text-search"></i>
            	<input id="privateDeal" class="text-search" type="text" placeholder="<%= ("KZ".equals(lang)) ? "Іздеу" : "Поиск" %>" onload="setFocus()">
            </div>
			<div class="lang-selector">
				<button id="topLangKz" class="lang-button<%= ("KZ".equals(lang)) ? " selected" : "" %>">KZ</button>
				<button id="topLangRu" class="lang-button<%= ("RU".equals(lang)) ? " selected" : "" %>">RU</button>
			</div>
		</div>
		<div class="left-side" data-options="region:'west', border: false" style="width:305px;">
			<div class="userInfo_container">
				<div class="user-info" style="margin: 0;">
					<div class="user-image">
					<% if (img64 != null && img64.length() > 0) { %>
						<img src="data:image/png;base64,<%= img64 %>" />
					<% } else { %>
						<img src="css/img/empty-avatar-34.png" />
					<% } %>
					</div>
					<div class="user-vert">
						<span class='user-name'><%=userName%></span>
						<span class='user-position'><%=userPosition%></span>
					</div>
					<i class="icon-arrow-down"></i>
				</div>
				<% if (showPing) { %>
				<div class="server-ping">
					<%=Funcs.sanitizeXml(rb.getString("pingTime"))%>: <span id="ping"></span> мс
				</div>
				<% } %>
			</div>
			<nav>
		     	<ul class="left-menu">
		        	<li class="selected" onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-main-page" href="#select=main-page"><i class="icon-menu-main"></i><%=Funcs.sanitizeXml(rb.getString("webStartPage"))%></a>
		        	</li>
<%
if (Boolean.TRUE.equals(m.get("menu.monitor"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-monitor" href="#select=monitor"><i class="icon-menu-monitor"></i><%=Funcs.sanitizeXml(rb.getString("webMonitor"))%></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.myinfo"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-personinfo" href="#select=personinfo&cmd=openArch&uid=1014162.3198690"><i class="icon-menu-shtat"></i><%=Funcs.sanitizeXml(rb.getString("webMyProfile"))%></a>
		        	</li>
<%
}
%>
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-orders" href="#select=orders"><i class="icon-menu-task"></i><%= ("KZ".equals(lang)) ? "Тапсырмалар" : "Задачи" %></a>
		        	</li>
<%
if (Boolean.TRUE.equals(m.get("menu.notification"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-notifications" href="#select=notifications"><i class="icon-menu-notification"></i><%=Funcs.sanitizeXml(rb.getString("webNotification"))%></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.process"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-processes" href="#select=processes"><i class="icon-menu-process"></i><%=Funcs.sanitizeXml(rb.getString("webProcesses"))%></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.shtat"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-shtat" href="#select=shtat&cmd=openArch&uid=1014162.3211302"><i class="icon-menu-shtat"></i><%=Funcs.sanitizeXml(rb.getString("webShtat"))%></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.userrights"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-rights" href="#select=rights&cmd=openArch&uid=1014162.3554408"><i class="icon-menu-shtat"></i><%=Funcs.sanitizeXml(rb.getString("webRights"))%></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.archive"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-archive" href="#select=archive"><i class="icon-menu-archive"></i><%= ("KZ".equals(lang)) ? "Электрондық қойма" : "Электронное хранилище" %></a>
		        	</li>
<%
}
if (Boolean.TRUE.equals(m.get("menu.dict"))) {
%>	
		        	<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-dicts" href="#select=dicts"><i class="icon-menu-dicts"></i><%= ("KZ".equals(lang)) ? "Анықтамалықтар" : "Справочники" %></a>
		        	</li>
<%
}
%>

		        	<!-- <li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-help" href="#select=help"><i class="icon-menu-shtat"></i><%=Funcs.sanitizeXml(rb.getString("webHelp"))%></a>
		        	</li> -->
					<li onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
		        		<a id="menu-grahicalSR" href="#select=grahicalSR&cmd=openArch&uid=1014162.35692440"><i class="icon-menu-shtat"></i><%= ("KZ".equals(lang)) ? "Графикалық ШО" : "Графическая ШР" %></a>
		        	</li>



				</ul>
			</nav>
			
			<div class="help">
				<i class="icon-help"></i>
				<span class='help-large'><%= ("KZ".equals(lang)) ? "Көмек керек пе?" : "Нужна помощь?" %></span>
				<span class='help-small'><%= ("KZ".equals(lang)) ? "FAQ оқыңыз" : "Пожалуйста прочтите FAQ" %></span>
				<button class='help-btn' onclick="forseSaveChanges();location.hash=$(this).find('a').attr('href')">
					<a id="menu-help" href="#select=help" style="text-decoration: none; color: #444444;"><%= ("KZ".equals(lang)) ? "НҰСҚАУЛАР" : "ИНСТРУКЦИИ" %></a>
				</button>
			</div>
			<!-- User был здесь Жаркын 10.02.2023 -->
		</div>
		<div class="app-panel" data-options="region:'center', border: false">
		 	<div id="main-page-panel" class="top-view-panel easyui-layout" data-options="fit:true">
				<div class="widgets-panel" data-options="region:'north', border: false" style="height:100px;">
					<div class="widget-row">
						<div class="widget-col" style="width:20%">
							<div class="widget widget-light" id="stazhGov" cls="MainPage" method="getStazhGossluzhby"></div>
						</div>
						<div class="widget-col" style="width:22%">
							<div class="widget widget-light" id="stazhOrg" cls="MainPage" method="getStazhOrganization"></div>
						</div>
						<div class="widget-col" style="width:24%">
							<div class="widget widget-light" id="stazhDolj" cls="MainPage" method="getStazhDoljnosty"></div>
						</div>
						<div class="widget-col" style="width:34%">
							<div class="widget widget-dark" id="otpuskDays" cls="MainPage" method="getVacationDays"></div>
						</div>
					</div>
				</div>
				<div class="portlets-panel-left" data-options="region:'west', border: false" style="width:33%;">
				    <div class="portlet-col">
						<div class="portlet-row-1-2">
							<div class="portlet portlet-transparent" id="freqUsedProcs" cls="MainPage" method="getFrequentlyUsedProcesses">
								<div class="portletTitle">
									<span><%= ("KZ".equals(lang)) ? "Ең жиі қолданылатын процестер 1" : "Наиболее часто используемые процессы 1" %></span>
									<i class="collapse-portlet icon-arrows-up"></i>
								</div>
								<div class="portletBody">
								</div>
							</div>
						</div>
						<div class="portlet-row-1-2">
							<div class="portlet portlet-light" id="ads" cls="MainPage" method="getAds">
								<div class="portletTitle">
									<span><%= ("KZ".equals(lang)) ? "Хабарландырулар 1" : "Объявления 1" %></span>
									<i class="collapse-portlet icon-arrows-up"></i>
								</div>
								<div class="portletBody">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="portlets-panel-center" data-options="region:'center', border: false" style="width:33%;">
				    <div class="portlet-col">
				    	<div class="portlet portlet-light" id="orders" cls="MainPage" method="getUserOrders">
				    		<!-- <div class="portletTitle">
								<span><%= ("KZ".equals(lang)) ? "Тапсырмалар 1" : "Задачи 1" %></span>
								<i class="collapse-portlet icon-arrows-up"></i>
							</div> -->
							<!-- <div class="ordersLeftButton"><i class="collapse-portlet icon-arrows-left"></i></div>
							<div class="ordersRightButton"><i class="collapse-portlet icon-arrows-right"></i></div> -->
							<div class="portletBody">
							</div>
				    	</div>
				    </div>
				</div>
				<div class="portlets-panel-right" data-options="region:'east', border: false" style="width:33%;">
				    <div class="portlet-col">
				    <div class="portlet-row-1-2">
						<div class="portlet portlet-light" id="statistics" cls="MainPage" method="getStatistics">
				    		<div class="portletTitle">
								<span>Статистика 1</span>
								<i class="collapse-portlet icon-arrows-up"></i>
							</div>
							<div class="portletBody">
					    	</div>
						</div>
					</div>
					<div class="portlet-row-1-2">
						<div class="portlet portlet-light" id="vacancies" cls="MainPage" method="getVacancies">
							<div class="portletTitle">
								<span><%= ("KZ".equals(lang)) ? "Бос жұмыс орындары 1" : "Вакансии 1" %></span>
								<i class="collapse-portlet icon-arrows-up"></i>
							</div>
							<div class="portletBody">
							</div>
						</div>
					</div>
					</div>
				</div>
			</div>
			
			<div id="ui-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div data-options="region:'north',split:false,border:false" style="height: 120px;" id="ui-header">
					<div id="ui-title" class="header ui-title" style="display: none;">
						<%= ("KZ".equals(lang)) ? "Интерфейс атауы" : "Название интерфейса" %>
					</div>
					<div id="ui-breadcrump" class="header header-t"></div>
					<div id="uiToolbar" class="ui-toolbar" style="display: none;">
						<div style="padding: 5px; display: inline;">
							<a class="easyui-linkbutton"
								data-options="iconCls:'icon-save',disabled:true"
								onclick="saveChanges()" id="saveBtn"><%=Funcs.sanitizeXml(rb.getString("save"))%></a>
							<a class="easyui-linkbutton"
								data-options="iconCls:'icon-undo',disabled:true" id="cancelBtn"
								onclick="resetChanges()"><%=Funcs.sanitizeXml(rb.getString("cancelChangesShort"))%></a>
							<a class="easyui-linkbutton"
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
				<div data-options="region:'center',border:false" style="min-height: 300px;">
					<div id="ui-body"></div> 
				</div>
			</div>
<%
if (Boolean.TRUE.equals(m.get("menu.monitor"))) {
%>			
			<div id="monitor-panel" class="top-view-panel easyui-panel" data-options="fit:true,border:false" style="display: none;">
				<div class="monitor-panel-header">
					<div class="monitor-panel-title"><%= ("KZ".equals(lang)) ? "Үдеріс мониторы" : "Монитор процессов" %></div>
					<span id="monitor-total-counter" class="badge badge-info"></span>
					<div class="monitor-panel-toolbar">
						<input id="monitor-search-text" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">
					</div>
				</div>
				<div id="monitor-pager" class="monitor-pager" style="width:100%;"></div>
				
				<div class="monitor-table">
					<div class="pcontent easyui-panel" data-options="fit:true,border:false"></div>
				</div>
			</div>
<%
}
%>
			<div id="orders-panel" class="top-view-panel easyui-panel" data-options="fit:true,border:false" style="display: none;">
				<div class="orders-header">
					<div class="active orders-tab" tab="my">
						<i class="icon-orders-my"></i>
						<div class="orders-tab-title"><%=Funcs.sanitizeXml(rb.getString("webTasksMy"))%></div>
						<span class="badge badge-info" id="ordersList_my_count"></span>
					</div>
					<div class="orders-tab" tab="in">
						<i class="icon-orders-in"></i>
						<div class="orders-tab-title"><%=Funcs.sanitizeXml(rb.getString("webTasksIn"))%></div>
						<span class="badge badge-important" id="ordersList_in_fire_count"></span>
						<span class="badge badge-info" id="ordersList_in_count"></span>
					</div>
					<div class="orders-tab" tab="out">
						<i class="icon-orders-out"></i>
						<div class="orders-tab-title"><%=Funcs.sanitizeXml(rb.getString("webTasksOut"))%></div>
						<span class="badge badge-info" id="ordersList_out_count"></span>
					</div>
				</div>
				
				<div id="orders-panel-my" class="orders-body" >
					<div class="pcontent easyui-panel" id="ordersList_my" data-options="fit:true,border:false"> </div>
				</div>
				<div id="orders-panel-in" class="orders-body" style="display: none">
					<div class="pcontent easyui-panel" id="ordersList_in" data-options="fit:true,border:false"> </div>
				</div>
				<div id="orders-panel-out" class="orders-body" style="display: none">
					<div class="pcontent easyui-panel" id="ordersList_out" data-options="fit:true,border:false"> </div>
			   	</div>
			</div>
<%
if (Boolean.TRUE.equals(m.get("menu.notification"))) {
%>
			<div id="notifications-panel" class="top-view-panel easyui-panel" data-options="fit:true,border:false" style="display: none;">
				<div class="notifications-panel-header">
					<div class="notifications-panel-title"><%= ("KZ".equals(lang)) ? "Хабарламалар" : "Уведомления" %></div>
					<span id="notifications-unread-counter" class="badge badge-important"></span>
					<span id="notifications-total-counter" class="badge badge-info"></span>
				</div>
				<div class="notifications-panel-toolbar">
					<label class="notifications-toolbar-label"><%= ("KZ".equals(lang)) ? "Хабарламаларды алу кезеңі:" : "Период получения уведомлений:" %></label>
					<label class="notifications-toolbar-label"><%= ("KZ".equals(lang)) ? "Басталу күні" : "Дата начала" %></label>
					<input class="easyui-datebox" id="dateN1" style="width:100px;height:22px;">
					<label class="notifications-toolbar-label"><%= ("KZ".equals(lang)) ? "Аяқталу күні" : "Дата конца" %></label>
					<input class="easyui-datebox" id="dateN2" style="width:100px;height:22px;">
					<label class="notifications-toolbar-label"><%= ("KZ".equals(lang)) ? "Белгісі:" : "Признак:" %></label>
					<select id="notifications-select-type">
						<option value="0" selected><%=Funcs.sanitizeXml(rb.getString("webAll"))%></option>
						<option value="1"><%=Funcs.sanitizeXml(rb.getString("webViewed"))%></option>
						<option value="2"><%=Funcs.sanitizeXml(rb.getString("webUnreviewed"))%></option>
					</select>
					<button id="filterNoti" onclick="filterNotification()" class="notifications-toolbar-btn-filter easyui-linkbutton c1 l-btn l-btn-small"><i class="icon-filter"></i><%= ("KZ".equals(lang)) ? " Сүзу" : " Фильтрация" %></button>
					<button id="filterClean" onclick="cleanNotification()" class="notifications-toolbar-btn-clear easyui-linkbutton c1 l-btn l-btn-small"><i class="icon-clear"></i> <%= ("KZ".equals(lang)) ? "Тазалау" : "Очистить" %></button>
					<input id="notifications-search-text" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">
				</div>
				<div id="notifications-pager" class="notifications-pager" style="width:100%;"></div>
				<div class="notifications-table-header">
					<div id="sortTitle" title="Сортировать по заголовоку" onclick="sortColumnNotif(sortTitle)"><%=Funcs.sanitizeXml(rb.getString("webNotificationTitle"))%></div>
					<div id="sortFrom" title="Сортировать по отправителю" onclick="sortColumnNotif(sortFrom)"><%=Funcs.sanitizeXml(rb.getString("webNotificationFrom"))%></div>
					<div id="sortInDate" title="Сортировать по дате получения" onclick="sortColumnNotif(sortInDate)" class="sortNotif asc"><%=Funcs.sanitizeXml(rb.getString("webNotificationInDate"))%></div>
					<div id="sortOpenDate" title="Сортировать по дате открытия" onclick="sortColumnNotif(sortOpenDate)"><%=Funcs.sanitizeXml(rb.getString("webNotificationOpenDate"))%></div>
					<div id="sortAwereDate" title="Сортировать по дате ознакомления" onclick="sortColumnNotif(sortAwereDate)"><%=Funcs.sanitizeXml(rb.getString("webNotificationAwereDate"))%></div>
				</div>
				
				<div class="notifications-table">
					<div class="pcontent easyui-panel" data-options="fit:true,border:false"></div>
				</div>
			</div>
<%
}

if (Boolean.TRUE.equals(m.get("menu.process"))) {
%>

			<div id="processes-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div class="process-header">
					<span><%=Funcs.sanitizeXml(rb.getString("webAvailProc"))%></span>
					<input id="processSearchPage" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">					
				</div>
				<div class="pcontent easyui-panel" data-options="fit:true,border:false">
					<div id="processesList_Layout" class="easyui-layout" data-options="fit:true,border:false">
						<div data-options="region:'west',split:true,minWidth:250,border:false" id="processesList_Tree" style="width: 320px;">
							<ul class='process-tree easyui-tree nochange' id='processTree' data-options='url:"<%=webContextName%>/main?cmd=getProcessData&guid=<%=guid%>"'>
							</ul>
						</div>
						<div data-options="region:'center',border:false" id="processesList_body"></div>
					</div>
				</div>
			</div>

			<!-- div id="shtat-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div class="portletTitle">
					<span>Штатка</span>
				</div>
			</div-->
<%
}

if (Boolean.TRUE.equals(m.get("menu.archive"))) {
%>

			<div id="archive-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div class="process-header">
					<span><%=Funcs.sanitizeXml(rb.getString("webArchive"))%></span>
					<input id="archiveSearchPage" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">					
				</div>
				<div class="pcontent easyui-panel" data-options="fit:true,border:false">
					<div id="archiveList_Layout" class="easyui-layout" data-options="fit:true,border:false">
						<div data-options="region:'west',split:true,minWidth:250,border:false" id="archiveList_Tree" style="width: 320px;">
							<ul class='archive-tree easyui-tree nochange' id='archiveTree' data-options='url:"<%=webContextName%>/main?cmd=getArchiveData&guid=<%=guid%>"'>
							</ul>
						</div>
						<div data-options="region:'center',border:false" id="archiveList_body"></div>
					</div>
				</div>
			</div>
<%
}

if (Boolean.TRUE.equals(m.get("menu.dict"))) {
%>

			<div id="dicts-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div class="process-header">
					<span><%=Funcs.sanitizeXml(rb.getString("webNSI"))%></span>
					<input id="dictsSearchPage" class="filter-text" type="search" placeholder="<%=Funcs.sanitizeXml(rb.getString("webSearchMain"))%>">					
				</div>
				<div class="pcontent easyui-panel" data-options="fit:true,border:false">
					<div id="dictsList_Layout" class="easyui-layout" data-options="fit:true,border:false">
						<div data-options="region:'west',split:true,minWidth:250,border:false" id="archiveList_Tree" style="width: 320px;">
							<ul class='dicts-tree easyui-tree nochange' id='dictsTree' data-options='url:"<%=webContextName%>/main?cmd=getDictData&guid=<%=guid%>"'>
							</ul>
						</div>
						<div data-options="region:'center',border:false" id="dictsList_body"></div>
					</div>
				</div>
			</div>
<%
}
%>
			<div id="profile-panel" class="top-view-panel easyui-panel" data-options="fit:true,border:false" style="display: none;">
			
				<div class="profile-top">
					<div class="profile-section-top-left">
						<div class="profile-field-title" title=""><%= ("KZ".equals(lang)) ? "Пайдаланушы" : "Пользователь" %></div>
						<div class="profile-field-name" title=""><%=userName%></div>
						
						<!-- <div class="profile-field-title-normal"><%=Funcs.sanitizeXml(rb.getString("webBirthday"))%></div> -->
						<div class="profile-field-container">
							<div class="profile-field-title-normal"><%=userPosition%></div>
							<div class="profile-field-normal" style="padding-left: 15px;"><%=birthday%></div>
						</div>
					</div>
					
					<div class="profile-section-top-right">
						<div class="profile-photo-buttons">
							<a id="change-my-photo" href="#" class="easyui-linkbutton"><%= ("KZ".equals(lang)) ? "Фотосуретті жүктеу" : "Загрузить фото" %></a>
							<a id="delete-my-photo" href="#" class="easyui-linkbutton"><%= ("KZ".equals(lang)) ? "Фотосуретті жою" : "Удалить фото" %></a>
							<a id="take-photo-from-sys" href="#" class="easyui-linkbutton"><%= ("KZ".equals(lang)) ? "Жеке істен алыңыз" : "Взять из личного дела" %></a>
						</div>
						
						<div class="profile-photo-section">
							<div id="my-photo" class="profile-photo">
								<img id="my-image" />
							</div>
							<input type="file" style="display: none;" id="my-photo-upload" />
						</div>
					</div>
				</div>				
			
				<div class="profile-tabs">
					<div class="active profile-tab" tab="info">
						<div class="profile-tab-title"><%= ("KZ".equals(lang)) ? "Жалпы ақпарат" : "Общая информация" %></div>
					</div>
					<div class="profile-tab" tab="settings">
						<div class="profile-tab-title"><%= ("KZ".equals(lang)) ? "Параметрлер" : "Настройки" %></div>
					</div>
					<div class="profile-tab" tab="themes">
						<div class="profile-tab-title"><%= ("KZ".equals(lang)) ? "Тақырыптар" : "Темы" %></div>
					</div>
					<!-- <div class="profile-tab" tab="security">
						<div class="profile-tab-title"><%= ("KZ".equals(lang)) ? "Қауіпсіздік" : "Безопасность" %></div>
					</div> -->
					<div class="profile-tab" tab="contacts">
						<div class="profile-tab-title"><%= ("KZ".equals(lang)) ? "Байланыс" : "Контакты" %></div>
					</div>
				</div>
				
				<div id="profile-panel-info" class="profile-body" >
					<div class="profile-roles">
						<div class="profile-section-title">
							<%=Funcs.sanitizeXml(rb.getString("webRoles"))%>
						</div>
					
<%
						    for (String role : roles) {
%>
								<div class="profile-field-normal"><%=Funcs.sanitizeXml(role)%></div>
<%
						    }
%>
					</div>
						
						
					
					<div class="profile-position">
						<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webPosition"))%></div>
						<div class="profile-field-normal"><%=userPosition%></div>
					</div>
					<div class="profile-workplace">
						<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webGO"))%></div>
						<div class="profile-field-normal"><%=userGO%></div>
					</div>
					<div class="profile-ifc-lang">
						<div class="profile-section-title">
							<%=Funcs.sanitizeXml(rb.getString("webLang"))%>
						</div>
						<div class="profile-section-lang">
							<a id="langKz" class="lang-button-profile" href="#" <%if ("KZ".equals(lang)) {%>
								data-options="selected:true" <%}%>
								class="easyui-linkbutton">Қазақша</a>
							<a id="langRu" class="lang-button-profile" href="#" <%if ("RU".equals(lang)) {%>
								data-options="selected:true" <%}%>
								class="easyui-linkbutton">Русский</a>
						</div>
					</div>
				</div>
				<div id="profile-panel-settings" class="profile-body" style="display: none">
					<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webPass"))%>
					</div>
					<div class="pasSm">
						<a href="javascript:changePwdDialog(false);"> <%=Funcs.sanitizeXml(rb.getString("webPassChange"))%>
						</a>
					</div>

					<input type="file" style="display: none;" id="yourUpload" />
													
					<% 
					if ("true".equals(System.getProperty("showHintOfProcess"))) {  
					%>

					<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webTooltips")) %></div>
					<div id="uTooltip">
						<table class="utip">
							<tr>
								<td><%=Funcs.sanitizeXml(rb.getString("webOn"))%></td>
								<td><%=Funcs.sanitizeXml(rb.getString("webOff"))%></td>
							</tr>
							<tr>
								<td><input onclick="changeTooltipPref('true')" type="radio" name="nametool" value="1"
									<%if (onToolTip) {%> checked="true" <%}%> /></td>
								<td><input onclick="changeTooltipPref('false')" type="radio" name="nametool" value="2" 
									<%if (!onToolTip) {%> checked="true" <%}%> /></td>
							</tr>
						</table>
					</div>
					<%} else { onToolTip = false; }%>
						<% 
						if (activateNoteSound) {  
						%>

					<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webNoteSound")) %></div>
					<div id="uNoteSound">
						<table class="uNoteSnd">
							<tr>
								<td><%=Funcs.sanitizeXml(rb.getString("webOn"))%></td>
								<td><%=Funcs.sanitizeXml(rb.getString("webOff"))%></td>
							</tr>
							<tr>
								<td><input onclick="changeNoteSoundPref('true')" type="radio" name="nameNoteSound" value="1"
									<%if (useNoteSound) {%> checked="true" <%}%> /></td>
								<td><input onclick="changeNoteSoundPref('false')" type="radio" name="nameNoteSound" value="2" 
									<%if (!useNoteSound) {%> checked="true" <%}%> /></td>
							</tr>
						</table>
					</div>
					<%} else { useNoteSound = false; }%>
				
					<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webInstantECP")) %></div>
					<div id="uInstantECP">
						<table class="uiecp">
							<tr>
								<td><%=Funcs.sanitizeXml(rb.getString("webOn"))%></td>
								<td><%=Funcs.sanitizeXml(rb.getString("webOff"))%></td>
							</tr>
							<tr>
								<td><input onclick="changeInstantECPPref(true)" type="radio" name="nameiecp" value="1"
									<%if (instantECP) {%> checked="true" <%}%> /></td>
								<td><input onclick="changeInstantECPPref(false)" type="radio" name="nameiecp" value="2" 
									<%if (!instantECP) {%> checked="true" <%}%> /></td>
							</tr>
						</table>
					</div>
					<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webBreadcrumbs"))%></div>
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
				</div>
				<div id="profile-panel-themes" class="profile-body" style="display: none">
					<div class="profile-section-title">
						<table class="utheme">
							<tr>
								<td><input type="radio" name="theme" value="1"
									<%if (theme == 1) {%> checked="true" <%}%> /></td>
								<td><img alt="Тема 1" src="<%=pathMedia%>ekyzmetDesign1.jpg" />
								</td>
								<!-- <td><input type="radio" name="theme" value="2"
									<%if (theme == 2) {%> checked="true" <%}%> /></td>
								<td><img alt="Тема 2" src="<%=pathMedia%>theme2.png" />
								</td>
								<td><input type="radio" name="theme" value="3"
									<%if (theme == 3) {%> checked="true" <%}%> /></td>
								<td><img alt="Тема 3" src="<%=pathMedia%>theme3.png" />
								</td> -->
							</tr>
						</table>
					</div>
			   	</div>
				<!-- <div id="profile-panel-security" class="profile-body" style="display: none">
			   	</div> -->
				<div id="profile-panel-contacts" class="profile-body" style="display: none">
					<div class="profile-contacts">
						<div class="profile-section-title"><%=Funcs.sanitizeXml(rb.getString("webContacts"))%></div>
						<div class="profile-field-title-normal" title=""><%=Funcs.sanitizeXml(rb.getString("webTel"))%></div>
						<div class="profile-field-normal"><%=userPhoneIn%></div>
						<div class="profile-field-title-normal" title="">Email</div>
						<div class="profile-field-normal"><%=userEmail%></div>
					</div>
			   	</div>
			</div>

<%
if (Boolean.TRUE.equals(m.get("menu.help"))) {
%>
			<div id="help-panel" class="top-view-panel easyui-layout" data-options="fit:true,border:false" style="display: none;">
				<div class="help-header">
					<span><%=Funcs.sanitizeXml(rb.getString("webHelp"))%></span>
				</div>
				<div class="pcontent easyui-panel" data-options="fit:true,border:false">
		    	    <div id="help-tab-panel" class="pcontent" data-options="fit:true,border:false">
					</div>
				</div>
			</div>
<%
}
%>
		</div>
	</div>
	
	<div class="popup_user_menu">
		<div class="user-menu-item">
			<a id="menu-profile" onclick="forseSaveChanges();" href="#select=profile"><%= ("KZ".equals(lang)) ? "Жеке кабинет" : "Личный кабинет" %></a>
		</div>
		<div class="user-menu-item">
			<a onclick="forseSaveChanges();" href="#cmd=exit"><%=Funcs.sanitizeXml(rb.getString("webExit"))%></a>
		</div>
	</div>

	<!-- Контекстная менюшка для деревьев -->
	<div id="treeMenu" class="easyui-menu" style="width: 120px;">
		<div onclick="makeTree('expand')" data-options="iconCls:'icon-expand'"><%=Funcs.sanitizeXml(rb.getString("webExpand"))%></div>
		<div onclick="makeTree('expandAll')" data-options="iconCls:'icon-expandAll'"><%=Funcs.sanitizeXml(rb.getString("webExpandAll"))%></div>
		<div class="menu-sep"></div>
		<div onclick="makeTree('collapse')" data-options="iconCls:'icon-collapse'"><%=Funcs.sanitizeXml(rb.getString("webCollapse"))%></div>
		<div onclick="makeTree('collapseAll')" data-options="name:'collapseAll',iconCls:'icon-collapseAll'"><%=Funcs.sanitizeXml(rb.getString("webCollapseAll"))%></div>
	</div>
	<!-- Контекстная менюшка для TreeTable -->
	<div id="treeTableMenu" class="easyui-menu"
		style="width: 120px;">
		<div onclick="makeTreeTable('expand')" data-options="iconCls:'icon-expand'"><%=Funcs.sanitizeXml(rb.getString("webExpand"))%></div>
		<div onclick="makeTreeTable('expandAll')" data-options="iconCls:'icon-expandAll'"><%=Funcs.sanitizeXml(rb.getString("webExpandAll"))%></div>
		<div class="menu-sep"></div>
		<div onclick="makeTreeTable('collapse')" data-options="iconCls:'icon-collapse'"><%=Funcs.sanitizeXml(rb.getString("webCollapse"))%></div>
		<div onclick="makeTreeTable('collapseAll')" data-options="iconCls:'icon-collapseAll'"><%=Funcs.sanitizeXml(rb.getString("webCollapseAll"))%></div>
	</div>

	
	<!-- script type="module" src="<%=pathJS%>main.js"></script-->
	<script type="text/javascript" src="<%=pathJS%>amcharts/core.js"></script>
	<script type="text/javascript" src="<%=pathJS%>amcharts/charts.js"></script>
	<script type="text/javascript" src="<%=pathJS%>amcharts/animated.js"></script>
	<script type="text/javascript" src="<%=pathJS%>amcharts/ru_RU.js"></script>
		
	<script type="text/javascript" src="<%=pathJS%>or3-module.js?v=2023-02-28"></script>
	<script type="text/javascript" src="<%=pathJS%>easyuiDefaults.js?v=2023-02-28"></script>
	
	<script type="text/javascript">
		var guid = '<%=guid%>';
		var userId = <%=userId%>;
		var langCode = '<%= lang != null ? lang.toLowerCase() : "kz" %>';
		window.contextName = '<%=webContextName%>';
		window.mainUrl = window.contextName + "/main?guid=" + guid;
		
		var instantECP = <%=instantECP%>;
		
	</script>

	<script type="text/javascript" src="<%=pathJS%>temps.js?v=2023-02-28"></script>
	<script type="text/javascript" src="<%=pathJS%>grid.formatter.js?v=2023-02-28"></script>
	<script type="text/javascript" src="<%= pathJS %>tumsocket_or3.js?v=2023-02-27"></script>
	
</body>
</html>
<%
    }
%>
