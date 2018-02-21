<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>VlanInterface ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
function vlan(ip) {
	var acceptableStatus = [];
	var ingressStatus = [];
        var gvrpStatus = [];
	
    $("#vlanTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='acceptableStatus']").val();
            	acceptableStatus.push(value);
    });
    
    $("#vlanTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='ingressStatus']").val();
            	ingressStatus.push(value);
    });
	
    $("#vlanTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='gvrpStatus']").val();
            	gvrpStatus.push(value);
    });
    
	$.ajax({
	       type: "POST",
	       url: "VlanStatus?action=set",
	       data:{
	    	   acceptableStatus:acceptableStatus,
	    	   ingressStatus:ingressStatus,
                   gvrpStatus:gvrpStatus,
	    	   ip:ip
	    	   },
	       success:function(data){
	           alert(data);
	    	},
	});
}
</script>
</head>


<body>

<%-- The Beginning of Page Content --%>

	<c:if test="${param.action == 'Set'}">
		<c:set var="acceptableStatus" value="${paramValues.acceptableStatus}" scope="request"/>
		<c:set var="ingressStatus" value="${paramValues.ingressStatus}" scope="request"/>
		<c:set var="gvrpStatus" value="${paramValues.gvrpStatus}" scope="request"/>
		<jsp:include page="/VlanStatus">
			<jsp:param value="${param.ip}" name="ip"/>
			<jsp:param value="set" name="action"/>
			<jsp:param value="interface" name="mode"/>
		</jsp:include>
	</c:if>

	<jsp:include page="/VlanStatus">
		<jsp:param value="${param.ip}" name="ip"/>
		<jsp:param value="get" name="action"/>
		<jsp:param value="interface" name="mode"/>
	</jsp:include>

	
	<table id="vlanTable" class="interfaceTable">
		<%-- <caption>VLAN Interface</caption> --%>
		<tr>
			<th>Port</th>
			<th>PVID</th>
			<th>Acceptable Frame Type</th>
			<th>Ingress Filtering</th>
			<th>Gvrp Status</th>
		</tr>
		<c:forEach var="item" items="${interfaceVlanStatus}">
			<tr>
				<td>${item.portId}</td>
				<td>${item.dot1qPvid}</td>
				<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="acceptableStatus">
					<option value="${item.ifIndex}.1" <c:if test="${item.dot1qPortAcceptableFrameType == 'All Frames'}">selected</c:if>>All Frames</option>
					<option value="${item.ifIndex}.2" <c:if test="${item.dot1qPortAcceptableFrameType == 'Tagged Frames'}">selected</c:if>>Tagged Frames</option>
				</select></td>
				<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="ingressStatus">
					<option value="${item.ifIndex}.1" <c:if test="${item.dot1qPortIngressFiltering == 'Enabled'}">selected</c:if>>Enabled</option>
					<option value="${item.ifIndex}.2" <c:if test="${item.dot1qPortIngressFiltering == 'Disabled'}">selected</c:if>>Disabled</option>
				</select></td>
				<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="gvrpStatus">
					<option value="${item.ifIndex}.1" <c:if test="${item.dot1qPortGvrpStatus == 'Enabled'}">selected</c:if>>Enabled</option>
					<option value="${item.ifIndex}.2" <c:if test="${item.dot1qPortGvrpStatus == 'Disabled'}">selected</c:if>>Disabled</option>
				</select></td>
			</tr>
		</c:forEach>
	</table>
	<div class="buttonSection">
		<c:if test="${sessionScope.userLevel<2}">
		<input type="hidden" name="ip" value="${param.ip}">
		<button name="action" value="Set" class="btn btn-primary btn-xs" onclick="vlan('${param.ip}')">Apply</button>
		</c:if>
	</div>
</body>
</html>