function checkboxFormat(val,row){
    if (val=="x")
     return '<img src="'+window.contextName + '/jsp/media/img/checkbox_on.png">';
    else
     return '<img src="'+window.contextName + '/jsp/media/img/checkbox_off.png">';
}    

function hyperPopup(val, row) {
//	return '<i class="fam-bullet-green"></i>'+val;
	return val;
}

function memoFormat(val, row) {
	return '<i class="fam-bullet-green"></i>' + val;
}

function comboFormat(row, titleUID, wrap) {
	var title = row[titleUID];
	if (title == null || title == undefined) title = "";
	
	var comboTag = (wrap == 'true') 
						? $('<div readonly="1" style="nowrap: true;">' + sanitizeAttr(title) + '</>')
						: $('<input readonly="1" class="combo-column" style="width:100%;" value="' + sanitizeAttr(title) + '" />');

	var res = $('<div/>')
		.append($('<table class="combo-format"/>')
			.append($('<tr/>')
				.append($('<td style="width: 100%;"/>')
					.append(comboTag))
				.append('<td><span class="combo-arrow" style="height: 20px;" /></td>'))).html(); 
	
	return res;
}

function comboTableFormat(row){
	var opts = $(this).combobox('options');
	var attrs_arr = row[opts.textField];
	var table_row = '';
	for (var i = 0; i < attrs_arr.length; i++) {
		table_row += '<div class="combo-cell" style="width: ' + opts['width'] + 'px;">' + attrs_arr[i] + '</div>';
	}
	return table_row;
}

function deleteRowFmt(val, row) {
	return '<i class="fam-bin"></i>';
}

function sanitizeAttr(s) {
	if (s != null)
		return s.replace(/&/g,'&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot').replace(/'/g, '&#39;')
		.replace(/\//g,'&#47;').replace('/\r/g', '').replace(/\n/g, '');
	return null;
}