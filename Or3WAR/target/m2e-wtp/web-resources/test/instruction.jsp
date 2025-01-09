<%@page import="java.io.ByteArrayInputStream"%>
<%@ page language="java" contentType="text/html;charset=utf-8"
	pageEncoding="UTF-8"%>
<%@page import="kz.tamur.ekyzmet.test.TestServlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	HttpSession hs = request.getSession();
	String lang = request.getParameter("setlang");
	String frompage = request.getParameter("page");
	if (lang != null) {
		hs.setAttribute("langCode", lang);
		hs.setAttribute("programName", hs.getAttribute("programName_" + lang));
		hs.setAttribute("orgName", hs.getAttribute("orgName_" + lang));
		hs.setAttribute("depName", hs.getAttribute("depName_" + lang));
		hs.setAttribute("posName", hs.getAttribute("posName_" + lang));
	} else {
		lang = (String)hs.getAttribute("langCode");
	}
	String yes = request.getParameter("docread");

	if ("help".equals(frompage)) {
		if (yes == null) {
			response.sendRedirect("lang.jsp");
		} else {
			response.sendRedirect("picture.jsp");
		}
	}
	String status = (String)hs.getAttribute("userStatus");

	if (status == null || !"work".equals(status)) {
		response.sendRedirect("login.jsp");
	}

	String instrContent = TestServlet.getInstruction(hs);
%>
<c:set var="language" scope="session"><%=lang %></c:set>
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" /><!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="media/testapp.css" rel="stylesheet" lang=""
	media="screen" />
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/counter.js" type="text/javascript"></script>
<script src="js/audiojs/audio.min.js" type="text/javascript"></script>

</head>
<body>
	<table border="0" cellpadding="0" cellspacing="0" height="98%"
		width="98%">
		<tbody>
			<tr>
				<td align="center" valign="middle">
				<% response.getWriter().write(instrContent); %>
				<br>
					<form method="post" action="instruction.jsp">
						<input type="hidden" value="help" name="page"> <input
							value="<%=lang%>" name="setlang" type="hidden"> <label
							style="font-size: 16pt"><input name="docread" value="yes"
							type="checkbox">&nbsp;<fmt:message key="help.familiarized" /></label>&nbsp;&nbsp;&nbsp;&nbsp;
						<fmt:message key="help.submit" var="help_submit"/>
						<input value="${help_submit}" style="font-size: 14pt" type="submit">
					</form>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
