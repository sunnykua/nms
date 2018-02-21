package com.via.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.via.model.JDevice;
import com.via.model.JNetwork;

public class RemoteDevice implements Serializable {

	public static List<JDevice> getRemoteDevice() {
		return JNetwork.getRemoteDevice();
	}

	public static ArrayList<String[]> getTopology() {
		return JNetwork.getTopology();
	}

	public static boolean isAccountExisted(final String username) {
		return JNetwork.isAccountExisted(username);
	}

	public static boolean addAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) {
		return JNetwork.addAccount(username, password, level, name, email, phone_number, is_manage, is_remote);
	}

	public static String[] getAccountInfo(final String username) throws RemoteException {
		return JNetwork.getAccountInfo(username);
	}

	public static boolean updateRemotePassword(final String username, final String password) {
		return JNetwork.updateRemotePassword(username, password);
	}

	public static List<List<String[]>> getAliveStatus(final String[] SelectColumns, final String LAYER, final String startTime, final String endTime, final String ADDR) {
		return JNetwork.getAliveStatus(SelectColumns, LAYER, startTime, endTime, ADDR);
	}

	public static boolean insertNmsLog(final String SRC_ADDR, final Timestamp DATED, final String LEVEL, final String MESSAGE) {
		return JNetwork.insertNmsLog(SRC_ADDR, DATED, LEVEL, MESSAGE);
	}

	public static String[] getRemoteDeviceSystemInfo() {
		return JNetwork.getRemoteDeviceSystemInfo();
	}
	
	public static String RemotehostCpu() {
		return JNetwork.RemotehostCpu();
	}
	
	public static String RemotehostMemory() {
		return JNetwork.RemotehostMemory();
	}
	
	public static String RemotehostDisk() {
		return JNetwork.RemotehostDisk();
	}
	
	public static String getRemoteLocationAddress() {
		return JNetwork.getLocationAddress();
	}
	
	public static List<Map<String, String>> getRemoteNMSListLocationAddress() {
		return JNetwork.getRemoteNMSListLocationAddress();
	}
}