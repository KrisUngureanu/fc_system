import {Util} from './util.js';

export class Help {

	constructor(app) {
		this.app = app;
	}
	
	init() {
		let _this = this;
		
		//$('#help-panel .portlet').css('height', ($(window).height()-160)+"px");
		//$('.easyui-panel:not(.tamur-tabs)').panel();
			
		this.loadHelp();
	}
	
	loadHelp() {
		let _this = this;
		var par = {"sfunc":1, "cls":"XmlUtil", "name":"getHelpTabs"};
		this.app.post(par).then(data => {
			_this.parseHelpTabs(data)
		});
	}
	
	parseHelpTabs(res) {
		let _this = this;
		
		if (res.tabs) {
			$.each(res.tabs, function(i, tab) {
				var html = '<div class="Ek-help" title="' + tab.title + '"';

				if (tab.pic && tab.pic.length > 10) {
					html += 'data-options="iconCls:\'icon-' + tab.id + '\'"';

					var css = '.icon-' + tab.id +' {background: url("' + tab.pic + '") no-repeat scroll center center}';
					$('style').first().append('\n' + css);
				}

				html += ' style="padding:20px;">';
				
				var par = {"sfunc":1,"cls":"XmlUtil", "name":"getHelpContent", "arg0":tab.id};
				_this.saveAjaxRequest('POST', window.mainUrl, par, function(content) {
					if (content)
						content = content.replace('name=getHelpFile&', 'guid=' + guid + '&name=getHelpFile&');
					
					html += content;
				}, 'html', false);
				html += '</div>';

				$('#help-tab-panel').append($(html));
			});
			
			$('#help-tab-panel').tabs({
			    border:false			    
			});
			
			$( ".aboutSys" ).click(function() {
				$(this).next().toggle();
			});
		}
	}
	
	saveAjaxRequest(type, url, params, success, dataType, async) {
		params["rnd"] = Util.rnd();
		params["guid"] = guid;
	
		$.ajax({
			type : type,
			url : url,
			data : params,
			success : success,
			dataType : dataType,
			async : async
		});
	}
		
}