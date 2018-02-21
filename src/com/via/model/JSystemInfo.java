package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class JSystemInfo {

	private int tableCount;
	private List<JSystemInfoTable> tables;
	

	public JSystemInfoTable readDevice(JDevice device) {
	    Logger logger = Logger.getLogger(JSystemInfoTable.class);

		String[] hrStorageOids = {
				JOid.hrStorageDescr,
				JOid.hrStorageSize,
				JOid.hrStorageUsed
		};
		String[] hrProcessorOids = {
				JOid.hrProcessorLoad
		};

		Map<String, List<String>> hrStorageResult,hrProcessorResult;
		int a = 0,b = 0;
		boolean isFail=false;		// will temporary down

		final int StorageDescr = a++, StorageSize = a++, StorageUsed = a++;
		final int ProcessorLoad = b++;
		JSystemInfoTable table = new JSystemInfoTable();
		
		if (device == null) return table;

		// Get data from remote device
		try {
            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
			snmp.start();
			hrStorageResult = snmp.getTable(hrStorageOids);
			hrProcessorResult = snmp.getTable(hrProcessorOids);
			snmp.end();
	        logger.debug("Device IP = " + device.getPublicIp() +" System Information Get SNMP Table");   

		} catch (IOException e) {
			System.out.println("Read SNMP Failed.\n" + e.getMessage());
	        logger.warn("Device IP = " + device.getPublicIp() +" System Information Read Device Failed");   

			return table;
		}

		
		JMib.printRawMap(hrStorageResult, false);
		JMib.printRawMap(hrProcessorResult, false);

		// Extract received data and store into a List
		List<JSystemInfoItem> items = new ArrayList<JSystemInfoItem>();
		if (hrStorageResult != null && hrStorageResult.size() != 0) {
			for (Map.Entry<String, List<String>> entry : hrStorageResult.entrySet()) {
				JSystemInfoItem item = new JSystemInfoItem();		
	            String ifKey = entry.getKey();
//	    		System.out.println("hrStorageResult Index= " + ifKey); 
	    		if (hrStorageResult.containsKey(ifKey)) {
	    			item.setStorageIndex(ifKey);
	    			item.setStorageDescr(hrStorageResult.get(ifKey).get(StorageDescr)); 
	    			item.setStorageSize(hrStorageResult.get(ifKey).get(StorageSize)); 
	    			item.setStorageUsed(hrStorageResult.get(ifKey).get(StorageUsed)); 
				} else {
	    	        isFail=true;
	    		}
				items.add(item);
			}
		} else {
            logger.debug(" hrStorage  info failed.");     // TODO check if result has noSuch string
		}
	
		List<JSystemInfoItem> items1 = new ArrayList<JSystemInfoItem>();
		if (hrProcessorResult != null && hrProcessorResult.size() != 0) {
			for (Map.Entry<String, List<String>> entry : hrProcessorResult.entrySet()) {
				JSystemInfoItem item1 = new JSystemInfoItem();
	            String ifKey = entry.getKey();
//	    		System.out.println("hrProcessorResult Index= " + ifKey); 
	    		if (hrProcessorResult.containsKey(ifKey)) {
	    			item1.setProcessorIndex(ifKey); 
	    			item1.setProcessorLoad(hrProcessorResult.get(ifKey).get(ProcessorLoad)); 
				} else {
	    	        isFail=true;
	    		}
				items1.add(item1);
			}
		} else {
            logger.debug(" ProcessorLoad  info failed.");     // TODO check if result has noSuch string
		}

		
		// Store device data as a table
		table.setItems(items);
		table.setItems1(items1);
		table.setItemCount(items.size());		// should be equal to 'portNum'
		table.setSysDescr(device.getSysDescr());
		table.setIpAddr(device.getPublicIp());
		table.setLocalIp(device.getLocalIp());
//		table.setPortNum(portNum);

    	for (JSystemInfoItem item : table.getItems()) {
	    		System.out.print(String.format("IpAddr=%s StorageIndex=%s StorageDescr=%s StorageSize=%s StorageUsed=%s ",
	    				table.getIpAddr(), item.getStorageIndex(), item.getStorageDescr(), item.getStorageSize(), item.getStorageUsed()  ));
	    		
	    		System.out.println("");

	    }
    	
    	for (JSystemInfoItem item : table.getItems1()) {
    		System.out.print(String.format("IpAddr=%s ProcessorIndex=%s ProcessorLoad=%s ",
    				table.getIpAddr(), item.getProcessorIndex(), item.getProcessorLoad()  ));
    		
    	System.out.println("");

    }		
		
		if (!isFail)
			logger.debug("Device IP = " + device.getPublicIp() +" System Information Read Device Success ");
		else
			logger.warn("Device IP = " + device.getPublicIp() +" System Information Read Failed ");

		return table;
		
		
	}

	public List<JSystemInfoTable> readDeviceAll(List<JDevice> deviceList) {
	    Logger logger = Logger.getLogger(JSystemInfoTable.class);

		List<JSystemInfoTable> tables = new ArrayList<JSystemInfoTable>();
		if (deviceList == null) return tables;

		for (JDevice device : deviceList) {
			JSystemInfoTable table = readDevice(device);
			tables.add(table);
		}

        logger.debug("Current VLAN List Read All Device Success");   
		return tables;
	}

	public int getTableCount() {
		return tableCount;
	}
	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}
	public List<JSystemInfoTable> getTables() {
		return tables;
	}
	public void setTables(List<JSystemInfoTable> tables) {
		this.tables = tables;
	}
}
