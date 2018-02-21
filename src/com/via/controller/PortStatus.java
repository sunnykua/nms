package com.via.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JPortStatus.JPortStatusItem;

/**
 * Servlet implementation class PortStatus
 */
@WebServlet("/PortStatus")
public class PortStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PortStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	// ====================================================================================================

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String ip = request.getParameter("ip");
		String action = request.getParameter("action");
		//System.out.println("port status - ip: " + ip);

		if (action == null) {
			System.out.println("Action is not specified.");
			return;
		}
		if (action.equals("get")) {
			if (ip != null) {														// specified device
				List<JPortStatusItem> table = network.portStatus(ip);
				request.setAttribute("portStatus", table);
			}
		}
		else if (action.equals("set")) {
			boolean isOk = false;
			if (ip == null) {
				System.out.println("IP is not specified.");
		        //logger.debug(" IP is not specified. ");
				return;
			}
			String[] adminStatus = request.getParameterValues("adminStatus[]");
			String[] negoStatus = request.getParameterValues("negoStatus[]");

			if (!network.setPortStatus(ip, JOid.ifAdminStatus, adminStatus)) {
				System.out.println("Write PortAdminStatus failed.");
				isOk = false;
			}
			else{
				isOk = true;
			}
			if (!network.setPortStatus_db(ip, JOid.ifMauAutoNegoAdminStatus, negoStatus)) {
				System.out.println("Write PortAutoNegoAdminStatus failed.");
				isOk = false;
			}
			else{
				isOk = true;
			}
			
			try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
                System.out.println("Response failed when adding device.");
            }
		}

		//System.out.println("Using Time: " + (System.currentTimeMillis() - startTime) / 1000 + " sec.");
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
