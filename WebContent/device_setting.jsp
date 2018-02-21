<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device Setting</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>

	<jsp:include page="/Device">
		<jsp:param value="pingManaged" name="method"/>
	</jsp:include>
	
		<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
		<c:if test="${dev.publicIp==param.ip}">
		
			            <%-- <div class="extradiv" id="extra_${status.count}" style="display: none;"> --%>
			                <div id="tabs_${status.count}">
			                <%-- Device IP : ${dev.publicIp} Setting --%>
								<ul>
									<li><a href="#tabs-1">Topology</a></li>
									<c:if test="${dev.snmpSupport>0}">
										<li><a href="#tabs-2">Port status</a></li>
									</c:if>
									<c:if test="${dev.supportVlan}">
										<li><a href="#tabs-3">Vlan</a></li>
									</c:if>
									<c:if test="${dev.supportPoe}">
										<li><a href="#tabs-4">PoE</a></li>
									</c:if>
									<%-- <c:if test="${dev.supportPoe}">
										<li><a href="#tabs-5">PoE Schedule</a></li>
									</c:if> --%>
									<c:if test="${dev.supportTrap}">
										<li><a href="#tabs-6">Monitor</a></li>
									</c:if>
									<c:if test="${dev.supportEgcoTrap}">
										<li><a href="#tabs-7">Trap</a></li>
									</c:if>
									<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
										<li><a href="#tabs-8">AC Trap Setting</a></li>
									</c:if>
									<li><a href="#tabs-9">Mail Filter</a></li>
									<c:if test="${dev.deviceType == 'l2switch' || dev.deviceType == 'l3switch' || dev.deviceType == 'firewall'}">
										<li><a href="#tabs-10">Alias</a></li>
									</c:if>
									<li><a href="#tabs-11">Other</a></li>
								</ul>
								<input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
								<div id="tabs-1">
									<iframe class="settingIframe" id="topology_set_${status.count}" src="topology_set.jsp?ip=${dev.publicIp}" onLoad="autoResize('topology_set_${status.count}');"></iframe>
								</div>
								<c:if test="${dev.snmpSupport>0}">
								<div id="tabs-2">
									<iframe class="settingIframe" id="port_status_${status.count}" src="port_status.jsp?ip=${dev.publicIp}" onLoad="autoResize('port_status_${status.count}');" scrolling="no"></iframe>
								</div>
								</c:if>
								<c:if test="${dev.supportVlan}">
									<div id="tabs-3">
									    <iframe class="settingIframe" id="vlan_interface_${status.count}" src="vlan_interface.jsp?ip=${dev.publicIp}" onLoad="autoResize('vlan_interface_${status.count}');"></iframe>
									</div>
								</c:if>
								<c:if test="${dev.supportPoe}">
									<div id="tabs-4">
									    <iframe class="settingIframe" id="poe_interface_${status.count}" src="poe_interface.jsp?ip=${dev.publicIp}" onLoad="autoResize('poe_interface_${status.count}');"></iframe>
									</div>
								</c:if>
								<%-- <c:if test="${dev.supportPoe}">
									<div id="tabs-5">
										<iframe class="settingIframe" id="poe_schedule_${status.count}" src="poe_schedule.jsp?ip=${dev.publicIp}" onLoad="autoResize('poe_schedule_${status.count}');"></iframe>
									</div>
								</c:if> --%>
								<c:if test="${dev.supportTrap}">
								<div id="tabs-6">
									<iframe class="settingIframe" id="port_monitor_${status.count}" src="port_monitor.jsp?ip=${dev.publicIp}" onLoad="autoResize('port_monitor_${status.count}');"></iframe>
								</div>
								</c:if>
								<c:if test="${dev.supportEgcoTrap}">
									<div id="tabs-7">
										<iframe class="settingIframe" id="trap_table_${status.count}" src="trap_table.jsp?ip=${dev.publicIp}" onLoad="autoResize('trap_table_${status.count}');"></iframe>
									</div>
								</c:if>
								<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
									<div id="tabs-8">
										<iframe class="settingIframe" id="ac_trap_${status.count}" src="ac_trap.jsp?ip=${dev.publicIp}&mac=${dev.phyAddr}" onLoad="autoResize('ac_trap_${status.count}');"></iframe>
									</div>
								</c:if>
								<div id="tabs-9">
									<iframe class="settingIframe" id="port_filter_${status.count}" src="port_filter.jsp?ip=${dev.publicIp}" onLoad="autoResize('port_filter_${status.count}');"></iframe>
								</div>
								<c:if test="${dev.deviceType == 'l2switch' || dev.deviceType == 'l3switch' || dev.deviceType == 'firewall'}">
									<div id="tabs-10">
										<iframe class="settingIframe" id="port_alias_${status.count}" src="port_alias.jsp?ip=${dev.publicIp}" onLoad="autoResize('port_alias_${status.count}');"></iframe>
									</div>
								</c:if>
								<div id="tabs-11">
									<%-- <div class="otherSettingTitle">Alias</div>
									<div class="otherSettingDiv">
									    <table class="settingTable">
										    <tr>
											    <td>Alias setting:</td>
											    <td><input type="text" class="settingTextLen" id="alias_${status.count}" value="${dev.aliasName}">
											    </td><td><button class="btn btn-primary btn-xs" id="alias_set_${status.count}">set</button></td>
											</tr>
										</table>
									</div>
									<br> --%>
									<c:if test="${dev.snmpSupport>0 && (dev.deviceType == 'l2switch' || dev.deviceType == 'l3switch')}">
									<div class="otherSettingTitle">Snmp</div>
									<div class="snmpDiv">
									<table class="settingTable">
										<%-- <tr>
											<td>Mac refresh:</td>
											<td><button class="btn btn-primary btn-xs" id="device_refresh_${status.count}">refresh</button></td>
										</tr> --%>
										<tr>
											<td>Snmp Version:</td>
											<td><select id="snmp_version${status.count}">
												  <option value="1"<c:if test="${dev.snmpVersion == '1'}">selected</c:if>>V1</option>
										          <option value="2"<c:if test="${dev.snmpVersion == '2'}">selected</c:if>>V2c</option>
										          <option value="3"<c:if test="${dev.snmpVersion == '3'}">selected</c:if>>V3</option>
												</select></td>
										</tr>
										<tr>
											<td>Snmp Timeout:</td>
											<td><select id="snmp_timeout${status.count}" size="1">
												          <option value="1000"<c:if test="${dev.snmpTimeout == '1000'}">selected</c:if>>1sec</option>
												          <option value="2000"<c:if test="${dev.snmpTimeout == '2000'}">selected</c:if>>2sec</option>
												          <option value="3000"<c:if test="${dev.snmpTimeout == '3000'}">selected</c:if>>3sec</option>
												          <option value="4000"<c:if test="${dev.snmpTimeout == '4000'}">selected</c:if>>4sec</option>
												          <option value="5000"<c:if test="${dev.snmpTimeout == '5000'}">selected</c:if>>5sec</option>
												          <option value="6000"<c:if test="${dev.snmpTimeout == '6000'}">selected</c:if>>6sec</option>
												          <option value="7000"<c:if test="${dev.snmpTimeout == '7000'}">selected</c:if>>7sec</option>
												          <option value="8000"<c:if test="${dev.snmpTimeout == '8000'}">selected</c:if>>8sec</option>
												          <option value="9000"<c:if test="${dev.snmpTimeout == '9000'}">selected</c:if>>9sec</option>
												          <option value="10000"<c:if test="${dev.snmpTimeout == '10000'}">selected</c:if>>10sec</option>
											    </select></td>
										</tr>
									</table>
									<div id="snmpV2Setting">
									<table class="settingTable">
										<tr>
											<td>Read Community:</td>
											<td><input type="text" class="settingTextLen" id="read_community${status.count}" value="${dev.readCommunity}"></td>
										</tr>
										<tr>
											<td>Write Community:</td>
											<td><input type="text" class="settingTextLen" id="write_community${status.count}" value="${dev.writeCommunity}"></td>
										</tr>
									</table>
									</div>
									<div id="snmpV3Setting">
									<table class="settingTable">
										<tr>
											<td>Security Name:</td>
											<td><input type="text" class="settingTextLen" id="security_name${status.count}" value="${dev.securityName}"></td>
										</tr>
										<tr>
											<td>Security Level:</td>
											<td><select id="security_level${status.count}">
												  <option value="1"<c:if test="${dev.securityLevel == '1'}">selected</c:if>>noAuthNoPriv</option>
										          <option value="2"<c:if test="${dev.securityLevel == '2'}">selected</c:if>>AuthNoPriv</option>
										          <option value="3"<c:if test="${dev.securityLevel == '3'}">selected</c:if>>AuthPriv</option>
												</select></td>
										</tr>
										<tr>
											<td>Authentication Protocol:</td>
											<td><select id="auth_protocol${status.count}">
												  <option value="MD5"<c:if test="${dev.authProtocol == 'MD5'}">selected</c:if>>MD5</option>
										          <option value="SHA"<c:if test="${dev.authProtocol == 'SHA'}">selected</c:if>>SHA</option>
												</select></td>
										</tr>
										<tr>
											<td>Authentication Password:</td>
											<td><input type="password" class="settingTextLen" id="auth_password${status.count}" value="${dev.authPassword}"></td>
										</tr>
										<tr>
											<td>Privacy Protocol:</td>
											<td><select id="priv_protocol${status.count}">
												  <option value="DES"<c:if test="${dev.privProtocol == 'DES'}">selected</c:if>>DES</option>
												</select></td>
										</tr>
										<tr>
											<td>Privacy Password:</td>
											<td><input type="password" class="settingTextLen" id="priv_password${status.count}" value="${dev.privPassword}"></td>
										</tr>
									</table>
									</div>
									<table class="settingTable">
										<tr>
											<td ></td>
											<td><button class="btn btn-primary btn-xs" id="snmpUpdate${status.count}">set</button></td>
										</tr>
									</table>
									</div>
									</c:if>
									<c:if test="${dev.snmpSupport == 0}">
									<div class="otherSettingTitle">Device type</div>
										<div class="otherSettingDiv">
										    <table class="settingTable">
											    <tr>
												    <td>Change type:</td>
												    <td><select id="ping_type_${status.count}" <c:if test="${empty pingManagedModule}">disabled</c:if>>
							                                <option value="">Select Device</option>
							                                <c:forEach var="item" items="${pingManagedModule}">
								                            <option value="${item.objectId}">${item.modelName}</option>
								                            </c:forEach>
								                        </select></td>
								                    <td><button class="btn btn-primary btn-xs" id="model_set_${status.count}">set</button></td>
												</tr>
											</table>
										</div>
									</c:if>
								</div>
							</div>
			            <!-- </div> -->
	    </c:if>
		</c:forEach>
</body>
</html>