<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%@ include file="xss-filter.jsp" %>

<table>
	<tr>
		<td>
			<span>Ключевой контейнер</span>
		</td>
		<td colspan="2">
			<select uid="ecpType" style="height:24px;width:205px;" onchange="conteinerSelected('ecpType');">
				<option value="17">Файл</option>
				<option value="1">Удостоверение</option>
				<option value="15">Казтокен</option>
				<option value="4">еТокен</option>
			</select>
		</td>
	</tr>
	<tr id="ecpFileRow">
		<td>
			<span>Путь к файлу</span>
		</td>
		<td>
			<input type="text" uid="ecpFile" style="height:24px;width:150px;" />
		</td>
		<td>
			<input type="button" uid="ecpSelect" value="..." onclick="selectFile('ecpFile');"/>
		</td>
	</tr>
	<tr id="ecpKeyRow" style="display: none;">
		<td>
			<span>Ключ</span>
		</td>
		<td colspan="2">
			<select uid="ecpCert" style="height:24px;width:265px;">
			</select>
		</td>
	</tr>	
	<tr style="display: none;">
		<td>
			<span>Пароль</span>
		</td>
		<td colspan="2">
			<input type="password" uid="ecpPass" style="height:24px;width:150px;" />
		</td>
	</tr>
</table>