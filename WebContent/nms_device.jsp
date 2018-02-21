<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NMS</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device.css">
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript" src="theme/datatables/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
$(document).ready(function(){
    $('#deviceTable').dataTable({
    	stateSave: true,
    	"aoColumnDefs": [{ 'bSortable': false, 'aTargets': [ 3,5 ] },{ "sType": "title-string", "aTargets": [ 6 ] }],
    });
    var table = $('#deviceTable').DataTable();
    table.on('page', function( e, o) {
    	$('input[name=deleteAllDevices]').attr('checked', false);
    	$("input[name='deleteDevices']").each(function() { //loop through each checkbox
            this.checked = false; //deselect all checkboxes with class "checkbox1"                       
        });
    });
});
</script>
</head>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createNmsMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/Device">
		<jsp:param value="deviceList_get" name="method"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>
	
	<div id="page_content">
	<div id="page_title">Remote Device List</div>
	<table id="deviceTable">
		<thead>
			<tr class="deviceTable_th">
				<th>IP Addr</th>
				<!-- <th rowspan="2">UpTime</th> -->
				<th>Device Type</th>
				<th>Alias</th>
				<th>MAC Addr</th>
				<th>Eth. Port<br>RJ45/Fiber</th>
				<th>Last Update</th>
				<th>Alive</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="dev" items="${deviceTable}" varStatus="status">
				<tr class="deviceTable_tr">
					<c:choose>
						<c:when test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
							<td class="td_left_align"><a href="javascript:void(0)" onclick="window.open('remote_wlan_ap_list.jsp?remote_address=${param.ip}&remote_device_ip=${dev.publicIp}');">${dev.publicIpFull}</a></td>
						</c:when>
						<c:otherwise>
							<td class="td_left_align">${dev.publicIpFull}</td>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${dev.deviceType == 'l2switch'}">
							<td class="td_center_align">L2switch</td>
						</c:when>
						<c:when test="${dev.deviceType == 'l3switch'}">
							<td class="td_center_align">L3switch</td>
						</c:when>
						<c:otherwise>
							<td class="td_center_align">${dev.deviceType}</td>
						</c:otherwise>
					</c:choose>
					<%-- <td>${dev.sysUpTime}</td> --%>
					<td class="td_center_align"><div id="vAliasName${status.count}">${dev.aliasName}</div></td>
					<td class="td_center_align"><div id="vPhyAddr${status.count}">${fn:toUpperCase(dev.phyAddr)}</div></td>
					<td class="td_center_align">${dev.rj45Num}/${dev.fiberNum}</td>
					<td class="td_center_align"><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${dev.lastSeen}"/></td>
					<td class="td_center_align">
					   <c:if test="${dev.alive == 0}"><img src="images/fail.png" height="24" width="24" title="fail"></c:if>
					   <c:if test="${dev.alive == 1}"><img src="images/ok.png" height="24" width="24" title="ok"></c:if>
					   <c:if test="${dev.alive == 2}"><img src="images/warning.png" height="28" width="28" title="warning"></c:if>
					</td>
				</tr>
			</c:forEach>
            </tbody>
		</table>
	</div>
</div>
</body>
</html>