package com.via.model;

public class JInterfaceTerminalReport {
	private String rxOct;
	private String txOct;
	private String publicIp;
	private int portId;
	private String portRemoteDev;
	private String portRemoteIp;
	private int rxRanking;
	private int txRanking;
	private String aliasName;
	
	public String getRxOct() {
		return rxOct;
	}
	public void setRxOct(String rxOct) {
		this.rxOct = rxOct;
	}
	public String getTxOct() {
		return txOct;
	}
	public void setTxOct(String txOct) {
		this.txOct = txOct;
	}
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public int getPortId() {
		return portId;
	}
	public void setPortId(int portId) {
		this.portId = portId;
	}
	public String getPortRemoteDev() {
		return portRemoteDev;
	}
	public void setPortRemoteDev(String portRemoteDev) {
		this.portRemoteDev = portRemoteDev;
	}
	public String getPortRemoteIp() {
		return portRemoteIp;
	}
	public void setPortRemoteIp(String portRemoteIp) {
		this.portRemoteIp = portRemoteIp;
	}
	public int getRxRanking() {
		return rxRanking;
	}
	public void setRxRanking(int rxRanking) {
		this.rxRanking = rxRanking;
	}
	public int getTxRanking() {
		return txRanking;
	}
	public void setTxRanking(int txRanking) {
		this.txRanking = txRanking;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
}
