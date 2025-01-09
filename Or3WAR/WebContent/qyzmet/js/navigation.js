import {Util} from './util.js';
import {Translation} from './translation.js';

export class Navigation {
	activeMenuId = 'main-page';
	
	constructor(app) {
		this.prevHash = "";
		this.app = app;
		
		$(".profile-tab").on('click', function() {
			var tab_id = $(this).attr("tab");
			document.location.hash = "#select=profile&profileTab="+tab_id;
		});

	}
	
	hashChanged(hash) {
		console.log('hash changed from ' + this.prevHash + ' to ' + hash);
		if (this.prevHash != hash) {
			this.prevHash = hash;
			if (hash == "")
				return false;
			
			this.loadUI(encodeURI(hash));
		}
	}
	
	loadUI(hash) {
		var params = Util.parseHash(hash);
		console.log("params = " + JSON.stringify(params));
		
		if (params.cmd == 'exit') {
			this.app.logout();
			return;
		}
		
		if (params.select !== null && params.select !== undefined && params.select.length > 0) {
			this.activeMenuId = params.select;
			$('.top-view-panel[id != "' + this.activeMenuId + '-panel"]').hide();
			$('#' + this.activeMenuId + '-panel').show();
			
	        $('.left-menu li.selected').removeClass('selected');
        	$('#menu-' + this.activeMenuId).parent().addClass("selected");

			$('#' + this.activeMenuId + '-panel').panel();
			this.app.resize('#' + this.activeMenuId + '-panel');
			
			console.log("show #" + this.activeMenuId + '-panel');
		}

		if (params.profileTab !== null && params.profileTab !== undefined && params.profileTab.length > 0) {
			$('.profile-body').hide();
			$("#profile-panel-"+params.profileTab).show();
	
			$('.profile-tab').removeClass("active");
			$('.profile-tab[tab="' + params.profileTab + '"]').addClass("active");
			
			console.log("show #profile-panel-" + params.profileTab);
		}
		
		if (params.orders) {
			this.showOrders(params.orders);
		} else if (params.cmd == 'openTask') {
			Util.blockPage();
			var url = Util.makeUrl(params);
			$('#ui-body').attr('init',"1");
			this.openTask(params.uid, url);
		} else if (params.cmd !== null && params.cmd !== undefined) {
			Util.blockPage();
			$('.top-view-panel').hide();
			$('#ui-body').attr('init',"1");
			$('#ui-panel').show();
			$('#ui-title').show();
			$('.ui-toolbar').show();
			$('#ui-breadcrump').show();
			$("#nextBtn").hide();
			
			this.app.resize('#ui-panel');
			
			var url = Util.makeUrl(params);
			$('#ui-body').panel('refresh', url);
			this.app.resizeHeight();
	        //clearTimeout(refresher);
		}
		$('#ui-panel .easyui-panel:not(.tamur-tabs)').panel();
	}

	showOrders(type) {
		// отменить выделение предыдущего типа поручений
		$('.order-type-selected').removeClass('order-type-selected');
		// выделяем нужный тип
		if (type === 'all')
			$('.order-type:not([code])').addClass(('order-type-selected'));
		else
			$('.order-type[code=' + type + ']').addClass(('order-type-selected'));
		// скрываем/отображаем поручения нужного типа
		if (type === 'all') {
			//$('.order-block').show();
		} else {
			$('.order-blocks[code=' + type + ']').show();
			$('.order-blocks[code!=' + type + ']').hide();
			
		}
	}

