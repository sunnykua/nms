<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AP Real-Time Chart ${param.ip}</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="acap/stupidtable.js"></script>
<script type='text/javascript' src="acap/tablefilter_all_min.js"></script>
<script type='text/javascript' src="acap/ezEditTable.js"></script>
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

	function changeFunc(value) {
		//document.getElementById("debug").innerHTML = value;

		var NewArray = value.split("&");
		window.open("monitorchart_apssidlistrxtxbps.jsp?acip=${param.ip}&apip="
				+ NewArray[0] + "&apssidname=" + NewArray[1] + "&apssidindex="
				+ NewArray[3], value + "ssidrealtime_TXRX",
				"menubar=1,resizable=1,width=450,height=320");
	}
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
			<div id="page_title">AP Real-Time Chart</div>
			<jsp:include page="/APList">
				<jsp:param value="ap_realtime_chart" name="action" />
				<jsp:param value="${param.ip}" name="ip" />
			</jsp:include>
			<br>
			<table id="Log_Viewer_Table" class="Log_Viewer_Table">
				<thead>
					<tr class="one">
						<!-- <th data-sort="string" align="left">APs <a
							href="javascript:void(0)" id="TXButton" onclick="open_tx()">TX</a>&nbsp;/&nbsp;<a
							href="javascript:void(0)" id="RXButton" onclick="open_rx()">RX</a>
						</th>-->
						<th data-sort="string" align="left">AP Name</th>
						<th data-sort="string" align="left">IP(Wired)</th>
						<th data-sort="string" align="left">SSID(Wireless)<!-- <span id="debug">debug</span>--></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${data}" varStatus="status">
						<tr class="a" onmouseover="this.style.backgroundColor='#ffffa2'"
							onMouseOut="this.style.backgroundColor=''">
							<!-- <td><input type="checkbox" class="td" name="APDevices"
								value="${item[1]}"></td>-->
							<td class="a">${item[0]}</td>
							<td class="a"><a href="javascript:void(0)"
								title="${item[1]}"
								onclick="window.open('monitorchart_aplistrxtxbps.jsp?acip=${param.ip}&apip=${item[1]}','${item[1]}+realtime_TXRX','menubar=1,resizable=1,width=450,height=320')">${item[1]}</a></td>
							<td class="a"><select id="ssid_select"
								onmousedown="this.value='';" onchange="changeFunc(this.value);">
									<option value="">------Select SSID------</option>
									<c:forEach var="item2" items="${data2.get(status.index)}">
										<c:set var="ssid" value="${data2.get(status.index)}" />
										<option value="${item[1]}&${item2}">${fn:split(item2,'&')[0]}(${fn:split(item2,'&')[1]})</option>
									</c:forEach>
							</select></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<script type='text/javascript'>
			var table12_Props = {
				highlight_keywords : true,
				on_keyup : true,
				on_keyup_delay : 1,
				single_search_filter : true,
				selectable : true
			};
			var tf12 = setFilterGrid("Log_Viewer_Table", table12_Props);
		</script>
	</div>
</body>
</html>