﻿﻿var pingLost = 0;
var timeout2 = null;
var timeoutFunction2;

function sendAsyncTsk(req, post, func, enable) {
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	//	req.setRequestHeader("Content-Length", post.length);
		
		req.onreadystatechange = function() { processAsyncTskAnswer(req, post, func, enable); };

		req.send(post);
	}
}

function processAsyncTskAnswer(req, post, fn, enable)
{
	if (req.readyState == 4) {
		if (req.status == 200) {

			if (pingLost > 0 && enable) enableWholeWindow(true);
			pingLost = 0;
			timeout2 = null;

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
					pleaseWait(msg);
					alert(msg);
					return;
				}
			}

			if (fn == "updateTaskTable")
				updateTaskTable(req);
			else if (fn == "redirectToReport")
				redirectToReport(req);
			else if (fn == "redirectToDialog")
				redirectToDialog(req);
			else if (fn == "showResult")
				showResult(req);
						
		} else {
			pingLost++;
			var t = pingLost * 5;
			disableWholeWindow(currentLang=="kz"?("Сервермен байланыс жоғалды. Қайта қосылу "+t+" секундтан кейін."):("Связь с сервером утеряна. " +
					"Повторная попытка подключения через " + t + " сек."), false, t);
			timeoutFunction2 = function() { sendAsyncTsk(req, post, fn, enable); };
			timeout2 = setTimeout(timeoutFunction2, t * 1000);
		}
	}
}

function retryNow() {
	if (timeout2 != null) {
		clearTimeout(timeout2);
		timeout2 = setTimeout(timeoutFunction2, 100);
	}
}

function refreshTaskTable() {
	if (pingLost == 0) {
		var post = "xml=1&trg=srv&cmd=rtsk";

		var req = createAsync();
		var func = "updateTaskTable";
		sendAsyncTsk(req, post, func, true);
	}
}

