import {Util} from './util.js';
import {Translation} from './translation.js';
import {NCA} from './nca.js';
                         
export class Login {

	sLogin = false;
	language = "KZ";

	constructor(contextName, mainUrl, lang) {
		this.guid = Util.generateGUID();

		this.contextName = contextName;
		Util.restUrl = mainUrl;
		this.lang = lang;
		this.language = localStorage.getItem("EkyzmetLanguage");
	}

	init() {
		let _this = this;
		
		Translation.init(this.lang);
		this.browser = this.testBrowser();
		
		this.ncaLayer = new NCA();
		
		this.getContactInfo();

		$('#loginBtn').click(function() {
			$('#loginBtn').css({"background-color": "#298F94"});
			return _this.login(false);
		});
		$('#password').keydown(function(event) {
			if(event.keyCode === 13) {
				$('#loginBtn').css({"background-color": "#298F94"});
				return _this.login(false);
			}
		});
		$('#user').keydown(function(event) {
		 	if(event.keyCode === 13) {
				$('#loginBtn').css({"background-color": "#298F94"});
				return _this.login(false);
			}
		});
		$("#topLangKz").on("click", function() {
			_this.setLangToolTip("KZ");
		});

		$("#topLangRu").on("click", function() {
			_this.setLangToolTip("RU");
		});

		$('#loginEcpBtn').click(function() {
			return _this.loginECP();
		});

		$('#clearBtn').click(function() {
			$('#user').val('');
			$('#password').val('');
			$(".login-error-text").html("");
		}).keydown(function(event) {
			if(event.keyCode === 13) {
				$('#user').val('');
				$('#password').val('');
				$(".login-error-text").html("");
			}
		});
		
		$('.btn-login-ecp').click(function(e) {
			$('#login-pass-form').hide();
			$('.btn-login-pass').removeClass('selected');
			$('#login-ecp-form').show();
			$('.btn-login-ecp').addClass('selected');
			$(".login-error-text").html("");
		});
		$('.btn-login-pass').click(function(e) {
			$('#login-pass-form').show();
			$('.btn-login-pass').addClass('selected');
			$('#login-ecp-form').hide();
			$('.btn-login-ecp').removeClass('selected');
			$(".login-error-text").html("");
		});
		
		$('.label-forgot-pass').click(function(e) {
			_this.forgotPD(0);
		});

		$('.tooltip').css({'width':'800px'});
	}
	
	testBrowser() {
		BrowserDetect.init();
	
		if (BrowserDetect.version === "an unknown version" || 
			(
				(BrowserDetect.browser !== 'Chrome' || BrowserDetect.version < 35) 
				&& (BrowserDetect.browser !== 'Firefox' || BrowserDetect.version < 69) 
				&& (BrowserDetect.browser !== 'Explorer' || BrowserDetect.version < 10)
				&& (BrowserDetect.browser !== 'Mozilla' || BrowserDetect.version < 11)
			)
		) {
			$('#loginForm').removeAttr('onsubmit');
			$('#loginECPForm').removeAttr('onsubmit');
			$('#loginBtn').removeAttr('onclick');
			$('#loginECPBtn').removeAttr('onclick');
			$('#resetBtn').removeAttr('onclick');
			$('#loginBtn').hide();
			$('#loginECPBtn').hide();
			$('#resetBtn').hide();
			$('#errmsg').show();
		}
			
		return BrowserDetect.browser+";"+BrowserDetect.version+";"
				+BrowserDetect.OS+";"+BrowserDetect.mobile;
	}

