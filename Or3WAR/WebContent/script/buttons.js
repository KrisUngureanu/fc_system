function showArchive(){
  var url = location.pathname + "?trg=top&view=arc";
  if (url.indexOf("/") > 0) url = "/" + url;
  url += "&noCache=" + (new Date).getTime();
  window.location.replace(url);

}

function showService(){
  var url = location.pathname + "?trg=top&view=srv";
  if (url.indexOf("/") > 0) url = "/" + url;
  url += "&noCache=" + (new Date).getTime();
  window.location.replace(url);
}

function showDictionary(){
  var url = location.pathname + "?trg=top&view=dic";
  if (url.indexOf("/") > 0) url = "/" + url;
  url += "&noCache=" + (new Date).getTime();
  window.location.replace(url);
}

function disableButton(buttonId, backpage, ocm) {
	if (window != null && window.menu != null && window.menu.disableButton != null)
    window.menu.disableButton(buttonId, backpage, ocm);
  else if (parent != null && parent.menu != null && parent.menu.disableButton != null)
    parent.menu.disableButton(buttonId, backpage, ocm);
  else if (parent != null && parent.parent != null &&  parent.parent.menu != null && parent.parent.menu.disableButton != null)
    parent.parent.menu.disableButton(buttonId, backpage, ocm);
}

function disableWholeWindow(msg, permanently) {
  var w = parent;
  if (w != null && w.disableWindow) {
  	w.disableWindow(msg, permanently);
  }
}

function enableWholeWindow(perform) {
  var w = window;
  if (w != null && w.enableWindow) {
  	w.enableWindow(perform);
  } else if (w != null && w.parent != null && w.parent.enableWindow != null) {
		w.parent.enableWindow(perform);
  }
}

function menuBtnOver(obj) {
  obj.className = "menuBtnOver";
}

function menuBtnOut(obj) {
  obj.className = "menuBtn";
}
