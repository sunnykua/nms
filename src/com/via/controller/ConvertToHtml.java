package com.via.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.via.model.JSystem;

/**
 * Servlet implementation class ConvertToHtml
 */
@WebServlet("/ConvertToHtml")
public class ConvertToHtml extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConvertToHtml() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = Calendar.getInstance();
    	String timeSelect = request.getParameter("timeSelect");
    	String url = "", fileName = "";
    	
    	if(timeSelect.equals("daily")){
    		url = "/report.jsp?timeSelect=daily";
    		calendar.add(Calendar.DAY_OF_YEAR, -1);
    		fileName = "/" + sdf.format(calendar.getTime()) + ".html";
    	}else if(timeSelect.equals("weekly")){
    		url = "/report.jsp?timeSelect=weekly";
    		String startDay, endDay;
    		int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
    		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
    		startDay = sdf.format(calendar.getTime());
    		calendar.add(Calendar.DAY_OF_YEAR, 6);
    		endDay = sdf.format(calendar.getTime());
    		fileName = "/" + startDay + "~" + endDay + ".html";
    	}else if(timeSelect.equals("monthly")){
    		url = "/report.jsp?timeSelect=monthly";
    		String startDay, endDay;
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, 1);
			startDay = sdf.format(calendar.getTime());
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
			endDay = sdf.format(calendar.getTime());
    		fileName = "/" + startDay + "~" + endDay + ".html";
    	}
    	
		ServletContext sc = getServletContext();
		
		String projectSpace = JSystem.projectSpace;
		
		String pName = projectSpace + fileName;
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final ServletOutputStream stream = new ServletOutputStream() { 
			public void write(byte[] data, int offset, int length) {
				os.write(data, offset, length);
			}
			public void write(int b) throws IOException {
				os.write(b);
			}
		};
		final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
		HttpServletResponse rep = new HttpServletResponseWrapper(response) {
			public ServletOutputStream getOutputStream() {
				return stream;
			}
			public PrintWriter getWriter() {
				return pw;
			}
		};
		rd.include(request, rep);
		pw.flush();
		FileOutputStream fos = new FileOutputStream(pName);
		os.writeTo(fos);
		fos.close();
		//response.sendRedirect(fileName);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		service(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
