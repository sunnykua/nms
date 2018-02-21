package com.via.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.via.database.JModule;
import com.via.model.JLinkView.JLinkViewItem;
import com.via.model.JPortStatus.JPortStatusItem;
import com.via.model.JVlanStatus.JVlanStatusItem;
import com.via.model.JPoeStatus.JPoeStatusItem;
import com.via.model.JPortStatistics.JPortStatisticsItem;
import com.via.model.JEtherlikeStatistics.JEtherlikeStatisticsItem;
import com.via.model.JRmonStatistics.JRmonStatisticsItem;
import com.via.model.JStaticVlanList.JStaticVlanListItem;
import com.via.model.JCurrentVlanList.JCurrentVlanListItem;

public class JDevice implements Serializable {
	// Common information for SNMP
	private String publicIp;		// The IP directly connected from NMS, may be equal to local IP
	private String publicIpFull;
	private String parentIp;
	private String snmpPort;
	private int snmpTimeout;
	private int snmpVersion;
	private String snmpAddress;	// Full address with IP and SNMP port
	private String readCommunity;
	private String writeCommunity;
	private int snmpSupport;        // supported snmp version, 0:not support, 1:v1, 2:v2c, 3:v3
	
	private String securityName;
	private int securityLevel;
	private String authProtocol;
	private String authPassword;
	private String privProtocol;
	private String privPassword;
	// === System Info ===
	private String sysDescr;
	private String sysObjectId;
	private String sysUpTime;
	private String sysContact;
	private String sysName;
	private String sysLocation;
	private String brandName;
	private String modelName;
	private int modelRevision;
	// === Interface Info ===
	private String phyAddr;
	private String localIp;		// The IP in local area network
	private String ifCPU;
	private int infNum;
	private int rj45Num;
	private int fiberNum;
	private int stackNum;
	private List<JInterface> interfaces;           // interface will be placed by descent order of portId
	// === Control Info ===
	private Date createdTime;
	private String aliasName;
	private Date lastSeen;                         // Last time update this device
	private int isAlive;
	// === Support State ===
	private boolean isSupportHostResource;
	private boolean isSupportLinkState;			// ifOperStatus
	private boolean isSupportNegoState;			// ifMauAutoNegSupported
	private boolean isSupportRxTxOctet;			// ifInOctets, ifOutOctets
	private boolean isSupportPacketType;
	private boolean isSupportRmon;
	private boolean isSupportPvid;
	private boolean isSupportVlan;
	private boolean isSupportGvrp;
	private boolean isSupportPoe;
	private boolean isSupportTrap;
	private boolean isSupportLldp;
	private boolean isSupportRStp;
	private boolean isSupportMStp;
	private boolean isSupportEdgeCoreResource;
	private boolean isOctet64;
	private boolean isSupportEgcoTrap;
	
	private String deviceType;                     // l2switch, l3switch, wlanAC, wlanAP, firewall, router
	private String groupName;
    private boolean isVirtual;

	private int stpPriority;
	private int stpRootCost;
	private int stpRootPort;
	private String stpDesignatedRoot;              // the root's MAC
	private boolean stpValid;
	
	private int disconnectTicks;
	private int managementFailTicks;
	private String dbStatisticsTableName;
	// === POE Schedule ===
	private boolean isPoeManual;
	private String poeStartTime;
	private String poeStartStatus;
	private String poeEndTime;
	private String poeEndStatus;
	private boolean isPoePower;
	// === Mail Filter ===
	private boolean isMailFilter;
	private String profileStartTime;
	private String profileEndTime;
	private String profileName;
	
	
	private static Logger logger = Logger.getLogger(JDevice.class);
	
	public JDevice() {
	    this.createdTime = new Date();
	    this.lastSeen = null;
	    this.isAlive = 0;
	    
	    this.isVirtual = false;
	    
	    this.stpPriority = -1;
	    this.stpRootCost = -1;
	    this.stpRootPort = 0;
	    this.stpDesignatedRoot = "";
	    this.stpValid = false;
	    
	    this.disconnectTicks = 0;
	    this.managementFailTicks = 0;
	}

	public int initial(JModule module) {
	    logger.debug("==========initial " + publicIp);
	    //this.readCommunity = module.getReadCommunity();      // NOTE: read community strings should not specified by module
	    this.writeCommunity = module.getWriteCommunity();
	    this.sysObjectId = module.getObjectId();               // NOTE: this should not be changed
	    this.snmpSupport = module.getSnmpSupport();
	    this.brandName = module.getBrandName();
	    this.modelName = module.getModelName();
	    this.modelRevision = module.getModelRevision();
	    
	    if (module.getObjectId().indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {
			try {
				JSnmp snmp = snmpVersion <= 2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) : new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
				snmp.start();
				//System.out.println(snmp.getNode(JOid.ipAdEntIfIndex + "." + publicIp));
				module.setInfCpuIndex(snmp.getNode(JOid.ipAdEntIfIndex + "." + publicIp));
				snmp.end();
			} catch (IOException ioe) {
				System.out.println("Error, fetch simple info snmp is wrong.");
			}
		}
		this.ifCPU = module.getInfCpuIndex();
		
//	    this.snmpTimeout = module.getSnmpTimeout();
	    this.infNum = module.getInfNum();
	    this.rj45Num = module.getRj45Num();
	    this.fiberNum = module.getFiberNum();
	    this.stackNum = 1;

	    if (snmpSupport < 1) {
	        updateNonSnmpInterface(module);
	    }
	    else {
	    	if(sysObjectId.equals("1.3.6.1.4.1.3742.10.5901.1")){
	    		updateNonSnmpInterface(module);
	    	}
	    	else {
		    	updateInterface();
		    	this.infNum = this.interfaces.size();
	    	}
	    }
        
        this.isSupportHostResource = module.isSupHostResource();
        this.isSupportLinkState = module.isSupLinkState();
        this.isSupportNegoState = module.isSupNegoState();
        this.isSupportRxTxOctet = module.isSupRxTxOctet();
        this.isSupportPacketType = module.isSupPacketType();
        this.isSupportRmon = module.isSupRmon();
        this.isSupportPvid = module.isSupPvid();
        this.isSupportVlan = module.isSupVlan();
        this.isSupportGvrp = module.isSupGvrp();
        this.isSupportPoe = module.isSupPoe();
        this.isSupportTrap = module.isSupTrap();
        this.isSupportLldp = module.isSupLldp();
        this.isSupportRStp = module.isSupRStp();
        this.isSupportMStp = module.isSupMStp();
        this.isSupportEdgeCoreResource = module.isSupEdgeCoreResource();
        this.isOctet64 = module.isSupOctet64();
        this.isSupportEgcoTrap = module.isSupEgcoTrap();
        this.deviceType = module.getDeviceType();
        this.setVirtual(false);

        if (snmpSupport > 0) {
            this.isAlive = initSystemInfo();
            this.aliasName = !sysDescr.isEmpty() ? sysDescr : publicIp;
            initIpEntity();                                            // update mac addr from entity MIB
            if (isSupportRStp) initStpInfo();
            if (isSupportLldp) initLldpInfo();
            if (isSupportVlan) updateVlan();
            if (isSupportLldp && (deviceType.equals("wlanAC") || deviceType.equals("wlanAP"))) {
                updatePhyAddrFromLldp();
            }
        }
        else if(snmpSupport == 0){
        	this.sysDescr = module.getModelName();
        	/*String phyAddress = JTools.getMAC(JTools.parseSnmpIp(publicIp), 2);
        	if(phyAddress != null){
        		this.phyAddr = phyAddress;
        		System.out.println("phyAddress= "+phyAddress);
        	}
        	else{*/
        		this.phyAddr = "N/A";
        	//}
        	
        	this.aliasName = module.getModelName();
        	int pingResult = JTools.Ping(JTools.parseSnmpIp(publicIp), 2, 2);
          	
    		if(pingResult == 1){
    			this.isAlive = 1;
    		}
    		else{
    			this.isAlive = 0;
    		}
        }
        else {
            this.sysDescr = module.getModelName();
            this.phyAddr = "N/A";
            this.aliasName = module.getModelName();
            this.isAlive = 1;
        }
        if (sysObjectId.equals("1.3.6.1.4.1.3742.10.5901.1")) {     // TODO: temporary workaround
            this.aliasName = "VIA Access Point VNT5901";
        }
        if (sysObjectId.equals("1.3.6.1.4.1.3742.10.5801.1")) {     // TODO: temporary workaround
            this.aliasName = "VIA Wireless Controller VNT5801";
        }
        if (sysObjectId.indexOf("1.3.6.1.4.1.311.1.1.3.1.5.1.0") != -1) {     // TODO: temporary workaround
            this.aliasName = "VIA MGV Chief Server";
        }
        if (sysObjectId.indexOf("1.3.6.1.4.1.311.1.1.3.1.5.2.0") != -1) {     // TODO: temporary workaround
            this.aliasName = "VIA MGV Command Server";
        }
        if (sysObjectId.indexOf("1.3.6.1.4.1.311.1.1.3.1.6.1.0") != -1) {     // TODO: temporary workaround
            this.aliasName = "VIA MGV Player";
        }
        
        this.lastSeen = new Date();
        
        return isAlive;
	}

	public static String[] fetchSimpleInfo(String snmpAddress,String readCommunity) {
        String sysDescr = "", phyAddr = "", localIp = "";
        try {
            JSnmp snmp = new JSnmp(snmpAddress,readCommunity, 2, 1000, 1);
           snmp.start();
            String[] result = snmp.getNextNodes(new String[]{"1.3.6.1.2.1.1.1", JOid.ipAdEntAddr, JOid.ipAdEntIfIndex});    // for sysDescr, local IP and ifIndex of CPU
            if (result != null && result.length == 3) {
                sysDescr = result[0];
                localIp = result[1];
                phyAddr = snmp.getNode(JOid.ifPhysAddress + "." + result[2]);
            }
            else {
                System.out.println("Error, fetch simple info length is wrong.");
            }
            snmp.end();
        } catch (IOException e) {
            System.out.println("Error, fetch simple info snmp is wrong.");
        }
        System.out.println("==========\nip=" + snmpAddress + "\ndescr=" + sysDescr + "\nphyAddr=" + phyAddr + "\nlocalIp=" + localIp);
        
        return new String[]{snmpAddress, sysDescr, phyAddr, localIp};
    }

	/**<p>Return a set of data that belongs to System MIB.</p>
	 * <p>If this device is never be seen, it will do initial, otherwise, only upTime will be updated.
	 * And if device is not reachable, a set of empty string will returned.</p>
	 * <p>This function can be used to update current status.</p>
	 * @return an array contained 6 strings of system info.
	 */
	public String[] getSystemInfo() {
		if (snmpSupport > 0) {
			if (updateUpTime() == true) {
				this.lastSeen = new Date();
				this.isAlive = 1;
			} else {
				int pingResult = JTools.Ping(JTools.parseSnmpIp(snmpAddress), 2, 2);
				if (pingResult == 1) {
					this.isAlive = 2;
				} else {
					this.isAlive = 0;
				}
			}
		}
		return new String[] { sysDescr, sysObjectId, sysUpTime, sysContact, sysName, sysLocation };
	}
	
