package com.via.controller;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JCurrentVlanList.JCurrentVlanListItem;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class CurrentVlanList
 */
@WebServlet("/CurrentVlanList")
public class CurrentVlanList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CurrentVlanList() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
//		String ip = "192.168.0.110";
	    Logger logger = Logger.getLogger(CurrentVlanList.class);

		if (ip != null) {
			System.out.println("Current VLAN List - ip: " + ip);
	        logger.debug("Device IP = " + ip + " Current VLAN List Start");   

			List<JCurrentVlanListItem> table = network.currentvlanList(ip);
			request.setAttribute("currentvlan", table);
		}

		double endTime = System.currentTimeMillis();
		System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        logger.debug(" Current VLAN List Using Time: " + (endTime - startTime) / 1000 + " sec.");   

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
