package com.via.controller;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JStaticVlanList.JStaticVlanListItem;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class StaticVlanList
 */
@WebServlet("/StaticVlanList")
public class StaticVlanList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StaticVlanList() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
//		String ip = "192.168.0.110";
	    Logger logger = Logger.getLogger(StaticVlanList.class);

		if (ip != null) {
			System.out.println("Static VLAN List - ip: " + ip);
	        //logger.debug("Device IP = " + ip + " Static VLAN List Start");   

			List<JStaticVlanListItem> table = network.staticvlanList(ip);
			request.setAttribute("staticVlan", table);
		}

		double endTime = System.currentTimeMillis();
		System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        logger.debug(" Static VLAN List Using Time: " + (endTime - startTime) / 1000 + " sec.");   

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
