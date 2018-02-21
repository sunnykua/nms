package com.via.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.via.model.JDevice;
import com.via.model.JInterface;
import com.via.model.JNms;

public class JDbNms {
    private JDatabase dbInst;
    private String tableName;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String[] tableDefinition = {
            "PUBLIC_IP          VARCHAR(20) UNIQUE NOT NULL, ",
            "ALIAS_NAME         VARCHAR(255), ",
            "TOTAL_DEV_NUM      VARCHAR(10), ",
            "ONLINE_DEV_NUM     VARCHAR(10), ",
            "OFFLINE_DEV_NUM    VARCHAR(10), ",
            "LAST_SEEN          TIMESTAMP, ",
            "IS_ALIVE           VARCHAR(5) NOT NULL, ",
            "IS_ENABLE          VARCHAR(5) NOT NULL, ",
            "RMI_TIMEOUT        VARCHAR(10), ",
            "RMI_REGISTER_PORT  VARCHAR(10), ",
            "RMI_DATA_PORT      VARCHAR(10), ",
            "REMOTE_ACCOUNNT    VARCHAR(80), ",
            "REMOTE_PWD         VARCHAR(100), ",
            "REMOTE_MEMBER      VARCHAR(1000) ",
            
    };
    private int a = 0;
    private final int iPUBLIC_IP = a++, iALIAS_NAME = a++, iTOTAL_DEV_NUM = a++, iONLINE_DEV_NUM = a++, iOFFLINE_DEV_NUM = a++, iLAST_SEEN = a++, iIS_ALIVE = a++,
            iIS_ENABLE = a++, iRMI_TIMEOUT = a++, iRMI_REGISTER_PORT = a++, iRMI_DATA_PORT = a++, iREMOTE_ACCOUNNT = a++, iREMOTE_PWD = a++, iREMOTE_MEMBER = a++;
    
    public JDbNms(final JDatabase database, final String tableName) {
        this.dbInst = database;
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

    public boolean addNmsDevice(final JNms device) {
        if (device == null) return false;

        String publicIp = String.format("'%s'", device.getPublicIp());
        String aliasName = String.format("'%s'", device.getAliasName());
        String totalDeviceNum = String.format("'%s'", device.getTotalDeviceNum());
        String onlineDeviceNum = String.format("'%s'", device.getOnlineDeviceNum());
        String offlineDeviceNum = String.format("'%s'", device.getOfflineDeviceNum());
        String lastSeen = device.getLastSeen() != null ? String.format("'%s'", sdf.format(device.getLastSeen())) : "default"; // 'default' will let the timestamp be null
        String isAlive = String.format("'%s'", device.isAlive() ? "true" : "false");
        
        String isEnable = String.format("'%s'", device.isEnable() ? "true" : "false");
        String rmiTimeout = String.format("'%s'", device.getRmiTimeout());
        String rmiRegisterPort = String.format("'%s'", device.getRmiRegisterPort());
        String rmiDataPort = String.format("'%s'", device.getRmiRegisterPort());
        String remoteAccount = String.format("'%s'", device.getRemoteAccount());
        String remotePwd = String.format("'%s'", device.getRemotePwd());
        String remoteMember = String.format("'%s'", device.getRemoteMember());
        

        String[] values = { publicIp, aliasName, totalDeviceNum, onlineDeviceNum, offlineDeviceNum, lastSeen, isAlive, 
        		isEnable, rmiTimeout, rmiRegisterPort, rmiDataPort, remoteAccount, remotePwd, remoteMember
        };

        return dbInst.insert(tableName, values);
    }
    
    public boolean removeDevice(final JNms device) {
        if (device == null) return false;
        String filterColumnName = String.format("%s", "PUBLIC_IP"); // note the column name should not use double quotes
        String filterColumnValue = String.format("'%s'", device.getPublicIp());

        return dbInst.deleteRow(tableName, filterColumnName, filterColumnValue);
    }

    public List<JNms> getAllDevice() {
        List<JNms> deviceList = new ArrayList<JNms>();

        String[][] deviceArray = dbInst.getAll(tableName);
        
        if (deviceArray == null) return deviceList;

        for (String[] data : deviceArray) {
            if (data.length != tableDefinition.length) continue;

            String publicIp = data[iPUBLIC_IP];
            
            JNms device = new JNms();
            device.setPublicIp(data[iPUBLIC_IP]);
            device.setAliasName(data[iALIAS_NAME]);
            device.setTotalDeviceNum(Integer.parseInt(data[iTOTAL_DEV_NUM]));
            device.setOnlineDeviceNum(Integer.parseInt(data[iONLINE_DEV_NUM]));
            device.setOfflineDeviceNum(Integer.parseInt(data[iOFFLINE_DEV_NUM]));

            try {
                device.setLastSeen(sdf.parse(data[iLAST_SEEN]));
            } catch (ParseException e) {
                System.out.println("Read device " + publicIp + "from db exception when parsing lastSeen. Fill null and continue.");
                device.setLastSeen(null);
            }

            device.setAlive(data[iIS_ALIVE].equals("true"));
            device.setEnable(data[iIS_ENABLE].equals("true"));
            device.setRmiTimeout(data[iRMI_TIMEOUT]);
            device.setRmiRegisterPort(data[iRMI_REGISTER_PORT]);
            device.setRmiDataPort(data[iRMI_DATA_PORT]);
            device.setRemoteAccount(data[iREMOTE_ACCOUNNT]);
            device.setRemotePwd(data[iREMOTE_PWD]);
            device.setRemoteMember(data[iREMOTE_MEMBER]);

            deviceList.add(device);
        }

        return deviceList;
    }

    public boolean updateTotalDevNum(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "TOTAL_DEV_NUM", Integer.toString(device.getTotalDeviceNum()), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateOnlineDevNum(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "ONLINE_DEV_NUM", Integer.toString(device.getOnlineDeviceNum()), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateOfflineDevNum(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "OFFLINE_DEV_NUM", Integer.toString(device.getOfflineDeviceNum()), "PUBLIC_IP", device.getPublicIp());
    }

    public boolean updateLastSeen(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "LAST_SEEN", sdf.format(device.getLastSeen()), "PUBLIC_IP", device.getPublicIp());
    }

    public boolean updateAlive(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "IS_ALIVE", (device.isAlive() ? "true" : "false"), "PUBLIC_IP", device.getPublicIp());
    }
    
    public boolean updateRemoteAccountSetItems(final JNms device) {
        if (device == null) return false;

        return dbInst.update(tableName, "REMOTE_MEMBER", device.getRemoteMember(), "PUBLIC_IP", device.getPublicIp());
    }
    
}
