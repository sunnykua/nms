//update account
var user;

function update_view(user) {
	   this.user = user;
	   update_view_ajax();
	   update_view_allaccount();
	   
	}

function update_view_ajax(){
	var data = {view_user:user};
	   $.ajax({
		   type: "POST",
		   url: "Account?type=view_account",
		   data: data,
		   dataType: "JSON",
	       success: function(data) {
	    	   $("#Name").empty();
	    	   $("#Email").empty();
	    	   $("#PhoneNumber").empty();
	    	   $('#Name').append('<div>'+data[0]+ '<div>');
	    	   $('#Email').append('<div>'+data[1]+ '<div>');
	    	   $('#PhoneNumber').append('<div>'+data[2]+ '<div>');
	    	   //setTimeout(update_view_ajax, 60000);
	    	   document.getElementById("update_name_val").value = data[0];
	    	   document.getElementById("update_email_val").value = data[1];
	    	   document.getElementById("update_phoneNumber_val").value = data[2];
	       }
	   });
}

function update_view_allaccount(){
	   $.ajax({
		   type: "POST",
		   url: "Account?type=view_alluser_account",
		   //data: data,
		   dataType: "JSON",
	       success: function(data) {
	    	   $("#AccountTable").find("tr:gt(0)").remove();
	    	   for(var i=0;i<data.length;i++){
	    		   for(var j=0;j<data[0].length/7;j++)
	    			   if(data[i][j*7].toLowerCase() == user.toLowerCase()){
	    				   $("#AccountTable").append("<tr span class='validRow'><td align='center'><input type='checkbox' name='deleteAccount' value='" + data[i][j*7] +"' disabled></td><td span class ='selectCheckbox'>" + data[i][j*7].toLowerCase() +"</td><td>" + data[i][j*7+1] + "</td><td>" + data[i][j*7+2] + "</td><td>" + data[i][j*7+3] + "</td><td>"+ data[i][j*7+4] + "</td><td>"+ data[i][j*7+5] + "</td><td>"+ data[i][j*7+6] + "</td></tr>"+"\n");
	    			   }else{
	    				   $("#AccountTable").append("<tr span class='validRow'><td align='center'><input type='checkbox' name='deleteAccount' value='" + data[i][j*7] +"'></td><td span class ='selectCheckbox'>" + data[i][j*7].toLowerCase() +"</td><td>" + data[i][j*7+1] + "</td><td>" + data[i][j*7+2] + "</td><td>" + data[i][j*7+3] + "</td><td>"+ data[i][j*7+4] + "</td><td>"+ data[i][j*7+5] + "</td><td>"+ data[i][j*7+6] + "</td></tr>"+"\n");
	    			   }
		       }
	    	   //setTimeout(update_view_allaccount, 60000);
	       }
	   });
}

