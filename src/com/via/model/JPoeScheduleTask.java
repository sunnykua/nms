package com.via.model;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;

public class JPoeScheduleTask extends TimerTask {

	
	private List<JDevice> deviceList;
	private Date scheduledTime;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public JPoeScheduleTask(List<JDevice> deviceList) {
		this.deviceList = deviceList;
	}
	
	@Override
	public void run() {
	    this.scheduledTime = new Date(this.scheduledExecutionTime());      // the expected time this task should run
	    //this.executedTime = new Date();                                    // the actual time this task really run
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduledTime);
		//System.out.println("scheduledTime time: " + calendar.getTime());
		if (calendar.get(Calendar.MINUTE) % 5 == 0) {		              // do it every 5th minute
			poeSchedule();
		}
	}

	//====================================================================================================
	
	public void poeSchedule() {
		Calendar calendar = Calendar.getInstance();
		//System.out.println("poeSchedule time: " + calendar.getTime());
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		String currentTime = String.format("%02d:%02d", hours, minutes);
		//System.out.println("currentTime="+currentTime);
		
		//1.Set switch all port Poe on/off.
		/*if(currentTime.equals("00:00")){
			System.out.println("It's time 12:00!!!!!!!!!!!!!!!!!!!!!");
		}*/
		/*for (JDevice device : deviceList) {
			if(device.isSupportPoe() && device.isPoeManual() && device.getPoeStartTime().equals(currentTime)){
				String [] dataArray = new String[device.getRj45Num()];
				
				for(int i=0; i<dataArray.length;i++){
					if(device.getPoeStartStatus().equals("1")){
					    dataArray[i]="1";
					}else if (device.getPoeStartStatus().equals("0")){
						dataArray[i]="2";
					}
				}
				JPoeStatus poeStatus = new JPoeStatus();
				poeStatus.writeDevice(device, JOid.PoEPortAdminStatus, dataArray);
			}
			if(device.isSupportPoe() && device.isPoeManual() && device.getPoeEndTime().equals(currentTime)){
				String [] dataArray = new String[device.getRj45Num()];
				
				for(int i=0; i<dataArray.length;i++){
					if(device.getPoeEndStatus().equals("1")){
					    dataArray[i]="1";
					}else if (device.getPoeEndStatus().equals("0")){
						dataArray[i]="2";
					}
				}
				JPoeStatus poeStatus = new JPoeStatus();
				poeStatus.writeDevice(device, JOid.PoEPortAdminStatus, dataArray);
			}
		}*/
		
		//2.Use different profile to set switch port poe schedule.

		readPoeScheduleSnmp();
		
		for (JDevice device : deviceList) {
			if(device.isSupportPoe() && device.isPoePower()){
				String [] dataInf = new String[device.getInfNum()];
				for (JInterface inf : device.getInterfaces()) {
					dataInf[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
				}
				
				String [] initialData = new String[device.getRj45Num()];
				String [] dataArray = new String[device.getRj45Num()];
				for(int i=0; i<dataArray.length;i++){
					    dataArray[i]=dataInf[i];
					    initialData[i]=dataInf[i];
				}
				
				if(dataArray[0] == null){
					logger.info(device.getPublicIp() + " read snmp fail.");
					continue;
				} else {
					//System.out.println("Device ip = " + device.getPublicIp() + ". Initial data array = " + Arrays.toString(dataArray) + " Time = " + currentTime);
					for(JInterface inf : device.getInterfaces()){
						if(inf.isPoePower()){
							String [] spiltStart = inf.getPoeStartTime().split(":");
							int startHour = Integer.parseInt(spiltStart[0]);
							int startMinutes = Integer.parseInt(spiltStart[1]);
							
							String [] spiltEnd = inf.getPoeEndTime().split(":");
							int endHour = Integer.parseInt(spiltEnd[0]);
							int endMinutes = Integer.parseInt(spiltEnd[1]);
							
							Calendar setStart = Calendar.getInstance();//get start time calendar.
						    setStart.setTime(new java.util.Date());//set date object.
						    setStart.set(Calendar.HOUR_OF_DAY, startHour);
						    setStart.set(Calendar.MINUTE, startMinutes);
						    setStart.set(Calendar.MILLISECOND, 0);
						       
						    Calendar setEnd = Calendar.getInstance();
						    setEnd.setTime(new java.util.Date());
						    setEnd.set(Calendar.HOUR_OF_DAY, endHour);
						    setEnd.set(Calendar.MINUTE, endMinutes);
						    setEnd.set(Calendar.MILLISECOND, 0);
							
						    if (calendar.getTime().before(setEnd.getTime()) && calendar.getTime().after(setStart.getTime())){
						    	dataArray[inf.getInfIndex()-1] = "1";
							}else{
								dataArray[inf.getInfIndex()-1] = "2";
							}
						    
						    /*if(inf.isPoePower() && inf.getPoeStartTime().equals(currentTime)){
								dataArray[inf.getInfIndex()-1] = "1";//Start time always equal 1.
							}*/
						}
					}
					int count = 0;
					for(int i=0; i<dataArray.length;i++){
					    if(dataArray[i]!=initialData[i]){
					    	count++;
					    }
				    }
					if(count > 0){
						//Write new data to poe status
						System.out.println("Device = " + device.getPublicIp() + " will to write. Now data array = " + Arrays.toString(dataArray) + " Time = " + currentTime);
						
						JPoeStatus poeStatus = new JPoeStatus();
						poeStatus.writeDevice(device, JOid.PoEPortAdminStatus, dataArray);
						
						readPoeScheduleSnmp();
						
						String [] checkTemp = new String[device.getInfNum()];
						for (JInterface inf : device.getInterfaces()) {
							checkTemp[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
						}
						String [] checkArray = new String[device.getRj45Num()];
						for(int i=0; i<dataArray.length;i++){
							checkArray[i]=checkTemp[i];
					    }
						for(int i=0; i<dataArray.length;i++){
						    if(dataArray[i]!=checkArray[i]){
						    	logger.info("Device:"+device.getPublicIp()+" Write Fail.");
						    }
					    }
					}
				}
			}
		}
		
		//3.Use different profile to set switch port poe schedule. (Current time type is string.)
		
		readPoeScheduleSnmp();
		
		for (JDevice device : deviceList) {
			if(device.isSupportPoe() && device.isPoePower()){
				int count = 0;
				String [] dataInf = new String[device.getInfNum()];
				for (JInterface inf : device.getInterfaces()) {
					dataInf[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
				}
				String [] dataArray = new String[device.getRj45Num()];
				for(int i=0; i<dataArray.length;i++){
					    dataArray[i]=dataInf[i];
				}
				//System.out.println(device.getPublicIp() + " Start Array = " + Arrays.toString(dataArray));
				if(dataArray[0] == null){
					logger.info(device.getPublicIp()+" read snmp fail.");
					continue;
				} else {
					for(JInterface inf : device.getInterfaces()){
						if(inf.isPoePower() && inf.getPoeStartTime().equals(currentTime)){
							count++;
							dataArray[inf.getInfIndex()-1] = "1";//Start time always equal 1.
						}
					}
					if(count>0){//Write new data to poe status
						System.out.println("Device:" + device.getPublicIp() + " execute start time. Snmp array = " + Arrays.toString(dataArray));
						
						JPoeStatus poeStatus = new JPoeStatus();
						poeStatus.writeDevice(device, JOid.PoEPortAdminStatus, dataArray);
						
						readPoeScheduleSnmp();
						
						String [] checkTemp = new String[device.getInfNum()];
						for (JInterface inf : device.getInterfaces()) {
							checkTemp[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
						}
						String [] checkArray = new String[device.getRj45Num()];
						for(int i=0; i<dataArray.length;i++){
							checkArray[i]=checkTemp[i];
					    }
						for(int i=0; i<dataArray.length;i++){
						    if(dataArray[i]!=checkArray[i]){
						    	logger.info("Device:" + device.getPublicIp()+" Write Fail.");
						    }
					    }
					}
				}
			}
		}
		
		readPoeScheduleSnmp();
		
		for (JDevice device : deviceList) {
			if(device.isSupportPoe() && device.isPoePower()){
				int count = 0;
				String [] dataInf = new String[device.getInfNum()];
				for (JInterface inf : device.getInterfaces()) {
					dataInf[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
				}
				String [] dataArray = new String[device.getRj45Num()];
				for(int i=0; i<dataArray.length;i++){
					    dataArray[i]=dataInf[i];
				}
				//System.out.println(device.getPublicIp() + " End snmp Array = " + Arrays.toString(dataArray));
				if(dataArray[0] == null){
					logger.info(device.getPublicIp() + " read snmp fail.");
					continue;
				} else {
					for(JInterface inf : device.getInterfaces()){
						if(inf.isPoePower() && inf.getPoeEndTime().equals(currentTime)){
							count++;
							dataArray[inf.getInfIndex()-1] = "2";//Start time always equal 2.
						}
					}
					if(count>0){//Write new data to poe status
						System.out.println("Device:" + device.getPublicIp( )+ " execute end time. Snmp array = " + Arrays.toString(dataArray));
						
						JPoeStatus poeStatus = new JPoeStatus();
						poeStatus.writeDevice(device, JOid.PoEPortAdminStatus, dataArray);
						
						readPoeScheduleSnmp();
						
						String [] checkTemp = new String[device.getInfNum()];
						for (JInterface inf : device.getInterfaces()) {
							checkTemp[inf.getInfIndex()-1] = inf.getPoeCurrentStatus();
						}
						String [] checkArray = new String[device.getRj45Num()];
						for(int i=0; i<dataArray.length;i++){
							checkArray[i]=checkTemp[i];
					    }
						for(int i=0; i<dataArray.length;i++){
						    if(dataArray[i]!=checkArray[i]){
						    	logger.info("Device:" + device.getPublicIp() + " Write Fail.");
						    }
					    }
					}
				}
			}
		}
	}
	
	public void readPoeScheduleSnmp() {
		for (JDevice device : deviceList) {
			if(device.isSupportPoe() && device.isPoePower()){
			String[] poeStatusOids = {
					JOid.PoEPortAdminStatus
			};
			Map<String, List<String>> rawData2 = null;
			int a = 0;
			final int PortAdminStatus = a++;
			try {
	
	            JSnmp snmp = device.getSnmpVersion()<=2 ? new JSnmp(device.getSnmpAddress(), device.getReadCommunity(), device.getSnmpVersion(), device.getSnmpTimeout(), 1) :
	            	new JSnmp(device.getSnmpAddress(), device.getSnmpVersion(), device.getSnmpTimeout(), 1, device.getSecurityName(), device.getSecurityLevel(), device.getAuthProtocol(), device.getAuthPassword(), device.getPrivProtocol(), device.getPrivPassword());
				snmp.start();
				rawData2 = snmp.getTable(poeStatusOids);
				snmp.end();
		    } catch (IOException e) {
		    	logger.info("Read SNMP Failed.\n" + e.getMessage());
			}
			
			int i = 1;
				for(JInterface inf : device.getInterfaces()){	
				String ifKey = "1." + String.valueOf(i);
				i++;
					if (rawData2.containsKey(ifKey) && !rawData2.isEmpty()) {
		    			inf.setPoeCurrentStatus(rawData2.get(ifKey).get(PortAdminStatus).equals("1") ? "1" : "2");
		    			//System.out.println(device.getPublicIp()+" "+inf.getInfIndex()+" "+inf.getPoeCurrentStatus());
					} else {
						inf.setPoeCurrentStatus(null);
					}
				}
			}
		}
	}
}
