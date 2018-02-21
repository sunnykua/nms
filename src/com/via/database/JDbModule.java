package com.via.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JDbModule {
    private JDatabase dbInst;
    private String tableName;
    //private String tableNameInQuotes;
    private final String[] tableDefinition = {
    		"IS_DEFAULT         VARCHAR(1), ",
            "BRAND_NAME         VARCHAR(50), ",
            "MODEL_NAME         VARCHAR(100), ",
            "MODEL_REVISION     SMALLINT DEFAULT 1, ",      // start from 0, just for the same brand same model but different spec
            "DEVICE_TYPE        VARCHAR(20), ",             // l2switch, l3switch, firewall, server, ...
            "SNMP_SUPPORT       SMALLINT DEFAULT 2, ",      // 0:unavailable, 1:SNMPv1, 2:SNMPv2c, 3:SNMPv3
            "READ_COMMUNITY     VARCHAR(50), ",
            "WRITE_COMMUNITY    VARCHAR(50), ",
            "OBJECT_ID          VARCHAR(50), ",
            "INF_IDX_CPU        VARCHAR(10), ",
            "SNMP_TIMEOUT       SMALLINT DEFAULT 0, ",
            "INF_NUM            SMALLINT DEFAULT 0, ",
            "RJ45_NUM           SMALLINT DEFAULT 0, ",
            "FIBER_NUM          SMALLINT DEFAULT 0, ",
            "IFTYPE_LIST        VARCHAR(2000), ",
            "JACK_LIST          VARCHAR(200), ",            // jack type(rj45, fiber, unknown) for all interfaces, combine to a list
            "SPEED_LIST         VARCHAR(300), ",            // max speed for all interfaces, ex: 10 means 10Mbps, 1000 means 1Gbps
            "SUP_HOSTRESOURCE   VARCHAR(1), ",
            "SUP_LINKSTATE      VARCHAR(1), ",
            "SUP_NEGOSTATE      VARCHAR(1), ",
            "SUP_RXTXOCTET      VARCHAR(1), ",
            "SUP_PACKETTYPE     VARCHAR(1), ",                 // unicast, multicast, broadcast
            "SUP_RMON           VARCHAR(1), ",                 // packet size
            "SUP_PVID           VARCHAR(1), ",
            "SUP_VLAN           VARCHAR(1), ",
            "SUP_GVRP           VARCHAR(1), ",
            "SUP_POE            VARCHAR(1), ",
            "SUP_TRAP           VARCHAR(1), ",
            "SUP_LLDP           VARCHAR(1), ",
            "SUP_RSTP           VARCHAR(1), ",
            "SUP_MSTP           VARCHAR(1), ",
            "SUP_EDGECORERESOURCE           VARCHAR(1), ",
            "SUP_OCTET64        VARCHAR(1), ",                 // history will use this flag, so needs to figure out
            "SUP_EGCO_TRAP      VARCHAR(1) "
    };
    private int a = 0;
    private final int iDEFAULT = a++, iBRAND_NAME = a++, iMODEL_NAME = a++, iMODEL_REVISION = a++, iDEVICE_TYPE = a++, iSNMP_SUPPORT = a++, iREAD_COMMUNITY = a++, iWRITE_COMMUNITY = a++,
            iOBJECT_ID = a++, iINF_IDX_CPU = a++, iSNMP_TIMEOUT = a++, iINF_NUM = a++, iRJ45_NUM = a++, iFIBER_NUM = a++, iIFTYPE_LIST = a++, iJACK_LIST = a++, iSPEED_LIST = a++,
            iSUP_HOSTRESOURCE = a++, iSUP_LINKSTATE = a++, iSUP_NEGOSTATE = a++, iSUP_RXTXOCTET = a++, iSUP_PACKETTYPE = a++, iSUP_RMON = a++, iSUP_PVID = a++,
            iSUP_VLAN = a++, iSUP_GVRP = a++, iSUP_POE = a++, iSUP_TRAP = a++, iSUP_LLDP = a++, iSUP_RSTP = a++, iSUP_MSTP = a++, iSUP_EDGECORERESOURCE = a++, iSUP_OCTET64 = a++, iSUP_EGCO_TRAP = a++;
    
    public JDbModule(final JDatabase database, final String tableName) {
        this.dbInst = database;
        this.tableName = tableName;
        //this.tableNameInQuotes = "\"" + tableName + "\"";
    }
    
    public boolean isTableExisted() {
        return dbInst.isTableExisted(tableName);
    }
    
    public boolean createTable() {
        if (isTableExisted()) {
            System.out.println("Module table " + tableName + " exists.");
            return true;
        }
        
        String definition = "";
        for (String s : tableDefinition) definition += s;
        boolean success = dbInst.createTable(tableName, definition);
        if (success) {
            System.out.println("Module table " + tableName + " creates success.");
        }

        return success;
    }
    
    public boolean isModelNameExisted(final String modelName) {
        String query = String.format("SELECT MODEL_NAME FROM %s WHERE UPPER(MODEL_NAME) LIKE UPPER('%s')" , tableName, modelName);

        String[][] result = dbInst.getByQuery(query);
        
        System.out.println();

        if (result != null && result[0][0].equalsIgnoreCase(modelName)) {
            System.out.println("Model Name is existed.");
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean addModule(final JModule module) {
        if (module == null) {
            System.out.println("input module is null.");
            return false;
        }
        if ((module.getJackList() == null ) ||
                (module.getSpeedList() == null)) {
            System.out.println("jack list or speed list in input module does not has correct size.");
            return false;
        }
        
        String fdDefault = String.format("'%s'", module.isDefault() ? "T" : "F");
        String fdBrandName = String.format("'%s'", module.getBrandName());
        String fdModelName = String.format("'%s'", module.getModelName());
        String fdModelRevision = String.format("%s", module.getModelRevision());        // TODO: use this variable to allow same model
        String fdDeviceType = String.format("'%s'", module.getDeviceType());
        String fdSnmpSupport = String.format("%s", module.getSnmpSupport());
        String fdReadCommunity = String.format("'%s'", module.getReadCommunity());
        String fdWriteCommunity = String.format("'%s'", module.getWriteCommunity());
        String fdObjectId = String.format("'%s'", module.getObjectId());
        String fdInfIndexCPU = String.format("'%s'", module.getInfCpuIndex());
        String fdSnmpTimeout = String.format("%s", module.getSnmpTimeout());
        String fdInfNum = String.format("%s", module.getInfNum());
        String fdRj45Num = String.format("%s", module.getRj45Num());
        String fdFiberNum = String.format("%s", module.getFiberNum());
        
        String tempifType = module.getIfTypeList().size() > 0 ? module.getIfTypeList().get(0) : "";
        for (int i = 1; i < module.getIfTypeList().size(); i++) tempifType += "," + module.getIfTypeList().get(i);
        String fdIfTypeList = String.format("'%s'", tempifType);
        
        String tempJack = module.getJackList().size() > 0 ? module.getJackList().get(0) : "";
        for (int i = 1; i < module.getJackList().size(); i++) tempJack += "," + module.getJackList().get(i);
        tempJack = tempJack.replace("rj45", "rj");
        tempJack = tempJack.replace("fiber", "fb");
        tempJack = tempJack.replace("unknown", "un");
        String fdJackList = String.format("'%s'", tempJack);
        
        String tempSpeed = module.getSpeedList().size() > 0 ? module.getSpeedList().get(0) : "";
        for (int i = 1; i < module.getSpeedList().size(); i++) tempSpeed += "," + module.getSpeedList().get(i);
        String fdSpeedList = String.format("'%s'", tempSpeed);
        
        String fdSupHostResource = String.format("'%s'", module.isSupHostResource() ? "T" : "F");
        String fdSupLinkState = String.format("'%s'", module.isSupLinkState() ? "T" : "F");
        String fdSupNegoState = String.format("'%s'", module.isSupNegoState() ? "T" : "F");
        String fdSupRxTxOctet = String.format("'%s'", module.isSupRxTxOctet() ? "T" : "F");
        String fdSupPacketType = String.format("'%s'", module.isSupPacketType() ? "T" : "F");
        String fdSupRmon = String.format("'%s'", module.isSupRmon() ? "T" : "F");
        String fdSupPvid = String.format("'%s'", module.isSupPvid() ? "T" : "F");
        String fdSupVlan = String.format("'%s'", module.isSupVlan() ? "T" : "F");
        String fdSupGvrp = String .format("'%s'", module.isSupGvrp() ? "T" : "F");
        String fdSupPoe = String.format("'%s'", module.isSupPoe() ? "T" : "F");
        String fdSupTrap = String.format("'%s'", module.isSupTrap() ? "T" : "F");
        String fdSupLldp = String.format("'%s'", module.isSupLldp() ? "T" : "F");
        String fdSupRStp = String.format("'%s'", module.isSupRStp() ? "T" : "F");
        String fdSupMStp = String.format("'%s'", module.isSupMStp() ? "T" : "F");
        String fdSupEdgeCoreResource = String.format("'%s'", module.isSupEdgeCoreResource() ? "T" : "F");
        String fdSupOctet64 = String.format("'%s'", module.isSupOctet64() ? "T" : "F");
        String fdSupEgcoTrap = String.format("'%s'", module.isSupEgcoTrap() ? "T" : "F");
        
        String[] values = { fdDefault, fdBrandName, fdModelName, fdModelRevision, fdDeviceType, fdSnmpSupport, fdReadCommunity, fdWriteCommunity, fdObjectId, fdInfIndexCPU,
        		fdSnmpTimeout, fdInfNum, fdRj45Num, fdFiberNum, fdIfTypeList, fdJackList, fdSpeedList, fdSupHostResource, fdSupLinkState, fdSupNegoState, fdSupRxTxOctet, fdSupPacketType, fdSupRmon, fdSupPvid,
                fdSupVlan, fdSupGvrp, fdSupPoe, fdSupTrap, fdSupLldp, fdSupRStp, fdSupMStp, fdSupEdgeCoreResource, fdSupOctet64, fdSupEgcoTrap
        };
        
        return dbInst.insert(tableName, values);
    }

    public boolean removeModule(final String model) {
        if (model == null) return false;
        String filterColumnName = String.format("%s", "MODEL_NAME"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", model);

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }
    
    public boolean setModule(final JModule module) {
        if (module == null) {
            System.out.println("input module is null.");
            return false;
        }
        if ((module.getJackList() == null ) ||
                (module.getSpeedList() == null)) {
            System.out.println("jack list or speed list in input module does not has correct size.");
            return false;
        }
        
        String fdDefault = String.format("'%s'", module.isDefault() ? "T" : "F");
        String fdBrandName = String.format("'%s'", module.getBrandName());
        String fdModelName = String.format("'%s'", module.getModelName());
        String fdModelRevision = String.format("%s", module.getModelRevision());        // TODO: use this variable to allow same model
        String fdDeviceType = String.format("'%s'", module.getDeviceType());
        String fdSnmpSupport = String.format("%s", module.getSnmpSupport());
        String fdReadCommunity = String.format("'%s'", module.getReadCommunity());
        String fdWriteCommunity = String.format("'%s'", module.getWriteCommunity());
        String fdObjectId = String.format("'%s'", module.getObjectId());
        String fdInfIndexCPU = String.format("'%s'", module.getInfCpuIndex());
        String fdSnmpTimeout = String.format("%s", module.getSnmpTimeout());
        String fdInfNum = String.format("%s", module.getInfNum());
        String fdRj45Num = String.format("%s", module.getRj45Num());
        String fdFiberNum = String.format("%s", module.getFiberNum());
        
        String tempifType = module.getIfTypeList().size() > 0 ? module.getIfTypeList().get(0) : "";
        for (int i = 1; i < module.getIfTypeList().size(); i++) tempifType += "," + module.getIfTypeList().get(i);
        String fdIfTypeList = String.format("'%s'", tempifType);
        
        String tempJack = module.getJackList().size() > 0 ? module.getJackList().get(0) : "";
        for (int i = 1; i < module.getJackList().size(); i++) tempJack += "," + module.getJackList().get(i);
        tempJack = tempJack.replace("rj45", "rj");
        tempJack = tempJack.replace("fiber", "fb");
        tempJack = tempJack.replace("unknown", "un");
        String fdJackList = String.format("'%s'", tempJack);
        
        String tempSpeed = module.getSpeedList().size() > 0 ? module.getSpeedList().get(0) : "";
        for (int i = 1; i < module.getSpeedList().size(); i++) tempSpeed += "," + module.getSpeedList().get(i);
        String fdSpeedList = String.format("'%s'", tempSpeed);
        
        String fdSupHostResource = String.format("'%s'", module.isSupHostResource() ? "T" : "F");
        String fdSupLinkState = String.format("'%s'", module.isSupLinkState() ? "T" : "F");
        String fdSupNegoState = String.format("'%s'", module.isSupNegoState() ? "T" : "F");
        String fdSupRxTxOctet = String.format("'%s'", module.isSupRxTxOctet() ? "T" : "F");
        String fdSupPacketType = String.format("'%s'", module.isSupPacketType() ? "T" : "F");
        String fdSupRmon = String.format("'%s'", module.isSupRmon() ? "T" : "F");
        String fdSupPvid = String.format("'%s'", module.isSupPvid() ? "T" : "F");
        String fdSupVlan = String.format("'%s'", module.isSupVlan() ? "T" : "F");
        String fdSupGvrp = String .format("'%s'", module.isSupGvrp() ? "T" : "F");
        String fdSupPoe = String.format("'%s'", module.isSupPoe() ? "T" : "F");
        String fdSupTrap = String.format("'%s'", module.isSupTrap() ? "T" : "F");
        String fdSupLldp = String.format("'%s'", module.isSupLldp() ? "T" : "F");
        String fdSupRStp = String.format("'%s'", module.isSupRStp() ? "T" : "F");
        String fdSupMStp = String.format("'%s'", module.isSupMStp() ? "T" : "F");
        String fdSupEdgeCoreResource = String.format("'%s'", module.isSupEdgeCoreResource() ? "T" : "F");
        String fdSupOctet64 = String.format("'%s'", module.isSupOctet64() ? "T" : "F");
        String fdSupEgcoTrap = String.format("'%s'", module.isSupEgcoTrap() ? "T" : "F");
        
        /*String[] values = { fdDefault, fdBrandName, fdModelName, fdModelRevision, fdDeviceType, fdSnmpSupport, fdReadCommunity, fdWriteCommunity, fdObjectId, fdInfIndexCPU,
        		fdSnmpTimeout, fdInfNum, fdRj45Num, fdFiberNum, fdIfTypeList, fdJackList, fdSpeedList, fdSupHostResource, fdSupLinkState, fdSupNegoState, fdSupRxTxOctet, fdSupPacketType, fdSupRmon, fdSupPvid,
                fdSupVlan, fdSupGvrp, fdSupPoe, fdSupTrap, fdSupLldp, fdSupRStp, fdSupMStp, fdSupEdgeCoreResource, fdSupOctet64, fdSupEgcoTrap
        };*/
        
        String query = String.format("UPDATE %s SET BRAND_NAME = %s, MODEL_REVISION = %s, DEVICE_TYPE = %s, SNMP_SUPPORT = %s, WRITE_COMMUNITY = %s, OBJECT_ID = %s, INF_IDX_CPU = %s, SNMP_TIMEOUT = %s, INF_NUM = %s, RJ45_NUM = %s, FIBER_NUM = %s, JACK_LIST = %s, SPEED_LIST = %s, SUP_HOSTRESOURCE = %s, SUP_LINKSTATE = %s, SUP_NEGOSTATE = %s, SUP_RXTXOCTET = %s, SUP_PACKETTYPE = %s, SUP_RMON = %s, SUP_PVID = %s, SUP_VLAN = %s, SUP_GVRP = %s, SUP_POE = %s, SUP_TRAP = %s, SUP_LLDP = %s, SUP_RSTP = %s, SUP_MSTP = %s, SUP_EDGECORERESOURCE = %s, SUP_OCTET64 = %s, SUP_EGCO_TRAP = %s WHERE MODEL_NAME = %s",
        		tableName, fdBrandName, fdModelRevision, fdDeviceType, fdSnmpSupport, fdWriteCommunity, fdObjectId, fdInfIndexCPU, fdSnmpTimeout, fdInfNum, fdRj45Num, fdFiberNum, fdJackList, fdSpeedList, fdSupHostResource, fdSupLinkState, fdSupNegoState, fdSupRxTxOctet, fdSupPacketType, fdSupRmon, fdSupPvid, fdSupVlan, fdSupGvrp, fdSupPoe, fdSupTrap, fdSupLldp, fdSupRStp, fdSupMStp, fdSupEdgeCoreResource, fdSupOctet64, fdSupEgcoTrap, fdModelName );
        
        return dbInst.execute(query);
    }
    
    public List<JModule> getAllModule() {
        List<JModule> moduleList = new ArrayList<JModule>();
        
        String[][] moduleArray = dbInst.getAll(tableName);
        if (moduleArray == null) return moduleList;
        
        for (String[] data : moduleArray) {
            JModule module = createModule(data);
            if (module != null) moduleList.add(module);
        }
        
        return moduleList;
    }
    
    public List<JModule> getNonManagedModule() {
        List<JModule> moduleList = new ArrayList<JModule>();
        
        String[][] moduleArray = dbInst.getByColumn(tableName, new String[]{"SNMP_SUPPORT < 0"});
        if(moduleArray!=null){
	        for (String[] data : moduleArray) {
	            JModule module = createModule(data);
	            if (module != null) moduleList.add(module);
	        }
	        return moduleList;
        }
        
        return null;
    }
    
    public List<JModule> getPingManagedModule() {
        List<JModule> moduleList = new ArrayList<JModule>();
        
        String[][] moduleArray = dbInst.getByColumn(tableName, new String[]{"SNMP_SUPPORT = 0"});
        if(moduleArray!=null){
	        for (String[] data : moduleArray) {
	            JModule module = createModule(data);
	            if (module != null) moduleList.add(module);
	        }
	        return moduleList;
        }
        
        return null;
    }
    
    public JModule getByObjectId(final String objectId) {
        String filterColumnName = "OBJECT_ID";
        String filterColumnValue = String.format("'%s'", objectId);
        
        String[][] result = dbInst.getByColumn(tableName, filterColumnName, filterColumnValue);
        if (result == null || result.length == 0) return null;
        return createModule(result[0]);
    }
    public JModule getByObjectIdAndmodelRevision(final String objectId, final String modelRevision ) {
    	
    	String[][] result = dbInst.getByColumn(tableName,new String[]{"OBJECT_ID='"+objectId+"'","MODEL_REVISION="+modelRevision});
        if (result == null || result.length == 0) return null;
        return createModule(result[0]);
    }
    
    public void addDefaultModule() {
        if (!isTableExisted()) {
            System.out.println("Module table is not found when attempt to add default modules.");
            return;
        }
        
        // 1.HOSTRESOURCE  2.LINKSTATE  3.NEGOSTATE  4.RXTXOCTET  5.PACKETTYPE  6.RMON  7.PVID  8.VLAN  9.GVRP  10.POE  11.TRAP  12.LLDP  13.RSTP  14.MSTP  15.OCTET64
        String[][] moduleData = {
                {"T", "VIA Networking", "VNT5103-28PS", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.3742.10.5103.11", "1001", "1000", "33", "24", "4",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb,un,un,un,un,un",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,10,10,10,10,10",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T"
                },
                {"T", "Edge-Core", "VNT5013B24SS", "1", "l3switch", "3", "public", "private", "1.3.6.1.4.1.259.10.1.5", "1001", "1000", "24", "24", "0",
                	"eth",
                    "fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "F", "T", "T", "T", "T", "F", "T", "T"
                },
                {"T", "ZyXEL", "ZyWALL USG 1000", "1", "firewall", "3", "public", "private", "1.3.6.1.4.1.890.1.15", "7", "2000", "11", "5", "0",
                	"eth",
                    "un,un,un,un,un,un,rj,rj,rj,rj,rj",
                    "10,10,10,0,0,0,1000,1000,1000,0,0",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "VNT5801", "1", "wlanAC", "3", "public", "private", "1.3.6.1.4.1.3742.10.5801.1", "2", "1000", "7", "4", "0",
                	"eth",
                    "un,un,un,rj,rj,rj,rj",
                    "10,10,10,1000,1000,1000,1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                //new ac has 5 interface
                /*{"T", "VIA Networking", "VNT5801", "1", "wlanAC", "3", "public", "private", "1.3.6.1.4.1.3742.10.5801.1", "2", "1000", "5", "4", "0",
                    "eth",
                    "un,rj,rj,rj,rj",
                    "10,1000,0,0,0",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },*/
                {"T", "VIA Networking", "VNT5901", "1", "wlanAP", "3", "public", "private", "1.3.6.1.4.1.3742.10.5901.1", "2", "1000", "2", "1", "0",
                	"unknown,eth",
                    "un,rj",
                    "0,1000",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "VIA Networking", "VNT8011", "1", "NMS", "3", "public", "private", "1.3.6.1.4.1.3742.10.5601.1", "2", "1000", "5", "5", "0",
                	"eth",
                    "un,rj,rj,rj,rj",
                    "10,1000,10,10,10",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "Microsoft", "Windows NT Workstation", "1", "pc", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.1", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "Microsoft", "Windows NT Server", "1", "server", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.2", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "Microsoft", "Windows NT DC", "1", "server", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.3", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "VIA", "VIA MGV Chief Server", "1", "MGVChiefServer", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.5.1.0", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "VIA", "VIA MGV Command Server", "1", "MGVCommandServer", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.5.2.0", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "VIA", "VIA MGV Player", "1", "MGVPlayer", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.6.1.0", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                //Del
                {"T", "VIA", "VIA MGV Server", "1", "MGVServer", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.1.1.0", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                //Del
                {"T", "VIA", "VIA MGV Player", "1", "MGVPlayer", "2", "public", "private", "1.3.6.1.4.1.311.1.1.3.1.1.2.0", "-", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "Dopod", "CacheBoxsrv", "1", "server", "3", "dopod", "private", "1.3.6.1.4.1.8072.3.2.10", "2", "1000", "3", "3", "0",
                	"eth",
                    "un,rj,rj",
                    "10,1000,1000",
                    "T", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "T", "F"
                },
                {"T", "Edge-Core", "ECS4210-12P", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.259.10.1.42.104", "1001", "1000", "12", "10", "2",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "F", "T", "T"
                },
                {"T", "Edge-Core", "ECS4610-26T", "1", "l3switch", "3", "public", "private", "1.3.6.1.4.1.259.10.1.1.101", "1001", "1000", "26", "24", "2",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "F", "T", "T", "T", "T", "F", "F", "T"
                },
                {"T", "CISCO", "Catalyst-2924-XL", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.9.1.217", "1", "1000", "24", "24", "0",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100",
                    "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "F"
                },
                {"T", "CISCO", "Catalyst-2916M-XL", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.9.1.171", "1", "1000", "16", "16", "0",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100",
                    "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "F"
                },
                {"T", "NGN", "S5100-28PC", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.13464.1.3.24.5", "17825793", "1000", "24", "24", "0",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "T", "F", "F", "T", "T", "F", "F", "F"
                },
                {"T", "VIA Networking", "VNT5103T24PC", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.3742.1.1493", "22001", "1000", "24", "24", "0",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "F", "F", "F", "F", "T"
                },
	            {"T", "CISCO", "Catalyst-C3750", "1", "l3switch", "3", "networking", "private", "1.3.6.1.4.1.9.1.516", "108", "1000", "52", "52", "0",
                	"eth",
  	                "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
  	                "100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,1000,1000,1000,1000",
  	                "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "T", "T", "F", "F", "F", "F"
  	       	   	},
                {"T", "CISCO", "Catalyst-C3500XL-C3H2S-M", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.9.1.248", "1", "1000", "28", "26", "0",
                	"eth",
                    "un,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,un",
                    "10,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,1000,1000,10",
                    "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "F"
                }, //203.64.139.132
                {"T", "CISCO", "Catalyst-C3560-IPBASE-M", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.9.1.633", "1", "1000", "28", "24", "2",
                	"eth",
                    "un,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,un",
                    "10,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,1000,1000,10",
                    "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "F"
                }, //203.64.139.133, 203.64.139.136, 203.64.139.137
                {"T", "CISCO", "Catalyst-C3560-IPBASEK9-M", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.9.1.1020", "1", "1000", "28", "24", "2",
                	"eth",
                    "un,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,un",
                    "10,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,1000,1000,10",
                    "F", "T", "F", "T", "T", "F", "F", "F", "F", "F", "F", "F", "T", "F", "F", "F", "F"
                },//203.64.139.134
                {"T", "D-Link", "DGS-3120-24TC", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.171.10.117.1.1", "5121", "1000", "30", "24", "0",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,un,un,un,un,un,un",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,10,10,10,10,10,10",
                    "F", "T", "F", "T", "T", "T", "T", "T", "T", "F", "T", "T", "F", "F", "F", "F", "F"
                }, //203.64.139.135, 203.64.139.225
                {"T", "D-Link", "DGS-1500-28P", "1", "l2switch", "3", "public", "private", "1.3.6.1.4.1.171.10.126.3.1", "37", "1000", "28", "24", "4",
                	"eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "T", "F", "T", "T", "F", "T", "F", "T", "T", "T", "T", "F", "F", "F", "F", "F"
                }, //203.64.139.116,203.64.139.117
                {"T", "HGiga", "Power-Family-3400", "1", "firewall", "3", "public", "private", "1.3.6.1.4.1.2021.250.10", "4", "2000", "9", "4", "0",
                	"eth",
                    "un,rj,rj,rj,rj,un,un,un,un",
                    "0,1000,1000,1000,1000,0,0,0,0",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                }, //10.1.1.1
                {"T", "Fortinet", "FortiGate-100D", "1", "firewall", "3", "public", "private", "1.3.6.1.4.1.12356.101.1.1004", "1", "2000", "9", "8", "0",
                	"eth",
                    "rj,rj,rj,un,rj,rj,rj,rj,rj",
                    "1000,1000,1000,0,1000,1000,1000,1000,1000",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                }, //203.64.139.239
                {"T", "Fortinet", "FortiGate-240D", "1", "firewall", "3", "public", "private", "1.3.6.1.4.1.12356.101.1.2006", "12", "2000", "14", "10", "0",
                	"eth",
                    "un,rj,rj,rj,rj,rj,rj,un,un,rj,rj,un,rj,rj",
                    "0,1000,1000,1000,1000,1000,1000,0,0,1000,1000,0,1000,1000",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                }, //203.64.139.1
                {"T", "Aruba", "Aruba 3400", "1", "wlanAC", "3", "public", "private", "1.3.6.1.4.1.14823.1.1.15", "24578", "1000", "10", "4", "0",
                	"eth",
                    "rj,rj,rj,rj,un,un,un,un,un,un",
                    "1000,1000,1000,1000,10,10,10,10,10,10",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },//203.64.139.13
                {"T", "Aruba", "Aruba 3200", "1", "wlanAC", "3", "public", "private", "1.3.6.1.4.1.14823.1.1.13", "24578", "1000", "10", "4", "0",
                	"eth",
                    "rj,rj,rj,rj,un,un,un,un,un,un",
                    "1000,1000,1000,1000,10,10,10,10,10,10",
                    "F", "T", "F", "T", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },//203.64.139.15
                {"T", "VIA Networking", "L2SW1GR8", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.7", "1", "1000", "8", "8", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR8F2", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.8", "1", "1000", "10", "8", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR8F4", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.9", "1", "1000", "12", "8", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR16", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.10", "1", "1000", "16", "16", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR16F2", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.11", "1", "1000", "18", "16", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR16F4", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.12", "1", "1000", "20", "16", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR24", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.13", "1", "1000", "24", "24", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR24F2", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.14", "1", "1000", "26", "24", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T","VIA Networking", "L2SW1GR24F4", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.15", "1", "1000", "28", "24", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR48", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.16", "1", "1000", "48", "48", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR48F2", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.17", "1", "1000", "50", "48", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L2SW1GR48F4", "1", "l2switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.18", "1", "1000", "52", "48", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },                    
                {"T", "VIA Networking", "L3SW1GR8", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.19", "1", "1000", "8", "8", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR8F2", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.20", "1", "1000", "10", "8", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR8F4", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.21", "1", "1000", "12", "8", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR16", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.22", "1", "1000", "16", "16", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR16F2", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.23", "1", "1000", "18", "16", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR16F4", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.24", "1", "1000", "20", "16", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR24", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.25", "1", "1000", "24", "24", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR24F2", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.26", "1", "1000", "26", "24", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR24F4", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.27", "1", "1000", "28", "24", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR48", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.28", "1", "1000", "48", "48", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR48F2", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.29", "1", "1000", "50", "48", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "L3SW1GR48F4", "1", "l3switch", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.30", "1", "1000", "52", "48", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "PC", "1", "pc", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.1", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "Server", "1", "server", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.2", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "Internet", "1", "internet", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.3", "1", "1000", "1", "1", "0",
                	"eth",
                	"rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "AP", "1", "wlanAP", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.4", "1", "1000", "1", "1", "0",
                	"eth",
                	"rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "AC", "1", "wlanAC", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.5", "1", "1000", "4", "4", "0",
                	"eth,eth,eth,eth",
                    "rj,rj,rj,rj",
                    "1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "FirewallR4", "1", "firewall", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.31", "1", "1000", "4", "4", "0",
                	"eth,eth,eth,eth",
                    "rj,rj,rj,rj",
                    "1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "FirewallR5", "1", "firewall", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.6", "1", "1000", "5", "5", "0",
                	"eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "FirewallR7", "1", "firewall", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.32", "1", "1000", "7", "7", "0",
                	"eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                //non-management 以下  james 新增 -----------------------------------------------------------------------------------------------------------
                {"T", "unknown", "UNKNOWN", "1", "unknown", "0", "public", "private", "1.3.6.1.4.1.77777.1.1.33", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR8", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.7", "1", "1000", "8", "8", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR8F2", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.8", "1", "1000", "10", "8", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR8F4", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.9", "1", "1000", "12", "8", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR16", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.10", "1", "1000", "16", "16", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR16F2", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.11", "1", "1000", "18", "16", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR16F4", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.12", "1", "1000", "20", "16", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR24", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.13", "1", "1000", "24", "24", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR24F2", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.14", "1", "1000", "26", "24", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T","VIA Networking", "NL2SW1GR24F4", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.15", "1", "1000", "28", "24", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR48", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.16", "1", "1000", "48", "48", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR48F2", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.17", "1", "1000", "50", "48", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL2SW1GR48F4", "1", "l2switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.18", "1", "1000", "52", "48", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },                    
                {"T", "VIA Networking", "NL3SW1GR8", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.19", "1", "1000", "8", "8", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR8F2", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.20", "1", "1000", "10", "8", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR8F4", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.21", "1", "1000", "12", "8", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR16", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.22", "1", "1000", "16", "16", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR16F2", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.23", "1", "1000", "18", "16", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR16F4", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.24", "1", "1000", "20", "16", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR24", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.25", "1", "1000", "24", "24", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR24F2", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.26", "1", "1000", "26", "24", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR24F4", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.27", "1", "1000", "28", "24", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR48", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.28", "1", "1000", "48", "48", "0",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR48F2", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.29", "1", "1000", "50", "48", "2",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NL3SW1GR48F4", "1", "l3switch", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.30", "1", "1000", "52", "48", "4",
                	"eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,rj,fb,fb,fb,fb",
                    "1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NPC", "1", "pc", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.1", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NServer", "1", "server", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.2", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NInternet", "1", "internet", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.3", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NAP", "1", "wlanAP", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.4", "1", "1000", "1", "1", "0",
                	"eth",
                    "rj",
                    "1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NAC", "1", "wlanAC", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.5", "1", "1000", "4", "4", "0",
                	"eth,eth,eth,eth",
                    "rj,rj,rj,rj",
                    "1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NFirewallR4", "1", "firewall", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.31", "1", "1000", "4", "4", "0",
                	"eth,eth,eth,eth",
                    "rj,rj,rj,rj",
                    "1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NFirewallR5", "1", "firewall", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.6", "1", "1000", "5", "5", "0",
                	"eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                },
                {"T", "VIA Networking", "NFirewallR7", "1", "firewall", "-1", "public", "private", "1.3.6.1.4.1.77777.1.2.32", "1", "1000", "7", "7", "0",
                	"eth,eth,eth,eth,eth,eth,eth",
                    "rj,rj,rj,rj,rj,rj,rj",
                    "1000,1000,1000,1000,1000,1000,1000",
                    "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F", "F"
                }

        };
        
        for (String[] data : moduleData) {
            if (getByObjectIdAndmodelRevision(data[iOBJECT_ID], data[iMODEL_REVISION]) == null) {  //Fix Cisco 3750 issue
                JModule module = createModule(data);
                addModule(module);
            }
        }

    }
    
    // ====================================================================================================
    
    private JModule createModule(String[] data) {
        if (data.length != tableDefinition.length) {
            System.out.println("data length is wrong when create module.");
            return null;
        }
        
        JModule module = new JModule();
        module.setDefault(data[iDEFAULT].equals("T"));
        module.setBrandName(data[iBRAND_NAME]);
        module.setModelName(data[iMODEL_NAME]);
        try {
            module.setModelRevision(Integer.parseInt(data[iMODEL_REVISION]));
        }
        catch (NumberFormatException e) {
            System.out.println("parse model revision failed when create module.");
            return null;
        }
        module.setDeviceType(data[iDEVICE_TYPE]);
        try {
            module.setSnmpSupport(Integer.parseInt(data[iSNMP_SUPPORT]));
        }
        catch (NumberFormatException e) {
            System.out.println("parse snmp support failed when create module.");
            return null;
        }
        module.setReadCommunity(data[iREAD_COMMUNITY]);
        module.setWriteCommunity(data[iWRITE_COMMUNITY]);
        module.setObjectId(data[iOBJECT_ID]);
        module.setInfCpuIndex(data[iINF_IDX_CPU]);
        try {
            module.setSnmpTimeout(Integer.parseInt(data[iSNMP_TIMEOUT]));
            module.setInfNum(Integer.parseInt(data[iINF_NUM]));
            module.setRj45Num(Integer.parseInt(data[iRJ45_NUM]));
            module.setFiberNum(Integer.parseInt(data[iFIBER_NUM]));
        }
        catch (NumberFormatException e) {
            System.out.println("parse inf_num or rj45/fiber failed when create module.");
            return null;
        }
        
        String ifTypeString = data[iIFTYPE_LIST];
        String[] infIfType = ifTypeString.split(",");
        List<String> ifTypeList = new ArrayList<String>();
        Collections.addAll(ifTypeList, infIfType);
        
        module.setIfTypeList(ifTypeList);
        
        String jackString = data[iJACK_LIST];
        jackString = jackString.replace("rj", "rj45");
        jackString = jackString.replace("fb", "fiber");
        jackString = jackString.replace("un", "unknown");
        String[] infJack = jackString.split(",");
        List<String> jackList = new ArrayList<String>();
        Collections.addAll(jackList, infJack);
        /*if (jackList.size() != module.getInfNum()) {            // make sure the infNum is assigned the right value
            System.out.println("jack num != inf_num when create module.");
            return null;
        }*/
        module.setJackList(jackList);
        
        String speedString = data[iSPEED_LIST];
        String[] infSpeed = speedString.split(",");
        List<String> speedList = new ArrayList<String>();
        Collections.addAll(speedList, infSpeed);
        /*if (speedList.size() != module.getInfNum()) {           // make sure the infNum is assigned the right value
            System.out.println("speed num != inf_num when create module.");
            return null;
        }*/
        module.setSpeedList(speedList);
        
        module.setSupHostResource(data[iSUP_HOSTRESOURCE].equals("T"));
        module.setSupLinkState(data[iSUP_LINKSTATE].equals("T"));
        module.setSupNegoState(data[iSUP_NEGOSTATE].equals("T"));
        module.setSupRxTxOctet(data[iSUP_RXTXOCTET].equals("T"));
        module.setSupPacketType(data[iSUP_PACKETTYPE].equals("T"));
        module.setSupRmon(data[iSUP_RMON].equals("T"));
        module.setSupPvid(data[iSUP_PVID].equals("T"));
        module.setSupVlan(data[iSUP_VLAN].equals("T"));
        module.setSupGvrp(data[iSUP_GVRP].equals("T"));
        module.setSupPoe(data[iSUP_POE].equals("T"));
        module.setSupTrap(data[iSUP_TRAP].equals("T"));
        module.setSupLldp(data[iSUP_LLDP].equals("T"));
        module.setSupRStp(data[iSUP_RSTP].equals("T"));
        module.setSupMStp(data[iSUP_MSTP].equals("T"));
        module.setSupEdgeCoreResource(data[iSUP_EDGECORERESOURCE].equals("T"));
        module.setSupOctet64(data[iSUP_OCTET64].equals("T"));
        module.setSupEgcoTrap(data[iSUP_EGCO_TRAP].equals("T"));
        
        return module;
    }
    
}
