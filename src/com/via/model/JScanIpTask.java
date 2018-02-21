package com.via.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.via.database.*;

public class JScanIpTask extends Thread {

    private List<List<String>> ipScanList;
    private int scanIndex;
    private JDbDevice dbDevice;
    private JDbModule dbModule;
    private List<JDevice> deviceList;
    private int limitedDeviceNumber;
    private boolean isRunning = false;
    private boolean isReachLimit;
    private Logger logger = Logger.getLogger(this.getClass());
    
    public JScanIpTask(JDbDevice dbDevice, JDbModule dbModule, List<JDevice> deviceList, int limitedDeviceNumber) {
    	this.ipScanList = new ArrayList<List<String>>();
        this.scanIndex = 0;
        
        this.dbDevice = dbDevice;
        this.dbModule = dbModule;
        this.deviceList = deviceList;
        this.limitedDeviceNumber = limitedDeviceNumber;
        this.isReachLimit = false;
    }

	public final void setIpScanList(List<List<String>> columns) {
    	this.ipScanList = columns;
    }

	public final boolean isRunning() {
        return isRunning;
    }

    public final boolean isReachLimitation() {
        return isReachLimit;
    }
    
    public void resetReachLimitation() {
        this.isReachLimit = false;
    }
    
    public final void stopThread() {
        this.isRunning = false;
        interrupt();
    }

    @Override
    public void run() {
        if (ipScanList.size() <= 0) return;
        logger.debug(String.format("Scan IP list: %s to %s.", ipScanList.get(0), ipScanList.get(ipScanList.size() - 1)));
        this.isRunning = true;
        
        try {
            for (List<String> DeviceParameter : ipScanList) {
                if (deviceList.size() >= limitedDeviceNumber) {
                    this.isReachLimit = true;
                    System.out.println("Device number has reached license limitation during IP scanning process.");
                    break;
                }
                
                this.scanIndex = ipScanList.indexOf(DeviceParameter);
                processAddr(DeviceParameter);
            }
        }
        catch (InterruptedException e) {
        }
        catch (IOException e) {
            logger.debug("Read from snmp failed while scanning. exit();");
        }
        
        logger.debug("End the scan process.");
        this.ipScanList.clear();
        this.isRunning = false;
    }
    
    public float getPercentage() {
        int index = scanIndex;
        if (index < 0) return 0;
        else {
            return (float)index / (float)ipScanList.size() * 100;
        }
    }
    
    // ====================================================================================================
    
