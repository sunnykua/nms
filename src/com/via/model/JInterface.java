package com.via.model;

import java.io.Serializable;

public class JInterface implements Serializable {
    private int portId;
	private int infIndex;
	private int ifIndex;
	private String ifName;
	private String ifDescr;
	private String ifAlias;
	private String ifType;
	private String ifSpeed;
	private String ifPhysAddress;
	private String aliasName;
	private String portType;
	private String portRemoteDev;
	private String portRemoteIp;
	private int stackId;
	
	private boolean isPoePort;
	private String jackType;
	private String maxSpeed;
	private String lldpRemoteId;
	private String lldpRemoteIdType;     // indicate the type of remoteId read from LLDP, generally is macAddr
	private String lldpRemoteIp;
	private String manualRemoteId;
	private String manualRemoteIp;
	private boolean isEtherPort;
	private boolean isManual;
	private boolean isMonitored;
	private String[] vlanList;     //All switch vlan set, not now use.
	private String pvid;           //now use vlan set.
	// === POE Schedule ===
	private boolean isPoePower;
	private String poeStartTime;
	private String poeStartStatus;
	private String poeEndTime;
	private String poeEndStatus;
	private String poeCurrentStatus;
	private String poeScheduleName;
	// === Mail Filter ===
	private boolean isMailFilter;
	private String profileStartTime;
	private String profileEndTime;
	private String profileName;

	
	public final int getPortId() {
        return portId;
    }

    public final void setPortId(int portId) {
        this.portId = portId;
    }

    public final int getInfIndex() {
        return infIndex;
    }

    public final void setInfIndex(int infIndex) {
        this.infIndex = infIndex;
    }

    /**
	 * @return the ifIndex
	 */
	public int getIfIndex() {
		return ifIndex;
	}

	/**
	 * @param ifIndex the ifIndex to set
	 */
	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	/**
	 * @return the ifDescr
	 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @param ifDescr the ifDescr to set
	 */
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	/**
	 * @return the ifAlias
	 */
	public String getIfAlias() {
		return ifAlias;
	}

	/**
	 * @param ifAlias the ifAlias to set
	 */
	public void setIfAlias(String ifAlias) {
		this.ifAlias = ifAlias;
	}
	
	/**
	 * @return the ifName
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * @param ifName the ifName to set
	 */
	public void setIfName(String ifName) {
		this.ifName = ifName;
	}
	
	/**
	 * @return the ifType
	 */
	public String getIfType() {
		return ifType;
	}

	/**
	 * @param ifType the ifType to set
	 */
	public void setIfType(String ifType) {
		this.ifType = ifType;
	}

	/**
	 * @return the ifSpeed
	 */
	public String getIfSpeed() {
		return ifSpeed;
	}

	/**
	 * @param ifSpeed the ifSpeed to set
	 */
	public void setIfSpeed(String ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	/**
	 * @return the ifPhysAddress
	 */
	public String getIfPhysAddress() {
		return ifPhysAddress;
	}

	/**
	 * @param ifPhysAddress the ifPhysAddress to set
	 */
	public void setIfPhysAddress(String ifPhysAddress) {
		this.ifPhysAddress = ifPhysAddress;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
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

	/**
	 * @return the stack
	 */
	public int getStackId() {
		return stackId;
	}

	/**
	 * @param stack the stack to set
	 */
	public void setStackId(int stackId) {
		this.stackId = stackId;
	}

	/**
	 * @return the isPoePort
	 */
	public boolean isPoePort() {
		return isPoePort;
	}

	/**
	 * @param isPoePort the isPoePort to set
	 */
	public void setPoePort(boolean isPoePort) {
		this.isPoePort = isPoePort;
	}

	public String getJackType() {
		return jackType;
	}

    public void setJackType(String jackType) {
		this.jackType = jackType;
	}

    public final String getMaxSpeed() {
        return maxSpeed;
    }

    public final void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public final String getLldpRemoteId() {
        return lldpRemoteId;
    }

    public final void setLldpRemoteId(String lldpRemoteId) {
        this.lldpRemoteId = lldpRemoteId;
    }

    public final String getLldpRemoteIdType() {
        return lldpRemoteIdType;
    }

    public final void setLldpRemoteIdType(String lldpRemoteIdType) {
        this.lldpRemoteIdType = lldpRemoteIdType;
    }

    public final String getLldpRemoteIp() {
        return lldpRemoteIp;
    }

    public final void setLldpRemoteIp(String lldpRemoteIp) {
        this.lldpRemoteIp = lldpRemoteIp;
    }

    public final String getManualRemoteId() {
        return manualRemoteId;
    }

    public final void setManualRemoteId(String manualRemoteId) {
        this.manualRemoteId = manualRemoteId;
    }

    public final String getManualRemoteIp() {
        return manualRemoteIp;
    }

    public final void setManualRemoteIp(String manualRemoteIp) {
        this.manualRemoteIp = manualRemoteIp;
    }

    public final boolean isEtherPort() {
        return isEtherPort;
    }

    public final void setEtherPort(boolean isEtherPort) {
        this.isEtherPort = isEtherPort;
    }

    public final boolean isManual() {
        return isManual;
    }

    public final void setManual(boolean isManual) {
        this.isManual = isManual;
    }

    public final boolean isMonitored() {
        return isMonitored;
    }

    public final void setMonitored(boolean isMonitored) {
        this.isMonitored = isMonitored;
    }

	public String[] getVlanList() {
		return vlanList;
	}

	public void setVlanList(String[] vlanList) {
		this.vlanList = vlanList;
	}

	public String getPvid() {
		return pvid;
	}

	public void setPvid(String pvid) {
		this.pvid = pvid;
	}

	public boolean isPoePower() {
		return isPoePower;
	}

	public void setPoePower(boolean isPoePower) {
		this.isPoePower = isPoePower;
	}
	
	public String getPoeStartTime() {
		return poeStartTime;
	}

	public void setPoeStartTime(String poeStartTime) {
		this.poeStartTime = poeStartTime;
	}

	public String getPoeStartStatus() {
		return poeStartStatus;
	}

	public void setPoeStartStatus(String poeStartStatus) {
		this.poeStartStatus = poeStartStatus;
	}

	public String getPoeEndTime() {
		return poeEndTime;
	}

	public void setPoeEndTime(String poeEndTime) {
		this.poeEndTime = poeEndTime;
	}

	public String getPoeEndStatus() {
		return poeEndStatus;
	}

	public void setPoeEndStatus(String poeEndStatus) {
		this.poeEndStatus = poeEndStatus;
	}

	public String getPoeCurrentStatus() {
		return poeCurrentStatus;
	}

	public void setPoeCurrentStatus(String poeCurrentStatus) {
		this.poeCurrentStatus = poeCurrentStatus;
	}

	public String getPoeScheduleName() {
		return poeScheduleName;
	}

	public void setPoeScheduleName(String poeScheduleName) {
		this.poeScheduleName = poeScheduleName;
	}

	public boolean isMailFilter() {
		return isMailFilter;
	}

	public void setMailFilter(boolean isMailFilter) {
		this.isMailFilter = isMailFilter;
	}

	public String getProfileStartTime() {
		return profileStartTime;
	}

	public void setProfileStartTime(String profileStartTime) {
		this.profileStartTime = profileStartTime;
	}

	public String getProfileEndTime() {
		return profileEndTime;
	}

	public void setProfileEndTime(String profileEndTime) {
		this.profileEndTime = profileEndTime;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}
