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
<script>
var hostMemory = [];
var dataset;
var totalPoints = 30;

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
				var y = item.datapoint[1];

				$("#tooltip").html(y + "%")
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
            lineWidth: 1,
            fill: true
        },
        /* points: {
			show: true
		} */
    },
    xaxis: {
    	show: false,
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
            max: 100,
            /* tickSize: 50, */
            tickFormatter: function (v, axis) {
		        if (v % 10 == 0) {
		            return v + "%";
		        } else {
		            return "";
		        }
            },
            autoscaleMargin:0.1,
            //axisLabel: "hostMemory",
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
        backgroundColor:"#fffdf6",
        borderColor:"#999999",
        hoverable: true,
		//clickable: true
        
    }
};

function initData() {
    for (var i = 0; i < totalPoints; i++) {
        var temp = [now += 10000, 0];

        hostMemory.push(temp);
    }
}

function ClearData() {
	var renow=hostMemory[0][0]-10000;
	hostMemory.length=0;
	
	for (var i = 0; i < totalPoints; i++) {
    var temp = [renow += 10000, 0];

    hostMemory.push(temp);
    
	}
	dataset = [        
    { label: "hostMemory", data: hostMemory, lines:{ lineWidth:1.2}, color: "#00FF00" },
    ];
	
	$.plot($("#flot-placeholder1"), dataset, options);
}

$(function() {
	$("#clear").click(function() {
		
		var renow=hostMemory[0][0]-10000;
		hostMemory.length=0;
		
		for (var i = 0; i < totalPoints; i++) {
			var temp = [renow += 10000, 0];
			
			hostMemory.push(temp);
			
        }
		
		dataset = [        
        { label: "hostMemory", data: hostMemory, lines:{ lineWidth:1.2}, color: "#00FF00" },
        ];
		
		$.plot($("#flot-placeholder1"), dataset, options);
		
	});
});

function GetData() {
	portselect = [ip];
	   
    $.ajaxSetup({ cache: false });

    $.ajax({
    	//async: false,
        url: "SystemInfo?chart=hostMemory",
        data:{portselect:portselect},
        Type: 'POST',
        success: update,
    });
}

function update(response) {
	hostMemory.shift();
 
    now += 10000;
    hostMemory.push([now, response]);
	
	dataset = [
               { label: "Memory", data: hostMemory, lines: {  lineWidth: 1 }, color: "#BBFF00" },
               ];

    $.plot($("#flot-placeholder1"), dataset, options);
    setTimeout(GetData, updateInterval*1000);
}

function go_chart(ip, updateInterval) {
	this.ip = ip;
	this.updateInterval = updateInterval;
	initData();

    dataset = [        
        { label: "Memory", data: hostMemory, lines:{ lineWidth:1}, color: "#BBFF00" },
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

<%-- <div style="font-size: 1em; font-weight: bold;" >Data Rate [${param.ip}, port:${param.port}]</div> --%>
<br>
<div id="flot-placeholder1" style="width:600px;height:150px;"></div>

<!-- <p>Time between updates: <input id="updateInterval" type="text" value="" style="text-align: right; width:5em">sec</p> -->

Time between updates:
<select id="timeInterval" size="1">
          <option value="10">10sec</option>
          <option value="30">30sec</option>
          <!-- <option value="45">45sec</option> -->
          <option value="60">1minute</option>
          <!-- <option value="120">2minute</option> -->
</select>

<!-- <button id="clear">clear</button><button id="stop">stop</button><button id="start">start</button> -->

<%-- <p>Port change:
<select id="port_select">
		<c:forEach begin="1" end="28" varStatus="status">
			<option value="${status.index}">${status.index}</option>
		</c:forEach>
</select></p><br> --%>

<c:set var="timeout" value="<%= Config.getRealtimeChartTimeout() %>"/>
<c:out value="<script>$(document).ready(go_chart('${param.ip}', '${timeout}'));</script>" escapeXml="false"></c:out>

</body>
</html>