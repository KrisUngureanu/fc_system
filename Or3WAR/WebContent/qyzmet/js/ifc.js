import {Util, DataChecker} from './util.js';
import {TableOps} from './tables.js';
import {ErrorOps} from './errors.js';
import {TooltipOps} from './tooltip.js';
import {Translation} from './translation.js';

export class InterfaceController extends TableOps {
	
	dialogZindex = 345000;
	messagerZindex = 350000;

	lastShowDate = null;
	lastShowComboValue = null;
	focusedTable = undefined;
	
	popupcount = 0;
	dialogResult = [];
	
	multiSelection = {};
	dialogOpened = null;
	
	fatal = false;
	isDataIntegrityControl = false;
	opts = 0;

	// whats this
	data = 0;
	DLG_CHANGE_PD = 5; // создал Жаркын. Правильно ли сделал?

	lastDownloadTime = 0;
	waitTime = 1500;
	
	searchCount = 0;

	static popDlg = [];
	static popDlgType = [];
	static styles = {};
	static cssStyles = {};
	static openedDialogs = [];
	static attentionMap = {};
	hiddenPanels = {}
	
	constructor(app) {
		super(app);
		this.app = app;
		this.errorUtil = new ErrorOps();
	}
	
	init() {
		super.initDefaults(this);
		
		let _this = this;
		
		_this.setFocus();

		$("body").append($("<div></div>").attr("id", "trash").css("display", "none"));
		$("#trash").append($("<iframe></iframe>").attr("id", "report_frame").css("display", "none"));

		$('#ui-body').panel({
			onLoad: function() {
				if (typeof $("#ui-body .mainPanel").attr("data-uiTitle") !== "undefined") {
					$('.ui-title').text($("#ui-body .mainPanel").attr("data-uiTitle"));	
				}
				_this.preparefileUpload();
				
				_this.clickPopUpContent('ui-body');
				_this.showPopUpContent();
				_this.movePopUpContent();
				
				// удалить все предыдущие элементы
				$(".ui-toolbar").find("[onTop]").remove();
				
				_this.loadData({}, true).then(() => {
					$('#ui-header').show();
					_this.setElemForTopPane();
					_this.app.resizeHeight();
					$('#ui-body').panel('resize');
					_this.app.resize('#ui-body');
				});
				_this.loadReports();
				_this.app.analytic.init();
				
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
			}
		});
		
		$(window).bind('resizeEnd', function() {
			$('#ui-body').find('.panel, .panel-body, .easyui-fluid, .easyui-panel, .easyui-tabs, .datagrid-view, .datagrid-view2, .datagrid-header, .datagrid-body, .datagrid-footer').width(0);
			$('#ui-body').width(0);
			$('#ui-body').panel('resize');
			_this.app.resize('#ui-body');
		});
		
		_this.initDefaults();
		
		// при изменении значения любого отправляем на сервер
		$("body").on('change', 'input, select, textarea, iframe', function() {
			var id = $(this).attr('id') || $(this).parent().prev().attr('id');
			var comboSearch = $(this).parent().prev().hasClass('easyui-combobox');
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id) {
				var val = "";
				if ($(this).attr('type') == 'checkbox') {
					val = $(this).prop('checked');
				} else 
					val = $(this).val();
				if (!comboSearch) {
					_this.setValue(id, val);
				}
			}
		});
		
