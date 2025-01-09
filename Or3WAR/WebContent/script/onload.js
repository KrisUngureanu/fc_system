var enableWnd = false;
var actionType = null;
var lastChange = null;

jQuery(document).ready(
	function() {
		bootbox.animate(false);
		
	    $("[processId]").click(function () {
	        startProcess($(this));
	      });

	    $("[actionId]").click(function () {
	    	pleaseWait(null, "change");
		    makeAction($(this));
	      });
		$("[changeId]").change(function() {
			makeChange($(this));
		});
		$("[reportId]").click(function() {
			var reportId = $(this).attr('reportId');
			showReport(reportId);
		});
	    $("table.tbl tbody tr").click(function (e) {
	    	selectIfcRow(e, $(this).get(0));
        });

	    $('.modal-header button').text('×');

	    $(".modifier").click(function (e) {
	    	modifyValue($(this));
	    });

	    $(".cleaner").click(function (e) {
	    	cleanDialog($(this));
	    	stopWait();
	    });

	    $(".autocleaner").click(function (e) {
	    	autoCleanDialog($(this));
	    });

	    $(".dlgcleaner").click(function (e) {
	    	dlgCleanDialog($(this));
	    	stopWait();
	    });

	    $(".chpcleaner").click(function (e) {
	    	cleanPasswordDialog($(this));
	    	stopWait();
	    });

	    $(".fotocleaner").click(function (e) {
	    	cleanFotoDialog($(this));
	    });

	    $(".errorcleaner").click(function (e) {
	    	cleanErrorDialog($(this));
	    });
	    $(".errorautocleaner").click(function (e) {
	    	$('#errorModalAuto').modal('hide');
	    	$('#errorModalAuto').data('modal', null);
	    });

	    $(".dpick").datepicker({weekStart: 1, autoclose: true, language: 'ru'});
	    
	    createFormatControl($(".dpick"));
	    createFormatControlIntField($("[formatting]"));
	    createWYSIWYG($("[wysiwyg]"));
	}
);

if (!Array.prototype.indexOf){// Для IE
	Array.prototype.indexOf = function(elt/*, from*/){
    	var len = this.length,
    		from = Number(arguments[1]) || 0;
    	from = (from < 0) ? Math.ceil(from) : Math.floor(from);
        if (from < 0) from += len;
		for (; from < len; from++){
      		if (from in this && this[from] === elt)
        		return from;
    	}
    	return -1;
  	};
}


$(window).unbind('load');
$(window).bind("load", function() {
	resize($(this));
	$(this).unbind('resize');
	$(this).bind("resize", function() {
		resize($(this));
	});
});

function resize(wnd) {
	var post = "xml=1&trg=frm&cmd=rsz&w="+$(wnd).width()+"&h="+$(wnd).height();
	var req = createAsync();
	var func = "";
	sendAsync(req, post, func, true);
}

function pleaseWait(msg, type) {
	actionType = type;
	msg = msg || (currentLang=="kz"?"Сіздің сұранымыңыз өңделуде. Өтінеміз, күте тұрыңыз...":"Ваш запрос обрабатывается. Пожалуйста, ждите...");
	$('body').find('table').first().block({message: msg, css: { 
        border: 'none', 
        padding: '15px', 
        backgroundColor: '#000', 
        '-webkit-border-radius': '10px', 
        '-moz-border-radius': '10px', 
        opacity: .5, 
        color: '#fff' 
    } }); 
}

function stopWait(type) {
	if (actionType == type || type == null) {
		$('body').find('table').first().unblock();
	}
}

function createUploader(id, text, action) {
	if (document.getElementById(id)) {
		var uploader = new qq.FileUploader(
				{
					element : document.getElementById(id),
					action : action,
					uploadButtonText : '<i class="icon-upload icon-white"></i> '
							+ text,
					template : '<div class="qq-uploader span12">'
							+ '<div class="qq-upload-button btn btn-success" style="width: auto;">{uploadButtonText}</div>'
							+ '</div>',
					template : '<div class="qq-uploader span12">'
							+ '<pre class="qq-upload-drop-area span12"><span>{dragText}</span></pre>'
							+ '<div class="qq-upload-button btn btn-success" style="width: auto;">{uploadButtonText}</div>'
							+ '<ul class="qq-upload-list" style="margin-top: 10px; text-align: center;"></ul>'
							+ '</div>',
					classes : {
						button : 'qq-upload-button',
						drop : 'qq-upload-drop-area',
						dropActive : 'qq-upload-drop-area-active',
						dropDisabled : 'qq-upload-drop-area-disabled',
						list : 'qq-upload-list',
						progressBar : 'qq-progress-bar',
						file : 'qq-upload-file',
						spinner : 'qq-upload-spinner',
						finished : 'qq-upload-finished',
						size : 'qq-upload-size',
						cancel : 'qq-upload-cancel',
						failText : 'qq-upload-failed-text',
						success : '',
						fail : 'alert',
						successIcon : null,
						failIcon : null
					}
				});
	}
}

function createUploader2(id, text, action, template) {
	if (document.getElementById(id)) {
		var uploader = new qq.FileUploader(
				{
					element : document.getElementById(id),
					action : action,
					uploadButtonText : text,
					template : '<div class="qq-uploader span12">'
							+ '<pre class="qq-upload-drop-area span12"><span>{dragText}</span></pre>'
							+ '<div class="qq-upload-button" style="width: auto;  background: none; border-bottom: none; color: none;  padding: none;">'
							+ template
							+ '</div>'
							+ '<ul class="qq-upload-list" style="margin-top: 10px; text-align: center;"></ul>'
							+ '</div>',
					classes : {
						button : 'qq-upload-button',
						drop : 'qq-upload-drop-area',
						dropActive : 'qq-upload-drop-area-active',
						dropDisabled : 'qq-upload-drop-area-disabled',
						list : 'qq-upload-list',
						progressBar : 'qq-progress-bar',
						file : 'qq-upload-file',
						spinner : 'qq-upload-spinner',
						finished : 'qq-upload-finished',
						size : 'qq-upload-size',
						cancel : 'qq-upload-cancel',
						failText : 'qq-upload-failed-text',
						success : 'alert',
						fail : 'alert',
						successIcon : null,
						failIcon : null
					}

				});
	}
}

function cleanDialog(obj) {
	var id = obj.attr('data-id');
	
	var cmd = $('#modal' + id).find('#cmd').val();

	var post = "xml=1&trg=frm&cmd=" + cmd + "&val="+obj.attr('data-value');
	
	if (cmd == 'pcl') {
		var fid = $('#modal' + id).find('#id').val();
		post += "&id=" + fid;

		var row = $('#modal' + id).find('#row');
		var col = $('#modal' + id).find('#col');

		if (row.length > 0 && col.length > 0) {
			post += "&row="+row.val()+"&col="+col.val();
		}

	}
	
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);

	$('#modal' + id).modal('hide');
	$('#modal' + id).data('modal', null);
}

function autoCleanDialog(obj) {
	var val = obj.attr('data-value');

	if (val == 0 || val == '0') {
		var post = "xml=1&trg=frm&cmd=accm";
		var req = createAsync();
		var func = "processCanCommitAuto";
		sendAsync(req, post, func, true);
	} else {
		var post = "xml=1&trg=frm&cmd=wcl&val=AUTOCLEAR";
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);

		$('#modalAuto').modal('hide');
		$('#modalAuto').data('modal', null);
    	stopWait();
	}
}

function dlgCleanDialog(obj) {
	var val = obj.attr('data-value');

	if (val == 0 || val == '0') {
		var post = "xml=1&trg=frm&cmd=dccm&permit=1";
		var req = createAsync();
		var func = "processCanCommit";
		sendAsync(req, post, func, true);
	} else {
		var post = "xml=1&trg=frm&cmd=wcl&val=CLEAR";
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);

		$('#modalDlg').modal('hide');
		$('#modalDlg').data('modal', null);
	}
}

function cleanPasswordDialog(obj) {
	var des = obj.attr('data-value');
	if (des == 0 || des == '0') {
		var post = "xml=1&trg=frm&cmd=changePassword&old="+$('#oldpass').val() + "&new=" + $('#newpass').val() + "&confirm=" + $('#confirm').val();
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
	} else {
		$('#chpModal').modal('hide');
		$('#chpModal').data('modal', null);
	}
}

