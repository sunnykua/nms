package com.via.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.via.model.JDevice;

public interface RemoteInterface extends Remote {
	public ArrayList<String[]> getFriendlyAPList(final String ip) throws RemoteException;

	public ArrayList<String[]> getRogueAPList(final String ip) throws RemoteException;

	public ArrayList<String[]> getOnlineAPList(final String ip) throws RemoteException;

	public ArrayList<String[]> getOfflineAPList(final String ip) throws RemoteException;

	public ArrayList<String[]> getClientList(final String ip) throws RemoteException;

	public ArrayList<String[]> getClientDeviceList(final String ip) throws RemoteException;

	public ArrayList<String> getACLogIndex(final String ip) throws RemoteException;

	public String getACLog(final String ip, final String startTime, final String endTime) throws RemoteException;

	public ArrayList<String> getAPSTALogIndex(final String ip) throws RemoteException;

	public String getAPSTALog(final String ip, final String startTime, final String endTime) throws RemoteException;

	public List<JDevice> getRemoteDevice() throws RemoteException;

	public ArrayList<String[]> getTopology() throws RemoteException;

	public boolean isAccountExisted(final String username) throws RemoteException;

	public boolean addAccount(final String username, final String password, final String level, final String name, final String email, final String phone_number, final String is_manage, final String is_remote) throws RemoteException;

	public String[] getAccountInfo(final String username) throws RemoteException;

	public boolean updateRemotePassword(final String username, final String password) throws RemoteException;

	public List<List<String[]>> getAliveStatus(String[] SelectColumns, String LAYER, String startTime, String endTime, String ADDR) throws RemoteException;

	public boolean insertNmsLog(String SRC_ADDR, Timestamp timestamp, String LEVEL, String MESSAGE) throws RemoteException;

	public String[] getRemoteDeviceSystemInfo() throws RemoteException;

	public String RemotehostCpu() throws RemoteException;

	public String RemotehostMemory() throws RemoteException;

	public String RemotehostDisk() throws RemoteException;

	public String getRemoteLocationAddress() throws RemoteException;

	public List<Map<String, String>> getRemoteNMSListLocationAddress() throws RemoteException;
}
