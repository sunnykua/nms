<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dashboard ${param.address}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/report.css">
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>

<link rel="stylesheet" type="text/css" href="theme/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="theme/easyui/themes/icon.css">
<script type="text/javascript" src="theme/easyui/jquery.easyui.min.js"></script>

<script type="text/javascript" src="theme/easyui/jquery.portal.js"></script>

<link rel="stylesheet" href="theme/jqwidgets/styles/jqx.base.css" type="text/css" />
<script type="text/javascript" src="theme/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxdraw.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxchart.core.js"></script>
<script type="text/javascript" src="theme/jqwidgets/jqxdata.js"></script>

<script src="theme/Highcharts-4.1.5/highcharts.js"></script>
<script src="theme/Highcharts-4.1.5/highcharts-3d.js"></script>

<script src="theme/jQuery-tinyMap/jquery.tinyMap.js"></script>

</head>
<style>
.bar {
	border: 1px solid #ccc;
	-webkit-border-radius: 6px;
	border-radius: 6px;
	margin: 1px 11px;
	width: auto;
	height: 20px;
	text-indent: 10px;
	background: #808080 url(images/greenbar.jpg) 0px 0px no-repeat;
	color: #fff;
}

.reportTable_tr:nth-child(even) {
	background-color: #E0E0FF;
}

.reportTable_tr:nth-child(odd) {
	background-color: #FFFFFF;
}

.title {
	font-size: 16px;
	font-weight: bold;
	padding: 20px 10px;
	background: #eee;
	overflow: hidden;
	border-bottom: 1px solid #ccc;
}

.t-list {
	padding: 5px;
}

#dev_explain {
	margin: auto;
	width: 650px;
	font-family: Helvetica;
}

#dev_explain_table th {
	color: #191970;
	float: left;
	font-size: 18px;
	margin-top: 15px;
}

#dev_explain_table td {
	border-bottom: 1px solid #729ea5;
	font-size: 14px;
}

.panel-body {
	background: #FFFFFF;
}
</style>
<style type="text/css">
.chart-inner-text1 {
	fill: #00BAFF;
	color: #00BAFF;
	font-size: 20px;
	font-family: Verdana;
}

.chart-inner-text2 {
	fill: #009933;
	color: #009933;
	font-size: 20px;
	font-family: Verdana;
}

.chart-inner-text3 {
	fill: #FF6666;
	color: #FF6666;
	font-size: 20px;
	font-family: Verdana;
}

.report_table td {
	font-size: 16px;
	text-align: left;
}

.report_table th {
	text-align: left;
}

#Map {
	width: 100%;
	height: 200px
}
</style>

