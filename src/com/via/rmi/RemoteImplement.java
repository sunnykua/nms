package com.via.rmi;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.via.model.JDevice;

public class RemoteImplement implements RemoteInterface {
	public RemoteImplement() throws RemoteException {
		super();
	}

	@Override
	public ArrayList<String[]> getFriendlyAPList(String ip) {
		return RemoteACAPInfo.getFriendlyAPList(ip);
	}

	@Override
	public ArrayList<String[]> getRogueAPList(String ip) {
		return RemoteACAPInfo.getRogueAPList(ip);
	}

	@Override
	public ArrayList<String[]> getOnlineAPList(final String ip) throws RemoteException {
		return RemoteACAPInfo.getOnlineAPList(ip);
	}

	@Override
	public ArrayList<String[]> getOfflineAPList(final String ip) throws RemoteException {
		return RemoteACAPInfo.getOfflineAPList(ip);
	}

	@Override
	public ArrayList<String[]> getClientList(final String ip) throws RemoteException {
		return RemoteACAPInfo.getClientList(ip);
	}

	@Override
	public ArrayList<String> getACLogIndex(final String ip) throws RemoteException {
		return RemoteACAPInfo.getACLogIndex(ip);
	}

	@Override
	public String getACLog(final String ip, final String startTime, final String endTime) throws RemoteException {
		return RemoteACAPInfo.getACLog(ip, startTime, endTime);
	}

	@Override
	public ArrayList<String> getAPSTALogIndex(final String ip) throws RemoteException {
		return RemoteACAPInfo.getAPSTALogIndex(ip);
	}

	@Override
	public String getAPSTALog(final String ip, final String startTime, final String endTime) throws RemoteException {
		return RemoteACAPInfo.getAPSTALog(ip, startTime, endTime);
	}

	@Override
	public ArrayList<String[]> getClientDeviceList(final String ip) throws RemoteException {
		return RemoteACAPInfo.getClientDeviceList(ip);
	}

	@Override
	public List<JDevice> getRemoteDevice() throws RemoteException {
		return RemoteDevice.getRemoteDevice();
	}

	@Override
	public ArrayList<String[]> getTopology() throws RemoteException {
		return RemoteDevice.getTopology();
	}

	@Override
	public boolean isAccountExisted(final String username) throws RemoteException {
		return RemoteDevice.isAccountExisted(username);
	}

	@Override
	public boolean addAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) throws RemoteException {
		return RemoteDevice.addAccount(username, password, level, name, email, phone_number, is_manage, is_remote);
	}

	@Override
	public String[] getAccountInfo(final String username) throws RemoteException {
		return RemoteDevice.getAccountInfo(username);
	}

	@Override
	public boolean updateRemotePassword(final String username, final String password) throws RemoteException {
		return RemoteDevice.updateRemotePassword(username, password);
	}

	@Override
	public List<List<String[]>> getAliveStatus(final String[] SelectColumns, final String LAYER, final String startTime, final String endTime, final String ADDR) throws RemoteException {
		return RemoteDevice.getAliveStatus(SelectColumns, LAYER, startTime, endTime, ADDR);
	}

	@Override
	public boolean insertNmsLog(final String SRC_ADDR, final Timestamp DATED, final String LEVEL, final String MESSAGE) throws RemoteException {
		return RemoteDevice.insertNmsLog(SRC_ADDR, DATED, LEVEL, MESSAGE);
	}

	@Override
	public String[] getRemoteDeviceSystemInfo() throws RemoteException {
		return RemoteDevice.getRemoteDeviceSystemInfo();
	}

	@Override
	public String RemotehostCpu() throws RemoteException {
		return RemoteDevice.RemotehostCpu();
	}

	@Override
	public String RemotehostMemory() throws RemoteException {
		return RemoteDevice.RemotehostMemory();
	}

	@Override
	public String RemotehostDisk() throws RemoteException {
		return RemoteDevice.RemotehostDisk();
	}

	@Override
	public String getRemoteLocationAddress() throws RemoteException {
		return RemoteDevice.getRemoteLocationAddress();
	}

	@Override
	public List<Map<String, String>> getRemoteNMSListLocationAddress() throws RemoteException {
		return RemoteDevice.getRemoteNMSListLocationAddress();
	}
}
