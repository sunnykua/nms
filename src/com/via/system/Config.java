package com.via.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.PropertyConfigurator;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.via.database.DeviceTable;
import com.via.database.TableData;
import com.via.model.AlarmConfig;
import com.via.model.JNetwork;
import com.via.model.JSystem;
import com.via.model.JTools;
import com.via.model.JZip;
import com.via.model.MailConfig;
import com.via.model.RemoteServiceConfig;
import com.via.model.ShortMessageConfig;
import com.via.model.SmtpConfig;

public class Config {
	private static String generalPropertyFile;
	private static String log4jPropertyFile;
	private static String alarmPropertyFile;
	
	private static String exportFileName;
	private static String exportFileRelativePath;
	private static String exportFileAbsolutePath;
	private static String importFileAbsolutePath;
	
	private static String exportDeviceListFileName;
	private static String exportDeviceListFileRelativePath;
	private static String exportDeviceListFileAbsolutePath;
	
	private static class General {
		private static Properties props;
		private static String propertiesFile;
		private static Map<String, String> propertiesMap;
		
		static {
			propertiesMap = new LinkedHashMap<String, String>();
			propertiesMap.put("general.device.add.lastIP",				"");
			propertiesMap.put("general.device.scan.ipFrom",				"192.168.0.1");
			propertiesMap.put("general.device.scan.ipTo",				"192.168.0.254");
			
			propertiesMap.put("general.chart.realtime.timeout.s",		"10");
			propertiesMap.put("general.topology.topDevice",				"");
			propertiesMap.put("general.remoteWeb.port",					"8081");
			propertiesMap.put("general.login.user.max",					"5");
			
			propertiesMap.put("general.smtp.1.name",					"GMail");
			propertiesMap.put("general.smtp.1.host",					"smtp.gmail.com");
			propertiesMap.put("general.smtp.1.port",					"465");
			propertiesMap.put("general.smtp.1.timeout.ms",				"5000");
			propertiesMap.put("general.smtp.1.username",				"vianetworking1@gmail.com");
			propertiesMap.put("general.smtp.1.password",				"22185452");
			propertiesMap.put("general.smtp.2.name",					"");
			propertiesMap.put("general.smtp.2.host",					"");
			propertiesMap.put("general.smtp.2.port",					"");
			propertiesMap.put("general.smtp.2.timeout.ms",				"");
			propertiesMap.put("general.smtp.2.username",				"");
			propertiesMap.put("general.smtp.2.password",				"");
			
			propertiesMap.put("general.mail.subject",					"VIA NMS E-Mail Message");
			propertiesMap.put("general.mail.fromLabel",					"VIA NMS NOTICE");
			propertiesMap.put("general.mail.to",						"");
			propertiesMap.put("general.mail.cc",						"");
			propertiesMap.put("general.mail.bcc",						"");
			propertiesMap.put("general.mail.smtp.select",				"1");
			propertiesMap.put("general.mail.queue.enable",				"0");
			propertiesMap.put("general.mail.queue.interval.ms",			"60000");
			
			propertiesMap.put("general.sms.1.name",						"SANZHU");
			propertiesMap.put("general.sms.1.provider",					"SANZHU");
			propertiesMap.put("general.sms.1.encoding",					"BIG5");
			propertiesMap.put("general.sms.1.username",					"86870786");
			propertiesMap.put("general.sms.1.password",					"22185452");
			propertiesMap.put("general.sms.1.timeout.ms",				"5000");
			propertiesMap.put("general.sms.2.name",						"");
			propertiesMap.put("general.sms.2.provider",					"");
			propertiesMap.put("general.sms.2.encoding",					"");
			propertiesMap.put("general.sms.2.username",					"");
			propertiesMap.put("general.sms.2.password",					"");
			propertiesMap.put("general.sms.2.timeout.ms",				"");
			propertiesMap.put("general.sms.select",						"1");
			propertiesMap.put("general.sms.queue.enable",				"0");
			propertiesMap.put("general.sms.queue.interval.ms",			"60000");
			
			propertiesMap.put("general.nms.type",						"1");								// 1: Stand-alone, 2: Dual-Layer
			
			propertiesMap.put("general.rmi.display",					"0");
			propertiesMap.put("general.rmi.enable",						"0");
			propertiesMap.put("general.rmi.serviceAddress",				JSystem.ethAddress);
			//propertyMap.put("general.rmi.registryPort",				"1099");
			//propertyMap.put("general.rmi.dataPort",					"55555");
			propertiesMap.put("general.rmi.serverAddress",				"");
			//propertyMap.put("general.rmi.serverPort",					"1099");
			
			propertiesMap.put("general.report.mail.daily",				"");
			propertiesMap.put("general.report.mail.weekly",				"");
			propertiesMap.put("general.report.mail.monthly",				"");
			
			propertiesMap.put("general.location.address",				"新北市新店區中正路531號1樓");
			
			propertiesFile = generalPropertyFile;
			
			Properties defaultProps = Property.load(propertiesMap);
			if (new File(propertiesFile).isFile()) {
				System.out.println("Config.General will use local properties.");
				
				Property.refreshFile(defaultProps, propertiesFile, true);		// insert new property to file if needs
				props = Property.load(propertiesFile);
			}
			else {
				System.out.println("Config.General will use default properties, a local copy is creating.");
				
				Property.store(defaultProps, propertiesFile);			// it will create file
				props = defaultProps;
			}
		}

		public static String getDeviceAddLastIP() {
			return props.getProperty("general.device.add.lastIP");
		}
		
		public static void setDeviceAddLastIP(final String ip) {
			props.setProperty("general.device.add.lastIP", ip);
			
			Property.store(props, propertiesFile);
		}
		
		public static String[] getDeviceScanRange() {
			return new String[] {props.getProperty("general.device.scan.ipFrom"), props.getProperty("general.device.scan.ipTo")};
		}
		
		public static void setDeviceScanRange(final String ipFrom, final String ipTo) {
			props.setProperty("general.device.scan.ipFrom", ipFrom);
			props.setProperty("general.device.scan.ipTo", ipTo);
			
			Property.store(props, propertiesFile);
		}
		
		public static int getRealtimeChartTimeout() {
			return Integer.parseInt(props.getProperty("general.chart.realtime.timeout.s"));
		}
		
		public static void setRealtimeChartTimeout(final int timeout) {
			props.setProperty("general.chart.realtime.timeout.s", String.valueOf(timeout));
			
			Property.store(props, propertiesFile);
		}
		
