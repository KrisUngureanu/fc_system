var webSocket = null;
moduleUtils = "kz.gov.pki.knca.commonUtils";

var callback = null;

var storageAlias;
var keyType = "";

var path = "";
var pwd = "";

var signText = ""

var mode = -1;	// 0 - loginWihtNCALayer, 1 - signLiabilityAgreement, 2 - signTextFromWebInterface

let tokenStoresMap = {};
tokenStoresMap['AKEToken5110Store'] = 'eToken';
tokenStoresMap['AKKaztokenStore'] = 'Казтокен';
tokenStoresMap['PKCS12'] = 'Персональный компьютер';
tokenStoresMap['AKKZIDCardStore'] = 'Удостоверение личности';

function chooseNCAStorageCall() {
	if (webSocket === null || webSocket.readyState === 3 || webSocket.readyState === 2) {
		initNCALayer('chooseNCAStorageCall');
		return;
	}
	
	getData("getActiveTokens", [], function(data) {
		if (data.responseObject) {
			if (data.responseObject.length == 0)
				signXmlCall('PKCS12', signXmlBack);
			else
				showStorageSelectionDialog(data.responseObject);
		} else {
			resetOperation(true);
		}
	});
}

function getData(method, args, callbackM) {
	console.log("getData: " + method + ", args: " + args);

	var methodVariable = {'module' : moduleUtils, 'method' : method, 'args' : args};
	if (callbackM) {
		callback = callbackM;
	}
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
		webSocket = null;
		console.log("connection closed");
	};

	webSocket.onmessage = function(event) {
		var dataStr = event.data;
		var data = JSON.parse(dataStr);
		console.log("event.data: " + data);
		
		if (data && data.result && data.result.version)
			return;
		
		if (callback) {
			callback(data);
		}
	};
	
	webSocket.onerror = function(event) {
		if (mode == 0) {
			showErrorOnLoginPage("Ошибка соединения с NCALayer!");
		} else if (mode == 1 || mode == 2) {
			alert('Ошибка соединения с NCALayer');
		}
		resetOperation(true);
	}
}

function signXmlCall(alias, callbackM) {
	var dataToSign = "";
	var ecpType = 'SIGNATURE';
	if (mode == 0) {
		ecpType = 'AUTHENTICATION';
		if (isUseECP) {
			$.post(url, {cmd:'getSecret', rnd: rnd(), guid: guid, json:1}, function(data) {
				if (data.secret) {
					secret = data.secret;
					dataToSign = '<login><secret>' + secret + '</secret></login>';
					
					var args = [ alias, ecpType, dataToSign, "", "" ];
					getData('signXml', args, callbackM);
				}
			},'json');
			return;
		} else {
			dataToSign = '<login><secret>' + secret + '</secret></login>';
		}
	} else if (mode == 1) {
		dataToSign = '<la><liabilityText>' + b64EncodeUnicode(document.getElementById('liabilityText').innerHTML) + '</liabilityText></la>';
	} else if (mode == 2) {
		dataToSign = '<signTextWithNCA><![CDATA[' + signText + ']]></signTextWithNCA>';
	}
	
	var args = [ alias, ecpType, dataToSign, "", "" ];
	getData('signXml', args, callbackM);
}

function signXmlBack(data) {
	if (data.responseObject) {
		signedData = data.responseObject;
		var par = {};
		if (mode == 0) {
//			if (isUseECP) {
				//$("#user").prop('disabled', false);
				//$("#password").prop('disabled', false);
				//$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled");
//			} else {
				par["json"] = 1;
				par["signedData"] = signedData;
				//par["passwd"] = $('#pdECP').val();
				par["force"] = force ? 1 : 0;

				par["name"] = $('#user').val();
				par["passwd"] = $('#password').val();
				par["isUseECP"] = 1;

				par["sLogin"] = sLogin ? 1 : 0;

				par["browser"] = browser;
				par["profile"] = $('#file').val();
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
					} else if (data.passChange == "1") {
						alert(data.message);
						changePwdDialog(false);
					}  else if (data.passChange == "2") {
						sLogin = true;
						alert(data.message);
						changePwdDialog(false);
					} else if (data.reconnect == "1") {
						if (confirm(data.message)) {
							forceLogin(par);
						} else {
							$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
							$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
							$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
						}
					} else {
						alert(data.message);
						$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
						$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
						$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
					}
				},'json');
//			}
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
			par["cont"] = storageAlias;
    		
        	post(par);
		}
		resetOperation(false);
	} else {
		var msg = '';
		if (data.message === 'action.canceled') {
			msg = 'Действие отменено пользователем';
		} else {
			msg = 'Код ошибки: ' + data.code + ' Сообщение: ' + data.message;
		}
		if (mode == 0) {
			showErrorOnLoginPage(msg);
		} else if (mode == 1 || mode == 2){
			alert(msg);
		}
		resetOperation(true);
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
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		}
	},'json');
}

function signTextWithNCA(str, storage) {
	console.log("signTextWithNCA: " + str);

	mode = 2;
	signText = str;
	
	chooseNCAStorageCall();
}

function showStorageSelectionDialog(conteiners) {
	var options = '<option value="" disabled selected hidden>Выберите хранилище</option>' +
					'<option value="PKCS12">' + tokenStoresMap['PKCS12'] +'</option>';
	
	for (var ind = 0; ind < conteiners.length; ind++) {
		var contId  = conteiners[ind];
		options += '<option value="' + contId + '">' + tokenStoresMap[contId] + '</option>';
	}

	var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'storageInputDlg'}); 
	dialogDiv.html('<div style="padding: 20px">' +
			'<select id="keyStore1" onchange="storageChanged()">' +
				options +
			'</select></div>');
	
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
		buttons:[
			{
				text:'Принять',
				id: 'storageInputDlgOkBtn',
				handler:function(){
					storageSelected();
				}
			}, 
			{
				text:'Отмена',
				handler:function(){
					storageCanceled();
				}
			}
		],
		onOpen: function() {
			$('#keyStore1').focus();
		},
		onClose: function() {
			storageCanceled();
		}
	});

	$('body').unblock();
	var e = document.getElementById("keyStore1");
	if (e.options[e.selectedIndex].value.length == 0) {
		$("#storageInputDlgOkBtn").linkbutton("disable");
	} else {
		$("#storageInputDlgOkBtn").linkbutton("enable");
	}
	$('#storageInputDlg').dialog("open");
}

function storageSelected() {
	storageAlias = $("#keyStore1 option:selected").val();
	$("#storageInputDlg").dialog('destroy');

	signXmlCall(storageAlias, signXmlBack);
}

function storageCanceled() {
	$("#storageInputDlg").dialog('destroy');
	resetOperation(true);
}

function storageChanged() {
	var optVal = $("#keyStore1 option:selected").val();
   	console.log(optVal);
	$("#storageInputDlgOkBtn").linkbutton("enable");
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
	console.log("resetOperation: " + isFailed);
	if (isFailed) {
		if (mode == 0) {
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		} else if (mode == 1) {
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		} else if (mode == 2) {
			var par = {};
			par["cmd"] = "signTexWithNCAResult";
			par["error"] = "Подписание текста не выполнено!";
	    	post(par);
		}
	}
	if (webSocket != null) {
		webSocket.close();
		webSocket = null;
	}
	mode = -1;
}