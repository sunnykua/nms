package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JNode {
	private String name;
	private String type;
	private String inport;
	private String outport;
	private String icon;
	private String strokecolor;
	private List<JNode> children = new ArrayList<JNode>();

	public JNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setInport(String inport) {
		this.inport = inport;
	}

	public String getInport() {
		return inport;
	}

	public void setOutport(String outport) {
		this.outport = outport;
	}

	public String getOutport() {
		return outport;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}

	public void setStrokecolor(String strokecolor) {
		this.strokecolor = strokecolor;
	}

	public String getStrokecolor() {
		return strokecolor;
	}

	public List<JNode> getChildren() {
		return children;
	}

	public void addChildren(JNode node) {
		children.add(node);
	}
}