package com.via.model;

/*import java.util.ArrayList;
import java.util.List;*/

public class JOid {
	private static final String publicOid = "1.3.6.1.2.1";
	private static final String ieee802dot1mibs = "1.0.8802.1.1";

	private static final String nodeSystem = publicOid + ".1";						// system

	private static final String nodeInterfaces = publicOid + ".2";					// interfaces
	private static final String nodeIfTable = nodeInterfaces + ".2";					// ifTable
	
	private static final String nodeIp = publicOid + ".4";							// ip
	private static final String nodeIpAddrTable = nodeIp + ".20";						// ipAddrTable

	private static final String nodeTransmission = publicOid + ".10";					// transmission
	private static final String nodeDot3StatsTable = nodeTransmission + ".7.2";		// dot3/dot3StatsTable
	private static final String nodeDot3PauseTable = nodeTransmission + ".7.10";		// dot3/Dot3PauseTable

	private static final String nodeRmon = publicOid + ".16";							// RMON
	private static final String nodeEtherStatsTable = nodeRmon + ".1.1";				// EtherStatsTable

	private static final String nodeDot1dBridge = publicOid + ".17";					// dot1dBridge
	private static final String nodeDot1dStpPortTable = nodeDot1dBridge + ".2.15";	// dot1dStp/dot1dStpPortTable
	private static final String nodeqBridgeMIBObjects = nodeDot1dBridge + ".7.1";		// qBridgeMIB/qBridgeMIBObjects
	private static final String nodeDot1qVlan = nodeqBridgeMIBObjects + ".4";			// dot1qVlan

	private static final String nodeDot3MauMgt = publicOid + ".26";					// snmpDot3MauMgt
	private static final String nodeIfMauTable = nodeDot3MauMgt + ".2.1";				// dot3IfMauBasicGroup/ifMauTable
	private static final String nodeIfJackTable = nodeDot3MauMgt + ".2.2";			// dot3IfMauBasicGroup/ifJackTable
	private static final String nodeIfMauAutoNegoTable = nodeDot3MauMgt + ".5.1";		// dot3IfMauBasicGroup/ifMauAutoNegoTable

	private static final String nodeifMIB = publicOid + ".31";						// ifMIB
	private static final String nodeIfXTable = nodeifMIB + ".1.1";					// ifMIBObjects/ifXTable

	private static final String nodePoEMainTable = publicOid + ".105.1.3.1.1";		// PoE Main
	private static final String nodePoEPortTable = publicOid + ".105.1.1.1";			// PoE Port

	private static final String nodeLldpMIB = ieee802dot1mibs + ".2";					// lldpMIB
	private static final String nodeLldpLocalSystemData = nodeLldpMIB + ".1.3";			// lldpLocalSystemData
	private static final String nodeLldpRemoteSystemData = nodeLldpMIB + ".1.4";		// lldpRemoteSystemsData
	private static final String nodeLldpLocPortTable = nodeLldpLocalSystemData + ".7";	// lldpLocPortTable
	private static final String nodeLldpRemTable = nodeLldpRemoteSystemData + ".1";		// lldpRemTable

	private static final String privateOid = "1.3.6.1.4.1";                            // private/enterprises
	
	private static final String viaVNT5801 = privateOid + ".3742.10.5801.1";           // via/management/project/model
	private static final String viaVNT5103 = privateOid + ".3742.10.5103.11";
	private static final String vnt5801ApListTable = viaVNT5801 + ".5.1";              // manageInfo/AplistTable
	private static final String vnt5801SsidListTable = viaVNT5801 + ".5.2";            // manageInfo/ApSsidlistTable
	
	/***** system *****/
	public static final String sysDescr = nodeSystem + ".1.0";
	public static final String sysObjectID = nodeSystem + ".2.0";
	public static final String sysUpTime = nodeSystem + ".3.0";
	public static final String sysContact = nodeSystem + ".4.0";
	public static final String sysName = nodeSystem + ".5.0";
	public static final String sysLocation = nodeSystem + ".6.0";
	public static final String sysServices = nodeSystem + ".7.0";

