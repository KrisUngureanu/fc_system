var pingLost = 0;
var timeout4 = null;
var timeoutFunction4;

function sendAsyncHtml(req, post, fr) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		//req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() { processAsyncHtmlAnswer(req, post, fr); };
		req.send(post);
	}
}

function retryNow() {
	if (timeout4 != null) {
		clearTimeout(timeout4);
		timeout4 = setTimeout(timeoutFunction4, 100);
	}
}

function processAsyncHtmlAnswer(req, post, fr)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			pingLost = 0;
			var t = req.responseText;
			fr.document.write(t);
			fr.document.close();
		} else {
			pingLost++;
			var t = pingLost * 5;
			fr.document.write("Проблемы со связью. Повторная попытка подключения через " + t + " секунд.");
			fr.document.close();
			var f = function() { sendAsyncHtml(req, post, fr); };
	 		setTimeout(f, t * 1000);
		}
	}
}

function commit() {
	pleaseWait(currentLang=="kz"?"Енгізілген деректердің сақталуы...":"Сохранение введенных данных...");
	var post = "xml=1&trg=frm&cmd=com";
	var req = createAsync();
	var func = "processCommit";
	sendAsync(req, post, func, false);
}

function dcommit() {
	var post = "xml=1&trg=frm&cmd=com";
	var req = createAsync();
	var func = "processDCommit";
	sendAsync(req, post, func, true);
}

function acommit() {
	var post = "xml=1&trg=frm&cmd=com";
	var req = createAsync();
	var func = "processACommit";
	sendAsync(req, post, func, true);
}

function prevcommit() {
	pleaseWait(currentLang=="kz"?"Енгізілген деректердің сақталуы...":"Сохранение введенных данных...");

	var post = "xml=1&trg=frm&cmd=prevcom";
	var req = createAsync();
	var func = "processPrevious";
	sendAsync(req, post, func, false);
}

function disableWholeWindow(msg, permanently, t) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently, t);
	}
}

function enableWholeWindow(perform) {
	var w = parent;
	if (w != null && w.enableWindow) {
		w.enableWindow(perform);
	}
}

function hideRetryButton() {
	var w = parent;
	if (w != null && w.hideRetry) {
		w.hideRetry();
	}
}

function cancelProgram() {
	var post = "xml=1&trg=frm&cmd=ext";
	var req = createAsync();
	var func = "processCancel";
	sendAsync(req, post, func, true);
}

function processCommit(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			stopWait();
		}
	}
}

function windowClose() {
	opener.stopWait();
	window.close();
}

function processDCommit(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			opener.window.returnValue = "YES";
			opener.window.close();
			window.close();
		}
	}
}

function processACommit(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
		}
	}
}

function processCancel(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			parent.window.close();
		}
	}
}

// Remove existing table rows
function clearTable(tbody) {
	while (tbody.rows.length > 0) {
		tbody.deleteRow(0);
	}
}

function getRowById(tbody, rowId) {
	for (n = 0; n < tbody.rows.length; n++) {
		if (tbody.rows[n].id == rowId) {
			row = tbody.rows[n];
			return row;
		}
	}
}

function canCommit() {
	disableWholeWindow(currentLang=="kz"?"Деректерді толтыру дұрыстығын тексеру...":"Проверка правильности заполнения данных...", false);
	var post = "trg=frm&cmd=ccm";
	var fr = window.open('', 'CommitPopupFrame',
					'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');
	fr.moveTo(screen.availWidth / 2 - 310, screen.availHeight / 2 - 225);

	var req = createAsync();
	sendAsyncHtml(req, post, fr);
	
	disableWholeWindow(currentLang=="kz"?"Оқшауды алу үшін қателер терезесінде әрекетті таңдаңыз!": "Для снятия блокировки, выберите действие в окне ошибок!", false);
}

function previousWithCommit() {
	alert('commit2');
	makeNormalCursor();
	disableWholeWindow(currentLang=="kz"?"Деректерді толтыру дұрыстығын тексеру...":"Проверка правильности заполнения данных...", false);
	disableButton("prev");
	var post = "xml=1&trg=frm&cmd=prvwcc";
	var req = createAsync();
	var func = "processPrevious";
	sendAsync(req, post, func, false);
}

