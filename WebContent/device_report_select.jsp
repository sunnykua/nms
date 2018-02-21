<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device Report</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/report.css">
<script type="text/javascript" src="theme/jqPlot/jquery.jqplot.js"></script>
<script type="text/javascript" src="theme/jqPlot/jquery.jqplot.dateAxisRenderer.min.js"></script>
<script type="text/javascript" src="theme/jqPlot/jqplot.pointLabels.min.js"></script>
<script type="text/javascript" src="theme/jqPlot/jqplot.barRenderer.min.js"></script>
<script type="text/javascript" src="theme/jqPlot/jqplot.categoryAxisRenderer.min.js"></script>
<link rel="stylesheet" type="text/css" href="theme/jqPlot/jquery.jqplot.css" />
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script type="text/javascript" src="theme/jQueryUI/jquery.blockUI.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="theme/tablesorter/jquery.tablesorter.js"></script>
<script type="text/javascript" src="theme/tablesorter/parser-network.js"></script>
<script>
function monthly (){
	$( "#datepicker" ).datepicker({
		minDate: new Date(y1, m1 - 1, d1),
		maxDate: new Date(y2, m2 - 1, d2),//"+0",
		dateFormat: 'yy-mm-dd',
		defaultDate: "+1w",
		onSelect: function(dateText, inst) {
			open_windows('monthly', dateText);
		}});
};

function init_chart() {
	var dataSet = {
            action: 'calendar'
	};
	$.ajax({
        type: "POST",
        url: "DeviceReport",
        data: dataSet,
        dataType: "JSON",
        success: function(value) {
        	y1=value[0],m1=value[1],d1=value[2],y2=value[3],m2=value[4],d2=value[5];
        	//alert("start=" + y1 + m1 + d1 + "\nend:" + y2 + m2 + d2);
            
        	monthly (y1,m1,d1,y2,m2,d2);
        }
	});
}

function open_windows(timeSelect, dateText) {
	var ip_select = [];
	$("input[name='reportDevice']:checked").each(function() {
		ip_select.push($(this).val());
	});
	//alert(ip_select + " " + dateText);
	
	if(ip_select.length < 1){
		alert("Please select at least one of the devices");
	}
	else {
		window.open("dataflow_report.jsp?ip=" + ip_select + "&timeSelect=" + timeSelect + "&dateText=" + dateText, "fullscreen,scrollbars");
	}
}

$(document).ready(function() { 
    $("[id^=report]").tablesorter({
    	sortList: [[1,0]]
    });
});

$(function () {
	$("#selectAllReportDevice").click(function(event) {
        if(this.checked) {
            $("input[name='reportDevice']").each(function() {
                this.checked = true;             
            });
        }else{
            $("input[name='reportDevice']").each(function() {
                this.checked = false;                      
            });         
        }
    });
});

$(function () {
	$("input[name='reportDevice']").click(function() {
		if(!this.checked) {
			$('input[name=selectAllReportDevice]').attr('checked', false);
		}
	});
});

$(function () {
	$("#dailyAccountSelect").click(function(event) {
        $("input[name='dailyCheck']").each(function() {
            this.checked = false;                      
        });
    });
});

$(function () {
	$("#weeklyAccountSelect").click(function(event) {
        $("input[name='weeklyCheck']").each(function() {
            this.checked = false;                      
        });
    });
});

$(function () {
	$("#monthlyAccountSelect").click(function(event) {
        $("input[name='monthlyCheck']").each(function() {
            this.checked = false;                      
        });
    });
});

$(function () {
	$("#dailyAccountApply").click(function() {
		var email = "";
		$("input[name='dailyCheck']:checked").each(function() {
			email += $(this).val() + ",";
		});
		$("#dailyAccountText").val(email);
		dailyReportMailApply();
	});
});

$(function () {
	$("#weeklyAccountApply").click(function() {
		var email = "";
		
		$("input[name='weeklyCheck']:checked").each(function() {
			email += $(this).val() + ",";
		});
		
		$("#weeklyAccountText").val(email);
		weeklyReportMailApply();
	});
});

$(function () {
	$("#monthlyAccountApply").click(function() {
		var email = "";
		
		$("input[name='monthlyCheck']:checked").each(function() {
			email += $(this).val() + ",";
		});
		
		$("#monthlyAccountText").val(email);
		monthlyReportMailApply();
	});
});

function dailyReportMailApply(){
	var sendData = {
			dailyAccountText : $("#dailyAccountText").val()
	};
	$.ajax({
		   type: "POST",
		   url: "DeviceReport?action=setReportMailAddr",
	       data: sendData,
	       success: function(data) {
	    	   //alert(data);
	       }
	});
}