	/***** ifTable *****/
	public static final String ifIndex = nodeIfTable + ".1.1";
	public static final String ifDescr = nodeIfTable + ".1.2";
	public static final String ifType = nodeIfTable + ".1.3";
	public static final String ifMtu = nodeIfTable + ".1.4";
	public static final String ifSpeed = nodeIfTable + ".1.5";
	public static final String ifPhysAddress = nodeIfTable + ".1.6";
	public static final String ifAdminStatus = nodeIfTable + ".1.7";
	public static final String ifOperStatus = nodeIfTable + ".1.8";
	public static final String ifLastChange = nodeIfTable + ".1.9";
	public static final String ifInOctets = nodeIfTable + ".1.10";
	public static final String ifInUcastPkts = nodeIfTable + ".1.11";
	public static final String ifInNUcastPkts = nodeIfTable + ".1.12";				// deprecated
	public static final String ifOutOctets = nodeIfTable + ".1.16";
	public static final String ifOutUcastPkts = nodeIfTable + ".1.17";
	public static final String ifOutNUcastPkts = nodeIfTable + ".1.18";				// deprecated
	public static final String ifInDiscards = nodeIfTable + ".1.13";
	public static final String ifInErrors = nodeIfTable + ".1.14";
	public static final String ifOutDiscards = nodeIfTable + ".1.19";
	public static final String ifOutErrors = nodeIfTable + ".1.20";
	public static final String ifInUnknowns = nodeIfTable + ".1.15";
	
	/***** ipAddrTable *****/
	public static final String ipAdEntAddr = nodeIpAddrTable + ".1.1";
	public static final String ipAdEntIfIndex = nodeIpAddrTable + ".1.2";

	/***** dot3StatsTable *****/
	public static final String dot3StatsAlignmentErrors = nodeDot3StatsTable + ".1.2";
	public static final String dot3StatsFCSErrors = nodeDot3StatsTable + ".1.3";
	public static final String dot3StatsSingleCollisionFrames = nodeDot3StatsTable + ".1.4";
	public static final String dot3StatsMultipleCollisionFrames = nodeDot3StatsTable + ".1.5";
	public static final String dot3StatsSQETestErrors = nodeDot3StatsTable + ".1.6";
	public static final String dot3StatsDeferredTransmissions = nodeDot3StatsTable + ".1.7";
	public static final String dot3StatsLateCollisions = nodeDot3StatsTable + ".1.8";
	public static final String dot3StatsExcessiveCollisions = nodeDot3StatsTable + ".1.9";
	public static final String dot3StatsInternalMacTransmitErrors = nodeDot3StatsTable + ".1.10";
	public static final String dot3StatsCarrierSenseErrors = nodeDot3StatsTable + ".1.11";
	public static final String dot3StatsFrameTooLongs = nodeDot3StatsTable + ".1.13";
	public static final String dot3StatsInternalMacReceiveErrors = nodeDot3StatsTable + ".1.16";
	public static final String dot3StatsSymbolErrors = nodeDot3StatsTable + ".1.18";
	public static final String dot3StatsDuplexStatus = nodeDot3StatsTable + ".1.19";
	public static final String dot3StatsRateControlAbility = nodeDot3StatsTable + ".1.20";
	public static final String dot3StatsRateControlStatus = nodeDot3StatsTable + ".1.21";

	/***** dot3PauseTable *****/
	public static final String dot3InPauseFrames = nodeDot3PauseTable + ".1.3";
	public static final String dot3OutPauseFrames = nodeDot3PauseTable + ".1.4";

	/***** dot1dStp, dot1dStpPortTable *****/
	public static final String dot1dStpPriority = nodeDot1dBridge + ".2.2.0";
	public static final String dot1dStpDesignatedRoot = nodeDot1dBridge + ".2.5.0";
	public static final String dot1dStpRootCost = nodeDot1dBridge + ".2.6.0";
	public static final String dot1dStpRootPort = nodeDot1dBridge + ".2.7.0";
	public static final String dot1dStpPortEnable = nodeDot1dStpPortTable + ".1.4";

	/***** ifMauTable *****/
	public static final String ifMauType = nodeIfMauTable + ".1.3";
	public static final String ifMauDefaultType = nodeIfMauTable + ".1.11";
	public static final String ifMauAutoNegSupported = nodeIfMauTable + ".1.12";
	
	/***** ifJackTable *****/
	public static final String ifJackType = nodeIfJackTable + ".1.2";

	/***** ifMauAutoNegoTable *****/
	public static final String ifMauAutoNegoAdminStatus = nodeIfMauAutoNegoTable + ".1.1";
	public static final String ifMauAutoNegoRestart = nodeIfMauAutoNegoTable + ".1.8";
	public static final String ifMauAutoNegoCapabilityBits = nodeIfMauAutoNegoTable + ".1.9";
	public static final String ifMauAutoNegoCapAdvertisedBits = nodeIfMauAutoNegoTable + ".1.10";