<script type="text/javascript">
	$(function() {
		$('#pp').portal({
			border : false,
			fit : false
		});

		getLocation();
		getSysInfo();
		getDevices();
		getHost();
		getLost();
		getErrorLog();
	});

	function add() {
		for (var i = 0; i < 2; i++) {
			var p = $('<div/>').appendTo('body');
			p.panel({
				title : 'Title' + i,
				content : '<div style="padding:5px;">Content' + (i + 1) + '</div>',
				height : 100,
				closable : false,
				collapsible : true
			});
			$('#pp').portal('add', {
				panel : p,
				columnIndex : i
			});
		}
		$('#pp').portal('resize');
	}

	function remove() {
		$('#pp').portal('remove', $('#pgrid'));
		$('#pp').portal('resize');
	}

	function getLocation() {
		$("#getLocationTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			url : "Dashboard?method=getLocation&address=${param.address}",
			success : function(data) {
				console.log(data.split("|")[0]);
				$("#getLocationTime").html(data.split("|")[0] + "&nbsp;&nbsp;");

				$('#Location').empty();

				console.log(data.split("|")[1]);
				if (data.split("|")[1] === undefined || data.split("|")[1] == "") {
					$('#Location').html("No Address.");
				} else {
					$('#Location').append('<div id="Map"></div>');

					var map = $('#Map'), m = {};

					map.tinyMap({
						'center' : data.split("|")[1],
						'zoom' : 14,
						'marker' : [ {
							'addr' : data.split("|")[1],
							'text' : data.split("|")[1]
						} ],
						'event' : {
							'idle' : function() {
								window.setTimeout(function() {
									// 取得 tinyMap instance
									m = map.data('tinyMap');

									// 取得 markers 及其 infoWindow
									var marker = m._markers[0] || {}, infoWindow = marker.infoWindow || {};

									if ('function' === typeof infoWindow.open) {
										// 開啟 infoWindow
										infoWindow.open(m.map, marker);
									}
								}, 500);
							}
						}
					});
				}
			}
		});
	}

	function getSysInfo() {
		$("#getSysInfoTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			url : "Dashboard?method=getSysInfo&address=${param.address}",
			success : function(data) {
				$("#getSysInfoTime").html(data.split("|")[0] + "&nbsp;&nbsp;");
				$("#sysDescr").html("&nbsp;&nbsp;" + data.split("|")[1]);
				$("#sysObjectId").html("&nbsp;&nbsp;" + data.split("|")[2]);
				$("#sysUpTime").html("&nbsp;&nbsp;" + data.split("|")[3]);
				$("#sysContact").html("&nbsp;&nbsp;" + data.split("|")[4]);
				$("#sysName").html("&nbsp;&nbsp;" + data.split("|")[5]);
				$("#sysLocation").html("&nbsp;&nbsp;" + data.split("|")[6]);

				//setTimeout(getSysInfo, 5000);
			}
		});
	}

	function getDevices() {
		$("#getDevicesTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			url : "Dashboard?method=getDevices&address=${param.address}",
			success : function(data) {
				$("#getDevicesTime").html(data.split("|")[0] + "&nbsp;&nbsp;");

				var tmp = new Array();
				if (parseInt(data.split("|")[4], 10) != 0)
					tmp.push([ 'L2 Switch (' + parseInt(data.split("|")[4], 10) + ')', parseInt(data.split("|")[4], 10) ]);
				if (parseInt(data.split("|")[5], 10) != 0)
					tmp.push([ 'L3 Switch (' + parseInt(data.split("|")[5], 10) + ')', parseInt(data.split("|")[5], 10) ]);
				if (parseInt(data.split("|")[6], 10) != 0)
					tmp.push([ 'Firewall (' + parseInt(data.split("|")[6], 10) + ')', parseInt(data.split("|")[6], 10) ]);
				if (parseInt(data.split("|")[7], 10) != 0)
					tmp.push([ 'AC (' + parseInt(data.split("|")[7], 10) + ')', parseInt(data.split("|")[7], 10) ]);
				if (parseInt(data.split("|")[8], 10) != 0)
					tmp.push([ 'AP (' + parseInt(data.split("|")[8], 10) + ')', parseInt(data.split("|")[8], 10) ]);
				if (parseInt(data.split("|")[9], 10) != 0)
					tmp.push([ 'Server (' + parseInt(data.split("|")[9], 10) + ')', parseInt(data.split("|")[9], 10) ]);
				if (parseInt(data.split("|")[10], 10) != 0)
					tmp.push([ 'NMS (' + parseInt(data.split("|")[10], 10) + ')', parseInt(data.split("|")[10], 10) ]);
				if (parseInt(data.split("|")[11], 10) != 0)
					tmp.push([ 'PC (' + parseInt(data.split("|")[11], 10) + ')', parseInt(data.split("|")[11], 10) ]);
				if (parseInt(data.split("|")[12], 10) != 0)
					tmp.push([ 'Internet (' + parseInt(data.split("|")[12], 10) + ')', parseInt(data.split("|")[12], 10) ]);
				if (parseInt(data.split("|")[13], 10) != 0)
					tmp.push([ 'Other (' + parseInt(data.split("|")[13], 10) + ')', parseInt(data.split("|")[13], 10) ]);
				if (parseInt(data.split("|")[14], 10) != 0)
					tmp.push([ 'MGV Chief Server (' + parseInt(data.split("|")[14], 10) + ')', parseInt(data.split("|")[14], 10) ]);
				if (parseInt(data.split("|")[15], 10) != 0)
					tmp.push([ 'MGV Command Server (' + parseInt(data.split("|")[15], 10) + ')', parseInt(data.split("|")[15], 10) ]);
				if (parseInt(data.split("|")[16], 10) != 0)
					tmp.push([ 'MGV Player (' + parseInt(data.split("|")[16], 10) + ')', parseInt(data.split("|")[16], 10) ]);

				//console.log(tmp);

				$('#container').highcharts({
					chart : {
						type : 'pie',
						options3d : {
							enabled : true,
							alpha : 45,
							beta : 0
						}
					},
					title : {
						text : '<b>Total：' + data.split("|")[1] + '</b>'
					},
					tooltip : {
						pointFormat : '<b>{point.percentage:.1f}%</b>'
					},
					plotOptions : {
						pie : {
							allowPointSelect : true,
							cursor : 'pointer',
							depth : 35,
							dataLabels : {
								enabled : true,
								format : '{point.name}'
							}
						}
					},
					series : [ {
						type : 'pie',
						name : '',
						data : tmp
					/*
										[ [ 'L2 Switch (' + parseInt(data.split("|")[4], 10) + ')', parseInt(data.split("|")[4], 10) ],
										[ 'L3 Switch (' + parseInt(data.split("|")[5], 10) + ')', parseInt(data.split("|")[5], 10) ],
										[ 'Firewall (' + parseInt(data.split("|")[6], 10) + ')', parseInt(data.split("|")[6], 10) ],
										[ 'AC (' + parseInt(data.split("|")[7], 10) + ')', parseInt(data.split("|")[7], 10) ],
										[ 'AP (' + parseInt(data.split("|")[8], 10) + ')', parseInt(data.split("|")[8], 10) ],
										[ 'Server (' + parseInt(data.split("|")[9], 10) + ')', parseInt(data.split("|")[9], 10) ],
										[ 'NMS (' + parseInt(data.split("|")[10], 10) + ')', parseInt(data.split("|")[10], 10) ],
										[ 'PC (' + parseInt(data.split("|")[11], 10) + ')', parseInt(data.split("|")[11], 10) ],
										[ 'Internet (' + parseInt(data.split("|")[12], 10) + ')', parseInt(data.split("|")[12], 10) ],
										[ 'Other (' + parseInt(data.split("|")[13], 10) + ')', parseInt(data.split("|")[13], 10) ] ]
					 */
					} ]
				});

				$('#Devices').empty();

				if (parseInt(data.split("|")[2], 10) != 0) {
					$("#Devices").append('<span style="color: #333333; font-size: 16px; fill: #333333;"><span id="Online"></span><br> <span id="OnlineList"></span></span>');
					$("#Online").html('&nbsp;&nbsp;<b>Online：' + data.split("|")[2] + '</b>');
					$("#OnlineList").html('&nbsp;&nbsp;' + data.split("|")[17]);
				}

				if (parseInt(data.split("|")[3], 10) != 0) {
					if (parseInt(data.split("|")[2], 10) != 0) {
						$("#Devices").append('<br><br>');
					}
					$("#Devices").append('<span style="color: #333333; font-size: 16px; fill: #333333;"><span id="Offline"></span><br> <span id="OfflineList"></span></span>');
					$("#Offline").html('&nbsp;&nbsp;<b>Offline：' + data.split("|")[3] + '</b>');
					$("#OfflineList").html('&nbsp;&nbsp;' + data.split("|")[18]);
				}

				//setTimeout(getDevices, 5000);
			}
		});
	}

	function getHost() {
		$("#getHostTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			url : "Dashboard?method=getHost&address=${param.address}",
			success : function(data) {
				//console.log(data);

				//$("#cpuUsage").html(data.split(",")[0] + " %");
				//$("#memoryUsage").html(data.split(",")[1] + " %");
				//$("#diskUsage").html(data.split(",")[2] + " %");
				$("#getHostTime").html(data.split(",")[0] + "&nbsp;&nbsp;");

				$('#Host').empty();

				if (data.split(",").length == 4) {
					$('#Host').append('<div style="display: inline-block;"><div id=\'chartContainer1\' style="width: 140px; height: 170px;"></div></div><div style="display: inline-block;"><div id=\'chartContainer2\' style="width: 140px; height: 170px;"></div></div><div style="display: inline-block;"><div id=\'chartContainer3\' style="width: 140px; height: 170px;"></div></div>');
					displayClusterMetrics(data.split(",")[1], data.split(",")[2], data.split(",")[3]);
				} else {
					$('#Host').append('No Data.');
				}

				//setTimeout(getHost, 5000);
			}
		});
	}

	function displayClusterMetrics(cpu, memory, disk) {
		var metrics = [ {
			name : 'CPU<br>Usage ' + cpu + '%',
			value : cpu,
			max : 100
		}, {
			name : 'Memory<br>Usage ' + memory + '%',
			value : memory,
			max : 100
		}, {
			name : 'Disk<br>Usage ' + disk + '%',
			value : disk,
			max : 100
		} ];

		for (var i = 0; i < metrics.length; i++) {

			var data = [];

			data.push({
				text : 'Available',
				value : metrics[i].max - metrics[i].value + '%'
			}); // current
			data.push({
				text : 'Used',
				value : metrics[i].value + '%'
			}); // remaining

			var settings = {
				title : metrics[i].name,
				description : '',
				enableAnimations : true,
				showLegend : false,
				showBorderLine : false,
				backgroundColor : '#FFFFFF',
				padding : {
					left : 0,
					top : 0,
					right : 0,
					bottom : 0
				},
				titlePadding : {
					left : 0,
					top : 0,
					right : 0,
					bottom : 0
				},
				source : data,
				showToolTips : true,
				seriesGroups : [ {
					type : 'donut',
					useGradientColors : false,
					series : [ {
						showLabels : false,
						enableSelection : true,
						displayText : 'text',
						dataField : 'value',
						labelRadius : 120,
						initialAngle : 90,
						radius : 60,
						innerRadius : 50,
						showDelay : 100,
						hideDelay : 100,
						centerOffset : 0
					} ]
				} ]
			};

			var selector = '#chartContainer' + (i + 1).toString();

			//console.log(metrics[i]);
			var valueText = metrics[i].value.toString() + '%';

			if (i == 0) {

				settings.drawBefore = function(renderer, rect) {
					sz = renderer.measureText(valueText, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});

					renderer.text(valueText, rect.x + (rect.width - sz.width) / 2, rect.y + (rect.height / 2) + 10, 0, 0, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});
				};

				$(selector).jqxChart(settings);
				$(selector).jqxChart('addColorScheme', 'customColorScheme', [ '#EDE6E7', '#00BAFF' ]);
				$(selector).jqxChart({
					colorScheme : 'customColorScheme'
				});
			}

			if (i == 1) {

				settings.drawBefore = function(renderer, rect) {
					sz = renderer.measureText(valueText, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});

					renderer.text(valueText, rect.x + (rect.width - sz.width) / 2, rect.y + (rect.height / 2) + 10, 0, 0, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});
				};
				$(selector).jqxChart(settings);
				$(selector).jqxChart('addColorScheme', 'customColorScheme', [ '#EDE6E7', '#009933' ]);
				$(selector).jqxChart({
					colorScheme : 'customColorScheme'
				});
			}

			if (i == 2) {

				settings.drawBefore = function(renderer, rect) {
					sz = renderer.measureText(valueText, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});

					renderer.text(valueText, rect.x + (rect.width - sz.width) / 2, rect.y + (rect.height / 2) + 10, 0, 0, 0, {
						'class' : 'chart-inner-text' + (i + 1)
					});
				};
				$(selector).jqxChart(settings);
				$(selector).jqxChart('addColorScheme', 'customColorScheme', [ '#EDE6E7', '#FF6666' ]);
				$(selector).jqxChart({
					colorScheme : 'customColorScheme'
				});
			}
		}
	}

	$.fn.bars = function(opts) {
		var set = $.extend({
			'max' : 100
		}, $('.bar'));
		return this.each(function() {
			var $this = $(this);
			var barW = $this.width();
			var val = parseFloat($this.text().replace(',', '.'));
			var ratio = (val / set.max * barW);

			$this.stop().animate({
				backgroundPosition : ratio + 'px'
			}, 600);

		});
	};

	function getLost() {
		$("#getLostTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			dataType : "JSON",
			url : "Dashboard?method=getLost&address=${param.address}",
			success : function(data) {
				//console.log(data);
				//console.log(data.length);
				//console.log(data[0][0] + "," + data[0][1] + "," + data[0][2]);

				$("#getLostTime").html(data[0][0] + "&nbsp;&nbsp;");

				$('#Lost').empty();

				if (data.length == 1) {
					$('#Lost').append('<tr><td><font size="3">No Data.</font></td></tr>');
				}

				if (data.length > 1) {
					$('#Lost').append('<tr class="reportTable_tr"><th width="45"><font size="3">No.</font></th><th width="80"><font size="3">Type</font></th><th width="150"><font size="3">Address</font></th><th><font size="3" color="#808080">Offline</font></th><th width="50"><font size="3">Chart</font></th></tr>');
				}

				$.each(data, function(index, value) {
					//console.log(index + "," + value);

					if (index > 0) {
						$('#Lost').append('<tr onmouseover="this.style.backgroundColor=\'#ffffa2\'" onMouseOut="this.style.backgroundColor=\'\'"><td>' + index + '</td><td>' + value[0] + '</td><td>' + value[1] + '</td><td><div class="bar">' + value[2] + '%</div></td><td><a href="javascript:void(0)" onclick="window.open(\'alive_status_chart.jsp?address=${param.address}&device=' + value[1] + '&startTime=' + data[0][1] + '&endTime=' + data[0][2] + '\',\'htchart1\',\'menubar=1,resizable=1,width=1074,height=300\')"><img border="0" src="theme/easyui/themes/icons/large_chart.png" width="17" height="20"></a></td></tr>');
					}
				});

				$('.bar').bars();

				//setTimeout(getLost, 5000);
			}
		});
	}

	function getErrorLog() {
		$("#getErrorLogTime").html("Processing, please wait ...");

		$.ajax({
			type : "POST",
			dataType : "JSON",
			url : "Dashboard?method=getErrorLog&address=${param.address}",
			success : function(data) {
				//console.log(data);

				$("#getErrorLogTime").html(data[0] + "&nbsp;&nbsp;");

				$('#ErrorLog').empty();

				if (data.length == 1) {
					$('#ErrorLog').append('<tr><td><font size="3">No Data.</font></td></tr>');
				}

				$.each(data, function(index, value) {
					//console.log(index + "," + value);

					if (index > 0) {
						$('#ErrorLog').append('<tr class="reportTable_tr" onmouseover="this.style.backgroundColor=\'#ffffa2\'" onMouseOut="this.style.backgroundColor=\'\'"><td><font size="3"><font color="#FF0000"><b>．</b></font>' + value + '</font></td></tr>');
					}
				});

				//setTimeout(getErrorLog, 5000);
			}
		});
	}
