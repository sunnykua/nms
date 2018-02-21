<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Client Number ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="acap/stupidtable.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css"
	href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<script>
	$(function() {
		$("table").stupidtable();
	});
</script>
<style type="text/css">
th[data-sort] {
	cursor: pointer;
}
</style>

<script type="text/javascript">
	$('document').ready(function() {

		$("#selectall").click(function() {
			$('.td').attr('checked', this.checked);
			//alert("You have selected all boxes");
		});

		$(".td").click(function() {
			//alert("You have checked  " + $(".td:checked").length + "  boxes");
			if ($(".td").length == $(".td:checked").length) {
				$("#selectall").attr("checked", "checked");
			} else {
				$("#selectall").removeAttr("checked");
			}

		});
	});
</script>
<script type="text/javascript">
	function open_tx() {
		var TX = [];
		var selected = TX;

		$("input[name='APDevices']:checked").each(function() {
			TX.push($(this).val());
		});

		if (selected.length < 1) {
			alert("Please select at least one AP");
		} else if (selected.length > 10) {
			alert("Select max ten APs");
		} else {
			var target = "ap_realtime_flow_diagram_tx.jsp?ip1=" + TX[0]
					+ "&ip2=" + TX[1] + "&ip3=" + TX[2] + "&ip4=" + TX[3]
					+ "&ip5=" + TX[4] + "&ip6=" + TX[5] + "&ip7=" + TX[6]
					+ "&ip8=" + TX[7] + "&ip9=" + TX[8] + "&ip10=" + TX[9]
					+ "&type=TX";
			//window.open(target);
			window.open(target, TX[0] + TX[1] + TX[2] + TX[3] + TX[4] + TX[5]
					+ TX[6] + TX[7] + TX[8] + TX[9]
					+ "TX Real-time Flow Diagram",
					"menubar=1,resizable=1,width=650,height=450");
		}
	}

	function open_rx() {

		var RX = [];
		var selected = RX;

		$("input[name='APDevices']:checked").each(function() {

			RX.push($(this).val());
		});

		if (selected.length < 1) {
			alert("Please select at least one AP");
		} else if (selected.length > 10) {
			alert("Select max ten APs");
		} else {
			var target = "ap_realtime_flow_diagram_rx.jsp?ip1=" + RX[0]
					+ "&ip2=" + RX[1] + "&ip3=" + RX[2] + "&ip4=" + RX[3]
					+ "&ip5=" + RX[4] + "&ip6=" + RX[5] + "&ip7=" + RX[6]
					+ "&ip8=" + RX[7] + "&ip9=" + RX[8] + "&ip10=" + RX[9]
					+ "&type=RX";
			//window.open(target);
			window.open(target, RX[0] + RX[1] + RX[2] + RX[3] + RX[4] + RX[5]
					+ RX[6] + RX[7] + RX[8] + RX[9]
					+ "RX Real-time Flow Diagram",
					"menubar=1,resizable=1,width=650,height=450");
		}
	}

	/*function open_rxtx() {
		//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;
		var target = "ap_realtime_flow_diagram_txrx.jsp?ip="
				+ document.getElementById("rxtx_select").value + "&type=TXRX";
		window.open(target, "realtime_TXRX",
				"menubar=1,resizable=1,width=650,height=450");
	}*/
</script>

</head>
<body onload="createAcMenu('${param.ip}')">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

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

		<div id="page_content" align="center">
			<div id="page_title">Client Number</div>
			<br>
			<table border="0">
				<tr>
					<td valign=top>
						<table id="Log_Viewer_Table" class="sortable">
							<div id="page_title">Real-Time</div>
							<thead>
								<tr>
									<th data-sort="string">Wireless Clients</th>
								</tr>
							</thead>
							<tr>
								<td class="a"><a href="javascript:void(0)"
									onclick="window.open('ap_client_number_diagram.jsp?ip=${param.ip}&type=anbgn','an & bgn Real-Time Client Number','menubar=1,resizable=1,width=650,height=450')">802.11an
										& bgn(2.4GHz & 5GHz)</a></td>
							</tr>
						</table>
					</td>
					<td valign=top>
						<table id="Log_Viewer_Table" class="sortable">
							<div id="page_title">History</div>
							<thead>
								<tr>
									<th data-sort="string">Wireless Clients</th>
								</tr>
							</thead>
							<tr>
								<td class="a"><a href="javascript:void(0)"
									onclick="window.open('ap_history_clients_totaloranorbgn_diagram_txrx.jsp?ip=${param.ip}&chart_type=Total','Total Wireless Client Number History','menubar=1,resizable=1,width=950,height=460')">Total</a></td>
							</tr>
							<tr>
								<td class="a"><a href="javascript:void(0)"
									onclick="window.open('ap_history_clients_totaloranorbgn_diagram_txrx.jsp?ip=${param.ip}&chart_type=an','an Wireless Client Number History','menubar=1,resizable=1,width=950,height=460')">802.11an(5GHz)</a></td>
							</tr>
							<tr>
								<td class="a"><a href="javascript:void(0)"
									onclick="window.open('ap_history_clients_totaloranorbgn_diagram_txrx.jsp?ip=${param.ip}&chart_type=bgn','bgn Wireless Client Number History','menubar=1,resizable=1,width=950,height=460')">802.11bgn(2.4GHz)</a></td>
							</tr>
							<tr>
								<td class="a"><a href="javascript:void(0)"
									onclick="window.open('ap_history_clients_anandbgn_diagram_txrx.jsp?ip=${param.ip}&chart_type=an%20%26%20bgn','an&bgn Wireless Client Number History','menubar=1,resizable=1,width=950,height=460')">802.11an
										& bgn(2.4GHz & 5GHz)</a></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>