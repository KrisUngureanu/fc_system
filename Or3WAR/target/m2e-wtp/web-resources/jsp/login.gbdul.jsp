<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>

<%@ page import="kz.tamur.web.controller.WebController"%>

<%
	/* Путь к логотипу для окна авторизации прописывается в файле web.xml
	 * без контекста веб-приложения
	 *
	 * <init-param>
     *	<param-name>logoLoginPath</param-name>
     *	<param-value>/jsp/media/img/login-logo.png</param-value>
     * </init-param>
	*/
	String tmp = getServletConfig().getServletContext().getInitParameter("login.servicedesk");
	boolean showServiceDesk = !"false".equals(tmp);
	
	String webContextName = request.getContextPath();
	String pathCSS = webContextName+"/jsp/media/css/";
	String pathJS = webContextName+"/jsp/media/js/";
	
	String windowTitle = WebController.WINDOW_TITLE;

	String logoPath = "media/img/logo-login-gis.png";

 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title><%= windowTitle %></title>
<link rel=" stylesheet" type="text/css" href="<%=pathCSS%>bootstrap.min.css">
<link rel=" stylesheet" type="text/css" href="<%=pathCSS%>uiv4.css?v=2019-09-05">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>bootstrap/easyui.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>custom-easyui.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>contactsInfo.css">
<link rel=" stylesheet" type="text/css" href="<%=pathCSS%>ul.css?v=2022-12-12">
<link rel="shortcut icon" type="image/x-icon" href="<%=webContextName%>/jsp/media/img/favico.png">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>liability.dialog.css">
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
<body style="padding: 0;" class="login"> 
<div id="loginbox">
	<div style="margin:20px;">
	<img src="<%= logoPath %>" class="logo">
	<div id='tt' class="easyui-tabs" data-options="plain:true, narrow:true">
		<div title="Логин/Пароль">
			<form id="loginForm" onsubmit="return loginFunc(false);"> 
				<input type="text" name="user" id="user"  placeholder="Пользователь"/>
				<input type="password" name="password" id="password"  placeholder="Пароль"/>
				<input id="submit" type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;"/>
			</form>
			<div class="btnPanel">
				<button id="loginBtn" class="btn btn-primary" onclick="loginFunc(false)">Войти в систему</button>
				<button id="resetBtn" class="btn btn-primary" onclick="reset()">Очистить</button>
			</div>
		</div>
		<div title="ЭЦП">
			<div class="btnPanel">
				<button id="loginECPBtn" class="btn btn-primary" onclick="loginToSystem()">Войти в систему</button>
			</div>
		</div>
	</div>
	<div class="btnPanel">
		<div id='errmsg' style='color:red;display:none;'>Вы используете неподдерживаемый браузер! Необходимо использовать Mozilla Firefox версии не ниже 32, Google Chrome версии не ниже 35 или Internet Explorer версии не ниже 10!
			<br><a href="<%=webContextName%>/jsp/media/files/Firefox Setup 38.0.5.exe"/>Скачать Firefox 38.0.5</a>
	   		<br><a href="<%=webContextName%>/jsp/media/files/Manual.docx"/>Инструкция</a>
	   	 </div>
	</div>
	</div>	
</div>
<% if (showServiceDesk) { %>
	<div class="tool-help"></div>
	<% } %>
	<div id="liabilityContentDiv" class="easyui-dialog"
		style="width: 800px; height: 600px;"
		data-options="title:'Обязательство о неразлгашении сведений',
					  closed: true,					  
					  draggable: false,
		        	  resizable: false,
		              closeOnEscape: false,
					  modal:true,
					  scrollable: true,
					  closable: false,
					  
					  buttons:[{text:'Подписать', disabled: true, id: 'liabilitySignBtn', handler:function(){signBtnPressed();}},
					  		   {text:'Отмена', handler:function(){cancelBtnPressed();}}]">
		<div>
			<div style="position: absolute; right: 10px;">
				<input type="button" value="RU" id="langButtonRU" />
				<input type="button" value="KZ" id="langButtonKZ" />
			</div>
		</div>
		<div id="liabilityText" disabled>Текст соглашения</div>
		<label><input id="liabilityInput" type="checkbox" /><a	id="liabilityInputText">Я принимаю условия соглашения</a></label>
	</div>	
