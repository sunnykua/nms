<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Chart ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/chart_select.css">
<script src="theme/Theme1/check_s.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<script type="text/javascript">
function open_rtchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "realtime_chart.jsp?ip=" + ip + "&port=" + document.getElementById("rt_port_select").value;
	window.open(target,"rtchart1","menubar=1,resizable=1,width=450,height=320");
}
function open_rtchart2(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
    var target = "monitorchart_rxtxbps.jsp?ip=" + ip + "&port=" + document.getElementById("rt_port_select").value;
    window.open(target,"rtchart2","menubar=1,resizable=1,width=450,height=320");
}

function open_htchart4(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "historychart_rxtxoctet_bps.jsp?ip=" + ip + "&port=" + document.getElementById("ht_port_select").value;
	window.open(target,"htchart4","menubar=1,resizable=1,width=950,height=460");
}

function open_hostchart1(ip) {
	var target = "host_cpu_chart.jsp?ip=" + ip;
	window.open(target,"hostchart1","menubar=1,resizable=1,width=650,height=225");
}
function open_hostchart2(ip) {
	var target = "host_memory_chart.jsp?ip=" + ip;
    window.open(target,"hostchart","menubar=1,resizable=1,width=650,height=225");
}
</script>
</head>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createFunctionMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>

<%-- The Beginning of Page Content --%>

    <c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
    <c:if test="${dev.publicIp==param.ip}">
    <table id="realtimechart_table">
			<caption>RealTime Chart Select</caption>
			<tr>
				<td style="font-weight: bold;">Chart Type</td>
				<td>
				<div class="chart_setting">
				Port: <select class="port_select" id="rt_port_select">
						<%-- <c:forEach begin="1" end="28" varStatus="status"> --%>
						<c:forEach begin="1" end="${dev.infNum}" varStatus="status">
							<option value="${status.index}">${status.index}</option>
						</c:forEach>
				</select>
				</div>
				</td>
			<tr>
			
			<tr>
				<td>
					<div class="chart_title">Rx Unicast, Multicast and Broadcast</div>
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onclick="open_rtchart1('${param.ip}')">Show</button>
				</td>
			
			<tr>
				<td>
					<div class="chart_title">Rx Tx Data Rate (kbps)</div>
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onclick="open_rtchart2('${param.ip}')">Show</button>
				</td>
			
			<tr>
		</table>
	<br>
	
	<hr><!-- --------------------------------------------------------------------------------------------------------- -->
	
	<table id="historychart_table">
			<caption>History Chart Select</caption>
			<tr>
				<td style="font-weight: bold;">Chart Type</td>
				<td>
					<div class="chart_setting">
						Port: <select class="port_select" id="ht_port_select">
							<%-- <c:forEach begin="1" end="28" varStatus="status"> --%>
							<c:forEach begin="1" end="${dev.infNum}" varStatus="status">
								<option value="${status.index}">${status.index}</option>
							</c:forEach>
						</select>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="chart_title">Rx Tx Data Rate (bps)</div>
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onclick="open_htchart4('${param.ip}')">Show</button>
				</td>
			</tr>
		</table>
		<hr><!-- --------------------------------------------------------------------------------------------------------- -->
		<c:if test="${dev.supportHostResource}">
		<table id="hostchart_table">
			<caption>Host Chart</caption>
			<tr>
				<td>
					<div class="chart_title">CPU</div>
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onclick="open_hostchart1('${param.ip}')">Show</button>
				</td>
			</tr>
			<tr>
				<td>
					<div class="chart_title">Memory</div>
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onclick="open_hostchart2('${param.ip}')">Show</button>
				</td>
			</tr>
		 </table>
		 </c:if>
	</c:if>
	</c:forEach>
</div>
</body>
</html>