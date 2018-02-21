<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link rel="stylesheet" type="text/css" href="theme/Theme1/link_view.css">
</head>
<body>

	<jsp:include page="/LinkView">
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>

	<c:catch var="parsingError">
		<%-- <fmt:parseNumber var="rowCount" type="number" value="${(table.portNum + 11) / 12}" integerOnly="true"/> --%><%-- ***** Need To Be Modify ***** --%>
		<c:set var="rowCount" value="2" scope="request"/><%-- ***** work around here ***** --%>
		
		<table class="panel" cellspacing="0">
			<tr>
				<td colspan="12"><table class="legend" cellspacing="0"><tr>
					<td><div class="iconAdminDown"></div></td>
					<td class="iconText">AdminDown</td>
					<td><div class="iconLinkUp"></div></td>
					<td class="iconText">LinkUp</td>
					<td><div class="iconLinkDown"></div></td>
					<td class="iconText">LinkDown</td>
				</tr></table></td>
			</tr>
			<c:forEach var="i" begin="0" end="${rowCount - 1}" varStatus="rowStatus">
				<tr>
					<c:forEach var="item" items="${table.items}" begin="${rowStatus.index * 12}" end="${rowStatus.count * 12 - 1}">
						<th class="portId">${item.index}</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach var="item" items="${table.items}" begin="${rowStatus.index * 12}" end="${rowStatus.count * 12 - 1}">
						<c:choose>
							<c:when test="${item.adminStatus != '1'}">	<%-- '1': Admin Up --%>
								<td class="adminDown"></td>
							</c:when>
							<c:when test="${item.operStatus == '1'}">	<%-- '1': Oper Up --%>
								<td class="linkUp"></td>
							</c:when>
							<c:otherwise>
								<td class="linkDown"></td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	</c:catch>
	
	<c:if test="${not empty parsingError}">
		<p>Oops! There is something wrong in this page.</p>
	</c:if>

</body>
</html>