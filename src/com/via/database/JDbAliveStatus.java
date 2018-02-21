package com.via.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JDbAliveStatus {
	private JDatabase dbInst;
	private String tableName;

	private final String[] tableDefinition = { "REC_TIME        TIMESTAMP NOT NULL, ", "LAYER     	     VARCHAR(255) NOT NULL, ", "TYPE            VARCHAR(255) DEFAULT '' NOT NULL, ", "ADDR            VARCHAR(255) NOT NULL, ", "ALIVE           BOOLEAN NOT NULL ", };

	public JDbAliveStatus(final JDatabase database, final String tableName) {
		this.dbInst = database;
		this.tableName = tableName;
	}

	public boolean isTableExisted() {
		return dbInst.isTableExisted(tableName);
	}

	public boolean createTable() {
		String definition = "";
		for (String s : tableDefinition)
			definition += s;

		return dbInst.createTable(tableName, definition);
	}

	public boolean dropTable() {
		return dbInst.deleteTable(tableName);
	}

	public boolean insert(final String[] value) {
		return dbInst.insert(tableName, value);
	}

	public List<List<String[]>> getAliveStatus(final String[] SelectColumns, final String LAYER, final String startTime, final String endTime, final String ADDR) {
		double prevTime = System.currentTimeMillis();

		String SelectString = SelectColumns[0];
		for (int i = 1; i < SelectColumns.length; i++)
			SelectString += ", " + SelectColumns[0];

		String _tableName = "\"" + tableName + "\"";

		String _LAYER = "\'" + LAYER + "\'";

		String startTimeExpression = "REC_TIME >= '" + startTime + "'";

		String endTimeExpression = "REC_TIME <= '" + endTime + "'";

		List<String[]> test1 = new ArrayList<String[]>();

		String _ADDR = "";
		if (!ADDR.equals("")) {
			_ADDR = "AND ADDR = '" + ADDR + "' ";
		}
		
		String query1 = String.format("SELECT DISTINCT ADDR FROM %s WHERE LAYER = %s AND %s AND %s", _tableName, _LAYER, startTimeExpression, endTimeExpression);
		//System.out.println(query1);

		String[][] result1 = dbInst.getByQuery(query1);
		if (result1 != null) {
			test1 = Arrays.asList(result1);
		}

		String query2 = String.format("SELECT %s FROM %s WHERE LAYER = %s AND %s AND %s %sORDER BY REC_TIME ASC", SelectString, _tableName, _LAYER, startTimeExpression, endTimeExpression, _ADDR);
		//System.out.println(query2);

		String[][] result2 = dbInst.getByQuery(query2);

		List<String[]> test2 = new ArrayList<String[]>();
		if (result2 != null) {
			test2 = Arrays.asList(result2);
		}

		List<List<String[]>> getLastAliveStatusList = new ArrayList<List<String[]>>();

		getLastAliveStatusList.add(test1);
		getLastAliveStatusList.add(test2);

		System.out.println("Get AliveStatus, costs: " + (System.currentTimeMillis() - prevTime) / 1000 + " sec.");

		return getLastAliveStatusList;
	}
}
