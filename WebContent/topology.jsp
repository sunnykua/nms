<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Topology</title>
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
</head>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/Topology">
        <jsp:param value="tree_view" name="action"/>
        <jsp:param value="${param.select_ip}" name="select_ip"/>
        <jsp:param value="${param.display_type}" name="display_type"/>
	</jsp:include>
	<jsp:include page="/Topology">
		<jsp:param value="top_device_get" name="action"/>
	</jsp:include>
	<jsp:include page="/Topology">
		<jsp:param value="remote_tree_view" name="action"/>
	</jsp:include>
	<c:set var="nmsType" value="<%= Config.getNmsType() %>"/>

	<div id="page_content">
		<div id="page_title">Topology</div>
		
		<div id="tree_view_section">
			<table id="legend_table">
			    <tr>
			        <td class="legend_icon"><img src='images/switch_layer2.png' style='width: 24px; height: 24px;'></td><td class="legend_text">L2 Switch</td>
			        <td class="legend_icon"><img src='images/switch_layer3.png' style='width: 24px; height: 24px;'></td><td class="legend_text">L3 Switch</td>
			        <td class="legend_icon"><img src='images/firewall.png' style='width: 24px; height: 24px;'></td><td class="legend_text">Firewall</td>
			        <td class="legend_icon"><img src='images/wlan_ac.png' style='width: 24px; height: 24px;'></td><td class="legend_text">AC</td>
			        <td class="legend_icon"><img src='images/wlan_ap.png' style='width: 24px; height: 24px;'></td><td class="legend_text">AP</td>
			        <td class="legend_icon"><img src='images/server.png' style='width: 24px; height: 24px;'></td><td class="legend_text">Server</td>
                    <td class="legend_icon"><img src='images/nms.png' style='width: 24px; height: 24px;'></td><td class="legend_text">NMS</td>
			        <td class="legend_icon"><img src='images/desktop.png' style='width: 24px; height: 24px;'></td><td class="legend_text">PC</td>			        
			        <td class="legend_icon"><img src='images/cloud.png' style='width: 24px; height: 24px;'></td><td class="legend_text">Internet</td>
			    </tr>
			    <tr>
			    	<td>&nbsp;</td>
			    </tr>	    
			    <tr>
			    	<td class="legend_icon"><img src='images/mgv_chief_server.png' style='width: 24px; height: 24px;'></td><td class="legend_text">MGV Chief Server</td>
			    	<td class="legend_icon"><img src='images/mgv_command_server.png' style='width: 24px; height: 24px;'></td><td class="legend_text">MGV Command Server</td>
			    	<td class="legend_icon"><img src='images/mgv_player.png' style='width: 24px; height: 24px;'></td><td class="legend_text">MGV Player</td>
			    </tr>
			</table>
			<hr>
            <table>
            <tr>
            	<td>
                ● Choose top device (please choose one device in topology map) or 
                <%-- <select id="topology_top_device_selector">
                    <option value="none" <c:if test="${empty param.select_ip}">selected="selected"</c:if>>not specified</option>
                    <c:if test="${not empty treeIpSelect}">
                        <c:forEach var="ip" items="${treeIpSelect}">
                            <option value="${ip}" <c:if test="${not empty param.select_ip && param.select_ip == ip}">selected="selected"</c:if>>${ip}</option>
                            
                        </c:forEach>
                    </c:if>
                </select> --%>
                <%-- <c:forEach var="ip" items="${treeIpSelect}">
                    <c:if test="${not empty param.select_ip && param.select_ip == ip}">${ip}</c:if>
                </c:forEach> --%>
                Choose display type:
                <select id="topology_display_type_selector">
                    <option value="ip_only" <c:if test="${not empty param.display_type && param.display_type == 'ip_only'}">selected="selected"</c:if>>IP only</option>
                    <option value="name_only" <c:if test="${not empty param.display_type && param.display_type == 'name_only'}">selected="selected"</c:if>>Name only</option>
                    <option value="ip_name" <c:if test="${empty param.display_type || param.display_type == 'ip_name'}">selected="selected"</c:if>>IP (Name)</option>
                    <option value="name_ip" <c:if test="${not empty param.display_type && param.display_type == 'name_ip'}">selected="selected"</c:if>>Name (IP)</option>
                    <option value="ip_utilization" <c:if test="${not empty param.display_type && param.display_type == 'ip_utilization'}">selected="selected"</c:if>>IP [Utilization]</option>
                    <option value="name_utilization" <c:if test="${not empty param.display_type && param.display_type == 'name_utilization'}">selected="selected"</c:if>>Name [Utilization]</option>
                    <option value="ip_name_utilization" <c:if test="${not empty param.display_type && param.display_type == 'ip_name_utilization'}">selected="selected"</c:if>>IP (Name) [Utilization]</option>
                    <option value="name_ip_utilization" <c:if test="${not empty param.display_type && param.display_type == 'name_ip_utilization'}">selected="selected"</c:if>>Name (IP) [Utilization]</option>
                </select>
                <button class="btn btn-primary btn-xs" id="topology_selection_button">Apply</button>
                </td>
           	</tr>
           	<tr>
            	<td>
                ● Refresh topology map: (scan all device stp and lldp) <button class="btn btn-primary btn-xs" id="topology_refresh_button">Refresh</button>
                </td>
           	</tr>
           	<tr>
            	<td>
                ● Save top device: (please choose one device in topology map) <button class="btn btn-primary btn-xs" id="top_device_set">Save (${topDevice})</button>
                </td>
           	</tr>
           	<tr>
            	<td>
            	● Trace Path: (please choose two device in topology map) <button class="btn btn-primary btn-xs" id="topology_scan">Trace Path</button>
            	</td>
           	</tr>
            </table>
            <hr>
            <ul id="topology_treeview" >
	            <li><c:if test="${nmsType > 1}"><div>局端 Local</div></c:if>
		            <ul>
		            <c:forEach begin="0" var="html" items="${topologyTree}" varStatus="status">
			            <div class="toplogyNumber">(${status.count})</div>
						   <c:out value="${html[0]}" escapeXml="false"/>
					</c:forEach>
					</ul>
				</li>
				<c:if test="${sessionScope.userLevel < 2 && nmsType > 1}">
				<c:forEach begin="0" var="sch" items="${remoteTopologyTree}" varStatus="status">
					<li><div>${sch.key}</div>
						<ul>
						<c:forEach begin="0" var="local" items="${sch.value}" varStatus="localStatus">
							<div class="toplogyNumber">(${localStatus.count})</div>
								<c:out value="${local[0]}" escapeXml="false"/>
						</c:forEach>
						</ul>
					</li>
				</c:forEach>
				</c:if>
			</ul>
		</div>
		
		<div id="trace_path_section">
		   <table id="TopologyTable">
				<tr>
				    <th colspan="5">From</th>
				    <th rowspan="2" class="direction_cell"></th>
				    <th colspan="5">To</th>
				</tr>
				<tr>
					<th class="icon_cell">Type</th>
					<th class="ip_cell">IP Addr</th>
					<th class="port_cell">Port</th>
					<th class="history_cell">History</th>
					<th class="realtime_cell">Real-time</th>
					<th class="icon_cell">Type</th>
					<th class="ip_cell">IP</th>
					<th class="port_cell">Port</th>
					<th class="history_cell">History</th>
					<th class="realtime_cell">Real-time</th>
				</tr>
		   </table>
		</div>
	</div>
	
	<c:out value="<script type='text/javascript'>function updateRate() { ${treeScript} }</script>" escapeXml="false"></c:out>
	
	<script type="text/javascript">
