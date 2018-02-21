package com.via.model;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

import com.via.database.*;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;
import com.via.system.Config;
import com.via.topology.JTopology;

public class JScheduleTask extends TimerTask {

	private JDbStatistics dbStat;
	private JDbDevice dbDevice;
	private List<JDevice> deviceList;
	private JDbAliveStatus dbAliveStatus;
	private JDbGroup dbGroup;
	private Date scheduledTime;
	//private Date executedTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger logger = Logger.getLogger(this.getClass());
	private final String[] grpOctets = {
	        JOid.ifInOctets,
	        JOid.ifHCInOctets,
            JOid.ifOutOctets,
            JOid.ifHCOutOctets
	};
	private final int iOCT32_RX_TOT = 0;
	private final int iOCT64_RX_TOT = 1;
	private final int iOCT32_TX_TOT = 2;
	private final int iOCT64_TX_TOT = 3;
	
	public JScheduleTask(JDbDevice dbDevice, JDbStatistics dbStatistics, List<JDevice> deviceList, JDbAliveStatus dbAliveStatus, JDbGroup dbGroup) {
		this.dbStat = dbStatistics;
		this.dbDevice = dbDevice;
		this.deviceList = deviceList;
		this.dbAliveStatus = dbAliveStatus;
		this.dbGroup = dbGroup;
	}
	
	@Override
	public void run() {
	    this.scheduledTime = new Date(this.scheduledExecutionTime());      // the expected time this task should run
	    //this.executedTime = new Date();                                    // the actual time this task really run
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduledTime);
		
		if(JNetwork.isFirstTime()){
			firstTimeAliveStatus();
			updateTopologyInfo();
			JNetwork.firstTimeIsOver();
		}
		
		if (calendar.get(Calendar.MINUTE) % 10 == 0) {		              // do it every 10th minute
			procDeviceTask();
		}
		
		if (JSystem.isServer && calendar.get(Calendar.MINUTE) % 30 == 0) {    // do it every 30th minute (must be server)
			procStatisticsTask();
		}
		
