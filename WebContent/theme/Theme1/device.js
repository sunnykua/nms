//============================== old ==========================
$(function() {
    $("#addText").click(function() {
    	$("#checkTable").append("<tr span class='validRow' ><td span class='selectCheckbox'>" +'<input name="checkselected" type="checkbox" checked="checked">'+
    			"</td><td>" + '<input name="ip" type="text" value="192.168.0.100">' + "</td><td>" +'<input name="port" type="text" value="161">');
    });
});
$(function() {
    $("#deleteText").click(function() {
    	$("input[name='checkselected']:checked").each(function(){
            $(this).parents("tr").remove(); 
    	});  
    });
});
$(function() {
    $("#checkText").click(function() {
    	var getip=[];
    	
    	$("#checkTable").find("tr").each(function () {
            
            if ($(this).find('input[type="checkbox"]').is(":checked") &&
            		$(this).find('input[name="ip"]').val().length > 0 && $(this).find('input[name="port"]').val().length > 0) {
            	var ip = $(this).find('input[name="ip"]').val() + '/' + $(this).find('input[name="port"]').val();
            	getip.push(ip);
            }
    	});
    	//if(confirm("Are you write these device?"+"\n"+getip)){
    	$("#candidateTable tr:has(td)").remove();
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
    	var ipaddrstring=getip;
    	if(!$("#checkTable tr").find('input[type="checkbox"]').is(":checked")){
    		setTimeout($.unblockUI);
    		alert("Please Add or Select Device Ip");
    	}else{	
    	$.ajax({
    	       type: "POST",
    	       url: "Device?method=check",
    	       data:{ipaddrstring:ipaddrstring},
    	       success:function(responseText){
    	    	    	   var data = responseText.split(',');
    	    	    	   for(var i=0;i<Math.floor(data.length/4);i++){
    	    	    		   if(data[i*4+1]&&data[i*4+2]&&data[i*4+3]!=null){
    	    	    				   $("#candidateTable").append("<tr span class='validRow'><td span class ='selectCheckbox'>" +'<input name="selectedDevices" type="checkbox" checked="checked" value='+data[i*4]+'>'
    	    	    						   +"</td><td span name = ipip>" + data[i*4] + "</td><td>" + data[i*4+1] + "</td><td>" + data[i*4+2] + "</td><td>"+ data[i*4+3] + "</td></tr>"+"\n");
    	    	    				   
    	    	    			}else
    	    	    				   $("#candidateTable").append("<tr span class='unvalidRow'><td span class='selectCheckbox'>" +'<input type="checkbox" disabled="disabled">'
    	    	    						   +"</td><td>" + data[i*4] + "</td><td>" + data[i*4+1] + "</td><td>" + data[i*4+2] + "</td><td>"+ data[i*4+3] + "</td></tr>"+"\n");
    	    	    			if(data[i*4]!=null){
    	    	    			    		   setTimeout($.unblockUI);
    	    	       			}
    	    	    		   
    	    	    		}
    	    	    	   if(responseText.length==0){
    	    	    		   setTimeout($.unblockUI);
    	    	    	   }
    	    	},
    	});
        }
    	//}
    });
});
$(function() {
    $("#addButton").click(function() {
    	var checkadd=[];
    	var selected = checkadd;
    	
    	$("input[name='selectedDevices']:checked").each(function(){
    	checkadd.push($(this).val());
    	});
    	
    	if(selected.length < 1) {
 		   alert("Please select at least one of the devices");
 	   } else {
 		  if(confirm("Are you sure to choose these devices?\n"+selected)) {
 			 $.ajax({
	    			type: "POST",
	    			url: "Device?method=add",
	    			data:{checkadd:checkadd},
	    			success: function(data) {
	    				location.reload();
	    			}
	    		});
 		  }
 	   }
    });
});
//============================== new ==========================
function remoteLogin(ip) {
	var sendData = {
			ip:ip
			};
	$.ajax({
		   type: "POST",
		   url: "Login?type=remote_match",
	       data: sendData,
	       success: function(data) {
	    	   var newData = data.split(",");
	    	   if(data == "failed"){
	    		   alert("failed");
	    	   }
	    	   else if (newData[1] == ""){
	    		   window.open("http://" + ip + "/login_remote.jsp?remote=" + newData[0],"remote" + newData[0],"fullscreen,scrollbars");
	    	   }
	    	   else {
	    		   window.open("http://" + ip + newData[1] + "/login_remote.jsp?remote=" + newData[0],"remote" + newData[0],"fullscreen,scrollbars");
	    	   }
	       },
	});
}

$(function () {
   $("#deleteNmsBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deleteNmsDevices']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one of the devices");
	   } else {
		   if (confirm("Are you sure to delete these devices?")) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=nms_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $('input[name=deleteAllNmsDevices]').attr('checked', false);
			    	   
			    	   var table = $('#nmsTable').DataTable();
			    	   $("[id^=deleteNmsDevices_]").each(function(){
				 		   if(this.checked){
				 			  $(this).parents("tr").addClass('selected1');
				 			  table.row('.selected1').remove().draw( false );
				 		   }
				 	   });
			    	   alert(data);
			        },
		       });
	       }
	   }
   });
});

$(function () {
   $("#refreshNmsBtu").click(function() {
	   var selected = [];
	   
	   $("input[name='deleteNmsDevices']:checked").each(function() {
		   selected.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   selected = null;
		   //alert("No device");
		   if (confirm("Are you sure to refresh all devices?")) {
			   $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=nmsRefresh",
			       data:{selected:selected},
			       success: function(data) {
			    	   alert(data);
			    	   location.reload();
			        },
		       });
		   }
	   } 
	   else {
		   if (confirm("Are you sure to refresh these devices?")) {
	    	  $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=nmsRefresh",
			       data:{selected:selected},
			       success: function(data) {
			    	   alert(data);
			    	   location.reload();
			        },
		       });
	       }
	   }
   });
});

$(function () {
	$('#exportDeviceListBtu').click(function() {
		$.ajax({
			url : "Device?method=devicelist_export",
			type : "POST",
			dataType: "JSON",
			success : function(data) {
				if (data.isDone) {
					$('<a href=\"' + data.file + '\" download></a>')[0].click();
				}
				else {
					alert("Failed to export device list.");
				}
			}
		});
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
	    		   url: "Device?method=device_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $('input[name=deleteAllDevices]').attr('checked', false);
			    	   
			    	   var table = $('#deviceTable').DataTable();
			    	   $("[id^=deleteDevices_]").each(function(){
				 		   if(this.checked){
				 			  $(this).parents("tr").addClass('selected1');
				 			  table.row('.selected1').remove().draw( false );
				 		   }
				 	   });
			    	   
			    	   alert(data);
			    	   
			    	   /*$("[id^=deleteDevices_]").each(function(){
			    		   if(this.checked){
			    			   $(this).parents("tr").remove();
			    			   $("#extra_" + $(this).attr('id').substr(14)).parents("tr").remove();
			    			   location.reload();
					       }
				       });
			    	   $("[id^=deleteDevices_acap]").each(function(){
			    		   if(this.checked){
			    			   $(this).parents("tr").remove();
			    			   $("#extra_acap" + $(this).attr('id').substr(18)).parents("tr").remove();
			    			   location.reload();
					       }
				       });*/
			        },
		       });
	       }
	   }
   });
});

