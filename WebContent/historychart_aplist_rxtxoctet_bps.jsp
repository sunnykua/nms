<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>History - ${param.ip}, ${param.port}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script language="javascript" type="text/javascript" src="theme/jqPlot/jquery.jqplot.js"></script>
<script language="javascript" type="text/javascript" src="theme/jqPlot/jquery.jqplot.dateAxisRenderer.min.js"></script>
<script language="javascript" type="text/javascript" src="theme/jqPlot/jqplot.pointLabels.min.js"></script>
<script language="javascript" type="text/javascript" src="theme/jqPlot/jqplot.barRenderer.min.js"></script>
<script language="javascript" type="text/javascript" src="theme/jqPlot/jqplot.categoryAxisRenderer.min.js"></script>
<link rel="stylesheet" type="text/css" href="theme/jqPlot/jquery.jqplot.css" />
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script src="theme/jQueryUI/jquery-ui.js"></script>
<script src="theme/jQueryUI/jquery.blockUI.js"></script>
<script src="theme/Theme1/check_s.js"></script>
<style>
.ui-datepicker {font-size:11px;}
.item_title {
	font-weight: bold;
}
.item_content {
	margin: 10px 0;
}
</style>
</head>

<body>

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

<table><tr>
	<td><div id="chart2" class="chart" style="height:350px;width:600px;"></div></td>
	<td><ul>
		<li><div class="item_title">Select one single day:</div>
			<div class="item_content">
				<div id="datepicker"></div>
			</div>
		</li>
		<li><div class="item_title">Select the period of time:</div>
			<div class="item_content">
				<button id=day onclick="go_chart(acIp, ip,'day','%H:%M')">Last Day</button>
				<button id=week onclick="go_chart(acIp, ip,'week','%m/%d')">Last Week</button>
				<button id=month onclick="go_chart(acIp, ip,'month','%m/%d')">Last Month</button>
			</div>
		</li>
		<li><div class="item_title">Select the time range:</div>
			<div class="item_content">
				<table>
					<tr><td><label for="from">From:</label></td><td><input type="text" id="from" name="from"></td></tr>
					<tr><td><label for="to">To:</label></td><td><input type="text" id="to" name="to"></td><td><button id="choose_date">Apply</button></td></tr>
				</table>
			</div>
		</li>
	</ul></td>
</tr></table>
<script>

