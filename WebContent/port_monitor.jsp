<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>

    <jsp:include page="/Device">
		<jsp:param value="poe_items" name="method"/>
	</jsp:include>
	
      <c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
      <c:if test="${dev.publicIp==param.ip}">
      <div id="extra_${status.count}">
         <table id="portSection" class="interfaceTable_l">
				<tr VALIGN=TOP>
					<td><input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
						<table class="interfaceTable">
						<tr>
						<th>Port</th><th>Monitored</th>
						</tr>
							<c:forEach begin="0" end="${dev.infNum/2-1}" var="inf" items="${dev.interfaces}" varStatus="select1">
							<c:if test="${inf.ifType == 'eth'}">
								<tr>
									<td>${inf.portId}<input type="hidden" name="ifIndex" value="${inf.ifIndex}"></td>
									<td><input type="checkbox" id="CP"<c:if test="${inf.monitored == true}">checked</c:if>></td>
								</tr>
							</c:if>
							</c:forEach>
						</table>
					</td>
					<c:if test="${dev.infNum>1}">
					<td>
						<table class="interfaceTable">
						<tr>
						<th>Port</th><th>Monitored</th>
						</tr>
							<c:forEach begin="${dev.infNum/2}" end="${dev.infNum-1}" var="inf" items="${dev.interfaces}" varStatus="select2">
							<c:if test="${inf.ifType == 'eth'}">
								<tr>
									<td>${inf.portId}<input type="hidden" name="ifIndex" value="${inf.ifIndex}"></td>
									<td><input type="checkbox" id="CP"<c:if test="${inf.monitored == true}">checked</c:if>></td>
								</tr>
								</c:if>
							</c:forEach>
						</table>
					</td>
					</c:if>
				</tr>
			</table>
		</div>
		<div class="buttonSection"><button class="btn btn-primary btn-xs" id="monitorApply_${status.count}">Apply</button></div>
		</c:if>
		</c:forEach>
		
</body>
</html>