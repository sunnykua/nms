package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JPoeStatus {
	
	public class JPoeStatusItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String poePortAdminStatus;
		private String poePortDetectionStatus;
		private String poePortPowerPriority;
		private String poeMainMaxAvailablePower;
		private String poeMainSysOperStatus;
		private String poeMainPowerConsumption;
		
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
		public String getPoePortAdminStatus() {
			return poePortAdminStatus;
		}
		public void setPoePortAdminStatus(String poePortAdminStatus) {
			this.poePortAdminStatus = poePortAdminStatus;
		}
		public String getPoePortDetectionStatus() {
			return poePortDetectionStatus;
		}
		public void setPoePortDetectionStatus(String poePortDetectionStatus) {
			this.poePortDetectionStatus = poePortDetectionStatus;
		}
		public String getPoePortPowerPriority() {
			return poePortPowerPriority;
		}
		public void setPoePortPowerPriority(String poePortPowerPriority) {
			this.poePortPowerPriority = poePortPowerPriority;
		}
		public String getPoeMainMaxAvailablePower() {
			return poeMainMaxAvailablePower;
		}
		public void setPoeMainMaxAvailablePower(String poeMainMaxAvailablePower) {
			this.poeMainMaxAvailablePower = poeMainMaxAvailablePower;
		}
		public String getPoeMainSysOperStatus() {
			return poeMainSysOperStatus;
		}
		public void setPoeMainSysOperStatus(String poeMainSysOperStatus) {
			this.poeMainSysOperStatus = poeMainSysOperStatus;
		}
		public String getPoeMainPowerConsumption() {
			return poeMainPowerConsumption;
		}
		public void setPoeMainPowerConsumption(String poeMainPowerConsumption) {
			this.poeMainPowerConsumption = poeMainPowerConsumption;
		}
	}
	
	private List<JPoeStatusItem> poeStatusItems;
	
	public JPoeStatus() {
		this.poeStatusItems = new ArrayList<JPoeStatusItem>();
	}
	
	public void addPoeStatus(int portId, int ifIndex, int stackId, String poePortAdminStatus, String poePortDetectionStatus, String poePortPowerPriority){
		JPoeStatusItem item = new JPoeStatusItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setPoePortAdminStatus(poePortAdminStatus);
		item.setPoePortDetectionStatus(poePortDetectionStatus);
		item.setPoePortPowerPriority(poePortPowerPriority);
		this.poeStatusItems.add(item);
	}
	
	public void addGlobalPoeStatus(String poeMainMaxAvailablePower, String poeMainSysOperStatus, String poeMainPowerConsumption){
		JPoeStatusItem item = new JPoeStatusItem();
		item.setPoeMainMaxAvailablePower(poeMainMaxAvailablePower);
		item.setPoeMainSysOperStatus(poeMainSysOperStatus);
		item.setPoeMainPowerConsumption(poeMainPowerConsumption);
		this.poeStatusItems.add(item);
	}
	
	public final List<JPoeStatusItem> getPoeStatusItems() {
		return poeStatusItems;
	}

	public boolean writeDevice(JDevice device, String columnOid, String[] dataArray) {
	    Logger logger = Logger.getLogger(JPoeStatus.class);
		if (device == null || dataArray == null) return false;
		//System.out.println("Writing OID: " + columnOid + " to " + device.getPublicIp());

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			//System.out.println("Poe Status Execute Success.");
			for (int i = 0; i < dataArray.length; i++) {
				String[] dataSplit = dataArray[i].split("\\.");
				String ifKey = ".1." + dataSplit[0];
				//System.out.println(" ifKey= "+ifKey+" poe data= "+dataSplit[1]);
				snmp.setNode(columnOid + ifKey, Integer.valueOf(dataSplit[1]));
			}
			snmp.end();
	        //logger.debug("Device IP = " + device.getPublicIp() +" POE Status Write SNMP Table");
		}
		catch (IOException e) {
			//System.out.println("Write SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" POE Status Write SNMP Table Failed");   
			return false;
		}

		return true;
	}
}