function signAndSend() {
	disableWholeWindow(currentLang=="kz"?"Деректерді толтыру дұрыстығын тексеру...":"Проверка правильности заполнения данных...", false);
	disableButton("prev");
	var post = "xml=1&trg=frm&cmd=prvws";
	var req = createAsync();
	var func = "processSign";
	sendAsync(req, post, func, false);
}

function doAfterLoad() {
	if (parent.srv.doAfterLoad)
		parent.srv.doAfterLoad();
}

function processPrevious(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("state");
			if (tags != null && tags.length > 0) {
				var d = parent.srv.document;

				for (k = 0; k < tags.length; k++) {
					var changeTag = tags[k];

					var tag = changeTag.getElementsByTagName("id")[0];
					var id = tag.childNodes[0].nodeValue;
					var obj = d.getElementById(id);

					tag = changeTag.getElementsByTagName("value")[0];
					var state = tag.childNodes[0].nodeValue;

//					if (obj != null) {
//						if (state == "1") {
//							obj.style.backgroundColor = "#FFCCCC";
//						} else if (state == "2") {
//							obj.style.backgroundColor = "#CAF7BB";
//						} else {
//							obj.style.backgroundColor = null;
//						}
//					}
					setErrorType(obj, (state == 1) ? 0 : state); //TODO app.js
				}
			}

			tags = responseTag.getElementsByTagName("state2");
			if (tags != null && tags.length > 0) {
				var d = parent.srv.document;

				for (k = 0; k < tags.length; k++) {
					var changeTag = tags[k];

					var tag = changeTag.getElementsByTagName("id")[0];
					var id = tag.childNodes[0].nodeValue;
					var obj = d.getElementById(id);

					tag = changeTag.getElementsByTagName("row")[0];
					var row = tag.childNodes[0].nodeValue;

					tag = changeTag.getElementsByTagName("col")[0];
					var col = tag.childNodes[0].nodeValue;

					tag = changeTag.getElementsByTagName("value")[0];
					var state = tag.childNodes[0].nodeValue;

					if (obj != null) {

						var tables = obj.getElementsByTagName("table");
						var table = tables[tables.length - 1];

						var tbody = table.getElementsByTagName("tbody")[0];

						var rowObj = tbody.rows[parseInt(row)];
						if (rowObj != null) {
							var cellTags = rowObj.getElementsByTagName("td");
							var td = cellTags[parseInt(col)];

//							if (state == "1") {
//								td.style.backgroundColor = "#FFCCCC";
//							} else if (state == "2") {
//								td.style.backgroundColor = "#CAF7BB";
//							} else {
//								td.style.backgroundColor = null;
//							}
							setErrorType(obj, (state == 1) ? 0 : state); //TODO app.js
						}
					}
				}
			}

			var changeTags = responseTag.childNodes;
			for (i = 0; i < changeTags.length; i++) {
				var changeTag = changeTags[i];
				var name = changeTag.nodeName.toLowerCase();
				if ("prev" == name) {
					var id = changeTag.childNodes[0].nodeValue;
					var url = location.pathname + "?trg=frm&cmd=prv";
					if (url.indexOf("/") > 0)
						url = "/" + url;
					if (id != null && id == "1") {
						url = location.pathname + "?trg=frm&cmd=opn";
						if (url.indexOf("/") > 0)
							url = "/" + url;
						url += "&noCache=" + (new Date).getTime();
						if (parent.frames.srv)
							parent.frames.srv.location.assign(url);
						//            else
						//              window.frames.location.assign(url);
					} else {
						url = location.pathname + "?trg=fwb";
						if (url.indexOf("/") > 0)
							url = "/" + url;
						url += "&noCache=" + (new Date).getTime();
						if (parent.frames.srv)
							parent.frames.srv.location.assign(url);
						//            else
						//              window.frames.srv.location.assign(url);
					}
				} else if ("haserrors" == name) {
					var hasErrs = changeTag.childNodes[0].nodeValue;
					if (hasErrs != null && hasErrs == "true") {
						disableWholeWindow(currentLang=="kz"?"Оқшауды алу үшін қателер терезесінде әрекетті таңдаңыз!":"Для снятия блокировки, выберите действие в окне ошибок!", false);

						var post = "trg=frm&cmd=prverr";
						var fr = window.open('', 'CommitPopupFrame',
										'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');
						fr.moveTo(screen.availWidth / 2 - 310, screen.availHeight / 2 - 225);

						var req = createAsync();
						sendAsyncHtml(req, post, fr);
					}
				}
			}
		}
	}
}

