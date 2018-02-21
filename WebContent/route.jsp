<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Routing Table</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/route.css">
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>

<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%-- 
<c:if test="${sessionScope.userLevel > 0}">
    <c:redirect url="login.jsp"/>
</c:if>
 --%>
<%@ include file="theme/Theme1/prefix.jspf"%>

<div id="page_content">
    <div id="page_title">Route Setting</div>

    <div style="margin: 10px 0;">
        <div style="width: 100%;">
            <span style="display: block; margin: 0 auto; width: 660px;">Routing table:</span>
            <textarea id="routing_table_textarea" style="display: block; margin: 0 auto; width: 660px; height: 250px;" readonly="readonly"></textarea>
        </div>
        <br>
        <div style="text-align: center;">
            <button class="btn btn-primary btn-sm" id="update_routing_table_button" onclick="refreshRoutingTable()">Refresh</button>
        </div>
    </div>
    
    <hr>
    
    <div style="margin: 10px 0;">
        <div style="width: 100%;">
            <span style="margin-left: 107px; float: left; width: 400px;">Eth1:</span>
            <span style="margin-left: 5px; width: 400px;">Eth2:</span>
        </div>
        <div style="width: 100%;">
            <textarea id="routing_setting_textarea1" style=" margin-left: 107px; float: left; width: 400px; height: 400px;" spellcheck="false"></textarea>
            <textarea id="routing_setting_textarea2" style=" margin-left: 5px; width: 400px; height: 400px;" spellcheck="false"></textarea>
        </div>
        <br>
        <div style="text-align: center;">
            <button class="btn btn-primary btn-sm" id="update_routing_setting_button" onclick="refreshRoutingSetting()">Refresh</button>
            <button class="btn btn-primary btn-sm" id="upload_routing_setting_button" onclick="modifyRoutingSetting()">Modify</button>
        </div>
    </div>

    <script type="text/javascript">
    	function refreshRoutingTable() {
    	    var param = {
    	            action: 'get_routing_table'
    	    };
		    $.ajaxSetup({ cache: false });
		    $.ajax({
		        url: "Settings",
		        data: param,
		        //dataType: 'json',
		        Type: "POST",
		        success: function(response) {
		            $('#routing_table_textarea').val(response);
		        },
		        error: function () {
		            alert('error');
		        }
		    });
    	}
    	
    	function refreshRoutingSetting() {
    	    var param = {
    	            action: 'get_route_setting'
    	    };
		    $.ajaxSetup({ cache: false });
		    $.ajax({
		        url: "Settings",
		        data: param,
		        dataType: 'json',
		        Type: "POST",
		        success: function(response) {
		            if (!response.done) {
		                $('#routing_setting_textarea1').val(response.text);
		                $('#routing_setting_textarea2').val(response.text);
		            }
		            else {
		                $.each( response.interfaces, function( ifName, ifString ) {
		                    if (ifName == 'eth0') {
		                        $('#routing_setting_textarea1').val(ifString);
		                    }
		                    else if (ifName == 'eth1') {
		                        $('#routing_setting_textarea2').val(ifString);
		                    }
		                });
		            }
		        },
		        error: function () {
		            alert('error');
		        }
		    });
    	}
    	
    	function modifyRoutingSetting() {
    	    var param = {
    	            action: 'set_route_setting',
    	            "eth0" : $('#routing_setting_textarea1').val(),	// .val() could get the real, not the initial value in tag
    	            "eth1" : $('#routing_setting_textarea2').val()
    	    };
		    $.ajaxSetup({ cache: false });
		    $.ajax({
		        url: "Settings",
		        data: param,
		        dataType: 'json',
		        Type: "POST",
		        success: function(response) {
		            if (response.done) {
		                if (confirm("Setting upload success.\nRestart network interface?")) {
		                    restartNetworkInterface();
		                }
		            }
		            else {
		                alert(response.text);
		            }
		        },
		        error: function () {
		            alert('error');
		        }
		    });
    	}
    	
    	function restartNetworkInterface() {
    	    var param = {
    	            action: 'restart_network'
    	    };
		    $.ajaxSetup({ cache: false });
		    $.ajax({
		        url: "Settings",
		        data: param,
		        //dataType: 'json',
		        Type: "POST",
		        success: function(response) {
		            alert(response);
		            refreshRoutingTable();
		        },
		        error: function () {
		            alert('error');
		        }
		    });
    	}
    </script>
    
    <c:out value="<script type='text/javascript'>$(document).ready(refreshRoutingTable(), refreshRoutingSetting());</script>" escapeXml="false"></c:out>
</div> <!-- div:page_content -->
</div>
</body>
</html>