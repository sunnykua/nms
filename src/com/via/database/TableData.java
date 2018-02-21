package com.via.database;

import java.util.List;

public class TableData {
	private String name;
	private List<List<String>> columns;
	private List<List<String>> rows;
	
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final List<List<String>> getColumns() {
		return columns;
	}
	public final void setColumns(List<List<String>> columns) {
		this.columns = columns;
	}
	public final List<List<String>> getRows() {
		return rows;
	}
	public final void setRows(List<List<String>> rows) {
		this.rows = rows;
	}
}