function higlihtError(id, state) {
	parent.srv.higlihtError(id, state);
}

function higlihtError2(id, row, col, state) {
	parent.srv.higlihtError2(id, row, col, state);
}

function processSign(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var changeTags = responseTag.childNodes;
			for (i = 0; i < changeTags.length; i++) {
				var changeTag = changeTags[i];
				var name = changeTag.nodeName.toLowerCase();
				if ("sign" == name) {
					//alert("Данные успешно подписаны и переданы на дальнейшую обработку!");
					//if (parent)
					//  parent.close();
					var changeTags = changeTag.childNodes;
					var xmlToSign = changeTag.childNodes[0].childNodes[0].nodeValue;
					var profile = changeTag.childNodes[1].childNodes[0].nodeValue;
					var cert = changeTag.childNodes[2].childNodes[0].nodeValue;

					var password = window.showModalDialog("/ekyzmet-ui/pwd.html",
							"pwd", "dialogWidth:320px;dialogHeight:220px");

					disableWholeWindow(currentLang=="kz"?"Электронды қолтаңбаның жасалуы...":"Формирование электронной подписи...",
							false);

					var signed = parent.srv.document.app.signXml(xmlToSign,
							profile, password, cert);

					if (signed != null && signed.length > xmlToSign.length) {
						var post = "xml=1&trg=frm&cmd=prvas&signed="
								+ encodeURIComponent(signed);

						var reqIn = createAsync();
						var func = "processAfterSign";
						sendAsync(reqIn, post, func, false);
					}

				} else if ("haserrors" == name) {
					var hasErrs = changeTag.childNodes[0].nodeValue;
					if (hasErrs != null && hasErrs == "true") {
						var url = location.pathname + "?trg=frm&cmd=signerr";
						if (url.indexOf("/") > 0)
							url = "/" + url;
						url += "&noCache=" + (new Date).getTime();
						fr = window.open(url, 'CommitPopupFrame',
										'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');
						fr.moveTo(screen.availWidth / 2 - 310,
								screen.availHeight / 2 - 225);
						disableWholeWindow(currentLang=="kz"?"Оқшауды алу үшін қателер терезесінде әрекетті таңдаңыз!": "Для снятия блокировки, выберите действие в окне ошибок!", false);
					}
				}
			}
		}
	}
}

function processAfterSign(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("ok");
			if (tags != null && tags.length > 0) {
				alert(currentLang=="kz"?"Деректер жазылды және кейнгі өңделуге берілді!":"Данные успешно подписаны и переданы на дальнейшую обработку!");
				if (parent)
					parent.close();

				return;
			}
		}
	}
}

function setOpacity(obj, opacity) {
	opacity = (opacity == 100) ? 99.999 : opacity;
	// IE/Win
	if (obj != null && obj.style != null) {
		obj.style.filter = "alpha(opacity:" + opacity + ")";
	}
	// Safari<1.2, Konqueror
	//  obj.style.KHTMLOpacity = opacity/100;

	// Older Mozilla and Firefox
	//  obj.style.MozOpacity = opacity/100;

	// Safari 1.2, newer Firefox and Mozilla, CSS3
	//  obj.style.opacity = opacity/100;
}

function showPrintMenu() {
	var menu = parent.srv.document.getElementById("menu0");
	if (menu != null) {
		menu.style.display = '';
		menu.style.visibility = 'visible';
		var fone = parent.srv.document.getElementById("fone");
		fone.style.display = '';
		fone.style.visibility = 'visible';
		var ifone = parent.srv.document.getElementById("ifone");
		if (ifone != null) {
			ifone.style.display = '';
			ifone.style.visibility = 'visible';
		}
	}
}

function showHelpMenu() {
	var menu = parent.document.getElementById("printMenu");
	if (menu != null) {
		menu.style.display = '';
		menu.style.visibility = 'visible';
		var fone = parent.document.getElementById("fone");
		fone.style.display = '';
		fone.style.visibility = 'visible';
	}
}

