<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.via.system.Config"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Google Maps</title>

<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script src="theme/jQuery-tinyMap/jquery.tinyMap.js"></script>

</head>

<style>
body {
	margin: 0; /* This is used to reset any browser-default margins */
}

#Map {
	width: 100%;
	height: 100vh;
}
</style>

<script type="text/javascript">
	$(function() {
		getRemoteNMSListLocationAddress();
	});

	var json = [ {
		'addr' : "110台灣台北市信義區忠孝東路五段51號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段51號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段151號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段151號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段232號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段232號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段344號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段344號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段444號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段444號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段544號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段544號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段644號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段644號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路五段44號",
		'text' : '<strong>110台灣台北市信義區忠孝東路五段44號</strong>'
	}, {
		'addr' : "110台灣台北市信義區忠孝東路四段151號",
		'text' : '<strong>110台灣台北市信義區忠孝東路四段151號</strong>'
	} ]

	function getRemoteNMSListLocationAddress() {
		$.ajax({
			type : "POST",
			url : "Dashboard?method=getRemoteNMSListLocationAddress&address=${param.address}",
			success : function(data) {
				//console.log(data);

				console.log(data[0]);

				console.log(data[1]);
				if (data[1] === undefined || data[1] == "") {
					$('#Map').tinyMap({});
				} else {
					$('#Map').tinyMap({
						'markerFitBounds' : true,
						'markerCluster' : true,
						'marker' : data[1]
					});
				}
			}
		});
	}
</script>

<body>

	<div id="Map"></div>

</body>

</html>