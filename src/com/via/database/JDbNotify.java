package com.via.database;


public class JDbNotify {
    private static String tableNameInQuotes;
    private static final String[] tableDefinition = {
    		"PHY_ADDR    VARCHAR(20), ",
    		"PUBLIC_IP   VARCHAR(20), ",
            "AP_JOIN_EMAIL_ENABLE    VARCHAR(10), ",
            "AP_JOIN_EMAIL_LIST VARCHAR(500), ",
            "AP_JOIN_SMS_ENABLE    VARCHAR(10), ",
            "AP_JOIN_SMS_LIST VARCHAR(500), ",
            "AP_LEAVE_EMAIL_ENABLE    VARCHAR(10), ",
            "AP_LEAVE_EMAIL_LIST VARCHAR(500), ",
            "AP_LEAVE_SMS_ENABLE    VARCHAR(10), ",
            "AP_LEAVE_SMS_LIST VARCHAR(500), ",
            "AC_COLDSTART_EMAIL_ENABLE    VARCHAR(10), ",
            "AC_COLDSTART_EMAIL_LIST VARCHAR(500), ",
            "AC_COLDSTART_SMS_ENABLE    VARCHAR(10), ",
            "AC_COLDSTART_SMS_LIST VARCHAR(500), ",
            "AC_WARMSTART_EMAIL_ENABLE    VARCHAR(10), ",
            "AC_WARMSTART_EMAIL_LIST VARCHAR(500), ",
            "AC_WARMSTART_SMS_ENABLE    VARCHAR(10), ",
            "AC_WARMSTART_SMS_LIST VARCHAR(500) "
    };
    
    public static boolean isTableExisted(final String tableName) {
        tableNameInQuotes = "\"" + tableName + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        boolean result =  database.isTableExisted(tableName);
        
		database.disconnect();
		return result;
    }
    
    public static boolean createTable(final String tableName) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String definition = "";
        for (String s : tableDefinition) definition += s;
        boolean result = database.createTable(tableName, definition);
       
		database.disconnect();
		return result;
    }
    
    public static boolean updateNotify(final String phyaddr,final String ip, 
    		final String apjoin_email_enable, final String apjoin_email_list,final String apjoin_sms_enable, final String apjoin_sms_list,
    		final String apleave_email_enable, final String apleave_email_list,final String apleave_sms_enable, final String apleave_sms_list,
       		final String acwarm_email_enable, final String acwarm_email_list, final String acwarm_sms_enable, final String acwarm_sms_list,
    		final String accold_email_enable, final String accold_email_list, final String accold_sms_enable, final String accold_sms_list
    		) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String query = String.format("UPDATE %s SET PUBLIC_IP = '%s', AP_JOIN_EMAIL_ENABLE = '%s', AP_JOIN_EMAIL_LIST = '%s', AP_JOIN_SMS_ENABLE = '%s', AP_JOIN_SMS_LIST = '%s', AP_LEAVE_EMAIL_ENABLE = '%s', AP_LEAVE_EMAIL_LIST = '%s', AP_LEAVE_SMS_ENABLE = '%s', AP_LEAVE_SMS_LIST = '%s', AC_WARMSTART_EMAIL_ENABLE = '%s', AC_WARMSTART_EMAIL_LIST = '%s', AC_WARMSTART_SMS_ENABLE = '%s', AC_WARMSTART_SMS_LIST = '%s', AC_COLDSTART_EMAIL_ENABLE = '%s', AC_COLDSTART_EMAIL_LIST = '%s', AC_COLDSTART_SMS_ENABLE = '%s', AC_COLDSTART_SMS_LIST = '%s' WHERE PHY_ADDR = '%s'",
                tableNameInQuotes, ip, 
                apjoin_email_enable, apjoin_email_list, apjoin_sms_enable, apjoin_sms_list,
                apleave_email_enable, apleave_email_list, apleave_sms_enable, apleave_sms_list,
                acwarm_email_enable, acwarm_email_list, acwarm_sms_enable, acwarm_sms_list,
                accold_email_enable, accold_email_list, accold_sms_enable, accold_sms_list,
                phyaddr
                );
        boolean result =   database.execute(query);
        
		database.disconnect();
		return result;
    }
    
    public static String[][] getNotify(final String phyaddr) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String query = String.format("SELECT AP_JOIN_EMAIL_ENABLE, AP_JOIN_EMAIL_LIST, AP_JOIN_SMS_ENABLE, AP_JOIN_SMS_LIST, AP_LEAVE_EMAIL_ENABLE, AP_LEAVE_EMAIL_LIST, AP_LEAVE_SMS_ENABLE, AP_LEAVE_SMS_LIST FROM %s WHERE PHY_ADDR='%s'", tableNameInQuotes, phyaddr);
        String[][] result = database.getByQuery(query);
        
		database.disconnect();
        
        if (result != null ) {
            return result; // join_mail_en,join_mail_list,join_sms_en,join_sms_list,leav_mail_en,leav_mail_list,leav_sms_en,leav_sms_list
        }
        else {
            return null;
        }

    }

    public static String[][] getNotifyAc(final String phyaddr) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String query = String.format("SELECT AC_WARMSTART_EMAIL_ENABLE, AC_WARMSTART_EMAIL_LIST, AC_WARMSTART_SMS_ENABLE, AC_WARMSTART_SMS_LIST, AC_COLDSTART_EMAIL_ENABLE, AC_COLDSTART_EMAIL_LIST, AC_COLDSTART_SMS_ENABLE, AC_COLDSTART_SMS_LIST FROM %s WHERE PHY_ADDR='%s'", tableNameInQuotes, phyaddr);
        String[][] result = database.getByQuery(query);
        
		database.disconnect();
        
        if (result != null ) {
            return result; // join_mail_en,join_mail_list,join_sms_en,join_sms_list,leav_mail_en,leav_mail_list,leav_sms_en,leav_sms_list
        }
        else {
            return null;
        }

    }
    
    public static boolean isNotifyExist(final String phyaddr) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String query = String.format("SELECT PHY_ADDR FROM %s WHERE PHY_ADDR='%s'", tableNameInQuotes, phyaddr);
        String[][] result = database.getByQuery(query);
        
		database.disconnect();
        
