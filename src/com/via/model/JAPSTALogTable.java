package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JAPSTALogTable {
	public class JAPSTALogItem {
		private String datetime;
		private String mac;
		private String message;
		private String eventid;

		public final String getDatetime() {
			return datetime;
		}

		public final void setDatetime(String datetime) {
			this.datetime = datetime;
		}

		public final String getMac() {
			return mac;
		}

		public final void setMac(String mac) {
			this.mac = mac;
		}

		public final String getMessage() {
			return message;
		}

		public final void setMessage(String message) {
			this.message = message;
		}

		public final String getEventid() {
			return eventid;
		}

		public final void setEventid(String eventid) {
			this.eventid = eventid;
		}
	}

	private List<JAPSTALogItem> logItems;

	public JAPSTALogTable() {
		this.logItems = new ArrayList<JAPSTALogItem>();
	}

	public void addLog(String datetime, String mac, String message, String eventid) {
		if (datetime != null && mac != null && message != null && eventid != null) {
			JAPSTALogItem item = new JAPSTALogItem();
			item.setDatetime(datetime);
			item.setMac(mac);
			item.setMessage(message);
			item.setEventid(eventid);
			this.logItems.add(item);
		}
	}

	public final List<JAPSTALogItem> getLogItems() {
		return logItems;
	}
}
