$(function() {
	$('#value_set').click(function() {
		if($('#nms_port').val() == "" /*|| $('#login_num').val() == ""*/){
			alert("The values is empty.");
		}
		/*else if($('#login_num').val() > 20){
			alert("You can't set more than 20.");
		}*/
		else {
			var sendData = {
				chart_timeInterval : $('#chart_timeInterval').val(),
				nms_port : $('#nms_port').val(),
				login_num : $('#login_num').val(),
			};
	
			$.ajax({
				url : "Settings?action=value_set",
				type : "POST",
				data : sendData,
				success : function(data) {
					alert("success!");
				}
			});
		}
	});
});

$(function() {
	$('#log_submit').click(function() {
		if ($('#mailto_input').val() == "" || $('#mailfrom_input').val() == "") {
			alert("Empty is not allowed to 'From' and 'To' fields.");
		}else {
			var data = {
				log_mail_enable : $('#mailenable_input').prop('checked'),
				log_mail_from : $('#mailfrom_input').val(),
				log_mail_to : $('#mailto_input').val(),
				log_mail_cc : $('#mailcc_input').val(),
				log_mail_bcc : $('#mailbcc_input').val(),
				log_mail_subject : $('#mailsubject_input').val(),
				smtp_host : $('#smtp_host').val(),
				//smtp_port : $('#smtp_port').val(),
				smtp_username : $('#smtp_username').val(),
				smtp_password : $('#smtp_password').val()
			};

			$.ajax({
				url : "Settings?action=set_log_settings",
				type : "POST",
				data : data,
				//dataType: "JSON",
				success : function(result) {
					alert(result);
				}
			});
		}
	});
});

$(function() {
	$('#sms_set').click(function() {
		//var sendData = {smsUserName : $('#smsUserName').val(),smsPwd : $('#smsPwd').val(),
				//phoneNumber : $('#smsPhone1').val() + "," + $('#smsPhone2').val() + "," + $('#smsPhone3').val() + "," + $('#smsPhone4').val() + "," + $('#smsPhone5').val()};
		var sendData = { sms_username : $('#smsUserName').val(), sms_password : $('#smsPwd').val(),
				sms_mobile1 : $('#smsPhone1').val(), sms_mobile2 : $('#smsPhone2').val(), sms_mobile3 : $('#smsPhone3').val(), sms_mobile4 : $('#smsPhone4').val(), sms_mobile5 : $('#smsPhone5').val()};

		$.ajax({
			url : "Settings?action=set_sms_settings",
			type : "POST",
			data : sendData,
			success : function(data) {
				alert("success!");
			}
		});
	});
});