    private void processAddr(List<String> DeviceParameter) throws InterruptedException, IOException {
		String port = DeviceParameter.get(2);
		if (port.equals("")) {
			port = "161";
		}

		String community = DeviceParameter.get(4);
		if (community.equals("")) {
			community = "public";
		}

		int version;
		if (DeviceParameter.get(3).equals("")) {
			version = 2;
		} else {
			try {
				version = Integer.parseInt(DeviceParameter.get(3));
			} catch (NumberFormatException e) {
				version = 2;
			}
		}

		int timeout;
		if (DeviceParameter.get(5).equals("")) {
			timeout = 2000;
		} else {
			try {
				timeout = Integer.parseInt(DeviceParameter.get(5));
			} catch (NumberFormatException e) {
				timeout = 2000;
			}
		}

		JSnmp snmp = new JSnmp(DeviceParameter.get(0) + "/" + port, community, version, timeout, 0);
		snmp.start();

		int modelRevision = 1;
		String objectId = snmp.getNode(JOid.sysObjectID);
		String sysDescr = snmp.getNode(JOid.sysDescr);

		if (objectId == null || objectId.isEmpty()) { // Fix Cisco 3750 issue
			System.out.println("ScanIP : input IP can't be used to read objectId.");
			/*int pingResult = JTools.Ping(JTools.parseSnmpIp(addr), 2, 2);
			
			if(pingResult == 1 && type.isEmpty()){
				addDeviceByObjectId(addr, "1.3.6.1.4.1.77777.1.1.33", "public", 1000, 1, 0);
			}else if(pingResult == 1 && !type.isEmpty()){
				addDeviceByObjectId(addr, type, "public", 1000, 1, 0);
			}else {
				System.out.println("AddManagedDevice : input IP can't be used to ping.");*/
			return;
			//}
		} else if (objectId.equals("1.3.6.1.4.1.9.1.516")) { //cisco 3750 issue
			if (sysDescr.indexOf("C3750E-UNIVERSALK9-M") != -1) {
				modelRevision = 1;
				System.out.println("ScanIP : modelRevision = " + modelRevision);
			} else if (sysDescr.indexOf("C3750-IPBASEK9-M") != -1) {
				modelRevision = 2;
				System.out.println("ScanIP : modelRevision = " + modelRevision);
			} else if (sysDescr.indexOf("C3750E-IPBASEK9-M") != -1) {
				modelRevision = 4;
				System.out.println("ScanIP : modelRevision = " + modelRevision);
			} else {
				modelRevision = 3;
				System.out.println("ScanIP : modelRevision = " + modelRevision);
			}
		} else {
			modelRevision = 1;
			System.out.println("ScanIP : modelRevision = " + modelRevision);
		}

		addDeviceByObjectId(DeviceParameter.get(0) + "/" + port, objectId, snmp.getCommunity(), (int) snmp.getTimeout(), modelRevision, version, DeviceParameter.get(1));
		/*JModule module = dbModule.getByObjectIdAndmodelRevision(objectId, String.valueOf(modelRevision));  // Fix Cisco 3750 issue

		if (module == null) return;

		for (JDevice device : deviceList) {
		    if (addr.equals(device.getSnmpAddress())) {
		        logger.debug("Addr: " + addr + " has already existed in device list.");
		        return;
		    }
		}
		
		//logger.debug("Ip: " + addr + " is available.");
		
		JDevice device = new JDevice();
		device.setPublicIp(JTools.parseSnmpIp(addr));
		device.setSnmpPort(JTools.parseSnmpPort(addr));     // has to ensure the addr is snmp address
		device.setSnmpAddress(addr);
		device.setReadCommunity(snmp.getCommunity());
		device.setSnmpTimeout( (int) snmp.getTimeout());
		device.setSnmpVersion(snmpVersion);
		System.out.println("snmpVersion="+snmpVersion);
		
		if (device.initial(module) != true) {
		    logger.debug("Device: " + device.getSnmpAddress() + " initial failed.");
		    return;             // skip this ip if it fails to read snmp
		}
		
		deviceList.add(device);

		// Add to device table in database
		if (dbDevice.addDevice(device)) {
		    logger.debug("Device " + device.getSnmpAddress() + " add to device table success.");
		}
		else {
		    logger.debug("Device " + device.getSnmpAddress() + " add to device table failed.");
		}*/

		snmp.end();
	}
    

    
    private void addDeviceByObjectId(String addr, String objectId, String readCommunity, int snmpTimeout, int modelRevision, int snmpVersion, String AliasName) throws InterruptedException, IOException {
    	JModule module = dbModule.getByObjectIdAndmodelRevision(objectId, String.valueOf(modelRevision));  // Fix Cisco 3750 issue

        if (module == null) return;

		for (JDevice device : deviceList) {
			if (addr.equals(device.getSnmpAddress())) {
				logger.debug("Addr: " + addr + " has already existed in device list.");

				if(!AliasName.equals("")){
					device.setAliasName(AliasName);
					if (dbDevice.updateAliasName(device)) {
						System.out.println("Change " + device.getPublicIp() +"'s alias name to " + "\""+ AliasName + "\".");
					}
				}

				return;
			}
        }
        
        //logger.debug("Ip: " + addr + " is available.");
        
        JDevice device = new JDevice();
        device.setPublicIp(JTools.parseSnmpIp(addr));
        device.setSnmpPort(JTools.parseSnmpPort(addr));     // has to ensure the addr is snmp address
        device.setSnmpAddress(addr);
        device.setReadCommunity(readCommunity);
        device.setSnmpTimeout(snmpTimeout);
        device.setSnmpVersion(snmpVersion);
        System.out.println("snmpVersion 00="+snmpVersion);
        
        if (device.initial(module) != 1) {
            logger.debug("Device: " + device.getSnmpAddress() + " initial failed.");
            return;             // skip this ip if it fails to read snmp
        }
        
        deviceList.add(device);

        // Add to device table in database
        if (dbDevice.addDevice(device)) {
            logger.debug("Device " + device.getSnmpAddress() + " add to device table success.");
            
            if(!AliasName.equals("")){
				device.setAliasName(AliasName);
				if (dbDevice.updateAliasName(device)) {
					System.out.println("Change " + device.getPublicIp() +"'s alias name to " + "\""+ AliasName + "\".");
				}
            }
        }
        else {
            logger.debug("Device " + device.getSnmpAddress() + " add to device table failed.");
        }
    }
}
