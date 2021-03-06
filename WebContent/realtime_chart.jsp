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
var Unicast = [], Multicast = [], Broadcast = [];
var dataset;
var totalPoints = 60;

var now = new Date().getTime()-600000;
var i=0;
var ip;
var port;

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
	
	$("#port_select").val(port).change(function () {
		ClearData();
		var portselect = $(this).val();
		port = +portselect;
		$(this).val("" + port);
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

        Unicast.push(temp);
        Multicast.push(temp);
        Broadcast.push(temp);
    }
}

function ClearData() {
	var renow=Unicast[0][0]-10000;
	Unicast.length=0;
	Multicast.length=0;
	Broadcast.length=0;
	
	for (var i = 0; i < totalPoints; i++) {
    var temp = [renow += 10000, 0];

    Unicast.push(temp);
    Multicast.push(temp);
    Broadcast.push(temp);
    }
	dataset = [        
    { label: "Unicast", data: Unicast, lines:{ lineWidth:1.2}, color: "#00FF00" },
    { label: "Multicast", data: Multicast, lines: { lineWidth: 1.2}, color: "#FF0000" },
    { label: "Broadcast", data: Broadcast, lines: {  lineWidth: 1.2}, color: "#0044FF" },
    ];
	
	$.plot($("#flot-placeholder1"), dataset, options);
}

$(function() {
	$("#clear").click(function() {
		
		var renow=Unicast[0][0]-10000;
		Unicast.length=0;
		Multicast.length=0;
		Broadcast.length=0;
		
		for (var i = 0; i < totalPoints; i++) {
			var temp = [renow += 10000, 0];
			
			Unicast.push(temp);
			Multicast.push(temp);
			Broadcast.push(temp);
        
        }
		
		dataset = [        
        { label: "Unicast", data: Unicast, lines:{ lineWidth:1.2}, color: "#00FF00" },
        { label: "Multicast", data: Multicast, lines: { lineWidth: 1.2}, color: "#FF0000" },
        { label: "Broadcast", data: Broadcast, lines: {  lineWidth: 1.2}, color: "#0044FF" },
        ];
		
		$.plot($("#flot-placeholder1"), dataset, options);
		
	});
});

function GetData() {
	portselect = [ip, port];
	   
    $.ajaxSetup({ cache: false });

    $.ajax({
    	//async: false,
        url: "Chart?chart=chart1&updateinterval=" + updateInterval,
        data:{portselect:portselect},
        Type: 'POST',
        success: update,
        error: function () {
            setTimeout(GetData, updateInterval);
        }
    });
}

function update(response) {
	var data=response.split(",");
	
	
	Unicast.shift();
	Multicast.shift();
    Broadcast.shift();
 
    now += 10000;
    Unicast.push([now, data[0]]);
    Multicast.push([now, data[1]]);
    Broadcast.push([now, data[2]]);
	
	
    dataset = [
               { label: "Unicast", data: Unicast, lines: {  lineWidth: 1.2 }, color: "#00FF00" },
               { label: "Multicast" , data: Multicast, lines: {  lineWidth: 1.2}, color: "#FF0000" },
               { label: "Broadcast" , data: Broadcast, lines: {  lineWidth: 1.2}, color: "#0044FF" },
                  
               ];

    $.plot($("#flot-placeholder1"), dataset, options);
    setTimeout(GetData, updateInterval*1000);
}

function go_chart(ip, port, updateInterval) {
	this.ip = ip;
	this.port = port;
	this.updateInterval = updateInterval;
	initData();

    dataset = [        
        { label: "Unicast", data: Unicast, lines:{ lineWidth:1.2}, color: "#00FF00" },
        { label: "Multicast", data: Multicast, lines: { lineWidth: 1.2}, color: "#FF0000" },
        { label: "Broadcast", data: Broadcast, lines: {  lineWidth: 1.2}, color: "#0044FF" },
    ];

    $.plot($("#flot-placeholder1"), dataset, options);
    setTimeout(GetData, updateInterval*1000);
}
</script>
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

<div style="font-size: 1em; font-weight: bold;" >Data Rate [${param.ip}, port:${param.portid}]</div>
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

<%-- <p>Port change:
<select id="port_select">
		<c:forEach begin="1" end="28" varStatus="status">
			<option value="${status.index}">${status.index}</option>
		</c:forEach>
</select></p><br> --%>

<c:set var="timeout" value="<%= Config.getRealtimeChartTimeout() %>"/>
<c:out value="<script>$(document).ready(go_chart('${param.ip}', '${param.ifid}', '${timeout}'));</script>" escapeXml="false"></c:out>

</body>
</html>