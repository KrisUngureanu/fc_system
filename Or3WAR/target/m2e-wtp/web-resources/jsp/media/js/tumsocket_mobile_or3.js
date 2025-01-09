/* соединение с локальной программой для работы с ЭЦП (Cryptosocket) */
var socket;
/* флаг успешного подключения к Cryptosocket */
var isConnect = false;
/* Сообщение при невозможности подключения к ПО ЭЦП */
var connectServerFailMsg = "Не удалось подключиться к программе KAZTOKEN Mobile!";

/* мапа с коллбэками */
var callbacks = {};

/* порядковый номер запроса к Cryptosocket */
var num = 1;

var COLUMN_SEPARATOR = "|col|";

var lastTumSocketId = "";

/* вызываем функцию, после отработки запроса по идентификатору */
function callCallback(event, callback) { 
	var dataStr = event.data;
	console.log("Event.data: " + dataStr);
	var data = JSON.parse(dataStr);

	if (data.errorCode == "NONE" || data.code == "200") {
		try {
			callback(data);
		} catch (e) {
			console.log(e);
			console.log(e.message);
		}
	} else {
       	console.log("Функция отработала с ошибкой");
        console.log("Код ошибки " +  (data.errorCode != null ? data.errorCode : data.code));
	}
}

/* Подключение к веб-сокету программы Cryptosocket для последующего вызова ее функций */
function connectTamurSocket(notify) {
	if (!isConnect) {
		/* Если соединение ранее не было установлено, то пытаемся установить */
		/* блокировка окна браузера с сообщением ожидания */
		block('Подключение к KAZTOKEN Mobile...');
		/* Открытие веб-сокета к Cryptosocket */
		createTamurSocket(notify);
	} else {
		/* Если соединение уже было ранее установлено, то рапортуем на сервер */
		if (notify)
			post({"ucgosocketconn":true});
	}
}

/* Открытие веб-сокета к Cryptosocket */
function createTamurSocket(notify) {
	/* открытие веб-сокета */
	socket = new WebSocket("wss://127.0.0.1:13579");
	
	/* после успешного подключения */
	socket.onopen = function() {
		/* разблокировать окно браузера */
		unblock();
		/* установить флаг наличия подключения */
		isConnect = true;
		
		console.log("connection opened");

		// оповестить веб-клиент о удачной попытке подключения к Cryptosocket
		if (notify) {
			post({"ucgosocketconn":true});
		}
	}
	/* при закрытии подключения к Cryptosocket */
	socket.onclose = function(){
		/* сбросить флаг подключения */
		isConnect = false;
		console.log("connection closed");
	}
	/* при ошибке установления связи */
	socket.onerror = function(event) {
		/* вывод в лог события */	
		console.log("connection error");
		console.log(event);

		// оповестить веб-клиент о неудачной попытке подключения к Cryptosocket
		if (notify) {
			post({"ucgosocketconn":false});
		}
		/* разблокировать окно браузера */
		unblock();
		/* вывести сообщение о невозможности подключения к Cryptosocket */
		alert(connectServerFailMsg);
		return;
	}
}

/* блокировка окна браузера */
function block(msg) {
    $.blockUI({
        message: msg,
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

/* разблокировка окна браузера */
function unblock() {
	appletLoading = false;
	$.unblockUI();
}

/** Универсальная функция для обращения к Cryptosocket. Информация о функционале заложена в param (json-объект), где содержится 
	название функции в ПО ЭЦП и параметры запроса. Например:
	{
		"TumarCSP": "BaseAPI",
		"Function": "GetProfInfo",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
		}
	}
	Описание функций доступно по адресу https://127.0.0.1:6129/

	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/

function callCryptosocket(param, callback) {
	// Вывод в лог параметров запроса
	console.log(JSON.stringify(param));
	// если метод вызван до подсоединения к ПО ЭЦП
	if (!isConnect) {
		// вызвать функцию ошибки - просто показать сообщение пользователю
		errorConnectCallBack("KAZTOKEN Mobile не запущен!");
		return null;
	}
	// установить для сокета функцию, выполняемую после получения результата обращения
	socket.onmessage = (msg) => {
		callCallback(msg, callback); 
	};
	// обратиться к ПО ЭЦП
	socket.send(JSON.stringify(param));
}

/* Функция, вызываемая по умолчанию при ошибке обращения к Cryptosocket */
function errorConnectCallBack(msg) {
	var par = {};
	par["signres"] = "";
	par["ucgores"] = "";
	par["errcode"] = "10";
	par["errmsg"] = msg;
	alert(msg);
	post(par);
}
////////////////////////////////////////////////////////////////////////////////////
//             Функции, вызываемые из ОР3                                         //
/////////////////////////////////////////.//////////////////////////////////////////
/** Функция для генерации запроса на выпуск ЭЦП в формате PKCS10.
	dn - полное DN-имя человека, на которого запрашивается получение ЭЦП
*/
function generateUcgoPKCS10(dn, auth) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"dn":dn, "auth": auth};
		readersLoaded("PKCS10", data, params);
	});
}

