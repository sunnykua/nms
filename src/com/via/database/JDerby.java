package com.via.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

enum DerbyType {
	Embedded, ClientServer
}

public class JDerby {
	public static final DerbyType Embedded = DerbyType.Embedded;
	public static final DerbyType ClientServer = DerbyType.ClientServer;
	
	private DerbyType type;
	private String typeName;
	private String driverName;
	//private String dbName;				// currently not use
	private String url;
	private String username;
	private String password;
	
	private Connection connection;
	private Statement statement;
	private final Object lock = new Object();  // use to lock statement bcz ResultSet obtained from statement will be closed if someone else request a new statement
	
	public JDerby (final DerbyType type, final String url) {
		init(type, url, "user", "user");
	}
	
	public JDerby (final DerbyType type, final String url, final String username, final String password) {
		init(type, url, username, password);
	}
	
	/*public void setParameter(final DerbyType type, final String dbName) {
		this.type = type;
		this.dbName = dbName;
		init("", "");
	}*/
	
	private void init (final DerbyType type, final String url, final String username, final String password) {
		this.type = type;
		if (this.type == Embedded) {
			this.driverName = "org.apache.derby.jdbc.EmbeddedDriver";
			this.typeName = "Embedded";
		}
		else if (this.type == ClientServer) {
			this.driverName = "org.apache.derby.jdbc.ClientDriver";
			this.typeName = "Client";
		}
		
		//this.dbName = "";
		this.url = url;
		this.username = username;
		this.password = password;
		this.connection = null;
		this.statement = null;
//		System.out.println("[OK] Derby is created in " + typeName + " type and URL: " + url);
		
		open();
	}
	
	// ====================================================================================================
	
	public final String getTypeName() {
        return typeName;
    }

    public boolean findSchema(final String schemaName) {
		if (!isConnected()) {
			System.out.println("[WARN] @findSchema(): No conneciton, try to open.");
			if (!open()) return false;
		}
		
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("[ERROR] @findSchema(): getMetaData() failed.\n\t - " + e.getMessage());
			return false;
		}
		
		ResultSet schemas;
		try {
			schemas = dbm.getSchemas(null, schemaName);
		} catch (SQLException e) {
			System.out.println("[ERROR] @findSchema(): getSchemas() failed.\n\t - " + e.getMessage());
			return false;
		}
		
		boolean isExisted = false;
		try {
			if (schemas.next()) {
				isExisted = true;
			}
			else {
				isExisted = false;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] @findSchema(): rs.next() failed.\n\t - " + e.getMessage());
		}
		
		try {
			schemas.close();
		} catch (SQLException e) {
			System.out.println("[WARN] @findSchema(): rs.close() failed.\n\t - " + e.getMessage());
		}
		
		return isExisted;
	}
	
	/**
	 * @param tableName must in upper case and without schema name.
	 * @return
	 */
	public boolean findTable(final String tableName) {
		if (!isConnected()) {
			System.out.println("[WARN] @findTable(): No conneciton, try to open.");
			if (!open()) return false;
		}
		
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("[ERROR] @findTable(): getMetaData() failed.\n\t - " + e.getMessage());
			return false;
		}
		
		ResultSet tables;
		try {
			tables = dbm.getTables(null, null, tableName, null);
		} catch (SQLException e) {
			System.out.println("[ERROR] @findTable(): getTables() failed.\n\t - " + e.getMessage());
			return false;
		}
		
		boolean isExisted = false;
		try {
			if (tables.next()) {
				isExisted = true;
			}
			else {
				isExisted = false;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] @findTable(): rs.next() failed.\n\t - " + e.getMessage());
		}
		
		try {
			tables.close();
		} catch (SQLException e) {
			System.out.println("[WARN] @findTable(): rs.close() failed.\n\t - " + e.getMessage());
		}
		
		return isExisted;
	}
	
	public boolean execute(final String query) {
		if (!isConnected()) {
			System.out.println("[WARN] @execute(): No conneciton, try to open.");
			if (!open()) return false;
		}
		
//		Statement stmt;
		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
//			stmt.execute(query);
		    synchronized (lock) {
		        statement.execute(query);
		    }
			//System.out.println("[OK] sql: " + query);
		} catch (SQLException e) {
			System.out.println("[ERROR] @execute(): " + e.getMessage());
			return false;
		}
		
