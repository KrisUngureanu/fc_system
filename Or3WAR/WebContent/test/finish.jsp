<%@page language="java" contentType="text/html; charset=utf-8"	pageEncoding="UTF-8"%>
<%@page import="kz.tamur.ekyzmet.test.ResultRecord"%>
<%@page import="java.util.List"%>
<%@page import="kz.tamur.ekyzmet.test.TestServlet"%>
<%@page import="kz.tamur.ekyzmet.test.Question"%>
<%@page import="kz.tamur.ekyzmet.test.Block"%>
<%@page import="kz.tamur.ekyzmet.test.SubSection"%>
<%@page import="kz.tamur.ekyzmet.test.Section"%>
<%@page import="kz.tamur.ekyzmet.test.UserAnswer"%>
<%@page import="java.util.Map"%>
<%@page import="kz.tamur.ekyzmet.test.Program"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	HttpSession hs = request.getSession();
	Boolean testComp = TestServlet.OBJ_DIRTYPE_COMP.equals(hs.getAttribute(TestServlet.HS_TEST_DIRTYPE));
	
	String status = (String)hs.getAttribute("userStatus");

	if (status == null || !"finish".equals(status)) {
		response.sendRedirect("login.jsp");
	}
	 String lang = (String)hs.getAttribute("langCode");
	 boolean ru = "ru".equals(lang);
	 
	String webContextName = request.getContextPath();

	boolean gmaTest = TestServlet.OBJ_DIRTYPE_GMA.equals(hs.getAttribute(TestServlet.HS_TEST_DIRTYPE));

	Program prg = (Program) hs.getAttribute("program");
	boolean result = TestServlet.OBJ_RES_PASSED.equals(hs.getAttribute(TestServlet.HS_RESULT));

	boolean erkTest = (Boolean)hs.getAttribute(TestServlet.HS_TEST_ERK);
%>
<c:set var="language" scope="session">
<%=lang %>
</c:set>
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" />
<!DOCTYPE html>
<html>
<head>
<title><% if (result) {%><fmt:message key="certificate" /><% } else { %><fmt:message key="report" /><% } %></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="media/testapp.css" rel="stylesheet" lang=""
	media="screen" />
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/counter.js" type="text/javascript"></script>
<script src="js/audiojs/audio.min.js" type="text/javascript"></script>
<script src="js/jquery.printPage.js" type="text/javascript"></script>
<script>
window.onload = function () {
    if (typeof history.pushState === "function") {
        history.pushState("testStarted", null, null);
        window.onpopstate = function () {
            history.pushState('testStarted', null, null);
        };
    }
    
}
</script>
<style>
.border {border-collapse: collapse;}
.border th,.border td{border:solid 1px #000; padding: 4px;}
</style>
</head>
<body>
<div style="font-size:12pt;width:60%;height:60%;position:absolute;left:20%;top:10%;">
<table border="0" width="100%">
<tbody><tr><td>
<fmt:message key="fio" />
: <b>${sessionScope.fio}</b>
</td></tr><tr><td>
<fmt:message key="iin" />
: <b>${sessionScope.iin}</b>
</td></tr><tr><td>
<fmt:message key="program" />
: <b>${sessionScope.programName}</b>
</td></tr>
</tbody></table>
<br>
<%
List<ResultRecord> resRecords = TestServlet.getResultRecords(hs);
if (resRecords != null && resRecords.size() > 0) { %>
<table style="font-size:10pt;border-collapse:collapse" border="1" cellpadding="3" cellspacing="0" width="100%" class="border">
<tbody>
<tr align="center">
<%if (gmaTest) {%>
	<td><i><fmt:message key="test_name.gma" /></i></td>
<%} else { %>
	<td><i><fmt:message key="test_name" /></i></td>
<%} %>
	<td style="width:80px"><i><fmt:message key="qcount" /></i></td>
<%if (!gmaTest) {%>
	<td style="width:80px"><i><fmt:message key="level" /></i></td>
<%} %>
	<td style="width:80px"><i><fmt:message key="correct" /></i></td>
</tr>
<%
	for (ResultRecord resRec : resRecords) {
%>
<%if (resRec.subSection != null) { %>
<tr>
	<td align="center"><%= ru ? resRec.subSection.nameRu : resRec.subSection.nameKz %></td>
	<td align="center"><%= resRec.qsnCount %></td>
	<%if (!gmaTest) { %>
		<td align="center"><%= resRec.level %></td>
	<%} %>
	<td align="center"><%= resRec.correctAwrCount %></td>
</tr>
<%} else { %>
<tr>
	<td align="center"><fmt:message key="result.subtotal" /></td>
	<td align="center"><%= resRec.qsnCount %></td>
	<%if (!gmaTest) { %>
		<td align="center"><%= resRec.level %></td>
	<%} %>
	<td align="center"><%= resRec.correctAwrCount %></td>
</tr>
<%} %>
<%
	}
 %>
</tbody></table>
<%} %>
<%if (!testComp) { %>
	<p align="right"><fmt:message key="total_result" />:<b>
	<%if (gmaTest) {%>
		${sessionScope.res_gma}
	<%} else {%>
		<%if (result) { %>
			<fmt:message key="passed" />
		<%} else { %>
			<fmt:message key="not_passed" />
		<%} %>
	<%} %>
	</b></p>
<%} %>
<p align="right">
<b>
<% if (result) { %>
<% if (erkTest) {%>
	<a href='<%=webContextName%>/test/action?cert' style="font-size:22px; margin-right: 20px;"><fmt:message key="print" /></a>
	<input id="exitBtn" name="docon" value="<fmt:message key="exit" />" style="width:100px;height:22px" type="button" onclick="exit()">
<% } else { %>
	<input class="btnPrint" href='<%=webContextName%>/test/action?cert' style="font-size:22px; margin-right: 20px;" type="button" value="<fmt:message key="print" />">
	<input id="exitBtn" name="docon" value="<fmt:message key="exit" />" style="width:100px;height:22px;display:none" type="button" onclick="exit()">
<% } %>
<% } else { %>
	<input id="exitBtn" name="docon" value="<fmt:message key="exit" />" style="width:100px;height:22px;" type="button" onclick="exit()">
<% } %>
</p>
</div>


<script>

  
$(document).ready(function() {
  $(".btnPrint").printPage({
	  onfinish : function() {$('#exitBtn').show();}
  });
  
});
function exit() {
	var source = '<%=webContextName%>/test/action';
	$.get(source, {
		'exit' : ''
	}, function(data) {

		window.location.href = "login.jsp";

	}, 'json');
}
</script>
</body>
</html>
