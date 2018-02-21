<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" type="text/css" href="theme/Theme1/poe_global.css">
</head>

<body>

	<jsp:include page="/PoeStatus">
		<jsp:param value="get" name="action"/>
		<jsp:param value="global" name="mode"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<table id="PoE_Global_Table">
		<caption>PoE Global</caption>
		<tr>
			<th colspan="2">Mainpower Status</th>
		</tr>
		<tr>
			<td>PoE Maximum Available Power</td><td>${globalPoeStatus.items[0].poeMainMaxAvailablePower} Walts</td>
		</tr>
		<tr>
			<td>System Operation Status</td><td>${globalPoeStatus.items[0].poeMainSysOperStatus}</td>
		</tr>							
		<tr>
			<td>PoE Power Consumption</td><td>${globalPoeStatus.items[0].poeMainPowerConsumption} Walts</td>
		</tr>
	</table>

</body>
</html>