function cleanFotoDialog(obj) {
	var id = obj.attr('data-id');
	var des = obj.attr('data-value');
	if (des == 0 || des == '0') {
		$('#fotoModal' + id).find('form').submit();
	} else {
		$('#fotoModal' + id).modal('hide');
		$('#fotoModal' + id).data('modal', null);
	}
}

function cleanErrorDialog(obj) {
	var des = obj.attr('data-value');
	var repIn = des == 0 || des == '0';
	if (repIn) {
		selectCommit(0);
		stopWait();
	} else {
		selectCommit(1);
	}
	$('#errorModal').modal('hide');
	$('#errorModal').data('modal', null);
	// если диалог был вызван с гиперссылки и была нажата кнопка "сохранить"
	if(!repIn){
		var children = $(obj).parent().parent().children();
		var table = $(children.get(1)).children().get(0);
		if($(table).attr('hl')){
			hiperLabelForward();
		}
	}
	
}

var pingLost = 0;
var timeout6 = null;
var timeoutFunction6;
var lastStartedId = "";
var beginTime = null;

function createAsync() {
	return (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
}

function sendAsync(req, post, func, enable,param) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		req.onreadystatechange = function() { processAsyncAnswer(req, post, func, enable,param); };
		beginTime = (new Date).getTime();
		req.send(post);
	}
}

function processAsyncAnswer(req, post, fn, enable, param) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var pingTime = (new Date).getTime() - beginTime;
			if ($('#ping') != null){
				$('#ping').text('' + pingTime);
			}
			if (pingLost > 0 && enable){
				enableWholeWindow(true);
			}
			pingLost = 0;
			timeout6 = null;
			
			if (req.responseText.length == 0 || req.responseXML==null){
				return;
			}
			
			var rts = req.responseXML.getElementsByTagName("r");
			if (rts == null || rts.length == 0){
				return;
			}
			
			var rt = rts[0];
			var tags = rt.getElementsByTagName("alert");
			if (tags != null && tags.length > 0) {
				for (var i = 0; i < tags.length; i++) {
					var tag = tags[i];
					var msg = tag.childNodes[0].nodeValue;
					if (msg != null && msg.length > 0) {
						var tagCode = rt.getElementsByTagName("code");
						if (tagCode != null && tagCode.length > 0) {
							var code = tagCode[0].childNodes[0].nodeValue;
							if (code=="completeMessage"){ // Пароль успешно изменен
								$('#chpModal').modal('hide');
								$('#chpModal').data('modal', null);
							}
						}
						bootbox.alert("<div style='width: 480px;'>" + msg.replace(/\n|\\n/g, "<br/>") + "</div>");
						stopWait();
					}
				}
			}
			tags = rt.getElementsByTagName("fatal");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					pleaseWait(msg);
					bootbox.alert("<div style='width: 480px;'>" + msg.replace(/\n|\\n/g, "<br/>") + "</div>");
					//stopWait();
					return;
				}
			}
			
			tags = rt.getElementsByTagName("stopWait");
			if (tags != null && tags.length > 0) {
				stopWait();
			}

			tags = rt.getElementsByTagName("openUI");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					openUI(msg);
					return;
				}
			}
			
			tags = rt.getElementsByTagName("prev");
			if (tags != null && tags.length > 0) {
				openPrev();
				return;
			}

			tags = rt.getElementsByTagName("openDialog");
			if (tags != null && tags.length > 0) {
				var changeTag = tags[0];
				var tag = changeTag.getElementsByTagName("id")[0];
				var frmId = tag.childNodes[0].nodeValue;
				
				tag = changeTag.getElementsByTagName("title")[0];
				var title = tag.childNodes[0].nodeValue;

				tag = changeTag.getElementsByTagName("width")[0];
				var width = tag.childNodes[0].nodeValue;
				tag = changeTag.getElementsByTagName("height")[0];
				var height = tag.childNodes[0].nodeValue;
				var h = parseInt(height);
				if (h < 100) h = document.body.clientHeight - 100;
				var w = parseInt(width);
				if (w < 101) w = document.body.clientWidth - 50;

				if (frmId != null && frmId.length > 0) {
					openDialog(frmId, title, w, h);
				}
			}

			if (fn == "processNode")
				processNode(req);
			else if (fn == "startProcessResponse")
				startProcessResponse(req);
			else if (fn == "rollbackResponse")
				rollbackResponse(rt);
			else if (fn == "afterExit")
				afterExit(rt);
			else if (fn == "redirectToReport")
				redirectToReport(rt);

// From operations.js						
			else if (fn == "processAddRow")
				processAddRow(req);
			else if (fn == "processPopupPressed")
				processPopupPressed(req);
			else if (fn == "processMapPressed")
				processMapPressed(req);
			else if (fn == "processAfterSign")
				processAfterSign(req);
			else if (fn == "processGetEditor")
				processGetEditor(req);
			else if (fn == "processCanCommit")
				processCanCommit(req);
			else if (fn == "processCanCommitAuto")
				processCanCommitAuto(req);
			else if (fn == "processNodeIfc")
				processNodeIfc(req);
			else if (fn == "hiperLabelCanCommit")
				hiperLabelCanCommit(req,param);

// From commit.js						
			else if (fn == "processCommit")
				processCommit(req);
			else if (fn == "processDCommit")
				processDCommit(req);
			else if (fn == "processACommit")
				processACommit(req);
			else if (fn == "processCancel")
				processCancel(req);
			else if (fn == "processRollback")
				processRollback(rt);
			else if (fn == "processPrevious")
				processPrevious(req);
			else if (fn == "processSign")
				processSign(req);
			else if (fn == "processAfterSign")
				processAfterSign(req);
			else if (fn == "enableWindowResponse")
				enableWindowResponse();
			
		} else {
			pingLost++;
			var t = pingLost * 5;
			disableWholeWindow(currentLang=="kz"?("Сервермен байланыс жоғалды. Қайта қосылу "+t+" секундтан кейін."):("Связь с сервером утеряна. " +
					"Повторная попытка подключения через " + t + " сек."), false, t);
			timeoutFunction6 = function() { sendAsync(req, post, fn, enable); };
			timeout6 = setTimeout(timeoutFunction6, t * 1000);
		}
	}
}

