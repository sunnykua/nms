<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>PoeInterface ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
function poe(ip) {
	var poeAdmin = [];
	var poePower = [];
	
    $("#poeTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='adminStatus']").val();
            	poeAdmin.push(value);
    });
    
    $("#poeTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='powerPriority']").val();
            	poePower.push(value);
    });
    
	$.ajax({
	       type: "POST",
	       url: "PoeStatus?action=set",
	       data:{
	    	   adminStatus:poeAdmin,
	    	   powerPriority:poePower,
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

	<%-- <c:if test="${param.action == 'Set'}">
		<c:set var="adminStatus" value="${paramValues.adminStatus}" scope="request"/>
		<c:set var="powerPriority" value="${paramValues.powerPriority}" scope="request"/>
		<jsp:include page="/PoeStatus">
			<jsp:param value="${param.ip}" name="ip"/>
			<jsp:param value="set" name="action"/>
			<jsp:param value="interface" name="mode"/>
		</jsp:include>
	</c:if> --%>

	<jsp:include page="/PoeStatus">
		<jsp:param value="get" name="action"/>
		<jsp:param value="interface" name="mode"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>
	
	<table id="poeTable" class="interfaceTable">
		<%-- <caption>PoE Interface</caption> --%>
		<tr>
			<th>Port</th>
			<th>Admin Status</th>
			<th>Mode</th>
			<th>Priority</th>
		</tr>
		<c:forEach var="item" items="${interfacePoeStatus}">
			<tr>
				<td>${item.portId}</td>
				<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="adminStatus">
					<option value="${item.ifIndex}.1" <c:if test="${item.poePortAdminStatus == 'Enabled'}">selected</c:if>>Enabled</option>
					<option value="${item.ifIndex}.2" <c:if test="${item.poePortAdminStatus == 'Disabled'}">selected</c:if>>Disabled</option>
				</select></td>
				<td>${item.poePortDetectionStatus}</td>
				<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="powerPriority">
					<option value="${item.ifIndex}.1" <c:if test="${item.poePortPowerPriority == 'Critical'}">selected</c:if>>Critical</option>
					<option value="${item.ifIndex}.2" <c:if test="${item.poePortPowerPriority == 'High'}">selected</c:if>>High</option>
					<option value="${item.ifIndex}.3" <c:if test="${item.poePortPowerPriority == 'Low'}">selected</c:if>>Low</option>
				</select></td>
			</tr>
		</c:forEach>
	</table>
	<div class="buttonSection">
		<c:if test="${sessionScope.userLevel<2}">
		<input type="hidden" name="ip" value="${param.ip}">
		<button name="action" value="Set" class="btn btn-primary btn-xs" onclick="poe('${param.ip}')">Apply</button>
	</c:if>
	</div>
</body>
</html>