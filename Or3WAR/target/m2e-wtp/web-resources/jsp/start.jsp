<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="kz.tamur.web.common.WebSession"%>
<%@ page import="kz.tamur.web.controller.WebController"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="kz.tamur.util.Funcs"%>

<%
	String webContextName = request.getContextPath();
	String guid = request.getParameter("guid");
	Map<String, Object> s = WebController.getSession(request, guid);
	if (s != null) {
		WebSession ws = (WebSession) s.get("ws");
		if (ws != null) {
			ResourceBundle rb = ws.getResource();
			if (rb != null) {
				boolean showLDinput = ws.showLDinput();
				boolean isScopeEmpty = ws.isScopeEmpty();
				String ldSrchTxt = ws.getLdSrchTxt();
%>

<div class="startListTasks">
	<table style="border-spacing:0" width="100%" heigth="100%">
		<tbody>
			<tr>
			<td id="startPanelContent" width=253px>
					<div class="greenLine active  startTab" tab="myProgectDoc" style="transition-property: -moz-transform; transform-origin: 0px 0px 0px; transform: translate(0px, 0px);" >
						<div class="startPanelPadding"> <%= Funcs.sanitizeXml(rb.getString("webTasksMy")) %> <span class="badge badge-info" id="ordersList_my_count"></span></div>
					</div>
				</td>
				<td id="startPanelContent" width=300px>
					<div class="redLine startTab" tab="incomingTasks" style="transition-property: -moz-transform; transform-origin: 0px 0px 0px; transform: translate(0px, 0px);" >
						<div class="startPanelPadding"><%= Funcs.sanitizeXml(rb.getString("webTasksIn")) %> <span class="badge badge-important" id="ordersList_in_fire_count"></span>
						<span class="badge badge-info" id="ordersList_in_count"></span></div>
					</div>
				</td>
				<td id="startPanelContent" width=253px>
					<div class="blueLine startTab" tab="outboxTasks" style="transition-property: -moz-transform; transform-origin: 0px 0px 0px; transform: translate(0px, 0px);" >
						<div class="startPanelPadding"><%= Funcs.sanitizeXml(rb.getString("webTasksOut")) %> <span class="badge badge-info" id="ordersList_out_count"></span></div>
					</div>
				</td>
				<td class="tab-l">
					<div class="managment">
						<input type="checkbox" id="main_checkbox"/><label style="margin-left: 5px;"><%= Funcs.sanitizeXml(rb.getString("webAllCheckedProc")) %></label>
						<button id="removeprocess" class="easyui-linkbutton c5 l-btn l-btn-small" style="display: none;">
							<span class="l-btn-left">
							<span class="l-btn-text" style="color: #fff"><%= Funcs.sanitizeXml(rb.getString("webDeleteProcess")) %></span>
							</span>
						</button>	
					</div>
					<div class="sortProcess" style="display: none;">
					<label><%= Funcs.sanitizeXml(rb.getString("webSort")) %></label>
						<select id="sortprocinout">
							<option value="0"><%= Funcs.sanitizeXml(rb.getString("webChronology")) %></option>
							<option value="1"><%= Funcs.sanitizeXml(rb.getString("webOverdue")) %></option>
						</select>
					</div>
					<div class="filter" title="Сортировка">
						<a id="flt" href="javascript:void(0)">
							<div id="ui_filter"></div>
					 		<div id="ui_vniz"></div>
    		 	 		</a>
    		 	 	</div> 
    		 	 	</td>
    		 	 	<% 	if(showLDinput && !isScopeEmpty) { %>
    		 	 	<td>
					<div class="autocomplete" style="width: 300px;">
						<input autocomplete="off" class="autocomplete-input"
							id="privateDeal" type="search" placeholder="<%=ldSrchTxt%>">
					</div> 
					</td>
					<%}%>
					<td>
					<div style="text-align: right">
						<input id="txtSearchPage" class="filter-text" type="search" placeholder="<%= Funcs.sanitizeXml(rb.getString("webSearchTaskByName")) %>" style="margin: 3px 0 0 0 !important">
					</div>
				</td>	
				<td>
					<div>
						<input type="checkbox" id="filter_checkbox" style="margin: 12px">
					</div>
				</td>	
			</tr>
		</tbody>
	</table>
	<div id="tab_myProgectDoc" class="tabInfo b-m portlet" >
		<div class="pcontent easyui-panel" id="ordersList_my" data-options="fit:true,border:false"> </div>
	</div>
	<div id="tab_incomingTasks" class="tabInfo b-m portlet" style="display: none">
		<div class="pcontent easyui-panel" id="ordersList_in" data-options="fit:true,border:false"> </div>
	</div>
	<div id="tab_outboxTasks" class="tabInfo b-m portlet" style="display: none">
		<div class="pcontent easyui-panel" id="ordersList_out" data-options="fit:true,border:false"> </div>
   	</div>
   	<div id="assortTasksContent" style="display: none">
   		<div class="assortTasksist">
   		<div class="span3">
                <h3>Сортировка</h3>
                <label class="checkbox"><input type="checkbox" value=""> Все</label>
                <label class="checkbox"><input type="checkbox" value=""> Автор</label>
                <label class="checkbox"><input type="checkbox" value=""> Дата создания</label>
                <label class="checkbox"><input type="checkbox" value=""> Просроченные</label>
                <label class="checkbox"><input type="checkbox" value=""> Критические</label>
             </div>
   		</div>
   	</div>
</div>
   	
<script>
$(function() {
	$(".startTab").on('click', function() {
		$('.tabInfo').hide();
		$('.startTab').removeClass("active");
		var tab_id = $(this).attr("tab");
		$("#tab_"+tab_id).show();
		$(this).addClass("active");
		if($(this).hasClass("greenLine")){
			$('.managment').show();
			$('.sortProcess').hide();
		} else {
			$('.managment').hide();
			$('.sortProcess').show();
		}
		$('.easyui-panel').panel();
	});
});

 $(function(){
       $('#assortTasksDev').tooltip({
			deltaX: 0,
		    content: $('#assortTasksContent').html(),
		    showEvent: 'click',
		    onUpdate: function(content){
		    },
		    onPosition: function() {
		    	$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() -5);
		    	$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() -15);
	    	},
		    onShow: function(){
		        var t = $(this);
		        t.tooltip('tip').unbind().bind('mouseenter', function(){
		            t.tooltip('show');
		        }).bind('mouseleave', function() {
		            t.tooltip('hide');
		        });
		    }
       });
       $('#ordersList_in_fire_count').tooltip({
           position: 'bottom',
           content: '<span style="color:#fff"><%= rb.getString("webInFireCount")%></span>',
           onShow: function(){
               $(this).tooltip('tip').css({
                   backgroundColor: '#DD4B39',
                   borderColor: '#DD4B39'
               });
           }
       });
       $('#filter_checkbox').tooltip({
	       position: 'top',
	       content: 'Поиск только по названию задачи',   
	       onShow: function(){
				$(this).tooltip('tip').css({
					backgroundColor: '#fafad2',
	                borderColor: '#666'
	           });
       		}
       });
 });


</script>	
<style>
.managment, .sortProcess{
	float: left;
	line-height: 40px;
    margin-left: 10px;
    font-size:14px;
}
.tabLine{
	border-left: 1px solid #d7d7d7;
	border-bottom: 1px solid #d7d7d7;
	padding: 0 0 1px 0;
    background: transparent;
    height: 39px
}
.startListTasks{
	padding:10px 30px 30px 10px;
}
#startPanelContent{
	cursor: pointer;
	background:#E5E5E5;
	border-top: 1px solid #d7d7d7;
	border-left: 1px solid #d7d7d7;
	padding:0;
 }