$(function () {
   $("#refreshDeviceBtu").click(function() {
	   var selected = [];
	   
	   $("input[name='deleteDevices']:checked").each(function() {
		   selected.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   selected = null;
		   //alert("No device");
		   if (confirm("Are you sure to refresh all devices?")) {
			   $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=deviceRefresh",
			       data:{selected:selected},
			       success: function(data) {
			    	   alert(data);
			    	   location.reload();
			        },
		       });
		   }
	   } 
	   else {
		   if (confirm("Are you sure to refresh these devices?")) {
	    	  $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=deviceRefresh",
			       data:{selected:selected},
			       success: function(data) {
			    	   alert(data);
			    	   location.reload();
			        },
		       });
	       }
	   }
   });
});

$(function () {
	   $("#devGroupApply").click(function() {
		   var selected = [];
		   var count = [];
		   
		   $("input[id^='deleteDevices_']:checked").each(function() {
			   selected.push($(this).val());
			   count.push($(this).attr('id').substr(14));
		   });
		   
		   if(selected.length < 1) {
			   alert("Please select at least one of the devices");
		   } else {
			   if (confirm("Are you sure to select these devices?")) {
		    	  $.ajax({
		    		   type: "POST",
		    		   url: "Device?method=devGroup_set",
				       data:{
				    	   selected:selected,
				    	   groupName: $("#devGroupSelect").val()
				       },
				       success: function(data) {
				    	   for(var i=0; i<count.length; i++){
				    		   var value = count[i];
				    		   $("#groupDiv" + value).html($("#devGroupSelect").val());
				    	   }
				    	   $("input[id^='deleteDevices_']:checked").each(function() {
				    		   this.checked = false;
				    	   });
				    	   $("#selectallDevice").prop("checked", false);
				    	   //alert(data);
				        },
			       });
		       }
		   }
	   });
	});

$(function () {
   $("#devFilApply").click(function() {
	   var selected = [];
	   
	   $("input[name='deleteDevices']:checked").each(function() {
		   selected.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one of the devices");
	   } else {
		   if (confirm("Are you sure to select these devices?")) {
	    	  $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=devFil_set",
			       data:{
			    	   selected:selected,
			    	   profile: $("#devFil_select").val()
			       },
			       success: function(data) {
			    	   alert(data);
			        },
		       });
	       }
	   }
   });
});

$(function() {
	$(".selectVersion").click(function () {
        switch ($(this).val()) {
	        case "-1":
	            $("#scanOption").find("*").prop('disabled',true);
            break;
        	case "0":
                $("#scanOption").find("*").prop('disabled',false);
                break;
            case "1":
                $("#scanOption").find("*").prop('disabled',false);
                break;
            case "2":
                $("#scanOption").find("*").prop('disabled',false);
                break;
            case "3":
                $("#scanOption").find("*").prop('disabled',true);
                break;
        }
    });
});

