<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Upload Message</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="acap/ap.css">
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<script>
	
</script>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
	<div style="width: 1024px; margin: auto;">
		<%@ include file="theme/Theme1/prefix.jspf"%>

		<%-- The Beginning of Page Content --%>

		<div id="page_content">
			<h2>${requestScope.message}</h2>
			<font size="4" face="Arial"><a target="_top"
				href="${requestScope.returnpage}">Return to last page</a></font>
		</div>
	</div>
</body>
</html>