var msie = (window.navigator.userAgent.indexOf("MSIE ") > -1)  || !!navigator.userAgent.match(/Trident.*rv\:11\./);
    
window.mainUrl = window.contextName + "/main?guid=" + guid;
window.popupcount = 0;
window.openedDialogs = [];
window.dialogResult = [];
window.hintOptions = [];

var loadingCount = 0;
var noConnection = false;
var screenBlocked = false;
var reconnectTimeout = 500;
var prevHash = "";
var currentSelectedId = '';
var dateOrders = false;
var tasksRefreshing = true;
var res;
var tasksCount=-1;

var searchTreeId = null;
var searchTreeVal = null;
var lastId = -1;
var searchProcess = "";
var indexSearchProces = 1;
var countSearchResulProces = 0;

var dialogZindex = 345000;
var messagerZindex = 350000;
var appletLoadTryCount = 0;
var appletLoadTryMax = 10;
var appletLoadFailMsg = "Не удалось загрузить апплет. Возможно в браузере установлен запрет на использование Java";
var appletCallFailMsg = "Не удалось выполнить операцию. Возможно Вы ограничили доступ к ресурсам компьютера";

var orderStatus = Array();
orderStatus[0] = "label-success";
orderStatus[1] = "label-warning";
orderStatus[2] = "label-important";
orderStatus[3] = "label-info";

var condition = Array();
condition[0] = "state-green";
condition[1] = "state-yellow";
condition[2] = "state-red";

var ifcRefreshers = [];
var styles = {};
var cssStyles = {};
var breadcrumpsVisible = true;
var activeMenu = "ui_startPage";
var loadFirstTime = [true, true, true, true];

var attentionMap = {};

var tablesToReloadMap = {};

//для уведомлений
var notificationPageSize = 50;
var statusReadingNotification = 0;
var dateParseN1 = 0;
var dateParseN2 = 0;
var notificationSearch = 0;
var sortNotifColumn = "sortInDate_asc";

var tasksLoaded = false;

$(window).bind('resizeEnd', function() {
	$('#app').find('.panel, .panel-body, .easyui-fluid, .easyui-panel, .easyui-tabs, .datagrid-view, .datagrid-view2, .datagrid-header, .datagrid-body, .datagrid-footer').width(0);
	$('#app').width(0);
	$('#app').panel('resize');
	resize('#app');
});

$("#tasksList").on('click', '#disable_task', function() {
	if ($(this).hasClass('c5')){
		tasksRefreshing = true;
		$(this).removeClass('c5');
		$(this).find('.l-btn-text').text("Выключить обновление списка").css('color','#000');
		var par = {};
		par["cmd"] = "tasksRefreshing";
		par["value"] = "1";
		$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {}, 'json');
	} else {
		tasksRefreshing = false;
		$(this).addClass('c5');
		$(this).find('.l-btn-text').text("Включить обновление списка").css('color','#fff');
		var par = {};
		par["cmd"] = "tasksRefreshing";
		par["value"] = "0";
		$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {}, 'json');
	}
});

function getContactInfo() {
	var url = window.mainUrl + "&rnd=" + rnd();
	var par = {};
	par["cmd"] = 'contactInfo';
	par["additionalInfo"] = "false";
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url,
		data : par,
		success : function(data) {
			if (data.contacts) {
				var table = "";
				for (var i = 0; i < data.contacts.length; i++) {
					var contact = data.contacts[i];
					var row = "<div class='support-person'><h4>" + contact.person + "</h4><span class='phone'>" + contact.telephone + "</span><a href=\"mailto:" + contact.email + "\" class='mail'>" + contact.email + "</a></div>";
					table = table + row;
				}
			  	table = table + "";
			  	var content = "<div class=\"help-name\"><h3>" + translation['supportTeam'] + "</h3>" + table+"</div>";
			  	$('.tool-help').append($(content));
			}
		},
		dataType : 'json',
		async : false
	});
}

function hideItems() {
	var url = window.mainUrl + "&rnd=" + rnd();
	var par = {};
	par["cmd"] = 'hideItems';
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url,
		data : par,
		success : function(data) {
			if (data.items != null) {
				for (var i = 0; i < data.items.length; i++) {
					var id = data.items[i];
					$('#' + id).hide();
				}
			}
		},
		dataType : 'json',
		async : false
	});
}


function resize(selector) {
	var comp = $(selector);
	if (comp.hasClass('easyui-panel'))
		comp.panel('resize');
	else if (comp.hasClass('easyui-tabs'))
		comp.tabs('resize');
	
	$.each(comp.children(), function(i, child) {
		resize(child);
	});
}

$(window).resize(function() {
	$('#left-panel-content > ul').css('height', ($(window).height() - 120)+"px");
	if (this.resizeTO) clearTimeout(this.resizeTO);
    this.resizeTO = setTimeout(function() {
    	$(this).trigger('resizeEnd');
    }, 500);
});

var lostRequests = Array();
//конструктор
function LostRequest(type, url, params, success, dataType) {
	this.type = type;
	this.url = url;
	this.params = params;
	this.success = success;
	this.dataType = dataType;
}

function addStyle(st) {
    var style = document.createElement('style');
	style.type = 'text/css';
	style.innerHTML = st;
	document.getElementsByTagName('head')[0].appendChild(style);
}

var refresher;
function refreshTasks() {
    if (refresher != null) clearTimeout(refresher);

	if($('#sessionsList').is(":visible")) {
		if (loadingCount == 0) {
			loadSessions();
			refresher = setTimeout(refreshTasks, 7000);
		}
	} else if($('#startDiv').is(":visible")) {
		loadOrdersIn();
		loadOrdersOut();
		loadOrdersMy();
	} else if($('#oldStartDiv').is(":visible")) {
		loadOrdersOld();
    }
}

var pinger;
function ping() {
    if (pinger != null) clearTimeout(pinger);
	pingServer();
	pinger = setTimeout(ping, 30000);
}

function treeCallLater(name, comp, id, count) {
	//console.log("Count = " + count);
	var node = comp.tree('find', id);
	if (node != null) {
		comp.tree(name, node.target);
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){treeCallLater(name, comp, id, count);}, 800);
	}
}

function treeCallLaterHigliht(comp, id, text, count) {
	//console.log("Count = " + count);
	var node = comp.tree('find', id);
	if (node != null) {
		var html = $(node.target).find('.tree-title').html();
		var ind = html.toUpperCase().indexOf(text.toUpperCase());
		html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + text.length) + '</span>' + html.substring(ind + text.length);
		$(node.target).find('.tree-title').html(html);
		
		comp.parent().scrollTop($(node.target).offset().top - comp.offset().top);
		
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){treeCallLaterHigliht(comp, id, text, count);}, 800);
	}
}

function longPolling() {
	// Идентификатор таймаута, который должен разблокировать экран при появлении связи
	var timeoutId = null;
	// Если связи нет
	if (noConnection) {
		// То запускаем таймер для разблокировки экрана
		timeoutId = setTimeout(function() {
			noConnection = false;
			if (screenBlocked) {
				$('body').unblock();
				screenBlocked = false;
				reconnectTimeout = 500;
			}
			var requests = [];
			for (var i=0; i<lostRequests.length; i++) {
				requests.push(lostRequests[i]);
			}
			lostRequests = [];
			for (var i=0; i<requests.length; i++) {
				var req = requests[i];
				saveAjaxRequest(req.type, req.url, req.params, req.success, req.dataType, true);
			}
			requests = null;
			timeoutId = null;
		}, 2000 + reconnectTimeout);
	}
	
	var url = window.mainUrl + "&polling=" + rnd();
	$.get(url, function(data) {
		if (data) {
			var ok = checkData(data);
			if (ok) {
				processingPolling(data);
				setTimeout(function(){longPolling();}, 500);
			}
		}
	}, 'json')
	.fail(function() {
		// Отменяем разблокировку экрана, так как связь не появилась
		if (timeoutId != null) {
			clearTimeout(timeoutId);
			if (!screenBlocked) {
				screenBlocked = true;
				reconnectTimeout = 2000;
				blockPage("Соединение с сервером утеряно.<br>Попытка возобновления связи!<br>Подождите...");
			}			
		}
		// Если связь была и вдруг пропала
		if (!noConnection) {
			noConnection = true;
		}			

		setTimeout(function(){longPolling();}, reconnectTimeout);
	});
}

function processingPolling(data) {
	if (data.commands && data.commands.length>0) {
		var cmd;
		for (var i=0; i<data.commands.length; i++) {
			cmd = data.commands[i];
			if (cmd) {
				if (cmd.refresh != null) {
					var par = {};
					par["rnd"] = rnd();
					par["getChange"] = "";
					postAndParseData(par);
				} else if (cmd.prevUI != null) {
					if (cmd.prevUI == 1)
						document.location.hash = "cmd=prevUI&rnd=" + rnd();
					else
						toActiveMain();
				} else if (cmd.main_ui != null) {
					toActiveMain();
				} else if (cmd.closePopupInterface != null) {
					if (window.popupcount > 0) {
						var did = 'or3_popup' + (window.popupcount - 1);
						showChangeMsg();
						window.dialogResult[did] = '0';
						var opts = $('#' + did).dialog('options');
					    
					    var par = {};
						par["uid"] = opts.uid ;
						par["val"] = cmd.closePopupInterface;
						par["cmd"] = "closePopup";
	
						loadData(par, true, function(data) {
							if (!data.result || data.result == 'success') {
								$("#" + did).dialog('destroy');
							} else {
								showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
								setDialogBtnsEnabled(did, true);
							}
						});
					}
				} else if (cmd.closeInterface != null) {
					closeIfc();
				} else if (cmd.start_ui) {
					if (document.location.hash.indexOf("cmd=openTask&uid=" + cmd.start_ui) == -1) {
						document.location.hash = "cmd=openTask&uid=" + cmd.start_ui + "&id=" + activeMenu + "&rnd=" + rnd();
						if (dialogOpened) {
							window.dialogResult[dialogOpened] = '0';
							$('#' + dialogOpened).dialog('destroy');
						}
					}
				} else if (cmd.next_ui) {
					document.location.hash = "cmd=openTask&uid=" + cmd.next_ui + "&id=" + activeMenu + "&rnd=" + rnd();
					if (dialogOpened) {
						window.dialogResult[dialogOpened] = '0';
						$('#' + dialogOpened).dialog('destroy');
					}
				} else if (cmd.open_report) {
					var url = window.mainUrl + "&rnd=" + rnd() + "&cmd=opf&fn=" + encodeURIComponent(cmd.open_report);
					$('#report_frame').attr('src', url);
				} else if (cmd.signString) {
					var str = cmd.signString.str;
					var path = cmd.signString.path;
					var pass = cmd.signString.pass;
					var cont = cmd.signString.cont;
					generateUcgoSign(str, path, pass, cont);
				} else if (cmd.signTextWithNCA) {
					var text = cmd.signTextWithNCA.text;
					loadScript1(window.contextName + "/jsp/media/js/ncaLayerOps.js", function() {
						signTextWithNCA(text);
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
							buttons:[{text:'Закрыть', id: 'closeDialog', handler:function(){sendFile();}}]
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
					generateUcgoPKCS10(str);
				} else if (cmd.generateUcgoPKCS7) {
					var str = cmd.generateUcgoPKCS7.str;
					generateUcgoPKCS7(str);
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
					if (dialogOpened){
						setDialogBtnsEnabled(dialogOpened, true);
					}
					
					if (cmd.showErrors.btnEdt && cmd.showErrors.btnIgn){
						showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, cmd.showErrors.btnIgn, cmd.showErrors.btnEdt);
					}else{
						showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, translation['ignore']);
					}
				} else if (cmd.showOptions) {
					$('body').unblock();
					showOptions(cmd.showOptions.options);
				} else if (cmd.confirm) {
					confirmMessage(cmd.confirm);
				} else if (cmd.alert != null) {
					alert(cmd.alert, false);
				} else if (cmd.logout) {
					logout();
				} else if (cmd.alertAndDrop) {
					alert(cmd.alertAndDrop, true, true);
				} else if (cmd.alertError != null) {
					alertError(cmd.alertError, false);
				} else if (cmd.alertWarning != null) {
					alertWarning(cmd.alertWarning, false);
				} else if (cmd.alertInfoFlow != null) {
					alert(cmd.alertInfoFlow, true);
				} else if (cmd.alertErrorFlow != null) {
					alertError(cmd.alertErrorFlow, true);
				} else if (cmd.alertWarningFlow != null) {
					alertWarning(cmd.alertWarningFlow, true);
				} else if (cmd.slide) {
					slide(cmd.slide);
				} else if (cmd.showWaiting) {
					document.body.style.cursor = 'wait';
					blockPage(cmd.showWaiting);
				} else if (cmd.closeWaiting) {
					document.body.style.cursor = 'default';
					$('body').unblock();
				} else if (cmd.disableCancelBtn) {
					hideChangeMsg();
				} else if (cmd.nodeType) {
					$('#nextBtn').linkbutton({text: cmd.nodeType});
				} else if (cmd.cursor) {
					if (cmd.cursor == "0") {
						document.body.style.cursor = 'default';
						$('body').unblock();
					} else if (cmd.cursor == "1") {
						document.body.style.cursor = 'wait';
						blockPage();
					} else if (cmd.cursor == "2") {
						document.body.style.cursor = 'pointer';
					}
				} else if (cmd.stack) {
					showStack(cmd.stack);
				} else if (cmd.hideFullPath) {
					$("#fullPath").hide();
				} else if (cmd.deleteTask) {
					deleteTask(cmd.deleteTask);
				} else if (cmd.addTask) {
					addTask(cmd.addTask);
				} else if (cmd.updateTask) {
					updateTask(cmd.updateTask);
				} else if (cmd.deleteOrders) {
					deleteOrders(cmd.deleteOrders);
				} else if (cmd.updateOrders) {
					updateOrders(cmd.updateOrders);
				} else if (cmd.reload) {
					reload(cmd.reload);
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
				} else if (cmd.readIdCard) {
					readIdCard();
				} else if (cmd.loadApplet) {
					loadApplet(cmd.loadApplet);
				} else if (cmd.openArh) {
					startArch(cmd.openArh);
				} else if (cmd.openDocument) {
					openDocument(cmd.openDocument);
				} else if (cmd.askPassword) {
					askPassword(function(res) {
						if (res != null)
							post({"promptRes":res, "promptAction":1});
						else
							post({"promptAction":0});
							
					}, cmd.askPassword);
				}
			}
		}
	} 
}

function confirmMessage(msg) {
	var a = msg.length;
	if (a<20){
		width = '200';
		msg = "<div style='text-align:left'>"+msg+"</div>";
	} else if (a<61) {
		width = '450'
	} else {
		width = '600'
	}
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = messagerZindex++;
	$.messager.defaults.zIndex = ++messagerZindex;
	
	$.messager.confirm({
		title:'',
        msg:msg,
        width: width,
        height: a>3000 ? 500 : 'auto',
        icon:'question',
        showType:null,
        fn: function(e){
        	$.fn.window.defaults.zIndex = oldZindex;
    		if (e){
    			post({"confirm":"3"});
    		} else {
    			post({"confirm":"4"});
    		}
    	}
    });
}

function alert(msg, command, isLogout) {
	if (msg.length == 0) msg = "-Пустое сообщение-";
	var a = msg.length;
	if (a<20){
		width = '200';
		msg = "<div style='text-align:left'>"+msg+"</div>";
	} else if (a<61) {
		width = '450'
	} else {
		width = '600'
	}
	
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = messagerZindex++;
	$.messager.defaults.zIndex = ++messagerZindex;

	$.messager.alert({
		title:'',
        msg:msg,
        width: width,
        height: a>3000 ? 500 : 'auto',
        icon:'info',
        showType:null,
        fn: function(e){
        	$.fn.window.defaults.zIndex = oldZindex;
        	if (command) {
        		if (isLogout) {
        			logout();
        		} else {
        			post({"alert":"0"});
        		}
        	}
        }
    });
}

function alertError(msg, command) {
	var a = msg.length;
	if (a<20){
		width = '200';
		msg = "<div style='text-align:left'>"+msg+"</div>";
	} else if (a<61) {
		width = '450'
	} else {
		width = '600'
	}
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = messagerZindex++;
	$.messager.defaults.zIndex = ++messagerZindex;
	$.messager.alert({
		title:'Ошибка!',
        msg:msg,
        width: width,
        height: a>3000 ? 500 : 'auto',
        icon:'error',
        showType:null,
        closable: false,
        fn: function(e){
        	$.fn.window.defaults.zIndex = oldZindex;
        	if (command) post({"alert":"0"});
        }
    });
}

function alertWarning(msg, command) {
	var a = msg.length;
	if (a<20){
		width = '200';
		msg = "<div style='text-align:left'>"+msg+"</div>";
	} else if (a<61) {
		width = '450'
	} else {
		width = '600'
	}
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = messagerZindex++;
	$.messager.defaults.zIndex = ++messagerZindex;
	$.messager.alert({
		title:'Предупреждение!',
        msg:msg,
        width: width,
        height: a>3000 ? 500 : 'auto',
        icon:'warning',
        showType:null,
        closable: false,
        fn: function(e){
        	$.fn.window.defaults.zIndex = oldZindex;
        	if (command) post({"alert":"0"});
        }
    });
}

function toActiveMain() {
	var a = $('#' + activeMenu);
	document.location.hash = a.attr('href');
	a.click();
}

$(function() {
	longPolling();
	if ($('#ping').length > 0) ping();
	$('.box').css('width22', ($(document).width()-200)+"px");

	var style = document.createElement('style');
	style.type = 'text/css';
	css = '.tamur-tabs-bak {width:100% } '+
		   '.dialog-content .tamur-tabs { width: 850px;}';
	style.innerHTML = css;
	document.getElementsByTagName('head')[0].appendChild(style);
	$("body").append($("<div></div>").attr("id", "trash").css("display", "none"));
	$("#trash").append($("<iframe></iframe>").attr("id", "report_frame").css("display", "none"));
	$(".container").append($("<div></div>").attr("id", "changePanel").css("display", "none"));

	$("#app, .ui-toolbar").on('click', function (e) {
		// источник события
		var target = e && e.target || event.srcElement;
		return hidePopUp(target,"#app");
	});
	
	$('#app').panel({
		onLoad: function() {
			styles = {};
			if (typeof $("#app .mainPanel").attr("data-uiTitle") !== "undefined") {
				$('.ui-title').text($("#app .mainPanel").attr("data-uiTitle"));	
			}
			if(!onTool)
				$("body").find('[tooltip]').removeAttr("tooltip");
			/************************************/
			$("body").find('[tooltip]').each(function(i) {
				var temp = $(this);
				if ($(this).attr("tooltip") == 'null')
					return false;
				if ($(this).parent().parent().parent().hasClass('easyui-treegrid')) {
					$(this).parent().parent().parent().parent().find('.datagrid-cell').parent().each(function(j) {
						if($(this).attr('field') == temp.attr('id'))
							attachTooltip($(this), temp);
					});
				}
					
				if ($(this).hasClass('easyui-datebox') || $(this).hasClass('easyui-datetimebox') || $(this).hasClass('easyui-combobox')) {
					$(this).parent().find('.textbox-text').each(function(j) {
						attachTooltip($(this), temp);
					});
				} else if ($(this).hasClass('or3-file-upload')) {
					attachTooltip($(this).parent('span'), $(this));
				} else {
					attachTooltip($(this), $(this));
				}
			});
			/************************************/
			preparefileUpload();
			loadData({}, true);
			loadReports();
			createWYSIWYG($("[wysiwyg]"));
			// удалить элементы предыдущего интерфейса
			var forDel = $(".ui-toolbar").find("[onTop]");
			for (var j = 0; j < forDel.length; j++) {
				$("#"+$(forDel[j]).attr("id")).remove();
			} 
			setElemForTopPane($("[onTop]"));
			
			clickPopUpContent('app');
			showPopUpContent();
			var colPan = $(".ttl-coll-pan-v");
			for (var j = 0; j < colPan.length; j++) {
				var h = $(colPan[j]).height();
				var w = $(colPan[j]).width();
				var p = $(colPan[j]).parent();
				$(p).css('height',w+'px');
				$(p).css('width',h+'px');
				$(colPan[j]).css('height',h+'px');
				$(colPan[j]).css('width',w+'px');
				var t = Math.floor((w-h)/2);
				if($(colPan[j]).hasClass('vertical-90')){
					$(colPan[j]).css('transform','rotate(-90deg) translate(-'+t+'px, -'+t+'px)');
				} else {
					$(colPan[j]).css('transform','rotate(90deg) translate('+t+'px, '+t+'px)');
				}
			} 
			
			////////////
			$('.clean-btn').click(function(e) {
				var par = {};
				par["uid"] = $(this).attr('id').substring(3); // первые три символа это префикс "clr"
				par["cmd"] = "clr";
				postAndParseData(par);
				return false;
			});
										
			// добавить события для отслуживание фокуса компонента
			var dboxs = $('.easyui-datebox, .easyui-datetimebox, .easyui-combobox, .format_hh_mm, .format_dd_mm_yyyy_hh_mm, .easyui-numberbox');
			for (var i = 0; i < dboxs.length; i++) {
				 $(dboxs[i]).parent().click(function(e) {
					if ($(this).find('.textbox-text').attr("readonly") != "readonly") {
						var id = $(this).find('input').attr('id');
						if (id != null && id != currentSelectedId) {
							currentSelectedId = id;
							var par = {"cmd":"fcs","uid":id};
							post(par);
						}
					}
				});
			}
			
			$('.fcs').click(function(e) {
				if ($(this).attr("readonly") != "readonly") {
					var id = $(this).attr('id');
					if (id != null && id != currentSelectedId) {
						currentSelectedId = id;
						var par = {"cmd":"fcs","uid":id};
						post(par);
					}
				}
			});
			
			// загружаем компонент CheckboxList
			$('.checklist').each(function(i) {
				var list = $(this);
				var url = list.attr('url');
				$.ajax({
					type : 'post',
					url : url + (url.indexOf("?") > 0 ? "&" : "?") +"rnd=" + rnd(),
					data : "",
					success : function(res) {
						var html = '';
						if (res.result == null) {
							$.each(res, function(i, item) {
								html += '<label class="floating"><input type="checkbox" value="' + i + '"' + (item.checkbox ? ' checked="checked"' : '') + '>' + item.text + '</label>';
							});
						}
						list.html(html);
					},
					dataType : 'json',
					async : true
				});
			});
			
			$('.easyui-datebox, .easyui-datetimebox').each(function(i) {
				var db = $(this);
				var hidePanel = db.attr('hidePanel') == 'true';
				if (hidePanel) {
					var opts = $(this).datebox("panel").panel("options");
					opts.nopanel = true;
					
					var fld = $(this).parent().find('.textbox-text');
					var cal = $(this).parent().find('.textbox-addon');
					
					if (cal.css('display') != "none") {
						cal.hide();
						fld.width(fld.width() + 18);
					}
				}
			});
			
			if (!tasksLoaded) {
				loadTasksContent(false);
			}
		},
		onBeforeOpen : function() {
		}
	});
	
	$('#left-panel-content > ul').mCustomScrollbar({
		mouseWheel:true,
		autoHideScrollbar:true,
		theme: 'dark-thick'
	});

	$(window).on('hashchange', function() {
		var day = location.hash;
		if (prevHash != day) {
			prevHash = day;
			if (day == "")
				return false;
			
			loadUI(encodeURI(day));
		}
	});

	if ($('#startDiv').length > 0) {
		$('#startDiv').load(window.contextName + '/jsp/start.jsp?guid=' + guid, function() {
			$('.portlet').css('height', ($(window).height()-160)+"px");
			$('.easyui-panel:not(.tamur-tabs)').panel();
			refreshTasks();
		});
	}
	if ($('#oldStartDiv').length > 0) {
		$('#oldStartDiv').load(window.contextName + '/jsp/oldStart.jsp?guid=' + guid, function() {
			$('.portlet').css('height', ($(window).height()-160)+"px");
			$('.easyui-panel:not(.tamur-tabs)').panel();
			if ($('#startDiv').length == 0) refreshTasks();
		});
	}
	if ($('#helpWnd').length > 0) {
		$('#helpWnd').load(window.contextName + '/jsp/faq.jsp?guid=' + guid, function() {
			$('.portlet').css('height', ($(window).height()-160)+"px");
			$('.easyui-panel:not(.tamur-tabs)').panel();
			if (($('#startDiv').length == 0) && ($('#oldStartDiv').length == 0)) refreshTasks();
			var par = {"sfunc":1,"cls":"XmlUtil","name":"getHelpTabs"};
			saveAsyncPostRequest(window.mainUrl, par, parseHelpTabs, 'json');
		});
	}

	var openingIfc = false;
	var day = location.hash;
	if (day == null || day.length == 0) {
		day = startHash;
	} else {
		openingIfc = (day.indexOf("openTask") > -1);
	}
	loadUI(encodeURI(day));

	$("#box").on('click', '.task-link', function() {});
	$("#trash").append("<div id='wait_msg'><h1>" + translation['wait'] + "</h1></div>");
	
	key('ctrl+shift+z', function(){
		if (typeof $('.mainPanel').attr("data-uid") !== "undefined")
			alert($('.mainPanel').attr("data-uid"));
		else
			alert($('.mainPanel').attr("uid"));
		return false; });
	
	if (changePass == true || changePass == "true") {
		changePwdDialog(true);
	}
	
	loadArchList();
	loadProccessList();
	loadDictsList();
	loadAdminList();
	initProfile();
	if (notis) {
		loadNotification();
	}

	if (!openingIfc)
		loadTasksContent(false);
});

function loadTasksContent(searchEvent) {
	tasksLoaded = true;
	$('#tasksPages').pagination({
		pageList: [100,200,500,1000],
		layout:['list','sep','first','prev','sep','links','sep','next','last','refresh','sep','manual'],
		total:tasksCount,
		pageSize:100,
		onSelectPage:function(pageNumber, pageSize){
			$(this).pagination('loading');
			var rowLast = pageNumber*pageSize;
			if(tasksCount>-1 && rowLast>tasksCount && !searchEvent) rowLast=tasksCount;
			var rowFirst = (pageNumber-1) * pageSize + 1;
			loadTasks(rowFirst, rowLast);
			$(this).pagination('loaded');
		}
	});
	$('#tasksPages').pagination('select');
}

function loadTasks(rowFirst, rowLast) {
	loadingCount++;
	var searchText = $('#taskSearchPage').val();
	
	var url = window.mainUrl;
	var par = {};
	par["cmd"] = 'loadTasks';
	par["rowFirst"] = rowFirst;
	par["rowLast"] = rowLast;
	if (searchText && searchText.length > 0) {
		par["searchText"] = searchText;
	}
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url + "&rnd=" + rnd(),
		data : par,
		success : function(data) {
			checkData(data);
			html = '<table width="100%" border="0">';
			var processList = data.processes;
			tasksCount = data.tasksCount;
			processList.reverse();
			$.each(processList, function(i, process) {
				$.each(process, function(key, task) {
					html += makeTask(key, task);
				});
			});
			html += '</table>';
			$('#tasksList .pcontent').html(html);
			setTasksCount(tasksCount);
			loadingCount--;
		},
		dataType : 'json',
		async : false
	});
}

function setTasksCount(count) {
	tasksCount = count;
	$('.processes_counter').text(count);

	$('#tasksPages').pagination('refresh', {
		total: tasksCount
	});
}

$("body").on('keypress', '#processSearchPage', function(e) {
	var key = e.which || e.keyCode;
	if (key === 13) {
		indexSearchProces = 1;
		searchProcess = $(this).val();
		var par = {"cmd":"searchProcess","text":searchProcess};
		post(par, function (data){
			checkData(data);
			countSearchResulProces = data.size;
			if(data.parent) {
				var arr = data.parent.split(",").reverse();
				var col = arr.length-1;
				if(col == 1) {
					var node = $('#processTree').tree('find', arr[1]);
					if(node != null) {
						$('#processTree').tree('select', node.target);
						selectProcess(node);
					}
				} else {
					for(var i = 1; i < col; i++) {
						searchProcCallLater('expand', arr[i], 5);
					}
					searchProcCallLater('select', arr[col], 5);
				}
			} else {
				alert("Ничего не найдено!");
			}
		});
	}
});

$("body").on('keypress', '#dictSearchPage', function(e) {
	var key = e.which || e.keyCode;
	if (key === 13 || key === 10) {
		searchDict(e.ctrlKey);
	}
});

function searchDict(isContinue) {
	var searchDict = $('#dictSearchPage').val();
	var par = {"cmd":"searchDict","text":searchDict};
	post(par, function (data) {
		checkData(data);
		if (data.dicts) {
			if (isContinue) {
				var selectedNode = $('#dictsTree').tree('getSelected');
				if (selectedNode) {
					var isSelect = false;
					for (var i = 0; i < data.dicts.length; i++) {
						var dict = data.dicts[i];
						if (!isSelect) {
							if (dict.parentNodeUid && dict.parentNodeUid == selectedNode.id) {
								isSelect = true;
							} else if (dict.nodeUid == selectedNode.id) {
								isSelect = true;
							}
						} else {
							var node = $('#dictsTree').tree('find', dict.parentNodeUid ? dict.parentNodeUid : dict.nodeUid);
							if (node != null) {
								$('#dictsTree').tree('select', node.target);
								selectDict(node);
							}
							break;
						}
					}
				} else {
					var dict = data.dicts[0];
					var node = $('#dictsTree').tree('find', dict.parentNodeUid ? dict.parentNodeUid : dict.nodeUid);
					if(node != null) {
						$('#dictsTree').tree('select', node.target);
						selectDict(node);
					}
				}
			} else {
				var dict = data.dicts[0];
				var node = $('#dictsTree').tree('find', dict.parentNodeUid ? dict.parentNodeUid : dict.nodeUid);
				if(node != null) {
					$('#dictsTree').tree('select', node.target);
					selectDict(node);
				}
			}
		} else {
			alert("Ничего не найдено!");
		}
	});
}

function searchProcCallLater(name, id, count) {
	//console.log("Count = " + count);
	var node = $('#processTree').tree('find', id);
	if (node != null) {
		$('#processTree').tree(name, node.target);
		if(name === 'select') selectProcess(node);
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){searchProcCallLater(name, id, count);}, 800);
	}
}

function selectProcess(node) {
	$.post(window.mainUrl+"&cmd=getProcessData&leaf", {id:node.id}, function (data){
		$('#processesList_body').html("");
		$.each(data, function(e,proc) {
			funcName = "startProcc";
			var u = proc.id.indexOf(".");
			var key = proc.id.substring(u+1);
			var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
			$('#processesList_body').append("<div id='process" + key + "' class='ico_" + proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
			addProcIcon('#processesList_body .proc.ico_' + proc.id.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+'.png');
			if (proc.time != null) {
				$('#process'+key).tooltip({
			           position: 'bottom',
			           content: translation['lastSuccesProcessDef']+'<br>'+proc.time,
			           onShow: function(){
			               $(this).tooltip('tip').css({
			                   backgroundColor: '#fafad2',
			                   borderColor: '#666'
			               });
			           }
			    });
			}
		});
	}, 'json');
}

function selectDict(node) {
	$.post(window.mainUrl+"&cmd=getDictData&leaf", {id:node.id}, function (data){
		$('#dictsList_body').html("");
		$.each(data, function(e,proc) {
			funcName = "startDict";
			$('#dictsList_body').append("<div class='ico_" + proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ");
		});
	}, 'json');
}

$("body").on('change', '.tree-search', function() {
	var treeId = $(this).attr('id').substring(1);
	var str = $(this).val();
	lastId = -1;
	searchTreeId = treeId;
	searchTreeVal = str;
	findNode(treeId, str, false);
});

$("body").on('change', '.treefield-search', function() {
	var treeId = $(this).attr('id').substring(1);
	var str = $(this).val();
	lastId = -1;
	searchTreeId = treeId;
	searchTreeVal = str;
	findNode(treeId, str, true);
});

$("body").on('change', '.treetable-search', function() {
	var treeId = $(this).attr('id').substring(1);
	var str = $(this).val();
	lastId = -1;
	searchTreeId = treeId;
	searchTreeVal = str;
	findTreeTableNode(treeId, str);
});

$("body").on('keypress', ".table-search", function(e) {
	if (e.which && e.which == 13) {
		return false;
	}
});

$("body").on('keyup', ".table-search", function(e) {
	if (e.which && e.which == 13) {
		var treeId = $(this).attr('id').substring(1);
		var str = $(this).val();
		lastId = -1;
		searchTreeId = treeId;
		searchTreeVal = str;
		findTableNode(treeId, str);
		if (!msie) {
			e.preventDefault();
			e.stopPropagation();
		}
		return false;
	}
});

function findNode(treeId, str, trfld) {
	if (str.length > 0) {
		var par = {"treeFind":treeId,"id":lastId > -1 ? "" + lastId : "", "title":str};
		post(par, function(data) {
			checkData(data);
			if (data.node) {
				var nodeId = data.node;
				lastId = nodeId;
				
				if (trfld) treeId = 'trfld'+treeId;
				var root = $('#' + treeId).tree('getRoot');
				
				if (root.id == nodeId) {
					$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');

					var html = sanitizeHtml($(root.target).find('.tree-title').get(0).textContent);
					var ind = html.toUpperCase().indexOf(str.toUpperCase());
					html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + str.length) + '</span>' + html.substring(ind + str.length);
					$(root.target).find('.tree-title').html(html);
					$('#' + treeId).tree('select', root.target);
					$('#' + treeId).parent().scrollTop($(root.target).offset().top - $('#' + treeId).offset().top);
				} else {
					var children = $('#' + treeId).tree('getChildren', root.target);
					for (var i=0; i<children.length; i++) {
						$('#' + treeId).tree('remove', children[i].target);
					}
					$('#' + treeId).tree('reload');
					treeCallLater('select', $('#' + treeId), nodeId, 20);
					treeCallLaterHigliht($('#' + treeId), nodeId, str, 20);
				}
				$('#_' + treeId).focus();
			} else {
				alert('Ничего не найдено!');
			}
		});
	} else {
		$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
	}
}

function findTreeTableNode(treeId, str) {
	if (str.length > 0) {
		var par = {"treeFind":treeId,"id":lastId > -1 ? "" + lastId : "", "title":str};
		post(par, function(data) {
			checkData(data);
			if (data.node) {
				var nodeId = data.node;
				lastId = nodeId;
				
				var root = $('#' + treeId).treegrid('getRoot');
				
				if (root.id == nodeId) {
					var tr = $('.datagrid-view2 tr[node-id=' + root.id + ']');
					var div = tr.closest('.datagrid-body');
					var table = tr.closest('.datagrid-btable');
					
					table.find('.higliht').removeClass('higliht').addClass('nohigliht');

					str = sanitizeHtml(str);
					
					$.each(tr.find('td'), function(i, td) {
						var tag = $(td).find('.tree-title');
						if (tag.length == 0) tag = $(td).find('div');
						
						var html = sanitizeHtml(tag.get(0).textContent);
						var ind = html.toUpperCase().indexOf(str.toUpperCase());
						if (ind > -1) {
							html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + str.length) + '</span>' + html.substring(ind + str.length);
							tag.html(html);
							$('#' + treeId).treegrid('select', root.id);
							div.scrollTop(tr.offset().top - table.offset().top);
						}
					});
				} else {
					var children = $('#' + treeId).treegrid('getChildren', root.id);
					for (var i = 0; i<children.length; i++) {
						if (root.id == children[i].parent)
							$('#' + treeId).treegrid('remove', children[i].id);
					}
					$('#' + treeId).treegrid('reload');
					treegridCallLater('select', $('#' + treeId), nodeId, 20);
					treedridCallLaterHigliht($('#' + treeId), nodeId, str, 20);
				}
				$('#_' + treeId).focus();
			} else {
				alert('Ничего не найдено!');
			}
		});
	} else {
		$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
	}
}

