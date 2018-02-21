<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tools</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet"
	type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<script type="text/javascript">
	var get_end_array = "get_end_array_notok";
	var a;
	$(document).ready(function() {
		ping_status();
	});

	function get_ping() {
		get_end_array = "get_end_array_notok";
		var address = $("#address").val();
		var count = $("#count").val();
		var timeout_sec = $("#timeout_sec").val();
		/*alert(a.address.value);*/
		var senddata = {
			address : address,
			count : count,
			timeout_sec : timeout_sec
		};
		$.ajax({
			type : "POST",
			url : "Tools?method=get_ping",
			data : senddata,
			success : function(data) {
				//alert(data);
				if (data == "ping_run") {
					//alert("data == run");
				} else if (data == "ping_stop") {
					//alert("data == stop");
				}
			}
		});
	}

	function ping_status() {
		var senddata = {
			get_end_array : get_end_array
		};
		$
				.ajax({
					type : "POST",
					url : "Tools?method=ping_status",
					data : senddata,
					success : function(data) {
						//alert(data + "\n" + get_end_array);
						if (data.indexOf("print_notok") > -1) {
							document.getElementById("textarea_in").value = "";
							var datasplit = data.split(',');
							for (var i = 0; i < datasplit.length; i++) {

								a = datasplit[i].replace("[", "");
								a = a.replace("]", "");

								if (a != ""
										&& a.indexOf("ping_run") == -1
										&& a.indexOf("ping_stop") == -1
										&& a.indexOf("get_end_array_ok") == -1
										&& a.indexOf("get_end_array_notok") == -1
										&& a.indexOf("print_ok") == -1
										&& a.indexOf("print_notok") == -1
										&& a.indexOf("end_array") == -1) {

									document.getElementById("textarea_in").value += a
											+ "\n";

									document.getElementById("textarea_in").scrollTop = document
											.getElementById("textarea_in").scrollHeight;
								}

								//alert(a);
								if (a.indexOf("end_array") > -1) {
									//alert("a=yes");
									get_end_array = "get_end_array_ok";
								}

								//alert(stop);
							}
						}
						setTimeout(ping_status, 1);
					}
				});
	}

	function reset() {
		document.getElementById("address").value = "";
		document.getElementById("count").
	value = "1";
		document.getElementById("timeout_sec").value = "1";
	}
</script>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<%-- The Beginning of Page Content --%>

		<c:if test="${sessionScope.userLevel>1}">
			<c:redirect url="login.jsp"></c:redirect>
		</c:if>

		<div id="page_content">
			<div id="page_title">Tools</div>
			<jsp:include page="/Tools" />
			<br>
			<table id="account_info_table" align="center">
				<div align="center">
					<strong><font size="4">Ping</font></strong>
				</div>
				<tr>
					<th align="right">Address：</th>
					<td align="left"><input type="text" id="address" size="15"></td>
				</tr>
				<tr>
					<th align="right">Count (1-20)：</th>
					<td align="left"><input type="text" id="count" size="15"
						value="1"><br></td>
				</tr>
				<tr>
					<th align="right">Timeout (1-60 sec)：</th>
					<td align="left"><input type="text" id="timeout_sec" size="15"
						value="1"></td>
				</tr>
				<tr>
					<td align="right"></td>
					<td align="left"><input type="button"
						class="btn btn-primary btn-sm" style="width: 60px;" value="Ping"
						onclick="get_ping()"> <input type="button"
						class="btn btn-primary btn-sm" style="width: 60px;" value="Clear"
						onclick="reset()"></td>
				</tr>
				<tr>
					<td colspan="2"><textarea readonly="true" rows="11"
							style="width: 500px;" id="textarea_in" name="S1" cols="50">
							</textarea></td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>