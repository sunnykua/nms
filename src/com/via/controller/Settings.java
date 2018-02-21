package com.via.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.via.database.JModule;
import com.via.model.*;
import com.via.system.Config;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class Settings
 */
@MultipartConfig
@WebServlet("/Settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Settings() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String action = request.getParameter("action");
		//System.out.println("action: " + action);
		String accountUser = request.getSession().getAttribute("username").toString();

		JNetwork network = (JNetwork) getServletContext().getAttribute("network");
		Logger logger = Logger.getLogger(this.getClass());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (action == null) {
			System.out.println("action is missing.");
			//return;
			
		} else if (action.equals("get_log_settings")) {
			MailConfig mailConfig = Config.getMailConfig();
			request.setAttribute("log_mail_subject", mailConfig.getSubject());
			request.setAttribute("log_mail_from", mailConfig.getFromLable());
			SmtpConfig smtpConfig = Config.getSmtp1();
			request.setAttribute("smtp_host", smtpConfig.getHost());
			//request.setAttribute("smtp_port", smtpConfig.getPort());
			request.setAttribute("smtp_username", smtpConfig.getUsername());
			request.setAttribute("smtp_password", smtpConfig.getPassword());
			

		} else if (action.equals("set_log_settings")) {
			String fromLabel = request.getParameter("log_mail_from");
			String subject = request.getParameter("log_mail_subject");
			String smtp_host = request.getParameter("smtp_host");
			//String smtp_port = request.getParameter("smtp_port");
			String smtp_username = request.getParameter("smtp_username");
			String smtp_password = request.getParameter("smtp_password");
			MailConfig mailConfig = new MailConfig();
			mailConfig.setFromLable(fromLabel);
			mailConfig.setSubject(subject);
			Config.setMailConfig(mailConfig);
			SmtpConfig smtpConfig = new SmtpConfig();
			smtpConfig.setHost(smtp_host);
			smtpConfig.setPort("465");
			smtpConfig.setTimeout(5000);
			smtpConfig.setUsername(smtp_username);
			smtpConfig.setPassword(smtp_password);
			Config.setSmtp1(smtpConfig);
			
			if(!accountUser.equalsIgnoreCase("super")){
				logger.info("Set Mail Success. User: " + accountUser);
			}

			try {
				response.getWriter().write("Success");
			} catch (IOException e) {
				System.out.println("Response failed.");
			}
		} else if (action.equals("get_alarm_settings")) {
			String logAlarmInfo[] = JAlarm.getAlarmInfo();
			request.setAttribute("NmsStartUpSendEmail", logAlarmInfo[0]);
			request.setAttribute("NmsShutDownSendEmail", logAlarmInfo[1]);
			request.setAttribute("DeviceDisconnectSendEmail", logAlarmInfo[2]);
			request.setAttribute("MonitoredPortLinkUpSendEmail", logAlarmInfo[3]);
			request.setAttribute("MonitoredPortLinkDownSendEmail", logAlarmInfo[4]);
			request.setAttribute("CriticalPortLinkUpSendEmail", logAlarmInfo[5]);
			request.setAttribute("CriticalPortLinkDownSendEmail", logAlarmInfo[6]);
			request.setAttribute("NmsStartUpSendSms", logAlarmInfo[7]);
			request.setAttribute("NmsShutDownSendSms", logAlarmInfo[8]);
			request.setAttribute("DeviceDisconnectSendSms", logAlarmInfo[9]);
			request.setAttribute("MonitoredPortLinkUpSendSms", logAlarmInfo[10]);
			request.setAttribute("MonitoredPortLinkDownSendSms", logAlarmInfo[11]);
			request.setAttribute("CriticalPortLinkUpSendSms", logAlarmInfo[12]);
			request.setAttribute("CriticalPortLinkDownSendSms", logAlarmInfo[13]);
			request.setAttribute("WebUpdatedSendEmail", logAlarmInfo[14]);
			request.setAttribute("WebUpdatedSendSms", logAlarmInfo[15]);
			request.setAttribute("ManagementFailSendEmail", logAlarmInfo[16]);
			request.setAttribute("ManagementFailSendSms", logAlarmInfo[17]);
			
			List<String[]> emailItems = new ArrayList<>(); //下面的資料是有順序性的，需依序加入 array list
			
			emailItems.add(JAlarm.getNmsStartUpSendEmailList().split(","));
			emailItems.add(JAlarm.getNmsShutDownSendEmailList().split(","));
			emailItems.add(JAlarm.getDeviceDisconnectSendEmailList().split(","));
			emailItems.add(JAlarm.getMonitoredPortLinkUpSendEmailList().split(","));
			emailItems.add(JAlarm.getMonitoredPortLinkDownSendEmailList().split(","));
			emailItems.add(JAlarm.getCriticalPortLinkUpSendEmailList().split(","));
			emailItems.add(JAlarm.getCriticalPortLinkDownSendEmailList().split(","));
			emailItems.add(JAlarm.getWebUpdatedSendEmailList().split(","));
			emailItems.add(JAlarm.getManagementFailSendEmailList().split(","));
			
			request.setAttribute("emailAlarm",emailItems);
			
            List<String[]> smsItems = new ArrayList<>();
			
			smsItems.add(JAlarm.getNmsStartUpSendSmsList().split(","));
			smsItems.add(JAlarm.getNmsShutDownSendSmsList().split(","));
			smsItems.add(JAlarm.getDeviceDisconnectSendSmsList().split(","));
			smsItems.add(JAlarm.getMonitoredPortLinkUpSendSmsList().split(","));
			smsItems.add(JAlarm.getMonitoredPortLinkDownSendSmsList().split(","));
			smsItems.add(JAlarm.getCriticalPortLinkUpSendSmsList().split(","));
			smsItems.add(JAlarm.getCriticalPortLinkDownSendSmsList().split(","));
			smsItems.add(JAlarm.getWebUpdatedSendSmsList().split(","));
			smsItems.add(JAlarm.getManagementFailSendSmsList().split(","));
			
			request.setAttribute("smsAlarm",smsItems);
			

		} else if (action.equals("set_alarm_settings")) {
			String[] data = request.getParameterValues("alarm_setting[]");
			String[] caseEmail1 = request.getParameterValues("caseEmail1[]");
			String caseEmail1User = network.setAlarmUser(caseEmail1);
			
			String[] caseSMS1 = request.getParameterValues("caseSMS1[]");
			String caseSMS1User = network.setAlarmUser(caseSMS1);
			
			String[] caseEmail2 = request.getParameterValues("caseEmail2[]");
			String caseEmail2User = network.setAlarmUser(caseEmail2);
			
			String[] caseSMS2 = request.getParameterValues("caseSMS2[]");
			String caseSMS2User = network.setAlarmUser(caseSMS2);
			
			String[] caseEmail3 = request.getParameterValues("caseEmail3[]");
			String caseEmail3User = network.setAlarmUser(caseEmail3);
			
			String[] caseSMS3 = request.getParameterValues("caseSMS3[]");
			String caseSMS3User = network.setAlarmUser(caseSMS3);
			
			String[] caseEmail4 = request.getParameterValues("caseEmail4[]");
			String caseEmail4User = network.setAlarmUser(caseEmail4);
			
			String[] caseSMS4 = request.getParameterValues("caseSMS4[]");
			String caseSMS4User = network.setAlarmUser(caseSMS4);
			
			String[] caseEmail5 = request.getParameterValues("caseEmail5[]");
			String caseEmail5User = network.setAlarmUser(caseEmail5);
					
			String[] caseSMS5 = request.getParameterValues("caseSMS5[]");
			String caseSMS5User = network.setAlarmUser(caseSMS5);
			
			String[] caseEmail6 = request.getParameterValues("caseEmail6[]");
			String caseEmail6User = network.setAlarmUser(caseEmail6);
			
			String[] caseSMS6 = request.getParameterValues("caseSMS6[]");
			String caseSMS6User = network.setAlarmUser(caseSMS6);
			
			String[] caseEmail7 = request.getParameterValues("caseEmail7[]");
			String caseEmail7User = network.setAlarmUser(caseEmail7);
			
			String[] caseSMS7 = request.getParameterValues("caseSMS7[]");
			String caseSMS7User = network.setAlarmUser(caseSMS7);

			String[] caseEmail8 = request.getParameterValues("caseEmail8[]");
			String caseEmail8User = network.setAlarmUser(caseEmail8);		

			String[] caseSMS8 = request.getParameterValues("caseSMS8[]");
			String caseSMS8User = network.setAlarmUser(caseSMS8);
			
			String[] caseEmail9 = request.getParameterValues("caseEmail9[]");
			String caseEmail9User = network.setAlarmUser(caseEmail9);		

			String[] caseSMS9 = request.getParameterValues("caseSMS9[]");
			String caseSMS9User = network.setAlarmUser(caseSMS9);

			JAlarm.setAlarmInfo(data[0], data[2], data[4], data[6], data[8], data[10], data[12], data[14], data[16], data[1], data[3], data[5], data[7], data[9], data[11], data[13], data[15], data[17]);
			
			JAlarm.setAlarmList(caseEmail1User, caseEmail2User, caseEmail3User, caseEmail4User, caseEmail5User, caseEmail6User, caseEmail7User, caseEmail8User, caseEmail9User, caseSMS1User, caseSMS2User, caseSMS3User, caseSMS4User, caseSMS5User, caseSMS6User, caseSMS7User, caseSMS8User, caseSMS9User);
			
			if(!accountUser.equalsIgnoreCase("super")){
				logger.info("Set Alarm Success. User: " + accountUser);
			}
			
			try {
				response.getWriter().write("Success");
			} catch (IOException e) {
				System.out.println("Response failed.");
			}
		} else if (action.equals("get_sms_settings")) {
			ShortMessageConfig config = Config.getSms1Config();
			request.setAttribute("sms_username", config.getUsername());
			request.setAttribute("sms_password", config.getPassword());
			
			System.out.println("get_sms_settings: " + config.getUsername() + ", " + config.getPassword());
		} else if (action.equals("set_sms_settings")) {
			String username = request.getParameter("sms_username");
			String password = request.getParameter("sms_password");
			
			ShortMessageConfig config = Config.getSms1Config();
			config.setProvider("SANZHU");
			config.setEncoding("BIG5");
			config.setUsername(username);
			config.setPassword(password);
			config.setTimeout(5000);			// ms
			Config.setSms1Config(config);
			
			if(!accountUser.equalsIgnoreCase("super")){
				logger.info("Set SMS Username and Password Success. User: " + accountUser);
			}

			try {
				response.getWriter().write("Success");
			} catch (IOException e) {
				System.out.println("Response failed.");
			}
		} else if (action.equals("accountAllItems")) {
    		List<JAccount> table = network.accountAllItems();
	    		if(table==null){
	    			System.out.println("no account items");
	    		}else
	    			request.setAttribute("accountAllItems", table);
		} else if (action.equals("module_check")){
			String model_name = request.getParameter("model_name");
			boolean check_complete = network.checkModelName(model_name);
			
			String result = check_complete ? "Repeat" : "No Repeat";
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else if (action.equals("module_add")) {
			String isDefault = "F";
			String brand = request.getParameter("brand");
			String model = request.getParameter("model");
			String device_type = request.getParameter("device_type");
			String snmp_sup = request.getParameter("snmp_sup");
			String snmp_timeout = request.getParameter("snmp_timeout");
			String revision = request.getParameter("revision");
			String read_commumity = request.getParameter("read_commumity");
			String write_commumity = request.getParameter("write_commumity");
			String object_id = request.getParameter("object_id");
			String inf_idx_cpu = request.getParameter("inf_idx_cpu");
			String inf_num = request.getParameter("inf_num");
			String rj45_num = request.getParameter("rj45_num");
			String fiber_num = request.getParameter("fiber_num");
			String jack_list = request.getParameter("jack_list");
			String speed_list = request.getParameter("speed_list");
			String host_resource = request.getParameter("host_resource");
			String sup_linkstate = request.getParameter("sup_linkstate");
			String sup_negostate = request.getParameter("sup_negostate");
			String sup_rxtxoctet = request.getParameter("sup_rxtxoctet");
			String sup_pkttype = request.getParameter("sup_pkttype");
			String sup_rmon = request.getParameter("sup_rmon");
			String sup_pvid = request.getParameter("sup_pvid");
			String sup_vlan = request.getParameter("sup_vlan");
			String sup_gvrp = request.getParameter("sup_gvrp");
			String sup_poe = request.getParameter("sup_poe");
			String sup_trap = request.getParameter("sup_trap");
			String sup_lldp = request.getParameter("sup_lldp");
			String sup_rstp = request.getParameter("sup_rstp");
			String sup_mstp = request.getParameter("sup_mstp");
			String sup_edgecoreresource = request.getParameter("sup_edgecoreresource");
			String sup_octet64 = request.getParameter("sup_octet64");
			String sup_egco_trap = request.getParameter("sup_egco_trap");

			/*System.out.println("===="+"\n"+brand+"\n"+model+"\n"+device_type+"\n"+snmp_sup+"\n"+snmp_timeout+"\n"+revision+"\n"+read_commumity+"\n"+write_commumity+"\n"
					+object_id+"\n"+inf_idx_cpu+"\n"+inf_num+"\n"+rj45_num+"\n"+fiber_num+"\n"+jack_list+"\n"+speed_list+"\n"
					+host_resource+"\n"+sup_linkstate+"\n"+sup_negostate+"\n"+sup_rxtxoctet+"\n"+sup_pkttype+"\n"+sup_rmon+"\n"+sup_pvid+"\n"
					+sup_vlan+"\n"+sup_gvrp+"\n"+sup_poe+"\n"+sup_trap+"\n"+sup_lldp+"\n"+sup_rstp+"\n"+sup_mstp+"\n"+sup_edgecoreresource+"\n"+sup_octet64+"\n"+sup_egco_trap);*/

			String result = "Fail";
			
			do {
				JModule module = new JModule();
				module.setDefault(isDefault.equals("T"));
				module.setBrandName(brand);
				module.setModelName(model);
				module.setModelRevision(0);
				module.setDeviceType(device_type);
				try {
					module.setSnmpSupport(Integer.parseInt(snmp_sup));
					module.setSnmpTimeout(Integer.parseInt(snmp_timeout));
				} catch (NumberFormatException e) {
					System.out.println("parse snmp support failed when add module.");
					break;
				}
				module.setModelRevision(Integer.parseInt(revision));
				module.setReadCommunity(read_commumity);
				module.setWriteCommunity(write_commumity);
				module.setObjectId(object_id);
				module.setInfCpuIndex(inf_idx_cpu);
				try {
					module.setInfNum(Integer.parseInt(inf_num));
					module.setRj45Num(Integer.parseInt(rj45_num));
					module.setFiberNum(Integer.parseInt(fiber_num));
				} catch (NumberFormatException e) {
					System.out.println("parse inf_num or rj45/fiber num failed when add module.");
					break;
				}

				String jackString = jack_list;
				jackString = jackString.replace("rj", "rj45");
				jackString = jackString.replace("fb", "fiber");
				jackString = jackString.replace("un", "unknown");
				String[] infJack = jackString.split(",");
				List<String> jackList = new ArrayList<String>();
				Collections.addAll(jackList, infJack);
				if (jackList.size() != module.getInfNum()) {
					System.out.println("get wrong jack num when add module.");
					break;
				}
				module.setJackList(jackList);

				String speedString = speed_list;
				String[] infSpeed = speedString.split(",");
				List<String> speedList = new ArrayList<String>();
				Collections.addAll(speedList, infSpeed);
				if (speedList.size() != module.getInfNum()) {
					System.out.println("get wrong speed num when add module.");
					break;
				}
				module.setSpeedList(speedList);

				module.setSupHostResource(host_resource.equals("T"));
				module.setSupLinkState(sup_linkstate.equals("T"));
				module.setSupNegoState(sup_negostate.equals("T"));
				module.setSupRxTxOctet(sup_rxtxoctet.equals("T"));
				module.setSupPacketType(sup_pkttype.equals("T"));
				module.setSupRmon(sup_rmon.equals("T"));
				module.setSupPvid(sup_pvid.equals("T"));
				module.setSupVlan(sup_vlan.equals("T"));
				module.setSupGvrp(sup_gvrp.equals("T"));
				module.setSupPoe(sup_poe.equals("T"));
				module.setSupTrap(sup_trap.equals("T"));
				module.setSupLldp(sup_lldp.equals("T"));
				module.setSupRStp(sup_rstp.equals("T"));
				module.setSupMStp(sup_mstp.equals("T"));
				module.setSupEdgeCoreResource(sup_edgecoreresource.equals("T"));
				module.setSupOctet64(sup_octet64.equals("T"));
				module.setSupEgcoTrap(sup_egco_trap.equals("T"));

				result = network.addModule(module);
			} while (false);

			
			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if (action.equals("module_delete")) {
			network.removeModule(request.getParameterValues("checkdelete[]"));						// TODO: check the result success or fail
			
		} else if (action.equals("module_set")) {
			String isDefault = "F";
			String brand = request.getParameter("brand");
			String model = request.getParameter("model");
			String device_type = request.getParameter("device_type");
			String snmp_sup = request.getParameter("snmp_sup");
			String snmp_timeout = request.getParameter("snmp_timeout");
			String read_commumity = request.getParameter("read_commumity");
			String write_commumity = request.getParameter("write_commumity");
			String object_id = request.getParameter("object_id");
			String inf_idx_cpu = request.getParameter("inf_idx_cpu");
			String inf_num = request.getParameter("inf_num");
			String rj45_num = request.getParameter("rj45_num");
			String fiber_num = request.getParameter("fiber_num");
			String jack_list = request.getParameter("jack_list");
			String speed_list = request.getParameter("speed_list");
			String host_resource = request.getParameter("host_resource");
			String sup_linkstate = request.getParameter("sup_linkstate");
			String sup_negostate = request.getParameter("sup_negostate");
			String sup_rxtxoctet = request.getParameter("sup_rxtxoctet");
			String sup_pkttype = request.getParameter("sup_pkttype");
			String sup_rmon = request.getParameter("sup_rmon");
			String sup_pvid = request.getParameter("sup_pvid");
			String sup_vlan = request.getParameter("sup_vlan");
			String sup_gvrp = request.getParameter("sup_gvrp");
			String sup_poe = request.getParameter("sup_poe");
			String sup_trap = request.getParameter("sup_trap");
			String sup_lldp = request.getParameter("sup_lldp");
			String sup_rstp = request.getParameter("sup_rstp");
			String sup_mstp = request.getParameter("sup_mstp");
			String sup_edgecoreresource = request.getParameter("sup_edgecoreresource");
			String sup_octet64 = request.getParameter("sup_octet64");
			String sup_egco_trap = request.getParameter("sup_egco_trap");

			/*System.out.println("===="+"\n"+brand+"\n"+model+"\n"+device_type+"\n"+snmp_sup+"\n"+snmp_timeout+"\n"+read_commumity+"\n"+write_commumity+"\n"
					+object_id+"\n"+inf_idx_cpu+"\n"+inf_num+"\n"+rj45_num+"\n"+fiber_num+"\n"+jack_list+"\n"+speed_list+"\n"
					+host_resource+"\n"+sup_linkstate+"\n"+sup_negostate+"\n"+sup_rxtxoctet+"\n"+sup_pkttype+"\n"+sup_rmon+"\n"+sup_pvid+"\n"
					+sup_vlan+"\n"+sup_gvrp+"\n"+sup_poe+"\n"+sup_trap+"\n"+sup_lldp+"\n"+sup_rstp+"\n"+sup_mstp+"\n"+sup_edgecoreresource+"\n"+sup_octet64+"\n"+sup_egco_trap);*/

			boolean success = false;

			do {
				JModule module = new JModule();
				module.setDefault(isDefault.equals("T"));
				module.setBrandName(brand);
				module.setModelName(model);
				module.setModelRevision(0);
				module.setDeviceType(device_type);
				try {
					module.setSnmpSupport(Integer.parseInt(snmp_sup));
					module.setSnmpTimeout(Integer.parseInt(snmp_timeout));
				} catch (NumberFormatException e) {
					System.out.println("parse snmp support failed when add module.");
					break;
				}
				module.setReadCommunity(read_commumity);
				module.setWriteCommunity(write_commumity);
				module.setObjectId(object_id);
				module.setInfCpuIndex(inf_idx_cpu);
				try {
					module.setInfNum(Integer.parseInt(inf_num));
					module.setRj45Num(Integer.parseInt(rj45_num));
					module.setFiberNum(Integer.parseInt(fiber_num));
				} catch (NumberFormatException e) {
					System.out.println("parse inf_num or rj45/fiber num failed when add module.");
					break;
				}

				String jackString = jack_list;
				jackString = jackString.replace("rj", "rj45");
				jackString = jackString.replace("fb", "fiber");
				jackString = jackString.replace("un", "unknown");
				String[] infJack = jackString.split(",");
				List<String> jackList = new ArrayList<String>();
				Collections.addAll(jackList, infJack);
				if (jackList.size() != module.getInfNum()) {
					System.out.println("get wrong jack num when add module.");
					break;
				}
				module.setJackList(jackList);

				String speedString = speed_list;
				String[] infSpeed = speedString.split(",");
				List<String> speedList = new ArrayList<String>();
				Collections.addAll(speedList, infSpeed);
				if (speedList.size() != module.getInfNum()) {
					System.out.println("get wrong speed num when add module.");
					break;
				}
				module.setSpeedList(speedList);

				module.setSupHostResource(host_resource.equals("T"));
				module.setSupLinkState(sup_linkstate.equals("T"));
				module.setSupNegoState(sup_negostate.equals("T"));
				module.setSupRxTxOctet(sup_rxtxoctet.equals("T"));
				module.setSupPacketType(sup_pkttype.equals("T"));
				module.setSupRmon(sup_rmon.equals("T"));
				module.setSupPvid(sup_pvid.equals("T"));
				module.setSupVlan(sup_vlan.equals("T"));
				module.setSupGvrp(sup_gvrp.equals("T"));
				module.setSupPoe(sup_poe.equals("T"));
				module.setSupTrap(sup_trap.equals("T"));
				module.setSupLldp(sup_lldp.equals("T"));
				module.setSupRStp(sup_rstp.equals("T"));
				module.setSupMStp(sup_mstp.equals("T"));
				module.setSupEdgeCoreResource(sup_edgecoreresource.equals("T"));
				module.setSupOctet64(sup_octet64.equals("T"));
				module.setSupEgcoTrap(sup_egco_trap.equals("T"));

				success = network.setModule(module);
			} while (false);

			String result = success ? "Success" : "Fail";

			try {
				response.getWriter().write(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
    	} else if (action.equals("module_get")) {
			List<JModule> moduleTable = network.getModule();
			request.setAttribute("moduleTable", moduleTable);
		
    	} else if (action.equals("version_set")) {
			String version = request.getParameter("version");
			Config.setNmsType(version);
		
		} else if (action.equals("value_set")) {
			String chart_timeInterval = request.getParameter("chart_timeInterval");
			if (chart_timeInterval != null && !chart_timeInterval.isEmpty()) {
				Config.setRealtimeChartTimeout(Integer.valueOf(chart_timeInterval));
				logger.info("Set Realtime Chart Timeout Success. User: " + accountUser);
			}
			
			String nms_port = request.getParameter("nms_port");
			if (nms_port != null && !nms_port.isEmpty()) {
				Config.setRemoteWebPort(Integer.valueOf(nms_port));
				logger.info("Set Remote Web Port Success. User: " + accountUser);
			}
			
			String login_num = request.getParameter("login_num");
			if (login_num != null && !login_num.isEmpty()) {
				Config.setLoginUserMax(Integer.valueOf(login_num));
				logger.info("Set Login User Max Success. User: " + accountUser);
			}
		
		} else if (action.equals("poe_set")) {//All poe schedule setting.
			String poeManual = request.getParameter("poeManual");
			String switchid = request.getParameter("switchid");
			String poeStartTime = request.getParameter("poeStartTime");
			String poeStartStatus = request.getParameter("poeStartStatus");
			String poeEndTime = request.getParameter("poeEndTime");
			String poeEndStatus = request.getParameter("poeEndStatus");
			//System.out.println("===="+"\n"+poeManual+"\n"+switchid+"\n"+poeStartTime+"\n"+poeStartStatus+"\n"+poeEndTime+"\n"+poeEndStatus);
			
			boolean isPoeManual = poeManual.equals("1") ? true : false;
			//boolean isPoeStartStatus = poeStartStatus.equals("1") ? true : false;
			//boolean isPoeEndStatus = poeEndStatus.equals("1") ? true : false;
			network.setPoeSchedul(isPoeManual, switchid, poeStartTime, poeStartStatus, poeEndTime, poeEndStatus);
			
		} else if (action.equals("poe_add")) {
			String schedule_name = request.getParameter("schedule_name");
			String poeStartTime = request.getParameter("poe_StartTime");
			String poeEndTime = request.getParameter("poe_EndTime");
			//System.out.println("===="+"\n"+schedule_name+"\n"+poeStartTime+"\n"+poeEndTime);
			
			String insert_complete = network.addPoeSchedule(schedule_name, poeStartTime, poeEndTime);
			
			
			logger.info("Add New POE Schedule: " + schedule_name + ". User: " + accountUser);
			
	        try {
				response.getWriter().write(insert_complete);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	        
		} else if (action.equals("poe_view")) {
			String[][] view_poe_complete = network.viewPoeSchedule();
		    ArrayList<String[]> list = new ArrayList<String[]>();

		    if (view_poe_complete != null) {
		        for(int i=0;i<view_poe_complete.length;i++){
		            list.add(view_poe_complete[i]);
		            //System.out.println(Arrays.toString(list.get(list.size()-1)));
		        }
		    }
		    
		    String json_data = new Gson().toJson(list);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    try {
		        response.getWriter().write(json_data);
		    } catch (IOException e) {
		        System.out.println(e.getMessage());
		    }
		    
		} else if (action.equals("poe_delete")) {
			System.out.println(Arrays.toString(request.getParameterValues("checkdelete[]")));
			network.removePoeScheduleItems(request.getParameterValues("checkdelete[]"));
			String[] deleteArray = request.getParameterValues("checkdelete[]");
			
			for(int i=0;i<deleteArray.length;i++){
				logger.info("Delete POE Schedule: " + deleteArray[i] + ". User: " + accountUser);
			}

		} else if (action.equals("profile_add")) {
			String profile_name = request.getParameter("profile_name");
			String profileStartTime = request.getParameter("profile_StartTime");
			String profileEndTime = request.getParameter("profile_EndTime");
			//System.out.println("===="+"\n"+schedule_name+"\n"+poeStartTime+"\n"+poeEndTime);
			
			String insert_complete = network.addProfile(profile_name, profileStartTime, profileEndTime);
			
			if(!accountUser.equalsIgnoreCase("super")){
				logger.info("Add New Profile Schedule: " + profile_name + ". User: " + accountUser);
			}
			
	        try {
				response.getWriter().write(insert_complete);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	        
		} else if (action.equals("profile_view")) {
			String[][] result = network.viewProfile();
		    ArrayList<String[]> list = new ArrayList<String[]>();

		    if (result != null) {
		        for(int i=0;i<result.length;i++){
		            list.add(result[i]);
		            //System.out.println(Arrays.toString(list.get(list.size()-1)));
		        }
		    } else{
		    	System.out.println("Profile table is null.");
		    }
		    
		    String json_data = new Gson().toJson(list);
		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    try {
		        response.getWriter().write(json_data);
		    } catch (IOException e) {
		        System.out.println(e.getMessage());
		    }
		    
		} else if (action.equals("profile_delete")) {
			System.out.println(Arrays.toString(request.getParameterValues("checkdelete[]")));
			network.removeProfileItems(request.getParameterValues("checkdelete[]"));
			String[] deleteArray = request.getParameterValues("checkdelete[]");
			
			for(int i=0;i<deleteArray.length;i++){
				if(!accountUser.equalsIgnoreCase("super")){
					logger.info("Delete Profile Schedule: " + deleteArray[i] + ". User: " + accountUser);
				}
			}
			
		} else if (action.equals("get_interfaces")) {
			System.out.println("action：get_interfaces");
			List<String> interface_info = new Vector<String>();
			String line = "";

			for (int i = 0; i < 12; i++) {
				interface_info.add("");
			}

			if (JSystem.osName.equalsIgnoreCase("Linux")) {
				try {
					// eth0
				    String[] cmd_eth0_mac = { "/bin/sh", "-c", "/sbin/ifconfig eth0 2> /dev/null | /bin/grep 'HWaddr' | /usr/bin/awk '{ print $5}'" };
					Process Process_eth0_mac = Runtime.getRuntime().exec(cmd_eth0_mac);
					BufferedReader BufferedReader_eth0_mac = new BufferedReader(new InputStreamReader(Process_eth0_mac.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_eth0_mac.readLine()) != null) {
						interface_info.set(0, line);
					}
					BufferedReader_eth0_mac.close();
					Process_eth0_mac.waitFor();

					String[] cmd_eth0_type = {"", "", ""};
					if (JSystem.osDistribution.equals("Ubuntu")) {
					    cmd_eth0_type = new String[]{ "/bin/sh", "-c", "sed -n '5p' /etc/network/interfaces | /usr/bin/awk '{ print $4}'" };
					}
					else if (JSystem.osDistribution.equals("CentOS")) {
					    cmd_eth0_type = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"BOOTPROTO\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
					}
					Process Process_eth0_type = Runtime.getRuntime().exec(cmd_eth0_type);
					BufferedReader BufferedReader_eth0_type = new BufferedReader(new InputStreamReader(Process_eth0_type.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_eth0_type.readLine()) != null) {
						interface_info.set(1, line);
					}
					BufferedReader_eth0_type.close();
					Process_eth0_type.waitFor();

					if (interface_info.get(1).equals("dhcp")) {
						String[] cmd_eth0_ip = { "/bin/sh", "-c", "/sbin/ifconfig eth0 2> /dev/null | /bin/grep 'inet addr:' | /usr/bin/cut -d ':' -f2 | /usr/bin/awk '{ print $1}'" };
						Process Process_eth0_ip = Runtime.getRuntime().exec(cmd_eth0_ip);
						BufferedReader BufferedReader_eth0_ip = new BufferedReader(new InputStreamReader(Process_eth0_ip.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_ip.readLine()) != null) {
							interface_info.set(2, line);
						}
						BufferedReader_eth0_ip.close();
						Process_eth0_ip.waitFor();

						String[] cmd_eth0_netmask = { "/bin/sh", "-c", "/sbin/ifconfig eth0 2> /dev/null | /bin/grep 'inet addr:' | /usr/bin/cut -d ':' -f4" };
						Process Process_eth0_netmask = Runtime.getRuntime().exec(cmd_eth0_netmask);
						BufferedReader BufferedReader_eth0_netmask = new BufferedReader(new InputStreamReader(Process_eth0_netmask.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_netmask.readLine()) != null) {
							interface_info.set(3, line);
						}
						BufferedReader_eth0_netmask.close();
						Process_eth0_netmask.waitFor();

						String[] cmd_eth0_gateway = { "/bin/sh", "-c", "/sbin/route -n | /bin/grep 'UG[ \t]' | /bin/grep 'eth0' | /usr/bin/awk '{print $2}'" };
						Process Process_eth0_gateway = Runtime.getRuntime().exec(cmd_eth0_gateway);
						BufferedReader BufferedReader_eth0_gateway = new BufferedReader(new InputStreamReader(Process_eth0_gateway.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_gateway.readLine()) != null) {
							interface_info.set(4, line);
						}
						BufferedReader_eth0_gateway.close();
						Process_eth0_gateway.waitFor();

						if (JSystem.osDistribution.equals("Ubuntu")) {
						    String[] cmd_eth0_dns1 = { "/bin/sh", "-c", "/bin/sed -n '3p' /etc/resolv.conf  | /usr/bin/awk '{ print $2}'" };
						    Process Process_eth0_dns1 = Runtime.getRuntime().exec(cmd_eth0_dns1);
						    BufferedReader BufferedReader_eth0_dns1 = new BufferedReader(new InputStreamReader(Process_eth0_dns1.getInputStream(), "UTF-8"));
						    while ((line = BufferedReader_eth0_dns1.readLine()) != null) {
						        interface_info.set(5, line);
						    }
						    BufferedReader_eth0_dns1.close();
						    Process_eth0_dns1.waitFor();

						    String[] cmd_eth0_dns2 = { "/bin/sh", "-c", "/bin/sed -n '4p' /etc/resolv.conf  | /usr/bin/awk '{ print $2}'" };
						    Process Process_eth0_dns2 = Runtime.getRuntime().exec(cmd_eth0_dns2);
						    BufferedReader BufferedReader_eth0_dns2 = new BufferedReader(new InputStreamReader(Process_eth0_dns2.getInputStream(), "UTF-8"));
						    while ((line = BufferedReader_eth0_dns2.readLine()) != null) {
						        interface_info.set(6, line);
						    }
						    BufferedReader_eth0_dns2.close();
						    Process_eth0_dns2.waitFor();

						    String[] cmd_eth0_dns3 = { "/bin/sh", "-c", "/bin/sed -n '5p' /etc/resolv.conf  | /usr/bin/awk '{ print $2}'" };
						    Process Process_eth0_dns3 = Runtime.getRuntime().exec(cmd_eth0_dns3);
						    BufferedReader BufferedReader_eth0_dns3 = new BufferedReader(new InputStreamReader(Process_eth0_dns3.getInputStream(), "UTF-8"));
						    while ((line = BufferedReader_eth0_dns3.readLine()) != null) {
						        interface_info.set(7, line);
						    }
						    BufferedReader_eth0_dns3.close();
						    Process_eth0_dns3.waitFor();
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
						    String[] cmd_eth0_dns = { "/bin/sh", "-c", "/usr/bin/awk '$1==\"nameserver\" { print $2 }' /etc/resolv.conf" };
						    Process Process_eth0_dns = Runtime.getRuntime().exec(cmd_eth0_dns);
						    BufferedReader BufferedReader_eth0_dns = new BufferedReader(new InputStreamReader(Process_eth0_dns.getInputStream(), "UTF-8"));
                            int lineNum = 5;
						    while ((line = BufferedReader_eth0_dns.readLine()) != null) {
                                interface_info.set(lineNum++, line);
                            }
                            BufferedReader_eth0_dns.close();
                            Process_eth0_dns.waitFor();
						}
					} else if (interface_info.get(1).equals("static")) {
					    String[] cmd_eth0_ip = {"", "", ""};
					    if (JSystem.osDistribution.equals("Ubuntu")) {
					        cmd_eth0_ip = new String[]{ "/bin/sh", "-c", "sed -n '6p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
					    }
					    else if (JSystem.osDistribution.equals("CentOS")) {
					        cmd_eth0_ip = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"IPADDR\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
					    }
						Process Process_eth0_ip = Runtime.getRuntime().exec(cmd_eth0_ip);
						BufferedReader BufferedReader_eth0_ip = new BufferedReader(new InputStreamReader(Process_eth0_ip.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_ip.readLine()) != null) {
							interface_info.set(2, line);
						}
						BufferedReader_eth0_ip.close();
						Process_eth0_ip.waitFor();

						String[] cmd_eth0_netmask = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth0_netmask = new String[]{ "/bin/sh", "-c", "sed -n '7p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
						    cmd_eth0_netmask = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"NETMASK\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
                        }
						Process Process_eth0_netmask = Runtime.getRuntime().exec(cmd_eth0_netmask);
						BufferedReader BufferedReader_eth0_netmask = new BufferedReader(new InputStreamReader(Process_eth0_netmask.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_netmask.readLine()) != null) {
							interface_info.set(3, line);
						}
						BufferedReader_eth0_netmask.close();
						Process_eth0_netmask.waitFor();

						String[] cmd_eth0_gateway = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth0_gateway = new String[]{ "/bin/sh", "-c", "sed -n '8p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
						    cmd_eth0_gateway = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"GATEWAY\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
                        }
						Process Process_eth0_gateway = Runtime.getRuntime().exec(cmd_eth0_gateway);
						BufferedReader BufferedReader_eth0_gateway = new BufferedReader(new InputStreamReader(Process_eth0_gateway.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_gateway.readLine()) != null) {
							interface_info.set(4, line);
						}
						BufferedReader_eth0_gateway.close();
						Process_eth0_gateway.waitFor();

						String[] cmd_eth0_dns1 = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth0_dns1 = new String[]{ "/bin/sh", "-c", "sed -n '9p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
						    cmd_eth0_dns1 = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"DNS1\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
						}
						Process Process_eth0_dns1 = Runtime.getRuntime().exec(cmd_eth0_dns1);
						BufferedReader BufferedReader_eth0_dns1 = new BufferedReader(new InputStreamReader(Process_eth0_dns1.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_dns1.readLine()) != null) {
							interface_info.set(5, line);
						}
						BufferedReader_eth0_dns1.close();
						Process_eth0_dns1.waitFor();

						String[] cmd_eth0_dns2 = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth0_dns2 = new String[]{ "/bin/sh", "-c", "sed -n '9p' /etc/network/interfaces | /usr/bin/awk '{ print $3}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
                            cmd_eth0_dns2 = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"DNS2\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
                        }
						Process Process_eth0_dns2 = Runtime.getRuntime().exec(cmd_eth0_dns2);
						BufferedReader BufferedReader_eth0_dns2 = new BufferedReader(new InputStreamReader(Process_eth0_dns2.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_dns2.readLine()) != null) {
							interface_info.set(6, line);
						}
						BufferedReader_eth0_dns2.close();
						Process_eth0_dns2.waitFor();

						String[] cmd_eth0_dns3 = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth0_dns3 = new String[]{ "/bin/sh", "-c", "sed -n '9p' /etc/network/interfaces | /usr/bin/awk '{ print $4}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
                            cmd_eth0_dns3 = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"DNS3\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth0" };
                        }
						Process Process_eth0_dns3 = Runtime.getRuntime().exec(cmd_eth0_dns3);
						BufferedReader BufferedReader_eth0_dns3 = new BufferedReader(new InputStreamReader(Process_eth0_dns3.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth0_dns3.readLine()) != null) {
							interface_info.set(7, line);
						}
						BufferedReader_eth0_dns3.close();
						Process_eth0_dns3.waitFor();
					}

					// eth1
					String[] cmd_eth1_mac = { "/bin/sh", "-c", "/sbin/ifconfig eth1 2> /dev/null | /bin/grep 'HWaddr' | /usr/bin/awk '{ print $5}'" };
					Process Process_eth1_mac = Runtime.getRuntime().exec(cmd_eth1_mac);
					BufferedReader BufferedReader_eth1_mac = new BufferedReader(new InputStreamReader(Process_eth1_mac.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_eth1_mac.readLine()) != null) {
						interface_info.set(8, line);
					}
					BufferedReader_eth1_mac.close();
					Process_eth1_mac.waitFor();

					String[] cmd_eth1_type = {"", "", ""};
					if (JSystem.osDistribution.equals("Ubuntu")) {
					    cmd_eth1_type = new String[]{ "/bin/sh", "-c", "sed -n '12p' /etc/network/interfaces | /usr/bin/awk '{ print $4}'" };
					}
					else if (JSystem.osDistribution.equals("CentOS")) {
                        cmd_eth1_type = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"BOOTPROTO\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth1" };
                    }
					Process Process_eth1_type = Runtime.getRuntime().exec(cmd_eth1_type);
					BufferedReader BufferedReader_eth1_type = new BufferedReader(new InputStreamReader(Process_eth1_type.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_eth1_type.readLine()) != null) {
						interface_info.set(9, line);
					}
					BufferedReader_eth1_type.close();
					Process_eth1_type.waitFor();

					if (interface_info.get(9).equals("dhcp")) {
						String[] cmd_eth1_ip = { "/bin/sh", "-c", "/sbin/ifconfig eth1 2> /dev/null | /bin/grep 'inet addr:' | /usr/bin/cut -d ':' -f2 | /usr/bin/awk '{ print $1}'" };
						Process Process_eth1_ip = Runtime.getRuntime().exec(cmd_eth1_ip);
						BufferedReader BufferedReader_eth1_ip = new BufferedReader(new InputStreamReader(Process_eth1_ip.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth1_ip.readLine()) != null) {
							interface_info.set(10, line);
						}
						BufferedReader_eth1_ip.close();
						Process_eth1_ip.waitFor();

						String[] cmd_eth1_netmask = { "/bin/sh", "-c", "/sbin/ifconfig eth1 2> /dev/null | /bin/grep 'inet addr:' | /usr/bin/cut -d ':' -f4" };
						Process Process_eth1_netmask = Runtime.getRuntime().exec(cmd_eth1_netmask);
						BufferedReader BufferedReader_eth1_netmask = new BufferedReader(new InputStreamReader(Process_eth1_netmask.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth1_netmask.readLine()) != null) {
							interface_info.set(11, line);
						}
						BufferedReader_eth1_netmask.close();
						Process_eth1_netmask.waitFor();
					} else if (interface_info.get(9).equals("static")) {
					    String[] cmd_eth1_ip = {"", "", ""};
					    if (JSystem.osDistribution.equals("Ubuntu")) {
					        cmd_eth1_ip = new String[]{ "/bin/sh", "-c", "sed -n '13p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
					    }
					    else if (JSystem.osDistribution.equals("CentOS")) {
					        cmd_eth1_ip = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"IPADDR\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth1" };
					    }
						Process Process_eth1_ip = Runtime.getRuntime().exec(cmd_eth1_ip);
						BufferedReader BufferedReader_eth1_ip = new BufferedReader(new InputStreamReader(Process_eth1_ip.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth1_ip.readLine()) != null) {
							interface_info.set(10, line);
						}
						BufferedReader_eth1_ip.close();
						Process_eth1_ip.waitFor();

						String[] cmd_eth1_netmask = {"", "", ""};
						if (JSystem.osDistribution.equals("Ubuntu")) {
						    cmd_eth1_netmask = new String[]{ "/bin/sh", "-c", "sed -n '14p' /etc/network/interfaces | /usr/bin/awk '{ print $2}'" };
						}
						else if (JSystem.osDistribution.equals("CentOS")) {
						    cmd_eth1_netmask = new String[]{ "/bin/sh", "-c", "/usr/bin/awk 'BEGIN {FS=\"=\"} $1==\"NETMASK\" { print $2 }' /etc/sysconfig/network-scripts/ifcfg-eth1" };
						}
						Process Process_eth1_netmask = Runtime.getRuntime().exec(cmd_eth1_netmask);
						BufferedReader BufferedReader_eth1_netmask = new BufferedReader(new InputStreamReader(Process_eth1_netmask.getInputStream(), "UTF-8"));
						while ((line = BufferedReader_eth1_netmask.readLine()) != null) {
							interface_info.set(11, line);
						}
						BufferedReader_eth1_netmask.close();
						Process_eth1_netmask.waitFor();
					}
				} catch (Exception e) {
					System.out.println("發生 IOException");
					e.printStackTrace();
				}
			}
			request.setAttribute("data", interface_info);
		} else if (action.equals("set_interfaces")) {
			System.out.println("action = set_interfaces");
			String line = "";

			if (JSystem.osDistribution.equals("Ubuntu")) {
			    String eth0_type = request.getParameter("eth0_type");
			    String eth0_ip = "";
			    String eth0_netmask = "";
			    String eth0_gateway = "";
			    String eth0_dns1 = "";
			    String eth0_dns2 = "";
			    String eth0_dns3 = "";
			    String eth0_dns = "";
			    if (eth0_type.equals("dhcp")) {
			        eth0_ip = "\\\'#\\\'";
			        eth0_netmask = "\\\'#\\\'";
			        eth0_gateway = "\\\'#\\\'";
			        eth0_dns = "\\\'#\\\'";
			    } else if (eth0_type.equals("static")) {
			        eth0_ip = "address " + request.getParameter("eth0_ip");
			        eth0_netmask = "netmask " + request.getParameter("eth0_netmask");
			        eth0_gateway = "gateway " + request.getParameter("eth0_gateway");
			        eth0_dns1 = request.getParameter("eth0_dns1");
			        eth0_dns2 = request.getParameter("eth0_dns2");
			        eth0_dns3 = request.getParameter("eth0_dns3");

			        if (eth0_dns1.equals("") && eth0_dns2.equals("") && eth0_dns3.equals("")) {
			            eth0_dns = "\\\'#\\\'";
			        } else {
			            eth0_dns = "dns-nameservers " + eth0_dns1 + " " + eth0_dns2 + " " + eth0_dns3;
			        }
			    }

			    String eth1_type = request.getParameter("eth1_type");
			    String eth1_ip = "";
			    String eth1_netmask = "";
			    if (eth1_type.equals("dhcp")) {
			        eth1_ip = "\\\'#\\\'";
			        eth1_netmask = "\\\'#\\\'";
			    } else if (eth1_type.equals("static")) {
			        eth1_ip = "address " + request.getParameter("eth1_ip");
			        eth1_netmask = "netmask " + request.getParameter("eth1_netmask");
			    }

			    try {
			        String sed1 = "/bin/sed -i \"7c send \\\"echo auto lo > /etc/network/interfaces;" +
			                "echo iface lo inet loopback >> /etc/network/interfaces;" +
			                "echo >> /etc/network/interfaces;" +
			                "echo auto eth0 >> /etc/network/interfaces;" +
			                "echo iface eth0 inet " + eth0_type + " >> /etc/network/interfaces;" +
			                "echo " + eth0_ip + " >> /etc/network/interfaces;" +
			                "echo " + eth0_netmask + " >> /etc/network/interfaces;" +
			                "echo " + eth0_gateway + " >> /etc/network/interfaces;" +
			                "echo " + eth0_dns + " >> /etc/network/interfaces;" +
			                "echo >> /etc/network/interfaces;" +
			                "echo auto eth1 >> /etc/network/interfaces;" +
			                "echo iface eth1 inet " + eth1_type + " >> /etc/network/interfaces;" +
			                "echo " + eth1_ip + " >> /etc/network/interfaces;" +
			                "echo " + eth1_netmask + " >> /etc/network/interfaces\\\\\\r\\\"\" /etc/tomcat7/set_interfaces_script";
			        String[] cmd_set_interfaces = { "/bin/sh", "-c", sed1 };
			        //System.out.println(cmd_set_interfaces[2]);
			        Process Process_set_interfaces = Runtime.getRuntime().exec(cmd_set_interfaces);
			        BufferedReader BufferedReader_set_interfaces = new BufferedReader(new InputStreamReader(Process_set_interfaces.getInputStream(), "UTF-8"));
			        while ((line = BufferedReader_set_interfaces.readLine()) != null) {
			        }
			        BufferedReader_set_interfaces.close();
			        Process_set_interfaces.waitFor();

			        String[] cmd_print = { "/bin/sh", "-c", "/bin/sed -n '7p' /etc/tomcat7/set_interfaces_script" };
			        Process Process_print = Runtime.getRuntime().exec(cmd_print);
			        BufferedReader BufferedReader_print = new BufferedReader(new InputStreamReader(Process_print.getInputStream(), "UTF-8"));
			        while ((line = BufferedReader_print.readLine()) != null) {
			        }
			        BufferedReader_print.close();
			        Process_print.waitFor();

			        String[] cmd_restart_interfaces = { "/bin/sh", "-c", "/etc/tomcat7/set_interfaces_script" };
			        Process Process_restart_interfaces = Runtime.getRuntime().exec(cmd_restart_interfaces);
			        BufferedReader BufferedReader_restart_interfaces = new BufferedReader(new InputStreamReader(Process_restart_interfaces.getInputStream(), "UTF-8"));
			        while ((line = BufferedReader_restart_interfaces.readLine()) != null) {
			        }
			        BufferedReader_restart_interfaces.close();
			        Process_restart_interfaces.waitFor();

			        //System.out.println(Process_set_interfaces.exitValue());
			        logger.info("Set Network Interface Success. User: " + accountUser);
			    } catch (Exception e) {
			        System.out.println("發生 IOException");
			        e.printStackTrace();
			    }
			}
			else if (JSystem.osDistribution.equals("CentOS")) {
			    // eth0
			    String eth0Cfg = "/etc/sysconfig/network-scripts/ifcfg-eth0";
			    boolean isEth0Static = request.getParameter("eth0_type").equals("static");
			    String eth0BootProto = "";
                String eth0IpAddr = "";
                String eth0Netmask = "";
                String eth0Gateway = "";
                String eth0Dns1 = "";
                String eth0Dns2 = "";
                if (isEth0Static) {
                    eth0BootProto = "BOOTPROTO=static";
                    eth0IpAddr = "IPADDR=" + request.getParameter("eth0_ip");
                    eth0Netmask = "NETMASK=" + request.getParameter("eth0_netmask");
                    eth0Gateway = "GATEWAY=" + request.getParameter("eth0_gateway");
                }
                else {
                    eth0BootProto = "BOOTPROTO=dhcp";
                }
                eth0Dns1 = "DNS1=" + request.getParameter("eth0_dns1");
                eth0Dns2 = "DNS2=" + request.getParameter("eth0_dns2");
                
                // eth1
                String eth1Cfg = "/etc/sysconfig/network-scripts/ifcfg-eth1";
                boolean isEth1Static = request.getParameter("eth1_type").equals("static");
                String eth1BootProto = "";
                String eth1IpAddr = "";
                String eth1Netmask = "";
                if (isEth1Static) {
                    eth1BootProto = "BOOTPROTO=static";
                    eth1IpAddr = "IPADDR=" + request.getParameter("eth1_ip");
                    eth1Netmask = "NETMASK=" + request.getParameter("eth1_netmask");
                }
                else {
                    eth1BootProto = "BOOTPROTO=dhcp";
                }
                
                String setInterfaceScript = JSystem.projectSpace + "/set_interfaces_script";
                String sedEth0 = "/bin/sed -i '2c " +
                        "echo DEVICE=eth0 > " + eth0Cfg + ";" +
                        "echo TYPE=Ethernet >> " + eth0Cfg + ";" +
                        "echo ARPCHECK=no >> " + eth0Cfg + ";" +
                        "echo NM_CONTROLLED=no >> " + eth0Cfg + ";" +
                        "echo ONBOOT=yes >> " + eth0Cfg + ";" +
                        "echo " + eth0BootProto + " >> " + eth0Cfg + ";" +
                        "echo " + eth0IpAddr + " >> " + eth0Cfg + ";" +
                        "echo " + eth0Netmask + " >> " + eth0Cfg + ";" +
                        "echo " + eth0Gateway + " >> " + eth0Cfg + ";" +
                        "echo " + eth0Dns1 + " >> " + eth0Cfg + ";" +
                        "echo " + eth0Dns2 + " >> " + eth0Cfg + ";" +
                        "echo USERCTL=no >> " + eth0Cfg + ";" +
                        "echo IPV6INIT=no >> " + eth0Cfg + ";" +
                        (isEth0Static ? "" : "echo PEERDNS=no >> " + eth0Cfg + ";") +
                        "' " + setInterfaceScript;
                String sedEth1 = "/bin/sed -i '3c " +
                        "echo DEVICE=eth1 > " + eth1Cfg + ";" +
                        "echo TYPE=Ethernet >> " + eth1Cfg + ";" +
                        "echo ARPCHECK=no >> " + eth1Cfg + ";" +
                        "echo NM_CONTROLLED=no >> " + eth1Cfg + ";" +
                        "echo ONBOOT=yes >> " + eth1Cfg + ";" +
                        "echo " + eth1BootProto + " >> " + eth1Cfg + ";" +
                        "echo " + eth1IpAddr + " >> " + eth1Cfg + ";" +
                        "echo " + eth1Netmask + " >> " + eth1Cfg + ";" +
                        "echo USERCTL=no >> " + eth1Cfg + ";" +
                        "echo IPV6INIT=no >> " + eth1Cfg + ";" +
                        (isEth1Static ? "" : "echo PEERDNS=no >> " + eth1Cfg + ";") +   // PEERDNS=no means it will not allow using DHCP provided DNS.
                        "' " + setInterfaceScript;

                String result = "";
                try {
                    String[] setInterfacesScriptEth0 = { "/bin/sh", "-c", sedEth0 };
                    Process setInterfacesScriptProcessEth0 = Runtime.getRuntime().exec(setInterfacesScriptEth0);
                    setInterfacesScriptProcessEth0.waitFor();
                    
                    String[] setInterfacesScriptEth1 = { "/bin/sh", "-c", sedEth1 };
                    Process setInterfacesScriptProcessEth1 = Runtime.getRuntime().exec(setInterfacesScriptEth1);
                    setInterfacesScriptProcessEth1.waitFor();
                    
                    String[] runInterfaceScript = { "/bin/sh", "-c", setInterfaceScript };
                    Process runInterfacesScriptProcess = Runtime.getRuntime().exec(runInterfaceScript);
                    BufferedReader setInterfacesBuffReaderResult = new BufferedReader(new InputStreamReader(runInterfacesScriptProcess.getInputStream(), "UTF-8"));
                    while ((line = setInterfacesBuffReaderResult.readLine()) != null) {
                        System.out.println(line);
                    }
                    setInterfacesBuffReaderResult.close();
                    runInterfacesScriptProcess.waitFor();

                    result = "Success!";
                    logger.info("Set Network Interface Success. User: " + accountUser);
                }
                catch (IOException | InterruptedException e) {
                    System.out.println("Fail to process set_interface_script. msg:" + e.getMessage());
                    result = "Fail!";
                }
                
                try {
                    response.getWriter().write(result);
                }
                catch (IOException e) {
                }
			}
		}
		else if (action.equals("web_update")) {
		    Map<String, Object> map = updateWebApp(request);
		    
		    if ((Boolean)map.get("done")) {
		        logger.info("Web Updated. User: " + accountUser);
		        
		        if (JAlarm.getAlarmInfo()[14].equals("1") && !JAlarm.getWebUpdatedSendEmailList().isEmpty()) {    //WebUpdatedSendEmail
		        	String result = JNetwork.setAlarmEmailStr(JAlarm.getWebUpdatedSendEmailList());
		        	JAlarm.doEmailSend(sdf.format(new Date()) + " Web Updated. User: " + accountUser, result);
		        }
		        if (JAlarm.getAlarmInfo()[15].equals("1") && !JAlarm.getWebUpdatedSendSmsList().isEmpty()) {    //WebUpdatedSendSms
		        	String result = JNetwork.setAlarmSmsStr(JAlarm.getWebUpdatedSendSmsList());
		            JAlarm.doSMSSend(sdf.format(new Date()) + " VIA CyberExpert SMS Message: " + "Web Updated. User: " + accountUser, result);
		        }
		    }
		    
		    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    list.add(map);
		    Map<String, List<Map<String, Object>>> jsonSource = new LinkedHashMap<String, List<Map<String, Object>>>();
		    jsonSource.put("files", list);
		    
		    sendOut(action, response, jsonSource, true);
		}
		else if (action.equals("get_routing_table")) {
		    String result = "";
            try {
                result = getRoutingTable();
            }
            catch (Exception e) {
                result = e.getMessage();
            }
		    
            sendOut(action, response, result, false);
		}
		else if (action.equals("get_route_setting")) {
		    Map<String, String> routeMap = null;
		    String text = "";
		    boolean isDone = true;
		    try {
		        routeMap = getRouteSetting();
            }
            catch (Exception e) {
                isDone = false;
                text = e.getMessage();
            }
		    
		    Map<String, Object> map = new LinkedHashMap<String, Object>();
		    map.put("done", new Boolean(isDone));
		    map.put("interfaces", routeMap);
		    map.put("text", text);
		    sendOut(action, response, map, true);
		}
		else if (action.equals("set_route_setting")) {
		    Map<String, String> ifSettings = new LinkedHashMap<String, String>();
            ifSettings.put("eth0", (String) request.getParameter("eth0"));
            ifSettings.put("eth1", (String) request.getParameter("eth1"));
		    String text = "";
		    boolean isDone = true;
		    
		    try {
                setRouteSetting(ifSettings);
            }
            catch (Exception e) {
                isDone = false;
                text = e.getMessage();
            }
		    
		    Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("done", new Boolean(isDone));
            map.put("text", text);
            sendOut(action, response, map, true);
		}
		else if (action.equals("get_ifconfig")) {
            String result = "";
            try {
                result = getIfConfig();
            }
            catch (Exception e) {
                result = e.getMessage();
            }
            
            sendOut(action, response, result, false);
        }
		else if (action.equals("get_interface_setting")) {
            Map<String, String> interfaceMap = null;
            String text = "";
            boolean isDone = true;
            try {
                interfaceMap = getInterfaceSetting();
            }
            catch (Exception e) {
                isDone = false;
                text = e.getMessage();
            }
            
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("done", new Boolean(isDone));
            map.put("interfaces", interfaceMap);
            map.put("text", text);
            sendOut(action, response, map, true);
        }
		else if (action.equals("set_interface_setting")) {
            Map<String, String> ifSettings = new LinkedHashMap<String, String>();
            ifSettings.put("eth0", (String) request.getParameter("eth0"));
            ifSettings.put("eth1", (String) request.getParameter("eth1"));
            String text = "";
            boolean isDone = true;
            
            try {
                setInterfaceSetting(ifSettings);
            }
            catch (Exception e) {
                isDone = false;
                text = e.getMessage();
            }
            
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("done", new Boolean(isDone));
            map.put("text", text);
            sendOut(action, response, map, true);
        }
		else if (action.equals("restart_network")) {
		    String result = restartNetwork();
		    sendOut(action, response, result, false);
		}
		else if (action.equals("remote_service_get")) {
			System.out.println("action = remote_service_get");
			
			RemoteServiceConfig rmiConfig = Config.getRemoteServiceConfig();
			request.setAttribute("remote_service_switch", rmiConfig.isEnable());
			request.setAttribute("remote_service_address", rmiConfig.getLocalAddress());
			/*request.setAttribute("remote_service_registry_port", JSetting.rmiRegistryPort);
			request.setAttribute("remote_service_data_port", JSetting.rmiDataPort);*/
			request.setAttribute("remote_server_address", rmiConfig.getServerAddress());
			/*request.setAttribute("remote_server_port", JSetting.rmiRemoteServerPort);*/
		}
		else if (action.equals("remote_service_set")) {
			System.out.println("action = remote_service_set");

			if (network.getRemoteServiceisRunning() == true) {
				System.out.println("Remote Service is Running = true");
				try {
					response.getWriter().write("true");
				} catch (IOException e) {
					System.out.println("response.getWriter().write(\"true\");\nException：" + e.getMessage());
					e.printStackTrace();
				}
			} else {

				String tmp = "";
				RemoteServiceConfig rmiConfig = new RemoteServiceConfig();

				String remote_service_switch = request.getParameter("remote_service_switch");
				if (remote_service_switch.equals("true")) {
					rmiConfig.setEnable(true);
					tmp = tmp + "Remote Service : Enable";
				} else {
					rmiConfig.setEnable(false);
					tmp = tmp + "Remote Service : Disable";
				}

				String remote_service_address = request.getParameter("remote_service_address");
				rmiConfig.setLocalAddress(remote_service_address);
				tmp = tmp + "\nRemote Service Address : " + remote_service_address;

				/*String remote_service_registry_port = request.getParameter("remote_service_registry_port");
				JSetting.setRmiRegistryPort(remote_service_registry_port);
				tmp = tmp + "\nRemote Service Registry Port : " + remote_service_registry_port;

				String remote_service_data_port = request.getParameter("remote_service_data_port");
				JSetting.setRmiDataPort(remote_service_data_port);
				tmp = tmp + "\nRemote Service Data Port : " + remote_service_data_port;*/

				String remote_server_address = request.getParameter("remote_server_address");
				rmiConfig.setServerAddress(remote_server_address);
				tmp = tmp + "\nRemote Server Address : " + remote_server_address;

				/*String remote_server_port = request.getParameter("remote_server_port");
				JSetting.setRmiRemoteServerPort(remote_server_port);
				tmp = tmp + "\nRemote Server Port : " + remote_server_port;*/

				Config.setRemoteServiceConfig(rmiConfig);
				network.initRemoteService();

				try {
					response.getWriter().write(tmp);
				} catch (IOException e) {
					System.out.println("response.getWriter().write(tmp);\nException：" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		else if (action.equals("location_address_get")) {
			System.out.println("action = location_address_get");
			
			request.setAttribute("address", Config.getLocationAddress());
		}
		else if (action.equals("location_address_set")) {
			System.out.println("action = location_address_set");
			
				String address = request.getParameter("address");
				
				Config.setLocationAddress(address);

				try {
					response.getWriter().write("ok");
				} catch (IOException e) {
					System.out.println("response.getWriter().write(tmp);\nException：" + e.getMessage());
					e.printStackTrace();
				}
		}
		else if (action.equals("settings_export")) {
			System.out.println("action = " + action);
			
			boolean result = Config.exportToFile();
			if (result) {
				System.out.println("Export settings to " + Config.getExportFileAbsolutePath());
			}
			else {
				System.out.println("Export settings FAILED.");
			}
			
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("isDone", result);
			map.put("file", Config.getExportFileRelativePath());
			
			sendOut(action, response, map, true);
		}
		else if (action.equals("settings_import")) {
			System.out.println("action = " + action);
			
			Map<String, Object> result = importSettings(request);
			
			if ((Boolean)result.get("isDone")) {
				logger.info("Settings has imported successfully. User: " + accountUser);
			}
			
			sendOut(action, response, result, true);
		}
		else {
			System.out.println("Settings: unknown action!");
		}
	}

	/********************************************************************************************************************************************/
	
	private String getFileName(Part part) {
	    String header = part.getHeader("Content-Disposition");
	    String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
	    return fileName;
	}
	
	private File receiveFile(HttpServletRequest request) throws Exception {
	    Part part = null;
	    try {
            part = request.getPart("file");     // uploading process will hang on this statement
            if (part == null) {
                System.out.println("upload file can not be got.");
                throw new Exception("File transfered error!");
            }
        }
        catch (IllegalStateException | IOException e) {
            System.out.println("upload file expcetion when getPart");
            throw new Exception("File transfered error!");
        }
        
        File file = new File(JSystem.projectSpace + File.separator + getFileName(part));
        try {
            part.write(file.getAbsolutePath());
        }
        catch (IOException e) {
            System.out.println("upload file save failed.");
            throw new Exception("File transfered error!");
        }
        System.out.println("Uploaded file name: " + file.getName() + ", size: " + file.length() + ", saved to " + file.getAbsolutePath());
        
        return file;
	}
	
	private Map<String, Object> updateWebApp(HttpServletRequest request) {
	    Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("fileName", "");
        result.put("fileSize", new Long(0));
        result.put("done", new Boolean(false));     // default
        result.put("text", "");
        
        File uploadedFile;
        try {
            uploadedFile = receiveFile(request);
            result.put("fileName", uploadedFile.getName());
            result.put("fileSize", new Long(uploadedFile.length()));
        }
        catch (Exception e) {
            result.put("text", e.getMessage());
            return result;
        }
        File targetFile = new File(JSystem.projectSpace + File.separator + "CyberExpert.war");
	    
	    int unzipResult = JZip.unzip(uploadedFile.getAbsolutePath(), targetFile.getName(), JSystem.projectSpace, "22185452");
        if (unzipResult > 0) {
            System.out.println("extract uploaded file failed.(" + unzipResult);
            result.put("text", "File is not a correct package for CyberExpert!");
            if (uploadedFile.exists()) uploadedFile.delete();
            return result;
        }
        System.out.println(targetFile.getName() + " has been extracted successfully.");
	    
        // copy the web package unzipped from uploaded file into tomcat webapp folder and it will deploy automatically
        String[] cmd = { "/bin/sh", "-c", "" };    
        if (JSystem.osDistribution.equals("Ubuntu")) {
            cmd[2] = JSystem.projectSpace + "/update_web_script";
        }
        else if (JSystem.osDistribution.equals("CentOS")) {
            cmd[2] = String.format("/bin/cp %s %s", targetFile.getAbsolutePath(), "/usr/local/tomcat7/webapps/ROOT.war");    // webapp's name should be ROOT
        }
        else {
            System.out.println("Current OS does not support this function.");       // FOR test purpose, incorrect OS will stop the process after files are uploaded well
            result.put("text", "Current OS does not support this function.");
            return result;
        }

        List<String> cmdResultList = processLinuxCommand(cmd);
        String temp = "";
        if (cmdResultList.size() > 0) temp = cmdResultList.get(0);
        if (cmdResultList.size() > 1) {
            for (int i = 1; i < cmdResultList.size(); i++) temp += "\n" + cmdResultList.get(i);   // add new line flag only if there exists next line
        }
        System.out.println(temp);

        uploadedFile.delete();
        targetFile.delete();
        result.put("done", new Boolean(true));
        result.put("text", "Web has updated sucessfully.\nPlease wait 3 min for system restart.");

        return result;
	}
	
	private Map<String, Object> importSettings(HttpServletRequest request) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("fileName", "");
		result.put("fileSize", new Long(0));
		result.put("isDone", new Boolean(false));
		result.put("text", "");
		
		File uploadedFile;
        try {
            uploadedFile = receiveFile(request);
            result.put("fileName", uploadedFile.getName());
            result.put("fileSize", new Long(uploadedFile.length()));
        }
        catch (Exception e) {
        	result.put("text", "Upload FAILED.");
        	System.out.println("Upload failed. " + e.getMessage());
        	return result;
        }
        
        if (Config.testImportFile(uploadedFile.getAbsolutePath())) {
        	System.out.println("Import settings has tested sucess.");
        	
        	boolean isDone = true;
        	if (uploadedFile.getName().equals(Config.getImportFileName()) == false) {			// should rename to default name for import setting
        		isDone = uploadedFile.renameTo(new File(Config.getImportFileAbsolutePath()));
        		System.out.println("Uploaded file's name changes to " + Config.getImportFileName() + (isDone?" SUCCESS.":" FAILED."));
        	}
        	
        	if (isDone) {
        		result.put("isDone", new Boolean(true));
        		result.put("text", "New settings uploaded success.\nSystem is going to restart.\nPlease wait about 1 min and login again.");
        		
        		if (JSystem.osDistribution.equals("CentOS")) {
        			JTools.CLI("touch -m " + JSystem.getWebappHome() + File.separator + "ROOT.war");
        			System.out.println("Restarting tomcat to apply new settings.");
        		}
        		else {
        			System.out.println("Can not restart tomcat to apply new setting due to WRONG operating system.");
        		}
        	}
        	else {
        		result.put("isDone", new Boolean(false));
        		result.put("text", "Uploaded file can not be set to new settings.");
        		uploadedFile.delete();
        	}
        }
        else {
        	System.out.println("Import settings has tested FAILED.");
        	
        	result.put("isDone", new Boolean(false));
        	result.put("text", "Uploaded file is not valid.");
        	uploadedFile.delete();
        }
        
        return result;
	}
	
	private String getRoutingTable() throws Exception {
	    if (!JSystem.isServer) {
	        throw new Exception("Current OS does not support this function.");
	    }
	    
	    String[] cmd = {"/bin/sh", "-c", ""};
	    if (JSystem.osDistribution.equals("Ubuntu")) {
	        cmd[2] = "route -n";
	    }
	    else if (JSystem.osDistribution.equals("CentOS")) {
	        cmd[2] = "route -n";
	    }
	    
	    List<String> cmdResultList = processLinuxCommand(cmd);
        String result = "";
        if (cmdResultList.size() > 0) result = cmdResultList.get(0);
        if (cmdResultList.size() > 1) {
            for (int i = 1; i < cmdResultList.size(); i++) result += "\n" + cmdResultList.get(i);   // add new line flag only if there exists next line
        }

	    return result;
	}
	
	private Map<String, String> getRouteSetting() throws Exception {
	    if (!JSystem.isServer) {
            throw new Exception("Current OS does not support this function.");
        }
	    
	    Map<String, String> routeMap = new LinkedHashMap<String, String>();   // <eth#, textString>
	    
	    if (JSystem.osDistribution.equals("Ubuntu")) {
	        throw new Exception("Current OS does not support this function.");
	        /*String[] cmd = {"/bin/sh", "-c", "cat /etc/network/interfaces"};       // Ubuntu put the routing setting in network configuration file
	        List<String> cmdResultList = processLinuxCommand(cmd);
	        interfaceList.add(cmdResultList);*/
	    }
	    else if (JSystem.osDistribution.equals("CentOS")) {
	        List<String> ethList = new ArrayList<String>();
	        ethList.add("eth0");
	        ethList.add("eth1");       // TODO: it should get the real eth name
	        
	        for (String eth : ethList) {
	            String routeFilePath = "/etc/sysconfig/network-scripts/route-" + eth;
	            File routeFile = new File(routeFilePath);
	            String text = "";
	            
	            if (routeFile.exists() && !routeFile.isDirectory()) {
	                String[] cmd = {"/bin/sh", "-c", "cat " + routeFilePath};
	                List<String> cmdResultList = processLinuxCommand(cmd);
	                
	                if (cmdResultList.size() > 0) text = cmdResultList.get(0);
                    if (cmdResultList.size() > 1) {
                        for (int i = 1; i < cmdResultList.size(); i++) text += "\n" + cmdResultList.get(i); // add new line flag only if there exists next line
                    }
	                
	                /*Map<String, String> ethMap = new LinkedHashMap<String, String>();
	                for (String line : cmdResultList) {
	                    if (line.startsWith("#")) continue;    // ignore the comment line
	                    String[] lineItems = line.split("=");
	                    if (lineItems == null || lineItems.length != 2) continue;
	                    ethMap.put(lineItems[0], lineItems[1]);
	                }*/
	            }
	            else {
	                text = "Does not exist.";
	            }
	            
	            routeMap.put(eth, text);
	        }
	    }
	    
	    return routeMap;
	}
	
	private boolean setRouteSetting(Map<String, String> routeSettings) throws Exception {
	    if (!JSystem.isServer) {
            throw new Exception("Current OS does not support this function.");
        }
	    if (routeSettings == null) {
	        throw new Exception("Input error.");
	    }
	    if (routeSettings.size() != 2) {
	        throw new Exception("Two interfaces needed.");
	    }
	    
	    if (JSystem.osDistribution.equals("Ubuntu")) {
	        throw new Exception("Current OS does not support this function.");
	        /*String[] cmd = {"/bin/sh", "-c", "echo \"" + interfaceSetting + "\" > '/etc/network/interfaces_bak'"};
            List<String> cmdResultList = processLinuxCommand(cmd);
            System.out.println("result:\n" + cmdResultList);*/
	    }
	    else if (JSystem.osDistribution.equals("CentOS")) {
	        for (Map.Entry<String, String> entry : routeSettings.entrySet()) {
                String ifName = entry.getKey();
                String ifString = entry.getValue();
                String ifFilePath = "/etc/sysconfig/network-scripts/route-" + ifName;
                String[] cmd = {"/bin/sh", "-c", "echo \"" + ifString + "\" > '" + ifFilePath + "'"};
                processLinuxCommand(cmd);
            }
	    }
	    
	    return true;
	}
	
	private String getIfConfig() throws Exception {
        if (!JSystem.isServer) {
            throw new Exception("Current OS does not support this function.");
        }
        
        String[] cmd = {"/bin/sh", "-c", ""};
        if (JSystem.osDistribution.equals("Ubuntu")) {
            cmd[2] = "ifconfig";
        }
        else if (JSystem.osDistribution.equals("CentOS")) {
            cmd[2] = "ifconfig";
        }

        List<String> cmdResultList = processLinuxCommand(cmd);
        String result = "";
        if (cmdResultList.size() > 0) result = cmdResultList.get(0);
        if (cmdResultList.size() > 1) {
            for (int i = 1; i < cmdResultList.size(); i++) result += "\n" + cmdResultList.get(i);   // add new line flag only if there exists next line
        }

        return result;
    }
	
	private Map<String, String> getInterfaceSetting() throws Exception {
        if (!JSystem.isServer) {
            throw new Exception("Current OS does not support this function.");
        }
        
        Map<String, String> interfaceMap = new LinkedHashMap<String, String>();   // <eth#, textString>
        
        if (JSystem.osDistribution.equals("Ubuntu")) {
            throw new Exception("Current OS does not support this function.");
            /*String[] cmd = {"/bin/sh", "-c", "cat /etc/network/interfaces"};       // Ubuntu put the routing setting in network configuration file
            List<String> cmdResultList = processLinuxCommand(cmd);
            interfaceList.add(cmdResultList);*/
        }
        else if (JSystem.osDistribution.equals("CentOS")) {
            List<String> ethList = new ArrayList<String>();
            ethList.add("eth0");
            ethList.add("eth1");       // TODO: it should get the real eth name
            
            for (String eth : ethList) {
                String ifcfgFilePath = "/etc/sysconfig/network-scripts/ifcfg-" + eth;
                File ifcfgFile = new File(ifcfgFilePath);
                String text = "";
                
                if (ifcfgFile.exists() && !ifcfgFile.isDirectory()) {
                    String[] cmd = {"/bin/sh", "-c", "cat " + ifcfgFilePath};
                    List<String> cmdResultList = processLinuxCommand(cmd);
                    
                    if (cmdResultList.size() > 0) text = cmdResultList.get(0);
                    if (cmdResultList.size() > 1) {
                        for (int i = 1; i < cmdResultList.size(); i++) text += "\n" + cmdResultList.get(i); // add new line flag only if there exists next line
                    }
                    
                    /*Map<String, String> ethMap = new LinkedHashMap<String, String>();
                    for (String line : cmdResultList) {
                        if (line.startsWith("#")) continue;    // ignore the comment line
                        String[] lineItems = line.split("=");
                        if (lineItems == null || lineItems.length != 2) continue;
                        ethMap.put(lineItems[0], lineItems[1]);
                    }*/
                }
                else {
                    text = "Does not exist.";
                }
                
                interfaceMap.put(eth, text);
            }
        }
        
        return interfaceMap;
    }
	
	private boolean setInterfaceSetting(Map<String, String> interfaceSetting) throws Exception {
        if (!JSystem.isServer) {
            throw new Exception("Current OS does not support this function.");
        }
        if (interfaceSetting == null) {
            throw new Exception("Input error.");
        }
        if (interfaceSetting.size() != 2) {
            throw new Exception("Two interfaces needed.");
        }
        
        if (JSystem.osDistribution.equals("Ubuntu")) {
            throw new Exception("Current OS does not support this function.");
            /*String[] cmd = {"/bin/sh", "-c", "echo \"" + interfaceSetting + "\" > '/etc/network/interfaces_bak'"};
            List<String> cmdResultList = processLinuxCommand(cmd);
            System.out.println("result:\n" + cmdResultList);*/
        }
        else if (JSystem.osDistribution.equals("CentOS")) {
            for (Map.Entry<String, String> entry : interfaceSetting.entrySet()) {
                String ifName = entry.getKey();
                String ifString = entry.getValue();
                String ifFilePath = "/etc/sysconfig/network-scripts/ifcfg-" + ifName;
                String[] cmd = {"/bin/sh", "-c", "echo \"" + ifString + "\" > '" + ifFilePath + "'"};
                processLinuxCommand(cmd);
            }
        }
        
        return true;
    }
	
	private String restartNetwork() {
	    String text = "";
	    
	    if (JSystem.osDistribution.equals("CentOS")) {                     // currently only CentOS support this
	        String[] cmd = {"/bin/sh", "-c", "service network restart"};
	        List<String> cmdResultList = processLinuxCommand(cmd);
	        
	        if (cmdResultList.size() > 0) text = cmdResultList.get(0);
            if (cmdResultList.size() > 1) {
                for (int i = 1; i < cmdResultList.size(); i++) text += "\n" + cmdResultList.get(i); // add new line flag only if there exists next line
            }
	        
	        System.out.println(text);
	    }
	    else {
	        text = "Current OS does not support this function.";
	    }
	    
	    return text;
	}
	
	private List<String> processLinuxCommand(String[] cmdArray) {
	    Process proc;
        List<String> result = new ArrayList<String>();
        
        try {
            proc = Runtime.getRuntime().exec(cmdArray);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
            proc.waitFor();
        }
        catch (IOException e) {
            System.out.println("processLinuxCommand, exception for IO");
        }
        catch (InterruptedException e) {
            System.out.println("processLinuxCommand, exception for interrupt");
        }
        
        return result;
	}
	
	private void sendOut(String action, HttpServletResponse response, Object obj, boolean isJson) {
	    String output = "";
	    if (isJson) {
	        output = new Gson().toJson(obj);
	        response.setContentType("application/json");
	    }
	    else {
	        output = (String) obj;
	    }
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(output);
        }
        catch (IOException e) {
            System.out.println("action: " + action + " send out failed.");
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
}