/** Формирование подписи в формате PKCS7 
	content - строка для подписи 
*/
function generateUcgoPKCS7(content, auth) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"content":content, "auth": auth};
		readersLoaded("PKCS7", data, params);
	});
}

/** Формирование подписи в формате PlainText 
	content - строка для подписи (не зашифрованная)
*/
function generateUcgoSign(content, sn, pass, profile, auth) {
	if (profile != null && pass != null && pass.length > 0 && sn != null) {
		var options = {"profile":profile, "pass":pass, "sn":sn, "content": content, "isConvert": false};
    	signUCGO(options, function(data2) {
    		var options2 = {"profile":profile, "pass":pass, "sn":sn};
       		signGenerated(data2, options2);
       	});
	} else {
		var readers = getConnectedTokens(null, function(data) {
			var params = {"content":content, "auth": auth};
			readersLoaded("SIGN", data, params);
		});
	}
}

/** 
	Предоставить выбор сертификатов на основе предоставленных iin, bin
*/
function selectUcgoCertificate(iin, bin) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"iin":iin, "bin":bin};
		readersLoaded("LOAD_CERTS", data, params);
	});
}

function haveUcgoCertificate(profile, uid, pass) {
	var options = {"profile":profile, "pass":pass};
		        			
	getCertificates(options, function(data) {
		var params2 = {"uid":uid, "processName":"HAVE_CERT"};
		selectUcgoCertificateFromList(data, params2);
	});
}

function deleteUcgoCertificate(uid) {
	var readers = getConnectedTokens(null, function(data) {
		var params = {"uid":uid, "processName":"DEL_CERT"};
		readersLoaded("DEL_CERT", data, params);
	});
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


////////////////////////////////////////////////////////////////////////////////////
//             Вспомогательные функции, обращающиеся к Cryptosocket               //
/////////////////////////////////////////.//////////////////////////////////////////

/** Вызов функции для проверки подключенных типов токенов (Казтокен, еТокен, УДЛ и др.) 
	options - json-объект, не используется
	callback - функция, вызываемая после возврата ответа Cryptosocket. Получает входной параметр в виде json-объекта
*/
function getConnectedTokens(options, callback) {
	var param = {
		"module": "kz.gov.pki.knca.commonUtils",
    	"method": "getActiveTokens"
	};
	callCryptosocket(param, callback);
}

function getTokensInfo(tokens, callback) {
	var param = {
		"module": "kz.digiflow.mobile.extensions",
  		"method": "getTokensInfo",
  		"args": [
    		"AKKaztokenStore"
  		]
	};
	callCryptosocket(param, callback);
}

/** Вызов функции для получения информации о токене (в основном для серийного номера)
	options - json-объект
		profile - профайл токена
		pass - пароль к токену
	callback - функция, вызываемая после возврата ответа Cryptosocket. Получает входной параметр в виде json-объекта
*/
function getTokenInfo(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "GetProfInfo",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
		}
	};
	callCryptosocket(param, callback);
}

/** Вызов функции для получения информации о сформированном PKCS10 (для отправки в УЦГО данных об открытом ключе)
	options - json-объект
		data - PKCS10 запрос в кодировке Base64
	callback - функция, вызываемая после возврата ответа Cryptosocket. Получает входной параметр в виде json-объекта
*/
function ShowInfoPKCS10(options, callback){
	var param = {
		"TumarCSP": "ASNAPI",
		"Function": "ShowInfoPKCS10",
		"Param": {
			"data":options.data,
		}
	};
	callCryptosocket(param, callback);
}

