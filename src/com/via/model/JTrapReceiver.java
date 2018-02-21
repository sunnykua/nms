package com.via.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.apache.log4j.Logger;

import com.via.model.JNetwork;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;
import com.via.system.Config;

public class JTrapReceiver implements CommandResponder {
    private Logger logger = Logger.getLogger(this.getClass());
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;
	private static final  String SnmpTrapOID = "1.3.6.1.6.3.1.1.4.1.0";
	private static final  String SnmpTrapAddressOID = "1.3.6.1.6.3.18.1.3.0";
	private static final  String IfIndexOID = "1.3.6.1.2.1.2.2.1.1";
	private static final  String ColdStartTrapOID = "1.3.6.1.6.3.1.1.5.1";
	private static final  String WarmStartTrapOID = "1.3.6.1.6.3.1.1.5.2";
	private static final  String LinkDownTrapOID  = "1.3.6.1.6.3.1.1.5.3";
	private static final  String LinkUpTrapOID    = "1.3.6.1.6.3.1.1.5.4";
	private static final  String APTrapOID    = "1.3.6.1.4.1.3742.4.0.2";
	private static final  String APLeaveTrapAddressOID = "1.3.6.1.4.1.3742.10.5801.5.1.3";
	private static final  String APJoinTrapAddressOID = "1.3.6.1.4.1.3742.10.5801.5.1.2";
	private static final  String AcWarmStartTrapAddressOID = "1.3.6.1.4.1.3742.10.5801.5.1.5";
	private static final  String AcColdStartTrapAddressOID = "1.3.6.1.4.1.3742.10.5801.5.1.4";

	private List<JDevice> deviceList;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public JTrapReceiver(List<JDevice> deviceList) {
	    this.deviceList = deviceList;
	}

	public void doTrap(String ServerIP) {
		/*final JTrapReceiver server = new JTrapReceiver();
		server.run(ServerIP);*/
	    run(ServerIP);
	}
	public void run(String ServerIP) {
		try {
			init(ServerIP);
			snmp.addCommandResponder(this);
		} catch (final Exception ex) {
//			ex.printStackTrace();
            System.out.println("Add Command Responder failed.");
		}
	}
	
	private void init(String ServerIP) throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		
//		listenAddress = new UdpAddress("localhost/162");
		listenAddress = new UdpAddress(ServerIP);
		TransportMapping<?> transport;		
		transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
		
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		
        // Create Target	        
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        
		final USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		final String userName = "anuj";
		//
		final String authenticationPwd = "temptemptemptemp";
		final String privacyPwd = "temptemptemptemp";
		final byte[] localEngineId = MPv3.createLocalEngineID();
		//Add user to the USM
		snmp.getUSM().addUser(
		new OctetString(userName),
		new OctetString(localEngineId),
		new UsmUser(new OctetString(userName), AuthMD5.ID, new OctetString(authenticationPwd), PrivDES.ID,
		new OctetString(privacyPwd), new OctetString(localEngineId)));
		
		snmp.listen();
		System.out.println("--------------->Listening on Trap Information Server IP = " + listenAddress +  " <---------------");   
	}
	/**
	 * Free all resources.
	 * @throws IOException
	 */
	public void doTrapEnd() throws IOException {
		if (snmp != null) {
		    snmp.close();
		    threadPool.cancel();
		}
	}
	