function updateTaskTable(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var table = d.getElementById("taskTable");
			
			if (table != null) {
				var tbody = table.getElementsByTagName('tbody')[0];
				var selCol = table.getAttribute("selectedCol");
				var oldCol = parseInt(selCol);
	
				var tags = responseTag.getElementsByTagName("deleted");
				var i = 0;
				if (tags != null && tags.length > 0) {
					var rows = tags[0].getElementsByTagName("row");
					for (i = 0; i < rows.length; i++) {
						var rowTag = rows[i];
						var rowId = rowTag.getAttribute("id");
						var row = d.getElementById(rowId);
						if (row != null)
							tbody.deleteRow(row.rowIndex);
					}
				}
	
				tags = responseTag.getElementsByTagName("inserted");
				if (tags != null && tags.length > 0) {
					var rows = tags[0].getElementsByTagName("tr");
					for (i = 0; i < rows.length; i++) {
						var rowTag = rows[i];
						var rowId = rowTag.getAttribute("id");
						var row = d.createElement("TR");
						row.setAttribute("id", rowId);
						var cname = rowTag.getAttribute("class");
	
						if (cname == "selected") {
							removeSelection(tbody, oldCol);
						}
	
						row.className = cname;
	
						row.height = rowTag.getAttribute("height");
						var st = rowTag.getAttribute("style");
						if (st != null) {
							row.style.cssText = st;
						}
						var strOnClick = rowTag.getAttribute("onclick");
						row.onclick = function(event) {
							eval(strOnClick);
						};
			
						var tdTags = rowTag.getElementsByTagName("td");
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
									} else if (tdTag.attributes[k].nodeValue.indexOf("killProcess") > -1) {
										type = 2;
										var b1 = tdTag.attributes[k].nodeValue.indexOf("'");
										var b2 = tdTag.attributes[k].nodeValue.indexOf("'", b1 + 1);
										msg2 = tdTag.attributes[k].nodeValue.substring(b1 + 1, b2);
									} else if (tdTag.attributes[k].nodeValue.indexOf("openControlInterface") > -1) {
										type = 3;
									} else if (tdTag.attributes[k].nodeValue.indexOf("showProcessReport") > -1) {
										type = 4;
									} else if (tdTag.attributes[k].nodeValue.indexOf("showFastReport") > -1) {
										type = 5;
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
									} else if (type == 4) {
										td.onclick = function() {
											eval("showProcessReport(this);");
										};
									} else if (type == 5) {
										td.onclick = function() {
											eval("showFastReport(this);");
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
	
				tags = responseTag.getElementsByTagName("updated");
				if (tags != null && tags.length > 0) {
					var rows = tags[0].getElementsByTagName("tr");
					if (rows != null) {
						for (i = 0; i < rows.length; i++) {
							var rowTag = rows[i];
							var rowId = rowTag.getAttribute("id");
	
							var row = d.getElementById(rowId);
							if (row != null) {
								var cname = rowTag.getAttribute("class");
	
								if (cname == "selected") {
									removeSelection(tbody, oldCol);
								}
	
								row.className = cname;
	
								row.height = rowTag.getAttribute("height");
								var st = rowTag.getAttribute("style");
								if (st != null) {
									row.style.cssText = st;
								}
	
								var strOnClick = rowTag.getAttribute("onclick");
								row.onclick = function(event) {
									eval(strOnClick);
								};
								var tdTags = rowTag.getElementsByTagName("td");
	
								var msg1 = "";
								var msg2 = "";
								for (var j = 0; j < tdTags.length; j++) {
									var tdTag = tdTags[j];
									var td = row.cells[j];
									td.onclick = null;
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
											} else if (tdTag.attributes[k].nodeValue.indexOf("killProcess") > -1) {
												type = 2;
												var b1 = tdTag.attributes[k].nodeValue.indexOf("'");
												var b2 = tdTag.attributes[k].nodeValue.indexOf("'", b1 + 1);
												msg2 = tdTag.attributes[k].nodeValue.substring(b1 + 1, b2);
											} else if (tdTag.attributes[k].nodeValue.indexOf("openControlInterface") > -1) {
												type = 3;
											} else if (tdTag.attributes[k].nodeValue.indexOf("showProcessReport") > -1) {
												type = 4;
											} else if (tdTag.attributes[k].nodeValue.indexOf("showFastReport") > -1) {
												type = 5;
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
											} else if (type == 4) {
												td.onclick = function() {
													eval("showProcessReport(this);");
												};
											} else if (type == 5) {
												td.onclick = function() {
													eval("showFastReport(this);");
												};
											}
										} else {
											td.setAttribute(tdTag.attributes[k].nodeName, tdTag.attributes[k].nodeValue);
										}
									}
									
									var oldchildren = td.childNodes;
									var m = 0;
									for (m = oldchildren.length - 1; m>=0; m--) {
										var child = oldchildren[m];
										if (child.nodeType == 1 || child.nodeType == 3) {
											td.removeChild(child);
										}
									}
									
									var	children = tdTag.childNodes;
									var n = 0;
									for (n = 0; n < children.length; n++) {
										var child = children[n];
										var chres = getElementNode(d, child);
										if (chres != null) {
											td.appendChild(chres);
										}
									}
								}
							}
						}
					}
				}
			}
			var tags;
			tags = responseTag.getElementsByTagName("openUI");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var msg = tag.childNodes[0].nodeValue;
				if (msg != null && msg.length > 0) {
					openUI(msg);
					return;
				}
			}else{
				tags = responseTag.getElementsByTagName("auto");
				if (tags != null && tags.length > 0) {
					var rows = tags[0].getElementsByTagName("tr");
					for (i = 0; i < rows.length; i++) {
						var rowTag = rows[i];
						var rowId = rowTag.getAttribute("id");
						var width = rowTag.getAttribute("widthD");
						var height = rowTag.getAttribute("heightD");
						if (width==null){
							width = 800;
						}
						if (height==null){
							height = 600;
						}
						
						eval("startAutoInterface('" + rowId + "'," +  "width," +  "height" +  ");");
					}
				}
			}	
		}
	}
}