function checkConfirms(frameId) {
	var post = "xml=1&trg=frm&id=" + frameId + "&cmd=ccf";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function selectOption(id, opt) {
	var post = "xml=1&trg=frm&cmd=sop&id=" + id + "&opt=" + opt;
	var req = createAsync();
	var func = "";
	sendAsync(req, post, func, true);
}

function selectCommit(opt) {
	var post = "xml=1&trg=frm&cmd=commitOptionSelected&opt=" + opt;
	var req = createAsync();
	var func = (opt == 0 || enableWnd) ? "enableWindowResponse" : "";
	sendAsync(req, post, func);
	
	if (opt == 0) {
		enableButton("prev", true);
		enableButton("run", true);
	}
}

function startProcess(node) {
	var procId = node.attr('processId');
	var msg = node.attr('msg');
	var title = (node.attr('data-original-title') != null) ? node.attr('data-original-title') : node.text();
	
	//var c = (!askConfirm) ? true : confirm(msg + ": '" + title + "'?");
	if (!askConfirm) {
		var post = "xml=1&trg=srv&cmd=crp&id=" + procId;
		var req = createAsync();
		var func = "startProcessResponse";
		sendAsync(req, post, func);
	} else {
		bootbox.confirm("<div style='width: 480px;'>" + msg.replace(/\n|\\n/g, "<br/>") + ": '" + title + "'?" + "</div>", currentLang=="kz"?"Болдырмау":"Отмена", currentLang=="kz"?"Ия":"Да", function(result) {
		    if (result) {
				var post = "xml=1&trg=srv&cmd=crp&id=" + procId;
				var req = createAsync();
				var func = "startProcessResponse";
				sendAsync(req, post, func);
		    }
		});
	}
}

function startProcessResponse(req) {
	
	var responseTag = req.responseXML.getElementsByTagName("r")[0];
	var tags = responseTag.getElementsByTagName("tr");

	if (tags != null && tags.length > 0) {
		var d = document;

		var rowId = tags[0].getAttribute("id");
		lastStartedId = rowId;
		
		var table = d.getElementById("taskTable");
		var tbody = table.getElementsByTagName('tbody')[0];
		var selCol = table.getAttribute("selectedCol");
		var oldCol = parseInt(selCol);
        	
		var row = d.createElement("TR");
		row.setAttribute("id", rowId);

		var cname = tags[0].getAttribute("class");

		if (cname == "selected") {
			removeSelection(tbody, oldCol);
		}

		row.className = cname;

		row.height = tags[0].getAttribute("height");
		var st = tags[0].getAttribute("style");
		if (st != null) {
			row.style.cssText = st;
		}
		var strOnClick = tags[0].getAttribute("onclick");

		row.onclick = function(event) {
			eval(strOnClick);
		};

		var tdTags = tags[0].getElementsByTagName("td");
		var msg1 = "";
		var msg2 = "";
	
		for (var j = 0; j < tdTags.length; j++) {
			var tdTag = tdTags[j];
			var td = d.createElement("TD");
			for (var k = 0; k < tdTag.attributes.length; k++) {
				if (tdTag.attributes[k].nodeName == "style") {
					td.style.cssText = tdTag.attributes[k].nodeValue;
				} else if (tdTag.attributes[k].nodeName == "class") {
					td.className = tdTag.attributes[k].nodeValue;
				} else if (tdTag.attributes[k].nodeName == "onclick") {
					var type = 0;
					if (tdTag.attributes[k].nodeValue.indexOf("nextStep") > -1) {
						type = 1;
						var b1 = tdTag.attributes[k].nodeValue.indexOf("'");
						var b2 = tdTag.attributes[k].nodeValue.indexOf("'", b1 + 1);
						msg1 = tdTag.attributes[k].nodeValue.substring(b1 + 1, b2);
					}
					else if (tdTag.attributes[k].nodeValue.indexOf("killProcess") > -1) {
						type = 2;
						var b1 = tdTag.attributes[k].nodeValue.indexOf("'");
						var b2 = tdTag.attributes[k].nodeValue.indexOf("'", b1 + 1);
						msg2 = tdTag.attributes[k].nodeValue.substring(b1 + 1, b2);
					}
					else if (tdTag.attributes[k].nodeValue.indexOf("openControlInterface") > -1) {
						type = 3;
					}
					if (type == 0) {
						td.onclick = function() {
							eval("openInterface(this);");
						};               
					} else if (type == 1) {
						td.onclick = function() {
							eval("nextStep(this, '" + msg1 + "');");
						};
					} else if (type == 2) {
						td.onclick = function() {
							eval("killProcess(this, '" + msg2 + "');");
						};
					} else if (type == 3) {
						td.onclick = function() {
							eval("openControlInterface(this);");
						};
					}
				} else {
					td.setAttribute(tdTag.attributes[k].nodeName, tdTag.attributes[k].nodeValue);
				}
			}
	
			var children = tdTag.childNodes;
			var n = 0;
			for (n = 0; n < children.length; n++) {
				var child = children[n];
				var chres = getElementNode(d, child);
				if (chres != null) {
					td.appendChild(chres);
				}
			}
			row.appendChild(td);
		}

        tbody.appendChild(row);
	}
}

function rollback() {
	pleaseWait(currentLang=="kz"?"Барлық сақталмаған өзгерістерді болдырмау...":"Отмена всех несохраненных изменений...");

	var post = "xml=1&trg=frm&cmd=rlb";
	var req = createAsync();
	var func = "processRollback";
	sendAsync(req, post, func, false);
}

function commit() {
	enableWnd = true;
	pleaseWait();
	var post = "xml=1&trg=frm&cmd=com";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, false);
}

function previous() {
	enableWnd = false;
	pleaseWait();
	enableButton("prev", false);
	var post = "xml=1&trg=frm&cmd=previous";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, false);
}

function next() {
	enableWnd = false;
	pleaseWait();
	enableButton("run", false);
	var post = "xml=1&trg=frm&cmd=run";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, false);
}

