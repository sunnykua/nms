package com.via.model;

import java.util.ArrayList;
import java.util.List;

public class JDeviceReport {
	private String publicIp;
	private int snmpSupport;
	private String sysObjectId;
	private String brandName;
	private String phyAddr;
	private int stackNum;
	private List<JInterfaceReport> interfacesReport; 
	private String aliasName;
	private String deviceType;
	
	public class JInterfaceReport {
		private int portId;
		private int ifIndex;
		private String ifPhysAddress;
		private int stackId;
		private boolean isPoePort;
		private String rxOct;
		private String txOct;
		private String rootStatus;
		private String portRemoteDev;
		private String portRemoteIp;
		private String rxRanking;
		private String txRanking;
		private String aliasName;
		
		public int getPortId() {
			return portId;
		}
		public void setPortId(int portId) {
			this.portId = portId;
		}
		public int getIfIndex() {
			return ifIndex;
		}
		public void setIfIndex(int ifIndex) {
			this.ifIndex = ifIndex;
		}
		public String getIfPhysAddress() {
			return ifPhysAddress;
		}
		public void setIfPhysAddress(String ifPhysAddress) {
			this.ifPhysAddress = ifPhysAddress;
		}
		public int getStackId() {
			return stackId;
		}
		public void setStackId(int stackId) {
			this.stackId = stackId;
		}
		public boolean isPoePort() {
			return isPoePort;
		}
		public void setPoePort(boolean isPoePort) {
			this.isPoePort = isPoePort;
		}
		public String getRxOct() {
			return rxOct;
		}
		public void setRxOct(String rxOct) {
			this.rxOct = rxOct;
		}
		public String getTxOct() {
			return txOct;
		}
		public void setTxOct(String txOct) {
			this.txOct = txOct;
		}
		public String getRootStatus() {
			return rootStatus;
		}
		public void setRootStatus(String rootStatus) {
			this.rootStatus = rootStatus;
		}
		public String getPortRemoteDev() {
			return portRemoteDev;
		}
		public void setPortRemoteDev(String portRemoteDev) {
			this.portRemoteDev = portRemoteDev;
		}
		public String getPortRemoteIp() {
			return portRemoteIp;
		}
		public void setPortRemoteIp(String portRemoteIp) {
			this.portRemoteIp = portRemoteIp;
		}
		public String getRxRanking() {
			return rxRanking;
		}
		public void setRxRanking(String rxRanking) {
			this.rxRanking = rxRanking;
		}
		public String getTxRanking() {
			return txRanking;
		}
		public void setTxRanking(String txRanking) {
			this.txRanking = txRanking;
		}
		public String getAliasName() {
			return aliasName;
		}
		public void setAliasName(String aliasName) {
			this.aliasName = aliasName;
		}
	}
	
	public JDeviceReport() {
		this.interfacesReport = new ArrayList<JInterfaceReport>();
	}
	
	public void addInterfacesReport(
			int portId, 
			int ifIndex, 
			String ifPhysAddress, 
			int stackId, 
			boolean isPoePort, 
			String rxOct, 
			String txOct, 
			String rootStatus, 
			String portRemoteDev, 
			String portRemoteIp,
			String aliasName) {
		JInterfaceReport item = new JInterfaceReport();
		item.setPortId(portId);
		item.setIfIndex(ifIndex);
		item.setIfPhysAddress(ifPhysAddress);
		item.setStackId(stackId);
		item.setPoePort(isPoePort);
		item.setRxOct(rxOct);
		item.setTxOct(txOct);
		item.setRootStatus(rootStatus);
		item.setPortRemoteDev(portRemoteDev);
		item.setPortRemoteIp(portRemoteIp);
		item.setAliasName(aliasName);
		this.interfacesReport.add(item);
	}
	
	public void sortPacket(ArrayList<Double> rxSort, ArrayList<Double> txSort) {
		if(rxSort != null || txSort != null){
			
			int rxSortSize = rxSort.size() > 5 ? rxSortSize = 5 : rxSort.size();
			int txSortSize = txSort.size() > 5 ? txSortSize = 5 : txSort.size();
			
			for(int i=0; i<rxSortSize; i++){
				for(int j=0; j<interfacesReport.size(); j++){
					if(!interfacesReport.get(j).rxOct.equals("0") && Double.parseDouble(interfacesReport.get(j).rxOct) == rxSort.get(i)){
						interfacesReport.get(j).setRxRanking(Integer.toString(i+1));
					}
				}
			}
			
			for(int i=0; i<txSortSize; i++){
				for(int j=0; j<interfacesReport.size(); j++){
					if(!interfacesReport.get(j).txOct.equals("0") && Double.parseDouble(interfacesReport.get(j).txOct) == txSort.get(i)){
						interfacesReport.get(j).setTxRanking(Integer.toString(i+1));
					}
				}
			}
		}
	}


	public String getPublicIp() {
		return publicIp;
	}

	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}

	public int getSnmpSupport() {
		return snmpSupport;
	}

	public void setSnmpSupport(int snmpSupport) {
		this.snmpSupport = snmpSupport;
	}

	public String getSysObjectId() {
		return sysObjectId;
	}

	public void setSysObjectId(String sysObjectId) {
		this.sysObjectId = sysObjectId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPhyAddr() {
		return phyAddr;
	}

	public void setPhyAddr(String phyAddr) {
		this.phyAddr = phyAddr;
	}

	public int getStackNum() {
		return stackNum;
	}

	public void setStackNum(int stackNum) {
		this.stackNum = stackNum;
	}

	public List<JInterfaceReport> getInterfacesReport() {
		return interfacesReport;
	}

	public void setInterfacesReport(List<JInterfaceReport> interfacesReport) {
		this.interfacesReport = interfacesReport;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
