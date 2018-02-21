<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Port Statistics ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
$(function () {
	$('#refresh1').click(function() {		 
		location.reload();
	});
});
var reloading;
function checkReloading() {
    if (window.location.hash=="#autoreload") {
        reloading=setTimeout("window.location.reload();", 30000);
        document.getElementById("reloadCB").checked=true;
    }
}
function toggleAutoRefresh(cb, ip) {
    if (cb.checked) {
        window.location.href='port_statistics.jsp?ip=' + ip + "#autoreload";
        location.reload();
        reloading=setTimeout("window.location.reload();", 30000);
    } else {
        window.location.replace("#");
        clearTimeout(reloading);
    }
}

$(document).ready(function () {
	checkReloading();
});
</script>
</head>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createFunctionMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>
<%@ include file="theme/Theme1/link_view.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/PortStatistics">
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>
	
	<table class="interfaceTable" style="width:1024px;">
		<caption>Port Statistics</caption>
	</table>
<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
<c:if test="${dev.publicIp == param.ip && dev.isSupportRxTxOctet()}">
	<c:forEach begin="1" end="${dev.stackNum}" varStatus="stackId">
		<table class="interfaceTable">
		<c:if test="${dev.stackNum > 1}"><caption>Stack ${stackId.index}</caption></c:if>
			<tr>
				<th rowspan="2">Port</th>
				<th colspan="7">Received</th>
				<th colspan="6">Transmitted</th>
			</tr>
			<tr>
				<th>Octets</th>
				<th>Unicast Packets</th>
				<th>Multicast Packets</th>
				<th>Broadcast Packets</th>
				<th>Discarded Packets</th>
				<th>Unknown Packets</th>
				<th>Errors</th>
				<th>Octets</th>
				<th>Unicast Packets</th>
				<th>Multicast Packets</th>
				<th>Broadcast Packets</th>
				<th>Discarded Packets</th>
				<th>Errors</th>
			</tr>
			<c:forEach var="item" items="${portStatistics}">
			<c:if test="${item.stackId == stackId.index}">
				<tr>
					<td>${item.portId}</td>
					<td>${item.rxOctets}</td>
					<td>${item.rxUnicast}</td>
					<td>${item.rxMulticast}</td>
					<td>${item.rxBroadcast}</td>
					<td>${item.rxDiscards}</td>
					<td>${item.rxUnknowns}</td>
					<td>${item.rxErrors}</td>
					<td>${item.txOctets}</td>
					<td>${item.txUnicast}</td>
					<td>${item.txMulticast}</td>
					<td>${item.txBroadcast}</td>
					<td>${item.txDiscards}</td>
					<td>${item.txErrors}</td>
				</tr>
			</c:if>
			</c:forEach>
		</table>
	</c:forEach>
	<button id="refresh1" class="btn btn-primary btn-xs">Refresh</button>
	<input type="checkbox" onclick="toggleAutoRefresh(this, '${param.ip}');" id="reloadCB"> Auto Refresh
</c:if>
<c:if test="${dev.publicIp == param.ip && !dev.isSupportRxTxOctet()}">
<div>Is not support!</div>
</c:if>
</c:forEach> 
</div>
</body>
</html>