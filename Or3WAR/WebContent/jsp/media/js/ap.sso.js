/* соединение с локальной программой для работы с ЭЦП (Cryptosocket) */
var ssoSocket;
/* флаг успешного подключения к Cryptosocket */
var isConnectSSO = false;
/* Сообщение при невозможности подключения к ПО ЭЦП */
var connectServerFailMsgSSO = "Не удалось подключиться к программе CryptoSocket!";

/* мапа с коллбэками */
var callbacksSSO = {};

/* порядковый номер запроса к Cryptosocket */
var numSSO = 1;

var lastSSOSocketId = "";

var apiKeysSSO = [
	// 129.0.100.162
	"AgGCMDXkjV4si4qA7XZlHqA6sGJDChQL5fqrNsh1K5y0+DKL1UmE1N+iZgcRbh4SwAU8pgOB+l8dHmh16M58F4eXy3IyMDk5MTIzMTIzNTk1OVoAEJkDdLRIAP5gImwb3nZGvEgnoRCKiS+iGc8TFa/oiRtt5QA//3QkN9+n4qzd4/RiDCveFB8NTLVns4J4PDgi9g==",
	// 129.0.100.204
	"AgGCMHdxMYUeBokvtBTvDVbwIo3+zLPj5pOtAAtmiJXQM/v3hgOWmUiceXu67/nKrNJX2rzinh7S/nzxEsN84PYJPW8yMDk5MTIzMTIzNTk1OVoALuzulDPVFjSZ44GSmkmzys5FHPmKgwEmVY03mhmyyAtZtNiZu+iHDwvgTo9DKHkOrnjB9wgh+NiREwuoLi1luw==",
	// mgs.kz
	"AgGCMEKXSr8V/f++V/SDt4IhJs/MgRnxPQvSXJld6avJwVPijFTd5AGe+2kyuFqbbyWVzUwh/EfGW/FLV2Lr0xuG2koyMDk5MTIzMTIzNTk1OVoADmO50fxp+vr7kvMFK7BCEr9ccUDGgplj75ZG6FGR3OxAGcqkPUwPMasvhJV/u7wbCF6mhF/DugeqTdCKQviJwg==",
	// 10.61.42.73
	"AgGCMIAFtF6811MirYRyD4HPCcyJdaQRHLV2bDZvoqrr5ZNfoTEDzIx5Sipyxji8KN5aDjg+oj7AcrlHB0lkUjXkeeMyMDIxMDQwMTIzNTk1OVoABbND5j/qwOdXAQi+XFHaTOCn0rLccfd09s+3v41WkiCzfxZ6LyV4JtHzdHw7tT5FglhYtfHsd0qKgDFIEz2UQw==",
	// 10.245.12.73
	"AgGCMAq++VIk5wpV2XnOhm9j+qb/Npgl2UD3u6befQ3TRz81a93WP3RhTbb8vrqoE1CWJyCjcqr09PCy4D/FuxhAbtIyMDIxMDQwMTIzNTk1OVoAX03H8LxUFt/wLN6Z5N6QN8nEKwMPVp5glW2vpbjc7rkWgYI8iB6ADF4xYr+bmDDRzn4JS0ndmAZCId6RbLnzpg==",
	// ekyzmet.kz
	"AgGCMDBhZq+6RViYw+0MaUGG8A6/sxE6OvgsZmwPpcG4DjxCyI3hGXLY+WAphl3aYlgl06jIN8+lzlya5MxqOnO1dlgyMDIxMDQwMTIzNTk1OVoAftEYqe0V72YilapHBocabJ2/pwG9Q86m+PzKd0IaK5lMEAzf6vHiJ4mMed9u1rOX9vAiabJB7kG7IFQBwOxFvw=="
];

// для IE11 - реализация функции padStart
if (!String.prototype.padStart) {
    String.prototype.padStart = function padStart(targetLength,padString) {
        targetLength = targetLength>>0; //truncate if number or convert non-number to 0;
        padString = String((typeof padString !== 'undefined' ? padString : ' '));
        if (this.length > targetLength) {
            return String(this);
        }
        else {
            targetLength = targetLength-this.length;
            if (targetLength > padString.length) {
                padString += padString.repeat(targetLength/padString.length); //append to original to ensure we are longer than needed
            }
            return padString.slice(0,targetLength) + String(this);
        }
    };
}