/** Вызов функции для получения сертификатов на носителе
	options - json-объект, содержит идентификатор запроса, тип устройства (KAZToken, e-Token), пароль,
	bin (не обязательно), iin (не обязательно) {"id": 1, "reader": "KAZToken", "pass": "1234567890", "iin": "800501401709"}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function getCertificates(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "LoadKeyFromProfile",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
		}
	};
	callCryptosocket(param, callback);
}

function createPKCS7(options, callback){
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "Sign",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
			"sn":options.uid,
			"detach":true,
			"isConvert":options.isConvert,
			"isCert":true,
			"data":options.content,
			"hashType":0
		}
	};
	callCryptosocket(param, callback);
}

function signUCGO(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "NativeSign",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
			"sn":options.sn,
			"data":options.content,
			"isConvert":options.isConvert,
			"hashType":0,
		}
	};
	callCryptosocket(param, callback);
}

function signGenerated(data, params) {
	var loginECPTypeElement = document.getElementById("loginECPType");
	if (loginECPTypeElement != null) {
		if (data.error) {
			alert("Не обнаружен ключевой контейнер или ЭЦП на нем!");
		} else {
			loginWithUCGO(data, params);
		}
	} else {
		var par = {};
		par["signres"] = "";
	
		if (data.error) {
			par["errcode"] = "4";
			par["ucgoerrcode"] = data.code;
			par["ucgoerrmsg"] = data.error;
			par["errmsg"] = "Не обнаружен ключевой контейнер или ЭЦП на нем!";
			alert("Не обнаружен ключевой контейнер или ЭЦП на нем!");
		} else {
			par["signres"] = data.response;
			par["cert"] = params.cert;
			par["path"] = params.sn;
			if (instantECP)
				par["code"] = params.pass;
			par["cont"] = params.profile;
		}
		post(par);
	}
}

function pkcs7Generated(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		par["errcode"] = "5";
		par["ucgoerrcode"] = data.code;
		par["ucgoerrmsg"] = data.error;
		par["errmsg"] = "Ошибка при формировании PKCS7!";
		alert("Ошибка при формировании PKCS7!");
	} else {
		par["ucgores"] = data.pkcs7;
	}
	post(par);

	var options = {"isConvert":false, "data":"444444444444444444444", "sign":data.pkcs7};
	VerifyPKCS7(options, afterCerifypkcs7);
}

function createPKCS10(options, callback, auth) {
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "CreatePKCS10",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
			"dn":options.dn,
			"uid":"ERIK1234",
			"templ":"CN=GOST_RAUTIL_USER_1Y,O=Template,C=KZ",
			"isExport":1,
			"genNew":0,
			"keyType": (auth) ? 1 : 0,
			"keySize":0,
			"isAltName":false,
			"hashType":0,
		}
	};
	callCryptosocket(param, callback);
}

function pkcs10Generated(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		par["errcode"] = "5";
		par["ucgoerrcode"] = data.code;
		par["ucgoerrmsg"] = data.error;
		par["errmsg"] = "Ошибка при формировании PKCS10!";
		alert("Ошибка при формировании PKCS10!");
	} else {
		par["ucgores"] = data.response;
	}
	post(par);
}



/* Функция вызывается автоматически после получения ответа от Cryptosocket о подключенных устройствах хранения ключей.
   Дальнейшие действия выполняются в зависимости от аргумента processName:
   PKCS7 - требуется сформировать подпись.
*/
function readersLoaded(processName, data, params) {
	console.log ("readersLoaded data: " + JSON.stringify(data));
	console.log ("readersLoaded params: " + JSON.stringify(params));

	var par = {};
	if (processName == 'SIGN')
		par["signres"] = "";
	else
		par["ucgores"] = "";
	
	/*
		data.response = [{"profile":"kztoken:\/\/user@\/DigiFlow%20LLP.%20KAZTOKEN%200?ext=tok","reader":"DigiFlow LLP. KAZTOKEN 0"},
				{"profile":"kztoken:\/\/user@\/DigiFlow%20LLP.%20KAZTOKEN%201?ext=tok","reader":"DigiFlow LLP. KAZTOKEN 1"}];
	*/
	
	if (data.error) {
		par["errcode"] = "1";
		
		if (data.code === '10001') {
			par["errmsg"] = "TumSocket not initialize";
			alert("TumSocket not initialize");
		} else {
			par["errmsg"] = "Вставьте ключевой контейнер!";
			alert("Вставьте ключевой контейнер!");
		}
		par["ucgoerrcode"] = data.code;
		par["ucgoerrmsg"] = data.error;
		post(par);
	} else if (data.response.length == 0) {
		par["errcode"] = "1";
		par["ucgoerrcode"] = "1";
		par["ucgoerrmsg"] = "";
		par["errmsg"] = "Вставьте ключевой контейнер!";
		alert("Вставьте ключевой контейнер!");
		post(par);
	} else {
		// Если выбрано устройство и введен пароль
		if (params.reader && params.pass) {
		// Если не выбрано устройство то запрашиваем устройство и пароль
		} else {
			var dialogId = 'or3_popup' + window.popupcount;
			var okId = 'ok' + window.popupcount;
			$('#trash').append($("<div></div>").attr('id', dialogId));
			window.dialogResult[dialogId] = '1';
		
			var loginECPTypeElement = document.getElementById("loginECPType");
			var buttonOk = {
				text: (loginECPTypeElement != null ? "Ок" : translation['ok']),
				id: okId,
				handler: function() {
					var profile = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
	        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					if (profile == null || pass == null || pass.length == 0) {
						alert('Выберите ключевой контейнер и введите пароль!');
					} else {
						window.dialogResult[dialogId] = '0';
						$("#" + dialogId).dialog('destroy');
					}
				}
			};
			
			var buttonCancel = {
				text: (loginECPTypeElement != null ? "Отмена" : translation['cancel']),
				handler: function() {
					window.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				}
			};
			
			var buttons = [buttonOk, buttonCancel];
			var oldZindex = $.fn.window.defaults.zIndex;
			$.fn.window.defaults.zIndex = dialogZindex++;
			
			$('#' + dialogId).dialog({
				title: (loginECPTypeElement != null ? "Хранилище" : translation['keystore']),
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
						var profile = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
		        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
	
	        			if (processName == 'PKCS10') {
		        			var options = {"profile":profile, "pass":pass, "dn": params.dn};
	        				createPKCS10(options, pkcs10Generated, params.auth);
		        			//var options = {
							//	"PKCS10":{"profile":profile, "pass":pass, "dn": params.dn},
							//	"PKCS7":{"profile":"kztoken:\/\/user@\/Aladdin%20Token%20JC%200?ext=tok", "pass":"1234567890", "sn": "7E6BD0C510A97B6DCF09F47CB99603B47BFEF55FA40773690020903ED706550E"},
							//};
	        				//CreateCertificateRequest(options, pkcs10Generated, params.auth);
	        			} else if (processName == 'PKCS7') {
	        				var options = {"profile":profile, "pass":pass};
		        			getCertificates(options, function(data) {
		        				var params2 = {"profile":options.profile, "pass":options.pass, "content":params.content, "processName":"PKCS7", "auth": params.auth};
		        				selectUcgoCertificateFromList(data, params2);
	        				});
	        			} else if (processName == 'SIGN') {
	        				var options = {"profile":profile, "pass":pass, "auth": params.auth};
		        			getCertificates(options, function(data) {
		        				var params2 = {"profile":options.profile, "pass":options.pass, "content":params.content, "processName":"SIGN", "auth": params.auth};
		        				selectUcgoCertificateFromList(data, params2);
	        				});
	        			} else if (processName == 'LOAD_CERTS') {
	        				var options = {"profile":profile, "pass":pass};
		        			
		        			getCertificates(options, function(data) {
		        				var params2 = {"bin":params.bin, "iin":params.iin, "processName":"LOAD_CERTS"};
		        				selectUcgoCertificateFromList(data, params2);
	        				});
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
					for (var ind = 0; ind < data.response.length; ind++) {
						var info  = data.response[ind];
						options += '<option value="' + info.profile + '">' + info.reader + '</option>';
					}
					$('#' + dialogId).find('[uid="ucgoConteinerType"]').html(options);
					if (data.response.length == 1) {
						$('#' + dialogId).find('[uid="ucgoConteinerType"]').closest('tr').hide();
					}
					$('#' + dialogId).find('[uid="ucgoConteinerPass"]').focus().keyup(function(e) {
						if (e.which && e.which == 13)
							$('#' + okId).click();
					});
				},
				buttons: buttons
			});
			
			window.popupcount++;
		}
	}
}

