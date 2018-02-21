package com.via.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.via.database.JDbAliveStatus;
import com.via.database.JDbNms;
import com.via.rmi.RemoteInterface;
import com.via.rmi.RemoteService;

public class JNmsScheduleTask extends TimerTask {
	
	
	private List<JNms> nmsList;
	private JDbNms dbNms;
	private JDbAliveStatus dbAliveStatus;
	private Date scheduledTime;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public JNmsScheduleTask(JDbNms dbNms, List<JNms> nmsList, JDbAliveStatus dbAliveStatus) {
		this.nmsList = nmsList;
		this.dbNms = dbNms;
		this.dbAliveStatus = dbAliveStatus;
	}
	
	public void run() {
	    this.scheduledTime = new Date(this.scheduledExecutionTime());      // the expected time this task should run
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduledTime);
		
		if (calendar.get(Calendar.MINUTE) % 10 == 0) {		              // do it every 30th minute
			procDeviceTask();
		}
	}
	
	public void procDeviceTask() {
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
				logger.warn("Device IP = " + device.getPublicIp() + " disconnects.");
			}
			
			String[] AliveStatus = { "\'" + new java.sql.Timestamp(System.currentTimeMillis()) + "\'", "\'" + "2" + "\'", "\'" + "NMS" + "\'", "\'" + device.getPublicIp() + "\'", "\'" + device.isAlive() + "\'" };
			dbAliveStatus.insert(AliveStatus);
		}
	}
}
