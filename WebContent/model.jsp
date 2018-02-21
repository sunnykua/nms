<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Model</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/module.css">
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<script type="text/javascript">
$(function() {
	$("#tabs").tabs({
		//event: "mouseover"
	});
});

$(function() {
    $('[id^=check_modelName]').click(function() {
    	if($('#model' + $(this).attr('id').substr(15)).val()==""){
    		alert("Model Name not Empty!!");
    	}else{
	    	var data = {
	    			model_name:  $('#model' + $(this).attr('id').substr(15)).val()
	    			};
	    	var name = $(this).attr('id').substr(15);
	    	
	    	$.ajax({
	            url: "Settings?action=module_check",
	            type: "post",
	            data: data,
	            success: function(data) {
	            	if(data=="Repeat"){
	            		alert("This Model Name Can't Use!!");
	            		$('#model' + name).val("");
	        		}else
	        			alert("This Model Name Can Use!!");
	            }
	            
	    	});
    	}
    });
});

$(function () {
	   $("#module_add").click(function() {
		   var rj45 = $("#rj45_num").val(), fiber = $("#fiber_num").val(), sum = 0;
		   sum = parseInt(rj45) + parseInt(fiber);
		   /* if($("#brand").val() == "" || $("#model").val() == "" || $("#read_commumity").val() == "" || $("#write_commumity").val() == "" || $("#object_id").val() == "" || $("#inf_idx_cpu").val() == "" || $("#rj45_num").val() == "" || $("#fiber_num").val() == "" || $("#jack_list").val() == "" || $("#speed_list").val()== "" ){
			   alert("Have null fields!");
		   }else  */if($("#inf_num").val() < sum){
			   alert("INF Number is error!");
		   }else{
			   var module = {
					   brand: $("#brand").val(),
					   model: $("#model").val(),
					   device_type: $("#device_type").val(),
					   snmp_sup: $("#snmp_sup").val(),
					   snmp_timeout: $("#snmp_timeout").val(),
					   revision : $("#revision").val(),
					   read_commumity: $("#read_commumity").val(),
					   write_commumity: $("#write_commumity").val(),
					   object_id: $("#object_id").val(),
					   inf_idx_cpu: $("#inf_idx_cpu").val(),
					   inf_num: $("#inf_num").val(),
					   rj45_num: $("#rj45_num").val(),
					   fiber_num: $("#fiber_num").val(),
					   jack_list: $("#jack_list").val(),
					   speed_list: $("#speed_list").val(),
					   host_resource: $("#host_resource").val(),
					   sup_linkstate: $("#sup_linkstate").val(),
					   sup_negostate: $("#sup_negostate").val(),
					   sup_rxtxoctet: $("#sup_rxtxoctet").val(),
					   sup_pkttype: $("#sup_pkttype").val(),
					   sup_rmon: $("#sup_rmon").val(),
					   sup_pvid: $("#sup_pvid").val(),
					   sup_vlan: $("#sup_vlan").val(),
					   sup_gvrp: $("#sup_gvrp").val(),
					   sup_poe: $("#sup_poe").val(),
					   sup_trap: $("#sup_trap").val(),
					   sup_lldp: $("#sup_lldp").val(),
					   sup_rstp: $("#sup_rstp").val(),
					   sup_mstp: $("#sup_mstp").val(),
					   sup_edgecoreresource: $("#sup_edgecoreresource").val(),
					   sup_octet64: $("#sup_octet64").val(),
					   sup_egco_trap: $("#sup_egco_trap").val()
			   };
			   
			   $.ajax({
	    	       type: "POST",
	    	       url: "Settings?action=module_add",
	    	       data:module,
	    	       success:function(data){
	    	    	   if(data=="Repeat"){
		            		alert("This Model Name Can't Use!!");
		            		$('#model').val("");
		        		}else{
		    	    	   alert(data);
		    	    	   location.reload();
		        		}
	    	       },
			   });
		   }
	   });
});

$(function () {
   $("#deleteDeviceBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deleteDevices']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one of the devices");
	   } else {
		   if (confirm("Are you sure to delete these devices?")) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Settings?action=module_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $("[id^=deleteDevices_]").each(function(){
			    		   if(this.checked){
			    			   $(this).parents("tr").remove();
			    			   //$("#extra_" + $(this).attr('id').substr(14)).parents("tr").remove();
			    			   //location.reload();
					       }
				       });
			        },
		       });
	       }
	   }
   });
});