	login(force) {
		this.whatLanguageNow();
		let _this = this,
			wait = this.language == "KZ" ? "Күте тұрыңыз..." : "Подождите...",
			enterTheSystem = this.language == "KZ" ? "Жүйеге кіру" : "Войти в систему";
			
		if (force || !$('#loginBtn').hasClass('btn-disabled')) {
			$('#loginBtn').addClass('btn-disabled').attr("disabled", "disabled").html(`<i class="icon-refresh icon-white icon-refresh-animate"></i> ${wait}`);

			let par = {
				name: $('#user').val(),
				passwd: $('#password').val(),
				browser: this.browser,
				json: 1,
				force: (force ? 1 : 0),
				sLogin: (this.sLogin ? 1 : 0),
				rnd: Util.rnd(),
				lang: this.lang,
				guid: this.guid
			};
			
			Util.post2(par).then(data => {
				if (data.result == "success") {
					if (data.tempReg) {
						alert(data.tempReg);
					}
					window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + Util.rnd();
				} else if (data.passChange == "1") {
					alert(data.message);
					$('.login-error-text').text(data.message);
					_this.changePwdDialog(false);
				}  else if (data.passChange == "2") {
					_this.sLogin = true;
					alert(data.message);
					$('.login-error-text').text(data.message);
					_this.changePwdDialog(false);
				} else if (data.reconnect == "1") {
					if (confirm(data.message)) {
						_this.login(true);
					} else {
						$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
						$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
					}
				} else {
					Util.alert(data.message);
					$('.login-error-text').text(data.message);
					$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
					$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				}
			});
		}
		return false;
	}
	
	loginECP() {
		this.whatLanguageNow();
		let _this = this,
		wait = this.language == "KZ" ? "Күте тұрыңыз..." : "Подождите...",
		actionCanceledUser = this.language == "KZ" ? "Әрекетті пайдаланушы жойды" : "Действие отменено пользователем",
		mistakeCode = this.language == "KZ" ? "Қате коды: " : "Код ошибки: ",
		messageForUser = this.language == "KZ" ? " Хабар: " : " Сообщение: ",
		selectSertificate = this.language == "KZ" ? "Сертификатты таңдаңыз" : "Выбрать сертификат";


		if (!$('#loginEcpBtn').hasClass('btn-disabled')) {
			$('.login-error-text').empty();
			$('#loginEcpBtn').addClass('btn-disabled').attr("disabled", "disabled").html(`<i class="icon-refresh icon-white icon-refresh-animate"></i> ${wait}`);	
			
			var par = {cmd:'getSecret', rnd: Util.rnd(), guid: this.guid, json:1, lang: this.lang};

			Util.post2(par).then(data => {
				if (data.secret) {
					let xml = '<login><secret>' + secret + '</secret></login>';
					_this.ncaLayer.signXml(NCA.COMMON, NCA.AUTH, xml).then(data2 => {
						if (data2.responseObject) {
							let signedData = data2.responseObject;
							par = {
								secret: data.secret,
								signedData: signedData,
								browser: this.browser,
								rnd: Util.rnd(),
								guid: this.guid,
								json: 1,
								lang: this.lang
							};
							_this.sendLoginInfo(par);
						} else {
							let error = (data2.message === 'action.canceled')
								? actionCanceledUser
								: mistakeCode + data2.code + messageForUser + data2.message;
							console.error("msg text: " + error);
							$('.login-error-text').text(error);
							$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
						}
					}, error => {
						console.error("error text: " + error);
						$('.login-error-text').text(error);
						$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
					});
				} else {
					Util.alert(data.message);
					$('.login-error-text').text(data.message);
					$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
				}
			});
		}
		return false;
	}
	
	sendLoginInfo(par) {
		this.whatLanguageNow();
		let _this = this,
			selectSertificate = this.language == "KZ" ? "Сертификатты таңдаңыз" : "Выбрать сертификат";

		Util.post2(par).then(data => {
			if (data.result == "success") {
				if (data.dl) {
					Util.alert(data.dl);
				}
				if (data.tempReg) {
					Util.alert(data.tempReg);
				}
				window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + Util.rnd();
			} else if (data.reconnect == "1") {
				Util.confirmMessage(data.message, function(e) {
					if (e) {
						par["force"] = 1;
						_this.sendLoginInfo(par);
					} else {
						$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
					}
				});
			} else {
				Util.alert(data.message);
				$('.login-error-text').text(data.message);
				$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
			}
		});
	}
	
	reset() {
		if (!$('#resetBtn').hasClass('btn-disabled')) {
			$('#loginForm')[0].reset();
			$('#loginECPForm')[0].reset();
		}
	}
	