//		try {
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println("[WARN] @execute(): Close() failed.\n\t - " + e.getMessage());
//		}
		
		return true;
	}
	
	public boolean insertOne(final String tableName, final String[] data) {
		if (!isConnected()) {
			System.out.println("[WARN] @insertOne(): No conneciton, try to open.");
			if (!open()) return false;
		}
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for ( ; i < data.length - 1; i++) {
			sb.append(data[i] + ", ");
		}
		String values = sb.toString() + data[i];
		String sql = String.format("INSERT INTO %s VALUES ( %s )", tableName, values);
		
//		Statement stmt;
		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			@SuppressWarnings("unused")
			int ret;
//			int ret = stmt.executeUpdate(sql);									// will return row count or 0 for nothing, otherwise, success if no exception
			//double timeForIns = System.currentTimeMillis();
			synchronized (lock) {
			    ret = statement.executeUpdate(sql);
			}
			//System.out.println("    insertOne use " + (System.currentTimeMillis() - timeForIns) / 1000 + " sec.");
			//System.out.println("[OK] ret: " + ret + ", sql: " + sql);
		} catch (SQLException e) {
			System.out.println("[ERROR] @insertOne(): " + e.getMessage());
			System.out.println("sql: " + sql);
			return false;
		}
		
//		try {
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println("[WARN] @insertOne(): Close() failed.\n\t - " + e.getMessage());
//		}
		
		return true;
	}
	
	public boolean insertMany(final String tableName, final String[][] data) {
		if (!isConnected()) {
			System.out.println("[WARN] @insertMany(): No conneciton, try to open.");
			if (!open()) return false;
		}
		
		String sql = String.format("INSERT INTO %s VALUES ", tableName);
		String[] valueStrings = new String[data.length];
		
		int i;
		for (i = 0; i < data.length; i++) {
			StringBuilder sb = new StringBuilder();
			int j = 0;
			for ( ; j < data[i].length - 1; j++) {
				sb.append(data[i][j] + ",");
			}
			valueStrings[i] = String.format("( %s %s )", sb.toString(), data[i][j]);	// append last field to each value string
		}
		for (i = 0; i < data.length - 1; i++) {
			sql += valueStrings[i] + ", ";
		}
		sql += valueStrings[i];					// append the last value string
		
//		Statement stmt;
		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
//			int ret = stmt.executeUpdate(sql);									// same as insertOne()
		    int ret = -1;
		    synchronized (lock) {
		        ret = statement.executeUpdate(sql);
		    }
			System.out.println("[OK] ret: " + ret + ", sql: " + sql);
		} catch (SQLException e) {
			System.out.println("[ERROR] @insertMany(): " + e.getMessage());
			return false;
		}
		
//		try {
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println("[WARN] @insertMany(): Close() failed.\n\t - " + e.getMessage());
//		}
		
		return true;
	}
	
	public boolean deleteRow(final String tableName, final String filterColumnName, final String filterColumnValue) {
		if (!isConnected()) {
			System.out.println("[WARN] @deleteRow(): No conneciton, try to open.");
			if (!open()) return false;
		}
		boolean flag = true;
		String sql = String.format("DELETE FROM %s WHERE UPPER(%s) LIKE UPPER(%s)", tableName, filterColumnName, filterColumnValue);
		//System.out.println("sql: " + sql);
		
//		Statement stmt = null;
		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
//			int ret = stmt.executeUpdate(sql);									// will return row count or 0 for nothing, otherwise, success if no exception
		    int ret = -1;
		    synchronized (lock) {
		        ret = statement.executeUpdate(sql);
		    }
			if (ret < 1) {
				System.out.println("[ERROR] @deleteRow(): delete nothing from database.");
				flag = false;
			}
			else {
				//System.out.println("[OK] delete " + ret + " rows success.");
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] @deleteRow(): " + e.getMessage());
			flag = false;
		}
		
