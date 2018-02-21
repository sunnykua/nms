<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Vlan ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/vlan_global.css">
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
        window.location.href='all_vlanlist.jsp?ip=' + ip + "#autoreload";
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

<%-- The Beginning of PVID List Page Content --%>

	<jsp:include page="/VlanStatus">
		<jsp:param value="${param.ip}" name="ip"/>
		<jsp:param value="get" name="action"/>
		<jsp:param value="interface" name="mode"/>
	</jsp:include>
	
<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
<c:if test="${dev.publicIp == param.ip && dev.isSupportVlan()}">
	<form action="vlan_interface.jsp" method="post">
	<table class="interfaceTable">
	<caption>PVID List</caption>	
		<tr>
			<th></th>
			<c:forEach begin="0" end="${dev.infNum}" var="inf" items="${dev.interfaces}" varStatus="select1">
				<c:if test="${inf.ifType == 'eth'}">
	   			<th>P${inf.portId}</th>
	   			</c:if>
			</c:forEach>
		</tr>
		<tr style="background-color: lightgrey;">
			<th>PVID</th>
			<c:forEach var="item" items="${interfaceVlanStatus}">
				<td>${item.dot1qPvid}</td>
			</c:forEach>
		</tr>
	</table>
	<br>
 	</form>
 	
<%-- The Beginning of Static VLAN Page Content --%>

	<jsp:include page="/StaticVlanList">
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<table class="interfaceTable">	
	<caption>Static VLAN List</caption>
		<tr>
			<th>VID</th>
			<c:forEach begin="0" end="${dev.infNum}" var="inf" items="${dev.interfaces}" varStatus="select1">
				<c:if test="${inf.ifType == 'eth'}">
	   			<th>P${inf.portId}</th>
	   			</c:if>
			</c:forEach>
		</tr>		
		<c:forEach var="item" items="${staticVlan}" varStatus="vlanList">
			<c:if test="${vlanList.index%2==0}">
				<tr style="background-color: lightgrey;">
					<td>${item.vlanID}</td>
					<c:forEach var="portState1" items="${item.portState}">
						<td>${portState1}</td>
					</c:forEach>
				</tr>
			</c:if>
			<c:if test="${vlanList.index%2==1}">
				<tr>
					<td>${item.vlanID}</td>
					<c:forEach var="portState1" items="${item.portState}">
						<td>${portState1}</td>
					</c:forEach>
				</tr>
			</c:if>
		</c:forEach>
	</table>    
    <br>
    
    
<%-- The Beginning of Current VLAN Page Content --%>

	<jsp:include page="/CurrentVlanList">
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<table class="interfaceTable">
	<caption>Current VLAN List</caption>	
		<tr>
			<th>VID</th>
			<c:forEach begin="0" end="${dev.infNum}" var="inf" items="${dev.interfaces}" varStatus="select1">
				<c:if test="${inf.ifType == 'eth'}">
	   			<th>P${inf.portId}</th>
	   			</c:if>
			</c:forEach>
		</tr>		
		<c:forEach var="item" items="${currentvlan}" varStatus="vlanList">
			<c:if test="${vlanList.index%2==0}">
				<!-- <div> -->
					<tr style="background-color: lightgrey;">
						<td>${item.vlanID}</td>
						<c:forEach var="portState1" items="${item.portState}">
							<td>${portState1}</td>
						</c:forEach>
					</tr>
				<!-- </div> -->
			</c:if>
			<c:if test="${vlanList.index%2==1}">
				<!-- <div > -->
					<tr>
						<td>${item.vlanID}</td>
						<c:forEach var="portState1" items="${item.portState}">
							<td>${portState1}</td>
						</c:forEach>
					</tr>
				<!-- </div> -->
			</c:if>
		</c:forEach>
	</table>
	<div>U:Untagged T:Tagged F:Forbidden N:None</div>
	<button id="refresh1" class="btn btn-primary btn-xs">Refresh</button>
    <input type="checkbox" onclick="toggleAutoRefresh(this, '${param.ip}');" id="reloadCB"> Auto Refresh
</c:if>
<c:if test="${dev.publicIp == param.ip && !dev.isSupportVlan()}">
<div>Is not support!</div>
</c:if>
</c:forEach>
</div>
</body>
</html>