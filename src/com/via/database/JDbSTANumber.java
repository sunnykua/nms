package com.via.database;

public class JDbSTANumber {
	private JDatabase dbInst;
	private String tableName;
	//private String tableNameInQuotes;
	private final String[] tableDefinition = {
			"SCHEDULETIME	VARCHAR(255) NOT NULL, ",
			"RECTIME		VARCHAR(255) NOT NULL, ",
			"ACIP			VARCHAR(255) NOT NULL, ",
			"TOTAL			VARCHAR(255) NOT NULL, ",
			"AN				VARCHAR(255) NOT NULL, ",
			"BGN			VARCHAR(255) NOT NULL " };

	public JDbSTANumber(final JDatabase database, final String tableName) {
		this.dbInst = database;
		this.tableName = tableName;
		//this.tableNameInQuotes = "\"" + tableName + "\"";
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

	public String getFirstTime(final String ip) {
		String ACIP = "'" + ip + "'";

		String[] result = dbInst.getFirst(tableName, "RECTIME", "ACIP", ACIP);

		return result != null ? result[1] : "0";
	}

	public String getLastTime(final String ip) {
		String ACIP = "'" + ip + "'";

		String[] result = dbInst.getLast(tableName, "RECTIME", "ACIP", ACIP);

		return result != null ? result[1] : "0";
	}

	public String[][] getSTANumberHistoryByColumValue(final String Column, final String value) {

		String Keyword = "'" + value + "'";

		String[][] result = dbInst.getByColumn(tableName, Column, Keyword);

		return result;
	}
}