$(function () {
	   $("[id^=modify_module]").click(function() {
		   var rj45 = $("#rj45_num" + $(this).attr('id').substr(13)).val(), fiber = $("#fiber_num" + $(this).attr('id').substr(13)).val(), sum = 0;
		   sum = parseInt(rj45) + parseInt(fiber);
		   /* if($("#brand").val() == "" || $("#model").val() == "" || $("#read_commumity").val() == "" || $("#write_commumity").val() == "" || $("#object_id").val() == "" || $("#inf_idx_cpu").val() == "" || $("#rj45_num").val() == "" || $("#fiber_num").val() == "" || $("#jack_list").val() == "" || $("#speed_list").val()== "" ){
			   alert("Have null fields!");
		   }else  */if($("#inf_num" + $(this).attr('id').substr(13)).val() < sum){
			   alert("INF Number is error!");
		   }else{
			   var module = {
					   brand: $("#brand" + $(this).attr('id').substr(13)).val(),
					   model: $("#model" + $(this).attr('id').substr(13)).val(),
					   device_type: $("#device_type" + $(this).attr('id').substr(13)).val(),
					   snmp_sup: $("#snmp_sup" + $(this).attr('id').substr(13)).val(),
					   snmp_timeout: $("#snmp_timeout" + $(this).attr('id').substr(13)).val(),
					   revision : $("#revision" + $(this).attr('id').substr(13)).val(),
					   read_commumity: $("#read_commumity" + $(this).attr('id').substr(13)).val(),
					   write_commumity: $("#write_commumity" + $(this).attr('id').substr(13)).val(),
					   object_id: $("#object_id" + $(this).attr('id').substr(13)).val(),
					   inf_idx_cpu: $("#inf_idx_cpu" + $(this).attr('id').substr(13)).val(),
					   inf_num: $("#inf_num" + $(this).attr('id').substr(13)).val(),
					   rj45_num: $("#rj45_num" + $(this).attr('id').substr(13)).val(),
					   fiber_num: $("#fiber_num" + $(this).attr('id').substr(13)).val(),
					   jack_list: $("#jack_list" + $(this).attr('id').substr(13)).val(),
					   speed_list: $("#speed_list" + $(this).attr('id').substr(13)).val(),
					   host_resource: $("#host_resource" + $(this).attr('id').substr(13)).val(),
					   sup_linkstate: $("#sup_linkstate" + $(this).attr('id').substr(13)).val(),
					   sup_negostate: $("#sup_negostate" + $(this).attr('id').substr(13)).val(),
					   sup_rxtxoctet: $("#sup_rxtxoctet" + $(this).attr('id').substr(13)).val(),
					   sup_pkttype: $("#sup_pkttype" + $(this).attr('id').substr(13)).val(),
					   sup_rmon: $("#sup_rmon" + $(this).attr('id').substr(13)).val(),
					   sup_pvid: $("#sup_pvid" + $(this).attr('id').substr(13)).val(),
					   sup_vlan: $("#sup_vlan" + $(this).attr('id').substr(13)).val(),
					   sup_gvrp: $("#sup_gvrp" + $(this).attr('id').substr(13)).val(),
					   sup_poe: $("#sup_poe" + $(this).attr('id').substr(13)).val(),
					   sup_trap: $("#sup_trap" + $(this).attr('id').substr(13)).val(),
					   sup_lldp: $("#sup_lldp" + $(this).attr('id').substr(13)).val(),
					   sup_rstp: $("#sup_rstp" + $(this).attr('id').substr(13)).val(),
					   sup_mstp: $("#sup_mstp" + $(this).attr('id').substr(13)).val(),
					   sup_edgecoreresource: $("#sup_edgecoreresource" + $(this).attr('id').substr(13)).val(),
					   sup_octet64: $("#sup_octet64" + $(this).attr('id').substr(13)).val(),
					   sup_egco_trap: $("#sup_egco_trap" + $(this).attr('id').substr(13)).val()
			   };
			   
			   $.ajax({
	    	       type: "POST",
	    	       url: "Settings?action=module_set",
	    	       data:module,
	    	       success:function(data){
	    	    	   alert(data);
	    	    	   location.reload();
	    	       },
			   });
		   }
	   });
});

$(function() {
	$("[id^=jack_list]").click(function(event) {
		event.preventDefault();
		if($("#inf_num" + $(this).attr('id').substr(9)).val()==""){
			alert("Please input INF number!");
		}else{
		var inf_num = $("#inf_num" + $(this).attr('id').substr(9)).val();
		/* if ($("#jack_select_div").is( ":visible" )){
			$("#jack_select_div").hide();
			$("#jack_button_div").hide();
		} else { */
        	$("#jack_select_div" + $(this).attr('id').substr(9)).show();
        	$("#jack_button_div" + $(this).attr('id').substr(9)).show();
        	$("#jack_select_div" + $(this).attr('id').substr(9)).empty();

        	var name = "jack_select" + $(this).attr('id').substr(9);
        	for(var i=1; i<=inf_num; i++){
        	$("#jack_select_div" + $(this).attr('id').substr(9)).append("<select name='" + name + "'><option value='rj'>RJ45</option><option value='fb'>FIBER</option><option value='un'>NONE</option>");
        	}
        //}
		}
	});
});

