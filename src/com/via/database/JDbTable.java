package com.via.database;

public class JDbTable {
	
	private String name = "";
	private String[] heads = {""};
	private String[][] data = {{""}};
	
	public JDbTable() {
		/*this.name = "";
		this.heads = null;
		this.data = null;*/
	}
	
	public final String getSysName() {
		return name;
	}
	public final void setSysName(String name) {
		this.name = name;
	}
	public final String[] getHeads() {
		return heads;
	}
	public final void setHeads(String[] heads) {
		this.heads = heads;
	}
	public final String[][] getData() {
		return data;
	}
	public final void setData(String[][] data) {
		this.data = data;
	}
}
