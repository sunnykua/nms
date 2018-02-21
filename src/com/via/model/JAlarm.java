package com.via.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.PropertyConfigurator;

import com.via.system.Config;

public class JAlarm {
    private static Properties props;
    private static String localPropertiesFile;
	
	//Alarm Flag
    private static String NmsStartUpSendEmail = "";
    private static String NmsShutDownSendEmail = "";
    private static String DeviceDisconnectSendEmail = "";
    private static String MonitoredPortLinkUpSendEmail = "";
    private static String MonitoredPortLinkDownSendEmail = "";
    private static String CriticalPortLinkUpSendEmail = "";
    private static String CriticalPortLinkDownSendEmail = "";
    private static String NmsStartUpSendSms = "";
    private static String NmsShutDownSendSms = "";
    private static String DeviceDisconnectSendSms = "";
    private static String MonitoredPortLinkUpSendSms = "";
	private static String MonitoredPortLinkDownSendSms = "";
	private static String CriticalPortLinkUpSendSms = "";
	private static String CriticalPortLinkDownSendSms = "";
    private static String WebUpdatedSendEmail = "";
    private static String WebUpdatedSendSms = "";
    private static String ManagementFailSendEmail = "";
    private static String ManagementFailSendSms = "";
    
	//Alarm classify
    private static String NmsStartUpSendEmailList = "";
    private static String NmsShutDownSendEmailList = "";
    private static String DeviceDisconnectSendEmailList = "";
    private static String MonitoredPortLinkUpSendEmailList = "";
    private static String MonitoredPortLinkDownSendEmailList = "";
    private static String CriticalPortLinkUpSendEmailList = "";
    private static String CriticalPortLinkDownSendEmailList = "";
    private static String NmsStartUpSendSmsList = "";
    private static String NmsShutDownSendSmsList = "";
    private static String DeviceDisconnectSendSmsList = "";
    private static String MonitoredPortLinkUpSendSmsList = "";
	private static String MonitoredPortLinkDownSendSmsList = "";
	private static String CriticalPortLinkUpSendSmsList = "";
	private static String CriticalPortLinkDownSendSmsList = "";
    private static String WebUpdatedSendEmailList = "";
    private static String WebUpdatedSendSmsList = "";
    private static String ManagementFailSendEmailList = "";
    private static String ManagementFailSendSmsList = "";
    
    private static String NmsStartUpSendEmailCheck = "";
    private static String NmsShutDownSendEmailCheck = "";
    private static String DeviceDisconnectSendEmailCheck = "";
    private static String MonitoredPortLinkUpSendEmailCheck = "";
    private static String MonitoredPortLinkDownSendEmailCheck = "";
    private static String CriticalPortLinkUpSendEmailCheck = "";
    private static String CriticalPortLinkDownSendEmailCheck = "";
    private static String NmsStartUpSendSmsCheck = "";
    private static String NmsShutDownSendSmsCheck = "";
    private static String DeviceDisconnectSendSmsCheck = "";
    private static String MonitoredPortLinkUpSendSmsCheck = "";
	private static String MonitoredPortLinkDownSendSmsCheck = "";
	private static String CriticalPortLinkUpSendSmsCheck = "";
	private static String CriticalPortLinkDownSendSmsCheck = "";
    private static String WebUpdatedSendEmailCheck = "";
    private static String WebUpdatedSendSmsCheck = "";
    private static String ManagementFailSendEmailCheck = "";
    private static String ManagementFailSendSmsCheck = "";
    