	public void updateTopologyInfo(/*Map<String, String[]> mapper*/) {		// mapper: MAC to { publicIp, snmpPort, snmpAddress }
		
      if (updateUpTime() == true) {
	      this.lastSeen = new Date();
	      this.isAlive = 1;
	  }
	  else {
			int pingResult = JTools.Ping(JTools.parseSnmpIp(snmpAddress), 2, 2);
			if (pingResult == 1) {
				this.isAlive = 2;
			} else {
				this.isAlive = 0;
			}
	  }
      if (isSupportRStp) initStpInfo();
      if (isSupportLldp) initLldpInfo();
	}
	
	public void devicesRefresh() {
	
	  if (snmpSupport > 0) {
		  if (updateUpTime() == true) {
		      this.lastSeen = new Date();
		      this.isAlive = 1;
		  }
		  else {
				int pingResult = JTools.Ping(JTools.parseSnmpIp(snmpAddress), 2, 2);
				//System.out.println(pingResult);
				if (pingResult == 1) {
					this.isAlive = 2;
				} else {
					this.isAlive = 0;
				}
		  }
	      if(isAlive == 1){
				initIpEntity();
				if (isSupportLldp && (deviceType.equals("wlanAC") || deviceType.equals("wlanAP"))) {
		            updatePhyAddrFromLldp();
		        }
	      }
	  }
	  else if(snmpSupport == 0){
		int pingResult = JTools.Ping(JTools.parseSnmpIp(publicIp), 2, 2);
      	
		if(pingResult == 1){
			this.lastSeen = new Date();
		    this.isAlive = 1;
      		//System.out.println("IP="+publicIp+"Alive="+isAlive);
      	}
      	else {
      		this.isAlive = 0;
      	}
	  }
	}
	
	public String deviceMacRefresh(JDevice device) {
		
		this.isAlive = initSystemInfo();
		
		if(isAlive == 1){
			initIpEntity();
			if (isSupportLldp && (deviceType.equals("wlanAC") || deviceType.equals("wlanAP"))) {
	            updatePhyAddrFromLldp();
	        }
			return "success";
		}
		else {
			return "fail";
		}
	}
	
	public String getJackType(int portId) {
		if (portId <= 0 || portId > infNum) return new String();
		
		return interfaces.get(portId).getJackType();
	}

	public int[] getChart1Data(int port) {
		int[] dataArray = new int[]{0, 0, 0};
		if (port < 1 || port > infNum) {
			System.out.println("port is out of range.");
			return dataArray;
		}
		
		String[] chartOids = {
				JOid.ifInUcastPkts + "." + port,
				JOid.ifInMulticastPkts  + "." + port,
				JOid.ifInBroadcastPkts + "." + port
		};
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			String[] result = snmp.getNodes(chartOids);
			
			if (result.length == chartOids.length) {
				String unicastPkt = result[0];
				String multicastPkt = result[1];
				String broadcastPkt = result[2];
				
				dataArray[0] = Integer.parseInt(unicastPkt);
				dataArray[1] = Integer.parseInt(multicastPkt);
				dataArray[2] = Integer.parseInt(broadcastPkt);
			}

			snmp.end();
		}
		catch (IOException ioe) {
			System.out.println("Read chart1 data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse chart1 data failed.");
		}