		public static String getTopologyTopDevice() {
			return props.getProperty("general.topology.topDevice");
		}
		
		public static void setTopologyTopDevice(final String ip) {
			props.setProperty("general.topology.topDevice", ip);
			
			Property.store(props, propertiesFile);
		}
		
		public static int getRemoteWebPort() {
			return Integer.parseInt(props.getProperty("general.remoteWeb.port"));
		}
		
		public static void setRemoteWebPort(final int port) {
			props.setProperty("general.remoteWeb.port", String.valueOf(port));
			
			Property.store(props, propertiesFile);
		}
		
		public static int getLoginUserMax() {
			return Integer.parseInt(props.getProperty("general.login.user.max"));
		}
		
		public static void setLoginUserMax(final int num) {
			props.setProperty("general.login.user.max", String.valueOf(num));
			
			Property.store(props, propertiesFile);
		}
		
		public static SmtpConfig getSmtp1() {
			SmtpConfig smtp = new SmtpConfig();
			smtp.setHost(props.getProperty("general.smtp.1.host"));
			smtp.setPort(props.getProperty("general.smtp.1.port"));
			smtp.setTimeout(Integer.valueOf(props.getProperty("general.smtp.1.timeout.ms")));
			smtp.setUsername(props.getProperty("general.smtp.1.username"));
			smtp.setPassword(props.getProperty("general.smtp.1.password"));
			
			return smtp;
		}
		
		public static void setSmtp1(final SmtpConfig smtp) {
			props.setProperty("general.smtp.1.host", smtp.getHost());
			props.setProperty("general.smtp.1.port", smtp.getPort());
			props.setProperty("general.smtp.1.timeout.ms", String.valueOf(smtp.getTimeout()));
			props.setProperty("general.smtp.1.username", smtp.getUsername());
			props.setProperty("general.smtp.1.password", smtp.getPassword());
			
			Property.store(props, propertiesFile);
		}
		
		public static MailConfig getMailConfig() {
			MailConfig mail = new MailConfig();
			mail.setSubject(props.getProperty("general.mail.subject"));
			mail.setFromLable(props.getProperty("general.mail.fromLabel"));
			mail.setTo(props.getProperty("general.mail.to"));
			mail.setCc(props.getProperty("general.mail.cc"));
			mail.setBcc(props.getProperty("general.mail.bcc"));
			
			return mail;
		}
		
		public static void setMailConfig(final MailConfig mail) {
			props.setProperty("general.mail.subject", mail.getSubject());
			props.setProperty("general.mail.fromLabel", mail.getFromLable());
			props.setProperty("general.mail.to", mail.getTo());
			props.setProperty("general.mail.cc", mail.getCc());
			props.setProperty("general.mail.bcc", mail.getBcc());
			
			Property.store(props, propertiesFile);
		}
		
		public static boolean isMailQueueEnabled() {
			return props.getProperty("general.mail.queue.enable").equals("1");
		}
		
		public static void setMailQueueEnable(boolean enable) {
			props.setProperty("general.mail.queue.enable", (enable ? "1" : "0"));
			
			Property.store(props, propertiesFile);
		}
		
		public static long getMailQueueInterval() {
			return Long.parseLong(props.getProperty("general.mail.queue.interval.ms"));
		}
		
		public static void setMailQueueInterval(long interval) {
			props.setProperty("general.mail.queue.interval.ms", String.valueOf(interval));
			
			Property.store(props, propertiesFile);
		}
		
		public static ShortMessageConfig getSms1Config() {
			ShortMessageConfig config = new ShortMessageConfig();
			config.setProvider(props.getProperty("general.sms.1.provider"));
			config.setEncoding(props.getProperty("general.sms.1.encoding"));
			config.setUsername(props.getProperty("general.sms.1.username"));
			config.setPassword(props.getProperty("general.sms.1.password"));
			config.setTimeout(Integer.valueOf(props.getProperty("general.sms.1.timeout.ms")));
			
			return config;
		}
		
		public static void setSms1Config(ShortMessageConfig config) {
			props.setProperty("general.sms.1.provider", config.getProvider());
			props.setProperty("general.sms.1.encoding", config.getEncoding());
			props.setProperty("general.sms.1.username", config.getUsername());
			props.setProperty("general.sms.1.password", config.getPassword());
			props.setProperty("general.sms.1.timeout.ms", String.valueOf(config.getTimeout()));
			
			Property.store(props, propertiesFile);
		}
		
		public static boolean isSmsQueueEnabled() {
			return props.getProperty("general.sms.queue.enable").equals("1");
		}
		
		public static void setSmsQueueEnable(boolean enable) {
			props.setProperty("general.sms.queue.enable", (enable ? "1" : "0"));
			
			Property.store(props, propertiesFile);
		}
		
		public static long getSmsQueueInterval() {
			return Long.parseLong(props.getProperty("general.sms.queue.interval.ms"));
		}
		
		public static void setSmsQueueInterval(long interval) {
			props.setProperty("general.sms.queue.interval.ms", String.valueOf(interval));
			
			Property.store(props, propertiesFile);
		}
		
		public static String getNmsType() {
			return props.getProperty("general.nms.type");
		}
		
		public static void setNmsType(final String type) {
			props.setProperty("general.nms.type", type);
			
			Property.store(props, propertiesFile);
		}
		
		public static RemoteServiceConfig getRemoteServiceConfig() {
			RemoteServiceConfig config = new RemoteServiceConfig();
			config.setEnable(props.getProperty("general.rmi.enable").equals("1"));
			config.setLocalAddress(props.getProperty("general.rmi.serviceAddress"));
			config.setServerAddress(props.getProperty("general.rmi.serverAddress"));
			
			return config;
		}
		
		public static void setRemoteServiceConfig(RemoteServiceConfig config) {
			props.setProperty("general.rmi.enable", (config.isEnable() ? "1" : "0"));
			props.setProperty("general.rmi.serviceAddress", config.getLocalAddress());
			props.setProperty("general.rmi.serverAddress", config.getServerAddress());
			
			Property.store(props, propertiesFile);
		}
		
		public static String getReportDailyMail() {
			return props.getProperty("general.report.mail.daily");
		}
		
		public static void setReportDailyMail(final String dailyMail) {
			props.setProperty("general.report.mail.daily", dailyMail);
			
			Property.store(props, propertiesFile);
		}
		
