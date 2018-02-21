package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JVlanStatus {
	
	public class JVlanStatusItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String dot1qPvid;
		private String dot1qVlanStaticName;
		private String dot1qPortAcceptableFrameType;
		private String dot1qPortIngressFiltering;
		private String dot1qPortGvrpStatus;
		private String dot1qVlanStaticStatus;
		private String vlanNum;

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
		public String getDot1qPvid() {
			return dot1qPvid;
		}
		public void setDot1qPvid(String dot1qPvid) {
			this.dot1qPvid = dot1qPvid;
		}
		public String getDot1qVlanStaticName() {
			return dot1qVlanStaticName;
		}
		public void setDot1qVlanStaticName(String dot1qVlanStaticName) {
			this.dot1qVlanStaticName = dot1qVlanStaticName;
		}
		public String getDot1qPortAcceptableFrameType() {
			return dot1qPortAcceptableFrameType;
		}
		public void setDot1qPortAcceptableFrameType(String dot1qPortAcceptableFrameType) {
			this.dot1qPortAcceptableFrameType = dot1qPortAcceptableFrameType;
		}
		public String getDot1qPortIngressFiltering() {
			return dot1qPortIngressFiltering;
		}
		public void setDot1qPortIngressFiltering(String dot1qPortIngressFiltering) {
			this.dot1qPortIngressFiltering = dot1qPortIngressFiltering;
		}
		public String getDot1qPortGvrpStatus() {
			return dot1qPortGvrpStatus;
		}
		public void setDot1qPortGvrpStatus(String dot1qPortGvrpStatus) {
			this.dot1qPortGvrpStatus = dot1qPortGvrpStatus;
		}
		public String getDot1qVlanStaticStatus() {
			return dot1qVlanStaticStatus;
		}
		public void setDot1qVlanStaticStatus(String dot1qVlanStaticStatus) {
			this.dot1qVlanStaticStatus = dot1qVlanStaticStatus;
		}
		public String getVlanNum() {
			return vlanNum;
		}
		public void setVlanNum(String vlanNum) {
			this.vlanNum = vlanNum;
		}	
	}
	
	private List<JVlanStatusItem> vlanStatusItem;
	
	public JVlanStatus() {
		this.vlanStatusItem = new ArrayList<JVlanStatusItem>();
	}
	
	public void addVlanStatus(int portId, int ifIndex, int stackId, String dot1qPvid, String dot1qPortAcceptableFrameType, String dot1qPortIngressFiltering, String dot1qPortGvrpStatus){
		JVlanStatusItem item = new JVlanStatusItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setDot1qPvid(dot1qPvid);
		item.setDot1qPortAcceptableFrameType(dot1qPortAcceptableFrameType);
		item.setDot1qPortIngressFiltering(dot1qPortIngressFiltering);
		item.setDot1qPortGvrpStatus(dot1qPortGvrpStatus);
		this.vlanStatusItem.add(item);
	}
	
	public void addGlobalVlanStatus(String vlanNum, String dot1qVlanStaticName, String dot1qVlanStaticStatus){
		JVlanStatusItem item = new JVlanStatusItem();
		item.setVlanNum(vlanNum);
		item.setDot1qVlanStaticName(dot1qVlanStaticName);
		item.setDot1qVlanStaticStatus(dot1qVlanStaticStatus);
		this.vlanStatusItem.add(item);
	}
	
	public final List<JVlanStatusItem> getVlanStatusItems() {
		return vlanStatusItem;
	}
	
	public boolean writeDevice(JDevice device, String columnOid, String[] dataArray) {
	    Logger logger = Logger.getLogger(JVlanStatus.class);
		if (device == null || dataArray == null) return false;
		//System.out.println("Writing OID: " + columnOid + " to " + device.getPublicIp());

		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			for (int i = 0; i < dataArray.length; i++) {
				String[] dataSplit = dataArray[i].split("\\.");
				String ifKey = "." + dataSplit[0];
				//System.out.println(" ifKey= "+ifKey+" vlan data= "+dataSplit[1]);
				snmp.setNode(columnOid + ifKey, Integer.valueOf(dataSplit[1]));
			}
			snmp.end();
		}
		catch (IOException e) {
			//System.out.println("Write SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" VLAN Status Write SNMP Table Failed");   
			return false;
		}

		return true;
	}
}
