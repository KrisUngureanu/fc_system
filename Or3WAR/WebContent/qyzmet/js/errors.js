export class ErrorOps {
	
	higlightErrors(ers) {
		let _this = this;
		var list = ["<ul class='error-red'>","<ul class='error-blue'>","<ul class='error-gray'>"];
		$.each(ers, function(i,er) {
			var rownum = -1;
			if ($("td[field="+ er.uuid +"]").length > 1) {
				rownum = er.msg.substring(er.msg.lastIndexOf('(')+1, er.msg.lastIndexOf(')')) - 1;
			}
			list[er.type] += "<li uuid='" + er.uuid + "'";
			if (rownum > -1) {
				list[er.type] += " row='" + rownum + "'";
			}
			list[er.type] += ">" + er.msg.replace(/\n/g, '<br/>') + "</li>";
			_this.higlightErrorComp(er, rownum);
		});
		list[0]+="</ul>";
		list[1]+="</ul>";
		list[2]+="</ul>";
		
		return list[0]+list[1]+list[2];
	}
	
	higlightErrorComp(error, rownum) {
		var e = $('#'+error.uuid);
	
		// подсветка ячейки в таблице
		if (rownum > -1) {
			var td_field = $("td[field="+ error.uuid +"]");
			if (td_field.length >= rownum) {
				$(td_field).parent().parent().find("[datagrid-row-index="+ rownum +"] > td[field="+ error.uuid +"]")
					.toggleClass("error-type-"+error.type, true);
			}
		// подсветка поля
		} else {
			this.setErrorType(e, error.type);
		}
	}
	
	setErrorType(e, errID) {
		let this_ = this;
		if ($(e).hasClass('easyui-datebox')) {
			if (!$(e).hasClass('datebox-f')) comp.datebox();
			e = $(e).datebox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		} else if ($(e).hasClass('easyui-datetimebox')) {
			if (!$(e).hasClass('datetimebox-f')) comp.datetimebox();
			e = $(e).datetimebox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		} else if ($(e).hasClass('easyui-combobox')) {
			if (!$(e).hasClass('combobox-f')) $(e).combobox();
			e = $(e).combobox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return;
		} else if ($(e).hasClass('easyui-numberbox')) {
			if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
			e = $(e).numberbox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return;
		} else if ($(e).is("input") 
				|| $(e).is("select")
				|| $(e).is("textarea")
				|| $(e).hasClass("textbox-text") 
				|| $(e).hasClass("datebox")
				|| $(e).hasClass("datetimebox")){
			$(e).one("change", function () {
				this_.delErrorType($(this));
		    });
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		}
	}

	delErrorType(e) {
		if ($(e).hasClass('easyui-datebox')) {
			if (!$(e).hasClass('datebox-f')) $(e).datebox();
			e = $(e).datebox("textbox").parent();
		}
		else if ($(e).hasClass('easyui-datetimebox')) {
			if (!$(e).hasClass('datetimebox-f')) $(e).datetimebox();
			e = $(e).datetimebox("textbox").parent();
		}
		else if ($(e).hasClass('easyui-combobox')) {
			if (!$(e).hasClass('combobox-f')) $(e).combobox();
			e = $(e).combobox("textbox").parent();
			
		}
		else if ($(e).hasClass('easyui-numberbox')) {
			if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
			e = $(e).numberbox("textbox").parent();
		}
		var cl = $(e).attr('class');
		if (cl !== undefined){
			var classList = cl.split(/\s+/);
			$.each( classList, function(index, item){
				if (item.indexOf('error-type-') >= 0) {
					$(e).toggleClass(item, false);
				}
			});
		}
	}
	
	clearAllErrorType() {
		let this_ = this;
		$.each($("[class*=error-type-]"), function(i, em){
			this_.delErrorType(em);
		});
	}
}