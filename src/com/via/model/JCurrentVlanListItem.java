package com.via.model;

public class JCurrentVlanListItem {

	private String vlanID;
	private String egressPorts;
	private String forbiddenPorts;
	private String untaggedPorts;
	private String vlanStatus;
	private String[] portState;
	private String[] PortVLAN;
	
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