function findTableNode(tableId, str) {
	if (str.length > 0) {
		// Находим таблицу
		var t = $('#' + tableId);
		var panel = t.datagrid('getPanel');
		
		// Находим ячейку содержащую текст
		var tds = $('.datagrid-cell:contains("' + str + '")', panel);
		
		var nextIndex = -1;
		
		var tr = null;
		for (i=0; i<tds.length; i++) {
			// Строки
			tr = $(tds.get(i)).parent().parent();
			// индекс строки
			var rIndex = parseInt(tr.attr('datagrid-row-index'));
			if (rIndex > lastId) {
				nextIndex = rIndex;
				break;
			}
		}
		
		if (nextIndex > -1) {
			lastId = nextIndex;
	        t.datagrid('clearSelections');
	        // выделяем строку
	        t.datagrid('selectRow', nextIndex);

	        // отправляем номер выделенной строки на сервер
	        var index = globalIndex(t, nextIndex);
	        makeSelection(t, index);
	        
			// Таблица и ее контейнер 
			var div = tr.closest('.datagrid-body');
			var table = div.children('.datagrid-btable');
			// скролируем контейнер до нужной строки
			div.scrollTop(tr.offset().top - table.offset().top);
		} else {
			alert('Ничего не найдено!');
		}
	}
}

function treegridCallLater(name, comp, id, count) {
	//console.log("Count = " + count);
	var node = comp.treegrid('find', id);
	if (node != null) {
		comp.treegrid(name, id);
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){treegridCallLater(name, comp, id, count);}, 800);
	}
}

function treedridCallLaterHigliht(comp, id, text, count) {
	//console.log("Count = " + count);
	var node = comp.treegrid('find', id);
	if (node != null) {
		var tr = $('.datagrid-view2 tr[node-id=' + id + ']');
		var div = tr.closest('.datagrid-body');
		var table = div.children('.datagrid-btable');
		
		table.find('.higliht').removeClass('higliht').addClass('nohigliht');
		
		text = sanitizeHtml(text);
		
		$.each(tr.find('td'), function(i, td) {
			var tag = $(td).find('.tree-title');
			if (tag.length == 0) tag = $(td).find('div');
			
			var html = sanitizeHtml(tag.get(0).textContent);
			var ind = html.toUpperCase().indexOf(text.toUpperCase());
			if (ind > -1) {
				html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + text.length) + '</span>' + html.substring(ind + text.length);
				tag.html(html);
				div.scrollTop(tr.offset().top - table.offset().top);
			}
		});
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){treedridCallLaterHigliht(comp, id, text, count);}, 800);
	}
}

function parseHelpTabs(res) {
	if (res) {
		checkData(res);
		if (res.tabs) {
			$.each(res.tabs, function(i, tab) {
				var html = '<div class="Ek-help" title="' + tab.title + '"';

				if (tab.pic && tab.pic.length > 10) {
					html += 'data-options="iconCls:\'icon-' + tab.id + '\'"';

					var css = '.icon-' + tab.id +' {background: url("' + tab.pic + '") no-repeat scroll center center}';
					$('style').first().append('\n' + css);
				}

				html += ' style="padding:20px;">';
				
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getHelpContent","arg0":tab.id};
				saveAjaxRequest('POST', window.mainUrl, par, function(content) {
					if (content)
						content = content.replace('name=getHelpFile&', 'guid=' + guid + '&name=getHelpFile&');
					
					html += content;
				}, 'html', false);
				html += '</div>';

				$('#helpWnd_tab').append($(html));
			});
			$('#helpWnd #helpWnd_tab').tabs({
			    border:false			    
			});
			$( ".aboutSys" ).click(function() {
				$(this).next().toggle();
			});
		}
	}
}

function reload(uid) {
	var par = {"cmd":"reload","uid":uid};
	saveAsyncPostRequest(window.mainUrl, par, function(content) {
		var parent = $('#' + uid).parent();
		parent.html(content);
		parent.find('.easyui-panel:not(.tamur-tabs)').panel();
		parent.find('.easyui-datagrid').datagrid();
		preparefileUpload(parent);

		var par = {};
		par["rnd"] = rnd();
		par["getChange"] = "";
		postAndParseData(par);

	}, 'html');
}

function openDocument(id, ext, fn) {
	var url = window.mainUrl;
	if (ext == null && fn == null) {
		url += "&trg=frm&cmd=opf&fn=" + encodeURIComponent(id);
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&rnd=" + rnd();
	} else {
		url += "&sfunc&cls=XmlUtil&name=getHelpFile&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + id + "&rnd=" + rnd();
	}
	$('#report_frame').attr('src', url);
}

$("body").on('click', '.coll-pan .ttl-coll-pan', function() {
	// первый символ это префикс "t", cnt - префикс контента
	var content = $('#cnt'+ $(this).attr('id').substring(1));
    if( $(content).hasClass('expanded') ){
        $(content).removeClass('expanded');
    } else {
    	$(content).addClass("expanded");
    }
    return false;
});

$("body").on('click', '.coll-pan-v .ttl-coll-pan-v', function() {
	// первый символ это префикс "t", cnt - префикс контента
	var content = $('#cnt'+ $(this).attr('id').substring(1));
    if( $(content).hasClass('expanded-v') ){
        $(content).removeClass('expanded-v');
    } else {
    	$(content).addClass("expanded-v");
    }
    return false;
});

$("body").on('click', '.accordion .ttl-coll-pan', function() {
	var parent = $(this).parent();
	// первый символ это префикс "t", cnt - префикс контента
	var content = $('#cnt'+ $(this).attr('id').substring(1));
	if($(content).hasClass('accord')) {		
		var panID = $(content).attr('id');
		if($(parent).attr('multi') == '0'){
			var panels = $(parent).find('.cnt-coll-pan');
			for (var j = 0; j < panels.length; j++) {
				if($(panels[j]).attr('id') != panID && $(panels[j]).hasClass('expanded')){
					$(panels[j]).removeClass('expanded');
				}
			}
		}
	    if($(content).hasClass('expanded') ){
	        $(content).removeClass('expanded');
	    } else {
	    	$(content).addClass("expanded");
	    }
    }
    return false;
});

$("body").on('click', '.accordion-v .ttl-coll-pan-v', function() {
	var parent = $(this).parents('.accordion-v');
	// первый символ это префикс "t", cnt - префикс контента
	var content = $('#cnt'+ $(this).attr('id').substring(1));
	if($(content).hasClass('accord')) {
		var panID = $(content).attr('id');
		if($(parent).attr('multi') == '0'){
			var panels = $(parent).find('.cnt-coll-pan-v');
			for (var j = 0; j < panels.length; j++) {
				if($(panels[j]).attr('id') != panID && $(panels[j]).hasClass('expanded-v')){
					$(panels[j]).removeClass('expanded-v');
				}
			}
		}
	    if($(content).hasClass('expanded-v')){
	        $(content).removeClass('expanded-v');
	    } else {
	    	$(content).addClass("expanded-v");
	    }
    }
    return false;
});

function attachTooltip(comp, baseComp) {
	var id = "#or" + baseComp.attr("id");
	if (baseComp.attr("tooltip") != 'null' && $(id).length > 0) {
		comp.tooltip({
			deltaX: -20,
		    showDelay: 750,
			content: function() {
				return $(id);
			},
			onUpdate: function(content){
		    },
			onShow: function(){
				var t = $(this);
		        t.tooltip('tip').css({backgroundColor: '#FFDEAD',borderColor: '#666'}).unbind().bind('mouseenter', function(){
		            t.tooltip('show');
		        }).bind('mouseleave', function() {
		            t.tooltip('hide');
		        });
			},
			onPosition: function(){
				var t = $(this);
				var left = t.offset().left + t.width() - t.tooltip('tip').width() - 15;
				var arrowLeft = t.tooltip('tip').width() - 20;
				if (left < 5) {
					arrowLeft = arrowLeft - 5 + left;
					left = 5;
				}
				t.tooltip('tip').css('left', left);
				t.tooltip('arrow').css('left', arrowLeft);
			}
		});
	}
}

function initProfile() {
	$("#yourPhoto").bind('contextmenu', function(e) {
		e.preventDefault();
		$('#mm').menu('show', {
			left: e.pageX,
			top: e.pageY
		});
	});
	
	var par = {"objId":userId,"attr":'аватар'};
	$.post(window.mainUrl + "&getAttr&rnd=" + rnd(), par, function(res) {
		checkData(res);
		if (res.result) {
			$('#yourImg').attr('src', "data:image/png;base64," + res.result);
			$('#popup_user_content .phPr img').attr('src', "data:image/png;base64," + res.result);
		}
		$('#account_panel').tooltip({
			deltaX: -20,
		    content: $('#popup_user_content').html(),
		    showEvent: 'mouseenter',
		    onUpdate: function(content){
		    },
		    onPosition: function() {
		    	$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() - 15);
		    	$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() - 20);
	    	},
		    onShow: function(){
		        var t = $(this);
		        t.tooltip('tip').unbind().bind('mouseenter', function(){
		            t.tooltip('show');
		        }).bind('mouseleave', function() {
		            t.tooltip('hide');
		        });
		    }
		});
	}, 'json');	
}

getContactInfo();
loadProccessList2();
hideItems();
$('#helpPhone').tooltip({
	deltaX: -20,
	content: $('#popup_help_content').html(),	
    onUpdate: function(content){
    },
    onPosition: function() {
    	$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() - 15);
    	$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() - 20);
	},
    onShow: function(){
        var t = $(this);
        t.tooltip('tip').unbind().bind('mouseenter', function(){
            t.tooltip('show');
        }).bind('mouseleave', function() {
            t.tooltip('hide');
        });
    }
});
$.blockUI.defaults.baseZ = 300000;
$.extend($.fn.combo.defaults, {zIndex: 150000});
$.extend($.fn.window.defaults, {zIndex: 110000, shadow: false});
$.extend($.messager.defaults, {zIndex: messagerZindex, width: 450, minHeight: 100});
$.extend($.fn.combobox.defaults.filter = function(q,row){
	var opts = $(this).combobox('options');
	var toUpperCase = opts.toUpperCase;
	if (toUpperCase) {
		return row[opts.textField].toUpperCase().indexOf(q.toUpperCase()) >= 0;
	} else {
		return row[opts.textField].indexOf(q) >= 0;
	}
});

$.extend($.fn.combobox.defaults, {panelHeight: 'auto', panelMaxHeight: 400});

$.extend($.fn.menu.defaults, {
	zIndex: 120000,
	onHide: function() {
		if($('.calendar:visible').length > 0)
			$(this).menu('show');
	},
	onShow: function() {
		if ($(this).attr('id') != null && $(this).attr('id').indexOf('pop') === 0)
			$(this).unbind('mouseleave.menu').unbind('mousedown.menu');
	}
});

$.extend($.fn.calendar.defaults, {
	firstDay: 1,
	months: lbl['months'],
	weeks: lbl['weeks'],
	currentText: lbl['today'],
	closeText: lbl['close']
});
$.extend($.fn.datebox.defaults, {
	currentText: lbl['today'],
	closeText: lbl['close']
});
$.extend($.fn.datetimebox.defaults, {
	currentText: lbl['today'],
	closeText: lbl['close']
});
$.fn.setCursorPosition = function(position){
    if(this.length == 0) return this;
    return $(this).setSelection(position, position);
};

$.fn.tabs.defaults.onSelect = function(title,index) {
	var tp = $(this);
	var par = {"cmd":"selTab","uid":tp.attr('id'),"indx":index};
	post(par, function(data) {
		checkData(data);
		$.each(tablesToReloadMap, function(key, value) {
			$.each($('#' + key).parents('.easyui-tabs'), function (i, tb) { 
				if ($(tb).attr('id') == tp.attr('id')) {
					$('#' + key).datagrid('load');
				}
			});
		});
		tp.find('.easyui-tabs').tabs('resize');
	});
};

$.fn.setSelection = function(selectionStart, selectionEnd) {
    if(this.length == 0) return this;
    input = this[0];

    if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
    } else if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
    }

    return this;
};

$.fn.getSelectionLength = function() {
    if(this.length == 0) return 0;
    input = this[0];

    if (input.createTextRange != null) {
        var range = input.createTextRange();
        alert(range.text);
        return range.text.length;
    } else if (input.selectionStart != null) {
    	return input.selectionEnd - input.selectionStart;
    }

    return 0;
};

$.fn.focusEnd = function(){
    this.setCursorPosition(this.val().length);
    return this;
};

$.fn.datebox.defaults.formatter = function(date) {
	if (date != null) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		if (d < 10) d = "0" + d;
		if (m < 10) m = "0" + m;
		return d + '.' + m + '.' + y;
	} else
		return 'дд.мм.гггг';
};
var lastShowDate = null;
$.fn.datebox.defaults.onShowPanel = function() {
	lastShowDate = $(this).datebox("getText");
};
$.fn.datebox.defaults.onHidePanel = function() {
	var id = $(this).attr('id');
	var date = $(this).datebox("getText");
	if (id == 'cellDatabox') {
		endEditing();
	} else if (id && (date != lastShowDate || date == "")) {
		setValue(id, date);
		focusNext($(this).datebox("textbox"));
	}
};

var lastShowComboValue = null;
$.fn.combobox.defaults.onLoadSuccess = function() {
	var id = $(this).attr('id');
	var opts = $(this).combobox("options");
	var data = $(this).combobox("getData");
	var children = $(this).combobox("panel").children();
	if (data.length > 1) {
		for(var i = 0; i<data.length; i++) {
			$.each(children, function(index, obj){
				if (index == i) {
					var val = data[i][opts.valueField];
					if (window.hintOptions[id] != null)
						obj.title = window.hintOptions[id][val];
					return false;
				}
				else
					return true;
			});
		}
	}
};
$.fn.combobox.defaults.onShowPanel = function() {
	lastShowComboValue = $(this).combobox("getValue");
	var textboxCombo = $(this).combo("textbox");
	var panelCombo = $(this).combo("panel");
	panelCombo.children(".combobox-item").css({"font-size":textboxCombo.css("font-size"),"font-family":textboxCombo.css("font-family"),"font-style":textboxCombo.css("font-style"),"font-weight":textboxCombo.css("font-weight")});
	if (!$(this).hasClass("easyui-combobox")) {
		panelCombo.css("background-color", "rgb(255, 222, 173)");
	}
};
$.fn.combobox.defaults.onHidePanel = function() {
	var tableCombobox = $(this).hasClass("datagrid-editable-input");
	if(tableCombobox)
		endEditing();
	else {
		var id = $(this).attr('id');
		var opts = $(this).combobox("options");
		var val = $(this).combobox("getValue");
		var data = $(this).combobox("getData");
		var cleanCombo = true;
		for(var i = 0; i<data.length; i++) {
			if(data[i][opts.valueField] === val && id && val != lastShowComboValue && val != undefined) {
				setValue(id,val);
				cleanCombo = false;
				break;
			}
		}
		if (cleanCombo && val != lastShowComboValue && id)
			$(this).combobox('clear');
	}
};
$.fn.combobox.defaults.onChange = function(newValue, oldValue) {
	if (newValue != oldValue)
		delErrorType($(this));
}
$.fn.datebox.defaults.onChange = function(newValue, oldValue) {
	if (newValue != oldValue)
		delErrorType($(this));
}
$.fn.datetimebox.defaults.onChange = function(newValue, oldValue) {
	if (newValue != oldValue){
		var id = $(this).attr('id');
		setValue(id, newValue);
		delErrorType($(this));
	}
}
$.fn.numberbox.defaults.onChange = function(newValue, oldValue) {
	if (newValue != oldValue)
		delErrorType($(this));
}

$.fn.datetimebox.defaults.formatter = function(date) {
	var secs = $(this).hasClass('seconds');
	if (date != null) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		var h = date.getHours();
		var min = date.getMinutes();
		var sec = secs ? date.getSeconds() : 0;
		if (d < 10) d = "0" + d;
		if (m < 10) m = "0" + m;
		if (h < 10) h = "0" + h;
		if (min < 10) min = "0" + min;
		if (secs && sec < 10) sec = "0" + sec;
		
		return d + '.' + m + '.' + y + ' ' + h + ':' + min + (secs ? ':' + sec : '');
	} else
		return secs ? 'дд.мм.гггг чч:ММ:сс' : 'дд.мм.гггг чч:ММ';
};
$.fn.datetimebox.defaults.onSelect = function(date) {
	var id = $(this).attr('id');
	if (id == 'cellDatabox') {
		endEditing();
	} else if (id) {
		var value=$(this).datetimebox("textbox");
		setValue(id,value);
		focusNext($(this).datetimebox("textbox"));
	}
};
$.fn.datebox.defaults.parser = function(s) {
	var t = null;
	if (s != null) {
		if (s.length > 10) s = s.substring(0, 10);
		var dateParts = s.split(".");
		if (dateParts.length > 2) {
			t = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
			if (isNaN(t)) t = null;
		}
	}
	return t;
};
$.fn.datetimebox.defaults.parser = function(s) {	
	var t = null;
	if (s != null) {
		var dateTimeParts = s.split(" ");
		if (dateTimeParts.length > 1) {
			var s1 = dateTimeParts[0];
			var s2 = dateTimeParts[1];
			var dateParts = s1.split(".");
			var y = -1;
			var m = -1;
			var d = -1;
			var h = 0;
			var min = 0;
			var sec = 0;
			if (dateParts.length > 2) {
				y = dateParts[2];
				m = dateParts[1] - 1;
				d = dateParts[0];
			}
			var timeParts = s2.split(":");
			if (timeParts.length > 0) {
				h = timeParts[0];
				h = isNaN(parseInt(h)) ? 12 : parseInt(h);
			}
			if (timeParts.length > 1) {
				min = timeParts[1];
				min = isNaN(parseInt(min)) ? 0 : parseInt(min);
			}
			if (timeParts.length > 2) {
				sec = timeParts[2];
				sec = isNaN(parseInt(sec)) ? 0 : parseInt(sec);
			}
			t = new Date(y, m, d, h, min, sec);
			if (isNaN(t)) t = null;
		}

	}
	return t;
};

$.extend($.fn.calendar.methods, {
	moveTo: function(jq,date){
		return jq.each(function() {
			var isNull = false;
			if (date == null) {
				isNull = true;
				date = new Date();
			}
			$(this).calendar({year:date.getFullYear(),month:date.getMonth()+1,current:date});
			if (isNull) {
				$(this).calendar("options").current = null;
			}
		});
	}
});

window.clickedGrid = "";
window.selectedRow = [];

$.fn.datagrid.defaults.onLoadSuccess = function(rData) {
	var index = $(this).attr('selRows');
	selectTableRows($(this), index);

	var uid = $(this).attr("id");
	delete tablesToReloadMap[uid];

	$(this).parent().find('a.hyper').on('click', function() {
		if (!$(this).hasClass('btn-disabled')) {
			var cuid = $(this).attr("id");
			var row = $(this).attr("row");
			blockPage();
			document.location.hash = "cmd=openIfc&uid=" + uid + "&cuid=" + cuid + "&row=" + row;
		}
		return false;
	});
};

$.fn.datagrid.defaults.onLoadError = function() {
	var uid = $(this).attr("id");
	delete tablesToReloadMap[uid];
};

$.fn.datagrid.defaults.onBeforeLoad = function() {
	var isVisible = $(this).parents('.easyui-tabs').length == 0 // нету радительских табов
					|| ($(this).parents('.tabs-panels').length == $(this).parents('.easyui-tabs').length 				// или все табы распарсены 
					&& $(this).parents('.tabs-panels>.panel:visible').length == $(this).parents('.tabs-panels').length); // и видимы

	var uid = $(this).attr("id");
	
	if (isVisible && (tablesToReloadMap[uid] == null || tablesToReloadMap[uid] == 0)) {
		tablesToReloadMap[uid] = 1;
		return true;
	} else {
		if (tablesToReloadMap[uid] == null)
			tablesToReloadMap[uid] = 0;
		return false;
	}
};

function selectTableRows(t, rs) {
	t.attr('selRows', rs);

	var count = t.datagrid('getRows') != null ? t.datagrid('getRows').length : 0;
	if (count > 0) {
		t.datagrid('clearSelections');
		if (rs) {
			var rows = rs.split(',');
			var count = t.datagrid('getRows').length;    // row count
			
			for (var ind=0; ind<rows.length; ind++) {
				rows[ind] = pageIndex(t, rows[ind]);
			    if (rows[ind] >= 0 && rows[ind] < count) {
			        t.datagrid('selectRow', rows[ind]);
			    }
			}
			var selCol = t.attr('selectedCol');
			if (selCol != null) {
				t.datagrid('selectCell', {index:rows[0],field:selCol});
			}
			var toolbar_sel = $(t).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
			$(toolbar_sel).text((parseInt(rows[0])+1)+" /");
		}
	}
}

var ctrlPressed = false;
$(window).keydown(function(e) {
	if (e.which == 17) {
		ctrlPressed = true;
	}
}).keyup(function(e) {
	if (e.which == 17) {
		ctrlPressed = false;
	} else if (e.which == 114) {
		if (searchTreeId != null && searchTreeVal != null) {
			if ($('#_' + searchTreeId).hasClass('treetable-search'))
				findTreeTableNode(searchTreeId, searchTreeVal);
			else if ($('#_' + searchTreeId).hasClass('table-search'))
				findTableNode(searchTreeId, searchTreeVal);
			else if ($('#_' + searchTreeId).hasClass('treefield-search'))
				findNode(searchTreeId, searchTreeVal, true);
			else
				findNode(searchTreeId, searchTreeVal, false);
		} else if(searchProcess != "") {
			findProcess();
		}
	}
});

function findProcess(){
	if(countSearchResulProces > indexSearchProces) {
	var par = {"cmd":"searchProcess","text":searchProcess,"index":indexSearchProces};
	post(par, function (data){
		checkData(data);
		if(data.parent) {
			var arr = data.parent.split(",").reverse();
			var col = arr.length-1;
			if(col == 1) {
				var node = $('#processTree').tree('find', arr[1]);
				if(node != null) {
					$('#processTree').tree('select', node.target);
					selectProcess(node);
				}
			} else {
				for(var i = 1; i < col; i++) {
					searchProcCallLater('expand', arr[i], 5);
				}
				searchProcCallLater('select', arr[col], 5);
			}
		}
	});
	indexSearchProces++;
	} else {
		alert('Ничего не найдено!');
	}
}

$.fn.datagrid.defaults.onClickRow = function(rIndex, rData) {
	hidePopUp($(this),"#app");
	var tbl = $(this);
	var fn = function() {
		var objs = tbl.datagrid('getSelections');
		var indxs = [objs.length];
		for (var i = 0; i < objs.length; i++) {
			indxs[i] = tbl.datagrid('getRowIndex', objs[i]);
		}
		
		var index = globalIndex(tbl, rIndex);
		
		var val = '';
		var rowUnselecting = true;
		for (var i=0; i<indxs.length; i++) {
			var indx = indxs[i];
			indxs[i] = globalIndex(tbl, indxs[i]);
			
			if (indxs[i] != index) {
				if (ctrlPressed) {
					val += ',' + indxs[i];
				} else {
					tbl.datagrid('unselectRow', indx);
				}
			} else {
				rowUnselecting = false;
			}
		}
		if (rowUnselecting && indxs.length > 0) {
			if (val.length > 0 && val != '') {
				val = val.substring(1);
			} else {
				val = index;
			}
			index = indxs[0];
		} else {
			val = index + val;
		}
		if (indxs.length == 0) {
			tbl.datagrid('selectRow', rIndex);
		}

		if (window.selectedRow[tbl.attr("id")] != index) {
			window.clickedGrid = tbl.attr("id");
			window.selectedRow[tbl.attr("id")] = index;
		}
		
		var oldVal = tbl.attr('selRows');
		if (oldVal != val) {
			setValue(tbl.attr('id'), val);
			tbl.attr('selRows', val);
			currentSelectedId = id;
		} else {
			var id = tbl.attr('id');
			if (id != null && id != currentSelectedId) {
				currentSelectedId = id;
				var par = {"cmd":"fcs","uid":id};
				post(par);
			}
		}
		
		var toolbar_sel = tbl.parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
		$(toolbar_sel).html((index+1)+"&nbsp;/");
		
		var panel = tbl.datagrid('getPanel').panel('panel');
		panel.attr('tabindex', 0);
		panel.focus();
		focusedTable = tbl.attr('id');
		panel.unbind('keydown').bind('keydown',function(event) {
			if (event.which && event.which == 38) {
		        return selectRow($(this), true);
			} else if (event.which && event.which == 40) {
		        return selectRow($(this), false);
			} else if (event.which && event.which == 37) {
		        return selectColumn($(this), true);
			} else if (event.which && event.which == 39) {
		        return selectColumn($(this), false);
			} else if (event.which && event.which == 32) {
		        return beginEditCell($(this));
			}
		});
	};
	
	var b = endEditing(false, fn);
	if (b) {
		fn();
	}
};

function selectRow(elem, up){
    var t = elem.find('.easyui-datagrid');
	
    if (t.datagrid('getPanel').find('.datagrid-row-editing').length > 0) return true;

	var count = t.datagrid('getRows').length;    // row count
    var selected = t.datagrid('getSelected');
    if (selected){
        var rIndex = t.datagrid('getRowIndex', selected);
        rIndex = rIndex + (up ? -1 : 1);
        if (rIndex < 0 || rIndex >= count) return true;
        t.datagrid('clearSelections');
        t.datagrid('selectRow', rIndex);

        var index = globalIndex(t, rIndex);
        makeSelection(t, index);
    } else {
    	var rIndex = up ? count-1 : 0;
        t.datagrid('selectRow', rIndex);
    	
        var index = globalIndex(t, rIndex);
        makeSelection(t, index);
    }
    return false;
}

function selectColumn(elem, left){
    var t = elem.find('.easyui-datagrid');
    var selected = t.datagrid('getSelected');
	var selCol = t.attr('selectedCol');
    if (selected && selCol) {
        var index = t.datagrid('getRowIndex', selected);
    	var td = t.datagrid('getCell', {index:index, field:selCol});
    	if (td.parent().hasClass('datagrid-row-editing')) return true;
    	var next = left ? td.prev('td') : td.next('td');
    	if (!next || next.length == 0) {
    		return true;
    		//next = left ? td.parent().find('td:last') : td.parent().find('td:first'); 
    	}
    	var field = next.attr('field');
    	t.datagrid('selectCell', {index:index, field:field});
    	t.attr('selectedCol', field);
    	return false;
    }
    return true;
}

function beginEditCell(elem){
    var t = elem.find('.easyui-datagrid');
    var selected = t.datagrid('getSelected');
	var selCol = t.attr('selectedCol');
    if (selected && selCol) {
        var index = t.datagrid('getRowIndex', selected);
    	var td = t.datagrid('getCell', {index:index, field:selCol});
    	if (td.parent().hasClass('datagrid-row-editing')) return true;
		if (endEditing()) {
			var realIndex = globalIndex(t, index);
		
			var id = t.attr("id");
			editingTable = id;
			editIndex[id] = {index: index, field: selCol, realIndex: realIndex};
			t.datagrid('editCell', {index:index,field:selCol});
			return false;
		}
    }
    return true;
}

function makeSelection(t, index) {
	if (window.selectedRow[t.attr("id")] != index) {
		window.clickedGrid = t.attr("id");
		window.selectedRow[t.attr("id")] = index;
		
		setValue(t.attr('id'), index);
		t.attr('selRows', index);
	}
	var selCol = t.attr('selectedCol');
	if (selCol) {
		t.datagrid('selectCell', {index:index,field:selCol});
	}
	var toolbar_sel = $(t).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
	$(toolbar_sel).text((index+1)+" /");
}

$.fn.datebox.defaults.keyHandler.left = function(){};
$.fn.datebox.defaults.keyHandler.right = function(){};

$.fn.treegrid.defaults.onClickRow = function(rIndex, rData) {
	hidePopUp($(this),"#app");
	var tbl = $(this);
	var fn = function() {
		var indxs = tbl.treegrid('getSelectedIndexes');
		var index = rIndex.id;
		var val = '';
		var rowUnselecting = true;
		for (var i=0; i<indxs.length; i++) {
			if (indxs[i] != index) {
				if (ctrlPressed) {
					val += ',' + indxs[i];
				} else {
					tbl.treegrid('unselectRow', indxs[i]);
				}
			} else {
				rowUnselecting = false;
			}
		}
		if (rowUnselecting && indxs.length > 0) {
			if (val.length > 0) val = val.substring(1);
			index = indxs[0];
		} else {
			val = index + val;
		}
		if (indxs.length == 0) {
			tbl.treegrid('selectRow', rIndex.id);
		}

		if (window.selectedRow[tbl.attr("id")] != rIndex) {
			window.clickedGrid = tbl.attr("id");
			window.selectedRow[tbl.attr("id")] = rIndex;
		}
		var oldVal = tbl.attr('selRows');
		if (oldVal != val) {
			setSelectedValue(tbl.attr('id'), val);
			tbl.attr('selRows', val);
		} else {
			var id = tbl.attr('id');
			if (id != null && id != currentSelectedId) {
				currentSelectedId = id;
				var par = {"cmd":"fcs","uid":id};
				post(par);
			}
		}
		
		var panel = tbl.datagrid('getPanel').panel('panel');
		panel.attr('tabindex', 0);
		panel.focus();
		focusedTable = tbl.attr('id');
		panel.unbind('keydown').bind('keydown',function(event) {
			if (event.which && event.which == 38) {
		        return selectRowt($(this), true);
			} else if (event.which && event.which == 40) {
		        return selectRowt($(this), false);
			} else if (event.which && event.which == 37) {
		        return selectColumnt($(this), true);
			} else if (event.which && event.which == 39) {
		        return selectColumnt($(this), false);
			}
		});
	};
	
	var b = endEditing(false, fn);
	if (b) {
		fn();
	}

};

/*  Навигация по treeGrid    */
function selectRowt(elem, up){
    var t = elem.find('.easyui-treegrid');
	
    if (t.treegrid('getPanel').find('.treegrid-row-editing').length > 0) return true;

	var count = t.treegrid('getSelected');    // row count
    var selected = t.treegrid('getSelections');
    if (selected){
        var index = t.treegrid('getLevel', count.id);//t.attr('selrows');
        index = index + (up ? -1 : 1);
        if (index < 0 || index >= count) return true;
        t.treegrid('unselectAll');
        //var tt = t.treegrid('find',selected.id);
        t.treegrid('select', count.id);
        makeSelectiont(t, count.id);
    } else {
    	var index = up ? count-1 : 0;
        t.treegrid('select', index);
        makeSelectiont(t, index);
    }
    return false;
}

