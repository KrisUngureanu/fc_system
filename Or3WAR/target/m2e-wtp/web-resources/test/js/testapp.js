// Режима навигации по вопросам подраздела
var NAV_MODE_ALL		= 0; // Произвольный
var NAV_MODE_FORWARD	= 1; // Только следующий
var NAV_MODE_BACKWARD_1	= 2; // Возможность вернуться на 1 вопрос назад

var qData = {};
var source = 'action';
var sid = "0";
var counter = false;
var counterGlobal = false;
var _yes_btn = "";
var _no_btn = "";
var clickedLi = false;

var navMode = NAV_MODE_ALL;
var navCurr = 0; // ID текущего вопроса
var navBwdDepth = 0; // Количество возвратов к предыдущему впросу

$(document).ajaxStop($.unblockUI);
 
$(function() {
	var h = $("body table tr").first().height()+$("body table tr").last().height();
	$("#leftside").height($("body").height()-h-20);
	_yes_btn = $("#yesMsg").text();
	_no_btn = $("#noMsg").text();
	
	$("#leftside").on(
			"click",
			"li",
			function() {
				parent = $("#leftside .active").attr("parent");
				parent_it = $(this).attr("parent");
				
				if ($(this).attr("sid") == $("#leftside .active").attr("sid"))
					return false;

				if (parent != parent_it) { // other dir
					return false;
				} else {
					totals =  $('#blocks li').length;
					greens = $('#blocks li.state2').length;
					/*if (greens != totals) {
							window.nextDir = this;
							$( "#dialog-confirm span" ).text(getNumMsg() );
							showAlert4();
					} else {*/
						var nextSection =  this;
						$('#leftside li.active').removeClass("active");
						$(nextSection).addClass("active");
						loadSection($(nextSection).attr("sid"));
					//}
				}
	});

	$("#qList").on("click", "tr", function(e) {
		clickedLi = this;
		
		$.blockUI();
		var answer = "0";
		if ($(clickedLi).find("img").attr('src') == "media/img/off.png") {
			answer = $(this).attr("oid");
			//
		} else {
			answer = "0";
		}
		
		console.log("clicked oid " + $(this).attr("oid"));
		label = $(this);
		$.get(source, {
			"answer" : answer,
			"question" : $(this).attr('qid')
		}, function(data) {
			
			if ($(clickedLi).find("img").attr('src') == "media/img/off.png") {
				$('.optLi img ').attr("src","media/img/off.png");
				$(clickedLi).find("img").attr("src","media/img/on.png");
				$("#blocks li.active").removeClass('state1').removeClass('state0').addClass("state2");
			} else {
				$(clickedLi).find("img").attr("src","media/img/off.png");
				$('.optLi img ').attr("src","media/img/off.png");
				$("#blocks li.active").removeClass('state1').addClass('state0').removeClass("state2");
			}
			
			checkData(data);
			if (data.unanswered==0) {
				$('#leftside li.active span').hide();
			} else {
				$('#leftside li.active span.noanswer').text(data.unanswered);
				$('#leftside li.active span').show();
			}
			
		}, 'json');

		if (navMode != NAV_MODE_ALL) {
			$('#nextQuestionBtn').prop('disabled', answer == "0");
		}
	});

	
	$('.finishTest')
			.on(
					'click',
					function() {

						if (confirm($("#alertFinishMsg").text())) {

							finish('user');

						}
						;
					});
	
	$('.finishDir').on('click',
	function() {
			nextDir();
		;
	});

	loaddirs("");
});

function nextDir() {
}

