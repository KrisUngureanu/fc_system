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
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(request, guid);
	if (s != null) {
		WebSession ws = (WebSession) s.get("ws");
		if (ws != null) {
			ResourceBundle rb = ws.getResource();
			if (rb != null) {
%>

<table class="portlet-table" width="100%">
	<tr>
		<td>
    		<div class="header"><%= Funcs.sanitizeXml(rb.getString("webHelp")) %></div>
	    </td>
	</tr>
<tr>
	<td>
    	<div class="portlet">
    		<div class="pcontent easyui-panel" width="100%" data-options="fit:true,border:false">
	    	    <div id="helpWnd_tab" class="pcontent easyui-tabs" data-options="fit:true,border:false">
				</div>
		    </div>
    	</div>
    </td>
</tr>
</table>

<%
			}
		}
	}
%>