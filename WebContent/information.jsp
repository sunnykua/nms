<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Information</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/information.css">
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>

<body onload="createMainMenu()">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

<c:if test="${empty sessionScope.username}">
	<c:redirect url="login.jsp"></c:redirect>
</c:if>

<div id="Page_Content">
	<div id="Device_List">
		<ul>
			<li>
				<c:choose>
					<c:when test="${param.device == 'all'}">
						<div class="List_Item List_Item_Slt" onclick="location.href='information.jsp?device=all'">
							<div class="Item_Name">View All Devices</div>
							<!-- <hr> -->
						</div>
					</c:when>
					<c:otherwise>
						<div class="List_Item" onclick="location.href='information.jsp?device=all'">
							<div class="Item_Name">View All Devices</div>
							<!-- <hr> -->
						</div>
					</c:otherwise>
				</c:choose>
				<div class="List_Seperater"><hr></div>
			</li>
		</ul>
		<ul>
			<c:forEach var="device" items="${applicationScope.network.deviceList}" varStatus="status">
				<li>
					<c:choose>
						<c:when test="${param.ip == device.publicIp}">
							<div class="List_Item List_Item_Slt" onclick="location.href='information.jsp?ip=${device.publicIp}'">
								<div class="Item_Name">${device.publicIp}</div>
								<!-- <hr> -->
								<div class="Item_Desc">mac: ${device.phyAddr} port: ${device.rj45Num}+${device.fiberNum}</div>
							</div>
						</c:when>
						<c:otherwise>
							<div class="List_Item" onclick="location.href='information.jsp?ip=${device.publicIp}'">
								<div class="Item_Name">${device.publicIp}</div>
								<!-- <hr> -->
								<div class="Item_Desc">mac: ${device.phyAddr} port: ${device.rj45Num}+${device.fiberNum}</div>
							</div>
						</c:otherwise>
					</c:choose>
					<div class="Item_Seperater"><hr></div>
				</li>
			</c:forEach>
		</ul>
	</div>

	<div id="Device_Content">
		<c:choose>
			<c:when test="${not empty param.ip}">
				<div id="Function_Link"><div id="Link_Container"></div></div>
				<c:out value='<script type="text/javascript">createFunctionMenu(false, "${param.ip}");</script>' escapeXml="false"/>

				<div id="Function_Content">
					<iframe name="iframe" src="devinfo.jsp?ip=${param.ip}"></iframe>
				</div>
			</c:when>
			<c:when test="${param.device == 'all'}">
				<div id="Function_Link"><div id="Link_Container"></div></div>
				<c:out value='<script type="text/javascript">createFunctionMenu(true, "");</script>' escapeXml="false"/>
				
				<div id="Function_Content">
					<iframe name="iframe"></iframe>
				</div>
			</c:when>
		</c:choose>
	</div>
</div>

<%-- The End of Page Content --%>

</body>
</html>