		public static String getReportWeeklyMail() {
			return props.getProperty("general.report.mail.weekly");
		}
		
		public static void setReportWeeklyMail(final String WeeklyMail) {
			props.setProperty("general.report.mail.weekly", WeeklyMail);
			
			Property.store(props, propertiesFile);
		}
		
		public static String getReportMonthlyMail() {
			return props.getProperty("general.report.mail.monthly");
		}
		
		public static void setReportMonthlyMail(final String MonthlyMail) {
			props.setProperty("general.report.mail.monthly", MonthlyMail);
			
			Property.store(props, propertiesFile);
		}
		
		public static String getLocationAddress() {
			return props.getProperty("general.location.address");
		}
		
		public static void setLocationAddress(String Address) {
			props.setProperty("general.location.address", Address);
			
			Property.store(props, propertiesFile);
		}
	}
	
	private static class Alarm {
		private static Properties props;
		private static String propertiesFile;
		private static Map<String, String> propertiesMap;
		
		static {
			propertiesMap = new LinkedHashMap<String, String>();
			propertiesMap.put("alarm.projectStartup.mail.enable",			"0");
			propertiesMap.put("alarm.projectStartup.mail.userList",			"");
			propertiesMap.put("alarm.projectStartup.sms.enable",			"0");
			propertiesMap.put("alarm.projectStartup.sms.userList",			"");
			
			propertiesMap.put("alarm.projectShutdown.mail.enable",			"0");
			propertiesMap.put("alarm.projectShutdown.mail.userList",		"");
			propertiesMap.put("alarm.projectShutdown.sms.enable",			"0");
			propertiesMap.put("alarm.projectShutdown.sms.userList",			"");
			
			propertiesMap.put("alarm.deviceDisconnect.mail.enable",			"0");
			propertiesMap.put("alarm.deviceDisconnect.mail.userList",		"");
			propertiesMap.put("alarm.deviceDisconnect.sms.enable",			"0");
			propertiesMap.put("alarm.deviceDisconnect.sms.userList",		"");
			
			propertiesMap.put("alarm.monitorPortLinkUp.mail.enable",		"0");
			propertiesMap.put("alarm.monitorPortLinkUp.mail.userList",		"");
			propertiesMap.put("alarm.monitorPortLinkUp.sms.enable",			"0");
			propertiesMap.put("alarm.monitorPortLinkUp.sms.userList",		"");
			
			propertiesMap.put("alarm.monitorPortLinkDown.mail.enable",		"0");
			propertiesMap.put("alarm.monitorPortLinkDown.mail.userList",	"");
			propertiesMap.put("alarm.monitorPortLinkDown.sms.enable",		"0");
			propertiesMap.put("alarm.monitorPortLinkDown.sms.userList",		"");
			
			propertiesMap.put("alarm.criticalPortLinkUp.mail.enable",		"0");
			propertiesMap.put("alarm.criticalPortLinkUp.mail.userList",		"");
			propertiesMap.put("alarm.criticalPortLinkUp.sms.enable",		"0");
			propertiesMap.put("alarm.criticalPortLinkUp.sms.userList",		"");
			
			propertiesMap.put("alarm.criticalPortLinkDown.mail.enable",		"0");
			propertiesMap.put("alarm.criticalPortLinkDown.mail.userList",	"");
			propertiesMap.put("alarm.criticalPortLinkDown.sms.enable",		"0");
			propertiesMap.put("alarm.criticalPortLinkDown.sms.userList",	"");
			
			propertiesMap.put("alarm.wlanAPJoin.mail.enable",				"0");
			propertiesMap.put("alarm.wlanAPJoin.mail.userList",				"");
			propertiesMap.put("alarm.wlanAPJoin.sms.enable",				"0");
			propertiesMap.put("alarm.wlanAPJoin.sms.userList",				"");
			
			propertiesMap.put("alarm.wlanAPLeave.mail.enable",				"0");
			propertiesMap.put("alarm.wlanAPLeave.mail.userList",			"");
			propertiesMap.put("alarm.wlanAPLeave.sms.enable",				"0");
			propertiesMap.put("alarm.wlanAPLeave.sms.userList",				"");
			
			propertiesMap.put("alarm.webUpdate.mail.enable",				"0");
			propertiesMap.put("alarm.webUpdate.mail.userList",				"");
			propertiesMap.put("alarm.webUpdate.sms.enable",					"0");
			propertiesMap.put("alarm.webUpdate.sms.userList",				"");
			
			propertiesFile = alarmPropertyFile;
			
			Properties defaultProps = Property.load(propertiesMap);
			if (new File(propertiesFile).isFile()) {
				System.out.println("Config.Alarm will use local properties.");
				
				Property.refreshFile(defaultProps, propertiesFile, true);		// insert new property to file if needs
				props = Property.load(propertiesFile);
			}
			else {
				System.out.println("Config.Alarm will use default properties, a local copy is creating.");
				
				Property.store(defaultProps, propertiesFile);			// it will create file
				props = defaultProps;
			}
		}
		
		private static String mergeList(List<String> list) {
			if (list.size() < 1) return "";
			String result = list.get(0);
			for (String s : list) {
				result += "," + s; 
			}
			return result;
		}
		
