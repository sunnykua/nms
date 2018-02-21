package com.via.controller;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JACAPInfo;
import com.via.model.JACLogTable;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

/**
 * Servlet implementation class ACOverview
 */
@WebServlet("/ACStatisticsData")
public class ACStatisticsData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ACStatisticsData() {
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

		String level = request.getParameter("level");
		String dateText = request.getParameter("date");
		String dateFromText = request.getParameter("datefrom");
		String dateToText = request.getParameter("dateto");

		String logStartTime, logEndTime;

		ArrayList<String> ACLogIndex = new ArrayList<String>();

		JACLogTable logTable = new JACLogTable();

		String ACLog = new String();

		if (level == null || (!level.equals("info") && !level.equals("error"))) {
			//System.out.println("log level is not correct: " + level + ", reset to info.");
			level = "info";
		}

		if (dateText != null && !dateText.isEmpty()) {
			logStartTime = dateText + " 00:00:00.000";
			logEndTime = dateText + " 23:59:59.999";
		} else if (dateFromText != null && !dateFromText.isEmpty() && dateToText != null && !dateToText.isEmpty()) {
			logStartTime = dateFromText + " 00:00:00.000";
			logEndTime = dateToText + " 23:59:59.999";
		} else {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String defaultDateText = sdf.format(calendar.getTime()); // use current date as default
			logStartTime = defaultDateText + " 00:00:00.000";
			logEndTime = defaultDateText + " 23:59:59.999";
		}

		System.out.println("AC Log Select:start:" + logStartTime + ", end:" + logEndTime);
		//JLogTable logTable = network.getLogsByTime(level, logStartTime, logEndTime);

		if (ip != null) {
			ACLogIndex = JACAPInfo.getACLogIndex(ip);

			ACLog = JACAPInfo.getACLog(ip, logStartTime, logEndTime);
		} else if (remote_address != null && remote_device_ip != null) {
			System.out.println("remote_address= " + remote_address);
			System.out.println("remote_device_ip= " + remote_device_ip);

			RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

			if (Service != null) {
				try {
					ACLogIndex = Service.getACLogIndex(remote_device_ip);
				} catch (RemoteException e) {
					System.out.println("ACLogIndex = Service.getACLogIndex(ip);\nRemoteException:" + e.getMessage());
					//e.printStackTrace();
				}

				try {
					ACLog = Service.getACLog(remote_device_ip, logStartTime, logEndTime);
				} catch (RemoteException e) {
					System.out.println("ACLog = Service.getACLog(remote_device_ip, logStartTime, logEndTime);\nRemoteException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (ACLog.split("&&").length > 3 && Integer.parseInt(ACLog.split("&&")[ACLog.split("&&").length - 2]) > 0) {
			for (int i = 1; i < ACLog.split("&&").length - 2; i++) {
				//System.out.println(ACLog.split("&&")[i]);

				try {
					logTable.addLog(ACLog.split("&&")[i].split(";")[0], ACLog.split("&&")[i].split(";")[2], ACLog.split("&&")[i].split(";")[1], ACLog.split("&&")[i].split(";")[3]);
				} catch (Exception e) {
					System.out.println("logTable.addLog(ACLog.split(\"&&\")[i].split(\";\")[0], ACLog.split(\"&&\")[i].split(\";\")[2], ACLog.split(\"&&\")[i].split(\";\")[1], ACLog.split(\"&&\")[i].split(\";\")[3]);\nException:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (ACLogIndex.size() == 2) {
			//System.out.println(ACLogIndex.get(1));
			//System.out.println(ACLogIndex.get(0));

			request.setAttribute("firstLogTime", ACLogIndex.get(1));
			//request.setAttribute("lastLogTime", ACLogIndex.get(0));

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String defaultDateText = sdf.format(calendar.getTime()); // use current date as default
			logEndTime = defaultDateText + " 23:59:59.999";
			request.setAttribute("lastLogTime", logEndTime);
		} else {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String defaultDateText = sdf.format(calendar.getTime()); // use current date as default
			logStartTime = defaultDateText + " 00:00:00.000";
			logEndTime = defaultDateText + " 23:59:59.999";

			request.setAttribute("firstLogTime", logStartTime);
			request.setAttribute("lastLogTime", logEndTime);
		}

		request.setAttribute("logTable", logTable);
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
