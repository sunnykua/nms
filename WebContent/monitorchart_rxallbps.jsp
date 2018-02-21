<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Chart</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/flot/jquery.flot.min.js"></script>
<script type="text/javascript" src="theme/flot/jquery.flot.time.min.js"></script>
<script type="text/javascript" src="https://gflot.googlecode.com/svn-history/r154/trunk/flot/jquery.flot.axislabels.js"></script>
<script type="text/javascript" src="theme/Theme1/curvedLines.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
var dataLine1 = [];
var dataLine2 = [];
var dataLine3 = [];
var dataLine4 = [];
var dataLine5 = [];
var dataLine6 = [];
var dataLine7 = [];
var dataLine8 = [];
var dataLine9 = [];
var dataLine10 = [];
var dataLine11 = [];
var dataLine12 = [];
var dataLine13 = [];
var dataLine14 = [];
var dataLine15 = [];
var dataLine16 = [];
var dataLine17 = [];
var dataLine18 = [];
var dataLine19 = [];
var dataLine20 = [];
var dataLine21 = [];
var dataLine22 = [];
var dataLine23 = [];
var dataLine24 = [];
var dataLine25 = [];
var dataLine26 = [];
var dataLine27 = [];
var dataLine28 = [];
var dataset;
var totalPoints = 60;

var now = new Date().getTime()-600000;
//var i=0;
var ip;

dataset = [        
	{ label: "1", data: dataLine1 },
	{ label: "2", data: dataLine2 },
	{ label: "3", data: dataLine3 },
	{ label: "4", data: dataLine4 },
	{ label: "5", data: dataLine5 },
	{ label: "6", data: dataLine6 },
	{ label: "7", data: dataLine7 },
	{ label: "8", data: dataLine8 },
	{ label: "9", data: dataLine9 },
	{ label: "10", data: dataLine10 },
	{ label: "11", data: dataLine11 },
	{ label: "12", data: dataLine12 },
	{ label: "13", data: dataLine13 },
	{ label: "14", data: dataLine14 },
	{ label: "15", data: dataLine15 },
	{ label: "16", data: dataLine16 },
	{ label: "17", data: dataLine17 },
	{ label: "18", data: dataLine18 },
	{ label: "19", data: dataLine19 },
	{ label: "20", data: dataLine20 },
	{ label: "21", data: dataLine21 },
	{ label: "22", data: dataLine22 },
	{ label: "23", data: dataLine23 },
	{ label: "24", data: dataLine24 },
	{ label: "25", data: dataLine25 },
	{ label: "26", data: dataLine26 },
	{ label: "27", data: dataLine27 },
	{ label: "28", data: dataLine28 },
];
var newdataset = [];

var updateInterval;
$(function() {
	$("#timeInterval").val(updateInterval).change(function () {
		var v = $(this).val();
		
			updateInterval = +v;
			
			$(this).val("" + updateInterval);
		
	});
	$("#updateInterval").val(updateInterval).change(function () {
		var v = $(this).val();
		if (v && !isNaN(+v)) {
			updateInterval = +v;
			if (updateInterval < 10) {
				updateInterval = 10;
			} else if (updateInterval > 60) {
				updateInterval = 60;
			}
			$(this).val("" + updateInterval);
		}
	});
	$("<div id='tooltip'></div>").css({
		position: "absolute",
		display: "none",
		border: "1px solid #fdd",
		padding: "2px",
		"background-color": "#fee",
		opacity: 0.80
	}).appendTo("body");

	$("#monitorchart_rxallbps").bind("plothover", function (event, pos, item) {

		/* if ($("#enablePosition:checked").length > 0) {
			var str = "(" + pos.x.toFixed(2) + ", " + pos.y.toFixed(2) + ")";
			$("#hoverdata").text(str);
		} */

		//if ($("#enableTooltip:checked").length > 0) {
			if (item) {
				var y = item.datapoint[1].toFixed(2);

				$("#tooltip").html(item.series.label + " = " + y)
					.css({top: item.pageY+5, left: item.pageX+5})
					.fadeIn(200);
			} else {
				$("#tooltip").hide();
			}
		//}
	});
});
var options = {
    series: {
        lines: {
        	//show: true,
            lineWidth: 0.5
        },
        /* points: {
			show: true
		} */
    },
    xaxis: {
        mode: "time",
        tickSize: [60, "second"],
        tickFormatter: function (v, axis) {
        	var date = new Date(v);
        	
        	
            if (date.getSeconds() % 60 == 0) {
                var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
                var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
                var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

                return hours + ":" + minutes + ":" + seconds;
            } else {
            
                return "";
            }
        },
        //axisLabel: "Time",
        axisLabelUseCanvas: true,
        axisLabelFontSizePixels: 10,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 10
    },
    yaxes: [
        {
            min: 0,
            /* max: 500,
            tickSize: 50, */
            
            autoscaleMargin:0.1,
            axisLabel: "Rx(kbps)",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 10,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 6 
        }, {
            /* max: 100,
            tickSize: 10,
            position: "right",
            axisLabel: "Multicast Broadcast ",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 6  */
        }
    ],
    legend: {
        noColumns: 0,
        backgroundOpacity: 0.5,
        position:"nw"
    },
    grid: {      
        backgroundColor: { colors: ["#fffdf6", "#fffdf6"] },
        borderColor:"#999999",
        hoverable: true,
		//clickable: true
        
    }
};