	forgotPD(purpose) {
		this.whatLanguageNow();
		let _this = this,
			enterUserName = this.language == "KZ" ? "Пайдаланушы атын енгізіңіз" : "Введите имя пользователя";

		var un = $('#user').val();
		if (un == null || un.length < 1) {
			Util.alert(enterUserName);
		} else {
			var par = {};
			par["arg0"] = $('#user').val();
			par["cmd"] = "execute";
			par["cls"] = "ВСПОМОГАТЕЛЬНЫЙ КЛАСС";
			par["name"] = "restoreAccess";
			par["arg1"] = purpose;
			par["json"] = 1;
			par["rnd"] = Util.rnd();
			par["arg2"] = this.language;
		  	Util.post2(par).then(data => {
				console.log(data);
				let msg = _this.language == "KZ" ? data.msgKz : data.msgRu;
		  		if (purpose == 0 && data.code == 0) {
		  			Util.confirmMessage(msg, function(e) {
		  				if (e)  
		  					_this.forgotPD(1);
		  			});
		  		} else {
		 			Util.alert(msg);
		  		}
			});
		}
	}
	
	getContactInfo() {
		this.whatLanguageNow();
		let _this = this;
		var par = {"cmd":'contactInfo', "additionalInfo": "true", "json":1};
		let consultant = this.language == "KZ" ? "Кеңесші" : "Консультант",
			supportService = this.language == "KZ" ? "Қолдау қызметі" : "Служба поддержки",
			downloadCryptoSocket = this.language == "KZ" ? "ЭЦҚ-мен жұмыс істеу үшін бағдарламаны жүктеу және орнату (CryptoSocket, 03.09.2021 жаңартылған)" : "Скачать и установить программу для работы с ЭЦП (CryptoSocket, обновлено 03.09.2021)",
			connectionCryptoSocket = this.language == "KZ" ? "Cryptosocket көмегімен бастапқы қосылымды орнатыңыз" : "Установить первичное соединение с CryptoSocket";

		Util.query(par).then(response => {
			response.json().then(data => {
				if (data.contacts) {		
					
					// var table = `<div style="overflow: auto;"><table class="contactInfoTable"><thead><tr><th>${consultant}</th><th>Телефон</th><th>E-mail</th></tr></thead><tbody>`;
					var table = `<div style="overflow: auto;"><table class="contactInfoTable"><thead><tr><th>Телефон</th><th>E-mail</th></tr></thead><tbody>`;
					for (var i = 0; i < data.contacts.length; i++) {
						var contact = data.contacts[i];
						// var row = "<tr><td align='center' width='500'>" + Util.encodeHTML(contact.person) + "</td><td align='center' width='500'>" + Util.encodeHTML(contact.telephone) + "</td><td align='center' width='350'><a href=\"mailto:" + Util.encodeHTML(contact.email) + "\">" + Util.encodeHTML(contact.email) + "</a></td></tr>";

						var row = "<tr><td align='center' width='250'>" + Util.encodeHTML(contact.telephone) + "</td><td align='center' width='150'><a href=\"mailto:" + Util.encodeHTML(contact.email) + "\">" + Util.encodeHTML(contact.email) + "</a></td></tr>";
						table = table + row;
					}
				  	table = table + "</tbody></table><div>";
				  	// var content = `<div class="help-name">${supportService}</div><br>` + table;
					var content = table;
				  	$('.tool-help').append($(content));
				}
				if (data.additionalInfo) {
					$('.tool-help').append("<br><div align='center' width='500' style='font-size: 10pt; font-weight: bold'>" + Util.encodeHTML(data.additionalInfo) + "</div>")
				}
		  		var content = `<br><a href="${_this.contextName}/jsp/media/files/SetupCSPv6.3_20220903.exe">${downloadCryptoSocket}</a>`;
				console.log(_this.contextName)
		  		$('.tool-help').append($(content));
		  		var content = `<br><a href="https://localhost:6127/tumarcsp">${connectionCryptoSocket}</a>`;
		  		$('.tool-help').append($(content));
		  		
		  		$('.label-show-support').tooltip({
					deltaX: -20,
				    content: $('.tool-help').html(),
				    showEvent: 'click',
				    onPosition: function() {
				    	//$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() - 15);
				    	//$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() - 20);
			    	},
				    onShow: function(){
				        var t = $(this);
				        t.tooltip('tip').unbind().bind('mouseenter', function(){
				            t.tooltip('show');
				        }).bind('mouseleave', function() {
				            t.tooltip('hide');
				        }).css({
				        	width: '500px'
				        });
				    }
				});
			});
		});
	}
	
