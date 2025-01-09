var BOSH_DOMAIN = 'jabber.erik.kz';
var SEARCH_USERS_METHOD = null;
var SEARCH_RESULTS_HEADERS = null;
var SEARCH_RESULTS_HEADERS_WIDTH = [];

window.chatConnectCount = 0;
window.popupCount = 0;

// Выполняется сразу после загрузки страницы
$(function() {
	loadSoundFile(window.contextName+'/chat/js/chat-files/sound.mp3');
	
	$.get(window.mainUrl+"&sfunc&cls=WsUtilNew&name=get_chat_params&arg0="+userId, function(data) {
		if (data.login) {
			var chatUser = data.login;
			var chatKey = data.pass;
			BOSH_DOMAIN = data.boshDomain;
			
			SEARCH_USERS_METHOD = data.searchMethod;
			SEARCH_RESULTS_HEADERS = data.searchResultHeaders;
			var tmp = data.searchResultWidths;
			
			if (tmp != null) {
				var percents = tmp.split(',');
				for (var k=0; k<percents.length; k++) {
					SEARCH_RESULTS_HEADERS_WIDTH[k] = Math.round(655 * parseInt(percents[k])/100);
				}
			}
			
			chatConnect(chatUser, chatKey);		
		} else {
			$('#chatPanel').hide();
		}
	},'json');

	//chatConnect();
});

var formatId = function(name) {
	return name.replace(/%/g, "\\%");
};

var formatJid = function(name) {
	var names = name.split('@');
	names[0] = names[0].replace(/\s/g, "%20").replace(/\./g, "%2e");
	console.log(names[0]);
	return names.join('@');
};

////////////////////////////////////////
// Переводы
/////////////////
var chatLangRU = Array();
chatLangRU['find']="Найти";
chatLangRU['request-sended']="Запрос отправлен";
chatLangRU['chat-user-not-add']="Пользователь не добавил вас в контакты. Вы не можете писать ему сообщение";
chatLangRU['searching']='Идет поиск';
chatLangRU['chat-invite']='хочет добавить вас в список контактов. Разрешить или отказать?';
chatLangRU['deny']='Отказать';
chatLangRU['allow']='Разрешить';

function i18n(key) {
    return chatLangRU[key];
}
////////////////////////////

var context = new window.AudioContext(); //
//переменные для буфера, источника и получателя
var buffer, source, destination; 

//функция для подгрузки файла в буфер
var loadSoundFile = function(url) {
	// делаем XMLHttpRequest (AJAX) на сервер
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	xhr.responseType = 'arraybuffer'; // важно
	xhr.onload = function(e) {
		// декодируем бинарный ответ
 		context.decodeAudioData(this.response,
 			function(decodedArrayBuffer) {
				// получаем декодированный буфер
	   			buffer = decodedArrayBuffer;
	 		}, function(e) {
   				console.log('Error decoding file', e);
 			});
	};
	xhr.send();
}

var chatSound = 1;
var playChatSound = function()	{
	if (chatSound==0)
		return;

	// создаем источник
	source = context.createBufferSource();
	// подключаем буфер к источнику
	source.buffer = buffer;
	// дефолтный получатель звука
	destination = context.destination;
	// подключаем источник к получателю
	source.connect(destination);
	// воспроизводим
	source.start(0);
}

// Соединение с XMPP-сервером (Openfire)
function chatConnect(login, pd) {
	var chatHost = window.location.hostname;
	var port = window.location.port;
	if (port!="80") {
		chatHost+=":"+port;
	}
	var chatProtocol = (window.location.protocol.indexOf('https') > -1) ? 'wss' : 'ws';

	// нужен nginx для проксирования websocket запросов, так как браузер не будет отправялть запросы на незнакомые урл
	var conn = new Strophe.Connection(chatProtocol + "://"+chatHost+"/xmpp-websocket/", {protocol: "ws"});
	
	// Пробелы заменяем на \20
	var chatLogin = formatJid(login.toLowerCase()) + "@" + BOSH_DOMAIN;
	var chatPd = pd;
	
	conn.connect(chatLogin, chatPd, function(status) {
		console.log("CHAT_STATUS " + status);
		if (status === Strophe.Status.CONNECTED) {
			window.chatConnectCount=0;
			//$(document).trigger('connected');
            chatConnected()
		} else if (status === Strophe.Status.AUTHFAIL) {
			$('#chatPanel').hide();
		} else if (status === Strophe.Status.DISCONNECTED) {
			$(document).trigger('disconnected');
		} else if (status == Strophe.Status.DISCONNECTING) {
			if (window.chatConnectCount<5) {
				$(document).trigger('connect');
				window.chatConnectCount++;
			}
		} else if (status == Strophe.Status.CONNFAIL) {
			console.log("Chat connection failed");
		}
	});
	iChat.connection = conn;
	iChat.client_jid = chatLogin;
	iChat.initChat();

//});
}

$(window).unload(function() {
	console.log("Log out from chat" );
	iChat.connection.options.sync = true; // Switch to using synchronous requests since this is typically called onUnload.
	iChat.connection.flush();
	iChat.connection.disconnect();
});

// После соединения
function chatConnected() {
	console.log('connected');
	iChat.storage = $.sessionStorage;
	
	var iq = $iq({
		type: 'get'
	}).c('query', {
		xmlns: 'jabber:iq:roster'
	});
    
	iChat.connection.sendIQ(iq, iChat.on_roster);
	iChat.connection.addHandler(iChat.on_roster_changed, "jabber:iq:roster", "iq", "set");
	iChat.connection.addHandler(iChat.on_message, null, "message", "chat");
	iChat.connection.addHandler(iChat.on_presence, null, "presence");

	console.log("connected done");
    iChat.updateStatus();
	//window.setTimeout(function() {iChat.updateStatus()}, 10000);
}

