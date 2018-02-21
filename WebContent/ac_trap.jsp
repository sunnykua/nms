<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Trap</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>
<%-- The Beginning of Page Content --%>

	<jsp:include page="/Device">
		<jsp:param value="acTrap_view" name="method"/>
		<jsp:param value="${param.ip}" name="ip"/>
		<jsp:param value="${param.mac}" name="mac"/>
	</jsp:include>

	<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
      <c:if test="${dev.publicIp==param.ip}">
      <input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
      <input type="hidden" id="switchPhy_${status.count}" value="${dev.phyAddr}">
      <table class="interfaceTable" id="acTrapTable_${status.count}">
            <tr>
            	<th></th>
            	<th></th>
            	<th>Email</th>
            	<th></th>
            	<th>SMS</th>
            </tr>
			<tr>
				<th class="th_left_align">AP Join</th>
				<td><input type="checkbox" id="apJoinMailCheck_${status.count}" <c:if test="${apJoinMailCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="apJoinMail_${status.count}" value="${apJoinMail}"></td>
				<td><input type="checkbox" id="apJoinSmsCheck_${status.count}" <c:if test="${apJoinSmsCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="apJoinSms_${status.count}" value="${apJoinSms}"></td>
			</tr>
			<tr>
				<th class="th_left_align">AP Leave</th>
				<td><input type="checkbox" id="apLeaveMailCheck_${status.count}" <c:if test="${apLeaveMailCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="apLeaveMail_${status.count}" value="${apLeaveMail}"></td>
				<td><input type="checkbox" id="apLeaveSmsCheck_${status.count}" <c:if test="${apLeaveSmsCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="apLeaveSms_${status.count}" value="${apLeaveSms}"></td>
			</tr>
			<tr>
				<th class="th_left_align">AC WarmStart</th>
				<td><input type="checkbox" id="acWarmStartMailCheck_${status.count}" <c:if test="${acWarmStartMailCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="acWarmStartMail_${status.count}" value="${acWarmStartMail}"></td>
				<td><input type="checkbox" id="acWarmStartSmsCheck_${status.count}" <c:if test="${acWarmStartSmsCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="acWarmStartSms_${status.count}" value="${acWarmStartSms}"></td>
			</tr>
			<tr>
				<th class="th_left_align">AC ColdStart</th>
				<td><input type="checkbox" id="acColdStartMailCheck_${status.count}" <c:if test="${acColdStartMailCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="acColdStartMail_${status.count}" value="${acColdStartMail}"></td>
				<td><input type="checkbox" id="acColdStartSmsCheck_${status.count}" <c:if test="${acColdStartSmsCheck == 1}">checked="checked"</c:if>></td>
				<td><input type="text" style="width:300px;" id="acColdStartSms_${status.count}" value="${acColdStartSms}"></td>
			</tr>
      </table>
      <div class="buttonSection"><button class="btn btn-primary btn-xs" id="acTrapApply_${status.count}">Apply</button></div>
      </c:if>
	</c:forEach>
</body>
</html>