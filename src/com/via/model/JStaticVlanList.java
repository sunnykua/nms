package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JStaticVlanList {
	
	public class JStaticVlanListItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String vlanID;
		private String egressPorts;
		private String forbiddenPorts;
		private String untaggedPorts;
		private String[] portState;
		
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
		public String[] getPortState() {
			return portState;
		}
		public void setPortState(String[] portState) {
			this.portState = portState;
		}
	}
	
	private List<JStaticVlanListItem> staticVlanItems;
	
	public JStaticVlanList() {
		this.staticVlanItems = new ArrayList<JStaticVlanListItem>();
	}
	
	public void addStaticVlan(String vlanID, String egressPorts, String forbiddenPorts, String untaggedPorts, String[] portState){
		JStaticVlanListItem item = new JStaticVlanListItem();
		/*item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);*/
		item.setVlanID(vlanID);
		item.setEgressPorts(egressPorts);
		item.setForbiddenPorts(forbiddenPorts);
		item.setUntaggedPorts(untaggedPorts);
		item.setPortState(portState);
		this.staticVlanItems.add(item);
	}
	
	public final List<JStaticVlanListItem> getStaticVlanItems() {
		return staticVlanItems;
	}
}
