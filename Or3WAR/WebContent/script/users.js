function deleteUser(sid) {
  var url = location.pathname;
  if (url.indexOf("/") > 0) url = "/" + url;
  var post = "trg=dlu&sid="+sid;
  post += "&noCache=" + (new Date).getTime();

  var req;
  if (window.XMLHttpRequest) {
    req = new XMLHttpRequest();
  } else if (window.ActiveXObject) {
    req = new ActiveXObject("Microsoft.XMLHTTP");
  }

  req.open("POST", url, true);
  req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
 // req.setRequestHeader("Content-Length", post.length);
  req.onreadystatechange = function() {};
  req.send(post);

	var obj = document.getElementById("users");
	var tbody = obj.getElementsByTagName('tbody')[0];
  var row = getRowById(tbody, sid);
  tbody.deleteRow(row.rowIndex);
}

function menuBtnOver(obj) {
	obj.className = "menuBtnOver";
}

function menuBtnOut(obj) {
	obj.className = "menuBtn";
}

function getRowById(tbody, rowId) {
  var row = null;
  for (n = 0; n<tbody.rows.length; n++) {
    if (tbody.rows[n].id == rowId) {
      row = tbody.rows[n];
      break;
    }
  }
  return row;
}
