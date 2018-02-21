<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/md5-min.js"></script>
<script type="text/javascript" src="theme/sha1-min.js"></script>
<link rel="stylesheet" href="theme/jQueryUI/jquery-ui.css">
<script type="text/javascript" src="theme/jQueryUI/jquery-ui.js"></script>
<script>
function remote_login(remote) {
    	
      var sendData = { code : remote };

      $.ajax({
          url: "Login?type=remote_login",
          type: "post",
          data: sendData,
          dataType: "JSON",
          success: function(data) {
        	  if(data=="1"){
          		location.href="default.jsp";
        	  }
          }
      });
    	
}
</script>
</head>
<body>
<c:out value="<script>$(document).ready(remote_login('${param.remote}'));</script>" escapeXml="false"></c:out>
</body>
</html>