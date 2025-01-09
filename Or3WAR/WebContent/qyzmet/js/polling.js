import { Translation } from './translation.js';
import {Util} from './util.js';
import {NCA} from './nca.js';

export class Polling {
	
	app = null;
	noConnection = false;
	screenBlocked = false;
	reconnectTimeout = 500;
	lostRequests = Array();
	
	constructor(app) {
		this.app = app;
	}

	longPolling() {
		let _this = this;
		// Идентификатор таймаута, который должен разблокировать экран при появлении связи
		var timeoutId = null;
		// Если связи нет
		if (_this.noConnection) {
			// То запускаем таймер для разблокировки экрана
			timeoutId = setTimeout(function() {
				_this.noConnection = false;
				if (_this.screenBlocked) {
					$('body').unblock();
					_this.screenBlocked = false;
					_this.reconnectTimeout = 500;
				}
				var requests = [];
				for (var i=0; i<_this.lostRequests.length; i++) {
					requests.push(_this.lostRequests[i]);
				}
				_this.lostRequests = [];
				for (var i=0; i<requests.length; i++) {
					var req = requests[i];
					_this.saveAjaxRequest(req.type, req.url, req.params, req.success, req.dataType, true);
				}
				requests = null;
				timeoutId = null;
			}, 2000 + _this.reconnectTimeout);
		}
		
		var par = {"polling": Util.rnd()};
		
		_this.app.post(par).then(data => {
			_this.processingPolling(data);
			setTimeout(function(){_this.longPolling();}, 500);
		}).catch(error => {
			console.error("Long polling error: " + error);
			if (error.stack)
				console.log(error.stack);
			// Отменяем разблокировку экрана, так как связь не появилась
			if (timeoutId != null) {
				clearTimeout(timeoutId);
				if (!_this.screenBlocked) {
					_this.screenBlocked = true;
					_this.reconnectTimeout = 2000;
					Util.blockPage("Соединение с сервером утеряно.<br>Попытка возобновления связи!<br>Подождите...");
				}			
			}
			// Если связь была и вдруг пропала
			if (!_this.noConnection) {
				_this.noConnection = true;
			}			
	
			setTimeout(function(){_this.longPolling();}, _this.reconnectTimeout);
		});
	}
	
