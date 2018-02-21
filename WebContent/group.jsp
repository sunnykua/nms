<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.via.system.Config" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Group Setting</title>
</head>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<link rel="stylesheet" type="text/css" href="theme/Theme1/device.css">
<script type="text/javascript" src="theme/jQueryUI/jquery.blockUI.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="theme/datatables/jquery.dataTables.css">
<script type="text/javascript" src="theme/datatables/jquery.dataTables.js"></script>
<script type="text/javascript" src="theme/datatables/ip-address.js"></script>
<link rel="stylesheet" type="text/css" href="theme/bootstrap/bootstrap.css">
<script type="text/javascript" src="theme/bootstrap/bootstrap.min.js"></script>
<script>
var groupData;
$(document).ready(function(){
	$("[id^=nameText]").hide();
	
    $('#deviceTable').dataTable({
    	stateSave: true,
    	"paging":   false,
    });
    var table = $('#deviceTable').DataTable();
    table.on('page', function( e, o) {
    	$('input[name=deleteAllGroups]').attr('checked', false);
    	$("input[name='deleteGroups']").each(function() {
            this.checked = false;
        });
    });
});

$(function() {
    $("#addGroupBtu").click(function() {
    	var sendData={
    			groupName : $("#groupName").val()
    	};
    	
    	$("#blockAddDiv_l").block({
    		message: null,
	    	overlayCSS:  { 
	            backgroundColor: '#fff', 
	            opacity:         0.0, 
	            cursor:          'wait' 
	                         },
    	});
        
        $("#blockAddDiv_s").block({
    		message: 'Please Wait...', 
    		css: {
				border: 'none',
				padding: '10px',
		        backgroundColor: '#000',
		        '-webkit-border-radius': '10px',
		        '-moz-border-radius': '10px',
		        opacity: .5,
		        color: '#fff'
		                     },
	    	overlayCSS:  { 
	            backgroundColor: '#fff', 
	            opacity:         0.0, 
	            cursor:          'wait' 
	                         },
    	});
        
        
    	if($("#groupName").val()==""){
    		$("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    		alert("Group name is empty.");
    	} else{
	    	$.ajax({
	    	       type: "POST",
	    	       url: "Device?method=group_add",
	    	       data: sendData,
	    	       success:function(responseText){
    	    	    	   if (responseText == "Success"){
    	    	    		   $("#blockAddDiv_l").unblock();
    	    	    		   $("#blockAddDiv_s").unblock();
    	    	    	       location.reload();
    	    	    	   }else {
    	    	    		   $("#blockAddDiv_l").unblock();
    	    	    		   $("#blockAddDiv_s").unblock();
    	    	    	       alert(responseText);
    	    	    	   }
	    	    	},
	    	});
    	}
    });
});

$(function () {
   $("#deleteGroupBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deleteGroups']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one of the devices");
	   } else {
		   if (confirm("Are you sure to delete these devices?")) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=group_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $('input[name=deleteAllGroups]').attr('checked', false);
			    	   
			    	   var table = $('#deviceTable').DataTable();
			    	   $("[id^=deleteGroups_]").each(function(){
				 		   if(this.checked){
				 			  $(this).parents("tr").addClass('selected1');
				 			  table.row('.selected1').remove().draw( false );
				 		   }
				 	   });
			    	   
			    	   $('iframe').each(function(){
			    		   this.contentWindow.location.reload(true);
			    	   });
			    	   
			    	   alert(data);
			    	   
			        },
		       });
	       }
	   }
   });
});

var nameCount = "";
$(function() {
    $("[id^=nameDiv]").click(function() {
    	nameCount = $(this).attr('id').substr(7);
    	$("#nameText" + $(this).attr('id').substr(7)).show();
    	$("#nameDiv" + $(this).attr('id').substr(7)).hide();
    });
});