#startPanelContent > ul {
	text-decoration: none;
	margin: 0;	
	overflow: auto;
	}
#startPanelContent > li {
	text-decoration: none;
	margin: 0;
	padding: 10px 0 0 0;
	}
.b-m{
	margin-top:7px;
	border: 1px solid #d7d7d7;
}
#startPanelContent .active{
	height:40px;
	background:#FFFFFF;
	border-bottom: none;
	}
#startPanelContent ,.lp-dropdown-menu a,#left-panel-content a span,.lp-dropdown-menu a span
	{
	transition: color 0.2s ease 0s;
}
#startPanelContent :hover,.lp-dropdown-menu a:hover,#left-panel-content .lp-dropdown-toggle.open,.lp-dropdown-menu .lp-dropdown-toggle.open,#left-panel-content a:hover span,.lp-dropdown-menu a:hover span,#left-panel-content .lp-dropdown-toggle.open span,.lp-dropdown-menu .lp-dropdown-toggle.open span
	{
	text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}
.startPanelPadding{
	padding: 10px;
	color: #333333;
	font-size: 16px;
}
.assortTasks{
	background: #E5E5E5;
	border: 1px solid #d7d7d7;
	position:relative;
	height: 32px;
	width: 51px;
	float: right;	
	border-radius: 2px;
	}
.assortTasks:hover{
	-webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,.1);
	box-shadow: inset 0 1px 2px rgba(0,0,0,.1);
	background: #f8f8f8;
	color: #333;
	}
#assortTaskDown{
	margin-top: -8px;
	margin-left: 19px;
	vertical-align: middle;
	background: url("<%=webContextName%>/jsp/media/css/or3/vniz.png") no-repeat scroll center transparent;
	height: 5px;
	}
#assortTaskIcon:hover{
	margin-top: 8px;
	margin-right: 10px;
	background: url("<%=webContextName%>/jsp/media/css/or3/sort.png") no-repeat scroll center transparent;
	height: 16px;
	}
#assortTaskIcon{
	margin-top: 8px;
	margin-right: 10px;
	background: url("<%=webContextName%>/jsp/media/css/or3/sort-st.png") no-repeat scroll center transparent;
	height: 16px;
	position: relative;
	}
.assortTasksist{
	padding: 10px 0;
	min-width: 200px;
	height:200px;	
	}	
.assortTasksist .checkbox { 
	display: block;
	margin-top: 10px;
 	font-size: 13px;
 	line-height: 18px;
  	color: #555;
  	background-color: #f9f9f9;
  	-webkit-border-radius: 3px;
  	   -moz-border-radius: 3px;
   	       border-radius: 3px;
 	cursor: pointer;
}	
.assortTasksist .checkbox:hover {
 	 color: #333;
 	 background-color: #f5f5f5;
}
	
.span3 {
	width: 180px;
	float: left;
	min-height: 1px;
	margin-left: 10px;
}	
#assortTasksContent span{
	color: #333;
	}

.greenLine:hover{
height:2px;
background:#52A83D; 
}
.redLine:hover{
height:2px;
background:#DD4B39; 
}
.blueLine:hover{
height:2px;
background:#0189D5; 
}

</style>	

<%
			}
		}
	}
%>