var webSocket = null;
moduleUtils = "kz.gov.pki.knca.applet.Applet";
var fileStoreAllowed = (typeof isMobile === 'undefined') ? true : !isMobile;

var callback = null;

var storageAlias;
var keyType = "";

var path = "";
var pwd = "";

var signText = ""

var mode = -1;	// 0 - loginWihtNCALayer, 1 - signLiabilityAgreement, 2 - signTextFromWebInterface

function chooseNCAStorageCall() {
	if (webSocket === null || webSocket.readyState === 3 || webSocket.readyState === 2) {
		initNCALayer('chooseNCAStorageCall');
		return;
	}
	if (mode == 0) {
		if (fileStoreAllowed) {
			showStorageSelectionDialog();
/*			var storagePath = document.getElementById('file').value;
			if ($('#keyStore').length > 0) {
				storageAlias = $("#keyStore option:selected").val();
			} else {
				storageAlias = "PKCS12";
			}
			var args = [ storageAlias, 'P12', storagePath ];
			getData('browseKeyStore', args, 'chooseNCAStorageBack');*/
		} else {
			storageAlias = 'AKKaztokenStore';
			var args = [ storageAlias, 'P12', path ];
			getData('browseKeyStore', args, 'chooseNCAStorageBack');
		}
	} else if (mode == 1 || mode == 2) {
		if (fileStoreAllowed) {
			showStorageSelectionDialog();
		} else {
			storageAlias = 'AKKaztokenStore';
			var args = [ storageAlias, 'P12', path ];
			getData('browseKeyStore', args, 'chooseNCAStorageBack');
		}
	}
}

function chooseNCAStorageBack(result) {
	if (result.getErrorCode()) {
		if (result.getErrorCode() === 'NONE') {
			var storagePath = result.getResult();
			if (storagePath !== null && storagePath !== '' && typeof storagePath !== 'undefined') {
				if (mode == 0) {
					document.getElementById('file').value = storagePath;
					path = storagePath;
					showPasswordDialog();
				} else if (mode == 1 || mode == 2) {
					path = storagePath;
					showPasswordDialog();
				}
				return;
			}
		}
		resetOperation(true);
	}
}

function getData(method, args, callbackM) {
	var methodVariable = {'module' : moduleUtils, 'method' : method, 'args' : args};
	if (callbackM) {
		callback = callbackM;
	}
	console.log(JSON.stringify(methodVariable));
	webSocket.send(JSON.stringify(methodVariable));
}

function initNCALayer(callbackM) {
	webSocket = new WebSocket('wss://127.0.0.1:13579/');

	webSocket.onopen = function() {
		if (callbackM) {
			window[callbackM]();
		}
	};

	webSocket.onclose = function(event) {
		console.log("connection closed");
	};

	webSocket.onmessage = function(event) {
		console.log("data: " + event.data);

		var result = JSON.parse(event.data);
		var rw = {
			result : result.result,
			secondResult : result.secondResult,
			errorCode : result.errorCode,
			getResult : function() {
				return this.result;
			},
			getSecondResult : function() {
				return this.secondResult;
			},
			getErrorCode : function() {
				return this.errorCode;
			}
		};
		if (callback) {
			window[callback](rw);
		}
	};
	
	webSocket.onerror = function(event) {
		console.log("connection error NCA");
		if (mode == 0) {
			showErrorOnLoginPage("Ошибка соединения с NCALayer!");
		} else if (mode == 1 || mode == 2) {
			alert('Ошибка соединения с NCALayer');
		}
		resetOperation(true);
	}
}

function getKeysCall() {
	if (webSocket === null || webSocket.readyState === 3 || webSocket.readyState === 2) {
		initNCALayer('getKeysCall');
		return;
	}	
	var storagePath = "";
	var pd = null;
	if (mode == 0) {
		storagePath = document.getElementById('file').value;
		if (isUseECP) {
			pd = pwd;
		} else {
			pd = document.getElementById('pdECP').value;
		}
		keyType = "AUTH";
	} else if (mode == 1 || mode == 2) {
		storagePath = path;
		pd = pwd;
		keyType = "SIGN";
	}
	var msg = '';
	if (storagePath && storagePath.length > 0) {
		if (pd && pd.length > 0) {
			var args = [];
			args = [ storageAlias, storagePath, pd, keyType ];
			getData('getKeys', args, 'getKeysBack');
			return;
		} else {
			msg = 'Введите пароль';
		}
	} else {
		msg = 'Укажите путь к ключу';
	}
	if (mode == 0) {
		showErrorOnLoginPage(msg);
	} else if (mode == 1 || mode == 2){
		alert(msg);
	}
	resetOperation(true);
}