		return dataArray;
	}
	
	public int[] getChart2Data(int port) {
		int[] dataArray = new int[]{0, 0};
		if (port < 1 || port > infNum) {
			System.out.println("port is out of range.");
			return dataArray;
		}
		
		String[] chartOids = {
				JOid.ifInUcastPkts + "." + port,
				JOid.ifInNUcastPkts + "." + port,
				JOid.ifOutUcastPkts + "." + port,
				JOid.ifOutNUcastPkts + "." + port,
		};
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			String[] result = snmp.getNodes(chartOids);
			
			if (result.length == chartOids.length) {
				int rxPackets = Integer.parseInt(result[0]) + Integer.parseInt(result[1]);
				int txPackets = Integer.parseInt(result[2]) + Integer.parseInt(result[3]);
				
				dataArray[0] = rxPackets;
				dataArray[1] = txPackets;
			}

			snmp.end();
		}
		catch (IOException ioe) {
			System.out.println("Read chart1 data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse chart1 data failed.");
		}

		return dataArray;
	}
	
	public long[] getRxTxPacket(int port) {
		long[] dataArray = new long[]{0, 0, 0};
		if (port < 1 || port > infNum) {
			System.out.println("port is out of range.");
			return dataArray;
		}
		
		String[] chartOids = {
				JOid.ifInUcastPkts + "." + port,
				JOid.ifInNUcastPkts + "." + port,
				JOid.ifOutUcastPkts + "." + port,
				JOid.ifOutNUcastPkts + "." + port,
				JOid.ifSpeed + "." + port,
		};
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			String[] result = snmp.getNodes(chartOids);
			
			if (result.length == chartOids.length) {
				long rxPackets = Long.parseLong(result[0]) + Long.parseLong(result[1]);
				long txPackets = Long.parseLong(result[2]) + Long.parseLong(result[3]);
				long rxSpeed   = Long.parseLong(result[4]);
				
				dataArray[0] = rxPackets;
				dataArray[1] = txPackets;
				dataArray[2] = rxSpeed;
			}

			snmp.end();
		}
		catch (IOException ioe) {
			System.out.println("Read RxTxPacket data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse RxTxPacket data failed.");
		}

		return dataArray;
	}
	
	public long[] getRxTxOctet(int port) {
	    long[] output = null;
	    if (port < 1 || port > infNum) {
            System.out.println(String.format("Input port(%d) is out of range.", port));
            //return dataArray;
        }
	    
	    String[] oidOctet = {
                JOid.ifInOctets + "." + port,
                JOid.ifOutOctets + "." + port
        };
	    
	    try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            String[] result = snmp.getNodes(oidOctet);
            snmp.end();
            
            if (result.length == oidOctet.length) {
                long[] data = new long[2];
                data[0] = Long.parseLong(result[0]);
                data[1] = Long.parseLong(result[1]);
                output = data;
            }
            else {
                System.out.println("Result length from SNMP is error.");
            }
        }
        catch (IOException ioe) {
            System.out.println("Read rxtx octet by SNMP failed.");
        }
        catch (NumberFormatException nfe) {
            System.out.println("Parse rxtx octet from Int to String failed.");
        }
	    
	    return output;
	}
	
	public long[] getAplistRxTxOctet(String apip) {
	    long[] output = null;
	    
	    String[] oidOctet = {
                JOid.vnt5801ApListApRxOctet + "." + apip,  //rx
                JOid.vnt5801ApListApTxOctet + "." + apip  //tx
        };
	    
	    try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            String[] result = snmp.getNodes(oidOctet);
            snmp.end();
            
            if (result.length == oidOctet.length) {
                long[] data = new long[2];
                data[0] = Long.parseLong(result[0]);
                data[1] = Long.parseLong(result[1]);
                output = data;
            }
            else {
                System.out.println("Result Aplist length from SNMP is error.");
            }
        }
        catch (IOException ioe) {
            System.out.println("Read Aplist rxtx octet by SNMP failed.");
        }
        catch (NumberFormatException nfe) {
            System.out.println("Parse Aplist rxtx octet from Int to String failed.");
        }
	    
	    return output;
	}
	
	public long[] getApSsidlistRxTxOctet(String apip, int apssidindex) {
	    long[] output = null;

	    String[] oidOctet = {
                JOid.vnt5801SsidListRxOctet + "." + apip + "." + apssidindex,  //rx
                JOid.vnt5801SsidListTxOctet + "." + apip + "." + apssidindex  //tx
        };
	    
	    try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            String[] result = snmp.getNodes(oidOctet);
            snmp.end();
            
            if (result.length == oidOctet.length) {
                long[] data = new long[2];
                data[0] = Long.parseLong(result[0]);
                data[1] = Long.parseLong(result[1]);
                output = data;
            }
            else {
                System.out.println("Result ApSsidlist length from SNMP is error.");
            }
        }
        catch (IOException ioe) {
            System.out.println("Read ApSsidlist rxtx octet by SNMP failed.");
        }
        catch (NumberFormatException nfe) {
            System.out.println("Parse ApSsidlist rxtx octet from Int to String failed.");
        }
	    
	    return output;
	}
	
	public long[] getRxTxOctetAll(String rxtxSelect) {
        long[] output = null;
        String[] oidOctet;
        if (rxtxSelect.equalsIgnoreCase("rx")) {
            oidOctet = new String[]{JOid.ifInOctets};
        }
        else if (rxtxSelect.equalsIgnoreCase("tx")) {
            oidOctet = new String[]{JOid.ifOutOctets};
        }
        else {
            System.out.println("getRxTxOctetAll(): should select between rx and tx.");
            return null;
        }
        
        try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            Map<String, List<String>> result = snmp.getTable(oidOctet);
            snmp.end();
            
            if (result != null && result.size() >= infNum) {        // care about  = 0
                output = new long[infNum];
                
                for (int i = 0; i < infNum; i++) {
                    String ifKey = String.valueOf(i + 1);
                    
                    if (result.containsKey(ifKey)) {
                        try {
                            if (result.get(ifKey).size() == 1) {                         // size should be 1 because requested column is just 1
                                if (result.get(ifKey).get(0) == null || result.get(ifKey).get(0).equalsIgnoreCase("null")) {
                                    output[i] = 0;
                                }
                                else {
                                    output[i] = Long.parseLong(result.get(ifKey).get(0));
                                }
                            }
                            else {
                                output[i] = 0;
                            }
                        }
                        catch (NumberFormatException e) {       // TODO: add debug message in this function
                            output[i] = 0;
                        }
                    }
                    else {                      // it contains a index that does not in the range of 1 to 
                        output[i] = 0;
                    }
                }
            }
            else {
                System.out.println("getRxTxOctetAll(): Result length from SNMP is error.(" + result.size());
            }
        }
        catch (IOException ioe) {
            System.out.println("Read rxtx octet by SNMP failed.");
        }
        
        return output;
    }
	
	public String getHostCpu() {
		String dataArray = "";
		
		int b = 0;
		Map<String, List<String>> hrProcessorResult;
		final int ProcessorLoad = b++;
		
		String[] hrProcessorOids = {
				JOid.hrProcessorLoad
		};
//		String hrProcessorOids = JOid.ssCpuUser;
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
//			dataArray = snmp.getNode(hrProcessorOids);
			hrProcessorResult = snmp.getTable(hrProcessorOids);
			
			snmp.end();
			
			JMib.printRawMap(hrProcessorResult, false);
			
			if (hrProcessorResult != null && hrProcessorResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrProcessorResult.entrySet()) {
					
		            String ifKey = entry.getKey();
			    		if (hrProcessorResult.containsKey(ifKey)) {
			    			dataArray+= hrProcessorResult.get(ifKey).get(ProcessorLoad) + ",";
			    		}
		    		}
				}
			
		}
		catch (IOException ioe) {
			System.out.println("Read host cpu data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse host cpu data failed.");
		}
		
		return dataArray;
	}
	
	public String getHostMemory() {
		String dataArray = "";
		
		int a = 0;
		Map<String, List<String>> hrStorageResult;
		String hrStorageRam = "1.3.6.1.2.1.25.2.1.2";
		final int StorageType = a++, StorageDescr = a++, hrStorageAllocationUnits=a++, StorageSize = a++, StorageUsed = a++;
		
		String[] hrStorageOids = {
				JOid.hrStorageType,
				JOid.hrStorageDescr,
				JOid.hrStorageAllocationUnits,
				JOid.hrStorageSize,
				JOid.hrStorageUsed
		};
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			hrStorageResult = snmp.getTable(hrStorageOids);
			
			snmp.end();
			
			JMib.printRawMap(hrStorageResult, false);
			
			Double PhysicalMemorySize =0.0;
			Double PhysicalMemoryUsed=0.0;
			if (hrStorageResult != null && hrStorageResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrStorageResult.entrySet()) {
					String ifKey = entry.getKey();
					//System.out.println("hrStorageResult Index= " + ifKey);
					if (hrStorageResult.containsKey(ifKey)) {
						//hrStorageResult.get(ifKey).get(StorageDescr); 
						dataArray += hrStorageResult.get(ifKey).get(StorageSize) + "," + hrStorageResult.get(ifKey).get(StorageUsed) + ",";
					}
					
					if (sysObjectId.indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {						
						if (hrStorageResult.get(ifKey).get(StorageType).equals(hrStorageRam)) {
							PhysicalMemorySize = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageSize)) * Double.parseDouble(hrStorageResult.get(ifKey).get(hrStorageAllocationUnits)) / 1024;
							PhysicalMemoryUsed = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUsed)) * Double.parseDouble(hrStorageResult.get(ifKey).get(hrStorageAllocationUnits)) / 1024;
						}
						
						dataArray = PhysicalMemorySize + "," + PhysicalMemoryUsed + ",0,0,0,0,0";
					}
				}
			}
		}
		catch (IOException ioe) {
			System.out.println("Read host cpu data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse host cpu data failed.");
		}
		
		return dataArray;
	}
	
	public String getEdgeCoreCpu() {
		String dataArray = "";
		
		String hrProcessorOids = JOid.vnt5103CpuCurrent;
		
		try {
            JSnmp snmp = new JSnmp(snmpAddress, readCommunity, 2, snmpTimeout, 1);
			snmp.start();
			
			dataArray = snmp.getNode(hrProcessorOids);
			
			snmp.end();
			
		}
		catch (IOException ioe) {
			System.out.println("Read host cpu data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse host cpu data failed.");
		}
		
		return dataArray;
	}
	
	public String getEdgeCoreMemory() {
		String dataArray = "";
		
		String MemoryTotalOids = JOid.vnt5103MemoryTotal;
		String MemoryUsedOids = JOid.vnt5103MemoryUsed;
		String MemoryFreedOids = JOid.vnt5103MemoryFreed;
		String memoryTotal;
		String memoryUsed;
		String memoryFreed;
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			memoryTotal = snmp.getNode(MemoryTotalOids);
			memoryUsed = snmp.getNode(MemoryUsedOids);
			memoryFreed = snmp.getNode(MemoryFreedOids);
			
			snmp.end();
			
			dataArray = memoryTotal + "," + memoryUsed + "," + memoryFreed + ",";
			
		}
		catch (IOException ioe) {
			System.out.println("Read host cpu data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse host cpu data failed.");
		}
		
		return dataArray;
	}

	public String getHostDisk() {
		String dataArray = "";
		
		int a = 0;
		Map<String, List<String>> hrStorageResult;
		String hrStorageFixedDisk = "1.3.6.1.2.1.25.2.1.4";
		final int StorageType = a++, StorageUnit = a++, StorageSize = a++, StorageUsed = a++;
		
		String[] hrStorageOids = {
				JOid.hrStorageType,
				JOid.hrStorageAllocationUnits,
				JOid.hrStorageSize,
				JOid.hrStorageUsed
		};
		
		try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			
			hrStorageResult = snmp.getTable(hrStorageOids);
			
			snmp.end();
			
			JMib.printRawMap(hrStorageResult, false);
			
			if (hrStorageResult != null && hrStorageResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrStorageResult.entrySet()) {
					String ifKey = entry.getKey();
//		    		System.out.println("hrStorageResult Index= " + ifKey); 
		    		if (hrStorageResult.containsKey(ifKey)) {
		    			if (hrStorageResult.get(ifKey).get(StorageType).equals(hrStorageFixedDisk)) {
		    				double disk_size = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUnit)) * Double.parseDouble(hrStorageResult.get(ifKey).get(StorageSize));
		    				double disk_used = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUnit)) * Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUsed));
			    			dataArray+= String.valueOf(disk_size ) + "," + String.valueOf(disk_used ) + ","; 
		    			} else {
		    				continue;
		    			}
					}
				}
			}
		}
		catch (IOException ioe) {
			System.out.println("Read host cpu data failed.");
		}
		catch (NumberFormatException nfe) {
			System.out.println("Parse host cpu data failed.");
		}
		
		return dataArray;
	}
	// ====================================================================================================
	
	/**Initial system info from MIB
	 * @return
	 */
	private int initSystemInfo() {
		int flag = 1;
		String[] oidGroup = new String[]{JOid.sysDescr, JOid.sysUpTime, JOid.sysContact, JOid.sysName, JOid.sysLocation};
		String[] result = null;

		try {
			JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
//			System.out.println("initSystemInfo SnmpTimeout = " + snmpTimeout);
			snmp.start();
			result = snmp.getNodes(oidGroup);
			snmp.end();
		} catch (IOException e) {
		    flag = 0;
			System.out.println("Read system information failed.");
		}
		
		if (result != null && result.length == oidGroup.length) {
            this.sysDescr = result[0];
            this.sysUpTime = result[1];
            this.sysContact = result[2];
            this.sysName = result[3];
            this.sysLocation = result[4];
        }
        else {
            this.sysDescr = "";
            this.sysUpTime = "";
            this.sysContact = "";
            this.sysName = "";
            this.sysLocation = "";
            flag = 0;
        }

		return flag;
	}

	/**
	 * Initial phyAddr, localIp
	 * @return
	 */
	private boolean initIpEntity() {
		boolean flag = true;
		String localIp = "", phyAddr = "";
		
		try {
			JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
//			System.out.println("initIpEntity SnmpTimeout = " + snmpTimeout);
			snmp.start();
			
//			String[] resultIpEntity = snmp.getNextNodes(new String[]{JOid.ipAdEntAddr, JOid.ipAdEntIfIndex});	// for local IP and ifIndex of CPU
//			if (resultIpEntity != null && resultIpEntity.length == 2) {
//				localIp = resultIpEntity[0];
//				ifCPU = resultIpEntity[1];
//				phyAddr = snmp.getNode(JOid.ifPhysAddress + "." + ifCPU);			// NGN can't see this OID in MIB browser but still can read
//			}
			localIp = snmp.getNextNode(JOid.ipAdEntAddr);
            
			if (snmp.getNode(JOid.sysObjectID).indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {		//for VIAMagicView
            	this.ifCPU = snmp.getNode(JOid.ipAdEntIfIndex + "." + publicIp);         	
            }
            
			phyAddr = snmp.getNode(JOid.ifPhysAddress + "." + ifCPU);
            if (phyAddr.isEmpty() || phyAddr.startsWith("noSuch")) {		//for Dlink 1500 issue
            	phyAddr = snmp.getNode(JOid.lldpLocChassisId);
            }
			
			snmp.end();
		}
		catch (IOException e) {
			System.out.println("Read IpEntity failed.");
			flag = false;
		}
		
		this.localIp = localIp;
        this.phyAddr = phyAddr;
		
        if (localIp.isEmpty()) localIp = "[empty]";
        if (phyAddr.isEmpty()) phyAddr = "[empty]";
        logger.debug("localIp: " + localIp + ", ifCPU: " + ifCPU + ", phyAddr: " + phyAddr);
		
		return flag;
	}
	
	public void updateNonSnmpInterface(JModule module) {
		List<JInterface> interfaces = new ArrayList<JInterface>();
        int portIdCount = 1;
        for (int i = 0; i < module.getInfNum(); i++) {
            JInterface inf = new JInterface();
            if(module.getIfTypeList().get(i).equals("eth")){
            	inf.setPortId(portIdCount++);
            	inf.setStackId(1);
            }
            inf.setIfDescr("");
            inf.setInfIndex(i + 1);
            inf.setIfIndex(i + 1);
            inf.setIfType(module.getIfTypeList().get(i));
            inf.setJackType(module.getJackList().get(i));       // no reason it will wrong
            inf.setMaxSpeed(module.getSpeedList().get(i));
            inf.setLldpRemoteId("");
            inf.setManualRemoteIp("");
            inf.setManual(false);
            inf.setMonitored(false);
            interfaces.add(inf);
        }
        this.interfaces = interfaces;
	}

	public void updateInterface() {
		String[] ifTableOid = new String[] {
                JOid.ifIndex,
                JOid.ifDescr,
                JOid.ifType,
                JOid.ifPhysAddress
        };
		
		String[] poeStatusOids = {
				JOid.PoEPortAdminStatus
		};
		
		Map<String, List<String>> ifTableResult;
		Map<String, List<String>> poeStatusResult;
        
        try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            ifTableResult = snmp.getTable(ifTableOid);           // it will be an empty map if nothing read
            poeStatusResult = snmp.getTable(poeStatusOids);
            snmp.end();
        } catch (IOException e) {
            logger.debug("Read SNMP failed, exit(). Message: " + e.getMessage());
            return;
        }

        JTools.print(ifTableResult, false);
        JTools.print(poeStatusResult, false);
        
        List<JInterface> interfaces = new ArrayList<JInterface>();
        List<String> stackItems = new ArrayList<String>();
        int count = 1;
        String checkMacAddr = "";
        
        if (ifTableResult != null && !ifTableResult.isEmpty()) {
        	if (sysObjectId.indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {
				for (Map.Entry<String, List<String>> entry : ifTableResult.entrySet()) {
					JInterface inf = new JInterface();
					String ifIndex = entry.getValue().get(0);
					String ifDescr = entry.getValue().get(1);
					String ifType = entry.getValue().get(2);
					String ifPhysAddress = entry.getValue().get(3);

					if (ifCPU.equals(ifIndex)) {
						//System.out.println(count + "," + ifCPU + "," + ifDescr + "," + ifType + "," + ifPhysAddress);

						inf.setInfIndex(count++);
						inf.setIfIndex(Integer.parseInt(ifCPU));
						inf.setIfDescr(ifDescr);

						if (inf.getIfDescr().length() < 4) {
							inf.setIfType(ifType.equals("6") || ifType.equals("117") ? "eth" : "unknown");
						} else {
							inf.setIfType(ifType.equals("6") && inf.getIfDescr().substring(0, 4).equalsIgnoreCase("vlan") ? "unknown" : ifType.equals("6") || ifType.equals("117") ? "eth" : "unknown");
						}
						inf.setIfPhysAddress(ifPhysAddress);

						if (ifType.equals("6") || ifType.equals("117")) {
							if (inf.getIfDescr().indexOf("TenGiga") >= 0) {
								inf.setIfSpeed("10000");
								//System.out.println("speed="+inf.getIfSpeed()+" Ten");
							} else if (inf.getIfDescr().indexOf("FastEth") >= 0) {
								inf.setIfSpeed("100");
								//System.out.println("speed="+inf.getIfSpeed()+" Fast");
							} else if (inf.getIfDescr().indexOf("Gigabit") >= 0 || inf.getIfDescr().indexOf("GE") >= 0 || ifType.equals("117") || ifType.equals("6")) {
								inf.setIfSpeed("1000");
								//System.out.println("speed="+inf.getIfSpeed());
							}
						} else {
							inf.setIfSpeed("0");
						}

						inf.setLldpRemoteId("");
						inf.setManualRemoteIp("");
						inf.setManual(false);
						inf.setMonitored(false);
						inf.setPoePort(false);
						interfaces.add(inf);
					}
				}
			} else {
				for (Map.Entry<String, List<String>> entry : ifTableResult.entrySet()) {
					JInterface inf = new JInterface();
					String ifIndex = entry.getValue().get(0);
					String ifDescr = entry.getValue().get(1);
					String ifType = entry.getValue().get(2);
					String ifPhysAddress = entry.getValue().get(3);

					inf.setInfIndex(count++);
					inf.setIfIndex(Integer.parseInt(ifIndex));
					inf.setIfDescr(ifDescr);
					if (inf.getIfDescr().length() < 4) {
						inf.setIfType(ifType.equals("6") || ifType.equals("117") ? "eth" : "unknown");
					} else {
						inf.setIfType(ifType.equals("6") && inf.getIfDescr().substring(0, 4).equalsIgnoreCase("vlan") ? "unknown" : ifType.equals("6") || ifType.equals("117") ? "eth" : "unknown");
					}
					inf.setIfPhysAddress(ifPhysAddress);

					if (ifType.equals("6") || ifType.equals("117")) {
						if (inf.getIfDescr().indexOf("TenGiga") >= 0) {
							inf.setIfSpeed("10000");
							//System.out.println("speed="+inf.getIfSpeed()+" Ten");
						} else if (inf.getIfDescr().indexOf("FastEth") >= 0) {
							inf.setIfSpeed("100");
							//System.out.println("speed="+inf.getIfSpeed()+" Fast");
						} else if (inf.getIfDescr().indexOf("Gigabit") >= 0 || inf.getIfDescr().indexOf("GE") >= 0 || ifType.equals("117") || ifType.equals("6")) {
							inf.setIfSpeed("1000");
							//System.out.println("speed="+inf.getIfSpeed());
						}
					} else {
						inf.setIfSpeed("0");
					}

					if (brandName.equals("CISCO") && inf.getIfType().equals("eth")) {
						if (!checkMacAddr.equalsIgnoreCase(inf.getIfPhysAddress().substring(0, 11))) {
							checkMacAddr = inf.getIfPhysAddress().substring(0, 11);
							if (stackItems.isEmpty()) {
								stackItems.add(checkMacAddr);
							} else {
								boolean isNotExist = true;
								for (int i = 0; i < stackItems.size(); i++) {
									if (stackItems.get(i).equals(checkMacAddr)) {
										isNotExist = false;
									}
								}
								if (isNotExist) {
									stackItems.add(checkMacAddr);
								}
							}
						}
					}

					inf.setLldpRemoteId("");
					inf.setManualRemoteIp("");
					inf.setManual(false);
					inf.setMonitored(false);
					inf.setPoePort(false);
					interfaces.add(inf);
				}
			}
            this.interfaces = interfaces;
        }
        else {
            logger.debug(publicIp + ": ifTable is empty.");
        }
        
        if (poeStatusResult != null && !poeStatusResult.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : poeStatusResult.entrySet()) {
            	String[] indexes = entry.getKey().split("\\.");
            	
            	for(JInterface inf : this.interfaces){
            		if(indexes[1].equals(Integer.toString(inf.getIfIndex()))){
                		inf.setPoePort(true);
                	}
            	}
            }
        }
        else {
            logger.debug(publicIp + ": poeTable is empty.");
        }
        
        
        
        int portCount = 1;
        checkMacAddr = "";
        
        
        for(JInterface inf : this.interfaces){
        	if(brandName.equals("CISCO") && inf.getIfType().equals("eth")){
	        	for(int i=0; i<stackItems.size(); i++){
		        	if(inf.getIfPhysAddress().substring(0, 11).equalsIgnoreCase(stackItems.get(i))){
		        		inf.setStackId(i+1);
		        	}
	        	}
        	}
        	else if(inf.getIfType().equals("eth")){
        		inf.setStackId(1);
        	}
        	else {
        		inf.setStackId(0);
        	}
        	
        }
        if(brandName.equals("CISCO")){
	        for(int i=0; i<stackItems.size(); i++){
	        	portCount=1;
		        for(JInterface inf : this.interfaces){
		        	if(inf.getIfType().equals("eth")){
	        			if(inf.getStackId() == i+1){
	        				inf.setPortId(portCount++);
	        			}
		        	}
		        	else {
		        		inf.setPortId(0);
		        	}
		        }
	        }
        }
        else {
        	for(JInterface inf : this.interfaces){
	        	if(inf.getIfType().equals("eth")){
	        		inf.setPortId(portCount++);
	        	}
	        	else {
	        		inf.setPortId(0);
	        	}
        	}
        }
        
        if (stackItems != null && !stackItems.isEmpty()) {
        	this.stackNum = stackItems.size();
        }
        else {
        	this.stackNum = 1;
        }
        
        /*for (int i=0;i<stackItems.size();i++){
    		System.out.println("stackItems.get(i)= "+stackItems.get(i));
    	}*/
        /*for(JInterface inf : this.interfaces){
        	System.out.println("IfIndex= "+inf.getIfIndex()+" portId= "+inf.getPortId()+" stackCount= "+inf.getStackId()+" is poe port= "+inf.isPoePort());

        }*/    
	}
	
	private boolean updateUpTime() {
		try {
			JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 0) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 0, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			String upTime = snmp.getNode(JOid.sysUpTime);
			snmp.end();
			
			if (upTime != null && !upTime.isEmpty()) {
				this.sysUpTime = upTime;
				return true;
			}
			else {
				System.out.println("IP: " + publicIp + " read up time error.");
			}
		} catch (IOException e) {
			System.out.println("IP: " + publicIp + " read up time failed.");
		}

		return false;
	}
	
	private void initStpInfo() {
	    updateStpInfo();
	}
	
	public void updateStpInfo() {
	    String[] oidStp = new String[] {
                JOid.dot1dStpPriority,
                JOid.dot1dStpDesignatedRoot,
                JOid.dot1dStpRootCost,
                JOid.dot1dStpRootPort
        };
        String[] resultStp = null;
        int a = 0;
        final int PRIORITY = a++, DESIGNROOT = a++, ROOTCOST = a++, ROOTPORT = a++;
        int priority = -1, rootCost = -1, rootPort = -1;
        String designatedRoot = "";
        boolean success = true;
        
        try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            resultStp = snmp.getNodes(oidStp);
            snmp.end();
            
            if (resultStp == null || resultStp.length != oidStp.length) success = false;
        }
        catch (IOException e) {
            logger.debug("Read stp info failed, exit(). Message: " + e.getMessage());
            success = false;
        }
        
        if (success) {
            try {
                priority = Integer.parseInt(resultStp[PRIORITY]);
                rootCost = Integer.parseInt(resultStp[ROOTCOST]);
                rootPort = Integer.parseInt(resultStp[ROOTPORT]);
                if (resultStp[DESIGNROOT].length() == 23)
                    designatedRoot = resultStp[DESIGNROOT].substring(6);
                
                this.stpPriority = priority;
                this.stpRootCost = rootCost;
                this.stpRootPort = rootPort;
                this.stpDesignatedRoot = designatedRoot;
                this.stpValid = true;
                //logger.debug(String.format("%s PRI: %d, DROOT: %s, ROOTC: %d, ROOTP: %d", publicIp ,priority, designatedRoot, rootCost, rootPort));
                
                return;
            }
            catch (NumberFormatException e) {
                logger.debug("parse int for stp info failed.");     // TODO check if result has noSuch string
            }
        }

        this.stpPriority = -1;
        this.stpRootCost = -1;
        this.stpRootPort = -1;
        this.stpDesignatedRoot = "";
        this.stpValid = false;
	}
	
	private void initLldpInfo() {
	    updateLldpInfo(null);
	}
	
	public void updateLldpInfo(Map<String, String[]> mapper) {
	    if (interfaces == null) {
	        logger.debug("error, interface is null, it should be initialed prior this function.");
	        return;
	    }
	    System.out.println("=====update lldp info: " + publicIp);
	    
	    // Step 1: Read remote table to get lldpPort and correlated remote macAddr
	    String[] ogLldpRemTable = new String[] {
                JOid.lldpRemChassisIdSubtype,
                JOid.lldpRemChassisId
        };
        Map<String, List<String>> resultLldpRemote;
        try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            resultLldpRemote = snmp.getTable(ogLldpRemTable);
            snmp.end();
        }
        catch (IOException e) {
            logger.debug("Read SNMP failed, exit(). Message: " + e.getMessage());
            return;
        }
        JTools.print(resultLldpRemote, false);
        if (resultLldpRemote == null || resultLldpRemote.isEmpty()) {
        	logger.debug(publicIp + ": remote table is empty.");
        	return;
        }
        for(JInterface inf : interfaces){
        	inf.setLldpRemoteId("");
        	inf.setLldpRemoteIdType("");
        	inf.setLldpRemoteIp("");
        }
        
        // Step 2: use lldpPort to find correlated ifDesc, and then find the ifIndex
        for (Map.Entry<String, List<String>> entry : resultLldpRemote.entrySet()) {
        	String[] indexes = entry.getKey().split("\\.");             // there should be three indexes in remote table
        	if (indexes.length != 3) {
        		logger.debug("Index length from remote table is error.");
        		continue;
        	}
        	if (entry.getValue().size() != ogLldpRemTable.length) {
        		logger.debug("Value size from remote table is error.");
        		continue;
        	}
        	
        	String lldpLocalPort = indexes[1];
        	String oidLocPortIdSubtype = JOid.lldpLocPortIdSubtype + "." + lldpLocalPort;
        	String oidLocPortId = JOid.lldpLocPortId + "." + lldpLocalPort;
        	String remoteId = entry.getValue().get(1);          // lldpRemChassisId
        	String remoteIdType = entry.getValue().get(0);      // lldpRemChassisIdSubtype 4: macAddress
        	
//        	String locPortIdSubtype;
        	String locPortId;
        	String locPortDesc;
        	try {
                JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
    				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
                snmp.start();
                String[] result = snmp.getNodes(new String[] {oidLocPortIdSubtype, oidLocPortId});
                //System.out.println("result= "+Arrays.toString(result));
//                locPortIdSubtype = result[0];
                if(result !=null && result.length > 1){
                	locPortId = result[1];
                }
                else {
                	locPortId = "";
                }
                locPortDesc = snmp.getNode(JOid.lldpLocPortDesc + "." + lldpLocalPort);		// directly point to the cell
                snmp.end();
            }
        	catch (IOException e) {
                logger.debug("Loc Port Desc Read SNMP failed, skip at locPort " + lldpLocalPort);
                continue;
            }
        	
        	// TODO Either LocPortId nor LocPortDesc could not say that is always right, using LocPortDesc here and work around also needs
        	if (sysObjectId.equals("1.3.6.1.4.1.171.10.126.3.1")) {		// D-Link, DGS-1500-28P
        		System.out.println("locPortId:" + locPortId + " uses special case that directly match ifIndex.");
        		boolean isEnter = false;
        		for (JInterface inf : interfaces) {
        			if (!inf.getIfType().equals("eth")) continue;
        			
        			if (locPortId.equals(String.valueOf(inf.getIfIndex())))	{
        				inf.setLldpRemoteId(remoteId);
        				inf.setLldpRemoteIdType(remoteIdType);
        				System.out.println("locPort: " + lldpLocalPort + ", locPortId: " + locPortId + " is related to ifIndex: " + inf.getIfIndex());
        				isEnter = true;
        				break;
        			}
        		}
        		if (!isEnter) System.out.println("no match");
        	}
        	else {
        		for (JInterface inf : interfaces) {
        			if (!inf.getIfType().equals("eth")) continue;

        			if (inf.getIfDescr().isEmpty()) {		// should happen when read back from db
        				try {
        					JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
        						new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
        					snmp.start();
        					inf.setIfDescr(snmp.getNode(JOid.ifDescr + "." + inf.getIfIndex()));
        					snmp.end();
        				}
        				catch (IOException e) {
        					logger.debug("Update ifDesc Read SNMP failed, skip at if " + inf.getIfIndex());
        					continue;
        				}
        			}

        			// find the interface match by desc between LocPortTable and ifTable
        			if (inf.getIfDescr().equals(locPortDesc)) {
        				inf.setLldpRemoteId(remoteId);
        				inf.setLldpRemoteIdType(remoteIdType);
        				//System.out.println("locPort: " + lldpLocalPort + ", locPortDesc: " + locPortDesc + " is related to ifIndex: " + inf.getIfIndex());
        				break;
        			}
        		}
        	}
//        	if (locPortIdSubtype.equals("1")) {					// 1: interfaceAlias
//        		System.out.println("locPortId:" + locPortId + " uses ifAlias.");
//        		boolean isEnter = false;
//        		for (JInterface inf : interfaces) {
//        			if (!inf.getIfType().equals("eth")) continue;
//        			
//        			if (inf.getIfAlias().isEmpty()) {
//        				try {
//        					JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
//        						new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
//        					snmp.start();
//        					inf.setIfAlias(snmp.getNode(JOid.ifAlias + "." + inf.getIfIndex()));
//        					snmp.end();
//        				}
//        				catch (IOException e) {
//        					logger.debug("Update ifAlias Read SNMP failed, skip at locPort " + lldpLocalPort);
//        					continue;
//        				}
//        			}
//        			
//        			if (inf.getIfAlias().equals(locPortId)) {
//        				inf.setLldpRemoteId(remoteId);
//        				inf.setLldpRemoteIdType(remoteIdType);
//        				System.out.println("locPort: " + lldpLocalPort + ", locPortId(ifAlias): " + locPortId + " is related to ifIndex: " + inf.getIfIndex());
//        				isEnter = true;
//        				break;
//        			}
//        		}
//        		if (!isEnter) System.out.println("no match");
//        	}
//        	else if (locPortIdSubtype.equals("3")) {			// 3: macAddress
//        		System.out.println("locPortId:" + locPortId + " uses macAddress.");
//        		boolean isEnter = false;
//        		for (JInterface inf : interfaces) {
//        			if (!inf.getIfType().equals("eth")) continue;
//        			
//        			if (inf.getIfPhysAddress().isEmpty()) {
//        				try {
//        					JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
//        						new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
//        					snmp.start();
//        					inf.setIfPhysAddress(snmp.getNode(JOid.ifPhysAddress + "." + inf.getIfIndex()));
//        					snmp.end();
//        				}
//        				catch (IOException e) {
//        					logger.debug("Update ifMac Read SNMP failed, skip at locPort " + lldpLocalPort);
//        					continue;
//        				}
//        			}
//        			
//        			if (inf.getIfPhysAddress().equals(locPortId)) {
//        				inf.setLldpRemoteId(remoteId);
//        				inf.setLldpRemoteIdType(remoteIdType);
//        				System.out.println("locPort: " + lldpLocalPort + ", locPortId(ifMac): " + locPortId + " is related to ifIndex: " + inf.getIfIndex());
//        				isEnter = true;
//        				break;
//        			}
//        		}
//        		if (!isEnter) System.out.println("no match");
//        	}
//        	else if (locPortIdSubtype.equals("5")) {	// 5: interfaceName, maybe we can say that if this type is interfaceName, ifXTable should be supported
//        		System.out.println("locPortId:" + locPortId + " uses ifName.");
//        		boolean isEnter = false;
//        		for (JInterface inf : interfaces) {
//        			if (!inf.getIfType().equals("eth")) continue;
//        			
//        			if (inf.getIfName().isEmpty()) {
//        				try {
//        					JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
//        						new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
//        					snmp.start();
//        					inf.setIfName(snmp.getNode(JOid.ifName + "." + inf.getIfIndex()));
//        					snmp.end();
//        				}
//        				catch (IOException e) {
//        					logger.debug("Update ifName Read SNMP failed, skip at locPort " + lldpLocalPort);
//        					continue;
//        				}
//        			}
//        			
//        			if (inf.getIfName().equals(locPortId)) {
//        				inf.setLldpRemoteId(remoteId);
//        				inf.setLldpRemoteIdType(remoteIdType);
//        				System.out.println("locPort: " + lldpLocalPort + ", locPortId(ifName): " + locPortId + " is related to ifIndex: " + inf.getIfIndex());
//        				isEnter = true;
//        				break;
//        			}
//        		}
//        		if (!isEnter) System.out.println("no match");
//        	}
        }
	}
	
	private void updatePhyAddrFromLldp() {
	    String localChassisId = null;
	    
	    try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            localChassisId = snmp.getNode(JOid.lldpLocChassisId);
            snmp.end();
        }
        catch (IOException e) {
            logger.debug("Read local chassis id failed, exit(). Message: " + e.getMessage());
            return;
        }
	    
	    if (localChassisId != null && !localChassisId.isEmpty() && !localChassisId.startsWith("noSuch")) {
	        this.phyAddr = localChassisId;
	        logger.debug("update phyaddr from Lldp, " + localChassisId);
	    }
	}
	
	public void updateVlan() {
	    if (interfaces == null) {
	        logger.debug("error, interface is null, it should be initialed prior this function.");
	        return;
	    }
	    String[] vlanListOids = {
				JOid.dot1qVlanCurrentEgressPorts,
				JOid.dot1qVlanCurrentFdbId,
				JOid.dot1qVlanCurrentUntaggedPorts,
				JOid.dot1qVlanCurrentStatus
		};
		String[] dot1qPvidOids = {
				JOid.dot1qPvid
		};
		Map<String, List<String>> dyamicVlanListResult,pvidResult;
		int portNum;
		int a = 0;
		final int EgressPorts = a++, Vlanid = a++, PVID = a++;
        
        try {
            JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
            snmp.start();
            dyamicVlanListResult = snmp.getTable(vlanListOids);
			pvidResult = snmp.getTable(dot1qPvidOids);
            snmp.end();
        } catch (IOException e) {
            logger.debug("Read SNMP failed, exit(). Message: " + e.getMessage());
            return;
        }
        
        portNum = getInfNum() == 0 ? pvidResult.size() : getInfNum();			// choose the smaller as actual port number
        JMib.printRawMap(dyamicVlanListResult, false);
		JMib.printRawMap(pvidResult, false);
		
		if (pvidResult != null && pvidResult.size() != 0) {
			for (Map.Entry<String, List<String>> entry : pvidResult.entrySet()) {
				String value = entry.getValue().get(0);
				JInterface inf = interfaces.get(PVID);
	            inf.setPvid(value);
	            //System.out.println("PVID="+inf.getPvid());
			}
		} else {
            logger.debug("PVID  info failed.");     // TODO check if result has noSuch string
		}
        
		/*List<String> vid = new ArrayList<String>();
		List<String[]> tempVlanList = new ArrayList<String[]>();
		if (dyamicVlanListResult != null && dyamicVlanListResult.size() != 0) {
			for (Map.Entry<String, List<String>> entry : dyamicVlanListResult.entrySet()) {
				String ifKey = entry.getKey();
	    		if (dyamicVlanListResult.containsKey(ifKey)) {
		    		String vlanEgressPorts = dyamicVlanListResult.get(ifKey).get(EgressPorts).replace(":","");
	    			String vlanEgressPorts1 = "1"+vlanEgressPorts;
				    char[] vlanEgressPorts2 = null;
				    try {
					    vlanEgressPorts2 = Long.toBinaryString(Long.parseLong(vlanEgressPorts1,16)).toCharArray();
		            }
		            catch (NumberFormatException e) {
		                logger.debug("parse int for Current VLAN EgressPorts info failed.");     // TODO check if result has noSuch string
		            }
				    
				    String[] portVLAN = new String[portNum];
	    			for (int i = 1; i <= portNum; i++) {
	    				if (vlanEgressPorts2[i]=='1')
	    					portVLAN[i-1] = dyamicVlanListResult.get(ifKey).get(Vlanid);
	    				//System.out.println("portVLAN="+portVLAN[i-1]);
	    			}
	    			tempVlanList.add(portVLAN);
	    			vid.add(dyamicVlanListResult.get(ifKey).get(Vlanid));
	    			//System.out.println("PortVLAN="+Arrays.toString(portVLAN));
				}
			}
			//System.out.println("vid"+vid);
			//System.out.println("");
			List<String[]> List = new ArrayList<String[]>();
			int columCount = 0;
			while(columCount<tempVlanList.get(0).length){
				String[] newVlan = new String[tempVlanList.size()];
				for(int i=0;i<tempVlanList.size();i++){
					newVlan[i] = tempVlanList.get(i)[columCount];
				}
				List.add(newVlan);
				
				JInterface inf = interfaces.get(columCount);
	            inf.setVlanList(newVlan);
	            columCount++;
			}
		} else {
            logger.debug(" Current VLAN  info failed.");     // TODO check if result has noSuch string
		}*/
		
        
	}
	
	public List<JLinkViewItem> readLinkView() {
	    Logger logger = Logger.getLogger(JLinkView.class);

    	String[] statusOids = {
    			JOid.ifAdminStatus,
    			JOid.ifOperStatus
    	};

    	Map<String, List<String>> retColumns = null;
		
		JLinkView linkView = new JLinkView();
		List<JLinkViewItem> items = new ArrayList<JLinkViewItem>();

		try {
            JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			retColumns = snmp.getTable(statusOids);
			snmp.end();
	        //logger.debug("Device IP = " + publicIp() +" Link View Get SNMP Table");
		} catch (IOException e) {
			System.out.println("Read Device Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Link View Read Device Failed");   
			return items;
		}
		JMib.printRawMap(retColumns, false);
       
		if (retColumns != null) {
    		for (Map.Entry<String, List<String>> entry : retColumns.entrySet()) {
	        	for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			linkView.addLinkView(inf.getPortId(), inf.getIfIndex(), inf.getStackId(), entry.getValue().get(0), entry.getValue().get(1));
	        		}
	        	}
    		}
    	}

    	return linkView.getLinkViewItems();
    }
	
	public List<JPortStatusItem> readPortStatus() {
		String[] statusOids = {
				JOid.ifSpeed,
				JOid.ifAdminStatus,
				JOid.ifOperStatus,
				JOid.dot3StatsDuplexStatus,
				JOid.dot3StatsRateControlAbility,
				JOid.dot3StatsRateControlStatus
		};
		String[] negoOids = {
				JOid.ifMauAutoNegSupported,					// can be anticipated read
				JOid.ifMauAutoNegoAdminStatus
		};
		
		Map<String, List<String>> statusResult, negoResult;
		
		JPortStatus portStatus = new JPortStatus();
		List<JPortStatusItem> items = new ArrayList<JPortStatusItem>();

		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			statusResult = snmp.getTable(statusOids);
			negoResult = snmp.getTable(negoOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.debug("Device IP = " + publicIp +" Port Status Read Device Failed");   
			return items;
		}

		JMib.printRawMap(statusResult, false);
		JMib.printRawMap(negoResult, false);
		
		if(statusResult !=null){
			for (Map.Entry<String, List<String>> entry : statusResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			String speed = entry.getValue().get(0);
	        			portStatus.addPortStatus(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					speed.endsWith("000000000") ? speed.substring(0, speed.length() - 9) + "G" : speed.endsWith("000000") ? speed.substring(0, speed.length() - 6) + "M" : speed, 
	        					entry.getValue().get(1).equals("1") ? "Up" : "Down", 
	        					entry.getValue().get(2).equals("1") ? "Up" : "Down", 
	        					entry.getValue().get(3).equals("3") ? "Full" : entry.getValue().get(3).equals("2") ? "Half" : "unknown", 
	        					entry.getValue().get(4).equals("1") ? "Enable" : "Disable",
	        					entry.getValue().get(5).equals("2") ? "On" : "Off");
	        		}
				}
			}
		}
		
		if(negoResult !=null){
			for (Map.Entry<String, List<String>> entry : negoResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		String[] indexes = entry.getKey().split("\\.");
	        		if(Integer.toString(inf.getIfIndex()).equals(indexes[0]) && inf.getIfType().equals("eth")){
	        			portStatus.addNego(Integer.parseInt(indexes[0]),
	        					entry.getValue().get(0).equals("1") ? "Yes" : entry.getValue().get(0).equals("2") ? "No" : "--", 
	        					entry.getValue().get(1).equals("1") ? "Enabled" : entry.getValue().get(1).equals("2") ? "Disabled" : "--");
	        		}
				}
			}
		}
		
		return portStatus.getPortStatusItems();
	}
	
	public List<JVlanStatusItem> readVlanStatus() {
		String[] vlanInfOids = {
				JOid.dot1qPvid,
				JOid.dot1qPortAcceptableFrameType,
				JOid.dot1qPortIngressFiltering,
				JOid.dot1qPortGvrpStatus
		};
		
		Map<String, List<String>> vlanInfResult;
		
		JVlanStatus vlanStatus = new JVlanStatus();
		List<JVlanStatusItem> items = new ArrayList<JVlanStatusItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			vlanInfResult = snmp.getTable(vlanInfOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" VLAN Status Global Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(vlanInfResult, false);
		
		if(vlanInfResult != null){
			for (Map.Entry<String, List<String>> entry : vlanInfResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			vlanStatus.addVlanStatus(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					entry.getValue().get(0),
	        					entry.getValue().get(1).equals("1") ? "All Frames" : "Tagged Frames",
	        					entry.getValue().get(2).equals("1") ? "Enabled" : "Disabled",
	        					entry.getValue().get(3).equals("1") ? "Enabled" : "Disabled");
	        		}
				}
			}
		}
		
		return vlanStatus.getVlanStatusItems();
	}
	
	public List<JVlanStatusItem> readGlobalVlanStatus() {
		String[] vlanGlobalOids = {
				JOid.dot1qVlanStaticName,
				JOid.dot1qVlanStaticStatus
		};
		Map<String, List<String>> globalVlanStatusResult;
		JVlanStatus globalVlanStatus = new JVlanStatus();
		List<JVlanStatusItem> items = new ArrayList<JVlanStatusItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			globalVlanStatusResult = snmp.getTable(vlanGlobalOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" VLAN Status Global Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(globalVlanStatusResult, false);
		
		if(globalVlanStatusResult != null){
			for (Map.Entry<String, List<String>> entry : globalVlanStatusResult.entrySet()) {
				globalVlanStatus.addGlobalVlanStatus(entry.getKey(), 
						entry.getValue().get(0), 
						entry.getValue().get(1).equals("1") ? "Enabled" : "Disabled");
			}
		}
		
		return globalVlanStatus.getVlanStatusItems();
		
	}
	
	public List<JStaticVlanListItem> readStaticVlan() {
		String[] vlanListOids = {
				JOid.dot1qVlanStaticEgressPorts,
				JOid.dot1qVlanStaticForbiddenEgressPorts,
				JOid.dot1qVlanStaticUntaggedPorts
		};
		
		Map<String, List<String>> staticVlanResult;
		JStaticVlanList staticVlan = new JStaticVlanList();
		List<JStaticVlanListItem> items = new ArrayList<JStaticVlanListItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			staticVlanResult = snmp.getTable(vlanListOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Static VLAN List Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(staticVlanResult, false);
		
		int portNum = 0;
		for (int i = 0; i < interfaces.size(); i++) {
			JInterface inf = interfaces.get(i);
			if(inf.getIfType().equals("eth")){
				portNum++;
			}
		}
		
		if(staticVlanResult != null){
			for (Map.Entry<String, List<String>> entry : staticVlanResult.entrySet()) {
				String vlanEgressPorts = entry.getValue().get(0).replace(":","");
    			String vlanEgressPorts1 = "1"+vlanEgressPorts.substring(0, portNum/4+1);
			    char[] vlanEgressPorts2 = null;
			    try {
	    			vlanEgressPorts2 = Long.toBinaryString(Long.parseLong(vlanEgressPorts1,16)).toCharArray();
	            }
	            catch (NumberFormatException e) {
	                logger.debug("parse int for Static VLAN EgressPorts info failed.");
	            }
			    
    			String vlanForbiddenPorts = entry.getValue().get(1).replace(":","");
    			String vlanForbiddenPorts1 = "1"+vlanForbiddenPorts.substring(0, portNum/4+1);
			    char[] vlanForbiddenPorts2 = null;
			    try {
		            vlanForbiddenPorts2 = Long.toBinaryString(Long.parseLong(vlanForbiddenPorts1,16)).toCharArray();
	            }
	            catch (NumberFormatException e) {
	                logger.debug("parse int for Static VLAN ForbiddenPorts info failed.");
	            }	
    	    	
    			String vlanUntaggedPorts = entry.getValue().get(2).replace(":","");
    			String vlanUntaggedPorts1 = "1"+vlanUntaggedPorts.substring(0, portNum/4+1);
			    char[] vlanUntaggedPorts2 = null;
			    try {
				    vlanUntaggedPorts2 = Long.toBinaryString(Long.parseLong(vlanUntaggedPorts1,16)).toCharArray();
	            }
	            catch (NumberFormatException e) {
	                logger.debug("parse int for Static VLAN UntaggedPorts info failed.");
	            }
    	    	String[] PortState = new String[portNum];
    			for (int j = 1; j <= portNum; j++) {
    				if (vlanEgressPorts2[j]=='1' && vlanUntaggedPorts2[j]=='1' && vlanForbiddenPorts2[j]=='0')
    					PortState[j-1] = "U"; //Untagged
    				else if (vlanEgressPorts2[j]=='1' && vlanUntaggedPorts2[j]=='0' && vlanForbiddenPorts2[j]=='0')
    					PortState[j-1] = "T";//Tagged
    				else if (vlanEgressPorts2[j]=='0' && vlanUntaggedPorts2[j]=='0' && vlanForbiddenPorts2[j]=='1')
    					PortState[j-1] = "F";//Forbidden
    				else
    					PortState[j-1] = "N";//None
    			}
    			
    			staticVlan.addStaticVlan(
    					entry.getKey(),
    					entry.getValue().get(0),
    					entry.getValue().get(1),
    					entry.getValue().get(2),
    					PortState);
	        }
		}
		
		return staticVlan.getStaticVlanItems();
		
	}
	
	public List<JCurrentVlanListItem> readCurrentVlan() {
		String[] vlanListOids = { 
				JOid.dot1qVlanCurrentEgressPorts,
				JOid.dot1qVlanCurrentFdbId, 
				JOid.dot1qVlanCurrentUntaggedPorts,
				JOid.dot1qVlanCurrentStatus 
		};
		
		Map<String, List<String>> dyamicVlanListResult;
		JCurrentVlanList currentVlan = new JCurrentVlanList();
		List<JCurrentVlanListItem> items = new ArrayList<JCurrentVlanListItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			dyamicVlanListResult = snmp.getTable(vlanListOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Static VLAN List Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(dyamicVlanListResult, false);
		
		int portNum = 0;
		for (int i = 0; i < interfaces.size(); i++) {
			JInterface inf = interfaces.get(i);
			if(inf.getIfType().equals("eth")){
				portNum++;
			}
		}
		
		if(dyamicVlanListResult != null){
			for (Map.Entry<String, List<String>> entry : dyamicVlanListResult.entrySet()) {
				
    			String vlanEgressPorts = entry.getValue().get(0).replace(":", "");
				String vlanEgressPorts1 = "1" + vlanEgressPorts.substring(0, portNum/4+1);
				char[] vlanEgressPorts2 = null;
				try {
					vlanEgressPorts2 = Long.toBinaryString(Long.parseLong(vlanEgressPorts1, 16)).toCharArray();
				} catch (NumberFormatException e) {
					logger.debug("parse int for Current VLAN EgressPorts info failed.");
				}
				String vlanUntaggedPorts = entry.getValue().get(2).replace(":", "");
				String vlanUntaggedPorts1 = "1" + vlanUntaggedPorts.substring(0, portNum/4+1);
				char[] vlanUntaggedPorts2 = null;
				try {
					vlanUntaggedPorts2 = Long.toBinaryString(Long.parseLong(vlanUntaggedPorts1, 16)).toCharArray();
				} catch (NumberFormatException e) {
					logger.debug("parse int for Current VLAN UntaggedPorts info failed."); 
				}
				String[] PortState = new String[portNum];
				String[] PortVLAN = new String[portNum];
				for (int j = 1; j <= portNum; j++) {
					if (vlanEgressPorts2[j] == '1'
							&& vlanUntaggedPorts2[j] == '1') {
						PortState[j - 1] = "U"; // Untagged
						PortVLAN[j - 1] = entry.getValue().get(1); }
					else if (vlanEgressPorts2[j] == '1'
							&& vlanUntaggedPorts2[j] == '0') {
						PortState[j - 1] = "T";// Tagged
						PortVLAN[j - 1] = entry.getValue().get(1); } 
					else
						PortState[j - 1] = "N";// None
				}
				currentVlan.addCurrentVlan(
    					entry.getValue().get(0),
    					entry.getValue().get(1),
    					entry.getValue().get(2),
    					entry.getValue().get(2),
    					PortState,
    					PortVLAN);
	        }
		}
		
		return currentVlan.getCurrentVlanItems();
	}
	
	public List<JPoeStatusItem> readPoeStatus() {
		String[] poeStatusOids = {
				JOid.PoEPortAdminStatus,
				JOid.PoEPortDetectionStatus,
				JOid.PoEPortPowerPriority
		};
		
		Map<String, List<String>> poeInfResult;
		JPoeStatus poeStatus = new JPoeStatus();
		List<JPoeStatusItem> items = new ArrayList<JPoeStatusItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			poeInfResult = snmp.getTable(poeStatusOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" POE Status Interface Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(poeInfResult, false);
		
		if(poeInfResult != null){
			for (Map.Entry<String, List<String>> entry : poeInfResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		String[] indexes = entry.getKey().split("\\.");
	        		if(Integer.toString(inf.getIfIndex()).equals(indexes[1]) && inf.getIfType().equals("eth") && inf.isPoePort()){
	        			poeStatus.addPoeStatus(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					entry.getValue().get(0).equals("1") ? "Enabled" : "Disabled",
	        					entry.getValue().get(1).equals("1") ? "Disabled" :
	        					entry.getValue().get(1).equals("2") ? "Searching" : 
	        					entry.getValue().get(1).equals("3") ? "DeliveringPower" :	
	        					entry.getValue().get(1).equals("4") ? "Fault" :
	        					entry.getValue().get(1).equals("5") ? "Test" : "OtherFault",
	        					entry.getValue().get(2).equals("3") ? "Low" : 
	        					entry.getValue().get(2).equals("2") ?  "High" : "Critical");
	        		}
				}
			}
		}
		
		return poeStatus.getPoeStatusItems();
		
	}
	
	public List<JPoeStatusItem> readGlobalPoeStatus() {
		String poeStatusOids1 = JOid.PoEMainMaxAvailablePower + ".1";
		String poeStatusOids2 = JOid.PoEMainSysOperStatus + ".1";
		String poeStatusOids3 = JOid.PoEMainPowerConsumption + ".1";
		
		JPoeStatus globalPoeStatus = new JPoeStatus();
		List<JPoeStatusItem> items = new ArrayList<JPoeStatusItem>();
		String MainMaxPower="";
		String MainStatus="";
		String MainConsumption="";
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			
			snmp.start();
			
			MainMaxPower = snmp.getNode(poeStatusOids1);
			MainStatus = snmp.getNode(poeStatusOids2);
			MainConsumption = snmp.getNode(poeStatusOids3);
			
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" POE Status Global Read Device Failed ");   
			return items;
		}
		
		//System.out.println(" MainMaxPower= "+MainMaxPower+" MainStatus= "+MainStatus+" MainConsumption= "+MainConsumption);
		
		globalPoeStatus.addGlobalPoeStatus(MainMaxPower, MainStatus, MainConsumption);
		
		return globalPoeStatus.getPoeStatusItems();
	}
	
	public List<JPortStatisticsItem> readPortStatistics() {
		String[] statisticOids = {
				JOid.ifInOctets,
				JOid.ifInUcastPkts,
				JOid.ifOutOctets,
				JOid.ifOutUcastPkts,
				JOid.ifInDiscards,
				JOid.ifInErrors,
				JOid.ifOutDiscards,
				JOid.ifOutErrors,
				JOid.ifInUnknowns,
				JOid.ifInMulticastPkts,
				JOid.ifInBroadcastPkts,
				JOid.ifOutMulticastPkts,
				JOid.ifOutBroadcastPkts
		};
		
		Map<String, List<String>> statisticResult;
		
		JPortStatistics portStatistics = new JPortStatistics();
		List<JPortStatisticsItem> items = new ArrayList<JPortStatisticsItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			statisticResult = snmp.getTable(statisticOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Port Statistics Get SNMP Table");   
			return items;
		}	
		JMib.printRawMap(statisticResult, false);
		
		if(statisticResult != null){
			for (Map.Entry<String, List<String>> entry : statisticResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			portStatistics.addPortStatistics(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					entry.getValue().get(0).equals("null") ? "--" : entry.getValue().get(0),
	        					entry.getValue().get(1).equals("null") ? "--" : entry.getValue().get(1),
	        					entry.getValue().get(2).equals("null") ? "--" : entry.getValue().get(2),
	        					entry.getValue().get(3).equals("null") ? "--" : entry.getValue().get(3),
	        					entry.getValue().get(4),
	        					entry.getValue().get(5),
	        					entry.getValue().get(6),
	        					entry.getValue().get(7),
	        					entry.getValue().get(8),
	        					entry.getValue().get(9).equals("null") ? "--" : entry.getValue().get(9),
	        					entry.getValue().get(10).equals("null") ? "--" : entry.getValue().get(10),
	        					entry.getValue().get(11).equals("null") ? "--" : entry.getValue().get(11),
	        					entry.getValue().get(12).equals("null") ? "--" : entry.getValue().get(12));
	        		}
				}
			}
		}
		
		return portStatistics.getPortStatisticsItem();
	}
	
	public List<JEtherlikeStatisticsItem> readEtherlikeStatistics() {
	    String[] dot3StatOids = {
				JOid.dot3StatsAlignmentErrors,
				JOid.dot3StatsFCSErrors,
				JOid.dot3StatsInternalMacReceiveErrors,
				JOid.dot3StatsSymbolErrors,
				JOid.dot3StatsSQETestErrors,
				JOid.dot3StatsInternalMacTransmitErrors,
				JOid.dot3StatsCarrierSenseErrors,
				JOid.dot3StatsFrameTooLongs,
				JOid.dot3StatsDeferredTransmissions,
				JOid.dot3StatsLateCollisions,
				JOid.dot3StatsSingleCollisionFrames,
				JOid.dot3StatsMultipleCollisionFrames,
				JOid.dot3StatsExcessiveCollisions,
				JOid.dot3InPauseFrames,
				JOid.dot3OutPauseFrames
		};
		
		Map<String, List<String>> dot3StatResult;
		
		JEtherlikeStatistics etherlikeStatistics = new JEtherlikeStatistics();
		List<JEtherlikeStatisticsItem> items = new ArrayList<JEtherlikeStatisticsItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			dot3StatResult = snmp.getTable(dot3StatOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Etherlike Statistics Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(dot3StatResult, false);
		
		if(dot3StatResult != null){
			for (Map.Entry<String, List<String>> entry : dot3StatResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			etherlikeStatistics.addEtherlikeStatistics(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					entry.getValue().get(0),
	        					entry.getValue().get(1),
	        					entry.getValue().get(2),
	        					entry.getValue().get(3).equals("null") ? "--" : entry.getValue().get(3),
	        					entry.getValue().get(4).equals("null") ? "--" : entry.getValue().get(4),
	        					entry.getValue().get(5),
	        					entry.getValue().get(6).equals("null") ? "--" : entry.getValue().get(6),
	        					entry.getValue().get(7),
	        					entry.getValue().get(8),
	        					entry.getValue().get(9),
	        					entry.getValue().get(10),
	        					entry.getValue().get(11),
	        					entry.getValue().get(12),
	        					entry.getValue().get(13).equals("null") ? "--" : entry.getValue().get(13),
	        					entry.getValue().get(14).equals("null") ? "--" : entry.getValue().get(14));
	        		}
				}
			}
		}
		
		return etherlikeStatistics.getEtherlikeStatisticsItems();
		
	}
	
	public List<JRmonStatisticsItem> readRmonStatistics() {
		String[] rmonOids = {
				JOid.EtherStatsDropEvents,
				JOid.EtherStatsOctets,
				JOid.EtherStatsPkts,
				JOid.EtherStatsBroadcastPkts,
				JOid.EtherStatsMulticastPkts,
				JOid.EtherStatsCRCAlignErrors,
				JOid.EtherStatsUndersizePkts,
				JOid.EtherStatsOversizePkts,
				JOid.EtherStatsFragments,
				JOid.EtherStatsJabbers,
				JOid.EtherStatsCollisions,
				JOid.EtherStatsPkts64Octets,
				JOid.EtherStatsPkts65to127Octets,
				JOid.EtherStatsPkts128to255Octets,
				JOid.EtherStatsPkts256to511Octets,
				JOid.EtherStatsPkts512to1023Octets,
				JOid.EtherStatsPkts1024to1518Octets
		};
		
		Map<String, List<String>> rmonResult;
		
		JRmonStatistics rmonStatistics = new JRmonStatistics();
		List<JRmonStatisticsItem> items = new ArrayList<JRmonStatisticsItem>();
		
		try {
			JSnmp snmp  = snmpVersion<=2 ? new JSnmp(snmpAddress, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddress, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			rmonResult = snmp.getTable(rmonOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + publicIp +" Rmon Statistics Read Device Failed");   
			return items;
		}	
		JMib.printRawMap(rmonResult, false);
		
		if(rmonResult != null){
			for (Map.Entry<String, List<String>> entry : rmonResult.entrySet()) {
				for (int i = 0; i < interfaces.size(); i++) {
	        		JInterface inf = interfaces.get(i);
	        		if(Integer.toString(inf.getIfIndex()).equals(entry.getKey()) && inf.getIfType().equals("eth")){
	        			rmonStatistics.addRmonStatistics(inf.getPortId(), inf.getIfIndex(), inf.getStackId(),
	        					entry.getValue().get(0),
	        					entry.getValue().get(1),
	        					entry.getValue().get(2),
	        					entry.getValue().get(3),
	        					entry.getValue().get(4),
	        					entry.getValue().get(5),
	        					entry.getValue().get(6),
	        					entry.getValue().get(7),
	        					entry.getValue().get(8),
	        					entry.getValue().get(9),
	        					entry.getValue().get(10),
	        					entry.getValue().get(11),
	        					entry.getValue().get(12),
	        					entry.getValue().get(13),
	        					entry.getValue().get(14),
	        					entry.getValue().get(15),
	        					entry.getValue().get(16));
	        		}
				}
			}
		}
		
		return rmonStatistics.getRmonStatisticsItems();
		
	}

	// ====================================================================================================
	public final String getPublicIp() {
		return publicIp;
	}

	public final void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
		String[] ipArray = publicIp.split("\\.");                 // TODO: this section should put to a proper place
		int ip1 = Integer.parseInt(ipArray[0]);
		int ip2 = Integer.parseInt(ipArray[1]);
		int ip3 = Integer.parseInt(ipArray[2]);
		int ip4 = Integer.parseInt(ipArray[3]);
		this.publicIpFull = String.format("%03d.%03d.%03d.%03d", ip1, ip2, ip3, ip4);
		String statTableName = "STAT01_" + String.format("%03d%03d%03d%03d", ip1, ip2, ip3, ip4);
		this.dbStatisticsTableName = statTableName;
	}

	public final String getPublicIpFull() {
        return publicIpFull;
    }

    public final void setPublicIpFull(String publicIpFull) {
        this.publicIpFull = publicIpFull;
    }

    public String getParentIp() {
		return parentIp;
	}

	public void setParentIp(String parentIp) {
		this.parentIp = parentIp;
	}

	public final String getSnmpPort() {
		return snmpPort;
	}

	public final void setSnmpPort(String snmpPort) {
		this.snmpPort = snmpPort;
	}

	public int getSnmpVersion() {
		return snmpVersion;
	}

	public void setSnmpVersion(int snmpVersion) {
		this.snmpVersion = snmpVersion;
	}

	public final String getSnmpAddress() {
		return snmpAddress;
	}

	public final void setSnmpAddress(String snmpAddress) {
		this.snmpAddress = snmpAddress;
	}

	public final String getReadCommunity() {
        return readCommunity;
    }

    public final void setReadCommunity(String readCommunity) {
        this.readCommunity = readCommunity;
    }

    public final String getWriteCommunity() {
        return writeCommunity;
    }

    public final void setWriteCommunity(String writeCommunity) {
        this.writeCommunity = writeCommunity;
    }

    public final int getSnmpSupport() {
        return snmpSupport;
    }

    public final void setSnmpSupport(int snmpSupport) {
        this.snmpSupport = snmpSupport;
    }

    public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getAuthProtocol() {
		return authProtocol;
	}

	public void setAuthProtocol(String authProtocol) {
		this.authProtocol = authProtocol;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getPrivProtocol() {
		return privProtocol;
	}

	public void setPrivProtocol(String privProtocol) {
		this.privProtocol = privProtocol;
	}

	public String getPrivPassword() {
		return privPassword;
	}

	public void setPrivPassword(String privPassword) {
		this.privPassword = privPassword;
	}

	public final String getSysDescr() {
		return sysDescr;
	}

	public final void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	public final String getSysObjectId() {
		return sysObjectId;
	}

	public final void setSysObjectId(String sysObjectId) {
		this.sysObjectId = sysObjectId;
	}

	public final String getSysUpTime() {
		return sysUpTime;
	}

	public final void setSysUpTime(String sysUpTime) {
		this.sysUpTime = sysUpTime;
	}

	public final String getSysContact() {
		return sysContact;
	}

	public final void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}

	public final String getSysName() {
		return sysName;
	}

	public final void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public final String getSysLocation() {
		return sysLocation;
	}

	public final void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}

	public final String getBrandName() {
        return brandName;
    }

    public final void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public final String getModelName() {
        return modelName;
    }

    public final void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public final int getModelRevision() {
        return modelRevision;
    }

    public final void setModelRevision(int modelRevision) {
        this.modelRevision = modelRevision;
    }

    public final String getPhyAddr() {
		return phyAddr;
	}

	public final void setPhyAddr(String phyAddr) {
		this.phyAddr = phyAddr;
	}

	public final String getLocalIp() {
		return localIp;
	}

	public final void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public final String getIfCPU() {
		return ifCPU;
	}

	public final void setIfCPU(String ifCPU) {
		this.ifCPU = ifCPU;
	}

	public int getSnmpTimeout() {
		return snmpTimeout;
	}

	public void setSnmpTimeout(int snmpTimeout) {
		this.snmpTimeout = snmpTimeout;
	}

	public final int getInfNum() {
        return infNum;
    }

    public final void setInfNum(int infNum) {
        this.infNum = infNum;
    }

    public final int getRj45Num() {
		return rj45Num;
	}

	public final void setRj45Num(int rj45Num) {
		this.rj45Num = rj45Num;
	}

	public final int getFiberNum() {
		return fiberNum;
	}

	public final void setFiberNum(int fiberNum) {
		this.fiberNum = fiberNum;
	}

	public int getStackNum() {
		return stackNum;
	}

	public void setStackNum(int stackNum) {
		this.stackNum = stackNum;
	}

	public final List<JInterface> getInterfaces() {
		return interfaces;
	}

	public final void setInterfaces(List<JInterface> interfaces) {
		this.interfaces = interfaces;
	}

	public final Date getCreatedTime() {
		return createdTime;
	}

	public final void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public final String getAliasName() {
		return aliasName;
	}

	public final void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public final Date getLastSeen() {
		return lastSeen;
	}

	public final void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public final int isAlive() {
		return isAlive;
	}

	public final int getAlive() {
		return isAlive;
	}

	public final void setAlive(int isAlive) {
		this.isAlive = isAlive;
	}

	public final boolean isSupportHostResource() {
        return isSupportHostResource;
    }

    public final void setSupportHostResource(boolean isSupportHostResource) {
        this.isSupportHostResource = isSupportHostResource;
    }

    public final boolean isSupportLinkState() {
		return isSupportLinkState;
	}

	public final void setSupportLinkState(boolean isSupportLinkState) {
		this.isSupportLinkState = isSupportLinkState;
	}

	public final boolean isSupportNegoState() {
		return isSupportNegoState;
	}

	public final void setSupportNegoState(boolean isSupportNegoState) {
		this.isSupportNegoState = isSupportNegoState;
	}

	public final boolean isSupportRxTxOctet() {
		return isSupportRxTxOctet;
	}

	public final void setSupportRxTxOctet(boolean isSupportRxTxOctet) {
		this.isSupportRxTxOctet = isSupportRxTxOctet;
	}

	public final boolean isSupportPacketType() {
        return isSupportPacketType;
    }

    public final void setSupportPacketType(boolean isSupportPacketType) {
        this.isSupportPacketType = isSupportPacketType;
    }

    public final boolean isSupportRmon() {
        return isSupportRmon;
    }

    public final void setSupportRmon(boolean isSupportRmon) {
        this.isSupportRmon = isSupportRmon;
    }

    public final boolean isSupportPvid() {
        return isSupportPvid;
    }

    public final void setSupportPvid(boolean isSupportPvid) {
        this.isSupportPvid = isSupportPvid;
    }

    public final boolean isSupportVlan() {
		return isSupportVlan;
	}

	public final void setSupportVlan(boolean isSupportVlan) {
		this.isSupportVlan = isSupportVlan;
	}

	public final boolean isSupportGvrp() {
        return isSupportGvrp;
    }

    public final void setSupportGvrp(boolean isSupportGvrp) {
        this.isSupportGvrp = isSupportGvrp;
    }

    public final boolean isSupportPoe() {
		return isSupportPoe;
	}

	public final void setSupportPoe(boolean isSupportPoe) {
		this.isSupportPoe = isSupportPoe;
	}

	public final boolean isSupportLldp() {
		return isSupportLldp;
	}

	public final void setSupportLldp(boolean isSupportLldp) {
		this.isSupportLldp = isSupportLldp;
	}

	public final boolean isSupportRStp() {
        return isSupportRStp;
    }

    public final void setSupportRStp(boolean isSupportRStp) {
        this.isSupportRStp = isSupportRStp;
    }

    public final boolean isSupportMStp() {
        return isSupportMStp;
    }

    public final void setSupportMStp(boolean isSupportMStp) {
        this.isSupportMStp = isSupportMStp;
    }

    public final int getDisconnectTicks() {
		return disconnectTicks;
	}

	public final void setDisconnectTicks(int disconnectTicks) {
		this.disconnectTicks = disconnectTicks;
	}

    public final int getManagementFailTicks() {
		return managementFailTicks;
	}

	public final void setManagementFailTicks(int managementFailTicks) {
		this.managementFailTicks = managementFailTicks;
	}
	
	public final int getStpPriority() {
		return stpPriority;
	}

	public final void setStpPriority(int stpPriority) {
		this.stpPriority = stpPriority;
	}

	public final String getStpDesignatedRoot() {
        return stpDesignatedRoot;
    }

    public final void setStpDesignatedRoot(String stpDesignatedRoot) {
        this.stpDesignatedRoot = stpDesignatedRoot;
    }

    public final int getStpRootCost() {
		return stpRootCost;
	}

	public final void setStpRootCost(int stpRootCost) {
		this.stpRootCost = stpRootCost;
	}

	public final int getStpRootPort() {
		return stpRootPort;
	}

	public final void setStpRootPort(int stpRootPort) {
		this.stpRootPort = stpRootPort;
	}

	public final boolean isStpValid() {
		return stpValid;
	}

	public final void setStpValid(boolean stpValid) {
		this.stpValid = stpValid;
	}

	public final String getDeviceType() {
        return deviceType;
    }

    public final void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public final boolean isVirtual() {
        return isVirtual;
    }

    public final void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }

	public final boolean isOctet64() {
        return isOctet64;
    }

    public final void setOctet64(boolean isOctet64) {
        this.isOctet64 = isOctet64;
    }

	public boolean isSupportEgcoTrap() {
		return isSupportEgcoTrap;
	}

	public void setSupportEgcoTrap(boolean isSupportEgcoTrap) {
		this.isSupportEgcoTrap = isSupportEgcoTrap;
	}

	public final String getDbStatisticsTableName() {
		return dbStatisticsTableName;
	}

	public boolean isPoeManual() {
		return isPoeManual;
	}

	public void setPoeManual(boolean isPoeManual) {
		this.isPoeManual = isPoeManual;
	}

	public String getPoeStartTime() {
		return poeStartTime;
	}

	public void setPoeStartTime(String poeStartTime) {
		this.poeStartTime = poeStartTime;
	}

	public String getPoeStartStatus() {
		return poeStartStatus;
	}

	public void setPoeStartStatus(String poeStartStatus) {
		this.poeStartStatus = poeStartStatus;
	}

	public String getPoeEndTime() {
		return poeEndTime;
	}

	public void setPoeEndTime(String poeEndTime) {
		this.poeEndTime = poeEndTime;
	}

	public String getPoeEndStatus() {
		return poeEndStatus;
	}

	public void setPoeEndStatus(String poeEndStatus) {
		this.poeEndStatus = poeEndStatus;
	}

	public boolean isPoePower() {
		return isPoePower;
	}

	public void setPoePower(boolean isPoePower) {
		this.isPoePower = isPoePower;
	}

	public boolean isSupportTrap() {
		return isSupportTrap;
	}

	public void setSupportTrap(boolean isSupportTrap) {
		this.isSupportTrap = isSupportTrap;
	}

	public boolean isSupportEdgeCoreResource() {
		return isSupportEdgeCoreResource;
	}

	public void setSupportEdgeCoreResource(boolean isSupportEdgeCoreResource) {
		this.isSupportEdgeCoreResource = isSupportEdgeCoreResource;
	}

	public boolean isMailFilter() {
		return isMailFilter;
	}

	public void setMailFilter(boolean isMailFilter) {
		this.isMailFilter = isMailFilter;
	}

	public String getProfileStartTime() {
		return profileStartTime;
	}

	public void setProfileStartTime(String profileStartTime) {
		this.profileStartTime = profileStartTime;
	}

	public String getProfileEndTime() {
		return profileEndTime;
	}

	public void setProfileEndTime(String profileEndTime) {
		this.profileEndTime = profileEndTime;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	

}
