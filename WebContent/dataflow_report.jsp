<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Report</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/report.css">
<script type="text/javascript" src="theme/jQueryUI/jquery.blockUI.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript" src="theme/datatables/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="theme/tablesorter/jquery.tablesorter.js"></script>
<script type="text/javascript" src="theme/tablesorter/parser-network.js"></script>
</head>
<script>
$(document).ready(function() { 
    $("[id^=report]").tablesorter({
    	sortList: [[0,0]]
    });
}); 

function title(timeSelect) {
	var date = new Date();
	if(timeSelect == "week"){
		var startDay = $.datepicker.formatDate('yy-mm-dd', new Date(date.getFullYear(), date.getMonth(), date.getDate() - 7));
		var endDay = $.datepicker.formatDate('yy-mm-dd', new Date());
		var dateText = startDay + " ~ " + endDay;
		$("#page_title").html("Last 7 Days (" + dateText + ")");
	}
	else if(timeSelect == "last_week"){
		var startDay = $.datepicker.formatDate('yy-mm-dd', new Date(date.setDate(date.getDate() - date.getDay() - 7)));
		var endDay = $.datepicker.formatDate('yy-mm-dd', new Date(date.setDate(date.getDate() - date.getDay() + 6)));
		var dateText = startDay + " ~ " + endDay;
		$("#page_title").html("Last Week (" + dateText + ")");
	}
	else if(timeSelect == "month"){
		var startDay = $.datepicker.formatDate('yy-mm-dd', new Date(date.setMonth(date.getMonth() - 1)));
		var endDay = $.datepicker.formatDate('yy-mm-dd', new Date());
		var dateText = startDay + " ~ " + endDay;
		$("#page_title").html("Last Month (" + dateText + ")");
	}
	
}
</script>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMenu('Report')">
<div id="area" style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>
	<jsp:include page="/DeviceReport">
		<jsp:param value="reportTable" name="action"/>
		<jsp:param value="${param.ip}" name="ip"/>
		<jsp:param value="${param.timeSelect}" name="timeSelect"/>
		<jsp:param value="${param.dateText}" name="dateText"/>
	</jsp:include>
	
	<div id="page_title">
		<c:choose>
		    <c:when test="${param.timeSelect == 'monthly'}">
		    	${param.dateText}
		    </c:when>
		    <c:when test="${param.timeSelect == 'day'}">
		    	Last 24 Hour
		    </c:when>
			<c:when test="${param.timeSelect == 'week'}">
		    	<c:out value="<script>title('${param.timeSelect}')</script>" escapeXml="false"></c:out>
		    </c:when>
		    <c:when test="${param.timeSelect == 'last_week'}">
		    	<c:out value="<script>title('${param.timeSelect}')</script>" escapeXml="false"></c:out>
		    </c:when>
		    <c:when test="${param.timeSelect == 'month'}">
		    	<c:out value="<script>title('${param.timeSelect}')</script>" escapeXml="false"></c:out>
		    </c:when>
		</c:choose>
	</div>
	<br>
	
	<c:if test="${fn:length(terminal) > 0}">
	<table class="report_table_l">
	<caption>Rx Ranking</caption>
	</table>
	<table id="report${status.count}" class="report_table" style="width:800px">
		<thead> 
		<tr class="reportTable_th">
			<th>RX Ranking</th>
			<th>RX (MB)</th>
			<th style="width:100px">IP</th>
			<th style="width:50px">Port</th>
			<th style="width:100px">Remote IP</th>
			<th>Remote Dev</th>
			<th style="width:200px">Alias Name</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="ter" items="${terminal}" varStatus="select1">
		<c:if test="${ter.rxRanking != 0}">
		<tr class="reportTable_tr">
			<td><font color="red">${ter.rxRanking}</font></td>
			<td><font color="blue">${ter.rxOct}</font></td>
			<td>${ter.publicIp}</td>
			<td>${ter.portId}</td>
			<td>${ter.portRemoteIp}</td>
			<td>${ter.portRemoteDev}</td>
			<td>${ter.aliasName}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table><br></c:if>
	
	<c:if test="${fn:length(terminal) > 0}">
	<table class="report_table_l">
	<caption>Tx Ranking</caption>
	</table>
	<table id="report${status.count}" class="report_table" style="width:800px">
		<thead> 
		<tr class="reportTable_th">
			<th>TX Ranking</th>
			<th>TX (MB)</th>
			<th style="width:100px">IP</th>
			<th style="width:50px">Port</th>
			<th style="width:100px">Remote IP</th>
			<th>Remote Dev</th>
			<th style="width:200px">Alias Name</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="ter" items="${terminal}" varStatus="select1">
		<c:if test="${ter.txRanking != 0}">
		<tr class="reportTable_tr">
			<td><font color="red">${ter.txRanking}</font></td>
			<td><font color="blue">${ter.txOct}</font></td>
			<td>${ter.publicIp}</td>
			<td>${ter.portId}</td>
			<td>${ter.portRemoteIp}</td>
			<td>${ter.portRemoteDev}</td>
			<td>${ter.aliasName}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table><br></c:if>
	
	
	<table class="report_table_l">
	<c:if test="${fn:length(l2l3Switch) > 0}"><caption>Switch</caption></c:if>
	</table>
	<c:forEach var="dev" items="${l2l3Switch}" varStatus="status">
				<table class="report_table" style="width:800px">
					<tr class="reportTable_th">
						<th style="width:50px">IP</th>
						<th>${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px">Name</th>
						<th>${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" class="report_table" style="width:800px">
					<thead> 
					<tr class="reportTable_th">
						<th style="width:50px">Port</th>
						<th style="width:200px">Name</th>
						<th>Root</th>
						<th>Remote Dev</th>
						<th style="width:100px">Remote IP</th>
						<th>RX (MB)</th>
						<th>RX Ranking</th>
						<th>TX (MB)</th>
						<th>TX Ranking</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td><c:if test="${dev.stackNum > 1}">${inf.stackId} - </c:if>${inf.portId}</td>
						<td>${inf.aliasName}</td>
						<td>${inf.rootStatus}</td>
						<td>${inf.portRemoteDev}</td>
						<td>${inf.portRemoteIp}</td>
						<td><font color="blue">${inf.rxOct}</font></td>
						<td><font color="red">${inf.rxRanking}</font></td>
						<td><font color="blue">${inf.txOct}</font></td>
						<td><font color="red">${inf.txRanking}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	<table class="report_table_l">
	<c:if test="${fn:length(firewall) > 0}"><caption>Firewall</caption></c:if>
	</table>
	<c:forEach var="dev" items="${firewall}" varStatus="status">
				<table class="report_table" style="width:800px">
					<tr class="reportTable_th">
						<th style="width:50px">IP</th>
						<th>${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px">Name</th>
						<th>${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" class="report_table" style="width:800px">
					<thead> 
					<tr class="reportTable_th">
						<th style="width:50px">Port</th>
						<th style="width:200px">Name</th>
						<th>Root</th>
						<th>Remote Dev</th>
						<th style="width:100px">Remote IP</th>
						<th>RX (MB)</th>
						<th>RX Ranking</th>
						<th>TX (MB)</th>
						<th>TX Ranking</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td><c:if test="${dev.stackNum > 1}">${inf.stackId} - </c:if>${inf.portId}</td>
						<td>${inf.aliasName}</td>
						<td>${inf.rootStatus}</td>
						<td>${inf.portRemoteDev}</td>
						<td>${inf.portRemoteIp}</td>
						<td><font color="blue">${inf.rxOct}</font></td>
						<td><font color="red">${inf.rxRanking}</font></td>
						<td><font color="blue">${inf.txOct}</font></td>
						<td><font color="red">${inf.txRanking}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	<table class="report_table_l">
	<c:if test="${fn:length(server) > 0}"><caption>Server</caption></c:if>
	</table>
	<c:forEach var="dev" items="${server}" varStatus="status">
				<table class="report_table" style="width:500px">
					<tr class="reportTable_th">
						<th style="width:50px">IP</th>
						<th>${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px">Name</th>
						<th>${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" class="report_table" style="width:500px">
					<thead> 
					<tr class="reportTable_th">
						<th style="width:50px">Port</th>
						<th>Root</th>
						<th>Remote Dev</th>
						<th style="width:100px">Remote IP</th>
						<th>RX (MB)</th>
						<th>TX (MB)</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td>${inf.portId}</td>
						<td>${inf.rootStatus}</td>
						<td>${inf.portRemoteDev}</td>
						<td>${inf.portRemoteIp}</td>
						<td><font color="blue">${inf.rxOct}</font></td>
						<td><font color="blue">${inf.txOct}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	
	<table class="report_table_l">
	<c:if test="${fn:length(ac) > 0}"><caption>AC</caption></c:if>
	</table>
	<c:forEach var="dev" items="${ac}" varStatus="status">
				<table class="report_table" style="width:500px">
					<tr class="reportTable_th">
						<th style="width:50px">IP</th>
						<th>${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px">Name</th>
						<th>${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" class="report_table" style="width:500px">
					<thead> 
					<tr class="reportTable_th">
						<th style="width:50px">Port</th>
						<th>Root</th>
						<th>Remote Dev</th>
						<th style="width:100px">Remote IP</th>
						<th>RX (MB)</th>
						<th>TX (MB)</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td>${inf.portId}</td>
						<td>${inf.rootStatus}</td>
						<td>${inf.portRemoteDev}</td>
						<td>${inf.portRemoteIp}</td>
						<td><font color="blue">${inf.rxOct}</font></td>
						<td><font color="blue">${inf.txOct}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	
	<table class="report_table_l">
	<c:choose>
		<c:when test="${fn:length(ap) > 0}">
			<caption>AP</caption>
			<table id="report${status.count}" class="report_table" style="width: 300px">
				<thead>
					<tr class="reportTable_th">
						<th style="width: 100px">IP</th>
						<th>Name</th>
						<th>RX (MB)</th>
						<th>TX (MB)</th>
					</tr>
				</thead>
		</c:when>
		<c:otherwise>
			<c:forEach var="dev" items="${ac}" varStatus="status">
			<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
				<caption>AP</caption>
				<table id="report${status.count}" class="report_table" style="width: 300px">
					<thead>
						<tr class="reportTable_th">
							<th style="width: 100px">IP</th>
							<th>Name</th>
							<th>RX (MB)</th>
							<th>TX (MB)</th>
						</tr>
					</thead>
			</c:if>
			</c:forEach>
		</c:otherwise>
	</c:choose>
		<tbody>
			<c:forEach var="dev" items="${ap}" varStatus="status">
				<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td>${dev.publicIp}</td><td>${dev.aliasName}</td><td><font color="blue">${inf.rxOct}</font></td><td><font color="blue">${inf.txOct}</font></td>
					</tr>
				</c:forEach>
			</c:forEach>
			<c:forEach var="dev" items="${ac}" varStatus="status">
			<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
				<c:forEach var="item" items="${ApTrafficList}" varStatus="select1">
				<tr class="reportTable_tr">
					<td>${item[0]}</td>
					<td>${item[1]}</td>
					<td><font color="blue">${item[2]}</font></td>
					<td><font color="blue">${item[3]}</font></td>
				</tr>
				</c:forEach>
			</c:if>
			</c:forEach>
		</tbody>
		</table>
	</table>
	</table>

	<c:choose>
		<c:when test="${fn:length(ApSsidTraffic) > 0}">
			<table class="report_table_l">
				<caption>SSID</caption>
				<tr VALIGN=TOP>
					<td>
						<table id="report${status.count}" class="report_table"
							style="width: 500px">
							<thead>
								<tr class="reportTable_th">
									<th style="width: 100px">IP</th>
									<th>Name</th>
									<th>Radio</th>
									<th style="width: 110px">SSID</th>
									<th>RX (MB)</th>
									<th>TX (MB)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="item" items="${ApSsidTraffic}"
									varStatus="select1">
									<tr class="reportTable_tr">
										<td>${item[0]}</td>
										<td>${item[1]}</td>
										<td>${item[2]}</td>
										<td>${item[3]}</td>
										<td><font color="blue">${item[4]}</font></td>
										<td><font color="blue">${item[5]}</font></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
					<td>
						<table id="report${status.count}" class="report_table"
							style="width: 300px">
							<thead>
								<tr class="reportTable_th">
									<th style="width: 110px">SSID</th>
									<th>RX (MB)</th>
									<th>TX (MB)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="item" items="${SsidTraffic}"
									varStatus="select1">
									<tr class="reportTable_tr">
										<td>${item[0]}</td>
										<td><font color="blue">${item[1]}</font></td>
										<td><font color="blue">${item[2]}</font></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
			</table>
			<br>
		</c:when>
	</c:choose>
</div>
</body>
</html>