function weeklyReportMailApply(){
	var sendData = {
			weeklyAccountText : $("#weeklyAccountText").val()
	};
	$.ajax({
		   type: "POST",
		   url: "DeviceReport?action=setReportMailAddr",
	       data: sendData,
	       success: function(data) {
	    	   //alert(data);
	       }
	});
}

function monthlyReportMailApply(){
	var sendData = {
			monthlyAccountText : $("#monthlyAccountText").val()
	};
	$.ajax({
		   type: "POST",
		   url: "DeviceReport?action=setReportMailAddr",
	       data: sendData,
	       success: function(data) {
	    	   //alert(data);
	       }
	});
}

$(function () {
   $("#daily_report_send").click(function() {
	   var sendData = {
			   timeSelect : "daily"
	   };
	   if (confirm("Are you sure to send daily mail?")) {
		   $.ajax({
		   		   type: "POST",
		   		   url: "DeviceReport?action=reportSend",
		   		   data: sendData,
			       success: function(data) {
			    	   //alert(data);
		    	},
	       });
	   }
   });
});

$(function () {
   $("#weekly_report_send").click(function() {
	   var sendData = {
			   timeSelect : "weekly"
	   };
	   if (confirm("Are you sure to send weekly mail?")) {
		   $.ajax({
		   		   type: "POST",
		   		   url: "DeviceReport?action=reportSend",
		   		   data: sendData,
			       success: function(data) {
			    	   //alert(data);
		    	},
	       });
	   }
   });
});

$(function () {
   $("#monthly_report_send").click(function() {
	   var sendData = {
			   timeSelect : "monthly"
	   };
	   if (confirm("Are you sure to send weekly mail?")) {
		   $.ajax({
		   		   type: "POST",
		   		   url: "DeviceReport?action=reportSend",
		   		   data: sendData,
			       success: function(data) {
			    	   //alert(data);
		    	},
	       });
	   }
   });
});
</script>
<style>
.ui-datepicker {font-size:11px;}
</style>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div id="area" style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>
<%-- The Beginning of Page Content --%>
	<jsp:include page="/Account">
		<jsp:param value="accountItems" name="type" />
	</jsp:include>
	<jsp:include page="/DeviceReport">
		<jsp:param value="getReportMailAddr" name="action" />
	</jsp:include>
<br>
	<div class="reportTitle">Online Report</div>
	<div class="reportOptionDiv1">
	<table>
		<tr>
			<td>
			<ul>
				<li><div class="item_title">Select one or more of device and select day at right.</div>
				</li>
			</ul>
			<div style="width:700px;height:250px;overflow:auto;border: 1px solid #729ea5;">
			
			<table id="report" class="report_table">
				<thead>
				<tr class="reportTable_th">
					<th><input type="checkbox" id="selectAllReportDevice" name="selectAllReportDevice"></th><th style="width:100px">IP</th><th style="width:580px">Alias Name</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
				<c:if test="${dev.snmpSupport > 0 && dev.sysObjectId != '1.3.6.1.4.1.3742.10.5901.1'}">
					<tr class="reportTable_tr">
						<td><input type="checkbox" name="reportDevice" value="${dev.publicIp}"></td>
						<td>${dev.publicIp}</td>
						<td>${dev.aliasName}</td>
					</tr>
				</c:if>
				</c:forEach>
				</tbody>
			</table>
			</div>
			</td>
			<td><ul>
				<li><div class="item_title">Select one single day:</div>
					<div class="item_content">
						<div id="datepicker"></div>
					</div>
				</li>
				<li><div class="item_title">Select the period of time:</div>
					<div class="item_content">
						<button class="btn btn-primary btn-xs" id=day onclick="open_windows('day')">Last 24 hours</button>
						<button class="btn btn-primary btn-xs" id=week onclick="open_windows('week')">Last 7 days</button>
						<button class="btn btn-primary btn-xs" id=last_week onclick="open_windows('last_week')">Last Week</button>
						
					</div>
					<div>
						<button class="btn btn-primary btn-xs" id=month onclick="open_windows('month')">Last Month</button>
					</div>
				</li>
			</ul></td>
		</tr>
	</table>
	</div>
	<br>

