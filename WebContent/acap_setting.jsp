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
<script src="theme/Theme1/device.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>
		<c:forEach var="dev" items="${applicationScope.network.acapList}" varStatus="status">
		<c:if test="${dev.publicIp==param.ip}">
		
			            <%-- <div class="extra_acap" id="extra_acap${status.count}" style="display: none;"> --%>
				            <div id="tabs_acap${status.count}">
				            Device IP : ${dev.publicIp} Setting
								<ul>
									<li><a href="#tabs-1">Setting</a></li>
								</ul>
								<input type="hidden" id="acapid_${status.count}" value="${dev.publicIp}">
								<div id="tabs-1">
									<table>
									    <tr>
										    <td>Alias setting:</td>
										    <td><input type="text" class="settingTextLen" id="alias_acap${status.count}" value="${dev.aliasName}">
										    <button class="btn btn-primary btn-xs" id="alias_acap_set${status.count}">set</button></td>
										</tr>
										<c:if test="${dev.snmpSupport>0}">
										<tr>
											<td>Read Community:</td>
											<td><input type="text" class="settingTextLen" id="read_community_acap${status.count}" value="${dev.readCommunity}">
											<button class="btn btn-primary btn-xs" id="read_community_set_acap${status.count}">set</button></td>
										</tr>
										<tr>
											<td>Write Community:</td>
											<td><input type="text" class="settingTextLen" id="write_community_acap${status.count}" value="${dev.writeCommunity}">
											<button class="btn btn-primary btn-xs" id="write_community_set_acap${status.count}">set</button></td>
										</tr>
										</c:if>
									</table>
								</div>
							</div>
			            <!-- </div> -->
	    </c:if> 
		</c:forEach>
</body>
</html>