//		$(document).ready(function(){
// 	        $("#topology_treeview").treeview({
// 	            control: "#treecontrol",
// 	            persist: "cookie",
// 	            cookieId: "treeview-black"
// 	        });
// 	    });
		
		function initTreeView() {
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
            
            updateRate();
            setInterval(function(){ updateRate(); }, 20000);
        }
		
		function openOnNewPage(url) {
			var newPage = window.open();
			newPage.location= url;
		}
		
		function clearData() {
			var param = {
		            chart: 'rx_tx_utilization',
		            action: 'clear'
		    };
			
		    $.ajaxSetup({ cache: false });

		    $.ajax({
		        url: "Chart",
		        data: param,
		        Type: "POST",
		        success: function() {
		        },
		        error: function () {
		            //alert("getData failed.");
		        }
		    });
		}
		
		function getData(ip, port, upRateId, dnRateId) {
			var updateInterval = 20;
			var param = {
		            ip: ip,
		            port: port,
		            chart: 'rx_tx_utilization',
		            action: 'read',
		            updateinterval: updateInterval
		    };
		    
		    $.ajaxSetup({ cache: false });

		    $.ajax({
		        url: "Chart",
		        data: param,
		        Type: "POST",
		        success: function(response) {
		        	var data = response.split(",");
		            if (data.length == 2) {
		            	var downRateKbps = parseFloat(data[0]);   // rx
	                    var upRateKbps = parseFloat(data[1]);       // tx
	                    
	                    var downRateString, upRateString;
	                    	downRateString = downRateKbps.toFixed(0) + ' %';
	                    	upRateString = upRateKbps.toFixed(0) + ' %';
	                    
	                    if(upRateKbps.toFixed(0)>=80){
	                    	$("#"+upRateId).addClass('highRate').removeClass('middleRate').removeClass('lowRate');
	                    }
	                    else if(upRateKbps.toFixed(0)<80 && upRateKbps.toFixed(0)>=60){
	                    	$("#"+upRateId).addClass('middleRate').removeClass('highRate').removeClass('lowRate');
	                    }
	                    else if(upRateKbps.toFixed(0)<60 && upRateKbps.toFixed(0)>=0){
	                    	$("#"+upRateId).addClass('lowRate').removeClass('highRate').removeClass('middleRate');
	                    }
	                    
	                    if(downRateKbps.toFixed(0)>=80){
	                    	$("#"+dnRateId).addClass('highRate').removeClass('middleRate').removeClass('lowRate');
	                    }
	                    else if(downRateKbps.toFixed(0)<80 && downRateKbps.toFixed(0)>=60){
	                    	$("#"+dnRateId).addClass('middleRate').removeClass('highRate').removeClass('lowRate');
	                    }
	                    else if(downRateKbps.toFixed(0)<60 && downRateKbps.toFixed(0)>=0){
	                    	$("#"+dnRateId).addClass('lowRate').removeClass('highRate').removeClass('middleRate');
	                    }
	                    document.getElementById(upRateId).innerHTML = upRateString;
	                    document.getElementById(dnRateId).innerHTML = downRateString;
		            }
		        },
		        error: function () {
		            //alert("getData failed.");
		        }
		    });
		}
		function getData1(ip, port, upRateId, dnRateId) {
			var updateInterval = 20;
			var param = {
		            ip: ip,
		            port: port,
		            chart: 'rx_tx_octet_bps',
		            action: 'read',
		            updateinterval: updateInterval
		    };
		    
		    $.ajaxSetup({ cache: false });

		    $.ajax({
		        url: "Chart",
		        data: param,
		        Type: "POST",
		        success: function(response) {
		        	var data = response.split(",");
		            if (data.length == 2) {
		            	var downRateKbps = parseFloat(data[0]);   // rx
	                    var upRateKbps = parseFloat(data[1]);       // tx
	                    
	                    var downRateString, upRateString;
	                    if (downRateKbps > 1024) {
	                    	downRateKbps /= 1024;
	                    	downRateString = downRateKbps.toFixed(1) + ' Mbps';
	                    }
	                    else {
	                    	downRateString = downRateKbps.toFixed(1) + ' Kbps';
	                    }
	                    if (upRateKbps > 1024) {
	                    	upRateKbps /= 1024;
	                    	upRateString = upRateKbps.toFixed(1) + ' Mbps';
	                    }
	                    else {
	                    	upRateString = upRateKbps.toFixed(1) + ' Kbps';
	                    }
	                    
	                    document.getElementById(upRateId).innerHTML = upRateString;
	                    document.getElementById(dnRateId).innerHTML = downRateString;
		            }
		        },
		        error: function () {
		            //alert("getData failed.");
		        }
		    });
		}
		
		function deleteSingleIp(){
			var senddata ={ip:"delete"};
			   $.ajax({
				   type: "POST",
				   url: "Device?method=single_ip_delete",
				   data: senddata,
				   success: function(response) {
			       },
			   });
		}
		
		$(function() {
		    $("#topology_refresh_button").click(function() {
		    	var refresh = ["refresh"];
		    	$.ajax({
		    		complete: function() {
		    			$.blockUI({ css: {
		    				border: 'none',
		    				padding: '15px',
		    		        backgroundColor: '#000',
		    		        '-webkit-border-radius': '10px',
		    		        '-moz-border-radius': '10px',
		    		        opacity: .5,
		    		        color: '#fff'
		    		                     } 
		    			});
		    		},
		    	});
		    	$.ajax({
		    	       type: "POST",
		    	       url: "Topology?action=refresh",
		    	       data:{refresh:refresh},
		    	       success:function(data){
		    	    	   setTimeout($.unblockUI);
		    	    	    	  if(data=="Success"){
		    	    	    		  alert("Success refresh, now reload page.");
		    	    	    	   location.reload();
		    	    	    	  }else{
		    	    	    		  alert("Fail!");
		    	    	    	  }
		    	    	},
		    	});
		    });
		});
		
		$(function() {
			$('#top_device_set').click(function() {
				var sendData;
				
				var select_ip = [];
		        $("input[name='selectSwitch']:checked").each(function(){
		        	select_ip.push($(this).val());
			    });
		        
		        if(select_ip.length > 1) {
			    	   alert("Please just select one device or not select.");
		  	   	}
		       	else {
			        sendData = {
							select_ip : select_ip[0]
					};
	
					$.ajax({
						url : "Topology?action=top_device_set",
						type : "POST",
						data : sendData,
						success : function(data) {
							alert("success!");
							location.reload();
						}
					});
		       	}
			});
		});

		$(function() {
		    $("#topology_selection_button").click(function() {
		        var select_ip = [];
		        $("input[name='selectSwitch']:checked").each(function(){
		        	select_ip.push($(this).val());
			    });
		        var display_type = $("#topology_display_type_selector").val();
		        
		        if(select_ip.length > 1) {
			    	   alert("Please just select one device or not select.");
		  	   	}
		       	else {
			        var param_string = "display_type=" + display_type;
			        if (select_ip != '' && select_ip != "none") {
			            param_string += "&select_ip=" + select_ip;
			        }
			        
			        location.assign("topology.jsp?" + param_string);
			    }
		    });

		    $("#topology_scan").click(function() {
		    	var scanip=[];
		    	var selected = scanip;
		    	
		    	//search checkbox
		    	$("input[name='selectSwitch']:checked").each(function(){
		    		scanip.push($(this).val());
			    });
			    	
		       if(selected.length != 2) {
		    	   alert("Please select two devices");
		  	   }
		       else {
					if(!confirm("Are you sure to choose these devices?\n"+selected)) {
						return;
					}
		  			 $.ajax({
	 	    			type: "POST",
	 	    			url: "Topology?action=trace_path",
	 	    			data:{scanip:scanip},
	 	    			dataType: "JSON",
	 	    			success: function(data) {
	 	    				$("#TopologyTable").find("tr:gt(1)").remove();//remove last time table value
	 	    	    	   
	 	    				for(var i=0;i<data.length;i++){
	
		 	    			  var from_ip = data[i][0][0];
		 	    			  var from_port = data[i][0][1];
		 	    			  var from_type = data[i][0][2];
		 	    			  var from_is_virtual = data[i][0][3] == "true";// ? true : false;
		 	    			  var from_ifIndex = data[i][0][4];
		 	    			  var from_is_endpoint = !(from_type == "l2switch" || from_type == "l3switch");
		 	    			  var to_ip = data[i][1][0];
		 	    			  var to_port = data[i][1][1];
		 	    			  var to_type = data[i][1][2];
		 	    			  var to_is_virtual = data[i][1][3] == "true";// ? true : false;
		 	    			  var to_ifIndex = data[i][1][4];
		 	    			  var to_is_endpoint = !(to_type == "l2switch" || to_type == "l3switch");
		 	    			  var button_history_from = "";
		 	    			  var button_realtime_from = "";
		 	    			  var button_history_to = "";
		 	    			  var button_realtime_to = "";
		 	    			  var img_type_from = "";
	                          var img_type_to = "";
		 	    			  
	 	    				  if (from_port != "") {
	 	    					 var onclick_history_from = "";
	 	    				     var onclick_realtime_from = "";
	 	    				     
	 	    				     if (!from_is_virtual) {
	 	    				        onclick_history_from = "onclick=\"window.open('historychart_rxtxoctet_bps.jsp?ip=" + from_ip +
	 	    				                "&ifid=" + from_ifIndex + "&portid=" + from_port + "', 'history" + from_ip + from_port + "', 'menubar=1,resizable=1,location=no,width=950,height=460');\"";
	 	    				        onclick_realtime_from = "onclick=\"window.open('monitorchart_rxtxbps.jsp?ip=" + from_ip +
	 	    				       			"&ifid=" + from_ifIndex + "&portid=" + from_port + "', 'realtime" + from_ip + from_port + "', 'menubar=1,resizable=1,location=no,width=450,height=320');\"";
	 	    				     }
	 	    				     
	 	    				     if (!from_is_endpoint) {
		 	    				     button_history_from = "<button type='button' " + 
	     	    				        "style='background-image: url( images/calendar.png ); background-size: 24px 24px; width: 28px; height: 28px;' " +
	     	    				        onclick_history_from + "></button>";
	     	    				     button_realtime_from = "<button type='button' " +
	     	    				        "style='background-image: url( images/realtime.png ); background-size: 24px 24px; width: 28px; height: 28px;' " +
	     	    				        onclick_realtime_from + "></button>";
	 	    				     }
	 	    				  }
	
	                          if (from_type == "l2switch")
	                        	  img_type_from = "<img src='images/switch_layer2.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "l3switch")
	                        	  img_type_from = "<img src='images/switch_layer3.png' style='width: 24px; height: 24px;'>";
	                    	  else if (from_type == "wlanAC")
	                              img_type_from = "<img src='images/wlan_ac.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "wlanAP")
	                              img_type_from = "<img src='images/wlan_ap.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "firewall")
	                              img_type_from = "<img src='images/firewall.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "server")
	                              img_type_from = "<img src='images/server.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "pc")
	                              img_type_from = "<img src='images/desktop.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "internet")
	                              img_type_from = "<img src='images/cloud.png' style='width: 24px; height: 24px;'>";
	                          else if (from_type == "nms")
	                              img_type_from = "<img src='images/nms.png' style='width: 24px; height: 24px;'>";
					          else if (from_type == "MGVChiefServer")
					              img_type_from = "<img src='images/mgv_chief_server.png' style='width: 24px; height: 24px;'>";
			                  else if (from_type == "MGVCommandServer")
			                      img_type_from = "<img src='images/mgv_command_server.png' style='width: 24px; height: 24px;'>";
			                  else if (from_type == "MGVPlayer")
			                      img_type_from = "<img src='images/mgv_player.png' style='width: 24px; height: 24px;'>";
		 	    			  
	 	    				  if (to_port != "") {
	 	    					  var onclick_history_to = "";
	                              var onclick_realtime_to = "";
	                                 
	                              if (!to_is_virtual) {
	                                  onclick_history_to = "onclick=\"window.open('historychart_rxtxoctet_bps.jsp?ip=" + to_ip +
	                                  		"&ifid=" + to_ifIndex + "&portid=" + to_port + "', 'history" + to_ip + to_port + "', 'menubar=1,resizable=1,location=no,width=950,height=460');\"";
	                                  onclick_realtime_to = "onclick=\"window.open('monitorchart_rxtxbps.jsp?ip=" + to_ip +
	                                  		"&ifid=" + to_ifIndex + "&portid=" + to_port + "', 'realtime" + to_ip + to_port + "', 'menubar=1,resizable=1,location=no,width=450,height=320');\"";
	                              }
	                              
	                              if (!to_is_endpoint) {
		                              button_history_to = "<button type='button' " +
		                                  "style='background-image: url( images/calendar.png ); background-size: 24px 24px; width: 28px; height: 28px;' " +
		                                  onclick_history_to + "></button>";
		                              button_realtime_to = "<button type='button' " +
		                                  "style='background-image: url( images/realtime.png ); background-size: 24px 24px; width: 28px; height: 28px;' " +
		                                  onclick_realtime_to + "></button>";
	                              }
	 	    				  }
	
	 	    				  if (to_type == "l2switch")
	 	    					  img_type_to = "<img src='images/switch_layer2.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "l3switch")
	                              img_type_to = "<img src='images/switch_layer3.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "wlanAC")
	                              img_type_to = "<img src='images/wlan_ac.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "wlanAP")
	                              img_type_to = "<img src='images/wlan_ap.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "firewall")
	                              img_type_to = "<img src='images/firewall.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "server")
	                              img_type_to = "<img src='images/server.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "pc")
	                              img_type_to = "<img src='images/desktop.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "internet")
	                              img_type_to = "<img src='images/cloud.png' style='width: 24px; height: 24px;'>";
	                          else if (to_type == "nms")
	                              img_type_to = "<img src='images/nms.png' style='width: 24px; height: 24px;'>";
				              else if (to_type == "MGVChiefServer")
				                  img_type_to = "<img src='images/mgv_chief_server.png' style='width: 24px; height: 24px;'>";
				              else if (to_type == "MGVCommandServer")
				                      img_type_to = "<img src='images/mgv_command_server.png' style='width: 24px; height: 24px;'>";
			                  else if (to_type == "MGVPlayer")
			                      img_type_to = "<img src='images/mgv_player.png' style='width: 24px; height: 24px;'>";
		 	    			  
	                          $("#TopologyTable").append("<tr>" +
		 	    					  "<td class='icon_cell'>" + img_type_from + "</td>\n" +
	 	    	    				  "<td class='ip_cell'>" + from_ip + "</td>\n" +
	 	    	    				  "<td class='port_cell'>" + from_port + "</td>\n" +
	 	    	    				  "<td class='history_cell'>" + button_history_from + "</td>\n" +
	 	    	    				  "<td class='realtime_cell'>" + button_realtime_from + "</td>\n" +
	 	    	    				  "<td class='direction_cell'>=></td>\n" +
	 	    	    				  "<td class='icon_cell'>" + img_type_to + "</td>\n" +
	 	    	    				  "<td class='ip_cell'>" + to_ip + "</td>\n" +
	 	    	    				  "<td class='port_cell'>"+ to_port + "</td>\n" +
	 	    	    				  "<td class='history_cell'>" + button_history_to + "</td>\n" +
	 	    	    				  "<td class='realtime_cell'>" + button_realtime_to + "</td>\n" +
	 	    	    				  "</tr>\n");
		 	    			 }
	 	    			}
		 	    	});
			  	   }   // else, select = 2
			    });
			});
	</script>
	
	<c:out value="<script type='text/javascript'>$(document).ready(clearData(),initTreeView(),deleteSingleIp());</script>" escapeXml="false"></c:out>

<%-- The End of Page Content --%>
</div>
</body>
</html>
