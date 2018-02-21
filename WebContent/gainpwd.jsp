<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Account Recovery</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script src="theme/jQueryUI/jquery-ui.js"></script>
<script>
$(function() {
    $('#continue').click(function() {
    	
    	if($('#email').val()==""){
    		alert("Please input email");
    	}else {
    		var data = {account_recovery:  $('#email').val()};
    		
                $.ajax({
                    url: "Login?type=recovery_pwd",
                    type: "post",
                    data: data,
                    dataType: "JSON",
                    success: function(data) {
                    	if(data=="right email"){
                    		alert("SUCCESS");
                    		location.href="login.jsp";
                		}else if(data=="search failed")
                			alert("Your emai not exit");
                    }
                });
                return false;
    	}
    });
});

$(function() {
	$( "input[name=login], button" )
		.button()
		.click(function( event ) {
			event.preventDefault();
		});
});
</script>
<style type="text/css">
html, body {
	font-size: 100%;
	height: 100%;
	margin: 0;
}
#login_panel {
	border-radius: 10px;
	/* border: 10px solid #4682B4; */
	background: #e6e6fa;
	width: 350px;
	height: 150px;
	margin: auto;
}
h1 {
	text-align: center;
}
.info {
	width: 250px;
	margin: auto;
	margin-top: 50px;
}
.login_box {
	border: 1px solid grey;/* #C0C0C0; */
	border-collapse:collapse;
}
.login_box td {
	padding: 10px 20px 10px;
}
.input_field {
    width: 250px;
    height:30px;
    /* font-size:17pt; */
	/* border: 1px solid black;
	border-radius: 5px;
	padding: 0 5px 0; */
}
.submit_btn {
	width: 100%;
	height: 40px;
	background:#0082c3;
	text-align: center;
	color: white;
}
.input_label
</style>
</head>
<body>
<div style="width: 1024px; margin: auto;">
<br>
<div style="heigh:200px;width:200px;margin:auto;" ><img style="heigh:200px;width:200px;margin:auto;" src="images/vialogo.png"/></div>
<div style="heigh:200px;width:600px;margin:auto;" >
<h1>Cyber Expert</h1>
<div style="font-size: 18px; text-align:center;">To reset your password, enter the email address.</div>
</div>
<div id="login_panel">
		

		<div class="info">
			
				<table>
					<tr><td>
					<p></p>
					</td></tr>
					<tr><td>
						
						<input class="input_field" type="text" name="email" id="email" placeholder="Email Address">
					</td></tr>
					<tr><td>
						<br>
						<button class="submit_btn" id="continue" name="continue" >continue</button>
					</td></tr>
				</table>
			
		</div>
	</div>
</div>
</body>
</html>