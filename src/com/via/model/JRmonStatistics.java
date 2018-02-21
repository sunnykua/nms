package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.via.model.JLinkView.JLinkViewItem;

public class JRmonStatistics {
	
	public class JRmonStatisticsItem {

		private int portId;
		private int ifIndex;
		private int stackId;
		private String dropEvents;
		private String octets;
		private String pkts;
		private String broadcastPkts;
		private String multicastPkts;
		private String crcAlignErrors;
		private String undersizePkts;
		private String oversizePkts;
		private String fragments;
		private String jabbers;
		private String collisions;
		private String pkts64Octets;
		private String pkts65to127Octets;
		private String pkts128to255Octets;
		private String pkts256to511Octets;
		private String pkts512to1023Octets;
		private String pkts1024to1518Octets;

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
		public String getDropEvents() {
			return dropEvents;
		}
		public void setDropEvents(String dropEvents) {
			this.dropEvents = dropEvents;
		}
		public String getOctets() {
			return octets;
		}
		public void setOctets(String octets) {
			this.octets = octets;
		}
		public String getPkts() {
			return pkts;
		}
		public void setPkts(String pkts) {
			this.pkts = pkts;
		}
		public String getBroadcastPkts() {
			return broadcastPkts;
		}
		public void setBroadcastPkts(String broadcastPkts) {
			this.broadcastPkts = broadcastPkts;
		}
		public String getMulticastPkts() {
			return multicastPkts;
		}
		public void setMulticastPkts(String multicastPkts) {
			this.multicastPkts = multicastPkts;
		}
		public String getCrcAlignErrors() {
			return crcAlignErrors;
		}
		public void setCrcAlignErrors(String crcAlignErrors) {
			this.crcAlignErrors = crcAlignErrors;
		}
		public String getUndersizePkts() {
			return undersizePkts;
		}
		public void setUndersizePkts(String undersizePkts) {
			this.undersizePkts = undersizePkts;
		}
		public String getOversizePkts() {
			return oversizePkts;
		}
		public void setOversizePkts(String oversizePkts) {
			this.oversizePkts = oversizePkts;
		}
		public String getFragments() {
			return fragments;
		}
		public void setFragments(String fragments) {
			this.fragments = fragments;
		}
		public String getJabbers() {
			return jabbers;
		}
		public void setJabbers(String jabbers) {
			this.jabbers = jabbers;
		}
		public String getCollisions() {
			return collisions;
		}
		public void setCollisions(String collisions) {
			this.collisions = collisions;
		}
		public String getPkts64Octets() {
			return pkts64Octets;
		}
		public void setPkts64Octets(String pkts64Octets) {
			this.pkts64Octets = pkts64Octets;
		}
		public String getPkts65to127Octets() {
			return pkts65to127Octets;
		}
		public void setPkts65to127Octets(String pkts65to127Octets) {
			this.pkts65to127Octets = pkts65to127Octets;
		}
		public String getPkts128to255Octets() {
			return pkts128to255Octets;
		}
		public void setPkts128to255Octets(String pkts128to255Octets) {
			this.pkts128to255Octets = pkts128to255Octets;
		}
		public String getPkts256to511Octets() {
			return pkts256to511Octets;
		}
		public void setPkts256to511Octets(String pkts256to511Octets) {
			this.pkts256to511Octets = pkts256to511Octets;
		}
		public String getPkts512to1023Octets() {
			return pkts512to1023Octets;
		}
		public void setPkts512to1023Octets(String pkts512to1023Octets) {
			this.pkts512to1023Octets = pkts512to1023Octets;
		}
		public String getPkts1024to1518Octets() {
			return pkts1024to1518Octets;
		}
		public void setPkts1024to1518Octets(String pkts1024to1518Octets) {
			this.pkts1024to1518Octets = pkts1024to1518Octets;
		}
	}
	
	private List<JRmonStatisticsItem> rmonStatisticsItems;
	
	public JRmonStatistics() {
		this.rmonStatisticsItems = new ArrayList<JRmonStatisticsItem>();
	}
	
	public void addRmonStatistics(int portId, int ifIndex, int stackId, 
			String dropEvents, String octets, String pkts, String broadcastPkts, String multicastPkts, String crcAlignErrors, String undersizePkts, String oversizePkts, String fragments, String jabbers,
			String collisions, String pkts64Octets, String pkts65to127Octets, String pkts128to255Octets, String pkts256to511Octets, String pkts512to1023Octets, String pkts1024to1518Octets){
		JRmonStatisticsItem item = new JRmonStatisticsItem();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setStackId(stackId);
		item.setDropEvents(dropEvents);
		item.setOctets(octets);
		item.setPkts(pkts);
		item.setBroadcastPkts(broadcastPkts);
		item.setMulticastPkts(multicastPkts);
		item.setCrcAlignErrors(crcAlignErrors);
		item.setUndersizePkts(undersizePkts);
		item.setOversizePkts(oversizePkts);
		item.setFragments(fragments);
		item.setJabbers(jabbers);
		item.setCollisions(collisions);
		item.setPkts64Octets(pkts64Octets);
		item.setPkts65to127Octets(pkts65to127Octets);
		item.setPkts128to255Octets(pkts128to255Octets);
		item.setPkts256to511Octets(pkts256to511Octets);
		item.setPkts512to1023Octets(pkts512to1023Octets);
		item.setPkts1024to1518Octets(pkts1024to1518Octets);
		this.rmonStatisticsItems.add(item);
	}
	
	public final List<JRmonStatisticsItem> getRmonStatisticsItems() {
		return rmonStatisticsItems;
	}
}
