<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/device_interface.css">
<script type="text/javascript" src="theme/Theme1/device.js"></script>
<script type="text/javascript" src="theme/Theme1/check_s.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>

<%-- <jsp:include page="/Device">
	<jsp:param value="poeItems" name="method"/>
</jsp:include> --%>
	
      <c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
      <c:if test="${dev.publicIp == param.ip}">
      <div id="extra_${status.count}"><input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
         <table id="portSection" class="interfaceTable_l">
				<tr VALIGN=TOP>
				<c:forEach begin="1" end="${dev.stackNum}" varStatus="stackId">
					<td>
						<table class="interfaceTable">
						<tr>
						<th>Port</th><th>Manual</th><th>IP</th><%-- <th>Monitored</th><c:if test="${dev.supportPoe}"><th>Poe Schedule</th></c:if> --%>
						</tr>
							<c:forEach begin="0" end="${dev.infNum}" var="inf" items="${dev.interfaces}" varStatus="select1">
							<c:if test="${inf.ifType == 'eth' && inf.stackId == stackId.index}">
								<tr>
									<td>${inf.portId}<input type="hidden" name="ifIndex" value="${inf.ifIndex}"></td>
									<td><input type="checkbox" id="auto${status.count}_${select1.index}" name="auto"<c:if test="${inf.manual == true}">checked</c:if>></td>
									<td><select id="port_select${status.count}_${select1.index}" <c:if test="${inf.manual != true}">disabled</c:if>>
											<option value="none">------NONE------</option>
											<optgroup label="Switch"></optgroup>
											<c:forEach var="dev_switch" items="${applicationScope.network.deviceList}">
											<c:if test="${dev_switch.deviceType == 'l2switch' || dev_switch.deviceType == 'l3switch'}">
											    <c:if test="${dev_switch.publicIp!=dev.publicIp}">
											    <c:if test="${inf.manual != true}">
												    <c:if test="${dev_switch.phyAddr == inf.lldpRemoteId}">
													       <option value="${dev_switch.publicIp}" selected>${dev_switch.publicIp}</option>
													</c:if>
													<c:if test="${dev_switch.phyAddr != inf.lldpRemoteId}">
													       <option value="${dev_switch.publicIp}">${dev_switch.publicIp}</option>
													</c:if>
												</c:if>
												<c:if test="${inf.manual == true}">
													<c:if test="${dev_switch.publicIp == inf.manualRemoteIp}">
													       <option value="${dev_switch.publicIp}" selected>${dev_switch.publicIp}</option>
													</c:if>
													<c:if test="${dev_switch.publicIp != inf.manualRemoteIp}">
													       <option value="${dev_switch.publicIp}">${dev_switch.publicIp}</option>
													</c:if>
												</c:if>
												</c:if>
											</c:if>
											</c:forEach>
                                            <optgroup label="AC"></optgroup>
                                            <c:forEach var="dev_ac" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_ac.deviceType == 'wlanAC'}">
                                                <c:if test="${dev_ac.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_ac.phyAddr && dev_ac.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_ac.publicIp}" selected>${dev_ac.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_ac.publicIp}">${dev_ac.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_ac.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_ac.publicIp}" selected>${dev_ac.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_ac.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_ac.publicIp}">${dev_ac.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="AP"></optgroup>
                                            <c:forEach var="dev_ap" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_ap.deviceType == 'wlanAP'}">
                                                <c:if test="${dev_ap.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_ap.phyAddr && dev_ap.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_ap.publicIp}" selected>${dev_ap.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_ap.publicIp}">${dev_ap.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_ap.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_ap.publicIp}" selected>${dev_ap.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_ap.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_ap.publicIp}">${dev_ap.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="Firewall"></optgroup>
                                            <c:forEach var="dev_firewall" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_firewall.deviceType == 'firewall'}">
                                                <c:if test="${dev_firewall.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_firewall.phyAddr && dev_firewall.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_firewall.publicIp}" selected>${dev_firewall.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_firewall.publicIp}">${dev_firewall.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_firewall.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_firewall.publicIp}" selected>${dev_firewall.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_firewall.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_firewall.publicIp}">${dev_firewall.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="Server"></optgroup>
                                            <c:forEach var="dev_server" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_server.deviceType == 'server'}">
                                                <c:if test="${dev_server.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_server.phyAddr && dev_server.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_server.publicIp}" selected>${dev_server.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_server.publicIp}">${dev_server.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_server.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_server.publicIp}" selected>${dev_server.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_server.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_server.publicIp}">${dev_server.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="NMS"></optgroup>
                                            <c:forEach var="dev_nms" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_nms.deviceType == 'NMS'}">
                                                <c:if test="${dev_nms.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_nms.phyAddr && dev_nms.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_nms.publicIp}" selected>${dev_nms.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_nms.publicIp}">${dev_nms.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_nms.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_nms.publicIp}" selected>${dev_nms.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_nms.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_nms.publicIp}">${dev_nms.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="PC"></optgroup>
                                            <c:forEach var="dev_pc" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_pc.deviceType == 'pc'}">
                                                <c:if test="${dev_pc.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_pc.phyAddr && dev_nms.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_pc.publicIp}" selected>${dev_pc.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_pc.publicIp}">${dev_pc.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_pc.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_pc.publicIp}" selected>${dev_pc.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_pc.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_pc.publicIp}">${dev_pc.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="Internet"></optgroup>
                                            <c:forEach var="dev_internet" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_internet.deviceType == 'internet'}">
                                                <c:if test="${dev_internet.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_internet.phyAddr && dev_internet.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_internet.publicIp}" selected>${dev_internet.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_internet.publicIp}">${dev_internet.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_internet.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_internet.publicIp}" selected>${dev_internet.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_internet.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_internet.publicIp}">${dev_internet.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="MGVServer"></optgroup>
                                            <c:forEach var="dev_mgvserver" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_mgvserver.deviceType == 'MGVChiefServer' || dev_mgvserver.deviceType == 'MGVCommandServer'}">
                                                <c:if test="${dev_mgvserver.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_mgvserver.phyAddr && dev_mgvserver.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_mgvserver.publicIp}" selected>${dev_mgvserver.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_mgvserver.publicIp}">${dev_mgvserver.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_mgvserver.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_mgvserver.publicIp}" selected>${dev_mgvserver.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_mgvserver.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_mgvserver.publicIp}">${dev_mgvserver.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="MGVPlayer"></optgroup>
                                            <c:forEach var="dev_mgvplayer" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_mgvplayer.deviceType == 'MGVPlayer'}">
                                                <c:if test="${dev_mgvplayer.publicIp!=dev.publicIp}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_mgvplayer.phyAddr && dev_mgvplayer.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_mgvplayer.publicIp}" selected>${dev_mgvplayer.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_mgvplayer.publicIp}">${dev_mgvplayer.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_mgvplayer.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_mgvplayer.publicIp}" selected>${dev_mgvplayer.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_mgvplayer.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_mgvplayer.publicIp}">${dev_mgvplayer.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                                </c:if>
                                            </c:forEach>
									</select></td>
									<%-- <td><input type="checkbox" id="CP"<c:if test="${inf.monitored == true}">checked</c:if>></td>
									<c:if test="${dev.supportPoe}">
									<td><input type="checkbox" id="poe_check${status.count}_${select1.index}" name="poe_check" <c:if test="${inf.poePower == true}">checked</c:if>>
										<select id="poe_select${status.count}_${select1.index}" name="poe_select" <c:if test="${inf.poePower != true}">disabled</c:if>>
											<c:forEach var="item" items="${poeItems}">
											    <c:if test="${inf.poePower != true}">
					                               <option value="${item.poeScheduleName}">${item.poeScheduleName}</option>
					                            </c:if>
					                            <c:if test="${inf.poePower == true}">
					                               <c:if test="${item.poeScheduleName == inf.poeScheduleName}">
					                                  <option value="${item.poeScheduleName}" selected>${item.poeScheduleName}</option>
					                               </c:if>
					                            </c:if>
				                            </c:forEach>
										</select>
									</td>
									</c:if> --%>
								</tr>
								</c:if>
							</c:forEach>
						</table>
					</td>
					</c:forEach>
					<%-- <c:if test="${dev.infNum>1}">
					<td>
						<table class="interfaceTable">
						<tr>
						<th>Port</th><th>Manual</th><th>IP</th><th>Monitored</th><c:if test="${dev.supportPoe}"><th>Poe Schedule</th></c:if>
						</tr>
							<c:forEach begin="${dev.infNum/2}" end="${dev.infNum-1}" var="inf" items="${dev.interfaces}" varStatus="select2">
							<c:if test="${inf.jackType == 'rj45' || inf.jackType == 'fiber'}">
								<tr>
									<td>${inf.portId}</td>
									<td><input type="checkbox" id="auto${status.count}_${select2.index}" name="auto"<c:if test="${inf.manual == true}">checked</c:if>></td>
									<td><select id="port_select${status.count}_${select2.index}" <c:if test="${inf.manual != true}">disabled</c:if>>
											<option value="none">------NONE------</option>
											<optgroup label="SWITCH"></optgroup>
											<c:forEach var="dev_select2" items="${applicationScope.network.deviceList}">
											<c:if test="${dev_select2.deviceType != 'wlanAC' && dev_select1.deviceType != 'wlanAP'}">
											    <c:if test="${dev_select2.publicIp!=dev.publicIp}">
											        <c:if test="${inf.manual != true}">
													<c:if test="${dev_select2.phyAddr == inf.lldpRemoteId}">
													       <option value="${dev_select2.publicIp}" selected>${dev_select2.publicIp}</option>
													</c:if>
													<c:if test="${dev_select2.phyAddr != inf.lldpRemoteId}">
													       <option value="${dev_select2.publicIp}">${dev_select2.publicIp}</option>
													</c:if>
													</c:if>
												    <c:if test="${inf.manual == true}">
													<c:if test="${dev_select2.publicIp == inf.manualRemoteIp}">
													       <option value="${dev_select2.publicIp}" selected>${dev_select2.publicIp}</option>
													</c:if>
													<c:if test="${dev_select2.publicIp != inf.manualRemoteIp}">
													       <option value="${dev_select2.publicIp}">${dev_select2.publicIp}</option>
													</c:if>
												    </c:if>
												</c:if>
											</c:if>		
											</c:forEach>
                                            <optgroup label="AC"></optgroup>
                                            <c:forEach var="dev_ac" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_ac.deviceType == 'wlanAC'}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_ac.phyAddr && dev_ac.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_ac.publicIp}" selected>${dev_ac.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_ac.publicIp}">${dev_ac.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_ac.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_ac.publicIp}" selected>${dev_ac.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_ac.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_ac.publicIp}">${dev_ac.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <optgroup label="AP"></optgroup>
                                            <c:forEach var="dev_ap" items="${applicationScope.network.deviceList}">
                                                <c:if test="${dev_ap.deviceType == 'wlanAP'}">
                                                    <c:if test="${inf.manual != true}">
                                                        <c:choose>
                                                            <c:when test="${!empty dev_ap.phyAddr && dev_ap.phyAddr == inf.lldpRemoteId}">
                                                                <option value="${dev_ap.publicIp}" selected>${dev_ap.publicIp}</option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="${dev_ap.publicIp}">${dev_ap.publicIp}</option>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                    <c:if test="${inf.manual == true}">
                                                        <c:if test="${dev_ap.publicIp == inf.manualRemoteIp}">
                                                            <option value="${dev_ap.publicIp}" selected>${dev_ap.publicIp}</option>
                                                        </c:if>
                                                        <c:if test="${dev_ap.publicIp != inf.manualRemoteIp}">
                                                            <option value="${dev_ap.publicIp}">${dev_ap.publicIp}</option>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
									</select></td>
									<td><input type="checkbox" id="CP"<c:if test="${inf.monitored == true}">checked</c:if>></td>
									<c:if test="${dev.supportPoe}">
									<td><input type="checkbox" id="poe_check${status.count}_${select2.index}" name="poe_check" <c:if test="${inf.poePower == true}">checked</c:if>>
										<select id="poe_select${status.count}_${select2.index}" name="poe_select" <c:if test="${inf.poePower != true}">disabled</c:if>>
											<c:forEach var="item" items="${poeItems}">
					                            <c:if test="${inf.poePower != true}">
					                               <option value="${item.poeScheduleName}">${item.poeScheduleName}</option>
					                            </c:if>
					                            <c:if test="${inf.poePower == true}">
					                               <c:if test="${item.poeScheduleName == inf.poeScheduleName}">
					                                  <option value="${item.poeScheduleName}" selected>${item.poeScheduleName}</option>
					                               </c:if>
					                            </c:if>
				                            </c:forEach>
										</select>
									</td>
									</c:if>
								</tr>
								</c:if>
							</c:forEach>
						</table>
					</td>
					</c:if> --%>
				</tr>
			</table>
		</div>
		<div class="buttonSection"><button class="btn btn-primary btn-xs" id="choose_port_ip_${status.count}">Apply</button></div>
		</c:if>
		</c:forEach>
		
</body>
</html>