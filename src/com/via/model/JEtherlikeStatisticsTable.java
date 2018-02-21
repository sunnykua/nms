package com.via.model;

import java.util.List;

public class JEtherlikeStatisticsTable {

	private String descr;
	private String ipAddr;
	private String localIp;
	private int itemCount;
	private List<JEtherlikeStatisticsItem> items;
	private int portNum;

	public String getSysDescr() {
		return descr;
	}
	public void setSysDescr(String descr) {
		this.descr = descr;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public List<JEtherlikeStatisticsItem> getItems() {
		return items;
	}
	public void setItems(List<JEtherlikeStatisticsItem> items) {
		this.items = items;
	}
	public int getPortNum() {
		return portNum;
	}
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
}
