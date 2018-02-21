package com.via.controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/LoginFilter")
public class LoginFilter implements Filter {

    /**
     * Default constructor. 
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		//System.out.println("destroy!!!");
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        Cookie[] cookies = req.getCookies();
        
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache,no-store");
        res.setDateHeader("Expires", 0);
        
        String requestURI = req.getRequestURI();
        
      //Check session status at ajax request.  
        if (req.getHeader("x-requested-with") != null && req.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
        	if(session.getAttribute("username") == null){
	        	res.setHeader("sessionstatus", "timeout");
        	}
        }
        
      //1. NO STAY. Close browser and run browser again.
      //2. STAY.    If stay cookie timeout(7 days). Session was still exist.
        if(session.getAttribute("username") != null && cookies.length <=1){
        	session.removeAttribute("username");
        	session.removeAttribute("userLevel");
        	//session.invalidate();
        }
        
      //1. NO STAY. When session timeout, stay cookie was still exist.
      //2. DEPLOY.  When deploy new project, stay cookie still exist.
        if(session.getAttribute("username") == null && null != cookies){
        	for (int i = 0; i < cookies.length; i++) {
                //String name = cookies[i].getName();
                //String value = cookies[i].getValue();
                if(cookies[i].getName().equals("stay")){
                	Cookie cookies1 = new Cookie("stay", "");
        			cookies1.setMaxAge(0);
        		    res.addCookie(cookies1);
                }
              }
        }
        

      //Overview, real-time chart and history chart.
        if(session.getAttribute("username") == null 
        		&& (requestURI.endsWith("overview.jsp")
        		|| requestURI.endsWith("port_statistics.jsp")
        		|| requestURI.endsWith("etherlike_statistics.jsp")
        		|| requestURI.endsWith("rmon_statistics.jsp")
        		|| requestURI.endsWith("fw_overview.jsp")
        		|| requestURI.endsWith("realtime_chart.jsp") 
        		|| requestURI.endsWith("monitorchart_rxtxbps.jsp") 
        		|| requestURI.endsWith("fw_monitorchart_rxtxbps.jsp")   
        		|| requestURI.endsWith("historychart_rxtxoctet_bps.jsp")
        		|| requestURI.endsWith("ap_realtime_flow_diagram_rx.jsp")
        		|| requestURI.endsWith("ap_realtime_flow_diagram_tx.jsp")
        		|| requestURI.endsWith("ap_realtime_flow_diagram_txrx.jsp")
        		|| requestURI.endsWith("ap_client_number_diagram.jsp")
        		|| requestURI.endsWith("ap_history_clients_anandbgn_diagram_txrx.jsp")
        		|| requestURI.endsWith("ap_history_clients_totaloranorbgn_diagram_txrx.jsp")
        		|| requestURI.endsWith("ap_history_flow_diagram_txrx.jsp"))){
        	//session.invalidate();
        	res.sendRedirect("logout.jsp");
            return;
        }
        
        if(session.getAttribute("username") == null 
        		&& !requestURI.endsWith("login.jsp") 
        		&& !requestURI.endsWith("gainpwd.jsp") 
        		&& !requestURI.endsWith("logout.jsp")
        		&& !requestURI.endsWith("login_remote.jsp")
        		&& !requestURI.endsWith("report.jsp")
        		&& !requestURI.endsWith("weekly_report.jsp")
    			&& !requestURI.endsWith("path_status.jsp")
    			&& !requestURI.endsWith("google_maps.jsp")){
        	//no cookie will go to login page
        	//session.invalidate();
        	res.sendRedirect("login.jsp");
            return;
        }
        
        chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		//System.out.println("init LoginFilter");
	}

}
