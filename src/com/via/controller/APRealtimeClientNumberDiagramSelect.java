package com.via.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JACAPInfo;

/**
 * Servlet implementation class PortStatistics
 */
@WebServlet("/APRealtimeClientNumberDiagramSelect")
public class APRealtimeClientNumberDiagramSelect extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APRealtimeClientNumberDiagramSelect() {
		super();
		// TODO Auto-generated constructor stub
	}

	// ====================================================================================================

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		String output = "0";

		int annum = 0;
		int bgnnum = 0;

		String ip = request.getParameter("ip");
		//System.out.println(ip);
		if (ip != null) {
			ArrayList<String> GlobalInfo = JACAPInfo.getGlobalInfo(ip);
			if (GlobalInfo.size() > 0) {
				annum = Integer.parseInt(GlobalInfo.get(1));
				bgnnum = Integer.parseInt(GlobalInfo.get(2));

				System.out.println("an num: " + annum + ", bgn num: " + bgnnum);
			} else {
				annum = 0;
				bgnnum = 0;

				System.out.println("無Cliet Number資料");
			}
		}

		if (type == null) {
			System.out.println("chart parameter is missing.");
		} else if (type.equals("anbgn")) {
			output = annum + "," + bgnnum;
		} else {
			System.out.println("chart select is not correct.");
		}

		response.getWriter().write(output);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
