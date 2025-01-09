<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>

<%
    String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/jsp/media/css/";
    String pathJS = webContextName + "/jsp/media/js/";
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel=" stylesheet" type="text/css" href="<%= pathCSS %>bootstrap.min.css">
<link rel=" stylesheet" type="text/css" href="<%= pathCSS %>ui.css">
<style>
.icon-refresh-animate {
  animation-name: rotateThis;
  animation-duration: .5s;
  animation-iteration-count: infinite;
  animation-timing-function: linear;
  -webkit-animation-name: rotateThis; 
  -webkit-animation-duration: .5s; 
  -webkit-animation-iteration-count:  infinite;
  -webkit-transition-timing-function: linear;
  -moz-animation-name: rotateThis;
  -moz-animation-duration: .5s;
  -moz-animation-iteration-count: infinite;
  -moz-animation-timing-function: linear;
  -ms-animation-name: rotateThis;
  -ms-animation-duration: .5s;
  -ms-animation-iteration-count: infinite;
  -ms-animation-timing-function: linear;
}
 
@keyframes rotateThis {
  from { transform: scale( 1 ) rotate( 0deg );   }
  to   { transform: scale( 1 ) rotate( 360deg ); }
}
@-ms-keyframes rotateThis {
  from { transform: scale( 1 ) rotate( 0deg );   }
  to   { transform: scale( 1 ) rotate( 360deg ); }
}
@-moz-keyframes rotateThis {
  from { -moz-transform: scale( 1 ) rotate( 0deg );   }
  to   { -moz-transform: scale( 1 ) rotate( 360deg ); }
}
@-webkit-keyframes rotateThis {
  from { transform: scale( 1 ) rotate( 0deg );   }
  to   { transform: scale( 1 ) rotate( 360deg ); }
}
</style>
</head>
<body >
<div id="loginbox">
<div style="margin:20px; ">
<img src="/ekyzmet-ui/jsp/media/img/login-logo.png" width="222" height="80">
<form id="loginForm" onsubmit="return getSecretString();">
<table cellpadding="0" cellspacing="0" style="border-width: 0;">
	<tr>
		<td>
			<input type="text" name="file" value="" class="input" id="file" placeholder="Ключевой контейнер" style="width: 230px;" />
		</td>
		<td>
			<input name="sel" type="button" class="button" value="..." onclick="selectFile(document.getElementById('file').value);" style="width: 30px; padding: 0;" />
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="password" name="password" id="password"  placeholder="Пароль" style="width: 230px;" />
		</td>
</table> 
<input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;"/>
</form>
<div id='errmsg' style='color:red;display:none;'>Вы используете неподдерживаемый браузер! Необходимо использовать Mozilla Firefox версии не ниже 32, Google Chrome версии не ниже 35 или Internet Explorer версии не ниже 10!
	<br><a href="<%=webContextName%>/jsp/media/files/Firefox Setup 38.0.5.exe"/>Скачать Firefox 38.0.5</a>
	<br><a href="<%=webContextName%>/jsp/media/files/Manual.docx"/>Инструкция</a>
</div>
 <div class="btnPanel">
  <button id="loginBtn" class="btn btn-primary" onclick="getSecretString()" style="font-size: 16px; padding: 4px 10px;">Войти в систему</button>
</div>
</div>
</div>
<script>
var langCode = 'ru';
window.contextName = '<%=webContextName%>';
</script>
<script type="text/javascript" src="<%= pathJS %>jquery.min.js"></script>
<script type="text/javascript" src="<%= pathJS %>prefixfree.min.js"></script>
<script type="text/javascript" src="<%= pathJS %>browserDetector.js"></script>
<SCRIPT TYPE='text/javascript' SRC='<%= pathJS %>jquery.blockUI.js'></SCRIPT>
<SCRIPT TYPE='text/javascript' SRC='<%= pathJS %>applet-load.js?v=2016-08-01'></SCRIPT>
<script>
BrowserDetect.init();
var browser = BrowserDetect.browser+";"+BrowserDetect.version+";"+BrowserDetect.OS;
if (BrowserDetect.version === "an unknown version" || 
		(
			(BrowserDetect.browser !== 'Chrome' || BrowserDetect.version < 35) 
			&& (BrowserDetect.browser !== 'Firefox' || BrowserDetect.version < 32) 
			&& (BrowserDetect.browser !== 'Explorer' || BrowserDetect.version < 10)
			&& (BrowserDetect.browser !== 'Mozilla' || BrowserDetect.version < 11)
		)
	) {
	$('#loginForm').removeAttr('onsubmit');
	$('#loginBtn').removeAttr('onclick');
	$('#loginBtn').hide();
	$('#errmsg').show();
}
var url = "/ekyzmet-ui/main";

loadApplet("ECPApplet");

function login(secret) {
	var s = document.getElementById('ECPApplet').getSignAndCert(secret, 17, $('#file').val(), $('#password').val(), true);
	if (s != null) {
  		if (s.length > 7 && "[ERROR: " == s.substring(0, 8)) {
			alert(s.substring(8, s.length - 1));
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
  		} else {
			var b1 = s.indexOf("sign=");
			var b2 = s.indexOf("&cert=");

			var sign = s.substring(b1 + 5, b2);
			var cert = s.substring(b2 + 6);
			
			var par = {};
			par["json"] = 1;
			par["sign"] = sign;
			par["cert"] = cert;
			par["passwd"] = $('#password').val();
			par["browser"] = browser;
			par["profile"] = $('#file').val();
			par["noCache"] = (new Date).getTime();
			
			$.post(url, par, function(data) {
				if (data.result == "success") {
					window.location.href = "/ekyzmet-ui/jsp/index.jsp";
				} else {
					alert(data.message);
					$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
				}
			},'json');
		}
	}            
} 
 
function selectFile(path) {
 	var f = document.getElementById('ECPApplet').selectFile("p12", "Файлы ключей ЭЦП в формате PKCS12", path);
}

function setSelectedFile(f) {
  	if (f != null) {
  		if (f.length > 7 && "[ERROR: " == f.substring(0, 8)) {
    		alert(f.substring(8, f.length - 1));
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
  		} else {
			$('#file').val(f);
  		}
  	}
}


function getSecretString() {
	if (!$('#loginBtn').hasClass('btn-disabled')) {
		$('#loginBtn').addClass('btn-disabled').attr("disabled", "disabled").html('<i class="icon-refresh icon-white icon-refresh-animate"></i> Подождите...');	
	
		$.post(url, {cmd:'getSecret',noCache:(new Date).getTime(),json:1}, function(data) {
			if (data.secret) {
				login(data.secret);
			} else {
				alert(data.message);
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			}
		},'json');
	}
	return false;
} 

 
</script>

</body>
</html>