package com.via.database;

import java.sql.*;
import java.util.Properties;

/*
 * Example for extracting data from MySql
	String[][] tableArray = null;
		
	try {
		JMySql nms_db = new JMySql("//localhost:3306/nms_db", "root", "12342345");
		nms_db.open();

		// Insert a set of data into database
		//String value = "6, 'Marilyn Monroe', 300, 'Marilyn@mysql.com', 'United States'";
		//nms_db.insertValues("customer", value);

		tableArray = nms_db.selectValues("customer");
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}

	if (tableArray != null && tableArray.length != 0) {
		for (int i = 0; i < tableArray.length; i++) {
			System.out.println(Arrays.toString(tableArray[i]));
		}
	}
	else {
		System.out.println("read table failed.");
	}
 * */

public class JMySql {
	private String driverName;
	private String dbName;
	private String url;
	private String username;
	private String password;
	private Connection connection;
	
	@SuppressWarnings("unused")
	private JMySql() {}
	
	public JMySql(final String dbName) {
		this.dbName = dbName;
		init("", "");
	}
	
	public JMySql(final String dbName, final String username, final String password) {
		this.dbName = dbName;
		init(username, password);
	}
	
	private void init (final String username, final String password) {
		this.driverName = "com.mysql.jdbc.Driver";
		this.url = "jdbc:mysql:" + dbName;
		this.username = username;
		this.password = password;
		this.connection = null;
	}
	
	// ====================================================================================================
	
	public void insertValues(String tableName, String values) {
    	Statement stmt = null;

		String script = String.format("INSERT INTO %s VALUES( %s );", tableName, values);

		try {
			stmt = connection.createStatement();
		}
		catch (SQLException e) {
			System.out.println("SQLException from createStatement()");
		}
		
		try {
			stmt.executeUpdate(script);
			System.out.println("excuted: " + script);
		}
		catch (SQLException e) {
			System.out.println("SQLException from executeUpdate()");
		}
    }
	
	public void insertValues(String tableName, String[] values) {
    	Statement stmt = null;
		String valueString = "";

		for (int i = 0; i < values.length; i++) {
			if (i == 0) valueString = values[0];
			else valueString += ", " + values[i];
		}

		String script = String.format("INSERT INTO %s VALUES( %s );", tableName, valueString);

		try {
			stmt = connection.createStatement();
		}
		catch (SQLException e) {
			System.out.println("SQLException from createStatement()");
		}
		
		try {
			stmt.executeUpdate(script);
			System.out.println("excuted: " + script);
		}
		catch (SQLException e) {
			System.out.println("SQLException from executeUpdate()");
		}
    }
	
	public String[][] selectValues(String tableName) {
    	Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		int columnCount = 0;
		int rowCount = 0;
		String[][] tableArray = new String[0][0];
		
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + tableName);
			rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			while(rs.next()) {
				rowCount++;
			}
			tableArray = new String[rowCount][columnCount];
			
			int currRow = 0;
			rs.beforeFirst();
			while(rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					tableArray[currRow][i - 1] = rs.getString(i);
				}
				currRow++;
			}
			rs.close();
		}
		catch (SQLException e) {
			System.out.println("SQLException");
		}
		
		return tableArray;
    }
	
	public void open() throws Exception {
		Properties props = new Properties();
		props.put("user", username);
		props.put("password", password);
    	
		try {
			Class.forName(driverName);
			
			this.connection = DriverManager.getConnection(url, props);
		}
    	catch (ClassNotFoundException e) {
    		//System.out.println("ClassNotFoundException");
    		throw new Exception("ClassNotFoundException");
		}
    	catch (SQLException e) {
			//System.out.println("SQLException from getConnection()");
			throw new Exception("SQLException from getConnection()");
		}
	}
}
