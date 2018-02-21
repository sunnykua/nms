package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class JTrapStatus {

    Logger logger = Logger.getLogger(JTrapStatus.class);


	public List<JTrapStatusItem> readDevice(JDevice device) {

		String[] trapStatusOids = {
				JOid.vnt5103TrapDestVersion,
				JOid.vnt5103TrapDestCommunity,
				JOid.vnt5103TrapDestUdpPort,
				JOid.vnt5103TrapDestStatus
				
		};

		Map<String, List<String>> rawData3;
		int b = 0;
		boolean isFail=false;		// will temporary down
		String inActive="";
		final int Version = b++, Community = b++, UdpPort = b++, Status = b++;
		// Extract received data and store into a List
		List<JTrapStatusItem> traplist = new ArrayList<JTrapStatusItem>();

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			rawData3 = snmp.getTable(trapStatusOids);
			snmp.end();
	        //logger.debug("Device IP = " + device.getPublicIp() +" TRAP IP Status Global Get SNMP Table");   
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" TRAP IP Status Global Read Device Failed");   
			return traplist;
		}
		
		JMib.printRawMap(rawData3, false);
		
		if (rawData3 != null && rawData3.size() != 0) {
			for (Map.Entry<String, List<String>> entry : rawData3.entrySet()) {
				JTrapStatusItem item = new JTrapStatusItem();
	            String ifKey = entry.getKey();
//	    		System.out.println("Static VLAN ID= " + ifKey); 
				item.setTrapIpAddress(ifKey);
	    		if (rawData3.containsKey(ifKey)) {
    				item.setTrapDestVersion(rawData3.get(ifKey).get(Version)); // 
    				item.setTrapDestCommunity(rawData3.get(ifKey).get(Community)); // 
    				item.setTrapDestUdpPort(rawData3.get(ifKey).get(UdpPort)); // 
    				item.setTrapDestStatus(rawData3.get(ifKey).get(Status)); // 
	    		} else {
	    			inActive +=ifKey + " " ;
	    	        isFail=true;
	    		} 
	    		traplist.add(item);
			}
		} else {
            logger.debug(" TRAP IP Status  info failed.");     // TODO check if result has noSuch string
		}		

		// Store device data as a table

    	/*for (JTrapStatusItem item : table.getItems()) {
    		if (item.getDot1qVlanStaticName()!= null) {
    			System.out.println(String.format("VlanNum=%d IpAddr=%s VlanID=%d VlanStaticName=%s VlanStaticStatus=%s ",
    				table.getVlanNum(), table.getIpAddr(), item.getIndex(), item.getDot1qVlanStaticName(), item.getDot1qVlanStaticStatus() ));
    		}	
    	}*/
		/*if (!isFail && vlanNum != 0)
			logger.debug("Device IP = " + device.getPublicIp() +" VLAN Status Global Read Device Success ");
		else
			logger.warn("Device IP = " + device.getPublicIp() +" VLAN Status Global Port = "+ (inActive.isEmpty()?"All ":inActive) + "Read Failed ");*/
		
		return traplist;
	}
	

	


	public boolean writeDevice(JDevice device, String trapAddr, String trapVersion, String trapCommunity, String trapPort) {
		if (device == null || trapAddr == null || trapVersion == null || trapCommunity == null || trapPort == null) return false;
		//System.out.println("Writing OID: " + columnOid + " to " + device.getPublicIp());

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			snmp.setNode(JOid.vnt5103TrapDestStatus+"."+trapAddr, 1);	//Create a trap entry
			snmp.setNode(JOid.vnt5103TrapDestCommunity+"."+trapAddr, trapCommunity);	//set trapCommunity
			snmp.setNode(JOid.vnt5103TrapDestUdpPort+"."+trapAddr, Integer.valueOf(trapPort));	//set UdpPort
			snmp.setNode(JOid.vnt5103TrapDestVersion+"."+trapAddr, Integer.valueOf(trapVersion));	//set trapVersion
			snmp.end();
		}
		catch (IOException e) {
			System.out.println("Write SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" TRAP IP Status Write SNMP Table Failed");   
			return false;
		}

		return true;
	}
	
	public boolean deleteDevice(JDevice device, String[] trapAddr) {
		if (device == null ) return false;
		//System.out.println("Writing OID: " + columnOid + " to " + device.getPublicIp());
		
		for(int i=0;i<trapAddr.length;i++)
		{
			try {
	            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
	            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
				snmp.start();
				snmp.setNode(JOid.vnt5103TrapDestStatus+"."+trapAddr[i], 2);	//Delete a trap entry
				snmp.end();
			}
			catch (IOException e) {
				System.out.println("Write SNMP Failed.\n" + e.getMessage());
		        logger.warn("Device IP = " + device.getPublicIp() +" TRAP IP Status Delete SNMP Table Failed");   
				return false;
			}
		}

		return true;
	}


}
