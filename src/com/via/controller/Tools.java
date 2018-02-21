package com.via.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JNetwork;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/Tools")
public class Tools extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Tools() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String method = request.getParameter("method");
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");

		List<String> ping_info = network.getping_info();
		String inputLine = null;
		String get_end_array = "no";

		if (method == null) {
			System.out.println("method == null");

			ping_info.clear();
			ping_info.add("ping_stop");
			ping_info.add("print_ok");
		} else if (method.equals("get_ping")) {
			System.out.println("method = get_ping");

			String address = null;
			String count = null;
			String timeout_sec = null;
			int timeout_ms = 1;
			//System.out.println(address);
			//System.out.println(count);
			//System.out.println(timeout_sec);

			if (ping_info.get(0).equals("ping_stop") && ping_info.get(1).equals("print_ok")) {
				//System.out.println(get_end_array);
				//System.out.println("ping_info.get(0).equals(notrun)");

				ping_info.clear();
				ping_info.add("ping_run");
				ping_info.add("print_notok");

				address = request.getParameter("address");
				count = request.getParameter("count");
				timeout_sec = request.getParameter("timeout_sec");
				//System.out.println(address);
				//System.out.println(count);
				//System.out.println(timeout_sec);

				try {
					if (count == "" || Integer.parseInt(count) <= 0) {
						count = "1";
					}
				} catch (Exception e) {
					count = "1";
				}

				if (Integer.parseInt(count) > 20) {
					count = "20";
				}

				try {
					if (timeout_sec == "" || Integer.parseInt(timeout_sec) <= 0) {
						timeout_sec = "1";
					}
				} catch (Exception e) {
					timeout_sec = "1";
				}

				if (Integer.parseInt(timeout_sec) > 60000) {
					timeout_sec = "60";
				}

				timeout_ms = Integer.parseInt(timeout_sec) * 1000;

				try {
					String cmd = "";

					if (System.getProperty("os.name").startsWith("Windows")) {
						// For Windows
						cmd = "ping -n " + count + " -w " + timeout_ms + " " + address;
					} else {
						// For Linux and OSX
						cmd = "ping -c " + count + " -W " + timeout_sec + " " + address;
					}

					//System.out.println(cmd);

					Process myProcess = Runtime.getRuntime().exec(cmd);
					BufferedReader in = new BufferedReader(new InputStreamReader(myProcess.getInputStream(), "UTF-8"));
					while ((inputLine = in.readLine()) != null) {
						//System.out.println(inputLine);
						if (inputLine != "") {
							ping_info.add(inputLine);
						}
					}
					in.close();

					myProcess.waitFor();

					//System.out.println(myProcess.exitValue());
				} catch (Exception e) {
					//e.printStackTrace();
					System.out.println("發生 IOException");
				}

				ping_info.add("end_array");

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

				ping_info.set(0, "ping_stop");
				ping_info.set(1, "print_ok");
			}

			try {
				response.getWriter().write(ping_info.get(0));
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("response.getWriter().write(ping_info.toString()); ==> 發生 IOException");
			}
		} else if (method.equals("ping_status")) {
			//System.out.println("method = ping_status");

			get_end_array = request.getParameter("get_end_array");
			//System.out.println(get_end_array);

			if (get_end_array.equals("get_end_array_ok")) {
				//System.out.println(get_end_array);

				//會導致同時用有問題
				//				ping_info.set(0, "ping_stop");
				//				ping_info.set(1, "print_ok");
			}

			if (ping_info != null) {
				//System.out.println(ping_info);

				try {
					response.getWriter().write(ping_info.toString());
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("response.getWriter().write(ping_info.toString()); ==> 發生 IOException");
				}
			}
		}
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