function loaddirs(dir) {
	$.blockUI();
	$.get(source, {
		"dirs" : dir
	}, function(dirs) {
		checkData(dirs);
		if (dirs.time < 0) {
			finish('timer', 'loaddirs');
		} else {
			var sid2=0;
			navMode = dirs.navMode;
			if (navMode == NAV_MODE_FORWARD)
				$('#prevQuestionBtn').hide();
			else
				$('#prevQuestionBtn').show();
			
			navCurr = dirs.navCurr;
			navBwdDepth = dirs.navBwdDepth;
			
		$('#sectonTitle').html(dirs.title);
		var hasActive = false;
		$("#leftside ul").html("");
		$.each(dirs.section,
				function(i, section) {
					if (section.status==1 && $("#leftside li.active").length==0) {
						sid2 = section.sid;
						$('.dir_title').text(section.ptitle);
						loadSection(sid2);
					}
					var class_="";
					
					if (section.status==2)
						class_="closed";
					else if (section.status==1 && $("#leftside li.active").length==0) 
						class_="active";
					var liText ="<li class='"+class_+"' sid='" + section.sid + "' status='"+section.status+"' parent='"+section.pid+"' ptitle='"+section.ptitle+"'>" + section.title;  
					if (section.unanswered == 0) {
						liText +="<br><span style='font-size:9pt;color:#FF6347;display:none'>"+noanswered+" <span class='noanswer'>"+section.unanswered+"</span></span></li>"; 
					} else {
						liText +="<br><span style='font-size:9pt;color:#FF6347'>"+noanswered+" <span class='noanswer'>"+section.unanswered+"</span></span></li>";
					}
					
					$("#leftside ul").append(liText);
				});

		stopTimers();
		
		a = new Date();
		c = new Date();
		c.setSeconds(a.getSeconds() + dirs.total_time);

		counterGlobal = new luxCountdown({
			title : "",
			start : a,
			end : c,
			startText : "Начало",
			endText : "Конец",
			onend : function() {}
		});
		$("#counterGlobal").html(counterGlobal.getCountdown());

		var a = new Date();
		var c = new Date();
		c.setSeconds(a.getSeconds() + dirs.time);

		counter = new luxCountdown({
			title : "",
			start : a,
			end : c,
			startText : "Начало",
			endText : "Конец",
			onend : function() {
				loadNextSection('timer');
			}
		});
		$("#counterDiv").html(counter.getCountdown());
		
		
		
		}
	}, 'json');
}

function loadSection(sid2) {
	$.blockUI();
	$.get(source, {
		'blocks' : sid2
	}, function(data) {
		checkData(data);
		qData = data;
		$('#blocks ul').html("");
		var rid = 0;
		$.each(data, function(i, block) {
			if (navMode == NAV_MODE_ALL || navCurr == 0) {
				if (i == 0) {
					navCurr = block.bid;
				}
			} else if (block.bid == navCurr) {
				rid = i;
			}
			$('#blocks ul').append(
					"<li rid='" + i + "' bid='" + block.bid
							+ "' class='state" + block.ready + "'>"
							+ block.text + "</li>");
		});
		$('#blocks ul').show();
		loadblock(rid, navCurr);
		if ($("#leftside li").length<2){
			$(".finishDir").hide();
		}
		
		if (navMode == NAV_MODE_ALL) {
			$("#blocks ul").off("click", "li").on("click", "li", function() {
				var block = $(this);
				loadblock(block.attr("rid"), block.attr('bid'));
			});
		}
		
	}, 'json');
}

function finish(src, info) {
	var parentId = $('#leftside li.active').attr("parent");
	stopTimers();
	$.blockUI();
	$.get(source,
			{
				'finish' : '',
				'src' : src,
				'sid' : parentId,
				'info' : info
			},
			function(data) {
				window.location.href="finish.jsp";
				
			},'json');
}

function prevQ() {
	var index = $( "#blocks li" ).index( $('#blocks li.active'));
	
	if (index>0) {
		var rowId = parseInt($("#blocks li.active").prev().attr("rid"));
		var blockId = parseInt($("#blocks li.active").prev().attr("bid"));
		loadblock(rowId,blockId);
	}
}

function nextQ() {
	nextBlock();
}

function getNumMsg() {
	totals =  $('#blocks li').length
	greens = $('#blocks li.state2').length;
	var noanswered = [];
	 $('#blocks li:not(.state2)').each(function(i,li) {
		 noanswered.push("№ "+$(li).text());
	 });
	 return noanswered.join(',');
}

function nextBlock() {
	
	var index = $("#blocks li").index($('#blocks li.active'));
	
	var section_index = $( "#leftside li" ).index($('#leftside li.active'));
	var parentId = $('#leftside li.active').attr("parent");
	var section_total = $( "#leftside li" ).length - 1;
	if (index < $( "#blocks li" ).length - 1) {
		var rowId = parseInt($("#blocks li.active").next().attr("rid"));
		var blockId = parseInt($("#blocks li.active").next().attr("bid"));
		loadblock(rowId,blockId);

	} else { // next section check if there are any unasnwered q
		
		totals =  $('#blocks li').length;
		greens = $('#blocks li.state2').length;
		if (greens!=totals) {
			if (section_index < section_total) { // not final section
				$("#dialog-confirm span").text(getNumMsg());
				showAlert1();
			} else {
				$("#dialog-confirm2 span").text(getNumMsg());
				showAlert2();
			}
		} else {
			//load();
			if (section_index<section_total) { // not final section
				nextSection_func();
			} else {
				dlgClearFinish();
			}
		}
	}
}

