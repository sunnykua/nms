package com.via.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.*;
import com.via.model.JVlanStatus.JVlanStatusItem;

import org.apache.log4j.Logger;
/**
 * Servlet implementation class VlanStatus
 */
@WebServlet("/VlanStatus")
public class VlanStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VlanStatus() {
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
	    Logger logger = Logger.getLogger(VlanStatus.class);

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
					//System.out.println("vlan status global - ip: " + ip);
			        //logger.debug("Device IP = " + ip + " VLAN Status Global Start");
					List<JVlanStatusItem> vlanStatus_globalItems = network.vlanStatus_global(ip);
					request.setAttribute("globalVlanStatus", vlanStatus_globalItems);
				}
				else {																// specified device and interface
					//System.out.println("vlan status interface - ip: " + ip);
			        //logger.debug("Device IP = " + ip + " VLAN Status Interface Start");   
					List<JVlanStatusItem> vlanStatus_interfaceItems = network.vlanStatus_interface(ip);
					request.setAttribute("interfaceVlanStatus", vlanStatus_interfaceItems);
				}
			}
			else {
				if (mode.equals("global")) {										// all devices and global
					//System.out.println("vlan status global - all");
			        //logger.debug(" VLAN Status Global - All Device");   
					//List<JVlanStatusTable> tables = network.vlanStatus_global();
					//request.setAttribute("globalVlanStatusList", tables);
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

			//String[] pvidStatus = (String[])request.getAttribute("pvidStatus");
			String[] acceptableStatus = request.getParameterValues("acceptableStatus[]");
			String[] ingressStatus = request.getParameterValues("ingressStatus[]");
			String[] gvrpStatus = request.getParameterValues("gvrpStatus[]");

			/*if (!network.setVlanStatus(ip, JOid.dot1qPvid, pvidStatus)) {
				System.out.println("Write PVID failed.");
			}*/
			if (!network.setVlanStatus(ip, JOid.dot1qPortAcceptableFrameType, acceptableStatus)) {
				System.out.println("Write PortAcceptableFrameType failed.");
		        //logger.debug("Device IP = " + ip + " VLAN Status : Write PortAcceptableFrameType failed.");   
				isOk = false;
			}
			else{
				isOk = true;
			}
			if (!network.setVlanStatus(ip, JOid.dot1qPortIngressFiltering, ingressStatus)) {
				System.out.println("Write PortIngressFiltering failed.");
		        //logger.debug("Device IP = " + ip + " VLAN Status : Write PortIngressFiltering failed.");   
				isOk = false;
			}
			else{
				isOk = true;
			}
			if (!network.setVlanStatus(ip, JOid.dot1qPortGvrpStatus, gvrpStatus)) {
				System.out.println("Write PortGvrpStatus failed.");
		        //logger.debug("Device IP = " + ip + " VLAN Status : Write PortGvrpStatus failed.");   
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
		else {
			System.out.println("Action is invalid.");
	        //logger.warn(" Action is invalid. ");
			return;
		}

		//double endTime = System.currentTimeMillis();
		/*System.out.println("Using Time: " + (endTime - startTime) / 1000 + " sec.");
        logger.debug(" VLAN Status Using Time: " + (endTime - startTime) / 1000 + " sec.");*/
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
