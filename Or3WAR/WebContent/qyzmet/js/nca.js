import {Util} from './util.js';

export class NCA {
	
	static APPLET = "kz.gov.pki.knca.applet.Applet";
	static COMMON = "kz.gov.pki.knca.commonUtils";
	static BASICS = "kz.gov.pki.knca.basics";
	
	static AUTH = 'AUTHENTICATION';
	static SIGN = 'SIGNATURE'; 
	
	tokenStoresMap = {
		'AKEToken5110Store': 'eToken',
		'AKKaztokenStore': 'Казтокен',
		'PKCS12': 'Персональный компьютер',
		'AKKZIDCardStore': 'Удостоверение личности',
	}; 
	
	webSocket = null;
	onMessage = null;
	onError = null;
	
	constructor() {
	}
	
	init() {
		let _this = this;
		
		if (_this.webSocket && _this.webSocket.readyState < 2) {
	        console.log("reusing the socket, state = " + _this.webSocket.readyState);
	        return Promise.resolve();
	    }
		
		return new Promise((resolve, reject)  => {
			_this.webSocket = new WebSocket('wss://127.0.0.1:13579/');
		
			_this.webSocket.onopen = function() {
				resolve();
			};
		
			_this.webSocket.onclose = function(event) {
				_this.webSocket = null;
				console.log("connection closed");
				console.log('Code: ' + event.code + ' Reason: ' + event.reason);
			};
		
			_this.webSocket.onmessage = function(event) {
				var dataStr = event.data;
				var data = JSON.parse(dataStr);
				console.log("event.data: " + data);
				
				if (data && data.result && data.result.version)
					return;
				
				if (_this.onMessage) {
					_this.onMessage(data);
				}
			};
			
			_this.webSocket.onerror = function(error) {
				reject(error);
			}
		});
	}
	
	showError() {
		if (mode == 0) {
			this.showErrorOnLoginPage("Ошибка соединения с NCALayer!");
		} else if (mode == 1 || mode == 2) {
			Util.alert('Ошибка соединения с NCALayer');
		}
		this.resetOperation(true);
	}
	
	sendToNCALayer(module, method, args) {
		let _this = this;
		
		return new Promise((resolve, reject) => {
			
			_this.init().then(() => {
				_this.onMessage = resolve;	
				console.log("sendToNCALayer: " + module + ", method: " + method + ", args: " + args);
			
				var json = {'module' : module, 'method' : method, 'args' : args};
				_this.webSocket.send(JSON.stringify(json));
			}, err => {
				console.error("socket error : ", err);
				reject("Ошибка соединения с NCALayer!");
			});
		}); 
	}
	
	chooseStorage(data) {
		let _this = this;
		if (data.responseObject) {
			if (data.responseObject.length == 0)
				return Promise.resolve('PKCS12');
			else
				return _this.showStorageSelectionDialog(data.responseObject);
		} else {
			return Promise.reject("Ошибка при получении доступных хранилищ ключей");
		}
	}
	
	getActiveTokens(module) {
		return this.sendToNCALayer(module, "getActiveTokens", []);
	}
	
	signXml(module, ecpType, xml) {
		let _this = this;

		if (module === NCA.COMMON) {
			return new Promise((resolve, reject) => {
				_this.getActiveTokens(module).then(data => {
					_this.chooseStorage(data).then(alias => {
						let args = [ alias, ecpType, xml, "", "" ];
						_this.sendToNCALayer(module, "signXml", args).then(resolve, reject);
					}, reject);
				}, reject);
			});
		} else if (module === NCA.BASICS) {
			return new Promise((resolve, reject) => {
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
						// Защищенная электронная почта (1.3.6.1.5.5.7.3.4) подписть
						extKeyUsageOids: ["1.3.6.1.5.5.7.3.2"] 
					},
					locale: "ru"
				};
				_this.sendToNCALayer(module, "sign", args).then(data => {
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
	
	showStorageSelectionDialog(conteiners) {
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
}
