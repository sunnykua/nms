<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Settings</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script src="theme/jQueryUI/jquery.blockUI.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/settings.css">
<script type="text/javascript" src="theme/Theme1/setting.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<script src="theme/jQuery-File-Upload-9.8.0/js/vendor/jquery.ui.widget.js"></script>
<script src="theme/jQuery-File-Upload-9.8.0/js/jquery.iframe-transport.js"></script>
<script src="theme/jQuery-File-Upload-9.8.0/js/jquery.fileupload.js"></script>
</head>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px; margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

<c:if test="${sessionScope.userLevel>1}">
	<c:redirect url="login.jsp"></c:redirect>
</c:if>

<jsp:include page="/Settings">
	<jsp:param value="get_log_settings" name="action" />
</jsp:include>

<jsp:include page="/Settings">
	<jsp:param value="get_alarm_settings" name="action" />
</jsp:include>

<jsp:include page="/Settings">
	<jsp:param value="get_sms_settings" name="action" />
</jsp:include>

<jsp:include page="/Account">
	<jsp:param value="accountItems" name="type" />
</jsp:include>

<jsp:include page="/Settings">
    <jsp:param value="get_interfaces" name="action" />
</jsp:include>

<jsp:include page="/Settings">
    <jsp:param value="remote_service_get" name="action" />
</jsp:include>

<jsp:include page="/Settings">
    <jsp:param value="location_address_get" name="action" />
</jsp:include>

