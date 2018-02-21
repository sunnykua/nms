<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Overview ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/link_view.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/overview.css">
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>

<script>
function open_rtchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
    var target = "fw_monitorchart_rxtxbps.jsp?ip=" + ip + "&ifid=" + document.getElementById("rt_port_select").value + "&portid=" + $("#rt_port_select :selected").text();
    window.open(target,"rtchart1" + $('#rt_port_select').val(),"menubar=1,resizable=1,width=450,height=320");
}
function open_htchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "historychart_rxtxoctet_bps.jsp?ip=" + ip + "&ifid=" + document.getElementById("ht_port_select").value + "&portid=" + $("#ht_port_select :selected").text();
	window.open(target,"htchart1" + $('#ht_port_select').val(),"menubar=1,resizable=1,width=950,height=460");
}
</script>
</head>
<body onload="createFwMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>
<%@ include file="theme/Theme1/link_view.jspf"%>

<%-- The Beginning of Page Content --%>

<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
<c:if test="${dev.publicIp==param.ip}">
	<%-- <table class="overViewTable_l">
	    <caption>Realtime Chart</caption>
		   <tr>
				<td>
					<div class="div_bar">RX</div> 
					<iframe width="920" height="220" src="fw_monitorchart_rxallbps.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
				</td>
			</tr>
			<tr>
				<td>
					<div class="div_bar">TX</div> 
					<iframe width="920" height="220" src="fw_monitorchart_txallbps.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
				</td>
			</tr>
			<tr>
				<td>
					<div class="div_bar">One Port of RX TX</div> 
					<iframe width="920" height="220" src="fw_monitorchart_rxtxbps.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
				</td>
			</tr>		
	</table>
	<hr> --%>
	<table class="overViewTable_l">
	    <caption>Chart Select</caption>
			<tr>
				<td>
					<!-- <div class="div_bar">RealTime Chart Select</div><hr> -->
					<table class="overViewTable">
						<caption>RealTime</caption>
						<tr>
							<td style="font-weight: bold;">Chart Type</td>
							<td>
							<div class="chart_setting">
							Port: <select class="port_select" id="rt_port_select">
									<c:forEach var="inf" items="${dev.interfaces}" varStatus="status">
										<c:if test="${inf.ifType == 'eth'}">
											<option value="${inf.ifIndex}">${inf.portId}</option>
										</c:if>
									</c:forEach>
								  </select>
							</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="chart_title">Rx Tx Data Rate (kbps)</div>
							</td>
							<td>
								<button class="btn btn-primary btn-xs" onclick="open_rtchart1('${param.ip}')">Show</button>
							</td>
						
						</tr>
					</table>
				</td>
				<td>
					<!-- <div class="div_bar">History Chart Select</div><hr> -->
					<table class="overViewTable">
						<caption>History</caption>
						<tr>
							<td style="font-weight: bold;">Chart Type</td>
							<td>
								<div class="chart_setting">
									Port: <select class="port_select" id="ht_port_select">
											<c:forEach var="inf" items="${dev.interfaces}" varStatus="status">
												<c:if test="${inf.ifType == 'eth'}">
													<option value="${inf.ifIndex}">${inf.portId}</option>
												</c:if>
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
								<button class="btn btn-primary btn-xs" onclick="open_htchart1('${param.ip}')">Show</button>
							</td>
						</tr>
					</table>
				</td>
			</tr>
	</table>
</c:if>
</c:forEach>
</div>
</body>
</html>