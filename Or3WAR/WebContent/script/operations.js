var beginTimeHlb;
var waitTime = 8000;
var lastChar = "";
var tempHolder = null;
var tempHolderType = 0;

var keyPath = null;
var keyPass = null;
var signMsg = null;
var click = false;

function controlText(obj, e, length, re, upperAll, upperFirst, type) {
	if (obj != null && obj.disabled != true) {
		var code = e.keyCode;
		var notReplace = true;
		if (code == 27 || code == 13 || code == 9 || code == 8 || code == 46
				|| (code > 34 && code < 41))
			return true;
		if (length == 0 || obj.value.length < length) {

			var cur;
			if (code != 0) {
				cur = String.fromCharCode(e.keyCode);
			} else if (e.charCode != 0) {
				cur = String.fromCharCode(e.charCode);
			} else {
				cur = e.which;
			}
			if (upperAll || (upperFirst && obj.value.length == 0)) {
				if (code != 0 || e.charCode != 0) {
					obj.value = obj.value + cur.toUpperCase();
					notReplace = false;
				}
			}

			if (type != null) {
				if (type == 0) { // TextField
					// ничего не делаем
				} else if (type == 1) { // IntField
					var C = /[0-9\x25\x27\x24\x23]/;
					var a = e.which;
					var c = String.fromCharCode(a);
					return !!(a == 0 || a == 8 || a == 9 || a == 13 || c
							.match(C));
				} else if (type == 2) { // FloatField

				}
			}
			if (re != null && re.length > 0) {
				var sym = eval(re);
				if (sym.test(cur)) {
					return true && notReplace;
				}
			} else {
				return true && notReplace;
			}
		}
	}
	e.cancelBubble = true;
	e.returnValue = false;

	if (e.stopPropagation) {
		e.stopPropagation();
		e.preventDefault();
	}
	return false;
}

function moveStart() 
{
	var range = document.selection.createRange();
	range.move('textedit', -1);
	if (lastChar.length > 0) {
		range.move('character', 1);
		lastChar = "";
	}
	range.select();
}

function formatDate (obj, e) {
	if (obj == null || obj.disabled == true) return false;
	var code = e.keyCode; 
	if (code == 27 || code == 13 || code == 9) return true;

	if (code >= 35 && code <= 40) return true;

	var str = obj.value;
	var range = document.selection.createRange();
	if (str.length == 10) {
		var d = -range.moveStart('character',-11);

		var cur = String.fromCharCode(e.keyCode);

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<10) {
			if (d==2 || d==5) d++;

			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4) d++;
			obj.value = str;
			range.move('character', d+1);
			range.select();
		}

		if (code==8 && d>0) {
			if (d==3 || d==6) d--;
			var f = "г";
			if (d<3) f="д";
			else if (d<6) f = "м";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7) d--;

			obj.value = str;
			range.move('character', d-1);
			range.select();
		}
	} else if (str.length == 16) {
		var d = -range.moveStart('character',-17);

		var cur = String.fromCharCode(e.keyCode);

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<16) {
			if (d==2 || d==5 || d == 10 || d == 13) d++;
			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4 || d == 9 || d == 12) d++;
			obj.value = str;
			range.move('character', d+1);
			range.select();
		}

		if (code==8 && d>0) {
			if (d==3 || d==6 || d == 11 || d == 14) d--;
			var f = "М";
			if (d<3) f="д";
			else if (d<6) f = "м";
			else if (d<11) f = "г";
			else if (d<14) f = "ч";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7 || d == 12 || d == 15) d--;

			obj.value = str;
			range.move('character', d-1);
			range.select();
		}
	} else if (str.length == 8) {
		var d = -range.moveStart('character',-9);

		var cur = String.fromCharCode(e.keyCode);

		if (((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) && d<8) {
			if (d==2 || d==5) d++;

			var c = 0;
			if (code <=57) c = code - 48;
			else c = code - 96;

			str = str.substring(0,d) + c + str.substring(d+1, str.length);
			if (d==1 || d==4) d++;
			obj.value = str;
			range.move('character', d+1);
			range.select();
		}

		if (code==8 && d>0) {
			if (d==3 || d==6) d--;
			var f = "с";
			if (d<3) f="ч";
			else if (d<6) f = "М";
			str = str.substring(0,d-1) + f + str.substring(d, str.length);
			if (d==4 || d==7) d--;

			obj.value = str;
			range.move('character', d-1);
			range.select();
		}
	}
	e.cancelBubble = true;
	e.returnValue = false;

	if (e.stopPropagation) {
		e.stopPropagation();
		e.preventDefault();
	}
	return false;
}

function retryNow() {
	if (timeout1 != null) {
		clearTimeout(timeout1);
		timeout1 = setTimeout(timeoutFunction1, 100);
	}
}

function textChanged(selector, id, row, col) {
	if (selector == null || selector.disabled == true) return;
	var val = "";

	if (selector.type == "checkbox") {
		val = selector.checked;
	} else if (selector.nodeName == "DIV") {
		val = encodeURIComponent(selector.innerHTML);
	} else {
		val = encodeURIComponent(selector.value);
	}

	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val="+val;
	if (row != null && col != null) {
		post += "&row="+row+"&col="+col;
		var td = selector.parentNode;
		if (selector.type == "checkbox") {
			td.innerHTML = "<span>" + selector.value + "</span>";
		} else if (selector.tagName != null && selector.tagName.toLowerCase() == "select") {
			enableSelection(document.body);
			td.innerHTML = "<span>" + selector.options[selector.selectedIndex].text + "</span>";
		}
	}

	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function getXmlByParams(params) {
	var post = "xml=1&" + params;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function openInterfaceHlb(obj, tid, row, col) {
	var post = "xml=1&trg=frm&cmd=accm";
	var req = createAsync();
	var func = "hiperLabelCanCommit";
	sendAsync(req, post, func, true, new Array(obj,tid,row,col));
}

var objM;
var tidM;
var rowM;
var colM;

function hiperLabelCanCommit(req, param) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			if (param) {
				objM = param[0];
				tidM = param[1];
				rowM = param[2];
				colM = param[3];
			} 
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("hasErrors");
			var mtags = responseTag.getElementsByTagName("message");
			if (tags != null && tags.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=aerr";
				if (param){
					url += "&hl='1'";
				}
				if (url.indexOf("/") > 0){
					url = "/" + url;
				}
				url += "&noCache=" + (new Date).getTime();
				$('#errorModal').modal({remote: url, backdrop: 'static', keyboard: true});
			} else if (mtags != null && mtags.length > 0) {
				var mtag = mtags[0];
				var message = mtag.childNodes[0].nodeValue;
				alert(message);
			} else if (responseTag.childNodes.length == 0) {
				hiperLabelForward(param);
			}
		}
	}
}


function hiperLabelForward(param) {
	pleaseWait(currentLang=="kz"?"Интерфейстың ашылуы...":"Открытие интерфейса...");
	var obj;
	var tid;
	var row;
	var col;
	if (param) {
		obj = param[0];
		tid = param[1];
		row = param[2];
		col = param[3];
	} else {
		obj = objM;
		tid = tidM;
		row = rowM;
		col = colM;
	}

	var endTime = (new Date).getTime();
	if (beginTimeHlb == null || endTime - beginTimeHlb > waitTime) {
		beginTimeHlb = endTime;
		if (tid != null && row != null && col != null) {
			var url = location.pathname + "?trg=frm&cmd=hlb&id=" + tid
					+ "&row=" + row + "&col=" + col;
			if (url.indexOf("/") > 0)
				url = "/" + url;
			url += "&noCache=" + (new Date).getTime();
			document.location.assign(url);
		} else {
			obj = obj.parentNode;
			var flowId = obj.id;
			var url = location.pathname + "?trg=frm&cmd=hlb&id=" + flowId;
			if (url.indexOf("/") > 0)
				url = "/" + url;
			url += "&noCache=" + (new Date).getTime();
			document.location.assign(url);
		}
	}

}

function popupPressed2(obj) {
	if ("td" == obj.tagName.toLowerCase()) {
		var col = obj.cellIndex;
		obj = obj.parentNode;
		var row = obj.rowIndex - 1;
		obj = obj.parentNode.parentNode.parentNode;
		var tid = obj.id;
		var url = location.pathname + "?trg=frm&cmd=pops&id=" + tid + "&row="+row+"&col="+col;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		var value = window.showModalDialog(url, "Popup", "dialogWidth:850px;dialogHeight:720px");
		if (value != null && value != "undefined")
			popupClosed(tid, value, row, col);
	} else {
		obj = obj.parentNode;
		var id = obj.id;
		var url = location.pathname + "?trg=frm&cmd=pops&id=" + id;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		var value = window.showModalDialog(url, "Popup", "dialogWidth:850px;dialogHeight:720px");
		if (value != null && value != "undefined") 
			popupClosed(id, value);
	}
}

function deselectExcept(curObj) {
	var rowObj = curObj.parentNode;
	var table = curObj.parentNode.parentNode.parentNode;
	var zebra1 = table.getAttribute("zebra1");
	var zebra2 = table.getAttribute("zebra2");
	var tbody = table.getElementsByTagName("tbody")[0];

	for (i=0; i < tbody.rows.length; i++) {
		var zebra = null;
		if (i % 2 != 0) {
			zebra = zebra1;
		} else {
			zebra = zebra2;
		}
		tbody.rows[i].className = "notselected";
		if (zebra != null) {
			tbody.rows[i].style.backgroundColor = zebra;
		}
		var cellTags = tbody.rows[i].getElementsByTagName("td");
		var td = cellTags[0];
		var inputObjs = td.getElementsByTagName("input");
		inputObjs[0].checked = false;
	} 

	if (tbody.rows.length > 0) {
		rowObj.className = "selected";
		rowObj.style.backgroundColor = '';
		var cellTags = rowObj.getElementsByTagName("td");
		var td = cellTags[0];
		var inputObjs = td.getElementsByTagName("input");
		inputObjs[0].checked = true;
	}

}

function popupPressed(obj) {
	var post = "";
	if ("td" == obj.tagName.toLowerCase()) {

		var col = obj.cellIndex;
		obj = obj.parentNode;
		if (obj != null && obj.className == "selected") {
			var row = obj.rowIndex - 1;
			obj = obj.parentNode.parentNode;
			var tid = obj.id;
			if (tid == null || tid.length < 4) return;
			tid = tid.substring(3);
			post = "trg=frm&cmd=pops&id=" + tid + "&row="+row+"&col="+col+"&xml=1";
		}
	} else {
		obj = obj.parentNode;
		var id = obj.id;
		post = "trg=frm&cmd=pops&id=" + id+"&xml=1";
	}

	if (post.length > 0) {
		pleaseWait();

		var req = createAsync();
		var func = "processPopupPressed";
		sendAsync(req, post, func, false);
	}
}

function mapPressed(obj, ind) {
	obj = obj.parentNode.parentNode;
	var id = obj.id;
	var post = "trg=frm&cmd=maps&id=" + id+"&xml=1&aid="+ind;

	var req = createAsync();
	var func = "processMapPressed";
	sendAsync(req, post, func, true);
}

function mapSelected(obj, ind) {
	obj = obj.parentNode.parentNode;
	var id = obj.id;
	var post = "trg=frm&cmd=mapsel&id=" + id+"&xml=1&aid="+ind;

	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function processPopupPressed(req)
{
	var d = document;
	//beginTime = null;

	var responseTag = req.responseXML.getElementsByTagName("r")[0];

	var tags = responseTag.getElementsByTagName("id");

	if (tags == null || tags.length == 0) return;
	var tag = tags[0];

	var id = tag.childNodes[0].nodeValue;
	tag = responseTag.getElementsByTagName("fid")[0];
	var fid = tag.childNodes[0].nodeValue;
	tag = responseTag.getElementsByTagName("width")[0];
	var width = tag.childNodes[0].nodeValue;
	tag = responseTag.getElementsByTagName("height")[0];
	var height = tag.childNodes[0].nodeValue;
	tag = responseTag.getElementsByTagName("title")[0];
	var title = tag.childNodes[0].nodeValue;
	
	var h = parseInt(height) + 16;
	if (h < 100) h = document.body.clientHeight - 100;
	var w = parseInt(width);
	if (w < 101) w = document.body.clientWidth - 50;
	var tags = responseTag.getElementsByTagName("row");
	if (tags.length > 0) {
		var row = tags[0].childNodes[0].nodeValue;
		tag = responseTag.getElementsByTagName("col")[0];
		var col = tag.childNodes[0].nodeValue;

/* For jQuery
				var pop = $("#pop"+id).dialog({
									autoOpen: false,
									modal: true;
									width: w;
									height: h
									buttons: {
										"Create an account": function() {
																						popupClosed(id, 'YES');
																				 },
										"Cancel": function() {
																						popupClosed(id, 'NO');
																				 }
								 	}
		 		});
*/
		var url = location.pathname + "?trg=frm&cmd=pop&id=" + id + "&fid="+fid+"&row="+row+"&col="+col;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();

 	    var trg = $('#modal' + fid);
 	    $('#dhead' + fid).text(title);
 	    
 	    trg.find('.modal-body').width(w);
 	    trg.find('.modal-body').height(h);
 	    trg.css({"margin-left": -w/2, "margin-top": -h/2 - 30});

 	    $(trg).modal({remote: url, backdrop: 'static'});
	} else {
		var url = location.pathname + "?trg=frm&cmd=pop&id=" + id + "&fid=" + fid;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		
 	    var trg = $('#modal' + fid);
 	    $('#dhead' + fid).text(title);

 	    trg.find('.modal-body').width(w);
 	    trg.find('.modal-body').height(h);
 	    trg.css({"margin-left": -w/2, "margin-top": -h/2 - 30});

 	    $(trg).modal({remote: url, backdrop: 'static'});
	}
}

function processMapPressed(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];

			var tags = responseTag.getElementsByTagName("id");
			if (tags == null || tags.length == 0) return;
			var tag = tags[0];
			var id = tag.childNodes[0].nodeValue;
			tag = responseTag.getElementsByTagName("fid")[0];
			var fid = tag.childNodes[0].nodeValue;
			tag = responseTag.getElementsByTagName("width")[0];
			var width = tag.childNodes[0].nodeValue;
			tag = responseTag.getElementsByTagName("height")[0];
			var height = tag.childNodes[0].nodeValue;
			var h = parseInt(height) + 73;
			if (h < 153) h = document.body.clientHeight;
			var w = parseInt(width) + 21;
			if (w < 121) w = document.body.clientWidth;

			var url = location.pathname + "?trg=frm&cmd=map&id=" + id + "&fid=" + fid;
			if (url.indexOf("/") > 0) url = "/" + url;
			url += "&noCache=" + (new Date).getTime();
			var value = window.showModalDialog(url, "Popup", "dialogWidth:" +w+"px;dialogHeight:"+h+"px");
			if (value != null && value != "undefined")
				popupClosed(id, value);
		}
	}            
}

