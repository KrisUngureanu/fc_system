<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>
<table style="margin: auto;">
	<tr>
		<td>
			<span>Ключевой носитель</span>
		</td>
		<td colspan="2">
			<select uid="ucgoConteinerType" style="height:24px;width:205px;">
			</select>
		</td>
	</tr>
	<tr>
		<td>
			<span>Пароль</span>
		</td>
		<td colspan="2">
			<input type="password" uid="ucgoConteinerPass" style="height:24px;width:150px;" />
		</td>
	</tr>
</table>