function getKeysBack(result) {
	if (result.getErrorCode()) {
		if (result.errorCode === 'NONE') {
			var str = result.result;
			var alias = str.split('|')[3];
		 	signXmlCall(alias, 'signXmlBack');
		} else {
			var msg = '';
			if (result.errorCode === 'WRONG_PASSWORD') {
				msg = 'Пароль неверен';
			} else if (result.errorCode === 'EMPTY_KEY_LIST') {
		        if (keyType && keyType === 'AUTH') {
		        	msg = 'Выберите сертификат для авторизации (с префиксом AUTH)';
		        } else if (keyType && keyType === 'SIGN') {
		        	msg = 'Выберите сертификат для подписания заявки';
		        } else {
		        	msg = 'Произошла техническая ошибка. Повторите запрос позже';
		        }
			} else {
				msg = 'Код ошибки ' + result.errorCode;
			}
			if (mode == 0) {
				showErrorOnLoginPage(msg);
			} else if (mode == 1 || mode == 2){
				alert(msg);
			}
			resetOperation(true);
		}
	}
}

function signXmlCall(alias, callbackM) {
	var storagePath = "";
	var pd = null;
	var data = "";
	if (mode == 0) {
		storagePath = document.getElementById('file').value;
		if (isUseECP) {
			pd = pwd;
			$.post(url, {cmd:'getSecret', rnd: rnd(), guid: guid, json:1}, function(data) {
				if (data.secret) {
					secret = data.secret;
					data = '<login><secret>' + secret + '</secret></login>';
					var args = [ storageAlias, storagePath, alias, pd, data ];
					getData('signXml', args, callbackM);
				}
			},'json');
			return;
		} else {
			pd = document.getElementById('pdECP').value;
			data = '<login><secret>' + secret + '</secret></login>';
		}
	} else if (mode == 1) {
		storagePath = path;
		pd = pwd;
		data = '<la><liabilityText>' + b64EncodeUnicode(document.getElementById('liabilityText').innerHTML) + '</liabilityText></la>';
	} else if (mode == 2) {
		storagePath = path;
		pd = pwd;
		data = '<signTextWithNCA><signText>' + b64EncodeUnicode(signText) + '</signText></signTextWithNCA>';
	}
	var args = [ storageAlias, storagePath, alias, pd, data ];
	getData('signXml', args, callbackM);
}

function signXmlBack(result) {
	if (result.getErrorCode()) {
		if (result.errorCode === 'NONE') {
			signedData = result.getResult();
			var par = {};
			if (mode == 0) {
				//if (isUseECP) {
				//	$("#user").prop('disabled', false);
				//	$("#password").prop('disabled', false);
				//	$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled");
				//} else {
					par["json"] = 1;
					par["signedData"] = signedData;
					par["passwd"] = pwd; //$('#pdECP').val();
					par["browser"] = browser;
					par["profile"] = path;// $('#file').val();
					par["noCache"] = (new Date).getTime();
					par["secret"] = secret;
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
						} else if (data.reconnect == "1") {
							if (confirm(data.message)) {
								forceLogin(par);
							} else {
								$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
								$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
							}
						} else {
							alert(data.message);
							$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
							$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
						}
					},'json');
				//}
			} else if (mode == 1) {
	    		par["json"] = 1;
				par["cmd"] = "signLiability";
				par["isNCAMode"] = 1;
	    		par["signedData"] = signedData;
	    		par["liabilityText"] = document.getElementById('liabilityText').innerHTML;
	        	par["signDate"] = (new Date).getTime();
	        	par["liabilityObjectUID"] = liabilityObjectUID;
	        	par["guid"] = guid;
	        	$.post(url, par, function(data) {
	        		if (data.signResult == 0) {
	        			alert("Ошибка при подписании соглашения!" + (data.errorMessage != null ? (" " + data.errorMessage) : ""));
	        		} else {
						alert("Соглашение успешно подписано!");
						$('#liabilityContentDiv').dialog("close");
	        			window.location.replace(window.contextName + "/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd());
	        		}
	        		isLASingOp = false;
	        	}, 'json');
			} else if (mode == 2) {
				par["cmd"] = "signTexWithNCAResult";
	    		par["signedData"] = signedData;
	    		par["signText"] = signText;
	        	par["signDate"] = (new Date).getTime();

	        	// Запоминаем выбранные параметры
				par["path"] = path;
				if (instantECP)
					par["code"] = pwd;
				par["cont"] = storageAlias;
	    		
	        	post(par);
			}
			resetOperation(false);
		} else {
			var msg = '';
			if (result.errorCode === 'WRONG_PASSWORD') {
				msg = 'Пароль неверен';
			} else {
				msg = 'Код ошибки ' + result.errorCode;
			}
			if (mode == 0) {
				showErrorOnLoginPage();
			} else if (mode == 1 || mode == 2){
				alert(msg);
			}
			resetOperation(true);
		}
	}
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

