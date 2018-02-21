package com.via.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.via.model.JLogTable;
import com.via.model.JNetwork;

/**
 * Servlet implementation class LogViewer
 */
@WebServlet("/LogViewer")
public class LogViewer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogViewer() {
        super();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	String level = request.getParameter("level");
    	String dateText = request.getParameter("date");
    	String dateFromText = request.getParameter("datefrom");
    	String dateToText = request.getParameter("dateto");
    	String logStartTime, logEndTime;

    	if (level == null || (!level.equals("info") && !level.equals("error"))) {
    		//System.out.println("log level is not correct: " + level + ", reset to info.");
    		level = "info";
    	}
    	
    	String[] timeBoundary = network.getLogsTimeBoundary(level);
    	request.setAttribute("firstLogTime", timeBoundary[0]);
    	request.setAttribute("lastLogTime", timeBoundary[1]);
    	
    	if (dateText != null && !dateText.isEmpty()) {
    		logStartTime = dateText + " 00:00:00.000";
    		logEndTime = dateText + " 23:59:59.999";
    	}
    	else if (dateFromText != null && !dateFromText.isEmpty() && dateToText != null && !dateToText.isEmpty()) {
    		logStartTime = dateFromText + " 00:00:00.000";
    		logEndTime = dateToText + " 23:59:59.999";
    	}
    	else {
    		Calendar calendar = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		String defaultDateText = sdf.format(calendar.getTime());		// use current date as default
    		logStartTime = defaultDateText + " 00:00:00.000";
    		logEndTime = defaultDateText + " 23:59:59.999";
    	}
    	
    	System.out.println("logViewer level:" + level + ", start:" + logStartTime + ", end:" + logEndTime);
    	JLogTable logTable = network.getLogsByTime(level, logStartTime, logEndTime);
    	request.setAttribute("logTable", logTable);
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

}
