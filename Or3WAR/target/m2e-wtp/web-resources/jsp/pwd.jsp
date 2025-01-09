<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="kz.tamur.web.common.WebSession"%>
<%@ page import="kz.tamur.web.common.CommonHelper"%>
<%@ page import="kz.tamur.web.controller.WebController"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="kz.tamur.util.Funcs"%>

<%
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(request, guid);
	WebSession ws = (WebSession) s.get("ws");
    String webPDOld;
    String webPDNew;
    String webPDConfirm;
    if (ws == null) {
        webPDOld = "Старый пароль";
        webPDNew = "Новый пароль";
        webPDConfirm = "Повтор пароля";
    } else {
        ResourceBundle rb = ws.getResource();
        if (rb == null) rb = CommonHelper.RESOURCE_RU;
        webPDOld = Funcs.sanitizeXml(rb.getString("webPassOld"));
        webPDNew = Funcs.sanitizeXml(rb.getString("webPassNew"));
        webPDConfirm = Funcs.sanitizeXml(rb.getString("webPassConfirm"));
    }
%>

<table>
	<tr>
		<td><span><%=webPDOld%></span></td>
		<td><input type="password" uid="oldPass" style="height: 24px; width: 150px;" /></td>
	</tr>
	<tr>
		<td><span><%=webPDNew%></span></td>
		<td><input type="password" uid="newPass" style="height: 24px; width: 150px;" /></td>
	</tr>
	<tr>
		<td><span><%=webPDConfirm%></span></td>
		<td><input type="password" uid="confirmPass" style="height: 24px; width: 150px;" /></td>
	</tr>
</table>