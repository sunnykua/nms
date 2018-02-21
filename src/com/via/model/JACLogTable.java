package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JACLogTable {
	public class JACLogItem {
		private String username;
		private String ipaddress;
		private String time;
		private String loginfo;

		public final String getUsername() {
			return username;
		}

		public final void setUsername(String username) {
			this.username = username;
		}

		public final String getIpaddress() {
			return ipaddress;
		}

		public final void setIpaddress(String ipaddress) {
			this.ipaddress = ipaddress;
		}

		public final String getTime() {
			return time;
		}

		public final void setTime(String time) {
			this.time = time;
		}

		public final String getLoginfo() {
			return loginfo;
		}

		public final void setLoginfo(String loginfo) {
			this.loginfo = loginfo;
		}
	}

	private List<JACLogItem> logItems;

	public JACLogTable() {
		this.logItems = new ArrayList<JACLogItem>();
	}

	public void addLog(String username, String ipaddress, String time, String loginfo) {
		if (username != null && ipaddress != null && time != null && loginfo != null) {
			JACLogItem item = new JACLogItem();
			item.setUsername(username);
			item.setIpaddress(ipaddress);
			item.setTime(time);
			item.setLoginfo(loginfo);
			this.logItems.add(item);
		}
	}

	public final List<JACLogItem> getLogItems() {
		return logItems;
	}
}
