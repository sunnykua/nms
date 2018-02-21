package com.via.topology;

import java.util.List;

import com.via.model.JInterface;

public class JNode {
	private String publicIp;
	private String phyAddr;
	private int snmpSupport;
	private String sysObjectId;
	private String aliasName;
	private String deviceType;
	private int rj45Num;
	private int stackNum;
	private int parentStackNum = 0;
	private String parentDeviceType;

	private int stpPriority;
	private int stpRootCost;
	private int stpRootPort;

	private String remoteIdType;
	private String[] parent;               // {'Local Port', 'Remote ID(MAC)'}
	private int parentPort = 0;
	private String parentId;
	private String parentIp;
	private int portToParent = 0;
	private int parentPortIfIndex = 0;
	private int portToParentIfIndex = 0;
	private int parentPortStack = 0;
	private int portToParentStack = 0;
	private List<String[]> children;       // the same
	private List<JInterface> interfaces;

	private boolean isRoot;
	private boolean hasNext;
	private boolean hasChild;
	private int isAlive;
	private boolean isVirtual;
	
	private String nodeHTML;
	private String groupName;
	

	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public String getPhyAddr() {
		return phyAddr;
	}
	public void setPhyAddr(String phyAddr) {
		this.phyAddr = phyAddr;
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
	public final String getAliasName() {
        return aliasName;
    }
    public final void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public final String getDeviceType() {
        return deviceType;
    }
    public final void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public final int getStpPriority() {
        return stpPriority;
    }
    public final void setStpPriority(int stpPriority) {
        this.stpPriority = stpPriority;
    }
    public final int getStpRootCost() {
        return stpRootCost;
    }
    public final void setStpRootCost(int stpRootCost) {
        this.stpRootCost = stpRootCost;
    }
    public final int getStpRootPort() {
        return stpRootPort;
    }
    public final void setStpRootPort(int stpRootPort) {
        this.stpRootPort = stpRootPort;
    }
	public String getRemoteIdType() {
		return remoteIdType;
	}
	public void setRemoteIdType(String remoteIdType) {
		this.remoteIdType = remoteIdType;
	}
	public String[] getParent() {
		return parent;
	}
	public void setParent(String[] parent) {
		this.parent = parent;
	}
	public final int getParentPort() {
        return parentPort;
    }
    public final void setParentPort(int parentPort) {
        this.parentPort = parentPort;
    }
    public final String getParentId() {
        return parentId;
    }
    public final void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public final String getParentIp() {
        return parentIp;
    }
    public final void setParentIp(String parentIp) {
        this.parentIp = parentIp;
    }
    public final int getPortToParent() {
        return portToParent;
    }
    public final void setPortToParent(int portToParent) {
        this.portToParent = portToParent;
    }
	public int getParentPortIfIndex() {
		return parentPortIfIndex;
	}
	public void setParentPortIfIndex(int parentPortIfIndex) {
		this.parentPortIfIndex = parentPortIfIndex;
	}
	public int getPortToParentIfIndex() {
		return portToParentIfIndex;
	}
	public void setPortToParentIfIndex(int portToParentIfIndex) {
		this.portToParentIfIndex = portToParentIfIndex;
	}
	public int getParentPortStack() {
		return parentPortStack;
	}
	public void setParentPortStack(int parentPortStack) {
		this.parentPortStack = parentPortStack;
	}
	public int getPortToParentStack() {
		return portToParentStack;
	}
	public void setPortToParentStack(int portToParentStack) {
		this.portToParentStack = portToParentStack;
	}
	public List<String[]> getChildren() {
		return children;
	}
	public void setChildren(List<String[]> children) {
		this.children = children;
	}
	public final List<JInterface> getInterfaces() {
        return interfaces;
    }
    public final void setInterfaces(List<JInterface> interfaces) {
        this.interfaces = interfaces;
    }
    public boolean isRoot() {
		return isRoot;
	}
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	public boolean isHasChild() {
		return hasChild;
	}
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}
    public int isAlive() {
		return isAlive;
	}
	public void setAlive(int isAlive) {
		this.isAlive = isAlive;
	}
	public final boolean isVirtual() {
        return isVirtual;
    }
    public final void setVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }
    public String getNodeHTML() {
		return nodeHTML;
	}
	public void setNodeHTML(String nodeHTML) {
		this.nodeHTML = nodeHTML;
	}
	public int getRj45Num() {
		return rj45Num;
	}
	public void setRj45Num(int rj45Num) {
		this.rj45Num = rj45Num;
	}
	public int getStackNum() {
		return stackNum;
	}
	public void setStackNum(int stackNum) {
		this.stackNum = stackNum;
	}
	public int getParentStackNum() {
		return parentStackNum;
	}
	public void setParentStackNum(int parentStackNum) {
		this.parentStackNum = parentStackNum;
	}
	public String getParentDeviceType() {
		return parentDeviceType;
	}
	public void setParentDeviceType(String parentDeviceType) {
		this.parentDeviceType = parentDeviceType;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