function showFastReport(obj) {
	pleaseWait(currentLang=="kz"?"Есептің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие отчета...");

	obj = obj.parentNode;
	var flowId = obj.id;

	var post = "xml=1&trg=frm&cmd=mfrpt&id="+flowId;
	var req = createAsync();
	var func = "redirectToReport";
	sendAsyncTsk(req, post, func, false);
}

function openInterface(obj) {
	var dial = obj.getAttribute("dialog");
	obj = obj.parentNode;
	var flowId = obj.id;
	if ("true" == dial) {
		var post = "xml=1&trg=frm&cmd=opndlg&id="+flowId;
		var req = createAsync();
		var func = "redirectToDialog";
		sendAsyncTsk(req, post, func, false);

		//var value = window.showModalDialog(url, "DialogFrame", "dialogWidth:800px;dialogHeight:600px");
		//if (value != null && value != "undefined")
		//	windowClosed(value);
	} else {
		pleaseWait(currentLang=="kz"?"Интерфейстің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие интерфейса...");

		var url = location.pathname + "?trg=frm&cmd=opn&id=" + flowId;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		parent.parent.location.assign(url);
	}
}

function openControlInterface(obj) {
	var dial = "true";
	obj = obj.parentNode;
	var flowId = obj.id;

	var url = location.pathname + "?trg=frm&cmd=ctl&id=" + flowId;
	if (url.indexOf("/") > 0) url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	if ("true" == dial) {
		url = url + "&dialog=true";
		var value = window.showModalDialog(url, "DialogFrame", "dialogWidth:800px;dialogHeight:600px");
		if (value != null && value != "undefined")
			windowClosed(value);
	} else {
		pleaseWait(currentLang=="kz"?"Интерфейстің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие интерфейса...");
		parent.parent.location.assign(url);
	}
}

function redirectToDialog(req) {
	var responseTag = req.responseXML.getElementsByTagName("r")[0];
	var tag = responseTag.getElementsByTagName("f")[0];
	var flowId = tag.childNodes[0].nodeValue;
		
	var tags = responseTag.getElementsByTagName("w");
	var width = 800;
	if (tags != null && tags.length > 0) {
		width = parseInt(tags[0].childNodes[0].nodeValue);
	}
	
	tags = responseTag.getElementsByTagName("h");
	var height = 600;
	if (tags != null && tags.length > 0) {
		height = parseInt(tags[0].childNodes[0].nodeValue);
	}

	//var row = document.getElementById(flowId);
	//if (row != null){
	//	disableRow(row);
	//}
	
	var autos = responseTag.getElementsByTagName("a");
	var url = location.pathname + "?trg=frm&cmd=autdlg&id=" + flowId;
	if (url.indexOf("/") > 0){
		url = "/" + url;
	}
	url += "&noCache=" + (new Date).getTime();

	if (autos != null && autos.length > 0) {
		url += "&auto=true";

		var trg = $('#modalAuto');
	    
	    trg.find('.modal-body').width(width);
	    trg.find('.modal-body').height(height);
	    var hf = width/2;
	    var hh = height/2;
	    trg.css({"margin-left": -hf, "margin-top": -hh - 30});
	
	    $(trg).modal({remote: url, backdrop: 'static'});
	} else {
		var trg = $('#modalDlg');
	    
	    trg.find('.modal-body').width(width);
	    trg.find('.modal-body').height(height);
	    var hf = width/2;
	    var hh = height/2;
	    trg.css({"margin-left": -hf, "margin-top": -hh - 30});
	
	    $(trg).modal({remote: url, backdrop: 'static'});
	}
}

function startAutoInterface(flowId, width, height) {
	var row = document.getElementById(flowId);
	if (row != null){
		disableRow(row);
	}
	var url = location.pathname + "?trg=frm&cmd=aut&id=" + flowId;
	if (url.indexOf("/") > 0){
		url = "/" + url;
	}
	url = url + "&dialog=true";
	url += "&noCache=" + (new Date).getTime();
	
	//var value = window.showModalDialog(url, "DialogFrame", "dialogWidth:"+width+"px; dialogHeight:"+height+"px; resizable:yes; center:yes;");
	//if (value != null && value != "undefined"){
	//	windowClosed(value);
	//}
	
    var trg = $('#modalAuto');
    //$('#dheadAuto').text(title);
    
    trg.find('.modal-body').width(width);
    trg.find('.modal-body').height(height);
    trg.css({"margin-left": -width/2, "margin-top": -height/2 - 30});

    $(trg).modal({remote: url, backdrop: 'static'});
}