function initData() {
    for (var i = 0; i < totalPoints; i++) {
        var temp = [now += 10000, 0];

        dataLine1.push(temp);
        dataLine2.push(temp);
        dataLine3.push(temp);
        dataLine4.push(temp);
        dataLine5.push(temp);
        dataLine6.push(temp);
        dataLine7.push(temp);
        dataLine8.push(temp);
        dataLine9.push(temp);
        dataLine10.push(temp);
        dataLine11.push(temp);
        dataLine12.push(temp);
        dataLine13.push(temp);
        dataLine14.push(temp);
        dataLine15.push(temp);
        dataLine16.push(temp);
        dataLine17.push(temp);
        dataLine18.push(temp);
        dataLine19.push(temp);
        dataLine20.push(temp);
        dataLine21.push(temp);
        dataLine22.push(temp);
        dataLine23.push(temp);
        dataLine24.push(temp);
        dataLine25.push(temp);
        dataLine26.push(temp);
        dataLine27.push(temp);
        dataLine28.push(temp);
    }
}

function GetData() {
	portselect = [ip];
	var param = {
			ip: ip,
			chart: 'rx_all_octet_bps',
			action: 'read',
			updateinterval: updateInterval
	};
	
    $.ajaxSetup({ cache: false });

    $.ajax({
    	//async: false,
        url: "Chart",
        data: param,
        Type: 'POST',
        success: update,
        /* error: function () {
        	alert("GetData->ajax:error");
            setTimeout(GetData, updateInterval);
        } */
    });
}

function update(response) {
	var data=response.split(",");
	/* if (data.length != 28) {                               // ***** NOT a good solution, must modify *****
		setTimeout(GetData, updateInterval*1000);
		return;
	} */

	dataLine1.shift();
	dataLine2.shift();
	dataLine3.shift();
	dataLine4.shift();
	dataLine5.shift();
	dataLine6.shift();
	dataLine7.shift();
	dataLine8.shift();
	dataLine9.shift();
	dataLine10.shift();
	dataLine11.shift();
    dataLine12.shift();
    dataLine13.shift();
    dataLine14.shift();
    dataLine15.shift();
    dataLine16.shift();
    dataLine17.shift();
    dataLine18.shift();
    dataLine19.shift();
    dataLine20.shift();
    dataLine21.shift();
    dataLine22.shift();
    dataLine23.shift();
    dataLine24.shift();
    dataLine25.shift();
    dataLine26.shift();
    dataLine27.shift();
    dataLine28.shift();
 
    now += 10000;    // interval
    dataLine1.push([now, data[0]]);
    dataLine2.push([now, data[1]]);
    dataLine3.push([now, data[2]]);
    dataLine4.push([now, data[3]]);
    dataLine5.push([now, data[4]]);
    dataLine6.push([now, data[5]]);
    dataLine7.push([now, data[6]]);
    dataLine8.push([now, data[7]]);
    dataLine9.push([now, data[8]]);
    dataLine10.push([now, data[9]]);
    dataLine11.push([now, data[10]]);
    dataLine12.push([now, data[11]]);
    dataLine13.push([now, data[12]]);
    dataLine14.push([now, data[13]]);
    dataLine15.push([now, data[14]]);
    dataLine16.push([now, data[15]]);
    dataLine17.push([now, data[16]]);
    dataLine18.push([now, data[17]]);
    dataLine19.push([now, data[18]]);
    dataLine20.push([now, data[19]]);
    dataLine21.push([now, data[20]]);
    dataLine22.push([now, data[21]]);
    dataLine23.push([now, data[22]]);
    dataLine24.push([now, data[23]]);
    dataLine25.push([now, data[24]]);
    dataLine26.push([now, data[25]]);
    dataLine27.push([now, data[26]]);
    dataLine28.push([now, data[27]]);    
	
    $.plot($("#monitorchart_rxallbps"), newdataset, options);
    setTimeout(GetData, updateInterval*1000);   
}