$(function() {
	$("[id^=jack_apply]").click(function() {
		var jackArray = [];
		var name = "jack_select" + $(this).attr('id').substr(10);
		$("[name='"+ name +"']").each(function() {
			jackArray.push($(this).val());
		});
		$("#jack_list" + $(this).attr('id').substr(10)).val(jackArray);
		$("#jack_select_div" + $(this).attr('id').substr(10)).hide();
		$("#jack_button_div" + $(this).attr('id').substr(10)).hide();
	});
});

$(function() {
	$("[id^=jack_rj45]").click(function() {
		var name = "jack_select" + $(this).attr('id').substr(9);
		$("[name='"+ name +"']").val('rj');
	});
});

$(function() {
	$("[id^=jack_fiber]").click(function() {
		var name = "jack_select" + $(this).attr('id').substr(10);
		$("[name='"+ name +"']").val('fb');
	});
});

$(function() {
	$("[id^=jack_none]").click(function() {
		var name = "jack_select" + $(this).attr('id').substr(9);
		$("[name='"+ name +"']").val('un');
	});
});

$(function() {
	$("[id^=speed_list]").click(function(event) {
		event.preventDefault();
		if($("#inf_num" + $(this).attr('id').substr(10)).val()==""){
			alert("Please input INF number!");
		}else{
		var inf_num = $("#inf_num" + $(this).attr('id').substr(10)).val();
		/* if ($("#jack_select_div").is( ":visible" )){
			$("#jack_select_div").hide();
			$("#jack_button_div").hide();
		} else { */
        	$("#speed_select_div" + $(this).attr('id').substr(10)).show();
        	$("#speed_button_div" + $(this).attr('id').substr(10)).show();
        	$("#speed_select_div" + $(this).attr('id').substr(10)).empty();
        	
        	var name = "speed_select" + $(this).attr('id').substr(10);
        	for(var i=1; i<=inf_num; i++){
        	$("#speed_select_div" + $(this).attr('id').substr(10)).append("<select name='" + name + "'><option value='10'>10MB</option><option value='100'>100MB</option><option value='1000'>1GB</option><option value='10000'>10GB</option>");
        	}
        //}
		}
	});
});

$(function() {
	$("[id^=speed_apply]").click(function() {
		var speedArray = [];
		var name = "speed_select" + $(this).attr('id').substr(11);
		$("[name='" + name + "']").each(function() {
			speedArray.push($(this).val());
		});
		$("#speed_list" + $(this).attr('id').substr(11)).val(speedArray);
		$("#speed_select_div" + $(this).attr('id').substr(11)).hide();
		$("#speed_button_div" + $(this).attr('id').substr(11)).hide();
	});
});

$(function() {
	$("[id^=speed_10]").click(function() {
		var name = "speed_select" + $(this).attr('id').substr(8);
		$("[name='" + name + "']").val('10');
	});
});

$(function() {
	$("[id^=speed_100]").click(function() {
		var name = "speed_select" + $(this).attr('id').substr(9);
		$("[name='" + name + "']").val('100');
	});
});

$(function() {
	$("[id^=speed_1000]").click(function() {
		var name = "speed_select" + $(this).attr('id').substr(10);
		$("[name='" + name + "']").val('1000');
	});
});

$(function() {
	$("[id^=speed_10000]").click(function() {
		var name = "speed_select" + $(this).attr('id').substr(11);
		$("[name='" + name + "']").val('10000');
	});
});

$(function() {
    $('#addDeviceBtu').click(function() {
    	$('#brand').val("");
    	$('#model').val("");
    	$('#device_type').val("l2switch");
    	$('#snmp_sup').val("0");
    	$('#snmp_timeout').val("2000");
    	$('#revision').val("");
    	$('#write_commumity').val("");
    	$('#object_id').val("");
    	$('#inf_idx_cpu').val("");
    	$('#inf_num').val("");
    	$('#rj45_num').val("");
    	$('#fiber_num').val("");
    	$('#jack_list').val("");
    	$('#speed_list').val("");
    	$('#host_resource').val("T");
    	$('#sup_linkstate').val("T");
    	$('#sup_negostate').val("T");
    	$('#sup_rxtxoctet').val("T");
    	$('#sup_pkttype').val("T");
    	$('#sup_rmon').val("T");
    	$('#sup_pvid').val("T");
    	$('#sup_vlan').val("T");
    	$('#sup_gvrp').val("T");
    	$('#sup_poe').val("T");
    	$('#sup_trap').val("F");
    	$('#sup_lldp').val("T");
    	$('#sup_rstp').val("T");
    	$('#sup_mstp').val("T");
    	$('#sup_edgecoreresource').val("T");
    	$('#sup_octet64').val("T");
    	$('#sup_egco_trap').val("T");
    });
});