function selectUcgoCertificateFromList(data, params) {
	var par = {};
	if (params != null && params.processName == 'SIGN')
		par["signres"] = "";
	else
		par["ucgores"] = "";

	if (data.error) {
		par["errcode"] = "2";
		par["ucgoerrcode"] = data.code;
		par["ucgoerrmsg"] = data.error;
		par["errmsg"] = "Ошибка при чтении контейнера, проверьте пароль!";
		alert("Ошибка при чтении контейнера, проверьте пароль!");
		post(par);
		return;
	}

	var haveCerts = false;
	var dataFiltered = {};
	var certsCount = 0;

	for (var objCertificate in data) {
    	if (objCertificate.indexOf("certificate") >= 0) {
    		
    		var curCert = data[objCertificate];
    		
    		var usage = (curCert.usage && curCert.usage.length > 0) ? parseInt(curCert.usage) : 0;
    		var usageAuth = (usage & 32) > 0;
    		var usageSign = (usage & 64) > 0;

			if (params != null && (params.iin != null || params.bin != null)) {
				var subject = data[objCertificate].subjectDN;
				if (subject) {				
		        	if ((params.iin != null && subject.indexOf("IIN" + params.iin) > -1)
		        		|| (params.bin != null && subject.indexOf("OU=BIN" + params.bin) > -1)) {
		        
		        		if (params.auth == null || (params.auth && usageAuth) || (!params.auth && usageSign)) {
			            	haveCerts = true;
	        		    	dataFiltered[objCertificate] = data[objCertificate];
	        		    	certsCount++;
		        		}
		        	}
	            }
			} else 	if (params != null && params.uid != null) {
				var sn = data[objCertificate].serialNumber;
				if (sn && params.uid != null && sn == params.uid) {
	            	haveCerts = true;
       		    	dataFiltered[objCertificate] = data[objCertificate];
    		    	certsCount++;
	            }
			} else {
        		if (params.auth == null || (params.auth && usageAuth) || (!params.auth && usageSign)) {
	            	haveCerts = true;
	            	dataFiltered[objCertificate] = data[objCertificate];
			    	certsCount++;
        		}
            }	
		}
	}
	if (!haveCerts) {
		par["errcode"] = "3";
		par["ucgoerrcode"] = 0;
		par["ucgoerrmsg"] = '';
		par["errmsg"] = "Контейнер не содержит ЭЦП!";

		if (params != null && params.processName == 'HAVE_CERT') {
			par["ucgores"] = "false";
		} else {
			alert("Контейнер не содержит ЭЦП!");
		}	
		post(par);
		return;
	} else if (params != null && params.processName == 'HAVE_CERT') {
		par["ucgores"] = "true";
		post(par);
		return;
	}

	if (certsCount == 1 && params.processName == 'SIGN') {
		var info = null;
		for (var objCertificate in dataFiltered) {
			info = dataFiltered[objCertificate];
		}
		
		var options = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "content": params.content, "isConvert": false};
		signUCGO(options, function(data2) {
			var options2 = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "cert":info.certificateBlob};
			signGenerated(data2, options2);
		});
		return;
	}
	
	var dialogId = 'or3_popup' + window.popupcount;
	var okId = 'ok' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	var loginECPTypeElement = document.getElementById("loginECPType");
	var buttonOk = {
		text: (loginECPTypeElement != null ? "Ок" : translation['ok']),
		id: okId,
		handler: function() {
			window.dialogResult[dialogId] = '0';
			$("#" + dialogId).dialog('destroy');
		}
	};
	
	var buttonCancel = {
		text: (loginECPTypeElement != null ? "Отмена" : translation['cancel']),
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	
	var buttons = [buttonOk, buttonCancel];
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = dialogZindex++;
	
	$('#' + dialogId).dialog({
		title: (loginECPTypeElement != null ? "Хранилище" : translation['keystore']),
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
				var objCertificate = $('#' + dialogId).find('[uid="ucgoCert"]').val();
				var info  = dataFiltered[objCertificate];
				var subject = info.subjectDN.replace(/;/g, ',');
				
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
        			var options = {"profile":params.profile, "pass":params.pass, "uid":info.serialNumber, "content": params.content, "isConvert": true, "hashType": 0};
        			createPKCS7(options, pkcs7Generated);
				} else if (params != null && params.processName == 'SIGN') {
        			var options = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "content": params.content, "isConvert": false};
        			signUCGO(options, function(data2) {
	        			var options2 = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "cert":info.certificateBlob};
        				signGenerated(data2, options2);
        			});
	            } else {
	            	par["ucgores"] = info.certificateBlob + COLUMN_SEPARATOR + subject + COLUMN_SEPARATOR +
								iin + COLUMN_SEPARATOR + bin + COLUMN_SEPARATOR + email +
								COLUMN_SEPARATOR + info.serialNumber;
	    			post(par);
	            }
			} else {
				post(par);
			}
		},
		onLoad: function() {
			var options = '';
			for (var objCertificate in dataFiltered) {
		    	if (objCertificate.indexOf("certificate") >= 0) {
	                var info = dataFiltered[objCertificate];
	                if (info.certificateBlob != null && info.certificateBlob.length > 0) {
						var subject = info.subjectDN;
				
			            var dnArray = subject.split(';');
                		var fio = "";

			            for (var i = 0; i < dnArray.length; i++) {
	        		        var uzel = dnArray[i].trim().split('=');
	    		            if (uzel[0] == 'CN') {
	            		        fio = uzel[1];
			                }
	    		        }

                		var usage = (info.usage && info.usage.length > 0) ? parseInt(info.usage) : 0;
                		var usageAuth = (usage & 32) > 0;
						var usageSign = (usage & 64) > 0;
						
						var usageTxt = (usageAuth && usageSign) ? "(all)" : (usageAuth ? " (auth)" : " (sign)");
						
                		options += '<option value="' + objCertificate + '">' + fio + usageTxt + '</option>';
					}
                }
			}
			$('#' + dialogId).find('[uid="ucgoCert"]').html(options).focus().keyup(function(e) {
				if (e.which && e.which == 13)
					$('#' + okId).click();
			});
		},
		buttons: buttons
	});
	
	window.popupcount++;
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
	callCryptosocket(param, callback);
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
	callCryptosocket(param, callback);
}

