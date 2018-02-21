package com.via.database;

public class TableColumn {
	private String name;
	private String type;
	private int size;
	private boolean isNotNull;
	private boolean isUnique;
	private boolean isPrimary;
	private boolean isID;
	
	public TableColumn() {
		name = "";
		type = "";
		size = 0;
		isNotNull = false;
		isUnique = false;
		isPrimary = false;
		isID = false;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getType() {
		return type;
	}
	public final void setType(String type) {
		this.type = type;
	}
	public final int getSize() {
		return size;
	}
	public final void setSize(int size) {
		this.size = size;
	}
	public final boolean isNotNull() {
		return isNotNull;
	}
	public final void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}
	public final boolean isUnique() {
		return isUnique;
	}
	public final void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	public final boolean isPrimary() {
		return isPrimary;
	}
	public final void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public final boolean isID() {
		return isID;
	}
	public final void setID(boolean isID) {
		this.isID = isID;
	}
}