	/***** ifXTable *****/
	public static final String ifName = nodeIfXTable + ".1.1";
	public static final String ifInMulticastPkts = nodeIfXTable + ".1.2";
	public static final String ifInBroadcastPkts = nodeIfXTable + ".1.3";
	public static final String ifOutMulticastPkts = nodeIfXTable + ".1.4";
	public static final String ifOutBroadcastPkts = nodeIfXTable + ".1.5";
	public static final String ifHCInOctets = nodeIfXTable + ".1.6";
	public static final String ifHCInUcastPkts = nodeIfXTable + ".1.7";
	public static final String ifHCInMulticastPkts = nodeIfXTable + ".1.8";
	public static final String ifHCInBroadcastPkts = nodeIfXTable + ".1.9";
	public static final String ifHCOutOctets = nodeIfXTable + ".1.10";
	public static final String ifHCOutUcastPkts = nodeIfXTable + ".1.11";
	public static final String ifHCOutMulticastPkts = nodeIfXTable + ".1.12";
	public static final String ifHCOutBroadcastPkts = nodeIfXTable + ".1.13";
	public static final String ifLinkUpDownTrapEnable = nodeIfXTable + ".1.14";        // TODO: for future use
	public static final String ifHighSpeed = nodeIfXTable + ".1.15";
	public static final String ifAlias = nodeIfXTable + ".1.18";

	/***** dot1qBridge *****/
	public static final String dot1qPvid = nodeDot1qVlan + ".5.1.1";
	public static final String dot1qPortAcceptableFrameType = nodeDot1qVlan + ".5.1.2";
	public static final String dot1qPortIngressFiltering = nodeDot1qVlan + ".5.1.3";
	public static final String dot1qPortGvrpStatus = nodeDot1qVlan + ".5.1.4";
	public static final String dot1qVlanStaticName = nodeDot1qVlan + ".3.1.1";
	public static final String dot1qVlanStaticStatus = nodeDot1qVlan + ".3.1.5";

	/***** PoE *****/
	public static final String PoEMainMaxAvailablePower = nodePoEMainTable + ".2";
	public static final String PoEMainSysOperStatus = nodePoEMainTable + ".3";
	public static final String PoEMainPowerConsumption = nodePoEMainTable + ".4";
	public static final String PoEPortAdminStatus = nodePoEPortTable + ".3";
	public static final String PoEPortDetectionStatus = nodePoEPortTable + ".6";
	public static final String PoEPortPowerPriority = nodePoEPortTable + ".7";

	/***** EtherStatsTable *****/
	public static final String EtherStatsDropEvents = nodeEtherStatsTable + ".1.3";	 
	public static final String EtherStatsOctets = nodeEtherStatsTable + ".1.4";
	public static final String EtherStatsPkts = nodeEtherStatsTable + ".1.5";
	public static final String EtherStatsBroadcastPkts = nodeEtherStatsTable + ".1.6";
	public static final String EtherStatsMulticastPkts = nodeEtherStatsTable + ".1.7";
	public static final String EtherStatsCRCAlignErrors = nodeEtherStatsTable + ".1.8";
	public static final String EtherStatsUndersizePkts = nodeEtherStatsTable + ".1.9";
	public static final String EtherStatsOversizePkts = nodeEtherStatsTable + ".1.10";
	public static final String EtherStatsFragments = nodeEtherStatsTable + ".1.11";
	public static final String EtherStatsJabbers = nodeEtherStatsTable + ".1.12";
	public static final String EtherStatsCollisions = nodeEtherStatsTable + ".1.13";
	public static final String EtherStatsPkts64Octets = nodeEtherStatsTable + ".1.14";
	public static final String EtherStatsPkts65to127Octets = nodeEtherStatsTable + ".1.15";
	public static final String EtherStatsPkts128to255Octets = nodeEtherStatsTable + ".1.16";
	public static final String EtherStatsPkts256to511Octets = nodeEtherStatsTable + ".1.17";
	public static final String EtherStatsPkts512to1023Octets = nodeEtherStatsTable + ".1.18";
	public static final String EtherStatsPkts1024to1518Octets = nodeEtherStatsTable + ".1.19";

	/***** lldpLocalSystemData / lldpLocPortTable *****/
	public static final String lldpLocChassisId = nodeLldpLocalSystemData + ".2.0";
	public static final String lldpLocPortIdSubtype = nodeLldpLocPortTable + ".1.2";
	public static final String lldpLocPortId = nodeLldpLocPortTable + ".1.3";
	public static final String lldpLocPortDesc = nodeLldpLocPortTable + ".1.4";
	
	/***** lldpRemTable *****/
	public static final String lldpRemChassisIdSubtype = nodeLldpRemTable + ".1.4";
	public static final String lldpRemChassisId = nodeLldpRemTable + ".1.5";
	
