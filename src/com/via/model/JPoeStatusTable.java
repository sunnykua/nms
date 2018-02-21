package com.via.model;

import java.util.List;

public class JPoeStatusTable {

	private String descr;
	private String ipAddr;
	private String localIp;
	private int itemCount;
	private List<JPoeStatusItem> items;
	private int poeNum;
	private int poeGlobalNum;

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
	public List<JPoeStatusItem> getItems() {
		return items;
	}
	public void setItems(List<JPoeStatusItem> items) {
		this.items = items;
	}
	public int getPoeNum() {
		return poeNum;
	}
	public void setPoeNum(int poeNum) {
		this.poeNum = poeNum;
	}
	public int getPoeGlobalNum() {
		return poeGlobalNum;
	}
	public void setPoeGlobalNum(int poeGlobalNum) {
		this.poeGlobalNum = poeGlobalNum;
	}


}
