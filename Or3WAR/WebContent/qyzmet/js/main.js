import {DataChecker, Util} from './util.js';
import {Navigation} from './navigation.js';
import {InterfaceController} from './ifc.js';
import {Orders} from './orders.js';
import {Notifications} from './notifications.js';
import {Monitor} from './monitor.js';

import {Processes} from './processes.js';
import {Archives} from './archive.js';
import {Translation} from './translation.js';
import {EventOps} from './events.js';
import {Polling} from './polling.js';
import {Analytic} from './analytic.js';
import {NCA} from './nca.js';
import {Ping} from './ping.js';
import {PersonalPhoto} from './personalPhoto.js';
import {Autocomplete} from './autocomplete.js';
import {Help} from './help.js';

export class PortletApp {
	
	mainUrl = "";
	restUrl = "";
	// создал Жаркын
	centerPanelWidth = null;
	centerPanelLeft = null;
	OLAP_URL = "";
	translation = null;

	constructor(mainUrl, restUrl, userId, lang) {
		this.mainUrl = mainUrl;
		this.restUrl = restUrl;
		this.userId= userId;
		this.lang = lang;
		Util.restUrl = restUrl;
		this.OLAP_URL = `http://${new URL(document.location.href).host}/analytic/rest/`;
	}
	
	init() {
		let app = this;

		Translation.init(this.lang);
		this.translation = Translation.translation;

		this.polling = new Polling(app);
		this.polling.longPolling();
		
		let nav = new Navigation(app);
		this.nav = nav;

		this.analytic = new Analytic(app);
		// this.analytic.getOlapPort();

		let ifcController = new InterfaceController(app);
		this.ifcController = ifcController;
		ifcController.init();

		let pingClass = new Ping();
		if ($('#ping').length > 0) pingClass.init();

		let localStorLanguage = localStorage.getItem("EkyzmetLanguage");

		if(localStorLanguage) {
			let tempLang = $('#topLangKz').hasClass("selected") ? "KZ" : "RU";

			if(localStorLanguage !== tempLang) {
				ifcController.changeInterfaceLang(localStorLanguage, event)
			}
		}

		this.monitor = new Monitor(app);
		this.monitor.init();
		
		let orders = new Orders(app);
		this.orders = orders;
		this.orders.init();
		
		let notifications = new Notifications(app);
		this.notifications = notifications;
		this.notifications.init();

		let processes = new Processes(app);
		this.processes = processes;
		this.processes.init();
		
		let archives = new Archives(app);
		this.archives = archives;
		this.archives.init();

		this.help = new Help(app);
		this.help.init();
		
		this.ncaLayer = new NCA();
		
		let photo = new PersonalPhoto(app);
		photo.initProfile(app.userId);
		$("#change-my-photo").on("click", function() {
			photo.uploadYourImage();
		});
		
		$("#delete-my-photo").on("click", function() {
			photo.deleteImage();
		});
		
		$("#take-photo-from-sys").on("click", function() {
			photo.copyImageFromData();
		});
		EventOps.init(this);

		$(window).on('hashchange', function() {
			var day = location.hash;
			nav.hashChanged(day);
		});
		nav.hashChanged(location.hash);

		$(document).ready(function() {
			let par = {"sfunc":1,"cls":"MainPage","name":"otherMethods", "arg0":app.userId, "arg1": "getPoruchWidth"};
			app.query(par).then(response => {
				if (response.status === 200) {
      				response.json().then(json => {
						if(json.width == 1) {
							app.pushAside();
						}
					});
				}
			});
		});

		$("body").on('click', '.order-type', function() {
			let jThis = $(this);
			if (jThis.hasClass('order-type-selected')) {
				console.log('no change');
			} else {
				var type = jThis.attr('code');
				location.hash = 'orders=' + (type ? type : 'all');
			}
		    return false;
		});

		$("body").on('click', '.order-block .title', function() {
			let jThis = $(this);
			if (jThis.attr('type') === 'out')
				nav.openOrder(null, jThis.attr('iuid'), jThis.attr('iter'), false);
			else
				nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');			
		});
		
		$("body").on('click', '.collapse-portlet', function() {
			let jThis = $(this);
			if (jThis.hasClass('icon-arrows-up')) {
				jThis.parents('.portlet').find('.portletBody').hide();
				jThis.parents('.portlet').parent().css({
					"flex-basis": "content",
					"flex-grow": "0"
				});
				jThis.addClass('icon-arrows-down').removeClass('icon-arrows-up');
			} else {
				jThis.addClass('icon-arrows-up').removeClass('icon-arrows-down');
				jThis.parents('.portlet').parent().css({
					"flex-basis": "",
					"flex-grow": ""
				});
				jThis.parents('.portlet').find('.portletBody').show();
			}
		});

		$('.user-info').tooltip({
			deltaX: -20,
		    content: $('.popup_user_menu').html(),
		    showEvent: 'click',
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
		
		key('ctrl+shift+z', function() {
			var str = "";
			$('[data-uid]').each(function(i, p) {
				if (($(this).closest('.popUpPanContent').length == 0))
					str += $(this).attr('data-uid') + "\n";
			});
			Util.alert(str);
			return false;
		});

		document.addEventListener("click", function(e) {
			let arr = ['popUpPan', "popUpPanContent", "combo-p", "window-body", "panel-header", "dialog-button", "messager-window", "calendar-day"];
			let bool = false;
			for(let nameClass of arr) {
				if(e.target.closest("." + nameClass)) {
					bool = true;
					break;
				}
			}
			if(!bool) {
				app.hidePopUpPanContent();
			}
		});

		this.loadPortletsContent();
		
		Autocomplete.autocomplete(document.getElementById("privateDeal"));
	}
	
	loadPortletsContent() {
		let app = this;
		$(".portlet, .widget").each(function(i) {
			var portlet = $(this);
			var clsName = portlet.attr('cls');
			var methodName = portlet.attr('method');
			
			app.loadPortletContent(clsName, methodName);
		});
	}
	
	loadPortletContent(clsName, methodName, arg="") { 
		// arg - передовать объект с аргументами {"arg1": "text1", "arg2":"text2"}. Можно и не передовать
		if (clsName && methodName) {
			if(arg !== "") {
				let mainSettings = {"sfunc":1,"cls":clsName,"name":methodName, "arg0":this.userId};
				var par = {...mainSettings, ...arg};
			} else {
				var par = {"sfunc":1,"cls":clsName,"name":methodName, "arg0":this.userId};
			}

			this.query(par).then(response => {
				if (response.status === 200) {
      				response.json().then(json => {
						this.parsePortletContent(json);
					});
				}
			});
		}
	}

	parsePortletContent(json) {
		if (json) {
			DataChecker.checkData(json).then(res => {
				if (res.portletId) {
					var div = $('#' + res.portletId);
					if (div.length > 0) {
						if (res.title) {
								$('#' + res.portletId + ' .portletTitle span').html(res.title);				
						} else {
							// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
							if($('#topLangKz').hasClass("selected")) {
								$('#' + res.portletId + ' .portletTitle span').html(res.titleKz);
							} else {
								$('#' + res.portletId + ' .portletTitle span').html(res.titleRu);
							}
							// ==========================================
						}
						if (div.find('.portletBody').length > 0)
							this.parseContentByType(res, div.find('.portletBody'));
						else
							this.parseContent(res, div);
					}
				}
			});
		}
	}
	
	parseContent(res, div) {
		// здесь код изменил Жаркын, чтобы выбрать язык
		if(!res.title) {
			if($('#topLangKz').hasClass("selected")) {
				res.title = res.titleKz;
				res.value = res.valueKz;
			} else {
				res.title = res.titleRu;
				res.value = res.valueRu;
			}
		}
		var html = '<img class="widget-img" src="css/img/' + res.image + '" /><div class="vert">';
		html += '<span class="widget-title">' + res.title + '</span>';
		html += '<span class="widget-value">' + res.value + '</span></div>';
		div.append($(html));
	}
	
	parseContentByType(res, div) {
		let app = this;
		
		// здесь код изменил Жаркын, чтобы выбрать язык
		if(!res.content) {
			res.content = $('#topLangKz').hasClass("selected") ? res.contentKz : res.contentRu;
		}
		if (res.contentType == 'startProcess') {
			$.each(res.content, function(i, content) {
				var html = '<div class="process" uid="' + content.uid + '">';
				html += '<i class="icon-left icon-' + content.icon + '"/>';
				html += '<span>' + content.title + '</span>';
				html += '<i class="icon-right icon-arrow-right" />';
				html += '</div>';
				div.append($(html));
			});
		}
		else if (res.contentType == 'openIfc') {
			$.each(res.content, function(i, content) {
				var html = '<div class="ifc">';
				html += app.parseJSONContent(content, 'ifc');
				html += '</div>';
				div.append($(html));
			});
		}
		else if (res.contentType == 'orders') {
			let params = Util.parseHash(encodeURI(location.hash));
			let tabSelected = params.orders ? params.orders : null;
		
			var html = '<div class="orders">';
			html += '<div class="order-types">';
			
			var counts = {};
			var ordersDivs = {};
			
			$.each(res.types, function(i, type) {
				var isSelected = (tabSelected) ? (type.code == tabSelected) : (type.selected != null && type.selected != undefined);
				html += '<span class="order-type' + (isSelected ? ' order-type-selected' : '') + '"' 
						+ ((type.code) ? ' code="' + type.code + '"' : '') 
						+ '>';
				if (type.code)
					counts[type.code] = 0;
				else
					counts.all = 0;
				html += type.title;
				html += '<span class="order-count">';
				html += '</span>';
				html += '</span>';
				
				ordersDivs[type.code] =  '<div class="order-blocks orders-' + type.code + (isSelected ? ' order-blocks-selected"' : '" style="display: none;"')
						+ ((type.code) ? ' code="' + type.code + '"' : '') 
						+ '>';
			});
			// здесь код поменял Жаркын 24.01.2023, создал кнопку, чтобы задачи расскрыть на всю страницу
			let language = "ru";
			$('#topLangKz').hasClass("selected") ? language = "kz" : language = "ru";
			let hint = language === "kz" ? "Кеңейту" : "Расширить";
			html += `<button class="tasksFullWidth" title="${hint}">&hoarr;</button>`; // только эти строки добавил

			html += '</div>';
			$.each(res.content, function(i, content) {
				if (content.type) {
					ordersDivs[content.type] += '<div uid="' + content.uid + '" class="order-block" type="' + content.type + '">';
					if (content.type == 'my')
						ordersDivs[content.type] += '<i class="delete-order icon-close" task="' + content.proc + '"/>';
					
					ordersDivs[content.type] += app.parseJSONContent(content, 'order');
					ordersDivs[content.type] += '</div>';
					counts[content.type]++;
					
				}
				counts.all++;
			});
			$.each(res.types, function(i, type) {
				ordersDivs[type.code] += '</div>';
				html += ordersDivs[type.code];
			});
			
			html += '</div>';
			div.append($(html));
			
			$('.tasksFullWidth').on('click', function() {
				app.pushAside();
			});  // и здесь

			$.each(counts, function(type, count) {
				if (type === 'all')
					div.find('.order-type:not([code]) .order-count').html(count);
				else
					div.find('.order-type[code=' + type + '] .order-count').html(count);
			});
		}
		else if (res.contentType == 'chart') {
			var canvas = $('<canvas id="myChart" width="100%" height="100%"></canvas>');
			div.append(canvas);
			
			var scales = res.scales ? res.scales : {
				r: {
					beginAtZero: true,
					min: 0,
			        max: 5,
					autoSkip: false,
			        stepSize: 1
				}
			};
			// здесь код поменял Жаркын для выбора языка паутины
			res.data = $('#topLangKz').hasClass("selected") ? res.dataKz : res.dataRu;

			const myChart = new Chart(canvas, {
	    		type: res.chartType,
	    		data: res.data,
				options: {
					maintainAspectRatio: false,
					elements: {
				    	line: {
				        	borderWidth: 3
				      	}
				    },
					scales: scales,
					plugins: {
						legend: {
							position: 'bottom'
						}
					}
				},
			});
		}
	}
	
	parseJSONContent(json, contentType) {
		var res = "";
		let orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
		// здесь код изменил Жаркын, чтобы выбрать язык
		let lang = "ru";
		lang = $('#topLangKz').hasClass("selected") ? "kz" : "ru";
		
		for (var tag in json) {
			if (tag === 'line') {
				res += '<div class="content-row">';
				res += this.parseJSONContent(json[tag], contentType);
				if (json.ctrl) {
					let textTitle = lang == "kz" ? "Орындау мерзімі" : "Срок исполнения";
					res += `<div class="order-deadline ${orderStatus[json.status]}" title="${textTitle}">${json.ctrl}</div>`;
				} 
				res += '</div>';
			} else if (tag === 'col') {
				res += '<div class="content-column">';
				res += this.parseJSONContent(json[tag], contentType);
				res += '</div>';
			} else if (tag === 'views') {
				res += '<span class="views"><i class="icon-eye-opened"/>' + json.views + '</span>';
			} else if (tag === 'image') {
				res += '<span class="content-image"><img src="' + (json.image.length > 0 ? json.image : 'css/img/empty-avatar-30.png') + '" /></span>';
			} else if (tag === 'typeIcon') {
				// res += '<i class="icon-type icon-type-' + json.typeIcon + '"/>';
			} else if (tag === 'location') {
				res += '<span class="location"><i class="icon-location"/>' + json.location + '</span>';
			} else if (tag === 'linkRu') {
				if(!json.link) {json.link = lang == "kz" ? json.linkKz : json.linkRu;}
				res += '<a class="link" onclick="javascript:openIfc(\'' + json.ifc + '\', \'' + json.obj + '\')">' + json.link + '</a>';
			} else if (contentType === 'order' && tag === 'title') {
				let iuidHtml = (json.line && json.line.col && json.line.col.iuid) ? ('" iuid="' + json.line.col.iuid) : '';
				let orderColor = "";
				if(json.type == "in" || json.type == "out") {
					orderColor = json.status == 0 ? "#468847" : json.status == 1 ? "#F89406" : "#E05540";
				}
				res += '<span class="title" style="color:' + orderColor + ';" iter="' + json.iter + '" proc="' + json.proc + '" uid="' + json.uid + iuidHtml + '" type="' + json.type + '">' + json.title + '</span>';
			} else if (tag !== 'uid' && tag !== 'ifc' && tag !== 'obj' && tag !== 'type' && tag !== 'proc'  && tag !== 'iter' && tag !== 'linkKz' && tag !== "status" && tag !== "ctrl") {
				res += '<span class="' + tag + '">' + json[tag] + '</span>';
			}
		}
		return res;
	}

	query(params) {
		let url = (params.cmd) ? (this.restUrl + '&cmd=' + params.cmd) : this.restUrl;
		return fetch(url, {
			method: 'POST',
	    	credentials: 'include',
			body: $.param(params)
	    });
	}
	
	post(par) {
		let _this = this; 
		return new Promise((resolve, reject) => {
			_this.query(par).then(response => {
				response.json().then(json => {
					DataChecker.checkData(json).then(data => {
						resolve(data);
					});
				});
			});
		});
	}
	
	logout() {
		let _this = this;
		_this.ifcController.forseSaveChanges().then(data => {
			//if (confirm('Вы действительно хотите выйти из Системы?')) { #1532
			let par = {"cmd":"ext", "json":1};
			_this.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						window.location.href = _this.mainUrl + "/qyzmet/login.jsp?rnd=" + Util.rnd();
					});
				}
			});
			//}
		});
	}

	resize(selector) {
		let app = this;
		var comp = $(selector);
		
		if (comp.is(":visible")) {
			if (comp.hasClass('easyui-panel')) {
				comp.panel();
				comp.panel('resize');
			} else if (comp.hasClass('easyui-tabs'))
				comp.tabs('resize');
			
			$.each(comp.children(), function(i, child) {
				app.resize(child);
			});
		}
	}

	resizeHeight() {
		var c = $('#ui-panel').layout();
		var pc = c.layout('panel','center');				// панель интерфейса
		var pn = c.layout('panel','north');					// панель заголовка, крошек и тулбар
		
		pn.panel('resize', {height:'auto'});				// подогнать высоту
		var newHeight = pn.panel('panel').outerHeight();	// считать новую высоту
		pc.height(c.height() - newHeight);					// уменьшить высоту панели интерфейса
		
		pc.panel('move',{									// и сместить ее
			left: 0,
			top: newHeight
		});
	
		$('#ui-panel .easyui-panel:not(.tamur-tabs)').panel();							// пересчитать высоты в интерфейсе
	}

	pushAside() {
		let this_ = this;
		// метод создал Жаркын, чтобы при нажатии кнопки(рядом с входящими), входящие открылись на полную
		let language = "ru";
		$('#topLangKz').hasClass("selected") ? language = "kz" : language = "ru";
		let hint = language === "kz" ? "Кеңейту" : "Расширить";
		let hint2 = language === "kz" ? "Кішірейту" : "Уменьшить";

		let centerPanel = $("#orders").parent().parent().parent(),
			bigWidth = centerPanel.parent().width(), //1200px
			centerPanelWidth = centerPanel.width(), // 550px
			firstChild = centerPanel.children(":first"),
			neighborAbove = centerPanel.prev(),
			neighborBelow = centerPanel.next();

		if(centerPanel.position().left !== 0) {
			this.centerPanelLeft = centerPanel.position().left;
			this.centerPanelWidth = centerPanelWidth;
		}
		
		let l = this.centerPanelLeft,
			w = this.centerPanelWidth,
			second = 500;
	
		let bigOrLittle = centerPanel.position().left == 0 ? true : false;

		if(bigOrLittle) {
			centerPanel.fadeOut(second, function() {
				firstChild.width(w);
				$(this).width(w);
				$(this).css({"left": `${l}px`});
				$(this).fadeIn(second);
				neighborAbove.fadeIn(second);
				neighborBelow.fadeIn(second);	

				if(!$(this).hasClass("panel")) {
					$(this).addClass("panel");
				}
				$(".tasksFullWidth").attr('title', hint).html("&hoarr;");
			});

			this_.loadPortletContent("MainPage", "otherMethods", {
				"arg1": "setPoruchWidth",
				"arg2":"0"
			})

		} else {
			neighborAbove.fadeOut(second);
			neighborBelow.fadeOut(second);

			centerPanel.fadeOut(second, function() {
				firstChild.width(bigWidth);
				$(this).width(bigWidth);
				$(this).css({"left": "0px"});
				$(this).fadeIn(second);

				if($(this).hasClass("panel")) {
					$(this).removeClass("panel");
				}

				$(".tasksFullWidth").attr('title', hint2).html("&roarr;");
			});

			this_.loadPortletContent("MainPage", "otherMethods", {
				"arg1": "setPoruchWidth",
				"arg2":"1"
			});
		}
	}

	hidePopUpPanContent() {
		$(".popUpPanContent").each(function() {
			if($(this).css("display") && $(this).css("display") != "none") {
				$(this).fadeOut();
			}
		})
	}
}