$(function() {
    $('[id^=setDeviceBtu]').click(function() {
    	$('#brand' + $(this).attr('id').substr(12)).val($('#vBrand' + $(this).attr('id').substr(12)).html());
    	$('#model' + $(this).attr('id').substr(12)).val($('#vModel' + $(this).attr('id').substr(12)).html());
    	$('#device_type' + $(this).attr('id').substr(12)).val($('#vDeviceType' + $(this).attr('id').substr(12)).html());
    	$('#snmp_sup' + $(this).attr('id').substr(12)).val($('#vSnmpSupport' + $(this).attr('id').substr(12)).html());
    	$('#snmp_timeout' + $(this).attr('id').substr(12)).val($('#vSnmpTimeout' + $(this).attr('id').substr(12)).html());
    	$('#revision' + $(this).attr('id').substr(12)).val($('#vRevision' + $(this).attr('id').substr(12)).html());
    	$('#write_commumity' + $(this).attr('id').substr(12)).val($('#vWriteCommunity' + $(this).attr('id').substr(12)).html());
    	$('#object_id' + $(this).attr('id').substr(12)).val($('#vObjectId' + $(this).attr('id').substr(12)).html());
    	$('#inf_idx_cpu' + $(this).attr('id').substr(12)).val($('#vInfCpuIndex' + $(this).attr('id').substr(12)).html());
    	$('#inf_num' + $(this).attr('id').substr(12)).val($('#vInfNum' + $(this).attr('id').substr(12)).html());
    	$('#rj45_num' + $(this).attr('id').substr(12)).val($('#vRj45Num' + $(this).attr('id').substr(12)).html());
    	$('#fiber_num' + $(this).attr('id').substr(12)).val($('#vFiberNum' + $(this).attr('id').substr(12)).html());
    	$('#jack_list' + $(this).attr('id').substr(12)).val($('#vJackList' + $(this).attr('id').substr(12)).html().replace(/\s/g,''));
    	$('#speed_list' + $(this).attr('id').substr(12)).val($('#vSpeedList' + $(this).attr('id').substr(12)).html().replace(/\s/g,''));
    	$('#host_resource' + $(this).attr('id').substr(12)).val($('#vHostResource' + $(this).attr('id').substr(12)).html() == "true" ? "T" : "F");
    });
});
$(function() {
    $('#version_set').click(function() {
    	var data = {
    			version:  $('#change_version').val()
    			};
    	
    	$.ajax({
            url: "Settings?action=version_set",
            type: "post",
            data: data,
            success: function(data) {
            	alert("Success");
            }
    	});
    });
});
</script>
</head>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<c:if test="${sessionScope.userLevel>0}">
        <c:redirect url="login.jsp"></c:redirect>
    </c:if>

	<jsp:include page="/Settings">
	        <jsp:param value="module_get" name="action"/>
	</jsp:include>
    <div id="page_content">
    	<div class="setting_title">Host Network Settings</div>
        <table class="setting_table">
            <tr>
                <td class="">Interface Configuration：</td>
                <td><button class="btn btn-primary btn-xs" onclick="window.location = 'interfaces.jsp';">Setting</button><span style="margin-left: 5px;">(change page)</span></td>
            </tr>
            <tr>
                <td class="">Routing Table Configuration：</td>
                <td><button class="btn btn-primary btn-xs" onclick="window.location = 'route.jsp';">Setting</button><span style="margin-left: 5px;">(change page)</span></td>
            </tr>
        </table>
        <br>
        <div class="setting_title">Version Settings</div>
        <table class="setting_table">
			<tr>
				<td>Change Version</td>
				<td><input type="text" id="change_version" value="<%= Config.getNmsType() %>"></td>
				<td><button class="btn btn-primary btn-xs" id="version_set">set</button></td>
			</tr>
		</table>
		<br>
		<div id="page_title">Model List</div>
		<div class="devBtuGroup">
			<button class="btn btn-primary btn-sm" id="addDeviceBtu" data-toggle="modal" data-target=".showAddDiv" style="width:60px;">Add</button>
			<button class="btn btn-primary btn-sm" id="deleteDeviceBtu" style="width:60px;">Delete</button>
		</div>
			<div id="module_section">
			    <table class="module_getTable">
					<tr class="moduleTable_th">
					    <th>Delete</th>
					    <th>Setting</th>
						<th>Brand</th>
						<th>Model</th>
						<th>Revision</th>
						<th>Device Type</th>
						<th>SNMP</th>
						<!-- <th>Read Community</th> -->
						<th>Write Community</th>
						<th>Object Id</th>
						<th>CPU Index</th>
						<th>Snmp Timeout</th>
						<th>Total Number</th>
						<th>Rj45 Number</th>
						<th>Fiber Number</th>
						<th>IfType list</th>
						<th>Jack list</th>
						<th>Speed list</th>
						<th>Host Resource</th>
						<th>Link State</th>
						<th>Nego State</th>
						<th>RXTX Octet</th>
						<th>Packet Type</th>
						<th>RMON</th>
						<th>PVID</th>
						<th>VLAN</th>
						<th>GVRP</th>
						<th>POE</th>
						<th>TRAP</th>
						<th>LLDP</th>
						<th>RSTP</th>
						<th>MSTP</th>
						<th>EdgeCore Resource</th>
						<th>64-bit Octet</th>
						<th>EdgeCore Trap</th>
					</tr>
					<c:forEach var="item" items="${moduleTable}" varStatus="status">
					<tr class="moduleTable_tr">
						<td><input type="checkbox" id="deleteDevices_${status.count}" name="deleteDevices" value="${item.modelName}"<c:if test="${item.isDefault()}">disabled</c:if>></td>
						<td><button class="btn btn-default btn-sm" data-toggle="modal" data-target=".devShow${status.count}" id="setDeviceBtu${status.count}">Setting</button></td>
						<td><div id="vBrand${status.count}">${item.brandName}</div></td>
						<td><div id="vModel${status.count}">${item.modelName}</div></td>
						<td><div id="vRevision${status.count}">${item.modelRevision}</div></td>
						<td><div id="vDeviceType${status.count}">${item.deviceType}</div></td>
						<td><div id="vSnmpSupport${status.count}">${item.snmpSupport}</div></td>
						<%-- <td>${item.readCommunity}</td> --%>
						<td><div id="vWriteCommunity${status.count}">${item.writeCommunity}</div></td>
						<td><div id="vObjectId${status.count}">${item.objectId}</div></td>
						<td><div id="vInfCpuIndex${status.count}">${item.infCpuIndex}</div></td>
						<td><div id="vSnmpTimeout${status.count}">${item.snmpTimeout}</div></td>
						<td><div id="vInfNum${status.count}">${item.infNum}</div></td>
						<td><div id="vRj45Num${status.count}">${item.rj45Num}</div></td>
						<td><div id="vFiberNum${status.count}">${item.fiberNum}</div></td>
						<td><div class="td_filter" id="vIfTypeList${status.count}">
						<c:forEach var="ifType" items="${item.ifTypeList}">
						${ifType},
						</c:forEach>
						</div></td>
						<td><div class="td_filter" id="vJackList${status.count}">
						<c:forEach var="jack" items="${item.jackList}">
						${jack},
						</c:forEach>
						</div></td>
						<td><div class="td_filter" id="vSpeedList${status.count}">
						<c:forEach var="speed" items="${item.speedList}">
						${speed},
						</c:forEach>
						</div></td>
						<td><div id="vHostResource${status.count}">${item.isSupHostResource()}</div></td>
						<td><div id="vLinkState${status.count}">${item.isSupLinkState()}</div></td>
						<td><div id="vNegoState${status.count}">${item.isSupNegoState()}</div></td>
						<td><div id="vRxTxOctet${status.count}">${item.isSupRxTxOctet()}</div></td>
						<td><div id="vPacketType${status.count}">${item.isSupPacketType()}</div></td>
						<td><div id="vRmon${status.count}">${item.isSupRmon()}</div></td>
						<td><div id="vPvid${status.count}">${item.isSupPvid()}</div></td>
						<td><div id="vVlan${status.count}">${item.isSupVlan()}</div></td>
						<td><div id="vGvrp${status.count}">${item.isSupGvrp()}</div></td>
						<td><div id="vPoe${status.count}">${item.isSupPoe()}</div></td>
						<td><div id="vTrap${status.count}">${item.isSupTrap()}</div></td>
						<td><div id="vLldp${status.count}">${item.isSupLldp()}</div></td>
						<td><div id="vRstp${status.count}">${item.isSupRStp()}</div></td>
						<td><div id="vMstp${status.count}">${item.isSupMStp()}</div></td>
						<td><div id="vEdgeCoreResource${status.count}">${item.isSupEdgeCoreResource()}</div></td>
						<td><div id="vOctet64${status.count}">${item.isSupOctet64()}</div></td>
						<td><div id="vEgcoTrap${status.count}">${item.isSupEgcoTrap()}</div></td>
					</tr>
					</c:forEach>
				</table>
				<c:forEach var="item" items="${moduleTable}" varStatus="status">
					<div class="modal fade devShow${status.count}" tabindex="-1" role="dialog" aria-labelledby="tx" aria-hidden="true">
					  <div class="modal-dialog modal-lg">
					    <div class="modal-content">
					    <input type="hidden" id="oid${status.count}" value="${item.modelName}">
					    <div class="modal-header">
			        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
			          		<h3 class="modal-title">Model: ${item.modelName} Setting</h3>
			          	</div>
			          	<div id="page_title">Model Setting</div>
						<div id="module_section">
						    <div class="module_settingTable">
							<table class="module_settingTable">
								<tr><th>Model Setting</th></tr>
								<tr><td>Model</td><td>${item.modelName}</td>
								<tr><td>Brand</td><td><input type="text" id="brand${status.count}" value="${item.brandName}"></td></tr>
								<tr><td>Device Type</td><td><select id="device_type${status.count}">
								    	                        <option value="l2switch" <c:if test="${item.deviceType == 'l2switch'}">selected</c:if>>L2 Switch</option>
								    	                        <option value="l3switch" <c:if test="${item.deviceType == 'l3switch'}">selected</c:if>>L3 Switch</option>
								    	                        <option value="wlanAC" <c:if test="${item.deviceType == 'wlanAC'}">selected</c:if>>AC</option>
								    	                        <option value="wlanAP" <c:if test="${item.deviceType == 'wlanAP'}">selected</c:if>>AP</option>
								    	                        <option value="firewall" <c:if test="${item.deviceType == 'firewall'}">selected</c:if>>Firewall</option>
								                                <option value="server" <c:if test="${item.deviceType == 'server'}">selected</c:if>>Server</option>
								                                <option value="pc" <c:if test="${item.deviceType == 'pc'}">selected</c:if>>PC</option>
								                                <option value="internet" <c:if test="${item.deviceType == 'internet'}">selected</c:if>>Internet</option>
								                                <option value="NMS" <c:if test="${item.deviceType == 'NMS'}">selected</c:if>>NMS</option>
								                                <option value="MGVChiefServer" <c:if test="${item.deviceType == 'MGVChiefServer'}">selected</c:if>>MGV Chief Server</option>
								                                <option value="MGVCommandServer" <c:if test="${item.deviceType == 'MGVCommandServer'}">selected</c:if>>MGV Command Server</option>
								                                <option value="MGVPlayer" <c:if test="${item.deviceType == 'MGVPlayer'}">selected</c:if>>MGV Player</option>
									                        </select></td></tr>
								<tr><td>SNMP</td><td><select id="snmp_sup${status.count}">
								    	                        <option value="0" <c:if test="${item.snmpSupport == '0'}">selected</c:if>>Unavailable</option>
								    	                        <option value="1" <c:if test="${item.snmpSupport == '1'}">selected</c:if>>SNMPv1</option>
								    	                        <option value="2" <c:if test="${item.snmpSupport == '2'}">selected</c:if>>SNMPv2c</option>
								    	                        <option value="3" <c:if test="${item.snmpSupport == '3'}">selected</c:if>>SNMPv3</option>
								    	                     </select></td></tr>
								<tr><td>Snmp Timeout:</td><td><select id="snmp_timeout${status.count}">
																<option value="1000" <c:if test="${item.snmpTimeout == '1000'}">selected</c:if>>1sec</option>
																<option value="2000" <c:if test="${item.snmpTimeout == '2000'}">selected</c:if>>2sec</option>
																<option value="3000" <c:if test="${item.snmpTimeout == '3000'}">selected</c:if>>3sec</option>
																<option value="4000" <c:if test="${item.snmpTimeout == '4000'}">selected</c:if>>4sec</option>
																<option value="5000" <c:if test="${item.snmpTimeout == '5000'}">selected</c:if>>5sec</option>
																<option value="6000" <c:if test="${item.snmpTimeout == '6000'}">selected</c:if>>6sec</option>
																<option value="7000" <c:if test="${item.snmpTimeout == '7000'}">selected</c:if>>7sec</option>
																<option value="8000" <c:if test="${item.snmpTimeout == '8000'}">selected</c:if>>8sec</option>
																<option value="9000" <c:if test="${item.snmpTimeout == '9000'}">selected</c:if>>9sec</option>
																<option value="10000" <c:if test="${item.snmpTimeout == '10000'}">selected</c:if>>10sec</option>
															   </select></td></tr>
								<tr><td>Revision</td><td><input type="text" id="revision${status.count}" value="${item.modelRevision}"></td></tr>
								<!-- <tr><td>Read Community</td><td><input type="text" id="read_commumity${status.count}" value="${item.readCommunity}"></td></tr> -->
								<tr><td>Write Community</td><td><input type="text" id="write_commumity${status.count}" value="${item.writeCommunity}"></td></tr>
								<tr><td>Object Id</td><td><input type="text" id="object_id${status.count}" value="${item.objectId}"></td>
								<tr><th>Interface</th></tr>
								<tr><td>CPU Index</td><td><input type="text" id="inf_idx_cpu${status.count}" value="${item.infCpuIndex}"></td></tr>
								<tr><td>Total Number</td><td><input type="text" id="inf_num${status.count}" value="${item.infNum}"></td></tr>
								<tr><td>Rj45 Number</td><td><input type="text" id="rj45_num${status.count}" value="${item.rj45Num}"></td></tr>
								<tr><td>Fiber Number</td><td><input type="text" id="fiber_num${status.count}" value="${item.fiberNum}"></td></tr>
								<tr><td>Jack list</td><td><input type="text" id="jack_list${status.count}" value="<c:forEach var="jack" items="${item.jackList}">${jack},</c:forEach>"></td></tr>
								<tr><td colspan=2><div id="jack_select_div${status.count}" style="display: none;"></div><div id="jack_button_div${status.count}" style="display: none;"><button id='jack_apply${status.count}'>Apply</button><button id="jack_rj45${status.count}">RJ45</button><button id="jack_fiber${status.count}">FIBER</button><button id="jack_none${status.count}">NONE</button></div></td></tr>
								<tr><td>Speed list</td><td><input type="text" id="speed_list${status.count}" value="<c:forEach var="speed" items="${item.speedList}">${speed},</c:forEach>"></td></tr>
								<tr><td colspan=2><div id="speed_select_div${status.count}" style="display: none;"></div><div id="speed_button_div${status.count}" style="display: none;"><button id='speed_apply${status.count}'>Apply</button><button id="speed_10${status.count}">10MB</button><button id="speed_100${status.count}">100MB</button><button id="speed_1000${status.count}">1GB</button><button id="speed_10000${status.count}">10GB</button></div></td></tr>
								<tr><th>Support</th></tr>
								<tr><td>Host Resource</td><td><select id="host_resource${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupHostResource()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupHostResource()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>Link State</td><td><select id="sup_linkstate${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupLinkState()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupLinkState()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>Nego State</td><td><select id="sup_negostate${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupNegoState()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupNegoState()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>RXTX Octet</td><td><select id="sup_rxtxoctet${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupRxTxOctet()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupRxTxOctet()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>Packet Type</td><td><select id="sup_pkttype${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupPacketType()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupPacketType()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>RMON</td><td><select id="sup_rmon${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupRmon()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupRmon()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>PVID</td><td><select id="sup_pvid${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupPvid()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupPvid()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>VLAN</td><td><select id="sup_vlan${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupVlan()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupVlan()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>GVRP</td><td><select id="sup_gvrp${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupGvrp()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupGvrp()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>POE</td><td><select id="sup_poe${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupPoe()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupPoe()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>TRAP</td><td><select id="sup_trap${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupTrap()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupTrap()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>LLDP</td><td><select id="sup_lldp${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupLldp()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupLldp()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>RSTP</td><td><select id="sup_rstp${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupRStp()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupRStp()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>MSTP</td><td><select id="sup_mstp${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupMStp()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupMStp()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>EdgeCode Resource</td><td><select id="sup_edgecoreresource${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupEdgeCoreResource()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupEdgeCoreResource()}">selected</c:if>>No</option>
								    	                     </select></td></tr>    	                     
								<tr><td>64-bit Octet</td><td><select id="sup_octet64${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupOctet64()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupOctet64()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
								<tr><td>EdgeCode Trap</td><td><select id="sup_egco_trap${status.count}">
								    	                        <option value="T" <c:if test="${item.isSupEgcoTrap()}">selected</c:if>>Yes</option>
								    	                        <option value="F" <c:if test="${!item.isSupEgcoTrap()}">selected</c:if>>No</option>
								    	                     </select></td></tr>
							</table>
							<button class="btn btn-primary btn-xs" id="modify_module${status.count}">Setting</button>
							</div>
						</div>
			          	</div>
			          </div>
			        </div>
				</c:forEach>
			</div>
			<!-- Model add -->
			<div class="modal fade showAddDiv" tabindex="-1" role="dialog" aria-labelledby="showAddDiv" aria-hidden="true" id="blockAddDiv_l">
			  <div class="modal-dialog modal-lg" id="blockAddDiv_s">
			    <div class="modal-content">
			    <div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
	          		<h3 class="modal-title">Add</h3>
	          	</div>
	          	<div id="page_title">Model Add</div>
				<div id="module_section">
				    <div class="module_settingTable">
					<table class="module_settingTable">
						<tr><th>Model Setting</th></tr>
						<tr><td>Brand</td><td><input type="text" id="brand"></td></tr>
						<tr><td>Model</td><td><input type="text" id="model"> <button class="btn btn-primary btn-xs" id="check_modelName">Check</button></td></tr>
						<tr><td>Device Type</td><td><select id="device_type">
						    	                        <option value="l2switch">L2 Switch</option>
						    	                        <option value="l3switch">L3 Switch</option>
						    	                        <option value="wlanAC">AC</option>
						    	                        <option value="wlanAP">AP</option>
						    	                        <option value="firewall">Firewall</option>
						                                <option value="server">Server</option>
						                                <option value="pc">PC</option>
						                                <option value="internet">Internet</option>
						                                <option value="NMS">NMS</option>
						                                <option value="MGVChiefServer">MGV Chief Server</option>
						                                <option value="MGVCommandServer">MGV Command Server</option>
						                                <option value="MGVPlayer">MGV Player</option>
							                        </select></td></tr>
						<tr><td>SNMP</td><td><select id="snmp_sup">
						    	                        <option value="0">Unavailable</option>
						    	                        <option value="1">SNMPv1</option>
						    	                        <option value="2">SNMPv2c</option>
						    	                        <option value="3">SNMPv3</option>
						    	                     </select></td></tr>
						<tr><td>Snmp Timeout:</td><td><select id="snmp_timeout">
														          <option value="1000">1sec</option>
														          <option value="2000" selected>2sec</option>
														          <option value="3000">3sec</option>
														          <option value="4000">4sec</option>
														          <option value="5000">5sec</option>
														          <option value="6000">6sec</option>
														          <option value="7000">7sec</option>
														          <option value="8000">8sec</option>
														          <option value="9000">9sec</option>
														          <option value="10000">10sec</option>
													   </select></td></tr>
						<tr><td>Revision</td><td><input type="text" id="revision"></td></tr>
						<!-- <tr><td>Read Community</td><td><input type="text" id="read_commumity"></td></tr> -->
						<tr><td>Write Community</td><td><input type="text" id="write_commumity"></td></tr>
						<tr><td>Object Id</td><td><input type="text" id="object_id"></td></tr>
						<tr><th>Interface</th></tr>
						<tr><td>CPU Index</td><td><input type="text" id="inf_idx_cpu"></td></tr>
						<tr><td>Total Number</td><td><input type="text" id="inf_num"></td></tr>
						<tr><td>Rj45 Number</td><td><input type="text" id="rj45_num"></td></tr>
						<tr><td>Fiber Number</td><td><input type="text" id="fiber_num"></td></tr>
						<tr><td>Jack list</td><td><input type="text" id="jack_list"></td></tr>
						<tr><td colspan=2><div id="jack_select_div" style="display: none;"></div><div id="jack_button_div" style="display: none;"><button id='jack_apply'>Apply</button><button id="jack_rj45">RJ45</button><button id="jack_fiber">FIBER</button><button id="jack_none">NONE</button></div></td></tr>
						<tr><td>Speed list</td><td><input type="text" id="speed_list"></td></tr>
						<tr><td colspan=2><div id="speed_select_div" style="display: none;"></div><div id="speed_button_div" style="display: none;"><button id='speed_apply'>Apply</button><button id="speed_10">10MB</button><button id="speed_100">100MB</button><button id="speed_1000">1GB</button><button id="speed_10000">10GB</button></div></td></tr>
						<tr><th>Support</th></tr>
						<tr><td>Host Resource</td><td><select id="host_resource">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>Link State</td><td><select id="sup_linkstate">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>Nego State</td><td><select id="sup_negostate">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>RXTX Octet</td><td><select id="sup_rxtxoctet">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>Packet Type</td><td><select id="sup_pkttype">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>RMON</td><td><select id="sup_rmon">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>PVID</td><td><select id="sup_pvid">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>VLAN</td><td><select id="sup_vlan">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>GVRP</td><td><select id="sup_gvrp">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>POE</td><td><select id="sup_poe">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>TRAP</td><td><select id="sup_trap">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F" selected>No</option>
						    	                     </select></td></tr>
						<tr><td>LLDP</td><td><select id="sup_lldp">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>RSTP</td><td><select id="sup_rstp">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>MSTP</td><td><select id="sup_mstp">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>EdgeCode Resource</td><td><select id="sup_edgecoreresource">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>    	                     
						<tr><td>64-bit Octet</td><td><select id="sup_octet64">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
						<tr><td>EdgeCode Trap</td><td><select id="sup_egco_trap">
						    	                        <option value="T">Yes</option>
						    	                        <option value="F">No</option>
						    	                     </select></td></tr>
					</table>
					<button class="btn btn-primary btn-xs" id="module_add">Setting</button>
					</div>
				</div>
				</div>
				</div>
			</div>
		</div>
</div>
</body>
</html>