		public static AlarmConfig getAlarmMailConfig() {
			AlarmConfig config = new AlarmConfig();
			config.setProjectStartup(props.getProperty("alarm.projectStartup.mail.enable").equals("1"));
			config.setProjectStartupUserList(Arrays.asList(props.getProperty("alarm.projectStartup.mail.userList").split(",")));
			config.setProjectShutdown(props.getProperty("alarm.projectShutdown.mail.enable").equals("1"));
			config.setProjectShutdownUserList(Arrays.asList(props.getProperty("alarm.projectShutdown.mail.userList").split(",")));
			config.setDeviceDisconnect(props.getProperty("alarm.deviceDisconnect.mail.enable").equals("1"));
			config.setDeviceDisconnectUserList(Arrays.asList(props.getProperty("alarm.deviceDisconnect.mail.userList").split(",")));
			config.setMonitorPortLinkUp(props.getProperty("alarm.monitorPortLinkUp.mail.enable").equals("1"));
			config.setMonitorPortLinkUpUserList(Arrays.asList(props.getProperty("alarm.monitorPortLinkUp.mail.userList").split(",")));
			config.setMonitorPortLinkDown(props.getProperty("alarm.monitorPortLinkDown.mail.enable").equals("1"));
			config.setMonitorPortLinkDownUserList(Arrays.asList(props.getProperty("alarm.monitorPortLinkDown.mail.userList").split(",")));
			config.setCriticalPortLinkUp(props.getProperty("alarm.criticalPortLinkUp.mail.enable").equals("1"));
			config.setCriticalPortLinkUpUserList(Arrays.asList(props.getProperty("alarm.criticalPortLinkUp.mail.userList").split(",")));
			config.setCriticalPortLinkDown(props.getProperty("alarm.criticalPortLinkDown.mail.enable").equals("1"));
			config.setCriticalPortLinkDownUserList(Arrays.asList(props.getProperty("alarm.criticalPortLinkDown.mail.userList").split(",")));
			config.setWlanAPJoin(props.getProperty("alarm.wlanAPJoin.mail.enable").equals("1"));
			config.setWlanAPJoinUserList(Arrays.asList(props.getProperty("alarm.wlanAPJoin.mail.userList").split(",")));
			config.setWlanAPLeave(props.getProperty("alarm.wlanAPLeave.mail.enable").equals("1"));
			config.setWlanAPLeaveUserList(Arrays.asList(props.getProperty("alarm.wlanAPLeave.mail.userList").split(",")));
			config.setWebUpdate(props.getProperty("alarm.webUpdate.mail.enable").equals("1"));
			config.setWebUpdateUserList(Arrays.asList(props.getProperty("alarm.webUpdate.mail.userList").split(",")));
			
			return config;
		}
		
		public static void setAlarmMailConfig(AlarmConfig config) {
			props.setProperty("alarm.projectStartup.mail.enable", config.isProjectStartup() ? "1" : "0");
			props.setProperty("alarm.projectStartup.mail.userList", mergeList(config.getProjectStartupUserList()));
			props.setProperty("alarm.projectShutdown.mail.enable", config.isProjectShutdown() ? "1" : "0");
			props.setProperty("alarm.projectShutdown.mail.userList", mergeList(config.getProjectShutdownUserList()));
			props.setProperty("alarm.deviceDisconnect.mail.enable", config.isDeviceDisconnect() ? "1" : "0");
			props.setProperty("alarm.deviceDisconnect.mail.userList", mergeList(config.getDeviceDisconnectUserList()));
			props.setProperty("alarm.monitorPortLinkUp.mail.enable", config.isMonitorPortLinkUp() ? "1" : "0");
			props.setProperty("alarm.monitorPortLinkUp.mail.userList", mergeList(config.getMonitorPortLinkUpUserList()));
			props.setProperty("alarm.monitorPortLinkDown.mail.enable", config.isMonitorPortLinkDown() ? "1" : "0");
			props.setProperty("alarm.monitorPortLinkDown.mail.userList", mergeList(config.getMonitorPortLinkDownUserList()));
			props.setProperty("alarm.criticalPortLinkUp.mail.enable", config.isCriticalPortLinkUp() ? "1" : "0");
			props.setProperty("alarm.criticalPortLinkUp.mail.userList", mergeList(config.getCriticalPortLinkUpUserList()));
			props.setProperty("alarm.criticalPortLinkDown.mail.enable", config.isCriticalPortLinkDown() ? "1" : "0");
			props.setProperty("alarm.criticalPortLinkDown.mail.userList", mergeList(config.getCriticalPortLinkDownUserList()));
			props.setProperty("alarm.wlanAPJoin.mail.enable", config.isWlanAPJoin() ? "1" : "0");
			props.setProperty("alarm.wlanAPJoin.mail.userList", mergeList(config.getWlanAPJoinUserList()));
			props.setProperty("alarm.wlanAPLeave.mail.enable", config.isWlanAPLeave() ? "1" : "0");
			props.setProperty("alarm.wlanAPLeave.mail.userList", mergeList(config.getWlanAPLeaveUserList()));
			props.setProperty("alarm.webUpdate.mail.enable", config.isWebUpdate() ? "1" : "0");
			props.setProperty("alarm.webUpdate.mail.userList", mergeList(config.getWebUpdateUserList()));
			
			Property.store(props, propertiesFile);
		}
		
		public static AlarmConfig getAlarmSmsConfig() {
			AlarmConfig config = new AlarmConfig();
			config.setProjectStartup(props.getProperty("alarm.projectStartup.sms.enable").equals("1"));
			config.setProjectStartupUserList(Arrays.asList(props.getProperty("alarm.projectStartup.sms.userList").split(",")));
			config.setProjectShutdown(props.getProperty("alarm.projectShutdown.sms.enable").equals("1"));
			config.setProjectShutdownUserList(Arrays.asList(props.getProperty("alarm.projectShutdown.sms.userList").split(",")));
			config.setDeviceDisconnect(props.getProperty("alarm.deviceDisconnect.sms.enable").equals("1"));
			config.setDeviceDisconnectUserList(Arrays.asList(props.getProperty("alarm.deviceDisconnect.sms.userList").split(",")));
			config.setMonitorPortLinkUp(props.getProperty("alarm.monitorPortLinkUp.sms.enable").equals("1"));
			config.setMonitorPortLinkUpUserList(Arrays.asList(props.getProperty("alarm.monitorPortLinkUp.sms.userList").split(",")));
			config.setMonitorPortLinkDown(props.getProperty("alarm.monitorPortLinkDown.sms.enable").equals("1"));
			config.setMonitorPortLinkDownUserList(Arrays.asList(props.getProperty("alarm.monitorPortLinkDown.sms.userList").split(",")));
			config.setCriticalPortLinkUp(props.getProperty("alarm.criticalPortLinkUp.sms.enable").equals("1"));
			config.setCriticalPortLinkUpUserList(Arrays.asList(props.getProperty("alarm.criticalPortLinkUp.sms.userList").split(",")));
			config.setCriticalPortLinkDown(props.getProperty("alarm.criticalPortLinkDown.sms.enable").equals("1"));
			config.setCriticalPortLinkDownUserList(Arrays.asList(props.getProperty("alarm.criticalPortLinkDown.sms.userList").split(",")));
			config.setWlanAPJoin(props.getProperty("alarm.wlanAPJoin.sms.enable").equals("1"));
			config.setWlanAPJoinUserList(Arrays.asList(props.getProperty("alarm.wlanAPJoin.sms.userList").split(",")));
			config.setWlanAPLeave(props.getProperty("alarm.wlanAPLeave.sms.enable").equals("1"));
			config.setWlanAPLeaveUserList(Arrays.asList(props.getProperty("alarm.wlanAPLeave.sms.userList").split(",")));
			config.setWebUpdate(props.getProperty("alarm.webUpdate.sms.enable").equals("1"));
			config.setWebUpdateUserList(Arrays.asList(props.getProperty("alarm.webUpdate.sms.userList").split(",")));
			
			return config;
		}
		
