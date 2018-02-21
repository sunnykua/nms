<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/home.css">
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<style>
#dev_explain {
  margin: auto;
  width: 650px;
  font-family: Helvetica;
}
#dev_explain_table th {
  color:#191970;
  float:left;
  font-size:18px;
  margin-top:15px;
}
#dev_explain_table td {
  border-bottom: 1px solid #729ea5;
  font-size:14px;
}
</style>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

<%-- <c:choose>
	<c:when test="${cookie.stay.value == 'Y'}">
		<c:if test="${empty cookie.stay}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
        <c:if test="${empty sessionScope.username}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
	</c:when> --%>
	<%-- <c:when test="${cookie.stay.value == 'N'}">
		<c:if test="${empty sessionScope.username}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
        <c:if test="${empty cookie.username}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
	</c:when> --%>
	<%-- <c:otherwise>
	    <c:if test="${empty sessionScope.username}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
        <c:if test="${empty cookie.username}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
	</c:otherwise>
</c:choose> --%>

<%-- <c:if test="${empty sessionScope.username}">
	<c:redirect url="login.jsp"></c:redirect>
</c:if>

<c:if test="${empty cookie.username}">
	<c:redirect url="login.jsp"></c:redirect>
</c:if> --%>

	<div id="dev_explain"><br>
		<table id="dev_explain_table">
			<tr><th>Device</th></tr>
			<tr><td><p>Collect devices for management and show basic information and status.</p></td></tr>
			<tr><th>Topology</th></tr>
			<tr><td><p>Provide a tree structure to show all managed devices by their logical relationship, and also provide a function to show trace path between any two peers.</p></td></tr>
			<tr><th><p>WLC</p></th></tr>
			<tr><th>AC- </th></tr>
			<tr><td><p>AC information and Status.</p></td></tr>
			<tr><th>AP- </th></tr>
			<tr><td><p>AP list, real-time chart, history chart and log.</p></td></tr>
			<tr><th>Log</th></tr>
			<tr><td><p>System wide log viewer. </p></td></tr>
			<tr><th>Account</th></tr>
			<tr><td><p>View and edit user account data.</p></td></tr>
			<tr><th>Setting</th></tr>
			<tr><td><p>Modify system settings.</p></td></tr>
			<tr><th>About</th></tr>
			<tr><td><p>Show NMS version and company logo, address, contact persion, e-mail and telephone.</p></td></tr>
			<tr><th>Switch Overview</th></tr>
			<tr><td><p>Device detail information and setting.</p></td></tr>
			<tr><th>Statistics</th></tr>
			<tr><td><p>Record many types of data counter. </p></td></tr>
			<tr><th>Interface</th></tr>
			<tr><td><p>View and set something to device.</p></td></tr>
			<tr><th>Chart</th></tr>
			<tr><td><p>Real-time and history chart.</p></td></tr>
		</table>
	</div>
<%-- The End of Page Content --%>
</div>
</body>
</html>