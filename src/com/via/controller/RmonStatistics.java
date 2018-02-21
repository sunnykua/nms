package com.via.controller;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JRmonStatistics.JRmonStatisticsItem;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class RmonStatistics
 */
@WebServlet("/RmonStatistics")
public class RmonStatistics extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RmonStatistics() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
	    Logger logger = Logger.getLogger(RmonStatistics.class);

		if (ip != null) {
			System.out.println("rmon statistics - ip: " + ip);
	        logger.debug("Device IP = " + ip + " Rmon Statistics Start");   

			List<JRmonStatisticsItem> table = network.rmonStatistics(ip);
			request.setAttribute("rmonStatistics", table);
		}

		double endTime = System.currentTimeMillis();
		System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        logger.debug(" Rmon Statistics Using Time: " + (endTime - startTime) / 1000 + " sec.");   

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}
}
