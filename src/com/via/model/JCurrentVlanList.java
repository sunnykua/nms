package com.via.model;


import java.util.ArrayList;
import java.util.List;

public class JCurrentVlanList {
	
	public class JCurrentVlanListItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String vlanID;
		private String egressPorts;
		private String forbiddenPorts;
		private String untaggedPorts;
		private String vlanStatus;
		private String[] portState;
		private String[] PortVLAN;
		
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
		public String getVlanID() {
			return vlanID;
		}
		public void setVlanID(String vlanID) {
			this.vlanID = vlanID;
		}
		public String getEgressPorts() {
			return egressPorts;
		}
		public void setEgressPorts(String egressPorts) {
			this.egressPorts = egressPorts;
		}
		public String getForbiddenPorts() {
			return forbiddenPorts;
		}
		public void setForbiddenPorts(String forbiddenPorts) {
			this.forbiddenPorts = forbiddenPorts;
		}
		public String getUntaggedPorts() {
			return untaggedPorts;
		}
		public void setUntaggedPorts(String untaggedPorts) {
			this.untaggedPorts = untaggedPorts;
		}

		public String getVlanStatus() {
			return vlanStatus;
		}
		public void setVlanStatus(String vlanStatus) {
			this.vlanStatus = vlanStatus;
		}
		public String[] getPortState() {
			return portState;
		}
		public void setPortState(String[] portState) {
			this.portState = portState;
		}
		public String[] getPortVLAN() {
			return PortVLAN;
		}
		public void setPortVLAN(String[] portVLAN) {
			PortVLAN = portVLAN;
		}
	}
	
	private List<JCurrentVlanListItem> currentVlanItems;
	
	public JCurrentVlanList() {
		this.currentVlanItems = new ArrayList<JCurrentVlanListItem>();
	}
	
	public void addCurrentVlan(String egressPorts, String vlanID, String untaggedPorts, String vlanStatus, String[] portState, String[] portVLAN){
		JCurrentVlanListItem item = new JCurrentVlanListItem();
		/*item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);*/
		item.setEgressPorts(egressPorts);
		item.setVlanID(vlanID);
		item.setUntaggedPorts(untaggedPorts);
		item.setVlanStatus(vlanStatus);
		item.setPortState(portState);
		item.setPortVLAN(portVLAN);
		this.currentVlanItems.add(item);
	}
	
	public final List<JCurrentVlanListItem> getCurrentVlanItems() {
		return currentVlanItems;
	}
}