$(function() {
	$("[id^=nameText]").bind("enterKey",function(e){
		var count = $(this).attr('id').substr(8);
		$("#nameDiv" + count).show();
		$("#nameText" + count).hide();
		
		if($("#nameText" + count).val().trim() == ""){
			alert("Group name is empty!!");
  	    	$("#nameText" + count).val($("#nameDiv" + count).html());
  	    }else{
			var sendData = {
				groupName : $("#nameText" + count).val(),
				bgroupName : $("#nameDiv" + count).html()
			};
			
			$.ajax({
				   type: "POST",
				   url: "Device?method=groupNameUpdate",
			       data:sendData,
			       success: function(data) {
			    	   if(data == "Repeat"){
			    		   //alert("The new name is repeat.");
			    		   $("#nameText" + count).val($("#nameDiv" + count).html());
			    	   }
			    	   else if(data == "Success"){
			    		   $('iframe').each(function(){
				    		   this.contentWindow.location.reload(true);
				    	   });
			    		   $('select').find('option').each(function() {
			    		        if($(this).val() == $("#nameId" + count).val()) {
			    		        	$(this).html($("#nameText" + count).val());
			    		        }
			    		   });
			    		   $("#nameDiv" + count).html($("#nameText" + count).val());
			    	   }
			        },
		    });
  	    }
		
	});
	$("[id^=nameText]").keyup(function(e){
		if(e.keyCode == 13)
		{
		  $(this).trigger("enterKey");
		}
	});
});

$(function() {
	$('body').mousedown(function() {
		var count = nameCount;
		$("#nameDiv" + count).show();
		$("#nameText" + count).hide();
		
		if($("#nameText" + count).val().trim() == ""){
			alert("Group name is empty!!");
  	    	$("#nameText" + count).val($("#nameDiv" + count).html());
  	    }else{
			var sendData = {
				groupName : $("#nameText" + count).val(),
				bgroupName : $("#nameDiv" + count).html()
			};
			
			$.ajax({
				   type: "POST",
				   url: "Device?method=groupNameUpdate",
			       data:sendData,
			       success: function(data) {
			    	   if(data == "Repeat"){
			    		   //alert("The new name is repeat.");
			    		   $("#nameText" + count).val($("#nameDiv" + count).html());
			    	   }
			    	   else if(data == "Success"){
			    		   $('iframe').each(function(){
				    		   this.contentWindow.location.reload(true);
				    	   });
			    		   $('select').find('option').each(function() {
			    		        if($(this).val() == $("#nameId" + count).val()) {
			    		        	$(this).html($("#nameText" + count).val());
			    		        }
			    		   });
			    		   $("#nameDiv" + count).html($("#nameText" + count).val());
			    	   }
			        },
		    });
  	    }
	});
});

$(function() {
	$("[id^=nameText]").mousedown(function (event) {
		event.stopPropagation();
	});
});

$(function () {
	$('#selectAllGroups').click(function(event) {  //on click 
        if(this.checked) { // check select status
            $("input[name='deleteGroups']").each(function() { //loop through each checkbox
                this.checked = true;  //select all checkboxes with class "checkbox1"               
            });
        }else{
            $("input[name='deleteGroups']").each(function() { //loop through each checkbox
                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
            });         
        }
    });
});

$(function () {
	$("input[name='deleteGroups']").click(function() {
		if(!this.checked) {
			$('input[name=deleteAllGroups]').attr('checked', false);
		}
	});
});

$(function() {
	$("[id^=parent]").on('change', function() {
		var parent = $(this).val();
		var count = $(this).attr('id').substr(6);
		
		//alert($("#nameText" + count).val());
		
		var sendData = {
			groupName : $("#nameText" + count).val(),
			parent : parent
		};
			
		$.ajax({
			   type: "POST",
			   url: "Device?method=groupParent",
		       data:sendData,
		       success: function(data) {
		    	   $('iframe').each(function(){
		    		   this.contentWindow.location.reload(true);
		    		});
		       },
	    });
	});
});

