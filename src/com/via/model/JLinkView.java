package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JLinkView {
	
	public class JLinkViewItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String adminStatus;
		private String operStatus;
		
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
		public String getAdminStatus() {
			return adminStatus;
		}
		public void setAdminStatus(String adminStatus) {
			this.adminStatus = adminStatus;
		}
		public String getOperStatus() {
			return operStatus;
		}
		public void setOperStatus(String operStatus) {
			this.operStatus = operStatus;
		}
	}
	
	private List<JLinkViewItem> linkViewItems;
	
	public JLinkView() {
		this.linkViewItems = new ArrayList<JLinkViewItem>();
	}
	
	public void addLinkView(int portId, int ifIndex, int stackId, String adminStatus, String operStatus){
		JLinkViewItem item = new JLinkViewItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setAdminStatus(adminStatus);
		item.setOperStatus(operStatus);
		this.linkViewItems.add(item);
	}
	
	public final List<JLinkViewItem> getLinkViewItems() {
		return linkViewItems;
	}
}