//		try {
//			if (stmt != null) stmt.close();
//		} catch (SQLException e) {
//			System.out.println("[WARN] @deleteRow(): Close() failed.\n\t - " + e.getMessage());
//		}
		
		return flag;
	}
	
	public boolean deleteRow(final String tableName, final String filterColumnName, final int filterColumnValue) {
		if (!isConnected()) {
			System.out.println("[WARN] @deleteRow(): No conneciton, try to open.");
			if (!open()) return false;
		}
		boolean flag = true;
		String sql = String.format("DELETE FROM %s WHERE %s = %s", tableName, filterColumnName, filterColumnValue);
		//System.out.println("sql: " + sql);
		
		try {
		    int ret = -1;
		    synchronized (lock) {
		        ret = statement.executeUpdate(sql);
		    }
			if (ret < 1) {
				System.out.println("[ERROR] @deleteRow(): delete nothing from database.");
				flag = false;
			}
			else {
				//System.out.println("[OK] delete " + ret + " rows success.");
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] @deleteRow(): " + e.getMessage());
			flag = false;
		}
		
		return flag;
	}
	
	// ==================================================================================================== SELECT SQL
	
	private String[][] fromResultSet(ResultSet rs) {
		if (rs == null) {
			System.out.println("[ERROR] ResultSet is null.");
			return null;
		}
		
		int columnCount;
		int rowCount;
		try {
			columnCount = rs.getMetaData().getColumnCount();			// ***** NOTE: if nothing is queried, it will still has null in corresponding fields. *****
			rs.last();
			rowCount = rs.getRow();
			//System.out.println("fromResultSet() rowCount=" + rowCount + ", columnCount=" + columnCount);
			if (rowCount == 0 || columnCount == 0) {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] Get column or row count failed.\n\t - " + e.getMessage());
			return null;
		}
		
		String[][] values = new String[rowCount][columnCount];
		try {
			rs.beforeFirst();
			int currRow = 0;
			while (rs.next()) {
				for (int i = 0; i < columnCount; i++) {
					values[currRow][i] = rs.getString(i + 1);			// ***** NOTE: just as the doc said, if nothing is queried, it will return null (not string null) *****
				}
				currRow++;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] Get data from ResultSet failed.\n\t - " + e.getMessage());
			return null;
		}
		
		return values;
	}
	
	private String mergeFilterExpression(final String[] filterExpressions) {
		String filterString = "";
		
		if (filterExpressions != null && filterExpressions.length > 0) {
			filterString = "WHERE " + filterExpressions[0];
			for (int i = 1; i < filterExpressions.length; i++) {
				filterString += " AND " + filterExpressions[i];
			}
		}
		
		return filterString;
	}
	
	public int getRowCount(final String tableName) {
		
		String query = String.format("SELECT COUNT(*) FROM %s", tableName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			try {
				return Integer.parseInt(values[0][0]);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		else {
			return -1;
		}
	}
	
	public int getRowCount(final String tableName, final String filterColumnName, final String filterColumnValue) {

		String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = %s", tableName, filterColumnName, filterColumnValue);

		String[][] values = getByQuery(query);

		if (values != null) {
			try {
				return Integer.parseInt(values[0][0]);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		else {
			return -1;
		}
	}
	
	public int getRowCount(final String tableName, final String[] filterExpressions) {
		String whereClause = mergeFilterExpression(filterExpressions);

		String query = String.format("SELECT COUNT(*) FROM %s %s", tableName, whereClause);

		String[][] values = getByQuery(query);

		if (values != null) {
			try {
				return Integer.parseInt(values[0][0]);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		else {
			return -1;
		}
	}
	
	public String[] getOne(final String tableName, final int index) {
		
		String query = String.format("SELECT * FROM %s OFFSET %d ROWS FETCH FIRST ROW ONLY", tableName, index - 1);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	public String[] getFirst(final String tableName) {
		return getOne(tableName, 1);
	}
	
	/**
	 * Get first record from 'tableName' by sorting the column 'orderColumnName' in ascent order
	 * @param tableName
	 * @param orderColumnName
	 * @return
	 */
	public String[] getFirst(final String tableName, final String orderColumnName) {
		String query = String.format(
				"SELECT * FROM %s ORDER BY %s ASC FETCH FIRST ROW ONLY", tableName, orderColumnName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get first record from 'tableName' by sorting the column 'orderColumnName' in ascent order and indicate that the value of column 'filterColumnName' must be 'filterColumnValue'
	 * @param tableName
	 * @param orderColumnName
	 * @param filterColumnName
	 * @param filterColumnValue
	 * @return
	 */
	public String[] getFirst(final String tableName, final String orderColumnName, final String filterColumnName, final String filterColumnValue) {
		String query = String.format(
				"SELECT * FROM %s WHERE %s = %s ORDER BY %s ASC FETCH FIRST ROW ONLY", tableName, filterColumnName, filterColumnValue, orderColumnName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	public String[] getFirst(final String tableName, final String orderColumnName, final String[] filterExpressions) {
		String whereClause = mergeFilterExpression(filterExpressions);
		
		String query = String.format(
				"SELECT * FROM %s %s ORDER BY %s ASC FETCH FIRST ROW ONLY", tableName, whereClause, orderColumnName);
		//System.out.println("sql: " + query);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	public String[] getLast(final String tableName) {
		String query = String.format(
				"SELECT * FROM ( SELECT ROW_NUMBER() OVER() AS R, %1$s.* FROM %1$s) AS TR ORDER BY R DESC FETCH FIRST ROW ONLY", tableName);
				// generate a column to indicate the row number, then sort it in desc order and get the first record
				// note that it will add an extra column in the front, so return all fields of data except the first
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			String[] v = new String[values[0].length - 1];
			for (int i = 0; i < values[0].length - 1; i++) {
				v[i] = values[0][i + 1];
			}
			
			return v;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get last record from 'tableName' by sorting the column 'orderColumnName' in descent order
	 * @param tableName
	 * @param orderColumnName
	 * @return
	 */
	public String[] getLast(final String tableName, final String orderColumnName) {
		String query = String.format(
				"SELECT * FROM %s ORDER BY %s DESC FETCH FIRST ROW ONLY", tableName, orderColumnName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get last record from 'tableName' by sorting the column 'orderColumnName' in descent order and indicate that the value of column 'filterColumnName' must be 'filterColumnValue'
	 * @param tableName
	 * @param orderColumnName
	 * @param filterColumnName
	 * @param filterColumnValue
	 * @return
	 */
	public String[] getLast(final String tableName, final String orderColumnName, final String filterColumnName, final String filterColumnValue) {
		String query = String.format(
				"SELECT * FROM %s WHERE %s = %s ORDER BY %s DESC FETCH FIRST ROW ONLY", tableName, filterColumnName, filterColumnValue, orderColumnName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	public String[] getLast(final String tableName, final String orderColumnName, final String[] filterExpressions) {
		String whereClause = mergeFilterExpression(filterExpressions);
		
		String query = String.format(
				"SELECT * FROM %s %s ORDER BY %s DESC FETCH FIRST ROW ONLY", tableName, whereClause, orderColumnName);
		
		String[][] values = getByQuery(query);
		
		if (values != null) {
			return values[0];
		}
		else {
			return null;
		}
	}
	
	/**Note that the order of output array is in descent order
	 * @param tableName
	 * @param orderColumnName
	 * @param filterExpressions
	 * @param rowCount
	 * @return
	 */
	public String[][] getLast(final String tableName, final String orderColumnName, final String[] filterExpressions, final int rowCount) {
		String whereClause = mergeFilterExpression(filterExpressions);
		String fetchString = rowCount > 1 ? String.valueOf(rowCount) + " ROWS" : "ROW";
		
		String query = String.format(
				"SELECT * FROM %s %s ORDER BY %s DESC FETCH FIRST %s ONLY", tableName, whereClause, orderColumnName, fetchString);
		
		return getByQuery(query);
	}
	
	public String[][] getByColumn(final String tableName, final String filterColumnName, final String filterColumnValue) {
	    String query = String.format("SELECT * FROM %s WHERE %s = %s", tableName, filterColumnName, filterColumnValue);
	    
	    return getByQuery(query);
	}
	
	public String[][] getByColumn(final String tableName, final String[] filterExpressions) {
	    String whereClause = mergeFilterExpression(filterExpressions);
	    
        String query = String.format("SELECT * FROM %s %s", tableName, whereClause);
        
        return getByQuery(query);
    }
	
	public String[][] getAll(final String tableName) {
		if (!isConnected()) {
			System.out.println("[WARN] @getAll(): No conneciton, try to open.");
			if (!open()) return null;
		}
		
		String query = String.format("SELECT * FROM %s", tableName);
		
		return getByQuery(query);
	}
	
	public String[][] getAll(final String tableName, final String descentOrderColumnName) {
		if (!isConnected()) {
			System.out.println("[WARN] @getAll(): No conneciton, try to open.");
			if (!open()) return null;
		}
		
		String query = String.format("SELECT * FROM %s ORDER BY %s DESC", tableName, descentOrderColumnName);
		
		return getByQuery(query);
	}
	
	public String[][] getHead(final String tableName) {
		if (!isConnected()) {
			System.out.println("[WARN] @getHead(): No conneciton, try to open.");
			if (!open()) return null;
		}
		
//		Statement stmt;
//		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//		} catch (SQLException e) {
//			System.out.println("[ERROR] @getHead(): Create statement failed.\n\t - " + e.getMessage());
//			return null;
//		}
		String[][] columns = null;
		synchronized (lock) {
		    ResultSet rs;
		    try {
		        //			rs = stmt.executeQuery("SELECT * FROM " + tableName + " FETCH FIRST ROW ONLY");
		        rs = statement.executeQuery("SELECT * FROM " + tableName + " FETCH FIRST ROW ONLY");
		    } catch (SQLException e) {
		        System.out.println("[ERROR] @getHead(): Excute query failed.\n\t - " + e.getMessage());
		        return null;
		    }

		    ResultSetMetaData rsmd;
		    try {
		        rsmd = rs.getMetaData();
		    } catch (SQLException e) {
		        System.out.println("[ERROR] @getHead(): Get MetaData failed.\n\t - " + e.getMessage());
		        return null;
		    }

		    int columnCount;
		    try {
		        columnCount = rsmd.getColumnCount();
		    } catch (SQLException e) {
		        System.out.println("[ERROR] @getHead(): Get column count failed.\n\t - " + e.getMessage());
		        return null;
		    }
		    //System.out.println("ColumnCount: " + columnCount);

		    columns = new String[columnCount][3];
		    for (int i = 0; i < columnCount; i++) {
		    	String name, type, size;
		        try {
		        	name = rsmd.getColumnName(i + 1);
		        	type = rsmd.getColumnTypeName(i + 1);
		        	size = String.valueOf(rsmd.getColumnDisplaySize(i + 1));
		            //System.out.println("ColumnName: " + name + ", ColumnType: " + type + ", ColumnSize: " + size);
		        } catch (SQLException e) {
		            System.out.println("[ERROR] @getHead(): Get column heads failed.\n\t - " + e.getMessage());
		            return null;
		        }
		        columns[i][0] = name;
		        columns[i][1] = type;
		        columns[i][2] = size;
		    }
		}
		
//		try {
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println("[WARN] @getHead(): Close statement failed.\n\t - " + e.getMessage());
//		}
		
		return columns;
	}
	
	/**
	 * @param query
	 * @return if there is no result from the query, corresponding fields will be null.
	 */
	public String[][] getByQuery(final String query) {
		if (!isConnected()) {
			System.out.println("[WARN] @getByQuery(): No conneciton, try to open.");
			if (!open()) return null;
		}
		
//		Statement stmt;
//		try {
//			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//		} catch (SQLException e) {
//			System.out.println("[ERROR] @getByQuery: Create statement failed.\n\t - " + e.getMessage());
//			return null;
//		}
		String[][] values = null;
		synchronized (lock) {
		    ResultSet rs;
		    try {
		        //			rs = stmt.executeQuery(query);
		        //double timeForExecQ = System.currentTimeMillis();
		        rs = statement.executeQuery(query);
		        //System.out.print("    getByQuery exec use " + (System.currentTimeMillis() - timeForExecQ) / 1000 + " sec, ");
		    } catch (SQLException e) {
		        System.out.println("[ERROR] @getByQuery: Excute query failed.\n\t - " + e.getMessage());
		        return null;
		    }

		    //double timeForRS = System.currentTimeMillis();
		    values = fromResultSet(rs);
		    //System.out.println("calc use " + (System.currentTimeMillis() - timeForRS) / 1000 + " sec.");
		}
		
//		try {
//			stmt.close();				// NOTE: it will also close ResultSet
//		} catch (SQLException e) {
//			System.out.println("[WARN] @getByQuery: Close statement failed.\n\t - " + e.getMessage());
//		}
		
		return values;
	}
	/*
	public String[] getTableNames() {
		if (connection == null) {
			System.out.println("[ERROR] @execute(): No conneciton.");
			return null;
		}
		
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("[ERROR] @tableExisted(): getMetaData() failed.\n\t - " + e.getMessage());
			return null;
		}
		
		ResultSet tables;
		try {
			tables = dbm.getTables(null, "app", "", null);
		} catch (SQLException e) {
			System.out.println("[ERROR] @tableExisted(): getTables() failed.\n\t - " + e.getMessage());
			return null;
		}
		
		String[] tableNames = null;
		try {
			tables.last();
			tableNames = new String[tables.getRow()];
			tables.beforeFirst();
			int currRow = 0;
			
			while (tables.next()) {
				tableNames[currRow] = tables.getString(0);
				currRow++;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] @tableExisted(): failed.\n\t - " + e.getMessage());
			return null;
		}
		
		return tableNames;
	}
*/	

	public boolean update(final String tableName, final String columnName, final String columnValue, final String filterColumnName, final String filterColumnValue) {
	    if (!isConnected()) {
            System.out.println("[WARN] @insertOne(): No conneciton, try to open.");
            if (!open()) return false;
        }
	    
	    String sql = String.format("UPDATE %s SET %s = %s WHERE %s = %s", tableName, columnName, columnValue, filterColumnName, filterColumnValue);
	    
//	    Statement stmt;
        try {
//            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
//            stmt.executeUpdate(sql);
            synchronized (lock) {
                statement.executeUpdate(sql);
            }
            //System.out.println("[OK] sql: " + query);
        } catch (SQLException e) {
            System.out.println("[ERROR] @execute(): " + e.getMessage());
            return false;
        }
        
//        try {
//            stmt.close();
//        } catch (SQLException e) {
//            System.out.println("[WARN] @execute(): Close() failed.\n\t - " + e.getMessage());
//        }
        
        return true;
	}
	
	public final boolean isConnected() {
		if (connection == null || statement == null) return false;
		
		try {
            if (connection.isClosed() || statement.isClosed()) return false;
        } catch (SQLException e) {
            System.out.println("[ERROR] @isConnected(): Check connection or statement failed.\n\t" + e.getMessage());
            return false;
        }
		
		return true;
	}

	public boolean open() {
    	if (url.isEmpty()) {
    		System.out.println("[ERR] @open(): Database url is empty.");
    		return false;
    	}
    	Properties props = new Properties();
        props.put("user", username);
        props.put("password", password);
    	
    	try {
			if (connection != null && !connection.isClosed()) {
				//System.out.println("[OK] Connection has already opened.");
				//return true;
			}
			else {
			    try {
		            Class.forName(driverName).newInstance();
		        } catch (InstantiationException e) {
		            System.out.println("[ERR] @open(): InstantiationException of Class.forName");
		            return false;
		        } catch (IllegalAccessException e) {
		            System.out.println("[ERR] @open(): IllegalAccessException of Class.forName");
		            return false;
		        } catch (ClassNotFoundException e) {
		            System.out.println("[ERR] @open(): ClassNotFoundException of Class.forName");
		            return false;
		        }
			    
			    try {
		            this.connection = DriverManager.getConnection(url, props);
		            //System.out.println("[OK] Connected to db " + url);
		        }
		        catch (SQLException e) {
		            System.out.println("[ERR] @open(): Connect to db failed.\n\t" + e.getMessage());
		            return false;
		        }
			}
		} catch (SQLException e) {
			System.out.println("[ERR] @open(): Check connection failed.");
			return false;
		}
    	
    	try {
            this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //System.out.println("[OK] Sql statement has created.");
        }
        catch (SQLException e) {
            System.out.println("[ERR] @open(): Sql statement creates failed.\n\t" + e.getMessage());
            return false;
        }
    	
    	return true;
    }
	
	public void close() {
		try {
		    if (statement != null) statement.close();
		    //System.out.println("[OK] Statement closed.");
		} catch (SQLException e) {
		    System.out.println("[ERROR] @close(): Statement closes failed.\n\t - " + e.getMessage());
		}
		
		try {
		    if (connection == null) connection.close();
			//System.out.println("[OK] Connection closed.");
		} catch (SQLException e) {
			System.out.println("[ERROR] @close(): Connection closes failed.\n\t" + e.getMessage());
		}

		/*try {
			DriverManager.getConnection(url + ";shutdown=true");     // NOTE: this will shutdown the database and no application can access any more.
		} catch (SQLException se) {
			System.out.println("[OK] Database shut down normally");				// NOTE: success case
		}*/
	}
}