$(function () {
   $("#deleteBtu").click(function() {
	   var checkdelete = [];
	   var selected = checkdelete;
	   
	   $("input[name='deleteAccount']:checked").each(function() {
	   checkdelete.push($(this).val());
	   });
	   
	   if(selected.length < 1) {
		   alert("Please select at least one.");
	   } else {
		   if (confirm("Are you sure to delete?\n"+selected)) {
	    	   $.ajax({
	    		   type: "POST",
	    		   url: "Account?type=account_delete",
			       data:{checkdelete:checkdelete},
			       success: function(data) {
			    	   $("input[name='deleteAccount']:checked").each(function(){
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
    $('#Update_pwd').click(function() {
  	    if($('#bef_password').val()==""){
  	    	alert("Password not Empty!!");
  	    }else if($('#update_password').val()==""){
  	    	alert("NEW Password not Empty!!");
  	    }else if($('#update_password2').val()==""){
  	    	alert("Please Input Password again!!");
  	    }else if($('#update_password2').val()!=$('#update_password').val()){
  	    	alert("Check two New Password!!");
  	    }else{
        var dwp = hex_sha1(hex_md5($('#update_password').val()));
       
        var bef_dwp = hex_sha1(hex_md5($('#bef_password').val()));
       
        var data = { update_username:  user,
          		     update_password:  dwp,
                     bef_password:  bef_dwp};
          $.ajax({
              url: "Account?type=update_pwd",
              type: "post",
              data: data,
              dataType: "JSON",
              success: function(data) {
              	if(data == "Success"){
              		alert("Update Password Success!!");
              		update_view_allaccount();
              		update_view_ajax();
              		$('#update_pwd').hide();
                	$('#update_view').show();
          		}else if(data == "Pwd Error"){
          			alert("Password Error!!");
          		}else{
          			alert("Update Password Error!!");
              	}    
              }
          });
  	    }
      });
});

$(function() {
    $('#Update_name').click(function() {
  	    if($('#update_name_val').val().trim() == ""){
  	    	alert("Name Empty!!");
  	    }else{
        
        var data = { update_username:  user, 
        		     update_name:  $('#update_name_val').val()};
          $.ajax({
              url: "Account?type=update_name",
              type: "post",
              data: data,
              dataType: "JSON",
              success: function(data) {
              	if(data == "Success"){
              		alert("Update Name Success!!");
              		update_view_allaccount();
              		update_view_ajax();
              		$('#update_name').hide();
                	$('#update_view').show();
          		}else{
          			alert("Update Name Error!!");
              	}    
              }
          });
  	    }
      });
});

$(function() {
    $('#Update_email').click(function() {
  	    if($('#update_email_val').val()==""){
  	    	alert("Email Empty!!");
  	    }else{
        
        var data = { update_username:  user, 
   		             update_email:  $('#update_email_val').val(),};
          $.ajax({
              url: "Account?type=update_email",
              type: "post",
              data: data,
              dataType: "JSON",
              success: function(data) {
              	if(data == "Success"){
              		alert("Update Email Success!!");
              		update_view_allaccount();
              		update_view_ajax();
              		$('#update_email').hide();
                	$('#update_view').show();
          		}else{
          			alert("Update Email Error!!");
              	}    
              }
          });
  	    }
      });
});

$(function() {
    $('#Update_phoneNumber').click(function() {
  	    if($('#update_phoneNumber_val').val()==""){
  	    	alert("Email Empty!!");
  	    }else{
        
        var data = { update_username:  user, 
   		             update_phoneNumber:  $('#update_phoneNumber_val').val(),};
          $.ajax({
              url: "Account?type=update_phoneNumber",
              type: "post",
              data: data,
              dataType: "JSON",
              success: function(data) {
              	if(data == "Success"){
              		alert("Update Phone Number Success!!");
              		update_view_allaccount();
              		update_view_ajax();
              		$('#update_phoneNumber').hide();
                	$('#update_view').show();
          		}else{
          			alert("Update Phone Number Error!!");
              	}    
              }
          });
  	    }
      });
});

//insert new account 
$(function() {
    $('#check_username').click(function() {
    	if($('#username').val()==""){
    		alert("Username not Empty!!");
    	}else{
    	var data = { add_username:  $('#username').val()};
    	
    	$.ajax({
            url: "Account?type=check_username",
            type: "post",
            data: data,
            dataType: "JSON",
            success: function(data) {
            	if(data=="Repeat"){
            		alert("This Username Can't Use!!");
            		$('#username').val("");
        		}else
        			alert("This Username Can Use!!");
            }
            
    	});
    	}
    });
});

$(function() {
    $('#create_account').click(function() {
    	if($('#username').val()==""){
	    	alert("Username is empty.");
	    }else if($('#password').val()==""){
	    	alert("Password is empty.");
	    }else if($('#password2').val()!=$('#password').val()){
	    	alert("Check your password.");
	    }else if($('#name').val()==""){
   	    	alert("Name is empty.");
   	    }else if($('#email').val()==""){
   	    	alert("Email is empty.");
   	    }else if($('#phoneNumber').val()==""){
	    	alert("Phone number is empty.");
   	    }else {
   	    	
        var dwp = hex_sha1(hex_md5($('#password').val()));
        
        var data = { add_name:  $('#name').val(),
           		     add_level:  $('#level').val(),
        		     add_phoneNumber:  $('#phoneNumber').val(),
           		     add_email:  $('#email').val(),
           		     add_username:  $('#username').val(),
                     add_password:  dwp};
            $.ajax({
                url: "Account?type=add_account",
                type: "post",
                data: data,
                dataType: "JSON",
                success: function(data) {
                	if(data=="Repeat"){
                		alert("This Username Can't Use!!");
                		$('#username').val("");
            		}else{
            			alert("Creat new account complete!!");
            			update_view_allaccount();
            			update_view_ajax();
            			$('#account').hide();
            	    	$('#update_view').show();
            		}    
                }
            });
    	    }
        });
});

$(function() {
    $('#editor_pwd').click(function() {
    	$('#update_view').hide();
    	$('#update_pwd').show();
    	$('#bef_password').val("");
    	$('#update_password').val("");
    	$('#update_password2').val("");
    });
});

$(function() {
    $('#editor_name').click(function() {
    	$('#update_view').hide();
    	$('#update_name').show();
    	update_view_ajax();
    });
});

$(function() {
    $('#editor_email').click(function() {
    	$('#update_view').hide();
    	$('#update_email').show();
    	update_view_ajax();
    });
});

$(function() {
    $('#editor_phoneNumber').click(function() {
    	$('#update_view').hide();
    	$('#update_phoneNumber').show();
    	update_view_ajax();
    });
});

$(function() {
    $('#Modify').click(function() {
    	$('#update_view').hide();
    	$('#update_change').show();
    });
});

$(function() {
    $('#Cancel_pwd').click(function() {
    	$('#update_pwd').hide();
    	$('#update_view').show();
    });
});

$(function() {
    $('#Cancel_name').click(function() {
    	$('#update_name').hide();
    	$('#update_view').show();
    });
});

$(function() {
    $('#Cancel_email').click(function() {
    	$('#update_email').hide();
    	$('#update_view').show();
    });
});

$(function() {
    $('#Cancel_phoneNumber').click(function() {
    	$('#update_phoneNumber').hide();
    	$('#update_view').show();
    });
});

$(function() {
    $('#add_account').click(function() {
    	$('#update_view').hide();
    	$('#account').show();
    	$('#username').val("");
    	$('#password').val("");
    	$('#password2').val("");
    	$('#name').val("");
    	$('#email').val("");
    	$('#phoneNumber').val("");
    	$('.admin').attr('selected', 'selected');
    });
});
$(function() {
    $('#Cancel_account').click(function() {
    	$('#account').hide();
    	$('#update_view').show();
    	$('#username').val("");
    	$('#password').val("");
    	$('#password2').val("");
    	$('#name').val("");
    	$('#email').val("");
    	$('#phoneNumber').val("");
    	$('.admin').attr('selected', 'selected');
    });
});