function showUsers() {
	var url = location.pathname + "?trg=shu";
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	fr = window.open(url, 'ShowUsersFrame',
					'directories=no,height=350,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=400');
	fr.moveTo(screen.availWidth / 2 - 200, screen.availHeight / 2 - 175);
}

function makeNormalCursor(e) {
	var curs = document.body.style.cursor;

	if (curs != null && curs.toLowerCase() == "help") {
		changeCursor(e);
	}
}

function changeCursor(e) {
	if (!e)
		e = window.event;
	showToolTip(e, 'tipCtx');

	var curs = document.body.style.cursor;

	if (curs != null && curs.toLowerCase() == "help") {
		parent.document.body.style.cursor = "";
		parent.srv.document.body.style.cursor = "";
		if (parent.srv.hideAllTips)
			parent.srv.hideAllTips();

		document.body.onkeypress = null;
		parent.srv.document.body.onkeypress = null;
		parent.sts.document.body.onkeypress = null;

		document.body.onclick = null;
		parent.srv.document.body.onclick = null;
		parent.sts.document.body.onclick = null;

		var inps = parent.srv.document.getElementsByTagName("input");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "";
		}
		inps = parent.srv.document.getElementsByTagName("a");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "";
		}
		inps = parent.srv.document.getElementsByTagName("textarea");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "";
		}
		if (parent.srv.srv2 != null) {
			parent.srv.srv2.document.body.style.cursor = "";
			if (parent.srv.srv2.tsk != null) {
				parent.srv.srv2.tsk.document.body.style.cursor = "";
			}
			if (parent.srv.srv2.prs != null) {
				parent.srv.srv2.prs.document.body.style.cursor = "";
			}
		}
		parent.sts.document.body.style.cursor = "";
		document.body.style.cursor = "";
	} else {
		parent.document.body.style.cursor = "Help";
		parent.srv.document.body.style.cursor = "Help";
		var inps = parent.srv.document.getElementsByTagName("input");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "Help";
		}
		inps = parent.srv.document.getElementsByTagName("a");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "Help";
		}
		inps = parent.srv.document.getElementsByTagName("textarea");
		for (i = 0; i < inps.length; i++) {
			inps[i].style.cursor = "Help";
		}
		if (parent.srv.srv2 != null) {
			parent.srv.srv2.document.body.style.cursor = "Help";
			if (parent.srv.srv2.tsk != null) {
				parent.srv.srv2.tsk.document.body.style.cursor = "Help";
			}
			if (parent.srv.srv2.prs != null) {
				parent.srv.srv2.prs.document.body.style.cursor = "Help";
			}
		}
		parent.sts.document.body.style.cursor = "Help";
		document.body.style.cursor = "Help";

		document.body.onkeypress = function(e) {
			if (!e)
				e = window.event;
			if (e.keyCode == 27) { //ESC
				changeCursor(e);
			}
		};
		parent.srv.document.body.onkeypress = function(e) {
			if (!e)
				e = parent.srv.event;
			if (e.keyCode == 27) { //ESC
				changeCursor(e);
			}
		};
		parent.sts.document.body.onkeypress = function(e) {
			if (!e)
				e = parent.sts.event;
			if (e.keyCode == 27) { //ESC
				changeCursor(e);
			}
		};

		document.body.onclick = function() {
			parent.srv.hideAllTips();
		};

		parent.srv.document.body.onclick = function() {
			parent.srv.hideAllTips();
		};

		parent.sts.document.body.onclick = function() {
			parent.srv.hideAllTips();
		};
	}
}

function menuBtnOver(obj) {
	obj.className = "menuBtnOver";
}

function menuBtnOut(obj) {
	obj.className = "menuBtn";
}

function cancelUpload() {
	if (parent.window.onunload) {
		parent.window.onunload = function() {
		};
	} else {
		parent.document.body.onunload = function() {
		};
	}
	return true;
}

function showToolTip(e, tipId) {
	if (!e)
		e = window.event;
	popUp(e, tipId);
}

function pw() {
	return window.innerWidth || document.documentElement.clientWidth
			|| document.body.clientWidth;
};