function monthly (){
	$( "#datepicker" ).datepicker({
		minDate: new Date(y1, m1 - 1, d1),
		maxDate: new Date(y2, m2 - 1, d2),//"+0",
		dateFormat: 'yy-mm-dd',
		defaultDate: "+1w",
		onSelect: function(dateText, inst) {
			go_chart(acIp, ip,'monthly','%H:%M',dateText);
		}});
};
$(function () {
	   $("#choose_date").click(function() {
		   //if($("#from").val() && $("#to").val()!=""){
		   //go_chart(ip, port,'choose_date');
		   //}
		   if($("#from").val()==""){
			   alert("Please select startdate");
		   }else if($("#to").val()==""){
			   alert("Please select enddate");
		   }else
			   go_chart(acIp, ip,'choose_date','%m/%d');
		   
		   /* if($("#from").val()=="" && $("#to").val()==""){
			   alert("Please select startdate and enddate");
		   } */
	   });
});
function choose_date (){
	$( "#from" ).datepicker({
		minDate: new Date(y1, m1 - 1, d1),
		maxDate: new Date(y2, m2 - 1, d2),//"+0",
		dateFormat: 'yy-mm-dd',
		defaultDate: "+1w",
		changeMonth: true,
		numberOfMonths: 1,
		onClose: function( selectedDate ) {
			$( "#to" ).datepicker( "option", new Date(y1, m1 - 1, d1), selectedDate );
		}
	});
	$( "#to" ).datepicker({
		minDate: new Date(y1, m1 - 1, d1),
		maxDate: new Date(y2, m2 - 1, d2),//"+0",
		dateFormat: 'yy-mm-dd',
		efaultDate: "+1w",
		changeMonth: true,
		numberOfMonths: 1,
		onClose: function( selectedDate ) {
			//$( "#from" ).datepicker( "option", "maxDate", selectedDate );
		}
	});
};
var ip,port;
function go_chart(acIp, ip, time_select,time_format,dateText) {
	this.acIp = acIp;//get acIp value
	this.ip = ip;//get ip value
	//alert(dateText);
	$("#chart2").block({ 
        message: '<img src="images/712.GIF" />', 
        css: {
            border: 'none',
            padding: '1px',
            backgroundColor: '#fff',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '1px',
            opacity: 0.5,
            color: '#fff'
                         },
        overlayCSS:  { 
            backgroundColor: '#fff', 
            opacity:         0.5, 
            cursor:          'wait' 
                         }, 
        });
	sendData = {
			time_select: time_select,
			chart_type: 'rx_tx_octet_bps_history',
			acIp: acIp,
			ip: ip,
			startdate: $("#from").val(),
			enddate: $("#to").val(),
			dateText: dateText };
	$.ajax({
 		   type: "POST",
 		   url: "HistoryApListChart",
 		   dataType: "JSON",
		   data: sendData,
		   success: function(recvData) {
			   setTimeout($.unblockUI);
			   /* $.each(recvData, function(date, value) {
				   y1=value[2],m1=value[3],d1=value[4],y2=value[5],m2=value[6],d2=value[7];
				   monthly (y1,m1,d1,y2,m2,d2);
				   choose_date (y1,m1,d1,y2,m2,d2);
			   }); */
			   var line1 = [];
			   $.each(recvData, function(date, value) {
				   line1.push([date,value[0]]);
			   });
			   var line2 = [];
			   $.each(recvData, function(date, value) {
				   line2.push([date,value[1]]);
			   });
			   
			   var plot=$.jqplot('chart2', [line1,line2], {
			      title:'Rx Tx Data Rate (kbps)', 
			      /* gridPadding:{right:35}, */
			      axes:{
			        xaxis:{
			        	//autoscale: true,
			          renderer:$.jqplot.DateAxisRenderer, 
			          tickOptions:{formatString:time_format},
			          //min:'May 30, 2008', 
			          //tickInterval:'3 hour'
			        },
			        yaxis:{
			        	min:0,
			        	//max:100,
			        	//autoscale: true
			        }
			      },
			      series:[
			              {
			            	  label:'Rx',
			            	  lineWidth:1, 
			            	  //markerOptions:{style:'square'}
			              },
			              {
			            	  label:'Tx',
			            	  lineWidth:1, 
			                  //markerOptions: { style:'dimaond' }
			              }
			              ],
			      legend: {
			                  show: true,
			                  placement: 'outsideGrid'
			              },
			      seriesDefaults: {
			                  showMarker: false,
			                  //pointLabels: { show:true } 
			              },
			      axesDefaults: {
			                  //showTicks: false,
			                  //showTickMarks: false       
			              },
			  });
			   plot.replot();
		   }
	});
		
}
function init_chart(acIp, ip, time_select, time_format, dateText) {
	var dataSet = {
			ip: ip,
			acIp: acIp,
            action: 'apList_calendar'
	};
	$.ajax({
        type: "POST",
        url: "HistoryApListChart",
        data: dataSet,
        dataType: "JSON",
        success: function(value) {
        	y1=value[0],m1=value[1],d1=value[2],y2=value[3],m2=value[4],d2=value[5];
        	//alert("start=" + y1 + m1 + d1 + "\nend:" + y2 + m2 + d2);
            
        	monthly (y1,m1,d1,y2,m2,d2);
            choose_date (y1,m1,d1,y2,m2,d2);
        }
	});
	
	go_chart(acIp, ip, time_select, time_format, dateText);
}

</script>
<!-- <button id="CallChart">Call Chart data</button> -->
<c:out value="<script>$(document).ready(init_chart('${param.acip}', '${param.apip}', 'week','%m/%d'));</script>" escapeXml="false"></c:out>
</body>
</html>