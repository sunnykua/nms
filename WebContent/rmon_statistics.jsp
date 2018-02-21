<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rmon Statistics ${param.ip}</title>
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
        window.location.href='rmon_statistics.jsp?ip=' + ip + "#autoreload";
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

	<jsp:include page="/RmonStatistics">
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
<c:if test="${dev.publicIp == param.ip && dev.isSupportRmon()}">
	<table class="interfaceTable">
		<caption>RMON Statistics</caption>
		<tr>
			<th rowspan="2">Port</th>
			<th colspan="9">Packets</th>
			<!-- <th rowspan="2">Received Octets</th>
			<th rowspan="2">Drop Events</th>
			<th rowspan="2">CRC Align Errors</th>
			<th rowspan="2">Fragments</th>
			<th rowspan="2">Collisions</th> -->
		</tr>
		<tr>
			<th>Received Pkts&nbsp;</th>
			<!-- <th>Broadcast Pkts&nbsp;</th>
			<th>Multicast Pkts&nbsp;</th> -->
			<th>Undersize Pkts&nbsp;</th>
			<th>Oversize Pkts&nbsp;</th>
			<th>64 Pkts&nbsp;</th>
			<th>65to127 Pkts&nbsp;</th>
			<th>128to255 Pkts&nbsp;</th>
			<th>256to511 Pkts&nbsp;</th>
			<th>512to1023 Pkts&nbsp;</th>
			<th>1024to1518 Pkts&nbsp;</th>
		</tr>
		<c:forEach var="item" items="${rmonStatistics}">
			<tr>
				<td>${item.portId}</td>
				<td>${item.pkts}</td>
				<%-- <td>${item.broadcastPkts}</td>
				<td>${item.multicastPkts}</td> --%>
				<td>${item.undersizePkts}</td>
				<td>${item.oversizePkts}</td>
				<td>${item.pkts64Octets}</td>
				<td>${item.pkts65to127Octets}</td>
				<td>${item.pkts128to255Octets}</td>
				<td>${item.pkts256to511Octets}</td>
				<td>${item.pkts512to1023Octets}</td>
				<td>${item.pkts1024to1518Octets}</td>
				<%-- <td>${item.octets}</td>
				<td>${item.dropEvents}</td>
				<td>${item.crcAlignErrors}</td>
				<td>${item.fragments}</td>
				<td>${item.collisions}</td> --%>
			</tr>
		</c:forEach>
	</table>
	<button id="refresh1" class="btn btn-primary btn-xs">Refresh</button>
    <input type="checkbox" onclick="toggleAutoRefresh(this, '${param.ip}');" id="reloadCB"> Auto Refresh
</c:if>
<c:if test="${dev.publicIp == param.ip && !dev.isSupportRmon()}">
<div>Is not support!</div>
</c:if>
</c:forEach> 
</div>
</body>
</html>