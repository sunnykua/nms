<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/account.css">
<script type="text/javascript" src="theme/Theme1/account.js"></script>
<script type="text/javascript" src="javascript/sorttable.js"></script>
<script type="text/javascript" src="theme/md5-min.js"></script>
<script type="text/javascript" src="theme/sha1-min.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link href="theme/bootstrap/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>

<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>

<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>

<%-- The Beginning of Page Content --%>

	<c:out value="<script>$(document).ready(update_view('${sessionScope.username}'));</script>" escapeXml="false"></c:out>
	<div id="page_content">
		<c:forEach var="acc" items="${accountAllItems}" varStatus="status">
	    <c:if test="${fn:toLowerCase(acc.userName) == fn:toLowerCase(sessionScope.username)}">
	    <c:if test="${!acc.isRemote()}">
		<div class="section_title">User Information</div>
			<div id="update_view">
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td>${fn:toLowerCase(sessionScope.username)}</td>
					</tr>
					<tr>
						<td>Password:</td><td>*******</td>
						<td><a href="javascript:void(0)" id="editor_pwd">Edit</a></td>
						</tr>
					<tr>
						<td>Name:</td><td><div id="Name"></div></td>
						<td><a href="javascript:void(0)" id="editor_name">Edit</a></td></tr>
					<tr>
						<td>Email:</td><td><div id="Email"></div></td>
						<td><a href="javascript:void(0)" id="editor_email">Edit</a></td></tr>
					<tr>
						<td>Phone Number:</td><td><div id="PhoneNumber"></div></td>
						<td><a href="javascript:void(0)" id="editor_phoneNumber">Edit</a></td></tr>
					<tr>
						<td></td>
						<td><c:if test="${sessionScope.userLevel<2}"><button class="btn btn-primary btn-xs" id="add_account">Create NEW Account</button></c:if></td>
					</tr>
				</table>
			</div>
			</c:if>
			</c:if>
			</c:forEach>
			<div id="update_pwd" style="display:none;" >
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td>${fn:toLowerCase(sessionScope.username)}</td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input type="password" id="bef_password" ></input></td>
					</tr>
					<tr>
						<td>Input NEW Password:</td>
						<td><input type="password" id="update_password"></input></td>
					</tr>
					<tr>
						<td>Input NEW Password again:</td>
						<td><input type="password" id="update_password2"></input></td>
					</tr>
					<tr>
						<td></td>
						<td><button class="btn btn-primary btn-xs" style="width:60px;" id="Cancel_pwd">Cancel</button>
						<button class="btn btn-primary btn-xs" style="width:60px;" id="Update_pwd">Apply</button></td>
					</tr>
				</table>
			</div>
			<div id="update_name" style="display:none">
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td>${fn:toLowerCase(sessionScope.username)}</td>
					</tr>
					<tr>
						<td>Name:</td>
						<td><input type="text" id="update_name_val"></input></td>
					</tr>
					<tr>
						<td></td>
						<td><button class="btn btn-primary btn-xs" style="width:60px;" id="Cancel_name">Cancel</button>
						<button class="btn btn-primary btn-xs" style="width:60px;" id="Update_name">Apply</button></td>
					</tr>
				</table>
			</div>
			<div id="update_email" style="display:none">
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td>${fn:toLowerCase(sessionScope.username)}</td>
					</tr>
					<tr>
						<td>Email:</td>
						<td><input type="text" id="update_email_val"></input></td>
					</tr>
					<tr>
						<td></td>
						<td><button class="btn btn-primary btn-xs" style="width:60px;" id="Cancel_email">Cancel</button>
						<button class="btn btn-primary btn-xs" style="width:60px;" id="Update_email">Apply</button></td>
					</tr>
				</table>
			</div>
			<div id="update_phoneNumber" style="display:none">
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td>${fn:toLowerCase(sessionScope.username)}</td>
					</tr>
					<tr>
						<td>Phone Number:</td>
						<td><input type="text" id="update_phoneNumber_val"></input></td>
					</tr>
					<tr>
						<td></td>
						<td><button class="btn btn-primary btn-xs" style="width:60px;" id="Cancel_phoneNumber">Cancel</button>
						<button class="btn btn-primary btn-xs" style="width:60px;" id="Update_phoneNumber">Apply</button></td>
					</tr>
				</table>
			</div>
			<div id="account" style="display:none">
				<c:if test="${sessionScope.userLevel<2}">
				<table class="account_info_table">
					<tr>
						<td>Username:</td>
						<td><input type="text" id="username"></input>
					<button class="btn btn-primary btn-xs" id="check_username">Check</button></td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input type="password" id="password"></input></td>
					</tr>
					<tr>
						<td>Password again:</td>
						<td><input type="password" id="password2"></input></td>
					</tr>
					<tr>
						<td>Level:</td>
						<td><select id="level">
						    	<option class="admin">admin</option>
						    	<option>user</option>
						    </select>
						</td>
					</tr>
					<tr>
						<td>Name:</td>
						<td><input type="text" id="name"></input></td>
					</tr>
					<tr>
						<td>Email:</td>
						<td><input type="text" id="email"></input></td>
					</tr>
					<tr>
						<td>Phone Number:</td>
						<td><input type="text" id="phoneNumber"></input></td>
					</tr>
					<tr>
					<td></td>
						<td><button class="btn btn-primary btn-xs" style="width:60px;" id="Cancel_account">Cancel</button>
						<button class="btn btn-primary btn-xs" style="width:60px;" id="create_account">Apply</button></td>
					</tr>
				</table>
				</c:if>
			</div>
		<c:if test="${sessionScope.userLevel<2}">
		<hr>
			<div class="section_title">Account List</div>
				<table id="AccountTable" class="sortable">
				    <tr>
				        <th>Delete</th><th>Username</th><th>Level</th><th>Name</th><th>Email</th><th>Phone number</th><th>Login time</th><th>Logout time</th>
				    </tr>
				</table>
				<br>
				<div class="buttonSection">
					<button class="btn btn-primary btn-xs" id="deleteBtu">Delete</button>
				</div>
		</c:if>
	</div>
</div>
</body>
</html>