//		boolean result1;
        if (result != null) {        	
            return true; // join_mail_en,join_mail_list,join_sms_en,join_sms_list,leav_mail_en,leav_mail_list,leav_sms_en,leav_sms_list
        }
        else {
            return false;
        }

    }
    
    public static boolean addNotify(final String phyaddr, final String ip, 
    		final String apjoin_email_enable, final String apjoin_email_list,final String apjoin_sms_enable, final String apjoin_sms_list,
    		final String apleave_email_enable, final String apleave_email_list,final String apleave_sms_enable, final String apleave_sms_list,
    		final String acwarm_email_enable, final String acwarm_email_list, final String acwarm_sms_enable, final String acwarm_sms_list,
    		final String accold_email_enable, final String accold_email_list, final String accold_sms_enable, final String accold_sms_list
    		) {
        tableNameInQuotes = "\"" + "NOTIFY02" + "\"";
    	JDatabase database;
        database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        
        String query = String.format("INSERT INTO %s (PHY_ADDR, PUBLIC_IP, AP_JOIN_EMAIL_ENABLE, AP_JOIN_EMAIL_LIST, AP_JOIN_SMS_ENABLE, AP_JOIN_SMS_LIST, AP_LEAVE_EMAIL_ENABLE, AP_LEAVE_EMAIL_LIST, AP_LEAVE_SMS_ENABLE, AP_LEAVE_SMS_LIST, AC_WARMSTART_EMAIL_ENABLE, AC_WARMSTART_EMAIL_LIST, AC_WARMSTART_SMS_ENABLE, AC_WARMSTART_SMS_LIST, AC_COLDSTART_EMAIL_ENABLE, AC_COLDSTART_EMAIL_LIST, AC_COLDSTART_SMS_ENABLE, AC_COLDSTART_SMS_LIST) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                tableNameInQuotes, phyaddr, ip, apjoin_email_enable, apjoin_email_list, apjoin_sms_enable, apjoin_sms_list,
                apleave_email_enable, apleave_email_list, apleave_sms_enable, apleave_sms_list,
                acwarm_email_enable, acwarm_email_list, acwarm_sms_enable, acwarm_sms_list,
                accold_email_enable, accold_email_list, accold_sms_enable, accold_sms_list
                );
        boolean result = database.execute(query);
        
		database.disconnect();
		return result;

        
        
    }
    

}
