import {DataChecker} from './util.js';

export class Archives {

	imgExt = ".png";
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		$('#archiveList_Layout').layout();
		$('#archiveTree').tree({
			onClick: function(node){
				this_.selectArchive(node);
			}
		});
		
		$("body").on('click', '#archiveList_body .archive', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.openArchive(procId);
		});
		
		$('#dictsList_Layout').layout();
		$('#dictsTree').tree({
			onClick: function(node){
				this_.selectDict(node);
			}
		});
		
		$("body").on('click', '#dictsList_body .dict', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.openDict(procId);
		});
		
	}
	
	selectArchive(node) {
		let this_ = this;
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getArchiveData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#archiveList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
							$('#archiveList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='processess" + key 
								+ "' class='archive' procId='" + proc.id
								+ "'><span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>" 
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#archiveList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('processess'+key);
							
						});
						resolve();
					});
				}
			});
		});
	}
	
	selectDict(node) {
		let this_ = this;
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getDictData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#dictsList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
							$('#dictsList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='dict" + key 
								+ "' class='dict' procId='" + proc.id
								+ "'><span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>" 
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#dictsList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('dict'+key);
							
						});
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
}