package com.via.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum DbType {
	Derby
}

public class JDatabase {
	public static final DbType Derby = DbType.Derby;

	private DbType dbType;
	private JDerby derbyInst = null;
	private DerbyType derbyType;

	public JDatabase(final String url, final String username, final String password) {
		if (url.startsWith("jdbc:derby")) {
			this.dbType = Derby;
			this.derbyType = url.startsWith("//", 11) ? JDerby.ClientServer : JDerby.Embedded;
			this.derbyInst = new JDerby(derbyType, url, username, password);
		}
		/*else if (url.startsWith("jdbc:mysql")) {
		}*/
		else {
			System.out.println("[ERROR] @JDatabase(): Unknown database protocol.");
			//throw new Exception("Unknown database protocol."); 
		}
	}

	public String getDbType() {
		String name;
		if (dbType == DbType.Derby)
			name = "Derby";
		else
			name = "Unknown";

		return name;
	}

	public boolean isConnected() {
		return derbyInst != null && derbyInst.isConnected() ? true : false;
	}

	public boolean connect() {
		boolean result = false;
		
		if (dbType == DbType.Derby) {
			result = derbyInst.open();
		}
		
		return result;
	}
	
	public void disconnect() {
	    if (dbType == DbType.Derby) {
	        derbyInst.close();
	    }
	}
	
	public boolean isSchemaExisted(final String schemaName) {
		if (dbType == DbType.Derby) {
			return derbyInst.findSchema(schemaName);
		}

		return false;
	}

	public boolean createSchema(final String schemaName) {
		String query;

		if (dbType == DbType.Derby) {
			query = String.format("CREATE SCHEMA %s", schemaName);

			return derbyInst.execute(query);
		}

		return false;
	}

	/**
	 * Because isExisted() function is not using sql, so it won't insert table name into quotes.
	 * @param tableName
	 * @return
	 */
	public boolean isTableExisted(final String tableName) {
		if (dbType == DbType.Derby) {
			return derbyInst.findTable(tableName);
		}

		return false;
	}

	public boolean createTable(final String tableName, final String tableDefinition) {
        String query;

        if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";
            query = String.format("CREATE TABLE %s ( %s )", _tableName, tableDefinition);

            return derbyInst.execute(query);
        }

