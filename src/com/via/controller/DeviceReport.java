package com.via.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.via.model.*;
import com.via.system.Config;

/**
 * Servlet implementation class HistoryChart
 */
@WebServlet("/DeviceReport")
public class DeviceReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceReport() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
    	JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		String action = request.getParameter("action");

		if (action != null && action.equals("calendar")) {
		    String[] boundaryTimes = network.getDeviceReportTime();
		    
		    int y1 = 0, m1 = 0, d1 = 0, y2 = 0, m2 = 0, d2 = 0;
		    boolean parseFailed = true;
		    if (boundaryTimes != null) {
		        try {
                    calendar.setTime(sdf.parse(boundaryTimes[0]));          // first record
                    y1 = calendar.get(Calendar.YEAR);
                    m1 = calendar.get(Calendar.MONTH) + 1;
                    d1 = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.setTime(new Date());          // last record
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
		    
		    System.out.println(String.format("=====calendar y1=%d m1=%d d1=%d y2=%d m2=%d d2=%d", y1, m1, d1, y2, m2, d2));
		    
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
		else if (action != null && action.equals("reportTable")) {
			String ipAddr = request.getParameter("ip");
			String timeSelect = request.getParameter("timeSelect");
			String dateText = request.getParameter("dateText");
			
			System.out.println("ipArray= " + ipAddr + " dateType= " + timeSelect + " dateText= " + dateText);
			
			String[] ipItems =  ipAddr.split(",");
			
			String startTime, endTime;
			
			

			if(timeSelect != null && timeSelect.equals("last_week")){
				int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
	    		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
	    		String startDay = sdfDay.format(calendar.getTime());
	    		calendar.add(Calendar.DAY_OF_YEAR, 6);
	    		String endDay = sdfDay.format(calendar.getTime());
	    		
				startTime = startDay + " 00:00:00.000";
				endTime = endDay + " 23:59:59.999";
			}
			else if (timeSelect != null && (timeSelect.equals("day") || timeSelect.equals("week") || timeSelect.equals("month"))) {
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
		        startTime = dateText != null && !dateText.isEmpty() ? dateText + " 00:00:00.000" : null;
		        endTime = dateText != null && !dateText.isEmpty() ? dateText + " 23:59:59.999" : null;
		    }
		    
		    System.out.println("startTime= "+startTime+" endTime= "+endTime);
		    
		    ArrayList<JDeviceReport> item = network.getRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    //request.setAttribute("deviceReportList", item);
		    
		    ArrayList<JDeviceReport> l2l3Switch = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("l2switch") || device.getDeviceType().equals("l3switch"))
		    		l2l3Switch.add(device);
		    }
		    
		    request.setAttribute("l2l3Switch", l2l3Switch);
		    
		    ArrayList<JDeviceReport> server = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("server") || device.getDeviceType().equals("NMS"))
		    		server.add(device);
		    }
		    
		    request.setAttribute("server", server);
		    
		    ArrayList<JDeviceReport> ac = new ArrayList<JDeviceReport>();
			ArrayList<String[]> ApTrafficList = new ArrayList<String[]>();
			List<ArrayList<String[]>> ApSsidTrafficList = new ArrayList<ArrayList<String[]>>();
			ArrayList<String[]> ApSsidTraffic = new ArrayList<String[]>();
			ArrayList<String[]> SsidTraffic = new ArrayList<String[]>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAC"))
		    		ac.add(device);

		    	if (device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")) {
					ApTrafficList = network.getApListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);

					ApSsidTrafficList = network.getApSsidListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);
					if (ApSsidTrafficList != null) {
						ApSsidTraffic = ApSsidTrafficList.get(0);
						SsidTraffic = ApSsidTrafficList.get(1);
					}
				}
		    }
		    
		    request.setAttribute("ac", ac);
			request.setAttribute("ApTrafficList", ApTrafficList);
			request.setAttribute("ApSsidTraffic", ApSsidTraffic);
			request.setAttribute("SsidTraffic", SsidTraffic);
		    
		    ArrayList<JDeviceReport> firewall = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("firewall"))
		    		firewall.add(device);
		    }
		    
		    request.setAttribute("firewall", firewall);
		    
		    ArrayList<JDeviceReport> ap = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAP"))
		    		ap.add(device);
		    }
		    
		    request.setAttribute("ap", ap);
		    
		    ArrayList<JInterfaceTerminalReport> terminal = network.getTerminalRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    request.setAttribute("terminal", terminal);
		    
		}
		else if (action != null && action.equals("getReportMailAddr")) {
			request.setAttribute("DailyMail", Config.getReportDailyMail().split(","));
			request.setAttribute("WeeklyMail", Config.getReportWeeklyMail().split(","));
			request.setAttribute("MonthlyMail", Config.getReportMonthlyMail().split(","));
		}
		else if (action != null && action.equals("setReportMailAddr")) {
			String dailyAccountText= request.getParameter("dailyAccountText");
			String weeklyAccountText= request.getParameter("weeklyAccountText");
			String monthlyAccountText= request.getParameter("monthlyAccountText");
			boolean isOk = false;
			
			if(dailyAccountText != null){
				Config.setReportDailyMail(dailyAccountText);
				isOk = true;
			}
			if(weeklyAccountText != null){
				Config.setReportWeeklyMail(weeklyAccountText);
				isOk = true;
			}
			if(monthlyAccountText != null){
				Config.setReportMonthlyMail(monthlyAccountText);
				isOk = true;
			}
			
			try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
                System.out.println("Response failed when set mail report.");
            }
		}
		else if (action != null && action.equals("periodicalReport")) {
			List<JDevice> deviceList = network.getDeviceList();
			String timeSelect= request.getParameter("timeSelect");
			String startTime = "", endTime = "";
			
			if(timeSelect != null && timeSelect.equals("daily")){
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				String date = sdfDay.format(calendar.getTime());
				request.setAttribute("date", date);
				
				startTime = sdfDay.format(calendar.getTime()) + " 00:00:00.000";
				endTime = sdfDay.format(calendar.getTime()) + " 23:59:59.999";
				
				System.out.println("startTime= "+startTime+" endTime= "+endTime);
			}
			else if(timeSelect != null && timeSelect.equals("weekly")){
				String startDay, endDay;
	    		int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
	    		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
	    		startDay = sdfDay.format(calendar.getTime());
	    		calendar.add(Calendar.DAY_OF_YEAR, 6);
	    		endDay = sdfDay.format(calendar.getTime());
	    		
	    		String date = startDay + " ~ " + endDay;
				request.setAttribute("date", date);
	    		
				startTime = startDay + " 00:00:00.000";
				endTime = endDay + " 23:59:59.999";

				System.out.println("startTime= "+startTime+" endTime= "+endTime);
			}
			else if(timeSelect != null && timeSelect.equals("monthly")){
				String startDay, endDay;
    			calendar.add(Calendar.MONTH, -1);
    			calendar.set(Calendar.DATE, 1);
    			startDay = sdfDay.format(calendar.getTime());
    			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // changed calendar to cal
    			endDay = sdfDay.format(calendar.getTime());
    			
    			String date = startDay + " ~ " + endDay;
				request.setAttribute("date", date);
				
				startTime = startDay + " 00:00:00.000";
				endTime = endDay + " 23:59:59.999";

				System.out.println("startTime= "+startTime+" endTime= "+endTime);
			}
		    
			String ipStr = "";
		    for(JDevice device : deviceList){
		    	if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5901.1")){
		    		ipStr += device.getPublicIp()+",";
		    	}
		    }
		    String[] ipItems = ipStr.split(",");
		    ArrayList<JDeviceReport> item = network.getRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    //request.setAttribute("deviceReportList", item);
		    
		    ArrayList<JDeviceReport> l2l3Switch = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("l2switch") || device.getDeviceType().equals("l3switch"))
		    		l2l3Switch.add(device);
		    }
		    
		    request.setAttribute("l2l3Switch", l2l3Switch);
		    
		    ArrayList<JDeviceReport> server = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("server") || device.getDeviceType().equals("NMS"))
		    		server.add(device);
		    }
		    
		    request.setAttribute("server", server);
		    
		    ArrayList<JDeviceReport> ac = new ArrayList<JDeviceReport>();
			ArrayList<String[]> ApTrafficList = new ArrayList<String[]>();
			List<ArrayList<String[]>> ApSsidTrafficList = new ArrayList<ArrayList<String[]>>();
			ArrayList<String[]> ApSsidTraffic = new ArrayList<String[]>();
			ArrayList<String[]> SsidTraffic = new ArrayList<String[]>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAC"))
		    		ac.add(device);

		    	if (device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")) {
					ApTrafficList = network.getApListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);

					ApSsidTrafficList = network.getApSsidListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);
					if (ApSsidTrafficList != null) {
						ApSsidTraffic = ApSsidTrafficList.get(0);
						SsidTraffic = ApSsidTrafficList.get(1);
					}
				}
		    }
		    
		    request.setAttribute("ac", ac);
			request.setAttribute("ApTrafficList", ApTrafficList);
			request.setAttribute("ApSsidTraffic", ApSsidTraffic);
			request.setAttribute("SsidTraffic", SsidTraffic);
		    
		    ArrayList<JDeviceReport> firewall = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("firewall"))
		    		firewall.add(device);
		    }
		    
		    request.setAttribute("firewall", firewall);
		    
		    ArrayList<JDeviceReport> ap = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAP"))
		    		ap.add(device);
		    }
		    
		    request.setAttribute("ap", ap);
		    
		    ArrayList<JInterfaceTerminalReport> rx = network.getTerminalRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    Collections.sort(rx, new Comparator<JInterfaceTerminalReport>() {
	            @Override
	            public int compare(JInterfaceTerminalReport  rx1, JInterfaceTerminalReport  rx2)
	            {
	            	int result = 0;
	            	if(rx1.getPortId() != 0){
	            		result = rx1.getRxRanking()-rx2.getRxRanking();
	            	}

	                return result;
	            }
	        });
		    
		    request.setAttribute("rx", rx);
		    
		    ArrayList<JInterfaceTerminalReport> tx = new ArrayList<JInterfaceTerminalReport>(rx);
		    
		    Collections.sort(tx, new Comparator<JInterfaceTerminalReport>() {
	            @Override
	            public int compare(JInterfaceTerminalReport  tx1, JInterfaceTerminalReport  tx2)
	            {
	            	int result = 0;
	            	if(tx1.getPortId() != 0){
	            		result = tx1.getTxRanking()-tx2.getTxRanking();
	            	}

	                return result;
	            }
	        });
		    
		    request.setAttribute("tx", tx);
		    
		}
		else if (action != null && action.equals("weeklyReportTable")) {
			List<JDevice> deviceList = network.getDeviceList();
			String startDay, endDay;
    		int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
    		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
    		startDay = sdfDay.format(calendar.getTime());
    		calendar.add(Calendar.DAY_OF_YEAR, 6);
    		endDay = sdfDay.format(calendar.getTime());
    		
    		String weekDate = startDay + " ~ " + endDay;
			request.setAttribute("date", weekDate);
    		
			
			String startTime = startDay + " 00:00:00.000", endTime = endDay + " 23:59:59.999";

			System.out.println("startTime= "+startTime+" endTime= "+endTime);
		    
			String ipStr = "";
		    for(JDevice device : deviceList){
		    	if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5901.1")){
		    		ipStr += device.getPublicIp()+",";
		    	}
		    }
		    String[] ipItems = ipStr.split(",");
		    ArrayList<JDeviceReport> item = network.getRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    //request.setAttribute("deviceReportList", item);
		    
		    ArrayList<JDeviceReport> l2l3Switch = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("l2switch") || device.getDeviceType().equals("l3switch"))
		    		l2l3Switch.add(device);
		    }
		    
		    request.setAttribute("l2l3Switch", l2l3Switch);
		    
		    ArrayList<JDeviceReport> server = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("server") || device.getDeviceType().equals("NMS"))
		    		server.add(device);
		    }
		    
		    request.setAttribute("server", server);
		    
		    ArrayList<JDeviceReport> ac = new ArrayList<JDeviceReport>();
			ArrayList<String[]> ApTrafficList = new ArrayList<String[]>();
			List<ArrayList<String[]>> ApSsidTrafficList = new ArrayList<ArrayList<String[]>>();
			ArrayList<String[]> ApSsidTraffic = new ArrayList<String[]>();
			ArrayList<String[]> SsidTraffic = new ArrayList<String[]>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAC"))
		    		ac.add(device);

		    	if (device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")) {
					ApTrafficList = network.getApListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);

					ApSsidTrafficList = network.getApSsidListRxTxOctetTraffic(device.getPublicIp(), startTime, endTime);
					if (ApSsidTrafficList != null) {
						ApSsidTraffic = ApSsidTrafficList.get(0);
						SsidTraffic = ApSsidTrafficList.get(1);
					}
				}
		    }
		    
		    request.setAttribute("ac", ac);
			request.setAttribute("ApTrafficList", ApTrafficList);
			request.setAttribute("ApSsidTraffic", ApSsidTraffic);
			request.setAttribute("SsidTraffic", SsidTraffic);
		    
		    ArrayList<JDeviceReport> firewall = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("firewall"))
		    		firewall.add(device);
		    }
		    
		    request.setAttribute("firewall", firewall);
		    
		    ArrayList<JDeviceReport> ap = new ArrayList<JDeviceReport>();
		    for(JDeviceReport device : item){
		    	if(device.getDeviceType().equals("wlanAP"))
		    		ap.add(device);
		    }
		    
		    request.setAttribute("ap", ap);
		    
		    ArrayList<JInterfaceTerminalReport> rx = network.getTerminalRxTxOctetRaterReport(ipItems, startTime, endTime);
		    
		    Collections.sort(rx, new Comparator<JInterfaceTerminalReport>() {
	            @Override
	            public int compare(JInterfaceTerminalReport  rx1, JInterfaceTerminalReport  rx2)
	            {
	            	int result = 0;
	            	if(rx1.getPortId() != 0){
	            		result = rx1.getRxRanking()-rx2.getRxRanking();
	            	}

	                return result;
	            }
	        });
		    
		    request.setAttribute("rx", rx);
		    
		    ArrayList<JInterfaceTerminalReport> tx = new ArrayList<JInterfaceTerminalReport>(rx);
		    
		    Collections.sort(tx, new Comparator<JInterfaceTerminalReport>() {
	            @Override
	            public int compare(JInterfaceTerminalReport  tx1, JInterfaceTerminalReport  tx2)
	            {
	            	int result = 0;
	            	if(tx1.getPortId() != 0){
	            		result = tx1.getTxRanking()-tx2.getTxRanking();
	            	}

	                return result;
	            }
	        });
		    
		    request.setAttribute("tx", tx);
		}
		else if (action.equals("reportSend")) {
			
    		String projectSpace = JSystem.projectSpace;
    		String fileName = "", filePath = "", content = "";
    		String timeSelect = request.getParameter("timeSelect");
    		
    		MailConfig mailConfig = Config.getMailConfig();
        	SmtpConfig smtpConfig = Config.getSmtp1();
        	String from = mailConfig.getFromLable() + " <" + smtpConfig.getUsername() + ">";
        	String subject = mailConfig.getSubject();
        	String mailHost = smtpConfig.getHost();
        	String mailPort = smtpConfig.getPort();
        	String username = smtpConfig.getUsername();
        	String password = smtpConfig.getPassword();
        	int timeout = smtpConfig.getTimeout();
        	ReqServlet call = new ReqServlet();
        	boolean isOk = false;
        	List<JAccount> account = network.accountItems();
    		
    		if(Config.getReportWeeklyMail() != null && timeSelect.equals("daily")){
    			ArrayList<String> mailList = new ArrayList<String>();
    			for(JAccount data : account){
    				for(String name : Config.getReportDailyMail().split(",")){
    					if(data.getName().equals(name)){
    						mailList.add(data.getEmail());
    					}
    				}
    			}
    			
    			call.redirect(timeSelect);
    			
    			calendar.add(Calendar.DAY_OF_YEAR, -1);
        		fileName = "/" + sdfDay.format(calendar.getTime()) + ".html";
        		filePath = projectSpace + fileName;
    			
    			File file = new File(filePath);
    			if(file.exists()){
    				System.out.println(fileName + " is exist.");
    				
    				try {
    	    			content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
    	    		} catch (FileNotFoundException e) {
    	    			e.printStackTrace();
    	    		}
    				
    				for (String to : mailList) {
    					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
    		    			System.out.println("Send mail to " + to + " success.");
    		    			isOk = true;
    		    		}
    		    		else {
    		    			System.out.println("Send mail to " + to + " failed.");
    		    		} 
    				}
    			}
    			
    		}
    		
    		if(Config.getReportWeeklyMail() != null && timeSelect.equals("weekly")){
    			ArrayList<String> mailList = new ArrayList<String>();
    			for(JAccount data : account){
    				for(String name : Config.getReportWeeklyMail().split(",")){
    					if(data.getName().equals(name)){
    						mailList.add(data.getEmail());
    					}
    				}
    			}
    			call.redirect(timeSelect);
    			
    			String startDay, endDay;
        		int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
        		startDay = sdfDay.format(calendar.getTime());
        		calendar.add(Calendar.DAY_OF_YEAR, 6);
        		endDay = sdfDay.format(calendar.getTime());
        		fileName = "/" + startDay + "~" + endDay + ".html";
        		filePath = projectSpace + fileName;
        		
        		File file = new File(filePath);
    			if(file.exists()){
    				System.out.println(fileName + " is exist.");
    				
    				try {
    	    			content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
    	    		} catch (FileNotFoundException e) {
    	    			e.printStackTrace();
    	    		}
    				
    				for (String to : mailList) {
    					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
    		    			System.out.println("Send mail to " + to + " success.");
    		    			isOk = true;
    		    		}
    		    		else {
    		    			System.out.println("Send mail to " + to + " failed.");
    		    		} 
    				}
    			}
    			
    		}
    		
    		if(Config.getReportMonthlyMail() != null && timeSelect.equals("monthly")){
    			ArrayList<String> mailList = new ArrayList<String>();
    			for(JAccount data : account){
    				for(String name : Config.getReportMonthlyMail().split(",")){
    					if(data.getName().equals(name)){
    						mailList.add(data.getEmail());
    					}
    				}
    			}
    			call.redirect(timeSelect);
    			
    			String startDay, endDay;
    			calendar.add(Calendar.MONTH, -1);
    			calendar.set(Calendar.DATE, 1);
    			startDay = sdfDay.format(calendar.getTime());
    			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // changed calendar to cal
    			endDay = sdfDay.format(calendar.getTime());
        		fileName = "/" + startDay + "~" + endDay + ".html";
        		filePath = projectSpace + fileName;
        		
        		File file = new File(filePath);
    			if(file.exists()){
    				System.out.println(fileName + " is exist.");
    				
    				try {
    	    			content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
    	    		} catch (FileNotFoundException e) {
    	    			e.printStackTrace();
    	    		}
    				
    				for (String to : mailList) {
    					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
    		    			System.out.println("Send mail to " + to + " success.");
    		    			isOk = true;
    		    		}
    		    		else {
    		    			System.out.println("Send mail to " + to + " failed.");
    		    		} 
    				}
    			}
    			
    		}
    		
    		try {
                response.getWriter().write(isOk ? "Success" : "Failed");
            }
            catch (IOException e) {
                System.out.println("Response failed when send immediately mail report.");
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