function rollbackResponse(responseTag) {
	var d = document;
	
	var tags = responseTag.getElementsByTagName("confirm");
	if (tags != null && tags.length > 0) {
		var tag = tags[0];
		var msg = tag.childNodes[0].nodeValue;
		if (msg != null && msg.length > 0) {
			bootbox.confirm("<div style='width: 480px;'>" + msg.replace(/\n|\\n/g, "<br/>") + "</div>", currentLang=="kz"?"Болдырмау":"Отмена", currentLang=="kz"?"Ия":"Да", function(c) {
				var post = "xml=1&trg=frm&cmd=cfr&res=";
				post += (c) ? "3" : "4";
				var req = createAsync();
				var func = "";
				sendAsync(req, post, func);
				if (!c) stopWait("change");
			});
		}
	}

	tags  = responseTag.getElementsByTagName("optionPane");
	if (tags != null && tags.length > 0) {
		var tag = tags[0].getElementsByTagName("width")[0];
		var width = tag.childNodes[0].nodeValue;
		tag = tags[0].getElementsByTagName("height")[0];
		var height = tag.childNodes[0].nodeValue;
		var flows = tags[0].getElementsByTagName("flow");
		if (flows != null && flows.length > 0) {
			if (flows[0].childNodes.length > 0) {
				var flowId = flows[0].childNodes[0].nodeValue;
				var w = parseInt(width);
				var h = parseInt(height);
				var address = location.pathname + "?trg=srv&cmd=gop&id="+flowId;
				if (address.indexOf("/") > 0) address = "/" + address;
				address += "&noCache=" + (new Date).getTime();
				fr = window.open(address, 'OptionPane', 'directories=no,height='+h+',location=no,menubar=yes,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width='+w);  
				fr.moveTo(screen.availWidth/2-w/2, screen.availHeight/2-h/2);
			}
		}
	}

	tags  = responseTag.getElementsByTagName("hasErrors");
	if (tags != null && tags.length > 0) {
		var url = location.pathname + "?trg=frm&cmd=getCommitErrors";
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();

		$('#errorModal').modal({remote: url, backdrop: 'static', keyboard: true});
	}

	var changeTags = responseTag.childNodes;
	for (var ch = 0; ch < changeTags.length; ch++) {		
		var changeTag = changeTags[ch];
		var name = changeTag.nodeName.toLowerCase();
		if ("change" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;

			var obj = $('#' + id);
			tag = changeTag.getElementsByTagName("value")[0];

			if (obj != null) {
				if (obj.is('div') || obj.is('span')) {
					

					if (tag.childNodes.length > 0) {
						var val = "";
						for (var g = 0; g < tag.childNodes.length; g++) {
							var childTag = tag.childNodes[g];
							val = val + getElementXml(childTag);
						}
						obj.html(val);
						$(obj).find(".dpick").datepicker({weekStart: 1, autoclose: true, language: 'ru'});
						createFormatControl($(obj).find(".dpick"));
					    createFormatControlIntField($(obj).find("[formatting]"));
					    createWYSIWYG($(obj).find("[wysiwyg]"));
					} else {
						obj.html('');
					}
				} else {
					var val = "";
					if (tag.childNodes.length > 0) {
						val = tag.childNodes[0].nodeValue;
					}
					if (obj.is('input') || obj.is('select')) {
						if (obj.attr('type') == 'checkbox' || obj.attr('type') == 'radio') {
							if (val == "true") {
								obj.attr('checked', '1');
							} else {
								obj.removeAttr('checked');
							}
						} else {
							obj.val(val);
						}
					} else if (obj.is('button')) {
						var img = $('img',obj);
						$(obj).text(val).prepend(img);
					} else {
						obj.text(val);

					}
				}
			}
			stopWait("change");
		} else if ("changemap" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;

			var objmap = d.getElementById("im" + id);
			var objsel = d.getElementById("is" + id);

			tag = changeTag.getElementsByTagName("imgSrc")[0];

			if (objmap != null && tag.childNodes.length > 0) {
				var val = tag.childNodes[0].nodeValue;
				objmap.src = val;
			}
			tag = changeTag.getElementsByTagName("imgSel")[0];

			if (objsel != null && tag.childNodes.length > 0) {
				var val = tag.childNodes[0].nodeValue;
				objsel.src = val;
			}
		} else if ("visible" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;

			var obj = d.getElementById(id);
			tag = changeTag.getElementsByTagName("value")[0];

			if (obj != null) {
				if (obj.tagName.toLowerCase() == "div"
						|| obj.tagName.toLowerCase() == "table") {

					if (tag.childNodes.length > 0) {
						var val = "";
						for (g = 0; g < tag.childNodes.length; g++) {
							var childTag = tag.childNodes[g];
							val = val + getElementXml(childTag)
									+ "\r\n";
						}
						obj.outerHTML = val;
						
						$('#' + id).find("table.tbl tbody tr").click(function (e) {
					    	selectIfcRow(e, $(this).get(0));
				        });
						$('#' + id).find("[actionId]").click(function () {
					    	makeAction($(this));
					    });
						
						$('#' + id).find(".modifier").click(function (e) {
					    	modifyValue($(this));
					    });
						
						$('#' + id).find(".dpick").datepicker({weekStart: 1, autoclose: true, language: 'ru'});
						createFormatControl($('#' + id).find(".dpick"));
					    createFormatControlIntField($('#' + id).find("[formatting]"));
					    createWYSIWYG($('#' + id).find("[wysiwyg]"));
					} else {
						obj.innerHTML = "";
					}
				}
			}
		} else if ("newenable" == name) {
			var tag = changeTag.getElementsByTagName("actionId")[0];
			var id = tag.childNodes[0].nodeValue;
			var obj = $('[actionId = "' + id + '"]');
			tag = changeTag.getElementsByTagName("value")[0];
			
			if ("true" == tag.childNodes[0].nodeValue) {
				obj.removeAttr('disabled');
			} else {
				obj.attr('disabled', 'disabled');
			}
			

				} else if ("enable" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var obj = d.getElementById(id);
			tag = changeTag.getElementsByTagName("value")[0];

			if (obj != null) {
				var tg = obj.tagName.toLowerCase();
				if (tg == "div" || tg == "span") {
					if ($.trim(obj.className) == "nicEdit-main") {
						if (tag.childNodes.length > 0 && tag.childNodes[0].nodeValue == "false"){
							$("[id='"+$(obj).attr('id')+"_ed']").hide();
							$("[id='"+$(obj).attr('id')+"_block']").show();
						}else{
							$("[id='"+$(obj).attr('id')+"_ed']").show();
							$("[id='"+$(obj).attr('id')+"_block']").hide();
						}
					} else if (tag.childNodes.length > 0) {
						var val = "";
						for (g = 0; g < tag.childNodes.length; g++) {
							var childTag = tag.childNodes[g];
							val = val + getElementXml(childTag);
						}
						val = val.replace("<br></br>", "<br/>");
						$(obj).html(val);
						$(obj).find("[actionId]").click(function() {
							makeAction($(this));
						});
						$(obj).find(".dpick").datepicker({
							weekStart : 1,
							autoclose : true,
							language : 'ru'
						});
						createFormatControl($(obj).find(".dpick"));
						createFormatControlIntField($(obj).find("[formatting]"));
						createWYSIWYG($(obj).find("[wysiwyg]"));
					} else {
						$(obj).html('');
					}
				} else if (tg == "img") {
					var val = false;
					if (tag.childNodes.length > 0
							&& tag.childNodes[0].nodeValue == "false")
						val = true;
					if (val) {
						obj.style.opacity = 1;
						obj.style.filter = "alpha(opacity:99.99)";
					} else {
						obj.style.opacity = 0.35;
						obj.style.filter = "alpha(opacity:35)";
					}
				} else {
					var val = false;
					if (tag.childNodes.length > 0 && tag.childNodes[0].nodeValue == "false"){
						val = true;
					}
					if (tg == "textarea" || (tg == "input" && obj.getAttribute("type") == "text")) {
						obj.readonly = val;
						if (val) {
							obj.className = "readonly";
							obj.setAttribute("readOnly", "1");
						} else {
							obj.className = "";
							obj.removeAttribute("readOnly");
						}
					} else {
						obj.disabled = val;
					}
				}
			}
		} else if ("model" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var opTags = changeTag.getElementsByTagName("option");

			var obj = d.getElementById(id);
			if (obj != null) {
				var oldOps = obj.getElementsByTagName("option");
				for (var j = oldOps.length - 1; j >= 0; j--) {
					var oldOp = oldOps[j];
					obj.removeChild(oldOp);
				}

				for (var j = 0; j < opTags.length; j++) {
					var opTag = opTags[j];
					var op = d.createElement("option");
					op.setAttribute("value", opTag.getAttribute("value"));
					strVal = "";
					if (opTag.childNodes.length > 0)
						strVal = opTag.childNodes[0].nodeValue;
					var text = d.createTextNode(strVal);
					op.appendChild(text);
					obj.appendChild(op);
				}
				while (obj.tagName.toLowerCase() != "body") {
					obj = obj.parentNode;
					if (obj.tagName.toLowerCase() == "ul"
							&& obj.className == "Hidden") {
						obj.className = "";
						obj.className = "Hidden";
					}
				}
			}
		} else if ("tabledata" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var tbodyTag = changeTag.getElementsByTagName("tbody")[0];
			var tbodyClone = tbodyTag.cloneNode(true);
			var rowTags = tbodyTag.getElementsByTagName("tr");

			var obj = d.getElementById(id);
			if (obj != null) {
				var tables = obj.getElementsByTagName("table");
				var table;
				if (tables.length > 0)
					table = tables[tables.length - 1];

				var sTags = changeTag .getElementsByTagName("selectedRows");
				if (sTags != null && sTags.length > 0) {
					var selsString = sTags[0].childNodes[0].nodeValue;
					table.setAttribute("selectedRows", selsString);
				}else{
					table.removeAttribute("selectedRows");
				}

				var tbody = table.getElementsByTagName("tbody")[0];

				clearTable(tbody);

				for (j = 0; j < rowTags.length; j++) {
					var rowTag = rowTags[j];

					var row = tbody.insertRow(tbody.rows.length);
					row.id = rowTag.getAttribute("id");
					row.className = rowTag.getAttribute("class");
					row.height = rowTag.getAttribute("height");
					var st = rowTag.getAttribute("style");
					if (st != null) {
						row.style.cssText = st;
					}

					var cellTags = rowTag.getElementsByTagName("td");
					for (var k = 0; k < cellTags.length; k++) {
						var cellTag = cellTags[k];
						var cell = row.insertCell(row.cells.length);

						for (var ak = 0; ak < cellTag.attributes.length; ak++) {
							if (cellTag.attributes[ak].nodeName == "style") {
								cell.style.cssText = cellTag.attributes[ak].nodeValue;
							} else if (cellTag.attributes[ak].nodeName == "onclick") {
								var tdOnClick = cellTag
										.getAttribute("onclick");
								if (tdOnClick.indexOf("getCellEditor") > -1) {
									cell.onclick = function() {
										eval("getCellEditor(this);");
									};
								} else if (tdOnClick.indexOf("popupPressed") > -1) {
									cell.onclick = function() {
										eval("popupPressed(this);");
									};
								} else if (tdOnClick.indexOf("docFieldPressed") > -1) {
									cell.onclick = function() {
										eval("docFieldPressed(this);");
										return false;
									};
								} else if (tdOnClick.indexOf("loadImage2") > -1) {
									cell.onclick = function() {
										eval("loadImage2(this);");
										return false;
									};
								}
							} else if (cellTag.attributes[ak].nodeName == "colspan") {
								cell.colSpan = cellTag.attributes[ak].nodeValue;
							} else {
								cell[cellTag.attributes[ak].nodeName] = cellTag.attributes[ak].nodeValue;
							}
						}
						var children = cellTag.childNodes;
						var n = 0;
						var resHTML = "";
						for (n = 0; n < children.length; n++) {
							var child = children[n];
							resHTML += getElementXml(child);
						}
						cell.innerHTML = resHTML;
					}
				}
				
			    $(tbody).find("tr").click(function (e) {
			    	selectIfcRow(e, $(this).get(0));
		        });
			}
		} else if ("deleted" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var table = $('#tbl' + id);
			var rows = changeTag.getElementsByTagName("tr");
			for (var i = 0; i < rows.length; i++) {
				var rowTag = rows[i];
				var rowId = rowTag.getAttribute("id");
				var row = table.find('tbody tr').get(rowId);
				$(row).remove();
			}
		} else if ("inserted" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var obj = d.getElementById(id);
			var tables = obj.getElementsByTagName("table");
			var table;
			if (tables.length > 0)
				table = tables[tables.length - 1];
			var rows = changeTag.getElementsByTagName("tr");
			var tbody = table.getElementsByTagName('tbody')[0];
			for (i = 0; i < rows.length; i++) {
				var rowTag = rows[i];
				var rowId = rowTag.getAttribute("id");
				var row = d.createElement("TR");
				row.id = rowId;
				row.className = rowTag.getAttribute("class");
				row.height = rowTag.getAttribute("height");
				var st = rowTag.getAttribute("style");
				if (st != null) {
					row.style.cssText = st;
				}

				var tdTags = rowTag.childNodes;

				for (j = 0; j < tdTags.length; j++) {
					var tdTag = tdTags[j];
					var td = d.createElement("TD");
					for (k = 0; k < tdTag.attributes.length; k++) {
						if (tdTag.attributes[k].nodeName == "style") {
							td.style.cssText = tdTag.attributes[k].nodeValue;
						} else if (tdTag.attributes[k].nodeName == "onclick") {
							var tdOnClick = tdTag
									.getAttribute("onclick");
							if (tdOnClick.indexOf("getCellEditor") > -1) {
								td.onclick = function() {
									eval("getCellEditor(this);");
								};
							} else if (tdOnClick.indexOf("popupPressed") > -1) {
								td.onclick = function() {
									eval("popupPressed(this);");
								};
							} else if (tdOnClick.indexOf("docFieldPressed") > -1) {
								td.onclick = function() {
									eval("docFieldPressed(this);");
									return false;
								};
							} else if (tdOnClick.indexOf("loadImage2") > -1) {
								td.onclick = function() {
									eval("loadImage2(this);");
									return false;
								};
							}
						} else if (tdTag.attributes[k].nodeName == "colspan") {
							td.colSpan = tdTag.attributes[k].nodeValue;
						} else {
							td[tdTag.attributes[k].nodeName] = tdTag.attributes[k].nodeValue;
						}
					}
					var children = tdTag.childNodes;
					var n = 0;
					var resHTML = "";
					for (n = 0; n < children.length; n++) {
						var child = children[n];
						resHTML += getElementXml(child);
					}
					td.innerHTML = resHTML;
					row.appendChild(td);
				}
				tbody.appendChild(row);
			    $(row).click(function (e) {
			    	selectIfcRow(e, $(this).get(0));
		        });
			}
			//stopWait();
		} else if ("updated" == name) {
			var tag = changeTag.getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;
			var table = $('#tbl' + id).get(0);
			
			if (table != null) {
				var rows = changeTag.getElementsByTagName("tr");
				var tbody = table.getElementsByTagName('tbody')[0];

				for (var i = 0; i < rows.length; i++) {
					var rowTag = rows[i];
					var rowId = rowTag.getAttribute("id");
					var row = getRowById(tbody, rowId);
					var tdTags = rowTag.childNodes;
					if (row != null) {
						var j1 = 0;
						for (j = 0; j < tdTags.length; j++) {
							var tdTag = tdTags[j];
							if (tdTag.nodeType == 1) {
								var td = row.cells[j1];
								j1++;
								for (k = 0; k < tdTag.attributes.length; k++) {
									if (tdTag.attributes[k].nodeName == "style") {
										td.style.cssText = tdTag.attributes[k].nodeValue;
									} else if (tdTag.attributes[k].nodeName == "onclick") {
										var tdOnClick = tdTag
												.getAttribute("onclick");
										if (tdOnClick.indexOf("getCellEditor") > -1) {
											td.onclick = function() {
												eval("getCellEditor(this);");
											};
										} else if (tdOnClick.indexOf("popupPressed") > -1) {
											td.onclick = function() {
												eval("popupPressed(this);");
											};
										} else if (tdOnClick.indexOf("docFieldPressed") > -1) {
											td.onclick = function() {
												eval("docFieldPressed(this);");
												return false;
											};
										} else if (tdOnClick.indexOf("loadImage2") > -1) {
											td.onclick = function() {
												eval("loadImage2(this);");
												return false;
											};
										}
									} else if (tdTag.attributes[k].nodeName == "colspan") {
										td.colSpan = tdTag.attributes[k].nodeValue;
									} else {
										td[tdTag.attributes[k].nodeName] = tdTag.attributes[k].nodeValue;
									}
								}
								var children = tdTag.childNodes;
								var n = 0;
								var resHTML = "";
								for (n = 0; n < children.length; n++) {
									var child = children[n];
									resHTML += getElementXml(child);
								}
								td.innerHTML = resHTML;
							}
						}
					}
				}
			}
			//stopWait();
		} else if ("openfile" == name) {
			stopWait();
			var fn = changeTag.childNodes[0].nodeValue;
			if (fn != null && fn.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();
				location.assign(url);
			}
		} else if ("viewfile" == name) {
			var children = changeTag.childNodes;
			var fn = "";
			for (var n = 0; n < children.length; n++) {
				var child = children[n];
				fn += getElementXml(child);
			}
			if (fn != null && fn.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();
				fr = window.open(url);
			}
		} else if ("editfile" == name) {// действие аналогично openfile, в будущем моежт переработаем
			var children = changeTag.childNodes;
			var fn = "";
			for (var n = 0; n < children.length; n++) {
				var child = children[n];
				fn += getElementXml(child);
			}
			if (fn != null && fn.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();
				fr = window.open(url);
			}
		} else if ("getecpparams" == name) {
			document.getElementById('ECPApplet').getECPParams();
			
			var post = "xml=1&trg=frm&cmd=operationres&res=1";
	  		var req2 = createAsync();
			var func = "";
			sendAsync(req2, post, func, true);
		} else if ("clearecpparams" == name) {
			document.getElementById('ECPApplet').clearECPParams();
			
			var post = "xml=1&trg=frm&cmd=operationres&res=1";
	  		var req2 = createAsync();
			var func = "";
			sendAsync(req2, post, func, true);
		} else if ("getlasterror" == name) {
			var par = document.getElementById('ECPApplet').getLastError();
			
			var post = "xml=1&trg=frm&cmd=operationres&res=" + par;
	  		var req2 = createAsync();
			var func = "";
			sendAsync(req2, post, func, true);
		} else if ("signdata" == name) {
			var tag = changeTag.getElementsByTagName("param")[0];
			
			var res =  "";

			var children = tag.childNodes;
			var i = 0;
			for (i = 0; i < children.length; i++) {
				var child = children[i];
				res += child.nodeValue;
			}
			
			var par = document.getElementById('ECPApplet').sign(res);
			
			var post = "xml=1&trg=frm&cmd=operationres&res=" + par;
	  		var req2 = createAsync();
			var func = "";
			sendAsync(req2, post, func, true);
		} else if ("sign" == name) {
			var xmlToSign = changeTag.childNodes[0].childNodes[0].nodeValue;
			var profile = changeTag.childNodes[1].childNodes[0].nodeValue;

			//var password = window.showModalDialog("/ekyzmet-ui/pwd.html", "pwd", "dialogWidth:320px;dialogHeight:220px");

			disableWholeWindow(currentLang=="kz"?"Электронды қолтаңбаның жасалуы...":"Формирование электронной подписи...", false);

			var signed = d.app.signString(xmlToSign, "", profile, "", "1.3.6.1.4.1.6801.1.5.8", true);

			if (signed != null && signed.length > 0) {
				var post = "xml=1&trg=frm&cmd=prvas&signed=" + encodeURIComponent(signed);
				
				var reqIn = createAsync();
				var func = "processAfterSign";
				sendAsync(reqIn, post, func, false);
			} else {
				alert(d.app.getLastError());
				enableWholeWindow(true);
			}
		} else if ("getcertificate" == name) {
			var par = document.getElementById('ECPApplet').getCertificate();
			
			var post = "xml=1&trg=frm&cmd=operationres&res=" + par;
	  		var req2 = createAsync();
			var func = "";
			sendAsync(req2, post, func, true);
		} else if ("close" == name) {
			var post = "xml=1&trg=frm&cmd=prvas";
			pleaseWait(currentLang=="kz"?"Электронды қолтаңбаның жасалуы...":"Формирование электронной подписи...");
	
			var reqIn = createAsync();
			var func = "processAfterSign";
			sendAsync(reqIn, post, func, false);
		} else if ("notification" == name) {
			var tag = changeTag.getElementsByTagName("code")[0];
			var code = tag.childNodes[0].nodeValue;
			if (code == "1") {
				var tagTitle = changeTag.getElementsByTagName("title")[0];
				if (tagTitle == null) {
					showNotification('Вам пришло сообщение!');
				} else {
					showNotification(tagTitle.childNodes[0].nodeValue);
				}
			}
		}
	}
	enableWholeWindow(true);
}

