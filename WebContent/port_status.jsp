<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>PortStatus ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
function port(ip) {
	var portAdmin = [];
	var portNego = [];
	
    $("#portTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='adminStatus']").val();
            	portAdmin.push(value);
    });
    
    $("#portTable").find("tr:not(:has(th))").each(function () {
            	var value = $(this).find("[name='negoStatus']").val();
            	portNego.push(value);
    });
    
	$.ajax({
	       type: "POST",
	       url: "PortStatus?action=set",
	       data:{
	    	   adminStatus:portAdmin,
	    	   negoStatus:portNego,
	    	   ip:ip
	    	   },
	       success:function(data){
	           alert(data);
	    	},
	});
}
</script>
</head>
<%-- <%@ include file="theme/Theme1/menu_bar.jspf"%> --%>
<body onload="createFunctionMenu('${param.ip}')">
<!-- <div style="width: 1024px; margin: auto;"> -->
<%-- <%@ include file="theme/Theme1/prefix_s.jspf"%> --%>

<%-- The Beginning of Page Content --%>

	<%-- <c:if test="${param.action == 'Set'}">
		<c:set var="adminStatus" value="${paramValues.adminStatus}" scope="request"/>
		<c:set var="negoStatus" value="${paramValues.negoStatus}" scope="request"/>	
		<jsp:include page="/PortStatus">
			<jsp:param value="set" name="action"/>
			<jsp:param value="${param.ip}" name="ip"/>
		</jsp:include>
	</c:if> --%>

	<jsp:include page="/PortStatus">
		<jsp:param value="get" name="action"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	
	<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
	<c:if test="${dev.publicIp == param.ip}">
	<c:forEach begin="1" end="${dev.stackNum}" varStatus="stackId">
		<table id="portTable" class="interfaceTable">
		<c:if test="${dev.stackNum > 1}"><caption>Stack ${stackId.index}</caption></c:if>
			<tr>
				<th rowspan="2">Port</th>
				<!-- <th rowspan="2">Interface</th> -->
				<th rowspan="2">Admin</th>
				<th rowspan="2">Link</th>
				<c:if test="${dev.isSupportNegoState()}">
				<th colspan="2">AutoNego</th>
				</c:if>
				<th rowspan="2">Speed</th>
				<th rowspan="2">Duplex</th>
				<th colspan="2">FlowControl</th>
			</tr>
			<tr>
			<c:if test="${dev.isSupportNegoState()}">
				<th>Support</th>
				<th>Status</th>
			</c:if>
				<th>Ability</th>
				<th>Status</th>
			</tr>
				<c:forEach var="item" items="${portStatus}">
					<c:if test="${item.stackId == stackId.index}">
					<tr>
						<td>${item.portId}</td>
						<%-- <td>${item.interfaceStatus}</td> --%>
						<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="adminStatus">
							<option value="${item.ifIndex}.1" <c:if test="${item.admin == 'Up'}">selected</c:if>>Up</option>
							<option value="${item.ifIndex}.2" <c:if test="${item.admin == 'Down'}">selected</c:if>>Down</option>
						</select></td>
						<td>${item.oper}</td>
						<c:if test="${dev.isSupportNegoState()}">
						<td>${item.autoNegoSupport}</td>
						<td><select <c:if test="${sessionScope.userLevel>1}">disabled</c:if> name="negoStatus">
							<c:choose>
								<c:when test="${item.autoNegoStatus == 'NA'}">
									<option value="9" <c:if test="${item.autoNegoStatus == 'NA'}">selected</c:if>>NA</option>
								</c:when>
								<c:otherwise>
									<option value="${item.ifIndex}.1" <c:if test="${item.autoNegoStatus == 'Enabled'}">selected</c:if>>Enabled</option>
									<option value="${item.ifIndex}.2" <c:if test="${item.autoNegoStatus == 'Disabled'}">selected</c:if>>Disabled</option>								
								</c:otherwise>
							</c:choose>
						</select></td>
						</c:if>
						<td>${item.speed}</td>
						<td>${item.duplex}</td>
						<td>${item.flowCtrlAbility}</td>
						<td>${item.flowCtrlStatus}</td>
					</tr>
					</c:if>
			</c:forEach>
		</table>
		</c:forEach>
		<div class="buttonSection">
			<c:if test="${sessionScope.userLevel<2}">
			<input type="hidden" name="ip" value="${param.ip}">
			<button name="action" value="Set" class="btn btn-primary btn-xs" onclick="port('${param.ip}')">Apply</button>
			</c:if>
		</div>
	</c:if>
	</c:forEach>
<!-- </div> -->
</body>
</html>