		public static void setAlarmSmsConfig(AlarmConfig config) {
			props.setProperty("alarm.projectStartup.sms.enable", config.isProjectStartup() ? "1" : "0");
			props.setProperty("alarm.projectStartup.sms.userList", mergeList(config.getProjectStartupUserList()));
			props.setProperty("alarm.projectShutdown.sms.enable", config.isProjectShutdown() ? "1" : "0");
			props.setProperty("alarm.projectShutdown.sms.userList", mergeList(config.getProjectShutdownUserList()));
			props.setProperty("alarm.deviceDisconnect.sms.enable", config.isDeviceDisconnect() ? "1" : "0");
			props.setProperty("alarm.deviceDisconnect.sms.userList", mergeList(config.getDeviceDisconnectUserList()));
			props.setProperty("alarm.monitorPortLinkUp.sms.enable", config.isMonitorPortLinkUp() ? "1" : "0");
			props.setProperty("alarm.monitorPortLinkUp.sms.userList", mergeList(config.getMonitorPortLinkUpUserList()));
			props.setProperty("alarm.monitorPortLinkDown.sms.enable", config.isMonitorPortLinkDown() ? "1" : "0");
			props.setProperty("alarm.monitorPortLinkDown.sms.userList", mergeList(config.getMonitorPortLinkDownUserList()));
			props.setProperty("alarm.criticalPortLinkUp.sms.enable", config.isCriticalPortLinkUp() ? "1" : "0");
			props.setProperty("alarm.criticalPortLinkUp.sms.userList", mergeList(config.getCriticalPortLinkUpUserList()));
			props.setProperty("alarm.criticalPortLinkDown.sms.enable", config.isCriticalPortLinkDown() ? "1" : "0");
			props.setProperty("alarm.criticalPortLinkDown.sms.userList", mergeList(config.getCriticalPortLinkDownUserList()));
			props.setProperty("alarm.wlanAPJoin.sms.enable", config.isWlanAPJoin() ? "1" : "0");
			props.setProperty("alarm.wlanAPJoin.sms.userList", mergeList(config.getWlanAPJoinUserList()));
			props.setProperty("alarm.wlanAPLeave.sms.enable", config.isWlanAPLeave() ? "1" : "0");
			props.setProperty("alarm.wlanAPLeave.sms.userList", mergeList(config.getWlanAPLeaveUserList()));
			props.setProperty("alarm.webUpdate.sms.enable", config.isWebUpdate() ? "1" : "0");
			props.setProperty("alarm.webUpdate.sms.userList", mergeList(config.getWebUpdateUserList()));
			
			Property.store(props, propertiesFile);
		}
		
		public static boolean isProjectStartupMailEnabled() {
			return props.getProperty("alarm.projectStartup.mail.enable").equals("1");
		}
		
		public static boolean isProjectStartupSmsEnabled() {
			return props.getProperty("alarm.projectStartup.sms.enable").equals("1");
		}
		
		public static boolean isProjectShutdownMailEnabled() {
			return props.getProperty("alarm.projectShutdown.mail.enable").equals("1");
		}
		
		public static boolean isProjectShutdownSmsEnabled() {
			return props.getProperty("alarm.projectShutdown.sms.enable").equals("1");
		}
		
		public static boolean isDeviceDisconnectMailEnabled() {
			return props.getProperty("alarm.deviceDisconnect.mail.enable").equals("1");
		}
		
		public static boolean isDeviceDisconnectSmsEnabled() {
			return props.getProperty("alarm.deviceDisconnect.sms.enable").equals("1");
		}
	}
	
	private static class Log4j {
		private static Properties props;			// TODO does it still need to maintain a properties for Log4j?
		private static String propertiesFile;
		
		static {
			propertiesFile = log4jPropertyFile;
			
			if (new File(propertiesFile).isFile()) {
				System.out.println("Config.Log4j will use local properties.");
				
				props = Property.load(propertiesFile);				// Should it check if any property is removed by someone ?
				PropertyConfigurator.configure(props);				// force log4j to load properties
			}
			else {
				System.out.println("Config.Log4j will use default properties, a local copy is creating.");
				
				// Load the default properties
				InputStream inStream = Log4j.class.getResourceAsStream("/log4j.properties");	// TODO if return null
				props = Property.load(inStream);
				
				Property.store(props, propertiesFile);
			}
			
			System.out.println(String.format("Config.Log4j: console:%b, file:%b, mail:%b, db:%b", isConsoleEnable(), isFileEnable(), isMailEnable(), isDbEnable()));
		}
		
		private static void setRootLogger(boolean consoleEnable, boolean fileEnable, boolean mailEnable, boolean dbEnable) {
			String rootLogger = "All";
			if (consoleEnable) rootLogger += ", consoleAppender";
			if (fileEnable) rootLogger += ", fileAppender";
			if (mailEnable) rootLogger += ", mailAppender";
			if (dbEnable) rootLogger += ", dbAppender";
			props.setProperty("log4j.rootLogger", rootLogger);
			
			PropertyConfigurator.configure(props);
			Property.store(props, propertiesFile);
		}
		
		public static boolean isConsoleEnable() {
			return props.getProperty("log4j.rootLogger").indexOf("consoleAppender") >= 0 ? true : false;
		}
		
		public static void setConsoleEnable(boolean enable) {
			if (enable && isConsoleEnable()) return;
			
			setRootLogger(enable, isFileEnable(), isMailEnable(), isDbEnable());
		}
		
		public static boolean isFileEnable() {
			return props.getProperty("log4j.rootLogger").indexOf("fileAppender") >= 0 ? true : false;
		}
		
		public static void setFileEnable(boolean enable) {
			if (enable && isFileEnable()) return;
			
			setRootLogger(isConsoleEnable(), enable, isMailEnable(), isDbEnable());
		}
		
		public static boolean isMailEnable() {
			return props.getProperty("log4j.rootLogger").indexOf("mailAppender") >= 0 ? true : false;
		}
		
