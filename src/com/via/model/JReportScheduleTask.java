package com.via.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.via.database.JDbAccount;
import com.via.database.JDbDevice;
import com.via.system.Config;

public class JReportScheduleTask extends TimerTask {
	
	
	private List<JDevice> deviceList;
	private JDbAccount dbAccount;
	private Date scheduledTime;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public JReportScheduleTask(List<JDevice> deviceList, JDbAccount dbAccount) {
		this.deviceList = deviceList;
		this.dbAccount = dbAccount;
	}
	
	public void run() {
	    this.scheduledTime = new Date(this.scheduledExecutionTime());      // the expected time this task should run
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduledTime);
		
		if (calendar.get(Calendar.MINUTE) % 10 == 0) {		              // do it every 30th minute
			reportSchedule();
		}
	}
	
	public void reportSchedule() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		int month = calendar.get(Calendar.DAY_OF_MONTH);
		String currentTime = String.format("%02d:%02d", hours, minutes);
		String projectSpace = JSystem.projectSpace;
		String fileName = "", filePath = "", timeSelect = "", content = "";
		
		MailConfig mailConfig = Config.getMailConfig();
    	SmtpConfig smtpConfig = Config.getSmtp1();
    	String from = mailConfig.getFromLable() + " <" + smtpConfig.getUsername() + ">";
    	String subject = mailConfig.getSubject();
    	String mailHost = smtpConfig.getHost();
    	String mailPort = smtpConfig.getPort();
    	String username = smtpConfig.getUsername();
    	String password = smtpConfig.getPassword();
    	int timeout = smtpConfig.getTimeout();
    	List<JAccount> account = dbAccount.getAccountItems();
		
		if(Config.getReportDailyMail() != null && currentTime.equals("08:00")){
			ArrayList<String> mailList = new ArrayList<String>();
			for(JAccount data : account){
				for(String name : Config.getReportDailyMail().split(",")){
					if(data.getName().equals(name)){
						mailList.add(data.getEmail());
					}
				}
			}
			timeSelect = "daily";
			ReqServlet call = new ReqServlet();
			call.redirect(timeSelect);
			
			calendar.add(Calendar.DAY_OF_YEAR, -1);
    		fileName = "/" + sdf.format(calendar.getTime()) + ".html";
    		filePath = projectSpace + fileName;
			
			File file = new File(filePath);
			if(file.exists()){
				System.out.println(fileName + " is exist.");
				
				content = getWebContent(filePath);
				
				for (String to : mailList) {
					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
		    			System.out.println("Send mail to " + to + " success.");
		    		}
		    		else {
		    			System.out.println("Send mail to " + to + " failed.");
		    		} 
				}
			}
			
		}
		
		if(Config.getReportWeeklyMail() != null && currentTime.equals("08:00") && week == 1){
			ArrayList<String> mailList = new ArrayList<String>();
			for(JAccount data : account){
				for(String name : Config.getReportWeeklyMail().split(",")){
					if(data.getName().equals(name)){
						mailList.add(data.getEmail());
					}
				}
			}
			timeSelect = "weekly";
			ReqServlet call = new ReqServlet();
			call.redirect(timeSelect);
			
			String startDay, endDay;
    		int diff = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
    		calendar.add(Calendar.DAY_OF_YEAR, -diff -7);
    		startDay = sdf.format(calendar.getTime());
    		calendar.add(Calendar.DAY_OF_YEAR, 6);
    		endDay = sdf.format(calendar.getTime());
    		fileName = "/" + startDay + "~" + endDay + ".html";
    		filePath = projectSpace + fileName;
    		
    		File file = new File(filePath);
			if(file.exists()){
				System.out.println(fileName + " is exist.");
				
				content = getWebContent(filePath);
				
				for (String to : mailList) {
					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
		    			System.out.println("Send mail to " + to + " success.");
		    		}
		    		else {
		    			System.out.println("Send mail to " + to + " failed.");
		    		} 
				}
			}
			
		}
		
		if(Config.getReportMonthlyMail() != null && currentTime.equals("08:00") && month == 1){
			ArrayList<String> mailList = new ArrayList<String>();
			for(JAccount data : account){
				for(String name : Config.getReportMonthlyMail().split(",")){
					if(data.getName().equals(name)){
						mailList.add(data.getEmail());
					}
				}
			}
			timeSelect = "monthly";
			ReqServlet call = new ReqServlet();
			call.redirect(timeSelect);
			
			String startDay, endDay;
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, 1);
			startDay = sdf.format(calendar.getTime());
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // changed calendar to cal
			endDay = sdf.format(calendar.getTime());
    		fileName = "/" + startDay + "~" + endDay + ".html";
    		filePath = projectSpace + fileName;
			
			File file = new File(filePath);
			if(file.exists()){
				System.out.println(fileName + " is exist.");
				
				content = getWebContent(filePath);
				
				for (String to : mailList) {
					if (Mail.send(from, to, subject, "test send attachment", mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), filePath, fileName, content)) {
		    			System.out.println("Send mail to " + to + " success.");
		    		}
		    		else {
		    			System.out.println("Send mail to " + to + " failed.");
		    		} 
				}
			}
			
		}
		
	}
	
	public String getWebContent(String filePath) {
		String content = "";
		try {
			content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return content;
	}
}
