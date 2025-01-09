import {DataChecker} from './util.js';
import { Util } from './util.js';

export class Processes {
	searchProcess = "";
	SearchText;
    SearchCount = 0;
	heightRange = 40;
	styledInputElement;
	styledElement;

	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
	imgExt = ".png";

	constructor(app) {
		this.app = app;
	}
	
	findOfWordsAutocomplete() {
		console.log("findOfWordsAutocomplete");
	}

	init() {
		let this_ = this;
		$('#processesList_Layout').layout();

		// $( "#processSearchPage" ).autocomplete({
		// 	source:  this_.findOfWordsAutocomplete(),
		// });

		$("body").on('keypress', '#processSearchPage', function(e) {
			var key = e.which || e.keyCode;
			if (key === 13) {
				this_.searchProcess = $(this).val();
				if(this_.SearchText == this_.searchProcess){
					this_.SearchCount++;
				} else{
					this_.SearchText = this_.searchProcess;
					this_.SearchCount = 0;
				}
				var par = {"cmd":"searchProcess","text":this_.searchProcess, "index": this_.SearchCount};
				this_.post(par, function (data){
					DataChecker.checkData(data).then(json => {
						if(data.parent) {
							var arr = data.parent.split(",").reverse();
							var procUid = arr.pop();
							var point = procUid.indexOf('.') + 1;
							var procElId = 'processess' + procUid.substring(point);
							var col = arr.length-1;
							var element;
							if(col == 1) {
								var node = $('#processTree').tree('find', arr[1]);
								if(node != null) {
									$('#processTree').tree('select', node.target);
									var nodeTopPos = node.target.offsetTop;
									var treeElement = document.getElementById('processesList_Tree');
									if(treeElement)
									treeElement.scrollTop = nodeTopPos - (treeElement.offsetHeight - this_.heightRange);
									this_.selectProcess(node, function() {
										element = document.getElementById(procElId);
										if(element){
											element.style.boxShadow = "0px 0px 5px blue";
											this_.styledElement = element;
											var atag = element.getElementsByTagName("A")[0];
											if(atag){
												atag.style.color = "blue";
												this_.styledInputElement = atag;
											}
											var topPos = element.offsetTop;
											var outerElement = document.getElementById('processesList_body');
											if(outerElement)
											outerElement.scrollTop = topPos - (outerElement.offsetHeight - element.offsetHeight);
										}
									});
								}
							} else {
								for(var i = 1; i < col; i++) {
									this_.searchProcCallLater('expand', arr[i], 5);
								}
								this_.searchProcCallLater('select', arr[col], 5, function(){
									element = document.getElementById(procElId);
									if(element){
										element.style.boxShadow = "0px 0px 5px blue";
										this_.styledElement = element;
										var atag = element.getElementsByTagName("A")[0];							
										if(atag){
											atag.style.color = "blue";
											this_.styledInputElement = atag;
										}
										var topPos = element.offsetTop;
										var outerElement = document.getElementById('processesList_body');
										if(outerElement)
										outerElement.scrollTop = topPos - (outerElement.offsetHeight - element.offsetHeight);
									}
								});
							}
						} else {
							this_.app.lang == "kz" ? alert("Еш нәрсе табылмады!") : alert("Ничего не найдено!");
						}
					})
				});
			}
		});

		$('#processTree').tree({
			onClick: function(node){
				this_.selectProcess(node);
			}
		});
				
		$("body").on('click', '#processesList_body .proc', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.startProcess(procId);
		});

		$("body").on('click', '#freqUsedProcs .process', function() {
			var procId = $(this).attr("uid");
			this_.app.nav.startProcess(procId);
		});
		
		$("body").on('click', '#processesList_body .proc .icon-remove-favourite', function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			this_.removeFromFavourite($(this));
		});
		
		$("body").on('click', '#processesList_body .proc .icon-add-favourite', function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			this_.addToFavourite($(this));
		});
	}
	
	selectProcess(node, func) {
		let this_ = this;
		
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getProcessData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#processesList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							
							var fav = (proc.favourite)
										? "<i class='icon-remove-favourite' uid='" + key + "'></i>"
										: "<i class='icon-add-favourite' uid='" + key + "'></i>";
							
							var fontWeight = proc.fontWeight ? "font-weight:bold;" : "";
							$('#processesList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='processess" + key 
								+ "' class='proc' procId='" + proc.id + "'>"
								+ fav
								+ "<span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>"
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#processesList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('processess'+key);
							
						});
						if(func) {
							func();
						}
						resolve();
					});
				}
			});
		});
	}
	
	addProcIcon(className, src) {
		let this_ = this;
		var img = new Image();
		img.onload = function() {
			this_.addStyle(className+' {background-image: url('+src+') !important}');
		};
		img.src = src;
	}
	
	addStyle(st) {
	    var style = document.createElement('style');
		style.type = 'text/css';
		style.innerHTML = st;
		document.getElementsByTagName('head')[0].appendChild(style);
	}
	
	attachProcessTooltips(proc_id) {
		
	}
	
	addToFavourite(elem) {
		var procId = elem.parent().attr("procId");
		var par = {cmd: 'addToFavorites', processUID: procId,  json: 1};
		this.app.query(par);
		
		elem.removeClass('icon-add-favourite').addClass('icon-remove-favourite');
		
		var html = '<div class="process" uid="' + procId + '">';
		html += '<i class="icon-left icon-vacation"/>';
		html += '<span>' + elem.parent().find('a').text() + '</span>';
		html += '<i class="icon-right icon-arrow-right" />';
		html += '</div>';
		
		$('#freqUsedProcs .portletBody').append($(html));
				
	}
	
	removeFromFavourite(elem) {
		var procId = elem.parent().attr("procId");
		var par = {cmd: 'removeFromFavorites', processUID: procId,  json: 1};
		this.app.query(par);
		
		elem.removeClass('icon-remove-favourite').addClass('icon-add-favourite');
		$('#freqUsedProcs .process[uid="' + procId + '"]').remove();
	}


	// добавил Жаркын
	post(par, func) {
        this.saveAsyncPostRequest(window.mainUrl, par, func, func != null ? 'json' : null);
    }

    saveAsyncPostRequest(url, params, success, dataType) {
        params["rnd"] = Util.rnd();
        params["guid"] = guid;
    
        $.post(url, params, success, dataType).fail(function() {
            // Добавляем запрос в хранилище необработанных запросов
            // lostRequests.push(new LostRequest("POST", url, params, success, dataType));
        });
    }

	searchProcCallLater(name, id, count, func) {
        //console.log("Count = " + count);
        var node = $('#processTree').tree('find', id);
        if (node != null) {
            $('#processTree').tree(name, node.target);
            if(name === 'select') {
                var nodeTopPos = node.target.offsetTop;
                var treeElement = document.getElementById('processesList_Tree');
                if(treeElement)
                treeElement.scrollTop = nodeTopPos - (treeElement.offsetHeight - this.heightRange);
                this.selectProcess(node, func);
            }
            return;
        } else if (count > 0) {
            let _this = this;
            count--;
            setTimeout(function(){_this.searchProcCallLater(name, id, count, func);}, 300);
        }
    
    }
}