        return false;
    }
	
	public boolean deleteTable(final String tableName) {
		String query;

		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";

			query = "DROP TABLE " + _tableName;

			return derbyInst.execute(query);
		}

		return false;
	}
	
	public boolean truncateTable(final String tableName) {
		String query;

		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";

			query = "TRUNCATE TABLE " + _tableName;

			return derbyInst.execute(query);
		}

		return false;
	}

	public boolean insert(final String tableName, final String[] value) {
		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";

			return derbyInst.insertOne(_tableName, value);
		}

		return false;
	}

	public boolean insert(final String tableName, final String[][] values) {
		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";

			return derbyInst.insertMany(_tableName, values);
		}

		return false;
	}

	public final String[] getFirst(final String tableName) {
		if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getFirst(_tableName);
        }

        return null;
	}
	
	public final String[] getFirst(final String tableName, final String orderColumnName) {
		if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getFirst(_tableName, orderColumnName);
        }

        return null;
	}
	
	public final String[] getFirst(final String tableName, final String orderColumnName, final String[] filterExpressions) {
	    if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getFirst(_tableName, orderColumnName, filterExpressions);
        }

        return null;
	}
	
	public final String[] getFirst(final String tableName, final String orderColumnName, final String filterColumnName, final String filterColumnValue) {
        if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getFirst(_tableName, orderColumnName, filterColumnName, filterColumnValue);
        }

        return null;
    }

	public final String[] getLast(final String tableName, final String orderColumnName, final String filterColumnName, final String filterColumnValue) {
        if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getLast(_tableName, orderColumnName, filterColumnName, filterColumnValue);
        }

        return null;
    }
	
	public final String[] getLast(final String tableName, final String orderColumnName, final String[] filterExpressions) {
	    
	    String[][] result = getLast(tableName, orderColumnName, filterExpressions, 1);
	    
	    return result != null ? result[0] : null;
	}
	
	public final String[][] getLast(final String tableName, final String orderColumnName, final String[] filterExpressions, final int rowCount) {
	    if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";

            return derbyInst.getLast(_tableName, orderColumnName, filterExpressions, rowCount);
        }

        return null;
	}

	public final String[][] getByColumn(final String tableName, final String filterColumnName, final String filterColumnValue) {
	    if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";
	        
	        return derbyInst.getByColumn(_tableName, filterColumnName, filterColumnValue);
	    }
	    
	    return null;
	}
	
	public final String[][] getByColumn(final String tableName, final String[] filterExpressions) {
        if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";
            
            return derbyInst.getByColumn(_tableName, filterExpressions);
        }
        
        return null;
    }
	
	public String[][] getByQuery(final String query) {
	    if (dbType == DbType.Derby) {

            return derbyInst.getByQuery(query);
        }

        return null;
	}
	
	public String[][] getAll(final String tableName) {
	    if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";

            return derbyInst.getAll(_tableName);
        }

        return null;
	}
	
	public int getRowCount(final String tableName) {
		if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";
	        
	        return derbyInst.getRowCount(_tableName);
	    }
	    
	    return -1;
	}
	
	public int getRowCount(final String tableName, final String[] filterExpressions) {
	    if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";
	        
	        return derbyInst.getRowCount(_tableName, filterExpressions);
	    }
	    
	    return -1;
	}
	
	public boolean deleteRow(final String tableName, final String filterColumnName, final String filterColumnValue) {
	    if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";

            return derbyInst.deleteRow(_tableName, filterColumnName, filterColumnValue);
        }

        return false;
	}
	
	public boolean deleteRow(final String tableName, final String filterColumnName, final int filterColumnValue) {
		if (dbType == DbType.Derby) {
	        String _tableName = "\"" + tableName + "\"";

            return derbyInst.deleteRow(_tableName, filterColumnName, filterColumnValue);
        }

        return false;
	}
	
	public boolean update(final String tableName, final String columnName, final String columnValue, final String filterColumnName, final String filterColumnValue) {
	    if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";
            String _columnValue = String.format("'%s'", columnValue);
            String _filterColumnValue = String.format("'%s'", filterColumnValue);		// value should be included in quotes if type is char

            return derbyInst.update(_tableName, columnName, _columnValue, filterColumnName, _filterColumnValue);
        }

        return false;
	}
	
	public boolean update(final String tableName, final String columnName, final String columnValue, final String filterColumnName, final int filterColumnValue) {
	    if (dbType == DbType.Derby) {
            String _tableName = "\"" + tableName + "\"";
            String _columnValue = String.format("'%s'", columnValue);
            String _filterColumnValue = String.valueOf(filterColumnValue);

            return derbyInst.update(_tableName, columnName, _columnValue, filterColumnName, _filterColumnValue);
        }

        return false;
	}

	public boolean execute(final String query) {
	    if (dbType == DbType.Derby) {

            return derbyInst.execute(query);
        }

        return false;
	}
	
	// ====================================================================================================

	public TableData getTable(final String tableName) {
		TableData table = new TableData();

		if (dbType == DbType.Derby) {
			table.setName(tableName);
			String _tableName = "\"" + tableName + "\"";

			String[][] columns = derbyInst.getHead(_tableName);
			String[][] rows = derbyInst.getAll(_tableName);
			
			if (columns != null && rows != null) {
				List<List<String>> columnList = new ArrayList<List<String>>();
				for (String[] col : columns) {
					columnList.add(Arrays.asList(col));
				}
				table.setColumns(columnList);
				
				List<List<String>> rowList = new ArrayList<List<String>>();
				for (String[] row : rows) {
					rowList.add(Arrays.asList(row));
				}
				table.setRows(rowList);
			}
			else {
				table.setColumns(new ArrayList<List<String>>());
				table.setRows(new ArrayList<List<String>>());
			}
		}

		return table;
	}
	
	public List<List<String>> getColumns(final String tableName) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";
			
			String[][] columns = derbyInst.getHead(_tableName);
			if (columns != null) {
				for (String[] column : columns) {
					result.add(Arrays.asList(column));
				}
			}
		}
		
		return result;
	}
	
	public List<List<String>> getRows(final String tableName) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";
			String[][] dataRows = derbyInst.getAll(_tableName);
			if (dataRows != null) {
				for (String[] row : dataRows) {
					result.add(Arrays.asList(row));
				}
			}
		}
		
		return result;
	}
	
	// ====================================================================================================
	
	public TableData getDeviceListTable(final String tableName) {
		TableData table = new TableData();

		if (dbType == DbType.Derby) {
			String _tableName = "\"" + tableName + "\"";
			
			String SelectString = "PUBLIC_IP,ALIAS_NAME,SNMP_PORT,SNMP_VERSION,READ_COMMUNITY";
			
			String query = String.format("SELECT %s FROM %s", SelectString, _tableName);
			//System.out.println(query);
			
			String[][] rows = derbyInst.getByQuery(query);
			
			if (rows != null) {
				List<List<String>> rowList = new ArrayList<List<String>>();
				for (String[] row : rows) {
					rowList.add(Arrays.asList(row));
				}
				table.setRows(rowList);
			}
			else {
				table.setRows(new ArrayList<List<String>>());
			}
		}

		return table;
	}
}
