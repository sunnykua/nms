package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class JPortStatus {
	public class JPortStatusItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String type;
		private String admin;
		private String oper;
		private String autoNegoSupport;
		private String autoNegoStatus;
		private String speed;
		private String duplex;
		private String flowCtrlAbility;
		private String flowCtrlStatus;
		private String interfaceStatus;

		public int getPortId() {
			return portId;
		}
		public void setPortId(int portId) {
			this.portId = portId;
		}
		public int getIfIndex() {
			return ifIndex;
		}
		public void setIfIndex(int ifIndex) {
			this.ifIndex = ifIndex;
		}
		public int getStackId() {
			return stackId;
		}
		public void setStackId(int stackId) {
			this.stackId = stackId;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAdmin() {
			return admin;
		}
		public void setAdmin(String admin) {
			this.admin = admin;
		}
		public String getOper() {
			return oper;
		}
		public void setOper(String oper) {
			this.oper = oper;
		}
		public String getAutoNegoSupport() {
			return autoNegoSupport;
		}
		public void setAutoNegoSupport(String autoNegoSupport) {
			this.autoNegoSupport = autoNegoSupport;
		}
		public String getAutoNegoStatus() {
			return autoNegoStatus;
		}
		public void setAutoNegoStatus(String autoNegoStatus) {
			this.autoNegoStatus = autoNegoStatus;
		}
		public String getSpeed() {
			return speed;
		}
		public void setSpeed(String speed) {
			this.speed = speed;
		}
		public String getDuplex() {
			return duplex;
		}
		public void setDuplex(String duplex) {
			this.duplex = duplex;
		}
		public String getFlowCtrlAbility() {
			return flowCtrlAbility;
		}
		public void setFlowCtrlAbility(String flowCtrlAbility) {
			this.flowCtrlAbility = flowCtrlAbility;
		}
		public String getFlowCtrlStatus() {
			return flowCtrlStatus;
		}
		public void setFlowCtrlStatus(String flowCtrlStatus) {
			this.flowCtrlStatus = flowCtrlStatus;
		}
		public final String getInterfaceStatus() {
			return interfaceStatus;
		}
		public final void setInterfaceStatus(String interfaceStatus) {
			this.interfaceStatus = interfaceStatus;
		}
	}
	
	private List<JPortStatusItem> portStatusItems;
	
	public JPortStatus() {
		this.portStatusItems = new ArrayList<JPortStatusItem>();
	}
	
	public void addPortStatus(int portId, int ifIndex, int stackId, String speed, String admin, String oper, String duplex, String flowCtrlAbility, String flowCtrlStatus){
		JPortStatusItem item = new JPortStatusItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setSpeed(speed);
		item.setAdmin(admin);
		item.setOper(oper);
		item.setDuplex(duplex);
		item.setFlowCtrlAbility(flowCtrlAbility);
		item.setFlowCtrlStatus(flowCtrlStatus);
		this.portStatusItems.add(item);
	}
	
	public void addNego(int ifIndex, String autoNegoSupport, String autoNegoStatus){
		
		for(int i=0; i<portStatusItems.size(); i++){
		JPortStatusItem item = portStatusItems.get(i);
		if(item.getIfIndex() == ifIndex){
			item.setAutoNegoSupport(autoNegoSupport);
			item.setAutoNegoStatus(autoNegoStatus);
		}
		}
	}
	
	public final List<JPortStatusItem> getPortStatusItems() {
		return portStatusItems;
	}


	private int tableCount;
	private List<JPortStatusTable> tables;

	public JPortStatusTable readDevice(JDevice device) {
	    Logger logger = Logger.getLogger(JPortStatus.class);

		String[] statusOids = {
				JOid.ifHighSpeed,
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
		String[] vlanInfOids = {
				JOid.dot1qPvid
		};
		Map<String, List<String>> statusResult, negoResult,vlanResult;
		int portNum,Rj45Num;
		int a = 0, b = 0;
		boolean isFail=false;		// will temporary down
		String inActive="";

		final int SPEED = a++, ADMIN_STAT = a++, OPER_STAT = a++, DUPLEX = a++, RC_ABILITY = a++, RC_STAT = a++;
		final int MAU_TYPE = b++, AUTO_NEGO_SUPPORT = b++, AUTO_NEGO_STAT = b++;
		JPortStatusTable table = new JPortStatusTable();

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			statusResult = snmp.getTable(statusOids);
			negoResult = snmp.getTable(negoOids);
			vlanResult = snmp.getTable(vlanInfOids);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.debug("Device IP = " + device.getPublicIp() +" Port Status Read Device Failed");   
			return table;
		}

		portNum = device.getInfNum() == 0 ? vlanResult.size() : device.getInfNum();			// choose the smaller as actual port number
		Rj45Num = device.getRj45Num() == 0 ? vlanResult.size() : device.getRj45Num();			// choose the smaller as actual port number
		JMib.printRawMap(statusResult, false);
		JMib.printRawMap(negoResult, false);

		List<JPortStatusItem> items = new ArrayList<JPortStatusItem>();
		for (int i = 1; i <= portNum; i++) {						// NOTE: include RJ45 and Fiber interfaces
//			JPortStatusItem item = new JPortStatusItem();
    		JInterface inf = device.getInterfaces().get(i-1);

			String ifKey = String.valueOf(i);
			String mauKey = ifKey + ".1";							// is Mau always 1 ?
//			item.setIndex(i);

			if (statusResult.containsKey(ifKey)) {
				String speed = statusResult.get(ifKey).get(SPEED);
				if (speed.endsWith("000"))
					speed = speed.substring(0, speed.length() - 3) + "G";
				else
					speed = speed + "M";
//				item.setSpeed(speed);
//				item.setAdmin(statusResult.get(ifKey).get(ADMIN_STAT).equals("1") ? "Up" : "Down");
//				item.setOper(statusResult.get(ifKey).get(OPER_STAT).equals("1") ? "Up" : "Down");
//				String duplex = statusResult.get(ifKey).get(DUPLEX);
//				item.setDuplex(duplex.equals("3") ? "Full" : duplex.equals("2") ? "Half" : "unknown");
//				item.setFlowCtrlAbility(statusResult.get(ifKey).get(RC_ABILITY).equals("1") ? "Enable" : "Disable");
//				item.setFlowCtrlStatus(statusResult.get(ifKey).get(RC_STAT).equals("2") ? "On" : "Off");
				
				
			} else {
//				logger.warn("Device IP = " + device.getPublicIp() +" Port Status Port = " + ifKey +"  Read Device Failed");
    			inActive +=ifKey + " " ;
    	        isFail=true;
	        
			}
			if (i <= Rj45Num) {
				if (negoResult.containsKey(mauKey)) {
//					item.setAutoNegoSupport(negoResult.get(mauKey).get(AUTO_NEGO_SUPPORT).equals("1") ? "Yes" :
//							negoResult.get(mauKey).get(AUTO_NEGO_SUPPORT).equals("2") ? "No" : "--");
//					item.setAutoNegoStatus(negoResult.get(mauKey).get(AUTO_NEGO_STAT).equals("1") ? "Enabled" : 
//							negoResult.get(mauKey).get(AUTO_NEGO_STAT).equals("2") ? "Disabled" : "--");
//					item.setInterfaceStatus("RJ45");
				} else {
//					item.setAutoNegoSupport ("--");
//					item.setAutoNegoStatus("--");
//					item.setInterfaceStatus("--");
				}
			}
			else {
//				item.setAutoNegoSupport("--");
//				item.setAutoNegoStatus("--");
//				item.setInterfaceStatus("Fiber");
			}

//			items.add(item);
//			System.out.println(String.format("index=%d speed=%s admin=%s oper=%s nego=%s duplex=%s fcSta=%s",
//					item.getIndex(), item.getSpeed(), item.getAdmin(), item.getOper(), item.getAutoNegoSupport(), item.getDuplex(), item.getFlowCtrlStatus()));
		}
		
		//check JACK_LIST in JdbModule
        int count=1;
        for (int i = 0; i <portNum; i++) {
			JPortStatusItem item = new JPortStatusItem();
        	JInterface inf = device.getInterfaces().get(i);
        	
        	if(inf.getJackType().equals("rj45") || inf.getJackType().equals("fiber")){
        		
        		//item.setIndex(count++);
        		
        		items.add(item);
        	}
        }

		// Store device data as a table
		//table.setItems(items);
		table.setItemCount(items.size());		// should be equal to 'portNum'
		table.setSysDescr(device.getSysDescr());
		table.setIpAddr(device.getPublicIp());
		table.setLocalIp(device.getLocalIp());
//		table.setPortNum(device.getEthPortNum());
		table.setPortNum(portNum);

		if (isFail)
			logger.debug("Device IP = " + device.getPublicIp() +" Port Status Port = "+ (inActive.isEmpty()?"All ":inActive) + "Read Failed ");
		
		return table;
	}

	public List<JPortStatusTable> readDeviceAll(List<JDevice> deviceList) {
		List<JPortStatusTable> tables = new ArrayList<JPortStatusTable>();
		if (deviceList == null) return tables;

		for (JDevice device : deviceList) {
			JPortStatusTable table = readDevice(device);
			tables.add(table);
		}
		return tables;
	}

	public boolean writeDevice(JDevice device, String columnOid, String[] dataArray) {
	    Logger logger = Logger.getLogger(JPortStatus.class);
		if (device == null || dataArray == null) return false;
        logger.debug("Device IP = " + device.getPublicIp() +" Port Status : " + "Writing OID: " + columnOid + " to " + device.getPublicIp());   

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			for (int i = 0; i < dataArray.length; i++) {
				String[] dataSplit = dataArray[i].split("\\.");
				String ifKey = "." + dataSplit[0];
				//System.out.println(" ifKey= "+ifKey+" admin data= "+dataSplit[1]);
				snmp.setNode(columnOid + ifKey, Integer.valueOf(dataSplit[1]));
			}
			snmp.end();
		}
		catch (IOException e) {
			System.out.println("Write SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" Port Status Write SNMP Table Failed");   
			return false;
		}

		return true;
	}
	public boolean writeDevice_db(JDevice device, String columnOid, String[] dataArray) {
	    Logger logger = Logger.getLogger(JPortStatus.class);
		if (device == null || dataArray == null) return false;
        logger.debug("Device IP = " + device.getPublicIp() +" Port Status : " + "Writing double index OID: " + columnOid + " to " + device.getPublicIp());   

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			for (int i = 0; i < dataArray.length; i++) {
				String[] dataSplit = dataArray[i].split("\\.");
				String ifKey = "." + dataSplit[0] + ".1";
				//System.out.println(" ifKey= "+ifKey+" nego data= "+dataSplit[1]);
				snmp.setNode(columnOid + ifKey, Integer.valueOf(dataSplit[1]));
			}
			snmp.end();
		}
		catch (IOException e) {
			System.out.println("Write SNMP Failed.\n" + e.getMessage());
	        logger.debug("Device IP = " + device.getPublicIp() +" Port Status Write SNMP Table for double index Failed");   
			return false;
		}

		return true;
	}

	public int getTableCount() {
		return tableCount;
	}
	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}
	public List<JPortStatusTable> getTables() {
		return tables;
	}
	public void setTables(List<JPortStatusTable> tables) {
		this.tables = tables;
	}
}
