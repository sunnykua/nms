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
<script src="theme/Theme1/check_s.js"></script>
<script>
var rxData = [], txData = [];
var dataset;
var totalPoints = 60;
var now = new Date().getTime()-600000;
var i=0;
var acip;
var apip;
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

	$("#flot-placeholder1").bind("plothover", function (event, pos, item) {

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
        	show: true,
            lineWidth: 1.2
        },
        /* points: {
			show: true
		} */
    },
    xaxis: {
        mode: "time",
        tickSize: [120, "second"],
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
        axisLabelFontSizePixels: 12,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 10
    },
    yaxes: [
        {
            min: 0,
            /* max: 500,
            tickSize: 50, */
            
            autoscaleMargin:0.1,
            //axisLabel: "Unicast",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
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

        rxData.push(temp);
        txData.push(temp);
    }
}

$(function() {
	$("#clear").click(function() {
		
		var renow=rxData[0][0]-10000;
		rxData.length=0;
		txData.length=0;

		for (var i = 0; i < totalPoints; i++) {
	        var temp = [renow += 10000, 0];
	
	        rxData.push(temp);
	        txData.push(temp);

        }
		
		dataset = [        
        { label: "Rx", data: rxData, lines:{ lineWidth:1.2},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#00FF00" },
        { label: "Tx", data: txData, lines: { lineWidth: 1.2},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#FF0000" },
        ];
		
		$.plot($("#flot-placeholder1"), dataset, options);
		
	});
});

function GetData() {
	portselect = [acip, apip];
	var param = {
			acip: acip,
			apip: apip,
			chart: 'aplist_rx_tx_octet_bps',
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
	if(response == "SignOut"){
		alert("Is Logout");
		location.href="logout.jsp";
	}
	var data=response.split(",");
	if (data.length != 2) return;

	rxData.shift();
	txData.shift();
 
    now += 10000; // interval
    rxData.push([now, data[0]]);
    txData.push([now, data[1]]);
	
    dataset = [
               { label: "Rx", data: rxData, lines: {  lineWidth: 1.2 },/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#00FF00" },
               { label: "Tx" , data: txData, lines: {  lineWidth: 1.2},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#FF0000" },
               ];

    $.plot($("#flot-placeholder1"), dataset, options);
    setTimeout(GetData, updateInterval*1000);
}

function go_chart(acip, apip, updateInterval) {
	this.acip = acip;
	this.apip = apip;
	this.updateInterval = updateInterval;
	initData();

    dataset = [        
        { label: "Rx", data: rxData, lines:{ lineWidth:1.2},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#00FF00" },
        { label: "Tx", data: txData, lines: { lineWidth: 1.2},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */ color: "#FF0000" },
    ];

    $.plot($("#flot-placeholder1"), dataset, options);
    setTimeout(GetData, updateInterval*1000);

}
</script>
</head>
<body>

<%-- The Beginning of Page Content --%>

<div style="font-size: 1em; font-weight: bold;" >AP Data Rate (kbps) [acip:${param.acip}, apip:${param.apip}]</div>
<br>
<div id="flot-placeholder1" style="width:400px;height:225px;"></div>

<!-- <p>Time between updates: <input id="updateInterval" type="text" value="" style="text-align: right; width:5em">sec</p> -->

Time between updates:
<select id="timeInterval" size="1">
          <option value="10">10sec</option>
          <option value="30">30sec</option>
          <!-- <option value="45">45sec</option> -->
          <option value="60">1minute</option>
          <option value="120">2minute</option>
</select>

<button id="clear">clear</button><!-- <button id="stop">stop</button><button id="start">start</button> -->

<c:set var="timeout" value="<%= Config.getRealtimeChartTimeout() %>"/>
<c:out value="<script>$(document).ready(go_chart('${param.acip}', '${param.apip}', '${timeout}'));</script>" escapeXml="false"></c:out>

</body>
</html>