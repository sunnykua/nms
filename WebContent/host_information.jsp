<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Host Information</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/host_information.css">
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
</head>
<script>
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
	    	   setTimeout(update_memory, 2000);
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
	used = parseInt(disk[0]);
	free = 100 - parseInt(disk[0]);
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
      colors:['blue','green'],
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart_3d'));
    chart.draw(data, options);
  }


</script>
<%-- <%@ include file="theme/Theme1/menu_bar.jspf"%> --%>
<body onload="createFunctionMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/SystemInfo">
		<jsp:param value="${param.ip}" name="ip"/>
		<jsp:param value="hostView" name="chart"/>
	</jsp:include>
	
	<table id="hostTable">
	<caption>Host chart</caption>
	    <tr>
			<td>
				<div id="div_bar">CPU</div>
			</td>
		</tr>
	    <tr>
	         <td><iframe width="650" height="225" src="host_cpu_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
	             <%-- <jsp:include page="/host_cpu_chart.jsp?ip=${param.ip}"></jsp:include> --%>
	         </td>
	    </tr>
	    <tr>
			<td>
				<div id="div_bar">Memory</div>
			</td>
		</tr>
	    <tr>
	         <td><iframe width="650" height="225" src="host_memory_chart.jsp?ip=${param.ip}" frameborder="0" style=" border:none"></iframe>
	             <%-- <jsp:include page="/host_memory_chart.jsp?ip=${param.ip}"></jsp:include> --%>
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
	    <tr>
	         <td>
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
	
	<table id="host_Table">
		<tr>
			<!-- <th>storageType&nbsp;</th> -->
			<th>storageDescr&nbsp;</th>
			<th>storageSize&nbsp;</th>
			<th>storageUsed&nbsp;</th>
			<th>storageIndex&nbsp;</th>
		</tr>
		<c:forEach var="item" items="${HostInfor.items}">
			<tr>
				<td>${item.storageType}</td>
				<td>${item.storageDescr}</td>
				<td>${item.storageSize}</td>
				<td>${item.storageUsed}</td>
				<td>${item.storageIndex}</td>
			</tr>
		</c:forEach>
		
	</table>
	<%-- <table id="host_Table">
	    <tr>
	        <th>processorLoad&nbsp;</th>
			<th>processorIndex&nbsp;</th>
	    </tr>
	    <c:forEach var="item" items="${HostInfor.items1}">
			<tr>
				<td>${item.processorLoad}</td>
				<td>${item.processorIndex}</td>
			<tr>
		</c:forEach>
	</table> --%>
	<c:out value="<script>$(document).ready(update('${param.ip}'));</script>" escapeXml="false"></c:out>
</div>
</body>
</html>