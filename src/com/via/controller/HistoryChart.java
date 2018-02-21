package com.via.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.via.model.*;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/HistoryChart")
public class HistoryChart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HistoryChart() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Calendar calendar = Calendar.getInstance();
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	int pointNumber = Integer.MAX_VALUE;
    	String timeSelect = request.getParameter("time_select");
    	String chartType = request.getParameter("chart_type");
    	String ipAddr = request.getParameter("ip");
		String ifIndex = request.getParameter("ifid");
    	JDevice device = network.findDeviceByIp(ipAddr);
		if (device.getSysObjectId().indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {
			ifIndex = "1";
		}
    	String startdate = request.getParameter("startdate");
		String enddate = request.getParameter("enddate");
		String dateText = request.getParameter("dateText");
		String action = request.getParameter("action");

		if (action != null && action.equals("calendar")) {
		    String[] boundaryTimes = network.getHistoryBoundaryTime(ipAddr, ifIndex);
		    
		    int y1 = 0, m1 = 0, d1 = 0, y2 = 0, m2 = 0, d2 = 0;
		    boolean parseFailed = true;
		    if (boundaryTimes != null) {
		        try {
                    calendar.setTime(sdf.parse(boundaryTimes[0]));          // first record
                    y1 = calendar.get(Calendar.YEAR);
                    m1 = calendar.get(Calendar.MONTH) + 1;
                    d1 = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.setTime(sdf.parse(boundaryTimes[1]));          // last record
                    y2 = calendar.get(Calendar.YEAR);
                    m2 = calendar.get(Calendar.MONTH) + 1;
                    d2 = calendar.get(Calendar.DAY_OF_MONTH);
                    parseFailed = false;
                }
                catch (ParseException e) {
                    System.out.println("parse history boundary time failed.");
                }
		    }
		    if (parseFailed) {
		        y1 = y2 = calendar.get(Calendar.YEAR);
		        m1 = m2 = calendar.get(Calendar.MONTH) + 1;
		        d1 = d2 = calendar.get(Calendar.DAY_OF_MONTH);
		    }
		    
		    System.out.println(String.format("=====ip: %s, if: %s, calendar y1=%d m1=%d d1=%d y2=%d m2=%d d2=%d", ipAddr, ifIndex, y1, m1, d1, y2, m2, d2));
		    
		    int[] timeDigits = new int[]{y1, m1, d1, y2, m2, d2};
		    
		    String json_data = new Gson().toJson(timeDigits);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().write(json_data);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
		}
		else {
		    System.out.println(String.format("=====ip: %s, if: %s, startdate: %s, enddate: %s, dateText: %s, timeSelect: %s, chart_type: %s",
		            ipAddr, ifIndex, startdate, enddate, dateText, timeSelect, chartType));
		    Map<String, float[]> dataSet = new LinkedHashMap<String, float[]>();
	        String startTime, endTime;

		    if (timeSelect != null && (timeSelect.equals("day") || timeSelect.equals("week") || timeSelect.equals("month"))) {    // TODO: page should not give out of this three ones
		        // if timeSelect is specified, means the endTime is now and then calculates the start time
		        endTime = sdf.format(calendar.getTime());

		        if (timeSelect.equals("day")) {
		            calendar.add(Calendar.DAY_OF_YEAR, -1);
		            startTime = sdf.format(calendar.getTime());
		        }
		        else if (timeSelect.equals("week")) {
		            calendar.add(Calendar.DAY_OF_YEAR, -7);
		            startTime = sdf.format(calendar.getTime());
		        }
		        else if (timeSelect.equals("month")) {
		            calendar.add(Calendar.MONTH, -1);
		            startTime = sdf.format(calendar.getTime());
		        }
		        else {
		            startTime = null;       // it will use the far past time although it should not happen
		        }
		    }
		    else {
		        startTime = dateText != null && !dateText.isEmpty() ? dateText + " 00:00:00.000" : startdate != null && !startdate.isEmpty() ? startdate + " 00:00:00.000" : null;
		        endTime = dateText != null && !dateText.isEmpty() ? dateText + " 23:59:59.999" : enddate != null && !enddate.isEmpty() ? enddate + " 23:59:59.999" : null;
		    }
		    //System.out.println("=====startTime: " + startTime + ", endTime: " + endTime);

		    String[][] chartData = null;
		    if (chartType != null) {
		        // Rx Tx Octets bps
		        if (chartType.equals("rx_tx_octet_bps_history")) {
		            chartData = network.getRxTxOctetRateHistory(ipAddr, ifIndex, startTime, endTime, pointNumber);
		        }
		    }

		    if (chartData != null) {
		        //JTools.print(chartData, false);

		        for (int i = 0; i < chartData.length; i++) {
		            if (chartData[i].length != chartData[0].length) System.out.println("Data size of chart is wrong at i=" + i);

		            float[] data = new float[chartData[i].length - 1];
		            int j = 0;
                    for ( ; j < chartData[i].length - 1; j++) {
                        data[j] = Float.parseFloat(chartData[i][j + 1]);
                    }
		            dataSet.put(chartData[i][0], data);
		        }
		    }
		    else {
		        System.out.println("Nothing returned from database.");
		        dataSet.put(sdf.format(new Date()), new float[]{0, 0, 0});
		    }

		    String json_data = new Gson().toJson(dataSet);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    try {
		        response.getWriter().write(json_data);
		    } catch (IOException e) {
		        System.out.println(e.getMessage());
		    }
		}
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