$(function() {
	$('#alarm_set').click(function() {
		var caseEmail1= [];
		var countE1=0;
		$("#caseE1").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee1']").is(":checked")){
				countE1++;
				caseEmail1.push($('#checkCaseE1'+countE1).val());
			}
			else{
				countE1++;
				caseEmail1.push(" ");
			}
			
		});
		
        var caseSMS1= [];
		var countS1=0;
		$("#caseS1").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases1']").is(":checked")){
				countS1++;
				caseSMS1.push($('#checkCaseS1'+countS1).val());
			}
			else{
				countS1++;
				caseSMS1.push(" ");
			}
			
		});
		
		var caseEmail2= [];
		var countE2=0;
		$("#caseE2").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee2']").is(":checked")){
				countE2++;
				caseEmail2.push($('#checkCaseE2'+countE2).val());
			}
			else{
				countE2++;
				caseEmail2.push(" ");
			}
			
		});
		
        var caseSMS2= [];
		var countS2=0;
		$("#caseS2").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases2']").is(":checked")){
				countS2++;
				caseSMS2.push($('#checkCaseS2'+countS2).val());
			}
			else{
				countS2++;
				caseSMS2.push(" ");
			}
			
		});
		
		var caseEmail3= [];
		var countE3=0;
		$("#caseE3").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee3']").is(":checked")){
				countE3++;
				caseEmail3.push($('#checkCaseE3'+countE3).val());
			}
			else{
				countE3++;
				caseEmail3.push(" ");
			}
			
		});
		
        var caseSMS3= [];
		var countS3=0;
		$("#caseS3").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases3']").is(":checked")){
				countS3++;
				caseSMS3.push($('#checkCaseS3'+countS3).val());
			}
			else{
				countS3++;
				caseSMS3.push(" ");
			}
			
		});
		
		var caseEmail4= [];
		var countE4=0;
		$("#caseE4").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee4']").is(":checked")){
				countE4++;
				caseEmail4.push($('#checkCaseE4'+countE4).val());
			}
			else{
				countE4++;
				caseEmail4.push(" ");
			}
			
		});
		
        var caseSMS4= [];
		var countS4=0;
		$("#caseS4").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases4']").is(":checked")){
				countS4++;
				caseSMS4.push($('#checkCaseS4'+countS4).val());
			}
			else{
				countS4++;
				caseSMS4.push(" ");
			}
			
		});
		
		var caseEmail5= [];
		var countE5=0;
		$("#caseE5").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee5']").is(":checked")){
				countE5++;
				caseEmail5.push($('#checkCaseE5'+countE5).val());
			}
			else{
				countE5++;
				caseEmail5.push(" ");
			}
			
		});
		
        var caseSMS5= [];
		var countS5=0;
		$("#caseS5").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases5']").is(":checked")){
				countS5++;
				caseSMS5.push($('#checkCaseS5'+countS5).val());
			}
			else{
				countS5++;
				caseSMS5.push(" ");
			}
			
		});
		
		var caseEmail6= [];
		var countE6=0;
		$("#caseE6").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee6']").is(":checked")){
				countE6++;
				caseEmail6.push($('#checkCaseE6'+countE6).val());
			}
			else{
				countE6++;
				caseEmail6.push(" ");
			}
			
		});
		
        var caseSMS6= [];
		var countS6=0;
		$("#caseS6").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases6']").is(":checked")){
				countS6++;
				caseSMS6.push($('#checkCaseS6'+countS6).val());
			}
			else{
				countS6++;
				caseSMS6.push(" ");
			}
			
		});
		
		var caseEmail7= [];
		var countE7=0;
		$("#caseE7").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee7']").is(":checked")){
				countE7++;
				caseEmail7.push($('#checkCaseE7'+countE7).val());
			}
			else{
				countE7++;
				caseEmail7.push(" ");
			}
			
		});
		
        var caseSMS7= [];
		var countS7=0;
		$("#caseS7").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases7']").is(":checked")){
				countS7++;
				caseSMS7.push($('#checkCaseS7'+countS7).val());
			}
			else{
				countS7++;
				caseSMS7.push(" ");
			}
			
		});
		
		var caseEmail8= [];
		var countE8=0;
		$("#caseE8").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee8']").is(":checked")){
				countE8++;
				caseEmail8.push($('#checkCaseE8'+countE8).val());
			}
			else{
				countE8++;
				caseEmail8.push(" ");
			}
			
		});
		
        var caseSMS8= [];
		var countS8=0;
		$("#caseS8").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases8']").is(":checked")){
				countS8++;
				caseSMS8.push($('#checkCaseS8'+countS8).val());
			}
			else{
				countS8++;
				caseSMS8.push(" ");
			}
			
		});
		
		var caseEmail9= [];
		var countE9=0;
		$("#caseE9").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='casee9']").is(":checked")){
				countE9++;
				caseEmail9.push($('#checkCaseE9'+countE9).val());
			}
			else{
				countE9++;
				caseEmail9.push(" ");
			}
			
		});
		
		var caseSMS9= [];
		var countS9=0;
		$("#caseS9").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='cases9']").is(":checked")){
				countS9++;
				caseSMS9.push($('#checkCaseS9'+countS9).val());
			}
			else{
				countS9++;
				caseSMS9.push(" ");
			}
			
		});

		var alarm_setting = [];
		$("#alarmTable").find("tr:not(:has(th))").each(function () {
			if($(this).find("[name='mail_status']").is(":checked")){
				alarm_setting.push("1");
			}
			else{
				alarm_setting.push("0");
			}
			
			if($(this).find("[name='sms_status']").is(":checked")){
				alarm_setting.push("1");
			}
			else{
				alarm_setting.push("0");
			}
		});
		
		$.ajax({
			url : "Settings?action=set_alarm_settings",
			type : "POST",
			data : {
				alarm_setting:alarm_setting,
				caseEmail1:caseEmail1,
				caseSMS1:caseSMS1,
				caseEmail2:caseEmail2,
				caseSMS2:caseSMS2,
				caseEmail3:caseEmail3,
				caseSMS3:caseSMS3,
				caseEmail4:caseEmail4,
				caseSMS4:caseSMS4,
				caseEmail5:caseEmail5,
				caseSMS5:caseSMS5,
				caseEmail6:caseEmail6,
				caseSMS6:caseSMS6,
				caseEmail7:caseEmail7,
				caseSMS7:caseSMS7,
				caseEmail8:caseEmail8,
				caseSMS8:caseSMS8,
				caseEmail9:caseEmail9,
				caseSMS9:caseSMS9,
				
				},
			success : function(data) {
				alert("success!");
			}
		});
	});
});

