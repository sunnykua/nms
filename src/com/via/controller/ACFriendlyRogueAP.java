package com.via.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JACAPInfo;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

/**
 * Servlet implementation class ACOverview
 */
@WebServlet("/ACFriendlyRogueAP")
public class ACFriendlyRogueAP extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ACFriendlyRogueAP() {
		super();
		// TODO Auto-generated constructor stub
	}

	// ====================================================================================================

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//udp
		String action = request.getParameter("action");
		System.out.println("action= " + action);

		String ip = request.getParameter("ip");

		String remote_address = request.getParameter("remote_address");
		String remote_device_ip = request.getParameter("remote_device_ip");

		if (action.indexOf("FriendlyAPList") != -1) {
			ArrayList<String[]> FriendlyAPList = new ArrayList<String[]>();

			if (ip != null) {
				FriendlyAPList = JACAPInfo.getFriendlyAPList(ip);
			} else if (remote_address != null && remote_device_ip != null) {
				System.out.println("remote_address= " + remote_address);
				System.out.println("remote_device_ip= " + remote_device_ip);

				RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

				if (Service != null) {
					try {
						FriendlyAPList = Service.getFriendlyAPList(remote_device_ip);
					} catch (RemoteException e) {
						System.out.println("FriendlyAPList = Service.getFriendlyAPList(remote_device_ip);\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}
				}
			}

			request.setAttribute("FriendlyAPList", FriendlyAPList);
			request.setAttribute("FriendlyAPList_size", FriendlyAPList.size());
		} else if (action.indexOf("RogueAPList") != -1) {
			ArrayList<String[]> RogueAPList = new ArrayList<String[]>();

			if (ip != null) {
				RogueAPList = JACAPInfo.getRogueAPList(ip);
			} else if (remote_address != null && remote_device_ip != null) {
				System.out.println("remote_address= " + remote_address);
				System.out.println("remote_device_ip= " + remote_device_ip);

				RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

				if (Service != null) {
					try {
						RogueAPList = Service.getRogueAPList(remote_device_ip);
					} catch (RemoteException e) {
						System.out.println("RogueAPList = Service.getRogueAPList(remote_device_ip);\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}
				}
			}

			request.setAttribute("RogueAPList", RogueAPList);
			request.setAttribute("RogueAPList_size", RogueAPList.size());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		processRequest(request, response);
	}
}