function windowClosed(val) {
	var post = "xml=1&trg=frm&cmd=wcl&val=" + val;
	var req = createAsync();
	var func = "";
	sendAsyncTsk(req, post, func, true);
}

function nextStep(obj, msg) {
	obj = obj.parentNode;
	var flowId = obj.id;
	var c = (!askConfirm) ? true : confirm(msg);
	if (c) {
		disableRow(obj);
		var post = "xml=1&trg=srv&cmd=nst&id=" + flowId;
		var req = createAsync();
		var func = "showResult";
		sendAsyncTsk(req, post, func, true);
	}
}

function showResult(req) {
	var d = document;
	var responseTag = req.responseXML.getElementsByTagName("r")[0];
	if (responseTag != null) {
		var tags  = responseTag.getElementsByTagName("optionPane");
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

			tags = responseTag.getElementsByTagName("deleted");
			var i = 0;
			if (tags != null && tags.length > 0) {
				var rows = tags[0].getElementsByTagName("row");
				var table = d.getElementById("taskTable");
				var tbody = table.getElementsByTagName('tbody')[0];
				for (i = 0; i < rows.length; i++) {
					var rowTag = rows[i];
					var rowId = rowTag.getAttribute("id");
					var row = d.getElementById(rowId);
					if (row != null)
						tbody.deleteRow(row.rowIndex);
				}
			}

			var rows = responseTag.getElementsByTagName("tr");
			if (rows != null && rows.length > 0) {
					var table = d.getElementById("taskTable");
					var tbody = table.getElementsByTagName('tbody')[0];
					var selCol = table.getAttribute("selectedCol");
					var oldCol = parseInt(selCol);

					for (i = 0; i < rows.length; i++) {
						var rowTag = rows[i];
						var rowId = rowTag.getAttribute("id");

						var row = d.getElementById(rowId);
						if (row != null) {
							var cname = rowTag.getAttribute("class");

							if (cname == "selected") {
								removeSelection(tbody, oldCol);
							}

							row.className = cname;

							row.height = rowTag.getAttribute("height");
							var st = rowTag.getAttribute("style");
							if (st != null) {
								row.style.cssText = st;
							}

							var strOnClick = rowTag.getAttribute("onclick");
							row.onclick = function(event) {
								eval(strOnClick);
							};
							var tdTags = rowTag.getElementsByTagName("td");

							var msg1 = "";
							var msg2 = "";
							for (var j = 0; j < tdTags.length; j++) {
								var tdTag = tdTags[j];
								var td = row.cells[j];
								td.onclick = null;
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
										} else if (tdTag.attributes[k].nodeValue.indexOf("killProcess") > -1) {
											type = 2;
											var b1 = tdTag.attributes[k].nodeValue.indexOf("'");
											var b2 = tdTag.attributes[k].nodeValue.indexOf("'", b1 + 1);
											msg2 = tdTag.attributes[k].nodeValue.substring(b1 + 1, b2);
										} else if (tdTag.attributes[k].nodeValue.indexOf("openControlInterface") > -1) {
											type = 3;
										} else if (tdTag.attributes[k].nodeValue.indexOf("showProcessReport") > -1) {
											type = 4;
										} else if (tdTag.attributes[k].nodeValue.indexOf("showFastReport") > -1) {
											type = 5;
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
										} else if (type == 4) {
											td.onclick = function() {
												eval("showProcessReport(this);");
											};
										} else if (type == 5) {
											td.onclick = function() {
												eval("showFastReport(this);");
											};
										}
									} else {
										td[tdTag.attributes[k].nodeName] =
											tdTag.attributes[k].nodeValue;
									}
								}
								
								var oldchildren = td.childNodes;
								var m = 0;
								for (m = oldchildren.length - 1; m>=0; m--) {
									var child = oldchildren[m];
									if (child.nodeType == 1 || child.nodeType == 3) {
										td.removeChild(child);
									}
								}
								
								var	children = tdTag.childNodes;
								var n = 0;
								for (n = 0; n < children.length; n++) {
									var child = children[n];
									var chres = getElementNode(d, child);
									if (chres != null) {
										td.appendChild(chres);
									}
								}
							}
						}
					}
			}

	}
}

