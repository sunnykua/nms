package com.via.model;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.via.database.*;

public class JSTANumberScheduleTask extends TimerTask {
	private JDbSTANumber dbSTANumber;
	private List<JDevice> deviceList;

	private Date scheduledTime;
	//private Date executedTime;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public JSTANumberScheduleTask(List<JDevice> deviceList, JDbSTANumber dbSTANumber) {
		this.deviceList = deviceList;
		this.dbSTANumber = dbSTANumber;
	}

	@Override
	public void run() {
		this.scheduledTime = new Date(this.scheduledExecutionTime()); // the expected time this task should run
		//this.executedTime = new Date();                                    // the actual time this task really run
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(scheduledTime);

		//System.out.println("scheduledTime：" + sdf.format(scheduledTime));

		if (calendar.get(Calendar.MINUTE) % 30 == 0) { // do it every 30th minute
			System.out.println("JSTANumberScheduleTask");
			System.out.println("Process insert STA Number to DB Task at scheduled time: " + sdf.format(scheduledTime));
			//System.out.println(calendar.get(Calendar.MINUTE));
			//calendar.add(Calendar.MINUTE, -30);
			//System.out.println("Before 30 min：" + sdf.format(calendar.getTime()));
			//System.out.println("Start Time：" + sdf.format(calendar.getTime()) + ", End Time：" + sdf.format(scheduledTime));
			InsertSTANumber();
		}
	}

	//====================================================================================================

	private void InsertSTANumber() {
		double prevTime = System.currentTimeMillis();

		for (JDevice device : deviceList) {
			if (device.getDeviceType().equals("wlanAC") && device.getSysObjectId().equals("1.3.6.1.4.1.3742.10.5801.1")) {

				ArrayList<String> GlobalInfo = JACAPInfo.getGlobalInfo(device.getPublicIp());
				if (GlobalInfo.size() == 3) {
					//System.out.println(GlobalInfo.get(0));
					//System.out.println(GlobalInfo.get(1));
					//System.out.println(GlobalInfo.get(2));

					//System.out.println("DBFirstTime："+dbSTANumber.getFirstTime(device.getPublicIp()));
					//System.out.println("DBLastTime："+dbSTANumber.getLastTime(device.getPublicIp()));

					long DBFirstTimeGetTime = 0;

					try {
						DBFirstTimeGetTime = sdf.parse(dbSTANumber.getFirstTime(device.getPublicIp())).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					//System.out.println(DBFirstTimeGetTime);

					long DBLastTimeGetTime = 0;
					try {
						DBLastTimeGetTime = sdf.parse(dbSTANumber.getLastTime(device.getPublicIp())).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					//System.out.println(DBLastTime);

					long RecTimeGetTime = new Date().getTime();
					Timestamp RecTime = new java.sql.Timestamp(RecTimeGetTime);

					if (DBLastTimeGetTime != 0) {
						long DiffTime = (RecTimeGetTime - DBLastTimeGetTime);

						//和最後一筆間隔超過45分
						if (DiffTime > 2700000) {
							System.out.println("DiffTime > 45 min");

							//第一次0，資料庫最後一筆時間+1800000
							long ZeroDataTime1 = DBLastTimeGetTime + 1800000;
							Timestamp Time1 = new java.sql.Timestamp(ZeroDataTime1);
							String[] Insert_ZeroDataTime1 = { "\'" + sdf.format(scheduledTime).toString() + "\'", "\'" + Time1.toString() + "\'", "\'" + device.getPublicIp() + "\'", "\'" + "0" + "\'", "\'" + "0" + "\'", "\'" + "0" + "\'" };
							dbSTANumber.insert(Insert_ZeroDataTime1);

							//第二次0，這次紀錄的時間-1800000
							long ZeroDataTime2 = RecTimeGetTime - 1800000;
							Timestamp Time2 = new java.sql.Timestamp(ZeroDataTime2);
							String[] Insert_ZeroDataTime2 = { "\'" + sdf.format(scheduledTime).toString() + "\'", "\'" + Time2.toString() + "\'", "\'" + device.getPublicIp() + "\'", "\'" + "0" + "\'", "\'" + "0" + "\'", "\'" + "0" + "\'" };
							dbSTANumber.insert(Insert_ZeroDataTime2);
						}
					}

					//和最後一筆至少間隔超過15分才存
					//if (DiffTime > 900000) {
					String[] InsertData = { "\'" + sdf.format(scheduledTime).toString() + "\'", "\'" + RecTime.toString() + "\'", "\'" + device.getPublicIp() + "\'", "\'" + GlobalInfo.get(0) + "\'", "\'" + GlobalInfo.get(1) + "\'", "\'" + GlobalInfo.get(2) + "\'" };
					dbSTANumber.insert(InsertData);

					System.out.println("Insert " + device.getPublicIp() + " STA Number to DB success.");
					//} else if (DiffTime <= 900000) {//和最後一筆間隔不到15分
					//System.out.println("DiffTime <= 15 min");
					//}
				} else {
					System.out.println("Insert " + device.getPublicIp() + " STA Number to DB fail, because can't get data.");
				}
			}
		}

		System.out.println("Insert STA Number to DB task scheduled at " + sdf.format(scheduledTime) + " costs: " + (System.currentTimeMillis() - prevTime) / 1000 + " sec.");
	}
}
