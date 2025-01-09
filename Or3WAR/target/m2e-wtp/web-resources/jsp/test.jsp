<%@ page import="java.util.UUID" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%
    request.setCharacterEncoding("UTF-8");
    HttpSession s = request.getSession();
    String myUID = (String)s.getAttribute("uniqueUID");
    if (myUID == null) {
    	myUID = UUID.randomUUID().toString();
    	s.setAttribute("uniqueUID", myUID);
	}

	String webContextName = request.getContextPath();
    String pathCSS = webContextName + "/jsp/media/css/";
    String pathJS = webContextName + "/jsp/media/js/";
    
%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>

	<script type="text/javascript" src="<%=pathJS%>jquery.min.js"></script>
	<script type="text/javascript" src="<%=pathJS%>jquery.blockUI.js"></script>
	
	<style>
		p {
			margin: 1px 10px 1px 10px;
			font-family: Tahoma;
			font-size: 12px;
			font-weight: bold;
		}
	
		.green {
			color: green;
		}
		
		.red {
			color: red;
		}
		
		.black {
			color: black;
		}
	</style>
</head>



<script type="text/javascript">

window.contextName = '<%= webContextName %>';
window.mainUrl = window.contextName + "/nettest";

var uid = '<%= myUID %>';
var noConnection = false;
var eventSource = null;
	
$(function() {
	longPolling();
	//startSSE();
});

function startSSE() {
	eventSource = new EventSource(window.mainUrl + "?sse=1");
	eventSource.onmessage = function(event) {
		console.log("event: " + event);
		console.log("data: " + event.data);
		console.log("erik: " + event.erik);
	};
	eventSource.onopen = function(e) {
  		console.log("Соединение открыто");
	};

	eventSource.onerror = function(e) {
  		if (this.readyState == EventSource.CONNECTING) {
    		console.log("Соединение порвалось, пересоединяемся...");
  		} else {
    		console.log("Ошибка, состояние: " + this.readyState);
  		}
	};
}

function closeSSE() {
	eventSource.close();
}

function longPolling() {
	// Идентификатор таймаута, который должен разблокировать экран при появлении связи
	var timeoutId = null;
	// Если связи нет
	if (noConnection) {
		log('Попытка возобновления связи!', 'red');
		// То запускаем таймер для разблокировки экрана
		timeoutId = setTimeout(function(){
			if (noConnection) {
				log('Соединение восстановлено!', 'green');
				$('body').unblock();
				noConnection = false;
			}
		}, 4000);
	}

	var url = window.mainUrl + "?polling=" + rnd();
	$.get(url, function(data) {
		if (data) {
			processingPolling(data);
			setTimeout(function(){longPolling();}, 500);
		}
	}, 'json')
	.fail(function() {
		// Отменяем разблокировку экрана, так как связь не появилась
		if (timeoutId != null)
			clearTimeout(timeoutId);
		// Если связь была и вдруг пропала
		if (!noConnection) {
			noConnection = true;
			log('Соединение с сервером утеряно.', 'red');
			blockPage("Соединение с сервером утеряно.<br>Попытка возобновления связи!<br>Подождите...");
		}			

		setTimeout(function(){longPolling();}, 2000);
	});
}
	
function blockPage(title) {
	$('body').block({
		message: '<img src="'+window.contextName+'/jsp/media/img/loader.gif"><h1 style="color:#fff;font-size:16px;margin-top:10px;">'+title+'</h1>', 
		overlayCSS: {
			backgroundColor: '#000',
			opacity: 0.3,
			cursor: 'wait'
		},
		css: {
			border: 'none',
			width: '10%',
			padding: '15px',
			backgroundColor: '#000',
			'border-radius': '10px',
			color: '#fff'
		},
	});
}

function processingPolling(data) {
	if (data.result)
		log(data.result + " - " + data.msg, 'black');
}

function sendCommand() {
	var url = window.mainUrl;
	var par = {};
	par["wake"] = '1';
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url + (url.indexOf("?") > 0 ? "&" : "?") +"rnd=" + rnd(),
		data : par,
		success : function(content) {},
		dataType : 'json',
		async : false
	});
}

function getSSE() {
	var url = window.mainUrl;
	var par = {};
	par["get"] = '1';
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url + (url.indexOf("?") > 0 ? "&" : "?") +"rnd=" + rnd(),
		data : par,
		dataType : 'json',
		async : false
	});
}

function rnd() {
	return Math.floor(Math.random() * 99999999999);
}

function log(msg, style) {
	var date = new Date();
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	var d = date.getDate();
	var h = date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	var msec = date.getMilliseconds();
	
	if (d < 10) d = "0" + d;
	if (m < 10) m = "0" + m;
	if (h < 10) h = "0" + h;
	if (min < 10) min = "0" + min;
	if (sec < 10) sec = "0" + sec;
	msec = (msec < 10) ? ("00" + msec) : (msec < 100) ? ("0" + msec) : msec;
	
	var fd = d + '.' + m + '.' + y + ' ' + h + ':' + min + ':' + sec + '.' + msec;
	
	var logMsg = fd + " - " + msg;
	
	$('#log').append($("<p class='" + style + "'>" + logMsg + "</p>"));
}

</script>

<body>

<p> Моя сессия: <b><%= myUID %></b></p>
<button onclick="sendCommand();" value="111" title="222">Отправить команду</button>
<!-- button onclick="getSSE();" value="111" title="222">SSE</button>
<button onclick="closeSSE();" value="111" title="222">closeSSE</button-->

<div id="log">
</div>

</body>
</html>