<script>
var langCode = 'ru';
window.contextName = '<%=webContextName%>';
</script>
<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>prefixfree.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>browserDetector.js"></script>
<script type='text/javascript' src='<%=pathJS%>jquery.blockUI.js'></script>
<script type='text/javascript' src='<%=pathJS%>applet-load.js?v=2016-08-17'></script>
<script type='text/javascript' src='<%=pathJS%>ncaLayerOps.2015.js?v=2023-03-02'></script>
<script type='text/javascript' src='<%=pathJS%>liability.dialog.js?v=2023-03-02'></script>
<script>

var isUseECP = false;
var signedData;

var liabilityObjectUID = "";
var liabilityDialogTitleRU = "";
var liabilityDialogTitleKZ = "";
var liabilityTextRU = "";
var liabilityTextKZ = "";
var liabilityCheckTextRU = "";
var liabilityCheckTextKZ = "";

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
	$('#loginECPForm').removeAttr('onsubmit');
	$('#loginBtn').removeAttr('onclick');
	$('#loginECPBtn').removeAttr('onclick');
	$('#resetBtn').removeAttr('onclick');
	$('#loginBtn').hide();
	$('#loginECPBtn').hide();
	$('#resetBtn').hide();
	$('#errmsg').show();
}
var url = "<%=webContextName%>/main";
var sLogin = false;
getContactInfo();

$("#liabilityInput").change(function(event) {
    var checkbox = event.target;
    if (checkbox.checked) {
    	$("#liabilitySignBtn").linkbutton("enable");
    } else {
    	$("#liabilitySignBtn").linkbutton("disable");
    }
});

$('#langButtonRU').click(function() {
	$('#langButtonRU').prop('disabled', true);
	$('#langButtonKZ').prop('disabled', false);
	$('#liabilityContentDiv').dialog('setTitle', liabilityDialogTitleRU);
	$('#liabilityText').html(liabilityTextRU);
	$('#liabilityInputText').text(liabilityCheckTextRU);
});

$('#langButtonKZ').click(function() {
	$('#langButtonKZ').prop('disabled', true);
	$('#langButtonRU').prop('disabled', false);
	$('#liabilityContentDiv').dialog('setTitle', liabilityDialogTitleKZ);
	$('#liabilityText').html(liabilityTextKZ);
	$('#liabilityInputText').text(liabilityCheckTextKZ);
});

$('#tt').tabs({
	onSelect:function(title){
		if (title == "Логин/Пароль") {
			$('#loginBtn').show();
			$('#loginECPBtn').hide();
		}
		else if (title == "ЭЦП") {
			$('#loginECPBtn').show();
			$('#loginBtn').hide();
		}
	}
});

var secret = "";

function reset() {
	if (!$('#resetBtn').hasClass('btn-disabled')) {
		$('#loginForm')[0].reset();
		$('#loginECPForm')[0].reset();
	}
}

function getContactInfo() {
	var par = {};
	par["cmd"] = 'contactInfo';
	par["additionalInfo"] = "true";
	par["json"] = 1;
	par["guid"] = guid;
	$.ajax({
		type : 'POST',
		url : url + (url.indexOf("?") > 0 ? "&" : "?") +"rnd=" + rnd(),
		data : par,
		success : function(data) {
			if (data.contacts) {			
				var table = "<div style=\"overflow: auto;\"><table class=\"contactInfoTable\"><thead><tr><th>Консультант</th><th>Телефон</th><th>E-mail</th></tr></thead><tbody>";
				for (var i = 0; i < data.contacts.length; i++) {
					var contact = data.contacts[i];
					var row = "<tr><td align='center' width='500'>" + encodeHTML(contact.person) + "</td><td align='center' width='500'>" + encodeHTML(contact.telephone) + "</td><td align='center' width='350'><a href=\"mailto:" + encodeHTML(contact.email) + "\">" + encodeHTML(contact.email) + "</a></td></tr>";
					table = table + row;
				}
			  	table = table + "</tbody></table><div>";
			  	var content = "<div class=\"help-name\">Служба поддержки</div><br>" + table;
			  	$('.tool-help').append($(content));
			}
			if (data.additionalInfo) {
				$('.tool-help').append("<br><div align='center' width='1350' style='font-size: 10pt; font-weight: bold'>" + encodeHTML(data.additionalInfo) + "</div>")
			}
		},
		dataType : 'json',
		async : false
	});
}

