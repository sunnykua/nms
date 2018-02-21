<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>About</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/about.css">
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<script type="text/javascript">
function open_rtchart1() {
	
	/* var target = "realtime_chart.jsp?ip=" + ip + "&port=" + document.getElementById("rt_port_select").value; */
	var target = "document/CyberExpertUserGuide_V1_0_2.pdf";
	window.open(target,"rtchart1","menubar=1,resizable=1,width=1024,height=768");
}
</script>

</head>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<div id="about_info">
		<table id="about_table_title">
			<tr>
				<td><img style="heigh:70px;width:70px;" src="images/vialogo3.png"/></td>
                <td style="font-size:25px;">Cyber Expert</td>
                <td style="font-size:15px; padding-left: 5px;">v1.0.4</td>
			</tr>
		</table>
		<table id="about_table">
			<!-- <tr><th>Version 1.0</th></tr> -->
			<tr><th>Headquarters</th></tr>
			<tr><td>1F,531,Zhongzheng Rd.,Xindian Dist. New Taipei City 231, Taiwan</td></tr>
			<tr><th>Tel</th></tr>
			<tr><td>886-2-2218-5452 Ext:6917</td></tr>
			<tr><th>Fax</th></tr>
			<tr><td>886-2-2219-8461</td></tr>
			<tr><th>E-Mail</th></tr>
			<tr><td>StevenYang@via.com.tw</td></tr>
			<tr><th>User Manual</th></tr>
			<tr>
			<td>
			<button class="btn btn-primary btn-xs" onclick="open_rtchart1()">Show</button>
			</td>
			</tr>
		</table>
	</div>
	<div id="about_info">Copyright©2014 VIA Technologies, Inc.</div>
</div>
</body>
</html>