<div class="page_content">
	<div class="page_title">Settings</div>

	<div class="settings_section">
	    <div class="log_title">Value Setting</div>
		<table class="log_table">
			<tr>
				<td class="log_item_label">Default Update Interval for Real-time Chart:</td>
				<td>
				<select id="chart_timeInterval" size="1">
				          <option value="10" <c:if test="<%= Config.getRealtimeChartTimeout() == 10 %>">selected</c:if>>10sec</option>
				          <option value="30" <c:if test="<%= Config.getRealtimeChartTimeout() == 30 %>">selected</c:if>>30sec</option>
				          <option value="60" <c:if test="<%= Config.getRealtimeChartTimeout() == 60 %>">selected</c:if>>1minute</option>
				          <option value="120" <c:if test="<%= Config.getRealtimeChartTimeout() == 120 %>">selected</c:if>>2minute</option>
				</select></td>
			</tr>
			<tr>
				<td class="log_item_label">Remote web port setting of device:</td>
				<td><input type="text" id="nms_port" value="<%= Config.getRemoteWebPort() %>"></td>
			</tr>
			<%-- <tr>
				<td class="log_item_label">Member Login limit Setting:</td>
				<td><input type="text" id="login_num" value="<%= Config.getLoginUserMax() %>"></td>
			</tr> --%>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="value_set">Submit</button>
		</div>
	</div>
	
	<div class="settings_section">
		<div class="log_title">Notify Mail</div>
		<table class="log_table">
			<tr>
				<td class="log_item_label">Sender:</td>
				<td class="log_item_content" ><input type="text" id="mailfrom_input" value="${log_mail_from}" style="width:400px"></td>
			</tr>
			<tr>
				<td class="log_item_label">Subject:</td>
				<td class="log_item_content"><input type="text" id="mailsubject_input" value="${log_mail_subject}" style="width:400px"></td>
			</tr>
			<tr>
				<td class="log_item_label">SMTP host:</td>
				<td class="log_item_content">
				<select id="smtp_host">
					<option <c:if test="${smtp_host == 'smtp.gmail.com'}">selected</c:if>>smtp.gmail.com</option>
					<option <c:if test="${smtp_host == 'smtp.mail.yahoo.com'}">selected</c:if>>smtp.mail.yahoo.com</option>
				</select></td>
			</tr><%-- 
			<tr>
				<td class="log_item_label">SMTP port:</td>
				<td class="log_item_content"><input type="text" id="smtp_port" value="${smtp_port}"></td>
			</tr> --%>
			<tr>
				<td class="log_item_label">SMTP username:</td>
				<td class="log_item_content"><input type="text" id="smtp_username" value="${smtp_username}"></td>
			</tr>
			<tr>
				<td class="log_item_label">SMTP password:</td>
				<td class="log_item_content"><input type="password" id="smtp_password" value="${smtp_password}"></td>
			</tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="log_submit">Submit</button>
		</div>
	</div>
	
	<div class="settings_section">
		<div class="log_title">Notify SMS</div>
		<table class="log_table">
			<tr>
				<td class="log_item_label">Username:</td>
				<td class="log_item_content"><input type="text" id="smsUserName" value="${sms_username}"></td>
			</tr>
			<tr>
				<td class="log_item_label">Password:</td>
				<td class="log_item_content"><input type="password" id="smsPwd" value="${sms_password}"></td>
			</tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="sms_set">Submit</button>
		</div>
	</div>
	
	<div class="settings_section">
		<div class="log_title">Alarm Setting</div>
		<table class="log_table" id="alarmTable">
		    <tr>
				<th>Alarm Name</th>
				<th>Email</th>
				<th>SMS</th>
				<th></th>
			</tr>
			<tr>
				<td>CyberExpert Start Up</td>
				<td align="center"><input type="checkbox" id="NmsStartUpSendEmail" name="mail_status" <c:if test="${NmsStartUpSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="NmsStartUpSendSms" name="sms_status" <c:if test="${NmsStartUpSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case1">Select</button></td>
			</tr>
			<tr>
				<td>CyberExpert Shut Down</td>
				<td align="center"><input type="checkbox" id="NmsShutDownSendEmail" name="mail_status" <c:if test="${NmsShutDownSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="NmsShutDownSendSms" name="sms_status" <c:if test="${NmsShutDownSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case2">Select</button></td>
			</tr>
			<tr>
				<td>Device Disconnect</td>
				<td align="center"><input type="checkbox" id="DeviceDisconnectSendEmail" name="mail_status" <c:if test="${DeviceDisconnectSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="DeviceDisconnectSendSms" name="sms_status" <c:if test="${DeviceDisconnectSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case3">Select</button></td>
			</tr>
			<tr>
				<td>Management Fail</td>
				<td align="center"><input type="checkbox" id="ManagementFailSendEmail" name="mail_status" <c:if test="${ManagementFailSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="ManagementFailSendSms" name="sms_status" <c:if test="${ManagementFailSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case9">Select</button></td>
			</tr>
			<tr>
				<td>Monitored Port Link Up</td>
				<td align="center"><input type="checkbox" id="MonitoredPortLinkUpSendEmail" name="mail_status" <c:if test="${MonitoredPortLinkUpSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="MonitoredPortLinkUpSendSms" name="sms_status" <c:if test="${MonitoredPortLinkUpSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case4">Select</button></td>
			</tr>
			<tr>
				<td>Monitored Port Link Down</td>
				<td align="center"><input type="checkbox" id="MonitoredPortLinkDownSendEmail" name="mail_status" <c:if test="${MonitoredPortLinkDownSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="MonitoredPortLinkDownSendSms" name="sms_status" <c:if test="${MonitoredPortLinkDownSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case5">Select</button></td>
			</tr>
			<tr>
				<td>Critical Port Link Up</td>
				<td align="center"><input type="checkbox" id="CriticalPortLinkUpSendEmail" name="mail_status" <c:if test="${CriticalPortLinkUpSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="CriticalPortLinkUpSendSms" name="sms_status" <c:if test="${CriticalPortLinkUpSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case6">Select</button></td>
			</tr>
			<tr>
				<td>Critical Port Link Down</td>
				<td align="center"><input type="checkbox" id="CriticalPortLinkDownSendEmail" name="mail_status" <c:if test="${CriticalPortLinkDownSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="CriticalPortLinkDownSendSms" name="sms_status" <c:if test="${CriticalPortLinkDownSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case7">Select</button></td>
			</tr>
			<tr>
				<td>Web Update</td>
				<td align="center"><input type="checkbox" id="WebUpdatedSendEmail" name="mail_status" <c:if test="${WebUpdatedSendEmail == 1}">checked="checked"</c:if>></td>
				<td align="center"><input type="checkbox" id="WebUpdatedSendSms" name="sms_status" <c:if test="${WebUpdatedSendSms == 1}">checked="checked"</c:if>></td>
				<td><button class="btn btn-default btn-xs" data-toggle="modal" data-target=".case8">Select</button></td>
			</tr>
		</table>
		<c:forEach begin="1" end="9" var="div" varStatus="divStatus">
		<div class="modal fade case${divStatus.count}" tabindex="-1" role="dialog" aria-labelledby="case${divStatus.count}" aria-hidden="true">
		  <div class="modal-dialog modal-sm">
		    <div class="modal-content">
		    <table>
		    <tr>
			    <td>
				    <table id="caseE${divStatus.count}" class="alarmSelect">
				    <tr><th>Email</th></tr>
				    <c:forEach var="acc" items="${accountItems}" varStatus="status">
					    <tr>
						    <td>
						       <input type="checkbox" id="checkCaseE${divStatus.count}${status.count}" name="casee${divStatus.count}" value="${acc.userName}"
						       <c:if test="${emailAlarm[divStatus.index-1][status.index] == acc.userName}">checked="checked"</c:if>> ${acc.name}
						    </td>
					    </tr>
				    </c:forEach>
				    </table>
			    </td>
			    <td>
				    <table id="caseS${divStatus.count}" class="alarmSelect">
				    <tr><th>SMS</th></tr>
				    <c:forEach var="acc" items="${accountItems}" varStatus="status">
					    <tr>
						    <td>
						       <input type="checkbox" id="checkCaseS${divStatus.count}${status.count}" name="cases${divStatus.count}" value="${acc.userName}"
						       <c:if test="${smsAlarm[divStatus.index-1][status.index] == acc.userName}">checked="checked"</c:if>> ${acc.name}
						    </td>
					    </tr>
				    </c:forEach>
				    </table>
			    </td>
		    </tr>
		    </table>
		    </div>
	     </div>
	    </div>
	    </c:forEach>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="alarm_set">Submit</button>
		</div>
	</div>
	
	<div class="settings_section">
		<div class="log_title">Mail Filter Profile</div>

		<table class="log_table">
			<tr>
				<td class="log_item_label">Profile Name:</td>
				<td class="log_item_content"><input type="text" id="profile_name"></td>
			</tr>
			<tr>
				<td class="log_item_label">Start:</td>
				<td class="log_item_content"><select id="profile_StartHour">
						<option value="00">00</option>
						<option value="01">01</option>
						<option value="02">02</option>
						<option value="03">03</option>
						<option value="04">04</option>
						<option value="05">05</option>
						<option value="06">06</option>
						<option value="07">07</option>
						<option value="08">08</option>
						<option value="09">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
						<option value="15">15</option>
						<option value="16">16</option>
						<option value="17">17</option>
						<option value="18">18</option>
						<option value="19">19</option>
						<option value="20">20</option>
						<option value="21">21</option>
						<option value="22">22</option>
						<option value="23">23</option>
					</select>
					:<select id="profile_StartMinutes">
						<option value=":00">00</option>
						<option value=":05">05</option>
						<option value=":10">10</option>
						<option value=":15">15</option>
						<option value=":20">20</option>
						<option value=":25">25</option>
						<option value=":30">30</option>
						<option value=":35">35</option>
						<option value=":40">40</option>
						<option value=":45">45</option>
						<option value=":50">50</option>
						<option value=":55">55</option>
					</select></td>
			</tr>
			<tr>
				<td class="log_item_label">End:</td>
				<td class="log_item_content"><select id="profile_EndHour">
						<option value="00">00</option>
						<option value="01">01</option>
						<option value="02">02</option>
						<option value="03">03</option>
						<option value="04">04</option>
						<option value="05">05</option>
						<option value="06">06</option>
						<option value="07">07</option>
						<option value="08">08</option>
						<option value="09">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
						<option value="15">15</option>
						<option value="16">16</option>
						<option value="17">17</option>
						<option value="18">18</option>
						<option value="19">19</option>
						<option value="20">20</option>
						<option value="21">21</option>
						<option value="22">22</option>
						<option value="23">23</option>
					</select>
					:<select id="profile_EndMinutes">
						<option value=":00">00</option>
						<option value=":05">05</option>
						<option value=":10">10</option>
						<option value=":15">15</option>
						<option value=":20">20</option>
						<option value=":25">25</option>
						<option value=":30">30</option>
						<option value=":35">35</option>
						<option value=":40">40</option>
						<option value=":45">45</option>
						<option value=":50">50</option>
						<option value=":55">55</option>
					</select></td>
			</tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="profile_apply">Submit</button>
		</div>
		<div class="log_title">Mail Profile List</div>
		<table id="profileTable" class="log_table">
			    <tr>
			        <th>Delete</th><th>Profile Name</th><th>Start</th><th>End</th>
			    </tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="profileDeleteBtu">Delete</button>
		</div>
	</div>
	
	<!-- <div class="settings_section">
		<div class="log_title">PoE Daily Schedule</div>

		<table class="log_table">
			<tr>
				<td class="log_item_label">Schedule name:</td>
				<td class="log_item_content"><input type="text" id="schedule_name"></td>
			</tr>
			<tr>
				<td class="log_item_label">Start:</td>
				<td class="log_item_content"><select id="poe_StartHour">
						<option value="00">00</option>
						<option value="01">01</option>
						<option value="02">02</option>
						<option value="03">03</option>
						<option value="04">04</option>
						<option value="05">05</option>
						<option value="06">06</option>
						<option value="07">07</option>
						<option value="08">08</option>
						<option value="09">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
						<option value="15">15</option>
						<option value="16">16</option>
						<option value="17">17</option>
						<option value="18">18</option>
						<option value="19">19</option>
						<option value="20">20</option>
						<option value="21">21</option>
						<option value="22">22</option>
						<option value="23">23</option>
					</select>
					:<select id="poe_StartMinutes">
						<option value=":00">00</option>
						<option value=":05">05</option>
						<option value=":10">10</option>
						<option value=":15">15</option>
						<option value=":20">20</option>
						<option value=":25">25</option>
						<option value=":30">30</option>
						<option value=":35">35</option>
						<option value=":40">40</option>
						<option value=":45">45</option>
						<option value=":50">50</option>
						<option value=":55">55</option>
					</select></td>
			</tr>
			<tr>
				<td class="log_item_label">End:</td>
				<td class="log_item_content"><select id="poe_EndHour">
						<option value="00">00</option>
						<option value="01">01</option>
						<option value="02">02</option>
						<option value="03">03</option>
						<option value="04">04</option>
						<option value="05">05</option>
						<option value="06">06</option>
						<option value="07">07</option>
						<option value="08">08</option>
						<option value="09">09</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
						<option value="14">14</option>
						<option value="15">15</option>
						<option value="16">16</option>
						<option value="17">17</option>
						<option value="18">18</option>
						<option value="19">19</option>
						<option value="20">20</option>
						<option value="21">21</option>
						<option value="22">22</option>
						<option value="23">23</option>
					</select>
					:<select id="poe_EndMinutes">
						<option value=":00">00</option>
						<option value=":05">05</option>
						<option value=":10">10</option>
						<option value=":15">15</option>
						<option value=":20">20</option>
						<option value=":25">25</option>
						<option value=":30">30</option>
						<option value=":35">35</option>
						<option value=":40">40</option>
						<option value=":45">45</option>
						<option value=":50">50</option>
						<option value=":55">55</option>
					</select></td>
			</tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="poe_apply">Submit</button>
		</div>
		<div class="log_title">PoE Schedule List</div>
		<table id="poeScheduleTable" class="log_table">
			    <tr>
			        <th>Delete</th><th>Time Schedule Name</th><th>Start</th><th>End</th>
			    </tr>
		</table>
		<div class="log_button_section">
			<button class="btn btn-primary btn-xs" id="deleteBtu">Delete</button>
		</div>
	</div> -->
	
	<div class="settings_section">
		<div class="log_title">Location Address Setting</div>
		<table class="log_table">
			<tr>
				<td class="log_item_label">Address:</td>
				<td><input type="text" id="address"
					value="${address}" size="60"></td>
			</tr>
		</table>
		<div class="log_button_section">
			<input type="button" class="btn btn-primary btn-xs"
				style="width: auto;" value="Submit" onclick="location_address_set()">
		</div>
	</div>

	<div class="settings_section">
		<div class="log_title">Remote Service Setting</div>
		<table class="log_table">
			<tr>
				<td class="log_item_label"><input type="checkbox"
					id="remote_service_switch"
					<c:if test="${remote_service_switch}">checked="checked"</c:if>>
					Enable Remote Service</td>
			</tr>
			<tr>
				<td class="log_item_label">Service Address:</td>
				<td><input type="text" id="remote_service_address"
					value="${remote_service_address}"></td>
			</tr>
			<%--<tr>
				<td class="log_item_label">Remote Service Registry Port:</td>
				<td><input type="text" id="remote_service_registry_port"
					value="${remote_service_registry_port}"></td>
			</tr>
			<tr>
				<td class="log_item_label">Remote Service Data Port:</td>
				<td><input type="text" id="remote_service_data_port"
					value="${remote_service_data_port}"></td>
			</tr>--%>
			<tr>
				<td class="log_item_label">Remote Server Address:</td>
				<td><input type="text" id="remote_server_address"
					value="${remote_server_address}"></td>
			</tr>
			<%--<tr>
				<td class="log_item_label">Remote Server Port:</td>
				<td><input type="text" id="remote_server_port"
					value="${remote_server_port}"></td>
			</tr>--%>
		</table>
		<div class="log_button_section">
			<input type="button" class="btn btn-primary btn-xs"
				style="width: auto;" value="Submit" onclick="remote_service_set()">
		</div>
	</div>
	
	<jsp:include page="/License" />
	<div class="settings_section">
		<div class="log_title">License information</div>
        <table class="log_table">
        	<tr>
				<td class="log_item_label">NMS Serial Number：</td>
				<td>${license.get(0)}</td>
			</tr>
			<tr>
				<td class="log_item_label">Current Device Number：</td>
				<td>${license.get(1)}</td>
			</tr>
			<tr>
				<td class="log_item_label">License Device Number：</td>
				<td>${license.get(3)}</td>
			</tr>
			<tr>
				<td>
					Select license file to upload：
				</td>
				<td><form method="post" action="License?method=license_upload" enctype="multipart/form-data">
					<!-- <span class="btn btn-primary btn-xs file"> -->
                		<input type="file" name="file" width="100px" onchange="checkFileSize(this)">
                	<!-- </span> -->
                	<input type="submit" class="btn btn-primary btn-xs" value="Submit">
				</form></td>
            </tr>
        </table>
	</div>

	<div class="settings_section">
		<div class="log_title">Settings Backup</div>
		<table class="log_table">
			<tr><td class="log_item_label">Import:</td>
			<td class="log_item_content">
			<span class="btn btn-primary btn-xs file">
				Import <input id="settingsUpload" type="file" name="file" data-url="Settings?action=settings_import"/>
			</span>
			</td></tr>
			<tr><td class="log_item_label">Export:</td>
			<td class="log_item_content"><button class="btn btn-primary btn-xs" id="settingsExportBtn">Export</button></td></tr>
		</table>
	</div>
	
	<div class="settings_section">
        <div class="log_title">CyberExpert Update</div>
        <table class="log_table">
            <tr><td>
            <span class="btn btn-primary btn-xs file">
                Update <input id="fileupload" type="file" name="file" data-url="Settings?action=web_update"/>
            </span>
            </td></tr>
            <tr><td>
                <div id="progress" style="border: 1px solid black; position: relative; padding: 3px;">
                    <span id="percent" style="position: absolute; left: 50%; top: -3px;">0%</span>
                    <div id="bar" class="bar" style="width: 0%; height: 18px; background-color: #428bca;"></div>
                </div>
            </td></tr>
        </table>
	</div>
</div>
</div>
</body>
</html>
