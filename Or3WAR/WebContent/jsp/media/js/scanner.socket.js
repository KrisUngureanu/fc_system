/* соединение с локальной программой для работы со сканером */
var scan_socket;
/* флаг успешного подключения к ПО */
var scan_isConnect = false;
/* порт по умолчанию для работы ПО */
var DEFAULT_SCAN_PORT = 12018;

/* Счетчик количества подключений к локальному ПО */
var scan_startServerTryCount = -1;
/* Временной интервал между повторными попытками подключения */
var scan_startServerTryPeriod = 5000;
/* Максимальное количество попыток подключений */
var scan_startServerTryMax = 10;
/* расположение jnlp-файла в архиве WAR */
var scan_jnlpPath = window.contextName
		+ '/jsp/media/scan/scanner.jnlp?d=2019-12-18';

/* Сообщения */
var startServerFailMsg = "Не удалось запустить программу для работы со сканером";
var noConnectionMsg = "Нет подключения к локальному ПО";

var connectingInProcessMsg = "Подключение к локальной программе для работы со сканером...";
var scan_jnlpLinkMsg = 'Не найдено приложение для работы со сканером!<br/>'
		+ '<a href="javascript:downloadJNLP(\'' + scan_jnlpPath
		+ '\');">Запустить приложение</a>';

/* функция, вызываемая при вызове функций ПО при отсутствии связи с ПО */
var scan_errorConnectCallback = function(msg) {
	alert(msg);
}

/* мапа с коллбэками */
var callbacks2 = {};

var lastRequestId = 1;

/* регистрируем функцию, вызываемую после отработки запроса по идентификатору */
function registerCallback2(id, callback) {
	callbacks2[id] = callback;
}

/* вызываем функцию, после отработки запроса по идентификатору */
function callCallback2(res) {
	console.log(res.data);
	var data = JSON.parse(res.data);
	var callback = callbacks2[data.id];
	callback(data);
}

/* Подключение к веб-сокету для вызова функций на локальном компьютере */
function scan_connectSocket(port) {
	if (port == null)
		port = DEFAULT_SCAN_PORT;

	/* если вызов идет в первый раз и соединение с ПО еще не устанавливалось */
	if (!scan_isConnect) {
		/* сбросить счетчик попыток соединения */
		scan_startServerTryCount = -1;
		/* блокировка окна браузера с сообщением ожидания */
		blockPage(connectingInProcessMsg);
		/* Открытие веб-сокета к локальному ПО */
		scan_createSocket(port);
	} else {
		/* если соединение уже было ранее установлено */
		post({
			"scansocketconn" : true
		});
	}
}

function scan_disconnectSocket() {
	scan_socket.close();
}

/* Открытие веб-сокета к локальной программе */
function scan_createSocket(port) {
	/* открытие веб-сокета */
	scan_socket = new WebSocket("ws://127.0.0.1:" + port + "/ws/scan");

	/* после успешного подключения */
	scan_socket.onopen = function() {
		/* разблокировать окно браузера */
		$('body').unblock();
		/* сбросить счетчик попыток соединения */
		scan_startServerTryCount = -1;
		/* установить флаг наличия подключения */
		scan_isConnect = true;

		// оповестить веб-клиент о удачной попытке подключения к ПО
		post({
			"scansocketconn" : true
		});

		console.log("connection opened");
	}
	/* при закрытии подключения к ПО */
	scan_socket.onclose = function() {
		/* сбросить флаг подключения */
		scan_isConnect = false;

		console.log("connection closed");
	}
	/* при ошибке установления связи */
	scan_socket.onerror = function(event) {
		/* сбросить флаг подключения */
		scan_isConnect = false;
		/* вывод в лог события */
		console.log(event);

		/* если это первая попытка подключения */
		if (scan_startServerTryCount == -1) {
			/* предлагаем пользователю запустить ПО с помощью WebStart */
			showStartWebsocketServer(scan_jnlpLinkMsg);
			scan_startServerTryCount = 0;
		}

		/* если превышено максимальное количество попыток подключения */
		if (scan_startServerTryCount >= scan_startServerTryMax) {
			/* Обнуляем счетчик попыток */
			scan_startServerTryCount = -1;
			// оповестить веб-клиент о неудачной попытке подключения к ПО
			post({
				"scansocketconn" : false
			});
			/* разблокировать окно браузера */
			$('body').unblock();
			/* вывести сообщение о невозможности подключения к ПО ЭЦП */
			alert(startServerFailMsg);
			return;
		}

		/*
		 * устанавливаем таймер для повторного вызова функции попытки
		 * подключения через scan_startServerTryPeriod мс
		 */
		setTimeout(function() {
			scan_startServerTryCount++;
			scan_createSocket(port);
		}, scan_startServerTryPeriod);
		return;
	}
}

