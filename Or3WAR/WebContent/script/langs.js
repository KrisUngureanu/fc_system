var beginTime;

function setInterfaceLang(langId) {
	disableWholeWindow(currentLang=="kz"?"Интерфейс  тілінің өзгерілуі...":"Изменение языка интерфейса...", false);
	var url = location.pathname + "?trg=top&cmd=lng&val=" + langId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	if (parent.window.onunload) {
		parent.window.onunload = function() {
		};
	} else {
		parent.document.body.onunload = function() {
		};
	}
	parent.location.assign(url);
}

function setDataLang(langId) {
	disableWholeWindow(currentLang=="kz"?"Деректер тілінің өзгеруі...":"Изменение языка данных...", false);
	var url = location.pathname + "?trg=top&cmd=dlng&val=" + langId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	if (parent.window.onunload) {
		parent.window.onunload = function() {
		};
	} else {
		parent.document.body.onunload = function() {
		};
	}
	parent.location.assign(url);
}

function createAsync() {
	return (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
}

function refreshPingInfo() {
	var req = createAsync();
	var post = "xml=1&trg=srv&cmd=rpng&noCache=" + (new Date).getTime();

	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	//	req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() {
			updatePingInfo(req);
		};
		beginTime = (new Date).getTime();
		req.send(post);
	}
}

function updatePingInfo(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var pingTime = (new Date).getTime() - beginTime;
			var pingSpan = document.getElementById("ping");

			pingSpan.innerHTML = "" + pingTime;

			var responseTag;
			var responseTags = req.responseXML.getElementsByTagName("r");
			if (responseTags == null || responseTags.length == 0) return;
			
			responseTag = responseTags[0];

			var tags = responseTag.getElementsByTagName("alert");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					alert(msg);
				}
			}
			tags = responseTag.getElementsByTagName("fatal");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					disableWholeWindow(msg, true);
					alert(msg);
				}
			}
		}
	}
}

function disableWholeWindow(msg, permanently) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently);
	}
}

function enableWholeWindow(perform) {
	var w = parent;
	if (w != null && w.enableWindow) {
		w.enableWindow(perform);
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
		var s = document.getElementsByTagName("img");
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

	var s = document.getElementsByTagName("img");
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

		if (parent.srv.focusPrev)
			parent.srv.focusPrev(null);
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

function focusNext(fromBegin) {
	var s = document.getElementsByTagName("img");
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

		parent.menu.focusNext(true);
		parent.menu.focus();
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
		var s = document.getElementsByTagName("img");
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
		var s = document.getElementsByTagName("img");
		var cur = s[curFocused];
		if (cur != null && cur.style.borderColor == focusColor) {
			cur.style.borderColor = curClass;
			cur.style.borderStyle = curBorder;
		}
		cur.blur();
	}
}
