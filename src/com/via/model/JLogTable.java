package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JLogTable {	
	public class JLogItem {
		private String time;
		private String level;
		private String message;
		
		public final String getTime() {
			return time;
		}
		public final void setTime(String time) {
			this.time = time;
		}
		public final String getLevel() {
			return level;
		}
		public final void setLevel(String level) {
			this.level = level;
		}
		public final String getMessage() {
			return message;
		}
		public final void setMessage(String message) {
			this.message = message;
		}
	}
	
	private List<JLogItem> logItems;
	
	public JLogTable() {
		this.logItems = new ArrayList<JLogItem>();
	}
	
	public void addLog(String time, String level, String message) {
		if (time != null && level != null && message != null) {
			JLogItem item = new JLogItem();
			item.setTime(time);
			item.setLevel(level);
			item.setMessage(message);
			this.logItems.add(item);
		}
	}

	public final List<JLogItem> getLogItems() {
		return logItems;
	}
}

