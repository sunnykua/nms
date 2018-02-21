<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Weekly Report</title>
<script>
<%@ include file="javascript/jquery-1.11.2.min.js"%>
<%@ include file="theme/tablesorter/jquery.tablesorter.js"%>
<%@ include file="theme/tablesorter/parser-network.js"%>
</script>
<script>
$(document).ready(function() { 
    $("[id^=report]").tablesorter({
    	sortList: [[0,0]]
    });
});
</script>
</head>
<body>
<div id="area" style="width: 1024px;margin: auto;">
	<jsp:include page="/DeviceReport">
		<jsp:param value="weeklyReportTable" name="action"/>
	</jsp:include>
	
	<div style="font-size: 36px;font-weight: bold;text-align: center;font-style: italic;">Cybert Expert</div>
	<div style="font-size: 20px;font-weight: bold;text-align: center;background-color:#00bfff;"><font color="#ffffff">Weekly Report</font></div>
	
	<font style="float:right;">${date}</font>
	<br>
	
	<c:if test="${fn:length(rx) > 0}">
	<table style="table-layout: fixed;margin: auto;">
	<caption style="font-size: 25px;font-weight: bold;width: 300px;">Rx Ranking</caption>
	</table>
	<table  style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
		<thead> 
		<tr style="background-color:#C0C0FF;">
			<th style="border: 1px solid #729ea5;text-align: center;">RX Ranking</th>
			<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
			<th style="width:100px;border: 1px solid #729ea5;text-align: center;">IP</th>
			<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
			<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
			<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
			<th style="width:200px;border: 1px solid #729ea5;text-align: center;">Alias Name</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="ter" items="${rx}" varStatus="select1">
		<c:if test="${ter.rxRanking != 0}">
		<tr class="reportTable_tr">
			<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${ter.rxRanking}</font></td>
			<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${ter.rxOct}</font></td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.publicIp}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portId}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portRemoteIp}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portRemoteDev}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.aliasName}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table><br></c:if>
	
	<c:if test="${fn:length(tx) > 0}">
	<table style="table-layout: fixed;margin: auto;">
	<caption style="font-size: 25px;font-weight: bold;width: 300px;">Tx Ranking</caption>
	</table>
	<table  style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
		<thead> 
		<tr style="background-color:#C0C0FF;">
			<th style="border: 1px solid #729ea5;text-align: center;">TX Ranking</th>
			<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
			<th style="width:100px;border: 1px solid #729ea5;text-align: center;">IP</th>
			<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
			<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
			<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
			<th style="width:200px;border: 1px solid #729ea5;text-align: center;">Alias Name</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="ter" items="${tx}" varStatus="select1">
		<c:if test="${ter.txRanking != 0}">
		<tr class="reportTable_tr">
			<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${ter.txRanking}</font></td>
			<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${ter.txOct}</font></td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.publicIp}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portId}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portRemoteIp}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.portRemoteDev}</td>
			<td style="border: 1px solid #729ea5;text-align: center;">${ter.aliasName}</td>
		</tr>
		</c:if>
		</c:forEach>
		</tbody>
	</table><br></c:if>
	
	
	<table style="table-layout: fixed;margin: auto;">
	<c:if test="${fn:length(l2l3Switch) > 0}"><caption style="font-size: 25px;font-weight: bold;width: 300px;">Switch</caption></c:if>
	</table>
	<c:forEach var="dev" items="${l2l3Switch}" varStatus="status">
				<table style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<thead> 
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
						<th style="width:200px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Root</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
						<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX Ranking</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX Ranking</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td style="border: 1px solid #729ea5;text-align: center;"><c:if test="${dev.stackNum > 1}">${inf.stackId} - </c:if>${inf.portId}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.aliasName}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.rootStatus}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteDev}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteIp}</td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.rxOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${inf.rxRanking}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.txOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${inf.txRanking}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	<table style="table-layout: fixed;margin: auto;">
	<c:if test="${fn:length(firewall) > 0}"><caption style="font-size: 25px;font-weight: bold;width: 300px;">Firewall</caption></c:if>
	</table>
	<c:forEach var="dev" items="${firewall}" varStatus="status">
				<table style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" style="width:800px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<thead> 
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
						<th style="width:200px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Root</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
						<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX Ranking</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX Ranking</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td style="border: 1px solid #729ea5;text-align: center;"><c:if test="${dev.stackNum > 1}">${inf.stackId} - </c:if>${inf.portId}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.aliasName}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.rootStatus}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteDev}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteIp}</td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.rxOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${inf.rxRanking}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.txOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="red">${inf.txRanking}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	<table style="table-layout: fixed;margin: auto;">
	<c:if test="${fn:length(server) > 0}"><caption style="font-size: 25px;font-weight: bold;width: 300px;">Server</caption></c:if>
	</table>
	<c:forEach var="dev" items="${server}" varStatus="status">
				<table style="width:500px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" style="width:500px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<thead> 
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Root</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
						<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portId}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.rootStatus}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteDev}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteIp}</td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.rxOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.txOct}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	
	<table style="table-layout: fixed;margin: auto;">
	<c:if test="${fn:length(ac) > 0}"><caption style="font-size: 25px;font-weight: bold;width: 300px;">AC</caption></c:if>
	</table>
	<c:forEach var="dev" items="${ac}" varStatus="status">
				<table style="width:500px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.publicIp}</th>
					</tr>
					<tr>
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">${dev.aliasName}</th>
					</tr>
				</table>
				<table id="report${status.count}" style="width:500px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;">
					<thead> 
					<tr style="background-color:#C0C0FF;">
						<th style="width:50px;border: 1px solid #729ea5;text-align: center;">Port</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Root</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Remote Dev</th>
						<th style="width:100px;border: 1px solid #729ea5;text-align: center;">Remote IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="inf" items="${dev.interfacesReport}" varStatus="select1">
					<tr class="reportTable_tr">
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portId}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.rootStatus}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteDev}</td>
						<td style="border: 1px solid #729ea5;text-align: center;">${inf.portRemoteIp}</td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.rxOct}</font></td>
						<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.txOct}</font></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				<br>
	</c:forEach>
	
	<table style="table-layout: fixed;margin: auto;">
	<c:choose>
		<c:when test="${fn:length(ap) > 0}">
			<caption style="font-size: 25px;font-weight: bold;">AP</caption>
			<table id="report${status.count}" style="width:300px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;" >
				<thead>
					<tr style="background-color:#C0C0FF;">
						<th  style="width: 100px;border: 1px solid #729ea5;text-align: center;">IP</th>
						<th style="border: 1px solid #729ea5;text-align: center;">Name</th>
						<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
						<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
					</tr>
				</thead>
		</c:when>
		<c:otherwise>
			<c:forEach var="dev" items="${ac}" varStatus="status">
			<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
				<caption style="font-size: 25px;font-weight: bold;">AP</caption>
				<table id="report${status.count}" style="width:300px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;" >
					<thead>
						<tr style="background-color:#C0C0FF;">
							<th  style="width: 100px;border: 1px solid #729ea5;text-align: center;">IP</th>
							<th style="border: 1px solid #729ea5;text-align: center;">Name</th>
							<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
							<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
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
						<td style="border: 1px solid #729ea5;text-align: center;">${dev.publicIp}</td><td style="border: 1px solid #729ea5;text-align: center;">${dev.aliasName}</td><td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.rxOct}</font></td><td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${inf.txOct}</font></td>
					</tr>
				</c:forEach>
			</c:forEach>
			<c:forEach var="dev" items="${ac}" varStatus="status">
			<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
				<c:forEach var="item" items="${ApTrafficList}" varStatus="select1">
				<tr class="reportTable_tr">
					<td style="border: 1px solid #729ea5;text-align: center;">${item[0]}</td>
					<td style="border: 1px solid #729ea5;text-align: center;">${item[1]}</td>
					<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[2]}</font></td>
					<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[3]}</font></td>
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
			<table style="table-layout: fixed;margin: auto;">
				<caption style="font-size: 25px;font-weight: bold;">SSID</caption>
				<tr VALIGN=TOP>
					<td>
						<table id="report${status.count}" style="width:500px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;"
							>
							<thead>
								<tr style="background-color:#C0C0FF;">
									<th  style="width: 100px;border: 1px solid #729ea5;text-align: center;">IP</th>
									<th style="border: 1px solid #729ea5;text-align: center;">Name</th>
									<th style="border: 1px solid #729ea5;text-align: center;">Radio</th>
									<th  style="width: 110px;border: 1px solid #729ea5;text-align: center;">SSID</th>
									<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
									<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="item" items="${ApSsidTraffic}"
									varStatus="select1">
									<tr class="reportTable_tr">
										<td style="border: 1px solid #729ea5;text-align: center;">${item[0]}</td>
										<td style="border: 1px solid #729ea5;text-align: center;">${item[1]}</td>
										<td style="border: 1px solid #729ea5;text-align: center;">${item[2]}</td>
										<td style="border: 1px solid #729ea5;text-align: center;">${item[3]}</td>
										<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[4]}</font></td>
										<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[5]}</font></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
					<td>
						<table id="report${status.count}" style="width:300px;table-layout: fixed;margin: auto;border-collapse: collapse;font-size:12px;"
							>
							<thead>
								<tr style="background-color:#C0C0FF;">
									<th  style="width: 110px;border: 1px solid #729ea5;text-align: center;">SSID</th>
									<th style="border: 1px solid #729ea5;text-align: center;">RX (MB)</th>
									<th style="border: 1px solid #729ea5;text-align: center;">TX (MB)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="item" items="${SsidTraffic}"
									varStatus="select1">
									<tr class="reportTable_tr">
										<td style="border: 1px solid #729ea5;text-align: center;">${item[0]}</td>
										<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[1]}</font></td>
										<td style="border: 1px solid #729ea5;text-align: center;"><font color="blue">${item[2]}</font></td>
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