$.ajaxSetup({  
    contentType:"application/x-www-form-urlencoded;charset=utf-8",  
    complete:function(XMLHttpRequest,textStatus){
        var sessionstatus=XMLHttpRequest.getResponseHeader("sessionstatus");   
        if(sessionstatus=="timeout"){
            window.location = "login.jsp";  
        }  
    }  
});