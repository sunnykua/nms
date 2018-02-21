package com.via.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.via.model.JACAPInfo;
import com.via.model.JSystem;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

/**
 * Servlet implementation class ACOverview
 */
@WebServlet("/APSTASpeed")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1MB
maxFileSize = 1024 * 1024 * 1, // 1MB
maxRequestSize = 1024 * 1024 * 1)
// 1MB
public class APSTASpeed extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public APSTASpeed() {
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

		ArrayList<String[]> ClientDeviceList = new ArrayList<String[]>();

		if (ip != null) {
			ClientDeviceList = JACAPInfo.getClientDeviceList(ip);
		} else if (remote_address != null && remote_device_ip != null) {
			System.out.println("remote_address= " + remote_address);
			System.out.println("remote_device_ip= " + remote_device_ip);

			RemoteInterface Service = RemoteService.creatClient(remote_address, 3);

			if (Service != null) {
				try {
					ClientDeviceList = Service.getClientDeviceList(remote_device_ip);
				} catch (RemoteException e) {
					System.out.println("ClientDeviceList = Service.getClientDeviceList(remote_device_ip);\nRemoteException:" + e.getMessage());
					//e.printStackTrace();
				}
			}
		}

		request.setAttribute("ClientDeviceList", ClientDeviceList);
		request.setAttribute("ClientDeviceList_size", ClientDeviceList.size());

		String method = request.getParameter("method");

		if (method == null) {
			System.out.println("method:null");
		} else if (method.equals("csv_upload")) {
			System.out.println("method:csv_upload");

			String Path = JSystem.projectSpace + "/AC/";

			String tmpPath = JSystem.projectSpace + "/AC/tmp/";

			// Create ini file
			File clientiniFile = new File(Path + ip.replace(".", "") + ".ini");
			clientiniFile.delete();
			clientiniFile.getParentFile().mkdirs();

			Wini clientini = null;
			if (!clientiniFile.exists()) {
				try {
					clientiniFile.createNewFile();
				} catch (IOException e) {
					System.out.println("clientiniFile.createNewFile();\nIOException:" + e.getMessage());
					e.printStackTrace();
				}

				try {
					clientini = new Wini(clientiniFile);
				} catch (InvalidFileFormatException e) {
					System.out.println("clientini = new Wini(clientiniFile);\nInvalidFileFormatException:" + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("clientini = new Wini(clientiniFile);\nIOException:" + e.getMessage());
					e.printStackTrace();
				}
			}

			// creates the save directory if it does not exists
			File tmpPathDir = new File(tmpPath);
			if (!tmpPathDir.exists()) {
				tmpPathDir.mkdirs();
			}
			String fileName = "";
			try {
				for (Part part : request.getParts()) {
					fileName = extractFileName(part);
					part.write(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]);
				}

				InputStreamReader fr = null;
				BufferedReader br = null;
				try {
					fr = new InputStreamReader(new FileInputStream(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]), "UTF-8");
					br = new BufferedReader(fr);
					String rec = null;
					while ((rec = br.readLine()) != null) {
						clientini.put(rec.split(",")[1].toUpperCase(), "client", rec.split(",")[0]);
					}
					clientini.store();
				} catch (IOException e) {
					System.out.println("fr = new InputStreamReader(new FileInputStream(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]), \"UTF-8\");\nIOException:" + e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						if (fr != null)
							fr.close();
						if (br != null)
							br.close();
					} catch (IOException e) {
						System.out.println("if (fr != null)\nIOException:" + e.getMessage());
						e.printStackTrace();
					}
				}

				File filetmpPathDir = new File(tmpPath + File.separator + fileName.split(Pattern.quote(File.separator))[fileName.split(Pattern.quote(File.separator)).length - 1]);
				File renamefilePathDir = new File(Path + ip.replace(".", "") + ".csv");

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

				request.setAttribute("returnpage", "./wlan_client_device_list.jsp?ip=" + ip);
				request.setAttribute("message", "Upload has been done successfully!");
				getServletContext().getRequestDispatcher("/upload_message.jsp").forward(request, response);
			} catch (Exception e) {
				System.out.println("for (Part part : request.getParts()) {\nException:" + e.getMessage());
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				request.setAttribute("returnpage", "./wlan_client_device_list.jsp?ip=" + ip);
				request.setAttribute("message", "Error!");
				try {
					getServletContext().getRequestDispatcher("/upload_message.jsp").forward(request, response);
				} catch (IOException e1) {
					System.out.println("getServletContext().getRequestDispatcher(\"/upload_message.jsp\").forward(request, response);\nIOException:" + e1.getMessage());
					//e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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