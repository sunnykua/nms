package com.via.database;

import java.util.ArrayList;
import java.util.List;

import com.via.model.JAccount;

public class JDbAccount {
    private JDatabase dbInst;
    private String tableName;
    private String tableNameInQuotes;
    private final String[] tableDefinition = {
            "USERNAME      VARCHAR(80) NOT NULL PRIMARY KEY, ",
            "PASSWORD      VARCHAR(100), ",
            "LEVEL         VARCHAR(20), ",
            "NAME          VARCHAR(50), ",
            "EMAIL         VARCHAR(100), ",
            "PHONE_NUMBER  VARCHAR(100), ",
            "LOGIN_DATE    TIMESTAMP, ",
            "LEAVE_DATE    TIMESTAMP, ",
            "SESSIONID     VARCHAR(100), ",
            "IS_MANAGE     VARCHAR(5), ",
            "IS_REMOTE     VARCHAR(5) ", 
    };
    
    private int a = 0;
    private final int iUSERNAME = a++, iNAME = a++, iEMAIL = a++, iPHONE_NUMBER = a++, iIS_MANAGE = a++, iIS_REMOTE = a++;

    public JDbAccount(final JDatabase database, final String tableName) {
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

    public String[] getAccountInfo(final String username) {
        String query = String.format("SELECT USERNAME, PASSWORD, LEVEL, IS_REMOTE, IS_MANAGE FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);

        String[][] result = dbInst.getByQuery(query);
        
        if (result == null){
        	return null;
        }

        if (result[0] != null) {
            return result[0];
        }

        return null;
    }

    public String[] getAccountView(final String username) {
        String query = String.format("SELECT * FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);

        String[][] result = dbInst.getByQuery(query);

        if (result[0] != null) {
            return new String[] { result[0][3], result[0][4], result[0][5] }; // NAME,EMAIL,PHONE_NUMBER
        }

        return null;
    }

    public boolean updateLoginDate(final String username, final String loginDate) {
        String query = String.format("UPDATE %s SET LOGIN_DATE = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, loginDate, username);

        return dbInst.execute(query);
    }

    public boolean updateLogoutDate(final String username, final String loginDate) {
        String query = String.format("UPDATE %s SET LEAVE_DATE = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, loginDate, username);

        return dbInst.execute(query);
    }
    
    public boolean updateSessionId(final String username, final String session_id) {
        String query = String.format("UPDATE %s SET SESSIONID = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, session_id, username);

        return dbInst.execute(query);
    }
    
    public String[] getSessionId(final String username) {
        String query = String.format("SELECT SESSIONID FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);
        
        String[][] result = dbInst.getByQuery(query);

        if (result[0] != null) {
            return result[0];
        }

        return null;
    }

    public boolean updatePassword(final String username, final String password) {
        String query = String.format("UPDATE %s SET PASSWORD = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, password, username);

        return dbInst.execute(query);
    }

    public boolean updateName(final String username, final String name) {
        String query = String.format("UPDATE %s SET NAME = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, name, username);

        return dbInst.execute(query);
    }

    public boolean updateEmail(final String username, final String email) {
        String query = String.format("UPDATE %s SET EMAIL = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, email, username);

        return dbInst.execute(query);
    }
    
    public boolean updatePhoneNumber(final String username, final String phoneNumber) {
        String query = String.format("UPDATE %s SET PHONE_NUMBER = '%s' WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, phoneNumber, username);

        return dbInst.execute(query);
    }

    public boolean pwdRecovery(final String newpwd, final String email) {
        String query = String.format("UPDATE %s SET PASSWORD = '%s' WHERE EMAIL = '%s'", tableNameInQuotes, newpwd, email);

        return dbInst.execute(query);
    }

    public boolean isAccountExisted(final String username) {
        String query = String.format("SELECT USERNAME FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equalsIgnoreCase(username)) {
            System.out.println("Account is existed.");
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) {
        String query = String.format("INSERT INTO %s (USERNAME, PASSWORD, LEVEL, NAME, EMAIL, PHONE_NUMBER, IS_MANAGE, IS_REMOTE) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                tableNameInQuotes, username, password, level, name, email, phone_number, is_manage, is_remote);

        return dbInst.execute(query);
    }
    
    public String getAlarmEmail(final String username) {
        String query = String.format("SELECT EMAIL FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);

        String[][] result = dbInst.getByQuery(query);

        if (result == null) {
            
            return username;
        }
        else {
        	return result[0][0]; //EMAIL
        }
    }
    
    public String getAlarmSms(final String username) {
        String query = String.format("SELECT PHONE_NUMBER FROM %s WHERE UPPER(USERNAME) LIKE UPPER('%s')", tableNameInQuotes, username);

        String[][] result = dbInst.getByQuery(query);

        if (result == null) {
            
            return "0900000000";
        }
        else {
        	return result[0][0]; //PHONE_NUMBER
        }
    }

    public String[][] getViewAllUserAccount(Object userCheck) {
    	if(userCheck.equals(0)){
	        String query = String.format("SELECT USERNAME, LEVEL, NAME, EMAIL, PHONE_NUMBER, LOGIN_DATE, LEAVE_DATE FROM %s ", tableNameInQuotes);
	
	        String[][] result = dbInst.getByQuery(query);
	
	        if (result[0] != null && result[0].length == 7) {
	            return result; // USERNAME, LEVEL, NAME, EMAIL, PHONE_NUMBER, LOGIN_DATE, LEAVE_DATE
	        }
	        else {
	            return null;
	        }
    	}
    	else{
    		String query = String.format("SELECT USERNAME, LEVEL, NAME, EMAIL, PHONE_NUMBER, LOGIN_DATE, LEAVE_DATE FROM %s WHERE (LEVEL ='admin' OR LEVEL ='user') AND IS_REMOTE = 'false' ", tableNameInQuotes);
    	
	        String[][] result = dbInst.getByQuery(query);
	
	        if (result[0] != null && result[0].length == 7) {
	            return result; // USERNAME, LEVEL, NAME, EMAIL, PHONE_NUMBER, LOGIN_DATE, LEAVE_DATE
	        }
	        else {
	            return null;
	        }
    	}
    }
    
    public boolean removeAccount(String accountItem) {
        if (accountItem == null) return false;
        String filterColumnName = String.format("%s", "USERNAME"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", accountItem);

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }

    public boolean getAccountRecovery(final String email) {
        String query = String.format("SELECT EMAIL FROM %s WHERE EMAIL='%s'", tableNameInQuotes, email);

        String[][] result = dbInst.getByQuery(query);

        if (result != null && result[0][0].equals(email)) {
            System.out.println("email is existed.");
            return true;
        }
        else {
            return false;
        }
    }
    
    public List<JAccount> getAccountItems() {
        List<JAccount> accountList = new ArrayList<JAccount>();
        
        String query = String.format("SELECT USERNAME, NAME, EMAIL, PHONE_NUMBER, IS_MANAGE, IS_REMOTE FROM %s WHERE (LEVEL ='admin' OR LEVEL ='user') AND IS_REMOTE = 'false' ", tableNameInQuotes);
        String[][] result = dbInst.getByQuery(query);
        
        if(result!=null){
	        for (String[] data : result) {
	        	JAccount account = createAccountItems(data);
	            if (account != null) accountList.add(account);
	        }
	        return accountList;
        }
        
        return null;
    }
    
    public List<JAccount> getAccountAllItems() {
        List<JAccount> accountList = new ArrayList<JAccount>();
        
        String query = String.format("SELECT USERNAME, NAME, EMAIL, PHONE_NUMBER, IS_MANAGE, IS_REMOTE FROM %s ", tableNameInQuotes);
        String[][] result = dbInst.getByQuery(query);
        
        if(result!=null){
	        for (String[] data : result) {
	        	JAccount account = createAccountItems(data);
	            if (account != null) accountList.add(account);
	        }
	        return accountList;
        }
        
        return null;
    }
    
    public List<JAccount> getRemoteAccountSetItems() {
        List<JAccount> accountList = new ArrayList<JAccount>();
        
        String query = String.format("SELECT USERNAME, NAME, EMAIL, PHONE_NUMBER, IS_MANAGE, IS_REMOTE FROM %s WHERE IS_MANAGE = 'false' AND IS_REMOTE = 'false' ", tableNameInQuotes);
        String[][] result = dbInst.getByQuery(query);
        
        if(result!=null){
	        for (String[] data : result) {
	        	JAccount account = createAccountItems(data);
	            if (account != null) accountList.add(account);
	        }
	        return accountList;
        }
        
        return null;
    }
    
    private JAccount createAccountItems(String[] data) {
        /*if (data.length != 5) {
            System.out.println("data length is wrong when create account.");
            return null;
        }*/
        
        JAccount accountItem = new JAccount();
        accountItem.setUserName(data[iUSERNAME]);
        accountItem.setName(data[iNAME]);
        accountItem.setEmail(data[iEMAIL]);
        accountItem.setPhoneNumber(data[iPHONE_NUMBER]);
        accountItem.setRemote(data[iIS_MANAGE].equals("true"));
        accountItem.setRemote(data[iIS_REMOTE].equals("true"));
        
        return accountItem;
    }

}
