<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.via.system.Config" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SMS Queue</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/sms_queue.css">
<script type="text/javascript" src="theme/datatables/jquery.dataTables.js"></script>
<link rel="stylesheet" type="text/css" href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript" src="theme/datatables/jquery.highlight-4.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script src="theme/jQueryUI/jquery-ui.js"></script>
<script src="theme/jQueryUI/jquery.blockUI.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
</head>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>
	<jsp:include page="/QueueViewer">
		<jsp:param value="sms" name="type"/>
		<jsp:param value="read" name="action"/>
	</jsp:include>
	
	<div id="page_content">
		<div id="page_title">SMS Queue</div>
		
		<div class="buttonGroup">
			<button class="btn btn-primary btn-sm" id="deleteBtn">Delete</button>
			<button class="btn btn-primary btn-sm" id="refreshBtn">Refresh</button>
		</div>
		
		<table class="tableSMSQueue"><tr>
			<th class="thCount">ID</th>
			<th class="thSelectAllCheck">
				<input type="checkbox" id="selectAllCheck" name="selectAllItem" <c:if test="${sessionScope.userLevel>1}">disabled</c:if>>
			</th>
			<th class="thItemName">ITEM</th>
			<th class="thItemValue">CONTENT</th>
		</tr></table>
		<c:forEach var="smsInfo" items="${smsQueueList}" varStatus="status">
			<table class="tableSMSQueue" id="smsTable">
				<tr>
					<td rowspan="7" class="tdCount">${status.count}</td>
					<td rowspan="7" class="tdSelectCheck">
						<input type="checkbox" id="selectedCheck_${status.count}" name="selectItem" value="${smsInfo.id}" <c:if test="${sessionScope.userLevel>1}">disabled</c:if>>
					</td>
					<th class="thItemName">Time</th><td class="tdItemValue">${smsInfo.time}</td>
				</tr>
				<tr>
					<th class="thItemName">Vendor</th><td class="tdItemValue">${smsInfo.provider}</td>
				</tr>
				<tr>
					<th class="thItemName">To</th><td class="tdItemValue">${smsInfo.recipient}</td>
				</tr>
				<tr>
					<th class="thItemName">Text</th><td class="tdItemValue">${smsInfo.text}</td>
				</tr>
				<tr>
					<th class="thItemName">Reason</th><td class="tdItemValue">${smsInfo.reason}</td>
				</tr>
			</table>
		</c:forEach>
	</div>
</div>

<script>
$(function () {
	$("#deleteBtn").click(function() {
		var checkedItems = [];
		var selectedItems = checkedItems;
		
		$("input[name='selectItem']:checked").each(function() {
			checkedItems.push($(this).val());
		});
		
		if (selectedItems.length < 1) {
			alert("Please select at least one of the SMS.");
			return;
		}
		
		if (confirm("Are you sure to delete these " + selectedItems.length + " SMS?")) {
			$.ajax({
				type: "POST",
				url: "QueueViewer?type=sms&action=delete",
				data: { items : selectedItems },
				success: function(data) {
					/* $("input[name='selectAllItem']").attr('checked', false);
					
					var table = $('#mailTable').DataTable();
					$("[id^=selectedCheck_]").each(function() {
						if(this.checked) {
							$(this).parents("tr").addClass('selected1');
							table.row('.selected1').remove().draw( false );
						}
					});
					*/
					alert(data);
					location.reload();
				}
			});
		}
	});
	
	$("#refreshBtn").click(function() {
		location.reload();
	});
	
	$('#selectAllCheck').click(function(event) {
		if(this.checked) {
			$("input[name='selectItem']").each(function() {
				this.checked = true;
			});
		}
		else {
			$("input[name='selectItem']").each(function() {
				this.checked = false;
			});
		}
	});
	
	$("input[name='selectItem']").click(function() {		// Select-all checkbox should be unckecked if any item is unckecked.
		if(!this.checked) {
			$("input[name='selectAllItem']").attr('checked', false);
		}
	});
});
</script>
</body>
</html>