	processingPolling(data) {
		let _this = this;
		let ifc = this.app.ifcController;
		let nav = this.app.nav;
		let orders = this.app.orders;
		let notifs = this.app.notifications;
		let nca = this.app.ncaLayer;
		let monitor = this.app.monitor;
		
		if (data.commands && data.commands.length>0) {
			var cmd;
			for (var i=0; i<data.commands.length; i++) {
				cmd = data.commands[i];
				if (cmd) {
					if (cmd.refresh != null) {
						var par = {};
						par["rnd"] = Util.rnd();
						par["getChange"] = "";
						ifc.postAndParseData(par);
					} else if (cmd.prevUI != null) {
						if (cmd.prevUI == 1)
							document.location.hash = "cmd=prevUI&rnd=" + Util.rnd();
						else
							_this.app.nav.toActiveMain();
					} else if (cmd.main_ui != null) {
						_this.app.nav.toActiveMain();
					} else if (cmd.closePopupInterface != null) {
						if (_this.app.ifcController.popupcount > 0) {
							var did = 'or3_popup' + (_this.app.ifcController.popupcount - 1);
							ifc.showChangeMsg();
							ifc.dialogResult[did] = '0';
							var opts = $('#' + did).dialog('options');
						    
						    var par = {};
							par["uid"] = opts.uid ;
							par["val"] = cmd.closePopupInterface;
							par["cmd"] = "closePopup";
		
							ifc.loadData(par, true, function(data) {
								if (!data.result || data.result == 'success') {
									$("#" + did).dialog('destroy');
								} else {
									ifc.showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
									ifc.setDialogBtnsEnabled(did, true);
								}
							});
						}
					} else if (cmd.closeInterface != null) {
						ifc.closeIfc();
					} else if(cmd.startProcessWrp != null){
						_this.app.nav.startProcess(cmd.startProcessWrp);
					} else if (cmd.showProcessUI != null) {
						Util.blockPage();
						if (data.infMsg) {
							Util.alert(data.infMsg);
					 	}
						var message = JSON.parse(cmd.showProcessUI);
						setTimeout(function(){
							_this.app.nav.showProcessUI(message)
						}, message.waitTime);					
					} else if(cmd.keyPressed) {
						let command = cmd.keyPressed === 'Enter'? 13:cmd.keyPressed === 'Escape'? 27: 0;
						if(command != 0){
							var e = jQuery.Event("keydown");
							e.which = command;
							e.key = cmd.keyPressed;
							$(document).trigger(e);
						}					
						
					}
					else if (cmd.start_ui) {
						if (document.location.hash.indexOf("cmd=openTask&uid=" + cmd.start_ui) == -1) {
							document.location.hash = "cmd=openTask&uid=" + cmd.start_ui + "&id=" + _this.app.nav.activeMenu + "&rnd=" + Util.rnd();
							if (ifc.dialogOpened) {
								ifc.dialogResult[ifc.dialogOpened] = '0';
								$('#' + ifc.dialogOpened).dialog('destroy');
							}
						}
					} else if (cmd.next_ui) {
						document.location.hash = "cmd=openTask&uid=" + cmd.next_ui + "&id=" + _this.app.nav.activeMenuId + "&rnd=" + Util.rnd();
						if (ifc.dialogOpened) {
							ifc.dialogResult[ifc.dialogOpened] = '0';
							$('#' + ifc.dialogOpened).dialog('destroy');
						}
					} else if (cmd.open_report) {
						var url = _this.app.restUrl + "&rnd=" + Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(cmd.open_report);
						$('#report_frame').attr('src', url);
					} else if (cmd.signString) {
						var str = cmd.signString.str;
						var path = cmd.signString.path;
						var pass = cmd.signString.pass;
						var cont = cmd.signString.cont;
						var auth = (cmd.signString.auth == "true") || (cmd.signString.auth == true);
	
						generateUcgoSign(str, path, pass, cont, auth);
					} else if (cmd.signTextWithNCA) {
						var text = cmd.signTextWithNCA.text;
						var path = cmd.signTextWithNCA.path;
						var pass = cmd.signTextWithNCA.pass;
						var cont = cmd.signTextWithNCA.cont;
	
						let xml = '<signTextWithNCA><signText>' + Util.b64EncodeUnicode(text) + '</signText></signTextWithNCA>';
						nca.signXml(NCA.COMMON, NCA.SIGN, xml).then(data2 => {
							if (data2.responseObject) {
								let signedData = data2.responseObject;
								var par = {
									cmd: "signTexWithNCAResult",
									signedData: signedData,
									signText: text,
									signDate: (new Date).getTime(),
									rnd: Util.rnd()
								};
								_this.app.post(par)
							} else {
								let error = (data2.message === 'action.canceled')
									? 'Действие отменено пользователем'
									: 'Код ошибки: ' + data2.code + ' Сообщение: ' + data2.message;
								console.error("msg text: " + error);
								Util.alert(error, Util.ERROR);
								
								var par = {cmd: "signTexWithNCAResult", error: "Подписание текста не выполнено!"};
						    	_this.app.post(par)
							}
						}, error => {
							console.error("error text: " + error);
							Util.alert(error, Util.ERROR);
							var par = {cmd: "signTexWithNCAResult", error: "Подписание текста не выполнено!"};
					    	_this.app.post(par)
						});
						
					} else if (cmd.getFile) {
						if (document.getElementById('getFile') == null) {
							var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'getFile'}); 
							dialogDiv.html('<div style=\"padding: 20px\"><input id=\"chooser\" type=\"file\" onchange=\"sendFile();\" style=\"display:none;\"><input id=\"chooserBtn\" type=\"button\" value=\"Выбрать\" style=\"width:100px; height:25px;\"></div>');
							dialogDiv.dialog({
								title:'Выбор файла',
								closed: true,
								draggable: false,
								resizable: false,
								closeOnEscape: false,
								modal:true,
								closable: false,
								buttons:[{text:'Закрыть', id: 'closeDialog', handler:function(){ifc.sendFile();}}]
							});
							$("#chooserBtn").click(function() {
								$("#chooser").click();
							});
						}
						var chooserElement = $("#chooser");
						chooserElement.replaceWith(chooserElement.val('').clone(true));
						$('body').unblock();
						$('#getFile').dialog("open");
					} else if (cmd.generateUcgoPKCS10) {
						var str = cmd.generateUcgoPKCS10.str;
						var auth = (cmd.generateUcgoPKCS10.auth == "true") || (cmd.generateUcgoPKCS10.auth == true);
						generateUcgoPKCS10(str, auth);
					} else if (cmd.generateUcgoPKCS7) {
						var str = cmd.generateUcgoPKCS7.str;
						var auth = (cmd.generateUcgoPKCS7.auth == "true") || (cmd.generateUcgoPKCS7.auth == true);
						generateUcgoPKCS7(str, auth);
					} else if (cmd.saveUcgoCertificate) {
						var cert = cmd.saveUcgoCertificate.cert;
						var reader = cmd.saveUcgoCertificate.reader;
						var uid = cmd.saveUcgoCertificate.uid;
						var tokPD = cmd.saveUcgoCertificate.tokpd;
						saveUcgoCertificate(cert, reader, uid, tokPD);
					} else if (cmd.haveUcgoCertificate) {
						var reader = cmd.haveUcgoCertificate.reader;
						var uid = cmd.haveUcgoCertificate.uid;
						var tokPD = cmd.haveUcgoCertificate.tokpd;
						haveUcgoCertificate(reader, uid, tokPD);
					} else if (cmd.selectUcgoCertificate) {
						var iin = cmd.selectUcgoCertificate.iin;
						var bin = cmd.selectUcgoCertificate.bin;
						selectUcgoCertificate(iin, bin);
					} else if (cmd.deleteUcgoCertificate) {
						deleteUcgoCertificate(cmd.deleteUcgoCertificate);
				
					// подключение к веб-сокету для работы с ПО ЭЦП УЦГО
					} else if (cmd.connectUcgoWebsocket) {
						connectTamurSocket(true);
					} else if (cmd.showErrors) {
						$('body').unblock();
						if (ifc.dialogOpened){
							ifc.setDialogBtnsEnabled(ifc.dialogOpened, true);
						}
						
						if (cmd.showErrors.btnEdt && cmd.showErrors.btnIgn){
							ifc.showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, cmd.showErrors.btnIgn, cmd.showErrors.btnEdt);
						}else{
							ifc.showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, Translation.translation['ignore']);
						}
					} else if (cmd.connectScanWebsocket) {
						scan_connectSocket(null);
					} else if (cmd.disconnectScanWebsocket) {
						scan_disconnectSocket();
					} else if (cmd.startScan) {
						var id = cmd.startScan.id;
						startScan(id);
					} else if (cmd.openClientFiles) {
		            	
						var id = cmd.openClientFiles.id;
						var windowTitle = cmd.openClientFiles.windowTitle;
						var buttonTitle = cmd.openClientFiles.buttonTitle;
						var dir = cmd.openClientFiles.dir;
						var extensions = cmd.openClientFiles.extensions;
						var description = cmd.openClientFiles.description;
	
						scan_openClientFiles(id, windowTitle, buttonTitle, dir, extensions, description);
					} else if (cmd.loadClientFile) {
						var id = cmd.loadClientFile.id;
						var path = cmd.loadClientFile.path;
						scan_loadClientFile(id, path);
					} else if (cmd.saveFileOnClient) {
						var id = cmd.saveFileOnClient.id;
						var path = cmd.saveFileOnClient.path;
						var data = cmd.saveFileOnClient.data;
						scan_saveFileOnClient(id, path, data);
					} else if (cmd.showOptions) {
						$('body').unblock();
						ifc.showOptions(cmd.showOptions.options);
					} else if (cmd.confirm) {
						Util.confirmMessage(cmd.confirm);
					} else if (cmd.alert != null) {
						Util.alert(cmd.alert, 0, false);
					} else if (cmd.logout) {
						Util.logout();
					} else if (cmd.notification) {
						notifs.notificationsProcessing(cmd.notification);
						Util.playNoteSound(); 
					} else if (cmd.updateNotifications) {
						notifs.loadNotifications2();
					} else if (cmd.deleteNotifications) {
						notifs.deleteNotifications(cmd.deleteNotifications.objids);
					} else if (cmd.alertAndDrop) {
						Util.alert(cmd.alertAndDrop, 0, true, true);
					} else if (cmd.alertError != null) {
						Util.alert(cmd.alertError, Util.ERROR, false, false);
					} else if (cmd.alertWarning != null) {
						Util.alert(cmd.alertWarning, Util.WARNING, false, false);
					} else if (cmd.alertInfoFlow != null) {
						Util.alert(cmd.alertInfoFlow, 0, true);
					} else if (cmd.alertErrorFlow != null) {
						Util.alert(cmd.alertErrorFlow, Util.ERROR, true, false);
					} else if (cmd.alertWarningFlow != null) {
						Util.alert(cmd.alertWarningFlow, Util.WARNING, true, false);
					} else if (cmd.slide) {
						Util.slide(cmd.slide);
					} else if (cmd.showWaiting) {
						document.body.style.cursor = 'wait';
						Util.blockPage(cmd.showWaiting);
					} else if (cmd.closeWaiting) {
						document.body.style.cursor = 'default';
						$('body').unblock();
					} else if (cmd.disableCancelBtn) {
						ifc.hideChangeMsg();
					} else if (cmd.nodeType) {
						$('#nextBtn').linkbutton({text: cmd.nodeType});
					} else if(cmd.makeTreeTable){
						ifc.makeTreeTable(cmd.makeTreeTable);
					} else if (cmd.cursor) {
						if (cmd.cursor == "0") {
							document.body.style.cursor = 'default';
							$('body').unblock();
						} else if (cmd.cursor == "1") {
							document.body.style.cursor = 'wait';
							Util.blockPage();
						} else if (cmd.cursor == "2") {
							document.body.style.cursor = 'pointer';
						}
					} else if (cmd.stack) {
						nav.showStack(cmd.stack);
					} else if (cmd.hideFullPath) {
						$("#fullPath").hide();
					} else if (cmd.deleteTask) {
						monitor.deleteTask(cmd.deleteTask);
					} else if (cmd.addTask) {
						monitor.addTask(cmd.addTask);
					} else if (cmd.updateTask) {
						monitor.updateTask(cmd.updateTask);
					} else if (cmd.deleteOrders) {
						orders.deleteOrders(cmd.deleteOrders);
						notifs.deleteNotification(cmd.deleteOrders);
					} else if (cmd.updateOrders) {
						orders.updateOrders(cmd.updateOrders);
						notifs.updateNotification(cmd.updateOrders);
					} else if (cmd.reload) {
						ifc.reload(cmd.reload);
					} else if (cmd.hideSend) {
						if (cmd.hideSend == "0"){
							$('#nextBtn').hide();
						} else if (cmd.hideSend == "1") {
							$('#nextBtn').show();
						}
					} else if (cmd.hideSave) {
						if (cmd.hideSave == "0"){
							$('#saveBtn').hide();
						} else if (cmd.hideSave == "1"){
							$('#saveBtn').show();
						}
					} else if (cmd.hideCancel) {
						if (cmd.hideCancel == "0"){
							$('#cancelBtn').hide();
						} else if (cmd.hideCancel == "1") {
							$('#cancelBtn').show();
						} else if (cmd.hideCancel == "2") {
							$('#cancelBtn').show();
							$('#cancelBtn').linkbutton('enable');
						}
					//} else if (cmd.readIdCard) {
					//	readIdCard();
					//} else if (cmd.loadApplet) {
					//	loadApplet(cmd.loadApplet);
					} else if (cmd.openArh) {
						nav.openArchive(cmd.openArh);
					} else if (cmd.openDocument) {
						Util.openDocument(cmd.openDocument);
					} else if (cmd.askPassword) {
						Util.askPassword(function(res) {
							if (res != null)
								post({"promptRes":res, "promptAction":1});
							else
								post({"promptAction":0});
								
						}, cmd.askPassword);
					} else if(cmd.setSelectedTab){					
						var compUid = cmd.setSelectedTab.split(',')[0];
						var index = parseInt(cmd.setSelectedTab.split(',')[1]);
						if($('#'+compUid).hasClass('easyui-tabs')){
							$('#'+compUid).tabs('select', index);
						}
					} else if (cmd.setMultiSelection) {
						let compUid = cmd.setMultiSelection.split(',')[0];
						let multi = cmd.setMultiSelection.split(',')[1] === 'true';
						console.log("workworkwork1", compUid);
						if (ifc.multiSelection[compUid]) {
						    ifc.multiSelection[compUid] = multi;
						}
						ifc.multiSelection[compUid] = multi;
					}
				}
			}
		} 
	}
	
	saveAjaxRequest(type, url, params, success, dataType, async) {
		params["rnd"] = Util.rnd();
		params["guid"] = guid;
	
		fetch(this.restUrl, {
			method: type,
	    	credentials: 'include',
			body: $.param(params)
	    }).then(response => {
			success(response);
		}, () => {
			console.error('Rejected');
			if (async)
				lostRequests.push(new LostRequest(type, url, params, success, dataType));
		}).catch(error => {
			console.error('Error:', error);
			if (async)
				lostRequests.push(new LostRequest(type, url, params, success, dataType));
		});
	}
}

export class LostRequest {
	type = null;
	url = null;
	params = null;
	success = null;
	dataType = null;
	
	constructor(type, url, params, success, dataType) {
		this.type = type;
		this.url = url;
		this.params = params;
		this.success = success;
		this.dataType = dataType;
	}
}