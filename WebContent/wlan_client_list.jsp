<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Client List ${param.ip}</title>
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
<script type="text/javascript">
	
</script>
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

		<jsp:include page="/APSTAList">
			<jsp:param value="ClientList" name="action" />
			<jsp:param value="${param.ip}" name="ip" />
		</jsp:include>

		<div id="page_content" align="center">
			<div id="page_title">Client List(${ClientList_size})</div>
			<%-- <div>
				<p align="center">
					<strong><font size="4">Search</font></strong>
			</div>--%>
			<table id="Log_Viewer_Table_min" class="Log_Viewer_Table_min">
				<thead>
					<tr class="one">
						<th data-sort="string" align="left">Client</th>
						<th data-sort="string" align="left">MAC</th>
						<th data-sort="string" align="left">MAC Vendor</th>
						<th data-sort="string" align="left">IP</th>
						<th data-sort="string" align="left">SSID</th>
						<th data-sort="int" align="left">Signal</th>
						<th data-sort="int" align="left">RX Rate</th>
						<th data-sort="int" align="left">TX Rate</th>
						<th data-sort="string" align="left">AP Name</th>
						<th data-sort="string" align="left">AP IP</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${ClientList}">
						<tr class="a" onmouseover="this.style.backgroundColor='#ffffa2'"
							onMouseOut="this.style.backgroundColor=''">
							<td class="a">${item[11]}</td>
							<td class="a">${item[5]}</td>
							<td class="a">${item[12]}</td>
							<td class="a">${item[6]}</td>
							<td class="a">${item[7]}</td>
							<td class="a">-${item[8]}&nbsp;dbm</td>
							<td class="a">${item[9]}&nbsp;Mbps</td>
							<td class="a">${item[10]}&nbsp;Mbps</td>
							<td class="a">${item[2]}</td>
							<td class="a">${item[3]}</td>
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
			var tf12 = setFilterGrid("Log_Viewer_Table_min", table12_Props);
		</script>
	</div>
</body>
</html>