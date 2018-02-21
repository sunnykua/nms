package com.via.model;

import java.util.List;

public class AlarmConfig {
	private boolean projectStartup;
	private List<String> projectStartupUserList;
	private boolean projectShutdown;
	private List<String> projectShutdownUserList;
	private boolean deviceDisconnect;
	private List<String> deviceDisconnectUserList;
	private boolean monitorPortLinkUp;
	private List<String> monitorPortLinkUpUserList;
	private boolean monitorPortLinkDown;
	private List<String> monitorPortLinkDownUserList;
	private boolean criticalPortLinkUp;
	private List<String> criticalPortLinkUpUserList;
	private boolean criticalPortLinkDown;
	private List<String> criticalPortLinkDownUserList;
	private boolean wlanAPJoin;
	private List<String> wlanAPJoinUserList;
	private boolean wlanAPLeave;
	private List<String> wlanAPLeaveUserList;
	private boolean webUpdate;
	private List<String> webUpdateUserList;
	
	public final boolean isProjectStartup() {
		return projectStartup;
	}
	public final void setProjectStartup(boolean projectStartup) {
		this.projectStartup = projectStartup;
	}
	public final List<String> getProjectStartupUserList() {
		return projectStartupUserList;
	}
	public final void setProjectStartupUserList(List<String> projectStartupUserList) {
		this.projectStartupUserList = projectStartupUserList;
	}
	public final boolean isProjectShutdown() {
		return projectShutdown;
	}
	public final void setProjectShutdown(boolean projectShutdown) {
		this.projectShutdown = projectShutdown;
	}
	public final List<String> getProjectShutdownUserList() {
		return projectShutdownUserList;
	}
	public final void setProjectShutdownUserList(
			List<String> projectShutdownUserList) {
		this.projectShutdownUserList = projectShutdownUserList;
	}
	public final boolean isDeviceDisconnect() {
		return deviceDisconnect;
	}
	public final void setDeviceDisconnect(boolean deviceDisconnect) {
		this.deviceDisconnect = deviceDisconnect;
	}
	public final List<String> getDeviceDisconnectUserList() {
		return deviceDisconnectUserList;
	}
	public final void setDeviceDisconnectUserList(
			List<String> deviceDisconnectUserList) {
		this.deviceDisconnectUserList = deviceDisconnectUserList;
	}
	public final boolean isMonitorPortLinkUp() {
		return monitorPortLinkUp;
	}
	public final void setMonitorPortLinkUp(boolean monitorPortLinkUp) {
		this.monitorPortLinkUp = monitorPortLinkUp;
	}
	public final List<String> getMonitorPortLinkUpUserList() {
		return monitorPortLinkUpUserList;
	}
	public final void setMonitorPortLinkUpUserList(
			List<String> monitorPortLinkUpUserList) {
		this.monitorPortLinkUpUserList = monitorPortLinkUpUserList;
	}
	public final boolean isMonitorPortLinkDown() {
		return monitorPortLinkDown;
	}
	public final void setMonitorPortLinkDown(boolean monitorPortLinkDown) {
		this.monitorPortLinkDown = monitorPortLinkDown;
	}
	public final List<String> getMonitorPortLinkDownUserList() {
		return monitorPortLinkDownUserList;
	}
	public final void setMonitorPortLinkDownUserList(
			List<String> monitorPortLinkDownUserList) {
		this.monitorPortLinkDownUserList = monitorPortLinkDownUserList;
	}
	public final boolean isCriticalPortLinkUp() {
		return criticalPortLinkUp;
	}
	public final void setCriticalPortLinkUp(boolean criticalPortLinkUp) {
		this.criticalPortLinkUp = criticalPortLinkUp;
	}
	public final List<String> getCriticalPortLinkUpUserList() {
		return criticalPortLinkUpUserList;
	}
	public final void setCriticalPortLinkUpUserList(
			List<String> criticalPortLinkUpUserList) {
		this.criticalPortLinkUpUserList = criticalPortLinkUpUserList;
	}
	public final boolean isCriticalPortLinkDown() {
		return criticalPortLinkDown;
	}
	public final void setCriticalPortLinkDown(boolean criticalPortLinkDown) {
		this.criticalPortLinkDown = criticalPortLinkDown;
	}
	public final List<String> getCriticalPortLinkDownUserList() {
		return criticalPortLinkDownUserList;
	}
	public final void setCriticalPortLinkDownUserList(
			List<String> criticalPortLinkDownUserList) {
		this.criticalPortLinkDownUserList = criticalPortLinkDownUserList;
	}
	public final boolean isWlanAPJoin() {
		return wlanAPJoin;
	}
	public final void setWlanAPJoin(boolean wlanAPJoin) {
		this.wlanAPJoin = wlanAPJoin;
	}
	public final List<String> getWlanAPJoinUserList() {
		return wlanAPJoinUserList;
	}
	public final void setWlanAPJoinUserList(List<String> wlanAPJoinUserList) {
		this.wlanAPJoinUserList = wlanAPJoinUserList;
	}
	public final boolean isWlanAPLeave() {
		return wlanAPLeave;
	}
	public final void setWlanAPLeave(boolean wlanAPLeave) {
		this.wlanAPLeave = wlanAPLeave;
	}
	public final List<String> getWlanAPLeaveUserList() {
		return wlanAPLeaveUserList;
	}
	public final void setWlanAPLeaveUserList(List<String> wlanAPLeaveUserList) {
		this.wlanAPLeaveUserList = wlanAPLeaveUserList;
	}
	public final boolean isWebUpdate() {
		return webUpdate;
	}
	public final void setWebUpdate(boolean webUpdate) {
		this.webUpdate = webUpdate;
	}
	public final List<String> getWebUpdateUserList() {
		return webUpdateUserList;
	}
	public final void setWebUpdateUserList(List<String> webUpdateUserList) {
		this.webUpdateUserList = webUpdateUserList;
	}

}