function reset_eth0() {
	if (document.getElementById("eth0_type_bak").innerHTML == "static") {
		document.getElementById("eth0_type").checked = false;
		document.getElementById("eth0_ip").disabled = false;
		document.getElementById("eth0_netmask").disabled = false;
		document.getElementById("eth0_gateway").disabled = false;
		document.getElementById("eth0_dns1").disabled = false;
		document.getElementById("eth0_dns2").disabled = false;
		document.getElementById("eth0_dns3").disabled = false;
	} else if (document.getElementById("eth0_type_bak").innerHTML == "dhcp") {
		document.getElementById("eth0_type").checked = true;
		document.getElementById("eth0_ip").disabled = true;
		document.getElementById("eth0_netmask").disabled = true;
		document.getElementById("eth0_gateway").disabled = true;
		document.getElementById("eth0_dns1").disabled = true;
		document.getElementById("eth0_dns2").disabled = true;
		document.getElementById("eth0_dns3").disabled = true;
	}

	document.getElementById("eth0_ip").value = document
			.getElementById("eth0_ip_bak").innerHTML;
	document.getElementById("eth0_netmask").value = document
			.getElementById("eth0_netmask_bak").innerHTML;
	document.getElementById("eth0_gateway").value = document
			.getElementById("eth0_gateway_bak").innerHTML;
	document.getElementById("eth0_dns1").value = document
			.getElementById("eth0_dns1_bak").innerHTML;
	document.getElementById("eth0_dns2").value = document
			.getElementById("eth0_dns2_bak").innerHTML;
	document.getElementById("eth0_dns3").value = document
			.getElementById("eth0_dns3_bak").innerHTML;
}

function reset_eth1() {
	if (document.getElementById("eth1_type_bak").innerHTML == "static") {
		document.getElementById("eth1_type").checked = false;
		document.getElementById("eth1_ip").disabled = false;
		document.getElementById("eth1_netmask").disabled = false;
	} else if (document.getElementById("eth1_type_bak").innerHTML == "dhcp") {
		document.getElementById("eth1_type").checked = true;
		document.getElementById("eth1_ip").disabled = true;
		document.getElementById("eth1_netmask").disabled = true;
	}
	document.getElementById("eth1_ip").value = document
			.getElementById("eth1_ip_bak").innerHTML;
	document.getElementById("eth1_netmask").value = document
			.getElementById("eth1_netmask_bak").innerHTML;
}

window.onload = createCode;

var code;
function createCode() {
	code = "";
	var codeLength = 6; //驗證碼的長度
	var checkCode = document.getElementById("checkCode");
	var codeChars = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'); //所有候選組成驗證碼的字符，當然也可以用中文的
	for (var i = 0; i < codeLength; i++) {
		var charNum = Math.floor(Math.random() * 52);
		code += codeChars[charNum];
	}
	if (checkCode) {
		checkCode.className = "code";
		checkCode.innerHTML = code;
	}

	document.getElementById("inputCode").value = "";
}