/* Функция генерации уникального номера запроса к Cryptosocket */
function generateCryptoRequestIdSSO() {
	var id = 'REQ_ID_' + ('000' + numSSO.toString()).substr(-4);
	numSSO++;
	console.log("ID: " + id);
	lastSSOSocketId = id;
	return id;
}

/* регистрируем функцию, вызываемую после отработки запроса по идентификатору */
function registerCallbackSSO(id, callback) { 
	callbacksSSO[id] = callback;
}

/* вызываем функцию, после отработки запроса по идентификатору */
function callCallbackSSO(event) { 
	var dataStr = event.data;
	console.log("Event.data: " + dataStr);

	var data = JSON.parse(dataStr);
   	console.log("Идентификатор " + data.id);

	var callback = null;
	
	var callbackId = (data.id) ? data.id : lastSSOSocketId;
	if (callbackId) {
		callback = callbacksSSO[callbackId];
		delete callbacksSSO[callbackId];
	}

	if (data.result == "true") {
	} else {
       	console.log("Функция отработала с ошибкой");
        console.log("Код ошибки " +  data.code);
       	console.log("Описание ошибки " +  data.error);
	}

	if (callbackId) {
		try {
			callback(data);
		} catch (e) {
			console.log(e);
			console.log(e.message);
		}
	}
}

/* Подключение к веб-сокету программы Cryptosocket для последующего вызова ее функций */
function connectSSOSocket(afterConnect) {
	if (!isConnectSSO) {
		/* Если соединение ранее не было установлено, то пытаемся установить */
		/* блокировка окна браузера с сообщением ожидания */
		blockSSO('Подключение к Cryptosocket...');
		/* Открытие веб-сокета к Cryptosocket */
		createSSOSocket(afterConnect);
	}
}

/* Открытие веб-сокета к Cryptosocket */
function createSSOSocket(afterConnect) {
	/* открытие веб-сокета */
	ssoSocket = new WebSocket("ws://localhost:6126/tumarcsp/");
	
	/* после успешного подключения */
	ssoSocket.onopen = function() {
		setApiKeySSO(0, afterConnect);
	}
	/* при закрытии подключения к Cryptosocket */
	ssoSocket.onclose = function(){
		/* сбросить флаг подключения */
		isConnectSSO = false;
		console.log("connection closed");
	}
	/* при ошибке установления связи */
	ssoSocket.onerror = function(event) {
		/* вывод в лог события */	
		console.log("connection error");
		console.log(event);

		/* разблокировать окно браузера */
		unblockSSO();
		/* вывести сообщение о невозможности подключения к Cryptosocket */
		alert(connectServerFailMsgSSO);
		return;
	}
}

