<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device.css">
<script type="text/javascript" src="theme/jQueryUI/jquery.blockUI.js"></script>
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript" src="theme/datatables/jquery.dataTables.js"></script>
<script type="text/javascript" src="theme/datatables/ip-address.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<script src="theme/jQuery-File-Upload-9.8.0/js/jquery.iframe-transport.js"></script>
<script src="theme/jQuery-File-Upload-9.8.0/js/jquery.fileupload.js"></script>

</head>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div id="area" style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<jsp:include page="/Device">
		<jsp:param value="nonManaged" name="method"/>
	</jsp:include>
	
	<jsp:include page="/Device">
		<jsp:param value="pingManaged" name="method"/>
	</jsp:include>
	
	<jsp:include page="/Device">
		<jsp:param value="profile_items" name="method"/>
	</jsp:include>
	
	<jsp:include page="/Device">
		<jsp:param value="group_view" name="method"/>
	</jsp:include>
	
	<c:set var="nmsType" value="<%= Config.getNmsType() %>"/>
	
	<div id="page_content">
	<c:if test="${sessionScope.userLevel < 2 && nmsType > 1}">
		<div id="page_title">NMS List</div>
			<div class="devBtuGroup">
				<button class="btn btn-primary btn-sm" data-toggle="modal" data-target=".showNmsAdd" style="width:60px;">Add</button>
				<button class="btn btn-primary btn-sm" id="deleteNmsBtu" style="width:60px;">Delete</button>
				<button class="btn btn-primary btn-sm" id="refreshNmsBtu" style="width:60px;">Refresh</button>
			</div>
		<div class="modal fade showNmsAdd" tabindex="-1" role="dialog" aria-labelledby="showNmsAdd" aria-hidden="true" id="blockAddDiv_nms_l">
		  <div class="modal-dialog modal-lg" id="blockAddDiv_nms_s">
		    <div class="modal-content">
		    <div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
          		<h3 class="modal-title">Add Device</h3>
          	</div>
          	<br>
          	<div class="optionTitle">Device Insert</div>
                <div class="optionDiv">
	                <table class="optionTable">
	                    <tr>
	                    	<th>Alias Name</th>
	                        <th>Public IP</th>
	                    </tr>
	                    <tr>
	                    	<td><input type="text" id="nms_alias" style="width:100px;"></td>
	                        <td><input type="text" id="add_nms_ip" value="<%= Config.getDeviceAddLastIP() %>" style="width:100px;"></td>
	                        <td><button id="addNms" class="btn btn-primary btn-xs">Add</button></td>
	                    </tr>
	                </table>
                </div>
            <br>
		</div>
        </div>
        </div>
        
        <table id="nmsTable" class="tableType">
		<thead>
			<tr class="deviceTable_th">
				<th>Redirect</th>
				<th>IP Addr</th>
				<th>Alias</th>
				<th>Total/Online/Offline Device</th>
				<th>Last Update</th>
				<th>Alive</th>
				<th>Setting</th>
			    <th><input type="checkbox" id="selectallNmsDevice" name="deleteAllNmsDevices" <c:if test="${sessionScope.userLevel>1}">disabled</c:if>></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="dev" items="${applicationScope.network.nmsList}" varStatus="status">
				<tr class="deviceTable_tr">
					<td class="td_left_align"><button class="btn btn-default btn-sm" onclick="remoteLogin('${dev.publicIp}')"
					<c:forEach var="remoteChk" items="${fn:split(dev.remoteMember, ',')}">
						<c:if test="${sessionScope.remoteLevel > 0}">
							<c:if test="${fn:toLowerCase(sessionScope.username) != remoteChk}">disabled</c:if>
						</c:if>
					</c:forEach>>${dev.publicIp}</button></td>
					<td class="td_left_align"><a href="javascript:void(0)" onclick="window.open('nms_dashboard.jsp?address=${dev.publicIp}');">${dev.publicIp}</a></td>
					<td class="td_center_align"><div id="nAliasDiv${status.count}">${dev.aliasName}</div><input type="text" id="nAliasText${status.count}" value="${dev.aliasName}">
					<input type="hidden" id="nAliasIp_${status.count}" value="${dev.publicIp}"></td>
					<td class="td_center_align">${dev.totalDeviceNum} / ${dev.onlineDeviceNum} / ${dev.offlineDeviceNum}</td>
					<td class="td_center_align"><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${dev.lastSeen}"/></td>
					<td class="td_center_align">
					   <c:if test="${dev.alive}"><img src="images/ok.png" height="24" width="24" title="ok"></c:if>
					   <c:if test="${!dev.alive}"><img src="images/fail.png" height="24" width="24" title="fail"></c:if>
					</td>
					<td class="td_center_align"><button class="btn btn-default btn-sm" data-toggle="modal" data-target=".nmsDevShow${status.count}" id="nmsShow_${status.count}"<c:if test="${sessionScope.userLevel>1}">disabled</c:if>>Setting</button></td>
				   <td class="deleteOption" class="td_center_align"><input type="checkbox" id="deleteNmsDevices_${status.count}" name="deleteNmsDevices" value="${dev.publicIp}"<c:if test="${sessionScope.userLevel>1}">disabled</c:if>></td>
				</tr>
			</c:forEach>
			<c:forEach var="dev" items="${applicationScope.network.nmsList}" varStatus="status">
				<div class="modal fade nmsDevShow${status.count}" tabindex="-1" role="dialog" aria-labelledby="${status.count}" aria-hidden="true">
				  <div class="modal-dialog modal-lg">
				    <div class="modal-content">
					    <div class="modal-header">
			        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
			          		<h3 class="modal-title">Device: ${dev.publicIp} setting</h3>
			          	</div>
			          	<iframe class="settingIframe" id="nms_setting_${status.count}" data-src="nms_setting.jsp?ip=${dev.publicIp}" src="nms_setting.jsp?ip=${dev.publicIp}" onLoad="autoResize('nms_setting_${status.count}');"></iframe>
		          	</div>
		          	</div>
	          	</div>
          	</c:forEach>
            </tbody>
		</table>
		</c:if>
          	
		<div id="page_title"><c:if test="${sessionScope.userLevel < 2 && nmsType > 1}">Intranet</c:if> Device List</div>
		<c:if test="${sessionScope.userLevel<2}">
			<div class="devBtuGroup">
				<button class="btn btn-primary btn-sm" data-toggle="modal" data-target=".showAddDiv" style="width:60px;">Add</button>
				<button class="btn btn-primary btn-sm" id="deleteDeviceBtu" style="width:60px;">Delete</button>
				<button class="btn btn-primary btn-sm" id="refreshDeviceBtu" style="width:60px;">Refresh</button>
				<div style="float:right;">
					Group Setting : 
					<select id="devGroupSelect">
						<option value="--">--select--</option>
						<c:forEach var="item" items="${groupList}">
							 <option value="${item.name}">${item.name}</option>
		                </c:forEach>
					</select>
					<button class="btn btn-primary btn-sm" id="devGroupApply">set</button>
					<button class="btn btn-primary btn-sm" id="exportDeviceListBtu" style="width:60px;">Export</button>
					<span class="btn btn-primary btn-sm file">
						Import<input id="importDeviceListBtu" type="file" name="file" data-url="Device?method=devicelist_import"/>
					</span>
				</div>
			</div>
			<%-- <div>
			Device Profile Setting : 
			<select id="devFil_select">
				<c:forEach var="item" items="${profileItems}">
					 <option value="${item.profileName}">${item.profileName}</option>
                   </c:forEach>
			</select>
			<button class="btn btn-primary btn-sm" id="devFilApply">set</button>
			</div> --%>
		</c:if>
		<div class="modal fade showAddDiv" tabindex="-1" role="dialog" aria-labelledby="showAddDiv" aria-hidden="true" id="blockAddDiv_l">
		  <div class="modal-dialog modal-lg" id="blockAddDiv_s">
		    <div class="modal-content">
		    <div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
          		<h3 class="modal-title">Add Device</h3>
          	</div>
		      <c:if test="${sessionScope.userLevel<2}">
		      <br>
		      	<table class="addTable">
		      		<tr VALIGN=TOP>
		      			<td>
		      				<div class="selectTitle"><input type="radio" name="version" value="-1" class="selectVersion"> Non-Management</div>
		      				<div class="selectOptionDiv">
			                <table class="selectTable">
			                	<tr>
			                		<th>Select Non-Snmp Device</th>
			                		<td><select id="vn_type" <c:if test="${empty nonManagedModule}">disabled</c:if>>
		                                <option value="">Select Device</option>
		                                <c:forEach var="item" items="${nonManagedModule}">
			                            <option value="${item.objectId}">${item.modelName}</option>
			                            </c:forEach>
			                        </select></td>
			                	</tr>
			                </table>
			                </div>
			                <br>
			                <div class="selectTitle"><input type="radio" name="version" value="0" class="selectVersion"> Ping Only</div>
		      				<div class="selectOptionDiv">
			                <table class="selectTable">
			                	<tr>
			                		<th>Select Ping Only Device</th>
			                		<td><select id="ping_type" <c:if test="${empty pingManagedModule}">disabled</c:if>>
		                                <option value="">Select Device</option>
		                                <c:forEach var="item" items="${pingManagedModule}">
			                            <option value="${item.objectId}">${item.modelName}</option>
			                            </c:forEach>
			                        </select></td>
			                	</tr>
			                </table>
			                </div>
			                <br>
			                <div class="selectTitle"><input type="radio" name="version" value="1" class="selectVersion"> SNMP v1</div>
			                <div class="selectOptionDiv">
			                <table class="selectTable">
			                	<tr>
			                		<th>Community</th><td><input type="text" id="v1_community" value="public" style="width:120px;"></td>
			                	</tr>
			                </table>
			                </div>
			                <br>
			                <div class="selectTitle"><input type="radio" name="version" value="2" class="selectVersion"> SNMP v2c</div>
			                <div class="selectOptionDiv">
			                <table class="selectTable">
			                	<tr>
			                		<th>Community</th><td><input type="text" id="v2_community" value="public" style="width:120px;"></td>
			                	</tr>
			                </table>
			                </div>
			                <br>
			                <div class="selectTitle"><input type="radio" name="version" value="3" class="selectVersion"> SNMP v3</div>
			                <div class="selectOptionDivV3">
			                <table class="selectTable">
			                	<tr>
			                		<th>Security Name</th><td><input type="text" id="security_name" value="" style="width:120px;"></td>
			                	</tr>
			                	<tr>
			                		<th>Security Level</th>
			                        <td><select id="security_level">
												  <option value="1">noAuthNoPriv</option>
										          <option value="2">AuthNoPriv</option>
										          <option value="3">AuthPriv</option>
									</select></td>
			                	</tr>
			                	<tr>
			                		<th>Authentication Protocol</th>
			                		<td><select id="auth_protocol">
												  <option value="MD5">MD5</option>
										          <option value="SHA">SHA</option>
									</select></td>
			                	</tr>
			                	<tr>
			                		<th>Authentication Password</th><td><input type="password" id="auth_password" value="" style="width:120px;"></td>
			                	</tr>
			                	<tr>
			                		<th>Privacy Protocol</th>
			                		<td><select id="priv_protocol">
												  <option value="DES">DES</option>
									</select></td>
			                	</tr>
			                	<tr>
			                		<th>Privacy Password</th><td><input type="password" id="priv_password" value="" style="width:120px;"></td>
			                	</tr>
			                </table>
			                </div>
		      			</td>
		      			<td>
		      				<div class="optionTitle">Device Insert</div>
			                <div class="optionDiv">
			                <table class="optionTable">
			                    <tr>
			                        <th>IP</th>
			                        <th>Port</th>
		                            <th>Timeout</th>
			                    </tr>
			                    <tr>
			                        <td><input type="text" id="add_ip" value="<%= Config.getDeviceAddLastIP() %>" style="width:100px;"></td>
			                        <td><input type="text" id="add_port" value="161" style="width:25px;"></td>
			                        <td><select id="add_snmp_timeout">
										          <option value="1000">1sec</option>
										          <option value="2000" selected >2sec</option>
										          <option value="3000">3sec</option>
										          <option value="4000">4sec</option>
										          <option value="5000">5sec</option>
										          <option value="6000">6sec</option>
										          <option value="7000">7sec</option>
										          <option value="8000">8sec</option>
										          <option value="9000">9sec</option>
										          <option value="10000">10sec</option>
										</select></td>
			                        <td><button id="addDevice" class="btn btn-primary btn-xs">Add</button></td>
			                    </tr>
			                </table>
			                </div>
			                <br>
			                <div class="optionTitle">Device Scan</div>
			                <div class="optionDiv" id="scanOption">
			                <table class="optionTable">
			                    <tr>
			                        <th>Start IP</th>
			                        <th>End IP</th>
		                            <th>Port</th>
		                            <th>Timeout</th>
			                    </tr>
			                    <tr>
			                        <td><input type="text" id="scan_ip_from" value="<%= Config.getDeviceScanRange()[0] %>" style="width:100px;"></td>
			                        <td><input type="text" id="scan_ip_to" value="<%= Config.getDeviceScanRange()[1] %>" style="width:100px;"></td>
			                        <td><input type="text" id="scan_port" value="161" style="width:25px;"></td>
			                        <td><select id="scan_snmp_timeout">
										          <option value="1000">1sec</option>
										          <option value="2000" selected >2sec</option>
										          <option value="3000">3sec</option>
										          <option value="4000">4sec</option>
										          <option value="5000">5sec</option>
										          <option value="6000">6sec</option>
										          <option value="7000">7sec</option>
										          <option value="8000">8sec</option>
										          <option value="9000">9sec</option>
										          <option value="10000">10sec</option>
										</select></td>
			                        <td><button id="scanDevice" class="btn btn-primary btn-xs">Scan</button></td>
			                    </tr>
								<tr>
									<!-- <th>Process:</th> -->
									<td><div id="scandiv" style="display: none;text-align: left;"></div><div id="scandiv_display"></div></td>
								</tr>
							</table>
							</div>
		      			</td>
		      		</tr>
		      	</table>
				<br>
             </c:if>
        </div>
        </div>
        </div>
		
		<table id="deviceTable" class="tableType">
		<thead>
			<tr class="deviceTable_th">
				<th>IP Addr</th>
				<!-- <th rowspan="2">UpTime</th> -->
				<th>Device Type</th>
				<th>Alias</th>
				<th>MAC Addr</th>
				<!-- <th>Eth. Port<br>RJ45/Fiber</th> -->
				<th>Group</th>
				<th>Last Update</th>
				<th>Alive</th>
			    <th>Setting</th>
			    <th><input type="checkbox" id="selectallDevice" name="deleteAllDevices" <c:if test="${sessionScope.userLevel>1}">disabled</c:if>></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
				<tr class="deviceTable_tr">
					<td class="td_left_align">
                        <c:if test="${dev.snmpSupport<1 || dev.deviceType == 'wlanAP'}">
                            ${dev.publicIp}
                        </c:if>
                        <c:if test="${dev.snmpSupport>0}">
                        	<c:if test="${dev.deviceType == 'wlanAC' && dev.sysObjectId == '1.3.6.1.4.1.3742.10.5801.1'}">
                            	<a href="javascript:void(0)" onclick="window.open('wlan_ac_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                        	</c:if>
                            <c:if test="${dev.deviceType == 'firewall' || (dev.deviceType == 'wlanAC' && dev.sysObjectId != '1.3.6.1.4.1.3742.10.5801.1')}">
                                <a href="javascript:void(0)" onclick="window.open('fw_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                            <c:if test="${dev.deviceType == 'l2switch' || dev.deviceType == 'l3switch'}">
                                <a href="javascript:void(0)" onclick="window.open('overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                            <c:if test="${dev.deviceType == 'server' || dev.deviceType == 'NMS' || dev.deviceType == 'pc'}">
                                <a href="javascript:void(0)" onclick="window.open('server_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                            <c:if test="${dev.deviceType == 'MGVChiefServer'}">
                                <a href="javascript:void(0)" onclick="window.open('server_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                            <c:if test="${dev.deviceType == 'MGVCommandServer'}">
                                <a href="javascript:void(0)" onclick="window.open('server_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                            <c:if test="${dev.deviceType == 'MGVPlayer'}">
                                <a href="javascript:void(0)" onclick="window.open('server_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
							<%-- //Del --%>
                            <c:if test="${dev.deviceType == 'MGVServer'}">
                                <a href="javascript:void(0)" onclick="window.open('server_overview.jsp?ip=${dev.publicIp}');">${dev.publicIp}</a>
                            </c:if>
                        </c:if>
					</td>
					<c:choose>
						<c:when test="${dev.deviceType == 'l2switch'}">
							<td class="td_center_align">L2switch</td>
						</c:when>
						<c:when test="${dev.deviceType == 'l3switch'}">
							<td class="td_center_align">L3switch</td>
						</c:when>
						<c:otherwise>
							<td class="td_center_align">${dev.deviceType}</td>
						</c:otherwise>
					</c:choose>
					<%-- <td>${dev.sysUpTime}</td> --%>
					
					<td class="td_center_align"><div id="aliasDiv${status.count}">${dev.aliasName}</div><input type="text" id="aliasText${status.count}" value="${dev.aliasName}">
					<input type="hidden" id="aliasIp_${status.count}" value="${dev.publicIp}"></td>
					<td class="td_center_align"><div id="vPhyAddr${status.count}">${fn:toUpperCase(dev.phyAddr)}</div></td>
					<%-- <td class="td_center_align">${dev.rj45Num}/${dev.fiberNum}</td> --%>
					<td class="td_center_align"><div id="groupDiv${status.count}">${dev.groupName}</div></td>
					<td class="td_center_align"><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${dev.lastSeen}"/></td>
					<td class="td_center_align">
					   <%-- <c:if test="${dev.alive}">Yes</c:if><c:if test="${!dev.alive}">No</c:if> --%>
					   <c:if test="${dev.alive == 0}"><img src="images/fail.png" height="24" width="24" title="fail"></c:if>
					   <c:if test="${dev.alive == 1}"><img src="images/ok.png" height="24" width="24" title="ok"></c:if>
					   <c:if test="${dev.alive == 2}"><img src="images/warning.png" height="28" width="28" title="warning"></c:if>
					</td>
					
				   <td class="td_center_align"><button class="btn btn-default btn-sm" data-toggle="modal" data-target=".devShow${status.count}" id="show_${status.count}"<c:if test="${sessionScope.userLevel>1}">disabled</c:if>>Setting</button></td>
				   <td class="deleteOption" class="td_center_align"><input type="checkbox" id="deleteDevices_${status.count}" name="deleteDevices" value="${dev.publicIp}"<c:if test="${sessionScope.userLevel>1}">disabled</c:if>></td>
				</tr>
				
			</c:forEach>
            </tbody>
		</table>
		<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
		<div class="modal fade devShow${status.count}" tabindex="-1" role="dialog" aria-labelledby="tx${status.count}" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		    <div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only"></span></button>
          		<h3 class="modal-title">Device: ${dev.publicIp} setting</h3>
          	</div>
		    <iframe class="settingIframe" id="device_setting_${status.count}" data-src="device_setting.jsp?ip=${dev.publicIp}" src="about:blank" onLoad="autoResize('device_setting_${status.count}');"></iframe>
			            <%-- <div class="extradiv" id="extra_${status.count}" style="display: none;">
			                <div id="tabs_${status.count}">
			                Device IP : ${dev.publicIp} Setting
								<ul>
									<li><a href="#tabs-1">Port Setting</a></li>
									<li><a href="#tabs-6">Other Setting</a></li>
								</ul>
								<input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
								<div id="tabs-1">
									<iframe class="settingIframe" id="topology_set_${status.count}" data-src="topology_set.jsp?ip=${dev.publicIp}" src="about:blank" onLoad="autoResize('topology_set_${status.count}');"></iframe>
								</div>
								<div id="tabs-6">
								    <table>
									    <tr>
										    <td>Alias setting:</td>
										    <td><input type="text" class="settingTextLen" id="alias_${status.count}" value="${dev.aliasName}">
										    <button class="btn btn-primary btn-xs" id="alias_set_${status.count}">set</button></td>
										</tr>
									</table>
								</div>
							</div>
			            </div> --%>
		  </div>
	     </div>
	    </div>
		</c:forEach>
	</div>
<%-- The End of Page Content --%>
</div>
</body>
</html>