function selectColumnt(elem, left){
    var t = elem.find('.easyui-treegrid');
    var selected = t.treegrid('getSelected');
	var selCol = t.attr('selectednode');//t.attr('selrows');
    if (selected && selCol) {
        var index = t.attr('selRows');//t.treegrid('getLevel', selected.id);
    	var td = t.treegrid('getCell', {index:index, field:selCol});
    	if (td.parent().hasClass('treegrid-row-editing')) return true;
    	var next = left ? td.prev('td') : td.next('td');
    	if (!next || next.length == 0) {
    		return true;
    		//next = left ? td.parent().find('td:last') : td.parent().find('td:first'); 
    	}
    	var field = next.attr('field');
    	t.treegrid('selectCell', {index:index, field:field});
    	t.attr('selectednode', field);
    	return false;
    }
    return true;
}

function makeSelectiont(t, index) {
	if (window.selectedRow[t.attr("id")] != index) {
		window.clickedGrid = t.attr("id");
		window.selectedRow[t.attr("id")] = index;
		
		setValue(t.attr('id'), index);
		t.attr('selRows', index);
	}
	var selCol = t.attr('selectednode');
	if (selCol) {
		t.treegrid('selectCell', {index:index,field:selCol});
	}
	var toolbar_sel = $(t).parents(".treegrid").find(".treegrid-toolbar:first .treegrid-sel");
	$(toolbar_sel).text((index+1)+" /");
}

/***************/

$.extend($.fn.datagrid.methods, {
	editCell: function(jq,p){
		return jq.each(function(){
			var doEdit = false;
			var table = $(this);
			var realIndex = globalIndex($(this), p.index);
			var map = {};
			var fields = table.datagrid('getColumnFields',true).concat(table.datagrid('getColumnFields'));
			for(var i=0; i<fields.length; i++){
				var col = $(this).datagrid('getColumnOption', fields[i]);
				if (fields[i] == p.field){
					var id = $(this).attr("id");
					var par = {};
					par["uid"] = id;
					par["row"] = realIndex;
					par["cuid"] = p.field;
					par["cmd"] = "set";
					par["com"] = "getEditor";
					
					saveAjaxRequest('POST', window.mainUrl, par, function(res) {
						if (res.tree) {
							openTree(id, res.title, realIndex, p.field);
						} else if (res.popup) {
							openPopup(id, realIndex, p.field);
						} else if (res.type) {
							col.editor = res;
							doEdit = true;
						}
					}, 'json', false);
				} else {
					map[fields[i]] = col.editor;
					col.editor = null;
				}
			}
			if (doEdit) {
				table.datagrid('beginEdit', p.index);
				var col = $('[field="' + p.field + '"]');
				if (col.length > 0) {
					var ed = col.find('.numberbox .textbox-text');
					if (ed.length > 0) {
						ed.focusEnd();
					} else {
						ed = col.find('.datagrid-editable-input');
						if (ed.length > 0 && ed.attr('type') == 'checkbox') {
			                if (ed.prop("checked")) {
			                    ed.prop("checked", false);
			                } else
			                	ed.prop("checked", true);
			                endEditing();
						} else if (ed.length > 0 && !ed.hasClass('combo-f') && (ed.attr('type') == 'text' || ed.prop("tagName") == 'TEXTAREA')) {
							ed.focusEnd();
						} else {
							ed = col.find('.combo-f');
							if (ed.length > 0) {
								col.find('.textbox-text').focus();
								ed.combo('showPanel');
							}
						}
					}
				}
			} else
                endEditing();
			
			$.each(map, function(field, editor) {
				var col = table.datagrid('getColumnOption', field);
				col.editor = editor;
			});
		});
	},
	selectCell: function(jq, p){
		var panel = jq.datagrid('getPanel');
		$('.datagrid-body td.selected-cell', panel).removeClass('selected-cell');
		var td = $('.datagrid-body tr[datagrid-row-index='+p.index+'] td[field='+p.field+']',panel);
		td.addClass('selected-cell');
	},
	getCell: function(jq, p){
		var panel = jq.datagrid('getPanel');
		var td = $('.datagrid-body tr[datagrid-row-index='+p.index+'] td[field='+p.field+']',panel);
		return td;
	},
	getRow: function(jq, i){
		var panel = jq.datagrid('getPanel');
		var tr = $('.datagrid-body tr[datagrid-row-index='+i+']',panel);
		return tr;
	},
	getSelectedIndexes: function(jq, p){
		var panel = jq.datagrid('getPanel');
		var trs = $('.datagrid-body tr.datagrid-row-selected',panel);
		var indxs = [];
		$.each(trs, function(i, tr) {
			indxs[i] = parseInt($(tr).attr('datagrid-row-index'));
		});
		return indxs;
	}
});

$.extend($.fn.treegrid.methods, {
	editCell: function(jq,param){
		return jq
				.each(function() {
					var doEdit = false;
					var table = $(this);
					var map = {};
					
					var fields = $(this).treegrid('getColumnFields', true)
							.concat($(this).treegrid('getColumnFields'));
					for (var i = 0; i < fields.length; i++) {
						var col = $(this)
								.treegrid('getColumnOption', fields[i]);
						if (fields[i] == param.field) {
							// если это первый столбец, то раскрыть дерево
							if (i == 0) {
								$(this).treegrid( param.state == 'open' ? 'collapse' : 'expand', param.index);
							} else {
								var id = $(this).attr("id");
								var par = {};
								par["uid"] = id;
								par["row"] = param.index;
								par["cuid"] = param.field;
								par["cmd"] = "set";
								par["com"] = "getEditor";

								saveAjaxRequest('POST', window.mainUrl, par, function(res) {
									if (res.tree) {
										openTree(id, res.title,
												param.index, param.field);
									} else if (res.popup) {
										openPopup(id, param.index,
												param.field);
									} else if (res.type) {
										col.editor = res;
										doEdit = true;
									}
								}, 'json', false);
							}
						} else {
							map[fields[i]] = col.editor;
							col.editor = null;
						}

					}
					if (doEdit) {
						$(this).treegrid('beginEdit', param.index);
						var col = $('[field="' + param.field + '"]');
						if (col.length > 0) {
							var ed = col.find('.numberbox .textbox-text');
							if (ed.length > 0) {
								ed.focusEnd();
							} else {
								ed = col.find('.datagrid-editable-input');
								if (ed.length > 0 && ed.attr('type') == 'checkbox') {
					                if (ed.prop("checked")) {
					                    ed.prop("checked", false);
					                } else
					                	ed.prop("checked", true);
					                endEditing();
								} else if (ed.length > 0 && !ed.hasClass('combo-f') && (ed.attr('type') == 'text' || ed.prop("tagName") == 'TEXTAREA')) {
									ed.focusEnd();
								} else {
									ed = col.find('.combo-f');
									if (ed.length > 0) {
										col.find('.textbox-text').focus();
										ed.combo('showPanel');
									}
								}
							}
						}
					} else
		                endEditing();
					
					$.each(map, function(field, editor) {
						var col = table.treegrid('getColumnOption', field);
						col.editor = editor;
					});
				});
	},
	getSelectedIndexes: function(jq, p){
		var panel = jq.treegrid('getPanel');
		var trs = $('.datagrid-view2 tr.datagrid-row-selected',panel);
		var indxs = [];
		$.each(trs, function(i, tr) {
			indxs[i] = $(tr).attr('node-id');
		});
		return indxs;
	},
	getCell: function(jq, p){
		var panel = jq.datagrid('getPanel');
		var td = $('.datagrid-body tr[node-id='+p.index+'] td[field='+p.field+']',panel);
		return td;
	},
	selectCell: function(jq, p){
		var panel = jq.datagrid('getPanel');
		$('.datagrid-body td.selected-cell', panel).removeClass('selected-cell');
		var td = $('.datagrid-body tr[node-id='+p.index+'] td[field='+p.field+']',panel);
		td.addClass('selected-cell');
		var par = {};
		par["uid"] = $(jq).attr('id');
		par["com"] = 'sct';
		par["cuid"] = p.field;
		par["cmd"] = "set";
		loadData(par, true);
	}
});

var editIndex = {};
var editingTable = undefined;
var focusedTable = undefined;

function getEditing() {
	if (editingTable == undefined) return null;
	
	var id = editingTable;
	if (editIndex[id] == undefined) return null;

	var edIndex = editIndex[id];
	
	return {id:id,edIndex:edIndex};
}

function restoreEditing(pars) {
	var t = $("#" + pars.id);

	if (t.length > 0) {
		var ed = t.datagrid('getEditor', {index:pars.edIndex.index,field:pars.edIndex.field});

		if (ed == null || ed.target == null) {
			t.datagrid('editCell', {index:pars.edIndex.index,field:pars.edIndex.field});
		} 
	}
}

function endEditing(async, fn) {
	if (editingTable == undefined) return true;
	
	var id = editingTable;
	if (editIndex[id] == undefined) return true;

	var edIndex = editIndex[id];
	
	editIndex[id] = undefined;
	editingTable = undefined;

	var t = $("#" + id);

	if (t.length > 0) {
		var ed = t.datagrid('getEditor', {index:edIndex.index,field:edIndex.field});
		var val = "";
		if (ed && ed.type == 'combobox') {
			var title = $(ed.target).combobox('getText');
			if (t.datagrid('getRows').length > 0)
				t.datagrid('getRows')[edIndex.index][edIndex.field + '-title'] = title;
			else
				t.datagrid('getSelected')[edIndex.field + '-title'] = title;
			val = $(ed.target).combobox('getValue');
		} else if (ed && ed.type == 'datebox') {
			val = $(ed.target).datebox('getValue');
		} else if (ed && ed.type == 'checkbox') {
			val = $(ed.target).get(0).checked;
		} else if (ed && ed.type == 'or3checkbox') {
			val = $(ed.target).get(0).checked;
		} else if (ed && ed.type == 'numberbox') {
			val = $(ed.target).next().children().first().val();
		} else if (ed) {
			val = $(ed.target).val();
		}
		
		if (ed && ed.target) {	
		    showChangeMsg();
			var par = {};
			par["uid"] = id;
			par["val"] = val;
			par["row"] = edIndex.realIndex;
			par["cuid"] = edIndex.field;
			par["cmd"] = "set";

			var count = t.datagrid('getRows').length;
		    var selected = t.datagrid('getSelected');
		    var selRow = 0;
		    if (selected) {
		    	selRow = t.datagrid('getRowIndex', selected);
		    }
			t.datagrid('endEdit', edIndex.index);
		    if (selRow >= 0 && selRow < count) {
		        t.datagrid('selectRow', selRow);
		    }
			loadData(par, true, fn);//(async == null || async == undefined) ? true : async);
			return false;
		} 
	}
	return true;
}

$.fn.datagrid.defaults.onDblClickCell = function(index, field) {
	var id = $(this).attr("id");
	var opt = $(this).datagrid('getColumnOption', field);
	var realIndex = globalIndex($(this), index);
	var doTextareaColumn = false;
	var isShowTextAsXML = false; 
	if(opt && opt.editor && opt.editor == 'textarea') {
		var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
		for(var i=0; i<fields.length; i++) {
			if (fields[i] == field) {
				var par = {};
				par["uid"] = id;
				par["row"] = realIndex;
				par["cuid"] = field;
				par["cmd"] = "set";
				par["com"] = "getEditor";
				
				saveAjaxRequest('POST', window.mainUrl, par, function(res) {
					if (res.type) {
						doTextareaColumn = false;
					} else {
						doTextareaColumn = true;
					}
					if (res.showTextAsXML) {
						isShowTextAsXML = res.showTextAsXML == "1";
					}
				}, 'json', false);
			}
		}
		if(doTextareaColumn) {
			var text = $('.datagrid-body tr[datagrid-row-index='+index+'] td[field='+field+']').children().text();
			if(text != "") {
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
				
				var buttons = [buttonOk];
					
				var oldZindex = $.fn.window.defaults.zIndex;
				$.fn.window.defaults.zIndex = dialogZindex++;
	
				$('#' + dialogId).dialog({
					title: opt.title,
					width: 600,
					height: 400,
					closed: true,
					cache: false,
					closable: true,
					modal: true,
					onLoad: function() {
						$('#' + dialogId).parent().find('.panel-title').css({'height' : 'auto'});
					},
					onClose: function() {
						window.dialogResult[dialogId] = '1';
						$("#" + dialogId).dialog('destroy');
					},
					onBeforeDestroy : function() {
			        	$.fn.window.defaults.zIndex = oldZindex;
					},
					buttons: buttons
				});
				if (isShowTextAsXML) {
					$('#' + dialogId).text(text);
				} else {
					$('#' + dialogId).html(text);
				}
				$('#' + dialogId).dialog("open");
				window.popupcount++;
			}
		}
	}
	if (opt && (opt.editor == null || opt.editor.type != 'checkbox')) {
		var tbl = $(this);
		var fn = function() {
			
			editingTable = id;
			editIndex[id] = {index: index, field: field, realIndex: realIndex};
			$('#' + id).datagrid('editCell', {index:index,field:field});
		};
		
		var b = endEditing(false, fn);
		if (b) {
			fn();
		}
	}
};

$.fn.datagrid.defaults.onClickCell = function(index, field) {
	var realIndex = globalIndex($(this), index);
	$(this).attr('selectedCol', field);
	$(this).datagrid('selectCell', {index:realIndex,field:field});
	var id = $(this).attr("id");
	var opt = $(this).datagrid('getColumnOption', field);
	if (opt && opt.editor && opt.editor.type == 'checkbox') {
		var fn = function() {
			editingTable = id;
			editIndex[id] = {index: index, field: field, realIndex: realIndex};
			$('#' + id).datagrid('editCell', {index:index,field:field});
		};
		
		var b = endEditing(false, fn);
		if (b) {
			fn();
		}
	}
};

$.fn.validatebox.defaults.novalidate = true;
$.extend($.fn.validatebox.methods, {
	validate:function(jq){
		return jq.each(function(){
		}); 
	}
});

$.fn.combobox.defaults.keyHandler.enter = function() {
	$(this).combobox('hidePanel');
	return false;
};

$("body").on('keydown', 'input.textbox-text',function(e) {
	var combobox = $(this).parent().prev().hasClass('easyui-combobox');
	if (combobox){
		if (e.keyCode == 40){
			$(this).parent().prev().combobox('showPanel');
		}
	}
});

$("body").on('keypress', 'span.numberbox input.textbox-text', function(e) {
	var maxlength = $(this).parent().prev().attr('maxlength');
	if (maxlength) {
		if (isCharacterKeyPress(e)) {
			var length = $(this).val().split(',')[0].length;
			if (length >= parseInt(maxlength)) {
				return false;
			}
		}
	}
	return true;
});

$("body").on('change', ':checkbox[offval]', function() {
	endEditing();
});

$("body").on('change', '.datagrid-editable-input:checkbox', function() {
	endEditing();
});

$.fn.treegrid.defaults.onDblClickCell = function(field, props){
	var tbl = $(this);
	var id = tbl.attr("id");
	var fn = function() {
		editingTable = id;
		editIndex[id] = {index: props.id, field: field, realIndex: props.id};
		tbl.treegrid('editCell', {index:props.id,field:field,state:props.state});
	};
	
	var b = endEditing(false, fn);
	if (b) {
		fn();
	}
};

$.fn.treegrid.defaults.onClickCell = function(field, props) {
	$(this).attr('selectedNode',  field);
	$(this).treegrid('selectCell',{index:props.id, field:field});
};

$("body").on('click', 'button.or3-note, a.or3-note', function() {
	uid = $(this).attr("id");
	window.open(window.mainUrl + "&cmd=hlp&uid=" + uid);
	return false;
});

$("body").on('click', 'button.popup', function() {
	if (!$(this).hasClass('btn-disabled')) {
		uid = $(this).attr("id");
		$('.menu-content,.menu-shadow').hide();
		openPopup(uid);
	}
});

$("body").on('click', 'a.popup', function() {
	if (!$(this).hasClass('btn-disabled')) {
		uid = $(this).attr("id");
		$('.menu-content,.menu-shadow').hide();
		openPopup(uid);
	}
});

$("body").on('click', 'a.hyper', function() {
	if (!$(this).hasClass('btn-disabled')) {
		uid = $(this).attr("id");
		blockPage();
		document.location.hash = "cmd=openIfc&uid=" + uid + "&rnd=" + rnd();
	}
	return false;
});

$("#tasksList").on('change', '.task_check', function() {
	if($(this).is(":checked")){
		$(this).closest('tr').addClass('taskCheckedTr');
    } else {
    	$(this).closest('tr').removeClass('taskCheckedTr');   	
    }
	if($('.task_check:visible').is(':checked')){
    	$('#taskremoveprocess').show();
    	$('#taskremoveprocess').find('.l-btn-text').css('color','#fff');
    } else {
    	$('#taskremoveprocess').hide();
    }
   if($('.task_check:visible:not(:checked)').length==0){
        $('#task_checkbox').prop('checked',true);
    }else{
        $('#task_checkbox').prop('checked',false);         
    }
   if ($(".task_check:visible:checked").length == 1) {
		$('#debugger').show();
	} else {
		$('#debugger').hide();
	}
});

$("#tasksList").on('change', '#task_checkbox', function() {
    if($("#task_checkbox").is(":checked")){
        $('.task_check:visible').prop('checked', true);
        $('#taskremoveprocess').show();
        $('#taskremoveprocess').find('.l-btn-text').css('color','#fff');
        $('.task_check:visible').closest('tr').addClass('taskCheckedTr');
    } else {
        $('.task_check:visible').prop('checked', false);
        $('#taskremoveprocess').hide();
        $('.task_check:visible').closest('tr').removeClass('taskCheckedTr');
    }
});

$("#tasksList").on('click', '#usedmemory', function() {
	$.get(window.mainUrl + "&cmd=getUsedMemory", function(data) {
		if(checkData(data))
			$('#usedmemorylabel').text(data.message);
	}, 'json');
});
$("#tasksList").on('click', '#taskremoveprocess', function() {
	var l = $(".task_check:visible:checked").length;
	if (l > 0) {
		var msg = (l == 1) ? translation['removeProcess'] : (translation['removeProcess2']+' ('+l+')'+'?');
		$.messager.confirm('', msg, function(e) {
			if (e) {
				deleteSelectedTasks();
			}
		});
	}
});

var deletingTasks = false;
function deleteSelectedTasks() {
	deletingTasks = true;
	blockPage(translation['deleting']);
	$(".task_check:visible:checked").each(function(index) {
		var par = {};
		par["id"] = $(this).attr('task');
		par["cmd"] = "kill";
		post(par);
	});
	$('#taskremoveprocess').hide();
}

var deletingOrders = false;
function deleteSelectedOrders(rbtn) {
	deletingOrders = true;
	blockPage(translation['deleting']);
	$(".my_check:visible:checked").each(function(index) {
		var par = {};
		par["uid"] = $(this).attr('task');
		par["obj"] = $(this).attr('obj');
		par["key"] = $(this).attr('key');
		par["cmd"] = "kill";
		post(par);
	});
	rbtn.hide();
}

$("#oldStartDiv").on('change', '.my_check', function() {
	if($(this).is(":checked")){
		$(this).closest('tr').addClass('myCheckedTr');
    } else {
    	$(this).closest('tr').removeClass('myCheckedTr');   	
    }
	if($('.my_check:visible').is(':checked')){
    	$('#old_removeprocess').show();
    } else {
    	$('#old_removeprocess').hide();
    }
   if($('.my_check:visible:not(:checked)').length==0){
        $('#old_checkbox').prop('checked',true);
    }else{
        $('#old_checkbox').prop('checked',false);         
    }
});

$("#oldStartDiv").on('change', '#old_checkbox', function() {
    if($("#old_checkbox").is(":checked")){
        $('.my_check:visible').prop('checked', true);
        $('#old_removeprocess').show();
        $('.my_check:visible').closest('tr').addClass('myCheckedTr');
    } else {
        $('.my_check:visible').prop('checked', false);
        $('#old_removeprocess').hide();
        $('.my_check:visible').closest('tr').removeClass('myCheckedTr');
    }
});
$("#startDiv").on('change', '.my_check', function() {
	if($(this).is(":checked")){
		$(this).closest('tr').addClass('myCheckedTr');
    } else {
    	$(this).closest('tr').removeClass('myCheckedTr');   	
    }
	if($('.my_check:visible').is(':checked')){
    	$('#removeprocess').show();
    } else {
    	$('#removeprocess').hide();
    }
   if($('.my_check:visible:not(:checked)').length==0){
        $('#main_checkbox').prop('checked',true);
    }else{
        $('#main_checkbox').prop('checked',false);         
    }
});

$("#startDiv").on('change', '#main_checkbox', function() {
    if($("#main_checkbox").is(":checked")){
        $('.my_check:visible').prop('checked', true);
        $('#removeprocess').show();
        $('.my_check:visible').closest('tr').addClass('myCheckedTr');
    } else {
        $('.my_check:visible').prop('checked', false);
        $('#removeprocess').hide();
        $('.my_check:visible').closest('tr').removeClass('myCheckedTr');
    }
});

$("#startDiv").on('click', '#removeprocess', function() {
	removeOrder('#removeprocess');
});
$("#oldStartDiv").on('click', '#old_removeprocess', function() {
	removeOrder('#old_removeprocess');
	
});
$("#oldStartDiv").on('click', '#old_return', function() {
		var par = {};
		par["cmd"] = "oldFlowPanRemove";
		par["json"] = 1;
		post(par, function(data) {
			window.location.href = "index.jsp?guid=" + guid + "&rnd=" + rnd();
		});
});

function removeOrder(rbtn) {
	var l = $(".my_check:visible:checked").length;
	if (l > 0) {
		var msg = (l == 1) ? translation['removeProcess'] : (translation['removeProcess2']+' ('+l+')'+'?');
		$.messager.confirm('', msg, function(e) {
			if (e) {
				deleteSelectedOrders($(rbtn));
			}
		});
	}
};

$("#sessionsList").on('click', '.icon-cancel', function() {
	var id = $(this).attr('uuid');
	$.messager.confirm('',"Удалить сессию пользователя?", function(e){
		if (e) {
			var par = {};
			par["id"] = id
			par["cmd"] = "killSession";
			post(par);
		}
	});
});

$("#startDiv").on('change', '#sortprocinout', function() {
	parseInt($('#sortprocinout').val()) == 1 ? dateOrders = true : dateOrders = false ;
    loadFirstTime[0] = true;
    loadFirstTime[1] = true;
    loadOrdersIn();
    loadOrdersOut();
});

$("#startDiv").on('click', '#ordersList_in_fire_count', function() {
	$('#ordersList_in_fire_count').show();
	$("#sortprocinout option[value='1']").prop('selected', true);
	dateOrders = true;
    loadFirstTime[0] = true;
    loadFirstTime[1] = true;
    loadOrdersIn();
    loadOrdersOut();
});

$("#oldStartDiv").on('change', '#sortoldproc', function() {
	parseInt($('#sortoldproc').val()) == 1 ? dateOrders = true : dateOrders = false ;
    loadFirstTime[3] = true;
    loadOrdersOld();
});
$("body").on('change', '.utip input', function() {
	
	toolTipN = parseInt($(this).val());
	if(toolTipN == 1)
		onTool = true;
	else
		onTool = false;
	
});

$("body").on('change', '.ubread input', function() {
	
	breadCrumps = parseInt($(this).val());
	if(breadCrumps == 1)
		breadcrumpsOn = true;
	else
		breadcrumpsOn = false;
	
});

$("body").on('change', '.utheme input', function() {
	blockPage();
	theme = parseInt($(this).val());
	
	var par = {"setTheme":theme};
	
	saveAjaxRequest('POST', window.mainUrl, par, go, 'html', false);
});

function go() {
	var url = "index.jsp?guid=" + guid + "&rnd=" + rnd() + "#ui=profileWnd&mode=tabs";
	window.location.href = url;
}

function changeInterfaceLang(code, e) {
	e.preventDefault();

	blockPage();
	var par = {"setLang":code};

	saveAjaxRequest('POST', window.mainUrl, par, go, 'html', false);
}
function clickPopUpContent(parentId){
	$('.popUpPan:not(.asMenu)').click(function(e) {
	    var popID = $(this).attr('rel'); 
	    var content = $(popID);
		var ppopID = $("#"+parentId).find(popID);
	    //Проверяем, пришло событие от компанента на котором оно вызвано или нет
		if(ppopID.length>0 || $(this).attr('sub')){
			//Если второй щелчек по кнопке, панель которой видима, то необходимо ее скрыть
		    if($(content).is(":visible")){
				// отправить на сервер событие закрытия панели Before
				var par = {};
				par["uid"] = $(content).attr('id').substring(3); // первые три символа это префикс "pop"
				par["cmd"] = "hidePopUp";
				par["t"] = "b";
				postAndParseData(par);
				
				$(content).fadeOut();
				// отправить на сервер событие закрытия панели After
				par["t"] = "a";
				postAndParseData(par);
		    }else{
			    // отправить на сервер событие открытия панели Before
				var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"b"};
				postAndParseData(par);
		
		    	var left = $(this).attr('sub') ? $(this).width() : $(this).offset().left;
		    	var top = $(this).attr('sub') ? -$(this).height() : $(this).offset().top + $(this).height() + 2;
		    	
		    	$(content).css('left', left);
		    	$(content).css('top', top);
				// скрыть предыдущие панели
				var parent = $(this).parent();
				while (!$(parent).hasClass('popUpPanContent') && $(parent).attr('id') != parentId && !$(parent).hasClass('ui-toolbar')) {
					parent = $(parent).parent();
				}
				var panels = $(parent).find(".popUpPanContent");
				for (var j = 0; j < panels.length; j++) {
					if($(panels[j]).attr('id') != popID && $(panels[j]).is(":visible")){
						// отправить на сервер событие закрытия панели Before
						var par = {};
						par["uid"] = $(panels[j]).attr('id').substring(3); // первые три символа это префикс "pop"
						par["cmd"] = "hidePopUp";
						par["t"] = "b";
						postAndParseData(par);
						
						$(panels[j]).fadeOut();
						// отправить на сервер событие закрытия панели After
						par["t"] = "a";
						postAndParseData(par);
					}
				} 
			    // показать панель
			    $(content).fadeIn();
			    // отправить на сервер событие открытия панели After
				var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"a"};
				postAndParseData(par);
		    }
		}
	    // если панель является подпанелью, то запретить всплытие события
    	return $(this).attr('sub') ? false : true;
	});
}

function showPopUpContent(){
	//console.log('showPopUpContent');
	$('.popUpPan.asMenu:not(.hover)').hover(
		function(e) {
			showPopupPanel($(this));
		},
		function(e) {
			hidePopupPanel($(this));
		}
	);
}

function showPopupPanel(popup) {
	var popID = popup.attr('rel'); 
	var content = $(popID);
	if(!popup.hasClass('hover')){
		popup.addClass('hover');
	}
	//удалить класс если он есть(косяки при открытии)
	if(popup.hasClass('btn-disabled')){
		popup.removeClass('btn-disabled');
	}
	//добавить панель к обработчику события
	popup.append($(content));
	// отправить на сервер событие открытия панели Before
	var par = {"cmd":"showPopUp","uid":popup.attr('id'),"t":"b"};
	postAndParseData(par);

	var left = popup.attr('sub') ? popup.width() : popup.offset().left;
	var top = popup.attr('sub') ? -popup.height() : popup.offset().top + popup.height() + 2;
	
	$(content).css('left', left);
	$(content).css('top', top);
	// показать панель
	$(content).fadeIn(500);
	// отправить на сервер событие открытия панели After
	var par = {"cmd":"showPopUp","uid":popup.attr('id'),"t":"a"};
	postAndParseData(par);
}

function hidePopupPanel(popup) {
	var popID = popup.attr('rel'); 
	var content = $(popID);
    // отправить на сервер событие скрыть панель Before
	var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"b"};
	postAndParseData(par);
    // отправить на сервер событие скрыть панель After
	var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"a"};
	postAndParseData(par);
    // скрыть панель
	$(content).fadeOut(100);
}

function openPopup(uid, row, colUid) {
	blockPage();
	var par = {};
	par["uid"] = uid;
	par["cmd"] = "openPopup";
	if (row != undefined) par["row"] = row;
	if (colUid != undefined) par["cuid"] = colUid;
	
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.result == 'nop') {
			// no operation
			$('body').unblock();
		} else if (data.result == 'error') {
			alertError(data.message);
			$('body').unblock();
		} else {
			blockPage();
			window.selectedRow = [];
			var did = window.popupcount;
			var dialogId = 'or3_popup' + did;
			
			$('#trash').append($("<div></div>").attr('id', dialogId));
			
			window.dialogResult[dialogId] = '1';
			$('#' + dialogId).on('click', function (e) {
				// источник события
				var target = e && e.target || event.srcElement;
				return hidePopUp(target,'#' + dialogId);
			});
			
			var w = data.w > $(window).width() ? $(window).width() : data.w;
			var h = data.h > ($(window).height() - 70) ? ($(window).height() - 70) : data.h;

			var buttons = (data.tv==0) ? [{
				id: "okPopupBtn",
				text: translation['ok'],
				handler: function() {
					setDialogBtnsEnabled(dialogId, false);
					sendUserDecision("0");
					closePopup(dialogId);
				}
			},
			{
				id: "cancelPopupBtn",
				text: translation['cancel'],
				handler: function() {
					setDialogBtnsEnabled(dialogId, false);
					sendUserDecision("1");
					window.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				}
			}] : [];
			
			var toolbar = [{
				id: 'reportBtn' + did,
				text: translation['print'],
				iconCls: 'icon-rept'
			}];
			
			var dialogOptions = {
				title: data.t,
				width: w,
				height: h,
				closed: false,
				cache: false,
				href: window.mainUrl + '&cmd=loadPopup',
				modal: true,
				uid: uid,
				row: row,
				cuid: colUid,
				onOpen: function() {
					$('#' + dialogId +' .panel').append($("<div class=\"glassDialog\"></div>"));
					$('#' + dialogId + ' .glassDialog').height($('#' + dialogId +' .panel').height()).width($('#' + dialogId +' .panel').width());
				},
				onLoad: function() {
					// Добавляем ID-окна в массив окон
					window.openedDialogs.push(dialogId);
					loadWindowTitle($('#' + dialogId));
					loadDialogReports(did);
					
					$('.easyui-datebox, .easyui-datetimebox').each(function(i) {
						var db = $(this);
						var hidePanel = db.attr('hidePanel') == 'true';
						if (hidePanel) {
							var opts = $(this).datebox("panel").panel("options");
							opts.nopanel = true;
							
							var fld = $(this).parent().find('.textbox-text');
							var cal = $(this).parent().find('.textbox-addon');
							
							if (cal.css('display') != "none") {
								cal.hide();
								fld.width(fld.width() + 18);
							}
						}
					});

					loadData({}, true);
					preparefileUpload();
					clickPopUpContent(dialogId);
					showPopUpContent();
				},
				onClose: function() {
					window.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				},
				onBeforeDestroy : function() {
					// Удаляем ID-окна из массива окон
					window.openedDialogs.pop();
					
					if (window.dialogResult[dialogId] == '1') {
						opts = $('#' + dialogId).dialog('options');
						var par = {};
			        	par["uid"] = opts.uid ;
			        	par["val"] = '1';
			        	if (opts.row != undefined) par["row"] = opts.row;
			        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
			        	par["cmd"] = "closePopup";
    		        	loadData(par, true);
					}
				},
				buttons: buttons
			};
			dialogOptions.toolbar = toolbar;
			
			$('#' + dialogId).dialog(dialogOptions);

			window.popupcount++;
		}
	}, 'json');
}

function sendUserDecision(decision) {
	var url = window.mainUrl;
	var par = {};
	par["cmd"] = 'userDecision';
	par["value"] = decision;
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url + "&rnd=" + rnd(),
		data : par,
		success : function(content) {},
		dataType : 'json',
		async : false
	});
}

function destroyPopup(count) {
	var popups = $("div[id^='or3_popup']");
	for (var i=1; i<=count; i++) {
		var popup = $(popups.get(popups.size() - i));
		window.dialogResult[popup.attr('id')] = '-1';
		popup.dialog('destroy');
	}
}

function closePopup(did) {
    showChangeMsg();

	window.dialogResult[did] = '0';
	var opts = $('#' + did).dialog('options');
    
    var par = {};
	par["uid"] = opts.uid ;
	par["val"] = 'CHECK';
	if (opts.row != undefined) par["row"] = opts.row;
	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
	par["cmd"] = "closePopup";

	loadData(par, true, function(data) {
		if (!data.result || data.result == 'success') {
			$("#" + did).dialog('destroy');
		} else {
			showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
			setDialogBtnsEnabled(did, true);
		}
	});
}

function openTree(uid, title, row, colUid) {
	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	$('#' + dialogId).dialog({
		title: '<div style="font-size:14px; color:#777;">'+title+'</div>',
		width: 800,
		height: 600,
		closed: false,
		cache: false,
		href: window.mainUrl + '&cmd=openTree&uid=' + uid + (row != undefined ? '&row=' + row + "&cuid=" + colUid : ""),
		modal: true,
		uid: uid,
		row: row,
		cuid: colUid,
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
			opts = $(this).dialog('options');
        	if (window.dialogResult[dialogId] == 0) {
                var par = {};
        		par["cmd"] = "set";
            	par["uid"] = opts.uid;
            	if (opts.row != undefined) par["row"] = opts.row;
            	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
    			par["val"] = -1;
        		var selNode = $(this).find('.easyui-tree').tree('getSelected');
        		if (selNode) {
        			par["val"] = selNode.id;
        		}
        		loadData(par, true);
        	}
		},
		buttons: [{
			text: translation['ok'],
			handler: function() {
				window.dialogResult[dialogId] = '0';
                showChangeMsg();
				$("#" + dialogId).dialog('destroy');
			}
		},
		{
			text: translation['cancel'],
			handler: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		}]
	});
	
	window.popupcount++;
}

