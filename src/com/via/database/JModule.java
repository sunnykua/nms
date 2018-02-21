package com.via.database;

import java.util.List;

public class JModule {
	private boolean isDefault;
    private String brandName;
    private String modelName;
    private int modelRevision;          // it is determined by code when adding to database
    private String deviceType;
    private int snmpSupport;
    private String readCommunity;
    private String writeCommunity;
    private String objectId;
    private String infCpuIndex;
    private int snmpTimeout;
    private int infNum;
    private int rj45Num;
    private int fiberNum;
    private List<String> ifTypeList;
    private List<String> jackList;
    private List<String> speedList;
    private boolean isSupHostResource;
    private boolean isSupLinkState;
    private boolean isSupNegoState;
    private boolean isSupRxTxOctet;
    private boolean isSupPacketType;
    private boolean isSupRmon;
    private boolean isSupPvid;
    private boolean isSupVlan;
    private boolean isSupGvrp;
    private boolean isSupPoe;
    private boolean isSupTrap;
    private boolean isSupLldp;
    private boolean isSupRStp;
    private boolean isSupMStp;
    private boolean isSupEdgeCoreResource;
    private boolean isSupOctet64;
    private boolean isSupEgcoTrap;
    
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public final String getBrandName() {
        return brandName;
    }
    public final void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public final String getModelName() {
        return modelName;
    }
    public final void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public final int getModelRevision() {
        return modelRevision;
    }
    public final void setModelRevision(int modelRevision) {
        this.modelRevision = modelRevision;
    }
    public final String getDeviceType() {
        return deviceType;
    }
    public final void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public final int getSnmpSupport() {
        return snmpSupport;
    }
    public final void setSnmpSupport(int snmpSupport) {
        this.snmpSupport = snmpSupport;
    }
    public final String getReadCommunity() {
        return readCommunity;
    }
    public final void setReadCommunity(String readCommunity) {
        this.readCommunity = readCommunity;
    }
    public final String getWriteCommunity() {
        return writeCommunity;
    }
    public final void setWriteCommunity(String writeCommunity) {
        this.writeCommunity = writeCommunity;
    }
    public final String getObjectId() {
        return objectId;
    }
    public final void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    public final String getInfCpuIndex() {
        return infCpuIndex;
    }
    public final void setInfCpuIndex(String infCpuIndex) {
        this.infCpuIndex = infCpuIndex;
    }
    public int getSnmpTimeout() {
		return snmpTimeout;
	}
	public void setSnmpTimeout(int snmpTimeout) {
		this.snmpTimeout = snmpTimeout;
	}
	public final int getInfNum() {
        return infNum;
    }
    public final void setInfNum(int infNum) {
        this.infNum = infNum;
    }
    public final int getRj45Num() {
        return rj45Num;
    }
    public final void setRj45Num(int rj45Num) {
        this.rj45Num = rj45Num;
    }
    public final int getFiberNum() {
        return fiberNum;
    }
    public final void setFiberNum(int fiberNum) {
        this.fiberNum = fiberNum;
    }
    /**
	 * @return the ifTypeList
	 */
	public List<String> getIfTypeList() {
		return ifTypeList;
	}
	/**
	 * @param ifTypeList the ifTypeList to set
	 */
	public void setIfTypeList(List<String> ifTypeList) {
		this.ifTypeList = ifTypeList;
	}
	public final List<String> getJackList() {
        return jackList;
    }
    public final void setJackList(List<String> jackList) {
        this.jackList = jackList;
    }
    public final List<String> getSpeedList() {
        return speedList;
    }
    public final void setSpeedList(List<String> speedList) {
        this.speedList = speedList;
    }
    public final boolean isSupHostResource() {
        return isSupHostResource;
    }
    public final void setSupHostResource(boolean isSupHostResource) {
        this.isSupHostResource = isSupHostResource;
    }
    public final boolean isSupLinkState() {
        return isSupLinkState;
    }
    public final void setSupLinkState(boolean isSupLinkState) {
        this.isSupLinkState = isSupLinkState;
    }
    public final boolean isSupNegoState() {
        return isSupNegoState;
    }
    public final void setSupNegoState(boolean isSupNegoState) {
        this.isSupNegoState = isSupNegoState;
    }
    public final boolean isSupRxTxOctet() {
        return isSupRxTxOctet;
    }
    public final void setSupRxTxOctet(boolean isSupRxTxOctet) {
        this.isSupRxTxOctet = isSupRxTxOctet;
    }
    public final boolean isSupPacketType() {
        return isSupPacketType;
    }
    public final void setSupPacketType(boolean isSupPacketType) {
        this.isSupPacketType = isSupPacketType;
    }
    public final boolean isSupRmon() {
        return isSupRmon;
    }
    public final void setSupRmon(boolean isSupRmon) {
        this.isSupRmon = isSupRmon;
    }
    public final boolean isSupPvid() {
        return isSupPvid;
    }
    public final void setSupPvid(boolean isSupPvid) {
        this.isSupPvid = isSupPvid;
    }
    public final boolean isSupVlan() {
        return isSupVlan;
    }
    public final void setSupVlan(boolean isSupVlan) {
        this.isSupVlan = isSupVlan;
    }
    public final boolean isSupGvrp() {
        return isSupGvrp;
    }
    public final void setSupGvrp(boolean isSupGvrp) {
        this.isSupGvrp = isSupGvrp;
    }
    public final boolean isSupPoe() {
        return isSupPoe;
    }
    public final void setSupPoe(boolean isSupPoe) {
        this.isSupPoe = isSupPoe;
    }
    public boolean isSupTrap() {
		return isSupTrap;
	}
	public void setSupTrap(boolean isSupTrap) {
		this.isSupTrap = isSupTrap;
	}
	public final boolean isSupLldp() {
        return isSupLldp;
    }
    public final void setSupLldp(boolean isSupLldp) {
        this.isSupLldp = isSupLldp;
    }
    public final boolean isSupRStp() {
        return isSupRStp;
    }
    public final void setSupRStp(boolean isSupRStp) {
        this.isSupRStp = isSupRStp;
    }
    public final boolean isSupMStp() {
        return isSupMStp;
    }
    public final void setSupMStp(boolean isSupMStp) {
        this.isSupMStp = isSupMStp;
    }
    public boolean isSupEdgeCoreResource() {
		return isSupEdgeCoreResource;
	}
	public void setSupEdgeCoreResource(boolean isSupEdgeCoreResource) {
		this.isSupEdgeCoreResource = isSupEdgeCoreResource;
	}
	public final boolean isSupOctet64() {
        return isSupOctet64;
    }
    public final void setSupOctet64(boolean isSupOctet64) {
        this.isSupOctet64 = isSupOctet64;
    }
	public boolean isSupEgcoTrap() {
		return isSupEgcoTrap;
	}
	public void setSupEgcoTrap(boolean isSupEgcoTrap) {
		this.isSupEgcoTrap = isSupEgcoTrap;
	}
    
}