function nextStep2(id, opt) {
	var post = "xml=1&trg=srv&cmd=nst&id=" + id + "&opt=" + opt;
	var req = createAsync();
	var func = "showResult";
	sendAsyncTsk(req, post, func, true);
}

function killProcess(obj, msg) {
  if (obj != null && obj.parentNode != null) {
		obj = obj.parentNode;

		var flowId = obj.id;

		var c = (!askConfirm) ? true : confirm(msg);
		if (c) {
			disableRow(obj);
			var post = "xml=1&trg=srv&cmd=klp&id=" + flowId;
			var req = createAsync();
			var func = "";
			sendAsync(req, post, func, true);
		}
	}
}

function selectRow(e, curObj) {
	var src;
  
	if (curObj != null) {
		var rowId = curObj.getAttribute("id");
		var tbl = curObj.parentNode.parentNode;
		
		var selCol = tbl.getAttribute("selectedCol");
		var curCol = parseInt(selCol);
		var oldCol = parseInt(selCol);
		var rowChanged = false;
		var colChanged = false;

		if (!e) e = window.event;
		if (e.target) src = e.target;
		else if (e.srcElement) src = e.srcElement;

		if (src != null && src.nodeName.toLowerCase() == "td") {
			if (curCol != src.cellIndex) {
				curCol = src.cellIndex; 
				colChanged = true;
			}
			tbl.setAttribute("selectedCol", curCol);
		}
    
		if (curObj.className != "selected") {
			tbl.setAttribute("selectedRow", curObj.rowIndex - 1);
			rowChanged = true;
			var tbody = curObj.parentNode;
	    
			if (tbody != null) {
				removeSelection(tbody, oldCol);
				curObj.className = "selected";

				if (curCol > -1)
					curObj.cells[curCol].className = "selected";

			}
		} else {
			if (oldCol > -1)
				curObj.cells[oldCol].className = "";
			if (curCol > -1)
				curObj.cells[curCol].className = "selected";
		}

		if (rowChanged || colChanged) {
			var post = "xml=1&trg=srv&cmd=sct";
			if (rowChanged)
				post += "&id="+rowId;
			if (colChanged)
				post += "&col="+curCol;

			var req = createAsync();
			var func = "";
			sendAsyncTsk(req, post, func, true);
		}
	}
}

// Remove existing table rows
function removeSelection(tbody, col) {
	for (i=1; i < tbody.rows.length; i++) {
		tbody.rows[i].className = "notselected";
		if (col > -1) tbody.rows[i].cells[col].className = "";
	}
}

function removeSelection2(tbody, col, row) {
	if (row < tbody.rows.length - 1) {
		tbody.rows[row+1].className = "notselected";
		if (col > -1) tbody.rows[row+1].cells[col].className = "";
	}
}

function getSelectedRow(tbody) {
	for (i=1; i < tbody.rows.length; i++) {
		if (tbody.rows[i].className = "selected") return tbody.rows[i];
	}
	return null;
}

function getSelectedRow2(tbody, row) {
  if (row > tbody.rows.length - 2) row = tbody.rows.length - 2;
  if (row >= 0)
		return tbody.rows[row + 1];
	else
		return null;
}