//	@Override
	boolean isColdStart=false;		// will temporary down
	boolean isWarmStart=false;		// will temporary down
	boolean isLinkDown=false;		// will temporary down
	boolean isLinkUp=false;		// will temporary down
	boolean isAPTrap=false;		// will temporary down
	String isLinkUpPort="";
	String isLinkDownPort="";
	public synchronized void processPdu(final CommandResponderEvent respEvnt) {	
		
//	    logger.debug("Received PDU...");
//		System.out.println("Received TRAP PDU...");   

        if (respEvnt != null && respEvnt.getPDU() != null) {   
            @SuppressWarnings("unchecked")
			Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();   
            for (int i = 0; i < recVBs.size(); i++) {   
                VariableBinding recVB = recVBs.elementAt(i); 
              
		        if (recVB.getOid().toString().equals(SnmpTrapOID) ) {
		        	if (recVB.getVariable().toString().equals(ColdStartTrapOID) ) {
		                isColdStart=true;
		        	}
		        	else if (recVB.getVariable().toString().equals(WarmStartTrapOID) ) {
		                isWarmStart=true;
		            }
		        	else if (recVB.getVariable().toString().equals(LinkDownTrapOID) ) {
		                isLinkDown=true;
		                isLinkUp=false;
		        	}
		        	else if (recVB.getVariable().toString().equals(LinkUpTrapOID) ) {
		                isLinkUp=true;
		                isLinkDown=false;
		            }
		        	else if (recVB.getVariable().toString().equals(APTrapOID) ) {
		                isAPTrap=true;
		            }
		        	else {
		        		continue;		
		        	}
		        } else if (recVB.getOid().toString().indexOf(AcWarmStartTrapAddressOID) != -1 ) {
			    	String acAliasName = "";
			    	String acGlobalIp = "";
			    	String acPhyaddr = recVB.getVariable().toString().split(" ")[1];
			    	//String acip = recVB.getVariable().toString().split(" ")[2];
	            	
				    for (JDevice device : deviceList) {

			            if (device.getPhyAddr().equals(acPhyaddr) ) {
			            	acAliasName = device.getAliasName();
			            	acGlobalIp = device.getPublicIp();
			            }
				        if (device.getPhyAddr().equals(acPhyaddr)) {  //find ACIP
			    			String[][] notifyresult = JNetwork.viewNotifyAc(acPhyaddr);
				        logger.warn("Received Warm Start notice "
		                		+ "from AC (" + acAliasName + ", " + acGlobalIp + ") ");
	                	if (notifyresult[0][0].equals("1") && !notifyresult[0][1].isEmpty()) {    //AcWarmStartSendEmail
	                		JAlarm.doEmailSend( sdf.format(new Date())+ " Received Warm Start notice " + "from AC (" + acAliasName + ", " + acGlobalIp + ") ", notifyresult[0][1]);
	                	}
	                	if (notifyresult[0][2].equals("1") && !notifyresult[0][3].isEmpty()) {    //AcWarmStartSendSms
	                		JAlarm.doSMSSend(sdf.format(new Date()) +
	                				" VIA CyberExpert SMS Message: " + "Received Warm Start notice " + "from AC (" + acAliasName + ", " + acGlobalIp + ") ",
	                				notifyresult[0][3]);
	                	}		            	
				        }
				    }
		        } else if (recVB.getOid().toString().indexOf(AcColdStartTrapAddressOID) != -1 ) {
			    	String acAliasName = "";
			    	String acGlobalIp = "";
			    	String acPhyaddr = recVB.getVariable().toString().split(" ")[1];
			    	//String acip = recVB.getVariable().toString().split(" ")[2];
	            	
				    for (JDevice device : deviceList) {

			            if (device.getPhyAddr().equals(acPhyaddr) ) {
			            	acAliasName = device.getAliasName();
			            	acGlobalIp = device.getPublicIp();

			            }
				        if (device.getPhyAddr().equals(acPhyaddr)) {  //find ACIP
			    			String[][] notifyresult = JNetwork.viewNotifyAc(acPhyaddr);
			            logger.warn("Received Cold Start notice "
		                		+ "from AC (" + acAliasName + ", " + acGlobalIp + ") ");
	                	if (notifyresult[0][4].equals("1") && !notifyresult[0][5].isEmpty()) {    //AcColdStartSendEmail
	                		JAlarm.doEmailSend( sdf.format(new Date())+ " Received Cold Start notice " + "from AC (" + acAliasName + ", " + acGlobalIp + ") ", notifyresult[0][5]);
	                	}
	                    if (notifyresult[0][6].equals("1") && !notifyresult[0][7].isEmpty()) {    //AcColdStartSendSms
	                        JAlarm.doSMSSend(sdf.format(new Date()) +
	                        		" VIA CyberExpert SMS Message: " + "Received Cold Start notice " + "from AC (" + acAliasName + ", " + acGlobalIp + ") ", notifyresult[0][7]);
	                	}		            	
			            }
				    }
		        }

		        for (int j = 0; j < 30; j++) {
			        if (isLinkUp && recVB.getOid().toString().equals(IfIndexOID+"."+String.valueOf(j)) ) {

			        	isLinkUpPort = recVB.getVariable().toString();
			        } 
			        else if (isLinkDown && recVB.getOid().toString().equals(IfIndexOID+"."+String.valueOf(j)) ) {

			        	isLinkDownPort = recVB.getVariable().toString();
			        } 
			        else {
			        	continue;
			        }
	            }

		        if (isColdStart && recVB.getOid().toString().equals(SnmpTrapAddressOID) ) {
	                logger.error("Device IP = " + recVB.getVariable().toString() + " received Cold Start notice.");
	                                    
					if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
						if (Service != null) {
							try {
								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + recVB.getVariable().toString() + " received Cold Start notice.");
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
					
	                setColdStart("Device IP = " + recVB.getVariable().toString() + " received Cold Start notice.");
	                isColdStart=false;
		        }
		        else if (isWarmStart && recVB.getOid().toString().equals(SnmpTrapAddressOID) ) {
	                logger.error("Device IP = " + recVB.getVariable().toString() + " received Warm Start notice");
	                
	                setWarmStart("Device IP = " + recVB.getVariable().toString() + " received Warm Start notice");
	                isWarmStart=false;
	                
	                if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
						if (Service != null) {
							try {
								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + recVB.getVariable().toString() + " received Warm Start notice");
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
		        }
			    else if (isAPTrap && recVB.getOid().toString().indexOf(APLeaveTrapAddressOID) != -1 ) {
			    	String acip = recVB.getVariable().toString().split(" ")[6];
			    	String acPhyaddr = recVB.getVariable().toString().split(" ")[5];
	            	
				    for (JDevice device : deviceList) {

			            if (device.getPhyAddr().equals(acPhyaddr) ) {// ac trap of small setting
							String[][] notifyresult = JNetwork.viewNotify(acPhyaddr);		    	
			    			logger.warn("Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") leave notice "
			    						+ "from AC (" + device.getAliasName() + ", " + acip + ") ");
		                	if (notifyresult[0][4].equals("1") && !notifyresult[0][5].isEmpty()) {    //APLeaveSendEmail
		                		JAlarm.doEmailSend( sdf.format(new Date())+ " Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") leave notice " + "from AC (" + device.getAliasName() + ", " + acip + ") ", notifyresult[0][5]);
		                	}
		                    if (notifyresult[0][6].equals("1") && !notifyresult[0][7].isEmpty()) {    //APLeaveSendSms
		                        JAlarm.doSMSSend(sdf.format(new Date()) +
		                        		" VIA CyberExpert SMS Message: "+"Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") leave notice " +
		                        		"from AC (" + device.getAliasName() + ", " + acip + ") ", notifyresult[0][7]);
		                	}
			                
						}

				    }
	                isAPTrap=false;
		        }
			    else if (isAPTrap && recVB.getOid().toString().indexOf(APJoinTrapAddressOID) != -1 ) {
			    	String acip = recVB.getVariable().toString().split(" ")[6];
			    	String acPhyaddr = recVB.getVariable().toString().split(" ")[5];
	            	
				    for (JDevice device : deviceList) {
			            if (device.getPhyAddr().equals(acPhyaddr) ) {// ac trap of small setting
			            	String[][] notifyresult = JNetwork.viewNotify(acPhyaddr);
				            logger.warn("Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") join notice "
				                + "from AC (" + device.getAliasName() + ", " + acip + ") ");
		                	if (notifyresult[0][0].equals("1") && !notifyresult[0][1].isEmpty()) {    //APLeaveSendEmail
		                		JAlarm.doEmailSend( sdf.format(new Date())+ " Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") join notice " + "from AC (" + device.getAliasName() + ", " + acip + ") ", notifyresult[0][1]);
		                	}
		                    if (notifyresult[0][2].equals("1") && !notifyresult[0][3].isEmpty()) {    //APLeaveSendSms
		                        JAlarm.doSMSSend(sdf.format(new Date()) +
		                        		" VIA CyberExpert SMS Message: " +
		                        		"Received AP (" + recVB.getVariable().toString().split(" ")[1] + ") join notice "+ "from AC (" + device.getAliasName() + ", " + acip + ") ",
		                        		notifyresult[0][3]);
		                	}
		                }
				    }
			    	
	                isAPTrap=false;
		        }
		        else if (isLinkDown && recVB.getOid().toString().equals(SnmpTrapAddressOID) && !isLinkDownPort.isEmpty()) {

	                processLinkUpDown(recVB.getVariable().toString(), isLinkDownPort, false);
	                
	                setLinkDown("Device IP = " + recVB.getVariable().toString() + " Port" + isLinkDownPort + " received Link Down notice");
	                isLinkDown=false;
	                isLinkDownPort = "";
		        }
		        else if (isLinkUp && recVB.getOid().toString().equals(SnmpTrapAddressOID) && !isLinkUpPort.isEmpty() ) {	
	                
		            processLinkUpDown(recVB.getVariable().toString(), isLinkUpPort, true);

	                setLinkUp("Device IP = " + recVB.getVariable().toString() + " Port" + isLinkUpPort  + " received Link Up notice");
	                isLinkUp=false;
	                isLinkUpPort = "";
		        }
		        else {
		        	continue;
		        }
            }   
        }   
	
	}
	
	
	private boolean processLinkUpDown(String ipAddr, String portId, boolean isLinkUp) {
	    for (JDevice device : deviceList) {
            if (device.getPublicIp().equals(ipAddr)) {
                for (JInterface inf : device.getInterfaces()) {
                    if (Integer.toString(inf.getPortId()).equals(portId)) {
                        if (inf.isMonitored()) {
                        	
                        	if(inf.isMailFilter()){
	    						
	    						Calendar calendar = Calendar.getInstance();
	    						Calendar setStart = Calendar.getInstance();//get start time calendar.
	    						Calendar setEnd = Calendar.getInstance();
	    						
	    						String [] spiltStart = inf.getProfileStartTime().split(":");
	    						int startHour = Integer.parseInt(spiltStart[0]);
	    						int startMinutes = Integer.parseInt(spiltStart[1]);
	    						
	    						String [] spiltEnd = inf.getProfileEndTime().split(":");
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
	    					    }
	    					    else {
	    					    	setEnd.setTime(new java.util.Date());
	    						    setEnd.set(Calendar.HOUR_OF_DAY, endHour);
	    						    setEnd.set(Calendar.MINUTE, endMinutes);
	    						    setEnd.set(Calendar.MILLISECOND, 0);
	    					    }
	    					    
	    					    if (isLinkUp) {
	                                logger.error("Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice");
	    					    }
	    					    else {
	                                logger.error("Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice");
	    					    }

    						    if (!(calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime()))){
    						    	
    	                            if (isLinkUp) {
    	                            	if (JAlarm.getAlarmInfo()[3].equals("1") && !JAlarm.getMonitoredPortLinkUpSendEmailList().isEmpty()) {    //MonitoredPortLinkUpSendEmail
    	                            		String result = JNetwork.setAlarmEmailStr(JAlarm.getMonitoredPortLinkUpSendEmailList());
    	                            		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice", result);
    	                            	}
    	                                if (JAlarm.getAlarmInfo()[10].equals("1") && !JAlarm.getMonitoredPortLinkUpSendSmsList().isEmpty()) {    //MonitoredPortLinkUpSendSms
    	                                	String result = JNetwork.setAlarmSmsStr(JAlarm.getMonitoredPortLinkUpSendSmsList());
    	                                    JAlarm.doSMSSend(sdf.format(new Date()) +
    	                                    		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
    	                                    		" Monitored port received Link Up notice", result);
    	                            	}
    	                                
    	                                if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
    	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
    	            						if (Service != null) {
    	            							try {
    	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice");
    	            							} catch (RemoteException e) {
    	            								e.printStackTrace();
    	            							}
    	            						}
    	            					}
    	                            }
    	                            else {
    	                            	if (JAlarm.getAlarmInfo()[4].equals("1") && !JAlarm.getMonitoredPortLinkDownSendEmailList().isEmpty()) {   //MonitoredPortLinkDownSendEmail
    	                            		String result = JNetwork.setAlarmEmailStr(JAlarm.getMonitoredPortLinkDownSendEmailList());
    	                            		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice", result);
    	                            	}
    	                                if (JAlarm.getAlarmInfo()[11].equals("1") && !JAlarm.getMonitoredPortLinkDownSendSmsList().isEmpty()) {   //MonitoredPortLinkDownSendSms
    	                                	String result = JNetwork.setAlarmSmsStr(JAlarm.getMonitoredPortLinkDownSendSmsList());
    	                                    JAlarm.doSMSSend(sdf.format(new Date()) +
    	                                    		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
    	                                    		" Monitored port received Link Down notice", result);
    	                            	}
    	                                
    	                                if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
    	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
    	            						if (Service != null) {
    	            							try {
    	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice");
    	            							} catch (RemoteException e) {
    	            								e.printStackTrace();
    	            							}
    	            						}
    	            					}
    	                            }
    						    	
    							}
    						} else {
    							
                                if (isLinkUp) {
                                    logger.error("Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice");                                    
                                    if (JAlarm.getAlarmInfo()[3].equals("1") && !JAlarm.getMonitoredPortLinkUpSendEmailList().isEmpty()) {    //MonitoredPortLinkUpSendEmail
	                            		String result = JNetwork.setAlarmEmailStr(JAlarm.getMonitoredPortLinkUpSendEmailList());
                                		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice", result);
                                	}
                                    if (JAlarm.getAlarmInfo()[10].equals("1") && !JAlarm.getMonitoredPortLinkUpSendSmsList().isEmpty()) {    //MonitoredPortLinkUpSendSms
	                                	String result = JNetwork.setAlarmSmsStr(JAlarm.getMonitoredPortLinkUpSendSmsList());
                                        JAlarm.doSMSSend(sdf.format(new Date()) +
                                        		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
                                        		" Monitored port received Link Up notice", result);
                                	}
                                    
                                    if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
	            						if (Service != null) {
	            							try {
	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Up notice");
	            							} catch (RemoteException e) {
	            								e.printStackTrace();
	            							}
	            						}
	            					}
                                }
                                else {
                                    logger.error("Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice");                                    
                                    if (JAlarm.getAlarmInfo()[4].equals("1") && !JAlarm.getMonitoredPortLinkDownSendEmailList().isEmpty()) {   //MonitoredPortLinkDownSendEmail
	                            		String result = JNetwork.setAlarmEmailStr(JAlarm.getMonitoredPortLinkDownSendEmailList());
                                		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice", result);
                                	}
                                    if (JAlarm.getAlarmInfo()[11].equals("1") && !JAlarm.getMonitoredPortLinkDownSendSmsList().isEmpty()) {   //MonitoredPortLinkDownSendSms
	                                	String result = JNetwork.setAlarmSmsStr(JAlarm.getMonitoredPortLinkDownSendSmsList());
                                        JAlarm.doSMSSend(sdf.format(new Date()) +
                                        		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
                                        		" Monitored port received Link Down notice", result);
                                	}
                                    
                                    if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
	            						if (Service != null) {
	            							try {
	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Monitored port received Link Down notice");
	            							} catch (RemoteException e) {
	            								e.printStackTrace();
	            							}
	            						}
	            					}
                                }
    						}
                      	                         
                        }
                        else if(!inf.getLldpRemoteId().isEmpty()) {
                        	String RemoteAddr = inf.getLldpRemoteId();
                        	for (JDevice Remotedevice : deviceList) {
                    			if (Remotedevice.getPhyAddr().equals(RemoteAddr)) {
                    				String Remotetype = Remotedevice.getDeviceType();

                    				if (Remotetype.equals("l2switch") || Remotetype.equals("l3switch") 
                    						|| Remotetype.equals("server")) {
                    					
                    					
                    					if (isLinkUp) {
                                            logger.error("Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Up notice");                                            
                                        	if (JAlarm.getAlarmInfo()[5].equals("1") && !JAlarm.getCriticalPortLinkUpSendEmailList().isEmpty()) {   //CriticalPortLinkUpSendEmail
                                        		String result = JNetwork.setAlarmEmailStr(JAlarm.getCriticalPortLinkUpSendEmailList());
                                        		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Up notice", result);
                                        	}
                                            if (JAlarm.getAlarmInfo()[12].equals("1") && !JAlarm.getCriticalPortLinkUpSendSmsList().isEmpty()) {   //CriticalPortLinkUpSendSms
                                            	String result = JNetwork.setAlarmSmsStr(JAlarm.getCriticalPortLinkUpSendSmsList());
                                                JAlarm.doSMSSend(sdf.format(new Date()) +
                                                		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
                                                		" Critical port received Link Up notice", result);
                                        	}
                                            
                                            if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
        	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
        	            						if (Service != null) {
        	            							try {
        	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Up notice");
        	            							} catch (RemoteException e) {
        	            								e.printStackTrace();
        	            							}
        	            						}
        	            					}
                    					}
                    					else {
                                            logger.error("Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Down notice");                                            
                                        	if (JAlarm.getAlarmInfo()[6].equals("1") && !JAlarm.getCriticalPortLinkDownSendEmailList().isEmpty()) { 	//CriticalPortLinkDownSendEmail
                                        		String result = JNetwork.setAlarmEmailStr(JAlarm.getCriticalPortLinkDownSendEmailList());
                                        		JAlarm.doEmailSend( sdf.format(new Date())+ " Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Down notice", result);
                                        	}
                                            if (JAlarm.getAlarmInfo()[13].equals("1") && !JAlarm.getCriticalPortLinkDownSendSmsList().isEmpty()) {	//CriticalPortLinkDownSendSms
                                            	String result = JNetwork.setAlarmSmsStr(JAlarm.getCriticalPortLinkDownSendSmsList());
                                                JAlarm.doSMSSend(sdf.format(new Date()) +
                                                		" VIA CyberExpert SMS Message: " + "Device IP = " + ipAddr + " Port" + portId +
                                                		" Critical port received Link Down notice", result);
                                        	}
                                            
                                            if (Config.getRemoteServiceConfig().isEnable() && !Config.getRemoteServiceConfig().getServerAddress().equals("")) {
        	            						RemoteInterface Service = RemoteService.creatClient(Config.getRemoteServiceConfig().getServerAddress(), 3);
        	            						if (Service != null) {
        	            							try {
        	            								Service.insertNmsLog(Config.getRemoteServiceConfig().getLocalAddress(), new java.sql.Timestamp(System.currentTimeMillis()), "ERROR", "Device IP = " + ipAddr + " Port" + portId + " Critical port received Link Down notice");
        	            							} catch (RemoteException e) {
        	            								e.printStackTrace();
        	            							}
        	            						}
        	            					}
                                        }
                    					                   					
                    				}
                    				else {
                    					/*
                    					if(inf.isMailFilter()){
            	    						
            	    						Calendar calendar = Calendar.getInstance();
            	    						Calendar setStart = Calendar.getInstance();//get start time calendar.
            	    						Calendar setEnd = Calendar.getInstance();
            	    						
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
            	    					    }
            	    					    else {
            	    					    	setEnd.setTime(new java.util.Date());
            	    						    setEnd.set(Calendar.HOUR_OF_DAY, endHour);
            	    						    setEnd.set(Calendar.MINUTE, endMinutes);
            	    						    setEnd.set(Calendar.MILLISECOND, 0);
            	    					    }

                						    if (!calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime())){
                	                            if (isLinkUp)
                	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Up notice");
                	                            else
                	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Down notice");    				
                							}
                						} else {*/
            	                            if (isLinkUp)
            	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Up notice");
            	                            else
            	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Down notice");    							
                						//}
                    					
                                    }
                    			}
                    		}
                        }
                        else {                        	
                        	/*if(inf.isMailFilter()){
	    						
	    						Calendar calendar = Calendar.getInstance();
	    						Calendar setStart = Calendar.getInstance();//get start time calendar.
	    						Calendar setEnd = Calendar.getInstance();
	    						
	    						String [] spiltStart = inf.getProfileStartTime().split(":");
	    						int startHour = Integer.parseInt(spiltStart[0]);
	    						int startMinutes = Integer.parseInt(spiltStart[1]);
	    						
	    						String [] spiltEnd = inf.getProfileEndTime().split(":");
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
	    					    }
	    					    else {
	    					    	setEnd.setTime(new java.util.Date());
	    						    setEnd.set(Calendar.HOUR_OF_DAY, endHour);
	    						    setEnd.set(Calendar.MINUTE, endMinutes);
	    						    setEnd.set(Calendar.MILLISECOND, 0);
	    					    }

    						    if (!calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime())){
    	                            if (isLinkUp)
    	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Up notice");
    	                            else
    	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Down notice");    				
    							}
    						} else {*/
	                            if (isLinkUp)
	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Up notice");
	                            else
	                                logger.info("Device IP = " + ipAddr + " Port" + portId + " received Link Down notice");    							
    						//}
                        }
        				return true;
                    }
                }
            }
        }
	    
	    return false;
	}
	

	private String ColdStart;
	private String WarmStart;
	private String LinkDown;
	private String LinkUp;
	
	public String getColdStart() {
		return ColdStart;
	}

	public void setColdStart(String coldStart) {
		ColdStart = coldStart;
	}

	public String getWarmStart() {
		return WarmStart;
	}

	public void setWarmStart(String warmStart) {
		WarmStart = warmStart;
	}

	public String getLinkDown() {
		return LinkDown;
	}

	public void setLinkDown(String linkDown) {
		LinkDown = linkDown;
	}

	public String getLinkUp() {
		return LinkUp;
	}

	public void setLinkUp(String linkUp) {
		LinkUp = linkUp;
	}



}
