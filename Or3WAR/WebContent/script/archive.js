var pingLost = 0;
var timeout3 = null;
var timeoutFunction3;

function createAsync() {
	return (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
}

function sendAsyncArc(req, post, func) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	//	req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() { processAsyncAnswerArc(req, post, func); };
		req.send(post);
	}
}

function processAsyncAnswerArc(req, post, fn) {
	if (req.readyState == 4) {
		if (req.status == 200) {

			pingLost = 0;
			enableWholeWindow(true);
			timeout3 = null;

			if (fn == "processNode")
				processNode(req);
						
		} else {
			pingLost++;
			var t = pingLost * 5;
			disableWholeWindow(currentLang=="kz"?("Сервермен байланыс жоғалды. Қайта қосылу "+t+" секундтан кейін."):("Связь с сервером утеряна. " +
					"Повторная попытка подключения через " + t + " сек."), false, t);
			timeoutFunction3 = function() { sendAsyncArc(req, post, fn); };
			timeout3 = setTimeout(timeoutFunction3, t * 1000);
		}
	}
}

function retryNow() {
	if (timeout3 != null) {
		clearTimeout(timeout3);
		timeout3 = setTimeout(timeoutFunction3, 100);
	}
}

function expand(nodeId, dict) {
	var d = document;

	var ul = d.getElementById("ul" + nodeId);
	var img = d.getElementById("img" + nodeId);
	var req;
	var waitAnswer = false;
	if (ul != null && ul.className != null && ul.innerHTML != null) {

		if (ul.className == "Hidden") {
			ul.className = "Shown";
			img.src = "images/Open.gif";

			var innerStr = trim(ul.innerHTML);
			if (innerStr == "") {
				ul.innerHTML = "<DIV CLASS='loadMsg'>&nbsp;Подождите идет загрузка...&nbsp;</DIV>";
				waitAnswer = true;
			}
		} else {
			ul.className = "Hidden";
			img.src = "images/CloseFolder.gif";
		}

		var post = "xml=1&cmd=exp&id=" + nodeId;
		if (dict == true) {
			post += "&trg=dic";
		} else {
			post += "&trg=arc";
		}
		if (!waitAnswer) {
			post += "&wait=no";
		}

		var req = createAsync();
		var func = "processNode";
		sendAsyncArc(req, post, func);
	}
}

function processNode(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var response = req.responseXML.getElementsByTagName("r");
			if (response != null && response.length > 0) {
				var responseTag = response[0];

				var tag = responseTag.getElementsByTagName("id")[0];
				var id = tag.childNodes[0].nodeValue;

				var ul = document.getElementById("ul" + id);

				var dataTag = responseTag.getElementsByTagName("data")[0];
				var liTags = dataTag.childNodes;

				var str = "";
				var i = 0;
				for (i = 0; i < liTags.length; i++) {
					var liTag = liTags[i];
					str = str + getElementXml(liTag) + "\r\n";
				}
				ul.innerHTML = str;
			}
		}
	}
}

function openInterface(nodeId) {
	pleaseWait(currentLang=="kz"?"Интерфейстың ашылуы...":"Открытие интерфейса...");

	var url = location.pathname + "?trg=arc&cmd=opn&id=" + nodeId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime() + Math.random() * 1234567;
	parent.window.location.assign(url);
}

function openDictInterface(nodeId) {
	pleaseWait(currentLang=="kz"?"Интерфейстың ашылуы...":"Открытие интерфейса...");
	var d = document;

	var url = location.pathname + "?trg=dic&cmd=opn&id=" + nodeId;
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime() + Math.random() * 1234567;
	parent.window.location.assign(url);
}

function trim(str) {
	return str.replace(/(^\s+)|(\s+$)/g, "");
}

function getElementXml(node) {
	if (node.xml) {
		return node.xml;
	} else if (node.nodeType == 1) {
		var res = "<" + node.nodeName;
		var ak = 0;
		for (ak = 0; ak < node.attributes.length; ak++) {
			var attr = node.attributes[ak];
			res += " " + attr.nodeName + "=\"" + attr.nodeValue + "\"";
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
		res += "</" + node.nodeName + ">";
		return res;
	}
	return "";
}

function disableWholeWindow(msg, permanently, t) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently, t);
	} else {
		w.parent.disableWindow(msg, permanently, t);
	}
}

function enableWholeWindow(perform) {
	var w = parent;
	if (w != null && w.enableWindow) {
		w.enableWindow(perform);
	} else {
		w.parent.enableWindow(perform);
	}
}
