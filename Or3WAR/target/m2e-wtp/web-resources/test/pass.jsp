<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String status = (String) request.getSession().getAttribute("userStatus");
    if (status == null || !"work".equals(status)) {
    	response.sendRedirect("login.jsp");
    } /*else {
    	if ("finish".equals(status)) {
    		response.sendRedirect("finish.jsp");	 
    	} 
    }*/
    String lang = (String) request.getSession().getAttribute("langCode");
%>
<c:set var="language" scope="session">
<%=lang %>
</c:set>
<fmt:setLocale value="${language}" />
<fmt:setBundle basename="kz.tamur.ekyzmet.test.test" />
<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="media/south-street/jquery-ui-1.10.3.custom.min.css" rel="stylesheet" />
        <link type="text/css" href="media/testapp.css" rel="stylesheet" lang="" media="screen"/>
        
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery-ui.custom.min.js"></script>
		<script src="js/jquery.blockUI.js"></script>
          <script src="js/counter.js" type="text/javascript"></script>
          <script src="js/audiojs/audio.min.js" type="text/javascript"></script>
          <script src="js/flowplayer/flowplayer-3.2.12.min.js"></script>
          <script>
          var time_section = '<fmt:message key="time.section" />';
          var time_global = '<fmt:message key="time.global" />';
          var noanswered = '<fmt:message key="noanswered" />';
	      var waitmsg = '<fmt:message key="wait" />';
	      $.blockUI.defaults.message = '<img src="media/img/loader.gif"><h1 style="color:#fff;font-size:16px;margin-top:10px;">'+waitmsg+'</h1>', 
	      $.blockUI.defaults.overlayCSS.backgroundColor = '#000';
	      $.blockUI.defaults.overlayCSS.opacity=0.3;
	      $.blockUI.defaults.css.border= 'none';
	      $.blockUI.defaults.css.width= '10%';
	      $.blockUI.defaults.css.padding= '15px';
	      $.blockUI.defaults.css.backgroundColor= '#000';
	      $.blockUI.defaults.css.color= '#fff';
	  		
	      
          window.onload = function () {
        	    if (typeof history.pushState === "function") {
        	        history.pushState("testStarted", null, null);
        	        window.onpopstate = function () {
        	            history.pushState('testStarted', null, null);
        	        };
        	    }
        	    $(document).keypress(function(e) {
        	    	if(e.which == 13 || e.which == 32) {
        	    	      event.preventDefault();
        	    	}
        	    });
        	}

        	
          
          </script>
          <script src="js/testapp.js" type="text/javascript"></script>
    </head>
    <body><table width="100%" border="0" height="100%">
  <tr>
    <td height="50" style="height: 50px; border-bottom: 1px solid #333;">  
    <table width="100%">
    <tr><td width="50%" style="font-size:16px;"><b style="width:60px">
<fmt:message key="fio" />
:</b> 
${sessionScope.fio}
<br>
<b class="dir_title"></b>
   </td><td> <table class="counterTable">
   <tr><td><fmt:message key="time.section" />&nbsp;</td><td style="white-space: nowrap;"><span id="counterDiv"></span>&nbsp;<fmt:message key="last" /></td></tr>
   <tr><td><fmt:message key="time.global" />&nbsp;</td><td style="white-space: nowrap;"><span id="counterGlobal"></span>&nbsp;<fmt:message key="last" /></td></tr>
   </table></td></tr></table></td></tr>
  <tr>
    <td><table width="100%" border="0" height="100%">
      <tr>
        <td id="main">
         <div id="content"></div>
         <div id="qList"></div>
        </td>
        <td width="300" style=" border-left: 1px solid #333;">   <div id="leftside">
                <ul></ul>
             </div></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td height="45" style="height: 45px; border-top: 1px solid #333;"> 
    <table width="100%">
	    <tr>
	    	<td><button id="prevQuestionBtn" onclick="prevQ()">⇐  <fmt:message key="prev" /></button></td>
	    	<td><button id="nextQuestionBtn" onclick="nextQ()"><fmt:message key="next" /> ⇒</button></td>
	    	<td><div id="blocks"><ul></ul></div></td>
	    </tr>
    </table> 
     </td>
  </tr>
</table>
<div style="display:none">
<div id="dialog-confirm" title="<fmt:message key="alert" />!">
  <div style="font-family:'Times New Roman';font-size:16pt"><fmt:message key="alert.msg.inform" /></div>
</div>
<div id="dialog-confirm2" title="<fmt:message key="alert" />!">
  <div style="font-family:'Times New Roman';font-size:18pt;padding:20px;"><fmt:message key="alert.msg.inform2" /></div>
</div>
<div id="dialog-confirm3" title="<fmt:message key="alert" />!">
  <div style="font-family:'Times New Roman';font-size:20pt"><fmt:message key="alert.msg.lastinform" /></div>
</div>

<div id="dialog-nextSection" title="<fmt:message key="alert" />!">
  <div style="font-family:'Times New Roman';font-size:20pt"><fmt:message key="alert.msg.inform.nextSection" /></div>
</div>
<div id="dialog-clearFinish" title="<fmt:message key="alert" />!">
  <div style="font-family:'Times New Roman';font-size:20pt"><fmt:message key="alert.msg.clearFinish" /></div>
</div>
<span id="alertFinishMsg" style="display: none"><fmt:message key="alert.msg.lastinform" /></span>
        <span id="yesMsg" style="display: none"><fmt:message key="alert.msg.yes" /></span>
         <span id="noMsg" style="display: none"><fmt:message key="alert.msg.no" /></span>
</div></body>
</html>