/* блокировка окна браузера */
function blockSSO(msg) {
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
function unblockSSO() {
	$.unblockUI();
}

function setApiKeySSO(num, afterConnect) {
	if (num < apiKeysSSO.length) {
		// установить для сокета функцию, выполняемую после получения результата обращения
		ssoSocket.onmessage = function(event) {
			var dataStr = event.data;
			console.log("Event.data: " + dataStr);
	
			var data = JSON.parse(dataStr);
			if (data.result == "true") {

				/* разблокировать окно браузера */
				unblockSSO();
				/* установить флаг наличия подключения */
				isConnectSSO = true;
				
				console.log("connection opened");

				afterConnect();
				
				return true;
			} else {
		       	console.log("Функция отработала с ошибкой");
		        console.log("Код ошибки " +  data.code);
		       	console.log("Описание ошибки " +  data.error);
		       	
		       	setApiKeySSO(num + 1, afterConnect);
			}
		};
		// обратиться к ПО ЭЦП
		console.log('{"TumarCSP":"SYSAPI","Function":"SetAPIKey","Param":{"apiKey":"' + apiKeysSSO[num] + '"}}');
		ssoSocket.send('{"TumarCSP":"SYSAPI","Function":"SetAPIKey","Param":{"apiKey":"' + apiKeysSSO[num] + '"}}');
	} else {
		console.log("ApiKey error");

		/* разблокировать окно браузера */
		unblockSSO();
		/* вывести сообщение о невозможности подключения к Cryptosocket */
		alert("Ошибочный ключ авторизации CryptoSocket!");
	}
}

function callCryptosocketSSO(param, callback) {
	param.Param.id = generateCryptoRequestIdSSO();
	
	// Вывод в лог параметров запроса
	console.log(JSON.stringify(param));
	// если метод вызван до подсоединения к ПО ЭЦП
	if (!isConnectSSO) {
		// вызвать функцию ошибки - просто показать сообщение пользователю
		errorConnectCallBackSSO("CryptoSocket не запущен!");
		return null;
	}
	// установить для сокета функцию, выполняемую после получения результата обращения
	registerCallbackSSO(param.Param.id, callback);
	ssoSocket.onmessage = callCallbackSSO;
	// обратиться к ПО ЭЦП
	ssoSocket.send(JSON.stringify(param));
}

/* Функция, вызываемая по умолчанию при ошибке обращения к Cryptosocket */
function errorConnectCallBackSSO(msg) {
	alert(msg);
}

////////////////////////////////////

function checkUSGOKeySSO(content, auth, pass) {
	var readers = getConnectedTokensSSO(null, function(data) {
		var params = {"content":content, "auth": auth, "pass" : pass};
		readersLoadedSSO(data, params);
	});
}


connectSSOSocket(function() {
	checkUSGOKeySSO("ddddd", false, "1234567890");
});
////////////////////////////////////////////////////////////////////////////////////
//             Вспомогательные функции, обращающиеся к Cryptosocket               //
/////////////////////////////////////////.//////////////////////////////////////////

/** Вызов функции для проверки подключенных типов токенов (Казтокен, еТокен, УДЛ и др.) 
	options - json-объект, не используется
	callback - функция, вызываемая после возврата ответа Cryptosocket. Получает входной параметр в виде json-объекта
*/
function getConnectedTokensSSO(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "LoadProfileFromTokens",
		"Param": {
		}
	};
	callCryptosocketSSO(param, callback);
}

/** Вызов функции для получения сертификатов на носителе
	options - json-объект, содержит идентификатор запроса, тип устройства (KAZToken, e-Token), пароль,
	bin (не обязательно), iin (не обязательно) {"id": 1, "reader": "KAZToken", "pass": "1234567890", "iin": "800501401709"}
	callback - функция, вызываемая после возврата ответа ПО ЭЦП. Получает входной параметр в виде json-объекта
*/
function getCertificatesSSO(options, callback){
	var param = {
		"TumarCSP": "BaseAPI",
		"Function": "LoadKeyFromProfile",
		"Param": {
			"profile":options.profile,
			"pass":options.pass,
		}
	};
	callCryptosocketSSO(param, callback);
}

function signUCGOSSO(options, callback){
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
	callCryptosocketSSO(param, callback);
}

function signGeneratedSSO(data, params) {
	if (data.error) {
		alert("Не обнаружено ЭЦП на ключевом контейнере!");
	} else {
		var subject = params.subjectDN;
		
        var dnArray = subject.split(';');
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

		loginFuncSSO(false, iin);
	}
}

function getIIN(subject) {
    var dnArray = subject.split(';');
    var iin = "";

    for (var i = 0; i < dnArray.length; i++) {
        var uzel = dnArray[i].trim().split('=');
        if (uzel[0] == 'SERIALNUMBER') {
            var result = /[0-9]+/.exec(uzel[1]);
            iin = result[0];
            return iin;
        }
    }
    return "";
}


function loginFuncSSO(force, iin) {
	var par = {};
	
	var link = "cls=LinkStartProcess;loginFunc=checkLoginAD;winlogon=" + winlogon + ";tokeniin=" + iin;
	
	var b64 = b64EncodeUnicodeSSO(link);
	
	par["encodedInfo"] = b64;
	par["browser"] = browser;
	par["json"] = 1;
	par["force"] = force ? 1 : 0;
	par["rnd"] = rnd();
	par["guid"] = guid;
	
	$.post(window.contextName + '/main', par, function(data) {
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
				window.location.replace(window.contextName + "/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd());
			}
		} else if (data.reconnect == "1") {
			if (confirm(data.message)) {
				loginFuncSSO(true, iin);
			}
		} else {
			alert(data.message);
		}
	},'json');
}