function go_chart(ip, updateInterval) {
	this.ip = ip;
	//this.port = port;
	this.updateInterval = updateInterval;
	initData();

	//$.plot($("#monitorchart_txallbps"), dataset, options);
    var i = 0;
   	$.each(dataset, function(key, val) {
   		val.color = i;
   		++i;
   	});

   	// insert checkboxes 
   	var choiceContainer = $("#rxchoices");
   	$.each(dataset, function(key, val) {
   		choiceContainer.append("<tr><td>"+"<input type='checkbox' name='" + key +
   			"' checked='checked' id='id" + key + "'></input>"+ "</td><td>"  +
   			"<label for='id'>"
   			+ "P"+val.label + "</label>" + "</td></tr>");
   	});

   	choiceContainer.find("input").click(plotAccordingToChoices);
   
   	function plotAccordingToChoices() {

   		newdataset = [];

   		choiceContainer.find("input:checked").each(function () {
   			var key = $(this).attr("name");
   			if (key && dataset[key]) {
   				newdataset.push(dataset[key]);
   			}
   		});

   		if (newdataset.length > 0) {
   			$.plot("#monitorchart_rxallbps", newdataset, options);
   		}
   		if (newdataset.length < 2) {
   			choiceContainer.find('input:checked').attr('disabled', true);
   		}else if(newdataset.length >= 2){
   			choiceContainer.find('input:checked').attr('disabled', false);
   		}
   	}

   	plotAccordingToChoices();
   	setTimeout(GetData, updateInterval*1000);

}
</script>
</head>
<style>
#realtime_rx_table {
  border-collapse: collapse;
}
#realtime_rx_table td, th {
  padding: 0;
}
</style>
<body>

<%-- The Beginning of Page Content --%>

<%-- <div style="font-size: 1em; font-weight: bold;" >Rx Data Rate (kbps) [${param.ip}, port:All]</div> 
<br>--%>
<table id="realtime_rx_table">
<tr><td><div id="monitorchart_rxallbps" style="width:1024px;height:190px;"></div></td></tr>
</table>

<!-- <p>Time between updates: <input id="updateInterval" type="text" value="" style="text-align: right; width:5em">sec</p>-->

<table>
<tr>
<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target=".rx">SELECT</button></td>
<td><p>Time between updates:
	<select id="timeInterval" size="1">
	          <option value="10">10sec</option>
	          <!-- <option value="20">20sec</option> -->
	          <option value="30">30sec</option>
	          <!-- <option value="45">45sec</option> -->
	          <option value="60">1minute</option>
	          <option value="120">2minute</option>
	</select></p></td>
</tr>
</table>

<!-- <button id="clear">clear</button><button id="stop">stop</button><button id="start">start</button> -->

<div class="modal fade rx" tabindex="-1" role="dialog" aria-labelledby="rx" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <table id="rxchoices" >
      <tr>
      <th colspan=2>Select at least one port</th>
      <th></th>
      </tr>
      </table>
    </div>
  </div>
</div>

<c:set var="timeout" value="<%= Config.getRealtimeChartTimeout() %>"/>
<c:out value="<script>$(document).ready(go_chart('${param.ip}', '${timeout}'));</script>" escapeXml="false"></c:out>

</body>
</html>
