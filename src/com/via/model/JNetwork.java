package com.via.model;

import java.io.*;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.via.database.*;
import com.via.model.JLinkView.JLinkViewItem;
import com.via.model.JPortStatus.JPortStatusItem;
import com.via.model.JVlanStatus.JVlanStatusItem;
import com.via.model.JPoeStatus.JPoeStatusItem;
import com.via.model.JPortStatistics.JPortStatisticsItem;
import com.via.model.JEtherlikeStatistics.JEtherlikeStatisticsItem;
import com.via.model.JRmonStatistics.JRmonStatisticsItem;
import com.via.model.JStaticVlanList.JStaticVlanListItem;
import com.via.model.JCurrentVlanList.JCurrentVlanListItem;
import com.via.system.Config;
import com.via.topology.*;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

public class JNetwork {
	private static List<JDevice> deviceList;
	private static List<JNms> nmsList;
	private List<JGroup> groupList;
	private Map<String, String[]> deviceMapper = null; // record the mapping of device's MAC address and it's basic information
	private static JTopology topology;
	private Timer scheduleTimer;
	private Timer poeScheduleTimer;
	private Timer acapscheduleTimer;
	private Timer stanumberscheduleTimer;
	private Timer reportScheduleTimer;
	private Timer nmsScheduleTimer;
	private JScanIpTask scanIpThread;
	private JDatabase database;
	private static JDbAccount dbAccount;
	private static JDbDevice dbDevice;
	private JDbStatistics dbStatistics;
	private JDbLog dbLog;
	private JDbModule dbModule;
	private JDbPoeSchedule dbPoeSchedule;
	private JDbProfile dbProfile;
	private JDbApList dbApList;
	private JDbApSsidList dbApSsidList;
	private JDbSTANumber dbSTANumber;
	private JDbNms dbNms;
	private JDbGroup dbGroup;
	private static JDbAliveStatus dbAliveStatus;
	private static JDbNmsLog dbNmsLog;
	private String dbAccountTableName = "ACCOUNT05";
	private String dbDeviceTableName = "DEVICE04";
	private String dbStatisticsTableName = "STATISTICS03";
	private String dbLogTableName = "LOG01";
	private String dbModuleTableName = "MODEL02";
	private String dbPoeScheduleTableName = "POE_SCHEDULE01";
	private String dbProfileTableName = "PROFILE01";
	private String dbApListTableName = "AP_LIST01";
	private String dbApSsidListTableName = "AP_SSID_LIST01";
	private String dbSTANumberTableName = "STANUMBER01";
	private String dbNotifyName = "NOTIFY02";
	private String dbNmsTableName = "NMS03";
	private String dbAliveStatusTableName = "ALIVE_STATUS01";
	private String dbNmsLogTableName = "NMS_LOG01";
	private String dbGroupTableName = "GROUP03";
	private JLogin login;
	private JTrapReceiver trap;
	private String licenseChk;
	private static boolean isTopologyRunning = false;
	private static boolean isFirstTime = true;
	
	private List<String> ping_info;
	
	private List<String> license_info;

	private RemoteService remoteservice;
	
	public JNetwork() {
		initDatabase();
		initAccountTable();
		initLoginSystem();
		initModuleTable(); // MUST initial before device
		initDeviceList();
		initNmsTable();
		initGroupTable();
		initStatisticsTable(); // check and create device's statistics table
		initApListTable();
		initApSsidListTable();
		initSTANumberTable();
		initLogTable();
		initNotifyTable();
		initPoeScheduleTable();
		initProfileTable();
		initAliveStatusTable();
		initNmsLogTable();
		initScheduleTask();
		initNmsScheduleTask();
		initReportScheduleTask();
		initPoeScheduleTask();
		initAcApScheduleTask();
		initSTANumberScheduleTask();
		initScanIpThread();
		initTopology();
		initSnmpTrap();
		initSetSingleIpScript();
		initDefaultInterfacesFile();
		initSetInterfaceScript();
		initGetSerialNumberScript();
		initUpdateWebScript();

		initLicense();
		initPing();

		initRemoteService();
		
		initTrapPort();
	}

	// ====================================================================================================
	
	public String[][] checkDevices(String[] snmpAddrArray, String readCommunity) {
		if (snmpAddrArray == null)
			return null;
		List<String[]> simpleDeviceInfoList = new ArrayList<String[]>();

		for (String ip : snmpAddrArray) {
			simpleDeviceInfoList.add(JDevice.fetchSimpleInfo(ip,readCommunity)); // should need to check device is valid or not ?
		}

		if (simpleDeviceInfoList.size() != 0) {
			String[][] simpleDeviceInfoArray = new String[simpleDeviceInfoList.size()][];

			for (int i = 0; i < simpleDeviceInfoList.size(); i++) {
				simpleDeviceInfoArray[i] = simpleDeviceInfoList.get(i);
			}

			return simpleDeviceInfoArray;
		} else {
			return null;
		}
	}

	public boolean isDeviceNumberReachLimitation() {
	    int usedDeviceNum = deviceList.size();
	    int limitedDeviceNum = 0;

	    try {
	        limitedDeviceNum = Integer.parseInt(license_info.get(3));
	        System.out.println("limitedDeviceNum:" + limitedDeviceNum);
	    }
	    catch(NumberFormatException e) {
	        System.out.println("Parsing number from license device number failed.");
	    }
	    
	    if (limitedDeviceNum - usedDeviceNum > 0) return false;
	    else return true;
	}
	
	public boolean addNmsDevice(String ip, String aliasName) {
		String username = "remote_admin", password = "";
		String uuid = UUID.randomUUID().toString();
		//System.out.println("uuid = " + uuid);
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(uuid.getBytes(), 0, uuid.length());
			password = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			
		}
		
		RemoteInterface Service = RemoteService.creatClient(ip, 3);
		
