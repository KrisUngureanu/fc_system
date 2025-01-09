import {Util, DataChecker} from './util.js';

export class Notifications {
	
	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
	notificationPageSize = 50;
	currentPageNumber = 1;
	total = 0;
	
	sortColumn = "sortInDate_asc"
	statusReadingNotification = 0;
	dateParseN1 = 0;
	dateParseN2 = 0;
	notificationSearch = 0;
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		
		$('#notifications-pager').pagination({
		    pageSize: this_.notificationPageSize,
		    pageNumber: 1,
		    pageList: [10, 50, 100, 150, 200],
		    layout: ['list','sep','first','prev','sep','links','sep','next','last','sep','manual'],
		    showPageList: true,

			onSelectPage:function(pageNumber, pageSize){
				Util.blockPage();
				$(this).pagination('loading');
				
				this_.currentPageNumber = pageNumber;
				this_.notificationPageSize = pageSize;
				
				this_.loadNotifications();
				
				$(this).pagination('loaded');
			}
		});
		
		this_.loadNotifications();
		
		$("body").on('click', '.notification-line .notification-title', function() {
			let jThis = $(this);
			this_.app.nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');
		});
		
	}
	
	loadNotifications() {
		// если доступно меню Уведомления пользователю
		if ($('#notifications-select-type').length > 0) {
			try {
				Util.blockPage();
				let this_ = this;
				
				let index = parseInt($('#notifications-select-type').val());
				this_.statusReadingNotification = index;
		
				let dateN1 = $("#dateN1").datebox("getValue");
				this_.dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
				let dateN2 = $("#dateN2").datebox("getValue");
				this_.dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
		
				this_.notificationSearch = $('#notifications-search-text').val().length > 0
											? $('#notifications-search-text').val() : "0";
			
				var end = 0;
				if (this_.total == 0)
					end = this_.notificationPageSize;
				else if (this_.total <= this_.currentPageNumber * this_.notificationPageSize)
					end = this_.total
				else
					end = this_.currentPageNumber * this_.notificationPageSize;
		
				var par = {"sfunc" : 1, "cls": "XmlUtil", "name": "getNewNotificationOrders", 
					"arg0": 1, "arg1": (this_.currentPageNumber-1) * this_.notificationPageSize, "arg2": end,
					"arg3": this_.dateParseN1, "arg4": this_.dateParseN2, 
					"arg5": this_.notificationSearch, "arg6": this_.statusReadingNotification, 
					"arg7": this_.sortColumn};
			
				this.app.query(par).then(response => {
					if (response.status === 200) {
						response.json().then(json => {
							DataChecker.checkData(json).then(data => {
								if (data.totalUnread > 0)
									$('#notifications-unread-counter').text(data.totalUnread);
									
								$('#notifications-pager').pagination({
									total: data.totalFilter ? data.totalFilter : data.total,
								});
								
								this_.total = data.total;
								
								$('#notifications-total-counter').text((data.totalFilter ? (data.totalFilter + '/') : '') + data.total);
								
								$('#notifications-panel .notifications-table .pcontent').empty();
								
								if (data.rows) {
									$.each(data.rows, function(i, o) {
										$.each(o, function(key, order) {
											$('#notifications-panel .notifications-table .pcontent').append(this_.makeNotification(key, order));
										});
									});
								}						
								$('body').unblock();
							});
						});
					}
				});
			} catch(e) {
				$('body').unblock();
				console.log("упал на notifications " + e)
			}
		}
	}
	
	makeNotification(key, order) {
		var orderDiv = $("<div class='notification-line'></div>").attr('id', key);
		if (order.openDate == "")
			orderDiv.addClass('notification-unread');
								
		var titleDiv = $("<div class='notification-title'></div>").html(order.title)
			.attr('iter', order.iter).attr('proc', order.proc).attr('uid', key).attr('type', 'notif');;
		
		var authorDiv = $("<div class='notification-author'></div>").text(order.from);
		var date1Div = $("<div class='notification-date'></div>").text(order.inDate);
		var date2Div = $("<div class='notification-date'></div>").text(order.openDate);
		var date3Div = $("<div class='notification-date'></div>").text(order.awereDate);
		
		orderDiv.append(titleDiv).append(authorDiv).append(date1Div).append(date2Div).append(date3Div);
		
		return orderDiv;
		//var func = order.iter == '' ? "href='javascript:openOrder(\""+order.proc+"\",\""+key+"\")'" : "href='javascript:startOrderIn(\""+order.iter+"\",\""+order.proc+"\",\""+key+"\")'";
	}
	
	deleteNotification(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type == "notif") {
				$.each(o, function(i, uid) {
					let existOrderDiv = $('#notifications-panel .notification-line[id="' + uid + '"]');
					let isImportant = existOrderDiv.hasClass('notification-unread');
					existOrderDiv.remove();
					
					var tmp = $('#notifications-total-counter').text();
					var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('#notifications-total-counter').text(oldCount - 1);
					
					if (isImportant) {
						tmp = $('#notifications-unread-counter').text();
						oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
						if (oldCount == 1) {
							$('#notifications-unread-counter').hide();
						} else {
							$('#notifications-unread-counter').show().text(oldCount - 1);
						}
					}
				});
			}
		});
	}
	
	updateNotification(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type == "notif") {
				$.each(o, function(i, uid) {
					let par = {"sfunc" : 1, "cls": "XmlUtil", "name": "getNewNotificationOrders", "arg0": uid};
					_this.app.post(par).then(data => {
						if (data.rows) {
							$.each(data.rows, function(type1, o1) {
								$.each(o1, function(key, order) {
									var delta = 0;
									let orderDiv = _this.makeNotification(key, order);
									delta += (orderDiv.hasClass('notification-unread')) ? 1 : 0;
									let existOrderDiv = $('#notifications-panel .notification-line[id="' + key + '"]');
									delta -= (existOrderDiv.hasClass('notification-unread')) ? 1 : 0;
									
									if (existOrderDiv.length > 0) {
										existOrderDiv.replaceWith(orderDiv);
									} else {
										var firstRow = $('#notifications-panel .notification-line:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv);
										else {
											$('#notifications-panel .notifications-table .pcontent').append(orderDiv);
										}
										
										var tmp = $('#notifications-total-counter').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('#notifications-total-counter').text(oldCount + 1);
									}
									
									if (delta != 0) {
										var tmp = $('#notifications-unread-counter').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										if (oldCount == 1) {
											$('#notifications-unread-counter').hide();
										} else {
											$('#notifications-unread-counter').show().text(oldCount + delta);
										}
									}
													
								});
							});
						}
					});
				});
			}
		});
	}
	
	notificationsProcessing(json) {
		console.log("notificationsProcessing = " + json);
	}
	
	deleteNotifications(json) {
		console.log("deleteNotifications = " + json);
	}
	
	loadNotifications2() {
		this.currentPageNumber = 1;
		$('#notifications-pager').pagination('refresh',{
			pageNumber: 1
		});
		this.loadNotifications();
	}

	cleanNotificationParams() {
		Util.blockPage();
		let this_ = this;
		let dateN1 = $("#dateN1").datebox("getValue"),
		    dateN2 = $("#dateN2").datebox("getValue");
		if (dateN1 != "" || dateN2 != "") {
			console.log("this3");
			$("#dateN1").datebox("clear");
			$("#dateN2").datebox("clear");
		}
		$('#notifications-select-type option').prop('selected', function() {
			console.log("this5");
			return this.defaultSelected;
		});
		$('#notifications-search-text').val("");
		this_.notificationSearch = 0;
		this_.statusReadingNotification = 0;
		this_.dateParseN1 = 0;
		this_.dateParseN2 = 0;
		this_.sortNotifColumn = "sortInDate_asc";
		// $('#toolNotif td a').removeClass('sortNotif asc desc');
		// $('#sortInDate').addClass('sortNotif').addClass('asc');
		this_.loadNotifications();
	}
}