</script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createNmsMenu('${param.address}')">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<div region="center" border="false">
			<div id="pp">
				<div>
					<div title="Location Map" collapsible="true" style="height: auto;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getLocation();
						}
					}]">
						<div id="getLocationTime" align="right"></div>
						<div id="Location"></div>
					</div>
					<div title="System Information" collapsible="true" style="height: auto;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getSysInfo();
						}
					}]">
						<div id="getSysInfoTime" align="right"></div>
						<table border="0" width="100%" cellspacing="0" cellpadding="0" class="report_table" style="word-break: break-all">
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Description</td>
								<td><div id="sysDescr"></div></td>
							</tr>
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Object ID</td>
								<td><div id="sysObjectId"></div></td>
							</tr>
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Up Time</td>
								<td><div id="sysUpTime"></div></td>
							</tr>
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Contact</td>
								<td><div id="sysContact"></div></td>
							</tr>
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Name</td>
								<td><div id="sysName"></div></td>
							</tr>
							<tr class="reportTable_tr" onmouseover="this.style.backgroundColor='#ffffa2'" onMouseOut="this.style.backgroundColor=''">
								<td width="150">&nbsp;&nbsp;Location</td>
								<td><div id="sysLocation"></div></td>
							</tr>
						</table>
					</div>
					<div title="Devices" collapsible="true" style="height: auto;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getDevices();
						}
					}]">
						<div id="getDevicesTime" align="right"></div>
						<div id="Devices" align="left" style="word-break: break-all"></div>
						<br>
						<div id="container" style="height: 350px;"></div>
					</div>
				</div>
				<div>
					<div title="System Resources" collapsible="true" style="height: auto;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getHost();
						}
					}]">
						<div id="getHostTime" align="right"></div>
						<div style="display: inline-block;">
							<div id='chartContainer1' style="width: 140px; height: 170px;"></div>
						</div>

						<div style="display: inline-block;">
							<div id='chartContainer2' style="width: 140px; height: 170px;"></div>
						</div>

						<div style="display: inline-block;">
							<div id='chartContainer3' style="width: 140px; height: 170px;"></div>
						</div>
					</div>
					<div title="Devices Alive Status (Last 24 Hours)" collapsible="true" style="height: 250px;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getLost();
						}
					}]">
						<div id="getLostTime" align="right"></div>
						<table id="Lost" border="0" width="100%" cellspacing="0" cellpadding="0" class="report_table" style="word-break: break-all">
						</table>
					</div>
					<div title="Alert Messages (Last 50)" collapsible="true" style="height: 250px;" align="center" data-options="
					tools:[{
						iconCls:'icon-reload',
						handler:function(){
							getErrorLog();
						}
					}]">
						<div id="getErrorLogTime" align="right"></div>
						<table id="ErrorLog" border="0" width="100%" cellspacing="0" cellpadding="0" class="report_table" style="word-break: break-all">
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>