<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device Overview ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/overview.css">
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>


<script type="text/javascript">

var ip;

function update_edgeCoresystemInfo(ip) {
	   this.ip = ip;

	   update_memory();
}
function update_memory(){
	memoryData = [ip];
	   $.ajax({
		   type: "POST",
	       url: "SystemInfo?chart=edgeCoreMemoryView",
		   data: {memoryData:memoryData},
		   success: function(data) {
			   if(data == "SignOut"){
					alert("Is Logout");
					location.href="logout.jsp";
				}
	    	   var memory = data.split(",");
	    	   $("#total").html(memory[0] + " MB");
	    	   $("#used").html(memory[1] + " MB");
	    	   $("#free").html(memory[2] + " MB");
	    	   setTimeout(update_memory, 5000);
	       }
	   });
}

function open_rtchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "realtime_chart.jsp?ip=" + ip + "&ifid=" + document.getElementById("rt_port_select").value + "&portid=" + $("#rt_port_select :selected").text();
	window.open(target,"rtchart1" + $('#rt_port_select').val(),"menubar=1,resizable=1,width=450,height=320");
}
function open_rtchart2(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
    var target = "monitorchart_rxtxbps.jsp?ip=" + ip + "&ifid=" + document.getElementById("rt_port_select").value + "&portid=" + $("#rt_port_select :selected").text();
    window.open(target,"rtchart2" + $('#rt_port_select').val(),"menubar=1,resizable=1,width=450,height=320");
}

