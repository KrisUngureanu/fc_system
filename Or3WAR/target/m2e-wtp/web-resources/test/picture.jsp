<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@page import="kz.tamur.ekyzmet.test.TestServlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	HttpSession hs = request.getSession();
	String status = (String)hs.getAttribute("userStatus");

	if (status == null || !"work".equals(status)) {
		response.sendRedirect("login.jsp");
	}
	
	boolean gmaTest = TestServlet.OBJ_DIRTYPE_GMA.equals(hs.getAttribute(TestServlet.HS_TEST_DIRTYPE));
%>
<fmt:setLocale value="${sessionScope.langCode}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" />
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" href="media/testapp.css" rel="stylesheet" lang="" media="screen" />
<script src="js/jquery.min.js"></script>
<script src="js/capture.js" type="text/javascript"></script>
</head>
<body>
	<table style="font-size: 12pt;" border="0" height="97%" width="100%">
		<tbody>
			<tr style="height: 60px">
				<td>
					<p style="font-weigh:bold;font-size:18pt;" align="center"><fmt:message key="picture.instructionLabel"/></p>
					<p style="font-size:14pt;" align="center"><fmt:message key="picture.instructionPreamble"/></p>
					<p style="font-size:14pt;"><fmt:message key="picture.instructionText"/></p>
				</td>
			</tr>
			<tr>
				<td style="font-size: 16pt; vertical-align: middle;" colspan="2" align="center">
					<div id="thumb">
						<img id="thumbPicture" src="media/img/pic-thumb.png" />
						<p id="thumbLabel"><fmt:message key="picture.example" /></p>
					</div>
					<div class="output">
						<div class="camera">
	    					<video id="video">Video stream not available.</video>
	  					</div>
    					<button id="startbutton"><fmt:message key="picture.takePicture" /></button>
  					</div>
  					<canvas id="canvas"></canvas>
  					<div class="output">
    					<img id="photo" alt="The screen capture will appear in this box." />
  					</div>
  					<div class="buttons">
    					<button id="deleteButton"><fmt:message key="picture.deletePicture" /></button>
						<form method="post" action="index.jsp">
							<input id="picture" type="hidden" name="picture">
							<fmt:message key="picture.submit" var="submitLabel"/>
							<input id="submit" type="submit" value="${submitLabel}" style="font-size: 14pt" disabled>
						</form>
  					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" valign="bottom"><hr></td>
			</tr>
			<tr style="height: 60px">
				<td style="color:red;font-size:14pt;" align="left">
					<p><fmt:message key="picture.warnLabel"/></p>
					<%if (gmaTest) {%>
						<p><fmt:message key="picture.gma.warnText"/></p>
					<%} else {%>
						<p><fmt:message key="picture.warnText"/></p>
					<%}%>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