		public static void setMailEnable(boolean enable) {
			if (enable && isMailEnable()) return;
			
			setRootLogger(isConsoleEnable(), isFileEnable(), enable, isDbEnable());
		}
		
		public static boolean isDbEnable() {
			return props.getProperty("log4j.rootLogger").indexOf("dbAppender") >= 0 ? true : false;
		}
		
		public static void setDbEnable(boolean enable) {
			if (enable && isDbEnable()) return;
			
			setRootLogger(isConsoleEnable(), isFileEnable(), isMailEnable(), enable);
		}
	}
	
	private static class Property {
		private static Properties createNew() {
			return new Properties() {
				private static final long serialVersionUID = 1L;

				@Override
				public synchronized Enumeration<Object> keys() {
					return Collections.enumeration(new TreeSet<Object>(super.keySet()));	//sort all keys in descent order, is used for output file
				}
			};
		}
		
		public static Properties load(Map<String, String> propertiesMap) {
			Properties props = createNew();
			
			for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
				props.setProperty(entry.getKey(), entry.getValue());
			}
			
			return props;
		}
		
		public static Properties load(InputStream inStream) {
			Properties props = null;
			
			try {
				Properties p = createNew();
				p.load(inStream);
				props = p;
			} catch (IOException e) {
				System.out.println("IO exception when loading local properties.");
			}
			
			return props;
		}
		
		public static Properties load(String fileName) {
			Properties props = null;
			InputStream input = null;
			
			try {
				input = new FileInputStream(fileName);
			}
			catch (FileNotFoundException e) {
				System.out.println("File not found when loading local properties.");
			}
			
			if (input != null) {
				props = load(input);
				
				try {
					input.close();
				}
				catch (IOException e) {
				}
			}
			
			return props;
		}
		
		public static void store(Properties props, String fileName) {
			OutputStream output = null;
			try {
				output =  new FileOutputStream(fileName);		// create file if not exist, throws exception when creates failed
				props.store(output, null);
			}
			catch (FileNotFoundException e) {
				System.out.println("File not found when storing local properties.");
			}
			catch (IOException e) {
				System.out.println("IO exception when storing local properties.");
			}
			finally {
				if (output != null) {
					try {
						output.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		
		public static void refreshFile(Properties props, String fileName, boolean isEraseUnused) {
			Properties targetProps = load(fileName);
			Properties sourceProps = props;
			
			if (targetProps == null) {
				System.out.println("Refresh properties failed due to null is read.");
				return;
			}
			
			boolean isNeedToUpdate = false;
			for (Map.Entry<?, ?> entry : sourceProps.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (!targetProps.containsKey(key)) {						// add new property only when key does not exist in file
					targetProps.setProperty(key, value);
					isNeedToUpdate = true;
				}
			}

			if (isEraseUnused) {
				Iterator<Map.Entry<Object, Object>> it = targetProps.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Object, Object> entry = it.next();
					String key = (String) entry.getKey();
					if (!sourceProps.containsKey(key)) {
						System.out.println("Erasing " + key);
						it.remove();
						isNeedToUpdate = true;
					}
				}
			}

			if (isNeedToUpdate) store(targetProps, fileName);
		}
	}
	
	static {
		generalPropertyFile = JSystem.projectSpace + File.separator + "networkmanagement.setting.properties";
		log4jPropertyFile = JSystem.projectSpace + File.separator + "networkmanagement.log4j.properties";
		alarmPropertyFile = JSystem.projectSpace + File.separator + "networkmanagement.JAlarm.properties";
		
		exportFileName = "networkmanagement.new-settings.zip";
		exportFileRelativePath = "data" + File.separator + exportFileName;
		exportFileAbsolutePath = JSystem.getProjectHome() + File.separator + exportFileRelativePath;
		importFileAbsolutePath = JSystem.projectSpace + File.separator + exportFileName;
		
		exportDeviceListFileName = "DeviceList.CSV";
		exportDeviceListFileRelativePath = "data" + File.separator + exportDeviceListFileName;
		exportDeviceListFileAbsolutePath = JSystem.getProjectHome() + File.separator + exportDeviceListFileRelativePath;
		
		// Note this function should avoid do anything that will trigger General/Log4j/Alarm to be initialized
		File importFile = new File(importFileAbsolutePath);
		if (importFile.isFile()) {
			System.out.println("Import settings file is found, and will be set to start-up configuration.");
			
			if (importFromFile(importFileAbsolutePath)) {
				System.out.println("Import settings apply success.");
				importFile.delete();
			}
			else {
				System.out.println("Import settings apply failed.");
			}
		}
		
		new General();
		new Log4j();
		//new Alarm();
	}
	
	public static String getExportFileName() {
		return exportFileName;
	}
	
	public static String getExportFileRelativePath() {
		return exportFileRelativePath;
	}
	
	public static String getExportFileAbsolutePath() {
		return exportFileAbsolutePath;
	}
	
	public static String getImportFileName() {
		return exportFileName;
	}
	
	public static String getImportFileAbsolutePath() {
		return importFileAbsolutePath;
	}
	
	public static String getExportDeviceListFileName() {
		return exportDeviceListFileName;
	}
	
	public static String getExportDeviceListFileRelativePath() {
		return exportDeviceListFileRelativePath;
	}
	
	public static String getExportDeviceListFileAbsolutePath() {
		return exportDeviceListFileAbsolutePath;
	}
	
	public static boolean exportToFile() {
		String generalFileName = generalPropertyFile;
		String log4jFileName = log4jPropertyFile;
		String alarmFileName = alarmPropertyFile;
		
		// Export device table and save to a CSV format file
		String deviceFile = JSystem.projectSpace + File.separator + "device_update_temp";
		TableData deviceData = DeviceTable.exportData();
		String deviceVersion = "0001";
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(deviceFile, true), ',');
			csvOutput.flush();
			
			csvOutput.write(deviceData.getName());
			csvOutput.write(deviceVersion);								// write out version of format
			csvOutput.endRecord();
			
			for (List<String> column : deviceData.getColumns()) {		// write out column name
				csvOutput.write(column.get(0));
			}
			csvOutput.endRecord();
			for (List<String> column : deviceData.getColumns()) {		// write out column type
				csvOutput.write(column.get(1));
			}
			csvOutput.endRecord();
			for (List<String> column : deviceData.getColumns()) {		// write out column size
				csvOutput.write(column.get(2));
			}
			csvOutput.endRecord();

			for (List<String> record : deviceData.getRows()) {			// write out all records
				for (String data : record) {
					csvOutput.write(data);
				}
				csvOutput.endRecord();
			}

			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
			new File(deviceFile).delete();
			return false;
		}
		
		// Pack all files by using Zip
		new File(exportFileAbsolutePath).delete();
		int resultCode = JZip.create(Arrays.asList(generalFileName, log4jFileName, alarmFileName, deviceFile), exportFileAbsolutePath, "22185452");
		if (resultCode > 0) {
			System.out.println("Config.export failed.");
		}
		
		// Delete the temp file of device table
		new File(deviceFile).delete();
		
		return new File(exportFileAbsolutePath).isFile();
	}
	