/* Функция вызывается автоматически после получения ответа от Cryptosocket о подключенных устройствах хранения ключей.
   Дальнейшие действия выполняются в зависимости от аргумента processName:
   PKCS7 - требуется сформировать подпись.
*/
function readersLoadedSSO(data, params) {
	console.log ("readersLoaded data: " + JSON.stringify(data));
	console.log ("readersLoaded params: " + JSON.stringify(params));

	if (data.error) {
		if (data.code === '10001') {
			alert("TumSocket not initialize");
		} else {
			alert("Вставьте ключевой контейнер!");
		}
	} else if (data.response.length == 0) {
		alert("Вставьте ключевой контейнер!");
	} else if (data.response.length > 1) {
		alert("Найдено более одного ключевого контейнера!");
	} else {
		var profile = data.response[0].profile;
		//var reader = data.response[0].reader;
		
		var options = {"profile":profile, "pass":params.pass};
		getCertificatesSSO(options, function(data) {
			var params2 = {"profile":options.profile, "pass":options.pass, "content":params.content, "auth": params.auth};
			selectUcgoCertificateFromListSSO(data, params2);
		});
	}
}

function selectUcgoCertificateFromListSSO(data, params) {
	if (data.error) {
		alert("Ошибка при чтении контейнера, проверьте пароль!");
		return;
	}

	var haveCerts = false;
	var dataFiltered = {};
	var certsCount = 0;
	
	//var iins = "IINS: ";

	for (var objCertificate in data) {
    	if (objCertificate.indexOf("certificate") >= 0) {
    		
    		var curCert = data[objCertificate];
    		
    		var usage = (curCert.usage && curCert.usage.length > 0) ? parseInt(curCert.usage) : 0;
    		var usageAuth = (usage & 32) > 0;

    		if (params.auth == null || (params.auth && usageAuth) || (!params.auth && !usageAuth)) {
    	
    			if (curCert.issuerDN === "CN=Удостоверяющий центр Государственных органов;O=Республика Казахстан;C=KZ") {
    			//var iin = getIIN(curCert.subjectDN);
    			
    			//if (iin != null && iin.length == 12 && iins.indexOf(iin) < 0) {
                	haveCerts = true;
                	dataFiltered[objCertificate] = curCert;
    		    	certsCount++;
    		    	//iins += iin + ";";
    			}		    	
    		}
		}
	}
	if (!haveCerts) {
		alert("Контейнер не содержит ЭЦП с ИИН!");
		return;
	}
	if (certsCount > 1) {
		alert("Контейнер содержит более одной ЭЦП! " + iins);
		return;
	}

	if (certsCount == 1) {
		var info = null;
		for (var objCertificate in dataFiltered) {
			info = dataFiltered[objCertificate];
		}
		
		var certOK = checkCertificateSSO(info);
		
		if (certOK === 1) {
			var options = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "content": params.content, "isConvert": false};
			signUCGOSSO(options, function(data2) {
				var options2 = {"profile":params.profile, "pass":params.pass, "sn":info.serialNumber, "cert":info.certificateBlob, "subjectDN": info.subjectDN};
				signGeneratedSSO(data2, options2);
			});
		}
		return;
	}
}

function checkCertificateSSO(info) {
	var today = new Date();
	var dd = String(today.getDate()).padStart(2, '0');
	var MM = String(today.getMonth() + 1).padStart(2, '0');
	var yyyy = today.getFullYear();
	var hh = String(today.getHours()).padStart(2, '0');
	var mm = String(today.getMinutes()).padStart(2, '0');
	var ss = String(today.getSeconds()).padStart(2, '0');
	today = yyyy + MM + dd + hh + mm+ss;
	
	if (info.validFrom > today) {
		alert("Сертификат еще не начал действовать!");
		return 0;
	} else if (info.validTo < today) {
		alert("Сертификат просрочен!");
		return 0;
	} else if (info.issuerDN !== "CN=Удостоверяющий центр Государственных органов;O=Республика Казахстан;C=KZ") {
		alert("Сертификат выдан не УЦГО!");
		return 0;
	}
	
	return 1;
}

/////////////////////////////////////
// Важные вспомогательные функции
/////////////////////////////////////

function b64EncodeUnicodeSSO(str) {
    return btoa(unicodeToStr(str));
}

function unicodeToStr(str) {
	str = encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
    	function toSolidBytes(match, p1) {
			return String.fromCharCode('0x' + p1);
	});
	return str;
}