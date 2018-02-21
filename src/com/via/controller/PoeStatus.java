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
import com.via.model.JPoeStatus.JPoeStatusItem;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class PoeStatus
 */
@WebServlet("/PoeStatus")
public class PoeStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PoeStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

	// ====================================================================================================
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//double startTime = System.currentTimeMillis();
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String mode = request.getParameter("mode");
		String ip = request.getParameter("ip");
		String action = request.getParameter("action");

	    Logger logger = Logger.getLogger(PoeStatus.class);

		if (action == null) {
			System.out.println("Action is not specified.");
	        //logger.debug("Device IP = " + ip + " Action is not specified. ");
			return;
		}
		if (action.equals("get")) {
			if (mode == null) {
				System.out.println("Mode is not specified.");
		        //logger.debug("Device IP = " + ip + " Mode is not specified. ");
				return;
			}
			else if (!mode.equals("global") && !mode.equals("interface")) {
				System.out.println("Mode is error.");
		        //logger.debug("Device IP = " + ip + " Mode is error. ");
				return;
			}

			if (ip != null) {
				if (mode.equals("global")) {										// specified device and global
					//System.out.println("poe status global - ip: " + ip);
			        //logger.debug("Device IP = " + ip + " POE Status Global Start");
					List<JPoeStatusItem> table = network.poeStatus_global(ip);
					request.setAttribute("globalPoeStatus", table);
				}
				else {																// specified device and interface
					//System.out.println("poe status interface - ip: " + ip);
			        //logger.debug("Device IP = " + ip + " POE Status Interface Start");
					List<JPoeStatusItem> table = network.poeStatus_interface(ip);
					request.setAttribute("interfacePoeStatus", table);
				}
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
			String[] powerPriority = request.getParameterValues("powerPriority[]");
			
			System.out.println("adminStatus= "+Arrays.toString(adminStatus));
			System.out.println("powerPriority= "+Arrays.toString(powerPriority));
			
			if (!network.setPoeStatus(ip, JOid.PoEPortAdminStatus, adminStatus)) {
				System.out.println("Write PoEPortAdminStatus failed.");
				isOk = false;
		        //logger.warn("Device IP = " + ip + " POE Status : Write PoEPortAdminStatus failed.");   
			}
			else{
				isOk = true;
			}
			if (!network.setPoeStatus(ip, JOid.PoEPortPowerPriority, powerPriority)) {
				System.out.println("Write PoEPortPowerPriority failed.");
				isOk = false;
		        //logger.warn("Device IP = " + ip + " POE Status : Write PoEPortPowerPriority failed.");   
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

		double endTime = System.currentTimeMillis();
		//System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        //logger.debug(" POE Status Using Time: " + (endTime - startTime) / 1000 + " sec.");
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, 
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}
}
