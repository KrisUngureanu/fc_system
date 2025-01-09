var pingLost2 = 0;
var timeout5 = null;
var timeoutFunction5;

function createAsync() {
	return (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
}

function sendAsyncTab(req, post, func) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	//	req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() { processAsyncAnswerTab(req, post, func); };
		req.send(post);
	}
}

function processAsyncAnswerTab(req, post, fn) {
	if (req.readyState == 4) {
		if (req.status == 200) {

			pingLost2 = 0;
			enableWholeWindow(true);
			timeout5 = null;
			
			var rts = req.responseXML.getElementsByTagName("r");
			if (rts == null || rts.length == 0) return;

			var rt = rts[0];
			var tags = rt.getElementsByTagName("alert");
			if (tags != null && tags.length > 0) {
				for (i = 0; i < tags.length; i++) {
					var tag = tags[i];
					var msg = tag.childNodes[0].nodeValue;
					if (msg != null && msg.length > 0) {
						alert(msg);
					}
				}
			}
			tags = rt.getElementsByTagName("fatal");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					disableWholeWindow(msg, true);
					alert(msg);
					return;
				}
			}

			if (fn == "processTab")
				processTab(req);
			else if (fn == "processTab2")
				processTab2(req);
						
		} else {
			pingLost2++;
			var t = pingLost2 * 5;
			disableWholeWindow(currentLang=="kz"?("Сервермен байланыс жоғалды. Қайта қосылу "+t+" секундтан кейін."):("Связь с сервером утеряна. " +
					"Повторная попытка подключения через " + t + " сек."), false, t);
			timeoutFunction5 = function() { sendAsyncTab(req, post, fn); };
			timeout5 = setTimeout(timeoutFunction5, t * 1000);
		}
	}
}

function retryNow2() {
	if (timeout5 != null) {
		clearTimeout(timeout5);
		timeout5 = setTimeout(timeoutFunction5, 100);
	}
}

function selectTab(tabbedPaneId, tabId) {
	var d = document;
	var tab = d.getElementById(tabbedPaneId + ":" + tabId);

	var i = 0;
	var sel = 0;
	var selectedTabId = tabbedPaneId + ":" + i;

	var selectedTab = d.getElementById(selectedTabId);
	var shownUl = d.getElementById("ul" + i);

	while (selectedTab != null) {
		if (selectedTab.className == "selectedTab") {
			selectedTab.className = "notselectedTab";
			shownUl.className = "Hidden";

			break;
		}
		i++;
		selectedTabId = tabbedPaneId + ":" + i;
		selectedTab = d.getElementById(selectedTabId);
		shownUl = d.getElementById("ul" + i);
	}
	tab.className = "selectedTab";

	var ul = d.getElementById("ul" + tabId);

	var waitAnswer = false;

	if (ul != null && ul.className != null && ul.innerHTML != null) {
		ul.className = "Shown";

		var innerStr = trim(ul.innerHTML);
		if (innerStr == "") {
			ul.innerHTML = "<DIV ONCLICK='cancelLoad(this);' CLASS='loadMsg'>&nbsp;Подождите идет загрузка...&nbsp;</DIV>";
			waitAnswer = true;
		}
		var post = "xml=1&trg=srv&cmd=tab&id=" + tabId;
		if (!waitAnswer) {
			post += "&wait=no";
		}
		var req = createAsync();
		var func = "processTab";
		sendAsyncTab(req, post, func);
	}
}

function processTab(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTags = req.responseXML.getElementsByTagName("r");

			if (responseTags != null && responseTags.length > 0) {
				var tags = responseTags[0].getElementsByTagName("id");

				if (tags != null && tags.length > 0) {
					var tag = tags[0];
					var id = tag.childNodes[0].nodeValue;
	
					var ul = d.getElementById("ul" + id);
	
					var dataTag = responseTags[0].getElementsByTagName("data")[0];
	
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
}

function selectTab2(tabbedPaneId, tabId) {
	var d = document;

	var tab = d.getElementById(tabbedPaneId + ":" + tabId);
	var tabbedPane = tab.parentNode;

	var lis = tabbedPane.children;
	var i = 0;
	for (i = 0; i < lis.length; i++) {
		var selectedTab = lis[i];
		var selectedTabId = selectedTab.id;
		var shownUl = d.getElementById("ul" + selectedTabId);

		if (selectedTab.className == "selectedTab") {
			selectedTab.className = "notselectedTab";
			shownUl.className = "Hidden";

			break;
		}
	}
	tab.className = "selectedTab";

	var ul = d.getElementById("ul" + tabbedPaneId + ":" + tabId);

	var waitAnswer = false;

	if (ul != null && ul.className != null && ul.innerHTML != null) {
		ul.className = "Shown";

		var innerStr = trim(ul.innerHTML);
		if (innerStr == "") {
			ul.innerHTML = "<DIV ONCLICK='cancelLoad(this);' CLASS='loadMsg'>&nbsp;Подождите идет загрузка...&nbsp;</DIV>";
			waitAnswer = true;
		}
		var post = "xml=1&trg=frm&cmd=tab&id="
				+ tabbedPaneId + "&tab=" + tabId;
		if (!waitAnswer) {
			post += "&wait=no";
		}
		var req = createAsync();
		var func = "processTab2";
		sendAsyncTab(req, post, func);
	}
}

function cancelLoad(obj) {
	var ul = obj.parentNode;
	ul.innerHTML = "";
}

function processTab2(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTags = req.responseXML.getElementsByTagName("r");
			if (responseTags != null && responseTags.length > 0) {
				var tag = responseTags[0].getElementsByTagName("id")[0];
				var id = tag.childNodes[0].nodeValue;
				var ul = d.getElementById("ul" + id);

				var dataTag = responseTags[0].getElementsByTagName("data")[0];

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
			res += " " + attr.nodeName + "=\"" + xmlQuote2(attr.nodeValue)
					+ "\"";
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

function xmlQuote2(str) {
	if (str == null)
		return "";

	var res = "";
	for (i = 0; i < str.length; i++) {
		var ch = str.charAt(i);

		if (ch == '<')
			res += "&#60;";
		else if (ch == '>')
			res += "&#62;";
		else if (ch == '&')
			res += "&#38;";
		else if (ch == '\'')
			res += "&#39;";
		else if (ch == '/')
			res += "&#47;";
		else if (ch == '"')
			res += "&#34;";
		else if (ch == '\r')
			res += "&#60;br&#47;&#62;";
		else if (ch == '\n')
			res += "&#60;br&#47;&#62;";
		else
			res += ch;
	}
	return res;
}

function disableWholeWindow(msg, permanently, t) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently, t);
	} else if (w != null && w.parent != null && w.parent.disableWindow) {
		w.parent.disableWindow(msg, permanently, t);
	} else if (w != null && w.parent != null && w.parent.parent != null
			&& w.parent.parent.disableWindow) {
		w.parent.parent.disableWindow(msg, permanently, t);
	}
}

function enableWholeWindow(perform) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.enableWindow(perform);
	} else if (w != null && w.parent != null && w.parent.disableWindow) {
		w.parent.enableWindow(perform);
	} else if (w != null && w.parent != null && w.parent.parent != null
			&& w.parent.parent.disableWindow) {
		w.parent.parent.enableWindow(perform);
	}
}