function openUI(flowId) {
	var url = location.pathname + "?trg=frm&cmd=opn&id=" + flowId;
	if (url.indexOf("/") > 0) url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	pleaseWait(currentLang=="kz"?"Интерфейстің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие интерфейса...");
	location.assign(url);
}

function openPrev() {
	var url = location.pathname + "?trg=frm";
	if (url.indexOf("/") > 0) url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	pleaseWait(currentLang=="kz"?"Интерфейстің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие интерфейса...");
	location.assign(url);
}

function processAfterSign(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("ok");
			if (tags != null && tags.length > 0) {
				alert(currentLang=="kz"?"Деректер жазылды және кейнгі өңделуге берілді!":"Данные успешно подписаны и переданы на дальнейшую обработку!");
				disableWholeWindow("Процесс закончен. Можно выйти из программы...", false);
				exit("about:blank");

				if (parent)
					parent.close();
				return;
			}
		}
	}
}


function openDialog(curFrameId, title, w, h) {
	var url = location.pathname + "?trg=frm&cmd=openDialog";
	if (url.indexOf("/") > 0){
		url = "/" + url;
	}
	url += "&noCache=" + (new Date).getTime();
	
    var trg = $('#modal' + curFrameId);
    $('#dhead' + curFrameId).text(title);
    var headerH = $("div[class=modal-header]").outerHeight();
    var footerH = $("div[class=modal-footer]").outerHeight();
    
    var winH = $(window).height();
    var realH = h + headerH + footerH;
    if  (realH > winH){
    	realH = winH;
    }
    var bodyH = realH-headerH-footerH-87;
    trg.css({"margin-left": -w/2, "margin-top": -realH/2});
    trg.css({"width": w+10, "height": realH});
	trg.modal({remote: url, backdrop: 'static', keyboard: true});
	timedFindDIV(0,bodyH);
}


