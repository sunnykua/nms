package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class JFirewallview {
	
	
	public JFirewallview (JDevice device) {
	    Logger logger = Logger.getLogger(JFirewallview.class);
		
		String[] statisticOids = {
				JOid.ifInOctets,
				JOid.ifOutOctets,
		};

		Map<String, List<String>> statisticResult = null;
		int a = 0;
		boolean isFail=false;		// will temporary down
		String inActive="";

		final int RXOCTETS = a++, TXOCTETS = a++;

		// Get data from remote device
		try {
			JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			statisticResult = snmp.getTable(statisticOids);
//			vlanResult = snmp.getTable(vlanInfOids);
			
			String[] sysresult = snmp.getNodes(new String[]{JOid.sysDescr, JOid.sysObjectID, JOid.sysUpTime, JOid.sysContact, JOid.sysName, JOid.sysLocation, JOid.sysServices});
			if (sysresult != null && sysresult.length == 7) {
				setSysDescr( sysresult[0] );
				setSysObjectId( sysresult[1]);
				setSysUpTime( sysresult[2] );
				setSysContact( sysresult[3] );
				setSysName( sysresult[4] );
				setSysLocation( sysresult[5] );
				setSysServices( sysresult[6] );
			}
			else {
				setSysDescr( "" );
				setSysObjectId( "" );
				setSysUpTime( "" );
				setSysContact( "" );
				setSysName( "" );
				setSysLocation( "" );
				setSysServices( "" );
			}
			
			snmp.end();
	        logger.debug(" ZyXEL Firewall Get SNMP Table");   
		} catch (IOException e) {
			System.out.println("ZyXEL Firewall Read SNMP Failed.\n" + e.getMessage());
	        logger.warn(" ZyXEL Firewall Read Device Failed");   
		}

		JMib.printRawMap(statisticResult, false);

		
		
		// Extract received data and store into a List
		List<JFirewallviewItem> items = new ArrayList<JFirewallviewItem>();
		for (int i = 7; i <= 11; i++) {							// NOTE: include RJ45 and Fiber interfaces
			JFirewallviewItem item = new JFirewallviewItem();
			String ifKey = String.valueOf(i);
			item.setIndex(i);

    		if (statisticResult.containsKey(ifKey)) {
    			item.setRxOctets(statisticResult.get(ifKey).get(RXOCTETS)); // be careful of the oid order
    			item.setTxOctets(statisticResult.get(ifKey).get(TXOCTETS));
    		} else {
    			inActive +=ifKey + " " ;
    	        isFail=true;
    		}
			items.add(item);

		}

			if (!isFail)
				logger.debug(" ZyXEL Firewall Read Device Success ");
			else
				logger.warn(" ZyXEL Firewall Port = "+ (inActive.isEmpty()?"All ":inActive) + "Read Failed ");
			
	}

	private String sysDescr;
	private String sysObjectId;
	private String sysUpTime;
	private String sysContact;
	private String sysName;
	private String sysLocation;
	private String sysServices;

	public String getSysDescr() {
		return sysDescr;
	}
	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}
	public String getSysObjectId() {
		return sysObjectId;
	}
	public void setSysObjectId(String sysObjectId) {
		this.sysObjectId = sysObjectId;
	}
	public String getSysUpTime() {
		return sysUpTime;
	}
	public void setSysUpTime(String sysUpTime) {
		this.sysUpTime = sysUpTime;
	}
	public String getSysContact() {
		return sysContact;
	}
	public void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getSysLocation() {
		return sysLocation;
	}
	public void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}
	public String getSysServices() {
		return sysServices;
	}
	public void setSysServices(String sysServices) {
		this.sysServices = sysServices;
	}

}