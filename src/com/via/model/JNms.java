package com.via.model;

import java.util.Date;

public class JNms {
	private String publicIp;
	private String publicIpFull;
	private String aliasName;
	private int totalDeviceNum;
	private int onlineDeviceNum;
	private int offlineDeviceNum;
	private Date lastSeen;
	private boolean isAlive;
	
	private boolean isEnable;
	private String rmiTimeout;
	private String rmiRegisterPort;
	private String rmiDataPort;
	private String remoteAccount;
	private String remotePwd;
	private String remoteMember;
	
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public String getPublicIpFull() {
		return publicIpFull;
	}
	public void setPublicIpFull(String publicIpFull) {
		this.publicIpFull = publicIpFull;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public int getTotalDeviceNum() {
		return totalDeviceNum;
	}
	public void setTotalDeviceNum(int totalDeviceNum) {
		this.totalDeviceNum = totalDeviceNum;
	}
	public int getOnlineDeviceNum() {
		return onlineDeviceNum;
	}
	public void setOnlineDeviceNum(int onlineDeviceNum) {
		this.onlineDeviceNum = onlineDeviceNum;
	}
	public int getOfflineDeviceNum() {
		return offlineDeviceNum;
	}
	public void setOfflineDeviceNum(int offlineDeviceNum) {
		this.offlineDeviceNum = offlineDeviceNum;
	}
	public Date getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	public String getRmiTimeout() {
		return rmiTimeout;
	}
	public void setRmiTimeout(String rmiTimeout) {
		this.rmiTimeout = rmiTimeout;
	}
	public String getRmiRegisterPort() {
		return rmiRegisterPort;
	}
	public void setRmiRegisterPort(String rmiRegisterPort) {
		this.rmiRegisterPort = rmiRegisterPort;
	}
	public String getRmiDataPort() {
		return rmiDataPort;
	}
	public void setRmiDataPort(String rmiDataPort) {
		this.rmiDataPort = rmiDataPort;
	}
	public String getRemoteAccount() {
		return remoteAccount;
	}
	public void setRemoteAccount(String remoteAccount) {
		this.remoteAccount = remoteAccount;
	}
	public String getRemotePwd() {
		return remotePwd;
	}
	public void setRemotePwd(String remotePwd) {
		this.remotePwd = remotePwd;
	}
	/**
	 * @return the remoteMember
	 */
	public String getRemoteMember() {
		return remoteMember;
	}
	/**
	 * @param remoteMember the remoteMember to set
	 */
	public void setRemoteMember(String remoteMember) {
		this.remoteMember = remoteMember;
	}

}
