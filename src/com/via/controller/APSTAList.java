package com.via.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
@WebServlet("/APSTAList")
public class APSTAList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APSTAList() {
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

		ArrayList<String[]> ClientList = new ArrayList<String[]>();
		ArrayList<String[]> ClientList_TMP = new ArrayList<String[]>();

		if (ip != null) {
			ClientList_TMP = JACAPInfo.getClientList(ip);

			//for Sort
			if (ClientList_TMP.size() > 0) {
				String[][] Dataarray = new String[ClientList_TMP.size()][ClientList_TMP.get(0).length];
				ClientList_TMP.toArray(Dataarray);
				Arrays.sort(Dataarray, new Comparator<String[]>() {
					public int compare(final String[] entry1, final String[] entry2) {
						final String time1 = entry1[11];
						final String time2 = entry2[11];
						return time1.compareTo(time2);
					}
				});

				ClientList = new ArrayList<String[]>();
				for (int s = 0; s < Dataarray.length; s++) {
					ClientList.add(Dataarray[s]);
				}
			}
		} else if (remote_address != null && remote_device_ip != null) {
			System.out.println("remote_address= " + remote_address);
			System.out.println("remote_device_ip= " + remote_device_ip);

			RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

			if (Service != null) {
				try {
					ClientList_TMP = Service.getClientList(remote_device_ip);
				} catch (RemoteException e) {
					System.out.println("ClientList_TMP = Service.getClientList(remote_device_ip);\nRemoteException：" + e.getMessage());
					//e.printStackTrace();
				}

				//for Sort
				if (ClientList_TMP.size() > 0) {
					String[][] Dataarray = new String[ClientList_TMP.size()][ClientList_TMP.get(0).length];
					ClientList_TMP.toArray(Dataarray);
					Arrays.sort(Dataarray, new Comparator<String[]>() {
						public int compare(final String[] entry1, final String[] entry2) {
							final String time1 = entry1[5];
							final String time2 = entry2[5];
							return time1.compareTo(time2);
						}
					});

					for (int s = 0; s < Dataarray.length; s++) {
						ClientList.add(Dataarray[s]);
					}
				}
			}
		}

		request.setAttribute("ClientList", ClientList);
		request.setAttribute("ClientList_size", ClientList.size());
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
