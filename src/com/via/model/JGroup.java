package com.via.model;

import java.util.ArrayList;


public class JGroup {
	private String id;
	private String name;
	private String aliasName;
	private String parent;
	private ArrayList<String> children;
	private String type;
	private JEntry entry;
	
	private boolean isRoot;
	private boolean belongGroup;
	private int aliveStatus;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public ArrayList<String> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<String> children) {
		this.children = children;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JEntry getEntry() {
		return entry;
	}
	public void setEntry(JEntry entry) {
		this.entry = entry;
	}
	public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public boolean isBelongGroup() {
		return belongGroup;
	}
	public void setBelongGroup(boolean belongGroup) {
		this.belongGroup = belongGroup;
	}
	public int getAliveStatus() {
		return aliveStatus;
	}
	public void setAliveStatus(int aliveStatus) {
		this.aliveStatus = aliveStatus;
	}
}