	public static boolean testImportFile(String sourceFile) {
		boolean result = false;
		String generalFileName = new File(generalPropertyFile).getName();
		String log4jFileName = new File(log4jPropertyFile).getName();
		String alarmFileName = new File(alarmPropertyFile).getName();
		String deviceFileName = "device_update_temp";
		String tempDirectory = JSystem.projectSpace + File.separator + "cyberexpert.tmp";
		String tempGeneralFile = tempDirectory + File.separator + generalFileName;
		String tempLog4jFile = tempDirectory + File.separator + log4jFileName;
		String tempAlarmFile = tempDirectory + File.separator + alarmFileName;
		String tempDeviceFile = tempDirectory + File.separator + deviceFileName;
		
		do {
			// Extract to test all necessary files exist or not
			if (JZip.unzip(sourceFile, generalFileName, tempDirectory, "22185452") > 0) {			// folder will be created if it does not exist
				System.out.println("Config.import extract file " + generalFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, log4jFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + log4jFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, alarmFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + alarmFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, deviceFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + deviceFileName + " failed.");
				break;
			}
			
			result = true;
		} while (false);
		new File(tempGeneralFile).delete();
		new File(tempLog4jFile).delete();
		new File(tempAlarmFile).delete();
		new File(tempDeviceFile).delete();
		new File(tempDirectory).delete();			// folder will not be deleted if there exists some other files
		
		return result;
	}
	
