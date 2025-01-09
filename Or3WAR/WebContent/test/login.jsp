<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	String webContextName = request.getContextPath();
 %>
<c:set var="language" value="${not empty param.lang ? param.lang : 'ru'}" scope="session" />
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" />
<!DOCTYPE html>
<html><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
 <script src="js/jquery.min.js" type="text/javascript"></script>
<style>
 html {width:98%;height:98%}
 body {width:100%;height:100%;background:#E0FFFF;cursor: pointer}
</style>
<title>Test</title></head>
<body>
<table border="0" cellpadding="5" cellspacing="5" width="100%">
<tbody><tr><td style="width:33%">
</td><td style="width:34%" align="center">
<img src="media/img/logo.png" style="width:200px;height:200px">
</td><td align="right" valign="top">
<form name="FrmLang" method="post">
<% if ("kz".equals(request.getParameter("lang"))) { %>
<label><input name="lang" value="kz" checked="checked" onclick="document.FrmLang.submit()" type="radio">Қазақ тілінде</label>
<label><input name="lang" value="ru" onclick="document.FrmLang.submit()" type="radio">На русском</label>
<% } else { %>
<label><input name="lang" value="kz" onclick="document.FrmLang.submit()" type="radio">Қазақ тілінде</label>
<label><input name="lang" value="ru" checked="checked" onclick="document.FrmLang.submit()" type="radio">На русском</label>
<% } %>
</form>
</td></tr></tbody></table>
<center style="font-size:16pt"><fmt:message key="login.label.orgname" /></center>
<br>
<hr width="70%">
<form name="login" method="post" onsubmit="return loginFunc()">
<center>
<table style="width:350px" border="0" cellpadding="12" cellspacing="3">
<tbody><tr style="font-size:14pt">
<td style="width:40%" align="right"><fmt:message key="iin" /></td><td>
<input name="login" id="login" autocomplete="off" value="" style="font-size:14pt;width:150px;height:22px" size="12" maxlength="12" onkeyup="checkIIN()">
</td></tr><tr><td colspan="2" align="center">
<table id="tblkey" border="0">
<tbody><tr style="height:30;text-align:center">
<td style="width:22px;border:1px solid blue" onclick="DoKey(0)">
0
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(1)">
1
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(2)">
2
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(3)">
3
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(4)">
4
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(5)">
5
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(6)">
6
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(7)">
7
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(8)">
8
</td><td style="width:22px;border:1px solid blue" onclick="DoKey(9)">
9
</td><td style="width:22px;border:1px solid blue" onclick="DoKey('-')">
⇐
</td></tr></tbody></table>
</td></tr><tr><td colspan="2" align="center"><input id="docon" name="docon" value="<fmt:message key="login.label.enter" />" style="width:100px;height:22px" type="button" onclick="loginFunc()" disabled="disabled">
</td></tr></tbody></table>
</center>
</form>
<hr width="70%">
<script>

var url = "<%=webContextName%>/test/action";

function loginFunc() {
$.post(url, {login:$('#login').val(),password:""}, function(data) {
	if (data.result == "ok") {
		window.location.href = "lang.jsp";
	} else if (data.result == "continue") {
		window.location.href = "pass.jsp";
	} else {
		alert(data.msg);
	}
},'json');
return false;
 };
function DoKey(k){
     // alert("k ["+k+"]");
     var x = document.getElementById("login");
     if ( k == "-" ) {
       if ( x.value.length > 1 ) {
         x.value = x.value.substr(0,x.value.length-1);
       } else {
         x.value = "";
         $("#docon").attr("disabled","disabled");
         }
     }else{
       if ( x.value.length < 12 ) x.value = x.value + k;
       $("#docon").removeAttr("disabled");
     }
    }

function checkIIN() {
	if ($("#login").val().length > 0)
		$("#docon").removeAttr("disabled");
	else
		$("#docon").attr("disabled","disabled");
}
</script>


</body></html>