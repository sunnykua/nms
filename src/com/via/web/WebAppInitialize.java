package com.via.web;

import java.io.File;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import com.via.database.MailQueue;
import com.via.database.ShortMessageQueue;
import com.via.model.JAlarm;
import com.via.model.JNetwork;
import com.via.model.JSystem;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;
import com.via.system.Config;

/**
 * Application Lifecycle Listener implementation class WebAppInitialize
 *
 */
@WebListener
public class WebAppInitialize implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass());
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    
    /**
     * Default constructor. 
     */
    public WebAppInitialize() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
    	ServletContext context = sce.getServletContext();
    	String projectHome = context.getRealPath("/");
    	projectHome = projectHome.substring(0, projectHome.length() - 1);		// cut out the last separator
    	String webappHome = System.getProperty("catalina.home") + File.separator + "webapps";
    	System.out.println("Project Home: " + projectHome);
    	System.out.println("WebApp Home:" + webappHome);
    	
    	System.out.println("============================================================ System");
    	JSystem.setWebappHome(webappHome);
    	JSystem.setProjectHome(projectHome);
    	
    	System.out.println("============================================================ Config");
    	new Config();
    	
    	System.out.println("============================================================ MailQueue");
    	new MailQueue();
    	System.out.print("queue: " + (Config.isMailQueueEnabled()?"enable":"disable\n"));
    	if (Config.isMailQueueEnabled()) {
    		MailQueue.setInterval(Config.getMailQueueInterval());
    		System.out.println(", status: " + (MailQueue.start()?"running":"stopped"));
    	}
    	
    	System.out.println("============================================================ SMS Queue");
    	new ShortMessageQueue();
    	System.out.print("queue: " + (Config.isSmsQueueEnabled()?"enable":"disable\n"));
    	if (Config.isSmsQueueEnabled()) {
    		ShortMessageQueue.setInterval(Config.getSmsQueueInterval());
    		System.out.println(", status: " + (ShortMessageQueue.start()?"running":"stopped"));
    	}
    	
    	System.out.println("============================================================ JNetwork");
    	JNetwork network = new JNetwork();
    	context.setAttribute("network", network);
    	
    	System.out.println("============================================================ JAlarm");
    	new JAlarm();
    	
        logger.error("Device IP = " + JSystem.ethAddress + " VIA CyberExpert Start Up");        
        if (JAlarm.getAlarmInfo()[0].equals("1") && !JAlarm.getNmsStartUpSendEmailList().isEmpty()) {   //NmsStartUpSendEmail
        	String result = JNetwork.setAlarmEmailStr(JAlarm.getNmsStartUpSendEmailList());
        	JAlarm.doEmailSend(sdf.format(new Date()) + " Device IP = " + JSystem.ethAddress + " VIA CyberExpert Start Up", result);
        }
        if (JAlarm.getAlarmInfo()[7].equals("1") && !JAlarm.getNmsStartUpSendSmsList().isEmpty()) {   //NmsStartUpSendSms
        	String result = JNetwork.setAlarmSmsStr(JAlarm.getNmsStartUpSendSmsList());
        	JAlarm.doSMSSend(sdf.format(new Date()) +
        			" VIA CyberExpert SMS Message: " + " Device IP = " + JSystem.ethAddress + " VIA CyberExpert Start Up.", result);
        }
        
        if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
			RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
			if (Service != null) {
				try {
					Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + JSystem.ethAddress + " VIA CyberExpert Start Up");
				} catch (RemoteException e) {
					System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
					//e.printStackTrace();
				}
			}
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    	ServletContext context = sce.getServletContext();
    	JNetwork network = (JNetwork) context.getAttribute("network");
    	
    	logger.debug("Try to cancel scheduled tasks.");
    	network.cancelScheduleTimer();
    	
    	logger.debug("Try to stop Scan-IP thread.");
    	network.stopScanIpThread();
    	
    	logger.debug("Try to cancel the SNMP Trap Receiver in network.");
    	network.stopTrapReceiver();

		logger.debug("Try to cancel the Trap Port in network.");
		network.stopTrapPort();
    	
		logger.error("Device IP = " + JSystem.ethAddress + " VIA CyberExpert Shut Down.");        
		if (JAlarm.getAlarmInfo()[1].equals("1") && !JAlarm.getNmsShutDownSendEmailList().isEmpty()) {   //NmsShutDownSendEmail
			String result = JNetwork.setAlarmEmailStr(JAlarm.getNmsShutDownSendEmailList());
			JAlarm.doEmailSend(sdf.format(new Date()) + " Device IP = " + JSystem.ethAddress + " VIA CyberExpert Shut Down", result);
		}
		if (JAlarm.getAlarmInfo()[8].equals("1") && !JAlarm.getNmsShutDownSendSmsList().isEmpty()) {   //NmsShutDownSendSms
			String result = JNetwork.setAlarmSmsStr(JAlarm.getNmsShutDownSendSmsList());
			JAlarm.doSMSSend(sdf.format(new Date()) +
					" VIA CyberExpert SMS Message: "+" Device IP = " + JSystem.ethAddress + " VIA CyberExpert Shut Down.", result);
		}
        
        if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
			RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
			if (Service != null) {
				try {
					Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + JSystem.ethAddress + " VIA CyberExpert Shut Down.");
				} catch (RemoteException e) {
					System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
					//e.printStackTrace();
				}
			}
		}
    	
    	logger.debug("Try to disconnect database.");
        network.disconnectDatabase();
        
        logger.debug("Try to stop Mail Queue.");
        MailQueue.stop();
        
        logger.debug("Try to stop SMS Queue.");
        ShortMessageQueue.stop();

		logger.debug("Try to cancel the Remote Service in network.");
		network.stopRemoteService();
    }
}
