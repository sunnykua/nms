package com.via.database;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigInteger;

import com.via.model.JDevice;
import com.via.model.JDeviceReport;
import com.via.model.JInterface;
import com.via.model.JInterfaceTerminalReport;
import com.via.model.JTools;

public class JDbStatistics {
    private JDatabase dbInst;
    private String tableName;
    private String tableNameInQuotes;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final BigInteger maxInt32 = new BigInteger("4294967296");               // 2 ^ 32
    private final BigInteger maxInt64 = new BigInteger("18446744073709551616");     // 2 ^ 64
    private final String[] tableDefinition = {
    		"ID INT GENERATED BY DEFAULT AS IDENTITY, ",
            "PUBLIC_IP      VARCHAR(20) NOT NULL, ",
            "PHY_ADDR       VARCHAR(20), ",
            "IF_INDEX       VARCHAR(10) NOT NULL, ",
            "REC_TIME       TIMESTAMP NOT NULL, ",
            "OCT_RX_TOT     VARCHAR(20), ",
            "OCT_TX_TOT     VARCHAR(20) "
    };
    private final int iREC_TIME = 4;
    
    public JDbStatistics(final JDatabase database, final String tableName) {
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

    public boolean add(final String[] value) {
        return dbInst.insert(tableName, value);
    }
    
    public boolean add(final String[][] values) {
        return dbInst.insert(tableName, values);
    }
    
    public String[] getDeviceReportTime() {
        String[] firstRecord = getFirstReport();
        
        if (firstRecord != null) {
            return new String[] { firstRecord[iREC_TIME]};
        }
        
        return null;
    }
    
    public String[] getFirstReport() {
    	JDatabase db = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
    	String[] result = db.getFirst(tableName);
    	db.disconnect();
        return result;
    }
    
    public String[] getFirstByInterface(final String ipAddr, final String ifIndex) {
        String ipAddrExpression = "PUBLIC_IP = '" + ipAddr + "'";
        String ifIndexExpression = "IF_INDEX = '" + ifIndex + "'";
        return dbInst.getFirst(tableName, "ID", new String[]{ ipAddrExpression, ifIndexExpression});
    }
    
    public String[] getLastByInterface(final String ipAddr, final String ifIndex) {
        String ipAddrExpression = "PUBLIC_IP = '" + ipAddr + "'";
        String ifIndexExpression = "IF_INDEX = '" + ifIndex + "'";
        return dbInst.getLast(tableName, "ID", new String[]{ ipAddrExpression, ifIndexExpression});
    }
    
    public String[] getBoundaryTimeByInterface(final String ipAddr, final String ifIndex) {
        String[] firstRecord = getFirstByInterface(ipAddr, ifIndex);
        String[] lastRecord = getLastByInterface(ipAddr, ifIndex);
        
        if (firstRecord != null && lastRecord != null) {
            return new String[] { firstRecord[iREC_TIME], lastRecord[iREC_TIME] };
        }
        
        return null;
    }

    /**
     * Return a set of array that contents timing and data rate of Rx/Tx octets.
     * 
     */
    public String[][] getRxTxOctetRateHistory(final String ipAddr, final String ifIndex, final String startTime, final String endTime, final boolean isCounter64) {
        String[] selectedColumns = { "OCT_RX_TOT", "OCT_TX_TOT" };

        return getHistoryInRate(ipAddr, ifIndex, startTime, endTime, selectedColumns, isCounter64);
    }

    @SuppressWarnings("unused")
	private String[][] getHistoryByColumns(
            final String ipAddr,
            final String ifIndex,
            final String startTime,
            final String endTime,
            final String[] selectedColumns,
            final int pointNumber,
            final boolean getTheRate) {
        if (selectedColumns == null || selectedColumns.length < 1) {
            System.out.println("Selected columns should be at least 1. Exit()");
            return null;
        }
        if (pointNumber < 3) {
            System.out.println("Point number should not less than 3. Exit()");
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        String baseStartTime = "2000-01-01 00:00:00.000"; // use this time as the far past time
        String baseEndTime = sdf.format(calendar.getTime()); // use current time as the latest time
        String ipAddrExpression = "PUBLIC_IP = '" + ipAddr + "'";
        String ifIndexExpression = "IF_INDEX = '" + ifIndex + "'";
        String startTimeExpression = "REC_TIME >= '" + (startTime != null && !startTime.isEmpty() ? startTime : baseStartTime) + "'";
        String endTimeExpression = "REC_TIME <= '" + (endTime != null && !endTime.isEmpty() ? endTime : baseEndTime) + "'";

        double timeForBoundary = System.currentTimeMillis();
        String[] firstRecord = dbInst.getFirst(tableName, "ID", new String[] { ipAddrExpression, ifIndexExpression, startTimeExpression });
        String[] lastRecord = dbInst.getLast(tableName, "ID", new String[] { ipAddrExpression, ifIndexExpression, endTimeExpression });
        int totalRowCont = dbInst.getRowCount(tableName, new String[] { ipAddrExpression, ifIndexExpression, startTimeExpression, endTimeExpression });
        System.out.println("firstRecord: " + (firstRecord != null && firstRecord.length != 0 ? firstRecord[1] : "NONE") + ", exp: " + startTimeExpression);
        System.out.println("lastRecord: " + (lastRecord != null && lastRecord.length != 0 ? lastRecord[1] : "NONE") + ", exp: " + endTimeExpression);
        System.out.println("total rows read from db: " + totalRowCont);
        System.out.println("getByColumn boundary use " + (System.currentTimeMillis() - timeForBoundary) / 1000 + " sec.");
        // Process the output when total records is not enough
        if (totalRowCont < pointNumber) {
            //System.out.println(String.format("There are %d records in database, but less than required(%d).", totalRowCont, pointNumber));
            double timeForLessPoint = System.currentTimeMillis();
            String selectedString = "REC_TIME, " + selectedColumns[0];
            for (int i = 1; i < selectedColumns.length; i++) {
                selectedString += ", " + selectedColumns[i];
            }
            String query = String.format("SELECT %s FROM %s WHERE %s AND %s AND %s AND %s",
            		selectedString, tableNameInQuotes, ipAddrExpression, ifIndexExpression, startTimeExpression, endTimeExpression);
            String[][] result = dbInst.getByQuery(query);
            //System.out.println("Result data read from database:");
            //JTools.print(result, true);
            if (result != null && result.length == totalRowCont && result[0].length == (selectedColumns.length + 1)) { // add rec_time
                String[][] output = new String[totalRowCont][selectedColumns.length + 1];

                if (getTheRate) {
                    output[0][0] = result[0][0];
                    for (int j = 1; j < result[0].length; j++)
                        output[0][j] = "0"; // first record don't need to process and should be filled with 0

                    for (int i = 1; i < totalRowCont; i++) {
                        long seconds;
                        output[i][0] = result[i][0]; // rec_time
                        try {
                            for (int j = 1; j < result[i].length; j++) {
                                try {
                                    seconds = (sdf.parse(output[i][0]).getTime() - sdf.parse(output[i - 1][0]).getTime()) / 1000;
                                } catch (ParseException e) {
                                    System.out.println("Calculate interval failed. Exit()");
                                    return null;
                                }
                                output[i][j] = result[i][j] != null ? String.valueOf(8 * Float.parseFloat(result[i][j]) / (float) seconds / 1000) : "0"; // kbps
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Parsing string to float failed. Exit()");
                            return null;
                        }
                    }
                } else {
                    for (int i = 0; i < totalRowCont; i++) {
                        for (int j = 0; j < result[i].length; j++) {
                            output[i][j] = result[i][j] != null ? result[i][j] : "0";
                        }
                    }
                }

                //System.out.println("Output data that is parsing after read from database:");
                //JTools.print(output, true);
                return output;
            }
            System.out.println("getByColumn lessPoints use " + (System.currentTimeMillis() - timeForLessPoint) / 1000 + " sec.");
            return null;
        }

        // Calculate the period of time for all intervals
        int interval;
        try {
            long seconds = (sdf.parse(lastRecord[1]).getTime() - sdf.parse(firstRecord[1]).getTime()) / 1000;
            interval = (int) (seconds / (pointNumber - 1));
        } catch (ParseException e) {
            System.out.println("Calculate interval failed. Exit()");
            return null;
        }
        // System.out.println("interval: " + interval);

        // Prepare initial output array
        String[][] output = new String[pointNumber][selectedColumns.length + 1];
        for (int i = 0; i < pointNumber; i++) {
            for (int j = 0; j < selectedColumns.length + 1; j++) {
                output[i][j] = "0";
            }
        }
        // System.out.println("selected columns size: " + selectedColumns.length);

        // Determine all timing that used for selecting data from DB
        output[0][0] = firstRecord[1];
        output[pointNumber - 1][0] = lastRecord[1];
        for (int i = 1; i < pointNumber - 1; i++) {
            try {
                calendar.setTime(sdf.parse(output[i - 1][0]));
            } catch (ParseException e) {
                System.out.println("Determine timing failed. Exit()");
                return null;
            }
            calendar.add(Calendar.SECOND, interval);
            calendar.set(Calendar.MILLISECOND, 0);
            output[i][0] = sdf.format(calendar.getTime());
        }

        // Combine selected columns which are used for doing summarize to a string
        String sumColumns = "SUM(" + selectedColumns[0] + ")";
        for (int i = 1; i < selectedColumns.length; i++) {
            sumColumns += ", SUM(" + selectedColumns[i] + ")";
        }

        double timeForAllPoint = System.currentTimeMillis();
        // Select data from DB and calculate the data rate for all selected columns
        for (int i = 1; i < pointNumber; i++) {
            String query = String.format("SELECT %s FROM %s WHERE PUBLIC_IP = '%s' AND IF_INDEX = '%s' AND REC_TIME >= '%s' AND REC_TIME <= '%s'",
                    sumColumns, tableNameInQuotes, ipAddr, ifIndex, output[i - 1][0], output[i][0]);
            // System.out.println("query: " + query);

            // System.out.println("Summary data from start_time=" + output[i - 1][0] + ", end_time=" + output[i][0]);
            double timeForQuery = System.currentTimeMillis();
            String[][] result = dbInst.getByQuery(query);
            System.out.print("    getByColumn query " + i + " use " + (System.currentTimeMillis() - timeForQuery) / 1000 + " sec, ");
            JTools.print(result, false);
            double timeForCalc = System.currentTimeMillis();
            if (result != null && result[0].length == selectedColumns.length) {
                if (getTheRate) {
                    try {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            if (result[0][j] != null) // Note that it may return null if nothing is queried in specified time range
                                output[i][j + 1] = String.valueOf(8 * Float.parseFloat(result[0][j]) / (float) interval / 1000); // kbps
                            // the summary should appear in the first column of result[][]
                            else
                                output[i][j + 1] = "0";
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Parsing string to float failed. Exit()");
                        return null;
                    }
                } else {
                    for (int j = 0; j < selectedColumns.length; j++) {
                        if (result[0][j] != null) // Note that it may return null if nothing is queried in specified time range
                            output[i][j + 1] = result[0][j];
                        else
                            output[i][j + 1] = "0";
                    }
                }
            } else {
                System.out.println("Size of data select from DB is wrong. Exit()");
                return null;
            }
            System.out.println("calc use " + (System.currentTimeMillis() - timeForCalc) / 1000 + " sec.");
        }
        System.out.println("getByColumn all points use " + (System.currentTimeMillis() - timeForAllPoint) / 1000 + " sec.");
        
        JTools.print(output, false);
        return output;
    }
    
    private String[][] getHistoryInRate(
            final String ipAddr,
            final String ifIndex,
            final String startTime,
            final String endTime,
            final String[] selectedColumns,
            final boolean isCounter64) {
        
        Calendar calendar = Calendar.getInstance();
        String baseStartTime = "2000-01-01 00:00:00.000"; // use this time as the far past time
        String baseEndTime = sdf.format(calendar.getTime()); // use current time as the latest time
        String ipAddrExpression = "PUBLIC_IP = '" + ipAddr + "'";
        String ifIndexExpression = "IF_INDEX = '" + ifIndex + "'";
        String startTimeExpression = "REC_TIME >= '" + (startTime != null && !startTime.isEmpty() ? startTime : baseStartTime) + "'";
        String endTimeExpression = "REC_TIME <= '" + (endTime != null && !endTime.isEmpty() ? endTime : baseEndTime) + "'";
        
        double timeForRate = System.currentTimeMillis();
        String selectedString = "REC_TIME, " + selectedColumns[0];
        for (int i = 1; i < selectedColumns.length; i++) selectedString += ", " + selectedColumns[i];
        String query = String.format("SELECT %s FROM %s WHERE %s AND %s AND %s AND %s",
                selectedString, tableNameInQuotes, ipAddrExpression, ifIndexExpression, startTimeExpression, endTimeExpression);
        String[][] result = dbInst.getByQuery(query);
        //System.out.println("Result data read from database:");
        //JTools.print(result, true);
        String[][] output = null;
        int lengthPerRecord = selectedColumns.length + 1;        // add one space to put rec_time in
        
        if (result != null && result.length > 0 && result[0].length == lengthPerRecord) {        // the length of first record should be correct
            output = new String[result.length][lengthPerRecord];

            output[0][0] = result[0][0];                    // rec_time
            for (int j = 1; j < result[0].length; j++) output[0][j] = "0";      // fill 0 to all lines in first record

            for (int i = 1; i < result.length; i++) {
                output[i][0] = result[i][0];                // rec_time
                if (result[i].length != lengthPerRecord) {
                    for (int j = 1; j < lengthPerRecord; j++) output[i][j] = "0";      // skip this record if length is wrong
                    continue;
                }
                
                long diffTime = 0;;
                try {
                    diffTime = (sdf.parse(output[i][0]).getTime() - sdf.parse(output[i - 1][0]).getTime()) / 1000;    // the diff between current and previous
                }
                catch (ParseException e) {
                    System.out.println("getHistoryInRate calculate diffTime failed.");
                    for (int j = 1; j < lengthPerRecord; j++) output[i][j] = "0";
                    continue;
                }
                
                for (int j = 1; j < lengthPerRecord; j++) {
                    try {
                        BigInteger curr = new BigInteger(result[i][j]);
                        BigInteger prev = new BigInteger(result[i - 1][j]);
                        BigInteger diff = curr.subtract(prev);
                        if (curr.compareTo(prev) < 0) {                 // loop occurred
                            BigInteger max = isCounter64 ? maxInt64 : maxInt32;
                            diff = max.subtract(prev).add(curr);
                        }
                        float rate = (diff.floatValue() * 8) / (float)diffTime / 1000;    // use 1 kbps = 1000 bps
                        if(diffTime > 3600){
                        	output[i-1][j] = "0";
                        	output[i][j] = "0";
                        }else{
                        	output[i][j] = String.valueOf(rate);
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("getHistoryInRate parsing BigInteger failed.");
                        output[i][j] = "0";
                    }
                }
            }

            //System.out.println("Output data that is parsing after read from database:");
            //JTools.print(output, true);
        }
        else {
            System.out.println("getHistoryInRate get data from db incorrect.");
        }
        System.out.println("getHistoryInRate uses " + (System.currentTimeMillis() - timeForRate) / 1000 + " sec.");
        
        return output;
    }
    
    public ArrayList<JDeviceReport> getRxTxOctetRateReport(List<JDevice> deviceList, String[] ipItems, String startTime, String endTime) {
    	Calendar calendar = Calendar.getInstance();
        String baseStartTime = "2000-01-01 00:00:00.000"; // use this time as the far past time
        String baseEndTime = sdf.format(calendar.getTime()); // use current time as the latest time
        String ipAddr = "";
        for(int i=0; i<ipItems.length; i++) ipAddr += "'"+ ipItems[i] +"',";
        String allIpAddr = ipAddr.substring(0, ipAddr.lastIndexOf(","));
        String ipAddrExpression = "PUBLIC_IP IN (" + allIpAddr + ")";
        String startTimeExpression = "REC_TIME >= '" + (startTime != null && !startTime.isEmpty() ? startTime : baseStartTime) + "'";
        String endTimeExpression = "REC_TIME <= '" + (endTime != null && !endTime.isEmpty() ? endTime : baseEndTime) + "'";
        String[] selectedColumns = { "OCT_RX_TOT", "OCT_TX_TOT" };
        double timeForRate = System.currentTimeMillis();
        
        String selectedString = "IF_INDEX, PUBLIC_IP, REC_TIME, " + selectedColumns[0];
        for (int i = 1; i < selectedColumns.length; i++) selectedString += ", " + selectedColumns[i];
        String query = String.format("SELECT %s FROM %s WHERE %s AND %s AND %s",
                selectedString, tableNameInQuotes, ipAddrExpression, startTimeExpression, endTimeExpression);
        JDatabase db = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        String[][] result = db.getByQuery(query);
        
        ArrayList<JDeviceReport> item = new ArrayList<JDeviceReport>();
        
        for(JDevice device : deviceList){
        	for(int j=0; j<ipItems.length; j++){
        		if(device.getPublicIp().equals(ipItems[j])){
        
		        JDeviceReport deviceReport = new JDeviceReport();
		        deviceReport.setPublicIp(device.getPublicIp());
		        deviceReport.setSnmpSupport(device.getSnmpSupport());
		        deviceReport.setSysObjectId(device.getSysObjectId());
		        deviceReport.setBrandName(device.getBrandName());
		        deviceReport.setPhyAddr(device.getPhyAddr());
		        deviceReport.setStackNum(device.getStackNum());
		        deviceReport.setAliasName(device.getAliasName());
		        deviceReport.setDeviceType(device.getDeviceType());
		        
		        ArrayList<Double> rxSort = new ArrayList<Double>();
		        ArrayList<Double> txSort = new ArrayList<Double>();
		        
		        for (JInterface inf : device.getInterfaces()) {
		        	if(inf.getIfType().equals("eth")){
		        		
		        		if (result != null) {
			        		ArrayList<String[]> tempResult = new ArrayList<String[]>();
			        		for(int i = 0; i < result.length; i++){
			        			if(result[i][0].equals(Integer.toString(inf.getIfIndex())) && result[i][1].equals(device.getPublicIp())){
			        				tempResult.add(result[i]);
			        			}
			        		}
			        		double rx = 0, tx = 0, rxSum = 0, txSum = 0;
			        		for(int i = 1; i < tempResult.size(); i++) {
			        			rx = (Double.parseDouble(tempResult.get(i)[3]) - Double.parseDouble(tempResult.get(i-1)[3])) < 0 ? Double.parseDouble(tempResult.get(i)[3]) / 1000000 : (Double.parseDouble(tempResult.get(i)[3]) - Double.parseDouble(tempResult.get(i-1)[3])) / 1000000;
			        			tx = (Double.parseDouble(tempResult.get(i)[4]) - Double.parseDouble(tempResult.get(i-1)[4])) < 0 ? Double.parseDouble(tempResult.get(i)[4]) / 1000000 : (Double.parseDouble(tempResult.get(i)[4]) - Double.parseDouble(tempResult.get(i-1)[4])) / 1000000;
			        			rxSum += rx;
			        			txSum += tx;
			        		}
			        		
		                    DecimalFormat df = new DecimalFormat("#.##");
		                    String rxOct = df.format(rxSum);
		                    String txOct = df.format(txSum);
		                    
		                    if(inf.getPortType() != null && inf.getPortType().equals("terminal")){
			        			rxSort.add(Double.parseDouble(rxOct));
			        			txSort.add(Double.parseDouble(txOct));
			        		}
		                    
		                    deviceReport.addInterfacesReport(inf.getPortId(), inf.getIfIndex(), inf.getIfPhysAddress(), inf.getStackId(), inf.isPoePort(), rxOct, txOct, inf.getPortType(), inf.getPortRemoteDev(), inf.getPortRemoteIp(), inf.getAliasName());
		                    
		                }
		                else {
		                	deviceReport.addInterfacesReport(inf.getPortId(), inf.getIfIndex(), inf.getIfPhysAddress(), inf.getStackId(), inf.isPoePort(), "--", "--", inf.getPortType(), inf.getPortRemoteDev(), inf.getPortRemoteIp(), inf.getAliasName());
		                    System.out.println("Port = " + inf.getPortId() + " Get data from db incorrect.");
		                }
		        	}
		        }
		        item.add(deviceReport);
		        
		        Collections.sort(rxSort, Collections.reverseOrder());
		        //System.out.println("Device IP = " + device.getPublicIp() + " Rx sort result= " + rxSort);
		        Collections.sort(txSort, Collections.reverseOrder());
		        //System.out.println("Device IP = " + device.getPublicIp() + " Tx sort result= " + txSort);
		        deviceReport.sortPacket(rxSort, txSort);
        		}
        	}
        }
        db.disconnect();
        
        System.out.println("Get history report uses " + (System.currentTimeMillis() - timeForRate) / 1000 + " sec.");
         
		return item;
    	
    }
    
    public ArrayList<JInterfaceTerminalReport> getTerminalRxTxOctetRateReport(List<JDevice> deviceList, String[] ipItems, String startTime, String endTime) {
    	Calendar calendar = Calendar.getInstance();
        String baseStartTime = "2000-01-01 00:00:00.000"; // use this time as the far past time
        String baseEndTime = sdf.format(calendar.getTime()); // use current time as the latest time
        String ipAddr = "";
        for(int i=0; i<ipItems.length; i++) ipAddr += "'"+ ipItems[i] +"',";
        String allIpAddr = ipAddr.substring(0, ipAddr.lastIndexOf(","));
        String ipAddrExpression = "PUBLIC_IP IN (" + allIpAddr + ")";
        String startTimeExpression = "REC_TIME >= '" + (startTime != null && !startTime.isEmpty() ? startTime : baseStartTime) + "'";
        String endTimeExpression = "REC_TIME <= '" + (endTime != null && !endTime.isEmpty() ? endTime : baseEndTime) + "'";
        String[] selectedColumns = { "OCT_RX_TOT", "OCT_TX_TOT" };
        double timeForRate = System.currentTimeMillis();
        
        String selectedString = "IF_INDEX, PUBLIC_IP, REC_TIME, " + selectedColumns[0];
        for (int i = 1; i < selectedColumns.length; i++) selectedString += ", " + selectedColumns[i];
        String query = String.format("SELECT %s FROM %s WHERE %s AND %s AND %s",
                selectedString, tableNameInQuotes, ipAddrExpression, startTimeExpression, endTimeExpression);
        JDatabase db = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user");
        String[][] result = db.getByQuery(query);
        //JTools.print(result, true);
        
        ArrayList<JInterfaceTerminalReport> terminal = new ArrayList<JInterfaceTerminalReport>();
        ArrayList<Double> rxSort = new ArrayList<Double>();
        ArrayList<Double> txSort = new ArrayList<Double>();
        
        for(JDevice device : deviceList){
        	for(int j=0; j<ipItems.length; j++){
        		if(device.getPublicIp().equals(ipItems[j])){
        			for (JInterface inf : device.getInterfaces()) {
        				if (result != null) {
        					if(inf.getPortType() != null && inf.getPortType().equals("terminal")){
        						ArrayList<String[]> tempResult = new ArrayList<String[]>();
        		        		for(int i = 0; i < result.length; i++){
        		        			if(result[i][0].equals(Integer.toString(inf.getIfIndex())) && result[i][1].equals(device.getPublicIp())){
        		        				
        		        				tempResult.add(result[i]);
        		        			}
        		        		}
        		        		double rx = 0, tx = 0, rxSum = 0, txSum = 0;
        		        		for(int i = 1; i < tempResult.size(); i++) {
        		        			rx = (Double.parseDouble(tempResult.get(i)[3]) - Double.parseDouble(tempResult.get(i-1)[3])) < 0 ? Double.parseDouble(tempResult.get(i)[3]) / 1000000 : (Double.parseDouble(tempResult.get(i)[3]) - Double.parseDouble(tempResult.get(i-1)[3])) / 1000000;
        		        			tx = (Double.parseDouble(tempResult.get(i)[4]) - Double.parseDouble(tempResult.get(i-1)[4])) < 0 ? Double.parseDouble(tempResult.get(i)[4]) / 1000000 : (Double.parseDouble(tempResult.get(i)[4]) - Double.parseDouble(tempResult.get(i-1)[4])) / 1000000;
        		        			rxSum += rx;
        		        			txSum += tx;
        		        		}
        		        		
        	                    DecimalFormat df = new DecimalFormat("#.##");
        	                    String rxOct = df.format(rxSum);
        	                    String txOct = df.format(txSum);
        	                    
        	                    
        	        			
        	        			if(!rxOct.equals("0") || !txOct.equals("0")){
        	        				
        	        				rxSort.add(Double.parseDouble(rxOct));
            	        			txSort.add(Double.parseDouble(txOct));
            	        			
	        	        			JInterfaceTerminalReport ter = new JInterfaceTerminalReport();
	        	        			ter.setRxOct(rxOct);
	        	        			ter.setTxOct(txOct);
	        	        			ter.setPublicIp(device.getPublicIp());
	        	        			ter.setPortId(inf.getPortId());
	        	        			ter.setPortRemoteDev(inf.getPortRemoteDev());
	        	        			ter.setPortRemoteIp(inf.getPortRemoteIp());
	        	        			ter.setAliasName(inf.getAliasName());
	        	        			terminal.add(ter);
        	        			}
        					}
        				}
        			}
        		}
        	}
        }
        
        Collections.sort(rxSort, Collections.reverseOrder());
        //System.out.println("Rx sort result= " + rxSort);
        Collections.sort(txSort, Collections.reverseOrder());
        //System.out.println("Tx sort result= " + txSort);
        
        if(rxSort != null || txSort != null){
			
			int rxSortSize = rxSort.size() > 10 ? rxSortSize = 10 : rxSort.size();
			int txSortSize = txSort.size() > 10 ? txSortSize = 10 : txSort.size();
			
			for(int i=0; i<rxSortSize; i++){
				for(JInterfaceTerminalReport ter : terminal){
					if(ter.getRxOct() != null && Double.parseDouble(ter.getRxOct()) == rxSort.get(i)){
						ter.setRxRanking(i+1);
					}
				}
			}
			
			for(int i=0; i<txSortSize; i++){
				for(JInterfaceTerminalReport ter : terminal){
					if(ter.getTxOct() != null && Double.parseDouble(ter.getTxOct()) == txSort.get(i)){
						ter.setTxRanking(i+1);
					}
				}
			}
		}
        
        db.disconnect();
        
        System.out.println("Get ranking report uses " + (System.currentTimeMillis() - timeForRate) / 1000 + " sec.");
		return terminal;
    }
}