function signTextWithNCA(str, storagePath, pass, storage) {
	mode = 2;
	signText = str;
	if (storage != null && pass != null && pass.length > 0 && storagePath != null) {
		storageAlias = storage;
		path = storagePath;
		pwd = pass;
		
		blockPage();
		getKeysCall();
	} else {
		chooseNCAStorageCall();
	}
}

function showStorageSelectionDialog() {
	if (document.getElementById('storageInputDlg') == null) {
		var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'storageInputDlg'}); 
		dialogDiv.html('<div style=\"padding: 20px\"><select id=\"keyStore1\" onchange=\"storageChanged()\"><option value=\"\" disabled selected hidden>Выберите хранилище</option><option value=\"AKKaztokenStore\">eToken</option><option value=\"AKKaztokenStore\">Казтокен</option><option value=\"PKCS12\">Персональный компьютер</option><option value=\"AKKZIDCardStore\">Удостоверение личности</option></select></div>');
		
		dialogDiv.find('#keyStore1').keyup(function(e) {
			if (e.which && e.which == 13)
				$('#storageInputDlgOkBtn').click();
		});
		
		dialogDiv.dialog({
			title:'Выбор хранилища ключей',
			closed: true,
			draggable: false,
			resizable: false,
			closeOnEscape: false,
			modal:true,
			closable: false,
			buttons:[{text:'Принять', id: 'storageInputDlgOkBtn', handler:function(){storageSelected();}}, {text:'Отмена', handler:function(){storageCanceled();}}],
			
			onOpen: function() {
				$('#keyStore1').focus();
			}
		});
	}
	$('body').unblock();
	var e = document.getElementById("keyStore1");
	if (e.options[e.selectedIndex].value.length == 0) {
		$("#storageInputDlgOkBtn").linkbutton("disable");
	} else {
		$("#storageInputDlgOkBtn").linkbutton("enable");
	}
	$('#storageInputDlg').dialog("open");
}

function storageChanged() {
	var optVal = $("#keyStore1 option:selected").val();
   	console.log(optVal);
	$("#storageInputDlgOkBtn").linkbutton("enable");
}

function showPasswordDialog() {
	if (document.getElementById('passInputDlg') == null) {
		var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'passInputDlg'}); 
		dialogDiv.html('<div style=\"padding: 20px\"><input id=\"passInput\" type=\"password\" oninput=\"passOnInputFunction(this)\" style=\"width:100%; height:25px; padding:5px;\"></div>');
		
		dialogDiv.find('#passInput').keyup(function(e) {
			if (e.which && e.which == 13)
				$('#passInputDlgOkBtn').click();
		});
		
		dialogDiv.dialog({
			title:'Введите пароль',
			closed: true,
			draggable: false,
			resizable: false,
			closeOnEscape: false,
			modal:true,
			closable: false,
			buttons:[{text:'Принять', id: 'passInputDlgOkBtn', handler:function(){passwordEntered();}}, {text:'Отмена', handler:function(){passwordCanceled();}}],
			
			onOpen: function() {
				$('#passInput').focus();
			}
		});
	}
	$('body').unblock();
	$('#passInput').val('');
	$("#passInputDlgOkBtn").linkbutton("disable");
	$('#passInputDlg').dialog("open");
}

function passOnInputFunction(component) {
	if (component.value.trim().length > 0) {
		$("#passInputDlgOkBtn").linkbutton("enable");
	} else {
		$("#passInputDlgOkBtn").linkbutton("disable");
	}
}

function passwordEntered() {
	pwd = $('#passInput').val();
	$('#passInputDlg').dialog("close");
	if (mode == 0 && isUseECP) {
	 	getKeysCall();	 	
	} else if (mode == 1) {
		getKeysCall();
	} else if (mode == 2) {
		blockPage();
		getKeysCall();
	}
}

function passwordCanceled() {
	resetOperation(true);
	$('#passInputDlg').dialog("close");
}

function storageSelected() {
	storageAlias = $("#keyStore1 option:selected").val();
	$('#storageInputDlg').dialog("close");
	var args = [ storageAlias, 'P12', path ];
	getData('browseKeyStore', args, 'chooseNCAStorageBack');
}

function storageCanceled() {
	resetOperation(true);
	$('#storageInputDlg').dialog("close");
}

function showErrorOnLoginPage(msg) {
	document.getElementById('errmsg').innerHTML = msg;
	document.getElementById('errmsg').style.display = '';
	$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
	$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
}

function b64DecodeUnicode(str) {
    return decodeURIComponent(atob(str).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}

function b64EncodeUnicode(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
        function toSolidBytes(match, p1) {
            return String.fromCharCode('0x' + p1);
    }));
}

function resetOperation(isFailed) {
	if (isFailed) {
		if (mode == 0) {
			
		} else if (mode == 1) {
			
		} else if (mode == 2) {
			var par = {};
			par["cmd"] = "signTexWithNCAResult";
			par["error"] = "Подписание текста не выполнено!";
	    	post(par);
		}
	}
    webSocket.close();
	mode = -1;
}