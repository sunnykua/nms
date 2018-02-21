<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AP List ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="acap/stupidtable.js"></script>
<script type='text/javascript' src="acap/tablefilter_all_min.js"></script>
<script type='text/javascript' src="acap/ezEditTable.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
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
<body onload="createAcMenu('${param.ip}')">
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

		<jsp:include page="/APList">
			<jsp:param value="APList" name="action" />
			<jsp:param value="${param.ip}" name="ip" />
		</jsp:include>

		<div id="page_content">
			<div id="page_title">AP List</div>
			<br>
			<div id="page_title">Online(${OnlineAPList_size})</div>
			<%-- <div>s
				<p align="center">
					<strong><font size="4">Search</font></strong>
			</div>--%>
			<table id="Log_Viewer_Table" class="Log_Viewer_Table">
				<thead>
					<tr class="one">
						<th data-sort="string" align="left">Name</th>
						<th data-sort="string" align="left">Model</th>
						<th data-sort="string" align="left">Firmware</th>
						<th data-sort="string" align="left">IP</th>
						<th data-sort="string" align="left">MAC</th>
						<th data-sort="string" align="left">2.4G / 5G<br>Channel
						</th>
						<th data-sort="string" align="left">Status</th>
						<th data-sort="string" align="left">Profile</th>
						<th data-sort="string" align="left">Group</th>
						<th>Alive</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${OnlineAPList}">
						<tr class="a" onmouseover="this.style.backgroundColor='#ffffa2'"
							onMouseOut="this.style.backgroundColor=''">
							<td class="a">${item[2]}</td>
							<td class="a">${item[3]}</td>
							<td class="a">${item[4]}</td>
							<td class="a">${item[5]}</td>
							<td class="a">${item[6]}</td>
							<td class="a">${item[7]}/${item[8]}</td>
							<td class="a">${item[9]}</td>
							<td class="a">${item[10]}</td>
							<td class="a">${item[11]}</td>
							<c:choose>
								<c:when test="${item[12] == 'offline'}">
									<td class="a"><img src="acap/offline.png" border="0"
										height="24" width="24"></td>
								</c:when>
								<c:when test="${item[12] == 'online'}">
									<td class="a"><img src="images/ok.png" border="0"
										height="24" width="24"></td>
								</c:when>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div id="page_title">Offline(${OfflineAPList_size})</div>
			<table id="Log_Viewer_Table" class="Log_Viewer_Table">
				<thead>
					<tr class="one">
						<th data-sort="string" align="left">Name</th>
						<th data-sort="string" align="left">Model</th>
						<th data-sort="string" align="left">Firmware</th>
						<th data-sort="string" align="left">IP</th>
						<th data-sort="string" align="left">MAC</th>
						<th data-sort="string" align="left">2.4G / 5G<br>Channel
						</th>
						<th data-sort="string" align="left">Status</th>
						<th data-sort="string" align="left">Profile</th>
						<th data-sort="string" align="left">Group</th>
						<th>Alive</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${OfflineAPList}">
						<tr class="a" onmouseover="this.style.backgroundColor='#ffffa2'"
							onMouseOut="this.style.backgroundColor=''">
							<td class="a">${item[2]}</td>
							<td class="a">${item[3]}</td>
							<td class="a">${item[4]}</td>
							<td class="a">${item[5]}</td>
							<td class="a">${item[6]}</td>
							<td class="a"></td>
							<td class="a">${item[9]}</td>
							<td class="a">${item[10]}</td>
							<td class="a">${item[11]}</td>
							<c:choose>
								<c:when test="${item[12] == 'offline'}">
									<td class="a"><img src="acap/offline.png" border="0"
										height="24" width="24"></td>
								</c:when>
								<c:when test="${item[12] == 'online'}">
									<td class="a"><img src="images/ok.png" border="0"
										height="24" width="24"></td>
								</c:when>
							</c:choose>
						</tr>
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