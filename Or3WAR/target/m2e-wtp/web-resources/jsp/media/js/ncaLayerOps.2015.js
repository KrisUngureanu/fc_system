var webSocket = null;

const APPLET = "kz.gov.pki.knca.applet.Applet";
const COMMON = "kz.gov.pki.knca.commonUtils";
const BASICS = "kz.gov.pki.knca.basics";
	
const AUTH = 'AUTHENTICATION';
const SIGN = 'SIGNATURE'; 

var onMessage = null;
var storageAlias = 'PKCS12';
let tokenStoresMap = {};
tokenStoresMap['AKEToken5110Store'] = 'eToken';
tokenStoresMap['AKKaztokenStore'] = 'Казтокен';
tokenStoresMap['PKCS12'] = 'Персональный компьютер';
tokenStoresMap['AKKZIDCardStore'] = 'Удостоверение личности';

function initNCALayer() {
	return new Promise((resolve, reject)  => {
	    if (webSocket === null || webSocket.readyState === 3 || webSocket.readyState === 2) {
	        webSocket = new WebSocket('wss://127.0.0.1:13579/');
	
	        webSocket.onopen = function () {
	            resolve();
	        };
	
	        webSocket.onclose = function (event) {
	        	webSocket = null;
				console.log("connection closed");
				console.log('Code: ' + event.code + ' Reason: ' + event.reason);
	        };
	
	        webSocket.onmessage = function (event) {
	            var result = JSON.parse(event.data);
	            if (result && result.result && result.result.version)
					return;
					
				if (onMessage) {
					onMessage(result);
				}
	        }
	
	        webSocket.onerror = function (error) {
	        	console.error("socket error : ", error);
	            reject("Ошибка соединения с NCALayer!");
	        };
	    } else {
	        resolve();
	    }
	});
}

function sendToNCALayer(module, method, args) {
	return new Promise((resolve, reject) => {
		
		initNCALayer().then(() => {
			onMessage = resolve;	
			console.log("sendToNCALayer: " + module + ", method: " + method + ", args: " + args);
		
			var json = {'module' : module, 'method' : method, 'args' : args};
			webSocket.send(JSON.stringify(json));
		}, err => {
			console.error("socket error : ", err);
			reject("Ошибка соединения с NCALayer!");
		});
	}); 
}

function chooseStorage(data) {
	if (data.responseObject) {
		if (data.responseObject.length == 0)
			return Promise.resolve('PKCS12');
		else
			return showStorageSelectionDialog(data.responseObject);
	} else {
		return Promise.reject("Ошибка при получении доступных хранилищ ключей");
	}
}

function getActiveTokens(module) {
	return sendToNCALayer(module, "getActiveTokens", []);
}

function signXml(module, ecpType, xml) {
	let _this = this;

	if (module === COMMON) {
		return new Promise((resolve, reject) => {
			getActiveTokens(module).then(data => {
				chooseStorage(data).then(alias => {
					let args = [ alias, ecpType, xml, "", "" ];
					sendToNCALayer(module, "signXml", args).then(resolve, reject);
				}, reject);
			}, reject);
		});
	} else if (module === BASICS) {
		return new Promise((resolve, reject) => {
		
			let usage = ecpType === AUTH ? "1.3.6.1.5.5.7.3.2" : "1.3.6.1.5.5.7.3.4";
			
			let args = {
				allowedStorages: "AKKaztokenStore,AKKZIDCardStore,AKEToken72KStore,AKJaCartaStore,PKCS12,JKS,AKAKEYStore,AKEToken5110Store,ABSIMCardStore,ABCloudServiceStore",
				format: "xml",
				data: xml,
				signingParams: {
					decode: false,
					digested: false,
					encapsulate: false,
					tsaProfile: null
				},
				signerParams: {
					// Проверка подлинности клиента (1.3.6.1.5.5.7.3.2) авторизация
					// Защищенная электронная почта (1.3.6.1.5.5.7.3.4) подпись
					extKeyUsageOids: [usage] 
				},
				locale: "ru"
			};
			sendToNCALayer(module, "sign", args).then(data => {
				if (data.status === true) {
					let response = (data.body.result) 
						?
					{
						responseObject: data.body.result[0]
					}
						:
					{
						message: 'action.canceled'
					};
					
					resolve(response);
				} else if (data.status === false) {
					let response = {
						code: data.code,
						message: data.message + ' (' + data.details + ')'
					};
					resolve(response);
				} else {
					reject('Неизвестная ошибка NCALayer!');
				}
			}, reject);
		});
	} else
		return Promise.reject("Неизвестный плагин NCALayer"); 
}

function showStorageSelectionDialog(conteiners) {
	let _this = this;

	var options = '<option value="" disabled selected hidden>Выберите хранилище</option>' +
					'<option value="PKCS12">' + this.tokenStoresMap['PKCS12'] +'</option>';
	
	for (var ind = 0; ind < conteiners.length; ind++) {
		var contId  = conteiners[ind];
		options += '<option value="' + contId + '">' + this.tokenStoresMap[contId] + '</option>';
	}
	
	return new Promise((resolve, reject) => {

		var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'storageInputDlg'}); 
		dialogDiv.html('<div style="padding: 20px">' +
				'<select id="keyStore1">' +
					options +
				'</select></div>');
		
		dialogDiv.find('#keyStore1').keyup(function(e) {
			if (e.which && e.which == 13)
				$('#storageInputDlgOkBtn').click();
		}).change(function (e) {
			$("#storageInputDlgOkBtn").linkbutton("enable");
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
						let storageAlias = $("#keyStore1 option:selected").val();
						$("#storageInputDlg").dialog('destroy');
						resolve(storageAlias);
					}
				}, 
				{
					text:'Отмена',
					handler:function(){
						$("#storageInputDlg").dialog('destroy');
						reject("Выбор хранилища отменен");
					}
				}
			],
			onOpen: function() {
				$('#keyStore1').focus();
			},
			onClose: function() {
				$("#storageInputDlg").dialog('destroy');
				reject("Окно выбора хранилища закрыто");
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
	});
}

function signTextWithNCA(str, storagePath, pass, storage) {

	let xml = '<signTextWithNCA><signText>' + b64EncodeUnicode(str) + '</signText></signTextWithNCA>';
	
	signXml(BASICS, SIGN, xml).then(data2 => {
		if (data2.responseObject) {
			let signedData = data2.responseObject;
			
			post({
				cmd: "signTexWithNCAResult",
				signedData: signedData,
				signText: str,
				signDate: (new Date).getTime(),
				rnd: rnd(),
				guid: guid,
				json: 1
			});
		} else {
			let error = (data2.message === 'action.canceled')
				? 'Действие отменено пользователем'
				: 'Код ошибки: ' + data2.code + ' Сообщение: ' + data2.message;
			
			console.error("msg text: " + error);

			post({
				cmd: "signTexWithNCAResult",
				error: "Подписание текста не выполнено!"
			});
		}
	}, error => {
		console.error("error text: " + error);
		alert(error);
		post({
			cmd: "signTexWithNCAResult",
			error: "Подписание текста не выполнено!"
		});
	});
}

function b64EncodeUnicode(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
        function toSolidBytes(match, p1) {
            return String.fromCharCode('0x' + p1);
    }));
}

function b64DecodeUnicode(str) {
    return decodeURIComponent(atob(str).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}