function set_interfaces() {
	var eth0_type = "";
	if (document.getElementById("eth0_type").checked) {
		eth0_type = "dhcp";
	} else {
		eth0_type = "static";
	}
	var eth0_ip = $("#eth0_ip").val();
	var eth0_netmask = $("#eth0_netmask").val();
	var eth0_gateway = $("#eth0_gateway").val();
	var eth0_dns1 = $("#eth0_dns1").val();
	var eth0_dns2 = $("#eth0_dns2").val();
	var eth0_dns3 = $("#eth0_dns3").val();
	var eth1_type = "";
	if (document.getElementById("eth1_type").checked) {
		eth1_type = "dhcp";
	} else {
		eth1_type = "static";
	}
	var eth1_ip = $("#eth1_ip").val();
	var eth1_netmask = $("#eth1_netmask").val();
	var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;

	list: {
		if (eth0_type == "static" && eth1_type == "static") {
			if (eth0_ip.match(ipformat) && eth0_netmask.match(ipformat)
					&& eth0_gateway.match(ipformat)
					&& eth1_ip.match(ipformat)
					&& eth1_netmask.match(ipformat)) {
				if (eth0_dns1 != "") {
					if (eth0_dns1.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS1 Address!");
						break list;
					}
				}

				if (eth0_dns2 != "") {
					if (eth0_dns2.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS2 Address!");
						break list;
					}
				}

				if (eth0_dns3 != "") {
					if (eth0_dns3.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS3 Address!");
						break list;
					}
				}
			} else {
				alert("You have entered an invalid Eth1's IP Address or Netmask or Gateway or Eth2's IP Address or Netmask!");
				break list;
			}
		} else if (eth0_type == "static" && eth1_type == "dhcp") {
			if (eth0_ip.match(ipformat) && eth0_netmask.match(ipformat)
					&& eth0_gateway.match(ipformat)) {
				if (eth0_dns1 != "") {
					if (eth0_dns1.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS1 Address!");
						break list;
					}
				}

				if (eth0_dns2 != "") {
					if (eth0_dns2.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS2 Address!");
						break list;
					}
				}

				if (eth0_dns3 != "") {
					if (eth0_dns3.match(ipformat)) {
					} else {
						alert("You have entered an invalid Eth1 DNS3 Address!");
						break list;
					}
				}
			} else {
				alert("You have entered an invalid Eth1's IP Address or Netmask or Gateway!");
				break list;
			}
		} else if (eth0_type == "dhcp" && eth1_type == "static") {
			if (eth1_ip.match(ipformat) && eth1_netmask.match(ipformat)) {
			} else {
				alert("You have entered an invalid Eth2's IP Address or Netmask!");
				break list;
			}
		}

		var inputCode = document.getElementById("inputCode").value;
		if (inputCode.length <= 0) {
			alert("Please enter the captcha code！");
		} else if (inputCode.toUpperCase() != code.toUpperCase()) {
			alert("Wrong code entered！");

			createCode();
		} else {
			var info = "";
			if (eth0_type == "static" && eth1_type == "static") {
				info = "Eth1" + "\n" + "IP Address：" + eth0_ip + "\n"
						+ "Netmask：" + eth0_netmask + "\n" + "Gateway："
						+ eth0_gateway + "\n" + "DNS1：" + eth0_dns1 + "\n"
						+ "DNS2：" + eth0_dns2 + "\n" + "DNS3：" + eth0_dns3
						+ "\n\n" + "Eth2" + "\n" + "IP Address：" + eth1_ip
						+ "\n" + "Netmask：" + eth1_netmask + "\n\n"
						+ "Continue?";
			} else if (eth0_type == "static" && eth1_type == "dhcp") {
				info = "Eth1" + "\n" + "IP Address：" + eth0_ip + "\n"
						+ "Netmask：" + eth0_netmask + "\n" + "Gateway："
						+ eth0_gateway + "\n" + "DNS1：" + eth0_dns1 + "\n"
						+ "DNS2：" + eth0_dns2 + "\n" + "DNS3：" + eth0_dns3
						+ "\n\n" + "Eth2" + "\n" + "Type：DHCP Client\n\n"
						+ "Continue?";
			} else if (eth0_type == "dhcp" && eth1_type == "static") {
				info = "Eth1" + "\n" + "Type：DHCP Client\n\n" + "Eth2"
						+ "\n" + "IP Address：" + eth1_ip + "\n"
						+ "Netmask：" + eth1_netmask + "\n\n" + "Continue?";
			} else if (eth0_type == "dhcp" && eth1_type == "dhcp") {
				info = "Eth1" + "\n" + "Type：DHCP Client\n\n" + "Eth2"
						+ "\n" + "Type：DHCP Client\n\n" + "Continue?";
			}

			if (confirm(info)) {
				var senddata = {
					eth0_type : eth0_type,
					eth0_ip : eth0_ip,
					eth0_netmask : eth0_netmask,
					eth0_gateway : eth0_gateway,
					eth0_dns1 : eth0_dns1,
					eth0_dns2 : eth0_dns2,
					eth0_dns3 : eth0_dns3,
					eth1_type : eth1_type,
					eth1_ip : eth1_ip,
					eth1_netmask : eth1_netmask
				};
				$.ajax({
					type : "POST",
					url : "Settings?action=set_interfaces",
					data : senddata
				});

				$.blockUI({
					css : {
						border : 'none',
						padding : '15px',
						backgroundColor : '#000',
						'-webkit-border-radius' : '10px',
						'-moz-border-radius' : '10px',
						opacity : .5,
						color : '#fff'
					}
				});

				function delay10sec() {
					javascript: window.location.reload();
				}
				setTimeout(delay10sec, 10000);
			} else {
				alert("Cancelled.");
			}
		}
	}

	//alert("list end");
}

