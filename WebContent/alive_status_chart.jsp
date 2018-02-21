<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Alive Status Remote：${param.ip} Local：${param.device}</title>

<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxdraw.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxchart.core.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		//console.log('${param.ip}' + ", " + '${param.device}' + ", " + '${param.startTime}' + ", " + '${param.endTime}');

		$.ajax({
			type : "POST",
			url : "Dashboard?method=getLostChart&ip=${param.ip}&device=${param.device}&startTime=${param.startTime}&endTime=${param.endTime}",
			success : function(data1) {
				//console.log(data1);

				var data = [];
				var tmp_time = '';
				var bo;
				$.each(data1[1], function(index, value) {
					console.log(index + ", " + value);

					if (value[4] == "true") {
						bo = 1;
					} else {
						bo = 0;
					}

					//補中間
					/*if (new Date(value[0]) - new Date(tmp_time) > 900000) {
						//console.log(new Date(value[0]) - new Date(tmp_time));

						data.push({
							timestamp : new Date((new Date(value[0]) - 600000).valueOf()),
							value : 0
						});
					}

					tmp_time = value[0];*/

					data.push({
						timestamp : new Date(new Date(value[0]).valueOf()),
						value : bo
					});
				});

				data = data.reverse();

				// prepare jqxChart settings
				var settings = {
					title : "Alive Status Record",
					description : "1：Online、0：Offline",
					enableAnimations : false,
					animationDuration : 1000,
					enableAxisTextAnimation : true,
					showLegend : true,
					padding : {
						left : 5,
						top : 5,
						right : 5,
						bottom : 5
					},
					titlePadding : {
						left : 0,
						top : 0,
						right : 0,
						bottom : 10
					},
					source : data,
					xAxis : {
						dataField : 'timestamp',
						type : 'date',
						baseUnit : 'minute',
						unitInterval : 30,
						formatFunction : function(value) {
							return $.jqx.dataFormat.formatdate(value, "HH:mm:ss", 'en-us');
						},
						gridLines : {
							step : 2
						},
						valuesOnTicks : true,
						labels : {
							angle : -45,
							offset : {
								x : -17,
								y : 0
							}
						}
					},
					colorScheme : 'scheme03',
					seriesGroups : [ {
						type : 'stepline',
						columnsGapPercent : 50,
						alignEndPointsWithIntervals : true,
						valueAxis : {
							minValue : 0,
							maxValue : 1,
							unitInterval : 1,
							title : {
								text : 'Value'
							}
						},
						series : [ {
							dataField : 'value',
							displayText : 'value',
							opacity : 1,
							lineWidth : 2,
							symbolType : 'circle',
							fillColorSymbolSelected : 'white',
							symbolSize : 4
						} ]
					} ]
				};

				// create the chart
				$('#chartContainer').jqxChart(settings);

				// get the chart's instance
				var chart = $('#chartContainer').jqxChart('getInstance');

				//setTimeout(getSysInfo, 5000);
			}
		});
	});
</script>
<body>
	<div id='chartContainer' style="width: 1024px; height: 250px;"></div>
</body>
</html>