function timedFindDIV(c, bodyH) {
	var bodyM = $("div[id='modalBody']");
	if ($(bodyM).length > 0) {
		$(bodyM).height(bodyH);
	} else if (c < 20) {
		setTimeout('timedFindDIV('+c+'+1,'+bodyH+')', 500);
	}
	// В открываемом диалоге повесить на поля ввода даты календарики
	$(bodyM).find(".dpick").datepicker({ weekStart : 1, autoclose : true, language : 'ru' });
	createFormatControl($(bodyM).find(".dpick"));
    createFormatControlIntField($(bodyM).find("[formatting]"));
    createWYSIWYG($(bodyM).find("[wysiwyg]"));
}

function changePassword() {
	var url = location.pathname + "?trg=chp";
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	
    $('#chpModal').modal({remote: url, backdrop: 'static', keyboard: true});
}

function makeAction(node) {
	var actionId = node.attr('actionId');
	
	var post = "xml=1&trg=frm&cmd=sta&id=" + actionId;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func);
}

function makeChange(node) {
	var changeId = $(node).attr('changeId');
	var val = $(node).attr("checked");
	val = val=="checked"?"1":"0";
	var curChange = changeId + "_" + val;
	if (lastChange != curChange) {
		pleaseWait(null, "change");
		lastChange = curChange;
		var post = "xml=1&trg=frm&cmd=chg&id=" + changeId + "&val=" + val;
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func);
	}
}


function modifyValue(obj) {
	var id = obj.attr('data-id');
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val="+obj.attr('data-value');
	
	var row = obj.attr('data-row');
	var col = obj.attr('data-col');
	
	if (row != null && col != null) {
		post += "&row="+row+"&col="+col;
	}
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);

	var wnd = null;
	if (col != null) {
		wnd = $('#modal' + id + '_' + col);
	} else {
		wnd = $('#modal' + id);
	}

	wnd.modal('hide');
	wnd.data('modal', null);
}


function disableWholeWindow(msg, permanently, t) {
	var w = window;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently, t);
	}
}

function enableWindowResponse() {
	stopWait();
}

function enableWholeWindow(perform) {
	var w = window;
	if (w != null && w.disableWindow) {
		w.enableWindow(perform);
	}
}

function removeSelection(tbody, col) {
	for (var i=1; i < tbody.rows.length; i++) {
		tbody.rows[i].className = "notselected";
		if (col > -1) tbody.rows[i].cells[col].className = "";
	}
}

function getLastStartedId() {
	return lastStartedId;
}

function enableButton(buttonId, enable, backPage, ocm) {
	var aobj = $('#' + buttonId);
	
	if (aobj != null && aobj.size() > 0) {
		if (enable) {
			aobj.parent().removeClass('disabled');
			if ("print" == buttonId) {
				aobj.get(0).onclick = function() {
					eval("showPrintMenu();");
				};
			} else if ("com" == buttonId) {
				aobj.get(0).onclick = function() {
					eval("commit();");
				};
			} else if ("rlb" == buttonId) {
				aobj.get(0).onclick = function() {
					eval("rollback();");
				};
			} else if ("run" == buttonId) {
				aobj.get(0).onclick = function() {
					eval("next();");
				};
			} else if ("prev" == buttonId) {
				aobj.get(0).onclick = function() {
					eval("previous();");
				};
			}
		}
		else {
			if (backPage != null && backPage.length > 0) {
				if (ocm != null && ocm.length > 0) {
					aobj.get(0).onclick = function() {
						eval(ocm);
						exit(backPage);
					};
				} else {
					aobj.get(0).onclick = function() {
						exit(backPage);
					};
				}
				aobj.parent().removeClass('disabled');
			} else {
				aobj.parent().addClass('disabled');
				aobj.get(0).onclick = "";
			}
		}
	}
}

function disableButton(buttonId) {
	var aobj = $('#' + buttonId);
	if (aobj != null) {
		aobj.parent().addClass('disabled');
	}
}

function exit(url) {
	var post = "xml=1&trg=frm&cmd=ext";
	var req = createAsync();
	var func = "afterExit";
	exitUrl = url;
	sendAsync(req, post, func, true);
}

function afterExit(responseTag) {
	location.href = exitUrl;
}

function getElementXml(node) {
	if (node.nodeType == 3) {
		return node.nodeValue;
	} else if (node.xml) {
		return node.xml;
	} else if (node.nodeType == 1) {
		var res =  "<" + node.nodeName;
		var ak = 0;
		for (ak = 0; ak < node.attributes.length; ak++) {
			var attr = node.attributes[ak];
			res += " " + attr.nodeName + "=\"" + xmlQuote2(attr.nodeValue) + "\"";
		}
		res += ">";
		var children = node.childNodes;
		var i = 0;
		for (i = 0; i < children.length; i++) {
			var child = children[i];
			if (child.nodeType == 1) {
				res += getElementXml(child);
			} else if (child.nodeType == 3) {
				res += child.nodeValue;
			}
		}
		res +=  "</" + node.nodeName + ">";
		return res;
	}
	return "";
}

