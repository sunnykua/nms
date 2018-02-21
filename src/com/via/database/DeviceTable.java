package com.via.database;

import java.util.List;

import com.via.model.JTools;

public class DeviceTable {
	private static JDatabase dbInst;
	private static String tableName;
    
    static {
    	tableName = "DEVICE04";
    	dbInst = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
    	//connectDatabase();
    }
    
    private static boolean connectDatabase() {
    	if (dbInst.isConnected() == false) {		// TODO Currently it will re-create JDb object if closed once, but is not good
			dbInst = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
		}
    	
    	/*if (dbInst.isConnected() && dbInst.isTableExisted(tableName) == false) {
    		String definition = "";
    		for (String s : tableDefinition) definition += s;
    		if (dbInst.createTable(tableName, definition)) {
    			System.out.println("DeviceTable: Create table success.");
    		}
    		else {
    			System.out.println("DeviceTable: Create table FAILED.");
    		}
		}*/
    	
    	return isConnected();
    }
    
    private static void disconnectDatabase() {
    	dbInst.disconnect();
    }
    
    private static boolean isConnected() {
    	return dbInst.isConnected() && dbInst.isTableExisted(tableName);
    }
    
    public static TableData exportData() {
    	connectDatabase();
    	
    	TableData table = dbInst.getTable(tableName);
    	
    	disconnectDatabase();
    	return table;
    }
    
    public static boolean importData(TableData tableData) {
    	boolean result = false;
    	List<List<String>> inputColumns = tableData.getColumns();
    	List<List<String>> inputRows = tableData.getRows();
    	
    	if (inputColumns.size() == 0 || inputRows.size() == 0) {
    		System.out.println("DeviceTable@importData: input is empty, do nothing.");
    		return false;
    	}
    	
    	System.out.println("DeviceTable@importData: column number: " + inputColumns.size() + ", row number: " + inputRows.size());
    	/*System.out.println("tableName: " + tableData.getName());
    	for (List<String> column : inputColumns) {
    		System.out.println("colName: " + column.get(0) + ", colType: " + column.get(1) + ", colSize: " + column.get(2));
    	}
    	for (List<String> row : inputRows) {
    		System.out.println("data: " + row);
    	}*/
    	
    	connectDatabase();

    	do {
    		if (dbInst.truncateTable(tableName) == false) {
    			System.out.println("DeviceTable@importData: truncate table failed.");
    			break;
    		}
    		List<List<String>> currColumns = dbInst.getColumns(tableName);

    		// Prepare all rows for insert once
    		String[][] insertRows = new String[inputRows.size()][currColumns.size()];	// row number comes from input, column number comes from existed table
    		for (int j = 0; j < currColumns.size(); j++) {
    			String colName = currColumns.get(j).get(0);
    			int posAtInput = -1;
    			for (int k = 0; k < inputColumns.size(); k++) {			// find out the same column from input settings
    				if (inputColumns.get(k).get(0).equals(colName)) {
    					posAtInput = k;
    					break;
    				}
    			}
    			if (posAtInput == -1) {
    				System.out.println("DeviceTable@importData: column " + colName + " is not found in input settings, empty string will be inserted as default.");
    				for (int i = 0; i < inputRows.size(); i++) {
    					insertRows[i][j] = "''";
    				}
    				continue;
    			}

    			for (int i = 0; i < inputRows.size(); i++) {						// data will be copied column by column
    				insertRows[i][j] = String.format("'%s'", inputRows.get(i).get(posAtInput));		// TODO care about the column type
    			}
    		}
    		JTools.print(insertRows, false);

    		if (dbInst.insert(tableName, insertRows) == false) {
    			System.out.println("DeviceTable@importData: insert data failed.");
    			break;
    		}
    		
    		result = true;
    	} while(false);
    	
    	disconnectDatabase();
    	
    	return result;
    }
}