	public static boolean importFromFile(String sourceFile) {
		boolean result = false;
		String generalFileName = new File(generalPropertyFile).getName();
		String log4jFileName = new File(log4jPropertyFile).getName();
		String alarmFileName = new File(alarmPropertyFile).getName();
		String deviceFileName = "device_update_temp";
		String tempDirectory = JSystem.projectSpace + File.separator + "cyberexpert.tmp";
		String tempGeneralFile = tempDirectory + File.separator + generalFileName;
		String tempLog4jFile = tempDirectory + File.separator + log4jFileName;
		String tempAlarmFile = tempDirectory + File.separator + alarmFileName;
		String tempDeviceFile = tempDirectory + File.separator + deviceFileName;
		
		do {
			// Extract all necessary files from zip
			if (JZip.unzip(sourceFile, generalFileName, tempDirectory, "22185452") > 0) {			// folder will be created if it does not exist
				System.out.println("Config.import extract file " + generalFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, log4jFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + log4jFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, alarmFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + alarmFileName + " failed.");
				break;
			}
			if (JZip.unzip(sourceFile, deviceFileName, tempDirectory, "22185452") > 0) {
				System.out.println("Config.import extract file " + deviceFileName + " failed.");
				break;
			}
			
			// Process device table
			TableData deviceData = new TableData();
			try {
				CsvReader products = new CsvReader(new InputStreamReader(new FileInputStream(tempDeviceFile), "UTF-8"));
				//products.readHeaders();		// it can be used to locate any cell for each record
				
				products.readRecord();
				String tableName = products.getValues()[0];
				//String tableVersion = products.getValues()[1];
				deviceData.setName(tableName);
				
				List<List<String>> columns = new ArrayList<List<String>>();
				products.readRecord();
				List<String> columnNameList = Arrays.asList(products.getValues());		// read column name
				products.readRecord();
				List<String> columnTypeList = Arrays.asList(products.getValues());		// read column type
				products.readRecord();
				List<String> columnSizeList = Arrays.asList(products.getValues());		// read column size
				for (int i = 0; i < columnNameList.size(); i++) {
					columns.add(Arrays.asList(new String[]{columnNameList.get(i), columnTypeList.get(i), columnSizeList.get(i)}));
				}
				deviceData.setColumns(columns);
				
				List<List<String>> rows = new ArrayList<List<String>>();
				while (products.readRecord()) {
					rows.add(Arrays.asList(products.getValues()));
				}
				deviceData.setRows(rows);
				
				products.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (DeviceTable.importData(deviceData)) {
			} else {
				System.out.println("Config.import process device table failed.");
				break;
			}
			
			// Process general settings
			if (JTools.copyFile(tempGeneralFile, generalPropertyFile)) {
				System.out.println("Config.import copy to overwrite " + generalPropertyFile + " success.");
			} else {
				System.out.println("Config.import copy to overwrite " + generalPropertyFile + " failed.");
				break;
			}
			
			// Process log4j settings
			if (JTools.copyFile(tempLog4jFile, log4jPropertyFile)) {
				System.out.println("Config.import copy to overwrite " + log4jPropertyFile + " success.");
			} else {
				System.out.println("Config.import copy to overwrite " + log4jPropertyFile + " failed.");
				break;
			}
			
			// Process alarm settings
			if (JTools.copyFile(tempAlarmFile, alarmPropertyFile)) {
				System.out.println("Config.import copy to overwrite " + alarmPropertyFile + " success.");
			} else {
				System.out.println("Config.import copy to overwrite " + alarmPropertyFile + " failed.");
				break;
			}
			
			result = true;
		}
		while (false);
		new File(tempGeneralFile).delete();
		new File(tempLog4jFile).delete();
		new File(tempAlarmFile).delete();
		new File(tempDeviceFile).delete();
		new File(tempDirectory).delete();			// folder will not be deleted if there exists some other files
		
		return result;
	}
	
	//====================================================================================================
	
	public static String getDeviceAddLastIP() {
		return General.getDeviceAddLastIP();
	}
	
	public static void setDeviceAddLastIP(final String ip) {		// TODO check input value is valid
		General.setDeviceAddLastIP(ip);
	}
	
	public static String[] getDeviceScanRange() {
		return General.getDeviceScanRange();
	}
	
	public static void setDeviceScanRange(final String from, final String to) {
		General.setDeviceScanRange(from, to);
	}
	
	public static int getRealtimeChartTimeout() {
		return General.getRealtimeChartTimeout();
	}
	
	public static void setRealtimeChartTimeout(final int timeout) {
		General.setRealtimeChartTimeout(timeout);
	}
	
	public static String getTopologyTopDevice() {
		return General.getTopologyTopDevice();
	}
	
	public static void setTopologyTopDevice(final String ip) {
		General.setTopologyTopDevice(ip);
	}
	
	public static int getRemoteWebPort() {
		return General.getRemoteWebPort();
	}
	
	public static void setRemoteWebPort(final int port) {
		General.setRemoteWebPort(port);
	}
	
	public static int getLoginUserMax() {
		return General.getLoginUserMax();
	}
	
	public static void setLoginUserMax(final int num) {
		General.setLoginUserMax(num);
	}
	
	public static SmtpConfig getSmtp1() {
		return General.getSmtp1();
	}
	
	public static void setSmtp1(final SmtpConfig config) {
		General.setSmtp1(config);
	}
	
	public static MailConfig getMailConfig() {
		return General.getMailConfig();
	}
	
	public static void setMailConfig(final MailConfig config) {
		General.setMailConfig(config);
	}
	
	public static boolean isMailQueueEnabled() {
		return General.isMailQueueEnabled();
	}
	
	public static void setMailQueueEnable(boolean enable) {
		General.setMailQueueEnable(enable);
	}
	
	public static long getMailQueueInterval() {
		return General.getMailQueueInterval();
	}
	
	public static void setMailQueueInterval(long interval) {
		General.setMailQueueInterval(interval);
	}
	
	public static ShortMessageConfig getSms1Config() {
		return General.getSms1Config();
	}
	
	public static void setSms1Config(ShortMessageConfig config) {
		General.setSms1Config(config);
	}
	
	public static boolean isSmsQueueEnabled() {
		return General.isSmsQueueEnabled();
	}
	
	public static void setSmsQueueEnable(boolean enable) {
		General.setSmsQueueEnable(enable);
	}
	
	public static long getSmsQueueInterval() {
		return General.getSmsQueueInterval();
	}
	
	public static void setSmsQueueInterval(long interval) {
		General.setSmsQueueInterval(interval);
	}
	
	public static String getNmsType() {
		return General.getNmsType();
	}
	
	public static void setNmsType(final String type) {
		General.setNmsType(type);
	}
	
	public static RemoteServiceConfig getRemoteServiceConfig() {
		return General.getRemoteServiceConfig();
	}
	
	public static void setRemoteServiceConfig(final RemoteServiceConfig config) {
		General.setRemoteServiceConfig(config);
	}
	
	public static String getReportDailyMail() {
		return General.getReportDailyMail();
	}
	
	public static void setReportDailyMail(final String dailyMail) {
		General.setReportDailyMail(dailyMail);
	}
	
	public static String getReportWeeklyMail() {
		return General.getReportWeeklyMail();
	}
	
	public static void setReportWeeklyMail(final String WeeklyMail) {
		General.setReportWeeklyMail(WeeklyMail);
	}
	
	public static String getReportMonthlyMail() {
		return General.getReportMonthlyMail();
	}
	
	public static void setReportMonthlyMail(final String MonthlyMail) {
		General.setReportMonthlyMail(MonthlyMail);
	}
	
	public static String getLocationAddress() {
		return General.getLocationAddress();
	}
	
	public static void setLocationAddress(final String Address) {
		General.setLocationAddress(Address);
	}
	
	//====================================================================================================
	
	public static AlarmConfig getAlarmMailConfig() {
		return Alarm.getAlarmMailConfig();
	}
	
	public static void setAlarmMailConfig(AlarmConfig config) {
		Alarm.setAlarmMailConfig(config);
	}
	
	public static AlarmConfig getAlarmSmsConfig() {
		return Alarm.getAlarmSmsConfig();
	}
	
	public static void setAlarmSmsConfig(AlarmConfig config) {
		Alarm.setAlarmSmsConfig(config);
	}
	
	public static boolean isProjectStartupMailAlarm() {
		return Alarm.isProjectStartupMailEnabled();
	}
	
	public static boolean isProjectStartupSmsAlarm() {
		return Alarm.isProjectStartupSmsEnabled();
	}
	
	public static boolean isProjectShutdownMailAlarm() {
		return Alarm.isProjectShutdownMailEnabled();
	}
	
	public static boolean isProjectShutdownSmsAlarm() {
		return Alarm.isProjectShutdownSmsEnabled();
	}
	
	public static boolean isDeviceDisconnectMailAlarm() {
		return Alarm.isDeviceDisconnectMailEnabled();
	}
	
	public static boolean isDeviceDisconnectSmsAlarm() {
		return Alarm.isDeviceDisconnectSmsEnabled();
	}
	
	//====================================================================================================
	
	public static void setConsoleLogEnable(boolean enable) {
		Log4j.setConsoleEnable(enable);
	}
	
	public void setFileLogEnable(boolean enable) {
		Log4j.setFileEnable(enable);
	}
	
	public void setMailLogEnable(boolean enable) {
		Log4j.setMailEnable(enable);
	}
	
	public void setDbLogEnable(boolean enable) {
		Log4j.setDbEnable(enable);
	}
	
	//====================================================================================================
	
	public static boolean exportDeviceListToFile() {
		// Export device list and save to a CSV format file
		new File(exportDeviceListFileAbsolutePath).delete();
		
		TableData deviceData = JNetwork.exportDeviceList();
		
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(exportDeviceListFileAbsolutePath, true), ',');
			
			csvOutput.write("* Required field");
			csvOutput.endRecord();
			
			for (String item : new String[]{"* IP Address", "Alias Name", "SNMP Port", "SNMP Version", "SNMP Community"}) {
				csvOutput.write(item);
			}
			csvOutput.endRecord();
			
			for (List<String> record : deviceData.getRows()) {			// write out all records
				for (String data : record) {
					csvOutput.write(data);
				}
				csvOutput.endRecord();
			}

			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
			new File(exportDeviceListFileAbsolutePath).delete();
			return false;
		}
		
		return new File(exportDeviceListFileAbsolutePath).isFile();
	}
}
