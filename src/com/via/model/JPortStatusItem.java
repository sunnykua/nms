package com.via.model;

public class JPortStatusItem {

	private int index;
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

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
