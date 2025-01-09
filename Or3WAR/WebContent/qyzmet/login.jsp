<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

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
	String pathCSS = webContextName+"/qyzmet/css/";
	String pathJS = webContextName+"/qyzmet/js/";
	
	String windowTitle = WebController.WINDOW_TITLE;
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><%= windowTitle %></title>
<link rel=" stylesheet" type="text/css" href="<%=pathCSS%>bootstrap.min.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>bootstrap/easyui.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>login.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>contactsInfo.css">
<link rel=" stylesheet" type="text/css"	href="<%=pathCSS%>liability.dialog.css">

</head>
<body style="padding: 0;">
<div id="loginbox">
	<div class="logo-pane">
		<span class="gerb"></span>
		<span class="logo-xxx"></span>
		<div class="system-name">
		Система управления персоналом
		</div>
	</div>
	
	<div class="main-pane">
		<div class="main-pane-header">
			<div class="logo"></div>
			<div class="lang-selector">
				<button id="topLangKz" class="lang-button">KZ</button>
				<button id="topLangRu" class="lang-button selected">RU</button>
			</div>
		</div>
		<div class="info">
		Войдите в систему с помощью логина/пароля или ключа ЭЦП
		</div>
		<div class="select-login-type">
			<div class="select-info">
				<span>——————</span><span class="info-monroe"> Войти с помощью </span><span>——————</span>
			</div>
			<div class="btn-select-login btn-login-pass selected">
				<span class="btn-icon icon-login"></span>
				<span>Логин</span>
			</div>
			<div class="btn-select-login btn-login-ecp">
				<span class="btn-icon icon-ecp"></span>
				<span>ЭЦП</span>
			</div>
		</div>
		
		<div id="login-pass-form">
			<label>Имя пользователя</label>
			<input type="text" name="user" id="user"  placeholder="Введите имя пользователя"/>
			<label>Пароль</label>
			<a href="#" class="label-forgot-pass" tabindex="-1">Забыли пароль?</a>
			
			<input type="password" name="password" id="password" placeholder="Пароль"/>
			
			<button id="loginBtn" class="btn-login btn btn-primary">Войти в систему</button>
			<button id="clearBtn" class="clearBtn btn btn-primary">Очистить</button>
		</div>
		
		<div id="login-ecp-form" style="display: none;">
			<button id="loginEcpBtn" class="btn-login btn btn-primary">Выбрать сертификат</button>
		</div>
		
		<div class="login-error-text">
		</div>
		
		<a href="#" class="label-show-support">
			<span class="btn-icon icon-support"></span>
			<span>Служба поддержки</span>
		</a>
		
		
		
	</div>
	
</div>

<div class="tool-help" style="display: none"></div>
    
<% if (showServiceDesk) { %>

<% } %>

<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=pathJS%>browserDetector.js?v=2022-04-15"></script>
<script type='text/javascript' src='<%=pathJS%>jquery.blockUI.js'></script>
<script type='text/javascript' src='<%=pathJS%>ncaLayerOps.js?v=2022-04-15'></script>

<script type="text/javascript" src="<%=pathJS%>or3-module.js"></script>

<script>

var langCode = 'ru';
window.contextName = '<%=webContextName%>';
window.mainUrl = window.contextName + "/main";
let loginApp = new or3web.Login(window.contextName, window.mainUrl, langCode);

</script>

<script>
(function () {
	loginApp.init();

	let tempLang = localStorage.getItem('EkyzmetLanguage');
	if(tempLang) {
		changeLanguage(tempLang);
	} else {
		changeLanguage("KZ");
	}

}());

$("#topLangKz").click(() => changeLanguage("KZ"));
$("#topLangRu").click(() => changeLanguage("RU"));