		if(Service != null) {
			List<JDevice> remoteDeviceList = new ArrayList<JDevice>();
			boolean isAccountExisted = true; 
			try {
				remoteDeviceList = Service.getRemoteDevice();
				isAccountExisted = Service.isAccountExisted("remote_admin");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			JNms device = new JNms();
			if(isAccountExisted){
				try {
					Service.updateRemotePassword(username, password);
					//System.out.println("Is account existed");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					Service.addAccount(username, password, "admin", "admin", "", "", "false", "true");
					//System.out.println("Is account not existed");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
				for (JNms checkDevice : nmsList) {
					if (ip.equals(checkDevice.getPublicIp())) {
						System.out.println(ip + " is duplicated in device list.");
						return false;
					}
				}
				
				int onlineDeviceNum = 0, offlineDeviceNum = 0;
				for (JDevice remoteDevice : remoteDeviceList) {
					if(remoteDevice.isAlive() == 1){
						onlineDeviceNum++;
					}
					else{
						offlineDeviceNum++;
					}
				}
				
				device.setPublicIp(ip);
				device.setAliasName(aliasName);
				device.setTotalDeviceNum(remoteDeviceList.size());
				device.setOnlineDeviceNum(onlineDeviceNum);
				device.setOfflineDeviceNum(offlineDeviceNum);
				device.setLastSeen(new Date());
				device.setAlive(true);
				device.setRemoteAccount(username);
				device.setRemotePwd(password);
				
				nmsList.add(device);

				if (dbNms.addNmsDevice(device)) {
					System.out.println("Device " + device.getPublicIp() + " add to device table success.");
				} else {
					System.out.println("Device " + device.getPublicIp() + " add to device table failed.");
				}

				return true;
		}
		
		return false;
	}
	
	public List<JGroup> viewGroup() {
		return dbGroup.getAllDevice();
	}
	
	public String addGroup(String groupName) {
		
		if (dbGroup.isNameExisted(groupName)) {
			return "Repeat";
		}
		
		JGroup group = new JGroup();
		group.setName(groupName);
		
		if (dbGroup.addGroupDevice(group)) {
			System.out.println("Group " + group.getName() + " add to device table success.");
		} else {
			System.out.println("Group " + group.getName() + " add to device table failed.");
		}
		
		return "Success";
	}
	
	public String updateGroup(String groupName, String bgroupName) {
		
		if (dbGroup.isNameExisted(bgroupName)) {
			if (!dbGroup.isNameExisted(groupName)) {
				if(dbGroup.updateName(groupName, bgroupName)){
					for (JDevice device : deviceList) {
						if(device.getGroupName().equals(bgroupName)){
							device.setGroupName(groupName);
							dbDevice.updateGroupName(device);
						}
					}
					return "Success";
				}
			}
			else {
				return "Repeat";
			}
		}
		
		return "fail";
		
	}
	
	public String updateParent(String groupName, String parent) {
		
		if(dbGroup.updateParent(groupName, parent)){
			return "Success";
		}
		
		return "fail";
		
	}
	
	public String updateRoot(String groupName, String root) {
		
		if (!dbGroup.isRootExisted(groupName, root)) {
			if(dbGroup.updateRoot(groupName, root) && dbGroup.updateParent(groupName, "none")){
				return "Success";
			}
		}
		
		return "fail";
		
	}
	
	public static boolean isAccountExisted(final String username) {
		return dbAccount.isAccountExisted(username);
	}
	
	public static boolean addAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) {
		return dbAccount.addAccount(username, password, level, name, email, phone_number, is_manage, is_remote);
	}
	
	public static boolean updateRemotePassword(final String username, final String password) {
		return dbAccount.updatePassword(username, password);
	}
	
	public static List<List<String[]>> getAliveStatus(final String[] SelectColumns, final String LAYER, final String startTime, final String endTime, final String ADDR) {
		return dbAliveStatus.getAliveStatus(SelectColumns, LAYER, startTime, endTime, ADDR);
	}
	
	public static boolean insertNmsLog(final String SRC_ADDR, final Timestamp DATED, final String LEVEL, final String MESSAGE) {
		for (JNms checkDevice : nmsList) {
			if (SRC_ADDR.equals(checkDevice.getPublicIp())) {
				return dbNmsLog.insert(SRC_ADDR, DATED, LEVEL, MESSAGE);
			}
		}

		//System.out.println(SRC_ADDR + " is not in NMS list.");
		return false;
	}
	
	public String[][] getNmsLast50Log(final String SRC_ADDR) {
		return dbNmsLog.getNmsLast50Log(SRC_ADDR);
	}
	
	public static List<JDevice> getRemoteDevice() {
		return deviceList;
	}
	
	public List<JDevice> getRemoteDeviceTable(String ip) {
		RemoteInterface Service = RemoteService.creatClient(ip, 3);
		
		if(Service != null) {
			List<JDevice> remoteDeviceList = new ArrayList<JDevice>();
			try {
				remoteDeviceList = Service.getRemoteDevice();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			if(remoteDeviceList.size() > 0) {
				return remoteDeviceList;
			}
		}
		return null;
	}

	public boolean addManagedDevice(String snmpAddr, String readCommunity, int snmpTimeout, int snmpVersion,
			String securityName, int securityLevel, String authProtocol, String authPassword, String privProtocol, String privPassword) {
		
		String objectId = "";
		int modelRevision = 1;
		try {
			JSnmp snmp = snmpVersion<=2 ? new JSnmp(snmpAddr, readCommunity, snmpVersion, snmpTimeout, 1) :
				new JSnmp(snmpAddr, snmpVersion, snmpTimeout, 1, securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
			snmp.start();
			objectId = snmp.getNode(JOid.sysObjectID);
			snmp.end();
		} catch (IOException e) {
		    System.out.println("SNMP read objectId exception: " + e.getMessage());
		}
		if (objectId.isEmpty()) {
			System.out.println("AddManagedDevice : input IP can't be used to read objectId.");
			/*int pingResult = JTools.Ping(JTools.parseSnmpIp(snmpAddr), 2, 2);
			
			if(pingResult == 1){
				return addDeviceByObjectId(snmpAddr, "1.3.6.1.4.1.77777.1.1.33", "public", 1000, 1, 0, "", 0,"", "","","");
			}else{
				System.out.println("AddManagedDevice : input IP can't be used to ping.");*/
				return false;
			//}
		}
		else {
			modelRevision = 1;
			System.out.println("AddManagedDevice : modelRevision = " + modelRevision);
		}
			
		return addDeviceByObjectId(snmpAddr, objectId, readCommunity, snmpTimeout, modelRevision, snmpVersion,
				securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword);
	}

	public boolean addNonManagedDevice(String snmpAddr, String objectId) {
		return addDeviceByObjectId(snmpAddr, objectId, "public", 1000, 1, -1, "", 0,"", "","","");
	}
	
	public boolean addPingManagedDevice(String snmpAddr, String objectId) {
		int pingResult = JTools.Ping(JTools.parseSnmpIp(snmpAddr), 2, 2);
		
		if(pingResult == 1){
			return addDeviceByObjectId(snmpAddr, objectId, "public", 1000, 1, 0, "", 0,"", "","","");
		}else{
			System.out.println("AddManagedDevice : input IP can't be used to ping.");
			return false;
		}
	}

	private boolean addDeviceByObjectId(String snmpAddr, String objectId, String readCommunity, int snmpTimeout, int modelRevision, int snmpVersion,
			String securityName, int securityLevel, String authProtocol, String authPassword, String privProtocol, String privPassword) {
		JModule module = dbModule.getByObjectIdAndmodelRevision(objectId, String.valueOf(modelRevision));	//Fix Cisco 3750 issue
		if (module == null) {
			System.out.println("input IP can't find the module.");
			return false;
		}

		for (JDevice device : deviceList) {
			if (snmpAddr.equals(device.getSnmpAddress())) {
				System.out.println(snmpAddr + " is duplicated in device list.");
				return false;
			}
		}

		JDevice device = new JDevice();
		device.setPublicIp(JTools.parseSnmpIp(snmpAddr));
		device.setSnmpPort(JTools.parseSnmpPort(snmpAddr)); // has to ensure the addr is snmp address
		device.setSnmpAddress(snmpAddr);
		device.setReadCommunity(readCommunity);
		device.setSnmpTimeout(snmpTimeout);
		device.setSnmpVersion(snmpVersion);
		device.setSecurityName(securityName);
		device.setSecurityLevel(securityLevel);
		device.setAuthProtocol(authProtocol);
		device.setAuthPassword(authPassword);
		device.setPrivProtocol(privProtocol);
		device.setPrivPassword(privPassword);
		device.setGroupName("--");
		
		if (device.initial(module) != 1) {
			System.out.println(String.format("Device: %s(%s) initial failed.", device.getPublicIp(), device.getDeviceType()));
		}

		deviceList.add(device);

		if (dbDevice.addDevice(device)) {
			System.out.println("Device " + device.getSnmpAddress() + " add to device table success.");
		} else {
			System.out.println("Device " + device.getSnmpAddress() + " add to device table failed.");
		}

		return true;
	}

	public void portAddInsert(String add_port) {

		if (add_port != null) {
			Config.setDeviceAddLastIP(add_port);
		} else {
			System.out.println("No Port.");
		}
	}

	public void portScanInsert(String scan_port_from, String scan_port_to) {

		if (scan_port_from != null && scan_port_to != null) {
			Config.setDeviceScanRange(scan_port_from, scan_port_to);
		} else {
			System.out.println("No Port.");
		}
	}
	
	public boolean removeNmsDevices(String[] ipList) {
		if (ipList == null)
			return false;
		boolean updated = false;

		for (String ip : ipList) {
			Iterator<JNms> it = nmsList.iterator();
			while (it.hasNext()) {
				JNms device = it.next();
				if (device.getPublicIp().equals(ip)) {
					// Remove record from device table
					if (dbNms.removeDevice(device)) {
						System.out.println("Device " + device.getPublicIp() + " remove from db success.");
					}

					it.remove();
					updated = true;
					break;
				}
			}
		}


		if (updated) {
			updateDeviceMapper();
		}

		return updated;
	}

	public boolean removeDevices(String[] ipList, String type) {
		if (ipList == null)
			return false;
		boolean updated = false;

		/*if (type == "device") {*/

		for (String ip : ipList) {
			Iterator<JDevice> it = deviceList.iterator();
			while (it.hasNext()) {
				JDevice device = it.next();
				if (device.getPublicIp().equals(ip)) {
					// Remove record from device table
					if (dbDevice.removeDevice(device)) {
						System.out.println("Device " + device.getSnmpAddress() + " remove from db success.");

						// Remove statistics table from db
						/*if (database.deleteTable(device.getDbStatisticsTableName())) {
						    System.out.println("Statistics table " + device.getDbStatisticsTableName() + " has removed.");
						}*/
					}

					it.remove();
					updated = true;
					break;
				}
			}
		}


		if (updated) {
			updateDeviceMapper();
		}

		return updated;
	}
	
	public boolean removeGroupDevices(String[] nameList) {
		if (nameList == null)
			return false;
		boolean updated = false;

		for (String name : nameList) {
			if (dbGroup.removeGroup(name)) {
				updated = true;
				System.out.println("Device " + name + " remove from db success.");
				for (JDevice device : deviceList) {
					if(device.getGroupName().equals(name)){
						device.setGroupName("--");
						dbDevice.updateGroupName(device);
					}
				}
			}
		}

		return updated;
	}

	public String[] getDeviceSystemInfo(String ip) {
		JDevice device = findDeviceByIp(ip);
		String[] info = new String[6];

		if (device != null) {
			info = device.getSystemInfo();
			//System.out.println(Arrays.toString(info));
		} else {
			System.out.println("No Such Device.");
		}

		return info;
	}

	public static String[] getRemoteDeviceSystemInfo() {
		String[] info = null;

		String Community = "public";

		try {
			if (JSystem.osName.equals("Linux")) {
				String line = "";

				try {
					String[] cmd = { "/bin/sh", "-c", "cat /etc/snmp/snmpd.conf | grep 'com2sec ro default'" };
					Process Process_bak = Runtime.getRuntime().exec(cmd);
					BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_bak.readLine()) != null) {
						//System.out.println(line);
						Community = line.split(" ")[3];
					}
					BufferedReader_bak.close();
					Process_bak.waitFor();
				} catch (Exception e) {
					System.out.println("cat /etc/snmp/snmpd.conf | grep 'com2sec ro default' 發生 IOException");
					//e.printStackTrace();
				}
			}

			JSnmp snmp = new JSnmp("127.0.0.1/161", Community, 2, 2000, 1);
			snmp.start();
			info = snmp.getNodes(new String[] { JOid.sysDescr, JOid.sysObjectID, JOid.sysUpTime, JOid.sysContact, JOid.sysName, JOid.sysLocation });
			//System.out.println(info.length);
			snmp.end();
		} catch (IOException e) {
			System.out.println("Read system information failed.");
			return info;
		}

		return info;
	}
	
	public boolean updateNmsAliasName(String ip, String aliasName) {
		if(ip == null || aliasName == null) return false;
		
		for (JNms device : nmsList) {
			if(ip.equals(device.getPublicIp())){
				device.setAliasName(aliasName);
				dbNms.updateAlive(device);
				return true;
			}
		}

		return false;
	}

	public boolean updateDeviceAliasName(String ip, String aliasName) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setAliasName(aliasName);
			if(dbDevice.updateAliasName(device)){
				return true;
			}
		} else {
			System.out.println("No Such Device.");
			return false;
		}
		return false;
	}

	public void updateDeviceReadCommunity(String ip, String readCommunity) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setReadCommunity(readCommunity);
			dbDevice.updateDeviceReadCommunity(device);
		} else {
			System.out.println("No Such Device.");
		}
	}
	
	public void updateDeviceWriteCommunity(String ip, String writeCommunity) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setWriteCommunity(writeCommunity);
			dbDevice.updateDeviceWriteCommunity(device);
		} else {
			System.out.println("No Such Device.");
		}
	}

	public void updateDeviceSnmpTimeout(String ip, String snmpTimeout) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setSnmpTimeout(Integer.parseInt(snmpTimeout));
			dbDevice.updateDeviceSnmpTimeout(device);
		} else {
			System.out.println("No Such Device.");
		}
	}
	
	public boolean updateSnmpV2(String ip, int snmpVersion, int snmpTimeout, String readCommunity, String writeCommunity) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setSnmpVersion(snmpVersion);
			device.setSnmpTimeout(snmpTimeout);
			device.setReadCommunity(readCommunity);
			device.setWriteCommunity(writeCommunity);
			
			dbDevice.updateDeviceSnmpVersion(device);
			dbDevice.updateDeviceSnmpTimeout(device);
			dbDevice.updateDeviceReadCommunity(device);
			dbDevice.updateDeviceWriteCommunity(device);
			return true;
		} else {
			System.out.println("No Such Device.");
			return false;
		}
	}
	
	public boolean updateSnmpV3(String ip, int snmpVersion, int snmpTimeout,
			String securityName, int securityLevel, String authProtocol, String authPassword, String privProtocol, String privPassword) {
		JDevice device = findDeviceByIp(ip);

		if (device != null) {
			device.setSnmpVersion(snmpVersion);
			device.setSnmpTimeout(snmpTimeout);
			device.setSecurityName(securityName);
			device.setSecurityLevel(securityLevel);
			device.setAuthProtocol(authProtocol);
			device.setAuthPassword(authPassword);
			device.setPrivProtocol(privProtocol);
			device.setPrivPassword(privPassword);
			
			dbDevice.updateDeviceSnmpVersion(device);
			dbDevice.updateDeviceSnmpTimeout(device);
			dbDevice.updateDeviceSecurityName(device);
			dbDevice.updateDeviceSecurityLevel(device);
			dbDevice.updateDeviceAuthProtocol(device);
			dbDevice.updateDeviceAuthPassword(device);
			dbDevice.updateDevicePrivProtocol(device);
			dbDevice.updateDevicePrivPassword(device);
			return true;
		} else {
			System.out.println("No Such Device.");
			return false;
		}
	}
	
	public boolean nmsRefresh(String[] ipList) {
		boolean updated = false;
		if (ipList == null){
			for (JNms device : nmsList) {
				RemoteInterface Service = RemoteService.creatClient(device.getPublicIp(), 3);
				
				if(Service != null) {
					List<JDevice> remoteDeviceList = new ArrayList<JDevice>();
					try {
						remoteDeviceList = Service.getRemoteDevice();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					if(remoteDeviceList.size() > 0) {
						int onlineDeviceNum = 0, offlineDeviceNum = 0;
						for (JDevice remoteDevice : remoteDeviceList) {
							if(remoteDevice.isAlive() == 1){
								onlineDeviceNum++;
							}
							else{
								offlineDeviceNum++;
							}
						}
						
						device.setTotalDeviceNum(remoteDeviceList.size());
						device.setOnlineDeviceNum(onlineDeviceNum);
						device.setOfflineDeviceNum(offlineDeviceNum);
						device.setLastSeen(new Date());
						device.setAlive(true);
						
						dbNms.updateTotalDevNum(device);
						dbNms.updateOnlineDevNum(device);
						dbNms.updateOfflineDevNum(device);
						dbNms.updateLastSeen(device);
						dbNms.updateAlive(device);
					}
				}
				else {
					device.setAlive(false);
					dbNms.updateAlive(device);
				}
				updated = true;
			}
			
		}
		else {
			for (String ip : ipList) {
				Iterator<JNms> it = nmsList.iterator();
				while (it.hasNext()) {
					JNms device = it.next();
					if (device.getPublicIp().equals(ip)) {
						RemoteInterface Service = RemoteService.creatClient(device.getPublicIp(), 3);
						
						if(Service != null) {
							List<JDevice> remoteDeviceList = new ArrayList<JDevice>();
							try {
								remoteDeviceList = Service.getRemoteDevice();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							
							if(remoteDeviceList.size() > 0) {
								int onlineDeviceNum = 0, offlineDeviceNum = 0;
								for (JDevice remoteDevice : remoteDeviceList) {
									if(remoteDevice.isAlive() == 1){
										onlineDeviceNum++;
									}
									else{
										offlineDeviceNum++;
									}
								}
								
								device.setTotalDeviceNum(remoteDeviceList.size());
								device.setOnlineDeviceNum(onlineDeviceNum);
								device.setOfflineDeviceNum(offlineDeviceNum);
								device.setLastSeen(new Date());
								device.setAlive(true);
								
								dbNms.updateTotalDevNum(device);
								dbNms.updateOnlineDevNum(device);
								dbNms.updateOfflineDevNum(device);
								dbNms.updateLastSeen(device);
								dbNms.updateAlive(device);
							}
						}else {
							device.setAlive(false);
							dbNms.updateAlive(device);
						}
						updated = true;
						break;
					}
				}
			}
		}

		return updated;
	}
	
	public boolean devicesRefresh(String[] ipList) {
		boolean updated = false;
		if (ipList == null){
			for (JDevice device : deviceList) {
				if (device.getSnmpSupport() < 1)
					continue;
					device.devicesRefresh();
					dbDevice.updateAlive(device);
				dbDevice.updateMacAddr(device);
				if (device.isAlive() == 1 && device.getSysObjectId().indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {
					dbDevice.updateIfIndex(device);
				}
			}
			updated = true;
		}
		else {
			for (String ip : ipList) {
				Iterator<JDevice> it = deviceList.iterator();
				while (it.hasNext()) {
					JDevice device = it.next();
					if (device.getPublicIp().equals(ip)) {
						device.devicesRefresh();
						dbDevice.updateAlive(device);
						dbDevice.updateMacAddr(device);
						if (device.isAlive() == 1 && device.getSysObjectId().indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {
							dbDevice.updateIfIndex(device);
						}
						updated = true;
						break;
					}
				}
			}
		}
		
		List<JGroup> groupList = dbGroup.getAllDevice();
		
		for(JGroup group : groupList){
			boolean isRed = false, isYellow = false;
			for (JDevice device : deviceList) {
				if(group.getName().equals(device.getGroupName()) && isRed == false){
					if(device.isAlive() == 0){
						dbGroup.updateAlarmStatus(device);
						group.setAliveStatus(0);
						isRed = true;
					}
					else if (device.isAlive() == 2){
						dbGroup.updateAlarmStatus(device);
						group.setAliveStatus(2);
						isYellow = true;
					}
					else if (device.isAlive() == 1 && isYellow == false){
						dbGroup.updateAlarmStatus(device);
						group.setAliveStatus(1);
					}
				}
			}
			if(group.getAliveStatus() == 0){
				for (JDevice device : deviceList) {
					if(!group.getName().equals(device.getGroupName())){
						dbGroup.updateAlarmStatusFalse(group.getName());
					}
				}
			}
		}

		return updated;
	}
	
	public boolean setDevGroup(String[] ipList, String groupName) {
		boolean updated = false;
		if (ipList != null){
			for (String ip : ipList) {
				for(JDevice device : deviceList){
					if (device.getPublicIp().equals(ip)) {
						device.setGroupName(groupName);
						dbDevice.updateGroupName(device);
						updated = true;
					}
				}
			}
		}

		return updated;
	}
	
	public boolean setDevPro(String[] ipList, String profileName) {
		boolean updated = false;
		List<JProfile> list = dbProfile.getProfileItems();
		if (ipList != null){
			for (String ip : ipList) {
				for(JDevice device : deviceList){
					
					if (device.getPublicIp().equals(ip)) {
						device.setProfileName(profileName);
						device.setMailFilter(true);
						for (JProfile item : list) {
							if(item.getProfileName().equals(device.getProfileName())){
								device.setProfileStartTime(item.getStartTime());
								device.setProfileEndTime(item.getEndTime());
								if(device.isMailFilter()){
									System.out.println("ProfileStartTime= " + device.getProfileStartTime());
									System.out.println("ProfileEndime= " + device.getProfileEndTime());
									
								}
								dbDevice.updateDevMailFilter(device);
								dbDevice.updateDevProfileName(device);
								dbDevice.updateDevProfileStartTime(device);
								dbDevice.updateDevProfileEndTime(device);
								updated = true;
							}
						}
					}
				}
			}
		}

		return updated;
	}

	public String deviceMacRefresh(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		if (device != null) {
			String result = device.deviceMacRefresh(device);
			if (result == "success"){
				return "success";
			}
			else {
				return "fail";
			}
		} 
		else {
			System.out.println("No Such Device.");
			return "fail";
		}
	}

	/**
	 * Return specified device's link view information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JLinkViewItem> linkView(String ip) {
		JDevice device = findDeviceByIp(ip);

		return device.readLinkView();
	}

	/**
	 * Return specified device's port status information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JPortStatusItem> portStatus(String ip) {
		JDevice device = findDeviceByIp(ip);
		if (device == null) {
			return null;
		} else {
			return device.readPortStatus();
		}
	}

	public boolean setPortStatus(String ip, String columnOid, String[] dataArray) {
		JDevice device = findDeviceByIp(ip);

		JPortStatus portStatus = new JPortStatus();
		return portStatus.writeDevice(device, columnOid, dataArray);
	}

	public boolean setPortStatus_db(String ip, String columnOid, String[] dataArray) {
		JDevice device = findDeviceByIp(ip);

		JPortStatus portStatus = new JPortStatus();
		return portStatus.writeDevice_db(device, columnOid, dataArray);
	}

	/**
	 * Return specified device's port statistics information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JPortStatisticsItem> portStatistics(String ip) {
		JDevice device = findDeviceByIp(ip);
		if (device == null) {
			return null;
		} else {
			return device.readPortStatistics();
		}
	}
	
	/**
	 * Return specified device's EtherLike statistics information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JEtherlikeStatisticsItem> etherlikeStatistics(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readEtherlikeStatistics();
	}

	/**
	 * Return specified device's RMON information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JRmonStatisticsItem> rmonStatistics(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readRmonStatistics();
	}

	/**
	 * Return specified device's Static VlanList information.
	 * 
	 * @param ip
	 * @return
	 */

	public List<JStaticVlanListItem> staticvlanList(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readStaticVlan();
	}

	/**
	 * Return specified device's System information.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JSystemInfoTable> systemInfo() {
		JSystemInfo systemInfo = new JSystemInfo();
		return systemInfo.readDeviceAll(deviceList);
	}

	public JSystemInfoTable systemInfo(String ip) {
		JDevice device = findDeviceByIp(ip);

		JSystemInfo systemInfo = new JSystemInfo();
		return systemInfo.readDevice(device);
	}

	/**
	 * Return specified device's Current VlanList information.
	 * 
	 * @param ip
	 * @return
	 */

	public List<JCurrentVlanListItem> currentvlanList(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readCurrentVlan();
	}

	/**
	 * Return global VLAN status from specified device.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JVlanStatusItem> vlanStatus_global(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readGlobalVlanStatus();
	}

	/**
	 * Return interfaced VLAN status from specified device.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JVlanStatusItem> vlanStatus_interface(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readVlanStatus();
	}

	/**
	 * Set a column of data by it's OID to the specified device.
	 * 
	 * @param ip
	 * @param columnOid
	 * @param dataArray
	 * @return
	 */
	public boolean setVlanStatus(String ip, String columnOid, String[] dataArray) {
		JDevice device = findDeviceByIp(ip);

		JVlanStatus vlanStatus = new JVlanStatus();
		return vlanStatus.writeDevice(device, columnOid, dataArray);
	}

	/**
	 * Return global PoE status from specified device.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JPoeStatusItem> poeStatus_global(String ip) {
		JDevice device = findDeviceByIp(ip);
		
		return device.readGlobalPoeStatus();
	}

	/**
	 * Return interfaced PoE status from specified device.
	 * 
	 * @param ip
	 * @return
	 */
	public List<JPoeStatusItem> poeStatus_interface(String ip) {
		JDevice device = findDeviceByIp(ip);

		return device.readPoeStatus();
	}

	/**
	 * Set a column of data by it's OID to the specified device.
	 * 
	 * @param ip
	 * @param columnOid
	 * @param dataArray
	 * @return
	 */
	public boolean setPoeStatus(String ip, String columnOid, String[] dataArray) {
		JDevice device = findDeviceByIp(ip);

		JPoeStatus poeStatus = new JPoeStatus();
		return poeStatus.writeDevice(device, columnOid, dataArray);
	}

	/**
	 * Extract the topology of whole network.
	 * 
	 * @return
	 */
	public ArrayList<String[]> getTopologyTree(String rootIp, String displayType, String userCheck) {
		//updateDeviceMapper();

		/*for (JDevice device : deviceList) {
			if (!device.getDeviceType().equals("firewall")) // skip firewall currently
				device.updateTopologyInfo(deviceMapper);
		}*/
		//topology.updateTree();

		//return topology.generateHtmlAndScript();
		return topology.getHtmlAndScript(rootIp, displayType, userCheck);
	}
	
	public Map<String, ArrayList<String[]>> getRemoteTopologyTree() {
		Map<String, ArrayList<String[]>> htmlResult = new LinkedHashMap<String, ArrayList<String[]>>();
		if(nmsList == null) return null;
		
		for (JNms device : nmsList) {
			RemoteInterface Service = RemoteService.creatClient(device.getPublicIp(), 3);
    		if(Service != null) {
    			ArrayList<String[]> topologyTree = new ArrayList<String[]>();
    			try {
    				topologyTree = Service.getTopology();
    			} catch (RemoteException e) {
    				e.printStackTrace();
    			}
    			if(topologyTree.size() > 0) {
    				htmlResult.put(device.getAliasName(), topologyTree);
    			}
    		}
		}
		return htmlResult;
		
	}
	
	public static ArrayList<String[]> getTopology() {
		String rootIp = Config.getTopologyTopDevice();
		String displayType = "ip_name";
		String userCheck = "2";
		ArrayList<String[]> topologtTree = topology.getHtmlAndScript(rootIp, displayType, userCheck);
		return topologtTree;
	}

	/**
	 * Return all pairs of link from start device to end device.
	 * 
	 * @param startAddr
	 * @param endAddr
	 * @return Each element of the list composed by { 'IP of start device',
	 *         'Port of start device', 'IP of end device', 'Port of end
	 *         device'}.
	 */
	public List<String[][]> getTopologyPath(String startAddr, String endAddr) {

		// topology.refresh();

		return topology.getPathBetween(startAddr, endAddr);
	}
	
	public boolean updateTopologyInfo() {
		
		System.out.println("<<<Start to update topology information.>>>");
		for (JDevice device : deviceList) {
			if (device.getSnmpSupport() < 1)
				continue;
			device.updateTopologyInfo();
		}
		System.out.println("<<<Stop update topology information.>>>");
		
		return true;
		
	}
	
	public boolean setRemoteAccountItems(String ip, String userItmes) {
		if(ip == null || userItmes == null) return false;
		
		for (JNms device : nmsList) {
			if(ip.equals(device.getPublicIp())){
				device.setRemoteMember(userItmes);
				dbNms.updateRemoteAccountSetItems(device);
				return true;
			}
		}

		return false;
	}

	public boolean setDeviceRemote(String ip, int port, boolean isManual, String remoteIp/*, boolean isMonitored, boolean isPoePower, String poeScheduleName*/) {
		JDevice currDevice = findDeviceByIp(ip);
		JDevice remoDevice = findDeviceByIp(remoteIp); // null is allow
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("device is not found by this IP:" + ip + ", " + port);
			return isDone;
		}
		/*List<JPoeSchedule> poeList = dbPoeSchedule.getPoeItems();
		currDevice.setPoePower(true);
		dbDevice.updatePoePower(currDevice);*/
		
		/*if (port < 1 || port > currDevice.getInfNum()) {
			System.out.println("input port is not correct for this device:" + ip + ", " + port);
			return isDone;
		}*/

		for (JInterface inf : currDevice.getInterfaces()) {
			if (inf.getIfIndex() == port) {
				inf.setManual(isManual);
				if (isManual) { // if set to auto, manualRemote is ignored and use lldpRemote
					inf.setManualRemoteIp(remoDevice == null ? "" : remoteIp); // when user set it to 'none' or the given ip is not found in db
					inf.setManualRemoteId(remoDevice == null ? "" : remoDevice.getPhyAddr());
				}
				/*inf.setPoePower(isPoePower);
				if (isPoePower) { //Set to poe schedule on.
					inf.setPoeScheduleName(poeScheduleName);
					for (JPoeSchedule poe : poeList) {
						if(poe.getPoeScheduleName().equals(inf.getPoeScheduleName())){
							inf.setPoeStartTime(poe.getStartTime());
							//inf.setPoeStartStatus("1");
							inf.setPoeEndTime(poe.getEndTime());
							//inf.setPoeEndStatus("2");
							System.out.println("PoeStartTime="+inf.getPoeStartTime());
							System.out.println("PoeStartTime="+inf.getPoeEndTime());
							
						}
					}
				}
				inf.setMonitored(isMonitored);*/
				isDone = true;
				break;
			}
		}
		if (isDone) {
			dbDevice.updateInterfaceIsManual(currDevice);
			dbDevice.updateInterfaceManualRemoteIp(currDevice);
			/*dbDevice.updateInterfaceIsMonitored(currDevice);
			dbDevice.updateInterfaceIsPoePower(currDevice);
			dbDevice.updateInterfacePoeScheduleName(currDevice);
			dbDevice.updateInterfacePoeStartTime(currDevice);
			dbDevice.updateInterfacePoeEndTime(currDevice);*/
		} else {
			System.out.println("Interface set is not success. IP:" + ip + ", " + port);
		}

		return isDone;
	}
	
	public boolean setPoeSchedule(String ip, int port, boolean isPoePower, String poeScheduleName) {
		JDevice currDevice = findDeviceByIp(ip);
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("device is not found by this IP:" + ip + ", " + port);
			return isDone;
		}
		List<JPoeSchedule> poeList = dbPoeSchedule.getPoeItems();
		currDevice.setPoePower(true);
		dbDevice.updatePoePower(currDevice);
		
		/*if (port < 1 || port > currDevice.getInfNum()) {
			System.out.println("input port is not correct for this device:" + ip + ", " + port);
			return isDone;
		}*/

		for (JInterface inf : currDevice.getInterfaces()) {
			if (inf.getIfIndex() == port) {
				inf.setPoePower(isPoePower);
				if (isPoePower) { //Set to poe schedule on.
					inf.setPoeScheduleName(poeScheduleName);
					for (JPoeSchedule poe : poeList) {
						if(poe.getPoeScheduleName().equals(inf.getPoeScheduleName())){
							inf.setPoeStartTime(poe.getStartTime());
							inf.setPoeEndTime(poe.getEndTime());
							if(inf.isPoePower()){
								System.out.println("PoeStartTime= " + inf.getPoeStartTime());
								System.out.println("PoeEndime= " + inf.getPoeEndTime());
							}
							
						}
					}
				}
				isDone = true;
				break;
			}
		}
		if (isDone) {
			dbDevice.updateInterfaceIsPoePower(currDevice);
			dbDevice.updateInterfacePoeScheduleName(currDevice);
			dbDevice.updateInterfacePoeStartTime(currDevice);
			dbDevice.updateInterfacePoeEndTime(currDevice);
		} else {
			System.out.println("POE schedule set is not success. IP:" + ip + ", " + port);
		}

		return isDone;
	}
	
	public void setPoePowerStatus(String ip) {
		JDevice currDevice = findDeviceByIp(ip);
		currDevice.setPoePower(false);
		dbDevice.updatePoePower(currDevice);
		System.out.println("No poe schedule at " + ip);
		
	}
	
	public boolean setMonitor(String ip, int port, boolean isMonitored) {
		JDevice currDevice = findDeviceByIp(ip);
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("device is not found by this IP:" + ip + ", " + port);
			return isDone;
		}
		
		/*if (port < 1 || port > currDevice.getInfNum()) {
			System.out.println("input port is not correct for this device:" + ip + ", " + port);
			return isDone;
		}*/

		for (JInterface inf : currDevice.getInterfaces()) {
			if (inf.getIfIndex() == port) {
				inf.setMonitored(isMonitored);
				isDone = true;
				break;
			}
		}
		if (isDone) {
			dbDevice.updateInterfaceIsMonitored(currDevice);
		} else {
			System.out.println("Monitor set is not success. IP:" + ip + ", " + port);
		}

		return isDone;
	}
	
	public boolean setDevProfile(String ip, boolean isMailFilter, String profileName) {
		JDevice currDevice = findDeviceByIp(ip);
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("Device is not found by this IP:" + ip);
			return isDone;
		}
		List<JProfile> list = dbProfile.getProfileItems();
		
		if(isMailFilter){
			currDevice.setMailFilter(isMailFilter);
			currDevice.setProfileName(profileName);
			for (JProfile item : list) {
				if(item.getProfileName().equals(currDevice.getProfileName())){
					currDevice.setProfileStartTime(item.getStartTime());
					currDevice.setProfileEndTime(item.getEndTime());
					if(currDevice.isMailFilter()){
						System.out.println("ProfileStartTime= " + currDevice.getProfileStartTime());
						System.out.println("ProfileEndime= " + currDevice.getProfileEndTime());
						isDone = true;
					}
					
				}
			}
			
		}
		else {
			currDevice.setMailFilter(false);
			currDevice.setProfileName("");
			isDone = true;
		}
		if (isDone) {
			dbDevice.updateDevMailFilter(currDevice);
			dbDevice.updateDevProfileName(currDevice);
			dbDevice.updateDevProfileStartTime(currDevice);
			dbDevice.updateDevProfileEndTime(currDevice);
		} else {
			System.out.println("Device profile schedule set is not success. IP:" + ip);
		}

		return isDone;
	}
	
	public boolean setInfProfile(String ip, int port, boolean isMailFilter, String profileName) {
		JDevice currDevice = findDeviceByIp(ip);
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("Device is not found by this IP:" + ip + ", " + port);
			return isDone;
		}
		List<JProfile> list = dbProfile.getProfileItems();

		for (JInterface inf : currDevice.getInterfaces()) {
			if (inf.getIfIndex() == port) {
				inf.setMailFilter(isMailFilter);
				if (isMailFilter) { //Set to poe schedule on.
					inf.setProfileName(profileName);
					for (JProfile item : list) {
						if(item.getProfileName().equals(inf.getProfileName())){
							inf.setProfileStartTime(item.getStartTime());
							inf.setProfileEndTime(item.getEndTime());
							if(inf.isMailFilter()){
								System.out.println("ProfileStartTime= " + inf.getProfileStartTime());
								System.out.println("ProfileEndime= " + inf.getProfileEndTime());
							}
							
						}
					}
				}
				isDone = true;
				break;
			}
		}
		if (isDone) {
			dbDevice.updateInfMailFilter(currDevice);
			dbDevice.updateInfProfileName(currDevice);
			dbDevice.updateInfProfileStartTime(currDevice);
			dbDevice.updateInfProfileEndTime(currDevice);
		} else {
			System.out.println("Profile schedule set is not success. IP:" + ip + ", " + port);
		}

		return isDone;
	}
	
	public List<JTrapStatusItem> getTrapStatus(String ip) {
		JDevice device = findDeviceByIp(ip);

		JTrapStatus trapStatus = new JTrapStatus();
		return trapStatus.readDevice(device);
	}
	
	public boolean setTrapStatus(String ip, String trapAddr, String trapVersion, String trapCommunity, String trapPort) {
		JDevice device = findDeviceByIp(ip);
		
		JTrapStatus trapStatus = new JTrapStatus();
		return trapStatus.writeDevice(device, trapAddr, trapVersion, trapCommunity, trapPort);
	}
	
	public boolean removeTrap(String ip, String[] ipList) {
		JDevice device = findDeviceByIp(ip);

		JTrapStatus trapStatus = new JTrapStatus();
		return trapStatus.deleteDevice(device, ipList);
	}
	
	public boolean setInfAliasName(String ip, int port, String portAliasName) {
		JDevice currDevice = findDeviceByIp(ip);
		boolean isDone = false;
		if (currDevice == null) {
			System.out.println("device is not found by this IP:" + ip + ", " + port);
			return isDone;
		}
		
		for (JInterface inf : currDevice.getInterfaces()) {
			if (inf.getIfIndex() == port) {
				inf.setAliasName(portAliasName);
				
				isDone = true;
				break;
			}
		}
		if (isDone) {
			dbDevice.updateInfAliasName(currDevice);
		} else {
			System.out.println("Interface profile set is not success. IP:" + ip + ", " + port);
		}

		return isDone;
	}

	public int getDeviceEtherPortNum(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			return -1;
		} else {
			return device.getRj45Num() + device.getFiberNum();
		}
	}
	
	public String setAlarmEmail(String[] dataArray){

		String alarmStr="";
		for(int i=0; i<dataArray.length; i++){
			if(!dataArray[i].equals(" ")){
				alarmStr+=dbAccount.getAlarmEmail(dataArray[i])+",";
			}
		}
		
		return alarmStr;
		
	}
	
    public String setAlarmSms(String[] dataArray){
		
    	String alarmStr="";
		for(int i=0; i<dataArray.length; i++){
			if(!dataArray[i].equals(" ")){
				alarmStr+=dbAccount.getAlarmSms(dataArray[i])+",";
			}
		}
		
		return alarmStr;
		
	}
    
   public String setAlarmCheck(String[] dataArray){
		
	   String checkStr="";
		for(int i=0; i<dataArray.length; i++){
			if(dataArray[i].equals(" ")){
				checkStr+="0,";
			}else
				checkStr+="1,";
		}
		
		return checkStr;
		
	}
   
   public String setAlarmUser(String[] dataArray){
		
	   String userStr="";
		for(int i=0; i<dataArray.length; i++){
			userStr+=dataArray[i]+",";
		}
		
		return userStr;
		
	}
   
   public static String setAlarmEmailStr(String data){
	   String[] dataArray = data.split(",");
	   
	   String alarmStr="";
	   for(int i=0; i<dataArray.length; i++){
		   if(!dataArray[i].equals(" ")){
			   alarmStr+=dbAccount.getAlarmEmail(dataArray[i])+",";
		   }
	   }
		
	   return alarmStr;
		
   }
	
   public static String setAlarmSmsStr(String data){
	   String[] dataArray = data.split(",");
	   
	   String alarmStr="";
	   for(int i=0; i<dataArray.length; i++){
		   if(!dataArray[i].equals(" ")){
			   alarmStr+=dbAccount.getAlarmSms(dataArray[i])+",";
		   }
	   }
		
	   return alarmStr;
		
	}
	
	public String setPoeSchedul(boolean isPoeManual, String ip, String poeStartTime, String poeStartStatus, String poeEndTime, String poeEndStatus) {
		JDevice currDevice = findDeviceByIp(ip);
		
		currDevice.setPoeManual(isPoeManual);
		currDevice.setPoeStartTime(poeStartTime);
		currDevice.setPoeStartStatus(poeStartStatus);
		currDevice.setPoeEndTime(poeEndTime);
		currDevice.setPoeEndStatus(poeEndStatus);
		
		if(!isPoeManual){
			currDevice.setPoeStartTime("00:00");
			currDevice.setPoeStartStatus("1");
			currDevice.setPoeEndTime("00:00");
			currDevice.setPoeEndStatus("1");
		}
		
		return "success";
	}
	
	public String addPoeSchedule(final String schedule_name, final String poeStartTime, final String poeEndTime) {
		if (dbPoeSchedule.isPoeScheduleExisted(schedule_name)) {
			return "Repeat";
		}
		
		if (dbPoeSchedule.addPoeSchedule(schedule_name, poeStartTime, poeEndTime)) {
			return "Success";
		}
		else {
			return "Failed.";
		}
	}
	
	public final String[][] viewPoeSchedule() {
        return dbPoeSchedule.getviewPoeSchedule();
    }
	
	public boolean removePoeScheduleItems(String[] poeList) {
		if (poeList == null){
			return false;
			
		} else {
			for (String poeItem : poeList) {
				for (JDevice device : deviceList){
					if(device.isSupportPoe()){
						for (JInterface inf : device.getInterfaces()){
							if(inf.isPoePower() && inf.getPoeScheduleName().equals(poeItem)){
								inf.setPoePower(false);
								inf.setPoeScheduleName("");
								inf.setPoeStartTime("");
								inf.setPoeEndTime("");
							}
						}
					}
				}
					// Remove record from device table
					if (dbPoeSchedule.removePoeScheduleItems(poeItem)) {
						System.out.println("Poe Schedule Item " + poeItem + " remove from db success.");
					}
			}
			for (JDevice device : deviceList){
				dbDevice.updateInterfaceIsPoePower(device);
				dbDevice.updateInterfacePoeScheduleName(device);
				dbDevice.updateInterfacePoeStartTime(device);
				dbDevice.updateInterfacePoeEndTime(device);
			}
			
		return true;
		
		}
		
	}
	
	public String addProfile(final String profile_name, final String profileStartTime, final String profileEndTime) {
		if (dbProfile.isProfileExisted(profile_name)) {
			return "Repeat";
		}
		
		if (dbProfile.addProfile(profile_name, profileStartTime, profileEndTime)) {
			return "Success";
		}
		else {
			return "Failed.";
		}
	}
	
	public final String[][] viewProfile() {
        return dbProfile.getViewProfile();
    }
	
	public boolean removeProfileItems(String[] profileList) {
		if (profileList == null){
			return false;
			
		} else {
			for (String item : profileList) {
				for (JDevice device : deviceList){
					if(device.getProfileName() != null && device.getProfileName().equals(item)){
						device.setMailFilter(false);
						device.setProfileName("");
						device.setProfileStartTime("");
						device.setProfileEndTime("");
					}
					for (JInterface inf : device.getInterfaces()){
						if(inf.getProfileName() != null && inf.getProfileName().equals(item)){
							inf.setMailFilter(false);
							inf.setProfileName("");
							inf.setProfileStartTime("");
							inf.setProfileEndTime("");
						}
					}
				}
					// Remove record from device table
					if (dbProfile.removeProfileItems(item)) {
						System.out.println("Profile Item " + item + " remove from db success.");
					}
			}
			for (JDevice device : deviceList){
				dbDevice.updateDevMailFilter(device);
				dbDevice.updateDevProfileName(device);
				dbDevice.updateDevProfileStartTime(device);
				dbDevice.updateDevProfileEndTime(device);
				dbDevice.updateInfMailFilter(device);
				dbDevice.updateInfProfileName(device);
				dbDevice.updateInfProfileStartTime(device);
				dbDevice.updateInfProfileEndTime(device);
			}
			
		return true;
		
		}
		
	}
	
	public static String[][] viewNotify(String acPhyaddr) {
        return JDbNotify.getNotify(acPhyaddr);
    }
	
	public static String[][] viewNotifyAc(String acPhyaddr) {
        return JDbNotify.getNotifyAc(acPhyaddr);
    }
	
	public static boolean checkNotify(String phyAddr, String acip, 
			String join_mail_en, String join_mail_list, String join_sms_en, String join_sms_list,
			String leav_mail_en, String leav_mail_list, String leav_sms_en, String leav_sms_list,
			String warm_mail_en, String warm_mail_list, String warm_sms_en, String warm_sms_list,
			String cold_mail_en, String cold_mail_list, String cold_sms_en, String cold_sms_list
			) {
		if (JDbNotify.isNotifyExist(phyAddr)) {
			if (JDbNotify.updateNotify(phyAddr, acip, join_mail_en, join_mail_list, join_sms_en, join_sms_list, 
					leav_mail_en, leav_mail_list, leav_sms_en, leav_sms_list,
					warm_mail_en, warm_mail_list, warm_sms_en, warm_sms_list,
					cold_mail_en, cold_mail_list, cold_sms_en, cold_sms_list
					))
			{
				return true;
			} else {
				return false;
			}
		} else {
			if (JDbNotify.addNotify(phyAddr, acip, join_mail_en, join_mail_list, join_sms_en, join_sms_list, 
					leav_mail_en, leav_mail_list, leav_sms_en, leav_sms_list,
					warm_mail_en, warm_mail_list, warm_sms_en, warm_sms_list,
					cold_mail_en, cold_mail_list, cold_sms_en, cold_sms_list
					))
			{
				return true;
			} else {
				return false;
			}
		}
//		return true;
    }
	
	
	public List<JAccount> accountItems() {
		return dbAccount.getAccountItems();
	}
	
	public List<JAccount> accountAllItems() {
		return dbAccount.getAccountAllItems();
	}
	
	public List<JAccount> remoteAccountSetItems() {
		return dbAccount.getRemoteAccountSetItems();
	}

	public int[] chart1(String ip, int port) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return new int[] { 0, 0, 0 };
		} else {
			return device.getChart1Data(port);
		}
	}

	public int[] chart2(String ip, int port) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return new int[] { 0, 0 };
		} else {
			return device.getChart2Data(port);
		}
	}

	public long[] getRxTxPacket(String ip, int port) {
		JDevice device = findDeviceByIp(ip);
		long[] output = null;

		if (device == null) {
			System.out.println("device not found.");
			output = new long[] { 0, 0, 0 };
		} else {
//			return device.getChart2Data(port);
			long[] data = device.getRxTxPacket(port);
			output = data;
		}
		return output;

	}
	
	public long[] getRxTxOctet(String ip, int port) {
		JDevice device = findDeviceByIp(ip);
		long[] output = null;

		if (device != null) {
			long[] data = device.getRxTxOctet(port);
			/*
			 * float[] dataInKbps = new float[]{0, 0}; if (data.length == 2) {
			 * dataInKbps[0] = data[0] - rxOctet > 0 ? ((data[0] - rxOctet) * 8)
			 * / (float)period : 0; dataInKbps[1] = data[1] - txOctet > 0 ?
			 * ((data[1] - txOctet) * 8) / (float)period : 0; }
			 */
			output = data;
		} else {
			System.out.println("No Such Device.");
		}

		return output;
	}
	
	public long[] getAplistRxTxOctet(String acip, String apip) {
		JDevice device = findDeviceByIp(acip);
		long[] output = null;

		if (device != null) {
			long[] data = device.getAplistRxTxOctet(apip);
			output = data;
		} else {
			System.out.println("Aplist : No Such Device.");
		}

		return output;
	}

	public long[] getApSsidlistRxTxOctet(String acip, String apip, int apssidindex) {
		JDevice device = findDeviceByIp(acip);
		long[] output = null;

		if (device != null) {
			long[] data = device.getApSsidlistRxTxOctet(apip, apssidindex);
			output = data;
		} else {
			System.out.println("ApSsidlist : No Such Device.");
		}

		return output;
	}

	public long[] getRxTxOctetAll(String ip, String rxtxSelect) {
		JDevice device = findDeviceByIp(ip);
		long[] output = null;

		if (device != null) {
			output = device.getRxTxOctetAll(rxtxSelect);
		} else {
			System.out.println("No Such Device.");
		}

		return output;
	}

	public String hostCpu(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return null;
		} else {
			return device.getHostCpu();
		}
	}

	public static String RemotehostCpu() {
		String cpu = "";

		String Community = "public";

		try {
			if (JSystem.osName.equals("Linux")) {
				String line = "";

				try {
					String[] cmd = { "/bin/sh", "-c", "cat /etc/snmp/snmpd.conf | grep 'com2sec ro default'" };
					Process Process_bak = Runtime.getRuntime().exec(cmd);
					BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_bak.readLine()) != null) {
						//System.out.println(line);
						Community = line.split(" ")[3];
					}
					BufferedReader_bak.close();
					Process_bak.waitFor();
				} catch (Exception e) {
					System.out.println("cat /etc/snmp/snmpd.conf | grep 'com2sec ro default' 發生 IOException");
					//e.printStackTrace();
				}
			}

			JSnmp snmp = new JSnmp("127.0.0.1/161", Community, 2, 2000, 1);
			snmp.start();
			int b = 0;
			Map<String, List<String>> hrProcessorResult;
			final int ProcessorLoad = b++;
			String[] hrProcessorOids = { JOid.hrProcessorLoad };
			hrProcessorResult = snmp.getTable(hrProcessorOids);

			snmp.end();
			JMib.printRawMap(hrProcessorResult, false);

			if (hrProcessorResult != null && hrProcessorResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrProcessorResult.entrySet()) {

					String ifKey = entry.getKey();
					if (hrProcessorResult.containsKey(ifKey)) {
						cpu += hrProcessorResult.get(ifKey).get(ProcessorLoad) + ",";
					}
				}
			}

			//System.out.println(cpu);
		} catch (IOException e) {
			System.out.println("Read system CPU information failed.");
			return cpu;
		}

		return cpu;
	}

	public String hostMemory(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return null;
		} else {
			return device.getHostMemory();
		}
	}

	public static String RemotehostMemory() {
		String memory = "";

		String Community = "public";

		try {
			if (JSystem.osName.equals("Linux")) {
				String line = "";

				try {
					String[] cmd = { "/bin/sh", "-c", "cat /etc/snmp/snmpd.conf | grep 'com2sec ro default'" };
					Process Process_bak = Runtime.getRuntime().exec(cmd);
					BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_bak.readLine()) != null) {
						//System.out.println(line);
						Community = line.split(" ")[3];
					}
					BufferedReader_bak.close();
					Process_bak.waitFor();
				} catch (Exception e) {
					System.out.println("cat /etc/snmp/snmpd.conf | grep 'com2sec ro default' 發生 IOException");
					//e.printStackTrace();
				}
			}

			JSnmp snmp = new JSnmp("127.0.0.1/161", Community, 2, 2000, 1);
			snmp.start();

			int a = 0;
			Map<String, List<String>> hrStorageResult;
			String hrStorageRam = "1.3.6.1.2.1.25.2.1.2";
			final int StorageType = a++, StorageDescr = a++, hrStorageAllocationUnits=a++, StorageSize = a++, StorageUsed = a++;

			String[] hrStorageOids = { JOid.hrStorageType, JOid.hrStorageDescr, JOid.hrStorageAllocationUnits, JOid.hrStorageSize, JOid.hrStorageUsed };
			hrStorageResult = snmp.getTable(hrStorageOids);

			Double PhysicalMemorySize =0.0;
			Double PhysicalMemoryUsed=0.0;
			if (hrStorageResult != null && hrStorageResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrStorageResult.entrySet()) {
					String ifKey = entry.getKey();
					//		    		System.out.println("hrStorageResult Index= " + ifKey); 
					if (hrStorageResult.containsKey(ifKey)) {
						//hrStorageResult.get(ifKey).get(StorageDescr); 
						memory += hrStorageResult.get(ifKey).get(StorageSize) + "," + hrStorageResult.get(ifKey).get(StorageUsed) + ",";
					}
					
					if (snmp.getNode(JOid.sysObjectID).indexOf("1.3.6.1.4.1.311.1.1.3") != -1) {						
						if (hrStorageResult.get(ifKey).get(StorageType).equals(hrStorageRam)) {
							PhysicalMemorySize = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageSize)) * Double.parseDouble(hrStorageResult.get(ifKey).get(hrStorageAllocationUnits)) / 1024;
							PhysicalMemoryUsed = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUsed)) * Double.parseDouble(hrStorageResult.get(ifKey).get(hrStorageAllocationUnits)) / 1024;
						}
						
						memory = PhysicalMemorySize + "," + PhysicalMemoryUsed + ",0,0,0,0,0";
					}
				}

				snmp.end();
			}

			//System.out.println(memory);
		} catch (IOException e) {
			System.out.println("Read system Memory information failed.");
			return memory;
		}

		return memory;
	}
	
	public String edgecoreCpu(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return null;
		} else {
			return device.getEdgeCoreCpu();
		}
	}

	public String edgecoreMemory(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return null;
		} else {
			return device.getEdgeCoreMemory();
		}
	}
	
	public String hostDisk(String ip) {
		JDevice device = findDeviceByIp(ip);

		if (device == null) {
			System.out.println("device not found.");
			return null;
		} else {
			return device.getHostDisk();
		}
	}
	
	public static String RemotehostDisk() {
		String disk = "";

		String Community = "public";

		try {
			if (JSystem.osName.equals("Linux")) {
				String line = "";

				try {
					String[] cmd = { "/bin/sh", "-c", "cat /etc/snmp/snmpd.conf | grep 'com2sec ro default'" };
					Process Process_bak = Runtime.getRuntime().exec(cmd);
					BufferedReader BufferedReader_bak = new BufferedReader(new InputStreamReader(Process_bak.getInputStream(), "UTF-8"));
					while ((line = BufferedReader_bak.readLine()) != null) {
						//System.out.println(line);
						Community = line.split(" ")[3];
					}
					BufferedReader_bak.close();
					Process_bak.waitFor();
				} catch (Exception e) {
					System.out.println("cat /etc/snmp/snmpd.conf | grep 'com2sec ro default' 發生 IOException");
					//e.printStackTrace();
				}
			}

			JSnmp snmp = new JSnmp("127.0.0.1/161", Community, 2, 2000, 1);
			snmp.start();

			int a = 0;
			Map<String, List<String>> hrStorageResult;
			String hrStorageFixedDisk = "1.3.6.1.2.1.25.2.1.4";
			final int StorageType = a++, StorageUnit = a++, StorageSize = a++, StorageUsed = a++;

			String[] hrStorageOids = { JOid.hrStorageType, JOid.hrStorageAllocationUnits, JOid.hrStorageSize, JOid.hrStorageUsed };

			hrStorageResult = snmp.getTable(hrStorageOids);

			snmp.end();

			JMib.printRawMap(hrStorageResult, false);

			if (hrStorageResult != null && hrStorageResult.size() != 0) {
				for (Map.Entry<String, List<String>> entry : hrStorageResult.entrySet()) {
					String ifKey = entry.getKey();
					//		    		System.out.println("hrStorageResult Index= " + ifKey); 
					if (hrStorageResult.containsKey(ifKey)) {
						if (hrStorageResult.get(ifKey).get(StorageType).equals(hrStorageFixedDisk)) {
							double disk_size = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUnit)) * Double.parseDouble(hrStorageResult.get(ifKey).get(StorageSize));
							double disk_used = Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUnit)) * Double.parseDouble(hrStorageResult.get(ifKey).get(StorageUsed));
							disk += String.valueOf(disk_size) + "," + String.valueOf(disk_used) + ",";
						} else {
							continue;
						}
					}
				}
			}

			//System.out.println(memory);
		} catch (IOException e) {
			System.out.println("Read system Memory information failed.");
			return disk;
		}

		return disk;
	}
	
	public String[] getDeviceReportTime() {
		return dbStatistics.getDeviceReportTime();
	}

	public String[] getHistoryBoundaryTime(String ipAddr, String ifIndex) {
		JDevice device = findDeviceByIp(ipAddr);

		if (device != null)
			return dbStatistics.getBoundaryTimeByInterface(ipAddr, ifIndex);
		else
			return null;
	}
	
	public String[] getHistoryApListBoundaryTime(String acIp, String ip) {
		JDevice device = findDeviceByIp(acIp);

		if (device != null)
			return dbApList.getBoundaryTimeByPort(ip);
		else
			return null;
	}
	
	public String[] getHistoryApSsidListBoundaryTime(String acIp, String ip, String ssid) {
		JDevice device = findDeviceByIp(acIp);

		if (device != null)
			return dbApSsidList.getBoundaryTimeByPort(ip, ssid);
		else
			return null;
	}

	public String[][] getRxTxOctetRateHistory(String ipAddr, String ifIndex, String startTime, String endTime, int pointNumber) {
		JDevice device = findDeviceByIp(ipAddr);

		if (device != null) {
			return dbStatistics.getRxTxOctetRateHistory(ipAddr, ifIndex, startTime, endTime, device.isOctet64());
		}

		return null;
	}
	
	public ArrayList<JDeviceReport> getRxTxOctetRaterReport(String[] ipItems, String startTime, String endTime) {

		if (ipItems != null) {
			return dbStatistics.getRxTxOctetRateReport(deviceList, ipItems, startTime, endTime);
		}

		return null;
	}
	
	public ArrayList<JInterfaceTerminalReport> getTerminalRxTxOctetRaterReport(String[] ipItems, String startTime, String endTime) {

		if (ipItems != null) {
			return dbStatistics.getTerminalRxTxOctetRateReport(deviceList, ipItems, startTime, endTime);
		}

		return null;
	}
	
	public String[][] getApListRxTxOctetRateHistory(String acIp, String ip, String startTime, String endTime, int pointNumber) {
		//JDevice device = findDeviceByIp(ip);

		//if (device != null) {
			return dbApList.getRxTxOctetRateHistory(acIp, ip, startTime, endTime);
		//}

		//return null;
	}
	
	public String[][] getApSsidListRxTxOctetRateHistory(String acIp, String ip, String port, String startTime, String endTime, int pointNumber) {
		//JDevice device = findDeviceByIp(ip);

		//if (device != null) {
			return dbApSsidList.getRxTxOctetRateHistory(acIp, ip, port, startTime, endTime);
		//}

		//return null;
	}
	
	public String[][] getHistoryApList(String acIp) {
		JDevice device = findDeviceByIp(acIp);

		if (device != null)
			return dbApSsidList.getHistoryApList(acIp);
		else
			return null;
	}
	
	public String[][] getHistoryApSsidList(String acIp, String apIp) {
		JDevice device = findDeviceByIp(acIp);

		if (device != null)
			return dbApSsidList.getHistoryApSsidList(acIp, apIp);
		else
			return null;
	}

	public String login(String username, String password) {
		return login.login(username, password);
	}
	
	public String authlogin(String username, String password) {
		String output = null;
		String level = null;
		String[] result = dbAccount.getAccountInfo(username);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		if (result != null && result[0].equalsIgnoreCase(username) && result[1].equals(password)) {
			level = result[2];

			if(level.equals("admin")){
				output = "1";
			}
			else if(level.equals("user")){
				output = "2";
			}
			else{
				output = "0";
			}
			dbAccount.updateLoginDate(username, sdf.format(new Date()));
		}
		return output;
	}
	
	public String[] rmiRemoteLogin(String ip) {
		String[] output = new String[2];
		String[] result = null;
		
		RemoteInterface Service = RemoteService.creatClient(ip, 3);
		
		if(Service != null) {
			
			for(JNms device : nmsList){
				if(ip.equals(device.getPublicIp())){
					try {
						result = Service.getAccountInfo(device.getRemoteAccount());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (result != null && result[0].equalsIgnoreCase(device.getRemoteAccount()) && result[1].equals(device.getRemotePwd())) {
						
						output[0] = result[4];
						output[1] = result[5];
					}
				}
			}
			
			
		}
		return output;
	}
	
	public static String[] getAccountInfo(final String username) {
		String url = "";
		if(JSystem.osDistribution.equals("CentOS")){
			url = "/nms";
		}
		String[] originalResult = dbAccount.getAccountInfo(username);
		if(originalResult != null){
			Random ran = new Random();
			String ranCode = Integer.toString(ran.nextInt(99999)+10000);
			
			String newResult[] = Arrays.copyOf(originalResult, originalResult.length + 2);
			newResult[4] = ranCode;
			newResult[5] = url;
			
			JRemoteAuth remote = new JRemoteAuth();
			remote.setRemoteInfo(newResult[4], originalResult[0], originalResult[1]);
			return newResult;
		}
		return null;
	}

	public String logout(String username) {
		return login.logout(username);
	}
	
	public int checkIsManage(String username) {
		return login.checkIsManage(username);
	}
	
	public String sessionId(String username, String session_id) {
		return login.sessionId(username, session_id);
	}
	
	public String checkSessionId(String username, String session_id) {
		return login.checkSessionId(username, session_id);
	}

	public String checkUserName(String username) {
		return login.checkUsername(username);
	}

	public String addNewAccount(String username, String password, String level, String name, String email, String phone_number, String is_manage, String is_remote) {
		return login.newAccount(username, password, level, name, email, phone_number, is_manage, is_remote);
	}

	public String[] viewAccount(String username) {
		return login.viewAccount(username);
	}

	public String updatePassword(String username, String bef_password, String update_password) {
		return login.updatePassword(username, bef_password, update_password);
	}

	public String updateName(String username, String name) {
		return login.updateName(username, name);
	}

	public String updateEmail(String username, String email) {
		return login.updateEmail(username, email);
	}
	
	public String updatePhoneNumber(String username, String phoneNumber) {
		return login.updatePhoneNumber(username, phoneNumber);
	}

	public String[][] viewAllUserAccount(Object userCheck) {
		return login.viewAllUserAccount(userCheck);
	}
	
	public boolean removeAccount(String[] accountList) {
		if (accountList == null){
			return false;
		} else {
			
			for (String accountItem : accountList) {
					String data1 = "";
					if(Arrays.asList(JAlarm.getNmsStartUpSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getNmsStartUpSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data1 += mail + ",";
							}
						}
						JAlarm.setNmsStartUpSendEmailList(data1);
					}
					
					String data2 = "";
					if(Arrays.asList(JAlarm.getNmsStartUpSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getNmsStartUpSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data2 += mail + ",";
							}
						}
						JAlarm.setNmsStartUpSendSmsList(data2);
					}
					
					String data3 = "";
					if(Arrays.asList(JAlarm.getNmsShutDownSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getNmsShutDownSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data3 += mail + ",";
							}
						}
						JAlarm.setNmsShutDownSendEmailList(data3);
					}
					
					String data4 = "";
					if(Arrays.asList(JAlarm.getNmsShutDownSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getNmsShutDownSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data4 += mail + ",";
							}
						}
						JAlarm.setNmsShutDownSendSmsList(data4);
					}
					
					String data5 = "";
					if(Arrays.asList(JAlarm.getDeviceDisconnectSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getDeviceDisconnectSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data5 += mail + ",";
							}
						}
						JAlarm.setDeviceDisconnectSendEmailList(data5);
					}
					
					String data6 = "";
					if(Arrays.asList(JAlarm.getDeviceDisconnectSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getDeviceDisconnectSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data6 += mail + ",";
							}
						}
						JAlarm.setDeviceDisconnectSendSmsList(data6);
					}
					
					String data7 = "";
					if(Arrays.asList(JAlarm.getMonitoredPortLinkUpSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getMonitoredPortLinkUpSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data7 += mail + ",";
							}
						}
						JAlarm.setMonitoredPortLinkUpSendEmailList(data7);
					}
					
					String data8 = "";
					if(Arrays.asList(JAlarm.getMonitoredPortLinkUpSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getMonitoredPortLinkUpSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data8 += mail + ",";
							}
						}
						JAlarm.setMonitoredPortLinkUpSendSmsList(data8);
					}
					
					String data9 = "";
					if(Arrays.asList(JAlarm.getMonitoredPortLinkDownSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getMonitoredPortLinkDownSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data9 += mail + ",";
							}
						}
						JAlarm.setMonitoredPortLinkDownSendEmailList(data9);
					}
					
					String data10 = "";
					if(Arrays.asList(JAlarm.getMonitoredPortLinkDownSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getMonitoredPortLinkDownSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data10 += mail + ",";
							}
						}
						JAlarm.setMonitoredPortLinkDownSendSmsList(data10);
					}
					
					String data11 = "";
					if(Arrays.asList(JAlarm.getCriticalPortLinkUpSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getCriticalPortLinkUpSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data11 += mail + ",";
							}
						}
						JAlarm.setCriticalPortLinkUpSendEmailList(data11);
					}
					
					String data12 = "";
					if(Arrays.asList(JAlarm.getCriticalPortLinkUpSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getCriticalPortLinkUpSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data12 += mail + ",";
							}
						}
						JAlarm.setCriticalPortLinkUpSendSmsList(data12);
					}
					
					String data13 = "";
					if(Arrays.asList(JAlarm.getCriticalPortLinkDownSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getCriticalPortLinkDownSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data13 += mail + ",";
							}
						}
						JAlarm.setCriticalPortLinkDownSendEmailList(data13);
					}
					
					String data14 = "";
					if(Arrays.asList(JAlarm.getCriticalPortLinkDownSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getCriticalPortLinkDownSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data14 += mail + ",";
							}
						}
						JAlarm.setCriticalPortLinkDownSendSmsList(data14);
					}
					
					String data15 = "";
					if(Arrays.asList(JAlarm.getWebUpdatedSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getWebUpdatedSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data15 += mail + ",";
							}
						}
						JAlarm.setWebUpdatedSendEmailList(data15);
					}
					
					String data16 = "";
					if(Arrays.asList(JAlarm.getWebUpdatedSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getWebUpdatedSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data16 += mail + ",";
							}
						}
						JAlarm.setWebUpdatedSendSmsList(data16);
					}
					
					String data17 = "";
					if(Arrays.asList(JAlarm.getManagementFailSendEmailList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getManagementFailSendEmailList().split(",")){
							if(!mail.equals(accountItem)){
								data17 += mail + ",";
							}
						}
						JAlarm.setManagementFailSendEmailList(data17);
					}
					
					String data18 = "";
					if(Arrays.asList(JAlarm.getManagementFailSendSmsList().split(",")).contains(accountItem)){
						for(String mail : JAlarm.getManagementFailSendSmsList().split(",")){
							if(!mail.equals(accountItem)){
								data18 += mail + ",";
							}
						}
						JAlarm.setManagementFailSendSmsList(data18);
					}
					
					String dailyReport = "";
					if(Arrays.asList(Config.getReportDailyMail().split(",")).contains(accountItem)){
						for(String mail : Config.getReportDailyMail().split(",")){
							if(!mail.equals(accountItem)){
								dailyReport += mail + ",";
							}
						}
						Config.setReportDailyMail(dailyReport);
					}
					
					String weeklyReport = "";
					if(Arrays.asList(Config.getReportWeeklyMail().split(",")).contains(accountItem)){
						for(String mail : Config.getReportWeeklyMail().split(",")){
							if(!mail.equals(accountItem)){
								weeklyReport += mail + ",";
							}
						}
						Config.setReportWeeklyMail(weeklyReport);
					}
					
					// Remove record from device table
					if (dbAccount.removeAccount(accountItem)) {
						System.out.println("Account: " + accountItem + " remove from db success.");
					}
			}
			
		return true;
		
		}
		
	}

	public String accountRecovery(String email) {
		return login.accountRecovery(email);
	}

	public List<JModule> nonManagedModule() {
		return dbModule.getNonManagedModule();
	}
	
	public List<JModule> pingManagedModule() {
		return dbModule.getPingManagedModule();
	}
	
	public boolean checkModelName(String modelName) {
		return dbModule.isModelNameExisted(modelName);
	}

	public String addModule(JModule module) {
		if(dbModule.isModelNameExisted(module.getModelName())){
			return "Repeat";
		}
		
		if(dbModule.addModule(module)){
			return "Success";
		}
		return "Fail";
	}
	
	public boolean removeModule(String[] modelList) {
		if (modelList == null)
			return false;
		boolean updated = false;

		for (String model : modelList) {
			if (dbModule.removeModule(model)) {
				System.out.println("Device: " + model + " remove from db success.");
			}
			updated = true;
			break;
		}

		return updated;
	}
	
	public boolean setModule(JModule module) {
		return dbModule.setModule(module);
	}

	public List<JModule> getModule() {
		return dbModule.getAllModule();
	}
	
	public List<JPoeSchedule> poeScheduleItems() {
		return dbPoeSchedule.getPoeItems();
	}
	
	public List<JProfile> profileItems() {
		return dbProfile.getProfileItems();
	}

	public void cancelScheduleTimer() {
		if (scheduleTimer != null){
			scheduleTimer.cancel();
		}
		if (poeScheduleTimer != null){
			poeScheduleTimer.cancel();
		}
		if (acapscheduleTimer != null){
			acapscheduleTimer.cancel();
		}
		if (stanumberscheduleTimer != null){
			stanumberscheduleTimer.cancel();
		}
		if (reportScheduleTimer != null){
			reportScheduleTimer.cancel();
		}
		if (nmsScheduleTimer != null){
			nmsScheduleTimer.cancel();
		}
	}

	// Try to cancel the SNMP Trap Receiver in network.
	public void stopTrapReceiver() {
		try {
			if (trap != null)
				trap.doTrapEnd();
		} catch (IOException e) {
			System.out.println("Stop trap exception.");
		}
	}

	public boolean isScanIpRunning() {

		return scanIpThread != null && scanIpThread.isRunning();
	}

	public boolean startScanIpThread(String startIp, String endIp, String community, int snmptimeout, int snmpVersion, String type) {
		if (scanIpThread == null || !scanIpThread.isRunning()) {
		    int limitedDeviceNumber = 0;
		    try {
                limitedDeviceNumber = Integer.parseInt(license_info.get(3));
            }
            catch (NumberFormatException e) {
                System.out.println("Parsing number from license device number failed.");
            }
		    
			this.scanIpThread = new JScanIpTask(dbDevice, dbModule, deviceList, limitedDeviceNumber);
			scanIpThread.setIpScanList(JTools.getIpScanList(startIp, endIp, community, snmptimeout, snmpVersion, type));
			scanIpThread.start();
			
			return true;
		} else {
			System.out.println("Scan IP is already running.");
			
			return false;
		}
	}

	public boolean startScanIpThread(List<List<String>> Columns) {
		if (scanIpThread == null || !scanIpThread.isRunning()) {
		    int limitedDeviceNumber = 0;
		    try {
                limitedDeviceNumber = Integer.parseInt(license_info.get(3));
            }
            catch (NumberFormatException e) {
                System.out.println("Parsing number from license device number failed.");
            }
		    
			this.scanIpThread = new JScanIpTask(dbDevice, dbModule, deviceList, limitedDeviceNumber);
			scanIpThread.setIpScanList(Columns);
			scanIpThread.start();
			
			return true;
		} else {
			System.out.println("Scan IP is already running.");
			
			return false;
		}
	}

	public void stopScanIpThread() {
		if (scanIpThread != null) {
			scanIpThread.stopThread();
		}
	}

	public String getScanIpStatus() {
		if (scanIpThread == null) {
			return "standby";
		}
		else if (scanIpThread.isReachLimitation()) {
		    scanIpThread.resetReachLimitation();
		    return "reach_limit";
		}
		else if (!scanIpThread.isRunning()) {
		    return "standby";
		}
		else {
		    int percent = (int) scanIpThread.getPercentage();
		    return String.format("running (%d%%)", percent);
		}
	}

	public JLogTable getLogs(String levelThreshold, int number) {
		return dbLog.getLogs(levelThreshold, number);
	}

	public JLogTable getLogsByTime(String levelThreshold, String startTime, String endTime) {
		return dbLog.getLogsByTime(levelThreshold, startTime, endTime);
	}
	
	public String[] getLogsTimeBoundary(String levelThreshold) {
		String first = dbLog.getFirstTime(levelThreshold);
		String last = dbLog.getLastTime(levelThreshold);
		return new String[]{first, last};
	}
	
	public String[][] getLast50Log() {
		return dbLog.getLast50Log();
	}
	
	public void disconnectDatabase() {
		database.disconnect();
	}

	// ACAP start
	public String[][] getSTANumberHistoryByColumValue(final String Column, final String value) {
		return dbSTANumber.getSTANumberHistoryByColumValue(Column,value);
	}
	
	public ArrayList<String[]> getApListRxTxOctetTraffic(String acIp, String startTime, String endTime) {
		//JDevice device = findDeviceByIp(ip);

		//if (device != null) {
		return dbApList.getRxTxOctetTraffic(acIp, startTime, endTime);
		//}

		//return null;
	}
	
	public List<ArrayList<String[]>> getApSsidListRxTxOctetTraffic(String acIp, String startTime, String endTime) {
		//JDevice device = findDeviceByIp(ip);

		//if (device != null) {
		return dbApSsidList.getRxTxOctetTraffic(acIp, startTime, endTime);
		//}

		//return null;
	}
	// ACAP end

	// ----------------------------------------------------------------------------------------------------

	private void initDatabase() {
		this.database = new JDatabase("jdbc:derby://localhost:1527/nms_db;create=true", "user", "user"); // TODO: next will create this object in each table
	}

	private void initAccountTable() {
		this.dbAccount = new JDbAccount(database, dbAccountTableName);

		if (database != null && database.isConnected()) {
			if (dbAccount.isTableExisted()) {
				System.out.println("[OK]Account table " + dbAccountTableName + " exists.");
			} else {
				if (dbAccount.createTable()) {
					System.out.println("[OK]Account table " + dbAccountTableName + " not found, create success.");

					if (dbAccount.addAccount("SUPER", "bdfd55b5c9812a73b61103c2cf04a7eaefaca9a6", "SUPER", "SUPER", "", "", "true", "false")) {
						System.out.println("Default account 'SUPER' create success.");
					} else{
						System.out.println("Default account 'SUPER' create failed.");
					}
					if (dbAccount.addAccount("admin", "cab369dcae5c75d0df936b48366f9f0208de478b", "admin", "Administrator", "", "", "true", "false")) {
						System.out.println("Default account 'admin' create success.");
					} else{
						System.out.println("Default account 'admin' create failed.");
					}
					
					/*if (dbAccount.addAccount("richard", "1f8c2454f1d40d0ab483da653a260d05653b9d3c", "admin", "RichardTai", "RichardTai@via.com.tw", "0978091837")) {
						System.out.println("Default account 'richard' create success.");
					}
					if (dbAccount.addAccount("steven", "25c18715a7a6f599efe787f63689ffee09f32658", "admin", "StevenYang", "StevenYang@via.com.tw", "0910829892")) {
						System.out.println("Default account 'steven' create success.");
					}
					if (dbAccount.addAccount("harry", "a8884cfde335e5a6d3a15c4b9aab422dd7af6be5", "admin", "HarryChiang", "HarryChiang@via.com.tw", "0920266815")) {
						System.out.println("Default account 'harry' create success.");
					}
					if (dbAccount.addAccount("kaichun", "5f44971414c92ab609287f4976c9cbcf373479b", "admin", "KaiChunChang", "KaiChunChang@via.com.tw", "0963330539")) {
						System.out.println("Default account 'kaichun' create success.");
					}
					if (dbAccount.addAccount("james", "3b2886b003b6c463cecb93ec2ce0931b318890bd", "admin", "JamesHung", "jameshung@via.com.tw", "0983379233")) {
						System.out.println("Default account 'james' create success.");
					}*/
					/*if (dbAccount.addAccount("user", "c481cdd7d558b272a7d8862e37bbfa43a2f6a193", "user", "user", "", "")) {
						System.out.println("Default account 'user' create success.");
					}*/
				} else {
					System.out.println("[ERR]Account table " + dbAccountTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Account table: no database connection.");
		}
	}

	private void initModuleTable() {
		this.dbModule = new JDbModule(database, dbModuleTableName);

		if (database != null && database.isConnected()) {
			database.deleteTable(dbModuleTableName);
			dbModule.createTable();
			dbModule.addDefaultModule();
		} else {
			System.out.println("[ERR]Module table: no database connection.");
		}
	}

	private void initDeviceList() {
		this.dbDevice = new JDbDevice(database, dbModule, dbDeviceTableName);
		this.deviceMapper = new LinkedHashMap<String, String[]>();
		List<JDevice> deviceList = new ArrayList<JDevice>();

		if (database != null && database.isConnected()) {
			if (dbDevice.isTableExisted()) {
				System.out.println("[OK]Device table: " + dbDeviceTableName + " exists.");

				deviceList = dbDevice.getAllDevice();
			} else {
				if (dbDevice.createTable()) {
					System.out.println("[OK]Device table " + dbDeviceTableName + " not found, create success.");

					// Copy data from DEVICE02 to DEVICE03. Some columns are not need anymore because there is module table.
					/*if (dbDeviceTableName.equals("DEVICE03") && database.isTableExisted("DEVICE02")) {
						String[][] dataAll = database.getAll("DEVICE02");

						for (String[] data : dataAll) {
							if (data.length != 27) {
								System.out.println("data size is wrong about IP=" + data[1]);
								continue;
							}
							if (data[25].startsWith("vn"))
								continue; // skip virtual device

							String fdPublicIp = String.format("'%s'", data[1]);
							String[] ipArray = data[1].split("\\.");
							String fdPublicIpFull = String.format("'%03d.%03d.%03d.%03d'", Integer.parseInt(ipArray[0]), Integer.parseInt(ipArray[1]), Integer.parseInt(ipArray[2]), Integer.parseInt(ipArray[3]));
							String fdSnmpPort = String.format("'%s'", data[2]);
							String fdReadCommunity = "'public'";
							String fdWriteCommunity = "'private'";
							String fdSysDescr = String.format("'%s'", data[4]);
							String fdSysObjectId = String.format("'%s'", data[5]);
							String fdSysUpTime = String.format("'%s'", data[6]);
							String fdSysContact = String.format("'%s'", data[7]);
							String fdSysName = String.format("'%s'", data[8]);
							String fdSysLocat = String.format("'%s'", data[9]);
							String fdPhyAddr = String.format("'%s'", data[11]);
							String fdLocalIp = String.format("'%s'", data[12]);
							String fdManualRemoteIp = String.format("'%s'", data[18]);
							String fdIsManual = String.format("'%s'", data[19]);
							String fdIsMonitor = String.format("'%s'", data[20]);
							String fdAddTime = String.format("'%s'", data[21]);
							String fdAliasName = String.format("'%s'", data[22]);
							String fdLastSeen = String.format("'%s'", data[23]);
							String fdIsAlive = String.format("'%s'", data[24]);
							String fdDevType = String.format("'%s'", data[25]);
							String[] value = { fdPublicIp, fdPublicIpFull, fdSnmpPort, fdReadCommunity, fdWriteCommunity, fdSysDescr, fdSysObjectId, fdSysUpTime, fdSysContact, fdSysName, fdSysLocat, fdPhyAddr, fdLocalIp, fdManualRemoteIp, fdIsManual, fdIsMonitor, fdAddTime, fdAliasName, fdLastSeen, fdIsAlive, fdDevType };
							if (database.insert("DEVICE03", value)) {
								System.out.println("IP=" + data[1] + "(" + data[25] + ") has moved.");
							} else {
								System.out.println("move a row failed at IP=" + data[1]);
							}
						}
						System.out.println("Move original device data from DEVICE02 to DEVICE03 is done.");
					}*/
				} else {
					System.out.println("[ERR]Device table " + dbDeviceTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Device table: no database connection.");
		}

		
		// Do not update device status at startup
		/*for (JDevice device : deviceList) {
		    
		    if (device.getSnmpSupport() < 1)
		        continue;
		    if (device.getPhyAddr().isEmpty())
				continue; // TODO: temporary solution to eliminate AC/AP which has wrong snmp

		    device.getSystemInfo(); // is used to update device status

		    if (device.isAlive()) {
		        dbDevice.updateAlive(device);

		        if (device.isSupportRStp())
		            device.updateStpInfo();
		        if (device.isSupportLldp())
		            device.updateLldpInfo(null);
		        if (device.isSupportVlan())
		            device.updateVlan();
		    }
		}*/

		System.out.println("[OK]deviceList has " + deviceList.size() + " devices read from db.");
		this.deviceList = deviceList;
	}
	
	private void initNmsTable() {
		this.dbNms = new JDbNms(database, dbNmsTableName);
		this.deviceMapper = new LinkedHashMap<String, String[]>();
		List<JNms> nmsList = new ArrayList<JNms>();

		if (database != null && database.isConnected()) {
			if (dbNms.isTableExisted()) {
				System.out.println("[OK]NMS table: " + dbNmsTableName + " exists.");

				nmsList = dbNms.getAllDevice();
			} else {
				if (dbNms.createTable()) {
					System.out.println("[OK]NMS table " + dbNmsTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]NMS table " + dbNmsTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]NMS table: no database connection.");
		}
		System.out.println("[OK]deviceList has " + deviceList.size() + " devices read from db.");
		this.setNmsList(nmsList);
	}
	
	private void initGroupTable() {
		database.deleteTable("GROUP02");
		this.dbGroup = new JDbGroup(database, dbGroupTableName);
		//List<JGroup> GroupList = new ArrayList<JGroup>();
		if (database != null && database.isConnected()) {
			if (dbGroup.isTableExisted()) {
				System.out.println("[OK]Group table: " + dbGroupTableName + " exists.");

				//GroupList = dbGroup.getAllDevice();
			} else {
				if (dbGroup.createTable()) {
					System.out.println("[OK]Group table " + dbGroupTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Group table " + dbGroupTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Group table: no database connection.");
		}
		System.out.println("[OK]deviceList has " + deviceList.size() + " devices read from db.");
		//this.setGroupList(GroupList);
	}

	private void initStatisticsTable() {
		this.dbStatistics = new JDbStatistics(database, dbStatisticsTableName);

		if (database != null && database.isConnected()) {
			if (dbStatistics.isTableExisted()) {
				System.out.println("[OK]Statistics table " + dbStatisticsTableName + " exists.");
			} else {
				if (dbStatistics.createTable()) {
					System.out.println("[OK]Statistics table " + dbStatisticsTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Statistics table " + dbStatisticsTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Statistics table: no database connection.");
		}
	}
	
	private void initApListTable() {
		this.dbApList = new JDbApList(database, dbApListTableName);

		if (database != null && database.isConnected()) {
			if (dbApList.isTableExisted()) {
				System.out.println("[OK]Ap List table " + dbApListTableName + " exists.");
			} else {
				if (dbApList.createTable()) {
					System.out.println("[OK]Ap List table " + dbApListTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Ap List table " + dbApListTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Ap List table: no database connection.");
		}
	}
	
	private void initApSsidListTable() {
		this.dbApSsidList = new JDbApSsidList(database, dbApSsidListTableName);

		if (database != null && database.isConnected()) {
			if (dbApSsidList.isTableExisted()) {
				System.out.println("[OK]Ap Ssid List table " + dbApSsidListTableName + " exists.");
			} else {
				if (dbApSsidList.createTable()) {
					System.out.println("[OK]Ap Ssid List table " + dbApSsidListTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Ap Ssid List table " + dbApSsidListTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Ap Ssid List table: no database connection.");
		}
	}
	
	private void initSTANumberTable() {
		this.dbSTANumber = new JDbSTANumber(database, dbSTANumberTableName);

		if (database != null && database.isConnected()) {
			//dbSTANumber.dropTable();
			if (dbSTANumber.isTableExisted()) {
				System.out.println("[OK]STANumber table: " + dbSTANumberTableName + " exists.");
			} else {
				if (dbSTANumber.createTable()) {
					System.out.println("[OK]STANumber table: " + dbSTANumberTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]STANumber table: " + dbSTANumberTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]STANumber table: no database connection.");
		}
	}

	private void initLogTable() {
		this.dbLog = new JDbLog(database, dbLogTableName);

		if (database != null && database.isConnected()) {
			if (dbLog.isTableExisted()) {
				System.out.println("[OK]Log table: " + dbLogTableName + " exists.");
			} else {
				if (dbLog.createTable()) {
					System.out.println("[OK]Log table: " + dbLogTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Log table: " + dbLogTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Log table: no database connection.");
		}
	}
	
	private void initNotifyTable() {
		if (database != null && database.isConnected()) {
			if (JDbNotify.isTableExisted("NOTIFY01")) {
				database.deleteTable("NOTIFY01");
			} else if (JDbNotify.isTableExisted(dbNotifyName)) {
				System.out.println("[OK]Notify table " + dbNotifyName + " exists.");
			} else {
				if (JDbNotify.createTable(dbNotifyName)) {
					System.out.println("[OK]Notify table " + dbNotifyName + " not found, create success.");

				} else {
					System.out.println("[ERR]Notify table " + dbNotifyName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Notify table : no database connection.");
		}
	}	    

	private void initPoeScheduleTable() {
		//database.deleteTable("POE_SCHEDULE01");
		this.dbPoeSchedule = new JDbPoeSchedule(database, dbPoeScheduleTableName);

		if (database != null && database.isConnected()) {
			if (dbPoeSchedule.isTableExisted()) {
				System.out.println("[OK]Account table " + dbPoeScheduleTableName + " exists.");
			} else {
				if (dbPoeSchedule.createTable()) {
					System.out.println("[OK]Account table " + dbPoeScheduleTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Account table " + dbPoeScheduleTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Account table: no database connection.");
		}
	}
	
	private void initProfileTable() {
		//database.deleteTable("PROFILE01");
		this.dbProfile = new JDbProfile(database, dbProfileTableName);

		if (database != null && database.isConnected()) {
			if (dbProfile.isTableExisted()) {
				System.out.println("[OK]Account table " + dbProfileTableName + " exists.");
			} else {
				if (dbProfile.createTable()) {
					System.out.println("[OK]Account table " + dbProfileTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]Account table " + dbProfileTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]Account table: no database connection.");
		}
	}
	
	private void initAliveStatusTable() {
		this.dbAliveStatus = new JDbAliveStatus(database, dbAliveStatusTableName);

		if (database != null && database.isConnected()) {
			//dbAliveStatus.dropTable();
			if (dbAliveStatus.isTableExisted()) {
				System.out.println("[OK]AliveStatus table: " + dbAliveStatusTableName + " exists.");
			} else {
				if (dbAliveStatus.createTable()) {
					System.out.println("[OK]AliveStatus table: " + dbAliveStatusTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]AliveStatus table: " + dbAliveStatusTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]AliveStatus table: no database connection.");
		}
	}
	
	private void initNmsLogTable() {
		this.dbNmsLog = new JDbNmsLog(database, dbNmsLogTableName);

		if (database != null && database.isConnected()) {
			//dbNmsLog.dropTable();
			if (dbNmsLog.isTableExisted()) {
				System.out.println("[OK]NmsLog table: " + dbNmsLogTableName + " exists.");
			} else {
				if (dbNmsLog.createTable()) {
					System.out.println("[OK]NmsLog table: " + dbNmsLogTableName + " not found, create success.");
				} else {
					System.out.println("[ERR]NmsLog table: " + dbNmsLogTableName + " not found, create failed.");
				}
			}
		} else {
			System.out.println("[ERR]NmsLog table: no database connection.");
		}
	}

	private void initScheduleTask() {
		this.scheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 5);
		System.out.println("Current Time: " + currentTime + ", Schedule starts: " + scheduledTime + ", period: " + 600000);
		scheduleTimer.scheduleAtFixedRate(new JScheduleTask(dbDevice, dbStatistics, deviceList, dbAliveStatus, dbGroup), scheduledTime, 5*60*1000); // 60 seconds, every 10 minutes
	}
	
	private void initPoeScheduleTask() {
		this.poeScheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 5);
		System.out.println("Current Time: " + currentTime + ", PoE Schedule starts: " + scheduledTime + ", period: " + 300000);
		poeScheduleTimer.scheduleAtFixedRate(new JPoeScheduleTask(deviceList), scheduledTime, 5*60*1000); // 300 seconds, every 5 minutes
	}
	
	private void initAcApScheduleTask() {
		this.acapscheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 5);
		System.out.println("Current Time: " + currentTime + ", AcAp Schedule starts: " + scheduledTime + ", period: " + 600000);
		acapscheduleTimer.scheduleAtFixedRate(new JAcApScheduleTask(dbDevice, dbStatistics, dbApList, dbApSsidList, deviceList, dbAliveStatus, dbGroup), scheduledTime, 5*60*1000); // 60 seconds, every 10 minutes
	}
	
	private void initSTANumberScheduleTask() {
		this.stanumberscheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 10);
		System.out.println("Current Time: " + currentTime + ", STANumber Schedule starts: " + scheduledTime + ", period: " + 600000);
		stanumberscheduleTimer.scheduleAtFixedRate(new JSTANumberScheduleTask(deviceList,dbSTANumber), scheduledTime, 10*60*1000); // 600 seconds, every 10 minutes
	}
	
	private void initReportScheduleTask() {
		this.reportScheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 10);
		System.out.println("Current Time: " + currentTime + ", Report Schedule starts: " + scheduledTime + ", period: " + 600000);
		reportScheduleTimer.scheduleAtFixedRate(new JReportScheduleTask(deviceList, dbAccount), scheduledTime, 10*60*1000); // 600 seconds, every 10 minutes
	}
	
	private void initNmsScheduleTask() {
		this.nmsScheduleTimer = new Timer();
		Date currentTime = new Date();
		Date scheduledTime = getNextNthMinuteTime(currentTime, 10);
		System.out.println("Current Time: " + currentTime + ", NMS Schedule starts: " + scheduledTime + ", period: " + 600000);
		nmsScheduleTimer.scheduleAtFixedRate(new JNmsScheduleTask(dbNms, nmsList, dbAliveStatus), scheduledTime, 10*60*1000); // 600 seconds, every 10 minutes
	}

	private void initScanIpThread() {
		//this.scanIpThread = new JScanIpTask(database, dbDeviceTableName, deviceList);
		this.scanIpThread = null; // By extending the Thread class, it must be created every time to do the job.
	}

	private void initLoginSystem() {
		this.login = new JLogin(dbAccount); // TODO: should consider if database has no connection

		if (database != null && database.isConnected()) {

		} else {
			System.out.println("[ERR]Log table:: no database connection.");
		}
	}

	private void initTopology() {
		this.topology = new JTopology(deviceList);
	}

	private void initSnmpTrap() {
		String ServerIP = JSystem.ethAddress + "/" + (JSystem.osDistribution.equals("Ubuntu") ? "1162" : "162"); // listen 1162 only in Ubuntu
		this.trap = new JTrapReceiver(deviceList);
		trap.doTrap(ServerIP);
	}

	private void initSetSingleIpScript() {
        String singleIpScript = JSystem.projectSpace + "/set_single_ip_script";
        File singleIpScriptFile = new File(singleIpScript);
        if (JSystem.osName.equalsIgnoreCase("Linux")) {
            try {
                singleIpScriptFile.delete();
                singleIpScriptFile.createNewFile();
                String[] cmd_set_single_ip_scriptFile = {""};
                
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + singleIpScript + ";" +
                            "echo \"#!/usr/bin/expect\"> " + singleIpScript + ";" +
                            "echo \"set timeout \\\"1\\\"\">> " + singleIpScript + ";" +
                            "echo \"spawn \\\"su\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"Password:\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"V1AC0ntr01\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"iptables --table nat --flush\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"iptables -t nat -A PREROUTING -p udp --dport 162 -j REDIRECT --to-port 1162\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"iptables -t nat -L\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"iptables -t nat -L\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + singleIpScript + ";" +
                            "echo \"send \\\"exit\\\\\\r\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"$\\\"\">> " + singleIpScript + ";" +
                            "echo \"expect \\\"eof\\\"\">> " + singleIpScript + ";"
                    };
                    cmd_set_single_ip_scriptFile = scriptContent;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + singleIpScript + ";" +
                            "echo \"#!/bin/bash\"> " + singleIpScript + ";" +
                            "echo \"iptables -t nat -F\">> " + singleIpScript + ";" +
                            "echo \"echo placeholder for iptables nat\">> " + singleIpScript + ";" +
                            "echo \"echo placeholder for iptables nat\">> " + singleIpScript + ";" +
                            "echo \"iptables -F FORWARD\">> " + singleIpScript + ";" +
                            "echo \"echo placeholder for iptables filter\">> " + singleIpScript + ";" +
                            "echo \"echo placeholder for iptables filter\">> " + singleIpScript + ";" +
                            "echo \"iptables -A FORWARD -j REJECT --reject-with icmp-host-prohibited\">> " + singleIpScript + ";"
                    };
                    cmd_set_single_ip_scriptFile = scriptContent;
                }
                Process singleIpScriptProcess = Runtime.getRuntime().exec(cmd_set_single_ip_scriptFile);
                try {
                    singleIpScriptProcess.waitFor();
                    System.out.println("Add single_ip_script Create Success.");
                } catch (InterruptedException e) {
                    System.out.println("Process Add single_ip_script File is Exception.(waitFor())");
                }
                String[] cmdRestart = { "/bin/sh", "-c", singleIpScript };
                Process processRestart = Runtime.getRuntime().exec(cmdRestart);
                try {
                    processRestart.waitFor();
                    System.out.println("Execute single_ip_script Success.");
                } catch (InterruptedException e) {
                    System.out.println("Process single_ip_script is Exception");
                }
            } catch (IOException e) {
                System.out.println("Process Add single_ip_script File is Exception.(createNewFile())");
            }
        }
	}
	
	private void initDefaultInterfacesFile() {
        // Create default 'interfaces'/'ifcfg-ethx' used for 'reset' process
        if (JSystem.osDistribution.equals("Ubuntu")) {
            String[] defaultInterfacesContent = {
                    "auto lo",
                    "iface lo inet loopback",
                    "",
                    "auto eth0",
                    "iface eth0 inet static",
                    "address 192.168.1.1",
                    "netmask 255.255.255.0",
                    "gateway 192.168.1.1",
                    "#",
                    "",
                    "auto eth1",
                    "iface eth1 inet static",
                    "address 192.168.2.1",
                    "netmask 255.255.255.0"
            };
            boolean defaultInterfacesNeedToCreate = true;
            File defaultInterfacesFile = new File(JSystem.projectSpace + "/interfaces");
            if (defaultInterfacesFile.exists()) {
                defaultInterfacesNeedToCreate = false;
                try {
                    BufferedReader output = new BufferedReader(new FileReader(defaultInterfacesFile));
                    String str;
                    int lineId = 0;
                    boolean success = true;
                    while ((str = output.readLine()) != null) { // NOTE that no matter there is a new line at EOF or not, result are the same
                        if (lineId >= defaultInterfacesContent.length) {
                            System.out.println("default interfaces length is wrong.");
                            success = false;
                            break;
                        }
                        if (str.equals(defaultInterfacesContent[lineId]) == false) {
                            System.out.println("default interfaces wrong at line " + lineId);
                            success = false;
                            break;
                        }
                        lineId++;
                    }
                    if (success) {
                        System.out.println("check default interfaces OK!");
                    } else {
                        defaultInterfacesNeedToCreate = true;
                    }
                    output.close();
                } catch (IOException e) {
                    System.out.println("read default interfaces failed.");
                }

            }
            if (defaultInterfacesNeedToCreate) {
                try {
                    defaultInterfacesFile.delete();
                    defaultInterfacesFile.createNewFile();

                    BufferedWriter input = new BufferedWriter(new FileWriter(defaultInterfacesFile));
                    for (String str : defaultInterfacesContent) {
                        input.write(str);
                        input.newLine();
                    }
                    input.close();
                    System.out.println("create default interfaces OK!");
                } catch (IOException e) {
                    System.out.println("write default interfaces failed.");
                }
            }
        }
        else if (JSystem.osDistribution.equals("CentOS")) {
            // TODO currently not support 'Reset Button' in CentOS
        }
    }
	
	private void initSetInterfaceScript() {
        String interfaceScriptFile = JSystem.projectSpace + "/set_interfaces_script";
        File fp_set_interfaces_scriptFile = new File(interfaceScriptFile);
        if (JSystem.osName.equalsIgnoreCase("Linux")) {
            try {
                fp_set_interfaces_scriptFile.delete();
                fp_set_interfaces_scriptFile.createNewFile();
                String[] cmd_set_interfaces_scriptFile = {""};
                
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + interfaceScriptFile + ";" +
                            "echo \"#!/usr/bin/expect\"> " + interfaceScriptFile + ";" +
                            "echo \"set timeout \\\"1\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"spawn \\\"su\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"expect \\\":\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"send \\\"V1AC0ntr01\\\\\\r\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"expect \\\"#\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"send \\\"\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"expect \\\"#\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"send \\\"/etc/init.d/networking restart\\\\\\r\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"send \\\"exit\\\\\\r\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"expect \\\"$\\\"\">> " + interfaceScriptFile + ";" +
                            "echo \"expect \\\"eof\\\"\">> " + interfaceScriptFile + ";"
                    };
                    cmd_set_interfaces_scriptFile = scriptContent;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + interfaceScriptFile + ";" +
                            "echo \"#!/bin/bash\"> " + interfaceScriptFile + ";" +
                            "echo \"echo placeholder for ifcfg-eth0\">> " + interfaceScriptFile + ";" +
                            "echo \"echo placeholder for ifcfg-eth1\">> " + interfaceScriptFile + ";" +
                            "echo \"/etc/init.d/network restart\" >> " + interfaceScriptFile + ";"
                    };
                    cmd_set_interfaces_scriptFile = scriptContent;
                }
                Process Process_set_interfaces_scriptFile = Runtime.getRuntime().exec(cmd_set_interfaces_scriptFile);
                try {
                    Process_set_interfaces_scriptFile.waitFor();
                    //System.out.println("set_interfaces_scriptFile not found, create success.");
                } catch (InterruptedException e) {
                    System.out.println("Process_set_interfaces_scriptFile.waitFor(); ==> 發生 IOException");
                }
            } catch (IOException e) {
                System.out.println("set_interfaces_scriptFile.createNewFile(); ==> 發生 IOException");
            }
        }
	}
	
	private void initGetSerialNumberScript() {
        String serialNumberScriptFile = JSystem.projectSpace + "/get_serial_number_script";
        File fp_get_serial_number_scriptFile = new File(serialNumberScriptFile);
        if (JSystem.osName.equalsIgnoreCase("Linux")) {
            try {
                fp_get_serial_number_scriptFile.delete();
                fp_get_serial_number_scriptFile.createNewFile();
                String[] cmd_get_serial_number_scriptFile = {""};
                
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + serialNumberScriptFile + ";" +
                            "echo \"#!/usr/bin/expect\"> " + serialNumberScriptFile + ";" +
                            "echo \"set timeout \\\"1\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"spawn \\\"su\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"expect \\\":\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"send \\\"V1AC0ntr01\\\\\\r\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"expect \\\"#\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"send \\\"hdparm -I /dev/sda 2> /dev/null | grep 'Serial Number'\\\\\\r\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"send \\\"exit\\\\\\r\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"expect \\\"$\\\"\">> " + serialNumberScriptFile + ";" +
                            "echo \"expect \\\"eof\\\"\">> " + serialNumberScriptFile + ";"
                    };
                    cmd_get_serial_number_scriptFile = scriptContent;
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + serialNumberScriptFile + ";" +
                            "echo \"#!/bin/bash\"> " + serialNumberScriptFile + ";" +
                            "echo \"hdparm -I /dev/sda 2> /dev/null | grep 'Serial Number'\">> " + serialNumberScriptFile + ";"
                    };
                    cmd_get_serial_number_scriptFile = scriptContent;
                }
                Process Process_get_serial_number_scriptFile = Runtime.getRuntime().exec(cmd_get_serial_number_scriptFile);
                try {
                    Process_get_serial_number_scriptFile.waitFor();
                    //System.out.println("get_serial_number_scriptFile not found, create success.");
                } catch (InterruptedException e) {
                    System.out.println("Process_get_serial_number_scriptFile.waitFor(); ==> 發生 IOException");
                }
            } catch (IOException e) {
                System.out.println("get_serial_number_scriptFile.createNewFile(); ==> 發生 IOException");
            }
        }
	}
	
	private void initUpdateWebScript() {
	    String pathToUpdateWebScript = JSystem.projectSpace + "/update_web_script";
        File updateWebScriptFile = new File(pathToUpdateWebScript);
        if (JSystem.osName.equalsIgnoreCase("Linux")) {
            do {
                updateWebScriptFile.delete();
                try {
                    updateWebScriptFile.createNewFile();
                }
                catch (IOException e1) {
                    System.out.println("create new file for update_web_script failed.");
                    break;
                }
                String[] cmdArray = {""};
                
                if (JSystem.osDistribution.equals("Ubuntu")) {
                    String[] scriptContent = {
                            "/bin/sh",
                            "-c",
                            "chmod u+x " + pathToUpdateWebScript + ";" +
                            "echo \"#!/usr/bin/expect\"> " + pathToUpdateWebScript + ";" +
                            "echo \"set timeout \\\"1\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"spawn \\\"su\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"expect \\\":\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"send \\\"V1AC0ntr01\\\\\\r\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"expect \\\"#\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"send \\\"/bin/cp /etc/tomcat7/CyberExpert.war /var/lib/tomcat7/webapps/ROOT.war\\\\\\r\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"send \\\"exit\\\\\\r\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"expect \\\"$\\\"\">> " + pathToUpdateWebScript + ";" +
                            "echo \"expect \\\"eof\\\"\">> " + pathToUpdateWebScript + ";"
                    };
                    cmdArray = scriptContent;
                    
                    try {
                        Process createUpdateWebScriptProcess = Runtime.getRuntime().exec(cmdArray);
                        createUpdateWebScriptProcess.waitFor();
                    }
                    catch (IOException e) {
                        System.out.println("run process to create update_web_script failed.");
                        break;
                    }
                    catch (InterruptedException e) {
                        System.out.println("wait for process to create update_web_script failed.");
                        break;
                    }
                    System.out.println("[OK] update_web_script create success.");
                }
                else if (JSystem.osDistribution.equals("CentOS")) {
                    // No need for CentOS because tomcat runs under root.
                }
            } while(false);
        }
	}
	// TODO more file need to create independently

	public List<String> get_license_info() {
		return license_info;
	}
	
	public void initLicense() {
		//Create license_info
		/*
		NMS Serial Number
		Current Device Number
		License File Contents
		License Device Number
		License Check Valid or Invalid
		 */
		license_info = new Vector<String>();
		int current_device_number = deviceList.size();

		for (int i = 0; i < 5; i++) {
			license_info.add("");
		}

		for (int i = 0; i < 5; i++) {
			license_info.set(i, "");
		}

		license_info.set(1, Integer.toString(current_device_number));
		license_info.set(3, "0");
		license_info.set(4, "Invalid");

		String line = "";
		String serialNumberScriptFile = JSystem.projectSpace + "/get_serial_number_script";
		if (JSystem.osName.equalsIgnoreCase("Linux")) {
			try {
				String[] cmd_print = { "/bin/sh", "-c", "/bin/sed -n '7p' " + serialNumberScriptFile };
				Process Process_print = Runtime.getRuntime().exec(cmd_print);
				BufferedReader BufferedReader_print = new BufferedReader(new InputStreamReader(Process_print.getInputStream(), "UTF-8"));
				while ((line = BufferedReader_print.readLine()) != null) {
				}
				BufferedReader_print.close();
				Process_print.waitFor();

				String nms_serial_number = "";
				String[] cmd_get_serial_number = { "/bin/sh", "-c", serialNumberScriptFile };
				Process Process_get_serial_number = Runtime.getRuntime().exec(cmd_get_serial_number);
				BufferedReader BufferedReader_get_serial_number = new BufferedReader(new InputStreamReader(Process_get_serial_number.getInputStream(), "UTF-8"));
				while ((line = BufferedReader_get_serial_number.readLine()) != null) {
					Pattern pattern_serial_number = Pattern.compile("Serial Number");
					Matcher matcher_serial_number = pattern_serial_number.matcher(line);
					while (matcher_serial_number.find()) {
						String[] serial_number = line.replaceAll(" ", "").split(":");
						license_info.set(0, serial_number[1]);
						nms_serial_number = serial_number[1];
					}
				}
				BufferedReader_get_serial_number.close();
				Process_get_serial_number.waitFor();

				String license_device_sn = "";
				String license_device_num = "";
				String hash = "";
				String[] cmd_get_license_file_contents = { "/bin/sh", "-c", "sed -n '1p' /etc/tomcat7/license.txt" };
				Process Process_get_license_file_contents = Runtime.getRuntime().exec(cmd_get_license_file_contents);
				BufferedReader BufferedReader_get_license_file_contents = new BufferedReader(new InputStreamReader(Process_get_license_file_contents.getInputStream(), "UTF-8"));
				while ((line = BufferedReader_get_license_file_contents.readLine()) != null) {
					license_info.set(2, line);

					String[] license_file_contents = line.split("_");

					license_device_sn = license_file_contents[0];

					license_device_num = license_file_contents[1];

					hash = license_file_contents[2];
				}
				BufferedReader_get_license_file_contents.close();
				Process_get_license_file_contents.waitFor();

				String[] cmd_calc_license = { "/bin/sh", "-c", "/etc/tomcat7/license " + nms_serial_number + license_device_num };
				Process Process_calc_license = Runtime.getRuntime().exec(cmd_calc_license);
				BufferedReader BufferedReader_calc_license = new BufferedReader(new InputStreamReader(Process_calc_license.getInputStream(), "UTF-8"));
				while ((line = BufferedReader_calc_license.readLine()) != null) {
					if (line.equals(hash)) {
						license_info.set(3, license_device_num);
						license_info.set(4, "Valid");
					} else {
						license_info.set(4, "Invalid");
					}
				}
				BufferedReader_calc_license.close();
				Process_calc_license.waitFor();
			} catch (Exception e) {
				//System.out.println("License_info 發生 IOException");
			}
		}
		else {
		    license_info.set(3, "100000");    // for Win or others
		}
		
		List<String> license = new ArrayList<String>();
		license = get_license_info();
		if (System.getProperty("os.name").startsWith("Windows")) {
			setLicenseChk("Valid");
		}else {
			setLicenseChk(license.get(4));
			//System.out.println("license="+license.get(4));
		}
	}
	public void initPing() {
		ping_info = new Vector<String>();
		ping_info.add("ping_stop");
		ping_info.add("print_ok");
	}

	public List<String> getping_info() {
		return ping_info;
	}

	public void initRemoteService() {
		stopRemoteService();
		RemoteServiceConfig rmiConfig = Config.getRemoteServiceConfig();
		if(rmiConfig.isEnable()){
			this.remoteservice = new RemoteService();
			this.remoteservice.start(rmiConfig.getLocalAddress());
		}
	}

	// Try to cancel the Remote Service in network.
	public void stopRemoteService() {
		try {
			if (this.remoteservice != null) {
				this.remoteservice.stop();
				this.remoteservice = null;
			}
		} catch (Exception e) {
			System.out.println("Stop Remote Service\nException：" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean getRemoteServiceisRunning() {
		if (this.remoteservice != null) {
			return this.remoteservice.getRemoteServiceisRunning();
		}
		else{
			return false;
		}
	}

	// iptables add trap port rule
	public void initTrapPort(){
		JTools.CLI("iptables -D INPUT -m state --state NEW -m udp -p udp --dport 162 -j ACCEPT");
		JTools.CLI("iptables -I INPUT -m state --state NEW -m udp -p udp --dport 162 -j ACCEPT");
	}

	// iptables delete trap port rule
	public void stopTrapPort() {
		JTools.CLI("iptables -D INPUT -m state --state NEW -m udp -p udp --dport 162 -j ACCEPT");
	}

	/**
	 * Get JDevice object
	 * 
	 * @param ip
	 * @return null - if IP is not in list
	 */
	public JDevice findDeviceByIp(String ip) {
		if (ip != null) {
			for (JDevice device : deviceList) {
				if (device.getPublicIp().equals(ip))
					return device;
			}
		}

		return null;
	}

	private void updateDeviceMapper() {
		deviceMapper.clear();

		for (JDevice device : deviceList) {
			String[] info = { device.getPublicIp(), device.getSnmpPort(), device.getSnmpAddress(), device.getDeviceType() };
			deviceMapper.put(device.getPhyAddr(), info);
			// System.out.println("Update mapper from deviceList for " +
			// device.getPhyAddr() + ": " + Arrays.toString(info));
		}
	}

	private Date getNextNthMinuteTime(Date currentTime, int nextNth) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTime);
		calendar.add(Calendar.MINUTE, nextNth - (calendar.get(Calendar.MINUTE) % nextNth));	// forward to the next Nth minute
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// ----------------------------------------------------------------------------------------------------
	
	public static void topologyIsStart() {
		isTopologyRunning = true;
	}
	
	public static void topologyIsStop() {
		isTopologyRunning = false;
	}
	
	public static void firstTimeIsOver() {
		isFirstTime = false;
	}

	public List<JDevice> getDeviceList() {
		return deviceList;
	}
	
	public List<JNms> getNmsList() {
		return nmsList;
	}
	
	public void setNmsList(List<JNms> nmsList) {
		this.nmsList = nmsList;
	}

	public List<JGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<JGroup> groupList) {
		this.groupList = groupList;
	}

	public String getLicenseChk() {
		return licenseChk;
	}

	public void setLicenseChk(String licenseChk) {
		this.licenseChk = licenseChk;
	}

	public static String getLocationAddress() {
		return Config.getLocationAddress();
	}

	public static List<Map<String, String>> getRemoteNMSListLocationAddress() {
		List<Map<String, String>> NMSListLocationAddress = new ArrayList<Map<String, String>>();

		for (JNms NMS : nmsList) {
			if (NMS.isAlive()) {
				Map<String, String> tmpList = new LinkedHashMap<String, String>();

				String Location = null;

				RemoteInterface Service = RemoteService.creatClient(NMS.getPublicIp(), 3);

				if (Service != null) {
					try {
						Location = Service.getRemoteLocationAddress();
					} catch (RemoteException e) {
						System.out.println("Location = Service.getRemoteLocationAddress();\nRemoteException:" + e.getMessage());
						//e.printStackTrace();
					}
				}

				tmpList.put("text", "<strong>" + NMS.getAliasName() + "</strong>");

				//System.out.println("text: " + NMS.getAliasName() + ", addr: " + Location);
				if (Location != null) {
					tmpList.put("addr", Location);
				} else {
					tmpList.put("addr", "");
				}

				if (!(Location == null || Location.equals(""))) {
					NMSListLocationAddress.add(tmpList);
				}
			}
		}
		return NMSListLocationAddress;
	}

	public static TableData exportDeviceList() {
		return dbDevice.exportDeviceList();
	}

	public boolean isTopologyRunning() {
		return isTopologyRunning;
	}

	public void setTopologyRunning(boolean isTopologyRunning) {
		this.isTopologyRunning = isTopologyRunning;
	}

	public static boolean isFirstTime() {
		return isFirstTime;
	}

	public static void setFirstTime(boolean isFirstTime) {
		JNetwork.isFirstTime = isFirstTime;
	}

}