function selectRadio(radio) {
	$('input[id='+radio+']').prop('checked', true);
	$('input[id='+radio+']').change();
}

function selectCheck(check) {
	var elem = $('input[id='+check+']');
	$(elem).prop('checked', !($(elem)).prop("checked"));
	$('input[id='+check+']').change();
}


function clickOKforModalFrame(element) {
}


/* from processes.js */
function expand(nodeId) {
	var d = document;

	var ul = d.getElementById("ul" + nodeId);
	var img = d.getElementById("img" + nodeId);
	var waitAnswer = false;
	
	if(ul != null && ul.className != null && ul.innerHTML != null) {
    
		if (ul.className == "Hidden") {
			ul.className = "Shown";
			img.src = "images/Open.gif";

			var innerStr = trim(ul.innerHTML);
			if(innerStr == ""){
				ul.innerHTML="<DIV CLASS='loadMsg'>&nbsp;Подождите идет загрузка...&nbsp;</DIV>";
				waitAnswer = true;
			}
		} else {
			ul.className = "Hidden";
			img.src = "images/CloseFolder.gif";
		}

		var post = "xml=1&trg=srv&cmd=exp&id=" + nodeId;
		if (!waitAnswer) {
			post += "&wait=no";
		}
		var req = createAsync();
		var func = "processNode";
		sendAsync(req, post, func);
	}
}

function askStartProcess(nodeId, msg) {
	var li = document.getElementById("li" + nodeId);
  
	if(li != null && li.innerHTML != null) {
    
		var aTag = li.childNodes[0];
		var textNode = aTag.childNodes[1];
		var c = (!askConfirm) ? true : confirm(msg + ": '" + textNode.nodeValue + "'?");
    
		if (c) {
			var post = "xml=1&trg=srv&cmd=crp&id=" + nodeId;
			var req = createAsync();
			var func = "startProcessResponse";
			sendAsync(req, post, func);
		}
	}
}

function processNode(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTags = req.responseXML.getElementsByTagName("r");
			var tag = responseTags[0].getElementsByTagName("id")[0];
			var id = tag.childNodes[0].nodeValue;

			var ul = document.getElementById("ul" + id);

			var dataTag = responseTags[0].getElementsByTagName("data")[0];
			var liTags = dataTag.childNodes;

			var str = "";
			var i = 0;
			for (i = 0; i < liTags.length; i++) {
				var liTag = liTags[i];
            	str = str + getElementXml(liTag) + "\r\n";
          	}
			ul.innerHTML = str;
		}o
	}
}
/* END from processes.js */

/* from langs.js */
function setInterfaceLang(langId) {
	pleaseWait(currentLang=="kz"?"Интерфейс  тілінің өзгерілуі...":"Изменение языка интерфейса...");
	var url = location.pathname + "?trg=top&cmd=lng&val=" + langId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	location.assign(url);
}

function setDataLang(langId) {
	pleaseWait(currentLang=="kz"?"Деректер тілінің өзгеруі...":"Изменение языка данных...");
	var url = location.pathname + "?trg=top&cmd=dlng&val=" + langId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	location.assign(url);
}
/* END from langs.js */

function redirectToReport(responseTag) {
	var changeTags = responseTag.childNodes;
	for (var ch = 0; ch < changeTags.length; ch++) {
		var changeTag = changeTags[ch];
		var name = changeTag.nodeName.toLowerCase();
		if ("viewfile" == name) {
			var fn = changeTag.childNodes[0].nodeValue;
			if (fn != null && fn.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();
				fr = window.open(url);
			}
		}
	}
	stopWait();
}

/**
 * Вывод уведомления
 * 
 * Код работает в Chome
 * для Firefox необходимо установить дополнение
 * https://addons.mozilla.org/en-US/firefox/addon/html-notifications
 * */
function showNotification(note) {
	var text = 'Сообщение системы OR3.';
	// уведомление показывать только если дано разрешение
	var notification = null;
	if (window.webkitNotifications.checkPermission() != 0) {
		window.webkitNotifications.requestPermission();
	}

	if (window.webkitNotifications.checkPermission() === 0) {
		notification = window.webkitNotifications.createNotification(
				'images/info.png', note, text);
		notification.ondisplay = function(event) {
			setTimeout(function() {
				event.currentTarget.cancel();
			}, 10000);
		};

		notification.onclick = function() {
			window.focus();
			this.cancel();
		};

		// показать уведомление
		notification.show();
	}
}

function processRollback(rt) {
	rollbackResponse(rt);
	stopWait();
}


function dateFormat(str, format) {
	var p0 = "01";
	var p1 = "012";
	var p2 = "0123";
	var p3 = "012345";
	var p4 = "0123456789";
	var arrStr = str.split("");
	var k = 0;
	var j = 0;
	if (format == 'HH:mm:ss'||format == 'HH:mm') {
		j = 11;
	}
	if (format != 'HH:mm:ss' && format != 'HH:mm') {
		// dd.mm.yyyy
		if (arrStr[0] != null) {
			if (p2.indexOf(arrStr[0]) == -1) {
				arrStr[0] = '3';
			}
		}
		if (arrStr[1] != null) {
			if (arrStr[0] == '3') {
				if (p0.indexOf(arrStr[1]) == -1) {
					arrStr[1] = '1';
				}
			}
		}
		if (arrStr[2] != null) {
			if (arrStr[2] != '.') {
				arrStr[2] = '.';
			}
		} else {
			if (arrStr[1] != null) {
				arrStr.push('.');
				k++;
			}
		}
		if (arrStr[3] != null) {
			if (p0.indexOf(arrStr[3]) == -1) {
				arrStr[3] = '1';
			}
		}
		if (arrStr[4] != null) {
			if (arrStr[3] == '1') {
				if (p1.indexOf(arrStr[4]) == -1) {
					arrStr[4] = '2';
				}
			}
		}
		if (arrStr[5] != null) {
			if (arrStr[5] != '.') {
				arrStr[5] = '.';
			}
		} else {
			if (arrStr[4] != null) {
				arrStr.push('.');
				k++;
			}
		}
		if (arrStr[6] != null) {
			if (p4.indexOf(arrStr[6]) == -1) {
				arrStr[6] = '9';
			}
		}
		if (arrStr[7] != null) {
			if (p4.indexOf(arrStr[7]) == -1) {
				arrStr[7] = '9';
			}
		}
		if (arrStr[8] != null) {
			if (p4.indexOf(arrStr[8]) == -1) {
				arrStr[8] = '9';
			}
		}
		if (arrStr[9] != null) {
			if (p4.indexOf(arrStr[9]) == -1) {
				arrStr[9] = '9';
			}
		}
		// dd.MM.yyyy HH:mm
		if (arrStr[10] != null) {
			if (arrStr[10] != ' ') {
				arrStr[10] = ' ';
			}
		} else {
			if (arrStr[9] != null) {
				arrStr.push(' ');
				k++;
			}
		}
	}
	if (arrStr[11 - j] != null) {
		if (p1.indexOf(arrStr[11 - j]) == -1) {
			arrStr[11 - j] = '2';
		}
	}
	if (arrStr[12 - j] != null) {
		if (arrStr[11 - j] == '2') {
			if (p2.indexOf(arrStr[12 - j]) == -1) {
				arrStr[12 - j] = '3';
			}
		}
	}
	if (arrStr[13 - j] != null) {
		if (arrStr[13 - j] != ':') {
			arrStr[13 - j] = ':';
		}
	} else {
		if (arrStr[12 - j] != null) {
			arrStr.push(':');
			k++;
		}
	}
	if (arrStr[14 - j] != null) {
		if (p3.indexOf(arrStr[14 - j]) == -1) {
			arrStr[14 - j] = '5';
		}
	}
	if (arrStr[15 - j] != null) {
		if (p4.indexOf(arrStr[15 - j]) == -1) {
			arrStr[15 - j] = '9';
		}
	}
	// dd.MM.yyyy HH:mm:ss
	if (arrStr[16 - j] != null) {
		if (arrStr[16 - j] != ':') {
			arrStr[16 - j] = ':';
		}
	} else {
		if (arrStr[15 - j] != null) {
			arrStr.push(':');
			k++;
		}
	}
	if (arrStr[17 - j] != null) {
		if (p3.indexOf(arrStr[17 - j]) == -1) {
			arrStr[17 - j] = '5';
		}
	}
	if (arrStr[18 - j] != null) {
		if (p4.indexOf(arrStr[18 - j]) == -1) {
			arrStr[18 - j] = '9';
		}
	}
	// dd.MM.yyyy HH:mm:ss:SSS
	if (format != 'HH:mm:ss') {
		if (arrStr[19] != null) {
			if (arrStr[19] != ':') {
				arrStr[19] = ':';
			}
		} else {
			if (arrStr[18] != null) {
				arrStr.push(':');
				k++;
			}
		}
		if (arrStr[20] != null) {
			if (p4.indexOf(arrStr[20]) == -1) {
				arrStr[20] = '9';
			}
		}
		if (arrStr[21] != null) {
			if (p4.indexOf(arrStr[21]) == -1) {
				arrStr[21] = '9';
			}
		}
		if (arrStr[22] != null) {
			if (p4.indexOf(arrStr[22]) == -1) {
				arrStr[22] = '9';
			}
		}
	}

	var ln = format.length;
	if ((str.length + k) < ln) {
		ln = str.length + k;
	}

	return arrStr.join("").substring(0, ln);
}


