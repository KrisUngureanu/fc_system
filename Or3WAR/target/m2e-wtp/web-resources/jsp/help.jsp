<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="kz.tamur.web.common.WebSession"%>
<%@ page import="kz.tamur.web.controller.WebController"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="kz.tamur.util.Funcs"%>

<%
    String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/jsp/media/css/";
    String pathJS = webContextName + "/jsp/media/js/";

	String guid = request.getParameter("guid");
	String helpId = request.getParameter("hid");
	String helpUid = request.getParameter("uid");
	String windowTitle = request.getParameter("title");
	Map<String, Object> s = WebController.getSession(request, guid);
	WebSession ws = (WebSession) s.get("ws");
	ResourceBundle rb = ws.getResource();
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
		<link rel="stylesheet" type="text/css" href="<%=pathCSS%>custom-easyui.css">
		<link rel="stylesheet" type="text/css" href="<%=pathCSS%>ul.css?v=2017-08-25">
		
		<script type="text/javascript">
				var guid = '<%=guid%>';
				var hid = '<%= (helpId != null) ? helpId : "" %>';
				var uid = '<%= (helpUid != null) ? helpUid : "" %>';
				window.contextName = '<%=webContextName%>';
		</script>
		
		<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
		<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js?v=2016-06-16"></script>
		<script type="text/javascript" src="<%=pathJS%>help.js"></script>
	</head>
	<body>
		<div class="pcontent easyui-panel" data-options="fit:true,border:false">
			<div id="help_layout" class="easyui-layout" data-options="fit:true,border:false">
				<div data-options="region:'west',split:true,minWidth:250,border:false" style="width: 250px;">
					<ul class='help-tree easyui-tree nochange' id='helpTree' data-options='url:"<%=webContextName%>/main?cmd=getHelpTree&hid=<%=helpId%>&uid=<%=helpUid%>&guid=<%=guid%>"'>
					</ul>
				</div>
				<div data-options="region:'center',border:false" id="help_body" style="padding: 5px; background: #fff;">
				</div>
			</div>
		</div>
	</body>
</html>