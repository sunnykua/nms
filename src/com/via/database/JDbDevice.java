package com.via.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.via.model.JDevice;
import com.via.model.JInterface;

public class JDbDevice {
    private JDatabase dbInst;
    private JDbModule dbModule;
    private String tableName;
    //private String tableNameInQuotes;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String[] tableDefinition = {
            "PUBLIC_IP       VARCHAR(20) UNIQUE NOT NULL, ",    // 1. xxx.xxx.xxx.xxx
            "PUBLIC_IP_FULL  VARCHAR(25), ",                    // 2. IP with 3 digits per number
            "SNMP_PORT       VARCHAR(5) NOT NULL, ",            // 3. usually 161
            "SNMP_TIMEOUT    VARCHAR(5) NOT NULL, ",			// 4. snmp timeout
            "SNMP_VERSION    VARCHAR(5) NOT NULL, ",            // 5. snmp version 
            "READ_COMMUNITY  VARCHAR(50), ",                    // 6. community string for read
            "WRITE_COMMUNITY VARCHAR(50), ",                    // 7. community string for write
            "SECURITY_NAME   VARCHAR(50), ",                    // 8. snmp v3 security name
            "SECURITY_LEVEL  VARCHAR(5), ",                     // 9. snmp v3 security level
            "AUTH_PROTOCOL   VARCHAR(50), ",                    // 10. snmp v3 authentication protocol
            "AUTH_PASS       VARCHAR(50), ",                    // 11. snmp v3 authentication password
            "PRIV_PROTOCOL   VARCHAR(50), ",                    // 12. snmp v3 privacy protocol
            "PRIV_PASS       VARCHAR(50), ",                    // 13. snmp v3 privacy password
            "SYSDESCR        VARCHAR(255), ",                   // 14. sys.descr from MIB
            "SYSOBJID        VARCHAR(50), ",                    // 15. sys.objectid from MIN
            "SYSUPTIME       VARCHAR(50), ",                    // 16. sys.uptime from MIB
            "SYSCONTACT      VARCHAR(255), ",                   // 17. sys.contact from MIB
            "SYSNAME         VARCHAR(255), ",                   // 18. sys.name from MIB
            "SYSLOCATION     VARCHAR(255), ",                   // 19. sys.location from MIB
            "PHY_ADDR        VARCHAR(20), ",                    // 20. physical address
            "LOCAL_IP        VARCHAR(20), ",                    // 21. the IP that device knows itself
            "INF_MAN_REM_IP  VARCHAR(10000), ",                 // 22. manual input remote IP for each interface
            "INF_IS_MANUAL   VARCHAR(2000), ",                  // 23. whether a interface is using manual remote
            "INF_IS_MONITOR  VARCHAR(2000), ",                  // 24. whether a interface is monitored as critical path
            "ADD_TIME        TIMESTAMP NOT NULL, ",             // 25. the time added to NMS
            "ALIAS_NAME      VARCHAR(255), ",                   // 26. user defined name
            "LAST_SEEN       TIMESTAMP, ",                      // 27. last time has been communicated with
            "IS_ALIVE        VARCHAR(5) NOT NULL, ",            // 28. it fails once the device can not connect with
            "DEV_TYPE        VARCHAR(20), ",                    // 29. device type: l2switch, l3switch, wlanAC, wlanAP, firewall, router ...
            "IS_POE_POWER        VARCHAR(5), ",                 // 30. it fails once the device not set poe schedule
            "INF_IS_POE_POWER    VARCHAR(2000), ",              // 31. whether a interface is using poe schedule
            "INF_POE_SCH_NAME    VARCHAR(10000), ",             // 32. Poe schedule name for each interface
            "INF_POE_START_TIME  VARCHAR(10000), ",             // 33. Poe schedule start time for each interface
            "INF_POE_END_TIME    VARCHAR(10000), ",             // 34. Poe schedule end time for each interface
            "PORT_ID             VARCHAR(2000), ",              // 35. it's linkview port number
            "IFINDEX             VARCHAR(10000), ",             // 36. it's ifTable ifIndex column
            "IFTYPE              VARCHAR(10000), ",             // 37. it's ifTable ifType column
            "IFSPEED             VARCHAR(10000), ",             // 38. it's ifTable ifSpeed column
            "STACK_ID            VARCHAR(2000), ",              // 39. it to store stack id count
            "IS_POE_PORT         VARCHAR(2000), ",              // 40. whether a interface is poe port
            "IS_MAIL_FILTER      VARCHAR(5), ",                 // 41. Device mail filter on/off
            "PF_SCH_NAME         VARCHAR(30), ",                // 42. Device profile name
            "PF_START_TIME       VARCHAR(20), ",                // 43. Device profile start time
            "PF_END_TIME         VARCHAR(20), ",                // 44. Device profile end time
            "INF_ALIAS_NAME      VARCHAR(10000), ",             // 45. Port alias name
            "INF_IS_MAIL_FILTER  VARCHAR(2000), ",              // 46. Mail filter on/off for each interface
            "INF_PF_SCH_NAME     VARCHAR(10000), ",             // 47. Profile name for each interface
            "INF_PF_START_TIME   VARCHAR(10000), ",             // 48. Profile start time for each interface
            "INF_PF_END_TIME     VARCHAR(10000), ",             // 49. Profile end time for each interface
            "GROUP_NAME          VARCHAR(30) ",                 // 50. Group name.
            
            
            
    };
    private int a = 0;
    private final int iPUBLIC_IP = a++, iPUBLIC_IP_FULL = a++, iSNMP_PORT = a++, iSNMP_TIMEOUT = a++, iSNMP_VERSION = a++, iREAD_COMMUNITY = a++, iWRITE_COMMUNITY = a++,
    		iSECURITY_NAME = a++, iSECURITY_LEVEL = a++, iAUTH_PROTOCOL = a++, iAUTH_PASS = a++, iPRIV_PROTOCOL = a++, iPRIV_PASS = a++,
            iSYS_DESRC = a++, iSYS_OBJID = a++, iSYS_UPTIME = a++, iSYS_CONTACT = a++, iSYS_NAME = a++, iSYS_LOCAT = a++,
            iPHY_ADDR = a++, iLOCAL_IP = a++, iINF_MAN_IP = a++, iINF_IS_MANU = a++, iINF_IS_MONI = a++,
            iADD_TIME = a++, iALIAS_NAME = a++, iLAST_SEEN = a++, iIS_ALIVE = a++, iDEV_TYPE = a++,
            iIS_POE_POWER = a++, iINF_IS_POE_POWER = a++, iINF_POE_SCH_NAME = a++, iINF_POE_START_TIME = a++, iINF_POE_END_TIME = a++,
            iPORT_ID = a++, iIFINDEX = a++, iIFTYPE = a++, iIFSPEED = a++, iSTACK_ID = a++, iIS_POE_PORT = a++,
            iIS_MAIL_FILTER = a++, iPF_SCH_NAME = a++, iPF_START_TIME = a++, iPF_END_TIME = a++,
            iINF_ALIAS_NAME = a++, iINF_IS_MAIL_FILTER = a++, iINF_PF_SCH_NAME = a++, iINF_PF_START_TIME = a++, iINF_PF_END_TIME = a++, iGROUP_NAME = a++;
    		
    
    public JDbDevice(final JDatabase database, final JDbModule dbModule, final String tableName) {
        this.dbInst = database;
        this.dbModule = dbModule;
        this.tableName = tableName;
        //this.tableNameInQuotes = "\"" + tableName + "\"";
    }
    
    public boolean isTableExisted() {
        return dbInst.isTableExisted(tableName);
    }
    
    public boolean createTable() {
        String definition = "";
        for (String s : tableDefinition) definition += s;

        return dbInst.createTable(tableName, definition);
    }

    public boolean addDevice(final JDevice device) {
        if (device == null) return false;

        String publicIp = String.format("'%s'", device.getPublicIp());
        String publicIpFull = String.format("'%s'", device.getPublicIpFull());
        String snmpPort = String.format("'%s'", device.getSnmpPort());
        String snmpTimeout = String.format("'%s'", device.getSnmpTimeout());
        String snmpVersion = String.format("'%s'", device.getSnmpVersion());
        String readCommunity = String.format("'%s'", device.getReadCommunity());
        String writeCommunity = String.format("'%s'", device.getWriteCommunity());
        // === Snmp v3 ===
        String securityName = String.format("'%s'", device.getSecurityName());
        String securityLevel = String.format("'%s'", device.getSecurityLevel());
        String authProtocol = String.format("'%s'", device.getAuthProtocol());
        String authPassword = String.format("'%s'", device.getAuthPassword());
        String privProtocol = String.format("'%s'", device.getPrivProtocol());
        String privPassword = String.format("'%s'", device.getPrivPassword());
        // === System Info ===
        String sysDescr = String.format("'%s'", device.getSysDescr());
        String sysObjectId = String.format("'%s'", device.getSysObjectId());
        String sysUpTime = String.format("'%s'", device.getSysUpTime());
        String sysContact = String.format("'%s'", device.getSysContact());
        String sysName = String.format("'%s'", device.getSysName());
        String sysLocation = String.format("'%s'", device.getSysLocation());
        // === Interface Info ===
        String phyAddr = String.format("'%s'", device.getPhyAddr());
        String localIp = String.format("'%s'", device.getLocalIp());
        String groupName = String.format("'%s'", device.getGroupName());

        List<JInterface> infList = device.getInterfaces();
        // INF_MAN_REM_IP
        String infManRemIpString = infList.size() > 0 ? infList.get(0).getManualRemoteIp() : "";
        for (int i = 1; i < infList.size(); i++)
            infManRemIpString += "," + infList.get(i).getManualRemoteIp();
        infManRemIpString = String.format("'%s'", infManRemIpString);
        // INF_IS_MANUAL
        String infIsManualString = infList.size() > 0 ? infList.get(0).isManual()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsManualString += "," + (infList.get(i).isManual()?"t":"f");
        infIsManualString = String.format("'%s'", infIsManualString);
        // INF_IS_MONITOR
        String infIsMonitorString = infList.size() > 0 ? infList.get(0).isMonitored()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsMonitorString += "," + (infList.get(i).isMonitored()?"t":"f");
        infIsMonitorString = String.format("'%s'", infIsMonitorString);
        // INF_IS_POE_POWER
        String infIsPoePowerString = infList.size() > 0 ? infList.get(0).isPoePower()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsPoePowerString += "," + (infList.get(i).isPoePower()?"t":"f");
        infIsPoePowerString = String.format("'%s'", infIsPoePowerString);
        // INF_POE_SCH_NAME
        String infPoeScheduleNameString = infList.size() > 0 ? infList.get(0).getPoeScheduleName() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeScheduleNameString += "," + infList.get(i).getPoeScheduleName();
        infPoeScheduleNameString = String.format("'%s'", infPoeScheduleNameString);
        // INF_POE_START_TIME
        String infPoeStartTimeString = infList.size() > 0 ? infList.get(0).getPoeStartTime() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeStartTimeString += "," + infList.get(i).getPoeStartTime();
        infPoeStartTimeString = String.format("'%s'", infPoeStartTimeString);
        // INF_POE_END_TIME
        String infPoeEndTimeString = infList.size() > 0 ? infList.get(0).getPoeEndTime() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeEndTimeString += "," + infList.get(i).getPoeEndTime();
        infPoeEndTimeString = String.format("'%s'", infPoeEndTimeString);
        // PORT_ID
        String portIdString = infList.size() > 0 ? Integer.toString(infList.get(0).getPortId()) : "";
        for (int i = 1; i < infList.size(); i++)
        	portIdString += "," + Integer.toString(infList.get(i).getPortId());
        portIdString = String.format("'%s'", portIdString);
        // IFINDEX
        String ifIndexString = infList.size() > 0 ? Integer.toString(infList.get(0).getIfIndex()) : "";
        for (int i = 1; i < infList.size(); i++)
        	ifIndexString += "," + Integer.toString(infList.get(i).getIfIndex());
        ifIndexString = String.format("'%s'", ifIndexString);
    	// IFTYPE
        String ifTypeString = infList.size() > 0 ? infList.get(0).getIfType() : "";
        for (int i = 1; i < infList.size(); i++)
        	ifTypeString += "," + infList.get(i).getIfType();
        ifTypeString = String.format("'%s'", ifTypeString);
        // IFSPEED
        String ifSpeedString = infList.size() > 0 ? infList.get(0).getIfSpeed() : "";
        for (int i = 1; i < infList.size(); i++)
        	ifSpeedString += "," + infList.get(i).getIfSpeed();
        ifSpeedString = String.format("'%s'", ifSpeedString);
        // STACK_ID
        String stackIdString = infList.size() > 0 ? Integer.toString(infList.get(0).getStackId()) : "";
        for (int i = 1; i < infList.size(); i++)
        	stackIdString += "," + Integer.toString(infList.get(i).getStackId());
        stackIdString = String.format("'%s'", stackIdString);
        // IS_POE_PORT
        String isPoePortString = infList.size() > 0 ? infList.get(0).isPoePort()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
        	isPoePortString += "," + (infList.get(i).isPoePort()?"t":"f");
        isPoePortString = String.format("'%s'", isPoePortString);

        // === Control Info ===
        String createdTime = String.format("'%s'", sdf.format(device.getCreatedTime()));
        String aliasName = String.format("'%s'", device.getAliasName());
        String lastSeen = device.getLastSeen() != null ? String.format("'%s'", sdf.format(device.getLastSeen())) : "default"; // 'default' will let the timestamp be null
        String isAlive = String.format("'%s'", device.isAlive() == 1 ? 1 : 0);
        String deviceType = device.isVirtual() ? String.format("'vn%s'", device.getDeviceType()) : String.format("'%s'", device.getDeviceType());
        String isPoePower = String.format("'%s'", device.isPoePower() ? "true" : "false");
        
        String isMailFilter = String.format("'%s'", device.isMailFilter() ? "true" : "false");
        String pfName = String.format("'%s'", device.getProfileName());
        String pfStartTime = String.format("'%s'", device.getProfileStartTime());
        String pfEndTime = String.format("'%s'", device.getProfileEndTime());
        
        // INF_ALIAS_NAME
        String infAliasNameString = infList.size() > 0 ? infList.get(0).getAliasName() : "";
        for (int i = 1; i < infList.size(); i++)
        	infAliasNameString += "," + infList.get(i).getAliasName();
        infAliasNameString = String.format("'%s'", infAliasNameString);
        // INF_MAIL_FILTER
        String infIsMailFilterString = infList.size() > 0 ? infList.get(0).isMailFilter()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
        	infIsMailFilterString += "," + (infList.get(i).isMailFilter()?"t":"f");
        infIsMailFilterString = String.format("'%s'", infIsMailFilterString);
        // INF_PROFILE_NAME
        String infProfileNameString = infList.size() > 0 ? infList.get(0).getProfileName() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileNameString += "," + infList.get(i).getProfileName();
        infProfileNameString = String.format("'%s'", infProfileNameString);
        // INF_PROFILE_START_TIME
        String infProfileStartTimeString = infList.size() > 0 ? infList.get(0).getProfileStartTime() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileStartTimeString += "," + infList.get(i).getProfileStartTime();
        infProfileStartTimeString = String.format("'%s'", infProfileStartTimeString);
        // INF_PROFILE_END_TIME
        String infProfileEndTimeString = infList.size() > 0 ? infList.get(0).getProfileEndTime() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileEndTimeString += "," + infList.get(i).getProfileEndTime();
        infProfileEndTimeString = String.format("'%s'", infProfileEndTimeString);

        String[] values = { publicIp, publicIpFull, snmpPort, snmpTimeout, snmpVersion, readCommunity, writeCommunity,
        		// === Snmp v3 ===
        		securityName, securityLevel, authProtocol, authPassword, privProtocol, privPassword,
                // === System Info ===
                sysDescr, sysObjectId, sysUpTime, sysContact, sysName, sysLocation,
                // === Interface Info ===
                phyAddr, localIp, infManRemIpString, infIsManualString, infIsMonitorString,
                // === Control Info ===
                createdTime, aliasName, lastSeen, isAlive, deviceType, 
                // === POE Schedule ===
                isPoePower, infIsPoePowerString, infPoeScheduleNameString, infPoeStartTimeString, infPoeEndTimeString,
                // === If Table Data ===
                portIdString, ifIndexString, ifTypeString, ifSpeedString, stackIdString, isPoePortString,
                // === Device profile setting ===
                isMailFilter, pfName, pfStartTime, pfEndTime,
                // === Interface alias and profile ===
                infAliasNameString, infIsMailFilterString, infProfileNameString, infProfileStartTimeString, infProfileEndTimeString, groupName
                
                
        };

        return dbInst.insert(tableName, values);
    }
    
    public boolean removeDevice(final JDevice device) {
        if (device == null) return false;
        String filterColumnName = String.format("%s", "PUBLIC_IP"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", device.getPublicIp());

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }

    public List<JDevice> getAllDevice() {
        List<JDevice> deviceList = new ArrayList<JDevice>();

        String[][] deviceArray = dbInst.getAll(tableName);
        
        if (deviceArray == null) return deviceList;

        for (String[] data : deviceArray) {
            if (data.length != tableDefinition.length) continue;

            String publicIp = data[iPUBLIC_IP];
            boolean isVirtual = data[iDEV_TYPE].startsWith("vn");
            String deviceType = isVirtual ? data[iDEV_TYPE].substring(2) : data[iDEV_TYPE];
            int modelRevision = 1;
            String objectId = data[iSYS_OBJID];
    		JModule module = dbModule.getByObjectIdAndmodelRevision(objectId, String.valueOf(modelRevision));
            if (module == null) {
                System.out.println("No module found for " + publicIp + ", " + objectId + " when get all device.");
                continue;
            }

            JDevice device = new JDevice();
            device.setPublicIp(publicIp);
            device.setPublicIpFull(data[iPUBLIC_IP_FULL]);
            device.setSnmpPort(data[iSNMP_PORT]);
            device.setSnmpTimeout(Integer.parseInt(data[iSNMP_TIMEOUT]));
            device.setSnmpVersion(Integer.parseInt(data[iSNMP_VERSION]));
            device.setSnmpAddress(publicIp + "/" + data[iSNMP_PORT]);
            device.setReadCommunity(data[iREAD_COMMUNITY]);
            device.setWriteCommunity(data[iWRITE_COMMUNITY]);
            device.setSnmpSupport(module.getSnmpSupport());
            device.setSecurityName(data[iSECURITY_NAME]);
            device.setSecurityLevel(Integer.parseInt(data[iSECURITY_LEVEL]));
            device.setAuthProtocol(data[iAUTH_PROTOCOL]);
            device.setAuthPassword(data[iAUTH_PASS]);
            device.setPrivProtocol(data[iPRIV_PROTOCOL]);
            device.setPrivPassword(data[iPRIV_PASS]);
            device.setSysDescr(data[iSYS_DESRC]);
            device.setSysObjectId(objectId);
            device.setSysUpTime(data[iSYS_UPTIME]);
            device.setSysContact(data[iSYS_CONTACT]);
            device.setSysName(data[iSYS_NAME]);
            device.setSysLocation(data[iSYS_LOCAT]);
            device.setBrandName(module.getBrandName());
            device.setModelName(module.getModelName());
            device.setModelRevision(module.getModelRevision());
            device.setPhyAddr(data[iPHY_ADDR]);
            device.setLocalIp(data[iLOCAL_IP]);
            device.setIfCPU(module.getInfCpuIndex());
            device.setInfNum(module.getInfNum());
            device.setRj45Num(module.getRj45Num());
            device.setFiberNum(module.getFiberNum());
            device.setStackNum(1);
            if(data[iGROUP_NAME].length() > 0 && data[iGROUP_NAME] != null && data[iGROUP_NAME].equals("null")){
            	device.setGroupName("--");
            }
            else{
            	device.setGroupName(data[iGROUP_NAME]);
            }
            

            String[] ifIndex = data[iIFINDEX].split(",");
            String[] portId = data[iPORT_ID].split(",");
            String[] ifType = data[iIFTYPE].split(",");
            String[] ifSpeed = data[iIFSPEED].split(",");
            String[] stackId = data[iSTACK_ID].split(",");
            String[] infManRemIp = data[iINF_MAN_IP].split(",");
            String[] infIsManual = data[iINF_IS_MANU].split(",");
            String[] infIsMonitor = data[iINF_IS_MONI].split(",");
            String[] infIsPoePower = data[iINF_IS_POE_POWER].split(",");
            String[] infPoeScheduleName = data[iINF_POE_SCH_NAME].split(",");
            String[] infPoeStartTime = data[iINF_POE_START_TIME].split(",");
            String[] infPoeEndTime = data[iINF_POE_END_TIME].split(",");
            String[] isPoePort = data[iIS_POE_PORT].split(",");
            
            device.setMailFilter(data[iIS_MAIL_FILTER].equals("") ? false : data[iIS_MAIL_FILTER].equals("true"));
            device.setProfileName(data[iPF_SCH_NAME]);
            device.setProfileStartTime(data[iPF_START_TIME]);
            device.setProfileEndTime(data[iPF_END_TIME]);
            
            String[] infAilasName = data[iINF_ALIAS_NAME].split(",");
            String[] infIsMailFilter = data[iINF_IS_MAIL_FILTER].split(",");
            String[] infProfileName = data[iINF_PF_SCH_NAME].split(",");
            String[] infProfileStartTime = data[iINF_PF_START_TIME].split(",");
            String[] infProfileEndTime = data[iINF_PF_END_TIME].split(",");
            
            if(device.getSnmpSupport() < 1){
	            List<JInterface> interfaces = new ArrayList<JInterface>();
	            
	            int count = 1;
	            for (int i = 0; i < device.getInfNum(); i++) {
	                JInterface inf = new JInterface();
	                if(module.getIfTypeList().get(i).equals("eth")){
	                	inf.setPortId(count++);
	                	inf.setStackId(1);
	                }
	                inf.setInfIndex(i + 1);
	                inf.setIfName("");
	                inf.setIfDescr("");
	                inf.setIfAlias("");
	                inf.setIfIndex(ifIndex.length > i ? Integer.parseInt(ifIndex[i]) : null);
	                inf.setIfType(module.getIfTypeList().get(i));
	                inf.setIfPhysAddress("");
	                inf.setJackType(module.getJackList().get(i));       // no reason it will wrong
	                inf.setMaxSpeed(module.getSpeedList().get(i));
	                inf.setLldpRemoteId("");
	                inf.setManualRemoteIp(infManRemIp.length > i ? infManRemIp[i] : "");
	                inf.setManual(infIsManual.length > i ? infIsManual[i].equals("t") : false);
	                inf.setMonitored(infIsMonitor.length > i ? infIsMonitor[i].equals("t") : false);
	                inf.setPoePower(infIsPoePower.length > i ? infIsPoePower[i].equals("t") : false);
	                inf.setPoeScheduleName(infPoeScheduleName.length > i ? infPoeScheduleName[i] : "");
	                inf.setPoeStartTime(infPoeStartTime.length > i ? infPoeStartTime[i] : "");
	                inf.setPoeEndTime(infPoeEndTime.length > i ? infPoeEndTime[i] : "");
	                if(infAilasName.length > i && infAilasName[i] != null && infAilasName[i].equals("null")){
	                	infAilasName[i] = "";
	                }
	                inf.setAliasName(infAilasName.length > i ? infAilasName[i] : "");
	                inf.setMailFilter(infIsMailFilter.length > i ? infIsMailFilter[i].equals("t") : false);
	                inf.setProfileName(infProfileName.length > i ? infProfileName[i] : "");
	                inf.setProfileStartTime(infProfileStartTime.length > i ? infProfileStartTime[i] : "");
	                inf.setProfileEndTime(infProfileEndTime.length > i ? infProfileEndTime[i] : "");
	                interfaces.add(inf);
	            }
	            device.setInterfaces(interfaces);
            }
            else if(device.getSnmpSupport() > 0){
            	//int stackNum = 0;
            	String stackName = "";
            	List<String> stackItems = new ArrayList<String>();
            	List<JInterface> interfaces = new ArrayList<JInterface>();
            	for (int i = 0; i < ifIndex.length; i++) {
                    JInterface inf = new JInterface();
                    inf.setPortId(portId.length > i ? Integer.parseInt(portId[i]) : null);
                    inf.setInfIndex(i + 1);
                    inf.setIfIndex(ifIndex.length > i ? Integer.parseInt(ifIndex[i]) : null);
                    inf.setIfName("");
                    inf.setIfDescr("");
                    inf.setIfAlias("");
                    inf.setIfType(ifType.length > i ? ifType[i] : "");
                    inf.setIfSpeed(ifSpeed.length > i ? ifSpeed[i] : "");
                    inf.setIfPhysAddress("");
                    inf.setStackId(stackId.length > i ? Integer.parseInt(stackId[i]) : null);
                    	
	                    if(device.getBrandName().equals("CISCO") && inf.getIfType().equals("eth")){
		                    if(!Integer.toString(inf.getStackId()).equals(stackName)){
		                    	stackName = Integer.toString(inf.getStackId());
		                    }
		                    if(stackItems.isEmpty()){
		                		stackItems.add(stackName);
		                	}
		                    else{
		                		boolean isNotExist = true;
			                	for (int j=0;j<stackItems.size();j++){
			                		if(stackItems.get(j).equals(stackName)){
			                			isNotExist = false;
			                		}
			                	}
			                	if(isNotExist){
			                		stackItems.add(stackName);
			                	}
		                	}
	                    }
                    inf.setLldpRemoteId("");
                    inf.setManualRemoteIp(infManRemIp.length > i ? infManRemIp[i] : "");
                    inf.setManual(infIsManual.length > i ? infIsManual[i].equals("t") : false);
                    inf.setMonitored(infIsMonitor.length > i ? infIsMonitor[i].equals("t") : false);
                    inf.setPoePower(infIsPoePower.length > i ? infIsPoePower[i].equals("t") : false);
                    inf.setPoeScheduleName(infPoeScheduleName.length > i ? infPoeScheduleName[i] : "");
                    inf.setPoeStartTime(infPoeStartTime.length > i ? infPoeStartTime[i] : "");
                    inf.setPoeEndTime(infPoeEndTime.length > i ? infPoeEndTime[i] : "");
                    inf.setPoePort(isPoePort.length > i ? isPoePort[i].equals("t") : false);
                    if(infAilasName.length > i && infAilasName[i] != null && infAilasName[i].equals("null")){
	                	infAilasName[i] = "";
	                }
                    inf.setAliasName(infAilasName.length > i ? infAilasName[i] : " ");
	                inf.setMailFilter(infIsMailFilter.length > i ? infIsMailFilter[i].equals("t") : false);
	                inf.setProfileName(infProfileName.length > i ? infProfileName[i] : "");
	                inf.setProfileStartTime(infProfileStartTime.length > i ? infProfileStartTime[i] : "");
	                inf.setProfileEndTime(infProfileEndTime.length > i ? infProfileEndTime[i] : "");
                    interfaces.add(inf);
                }
                device.setInterfaces(interfaces);
                if(!stackName.equals("")){
                	device.setStackNum(stackItems.size());
                }
                else {
                	device.setStackNum(1);
                }
                device.setInfNum(interfaces.size());
                
                /*System.out.println("====================="+device.getPublicIp()+"=====================");
                for (int i = 0; i < ifIndex.length; i++) {
                    JInterface inf = device.getInterfaces().get(i);
                	System.out.println("IfIndex= "+inf.getIfIndex()+" portId= "+inf.getPortId()+" stackCount= "+inf.getStackId()+" is poe port= "+inf.isPoePort());
                }*/
            }

            try {
                device.setCreatedTime(sdf.parse(data[iADD_TIME]));
            } catch (ParseException e) {
                System.out.println("Read device " + publicIp + "from db exception when parsing createdTime. Fill current and continue.");
                device.setCreatedTime(new Date()); // note
            }

            device.setAliasName(data[iALIAS_NAME]);

            try {
                device.setLastSeen(sdf.parse(data[iLAST_SEEN]));
            } catch (ParseException e) {
                System.out.println("Read device " + publicIp + "from db exception when parsing lastSeen. Fill null and continue.");
                device.setLastSeen(null);
            }
			
            if (data[iIS_ALIVE].equals("true")) {
				device.setAlive(1);
			}else if (data[iIS_ALIVE].equals("false")) {
			}else{
				device.setAlive(Integer.parseInt(data[iIS_ALIVE]));
			}
            device.setDeviceType(deviceType);
            device.setVirtual(isVirtual);

            device.setSupportHostResource(module.isSupHostResource());
            device.setSupportLinkState(module.isSupLinkState());
            device.setSupportNegoState(module.isSupNegoState());
            device.setSupportRxTxOctet(module.isSupRxTxOctet());
            device.setSupportPacketType(module.isSupPacketType());
            device.setSupportRmon(module.isSupRmon());
            device.setSupportPvid(module.isSupPvid());
            device.setSupportVlan(module.isSupVlan());
            device.setSupportGvrp(module.isSupGvrp());
            device.setSupportPoe(module.isSupPoe());
            device.setSupportTrap(module.isSupTrap());
            device.setSupportLldp(module.isSupLldp());
            device.setSupportRStp(module.isSupRStp());
            device.setSupportMStp(module.isSupMStp());
            device.setSupportEdgeCoreResource(module.isSupEdgeCoreResource());
            device.setOctet64(module.isSupOctet64());
            device.setSupportEgcoTrap(module.isSupEgcoTrap());
            
            device.setPoePower(data[iIS_POE_POWER].equals("true"));

            deviceList.add(device);
        }

        return deviceList;
    }

    public boolean updateReadCommunity(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "READ_COMMUNITY", device.getReadCommunity(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateWriteCommunity(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "WRITE_COMMUNITY", device.getWriteCommunity(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateAliasName(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "ALIAS_NAME", device.getAliasName(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceSnmpVersion(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "SNMP_VERSION", String.valueOf(device.getSnmpVersion()), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceSnmpTimeout(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "SNMP_TIMEOUT", String.valueOf(device.getSnmpTimeout()), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceReadCommunity(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "READ_COMMUNITY", device.getReadCommunity(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceSecurityName(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "SECURITY_NAME", device.getSecurityName(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceSecurityLevel(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "SECURITY_LEVEL", Integer.toString(device.getSecurityLevel()), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceAuthProtocol(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "AUTH_PROTOCOL", device.getAuthProtocol(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceAuthPassword(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "AUTH_PASS", device.getAuthPassword(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevicePrivProtocol(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PRIV_PROTOCOL", device.getPrivProtocol(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevicePrivPassword(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PRIV_PASS", device.getPrivPassword(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDeviceWriteCommunity(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "WRITE_COMMUNITY", device.getWriteCommunity(), "PUBLIC_IP", device.getPublicIp());
    }

    public boolean updateLastSeen(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "LAST_SEEN", sdf.format(device.getLastSeen()), "PUBLIC_IP", device.getPublicIp());
    }

    public boolean updateAlive(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "IS_ALIVE", (device.isAlive() != 0 ? Integer.toString(device.isAlive()) : "0"), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateMacAddr(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PHY_ADDR", device.getPhyAddr(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateIfIndex(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "IFINDEX", device.getIfCPU(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updatePoePower(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "IS_POE_POWER", (device.isPoePower() ? "true" : "false"), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfaceIsManual(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infIsManualString = infList.size() > 0 ? infList.get(0).isManual()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsManualString += "," + (infList.get(i).isManual()?"t":"f");

        return dbInst.update(tableName, "INF_IS_MANUAL", infIsManualString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfaceManualRemoteIp(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infManRemIpString = infList.size() > 0 ? infList.get(0).getManualRemoteIp() : "";
        for (int i = 1; i < infList.size(); i++)
            infManRemIpString += "," + infList.get(i).getManualRemoteIp();

        return dbInst.update(tableName, "INF_MAN_REM_IP", infManRemIpString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfaceIsMonitored(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infIsMonitorString = infList.size() > 0 ? infList.get(0).isMonitored()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsMonitorString += "," + (infList.get(i).isMonitored()?"t":"f");

        return dbInst.update(tableName, "INF_IS_MONITOR", infIsMonitorString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfaceIsPoePower(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infIsPoePowerString = infList.size() > 0 ? infList.get(0).isPoePower()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
            infIsPoePowerString += "," + (infList.get(i).isPoePower()?"t":"f");

        return dbInst.update(tableName, "INF_IS_POE_POWER", infIsPoePowerString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfacePoeScheduleName(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infPoeScheduleNameString = infList.size() > 0 ? infList.get(0).getPoeScheduleName() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeScheduleNameString += "," + infList.get(i).getPoeScheduleName();

        return dbInst.update(tableName, "INF_POE_SCH_NAME", infPoeScheduleNameString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfacePoeStartTime(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infPoeStartTimeString = infList.size() > 0 ? infList.get(0).getPoeStartTime() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeStartTimeString += "," + infList.get(i).getPoeStartTime();

        return dbInst.update(tableName, "INF_POE_START_TIME", infPoeStartTimeString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInterfacePoeEndTime(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infPoeEndTimeString = infList.size() > 0 ? infList.get(0).getPoeEndTime() : "";
        for (int i = 1; i < infList.size(); i++)
            infPoeEndTimeString += "," + infList.get(i).getPoeEndTime();

        return dbInst.update(tableName, "INF_POE_END_TIME", infPoeEndTimeString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInfAliasName(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infAliasNameString = infList.size() > 0 ? infList.get(0).getAliasName() : "";
        for (int i = 1; i < infList.size(); i++)
        	infAliasNameString += "," + infList.get(i).getAliasName();

        return dbInst.update(tableName, "INF_ALIAS_NAME", infAliasNameString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevMailFilter(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "IS_MAIL_FILTER", (device.isMailFilter() ? "true" : "false"), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevProfileName(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PF_SCH_NAME", device.getProfileName(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevProfileStartTime(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PF_START_TIME", device.getProfileStartTime(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateDevProfileEndTime(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "PF_END_TIME", device.getProfileEndTime(), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInfMailFilter(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infMailFilterString = infList.size() > 0 ? infList.get(0).isMailFilter()?"t":"f" : "";
        for (int i = 1; i < infList.size(); i++)
        	infMailFilterString += "," + (infList.get(i).isMailFilter()?"t":"f");

        return dbInst.update(tableName, "INF_IS_MAIL_FILTER", infMailFilterString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInfProfileName(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infProfileNameString = infList.size() > 0 ? infList.get(0).getProfileName() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileNameString += "," + infList.get(i).getProfileName();

        return dbInst.update(tableName, "INF_PF_SCH_NAME", infProfileNameString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInfProfileStartTime(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infProfileStartTimeString = infList.size() > 0 ? infList.get(0).getProfileStartTime() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileStartTimeString += "," + infList.get(i).getProfileStartTime();

        return dbInst.update(tableName, "INF_PF_START_TIME", infProfileStartTimeString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateInfProfileEndTime(final JDevice device) {
        if (device == null) return false;

        List<JInterface> infList = device.getInterfaces();
        String infProfileEndTimeString = infList.size() > 0 ? infList.get(0).getProfileEndTime() : "";
        for (int i = 1; i < infList.size(); i++)
        	infProfileEndTimeString += "," + infList.get(i).getProfileEndTime();

        return dbInst.update(tableName, "INF_PF_END_TIME", infProfileEndTimeString, "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateGroupName(final JDevice device) {
        if (device == null) return false;

        return dbInst.update(tableName, "GROUP_NAME", device.getGroupName(), "PUBLIC_IP", device.getPublicIp());
    }
    
    
    /*public boolean updateSupportList(final JDevice device) {
        if (device == null) return false;
        
        String supportString = device.isSupportLinkState() ? "t" : "f";
        supportString += "," + (device.isSupportIfType() ? "t" : "f");
        supportString += "," + (device.isSupportNegoState() ? "t" : "f");
        supportString += "," + (device.isSupportRxTxOctet() ? "t" : "f");
        supportString += "," + (device.isOctet64() ? "t" : "f");
        supportString += "," + (device.isSupportVlan() ? "t" : "f");
        supportString += "," + (device.isSupportPoE() ? "t" : "f");
        supportString += "," + (device.isSupportLLDP() ? "t" : "f");
        supportString += "," + (device.isSupportSTP() ? "t" : "f");
        
        return dbInst.update(tableName, "SUPPORT_LIST", supportString, "PUBLIC_IP", device.getPublicIp());
    }*/
    
    public TableData exportDeviceList() {
        
    	TableData table = dbInst.getDeviceListTable(tableName);
    	
    	return table;
    }
}
