<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@page import="kz.tamur.ekyzmet.test.JspUtil"%>
<%@page import="kz.tamur.ekyzmet.test.TestServlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	HttpSession hs = request.getSession();
	String lang = (String) hs.getAttribute("langCode");
	String status = (String) hs.getAttribute("userStatus");

	if (status == null || !"work".equals(status)) {
		response.sendRedirect("login.jsp");
	}
	
	String picUrl = (String)request.getParameter("picture");
	JspUtil.savePicture(request);

	String webContextName = request.getContextPath();

	boolean gmaTest = TestServlet.OBJ_DIRTYPE_GMA.equals(hs.getAttribute(TestServlet.HS_TEST_DIRTYPE));
	boolean erkTest = (Boolean)hs.getAttribute(TestServlet.HS_TEST_ERK);
%>
<c:set var="language" scope="session"><%=lang %></c:set>
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" />
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="media/testapp.css" rel="stylesheet" lang=""
	media="screen" />
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/counter.js" type="text/javascript"></script>
<script src="js/audiojs/audio.min.js" type="text/javascript"></script>
<script type="text/javascript">
	var source = '<%=webContextName%>/test/action';
	function exit() {
		$.get(source, {
			'exit' : ''
		}, function(data) {
			window.location.href = "login.jsp";
		}, 'json');
	}

	$(function() {
		loadTests("<%=lang%>");		
	});

	function loadTests(lang) {
		$("#sections tbody").html("");
		$.get(source, {
			'tests' : '',
			'lang' : lang
		}, function(data) {
			if (data.length == 0) {
				$('#sections').hide();
			} else {
				$.each(data,function(i,sec) {
					var tr = "<tr><td>"+sec.title+"</td><td style='text-align:center'>"+sec.totalq+"</td>";
					<%if (!gmaTest) {%>
					tr += "<td  style='text-align:center'>"+sec.level+"</td>";
					<%} %>
					tr += "</tr>"
					$("#sections tbody").append(tr);
				});
			}
		}, 'json');
	}
</script>
</head>
<body>
<div style="font-size:10pt;width:95%; margin: 10px auto;">
<table style="width:98%;height:98%;" border="0" cellpadding="1" cellspacing="0">
<tbody>
<tr><td><img src="<%=picUrl%>" /></td></tr>
<tr><td style="font-size:14pt;color:Blue"><b>${sessionScope.fio}</b></td></tr>
<tr><td><fmt:message key="iin" />: <b>${sessionScope.iin}</b></td></tr>
<tr><td><fmt:message key="program" />:<b>${sessionScope.programName}</b></td></tr>
<%if (erkTest) { %>
<tr><td><fmt:message key="orgName" />:<b>${sessionScope.orgName}</b></td></tr>
<tr><td><fmt:message key="depName" />:<b>${sessionScope.depName}</b></td></tr>
<tr><td><fmt:message key="posName" />:<b>${sessionScope.posName}</b></td></tr>
<%} %>
<tr>
	<td>
		<table id="sections" cellpadding="2" cellspacing="0" width="100%">
			<thead>
				<tr>
					<%if (gmaTest) {%>
					<th><fmt:message key="test_name.gma" /></th>
					<%} else {%>
					<th><fmt:message key="test_name" /></th>
					<%} %>
					<th><fmt:message key="qcount" /></th>
					<%if (!gmaTest) {%>
					<th><fmt:message key="level" /></th>
					<%} %>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</td>
</tr>
<tr>
	<td><br></td>
</tr>
<tr>
	<td><br></td>
</tr>
<tr>
	<td align="center">
		<form method="post" action="pass.jsp">
			<input name="test" style="font-size:30px" value="<fmt:message key="testing" />" type="submit"><br><br>
			<button type="button" onclick="exit()" style="font-size:18px"><fmt:message key="exit" /></button>
		</form>
	</td>
</tr>
</tbody>
</table>
</div>

</body>
</body>
</html>
