$.extend($.fn.calendar.defaults, {
	firstDay: 1,
	months: lbl['months'],
	weeks: lbl['weeks'],
	currentText: lbl['today'],
	closeText: lbl['close'],
	width: 230,
	height: 250
});
$.extend($.fn.calendar.methods, {
	moveTo: function(jq,date){
		return jq.each(function() {
			var isNull = false;
			if (date == null) {
				isNull = true;
				date = new Date();
			}
			$(this).calendar({year:date.getFullYear(),month:date.getMonth()+1,current:date});
			if (isNull) {
				$(this).calendar("options").current = null;
			}
		});
	}
});


$.extend($.fn.datebox.defaults, {
	panelWidth: 250,
	panelHeight: 280,
	currentText: lbl['today'],
	closeText: lbl['close']
});

$.extend($.fn.datetimebox.defaults, {
	currentText: lbl['current'],
	closeText: lbl['close']
});

$.fn.datebox.defaults.formatter = function(date) {
	if (date != null) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		if (d < 10) d = "0" + d;
		if (m < 10) m = "0" + m;
		return d + '.' + m + '.' + y;
	} else
		return 'дд.мм.гггг';
};
$.fn.datetimebox.defaults.formatter = function(date) {
	var secs = $(this).hasClass('seconds');
	if (date != null) {
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		var h = date.getHours();
		var min = date.getMinutes();
		var sec = secs ? date.getSeconds() : 0;
		if (d < 10) d = "0" + d;
		if (m < 10) m = "0" + m;
		if (h < 10) h = "0" + h;
		if (min < 10) min = "0" + min;
		if (secs && sec < 10) sec = "0" + sec;
		
		return d + '.' + m + '.' + y + ' ' + h + ':' + min + (secs ? ':' + sec : '');
	} else
		return secs ? 'дд.мм.гггг чч:ММ:сс' : 'дд.мм.гггг чч:ММ';
};

$.fn.datebox.defaults.parser = function(s) {
	var t = null;
	if (s != null) {
		if (s.length > 10) s = s.substring(0, 10);
		var dateParts = s.split(".");
		if (dateParts.length > 2) {
			t = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
			if (isNaN(t)) t = null;
		}
	}
	return t;
};
$.fn.datetimebox.defaults.parser = function(s) {	
	var t = null;
	if (s != null) {
		var dateTimeParts = s.split(" ");
		if (dateTimeParts.length > 1) {
			var s1 = dateTimeParts[0];
			var s2 = dateTimeParts[1];
			var dateParts = s1.split(".");
			var y = -1;
			var m = -1;
			var d = -1;
			var h = 0;
			var min = 0;
			var sec = 0;
			if (dateParts.length > 2) {
				y = dateParts[2];
				m = dateParts[1] - 1;
				d = dateParts[0];
			}
			var timeParts = s2.split(":");
			if (timeParts.length > 0) {
				h = timeParts[0];
				h = isNaN(parseInt(h)) ? 12 : parseInt(h);
			}
			if (timeParts.length > 1) {
				min = timeParts[1];
				min = isNaN(parseInt(min)) ? 0 : parseInt(min);
			}
			if (timeParts.length > 2) {
				sec = timeParts[2];
				sec = isNaN(parseInt(sec)) ? 0 : parseInt(sec);
			}
			t = new Date(y, m, d, h, min, sec);
			if (isNaN(t)) t = null;
		}

	}
	return t;
};


$.fn.setCursorPosition = function(position){
    if(this.length == 0) return this;
    return $(this).setSelection(position, position);
};

$.fn.setSelection = function(selectionStart, selectionEnd) {
    if(this.length == 0) return this;
    input = this[0];

    if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
    } else if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
    }

    return this;
};

$.fn.getSelectionLength = function() {
    if(this.length == 0) return 0;
    input = this[0];

    if (input.createTextRange != null) {
        var range = input.createTextRange();
        alert(range.text);
        return range.text.length;
    } else if (input.selectionStart != null) {
    	return input.selectionEnd - input.selectionStart;
    }

    return 0;
};

$.fn.focusEnd = function(){
    this.setCursorPosition(this.val().length);
    return this;
};