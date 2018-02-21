<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AP History Flow Diagram Select</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<script type="text/javascript">
var acIp;
function init(acIp) {
   this.acIp = acIp;
}

$(function () {
	$("select#apList").change(function(){
	    //alert($("#apList").val());
	    sendData = {
	    		acIp: acIp,
	    		apIp: $("#apList").val(),
	    }
	    $.ajax({
    	       type: "POST",
    	       url: "HistoryApList?action=get_ssidList",
    	       data: sendData,
    	       success:function(data){
    	    	   $('#ssidList').html('');
    	    	   $("#ssidList").append(new Option("Select SSID", "none"));
    	    	   for(var i=0;i<data.length;i++){
    	    		   $("#ssidList").append(new Option(data[i][0], data[i][0]));
    	    	   }
    	       }
	
    	       });
	    });
});

$(function () {
	$("#apListShow").click(function() {
		if($("#apList").val() == "none"){
			alert("Please select one of the ap ip.");
		} else {
			var target = "historychart_aplist_rxtxoctet_bps.jsp?acip=" + acIp + "&apip=" + $("#apList").val();
			window.open(target,"htchart4","menubar=1,resizable=1,width=950,height=460");
		}
	});
});

$(function () {
	$("#ssidListShow").click(function() {
		if($("#ssidList").val() == "none"){
			alert("Please select one of the ssid name.");
		} else {
			var target = "historychart_apssidlist_rxtxoctet_bps.jsp?acip=" + acIp + "&apip=" + $("#apList").val() + "&ssid=" + $("#ssidList").val();
			window.open(target,"htchart4","menubar=1,resizable=1,width=950,height=460");
		}
	});
});
</script>
<!-- <script type="text/javascript">
	function set_matching_word(selectObj, txtObj) {
		var letter = txtObj.value;

		for (var i = 0; i < selectObj.length; i++) {
			//alert(letter);
			//alert(selectObj.options[i].text);
			var str = selectObj.options[i].text.split(" ");
			//alert(str[0]);
			var str2 = str[1].split("（");
			var str3 = str2[1].split("）");
			//alert(str3[0]);
			if (str[0] == letter || str3[0] == letter)
				selectObj.selectedIndex = i;
		}
	}

	function open_htchart4(/* link,  */ip, chart_type) {
		//link.href = link.href + "?ip=" + ip + "&port=" + document.getElementById("port_select").value;

		var target = "ap_history_flow_diagram_txrx.jsp?ip="
				+ document.getElementById("ht_port_select4").value
				+ "&chart_type=" + chart_type;
		//window.open(target);

		window.open(target, document.getElementById("ht_port_select4").value
				+ "RX&TX History Flow Diagram",
				"menubar=1,resizable=1,width=950,height=460");
	}
</script> -->
</head>

<body onload="createAcMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

<c:out value="<script>$(document).ready(init('${param.ip}'));</script>" escapeXml="false"></c:out>

	<div id="page_content">
		<div id="page_title">AP History Flow Diagram Select</div>
		<jsp:include page="/HistoryApList">
			<jsp:param value="${param.ip}" name="acIp"/>
			<jsp:param value="get_apList" name="action"/>
		</jsp:include>
		<br>
		<div id="page_title">Wired</div>
			<table id="Log_Viewer_Table">
				<tr>
					<td><font size="4"><b>Select AP：</b></font>
						 <select class="port_select" id="apList" style="width: auto;" name="letters">
						 	<option value="none">Please select AP</option>
								<c:forEach var="item" items="${apList}" varStatus="status">
									<option value="${item[0]}">${item[0]}</option>
								</c:forEach>
						 </select>
					</td>
					<td><button id="apListShow" class="btn btn-primary btn-xs" >Show</button></td>
				</tr>
			</table>
		<br>
		<div id="page_title">Wireless</div>
			<table id="Log_Viewer_Table">
				<tr>
					<td><font size="4"><b>Select SSID：</b></font>
						<select class="port_select" id="ssidList" style="width: auto;" name="letters">
							<option value="none">Select SSID</option>
						</select>
					</td>
					<td><button id="ssidListShow" class="btn btn-primary btn-xs">Show</button></td>
				</tr>
			</table>
	</div>

	<%-- <div id="page_content">
		<div id="page_title">AP History Flow Diagram Select</div>
		<jsp:include page="/APList" />
		<br>
		<div id="page_title">Wired</div>
		<form name="check_letter_frm" action="#">
			<table id="Log_Viewer_Table">
				<tr>
					<td><font size="4"><b>Select AP：</b></font> <select
						class="port_select" id="ht_port_select4" style="width: auto;"
						name="letters">
							<c:forEach var="item" items="${data3}" varStatus="status">
								<option value="${item[5]}">${item[2]}（${item[5]}）</option>
							</c:forEach>
					</select></td>
					<td><button class="btn btn-primary btn-sm"
							onclick="open_htchart4('${param.ip}','ap_txrx')">SHOW</button></td>
				</tr>
			</table>
		</form>
		<br> <br>
		<div id="page_title">Wireless</div>
		<table id="Log_Viewer_Table">
			<thead>
				<tr>
					<th data-sort="string">SSID</th>
				</tr>
			</thead>
			<c:forEach var="item" items="${data4}">
				<tr>
					<td class="a"><a href="javascript:void(0)"
						onclick="window.open('ap_history_flow_diagram_txrx.jsp?ip=${item[2]}&chart_type=ap_ssid_sum_txrx','${item[2]}+RX&TX History Flow Diagram','menubar=1,resizable=1,width=950,height=460')">${item[2]}</a></td>
				</tr>
			</c:forEach>
		</table>
	</div> --%>
</div>
</body>
</html>