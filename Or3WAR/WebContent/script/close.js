var isPermanent = false;
var tm = 0;
var timer;

function cancelProgram() {
	var url = location.pathname + "?xml=1&trg=frm&cmd=ext";
	if (url.indexOf("/") > 0)
		url = "/" + url;
	url += "&noCache=" + (new Date).getTime();
	var req;
	if (window.XMLHttpRequest) {
		req = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
	}
	req.open("GET", url, true);
	req.onreadystatechange = function() {
	};
	req.send(null);
}

function enableWindow(perform) {
/*	if (perform && !isPermanent) {
		var fone = document.getElementById("allfone");
		fone.style.visibility = 'hidden';
		var ifone = document.getElementById("iallfone");
		if (ifone != null)
			ifone.style.visibility = 'hidden';

		var im = document.getElementById("loading");
		im.style.visibility = 'hidden';

		im = document.getElementById("loadtext");
		im.style.visibility = 'hidden';

		im = document.getElementById("loadbtn");
		im.style.visibility = 'hidden';
	}
*/}

function hideRetry() {
	var im = document.getElementById("loadbtn");
	im.style.visibility = 'hidden';
}

function disableWindow(msg, permanently, t) {
/*	isPermanent |= permanently;
	var fone = document.getElementById("allfone");
	fone.style.display = '';
	fone.style.visibility = 'visible';
	var ifone = document.getElementById("iallfone");
	if (ifone != null) {
		ifone.style.display = '';
		ifone.style.visibility = 'visible';
	}

	var im = document.getElementById("loading");
	im.style.left = screen.availWidth / 2 - 15;
	im.style.top = screen.availHeight / 2 - 15;
	im.style.display = '';
	im.style.visibility = 'visible';

	im = document.getElementById("loadtext");
	im.style.left = screen.availWidth / 2 - 120;
	im.style.top = screen.availHeight / 2 + 20;
	im.style.display = '';
	im.style.visibility = 'visible';
	im.value = msg;
	
	if (timer != null) clearTimeout(timer);

	if (t > 0) {
		tm = t;
		timer = setTimeout(function() {changeText(im, msg);}, 600);
		
		var bim = document.getElementById("loadbtn");
		bim.style.left = screen.availWidth / 2 - 65;
		bim.style.top = screen.availHeight / 2 + 60;
		bim.style.display = '';
		bim.style.visibility = 'visible';
	}
*/}

function changeText(txt, msg) {	
	msg = msg.replace("" + tm, "" + --tm);
	txt.value = msg;
	if (tm > 0)
		timer = setTimeout(function() {changeText(txt, msg);}, 1000);
}

function retryNow() {
	if (timer != null) clearTimeout(timer);
	if (window.frames.menu && window.frames.menu.retryNow) window.frames.menu.retryNow();
	if (window.frames.srv && window.frames.srv.retryNow) window.frames.srv.retryNow();
	if (window.frames.srv && window.frames.srv.retryNow2) window.frames.srv.retryNow2();
	if (window.frames.srv && window.frames.srv.srv2 
			&& window.frames.srv.srv2.retryNow) window.frames.srv.srv2.retryNow();
	if (window.frames.srv && window.frames.srv.srv2 
			&& window.frames.srv.srv2.tsk && window.frames.srv.srv2.tsk.retryNow) window.frames.srv.srv2.tsk.retryNow();
	if (window.frames.srv && window.frames.srv.srv2 
			&& window.frames.srv.srv2.prs && window.frames.srv.srv2.prs.retryNow) window.frames.srv.srv2.prs.retryNow();
	if (window.frames.srv && window.frames.srv.srv2 
			&& window.frames.srv.srv2.prs && window.frames.srv.srv2.prs.retryNow2) window.frames.srv.srv2.prs.retryNow2();
}

function showPassword(msg) {
	var im = document.getElementById("pwdDlg");
	im.style.left = screen.availWidth / 2 - 120;
	im.style.top = screen.availHeight / 2 - 100;
	im.style.display = '';
	im.style.visibility = 'visible';

	im = document.getElementById("pwdLbl");
	im.innerText = msg;

	im = document.getElementById("pwdInp");
}

function hideAll(in_this) {
	in_this.style.visibility = 'hidden';

	var ifone = document.getElementById("ifone");

	if (ifone != null)
		ifone.style.visibility = 'hidden';

	var allDivs = document.getElementsByTagName('div');
	for ( var i = 0; i < allDivs.length; i++) {
		if (allDivs[i].className == "report") {
			allDivs[i].style.visibility = 'hidden';
		}
	}
}

function HoverMe(in_this) {
	in_this.style.backgroundColor = '#d8dde7';
	in_this.style.cursor = 'pointer';
}

function UnhoverMe(in_this) {
	in_this.style.backgroundColor = '';
	in_this.style.cursor = '';
}
