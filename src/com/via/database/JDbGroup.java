package com.via.database;


import java.util.ArrayList;
import java.util.List;

import com.via.model.JDevice;
import com.via.model.JGroup;

public class JDbGroup {
    private JDatabase dbInst;
    private String tableName;
    private String tableNameInQuotes;
    private final String[] tableDefinition = {
    		"ID            INT NOT NULL GENERATED ALWAYS AS IDENTITY, ",
            "NAME          VARCHAR(25) UNIQUE NOT NULL, ",
            "PARENT        VARCHAR(25), ",
            "IS_ROOT       VARCHAR(5), ",
            "ALIVE_STATUS  VARCHAR(5) ",
    };
    private int a = 0;
    private final int iId = a++, iNAME = a++, iPARENT = a++, iIS_ROOT = a++, iALIVE_STATUS = a++;
    
    public JDbGroup(final JDatabase database, final String tableName) {
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

    public boolean addGroupDevice(final JGroup group) {
        if (group == null) return false;

        String groupName = group.getName();
        String parent = group.getParent();
        String isRoot = String.format("%s", group.isRoot());
        String aliveStatus = "1";
        
        String children = "";
        
        if(group.getChildren() != null){
	        children = group.getChildren().size() > 0 ? group.getChildren().get(0) : "";
	        for (int i = 1; i < group.getChildren().size(); i++)
	        	children += "," + group.getChildren().get(i);
	        children = String.format("%s", children);
        }
        else {
        	children = String.format("%s", children);
        }
        
        String query = String.format("INSERT INTO %s (NAME, PARENT, IS_ROOT, ALIVE_STATUS) VALUES('%s', '%s', '%s', '%s')",
        		tableNameInQuotes, groupName, parent, isRoot, aliveStatus);
        
        return dbInst.execute(query);
    }
    
    public boolean removeGroup(String group) {
        if (group == null) return false;
        String filterColumnName = String.format("%s", "NAME"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", group);

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }

    public List<JGroup> getAllDevice() {
        List<JGroup> groupList = new ArrayList<JGroup>();

        String[][] deviceArray = dbInst.getAll(tableName);
        
        if (deviceArray == null) return groupList;
        
        ArrayList<String> child = new ArrayList<String>();
        for (String[] data : deviceArray) {
            if (data.length != tableDefinition.length) continue;

            
            JGroup group = new JGroup();
            group.setId(data[iId]);
            group.setName(data[iNAME]);
            group.setParent(data[iPARENT]);
            group.setChildren(child);
            
            group.setRoot(data[iIS_ROOT].equals("true"));
            group.setAliveStatus(Integer.parseInt(data[iALIVE_STATUS]));
            groupList.add(group);
        }

        return groupList;
    }
    
    public boolean isNameExisted(final String groupName) {
        String query = String.format("SELECT NAME FROM %s WHERE NAME='%s'", tableNameInQuotes, groupName);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equals(groupName)) {
            //System.out.println("Name is existed.");
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean updateName(final String groupName, final String bgroupName) {
        String query = String.format("UPDATE %s SET NAME = '%s' WHERE NAME = '%s'", tableNameInQuotes, groupName, bgroupName);

        return dbInst.execute(query);
    }
    
    public boolean updateParent(final String groupName, final String parent) {
        String query = String.format("UPDATE %s SET PARENT = '%s' WHERE NAME = '%s'", tableNameInQuotes, parent, groupName);

        return dbInst.execute(query);
    }
    
    public boolean isRootExisted(final String groupName, final String root) {
        String query = String.format("SELECT NAME FROM %s WHERE IS_ROOT = 'true'", tableNameInQuotes);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equals(groupName)) {
            //System.out.println("Name is existed.");
            return false;
        }
        else {
        	String queryRoot = String.format("SELECT IS_ROOT FROM %s WHERE IS_ROOT = 'true'", tableNameInQuotes);
        	String[][] rootResult = dbInst.getByQuery(queryRoot);
        	if(rootResult != null && rootResult[0][0].equals("true")){
        		return true;
        	}else {
        		return false;
        	}
        }
    }
    
    public boolean updateRoot(final String groupName, final String root) {
        String query = String.format("UPDATE %s SET IS_ROOT = '%s' WHERE NAME = '%s'", tableNameInQuotes, root, groupName);

        return dbInst.execute(query);
    }
    
    public boolean updateAlarmStatus(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "ALIVE_STATUS", String.valueOf(device.isAlive()), "NAME", device.getGroupName());
    }
    
    public boolean updateAlarmStatusFalse(final String groupName) {
        if (groupName == null) return false;

        return dbInst.update(tableName, "ALIVE_STATUS", "0", "NAME", groupName);
    }
    
}