    static {
        props = new Properties() {
            private static final long serialVersionUID = 1L;

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));        //sort all keys in descent order, is used for output file
            }
        };
        localPropertiesFile = JSystem.projectSpace + "/networkmanagement.JAlarm.properties";
        if (new File(localPropertiesFile).isFile()) {
            System.out.println("[JAlarm]Local properties is found and used as current configuration.");
//            logger.debug("[JAlarm]Local properties is found and used as current configuration.");

            loadLocalProperties();
            PropertyConfigurator.configure(props);
        }
        else {
            System.out.println("[JAlarm]New local properties is creating and using default configuration.");
//            logger.debug("[JAlarm]New local properties is creating and using default configuration.");

            // Load the default properties
            props.setProperty("JAlarm.NmsStartUpSendEmail", NmsStartUpSendEmail);
            props.setProperty("JAlarm.NmsShutDownSendEmail", NmsShutDownSendEmail);
            props.setProperty("JAlarm.DeviceDisconnectSendEmail", DeviceDisconnectSendEmail);
            props.setProperty("JAlarm.MonitoredPortLinkUpSendEmail", MonitoredPortLinkUpSendEmail);
            props.setProperty("JAlarm.MonitoredPortLinkDownSendEmail", MonitoredPortLinkDownSendEmail);
            props.setProperty("JAlarm.CriticalPortLinkUpSendEmail", CriticalPortLinkUpSendEmail);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendEmail", CriticalPortLinkDownSendEmail);
           	props.setProperty("JAlarm.NmsStartUpSendSms", NmsStartUpSendSms);
           	props.setProperty("JAlarm.NmsShutDownSendSms", NmsShutDownSendSms);
           	props.setProperty("JAlarm.DeviceDisconnectSendSms", DeviceDisconnectSendSms);
           	props.setProperty("JAlarm.MonitoredPortLinkUpSendSms", MonitoredPortLinkUpSendSms);
           	props.setProperty("JAlarm.MonitoredPortLinkDownSendSms", MonitoredPortLinkDownSendSms);
           	props.setProperty("JAlarm.CriticalPortLinkUpSendSms", CriticalPortLinkUpSendSms);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendSms", CriticalPortLinkDownSendSms);
           	props.setProperty("JAlarm.WebUpdatedSendEmail", WebUpdatedSendEmail);
           	props.setProperty("JAlarm.WebUpdatedSendSms", WebUpdatedSendSms);
           	props.setProperty("JAlarm.ManagementFailSendEmailCheck", ManagementFailSendEmail);
           	props.setProperty("JAlarm.ManagementFailSendSmsCheck", ManagementFailSendSms);

        	//Alarm classify
            props.setProperty("JAlarm.NmsStartUpSendEmailList", NmsStartUpSendEmailList);
            props.setProperty("JAlarm.NmsShutDownSendEmailList", NmsShutDownSendEmailList);
            props.setProperty("JAlarm.DeviceDisconnectSendEmailList", DeviceDisconnectSendEmailList);
            props.setProperty("JAlarm.MonitoredPortLinkUpSendEmailList", MonitoredPortLinkUpSendEmailList);
            props.setProperty("JAlarm.MonitoredPortLinkDownSendEmailList", MonitoredPortLinkDownSendEmailList);
            props.setProperty("JAlarm.CriticalPortLinkUpSendEmailList", CriticalPortLinkUpSendEmailList);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendEmailList", CriticalPortLinkDownSendEmailList);
           	props.setProperty("JAlarm.NmsStartUpSendSmsList", NmsStartUpSendSmsList);
           	props.setProperty("JAlarm.NmsShutDownSendSmsList", NmsShutDownSendSmsList);
           	props.setProperty("JAlarm.DeviceDisconnectSendSmsList", DeviceDisconnectSendSmsList);
           	props.setProperty("JAlarm.MonitoredPortLinkUpSendSmsList", MonitoredPortLinkUpSendSmsList);
           	props.setProperty("JAlarm.MonitoredPortLinkDownSendSmsList", MonitoredPortLinkDownSendSmsList);
           	props.setProperty("JAlarm.CriticalPortLinkUpSendSmsList", CriticalPortLinkUpSendSmsList);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendSmsList", CriticalPortLinkDownSendSmsList);
           	props.setProperty("JAlarm.WebUpdatedSendEmailList", WebUpdatedSendEmailList);
           	props.setProperty("JAlarm.WebUpdatedSendSmsList", WebUpdatedSendSmsList);
           	props.setProperty("JAlarm.ManagementFailSendEmailCheck", ManagementFailSendEmailList);
           	props.setProperty("JAlarm.ManagementFailSendSmsCheck", ManagementFailSendSmsList);
           	
            props.setProperty("JAlarm.NmsStartUpSendEmailCheck", NmsStartUpSendEmailCheck);
            props.setProperty("JAlarm.NmsShutDownSendEmailCheck", NmsShutDownSendEmailCheck);
            props.setProperty("JAlarm.DeviceDisconnectSendEmailCheck", DeviceDisconnectSendEmailCheck);
            props.setProperty("JAlarm.MonitoredPortLinkUpSendEmailCheck", MonitoredPortLinkUpSendEmailCheck);
            props.setProperty("JAlarm.MonitoredPortLinkDownSendEmailCheck", MonitoredPortLinkDownSendEmailCheck);
            props.setProperty("JAlarm.CriticalPortLinkUpSendEmailCheck", CriticalPortLinkUpSendEmailCheck);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendEmailCheck", CriticalPortLinkDownSendEmailCheck);
           	props.setProperty("JAlarm.NmsStartUpSendSmsCheck", NmsStartUpSendSmsCheck);
           	props.setProperty("JAlarm.NmsShutDownSendSmsCheck", NmsShutDownSendSmsCheck);
           	props.setProperty("JAlarm.DeviceDisconnectSendSmsCheck", DeviceDisconnectSendSmsCheck);
           	props.setProperty("JAlarm.MonitoredPortLinkUpSendSmsCheck", MonitoredPortLinkUpSendSmsCheck);
           	props.setProperty("JAlarm.MonitoredPortLinkDownSendSmsCheck", MonitoredPortLinkDownSendSmsCheck);
           	props.setProperty("JAlarm.CriticalPortLinkUpSendSmsCheck", CriticalPortLinkUpSendSmsCheck);
           	props.setProperty("JAlarm.CriticalPortLinkDownSendSmsCheck", CriticalPortLinkDownSendSmsCheck);
           	props.setProperty("JAlarm.WebUpdatedSendEmailCheck", WebUpdatedSendEmailCheck);
           	props.setProperty("JAlarm.WebUpdatedSendSmsCheck", WebUpdatedSendSmsCheck);
           	props.setProperty("JAlarm.ManagementFailSendEmailCheck", ManagementFailSendEmailCheck);
           	props.setProperty("JAlarm.ManagementFailSendSmsCheck", ManagementFailSendSmsCheck);
           	
            storeLocalProperties();
        }
        System.out.println("[JAlarm]local properties: " + localPropertiesFile);