function changePwdDialog(obligatory) {
	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	var buttonOk = {
		text: translation['change'],
		handler: function() {
			window.dialogResult[dialogId] = '0';
			var par = {};
    		par["cmd"] = "changePass";
        	par["oldPass"] = $('#' + dialogId).find('[uid="oldPass"]').val();
        	par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
        	par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
        	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
        		checkData(data);
        		if (data.result == 'error') {
        			alertError(data.message);
        		} else {
        			$("#" + dialogId).dialog('destroy');
        			alert(data.message);
        		}
        	}, 'json');

		}
	};
	
	var buttonCancel = {
		text: translation['cancel'],
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	
	var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
	
	$('#' + dialogId).dialog({
		title: translation['passChange'],
		width: 300,
		height: 200,
		closed: false,
		cache: false,
		closable: obligatory != true,
		href: window.contextName + '/jsp/pwd.jsp?guid=' + guid + '&rnd=' + rnd(),
		modal: true,
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function askPassword(doAfterFunc, title) {
	if (title == null) title = translation['enterPassword'];
	
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
	
	var buttons = [buttonOk];
		
	var oldZindex = $.fn.window.defaults.zIndex;
	$.fn.window.defaults.zIndex = dialogZindex++;

	$('#' + dialogId).dialog({
		title: title,
		width: 360,
		height: 200,
		closed: false,
		cache: false,
		closable: true,
		href: window.contextName + '/jsp/enterPassword.jsp?guid=' + guid,
		modal: true,
		onLoad: function() {
			$('#' + dialogId).parent().find('.panel-title').css({'height' : 'auto'});
		},
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
			var pass = null;
			if (window.dialogResult[dialogId] == '0') {
        		pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
			}
        	$.fn.window.defaults.zIndex = oldZindex;
        	
        	doAfterFunc(pass);
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

/*
function saveUcgoCertificate(cert, reader, uid, tokPD) {
	var appl = getApplet('UCGOApplet');
	
	var par = {};
	par["ucgocertres"] = "";

	if (appletLoading) {
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			saveUcgoCertificate(cert, reader, uid, tokPD);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('UCGOApplet');

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
			href: window.contextName + '/jsp/enterPassword.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0') {
					tokPD = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					
	        		try {
		        		var signed = appl.saveCertificate(cert, reader, "gost", uid, tokPD);
		        		
						if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
							alert(signed.substring(8, signed.length - 1));
						} else {
							par["ucgocertres"] = signed;
						}
	        		} catch (e) {
	        			alert(appletCallFailMsg);
	        		}
				}
	        	post(par);
	        	$.fn.window.defaults.zIndex = oldZindex;
			},
			buttons: buttons
		});
		
		window.popupcount++;
	} else {
		try {
    		var signed = appl.saveCertificate(cert, reader, "gost", uid, tokPD);
			par["ucgocertres"] = signed;
		} catch (e) {
			alert(appletCallFailMsg);
		}
    	post(par);
	}
}

function haveUcgoCertificate(reader, uid, tokPD) {
	var appl = getApplet('UCGOApplet');
	
	var par = {};
	par["ucgohaveres"] = "";

	if (appletLoading) { 
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			haveUcgoCertificate(reader, uid, tokPD);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('UCGOApplet');

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
			href: window.contextName + '/jsp/enterPassword.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0') {
					tokPD = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					
	        		try {
		        		var signed = appl.haveCertificate(reader, "gost", uid, tokPD);
		        		
						if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
							alert(signed.substring(8, signed.length - 1));
						} else {
							par["ucgohaveres"] = signed;
						}
	        		} catch (e) {
	        			alert(appletCallFailMsg);
	        		}
				}
	        	post(par);
	        	$.fn.window.defaults.zIndex = oldZindex;
			},
			buttons: buttons
		});
		
		window.popupcount++;
	} else {
		try {
    		var signed = appl.haveCertificate(reader, "gost", uid, tokPD);
			par["ucgohaveres"] = signed;
		} catch (e) {
			alert(appletCallFailMsg);
		}
		post(par);
	}
}

function generateUcgoPKCS10(dn) {
	var appl = getApplet('UCGOApplet');

	var par = {};
	par["ucgogenres"] = "";
	
	if (appletLoading) { 
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			generateUcgoPKCS10(dn);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('UCGOApplet');

	var readers = "";
	try {
		readers = appl.getReaders();
	} catch (e) {
		post(par);
		alert(appletCallFailMsg);
		return;
	}

	if (readers == "") {
		post(par);
		alert("Вставьте ключевой контейнер!");
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
			href: window.contextName + '/jsp/selectKeyPairConteiner.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0') {
					var reader = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
					
					try {
		        		var uid = appl.generateName();
		        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
						
		        		var signed = appl.createPkcs10(reader, "gost", uid, dn, pass);
		
						if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
							alert(signed.substring(8, signed.length - 1));
						} else {
							par["ucgogenres"] = signed;
						}
	        		} catch (e) {
	        			alert(appletCallFailMsg);
	        		}
				}
	        	post(par);
	        	$.fn.window.defaults.zIndex = oldZindex;
			},
			onLoad: function() {
				var readersArr = readers.split(appl.ROW_SEPARATOR);
				var options = '';
				for (var ind = 0; ind < readersArr.length; ind++) {
					var info  = readersArr[ind].split(appl.COLUMN_SEPARATOR);
					options += '<option value="' + info[0] + '">' + info[1] + '</option>';
				}
				$('#' + dialogId).find('[uid="ucgoConteinerType"]').html(options);
			},
			buttons: buttons
		});
		
		window.popupcount++;
	}
}

function selectUcgoCertificate(iin) {
	var appl = getApplet('UCGOApplet');
	
	var par = {};
	par["ucgoselectcertres"] = "";

	if (appletLoading) {
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			selectUcgoCertificate(iin);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('UCGOApplet');

	var readers = "";
	try {
		readers = appl.getReaders();
	} catch (e) {
		post(par);
		alert(appletCallFailMsg);
		return;
	}
	
	if (readers == "") {
		post(par);
		alert("Вставьте ключевой контейнер!");
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
			href: window.contextName + '/jsp/selectKeyPairConteiner.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
	        	$.fn.window.defaults.zIndex = oldZindex;
	
				if (window.dialogResult[dialogId] == '0') {
					var reader = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
	        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();
					
	        		try {
		        		var certs = iin == '0' 
		        			? appl.getCertificates(reader, "gost", pass)
		        			: appl.getCertificates(reader, "gost", pass, iin);
		
						if (certs.length > 7 && "[ERROR: " == certs.substring(0, 8)) {
				        	post(par);
							alert(certs.substring(8, certs.length - 1));
						} else {
							selectUcgoCertificateStep2(certs);
						}
	        		} catch (e) {
			        	post(par);
	        			alert(appletCallFailMsg);
	        		}
				} else {
		        	post(par);
				}
			},
			onLoad: function() {
				var readersArr = readers.split(appl.ROW_SEPARATOR);
				var options = '';
				for (var ind = 0; ind < readersArr.length; ind++) {
					var info  = readersArr[ind].split(appl.COLUMN_SEPARATOR);
					options += '<option value="' + info[0] + '">' + info[1] + '</option>';
				}
				$('#' + dialogId).find('[uid="ucgoConteinerType"]').html(options);
			},
			buttons: buttons
		});
		
		window.popupcount++;
	}
}

function selectUcgoCertificateStep2(certs) {
	var appl = getApplet('UCGOApplet');
	var certsArr = certs.split(appl.ROW_SEPARATOR);
	var par = {};
	par["ucgoselectcertres"] = "";

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
		href: window.contextName + '/jsp/selectCert.jsp?guid=' + guid,
		modal: true,
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
        	$.fn.window.defaults.zIndex = oldZindex;

			if (window.dialogResult[dialogId] == '0') {
				var certInd = $('#' + dialogId).find('[uid="ucgoCert"]').val();
				var info  = certsArr[parseInt(certInd)].split(appl.COLUMN_SEPARATOR);
				var subject = info[5];
				
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

				par["ucgoselectcertres"] = info[3] + appl.COLUMN_SEPARATOR + subject + appl.COLUMN_SEPARATOR +
								iin + appl.COLUMN_SEPARATOR + bin + appl.COLUMN_SEPARATOR + email +
								appl.COLUMN_SEPARATOR + info[0];

			}
			post(par);
		},
		onLoad: function() {
			var options = '';
			for (var ind = 0; ind < certsArr.length; ind++) {
                var info = certsArr[ind].split(appl.COLUMN_SEPARATOR);
                if (info[3]) {
                	options += '<option value="' + ind + '">' + info[6] + '</option>';
                }
			}
			$('#' + dialogId).find('[uid="ucgoCert"]').html(options);
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function deleteUcgoCertificate(keyName) {
	var appl = getApplet('UCGOApplet');
	
	var par = {};
	par["ucgodeletecertres"] = "";

	if (appletLoading) { 
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			deleteUcgoCertificate(keyName);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('UCGOApplet');

	var readers = "";
	try {
		readers = appl.getReaders();
	} catch (e) {
		post(par);
		alert(appletCallFailMsg);
		return;
	}
	
	if (readers == "") {
		post(par);
		alert("Вставьте ключевой контейнер!");
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
			href: window.contextName + '/jsp/selectKeyPairConteiner.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
	        	$.fn.window.defaults.zIndex = oldZindex;
	
				if (window.dialogResult[dialogId] == '0') {
					var reader = $('#' + dialogId).find('[uid="ucgoConteinerType"]').val();
	        		var pass = $('#' + dialogId).find('[uid="ucgoConteinerPass"]').val();

	        		try {
		        		var res = appl.deleteCertificate(reader, "gost", pass, keyName);
		
						if (res.length > 7 && "[ERROR: " == res.substring(0, 8)) {
							alert(res.substring(8, res.length - 1));
						} else {
							par["ucgodeletecertres"] = "OK";
						}
	        		} catch (e) {
	        			alert(appletCallFailMsg);
	        		}
				}
	        	post(par);
			},
			onLoad: function() {
				var readersArr = readers.split(appl.ROW_SEPARATOR);
				var options = '';
				for (var ind = 0; ind < readersArr.length; ind++) {
					var info  = readersArr[ind].split(appl.COLUMN_SEPARATOR);
					options += '<option value="' + info[0] + '">' + info[1] + '</option>';
				}
				$('#' + dialogId).find('[uid="ucgoConteinerType"]').html(options);
			},
			buttons: buttons
		});
		
		window.popupcount++;
	}
}

function signDialog(str, path, pass, cont) {
	var appl = getApplet('ECPApplet');
	
	var par = {};
	par["signres"] = "";

	if (appletLoading) {
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			signDialog(str, path, pass, cont);
		}, 3000);
		return;
	}
	appletLoadTryCount = 0;
	appl = getApplet('ECPApplet');

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
			href: window.contextName + '/jsp/ecp2.jsp?guid=' + guid,
			modal: true,
			onClose: function() {
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if (window.dialogResult[dialogId] == '0' && keyPath != null && keyPath.length > 0) {
					if (keyPass != null && keyPass.length > 0) {
						
						try {
			        		var signed = appl.getSignAndCert(str, parseInt(keyCont), keyPath, keyPass, false);
							par["path"] = keyPath;
			
							if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
								alert(signed.substring(8, signed.length - 1));
							} else {
							  	var b1 = signed.indexOf("sign=");
								var b2 = signed.indexOf("&cert=");
						
								if (b1 > -1 && b2 > -1) {
									par["signres"] = signed.substring(b1 + 5, b2);
									par["cert"] = signed.substring(b2 + 6);
									par["path"] = keyPath;
									par["code"] = keyPass;
									par["cont"] = keyCont;
								}
							}
		        		} catch (e) {
		        			alert(appletCallFailMsg);
		        		}
			        	post(par);
					} else {
						askPassword(function(askedPass) {
							try {
				        		var signed = appl.getSignAndCert(str, parseInt(keyCont), keyPath, askedPass, false);
								par["path"] = askedPass;
				
								if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
									alert(signed.substring(8, signed.length - 1));
								} else {
								  	var b1 = signed.indexOf("sign=");
									var b2 = signed.indexOf("&cert=");
							
									if (b1 > -1 && b2 > -1) {
										par["signres"] = signed.substring(b1 + 5, b2);
										par["cert"] = signed.substring(b2 + 6);
										par["path"] = keyPath;
										par["code"] = keyPass;
										par["cont"] = keyCont;
									}
								}
			        		} catch (e) {
			        			alert(appletCallFailMsg);
			        		}
				        	post(par);
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
		try {
			var signed = appl.getSignAndCert(str, parseInt(keyCont), keyPath, keyPass, false);
			par["path"] = keyPath;
	
			if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
				alert(signed.substring(8, signed.length - 1));
			} else {
			  	var b1 = signed.indexOf("sign=");
				var b2 = signed.indexOf("&cert=");
		
				if (b1 > -1 && b2 > -1) {
					par["signres"] = signed.substring(b1 + 5, b2);
					par["cert"] = signed.substring(b2 + 6);
					par["path"] = keyPath;
					par["code"] = keyPass;
				}
			}
		} catch (e) {
			alert(appletCallFailMsg);
		}
    	post(par);
	}
}

var ecpUid = null;
function selectFile(uid) {
	ecpUid = uid;
	var appl = getApplet('ECPApplet');
	try {
		appl.selectFile("p12", translation['p12files'], $('[uid="' + uid + '"]').val());
	} catch (e) {
		alert(appletCallFailMsg);
	}
}

function setSelectedFile(f) {
  	if (f != null) {
  		if (f.length > 7 && "[ERROR: " == f.substring(0, 8)) {
    		alert(f.substring(8, f.length - 1));
  		} else {
  			$('[uid="' + ecpUid + '"]').val(f);
  		}
  	}
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
		var appl = getApplet('ECPApplet');
		var f = null;
		try {
			f = appl.loadToken(cid);
		} catch (e) {
			alert(appletCallFailMsg);
			return;
		}

		if (f == null || f == "") {
			alert("Устройство не подключено!");
			return;
		}
		
		// Запрашиваем пароль для устройства и выполняем функцию
		askPassword(function(pd) {
			if (pd != null) {
				$('[uid="ecpPass"]').val(pd)
					    			
				blockPage("Считывание доступных ключей на ключевом контейнере...");

				// Если устройство подключено, считываем список сертификатов
				var certs = appl.getStoreEntries(cid, pd);

    			$('body').unblock();

				if (certs.length > 7 && "[ERROR: " == certs.substring(0, 8)) {
					alert(certs.substring(8, certs.length - 1));
					return;
				} else {
					var certsArr = certs.split(appl.ROW_SEPARATOR);
					for (var ind = 0; ind < certsArr.length; ind++) {
			            var info = certsArr[ind].split(appl.COLUMN_SEPARATOR);
		            	options += '<option value="' + info[0] + '">' + info[2] + '</option>';
					}
					$('[uid="ecpCert"]').html(options);
				}
			}
		}, null);
	}
}
*/
$("body").on('click', 'a.treeField', function() {
	if (!$(this).hasClass('btn-disabled')) {
		uid = $(this).attr("id");
		openTree(uid, $(this).attr("title"));
	}	
	return false;
});

$("body").on('click', '.datagrid-row .fam-bin', function() {
	endEditing();
	var t = $(this).closest('.datagrid-view').find('.easyui-datagrid');
	var tr = $(this).closest('tr');
	var index = tr.attr('datagrid-row-index');
	index = globalIndex(t, index);
	
    var par = {};
	par["uid"] = t.attr('id');
	par["cmd"] = "set";
	par["com"] = "del";
	par["ind"] = index;

	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.changes) {
			parseData(data);
			reloadTables();
		} else {
	        if (data.result == "success") {
	        	$.messager.confirm('', data.message, function(e) {
	                if (e) {
	                	par["sure"] = 1;
	        			loadData(par, true);
	                }
	            });
	        }
		}
	}, 'json');

	return false;
});

function showChangeMsg() {
	$('#saveBtn').linkbutton('enable');
	$('#cancelBtn').linkbutton('enable');
}
function hideAllChangeMsg() {
	$('#saveBtn').linkbutton('disable');
	$('#cancelBtn').linkbutton('disable');
}
function hideChangeMsg() {
	$('#saveBtn').linkbutton('disable');
	$('#cancelBtn').linkbutton('disable');
}

function dgBtnAction2(com, i, btn) {
	if (!btn.hasClass('l-btn-disabled')) {
		dgBtnAction(com, i);
	}
}

function enableNaviBtn(btn, enable) {
	if (enable && btn.hasClass('l-btn-disabled')) {
		if (btn.attr('onclick') != null)
			btn.removeClass('l-btn-disabled').removeClass('l-btn-plain-disabled');
		else
			btn.linkbutton('enable');
	} else if (!enable && !btn.hasClass('l-btn-disabled')) {
		if (btn.attr('onclick') != null)
			btn.addClass('l-btn-disabled').addClass('l-btn-plain-disabled');
		else
			btn.linkbutton('disable');
	}
}

function dgBtnAction(com,i,btn) {
	endEditing();
    var par = {};
	par["uid"] = i;
	if(com == "del") {
		var objs = $('#'+i).datagrid('getSelections');
		if(objs.length > 0) {
			var indxs = [objs.length];
			for (var j = 0; j < objs.length; j++) {
				indxs[j] = globalIndex($('#'+i), $('#'+i).datagrid('getRowIndex', objs[j]));
			}
			var idx = indxs.join(",");
			par["idx"] = idx;
		}
	}
	
	var st = com.indexOf('media/img/');
	if (st>-1) {
		var fn = com.indexOf('.gif', st);
		par["com"] = com.substring(st+10,fn);
	}else{
		par["com"] = com;
	}
	par["cmd"] = "set";
	
	if (btn && com == "showDel") {
		var span = btn.find('.icon-showDel');
		if (span.length > 0)
			span.removeClass('icon-showDel').addClass('icon-showDelUn');
		else
			btn.find('.icon-showDelUn').removeClass('icon-showDelUn').addClass('icon-showDel');
	}
	
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.changes) {
			parseData(data);
			reloadTables();
		} else {
	        if (data.result == "success") {
	        	$.messager.confirm('', data.message, function(e){
	                if (e){
	                	par["sure"] = 1;
	        			loadData(par, true);
	        			showChangeMsg();
	                }
	            });
	        }
		}
	}, 'json');
}

function reportClick(btn) {
	var par = {};
	par["id"] = btn.attr('reportid');
	par["cmd"] = "print";
	
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.result == 'error') {
			alertError(data.message);
		} else if (data.file) {
			var url = window.mainUrl + "&rnd=" + rnd() + "&cmd=opf&fn=" + encodeURIComponent(data.file);
			$('#report_frame').attr('src', url);
		}
	}, 'json');
}

$("body").on('click', 'a[reportid]', function() {
	reportClick($(this));
});

$("body").on('change', 'input, select, textarea', function() {
	var id = $(this).attr('id') || $(this).parent().prev().attr('id');
	var comboSearch = $(this).parent().prev().hasClass('easyui-combobox');
	if (id == 'cellDatabox') {
		endEditing();
	} else if (id) {
		var val = "";
		if ($(this).attr('type') == 'checkbox') {
			val = $(this).prop('checked');
		} else 
			val = $(this).val();
		if (!comboSearch) {
			setValue(id, val);
		}
	}
});

$("body").on('change', '.datebox .textbox-text', function() {
	var id = $(this).parent().prev().attr('id');
	if (id == 'cellDatabox') {
		endEditing();
	} else if (id) {
		var val = $(this).val();
		setValue(id, val);
	}
});

$("body").on('keyup', "input, select, textarea", function(event) {
	if (event.which && event.which == 13 && ($(this).prop("tagName") != 'TEXTAREA' || event.ctrlKey)) {
		var id = $(this).attr('id');
		if (id)
			focusNext($(this));
		else {
			id = $(this).parent().prev().attr('id');
			if (id && id != 'cellDatabox') {
				//var val = $(this).val();
				//if (val == null || val == '')
				//	setValue(id, val);
				focusNext($(this));
			}
		}
	}			
});

$("#notiInList").on('change', '#selectNoti', function(){
	var i = parseInt($('#selectNoti').val());
	statusReadingNotification = i;
});

function focusNext(elem) {
	var curElem = elem;
	var nextElem = elem.next('input:visible:enabled, select:visible:enabled, textarea:visible:enabled, button:visible:enabled, .easyui-datagrid');
	while (nextElem.length < 1 && !curElem.is('body')) {
		nextElem = curElem.nextAll().find('input, select, textarea, button').filter(':visible:enabled:first');
		curElem = curElem.parent();
	}
	if (nextElem.length < 1) {
		var elems = $('body').find('input:visible:enabled, select:visible:enabled, textarea:visible:enabled, button:visible:enabled, .easyui-datagrid');
		if (elems.length > 0)
			nextElem = $(elems[0]);
	}
	focusedTable = undefined;
	if (nextElem != elem) {
		if (nextElem.hasClass('easyui-datagrid')) {
			var panel = nextElem.datagrid('getPanel').panel('panel');
			panel.attr('tabindex', 0);
			panel.focus();

			focusedTable = nextElem.attr('id');
			panel.unbind('keydown').bind('keydown',function(event) {
				if (event.which && event.which == 38) {
			        return selectRow(nextElem, true);
				} else if (event.which && event.which == 40) {
			        return selectRow(nextElem, false);
				} else if (event.which && event.which == 37) {
			        return selectColumn(nextElem, true);
				} else if (event.which && event.which == 39) {
			        return selectColumn(nextElem, false);
				} else if (event.which && event.which == 32) {
			        return beginEditCell(nextElem);
				}
			});
		} else
			nextElem.focus();
	} else
		elem.blur();
}

$("body").on('keyup', ".datagrid-editable-input, .numberbox .textbox-text", function(e) {
	if (e.which && e.which == 13)
		endEditing();
});

$("body").on('click', '.ortable', function(e) {
	endEditing();
});

$("body").on('click', '.or3-btn, .popup', function(e) {
	e.preventDefault();
	endEditing();
});

$("body").on('keyup', ".datagrid-editable .datebox .textbox-text", function(event) {
	if (event.which && event.which == 13) {
		$(this).parent().prev().combo('hidePanel');
		$(this).blur();
		endEditing();
	}
});

$("body").on('click', '.or3-btn:not(.view-file)', function(e) {
	e.preventDefault();
	if ($(this).attr('id') && !$(this).hasClass('btn-disabled')) {
		blockPage();
		setValue($(this).attr('id'), 1);
	}
});

$("body").on('click', '.view-file', function(e) {
	if (!$(this).hasClass('btn-disabled')&& $(this).attr('id')) {
		downloadFile(e, $(this).attr('id'));
	} else if ($(this).parent().attr('id')) {
		downloadFile(e, $(this).parent().attr('id'),$(this).parent().attr('index'));
	}
});

$("body").on('click', '.open-file', function(e) {
	if ($(this).attr('uid')) {
		var cls = $(this).attr('cls');
		var method = $(this).attr('method');
		var uid = $(this).attr('uid');
		var path = $(this).attr('path');
		var fn = $(this).attr('fn');
		var ext = $(this).attr('ext');
		var index = $(this).attr('index');

		var url = window.mainUrl + "&sfunc&cls=" + cls + "&name=" + method + "&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + uid + "&arg1=" + encodeURIComponent(path);
		if (index)
			url += "&arg2=" + index;
		$('#report_frame').attr('src', url);
	}
});

$("body").on('click', '.delete-file', function(e) {
	if ($(this).parent().attr('id')) {
	    var par = {};
		par["uid"] = $(this).parent().attr('id');
		par["cmd"] = "set";
		par["com"] = "del";
		par["ind"] = $(this).parent().attr('index');

		loadData(par, true);

		return false;
	}
});

var lastDownloadTime = 0;
var waitTime = 1500;

function downloadFile(e, id, row, col) {
	var curTime = (new Date).getTime();
	e.stopPropagation();
	e.preventDefault();
	
	if (lastDownloadTime == 0 || curTime - lastDownloadTime > waitTime) {
		lastDownloadTime = curTime;
		var par = {};
		par["uid"] = id;
		par["cmd"] = "viewFile";
		if (row != null)
			par["row"] = row;
		if (col != null)
			par["col"] = col;
		
		$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
			checkData(data);
			loadData({}, true);
			if (data.result == "success") {
				var url = window.mainUrl + "&trg=frm&cmd=opf&fn=" + encodeURIComponent(data.file);
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&rnd=" + rnd();
				
				if (data.action == "print") {
					var opts = {url: url};
					var btn = $("<button></button>").appendTo($('#trash'));
					btn.printPage(opts);
					btn.click();
				} else if (data.action == "view") {
					if (data.ext == "html") {
						window.open(url);
					} else {
						$('#report_frame').attr('src', url);
					}
				} else {
					$('#report_frame').attr('src', url);
				}
			}
		}, 'json');
	}
	return false;
} 

var isOnChek = true;
var nodeParent = false;
$.fn.tree.defaults.onSelect = function(node, checked) {
	var nodeCheck = true;
	if (!$(this).hasClass("nochange")) {
		var tree = $(this).tree('options');
		var multi2 = tree.multiSelect;
		var multi = getOption($(this), 'checkbox');
		if(multi2 == true || multi == "true") {
			var nodes = $(this).tree('getChecked');
			isOnChek = true;
			if(node.parent != 0) {
			    for (var i = 0; i<nodes.length; i++) {
			    	if (node.target == nodes[i].target) {
			    		$(this).tree('uncheck', node.target);
			    		nodeCheck = false;
			    	}
			    }
			} else {
				if(!nodeParent) {
					nodeCheck = true;
					nodeParent = true;
				} else {
					$(this).tree('uncheck', node.target);
					nodeCheck = false;
		    		nodeParent = false;
				}
			}
		    if (nodeCheck) $(this).tree('check', node.target);
		    isOnCheck = false;
		} else {
			setSelectedValue($(this).attr('id'), node.id);
		}
	}
	
	var tmp = $(this).attr('selectFolder');
	var canSelectFolder = tmp == null || tmp == 'true';
	
	if (!canSelectFolder) {
		var isFolder = $(node.target).find('.tree-folder').length > 0;
		var dlg = $(node.target).closest('.panel');
		
		if (isFolder) {
			dlg.find(".dialog-button .l-btn").first().linkbutton('disable');
		} else {
			dlg.find(".dialog-button .l-btn").first().linkbutton('enable');
		}
	}

};

var selectedTree = null;
var selectedProc = null;
var selectedFavProc = null;

$.fn.tree.defaults.onContextMenu = function(e,node){
	e.preventDefault();
	selectedTree = $(this);
	selectedTree.tree('select', node.target);
	var viewType=selectedTree.attr('viewType');
	var sortType=selectedTree.attr('sortType');
	var pUp=false;
	var pDown=false;
	if(sortType==1){
		var nodep=selectedTree.tree('getParent',node.target);
		if (nodep!=null){
			var chs=selectedTree.tree('getChildren',nodep.target);
			if (chs!=null && chs.length>0){
				var chFirst=chs[0];
				if(node.id!=chFirst.id)
					pUp=true;
				var chLast=chs[chs.length-1];
				if(node.id!=chLast.id)
					pDown=true;
			}
		}
	}
	if(pUp) 
		$('#treeMenu').menu('enableItem', $('#moveUp'));
	else
		$('#treeMenu').menu('disableItem', $('#moveUp'));
	if(pDown)
		$('#treeMenu').menu('enableItem', $('#moveDown'));
	else
		$('#treeMenu').menu('disableItem', $('#moveDown'));
	$('#treeMenu').menu('show',{
		left: e.pageX,
		top: e.pageY
	});
};

$.extend($.fn.tree.methods,{
	move: function(jq, param){
		return jq.each(function(){
			var t = $(this);
			var li = $(param.target).parent();
			li = param.dir=='up' ? li.prev() : li.next();
			var pnode = li.children('div.tree-node');
			if (pnode.length){
				var data = t.tree('pop', param.target);
				var options = {data:data};
				if (param.dir == 'up'){
					options['before'] = pnode[0];
				} else {
					options['after'] = pnode[0];
				}
				t.tree('insert',options);
			}
		})
	}
});

$("#addToFavorites").click(function() {
	if (selectedProc) {
		var url = window.mainUrl;
		var par = {};
		par["cmd"] = 'addToFavorites';
		par["processUID"] = selectedProc;
		par["json"] = 1;
		$.ajax({
			type : 'POST',
			url : url + "&rnd=" + rnd(),
			data : par,
			success : function(data) {
				loadProccessList2();
			},
			dataType : 'json',
			async : false
		});
	}
});

$("#removeFromFavorites").click(function() {
	if (selectedFavProc) {
		var url = window.mainUrl;
		var par = {};
		par["cmd"] = 'removeFromFavorites';
		par["processUID"] = selectedFavProc;
		par["json"] = 1;
		$.ajax({
			type : 'POST',
			url : url + "&rnd=" + rnd(),
			data : par,
			success : function(data) {
				loadProccessList2();			
			},
			dataType : 'json',
			async : false
		});
	}
});

$.fn.treegrid.defaults.onContextMenu = function(e,node){
	e.preventDefault();
	selectedTree = $(this);
	$(this).treegrid('select', node.id);
	$('#treeTableMenu').menu('show',{
		left: e.pageX,
		top: e.pageY
	});
};

function viewChild(comp, opts, node, zebra1, zebra2, hiddenNodesCount) {
	var tr = opts.finder.getTr(comp[0], node[opts.idField]);
	var cell = tr.find('div.datagrid-cell-rownumber');
	var value = cell.text();
	var index = parseInt(value) - hiddenNodesCount;
	if (index % 2 == 0) {
		tr.css('background', zebra1);
	} else {
		tr.css('background', zebra2);
	}
	var children = getMyChildren(comp, node.id);
	if (children.length > 0) {
		if (node.state == "open") {
			for (var i = 0; i < children.length; i++) {
				hiddenNodesCount += viewChild(comp, opts, children[i], zebra1, zebra2, hiddenNodesCount);
			}
		} else {
			return comp.treegrid('getChildren', node.id).length;
		}
	}
	return 0;
}

function getMyChildren(comp, parentId) {
	var children = comp.treegrid('getChildren', parentId);
	var myChildren = [];
	for (var i = 0; i < children.length; i++) {
		if (children[i].parent == parentId) {
			myChildren.push(children[i]);	
		}
	}
	return myChildren;
}

function paintTreegridRows(uid) {
	var zebra1;
	var zebra2;
	var url = window.mainUrl;
	var par = {};
	par["cmd"] = 'treegridZebraColors';
	par["uid"] = uid;
	par["json"] = 1;
	par["rnd"] = rnd();
	$.post({
		url : url,
		data : par,
		success : function(data) {
			zebra1 = data.zebra1 ? data.zebra1 : "";
			zebra2 = data.zebra2 ? data.zebra2 : "";

			if (zebra1 != "#FFFFFF" || zebra2 != "#FFFFFF") {
				var rootNode = $('#' + uid).treegrid('getRoot');
				var opts = $('#' + uid).treegrid('options');
				viewChild($('#' + uid), opts, rootNode, zebra1, zebra2, 0);
			}
		},
		dataType : 'json'
	});
}

function wrapNodesContent(uid) {
	var url = window.mainUrl;
	var par = {};
	par["cmd"] = 'isWrapNodeContent';
	par["uid"] = uid;
	par["json"] = 1;
	par["rnd"] = rnd();
	$.post({
		url : url,
		data : par,
		success : function(data) {
			if (data.isWrapNodeContent == 1) {
				var root = $('#' + uid).tree('getRoot');
				wrapNodeContent(-1, root);
			}
		},
		dataType : 'json'
	});
}

function wrapNodeContent(index, node) {
	var domId = node.domId;
	//console.log(domId);
	var divElement = document.getElementById(domId);
	divElement.className += " tree-node-wrap-text";
	var spanElement = divElement.getElementsByClassName("tree-title")[0];
    spanElement.className += " tree-title-wrap-text";
	var children = node.children;
	if (children) {
		$.each(children, function(i, child) {
			wrapNodeContent(i, child);
		});
	}
}

$.fn.tree.defaults.onLoadSuccess = function() {
	var uid = $(this).attr('id');
	if (uid != "adminsTree" && uid != "dictsTree" && uid != "processTree" && uid != "archiveTree") {
		wrapNodesContent($(this).attr('id'));
	}
}

$.fn.treegrid.defaults.onLoadSuccess = function(rData) {
	paintTreegridRows($(this).attr('id'));
	var sel = $(this).attr('selRows');
	if (sel && sel != 0) {
		var rows = sel.split(',');
		for (var ind = 0; ind < rows.length; ind++) {
			if ($(this).treegrid('find', rows[ind])) {
				$(this).treegrid('select', rows[ind]);
			}
		}
	}
	
/*	if ($(this).hasClass("old-tree")) {
		var trs = $(this).parent().find('.datagrid-view2 tr[node-id]');
		$.each(trs, function(i, tr) {
			var nid = $(tr).attr('node-id');
			if (nid.indexOf('_') == -1) {
				var tds = $(tr).children('td');
				
				if (!tds.first().attr('colspan')) {
					var cols = tds.size();
					var width = tds.first().width();
					$.each(tds, function(j, td) {
						if (j > 0) {
							width += $(td).width();
							$(td).remove();
						}
					});
					tds.first().width(width).attr('colspan', cols).children('div').width('100%');
					tds.first().width(width).attr('colspan', cols);
				}
			}
		});
	}*/
};