	changePwdDialog(obligatory) {
		let _this = this,
			change = this.language == "KZ" ? "Өзгерту" : "Изменить",
			passSuccessFullChanged = this.language == "KZ" ? "Құпия сөз сәтті өзгертілді!" : "Пароль успешно изменен!",
			cancelButton = this.language == "KZ" ? "Бас тарту" : "Отмена",
			enterTheSystem = this.language == "KZ" ? "Жүйеге кіру" : "Войти в систему",
			changePassword = this.language == "KZ" ? "Құпия сөзді өзгерту" : "Смена пароля";

		var dialogId = 'or3_popup';
		$('body').append($("<div></div>").attr('id', dialogId));
	
		var buttonOk = {
			text: change,
			handler: function() {
				$("#" + dialogId + " .dialog-button .l-btn").linkbutton('disable');
				var par = {json: 1, guid: _this.guid, browser: _this.browser, configNumber: 0};
	    		par["name"] = $('#user').val();
	        	par["passwd"] = $('#' + dialogId).find('[uid="oldPass"]').val();
	        	par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
	        	par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
	    		
	    		Util.post2(par).then(data => {
	        		if (data.result == 'error') {
	        			alert(data.message);
						$("#" + dialogId + " .dialog-button .l-btn").linkbutton('enable');
	        		} else {
	        			$("#" + dialogId).dialog('destroy');
	        			alert(passSuccessFullChanged);
	        			window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + Util.rnd();
	        		}
	    		});
			}
		};
		
		var buttonCancel = {
			text: cancelButton,
			handler: function() {
				$("#" + dialogId).dialog('destroy');
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				if(_this.sLogin){
					_this.login(false);
				}
			}
		};
		
		var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
				
		$('#' + dialogId).dialog({
			title: changePassword,
			width: 300,
			height: 200,
			closed: false,
			cache: false,
			closable: obligatory != true,
			href: _this.contextName + '/qyzmet/pwd.jsp?guid=' + guid + '&rnd=' + Util.rnd(),
			modal: true,
			onClose: function() {
				$("#" + dialogId).dialog('destroy');
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			},
			onBeforeDestroy : function() {
			},
			buttons: buttons
		});
	}

	whatLanguageNow() {
		this.language = localStorage.getItem("EkyzmetLanguage");
	}

	setLangToolTip(language) {
		let consultant = language == "KZ" ? "Кеңесші" : "Консультант",
			supportService = language == "KZ" ? "Қолдау қызметі" : "Служба поддержки",
			downloadCryptoSocket = language == "KZ" ? "ЭЦҚ-мен жұмыс істеу үшін бағдарламаны жүктеу және орнату (CryptoSocket, 03.09.2021 жаңартылған)" : "Скачать и установить программу для работы с ЭЦП (CryptoSocket, обновлено 03.09.2021)",
			connectionCryptoSocket = language == "KZ" ? "Cryptosocket көмегімен бастапқы қосылымды орнатыңыз" : "Установить первичное соединение с CryptoSocket";

		$(".help-name").html(supportService);
		$(".contactInfoTable thead th").each(function(index) {
			if(index == 3) {
				$(this).html(consultant);
			}
		});
		$(".contactInfoTable tbody tr td").each(function(index) {
			if($(this).html() == "Служба поддержки") {
				$(this).html(supportService)
			}
			if($(this).html() == "Қолдау қызметі") {
				$(this).html(supportService)
			}
		});

		$(".tooltip-content a").each(function(index) {
			if(index == 3) {
				$(this).html(downloadCryptoSocket);
			}
			if(index == 4) {
				$(this).html(connectionCryptoSocket);
			}
		});
	}
	
	changeLanguage(lang) {
		this.lang = lang;
		Translation.init(lang);
	}
}