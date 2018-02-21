<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Untitled</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/Theme1/check.js"></script>
<script type="text/javascript" src="theme/Theme1/ajaxCheck.js"></script>
</head>
<script>
function single_ip(ip){
	var senddata ={ip:ip};
	   $.ajax({
		   type: "POST",
		   url: "Device?method=single_ip_set",
		   data: senddata,
		   success: function(response) {
			   var data = response.split(",");
			   
			   /* if(data[2] == "localhost"){
	        	   location.href="http://"+ ip;
	           }else if(data[0] == "Extranet"){
	        	   location.href="http://"+ data[1];
	           }else if(response =="LAN"){
	        	   location.href="http://"+ ip;
	           } */
			   
			   if(data[3] == "localhost"){
				   location.href="http://"+ ip;
				   if(data[1] == "y"){
					   window.open("http://"+ ip);
				   }
				   else {
					   location.href="http://"+ ip;
				   }
	           }else if(data[0] == "Extranet"){
	        	   if(data[1] == "y"){
	        		   window.open("http://"+ data[2]);
				   }
				   else {
					   location.href="http://"+ data[2];
				   }
	           }else if(data[0] =="LAN"){
	        	   if(data[1] == "y"){
					   window.open("http://"+ ip);
				   }
				   else {
					   location.href="http://"+ ip;
				   }
	           }
	       },
	   });
}


</script>
<body>
    <%-- <jsp:include page="/Device">
        <jsp:param value="set_single_ip" name="method"/>
        <jsp:param value="${param.ip}" name="ip"/>
    </jsp:include>
    <c:redirect url="http://59.120.5.185:8081">
    </c:redirect> --%>
<c:out value="<script>$(document).ready(single_ip('${param.ip}'));</script>" escapeXml="false"></c:out>
</body>
</html>