function deletePressed(obj) {
	obj = obj.parentNode;
	var id = obj.id;
	var post = "xml=1&trg=frm&cmd=pcl&id="+id+"&val=DEL";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function sortColumn(id, col) {
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&com=sort&col="+col;
	
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function removeSort(id, col) {
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&com=rsort&col="+col;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function addRow(id) {
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&com=add";
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function addNodeTitle(id, title) {
	var post = "xml=1&trg=frm&cmd=addtl&id="+id+"&val="+title;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function processAddRow(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var changeTags = responseTag.childNodes;
			for (ch = 0; ch < changeTags.length; ch++) {
				var changeTag = changeTags[ch];
				if (changeTag.nodeType == 1) {
					var name = changeTag.nodeName.toLowerCase();
					if ("needTitle" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;

						var obj = d.getElementById(id);
						tag = changeTag.getElementsByTagName("value")[0];

						var url = location.pathname + "?trg=frm&cmd=addnt";
						if (url.indexOf("/") > 0) url = "/" + url;
						url += "&noCache=" + (new Date).getTime() + "&";
						fr = window.open(url, 'NeedTitlePopupFrame', 'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');  
						fr.moveTo(screen.availWidth/2-310, screen.availHeight/2-225);

					} else if ("visible" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;

						var obj = d.getElementById(id);
						tag = changeTag.getElementsByTagName("value")[0];

						if(obj != null) {
							if (obj.tagName.toLowerCase() == "div" ||
									obj.tagName.toLowerCase() == "table") {

								if (tag.childNodes.length > 0) {
									var val = "";
									for (g = 0; g<tag.childNodes.length; g++) {
										var childTag = tag.childNodes[g];
										val = val + getElementXml(childTag) + "\r\n";
									}
									if (tag.xml) {
										obj.outerHTML = val;
									} else {
										var k1 = val.indexOf('>') + 1;
										var k2 = val.lastIndexOf("</");
										if (k1 > 0) val = val.substring(k1, k2);
										obj.innerHTML = val;
									}
								} else {
									obj.innerHTML = "";
								} 
							}
						}
					} else if ("change" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;

						var obj = d.getElementById(id);
						tag = changeTag.getElementsByTagName("value")[0];

						if(obj != null) {
							if (obj.tagName.toLowerCase() == "div" ||
									obj.tagName.toLowerCase() == "span") {

								if (tag.childNodes.length > 0) {
									var val = "";
									for (g = 0; g<tag.childNodes.length; g++) {
										var childTag = tag.childNodes[g];
										val = val + getElementXml(childTag) + "\r\n";
									}
									obj.innerHTML = val;
								} else {
									obj.innerHTML = "";
								} 
							} else {
								var val = "";

								if (tag.childNodes.length > 0)
									val = tag.childNodes[0].nodeValue;

								if (obj.type == "checkbox" ||
										obj.type == "radio") {
									obj.checked = (val == "true");
								} else {
									obj.value = val;
								}
							}
						}
					} else if ("changemap" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;

						//var objmap = d.getElementById("im" + id);
						var objsel = d.getElementById("is" + id);

						//tag = changeTag.getElementsByTagName("imgSrc")[0];

						//if(objmap != null && tag != null && tag.childNodes != null && tag.childNodes.length > 0) {
						//   var val = tag.childNodes[0].nodeValue;
						//   objmap.src = val;
						//}
						tag = changeTag.getElementsByTagName("imgSrc")[0];

						if(objsel != null && tag != null && tag.childNodes != null && tag.childNodes.length > 0) {
							var val = tag.childNodes[0].nodeValue;
							objsel.src = val;
						}
					}
					else if ("enable" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var obj = d.getElementById(id);
						tag = changeTag.getElementsByTagName("value")[0];

						if(obj != null) {
							if (obj.tagName.toLowerCase() == "div" ||
									obj.tagName.toLowerCase() == "span") {

								if (tag.childNodes.length > 0) {
									var val = "";
									for (g = 0; g<tag.childNodes.length; g++) {
										var childTag = tag.childNodes[g];
										val = val + getElementXml(childTag) + "\r\n";
									}
									obj.innerHTML = val;
								    $(obj).find("[actionId]").click(function () {
								    	makeAction($(this));
								    });
								} else {
									obj.innerHTML = "";
								} 
							} else if (obj.tagName.toLowerCase() == "img") {
								var val = false;
								if (tag.childNodes.length > 0 && tag.childNodes[0].nodeValue == "false")
									val = true;
								if (val) {
									obj.style.opacity = 0.35;
									obj.style.filter = "alpha(opacity:35)";
								} else {
									obj.style.cssText = '';
								}

							} else {
								var val = false;
								if (tag.childNodes.length > 0 && tag.childNodes[0].nodeValue == "false")
									val = true;

								if (obj.tagName.toLowerCase() == "textarea" ||
										(obj.tagName.toLowerCase() == "input" &&
												obj.getAttribute("type") == "text")) {
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
					}
					else if ("model" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var opTags = changeTag.getElementsByTagName("option");

						var obj = d.getElementById(id);
						if(obj != null) {
							var oldOps = obj.getElementsByTagName("option");
							for (j = oldOps.length - 1; j >= 0; j--) {
								var oldOp = oldOps[j];
								obj.removeChild(oldOp);
							}

							for (j = 0; j < opTags.length; j++) {
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

							if (obj.getAttribute("size") != null && obj.getAttribute("size") > 0) {
								obj.style.display = 'block';
								obj.style.display = 'none';
							}

							while (obj.tagName.toLowerCase() != "body") {

								obj = obj.parentNode;
								if (obj.tagName.toLowerCase() == "ul" &&
										obj.className == "Hidden") {
									obj.className = "";
									obj.className = "Hidden";
								}
							}
						}
					}
					else if ("tablesort" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var tbodyTag = changeTag.getElementsByTagName("thead")[0];
						var tbodyClone = tbodyTag.cloneNode(true);
						var rowTags = tbodyTag.getElementsByTagName("tr");

						var obj = d.getElementById(id);
						if(obj != null) {
							var tables = obj.getElementsByTagName("table");
							var table;
							if (tables.length > 0) table = tables[tables.length - 1];
							var tbody = table.getElementsByTagName("thead")[0];
							var rows = tbody.getElementsByTagName("tr");

							for (j = 0; j < rowTags.length; j++) {
								var rowTag = rowTags[j];
								var row = rows[j];

								var cellTags = rowTag.getElementsByTagName("th");
								for (k=0; k<cellTags.length; k++) {
									var cellTag = cellTags[k];

									var cell = row.cells[k];

									var oldchildren = cell.childNodes;
									var m = 0;
									for (m = oldchildren.length - 1; m>=0; m--) {
										var child = oldchildren[m];
										if (child.nodeType == 1 || child.nodeType == 3) {
											cell.removeChild(child);
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
						}
					}
					else if ("tabledata" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var tbodyTag = changeTag.getElementsByTagName("tbody")[0];
						var tbodyClone = tbodyTag.cloneNode(true);
						var rowTags = tbodyTag.getElementsByTagName("tr");

						var obj = d.getElementById(id);
						if(obj != null) {
							var tables = obj.getElementsByTagName("table");
							var table;
							if (tables.length > 0) table = tables[tables.length - 1];

							var sTags = changeTag.getElementsByTagName("selectedRows");
							if (sTags != null && sTags.length > 0) {
								var selsString = sTags[0].childNodes[0].nodeValue;
								table.setAttribute("selectedRows", selsString);
							//	console.log(selsString+"|1");
							}

							var tbody = table.getElementsByTagName("tbody")[0];

							clearTable(tbody);

							for (j = 0; j < rowTags.length; j++) {
								var rowTag = rowTags[j];
								var row = tbody.insertRow(tbody.rows.length);
								var rowId = rowTag.getAttribute("id");
								row.id = rowId;
								row.setAttribute("id", rowId);
								row.className = rowTag.getAttribute("class");
								row.height = rowTag.getAttribute("height");
								var st = rowTag.getAttribute("style");
								if (st != null) {
									row.style.cssText = st;
								}

								var strOnClick = rowTag.getAttribute("onclick");

								row.onclick = function(event) {
									eval(strOnClick);
								};

								var cellTags = rowTag.getElementsByTagName("td");
								for (k=0; k<cellTags.length; k++) {
									var cellTag = cellTags[k];
									var cell = row.insertCell(row.cells.length);

									for (ak = 0; ak < cellTag.attributes.length; ak++) {
										if (cellTag.attributes[ak].nodeName == "style") {
											cell.style.cssText = cellTag.attributes[ak].nodeValue;
										} else if (cellTag.attributes[ak].nodeName == "onclick") {
											var tdOnClick = cellTag.getAttribute("onclick");
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
										} else if (cellTag.attributes[ak].nodeName == "checked") {
											cell.checked = (cellTag.attributes[ak].nodeValue == "1");
										} else {
											cell[cellTag.attributes[ak].nodeName] =
												cellTag.attributes[ak].nodeValue;
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
						}
					} else if ("deleted" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var obj = d.getElementById(id);
						var tables = obj.getElementsByTagName("table");
						var table;
						if (tables.length > 0) table = tables[tables.length - 1];
						var rows = changeTag.getElementsByTagName("tr");
						for (i = 0; i < rows.length; i++) {
							var rowTag = rows[i];
							var rowId = rowTag.getAttribute("id");
							var tbody = table.getElementsByTagName('tbody')[0];
							var row = getRowById(tbody, rowId);
							if (row != null) table.deleteRow(row.rowIndex);
						}
					} else if ("inserted" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;

						var obj = d.getElementById(id);
						var tables = obj.getElementsByTagName("table");
						var table;
						if (tables.length > 0) table = tables[tables.length - 1];
						var rows = changeTag.getElementsByTagName("tr");

						var zebra1 = table.getAttribute("zebra1");
						var zebra2 = table.getAttribute("zebra2");

						var tbody = table.getElementsByTagName('tbody')[0];
						for (i = 0; i < rows.length; i++) {
							var rowTag = rows[i];
							var rowId = rowTag.getAttribute("id");
							var row = d.createElement("TR");
							row.id = rowId;
							row.setAttribute("id", rowId);
							row.className = rowTag.getAttribute("class");
							
							var hh = rowTag.getAttribute("height");
							row.height = hh;

							var st = rowTag.getAttribute("style");
							if (st != null) {
								row.style.cssText = st;
							}

							var strOnClick = rowTag.getAttribute("onclick");
							if (navigator.appName.indexOf("Microsoft") == 0) {
								row.onclick = function() {
									eval(strOnClick);
								};
							} else {
								row.setAttribute("onclick", strOnClick);
							}

							var tdTags = rowTag.getElementsByTagName("td");

							for (j = 0; j < tdTags.length; j++) {
								var tdTag = tdTags[j];
								var td = d.createElement("TD");
								for (k = 0; k < tdTag.attributes.length; k++) {
									if (tdTag.attributes[k].nodeName == "style") {
										td.style.cssText = tdTag.attributes[k].nodeValue;
									} else if (tdTag.attributes[k].nodeName == "onclick") {
										var tdOnClick = tdTag.getAttribute("onclick");
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
										td[tdTag.attributes[k].nodeName] =
											tdTag.attributes[k].nodeValue;
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
								td.height = hh;
							}
							tbody.appendChild(row);

							var selRowsStr = table.getAttribute("selectedRows");
						//	console.log(selRowsStr+"  |01");
							if (selRowsStr == null || selRowsStr.length == 0) {
								if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
								else selRowsStr = "0";
							}

							var selRows = selRowsStr.split(',');
							removeSelectionIfc(tbody, zebra1, zebra2, selRows, rowId);

							table.setAttribute("selectedRows", "" + rowId);
						//	console.log(rowId+"|2");
							row.className = "selected";
						}
					} else if ("updated" == name) {
						var tag = changeTag.getElementsByTagName("id")[0];
						var id = tag.childNodes[0].nodeValue;
						var obj = d.getElementById(id);
						if (obj != null) {
							var tables = obj.getElementsByTagName("table");
							var table;
							if (tables.length > 0) table = tables[tables.length - 1];
							if (table != null) {
								var rows = changeTag.getElementsByTagName("tr");
								var tbody = table.getElementsByTagName('tbody')[0];

								var i = 0;
								
								while (i < rows.length) {
									var rowTag = rows[i];
									i++;
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
														var tdOnClick = tdTag.getAttribute("onclick");
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
														td[tdTag.attributes[k].nodeName] =
															tdTag.attributes[k].nodeValue;
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
						}
					} else if ("openfile" == name) {
						var fn = changeTag.childNodes[0].nodeValue;
						if (fn != null && fn.length > 0) {
							var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
							if (url.indexOf("/") > 0) url = "/" + url;
							url += "&noCache=" + (new Date).getTime();
							location.assign(url);
						}
					} else if ("viewfile" == name) {
						var fn = changeTag.childNodes[0].nodeValue;
						if (fn != null && fn.length > 0) {
							var url = location.pathname + "?trg=frm&cmd=opf&fn=" + encodeURIComponent(fn);
							if (url.indexOf("/") > 0) url = "/" + url;
							url += "&noCache=" + (new Date).getTime();
							location.assign(url);
						}
					} else if ("openwindow" == name) {
						var tag = changeTag.getElementsByTagName("address")[0];
						var address = tag.childNodes[0].nodeValue;
						tag = changeTag.getElementsByTagName("width")[0];
						var width = tag.childNodes[0].nodeValue;
						tag = changeTag.getElementsByTagName("height")[0];
						var height = tag.childNodes[0].nodeValue;

						if (address != null && address.length > 0) {
							var w = parseInt(width);
							var h = parseInt(height);
							fr = window.open(address, 'ReportFrame', 'directories=no,height='+h+',location=no,menubar=yes,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width='+w);
							fr.moveTo(screen.availWidth/2-w/2, screen.availHeight/2-h/2);
						}
					} else if ("sign" == name) {
						var changeTags = changeTag.childNodes;
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
					} else if ("close" == name) {
						var post = "xml=1&trg=frm&cmd=prvas";
						
						disableWholeWindow(currentLang=="kz"?"Электронды қолтаңбаның жасалуы...":"Формирование электронной подписи...", false);

						var reqIn = createAsync();
						var func = "processAfterSign";
						sendAsync(reqIn, post, func, false);

					} else if ("haserrors" == name) {
						var hasErrs = changeTag.childNodes[0].nodeValue;
						if (hasErrs != null && hasErrs=="true") {
							var url = location.pathname + "?trg=frm&cmd=signerr";
							if (url.indexOf("/") > 0) url = "/" + url;
							url += "&noCache=" + (new Date).getTime();
							fr = window.open(url, 'CommitPopupFrame', 'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');  
							fr.moveTo(screen.availWidth/2-310, screen.availHeight/2-225);
							pleaseWait(currentLang=="kz"?"Оқшауды алу үшін қателер терезесінде әрекетті таңдаңыз!":"Для снятия блокировки, выберите действие в окне ошибок!");
						}
					}
				}
			}
		}
	}
}
/*
function processAfterSign(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("ok");
			if (tags != null && tags.length > 0) {
				alert("Данные успешно подписаны и переданы на дальнейшую обработку!");

				disableWholeWindow("Процесс закончен. Можно выйти из программы...", false);

				if (parent)
					parent.close();

				return;
			}
		}
	}
}
*/
//Remove existing table rows
function clearTable(tbody) {
	while (tbody.rows.length > 0) {
		tbody.deleteRow(0);
	}
}
/*
function getRowById(tbody, rowId) {
//  var rid = parseInt(rowId);
//  if (tbody.rows.length > rid)
//	  return tbody.rows[rid];
//	else {
	  var row = null;
	  for (n = 0; n<tbody.rows.length; n++) {
  	  if (tbody.rows[n].id == rowId) {
    	  row = tbody.rows[n];
      	break;
	    }
  	}
	  return row;
// 	}
}
 */
function getRowById(tbody, rowId) {
	return tbody.rows[parseInt(rowId)];
}

/**
 * Удаление отметки выбора со строк
 * @param tbody
 * @param zebra1
 * @param zebra2
 * @param selRows
 * @param rowId
 */
function removeSelectionIfc(tbody, zebra1, zebra2, selRows, rowId) {
	for (var si = 0; si < selRows.length; si++) {
		var zebra = null;
		var row = getRowById(tbody, selRows[si]);
		if (row != null) {
			var rid = row.rowIndex;
			zebra = rid % 2 != 0 ? zebra1 : zebra2;
			if (selRows[si] != rowId) {
				row.className = "notselected";
				row.style.backgroundColor = zebra;
				for (var col = 0; col < row.getElementsByTagName("td").length; col++) {
					row.cells[col].className = "";
				}
				var chk = $(row).find('input[type=checkbox][id^=chb]');
				if (chk != null) {
					chk.removeAttr("checked");
				}
			}
		}
	}
}

function setRowSelected(row, tbody, arrObj) {
	//if (tbody.rows[row].className != "selected") {
	tbody.rows[row].className = "selected";
	tbody.rows[row].style.backgroundColor = '';
	arrObj[arrObj.length] = row;
	//}
}

//Remove existing table rows
function removeTreeSelection(obj) {
	var as = obj.getElementsByTagName("a");
	for (i=0; i < as.length; i++) {
		var child = as[i];
		if (child.className == "Current") {
			child.className = "";
		}
	}
}

function selectAll(curObj, id) {
	var sels = new Array();

	var table = curObj.parentNode.parentNode.parentNode.parentNode;
	var zebra1 = table.getAttribute("zebra1");
	var zebra2 = table.getAttribute("zebra2");
	var tbody = table.getElementsByTagName("tbody")[0];

	if(curObj.checked) {
		var k = 0;
		for (i=0; i < tbody.rows.length; i++) {
			tbody.rows[i].className = "selected";
			tbody.rows[i].style.backgroundColor = '';
			var cellTags = tbody.rows[i].getElementsByTagName("td");
			var td = cellTags[0];
			var inputObjs = td.getElementsByTagName("input");
			inputObjs[0].checked = true;
			sels[k] = i;
			k++;
		} 
	} else {
		if (tbody.rows.length > 0) {
			tbody.rows[0].className = "selected";
			tbody.rows[0].style.backgroundColor = '';
			var cellTags = tbody.rows[0].getElementsByTagName("td");
			var td = cellTags[0];
			var inputObjs = td.getElementsByTagName("input");
			inputObjs[0].checked = true;
			sels[0] = 0;
		}

		for (i=1; i < tbody.rows.length; i++) {
			var zebra = null;
			if (i % 2 != 0) {
				zebra = zebra1;
			} else {
				zebra = zebra2;
			}
			tbody.rows[i].className = "notselected";
			if (zebra != null) {
				tbody.rows[i].style.backgroundColor = zebra;
			}
			var cellTags = tbody.rows[i].getElementsByTagName("td");
			var td = cellTags[0];
			var inputObjs = td.getElementsByTagName("input");
			inputObjs[0].checked = false;
			sels[k] = i;
		} 

	}

	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val="+sels.join(',');
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function selectOneRow(inputObj) {
	var curObj = inputObj.parentNode.parentNode;

	if (curObj != null) {
		var val = curObj.id;
		var obj = curObj.parentNode.parentNode.parentNode;
		if (obj != null) {
			var id = obj.id;
			var table = curObj.parentNode.parentNode;
			var zebra1 = table.getAttribute("zebra1");
			var zebra2 = table.getAttribute("zebra2");
			var tbody = curObj.parentNode;

			var selRowsStr = table.getAttribute("selectedRows");
			//console.log(selRowsStr+"  |02");
			if (selRowsStr == null || selRowsStr.length == 0) selRowsStr = "0";

			var sels = selRowsStr.split(',');

			var pos = indexOfNum(val, sels);

			var copysels = new Array();
			if (sels.length == 1 && pos > -1) {
				inputObj.checked = true;
			} else {
				if (pos > -1) {
					var zebra = null;
					if (sels[pos] % 2 != 0) {
						zebra = zebra1;
					} else {
						zebra = zebra2;
					}
					tbody.rows[sels[pos]].className = "notselected";
					if (zebra != null) {
						tbody.rows[sels[pos]].style.backgroundColor = zebra;
					}
					for (i = 0; i < pos; i++) {
						copysels[i] = sels[i];
					}
					for (i = pos + 1; i < sels.length; i++) {
						copysels[i-1] = sels[i];
					}
					sels = copysels;
				} else if (pos == -1) {
					curObj.className = "selected";
					curObj.style.backgroundColor = '';
					sels[sels.length] = parseInt(val);
				}
				var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val="+sels.join(',');
				var req = createAsync();
				var func = "rollbackResponse";
				sendAsync(req, post, func, true);

			}
		}
	}

}

function doSomething(curObj)
{
}

function selectIfcRow(event, curObj, ctrlKey) {
	var shPr = false; // нажата клавиша SHIFT
	var ctPr = false; // нажата клавиша CTRL

	if (!event) {
		event = window.event;
	}
	var src = event.target || event.srcElement;

	if (event != null) {
		shPr = event.shiftKey;
		ctPr = event.ctrlKey;
	}

	// если задан атрибут ctrlKey, то должен срабатывать мультивыбор
	if (ctrlKey != null) {
		ctPr = ctrlKey;
	}

	if (curObj != null && curObj.parentNode != null) {
		var val = curObj.rowIndex - 1;
		var obj = curObj.parentNode.parentNode;
		if (obj != null) {
			var id = obj.id;
			if (id == null || id.length < 4) {
				return;
			}
			id = id.substring(3);
			var table = curObj.parentNode.parentNode;
			if (src.tagName.toLowerCase() != "input") {
				$(table).find('input[type="text"]').each(function() {
					$(this).parent().get(0).onclick = function() {
						eval("getCellEditor(this);");
					};
					$(this).parent().text($(this).val());
				});
			}
			var zebra1 = table.getAttribute("zebra1");
			var zebra2 = table.getAttribute("zebra2");
			var tbody = curObj.parentNode;

			// обычный клик мышкой
			if (!shPr && !ctPr) {
				var colChanged = false;
				// сбросить выделение с предудущих выделенных строк
				//if (curObj.className != "selected") {
					var selRowsStr = table.getAttribute("selectedRows");
					//console.log(selRowsStr+"  |03");
					if (selRowsStr == null || selRowsStr.length == 0) {
						selRowsStr = tbody.rows.length > 0 ? "" + val : "";
					}
					var selRows = selRowsStr.split(',');
					removeSelectionIfc(tbody, zebra1, zebra2, selRows, "" + val);
					table.setAttribute("selectedRows", "" + val);
					//console.log(val+"|3");
					curObj.className = "selected";
					curObj.style.backgroundColor = '';

					if (src != null && ((src.tagName.toLowerCase() == "td") || (src.parentNode != null && src.parentNode.tagName .toLowerCase() == "td") || (src.parentNode != null && src.parentNode.parentNode != null && src.parentNode.parentNode.tagName .toLowerCase() == "td"))) {
						if (src.tagName.toLowerCase() != "td") {
							src = src.parentNode;
						}
						if (src.tagName.toLowerCase() != "td") {
							src = src.parentNode;
						}
						var selCol = table.getAttribute("selectedCol");
						var curCol = (selCol != null) ? parseInt(selCol) : -1;
						var oldCol = curCol;
						if (curCol != src.cellIndex) {
							curCol = src.cellIndex;
							colChanged = true;
							table.setAttribute("selectedCol", curCol);
						}
						if (oldCol > -1 && curObj.cells.length > oldCol) {
							curObj.cells[oldCol].className = "";
						}
						if (curCol > -1) {
							curObj.cells[curCol].className = "selected";
						}
					}
				//} else {
				//	var selRow = ("" + val).split(',');
				//	removeSelectionIfc(tbody, zebra1, zebra2, selRow, "");
				//}
				var post = "xml=1&trg=frm&cmd=mod&com=sct&id=" + id;
				var enableAfter = false;

				post += "&row=" + val;

				if (colChanged) {
					post += "&col=" + curCol;
				}
				post += "&scl=1";

				var req = createAsync();
				var func = "rollbackResponse";
				sendAsync(req, post, func, enableAfter);
			} else if (ctPr && !shPr) {
				var selRowsStr = table.getAttribute("selectedRows");
				
			//	console.log(selRowsStr+"  |04");
				
				if (selRowsStr == null || selRowsStr.length == 0) {
					// selRowsStr = "-1";
				}

				var sels = selRowsStr==null?[]:selRowsStr.split(',');
				var pos = indexOfNum(val, sels);
				var copysels = new Array();
				if (sels.length == 1 && pos > -1) {
				} else {
					if (pos > -1) {
						var zebra = pos % 2 != 0 ? zebra1 : zebra2;
						tbody.rows[pos].className = "notselected";
						tbody.rows[pos].style.backgroundColor = zebra;
						for ( var i = 0; i < pos; i++) {
							copysels[i] = sels[i];
						}
						for ( var i = pos + 1; i < sels.length; i++) {
							copysels[i - 1] = sels[i];
						}
						sels = copysels;
					} else if (pos == -1) {
						curObj.className = "selected";
						curObj.style.backgroundColor = '';
						sels[sels.length] = parseInt(val);
					}

					var selsString = sels.join(',');
					selsString = selsString.replace(/^,/, "");
					table.setAttribute("selectedRows", selsString);
			//		console.log(selsString+"|4");
					var colChanged = false;
					if (src != null
							&& ((src.tagName.toLowerCase() == "td") || (src.parentNode != null && src.parentNode.tagName .toLowerCase() == "td") || (src.parentNode != null && src.parentNode.parentNode != null && src.parentNode.parentNode.tagName .toLowerCase() == "td"))) {
						if (src.tagName.toLowerCase() != "td"){
							src = src.parentNode;}
						if (src.tagName.toLowerCase() != "td"){
							src = src.parentNode;}

						var selCol = table.getAttribute("selectedCol");
						var curCol = (selCol != null) ? parseInt(selCol) : selCol;
						var oldCol = curCol;
						if (curCol != src.cellIndex) {
							curCol = src.cellIndex;
							colChanged = true;
							table.setAttribute("selectedCol", curCol);
						}

						if (oldCol > -1 && oldCol != null){
							curObj.cells[oldCol].className = "";
						}
						if (curCol > -1){
							curObj.cells[curCol].className = "selected";
						}
					}

					var post = "xml=1&trg=frm&cmd=mod&com=sct&id=" + id
							+ "&row=" + selsString;
					if (colChanged) {
						post += "&col=" + curCol;
					}
					var req = createAsync();
					var func = "rollbackResponse";
					sendAsync(req, post, func, true);
				}
			} else if (shPr && !ctPr) {
				var selRowsStr = table.getAttribute("selectedRows");
		//		console.log(selRowsStr+"  |05");
				if (selRowsStr == null || selRowsStr.length == 0)
					selRowsStr = "0";
				var sels = selRowsStr.split(',');
				removeSelectionIfc(tbody, zebra1, zebra2, sels, "");

				if (sels.length == 0) {
					curObj.className = "selected";
					curObj.style.backgroundColor = '';
					sels[0] = val;
				} else {
					var lastRow = sels[sels.length - 1];
					sels = new Array();
					if (lastRow > val) {
						for (i = val; i <= lastRow; i++) {
							setRowSelected(i, tbody, sels);
						}
					} else {
						for (j = lastRow; j <= val; j++) {
							setRowSelected(j, tbody, sels);
						}
					}
				}

				var selsString = sels.join(',');
				selsString = selsString.replace(/^,/, "");
				table.setAttribute("selectedRows", selsString);
			//	console.log(selsString+"|7");
				var colChanged = false;
				if (src != null
						&& ((src.tagName.toLowerCase() == "td")
								|| (src.parentNode != null && src.parentNode.tagName
										.toLowerCase() == "td") || (src.parentNode != null
								&& src.parentNode.parentNode != null && src.parentNode.parentNode.tagName
								.toLowerCase() == "td"))) {
					if (src.tagName.toLowerCase() != "td")
						src = src.parentNode;
					if (src.tagName.toLowerCase() != "td")
						src = src.parentNode;

					var selCol = table.getAttribute("selectedCol");
					var curCol = (selCol != null) ? parseInt(selCol) : selCol;
					var oldCol = curCol;
					if (curCol != src.cellIndex) {
						curCol = src.cellIndex;
						colChanged = true;
						table.setAttribute("selectedCol", curCol);
					}

					if (oldCol > -1)
						curObj.cells[oldCol].className = "";
					if (curCol > -1)
						curObj.cells[curCol].className = "selected";
				}

				var post = "xml=1&trg=frm&cmd=mod&com=sct&id=" + id + "&row="
						+ selsString;
				if (colChanged)
					post += "&col=" + curCol;

				var req = createAsync();
				var func = "rollbackResponse";
				sendAsync(req, post, func, true);
			}
		}
	}
}

function indexOfNum(num, arrObj)
{
	for (i=0; i<arrObj.length; i++)
	{
		if (parseInt(num)==arrObj[i]) return i;
	}
	return -1;
}


function deleteRow(id) {
	var c = (!askConfirm) ? true : confirm("Удалить строку?");
	if (c) {
		var post = "xml=1&trg=frm&cmd=mod&id="+id+"&com=del";
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
	}
}

function getCellEditor(curObj) {
	if (curObj != null && curObj.parentNode != null) {
		var col = curObj.cellIndex;
		var obj = curObj.parentNode;
		if (obj != null && obj.className == "selected") {
			var row = obj.rowIndex - 1;
			var obj = obj.parentNode.parentNode;
			if (obj != null) {
				var tid = obj.id;
				if (tid == null || tid.length < 4) return;
				tid = tid.substring(3);

				var post = "xml=1&trg=frm&cmd=mod&id="+tid+"&com=edt&row="+row+"&col="+col;
				var req = createAsync();
				var func = "processGetEditor";
				sendAsync(req, post, func, true);
			}
		}
	}
}

function processGetEditor(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];

			var tags = responseTag.getElementsByTagName("id");
			if (tags != null && tags.length > 0) {
				var tag = tags[0];
				var id = tag.childNodes[0].nodeValue;
				tag = responseTag.getElementsByTagName("row")[0];
				var row = tag.childNodes[0].nodeValue;
				tag = responseTag.getElementsByTagName("col")[0];
				var col = tag.childNodes[0].nodeValue;
				tag = responseTag.getElementsByTagName("editor")[0];
				var editor = tag;

				var obj = d.getElementById(id);
				if(obj != null) {
					var tables = obj.getElementsByTagName("table");
					var table;
					if (tables.length > 0) table = tables[tables.length - 1];
					var tbody = table.getElementsByTagName("tbody")[0];

					//var tr = getRowById(tbody, row);
					var tr = tbody.rows[parseInt(row)];
					var td = tr.cells[parseInt(col)];
					td.onclick = null;

					var children = editor.childNodes;
					var n = 0;
					var resHTML = "";

					for (n = 0; n < children.length; n++) {
						var child = children[n];
						resHTML += getElementXml(child);
					}

					td.innerHTML = resHTML;

					var dp = $(td).find(".dpick");
					createFormatControl(dp);
					createFormatControlIntField($(td).find("[formatting]"));
					createWYSIWYG($(td).find("[wysiwyg]"));
					dp.datepicker({weekStart: 1, autoclose: true, language: 'ru'});
					
					var inputs = td.getElementsByTagName("input");
					if (inputs.length > 0) {
						var curInput = inputs[0];
						curInput.focus();
						/* убрано по причине того, что хром чистит ячейку в таблицах при редактировании. 
						 * При клике он выделяет всё содержимое ячейки и функция удаляет весь выбранный интервал. 
						 * Так как функция не работет и на мозиле, её лучше убрать
						 * */
						//insertIfEmpty(curInput);
					} else {
						inputs = td.getElementsByTagName("select");
						if (inputs.length > 0) {
							var curInput = inputs[0];
							curInput.focus();
							disableSelection(document.body);
						}
					}
					
				}
			} else {
				if (!searching) {
					showSearch(curFocused);
					var sfd = document.getElementById("sfd");
					sfd.value = lastChar;
					sfd.focus();
					var r = sfd.createTextRange();
					r.collapse(false);
					r.select();
					lastChar = "";
				}
			}
		}
	}
}

function disableSelection(target){
	if (typeof target.onselectstart!="undefined") {
		//tempHolder = target.onselectstart;
		tempHolderType = 1;
		target.onselectstart=function() {return false;};
	} else if (typeof target.style.MozUserSelect!="undefined") {
		tempHolder = target.style.MozUserSelect;
		tempHolderType = 2;
		target.style.MozUserSelect="none";
	} else {
		tempHolder = target.onmousedown;
		tempHolderType = 3;
		target.onmousedown=function(){return false;};
	}
}

function enableSelection(target){
	if (tempHolderType == 1)
		target.onselectstart = null;
	else if (tempHolderType == 2)
		target.style.MozUserSelect=tempHolder;
	else
		target.onmousedown=tempHolder;
}

function insertIfEmpty(myField) {
	//IE support
	var lc = "";
	if (document.selection) {
		//in effect we are creating a text range with zero
		//length at the cursor location and replacing it
		//with myValue
		if (myField.onfocus != null && myField.value.length > 9 && lastChar.length == 1) {
			var c = parseInt(lastChar);
			if (c >= 0 && c <= 9) {

				myField.value = c + myField.value.substring(1, myField.value.length);
				var r = myField.createTextRange();
				r.collapse(true);
				r.move('character', 1);
				r.select();
				lc = lastChar;
			}
		} else {
			var r = myField.createTextRange();
			r.collapse(false);
			r.select();
			r.text = lastChar;
		}
		//Mozilla/Firefox/Netscape 7+ support
	} else if (myField.selectionStart || myField.selectionStart == '0') {
		//Here we get the start and end points of the
		//selection. Then we create substrings up to the
		//start of the selection and from the end point
		//of the selection to the end of the field value.
		//Then we concatenate the first substring, myValue,
		//and the second substring to get the new value.
		var startPos = myField.selectionStart;
		var endPos = myField.selectionEnd;
		myField.value = myField.value.substring(0, startPos) + lastChar + myField.value.substring(endPos, myField.value.length);
		myField.setSelectionRange(endPos+lastChar.length, endPos+lastChar.length);
	} else {
		myField.value += lastChar;
	}
	lastChar = lc;
}

function popupChanged(id)
{
	window.returnValue = "YES";
	window.close();
}

function popupCanceled(id)
{
	window.returnValue = "CLEAR";
	window.close();
}

function windowChanged()
{
	window.returnValue = "YES";
	window.close();
}

function windowChangedCC(permit) {
	var post = "xml=1&trg=frm&cmd=dccm&permit=" + permit;
	var req = createAsync();
	var func = "processCanCommit";
	sendAsync(req, post, func, true);
}

function processCanCommit(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];

			var tags = responseTag.getElementsByTagName("state");
			if (tags != null && tags.length > 0) {

				for (k = 0; k<tags.length; k++) {
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
				for (k = 0; k<tags.length; k++) {
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
						var cellTags = rowObj.getElementsByTagName("td");
						var td = cellTags[parseInt(col)];

//						if (state == "1") {				
//							td.style.backgroundColor = "#FFCCCC";
//						} else if (state == "2") {
//							td.style.backgroundColor = "#CAF7BB";
//						} else {
//							td.style.backgroundColor = null;
//						}
						setErrorType(obj, (state == 1) ? 0 : state);//TODO app.js
					}
				}
			}

			tags = responseTag.getElementsByTagName("hasErrors");
			if (tags != null && tags.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=derr";
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();
				fr = window.open(url, 'CommitPopupFrame', 'directories=no,height=450,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=620');  
				fr.moveTo(screen.availWidth/2-310, screen.availHeight/2-225);
			} else {
				var post = "xml=1&trg=frm&cmd=wcl&val=YES";
				var req = createAsync();
				var func = "rollbackResponse";
				sendAsync(req, post, func, true);

				$('#modalDlg').modal('hide');
				$('#modalDlg').data('modal', null);

				//window.returnValue = "YES";
				//window.close();
			}
		}
	}
}

function higlihtError2(id, row, col, state) {

	var obj = document.getElementById(id);

	if(obj != null) {

		var tables = obj.getElementsByTagName("table");
		var table = tables[tables.length - 1];

		var tbody = table.getElementsByTagName("tbody")[0];

		var rowObj = tbody.rows[row];
		var cellTags = rowObj.getElementsByTagName("td");
		var td = cellTags[col];

//		if (state == 1) {				
//			td.style.backgroundColor = "#FFCCCC";
//		} else if (state == 2) {
//			td.style.backgroundColor = "#CAF7BB";
//		} else {
//			td.style.backgroundColor = null;
//		}
		setErrorType(obj, (state == 1) ? 0 : state);//TODO app.js
	}
}

function higlihtError(id, state) {
	var obj = document.getElementById(id);

//	if(obj != null) {
//
//		if (state == 1) {				
//			obj.style.backgroundColor = "#FFCCCC";
//		} else if (state == 2) {
//			obj.style.backgroundColor = "#CAF7BB";
//		} else {
//			obj.style.backgroundColor = null;
//		}
//	}
	setErrorType(obj, state);
}

function windowCanceled()
{
	window.returnValue = "CLEAR";
	window.close();
}

function windowChangedAuto() {
	var post = "xml=1&trg=frm&cmd=accm";
	var req = createAsync();
	var func = "processCanCommitAuto";
	sendAsync(req, post, func, true);
}

function processCanCommitAuto(req)
{
	if (req.readyState == 4) {
		if (req.status == 200) {
			var d = document;
			var responseTag = req.responseXML.getElementsByTagName("r")[0];
			var tags = responseTag.getElementsByTagName("state");
			if (tags != null && tags.length > 0) {
				for (k = 0; k<tags.length; k++) {
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
					setErrorType(obj, state);//TODO app.js
				}
			}

			tags = responseTag.getElementsByTagName("state2");
			if (tags != null && tags.length > 0) {
				for (k = 0; k<tags.length; k++) {
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
						var cellTags = rowObj.getElementsByTagName("td");
						var td = cellTags[parseInt(col)];

//						if (state == "1") {				
//							td.style.backgroundColor = "#FFCCCC";
//						} else if (state == "2") {
//							td.style.backgroundColor = "#CAF7BB";
//						} else {
//							td.style.backgroundColor = null;
//						}
						setErrorType(obj, (state == 1) ? 0 : state);//TODO app.js
					}
				}
			}

			tags = responseTag.getElementsByTagName("hasErrors");
			var mtags = responseTag.getElementsByTagName("message");
			if (tags != null && tags.length > 0) {
				var url = location.pathname + "?trg=frm&cmd=aerr";
				if (url.indexOf("/") > 0) url = "/" + url;
				url += "&noCache=" + (new Date).getTime();

				$('#errorModalAuto').modal({remote: url, backdrop: 'static', keyboard: true});
			} else if (mtags != null && mtags.length > 0) {
				var mtag = mtags[0];
				var message = mtag.childNodes[0].nodeValue;
				alert(message);
			} else if (responseTag.childNodes.length == 0) {
				var post = "xml=1&trg=frm&cmd=wcl&val=YES";
				var req = createAsync();
				var func = "rollbackResponse";
				sendAsync(req, post, func, true);

				$('#modalAuto').modal('hide');
				$('#modalAuto').data('modal', null);
		    	stopWait();
			}
		}
	}
}

function windowCanceledAuto()
{
	window.returnValue = "AUTOCLEAR";
	window.close();
}

function popupClosed(id, val, row, col) {
	var post = "xml=1&trg=frm&cmd=pcl&id="+id+"&val="+val;
	if (row != null && col != null) {
		post += "&row="+row+"&col="+col;
	}
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);

	enableWholeWindow(true);
}

function treeFieldPressed(obj, width, height, tid, row, col) {
	if (tid != null && row != null && col != null) {
 	    var trg = $(obj).attr('data-target');
		obj = obj.parentNode.parentNode;

 		if (obj != null && obj.className == "selected") {
			var url = location.pathname + "?trg=frm&cmd=tfp&id=" + tid + "&row="+row+"&col="+col;
			if (url.indexOf("/") > 0) url = "/" + url;
			url += "&noCache=" + (new Date).getTime();
	 	    
	 	    $(trg).find(".modifier").attr('data-row', row);
	 	    $(trg).modal({remote: url, backdrop: 'static'});
		}
	} else {
		if (obj.tagName.toLowerCase() == "fieldset")
			obj = obj.parentNode;

		var id = obj.id;
		var url = location.pathname + "?trg=frm&cmd=tfp&id=" + id;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();

 	    var trg = $(obj).attr('data-target');

 	    $(trg).modal({remote: url, backdrop: 'static'});
	}
}


function treeFieldChanged(id)
{
	window.returnValue = "YES";
	window.close();
}

function treeFieldCanceled(id)
{
	window.returnValue = "CLEAR";
	window.close();
}

function treeFieldClosed(id, val, row, col) {
	var req;
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val="+val;
	if (row != null && col != null) {
		post += "&row="+row+"&col="+col;
	}
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);

	enableWholeWindow(true);
}

function buttonPressed(button, pass) {
	var id = button.id;
	var post = "xml=1&trg=frm&cmd=mod&id="+id;
	if (pass != null) {
		post += "&pass="+encodeURIComponent(pass);
	}
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function docFieldPressed(obj) {
	var post = "";
	
	if ("td" == obj.tagName.toLowerCase()) {
		var col = obj.cellIndex;
		obj = obj.parentNode;
		if (obj != null && obj.className == "selected") {
			var row = obj.rowIndex - 1;
			obj = obj.parentNode.parentNode;
			var tid = obj.id;
			if (tid == null || tid.length < 4) return;
			tid = tid.substring(3);

			post = "xml=1&trg=frm&cmd=dfd&id="+tid+"&row="+row+"&col="+col;
		}
	} else {
		var id = obj.id;
		post = "xml=1&trg=frm&cmd=dfd&id="+id;
	}
	if (post.length > 0) {
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
	}
}

function radioPressed(button) {
	var id = button.name.substring(5, button.name.length);
	var post = "xml=1&trg=frm&cmd=mod&id="+id+"&val=" + button.value;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function fix(parentDiv){
	var obj = parentDiv.childNodes[0];
	if (obj.className == "tbh") {
//		var table = par.childNodes[1].rows[0];
		obj.style.left = parentDiv.scrollLeft + "px";
		obj.style.top = parentDiv.scrollTop + "px";
	}
//	table.style.top = par.scrollTop + "px";
}


function getEventType(e) {
		    if (!e) e = window.event;
		    alert(e.type);
		}

function resetClick() {
	click = false;
}

function treeSelChanged(aObj, idBtn, enabled ,col) {
	// проверка необходимости выполенния функции
	if(click){ // убрать ошибка не по этому
		return;
	}else{
		click = true;
		setTimeout('resetClick()', 200);
	}	
	var d = document;
	var req;
	var nodeId = aObj.id.substring(1, aObj.id.length);
	var tree = aObj;
	while (true) {
		if (tree.tagName.toLowerCase() == "div"){
			break;
		}
		tree = tree.parentNode;
	}
	var treeId = tree.id;

	removeTreeSelection(tree);
	aObj.className = "Current";

	var post = "xml=1&trg=frm&cmd=mod&id="+treeId+"&val="+nodeId;
	if (col != null) {
		post += "&col=" + col;
	}
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
	// если вызов функции был из OrWebTreeField 
	if (idBtn != 'null'){
		if (enabled==true){
			$(".btn[data-value='YES'][data-id='"+idBtn+"']").removeAttr("disabled");	
		} else{
			$(".btn[data-value='YES'][data-id='"+idBtn+"']").attr("disabled","disabled");
		}
	}
}

function treeExpandID(id, col){
	// сбросить переменную клик
	click = false;
	treeExpand($("a[id='"+id+"']")[0], col);
}

function treeExpand(aObj, col) {
	var nodeId = aObj.id;
	var tree = aObj;
	while (tree.tagName.toLowerCase() != "div") {
		tree = tree.parentNode;
	}
	var treeId = tree.id;

	var ul = document.getElementById("ul" + nodeId);
	var img = document.getElementById("img" + nodeId);

	var waitAnswer = false;
	if(ul != null && ul.className != null && ul.innerHTML != null) {

		if (ul.className == "Hidden") {
			ul.className = "Shown";
			img.src = "images/minus.gif";

			var innerStr = trim(ul.innerHTML);
			if(innerStr == ""){
				ul.innerHTML="<DIV CLASS='loadMsg'>&nbsp;Подождите идет загрузка...&nbsp;</DIV>";
				waitAnswer = true;
			}
		} else {
			ul.className = "Hidden";
			img.src = "images/plus.gif";
		}
		var post = "xml=1&trg=frm&cmd=exp&id=" + treeId + "&nid=" + nodeId;
		if (col != null) {
			post += "&col=" + col;
		}
		if (!waitAnswer) {
			post += "&wait=no";
		}
		var req = createAsync();
		var func = "processNodeIfc";
		sendAsync(req, post, func, true);
	}
}

function treeTableExpand(aObj, pref) {
	var nodeId = aObj.id;
	if (pref != null) {
		nodeId = nodeId.substring(pref.length);
	}
	var tree = aObj;
	while (tree.tagName.toLowerCase() != "div") {
		tree = tree.parentNode;
	}
	var treeId = tree.id;
	var img = document.getElementById("img" + nodeId);
	if (img != null) {
		if (img.src.indexOf("plus") > -1) {
			img.src = "images/minus.gif";
		}else{
			if (pref != null) {
				return;
			}
			img.src = "images/plus.gif";
		}
		var post = "xml=1&trg=frm&cmd=exp&id=" + treeId + "&nid=" + nodeId;
		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
	}
	return false;
}

function processNodeIfc(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var responseTag = req.responseXML.getElementsByTagName("r")[0];

			var tags = responseTag.getElementsByTagName("id");
			if (tags == null || tags.length == 0) return;
			var tag = tags[0];

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

function doLoad(id) {
	var post = "xml=1&trg=frm&cmd=mod&id="+id;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);

	$('#fotoModal' + id).modal('hide');
	$('#fotoModal' + id).data('modal', null);
}

//-- Browser Detection --
var domLib_userAgent = navigator.userAgent.toLowerCase();
var domLib_isMac = navigator.appVersion.indexOf('Mac') != -1;
var domLib_isWin = domLib_userAgent.indexOf('windows') != -1;
//NOTE: could use window.opera for detecting Opera
var domLib_isOpera = domLib_userAgent.indexOf('opera') != -1;
var domLib_isOpera7up = domLib_userAgent.match(/opera.(7|8)/i);
var domLib_isSafari = domLib_userAgent.indexOf('safari') != -1;
var domLib_isKonq = domLib_userAgent.indexOf('konqueror') != -1;
//Both konqueror and safari use the khtml rendering engine
var domLib_isKHTML = (domLib_isKonq || domLib_isSafari || domLib_userAgent.indexOf('khtml') != -1);
var domLib_isIE = (!domLib_isKHTML && !domLib_isOpera && (domLib_userAgent.indexOf('msie 5') != -1 || domLib_userAgent.indexOf('msie 6') != -1 || domLib_userAgent.indexOf('msie 7') != -1));
var domLib_isIE5up = domLib_isIE;
var domLib_isIE50 = (domLib_isIE && domLib_userAgent.indexOf('msie 5.0') != -1);
var domLib_isIE55 = (domLib_isIE && domLib_userAgent.indexOf('msie 5.5') != -1);
var domLib_isIE5 = (domLib_isIE50 || domLib_isIE55);
//safari and konq may use string "khtml, like gecko", so check for destinctive /
var domLib_isGecko = domLib_userAgent.indexOf('gecko/') != -1;
var domLib_isMacIE = (domLib_isIE && domLib_isMac);
var domLib_isIE55up = domLib_isIE5up && !domLib_isIE50 && !domLib_isMacIE;
var domLib_isIE6up = domLib_isIE55up && !domLib_isIE55;

function HoverMe(in_this)
{
	in_this.style.backgroundColor = '#d8dde7';
	//in_this.style.color = '#FFFFFF';
	in_this.style.cursor = domLib_isIE ? 'hand' : 'pointer';

	var id = in_this.id;
	var allDivs = document.getElementsByTagName('div');
	for (var i = 0; i < allDivs.length; i++)
	{
		if (allDivs[i].className == "report")
		{
			if (allDivs[i].id.length >= id.length) {
				allDivs[i].style.visibility = 'hidden';
			}
		}
	}
	var div = document.getElementById("menu" + id.substring(4));
	if (div != null) {
		div.style.visibility = 'visible';
		div.style.display = '';
	}
}

function hideAll(in_this)
{
	in_this.style.visibility = 'hidden';

	var ifone = document.getElementById("ifone");

	if (ifone != null) ifone.style.visibility = 'hidden';

	var allDivs = document.getElementsByTagName('div');
	for (var i = 0; i < allDivs.length; i++)
	{
		if (allDivs[i].className == "report")
		{
			allDivs[i].style.visibility = 'hidden';
		}
	}
}

function hideAllMenu()
{
	var fone = document.getElementById("fone");

	fone.style.visibility = 'hidden';

	var ifone = document.getElementById("ifone");

	if (ifone != null) ifone.style.visibility = 'hidden';

	var allDivs = document.getElementsByTagName('div');
	for (var i = 0; i < allDivs.length; i++)
	{
		if (allDivs[i].className == "report")
		{
			allDivs[i].style.visibility = 'hidden';
		}
	}
}

function UnhoverMe(in_this)
{
	in_this.style.backgroundColor = '';
	in_this.style.cursor = '';
}

function disableButton(buttonId, backpage, ocm) {
	try {
		if (parent.parent != null && parent.parent.menu != null)
			parent.parent.menu.disableButton(buttonId, backpage, ocm);
	} catch (ex) {}
}

function enableButton(buttonId) {
	try {
		if (parent.parent != null && parent.parent.menu != null)
			parent.parent.menu.enableButton(buttonId);
	} catch (ex) {}
}
/*
function enableReportButton() {
  var aobj = parent.parent.menu.document.getElementById("print");
  aobj.onclick = function() {
    eval("parent.parent.menu.showPrintMenu();");
  };
  var iobj = aobj.getElementsByTagName('img')[0];
  parent.parent.menu.setOpacity(iobj, 100);
}
 */
function setDialogSize(width, height) {
	if (width > 0) {
		width = width + 25;
		parent.window.dialogWidth = width + "px";
		parent.window.dialogLeft = (screen.availWidth - width)/2 + "px";
	}
	if (height > 0) {
		height = height + 90;
		parent.window.dialogHeight = height + "px";
		parent.window.dialogTop = (screen.availHeight - height)/2 + "px";
	}
}

function setDialogSizeAndLoad(width, height, id, fid, row, col) {
	setDialogSize(width, height);
	if (row != null && col != null) {
		var url = location.pathname + "?trg=frm&cmd=pop&id=" + id + "&fid="+fid+"&row="+row+"&col="+col;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		window.location.replace(url);
	} else {
		var url = location.pathname + "?trg=frm&cmd=pop&id=" + id + "&fid=" + fid;
		if (url.indexOf("/") > 0) url = "/" + url;
		url += "&noCache=" + (new Date).getTime();
		window.location.replace(url);
	}
}

function showReport(id)
{
	pleaseWait(currentLang=="kz"?"Есептің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие отчета...");
//	hideAllMenu();

	var post = "xml=1&trg=frm&cmd=grpt&id="+id;
	post += "&noCache=" + (new Date).getTime();

	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function showProcessReport(obj) {
	pleaseWait(currentLang=="kz"?"Есептің ашылуы жүріп жатыр, күте тұрыңыз...":"Подождите, идет открытие отчета...");

	obj = obj.parentNode;
	var flowId = obj.id;

	var post = "xml=1&trg=frm&cmd=mrpt&id="+flowId;
	var req = createAsync();
	var func = "redirectToReport";
	sendAsync(req, post, func, false);
}

function loadImage(id)
{
	var url = location.pathname + "?trg=frm&cmd=shi&id="+id;
	if (url.indexOf("/") > 0) url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	
    $('#fotoModal' + id).modal({remote: url, backdrop: 'static', keyboard: true});

	//fr = window.open(url, 'ImageFrame', 'directories=no,height=150,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=300');  
	//fr.moveTo(screen.availWidth/2-150, screen.availHeight/2-75);
}

function loadImage2(obj)
{
	var url = location.pathname + "?trg=frm&cmd=shi&id=";
	
	if ("td" == obj.tagName.toLowerCase()) {
		var col = obj.cellIndex;
		obj = obj.parentNode;
		if (obj != null && obj.className == "selected") {
			var row = obj.rowIndex - 1;
			obj = obj.parentNode.parentNode;
			var tid = obj.id;
			if (tid == null || tid.length < 4) return;
			tid = tid.substring(3);
			
			url += tid+"&row="+row+"&col="+col;

			if (url.indexOf("/") > 0) url = "/" + url;
			url += "&noCache=" + (new Date).getTime();

		    $('#fotoModal' + tid + "_" + col).modal({remote: url, backdrop: 'static', keyboard: true});
		    //fr = window.open(url, 'ImageFrame', 'directories=no,height=150,location=no,menubar=no,resizable=yes,scrollbars=yes, status=yes, toolbar=no, width=300');  
			//fr.moveTo(screen.availWidth/2-150, screen.availHeight/2-75);
		}
	}
}

function deleteImage(id) {
	var post = "xml=1&trg=frm&cmd=dim&id="+id;
	var req = createAsync();
	var func = "rollbackResponse";
	sendAsync(req, post, func, true);
}

function showPrintMenu() {
	var menu = document.getElementById("menu0");
	if (menu != null) {
		menu.style.display = '';
		menu.style.visibility = 'visible';
		var fone = document.getElementById("fone");
		fone.style.display = '';
		fone.style.visibility = 'visible';
		var ifone = document.getElementById("ifone");
		if (ifone != null) {
			ifone.style.display = '';
			ifone.style.visibility = 'visible';
		}
	}
}

function menuBtnOver(obj) {
	obj.className = "menuBtnOver";
}

function menuBtnOut(obj) {
	obj.className = "menuBtn";
}

function cancelProgram(){
	var post = "xml=1&trg=frm&cmd=ext";
	var req = createAsync();
	var func = "";
	sendAsync(req, post, func, true);
}

function getElementNode(d, node) {
	if (node != null) {
		if (node.nodeType == 1) {
			var res =  d.createElement(node.nodeName);
			var ak = 0;

			var parW= "";
			var parH= "";
			var parId= "";
			var parR= "";
			var parC= "";

			for (ak = 0; ak < node.attributes.length; ak++) {
				var attr = node.attributes[ak];
				var attrName = attr.nodeName.toLowerCase();
				if (attrName == "style") {
					res.style.cssText = attr.nodeValue;
				} else if (attrName == "onclick") {
					var tdOnClick = attr.nodeValue;
					if (tdOnClick.indexOf("popupPressed") > -1) {
						res.onclick = function() {
							eval("popupPressed(this);");
							return false;
						};
					} else if (tdOnClick.indexOf("treeFieldPressed") > -1) {
						var b1 = tdOnClick.indexOf(",");
						var b2 = tdOnClick.indexOf(",", b1 + 1);
						parW = tdOnClick.substring(b1 + 2, b2);

						b1 = b2;
						var b2 = tdOnClick.indexOf(",", b1 + 1);
						parH = tdOnClick.substring(b1 + 2, b2);

						b1 = tdOnClick.indexOf("'", b2 + 1);
						var b2 = tdOnClick.indexOf("'", b1 + 1);
						parId = tdOnClick.substring(b1 + 1, b2);

						b1 = b2 + 1;
						var b2 = tdOnClick.indexOf(",", b1 + 1);
						parR = tdOnClick.substring(b1 + 2, b2);

						b1 = b2;
						var b2 = tdOnClick.indexOf(")", b1 + 1);
						parC = tdOnClick.substring(b1 + 2, b2);

						res.onclick = function() {
							eval("treeFieldPressed(this, " + parW + ", " + parH + ", '" + parId + "', " + parR + ", " + parC + ");");
							return false;
						};
					}
				} else if (attr.nodeName == "class") {
					res.className = attr.nodeValue;
				} else if (attr.nodeName == "src") {
					res.src = attr.nodeValue;
				} else {
					res.setAttribute(attr.nodeName, attr.nodeValue);
				}
			}
//			for (ak = 0; ak < node.attributes.length; ak++) {
//			var attr = node.attributes[ak];
//			res.setAttribute(attr.nodeName, attr.nodeValue);
//			}
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

function trim(str) {
	return str.replace(/(^\s+)|(\s+$)/g, "");
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

function positionImages(id1, id2) {
	o1 = document.getElementById(id1);
	o2 = document.getElementById(id2);
	o2.style.left = GetAbsLeft(o1);
	o2.style.top = GetAbsTop(o1);
}

function GetAbsTop(_obj) {
	var _top=0;
	var _parent=_obj;
	_top+=_parent.offsetTop;
	//_top+=_parent.clientTop;

	do {
		_parent=_parent.offsetParent;
		_top+=_parent.offsetTop;
		//_top+=_parent.clientTop;
	} while (_parent!==document.body);

	return _top-document.body.scrollTop;
}

function GetAbsLeft(_obj) {
	var _left=0;
	var _parent=_obj;
	_left+=_parent.offsetLeft;
	//_left+=_parent.clientLeft;

	do {
		_parent=_parent.offsetParent;
		_left+=_parent.offsetLeft;
		//_left+=_parent.clientLeft;
	} while (_parent!==document.body);

	return _left-document.body.scrollLeft;
}

function disableWholeWindow(msg, permanently, t) {
	var w = window;
	try {
		if (w != null && w.disableWindow) {
			w.disableWindow(msg, permanently, t);
		}
		else if (w.parent != null && w.parent.disableWindow) {
			w.parent.disableWindow(msg, permanently, t);
		}
		else if (w.parent.parent != null && w.parent.parent.disableWindow) {
			w.parent.parent.disableWindow(msg, permanently, t);
		}
	} catch (ex) {}
}

function enableWholeWindow(perform) {
	var w = parent;
	try {
		if (w != null && w.enableWindow) {
			w.enableWindow(perform);
		}
		else if (w != null && w.parent != null && w.parent.enableWindow) {
			w.parent.enableWindow(perform);
		}
	} catch (ex) {}
}

function findPos(obj) {
	var curleft = curtop = 0;
	var curleft = curtop = 0;
	while (obj) {
		curleft += obj.offsetLeft;
		curtop += obj.offsetTop;
		obj = obj.offsetParent;
	}
	return [curleft,curtop];
}

var lastComboIndex = -1;

function combotext_onkeydown(e,oText,oSelect,oTextIn){  
	if (oSelect.disabled == true) return false;
	if (!e) {
		e = window.event;
	}  
	keyCode = e.keyCode;  

	if (lastComboIndex == -1)
		lastComboIndex = oSelect.selectedIndex;

	if (keyCode == 40 || keyCode == 38) {  
		pos = findPos(oText);
		pos2 = findPos(oTextIn);

		oSelect.style.top = pos[1] - pos2[1] + oText.offsetHeight + "px";
		oSelect.style.left = pos[0] - pos2[0] + "px";

		oSelect.style.display = 'block';  
		oSelect.focus();  
		comboselect_onchange(oSelect, oText);  
	}  
	else if (keyCode == 27) {
		e.cancelBubble = true;  
		if (e.returnValue) e.returnValue = false;  
		if (e.stopPropagation) e.stopPropagation();  
		oSelect.selectedIndex = lastComboIndex;
		comboselect_onchange(oSelect, oText);  
		oSelect.style.display='none';  
		lastComboIndex = -1;
		oText.focus();  
		return false;  
	}
	else if (keyCode == 13) {
		e.cancelBubble = true;  
		if (e.returnValue) e.returnValue = false;  
		if (e.stopPropagation) e.stopPropagation();  
		comboselect_onchange(oSelect, oText);  
		oSelect.style.display='none';  
		lastComboIndex = -1;
		oText.focus();  
		textChanged(oSelect, oSelect.getAttribute("id"));
		return false;  
	}  
	else if(keyCode == 9) return true;  
	else {  
		pos = findPos(oText);
		pos2 = findPos(oTextIn);

		oSelect.style.top = pos[1] - pos2[1] + oText.offsetHeight + "px";
		oSelect.style.left = pos[0] - pos2[0] + "px";

		oSelect.style.display = 'block';  
		//var c = String.fromCharCode(keyCode);  
		//c = c.toUpperCase();   
		toFind = oText.value.toUpperCase();
		for (i=0; i < oSelect.options.length; i++){  
			nextOptionText = oSelect.options[i].text.toUpperCase();  

			if(nextOptionText.length >= toFind.length && toFind == nextOptionText.substring(0, toFind.length)){  
				oSelect.selectedIndex = i;  
				break;  
			}  
		}
	}  
}  

function comboselect_onchange(oSelect,oText) {  
	if(oSelect.selectedIndex != -1)	{
		oText.value = oSelect.options[oSelect.selectedIndex].text;
		if (oSelect.style.display != 'block') {
			lastComboIndex = -1;
			oSelect.style.display='none';  
			textChanged(oSelect, oSelect.getAttribute("id"));
		}
	}
}  

function comboselect_onkeyup(e,oSelect,oText){
	if (!e) {
		e = window.event;
	}  
	keyCode = e.keyCode;  

	if (keyCode == 13) {  
		comboselect_onchange(oSelect, oText);  
		oSelect.style.display='none';  
		lastComboIndex = -1;
		oText.focus();
		textChanged(oSelect, oSelect.getAttribute("id"));
	}  
	else if (keyCode == 27) {
		oSelect.selectedIndex = lastComboIndex;
		comboselect_onchange(oSelect, oText);  
		oSelect.style.display='none';  
		lastComboIndex = -1;
		oText.focus();  
	}
}  

function comboselect_onblur(oSelect){
	lastComboIndex = -1;
	oSelect.style.display='none';  
	textChanged(oSelect, oSelect.getAttribute("id"));
}  

function comboselect_onclick(oSelect, oText){
	lastComboIndex = -1;
	oSelect.style.display='none';  
	oText.focus();
	textChanged(oSelect, oSelect.getAttribute("id"));
}  

function combotext_ondblclick(oText,oSelect,oTextIn){  
	if (oSelect.disabled == true) return false;

	if (lastComboIndex == -1)
		lastComboIndex = oSelect.selectedIndex;

	pos = findPos(oText);
	pos2 = findPos(oTextIn);

	oSelect.style.top = pos[1] - pos2[1] + oText.offsetHeight + "px";
	oSelect.style.left = pos[0] - pos2[0] + "px";

	oSelect.style.display = 'block';  
	oSelect.focus();  
	comboselect_onchange(oSelect, oText);  
}  

function checkToSign(frameId) {
	var post = "xml=1&trg=frm&id=" + frameId + "&cmd=checkToSign";
	var req = createAsync();
	var url = location.pathname;
	if (url.charAt(1) != ':') {
		if (url.indexOf("/") > 0) url = "/" + url;
		post += "&noCache=" + (new Date).getTime();
		
		req.open("POST", url, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		//req.setRequestHeader("Content-Length", post.length);
		req.onreadystatechange = function() { showToSign(req); };
		req.send(post);
	}
}

function showToSign(req) {
	if (req.readyState == 4) {
		if (req.status == 200) {
			var resText = req.responseText;
			if (resText != null && resText.indexOf("<sign>") > 0) {
				
				pleaseWait();
				
				var s1 = resText.indexOf("<text>");
				if (s1 > 0) {
					var s2 = resText.lastIndexOf("</text>");
					var msg = resText.substring(s1 + 6, s2);
					
					s1 = resText.indexOf("<path>", s2);
					if (s1 > 0) {
						s2 = resText.indexOf("</path>", s1);
						if (s2 > 0) {
							keyPath = resText.substring(s1 + 6, s2);
						}
					}
					keyPass = null;
					s1 = resText.indexOf("<code>", s2);
					if (s1 > 0) {
						s2 = resText.indexOf("</code>", s1);
						if (s2 > 0) {
							keyPass = resText.substring(s1 + 6, s2);
						}
					}
					if (keyPass != null && keyPass.length > 0) {
						signMsg = msg;
						signStringCommonEnd(keyPath + ";" + keyPass);
					} else
						signStringCommon(msg, "iola");

				}		
				
			}
		}
	}
}

function signStringCommon(msg, type) {

		var url = "showECP.html?";
		url += "&noCache=" + (new Date).getTime();
		signMsg = msg;

		var w = 360;
		var h = 310;
		var fr = window.open(url, "KeyPath" + (new Date).getTime(), "directories=no,width=" +w+",height="+h+",location=no,menubar=no,resizable=no,scrollbars=no, status=no, toolbar=no");
		fr.moveTo(screen.availWidth/2-w/2, screen.availHeight/2-h/2);

}

function signStringCommonEnd(value) {
		var path = null;
		var sent = false;

		if (value != null && value.length > 1) {
	    	var s = value.split(';');
	    	path = s[0];
	    	pass = s[1];
	    	keyPath = path;
	    	keyPass = pass;

			  if (path != null) {
				var signed = document.getElementById('ECPApplet').getSignAndCert(signMsg, 17, path, pass);
	
				if (signed.length > 7 && "[ERROR: " == signed.substring(0, 8)) {
		  			alert(signed.substring(8, signed.length - 1));
		  		} else {
				  	var b1 = signed.indexOf("sign=");
					var b2 = signed.indexOf("&cert=");

					var sign = signed.substring(b1 + 5, b2);
					var cert = signed.substring(b2 + 6);

	  				var post = "xml=1&trg=frm&cmd=signres&path=" + encodeURIComponent(path) + "&sign=" + encodeURIComponent(sign)  + "&cert=" + encodeURIComponent(cert) + "&code=" + encodeURIComponent(pass);
			  		var req2 = createAsync();
	  				var func = "";
	  				sendAsync(req2, post, func, true);

	  				sent = true;
		  		}
			 }
		}
		
		if (!sent) {
			var post = "xml=1&trg=frm&cmd=signres&sign=";

	  		var req2 = createAsync();
 				var func = "";
 				sendAsync(req2, post, func, true);
		}

		stopWait();
}

function showToolTip(e, tip) {
	if (document.body.style.cursor != null && document.body.style.cursor.toLowerCase() == "help") {
		if (!e) e = window.event;
		//popUp(e, tipId);
		popUp(e, tip);
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();

		return false;
	}
	return true;
}

function pw() {
	return window.innerWidth 
	|| document.documentElement.clientWidth 
	|| document.body.clientWidth;
};

function mouseX(evt) {
	return evt.clientX ? evt.clientX : evt.pageX; //+ 
//	(document.documentElement.scrollLeft || document.body.scrollLeft) 
//	: evt.pageX;
}

function mouseY(evt) {
	return evt.clientY ? evt.clientY : evt.pageY;
	//+ (document.documentElement.scrollTop || document.body.scrollTop) 
	//: evt.pageY;
}

function popUp(evt, tip) {
	if (document.getElementById) {
		var wp = pw();
		dm = parent.document.getElementById("tip");
		if (dm != null) {
			ds = dm.style;
			if (tip == null || tip.length == 0) {
				ds.visibility = "hidden"; 
			} else {
				dm.innerHTML = tip;

				if (dm.offsetWidth) 
					ew = dm.offsetWidth; 
				else if (dm.clip.width) 
					ew = dm.clip.width; 

				tv = mouseY(evt); 
				lv = mouseX(evt); 
				if (lv < 2) lv = 2; 
				else if (lv + ew > wp) lv -= ew/2; 
				lv += 'px';
				tv += 'px';  
				ds.left = lv; 
				ds.top = tv; 
				ds.visibility = "visible";
			}
		}
	}
}

function popUp2(evt,oi) {
	if (document.getElementById) {
		hideAllTips();
		var wp = pw();
		dm = document.getElementById(oi);
		if (dm != null) {
			ds = dm.style;
			st = ds.visibility;
			if (dm.offsetWidth) 
				ew = dm.offsetWidth; 
			else if (dm.clip.width) 
				ew = dm.clip.width; 

			if (st == "visible" || st == "show") { 
				ds.visibility = "hidden"; 
			} else {
				tv = mouseY(evt) + 20; 
				lv = mouseX(evt) - (ew/4); 
				if (lv < 2) lv = 2; 
				else if (lv + ew > wp) lv -= ew/2; 
				lv += 'px';
				tv += 'px';  
				ds.left = lv; 
				ds.top = tv; 
				ds.visibility = "visible";
			}
		}
	}
}

function hideAllTips() {
	var tip = parent.document.getElementById("tip");
	tip.style.visibility = "hidden";
}

function attachKeydownHandler() {
	if (document.addEventListener){
		document.addEventListener('keypress', onkeydownHandler, true); 
		//document.addEventListener('click', onclickHandler, true); 
		window.addEventListener('scroll', onscrollHandler, true);
		document.getElementById("sfd").addEventListener('keyup', searchFromBegin, true); 
		document.getElementById("sfd").addEventListener('blur', hideSearch, true); 
	} else if (document.attachEvent){
		// IE style
		document.attachEvent('onkeydown', onkeydownHandler);
		document.attachEvent('onkeypress', onkeypressHandler);
		//document.attachEvent('onclick', onclickHandler); 
		window.attachEvent('onscroll', onscrollHandler);
		document.getElementById("sfd").attachEvent('onkeyup', searchFromBegin); 
		document.getElementById("sfd").attachEvent('onblur', hideSearch); 
	}
}

var searching = false;

function onscrollHandler(ev) {
	if (searching && curFocused != null) {
		var sfd = document.getElementById("sfd");
		sfd.style.top = oTop(curFocused);
	}
}

function hideSearch(e) {
	searching = false;
	var sfd = document.getElementById("sfd");

	sfd.style.display = 'none';
	sfd.style.visibility = 'hidden';

	return false;
}

function isChar(code) {
	return code == 32 || (code >= 48 && code <=90) || (code >= 96 && code <=111) || code >= 186;
}

function showSearch(divTbl) {
	searching = true;
	var sfd = document.getElementById("sfd");

	var tables = divTbl.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");

	var selRowsStr = table.getAttribute("selectedRows");
	//console.log(selRowsStr+"  |06");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}
	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];
	var curRow = parseInt(selRow);
	
	var curObj = tbody.rows[curRow];
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var curCell = curObj.cells[curCol];
	sfd.style.display = 'block';
	sfd.style.visibility = 'visible';
	sfd.style.left = oLeft(curCell) + 3;
	sfd.style.top = oTop(divTbl);
	sfd.style.width = curCell.offsetWidth - 6 + "px";
	sfd.style.height = "20px";
	sfd.value = "";
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
		var tables = curFocused.getElementsByTagName('table');
		var table = tables[tables.length - 1];
		var tbody = table.getElementsByTagName('tbody')[0];

		var selCol = table.getAttribute("selectedCol");
		var curCol = (selCol != null) ? parseInt(selCol) : -1;
		if (curCol == -1) curCol = 0;

		var selRowsStr = table.getAttribute("selectedRows");
	//	console.log(selRowsStr+"  |07");
		if (selRowsStr == null || selRowsStr.length == 0) {
			if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
			else selRowsStr = "0";
		}

		var selRows = selRowsStr.split(',');
		var selRow = selRows[selRows.length - 1];

		var curRow = parseInt(selRow);

		if (prev) {

			var k = curRow;

			for (i = k - 1; i>=0; i--) {
				var cell = tbody.rows[i].cells[curCol];
				var txt = getFirstTextNode(cell, hasImage(cell)).toLowerCase();
				if (txt.length >= str.length && txt.substring(0, str.length) == str) {
					sfd.style.color = "black";
					break;
				}
			}
			if (i == -1 && k >= 0) {
				for (i = tbody.rows.length - 1; i > k; i--) {
					var cell = tbody.rows[i].cells[curCol];
					var txt = getFirstTextNode(cell, hasImage(cell)).toLowerCase();
					if (txt.length >= str.length && txt.substring(0, str.length) == str) {
						sfd.style.color = "black";
						break;
					}
				}
			}

			if (i != curRow) {
				moveUp(true, curFocused, i);
			}
		} else {
			var k = (next) ? curRow + 1 : 0;

			for (i = k; i < tbody.rows.length; i++) {
				var cell = tbody.rows[i].cells[curCol];
				var txt = getFirstTextNode(cell, hasImage(cell)).toLowerCase();
				if (txt.length >= str.length && txt.substring(0, str.length) == str) {
					sfd.style.color = "black";
					break;
				}
			}
			if (i == tbody.rows.length && k > 1) {
				for (i = 0; i < k; i++) {
					var cell = tbody.rows[i].cells[curCol];
					var txt = getFirstTextNode(cell, hasImage(cell)).toLowerCase();
					if (txt.length >= str.length && txt.substring(0, str.length) == str) {
						sfd.style.color = "black";
						break;
					}
				}
			}
			if (i == tbody.rows.length) {
				if (!next) sfd.style.color = "red";
			} else if (i != curRow) {
				moveUp(true, curFocused, i);
			}
		}
	}
	if (searching && (next || prev)) {
		return false;
	}
	return true;
}

function onclickHandler(ev) {
	lastChar = "";
	ev = ev || window.event; // for IE
	var target = ev.srcElement || ev.target;
	if (target != null) {
		var focusableTarget = getFocusableTarget(target);
		if (focusableTarget != null && focusableTarget != curFocused) {
			focusTarget(focusableTarget);
		}
	}
	return true;
}

function getFocusableTarget(target) {
	while (target != null && target.getAttribute) {
		var f = target.getAttribute("isf");
		if (f) return target;
		target = target.parentNode;
	}
	return null;
}

function onkeypressHandler(ev) {
	ev = ev || window.event; // for IE
	var charCode = ev.which || ev.keyCode;

	if (!searching && isTableFocused() && notEmptyTable()) {
		if (isCurrentCellEditable() && !isCurrentCellEditing()) {
			lastChar += String.fromCharCode(charCode);
			return false;
		}
	}
	return true;
}

function onkeydownHandler(ev) {
	ev = ev || window.event; // for IE
	var charCode = ev.which || ev.keyCode;

	/*  if (curFocused != null && curFocused.onkeydown != null) {
  	return;
  }
	 */
	if (charCode == 9 && (!(isTableFocused() && isCurrentCellEditable())
			|| (!searching && isTableFocused() && ev.ctrlKey))) {
		var res = false;

		if (isMemoFocused() && !ev.ctrlKey) {
			insertAtCursor(curFocused, String.fromCharCode(9));
			res = false;
		} 
		else if (ev.shiftKey) res = focusPrev(curFocused);//ev.srcElement || ev.target);
		else res = focusNext(curFocused);//ev.srcElement || ev.target);

		if (!res) {
			ev.cancelBubble = true;
			ev.returnValue = false;

			if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
	} else if ((charCode == 13 || charCode == 32) && isHyperFocused()) {
		curFocused.click();
		return false;	
	} else if (charCode == 13 && !isSelectFocused() && !isMemoFocused() && !(isTableFocused() && isCurrentCellEditable())) {
		var res = false;
		if (curFocused != null && curFocused.getAttribute("alf") != null) {
			curFocused.blur(); curFocused.focus(); curFocused.value = "";
			res = false;
		} else {
			res = focusNext(curFocused);//ev.srcElement || ev.target);
		}
		if (!res) {
			ev.cancelBubble = true;
			ev.returnValue = false;

			if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
	} else if (!searching && charCode == 13 && isTableFocused()) {
		curFocused.focus();
		var res = moveLeft(false, curFocused);
		if (!res) {
			// cancelBubble is supported by IE - this will kill the bubbling process.
			ev.cancelBubble = true;
			ev.returnValue = false;

			//e.stopPropagation works only in Firefox.
			if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
	} else if (!searching && charCode == 9 && isTableFocused()) {
		curFocused.focus();
		var res = false;
		if (ev.shiftKey) res = moveLeft(true, curFocused);
		else res = moveLeft(false, curFocused);
		if (!res) {
			// cancelBubble is supported by IE - this will kill the bubbling process.
			ev.cancelBubble = true;
			ev.returnValue = false;

			//e.stopPropagation works only in Firefox.
			if (ev.stopPropagation) {
				ev.stopPropagation();
				ev.preventDefault();
			}
		}
		return res;
	} else if (!searching && charCode > 36 && charCode < 41 && curFocused != null && isTableFocused() && (!isCurrentCellEditing() || isCurrentCellCheckbox())) {
		var res = false;
		if (charCode == 37) res = moveLeft(true, curFocused);
		else if (charCode == 38) res = moveUp(true, curFocused, -1);
		else if (charCode == 39) res = moveLeft(false, curFocused);
		else if (charCode == 40) res = moveUp(false, curFocused, -1);

		if (!res) {
			// cancelBubble is supported by IE - this will kill the bubbling process.
			ev.cancelBubble = true;
			ev.returnValue = false;

			//e.stopPropagation works only in Firefox.
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
		if (!searching && isTableFocused() && notEmptyTable() && isChar(charCode)) {
			if (charCode == 32 && isCurrentCellCheckbox()) {
				clickCurrentCellCheckbox();
			}	else if (isCurrentCellEditing()) {
				return true;
			}	else if (isCurrentCellEditable()) {
				clickCurrentCell();
				lastChar = "";
				return true;
			} else {
				showSearch(curFocused);
				var sfd = document.getElementById("sfd");
				sfd.focus();
			}
		}
	}
	return true;
}

function insertAtCursor(myField, myValue) {
	//IE support
	if (document.selection) {
		//in effect we are creating a text range with zero
		//length at the cursor location and replacing it
		//with myValue
		var sel = document.selection.createRange();
		sel.text = myValue;
		//Mozilla/Firefox/Netscape 7+ support
	} else if (myField.selectionStart || myField.selectionStart == '0') {
		//Here we get the start and end points of the
		//selection. Then we create substrings up to the
		//start of the selection and from the end point
		//of the selection to the end of the field value.
		//Then we concatenate the first substring, myValue,
		//and the second substring to get the new value.
		var startPos = myField.selectionStart;
		var endPos = myField.selectionEnd;
		myField.value = myField.value.substring(0, startPos) + myValue + myField.value.substring(endPos, myField.value.length);
		myField.setSelectionRange(endPos+myValue.length, endPos+myValue.length);
	} else {
		myField.value += myValue;
	}
}

function isTableFocused() {
	return (curFocused != null && curFocused.tagName.toLowerCase() == "div" && curFocused.className.toLowerCase() == "tbl");
}

function isHyperFocused() {
	return (curFocused != null && curFocused.tagName.toLowerCase() == "a");
}

function isSelectFocused() {
	return (curFocused != null && curFocused.tagName.toLowerCase() == "select");
}

function isMemoFocused() {
	return (curFocused != null && curFocused.tagName.toLowerCase() == "textarea");
}

function notEmptyTable() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];
	return (tbody.rows.length > 0);
}

function isCurrentCellEditable() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var selRowsStr = table.getAttribute("selectedRows");
//	console.log(selRowsStr+"  |08");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];

	var curRow = parseInt(selRow);

	if (tbody.rows.length > curRow) {
		var tr = tbody.rows[curRow];
		var td = tr.cells[curCol];

		return ((td.onclick != null && ("" + td.onclick).indexOf("getCellEditor") > -1)
				|| (td.getElementsByTagName("input").length > 0)
				|| (td.getElementsByTagName("select").length > 0)
		);
	}
	return false;
}

function isCurrentCellEditing() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var selRowsStr = table.getAttribute("selectedRows");
//	console.log(selRowsStr+"  |09");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];

	var curRow = parseInt(selRow);

	if (tbody.rows.length > curRow) {
		var tr = tbody.rows[curRow];
		var td = tr.cells[curCol];
		return ((td.getElementsByTagName("input").length > 0)
				|| (td.getElementsByTagName("select").length > 0)
		);
	}
	return false;
}

function isCurrentCellCheckbox() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var selRowsStr = table.getAttribute("selectedRows");
	//console.log(selRowsStr+"  |010");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];

	var curRow = parseInt(selRow);

	var tr = tbody.rows[curRow];
	var td = tr.cells[curCol];

	var inps = td.getElementsByTagName("input");
	if (inps.length > 0) {
		return inps[0].getAttribute("type").toLowerCase() == "checkbox";
	}

	return false;
}

function clickCurrentCellCheckbox() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var selRowsStr = table.getAttribute("selectedRows");
//	console.log(selRowsStr+"  |011");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];

	var curRow = parseInt(selRow);

	var tr = tbody.rows[curRow];
	var td = tr.cells[curCol];

	var inps = td.getElementsByTagName("input");
	if (inps.length > 0 && inps[0].getAttribute("type").toLowerCase() == "checkbox") {
		inps[0].click();
	}
}

function clickCurrentCell() {
	var tables = curFocused.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var selCol = table.getAttribute("selectedCol");
	var curCol = (selCol != null) ? parseInt(selCol) : -1;
	if (curCol == -1) curCol = 0;

	var selRowsStr = table.getAttribute("selectedRows");
	//console.log(selRowsStr+"  |012");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var selRow = selRows[selRows.length - 1];

	var curRow = parseInt(selRow);

	var tr = tbody.rows[curRow];
	var td = tr.cells[curCol];

	td.click();
}

var curFocused = null;
var curClass = null;
var focusColor = "#d070f0";//ed958a";// "#dd3045";
var focusColorFF = "rgb(208, 112, 240)";//ed958a";// "#dd3045";
var curBorder = null;
var borderStyle = null;

function focusTarget(toElem) {
	if (toElem != null && toElem != curFocused) {
/*		try {
			try { if (curFocused != null) curFocused.blur(); } catch (ex) {}
			toElem.focus();
		} catch (ex) {
			try { if (curFocused != null) curFocused.focus(); } catch (ex) {}
			return true;
		}*/
		if (curFocused != null && (curFocused.style.borderColor == focusColor || curFocused.style.borderColor == focusColorFF)) {
			curFocused.style.borderColor = curClass;
			curFocused.style.borderStyle = curBorder;
		}
		curFocused = toElem;
		curClass = toElem.style.borderColor;
		toElem.style.borderColor = focusColor;
		curBorder = toElem.style.borderStyle;
		toElem.style.borderStyle = "solid";
		toElem.style.borderWidth = 1;
		return false;
	}

	return true;
}

function focusPrev(cur) {
/*	var toElem = (cur != null) ? getPrevFocusable(cur) : getLastFocusableChild(document.getElementsByTagName("body")[0]);
	if (toElem == null && parent != null && parent.menu != null) {
		parent.menu.focusPrev(true);
		parent.menu.focus();
		return false;
	} else {
		if (toElem == null) toElem = getLastFocusableChild(document.getElementsByTagName("body")[0]);
		if (toElem != null) {
			if (cur != null && (cur.style.borderColor == focusColor || cur.style.borderColor == focusColorFF)) {
				cur.style.borderColor = curClass;
				cur.style.borderStyle = curBorder;
			}
			curFocused = toElem;
			curClass = toElem.style.borderColor;
			toElem.style.borderColor = focusColor;
			curBorder = toElem.style.borderStyle;
			toElem.style.borderStyle = "solid";
			toElem.style.borderWidth = 1;
			try {
				if (cur != null) cur.blur();
				toElem.focus();
			} catch (ex) {
				focusPrev(toElem);
			}
		}
		return false;
	}
*/
	return true;
}

function focusNext(cur) {
/*	if (cur) {
		var toElem = getNextFocusable(cur);
		if (toElem == null && parent != null && parent.sts != null) {
			parent.sts.focusNext(true);
			parent.sts.focus();
		} else {
			if (toElem == null) toElem = getLastFocusableChild(document.getElementsByTagName("body")[0]);
			if (toElem != null) {
				if (cur.style.borderColor == focusColor || cur.style.borderColor == focusColorFF) {
					cur.style.borderColor = curClass;
					cur.style.borderStyle = curBorder;
				}
				curFocused = toElem;
				curClass = toElem.style.borderColor;
				toElem.style.borderColor = focusColor;
				curBorder = toElem.style.borderStyle;
				toElem.style.borderStyle = "solid";
				toElem.style.borderWidth = 1;
				try {
					cur.blur();
					toElem.focus();
				} catch (ex) {
					focusNext(toElem);
				}
			}
		}
		return false;
	} else {
		var toElem = getDefaultFocusableChild(document.getElementsByTagName("body")[0]);
		if (toElem == null) toElem = getFirstFocusableChild(document.getElementsByTagName("body")[0]);

		if (toElem != null) {
			if (curFocused != null && (curFocused.style.borderColor == focusColor || curFocused.style.borderColor == focusColorFF)) {
				curFocused.style.borderColor = curClass;
				curFocused.style.borderStyle = curBorder;
				try {
					curFocused.blur();
				} catch (ex) {
				}
			}
			curFocused = toElem;
			curClass = toElem.style.borderColor;
			toElem.style.borderColor = focusColor;
			curBorder = toElem.style.borderStyle;
			toElem.style.borderStyle = "solid";
			toElem.style.borderWidth = 1;
			try {
				if (parent != null && parent.srv != null) {
					parent.srv.focus();
				}
				toElem.focus();
			} catch (ex) {
				focusNext(toElem);
			}
			return false;
		}
	}
*/	return true;
}

function focusCurrent() {
	if (curFocused != null) {
		if (curFocused.style.borderColor != focusColor && curFocused.style.borderColor != focusColorFF) {
			curClass = curFocused.style.borderColor;
			curFocused.style.borderColor = focusColor;
			curBorder = curFocused.style.borderStyle;
			curFocused.style.borderStyle = "solid";
			curFocused.style.borderWidth = 1;
		}
		try { curFocused.focus(); } catch (ex) {}
	}
	else focusNext(null);
}

function blurCurrent() {
	if (curFocused != null) {
		curFocused.style.borderColor = curClass;
		curFocused.style.borderStyle = curBorder;
		try {
			curFocused.blur();
		} catch (ex) {
		}
	}
}

function getNextFocusable(node) {
	var res = null;
	var nn = node.nextSibling;
	while (nn == null && node.parentNode != null) {
		nn = node.parentNode.nextSibling;
		node = node.parentNode;
	}
	while (nn != null) {
		if (nn.nodeType == 1) {
			var focusable = nn.getAttribute("isf");
			if (focusable) return nn;

			res = getFirstFocusableChild(nn);
			if (res != null) return res;
		}
		node = nn;
		nn = nn.nextSibling;
		while (nn == null && node.parentNode != null) {
			nn = node.parentNode.nextSibling;
			node = node.parentNode;
		}
	}
	return null;
}

function getFirstFocusableChild(node) {
	var res = null;

	var children = node.childNodes;
	var i = 0;
	for (i = 0; i < children.length; i++) {
		var child = children[i];
		if (child.nodeType == 1) {
			focusable = child.getAttribute("isf");
			if (focusable) return child;
			res = getFirstFocusableChild(child);
			if (res != null) return res;
		}
	}
	return res;
}

function getDefaultFocusableChild(node) {
	var res = null;

	var children = node.childNodes;
	var i = 0;
	for (i = 0; i < children.length; i++) {
		var child = children[i];
		if (child.nodeType == 1) {
			focusable = child.getAttribute("dff");
			if (focusable) return child;
			res = getDefaultFocusableChild(child);
			if (res != null) return res;
		}
	}
	return res;
}

function getPrevFocusable(node) {
	var nn = node.previousSibling;
	while (nn == null && node.parentNode != null) {
		nn = node.parentNode.previousSibling;
		node = node.parentNode;
	}
	while (nn != null) {
		if (nn.nodeType == 1) {
			var focusable = nn.getAttribute("isf");
			if (focusable) return nn;

			var children = nn.childNodes;
			var i = 0;
			for (i = children.length - 1; i>=0; i--) {
				var child = children[i];
				if (child.nodeType == 1) {
					focusable = child.getAttribute("isf");
					if (focusable) return child;
					res = getLastFocusableChild(child);
					if (res != null) return res;
				}
			}
		}
		node = nn;
		nn = nn.previousSibling;
		while (nn == null && node.parentNode != null) {
			nn = node.parentNode.previousSibling;
			node = node.parentNode;
		}
	}
	return null;
}

function getFirstTextNode(node, hasImage) {
	var res = "";

	if (node != null) {
		var children = node.childNodes;
		var i = 0;
		for (i = 0; i < children.length; i++) {
			var child = children[i];
			if (child.nodeType == 1) {
				res = getFirstTextNode(child, hasImage);
				if (res != null && res.length > 0) return res;
			} else if (child.nodeType == 3) {
				if (child.nodeValue != null
					&& child.nodeValue.length > 0) {
					if (hasImage)
						return trim(child.nodeValue);
					else
						return child.nodeValue;
				} else {
					res = getFirstTextNode(child, hasImage);
					if (res != null && res.length > 0) return res;
				}
			}

		}
	}
	return res;
}

function hasImage(node) {
	var res = null;

	if (node != null) {
		var children = node.childNodes;
		var i = 0;
		for (i = 0; i < children.length; i++) {
			var child = children[i];
			if (child.nodeType == 1) {
				if (child.nodeName.toLowerCase() == "img") return true;
				res = hasImage(child);
				if (res != null) return res;
			}
		}
	}
	return res;
}

function trim(s) {
	s = s.replace(/(^\s+)|(\s+$)/g, '');
	return s;
}

function getLastFocusableChild(node) {
	var res = null;

	var children = node.childNodes;
	var i = 0;
	for (i = children.length - 1; i>=0; i--) {
		var child = children[i];
		if (child.nodeType == 1) {
			focusable = child.getAttribute("isf");
			if (focusable) return child;
			res = getLastFocusableChild(child);
			if (res != null) return res;
		}
	}
	return res;
}

function moveLeft(left, divTbl) {
	var tables = divTbl.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];

	var rowsCount = tbody.getElementsByTagName("tr").length;
	if (rowsCount == 0) return true; 
	var id = divTbl.id;

	var selCol = table.getAttribute("selectedCol");
	var selRowsStr = table.getAttribute("selectedRows");
//	console.log(selRowsStr+"  |013");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var rowId = selRows[selRows.length - 1];

	var oldRow = parseInt(rowId);

	var oldCol = (selCol != null) ? parseInt(selCol) : -1;
	var curCol = oldCol;
	var curObj = getRowById(tbody, rowId);

	var colsCount = curObj.getElementsByTagName("td").length;
	if (left) {
		curCol = (curCol > 0) ? curCol - 1 : colsCount - 1;
	} else {
		curCol = (curCol < colsCount - 1) ? curCol + 1 : 0;
	}

	var colChanged = true;
	table.setAttribute("selectedCol", curCol);

	if (selRowsStr != "" + rowId) {
		table.setAttribute("selectedRows", rowId);
	//	console.log(rowId+"|6");
		var zebra1 = table.getAttribute("zebra1");
		var zebra2 = table.getAttribute("zebra2");
		removeSelectionIfc(tbody, zebra1, zebra2, selRows, rowId);
		curObj.className = "selected";
		curObj.style.backgroundColor = '';

		rowChanged = true;
	}

	if (oldCol > -1)
		curObj.cells[oldCol].className = "";
	if (curCol > -1)
		curObj.cells[curCol].className = "selected";

	var curCell = curObj.cells[curCol];

	var leftPos = curCell.offsetLeft;
	var leftVis = sLeft(divTbl);

	if (leftVis > leftPos) setSLeft(divTbl, leftPos);
	var rightPos = curCell.offsetLeft + curCell.offsetWidth;
	var rightVis = sLeft(divTbl) + pWidth(divTbl);
	if (rightVis < rightPos) setSLeft(divTbl, rightPos - pWidth(divTbl));

	var rowChanged = false;

	if (rowChanged || colChanged) {
		var post = "xml=1&trg=frm&cmd=mod&com=sct&id="+id;
		if (colChanged)
			post += "&col="+curCol;
		if (rowChanged)
			post += "&row="+rowId;

		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
		return false;
	}
	else return true;
}

function moveUp(up, divTbl, rowNum) {
	var tables = divTbl.getElementsByTagName('table');
	var table = tables[tables.length - 1];
	var tbody = table.getElementsByTagName('tbody')[0];
	var id = divTbl.id;

	var selCol = table.getAttribute("selectedCol");
	var selRowsStr = table.getAttribute("selectedRows");
	//console.log(selRowsStr+"  |014");
	if (selRowsStr == null || selRowsStr.length == 0) {
		if (tbody.rows.length > 0) selRowsStr = "" + tbody.rows[0].id;
		else selRowsStr = "0";
	}

	var selRows = selRowsStr.split(',');
	var rowId = selRows[selRows.length - 1];

	var oldRow = parseInt(rowId);
	var curRow = oldRow;

	if (rowNum > -1) {
		curRow = rowNum;
	} else if (up) {
		curRow = (curRow > 0) ? curRow - 1 : curRow;
	} else {
		var rowsCount = tbody.getElementsByTagName("tr").length;
		curRow = (curRow < rowsCount - 1) ? curRow + 1 : curRow;
	}

	var curObj = getRowById(tbody, "" + curRow);

	var curCol = (selCol != null) ? parseInt(selCol) : -1;

	var rowChanged = false;

	if (selRows != "" + curRow) {

		table.setAttribute("selectedRows", "" + curRow);
		var zebra1 = table.getAttribute("zebra1");
		var zebra2 = table.getAttribute("zebra2");
		removeSelectionIfc(tbody, zebra1, zebra2, selRows, "" + curRow);
		curObj.className = "selected";
		curObj.style.backgroundColor = '';

		rowChanged = true;

		if (curCol > -1)
			curObj.cells[curCol].className = "selected";

		var upPos = curObj.offsetTop;
		if (searching) upPos -= 40;
		var upVis = sTop(divTbl);
		if (upVis > upPos) setSTop(divTbl, upPos);

		var downPos = table.offsetTop + curObj.offsetTop + curObj.offsetHeight;
		var downVis = sTop(divTbl) + pHeight(divTbl);
		if (downVis < downPos) setSTop(divTbl, downPos - pHeight(divTbl));
	}

	if (rowChanged) {
		var post = "xml=1&trg=frm&cmd=mod&com=sct&id="+id+"&row="+curRow;

		var req = createAsync();
		var func = "rollbackResponse";
		sendAsync(req, post, func, true);
		return false;
	}
	else return true;
}

function pHeight(e) {
	return e.offsetHeight;
}

function sTop(e) {
	return e.scrollTop;
}

function oTop(e) {
	var res = e.offsetTop;

	while (e != document.body) {
		e = e.offsetParent;
		res += e.offsetTop;
	}

	return res;
}

function oLeft(e) {
	var res = e.offsetLeft - e.scrollLeft;

	while (e != document.body) {
		e = e.offsetParent;
		res += e.offsetLeft - e.scrollLeft;
	}

	return res;
}

function setSTop(e, st) {
	e.scrollTop = st;
}

function pWidth(e) {
	return e.offsetWidth;
}

function sLeft(e) {
	return e.scrollLeft;
}

function setSLeft(e, st) {
	e.scrollLeft = st;
}