function changeLanguage(language) {
	langCode = language.toLowerCase();
	loginApp.changeLanguage(langCode);
	
	if(language == "KZ") {
		localStorage.setItem('EkyzmetLanguage', 'KZ');
		$("#topLangKz").addClass("selected");
		$("#topLangRu").removeClass("selected");

		$(".system-name").html("Персоналды басқару жүйесі");
		$(".info").html("Логин / пароль немесе ЭЦҚ кілті арқылы жүйеге кіріңіз");
		$(".info-monroe").html(" Көмегімен кіру ");
		$(".icon-login").next().html("Кіру");
		$(".icon-ecp").next().html("ЭЦҚ");
		$("#user").prev().html("Пайдаланушы аты");
		$("#user").attr("placeholder", "Пайдаланушы атын енгізіңіз")
		$("#user").next().html("Құпия сөз");
		$(".label-forgot-pass").html("Құпия сөзді ұмыттыңыз ба?");
		$("#password").attr("placeholder", "Құпия сөз");
		$("#loginBtn").html("Жүйеге кіру");
		$("#clearBtn").html("Тазалау");
		$("#loginEcpBtn").html("Сертификатты таңдаңыз");
		$(".label-show-support").children().last().html("Қолдау қызметі");
	} else {
		localStorage.setItem('EkyzmetLanguage', 'RU');
		$("#topLangKz").removeClass("selected");
		$("#topLangRu").addClass("selected");

		$(".system-name").html("Система управления персоналом");
		$(".info").html("Войдите в систему с помощью логина/пароля или ключа ЭЦП");
		$(".info-monroe").html(" Войти с помощью ");
		$(".icon-login").next().html("Логин");
		$(".icon-ecp").next().html("ЭЦП");
		$("#user").prev().html("Имя пользователя");
		$("#user").attr("placeholder", "Введите имя пользователя")
		$("#user").next().html("Пароль");
		$(".label-forgot-pass").html("Забыли пароль?");
		$("#password").attr("placeholder", "Пароль");
		$("#loginBtn").html("Войти в систему");
		$("#clearBtn").html("Очистить");
		$("#loginEcpBtn").html("Выбрать сертификат");
		$(".label-show-support").children().last().html("Служба поддержки");
	}
	
}


var signedData;

var url = "<%=webContextName%>/main";

var secret = "";

var guid = "";

window.dialogResult = '1';

function changePwdDialog(obligatory) {
	let language = localStorage.getItem("EkyzmetLanguage"),
		buttonChange = "Өзгерту",
		passwordSuccessChanged = "Құпия сөз сәтті өзгертілді!",
		cancelButton = "Бас тарту",
		enterTheSystem = "Жүйеге кіру",
		changePassword = "Құпия сөзді өзгерту";

	if(language) {
		if(language == "KZ") {
			buttonChange = "Өзгерту";
			passwordSuccessChanged = "Құпия сөз сәтті өзгертілді!";
			cancelButton = "Бас тарту";
			enterTheSystem = "Жүйеге кіру";
			changePassword = "Құпия сөзді өзгерту";
		} else {
			buttonChange = "Изменить";
			passwordSuccessChanged = "Пароль успешно изменен!";
			cancelButton = "Отмена";
			enterTheSystem = "Войти в систему";
			changePassword = "Смена пароля";
		}
	}

	var dialogId = 'or3_popup';
	$('body').append($("<div></div>").attr('id', dialogId));
	window.dialogResult = '1';

	var buttonOk = {
		text: buttonChange,
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
        			alert(passwordSuccessChanged);
        			window.location.replace("<%=webContextName%>/qyzmet/index.jsp?guid=" + guid + "&rnd=" + rnd());
        		}
        	}, 'json');
		}
	};
	
	var buttonCancel = {
		text: cancelButton,
		handler: function() {
			window.dialogResult = '1';
			$("#" + dialogId).dialog('destroy');
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			if(sLogin){
				loginFunc(false);
			}
		}
	};
	
	var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
	
	$('#' + dialogId).dialog({
		title: changePassword,
		width: 300,
		height: 200,
		closed: false,
		cache: false,
		closable: obligatory != true,
		href: '<%=webContextName%>/jsp/pwd.jsp?guid=' + guid + '&rnd=' + rnd(),
		modal: true,
		onClose: function() {
			$("#" + dialogId).dialog('destroy');
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		},
		onBeforeDestroy : function() {
		},
		buttons: buttons
	});
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