function moveLeft(left) {
	var table = document.getElementById("taskTable");
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var selRow = table.getAttribute("selectedRow");

	var curRow = parseInt(selRow);
	var oldRow = parseInt(selRow);
	var curObj = getSelectedRow2(tbody, curRow);

	if (curObj == null) return true;

	var oldCol = parseInt(selCol);
	var curCol = oldCol;
	var colsCount = curObj.getElementsByTagName("td").length;
	if (left) {
		curCol = (curCol > 0) ? curCol - 1 : colsCount - 1;
	} else {
		curCol = (curCol < colsCount - 1) ? curCol + 1 : 0;
	}

	var colChanged = true;
	table.setAttribute("selectedCol", curCol);

	if (oldCol > -1)
		curObj.cells[oldCol].className = "";
	if (curCol > -1)
		curObj.cells[curCol].className = "selected";

	var curCell = curObj.cells[curCol];

	var leftPos = curCell.offsetLeft;
	var leftVis = sLeft();
    
	if (leftVis > leftPos) setSLeft(leftPos);
	var rightPos = curCell.offsetLeft + curCell.offsetWidth;
	var rightVis = sLeft() + pWidth();
	if (rightVis < rightPos) setSLeft(rightPos - pWidth());

	var rowChanged = false;

	if (rowChanged || colChanged) {
		var post = "xml=1&trg=srv&cmd=sct";
		if (colChanged)
			post += "&col="+curCol;

		var req = createAsync();
		var func = "";
		sendAsyncTsk(req, post, func, true);
	}
	return false;
}

function moveUp(up, rowNum) {
	var table = document.getElementById("taskTable");
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var selRow = table.getAttribute("selectedRow");

	var oldRow = parseInt(selRow);
	var curRow = oldRow;

	if (rowNum > -1) {
		curRow = rowNum;
	} else if (up) {
		curRow = (curRow > 0) ? curRow - 1 : curRow;
	} else {
		var rowsCount = tbody.getElementsByTagName("tr").length - 1;
		curRow = (curRow < rowsCount - 1) ? curRow + 1 : curRow;
	}

	var curObj = getSelectedRow2(tbody, curRow);

	if (curObj == null) return true;

	var curCol = parseInt(selCol);
	var oldCol = parseInt(selCol);

	var rowChanged = false;

	if (curRow != oldRow) {
		table.setAttribute("selectedRow", curRow);
		rowChanged = true;

		removeSelection2(tbody, oldCol, oldRow);
		curObj.className = "selected";

		if (curCol > -1) {
			curObj.cells[curCol].className = "selected";
		}

		var upPos = curObj.offsetTop;
		if (searching) upPos -= curObj.offsetHeight + 10;
		var upVis = sTop();
		if (upVis > upPos) setSTop(upPos);

		var downPos = curObj.offsetTop + curObj.offsetHeight;
		var downVis = sTop() + pHeight();
		if (downVis < downPos) setSTop(downPos - pHeight());

		if (searching) {
			var sfd = document.getElementById("sfd");
			sfd.style.top = table.offsetTop + sTop() + 10;
		}
	}

	if (rowChanged) {
		var post = "xml=1&trg=srv&cmd=sct";
		if (rowChanged)
			post += "&id="+curRow;

		var req = createAsync();
		var func = "";
		sendAsyncTsk(req, post, func, true);
		return false;
	}
	else return true;
}

function scrollToVisible() {
	var table = document.getElementById("taskTable");
	if (table != null) {
		var tbody = table.getElementsByTagName('tbody')[0];
	
		var selCol = table.getAttribute("selectedCol");
		var selRow = table.getAttribute("selectedRow");
	
		var curRow = parseInt(selRow);
	
		var curObj = getSelectedRow2(tbody, curRow);
	
		if (curObj == null) return;
	
		var curCol = parseInt(selCol);
	
		var upPos = curObj.offsetTop;
		upPos -= curObj.offsetHeight + 10;
		var upVis = sTop();
		if (upVis > upPos) setSTop(upPos);
	
		var downPos = curObj.offsetTop + curObj.offsetHeight;
		var downVis = sTop() + pHeight();
		if (downVis < downPos) setSTop(downPos - pHeight());
	}
}

function pHeight() {
	return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
}

function sTop() {
	return (document.documentElement.scrollTop || document.body.scrollTop);
}