	openOrder(iter, proc, uid, startIfNotExists) {
		let _this = this;
		Util.blockPage();
		
		if (iter == null || iter == undefined || iter == "")
			document.location.hash = "cmd=openOrder&iuid=" + proc + "&ouid=" + uid;
		
		else {
			let par = {"uid": proc, "obj": iter, "cmd": "openProcess"};
					
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
				        if (data.result == "success" || data.acts == null || data.acts > 0 || (data.message != null && !startIfNotExists)) {
				     		_this.showProcessUI(data);
				        } else if (startIfNotExists) {
							par.cmd = "startProcess";
							par.obj = uid;
							_this.app.query(par).then(response => {
								if (response.status === 200) {
									response.json().then(data => {
										if (data.infMsg) {
											Util.alert(data.infMsg);
										}
										_this.showProcessUI(data);
									});
								}
							});				
				        } else if(data.result == "error" && data.message == null){
	        				$('body').unblock();
	            			Util.alert(Translation.translation['error'], Util.ERROR);
						}
					});
				}
			});
		}
	}
	
	showProcessUI(data) {
		let _this = this;
		let ifc = this.app.ifcController;
		
		if (data.result == "success") {
			if (data.mode && data.mode=="dialog") {
				ifc.openStartDialog(data.uid);
			} else if (data.mode && data.mode=="no") {
				_this.toActiveMain();
				$('body').unblock();
			} else if (data.uid) {
				if (document.location.hash.indexOf("cmd=openTask&uid=" + data.uid) == -1)
					document.location.hash = "cmd=openTask&uid=" + data.uid + "&rnd=" + Util.rnd();
			} else {
				_this.toActiveMain();
				$('body').unblock();
			}
		} else {
			var message = data.message;
			var unblock = true;
			// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
			let myMessage = " Открыть раннее запущенный процесс?";
			if($('#topLangKz').hasClass("selected")) {
				myMessage = " Бұрын іске қосылған процесті ашу?";
			} else {
				myMessage = " Открыть раннее запущенный процесс?";
			}
			if (message) {
				var flowIdText = "ID потока: ";
				var index = message.indexOf(flowIdText);
				if (index > 0) {
					var flowId = message.substring(index + flowIdText.length, message.length - 1);
					message = message.substring(0, index) + myMessage;
					$.messager.confirm('', message, function(e) {
						if (e) {
							document.location.hash = "cmd=openTask&uid=" + flowId + "&rnd=" + Util.rnd() + "&isPrevProc=1";
							unblock = false;
						}
					}); 
				} else {
					alert(message);
				}
			} else {
				Util.alert('error', Util.ERROR);
			}
			if (unblock)
				$('body').unblock();
		}
	}
	
	openTask(uid, url) {
		let par = {"uid": uid, "cmd": "taskType", "json": 1, "rnd": Util.rnd()};
		let nav = this;
		let ifc = this.app.ifcController;
			
		this.app.query(par).then(response => {
			if (response.status === 200) {
				response.json().then(data => {
					if (data.type == "report" || data.type == "fastreport") {
						$('#report_frame').attr('src', url);
						$('body').unblock();
					} else if (data.type == "htmlreport") {
						$('body').unblock();
						window.open(url);
					} else if (data.type == "undefined") {
						Util.delay(3000).then(() => nav.openTask(uid, url));
					} else if (data.type == "choose") {
						ifc.openStartDialog(uid);
					} else if (data.type == "dialog") {
						ifc.openStartDialog(uid);
					} else if (data.type == "option") {
						var par = {};
						par["cmd"] = "openTask";
						par["uid"] = uid;
						nav.app.query(par).then(response => {
							response.json().then(data => {
								if (data.message) {
									Util.alert(data.message, Util.INFO);
								}
							});
						});
					} else if (data.type == "error") {
			            Util.alert(data.msg, Util.ERROR);
						nav.toActiveMain();
						$('body').unblock();
					} else if (data.type == "noop") {
						nav.toActiveMain();
						$('body').unblock();
					} else {
						$('.top-view-panel').hide();
						$('#ui-panel').show();
						$('#ui-title').show();
						$('.ui-toolbar').show();
						$('#ui-breadcrump').show();
						$("#nextBtn").show();
						$("#nextBtn").addClass("active");

						$('#ui-body').panel('refresh', url);
						nav.app.resizeHeight();
						$('.easyui-panel:not(.tamur-tabs)').panel();
					}
				});
			}
		});
	}
	
	toActiveMain() {
		let a = $('#menu-' + this.activeMenuId);
		document.location.hash = a.attr('href');
		//a.click();
	}

	startProcess(pid) {
		let nav = this;
		
		Util.blockPage();
		
		let par = {"uid": pid, "cmd": "startProcess"};
				
		this.app.query(par).then(response => {
			if (response.status === 200) {
				response.json().then(data => {
					if (data.infMsg) {
						Util.alert(data.infMsg);
			   	 	}
					nav.showProcessUI(data);
				});
			}
		});
	}
	
	openArchive(pid) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openArch&uid=" + pid;
	}
	
	openDict(pid) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openDict&uid=" + pid;
	}
	
	openIfcWithObj(ui, obj) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openTaskIntf&intUid=" + ui + "&objUid=" + obj;
	}

	cancelStart(uid) {
		var par = {};
		par["cmd"] = "cancelProcess";
		par["uid"] = uid;
		this.app.query(par);
	}
	
	stepSelected(ignore, did) {
		var steps = document.getElementsByName("step");
		var transitionId;
		for (var i = 0; i < steps.length; i++) {
			if (steps[i].checked)
				transitionId = steps[i].value;
		}
		this.nextStep(ignore, did, transitionId);
		$('#stepSelectionDlg').dialog("close");
		$('body').unblock();
	}
	
	stepSelectionCanceled() {
		$('#stepSelectionDlg').dialog("close");
		$('body').unblock();
	}
	
	nextStep(ignore, did, transitionId) {
		let _this = this;
		let ifc = this.app.ifcController;
		let nav = this.app.nav;
		
		ifc.endEditing();
		if (ignore || $("#nextBtn").hasClass("active")) {
			if (transitionId || ignore != undefined || did != undefined || confirm(Translation.translation['askNextStep'])) {
				var par = {};
				par["cmd"] = "nextStep";
				if (transitionId) {
					par["transitionId"] = transitionId;
				}
				_this.app.post(par).then(data => {
					if (data.result == "success") {
						ifc.hideChangeMsg();
						if (did == null)
							nav.toActiveMain();
						if (data.message) {
							Util.alert(data.message);
						}
						if (did)
							$('#' + did).dialog('destroy');
						$('body').unblock();
					} else if (data.result == "selectStep") {
						// Показать диалог с выбором шага
						var dialogDiv = document.getElementById('stepSelectionDlg');
						if (dialogDiv != null) {
							dialogDiv.parentNode.removeChild(dialogDiv);
						}
						dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'stepSelectionDlg'});
						var html = '<div style="padding: 20px">';
						for (var i = 0; i < data.transitions.length; i++) {
							html += '<input style="margin: 0px 10px ' + (i < data.transitions.length - 1 ? '10px' : '0px') + ' 0px;" type="radio" name="step" id="step_' + i + '" value="' + data.transitions[i].value + '"' + (i == 0 ? ' checked' : '') + '/>';
							html += '<label for="step_' + i + '">' + data.transitions[i].title + '</label>' + (i < data.transitions.length - 1 ? '<br>' : '');
						}
						html += '</div>';
						dialogDiv.html(html);
						dialogDiv.dialog({
							title:'Выбор шага',
							closed: true,
							draggable: false,
							resizable: false,
							closeOnEscape: false,
							modal:true,
							closable: false,
							buttons:[{
								text:'Принять',
								handler:function(){_this.stepSelected(ignore, did);}
							}, {
								text:'Отмена',
								handler:function(){_this.stepSelectionCanceled();}
							}]
						});
						$('#stepSelectionDlg').dialog("open");
					} else {
						if (data.message) {
							Util.alert(data.message);
						}
						if (did)
							ifc.setDialogBtnsEnabled(did, true);
						$('body').unblock();
					}
				}, 'json');
			}
		}
	}
	
	showStack(stack, container) {}
}
