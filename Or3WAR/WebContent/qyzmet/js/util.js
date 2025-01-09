import {InterfaceController} from './ifc.js';
import {Translation} from './translation.js';

export class Util {
	static INFO = 0;
	static ERROR = 1;
	static WARNING = 2;
	
	static DLG_ALERT = 0;
	static DLG_CONFIRM = 10;
	
	static DLG_NO_SEND = 1;
	static DLG_TREE_FIELD = 2;
	static DLG_OPEN_AT_START = 3;
	static DLG_POPUP_IFC = 4;
	static DLG_CHANGE_PD = 5;
	static DLG_ERRORS = 6;
	static DLG_POPUP_ERRORS = 7;
	
	static alertOldZindex;
	static dialogZindex = 345000;
	static messagerZindex = 350000;
	static alertCommand = false;
	static alertIsLogout = false;
	
	static restUrl = "";

	static rnd() {
		var array = new Uint32Array(1);
		var crypto = window.crypto || window.msCrypto;
		crypto.getRandomValues(array);
		return array[0];
	};
	
	static generateGUID() {
		var array = new Uint16Array(8);
		var crypto = window.crypto || window.msCrypto;
		crypto.getRandomValues(array);
		
		var res = ('000' + array[0].toString(16)).substr(-4) +
			('000' + array[1].toString(16)).substr(-4) + '-' +
			('000' + array[2].toString(16)).substr(-4) + '-' +
			('000' + array[3].toString(16)).substr(-4) + '-' +
			('000' + array[4].toString(16)).substr(-4) + '-' +
			('000' + array[5].toString(16)).substr(-4) +
			('000' + array[6].toString(16)).substr(-4) +
			('000' + array[7].toString(16)).substr(-4);
		
		return res;
	}
	
