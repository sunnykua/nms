package com.via.database;

import java.util.ArrayList;
import java.util.List;

import com.via.model.*;

public class JDbLog {
    private JDatabase dbInst;
    private String tableName;
    //private String tableNameInQuotes;
    private final String[] tableDefinition = {
            "DATED   TIMESTAMP NOT NULL, ",
            "LEVEL   VARCHAR(10) NOT NULL, ",
            "LOGGER  VARCHAR(100) NOT NULL, ",
            "MESSAGE VARCHAR(200) NOT NULL "
    };
    
    public JDbLog(final JDatabase database, final String tableName) {
        this.dbInst = database;
        this.tableName = tableName;
        //this.tableNameInQuotes = "\"" + tableName + "\"";
    }
    
    public boolean isTableExisted() {
        return dbInst.isTableExisted(tableName);
    }
    
    public boolean createTable() {
        String definition = "";
        for (String s : tableDefinition) definition += s;

        return dbInst.createTable(tableName, definition);
    }

    public String getFirstTime(final String levelThreshold) {
    	List<String> filterExpressions = createLevelFilters(levelThreshold);
    	String[] result = dbInst.getFirst(tableName, "DATED", filterExpressions.toArray(new String[filterExpressions.size()]));
    	
    	return result != null ? result[0] : null;
    }
    
    public String getLastTime(final String levelThreshold) {
    	List<String> filterExpressions = createLevelFilters(levelThreshold);
    	String[] result = dbInst.getLast(tableName, "DATED", filterExpressions.toArray(new String[filterExpressions.size()]));
    	
    	return result != null ? result[0] : null;
    }
    
    public JLogTable getLogsByTime(final String levelThreshold, final String startTime, final String endTime) {
    	List<String> filterExpressions = createLevelFilters(levelThreshold);
    	
		//System.out.println("getLogsByDate - start: " + startTime + ", end: " + endTime);
		filterExpressions.add("DATED >= '" + startTime + "'");
		filterExpressions.add("DATED <= '" + endTime + "'");
		
		String[][] result = dbInst.getByColumn(tableName, filterExpressions.toArray(new String[filterExpressions.size()]));
		JLogTable logTable = new JLogTable();
		if (result != null) {
			for (String[] data : result) {
				logTable.addLog(data[0], data[1], data[3]);
			}
		}
		return logTable;
    }
    
    public JLogTable getLogs(final String levelThreshold, final int number) {
        List<String> filterExpressions = createLevelFilters(levelThreshold);

        String[][] result =  dbInst.getLast(tableName, "DATED", filterExpressions.toArray(new String[filterExpressions.size()]), number); // NOTE the output is in descent order
        JLogTable logTable = new JLogTable();
		if (result != null) {
			for (String[] data : result) {
				logTable.addLog(data[0], data[1], data[3]);
			}
		}
		return logTable;
    }
    
    private List<String> createLevelFilters(final String levelThreshold) {
    	List<String> filterExpressions = new ArrayList<String>();
        if (levelThreshold.equalsIgnoreCase("FATAL")) { // get only fatal
            filterExpressions.add("LEVEL != 'ERROR'"); // use the '!=' comparison is because derby.getLast is using AND to merge all expressions
            filterExpressions.add("LEVEL != 'WARN'");
            filterExpressions.add("LEVEL != 'INFO'");
            filterExpressions.add("LEVEL != 'DEBUG'");
        }
        else if (levelThreshold.equalsIgnoreCase("ERROR")) { // get error
            // and above
            filterExpressions.add("LEVEL != 'WARN'");
            filterExpressions.add("LEVEL != 'INFO'");
            filterExpressions.add("LEVEL != 'DEBUG'");
        }
        else if (levelThreshold.equalsIgnoreCase("WARN")) { // get warn
            // and above
            filterExpressions.add("LEVEL != 'INFO'");
            filterExpressions.add("LEVEL != 'DEBUG'");
        }
        else if (levelThreshold.equalsIgnoreCase("INFO")) { // get info
            // and above
            filterExpressions.add("LEVEL != 'DEBUG'");
        }
        return filterExpressions;
    }

	public String[][] getLast50Log() {
		String _tableName = "\"" + tableName + "\"";

		String query = String.format("SELECT DATED, MESSAGE FROM %s WHERE LEVEL = 'ERROR' ORDER BY DATED DESC FETCH FIRST 50 ROWS ONLY", _tableName);
		//System.out.println(query);

		return dbInst.getByQuery(query);
	}
}
