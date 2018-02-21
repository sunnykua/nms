package com.via.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JPortStatistics.JPortStatisticsItem;

import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

/**
 * Servlet implementation class PortStatistics
 */
@WebServlet("/PortStatistics")
public class PortStatistics extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PortStatistics() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
		String method = request.getParameter("method");
		
//	    PropertyConfigurator.configure("D:/eclipse_english_SR2/workspace/NetworkManagement/lib/Log4j.properties");
	    Logger logger = Logger.getLogger(PortStatistics.class);
    	
		if (method != null && method.equals("load1")) {
    		System.out.println("method=load1");
    		
	        logger.debug("Device IP = " + ip + " method=load1 ");   
    		
    		//String responseText = new String();
    		for (JDevice device : network.getDeviceList()) {
    			String Descr = device.getSysDescr();
    			String PublicIp = device.getPublicIp();
    			//responseText +=descr + "," + ipAddr;
    			response.getWriter().write(Descr+ ","+PublicIp+ ",");
    		}
    		return;
    	}

		if (ip != null) {
			System.out.println("port statistics - ip: " + ip);
	        logger.debug("Device IP = " + ip + " Port Statistics Start");   
//	        logger.error("Device IP = " + ip + " Port Statistics Error");   
//	        logger.fatal("Device IP = " + ip + " Port Statistics Fatal");   

			List<JPortStatisticsItem> table = network.portStatistics(ip);
			request.setAttribute("portStatistics", table);
		}

		double endTime = System.currentTimeMillis();
		System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        logger.debug(" Port Statistics Using Time: " + (endTime - startTime) / 1000 + " sec.");   

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
