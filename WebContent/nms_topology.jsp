<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NMS</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/topology.css">
<link rel="stylesheet" type="text/css" href="theme/treeview/jquery.treeview.css" />
<script src="theme/treeview/jquery.cookie.js" type="text/javascript"></script>
<script src="theme/treeview/jquery.treeview.js" type="text/javascript"></script>
<script src="theme/jQueryUI/jquery.blockUI.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
$(document).ready(function(){
	$("#topology_treeview").treeview({
        control: "#treecontrol",
        persist: "cookie",
        cookieId: "treeview-black"
    });
    $("#topology_treeview_2").treeview({
        control: "#treecontrol",
        persist: "cookie",
        cookieId: "treeview-black"
    });
    $('.treeview li:last-child').addClass('last');
 });
</script>
</head>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createNmsMenu('${param.ip}')">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/Topology">
		<jsp:param value="nms_tree_view" name="action"/>
		<jsp:param value="${param.ip}" name="ip"/>
	</jsp:include>
	
	<div id="page_content">
	<div id="page_title">Remote Topology</div>
		<div class="toplogyNumber">(1)</div>
			<ul id="topology_treeview" ><!-- class="treeview-black"> -->
			   <c:out value="${topologyTree[0][0]}" escapeXml="false"/>
			</ul>
            <ul id="topology_treeview_2" ><!-- class="treeview-black"> -->
            <c:forEach begin="1" var="html" items="${topologyTree}" varStatus="status">
            <div class="toplogyNumber">(${status.count+1})</div>
			   <c:out value="${html[0]}" escapeXml="false"/>
			</c:forEach>
			</ul>
	</div>
</div>
</body>
</html>