$.fn.tree.defaults.onCheck = function(node, checked) {
	var tree = $(this).tree('options');
	var multi2 = tree.multiSelect;
	var multi = getOption($(this), 'checkbox');
	if ((multi2 == true || multi == "true") && isOnChek){
	    var ids = "";
	    var nodes = $(this).tree('getChecked');
	    for (var i = 0; i<nodes.length; i++) {
	    	ids += nodes[i].id + ",";
	    }
	    if (ids.length > 0){
	    	ids = ids.substring(0, ids.length-1);
	    }
		setSelectedValue($(this).attr('id'), ids);
	}
};

$.fn.tree.defaults.onCollapse = function(node){
	var par = {"uid":$(this).attr('id'),"collapse":node.id};
	post(par);
};

$.fn.treegrid.defaults.onCollapse = function(node){
	var par = {"uid":$(this).attr('id'),"collapse":node.id};
	post(par);
	paintTreegridRows($(this).attr('id'));
};

$.fn.tree.defaults.onExpand = function(node){
	if (!$(this).hasClass("nochange")) {
		var par = {"uid":$(this).attr('id'),"expand":node.id};
		post(par);
	}
};

$.fn.treegrid.defaults.onExpand = function(node){
	var par = {"uid":$(this).attr('id'),"expand":node.id};
	post(par);
	paintTreegridRows($(this).attr('id'));
};

$("body").on('change', '.floating input[type="checkbox"]', function(e) {
	showChangeMsg();
	var par = {"uid":$(this).parents('.checklist').first().attr('id'),"val":$(this).val(),"cmd":"set","com":(this.checked ? "add" : "del")};
	loadData(par, true);
});


$.fn.datalist.defaults.onCheck = function(index, row) {
	showChangeMsg();
	var par = {"uid":$(this).attr('id'),"val":index,"cmd":"set","com":"add"};
	loadData(par, true);
};

$.fn.datalist.defaults.onUncheck = function(index, row) {
	showChangeMsg();
	var par = {"uid":$(this).attr('id'),"val":index,"cmd":"set","com":"del"};
	loadData(par, true);
};

$.fn.datalist.defaults.onLoadSuccess = function(data) {
	var total = data.total;
	//console.log(total);
	for (var i=0; i<data.rows.length; i++) {
		if (data.rows[i].checkbox) {
			$(this).datalist("checkRow", i);
		}
	}
};

function showError_selected_comp(error, rownum) {
	var e = $('#'+error.uuid);

	// подсветка ячейки в таблице
	if (rownum > -1) {
		var td_field = $("td[field="+ error.uuid +"]");
		if (td_field.length >= rownum) {
			$(td_field).parent().parent().find("[datagrid-row-index="+ rownum +"] > td[field="+ error.uuid +"]")
				.toggleClass("error-type-"+error.type, true);
		}
	// подсветка поля
	} else {
		setErrorType(e, error.type);
	}
}

function makeErrorsHTML(ers) {
	var list = ["<ul class='error-red'>","<ul class='error-blue'>","<ul class='error-gray'>"];
	$.each(ers, function(i,er) {
		var rownum = -1;
		if ($("td[field="+ er.uuid +"]").length > 1) {
			rownum = er.msg.substring(er.msg.lastIndexOf('(')+1, er.msg.lastIndexOf(')')) - 1;
		}
		list[er.type] += "<li uuid='" + er.uuid + "'";
		if (rownum > -1) {
			list[er.type] += " row='" + rownum + "'";
		}
		list[er.type] += ">" + er.msg.replace(/\n/g, '<br/>') + "</li>";
		showError_selected_comp(er, rownum);
	});
	list[0]+="</ul>";
	list[1]+="</ul>";
	list[2]+="</ul>";
	
	return list[0]+list[1]+list[2];
}

function focusComponent(uuid, rownum) {
	var e = $('#' + uuid);
	if (rownum > -1) {
		var td_fields = $(".datagrid-btable td[field="+ uuid +"]");
		if (td_fields.length >= rownum) {
			e = $(td_fields.get(rownum));
		}
	}
	
	if (e != null && e.length > 0) {
		// Если компонент лежит на табпанели, пробегаем по всем родителям - табпанелям
		$.each(e.parents('.tabs-panels > .panel > .panel-body'), function(i, selTab) {
			var tabPanel = $(selTab).parent().parent().parent();
			var index = parseInt($(selTab).attr('tabindex'));
			tabPanel.tabs('select', index);
		});
	
		if (rownum > -1) {
			e.click();
			var parent = e.parents('.datagrid-body');
			var table = parent.find('.datagrid-btable');
			
			parent.scrollTop(e.offset().top - table.offset().top);
			parent.scrollLeft(e.offset().left - table.offset().left);
			
			var openedDialogs = window.openedDialogs;
			if (openedDialogs.length == 0) {
				$('#app').scrollTop(e.parents('.datagrid').offset().top - $('#app').children('div').offset().top);
			}
		} else {
			if (e.hasClass('easyui-datebox')) {
				if (!e.hasClass('datebox-f')) e.datebox();
				e = e.datebox("textbox")
			} else if (e.hasClass('easyui-datetimebox')) {
				if (!e.hasClass('datetimebox-f')) e.datetimebox();
				e = e.datetimebox("textbox");
			} else if (e.hasClass('easyui-combobox')) {
				if (!e.hasClass('combobox-f')) e.combobox();
				e = e.combobox("textbox");
			} else if (e.hasClass('easyui-numberbox')) {
				if (!e.hasClass('numberbox-f')) e.numberbox();
				e = e.numberbox("textbox");
			}
			e.focus();
		}
	}
}

function showErrors(errors, filePath, fileName, command, btn1show, btn1text, popup) {
	clearAllErrorType();

	var content = makeErrorsHTML(errors);

	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	var buttons = [];

	if (btn1show == true) {
		var button1 = {
			text: btn1text,
			handler: function() {
				window.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
				
				if (popup)
					popup.dialog('destroy');
				
				if (popup == null && command == "nextStep")
					toActiveMain();
			}
		};
		buttons.push(button1);
	}	
	var button2 = {
		text: translation['continue'],
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	buttons.push(button2);

	$('#' + dialogId).dialog({
		title: '<div style="font-size:14px; color: #777;">'+translation['errors']+'</div>',
		width: 900,
		height: 550,
		resizable: true,
		style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
		closed: false,
		cache: false,
		modal: true,
		content:content,
		tools: [{
				iconCls:'icon-rept',
				handler:function(){
					if (filePath != null) {
						var url = window.mainUrl + "&rnd=" + rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
						$('#report_frame').attr('src', url);
					}
				}
		}],
		onOpen: function() {
			$("li[uuid]").on('click', function (e) {
				var uuid = $(this).attr('uuid');
				var rownum = -1;
				var row = $(this).attr('row');
				if (row != null && row.length > 0)
					rownum = parseInt(row);
					
				focusComponent(uuid, rownum);
				
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');

			});
		},
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
			opts = $(this).dialog('options');

        	if (window.dialogResult[dialogId] == '0') {
    			var par = {};
    			par["cmd"] = command;
    			par["result"] = "save";
    			blockPage(translation['saving']);
    			
				$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
					checkData(data);
					if (data.result == "success") {
						hideChangeMsg();
						loadData({}, true);
					} else {
						alertError(translation['error']);
					}
    				$('body').unblock();
				}, 'json').fail(function() {
					$('body').unblock();
				});
        	}
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function showForceErrors(errors, btn1show, filePath, fileName, btn1text, btn2text) {
	var content = makeErrorsHTML(errors);

	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));
	window.dialogResult[dialogId] = '1';

	var buttons = [];

	if (btn1show == true) {
		var button1 = {
				text: btn1text,
			handler: function() {
				window.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button1);
	}	
	var text2 = btn2text == undefined ? translation['continue'] : btn2text;
	var button2 = {
		text: text2,
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	buttons.push(button2);

	$('#' + dialogId).dialog({
		title: '<div style="font-size:14px; color: #777;">'+translation['errors']+'</div>',
		width: 900,
		height: 550,
		resizable: true,
		style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
		closed: false,
		cache: false,
		modal: true,
		content:content,
		tools: [{
			iconCls:'icon-rept',
			handler:function(){
				if (filePath != null) {
					var url = window.mainUrl + "&rnd=" + rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
					$('#report_frame').attr('src', url);
				}
			}
		}],
		onOpen: function() {
			$("li[uuid]").on('click', function (e) {
				var uuid = $(this).attr('uuid');
				var rownum = -1;
				var row = $(this).attr('row');
				if (row != null && row.length > 0)
					rownum = parseInt(row);
					
				focusComponent(uuid, rownum);
				
				window.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');

			});
		},
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
			blockPage();
			
			opts = $(this).dialog('options');

			var par = {};
        	if (window.dialogResult[dialogId] == '0') {
        		par["commitResult"] = 1;
        	} else {
        		par["commitResult"] = 0;
        	}
        	post(par);
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function showOptions(options) {
	
	var content = "<table class='options'><tr><td>";
	$.each(options, function(i, option) {
		content += "<tr><td><input type='radio' name='option' value='" + i + "'/>" + option.o + "</td></tr>";
	});
	content += "</table>";

	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));

	window.dialogResult[dialogId] = '1';

	var buttons = [];

	var button1 = {
		text: 'Ok',
		handler: function() {
			window.dialogResult[dialogId] = '0';
			$("#" + dialogId).dialog('destroy');
		}
	};
	buttons.push(button1);
	var button2 = {
		text: translation['cancel'],
		handler: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		}
	};
	buttons.push(button2);

	$('#' + dialogId).dialog({
		title: '<div style="font-size:14px; color:#777;">Выбор дальнейшей обработки</div>',
		width: 500,
		height: 250,
		closed: false,
		cache: false,
		modal: true,
		content:content,
		onClose: function() {
			window.dialogResult[dialogId] = '1';
			$("#" + dialogId).dialog('destroy');
		},
		onBeforeDestroy : function() {
			opts = $(this).dialog('options');

			var par = {};
    		par["optionResult"] = -1;
        	if (window.dialogResult[dialogId] == '0') {
        		var checked = $('#' + dialogId).find('input:checked');
        		if (checked.length > 0)
        			par["optionResult"] = checked.val();
        	}
        	post(par);
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function showPopupErrors(errors, filePath, fileName, opts, popup, fatal, isDataIntegrityControl) {
	var content = makeErrorsHTML(errors);

	var dialogId = 'or3_popup' + window.popupcount;
	$('#trash').append($("<div></div>").attr('id', dialogId));

	var buttons = [];

	var button1 = {
		text: translation['continue2'],
		handler: function() {
			$("#" + dialogId).dialog('destroy');
		}
	};
	buttons.push(button1);
	
	if (fatal && isDataIntegrityControl) {
		var button3 = {
			text: translation['cancel'],
			handler: function() {
				$("#" + dialogId).dialog('destroy');
				var openedDialogs = window.openedDialogs;
				if (openedDialogs.length > 0) {
					var dialogId1 = openedDialogs[openedDialogs.length - 1];
					setDialogBtnsEnabled(dialogId1, false);
					sendUserDecision("1");
					window.dialogResult[dialogId1] = '1';
					$("#" + dialogId1).dialog('destroy');
				}
			}
		};
		buttons.push(button3);
	} else {
		var button2 = {
			text: translation['save'],
			handler: function() {
				$("#" + dialogId).dialog('destroy');
				
				var par = {};
	        	par["uid"] = opts.uid ;
	        	par["val"] = '0';
	        	if (opts.row != undefined) par["row"] = opts.row;
	        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
	        	par["cmd"] = "closePopup";
	        	loadData(par, true);
				popup.dialog('destroy');
			}
		};
		buttons.push(button2);
	}

	$('#' + dialogId).dialog({
		title: '<div style="font-size:14px; color: #777;">'+translation['errors']+'</div>',
		width: 600,
		height: 400,
		style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
		closed: false,
		cache: false,
		modal: true,
		content:content,
		tools: [{
			iconCls:'icon-rept',
			handler:function(){
				if (filePath != null) {
					var url = window.mainUrl + "&rnd=" + rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
					$('#report_frame').attr('src', url);
				}
			}
		}],
		onOpen: function() {
			$("li[uuid]").on('click', function (e) {
				var uuid = $(this).attr('uuid');
				var rownum = -1;
				var row = $(this).attr('row');
				if (row != null && row.length > 0)
					rownum = parseInt(row);
					
				focusComponent(uuid, rownum);
				
				$("#" + dialogId).dialog('destroy');

			});
		},
		onClose: function() {
			$("#" + dialogId).dialog('destroy');
		},
		buttons: buttons
	});
	
	window.popupcount++;
}

function forseSaveChanges(initiator) {
	breadcrumpsVisible = !($(initiator).length > 0 
						&& $(initiator).attr('id').match(/ui_personInfo|ui_staff|ui_actions|ui_right|ui_stat/));

	endEditing();
	var par = {"cmd":"fcommit"};
	saveAjaxRequest('POST', window.mainUrl, par, null, null, false);
}

function saveChanges() {
	endEditing();
	blockPage(translation['saving']);
	var par = {"cmd":"commit"};
		
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.result == "success") {
			hideChangeMsg();
		} else {
			if (data.result == "fatal") {
				$('body').unblock();
			}
			showErrors(data.errors, data.path, data.name, "commit", true, translation['save']);
		}
		$('body').unblock();
	}, 'json');
}

function closeIfc() {
	endEditing();
	blockPage(translation['saving']);
	var par = {"cmd":"closeIfc"};
		
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		$('body').unblock();
	}, 'json');
}

function resetChanges() { 
	blockPage(translation['canceling']);
	var par = {};
	par["cmd"] = "rollback";
	loadData(par, true);
	hideChangeMsg();
	styles = {};
}

function nextStep(ignore, did, transitionId) {
	endEditing();
	if (ignore || $("#nextBtn").hasClass("active")) {
		if (transitionId || ignore != undefined || did != undefined || confirm(translation['askNextStep'])) {
			var par = {};
			par["cmd"] = "nextStep";
			if (transitionId) {
				par["transitionId"] = transitionId;
			}
			$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
				checkData(data);
				if (data.result == "success") {
					hideChangeMsg();
					if (did == null)
						toActiveMain();
					if (data.message) {
						alert(data.message);
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
						buttons:[{text:'Принять', handler:function(){stepSelected(ignore, did);}}, {text:'Отмена', handler:function(){stepSelectionCanceled();}}]
					});
					$('#stepSelectionDlg').dialog("open");
				} else {
					if (data.message) {
						alert(data.message);
					}
					if (did)
						setDialogBtnsEnabled(did, true);
					$('body').unblock();
				}
			}, 'json');
		}
	}
}

function stepSelected(ignore, did) {
	var steps = document.getElementsByName("step");
	var transitionId;
	for (var i = 0; i < steps.length; i++) {
		if (steps[i].checked)
			transitionId = steps[i].value;
	}
	nextStep(ignore, did, transitionId);
	$('#stepSelectionDlg').dialog("close");
	$('body').unblock();
}

function stepSelectionCanceled() {
	$('#stepSelectionDlg').dialog("close");
	$('body').unblock();
}

function cancelStart(uid) {
	var par = {};
	par["cmd"] = "cancelProcess";
	par["uid"] = uid;
	post(par);
}

function logout() {
	forseSaveChanges();
	//if (confirm('Вы действительно хотите выйти из Системы?')) { #1532
		$.get(window.mainUrl + "&cmd=ext&json=1", function(data) {
			if (backPage) {
				window.location.href = backPage;
			} else {
				window.location.href = window.contextName + "/jsp/login.jsp?rnd=" + rnd();
			}
		}, 'json');
	//}
}

function loadUI(hash) {
	var params = parseHash(hash);

	for (var i=0; i<ifcRefreshers.length; i++) {
		clearInterval(ifcRefreshers[i]);
	}
	ifcRefreshers = [];
	
	hideAllChangeMsg();
	if (params.params.id) {
		activeMenu = params.params.id;
        $('#left-panel-content li.active').removeClass('active');
        $('#' + activeMenu).parent().addClass("active");
    	selSect = pageName[activeMenu];
    	idSect = pageId[activeMenu];
	}
	$('.rep').hide();
	$('#changePanel').hide();
	
	if (params.params.ui) {
		var par = {};
		par["ifcLeft"] = 1;
		blockPage();
		post(par, function(data) {
			$('body').unblock();
		});
		if(params.params.ui == 'start') {
			$('.appwin').hide();
			$('#startDiv').show();
		} else if(params.params.ui == 'oldStart') {
			$('.appwin').hide();
			$('#oldStartDiv').show();
		} else {
			var win = params.params.ui;
			$('.appwin[id!="'+win+'"]').hide();
			$('#'+win).show();
			if (params.params.mode && params.params.mode=='tabs') {
				$('#'+win+'_tab').tabs('resize');
			}
			if (params.params.mode && params.params.mode=='layout') {
				$('#'+win+'_Layout').layout();
			}
		}
        refreshTasks();
	} else if (params.params.cmd == 'openTask') {
		blockPage();
		var url = makeUrl(params.params);
		$('#app').attr('init',"1");
		openTask(params.params.uid, url);
	} else if (params.params.cmd == 'openMainIfc') {
    	selSect = pageName['ui_startPage'];
    	idSect = pageId['ui_startPage'];
		blockPage();
		$('#app').attr('init',"1");
		$('#glassPane').show();
		$('#uiPanel').show();
		$('#uiTitle').show();
		$('#uiToolbar').show();
		$("#fullPath").hide();
		resizeHeight();
		$('#app').show();
		$('#app').panel('refresh', window.mainUrl + '&cmd=openMainIfc');
		$('#oldStartDiv,#startDiv, #processesList, #archList, #tasksList, #notiInList, #sessionsList, #archList, #dictsList, #adminsList, #helpWnd, #npa449Wnd, #npa233Wnd').hide();
        clearTimeout(refresher);
	} else {
		blockPage();
		$('#app').attr('init',"1");
		$('#glassPane').show();
		$('#uiPanel').show();
		$('#uiTitle').show();
		$('#uiToolbar').show();
		if (breadcrumpsOn && breadcrumpsVisible)
			$("#fullPath").show();
		else
			$("#fullPath").hide();
		resizeHeight();
		$('#app').show();
		var url = makeUrl(params.params);
		$('#app').panel('refresh', url);
		$('#oldStartDiv,#startDiv, #processesList, #archList, #tasksList, #notiInList, #sessionsList, #archList, #dictsList, #adminsList, #helpWnd, #npa449Wnd, #npa233Wnd').hide();
        clearTimeout(refresher);
	}

	$('#left-panel-content > ul').css('height', ($(window).height() - 120)+"px");
	$('#left-panel-content > ul').mCustomScrollbar("update");
	$('.portlet').css('height', ($(window).height()-160)+"px");
	$('.portletTasks').css('height', ($(window).height()-190)+"px");
	$('.portletNotif').css('height', ($(window).height()-200)+"px");
	
	var allProcesses_Layout = document.getElementById("allProcesses_Layout");
	if (allProcesses_Layout) {
		var processesList = document.getElementById("processesList");
		if (processesList) {
			$('#processesList').css('height', ($(window).height()-92)+"px");
		}
		var portletFavProc = document.getElementsByClassName("portletFavProc");
		if (portletFavProc) {
			$('.portletFavProc').css('height', "190px");
		}
		var portletProc = document.getElementsByClassName("portletProc");
		if (portletProc) {
			$('.portletProc').css('height', ($('#processesList').height() - 273)+"px");
		}
	}
	$('.easyui-panel:not(.tamur-tabs)').panel();
}

function makeUrl(pars) {
	var url = window.mainUrl;
	for (var prop in pars) {
		url += "&" + encodeURIComponent(prop) + "=" + encodeURIComponent(pars[prop]);
	}
	return url;
}

function openTask(uid, url) {
	$.get(window.mainUrl + "&cmd=taskType&json=1&uid=" + uid, function(data) {
		if (data.type == "report") {
			$('#report_frame').attr('src', url);
			$('body').unblock();
		} else if (data.type == "htmlreport") {
			$('body').unblock();
			window.open(url);
		} else if (data.type == "undefined") {
			setTimeout(function() {
				openTask(uid, url);
			}, 3000);
		} else if (data.type == "choose") {
			openStartDialog(uid);
		} else if (data.type == "dialog") {
			openStartDialog(uid);
		} else if (data.type == "option") {
			var par = {};
			par["cmd"] = "openTask";
			par["uid"] = uid;
			$.post(window.mainUrl + "&rnd=" + rnd(), par, function(res) {
				if (res.message) {
					alert(res.message);
				}
			});
		} else if (data.type == "error") {
            alertError(data.msg);
			toActiveMain();
			$('body').unblock();
		} else if (data.type == "noop") {
			toActiveMain();
			$('body').unblock();
		} else {
			$('#glassPane').show();
			$('#uiPanel').show();
			$('#uiTitle').show();
			$('#uiToolbar').show();
			$('#nextBtn').show();

			if (breadcrumpsOn && breadcrumpsVisible)
				$("#fullPath").show();
			else
				$("#fullPath").hide();
			resizeHeight();
			$('#app').show();
			$('#app').panel('refresh', url);
			$('#oldStartDiv,#startDiv, #processesList, #archList, #tasksList, #notiInList, #sessionsList, #archList, #dictsList, #adminsList, #helpWnd, #npa449Wnd, #npa233Wnd').hide();
	        clearTimeout(refresher);
	        
	    	$("#nextBtn").addClass("active");
		}
	}, 'json');
}

function parseHash(url) {
	return {
		params: (function() {
			var ret = {},
				seg = url.replace(/^\#/, '').split('&'),
				len = seg.length,
				i = 0,
				s;
			for (; i < len; i++) {
				if (!seg[i]) {
					continue;
				}
				s = seg[i].split('=');
				ret[s[0]] = s[1];
			}
			return ret;
		})()
	};
}

function checkData(data) {
	if (data && data.session && data.session == "off") {
		window.location.href = "login.jsp";
		return false;
	}
	return true;
}

function startOrderIn(iter,proc,uid) {
	blockPage();
	$.get(window.mainUrl + "&cmd=openProcess",{'uid':proc,'obj':iter}, function(data) {
        if (data.result == "success" || data.acts == null || data.acts > 0) {
     		showProcessUI(data);
        } else {
             $.get(window.mainUrl + "&cmd=startProcess",{'uid':proc,'obj':uid}, function(data) {
         		showProcessUI(data);
             },'json');
        }
   }, 'json');
}

function startOrderMy(iter,proc,uid) {
	blockPage();
	$.get(window.mainUrl + "&cmd=openProcess",{'uid':proc,'obj':iter}, function(data) {
        if (data.result == "success" || data.message != null) {
     		showProcessUI(data);
        } else if(data.result == "error" && data.message == null){
        	$('body').unblock();
            alertError(translation['error']);
        }
   }, 'json');
}

$.expr[":"].containsNoCase = function (el, i, m) {
	var search = m[3];
	if (!search) return false;
	var exp = new RegExp(search.replace(/\\/g, '\\\\').replace(/\./g, '\\.').replace(/\(/g, '\\(').replace(/\)/g, '\\)'),"i");
	
	var ch = $(el).find("a, span");
	
	var res = false;
	$.each(ch, function(i, e) {
		res = res || exp.test($(e).text());
	});

	return res;
};

$("#tasksList").on('keyup', "#taskSearchPage", function(event){
	if(event.keyCode == 13){
		loadTasksContent(true);
	}
});

function processCounter(search){
	var a = $('#tasksList').find('.portletTasks tr').length;
	$('#tasksList').find('.portletTasks tr').show();
	if (search && search.length > 0){
		$('#tasksList').find('.portletTasks tr').not(":containsNoCase('" + search + "')").hide();
		var b = $('#tasksList').find('.portletTasks tr').not(":containsNoCase('" + search + "')").length;
		$('#processes_counter').text(search +': '+(a-b+'/'+a));
	} else {
			$('#processes_counter').text(tasksCount);
	}
} 
$("body").on('keyup', "#txtSearchPage", function(event) {
	if(event.keyCode == 13){
		filterTasks();
	}
});

$("body").on('keyup', "#txtSearchPageOld", function(event) {
	if(event.keyCode == 13){
		var search = $('#txtSearchPageOld').val();
		filterOrders(search, 'old');
	}
});

$("body").on('keyup', "#txtSearchPageNotification", function(event) {
	if(event.keyCode == 13){
		var search = $('#txtSearchPageNotification').val();
		if(search.length>1)
			filterOrdersNotification();
		else
			alert("Для поиска должен быть как минимум два символа");
	}
});

function filterTasks (){	
	var search = $('#txtSearchPage').val();
	filterOrders(search, 'my');
	filterOrders(search, 'in');
	filterOrders(search, 'out');
}

function filterOrders(search, type){
	var ordersDiv = $('#ordersList_' + type);
	var a = ordersDiv.find('tr').length;
	if (type == 'in') {
		var c = ordersDiv.find('.label-important').length;
	}
	ordersDiv.find('tr').show();
	
	if (search && search.length > 0){
		ordersDiv.find('tr').not(":containsNoCase('" + search + "')").hide();
		var b = ordersDiv.find('tr').not(":containsNoCase('" + search + "')").length;
		$('#ordersList_' + type + '_count').text(a-b+'/'+a);
		
		if (type == 'in') {
			var d = ordersDiv.find('.label-important').parent().prev().not(":containsNoCase('" + search + "')").length;
			$('#ordersList_in_fire_count').text(c-d + '/' + c);
		}
	} else {
		$('#ordersList_' + type + '_count').text(a);
		$('#ordersList_in_fire_count').text(c);
	}
	if(type=='my'){
		if($('.my_check:visible').is(':checked')){
	    	$('#removeprocess').show();
	    } else {
	    	$('#removeprocess').hide();
	    }
	   if($('.my_check:visible:not(:checked)').length==0 && a>b){
	        $('#main_checkbox').prop('checked',true);
	    }else{
	        $('#main_checkbox').prop('checked',false);       
	    }
	}else if(type=='old'){
		if($('.my_check:visible').is(':checked')){
	    	$('#old_removeprocess').show();
	    } else {
	    	$('#old_removeprocess').hide();
	    }
	}
}

function filterOrder(tr){	
	var search = $('#txtSearchPage').val();

	if (search && search.length > 0)
		tr.not(":containsNoCase('" + search + "')").hide();
}

function filterOldOrder(tr){	
	var search = $('#txtSearchPageOld').val();

	if (search && search.length > 0)
		tr.not(":containsNoCase('" + search + "')").hide();
}

function deleteOrders(byType) {
	$.each(byType, function(type, o) {
		$.each(o, function(i, uid) {
			if (type == 'notif') {
				deleteNotificatin(uid);
				var labelImpor;
				var order = $('#ordersList_in #o' + uid.replace('.', '\\.'));
				labelImpor = order.find('.label-important');
				if (order.length > 0) {
					order.remove();
					var tmp = $('#ordersList_in_count').text();
					var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('#ordersList_in_count').text(oldCount - order.length);
				}
				if (labelImpor.length > 0) {
					var tmp2 = $('#ordersList_in_fire_count').text();
					var oldCount2 = (tmp2.length > 0) ? parseInt(tmp2) : 0;
					var count = oldCount2 - order.length;
					if (count == 0) {
						$('#ordersList_in_fire_count').hide();
					} else {
						$('#ordersList_in_fire_count').show().text(count);
					}
				}
			} else {
				var labelImpor;
				var order = $('#ordersList_' + type + ' #o' + uid.replace('.', '\\.'));
				if (type == 'in') {
					labelImpor = order.find('.label-important');
				}
				order.remove();
				if(type == 'my' || type == 'old') {
					var tmp = $('#ordersList_' + type).find('.myCheckedTr');
					if (deletingOrders && tmp.length == 0) {
						deletingOrders = false;
						$('body').unblock();
					}
				}
				var tmp = $('#ordersList_' + type + '_count').text();
				var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
				$('#ordersList_' + type + '_count').text(oldCount - order.length);
				if (type == 'in' && labelImpor.length > 0) {
					var tmp2 = $('#ordersList_in_fire_count').text();
					var oldCount2 = (tmp2.length > 0) ? parseInt(tmp2) : 0;
					var count = oldCount2 - order.length;
					if (count == 0) {
						$('#ordersList_in_fire_count').hide();
					} else {
						$('#ordersList_in_fire_count').show().text(count);
					}
				}
				if (type == 'my' || type == 'old') {
					$('#oldordersList_count').text(oldCount - order.length);
					if (oldCount - order.length == 0) {
						$('#old_return').show();
					}
				}
			}
		});
	});
}

function updateOrders(byType) {
	$.each(byType, function(type, o) {
		$.each(o, function(i, uid) {
			var method = (type == "in") ? "getOrders" : 
						 (type == "out") ? "getOrdersOut" : 
			 			 (type == "old") ? "getOldOrders" : 
						 (type == "notif") ? "getNewNotificationOrders" : "getMyOrders";
			var url = window.mainUrl + "&sfunc&cls=XmlUtil&name=" + method;
			url += "&arg0=" + uid;
			$.get(url, function(data) {
				if (data) {
					checkData(data);
					if (type == "notif") updateNotification(data.rows);
					if ((type == "in" || type == "out") && dateOrders) {
						var dataarray = data.rows;
						dataarray.sort(sortOrdersbyStatus);
						dataarray.sort(sortOrdersbyCtrl);
					}
					if (data.rows && type != "notif") {
						$.each(data.rows, function(i, o) {
							$.each(o, function(key, order) {
								var stop = order.isStop=='1' ? "<td><input type='checkbox' class='my_check' key='" + key + "' task='" + order.proc + "' obj='"+order.iter+"'/></td>" : "";
								var status = type=='my' || type=='old'? "" :  "<td ><span class='label " + orderStatus[order.status] + " 'title='Срок исполнения'>" +
						        order.ctrl + "</span></td>";
								var colorStatus = type == 'my' || type =='old'? "": order.status == 2 ? "#e35542" : "";
								var html;
								if(type=='out'){
									html = "<td class='order urgen_" + order.urgency + "'></td>"+stop+"<td>"+
					                "<h3><a style=\"color: "+colorStatus+"\" class=\"task-link\" href='javascript:openOrder(\""+order.iuid+"\",\""+order.iter+"\")'>" + 
					                order.title + "</a><br><span><i class='icon-user' title='Автор'></i> " + order.from + 
					                "</span>  <span><i class='icon-calendar' title='Дата создания'> </i> " + order.inDate +
					                "</span> </td>"+status;
								}else{
									var href = type == 'my' || type == 'old' ? 'startOrderMy' : 'startOrderIn';
									html = "<td class='order urgen_" + order.urgency + "'></td>"+stop+"<td>"+
					                "<h3><a style=\"color: "+colorStatus+"\" class=\"task-link\" href='javascript:"+href+"(\""+order.iter+"\",\""+order.proc+"\",\""+key+"\")'>" + 
					                order.title + "</a><br><span><i class='icon-user' title='Автор'></i> " + order.from + 
					                "</span>  <span><i class='icon-calendar' title='Дата создания'> </i> " + order.inDate +
					                "</span> </td>"+status;
								}
								var tr = $('#ordersList_' + type + ' #o' + key.replace('.', '\\.'));
								if (tr.length > 0) {
									tr.html(html);
								} else {
									tr = $("<tr id='o" + key + "'>" + html + "</tr>");
									var firstRow = $('#ordersList_' + type + ' tr:first');
									filterOrder(tr);
									if (firstRow.length > 0)
										firstRow.before(tr);
									else {
										var tbl = $('#ordersList_' + type + ' table');
										tbl.append(tr);
									}
									
									var tmp = $('#ordersList_' + type + '_count').text();
									var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
									$('#ordersList_' + type + '_count').text(oldCount + 1);
									
									if (type == 'in' && order.status == 2) {
										var tmp = $('#ordersList_in_fire_count').show().text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('#ordersList_in_fire_count').show().text(oldCount + 1);
									}
									else if (type == 'old') {
										$('#oldordersList_count').text(oldCount + 1);
									}
								}
							});
						});
					}
				}
			}, 'json');
		});
	});
}

function showOrdersLater(data, type, count) {
	//console.log("Count = " + count);
	var infire = 0;
	var found = $('#ordersList_' + type + '_count').length > 0 && $('#ordersList_' + type).length > 0;
	if (found) {
		$('#ordersList_' + type + '_count').text(data.total);

		var html = '<table class="table table-striped" width="100%" border="0">';
		if (data.rows) {
			var dataarray = data.rows;
			if(dateOrders) {
				dataarray.sort(sortOrdersbyStatus);
				dataarray.sort(sortOrdersbyCtrl);
			}
			$.each(dataarray, function(i, o) {
				$.each(o, function(key, order) {
					var stop = order.isStop=='1' ? "<td><input type='checkbox' class='my_check' key='" + key + "' task='" + order.proc + "' obj='"+order.iter+"'/></td>" : "";
					var status = type=='my' || type=='old' ? "" :  "<td ><span class='label " + orderStatus[order.status] + " 'title='Срок исполнения'>" +
	                order.ctrl + "</span></td>";
					var colorStatus = type == 'my' || type =='old'? "": order.status == 2 ? "#e35542" : "";
					if (type == 'in' && order.status == 2) {
						infire++;
					}
					 
					if(type == 'out'){
						html += "<tr id='o" + key + "'><td class='order urgen_" + order.urgency + "'></td>"+stop+"<td>"+
		                "<h3><a style=\"color: "+colorStatus+"\" class=\"task-link\" href='javascript:openOrder(\""+order.iuid+"\",\""+order.iter+"\")'>" + 
		                order.title + "</a><br><span><i class='icon-user' title='Автор'></i> " + order.from + 
		                "</span>  <span><i class='icon-calendar' title='Дата создания'> </i> " + order.inDate +
		                "</span> </td>"+status+"</tr>";
					} else {
						var href = type == 'my' || type == 'old' ? 'startOrderMy' : 'startOrderIn';
						html += "<tr id='o" + key + "'><td class='order urgen_" + order.urgency + "'></td>"+stop+"<td>"+
		                "<h3><a style=\"color: "+colorStatus+"\" class=\"task-link\" href='javascript:"+href+"(\""+order.iter+"\",\""+order.proc+"\",\""+key+"\")'>" + 
		                order.title + "</a><br><span><i class='icon-user' title='Автор'></i> " + order.from + 
		                "</span>  <span><i class='icon-calendar' title='Дата создания'> </i> " + order.inDate +
		                "</span> </td>"+status+"</tr>";
					}
				});
			});
		}
		html += "</table>";
		$('#ordersList_' + type).html(html);
		if (type == 'old') {
			$('#oldordersList_count').text(data.total);
			if(data.total == 0){
				$('#old_return').show();
			}
		}
		if (type == 'in') {
			if (infire > 0) {
				$('#ordersList_in_fire_count').show().text(infire);
			} else {
				$('#ordersList_in_fire_count').hide();
			}	
		}
		return;
	} else if (count > 0) {
		count--;
		setTimeout(function(){showOrdersLater(data, type, count);}, 800);
	}
}

function loadOrdersIn() {
	if (loadFirstTime[0]) {
		loadFirstTime[0] = false;
		loadingCount++;
		var url = window.mainUrl + "&sfunc&cls=XmlUtil&name=getOrders&arg0=1&rnd=" + rnd();
		$.post(url, function(data) {
			if (data) {
				checkData(data);
				showOrdersLater(data, 'in', 20);
			}
			loadingCount--;
		}, 'json');
	}
}

function loadOrdersOut() {
	if (loadFirstTime[1]) {
		loadFirstTime[1] = false;
		loadingCount++;
		var urlOut = window.mainUrl + "&sfunc&cls=XmlUtil&name=getOrdersOut&arg0=1&rnd=" + rnd();
		$.post(urlOut, function(data) {
			if (data) {
				checkData(data);
				showOrdersLater(data, 'out', 20);
			}
			loadingCount--;
		}, 'json');
	}
}

function loadOrdersMy() {
	if (loadFirstTime[2]) {
		loadFirstTime[2] = false;
		loadingCount++;
		var urlOut = window.mainUrl + "&sfunc&cls=XmlUtil&name=getMyOrders&arg0=1&rnd=" + rnd();
		$.post(urlOut, function(data) {
			if (data) {
				checkData(data);
				showOrdersLater(data, 'my', 20);
			}
			loadingCount--;
		}, 'json');
	}
}

function loadOrdersOld() {
	if (loadFirstTime[3]) {
		loadFirstTime[3] = false;
		loadingCount++;
		var urlOut = window.mainUrl + "&sfunc&cls=XmlUtil&name=getOldOrders&arg0=1&rnd=" + rnd();
		$.post(urlOut, function(data) {
			if (data) {
				checkData(data);
				showOrdersLater(data, 'old', 20);
			}
			loadingCount--;
		}, 'json');
	}
}

$("body").on('click', '#filterNoti', function(event) {
	filterOrdersNotification();
});

$("body").on('click', '#filterClean', function(event) {
	var dateN1 = $("#dateN1").datebox("getValue");
	var dateN2 = $("#dateN2").datebox("getValue");
	if (dateN1 != "" || dateN2 != "") {
		$("#dateN1").datebox("clear");
		$("#dateN2").datebox("clear");
	}
	$('#selectNoti option').prop('selected', function() {
        return this.defaultSelected;
    });
	$('#txtSearchPageNotification').val("");
	notificationSearch = 0;
	statusReadingNotification = 0;
	dateParseN1 = 0;
	dateParseN2 = 0;
	sortNotifColumn = "sortInDate_asc";
	$('#toolNotif td a').removeClass('sortNotif asc desc');
	$('#sortInDate').addClass('sortNotif').addClass('asc');
	loadNotification();
});

function loadNotification() {
	loadingCount++;
	var url = window.mainUrl + "&sfunc&cls=XmlUtil&name=getNewNotificationOrders&arg0=1&arg1=0&arg2="
	+notificationPageSize+"&arg3=0&arg4=0&arg5=0&arg6=0&arg7="+sortNotifColumn+"&rnd=" + rnd();
	$.post(url, function(data) {
		if(data.rows) {
			checkData(data);
			if (data.totalUnread > 0) $('.processesIN_counter').text(data.totalUnread);
			$('#pp').pagination({
			    total:data.total,
			    pageSize:notificationPageSize,
			    pageNumber: 1,
			    pageList: [10,50,100,150,200],
			    layout:['list','sep','first','prev','sep','links','sep','next','last','sep','manual'],
			    showPageList:true
			});
			$('#processesIN_counter').text(data.total);
			html = '<table width="100%" border="0">';
			$.each(data.rows, function(i, o) {
				$.each(o, function(key, order) {
					html += makeNotification(key, order);
				});
			});
			html += '</table>';
			$('#notiInList .pcontent').html(html);
		}
		loadingCount--;
	}, 'json');
}

function updateNotification(data) {
	$.each(data, function(i, o) {
		$.each(o, function(key, order) {
		var html = makeNotification(key, order);
		var id = key.indexOf(".");
		if ($('#notiInList .pcontent tr[id="' + key.substring(id+1) + '"]').length > 0) {
			var doOrder = $('#notiInList .pcontent tr[id="' + key.substring(id+1) + '"]').hasClass('notifacBold');
			$('#notiInList .pcontent tr[id="' + key.substring(id+1) + '"]').replaceWith(html);
			var beforeOrder = $('#notiInList .pcontent tr[id="' + key.substring(id+1) + '"]').hasClass('notifacBold');
			if(doOrder && !beforeOrder) {
				var tmp = $('.processesIN_counter').text();
				var count = (tmp.length > 0) ? parseInt(tmp) : 0;
				if (count > 1)	
					$('.processesIN_counter').text(count-1);
				else 
					$('.processesIN_counter').hide();
			}
		} else {
			var end = 0;
			var opts = $('#pp').pagination('options');
			var pageNu = opts.pageNumber;
			var pageSi = opts.pageSize;
			if(parseInt($('#processesIN_counter').text()) <= pageNu*pageSi)
				end = parseInt($('#processesIN_counter').text())
			else
				end = pageNu*pageSi;
			var dateN1 = $("#dateN1").datebox("getValue");
			dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
			var dateN2 = $("#dateN2").datebox("getValue");
			dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
			var search = $('#txtSearchPageNotification').val();
			if(search.length>1) {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":(pageNu-1) * pageSi,"arg2":end,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":sortNotifColumn};
				notificationSearch = search;
			} else {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":(pageNu-1) * pageSi,"arg2":end,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":sortNotifColumn};
				notificationSearch = 0;
			}
			post(par, parseNotifac2);
		}
		});
	});
}