$(function() {
	$("[id^=root]").on('change', function() {
		var groupName = $(this).val();
		var count = $(this).attr('id').substr(4);
		var root;
		var parentVal = $('#parent'+ count).val();
		if($(this).is(":checked")) {
			root="true";
			$('#parent'+ count).attr('disabled', true);
	    	$('#parent'+ count).val("none");
		}
		else {
			root="false";
			$('#parent'+ count).attr('disabled', false);
	    	$('#parent'+ count).val("none");
		}
		
		var sendData = {
				groupName : groupName,
				root : root
			};
				
			$.ajax({
				   type: "POST",
				   url: "Device?method=groupRoot",
			       data:sendData,
			       success: function(data) {
			    	   if(data == "fail"){
			    		   alert("The root was existed.");
			    		   $("#root" + count).attr('checked', false);
			    		   $('#parent'+ count).attr('disabled', false);
			    		   $('#parent'+ count).val(parentVal);
			    	   }
			    	   else{
			    		   $('iframe').each(function(){
				    		   this.contentWindow.location.reload(true);
				    	   });
			    	   }
			        },
		    });
	});
});

function autoResize(id){
    var newheight = "";
    var newwidth = "";

    if(document.getElementById){
        newheight=document.getElementById(id).contentWindow.document .body.scrollHeight;
        newwidth=document.getElementById(id).contentWindow.document .body.scrollWidth;
    }

    document.getElementById(id).height= (newheight) + "px";
    document.getElementById(id).width= (newwidth) + "px";
}
</script>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMainMenu('${sessionScope.userLevel}', <%= Config.isMailQueueEnabled() %>, <%= Config.isSmsQueueEnabled() %>)">
<div id="area" style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix.jspf"%>
	
	<jsp:include page="/Device">
		<jsp:param value="group_view" name="method"/>
	</jsp:include>
	
	<%-- The Beginning of Page Content --%>
	<div id="page_content">
	<div id="page_title">Group List</div>
	<div style="margin: auto;width:900px;">
		<div class="devBtuGroup">
			<input type="text" id="groupName" style="width:100px;"> <button class="btn btn-primary btn-sm" id=addGroupBtu style="width:60px;">Add</button>
			<button class="btn btn-primary btn-sm" id="deleteGroupBtu" style="width:60px;">Delete</button>
		</div>
	
	
	
		<table id="deviceTable">
			<thead>
			<tr class="deviceTable_th">
				<th>Group Name</th>
				<th>Parent</th>
				<th>Root</th>
			    <th><input type="checkbox" id="selectAllGroups" name="deleteAllGroups" <c:if test="${sessionScope.userLevel>1}">disabled</c:if>></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="dev" items="${groupList}" varStatus="status">
				<tr class="deviceTable_tr">
					<td class="td_center_align"><div id="nameDiv${status.count}">${dev.name}</div><input type="text" id="nameText${status.count}" value="${dev.name}"></td>
					<td class="td_center_align"><input type="hidden" id="nameId${status.count}" value="${dev.id}">
						<select id="parent${status.count}" <c:if test="${dev.isRoot()}">disabled</c:if>>
							<option value="none">select parent</option>
							<c:forEach var="par" items="${groupList}">
								<c:if test="${par.id != dev.id}">
								<option value="${par.id}" <c:if test="${par.id == dev.parent}">selected</c:if>>${par.name}</option>
								</c:if>
							</c:forEach>
						</select>
					</td>
					<td class="td_center_align"><input type="checkbox" id="root${status.count}" name="root" value="${dev.name}"<c:if test="${dev.isRoot()}">checked</c:if>></td>
				    <td class="deleteOption" class="td_center_align"><input type="checkbox" id="deleteGroups_${status.count}" name="deleteGroups" value="${dev.name}"<c:if test="${sessionScope.userLevel>1}">disabled</c:if>></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="page_title">Group Preview</div><br>
	<div style="margin: auto;width:900px;border-style:solid;border-width:10px;border-color:#C0C0FF">
		<iframe id="groupShow" src="groupShow.jsp" frameborder="0" style="width:100%; border:none" onLoad="autoResize('groupShow');" scrolling="yes"></iframe>
	</div>
	</div>
</div>
</body>
</html>