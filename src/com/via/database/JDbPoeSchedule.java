package com.via.database;

import java.util.ArrayList;
import java.util.List;

import com.via.model.JPoeSchedule;


public class JDbPoeSchedule {
    private JDatabase dbInst;
    private String tableName;
    private String tableNameInQuotes;
    private final String[] tableDefinition = {
            "TIME_RANGE_NAME    VARCHAR(30), ",
            "START_TIME         VARCHAR(30), ",
            "END_TIME           VARCHAR(30) "
    };
    
    private int a = 0;
    private final int iTIME_RANGE_NAME = a++, iSTART_TIME = a++, iEND_TIME = a++;

    public JDbPoeSchedule(final JDatabase database, final String tableName) {
        this.dbInst = database;
        this.tableName = tableName;
        this.tableNameInQuotes = "\"" + tableName + "\"";
    }

    public boolean isTableExisted() {
        return dbInst.isTableExisted(tableName);
    }
    
    public boolean createTable() {
        String definition = "";
        for (String s : tableDefinition) definition += s;

        return dbInst.createTable(tableName, definition);
    }

    public boolean isPoeScheduleExisted(final String schedule_name) {
        String query = String.format("SELECT TIME_RANGE_NAME FROM %s WHERE TIME_RANGE_NAME='%s'", tableNameInQuotes, schedule_name);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equals(schedule_name)) {
            System.out.println("Account is existed.");
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addPoeSchedule(final String schedule_name, final String poeStartTime, final String poeEndTime) {
        String query = String.format("INSERT INTO %s (TIME_RANGE_NAME, START_TIME, END_TIME) VALUES('%s', '%s', '%s')",
                tableNameInQuotes, schedule_name, poeStartTime, poeEndTime);

        return dbInst.execute(query);
    }

    public String[][] getviewPoeSchedule() {
        String query = String.format("SELECT TIME_RANGE_NAME, START_TIME, END_TIME FROM %s ", tableNameInQuotes);

        String[][] result = dbInst.getByQuery(query);

        return result; // TIME_RANGE_NAME, START_TIME, END_TIME
    }
    
    public boolean removePoeScheduleItems(String poeItem) {
        if (poeItem == null) return false;
        String filterColumnName = String.format("%s", "TIME_RANGE_NAME"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", poeItem);

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }
    
    public List<JPoeSchedule> getPoeItems() {
        List<JPoeSchedule> poeList = new ArrayList<JPoeSchedule>();
        
        String query = String.format("SELECT TIME_RANGE_NAME, START_TIME, END_TIME FROM %s ", tableNameInQuotes);
        String[][] result = dbInst.getByQuery(query);
        
        if(result!=null){
	        for (String[] data : result) {
	        	JPoeSchedule poe = createPoeItems(data);
	            if (poe != null) poeList.add(poe);
	        }
	        return poeList;
        }
        
        return null;
    }
    
    private JPoeSchedule createPoeItems(String[] data) {
        if (data.length != tableDefinition.length) {
            System.out.println("data length is wrong when create poe items.");
            return null;
        }
        
        JPoeSchedule poeItems = new JPoeSchedule();
        poeItems.setPoeScheduleName(data[iTIME_RANGE_NAME]);
        poeItems.setStartTime(data[iSTART_TIME]);
        poeItems.setEndTime(data[iEND_TIME]);
        
        return poeItems;
    }

}
