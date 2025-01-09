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
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(request, guid);
	WebSession ws = (WebSession) s.get("ws");
	String daysOldFlows = (String)s.get("daysOldFlows");
	ResourceBundle rb = ws.getResource();
%>

<div class="oldStartListTasks">
	<div class="warning_g"><%= Funcs.sanitizeXml(rb.getString("webTasksOldWarning")) %></div>
	<div class="warning_l"><%= Funcs.sanitizeXml(rb.getString("webTasksOldText1")) %><br />
<%= Funcs.sanitizeXml(rb.getString("webTasksOldText2")) %> <br />
<%= Funcs.sanitizeXml(rb.getString("webTasksOldText3")) %> <%= daysOldFlows %> <%= Funcs.sanitizeXml(rb.getString("webTasksOldText4")) %></div>
	<table style="border-spacing:0" width="100%" heigth="100%">
		<tbody>
			<tr>
			<td class="header">
				<div class="oldStartPanelPadding" > <%= Funcs.sanitizeXml(rb.getString("webTasksOld")) %> <span class="badge badge-info" id="ordersList_old_count"></span></div>
				<div class="sortmyProcess">
					<label><%= Funcs.sanitizeXml(rb.getString("webSort")) %></label>
						<select id="sortoldproc">
							<option value="0"><%= Funcs.sanitizeXml(rb.getString("webChronology")) %></option>
							<option value="1"><%= Funcs.sanitizeXml(rb.getString("webOverdue")) %></option>
						</select>
				</div>
				<div class="mymanagment">
						<input type="checkbox" id="old_checkbox"/><label style="margin-left: 5px;"><%= Funcs.sanitizeXml(rb.getString("webAllCheckedProc")) %></label>
						<button id="old_removeprocess" class="easyui-linkbutton c5 l-btn l-btn-small" style="display: none;">
							<span class="l-btn-left">
							<span class="l-btn-text" style="color: #fff"><%= Funcs.sanitizeXml(rb.getString("webDeleteProcess")) %></span>
							</span>
						</button>	
						<button id="old_return" class="easyui-linkbutton c1 l-btn l-btn-small" style="display: none;">
							<span class="l-btn-left">
							<span class="l-btn-text" style="color: #fff">Переход в штатный режим</span>
							</span>
						</button>	
				</div>
				<div class="filter" title="Сортировка">
						<a id="flt" href="javascript:void(0)">
							<div id="ui_filter"></div>
					 		<div id="ui_vniz"></div>
    		 	 		</a>
   		 	 	</div>
				<div>
						<input id="txtSearchPageOld" class="filter-text" type="search" placeholder="<%= Funcs.sanitizeXml(rb.getString("webSearchMain")) %>">
				</div>
				</td> 		
			</tr>
		</tbody>
	</table>
	<div id="tab_myOldProgectDoc" class="tabInfoOld b-m portlet" >
		<div class="pcontent easyui-panel" id="ordersList_old" data-options="fit:true,border:false"> </div>
	</div>
</div>
   	
<style>
.oldStartPanelPadding{
	float: left;
	line-height: 40px;
    margin-left: 60px;
	color: #333333;
	font-size: 16px;
}
.mymanagment,.sortmyProcess{
	float: left;
	line-height: 40px;
    margin-left: 60px;
    font-size:14px;
}
.warning_g{
	text-align: center;
	color:red;
	background:#E5E5E5;
    font-size:16px;
    font-weight:bold;
}
.warning_l{
	text-align: center;
	color:red;
	background:#E5E5E5;
    font-size:14px;
    font-weight:bold;
    font-style:italic;
}
.tabLine{
	border-left: 1px solid #d7d7d7;
	border-bottom: 1px solid #d7d7d7;
	padding: 0 0 1px 0;
    background: transparent;
    height: 39px
}
.oldStartListTasks{
	padding:10px 30px 30px 10px;
}
#oldStartPanelContent{
	cursor: pointer;
	background:#E5E5E5;
	border-top: 1px solid #d7d7d7;
	border-left: 1px solid #d7d7d7;
	padding:0;
 }
#oldStartPanelContent > ul {
	text-decoration: none;
	margin: 0;	
	overflow: auto;
	}
#oldStartPanelContent > li {
	text-decoration: none;
	margin: 0;
	padding: 10px 0 0 0;
	}
.b-m{
	margin-top:7px;
	border: 1px solid #d7d7d7;
}
#oldStartPanelContent .active{
	height:40px;
	background:#FFFFFF;
	border-bottom: none;
	}
#oldStartPanelContent ,.lp-dropdown-menu a,#left-panel-content a span,.lp-dropdown-menu a span
	{
	transition: color 0.2s ease 0s;
}
#oldStartPanelContent :hover,.lp-dropdown-menu a:hover,#left-panel-content .lp-dropdown-toggle.open,.lp-dropdown-menu .lp-dropdown-toggle.open,#left-panel-content a:hover span,.lp-dropdown-menu a:hover span,#left-panel-content .lp-dropdown-toggle.open span,.lp-dropdown-menu .lp-dropdown-toggle.open span
	{
	text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}
</style>	