function deleteNotificatin(uid) {
	var id = uid.indexOf(".");
	var order = $('#notiInList .pcontent #' + uid.substring(id+1));
	if (order.length > 0) {
		order.remove();
	}
	var end = 0;
	var opts = $('#pp').pagination('options');
	var pageNu = opts.pageNumber;
	var pageSi = opts.pageSize;
	if(parseInt($('#processesIN_counter').text()) <= pageNu*pageSi)
		end = parseInt($('#processesIN_counter').text())
	else
		end = pageNu*pageSi;
	var dateN1 = $("#dateN1").datebox("getValue");
	dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
	var dateN2 = $("#dateN2").datebox("getValue");
	dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
	var search = $('#txtSearchPageNotification').val();
	if(search.length>1) {
		var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":(pageNu-1) * pageSi,"arg2":end,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":sortNotifColumn};
		notificationSearch = search;
	} else {
		var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":(pageNu-1) * pageSi,"arg2":end,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":sortNotifColumn};
		notificationSearch = 0;
	}
	post(par, parseNotifac2);
}

$('#pp').pagination({
	onSelectPage:function(pageNumber, pageSize){
		blockPage();
		$(this).pagination('loading');
		var end = 0;
		if(parseInt($('#processesIN_counter').text()) <= pageNumber*pageSize)
			end = parseInt($('#processesIN_counter').text())
		else
			end = pageNumber*pageSize;
		notificationPageSize = pageSize;
		var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":(pageNumber-1) * pageSize,"arg2":end,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":notificationSearch,"arg6":statusReadingNotification,"arg7":sortNotifColumn};
		post(par, parseNotifac2);
		$(this).pagination('loaded');
	}
});

function filterOrdersNotification(){
	blockPage();
	var dateN1 = $("#dateN1").datebox("getValue");
	dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
	var dateN2 = $("#dateN2").datebox("getValue");
	dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
	var search = $('#txtSearchPageNotification').val();
	if(search.length>1) {
		var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":sortNotifColumn};
		notificationSearch = search;
	} else {
		var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":sortNotifColumn};
		notificationSearch = 0;
	}
	post(par, parseNotifac);
}

function sortColumnNotif(index) {
	blockPage();
	var dateN1 = $("#dateN1").datebox("getValue");
	dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
	var dateN2 = $("#dateN2").datebox("getValue");
	dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
	var search = $('#txtSearchPageNotification').val();
	if($('#'+index.id).hasClass('sortNotif')) {
		if($('#'+index.id).hasClass('asc')) {
			$('#'+index.id).removeClass('asc');
			$('#'+index.id).addClass('desc');
			sortNotifColumn = index.id+"_desc";
			if(search.length>1) {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":index.id+"_desc"};
				notificationSearch = search;
			} else {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":index.id+"_desc"};
				notificationSearch = 0;
			}
			post(par, parseNotifac);
		} else if($('#'+index.id).hasClass('desc')){
			$('#'+index.id).removeClass('desc');
			$('#'+index.id).addClass('asc');
			sortNotifColumn = index.id+"_asc";
			if(search.length>1) {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":index.id+"_asc"};
				notificationSearch = search;
			} else {
				var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":index.id+"_asc"};
				notificationSearch = 0;
			}
			post(par, parseNotifac);
		}
	} else {
		$('#toolNotif td a').removeClass('sortNotif asc desc');
		$('#'+index.id).addClass('sortNotif').addClass('asc');
		sortNotifColumn = index.id+"_asc";
		if(search.length>1) {
			var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":search,"arg6":statusReadingNotification,"arg7":index.id+"_asc"};
			notificationSearch = search;
		} else {
			var par = {"sfunc":1,"cls":"XmlUtil","name":"getNewNotificationOrders","arg0":"1","arg1":"0","arg2":notificationPageSize,"arg3":dateParseN1,"arg4":dateParseN2,"arg5":"0","arg6":statusReadingNotification,"arg7":index.id+"_asc"};
			notificationSearch = 0;
		}
		post(par, parseNotifac);
	}
}

function parseNotifac(data) {
	if(data.rows) {
		checkData(data);
		if (data.totalUnread > 0) $('.processesIN_counter').text(data.totalUnread);
		if(data.totalFilter != undefined) {
			$('#pp').pagination({
				total:data.totalFilter,
				pageNumber:1
			});
			$('#processesIN_counter').text(data.totalFilter+"/"+data.total);
		} else {
			$('#pp').pagination({
				total:data.total,
				pageNumber:1
			});
			$('#processesIN_counter').text(data.total);
		}	
		html = '<table width="100%" border="0">';
		$.each(data.rows, function(i, o) {
			$.each(o, function(key, order) {
				html += makeNotification(key, order);
			});
		});
		html += '</table>';
		$('#notiInList .pcontent').html(html);
	}
	$('body').unblock();
}

function parseNotifac2(data) {
	if(data.rows) {
		checkData(data);
		if (data.totalUnread > 0) $('.processesIN_counter').text(data.totalUnread);
		if(data.totalFilter != undefined) {
			$('#pp').pagination({
				total:data.totalFilter
			});
			$('#processesIN_counter').text(data.totalFilter+"/"+data.total);
		} else {
			$('#pp').pagination({
				total:data.total
			});
			$('#processesIN_counter').text(data.total);
		}	
		html = '<table width="100%" border="0">';
		$.each(data.rows, function(i, o) {
			$.each(o, function(key, order) {
				html += makeNotification(key, order);
			});
		});
		html += '</table>';
		$('#notiInList .pcontent').html(html);
	}
	$('body').unblock();
}

function deleteTask(flowId) {
	if (tasksCount > -1) {
		$('#tasksList .pcontent #' + flowId).remove();
		var task = $('#tasksList').find('.taskCheckedTr');
		if (deletingTasks && task.length == 0) {
			deletingTasks = false;
			$('body').unblock();
		}
		
		setTasksCount(tasksCount - 1);
	}
}

function pingServer() {
	var url = window.mainUrl + "&ping&rnd=" + rnd();
	var beginTime = (new Date).getTime();
	$.post(url, function(data) {
		var pingTime = (new Date).getTime() - beginTime;
		$('#ping').text('' + pingTime);
		var isShowCommitMessage = data.commitMessage;
		if (isShowCommitMessage != null && isShowCommitMessage == "1") {
			$.messager.confirm('', 'Сохранить введенные до текущего момента данные на сервер?!', function(e){
                if (e){
                	forseSaveChanges();
                }
            });
		}
	}, 'json');
}

//Сортировка задач по статусу
function sortOrdersbyStatus(a,b){
	$.each(a, function(x,y){
		OrdersStatus1 = y.status;
	});
	$.each(b, function(x,y){
		OrdersStatus2 = y.status;
	});		
	
	if (OrdersStatus1 < OrdersStatus2) return 1;
	if (OrdersStatus1 > OrdersStatus2) return -1;
	if (OrdersStatus1 == OrdersStatus2) return 0;
}

//Сортировка задач по дате контрол
function sortOrdersbyCtrl(a,b){
	$.each(a, function(x,y){
		OrdersCtrl1 = y.ctrl;
	});
	$.each(b, function(x,y){
		OrdersCtrl2 = y.ctrl;
	});
	if (OrdersCtrl1) {
		var date1 = new Date(OrdersCtrl1.replace(/(\d+).(\d+).(\d+)/, '$3/$2/$1'));
	}
	if (OrdersCtrl2) {
		var date2 = new Date(OrdersCtrl2.replace(/(\d+).(\d+).(\d+)/, '$3/$2/$1'));
	}
	if (date1 != undefined && date2 != undefined){
		if (date1 > date2) return 1;
		if (date1 < date2) return -1;
		if (date1 == date2) return 0;
	} else if (date1 != undefined && date2 == undefined){
		return -1;
	} else if (date1 == undefined && date2 != undefined){
		return 1;
	} else {
		return 0;
	}
}

function slide(msg) {
	$.messager.show({
		msg: msg,
		timeout: 8000,
		showType:'slide'
	});
}

function addTask(process) {
	if (tasksCount > -1) {
		var oldCount = tasksCount;
		$.each(process, function(key, task) {
			var html = makeTask(key, task);
			$('#tasksList .pcontent table').prepend(html);
			oldCount++;
		});
		setTasksCount(oldCount);
	}

	//$('.processes_counter').text($('#tasksList .pcontent tr').length);
	var search = $('#taskSearchPage');
	if (search && search.val() && search.val().length > 0){//не показывать задачи, которые не соответствуют отбору на мониторе 
		processCounter(search.val());
	}
}

function updateTask(process) {
	$.each(process, function(key, task) {
		var html = makeTask(key, task);
		if ($('#tasksList .pcontent tr[id="' + key + '"]').length > 0)
			$('#tasksList .pcontent tr[id="' + key + '"]').replaceWith(html);
	});
	//$('.processes_counter').text($('#tasksList .pcontent tr').length);
	var search = $('#taskSearchPage');
	if (search && search.val() && search.val().length > 0){//не показывать задачи, которые не соответствуют отбору на мониторе 
		processCounter(search.val());
	}
}

function makeTask(key, task) {
	var href = task.op=="1"?"#cmd=openTask&mnt=1&uid=" + key
			:task.op=="2"?"javascript:alert('"+translation['procPerformedMessage']+"');"
					:task.op=="3"?"javascript:alert('"+translation['rptGenerateMessage']+"');"
							:"javascript:alert('"+translation['ifcNotExistMessage']+"');";
	return '<tr id="' + key +'"><td><input type="checkbox" ' +(task.k == 1 ? 'class="task_check" task="'+key+'"' : 'disabled=""')
		+'/></td><td><span class="' + condition[task.c2] + '">&nbsp;</span></td'+(task.c3!=null?' style="background-color: '+task.c3+'"':'') +'><td><h3><a href="' + href
		+ '" class="task-link" task=' + key + '>' + task.p + '</a></h3>'
		+ '<h4><a href="' + href + '" class="task-link" task=' + key + '>' + task.t + '</a></h4>' + task.o
		+ '</td><td><span class="dt_ label ' + orderStatus[task.c1] + '"><i class="icon-time icon-white"></i> ' 
		+ task.d + '</span><br><span><i class="icon-user"></i> ' + task.i 
        + '</span></td></tr>';
}

function makeNotification(key, order) {
	var notic = order.openDate == "" ? "notifacBold"  : "";
	var func = order.iter == '' ? "href='javascript:openOrder(\""+order.proc+"\",\""+key+"\")'" : "href='javascript:startOrderIn(\""+order.iter+"\",\""+order.proc+"\",\""+key+"\")'";
	var id = key.indexOf(".");
	return "<tr id='" + key.substring(id+1) + "' class='"+notic+"'><td width='50%'>"+
    "<a class=\"noti-link\" "+func+">" + order.title + "</a></td><td width='20%'><i class='icon-user' title='Автор'></i> " + order.from + 
    "</td><td width='10%'><i class='icon-calendar'></i>" + order.inDate +
    "</td><td width='10%'><i class='icon-calendar'></i>" + order.openDate + "</td><td width='10%'><i class='icon-calendar'></i>" + order.awereDate +
    "</td></tr>";
}

function loadSessions() {
	loadingCount++;
	$.get(window.mainUrl + "&cmd=loadSessions", function(data) {
		checkData(data);
		html = '<table width="100%" border="0">';
		var processList = data.processes;
		$('#countSessions').text(processList.length);
		$.each(processList, function(i, process) {
			$.each(process, function(key, task) {
				html += '<tr><td><h3>' + task.p + '</h3>'
					+ '<h4>' + task.t + '</h4>' + task.o 
					+ '</td><td><span class="dt_ label"><i class="icon-time icon-white"></i> ' 
					+ task.d + '</span></td><td><span class="icon-cancel" uuid="' + key + '"' + '>&nbsp;</span></td></tr>';
			});
		});
		html += '</table>';
		$('#sessionsList .pcontent').html(html);
		loadingCount--;
	}, 'json');
}

function loadProccessCount() {
	loadingCount++;
	$.get(window.mainUrl + "&cmd=loadTasksCount&rnd=" + rnd(), function(data) {
		checkData(data);
		$('.processes_counter').text(data.total);
		loadingCount--;
	}, 'json');
}

function filterProcc(pid, parent) {
	$('.proc').show();
	$('.proc[parentproc!="' + pid + '"]').hide();
}

