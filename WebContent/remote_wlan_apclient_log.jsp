<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AP/STA Log ${param.remote_address}
	${param.remote_device_ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css"
	href="theme/Theme1/log_viewer.css">
<script type="text/javascript"
	src="theme/datatables/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css"
	href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript"
	src="theme/datatables/jquery.highlight-4.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css"
	href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script src="theme/jQueryUI/jquery-ui.js"></script>
<script src="theme/jQueryUI/jquery.blockUI.js"></script>
<style>
.ui-datepicker {
	font-size: 10px;
}
</style>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>

<body
	onload="createRemoteAcMenu('${param.remote_address}','${param.remote_device_ip}')">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<%-- The Beginning of Page Content --%>

		<jsp:include page="/APSTALog">
			<jsp:param value="APSTALog" name="action" />
			<jsp:param value="${param.remote_address}" name="remote_address" />
			<jsp:param value="${param.remote_device_ip}" name="remote_device_ip" />
		</jsp:include>

		<div id="page_content">
			<div id="page_title">AP/Client Log</div>
			<br>

			<table id="time-selector-table">
				<tr>
					<th class="time-selectotr-header">Select one single day:</th>
					<th class="time-selectotr-header">Select the time range:</th>
				</tr>
				<tr>
					<td class="time-selector-calendar"><div id="datepicker"></div></td>
					<td class="time-selector-fromto">
						<table>
							<tr>
								<td><label for="from">From:</label></td>
								<td><input type="text" id="log_from" name="from"></td>
							</tr>
							<tr>
								<td><label for="to">To:</label></td>
								<td><input type="text" id="log_to" name="to"></td>
								<td><button id="log_fromto_apply">Apply</button></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

			<table id="Log_Viewer_Table">
				<thead>
					<tr>
						<th>Time</th>
						<th>MAC</th>
						<th>Message</th>
						<th>EventID</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${not empty logTable}">
						<c:forEach var="item" items="${logTable.logItems}">
							<tr>
								<td class="itemLevel">${item.datetime}</td>
								<td class="itemLevel">${item.mac}</td>
								<td class="itemMessage">${item.message}</td>
								<td class="itemLevel">${item.eventid}</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
	<script type="text/javascript">
		function init_log_calendar(first_log_time, last_log_time,
				selected_log_date) {
			if (!selected_log_date)
				selected_log_date = new Date();
			$("#datepicker")
					.datepicker(
							{
								minDate : new Date(first_log_time),
								maxDate : new Date(last_log_time),
								dateFormat : 'yy-mm-dd',
								defaultDate : "+1w",
								onSelect : function(dateText, inst) {
									window.location.href = "remote_wlan_apclient_log.jsp?remote_address=${param.remote_address}&remote_device_ip=${param.remote_device_ip}&level="
											+ log_level + "&date=" + dateText;
								}
							}).datepicker('setDate', selected_log_date);
		};
		$(function() {
			$("#log_fromto_apply")
					.click(
							function() {
								if ($("#log_from").val() == "") {
									alert("Please select the start date");
								} else if ($("#log_to").val() == "") {
									alert("Please select the end date");
								} else {
									window.location.href = "remote_wlan_apclient_log.jsp?remote_address=${param.remote_address}&remote_device_ip=${param.remote_device_ip}&level="
											+ log_level
											+ "&datefrom="
											+ $("#log_from").val()
											+ "&dateto="
											+ $("#log_to").val();
								}
							});
		});
		function init_log_fromto(first_log_time, last_log_time,
				selected_log_from, selected_log_to) {
			$("#log_from").datepicker({
				minDate : new Date(first_log_time),
				maxDate : new Date(last_log_time),
				dateFormat : 'yy-mm-dd',
				defaultDate : "+1w",
				onClose : function(selectedDate) {
					//$( "#log_to" ).datepicker( "option", "minDate", selectedDate);
				}
			}).datepicker('setDate', selected_log_from);
			$("#log_to").datepicker({
				minDate : new Date(first_log_time),
				maxDate : new Date(last_log_time),
				dateFormat : 'yy-mm-dd',
				defaultDate : "+1w",
				onClose : function(selectedDate) {
					//$( "#log_from" ).datepicker( "option", "maxDate", selectedDate );
				}
			}).datepicker('setDate', selected_log_to);
		};

		var log_level;

		function init_time_selector(first_log_time, last_log_time,
				selected_log_date, selected_log_from, selected_log_to,
				log_level) {
			this.log_level = (!log_level) ? "info" : log_level;
			init_log_calendar(first_log_time, last_log_time, selected_log_date);
			init_log_fromto(first_log_time, last_log_time, selected_log_from,
					selected_log_to);
		}

		$(document).ready(function() {
			$('#Log_Viewer_Table').dataTable({
				"order" : [ [ 0, "desc" ] ],
				"sDom" : '<"H"l<"centered"f>r>t<"F"ip>',
			});
		});
	</script>
	<c:out
		value="<script>$(document).ready(init_time_selector('${firstLogTime}', '${lastLogTime}', '${param.date}', '${param.datefrom}', '${param.dateto}', '${param.level}'));</script>"
		escapeXml="false"></c:out>
</body>
</html>