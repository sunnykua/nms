<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>License</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<script>
	function checkFileSize(inputFile) {
		var max = 1 * 1024 * 1024; // 10MB

		var filename = inputFile.files[0].name;

		if (inputFile.files && filename != "license.txt") {
			alert("Please Upload license.txt."); // Do your thing to handle the error.
			inputFile.value = null; // Clear the field.
		} else if (inputFile.files && inputFile.files[0].size > max) {
			alert("File " + filename + " too large."); // Do your thing to handle the error.
			inputFile.value = null; // Clear the field.
		}
	}
</script>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<%-- The Beginning of Page Content --%>

        <c:if test="${sessionScope.userLevel>1}">
	        <c:redirect url="login.jsp"></c:redirect>
        </c:if>
        
		<div id="page_content">
			<div id="page_title">License</div>
			<jsp:include page="/License" />
			<br>
			<div align="center">
				<form method="post" action="License" enctype="multipart/form-data">
					<font face="Arial">Select license file(<font color="#0000FF"><b>license.txt</b></font>)to upload</font>
                    <input type="file" name="file" size="60" onchange="checkFileSize(this)"><br>
                    <input type="submit" class="btn btn-primary btn-sm" value="Upload">
				</form>
			</div>
			<br>
			<table id="account_info_table" align="center">
				<tr>
					<th align="right">NMS Serial Number：</th>
					<td align="left">${license.get(0)}</td>
				</tr>
				<tr>
					<th align="right">Current Device Number：</th>
					<td align="left">${license.get(1)}</td>
				</tr>
				<tr>
					<th align="right">License Device Number：</th>
					<td align="left">${license.get(3)}</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>