function loadNextSection(src) {
	var parentId = $('#leftside li.active').attr("parent");
	var nextSection = $('#leftside li.active').next();
	if (src == 'timer' || $(nextSection).attr("parent") != parentId) {
		stopTimers();
		$.get(source,
				{
					'finish' : '',
					'src' : src,
					'sid' : parentId,
					'info' : 'loadNextSection'
				},
				function(data) {
					if (data.result && data.result == "ok") {
						window.location.href = "finish.jsp";
					} else {
						loaddirs("");
					}
				},'json');
	} else {
		$('#leftside li.active').removeClass("active");
		$(nextSection).addClass("active");
		loadSection($(nextSection).attr("sid"));
	}
}

function  nextSection_func(){
	$( "#dialog-nextSection" ).dialog({
	      resizable: false,
	      height:300,
	      modal: true,
	      buttons:[ {
	    	  text: _yes_btn,
	    	  
	    	  click: function() {
	          $( this ).dialog( "close" );
	          loadNextSection('user');
	        },
	      },{
	    	  text:_no_btn,
	    	  click: function() {
	          $( this ).dialog( "close" );
	        	}
	      	},
	      ]
	    });
}

function showAlert1() {
	var parentId = $('#leftside li.active').attr("parent");
	$( "#dialog-confirm" ).dialog({
	      resizable: false,
	      height:450,
	      modal: true,
	      buttons:[ {
	    	  text: _yes_btn,
	    	  
	    	  click: function() {
	          $( this ).dialog( "close" );
	          load(parentId);
	        },
	      },{
	    	  text:_no_btn,
	    	  click: function() {
	          $( this ).dialog( "close" );
	        	}
	      	},
	      ]
	    });
}

function showAlert2() {
	$( "#dialog-confirm2" ).dialog({
	      resizable: false,
	      height:450,
	      modal: true,
	      buttons:[ {
	    	  text: _yes_btn,
	    	  click: function() {
	    	 showAlert3();
	          $( this ).dialog( "close" );
	         
	        },
	      },{
	      text:_no_btn,
    	  click: function() {
	          $( this ).dialog( "close" );
	        },
	      }]
	    });
}

function showAlert3() {
	var parentId = $('#leftside li.active').attr("parent");
	$( "#dialog-confirm3" ).dialog({
	      resizable: false,
	      height:350,
	      modal: true,
	       minWidth: 400,
	      buttons: [{
	    	  text: _yes_btn,
	    	  click: function() {
	          $( this ).dialog( "close" );
	          load(parentId);
	        },
	      },{
	      text:_no_btn,
    	  click: function() {
	          $( this ).dialog( "close" );
	        },
	      }
	      ]
	    });
}

function showAlert4() {
	$( "#dialog-confirm" ).dialog({
	      resizable: false,
	      height:300,
	      modal: true,
	      buttons: [{
	    	  text: _yes_btn,
	    	  click: function() {
	          $( this ).dialog( "close" );
	          		$("#leftside li").removeClass("active");
	          		$(window.nextDir).addClass("active");
	          		$('#blocks ul').html("");
	          		sid = $(window.nextDir).attr('sid');
	          		loadSection(sid);
	    	  },
	        },{
	        	text:_no_btn,
	      	  click: function() {
	          $( this ).dialog( "close" );
	        },}]
	      
	    });
	
}
function dlgClearFinish() {
	$( "#dialog-clearFinish" ).dialog({
	      resizable: false,
	      height:300,
	      modal: true,
	      buttons: [{
	    	  text: _yes_btn,
	    	  click: function() {
	    		  showAlert3();
	          $( this ).dialog( "close" );
	          		
	    	  },
	        },{
	        	text:_no_btn,
	      	  click: function() {
	          $( this ).dialog( "close" );
	        },}]
	      
	    });
	
}

function load(parentId) {
	if (parentId != $('#leftside li.active').attr("parent"))
		return;
	
	var section_index = $( "#leftside li" ).index( $('#leftside li.active'));

	var section_total = $( "#leftside li" ).length - 1;
	if (section_index == section_total) { // if last section 
		$.get(source,
				{
					'finish' : '',
					'src' : 'user',
					'sid' : parentId,
					'info' : 'load 1'
				},
				function(data) {
					if (data.result && data.result == "ok") {
						window.location.href="finish.jsp";
					} else {
						loaddirs("");
					}
				},'json');
	} else {

		var parentId = $('#leftside li.active').attr("parent");
		var nextSection =  $('#leftside li.active').next();
		if ($(nextSection).attr("parent")!=parentId) {
			$.get(source,
					{
						'finish' : '',
						'src' : 'user',
						'sid' : parentId,
						'info' : 'load 2'
					},
					function(data) {
						if (data.result && data.result == "ok") {
							window.location.href="finish.jsp";
						} else {
							loaddirs("");
						}
					},'json');
		} else {
			$('#leftside li.active').removeClass("active");
			$(nextSection).addClass("active");
			loadSection($(nextSection).attr("sid"));
		}
	}
}

