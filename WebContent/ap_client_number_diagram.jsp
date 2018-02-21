<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Real-Time Client Number ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/flot/jquery.flot.min.js"></script>
<script type="text/javascript" src="theme/flot/jquery.flot.time.min.js"></script>
<script type="text/javascript"
	src="https://gflot.googlecode.com/svn-history/r154/trunk/flot/jquery.flot.axislabels.js"></script>
<script type="text/javascript" src="theme/Theme1/curvedLines.js"></script>
<script>
	var ipp = [], ipp2 = [];
	var dataset;
	var totalPoints = 60;

	var now = new Date().getTime() - 600000;
	var i = 0;
	var ip;
	var type;

	//var sendip = ip.spilt(",");

	var updateInterval = 10;
	$(function() {
		$("#timeInterval").val(updateInterval).change(function() {
			var v = $(this).val();

			updateInterval = +v;

			$(this).val("" + updateInterval);

		});
		$("#updateInterval").val(updateInterval).change(function() {
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
			position : "absolute",
			display : "none",
			border : "1px solid #fdd",
			padding : "2px",
			"background-color" : "#fee",
			opacity : 0.80
		}).appendTo("body");

		$("#flot-placeholder1").bind("plothover", function(event, pos, item) {

			/* if ($("#enablePosition:checked").length > 0) {
				var str = "(" + pos.x.toFixed(2) + ", " + pos.y.toFixed(2) + ")";
				$("#hoverdata").text(str);
			} */

			//if ($("#enableTooltip:checked").length > 0) {
			if (item) {
				var y = item.datapoint[1].toFixed(2);

				$("#tooltip").html(item.series.label + " = " + y).css({
					top : item.pageY + 5,
					left : item.pageX + 5
				}).fadeIn(200);
			} else {
				$("#tooltip").hide();
			}
			//}
		});
	});
	var options = {
		series : {
			lines : {
				show : true,
				lineWidth : 1.2
			},
		/* points: {
			show: true
		} */
		},
		xaxis : {
			mode : "time",
			tickSize : [ 120, "second" ],
			tickFormatter : function(v, axis) {
				var date = new Date(v);

				if (date.getSeconds() % 60 == 0) {
					var hours = date.getHours() < 10 ? "0" + date.getHours()
							: date.getHours();
					var minutes = date.getMinutes() < 10 ? "0"
							+ date.getMinutes() : date.getMinutes();
					var seconds = date.getSeconds() < 10 ? "0"
							+ date.getSeconds() : date.getSeconds();

					return hours + ":" + minutes + ":" + seconds;
				} else {

					return "";
				}
			},
			axisLabel : "Time",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 12,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 10
		},
		yaxes : [ {
			min : 0,
			/* max: 500,
			tickSize: 50, */

			autoscaleMargin : 0.1,
			axisLabel : "clients",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 12,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 6
		}, {
		/* max: 100,
		tickSize: 10,
		position: "right",
		axisLabel: "Multicast Broadcast ",
		axisLabelUseCanvas: true,
		axisLabelFontSizePixels: 12,
		axisLabelFontFamily: 'Verdana, Arial',
		axisLabelPadding: 6  */
		} ],
		legend : {
			noColumns : 0,
			position : "nw"
		},
		grid : {
			backgroundColor : {
				colors : [ "#AFFEFF", "#EDF5FF" ]
			},
			borderColor : "#AFFEFF",
			hoverable : true,
		//clickable: true

		}
	};

	function initData() {
		for (var i = 0; i < totalPoints; i++) {
			var temp = [ now += 10000, 0 ];

			ipp.push(temp);
			ipp2.push(temp);
		}
	}

	$(function() {
		$("#clear").click(function() {

			var renow = Unicast[0][0] - 10000;
			Unicast.length = 0;
			Multicast.length = 0;
			Broadcast.length = 0;
			//location.reload();
			//$("#flot-placeholder1").empty();
			//GetData();
			//alert(updateInterval);
			for (var i = 0; i < totalPoints; i++) {
				var temp = [ renow += 10000, 0 ];

				Unicast.push(temp);
				Multicast.push(temp);
				Broadcast.push(temp);
			}
			dataset = [ {
				label : "Unicast",
				data : Unicast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#00FF00"
			}, {
				label : "Multicast",
				data : Multicast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#FF0000"
			}, {
				label : "Broadcast",
				data : Broadcast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#0044FF"
			}, ];

			$.plot($("#flot-placeholder1"), dataset, options);

		});
	});

	function GetData() {
		portselect = [ ip, type ];
		//alert($("#portselect").val());
		$.ajaxSetup({
			cache : false
		});

		$
				.ajax({
					//async: false,
					url : "APRealtimeClientNumberDiagramSelect?ip=${param.ip}&type=anbgn",
					data : {
						portselect : portselect
					},
					Type : 'POST',
					success : update,
					error : function() {
						setTimeout(GetData, updateInterval);
					}
				});
	}

	//var temp;

	function update(response) {
		//alert(response);
		//var unicast=Unicast[0].split(',');	
		//alert(unicast[0]);	
		var data = response.split(",");
		//alert(data[0]);
		//for(var i=0;i<Math.floor(data.length/3);i++){

		ipp.shift();
		ipp2.shift();

		now += 10000;

		temp = [ now, data[0] /* data[i*3] */];
		ipp.push(temp);

		temp = [ now, data[1] /* data[i*3] */];
		ipp2.push(temp);

		ipset = [];

		if (ip != "undefined")
			ipset.push(ip);
		if (type != "undefined")
			ipset.push(type);

		allset = [ {
			label : "an",
			data : ipp,
			lines : {
				lineWidth : 1.2
			},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
			color : "#00FF00"
		}, {
			label : "bgn",
			data : ipp2,
			lines : {
				lineWidth : 1.2
			},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
			color : "#FF0000"
		}, ]

		dataset = [];

		var n = ipset.length;

		for (var i = 0; i < n; i++) {
			dataset.push(allset[i]);
		}

		$.plot($("#flot-placeholder1"), dataset, options);
		var interval = setTimeout(GetData, updateInterval * 1000);

		$("#start").click(function() {
			var renow = Unicast[0][0] - 10000;
			Unicast.length = 0;
			Multicast.length = 0;
			Broadcast.length = 0;
			//location.reload();
			//$("#flot-placeholder1").empty();
			//GetData();
			//alert(updateInterval);
			for (var i = 0; i < totalPoints; i++) {
				var temp = [ renow += 10000, 0 ];

				Unicast.push(temp);
				Multicast.push(temp);
				Broadcast.push(temp);
			}
			dataset = [ {
				label : "Unicast",
				data : Unicast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#00FF00"
			}, {
				label : "Multicast",
				data : Multicast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#FF0000"
			}, {
				label : "Broadcast",
				data : Broadcast,
				lines : {
					lineWidth : 1.2
				},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
				color : "#0044FF"
			}, ];

			$.plot($("#flot-placeholder1"), dataset, options);
			clearTimeout(interval);
			GetData();
		});

		$("#stop").click(function() {
			clearTimeout(interval);
		});
		//}
	}

	function go_chart(ip, type) {

		this.ip = ip;
		this.type = type;
		initData();
		//alert(ip1+ip2);
		//sendip = ip.spilt(",");
		//for(var i=0;i<=sendip.length;i++)
		//alert(sendip[0]);

		ipset = [];

		if (ip != "undefined")
			ipset.push(ip);
		if (type != "undefined")
			ipset.push(type);

		allset = [ {
			label : "an",
			data : ipp,
			lines : {
				lineWidth : 1.2
			},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
			color : "#00FF00"
		}, {
			label : "bgn",
			data : ipp2,
			lines : {
				lineWidth : 1.2
			},/* curvedLines: {apply:true, fit: true, fitPointDist: 0.000001}, */
			color : "#FF0000"
		}, ];

		dataset = [];

		var n = ipset.length;

		for (var i = 0; i < n; i++) {
			dataset.push(allset[i]);
		}

		$.plot($("#flot-placeholder1"), dataset, options);
		setTimeout(GetData, updateInterval * 1000);

	}

	/* $(document).ready(function () {
	 initData();

	 dataset = [        
	 { label: "Unicast:", data: Unicast, lines:{ lineWidth:1.2}, color: "#00FF00" },
	 { label: "Multicast:", data: Multicast, lines: { lineWidth: 1.2}, color: "#FF0000" },
	 { label: "Broadcast:", data: Broadcast, lines: {  lineWidth: 1.2}, color: "#0044FF" },
	
	
	 ];

	 $.plot($("#flot-placeholder1"), dataset, options);
	 setTimeout(GetData, updateInterval);
	 }); */
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

	<div style="font-size: 1em; font-weight: bold;">
		<font color="#0000FF" size="5.5">802.11an & bgn(2.4GHz & 5GHz)</font>&nbsp;
		Real-Time Client Number
	</div>
	<br>
	<div id="flot-placeholder1"
		style="width: 600px; height: 350px; margin: 0 auto"></div>

	<!-- <p>Time between updates: <input id="updateInterval" type="text" value="" style="text-align: right; width:5em">sec</p> -->

	<!-- <p>Time between updates:
<select id="timeInterval" size="1">
          <option value="10">10sec</option>
          <option value="20">20sec</option>
          <option value="30">30sec</option>
          <option value="45">45sec</option>
          <option value="60">1minute</option>
          <option value="120">2minute</option>
</select></p><br>

<button id="clear">clear</button><button id="stop">stop</button> -->
	<!-- <button id="start">start</button> -->

	<c:out
		value="<script>$(document).ready(go_chart('${param.ip}', '${param.type}'));</script>"
		escapeXml="false"></c:out>


</body>
</html>