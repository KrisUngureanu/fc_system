export class EventOps {

	static ctrlPressed = false;
	static shiftPressed = false;
	
	static init(app) {
		let _this = this;
		$(window).keydown(function(e) {
			if (e.which == 16) {
				_this.shiftPressed = true;
			} else if (e.which == 17) {
				_this.ctrlPressed = true;
			}
			if (e.ctrlKey) {          
				if (e.keyCode == 65) {                         
					e.preventDefault();
					return false;
				}            
			}
		}).keyup(function(e) {
			if (e.which == 16) {
				_this.shiftPressed = false;
			} else if (e.which == 17) {
				_this.ctrlPressed = false;
			} else if (e.which == 114) {
				if (searchTreeId != null && searchTreeVal != null) {
					if ($('#_' + searchTreeId).hasClass('treetable-search'))
						app.ifcController.findTreeTableNode(searchTreeId, searchTreeVal);
					else if ($('#_' + searchTreeId).hasClass('table-search'))
						app.ifcController.findTableNode(searchTreeId, searchTreeVal);
		//			else if ($('#_' + searchTreeId).hasClass('treefield-search'))
		//				findNode(searchTreeId, searchTreeVal, true);
					else
						app.ifcController.findNode(searchTreeId, searchTreeVal, false);
				} 
			}
		});
		
		$(window).mousedown(function(e){
			if(e.shiftKey){
				e.preventDefault();
				return false;
			}
		});
		$("#ui-body").on("click", function() {
			$("input").off("keyup");
			$("input").keyup(function (e) {
				if (e.keyCode == 13) {
					console.log("input keyup events js")
					$(this).trigger('blur');
				}
			});

		})

		
		
	}
}