$(document).ready(function () {
	//setTimeout(runLogout, 10*60*1000);
	check();
});

$(function() {
	$('body').mousedown(function() {
		checkLogin();
		//setTimeout(runLogout, 10*60*1000);
	});
});

function MouseWheel (e) {
	e = e || window.event;
	//alert(['scrolled ', (( e.wheelDelta <= 0 || e.detail > 0) ? 'down' : 'up')].join(''));
	if(e.wheelDelta <= 0 || e.detail > 0){
		checkLogin();
		//setTimeout(runLogout, 10*60*1000);
	}
}

// hook event listener on window object
if ('onmousewheel' in window) {
	window.onmousewheel = MouseWheel;
} else if ('onmousewheel' in document) {
	document.onmousewheel = MouseWheel;
} else if ('addEventListener' in window) {
	window.addEventListener("mousewheel", MouseWheel, false);
	window.addEventListener("DOMMouseScroll", MouseWheel, false);
}

function checkLogin() {
	$.ajax({
	       type: "POST",
	       url: "CheckLogin",
	       success:function(data){
	    	   if(data == "SignOut"){
		    		//alert("SignOut");
					location.href="logout.jsp";
				}
	       },
	 });
}

function runLogout() {
	$.ajax({
	       type: "POST",
	       url: "Login?type=logout",
	       success:function(data){
	    	   location.href="logout.jsp";
	       },
	 });
}

function check() {
	$.ajax({
	       type: "POST",
	       url: "CheckLogin",
	       //data:checkData,
	       success:function(data){
	    	   	if(data == "SignOut"){
		    		//alert("SignOut");
					location.href="logout.jsp";
				}
	    	   	setTimeout(check, 13*60*1000);
	       },
	 });
}