function eth0_type(obj) {
	if (obj.checked) {
		document.getElementById("eth0_ip").disabled = true;
		document.getElementById("eth0_netmask").disabled = true;
		document.getElementById("eth0_gateway").disabled = true;
		document.getElementById("eth0_dns1").disabled = true;
		document.getElementById("eth0_dns2").disabled = true;
		document.getElementById("eth0_dns3").disabled = true;
	} else {
		document.getElementById("eth0_ip").disabled = false;
		document.getElementById("eth0_netmask").disabled = false;
		document.getElementById("eth0_gateway").disabled = false;
		document.getElementById("eth0_dns1").disabled = false;
		document.getElementById("eth0_dns2").disabled = false;
		document.getElementById("eth0_dns3").disabled = false;
	}
}

function eth1_type(obj) {
	if (obj.checked) {
		document.getElementById("eth1_ip").disabled = true;
		document.getElementById("eth1_netmask").disabled = true;
		document.getElementById("eth1_gateway").disabled = true;
	} else {
		document.getElementById("eth1_ip").disabled = false;
		document.getElementById("eth1_netmask").disabled = false;
	}
}

$(function() {
	$('[id^=poe_set]').click(function() {
		var manual = [];
		if($('#poeManual' + $(this).attr('id').substr(7)).is(":checked")){
			manual.push("1");
		}else{
			manual.push("0");
		}
		var sendData = {
			poeManual : manual[0],
			switchid : $('#switchid_' + $(this).attr('id').substr(7)).val(),
			poeStartTime : $('#poeStartTime' + $(this).attr('id').substr(7)).val(),
			poeStartStatus : $('#poeStartStatus' + $(this).attr('id').substr(7)).val(),
			poeEndTime : $('#poeEndTime' + $(this).attr('id').substr(7)).val(),
			poeEndStatus : $('#poeEndStatus' + $(this).attr('id').substr(7)).val()
		};

		$.ajax({
			url : "Settings?action=poe_set",
			type : "POST",
			data : sendData,
			success : function(data) {
				alert("success!");
			}
		});
	});
});