	/***** dot1qVlanCurrentTable *****/
	private static final String nodeDot1qVlanCurrentTable = nodeqBridgeMIBObjects + ".4.2";			// dot1qVlan
	public static final String dot1qVlanCurrentFdbId = nodeDot1qVlanCurrentTable + ".1.3";
	public static final String dot1qVlanCurrentEgressPorts = nodeDot1qVlanCurrentTable + ".1.4";
	public static final String dot1qVlanCurrentUntaggedPorts = nodeDot1qVlanCurrentTable + ".1.5";
	public static final String dot1qVlanCurrentStatus = nodeDot1qVlanCurrentTable + ".1.6";
	private static final String nodeDot1qVlanStaticTable = nodeqBridgeMIBObjects + ".4.3";			// dot1qVlan
	public static final String dot1qVlanStaticEgressPorts = nodeDot1qVlanStaticTable + ".1.2";
	public static final String dot1qVlanStaticForbiddenEgressPorts = nodeDot1qVlanStaticTable + ".1.3";
	public static final String dot1qVlanStaticUntaggedPorts = nodeDot1qVlanStaticTable + ".1.4";

	/***** HostTable *****/
	private static final String nodeHost = publicOid + ".25";
	public static final String nodehrStorageTable = nodeHost + ".2.3";
	public static final String nodehrDeviceTable = nodeHost + ".3.2";
	public static final String nodehrProcessorTable = nodeHost + ".3.3";
	public static final String hrStorageType = nodehrStorageTable + ".1.2";
	public static final String hrStorageDescr = nodehrStorageTable + ".1.3";
	public static final String hrStorageAllocationUnits = nodehrStorageTable + ".1.4";
	public static final String hrStorageSize = nodehrStorageTable + ".1.5";
	public static final String hrStorageUsed = nodehrStorageTable + ".1.6";
	public static final String hrProcessorLoad = nodehrProcessorTable + ".1.2";
	
	/***** private: VIA VNT5103 (EdgeCore) TrapDestMgt *****/
	public static final String vnt5103TrapDestCommunity = viaVNT5103 + ".1.14.1.1.2";
	public static final String vnt5103TrapDestStatus = viaVNT5103 + ".1.14.1.1.3";
	public static final String vnt5103TrapDestVersion = viaVNT5103 + ".1.14.1.1.4";
	public static final String vnt5103TrapDestUdpPort = viaVNT5103 + ".1.14.1.1.5";
	
	/***** private: VIA VNT5103 (EdgeCore) Host Resource *****/
	public static final String vnt5103CpuCurrent  = viaVNT5103 + ".1.39.2.1.0";
	public static final String vnt5103MemoryTotal = viaVNT5103 + ".1.39.3.1.0";
	public static final String vnt5103MemoryUsed  = viaVNT5103 + ".1.39.3.2.0";
	public static final String vnt5103MemoryFreed = viaVNT5103 + ".1.39.3.3.0";

	/***** private: VIA VNT5801 AP List Table *****/
	public static final String vnt5801ApListApMacAddress = vnt5801ApListTable + ".1.1";
	public static final String vnt5801ApListApIpAddress = vnt5801ApListTable + ".1.2";
	public static final String vnt5801ApListApName = vnt5801ApListTable + ".1.3";
	public static final String vnt5801ApListApTxOctet = vnt5801ApListTable + ".1.4";
	public static final String vnt5801ApListApRxOctet = vnt5801ApListTable + ".1.5";

	/***** private: VIA VNT5801 SSID List Table *****/
	public static final String vnt5801SsidListApMacAddress = vnt5801SsidListTable + ".1.1";
	public static final String vnt5801SsidListApIpAddress = vnt5801SsidListTable + ".1.2";
	public static final String vnt5801SsidListApName = vnt5801SsidListTable + ".1.3";
	public static final String vnt5801SsidListSsidIndex = vnt5801SsidListTable + ".1.4";
	public static final String vnt5801SsidListSsidName = vnt5801SsidListTable + ".1.5";
	public static final String vnt5801SsidListBssid = vnt5801SsidListTable + ".1.6";
	public static final String vnt5801SsidListRadioType = vnt5801SsidListTable + ".1.7";
	public static final String vnt5801SsidListTxOctet = vnt5801SsidListTable + ".1.8";
	public static final String vnt5801SsidListRxOctet = vnt5801SsidListTable + ".1.9";
	
	/***** Some Methods*****/
	/*public static List<String> createList(final String baseOid, final int fromIndex, final int toIndex) {
    	List<String> listOid = new ArrayList<String>();

    	for (int i = fromIndex; i <= toIndex; i++) {
    		String oid = baseOid + "." + i;
    		listOid.add(oid);
    	}

    	return listOid;
    }*/
	public static String[] createList(final String baseOid, final int fromIndex, final int toIndex) {
		int count = toIndex - fromIndex + 1;
		String[] oidList = new String[count];

    	for (int i = 0; i < count; i++) {
    		oidList[i] = baseOid + "." + (i + fromIndex);
    	}

    	return oidList;
    }
}