function rnd() {
	var array = new Uint32Array(1);
	var crypto = window.crypto || window.msCrypto;
	crypto.getRandomValues(array);
	return array[0];
}

function generateGUID() {
	var array = new Uint16Array(8);
	var crypto = window.crypto || window.msCrypto;
	crypto.getRandomValues(array);
	
	var res = ('000' + array[0].toString(16)).substr(-4) +
		('000' + array[1].toString(16)).substr(-4) + '-' +
		('000' + array[2].toString(16)).substr(-4) + '-' +
		('000' + array[3].toString(16)).substr(-4) + '-' +
		('000' + array[4].toString(16)).substr(-4) + '-' +
		('000' + array[5].toString(16)).substr(-4) +
		('000' + array[6].toString(16)).substr(-4) +
		('000' + array[7].toString(16)).substr(-4);
	
	return res;
}

var guid = generateGUID();

function loginFunc(force) {
	if (force || !$('#loginBtn').hasClass('btn-disabled')) {
		$('#loginBtn').addClass('btn-disabled').attr("disabled", "disabled").html('<i class="icon-refresh icon-white icon-refresh-animate"></i> Подождите...');
		$('#resetBtn').addClass('btn-disabled').attr("disabled", "disabled");
		
		$.post(url, {name:$('#user').val(),passwd:$('#password').val(),browser:browser,json:1,force:(force?1:0),sLogin:(sLogin?1:0),rnd:rnd(),guid:guid}, function(data) {
			if (data.result == "success") {
				if (data.dl) {
					window.alert(data.dl);
				}
				if (data.tempReg) {
					window.alert(data.tempReg);
				}
				if (data.la) {
					showLADialog(data.la);
				} else {
					window.location.replace("<%=webContextName%>/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd());
				}
			} else if (data.passChange == "1") {
				alert(data.message);
				changePwdDialog(false);
			}  else if (data.passChange == "2") {
				sLogin = true;
				alert(data.message);
				changePwdDialog(false);
			} else if (data.reconnect == "1") {
				if (confirm(data.message)) {
					loginFunc(true);
				} else {
					$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
					$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				}
			} else {
				alert(data.message);
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			}
		},'json');
	}
	return false;
}

window.dialogResult = '1';

function showLADialog(la) {
	// Вывод окна подписания обязательства о неразлгашении сведений
	if (la.liabilityDialogTitleRU) {
		liabilityDialogTitleRU = la.liabilityDialogTitleRU;
		$('#liabilityContentDiv').dialog('setTitle', liabilityDialogTitleRU);
	}
	if (la.liabilityDialogTitleKZ) {
		liabilityDialogTitleKZ = la.liabilityDialogTitleKZ;
	}
	
	if (la.liabilityTextRU) {
		liabilityTextRU = b64DecodeUnicode(la.liabilityTextRU);
		$('#liabilityText').html(liabilityTextRU);
	}
	if (la.liabilityTextKZ) {
		liabilityTextKZ = b64DecodeUnicode(la.liabilityTextKZ);
	}

	if (la.liabilityCheckTextRU) {
		liabilityCheckTextRU = la.liabilityCheckTextRU;
		$('#liabilityInputText').text(liabilityCheckTextRU);
	}
	if (la.liabilityCheckTextKZ) {
		liabilityCheckTextKZ = la.liabilityCheckTextKZ;
	}

	if (la.liabilityObjectUID) {
		liabilityObjectUID = la.liabilityObjectUID;
	}

	$('#langButtonRU').prop('disabled', true);
	
	$('#liabilityContentDiv').dialog("open");
}

function changePwdDialog(obligatory) {
	var dialogId = 'or3_popup';
	$('body').append($("<div></div>").attr('id', dialogId));
	window.dialogResult = '1';

	var buttonOk = {
		text: 'Изменить',
		handler: function() {
			$("#" + dialogId + " .dialog-button .l-btn").linkbutton('disable');
			window.dialogResult = '0';
			var par = {};
    		par["name"] = $('#user').val();
    		par["configNumber"] = '0';
    		par["browser"] = browser;
    		par["json"] = 1;
        	par["passwd"] = $('#' + dialogId).find('[uid="oldPass"]').val();
        	par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
        	par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
        	par["guid"] = guid;
        	$.post(url + "?rnd=" + rnd(), par, function(data) {
        		if (data.result == 'error') {
        			alert(data.message);
					$("#" + dialogId + " .dialog-button .l-btn").linkbutton('enable');
        		} else {
        			$("#" + dialogId).dialog('destroy');
        			alert('Пароль успешно изменен!');
        			window.location.replace("<%=webContextName%>/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd());
        		}
        	}, 'json');
		}
	};
	
	var buttonCancel = {
		text: 'Отмена',
		handler: function() {
			window.dialogResult = '1';
			$("#" + dialogId).dialog('destroy');
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			if(sLogin){
				loginFunc(false);
			}
		}
	};
	
	var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
	
	$('#' + dialogId).dialog({
		title: 'Смена пароля',
		width: 300,
		height: 200,
		closed: false,
		cache: false,
		closable: obligatory != true,
		href: '<%=webContextName%>/jsp/pwd.jsp?guid=' + guid + '&rnd=' + rnd(),
		modal: true,
		onClose: function() {
			$("#" + dialogId).dialog('destroy');
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		},
		onBeforeDestroy : function() {
		},
		buttons: buttons
	});
}

function loginToSystem() {
	if (!$('#loginECPBtn').hasClass('btn-disabled')) {
		document.getElementById('errmsg').style.display = 'none';
		$('#loginECPBtn').addClass('btn-disabled').attr("disabled", "disabled").html('<i class="icon-refresh icon-white icon-refresh-animate"></i> Подождите...');	
		$('#resetBtn').addClass('btn-disabled').attr("disabled", "disabled");
		$.post(url, {cmd:'getSecret',noCache:(new Date).getTime(),json:1}, function(data) {
			if (data.secret) {
				let xml = '<login><secret>' + data.secret + '</secret></login>';
				
				signXml(BASICS, AUTH, xml).then(data2 => {
					if (data2.responseObject) {
						let signedData = data2.responseObject;
						par = {
							secret: data.secret,
							signedData: signedData,
							browser: browser,
							rnd: rnd(),
							guid: guid,
							json: 1
						};
						
						$.post(url, par, function(data3) {
							if (data3.result == "success") {
								if (data3.dl) {
									window.alert(data3.dl);
								}
								if (data3.tempReg) {
									window.alert(data3.tempReg);
								}
								if (data3.la) {
									showLADialog(data3.la);
								} else {
									window.location.href = window.contextName + "/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd();
								}
							} else if (data3.reconnect == "1") {
								if (confirm(data3.message)) {
									forceLogin(par);
								} else {
									$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
									$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
								}
							} else {
								alert(data3.message);
								$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
								$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
							}
						},'json');
						
					} else {
						let error = (data2.message === 'action.canceled')
							? 'Действие отменено пользователем'
							: 'Код ошибки: ' + data2.code + ' Сообщение: ' + data2.message;
						console.error("msg text: " + error);
						alert(error);
						$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
						$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
					}
				}, error => {
					console.error("error text: " + error);
					alert(error);
					$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
					$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				});
				
			} else {
				alert(data.message);
				$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			}
		},'json');
	}
	return false;
}

function forceLogin(par) {
	par["force"] = 1;
	par["guid"] = guid;
	$.post(url, par, function(data) {
		if (data.result == "success") {
			if (data.dl) {
				window.alert(data.dl);
			}
			if (data.tempReg) {
				window.alert(data.tempReg);
			}
			if (data.la) {
				showLADialog(data.la);
			} else {
				window.location.href = window.contextName + "/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd();
			}	
		} else {
			alert(data.message);
			$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		}
	},'json');
}

function encodeHTML(s) {
    return s.replace(/&/g, '&amp;')
               .replace(/</g, '&lt;')
               .replace(/>/g, '&gt;')
               .replace(/"/g, '&quot;')
               .replace(/'/g, '&apos;');
}
</script>

</body>
</html>