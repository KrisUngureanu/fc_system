import {Util, DataChecker} from './util.js';
import {Translation} from './translation.js';

export class Monitor {
	
	states = ["state-green", "state-yellow", "state-red"];
	statesColor = ["#468847", "#F89406", "#E05540"];
	monitorPageSize = 100;
	currentPageNumber = 1;
	total = 0;
	
	monitorSearch = 0;
	tasksLoaded = false;
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		
		this.loadTasksCount().then(data => {
			this_.loadTasksContent();
		});
				
		$("body").on('click', '.task-line .task-title', function() {
			let jThis = $(this);
			let op = jThis.attr('op');
			
			if (op == "1")
				document.location.hash = "#cmd=openTask&mnt=1&uid=" + jThis.attr('uid');
			else if (op == "2")
				Util.alert(Translation.translation['procPerformedMessage'], Util.WARNING);
			else if (op == "3")
				Util.alert(Translation.translation['rptGenerateMessage'], Util.WARNING);
			else
				Util.alert(Translation.translation['ifcNotExistMessage'], Util.WARNING);
			
		});

		$("body").on('click', '.task-line .icon-close', function() {
			let task = $(this).parent();
			this_.killTask(task.attr('id'));
		});
		
		$("#monitor-panel").on('keyup', "#monitor-search-text", function(event) {
			if(event.keyCode == 13){
				this_.loadTasksContent(true);
			}
		});
	}
	
	loadTasksContent() {
		let this_ = this;
	
		$('#monitor-pager').pagination({
		    pageSize: this_.monitorPageSize,
		    total: this_.total,
		    pageNumber: 1,
		    pageList: [100, 200, 500, 1000],
			layout:['list','sep','first','prev','sep','links','sep','next','last','refresh','sep','manual'],
		    showPageList: true,
	
			onSelectPage:function(pageNumber, pageSize){
				Util.blockPage();
				$(this).pagination('loading');
				
				this_.currentPageNumber = pageNumber;
				this_.monitorPageSize = pageSize;
				
				this_.loadTasks();
				
				$(this).pagination('loaded');
			}
		});
		
		$('#monitor-pager').pagination('select');
		this_.tasksLoaded = true;
	}
		
	loadTasksCount() {
		let _this = this; 
		return new Promise((resolve, reject) => {
			_this.app.post({cmd: "getTasksCount"}).then(data => {
				_this.setTasksCount(data.message);
				resolve(data);
			});	
		});
	}
	
	setTasksCount(count) {
		this.total = count;
		$('#monitor-total-counter').text(count);
	
		if (this.tasksLoaded) {
			$('#monitor-pager').pagination('refresh', {
				total: count
			});
		}
	}	
	
	loadTasks() {
		let this_ = this;
		
		var end = 0;
		if (this_.total == 0)
			end = this_.monitorPageSize;
		else if (this_.total <= this_.currentPageNumber * this_.monitorPageSize)
			end = this_.total
		else
			end = this_.currentPageNumber * this_.monitorPageSize;
		
				
		var par = {"cmd" : "loadTasks", "rowFirst": (this_.currentPageNumber-1) * this_.monitorPageSize, 
			"rowLast": end};
			
		var searchText = $('#monitor-search-text').val();
		if (searchText && searchText.length > 0) {
			par["searchText"] = searchText;
		}

		this.app.post(par).then(data => {

			this_.setTasksCount(data.tasksCount);

			var processList = data.processes;
			processList.reverse();
			
			$('#monitor-panel .monitor-table .pcontent').empty();
			
			$.each(processList, function(i, o) {
				$.each(o, function(key, task) {
					$('#monitor-panel .monitor-table .pcontent').append(this_.makeTask(key, task));
				});
			});
			
			$('body').unblock();
		});
	}
	
	makeTask(key, task) {
		let this_ = this;
		var orderDiv = $("<div class='task-line'></div>").attr('id', key);

		orderDiv.append('<div class="task-condition ' + this.states[task.c2] + '"></div>');
		
		var col1Div = $("<div class='task-col'></div>");
		var authorDiv = $("<div class='task-author'></div>").text(task.i);
		var dateDiv = $("<div class='task-date'></div>").text(task.d);
		col1Div.append(authorDiv).append(dateDiv);
		
		var col2Div = $("<div class='task-col'></div>");
		var pretitleDiv = $("<div class='task-pretitle'></div>").text(task.t);
		var titleDiv = $("<div class='task-title'></div>").text(task.p)
			.attr('uid', key).attr('op', task.op).css({"color" : this_.statesColor[task.c2]});
		var descDiv = $("<div class='task-desc'></div>").text(task.o);
		col2Div.append(pretitleDiv).append(titleDiv).append(descDiv);
			
		orderDiv.append(col1Div);
		orderDiv.append('<div class="task-delimeter"></div>');
		orderDiv.append(col2Div);
		
		if (task.k == 1) {
			var closeDiv = $('<i class="icon icon-close"></i>');
			orderDiv.append(closeDiv);
		}
		
		return orderDiv;
	}
	
	killTask(flowId) {
		Util.blockPage(Translation.translation['deleting']);
		var par = {cmd: "kill", id: flowId};
		this.app.query(par).then(() => {
//			$('body').unblock();
		});
	}
	
	deleteTask(flowId) {
		let _this = this;
		if (this.total > -1) {
			$('#monitor-panel .monitor-table .pcontent #' + flowId).remove();
			$('body').unblock();
			
			_this.setTasksCount(_this.total - 1);
		}
	}
	
	addTask(process) {
		let _this = this; 
		if (this.total > -1) {
			var oldCount = this.total;
			$.each(process, function(key, task) {
				$('#monitor-panel .monitor-table .pcontent').prepend(_this.makeTask(key, task));
				oldCount++;
			});
			this.setTasksCount(oldCount);
		}
		var search = $('#monitor-search-text');
		if (search && search.val() && search.val().length > 0) { 
			_this.filterBySearchText(search.val());
		}
	}
	
	updateTask(process) {
		let _this = this;
		$.each(process, function(key, task) {
			var html = _this.makeTask(key, task);
			if ($('#monitor-panel .monitor-table .pcontent .task-line[id="' + key + '"]').length > 0)
				$('#monitor-panel .monitor-table .pcontent .task-line[id="' + key + '"]').replaceWith(html);
		});
		var search = $('#monitor-search-text');
		if (search && search.val() && search.val().length > 0) { 
			_this.filterBySearchText(search.val());
		}
	}
	
	filterBySearchText(search){
		let all = $('#monitor-panel .monitor-table .pcontent').find('.task-line').length;
		all.show();
		var a = all.length;
		
		if (search && search.length > 0){
			let toHide = $('#monitor-panel .monitor-table .pcontent').find('.task-line').not(":containsNoCase('" + search + "')");
			toHide.hide();
			var b = toHide.length;
			$('#monitor-total-counter').text(search +': '+(a-b+'/'+a));
		} else {
			$('#monitor-total-counter').text(tasksCount);
		}
	}
}
