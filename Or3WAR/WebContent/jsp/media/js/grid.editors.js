$.extend($.fn.datagrid.defaults.editors, {  
    file: {  
        init: function(container, options){
        	if (options != undefined && options['uid']) {
        		if (options['action'] == "DOC_UPDATE") {
	        		var input = $('<span class="btn btn-small fileinput-button" style="margin-right:2px;margin-top:2px; ">'
		        		+'<i class="fam-attach"></i>'
		        		+'<span class="btn-label" style="font-size:12px;font-weight:bold;">Загрузить</span>'
		        		+'<input class="or3-file-upload" type="file" '
		        		+'data-url="' + mainUrl + '&uid='+options['uid']+'&row='+options['row']+'" name="file">'
		        		+'</span>')
		        		.appendTo(container);
	        		
	                preparefileUpload();
	                return input;
        		} else if (options['action'] == "DOC_EDIT") {
        		}
        		return $('<span></span>').appendTo(container);
        	} else {
        		return $('<span></span>').appendTo(container);
        	}
        },
        getValue: function(target){
        },  
        setValue: function(target, value){
        }
    },
    checkbox: {  
        init: function(container, options){  
            var input = $('<input type="checkbox" class="datagrid-editable-input">').appendTo(container);  
            input.data('checkbox',{options:options});
            return input;  
        },
        getValue: function(target){  
            var check = $(target).is(':checked');
            if (typeof check !== 'undefined' && check !== false) {
               
                return "x";
            } else {
                return "";
            }
        },  
        setValue: function(target, value){
            var opts = $(target).data('checkbox').options;		
            if (opts.callback) value = opts.callback.call(target, value);
            if (value=="x") {
                $(target).attr("checked","checked");
            } else {
                $(target).removeAttr("checked");
            }
            $(target).val(value);  
        }
    },
	combobox: {
		init: function(container, options){  
			var input = $('<input type="text" class="datagrid-editable-input">').appendTo(container);
			options['formatter'] = comboTableFormat;
			options['width'] = container.parent().width();
			return input.combobox(options);
        },
        getValue: function(target){
            return $(target).combobox('getValue');
        },  
        setValue: function(target, value){
            $(target).combobox('setValue', value);
        }
	},
    datehhmmEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="datehhmmEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#datehhmmEditor').inputmask("d.m.y h:s").val(value);
        },
        getValue: function(target) {
            return $('#datehhmmEditor').val();
        }
    },
    datehhmmssEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="datehhmmssEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#datehhmmssEditor').inputmask("d.m.y h:s:s").val(value);
        },
        getValue: function(target) {
            return $('#datehhmmssEditor').val();
        }
    },
    datehhmmssSSSEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="datehhmmssSSSEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#datehhmmssSSSEditor').inputmask("d.m.y h:s:s.999").val(value);
        },
        getValue: function(target) {
            return $('#datehhmmssSSSEditor').val();
        }
    },
    ddmmEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="ddmmEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#ddmmEditor').inputmask("d.m").val(value);
        },
        getValue: function(target) {
            return $('#ddmmEditor').val();
        }
    },
    hhmmEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="hhmmEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#hhmmEditor').inputmask("h:s").val(value);
        },
        getValue: function(target) {
            return $('#hhmmEditor').val();
        }
    },
    hhmmssEditor : {
        init : function(container, opts) {
            var input = $('<input  type="text" class="datagrid-editable-input" id="hhmmssEditor" >').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            $('#hhmmssEditor').inputmask("h:s:s").val(value);
        },
        getValue: function(target) {
            return $('#hhmmssEditor').val();
        }
    },
    datebox : {
        init : function(container, opts) {
            var input = $('<input type="text" id="cellDatabox" />').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
            var dateParts = value.split(".");
            var date = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
			$('#cellDatabox').datebox().combo('textbox').inputmask("d.m.y", {
				"placeholder": "дд.мм.гггг",
				"insertMode" : false
			});
            if (isNaN(date.getDay())) {
            	$('#cellDatabox').datebox('setValue', null);
            } else {
            	$('#cellDatabox').datebox('setValue',value);
            }
        },
        getValue: function(target) {
            return $('#cellDatabox').combo('textbox').val();
        }
    },
    datetimebox : {
    	secs : false,
        init : function(container, opts) {
            secs = opts != null && opts.showSeconds;
            var input = $('<input type="text" id="cellDatabox" ' + (secs ? '' : 'data-options="showSeconds:false"') + '/>').appendTo(container);
            return input;
        },
        setValue: function(target, value) {
        	var date = null;
    		var dateTimeParts = value.split(" ");
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
    			}
    			if (timeParts.length > 1) {
    				min = timeParts[1];
    			}
    			if (timeParts.length > 2) {
    				sec = timeParts[2];
    			}
    			date = new Date(y, m, d, h, min, sec);

    			if (secs) {
					$('#cellDatabox').datetimebox().combo('textbox').inputmask("d.m.y h:s:s", {
						"placeholder": "дд.мм.гггг чч:ММ:сс",
						"insertMode" : false
					});
    			} else {
					$('#cellDatabox').datetimebox().combo('textbox').inputmask("d.m.y h:s", {
						"placeholder": "дд.мм.гггг чч:ММ",
						"insertMode" : false
					});
    			}
    		}
            if (date == null || isNaN(date.getDay())) {
            	$('#cellDatabox').datetimebox('setValue', null);
            } else {
            	$('#cellDatabox').datetimebox('setValue',value);
            }
        },
        getValue: function(target) {
            return $('#cellDatabox').combo('textbox').val();
        }
    },
    textareaNotReady : {
    	 init : function(container, opts) {
             var input = $('<input  type="text" class="datagrid-editable-input" id="hhmmEditor" >').appendTo(container);
             return input;
         },
         setValue: function(target, value) {
             $('#hhmmEditor').inputmask("h:s").val(value);
         },
         getValue: function(target) {
             return $('#hhmmEditor').val();
         }
    },
    intfield: {  
        init: function(container, opts) {
        	var w = container.innerWidth();
            var input = $('<input type="text" class="datagrid-editable-input" />').appendTo(container);
            input.width(w - 10);
            
            if (opts.maxlength != null && opts.maxlength > 0)
            	input.attr("maxlength", opts.maxlength);
            
            input.attr("include", opts.include);
            input.attr("exclude", opts.exclude);
            
            if ((opts.include != null && opts.include.length > 0) ||
            		(opts.exclude != null && opts.exclude.length > 0)) {
	            input.bind('keypress', function(e) {
	            	return validChars(this, e);
	            });
            }
            return input;  
        },
        getValue: function(target){  
            return $(target).val();
        },  
        setValue: function(target, value){
            $(target).val(value);  
        }
    },
    floatfield: {
    	init: function(container, opts) {
        	var w = container.innerWidth();
            var input = $('<input type="text" class="datagrid-editable-input" />').appendTo(container);
            input.width(w - 10);
            
            return input;  
        },
        getValue: function(target){  
            return $(target).val();
        },  
        setValue: function(target, value){
            $(target).val(value);  
        }
    },
    text: {
        init: function(container, options) {
    		var upperAllChars = false;
    		var upperCase = false;
        	if (options != undefined) {
        		if (options['upperAllChars']) {
        			upperAllChars = options['upperAllChars'];
        		}
        		if (options['upperCase']) {
        			upperCase = options['upperCase'];
        		}
        		
        	}
        	var w = container.innerWidth();
            var input = $('<textarea type="text" class="datagrid-editable-input" id="txtEditor"' + ((upperAllChars || upperCase) ? ' onkeyup="changeToUpperCase(this, event, ' + upperAllChars + ', ' + upperCase + ')"' : '') + '>').appendTo(container);
            input.width(w - 10);
            return input;
        },
        getValue: function(target){
            return $(target).val();
        },  
        setValue: function(target, value){
            $(target).val(value);  
        }
    }
});