		$("body").on('click', 'button.popup', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				$('.menu-content,.menu-shadow').hide();
				_this.openPopup(uid);
			}
		});
		
		$("body").on('click', 'a.popup', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				$('.menu-content,.menu-shadow').hide();
				_this.openPopup(uid);
			}
		});
		
		$("body").on('click', 'a.hyper', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				Util.blockPage();
				document.location.hash = "cmd=openIfc&uid=" + uid + "&rnd=" + Util.rnd();
			}
			return false;
		});
		
		$("body").on('click', 'a.treeField', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				_this.openTree(uid, $(this).attr("title"));
			}	
			return false;
		});

		$("body").on('click', '.clean-btn', function(e) {
			var par = {cmd: "clr", uid: $(this).attr('id').substring(3)}; // первые три символа это префикс "clr"
			_this.postAndParseData(par);
			return false;
		});

		$("body").on('click', 'a[reportid]', function() {
			_this.reportClick($(this));
		});

		$("body").on('click', '#langKz, #topLangKz', function(e) {
			_this.changeInterfaceLang('KZ', e);
			localStorage.setItem("EkyzmetLanguage", "KZ");
		});
		$("body").on('click', '#langRu, #topLangRu', function(e) {
			_this.changeInterfaceLang('RU', e);
			localStorage.setItem("EkyzmetLanguage", "RU");
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
		
		$("body").on('click', '.arr-parent', function() {
			var content = $('#cnt'+ $(this).attr('id').substring(1));
		    $(content).addClass("expanded-v");
		    if ($(this).css('display') == 'block')
				$(this).css('display', 'none');
		    return false;
		});
		
		$("body").on('click', '.accordion .ttl-coll-pan', function() {
			var parent = $(this).parent();
			// первый символ это префикс "t", cnt - префикс контента
			var content = $('#cnt'+ $(this).attr('id').substring(1));
			if ($(content).hasClass('accord')) {		
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
	}
	
	initDefaults() {
		let _this = this;
		$.blockUI.defaults.baseZ = 300000;

		$.extend($.fn.window.defaults, {zIndex: 110000, shadow: false});

		$.extend($.messager.defaults, {zIndex: this.messagerZindex, width: 450, minHeight: 100});
		
		$.fn.tabs.defaults.onSelect = function(title,index) {
			var tp = $(this);
			var par = {"cmd":"selTab","uid":tp.attr('id'),"indx":index};
			
			_this.displayEmptyParentPanels(tp.tabs('getSelected'));
			_this.app.post(par).then(data => {
				$.each(_this.tablesToReloadMap, function(key, value) {
					$.each($('#' + key).parents('.easyui-tabs'), function (i, tb) { 
						if ($(tb).attr('id') == tp.attr('id')) {
							$('#' + key).datagrid('load');
						}
					});
				});
				tp.find('.easyui-tabs').tabs('resize');
			});
		};
		
		$.extend($.fn.combo.defaults, {zIndex: 150000});
		
		$.extend($.fn.combobox.defaults, {panelHeight: 'auto', panelMaxHeight: 400});

		// поиск в комбобоксе с учетом регистра или без по флагу options.toUpperCase
		$.extend($.fn.combobox.defaults.filter = function(q,row){
			var opts = $(this).combobox('options');
			var toUpperCase = opts.toUpperCase;
			var s = row[opts.textField];
			if (toUpperCase) {
				for (var si in s) {
					if ((typeof s[si] == "string" && s[si].toUpperCase().indexOf(q.toUpperCase()) >= 0)
					 	|| (s[si].title && s[si].title.toUpperCase().indexOf(q.toUpperCase()) >= 0)) {
						return true;
					}
				}
				return false;
			} else {
				for (var si in s) {
					if ((typeof s[si] == "string" && s[si].indexOf(q) >= 0)
					 	|| (s[si].title && s[si].title.indexOf(q) >= 0)) {
						return true;
					}
				}
				return false;
			}
		});
		
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
		
		
		$.fn.datebox.defaults.onShowPanel = function() {
			_this.lastShowDate = $(this).datebox("getText");
		};
		$.fn.datebox.defaults.onHidePanel = function() {
			var id = $(this).attr('id');
			var date = $(this).datebox("getText");
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id && (date != _this.lastShowDate || date == "")) {
				_this.setValue(id, date);
				_this.focusNext($(this).datebox("textbox"));
			}
		};
		$.fn.datetimebox.defaults.onShowPanel = function() {
			_this.lastShowDate = $(this).datetimebox("getText");
		};
		$.fn.datetimebox.defaults.onHidePanel = function() {
			var id = $(this).attr('id');
			var date = $(this).datetimebox("getText");
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id && (date != lastShowDate || date == "")) {
				_this.setValue(id, date);
				_this.focusNext($(this).datetimebox("textbox"));
			}
		};
		
		$.fn.combobox.defaults.onShowPanel = function() {
			_this.lastShowComboValue = $(this).combobox("getValue");
			//var textboxCombo = $(this).combo("textbox");
			//var panelCombo = $(this).combo("panel");
			//panelCombo.children(".combobox-item").css({"font-size":textboxCombo.css("font-size"),"font-family":textboxCombo.css("font-family"),"font-style":textboxCombo.css("font-style"),"font-weight":textboxCombo.css("font-weight")});
			//panelCombo.css("background-color", "rgb(255, 222, 173)");
		};
		$.fn.combobox.defaults.onHidePanel = function() {
			var tableCombobox = $(this).hasClass("datagrid-editable-input");
			if (tableCombobox)
				_this.endEditing();
			else {
				var id = $(this).attr('id');
				var opts = $(this).combobox("options");
				var val = $(this).combobox("getValue");
				var data = $(this).combobox("getData");
				var cleanCombo = true;
				for(var i = 0; i<data.length; i++) {
					if(data[i][opts.valueField] === val && id && val != _this.lastShowComboValue && val != undefined) {
						_this.setValue(id,val);
						cleanCombo = false;
						break;
					}
				}
				if (cleanCombo && val != _this.lastShowComboValue && id)
					$(this).combobox('clear');
			}
		};
		$.fn.combobox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		$.fn.datebox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		$.fn.datetimebox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		
		$.fn.numberbox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		
		$("body").on('keyup', ".datagrid-editable-input, .numberbox .textbox-text", function(e) {
			if (e.which && e.which == 13)
				_this.endEditing();
		});
		
		$("body").on('click', '.ortable', function(e) {
			_this.endEditing();
		});
		
		$("body").on('click', '.or3-btn, .popup', function(e) {
			e.preventDefault();
			_this.endEditing();
		});
		
		$("body").on('keyup', ".datagrid-editable .datebox .textbox-text", function(event) {
			if (event.which && event.which == 13) {
				$(this).parent().prev().combo('hidePanel');
				$(this).blur();
				_this.endEditing();
			}
		});
		
		$("body").on('click', '.or3-btn:not(.view-file)', function(e) {
			e.preventDefault();
			if ($(this).attr('id') && !$(this).hasClass('btn-disabled')) {
				Util.blockPage();
				_this.setValue($(this).attr('id'), 1);
			}
		});
		
		$("body").on('click', '.view-file', function(e) {
			if (!$(this).hasClass('btn-disabled')&& $(this).attr('id')) {
				_this.downloadFile(e, $(this).attr('id'));
			} else if ($(this).parent().attr('id')) {
				_this.downloadFile(e, $(this).parent().attr('id'),$(this).parent().attr('index'));
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
		
				var url = _this.app.restUrl + "&sfunc&cls=" + cls + "&name=" + method + "&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + uid + "&arg1=" + encodeURIComponent(path);
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
		
				_this.loadData(par);
		
				return false;
			}
		});
	}
	
	setValue(id, val) {
		this.showChangeMsg();
		var par = {"uid":id,"val":(val != null ? val : ""),"cmd":"set"};
		this.loadData(par);
	}

	showChangeMsg() {
		$('#saveBtn').linkbutton('enable');
		$('#cancelBtn').linkbutton('enable');
	}
	hideAllChangeMsg() {
		$('#saveBtn').linkbutton('disable');
		$('#cancelBtn').linkbutton('disable');
	}
	hideChangeMsg() {
		$('#saveBtn').linkbutton('disable');
		$('#cancelBtn').linkbutton('disable');
	}

	loadData(par) {
		let _this = this;
		
		return new Promise((resolve, reject) => {
			par['getChange'] = '1';
			_this.app.query(par).then(response => {
				response.json().then(json => {
					DataChecker.checkData(json).then(data => {
						if (data.changes && data.changes.length > 0)
							_this.parseData(data);
							
						$('body').unblock();
						resolve(data);
					});
				});
			});
		});
	}
	
	postAndParseData(par) {
		let _this = this;
		return new Promise((resolve, reject) => {
			_this.app.query(par).then(response => {
				response.json().then(json => {
					DataChecker.checkData(json).then(data => {
						if (data.changes && data.changes.length > 0)
							_this.parseData(data);
						resolve(data);
					});
				});
			});
		});
	}

	parseData(data) {
		let _this = this; 
		if (data.changes) {
			// Пробегаем массив изменений
			$.each(data.changes, function(i, change) {
				// Каждое изменение - это отношенией id компонента - набор измененных свойств
				$.each(change, function(id, props) {
					var comp = null;
					// Пробегаемся по открытым диалогом в обратном порядке и ищем компонент
					for (var di = InterfaceController.openedDialogs.length - 1; di >= 0; di--) {
						var dialog = $('#' + InterfaceController.openedDialogs[di]);
						comp = dialog.find('#' + id);
						// Если компонент найден - выходим из цикла
						if (comp.length > 0) break; 
					}
					// Если компонент не найден в диалогах - ищем по всему окну
					if (comp == null || comp.length == 0) comp = $('#' + id);
					
					// Пробегаемся по всем измененным свойствам
					$.each(props, function(key, value) {
						_this.updateComponent(id, comp, key, value);
						
					});
				});
			});
		}
		_this.app.resize('#ui-body');
	}
	
	updateComponentStyle(comp, key, value, e_tagName, e_type) {
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
			// do nothing
		} else if(e_tagName == 'INPUT' && e_type == "file") {
			comp.parent().css(value);
		} else if(e_tagName == 'INPUT') {
			if (comp.hasClass('easyui-combobox')) {
				if (!comp.hasClass('combobox-f')) comp.combobox();
				$.each(value, function (akey, aval){
					if (akey == "margin-top" || akey == "margin-bottom" || akey == "margin-left" || akey == "margin-right"){
						comp.next().css(akey, aval);
					} else if (akey == "font-family" || (akey == "font-size" && aval > 0)) {
						comp.combobox("textbox").css(akey, aval);
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
	}

	updateComponent(i, comp, key, value) {
		let _this = this; 

		let e_tagName = comp.prop("tagName");
		let e_type = comp.attr("type");
		let e_class = comp.attr("class");
		let e_id = comp.attr("id");
		
		if (key == 'st') {
			_this.updateComponentStyle(comp, key, value, e_tagName, e_type);
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
						_this.errorUtil.delErrorType(comp);
					}
					if (e_type == 'radio') {
						comp.find('input[type=radio][value="' + aval + '"]').prop('checked', true);
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
					} else if (e_tagName == 'TEXTAREA' && e_class == 'tinyMCE') {
						tinyMCE.get(e_id).setContent(aval);
					} else if (e_tagName == 'TEXTAREA' && e_class != 'tinyMCE') {
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
							if (comp.hasClass('nicEdit-main')) {
								var parent = comp.parents().eq(2).find('textarea#' + comp.attr('id')).html(aval.replace(/@/g,'<br/>'));
							}
						}
					}
				} else if (akey == 'OrWebDocFieldProps') {
					comp.parent().find('span.btn-label').html(aval.text.replace(/@/g,'<br/>'));
					var spanElement = document.getElementById(aval.id).parentNode;
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
						InterfaceController.attentionMap[i] = setInterval(function() {
							if ($('#' + i).length > 0) {										
								if (document.getElementById(i).style.backgroundColor == "palegreen") {
									document.getElementById(i).style.background  = "initial";
								} else {
									document.getElementById(i).style.background  = "palegreen";
								}
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "buttonAttention") {
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							if ($('#' + i).length > 0) {										
								if (document.getElementById(i).style.backgroundColor == "rgb(79, 209, 197)") {
									document.getElementById(i).style.background  = "initial";
								} else {
									document.getElementById(i).style.background  = "#4fd1c5";
								}
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "dateFieldAttention") {
					var tb = $('#' + i).datebox('textbox');
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = tb.add(tb.parent()).css("background-color");
							if (color == "rgb(152, 251, 152)") {
								tb.add(tb.parent()).css("background-color","white");
							} else {
								tb.add(tb.parent()).css("background-color","palegreen");
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						tb.add(tb.parent()).css("background-color","white");
					}
				} else if (akey == "textFieldAttention") {
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							if (document.getElementById(i).style.backgroundColor == "palegreen") {
								document.getElementById(i).style.background  = "initial";
							} else {
								document.getElementById(i).style.background  = "palegreen";
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "comboBoxAttention") {
					var tb = $('#' + i).combobox('textbox');
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = tb.add(tb.parent()).css("background-color");
							if (color == "rgb(152, 251, 152)") {
								tb.add(tb.parent()).css("background-color","white");
							} else {
								tb.add(tb.parent()).css("background-color","palegreen");
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						tb.add(tb.parent()).css("background-color","white");
					}
				} else if (akey == "docFieldColumnAttention") {
					var dg = $('#' + aval.parent);
					var td = dg.datagrid('getPanel').find('div.datagrid-header td[field="' + i + '"]');
					if (aval.value == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = td.css('background-color');
							if (color == "rgb(152, 251, 152)") {
								td.css('background-color','initial');
							} else {
								td.css('background-color','palegreen');
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
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
				} else if (akey == "limitExceededMessage") {	
					var toolbarElement = document.getElementById("emt" + i);
					if (toolbarElement) {
						var html = toolbarElement.innerHTML;
						var i1 = html.indexOf('<td width="100%">');
						var i2 = html.indexOf('</td>', i1);
						var res = html.substring(i1, i2 + 5);
						if (aval.length > 0) {
							html = html.replace(res, '<td width="100%" style="color:red;padding-left:10;padding-right:10;">' + aval + '</td>');
						} else {
							html = html.replace(res, '<td width="100%"></td>');
						}
						toolbarElement.innerHTML = html;
					}
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
						_this.errorUtil.delErrorType(comp);
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
						var j = 0;
						$.each(aval, function(ok, ov) { // select
							// options
							var ti = i + '-title';
							options[j] = {};
							options[j][ti] = ov.o;
							options[j++][i] = ov.u;
						});
						var selectedValue = comp.combobox('getValue');
						comp.combobox('loadData', options);
						comp.combobox({
							formatter: comboTableFormat
						});
						comp.combobox('setValue', selectedValue);
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
			    	_this.errorUtil.delErrorType(comp);
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
						} else if (e_type == 'radio') {
							comp.find('input[type=radio]').attr("disabled", "disabled").attr("readonly", "readonly");
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
						} else if (e_type == 'radio') {
							comp.find('input[type=radio]').removeAttr("disabled").removeAttr("readonly");
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
								_this.enableNaviBtn(compb, true);
							else
								_this.enableNaviBtn(compb, false);
						});
					} else {
						var actionId = aval.actionId;
						var compb = $('#' + actionId);
						if (aval.e == 1)
							_this.enableNaviBtn(compb, true);
						else
							_this.enableNaviBtn(compb, false);
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
					try {
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
							
						var totalWidth = 0;
						$.each($('#' + i + ' ul.tabs:first > li'), function(ind, li) {
							if ($(li).is(":visible")) {
								totalWidth += $(li).width();
								console.log(ind + ' = ' + $(li).width());
							}
						});
						console.log('total = ' + totalWidth);
						console.log('wrap = ' + $('#' + i + ' .tabs-header:first').width());
						if (totalWidth >= $('#' + i + ' .tabs-header:first').width()) {
							$('#' + i + ' .tabs-scroller-left:first').removeClass('or3-hide');
							$('#' + i + ' .tabs-scroller-right:first').removeClass('or3-hide');
						} else {
							$('#' + i + ' .tabs-scroller-left:first').addClass('or3-hide');
							$('#' + i + ' .tabs-scroller-right:first').addClass('or3-hide');
						}
						
					} catch (err) {
						Util.logErrorInfo("Error parsing 'tabs' property", err);
					}
				} else if (akey == "v") {
					var showComp = comp;
					
					if (showComp.length > 0) {
						if (e_tagName == 'INPUT' && (e_type == 'checkbox' || e_type == 'file'))
							showComp = comp.parent();
						else if (comp.parent().get(0) && comp.parent().get(0).tagName == 'FIELDSET' && e_type == 'radio')
							showComp = comp.parent();
						else if (comp.parent().parent().parent().get(0) && comp.parent().parent().parent().get(0).tagName == 'FIELDSET')
							showComp = comp.parent().parent().parent();

						// спрятать всю ячейку TD вместе с компонентом
						var hideTd = (
										(showComp.hasClass('treeField')
										|| showComp.hasClass('popup')
										|| showComp.hasClass('or3-btn')
										|| e_tagName == 'TEXTAREA'
										|| e_type == 'radio'
										|| showComp.hasClass('orpanel')
										|| showComp.hasClass('ordatatable')
										|| (showComp.get(0) && showComp.get(0).tagName == 'FIELDSET')
										) 
									&& (
										(showComp.parent().get(0) && showComp.parent().get(0).tagName == 'TD')
										|| (showComp.parent().parent().get(0) && showComp.parent().parent().get(0).tagName == 'TD')
										|| (showComp.hasClass('ordatatable') && showComp.parent().parent().parent().parent().get(0)
												&& showComp.parent().parent().parent().parent().get(0).tagName == 'TD')
										|| (showComp.hasClass('ordatatable') && showComp.parent().parent().parent().parent().parent().get(0)
												&& showComp.parent().parent().parent().parent().parent().get(0).tagName == 'TD')	
									   )
									) 
								|| (showComp.parent().hasClass('orpanel') && !showComp.parent().parent().hasClass('or3-popup-panel')) 
								|| (showComp.parent().hasClass('tabs-panels') && !showComp.parent().parent().hasClass('or3-popup-panel'));
						
						var hideDate = showComp.hasClass('easyui-datebox') || showComp.hasClass('easyui-datetimebox') || showComp.hasClass('easyui-combobox');
						
						if (aval == '1' || aval == 1) {
							if (hideDate) {showComp.next().show();}
							else {showComp.show();}
							if (hideTd) showComp.parents('td:first').show();
							if (hideDate) showComp.parents('td:first').show();
							_this.displayEmptyParentPanels(showComp);
							
							comp.find('.easyui-datagrid').datagrid('resize');
							
							if (comp.hasClass('easyui-tabs'))
								comp.tabs('resize');
							
							if (comp.hasClass('orpanel'))
								delete _this.hiddenPanels[i];
						} else {
							showComp.hide();
							if (showComp[0]) showComp[0].style.setProperty('display', 'none', 'important');
							if (hideTd) showComp.parents('td:first').hide();
							if (hideDate) showComp.parents('td:first').hide();
							_this.hideEmptyParentPanels(showComp);
							
							if (comp.hasClass('orpanel'))
								_this.hiddenPanels[i] = true;
						}
					}
				} else if (akey == "rv") {
					// видимость отчетов
					$.each(aval, function(rid, rv) {
						var rep = $("[reportid='" + rid + "']");
						console.log("Report visibility: " + rid + ", " + rv);
						
						if (rep.length > 0) {
							if (rep.attr('id')) {
								if (rv === 1)
									rep.show();
								else
									rep.hide();
							} else {
								if (rv === 1)
									rep.parent().parent().show();
								else
									rep.parent().parent().hide();
							}
						}
					});
				} else if (akey == "border") { // border temp fix
					
				} else if (akey == 'updateRow') {
					$.each(aval, function(ri, row) {
						_this.updateTreeTableRow(i, row.index, row.row);
					});
				} else if (akey == 'updateTblRow') {
					var t = comp;
			    	var edPars = _this.getEditing();
					$.each(aval, function(ri, row) {
						try {
							var count = t.datagrid('getRows').length;    // row count
						    var selected = t.datagrid('getSelected');
						    var selRow = 0;
						    if (selected) {
						    	selRow = t.datagrid('getRowIndex', selected);
						    }
						    
							var index = _this.pageIndex(t, row.index);
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
						    if (_this.focusedTable != undefined) {
						    	var fTbl = $("#" + _this.focusedTable);
						    	if (fTbl != null && fTbl.length > 0) {
						    		var panel = fTbl.datagrid('getPanel').panel('panel');
						    		panel.attr('tabindex', 0);
						    		panel.focus();
						    	}
						    }
						} catch (err) {
							Util.logErrorInfo("Error parsing 'updateTblRow' property", err);
						}
					});
					var selRows = t.attr('selRows');
					var selCol = t.attr('selectedCol');
					if (selRows && selCol) {
						var rows = selRows.split(',');
						for (var ind=0; ind<rows.length; ind++) {
							rows[ind] = _this.pageIndex(t, rows[ind]);
							t.datagrid('selectCell', {index:rows[ind],field:selCol});
						}
					}
					if (edPars != null) {
						_this.restoreEditing(edPars);
					}
					TooltipOps.addDatagridTooltip(t);
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
						_this.nodeParent = false;
						_this.treeCallLater('select', comp, aval, 20);
					} else {
					    var nodes = comp.tree('getChecked');
					    _this.isOnCheck = false;
					    for (var k = 0; k<nodes.length; k++) {
					    	$(this).tree('uncheck', nodes[k].target);
					    }
					    _this.isOnCheck = true;
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
					} else if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						createImagePanel(comp);
					} else {
						comp.datagrid('reload');
					}
				} else if (akey == 'imageAdded') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						imageAdded(comp, aval);
					}
				} else if (akey == 'imageDeleted') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						imageDeleted(comp, aval);
					}
				} else if (akey == 'reloadUpload') {
					var pan = $('#uploaded' + i);
					if (pan.length > 0)
						pan.panel('refresh');
				} else if (akey == 'selectedRows') {
					if (comp.hasClass('easyui-datagrid')) {
						_this.selectTableRows(comp, aval);
					} else if (comp.hasClass('easyui-treegrid')){
						comp.attr('selRows', aval);
						_this.treeTableSetSelected(comp);
					}
				} else if (akey == 'selectedImg') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						selectImagePanel(comp, aval);
					}
				} else if (akey == 'imgTitleChanged') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						changeImageTitle(comp, aval.index, aval.title);
					}
				} else if (akey == 'imgFileChanged') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						changeImageFile(comp, aval.index, aval.file);
					}
				} else if (akey == 'reloadTreeTable') {
					comp.treegrid('reload');
				}  else if (akey == 'bg') {
					//comp.css({backgroundColor: aval});
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
					    	if (value != null && value.length > 0) {
						    	var index = value.indexOf('.');
						    	var c = 0;
						    	if (index != -1) {
						    		var sub = value.substring(index + 1);
						    		var c = sub.length;
						    	} 
						    	var n = parseFloat(value);
						    	
						    	if (!isNaN(n))
						    		return n.toLocaleString('ru', { minimumFractionDigits: c });
					    	}
					    	return "";
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
					_this.setValue(i, 1);
				}, value);
			}
		} else if (key == 'styleRow') {
			var styleByRow = InterfaceController.styles[i];
			if (styleByRow == null) {
				styleByRow = {};
				InterfaceController.styles[i] = styleByRow;
			}
			var view = $('[field=' + i + ']').first().parents('.datagrid-view');
			var table = view.find('.easyui-datagrid');
			if (table.length == 0)
				table = view.find('.easyui-treegrid');
			
			if (table.length > 0) {
				var delta = _this.pageIndex(table, 0);
				var tds = view.find('[datagrid-row-index] [field=' + i + ']');

				$.each(value, function(k, row) {
					var pIndex = row.index + delta;
					var styleName = "st" + row.hsh;
					styleByRow[pIndex] = {'class': styleName};
					
					var style = InterfaceController.cssStyles[styleName];
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
						InterfaceController.cssStyles[styleName] = style;
						if (style.length > 0)
							$('style').first().append('\n.' + styleName +' {' + style + '}');
						if (divStyle.length > 0)
							$('style').first().append('\n.' + styleName +' div {' + divStyle + '}');
					}
					try {
						var td = $(tds.get(pIndex));
						td.removeClass();
						td.addClass(styleName);
					} catch (err) {
					}
				});
			}
		} else if (key == 'options') {
			if ( comp.attr('type')=='radio' && value.length!=0){
				// очистить таблицу
				comp.empty();
				var colCount = comp.attr("count");
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
					comp.html(str);
				}
			}
		}
	}
	
	enableNaviBtn(btn, enable) {
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

	focusNext(elem) {
		let _this = this;
		
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
		_this.focusedTable = undefined;
		if (nextElem != elem) {
			if (nextElem.hasClass('easyui-datagrid')) {
				var panel = nextElem.datagrid('getPanel').panel('panel');
				panel.attr('tabindex', 0);
				panel.focus();
	
				_this.focusedTable = nextElem.attr('id');
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

	setElemForTopPane() {
		let _this = this;

		let elements = $("[onTop]");
		// отсортировать новые данные
		elements.sort(function (a, b){
			var i = $(a).attr('onTop');
			var j = $(b).attr('onTop'); 
			return i < j ? -1 : i > j ? 1 : 0;
		});
		
		$.each(elements, function(i, element) {
			_this.hideEmptyParentPanels($(element));
		});
		
		$(".ui-toolbar").append(elements);
	}
	
	movePopUpContent(dialogId) {
		let panel = dialogId !== undefined ? $('#' + dialogId) : $('#ui-body');
		let elements = dialogId !== undefined ? $('#' + dialogId +' .popUpPanContent') : $('.popUpPanContent');
		panel.append(elements);
	}
	
	openPopup(uid, row, colUid) {
		let _this = this;
		
		Util.blockPage();
		var par = {"uid": uid, "cmd": "openPopup"};
		if (row != undefined) par["row"] = row;
		if (colUid != undefined) par["cuid"] = colUid;
		
		_this.app.query(par).then(response => {
			response.json().then(json => {
				DataChecker.checkData(json).then(data => {
					_this.data = data;
					if (data.result == 'nop') {
						// no operation
						$('body').unblock();
					} else if (data.result == 'error') {
						Util.alert(data.message, Util.ERROR);
						$('body').unblock();
					} else {
						Util.blockPage();
						_this.selectedRow = [];
						var did = _this.popupcount;
						var dialogId = 'or3_popup' + did;
						
						$('#trash').append($("<div></div>").attr('id', dialogId));
						
						_this.dialogResult[dialogId] = '1';
						$('#' + dialogId).on('click', function (e) {
							// источник события
							var target = e && e.target || event.srcElement;
							return _this.hidePopUp(target, '#' + dialogId);
						});
						
						var w = data.w > $(window).width() + 50 ? $(window).width() + 50 : data.w;
						var h = data.h > ($(window).height() - 20) ? ($(window).height() - 20) : data.h;
			
						var buttons = (data.tv==0) ? [{
							id: "okPopupBtn",
							text: Translation.translation['ok'],
							handler: function() {
								_this.setDialogBtnsEnabled(dialogId, false);
								_this.sendUserDecision("0").then(data => {
									_this.closePopup(dialogId);
								});
							}
						},
						{
							id: "cancelPopupBtn",
							text: Translation.translation['cancel'],
							handler: function() {
								_this.setDialogBtnsEnabled(dialogId, false);
								_this.sendUserDecision("1").then(data => {
									_this.dialogResult[dialogId] = '1';
									$("#" + dialogId).dialog('destroy');
								});
							}
						}] : [];
						
						var toolbar = [{
							id: 'reportBtn' + did,
							text: Translation.translation['print'],
							iconCls: 'icon-rept'
						}];
						
						var dialogOptions = {
							title: data.t,
							width: w,
							height: h,
							closed: false,
							cache: false,
							href: _this.app.restUrl + '&cmd=loadPopup',
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
								InterfaceController.openedDialogs.push(dialogId);
								InterfaceController.popDlg.push(dialogId);
								InterfaceController.popDlgType.push(Util.DLG_POPUP_IFC);
								_this.loadWindowTitle($('#' + dialogId));
								_this.loadDialogReports(did);
								
								$('.easyui-datebox, .easyui-datetimebox').each(function(i) {
									var db = $(this);
									var hidePanel = db.attr('hidePanel') == 'true';
									if (hidePanel) {
										let opts = $(this).datebox("panel").panel("options");
										opts.nopanel = true;
										
										var fld = $(this).parent().find('.textbox-text');
										var cal = $(this).parent().find('.textbox-addon');
										
										if (cal.css('display') != "none") {
											cal.hide();
											fld.width(fld.width() + 18);
										}
									}
								});
								
								$('.easyui-treegrid.datagrid-f').each(function(i) {
									console.log('workworkwork2');
									let treegridComp = $(this);
									let id = treegridComp.attr('id');
									let multi = _this.multiSelection[id];
									if (_this.multiSelection[id] !== undefined) {
										treegridComp.treegrid({
											checkbox: multi,
											onlyLeafCheck: true,
											singleSelect: !multi
										});
										_this.multiSelection[id] = undefined;
									}
								});
			
								_this.loadData({}, true);
								_this.loadData({}, true).then(() => {
									$('#' + dialogId).panel('resize');
									_this.app.resize('#' + dialogId);
								});
								
								_this.preparefileUpload();
								_this.clickPopUpContent(dialogId);
								_this.showPopUpContent();
								_this.movePopUpContent(dialogId);
							},
							onClose: function() {
								_this.dialogResult[dialogId] = '1';
								$("#" + dialogId).dialog('destroy');
							},
							onBeforeDestroy : function() {
								// Удаляем ID-окна из массива окон
								InterfaceController.openedDialogs.pop();
								if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					        		InterfaceController.popDlg.pop();
						        	InterfaceController.popDlgType.pop();
					        	}
								
								if (_this.dialogResult[dialogId] == '1') {
									let opts = $('#' + dialogId).dialog('options');
									let par = {"cmd":"closePopup", "uid":opts.uid, "val":'1'};
						        	if (opts.row != undefined) par["row"] = opts.row;
						        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
			    		        	_this.loadData(par, true);
								}
							},
							buttons: buttons,
							resizable: true
						};
						dialogOptions.toolbar = toolbar;
						
						$('#' + dialogId).dialog(dialogOptions);
			
						_this.popupcount++;
					}
				});
			});
		});
	}
	
	hidePopUp(target, panelId) {
		let _this = this;
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
					
					_this.postAndParseData(par);
					
					$(panels[j]).fadeOut();
					// отправить на сервер событие закрытия панели After
					par["t"] = "a";
					
					_this.postAndParseData(par);
				}
			}
		}
	    return true;
	}
	
	setDialogBtnsEnabled(did, enable) {
		if (enable)
			$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('enable');
		else
			$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('disable');
	}
	
	sendUserDecision(decision) {
		var par = {"cmd":'userDecision', "value":decision, "json":1};
		return this.postAndParseData(par);
	}
	
	destroyPopup(count) {
		let _this = this;
		var popups = $("div[id^='or3_popup']");
		for (var i=1; i<=count; i++) {
			var popup = $(popups.get(popups.size() - i));
			_this.dialogResult[popup.attr('id')] = '-1';
			popup.dialog('destroy');
		}
	}
	
	closePopup(did) {
		let _this = this;
	    _this.showChangeMsg();
	
		_this.dialogResult[did] = '0';
		var opts = $('#' + did).dialog('options');
	    
	    var par = {};
		par["uid"] = opts.uid ;
		par["val"] = 'CHECK';
		if (opts.row != undefined) par["row"] = opts.row;
		if (opts.cuid != undefined) par["cuid"] = opts.cuid;
		par["cmd"] = "closePopup";
	
		_this.loadData(par, true).then(data => {
			if (!data.result || data.result == 'success') {
				$("#" + did).dialog('destroy');
			} else {
				_this.showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
				_this.setDialogBtnsEnabled(did, true);
			}
		});
	}

	loadReports() {
		let _this = this;
		var par = {"getReports":"1"};
		
		_this.app.post(par).then(data => {
			if (data.children && data.children.length > 0) {
				$('#reports').remove();
				$('#reportBtn').removeAttr("reportid").unbind('click');
				$('#reportBtn').removeClass("m-btn").removeClass("m-btn-small").removeClass("l-btn").removeClass("l-btn-small");
				var text = $('#reportBtn').find('.l-btn-text').text();
				if (text != null && text.length > 0) {
					$('#reportBtn').empty();
					$('#reportBtn').text(text);
				}
				if (data.children.length > 1 || data.children[0].children != null) {
					var html = "<div id='reports' class='subReports'>";
					$.each(data.children, function(i, c) {
						var cStyle = (c.v == 0) ? " style='display:none;'" : "";
	
						if (c.children && c.children.length > 0) {
							html += "<div" + cStyle + "><span reportid='"+c.id+"'>" + c.name + "</span><div>";
							$.each(c.children, function(i1, c1) {
								var c1Style = (c1.v == 0) ? " style='display:none;'" : "";
								if (c1.children && c1.children.length > 0) {
									html += "<div" + c1Style + "><span reportid='"+c1.id+"'>" + c1.name + "</span><div>";
									$.each(c1.children, function(i2, c2) {
										var c2Style = (c2.v == 0) ? " style='display:none;'" : "";
										html += "<div" + c2Style + "><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
									});
									html += "</div></div>";
								} else {
									html += "<div" + c1Style + "><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
								}
							});
							html += "</div></div>";
						} else {
							html += "<div" + cStyle + "><a reportid='"+c.id+"'>"+c.name+"</a></div>";
						}
					});
					html+="</div>";
					$('#trash').append(html); 
					$('#reportBtn').show();
					$('#reportBtn').menubutton({
							menu: '#reports'
					});
				} else {
					$('#reportBtn').attr("reportid", data.children[0].id);
					$('#reportBtn').linkbutton();
					$('#reportBtn').bind('click', function() {});
					if (data.children[0].v == 0) {
						$('#reportBtn').hide();
					} else {
						$('#reportBtn').show();
					}
				}
			} else {
				$('#reportBtn').hide();
			}
		});
	}

	loadWindowTitle(dlg) {
		let _this = this;
		var par = {"getTitle":"1"};
		this.postAndParseData(par).then(data => {
			var titlePan = dlg.parent().find('.window-header .panel-title');
			$(titlePan).text(data.title).css('font-size','14px');
			_this.loadStack(titlePan);
		});
	}
		
	loadDialogReports(did) {
		let _this = this;
		var par = {"getReports":"1"};
		this.postAndParseData(par).then(data => {
			if (data.children && data.children.length>0) {
				$('#reports' + did).remove();
	
				if (data.children.length > 1 || data.children[0].children != null) {
					var html = "<div id='reports" + did + "' class='subReports'>";
					$.each(data.children, function(i, c) {
						var cStyle = (c.v == 0) ? " style='display:none;'" : "";
	
						if (c.children && c.children.length > 0) {
							html += "<div" + cStyle + "><span reportid='"+c.id+"'>" + c.name + "</span><div>";
							$.each(c.children, function(i1, c1) {
								var c1Style = (c1.v == 0) ? " style='display:none;'" : "";
								if (c1.children && c1.children.length > 0) {
									html += "<div" + c1Style + "><span reportid='"+c1.id+"'>" + c1.name + "</span><div>";
									$.each(c1.children, function(i2, c2) {
										var c2Style = (c2.v == 0) ? " style='display:none;'" : "";
										html += "<div" + c2Style + "><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
									});
									html += "</div></div>";
								} else {
									html += "<div" + c1Style + "><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
								}
							});
							html += "</div></div>";
						} else {
							html += "<div" + cStyle + "><a reportid='"+c.id+"'>"+c.name+"</a></div>";
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
				} else {
					$('#reportBtn' + did).attr("reportid", data.children[0].id);
					$('#reportBtn' + did).find('.l-btn-text').text(data.children[0].name);
					if (data.children[0].v == 0) {
						$('#reportBtn' + did).hide();
						var shadow = $('#reportBtn' + did).parents('.panel').first().next();
						shadow.height(shadow.height() - 26);
					}
				}
			} else {
				$('#reportBtn' + did).hide();
				var shadow = $('#reportBtn' + did).parents('.panel').first().next();
				shadow.height(shadow.height() - 26);
			}
		});
	}
	
	loadStack(container) {
		let _this = this;
		var par = {"cmd":"getStack"};
		
		this.postAndParseData(par).then(data => {
			_this.showStack(data, container);
		});
	}
	
	showStack(stack, container) {
		if (container != null) {
			var dBody = $(container).parent().parent().find('.window-body');
			dBody.height(dBody.height());
		}
	}
	
	preparefileUpload(cont) {
		let _this = this;
		if (cont == null) cont = $('body');
		var uploads = cont.find('.or3-file-upload');
		for (var i=0; i<uploads.length; i++) {
			var upload = $(uploads.get(i));
			upload.fileupload({
				autoUpload: true,
				dropZone: upload.parent(),
				pasteZone: upload.parent(),
				sequentialUploads: true,
			    url: _this.app.restUrl + '&uid=' + upload.attr('id'),
		        dataType: 'json',
		        done: function (e, data) {
		        	if (data.result.result == 'success') {
			        	_this.endEditing();
		                _this.showChangeMsg();
			            _this.loadData({}, true);
			            
		        	} else {
		        		Util.alert(data.result.message, Util.ERROR);
		        		$('body').unblock();
		        	}	
		        },
		        add: function (e, data) {
		        	Util.blockPage();
		            data.submit();
		        },
		    });
		}
	}
	
	clickPopUpContent(parentId) {
		let _this = this;
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
					_this.postAndParseData(par);
					
					$(content).fadeOut();
					// отправить на сервер событие закрытия панели After
					par["t"] = "a";
					_this.postAndParseData(par);
			    }else{
				    // отправить на сервер событие открытия панели Before
					var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"b"};
					_this.postAndParseData(par);
			
					var left = $(this).attr('sub') ? $(this).offset().left + $(this).width() : $(this).offset().left;
					var top = $(this).attr('sub') ? $(this).offset().top : $(this).offset().top + $(this).height() + 2;

			    	//var left = $(this).attr('sub') ? $(this).width() : $(this).offset().left;
			    	//var top = $(this).attr('sub') ? -$(this).height() : $(this).offset().top + $(this).height() + 2;
			    	
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
							_this.postAndParseData(par);
							
							$(panels[j]).fadeOut();
							// отправить на сервер событие закрытия панели After
							par["t"] = "a";
							_this.postAndParseData(par);
						}
					} 
				    // показать панель
				    $(content).fadeIn();
				    // отправить на сервер событие открытия панели After
					var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"a"};
					_this.postAndParseData(par);
			    }
			}
		    // если панель является подпанелью, то запретить всплытие события
	    	return $(this).attr('sub') ? false : true;
		});
	}
	
	showPopUpContent(){
		let _this = this;
		$('.popUpPan.asMenu:not(.hover)').hover(
			function(e) {
				_this.showPopupPanel($(this));
			},
			function(e) {
				_this.hidePopupPanel($(this));
			}
		);
	}
	
	showPopupPanel(popup) {
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
		this.postAndParseData(par);
	
		var left = popup.attr('sub') ? popup.offset().left + popup.width() : popup.offset().left;
		var top = popup.attr('sub') ? popup.offset().top : popup.offset().top + popup.height() + 2;
		
		$(content).css('left', left);
		$(content).css('top', top);
		// показать панель
		$(content).fadeIn(500);
		// отправить на сервер событие открытия панели After
		var par = {"cmd":"showPopUp","uid":popup.attr('id'),"t":"a"};
		this.postAndParseData(par);
	}
	
	hidePopupPanel(popup) {
		var popID = popup.attr('rel'); 
		var content = $(popID);
	    // отправить на сервер событие скрыть панель Before
		var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"b"};
		this.postAndParseData(par);
	    // отправить на сервер событие скрыть панель After
		var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"a"};
		this.postAndParseData(par);
	    // скрыть панель
		$(content).fadeOut(100);
	}

	forseSaveChanges(initiator) {
		this.endEditing();
		var par = {"cmd":"fcommit"};
		return this.postAndParseData(par);
	}
	
	showErrors(errors, filePath, fileName, command, btn1show, btn1text, popup) {
		let _this = this;
		
		_this.errorUtil.clearAllErrorType();
	
		var content = this.errorUtil.higlightErrors(errors);
		
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		if (btn1show == true) {
			var button1 = {
				text: btn1text,
				handler: function() {
					_this.dialogResult[dialogId] = '0';
					$("#" + dialogId).dialog('destroy');
					
					if (popup)
						popup.dialog('destroy');
					
					if (popup == null && command == "nextStep")
						_this.nav.toActiveMain();
				}
			};
			buttons.push(button1);
		}	
		var button2 = {
			text: Translation.translation['continue'],
			handler: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button2);
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+Translation.translation['errors']+'</div>',
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
							var url = _this.app.restUrl + "&rnd=" + Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
							$('#report_frame').attr('src', url);
						}
					}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(Util.DLG_ERRORS);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				let opts = $(this).dialog('options');
	
	        	if (_this.dialogResult[dialogId] == '0') {
	    			var par = {};
	    			par["cmd"] = command;
	    			par["result"] = "save";
	    			Util.blockPage(Translation.translation['saving']);
	    			
					_this.app.post(par).then(data => {
						if (data.result == "success") {
							_this.hideChangeMsg();
							_this.loadData({});
						} else {
							Util.alert(Translation.translation['error'], Util.ERROR);
						}
	    				$('body').unblock();
					}, () => {
						$('body').unblock();
					});
	        	}
	        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}

	showForceErrors(errors, btn1show, filePath, fileName, btn1text, btn2text) {
		let _this = this;
		
		_this.errorUtil.clearAllErrorType();
	
		var content = this.errorUtil.higlightErrors(errors);
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		if (btn1show == true) {
			var button1 = {
				text: btn1text,
				handler: function() {
					_this.dialogResult[dialogId] = '0';
					$("#" + dialogId).dialog('destroy');
				}
			};
			buttons.push(button1);
		}	
		var text2 = btn2text == undefined ? Translation.translation['continue'] : btn2text;
		var button2 = {
			text: text2,
			handler: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button2);
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+Translation.translation['errors']+'</div>',
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
						var url = _this.app.restUrl + "&rnd=" + Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
						$('#report_frame').attr('src', url);
					}
				}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(Util.DLG_NO_SEND);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				Util.blockPage();
				
				let opts = $(this).dialog('options');
	
				var par = {};
	        	if (_this.dialogResult[dialogId] == '0') {
	        		par["commitResult"] = 1;
	        	} else {
	        		par["commitResult"] = 0;
	        	}
	        	_this.app.query(par).then(response => {
		        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
		        		InterfaceController.popDlg.pop();
			        	InterfaceController.popDlgType.pop();
		        	}
		        });
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}

	showPopupErrors(errors, filePath, fileName, opts, popup, fatal, isDataIntegrityControl) {
		
		let _this = this;
		var content = this.errorUtil.higlightErrors(errors);
		
		this.fatal = fatal;
		this.isDataIntegrityControl = isDataIntegrityControl;
		this.opts = opts;
	
		var dialogId = 'or3_popup' + this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
	
		var buttons = [];
	
		var button1 = {
			text: Translation.translation['continue2'],
			handler: function() {
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button1);
		
		if (fatal && isDataIntegrityControl) {
			var button3 = {
				text: Translation.translation['cancel'],
				handler: function() {
					$("#" + dialogId).dialog('destroy');
					var openedDialogs = InterfaceController.openedDialogs;
					if (openedDialogs.length > 0) {
						var dialogId1 = openedDialogs[openedDialogs.length - 1];
						_this.setDialogBtnsEnabled(dialogId1, false);
						_this.sendUserDecision("1");
						_this.dialogResult[dialogId1] = '1';
						$("#" + dialogId1).dialog('destroy');
					}
				}
			};
			buttons.push(button3);
		} else {
			var button2 = {
				text: Translation.translation['save'],
				handler: function() {
					$("#" + dialogId).dialog('destroy');
					
					var par = {};
		        	par["uid"] = opts.uid ;
		        	par["val"] = '0';
		        	if (opts.row != undefined) par["row"] = opts.row;
		        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
		        	par["cmd"] = "closePopup";
		        	_this.loadData(par, true).then(() => {
						popup.dialog('destroy');
					});
				}
			};
			buttons.push(button2);
		}
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+Translation.translation['errors']+'</div>',
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
						var url = _this.app.restUrl + "&rnd=" + Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
						$('#report_frame').attr('src', url);
					}
				}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(Util.DLG_POPUP_ERRORS);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					InterfaceController.popDlg.pop();
					InterfaceController.popDlgType.pop();
				}		
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}
	
	focusComponent(uuid, rownum) {
		let _this = this;
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
				
				var openedDialogs = InterfaceController.openedDialogs;
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
	
	downloadFile(e, id, row, col) {
		let _this = this;
		var curTime = (new Date).getTime();
		e.stopPropagation();
		e.preventDefault();
		
		if (_this.lastDownloadTime == 0 || curTime - _this.lastDownloadTime > _this.waitTime) {
			_this.lastDownloadTime = curTime;
			var par = {};
			par["uid"] = id;
			par["cmd"] = "viewFile";
			if (row != null)
				par["row"] = row;
			if (col != null)
				par["col"] = col;
			
			_this.app.post(par).then(data => {
				_this.loadData({}, true);
				if (data.result == "success") {
					var url = _this.app.restUrl + "&trg=frm&cmd=opf&fn=" + encodeURIComponent(data.file);
					if (url.indexOf("/") > 0) url = "/" + url;
					url += "&rnd=" + Util.rnd();
					
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
			});
		}
		return false;
	}
	
	openStartDialog(uid) {
		let _this = this;
		Util.blockPage();
		var par = {};
		par["uid"] = uid;
		par["cmd"] = "openTask";
		par["size"] = 1;
		
		_this.app.post(par).then(data => {
			if (data.result == 'error') {
				Util.alert(data.message, Util.ERROR);
			} else {
				Util.blockPage();
				_this.selectedRow = [];
				var dialogId = 'or3_popup' + _this.popupcount;
				$('#trash').append($("<div></div>").attr('id', dialogId));
				
				_this.dialogResult[dialogId] = '1';
				_this.dialogOpened = dialogId;
				
				$('#' + dialogId).dialog({
					title: '<div style="font-size:14px; color:#777;">'+data.t+'</div>',
					width: data.w,
					height: data.h,
					closed: false,
					cache: false,
					href: _this.app.restUrl + '&cmd=openTask&uid=' + uid + "&rnd=" + Util.rnd(),
					modal: true,
					uid: uid,
					onOpen: function() {
						$('#' + dialogId +' .panel').append($("<div class=\"glassDialog\"></div>"));
						$('#' + dialogId + ' .glassDialog').height($('#' + dialogId +' .panel').height()).width($('#' + dialogId +' .panel').width());
					},
					onLoad: function() {
						// Добавляем ID-окна в массив окон
						InterfaceController.openedDialogs.push(dialogId);
						InterfaceController.popDlg.push(dialogId);
						InterfaceController.popDlgType.push(Util.DLG_OPEN_AT_START);
						_this.loadData({});
						
						$('.clean-btn').click(function(e) {
							var par = {};
							par["uid"] = $(this).attr('id').substring(3);
							par["cmd"] = "clr";
							_this.postAndParseData(par);
							return false;
						});
						
						_this.preparefileUpload();
					},
					onClose: function() {
						_this.dialogResult[dialogId] = '1';
						$('#' + dialogId).dialog('destroy');
					},
					onBeforeDestroy : function() {
						// Удаляем ID-окна из массива окон
						InterfaceController.openedDialogs.pop();
						_this.dialogOpened = null;
						
						if (_this.dialogResult[dialogId] == '1' || _this.dialogResult[dialogId] == 1) {
							_this.app.nav.cancelStart(uid);
						}
						if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
			        		InterfaceController.popDlg.pop();
				        	InterfaceController.popDlgType.pop();
			        	}
					},
					buttons: [{
						text: Translation.translation['ok'],
						handler: function() {
							_this.setDialogBtnsEnabled(dialogId, false);
							Util.blockPage();
							_this.dialogResult[dialogId] = '0';
							_this.app.nav.nextStep(true, dialogId);
						}
					},
					{
						text: Translation.translation['close'],
						handler: function() {
							_this.setDialogBtnsEnabled(dialogId, false);
							$('body').unblock();
							_this.dialogResult[dialogId] = '1';
							$('#' + dialogId).dialog('destroy');
						}
					}]
				});
				_this.popupcount++;
			}
		}, 'json');
	}

	sendFile() {
		var file = document.getElementById("chooser").files.item(0);
		$('#getFile').dialog("close");
		Util.blockPage();
		
	    var url = this.app.restUrl + "&rnd=" + Util.rnd();
	   
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
	
	reload(uid) {
		let _this = this;
		var par = {"cmd":"reload","uid":uid};
		this.app.query(par).then(content => {
			var parent = $('#' + uid).parent();
			parent.html(content);
			parent.find('.easyui-panel:not(.tamur-tabs)').panel();
			parent.find('.easyui-datagrid').datagrid();
			preparefileUpload(parent);
	
			var par = {};
			par["rnd"] = Util.rnd();
			par["getChange"] = "";
			_this.postAndParseData(par);
		});
	}
	
	closeIfc() {
		this.endEditing();
		Util.blockPage(Translation.translation['saving']);
		var par = {"cmd":"closeIfc"};
		
		this.app.query(par).then(response => {
			$('body').unblock();
		});
	}

	resetChanges() {
		let _this = this;
		Util.blockPage(Translation.translation['canceling']);
		var par = {"cmd":"rollback"};
		this.loadData(par).then(data => {
			_this.hideChangeMsg();
			InterfaceController.styles = {};
		});
	}
	
	saveChanges() {
		let _this = this;
		this.endEditing();
		Util.blockPage(Translation.translation['saving']);
		var par = {"cmd":"commit"};
			
		this.app.post(par).then(data => {
			if (data.result == "success") {
				_this.hideChangeMsg();
			} else {
				if (data.result == "fatal") {
					$('body').unblock();
				}
				_this.showErrors(data.errors, data.path, data.name, "commit", true, Translation.translation['save']);
			}
			$('body').unblock();
		});
	}

	dgBtnAction2(com, i, btn) {
		if (!btn.hasClass('l-btn-disabled')) {
			this.dgBtnAction(com, i, btn);
		}
	}
	
	dgBtnAction(com, i, btn) {
		let _this = this;
		this.endEditing().then(() => {
		    var par = {};
			par["uid"] = i;
			if(com == "del") {
				var objs = $('#'+i).datagrid('getSelections');
				if(objs.length > 0) {
					var indxs = [objs.length];
					for (var j = 0; j < objs.length; j++) {
						indxs[j] = _this.globalIndex($('#'+i), $('#'+i).datagrid('getRowIndex', objs[j]));
					}
					var idx = indxs.join(",");
					par["idx"] = idx;
				}
			}
			
			var st = com.indexOf('media/img/');
			if (st>-1) {
				var fn = com.indexOf('.gif', st);
				par["com"] = com.substring(st+10,fn);
			} else {
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
			
			_this.app.post(par).then(data => {
				if (data.changes) {
					_this.parseData(data);
					_this.reloadTables();
				} else {
			        if (data.result == "success") {
			        	$.messager.confirm('', data.message, function(e){
			                if (e){
			                	par["sure"] = 1;
			        			_this.loadData(par).then(data => {
			        				_this.showChangeMsg();
			        			});
			                }
			            });
			        }
				}
			});
		});
	}
	
	reportClick(btn) {
		let _this = this;
		var par = {};
		par["id"] = btn.attr('reportid');
		par["cmd"] = "print";
		
		_this.app.post(par).then(data => {
			if (data.result == 'error') {
				Util.alert(data.message, Util.ERROR);
			} else if (data.file) {
				var url = _this.app.restUrl + "&rnd=" +Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(data.file);
				$('#report_frame').attr('src', url);
			}
		});
	}
	
	changeInterfaceLang(code, e) {
		e.preventDefault();
	
		Util.blockPage();
		this.app.query({"setLang":code}).then(data => {
			var url = "index.jsp?guid=" + guid + "&rnd=" + Util.rnd() + document.location.hash;
			window.location.href = url;
		});
	}
	
	showOptions(options) {
		let _this = this;
		var content = "<table class='options'><tr><td>";
		$.each(options, function(i, option) {
			content += "<tr><td><input type='radio' name='option' value='" + i + "'/>" + option.o + "</td></tr>";
		});
		content += "</table>";
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
	
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		var button1 = {
			text: 'Ok',
			handler: function() {
				_this.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button1);
		var button2 = {
			text: Translation.translation['cancel'],
			handler: function() {
				_this.dialogResult[dialogId] = '1';
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
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(Util.DLG_NO_SEND);
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				var par = {"optionResult": -1};
	        	if (_this.dialogResult[dialogId] == '0') {
	        		var checked = $('#' + dialogId).find('input:checked');
	        		if (checked.length > 0)
	        			par["optionResult"] = checked.val();
	        	}
	        	_this.app.query(par).then(response => {
		        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
		        		InterfaceController.popDlg.pop();
			        	InterfaceController.popDlgType.pop();
		        	}
		        });
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}
	
	openTree(uid, title, row, colUid) {
		let _this = this;
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color:#777;">'+title+'</div>',
			width: 800,
			height: 600,
			closed: false,
			cache: false,
			href: _this.app.restUrl + '&cmd=openTree&uid=' + uid + (row != undefined ? '&row=' + row + "&cuid=" + colUid : ""),
			modal: true,
			uid: uid,
			row: row,
			cuid: colUid,
			onLoad: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(Util.DLG_TREE_FIELD);
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				_this.searchCount = -1;
				let opts = $(this).dialog('options');
	        	if (_this.dialogResult[dialogId] == 0) {
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
	        		_this.loadData(par);
	        	}
	        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
			},
			buttons: [{
				text: Translation.translation['ok'],
				handler: function() {
					_this.dialogResult[dialogId] = '0';
	                _this.showChangeMsg();
					$("#" + dialogId).dialog('destroy');
				}
			},
			{
				text: Translation.translation['cancel'],
				handler: function() {
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				}
			}]
		});
		
		_this.popupcount++;
	}

	uploadImage(uid) {
		let _this = this;
		$('#upload').fileupload({
			dropZone: $('#' + uid),
			pasteZone: $('#' + uid),
		    url: _this.app.restUrl + '&uid=' + uid,
		    dataType: 'json',
	        done: function (e, data) {
	        	if (data.result.result == 'success') {
	                _this.showChangeMsg();
	        		_this.loadData({}, true);
	        	} else
	        		Util.alert(data.result.message, Util.ERROR);
	        }
		}).fileupload('option', {acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i}).click();
	}
	
	displayEmptyParentPanels(comp) {
		if (comp.parents('.popUpPanContent').length == 0) {
			let _this = this;
			let parentPanels = comp.parents('.orpanel');
			$.each(parentPanels, function(i, panel) {
				if ($(panel).attr('id') && !_this.hiddenPanels[$(panel).attr('id')]) {
					$(panel).show();
				}
			});
			
			let childPanels = comp.find('.orpanel');
			$.each(childPanels, function(i, panel) {
				if (!_this.hiddenPanels[$(panel).attr('id')]) { 
					let children = $(panel).find(':not(td, tr, tbody, [onTop], [onTop] *)')
					var show = false;
					$.each(children, function(k, child) {
						if ( $(child).css('display') != 'none' && $(child).css("visibility") != "hidden") {
							show = true;
						}
					});
						
					if (show) {
						$(panel).show();
					}
				}
			});
		}
	}			

	hideEmptyParentPanels(comp) {
		if (comp.parents('.popUpPanContent').length == 0) { 
			let parentPanels = comp.parents('.orpanel');
			$.each(parentPanels, function(i, panel) {
				if ($(panel).find(':not(td, tr, tbody, [onTop], [onTop] *):visible').length == 0)
					$(panel).hide();
			});
		}
	}

	setFocus() {
		// чтобы при заходе в систему курсор сразу встал на input(глобальный поиск)
		document.getElementById("privateDeal").focus();
	}

	changeTooltipPref(val) {
		// всплывающие подсказки вкл, выкл
		val == 'true' ? true : false;
		var par = {"setTooltipPref":val};

		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeTooltipPref")
			}
		});
		
		// дописать остальную функцию 
		// attachProcessTooltips('processess');
	}

	changeNoteSoundPref(val) {
		// звук вкл, выкл
		val == 'true' ? true : false;
		var par = {"setNoteSoundPref":val};

		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeNoteSoundPref")
			}
		});
	}

	changeInstantECPPref(val) {
		// Хранить пароль ЭЦП во время сессии
		var par = {"setInstantECPPref": val};
	
		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeInstantECPPref")
			}
		});
	}

	changePwdDialog(obligatory) {
		let this_ = this;
		var dialogId = 'or3_popup' + this_.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		this_.dialogResult[dialogId] = '1';
	
		var buttonOk = {
			text: Translation.translation['change'],
			handler: function() {
				this_.dialogResult[dialogId] = '0';
				var par = {};
				par["cmd"] = "changePass";
				par["oldPass"] = $('#' + dialogId).find('[uid="oldPass"]').val();
				par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
				par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
				$.post(window.mainUrl + "&rnd=" + Util.rnd(), par, function(json) {
					DataChecker.checkData(json).then(data => {
						if (data.result == 'error') {
							alert(data.message, Util.ERROR);
						} else {
							$("#" + dialogId).dialog('destroy');
							alert(data.message);
						}
					});
				}, 'json');
	
			}
		};
		
		var buttonCancel = {
			text: Translation.translation['cancel'],
			handler: function() {
				this_.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
		
		$('#' + dialogId).dialog({
			title: Translation.translation['passChange'],
			width: 300,
			height: 200,
			closed: false,
			cache: false,
			closable: obligatory != true,
			href: window.contextName + '/jsp/pwd.jsp?guid=' + guid + '&rnd=' + Util.rnd(),
			modal: true,
			onLoad: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(this_.DLG_CHANGE_PD);
			},
			onClose: function() {
				this_.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					InterfaceController.popDlg.pop();
					InterfaceController.popDlgType.pop();
				}
			},
			buttons: buttons
		});
		
		this_.popupcount++;
	}
}
