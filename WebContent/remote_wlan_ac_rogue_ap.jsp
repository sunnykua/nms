<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rogue AP List ${param.remote_address}
	${param.remote_device_ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="acap/stupidtable.js"></script>
<script type='text/javascript' src="acap/tablefilter_all_min.js"></script>
<script type='text/javascript' src="acap/ezEditTable.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css"
	href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<script>
	$(function() {
		$("table").stupidtable();
	});
</script>
<style type="text/css">
th[data-sort] {
	cursor: pointer;
}
</style>

</head>
<body
	onload="createRemoteAcMenu('${param.remote_address}','${param.remote_device_ip}')">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<%-- The Beginning of Page Content --%>

		<%-- <c:choose>
			<c:when test="${cookie.stay.value == 'Y'}">
				<c:if test="${empty cookie.username}">
					<c:redirect url="login.jsp"></c:redirect>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${empty sessionScope.username}">
					<c:redirect url="login.jsp"></c:redirect>
				</c:if>
				<c:if test="${empty cookie.username}">
					<c:redirect url="login.jsp"></c:redirect>
				</c:if>
			</c:otherwise>
		</c:choose> --%>

		<jsp:include page="/ACFriendlyRogueAP">
			<jsp:param value="RogueAPList" name="action" />
			<jsp:param value="${param.remote_address}" name="remote_address" />
			<jsp:param value="${param.remote_device_ip}" name="remote_device_ip" />
		</jsp:include>

		<div id="page_content" align="center">
			<%-- <div>
				<p align="center">
					<strong><font size="4">Search</font></strong>
			</div>--%>
			<div id="page_title">Rogue AP List(${RogueAPList_size})</div>
			<table id="Log_Viewer_Table" class="Log_Viewer_Table">
				<thead>
					<tr class="one">
						<th data-sort="string" align="left">BSSID</th>
						<th data-sort="string" align="left">SSID</th>
						<th data-sort="string" align="left">Security Mode</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${RogueAPList}">
						<c:choose>
							<c:when test="${item[2] == 'none'}">
							</c:when>
							<c:when test="${item[2] != 'none'}">
								<tr class="a" onmouseover="this.style.backgroundColor='#ffffa2'"
									onMouseOut="this.style.backgroundColor=''">
									<td class="a">${item[2]}</td>
									<td class="a">${item[3]}</td>
									<td class="a">${item[4]}</td>
								</tr>
							</c:when>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<script type='text/javascript'>
			var table12_Props = {
				highlight_keywords : true,
				on_keyup : true,
				on_keyup_delay : 1,
				single_search_filter : true,
				selectable : true
			};
			var tf12 = setFilterGrid("Log_Viewer_Table", table12_Props);
		</script>
	</div>
</body>
</html>