package com.via.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.via.model.JNetwork;
import com.via.model.JSystem;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/License")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1MB
maxFileSize = 1024 * 1024 * 1, // 1MB
maxRequestSize = 1024 * 1024 * 1)
// 1MB
public class License extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public License() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String method = request.getParameter("method");
		JNetwork network = (JNetwork) getServletContext().getAttribute("network");

		List<String> license_info = network.get_license_info();

		if (method == null) {
			System.out.println("method：null");

			request.setAttribute("license", license_info);
		} else if (method.equals("license_upload")) {
			System.out.println("method：license_upload");

			String line = "";
			if (System.getProperty("os.name").startsWith("Windows")) {
			} else {
				//bak license.txt to license.txt.bak
				try {
					String[] cmd_bak = { "/bin/sh", "-c", "cp -p /etc/tomcat7/license.txt /etc/tomcat7/license.txt.bak" };
					Process Process_bak = Runtime.getRuntime().exec(cmd_bak);
					BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_bak.readLine()) != null) {
					}
					BufferedReader_bak.close();
					Process_bak.waitFor();
				} catch (Exception e) {
					System.out.println("bak license.txt to license.txt.bak 發生 IOException");
					//e.printStackTrace();
				}
			}

			String Path = JSystem.projectSpace + File.separator + "tmp";
			String tmpPath = JSystem.projectSpace + File.separator + "tmp" + File.separator + "tmp";

			// creates the save directory if it does not exists
			File fileDir = new File(Path);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			// creates the tmp directory if it does not exists
			File TmpDir = new File(tmpPath);
			if (!TmpDir.exists()) {
				TmpDir.mkdirs();
			}

			try {
				String fileName = "";
				for (Part part : request.getParts()) {
					fileName = extractFileName(part);
					//System.out.println(tmpPath + File.separator + fileName);
					//System.out.println(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]);
					part.write(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]);
				}

				File filetmpPathDir = new File(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]);
				File renamefilePathDir = new File(fileDir + File.separator + "license.txt");

				if (renamefilePathDir.delete()) {
					System.out.println("Delete " + renamefilePathDir + " success.");
				} else {
					System.out.println("Delete " + renamefilePathDir + " fail.");
				}

				if (filetmpPathDir.renameTo(renamefilePathDir)) {
					System.out.println(filetmpPathDir + " move to " + renamefilePathDir + " success.");
				} else {
					System.out.println(filetmpPathDir + " move to " + renamefilePathDir + " fail.");
				}

				if (System.getProperty("os.name").startsWith("Windows")) {
				} else {
					//License_info
					/*
					NMS Serial Number
					Current Device Number
					License File Contents
					License Device Number
					License Check Valid or Invalid
					*/
					String[] cmd_print = { "/bin/sh", "-c", "/bin/sed -n '7p' /etc/tomcat7/get_serial_number_script" };
					Process Process_print = Runtime.getRuntime().exec(cmd_print);
					BufferedReader BufferedReader_print = new BufferedReader(new InputStreamReader(Process_print.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_print.readLine()) != null) {
						System.out.println(line);
					}
					BufferedReader_print.close();
					Process_print.waitFor();

					String nms_serial_number = "";
					String[] cmd_get_serial_number = { "/bin/sh", "-c", "/etc/tomcat7/get_serial_number_script" };
					Process Process_get_serial_number = Runtime.getRuntime().exec(cmd_get_serial_number);
					BufferedReader BufferedReader_get_serial_number = new BufferedReader(new InputStreamReader(Process_get_serial_number.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_get_serial_number.readLine()) != null) {
						Pattern pattern_serial_number = Pattern.compile("Serial Number");
						Matcher matcher_serial_number = pattern_serial_number.matcher(line);
						while (matcher_serial_number.find()) {
							String[] serial_number = line.replaceAll(" ", "").split(":");
							nms_serial_number = serial_number[1];
							System.out.println(nms_serial_number);
						}
					}
					BufferedReader_get_serial_number.close();
					Process_get_serial_number.waitFor();

					String license_contents = "";
					String license_serial_number = "";
					String license_device_num = "";
					String license_hash = "";
					String[] cmd_get_license_file_contents = { "/bin/sh", "-c", "sed -n '1p' /etc/tomcat7/tmp/license.txt" };
					Process Process_get_license_file_contents = Runtime.getRuntime().exec(cmd_get_license_file_contents);
					BufferedReader BufferedReader_get_license_file_contents = new BufferedReader(new InputStreamReader(Process_get_license_file_contents.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_get_license_file_contents.readLine()) != null) {
						System.out.println(line);

						license_contents = line;

						String[] license_file_contents = line.split("_");

						license_serial_number = license_file_contents[0];

						license_device_num = license_file_contents[1];

						license_hash = license_file_contents[2];
					}
					BufferedReader_get_license_file_contents.close();
					Process_get_license_file_contents.waitFor();

					String license_calc_hash = "";
					String[] cmd_calc_license = { "/bin/sh", "-c", "/etc/tomcat7/license " + nms_serial_number + license_device_num };
					Process Process_calc_license = Runtime.getRuntime().exec(cmd_calc_license);
					BufferedReader BufferedReader_calc_license = new BufferedReader(new InputStreamReader(Process_calc_license.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_calc_license.readLine()) != null) {
						System.out.println(line);
						license_calc_hash = line;
					}
					BufferedReader_calc_license.close();
					Process_calc_license.waitFor();

					if (license_calc_hash.equals(license_hash)) {
						try {
							String[] cmd_bak = { "/bin/sh", "-c", "cp -p /etc/tomcat7/tmp/license.txt /etc/tomcat7/license.txt" };
							Process Process_bak = Runtime.getRuntime().exec(cmd_bak);
							BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
							while ((line = BufferedReader_bak.readLine()) != null) {
							}

							license_info.set(2, license_contents);
							license_info.set(3, license_device_num);

							BufferedReader_bak.close();
							Process_bak.waitFor();
						} catch (Exception e) {
							System.out.println("cp tmp to license 發生 IOException");
							//e.printStackTrace();
						}
						request.setAttribute("message", "License file upload has been done successfully!");
					} else {
						request.setAttribute("message", "License file contents is Invalid!");
					}
				}
				request.setAttribute("returnpage", "./settings.jsp");
				getServletContext().getRequestDispatcher("/upload_message.jsp").forward(request, response);
			} catch (Exception e) {
				//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				request.setAttribute("returnpage", "./settings.jsp");
				request.setAttribute("message", "Error!");
				try {
					getServletContext().getRequestDispatcher("/upload_message.jsp").forward(request, response);
				} catch (IOException e1) {
					System.out.println("發生 IOException");
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				//response.sendRedirect(request.getContextPath() + "/license.jsp");
				//request.getRequestDispatcher("/license.jsp").forward(request, response);
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

	/**
	 * Extracts file name from HTTP header content-disposition
	 */
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}
}
