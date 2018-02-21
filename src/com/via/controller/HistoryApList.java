package com.via.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
@WebServlet("/HistoryApList")
public class HistoryApList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HistoryApList() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
    	String action = request.getParameter("action");
    	String acIp = request.getParameter("acIp");
    	String apIp = request.getParameter("apIp");

		if (action != null && action.equals("get_apList")) {
			String[][] apList = network.getHistoryApList(acIp);
			
			if (apList == null ) {
				System.out.println("The ap list table is no data.");
			}
			else
			{
				/*String[][] output = new String [apList.length-1][1];
				
				for(int i=0; i<output.length; i++){
					for(int j=0; j<1; j++){
					output[i][j] = apList[i][j];
					}
				}*/
				request.setAttribute("apList", apList);
			}
			
			ArrayList<String[]> list = new ArrayList<String[]>();
			if (apList != null) {
		        for(int i=0;i<apList.length;i++){
		            list.add(apList[i]);
		            //System.out.println(Arrays.toString(list.get(list.size()-1)));
		        }
		    }		
		}
		else if (action != null && action.equals("get_ssidList")) {
			String[][] ssidList = network.getHistoryApSsidList(acIp, apIp);
			
			ArrayList<String[]> list = new ArrayList<String[]>();
			if (ssidList != null) {
		        for(int i=0;i<ssidList.length;i++){
		            list.add(ssidList[i]);
		            //System.out.println(Arrays.toString(list.get(list.size()-1)));
		        }
		    }
			else {
				System.out.println("The ap ssid list table is no data.");
			}
			
			String json_data = new Gson().toJson(list);
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