/* вывод ссылки для запуска локального ПО ЭЦП с помощью технологии WebStart */
function showStartWebsocketServer(linkMsg) {
	$('body').unblock();

	blockPage(linkMsg);
}

/**
 * Функции для работы с ПО
 * 
 */

/**
 * Универсальная функция для обращения к ПО. Информация о функционале заложена в
 * param (json-объект), где содержится название функции и параметры запроса.
 * Например:
 * 
 * {"id": 1, "function": "scan", "param": {"reader": "KAZToken", "pass":
 * "1234567890"}}
 * 
 * callback - функция, вызываемая после возврата ответа ПО. Получает входной
 * параметр в виде json-объекта.
 */
function scan_callSocket(param, callback) {
	if (param.id == null)
		param.id = "" + lastRequestId++;

	// Вывод в лог параметров запроса
	console.log(JSON.stringify(param));
	// если метод вызван до подсоединения к ПО ЭЦП
	if (!scan_isConnect) {
		// вызвать функцию ошибки - просто показать сообщение пользователю
		scan_errorConnectCallback(noConnectionMsg);
		return null;
	}
	// установить для сокета функцию, выполняемую после получения результата
	// обращения
	registerCallback2(param.id, callback);
	scan_socket.onmessage = callCallback2;
	// обратиться к ПО
	scan_socket.send(JSON.stringify(param));
}

/**
 * Функции для отправки запросов в WebSocket Пользовательские функции
 */

function startScan(id) {
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"id" : id,
		"function" : "scan",
		"param" : {}
	};
	scan_callSocket(param, function(data) {
		var par = {};
		par["wsres"] = {
			"id" : data.id,
			"function" : "scan",
			"status" : data.status,
			"error" : data.error,
			"data" : data.data
		};
		post(par);
	});
}

function scan_saveFileOnClient(id, path, content) {
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"id" : id,
		"function" : "saveFileOnClient",
		"param" : {
			"data" : content,
			"path" : path
		}
	};
	scan_callSocket(param, function(data) {
		var par = {};
		par["wsres"] = {
			"id" : data.id,
			"function" : "saveFileOnClient",
			"status" : data.status,
			"error" : data.error
		};
		post(par);
	});
}

function scan_loadClientFile(id, path) {
	// json-объект, передаваемый в ПО ЭЦП
	var param = {
		"id" : id,
		"function" : "loadClientFile",
		"param" : {
			"path" : options.path
		}
	};
	scan_callSocket(param, function(data) {
		var par = {};
		par["wsres"] = {
			"id" : data.id,
			"function" : "loadClientFile",
			"status" : data.status,
			"error" : data.error,
			"data" : data.data
		};
		post(par);
	});
}

function scan_openClientFiles(id, windowTitle, buttonTitle, dir, extensions, description) {
	var param = {
		"id" : id,
		"function" : "openFiles",
		"windowTitle" : windowTitle,
		"buttonTitle" : buttonTitle,
		"dir" : dir,
		"extensions" : extensions,
		"description" : description,
	};
	scan_callSocket(param, function(data) {
		var par = {};
		par["wsres"] = {
				"id" : data.id,
				"function" : "openFiles",
				"status" : data.status,
				"name" : data.name,
				"data" : data.data,
				"error" : data.error,
		};
		post(par);
	});
}
