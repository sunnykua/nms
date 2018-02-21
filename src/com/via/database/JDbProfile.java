package com.via.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.tribes.util.Arrays;

import com.via.model.JPoeSchedule;
import com.via.model.JProfile;


public class JDbProfile {
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

    public JDbProfile(final JDatabase database, final String tableName) {
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

    public boolean isProfileExisted(final String profile_name) {
        String query = String.format("SELECT TIME_RANGE_NAME FROM %s WHERE TIME_RANGE_NAME='%s'", tableNameInQuotes, profile_name);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equals(profile_name)) {
            System.out.println("Profile is existed.");
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addProfile(final String profile_name, final String profileStartTime, final String profileEndTime) {
        String query = String.format("INSERT INTO %s (TIME_RANGE_NAME, START_TIME, END_TIME) VALUES('%s', '%s', '%s')",
                tableNameInQuotes, profile_name, profileStartTime, profileEndTime);

        return dbInst.execute(query);
    }

    public String[][] getViewProfile() {
        String query = String.format("SELECT TIME_RANGE_NAME, START_TIME, END_TIME FROM %s ", tableNameInQuotes);

        String[][] result = dbInst.getByQuery(query);

        return result; // TIME_RANGE_NAME, START_TIME, END_TIME
    }
    
    public boolean removeProfileItems(String item) {
        if (item == null) return false;
        String filterColumnName = String.format("%s", "TIME_RANGE_NAME"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", item);

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }
    
    public List<JProfile> getProfileItems() {
        List<JProfile> list = new ArrayList<JProfile>();
        
        String query = String.format("SELECT TIME_RANGE_NAME, START_TIME, END_TIME FROM %s ", tableNameInQuotes);
        String[][] result = dbInst.getByQuery(query);
        
        if(result!=null){
	        for (String[] data : result) {
	        	JProfile profile = createProfileItems(data);
	            if (profile != null) list.add(profile);
	        }
	        return list;
        }
        
        return null;
    }
    
    private JProfile createProfileItems(String[] data) {
        if (data.length != tableDefinition.length) {
            System.out.println("data length is wrong when create poe items.");
            return null;
        }
        
        JProfile item = new JProfile();
        item.setProfileName(data[iTIME_RANGE_NAME]);
        item.setStartTime(data[iSTART_TIME]);
        item.setEndTime(data[iEND_TIME]);
        
        return item;
    }

}