$(function() {
	$("[id^=snmp_version]").click(function () {
        switch ($(this).val()) {
            case "1":
            	$("#snmpV2Setting").find("*").prop('disabled',false);
            	$("#security_name" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#security_level" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#auth_protocol" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#auth_password" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#priv_protocol" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#priv_password" + $(this).attr('id').substr(12)).prop('disabled',true);
                break;
            case "2":
            	$("#snmpV2Setting").find("*").prop('disabled',false);
            	$("#security_name" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#security_level" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#auth_protocol" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#auth_password" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#priv_protocol" + $(this).attr('id').substr(12)).prop('disabled',true);
                $("#priv_password" + $(this).attr('id').substr(12)).prop('disabled',true);
                break;
            case "3":
                $("#snmpV2Setting").find("*").prop('disabled',true);
                $("#security_name" + $(this).attr('id').substr(12)).prop('disabled',false);
                $("#security_level" + $(this).attr('id').substr(12)).prop('disabled',false);
                switch ($("[id^=security_level]").val()) {
        	    case "1":
        	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        break;
        	    case "2":
        	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	        break;
        	    case "3":
        	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
        	        break;
        	}
                break;
        }
    });
	
	$("[id^=security_level]").click(function () {
        switch ($(this).val()) {
            case "1":
            	$("#auth_protocol" + $(this).attr('id').substr(14)).prop('disabled',true);
                $("#auth_password" + $(this).attr('id').substr(14)).prop('disabled',true);
                $("#priv_protocol" + $(this).attr('id').substr(14)).prop('disabled',true);
                $("#priv_password" + $(this).attr('id').substr(14)).prop('disabled',true);
                break;
            case "2":
            	$("#auth_protocol" + $(this).attr('id').substr(14)).prop('disabled',false);
                $("#auth_password" + $(this).attr('id').substr(14)).prop('disabled',false);
                $("#priv_protocol" + $(this).attr('id').substr(14)).prop('disabled',true);
                $("#priv_password" + $(this).attr('id').substr(14)).prop('disabled',true);
                break;
            case "3":
            	$("#auth_protocol" + $(this).attr('id').substr(14)).prop('disabled',false);
                $("#auth_password" + $(this).attr('id').substr(14)).prop('disabled',false);
                $("#priv_protocol" + $(this).attr('id').substr(14)).prop('disabled',false);
                $("#priv_password" + $(this).attr('id').substr(14)).prop('disabled',false);
                break;
        }
    });
});

$(document).ready(function(){
    switch ($("[id^=snmp_version]").val()) {
        case "1":
        	$("#snmpV2Setting").find("*").prop('disabled',false);
        	$("#security_name" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
            $("#security_level" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
            break;
        case "2":
        	$("#snmpV2Setting").find("*").prop('disabled',false);
        	$("#security_name" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
            $("#security_level" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
        	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
            break;
        case "3":
            $("#snmpV2Setting").find("*").prop('disabled',true);
            $("#security_name" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
            $("#security_level" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
            switch ($("[id^=security_level]").val()) {
    	    case "1":
    	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        break;
    	    case "2":
    	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
    	        break;
    	    case "3":
    	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
    	        break;
    	}
            break;
    }
});

$(document).ready(function(){
    switch ($("[id^=security_level]").val()) {
	    case "1":
	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        break;
	    case "2":
	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',true);
	        break;
	    case "3":
	    	$("#auth_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        $("#auth_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        $("#priv_protocol" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        $("#priv_password" + $("[id^=security_level]").attr('id').substr(14)).prop('disabled',false);
	        break;
	}
});

$(function() {
    $("#addNms").click(function() {
    	var sendData={
    			ip : $("#add_nms_ip").val(),
    			alias : $("#nms_alias").val()
    	};
    	
    	$("#blockAddDiv_nms_l").block({
    		message: null,
	    	overlayCSS:  { 
	            backgroundColor: '#fff', 
	            opacity:         0.0, 
	            cursor:          'wait' 
	                         },
    	});
        
        $("#blockAddDiv_nms_s").block({
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
        
        
    	if($("#add_nms_ip").val().trim() == ""){
    		$("#blockAddDiv_nms_l").unblock();
    	    $("#blockAddDiv_nms_s").unblock();
    		alert("IP is empty.");
    	} else if($("#nms_alias").val().trim() == ""){
    		$("#blockAddDiv_nms_l").unblock();
    	    $("#blockAddDiv_nms_s").unblock();
    		alert("Alias is empty.");
    	} else{
	    	$.ajax({
	    	       type: "POST",
	    	       url: "Device?method=nms_add",
	    	       data: sendData,
	    	       success:function(responseText){
    	    	    	   if (responseText == "Success"){
    	    	    		   $("#blockAddDiv_nms_l").unblock();
    	    	    		   $("#blockAddDiv_nms_s").unblock();
    	    	    	       location.reload();
    	    	    	   }else {
    	    	    		   $("#blockAddDiv_nms_l").unblock();
    	    	    		   $("#blockAddDiv_nms_s").unblock();
    	    	    	       alert(responseText);
    	    	    	   }
	    	    	},
	    	});
    	}
    });
});

$(function() {
    $("#addDevice").click(function() {
    	var version = $("[name='version']:checked").val();
    	var sendData={};
    	if(version == "-1"){
    		sendData={
    				version : "-1",
    				ip : $("#add_ip").val() +  '/' + $("#add_port").val(), 
    				type : $("#vn_type").val()
    				};
    	}else if(version == "0"){
    		sendData={
    				version : "0",
    				ip : $("#add_ip").val() +  '/' + $("#add_port").val(), 
    				type : $("#ping_type").val()
    				};
    	}else if(version == "1"){
    		sendData={
    				version : "1",
    				ip : $("#add_ip").val() +  '/' + $("#add_port").val(),
    				community : $("#v1_community").val(),
    				snmpTimeout : $("#add_snmp_timeout").val()
    				};
    	}else if(version == "2"){
    		sendData={
    				version : "2",
    				ip : $("#add_ip").val() +  '/' + $("#add_port").val(),
    				community : $("#v2_community").val(),
    				snmpTimeout : $("#add_snmp_timeout").val()
    				};
    	}else if(version == "3"){
    		sendData={
    				version : "3",
    				ip : $("#add_ip").val() +  '/' + $("#add_port").val(),
    				snmpTimeout : $("#add_snmp_timeout").val(),
    				securityName : $("#security_name").val(),
    				securityLevel : $("#security_level").val(),
    				authProtocol : $("#auth_protocol").val(),
    				authPassword : $("#auth_password").val(),
    				privProtocol : $("#priv_protocol").val(),
    				privPassword : $("#priv_password").val(),
    				};
    	}
    	
    	//differ part textbox
    	/*$("#addTable tr").each(function () {
            
            if ($("#addTable tr").find('input[name="ipa"]').val().length > 0 && $("#addTable tr").find('input[name="ipb"]').val().length > 0 
            		&& $("#addTable tr").find('input[name="ipc"]').val().length > 0 && $("#addTable tr").find('input[name="ipd"]').val().length > 0 && $("#addTable tr").find('input[name="port"]').val().length > 0) {
            	var ip = $(this).find('input[name="ipa"]').val() + '.' + $(this).find('input[name="ipb"]').val() + '.' 
            	+ $(this).find('input[name="ipc"]').val() + '.' + $(this).find('input[name="ipd"]').val() +  '/' + $(this).find('input[name="port"]').val();
            	getip.push(ip);
            }
    	});*/
    	
    	/*$.ajax({
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
    	});*/
    	
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
        
        
    	if($("#add_ip").val()=="" || $("#add_port").val()==""){
    		$("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    		alert("IP and port are empty.");
    	} else if ($("#add_snmp_timeout").val()=="") {
    		$("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Timeout is empty.");
    	} else if (version == null) {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Please select one version.");
    	} else if (version == "-1" && $("#vn_type").val() == "") {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Please select one non-management device.");
    	} else if (version == "0" && $("#ping_type").val() == "") {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Please select one ping only device.");
    	} else if (version == "1" && $("#v1_community").val() == "" || version == "2" && $("#v2_community").val() == "") {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Community is empty.");
    	} else if (version == "3" && ($("#security_name").val() == "")) {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Snmp v3 has empty column.");
    	} else{
	    	$.ajax({
	    	       type: "POST",
	    	       url: "Device?method=device_add",
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
	    	    	    	       //location.reload();
	    	    	    	   }
	    	    	},
	    	});
        }
    });
});

$(function() {
    $("#scanDevice").click(function() {

    	if (scanPressed) {
    	    alert("Scan is running.");
    	    return;
    	}
    	
    	var version = $("[name='version']:checked").val();
    	var sendData={};
    	if(version == "0"){
    		sendData={
    				version : "0",
    				startip: $("#scan_ip_from").val() + '/' + $("#scan_port").val(),
			        endip: $("#scan_ip_to").val() + '/' +  $("#scan_port").val(),
    				type : $("#ping_type").val()
    				};
    	}else if(version == "1"){
    		sendData={
    				version : "1",
    				startip: $("#scan_ip_from").val() + '/' + $("#scan_port").val(),
			        endip: $("#scan_ip_to").val() + '/' +  $("#scan_port").val(),
    				community : $("#v1_community").val(),
    				snmpTimeout : $("#scan_snmp_timeout").val()
    				};
    	}else if(version == "2"){
    		sendData={
    				version : "2",
    				startip: $("#scan_ip_from").val() + '/' + $("#scan_port").val(),
			        endip: $("#scan_ip_to").val() + '/' +  $("#scan_port").val(),
    				community : $("#v2_community").val(),
    				snmpTimeout : $("#scan_snmp_timeout").val()
    				};
    	}else if(version == "3"){
    		sendData={
    				version : "3",
    				startip: $("#scan_ip_from").val() + '/' + $("#scan_port").val(),
			        endip: $("#scan_ip_to").val() + '/' +  $("#scan_port").val(),
    				snmpTimeout : $("#scan_snmp_timeout").val(),
    				securityName : $("#security_name").val(),
    				securityLevel : $("#security_level").val(),
    				authProtocol : $("#auth_protocol").val(),
    				authPassword : $("#auth_password").val(),
    				privProtocol : $("#priv_protocol").val(),
    				privPassword : $("#priv_password").val(),
    				};
    	}
        
    	/*$.ajax({
    		complete: function() {
    			$.blockUI({ 
    			message: $('#scandiv_display'),
    			css: {
    				border: 'none',
    				padding: '15px',
    		        backgroundColor: '#000',
    		        '-webkit-border-radius': '30px',
    		        '-moz-border-radius': '10px',
    		        opacity: .5,
    		        color: '#fff'
    		                     }, 
    			});
    		},
    	});*/
    	
    	$("#blockAddDiv_l").block({
    		message: null,
	    	overlayCSS:  { 
	            backgroundColor: '#fff', 
	            opacity:         0.0, 
	            cursor:          'wait' 
	                         },
    	});
        
        $("#blockAddDiv_s").block({
    		message: $('#scandiv_display'), 
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
        
    	if($("#scan_ip_from").val()=="" || $("#scan_ip_to").val()=="" || $("#scan_port").val()==""){
    		$("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    		alert("IP and port are empty.");
    	} else if ($("#scan_snmp_timeout").val()=="") {
    		$("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Timeout is empty.");
    	} else if (version == null) {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Please select one version.");
    	} else if (version == "0" && $("#ping_type").val() == "") {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Please select one ping only device.");
    	} else if (version == "1" && $("#v1_community").val() == "" || version == "2" && $("#v2_community").val() == "") {
    	    $("#blockAddDiv_l").unblock();
    	    $("#blockAddDiv_s").unblock();
    	    alert("Community is empty.");
    	} else{

    	    scanPressed = true;
    	    
    	    $.ajax({
    	       type: "POST",
    	       url: "Device?method=device_scan",
               cache: false,
    	       data: sendData,
    	       //dataType: "JSON",
    	       success:function(data){
    	    	   if (data == "false") {
    	    		   $("#blockAddDiv_l").unblock();
    	    		   $("#blockAddDiv_s").unblock();
    	    		   alert("Scan has already in running.\nPlease wait until standby.");
    	    	   }

    	    	   setTimeout(function(){ scan_status(); }, 1000);
    	    	   scanIsRunning = true;
    	    	},
    	    });
        }
    });
});

var scanPressed = false;
var scanIsRunning = false;

$(document).ready(function () {
    scan_status();
});

function scan_status(){
	var senddata ={scanip:"192.168.0.100"};
	   $.ajax({
		   type: "POST",
		   url: "Device?method=device_scan_status",
		   cache: false,
		   //async: false,
		   data: senddata,
		   //dataType: "JSON",
	       success: function(data) {
	           $("#scandiv").html(data);
	           $("#scandiv_display").html(data);
	    	   
	           if(data == "standby" || data == "reach_limit"){
		    	   scanPressed = false;
		    	   if (data == "reach_limit")
		    	       alert("Warning, device number has reached license limitation.");
		    	   if (scanIsRunning) {
		    	       scanIsRunning = false;
		    	       alert("This page will refresh now.");
		    	       location.reload(true);        // reload by no-cache mode
		    	   }
	    	   }
	    	   else {
	    	       scanIsRunning = true;
		    	   setTimeout(function(){ scan_status(); }, 2000);
	    	   }
	       },
	   });
}

$(function () {
   $("[id^=remoteAccountSet_]").click(function() {
	   var checkSelect = "";
	   var accountItem = "remoteAccountChk_" + $(this).attr('id').substr(17);
	   
	   $("input[name= " + accountItem + "]:checked").each(function() {
		   checkSelect += $(this).val() + ",";
	   });
	   
	   var sendData = {
			   ip : $("#nms_ip" + $(this).attr('id').substr(17)).val(),
			   userItems : checkSelect
	   };
	   
	   if (confirm("Are you sure to select these user?")) {
    	   $.ajax({
    		   type: "POST",
    		   url: "Device?method=nms_remoteAccountSet",
		       data: sendData,
		       success: function(data) {
		    	   alert(data);
		        },
	       });
       }
		   
   });
});

$(function() {
	$("[id^=devFilterApply_]").click(function() {
		var devFilterArray = [];
		
		devFilterArray.push($("#switchid_" + $(this).attr('id').substr(15)).val());
		
    	if ($("#devFilter_check" + $(this).attr('id').substr(15)).is(":checked")) {
            var value = 1 + '/' + $("#devFilter_select" + $(this).attr('id').substr(15)).val();
            devFilterArray.push(value);
        }else {
        	var value = 0 + '/' + $("#devFilter_select" + $(this).attr('id').substr(15)).val();
        	devFilterArray.push(value);
        }
    	
	    //alert(devFilterArray);
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=devFilter_set",
 	       data:{devFilterArray:devFilterArray},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	});
});

$(function() {
	$("[id^=show_]").click(function(event) {
		event.preventDefault();
		if ($(".extradiv").not($("#extra_" + $(this).attr('id').substr(5))).is( ":visible" )){
			// Hide - slide up.
			$(".extradiv").hide();
			$("#extra_" + $(this).attr('id').substr(5)).show();
			
			var mainIframe = $("#device_setting_" + $(this).attr('id').substr(5));
			mainIframe.attr("src", mainIframe.data("src"));
        	var iframe = $("#topology_set_" + $(this).attr('id').substr(5));
    	    iframe.attr("src", iframe.data("src"));
    	    var iframe1 = $("#port_status_" + $(this).attr('id').substr(5));
    	    iframe1.attr("src", iframe1.data("src"));
    	    var iframe2 = $("#vlan_interface_" + $(this).attr('id').substr(5));
    	    iframe2.attr("src", iframe2.data("src"));
    	    var iframe3 = $("#poe_interface_" + $(this).attr('id').substr(5));
    	    iframe3.attr("src", iframe3.data("src"));
    	    var iframe4 = $("#trap_table_" + $(this).attr('id').substr(5));
    	    iframe4.attr("src", iframe4.data("src"));
		} else {
        	// Show - slide down.
			$(".extra_acap").hide();
        	$("#extra_" + $(this).attr('id').substr(5)).show();
        	
        	var mainIframe = $("#device_setting_" + $(this).attr('id').substr(5));
			mainIframe.attr("src", mainIframe.data("src"));
        	var iframe = $("#topology_set_" + $(this).attr('id').substr(5));
    	    iframe.attr("src", iframe.data("src"));
    	    var iframe1 = $("#port_status_" + $(this).attr('id').substr(5));
    	    iframe1.attr("src", iframe1.data("src"));
    	    var iframe2 = $("#vlan_interface_" + $(this).attr('id').substr(5));
    	    iframe2.attr("src", iframe2.data("src"));
    	    var iframe3 = $("#poe_interface_" + $(this).attr('id').substr(5));
    	    iframe3.attr("src", iframe3.data("src"));
    	    var iframe4 = $("#trap_table_" + $(this).attr('id').substr(5));
    	    iframe4.attr("src", iframe4.data("src"));
        }
	});
});

$(function() {
	$("[id^=show_acap]").click(function(event) {
		event.preventDefault();
		if ($(".extra_acap").not($("#extra_acap" + $(this).attr('id').substr(9))).is( ":visible" )){
			// Hide - slide up.
			$(".extra_acap").hide();
			$("#extra_acap" + $(this).attr('id').substr(9)).show();
			
			var acapIframe = $("#acap_setting_" + $(this).attr('id').substr(9));
			acapIframe.attr("src", acapIframe.data("src"));
		} else {
        	// Show - slide down.
			$(".extradiv").hide();
        	$("#extra_acap" + $(this).attr('id').substr(9)).show();
        	
        	var acapIframe = $("#acap_setting_" + $(this).attr('id').substr(9));
			acapIframe.attr("src", acapIframe.data("src"));
        }
	});
});

$(function() {
	$("[id^=Choose_port_ip_]").click(function() {
		var select_port_ip = [];
		var count=0;
		select_port_ip.push($("#switchid_" + $(this).attr('id').substr(15)).val());
	    $("#extra_" + $(this).attr('id').substr(15)).find("tr:not(:has(th))").each(function () {
	    	 
	    	 count++;
	            if ($(this).find("[name='auto']").is(":checked") && $(this).find("#CP").is(":checked") && $(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'manual'+ '/' +$(this).find(":selected").val() + '/' + 1 + '/' + 1 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if (!$(this).find("[name='auto']").is(":checked") && $(this).find("#CP").is(":checked") && $(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'auto'+ '/' +$(this).find(":selected").val() + '/' + 1 + '/' + 1 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if ($(this).find("[name='auto']").is(":checked") && !$(this).find("#CP").is(":checked") && $(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'manual'+ '/' +$(this).find(":selected").val() + '/' + 0 + '/' + 1 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if (!$(this).find("[name='auto']").is(":checked") && !$(this).find("#CP").is(":checked") && $(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'auto'+ '/' +$(this).find(":selected").val() + '/' + 0 + '/' + 1 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if ($(this).find("[name='auto']").is(":checked") && $(this).find("#CP").is(":checked") && !$(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'manual'+ '/' +$(this).find(":selected").val() + '/' + 1 + '/' + 0 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if (!$(this).find("[name='auto']").is(":checked") && $(this).find("#CP").is(":checked") && !$(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'auto'+ '/' +$(this).find(":selected").val() + '/' + 1 + '/' + 0 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else if ($(this).find("[name='auto']").is(":checked") && !$(this).find("#CP").is(":checked") && !$(this).find("[name='poe_check']").is(":checked")) {
	            	var value = count  + '/' + 'manual'+ '/' +$(this).find(":selected").val() + '/' + 0 + '/' + 0 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }else{
	            	var value = count  + '/' + 'auto'+ '/' +$(this).find(":selected").val() + '/' + 0 + '/' + 0 + '/' + $(this).find("[name='poe_select']").val();
	            	select_port_ip.push(value);
	            }
	    });
	    
	    //alert(select_port_ip);
		
		alert("WARNING:\nIf you modify IP on any interface, the remote side along the link should also modify.");
		
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=remote_set",
 	       data:{select_port_ip:select_port_ip},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
 	});
	    
	});
});

$(function() {
	$("[id^=choose_port_ip_]").click(function() {
		var select_port_ip = [];
		select_port_ip.push($("#switchid_" + $(this).attr('id').substr(15)).val());
	    $("#extra_" + $(this).attr('id').substr(15)).find("tr:not(:has(th))").each(function () {
	    	 
	            if ($(this).find("[name='auto']").is(":checked")) {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 'manual'+ '/' +$(this).find(":selected").val();
	            	select_port_ip.push(value);
	            }else {
	            	var value = $(this).find("[name='ifIndex']").val()  + '/' + 'auto'+ '/' +$(this).find(":selected").val();
	            	select_port_ip.push(value);
	            }
	    });
	    
	    //alert(select_port_ip);
		
		alert("WARNING:\nIf you modify IP on any interface, the remote side along the link should also modify.");
		
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=remote_set",
 	       data:{select_port_ip:select_port_ip},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	    
	});
});

$(function() {
	$("[id^=poeScheduleApply_]").click(function() {
		var poeArray = [];
		poeArray.push($("#switchid_" + $(this).attr('id').substr(17)).val());
	    $("#extra_" + $(this).attr('id').substr(17)).find("tr:not(:has(th))").each(function () {
	    	 if ($(this).find("[name='poe_check']").is(":checked")) {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 1 + '/' + $(this).find("[name='poe_select']").val();
	            	poeArray.push(value);
	            }else {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 0 + '/' + $(this).find("[name='poe_select']").val();
	            	poeArray.push(value);
	            }
	    });
	    //alert(poeArray);
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=poeSchedule_set",
 	       data:{poeArray:poeArray},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	});
});

$(function() {
	$("[id^=monitorApply_]").click(function() {
		var monitorArray = [];
		monitorArray.push($("#switchid_" + $(this).attr('id').substr(13)).val());
	    $("#extra_" + $(this).attr('id').substr(13)).find("tr:not(:has(th))").each(function () {
	    	 if ($(this).find("#CP").is(":checked")) {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 1;
	            	monitorArray.push(value);
	            }else {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 0;
	            	monitorArray.push(value);
	            }
	    });
	    //alert(monitorArray);
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=monitor_set",
 	       data:{monitorArray:monitorArray},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	});
});

$(function() {
	$("[id^=trapApply_]").click(function() {
		if($("#trapIpAddr_" + $(this).attr('id').substr(10)).val()==""){
  	    	alert("Address is empty.");
  	    }else if($("#trapCommunity_" + $(this).attr('id').substr(10)).val()==""){
  	    	alert("Community string is empty.");
  	    }else if($("#trapUdp_" + $(this).attr('id').substr(10)).val()==""){
  	    	alert("Port is empty.");
  	    }else if($('#trapTable tr').length == 2){
  	    	alert("Data table is full.");
  	    }else{
			var trapArray = [$("#switchid_" + $(this).attr('id').substr(10)).val(),
			                 $("#trapIpAddr_" + $(this).attr('id').substr(10)).val(),
			                 $("#trapVersion_" + $(this).attr('id').substr(10)).find(":selected").val(),
			                 $("#trapCommunity_" + $(this).attr('id').substr(10)).val(),
			                 $("#trapUdp_" + $(this).attr('id').substr(10)).val(),
			                 ];
			//alert(trapArray);
			$.ajax({
		 	       type: "POST",
		 	       url: "Device?method=trap_set",
		 	       data:{trapArray:trapArray},
		 	       success:function(responseText){
		 	           //alert(responseText);
		 	           location.reload();
		 	    	},
			});
  	    }
	});
});

$(function () {
   $("[id^=deleteTrapBtu_]").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   var ip = $("#switchid_" + $(this).attr('id').substr(14)).val();
	   
	   $("input[name='deleteTrap']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one.");
	   } else {
		   if (confirm("Are you sure to delete?\n"+selected)) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Device?method=trap_delete",
			       data:{ip:ip,checkdelete:checkdelete},
			       success: function(data) {
			    	   $("input[name='deleteTrap']:checked").each(function(){
			    		   if(this.checked){
			    			   $(this).parents("tr").remove();
					       }
				       });
			        },
		       });
	       }
	   }
   });
});

$(function() {
	$("[id^=acTrapApply_]").click(function() {
			var acIp = $("#switchid_" + $(this).attr('id').substr(12)).val();
			var acPhyAddr = $("#switchPhy_" + $(this).attr('id').substr(12)).val();
		
			var apJoinMailCheck;
			var apJoinMail = $("#apJoinMail_" + $(this).attr('id').substr(12)).val();
			if($("#apJoinMailCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				apJoinMailCheck = "1";
			}
			else {
				apJoinMailCheck = "0";
			}
			
			var apJoinSmsCheck;
			var apJoinSms = $("#apJoinSms_" + $(this).attr('id').substr(12)).val();
			if($("#apJoinSmsCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				apJoinSmsCheck = "1";
			}
			else {
				apJoinSmsCheck = "0";
			}
			
			var apLeaveMailCheck;
			var apLeaveMail = $("#apLeaveMail_" + $(this).attr('id').substr(12)).val();
			if($("#apLeaveMailCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				apLeaveMailCheck = "1";
			}
			else {
				apLeaveMailCheck = "0";
			}
			
			var apLeaveSmsCheck;
			var apLeaveSms = $("#apLeaveSms_" + $(this).attr('id').substr(12)).val();
			if($("#apLeaveSmsCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				apLeaveSmsCheck = "1";
			}
			else {
				apLeaveSmsCheck = "0";
			}
			
			var acWarmStartMailCheck;
			var acWarmStartMail = $("#acWarmStartMail_" + $(this).attr('id').substr(12)).val();
			if($("#acWarmStartMailCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				acWarmStartMailCheck = "1";
			}
			else {
				acWarmStartMailCheck = "0";
			}
			
			var acWarmStartSmsCheck;
			var acWarmStartSms = $("#acWarmStartSms_" + $(this).attr('id').substr(12)).val();
			if($("#acWarmStartSmsCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				acWarmStartSmsCheck = "1";
			}
			else {
				acWarmStartSmsCheck = "0";
			}
			
			var acColdStartMailCheck;
			var acColdStartMail = $("#acColdStartMail_" + $(this).attr('id').substr(12)).val();
			if($("#acColdStartMailCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				acColdStartMailCheck = "1";
			}
			else {
				acColdStartMailCheck = "0";
			}
			
			var acColdStartSmsCheck;
			var acColdStartSms = $("#acColdStartSms_" + $(this).attr('id').substr(12)).val();
			if($("#acColdStartSmsCheck_" + $(this).attr('id').substr(12)).is(":checked")){
				acColdStartSmsCheck = "1";
			}
			else {
				acColdStartSmsCheck = "0";
			}
		var sendData = {
				acIp :acIp,
				acPhyAddr : acPhyAddr,
				apJoinMailCheck : apJoinMailCheck,
				apJoinMail : apJoinMail,
				apJoinSmsCheck : apJoinSmsCheck,
				apJoinSms : apJoinSms,
				apLeaveMailCheck : apLeaveMailCheck,
				apLeaveMail : apLeaveMail,
				apLeaveSmsCheck : apLeaveSmsCheck,
				apLeaveSms : apLeaveSms,
				acWarmStartMailCheck : acWarmStartMailCheck,
				acWarmStartMail : acWarmStartMail,
				acWarmStartSmsCheck : acWarmStartSmsCheck,
				acWarmStartSms : acWarmStartSms,
				acColdStartMailCheck : acColdStartMailCheck,
				acColdStartMail : acColdStartMail,
				acColdStartSmsCheck : acColdStartSmsCheck,
				acColdStartSms : acColdStartSms
		};
		
		$.ajax({
	 	       type: "POST",
	 	       url: "Device?method=acTrap_set",
	 	       data: sendData,
	 	       success:function(responseText){
	 	           alert(responseText);
	 	    	},
		});
		
	});
});

$(function() {
	$("[id^=portAliasApply_]").click(function() {
		var aliasArray = [];
		aliasArray.push($("#switchid_" + $(this).attr('id').substr(15)).val());
	    $("#extra_" + $(this).attr('id').substr(15)).find("tr:not(:has(th))").each(function () {
	    	var aliasName = $(this).find("[name='port_alias']").val() == "" ? " " : $(this).find("[name='port_alias']").val();
	    	var value = $(this).find("[name='ifIndex']").val() + '/' + aliasName;
	    	aliasArray.push(value);
	    });
	    //alert(aliasArray);
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=portAlias_set",
 	       data:{aliasArray:aliasArray},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	});
});

$(function() {
	$("[id^=mailFilterApply_]").click(function() {
		var mailFilterArray = [];
		mailFilterArray.push($("#switchid_" + $(this).attr('id').substr(16)).val());
	    $("#extra_" + $(this).attr('id').substr(16)).find("tr:not(:has(th))").each(function () {
	    	 if ($(this).find("[name='mailFilter_check']").is(":checked")) {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 1 + '/' + $(this).find("[name='mailFilter_select']").val();
	            	mailFilterArray.push(value);
	            }else {
	            	var value = $(this).find("[name='ifIndex']").val() + '/' + 0 + '/' + $(this).find("[name='mailFilter_select']").val();
	            	mailFilterArray.push(value);
	            }
	    });
	    //alert(mailFilterArray);
		$.ajax({
 	       type: "POST",
 	       url: "Device?method=mailFilter_set",
 	       data:{mailFilterArray:mailFilterArray},
 	       success:function(responseText){
 	           alert(responseText);
 	    	},
		});
	});
});

var devProfile=[];
$(function() {
	$('[id^=devFilter_check]').click(function(event) {
		if(this.checked) {
			devProfile=[];
	    	$('#devFilter_select'+ $(this).attr('id').substr(15)).attr('disabled', false);
	    	devProfile.push($('#devFilter_select'+ $(this).attr('id').substr(9)).val());
	    }else if(devProfile=="") {
	    	$('#devFilter_select'+ $(this).attr('id').substr(15)).attr('disabled', true);
	    	$('#devFilter_select'+ $(this).attr('id').substr(15)).val($('#devFilter_select'+ $(this).attr('id').substr(15)).val());
	    	devProfile=[];
	    }else if(devProfile!="") {
	    	$('#devFilter_select'+ $(this).attr('id').substr(15)).attr('disabled', true);
	    	$('#devFilter_select'+ $(this).attr('id').substr(15)).val(devProfile[0]);
	    	devProfile=[];
	    }
	});
});

var autovalue=[];
$(function() {
	$('[id^=auto]').click(function(event) {
		if(this.checked) {
			autovalue=[];
	    	$('#port_select'+ $(this).attr('id').substr(4)).attr('disabled', false);
	    	autovalue.push($('#port_select'+ $(this).attr('id').substr(4)).val());
	    }else if(autovalue=="") {
	    	$('#port_select'+ $(this).attr('id').substr(4)).attr('disabled', true);
	    	$('#port_select'+ $(this).attr('id').substr(4)).val($('#port_select'+ $(this).attr('id').substr(4)).val());
	    	autovalue=[];
	    }else if(autovalue!="") {
	    	$('#port_select'+ $(this).attr('id').substr(4)).attr('disabled', true);
	    	$('#port_select'+ $(this).attr('id').substr(4)).val(autovalue[0]);
	    	autovalue=[];
	    }
	});
});

var poeScheduleName=[];
$(function() {
	$('[id^=poe_check]').click(function(event) {
		if(this.checked) {
			poeScheduleName=[];
	    	$('#poe_select'+ $(this).attr('id').substr(9)).attr('disabled', false);
	    	poeScheduleName.push($('#poe_select'+ $(this).attr('id').substr(9)).val());
	    }else if(poeScheduleName=="") {
	    	$('#poe_select'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    	$('#poe_select'+ $(this).attr('id').substr(9)).val($('#poe_select'+ $(this).attr('id').substr(9)).val());
	    	poeScheduleName=[];
	    }else if(poeScheduleName!="") {
	    	$('#poe_select'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    	$('#poe_select'+ $(this).attr('id').substr(9)).val(poeScheduleName[0]);
	    	poeScheduleName=[];
	    }
	});
});

var profileName=[];
$(function() {
	$('[id^=mailFilter_check]').click(function(event) {
		if(this.checked) {
			profileName=[];
	    	$('#mailFilter_select'+ $(this).attr('id').substr(16)).attr('disabled', false);
	    	profileName.push($('#mailFilter_select'+ $(this).attr('id').substr(9)).val());
	    }else if(profileName=="") {
	    	$('#mailFilter_select'+ $(this).attr('id').substr(16)).attr('disabled', true);
	    	$('#mailFilter_select'+ $(this).attr('id').substr(16)).val($('#mailFilter_select'+ $(this).attr('id').substr(16)).val());
	    	profileName=[];
	    }else if(profileName!="") {
	    	$('#mailFilter_select'+ $(this).attr('id').substr(16)).attr('disabled', true);
	    	$('#mailFilter_select'+ $(this).attr('id').substr(16)).val(profileName[0]);
	    	profileName=[];
	    }
	});
});

$(function() {
	$( '[id^=tabs_]' ).tabs({
	});
});

$(function() {
	$('#vn_check').click(function(event) {
		if(this.checked) {
			$('#vn_type').attr('disabled', false);
	    }else{
	    	$('#vn_type').attr('disabled', true);
	    }
	});
});

$(function () {
   $("[id^=model_set_]").click(function() {
	   
	   sendData={
				ip : $("#switchid_" + $(this).attr('id').substr(10)).val(), 
				type : $("#ping_type_" + $(this).attr('id').substr(10)).val()
				};
	   if (confirm("If you change type, the device setting will remove.")) {
		   $.ajax({
			   type: "POST",
			   url: "Device?method=changeModel",
		       data: sendData,
		       success: function(data) {
		    	   alert(data);
		    	   window.parent.location.reload();
		       },
	       });
	   }
   });
});

$(function () {
   $("[id^=nms_alias_set_]").click(function() {
	   var aliasSet = [$("#nms_ip" + $(this).attr('id').substr(14)).val(),$("#nms_alias_" + $(this).attr('id').substr(14)).val()];
	   var aliasCount = $(this).attr('id').substr(14);
	   $.ajax({
		   type: "POST",
		   url: "Device?method=nmsAliasUpdate",
	       data:{aliasSet:aliasSet},
	       success: function(data) {
	    	   window.parent.$("#nAliasName" + aliasCount).html($("#nms_alias_" + aliasCount).val());
	    	   alert(data);
	        },
       });
   });
});

var aliasCount = "";
var nAliasCount = "";
$(function() {
    $("[id^=nAliasDiv]").click(function() {
    	aliasCount = "";
    	nAliasCount = $(this).attr('id').substr(9);
    	$("#nAliasText" + $(this).attr('id').substr(9)).show();
    	$("#nAliasDiv" + $(this).attr('id').substr(9)).hide();
    });
});

$(function() {
	$("[id^=nAliasText]").bind("enterKey",function(e){
		var count = $(this).attr('id').substr(10);
		$("#nAliasDiv" + count).show();
		$("#nAliasText" + count).hide();
		
		if($("#nAliasText" + count).val().trim() == ""){
			alert("Alias name is empty!!");
  	    	$("#nAliasText" + count).val($("#nAliasDiv" + count).html());
  	    }else{
			var sendData = {
				ip : $("#nAliasIp_" + count).val(),
				aliasName : $("#nAliasText" + count).val()
			};
			
			$.ajax({
				   type: "POST",
				   url: "Device?method=nmsAliasUpdate",
			       data:sendData,
			       success: function(data) {
			    	   $("#nAliasDiv" + count).html($("#nAliasText" + count).val());
			        },
		    });
  	    }
		
	});
	$("[id^=nAliasText]").keyup(function(e){
		if(e.keyCode == 13)
		{
		  $(this).trigger("enterKey");
		}
	});
});

$(function() {
	$("[id^=nAliasText]").mousedown(function (event) {
		event.stopPropagation();
	});
});

$(function () {
   $("[id^=alias_set_]").click(function() {
	   var aliasSet = [$("#switchid_" + $(this).attr('id').substr(10)).val(),$("#alias_" + $(this).attr('id').substr(10)).val()];
	   var aliasCount = $(this).attr('id').substr(10);
	   $.ajax({
		   type: "POST",
		   url: "Device?method=aliasUpdate",
	       data:{aliasSet:aliasSet},
	       success: function(data) {
	    	   window.parent.$("#vAliasName" + aliasCount).html($("#alias_" + aliasCount).val());
	    	   alert(data);
	        },
       });
   });
});

$(function() {
    $("[id^=aliasDiv]").click(function() {
    	nAliasCount = "";
    	aliasCount = $(this).attr('id').substr(8);
    	$("#aliasText" + $(this).attr('id').substr(8)).show();
    	$("#aliasDiv" + $(this).attr('id').substr(8)).hide();
    });
});

$(function() {
	$("[id^=aliasText]").bind("enterKey",function(e){
		var count = $(this).attr('id').substr(9);
		$("#aliasDiv" + count).show();
		$("#aliasText" + count).hide();
		
		if($("#aliasText" + count).val().trim() == ""){
  	    	alert("Alias name is empty!!");
  	    	$("#aliasText" + count).val($("#aliasDiv" + count).html());
  	    }else{
			var sendData = {
				ip : $("#aliasIp_" + count).val(),
				aliasName : $("#aliasText" + count).val()
			};
			
			$.ajax({
				   type: "POST",
				   url: "Device?method=aliasUpdate",
			       data:sendData,
			       success: function(data) {
			    	   $("#aliasDiv" + count).html($("#aliasText" + count).val());
			        },
		    });
  	    }
		
	});
	$("[id^=aliasText]").keyup(function(e){
		if(e.keyCode == 13)
		{
		  $(this).trigger("enterKey");
		}
	});
});

$(function() {
	$('body').mousedown(function() {
		if(nAliasCount != ""){
			var ncount = nAliasCount;
			$("#nAliasDiv" + ncount).show();
			$("#nAliasText" + ncount).hide();
			
			if($("#nAliasText" + ncount).val().trim() == ""){
				alert("Alias name is empty!!");
	  	    	$("#nAliasText" + ncount).val($("#nAliasDiv" + ncount).html());
	  	    }else{
				var sendData = {
					ip : $("#nAliasIp_" + ncount).val(),
					aliasName : $("#nAliasText" + ncount).val()
				};
				
				$.ajax({
					   type: "POST",
					   url: "Device?method=nmsAliasUpdate",
				       data:sendData,
				       success: function(data) {
				    	   $("#nAliasDiv" + ncount).html($("#nAliasText" + ncount).val());
				        },
			    });
	  	    }
		}
		if(aliasCount != ""){
			var count = aliasCount;
			$("#aliasDiv" + count).show();
			$("#aliasText" + count).hide();
			
			if($("#aliasText" + count).val().trim() == ""){
	  	    	alert("Alias name is empty!!");
	  	    	$("#aliasText" + count).val($("#aliasDiv" + count).html());
	  	    }else{
				var sendData = {
					ip : $("#aliasIp_" + count).val(),
					aliasName : $("#aliasText" + count).val()
				};
				
				$.ajax({
					   type: "POST",
					   url: "Device?method=aliasUpdate",
				       data:sendData,
				       success: function(data) {
				    	   $("#aliasDiv" + count).html($("#aliasText" + count).val());
				        },
			    });
	  	    }
		}
	});
});

$(function() {
	$("[id^=aliasText]").mousedown(function (event) {
		event.stopPropagation();
	});
});

$(function () {
   $("[id^=read_community_set_]").click(function() {
	   var readCommunity = [$("#switchid_" + $(this).attr('id').substr(19)).val(),$("#read_community" + $(this).attr('id').substr(19)).val()];
	   
	   $.ajax({
		   type: "POST",
		   url: "Device?method=readCommunityUpdate",
	       data:{readCommunity:readCommunity},
	       success: function(data) {
	    	   alert("success!");
	        },
       });
   });
});

$(function () {
   $("[id^=write_community_set_]").click(function() {
	   var writeCommunity = [$("#switchid_" + $(this).attr('id').substr(20)).val(),$("#write_community" + $(this).attr('id').substr(20)).val()];
	   
	   $.ajax({
		   type: "POST",
		   url: "Device?method=writeCommunityUpdate",
	       data:{writeCommunity:writeCommunity},
	       success: function(data) {
	    	   alert("success!");
	        },
       });
   });
});

$(function () {
   $("[id^=snmp_timeout_set_]").click(function() {
	   
	   var snmpTimeout = [$("#switchid_" + $(this).attr('id').substr(17)).val(),$("#snmp_timeout" + $(this).attr('id').substr(17)).val()];
	   
	   $.ajax({
		   type: "POST",
		   url: "Device?method=snmpTimeoutUpdate",
	       data:{snmpTimeout:snmpTimeout},
	       success: function(data) {
	    	   alert("success!");
	        },
       });
   });
});

$(function () {
	   $("[id^=snmpUpdate]").click(function() {
		   
		   var sendData = {
				   ip : $("#switchid_" + $(this).attr('id').substr(10)).val(),
				   version :  $("#snmp_version" + $(this).attr('id').substr(10)).val(),
				   snmpTimeout : $("#snmp_timeout" + $(this).attr('id').substr(10)).val(),
				   readCommunity : $("#read_community" + $(this).attr('id').substr(10)).val(),
				   writeCommunity : $("#write_community" + $(this).attr('id').substr(10)).val(),
				   securityName : $("#security_name" + $(this).attr('id').substr(10)).val(),
   				   securityLevel : $("#security_level" + $(this).attr('id').substr(10)).val(),
   				   authProtocol : $("#auth_protocol" + $(this).attr('id').substr(10)).val(),
   				   authPassword : $("#auth_password" + $(this).attr('id').substr(10)).val(),
   				   privProtocol : $("#priv_protocol" + $(this).attr('id').substr(10)).val(),
   				   privPassword : $("#priv_password" + $(this).attr('id').substr(10)).val(),
				   };
		   if((sendData.version == "2" || sendData.version == "1") && (sendData.readCommunity == "" || sendData.writeCommunity == "")){
			   alert("Community is empty.");
		   }
		   else if (sendData.version == "3" && sendData.securityName == ""){
			   alert("Security Name is empty.");
		   }
		   else {
			   $.ajax({
				   type: "POST",
				   url: "Device?method=snmpUpdate",
			       data: sendData,
			       success: function(data) {
			    	   if(data == "Success"){
			    		   alert("Success");
			    	   } else 
			    		   alert("Failed");
			        },
		       });
		   }
	   });
	});

$(function () {
   $("[id^=device_refresh_]").click(function() {
	   var sendData = {ip:$("#switchid_" + $(this).attr('id').substr(15)).val()};
	   
	   $.ajax({
		   type: "POST",
		   url: "Device?method=deviceMacRefresh",
	       data:sendData,
	       success: function(data) {
	    	   alert(data);
	        },
       });
   });
});

$(function () {
	$('#selectallNmsDevice').click(function(event) {  //on click 
        if(this.checked) { // check select status
            $("input[name='deleteNmsDevices']").each(function() { //loop through each checkbox
                this.checked = true;  //select all checkboxes with class "checkbox1"               
            });
        }else{
            $("input[name='deleteNmsDevices']").each(function() { //loop through each checkbox
                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
            });         
        }
    });
});

$(function () {
	$('#selectallDevice').click(function(event) {  //on click 
        if(this.checked) { // check select status
            $("input[name='deleteDevices']").each(function() { //loop through each checkbox
                this.checked = true;  //select all checkboxes with class "checkbox1"               
            });
        }else{
            $("input[name='deleteDevices']").each(function() { //loop through each checkbox
                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
            });         
        }
    });
});

$(function () {
	$("input[name='deleteNmsDevices']").click(function() {
		if(!this.checked) {
			$('input[name=deleteAllNmsDevices]').attr('checked', false);
		}
	});
});

$(function () {
	$("input[name='deleteDevices']").click(function() {
		if(!this.checked) {
			$('input[name=deleteAllDevices]').attr('checked', false);
		}
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

$(document).ready(function(){
	$("[id^=aliasText]").hide();
	$("[id^=nAliasText]").hide();
	
    $('#deviceTable').dataTable({
    	stateSave: true,
    	"aoColumnDefs": [{ type: 'ip-address', targets: 0 },{ 'bSortable': false, 'aTargets': [ 3,5,7,8 ] },{ "sType": "title-string", "aTargets": [ 6 ] }],
    });
    var table = $('#deviceTable').DataTable();
    table.on('page', function( e, o) {
    	$('input[name=deleteAllDevices]').attr('checked', false);
    	$("input[name='deleteDevices']").each(function() { //loop through each checkbox
            this.checked = false; //deselect all checkboxes with class "checkbox1"                       
        });
    });
    $('#nmsTable').dataTable({
    	stateSave: true,
    	"aoColumnDefs": [{ type: 'ip-address', targets: 1 },{ 'bSortable': false, 'aTargets': [ 3,4,6,7 ] },{ "sType": "title-string", "aTargets": [ 5 ] }],
    });
    var table = $('#nmsTable').DataTable();
    table.on('page', function( e, o) {
    	$('input[name=deleteAllNmsDevices]').attr('checked', false);
    	$("input[name='deleteNmsDevices']").each(function() { //loop through each checkbox
            this.checked = false; //deselect all checkboxes with class "checkbox1"                       
        });
    });
});

$(function () {
	$('#importDeviceListBtu').fileupload({

		dataType: 'json',
		add: function (e, data) {

			if (scanPressed) {
				alert("Scan is running.");
				return;
			}

			$.blockUI({
				message: null,
				overlayCSS:  { 
					backgroundColor: '#fff', 
					opacity:         0.0, 
					cursor:          'wait' 
				},
			});

			$.blockUI({
				message: $('#scandiv_display'), 
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

			scanPressed = true;

			jqXHR = null;
			jqXHR = data.submit()
			.success(function (result, textStatus, jqXHR) {
				if(result.isDone == false){
					$.unblockUI();
					alert('Device List Import:\n' + result.text);
				}
			})
			.error(function (jqXHR, textStatus, errorThrown) {
				$.unblockUI();
				alert('Device List Import failed.');
			});

	    	setTimeout(function(){ scan_status(); }, 1000);
	    	scanIsRunning = true;
		}
	});
});
