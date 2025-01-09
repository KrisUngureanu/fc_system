<%@page import="kz.tamur.ekyzmet.test.TestServlet"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%
	HttpSession hs = request.getSession();
	
	String status = (String) hs.getAttribute("userStatus");

	if (status == null || !"work".equals(status)) {
		response.sendRedirect("login.jsp");
	}
	String lang = "kz";
	if (hs.getAttribute("langCode") != null) {
		lang = (String) hs.getAttribute("langCode");
	}
	
	boolean erkTest = (Boolean)hs.getAttribute(TestServlet.HS_TEST_ERK);
%>
<!DOCTYPE html>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="media/south-street/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" />
<link type="text/css" href="media/testapp.css" rel="stylesheet" lang=""	media="screen" />
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/jquery-ui.custom.min.js"></script>

</head>
<body>
<table style="font-size:12pt;" border="0" height="97%" width="100%">
<tbody><tr><td style="font-size:16pt;" colspan="2" align="center" valign="middle">
<p style="font-size:18pt;font-weight:bold" align="center">
Тестілеу тілін таңдаңыз / Выберите язык тестирования
</p>
<br><br>
<form id="langSelectionForm" method="post" action="instruction.jsp">
<input type="hidden" value="lang" name="page">
 <label>
<input <% if ("kz".equals(lang)) { %>checked="checked" <% }  %> name="setlang" value="kz" type="radio">
Қазақ тілінде тестілеу
</label>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<label>
<input <% if ("ru".equals(lang)) { %>checked="checked" <% }  %>  name="setlang" value="ru" type="radio">
Тестирование на русском языке
</label>
<br><br>
<%if (erkTest) {%>
<input value=" Жалғастыру / Продолжить " style="font-size:14pt" type="button" onclick="submitFunc()">
<%} else { %>
<input value=" Жалғастыру / Продолжить " style="font-size:14pt" type="submit">
<%} %>
</form>
</td></tr><tr><td colspan="2" valign="bottom"><hr>
</td></tr><tr style="height:60px">
<td style="color:red;font-weigh:bold;font-size: 14pt;" align="left">
Ескерту!<br>
Тестілеу тілі тест басталардың алдында таңдалады және тестілеу кезінде өзгертілмейді
</td><td style="color:red;font-weigh:bold;font-size: 14pt;" align="right">
Примечание!<br>
Язык тестирования выбирается в начале тестирования и не меняется в процессе тестирования
</td></tr></tbody></table>
	<div style="display: none">
		<div id="dialog-confirm">
			<div style="font-family: 'Times New Roman'; font-size: 16pt">
			</div>
		</div>
	</div>
	<script>
		function submitFunc() {
			var val = $("input[name='setlang']:checked").val();
			var yesMsg = 'ДА';
			var noMsg = 'НЕТ';
			var title = 'Внимание';
			if ("ru" == val) {
				$("#dialog-confirm div").html('Вы выбрали русский язык для прохождения тестирования по оценке личных компетенций.<br/>Вы подтверждаете выбор языка?');
			} else {
				yesMsg = 'ИӘ';
				noMsg = 'ЖОҚ';
				title = 'Ескерту';
				$("#dialog-confirm div").html('Сіз жеке бас құзыреттерін бағалау бойынша тестілеуден өту үшін қазақ тілін таңдадыңыз.<br/>Сіз тілді таңдауды растайсыз ба?');
			}
			$("#dialog-confirm").dialog({
				title: title,
				resizable : false,
				width : 400,
				modal : true,
				buttons : [ {
					text : yesMsg,
					click : function() {
						$(this).dialog("close");
						$('#langSelectionForm').submit();
					},
				}, {
					text : noMsg,
					click : function() {
						$(this).dialog("close");
					}
				}, ]
			});
		};
	</script>
</body>
</html>
