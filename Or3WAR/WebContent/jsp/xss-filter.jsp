<%@ page language="java" pageEncoding="UTF-8" %>
<% response.setHeader("Strict-Transport-Security", "max-age=31536000"); %>
<script> 
if (top != self) 
	top.location.href = "login.jsp";
</script>