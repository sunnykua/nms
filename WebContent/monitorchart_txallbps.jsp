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
var txdataLine1 = [];
var txdataLine2 = [];
var txdataLine3 = [];
var txdataLine4 = [];
var txdataLine5 = [];
var txdataLine6 = [];
var txdataLine7 = [];
var txdataLine8 = [];
var txdataLine9 = [];
var txdataLine10 = [];
var txdataLine11 = [];
var txdataLine12 = [];
var txdataLine13 = [];
var txdataLine14 = [];
var txdataLine15 = [];
var txdataLine16 = [];
var txdataLine17 = [];
var txdataLine18 = [];
var txdataLine19 = [];
var txdataLine20 = [];
var txdataLine21 = [];
var txdataLine22 = [];
var txdataLine23 = [];
var txdataLine24 = [];
var txdataLine25 = [];
var txdataLine26 = [];
var txdataLine27 = [];
var txdataLine28 = [];
var txdataset;
var txtotalPoints = 60;

var txnow = new Date().getTime()-600000;
//var i=0;
var txip;

txdataset = [        
	{ label: "1", data: txdataLine1 },
	{ label: "2", data: txdataLine2 },
	{ label: "3", data: txdataLine3 },
	{ label: "4", data: txdataLine4 },
	{ label: "5", data: txdataLine5 },
	{ label: "6", data: txdataLine6 },
	{ label: "7", data: txdataLine7 },
	{ label: "8", data: txdataLine8 },
	{ label: "9", data: txdataLine9 },
	{ label: "10", data: txdataLine10 },
	{ label: "11", data: txdataLine11 },
	{ label: "12", data: txdataLine12 },
	{ label: "13", data: txdataLine13 },
	{ label: "14", data: txdataLine14 },
	{ label: "15", data: txdataLine15 },
	{ label: "16", data: txdataLine16 },
	{ label: "17", data: txdataLine17 },
	{ label: "18", data: txdataLine18 },
	{ label: "19", data: txdataLine19 },
	{ label: "20", data: txdataLine20 },
	{ label: "21", data: txdataLine21 },
	{ label: "22", data: txdataLine22 },
	{ label: "23", data: txdataLine23 },
	{ label: "24", data: txdataLine24 },
	{ label: "25", data: txdataLine25 },
	{ label: "26", data: txdataLine26 },
	{ label: "27", data: txdataLine27 },
	{ label: "28", data: txdataLine28 },
];
var txnewdataset = [];