$(function() {
	$('[id^=poeManual]').click(function(event) {
		if(this.checked) {
			autovalue=[];
	    	$('#poeStartTime'+ $(this).attr('id').substr(9)).attr('disabled', false);
	    	$('#poeStartStatus'+ $(this).attr('id').substr(9)).attr('disabled', false);
	    	$('#poeEndTime'+ $(this).attr('id').substr(9)).attr('disabled', false);
	    	$('#poeEndStatus'+ $(this).attr('id').substr(9)).attr('disabled', false);
	    }else {
	    	$('#poeStartTime'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    	$('#poeStartStatus'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    	$('#poeEndTime'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    	$('#poeEndStatus'+ $(this).attr('id').substr(9)).attr('disabled', true);
	    }
	});
});

$(function() {
	$('#poe_apply').click(function() {
		
		if($('#schedule_name').val() == ""){
			alert("The schedule name is empty.");
		}
		else {
			var sendData = {
				schedule_name : $('#schedule_name').val(),
				poe_StartTime : $('#poe_StartHour').val() + $('#poe_StartMinutes').val(),
				poe_EndTime : $('#poe_EndHour').val() + $('#poe_EndMinutes').val(),	
			};
			
			$.ajax({
				url : "Settings?action=poe_add",
				type : "POST",
				data : sendData,
				success : function(data) {
					if(data=="Repeat"){
						$('#schedule_name').val("");
	            		alert("Data is invalid.");
	        		}else
	        			poe_view();
				}
			});
		}
	});
});

$(document).ready(function(){
	poe_view();
});

function poe_view(){
	   $.ajax({
		   type: "POST",
		   url: "Settings?action=poe_view",
		   //data: data,
		   dataType: "JSON",
		   success: function(data) {
	    	   $("#poeScheduleTable").find("tr:gt(0)").remove();
	    	   for(var i=0;i<data.length;i++){
	    		   for(var j=0;j<data[0].length/3;j++)
			   $("#poeScheduleTable").append("<tr span class='validRow'><td align='center'><input type='checkbox' name='deletePoe' value='" + data[i][j*3] +"'></td><td align='center'>" + data[i][j*3] +"</td><td align='center'>" + data[i][j*3+1] + "</td><td align='center'>" + data[i][j*3+2] + "</td></tr>"+"\n");
		       }
	    	   //setTimeout(update_view_allaccount, 60000);
	       }
	   });
}

$(function () {
   $("#deleteBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deletePoe']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one.");
	   } else {
		   if (confirm("Are you sure to delete?\n"+selected)) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Settings?action=poe_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $("input[name='deletePoe']:checked").each(function(){
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
	$('#profile_apply').click(function() {
		
		if($('#profile_name').val() == ""){
			alert("The profile name is empty.");
		}
		else {
			var sendData = {
				profile_name : $('#profile_name').val(),
				profile_StartTime : $('#profile_StartHour').val() + $('#profile_StartMinutes').val(),
				profile_EndTime : $('#profile_EndHour').val() + $('#profile_EndMinutes').val(),	
			};
			
			$.ajax({
				url : "Settings?action=profile_add",
				type : "POST",
				data : sendData,
				success : function(data) {
					if(data=="Repeat"){
						$('#profile_name').val("");
	            		alert("Data is invalid.");
	        		}else
	        			profile_view();
				}
			});
		}
	});
});

$(document).ready(function(){
	profile_view();
});

function profile_view(){
	   $.ajax({
		   type: "POST",
		   url: "Settings?action=profile_view",
		   //data: data,
		   dataType: "JSON",
		   success: function(data) {
	    	   $("#profileTable").find("tr:gt(0)").remove();
	    	   for(var i=0;i<data.length;i++){
	    		   for(var j=0;j<data[0].length/3;j++)
			   $("#profileTable").append("<tr span class='validRow'><td align='center'><input type='checkbox' name='deleteProfile' value='" + data[i][j*3] +"'></td><td align='center'>" + data[i][j*3] +"</td><td align='center'>" + data[i][j*3+1] + "</td><td align='center'>" + data[i][j*3+2] + "</td></tr>"+"\n");
		       }
	    	   //setTimeout(update_view_allaccount, 60000);
	       }
	   });
}

$(function () {
   $("#profileDeleteBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deleteProfile']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one.");
	   } else {
		   if (confirm("Are you sure to delete?\n"+selected)) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Settings?action=profile_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $("input[name='deleteProfile']:checked").each(function(){
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

function checkFileSize(inputFile) {
	var max = 1 * 1024 * 1024; // 10MB

	var filename = inputFile.files[0].name;

	if (inputFile.files && inputFile.files[0].size > max) {
		alert("File " + filename + " too large."); // Do your thing to handle the error.
		inputFile.value = null; // Clear the field.
	}
}

function location_address_set() {
	var address = $("#address").val();

	var senddata = {
			address : address
	};	$.ajax({
		type : "POST",
		url : "Settings?action=location_address_set",
		data : senddata,
		success : function(data) {
			if(data=="ok"){
				alert("Location Address Setting success.");
			}
		}
	});
}

function remote_service_set() {
	var remote_service_switch = document.getElementById("remote_service_switch").checked;
	var remote_service_address = $("#remote_service_address").val();
	/*var remote_service_registry_port = $("#remote_service_registry_port").val();
	var remote_service_data_port = $("#remote_service_data_port").val();*/
	var remote_server_address = $("#remote_server_address").val();
	/*var remote_server_port = $("#remote_server_port").val();*/

	var senddata = {
			remote_service_switch : remote_service_switch,
			remote_service_address : remote_service_address,
			/*remote_service_registry_port : remote_service_registry_port,
			remote_service_data_port : remote_service_data_port,*/
			remote_server_address : remote_server_address/*,
			remote_server_port : remote_server_port*/
	};	$.ajax({
		type : "POST",
		url : "Settings?action=remote_service_set",
		data : senddata,
		success : function(data) {
			if(data=="true"){
				alert("Set Up please wait.");
			}else{
				alert(data+"\n\nRemote Service Setting success.");
			}
		}
	});
}

$(function () {
	$('#settingsExportBtn').click(function() {
		$.ajax({
			url : "Settings?action=settings_export",
			type : "POST",
			dataType: "JSON",
			success : function(data) {
				if (data.isDone) {
					$('<a href=\"' + data.file + '\" download></a>')[0].click();
				}
				else {
					alert("Failed to export settings.");
				}
			}
		});
		
	});
});

$(function () {
	$('#settingsUpload').fileupload({
		dataType: 'json',
		add: function (e, data) {
			jqXHR = null;
			jqXHR = data.submit()
	        	.success(function (result, textStatus, jqXHR) {
	        	    alert('Settings Import:\n' + result.text);
	        	})
	        	.error(function (jqXHR, textStatus, errorThrown) {
	        	    if (errorThrown === 'abort') {
	        	        alert('Settings Import:\nFile uploading has been canceled.');
	        	    }
	        	    else {
	        	        /* this block will be run if there is exception in servlet */
	        	        alert('File uploading failed.');
	        	    }
	        	});
		}
	});
});

$(function () {
	$('#fileupload').fileupload({
        dataType: 'json',
        add: function (e, data) {
            jqXHR = null;
            data.context = $('<button/>').text('Cancel')
            	.addClass('btn btn-primary btn-sm')
                .insertAfter('#progress')
                .click(function () {
                    jqXHR.abort();
                });
            jqXHR = data.submit()
            	.success(function (result, textStatus, jqXHR) {
            	    if (!result.files[0].done) {				/* reset the progress bar only when error is detected in servlet */
            	        $('#progress #bar').css('width', '0%');
            	        $('#progress #percent').text('0%');
            	    }
            	    data.context.remove();
            	    alert('WEB UPDATE:\n' + result.files[0].text);
            	    /* 'success' only means the process in servlet is finished without any exception, the actual result will put in response object */
            	})
            	.error(function (jqXHR, textStatus, errorThrown) {
            	    $('#progress #bar').css('width', '0%');		/* this statement can't be put in cancel click, why? */
            	    $('#progress #percent').text('0%');
            	    data.context.remove();
            	    if (errorThrown === 'abort') {
            	        alert('WEB UPDATE:\nWeb package uploading has been canceled.');
            	    }
            	    else {
            	        /* this block will be run if there is exception in servlet */
            	        alert('Web package uploading failed.');
            	    }
            	});
        },
        progress: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress #bar').css('width', progress + '%');
            $('#progress #percent').text(progress + '%');
        }
    });
});