function mouseX(evt) {
	return evt.clientX ? evt.clientX
			+ (document.documentElement.scrollLeft || document.body.scrollLeft)
			: evt.pageX;
}

function popUp(evt, oi) {
	if (document.getElementById) {
		var wp = pw();
		dm = document.getElementById(oi);
		ds = dm.style;
		st = ds.visibility;
		if (dm.offsetWidth)
			ew = dm.offsetWidth;
		else if (dm.clip.width)
			ew = dm.clip.width;

		if (st == "visible" || st == "show") {
			ds.visibility = "hidden";
		} else {
			tv = 1;
			lv = mouseX(evt) + (ew / 4);
			if (lv < 2)
				lv = 2;
			else if (lv + ew > wp)
				lv -= ew / 2;
			lv += 'px';
			tv += 'px';
			ds.left = lv;
			ds.top = tv;
			ds.visibility = "visible";
		}
	}
}

function attachKeydownHandler() {
	if (document.addEventListener) {
		document.addEventListener('keypress', onkeydownHandler, true);
	} else if (document.attachEvent) {
		document.attachEvent('onkeydown', onkeydownHandler);
	}
}

function onkeydownHandler(ev) {
	ev = ev || window.event; // for IE
	var charCode = ev.which || ev.keyCode;

	if (charCode == 9) {
		var res = false;

		if (ev.shiftKey)
			res = focusPrev(false);//ev.srcElement || ev.target);
		else
			res = focusNext(false);//ev.srcElement || ev.target);

		if (!res) {
			ev.cancelBubble = true;
			ev.returnValue = false;

			if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
	} else if (curFocused > -1 && (charCode == 13 || charCode == 32)) {
		var s = document.getElementsByTagName("a");
		var toElem = s[curFocused];
		toElem.click();
		return false;
	}
	return true;
}

var curFocused = -1;
var curClass = null;
var focusColor = "#dd3045";
var curBorder = null;
var borderStyle = null;

function focusPrev(fromBegin) {

	var s = document.getElementsByTagName("a");
	var cur = (curFocused > -1) ? s[curFocused] : null;

	if (fromBegin)
		curFocused = s.length;

	curFocused--;
	if (curFocused < 0) {
		curFocused = s.length - 1;

		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		if (cur != null)
			cur.blur();

		parent.sts.focusPrev(true);
		parent.sts.focus();
	} else {
		var toElem = s[curFocused];

		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		curClass = toElem.style.borderColor;
		toElem.style.borderColor = focusColor;
		curBorder = toElem.style.borderStyle;
		toElem.style.borderStyle = "solid";
		toElem.style.borderWidth = 1;

		if (cur != null)
			cur.blur();
		toElem.focus();
	}
	return false;
}

function focusNext(fromBegin) {
	var s = document.getElementsByTagName("a");
	var cur = (curFocused > -1) ? s[curFocused] : null;

	if (fromBegin)
		curFocused = -1;

	curFocused++;
	if (curFocused == s.length) {
		curFocused = -1;
		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		if (cur != null)
			cur.blur();

		if (parent.srv.focusNext)
			parent.srv.focusNext(null);
		parent.srv.focus();
	} else {
		var toElem = s[curFocused];

		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		curClass = toElem.style.borderColor;
		toElem.style.borderColor = focusColor;
		curBorder = toElem.style.borderStyle;
		toElem.style.borderStyle = "solid";
		toElem.style.borderWidth = 1;

		if (cur != null)
			cur.blur();
		toElem.focus();
	}
	return false;
}

function focusCurrent() {
	if (curFocused != -1) {
		var s = document.getElementsByTagName("a");
		var cur = s[curFocused];
		if (cur.style.borderColor != focusColor) {
			curClass = cur.style.borderColor;
			cur.style.borderColor = focusColor;
			curBorder = cur.style.borderStyle;
			cur.style.borderStyle = "solid";
			cur.style.borderWidth = 1;
		}
		cur.focus();
	} else
		focusNext(false);
}

function blurCurrent() {
	if (curFocused != -1) {
		var s = document.getElementsByTagName("a");
		var cur = s[curFocused];
		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		cur.blur();
	}
}
