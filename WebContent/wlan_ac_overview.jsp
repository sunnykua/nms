<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AC Overview ${param.ip}</title>
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
<script type="text/javascript" src="https://www.google.com/jsapi"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>

<script>
function open_rtchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
    var target = "monitorchart_rxtxbps.jsp?ip=" + ip + "&ifid=" + document.getElementById("rt_port_select").value + "&portid=" + $("#rt_port_select :selected").text();
    window.open(target,"rtchart1" + $('#rt_port_select').val(),"menubar=1,resizable=1,width=450,height=320");
}
function open_htchart1(/* link,  */ip) {
	//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
	
	var target = "historychart_rxtxoctet_bps.jsp?ip=" + ip + "&ifid=" + document.getElementById("ht_port_select").value + "&portid=" + $("#ht_port_select :selected").text();
	window.open(target,"htchart1" + $('#ht_port_select').val(),"menubar=1,resizable=1,width=950,height=460");
}


var ip;

function update(ip) {
	   this.ip = ip;
	   update_memory();
	   diskChart();
}
function update_memory(){
	memoryData = [ip];
	   $.ajax({
		   type: "POST",
		   url: "SystemInfo?chart=hostMemoryView",
		   data: {memoryData:memoryData},
		   success: function(data) {
			   if(data == "SignOut"){
					alert("Is Logout");
					location.href="logout.jsp";
				}
	    	   var memory = data.split(",");
	    	   $("#total").html(memory[0] + " MB");
	    	   $("#cached").html(memory[1] + " MB");
	    	   $("#used").html(memory[2] + " MB");
	    	   $("#free").html(memory[3] + " MB");
	    	   setTimeout(update_memory, 5000);
	       }
	   });
}
google.load("visualization", "1", {packages:["corechart"]});

function diskChart(){
	diskChartData = [ip];
	   $.ajax({
		   type: "POST",
		   url: "SystemInfo?chart=hostDisk",
		   data: {diskChartData:diskChartData},
		   success: function(response) {
			   var disk = response.split(",");
			   $("#diskTotal").html(disk[1] + " GB");
	    	   $("#diskUsed").html(disk[2] + " GB");
	    	   $("#diskFree").html(disk[3] + " GB");
			   
			   google.setOnLoadCallback(drawChart(response));
			   
	       },
	   });
}

function drawChart(response) {
	var disk = response.split(",");
	used = parseFloat(disk[2]);
	free = parseFloat(disk[3]);
    var data = google.visualization.arrayToDataTable([
      ['Disk', 'use'],
      ['Used',  used],
      ['Free', free],
    ]);

    var options = {
      //title: 'My Daily Activities',
      is3D: true,
      //legend: 'none',
      //pieSliceText: 'label',
      pieStartAngle: 100,
      //pieHole: 0.2,
      'width':650,
      'height':225,
      colors:['#9999FF','#66FF66'],
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
    chart.draw(data, options);
  }
</script>
</head>
<body onload="createAcMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>
<%@ include file="theme/Theme1/link_view.jspf"%>

<%-- The Beginning of Page Content --%>

<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
<c:if test="${dev.publicIp==param.ip}">
	<table class="overViewTable_l">
	    <caption>Realtime Chart</caption>
		   <tr>
				<td>
					<div class="div_bar">CPU</div> 
					<iframe width="650" height="225" src="host_cpu_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
				</td>
			</tr>
			<tr>
				<td>
					<div class="div_bar">Memory</div> 
					<iframe width="650" height="225" src="host_memory_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
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
			                 <td>Cached</td><td><div id="cached"></div></td>
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
	<hr>
	<table class="overViewTable_l">
	    <caption>Disk Chart</caption>
			<tr>
		         <td>
		             <div class="div_bar">Disk</div>
		             <div id="piechart_3d" style="width: 650px; height: 225px;"></div>
		         </td>
		         <td>
		             <table>
			             <tr>
				             <th>Disk Status</th><th></th>
			             </tr>
			             <tr>
			                 <td>Total</td><td><div id="diskTotal"></div></td>
			             </tr>
			             <tr>
			                 <td>Used</td><td><div id="diskUsed"></div></td>
			             </tr>
			             <tr>
			                 <td>Free</td><td><div id="diskFree"></div></td>
			             </tr>
		             </table>
		         </td>
		    </tr>
	</table>
	<c:out value="<script>$(document).ready(update('${param.ip}'));</script>" escapeXml="false"></c:out>
	<hr>
	<table class="overViewTable_l">
			<tr>
				<td>
					<div class="div_bar">RealTime Chart Select</div><hr>
					<table class="overViewTable">
						<caption>RealTime Chart Select</caption>
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
					<div class="div_bar">History Chart Select</div><hr>
					<table class="overViewTable">
						<caption>History Chart Select</caption>
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