		if (calendar.get(Calendar.MINUTE) % 60 == 0) {		              // do it every 60 minute
			updateTopologyInfo();
		}
	}

	//====================================================================================================
	
	private void firstTimeAliveStatus() {
		for (JDevice device : deviceList) {
	    	if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
	    		if (device.getSnmpSupport() < 0) {
		            device.setLastSeen(new Date());
		            dbDevice.updateLastSeen(device);
		            continue;
		        }
		        
		        if (device.getSnmpSupport() == 0) {
		        	int pingResult = JTools.Ping(JTools.parseSnmpIp(device.getPublicIp()), 2, 2);
		        	if(pingResult == 1){
		        		device.setLastSeen(new Date());
		        		device.setAlive(1);
		        	}
		        	else {
		        		device.setAlive(0);
		        	}
		        }
		        
		        device.getSystemInfo();		// This is used to update upTime, or, initial if never been updated.
				
				dbDevice.updateAlive(device);
			}
	    }
		List<JGroup> groupList = dbGroup.getAllDevice();
		
		for(JGroup group : groupList){
			boolean isRed = false, isYellow = false;
			for (JDevice device : deviceList) {
				if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
					if(group.getName().equals(device.getGroupName()) && isRed == false){
						if(device.isAlive() == 0){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(0);
							isRed = true;
						}
						else if (device.isAlive() == 2){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(2);
							isYellow = true;
						}
						else if (device.isAlive() == 1 && isYellow == false){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(1);
						}
					}
				}
			}
			
			if(group.getAliveStatus() == 0){
				for (JDevice device : deviceList) {
					if(!group.getName().equals(device.getGroupName())){
						dbGroup.updateAlarmStatusFalse(group.getName());
					}
				}
			}
		}
	}
	
	private void procDeviceTask() {
	    for (JDevice device : deviceList) {
	    	if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
		        if (device.getSnmpSupport() < 0) {
		            //System.out.println("Skip non-snmp device for alive check: " + device.getPublicIp() + ", snmp=" + device.getSnmpSupport());
		            device.setLastSeen(new Date());
		            dbDevice.updateLastSeen(device);
		            continue;
		        }
		        
		        if (device.getSnmpSupport() == 0) {
		        	int pingResult = JTools.Ping(JTools.parseSnmpIp(device.getPublicIp()), 2, 2);
		        	if(pingResult == 1){
		        		device.setLastSeen(new Date());
		        		device.setAlive(1);
		        	}
		        	else {
		        		device.setAlive(0);
		        	}
		        		
		        	
		        }
		        
		        String[] sysInfo = device.getSystemInfo();		// This is used to update upTime, or, initial if never been updated.
				
				String[] AliveStatus = { "\'" + new java.sql.Timestamp(System.currentTimeMillis()) + "\'", "\'" + "1" + "\'", "\'" + device.getDeviceType() + "\'", "\'" + device.getPublicIp() + "\'", "\'" + (device.isAlive() == 1 ? true : false) + "\'" };
				dbAliveStatus.insert(AliveStatus);
				
				//JTools.print(sysInfo, false);
				
				Calendar calendar = Calendar.getInstance();
				Calendar setStart = Calendar.getInstance();//get start time calendar.
				Calendar setEnd = Calendar.getInstance();
				
				if(device.isMailFilter()){
					
					String [] spiltStart = device.getProfileStartTime().split(":");
					int startHour = Integer.parseInt(spiltStart[0]);
					int startMinutes = Integer.parseInt(spiltStart[1]);
					
					String [] spiltEnd = device.getProfileEndTime().split(":");
					int endHour = Integer.parseInt(spiltEnd[0]);
					int endMinutes = Integer.parseInt(spiltEnd[1]);
					
					
				    setStart.setTime(new java.util.Date());//set date object.
				    setStart.set(Calendar.HOUR_OF_DAY, startHour);
				    setStart.set(Calendar.MINUTE, startMinutes);
				    setStart.set(Calendar.MILLISECOND, 0);
				    
				    int startNum = Integer.parseInt(spiltStart[0] + spiltStart[1]);
				    int endNum = Integer.parseInt(spiltEnd[0] + spiltEnd[1]);
				    
				    if(endNum <= startNum){
				    	setEnd.add(Calendar.DAY_OF_MONTH, +1);
				    	setEnd.set(Calendar.HOUR_OF_DAY, endHour);
					    setEnd.set(Calendar.MINUTE, endMinutes);
					    setEnd.set(Calendar.MILLISECOND, 0);
					    //System.out.println("< " + setEnd.getTime());
				    }
				    else {
				    	setEnd.setTime(new java.util.Date());
					    setEnd.set(Calendar.HOUR_OF_DAY, endHour);
					    setEnd.set(Calendar.MINUTE, endMinutes);
					    setEnd.set(Calendar.MILLISECOND, 0);
					    //System.out.println("> " + setEnd.getTime());
				    }
				}
		        
				if (device.isAlive() == 1) {
					if (device.getDisconnectTicks() > 0) {
						logger.warn("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " re-connects.");					
						device.setDisconnectTicks(0);
					}
					
					if (device.getManagementFailTicks() > 0) {
						logger.warn("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " re-management.");					
						device.setManagementFailTicks(0);
					}
					
					dbDevice.updateLastSeen(device);
				}
				else if (device.isAlive() == 0) {
					device.setManagementFailTicks(0);
					device.setDisconnectTicks(device.getDisconnectTicks() + 1);
					if (device.getDisconnectTicks() == 1) {
						logger.warn("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " disconnects.");
					}
					else if (device.getDisconnectTicks() == 3){					
						logger.error("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " has disconnected over 30 minutes.");	// just alarm once when disconnect three times occurred
						
						if(device.isMailFilter()){
							
						    if (!(calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime()))){
						    												
			                	if (JAlarm.getAlarmInfo()[2].equals("1") && !JAlarm.getDeviceDisconnectSendEmailList().isEmpty()) {   //DeviceDisconnectSendEmail
			                		String result = JNetwork.setAlarmEmailStr(JAlarm.getDeviceDisconnectSendEmailList());
			                		JAlarm.doEmailSend(sdf2.format(new Date()) + " Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + 
			                				" has disconnected over 30 minutes.", result);
			                	}
			                    if (JAlarm.getAlarmInfo()[9].equals("1") && !JAlarm.getDeviceDisconnectSendSmsList().isEmpty()) {   //DeviceDisconnectSendSms
			                    	String result = JNetwork.setAlarmSmsStr(JAlarm.getDeviceDisconnectSendSmsList());
			                        JAlarm.doSMSSend(sdf2.format(new Date()) +
			                        		" VIA CyberExpert SMS Message: "+"Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() +
			                        		" has disconnected over 30 minutes.", result);
			                	}
			                    
								if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
									RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
									if (Service != null) {
										try {
											Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + 
																	device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " has disconnected over 30 minutes.");
										} catch (RemoteException e) {
											System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
											//e.printStackTrace();
										}
									}
								}
						    }
						    
						}
						else {
																		
							if (JAlarm.getAlarmInfo()[2].equals("1") && !JAlarm.getDeviceDisconnectSendEmailList().isEmpty()) {   //DeviceDisconnectSendEmail
		                		String result = JNetwork.setAlarmEmailStr(JAlarm.getDeviceDisconnectSendEmailList());
		                		JAlarm.doEmailSend(sdf2.format(new Date()) + " Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + 
		                				" has disconnected over 30 minutes.", result);
		                	}
		                    if (JAlarm.getAlarmInfo()[9].equals("1") && !JAlarm.getDeviceDisconnectSendSmsList().isEmpty()) {   //DeviceDisconnectSendSms
		                    	String result = JNetwork.setAlarmSmsStr(JAlarm.getDeviceDisconnectSendSmsList());
		                        JAlarm.doSMSSend(sdf2.format(new Date()) +
		                        		" VIA CyberExpert SMS Message: "+"Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() +
		                        		" has disconnected over 30 minutes.", result);
		                	}
		                    
							if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
								RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
								if (Service != null) {
									try {
										Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + device.getPublicIp() + " has disconnected over 30 minutes.");
									} catch (RemoteException e) {
										System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
										//e.printStackTrace();
									}
								}
							}
						}
	
					}
				}
				else if (device.isAlive() == 2) {
					device.setDisconnectTicks(0);
					device.setManagementFailTicks(device.getManagementFailTicks() + 1);
					if (device.getManagementFailTicks() == 1) {
						logger.warn("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " management fail.");
					}
					else if (device.getManagementFailTicks() == 3){					
						logger.error("Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " management fail over 30 minutes.");	// just alarm once when management fail three times occurred
						
						if(device.isMailFilter()){
							
						    if (!(calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime()))){
						    												
			                	if (JAlarm.getAlarmInfo()[16].equals("1") && !JAlarm.getManagementFailSendEmailList().isEmpty()) {   //ManagementFailSendEmail
			                		String result = JNetwork.setAlarmEmailStr(JAlarm.getManagementFailSendEmailList());
			                		JAlarm.doEmailSend(sdf2.format(new Date()) + " Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + 
			                				" management fail over 30 minutes.", result);
			                	}
			                    if (JAlarm.getAlarmInfo()[17].equals("1") && !JAlarm.getManagementFailSendSmsList().isEmpty()) {   //ManagementFailSendSms
			                    	String result = JNetwork.setAlarmSmsStr(JAlarm.getManagementFailSendSmsList());
			                        JAlarm.doSMSSend(sdf2.format(new Date()) +
			                        		" VIA CyberExpert SMS Message: "+"Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() +
			                        		" management fail over 30 minutes.", result);
			                	}
			                    
								if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
									RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
									if (Service != null) {
										try {
											Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + 
																	device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + " management fail over 30 minutes.");
										} catch (RemoteException e) {
											System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
											//e.printStackTrace();
										}
									}
								}
						    }
						    
						}
						else {
																		
							if (JAlarm.getAlarmInfo()[16].equals("1") && !JAlarm.getManagementFailSendEmailList().isEmpty()) {   //ManagementFailSendEmail
		                		String result = JNetwork.setAlarmEmailStr(JAlarm.getManagementFailSendEmailList());
		                		JAlarm.doEmailSend(sdf2.format(new Date()) + " Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() + 
		                				" management fail over 30 minutes.", result);
		                	}
		                    if (JAlarm.getAlarmInfo()[17].equals("1") && !JAlarm.getManagementFailSendSmsList().isEmpty()) {   //ManagementFailSendSms
		                    	String result = JNetwork.setAlarmSmsStr(JAlarm.getManagementFailSendSmsList());
		                        JAlarm.doSMSSend(sdf2.format(new Date()) +
		                        		" VIA CyberExpert SMS Message: "+"Device IP = " + device.getPublicIp() + ", " + device.getDeviceType() + ", " + device.getAliasName() +
		                        		" management fail over 30 minutes.", result);
		                	}
		                    
							if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
								RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
								if (Service != null) {
									try {
										Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + device.getPublicIp() + " management fail over 30 minutes.");
									} catch (RemoteException e) {
										System.out.println("insertNmsLog\nRemoteException:" + e.getMessage());
										//e.printStackTrace();
									}
								}
							}
						}
	
					}
				}
			    
				dbDevice.updateAlive(device);
			}
	    }
	    List<JGroup> groupList = dbGroup.getAllDevice();
		
	    for(JGroup group : groupList){
	    	boolean isRed = false, isYellow = false;
			for (JDevice device : deviceList) {
				if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
					if(group.getName().equals(device.getGroupName()) && isRed == false){
						if(device.isAlive() == 0){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(0);
							isRed = true;
						}
						else if (device.isAlive() == 2){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(2);
							isYellow = true;
						}
						else if (device.isAlive() == 1 && isYellow == false){
							dbGroup.updateAlarmStatus(device);
							group.setAliveStatus(1);
						}
					}
				}
			}
			
			if(group.getAliveStatus() == 0){
				for (JDevice device : deviceList) {
					if(!group.getName().equals(device.getGroupName())){
						dbGroup.updateAlarmStatusFalse(group.getName());
					}
				}
			}
		}
	}
	
	private void procStatisticsTask() {
		double prevTime = System.currentTimeMillis();
		Map<String, List<String>> errorMap = new TreeMap<String, List<String>>();     // key: device, value: port id
		List<String> emptyList = Collections.emptyList();                             // use for the error case that only needs device's IP
		boolean deviceNotAlive = false;
		boolean deviceNoEthPort = false;
		boolean deviceSnmpFail = false;
		boolean infNoData = false;
		boolean infParseFromSnmpFail = false;
		boolean infParseFromDbFail1 = false;
		boolean infParseFromDbFail2 = false;
		boolean infParseFromDbFail3 = false;
		boolean infParseFromDbFail4 = false;
		boolean infDataStoreFail = false;
	    
	    for (JDevice device : deviceList) {
	    	if(!device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
		        if (!device.isSupportRxTxOctet()) continue;
		        
		        if (device.isAlive() != 1) {
					//System.out.println(String.format("%s is skipped due to not alive.", device.getPublicIp()));
					errorMap.put(device.getPublicIp(), emptyList);
					deviceNotAlive = true;
					continue;
				}
	
				Map<String, List<String>> returnData;
				try {
					JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
		            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
					snmp.start();
					returnData = snmp.getTable(grpOctets);
					snmp.end();
				} catch (IOException e) {
					//System.out.println("Read mibs from " + device.getPublicIp() + " failed.");
					errorMap.put(device.getPublicIp(), emptyList);
					deviceSnmpFail = true;
					continue;
				}
				
				List<String> errInfList = new ArrayList<String>();
				boolean noData = false;
				boolean parseFromSnmpFail = false;		// currently not used
				boolean parseFromDbFail1 = false;
				boolean parseFromDbFail2 = false;
				boolean parseFromDbFail3 = false;
				boolean parseFromDbFail4 = false;
				boolean dataStoreFail = false;
	
				for (JInterface inf : device.getInterfaces()) {
					if (!inf.getIfType().equals("eth")) continue;
					
					String ifIndex = String.valueOf(inf.getIfIndex());
					String sOCT_RX_TOT = "0", sOCT_TX_TOT = "0";
					if (returnData.containsKey(ifIndex)) {
						List<String> data = returnData.get(ifIndex);
						sOCT_RX_TOT = data.get(iOCT32_RX_TOT);
						sOCT_TX_TOT = data.get(iOCT32_TX_TOT);
						String sOCT64_RX_TOT = data.get(iOCT64_RX_TOT);
	                    String sOCT64_TX_TOT = data.get(iOCT64_TX_TOT);
	                    if (!sOCT64_RX_TOT.isEmpty() && !sOCT64_RX_TOT.equals("null")) sOCT_RX_TOT = sOCT64_RX_TOT;	// TODO: should return noSuchInst but getTable() doesn't
	                    if (!sOCT64_TX_TOT.isEmpty() && !sOCT64_TX_TOT.equals("null")) sOCT_TX_TOT = sOCT64_TX_TOT;
					}
					else {
						errInfList.add(ifIndex);
						noData = true;
						continue;
					}
					
					String ipAddrField = String.format("'%s'", device.getPublicIp());
					String phyAddrField = String.format("'%s'", device.getPhyAddr());
					String ifIndexField = String.format("'%s'", ifIndex);
					if(device.getSysObjectId().indexOf("1.3.6.1.4.1.311.1.1.3") != -1){
						ifIndexField = "'1'";
					}
					String recordTimeField = String.format("'%s'", sdf.format(new Date()));
					String octetRxField = String.format("'%s'", sOCT_RX_TOT);
					String octetTxField = String.format("'%s'", sOCT_TX_TOT);
					String[] values = {"default", ipAddrField, phyAddrField, ifIndexField, recordTimeField, octetRxField, octetTxField};
	
					if (dbStat.add(values)) {
						//System.out.println(device.getPublicIp() + ":" + ifIndex + ", insert success.");
					}
					else {
						//System.out.println(device.getPublicIp() + ":" + ifIndex + ", insert failed.");
						errInfList.add(ifIndex);
					    dataStoreFail = true;
					}
	
					//System.out.println(device.getPublicIp() + ":" + ifIndex + ", OCT_RX_TOT: " + sOCT_RX_TOT + ", OCT_TX_TOT: " + sOCT_TX_TOT);
				}
				
				if (noData) {
				    errorMap.put(device.getPublicIp(), errInfList);
				    infNoData = true;
				}
				else if (parseFromSnmpFail) {
				    errorMap.put(device.getPublicIp(), errInfList);
				    infParseFromSnmpFail = true;
				}
				else if (parseFromDbFail1) {
				    errorMap.put(device.getPublicIp(), errInfList);
				    infParseFromDbFail1 = true;
				}
				else if (parseFromDbFail2) {
	                errorMap.put(device.getPublicIp(), errInfList);
	                infParseFromDbFail2 = true;
	            }
				else if (parseFromDbFail3) {
	                errorMap.put(device.getPublicIp(), errInfList);
	                infParseFromDbFail3 = true;
	            }
				else if (parseFromDbFail4) {
	                errorMap.put(device.getPublicIp(), errInfList);
	                infParseFromDbFail4 = true;
	            }
				else if (dataStoreFail) {
				    errorMap.put(device.getPublicIp(), errInfList);
				    infDataStoreFail = true;
				}
	    	}
		}	// loop deviceList
		    
	    if (deviceNotAlive) {
	        logger.debug("No alive, device: " + errorMap.keySet());
	    }
	    if (deviceNoEthPort) {
	        logger.debug("No ether port, device: " + errorMap.keySet());
	    }
	    if (deviceSnmpFail) {
	        logger.debug("Read snmp failed, device: " + errorMap.keySet());
	    }
	    if (infNoData) {
	        for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
	            logger.debug("Read data error, device: " + entry.getKey() + ", ifIndex: " + entry.getValue());
	        }
	    }
	    if (infParseFromSnmpFail) {
            for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                logger.debug("Parse data from snmp failed, device: " + entry.getKey() + ", ifIndex: " + entry.getValue());
            }
        }
	    if (infParseFromDbFail1 || infParseFromDbFail2 || infParseFromDbFail3 || infParseFromDbFail4) {
            for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                logger.debug("Parse data from db failed, device: " + entry.getKey() + ", ifIndex: " + entry.getValue());
            }
        }
	    if (infDataStoreFail) {
	        for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                logger.debug("Record to db failed, device: " + entry.getKey() + ", ifIndex: " + entry.getValue());
            }
	    }
	    
	    System.out.println("Statistics task scheduled at " + sdf.format(scheduledTime) + " costs: " + (System.currentTimeMillis() - prevTime) / 1000 + " sec.");
    
	}
	
	private void updateTopologyInfo() {
		System.out.println("<<<Start to update topology information in JScheduleTask.>>>");
		JNetwork.topologyIsStart();
		for (JDevice device : deviceList) {
			if (device.getSnmpSupport() > 0 && !device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")){
				device.updateTopologyInfo();
			}
		}
		System.out.println("<<<Stop update topology information in JScheduleTask.>>>");
		JTopology topology = new JTopology(deviceList);
		topology.getHtmlAndScript(Config.getTopologyTopDevice(), "ip_name", "2");
		JNetwork.topologyIsStop();
	}
}