<c:out value="<script>$(document).ready(init_chart());</script>" escapeXml="false"></c:out>
	<div class="reportTitle">Email Report</div>
	<div class="reportOptionDiv2">
	<table>
		<tr>
			<td><div class="item_title"> (1) Daily Report - The report will be sending at 8:00 am everyday</div></td>
		</tr>
		<tr>
			<td colspan="3">(select account)</td>
		</tr>
		<tr>
			<td><input type="text" style="width:500px;" id="dailyAccountText" value="<%= Config.getReportDailyMail() %>" readonly></td>
			<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target=".dailyAccountSelect" id="dailyAccountSelect">Account</button></td>
			<!-- <td><button class="btn btn-primary btn-xs" id="dailyReportMailApply">Save</button></td> -->
			<td><button class="btn btn-primary btn-xs" id="daily_report_send">Immediately send mail</button></td>
		</tr>
	</table>
	<table>
		<tr>
			<td><div class="item_title"> (2) Weekly Report - The report will be sending at 8:00 am on Sunday</div></td>
		</tr>
		<tr>
			<td colspan="3">(select account)</td>
		</tr>
		<tr>
			<td><input type="text" style="width:500px;" id="weeklyAccountText" value="<%= Config.getReportWeeklyMail() %>" readonly></td>
			<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target=".weeklyAccountSelect" id="weeklyAccountSelect">Account</button></td>
			<!-- <td><button class="btn btn-primary btn-xs" id="weeklyReportMailApply">Save</button></td> -->
			<td><button class="btn btn-primary btn-xs" id="weekly_report_send">Immediately send mail</button></td>
		</tr>
	</table>
	<table>
		<tr>
			<td colspan="3"><div class="item_title"> (3) Monthly Report - The report will be sending at 8:00 am on first day of month.</div></td>
		</tr>
		<tr>
			<td colspan="3">(select account)</td>
		</tr>
		<tr>
			<td><input type="text" style="width:500px;" id="monthlyAccountText" value="<%= Config.getReportMonthlyMail() %>" readonly></td>
			<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target=".monthlyAccountSelect" id="monthlyAccountSelect">Account</button></td>
			<!-- <td><button class="btn btn-primary btn-xs" id="weeklyReportMailApply">Save</button></td> -->
			<td><button class="btn btn-primary btn-xs" id="monthly_report_send">Immediately send mail</button></td>
		</tr>
	</table>
	<br>
	<div class="modal fade dailyAccountSelect" tabindex="-1" role="dialog" aria-labelledby="accountSelect" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			    <div class="modal-content">
			    <div style="width:250px;height:100px;overflow:auto;border: 1px solid #729ea5; margin:auto;">
			    	<table>
			    		<c:forEach var="acc" items="${accountItems}" varStatus="status">
			    		<tr>
			    			<td><input type="checkbox" value="${acc.name}" name="dailyCheck"></td><td>${acc.name}</td>
			    		</tr>
			    		</c:forEach>
			    	</table>
			    </div>
			    <div><button class="btn btn-primary btn-xs" id="dailyAccountApply" data-dismiss="modal">Select</button></div>
			    <br>
			    </div>
		</div>
	</div>
	<div class="modal fade weeklyAccountSelect" tabindex="-1" role="dialog" aria-labelledby="weeklyAccountSelect" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			    <div class="modal-content">
			    <div style="width:250px;height:100px;overflow:auto;border: 1px solid #729ea5; margin:auto;">
			    	<table>
			    		<c:forEach var="acc" items="${accountItems}" varStatus="status">
			    		<tr>
			    			<td><input type="checkbox" value="${acc.name}" name="weeklyCheck"></td><td>${acc.name}</td>
			    		</tr>
			    		</c:forEach>
			    	</table>
			    </div>
			    <div><button class="btn btn-primary btn-xs" id="weeklyAccountApply" data-dismiss="modal">Select</button></div>
			    <br>
			    </div>
		</div>
	</div>
	<div class="modal fade monthlyAccountSelect" tabindex="-1" role="dialog" aria-labelledby="monthlyAccountSelect" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			    <div class="modal-content">
			    <div style="width:250px;height:100px;overflow:auto;border: 1px solid #729ea5; margin:auto;">
			    	<table>
			    		<c:forEach var="acc" items="${accountItems}" varStatus="status">
			    		<tr>
			    			<td><input type="checkbox" value="${acc.name}" name="monthlyCheck"></td><td>${acc.name}</td>
			    		</tr>
			    		</c:forEach>
			    	</table>
			    </div>
			    <div><button class="btn btn-primary btn-xs" id="monthlyAccountApply" data-dismiss="modal">Select</button></div>
			    <br>
			    </div>
		</div>
	</div>
	</div>
</div>
</body>
</html>