function certOperationProcessed(data) {
	var par = {};
	par["ucgores"] = "";

	if (data.error) {
		alert(data.error);
		par["errcode"] = "5";
		par["ucgoerrcode"] = data.code;
		par["ucgoerrmsg"] = data.error;
	} else {
		par["ucgores"] = data.res;
	}
	post(par);
}


/// Попозже пригодятся функции

function afterVerifyPKCS7(data) {
	post(data);
}

function VerifyPKCS7(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "VerifyPKCS7",
		"Param": {
			"isConvert":options.isConvert,
			"data":options.data,
			"hashType":1,
			"sign":options.sign,
		}
	};
	callCryptosocket(param, callback);
}

function initialRegistrationRequest(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "initialRegistrationRequest",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
			"uid":options.uid,
			"secret":options.secret,
			"keyOID":options.keyOID,
		}
	};
	callCryptosocket(param, callback);
}

function CreateCertificateRequest(options, callback, auth) {
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "CreateCertificateRequest",
		"Param": {
			"PKCS10":{
				"profile":options.PKCS10.profile,
				"pass":options.PKCS10.pass,
				"dn":options.PKCS10.dn,
				"templ":"CN=GOST_RAUTIL_USER_1Y,O=Template,C=KZ",
				"isExport":1,
				"genNew":0,
				"keyType": (auth) ? 1 : 0,
				"keySize":0,
				"isAltName":false,
				"hashType":0,
			},
			"PKCS7":{
				"profile":options.PKCS7.profile,
				"pass":options.PKCS7.pass,
				"sn":options.PKCS7.sn,
				"isCert":true,
				"hashType":0,
			}
		}
	};
	callCryptosocket(param, callback);
}


