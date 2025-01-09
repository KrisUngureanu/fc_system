function notificationsProcessing(notification) {
	if ($("#notificationsPanel").length == 0) {
		var notificationsPanel = "<div id='notificationsPanel' style='float: right; bottom:" + ($('#chatPanel').length == 0 ? 0 : ($('#chatMainWindow').css('display') === 'none' ? 27 : 371)) + "px;'>" +
								 	"<div class='notificationsHeader bar1 brd1'>" +
								 		"<div>" +
								 			"<table style = 'width: 100%;'>" +
								 				"<tbody>" +
									 				"<tr style='cursor:pointer'>" +
									 					"<td onclick='resizeNotificationsPanel();'><span id='notificationsTitle'>translation['webNotification']</span></td>" +
									 					"<td title='Развернуть' id='toggleNotifications' class='collapsedNotificationsPanel' onclick='resizeNotificationsPanel();'></td>" +
									 					"<td title='Удаление всех уведомлений' id='deleteAllNotifications' onclick='deleteAllNotifications();'></td>" +
									 				"</tr>" +
									 			"</tbody>" +
								 			"</table>" +
								 		"</div>" +
								 	"</div>" +
								 	"<div id='notificationsList' class='brd1' style='display:none'>" +
								 		"<table class='table' id='notificationsTable'></table>" +
								 	"</div>" +
								"</div>";
		$('body').append(notificationsPanel);
	} else {
		 $("#notificationsPanel").show();
	}
	
	var notificationsTable = document.getElementById('notificationsTable');
	
	if (notificationsTable.querySelectorAll("[objId='" + notification.objId + "']").length == 0) {
		var row = notificationsTable.insertRow(0);
		
		row.setAttribute("uid", notification.uid);
		row.setAttribute("cuid", notification.cuid);
		row.setAttribute("objId", notification.objId);
		row.setAttribute("proc", notification.proc);
		row.setAttribute("iter", notification.iter);
		
		var cell1 = document.createElement("td");
		cell1.title='Открытие интерфейса'
		cell1.setAttribute("class", "bord1");
		cell1.onclick = function() {
			startProcessOrOpenInterface(this);
		}
		
		cell1.innerHTML = notification.message;
		
		var cell2 = document.createElement("td");
		cell2.title='Удаление уведомления'
		cell2.setAttribute("class", "bord2");
		cell2.onclick = function() {
			deleteNotification(this);
		}

		row.appendChild(cell1);
		row.appendChild(cell2);
		
		$("#notificationsTitle").text("уведомления (" + notificationsTable.rows.length + ")");
	}
}

function resizeNotificationsPanel() {
	var notificationsTable = document.getElementById('notificationsTable');
	var element = document.getElementById('toggleNotifications');
	if (element.classList.contains('collapsedNotificationsPanel')) {
		element.classList.remove("collapsedNotificationsPanel");
		element.classList.add("restoredNotificationsPanel");
		element.title = "Свернуть";		
		$("#notificationsTitle").text(translation['webNotification2'] + " (" + notificationsTable.rows.length + ")");
	} else if (element.classList.contains('restoredNotificationsPanel')) {
		element.classList.remove("restoredNotificationsPanel");
		element.classList.add("collapsedNotificationsPanel");
		element.title = "Развернуть";
		$("#notificationsTitle").text(translation['webNotification'] + " (" + notificationsTable.rows.length + ")");
	}
	$('#notificationsList').toggle();
}

function deleteAllNotifications() {
	$.messager.confirm('', "Вы действительно хотите безвозвратно удалить все уведомления?", function(e){
		if (e) {
			var notificationsTable = document.getElementById('notificationsTable');
			for (var i = notificationsTable.rows.length - 1; i >= 0; i--) {
				deleteNotification(notificationsTable.rows[i].cells[0]);
			}
		}
	});
}

function startProcessOrOpenInterface(element) {
//	var uid = element.parentNode.getAttribute("uid");
//	var cuid = element.parentNode.getAttribute("cuid");
//	var row = element.parentNode.getAttribute("row");
//	if (document.location.hash.indexOf("cmd=openIfc&uid=" + uid + "&cuid=" + cuid + "&row=" + row) == -1) {
//		document.location.hash = "cmd=openIfc&uid=" + uid + "&cuid=" + cuid + "&row=" + row;
//	}
//	var leftMenuItem = document.getElementById("ui_personInfo");
//	if (!leftMenuItem.parentElement.classList.contains("active")) {
//		leftMenuItem.click();
//	}
	
	var objUid = element.parentNode.getAttribute("uid");
	var intUid = element.parentNode.getAttribute("cuid");
	var proc = element.parentNode.getAttribute("proc");
	var iter = element.parentNode.getAttribute("iter");
	
	checkedObj = checkIfUIDIsCorrect(objUid);
	checkedInt = checkIfUIDIsCorrect(intUid);
	checkedProc = checkIfUIDIsCorrect(proc);
	checkedIter = checkIfUIDIsCorrect(iter);
	if(checkedObj && checkedInt && checkedProc && checkedIter){
		startOrderIn(iter, proc, objUid);
	} else if (checkedObj && checkedInt) {
		document.location.hash = "cmd=openTaskIntf&objUid=" + objUid +"&intUid=" + intUid;
	}
	deleteNotification(element);
}

function deleteNotification(element) {	
	var url = window.mainUrl + "&rnd=" + rnd();
	var par = {};
	par["cmd"] = 'deleteNotification';
	var objId = element.parentNode.getAttribute("objId");
	par["objId"] = objId;
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url,
		data : par,
		success : function(data) {},
		dataType : 'json',
		async : false
	});
	var rowIndex = element.parentNode.rowIndex;
	var notificationsTable = document.getElementById('notificationsTable');
	notificationsTable.deleteRow(rowIndex);
	if (notificationsTable.rows.length == 0) {
		 $("#notificationsPanel").hide();
	} else {
		$("#notificationsTitle").text("уведомления (" + notificationsTable.rows.length + ")");
	}
}

function loadNotifications() {
	var url = window.mainUrl + "&rnd=" + rnd();
	var par = {};
	par["cmd"] = 'loadNotifications';
	par["json"] = 1;
	$.ajax({
		type : 'POST',
		url : url,
		data : par,
		success : function(data) {
			var notificationsPanel = $("#notificationsPanel");
			if (notificationsPanel) {
				notificationsPanel.remove();
			}
			if (data.notifications) {
				$.each(data.notifications, function(i, notification) {
					notificationsProcessing(notification);
				});
			}
		},
		dataType : 'json',
		async : false
	});
}

function deleteNotifications(objids) {
	for (let i in objids) {
		$('tr[objid="' + objids[i] + '"]').remove();
	}
	var notificationsTable = document.getElementById('notificationsTable');
	if (notificationsTable.rows.length == 0) {
		 $("#notificationsPanel").hide();
	} else {
		$("#notificationsTitle").text("уведомления (" + notificationsTable.rows.length + ")");
	}
}

function moveNotPanel(bottom) {
	$('#notificationsPanel').css('bottom', bottom + 'px');
}