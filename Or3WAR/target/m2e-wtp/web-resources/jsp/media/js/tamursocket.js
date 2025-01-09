/* соединение с локальной программой для работы с ЭЦП */
var socket;
/* флаг успешного подключения к ПО ЭЦП */
var isConnect = false;

/* мапа для связи удобочитаемого названия устройства хранения с необходимым названием для ПО ЭЦП */
var readers = {"KAZToken" : "DigiFlow LLP. KAZTOKEN 0", "e-Token" : "Aladdin Token JC 0"};

var ROW_SEPARATOR = "|row|";
var COLUMN_SEPARATOR = "|col|";

/* Счетчик количества подключений к локальному ПО */
var startServerTryCount = -1;
/* Временной интервал между повторными попытками подключения */
var startServerTryPeriod = 5000;
/* Максимальное количество попыток подключений */
var startServerTryMax = 20;
/* Сообщение при невозможности подключения к ПО ЭЦП */
var startServerFailMsg = "Не удалось запустить программу для работы с ключами УЦГО.";

/* функция-заглушка просто отображает сообщение пользователю */
function errorConnectDefault(data) { 
	alert(data);
	createTamurSocket(false);
}
/* функция, вызываемая при вызове функций ПО ЭЦП при отсутствии связи с ПО ЭЦП */
var errorConnectCallBack = errorConnectDefault;
/* мапа с коллбэками */
var callbacks = {};

var lastRequestId = 1; 

/* регистрируем функцию, вызываемую после отработки запроса по идентификатору */
function registerCallback(id, callback) { 
	callbacks[id] = callback;
}

/* вызываем функцию, после отработки запроса по идентификатору */
function callCallback(res) { 
	console.log(res.data);
	var data = JSON.parse(res.data);
	var callback = callbacks[data.id];
	callback(data);
}

/* Подключение к веб-сокету для вызова функций, связанных с ЭЦП */
function connectTamurSocket(notify) {
	if (!isConnect) {
		/* блокировка окна браузера с сообщением ожидания */
		block('Подключение к локальной программе для работы с ключами УЦГО...');
		/* Открытие веб-сокета к локальной программе для работы с ЭЦП */
		createTamurSocket(notify);
	} else {
		post({"ucgosocketconn":true});
	}
}

/* Открытие веб-сокета к локальной программе для работы с ЭЦП */
function createTamurSocket(notify) {
	/* открытие веб-сокета */
	socket = new WebSocket("ws://127.0.0.1:12018/ws/ucgo");
	
	/* после успешного подключения */
	socket.onopen = function() {
		/* разблокировать окно браузера */
		unblock();
		/* сбросить счетчик попыток соединения */
		startServerTryCount = -1;
		/* установить флаг наличия подключения */
		isConnect = true;

		// оповестить веб-клиент о удачной попытке подключения к ПО ЭЦП
		if (notify) {
			post({"ucgosocketconn":true});
		}
		
		console.log("connection opened");
	}
	/* при закрытии подключения к программе ЭЦП */
	socket.onclose = function(){
		/* сбросить флаг подключения */
		isConnect = false;
		/* сбросить счетчик попыток соединения */
		startServerTryCount = -1;
		
		console.log("connection closed");
	}
	/* при ошибке установления связи */
	socket.onerror = function(event) {
		/* вывод в лог события */	
		console.log(event);

		/* если это первая попытка подключения */		
		if (startServerTryCount == -1) { 
			/* предлагаем пользователю запустить ПО ЭЦП с помощью WebStart */
			startWebsocketServer();
			startServerTryCount = 0;
		}
		
		/* если превышено максимальное количество попыток подключения */
		if (startServerTryCount >= startServerTryMax) {
			/* Обнуляем счетчик попыток */
			startServerTryCount = -1;
			// оповестить веб-клиент о неудачной попытке подключения к ПО ЭЦП
			if (notify) {
				post({"ucgosocketconn":false});
			}
			/* разблокировать окно браузера */
			unblock();
			/* вывести сообщение о невозможности подключения к ПО ЭЦП */
			alert(startServerFailMsg);
			return;
		}

		/* устанавливаем таймер для повторного вызова функции попытки подключения через startServerTryPeriod мс */
		setTimeout(function() {
			startServerTryCount++;
			createTamurSocket(notify);
		}, startServerTryPeriod);
		return;
	}
}