///////////////////////////////////////////////


/* Считываем подключенные токены */
function LoadKeyFromTokens(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "LoadKeyFromTokens",
		"Param": {
			"plugin_name":options.plugin_name,
			"pass":options.pass,
			"profile":options.profile
		}
	};
	callCryptosocket(param, callback);
}
function LoadKeyFromProfile(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "LoadKeyFromProfile",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
		}
	};
	callCryptosocket(param, callback);
}
function GetAllCertificate(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "GetAllCertificate",
		"Param": {
			"id":options.id,
		}
	};
	callCryptosocket(param, callback);
}

function GetAllKeys(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "GetAllKeys",
		"Param": {
			"url":options.url,
		}
	};
	callCryptosocket(param, callback);
}


/////////////////////////////////////
// Важные вспомогательные функции
/////////////////////////////////////

function b64DecodeUnicode(str) {
    return atob(str).split('').map(function(c) {
        return ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join('').toUpperCase();
}

function b64EncodeUnicode(str) {
    return btoa(hexToStr(str));
}

function hexToStr(str) {
	console.log("before: " + str.toUpperCase());
	str = str.toUpperCase().replace(/([0-9A-F]{2})/g,
        	function toSolidBytes(match, p1) {
			return String.fromCharCode('0x' + p1);
	});
	console.log("after: " + str);
	return str;
}