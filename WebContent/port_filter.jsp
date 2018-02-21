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
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
</head>
<body>

    <jsp:include page="/Device">
		<jsp:param value="profile_items" name="method"/>
	</jsp:include>
	
	<c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
    <c:if test="${dev.publicIp==param.ip}">
    	<table id="portSection" class="interfaceTable_l">
    		<caption>Device Disconnect Filter</caption>
    		<table class="interfaceTable">
				<tr>
					<th>Profile</th><th></th>
				</tr>
				<tr>
					<td><input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
						<input type="checkbox" id="devFilter_check${status.count}" name="devFilter_check" <c:if test="${dev.mailFilter == true}">checked</c:if>>
						<select id="devFilter_select${status.count}" name="devFilter_select" <c:if test="${dev.mailFilter != true}">disabled</c:if>>
							<c:forEach var="item" items="${profileItems}">
							    <c:if test="${dev.mailFilter != true}">
	                               <option value="${item.profileName}">${item.profileName}</option>
	                            </c:if>
	                            <c:if test="${dev.mailFilter == true}">
	                               <c:if test="${item.profileName == dev.profileName}">
	                                  <option value="${item.profileName}" selected>${item.profileName}</option>
	                               </c:if>
	                            </c:if>
                            </c:forEach>
						</select>
					</td>
					<td><button class="btn btn-primary btn-xs" id="devFilterApply_${status.count}">Apply</button></td>
				</tr>
			</table>
    	</table>
    	<br>
    </c:if>
    </c:forEach>
	
      <c:forEach var="dev" items="${applicationScope.network.deviceList}" varStatus="status">
      <c:if test="${dev.publicIp==param.ip && (dev.deviceType == 'l2switch' || dev.deviceType == 'l3switch')}">
      <div id="extra_${status.count}">
         <table id="portSection" class="interfaceTable_l">
         	<caption>Interface Link Status Filter</caption>
				<tr VALIGN=TOP>
					<td><input type="hidden" id="switchid_${status.count}" value="${dev.publicIp}">
						<table class="interfaceTable">
							<tr>
							<th>Port</th><th>Profile</th>
							</tr>
							<c:forEach begin="0" end="${dev.infNum}" var="inf" items="${dev.interfaces}" varStatus="select1">
							<c:if test="${inf.ifType == 'eth'}">
								<tr>
									<td>${inf.portId}<input type="hidden" name="ifIndex" value="${inf.ifIndex}"></td>
									<td><input type="checkbox" id="mailFilter_check${status.count}_${select1.index}" name="mailFilter_check" <c:if test="${inf.mailFilter == true}">checked</c:if>>
										<select id="mailFilter_select${status.count}_${select1.index}" name="mailFilter_select" <c:if test="${inf.mailFilter != true}">disabled</c:if>>
											<c:forEach var="item" items="${profileItems}">
											    <c:if test="${inf.mailFilter != true}">
					                               <option value="${item.profileName}">${item.profileName}</option>
					                            </c:if>
					                            <c:if test="${inf.mailFilter == true}">
					                               <option value="${item.profileName}" <c:if test="${item.profileName == inf.profileName}">selected</c:if>>${item.profileName}</option>
					                            </c:if>
				                            </c:forEach>
										</select>
									</td>
								</tr>
							</c:if>
							</c:forEach>
						</table>
					</td>
					<%-- <c:if test="${dev.infNum>1}">
					<td>
						<table class="interfaceTable">
						<tr>
						<c:if test="${dev.supportPoe}"><th>Port</th><th>Profile</th></c:if>
						</tr>
							<c:forEach begin="${dev.infNum/2}" end="${dev.infNum-1}" var="inf" items="${dev.interfaces}" varStatus="select2">
							<c:if test="${inf.jackType == 'rj45'}">
								<tr>
									<td>${inf.portId}</td>
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
		<div class="buttonSection"><button class="btn btn-primary btn-xs" id="mailFilterApply_${status.count}">Apply</button></div>
		</div>
		</c:if>
		</c:forEach>
		
</body>
</html>