var iChat = {
	smilesArray: [
		['angel.gif', '(:angel:)', ],
		['bad.gif', '(:bad:)', ],
		['biggrin.gif', '(:biggrin:)', ],
		['blum.gif', '(:blum:)', ],
		['blush.gif', '(:blush:)', ],
		['cray.gif', '(:cray:)', ],
		['crazy.gif', '(:crazy:)', ],
		['dance.gif', '(:dance:)', ],
		['diablo.gif', '(:diablo:)', ],
		['dirol.gif', '(:dirol:)', ],
		['drinks.gif', '(:drinks:)', ],
		['fool.gif', '(:fool:)', ],
		['give_rose.gif', '(:give_rose:)', ],
		['good.gif', '(:good:)', ],
		['kiss_mini.gif', '(:kiss_mini:)', ],
		['yahoo.gif', '(:yahoo:)', ],
		['man_in_love.gif', '(:man_in_love:)', ],
		['mocking.gif', '(:mocking:)', ],
		['music.gif', '(:music:)', ],
		['nea.gif', '(:nea:)', ],
		['pardon.gif', '(:pardon:)', ],
		['rofl.gif', '(:rofl:)', ],
		['rolleyes.gif', '(:rolleyes:)', ],
		['sad.gif', '(:sad:)', ],
		['scratch_one-s_head.gif', '(:scratch_one-s_head:)', ],
		['shok.gif', '(:shok:)', ],
		['shout.gif', '(:shout:)', ],
		['smile.gif', '(:smile:)', ],
		['unknw.gif', '(:unknw:)', ],
		['wacko2.gif', '(:wacko2:)', ],
		['wink.gif', '(:wink:)', ]
	],
	statusText: {
		'dnd': 'Не беспокоить',
		'away': 'Отошел',
		'online': 'Онлайн'
	},
	connection: null,
	client_jid: null,
	storage: null,
	status:"online",
	activeChatWindows: [],
	chatDlgHistory: [],
	chatFileUploader: null,
	activeChatSmile: null,
	chatGroups: [],
	chatSmilesList: "",
	insertSmile: function(smileIco) {
		console.log(smileIco);
		$('#chat-' + iChat.activeChatSmile + " .composeInput").append(" <img unselectable='on' contenteditable='false' src='" + smileIco.src + "'/> ");
	},
	insertAtCaret: function(areaId, text) {
		var txtarea = document.getElementById(areaId);
		var scrollPos = txtarea.scrollTop;
		var strPos = 0;
		var br = ((txtarea.selectionStart || txtarea.selectionStart == '0') ? "ff" : (document.selection ? "ie" : false));
		if (br == "ie") {
			txtarea.focus();
			var range = document.selection.createRange();
			range.moveStart('character', -txtarea.value.length);
			strPos = range.text.length;
		} else if (br == "ff") strPos = txtarea.selectionStart;
		var front = (txtarea.value).substring(0, strPos);
		var back = (txtarea.value).substring(strPos, txtarea.value.length);
		txtarea.value = front + text + back;
		strPos = strPos + text.length;
		if (br == "ie") {
			txtarea.focus();
			var range = document.selection.createRange();
			range.moveStart('character', -txtarea.value.length);
			range.moveStart('character', strPos);
			range.moveEnd('character', 0);
			range.select();
		} else if (br == "ff") {
			txtarea.selectionStart = strPos;
			txtarea.selectionEnd = strPos;
			txtarea.focus();
		}
		txtarea.scrollTop = scrollPos;
	},
	initChat: function() {
		console.log("Initialize chat interface");
		/*for (var k = 0; k < iChat.smilesArray.length; k++) {
			var img = new Image();
			img.src = '/ipgoBlue-theme/js/chat/chat-files/smiles/' + iChat.smilesArray[k][0];
			iChat.chatSmilesList += "<img src='" + img.src + "' class='chatSmileIco' data-ico='" + iChat.smilesArray[k][1] + "' onclick='iChat.insertSmile(this)'>";
		}*/
		//chatSmilesList.ctxMenu = contactCtxMenu;
		if ($("#chatPanel").length==0) {
			var mainPanel = "";
			mainPanel += "<div id=\"chatPanel\" class=\"jb-chat \">";
			mainPanel += "  <div class=\"chat-header bar brd closed \">";
			mainPanel += "    <div class='chat-title'>чат <button id='chatMainWindow' style='display:none;float: right; height: 20px; width: 20px; background: transparent none repeat scroll 0% 0%; border: medium none;' type='button' class='ui-button ui-widget ui-state-focus ui-corner-all ui-button-icon-only ui-dialog-titlebar-close' role='button' title='Close'><span class=\"ui-button-icon-primary ui-icon ui-icon-closethick\"></span></button><\/div>";
			mainPanel += "  <\/div>";
			mainPanel += "  <div id=\"contactList\" class=\"brd\" style=\"display:none\">";
			mainPanel += "    ";
			mainPanel += "    ";
			mainPanel += "  <\/div>";
			
			mainPanel += "  <div id=\"footerPanel\" class=\"chat-footer bar brd jb-chat\" style=\"display:none\">";
			mainPanel += "    <div  class=\"noselect\">";
			mainPanel += "    <div id=\"chatStatuses\" class=\"brd noselect\" style=\"display:none\">";
			mainPanel += "    <ul>";
			mainPanel += "    <li class=\"online\" data-status=\"online\">Онлайн<\/li>";
			mainPanel += "    <li class=\"away\" data-status=\"away\">Отошел<\/li>";
			mainPanel += "    <li class=\"dnd\"  data-status=\"dnd\">Не беспокоить<\/li>";
			mainPanel += "    <\/ul>";
			mainPanel += "    <\/div>";
			mainPanel += "    <button id=\"statusSelect\" class=\"chtBtn chat-status-btn active-chat-status-online\" status='online'>Онлайн</button>";
			mainPanel += "    <a id=\"findContact\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-add'\">Найти</a>";
			mainPanel += "    <\/div>";
			mainPanel += "  <\/div>";
			mainPanel += "<\/div>";
			$('body').append(mainPanel);
			moveNotPanel(27);
			
			var contactCtxMenu = "";
			contactCtxMenu += " <div id=\"context-menu\"><span class='chatCtxUserName'></span>";
			contactCtxMenu += "	      	<ul class=\"dropdown-menu\" role=\"menu\">";
			contactCtxMenu += "            <li><a tabindex=\"-1\" data-action='rename'><i class='icon-edit'></i> Переименовать<\/a><\/li>";
			contactCtxMenu += "	           <li><a tabindex=\"-1\" data-action='delete'><i class='icon-remove'></i> Удалить<\/a><\/li>";
			contactCtxMenu += "	      	<\/ul>";
			contactCtxMenu += "	      <\/div>";
			$('body').append(contactCtxMenu);
			$("#chatPanel .closed").click(function() {
				$('#chatPanel #contactList,#chatPanel #footerPanel.chat-footer,#chatMainWindow ').toggle();
				$(".chat-header").removeClass("closed");
				if ($('#chatMainWindow').css('display') === 'none') {
					moveNotPanel(27);
				} else {
					moveNotPanel(371);
				}
			});
			$("#chatMainWindow").click(function(e) {
				$('#chatPanel #contactList,#chatPanel #footerPanel.chat-footer,#chatMainWindow ').toggle();
				$(".chat-header").addClass("closed");
				e.stopPropagation();
			});
			
			$("#statusSelect").click(function() {
				$("#chatStatuses").toggle();
			});
			
			$("#chatPanel #findContact").linkbutton({
				text: false
			}).click(function() {
				//$("#chatSearchQuery").val("");
				//$("#chatSearchResult").html("");
				$("#searchchatUsers").dialog('open');
			});
			
			// Диалоговое окна поиска пользователей
			var searchDlgHtml = '<div id="searchchatUsers" class="jb-chat" class="hidden" title="Поиск пользователей">';
			searchDlgHtml +='<form class="form-horizontal" onsubmit="return chatSearchUsers();return false;"><table style="width:100%">';
			searchDlgHtml +='<tr><td style="padding: 10px; text-align: center;"><input type="text" class="" id="chatSearchQuery" placeholder="Введите Ф.И.О или E-mail">';
			searchDlgHtml +='<button id="chatSearchBtn" disabled="disabled" type="submit" onclick="return chatSearchUsers();" class="chat-search btn btn-info btn-disabled" data-load ing-text="'+i18n("searching")+'..."><i class="icon-search icon-white"></i> '+i18n("find")+'</button>';
			searchDlgHtml +='<hr class="chat-hr" style="margin:10px 0px;"></td></tr>';
			
			if (SEARCH_USERS_METHOD != null) {
				searchDlgHtml +='<tr><td><div style="width:782px; height: 391px;">';
				searchDlgHtml +='<table id="searchResultsTbl" class="easyui-datagrid" style="width:500px;height:300px" data-options="url:\'' + window.mainUrl + '&cmd=searchChatUsers\',striped:true,fit:true,singleSelect:true,pageSize:20,collapsible:false" pagination="true" pageList="[10,20,50,100]">';

				searchDlgHtml +='<thead>' + 
	            					'<tr>';
				
				var headers = SEARCH_RESULTS_HEADERS.split(',');
				
				for (var k=0; k<headers.length; k++) {
					searchDlgHtml += '<th data-options="field:\'field' + k + '\',width:' + SEARCH_RESULTS_HEADERS_WIDTH[k] + '">' + headers[k] + '</th>';
				}
				searchDlgHtml += '<th data-options="field:\'action\',width:110,align:\'right\'"></th>';
	            					
	            searchDlgHtml += '</tr>' + 
	            				'</thead>';
	        
				searchDlgHtml +='</table></div></td></tr>';
			} else {
				searchDlgHtml +='<tr><td id="chatSearchResult"></td></tr>';
			}
			searchDlgHtml +='</table></form></div>';

			$('body').append(searchDlgHtml);
			var par = {};
			par["cmd"] = 'getCahtSrchTxtPlaceholder';
			$.ajax({
				type : 'POST',
				url : window.mainUrl,
				data : par,
				success : function(data) {
					$('#chatSearchQuery').attr("placeholder", data.content);
				},
				dataType : 'json',
				async : false
			});
			$('#searchResultsTbl').datagrid({
				onBeforeLoad: function() {
					return true;
				},
				onLoadSuccess: function(rData) {
					var rows = rData.rows;
	
					for (var k=0; k<rows.length; k++) {
						var rowJid = rows[k].jid + '@' + BOSH_DOMAIN;
						
						var prevContact = $('#contactList li[data-jid="' + rowJid + '"]');
						var me = iChat.client_jid;
					
						if (prevContact.length !== 0) {
							var btn = $('button[data-jid="' + rows[k].jid + '"]')
							$(btn).after("<span>"+i18n("request-sended")+"</span>");
							$(btn).hide();
						} else if (rowJid === me) {
							var btn = $('button[data-jid="' + rows[k].jid + '"]')
							$(btn).hide();
						}
					}
					return true;
				}
			});
			
			$("#searchchatUsers").dialog({
				autoOpen: false,
				height: 500,
				closed: true,
				width: 800,
				modal: true
			});
			
			// Диалоговое окно переименование контакта
			var searchDlgHtml = '<div id="renamechatUsers" class="jb-chat" class="hidden" title="Переименовать">';
				searchDlgHtml+='<table style="width:100%"><tr><td style="padding: 3px;">Название</td></tr>'
					+ '<tr><td style="padding: 3px;"><input type="text" class="" id="renameChatUserName" style="width:100%"></td></tr>';
				//searchDlgHtml+='<tr><td style="padding: 3px; text-align: ;">Группа</td></tr><tr><td style="padding: 3px; text-align: ;"><select class="easyui-combobox" name="chatGroup" style="width:200px;" id="chatGroup"></select></td></tr>';
				searchDlgHtml+='</table></div>';
			$('body').append(searchDlgHtml);

			// Слушаем нажатия конпок в поле поиска пользователей
			$('#chatSearchQuery').keyup(function(e) {
				// если нажали Enter оставляем фокус в поле ввода
				if (e.which && e.which == 13) {
					if (!msie) {
						e.preventDefault();
						e.stopPropagation();
					}
					return false;
				}
			}).on('input', function(e) {
				var txt = $(this).val();
				// если текст для поиска меньше 3 символов, блокируем кнопку найти
				if (txt == null || txt.length < 1)
					$('#chatSearchBtn').attr("disabled", "disabled").addClass('btn-disabled');
				else
					$('#chatSearchBtn').removeAttr("disabled").removeClass('btn-disabled');
			});

            try {
    			$("#renamechatUsers").dialog({
    				autoOpen: false,
    				dialogClass: "jb-chat",
    				height: 250,
    				closed: true,
    				width: 350,
    				modal: true,
    				   buttons: [{
    					   id: "renameChatUserOk",
    					   text: "Сохранить",
    					   iconCls: 'icon-save',
    					   handler: function() {
    						   var q = $("#renamechatUsers").dialog('options')['contactLi'];
    						   groupName = "";//$('#chatGroup').combobox('getValue');
    						   //if ( typeof groupName === "undefined" || groupName=="")
    						   //groupName = $('#chatGroup').combobox('getText');
    						   iChat.modifyContact(q, $("#renameChatUserName").val(), groupName);
					
    						   var jid_id = iChat.jid_to_id(q);
    						   if ($('li[data-contact="' + jid_id + '"]').length > 0) {
    							   $('li[data-contact="' + jid_id + '"]').attr('data-name', $("#renameChatUserName").val());
    							   $('li[data-contact="' + jid_id + '"] div').text($("#renameChatUserName").val());
    						   }
					
    						   $("#renameChatUserName").val("");
    						   $('#renamechatUsers').dialog('close');
    					   }
    				   }]
    			});
            } catch(err) {
                
            }

			$("#renameChatUserName").keyup(function(e) {
				if (e.which && e.which == 13) {
					if (!msie) {
						e.preventDefault();
						e.stopPropagation();
					}
					
					$('#renameChatUserOk').click();
					return false;
				}
			});

			var chatHistoryMsg = '<div id="chatHistoryMsg" class="jb-chat" class="hidden" title="История сообщений"></div>';
			$('body').append(chatHistoryMsg);
			$("#chatHistoryMsg").dialog({
				autoOpen: false,
				dialogClass: "jb-chat",
				height: 120,
				closed: true,
				width: 400,
				modal: true,
				buttons: [{
					text: "Сохранить",
					iconCls: 'icon-save',
					handler: function() {
						$('#chatHistoryMsg').dialog('close');
					}
				}]
			});

            // при двойном нажатии на контакт
			$(document).on('dblclick', "#contactList li ", function() {
				if ($(this).hasClass("chat-groupLi")) {
					$(this).nextAll().toggle();
				} else {
					var jid = $(this).data("jid");
					var name = $(this).data("name");
					
					var subscr = $(this).data("subsribe");
		            
					// если контакт не принял приглашение
		            if (subscr=='none') {
		                alert(i18n("chat-user-not-add"));
		                return;
		            }
					
		            // убираем иконку сообщения с контакта и заголовка окна
					var jid_id = iChat.jid_to_id(jid);
					$('#contactList li[data-contact="' + jid_id + '"]').removeClass('chat-msg');
					$('.chat-title').removeClass('chat-msg');
					
					// окно разговора
					var chatW = new ChatWindow(jid_id, jid);
					chatW.addWindow();
					var me = iChat.jid_to_id(iChat.client_jid);
					
					var formatedJidId = formatId(jid_id);
					
					/////////////////////////////////
					
					var iq = $iq({
                    	type: 'get',
                    	id: iChat.connection.getUniqueId('retrieve')
                    }).c('retrieve', {
                    	'xmlns': "urn:xmpp:archive",
                    	'with': jid,
                    	'start': '2019-12-17T11:25:25.400Z'
                    }).c('set', {
                    	xmlns: "http://jabber.org/protocol/rsm"
                    }).c('max').t('100');
                    
					console.log(iq.toString());
            		iChat.connection.sendIQ(iq, function(data) {console.log(data.outerHTML)}, function(data) {console.log(data.outerHTML)});
					///////////////////////////////////
					
					
					$('#chat-' + formatedJidId + ' .chat-messages').html("");
					if (iChat.storage.isSet(me+"_"+jid_id + "history")) {
						var historyList = iChat.storage.get(me+"_"+jid_id + "history");
						//var jid_id = iChat.jid_to_id(jid);
						$(historyList).each(function(e, el) {
							var msg_ = iChat.msg2html(el.jid, el.msg, el.self, new Date(el.dt));
							$('#chat-' + formatedJidId + ' .chat-messages').append(msg_);
						});
						id_ = '#chat-' + formatedJidId;
						var div = $(id_ + ' .chat-messages').get(0);
						div.scrollTop = div.scrollHeight;
					}
				}
			});
			$(document).on('click', function(e) {
				if (!$(e.target).is('.jb-chat, .jb-chat *')) {
					//$(".chatWindow").dialog('close');
				}
			});

			// менюшка на контакте
			var myvar = '<div id="chatContactCtxMenu" class="easyui-menu" style="width:180px;">'
	             + '<div data-options="name:\'rename\',iconCls:\'icon-edit\'">Переименовать</div>'
	             + '<div data-options="name:\'delete\',iconCls:\'icon-save\'">Удалить</div>' + '</div>';
			var contactMenu = $(myvar).menu({
				shadow: false,
				onClick: function(item) {
					var contact = $("#chatContactCtxMenu").menu('options')['contactLi'];
					// удалить (отписаться от контакта)
					if (item.name == 'delete') {
						iChat.deleteContact(contact);
					// переименовать контакт
					} else if (item.name == "rename") {
						var contactTitle = $('#contactList li[data-jid="' + contact + '"]').attr('data-name');
						$("#renameChatUserName").val(contactTitle);
						$("#renamechatUsers").dialog('options')['contactLi'] = contact;
						$("#renamechatUsers").dialog('open');
					// история
					} else if (item.name == "history") {
                        var iq = $iq({
                        	type: 'get',
                        	id: iChat.connection.getUniqueId('retrieve')
                        }).c('retrieve', {
                        	xmlns: Strophe.NS.ARCHIVE,
                        	'with': this.jid
                        });
                        
                		iChat.connection.sendIQ(iq, function(data) {console.log(data)});
                       $("#chatHistoryMsg").dialog('open');
					}
				}
			});
				
			$(document).on('contextmenu', "#contactList li ", function(e) {
				console.log('show menu: ' + $(this).attr("data-jid"));
				
				contactMenu.menu('options')['contactLi'] = $(this).attr("data-jid");

				contactMenu.menu('show', {
					left: e.pageX,
					top: e.pageY
				});
				
				return false;
			});
			
			$("#chatStatuses ul li").click(function() {
				console.log($(this).data("status"));
				status = $(this).data("status");
				var presence = $pres();
				if (status == 'online') {
				} else
					presence = $pres().c("show").t($(this).data("status"));
				
				iChat.connection.send(presence);
				iChat.status = status;
				
				$("#statusSelect").removeClass("active-chat-status-online active-chat-status-dnd active-chat-status-away").addClass("active-chat-status-" + $(this).data("status"));
				$("#statusSelect").text(iChat.statusText[$(this).data("status")]);
				$("#chatStatuses").hide();
			}); /* file uploader*/
			$('body').append("<div style='display:none;' id='chatFilesContainer'></div>");
		}
	},
	// каждые 10 секунд отправляем на jabber сервер информацию о статусе
	updateStatus: function() {
		var presence = $pres();
		if (iChat.status == 'online') {} else
			presence = $pres().c("show").t(iChat.status);
		
		iChat.connection.send(presence);
		console.log("Update status "+ iChat.status);
		window.setTimeout(function() {iChat.updateStatus();}, 10000);
	},
	on_message: function(message) {
		var jid = Strophe.getBareJidFromJid($(message).attr('from'));
		var jid_id = iChat.jid_to_id(jid);
		var body = $(message).find("html > body");
		if (body.length === 0) {
			body = $(message).find('body');
			if (body.length > 0) {
				body = body.text()
			} else {
				body = null;
			}
		} else {
			body = body.contents();
			var span = $("<span></span>");
			body.each(function() {
				if (document.importNode) {
					$(document.importNode(this, true)).appendTo(span);
				} else {
					// IE workaround
					span.append(this.xml);
				}
			});
			body = span;
		}
		if (body) {
			//body = iChat.formatBody(body);
			// add the new message
			var dt = new Date();
			var msg_ = iChat.msg2html(jid, body, false, dt);
			iChat.saveMessage(jid_id, jid, body, dt, false);
			
			var formatedJidId = formatId(jid_id);
			if ($('#chat-' + formatedJidId + ' .chat-messages').length > 0) {
				$('#chat-' + formatedJidId + ' .chat-messages').append(msg_);
				iChat.scroll_chat(formatedJidId);
			} else {
				$('#contactList li[data-contact="' + iChat.jid_to_id(jid) + '"]').addClass('chat-msg');
				$('.chat-title').addClass('chat-msg');
				
			}
			playChatSound();
		}
		return true;
	},
	formatBody: function(content) {
		if (content != null) {
			content = content.replace(/&/g,'&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot').replace(/'/g, '&#39;')
				.replace(/\//g,'&#47;').replace(/\\/g,'&#92;').replace('/\r/g', '').replace(/\n/g, '');
		}
		return content;
	},
	formatSmiles: function(content) {
		for (var j = 0; j < iChat.smilesArray.length; j++) {
			var template = iChat.smilesArray[j][1].replace('(', '\\(').replace(')', '\\)');
			var re = new RegExp(template, 'gi');
			src = window.contextName + '/chat/js/chat-files/smiles/' + iChat.smilesArray[j][0];
			content = content.replace(re, "<img src='" + src + "' class='chatSmileIco' data-ico='" + iChat.smilesArray[j][1] + "'>");
		}
		return content;
	},
	formatHTML: function(html) {
		var smileTemplate = '\\<img[^\\>]*{0}[^\\>]*\\>';
		for (var i = 0; i < iChat.smilesArray.length; i++) {
			var template = smileTemplate.replace('{0}', iChat.smilesArray[i][0].replace('.', '\\.'));
			var re = new RegExp(template, 'gi');
			html = html.replace(re, iChat.smilesArray[i][1]);
		}
		// Remove all entries that start with href="http://location.host
		// where location.host is a JavaScript variable denoting the current
		// host. This allows for relative paths using the WYSIWYG editor.
		html = iChat.formatHTMLLocation(html, "href");
		html = iChat.formatHTMLLocation(html, "src");
		return html;
	},
	formatHTMLLocation: function(html, tag) {
		html = iChat.formatHTMLLocations(html, tag + "=\"", "http");
		html = iChat.formatHTMLLocations(html, tag + "='", "http");
		html = iChat.formatHTMLLocations(html, tag + "=\"", "https");
		html = iChat.formatHTMLLocations(html, tag + "='", "https");
		return html;
	},
	formatHTMLLocations: function(html, tag, protocol) {
		var localhostUrl = tag + protocol + "://" + location.host;
		while (html.indexOf(localhostUrl) >= 0) {
			html = html.replace(localhostUrl, tag);
		}
/*localhostUrl = tag + "<%= request.getContextPath() %>/html/common/@";
		while (html.indexOf(localhostUrl) >= 0) {
			html = html.replace(localhostUrl, tag + "@");
		}*/
		return html;
	},
	msg2html: function(jid, msg, self, date) {
		msg = iChat.formatSmiles(msg);

		var html = "";
		if (self) {
			html = "<div class='chat-message chat-message_self'>";
			html += "<div class='chat-message-date'><span>" + iChat.date2str(date) + "</span></div>" + "<div class='chat-message-from'><span>Я</span><div class='chat-fadeout'></div></div>";
		} else {
			html = "<div class='chat-message'>";
			html += "<div class='chat-message-date'><span>" + iChat.date2str(date) + "</span></div>" + "<div class='chat-message-from'><span>" + iChat.getNameFromJid(jid) + "</span><div class='chat-fadeout'></div></div>";
		}
		html += msg + "<br></div>";
		return html;
	},
	date2str: function(date) {
		var curDate = new Date();
		var curr_date = date.getDate();
		if (curr_date < 10)
			curr_date = "0" + curr_date;
		
		var curr_month = date.getMonth();
		curr_month++;
		if (curr_month < 10)
			curr_month = "0" + curr_month;
		var curr_year = date.getFullYear();
		var curr_hour = date.getHours();
		if (curr_hour < 10)
			curr_hour = "0" + curr_hour;
		var curr_min = date.getMinutes();
		if (curr_min < 10)
			curr_min = "0" + curr_min;
		if (curDate.getDate() != date.getDate() || date.getMonth() != curDate.getMonth() || date.getFullYear() != curDate.getFullYear()) {
			return curr_date + "." + curr_month + "." + curr_year + " " + curr_hour + ":" + curr_min;
		}
		return curr_hour + ":" + curr_min;
	},
	saveMessage: function(jid_id, jid, msg, dt, self) {
		var me = iChat.jid_to_id(iChat.client_jid);
		var historyList = iChat.storage.get(me+"_"+jid_id + "history");
		if (historyList) {
			historyList.push({
				'jid': jid,
				'msg': msg,
				'dt': dt,
				'self': self
			});
		} else {
			historyList = [];
			historyList.push({
				'jid': jid,
				'msg': msg,
				'dt': dt,
				'self': self
			});
		}
		iChat.storage.set(me+"_"+jid_id + "history", historyList);
	},
	saveContact: function(jid, name) {
		var me = iChat.jid_to_id(iChat.client_jid);
		var historyList = iChat.storage.get(me+"_contactList");
		if (historyList) {
			historyList[jid] = name;
		} else {
			historyList = {};
			historyList[jid] = name;
		}
		iChat.storage.set(me+"_contactList", historyList);
	},
	getNameFromJid: function(jid) {
		var me = iChat.jid_to_id(iChat.client_jid);
		var historyList = iChat.storage.get(me+"_contactList");
		if (historyList && historyList[jid]) {
			return historyList[jid];
		}
		return jid;
	},
	scroll_chat: function(jid_id) {
		var div = $('#chat-' + jid_id + ' .chat-messages').get(0);
		div.scrollTop = div.scrollHeight;
	},
	on_roster: function(iq) {
		console.log("Setting contact list ");
		$(iq).find('item').each(function() {
			console.log("on_roster: " + $(this)[0].outerHTML);

			var jid = $(this).attr('jid');
			var name = $(this).attr('name') || jid;
			var subscr = $(this).attr('subscription');
			var group = $(this).find('group');
			var groupName = false;
			if (group.length > 0) {
				groupName = group.text();
				iChat.chatGroups.push({
					id: groupName,
					text: groupName
				});
			}
			// transform jid into an id
			var jid_id = iChat.jid_to_id(jid);
			if (groupName) {}
			if ($('li[data-contact="' + jid_id + '"]').length==0){
				var contact = $("<li class='chat-contactLi' data-contact='" + jid_id + "' data-name='" + name + "' data-jid='" + jid + "' data-subsribe='" + subscr + "'>" + "<div class='context'>" + name + "</div></li>");
				iChat.saveContact(jid, name);
				iChat.insert_contact(contact, groupName);
			}
			// set up presence handler and send initial presence
//			iChat.connection.addHandler(iChat.on_presence, null, "presence");
			iChat.connection.send($pres());
		});
	},
	on_roster_changed: function(iq) {
		console.log("Update contact list ");
		$(iq).find('item').each(function() {
			console.log("on_roster_changed: " + $(this)[0].outerHTML);
			var sub = $(this).attr('subscription');
			var ask = $(this).attr('ask');
			var jid = $(this).attr('jid');
			var name = $(this).attr('name');
			var group = $(this).find('group');
			var groupName = false;
			if (group.length > 0) {
				groupName = group.text();
				iChat.chatGroups.push({
					id: groupName,
					text: groupName
				});
			}
			var jid_id = iChat.jid_to_id(jid);
			if (sub === 'remove') {
				$('#contactList li[data-contact="' + jid_id + '"]').remove();
			} else if (ask !== 'unsubscribe') {
				var username = jid.substring(0, jid.indexOf('@'));
				var li = $('li[data-contact="' + jid_id + '"]');
				if (li.length == 0) {
					console.log("Getting user ");
					iChat.getUser(username, function(result) {
						$(result).find('item').each(function() {
							console.log("find: " + $(this).html());
							var user = $(this);
							user.find('field').each(function() {
								var colName = $(this).attr('var');
								if (colName === "Name" || colName === "Имя") name = $(this).find("value").text();
								if (colName === "Username" || colName === "Имя пользователя") username = $(this).find("value").text();
							});
							
							var contact_html = $("<li class='chat-contactLi' data-contact='" + jid_id + "' data-name='" + name + "' data-jid='" + jid + "'  data-subsribe='" + sub + "' class='" + ($('#contactList li[data-contact="' + jid_id + '"]').attr('class') || "offline") + "'>" + "<div>" + name + "</div></li>");
							if ($('#contactList li[data-contact="' + jid_id + '"]').length > 0) {
								$('#contactList li[data-contact="' + jid_id + '"]').remove();
							}
							iChat.saveContact(jid, name);
							iChat.insert_contact(contact_html, groupName);
						});
					});
				} else {
					if (li.data('subsribe') !== 'none' && sub === 'none')
						li.remove();
					else
						li.data('subsribe', sub);
				}
			}
		});
		return true;
	},
	jid_to_id: function(jid) {
		return Strophe.getBareJidFromJid(jid).replace("@", "_").replace(/\./g, '-');
	},
	modifyContact: function(jid, name, groups) {
		this.addContact(jid, name, groups);
		this.saveContact(jid, name);
	},
	deleteContact: function(jid) {
		var iq = $iq({
			'type': "set"
		}).c("query", {
			'xmlns': Strophe.NS.ROSTER
		}).c("item", {
			'jid': jid,
			'subscription': "remove"
		});
		iChat.connection.sendIQ(iq);
		
		iChat.connection.send($pres({
			to: jid,
			"type": "unsubscribe"
		}));
	},
	addContact: function(jid, name, groups) {
		var iq = $iq({
			type: "set"
		}).c("query", {
			xmlns: Strophe.NS.ROSTER
		}).c("item", {
			name: name || "",
			jid: jid
		});
		if (groups && groups.length > 0) {
			iq.c("group").t(groups).up();
		}
		iChat.connection.sendIQ(iq);
	},
	insert_contact: function(elem, groupName) {
		if (!groupName) groupName = "Остальные";
		//groupName ="Остальные";
		var group = $('#contactList ul[group="' + groupName + '"]');
		if (group.length == 0) {
			var groupUl = $("<ul></ul>").attr("group", groupName).attr('class', 'Chat-groupUl').append("");//"<li class='chat-groupLi'><span>" + groupName + "</span></li>")
			$('#contactList').append(groupUl);
			group = $('#contactList ul[group="' + groupName + '"]');
		}
		var jid = elem.attr('data-contact');
		var pres = iChat.presence_value(elem);
		var contacts = $('#contactList li');
		if (contacts.length > 0) {
			var inserted = false;
			contacts.each(function() {
				var cmp_pres = iChat.presence_value($(this));
				var cmp_jid = $(this).attr('data-contact');
				if (pres > cmp_pres) {
					$(this).before(elem);
					inserted = true;
					return false;
				} else {
					if (jid < cmp_jid) {
						$(this).before(elem);
						inserted = true;
						return false;
					}
				}
			});
			if (!inserted) {
				$(group).append(elem);
			}
		} else {
			$(group).append(elem);
		}
		/*$('ul.Chat-GroupUl').each(function(i, ul) {
			if ($("li", $(ul)).length == 1) $(ul).remove();
		});*/
	},
	insert_contact_group: function(elem, group) {
		if (!group) groupName = "Остальные";
		group = $('#contactList ul[group="' + groupName + '"]');
		var jid = elem.attr('data-contact');
		var pres = iChat.presence_value(elem);
		var contacts = $('#contactList li');
		if (contacts.length > 0) {
			var inserted = false;
			contacts.each(function() {
				var cmp_pres = iChat.presence_value($(this));
				var cmp_jid = $(this).attr('data-contact');
				if (pres > cmp_pres) {
					$(this).before(elem);
					inserted = true;
					return false;
				} else {
					if (jid < cmp_jid) {
						$(this).before(elem);
						inserted = true;
						return false;
					}
				}
			});
			if (!inserted) {
				$('#contactList ul').append(elem);
			}
		} else {
			$('#contactList ul').append(elem);
		}
	},
	on_presence: function(presence) {
		var ptype = $(presence).attr('type');
		var from = $(presence).attr('from');

		if (ptype === 'subscribe') {
			if ($('#contactList li[data-contact="' + iChat.jid_to_id(from) + '"]').length == 0) {
				var q = from.replace("@" + BOSH_DOMAIN, "");
				iChat.getUser(q, function(result) {
					console.log(result);
					$(result).find('item').each(function() {
						var jid = $(this);
						var name = "";
						var username = "";
						jid.find('field').each(function() {
							var colName = $(this).attr('var');
							if (colName === "Name" || colName === "Имя") name = $(this).find("value").text();
							if (colName === "Username" || colName === "Имя пользователя") username = $(this).find("value").text();
							if (colName === "jid") jid_id = $(this).find("value").text();
						});
						showChatApproveDialog(from, name);
					});
				});
			} else {
				iChat.connection.send($pres({
					to: from,
					"type": "subscribed"
				}));
			}
			
		} else if (ptype !== 'error') {
			var contact = $('#contactList li[data-contact="' + iChat.jid_to_id(from) + '"]').removeClass("online").removeClass("away").removeClass("dnd").removeClass("offline");
			if (ptype === 'unavailable') {
				//contact.addClass("offline");
			} else {
				var show = $(presence).find("show").text();
				if (show === "" || show === "chat") {
					contact.addClass("online");
				} else {
					contact.addClass(show);
				}
			}
		}
		return true;
	},
	presence_value: function(elem) {
		if (elem.hasClass('online')) {
			return 2;
		} else if (elem.hasClass('away')) {
			return 1;
		}
		return 0;
	},
	show_traffic: function(body, type) {
		if (body.childNodes.length > 0) {
			var console = $('#console').get(0);
			var at_bottom = console.scrollTop >= console.scrollHeight - console.clientHeight;;
			$.each(body.childNodes, function() {
				$('#console').append("<div class='" + type + "'>" + iChat.xml2html(Strophe.serialize(this)) + "</div>");
			});
			if (at_bottom) {
				console.scrollTop = console.scrollHeight;
			}
		}
	},
	xml2html: function(s) {
		return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
	},
	getUser: function(q, callback) {
		var iq = $iq({
			type: 'set',
			id: 'search1',
			to: 'search.' + BOSH_DOMAIN
		}).c('query', {
			xmlns: 'jabber:iq:search'
		}).c('x', {
			xmlns: 'jabber:x:data',
			type: 'submit'
		}).c('field', {
			'var': 'FORM_TYPE'
		}).c('value').t('jabber:iq:search').up().up().c('field', {
			'var': 'search'
		}).c('value').t(q).up().up().c('field', {
			'var': 'Username'
		}).c('value').t("1").up().up().c('field', {
			'var': 'Name'
		}).c('value').t("1").up().up().c('field', {
			'var': 'Email'
		}).c('value').t("1")
		//.c('email').t(q); 
		iChat.connection.sendIQ(iq, callback);
	},
	searchUsers: function(q, div) {
		var iq = $iq({
			type: 'set',
			id: 'search1',
			to: 'search.' + BOSH_DOMAIN
		}).c('query', {
			xmlns: 'jabber:iq:search'
		}).c('x', {
			xmlns: 'jabber:x:data',
			type: 'submit'
		}).c('field', {
			'var': 'FORM_TYPE'
		}).c('value').t('jabber:iq:search').up().up().c('field', {
			'var': 'search'
		}).c('value').t(q).up().up().c('field', {
			'var': 'Username'
		}).c('value').t("1").up().up().c('field', {
			'var': 'Name'
		}).c('value').t("1").up().up().c('field', {
			'var': 'Email'
		}).c('value').t("1")
		//.c('email').t(q); 
		iChat.connection.sendIQ(iq, iChat.viewSearchResult);
	},
	viewSearchResult: function(result) {
		console.log(result);
		var html = "<table class='table table-bordered table-striped chat-table-results'><tr>" +
				//"<th style='width:85px;'>ИИН</th>" +
				"<th style='width:270px;'>Ф.И.О</th>" +
				"<th style='width:160px;'>E-mail</th>" +
				"<th></th></tr>";
		var finded = false;
		$(result).find('item').each(function() {
			var jid = $(this);
			var name = "";
			var email = "";
			var username = "";
			var jid_id = "";
			jid.find('field').each(function() {
				finded = true;
				var colName = $(this).attr('var');
				if (colName === "Name" || colName === "Имя") name = $(this).find("value").text();
				if (colName === "Email" || colName === "Электронная почта") email = $(this).find("value").text();
				if (colName === "Username" || colName === "Имя пользователя") username = $(this).find("value").text();
				if (colName === "jid") jid_id = $(this).find("value").text();
			});
			
			var prevContact = $('#contactList li[data-jid="' + jid_id + '"]');
			var me = iChat.client_jid;
			
			if (prevContact.length === 0 && jid_id !== me) {
				html += "<tr>";
				bt = '<button type="button" class="btn btn-success" onclick="chatAddContact(this)" data-jid="' + jid_id + '" data-name="' + name + '" data-username="' + username + '" data-email="' + email + '"><i class="icon-plus icon-white"></i> Пригласить</button>';
				html += //"<td>" + username + "</td>" +
						"<td>" + name + "</td>" +
						"<td>" + email + "</td>" +
						"<td>" + bt + "<span style='display:none' id='req_"+username +"'>"+i18n("request-sended")+"</span></td></tr>";
			}
		});
		html += "</table>";
		iChat.resultDiv.html(html);
		if(finded) {
		}
	},

	chatSendFile: function(toJid) {
		console.log(toJid);
		var up = $('#ipgo_logo').pluploadQueue();
		if (up.features.triggerDialog) {
			plupload.addEvent(document.getElementById('idOtherButton'), 'click', function(e) {
				var input = document.getElementById(up.id + '_html5');
				if (input && !input.disabled) { // for some reason FF (up to 8.0.1 so far) lets to click disabled input[type=file]
					input.click();
				}
				e.preventDefault();
			});
		}
	}
}

function ChatChangeStatus(status) {
	console.log(status);
	//status = $(this).data("status");
	var presence = $pres();
	if (status == 'online') {} else
	presence = $pres().c("show").t(status);
    iChat.status=status;
	iChat.connection.send(presence);
	$("#statusSelect").removeClass("active-chat-status-online active-chat-status-dnd active-chat-status-away").addClass("active-chat-status-" + status);
	$("#statusSelect").text(iChat.statusText[status]);
	$("#chatStatuses").hide();
}

function sendChatMessage(ev) {
	var jid = $(this).data('jid');
	if (ev.which === 13) {
		ev.preventDefault();
		var body = $(this).html();
		bodyFormated = iChat.formatHTML(body);
		console.log(body);
		iChat.connection.send($msg({
			to: jid,
			"type": "chat"
		}).c('body').t(bodyFormated));
		var jid_id = iChat.jid_to_id(jid);
		var dt = new Date();
		var msg_ = iChat.msg2html(iChat.client_jid, bodyFormated, true, dt);
		iChat.saveMessage(jid_id, jid, bodyFormated, dt, true);
		
		var formatedJidId = formatId(jid_id);
		$('#chat-' + formatedJidId + ' .chat-messages').append(msg_);
		iChat.scroll_chat(formatedJidId);
		$(this).text('');
	} else {}
}

function chatAddContact(btn) {
	
	var jid = $(btn).attr("data-jid");
	if (jid.indexOf('@') === -1)
		jid += '@' + BOSH_DOMAIN;
	
	data = {
		jid: formatJid(jid),
		name: $(btn).attr("data-name")
	}
	$(btn).after("<span>"+i18n("request-sended")+"</span>");
	$(btn).hide();
//	$("#req_" + formatId($(btn).attr("data-username"))).show();
	var iq = $iq({
		type: "set"
	}).c("query", {
		xmlns: "jabber:iq:roster"
	}).c("item", data);
	iChat.connection.sendIQ(iq);
	var subscribe = $pres({
		to: jid,
		"type": "subscribe"
	});
	iChat.connection.send(subscribe);
}

function chatSearchUsers() {
	var txt = $("#chatSearchQuery").val();
	if (SEARCH_USERS_METHOD != null) {
		$('#searchResultsTbl').datagrid('load', {search: txt, method: SEARCH_USERS_METHOD});
	} else {
		iChat.resultDiv = $("#chatSearchResult");
		iChat.searchUsers(txt, $("#chatSearchResult"));
	}
	
	return false;
}

function showChatApproveDialog(appJid, appName) {
	var dlgId = window.popupCount++;
	
	var approveDlgHtml = "<div id='approve_" + dlgId + "' style='padding:10px;' ><p><b>" + appName + "</b> "+i18n("chat-invite")+"</p></div>";
	$('body').append(approveDlgHtml);
	
	var deny = i18n("deny");
	var allow = i18n("allow");

	try {
		$('#approve_' + dlgId).dialog({
			autoOpen: false,
			dialogClass: "jb-chat",
			draggable: false,
			modal: true,
			closed: true,
			title: 'Запрос на добавление',
			buttons: [{
				text: i18n("deny"),
				iconCls: 'icon-remove',
				handler: function() {
					iChat.deleteContact(appJid);
					$('#approve_' + dlgId).dialog('close');
				}
			},
			{
				text: i18n("allow"),
				iconCls: 'icon-ok',
				handler: function() {
					iChat.connection.send($pres({
						to: appJid,
						"type": "subscribed"
					}));
					iChat.connection.send($pres({
						to: appJid,
						"type": "subscribe"
					}));
					var q = appJid.replace("@" + BOSH_DOMAIN, "");
					iChat.getUser(q, function(result) {
						console.log(result);
						$(result).find('item').each(function() {
							var jid = $(this);
							jid.find('field').each(function() {
								var colName = $(this).attr('var');
								if (colName === "Name" || colName === "Имя") name = $(this).find("value").text();
								if (colName === "Username" || colName === "Имя пользователя") username = $(this).find("value").text();
								if (colName === "jid") jid_id = $(this).find("value").text();
							});
							iChat.modifyContact(jid_id, name);
						});
					});
					$('#approve_' + dlgId).dialog('close');
				}
			}]
		});
		$('#approve_' + dlgId).dialog("open");
	
    } catch(err) {
        
    }
}

var ChatWindow = function(name, jid) {
	this.jid_id = name;
	this.jid = jid;
	iChat.chatDlgHistory[name] = {
		'jid_id': name,
		'jid': jid
	};
}
ChatWindow.prototype.panel = function() { /*return '<div id="chat-' + this.jid_id + '" class="chatWindow bar"> <div class="bar header"> </div><div class="messages"></div> <div class="footer"> <input type="text" name="msg" class="composeInput" data-jid="' + this.jid + '"/> </div></div>';*/
	chatDlg = '<div id="chat-' + this.jid_id + '" class="chatWindow"> ' + '<div class="chat-messages"></div> ';
	//chatDlg += '<div class="ui-widget-header"><button type="button" class="btn btn-mini" id="chatFileUploaderBtn_'+this.jid_id+'"><span class=" icon-file"></span></button></div>';
	chatDlg += '<div class="chat-footer">' + '<div class="composeInputDiv"><div name="msg" class="composeInput" contenteditable="true" data-jid="' + this.jid + '" ></div></div></div></div>';
	return chatDlg;
};
ChatWindow.prototype.addWindow = function() {
	
	var formatedJidId = formatId(this.jid_id);
	
	if ($('#chat-' + formatedJidId).length == 0) {
		$("body").append(this.panel());
		//activDlgs = $.inArray('#chat-' + this.jid_id, iChat.chatWindows);
		/*window['chatFileUpload_' + this.jid_id] = new plupload.Uploader({
			runtimes: 'html5,flash,html4',
			browse_button: 'chatFileUploaderBtn_'+this.jid_id,
			container: 'chatFilesContainer',
			max_file_size: '10mb',
			url: chatFileUploadUrl,
            jid_id : this.jid_id,
            jid : this.jid,
			flash_swf_url: '/ipgoBlue-theme/plupload/js/Moxie.swf',
		});
		window['chatFileUpload_' + this.jid_id].init();
		window['chatFileUpload_' + this.jid_id].bind('FilesAdded', function(up, files) {
			up.start();
		});
		window['chatFileUpload_' + this.jid_id].bind('Error', function(up, error) {
			alert(error.message);
		});
		window['chatFileUpload_' + this.jid_id].bind('UploadProgress', function(up, file) {
			//$("#"+up.settings.rdiv).html(file.percent + "%");
			//$(up.settings.browse_button).bootstrapBtn('loading');
		});
		window['chatFileUpload_' + this.jid_id].bind('FileUploaded', function(up, file, data) {
		//	$('#pgfoto').html('');
			json = jQuery.parseJSON(data.response);
            
            var url = chatFileDownloadUrl;
            url = url.replace("XXX",json.name);
            url = url.replace("YYY",json.file);
            var body = " <a class='chatMsgFile' target='_blank' href=\""+url+"\">"+json.name+"</a> <br>";
		      bodyFormated = iChat.formatHTML(body);;
		      console.log(body);
              console.log(up.settings.jid);
		      iChat.connection.send($msg({
			     to: up.settings.jid,
			     "type": "chat"
			}).c('body').t(bodyFormated));
			var jid_id = iChat.jid_to_id(up.settings.jid);
			var dt = new Date();
			var msg_ = iChat.msg2html(iChat.client_jid, body, true, dt);
			iChat.saveMessage(jid_id, up.settings.jid, body, dt, true);
			$('#chat-' + jid_id + ' .chat-messages').append(msg_);
			iChat.scroll_chat(jid_id);
				//$(up.settings.browse_button).bootstrapBtn('reset');
		      
		});*/
		if (iChat.activeChatWindows.length == 3) {
			var last_dlg = iChat.activeChatWindows[0];
			$(last_dlg).data('close', '1').dialog('close');
		}
		iChat.activeChatWindows.push('#chat-' + formatedJidId);
		$('#chat-' + formatedJidId).dialog({
			autoOpen: true,
			title: iChat.getNameFromJid(this.jid),
			jid: this.jid,
			jid_id: formatedJidId,
			messageCount: 0,
			dialogClass: 'chatDlgAbs',
			newMessageCount: 0,
			resizable: false,
			modal: false,
			width: 300,
			left: 'auto',
			top: 'auto',
			bottom: 0,
			draggable: false,
			onOpen: function() {
				var position = iChat.activeChatWindows.length;
				if (position == 0) position = 1;
				$(this).parent('.panel.window').css('bottom', 0).css('right', position * 300);
				
				var dlgOpts = $(this).dialog('options');
				if (position == 0) position = 1;
				id_ = '#chat-' + dlgOpts['jid_id'];
				var div = $(id_ + ' .chat-messages').get(0);
				div.scrollTop = div.scrollHeight;
			},
			onClose: function() {
				var dlgOpts = $(this).dialog('options');
				id_ = '#chat-' + dlgOpts['jid_id'];
				position = $.inArray(id_, iChat.activeChatWindows);
				if (~position) iChat.activeChatWindows.splice(position, 1);
				$(this).dialog("destroy").remove();
				$(iChat.activeChatWindows).each(function(e, el) {
					$(el).parent('.panel.window').css('top', 'auto').css('bottom', 0).css("left", "auto").css('right', (e + 1) * 300)
				});
			},
			destroy: function() {
				console.log("Chat " + $(this).dialog('options')['jid_id'] + " destroyed!");
			}
		});
		//$('#chat-' + this.jid_id).parent('.ui-dialog');
	} else {
		if (iChat.activeChatWindows.length == 3) {
			var last_dlg = iChat.activeChatWindows[0];
			$(last_dlg).dialog('close');
		}
		$('#chat-' + formatedJidId).dialog('open');
		position = $.inArray('#chat-' + formatedJidId, iChat.activeChatWindows);
		if (~position) {
			if (position == 0) position = 1;
			$('#chat-' + formatedJidId).parent('.ui-dialog').css('top', 'auto').css('bottom', 0).css("left", "auto").css('right', position * 300)
		} else {
			iChat.activeChatWindows.push('#chat-' + formatedJidId);
			position = iChat.activeChatWindows.length;
			if (position == 0) position = 1;
			$('#chat-' + formatedJidId).parent('.ui-dialog').css('top', 'auto').css('bottom', 0).css("left", "auto").css('right', position * 300);
		}
	}
	$('#chat-' + formatedJidId + " .composeInput").unbind("keypress").on('keypress', sendChatMessage);
	//$('#chat-' + this.jid_id + " a[role=button]").button();
}