/* блокировка окна браузера */
function block(msg) {
    $.blockUI({
        message: msg,
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
    });
}

/* разблокировка окна браузера */
function unblock() {
	appletLoading = false;
	$.unblockUI();
}

/* вывод ссылки для запуска локального ПО ЭЦП с помощью технологии WebStart */
function startWebsocketServer() {
	$('body').unblock();
	
    $.blockUI({
        message: 'Не найдено приложение для работы с ключами УЦГО! <br/> <a href="javascript:downloadJNLP(\'' + window.contextName + '/jsp/media/ucgo/ucgo.jnlp?d=2018-02-25\');">Запустить приложение</a>',
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
    });
}

function downloadJNLP(url) {
	$('#report_frame').attr('src', url);
}

/**
	Функции для работы с ПО ЭЦП
  */

/** Универсальная функция для обращения к ПО ЭЦП. Информация о функционале заложена в param (json-объект), где содержится 
	название функции в ПО ЭЦП и параметры запроса. Например:
	
	 {"Function": "checkPassword", "Param": {"id": 1, "reader": "KAZToken", "pass": "1234567890"}}

	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function callTamurSocket(param, callback) {
	param.Param.id = "" + lastRequestId++;
	
	// Вывод в лог параметров запроса
	console.log(JSON.stringify(param));
	// если метод вызван до подсоединения к ПО ЭЦП
	if (!isConnect) {
		// вызвать функцию ошибки - просто показать сообщение пользователю
		errorConnectCallBack("TamurSocket not available");
		return null;
	}
	// установить для сокета функцию, выполняемую после получения результата обращения
	registerCallback(param.Param.id, callback);
	socket.onmessage = callCallback;
	// обратиться к ПО ЭЦП
	socket.send(JSON.stringify(param));
}

/** Вызов функции для проверки подключенных типов токенов (Казтокен, еТокен, УДЛ и др.) 
	options - json-объект, содержит только идентификатор запроса {"id": 1}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function generateUID(){
	var d = new Date();
	return new Date().toISOString().replace(/[^\d]/g, '').substring(2);
}

/** Вызов функции для проверки подключенных типов токенов (Казтокен, еТокен, УДЛ и др.) 
	options - json-объект, содержит только идентификатор запроса
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function getConnectedTokens(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "getConnectedTokens",
		"Param": {}
	};
	callTamurSocket(param, callback);
}

function loadToken(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "loadToken",
		"Param": {
			"store": options.store
		}
	};
	callTamurSocket(param, callback);
}

/** Вызов функции для проверки пароля к носителю 
	options - json-объект, содержит идентификатор запроса, тип устройства (KAZToken, e-Token), пароль {"id": 1, "reader": "KAZToken", "pass": "1234567890"}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function checkPassword(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "checkPassword",
		"Param": {
			"reader": readers[options.reader],
			"pass":options.pass
		}
	};
	callTamurSocket(param, callback);
}

/** Вызов функции для получения сертификатов на носителе
	options - json-объект, содержит идентификатор запроса, тип устройства (KAZToken, e-Token), пароль,
	bin (не обязательно), iin (не обязательно) {"id": 1, "reader": "KAZToken", "pass": "1234567890", "iin": "800501401709"}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function getCertificates(options, callback){
	var param = {
		"Function": "getCertificates",
		"Param": {
			"reader": options.reader,
			"pass": options.pass
		}
	};
	if (options.bin) param['Param']['bin'] = options.bin;
	if (options.iin) param['Param']['iin'] = options.iin;

	callTamurSocket(param, callback);
}

function getStoreEntries(options, callback){
	var param = {
		"Function": "getCertificates",
		"Param": {
			"store": options.store,
			"pass": options.pass
		}
	};

	callTamurSocket(param, callback);
}

/** Вызов функции для сохранения сертификата на носителе
	options - json-объект, содержит идентификатор запроса, тип устройства (KAZToken, e-Token), пароль,
	bin (не обязательно), iin (не обязательно) {"id": 1, "reader": "KAZToken", "pass": "1234567890", "iin": "800501401709"}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/

function createPKCS7(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "createPKCS7",
		"Param": {
			"reader": options.reader,
			"pass":options.pass,
			"uid":options.uid,
			"content":options.content
		}
	};
	callTamurSocket(param, callback);
}

function createPKCS10(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "createPKCS10",
		"Param": {
			"reader": options.reader,
			"pass":options.pass,
			"uid":options.uid,
			"dn":options.dn
		}
	};
	callTamurSocket(param, callback);
}

function isCertExists(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "isCertExists",
		"Param": {
			"reader": options.reader,
			"pass": options.pass,
			"uid": options.uid
		}
	};
	callTamurSocket(param, callback);
}

function saveCertificate(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "saveCert",
		"Param": {
			"reader": options.reader,
			"pass":options.pass,
			"uid":options.uid,
			"cert":options.cert
		}
	};
	callTamurSocket(param, callback);
}

function deleteCertificate(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "deleteCert",
		"Param": {
			"reader": options.reader,
			"pass":options.pass,
			"uid":options.uid
		}
	};
	callTamurSocket(param, callback);
}

function signKalkan(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"Function": "sign",
		"Param": {
			"store": options.store,
			"path": options.path,
			"pass":options.pass,
			"auth": options.auth,
			"content":options.content
		}
	};
	callTamurSocket(param, callback);
}

function wsSelectFile(options, callback){
	var param = {
		"Function": "selectFile",
		"Param": {
			"ext": options.ext,
			"desc": options.desc,
			"old": options.old
		}
	};

	callTamurSocket(param, callback);
}

////////////////////////////////////////////////

////////////////////////////////////////////////

function generateUcgoPKCS10(dn) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"dn":dn};
		readersLoaded("PKCS10", data, params);
	});
}

function generateUcgoPKCS7(content) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"content":content};
		readersLoaded("PKCS7", data, params);
	});
}

function readersLoaded(processName, data, params) {
	console.log ("readersLoaded data: " + JSON.stringify(data));
	console.log ("readersLoaded params: " + JSON.stringify(params));

	var par = {};
	par["ucgores"] = "";
	
	//data.readers = [{"code":"DigiFlow LLP. KAZTOKEN 0", "name":"KAZToken"}, {"code":"Aladdin Token JC 0", "name":"e-Token"}];
	
	if (data.error) {
		post(par);
		alert(data.error);
	} else if (data.readers.length == 0) {
		post(par);
		alert("Вставьте ключевой контейнер!");
	} else {
		
		if (params.reader && params.pass) {
    		if (processName == 'PKCS7') {
    			var options = {"reader":params.reader, "pass":params.pass};
    			getCertificates(options, function(data) {
        			var params = {"reader":reader, "pass":pass, "content":params.content, "processName":"PKCS7"};
    				selectUcgoCertificateFromList(data, params);
    			});
    		} else if (processName == 'LOAD_CERTS') {
    			var options = {"reader":params.reader, "pass":params.pass};
    			
    			if (params.iin != null) {
    				options.iin = params.iin;
    			}
    			if (params.bin != null) {
    				options.bin = params.bin;
    			}

    			getCertificates(options, selectUcgoCertificateFromList);
    		}
		} else {
			var dialogId = 'or3_popup' + window.popupcount;
			$('#trash').append($("<div></div>").attr('id', dialogId));
			window.dialogResult[dialogId] = '1';
		
			var buttonOk = {
				text: translation['ok'],
				handler: function() {
					window.dialogResult[dialogId] = '0';
					$("#" + dialogId).dialog('destroy');
				}
			};
			
			var buttonCancel = {
				text: translation['cancel'],
				handler: function() {
					window.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				}
			};
			
			var buttons = [buttonOk, buttonCancel];
			var oldZindex = $.fn.window.defaults.zIndex;
			$.fn.window.defaults.zIndex = dialogZindex++;
			
			$('#' + dialogId).dialog({
				title: translation['keystore'],
				width: 360,
				height: 200,
				closed: false,
				cache: false,
				closable: true,
				href: window.contextName + '/jsp/selectKeyPairConteiner.jsp',
				modal: true,
				onClose: function() {
					window.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				},
				onBeforeDestroy : function() {
					if (window.dialogResult[dialogId] == '0') {
						var reader = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
						
		        		var uid = generateUID();
		        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
	
		        		if (processName == 'PKCS10') {
		        			var options = {"reader":reader, "pass":pass, "uid":uid, "dn": params.dn};
		        			createPKCS10(options, pkcs10Generated);
		        		} else if (processName == 'PKCS7') {
		        			var options = {"reader":reader, "pass":pass, "content":params.content};
		        			getCertificates(options, function(data) {
			        			var params2 = {"reader":options.reader, "pass":options.pass, "content":options.content, "processName":"PKCS7"};
		        				selectUcgoCertificateFromList(data, params2);
		        			});
		        		} else if (processName == 'LOAD_CERTS') {
		        			var options = {"reader":reader, "pass":pass};
		        			
		        			if (params.iin != null) {
		        				options.iin = params.iin;
		        			}
		        			if (params.bin != null) {
		        				options.bin = params.bin;
		        			}

		        			getCertificates(options, selectUcgoCertificateFromList);
		        		} else if (processName == 'DEL_CERT') {
		        			var options = {"reader":reader, "pass":pass, "uid":params.uid};
		        			
		        			deleteCertificate(options, certOperationProcessed);
		        		}
					} else {
						post(par);
					}
		        	$.fn.window.defaults.zIndex = oldZindex;
				},
				onLoad: function() {
					var options = '';
					for (var ind = 0; ind < data.readers.length; ind++) {
						var info  = data.readers[ind];
						options += '<option value="' + info.code + '">' + info.name + '</option>';
					}
					$('#' + dialogId).find('[uid="ucgoConteinerType"]').html(options);
				},
				buttons: buttons
			});
			
			window.popupcount++;
		}
	}
}

function pkcs10Generated(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		alert(data.error);
	} else {
		par["ucgores"] = data.obj.pkcs10 + '|' + data.obj.reader + '|' + data.obj.dn + '|' + data.obj.uid + '|' + data.obj.keyPair + '|' + data.obj.serial;
	}
	post(par);
}

function pkcs7Generated(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		alert(data.error);
	} else {
		par["ucgores"] = data.pkcs7;
	}
	post(par);
}

function haveUcgoCertificate(reader, uid, tokPD) {
	var par = {};
	par["ucgores"] = "";

	if (tokPD == null) {
		var dialogId = 'or3_popup' + window.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		window.dialogResult[dialogId] = '1';
	
		var buttonOk = {
			text: translation['ok'],
			handler: function() {
				window.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
			}
		};
			
		var buttonCancel = {
			text: translation['cancel'],
			handler: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttons = [buttonOk, buttonCancel];
			
		var oldZindex = $.fn.window.defaults.zIndex;
		$.fn.window.defaults.zIndex = dialogZindex++;
	
		$('#' + dialogId).dialog({
			title: translation['enterPassword'],
			width: 360,
			height: 200,
			closed: false,
			cache: false,
			closable: true,
			href: window.contextName + '/jsp/enterPassword.jsp',
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0') {
					tokPD = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					
					var options = {"reader":reader, "pass":tokPD, "uid":uid};
					isCertExists(options, certOperationProcessed);
				} else {
					post(par);
				}
	        	$.fn.window.defaults.zIndex = oldZindex;
			},
			buttons: buttons
		});
		
		window.popupcount++;
	} else {
		var options = {"reader":reader, "pass":tokPD, "uid":uid};
		isCertExists(options, certOperationProcessed);
	}
}

function selectUcgoCertificate(iin, bin) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"iin":iin, "bin":bin};
		readersLoaded("LOAD_CERTS", data, params);
	});
}

function selectUcgoCertificateFromList(data, params) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		alert(data.error);
		post(par);
		return;
	}

	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	var buttonOk = {
		text: translation['ok'],
		handler: function() {
			window.dialogResult[dialogId] = '0';
			$("#" + dialogId).dialog('destroy');
		}
	};
	
	var buttonCancel = {
		text: translation['cancel'],
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	
	var buttons = [buttonOk, buttonCancel];
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = dialogZindex++;
	
	$('#' + dialogId).dialog({
		title: translation['keystore'],
		width: 360,
		height: 200,
		closed: false,
		cache: false,
		closable: true,
		href: window.contextName + '/jsp/selectCert.jsp',
		modal: true,
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
        	$.fn.window.defaults.zIndex = oldZindex;

			if (window.dialogResult[dialogId] == '0') {
				var certInd = $('#' + dialogId).find('[uid="ucgoCert"]').val();
				var info  = data.certs[parseInt(certInd)];
				var subject = info.dn;
				
	            var dnArray = subject.split(',');
                var iin = "";
                var bin = "";
                var email = "";

	            for (var i = 0; i < dnArray.length; i++) {
	                var uzel = dnArray[i].trim().split('=');
	                if (uzel[0] == 'SERIALNUMBER') {
	                    var result = /[0-9]+/.exec(uzel[1]);
	                    iin = result[0];
	                }
	                if (uzel[0] == 'OU') {
	                    var result = /[0-9]+/.exec(uzel[1]);
	                    bin = result[0];
	                }
	                if (uzel[0] == 'E') {
	                    email = uzel[1];
	                }
	            }

	            if (params != null && params.processName == 'PKCS7') {
        			var options = {"reader":params.reader, "pass":params.pass, "uid":info.uid, "content": params.content};
        			createPKCS7(options, pkcs7Generated);
	            } else {
	            	par["ucgores"] = info.cert + COLUMN_SEPARATOR + subject + COLUMN_SEPARATOR +
								iin + COLUMN_SEPARATOR + bin + COLUMN_SEPARATOR + email +
								COLUMN_SEPARATOR + info.uid;
	    			post(par);
	            }
			} else {
				post(par);
			}
		},
		onLoad: function() {
			var options = '';
			for (var ind = 0; ind < data.certs.length; ind++) {
                var info = data.certs[ind];
                if (info.cert != null && info.cert.length > 0) {
                	options += '<option value="' + ind + '">' + info.name + '</option>';
                }
			}
			$('#' + dialogId).find('[uid="ucgoCert"]').html(options);
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function saveUcgoCertificate(cert, reader, uid, tokPD) {
	var par = {};
	par["ucgores"] = "";

	if (tokPD == null) {
		var dialogId = 'or3_popup' + window.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		window.dialogResult[dialogId] = '1';
	
		var buttonOk = {
			text: translation['ok'],
			handler: function() {
				window.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
			}
		};
			
		var buttonCancel = {
			text: translation['cancel'],
			handler: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttons = [buttonOk, buttonCancel];
			
		var oldZindex = $.fn.window.defaults.zIndex;
		$.fn.window.defaults.zIndex = dialogZindex++;
	
		$('#' + dialogId).dialog({
			title: translation['enterPassword'],
			width: 360,
			height: 200,
			closed: false,
			cache: false,
			closable: true,
			href: window.contextName + '/jsp/enterPassword.jsp',
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0') {
					tokPD = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					
					var options = {"reader":reader, "pass":tokPD, "uid":uid, "cert":cert};
					saveCertificate(options, certOperationProcessed);
				} else
					post(par);
	        	$.fn.window.defaults.zIndex = oldZindex;
			},
			buttons: buttons
		});
		
		window.popupcount++;
	} else {
		var options = {"reader":reader, "pass":tokPD, "uid":uid, "cert":cert};
		saveCertificate(options, certOperationProcessed);
	}
}

function deleteUcgoCertificate(uid) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"uid":uid, "processName":"DEL_CERT"};
		readersLoaded("DEL_CERT", data, params);
	});
}

function certOperationProcessed(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		alert(data.error);
	} else {
		par["ucgores"] = data.res;
	}
	post(par);
}


function signDialog(str, path, pass, cont) {
	var par = {};
	par["signres"] = "";


	var keyPath = path;
	var keyPass = pass;
	var keyCont = (cont != null && cont.length > 0) ? cont : '17';

	if (keyPass == null || keyPath == null || keyPath.length == 0) {
		var dialogId = 'or3_popup' + window.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		window.dialogResult[dialogId] = '1';
	
		var buttonOk = {
			text: translation['sign'],
			handler: function() {
				window.dialogResult[dialogId] = '0';
				
	        	keyPass = $('#' + dialogId).find('[uid="ecpPass"]').val();
	        	keyCont = $('#' + dialogId).find('[uid="ecpType"]').val();
	        	
	        	if (keyCont == "17") {
		        	keyPath = $('#' + dialogId).find('[uid="ecpFile"]').val();
	        	} else {
		        	keyPath = $('#' + dialogId).find('[uid="ecpCert"]').val();
	        	}
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttonCancel = {
			text: translation['cancel'],
			handler: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttons = [buttonOk, buttonCancel];
		
		var oldZindex = $.fn.window.defaults.zIndex;
		$.fn.window.defaults.zIndex = dialogZindex++;
		
		$('#' + dialogId).dialog({
			title: translation['keystore'],
			width: 430,
			height: 200,
			closed: false,
			cache: false,
			closable: true,
			href: window.contextName + '/jsp/ecp2.jsp',
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0' && keyPath != null && keyPath.length > 0) {
					if (keyPass != null && keyPass.length > 0) {
						var options = {"store":keyCont, "path":keyPath, "pass":keyPass, "auth": "false", "content": str};
	        			signKalkan(options, function(data) {
	        				afterSign(data, keyPath, keyPass, keyCont);
	        			});
					} else {
						askPassword(function(askedPass) {
							if (askedPass != null) {
								var options = {"store":keyCont, "path":keyPath, "pass":askedPass, "auth": "false", "content": str};
			        			signKalkan(options, function(data) {
			        				afterSign(data, keyPath, askedPass, keyCont);
			        			});
							} else {
								post(par);
							}
						}, null);
					}
				} else {
		        	post(par);
				}
	    		$.fn.window.defaults.zIndex = oldZindex;
			},
			onLoad: function() {
				if (path != null && (path.indexOf("/") > -1 || path.indexOf("\\") > -1))
					$('#' + dialogId).find('[uid="ecpFile"]').val(path);
				
				$('#' + dialogId).find('[uid="ecpType"]').val(keyCont);
				conteinerSelected('ecpType');
			},
			buttons: buttons
		});
		
		window.popupcount++;
	} else {
		var options = {"store":keyCont, "path":keyPath, "pass":keyPass, "auth": "false", "content": str};
		signKalkan(options, function(data) {
			afterSign(data, keyPath, keyPass, keyCont);
		});
	}
}

function afterSign(data, keyPath, keyPass, keyCont) {
	var par = {};
	par["signres"] = "";
	par["path"] = keyPath;

	if (data.error) {
		alert(data.error);
	} else {
		par["signres"] = data.obj.sign;
		par["cert"] = data.obj.cert;
		par["path"] = keyPath;
		par["code"] = keyPass;
		par["cont"] = keyCont;
	}

	post(par);
}

function conteinerSelected(uid) {
	// Обнуляем список сертификатов токена
	var options = '';
	$('[uid="ecpCert"]').html(options);

	// Выделенный контейнер
	var cid = $('[uid="' + uid + '"]').val();
	if (cid == "17") {
		// Если файл, то даем выбрать файл
		$('#ecpFileRow').show();
		$('#ecpKeyRow').hide();
	} else {
		// Иначе прячем выбор файла
		$('#ecpKeyRow').show();
		$('#ecpFileRow').hide();

		// Проверяем подключен ли контейнер
		loadToken(cid, function(data) {
			afterLoadToken(data, cid);
		});
	}
}

function afterLoadToken(data, keyCont) {
	if (data.error) {
		alert(data.error);
	} else if (data.res == false || data.res == 'false') {
		alert("Устройство не подключено!");
	} else {
		// Запрашиваем пароль для устройства и выполняем функцию
		askPassword(function(pd) {
			if (pd != null) {
				$('[uid="ecpPass"]').val(pd)
					    			
				blockPage("Считывание доступных ключей на ключевом контейнере...");
	
				// Если устройство подключено, считываем список сертификатов
				var options = {"store":keyCont, "pass":pd};
				var certs = getStoreEntries(options, afterLoadCerts);
			}
		}, null);
	}
}

function afterLoadCerts(data) {
	$('body').unblock();
	if (data.error) {
		alert(data.error);
	} else {
		var options = '';
		for (var ind = 0; ind < data.certs.length; ind++) {
            var info = data.certs[ind];
        	options += '<option value="' + info.uid + '">' + info.name + '</option>';
		}
		$('[uid="ecpCert"]').html(options);
	}
}

function selectFile(uid) {
	try {
		var options = {"ext":"p12", "desc":translation['p12files'], "old": $('[uid="' + uid + '"]').val()};
		wsSelectFile(options, function(data) {
			afterSelectFile(data, uid);
		});
	} catch (e) {
		alert(appletCallFailMsg);
	}
}

function afterSelectFile(data, ecpUid) {
	if (data.error) {
		alert(data.error);
	} else {
		$('[uid="' + ecpUid + '"]').val(data.file);
  	}
}

//function post(param) {
//	console.log("POST: " + JSON.stringify(param));
//}