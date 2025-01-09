//window.contextName = "/ekyzmet-ui/chat/js/";
BOSH_SERVICE = '/http-bind/';
BOSH_DOMAIN = 'chat.ipgo.kz';
window.chatConnectCount=0;
var chatSound=1;
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
var playChatSound = function(){
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
$(function() {
	var cssId = 'chatCss';
	if ($('#' + cssId).length == 0) {
		var head = document.getElementsByTagName('head')[0];
		var link = document.createElement('link');
		link.id = cssId;
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = window.contextName+'/chat/js/chat-files/chat.css';
		link.media = 'all';
		//head.appendChild(link);
	}
	cssId = 'chatCssUi';
	if ($('#' + cssId).length == 0) {
		var head = document.getElementsByTagName('head')[0];
		var link = document.createElement('link');
		link.id = cssId;
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.href = window.contextName+'/chat/js/chat-files/jquery-ui.css';
		link.media = 'all';
		//head.appendChild(link);
	}
    
//	$(document).trigger('connect');
	
	loadSoundFile(window.contextName+'/chat/js/chat-files/sound.mp3');
	$.get(window.mainUrl+"sfunc&cls=WsUtilNew&name=ipgo_chat&arg0="+userId, function(data) {
			if (data.login) {
				chatUser = data.login;
				chatKey = data.pass;
				IPGOChatConnect();
			} else {
				IPGOChatLogin();
			}
	},'json');
    
	
});
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
	pending_subscriber: null,
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
		mainPanel += "    <button id=\"statusSelect\" class=\"chtBtn chat-status-btn active-chat-status-online\" status='online'>Онлайн<\/button>";
		mainPanel += "    <a id=\"findContact\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-add'\">Поиск пользователей<\/a>";
		mainPanel += "    <\/div>";
		mainPanel += "  <\/div>";
			mainPanel += "<\/div>";
			$('body').append(mainPanel);
			var contactCtxMenu = "";
			contactCtxMenu += " <div id=\"context-menu\"><span class='chatCtxUserName'></span>";
			contactCtxMenu += "	      	<ul class=\"dropdown-menu\" role=\"menu\">";
			contactCtxMenu += "            <li><a tabindex=\"-1\" data-action='rename'><i class='icon-pencil'></i> Переименовать<\/a><\/li>";
			contactCtxMenu += "	           <li><a tabindex=\"-1\" data-action='delete'><i class='icon-remove'></i> Удалить<\/a><\/li>";
			contactCtxMenu += "	      	<\/ul>";
			contactCtxMenu += "	      <\/div>";
			$('body').append(contactCtxMenu);
			$("#chatPanel .closed").click(function() {
				$('#chatPanel #contactList,#chatPanel #footerPanel.chat-footer,#chatMainWindow ').toggle();
				$(".chat-header").removeClass("closed");
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
			$("#chatSearchQuery").val("");
			$("#chatSearchResult").html("");
			$("#searchchatUsers").dialog('open');
		});
			
			
			var searchDlgHtml = '<div id="searchchatUsers" class="jb-chat" class="hidden" title="Поиск пользователей">';
			searchDlgHtml +='<form class="form-horizontal" onsubmit="return chatSearchUsers();return false;"><table style="width:100%">';
			searchDlgHtml +='<tr><td style="padding: 10px; text-align: center;"><input type="text" class="" id="chatSearchQuery" placeholder="Введите Ф.И.О или E-mail">';
			searchDlgHtml +='<button type="submit" onclick="return chatSearchUsers();" class="chat-search btn btn-info" data-load ing-text="'+i18n("searching")+'..."><i class="icon-search icon-white"></i> '+i18n("find")+'</button>';
			searchDlgHtml +='<hr class="chat-hr" style="margin:10px 0px;"></td></tr><tr><td id="chatSearchResult"></td></tr></table></form></div>';
			$('body').append(searchDlgHtml);
			$("#searchchatUsers").dialog({
				autoOpen: false,
				height: 400,
				closed: true,
				width: 600,
				modal: true
			});
			var searchDlgHtml = '<div id="renamechatUsers" class="jb-chat" class="hidden" title="Переименовать"><form>';
				searchDlgHtml+='<table style="width:100%"><tr><td style="padding: 3px; text-align: ;">Название</td></tr><tr><td style="padding: 3px; text-align: ;"><input type="text" class="" id="renameChatUserName" style="width:100%"></td></tr>';
				//searchDlgHtml+='<tr><td style="padding: 3px; text-align: ;">Группа</td></tr><tr><td style="padding: 3px; text-align: ;"><select class="easyui-combobox" name="chatGroup" style="width:200px;" id="chatGroup"></select></td></tr>';
				searchDlgHtml+='</table></form></div>';
			$('body').append(searchDlgHtml);
            try {
    			$("#renamechatUsers").dialog({
    				autoOpen: false,
    				dialogClass: "jb-chat",
    				height: 250,
    				closed: true,
    				width: 350,
    				modal: true,
    				   buttons: [{
				text: "Сохранить",
				iconCls: 'icon-save',
				handler: function() {
					var q = iChat.pending_subscriber;
                    groupName = "";//$('#chatGroup').combobox('getValue');
                    //if ( typeof groupName === "undefined" || groupName=="")
                        //groupName = $('#chatGroup').combobox('getText');
					iChat.modifyContact(q, $("#renameChatUserName").val(),groupName);
					iChat.pending_subscriber = null;
                    $("#renameChatUserName").val("");
					$('#renamechatUsers').dialog('close');
					
				}
			}]
    			});
            } catch(err) {
                
            }
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
			var approveDlgHtml = "<div id='iChat_approve_dialog' style='padding:10px;' ><p><b id='approve-jid'></b> "+i18n("chat-invite")+"</p></div>";
			$('body').append(approveDlgHtml);
			var deny = i18n("deny");
			var allow = i18n("allow");
			
		
		
			try {
				$('#iChat_approve_dialog').dialog({
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
/*iChat.connection.send($pres({
						to: iChat.pending_subscriber,
						"type": "unsubscribed"
					}));*/
					var iq = $iq({
						'type': "set"
					}).c("query", {
						'xmlns': Strophe.NS.ROSTER
					}).c("item", {
						'jid': iChat.pending_subscriber,
						'subscription': "remove"
					});
					iChat.connection.sendIQ(iq);
					iChat.pending_subscriber = null;
					$('#iChat_approve_dialog').dialog('close');
				}
			},
			{
				text: i18n("allow"),
				iconCls: 'icon-ok',
				handler: function() {
					iChat.connection.send($pres({
						to: iChat.pending_subscriber,
						"type": "subscribed"
					}));
					iChat.connection.send($pres({
						to: iChat.pending_subscriber,
						"type": "subscribe"
					}));
					var q = iChat.pending_subscriber;
					q = q.replace("@" + BOSH_DOMAIN, "");
					iChat.getUser(q, function(result) {
						console.log(result);
						$(result).find('item').each(function() {
							var jid = $(this);
							jid.find('field').each(function() {
								if ($(this).attr('var') == "Name") name = $(this).find("value").text();
								if ($(this).attr('var') == "jid") jid_id = $(this).find("value").text();
							});
							iChat.modifyContact(jid_id, name);
						});
					});
					iChat.pending_subscriber = null;
					$('#iChat_approve_dialog').dialog('close');
				}
			}]
		});
			
            } catch(err) {
                
            }
			$(document).on('dblclick', "#contactList li ", function() {
				if ($(this).hasClass("chat-groupLi")) {
					$(this).nextAll().toggle();
				} else {
					var jid = $(this).data("jid");
					var name = $(this).data("name");
					
					var subscr = $(this).data("subsribe");
		            
		            if (subscr=='none') {
		                
		                alert(i18n("chat-user-not-add"));
		                return;
		                
		            }
					
					var jid_id = iChat.jid_to_id(jid);
					$('#contactList li[data-contact="' + iChat.jid_to_id(jid) + '"]').removeClass('chat-msg');
					$('.chat-title').removeClass('chat-msg');
					var chatW = new ChatWindow(jid_id, jid);
					chatW.addWindow();
					var me = iChat.jid_to_id(iChat.client_jid);
					$('#chat-' + jid_id + ' .chat-messages').html("");
					if (iChat.storage.isSet(me+"_"+jid_id + "history")) {
						var historyList = iChat.storage.get(me+"_"+jid_id + "history");
						var jid_id = iChat.jid_to_id(jid);
						$(historyList).each(function(e, el) {
							var msg_ = iChat.msg2html(el.jid, el.msg, el.self, new Date(el.dt));
							$('#chat-' + jid_id + ' .chat-messages').append(msg_);
						});
						id_ = '#chat-' + jid_id;
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
			$('.context').contextmenu({
				target: '#context-menu',
				before: function(e, context) {},
				onItem: function(context, e) {
					alert($(e.target).text());
				}
			})
			$(document).on('contextmenu', "#contactList li ", function(e) {
				var myvar = '<div id="chatContactCtxMenu" class="easyui-menu" style="width:180px;">'
	             + '<div  data-options="name:\'rename\'">Переименовать</div>'
	             + '<div data-options="name:\'delete\',iconCls:\'icon-save\'">Удалить</div>' + '</div>';
				$(myvar).menu({
					shadow: false,
					contactLi: $(this).attr("data-jid"),
	                contactName: $(this).text(),
					onClick: function(item) {
						contact = $("#chatContactCtxMenu").menu('options')['contactLi'];
						console.log(contact);
						if (item.name == 'delete') {
							var iq = $iq({
								'type': "set"
							}).c("query", {
								'xmlns': Strophe.NS.ROSTER
							}).c("item", {
								'jid': contact,
								'subscription': "remove"
							});
							iChat.connection.sendIQ(iq);
						} else if (item.name == "rename") {
						   contact = $("#chatContactCtxMenu").menu('options')['contactName'];
	                       $("#renameChatUserName").val(contact);
	                       iChat.pending_subscriber = $("#chatContactCtxMenu").menu('options')['contactLi'];
						   $("#renamechatUsers").dialog('open');
						} else if (item.name == "history") {
						   contact = $("#chatContactCtxMenu").menu('options')['contactLi'];
	                        
	                        var iq = $iq({type: 'get', id: iChat.connection.getUniqueId('retrieve')}).c('retrieve',
	                         {xmlns: Strophe.NS.ARCHIVE, 'with': this.jid});
	                        
	                		iChat.connection.sendIQ(iq, function(data) {console.log(data)});
	                       $("#chatHistoryMsg").dialog('open');
						}
					}
				}).menu('show', {
					left: e.pageX,
					top: e.pageY
				});
				return false;
			});
			$("#chatStatuses ul li").click(function() {
				console.log($(this).data("status"));
				status = $(this).data("status");
				var presence = $pres();
				if (status == 'online') {} else
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
	updateStatus: function() {
		var presence = $pres();
		if (iChat.status == 'online') {} else
			presence = $pres().c("show").t(iChat.status);
		
		iChat.connection.send(presence);
		console.log("Update status "+ iChat.status);
		window.setTimeout(function() {iChat.updateStatus();}, 10000);
	}
	,
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
			body = iChat.formatBody(body);
			// add the new message
			body = iChat.formatSmiles(body);
			var dt = new Date();
			var msg_ = iChat.msg2html(jid, body, false, dt);
			iChat.saveMessage(jid_id, jid, body, dt, false);
			if ($('#chat-' + jid_id + ' .chat-messages').length > 0) {
				$('#chat-' + jid_id + ' .chat-messages').append(msg_);
				iChat.scroll_chat(jid_id);
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
			src = window.contextName + '/chat/chat-files/smiles/' + iChat.smilesArray[j][0];
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
		var curr_month = date.getMonth();
		curr_month++;
		var curr_year = date.getFullYear();
		var curr_hour = date.getHours();
		var curr_min = date.getMinutes();
		if (curDate.getDate() != curr_date && curr_month != curDate.getMonth() + 1) {
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
		$(iq).find('item').each(function() {
			console.log("Setting contact list ");
			
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
			iChat.connection.addHandler(iChat.on_presence, null, "presence");
			iChat.connection.send($pres());
		});
	},
	on_roster_changed: function(iq) {
		$(iq).find('item').each(function() {
			console.log("Update contact list ");
			var sub = $(this).attr('subscription');
			var jid = $(this).attr('jid');
			var name = $(this).attr('name') || jid;
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
			} else {
				var contact_html = $("<li class='chat-contactLi' data-contact='" + jid_id + "' data-name='" + name + "' data-jid='" + jid + "'  data-subsribe='" + sub + "' class='" + ($('#contactList li[data-contact="' + jid_id + '"]').attr('class') || "offline") + "'>" + "<div>" + name + "</div></li>");
				if ($('#contactList li[data-contact="' + jid_id + '"]').length > 0) {
					$('#contactList li[data-contact="' + jid_id + '"]').remove();
				}
				iChat.insert_contact(contact_html, groupName);
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
			'jid': iChat.pending_subscriber,
			'subscription': "remove"
		});
		iChat.connection.sendIQ(iq);
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
		/*$('#contactList li div').contextmenu({
			target: '#context-menu',
			onItem: function(context, e) {
				ctxAction = $(e.target).data("action");
				contact = $(context).parent().attr("data-jid");
				if (ctxAction == 'delete') {
					var iq = $iq({
						'type': "set"
					}).c("query", {
						'xmlns': Strophe.NS.ROSTER
					}).c("item", {
						'jid': contact,
						'subscription': "remove"
					});
					iChat.connection.sendIQ(iq);
				} else if (ctxAction == 'rename') {
					 //contact = $("#chatContactCtxMenu").menu('options')['contactName'];
					 contact = $(context).text();
                     $("#renameChatUserName").val(contact);
                     iChat.pending_subscriber = $(context).parent().attr("data-jid");;
					 $("#renamechatUsers").dialog('open');
				}
			}
		});*/
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
/*if (from.indexOf("/") > 0) {
			from = from.substring(0, from.indexOf("/"));
		}*/
		if (ptype === 'subscribe') {
			if ($('#contactList li[data-contact="' + iChat.jid_to_id(from) + '"]').length == 0) {
				iChat.pending_subscriber = from;
				var q = iChat.pending_subscriber;
				q = q.replace("@" + BOSH_DOMAIN, "");
				iChat.getUser(q, function(result) {
					console.log(result);
					$(result).find('item').each(function() {
						var jid = $(this);
						jid.find('field').each(function() {
							if ($(this).attr('var') == "Name") name = $(this).find("value").text();
							if ($(this).attr('var') == "jid") jid_id = $(this).find("value").text();
						});
						$('#approve-jid').text(name);
						$('#iChat_approve_dialog').dialog('open');
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
		var html = "<table class='table table-bordered table-striped chat-table-results'><tr><th>Ф.И.О</th><th>E-mail</th><th></th></tr>";
		var finded = false;
		$(result).find('item').each(function() {
			html += "<tr>";
			var jid = $(this);
			var name = "";
			var email = "";
			var username = "";
			var jid_id = "";
			jid.find('field').each(function() {
				finded = true;
				if ($(this).attr('var') == "Name") name = $(this).find("value").text();
				if ($(this).attr('var') == "Email") email = $(this).find("value").text();
				if ($(this).attr('var') == "Username") username = $(this).find("value").text();
				if ($(this).attr('var') == "jid") jid_id = $(this).find("value").text();
			});
			bt = '<button type="button" class="btn" onclick="chatAddContact(this)" data-jid="' + jid_id + '" data-name="' + name + '" data-username="' + username + '" data-email="' + email + '"><i class="icon-plus"></i></button>';
			html += "<td>" + name + "</td><td>" + email + "</td><td>" + bt + "<span style='display:none' id='req_"+username +"'>"+i18n("request-sended")+"</span></td></tr>";
		});
		html += "</table>";
		iChat.resultDiv.html(html);
		if(finded) {
		$("button", iChat.resultDiv).button({
			icons: {
				primary: "ui-icon-plus"
			},
			text: false
		});
		}
		console.log(html);
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
		var msg_ = iChat.msg2html(iChat.client_jid, body, true, dt);
		iChat.saveMessage(jid_id, jid, body, dt, true);
		$('#chat-' + jid_id + ' .chat-messages').append(msg_);
		iChat.scroll_chat(jid_id);
		$(this).text('');
	} else {}
}

function chatAddContact(btn) {
	data = {
		jid: $(btn).data("jid"),
		name: $(btn).data("name")
	}
	$(btn).hide();
	$("#req_"+$(btn).data("username")).show();
	var iq = $iq({
		type: "set"
	}).c("query", {
		xmlns: "jabber:iq:roster"
	}).c("item", data);
	iChat.connection.sendIQ(iq);
	var subscribe = $pres({
		to: data.jid,
		"type": "subscribe"
	});
	iChat.connection.send(subscribe);
}

function chatSearchUsers() {
	iChat.resultDiv = $("#chatSearchResult");
	iChat.searchUsers($("#chatSearchQuery").val(), $("#chatSearchResult"));
	return false;
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
	if ($('#chat-' + this.jid_id).length == 0) {
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
		iChat.activeChatWindows.push('#chat-' + this.jid_id);
		$('#chat-' + this.jid_id).dialog({
			autoOpen: true,
			title: iChat.getNameFromJid(this.jid),
			jid: this.jid,
			jid_id: this.jid_id,
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
				console.log("dfgdfg");
			}
		});
		//$('#chat-' + this.jid_id).parent('.ui-dialog');
	} else {
		if (iChat.activeChatWindows.length == 3) {
			var last_dlg = iChat.activeChatWindows[0];
			$(last_dlg).dialog('close');
		}
		$('#chat-' + this.jid_id).dialog('open');
		position = $.inArray('#chat-' + this.jid_id, iChat.activeChatWindows);
		if (~position) {
			if (position == 0) position = 1;
			$('#chat-' + this.jid_id).parent('.ui-dialog').css('top', 'auto').css('bottom', 0).css("left", "auto").css('right', position * 300)
		} else {
			iChat.activeChatWindows.push('#chat-' + this.jid_id);
			position = iChat.activeChatWindows.length;
			if (position == 0) position = 1;
			$('#chat-' + this.jid_id).parent('.ui-dialog').css('top', 'auto').css('bottom', 0).css("left", "auto").css('right', position * 300);
		}
	}
	$('#chat-' + this.jid_id + " .composeInput").unbind("keypress").on('keypress', sendChatMessage);
	//$('#chat-' + this.jid_id + " a[role=button]").button();
}
function IPGOChatConnect() {
	
	
	
    
	var chatHost = window.location.hostname;
	var port = window.location.port;
	if (port!="80") {
		chatHost+=":"+port;
	}
	var conn = new Strophe.Connection("ws://"+chatHost+"/xmpp-websocket/", {protocol: "ws"});
	//var conn = new Openfire.Connection("ws://chat.ipgo.kz:7070/ws/server");
	var chatLogin = chatUser + "@" + BOSH_DOMAIN;
	conn.connect(chatLogin, chatKey, function(status) {
		console.log("CHAT_STATUS " + status);
		if (status === Strophe.Status.CONNECTED) {
			window.chatConnectCount=0;
			//$(document).trigger('connected');
            IPGOChatConnected()
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
function chatIpgoAuth() {
	
	$.get(window.mainUrl+"sfunc&cls=WsUtilNew&name=ipgo_chat&arg0="+userId+"&"+$('#chatIpgoLoginForm').serialize(), function(data) {
			if (data.login) {
				chatUser = data.login;
				chatKey = data.pass;
				$("#contactList").html("");
				$("#footerPanel").addClass("chat-footer").show();
				IPGOChatConnect();
			} else {
				if (data.error)
					alert(data.error);
				
				IPGOChatLogin();
			}
	},'json');
	
}
function IPGOChatLogin() {
	iChat.initChat();
	$("#footerPanel").removeClass("chat-footer").hide();
	$form = "<div class='chatIpgoLogo'></div><h3>Введите электронный адрес и пароль от ИПГО</h3><form id='chatIpgoLoginForm'  autocomplete='off'><table class='ipgochatLogin'>";
	$form+="<!-- <tr><th>Электронный адрес</th></tr>--> <tr><td><input type='text' placeholder='Электронный адрес' value='' name='arg1' autocomplete='off'></td></tr>";
	$form+="<!-- <tr><th>Пароль</th></tr>--><tr><td><input type='password' placeholder='Пароль' name='arg2' value='' autocomplete='off'></td></tr>";
	$form+="</table></form>";
	$form+="<button onclick='chatIpgoAuth()' class='chatipgoLoginBtn'>Авторизация</button>";
	$("#contactList").html($form);
}
$(window).unload(function() {
	console.log("Log out from chat" );
	iChat.connection.options.sync = true; // Switch to using synchronous requests since this is typically called onUnload.
	iChat.connection.flush();
	iChat.connection.disconnect();
});

function IPGOChatConnected() {

//
//$(document).bind('connected', function() {
	console.log('connected');
	iChat.storage = $.sessionStorage;
	
	var iq = $iq({
		type: 'get'
	}).c('query', {
		xmlns: 'jabber:iq:roster'
	});
    
    //var open = $build("open");
    //iChat.connection.sendIQ(open, null);
	//
    
	iChat.connection.sendIQ(iq, iChat.on_roster);
	iChat.connection.addHandler(iChat.on_roster_changed, "jabber:iq:roster", "iq", "set");
	iChat.connection.addHandler(iChat.on_message, null, "message", "chat");
	iChat.connection.addHandler(iChat.on_presence, null, "presence");
	//var presence = $pres();
	//iChat.connection.send(presence);
	console.log("connected done");
    iChat.updateStatus();
	window.setTimeout(function() {iChat.updateStatus()}, 10000);
//});
}
/*!
 * Bootstrap Context Menu
 * Author: @sydcanem
 * https://github.com/sydcanem/bootstrap-contextmenu
 *
 * Inspired by Bootstrap's dropdown plugin.
 * Bootstrap (http://getbootstrap.com).
 *
 * Licensed under MIT
 * ========================================================= */
;
(function($) {
	'use strict';
/* CONTEXTMENU CLASS DEFINITION
	 * ============================ */
	var toggle = '[data-toggle="context"]';
	var ContextMenu = function(element, options) {
		this.$element = $(element);
		this.before = options.before || this.before;
		this.onItem = options.onItem || this.onItem;
		this.scopes = options.scopes || null;
		if (options.target) {
			this.$element.data('target', options.target);
		}
		this.listen();
	};
	ContextMenu.prototype = {
		constructor: ContextMenu,
		show: function(e) {
			var $menu, evt, tp, items, relatedTarget = {
				relatedTarget: this,
				target: e.currentTarget
			};
			if (this.isDisabled()) return;
			this.closemenu();
			if (!this.before.call(this, e, $(e.currentTarget))) return;
			$menu = this.getMenu();
			$menu.trigger(evt = $.Event('show.bs.context', relatedTarget));
			tp = this.getPosition(e, $menu);
			items = 'li:not(.divider)';
			$menu.attr('style', '').css(tp).addClass('open').on('click.context.data-api', items, $.proxy(this.onItem, this, $(e.currentTarget))).trigger('shown.bs.context', relatedTarget);
			// Delegating the `closemenu` only on the currently opened menu.
			// This prevents other opened menus from closing.
			$('html').on('click.context.data-api', $menu.selector, $.proxy(this.closemenu, this));
			return false;
		},
		closemenu: function(e) {
			var $menu, evt, items, relatedTarget;
			$menu = this.getMenu();
			if (!$menu.hasClass('open')) return;
			relatedTarget = {
				relatedTarget: this
			};
			$menu.trigger(evt = $.Event('hide.bs.context', relatedTarget));
			items = 'li:not(.divider)';
			$menu.removeClass('open').off('click.context.data-api', items).trigger('hidden.bs.context', relatedTarget);
			$('html').off('click.context.data-api', $menu.selector);
			// Don't propagate click event so other currently
			// opened menus won't close.
			return false;
		},
		keydown: function(e) {
			if (e.which == 27) this.closemenu(e);
		},
		before: function(e) {
			return true;
		},
		onItem: function(e) {
			return true;
		},
		listen: function() {
			this.$element.on('contextmenu.context.data-api', this.scopes, $.proxy(this.show, this));
			$('html').on('click.context.data-api', $.proxy(this.closemenu, this));
			$('html').on('keydown.context.data-api', $.proxy(this.keydown, this));
		},
		destroy: function() {
			this.$element.off('.context.data-api').removeData('context');
			$('html').off('.context.data-api');
		},
		isDisabled: function() {
			return this.$element.hasClass('disabled') || this.$element.attr('disabled');
		},
		getMenu: function() {
			var selector = this.$element.data('target'),
				$menu;
			if (!selector) {
				selector = this.$element.attr('href');
				selector = selector && selector.replace(/.*(?=#[^\s]*$)/, ''); //strip for ie7
			}
			$menu = $(selector);
			return $menu && $menu.length ? $menu : this.$element.find(selector);
		},
		getPosition: function(e, $menu) {
			var mouseX = e.clientX,
				mouseY = e.clientY,
				boundsX = $(window).width(),
				boundsY = $(window).height(),
				menuWidth = $menu.find('.dropdown-menu').outerWidth(),
				menuHeight = $menu.find('.dropdown-menu').outerHeight(),
				tp = {
					"position": "absolute",
					"z-index": 9999
				},
				Y, X, parentOffset;
			if (mouseY + menuHeight > boundsY) {
				Y = {
					"top": mouseY - menuHeight + $(window).scrollTop()
				};
			} else {
				Y = {
					"top": mouseY + $(window).scrollTop()
				};
			}
			if ((mouseX + menuWidth > boundsX) && ((mouseX - menuWidth) > 0)) {
				X = {
					"left": mouseX - menuWidth + $(window).scrollLeft()
				};
			} else {
				X = {
					"left": mouseX + $(window).scrollLeft()
				};
			}
			// If context-menu's parent is positioned using absolute or relative positioning,
			// the calculated mouse position will be incorrect.
			// Adjust the position of the menu by its offset parent position.
			parentOffset = $menu.offsetParent().offset();
			X.left = X.left - parentOffset.left;
			Y.top = Y.top - parentOffset.top;
			return $.extend(tp, Y, X);
		}
	};
/* CONTEXT MENU PLUGIN DEFINITION
	 * ========================== */
	$.fn.contextmenu = function(option, e) {
		return this.each(function() {
			var $this = $(this),
				data = $this.data('context'),
				options = (typeof option == 'object') && option;
			if (!data) $this.data('context', (data = new ContextMenu($this, options)));
			if (typeof option == 'string') data[option].call(data, e);
		});
	};
	$.fn.contextmenu.Constructor = ContextMenu;
/* APPLY TO STANDARD CONTEXT MENU ELEMENTS
	 * =================================== */
	$(document).on('contextmenu.context.data-api', function() {
		$(toggle).each(function() {
			var data = $(this).data('context');
			if (!data) return;
			data.closemenu();
		});
	}).on('contextmenu.context.data-api', toggle, function(e) {
		$(this).contextmenu('show', e);
		e.preventDefault();
		e.stopPropagation();
	});
}(jQuery));