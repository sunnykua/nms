<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Trap</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>
<%-- The Beginning of Page Content --%>

	<jsp:include page="/Device">
		<jsp:param value="trap_view" name="method"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
      <c:if test="${dev.publicIp==param.ip}">
      <input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
      <table style="margin: auto;">
			<tr>
				<th class="th_left_align">IP Address</th>
				<td><input type="text" id="trapIpAddr_${status.count}"></td>
			</tr>
			<tr>
				<th class="th_left_align">Version</th>
				<td><select id="trapVersion_${status.count}">
			  		<option value="1">v1</option>
			  		<option value="2">v2c</option>
			 	  	</select></td>
			</tr>
			<tr>
				<th class="th_left_align">Community String</th>
				<td><input type="text" id="trapCommunity_${status.count}"></td>
			</tr>
			<tr>
				<th class="th_left_align">UDP port(1-65535)</th>
				<td><input type="text" id="trapUdp_${status.count}"></td>
			</tr>
			<tr>
				<td></td>
				<td><button class="btn btn-primary btn-xs" id="trapApply_${status.count}">Apply</button></td>
			</tr>
      </table>
      <br>
      <table class="interfaceTable" id="trapTable">
		    <tr>
		        <th><input type="checkbox" id="selectAll">Delete</th>
		        <th>IP Address</th>
		        <th>Version</th>
		        <th>Community</th>
		        <th>UDP Port</th>
		    </tr>
		    <c:forEach var="trap" items="${trapItems}">
		    <tr>
		        <td><input type="checkbox" name="deleteTrap" value="${trap.trapIpAddress}"></td>
		    	<td>${trap.trapIpAddress}</td>
		    	<td>${trap.trapDestVersion}</td>
		    	<td>${trap.trapDestCommunity}</td>
		    	<td>${trap.trapDestUdpPort}</td>
		    </tr>
		    </c:forEach>
		</table>
		<div class="buttonSection"><button class="btn btn-primary btn-xs" id="deleteTrapBtu_${status.count}">Delete</button></div>
      </c:if>
	</c:forEach>
</body>
</html>