function loadProccessList() {
	$('#processesList_Layout').layout();
	var allProcesses_Layout = document.getElementById('allProcesses_Layout');	
	$('#processTree').tree({
		onClick: function(node){
			$.post(window.mainUrl+"&cmd=getProcessData&leaf", {id:node.id}, function (data){
				$('#processesList_body').html("");
				$.each(data, function(e,proc) {
					funcName = "startProcc";
					var u = proc.id.indexOf(".");
					var key = proc.id.substring(u+1);
					var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
					$('#processesList_body').append("<div id='process" + key + "' class='ico_" + proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
					addProcIcon('#processesList_body .proc.ico_' + proc.id.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+'.png');
					if (proc.time != null) {
						$('#process'+key).tooltip({
					           position: 'bottom',
					           content: translation['lastSuccesProcessDef']+'<br>'+proc.time,
					           onShow: function(){
					               $(this).tooltip('tip').css({
					                   backgroundColor: '#fafad2',
					                   borderColor: '#666'
					               });
					           }
					    });
					}
					if (allProcesses_Layout) {
						$('#process'+key).bind('contextmenu', function(e) {
							e.preventDefault();
							selectedProc = proc.id;
							$('#procMenu').menu('show', {
								left: e.pageX,
								top: e.pageY
							});
						});
					}
				});
			}, 'json');
		}
	});
}
var lastFavoriteProcessesCount = 0;
function loadProccessList2() {
	var allProcesses_Layout = document.getElementById('allProcesses_Layout');	
	if (allProcesses_Layout) {
		$('#processesList_Layout2').layout();
		$.post(window.mainUrl+"&cmd=getFavoriteProcesses", {}, function (data) {
			$('#processesList_body2').html("");
			$.each(data, function(e,proc) {
				funcName = "startProcc";
				var u = proc.id.indexOf(".");
				var key = proc.id.substring(u+1);
				var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
				$('#processesList_body2').append("<div id='processFav" + key + "' class='ico_" + proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
				addProcIcon('#processesList_body2 .proc.ico_' + proc.id.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+'.png');
				if (proc.time != null) {
					$('#processFav'+key).tooltip({
				           position: 'bottom',
				           content: translation['lastSuccesProcessDef']+'<br>'+proc.time,
				           onShow: function(){
				               $(this).tooltip('tip').css({
				                   backgroundColor: '#fafad2',
				                   borderColor: '#666'
				               });
				           }
				    });
				}
				$('#processFav'+key).bind('contextmenu', function(e) {
					e.preventDefault();
					selectedFavProc = proc.id;
					$('#procMenu2').menu('show', {
						left: e.pageX,
						top: e.pageY
					});
				});
			});
			if (data.length == 0) {
//				$('.portletProc').css('height', ($('.portletProc').height() + 206)+"px");
				$('#allProcesses_Layout').layout('collapse','north');
			} else if (data.length > 0 && lastFavoriteProcessesCount == 0) {
//				$('.portletProc').css('height', ($('.portletProc').height() - 206)+"px");
				$('#allProcesses_Layout').layout('expand','north');
			}
			lastFavoriteProcessesCount = data.length;
		}, 'json');
	}
}

function loadArchList() {
	$('#archList_Layout').layout();
	$('#archiveTree').tree({
		onClick: function(node){
			$.post(window.mainUrl+"&cmd=getArchiveData&leaf", {id:node.id}, function (data){
				$('#archList_body').html("");
				$.each(data, function(e,proc) {
					funcName = "startArch";
					$('#archList_body').append("<div class='ico_" 
							+ proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ");
					/* disabled for archive icons
					 * addProcIcon('#archList_body .proc.ico_' + proc.id.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+'.png');
					 */	
				});
			}, 'json');
		}
	});
}

function loadDictsList() {
	$('#dictsList_Layout').layout();
	$('#dictsTree').tree({
		onClick: function(node){
			$.post(window.mainUrl+"&cmd=getDictData&leaf", {id:node.id}, function (data){
				$('#dictsList_body').html("");
				$.each(data, function(e,proc) {
					funcName = "startDict";
					$('#dictsList_body').append("<div class='ico_" 
							+ proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ");
					 /* disabled for dicts icons
					  * addProcIcon('#dictsList_body .proc.ico_' + proc.id.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+'.png');
					  */	
				});
			}, 'json');
		}
	});
}

function loadAdminList() {
	$('#adminsList_Layout').layout();
	$('#adminsTree').tree({
		onClick: function(node){
			$.post(window.mainUrl+"&cmd=getAdminData&leaf", {id:node.id}, function (data){
				$('#adminsList_body').html("");
				$.each(data, function(e,proc) {
					funcName = "startAdmin";
					$('#adminsList_body').append("<div class='ico_" 
							+ proc.id.replace('.', '_') + " proc' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ");
				});
			}, 'json');
		}
	});
}

function buildList(process, parent, funcName) {
	var list = "";
	if (funcName == undefined) 
		funcName = "startProcc";
	if (process.children!=undefined) {
	$.each(process.children, function(c, proc) {
		vv = 0;
		if (proc.children && proc.children.length > 0) {
			window.filterList += "<a class='easyui-linkbutton' data-options=\"toggle:true,group:'"+ parent.replace('.', '_')+"'\" href='#' onclick='filterProcc(\"" + proc.uid + "\",\"" + parent + "\")'>" + proc.title + "</a> ";
		} else {
			list += "<div class='ico_" + proc.uid.replace('.', '_') + " proc' parentProc='" + parent + "' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ";
			addProcIcon('#processesList .proc.ico_' + proc.uid.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.uid.replace('.', '_')+'.png');
		}
		if (proc.children && proc.children.length > 0) {
			list += buildList(proc, proc.uid, funcName);
		}
		list += "";
	});
	list += "";
	}
	return list;
}

function buildTabs(process, parent, funcName) {
	var list = "";
	if (funcName == undefined) 
		funcName = "startProcc";
	if (process.children!=undefined) {
	$.each(process.children, function(c, proc) {
		vv = 0;
		if (proc.children && proc.children.length > 0) {
			window.filterList += "<a class='easyui-linkbutton' data-options=\"toggle:true,group:'"+ parent.replace('.', '_')+"'\" href='#' onclick='filterProcc(\"" + proc.uid + "\",\"" + parent + "\")'>" + proc.title + "</a> ";
		} else {
			list += "<div class='ico_" + proc.uid.replace('.', '_') + " proc' parentProc='" + parent + "' onclick='javascript:"+funcName+"(\"" + proc.id + "\")'><a>" + proc.title + "</a></div> ";
			addProcIcon('#processesList .proc.ico_' + proc.uid.replace('.', '_'),window.contextName + '/jsp/media/css/or3/'+proc.uid.replace('.', '_')+'.png');
		}
		if (proc.children && proc.children.length > 0) {
			list += buildList(proc, proc.uid, funcName);
		}
		list += "";
	});
	list += "";
	}
	return list;
}

function addProcIcon(className, src) {
	var img = new Image();
	img.onload = function() {
		addStyle(className+' {background-image: url('+src+') !important}');
	};
	img.src = src;
}

var processStartWait = "<h1><div id='wait_canvas'></div>" + translation['wait'] + "</h1>";
var dialogOpened = null;

function openStartDialog(uid) {
	blockPage();
	var par = {};
	par["uid"] = uid;
	par["cmd"] = "openTask";
	par["size"] = 1;
	
	$.post(window.mainUrl + "&rnd=" + rnd(), par, function(data) {
		checkData(data);
		if (data.result == 'error') {
			alertError(data.message);
		} else {
			blockPage();
			window.selectedRow = [];
			var dialogId = 'or3_popup' + window.popupcount;
			$('#trash').append($("<div></div>").attr('id', dialogId));
			
			window.dialogResult[dialogId] = '1';
			dialogOpened = dialogId;
			
			$('#' + dialogId).dialog({
				title: '<div style="font-size:14px; color:#777;">'+data.t+'</div>',
				width: data.w,
				height: data.h,
				closed: false,
				cache: false,
				href: window.mainUrl + '&cmd=openTask&uid=' + uid + "&rnd=" + rnd(),
				modal: true,
				uid: uid,
				onOpen: function() {
					$('#' + dialogId +' .panel').append($("<div class=\"glassDialog\"></div>"));
					$('#' + dialogId + ' .glassDialog').height($('#' + dialogId +' .panel').height()).width($('#' + dialogId +' .panel').width());
				},
				onLoad: function() {
					// Добавляем ID-окна в массив окон
					window.openedDialogs.push(dialogId);
					loadData({}, true);
					
					$('.clean-btn').click(function(e) {
						var par = {};
						par["uid"] = $(this).attr('id').substring(3);
						par["cmd"] = "clr";
						postAndParseData(par);
						return false;
					});
					
					preparefileUpload();
				},
				onClose: function() {
					window.dialogResult[dialogId] = '1';
					$('#' + dialogId).dialog('destroy');
				},
				onBeforeDestroy : function() {
					// Удаляем ID-окна из массива окон
					window.openedDialogs.pop();
					dialogOpened = null;
					
					if (window.dialogResult[dialogId] == '1' || window.dialogResult[dialogId] == 1) {
						cancelStart(uid);
					}
				},
				buttons: [{
					text: translation['ok'],
					handler: function() {
						setDialogBtnsEnabled(dialogId, false);
						blockPage();
						window.dialogResult[dialogId] = '0';
						nextStep(true, dialogId);
					}
				},
				{
					text: translation['close'],
					handler: function() {
						setDialogBtnsEnabled(dialogId, false);
						$('body').unblock();
						window.dialogResult[dialogId] = '1';
						$('#' + dialogId).dialog('destroy');
					}
				}]
			});
			window.popupcount++;
		}
	}, 'json');
}

function startProcc(pid) {
	blockPage();
	$.get(window.mainUrl + "&cmd=startProcess&uid=" + pid + "&rnd=" + rnd(), function(data) {
		showProcessUI(data);
	}, 'json');
}

function showProcessUI(data) {
	if (data.result == "success") {
		if (data.mode && data.mode=="dialog") {
			openStartDialog(data.uid);
		} else if (data.mode && data.mode=="no") {
			toActiveMain();
			$('body').unblock();
		} else if (data.uid) {
			if (document.location.hash.indexOf("cmd=openTask&uid=" + data.uid) == -1)
				document.location.hash = "cmd=openTask&uid=" + data.uid + "&rnd=" + rnd();
		} else {
			toActiveMain();
			$('body').unblock();
		}
	} else {
		var message = data.message;
		var unblock = true;
		if (message) {
			var flowIdText = "ID потока: ";
			var index = message.indexOf(flowIdText);
			if (index > 0) {
				var flowId = message.substring(index + flowIdText.length, message.length - 1);
				message = message.substring(0, index) + " Открыть раннее запущенный процесс?";
				$.messager.confirm('', message, function(e) {
					if (e) {
						document.location.hash = "cmd=openTask&uid=" + flowId + "&rnd=" + rnd() + "&isPrevProc=1";
						unblock = false;
					}
				}); 
			} else {
				alert(message);
			}
		} else {
			alertError(translation['error']);
		}
		if (unblock)
			$('body').unblock();
	}
}

function startArch(pid) {
	document.location.hash = "cmd=openArch&uid=" + pid;
}

function startDict(pid) {
	document.location.hash = "cmd=openDict&uid=" + pid;
}

function startAdmin(pid) {
	document.location.hash = "cmd=openAdmin&uid=" + pid;
}

function openOrder(iuid,ouid) {
	document.location.hash = "cmd=openOrder&iuid=" + iuid+"&ouid=" + ouid;
}

function parseData(data) {
	if (data.changes) {
		$.each(data.changes, function(i, c) {
			$.each(c, function(i, props) {
				var comp = null;
				// Пробегаемся по открытым диалогом в обратном порядке и ищем компонент
				for (var di = window.openedDialogs.length - 1; di >= 0; di--) {
					var dialog = $('#' + window.openedDialogs[di]);
					comp = dialog.find('#' + i);
					// Если компонент найден - выходим из цикла
					if (comp.length > 0) break; 
				}
				// Если компонент не найден в диалогах - ищем по всему окну
				if (comp == null || comp.length == 0) comp = $('#' + i);
				
				$.each(props, function(key, value) {
					e_tagName = comp.prop("tagName");
					e_type = comp.attr("type");
					if (key == 'st') {
						
						delete value['min-width']; // for future may be fixed
						delete value['height'];
						delete value['min-height'];
						delete value['margin'];
						if (e_tagName=='TABLE') {
							delete value['margin-right'];
							delete value['margin-left'];
							delete value['margin-bottom'];
							delete value['margin-top'];
							delete value['width'];
						}
						if (value['background-color'] != null && value['background-color'].indexOf("background-color")>-1) {
							value['background-color'] = value['background-color'].replace("background-color","");
						}
						if (e_tagName == 'BUTTON' || e_tagName == 'A' || e_tagName=="TEXTAREA") {
						} else if(e_tagName == 'INPUT' && e_type=="file") {
							comp.parent().css(value);
						}else if(e_tagName == 'INPUT') {
							if (comp.hasClass('easyui-combobox')) {
								if (!comp.hasClass('combobox-f')) comp.combobox();
								$.each(value, function (akey,aval){
									if (akey == "margin-top"){
										comp.next().css("margin-top",aval);
									} else if (akey=="margin-bottom"){
										comp.next().css("margin-bottom",aval);
									} else if (akey=="margin-left"){
										comp.next().css("margin-left",aval);
									} else if (akey=="margin-right"){
										comp.next().css("margin-right",aval);
									} else if (akey == "font-family") {
										comp.combobox("textbox").css("font-family", aval);
									} else if (akey == "font-size") {
										if (aval != 0) {
										comp.combobox("textbox").css("font-size", aval);
										}
									} else if (akey == "font-style") {
										if (aval == 1) comp.combobox("textbox").css("font-weight","bold");
										if (aval == 2) comp.combobox("textbox").css("font-style","italic");
										if (aval == 3) comp.combobox("textbox").css("font-style","italic").css("font-weight","bold");
									}
								});
								
							}
						} else {
							comp.css(value);
						}
					} else if (key == 'pr') {
						var reloadRows = undefined;
						var comboValue = undefined;
						$.each(value, function(akey, aval) {
							if (akey == "heads") return;
							if (e_tagName == "SELECT" && akey == "size") return;
							if (e_tagName == "TEXTAREA" && akey == "size") return;
							if (e_tagName == "TABLE" && (akey == "height" || akey == "width") ) return;
							if (e_tagName == 'INPUT' && e_type=="file" && akey != "e" && akey != "v" && akey != "text" && akey != 'OrWebDocFieldProps' && akey != "reloadUpload") return;
							if (akey == 'text') {
								if (aval != null && $.trim(aval).length > 0) {
									delErrorType(comp);
								}
								if (e_tagName == 'INPUT' && e_type == 'radio') {
									$('input[type=radio][value="' + aval + '"]').prop('checked', true);
								} else if (e_tagName == 'INPUT' && e_type=="checkbox") {
									// Следующий элемент
									var ne = comp[0].nextSibling;
									if (ne == null || ne.nodeType != 3) {
										comp.after(document.createTextNode('-'));
										ne = comp[0].nextSibling;
									}
									ne.nodeValue = aval;
								} else if (e_tagName == 'INPUT' && e_type=="file") {
									comp.parent().find('span.btn-label').html(aval.replace(/@/g,'<br/>'));
								} else if (e_tagName == 'BUTTON') {
									$('#' + i+' span').html(aval.replace(/@/g,'<br/>'));
								} else if (e_tagName == 'INPUT') {
									comp.val(aval);
									if (comp.hasClass('easyui-datebox')) {
										if (!comp.hasClass('datebox-f')) comp.datebox();
										comp.datebox('setValue', aval); 
									} 
									else if (comp.hasClass('easyui-datetimebox')) {
										if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
										comp.datetimebox('setValue', aval); 
									}
									else if (comp.hasClass('easyui-numberbox')) {
										comp.numberbox('setValue', aval);
									}
								} else if (e_tagName == 'TEXTAREA') {
									comp.val(aval);
								} else if (comp.hasClass('easyui-linkbutton')) {
									comp.linkbutton({text:aval});
								} else if (e_tagName == 'A') {
									if (aval != null && aval.replace) {
										if (comp.hasClass("hyper")) {
											comp.html(aval.replace(/@/g,'<br/>'));
										} else if (comp.hasClass("treeField")) {
											while (comp.contents().filter(function() {return this.nodeType == 3;}).length > 0) {
												comp.contents().filter(function() {return this.nodeType == 3;}).eq(0).replaceWith('');
											}
											comp.find('br').remove();
											comp.append(document.createTextNode('-'));
											comp.contents().filter(function() {return this.nodeType == 3;}).eq(0).replaceWith(aval.replace(/@/g,'<br/>'));
										} else {
											comp.find('span.btn-label').html(aval.replace(/@/g,'<br/>'));
										}
									}
								} else {
									if (aval != null && aval.replace) {
										comp.html(aval.replace(/@/g,'<br/>'));
									}
								}
							} else if (akey == 'OrWebDocFieldProps') {
								comp.parent().find('span.btn-label').html(aval.text.replace(/@/g,'<br/>'));
								var spanElement = document.getElementById(aval.id).parentElement;
								if (aval.mode == 0) {
									spanElement.children[1].removeAttribute("id");
									spanElement.children[1].removeAttribute("action");
									spanElement.children[1].className = "docField";
									document.getElementById(aval.id).style.display  = "block";
								} else {
									document.getElementById(aval.id).style.display  = "none";
									spanElement.children[1].setAttribute("id", aval.id);
									spanElement.children[1].setAttribute("action", "0");
									spanElement.children[1].className = "or3-btn view-file trBtn";
								}
								if (aval.iconBytes.length > 0) {
									spanElement.children[0].style.display  = "inline";
									spanElement.children[0].src = "data:image/png;base64," + aval.iconBytes; 
								} else {
									spanElement.children[0].style.display  = "none";
								}
							} else if (akey == 'rootPanelTitle') {
								$("#uiTitle").text(aval);
								$(".fullPath-l").text(aval);
							} else if (akey == "hyperPopupAttention") {
								if (aval == 1) {
									attentionMap[i] = window.setInterval(function() {
										if (document.getElementById(i).style.backgroundColor == "palegreen") {
											document.getElementById(i).style.background  = "initial";
										} else {
											document.getElementById(i).style.background  = "palegreen";
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									document.getElementById(i).style.background  = "initial";
								}
							} else if (akey == "buttonAttention") {
								if (aval == 1) {
									attentionMap[i] = window.setInterval(function() {
										if (document.getElementById(i).style.backgroundColor == "palegreen") {
											document.getElementById(i).style.background  = "initial";
										} else {
											document.getElementById(i).style.background  = "palegreen";
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									document.getElementById(i).style.background  = "initial";
								}
							} else if (akey == "dateFieldAttention") {
								var tb = $('#' + i).datebox('textbox');
								if (aval == 1) {
									attentionMap[i] = window.setInterval(function() {
										var color = tb.add(tb.parent()).css("background-color");
										if (color == "rgb(152, 251, 152)") {
											tb.add(tb.parent()).css("background-color","white");
										} else {
											tb.add(tb.parent()).css("background-color","palegreen");
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									tb.add(tb.parent()).css("background-color","white");
								}
							} else if (akey == "textFieldAttention") {
								if (aval == 1) {
									attentionMap[i] = window.setInterval(function() {
										if (document.getElementById(i).style.backgroundColor == "palegreen") {
											document.getElementById(i).style.background  = "initial";
										} else {
											document.getElementById(i).style.background  = "palegreen";
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									document.getElementById(i).style.background  = "initial";
								}
							} else if (akey == "comboBoxAttention") {
								var tb = $('#' + i).combobox('textbox');
								if (aval == 1) {
									attentionMap[i] = window.setInterval(function() {
										var color = tb.add(tb.parent()).css("background-color");
										if (color == "rgb(152, 251, 152)") {
											tb.add(tb.parent()).css("background-color","white");
										} else {
											tb.add(tb.parent()).css("background-color","palegreen");
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									tb.add(tb.parent()).css("background-color","white");
								}
							} else if (akey == "docFieldColumnAttention") {
								var dg = $('#' + aval.parent);
								var td = dg.datagrid('getPanel').find('div.datagrid-header td[field="' + i + '"]');
								if (aval.value == 1) {
									attentionMap[i] = window.setInterval(function() {
										var color = td.css('background-color');
										if (color == "rgb(152, 251, 152)") {
											td.css('background-color','initial');
										} else {
											td.css('background-color','palegreen');
										}
									}, 1000);
								} else {
									clearInterval(attentionMap[i]);
									delete attentionMap[i];
									td.css('background-color','initial');
								}
							} else if (akey == "columnTitle") {
								var dg = $('#' + aval.parent);
								var span = dg.datagrid('getPanel').find('div.datagrid-header td[field="' + i + '"] div.datagrid-cell span:not(.datagrid-sort-icon)');
								span.html(aval.value);
							} else if (akey == "accordionPanelsVisible") {
								$.each(aval, function(i, c1) {
									$.each(c1, function(j, c2) {
										var visible = c2.visible;
										var spanElementId = c2.spanElementId;
										var divElementId = c2.divElementId;
										var spanElement = document.getElementById(spanElementId);
										var divElement = document.getElementById(divElementId);
										if (visible == 0) {
										    if ($('#' + divElementId).hasClass('expanded')) {
										    	$('#' + divElementId).removeClass('expanded');
										    }
											spanElement.style.visibility = "hidden";
											spanElement.style.height = "0px";
											divElement.style.visibility = "hidden";
										} else {
											spanElement.style.visibility = "visible";
											spanElement.style.height = "14px"
											divElement.style.visibility = "visible";
										}
									});
								});
							} else if (akey == "accordionPanelDynTitle") {
								var elementId = "t" + aval.index + i;
								var element = document.getElementById(elementId);
								element.textContent = aval.title;
							} else if (akey == 'checked') {
								if (e_tagName == 'INPUT' && e_type == 'checkbox') {
									if(aval){
										comp.prop('checked', true);
									} else {
										comp.prop('checked', false);
									}
								}
							} else if (akey == 'value') {
								if (aval != null && $.trim(aval).length > 0) {
									delErrorType(comp);
								}
								if (e_tagName == 'INPUT' ) {
									comp.val(aval);
									if (comp.hasClass('easyui-datebox')) {
										if (!comp.hasClass('datebox-f')) comp.datebox();
										comp.datebox('setValue', aval); 
									} else if (comp.hasClass('easyui-datetimebox')) {
										if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
										comp.datetimebox('setValue', aval); 
									} else if (comp.hasClass('easyui-combobox')) {
										if (!comp.hasClass('combobox-f')) comp.combobox();
										comp.combobox('setValue', aval);
									} else if (comp.hasClass('easyui-numberbox')) {
										comp.numberbox('setValue', aval);
									}
								}
							} else if (akey == 'content') {
								if (comp.hasClass('easyui-combobox')) {
									if (!comp.hasClass('combobox-f')) comp.combobox();
									var options = [];
									window.hintOptions[i] = {};
									var j = 0;
									$.each(aval, function(ok, ov) { // select
										// options
										var ti = i + '-title';
										options[j] = {};
										options[j][ti] = ov.o;
										options[j++][i] = ov.u;
										window.hintOptions[i][ov.u] = ov.h;
									});
									comp.combobox('loadData', options);
								} else if(e_tagName == "SELECT") {
									var options = '';
									$.each(aval, function(ok, ov) { // select
										// options
										options += '<option value="' + ok + '">' + ov.o + '</option>';
									});
									comp.html(options);
								}
							} else if (akey == 'fFam'){
								if (e_tagName == "SELECT") {
									comp.children('option').css("font-family",aval);
								}
							} else if (akey == 'fSize'){
								if (e_tagName == "SELECT") {
									comp.children('option').css("font-size",aval);
								}
							} else if (akey == 'fStyle'){
								if (e_tagName == "SELECT") {
									if (aval == 1) comp.children('option').css("font-weight","bold");
									if (aval == 2) comp.children('option').css("font-style","italic");
									if (aval == 3) comp.children('option').css("font-style","italic").css("font-weight","bold");
								}
							} else if(akey == 'nocopy'){
								var showComp = comp;
								
								var Table = showComp.hasClass("easyui-datagrid");
								if(aval == 0) {
									if(Table){comp.prev().children(".datagrid-body").attr("oncopy","return false").attr("ondragstart","return false");}
								} else if(aval == 1){
									if(Table){comp.prev().children(".datagrid-body").removeAttr("oncopy").removeAttr("ondragstart");}
								}
							} else if (akey=='change') {
						    	delErrorType(comp);
						    	comboValue = aval;
							} else if (akey == "img" && !comp.hasClass('staticImg')) {
								if (aval.src)
									comp.attr("src", window.contextName + "/images/foto/" + aval.src);
								else
									comp.attr("src", window.contextName + "/jsp/media/img/nofoto.png");
							} else if (akey == "e") {
								if (aval == 0) {
									if (comp.hasClass('treeField')||comp.hasClass('popup')){
										if($('#clr' + i)){
											$('#clr' + i).hide();
										}
									}
									if (e_tagName == 'INPUT' && comp.hasClass('easyui-datebox')) {
										if (!comp.hasClass('datebox-f')) comp.datebox();
										comp.datebox('disable');
										
										var fld = comp.parent().find('.textbox-text');
										var cal = comp.parent().find('.textbox-addon');
										fld.removeAttr("disabled").attr("readonly", "readonly");
										
										var hidePanel = comp.attr('hidePanel') == 'true';
										if (!hidePanel && cal.css('display') != "none") {
											cal.hide();
											fld.width(fld.width() + 18);
										}
									} else if (e_tagName == 'INPUT' && comp.hasClass('easyui-datetimebox')) {
										if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
										comp.datetimebox('disable');

										var fld = comp.parent().find('.textbox-text');
										var cal = comp.parent().find('.textbox-addon');
										fld.removeAttr("disabled").attr("readonly", "readonly");
										
										var hidePanel = comp.attr('hidePanel') == 'true';
										if (!hidePanel && cal.css('display') != "none") {
											cal.hide();
											fld.width(fld.width() + 18);
										}
									} else if(e_tagName == 'INPUT' && comp.hasClass('easyui-combobox')){
										if(!comp.hasClass('combobox-f')) comp.combobox();
										comp.combobox('disable');
										comp.parent().find('.textbox-text').attr("disabled","disabled").attr("readonly", "readonly");
									} else if (e_tagName == 'INPUT' && e_type == 'file') {
										comp.attr("disabled", "disabled").attr("readonly", "readonly");
										comp.parent().addClass('btn-disabled');
									} else if (comp.hasClass('easyui-numberbox')) {
										comp.numberbox("readonly", true);
									} else if ((e_tagName == "INPUT" && e_type == 'text') || e_tagName == "TEXTAREA") {
										comp.attr("readonly", "readonly");
									} else if (comp.attr('wysiwyg') == '1' || comp.attr('wysiwyg') == 'true' || comp.attr('contentEditable') == 'true') {
										var tarea = $('textarea[id=' + i + ']');
										if (tarea.length > 0) {
											nicEditors.findEditor(tarea.get(0)).disable();
										}
									} else if (e_tagName == 'INPUT' && e_type == 'radio') {
										comp.parents('table:first').find('input[type=radio]').attr("disabled", "disabled").attr("readonly", "readonly");
									} else if (e_tagName == "INPUT" || e_tagName == "BUTTON" || e_tagName == "SELECT") {
										comp.attr("disabled", "disabled").attr("readonly", "readonly");
									} else if (e_tagName == "A") {
										comp.addClass('btn-disabled');
									} else if (e_tagName == "IMG") {
										comp.unbind('contextmenu');
									}
								} else if (aval == 1) {
									if (comp.hasClass('treeField')||comp.hasClass('popup')){
										if($('#clr' + i)){
											$('#clr' + i).show();
										}
									}
									if (e_tagName == 'INPUT' && comp.hasClass('easyui-datebox')) {
										if (!comp.hasClass('datebox-f')) comp.datebox();
										comp.datebox('enable');
										
										var fld = comp.parent().find('.textbox-text');
										var cal = comp.parent().find('.textbox-addon');
										fld.removeAttr("readonly");

										var hidePanel = comp.attr('hidePanel') == 'true';
										if (!hidePanel && cal.css('display') == "none") {
											cal.show();
											fld.width(fld.width() - 18);
										}
									} else if (e_tagName == 'INPUT' && comp.hasClass('easyui-datetimebox')) {
										if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
										comp.datetimebox('enable');

										var fld = comp.parent().find('.textbox-text');
										var cal = comp.parent().find('.textbox-addon');
										fld.removeAttr("readonly");

										var hidePanel = comp.attr('hidePanel') == 'true';
										if (!hidePanel && cal.css('display') == "none") {
											cal.show();
											fld.width(fld.width() - 18);
										}
									} else if(e_tagName == 'INPUT' && comp.hasClass('easyui-combobox')){
										if(!comp.hasClass('combobox-f')) comp.combobox();
										comp.combobox('enable');
										comp.parent().find('.textbox-text').removeAttr("disabled").removeAttr("readonly");
									} else if (e_tagName == 'INPUT' && e_type == 'file') {
										comp.removeAttr("disabled").removeAttr("readonly");
										comp.parent().removeClass('btn-disabled');
									} else if (comp.hasClass('easyui-numberbox')) {
										comp.numberbox("readonly", false);
									} else if ((e_tagName == "INPUT" && e_type == 'text') || e_tagName == "TEXTAREA") {
										comp.removeAttr("readonly");
									} else if (comp.attr('wysiwyg') == '1' || comp.attr('wysiwyg') == 'true' || comp.attr('contentEditable') == 'false') {
										var tarea = $('textarea[id=' + i + ']');
										if (tarea.length > 0) {
											nicEditors.findEditor(tarea.get(0)).elm.setAttribute('contentEditable', 'true');
										}
									} else if (e_tagName == 'INPUT' && e_type == 'radio') {
										comp.parents('table:first').find('input[type=radio]').removeAttr("disabled").removeAttr("readonly");
									} else if (e_tagName == "INPUT" || e_tagName == "BUTTON" || e_tagName == "SELECT") {
										comp.removeAttr("disabled").removeAttr("readonly");
									} else if (e_tagName == "A") {
										comp.removeClass('btn-disabled');
									} else if (e_tagName == "IMG") {
										comp.bind('contextmenu', function(e) {
											e.preventDefault();
											$('#mm' + i).menu('show', {
												left: e.pageX,
												top: e.pageY
											});
										});
									}
								}
							} else if (akey == "ne") {
								// button enable for navi
								if (aval.length) {
									$.each(aval, function(nbi, nb) {
										var actionId = nb.actionId;
										var compb = $('#' + actionId);
										if (nb.e == 1)
											enableNaviBtn(compb, true);
										else
											enableNaviBtn(compb, false);
									});
								} else {
									var actionId = aval.actionId;
									var compb = $('#' + actionId);
									if (aval.e == 1)
										enableNaviBtn(compb, true);
									else
										enableNaviBtn(compb, false);
								}
							} else if (akey == "navi") { // toolbar for datagrid
								var toolbar = [];
								var btns='';
								$.each(aval.pr.toolBar.buttons, function(bi, btn) {
									if (btn.button && btn.menu){
									      toolbar.push({
									        iconCls:'icon-'+  btn.button.pr.actionId// ,
										 });
                                       addStyle('.icon-'+  btn.button.pr.actionId+' {background: url("'+btn.button.pr.img.src+'")  no-repeat scroll center center / cover  transparent; height:26px;}');
									}else if (btn.pr && btn.pr.img && btn.pr.img.src != "separator.png") {
									  if (btn.pr.img.src.indexOf('addNavi')>0) {
									   toolbar.push({
										    text: btn.pr.textInfo,
											iconCls: btn.pr.img.bytes ? 'icon-'+btn.pr.actionId : 'icon-add',
                                            id:btn.pr.actionId,
                                            disabled:btn.pr.e==0,
                                            handler:function(){
                                                dgBtnAction('add',i);
                                            }
										});
									    if (btn.pr.img.bytes) {
											addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
										}
										btns += '<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain';
										
										if (btn.pr.e==0)
											btns += ' l-btn-disabled l-btn-plain-disabled';

										btns += '" group="" id='+btn.pr.actionId+' onclick="dgBtnAction2(\'add\',\''+i+'\', $(this))">'
											+'<span class="l-btn-left l-btn-icon-left">'
											+'<span class="l-btn-text l-btn-empty">&nbsp;</span>'
											+'<span class="l-btn-icon icon-add">&nbsp;</span></span>'
											+'</a></td>';
									  } else if (btn.pr.img.src.indexOf('delNavi')>0) {
									      toolbar.push({
										    text: btn.pr.textInfo,
									        iconCls: btn.pr.img.bytes ? 'icon-'+btn.pr.actionId : 'icon-remove',
                                            id:btn.pr.actionId,
                                            disabled:btn.pr.e==0,
                                            handler:function(){
                                                dgBtnAction('del',i);
                                            }
										});
									    if (btn.pr.img.bytes) {
											addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
										}
										btns += '<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain';
										
										if (btn.pr.e==0)
											btns += ' l-btn-disabled l-btn-plain-disabled';
											
										btns += '" group="" id='+btn.pr.actionId+' onclick="dgBtnAction2(\'del\',\''+i+'\', $(this))">'
											+'<span class="l-btn-left l-btn-icon-left">'
											+'<span class="l-btn-text l-btn-empty">&nbsp;</span>'
											+'<span class="l-btn-icon icon-remove">&nbsp;</span></span>'
											+'</a></td>';
									  } else if (btn.pr.img.src.indexOf('showDel')>-1) {
										  var un = btn.pr.img.src.indexOf('showDelUn')>-1;
									      toolbar.push({
									    	text: btn.pr.tt,
											iconCls: un ? 'icon-showDelUn' : 'icon-showDel',
                                            uid:i,
                                            handler:function(){
                                                dgBtnAction('showDel', i, $(this));
                                            }
										});
									  }else{
									      toolbar.push({
										    text: btn.pr.textInfo,
									        iconCls:'icon-'+ btn.pr.actionId,
									        uid:i,
                                            handler:function(){
                                                dgBtnAction(btn.pr.img.src,i);
                                            }
										 });
										 if (btn.pr.img.bytes) {
											addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
									     } else {
									    	addStyle('.icon-'+  btn.pr.actionId+' {background: url("'+btn.pr.img.src+'")  no-repeat scroll center center / cover  transparent;}');
									     }
									  }
									}
								});
								var toolbar_t = $("#emt" + i);
								var p_info=false;
								if(toolbar_t !=null && typeof toolbar_t.html() != 'undefined'){
									toolbar = $("#emt" + i);
									p_info=true;
								}
								if (comp.hasClass('or3-icon-table')) {
									comp.panel({
										tools: toolbar,
									});
								} else {
									comp.datagrid({
										toolbar: toolbar,
										loadFilter: function(data){
											var toolbar_rows = $(this).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-count");
											$(toolbar_rows).text(data.total);
											return data;
										}
									});
									if(p_info){
										var dg_toolbar = comp.parents(".datagrid").find(".datagrid-toolbar:first");
										if (dg_toolbar.find(".datagrid-count").is("div") == false){
											if(btns!='')
												$(btns).insertBefore(dg_toolbar.find("table tr td"));
											$('<td style="width: 100%; display: flex;"><div class="datagrid-sel pagination-info" style="display: inline-block;"></div><div class="datagrid-count pagination-info" style="display: inline-block;"></div></td>').appendTo(dg_toolbar.find("table tr"));
										}
									}
								}
							} else if (akey == "tabs") { // tabs
								var firstShownIndex = -1;
								$.each(aval, function(ti, tab) {
									var tabIndex = tab.index;
									var tabVisible = tab.v;
									var tabSelect = tab.selected;
									
									if (tabVisible == 1) {
										if (firstShownIndex == -1)
											firstShownIndex = tabIndex;
										$("#" + i + " ul.tabs > li").eq(tabIndex).show();
										if (tabSelect) {
											comp.tabs('select', tabIndex);
										}
									} else
										$("#" + i + " ul.tabs > li").eq(tabIndex).hide();
								});
								var tab = comp.tabs('getSelected');
								var index = comp.tabs('getTabIndex',tab);
								if (firstShownIndex > -1 && !$("#" + i + " ul.tabs > li").eq(index).is(":visible"))
									comp.tabs('select', firstShownIndex);
								
							} else if (akey == "v") {
								var showComp = comp;
								
								if (e_tagName == 'INPUT' && (e_type == 'checkbox' || e_type == 'file'))
									showComp = comp.parent();
								else if (e_tagName == 'INPUT' && e_type == 'radio')
									showComp = comp.parents('table:first');
								else if (comp.parent().parent().get(0) && comp.parent().parent().parent().get(0).tagName == 'FIELDSET')
									showComp = comp.parent().parent().parent();

								var hideTd = ((showComp.hasClass('treeField') || showComp.hasClass('popup') || showComp.hasClass('or3-btn') || e_tagName == 'TEXTAREA'
										|| showComp.hasClass('orpanel')) && (showComp.parent().get(0).tagName == 'TD'
										|| showComp.parent().parent().get(0).tagName == 'TD')) 
										|| (showComp.parent().hasClass('orpanel') && !showComp.parent().parent().hasClass('or3-popup-panel')) 
										|| (showComp.parent().hasClass('tabs-panels') && !showComp.parent().parent().hasClass('or3-popup-panel'));
								
								var hideDate = showComp.hasClass('easyui-datebox') || showComp.hasClass('easyui-datetimebox') || showComp.hasClass('easyui-combobox');
								
								if (aval == '1' || aval == 1) {
									if (hideDate) {showComp.next().show();} else { 
									showComp.show();}
									if (hideTd) showComp.parents('td:first').show();
									if (hideDate) showComp.parents('td:first').show();
									
									comp.find('.easyui-datagrid').datagrid('resize');
									
									if (comp.hasClass('easyui-tabs'))
										comp.tabs('resize');
								} else {
									showComp.hide();
									if (hideTd) showComp.parents('td:first').hide();
									if (hideDate) showComp.parents('td:first').hide();
								}
							} else if (akey == "border") { // border temp fix
								
							} else if (akey == 'updateRow') {
								$.each(aval, function(ri, row) {
									updateTreeTableRow(i, row.index, row.row);
								});
							} else if (akey == 'updateTblRow') {
								var t = comp;
						    	var edPars = getEditing();
								$.each(aval, function(ri, row) {
									try {
										var count = t.datagrid('getRows').length;    // row count
									    var selected = t.datagrid('getSelected');
									    var selRow = 0;
									    if (selected) {
									    	selRow = t.datagrid('getRowIndex', selected);
									    }
									    
										var index = pageIndex(t, row.index);
									    if (0 <= index && index < count) {
											t.datagrid('updateRow',{
												index: index,
												row: row.row
											});
											var tr = t.datagrid('getRow', index); 
											if (tr.hasClass("datagrid-row-editing"))
												tr.removeClass("datagrid-row-editing");
										}

									    if (selRow >= 0 && selRow < count) {
									        t.datagrid('selectRow', selRow);
									    }
									    if (focusedTable != undefined) {
											var panel = $("#" + focusedTable).datagrid('getPanel').panel('panel');
											panel.attr('tabindex', 0);
											panel.focus();
									    }
									} catch (err) {}
								});
								var selRows = t.attr('selRows');
								var selCol = t.attr('selectedCol');
								if (selRows && selCol) {
									var rows = selRows.split(',');
									for (var ind=0; ind<rows.length; ind++) {
										rows[ind] = pageIndex(t, rows[ind]);
										t.datagrid('selectCell', {index:rows[ind],field:selCol});
									}
								}
								if (edPars != null) {
									restoreEditing(edPars);
								}
							} else if (akey == 'reloadRow') {
								reloadRows = aval;
							} else if (akey == 'setNodeTitle') {
								$.each(aval, function(ri, row) {
									if (comp.hasClass('easyui-tree')) {
										var node = comp.tree('find', row.index);
										if (node != null) {
											comp.tree('update', {
												target: node.target,
												text: row.title
											});
										}
									}
								});
							} else if (akey == 'reloadNode') {
								$.each(aval, function(ri, row) {
									if (comp.hasClass('easyui-tree')) {
										var node = comp.tree('find', row.index);
										if (node != null) {
											if (comp.tree('isLeaf', node.target)) {
												var parent = comp.tree('getParent', node.target);
												comp.tree('reload', parent.target);
											} else 
												comp.tree('reload', node.target);
										
										}
									}
								});
							} else if (akey == 'selectNode') {
								if (aval != 0) {
									nodeParent = false;
									treeCallLater('select', comp, aval, 20);
								} else {
								    var nodes = comp.tree('getChecked');
								    isOnChek = false;
								    for (var k = 0; k<nodes.length; k++) {
								    	$(this).tree('uncheck', nodes[k].target);
								    }
								    isOnChek = true;
								}
							} else if (akey == 'checkboxTree') {
								if (comp.hasClass('easyui-tree')) {
									if (aval == 0) {
										comp.tree({
											checkbox: function(node){
													return true;
											}
										});
									} else {
										comp.tree({
											checkbox: function(node){
												if (node.id == aval) {
													return false;
												} else {
													return true;
												}
											}
										});
									}
								}
							} else if (akey == 'deleteRow') {
								$.each(aval, function(ri, row) {
									comp.treegrid('remove', row.index);
								});
							} else if (akey == 'cv') {
								$.each(aval, function(ri, row) {
									if (row.v == 1 || row.v == '1')
										comp.treegrid('showColumn', row.uuid);
									else
										comp.treegrid('hideColumn', row.uuid);
								});
							} else if (akey == 'reload') {
								comp.attr('reload', '1');
								if (comp.hasClass('or3-icon-table')) {
									comp.panel('refresh');
								} else
									comp.datagrid('reload');
							} else if (akey == 'reloadUpload') {
								var pan = $('#uploaded' + i);
								if (pan.length > 0)
									pan.panel('refresh');
							} else if (akey == 'selectedRows') {
								if (comp.hasClass('easyui-datagrid')) {
									selectTableRows(comp, aval);
								} else if (comp.hasClass('easyui-treegrid')){
									comp.attr('selRows', aval);
								}
							} else if (akey == 'reloadTreeTable') {
								comp.treegrid('reload');
							}  else if (akey == 'bg') {
								comp.css({backgroundColor: aval});
							} else if (akey == 'fg') {
								comp.css({color: aval});
							} else if (akey == 'dialogs') {
								if (comp.hasClass('easyui-datagrid')) {
									$.each(aval, function(ok, ov) {
										if (ov.header){
											var td = comp.datagrid('getPanel').find('div.datagrid-header td[field="'+ov.header.uid+'"]');
											var hTitle = ov.header.title;
											if (hTitle)
												hTitle = hTitle.replace(/@/g,'<br/>');
											td.children().first().children().first().html(hTitle);
										}
									});
								}
							} else if (akey == 'zebra') {
								if (comp.hasClass('easyui-datagrid')) {
									$.each(aval, function(ok, ov){
										comp.datagrid({
											rowStyler: function(index,row) {
												if (index % 2 == 0){
													return 'background-color:'+ov.zebra1+';';
												} else {
													return 'background-color:'+ov.zebra2+';';
												}
											}
										});
									});
								}
							} else if (akey == 'bitSeparation') {
								$('#' + i).numberbox({
								    formatter:function(value) {
								    	var index = value.indexOf('.');
								    	var c = 0;
								    	if (index != -1) {
								    		var sub = value.substring(index + 1);
								    		var c = sub.length;
								    	} 
								    	var n = parseFloat(value);
								    	return n.toLocaleString('ru', { minimumFractionDigits: c });
								    }
								});
							} else if (akey == 'expanded') {
								$('#cnt' + i).addClass('expanded');
							} else {
								comp.attr(akey, aval);
							}
						});
						if (reloadRows != undefined) {
							$.each(reloadRows, function(ri, row) {
								var node = comp.treegrid('find', row.index);
								
								if (node != null) {
									if (node.children != null && node.children.length > 0) {
										comp.treegrid('reload', row.index);
									} else if (node.parent > 0) {
										comp.treegrid('update',{
											id: row.index,
											row: {
												children: [{
													parent: row.index,
												    id: -1000,
									                name: "untitled",
									            }]
											}
										});
										comp.treegrid('reload', row.index);
									} else {
										comp.treegrid('reload');
									}
								}
							});
						}
						if (comboValue != undefined) {
							if (comboValue > -1 ) {
                                $('#' + i + ' option:eq(' + comboValue + ')').prop('selected', true);
						    } else {
                                $('#' + i + ' option:eq(0)').prop('selected', true);
						    }
						}
					} else if (key == 'alert') {
						alert(value);
					} else if (key == 'autoRefresh') {
						if (comp.hasClass('easyui-datagrid')) {
							ifcRefreshers[ifcRefreshers.length] = setInterval(function() {
								comp.datagrid('reload');
							}, value);
						} else {
							ifcRefreshers[ifcRefreshers.length] = setInterval(function() {
								setValue(i, 1);
							}, value);
						}
					} else if (key == 'styleRow') {
						var styleByRow = styles[i];
						if (styleByRow == null) {
							styleByRow = {};
							styles[i] = styleByRow;
						}
						var view = $('[field=' + i + ']').first().parents('.datagrid-view');
						var table = view.find('.easyui-datagrid');
						if (table.length == 0)
							table = view.find('.easyui-treegrid');
						var delta = pageIndex(table, 0);
						var tds = view.find('[datagrid-row-index] [field=' + i + ']');

						$.each(value, function(k, row) {
							var pIndex = row.index + delta;
							var styleName = "st" + row.hsh;
							styleByRow[pIndex] = {'class': styleName};
							
							var style = cssStyles[styleName];
							if (style == null) {
								style = '';
								var divStyle = '';
								if(row.ff){
									divStyle += "font-family:" + row.ff + ";";
								}
								if(row.fs){
									divStyle += "font-size:" + row.fs + "px;";
								}
								if(row.fw){
									divStyle += "font-weight:" + row.fw + ";";
								}
								if(row.fst){
									divStyle += "font-style:" + row.fst + ";";
								}
								if(row.frg){
									divStyle += "color:" + row.frg + ";";
								}
								if(row.bcg){
									style += "background-color:" + row.bcg + ";";
								}
								cssStyles[styleName] = style;
								if (style.length > 0)
									$('style').first().append('\n.' + styleName +' {' + style + '}');
								if (divStyle.length > 0)
									$('style').first().append('\n.' + styleName +' div {' + divStyle + '}');
							}
							try {
								var td = $(tds.get(pIndex));
								td.removeClass();
								td.addClass(styleName);
							} catch (err) {}
						});
				} else if (key == 'options') {
					if ( comp.attr('type')=='radio' && value.length!=0){
						var table = $('#' + i );
						// очистить таблицу
						$(table).empty();
						var colCount = $(table).attr("count");
						if (colCount > 0) {
							var rowCount = Math.ceil(value.length/colCount);
							// создать необходимое количество ячеек
							var items = {};
							var ij=0;
							// value массив кнопок
							
							var d,checked,color,font,fontSize,fontStyle;
							$.each(value, function(akey, aval) {
								d = aval.pr.v == '1' ? "display:inline;" : "display:none;";
								checked = aval.pr.checked == true ? "checked='checked'" : "";
								color = aval.st.color == null ? "" : "color:"+aval.st.color+";";
								font = aval.st.font == "Dialog" ? "font-family:Arial;" : "font-family:"+aval.st.font+";";
								fontSize = aval.st.fontSize > 0 ? "font-size:"+aval.st.fontSize+"px;" : "";
								if (aval.st.fontStyle == 1)
									fontStyle = "font-weight:bold;";
								else if (aval.st.fontStyle == 2)
									fontStyle = "font-style:italic;";
								else if (aval.st.fontStyle == 3)
									fontStyle = "font-style:italic; font-weight:bold;";
								else fontStyle = "";
								items[ij] = "<label><input type='radio' value='" + aval.pr.value + "' name='"+aval.pr.name+"' id='"+i+"' "+checked+" style='"+d+"'/> "
								+"<span style='"+d+color+font+fontSize+fontStyle+"'>"+aval.pr.text+"</span></label>";
								ij++;
								});
							var j=0;
							var str= '';
							for (var r = 0; r < rowCount; r++) {
								str+="<tr>";
								for (var c = 0; c < colCount; c++) {
									if(j<ij){
										str+="<td>"+items[j]+"</td>";
										j++;
									}
								}
								str+="</tr>";
							}
							$(table).html(str);
						}
					}
				}
				});
			});
		});
	}  
}

function updateTreeTableRow(i, index, row) {
	if ($("#" + i).treegrid('find', index)) {
		$("#" + i).treegrid('update',{
			id: index,
			row: row
		});
		if (row.children) {
			$.each(row.children, function(ri, chrow) {
				updateTreeTableRow(i, chrow.id, chrow);
			});
		}
	}
}

function reloadTables() {
	window.clickedGrid = "";
}

function preparefileUpload(cont) {
	if (cont == null) cont = $('body');
	var uploads = cont.find('.or3-file-upload');
	for (var i=0; i<uploads.length; i++) {
		var upload = $(uploads.get(i));
		upload.fileupload({
			autoUpload: true,
			dropZone: upload.parent(),
			pasteZone: upload.parent(),
		    url: window.mainUrl + '&uid=' + upload.attr('id'),
	        dataType: 'json',
	        done: function (e, data) {
	        	if (data.result.result == 'success') {
		        	endEditing();
	                showChangeMsg();
		            loadData({}, true);
		            
	        	} else {
	        		alertError(data.result.message);
	        		$('body').unblock();
	        	}	
	        },
	        add: function (e, data) {
	        	blockPage();
	            data.submit();
	        },
	    });
	}
}

function uploadImage(uid) {
	$('#upload').fileupload({
		dropZone: $('#' + uid),
		pasteZone: $('#' + uid),
	    url: window.mainUrl + '&uid=' + uid,
	    dataType: 'json',
        done: function (e, data) {
        	if (data.result.result == 'success') {
                showChangeMsg();
        		loadData({}, true);
        	} else
        		alertError(data.result.message);
        }
	}).fileupload('option', {acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i}).click();
}

function uploadYourImage(userId) {
	$('#yourUpload').fileupload({
		dropZone: $('#yourPhoto'),
		pasteZone: $('#yourPhoto'),
	    url: window.mainUrl + '&width=150&height=180',
	    dataType: 'json',
        done: function (e, data) {
        	if (data.result.result == 'success') {
        		var par = {};
        		par["objId"] = userId;
        		par["attr"] = 'аватар';
        		$.post(window.mainUrl + "&getAttr&rnd=" + rnd(), par, function(res) {
        			checkData(res);
        			if (res.result) {
        				$('#yourImg').attr('src', "data:image/png;base64," + res.result);
        				$('#popup_user_content .phPr img').attr('src', "data:image/png;base64," + res.result);
        				$('.tooltip-content .phPr img').attr('src', "data:image/png;base64," + res.result);
        			} else
        				alertError(translation['error']);
        		}, 'json');
	        } else
	        		alertError(data.result.message);
	       }
	}).click();
}

function copyImageFromData() {
	var par = {};
	par["obj"] = "USER";
	par["name"] = 'взять фото из ЛД';
	$.post(window.mainUrl + "&sfunc&rnd=" + rnd(), par, function(res) {
		checkData(res);
		if (res.result) {
    		var par = {};
    		par["objId"] = userId;
    		par["attr"] = 'аватар';
    		$.post(window.mainUrl + "&getAttr&rnd=" + rnd(), par, function(res) {
    			checkData(res);
    			if (res.result) {
    				$('#yourImg').attr('src', "data:image/png;base64," + res.result);
    				$('#popup_user_content .phPr img').attr('src', "data:image/png;base64," + res.result);
    				$('.tooltip-content .phPr img').attr('src', "data:image/png;base64," + res.result);
    			} else
    				alert(translation['error']);
    		}, 'json');
		} else
			alert(translation['error']);
	}, 'json');
}

function deleteImage() {
	var par = {};
	par["obj"] = "USER";
	par["name"] = 'удалить фото из ЛД';
	$.post(window.mainUrl + "&sfunc&rnd=" + rnd(), par, function(res) {
		checkData(res);
		if (res.result) {
			$('#yourImg').attr('src', '');
			$('#popup_user_content .phPr img').attr('src', '');
			$('.tooltip-content .phPr img').attr('src', '');
		} else
			alert(translation['error']);
	}, 'json');
}

function loadReports() {
	$.post(window.mainUrl + "&getReports&rnd=" + rnd(), {}, function(data) {
		checkData(data);
		if (data.children && data.children.length>0) {
			$('#reports').remove();
			$('#reportBtn').removeAttr("reportid").unbind('click');
			$('#reportBtn').removeClass("m-btn").removeClass("m-btn-small").removeClass("l-btn").removeClass("l-btn-small");
			var text = $('#reportBtn').find('.l-btn-text').text();
			if (text != null && text.length > 0) {
				$('#reportBtn').empty();
				$('#reportBtn').text(text);
			}
			if(data.children.length > 1 || data.children[0].children != null){
				html = "<div id='reports' class='subReports'>";
				$.each(data.children, function(i, c) {
					if (c.children && c.children.length > 0) {
						html += "<div><span>" + c.name + "</span><div>";
						$.each(c.children, function(i1, c1) {
							if (c1.children && c1.children.length > 0) {
								html += "<div><span>" + c1.name + "</span><div>";
								$.each(c1.children, function(i2, c2) {
									html += "<div><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
								});
								html += "</div></div>";
							} else {
								html += "<div><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
							}
						});
						html += "</div></div>";
					} else {
						html += "<div><a reportid='"+c.id+"'>"+c.name+"</a></div>";
					}
				});
				html+="</div>";
				$('#trash').append(html); 
				$('#reportBtn').show();
				$('#reportBtn').menubutton({
						menu: '#reports'
				});
			}else{
				$('#reportBtn').attr("reportid", data.children[0].id);
				$('#reportBtn').show();
				$('#reportBtn').linkbutton();
				$('#reportBtn').bind('click', function() {
				});
			}
		} else {
			$('#reportBtn').hide();
		}
	}, 'json');
}

function loadWindowTitle(dlg) {
	$.post(window.mainUrl + "&getTitle&rnd=" + rnd(), {}, function(data) {
		var titlePan = dlg.parent().find('.window-header .panel-title');
		$(titlePan).text(data.title).css('font-size','14px');
		loadStack(titlePan);
	}, 'json');
}

function loadDialogReports(did) {
	$.post(window.mainUrl + "&getReports&rnd=" + rnd(), {}, function(data) {
		checkData(data);
		if (data.children && data.children.length>0) {
			$('#reports' + did).remove();

			if(data.children.length > 1 || data.children[0].children != null){
				html = "<div id='reports" + did + "' class='subReports'>";
				$.each(data.children, function(i, c) {
					if (c.children && c.children.length > 0) {
						html += "<div><span>" + c.name + "</span><div>";
						$.each(c.children, function(i1, c1) {
							if (c1.children && c1.children.length > 0) {
								html += "<div><span>" + c1.name + "</span><div>";
								$.each(c1.children, function(i2, c2) {
									html += "<div><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
								});
								html += "</div></div>";
							} else {
								html += "<div><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
							}
						});
						html += "</div></div>";
					} else {
						html += "<div><a reportid='"+c.id+"'>"+c.name+"</a></div>";
					}
				});
				html+="</div>";
				$('#trash').append(html); 
				$('#reportBtn' + did).menubutton({
						menu: '#reports' + did
				});
				
				var text = $('#reportBtn' + did).find('.l-btn-left').find('.l-btn-left');
				if (text != null && text.length > 0) {
					$('#reportBtn' + did).empty();
					$('#reportBtn' + did).append(text);
				}

			}else{
				$('#reportBtn' + did).attr("reportid", data.children[0].id);
				$('#reportBtn' + did).find('.l-btn-text').text(data.children[0].name);
			}
		} else {
			$('#reportBtn' + did).hide();
		}
	}, 'json');
}

function post(par, func) {
	saveAsyncPostRequest(window.mainUrl, par, func, func != null ? 'json' : null);
}

function postAndParseData(par) {
	post(par, function(data) {
		checkData(data);
		if (data.changes) {
			parseData(data);
		} 
	});
}

function setSelectedValue(id, val) {
	var par = {"uid":id,"val":(val != null ? val : ""),"cmd":"set"};
	loadData(par, true);
}

function setValue(id, val) {
	showChangeMsg();
	var par = {"uid":id,"val":(val != null ? val : ""),"cmd":"set"};
	loadData(par, true);
}

function loadData(params, async, fn) {
	if (async == null || async == undefined) async = true;
	$('.easyui-datebox').parent().find('.textbox-text').inputmask("d.m.y", {
		"placeholder": "дд.мм.гггг",
		"insertMode" : false,
	});
	$('.easyui-datetimebox.minutes').parent().find('.textbox-text').inputmask("d.m.y h:s", {
		"placeholder": "дд.мм.гггг чч:ММ",
		"insertMode" : false,
	});
	$('.easyui-datetimebox.seconds').parent().find('.textbox-text').inputmask("d.m.y h:s:s", {
		"placeholder": "дд.мм.гггг чч:ММ:сс",
		"insertMode" : false,
	});
    $('.format_hh_mm').mask("99:99",{placeholder:" "});
    $('.format_dd_mm_yyyy_hh_mm').mask("99.99.9999 99:99",{placeholder:" "});
    
	var retData = null;
	saveAjaxRequest('POST', window.mainUrl + "&getChange", params, function(data) {
		try {
			checkData(data);

			if (data.changes && data.changes.length > 0)
				parseData(data);
			reloadTables();

			retData = data;
			
			if (fn) fn(data);
		} catch (err) {
			//console.log(err);
		}
		$('body').unblock();
		$('#glassPane').hide();
		$('.glassDialog').hide();
	}, 'json', async);
	
	return retData;
}

function saveAsyncPostRequest(url, params, success, dataType) {
	params["rnd"] = rnd();
	params["guid"] = guid;

	$.post(url, params, success, dataType).fail(function() {
		// Добавляем запрос в хранилище необработанных запросов
		lostRequests.push(new LostRequest("POST", url, params, success, dataType));
	});
}

function saveAjaxRequest(type, url, params, success, dataType, async) {
	params["rnd"] = rnd();
	params["guid"] = guid;

	$.ajax({
		type : type,
		url : url,
		data : params,
		success : success,
		dataType : dataType,
		async : async
	})
	.fail(function() {
		// Добавляем запрос в хранилище необработанных запросов
		if (async)
			lostRequests.push(new LostRequest(type, url, params, success, dataType));
	});
}

function loadScript(url) {
	var head = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = url;
	head.appendChild(script);
}

function loadScript1(filename, callback) {
	var fileref = document.createElement('script');
	fileref.setAttribute("type", "text/javascript");
	fileref.onload = callback;
	fileref.setAttribute("src", filename);
	if (typeof fileref != "undefined") {
		document.getElementsByTagName("head")[0].appendChild(fileref)
	}
}

function createWYSIWYG(elements) {
	for (var i = 0; i < elements.length; i++) {
		new nicEditor().panelInstance(elements[i]); 
	}
}

function setErrorType(e, errID) {
	if ($(e).hasClass('easyui-datebox')) {
		if (!$(e).hasClass('datebox-f')) comp.datebox();
		e = $(e).datebox("textbox").parent();
		$(e).toggleClass("error-type-"+errID, true);
		return ;
	} else if ($(e).hasClass('easyui-datetimebox')) {
		if (!$(e).hasClass('datetimebox-f')) comp.datetimebox();
		e = $(e).datetimebox("textbox").parent();
		$(e).toggleClass("error-type-"+errID, true);
		return ;
	} else if ($(e).hasClass('easyui-combobox')) {
		if (!$(e).hasClass('combobox-f')) $(e).combobox();
		e = $(e).combobox("textbox").parent();
		$(e).toggleClass("error-type-"+errID, true);
		return;
	} else if ($(e).hasClass('easyui-numberbox')) {
		if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
		e = $(e).numberbox("textbox").parent();
		$(e).toggleClass("error-type-"+errID, true);
		return;
	} else if ($(e).is("input") 
			|| $(e).is("select")
			|| $(e).is("textarea")
			|| $(e).hasClass("textbox-text") 
			|| $(e).hasClass("datebox")
			|| $(e).hasClass("datetimebox")){
		$(e).one("change", function () {
			delErrorType($(this));
	    });
		$(e).toggleClass("error-type-"+errID, true);
		return ;
	}
}

function delErrorType(e) {
	if ($(e).hasClass('easyui-datebox')) {
		if (!$(e).hasClass('datebox-f')) $(e).datebox();
		e = $(e).datebox("textbox").parent();
	}
	else if ($(e).hasClass('easyui-datetimebox')) {
		if (!$(e).hasClass('datetimebox-f')) $(e).datetimebox();
		e = $(e).datetimebox("textbox").parent();
	}
	else if ($(e).hasClass('easyui-combobox')) {
		if (!$(e).hasClass('combobox-f')) $(e).combobox();
		e = $(e).combobox("textbox").parent();
		
	}
	else if ($(e).hasClass('easyui-numberbox')) {
		if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
		e = $(e).numberbox("textbox").parent();
	}
	var cl = $(e).attr('class');
	if (cl !== undefined){
		var classList = cl.split(/\s+/);
		$.each( classList, function(index, item){
			if (item.indexOf('error-type-') >= 0) {
				$(e).toggleClass(item, false);
			}
		});
	}
}

function clearAllErrorType() {
	$.each($("[class*=error-type-]"), function(i, em){
		delErrorType(em);
	});
}

$('.tlpBtn').tooltip({
    position: 'bottom',
       onShow: function(){
        $(this).tooltip('tip').css({
            backgroundColor: '#666',
            borderColor: '#666',
            color:'#fff !important'
        });
    }
});

function blockPage(title) {
	if (typeof title == 'undefined') {
		title = translation['wait'];
	}
	$('body').block({
		message: '<img src="'+window.contextName+'/jsp/media/img/loader.gif"><h1 style="color:#fff;font-size:16px;margin-top:10px;">'+title+'</h1>', 
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

function makeTree(operation) {
	if (selectedTree) {
		var node = selectedTree.tree('getSelected');
		if (operation == 'addToFavorites') {
			var url = window.mainUrl;
			var par = {};
			par["cmd"] = 'addToFavorites';
			par["processUID"] = node.id;
			par["json"] = 1;
			$.ajax({
				type : 'POST',
				url : url + "&rnd=" + rnd(),
				data : par,
				success : function(data) {
					 $('#processTree2').tree('reload');
				},
				dataType : 'json',
				async : false
			});
		} else if (operation == 'removeFromFavorites') {
			var url = window.mainUrl;
			var par = {};
			par["cmd"] = 'removeFromFavorites';
			par["processUID"] = node.id;
			par["json"] = 1;
			$.ajax({
				type : 'POST',
				url : url + "&rnd=" + rnd(),
				data : par,
				success : function(data) {
					 $('#processTree2').tree('reload');
				},
				dataType : 'json',
				async : false
			});
			$('#processesList_body2').html("");
		}else if(operation == 'moveUp'){
			var par = {"uid":selectedTree.attr('id'),"moveUp":node.id};
			post(par);
			selectedTree.tree('move',{
				  target: node.target,
				  dir: 'up'  // move up
				});
			showChangeMsg();
		}else if(operation == 'moveDown'){
			var par = {"uid":selectedTree.attr('id'),"moveDown":node.id};
			post(par);
			selectedTree.tree('move',{
			 target: node.target,
			  dir: 'down'  // move down
			});
			showChangeMsg();
		} else {
			selectedTree.tree(operation, node.target);
		}
	}
}

function makeTreeTable(operation) {
	if (selectedTree) {
		var node = selectedTree.treegrid('getSelected');
		selectedTree.treegrid(operation, node.id);
	}
}

function columnStyler(value, row, index) {
	var styleByRow = styles[this.field];
	if (styleByRow != null) {
		var style = styleByRow[index];
		if (style)
			return style;
	} 
	return '';
}

function globalIndex(t, index) {
	var res = index;
	if (t.datagrid('getPager').length > 0) {
		res += (t.datagrid('getPager').find('.pagination-num').val() - 1) * t.datagrid('getPager').find('.pagination-page-list').val();
	}
	return res;
}

function pageIndex(t, index) {
	var res = index;
	if (t.datagrid('getPager').length > 0) {
		res -= (t.datagrid('getPager').find('.pagination-num').val() - 1) * t.datagrid('getPager').find('.pagination-page-list').val();
	}
	return res;
}

function rnd() {
	var array = new Uint32Array(1);
	var crypto = window.crypto || window.msCrypto;
	crypto.getRandomValues(array);
	return array[0];
}

function getOption(elem, option){
	var out = null;
	var opt = $(elem).data('options');
	if(opt != null){
		var opts = opt.split(',');
		var p;
		var param;
		var val;
		$.each(opts, function(i, opt) {
			p = opt.indexOf(':');  
			param=$.trim(opt.substring(0,p));
			val = opt.length>p+1?opt.substring(p+1):'';
			if(param==option){
				out = val;
				return;
			}
		});
	}
	return out;
}

// validChars должен работать и по старому для TextField и по новому для IntField
function validChars(v1, v2) {
	var c = null; // Компонент
	var e = null; // Событие
	
	if (v2 == null) {
		e = v1;
		c = $(e.data.target);
	} else {
		e = v2;
		c = $(v1);
	}
	
	var code = e.keyCode;
	if (code == 27 || code == 13 || code == 9 || code == 8 || code == 46
			|| (code > 34 && code < 41))
		return true;
	if ((e.ctrlKey && e.charCode == 118) || (e.ctrlKey && e.charCode == 99)){
		return true;
	}
	
	var tmp = c.attr('exclude');
	var exclude = (tmp == null || tmp.length == 0) ? null : tmp.split(";");
	
	tmp = c.attr('include');
	var include = (tmp == null || tmp.length == 0) ? null : tmp.split(";");
	
	var cur;
	if (e.keyCode != 0) {
		cur = String.fromCharCode(e.keyCode);
	} else if (e.charCode != 0) {
		cur = String.fromCharCode(e.charCode);
	} else {
		cur = e.which;
	}
	if (cur == null || cur.length == 0) return true;

	if (exclude != null) {
		for (var i = 0; i < exclude.length; i++) {
			var exc = exclude[i];
			if (exc.length == 1) {
				// один символ
				if (exc == cur)
					return false;
			} else {
				// диапазон
				var A = exc[0];
				var B = exc[1];
				if (A <= cur && cur <= B)
					return false;
			}
		}
		return true;
	}
	
	if (include != null) {
		for (var i = 0; i < include.length; i++) {
			var inc = include[i];
			if (inc.length == 1) {
				// один символ
				if (inc == cur)
					return true;
			} else {
				// диапазон
				var A = inc[0];
				var B = inc[1];
				if (A <= cur && cur <= B)
					return true;
			}
		}
		return false;
	}
	return true;
}

function setElemForTopPane(elements) {
	// удалить все предыдущие элементы
	var forDel = $(".ui-toolbar").find("[onTop]");
	for (var i = 0; i < forDel.length; i++) {
		$(".ui-toolbar").remove(forDel[i]);
	}
	// отсортировать новые данные
	elements.sort(function (a, b){
		  var i = $(a).attr('onTop');
		  var j = $(b).attr('onTop'); 
		  return i < j ? -1 : i > j ? 1 : 0;
		});
	
	for (var i = 0; i < elements.length; i++) {
		$(".ui-toolbar").append(elements[i]);
	}
}

function hidePopUp(target,panelId) {
	// источник события
	target = $(target).closest('[id]');
	
	var isPopupOrButton = target != null && (target.hasClass('popup') || target.hasClass('trBtn'));
	var isOnPopupPanel = $(target).closest('.popUpPanContent').length > 0;
	
	if (!isOnPopupPanel || isPopupOrButton) {
		var panID = target != null ? ("pop" + $(target).attr('id')) : "";
		var panels = $(panelId).find(".popUpPanContent");
		for (var j = 0; j < panels.length; j++) {
			if($(panels[j]).attr('id') != panID && $(panels[j]).is(":visible")){
				// отправить на сервер событие закрытия панели Before
				var par = {};
				par["uid"] = $(panels[j]).attr('id').substring(3); // первые три символа это префикс "pop"
				par["cmd"] = "hidePopUp";
				par["t"] = "b";
				
				postAndParseData(par);
				
				$(panels[j]).fadeOut();
				// отправить на сервер событие закрытия панели After
				par["t"] = "a";
				
				postAndParseData(par);
			}
		}
	}
    return true;
}

function loadStack(container) {
	var par = {};
	par["cmd"] = "getStack";
	
	post(par, function(data) {
		showStack(data, container);
	});
}

function showStack(stack, container) {
	if (breadcrumpsOn) {
		var popups = $("div[id^='or3_popup']");
		var popCount = popups.size();
		var breadCount = stack.length;
		var last = breadCount-1;

		var html = '';
		
		if ($('#ui_startPage').size() > 0 && $('#ui_startPage').attr('href').indexOf("openMainIfc") > -1) {
			html += "<a class='fullPath' href=\"javascript:goToFrame(-20);\">" + pageName['ui_startPage'] + "/</a>";
		} else if ($('#ui_startPage').size() > 0 && $('#ui_startPage').attr('href').indexOf("ui=start") > -1) {
			html += "<a class='fullPath' href=\"javascript:goToFrame(-10);\">" + pageName['ui_startPage'] + "/</a>";
		}

		var k = 0;
		if (idSect == -50 || idSect == -70 || idSect == -100 || idSect == -120 || idSect == -130) {
			k++;
		}

		if (idSect != -10 && idSect != -20) {
			html += "<a class='fullPath' onclick=\"goToFrame(" + idSect + ");" + (k>0 ? "clearToFrame(2);" : "") + "\">" + selSect + "/</a>";
		}
		
		$.each(stack, function(i, frame) {
			if (i>=k) {
				if (last == i) {
					html = html + "<span class='fullPath-l'>" + frame.title + "</span>";
				} else if (frame.id != 0) {
					if (popCount > 0 && breadCount - i > popCount + 1)
						html = html + "<a class='fullPath' href=\"javascript:destroyPopup(" + popCount + "); goToFrame(" + (i + 2) + ");\">" + frame.title + "/</a>";
					else if (popCount > 0)
						html = html + "<a class='fullPath' onclick=\"destroyPopup(" + (breadCount - i - 1) + "); clearToFrame(" + (i + 2) + ");\">" + frame.title + "/</a>";
					else
						html = html + "<a class='fullPath' href=\"javascript:goToFrame(" + (i + 2) + ");\">" + frame.title + "/</a>";
				}
			}
		});
		
		if (container == null) {
			$("#fullPath").empty();
			$("#fullPath").html(html);
	
			resizeHeight();
		} else {
			$(container).parent('.window-header').find('#fullPathP').remove();
			$(container).parent('.window-header').append("<div id='fullPathP' class='header header-t'></div>");
			$(container).parent('.window-header').find('#fullPathP').html(html);
			
			var dBody = $(container).parent().parent().find('.window-body');
			dBody.height(dBody.height() + $("#fullPath").height());
		}
	}
}

function resizeHeight(){
	var c = $('#uiPanel');
	var pc = c.layout('panel','center');				// панель интерфейса
	var pn = c.layout('panel','north');					// панель заголовка, крошек и тулбар
	
	pn.panel('resize', {height:'auto'});				// подогнать высоту
	var newHeight = pn.panel('panel').outerHeight();	// считать новую высоту
	pc.height(c.height() - newHeight);					// уменьшить высоту панели интерфейса
	
	pc.panel('move',{									// и сместить ее
		left: 0,
		top: newHeight
	});

	$('.easyui-panel:not(.tamur-tabs)').panel();							// пересчитать высоты в интерфейсе
}

function goToFrame(id) {
	var popups = $("div[id^='or3_popup']");
	var popCount = popups.size();
	destroyPopup(popCount);
	
	if (id < 0) {
		var loc = null;
		var i = null;
		switch (id) {
		case -5:
			loc = "ui=oldStart";
			i = "ui_oldStartPage";
			break;
		case -10:
			loc = "ui=start";
			i = "ui_startPage";
			break;
		case -20:
			loc = "cmd=openMainIfc";
			i = "ui_startPage";
			break;
		case -30:
			loc = "ui=tasksList";
			i = "ui_Orders";
			break;
		case -40:
			loc = "ui=notiInList";
			i = "ui_OrdersNotification";
			break;
		case -50:
			loc = "cmd=openArch&uid=1014162.3198690";
			i = "ui_personInfo";
			break;
		case -60:
			loc = "ui=processesList&mode=layout";
			i = "ui_process";
			break;
		case -70:
			loc = "cmd=openArch&uid=1014162.3211302";
			i = "ui_staff";
			break;
		case -80:
			loc = "ui=archList&mode=layout";
			i = "ui_arch";
			break;
		case -90:
			loc = "ui=dictsList&mode=layout";
			i = "ui_dicts";
			break;
		case -100:
			loc = "cmd=openArch&uid=1014162.4046423";
			i = "ui_stat";
			break;
		case -110:
			loc = "ui=helpWnd&mode=tabs";
			i = "ui_help";
			break;
		case -120:
			loc = "cmd=openDict&uid=1014162.3554408";
			i = "ui_right";
			break;
		case -130:
			loc = "cmd=openDict&uid=1014162.3555088";
			i = "ui_actions";
			break;
		case -140:
			loc = "ui=adminsList&mode=layout";
			i = "ui_admins";
			break;
		}
		if (i != null) {
			document.location.hash = loc + "&id=" + i;
			$('#'+i).click();
		}
	} else {
		blockPage();
		$('#app').attr('init',"1");
		$('#glassPane').show();
		$('#uiPanel').show();
		$('#uiTitle').show();
		$('#uiToolbar').show();
		if (breadcrumpsOn && breadcrumpsVisible)
			$("#fullPath").show();
		else
			$("#fullPath").hide();
		resizeHeight();
		$('#app').show();
		$('#app').panel('refresh', window.mainUrl + "&cmd=goToFrame&index=" + id);
		$('#oldStartDiv,#startDiv, #processesList, #archList, #tasksList, #notiInList, #sessionsList, #archList, #dictsList, #adminsList, #helpWnd, #npa449Wnd, #npa233Wnd').hide();
        clearTimeout(refresher);
	}
}

function clearToFrame(index) {
	var par = {};
	par["cmd"] = "clearToFrame";
	par["index"] = index;
	
	postAndParseData(par);
}

function readIdCard() {
	var appl = getApplet('IdCardReaderApplet');
	
	var par = {};
	par["cmd"] = "readIdCard";
	par["err"] = "";

	if (appletLoading) { 
		if (appletLoadTryCount >= appletLoadTryMax) {
			appletLoadTryCount = 0;
			post(par);
			alert(appletLoadFailMsg);
			return;
		}
		
		setTimeout(function() {
			appletLoadTryCount++;
			readIdCard();
		}, 3000);
		return;
	}
	appl = getApplet('IdCardReaderApplet');

	try {
		par["err"] = appl.read(par);
	} catch (e) {
		alert(appletCallFailMsg);
	}

	post(par);
}

function getApplet(name) {
	loadApplet(name);
	return document.getElementById(name);
}

function setDialogBtnsEnabled(did, enable) {
	if (enable)
		$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('enable');
	else
		$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('disable');
}

function sanitizeHtml(s) {
	return s.replace(/&/g,'&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot').replace(/'/g, '&#39;')
			.replace(/\//g,'&#47;').replace(/\\/g,'&#92;').replace('/\r/g', '').replace(/\n/g, '');
}

function sanitizeHtml2(s) {
	var out = '';
	if (s != null) {
        for (var i = 0; i < s.length; i++) {
            var c = s.charAt(i);
            if (c == '&')
            	out += '&amp;';
            else if (c == '<')
            	out += '&lt;';
            else if (c == '>')
            	out += '&gt;';
            else if (c == '"')
            	out += '&quot;';
            else if (c == '\'')
            	out += '&#39;';
            else if (c == '/')
            	out += '&#47;';
            else if (c == '\\')
            	out += '&#92;';
            else if (c != '\r' && c != '\n')
            	out += c;
        }
	}
    return out;
}

function isCharacterKeyPress(evt) {
    if (typeof evt.which == "undefined") {
        return true;
    } else if (typeof evt.which == "number" && evt.which > 0) {
        return !evt.ctrlKey && !evt.metaKey && !evt.altKey && evt.which != 8;
    }
    return false;
}

function generateDocument(fn, ext, cls, name, arg0, arg1, arg2) {
	var url = window.mainUrl;

	url += "&sfunc&cls=" + cls + "&name=" + name + "&ext=" + ext + "&fn=" + encodeURIComponent(fn);
	if (arg0 != null)
		url += "&arg0=" + arg0;
	if (arg1 != null)
		url += "&arg1=" + arg1;
	if (arg2 != null)
		url += "&arg2=" + arg2;

	url += "&rnd=" + rnd();

	$('#report_frame').attr('src', url);
}

function changeToUpperCase(component, event, upperAllChars, upperFirstChar) {
	if (isCharacterKeyPress(event)) {
		var prevVal = component.value;
		var newVal = "";
		if (upperAllChars) {
			newVal = prevVal.toUpperCase();
		} else if (upperFirstChar) {
			newVal = prevVal.charAt(0).toUpperCase() + prevVal.slice(1);
		}
		if (prevVal != newVal) {
			component.value = newVal;
			$(component).change();
		}
	}
}

function sendFile() {
	var file = document.getElementById("chooser").files.item(0);
	$('#getFile').dialog("close");
	blockPage();
	
    var url = window.mainUrl + "&rnd=" + rnd();
   
	if (file) {
		var formData = new FormData();                  
		formData.append('getFile', file);
		$.ajax({
			type : 'POST',
			url : url,
			cache : false,
			contentType : false,
			processData : false,
			data : formData,
			success : function(data) {},
			dataType : 'json',
		});
	} else {
		var par = {};
		par["cmd"] = 'getFileIsNull';
		par["json"] = 1;
		$.ajax({
			type : 'POST',
			url : url,
			data : par,
			success : function(data) {},
			dataType : 'json',
			async : false
		});
	}
}

function showdebugger() {
	$(".task_check:visible:checked").each(function(index) {
		var taskId = $(this).attr('task');
		var url = window.mainUrl;
		var par = {};
		par["cmd"] = 'getVarsValues';
		par["taskId"] = taskId;
		par["json"] = 1;
		$.ajax({
			type : 'POST',
			url : url + "&rnd=" + rnd(),
			data : par,
			success : function(data) {
				// Вывод значений в окне				
				// Вывод диалога				 
				if (document.getElementById('debuggerDialog') == null) {
					var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'debuggerDialog'}); 
					dialogDiv.html('<div id=\"debuggerPanel\" style=\"padding: 20px; width: 500px; max-height: 350px; overflow: scroll; \"></div>');
					dialogDiv.dialog({
						title:'Отладка',
						closed: true,
						draggable: true,
						resizable: false,
						closeOnEscape: true,
						modal:true,
						closable: true,						
						buttons:[{text:'Закрыть', id: 'closeDialog', handler:function(){$('#debuggerDialog').dialog("close");;}}]
					});
				}
				// заполнить панель данными	используя easyui tree
				document.getElementById('debuggerPanel').innerHTML = "";					
				$("#debuggerPanel").append('<ul id="tt"></ul>');					
				$('#tt').tree({		
					data:data
				});
				$('#debuggerDialog').dialog("open");					
			},
			failure: function (result) { alert('Fail'); },
			dataType : 'json',
			async : false
		});
	});
}

function startInterview() {
	var url = window.mainUrl;
	var par = {};
	par["cmd"] = 'startInterview';
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url + "&rnd=" + rnd(),
		data : par,
		success : function(data) {
			if (data.oprosnik) {				
   				startProcc(data.oprosnik.processUID);
			}
		},
		dataType : 'json',
		async : false
	});
}

startInterview();