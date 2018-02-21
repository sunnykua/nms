<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" type="text/css" href="theme/Theme1/vlan_global.css">
</head>

<body>

	<jsp:include page="/VlanStatus">
		<jsp:param value="get" name="action"/>
		<jsp:param value="global" name="mode"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<table id="Vlan_Global_Table">
		<caption>VLAN Global</caption>
		<tr>
			<th>VLAN ID</th>
			<th>VLAN Name</th>
			<th>Status</th>
		</tr>
		<c:forEach var="item" items="${globalVlanStatus.items}">
			<%-- <c:if test="${ item.index <= item.vlanNum }"> --%>
				<tr>
					<%-- <td>${item.index}</td> --%>
					<td>${item.vlanNum}</td>
					<td>${item.dot1qVlanStaticName}</td>
					<td>${item.dot1qVlanStaticStatus}</td>
				</tr>
			<%-- </c:if> --%>
		</c:forEach>
	</table>

</body>
</html>