//        System.out.println(String.format("[JAlarm]console:%b, file:%b, mail:%b, db:%b", consoleEnable, fileEnable, mailEnable, dbEnable));
    }
    
    
    
    public static void loadLocalProperties() {
        InputStream input = null;
        try {
            input = new FileInputStream(localPropertiesFile);
            props.load(input);
        }
        catch (FileNotFoundException e) {
            System.out.println("[JAlarm]File not found when loading local properties.");
        }
        catch (IOException e) {
            System.out.println("[JAlarm]IO exceptiond when loading local properties.");
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                }
            }
        }
    }
    
    
    private static void storeLocalProperties() {
        OutputStream output = null;
        try {
            output =  new FileOutputStream(localPropertiesFile);
            props.store(output, null);
        }
        catch (FileNotFoundException e) {
            System.out.println("[JAlarm]File not found when storing local properties.");
        }
        catch (IOException e) {
            System.out.println("[JAlarm]IO exception when storing local properties.");
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
   	
    public static void setAlarmInfo(final String NmsStartUpSendEmail, final String NmsShutDownSendEmail, 
    		final String DeviceDisconnectSendEmail, final String ManagementFailSendEmail,
    		final String MonitoredPortLinkUpSendEmail, final String MonitoredPortLinkDownSendEmail, 
    		final String CriticalPortLinkUpSendEmail, final String CriticalPortLinkDownSendEmail,
    		final String WebUpdatedSendEmail,
    		final String NmsStartUpSendSms, final String NmsShutDownSendSms,
    		final String DeviceDisconnectSendSms, final String ManagementFailSendSms,
    		final String MonitoredPortLinkUpSendSms, final String MonitoredPortLinkDownSendSms,
    		final String CriticalPortLinkUpSendSms, final String CriticalPortLinkDownSendSms,
    		final String WebUpdatedSendSms
    		) {
        if (NmsStartUpSendEmail != null) props.setProperty("JAlarm.NmsStartUpSendEmail", NmsStartUpSendEmail);
        if (NmsShutDownSendEmail != null) props.setProperty("JAlarm.NmsShutDownSendEmail", NmsShutDownSendEmail);
        if (DeviceDisconnectSendEmail != null) props.setProperty("JAlarm.DeviceDisconnectSendEmail", DeviceDisconnectSendEmail);
        if (ManagementFailSendEmail != null) props.setProperty("JAlarm.ManagementFailSendEmail", ManagementFailSendEmail);
        if (MonitoredPortLinkUpSendEmail != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendEmail", MonitoredPortLinkUpSendEmail);
        if (MonitoredPortLinkDownSendEmail != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendEmail", MonitoredPortLinkDownSendEmail);
        if (CriticalPortLinkUpSendEmail != null) props.setProperty("JAlarm.CriticalPortLinkUpSendEmail", CriticalPortLinkUpSendEmail);
        if (CriticalPortLinkDownSendEmail != null) props.setProperty("JAlarm.CriticalPortLinkDownSendEmail", CriticalPortLinkDownSendEmail);
        if (WebUpdatedSendEmail != null) props.setProperty("JAlarm.WebUpdatedSendEmail", WebUpdatedSendEmail);

        if (NmsStartUpSendSms != null) props.setProperty("JAlarm.NmsStartUpSendSms", NmsStartUpSendSms);
        if (NmsShutDownSendSms != null) props.setProperty("JAlarm.NmsShutDownSendSms", NmsShutDownSendSms);
        if (DeviceDisconnectSendSms != null) props.setProperty("JAlarm.DeviceDisconnectSendSms", DeviceDisconnectSendSms);
        if (ManagementFailSendSms != null) props.setProperty("JAlarm.ManagementFailSendSms", ManagementFailSendSms);
        if (MonitoredPortLinkUpSendSms != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendSms", MonitoredPortLinkUpSendSms);
        if (MonitoredPortLinkDownSendSms != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendSms", MonitoredPortLinkDownSendSms);
        if (CriticalPortLinkUpSendSms != null) props.setProperty("JAlarm.CriticalPortLinkUpSendSms", CriticalPortLinkUpSendSms);
        if (CriticalPortLinkDownSendSms != null) props.setProperty("JAlarm.CriticalPortLinkDownSendSms", CriticalPortLinkDownSendSms);
        if (WebUpdatedSendSms != null) props.setProperty("JAlarm.WebUpdatedSendSms", WebUpdatedSendSms);
        
        storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static String[] getAlarmInfo() {
        String NmsStartUpSendEmail = props.getProperty("JAlarm.NmsStartUpSendEmail", "");
        String NmsShutDownSendEmail = props.getProperty("JAlarm.NmsShutDownSendEmail", "");
        String DeviceDisconnectSendEmail = props.getProperty("JAlarm.DeviceDisconnectSendEmail", "");
        String MonitoredPortLinkUpSendEmail = props.getProperty("JAlarm.MonitoredPortLinkUpSendEmail", "");
        String MonitoredPortLinkDownSendEmail = props.getProperty("JAlarm.MonitoredPortLinkDownSendEmail", "");
        String CriticalPortLinkUpSendEmail = props.getProperty("JAlarm.CriticalPortLinkUpSendEmail", "");
        String CriticalPortLinkDownSendEmail = props.getProperty("JAlarm.CriticalPortLinkDownSendEmail", "");
        String NmsStartUpSendSms = props.getProperty("JAlarm.NmsStartUpSendSms", "");
        String NmsShutDownSendSms = props.getProperty("JAlarm.NmsShutDownSendSms", "");
        String DeviceDisconnectSendSms = props.getProperty("JAlarm.DeviceDisconnectSendSms", "");
        String MonitoredPortLinkUpSendSms = props.getProperty("JAlarm.MonitoredPortLinkUpSendSms", "");
        String MonitoredPortLinkDownSendSms = props.getProperty("JAlarm.MonitoredPortLinkDownSendSms", "");
        String CriticalPortLinkUpSendSms = props.getProperty("JAlarm.CriticalPortLinkUpSendSms", "");
        String CriticalPortLinkDownSendSms = props.getProperty("JAlarm.CriticalPortLinkDownSendSms", "");
        String WebUpdatedSendEmail = props.getProperty("JAlarm.WebUpdatedSendEmail", "");
        String WebUpdatedSendSms = props.getProperty("JAlarm.WebUpdatedSendSms", "");
        String ManagementFailSendEmail = props.getProperty("JAlarm.ManagementFailSendEmail", "");
        String ManagementFailSendSms = props.getProperty("JAlarm.ManagementFailSendSms", "");
        
        return new String[]{NmsStartUpSendEmail, NmsShutDownSendEmail, DeviceDisconnectSendEmail,
        		MonitoredPortLinkUpSendEmail, MonitoredPortLinkDownSendEmail, CriticalPortLinkUpSendEmail,
        		CriticalPortLinkDownSendEmail,
        		NmsStartUpSendSms, NmsShutDownSendSms,
        		DeviceDisconnectSendSms, MonitoredPortLinkUpSendSms, MonitoredPortLinkDownSendSms,
        		CriticalPortLinkUpSendSms, CriticalPortLinkDownSendSms,
        		WebUpdatedSendEmail, WebUpdatedSendSms,
        		ManagementFailSendEmail, ManagementFailSendSms
        		};
    }
    
    public static void setAlarmList(final String NmsStartUpSendEmailList, final String NmsShutDownSendEmailList, final String DeviceDisconnectSendEmailList, 
    		final String MonitoredPortLinkUpSendEmailList, final String MonitoredPortLinkDownSendEmailList, final String CriticalPortLinkUpSendEmailList, 
    		final String CriticalPortLinkDownSendEmailList,
    		final String WebUpdatedSendEmailList,
    		final String ManagementFailSendEmailList,
    		final String NmsStartUpSendSmsList, final String NmsShutDownSendSmsList,
    		final String DeviceDisconnectSendSmsList, final String MonitoredPortLinkUpSendSmsList, final String MonitoredPortLinkDownSendSmsList,
    		final String CriticalPortLinkUpSendSmsList, final String CriticalPortLinkDownSendSmsList,
    		final String WebUpdatedSendSmsList,
    		final String ManagementFailSendSmsList
    		) {
        if (NmsStartUpSendEmailList != null) props.setProperty("JAlarm.NmsStartUpSendEmailList", NmsStartUpSendEmailList);
        if (NmsShutDownSendEmailList != null) props.setProperty("JAlarm.NmsShutDownSendEmailList", NmsShutDownSendEmailList);
        if (DeviceDisconnectSendEmailList != null) props.setProperty("JAlarm.DeviceDisconnectSendEmailList", DeviceDisconnectSendEmailList);
        if (MonitoredPortLinkUpSendEmailList != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendEmailList", MonitoredPortLinkUpSendEmailList);
        if (MonitoredPortLinkDownSendEmailList != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendEmailList", MonitoredPortLinkDownSendEmailList);
        if (CriticalPortLinkUpSendEmailList != null) props.setProperty("JAlarm.CriticalPortLinkUpSendEmailList", CriticalPortLinkUpSendEmailList);
        if (CriticalPortLinkDownSendEmailList != null) props.setProperty("JAlarm.CriticalPortLinkDownSendEmailList", CriticalPortLinkDownSendEmailList);
        if (NmsStartUpSendSmsList != null) props.setProperty("JAlarm.NmsStartUpSendSmsList", NmsStartUpSendSmsList);
        if (NmsShutDownSendSmsList != null) props.setProperty("JAlarm.NmsShutDownSendSmsList", NmsShutDownSendSmsList);
        if (DeviceDisconnectSendSmsList != null) props.setProperty("JAlarm.DeviceDisconnectSendSmsList", DeviceDisconnectSendSmsList);
        if (MonitoredPortLinkUpSendSmsList != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendSmsList", MonitoredPortLinkUpSendSmsList);
        if (MonitoredPortLinkDownSendSmsList != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendSmsList", MonitoredPortLinkDownSendSmsList);
        if (CriticalPortLinkUpSendSmsList != null) props.setProperty("JAlarm.CriticalPortLinkUpSendSmsList", CriticalPortLinkUpSendSmsList);
        if (CriticalPortLinkDownSendSmsList != null) props.setProperty("JAlarm.CriticalPortLinkDownSendSmsList", CriticalPortLinkDownSendSmsList);
        if (WebUpdatedSendEmailList != null) props.setProperty("JAlarm.WebUpdatedSendEmailList", WebUpdatedSendEmailList);
        if (WebUpdatedSendSmsList != null) props.setProperty("JAlarm.WebUpdatedSendSmsList", WebUpdatedSendSmsList);
        if (ManagementFailSendEmailList != null) props.setProperty("JAlarm.ManagementFailSendEmailList", ManagementFailSendEmailList);
        if (ManagementFailSendSmsList != null) props.setProperty("JAlarm.ManagementFailSendSmsList", ManagementFailSendSmsList);
        
        storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setNmsStartUpSendEmailList(final String NmsStartUpSendEmailList) {
    	if (NmsStartUpSendEmailList != null) props.setProperty("JAlarm.NmsStartUpSendEmailList", NmsStartUpSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setNmsShutDownSendEmailList(final String NmsShutDownSendEmailList) {
    	if (NmsShutDownSendEmailList != null) props.setProperty("JAlarm.NmsShutDownSendEmailList", NmsShutDownSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setDeviceDisconnectSendEmailList(final String DeviceDisconnectSendEmailList) {
    	if (DeviceDisconnectSendEmailList != null) props.setProperty("JAlarm.DeviceDisconnectSendEmailList", DeviceDisconnectSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setMonitoredPortLinkUpSendEmailList(final String MonitoredPortLinkUpSendEmailList) {
    	if (MonitoredPortLinkUpSendEmailList != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendEmailList", MonitoredPortLinkUpSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setMonitoredPortLinkDownSendEmailList(final String MonitoredPortLinkDownSendEmailList){
    	if (MonitoredPortLinkDownSendEmailList != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendEmailList", MonitoredPortLinkDownSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setCriticalPortLinkUpSendEmailList(final String CriticalPortLinkUpSendEmailList) {
    	if (CriticalPortLinkUpSendEmailList != null) props.setProperty("JAlarm.CriticalPortLinkUpSendEmailList", CriticalPortLinkUpSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setCriticalPortLinkDownSendEmailList(final String CriticalPortLinkDownSendEmailList) {
    	if (CriticalPortLinkDownSendEmailList != null) props.setProperty("JAlarm.CriticalPortLinkDownSendEmailList", CriticalPortLinkDownSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setNmsStartUpSendSmsList(final String NmsStartUpSendSmsList) {
    	if (NmsStartUpSendSmsList != null) props.setProperty("JAlarm.NmsStartUpSendSmsList", NmsStartUpSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setNmsShutDownSendSmsList(final String NmsShutDownSendSmsList) {
    	if (NmsShutDownSendSmsList != null) props.setProperty("JAlarm.NmsShutDownSendSmsList", NmsShutDownSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setDeviceDisconnectSendSmsList(final String DeviceDisconnectSendSmsList) {
    	if (DeviceDisconnectSendSmsList != null) props.setProperty("JAlarm.DeviceDisconnectSendSmsList", DeviceDisconnectSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setMonitoredPortLinkUpSendSmsList(final String MonitoredPortLinkUpSendSmsList) {
    	if (MonitoredPortLinkUpSendSmsList != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendSmsList", MonitoredPortLinkUpSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setMonitoredPortLinkDownSendSmsList(final String MonitoredPortLinkDownSendSmsList){
    	if (MonitoredPortLinkDownSendSmsList != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendSmsList", MonitoredPortLinkDownSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setCriticalPortLinkUpSendSmsList(final String CriticalPortLinkUpSendSmsList) {
    	if (CriticalPortLinkUpSendSmsList != null) props.setProperty("JAlarm.CriticalPortLinkUpSendSmsList", CriticalPortLinkUpSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setCriticalPortLinkDownSendSmsList(final String CriticalPortLinkDownSendSmsList) {
    	if (CriticalPortLinkDownSendSmsList != null) props.setProperty("JAlarm.CriticalPortLinkDownSendSmsList", CriticalPortLinkDownSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setWebUpdatedSendEmailList(final String WebUpdatedSendEmailList) {
    	if (WebUpdatedSendEmailList != null) props.setProperty("JAlarm.WebUpdatedSendEmailList", WebUpdatedSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setWebUpdatedSendSmsList(final String WebUpdatedSendSmsList){
    	if (WebUpdatedSendSmsList != null) props.setProperty("JAlarm.WebUpdatedSendSmsList", WebUpdatedSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }

    public static void setManagementFailSendEmailList(final String ManagementFailSendEmailList){
    	if (ManagementFailSendEmailList != null) props.setProperty("JAlarm.ManagementFailSendEmailList", ManagementFailSendEmailList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static void setManagementFailSendSmsList(final String ManagementFailSendSmsList){
    	if (ManagementFailSendSmsList != null) props.setProperty("JAlarm.ManagementFailSendSmsList", ManagementFailSendSmsList);
    	storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    
    public static String[] getAlarmList() {
        String NmsStartUpSendEmailList = props.getProperty("JAlarm.NmsStartUpSendEmailList", "");
        String NmsShutDownSendEmailList = props.getProperty("JAlarm.NmsShutDownSendEmailList", "");
        String DeviceDisconnectSendEmailList = props.getProperty("JAlarm.DeviceDisconnectSendEmailList", "");
        String MonitoredPortLinkUpSendEmailList = props.getProperty("JAlarm.MonitoredPortLinkUpSendEmailList", "");
        String MonitoredPortLinkDownSendEmailList = props.getProperty("JAlarm.MonitoredPortLinkDownSendEmailList", "");
        String CriticalPortLinkUpSendEmailList = props.getProperty("JAlarm.CriticalPortLinkUpSendEmailList", "");
        String CriticalPortLinkDownSendEmailList = props.getProperty("JAlarm.CriticalPortLinkDownSendEmailList", "");
        String NmsStartUpSendSmsList = props.getProperty("JAlarm.NmsStartUpSendSmsList", "");
        String NmsShutDownSendSmsList = props.getProperty("JAlarm.NmsShutDownSendSmsList", "");
        String DeviceDisconnectSendSmsList = props.getProperty("JAlarm.DeviceDisconnectSendSmsList", "");
        String MonitoredPortLinkUpSendSmsList = props.getProperty("JAlarm.MonitoredPortLinkUpSendSmsList", "");
        String MonitoredPortLinkDownSendSmsList = props.getProperty("JAlarm.MonitoredPortLinkDownSendSmsList", "");
        String CriticalPortLinkUpSendSmsList = props.getProperty("JAlarm.CriticalPortLinkUpSendSmsList", "");
        String CriticalPortLinkDownSendSmsList = props.getProperty("JAlarm.CriticalPortLinkDownSendSmsList", "");
        String WebUpdatedSendEmailList = props.getProperty("JAlarm.WebUpdatedSendEmailList", "");
        String WebUpdatedSendSmsList = props.getProperty("JAlarm.WebUpdatedSendSmsList", "");
        String ManagementFailSendEmailList = props.getProperty("JAlarm.ManagementFailSendEmailList", "");
        String ManagementFailSendSmsList = props.getProperty("JAlarm.ManagementFailSendSmsList", "");
        
        return new String[]{NmsStartUpSendEmailList, NmsShutDownSendEmailList, DeviceDisconnectSendEmailList,
        		MonitoredPortLinkUpSendEmailList, MonitoredPortLinkDownSendEmailList, CriticalPortLinkUpSendEmailList,
        		CriticalPortLinkDownSendEmailList, NmsStartUpSendSmsList, NmsShutDownSendSmsList,
        		DeviceDisconnectSendSmsList, MonitoredPortLinkUpSendSmsList, MonitoredPortLinkDownSendSmsList,
        		CriticalPortLinkUpSendSmsList, CriticalPortLinkDownSendSmsList,
        		WebUpdatedSendEmailList, WebUpdatedSendSmsList,
        		ManagementFailSendEmailList, ManagementFailSendSmsList
        		};
    }
    
    public static String getNmsStartUpSendEmailList() {
    	return props.getProperty("JAlarm.NmsStartUpSendEmailList", "");
    }
    
    public static String getNmsShutDownSendEmailList() {
    	return props.getProperty("JAlarm.NmsShutDownSendEmailList", "");
    }
    
    public static String getDeviceDisconnectSendEmailList() {
    	return props.getProperty("JAlarm.DeviceDisconnectSendEmailList", "");
    }
    
    public static String getMonitoredPortLinkUpSendEmailList() {
    	return props.getProperty("JAlarm.MonitoredPortLinkUpSendEmailList", "");
    }
    
    public static String getMonitoredPortLinkDownSendEmailList() {
    	return props.getProperty("JAlarm.MonitoredPortLinkDownSendEmailList", "");
    }
    
    public static String getCriticalPortLinkUpSendEmailList() {
    	return props.getProperty("JAlarm.CriticalPortLinkUpSendEmailList", "");
    }
    
    public static String getCriticalPortLinkDownSendEmailList() {
    	return props.getProperty("JAlarm.CriticalPortLinkDownSendEmailList", "");
    }
    
    public static String getWebUpdatedSendEmailList() {
    	return props.getProperty("JAlarm.WebUpdatedSendEmailList", "");
    }
    
    public static String getManagementFailSendEmailList() {
    	return props.getProperty("JAlarm.ManagementFailSendEmailList", "");
    }
    
    public static String getNmsStartUpSendSmsList() {
    	return props.getProperty("JAlarm.NmsStartUpSendSmsList", "");
    }
    
    public static String getNmsShutDownSendSmsList() {
    	return props.getProperty("JAlarm.NmsShutDownSendSmsList", "");
    }
    
    public static String getDeviceDisconnectSendSmsList() {
    	return props.getProperty("JAlarm.DeviceDisconnectSendSmsList", "");
    }
    
    public static String getMonitoredPortLinkUpSendSmsList() {
    	return props.getProperty("JAlarm.MonitoredPortLinkUpSendSmsList", "");
    }
    
    public static String getMonitoredPortLinkDownSendSmsList() {
    	return props.getProperty("JAlarm.MonitoredPortLinkDownSendSmsList", "");
    }
    
    public static String getCriticalPortLinkUpSendSmsList() {
    	return props.getProperty("JAlarm.CriticalPortLinkUpSendSmsList", "");
    }
    
    public static String getCriticalPortLinkDownSendSmsList() {
    	return props.getProperty("JAlarm.CriticalPortLinkDownSendSmsList", "");
    }
    
    public static String getWebUpdatedSendSmsList() {
    	return props.getProperty("JAlarm.WebUpdatedSendSmsList", "");
    }
    
    public static String getManagementFailSendSmsList() {
    	return props.getProperty("JAlarm.ManagementFailSendSmsList", "");
    }
    
    public static void setAlarmCheck(final String NmsStartUpSendEmailCheck, final String NmsShutDownSendEmailCheck, final String DeviceDisconnectSendEmailCheck, 
    		final String MonitoredPortLinkUpSendEmailCheck, final String MonitoredPortLinkDownSendEmailCheck, final String CriticalPortLinkUpSendEmailCheck, 
    		final String CriticalPortLinkDownSendEmailCheck, 
    		final String WebUpdatedSendEmailCheck,
    		final String ManagementFailSendEmailCheck,
    		final String NmsStartUpSendSmsCheck, final String NmsShutDownSendSmsCheck,
    		final String DeviceDisconnectSendSmsCheck, final String MonitoredPortLinkUpSendSmsCheck, final String MonitoredPortLinkDownSendSmsCheck,
    		final String CriticalPortLinkUpSendSmsCheck, final String CriticalPortLinkDownSendSmsCheck,
    		final String WebUpdatedSendSmsCheck,
    		final String ManagementFailSendSmsCheck
    		) {
        if (NmsStartUpSendEmailCheck != null) props.setProperty("JAlarm.NmsStartUpSendEmailCheck", NmsStartUpSendEmailCheck);
        if (NmsShutDownSendEmailCheck != null) props.setProperty("JAlarm.NmsShutDownSendEmailCheck", NmsShutDownSendEmailCheck);
        if (DeviceDisconnectSendEmailCheck != null) props.setProperty("JAlarm.DeviceDisconnectSendEmailCheck", DeviceDisconnectSendEmailCheck);
        if (MonitoredPortLinkUpSendEmailCheck != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendEmailCheck", MonitoredPortLinkUpSendEmailCheck);
        if (MonitoredPortLinkDownSendEmailCheck != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendEmailCheck", MonitoredPortLinkDownSendEmailCheck);
        if (CriticalPortLinkUpSendEmailCheck != null) props.setProperty("JAlarm.CriticalPortLinkUpSendEmailCheck", CriticalPortLinkUpSendEmailCheck);
        if (CriticalPortLinkDownSendEmailCheck != null) props.setProperty("JAlarm.CriticalPortLinkDownSendEmailCheck", CriticalPortLinkDownSendEmailCheck);
        if (NmsStartUpSendSmsCheck != null) props.setProperty("JAlarm.NmsStartUpSendSmsCheck", NmsStartUpSendSmsCheck);
        if (NmsShutDownSendSmsCheck != null) props.setProperty("JAlarm.NmsShutDownSendSmsCheck", NmsShutDownSendSmsCheck);
        if (DeviceDisconnectSendSmsCheck != null) props.setProperty("JAlarm.DeviceDisconnectSendSmsCheck", DeviceDisconnectSendSmsCheck);
        if (MonitoredPortLinkUpSendSmsCheck != null) props.setProperty("JAlarm.MonitoredPortLinkUpSendSmsCheck", MonitoredPortLinkUpSendSmsCheck);
        if (MonitoredPortLinkDownSendSmsCheck != null) props.setProperty("JAlarm.MonitoredPortLinkDownSendSmsCheck", MonitoredPortLinkDownSendSmsCheck);
        if (CriticalPortLinkUpSendSmsCheck != null) props.setProperty("JAlarm.CriticalPortLinkUpSendSmsCheck", CriticalPortLinkUpSendSmsCheck);
        if (CriticalPortLinkDownSendSmsCheck != null) props.setProperty("JAlarm.CriticalPortLinkDownSendSmsCheck", CriticalPortLinkDownSendSmsCheck);
        if (WebUpdatedSendEmailCheck != null) props.setProperty("JAlarm.WebUpdatedSendEmailCheck", WebUpdatedSendEmailCheck);
        if (WebUpdatedSendSmsCheck != null) props.setProperty("JAlarm.WebUpdatedSendSmsCheck", WebUpdatedSendSmsCheck);
        if (ManagementFailSendEmailCheck != null) props.setProperty("JAlarm.ManagementFailSendEmailCheck", ManagementFailSendEmailCheck);
        if (ManagementFailSendSmsCheck != null) props.setProperty("JAlarm.ManagementFailSendSmsCheck", ManagementFailSendSmsCheck);
        
        storeLocalProperties();
        PropertyConfigurator.configure(props);
    }
    
    public static String[] getAlarmCheck() {
        String NmsStartUpSendEmailCheck = props.getProperty("JAlarm.NmsStartUpSendEmailCheck", "");
        String NmsShutDownSendEmailCheck = props.getProperty("JAlarm.NmsShutDownSendEmailCheck", "");
        String DeviceDisconnectSendEmailCheck = props.getProperty("JAlarm.DeviceDisconnectSendEmailCheck", "");
        String MonitoredPortLinkUpSendEmailCheck = props.getProperty("JAlarm.MonitoredPortLinkUpSendEmailCheck", "");
        String MonitoredPortLinkDownSendEmailCheck = props.getProperty("JAlarm.MonitoredPortLinkDownSendEmailCheck", "");
        String CriticalPortLinkUpSendEmailCheck = props.getProperty("JAlarm.CriticalPortLinkUpSendEmailCheck", "");
        String CriticalPortLinkDownSendEmailCheck = props.getProperty("JAlarm.CriticalPortLinkDownSendEmailCheck", "");
        String NmsStartUpSendSmsCheck = props.getProperty("JAlarm.NmsStartUpSendSmsCheck", "");
        String NmsShutDownSendSmsCheck = props.getProperty("JAlarm.NmsShutDownSendSmsCheck", "");
        String DeviceDisconnectSendSmsCheck = props.getProperty("JAlarm.DeviceDisconnectSendSmsCheck", "");
        String MonitoredPortLinkUpSendSmsCheck = props.getProperty("JAlarm.MonitoredPortLinkUpSendSmsCheck", "");
        String MonitoredPortLinkDownSendSmsCheck = props.getProperty("JAlarm.MonitoredPortLinkDownSendSmsCheck", "");
        String CriticalPortLinkUpSendSmsCheck = props.getProperty("JAlarm.CriticalPortLinkUpSendSmsCheck", "");
        String CriticalPortLinkDownSendSmsCheck = props.getProperty("JAlarm.CriticalPortLinkDownSendSmsCheck", "");
        String WebUpdatedSendEmailCheck = props.getProperty("JAlarm.WebUpdatedSendEmailCheck", "");
        String WebUpdatedSendSmsCheck = props.getProperty("JAlarm.WebUpdatedSendSmsCheck", "");
        String ManagementFailSendEmailCheck = props.getProperty("JAlarm.ManagementFailSendEmailCheck", "");
        String ManagementFailSendSmsCheck = props.getProperty("JAlarm.ManagementFailSendSmsCheck", "");
        
        return new String[]{NmsStartUpSendEmailCheck, NmsShutDownSendEmailCheck, DeviceDisconnectSendEmailCheck,
        		MonitoredPortLinkUpSendEmailCheck, MonitoredPortLinkDownSendEmailCheck, CriticalPortLinkUpSendEmailCheck,
        		CriticalPortLinkDownSendEmailCheck,
        		WebUpdatedSendEmailCheck, ManagementFailSendEmailCheck,        		
        		NmsStartUpSendSmsCheck, NmsShutDownSendSmsCheck,
        		DeviceDisconnectSendSmsCheck, MonitoredPortLinkUpSendSmsCheck, MonitoredPortLinkDownSendSmsCheck,
        		CriticalPortLinkUpSendSmsCheck, CriticalPortLinkDownSendSmsCheck,
        		WebUpdatedSendSmsCheck, ManagementFailSendSmsCheck
        		
        		};
    }

	public static void doSMSSend(String text, String recipients) {
		ShortMessageConfig smsConfig = Config.getSms1Config();
		String username = smsConfig.getUsername();
		String password = smsConfig.getPassword();
		int timeout = smsConfig.getTimeout();
		String[] toArray = recipients.split(",");
		
		for (String to : toArray) {
			if (ShortMessage.send(SMSProvider.SANZHU, SMSEncoding.BIG5, timeout, username, password, to, text, Config.isSmsQueueEnabled())) {
				System.out.println("Send SMS to " + to + " success.");
			}
			else {
				System.out.println("Send SMS to " + to + " FAILED.");
			}
		}
	}
	
	//EMail Function
    public static void doEmailSend(String text, String recipients) {
    	MailConfig mailConfig = Config.getMailConfig();
    	SmtpConfig smtpConfig = Config.getSmtp1();
    	String from = mailConfig.getFromLable() + " <" + smtpConfig.getUsername() + ">";
    	String subject = mailConfig.getSubject();
    	String mailHost = smtpConfig.getHost();
    	String mailPort = smtpConfig.getPort();
    	String username = smtpConfig.getUsername();
    	String password = smtpConfig.getPassword();
    	int timeout = smtpConfig.getTimeout();
    	String[] toArray = recipients.split(",");
    	
    	for (String to : toArray) {
    		System.out.println("doEmailSend, from:" + from + ", to:" + to + ", sub:" + subject + ", host:" + mailHost + ", timeout:" + timeout);
    		if (Mail.send(from, to, subject, text, mailHost, mailPort, timeout, username, password, Config.isMailQueueEnabled(), "", "", "")) {
    			System.out.println("Send mail to " + to + " success.");
    		}
    		else {
    			System.out.println("Send mail to " + to + " FAILED.");
    		}
    	}
    }
}