function loadblock(rowId, bid) {
	$("#blocks ul li").removeClass("active");
	$("#blocks ul li[rid='" + rowId + "']").addClass("active");
	$('#content').html("").hide();
	$("#qList").html("");
	
	if (navMode != NAV_MODE_ALL) {
		$('#nextQuestionBtn').prop('disabled', !$("#blocks ul li[rid='" + rowId + "']").hasClass("state2"));
	}

	var data = qData[rowId];
	if (data != null) {
		$.each(data.content,
				function(i, content) {
					if (content.text)
						$('#content').html(content.text);
					if (content.audio) {
			            $('#content').append('<a id="audio" style="display:block;width:100%;height:30px;" href="' + content.audio + '"></a>');
			        }
			        if (content.video)
			            $('#content').append('<a id="video" style="display:block;width:425px;height:300px;" href="' + content.video + '"></a>');
			        if (content.image)
			            $('#content').append('<div class="qImg"><img src="' + content.image + '"></div>');
			        if ($('#content').html().length > 0) {
			        	$('#content').append("<hr>").show();
			        }
				});
		}
		$.blockUI();
		$.get(source, {
			'questions' : bid
		}, function(data) {
			checkData(data);
			
			var qListData = "";
			var qcount = 0;
			$.each(data, function(qid, qData2) {
				
				if (navMode == NAV_MODE_BACKWARD_1) {
					$('#prevQuestionBtn').prop('disabled', qData2.navBwdDepth > 0);
				}
	
				var selected = 0;
				if (qData2.selected) {
					selected = qData2.selected;
				}
	
				qListData += "<div class='qItem'><h3>" + qData2.q[0].text
						+ "</h3><table cellspacing=15>";
	
				qcount++;
				$.each(qData2.opts, function(oid, opt) {
					if (opt.content.text) {
						qListData += "<tr oid='" + opt.oid + "' qid='" + qData2.qid
								+ "' class='optLi'>";//<b>" + opt.letter + "</b> ";
						if (selected == opt.oid) {
							qListData += "<td><img src='media/img/on.png'/></td>";
						} else {
							qListData += "<td><img src='media/img/off.png'/></td>";
						}
						qListData += "<td><label class='optLabel' for='op2t_" + opt.oid
								+ "' oid='" + opt.oid + "'>" + opt.content.text
								+ "</label></td></tr>";
					}
					
					if (opt.content.image) {
						qListData += "<li oid='" + opt.oid + "' qid='" + qData2.qid
								+ "' class='optLi image'><b>" + opt.letter + "</b> ";
						if (selected == opt.oid) {
							qListData += "<input type='radio' name='testOpt_"
									+ qData2.qid + "' value='" + opt.oid
									+ "' id='opt_" + opt.oid
									+ "' checked='checked'/>";
						} else {
							qListData += "<input type='radio' name='testOpt_"
									+ qData2.qid + "' value='" + opt.oid
									+ "' id='opt_" + opt.oid + "' />";
						}
						qListData += "<label class='optLabel' for='op2t_" + opt.oid
								+ "' oid='" + opt.oid + "'><img src='" + opt.content.image+"'/>"
								+ "</label></li>";
					}
				});
				qListData += "</ul></div>";
			});
	
			$('#content').attr("qcount", qcount);
			$("#qList").html(qListData);
			initAudio();
		}, 'json');
	//}
}

function checkData(data) {
	if (data && data.result && data.result == "error") {
		if (data.msg)
			alert(data.msg);
		window.location.href = "login.jsp";
		
	}
}
function initAudio() {

	if ($('#audio').length>0) {
	    flowplayer("audio", "js/flowplayer/flowplayer-3.2.16.swf", {
	      clip:  {
	          autoPlay: false,
	          autoBuffering: true
	          
	      },plugins: {
	        controls: {
	            fullscreen: false,
	            height: 30,
	            autoHide: false
	        }
	    }});
	}
	if ($('#video').length>0) {
	flowplayer("video", "js/flowplayer/flowplayer-3.2.16.swf", {
	      clip:  {
	          autoPlay: false,
	          autoBuffering: true
	      },plugins: {
	        controls: {
	            fullscreen: false
	        }
	    }});
	}
}

function stopTimers() {
	
	var type = typeof counter;
	if (type == "object") {
		counter.stop();
		counter = false;
	}
	
	type = typeof counterGlobal;
	if (type == "object") {
		counterGlobal.stop();
		counterGlobal = false;
	}
}