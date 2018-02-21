package com.via.database;

import java.sql.Timestamp;

public class JDbNmsLog {
    private JDatabase dbInst;
    private String tableName;

    private final String[] tableDefinition = {
    		"REC_TIME TIMESTAMP NOT NULL, ",
    		"SRC_ADDR VARCHAR(255) NOT NULL, ",
            "DATED    TIMESTAMP NOT NULL, ",
            "LEVEL    VARCHAR(10) NOT NULL, ",
            "MESSAGE  VARCHAR(200) NOT NULL "
    };
    
    public JDbNmsLog(final JDatabase database, final String tableName) {
        this.dbInst = database;
        this.tableName = tableName;
    }
    
    public boolean isTableExisted() {
        return dbInst.isTableExisted(tableName);
    }
    
    public boolean createTable() {
        String definition = "";
        for (String s : tableDefinition) definition += s;

        return dbInst.createTable(tableName, definition);
    }

	public boolean dropTable() {
		return dbInst.deleteTable(tableName);
	}

	public boolean insert(final String[] value) {
		return dbInst.insert(tableName, value);
	}

	public boolean insert(final String SRC_ADDR, final Timestamp DATED, final String LEVEL, final String MESSAGE) {
		String[] value = { "\'" + new java.sql.Timestamp(System.currentTimeMillis()) + "\'", "\'" + SRC_ADDR + "\'", "\'" + DATED + "\'", "\'" + LEVEL + "\'", "\'" + MESSAGE + "\'" };
		
		return dbInst.insert(tableName, value);
	}

	public String[][] getNmsLast50Log(String SRC_ADDR) {
		String _tableName = "\"" + tableName + "\"";
		SRC_ADDR = "SRC_ADDR = '" + SRC_ADDR + "'";

		String query = String.format("SELECT DATED, MESSAGE FROM %s WHERE %s AND LEVEL = 'ERROR' ORDER BY DATED DESC FETCH FIRST 50 ROWS ONLY", _tableName, SRC_ADDR);
		//System.out.println(query);

		return dbInst.getByQuery(query);
	}
}