function open_htchart4(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "historychart_rxtxoctet_bps.jsp?ip=" + ip + "&ifid=" + document.getElementById("ht_port_select").value + "&portid=" + $("#ht_port_select :selected").text();
	window.open(target,"htchart4" + $('#ht_port_select').val(),"menubar=1,resizable=1,width=950,height=460");
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

    <c:out value="<script>$(document).ready(function() {update_edgeCoresystemInfo('${param.ip}');});</script>" escapeXml="false"></c:out>
	<%-- <c:out value="<script type='text/javascript'>$(document).ready(update_edgeCoresystemInfo('${param.ip}') );</script>" escapeXml="false"></c:out> --%>

<body onload="createFunctionMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>
<%@ include file="theme/Theme1/link_view.jspf"%>

<%-- The Beginning of Page Content --%>

		<%-- <table class="overViewTable_l">
	    <caption>Realtime Chart</caption>
		   <tr>
				<td>
				    <div class="div_bar">RX</div> 
					<iframe width="920" height="185" src="monitorchart_rxallbps.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
					<jsp:include page="/monitorchart_rxallbps.jsp?ip=${param.ip}"></jsp:include>
				</td>
			</tr>
			<tr>
				<td>
				    <div class="div_bar">TX</div> 
					<iframe width="920" height="185" src="monitorchart_txallbps.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
					<jsp:include page="/monitorchart_txallbps.jsp?ip=${param.ip}"></jsp:include>
				</td>
			</tr>
		</table> --%>
		
	    <c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
	    <c:if test="${dev.publicIp == param.ip}">
		<c:if test="${dev.supportEdgeCoreResource}">	
			<table class="overViewTable_l">
	    	<caption>CPU/Memory Utilization Chart</caption>
				<tr>
					<td>
						<div class="div_bar">CPU</div> 
						<iframe width="650" height="225" src="edgecore_cpu_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
					</td>
				</tr>
				<tr>
					<td>
						<div class="div_bar">Memory</div> 
						<iframe width="650" height="225" src="edgecore_memory_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
					</td>
					<td>
		            	 <table>
			            	 <tr>
				            	 <th>Physical memory</th><th></th>
			             	</tr>
			             	<tr>
			                	 <td>Total</td><td><div id="total"></div></td>
			             	</tr>
			            	 <tr>
			                	 <td>Used</td><td><div id="used"></div></td>
			             	</tr>
			             	<tr>
			                	 <td>Free</td><td><div id="free"></div></td>
			             	</tr>
		             	</table>
		         	</td>
				</tr>	
			</table>
		</c:if>
	
	<hr>
	<table class="overViewTable_l">
	    <caption>Chart Select</caption>
			<tr VALIGN=TOP>
				<td>
					<!-- <div class="div_bar">RealTime Chart Select</div><hr> -->
					<table class="overViewTable">
						<caption>RealTime</caption>
						<tr>
							<td style="font-weight: bold;">Chart Type</td>
							<td>
								<div class="chart_setting">
									Port: <select class="port_select" id="rt_port_select">
									<c:forEach begin="1" end="${dev.stackNum}" varStatus="stackId">
									<c:if test="${dev.stackNum > 1}"><optgroup label="Stack ${stackId.index}"></optgroup></c:if>
										<c:forEach var="inf" items="${dev.interfaces}" varStatus="status">
											<c:if test="${inf.ifType == 'eth' && inf.stackId == stackId.index}">
												<option value="${inf.ifIndex}">${inf.portId}</option>
											</c:if>
										</c:forEach>
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
									<c:forEach begin="1" end="${dev.stackNum}" varStatus="stackId">
									<c:if test="${dev.stackNum > 1}"><optgroup label="Stack ${stackId.index}"></optgroup></c:if>
										<c:forEach var="inf" items="${dev.interfaces}" varStatus="status">
											<c:if test="${inf.ifType == 'eth' && inf.stackId == stackId.index}">
												<option value="${inf.ifIndex}">${inf.portId}</option>
											</c:if>
										</c:forEach>
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
				</td>
				<c:if test="${dev.supportHostResource}">
				<td>
				<table class="overViewTable">
					<caption>Host</caption>
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
				</td>
				</c:if>
			</tr>
	</table>
	
	<hr>
	<table class="overViewTable_l">
			<tr>
			<c:if test="${dev.isSupportPoe()}">
				<td>
					<div class="poe_vlan">
						<!-- <div class="div_bar">PoE Status</div> -->
						<jsp:include page="/PoeStatus">
							<jsp:param value="get" name="action" />
							<jsp:param value="global" name="mode" />
							<jsp:param value="${param.ip}" name="ip" />
						</jsp:include>
	
						<table class="overViewTable_b">
							<caption>PoE Status</caption>
							<tr>
								<th colspan="6">Mainpower Status</th>
							</tr>
							<tr>
								<td>PoE Maximum Available Power</td>
								<td>${globalPoeStatus[0].poeMainMaxAvailablePower}Walts</td>
								<td>System Operation Status</td>
								<td>${globalPoeStatus[0].poeMainSysOperStatus}</td>
								<td>PoE Power Consumption</td>
								<td>${globalPoeStatus[0].poeMainPowerConsumption}Walts</td>
							</tr>
						</table>
					</div>
				</td>
			</c:if>
			<c:if test="${dev.isSupportVlan()}">
				<td>
					<div class="poe_vlan">
						<!-- <div class="div_bar">VLAN Status</div> -->
						<jsp:include page="/VlanStatus">
							<jsp:param value="get" name="action" />
							<jsp:param value="global" name="mode" />
							<jsp:param value="${param.ip}" name="ip" />
						</jsp:include>
	
						<table class="overViewTable_b">
							<caption>VLAN Status</caption>
							<tr>
								<th>VLAN ID</th>
								<th>VLAN Name</th>
								<th>Status</th>
							</tr>
							<c:forEach var="item" items="${globalVlanStatus}">
								<%-- <c:if test="${ item.index <= globalVlanStatus.vlanNum }"> --%>
								<%-- <c:if test="${ item.index <= item.vlanNum }"> --%>
									<tr>
										<%-- <td>${item.index}</td> --%>
										<td>${item.vlanNum}</td>
										<td>${item.dot1qVlanStaticName}</td>
										<td>${item.dot1qVlanStaticStatus}</td>
									</tr>
								<%-- </c:if> --%>
							</c:forEach>
						</table>
					</div>
				</td>
			</c:if>
			</tr>
	</table>
	</c:if>
	</c:forEach>
</div>
</body>
</html>