function createFormatControl(elements) {
	var onchange_callback = function(e) {
		$(elements).text(e);
	}
	/*
	 * @e.keyCode: 
	 * 9 - Tab 
	 * 8 - Backspace 
	 * 46 - Del 
	 * 48-57 - 0-9 
	 * 13 - Enter
	 * 37-40 - стрелки: left,top,right,bottom 
	 * 35 -Home 
	 * 36 -End
	 * 96-105 numpad 0 - 9
	 * 190 точка
	 * 32 пробел
	 * 58 двоеточие
	 */
	$(elements).bind(
			"keydown",
			function(e) {// Контроль ввода с клавиатуры
				var $this = $(this);
				return myFormatDate($(this), e);
/*				// получить формат поля
				var format = $this.attr("data-date-format");
				var keyCodeArray = [ 8, 9, 13, 46, 190 ];
				if (format.indexOf(" ") != -1) {
					keyCodeArray.unshift(32);
				}
				if (format.indexOf(":") != -1) {
					keyCodeArray.unshift(58);
				}
				var code = e.keyCode;
				return (code > 95 && code < 106) || (code > 47 && code < 58)
						|| keyCodeArray.indexOf(code) != -1
						|| (code > 34 && code < 41);
*/			});
/*	.bind("textchange", function(event, previousText) {// Контроль
																	// изменения
																	// текста
				
				myFormatDate($(this), previousText);
				
		var $this = $(this);
		var p = $this.caret();
		// получить формат поля
		var format = $this.attr("data-date-format");
		var origStr;
		if (format.indexOf(" ") != -1) {
			origStr = $this.val().replace(/[^0-9\.\: ]/g, "");
		} else if (format.indexOf(" ") == -1 && format.indexOf(":") != -1) {
			origStr = $this.val().replace(/[^0-9\:]+/g, "");
		} else {
			origStr = $this.val().replace(/[^0-9\.]+/g, "");
		}
		var str;
		str = dateFormat(origStr, format);
		if (previousText.length <= origStr.length) {
				p++;
		} else {
			str= origStr;
		}

		if (previousText != str) {
			$this.val(str);
			onchange_callback(str);
			$this.caret(p);
		}

	});
*/}

function myFormatDate (obj, e) {
	var code = e.keyCode; 
	if (code == 27) return true;
	
	if (code == 13 || code == 9) {
//		if (obj.attr("readonly") == null)
//			obj.change();
		return true;
	}

	if (obj == null || obj.attr("readonly") != null) return false;

	if (e.altKey) {
		var d = obj.caret();
		if (code == 37 && d > 0) {
			obj.caret(d-1);
		} else if (code == 39 && d < obj.val().length) {
			obj.caret(d+1);
		}
		e.cancelBubble = true;
		e.returnValue = false;

		if (e.stopPropagation) {
			e.stopPropagation();
		}
		e.preventDefault();
		return false;
	}
	
	if (code >= 35 && code <= 40) return true;

	var str = obj.val();
	
	if (str.length == 10) {
		var d = obj.caret();

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<10) {
			if (d==2 || d==5) d++;

			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4) d++;

			obj.val(str);
			obj.caret(d+1);
		}

		if (code==8 && d>0) {
			if (d==3 || d==6) d--;
			var f = currentLang == 'ru' ? "г" : "ж";
			if (d<3) f = currentLang == 'ru' ? "д" : "к";
			else if (d<6) f = currentLang == 'ru' ? "м" : "а";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7) d--;

			obj.val(str);
			obj.caret(d-1);
		}
	} else if (str.length == 16) {
		var d = obj.caret();

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<16) {
			if (d==2 || d==5 || d == 10 || d == 13) d++;
			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4 || d == 9 || d == 12) d++;
			obj.val(str);
			obj.caret(d+1);
		}

		if (code==8 && d>0) {
			if (d==3 || d==6 || d == 11 || d == 14) d--;
			var f = "М";
			if (d<3) f = currentLang == 'ru' ? "д" : "к";
			else if (d<6) f = currentLang == 'ru' ? "м" : "а";
			else if (d<11) f = currentLang == 'ru' ? "г" : "ж";
			else if (d<14) f= currentLang == 'ru' ? "ч" : "с";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7 || d == 12 || d == 15) d--;

			obj.val(str);
			obj.caret(d-1);
		}
	} else if (str.length == 8) {
		var d = obj.caret();

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<8) {
			if (d==2 || d==5) d++;

			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4) d++;

			obj.val(str);
			obj.caret(d+1);
		}

		if (code==8 && d>0) {
			if (d==3 || d==6) d--;
			var f = "с";
			if (d<3) f = currentLang == 'ru' ? "ч" : "с";
			else if (d<6) f = "М";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7) d--;

			obj.val(str);
			obj.caret(d-1);
		}
	}
	e.cancelBubble = true;
	e.returnValue = false;

	if (e.stopPropagation) {
		e.stopPropagation();
	}
	e.preventDefault();
	return false;
}

function numberFormat(str) {
	var str_array = [], length = str.length;
	var undefined2string = function(val) {
		return val ? val : "";
	}
	for ( var i = length - 1; i >= 0; i -= 3) {
		str_array.unshift(undefined2string(str[i - 2])
				+ undefined2string(str[i - 1]) + undefined2string(str[i]));
	}
	var resStr = str_array.join(" ");
	return resStr;
}

function createFormatControlIntField(elements) {
	var onchange_callback = function(e) {
		$(elements).text(e);
	}
	/*
	 * @e.keyCode: 9 - Tab 8 - Backspace 46 - Del 48-57 - 0-9 13 - Enter 37-40 -
	 * стрелки: left,top,right,bottom 35 -Home 36 -End 96-105 numpad 0 - 9 32
	 * пробел
	 */
	$(elements).bind(
			"keydown",
			function(e) {// контролируем ввод с клавиатуры
				var keyCodeArray = [ 8, 9, 13, 46, 32 ];
				return (e.keyCode > 95 && e.keyCode < 106)
						|| (e.keyCode > 47 && e.keyCode < 58)
						|| keyCodeArray.indexOf(e.keyCode) != -1
						|| (e.keyCode > 34 && e.keyCode < 41);
			}).bind("textchange", function(event, previousText) {// Контролируем
																	// изменение
																	// текста

		var $this = $(this);
		var p = $this.caret();
		var origStr = $this.val().replace(/[^0-9]/g, "");
		var str = numberFormat(origStr);

		if (previousText.length <= origStr.length) {
			p++;
		} else {
			p--;
		}

		if (previousText != str) {
			$this.val(str);
			onchange_callback(str);
			$this.caret(p);
		}
	});
}

function createWYSIWYG(elements) {
	for (var i = 0; i < elements.length; i++) {
		new nicEditor().panelInstance(elements[i]); 
	}
}
