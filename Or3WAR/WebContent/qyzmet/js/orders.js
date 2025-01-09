import {DataChecker, Util} from './util.js';
import {Translation} from './translation.js';

export class Orders {
	
	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];

	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		$(".orders-tab").on('click', function() {
			var tab_id = $(this).attr("tab");
			this_.showTab(tab_id);
		});
		
		this_.loadOrdersMy();
		this_.loadOrdersIn();
		this_.loadOrdersOut();
		
		$("body").on('click', '.order-line .order-title', function() {
			let jThis = $(this);
			if (jThis.attr('type') === 'out')
				this_.app.nav.openOrder(null, jThis.attr('iuid'), jThis.attr('iter'), false);
			else
				this_.app.nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');			
		});

		$("body").on('click', '.delete-order', function() {
			let jThis = $(this);
			var msg = Translation.translation['removeProcess'];
			Util.confirmMessage(msg, function(e) {
				if (e) {
					let span = jThis.parent().find('span.title');
					this_.deleteOrder(span.attr('uid'), span.attr('iter'), span.attr('proc'));
				}
			});
		});
	}
	
	showTab(tabId) {
		let this_ = this;
		$('.orders-body').hide();
		$("#orders-panel-"+tabId).show();

		$('.orders-tab').removeClass("active");
		$('.orders-tab[tab="' + tabId + '"]').addClass("active");
		
		$("#orders-panel-"+tabId).find('.easyui-panel').panel();
	}
	
	loadOrdersMy() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getMyOrders", "arg0": 1}, 'my');
	}

	loadOrdersIn() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getOrders", "arg0": 1}, 'in');
	}

	loadOrdersOut() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getOrdersOut", "arg0": 1}, 'out');
	}

	loadOrders(par, tabId) {
		let this_ = this;
				
		this.app.post(par).then(data => {
			this_.showOrdersLater(data, tabId, 20);
		});
	}
	
	showOrdersLater(data, type, count) {
		let _this = this;
		var infire = 0;
		var found = $('#ordersList_' + type + '_count').length > 0 && $('#ordersList_' + type).length > 0;
		if (found) {
			$('#ordersList_' + type + '_count').text(data.total);

			if (data.rows) {
				$.each(data.rows, function(i, o) {
					$.each(o, function(key, order) {
						
						let orderDiv = _this.makeOrderDiv(order, key, type);
						//var stop = order.isStop=='1' ? "<td><input type='checkbox' class='my_check' key='" + key + "' task='" + order.proc + "' obj='"+order.iter+"'/></td>" : "";
						if (type == 'in' && order.status == 2) {
							infire++;
						}
						
						$('#ordersList_' + type).append(orderDiv);
					});
				});
			}
			
			//$('#ordersList_' + type).html(html);
			if (type == 'old') {
				$('#oldordersList_count').text(data.total);
				if(data.total == 0){
					$('#old_return').show();
				}
			}
			if (type == 'in') {
				if (infire > 0)
					$('#ordersList_in_fire_count').show().text(infire);
				else
					$('#ordersList_in_fire_count').hide();
			}
			return;
		} else if (count > 0) {
			count--;
			setTimeout(function(){this.showOrdersLater(data, type, count);}, 800);
		}
	}
	
	makeOrderDiv(order, key, type) {
		var orderDiv = $("<div uid='" + key + "' class='order-line'></div>");
						
		orderDiv.append('<div class="order-photo"><img src="'
			+ ((order.img && order.img.length > 0) ? order.img : 'css/img/empty-avatar-30.png')
			+ '" /></div>');
		
		var col1Div = $("<div class='order-col'></div>");
		var authorDiv = $("<div class='order-author'></div>").text(order.from);
		var dateDiv = $("<div class='order-date'></div>").text(order.inDate);
		col1Div.append(authorDiv).append(dateDiv);
		
		var titles = order.title.split(':');
		var col2Div = $("<div class='order-col'></div>");
		var pretitleDiv = $("<div class='order-pretitle'></div>").text(titles.length > 1 ? titles[0] : "");
		var titleDiv = $("<div class='order-title'></div>").text(titles.length > 1 ? titles[1] : titles[0])
			.attr('iter', order.iter).attr('proc', order.proc).attr('iuid', order.iuid).attr('uid', key).attr('type', type);
			
		if (type != 'my' && type !='old' && order.status == 2)
			titleDiv.addClass('order-important');
		col2Div.append(pretitleDiv).append(titleDiv);
		
		orderDiv.append(col1Div);
		orderDiv.append('<div class="order-delimeter"></div>');
		orderDiv.append(col2Div);
		if (order.ctrl) {
			var deadlineDiv = $('<div class="order-deadline"></div>').text(order.ctrl).addClass(this.orderStatus[order.status]).attr('title', 'Срок исполнения');
			orderDiv.append(deadlineDiv);
		}
		return orderDiv;
	}
	
	makeOrderBlock(order, key, type) {
		var orderDiv = $("<div uid='" + key + "' class='order-block' type='" + type + "'></div>");
		
		if (type == 'my')
			orderDiv.append('<i class="delete-order icon-close" task="' + order.proc + '"/>');
						
		var titles = order.title.split(':');
		var pretitleSpan = $("<span class='preTitle'></span>").text(titles.length > 1 ? titles[0] : "");

		var titleSpan = $("<span class='title'></span>").text(titles.length > 1 ? titles[1] : titles[0])
			.attr('iter', order.iter).attr('proc', order.proc).attr('iuid', order.iuid).attr('uid', key).attr('type', type);
		
		var rowDiv = $("<div class='content-row'></div>");
		rowDiv.append('<span class="content-image"><img src="'
			+ ((order.img && order.img.length > 0) ? order.img : 'css/img/empty-avatar-30.png')
			+ '" /></span>');
			
		var colDiv = $("<div class='content-column'></div>");
		colDiv.append("<span class='person'>От " + order.from + "</span>");
		colDiv.append("<span class='time'>" + order.inDate + "</span>");
		rowDiv.append(colDiv);
		rowDiv.append("<i class='icon-type icon-type-in'></i>");
		
		orderDiv.append(pretitleSpan);
		orderDiv.append(titleSpan);
		orderDiv.append(rowDiv);
		return orderDiv;
	}

	deleteOrders(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type != "notif") {
				$.each(o, function(i, uid) {
					let existOrderDiv = $('#ordersList_' + type + ' .order-line[uid="' + uid + '"]');
					let isImportant = (type == 'in' && existOrderDiv.find('.label-important').length > 0);
					existOrderDiv.remove();
					
					var tmp = $('#ordersList_' + type + '_count').text();
					var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('#ordersList_' + type + '_count').text(oldCount - 1);
					
					if (isImportant) {
						tmp = $('#ordersList_in_fire_count').text();
						oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
						if (oldCount == 1) {
							$('#ordersList_in_fire_count').hide();
						} else {
							$('#ordersList_in_fire_count').show().text(oldCount - 1);
						}
					}
					
					
					let existOrderDiv2 = $('.orders-' + type + ' .order-block[uid="' + uid + '"]');
					existOrderDiv2.remove();
					
					tmp = $('.order-type[code=' + type + '] .order-count').text();
					oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('.order-type[code=' + type + '] .order-count').text(oldCount - 1);
					
				});
			}
		});
	}
	
	updateOrders(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type != "notif") {
				$.each(o, function(i, uid) {
					var method = (type == "in") ? "getOrders" : 
							 	(type == "out") ? "getOrdersOut" : 
				 			 	(type == "old") ? "getOldOrders" : "getMyOrders";
	
					let par = {"sfunc" : 1, "cls": "XmlUtil", "name": method, "arg0": uid};
					_this.app.post(par).then(data => {
						if (data.rows) {
							$.each(data.rows, function(type1, o1) {
								$.each(o1, function(key, order) {
									var delta = 0;
									let orderDiv = _this.makeOrderDiv(order, key, type);
									delta +=  (type == 'in' && orderDiv.find('.label-important').length > 0) ? 1 : 0;
									let existOrderDiv = $('#ordersList_' + type + ' .order-line[uid="' + key + '"]');
									delta -=  (type == 'in' && existOrderDiv.find('.label-important').length > 0) ? 1 : 0;
									
									if (existOrderDiv.length > 0) {
										existOrderDiv.replaceWith(orderDiv);
									} else {
										var firstRow = $('#ordersList_' + type + ' div:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv);
										else {
											$('#ordersList_' + type).append(orderDiv);
										}
										
										var tmp = $('#ordersList_' + type + '_count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('#ordersList_' + type + '_count').text(oldCount + 1);
									}
									
									if (delta != 0) {
										var tmp = $('#ordersList_in_fire_count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										if (oldCount == 1) {
											$('#ordersList_in_fire_count').hide();
										} else {
											$('#ordersList_in_fire_count').show().text(oldCount + delta);
										}
									}
									
									
									let orderDiv2 = _this.makeOrderBlock(order, key, type);
									let existOrderDiv2 = $('.orders-' + type + ' .order-block[uid="' + key + '"]');
									
									if (existOrderDiv2.length > 0) {
										existOrderDiv2.replaceWith(orderDiv2);
									} else {
										var firstRow = $('.orders-' + type + ' .order-block:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv2);
										else {
											var op = $('.orders-' + type);
											
											if (op.hasClass('mCustomScrollbar')) {
												op.find('.mCSB_container').append(orderDiv2);
												$('.order-blocks[code=' + type + ']').mCustomScrollbar("update");
											} else {
												op.append(orderDiv2);
											}
										}
										
										var tmp = $('.order-type[code=' + type + '] .order-count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('.order-type[code=' + type + '] .order-count').text(oldCount + 1);
									}
									
								});
							});
						}
					});
				});
			}
		});
	}
	updateTask(process) {}
	
	deleteOrder(uid, iter, proc) {
		Util.blockPage(Translation.translation['deleting']);
		var par = {};
		par["uid"] = proc;
		par["obj"] = iter;
		par["key"] = uid;
		par["cmd"] = "kill";
		this.app.query(par).then(() => {
			$('body').unblock();
		});
	}
}
