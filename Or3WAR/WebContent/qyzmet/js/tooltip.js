export class TooltipOps {
	
	constructor() {
	}
	
	init() {
	}
	
	static addDatagridTooltip(tbl) {
		var headerCells = tbl.datagrid('getPanel').find('div.datagrid-header td[field] div.datagrid-cell:not(:empty)');
		headerCells.tooltip({
			position : 'top',
			content : function(){
				var content = this.getElementsByClassName('tableColumnTooltipText')[0];			
				if(content){
					var pElem = content.getElementsByTagName('p')[0];
					var fontElem = content.getElementsByTagName('font')[0];
					if(fontElem){
						var color = fontElem.color;
						setAllChildrenColor(pElem, color);					
					}
					return content.innerHTML;
				}
			},
			onShow : function(){
				var content = this.getElementsByClassName('tableColumnTooltipText')[0];
				if(content){
					var t = $(this); 
					$(this).tooltip('tip').css({
						backgroundColor: '#fafad2',
						borderColor: '#666',
					}).unbind().bind('mouseenter', function(){					            	   
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');	
					})
				} else {
					$(this).tooltip("destroy");
				}
				
			}
		});
		var cells = tbl.datagrid('getPanel').find('div.datagrid-body td[field] div.datagrid-cell:not(:empty)');
		cells.tooltip({
			position : 'top',
			content: function(){	
				var scrWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0] ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].scrollWidth : this.lastChild.parentNode.scrollWidth;
				var clWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0]  ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].clientWidth : this.lastChild.parentNode.clientWidth;
				if(scrWidth > clWidth){
					
					var tp = this.lastChild.textContent;
					if(!tp){
						tp = this.lastChild.getElementsByTagName("INPUT")[0].value;
					}	    				 
					return tp;
				}
			},
			onShow: function(){
				var scrWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0] ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].scrollWidth : this.lastChild.parentNode.scrollWidth;
				var clWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0]  ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].clientWidth : this.lastChild.parentNode.clientWidth;
				if(scrWidth > clWidth){
					var t = $(this); 
					$(this).tooltip('tip').css({
						backgroundColor: '#fafad2',
						borderColor: '#666',
					}).unbind().bind('mouseenter', function(){					            	   
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');
					});
				} else if($(this).tooltip){
					$(this).tooltip("destroy");
				}
				
			}
		});
	}
}
