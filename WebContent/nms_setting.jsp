<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device Setting</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>
	
	<jsp:include page="/Account">
		<jsp:param value="remoteAccountSetItems" name="type" />
	</jsp:include>

	
		<c:forEach var="dev" items="${applicationScope.network.nmsList}" varStatus="status">
		<c:if test="${dev.publicIp==param.ip}">
		<input type="hidden" id="nms_ip${status.count}" value="${dev.publicIp}">
                <div id="tabs_${status.count}">
					<ul>
						<li><a href="#tabs-1">Setting</a></li>
					</ul>
					
					<div id="tabs-1">
					<%-- <div class="otherSettingTitle">Alias</div>
						<div class="otherSettingDiv">
						    <table class="settingTable">
							    <tr>
								    <td>Alias setting:</td>
								    <td><input type="text" class="settingTextLen" id="nms_alias_${status.count}" value="${dev.aliasName}">
								    </td><td><button class="btn btn-primary btn-xs" id="nms_alias_set_${status.count}">set</button></td>
								</tr>
							</table>
						</div> --%>
					<c:if test="${sessionScope.remoteLevel < 1}">
					<br>
					<div class="otherSettingTitle">User authority setting</div>
						<div>
							<table>
								<tr>
									<th><input type="checkbox"></th>
									<th>User</th>
								</tr>
								<c:forEach var="acc" items="${remoteAccountSetItems}">
									<tr>
									<td>
										<input type="checkbox" name="remoteAccountChk_${status.count}" value="${acc.userName}"
										<c:forEach var="remoteChk" items="${fn:split(dev.remoteMember, ',')}">
											<c:if test="${remoteChk == acc.userName}">checked</c:if>
										</c:forEach>>
									</td>
									<td>${acc.name}</td>
									</tr>
								</c:forEach>
							</table>
							<button class="btn btn-primary btn-xs" id="remoteAccountSet_${status.count}">set</button>
						</div>
					</c:if>
					</div>
				</div>
	    </c:if>
		</c:forEach>
</body>
</html>