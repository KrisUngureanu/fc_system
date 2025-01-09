var lastStartedId = "";
var pingLost = 0;
var timeout6 = null;
var timeoutFunction6;

function createAsync() {
	return (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
}

function sendAsyncPrs(req, post, func) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	//	req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() { processAsyncAnswerPrs(req, post, func); };
		req.send(post);
	}
}

function processAsyncAnswerPrs(req, post, fn) {
	if (req.readyState == 4) {
		if (req.status == 200) {

			pingLost = 0;
			enableWholeWindow(true);
			timeout6 = null;
			
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

			if (fn == "processNode")
				processNode(req);
			else if (fn == "processStartProcessResponse")
				processStartProcessResponse(req);
						
		} else {
			pingLost++;
			var t = pingLost * 5;
			disableWholeWindow(currentLang=="kz"?("Сервермен байланыс жоғалды. Қайта қосылу "+t+" секундтан кейін."):("Связь с сервером утеряна. " +
					"Повторная попытка подключения через " + t + " сек."), false, t);
			timeoutFunction6 = function() { sendAsyncPrs(req, post, fn); };
			timeout6 = setTimeout(timeoutFunction6, t * 1000);
		}
	}
}

function retryNow() {
	if (timeout6 != null) {
		clearTimeout(timeout6);
		timeout6 = setTimeout(timeoutFunction6, 100);
	}
}

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
		sendAsyncPrs(req, post, func);
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
		}
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
			var func = "processStartProcessResponse";
			sendAsyncPrs(req, post, func);
		}
	}
}

function processStartProcessResponse(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("tr");

			if (tags != null && tags.length > 0) {
				var d = parent.frames['tsk'].document;

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
				var strOnClick = "parent.frames['tsk']." + tags[0].getAttribute("onclick");

				row.onclick = function() {
					eval(strOnClick);
				};
    
					var tdTags = tags[0].getElementsByTagName("td");
					var msg1 = "";
					var msg2 = "";

					for (j = 0; j < tdTags.length; j++) {
						var tdTag = tdTags[j];
						var td = d.createElement("TD");
						for (k = 0; k < tdTag.attributes.length; k++) {
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
										eval("parent.frames['tsk'].openInterface(this);");
									};               
								} else if (type == 1) {
									td.onclick = function() {
										eval("parent.frames['tsk'].nextStep(this, '" + msg1 + "');");
									};
								} else if (type == 2) {
									td.onclick = function() {
										eval("parent.frames['tsk'].killProcess(this, '" + msg2 + "');");
									};
								} else if (type == 3) {
									td.onclick = function() {
										eval("parent.frames['tsk'].openControlInterface(this);");
									};
								}
							} else {
								td[tdTag.attributes[k].nodeName] =
									tdTag.attributes[k].nodeValue;
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
/*
  	          	var tdTags = tags[0].getElementsByTagName("td");
  	          	var i = 0;
  	          	for (i = 0; i < tdTags.length; i++) {
  	          		var tdTag = tdTags[i];
  	          		var td = d.createElement("TD");
  	          		var k = 0;
  	          		for (k = 0; k < tdTag.attributes.length; k++) {
  	          			if (tdTag.attributes[k].nodeName == "style") {
  	          				td.style.cssText = tdTag.attributes[k].nodeValue;
  	          			} else if (tdTag.attributes[k].nodeName == "onclick") {
  	          				var start = tdTag.attributes[k].nodeValue.indexOf("'");
  	          				var end = tdTag.attributes[k].nodeValue.indexOf("'", start + 1);
  	          				var rid = tdTag.attributes[k].nodeValue.substring(start + 1, end);
  	          				td.onclick = function() {
  	          					parent.frames['tsk'].killProcess(rid);
  	          				};
  	          			} else {
  	          				td.setAttribute(tdTag.attributes[k].nodeName,
  	          						tdTag.attributes[k].nodeValue);
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
  	          	*/
  	          	tbody.appendChild(row);
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
		var res =  "<" + node.nodeName;
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
		res +=  "</" + node.nodeName + ">";
		return res;
	}
	return "";
}

function getElementNode(d, node) {
	if (node != null) {
		if (node.nodeType == 1) {
			var res =  d.createElement(node.nodeName);
			var ak = 0;
			for (ak = 0; ak < node.attributes.length; ak++) {
				var attr = node.attributes[ak];
				res.setAttribute(attr.nodeName, attr.nodeValue);
			}
			var children = node.childNodes;
			var i = 0;
			for (i = 0; i < children.length; i++) {
				var child = children[i];
				var chres = getElementNode(d, child);
				if (chres != null) {
					res.appendChild(chres);
				}
			}
			return res;
		} else if (node.nodeType == 3) {
			return d.createTextNode(node.nodeValue);
		}
	}
	return null;
}

function disableWholeWindow(msg, permanently, t) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.disableWindow(msg, permanently, t);
	}
	else if (w != null && w.parent != null && w.parent.disableWindow) {
		w.parent.disableWindow(msg, permanently, t);
	}
	else if (w != null && w.parent != null && w.parent.parent != null && w.parent.parent.disableWindow) {
		w.parent.parent.disableWindow(msg, permanently, t);
	}
}

function enableWholeWindow(perform) {
	var w = parent;
	if (w != null && w.disableWindow) {
		w.enableWindow(perform);
	}
	else if (w != null && w.parent != null && w.parent.disableWindow) {
		w.parent.enableWindow(perform);
	}
	else if (w != null && w.parent != null && w.parent.parent != null && w.parent.parent.disableWindow) {
		w.parent.parent.enableWindow(perform);
	}
}

// Remove existing table rows
function removeSelection(tbody, col) {
	for (i=1; i < tbody.rows.length; i++) {
		tbody.rows[i].className = "notselected";
		if (col > -1) tbody.rows[i].cells[col].className = "";
	}
}

function getLastStartedId() {
	return lastStartedId;
}