function setSTop(st) {
	document.documentElement.scrollTop = st;
	document.body.scrollTop = st;
}

function pWidth() {
	return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
}

function sLeft() {
	return (document.documentElement.scrollLeft || document.body.scrollLeft);
}

function setSLeft(st) {
	document.documentElement.scrollLeft = st;
	document.body.scrollLeft = st;
}

function attachKeydownHandlerTT() {
	if (document.addEventListener){
		document.addEventListener('keypress', onkeydownHandlerTT, true);
		window.addEventListener('scroll', onscrollHandlerTT, true);
		document.getElementById("sfd").addEventListener('keyup', searchFromBegin, true); 
	} else if (document.attachEvent){
  	//IE style
		document.attachEvent('onkeydown', onkeydownHandlerTT);
		window.attachEvent('onscroll', onscrollHandlerTT);
		document.getElementById("sfd").attachEvent('onkeyup', searchFromBegin); 
	}
	scrollToVisible();
}

var searching = false;

function onscrollHandlerTT(ev) {
 	if (searching) {
		var sfd = document.getElementById("sfd");
 		sfd.style.top = sTop() + 10;
	}
}

function onkeydownHandlerTT(ev) {
	if ($('#modalAuto').is(":visible") || $('#modalDlg').is(":visible")) return true;
	
	ev = ev || window.event; // for IE
  var charCode = ev.which || ev.keyCode;
  if (charCode > 36 && charCode < 41) {
		var res = false;
		if (charCode == 37) res = moveLeft(true);
		else if (charCode == 38) res = moveUp(true, -1);
		else if (charCode == 39) res = moveLeft(false);
		else if (charCode == 40) res = moveUp(false, -1);

		if (!res) {
	  	// cancelBubble is supported by IE - this will kill the bubbling
		// process.
			ev.cancelBubble = true;
   		ev.returnValue = false;

			// e.stopPropagation works only in Firefox.
		 	if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
  } else if (searching && (charCode == 13 || charCode == 27)) {
  	hideSearch();
  	return false;
  } else if (searching && (charCode == 34 || charCode == 33)) {
  	return false;
	} else {
		if (!searching) {
		  if (isChar(charCode)) {
				showSearch();
      	var sfd = document.getElementById("sfd");
				sfd.focus();
			}
		}
	}
  return true;
}

function isChar(code) {
	return code == 32 || (code >= 48 && code <=90) || (code >= 96 && code <=111) || code >= 186;
}

function showSearch() {
	searching = true;
	var sfd = document.getElementById("sfd");

	var table = document.getElementById("taskTable");
	if (table != null) {
		var tbody = table.getElementsByTagName('tbody')[0];

		var selCol = table.getAttribute("selectedCol");

		var curObj = tbody.rows[0];
		var curCol = parseInt(selCol);
		if (curCol == -1) curCol = 1;
	
		var curCell = curObj.cells[curCol];
		
		sfd.style.display = 'block';
		sfd.style.visibility = 'visible';
		sfd.style.left = curCell.offsetLeft + 3;
		sfd.style.top = curCell.offsetTop + sTop() + 10;
		sfd.style.width = curCell.offsetWidth - 6 + "px";
		sfd.style.height = "20px";
		sfd.value = "";
	
		sfd.onblur = function() {
			hideSearch();
		};
	}
	return false;
}

function searchFromBegin(e) {
	var sfd = document.getElementById("sfd");
	var str = sfd.value;

	e = e || window.event; // for IE
  var charCode = e.which || e.keyCode;

  if (charCode > 36 && charCode < 41) return true;

  var next = false;
	if (charCode == 34) next = true;
  var prev = false;
	if (charCode == 33) prev = true;

	if (searching && str != null && str.length > 0) {
		str = str.toLowerCase();
		var table = document.getElementById("taskTable");
  	var tbody = table.getElementsByTagName('tbody')[0];

		var selCol = table.getAttribute("selectedCol");
		var curCol = parseInt(selCol);
		if (curCol == -1) curCol = 1;

		var selRow = table.getAttribute("selectedRow");

		var curRow = parseInt(selRow) + 1;

		if (prev) {

			var k = curRow;

			for (i = k - 1; i>0; i--) {
				var cell = tbody.rows[i].cells[curCol];
				var txt = cell.innerHTML.toLowerCase();
				if (txt.length >= str.length && txt.substring(0, str.length) == str) {
					sfd.style.color = "black";
					break;
				}
			}
			if (i == 0 && k >= 0) {
				for (i = tbody.rows.length - 1; i > k; i--) {
					var cell = tbody.rows[i].cells[curCol];
					var txt = cell.innerHTML.toLowerCase();
					if (txt.length >= str.length && txt.substring(0, str.length) == str) {
						sfd.style.color = "black";
						break;
					}
				}
			}

			if (i == 0) {
				sfd.style.color = "red";
			} else if (i != k) {
		  	moveUp(true, i - 1);
			}
		} else {
			var k = (next) ? curRow : 0;

			for (i = k + 1; i < tbody.rows.length; i++) {
				var cell = tbody.rows[i].cells[curCol];
				var txt = cell.innerHTML.toLowerCase();
				if (txt.length >= str.length && txt.substring(0, str.length) == str) {
					sfd.style.color = "black";
					break;
				}
			}
			if (i == tbody.rows.length && k > 1) {
				for (i = 1; i < k; i++) {
					var cell = tbody.rows[i].cells[curCol];
					var txt = cell.innerHTML.toLowerCase();
					if (txt.length >= str.length && txt.substring(0, str.length) == str) {
						sfd.style.color = "black";
						break;
					}
				}
			}
			if (i == tbody.rows.length) {
				sfd.style.color = "red";
			} else if (i != k) {
			  moveUp(true, i - 1);
			}
		}
	}
	if (searching && (next || prev)) {
		return false;
	}
	return true;
}

function hideSearch() {
 	searching = false;
	var sfd = document.getElementById("sfd");

  sfd.style.display = 'none';
  sfd.style.visibility = 'hidden';

	return false;
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
  var w = parent.parent.parent;
  if (w != null && w.disableWindow) {
  	w.disableWindow(msg, permanently, t);
  }
}

function enableWholeWindow(perform) {
  var w = parent.parent.parent;
  if (w != null && w.enableWindow) {
  	w.enableWindow(perform);
  }
}

function disableRow(row) {
	var td = row.cells[4];
	td.onclick = null;
	var imgTags = td.getElementsByTagName("img");
	if (imgTags != null && imgTags.length > 0) {
		var imgTag = imgTags[0];
		var s = imgTag.src;
		if (s.indexOf("Dis.gif") == -1) {
			imgTag.src = imgTag.src.substring(0, imgTag.src.length - 4) + "Dis.gif";
		}
	}
	
	td = row.cells[5];
	td.onclick = null;
	imgTags = td.getElementsByTagName("img");
	if (imgTags != null && imgTags.length > 0) {
		var imgTag = imgTags[0];
		var s = imgTag.src;
		if (s.indexOf("Dis.gif") == -1) {
			imgTag.src = imgTag.src.substring(0, imgTag.src.length - 4) + "Dis.gif";
		}
	}
	
	td = row.cells[6];
	td.onclick = null;
	imgTags = td.getElementsByTagName("img");
	if (imgTags != null && imgTags.length > 0) {
		var imgTag = imgTags[0];
		var s = imgTag.src;
		if (s.indexOf("Dis.gif") == -1) {
			imgTag.src = imgTag.src.substring(0, imgTag.src.length - 4) + "Dis.gif";
		}
	}
	
	td = row.cells[11];
	td.onclick = null;
	imgTags = td.getElementsByTagName("img");
	if (imgTags != null && imgTags.length > 0) {
		var imgTag = imgTags[0];
		var s = imgTag.src;
		if (s.indexOf("Dis.gif") == -1) {
			imgTag.src = imgTag.src.substring(0, imgTag.src.length - 4) + "Dis.gif";
		}
	}
}