var txupdateInterval;
$(function() {
	$("#timeInterval_tx").val(txupdateInterval).change(function () {
		var v = $(this).val();
		
			txupdateInterval = +v;
			
			$(this).val("" + txupdateInterval);
		
	});
	$("#updateInterval").val(txupdateInterval).change(function () {
		var v = $(this).val();
		if (v && !isNaN(+v)) {
			txupdateInterval = +v;
			if (txupdateInterval < 10) {
				txupdateInterval = 10;
			} else if (txupdateInterval > 60) {
				txupdateInterval = 60;
			}
			$(this).val("" + txupdateInterval);
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

	$("#monitorchart_txallbps").bind("plothover", function (event, pos, item) {

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
var txoptions = {
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
            axisLabel: "Tx(kbps)",
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

function txinitData() {
    for (var i = 0; i < txtotalPoints; i++) {
        var temp = [txnow += 10000, 0];

        txdataLine1.push(temp);
        txdataLine2.push(temp);
        txdataLine3.push(temp);
        txdataLine4.push(temp);
        txdataLine5.push(temp);
        txdataLine6.push(temp);
        txdataLine7.push(temp);
        txdataLine8.push(temp);
        txdataLine9.push(temp);
        txdataLine10.push(temp);
        txdataLine11.push(temp);
        txdataLine12.push(temp);
        txdataLine13.push(temp);
        txdataLine14.push(temp);
        txdataLine15.push(temp);
        txdataLine16.push(temp);
        txdataLine17.push(temp);
        txdataLine18.push(temp);
        txdataLine19.push(temp);
        txdataLine20.push(temp);
        txdataLine21.push(temp);
        txdataLine22.push(temp);
        txdataLine23.push(temp);
        txdataLine24.push(temp);
        txdataLine25.push(temp);
        txdataLine26.push(temp);
        txdataLine27.push(temp);
        txdataLine28.push(temp);
    }
}



function txGetData() {
	portselect = [txip];
	var param = {
			ip: txip,
			chart: 'tx_all_octet_bps',
			action: 'read',
			updateinterval: txupdateInterval
	};
	
    $.ajaxSetup({ cache: false });

    $.ajax({
    	//async: false,
        url: "Chart",
        data: param,
        Type: 'POST',
        success: txupdate,
        /* error: function () {
        	alert("GetData->ajax:error");
            setTimeout(GetData, updateInterval);
        } */
    });
}

function txupdate(response) {
	var data=response.split(",");
	/* if (data.length != 28) {                               // ***** NOT a good solution, must modify *****
		setTimeout(GetData, updateInterval*1000);
		return;
	} */

	txdataLine1.shift();
	txdataLine2.shift();
	txdataLine3.shift();
	txdataLine4.shift();
	txdataLine5.shift();
	txdataLine6.shift();
	txdataLine7.shift();
	txdataLine8.shift();
	txdataLine9.shift();
	txdataLine10.shift();
	txdataLine11.shift();
    txdataLine12.shift();
    txdataLine13.shift();
    txdataLine14.shift();
    txdataLine15.shift();
    txdataLine16.shift();
    txdataLine17.shift();
    txdataLine18.shift();
    txdataLine19.shift();
    txdataLine20.shift();
    txdataLine21.shift();
    txdataLine22.shift();
    txdataLine23.shift();
    txdataLine24.shift();
    txdataLine25.shift();
    txdataLine26.shift();
    txdataLine27.shift();
    txdataLine28.shift();
 
    txnow += 10000;  // interval
    txdataLine1.push([txnow, data[0]]);
    txdataLine2.push([txnow, data[1]]);
    txdataLine3.push([txnow, data[2]]);
    txdataLine4.push([txnow, data[3]]);
    txdataLine5.push([txnow, data[4]]);
    txdataLine6.push([txnow, data[5]]);
    txdataLine7.push([txnow, data[6]]);
    txdataLine8.push([txnow, data[7]]);
    txdataLine9.push([txnow, data[8]]);
    txdataLine10.push([txnow, data[9]]);
    txdataLine11.push([txnow, data[10]]);
    txdataLine12.push([txnow, data[11]]);
    txdataLine13.push([txnow, data[12]]);
    txdataLine14.push([txnow, data[13]]);
    txdataLine15.push([txnow, data[14]]);
    txdataLine16.push([txnow, data[15]]);
    txdataLine17.push([txnow, data[16]]);
    txdataLine18.push([txnow, data[17]]);
    txdataLine19.push([txnow, data[18]]);
    txdataLine20.push([txnow, data[19]]);
    txdataLine21.push([txnow, data[20]]);
    txdataLine22.push([txnow, data[21]]);
    txdataLine23.push([txnow, data[22]]);
    txdataLine24.push([txnow, data[23]]);
    txdataLine25.push([txnow, data[24]]);
    txdataLine26.push([txnow, data[25]]);
    txdataLine27.push([txnow, data[26]]);
    txdataLine28.push([txnow, data[27]]);    
	
    $.plot($("#monitorchart_txallbps"), txnewdataset, txoptions);
    setTimeout(txGetData, txupdateInterval*1000);
}

function txgo_chart(txip, txupdateInterval) {
	this.txip = txip;
	//this.port = port;
	this.txupdateInterval = txupdateInterval;
	txinitData();

    //$.plot($("#monitorchart_txallbps"), dataset, options);
    var i = 0;
   	$.each(txdataset, function(key, val) {
   		val.color = i;
   		++i;
   	});

   	// insert checkboxes 
   	var choiceContainer = $("#txchoices");
   	$.each(txdataset, function(key, val) {
   		choiceContainer.append("<tr><td>"+"<input type='checkbox' name='" + key +
   			"' checked='checked' id='id" + key + "'></input>" + "</td><td>" +
   			"<label for='id'>"
   			+ "P"+val.label + "</label>"+ "</td></tr>");
   	});

   	choiceContainer.find("input").click(plotAccordingToChoices);
   
   	function plotAccordingToChoices() {

   		txnewdataset = [];

   		choiceContainer.find("input:checked").each(function () {
   			var key = $(this).attr("name");
   			if (key && txdataset[key]) {
   				txnewdataset.push(txdataset[key]);
   			}
   		});

   		if (txnewdataset.length > 0) {
   			$.plot("#monitorchart_txallbps", txnewdataset, txoptions);
   		}
   		if (txnewdataset.length < 2) {
   			choiceContainer.find('input:checked').attr('disabled', true);
   		}else if(txnewdataset.length >= 2){
   			choiceContainer.find('input:checked').attr('disabled', false);
   		}
   	}

   	plotAccordingToChoices();
   	setTimeout(txGetData, txupdateInterval*1000);

}
</script>
</head>
<style>
#realtime_tx_table {
  border-collapse: collapse;
}
#realtime_tx_table td, th {
  padding: 0;
}
</style>
<body>

<%-- The Beginning of Page Content --%>

<%-- <div style="font-size: 1em; font-weight: bold;" >Rx Data Rate (kbps) [${param.ip}, port:All]</div> 
<br>--%>
<table id="realtime_tx_table">
<tr><td><div id="monitorchart_txallbps" style="width:1024px;height:190px;"></div></td></tr>
</table>

<!-- <p>Time between updates: <input id="updateInterval" type="text" value="" style="text-align: right; width:5em">sec</p>-->

<table>
<tr>
<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target=".tx">SELECT</button></td>
<td><p>Time between updates:
	<select id="timeInterval_tx" size="1">
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

<div class="modal fade tx" tabindex="-1" role="dialog" aria-labelledby="tx" aria-hidden="true">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <table id="txchoices" >
      <tr>
      <th colspan=2>Select at least one port</th>
      <th></th>
      </tr>
      </table>
    </div>
  </div>
</div>

<c:set var="timeout" value="<%= Config.getRealtimeChartTimeout() %>"/>
<c:out value="<script>$(document).ready(txgo_chart('${param.ip}', '${timeout}'));</script>" escapeXml="false"></c:out>

</body>
</html>
