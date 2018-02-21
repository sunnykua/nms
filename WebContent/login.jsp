<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/md5-min.js"></script>
<script type="text/javascript" src="theme/sha1-min.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script>
var uri="dashboard.jsp";

function load(uri) {
	this.uri = uri;
	checkIE();
}

function checkIE(){
	if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/7./i)=="7.") { 
		//alert("IE 7.0"); 
		} 
		else if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/8./i)=="8.") { 
		//alert("IE 8.0"); 
		} 
		else if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/9./i)=="9.") { 
			$("#Username_label").attr('class', 'stacked_label'); 
			$("#Password_label").attr('class', 'stacked_label'); 
			$("#login_panel").attr('class', 'login_panel_IE'); 
		} 
		else if(navigator.appName == "Microsoft Internet Explorer") { 
		//alert("IE 6.0"); 
		} 
};

function login() {
    	//alert(uri);
    	if($('#username').val()==""){
    		alert("Username not Empty!!");
    	}else if($('#password').val()==""){
    		alert("Password not Empty!!");
    	}else {
    		var chk=[];
    		if($('input[type="checkbox"]').is(":checked")){
    			chk.push("Y");
    		}else{
    			chk.push("N");
    		}
    		
    	        var dwp = hex_sha1(hex_md5($('#password').val()));
    	        
                var data = {username:  $('#username').val(),
                             password:  dwp,
                             ifchecked: chk[0]};
    		
                $.ajax({
                    url: "Login?type=login",
                    type: "post",
                    data: data,
                    dataType: "JSON",
                    success: function(data) {
                    	if(data=="1"){
                    		location.href=uri;
                		}else if(data=="2"){
                    		location.href=uri;
                		}else if(data=="Eusr"){
                			alert("Account isn't exist.");
                		}else if(data=="Epwd"){
                			alert("Password is error.");
                		}else if(data=="Full"){
                			alert("The online user is reach limited.");
                		}else
                			location.href=uri;
                    	
                    }
                });
                return false;
    	}
}

$(function() {
	$( "input[name=login], button" )
		.button()
		.click(function( event ) {
			event.preventDefault();
		});
});

function handleKeyPress(e){
	 var key=e.keyCode || e.which;
	  if (key==13){
	     login();
	  }
	}
</script>
<style type="text/css">
html, body {
	font-size: 100%;
	height: 100%;
	margin: 0;
}
.login_panel {
	/* border-radius: 10px;
	border: 10px solid #4682B4; */
	background: #e6e6fa;
	width: 350px;
	height: 200px;
	margin: auto;
	border-radius: 10px;
}

.login_panel_IE {
	border-radius: 10px;
	/* border: 10px solid #4682B4; */
	background: #e6e6fa;
	width: 350px;
	height: 280px;
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
    font-size:13pt;
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
.hidden_label {
	position: absolute !important;
	clip: rect(1px 1px 1px 1px); /* IE6, IE7 */
	clip: rect(1px, 1px, 1px, 1px);
	height: 0px;
	width: 0px;
	overflow: hidden;
	visibility: hidden;
}
.stacked_label {
	display: block;
	font-weight: bold;
	margin: .5em 0;
}

</style>

</head>
<c:if test="${!empty sessionScope.username}">
	<c:redirect url="dashboard.jsp"></c:redirect>
</c:if>
<c:if test="${!empty cookie.stay}">
	<c:redirect url="dashboard.jsp"></c:redirect>
</c:if>
<body>
<br>
<div style="width: 1024px; margin: auto;">
<div style="heigh:200px;width:200px;margin:auto;" ><img style="heigh:200px;width:200px;margin:auto;" src="images/vialogo.png"/></div>
<div style="heigh:200px;width:600px;margin:auto;" >
<h1>Cyber Expert</h1>
<div style="font-size: 18px; text-align:center;">Please use your account to login.</div>
</div>
	<div id="login_panel" class="login_panel">
		

		<div class="info">
			
				<table><!-- login_box no use -->
					<tr><td>
					<p></p>
					</td></tr>
					<tr><td>
						<label id="Username_label" class="hidden_label" >Username</label>
						<input class="input_field" type="text" name="username" id="username" placeholder="Username">
					</td></tr>
					<tr><td>
						<label id="Password_label" class="hidden_label" >Password</label>
						<input class="input_field" type="password" name="password" id="password" placeholder="Password" onkeypress="handleKeyPress(event)">
					</td></tr>
					<tr><td>
						<br>
						<button class="submit_btn" id="login" name="login" onclick="login();">sign in</button>
					</td></tr>
					<tr><td>
					<!-- <input type="checkbox" id="login_memory">Stay signed in --><!-- <div style="float:right;"> --><a href="gainpwd.jsp">Forget password</a><!-- </div> -->
					</td></tr>
				</table>
			<%-- <c:out value="<script>$(document).ready(load('${sessionScope.URI}'));</script>" escapeXml="false"></c:out> --%>
		</div>
	</div>
</div>
</body>
</html>