	static parseHash(url) {
		return (function() {
				var ret = {}, seg = url.replace(/^\#/, '').split('&');
				
				for (var i=0; i < seg.length; i++) {
					if (!seg[i]) {
						continue;
					}
					var s = seg[i].split('=');
					ret[s[0]] = s[1];
				}
				return ret;
			})();
	};
	
	static makeUrl(pars) {
		var url = window.mainUrl;
		for (var prop in pars) {
			url += "&" + encodeURIComponent(prop) + "=" + encodeURIComponent(pars[prop]);
		}
		return url;
	};
	
	static delay(ms) {
	  return new Promise((resolve, reject) => {
	    setTimeout(resolve, ms);
	  });
	};
	
	static slide(msg) {
		$.messager.show({
			msg: msg,
			timeout: 8000,
			showType:'slide'
		});
	}
	static alert(msg, type, command, isLogout) {
		let _this = this;
		let language = "ru";

		_this.alertCommand = command;
		_this.alertIsLogout = isLogout;
		
		// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
		if($('#topLangKz').hasClass("selected")) {
			language = "kz";
		} else {
			language = "ru";
		}

		if (msg == null || msg.length == 0) msg = language == "ru" ? "-Пустое сообщение-" : "- Бос хабарлама-";
	
		var alertTitle = '';
		var icon='';
	
		if(type == _this.ERROR){
			alertTitle = language == "ru" ? 'Ошибка' : 'Қателік';
			icon='error';
		}
		else if(type == _this.WARNING){
			alertTitle = language == "ru" ? 'Предупреждение' : 'Ескерту';
			icon='warning';
		}
		else if(type == _this.INFO){
			alertTitle = language == "ru" ? 'Уведомление' : 'Хабарлама';
			icon='info';
		}
	
		var a = msg.length;
		var width = '200';
		if (a < 20){
			width = '200';
		} else if (a<61) {
			width = '450'
		} else {
			width = '600'
		}
		msg = "<div style='text-align:left'>" + (msg.replace ? msg.replace(/\n/g, '<br/>') : msg) + "</div>";
	
		var oldZindex = $.fn.window.defaults.zIndex;
		_this.alertOldZindex = oldZindex;
		$.fn.window.defaults.zIndex = _this.messagerZindex++;
		$.messager.defaults.zIndex = ++_this.messagerZindex;
	
		InterfaceController.popDlg.push(0);
		InterfaceController.popDlgType.push(_this.DLG_ALERT);
		$.messager.alert({
			closable: false,
			title: alertTitle,
	        msg:msg,
	        width: width,
	        height: a>3000 ? 500 : 'auto',
	        icon:icon,
	        showType:null,
	        fn: function(e){
	        	if(InterfaceController.popDlgType[InterfaceController.popDlgType.length-1] == _this.DLG_ALERT){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
	        	$.fn.window.defaults.zIndex = oldZindex;
	        	if (command) {
	        		if (isLogout) {
	        			_this.logout();
	        		} else {
	        			_this.query({"alert":"0"});
	        		}
	        	}
	        }
	    });
	}
	
	static confirmMessage(msg, resolve) {
		let _this = this;
		let myTitle = "Сообщение",
			okay = "Да",
			cancel = "Отмена";

		var a = msg.length;
		var width = '200';
		if (a<20){
			width = '200';
			msg = "<div style='text-align:left'>"+msg+"</div>";
		} else if (a<61) {
			width = '450'
		} else {
			width = '600'
		}
		var oldZindex = $.fn.window.defaults.zIndex;
		_this.alertOldZindex = oldZindex;
	
		$.fn.window.defaults.zIndex = _this.messagerZindex++;
		$.messager.defaults.zIndex = ++_this.messagerZindex;
		
		if (typeof resolve !== "function") {
			InterfaceController.popDlg.push(0);
			InterfaceController.popDlgType.push(_this.DLG_CONFIRM);
		}
		// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
		if($('#topLangKz').hasClass("selected")) {
			myTitle = "Хабарлама";
			okay = "Ия";
			cancel = "Болдырмау";
		} else {
			myTitle = "Сообщение";
			okay = "Да";
			cancel = "Отмена";
		}

		$.messager.confirm({
			title: myTitle,
			ok: okay,
			cancel: cancel,
	        msg:msg,
	        width: width,
	        height: a>3000 ? 500 : 'auto',
	        icon:'question',
	        showType:null,
	        fn: (typeof resolve === "function") ? resolve : function(e){
	        	$.fn.window.defaults.zIndex = oldZindex;
	        	if(InterfaceController.popDlgType[InterfaceController.popDlgType.length-1] == _this.DLG_CONFIRM){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
	    		if (e) {
	    			_this.query({"confirm":"3"});
	    		} else {
	    			_this.query({"confirm":"4"});
	    		}
	    	}
	    });
    }
	
	static blockPage(title) {
		if (typeof title == 'undefined') {
			title = Translation.translation['wait'];
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
	};
	
	static blockDialog(dlg, title) {
		if (typeof title == 'undefined') {
			title = Translation.translation['wait'];
		}
		dlg.block({
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
	};

	static logErrorInfo(msg, err) {
		console.log(msg);
		
		if (console.trace)
			console.trace();
		if (err.stack)
			console.log(err.stack);
	}

	static sanitizeHtml(s) {
		return this.encodeHTML(s).replace('/\r/g', '').replace(/\n/g, '');
	}
	
	static encodeHTML(s) {
	    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;')
	    		.replace(/\//g,'&#47;').replace(/\\/g,'&#92;');
	}
	static openDocument(id, ext, fn) {
		var url = Util.restUrl;
		if (ext == null && fn == null) {
			url += "&trg=frm&cmd=opf&fn=" + encodeURIComponent(id);
			if (url.indexOf("/") > 0) url = "/" + url;
			url += "&rnd=" + Util.rnd();
		} else {
			url += "&sfunc&cls=XmlUtil&name=getHelpFile&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + id + "&rnd=" + Util.rnd();
		}
		$('#report_frame').attr('src', url);
	}

	static query(params) {
		return fetch(this.restUrl, {
			method: 'POST',
	    	credentials: 'include',
			body: $.param(params)
	    });
	}

	static post2(par) {
		let _this = this; 
		return new Promise((resolve, reject) => {
			$.post(_this.restUrl, par, resolve, 'json');
		});
	}

	
	static post(par) {
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

	static logout() {
		this.post({"cmd":"ext", "json":1}).then(data => {
			window.location.href = window.contextName + "/qyzmet/login.jsp?rnd=" + Util.rnd();
		});
	}
	
	static playNoteSound() {
	}
	
	static b64DecodeUnicode(str) {
	    return decodeURIComponent(atob(str).split('').map(function(c) {
	        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
	    }).join(''));
	}
	
	static b64EncodeUnicode(str) {
	    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
	        function toSolidBytes(match, p1) {
	            return String.fromCharCode('0x' + p1);
	    }));
	}
}

export class DataChecker {
	static checkData(data) {
		return new Promise((resolve, reject) => {
			if (data && data.session && data.session == "off") {
				window.location.href = window.contextName + "/qyzmet/login.jsp?rnd=" + Util.rnd();
				reject(data);
			}
			resolve(data);
		});
	}
}