package com.via.model;

import java.util.Calendar;

public class JRemoteAuth {
	private static String remCode; // rem is remote
	private static long remTime;
	private static String remAccount;
	private static String remPwd;
	
	
	public boolean setRemoteInfo(String NewRemoteCode, String NewRemoteAccount, String NewRemotePwd) {
		Calendar calendar = Calendar.getInstance();
		if(NewRemoteCode != null){
			remCode = NewRemoteCode;
			remTime = calendar.getTimeInMillis();
			remAccount = NewRemoteAccount;
			remPwd = NewRemotePwd;
			return true;
		}
		return false;
	}
	
	public static String[] getRemoteInfo(long centerTime) {
		long difference = (centerTime - remTime)/1000;
		
			if(difference < 60){
				String[] info = new String[3];
				info[0] = remCode;
				info[1] = remAccount;
				info[2] = remPwd;
				return info;
			}
			else {
				remCode = "";
				remTime = 0;
				remAccount = "";
				remPwd = "";
			}
			
		return null;
	}
	
	public static void removeRemoteInfo() {
		remCode = "";
		remTime = 0;
		remAccount = "";
		remPwd = "";
		System.out.println("Remove remCode "+remCode);
	}
	
}
